package com.webgis.map.raster;


import com.webgis.map.finalmap.FinalMap;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;

@Entity
@Table(name = "rasterMap")
public class RasterMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "final_map_id", nullable = false)
    private FinalMap finalMap;

    public RasterMap(){}

    public RasterMap(String title, String description){
        this.title = title;
        this.description = description;
    }

    public Long getId(){return id; }

    public String getTitle() {return title; }

    public String getDescription() { return description;}

    public FinalMap getFinalMap() { return finalMap; }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setFinalMap(FinalMap finalMap) {
        this.finalMap = finalMap;
    }

    public void setId(long id) {
        this.id = id;
    }
}

