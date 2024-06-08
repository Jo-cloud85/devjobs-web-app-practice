import { Component, EventEmitter, OnDestroy, OnInit, Output, inject } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { JobService } from '../services/job.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Subject, Subscription } from 'rxjs';
import { JobSummary } from '../models/job.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrl: './search.component.css'
})
export class SearchComponent implements OnInit {

  private readonly formbuilder = inject(FormBuilder);
  private readonly jobSvc = inject(JobService);
  private readonly router = inject(Router);

  searchForm !: FormGroup;

  displayedJobs: JobSummary[] = [];

  fullTime: boolean = false;
  sortByRecent: boolean = false;

  ngOnInit(): void {
    this.searchForm = this.formbuilder.group({
      q: this.formbuilder.control<string>(''), 
      loc: this.formbuilder.control<string>(''),
      fullTime: this.formbuilder.control<boolean>(false),
      sortByRecent: this.formbuilder.control<boolean>(false),
    })
  }

  search() {
    // Method 1
    // const search: string = this.searchForm.get('q')?.value;
    // const searchLocation: string = this.searchForm.get('loc')?.value;
    // const searchFullTime = this.searchForm.get('fullTime')?.value || false;
    // const sortByRecent = this.searchForm.get('sortByRecent')?.value || false;

    // Method 2
    // const search: string = this.searchForm.value['q'];
    // const searchLocation: string = this.searchForm.value['loc'];
    // const searchFullTime = this.searchForm.value['fullTime'] || false;
    // const sortByRecent = this.searchForm.value['sortByRecent'] || false;

    // console.log(search, searchLocation, searchFullTime, sortByRecent);

    const queryParams = { ...this.searchForm.value };
    this.router.navigate(['/'], { queryParams });
  }
}
