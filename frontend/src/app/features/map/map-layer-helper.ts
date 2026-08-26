import {getColorFromCmbnd, getRiskColor} from './map-utils';
import { MapOption } from './map-types';
import { TileMeanAndXYdto } from '../../shared/models/MapModel/RasterMapModel/TileMeanAndXYdto';
import { getTileMean, tileToPolygon } from './tile-utils';
import { signal } from '@angular/core';

export class MapLayerHelper {

  // Leaflet library instance
  private leaflet: any = null;
  // the main Leaflet map instance
  private map: any = null;
  // the GeoJSON layer displaying the divisions
  private geoJsonLayer: any = null;
  // the raster tile layer displayed
  private tileLayer: any = null;
  // the blue dot marker placed on division click
  private marker: any = null;
  // the red rectangle highlighting the clicked tile block
  private highlightLayer: any = null;
  // all annotation on the map
  private geoManLayer: any;
  // the value of the pixel (pixel group) in the raster layer
  public lastBlockMean = signal<number | null>(null);

  private inspectModeActive: boolean = false;

  /**
   * Initializes the Leaflet map on the given HTML element
   * and adds the OpenStreetMap background tiles
   *
   * @param elementId - the id of the HTML element to render the map in
   * @param center - the initial center coordinates [lat, lng]
   * @param zoom - the initial zoom level
   * @param minZoom - the minimum allowed zoom level
   * @param maxZoom - the maximum allowed zoom level
   */
  async initMap(elementId: string, center: any, zoom: number, minZoom : number, maxZoom : number, enableGeoman: boolean): Promise<void> {
    const leafletModule = await import('leaflet');
    if (enableGeoman) {
      await import('@geoman-io/leaflet-geoman-free');
    }

    const L = (leafletModule as any).default ?? leafletModule;
    this.leaflet = L;

    delete (L.Icon.Default.prototype as any)._getIconUrl;
    L.Icon.Default.mergeOptions({
      iconUrl:       'assets/leaflet-images/marker-icon.png',
      iconRetinaUrl: 'assets/leaflet-images/marker-icon-2x.png',
      shadowUrl:     'assets/leaflet-images/marker-shadow.png',
    });

    this.map = this.leaflet.map(elementId).setView(center, zoom);

    this.map.setMinZoom(minZoom);
    this.map.setMaxZoom(maxZoom);
    this.map.createPane('markerPane');
    this.map.getPane('markerPane').style.zIndex = 400;

    this.leaflet.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap'
    }).addTo(this.map);

    this.geoManLayer = this.leaflet.featureGroup().addTo(this.map);

    if (enableGeoman) {
      (this.map as any).pm.addControls({
        position: 'topleft',
        drawCircleMarker: false,
        rotateMode: false,
      });
    }

    if (enableGeoman) {
      (this.map as any).pm.setGlobalOptions({ layerGroup: this.geoManLayer });
    }
  }



  /**
   * Toggle the visibility of annotations on the map
   *
   * @param visible : boolean variable that controls annotation visibility
   *    True : show annotations
   *    False : hide annotations
   */
  toggleAnnotationsVisibility(visible: boolean): void {
    if (!this.geoManLayer) {
      return;
    }

    this.geoManLayer.eachLayer((layer: any) => {
      let element = null;

      // find markers
      if (layer.getElement) {
        element = layer.getElement();
      }

      // find polygons and lines
      if (!element && layer._path) {
        element = layer._path;
      }

      // find canvas
      if (!element && layer._renderer) {
        element = layer._renderer._container;
      }

      if (element){
        element.style.display = visible ? 'block' : 'none';
      }
    });
  }

  /**
   * Get annotations and transform it into string to allow the data to be sent to backend
   *
   * @return :
   *    GeoJSON data (String) : if the geoman data can be translated and ready to send
   *    null : otherwise
   * */
  getGeomanGeojson(): String | null {
    if (this.geoManLayer == null) return null;

    try {
      this.geoManLayer.eachLayer((layer: any) => {
        const shape = layer.pm?.getShape?.();
        if (!layer.feature) layer.feature = { type: 'Feature', properties: {} };
        if (!layer.feature.properties) layer.feature.properties = {};

        if (shape) layer.feature.properties.shape = shape;
        if (shape === 'Circle' && layer.getRadius) {
          layer.feature.properties.radius = layer.getRadius();
        }
        if (shape === 'CircleMarker' && layer.getRadius) {
          layer.feature.properties.radius = layer.getRadius();
        }
        if (shape === 'Text') {
          layer.feature.properties.text = layer.pm?.getText?.();
        }
      });

      const geomanInGeojson = this.geoManLayer.toGeoJSON();

      if (geomanInGeojson.features.length === 0) return null;
      return JSON.stringify(geomanInGeojson);

    } catch (e) {
      console.log("Issue during transformation of annotations");
      return null;
    }
  }

  /**
   * Take geojson data in string format into GeoJSON applicable on the
   * layer and add it on the map
   *
   * @param geoJsonString : GeoJSON data in String format
   * */
  loadAnnotationsFromGeoJson(geoJsonString: string): void {
    if (!this.leaflet || !this.geoManLayer){
      return;
    }

    this.geoManLayer.clearLayers();
    const geoJsonData = JSON.parse(geoJsonString);

    this.leaflet.geoJSON(geoJsonData, {
      pointToLayer: (feature: any, latlng: any) => {
        const shape = feature.properties?.shape;

        if (shape === 'Circle') {
          return this.leaflet.circle(latlng, {
            radius: feature.properties?.radius ?? 100
          });
        }

        if (shape === 'CircleMarker') {
          return this.leaflet.circleMarker(latlng, {
            radius: feature.properties?.radius ?? 10
          });
        }

        if (shape === 'Text') {
          const text = feature.properties?.text ?? '';
          const marker = this.leaflet.marker(latlng, {
            textMarker: true,
            text: text
          });
          return marker;
        }
        return this.leaflet.marker(latlng);
      }
    }).eachLayer((layer: any) => {
      this.geoManLayer.addLayer(layer);
    });
  }


  /**
   * Draws the GeoJSON divisions layer on the map
   * and colors each division based on its risk category
   *
   * @param geoJsonString - the GeoJSON string representing the divisions
   * @param onDivisionClick - callback fired when a division is clicked
   * @param tag - adapt the color display for Ebola map
   */
  applyDivisionsLayer(geoJsonString: string, onDivisionClick: (event: any) => void, tag?: string): void {
    const geoJson = JSON.parse(geoJsonString);
    const isDry = tag?.toLowerCase() === 'dry' || tag?.toLowerCase() === 'all_season';

    this.geoJsonLayer = this.leaflet.geoJSON(geoJson, {
      style: (feature: any) => {
        const props = feature?.properties;
        let fillColor: string;

        if (props?.rsk_cls) {
          fillColor = getRiskColor(props.rsk_cls);
        } else {
          const riskValue = props?.rsk_cmb ?? props?.cmbnd__ ?? 0;
          fillColor = getColorFromCmbnd(riskValue, isDry);
        }

        return {
          color: '#414241',
          weight: 1,
          fillColor,
          fillOpacity: 0.5,
        };
      },
      onEachFeature: (feature: any, layer: any) => {
        layer.options.pmIgnore = true;
        layer.on('mouseover', () => layer.setStyle({ weight: 2 }));
        layer.on('mouseout', () => layer.setStyle({ weight: 1 }));
        layer.on('click', (e: any) => {
          onDivisionClick({ properties: feature.properties, latlng: e.latlng });
        });
      }
    }).addTo(this.map);

    this.map.fitBounds(this.geoJsonLayer.getBounds());
  }

  /**
   * Switches the map display between divisions and a raster tile layer
   *
   * @param option - the selected map option containing the kind (divisions or tile) and optional id
   */
  switchTo(option: MapOption): void {
    this.clearTileLayer();
    if (option.kind === 'divisions') {
      this.geoJsonLayer?.setStyle({ fillOpacity: 0.5 });
    } else {
      this.geoJsonLayer?.setStyle({ fillOpacity: 0 });
      this.tileLayer = this.leaflet.tileLayer(
        `/tile/file/${option.id}/{z}/{x}/{y}.png`,
        { opacity: 0.7, zIndex: 500 }
      ).addTo(this.map);
      this.onTileSelected(option.id!, (v) => this.lastBlockMean.set(v));
    }
  }

  /**
   * Listens for clicks on the map when a raster tile layer is active
   * fetches the mean value of the clicked block and highlights it in red
   *
   * @param mapId - the id of the raster map currently displayed
   */
  onTileSelected(mapId: number, setMean: (v: number | null) => void): void {
      this.map.on('click', async (e:any)=>{
        console.log("\n [onTileSelected] : " + e.latlng)
        const coordinates : any = e.latlng
        const z : number = this.map.getZoom();

        console.log(mapId);
        console.log(coordinates.lat, coordinates.lng);
        const blockData : TileMeanAndXYdto | null = await getTileMean(mapId, z, coordinates.lat, coordinates.lng);

        if (blockData){
          console.log(blockData.mean);
          setMean(blockData.mean);


          const bounds = tileToPolygon(blockData.tileX, blockData.tileY, z, blockData.blockX, blockData.blockY);

          if (this.highlightLayer) {
            this.map.removeLayer(this.highlightLayer);
          }

          this.highlightLayer = this.leaflet.polygon(bounds, {
            color: 'red',
            weight: 2,
            fillOpacity: 0.1
          }).addTo(this.map);
      }
      });
    }

  /**
   * Places a blue circle marker at the given coordinates
   *
   * @param latlng - the coordinates where the marker should be placed
   */
  placeMarker(latlng: any): void {
    this.clearMarker();
    this.marker = this.leaflet.circleMarker(latlng, {
      radius: 5,
      color: '#1356eb',
      fillColor: '#1959e6',
      fillOpacity: 0.8,
      pane: 'markerPane',
    }).addTo(this.map);
  }

  /**
   * Removes the current marker from the map if one exists
   */
  clearMarker(): void {
    if (this.marker) {
      this.marker.remove();
      this.marker = null;
    }
  }

  /**
   * Removes the current raster tile layer from the map if one exists
   */
  clearTileLayer(): void {
    if (this.tileLayer) {
      this.tileLayer.remove();
      this.tileLayer = null;
    }
    if (this.highlightLayer) {
      this.map.removeLayer(this.highlightLayer);
      this.highlightLayer = null;
    }
    this.map.off('click');
    this.lastBlockMean.set(null);
  }

  clearGeomanLayers(): void {
    if (!this.geoManLayer) {
      return;
    }
    this.geoManLayer.clearLayers();
  }

  getMap(): any {
    return this.map;
  }

  /**
   * Checks if a raster tile layer is currently active
   *
   * @return true if a raster tile layer is active, false otherwise
   */
  isRasterActive(): boolean {
      return this.tileLayer !== null;
  }

  /**
   * Captures the current vector layers (division polygons + geoman annotations)
   * as a PNG blob, on a transparent background.
   */
  /**
   * Captures the current vector layers (division polygons + geoman annotations)
   * as a PNG blob, on a transparent background.
   */
