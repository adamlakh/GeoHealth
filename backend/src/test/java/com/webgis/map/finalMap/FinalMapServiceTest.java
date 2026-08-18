package com.webgis.map.finalMap;


import com.webgis.evaluationform.EvaluationFormService;
import com.webgis.map.finalmap.FinalMap;
import com.webgis.map.finalmap.FinalMapRepository;
import com.webgis.map.finalmap.FinalMapService;
import com.webgis.map.tile.TileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinalMapServiceTest {

    @Mock
    FinalMapRepository finalMapRepository;

    @Mock
    TileService tileService;

    @Mock
    EvaluationFormService evaluationFormService;

    @InjectMocks
    FinalMapService finalMapService;

    private FinalMap fm1;
    private FinalMap fm2;
    private FinalMap fm3;
    private long id;

    @BeforeEach
    void init(){
        List<String> list = new ArrayList<>(List.of("dry"));
        byte[] bytea = new byte[0];
        fm1 = new FinalMap("test1", "testdescription", list,bytea, "fakefile");
        fm2 = new FinalMap("test2", "testdescription",list,  bytea, "fakefile");
        fm3 = new FinalMap("test3", "testdescription", list, bytea, "fakefile");
        id = 1;
    }

    @Test
    void deleteMapNotFound(){
        //Arrange
        when(finalMapService.findById(1)).thenReturn(Optional.empty());

        //Act
        assertThrows(IllegalArgumentException.class, () -> finalMapService.deleteMap(1));

    }

    @Test
    void deleteMapValid(){
        // Arrange
        fm1.setRasterMaps(new ArrayList<>());
        when(finalMapRepository.findById(id)).thenReturn(Optional.of(fm1));
        when(evaluationFormService.getAllFormForFinalMap(fm1)).thenReturn(List.of());

        // Act
        finalMapService.deleteMap(id);

        // Assert
        verify(finalMapRepository).delete(fm1);
    }

    @Test
    void findAllValid(){

        //Arrange

        List<FinalMap> mapList = List.of(fm1, fm2, fm3);

        when(finalMapRepository.findAll()).thenReturn(mapList);

        //Act
        List<FinalMap> results = finalMapService.findAll();

        //Asserts
        assertEquals(3, results.size());
        assertTrue(results.contains(fm1));
        assertTrue(results.contains(fm2));
        assertTrue(results.contains(fm3));
    }

    @Test
    void saveValid(){
        //Arrange
        when(finalMapRepository.save(fm1)).thenReturn(fm1);

        //Act
        FinalMap result = finalMapService.save(fm1);

        //Assert
        assertEquals(fm1, result);
    }



    @Test
    void transformShapeFileNotFound() throws IOException {
        //Arrange
        when(finalMapRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> finalMapService.zipToGeoJsonFile(id));
    }

}