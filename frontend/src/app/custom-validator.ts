import { AbstractControl, ValidationErrors, ValidatorFn } from "@angular/forms";

export function mobileNumberValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;
    const valid = /^[89]\d{7}$/.test(value);

    return valid ? null : { mobileNumber: { value: control.value } };
  };
}
  
export function lessThanToday(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const inputDate = new Date(control.value);
    const today = new Date();

    if (inputDate <= today) {
      return { lessThanToday: { value: control.value } };
    }
    return null;
  };
}
  