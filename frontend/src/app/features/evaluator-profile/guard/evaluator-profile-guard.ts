import {CanActivateFn, Router} from '@angular/router';
import {inject, PLATFORM_ID} from '@angular/core';
import {isPlatformBrowser} from '@angular/common';
import {catchError, map, of} from 'rxjs';
import {EvaluatorProfileService} from '../../../core/service/EvaluatorProfileService/EvaluatorProfileService';

/**
 * Guard that checks if the connected user has already completed their evaluator profile.
 * If not, redirect to the evaluator-profile page instead of letting them access the route.
 */
export const evaluatorProfileGuard: CanActivateFn = () => {
  const platformId = inject(PLATFORM_ID);
  const evaluatorProfileService = inject(EvaluatorProfileService);
  const router = inject(Router);

  if (!isPlatformBrowser(platformId)) {
    return true;
  }

  return evaluatorProfileService.hasProfile().pipe(
    map(hasProfile => {
      if (!hasProfile) {
        router.navigate(['evaluator-profile']);
      }
      return hasProfile;
    }),
    catchError(() => {
      router.navigate(['evaluator-profile']);
      return of(false);
    })
  );
};
