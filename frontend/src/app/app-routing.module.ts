import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { JobDetailsComponent } from './components/job-details.component';
import { JobFormComponent } from './components/job-form.component';
import { AppliedSuccessComponent } from './components/applied-success.component';
import { MainComponent } from './components/main.component';
import { AppliedJobsListComponent } from './components/applied-jobs-list.component';
import { CompanyDetailsComponent } from './components/company-details.component';

const routes: Routes = [
  {path: '', component: MainComponent},
  {path: 'job/:id', component: JobDetailsComponent},
  {path: 'job/:id/apply', component: JobFormComponent},
  {path: 'job/:id/apply-success', component: AppliedSuccessComponent},  
  {path: 'appliedjobs', component: AppliedJobsListComponent},
  {path: 'company/:company-id', component: CompanyDetailsComponent},
  {path: '**', redirectTo: '/', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
