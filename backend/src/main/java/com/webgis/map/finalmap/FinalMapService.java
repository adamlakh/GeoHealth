package com.webgis.map.finalmap;

import com.converter.ZipFiles;
import com.webgis.evaluationform.EvaluationForm;
import com.webgis.evaluationform.EvaluationFormService;
import com.webgis.map.raster.RasterMap;
import com.webgis.map.tile.Tile;
import com.webgis.map.tile.TileService;
import jakarta.transaction.Transactional;
import org.geotools.api.referencing.FactoryException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import static com.converter.DetectFiles.findShpFile;
import static com.converter.ShapeFileToGeoJsonFile.transformShapeFileToGeoJsonFile;

@Service
public class FinalMapService {

    private final EvaluationFormService evaluationFormService;
    private final TileService tileService;
    private final FinalMapRepository finalMapRepository;
    private final ZipFiles unzipper;

    public FinalMapService(EvaluationFormService evaluationFormService,
                           TileService tileService,
                           FinalMapRepository finalMapRepository){
        this.evaluationFormService= evaluationFormService;
        this.tileService=tileService;
        this.finalMapRepository = finalMapRepository;
        this.unzipper = new ZipFiles();
    }

    /**
     * Search for a map in db using its identifier
     *
     * @param id identifier of the map you want to retrieve from the db
     * @return Map which identifier equals to id, empty otherwise
     */
    @Transactional
    public Optional<FinalMap> findById(long id){
        return finalMapRepository.findById(id);
    }

    /**
     * Returns a map from the database, find it by searching for specified name
     *
     * @param title name of the map you want to retrieve from the db
     * @return Map found using its name, empty if there's none
     * */
    public Optional<FinalMap> findByTitle(String title){return finalMapRepository.findByTitle(title);}


    /**
     * Save the geoJSON file and the files used to create it for a map identified by its id
     *
     * @param finalMap : the map you want to add geoJsonFile from the db
     * @return Saved map
     * */
    public FinalMap save(FinalMap finalMap){
        return finalMapRepository.save(finalMap);
    }

    /**
     * Delete the map which identifier equals id
     * (All the element linked to the map (raster map, evaluation form) will also be deleted)
     *
     * @param id The id of the map you want to delete
     */
    @Transactional
    public void deleteMap(long id){
        final Optional<FinalMap> map = findById(id);
        if (map.isEmpty()) {
            throw new IllegalArgumentException("Map does not exist");
        }
        final FinalMap finalMapDel = map.get();
        final List<RasterMap> rasterMaps = finalMapDel.getRasterMaps();

        //Delete all the tile link to the raster map
        for (RasterMap rasterMap : rasterMaps) {
            final List<Tile> tiles = tileService.allTileForAspecificRasterMap(rasterMap);
            for (Tile tile : tiles) {
                tileService.deleteTile(tile.getTileId());
            }
        }

        //Delete forms linked to the map
        final List<EvaluationForm> evaluationForms = evaluationFormService.getAllFormForFinalMap(finalMapDel);
        for(EvaluationForm evaluationForm:evaluationForms){
            evaluationFormService.deleteForm(evaluationForm.getId(),evaluationForm.getUser());
        }

        finalMapRepository.delete(finalMapDel);// The raster map is delete in cascade with the map no explicit deletion
    }

    /**
     * Converts a zipFile containing shapefiles into a geoJSON file
     *
     * @param id : id of the map in the database
     * @throws IOException : if method findShpFile doesn't find a shp file
     */
    public String zipToGeoJsonFile(long id) throws IOException, FactoryException {
        final FinalMap finalMap = finalMapRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("The map is not found for this id :"+id));
        final File tempFile = Files.createTempDirectory("shp_").toFile();

        unzipper.unzip(finalMap, tempFile);

        // shp file that will be converted and used to detect others important files into zip file for geojson file
        final File shpFile = findShpFile(tempFile);

        if (shpFile == null){
            throw new IOException("shpFile is null");
        }
        return transformShapeFileToGeoJsonFile(shpFile);
    }

    /**
     * Returns all the maps from the database
     *
     * @returns a list of all the maps
     */
    public List<FinalMap> findAll(){
        return finalMapRepository.findAll();
    }
}
