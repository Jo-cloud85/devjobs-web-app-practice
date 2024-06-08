import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { lessThanToday, mobileNumberValidator } from '../custom-validator';
import { ApplicationService } from '../services/application.service';
import { Subscription } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-job-form',
  templateUrl: './job-form.component.html',
  styleUrl: './job-form.component.css'
})
export class JobFormComponent implements OnInit, OnDestroy {

  private readonly fb = inject(FormBuilder);
  private readonly applSvc = inject(ApplicationService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  jobForm !: FormGroup;
  dueDateInPast !: boolean;
  private sub$ !: Subscription;

  ngOnInit(): void {
    this.jobForm = this.fb.group({
        name: this.fb.group({
            firstName: this.fb.control<string>('', [Validators.required, Validators.minLength(3)]),
            lastName: this.fb.control<string>('', [Validators.required, Validators.minLength(3)])
        }),
        email: this.fb.control<string>('', [Validators.required, Validators.email]),
        mobileNumber: this.fb.control<string>('', [Validators.required, mobileNumberValidator()]),
        position: this.fb.control<string>('', [Validators.required]),
        startDate: this.fb.control<string>('', [Validators.required, lessThanToday()]),
        feedback: this.fb.control<string>('', [Validators.required, Validators.maxLength(500)]),
        resume: this.fb.control<string>('', [Validators.required]),
        resumeFileSource: this.fb.control<string>('', [Validators.required]),
        otherDocs: this.fb.array([])
    });
  }

  createDoc(data: any): FormGroup {
    return this.fb.group({
      file: [data.file, Validators.required]
    });
  }

  onFileChange(event: any, controlName: string): void {
    const input = event.target.files;
    if (input.length > 0) {
      if (controlName === 'resume') {
          const file = input[0];
          this.jobForm.patchValue({ resumeFileSource: file });
      } else if (controlName === 'otherDocs') {
          const filesArray = this.jobForm.get(controlName) as FormArray;
          filesArray.clear(); // Clear previous files
          for (let i = 0; i < input.length; i++) {
            filesArray.push(this.createDoc({ file: input[i] }));
          }
      }
    }
  }

  submit(): void {
    const jobId = +this.activatedRoute.snapshot.params['id']; 
    if (this.jobForm.valid) {
      const formData = new FormData();
      const name = this.jobForm.get('name')?.value;
      formData.append('name', `${name.firstName} ${name.lastName}`);
      formData.append('email', this.jobForm.get('email')?.value);
      formData.append('mobileNumber', this.jobForm.get('mobileNumber')?.value);
      formData.append('position', this.jobForm.get('position')?.value);
      formData.append('startDate', this.jobForm.get('startDate')?.value); //startDate becomes string yyyy-mm-dd
      formData.append('feedback', this.jobForm.get('feedback')?.value);
      formData.append('resume', this.jobForm.get('resumeFileSource')?.value);

      const otherDocs = this.jobForm.get('otherDocs') as FormArray;
      otherDocs.controls.forEach((control, index) => {
        const file = control.value.file;
        if (file) {
          formData.append(`otherDocs`, file);
        }
      });

      formData.forEach((value, key) => {
        console.log(`${key}:`, value);
      });

      this.sub$ = this.applSvc.submitJobApplication(formData).subscribe({
        next: (result: any) => {
          console.log("Submitted result:", result);
          alert(`Job application: ${result}`);
          this.router.navigate(['/job', jobId, 'apply-success']);
        },
        error: (err: HttpErrorResponse) => {
          console.error("HTTP Error:", err);
          alert(`File upload failed: ${err.message}`);
        },
        complete: () => {
          this.jobForm.reset();
          console.log("Job application completed.");
        }
      });
    }
  }

  
  checkDueDate(): void {
		const startingDate = new Date(this.jobForm.value.startDate);
		const currentDate = new Date();
		this.dueDateInPast = startingDate < currentDate;
	}


  ngOnDestroy(): void {
      this.sub$.unsubscribe();
  }
}
