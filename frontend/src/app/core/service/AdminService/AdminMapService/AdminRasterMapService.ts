import {Injectable } from '@angular/core';
import {HttpClient } from '@angular/common/http';
import {Observable } from 'rxjs';
import {environment } from '../../../rest-api-management/environment';
import {MessageDto } from '../../../../shared/models/MessageDto';
import {API_ENDPOINTS } from '../../../rest-api-management/endpoint';
import {RasterMapListDto } from '../../../../shared/models/MapModel/RasterMapModel/RasterMapListDto';

@Injectable({
  providedIn: 'root'
})
export class AdminRasterMapService {

  private baseUrl= environment.apiBaseUrl;

  constructor(private HttpClient: HttpClient) {}

  /**
   * Adds a new raster map to an existing final map (only for admin).
   *
   * @param finalMapId the id of the final map this raster map belongs to
   * @param formData FormData containing title, description, and tifFile
   */
  uplaodNewRasterMap(finalMapId: number, formData: FormData): Observable<RasterMapListDto> {
    return this.HttpClient.post<RasterMapListDto>(`${this.baseUrl}${API_ENDPOINTS.ADMIN.MAPS.RASTERMAPS.UPLOAD}/${finalMapId}`,
      formData,
      {withCredentials: true});
  }

  /**
   * Deletes a raster map and all of its tiles (only for admin).
   *
   * @param rasterMapId the id of the raster map to delete
   */
  deleteRasterMap(rasterMapId: number): Observable<MessageDto> {
    return this.HttpClient.delete<MessageDto>(`${this.baseUrl}${API_ENDPOINTS.ADMIN.MAPS.RASTERMAPS.DELETE}/${rasterMapId}`,
      {withCredentials: true}
    );
  }
}
