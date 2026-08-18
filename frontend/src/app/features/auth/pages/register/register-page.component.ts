import {Component, inject, OnInit, signal} from '@angular/core';
import {LoginService} from '../../../../core/service/LoginService/loginService';
import {User} from '../../../../shared/models/UserModel/User';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import { CommonModule } from '@angular/common';
import {Router} from '@angular/router';
import {ButtonComponent} from '../../../../shared/components/button.component/button.component';
import {InputboxComponents} from '../../../../shared/components/inputbox.components/inputbox.components';
import {ErrorSuccessMessageComponent} from '../../../../shared/components/error-success-message.component/error-success-message.component';
import {TranslocoModule, TranslocoPipe} from '@jsverse/transloco';
import { TranslocoService } from '@jsverse/transloco';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-register',
  standalone: true,
    imports: [
        ReactiveFormsModule,
        CommonModule,
        ButtonComponent,
        InputboxComponents,
        ErrorSuccessMessageComponent,
        TranslocoModule,
        TranslocoPipe,
        FormsModule
    ],
  templateUrl: './register-page.component.html',
  styleUrl: './register-page.component.css',
})
export class RegisterPageComponent implements  OnInit {

  Users: User = {
    id: 0,
    username: "anonymous",
    firstName: "anonymous",
    lastName: "anonymous",
    password: "anonymous",
    email: "anonymous@email",
    role: "Admin"
  };

  registerError = signal(false);
  errorMessage = signal('');
  public showWindow:boolean = true;
  public Consented: boolean = false;

  /**
   * Init the form to add a new user/account to the database
   */
  formGroup!: FormGroup;

  private transloco = inject(TranslocoService);
  constructor(
    private loginService: LoginService,
    private router: Router) {}


  ngOnInit(): void {
    this.formGroup = new FormGroup({
      username: new FormControl('', [Validators.required, Validators.minLength(3)]),
      firstName: new FormControl('', [Validators.required, Validators.minLength(3)]),
      lastName: new FormControl('', [Validators.required, Validators.minLength(3)]),
      email: new FormControl('', [Validators.required, Validators.minLength(3), Validators.email]),
      password: new FormControl('', [Validators.required, Validators.minLength(8)]),
      confirmPassword : new FormControl('', [Validators.required, Validators.minLength(8)])
    });
  }


  /**
   * Redirection to login page
   */
  goToLogin(){
    this.router.navigate(['login'])
  }


  /**
   * @modifies : User with the new data that the user has recorded
   * */
  public register(): void {
    if (this.formGroup.invalid) {
      this.formGroup.markAllAsTouched();
      return;
    }

    const formValue = this.formGroup.value;

    const registerDto = {
      username: formValue.username,
      firstName: formValue.firstName,
      lastName: formValue.lastName,
      email: formValue.email,
      password: formValue.password
    };

    this.loginService.register(registerDto).subscribe({
      next: (response) => {
        console.log('Registration successful!', response);
        this.router.navigate(['/navigation']);
        this.loginService.setLoggedIn(true);
        this.registerError.set(false);
        this.errorMessage.set('');
      },
      error: (err) => {
        console.error('Error while creating user', err);
        this.registerError.set(true);
        const messageTranslate = this.transloco.translate('error.register-error');
        this.errorMessage.set(messageTranslate);
      }
    });
  }

  /**
   * @param : name of the form field
   * @return : boolean true if minlength is not respected
   * */
  nameLength(name: string): boolean {
    const control = this.formGroup.get(name);
    if (!control || !control.value) return false;

    return !!control.errors?.['minlength'];
  }

  /**
   * @param : name of the form field
   * @return : boolean true if mail is not respected
   * */
  mailLength(name: string): boolean {
    const control = this.formGroup.get(name);
    if (!control || !control.value) return false;

    return !!control.errors?.['email'];
  }
  /**
   * @param : name of the form field
   * @return : boolean true if minlength is not respected
   * */
  passwordLength(name: string): boolean {
    const control = this.formGroup.get(name);
    if (!control || !control.value) return false;

    return !!control.errors?.['minlength'];
  }

  /**
   * @return : boolean true if password and confirmPassword do not match
   * */
  passwordsMatch(): boolean {
    const password = this.formGroup.get("password")?.value
    const confirmPassword = this.formGroup.get("confirmPassword")?.value

    return !!confirmPassword && password !== confirmPassword;
  }
  public closeWindow(){
    this.showWindow=false;
  }

}
