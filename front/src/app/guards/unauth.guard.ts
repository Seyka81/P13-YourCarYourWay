import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { SessionService } from '../pages/services/session.service';
import { map, Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class UnauthGuard implements CanActivate {
  constructor(private router: Router, private sessionService: SessionService) {}

  public canActivate(): Observable<boolean> {
    return this.sessionService.$isLogged().pipe(
      map((isLogged) => {
        if (isLogged) {
          this.router.navigate(['homesupport']);
          return false;
        }
        return true;
      })
    );
  }
}