createAnnotationPng(): Promise<Blob> {
  const svg: SVGSVGElement | null = this.map.getPane('overlayPane')?.querySelector('svg');
  if (!svg) {
    return Promise.reject(new Error('No SVG found'));
  }

  const svgClone = svg.cloneNode(true) as SVGSVGElement;
  const size = this.map.getSize();
  svgClone.setAttribute('width', size.x.toString());
  svgClone.setAttribute('height', size.y.toString());

  const targetGroup = svgClone.querySelector('g') ?? svgClone;
  if (this.geoManLayer) {
    this.geoManLayer.eachLayer((layer: any) => {
      const shape = layer.pm?.getShape?.() ?? layer.feature?.properties?.shape;

      if (shape === 'Marker' && layer.getLatLng) {
        const point = this.map.latLngToLayerPoint(layer.getLatLng());

        const markerEl = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
        markerEl.setAttribute('cx', point.x.toString());
        markerEl.setAttribute('cy', point.y.toString());
        markerEl.setAttribute('r', '8');
        markerEl.setAttribute('fill', '#1959e6');
        markerEl.setAttribute('stroke', 'white');
        markerEl.setAttribute('stroke-width', '2');
        targetGroup.appendChild(markerEl);
        return;
      }

      if (shape !== 'Text' || !layer.getLatLng) return;

      const text: string =
        layer.pm?.getText?.() ??
        layer.feature?.properties?.text ??
        layer.options?.text ??
        '';
      if (!text) return;

      const point = this.map.latLngToLayerPoint(layer.getLatLng());

      const textEl = document.createElementNS('http://www.w3.org/2000/svg', 'text');
      textEl.setAttribute('x', point.x.toString());
      textEl.setAttribute('y', point.y.toString());
      textEl.setAttribute('font-size', '14');
      textEl.setAttribute('fill', '#222222');
      textEl.setAttribute('stroke', 'white');
      textEl.setAttribute('stroke-width', '4');
      textEl.setAttribute('paint-order', 'stroke');
      textEl.textContent = text;
      targetGroup.appendChild(textEl);
    });
  }

  const svgString = new XMLSerializer().serializeToString(svgClone);
  const svgBlob = new Blob([svgString], { type: 'image/svg+xml;charset=utf-8' });
  const url = URL.createObjectURL(svgBlob);

  return new Promise((resolve, reject) => {
    const img = new Image();
    img.onload = () => {
      const canvas = document.createElement('canvas');
      canvas.width = size.x;
      canvas.height = size.y;
      const ctx = canvas.getContext('2d')!;
      ctx.drawImage(img, 0, 0);
      URL.revokeObjectURL(url);
      canvas.toBlob((blob) => blob ? resolve(blob) : reject(new Error('toBlob failed')));
    };
    img.onerror = (err) => {
      URL.revokeObjectURL(url);
      reject(err);
    };
    img.src = url;
  });
}
}
