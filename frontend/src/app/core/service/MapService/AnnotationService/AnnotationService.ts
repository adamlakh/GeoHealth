import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable} from "rxjs";
import {environment} from '../../../rest-api-management/environment';
import {API_ENDPOINTS} from '../../../rest-api-management/endpoint';
import {AnnotationDTO} from '../../../../shared/models/MapModel/AnnotationModel/AnnotationDTO';

@Injectable({
  providedIn: 'root'
})
export class AnnotationService {

  private baseUrl = environment.apiBaseUrl;

  constructor(private HttpClient: HttpClient) {}

  public getAnnotations(mapId:number,
                        userId:number
                        ): Observable<AnnotationDTO> {
    return this.HttpClient.get<AnnotationDTO>(`${this.baseUrl}${API_ENDPOINTS.ANNOTATIONS.GET}/${mapId}/${userId}`,
      { withCredentials: true}
    );
  }

  public postAnnotations(dto:AnnotationDTO): Observable<any> {
    return this.HttpClient.post<any>(`${this.baseUrl}${API_ENDPOINTS.ANNOTATIONS.POST}`,
      dto,
      {withCredentials: true}
    );
  }

}
