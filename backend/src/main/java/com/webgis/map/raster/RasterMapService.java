package com.webgis.map.raster;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class RasterMapService {

    static Logger logger = LoggerFactory.getLogger(RasterMapService.class);

    private final RasterMapRepository rasterMapRepository;


    public RasterMapService(RasterMapRepository rasterMapRepository){
        this.rasterMapRepository = rasterMapRepository;
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

}
