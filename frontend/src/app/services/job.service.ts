import { HttpClient, HttpParams } from '@angular/common/http';
import { JobDetails, JobSummary } from '../models/job.model';
import { Injectable, inject } from "@angular/core";
import { Observable, firstValueFrom, map, tap } from 'rxjs';

@Injectable({providedIn: 'root'})
export class JobService {

    private readonly httpClient = inject(HttpClient);

    jobsList : JobSummary[] = [];
    
    job !: JobDetails;

    getJobsList(search: string, searchLocation: string, fullTime: string, sortByRecent: string): Observable<JobSummary[]> {
        const query = new HttpParams()
            .set('q', search)
            .set('loc', searchLocation)
            .set('fullTime', fullTime)
            .set('sortByRecent', sortByRecent)

        // If you are using proxy.config.json, then your url is relative i.e. '/api/jobs'
        // If you are using CORS, then your url should be full i.e. 'http://localhost:5050/api/jobs'

        return this.httpClient.get<JobSummary[]>('/api/jobs', { params: query })
            .pipe(
                map((result: any) => (result as any[])
                    .map (val => {
                        return {
                            id: val['id'],
                            logo: val['logo'],
                            logoBackground: val['logoBackground'],
                            postedAt: val['postedAt'],
                            contract: val['contract'],
                            position: val['position'],
                            company: val['company'],
                            location: val['location']
                        } as JobSummary })
                ),
                tap(
                    result => {
                        // console.info(">>> in tap: ", result)
                        // this.onJobsList.next(result)
                    }
                )
            )
    }


    getJob(id: number): Promise<JobDetails> {
        return firstValueFrom(
          this.httpClient.get<JobDetails>(`/api/job/${id}`)
            .pipe(
              map(val => ({
                id: val.id,
                company: val.company,
                logo: val.logo,
                logoBackground: val.logoBackground,
                position: val.position,
                postedAt: val.postedAt,
                contract: val.contract,
                location: val.location,
                website: val.website,
                apply: val.apply,
                description: val.description,
                requirements: val.requirements,
                role: val.role
              })),
              tap(result => {
                // console.info(">>> in tap: ", result);
                // this.onJob.next(result);
              })
            )
        );
      }
}