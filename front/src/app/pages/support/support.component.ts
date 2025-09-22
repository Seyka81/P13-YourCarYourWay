import { Component } from '@angular/core';
import { SessionService } from '../services/session.service';

@Component({
  selector: 'app-support',
  templateUrl: './support.component.html',
  styleUrls: ['./support.component.scss'],
})
export class SupportComponent {
  constructor(private sessionService: SessionService) {}

  get username(): string {
    return this.sessionService.getUser()?.name ?? '';
  }
}
