import { Component, OnInit } from '@angular/core';
import { AuthService } from './pages/services/auth.service';
import { SessionService } from './pages/services/session.service';
import { User } from './pages/models/user.models';
import { catchError, of } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {
  constructor(
    private authService: AuthService,
    private sessionService: SessionService
  ) {}
  public ngOnInit(): void {
    this.autoLog();
  }

  public autoLog(): void {
    this.authService
      .me()
      .pipe(
        catchError(() => {
          this.sessionService.logOut();
          return of(null);
        })
      )
      .subscribe((user: User | null) => {
        if (user) {
          this.sessionService.logIn(user);
        }
      });
  }
}
