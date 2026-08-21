import {Injectable} from '@angular/core';
import {environment} from '../../../rest-api-management/environment';
import {HttpClient} from '@angular/common/http';
import {API_ENDPOINTS} from '../../../rest-api-management/endpoint';
import {Observable} from 'rxjs';
import {AdminResponseEvaluationFormDto} from '../../../../shared/models/AdminModel/EvaluationFormModel/AdminResponseEvaluationFormDto';
import {MessageDto} from '../../../../shared/models/MessageDto'
import {UserSummaryDto} from '../../../../shared/models/AdminModel/UserModel/UserSummaryDto';

@Injectable({
  providedIn: 'root'
})
export class AdminEvaluationFormService {

  private baseUrl = environment.apiBaseUrl;

  constructor(private httpClient: HttpClient) {}

  /**
   * Get all the form for a specific map
   *
   * @param finalMapId the id of the map you are interested in
   *
   * @return A list of all the forms
   */
  public getAllForm(finalMapId:number):Observable<AdminResponseEvaluationFormDto[]>{
    return this.httpClient.get<AdminResponseEvaluationFormDto[]>(
      `${this.baseUrl}${API_ENDPOINTS.ADMIN.EVALUATIONFORM.GETALLFORMFORAMAP}/${finalMapId}`,
      {withCredentials:true}
    );
  }

  /**
   * Delete a specific evaluation form
   *
   * @param evaluationFormId the id of the evaluation form to delete
   *
   * @return A success or error message
   */
  public deleteForm(evaluationFormId:number):Observable<MessageDto>{
    return this.httpClient.delete<MessageDto>(
      `${this.baseUrl}${API_ENDPOINTS.ADMIN.EVALUATIONFORM.DELETEFORM}/${evaluationFormId}`,
      {withCredentials:true}  
    );
  }

  public getEvaluators(finalMapId: number) : Observable<UserSummaryDto[]> {
    return this.httpClient.get<UserSummaryDto[]>(
      `${this.baseUrl}${API_ENDPOINTS.ADMIN.REPORT.GETEVALUATORS}/${finalMapId}`,
      {withCredentials:true}
    );
  }

}
