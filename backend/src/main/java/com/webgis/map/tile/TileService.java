package com.webgis.map.tile;

import com.webgis.exception.CanDecompress;
import com.webgis.map.raster.RasterMap;
import com.webgis.map.raster.RasterMapRepository;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.awt.Graphics;

import static com.webgis.map.tile.TileConstants.getTileSize;
import static com.webgis.map.tile.TileConstants.getBlockSize;


@Service
public class TileService {
    private final TileRepository tileRepository;
    private final RasterMapRepository riskFactorMapRepository;
    static Logger logger = LoggerFactory.getLogger(TileService.class);
    final int tileSize = getTileSize();
    final int blockSize = getBlockSize();
    private static final float MAX_PIXEL_VALUE = 255f;

    public TileService (TileRepository tileRepository, RasterMapRepository riskFactorMapRepository){
        this.tileRepository = tileRepository;
        this.riskFactorMapRepository = riskFactorMapRepository;
    }

    /**
     * saves given tile in the database
     *
     * @param mapId : id of the risk factor map which this tile belongs to
     * @param zoom : zoom level of the tile
     * @param x : x level of the tile
     * @param y : y level of the tile
     * @param data : byte array containing the tile data
     *
     * @return : tile
     * */
    public Tile save(long mapId, int zoom, int x, int y, byte[] data){
        final RasterMap riskFactorMap = riskFactorMapRepository.findById(mapId)
                .orElseThrow(() -> new RuntimeException("Map not found: " + mapId));

        final TileId tileId = new TileId(mapId, zoom, x, y);
        final Tile tileToStore = new Tile(tileId, data, riskFactorMap);

        return tileRepository.save(tileToStore);
    }

    public Optional<Tile> findById(TileId tileId) {
        return tileRepository.findById(tileId);
    }


    /** returns the index of mean in 1D array of size constant blockSize
     *
     * @param x : int representing the x coordinates of mean BLOCK (0-blockSize)
     * @param y : int representing the y coordinate of mean BLOCK (0-blockSize)
     * @return  —  index for mean of mean in 1D array of size constant blockSize
     *          —  -1 if the specification is not respected
     * */
    public int getMeanIndex(int x, int y){
        if (x < 0 || y < 0 || x > blockSize || y > blockSize){
            return -1;
        }
        return y*blockSize + x;
    }


    /**returns coordinates of tile in OpenStreetMap 2D space
     *
     * @param z : current zoom level on the map
     * @param lat : latitude
     * @param lng : longitude
     * @return tile coordinates in OpenStreetMap 2D space in format array : [x,y]
     * */
    public int[] getTileCoordinates(int z, double lat, double lng){
        final double[] nAndLatRad = getNAndLatRad(z, lat);
        final double[] xy = getTileXY(nAndLatRad[0], lng, nAndLatRad[1]);

        return new int[]{
                (int)Math.floor(xy[0]),
                (int)Math.floor(xy[1])
        };
    }


    /**returns coordinates of tile in OpenStreetMap 2D space
     *
     * @param z : current zoom level on the map
     * @param lat : latitude
     * @param lng : longitude
     * @return tile coordinates in OpenStreetMap 2D space in format array : [x,y]
     * */
    public int[] getBlockCoordinates(int z, double lat, double lng){
        final double[] nAndLatRad = getNAndLatRad(z, lat);
        final double[] xy = getTileXY(nAndLatRad[0], lng, nAndLatRad[1]);

        return new int[]{
                (int)Math.floor((xy[0]*blockSize)%blockSize),
                (int)Math.floor((xy[1]*blockSize)%blockSize)
        };
    }


    /**
     * return 2^zoom and the latitude in radian into an array of size 2
     *
     * @param z : current zoom level on the map
     * @param latitude : latitude
     * @return an array of size 2 with index :
     *  0 : n -> 2^zoom
     *  1 : LatRad -> Latitude in Radian
     * */
    private double[] getNAndLatRad(int z,  double latitude){
        return new double[]{
                (int)Math.pow(2,z),
                (latitude * (Math.PI / 180))
        };
    }


