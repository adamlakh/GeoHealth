package com.webgis.map.raster;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RasterMapRepository extends JpaRepository<RasterMap, Long> {
    Optional<RasterMap> findById(long id);
}
