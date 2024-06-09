import { Component, OnInit, inject } from '@angular/core';
import { ApplicationService } from '../services/application.service';
import { ApplicationSummary } from '../models/application.model';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-applied-jobs-list',
  templateUrl: './applied-jobs-list.component.html',
  styleUrl: './applied-jobs-list.component.css'
})
export class AppliedJobsListComponent implements OnInit {

  private readonly applSvc = inject(ApplicationService);

  resultP$!: Promise<ApplicationSummary[]| undefined>

  ngOnInit(): void {
    this.resultP$ = this.applSvc.getAllJobApplications();
  }
}
