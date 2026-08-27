package com.webgis.admin.map;

import com.webgis.MessageDto;
import com.webgis.exception.NotFound;
import com.webgis.exception.SecurityZipFile;
import com.webgis.map.finalmap.FinalMap;
import com.webgis.map.finalmap.FinalMapService;
import com.webgis.map.finalmap.dto.FinalMapListDto;
import com.webgis.map.raster.RasterMap;
import com.webgis.map.raster.RasterMapService;
import com.webgis.map.raster.dto.RasterMapListDto;
import com.webgis.map.service.TransformTifFiles;
import org.geotools.api.referencing.FactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

import static com.converter.DetectFiles.validZipFile;

@RestController
@RequestMapping("/admin/rasterMaps")
public class AdminRasterMapController{
    private final RasterMapService rasterMapService;

    public AdminRasterMapController(RasterMapService rasterMapService){
        this.rasterMapService = rasterMapService;
    }

    @PostMapping("")
}
