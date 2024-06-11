import { HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable, firstValueFrom, map } from "rxjs";
import { ApplicationSummary } from "../models/application.model";

@Injectable({providedIn: 'root'})
export class ApplicationService {

    private readonly http = inject(HttpClient);

    // If you are using proxy.config.json, then your url is relative i.e. '/api/jobs'
    // If you are using CORS, then your url should be full i.e. 'http://localhost:5050/api/jobs'

    submitJobApplication(formData: FormData): Observable<any> {
      return this.http.post('/api/submit', formData);
    }

    getAllJobApplications(): Promise<ApplicationSummary[]> {
      return firstValueFrom(this.http.get<any[]>('/api/applications'))
        .then((results: any[]) => {
          return results.map(result => ({
            applicationId: result.applicationId,
            id: result.id,
            company: result.company,
            position: result.position,
            startDate: new Date(result.startDate),
          } as ApplicationSummary));
        });
    }

    getJobApplicationById(jobId: String): Observable<ApplicationSummary> {
      return this.http.get<ApplicationSummary>(`/api/application/${jobId}`)
        .pipe (
          map(val => ({
            applicationId: val.applicationId,
            id: val.id,
            company: val.company,
            position: val.position,
            startDate: new Date(val.startDate),
          }))
        )
    }
}