    /**
     * Return X and Y into an array of size 2
     *
     * @param n : represent 2^zoom
     * @param lng : longitude
     * @param latRad : Latitude in Radian
     * @return an array of size 2 with index :
     *      0 : X
     *      1 : Y
     **/
    private double[] getTileXY(double n, double lng, double latRad){
        return new double[]{
                n*((lng+180)/360),
                n * ( 1- ( Math.log( Math.tan( latRad )  +  1/Math.cos(latRad)) / Math.PI))/2
        };
    }

    /**
     * compute mean of block at given coordinates in given tileData
     *
     * @param : tileData : byte array containing each pixel value
     * @param : blockCoordinates : coordinates of block in tileData in a tileSize/blockSize x TILE/SIZE/blockSize 2d space
     *          i.e. block at coordinates (15,15) is the last block of tileData for tileSize = 16 & blockSize = 16
     * @return mean of pixel values in block
     * */
    public float getTileMeanBlock(byte[] tileData, int[] blockCoordinates){
        long sum=0;
        final int count = blockSize*blockSize;

        //getting the x and y offsets for tileData (coordinates of the first pixel of the block)
        final int xOffset = blockCoordinates[0]*blockSize;
        final int yOffset = blockCoordinates[1]*blockSize;

        //for each pixel in this block
        for (int x = 0; x < blockSize; x++){
            for (int y = 0; y < blockSize; y++) {

                //0xFF to remove the sign
                sum += tileData[(yOffset+y)*tileSize + (xOffset+x)] & 0xFF;
            }
        }
        //normalising to [0..1] value
        return sum /(count*MAX_PIXEL_VALUE);
    }

    /**
     * Decompress a png file for tile into a tileSize x tileSize byte array (if used on tile)
     *
     * @param tileData : byte array with tile data
     * @return byte array of size tileSize x tileSize containing each pixel of the tile
     * @throws CanDecompress
     * */
    public byte[] decompressPNGFile(@NonNull byte[] tileData)throws CanDecompress{


        try {
            final BufferedImage img = ImageIO.read(new ByteArrayInputStream(tileData));

            if (img == null) {
                throw new CanDecompress("The PNG data are invalid and null");
            }

            final BufferedImage grayImg = new BufferedImage(
                    img.getWidth(),
                    img.getHeight(),
                    BufferedImage.TYPE_BYTE_GRAY
            );

            final Graphics g = grayImg.getGraphics();
            g.drawImage(img, 0, 0, null);
            g.dispose();
            return ((DataBufferByte) grayImg.getRaster().getDataBuffer()).getData();

        } catch (IOException e) {
            logger.error("There is a IOException during the reading of path in decompressPNGFile");
        }

        throw new CanDecompress("The PNG file could not be decompressed");
    }

    /**
     * Delete a tile based on its id
     *
     * @param tileId the id of the tile you want to delete
     */
    public void deleteTile(TileId tileId){
        final Optional<Tile> tileTodelete= findById(tileId);
        tileTodelete.ifPresent(tileRepository::delete);
    }

    /**
     * Get all the tile for a final map
     *
     * @param rasterMap the rasterMap for which you want to get the tile
     *
     * @return A list of all the tile linked to the rasterMap
     */
    public List<Tile> allTileForAspecificRasterMap(RasterMap rasterMap){
        return tileRepository.findTileByRasterMap(rasterMap);
    }

    /**
     * Delete all the tiles corresponding to a given raster map in one bulk operation.
     *
     * @param rasterMap the raster map to delete
     */
    public void deleteAllTilesForRasterMap(RasterMap rasterMap){
        tileRepository.deleteAllByRasterMapId(rasterMap.getId());
    }
}
