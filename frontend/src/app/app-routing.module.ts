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
  {path: 'job/:id/:company', component: JobDetailsComponent},
  {path: 'job/:id/:company/apply', component: JobFormComponent},
  {path: 'job/:id/:company/apply-success', component: AppliedSuccessComponent},  
  {path: 'appliedjobs', component: AppliedJobsListComponent},
  {path: 'company/:id/:company', component: CompanyDetailsComponent},
  {path: '**', redirectTo: '/', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule { }
