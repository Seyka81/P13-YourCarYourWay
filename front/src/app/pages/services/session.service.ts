import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { User } from '../models/user.models';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class SessionService {
  private isLogged = false;
  private user: User | undefined;

  private isLoggedSubject = new BehaviorSubject<boolean>(this.isLogged);

  constructor(private router: Router) {
    const token = localStorage.getItem('token');
    if (token) {
      this.isLogged = true;
    }
    this.next();
  }

  public $isLogged(): Observable<boolean> {
    return this.isLoggedSubject.asObservable();
  }

  public logIn(user: User): void {
    this.user = user;
    this.isLogged = true;
    this.next();
  }

  public logOut(): void {
    localStorage.removeItem('token');
    this.user = undefined;
    this.isLogged = false;
    this.next();
    this.router.navigate(['home']);
  }

  private next(): void {
    this.isLoggedSubject.next(this.isLogged);
  }
  public getUser(): User | undefined {
    return this.user;
  }
}
