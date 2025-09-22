import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { ContactPayload } from '../models/contact.models';

@Injectable({
  providedIn: 'root',
})
export class ContactService {
  private pathService = `${environment.baseUrl}/api`;

  constructor(private http: HttpClient) {}

  send(payload: ContactPayload): Observable<void> {
    return this.http.post<void>(`${this.pathService}/support/contact`, payload);
  }
}
