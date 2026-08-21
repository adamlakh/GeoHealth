import {Injectable} from '@angular/core';
import {environment} from '../../rest-api-management/environment';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {API_ENDPOINTS} from '../../rest-api-management/endpoint';
import {DivisionRiskDto} from '../../../shared/models/MeasureModel/DivisionRiskDto';


@Injectable({
  providedIn: 'root'
})

export class ReportService{

  private baseUrl = environment.apiBaseUrl;

  constructor(private httpClient: HttpClient) {}

  /**
   * Get a report containing all the metrics for a specific map if the map exists, not found otherwise
   *
   * @param mapId the map you are interested in
   * @param divisionRiskDto A dto containing the risk evaluated in the model(original map) for each division
   *
   * @return the .xlsx report for a map in a byte array format if the map exists, not found otherwise
   */
  public getReport(mapId: number, divisionRiskDto: DivisionRiskDto): Observable<any> {
    return this.httpClient.post(
      `${this.baseUrl}${API_ENDPOINTS.REPORT.GET}/${mapId}` ,
      divisionRiskDto,
      { withCredentials: true,
        responseType:'blob'})
  }

  /**
   * TEST ONLY: get the .xlsx report for a specific user on a specific map
   */
  public getUserReport(mapId: number, userId: number): Observable<any> {
    return this.httpClient.get(
      `${this.baseUrl}${API_ENDPOINTS.REPORT.GETUSERREPORT}/${mapId}/${userId}`,
      {withCredentials: true, responseType: 'blob'}
    );
  }

}
