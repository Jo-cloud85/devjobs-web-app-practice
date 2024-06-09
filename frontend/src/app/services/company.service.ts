import { HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable, map, tap } from "rxjs";

@Injectable({providedIn: 'root'})
export class CompanyService {

    private readonly http = inject(HttpClient);

    getCompanyByName(companyName: String): Observable<any> {
      return this.http.get(`/api/company/${companyName}`)
        .pipe(
          map((response: any) => {
            return {
              company: response.company,
              address: response.address,
              coordinates: response.coordinates,
              reviews: response.reviews.map((review: any) => ({
                date: new Date(review.date),
                comments: review.comments,
                score: review.score
              })),
              jobPostings: response.jobPostings.map((job: any) => ({
                position: job.position,
                postedAt: job.postedAt,
                contract: job.contract,
              }))
            };
          }),
          // tap((result: any) => {
          //   console.log(">>> Result from CompanyService" + result)
          // })
        );
      }
}