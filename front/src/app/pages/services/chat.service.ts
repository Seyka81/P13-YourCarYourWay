import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  ChatSummary,
  CreateChatRequest,
  MessageDTO,
  SendMessageRequest,
} from '../models/tchat.models';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class ChatService {
  cloturerChat(chatId: number) {
    return this.http.patch(`${this.baseUrl}/chats/${chatId}/status`, {
      status: 'CLOSE',
    });
  }
  private baseUrl = `${environment.baseUrl}/api/support`;

  constructor(private http: HttpClient) {}

  getAllChats(): Observable<ChatSummary[]> {
    return this.http.get<ChatSummary[]>(`${this.baseUrl}/chats`);
  }

  createChat(body: CreateChatRequest): Observable<ChatSummary> {
    return this.http.post<ChatSummary>(`${this.baseUrl}/chats`, body);
  }

  getMessages(chatId: number): Observable<MessageDTO[]> {
    return this.http.get<MessageDTO[]>(
      `${this.baseUrl}/chats/${chatId}/messages`
    );
  }

  sendMessage(
    chatId: number,
    body: SendMessageRequest
  ): Observable<MessageDTO> {
    return this.http.post<MessageDTO>(
      `${this.baseUrl}/chats/${chatId}/messages`,
      body
    );
  }
}
