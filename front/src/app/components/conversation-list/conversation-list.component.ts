// conversation-list.component.ts
import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { ChatSummary } from 'src/app/pages/models/tchat.models';
import { ChatService } from 'src/app/pages/services/chat.service';

import { Client, IMessage } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { environment } from 'src/environments/environment';
import { SessionService } from 'src/app/pages/services/session.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-conversation-list',
  templateUrl: './conversation-list.component.html',
  styleUrls: ['./conversation-list.component.scss'],
})
export class ConversationListComponent implements OnInit, OnDestroy {
  @Input() isAdmin = false;
  chats: ChatSummary[] = [];
  loading = true;

  selectedChatId?: number;
  selectedChatTitle?: string;

  private stomp?: Client;
  private connecting = false;

  constructor(
    private chatService: ChatService,
    private sessionService: SessionService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadTchats();
    this.initRealtime();
  }
  loadTchats(): void {
    this.chatService.getAllChats().subscribe({
      next: (data) => {
        this.chats = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }
  ngOnDestroy(): void {
    if (this.stomp?.active) this.stomp.deactivate();
  }

  newTitle = '';

  onCreateChat(): void {
    const title = this.newTitle.trim();
    if (!title) return;

    this.chatService.createChat({ title }).subscribe({
      next: () => {
        this.toastr.success('Tchat créé avec succès.');
        this.newTitle = '';
        this.loadTchats();
      },
      error: () => this.toastr.error('Impossible de créer le tchat.'),
    });
  }

  openChat(chatId: number): void {
    if (this.stomp?.active) this.stomp.deactivate();
    this.selectedChatId = chatId;
    this.selectedChatTitle = this.chats.find((c) => c.id === chatId)?.title;
  }
  cloturerChat(chatId: number) {
    this.chatService.cloturerChat(chatId).subscribe({
      next: () => this.toastr.success('Tchat clôturé avec succès.'),
      error: () => this.toastr.error('Impossible de clôturer le tchat.'),
    });
  }
  closeChat(): void {
    this.selectedChatId = undefined;
    this.selectedChatTitle = undefined;
    this.initRealtime();
    this.loadTchats();
  }

  private initRealtime() {
    if (this.selectedChatId != null) return;
    if (this.stomp?.active || this.connecting) return;
    this.connecting = true;

    const token = localStorage.getItem('token') || '';
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
      connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
    });

    this.stomp.onConnect = () => {
      this.stomp?.subscribe('/topic/chats', (msg: IMessage) => {
        const incoming = JSON.parse(msg.body) as ChatSummary;
        if (incoming.status === 'CLOSE') {
          this.chats = this.chats.filter((c) => c.id !== incoming.id);
          return;
        }
        const idx = this.chats.findIndex((c) => c.id === incoming.id);
        const current = this.chats[idx];
        const currCount = Number(current?.messagesCount ?? 0);
        const incCount = Number(incoming?.messagesCount ?? 0);
        if (incCount > currCount) {
          if (!this.chats.some((c) => c.id === incoming.id)) return;
          current.messagesCount = incoming.messagesCount;
          this.chats = [...this.chats];
        } else if (this.sessionService.getUser()?.role === 'SUPPORT') {
          this.chats = [incoming, ...this.chats];
        }
      });
      if (this.sessionService.getUser()?.role === 'SUPPORT') {
        this.stomp?.subscribe('/topic/chats/support', (msg: IMessage) => {
          const incoming = JSON.parse(msg.body) as ChatSummary;

          const idx = this.chats.findIndex((c) => c.id === incoming.id);
          const current = this.chats[idx];

          const currCount = Number(current?.messagesCount ?? 0);
          const incCount = Number(incoming?.messagesCount ?? 0);
          if (incoming.status === 'CLOSE') {
            this.chats = this.chats.filter((c) => c.id !== incoming.id);
          } else {
            if (incCount > currCount) {
              current.messagesCount = incoming.messagesCount;
              this.chats = [...this.chats];
            } else {
              this.chats = [incoming, ...this.chats];
            }
          }
        });
      }

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
}
