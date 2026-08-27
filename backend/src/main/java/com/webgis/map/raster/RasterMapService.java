package com.webgis.map.raster;


import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.webgis.map.service.TransformTifFiles;
import com.webgis.map.tile.TileService;

import java.util.List;
import java.util.Optional;

@Service
public class RasterMapService {

    static Logger logger = LoggerFactory.getLogger(RasterMapService.class);

    private final RasterMapRepository rasterMapRepository;
    private final TileService tileService;
    private final TransformTifFiles transformTifFiles;


    public RasterMapService(
            RasterMapRepository rasterMapRepository,
            TileService tileService,
            TransformTifFiles transformTifFiles){
        this.rasterMapRepository = rasterMapRepository;
        this.tileService = tileService;
        this.transformTifFiles = transformTifFiles;
    }


    /**
     * Search for a risk factor in db using its identifier
     *
     * @param id identifier of the risk factor you want to retrieve from the db
     * @return risk factor which identifier equals to id, empty otherwise
     */
    public Optional<RasterMap> findById(long id){
        return rasterMapRepository.findById(id);
    }

    /**
     * Save the geoJSON file and the files used to create it for a risk factor identified by its id
     *
     * @param riskFactorMap : the risk factor you want to add geoJsonFile from the db
     * @return Saved map
     *
     */
    public RasterMap save(RasterMap riskFactorMap){
        return rasterMapRepository.save(riskFactorMap);
    }


    /**
     * Gets all risk factor map instances
     *
     * @return list of all the risk factor maps contained in database
     * */
    public List<RasterMap> findAll(){
        return this.rasterMapRepository.findAll();
    }
    
    /**
     * Updates a raster map's title/description and fully regenerates its tiles
     * from a new tif file — all existing tiles for this raster map are deleted
     * and replaced, as if the raster map had been re-added from scratch.
     *
     * @param id identifier of the raster map to update
     * @param title new title
     * @param description new description
     * @param tifFile the new tif file to regenerate tiles from
     * @return the updated RasterMap
     */
    @Transactional
    public RasterMap update(long id, String title, String description, MultipartFile tifFile){
        final Optional<RasterMap> rasterMapOptional = rasterMapRepository.findById(id);

        if (rasterMapOptional.isEmpty()) {
            throw new IllegalArgumentException("RasterMap does not exist: " + id);
        }

        final RasterMap rasterMap = rasterMapOptional.get();
        rasterMap.setTitle(title);
        rasterMap.setDescription(description);

        final RasterMap updatedRasterMap = rasterMapRepository.save(rasterMap);

        tileService.deleteAllTilesForRasterMap(updatedRasterMap);
        transformTifFiles.transformIntoTileFile(updatedRasterMap.getId(), tifFile);

        return updatedRasterMap;

    }

    /**
     * Deletes a raster map and all of its associated tiles.
     *
     * @param id identifier of the raster map to delete
     */
    @Transactional
    public void deleteRasterMap(long id){
        final Optional<RasterMap> rasterMapOptional = rasterMapRepository.getById(id);

        if (rasterMapOptional.isEmpty()){
            throw new IllegalArgumentException("RasterMap does not exist: " + id);
        }

        final RasterMap rasterMap = rasterMapOptional.get();
        tileService.deleteAllTilesForRasterMap(rasterMap);
        rasterMapRepository.delete(rasterMap);
    }

}
