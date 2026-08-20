import {CanActivateFn, Router} from '@angular/router';
import {inject, PLATFORM_ID} from '@angular/core';
import {isPlatformBrowser} from '@angular/common';
import {catchError, map, of, switchMap} from 'rxjs';
import {EvaluatorProfileService} from '../../../core/service/EvaluatorProfileService/EvaluatorProfileService';
import {UsersServices} from '../../../core/service/UserService/users-services';

/**
 * Guard that checks if the connected user has already completed their evaluator profile.
 * Admins and superadmins are never redirected, even without a profile.
 */
export const evaluatorProfileGuard: CanActivateFn = () => {
  const platformId = inject(PLATFORM_ID);
  const evaluatorProfileService = inject(EvaluatorProfileService);
  const usersServices = inject(UsersServices);
  const router = inject(Router);

  if (!isPlatformBrowser(platformId)) {
    return true;
  }

  return usersServices.getConnectedUser().pipe(
    switchMap(user =>
      evaluatorProfileService.hasProfile().pipe(
        map(hasProfile => {
          const isAdmin = user.role === 'ADMIN' || user.role === 'SUPERADMIN';
          if (!hasProfile && !isAdmin) {
            router.navigate(['evaluator-profile']);
          }
          return true;
        })
      )
    ),
    catchError(() => {
      router.navigate(['evaluator-profile']);
      return of(false);
    })
  );
};
