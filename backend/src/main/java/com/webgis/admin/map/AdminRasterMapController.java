package com.webgis.admin.map;

import com.webgis.MessageDto;
import com.webgis.map.raster.RasterMap;
import com.webgis.map.raster.RasterMapService;
import com.webgis.map.raster.dto.RasterMapListDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/admin/rasterMaps")
public class AdminRasterMapController{
    private final RasterMapService rasterMapService;

    public AdminRasterMapController(RasterMapService rasterMapService){
        this.rasterMapService = rasterMapService;
    }

    /**
     * Adds a new raster map to an existing final map, generating its tiles
     * from the given tif file.
     *
     * @param finalMapId id of the final map this raster map belongs to
     * @param title title of the new raster map
     * @param description description of the new raster map
     * @param tifFile tif file to generate tiles from
     *
     * @return the created raster map, or not found if the final map doesn't exist
     */
    @PostMapping(value = "/finalMap/{finalMapId}", consumes = "multipart/form-data")
    public ResponseEntity<Object> addRasterMap(
            @PathVariable long finalMapId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("tifFile") MultipartFile tifFile
    ){
        try{
            final RasterMap created = rasterMapService.addRasterMap(finalMapId, title, description, tifFile);
            return ResponseEntity.status(200).body(
                    new RasterMapListDto(created.getId(), created.getTitle()));
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(new MessageDto(e.getMessage()));
        }
    }

    /**
     * Deletes a raster map and all of its associated tiles.
     *
     * @param id id of the raster map to delete
     *
     * @return Ok if deleted, not found otherwise
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteRasterMap(@PathVariable long id) {
        try{
            rasterMapService.deleteRasterMap(id);
            return ResponseEntity.status(200).body(new MessageDto("Raster map deleted successfully"));
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(404).body(new MessageDto(e.getMessage()));
        }
    }
}
