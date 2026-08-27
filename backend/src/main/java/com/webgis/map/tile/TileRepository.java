package com.webgis.map.tile;

import com.webgis.map.raster.RasterMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface TileRepository extends JpaRepository<Tile, TileId> {
    Optional<Tile> findByTileId(TileId tileId);
    List<Tile> findTileByRasterMap(RasterMap rasterMap);

    @Modifying
    @Transactional
    @Query("DELETE FROM Tile t WHERE t.rasterMap.id = :rasterMapId")
    void deleteAllByRasterMapId(long rasterMapId);
}