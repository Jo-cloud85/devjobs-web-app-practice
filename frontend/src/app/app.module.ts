import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SearchComponent } from './components/search.component';
import { JobListComponent } from './components/job-list.component';
import { JobDetailsComponent } from './components/job-details.component';
import { JobFormComponent } from './components/job-form.component';
import { AppliedSuccessComponent } from './components/applied-success.component';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { MainComponent } from './components/main.component';
import { CompanyDetailsComponent } from './components/company-details.component';
import { AppliedJobsListComponent } from './components/applied-jobs-list.component';

@NgModule({
  declarations: [
    AppComponent,
    SearchComponent,
    JobListComponent,
    JobDetailsComponent,
    JobFormComponent,
    AppliedSuccessComponent,
    MainComponent,
    CompanyDetailsComponent,
    AppliedJobsListComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
