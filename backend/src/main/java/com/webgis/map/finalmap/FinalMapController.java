package com.webgis.map.finalmap;

import com.webgis.MessageDto;
import com.webgis.map.finalmap.dto.FinalMapDto;
import com.webgis.map.finalmap.dto.FinalMapListDto;
import com.webgis.map.raster.dto.RasterMapListDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/finalMaps")
public class FinalMapController {
    private final FinalMapService finalMapService;

    public FinalMapController(FinalMapService finalMapService){
        this.finalMapService = finalMapService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getGeoJsonFile(@PathVariable long id) {
        try {
            final Optional<FinalMap> optionalMap = finalMapService.findById(id);
            if (optionalMap.isPresent()) {
                final FinalMap finalMap = optionalMap.get();

                final List<RasterMapListDto> rasterMaps = finalMap.getRasterMaps().stream()
                        .map(r -> new RasterMapListDto(r.getId(), r.getTitle()))
                        .toList();

                final FinalMapDto finalMapDto = new FinalMapDto(
                        finalMap.getId(),
                        finalMap.getTitle(),
                        finalMap.getDescription(),
                        finalMap.getTags(),
                        finalMap.getFileGeoJson(),
                        rasterMaps);
                return ResponseEntity.status(200).body(finalMapDto);
            }
            return ResponseEntity.status(404).body(new MessageDto("Map not found"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(new MessageDto(e.getMessage()));
        }
    }

    @GetMapping("/allMaps")
    public ResponseEntity<Object> getAllMaps() {
        try {
            final List<FinalMap> allFinalMaps = finalMapService.findAll();
            final List<FinalMapListDto> finalMapListDtoList = new ArrayList<>();
            for(FinalMap finalMap : allFinalMaps){
                finalMapListDtoList.add(new FinalMapListDto(
                        finalMap.getId(),
                        finalMap.getTitle(),
                        finalMap.getDescription(),
                        finalMap.getTags())
                );
            }
            return ResponseEntity.status(200).body(finalMapListDtoList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(new MessageDto(e.getMessage()));
        }
    }

}
