export interface User {
  id: number;
  name: string;
  email: string;
  role: 'USER' | 'SUPPORT' | '';
  created_at: Date;
  updated_at: Date;
}
