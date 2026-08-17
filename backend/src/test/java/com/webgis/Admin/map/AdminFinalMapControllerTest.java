package com.webgis.admin.map;

import com.webgis.MessageDto;
import com.webgis.exception.NotFound;
import com.webgis.map.finalmap.FinalMap;
import com.webgis.map.finalmap.FinalMapService;
import com.webgis.map.raster.RasterMapService;
import com.webgis.map.service.TransformTifFiles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminFinalMapControllerTest {

    @Mock
    private FinalMapService finalMapService;

    @Mock
    private RasterMapService rasterMapService;

    @Mock
    private TransformTifFiles transformTifFiles;

    @InjectMocks
    private AdminFinalMapController controller;

    private MockMultipartFile makeTif() {
        return new MockMultipartFile("tifFile", "map.tif", "image/tiff", "tif-data".getBytes());
    }

    private MockMultipartFile makeZip() {
        byte[] zipMagicBytes = new byte[]{0x50, 0x4B, 0x03, 0x04, 0x00, 0x00};
        return new MockMultipartFile("zipFile", "shapes.zip", "application/zip", zipMagicBytes);
    }

    private FinalMap stubFinalMap(Long id) {
        FinalMap fm = new FinalMap("Title", "Desc", List.of("dry"), "zip-data".getBytes(), null);
        fm.setId(id);
        return fm;
    }


    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void sneakyThrow(Throwable e) throws E {
        throw (E) e;
    }

    //deleteMap Test
    @Test
    void deleteMapShouldReturnOkTest() {
        //Act
        doNothing().when(finalMapService).deleteMap(1L);

        //Arrange
        ResponseEntity<Object> response = controller.deleteMap(1L);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(MessageDto.class, response.getBody());
        assertEquals("Map deleted successfully", ((MessageDto) response.getBody()).getMessage());
        verify(finalMapService).deleteMap(1L);
    }

    @Test
    void deleteMapShouldReturnNotFoundTest() {

        //Arrange
        doThrow(new IllegalArgumentException("Map not found"))
                .when(finalMapService).deleteMap(99L);

        //Act
        ResponseEntity<Object> response = controller.deleteMap(99L);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertInstanceOf(MessageDto.class, response.getBody());
        assertEquals("Map not found", ((MessageDto) response.getBody()).getMessage());
    }

    //postShapeFile test
    @Test
    void postShapeFileNullIdAfterFirstSaveShouldReturnBadRequestTest(){
        when(finalMapService.save(any(FinalMap.class))).thenReturn(stubFinalMap(null));

        ResponseEntity<Object> response = controller.postShapeFile(
                "Title", "Desc", List.of("dry"), makeZip(), List.of(makeTif()), null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(MessageDto.class, response.getBody());
        assertTrue(((MessageDto) response.getBody()).getMessage().contains("There is no id"));
    }


    @Test
    void postShapeFileIoExceptionShouldReturnsInternalServerErrorTest() throws Exception {

        //Arrange
        MultipartFile badZip = mock(MultipartFile.class);
        when(badZip.getBytes()).thenThrow(new IOException("disk error"));

        //Act
        ResponseEntity<Object> response = controller.postShapeFile(
                "Title", "Desc", List.of("dry"), badZip, List.of(makeTif()), null);

        //Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(MessageDto.class, response.getBody());
        assertEquals("disk error", ((MessageDto) response.getBody()).getMessage());
    }



    @Test
    void postShapeFileIoExceptionOnGeoJsonShouldReturnsInternalServerError() throws Exception {
        //Arrange
        MultipartFile badGeoJson = mock(MultipartFile.class);
        when(badGeoJson.getBytes()).thenThrow(new IOException("geojson read error"));

        when(finalMapService.save(any(FinalMap.class))).thenReturn(stubFinalMap(1L));

        //Act
        ResponseEntity<Object> response = controller.postShapeFile(
                "Title", "Desc", List.of("dry"), makeZip(), List.of(makeTif()), badGeoJson);

        //Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("geojson read error", ((MessageDto) response.getBody()).getMessage());
    }


    @Test
    void postShapeFileNotFoundFromZipConversionShouldReturnsBadRequestTest() throws Exception {

        //Arrange
        doAnswer(invocation -> {
            FinalMap fm = invocation.getArgument(0);
            fm.setId(5L);
            return fm;
        }).when(finalMapService).save(any(FinalMap.class));

        doAnswer(invocation -> {
            sneakyThrow(new NotFound("Conversion failed"));
            return null;
        }).when(finalMapService).zipToGeoJsonFile(5L);

        //Act
        ResponseEntity<Object> response = controller.postShapeFile(
                "Title", "Desc", List.of("dry"), makeZip(), List.of(makeTif()), null);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Conversion failed", ((MessageDto) response.getBody()).getMessage());
    }

}