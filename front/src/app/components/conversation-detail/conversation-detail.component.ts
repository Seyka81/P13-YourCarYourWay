import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { ChatService } from 'src/app/pages/services/chat.service';
import {
  ChatSummary,
  MessageDTO,
  SendMessageRequest,
} from 'src/app/pages/models/tchat.models';

import { Client, IMessage } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { SessionService } from 'src/app/pages/services/session.service';
import { environment } from 'src/environments/environment';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-conversation-detail',
  templateUrl: './conversation-detail.component.html',
  styleUrls: ['./conversation-detail.component.scss'],
})
export class ConversationDetailComponent implements OnInit, OnDestroy {
  @Input() chatId!: number;
  @Input() chatTitle?: string;
  @Output() back = new EventEmitter<void>();

  messages: MessageDTO[] = [];
  loading = true;

  draft = '';
  sending = false;

  private stomp?: Client;
  private connecting = false;

  constructor(
    private chatService: ChatService,
    private sessionService: SessionService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadMessages();
    if (this.stomp?.active || this.connecting) return;
    this.connecting = true;

    const token = localStorage.getItem('token');
    const base = `${environment.baseUrl}/ws`;
    const wsUrl = token
      ? `${base}?access_token=${encodeURIComponent(token)}`
      : base;

    const sock = new SockJS(wsUrl, null, {
      transports: ['xhr-streaming'],
    });

    this.stomp = new Client({
      webSocketFactory: () => sock,
      reconnectDelay: 8000,
      heartbeatIncoming: 0,
      heartbeatOutgoing: 20000,
      connectHeaders: { Authorization: `Bearer ${token}` },
    });

    this.stomp.onConnect = () => {
      this.stomp?.subscribe(`/topic/chats/${this.chatId}`, (f: IMessage) => {
        const b = JSON.parse(f.body) as
          | { sender: string; content: string }
          | ChatSummary;
        if ('sender' in b && b.sender === this.sessionService.getUser()?.name)
          return;
        if ('status' in b && b.status === 'CLOSE') {
          this.goBack();
          return;
        }
        if ('content' in b && 'sender' in b && b.content) {
          this.messages = [
            ...this.messages,
            {
              id: Date.now(),
              sender: b.sender,
              content: b.content,
              sentAt: new Date().toISOString(),
            },
          ];
        }
        setTimeout(() => this.scrollToBottom(), 0);
      });
      this.connecting = false;
    };
    this.stomp.onWebSocketClose = () => {
      this.connecting = false;
    };
    this.stomp.onStompError = () => {
      this.connecting = false;
    };

    this.stomp.activate();
  }

  ngOnDestroy(): void {
    if (this.stomp?.active) this.stomp.deactivate();
    this.stomp = undefined;
    this.connecting = false;
  }

  loadMessages() {
    this.loading = true;
    this.chatService.getMessages(this.chatId).subscribe({
      next: (res: MessageDTO[]) => {
        this.messages = res;
        this.loading = false;
        setTimeout(() => this.scrollToBottom(), 0);
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  goBack() {
    this.back.emit();
  }

  send() {
    const text = this.draft?.trim();
    if (!text || this.sending) return;

    const payload: SendMessageRequest = {
      sender: this.sessionService.getUser()?.name ?? '',
      content: text,
    };
    const tempId = Date.now();
    const optimistic: MessageDTO = {
      id: tempId,
      sender: payload.sender,
      content: payload.content,
      sentAt: new Date().toISOString(),
    };
    this.messages = [...this.messages, optimistic];
    this.sending = true;

    this.chatService.sendMessage(this.chatId, payload).subscribe({
      next: (saved) => {
        this.messages = this.messages.map((m) => (m.id === tempId ? saved : m));
        this.draft = '';
        this.sending = false;
        setTimeout(() => this.scrollToBottom(), 0);
      },
      error: () => {
        this.messages = this.messages.filter((m) => m.id !== tempId);
        this.sending = false;
        this.toastr.error('Envoi impossible.');
      },
    });
  }

  onKeyDown(e: KeyboardEvent) {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      this.send();
    }
  }

  private scrollToBottom() {
    const el = document.querySelector('.messages');
    if (el) (el as HTMLElement).scrollTop = el.scrollHeight;
  }
}
