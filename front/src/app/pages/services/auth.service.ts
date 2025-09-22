import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RegisterRequest } from '../models/registerRequest.models';
import { AuthSuccess } from '../models/authSuccess.models';
import { LoginRequest } from '../models/loginRequest.models';
import { User } from '../models/user.models';
import { environment } from '../../../environments/environment';
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private pathService = `${environment.baseUrl}/api/auth`;

  constructor(private httpClient: HttpClient) {}

  public register(registerRequest: RegisterRequest): Observable<AuthSuccess> {
    return this.httpClient.post<AuthSuccess>(
      `${this.pathService}/register`,
      registerRequest
    );
  }

  public login(loginRequest: LoginRequest): Observable<AuthSuccess> {
    return this.httpClient.post<AuthSuccess>(
      `${this.pathService}/login`,
      loginRequest
    );
  }

  public me(): Observable<User> {
    return this.httpClient.get<User>(`${this.pathService}/me`);
  }

  editprofile(
    id: number,
    value: Partial<{
      email: string | null;
      username: string | null;
      password: string | null;
    }>
  ): Observable<User> {
    return this.httpClient.put<User>(`${this.pathService}/edit/${id}`, value);
  }
}
