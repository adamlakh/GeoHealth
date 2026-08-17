package com.webgis.map.raster;



import com.webgis.map.raster.dto.RasterMapListDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rasterMaps")
public class RasterMapController {
    private final RasterMapService rasterMapService;

    public RasterMapController(RasterMapService rasterMapService){
        this.rasterMapService = rasterMapService;
    }

    @GetMapping("/file/{id}")
    public ResponseEntity<Object> getRasterMap(@PathVariable long id) {
        final Optional<RasterMap> rasterMap = rasterMapService.findById(id);
        if (rasterMap.isPresent()) {
            final RasterMap r = rasterMap.get();
            return ResponseEntity.status(200).body(new RasterMapListDto(r.getId(), r.getTitle()));
        }
        return ResponseEntity.status(404).build();
    }

    @GetMapping("/rasters")
    public ResponseEntity<List<RasterMapListDto>> getAllRasterMaps() {
        try {
            final List<RasterMapListDto> dtoList = new ArrayList<>();

            final List<RasterMap> riskFactorMapList = rasterMapService.findAll();
            for (RasterMap r : riskFactorMapList) {
                final RasterMapListDto dto = new RasterMapListDto(r.getId(), r.getTitle());
                dtoList.add(dto);
            }

            return ResponseEntity.status(200).body(dtoList);
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }


}