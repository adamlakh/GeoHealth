import { Injectable } from '@angular/core';
import { environment} from '../../rest-api-management/environment';
import {HttpClient } from '@angular/common/http';
import {Observable } from 'rxjs';
import {API_ENDPOINTS } from '../../rest-api-management/endpoint';
import {SaveEvaluatorProfileDto } from '../../../shared/models/EvaluatorProfileModel/SaveEvaluatorProfileDto';
import {ResponseEvaluatorProfileDto } from '../../../shared/models/EvaluatorProfileModel/ResponseEvaluatorProfileDto';


@Injectable({
  providedIn: 'root'
})
export class EvaluatorProfileService {

  private baseUrl = environment.apiBaseUrl;

  constructor(private httpClient: HttpClient) {}

  /**
   * Save the evaluator profile for the connected user.
   * Can only be called once per user.
   */
  public saveProfile(evaluatorProfileDto: SaveEvaluatorProfileDto): Observable<ResponseEvaluatorProfileDto> {
    return this.httpClient.post<ResponseEvaluatorProfileDto>(
      `${this.baseUrl}${API_ENDPOINTS.EVALUATORPROFILE.SAVE}`,
      evaluatorProfileDto,
      { withCredentials: true }
    );
  }

  /**
   * Check whether the connected user has already submitted their evaluator profile.
   */
  public hasProfile(): Observable<boolean> {
    return this.httpClient.get<boolean>(
      `${this.baseUrl}${API_ENDPOINTS.EVALUATORPROFILE.HASPROFILE}`,
      { withCredentials: true }
    );
  }

  /**
   * Get the connected user's saved evaluator profile.
   */
  public getProfile(): Observable<ResponseEvaluatorProfileDto> {
    return this.httpClient.get<ResponseEvaluatorProfileDto>(
      `${this.baseUrl}${API_ENDPOINTS.EVALUATORPROFILE.GETPROFILE}`,
      { withCredentials: true }
    );
  }

  /**
   * Update the connected user's existing evaluator profile.
   */
  public updateProfile(evaluatorProfileDto: SaveEvaluatorProfileDto): Observable<ResponseEvaluatorProfileDto> {
    return this.httpClient.put<ResponseEvaluatorProfileDto>(
      `${this.baseUrl}${API_ENDPOINTS.EVALUATORPROFILE.UPDATE}`,
      evaluatorProfileDto,
      { withCredentials: true }
    );
  }

}
