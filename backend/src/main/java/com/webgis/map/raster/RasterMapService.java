package com.webgis.map.raster;


import com.webgis.map.finalmap.FinalMap;
import com.webgis.map.finalmap.FinalMapService;
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
    private final FinalMapService finalMapService;

    public RasterMapService(
            RasterMapRepository rasterMapRepository,
            TileService tileService,
            TransformTifFiles transformTifFiles,
            FinalMapService finalMapService){
        this.rasterMapRepository = rasterMapRepository;
        this.tileService = tileService;
        this.transformTifFiles = transformTifFiles;
        this.finalMapService = finalMapService;
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
     * Adds a new raster map to an existing final map, and generates its tiles
     * from the given tif file.
     *
     * @param finalMapId id of the final map this raster map belongs to
     * @param title title of the new raster map
     * @param description description of the new raster map
     * @param tifFile the tif file to generate tiles from
     * @return the newly created RasterMap
     */
    @Transactional
    public RasterMap addRasterMap(long finalMapId, String title, String description, MultipartFile tifFile){
        final Optional<FinalMap> finalMapOptional = finalMapService.findById(finalMapId);
        if (finalMapOptional.isEmpty()) {
            throw new IllegalArgumentException("FinalMap does not exist: " + finalMapId);
        }
        final FinalMap finalMap = finalMapOptional.get();

        final RasterMap rasterMap = new RasterMap(title, description);
        rasterMap.setFinalMap(finalMap);

        final RasterMap savedRasterMap = rasterMapRepository.save(rasterMap);
        transformTifFiles.transformIntoTileFile(savedRasterMap.getId(), tifFile);
        return savedRasterMap;
    }

    /**
     * Deletes a raster map and all of its associated tiles.
     *
     * @param id identifier of the raster map to delete
     */
    @Transactional
    public void deleteRasterMap(long id){
        final Optional<RasterMap> rasterMapOptional = rasterMapRepository.findById(id);

        if (rasterMapOptional.isEmpty()){
            throw new IllegalArgumentException("RasterMap does not exist: " + id);
        }

        final RasterMap rasterMap = rasterMapOptional.get();
        tileService.deleteAllTilesForRasterMap(rasterMap);
        rasterMapRepository.delete(rasterMap);
    }

}
