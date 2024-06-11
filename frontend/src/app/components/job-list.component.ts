import { Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges, inject } from '@angular/core';
import { JobDetails, JobSummary } from '../models/job.model';
import { JobService } from '../services/job.service';
import { Subscription, Observable } from 'rxjs';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-job-list',
  templateUrl: './job-list.component.html',
  styleUrl: './job-list.component.css'
})
export class JobListComponent implements OnInit, OnChanges, OnDestroy {

  private readonly jobSvc = inject(JobService);

  @Input() searchParams !: { q: string, loc: string, fullTime: boolean, sortByRecent: boolean };

  displayedJobs: JobSummary[] = [];
  allJobs: JobSummary[] = []; // To store all the jobs

  sub$ !: Subscription;

  initialDisplayLimit: number = 6; // Initial number of jobs to display
  displayLimit: number = this.initialDisplayLimit; // Current display limit

  ngOnInit(): void {
    this.loadJobs();
  }

  loadJobs(): void {
    this.sub$ = this.jobSvc.getJobsList(
      this.searchParams?.q || '',
      this.searchParams?.loc || '',
      this.searchParams?.fullTime?.toString() || 'false',
      this.searchParams?.sortByRecent?.toString() || 'false'
    ).subscribe({
        next: (result: any) => {
          this.allJobs = result;
          this.updateDisplayedJobs();
        },
        error: (error: HttpErrorResponse) => console.error('Error fetching jobs:', error),
        complete: () => console.log("Load jobs completed!")
    });
  }

  loadMore(): void {
    this.displayLimit = this.allJobs.length - 1; // Increase the display limit
    this.updateDisplayedJobs();
  }

  loadLess(): void {
    this.displayLimit = this.initialDisplayLimit;
    this.updateDisplayedJobs();
  }

  updateDisplayedJobs(): void {
    this.displayedJobs = this.allJobs.slice(0, this.displayLimit);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['searchParams']) {
      this.loadJobs();
    }
  }

  ngOnDestroy(): void {
    this.sub$.unsubscribe();
  }
}
