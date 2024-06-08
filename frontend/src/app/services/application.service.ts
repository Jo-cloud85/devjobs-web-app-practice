import { HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";

@Injectable({providedIn: 'root'})
export class ApplicationService {

    private readonly http = inject(HttpClient);

    // If you are using proxy.config.json, then your url is relative i.e. '/api/jobs'
    // If you are using CORS, then your url should be full i.e. 'http://localhost:5050/api/jobs'

    submitJobApplication(formData: FormData): Observable<any> {
        return this.http.post('/api/submit', formData);
    }

    // retrieveJobApplication()
}