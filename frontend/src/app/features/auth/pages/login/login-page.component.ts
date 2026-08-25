import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {LoginService} from '../../../../core/service/LoginService/loginService';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import {ButtonComponent} from '../../../../shared/components/button.component/button.component';
import {InputboxComponents} from '../../../../shared/components/inputbox.components/inputbox.components';
import {ErrorSuccessMessageComponent} from '../../../../shared/components/error-success-message.component/error-success-message.component';
import { signal } from '@angular/core';
import {TranslocoModule, TranslocoPipe} from '@jsverse/transloco';
import { inject } from '@angular/core';
import { TranslocoService } from '@jsverse/transloco';



@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CommonModule,
    ButtonComponent,
    InputboxComponents,
    ErrorSuccessMessageComponent,
    TranslocoModule,
    TranslocoPipe,
  ],
  templateUrl: './login-page.component.html',
  styleUrl: './login-page.component.css',
})
export class LoginPageComponent implements OnInit {

  formGroup!: FormGroup;
  loginError = signal(false);
  errorMessage = signal('');

  private transloco = inject(TranslocoService);
  constructor(
    private loginService: LoginService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.formGroup = new FormGroup({
      username: new FormControl('', [Validators.required]),
      password: new FormControl('', [Validators.required])
    });
  }

  /**
   * Request login to the backend with formGroup information
   */
  public login(): void {
    if (this.formGroup.invalid) {
      this.formGroup.markAllAsTouched();
      return;
    }

    this.loginService.login(this.formGroup.value).subscribe({
      next: () => {
        this.router.navigate(['/navigation']);
        this.loginService.setLoggedIn(true);
        this.loginError.set(false);
        this.errorMessage.set('');

      },
      error: () => {
        // Display error message
        this.loginError.set(true);
        const messageTranslate = this.transloco.translate('error.login-error');
        this.errorMessage.set(messageTranslate);

      }
    });
  }

  /**
   * Redirection to register page
   */
  goToRegister(){
    this.router.navigate(['register'])
  }




}
