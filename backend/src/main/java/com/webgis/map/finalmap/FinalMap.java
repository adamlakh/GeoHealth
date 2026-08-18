package com.webgis.map.finalmap;

import com.webgis.map.raster.RasterMap;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Basic;
import jakarta.persistence.FetchType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;

import java.util.List;

@Entity
@Table(name = "maps")
public class FinalMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private List<MapTag> tags;

    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition="BYTEA")
    private byte[] zipFile;

    @Column(name = "file_geo_json", columnDefinition = "TEXT")
    private String fileGeoJson;

    @OneToMany(mappedBy = "finalMap", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RasterMap> rasterMaps;

    public FinalMap(){}

    public FinalMap(String title,
                    String description,
                    List<String> tags,
                    byte[] zipFile,
                    String fileGeoJson){
        this.title=title;
        this.description =description;
        this.tags = transformStringIntoMaptag(tags);
        this.zipFile = zipFile;
        this.fileGeoJson = fileGeoJson;
    }

    public List<MapTag> transformStringIntoMaptag(List<String> tags){
        return tags.stream()
                .map(MapTag::fromValue)
                .toList();
    }


    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<MapTag> getTags() {
        return tags;
    }

    public byte[] getZipFile(){return zipFile;}

    public String getFileGeoJson() {
        return fileGeoJson;
    }

    public List<RasterMap> getRasterMaps() { return rasterMaps; }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {this.title = title;}

    public void setDescription(String description){
        this.description = description;
    }

    public void setZipFile(byte[] zipFile) {
        this.zipFile = zipFile;
    }

    public void setTags(List<MapTag> tags) {
        this.tags = tags;
    }

    public void setFileGeoJson(String fileGeoJson) {
        this.fileGeoJson = fileGeoJson;
    }

    public void setRasterMaps(List<RasterMap> rasterMaps) { this.rasterMaps = rasterMaps; }
}

