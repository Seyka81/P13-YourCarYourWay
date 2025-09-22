import { Component } from '@angular/core';
import { SessionService } from '../services/session.service';

@Component({
  selector: 'app-home-support',
  templateUrl: './home-support.component.html',
  styleUrls: ['./home-support.component.scss'],
})
export class HomeSupportComponent {
  constructor(private sessionService: SessionService) {}

  get username(): string {
    return this.sessionService.getUser()?.name ?? '';
  }
}
