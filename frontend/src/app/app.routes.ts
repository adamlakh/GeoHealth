import { Routes } from '@angular/router';
import {ExpertHomePageComponent} from './features/home/pages/expert-home/expert-home-page.component';
import {MapComponent} from './features/map/map.component';
import {NavigationPageComponent} from './features/navigation/pages/navigation/navigation-page.component';
import { LoginPageComponent } from './features/auth/pages/login/login-page.component';
import { RegisterPageComponent } from './features/auth/pages/register/register-page.component';
import {UsersListPageComponent} from './features/admin/pages/users-list/users-list-page.component';
import { authGuard } from './features/auth/services/authguard/authguard-guard';
import {ProfilePageComponent} from './features/auth/pages/profile/profile-page.component';
import {ChangePasswordPageComponent} from './features/auth/pages/change-password/change-password-page.component';
import {adminGuard} from './features/admin/guard/admin-guard';
import {AboutComponent} from './features/about/pages/about/about/about.component';
import {EvaluatorProfile} from './features/evaluator-profile/evaluator-profile';
import {evaluatorProfileGuard} from './features/evaluator-profile/guard/evaluator-profile-guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },{
    path:'about',
    component: AboutComponent,
  },{
    path:'home',
    component: ExpertHomePageComponent,
  },{
    path: 'maps/:id',
    component: MapComponent,
    canActivate: [authGuard, evaluatorProfileGuard],
  },{
    path:'navigation',
    component: NavigationPageComponent,
    canActivate: [authGuard]
  },{
    path:'users-list',
    component: UsersListPageComponent,
    canActivate: [authGuard,adminGuard]
  },{
    path:'login',
    component: LoginPageComponent,
  },{
    path:'register',
    component: RegisterPageComponent,
  },{
    path:'profile',
    component: ProfilePageComponent,
    canActivate: [authGuard]
  },{
    path:'change-password',
    component: ChangePasswordPageComponent,
  },{
    path: 'evaluator-profile',
    component: EvaluatorProfile,
    canActivate: [authGuard],
  },{
    path: '**',
    redirectTo: 'home',
    pathMatch: 'full'
  }
];


