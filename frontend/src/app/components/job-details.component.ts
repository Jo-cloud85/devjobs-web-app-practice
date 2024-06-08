import { JobDetails } from '../models/job.model';
import { Component, inject } from '@angular/core';
import { JobService } from '../services/job.service';
import { Subscription } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-job-details',
  templateUrl: './job-details.component.html',
  styleUrl: './job-details.component.css'
})
export class JobDetailsComponent {

  private readonly jobSvc = inject(JobService);
  private readonly activatedRoute = inject(ActivatedRoute);

  sub$ !: Subscription;

  job !: JobDetails;

  id !: number;

  ngOnInit(): void {
    const jobId = +this.activatedRoute.snapshot.paramMap.get('id')!;
    this.jobSvc.getJob(jobId)
      .then(details => {
        this.job = details;
      })
      .catch(error => {
        console.error('Error fetching job details:', error);
      });
  }
}
