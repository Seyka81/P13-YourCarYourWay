export interface ChatSummary {
  id: number;
  title: string;
  messagesCount: number;
  status: 'OPEN' | 'CLOSE';
}

export interface CreateChatRequest {
  title: string;
}

export interface SendMessageRequest {
  sender: string;
  content: string;
}

export interface MessageDTO {
  id: number;
  sender: string;
  content: string;
  sentAt: string;
}
