package com.webgis.admin.map;

import com.webgis.MessageDto;
import com.webgis.exception.NotFound;
import com.webgis.exception.SecurityZipFile;
import com.webgis.map.finalmap.FinalMap;
import com.webgis.map.finalmap.FinalMapService;
import com.webgis.map.finalmap.dto.FinalMapListDto;
import com.webgis.map.raster.RasterMap;
import com.webgis.map.raster.RasterMapService;
import com.webgis.map.service.TransformTifFiles;
import org.geotools.api.referencing.FactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

import static com.converter.DetectFiles.validZipFile;

@RestController
@RequestMapping("/admin/finalMaps")
public class AdminFinalMapController {

    private final FinalMapService finalMapService;
    private final RasterMapService rasterMapService;
    private final TransformTifFiles transformTifFiles;

    static Logger logger = LoggerFactory.getLogger(AdminFinalMapController.class);

    public AdminFinalMapController(
            FinalMapService finalMapService,
            RasterMapService rasterMapService,
            TransformTifFiles transformTifFiles
    ){
        this.finalMapService = finalMapService;
        this.rasterMapService = rasterMapService;
        this.transformTifFiles = transformTifFiles;
    }

    /**
     * Delete the final map with the specified id
     * (It will also delete all the map related element(raster map, evaluations forms)
     *
     * @param id the id of the map you want to delete
     *
     * @return Ok if the map is successfully deleted, not found otherwise
     */
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Object> deleteMap(@PathVariable long id) {
        try {
            finalMapService.deleteMap(id);
            return ResponseEntity.status(200).body(new MessageDto("Map deleted successfully"));
        } catch(IllegalArgumentException e) {
            return ResponseEntity.status(404).body(new MessageDto(e.getMessage()));
        }
    }


    @PostMapping(value = "/uploadShapeFile", consumes = "multipart/form-data" )
    public ResponseEntity<Object> postShapeFile(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("tags") List<String> tags,
            @RequestParam("zipFile") MultipartFile zipFile,
            @RequestParam("tifFiles") List<MultipartFile> tifFiles,
            @RequestParam(value = "geoJsonFile", required = false) MultipartFile geoJsonFile){

        try {
            final FinalMap finalMap = new FinalMap(title,
                    description,
                    tags,
                    zipFile.getBytes(),
                    null);

            if (!(validZipFile(finalMap.getZipFile()))){
                throw new SecurityZipFile("The format of the zip file is not detected as a zipFile");
            }

            finalMapService.save(finalMap);

            if (geoJsonFile != null){
                finalMap.setFileGeoJson(new String(geoJsonFile.getBytes()));
            } else{
                if (finalMap.getId()== null){
                    throw new NotFound("There is no id for this map : " + finalMap.getTitle());
                } else {
                    final String tempGeoJsonFile = finalMapService.zipToGeoJsonFile(finalMap.getId());
                    finalMap.setFileGeoJson(tempGeoJsonFile);
                }
            }



            final FinalMap savedFinalMap = finalMapService.save(finalMap);

            for (MultipartFile tifFile : tifFiles) {
                final String tifTitle = tifFile.getOriginalFilename() != null
                        ? tifFile.getOriginalFilename()
                        : title;
                final RasterMap rasterMap = new RasterMap(tifTitle, description);
                rasterMap.setFinalMap(savedFinalMap);
                rasterMapService.save(rasterMap);
                transformTifFiles.transformIntoTileFile(rasterMap.getId(), tifFile);
            }

            return ResponseEntity.status(200).body(
                    new FinalMapListDto(savedFinalMap.getId(),
                            savedFinalMap.getTitle(),
                            savedFinalMap.getDescription(), finalMap.getTags()));
        } catch (NotFound e) {
            return ResponseEntity.status(400).body(new MessageDto(e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new MessageDto(e.getMessage()));
        } catch (FactoryException e) {
            throw new RuntimeException(e);
        }
    }
}
