import { Component, OnInit } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { FormBuilder, Validators } from '@angular/forms';
import { User } from '../models/user.models';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss'],
})
export class UserComponent implements OnInit {
  form = this.formBuilder.group({
    email: ['', [Validators.required, Validators.email]],
    username: ['', [Validators.required, Validators.min(3)]],
    password: [
      '',
      [
        Validators.required,
        Validators.minLength(3),
        Validators.pattern(/^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\W_]).+$/),
      ],
    ],
  });
  user: User = {
    id: 0,
    name: '',
    email: '',
    role: '',
    created_at: new Date(),
    updated_at: new Date(),
  };

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.authService.me().subscribe((user) => {
      this.user = user;
      this.form.patchValue({
        email: user.email,
        username: user.name,
      });
    });
  }
  submit() {
    this.authService.editprofile(this.user.id, this.form.value).subscribe({
      next: (user) => {
        this.user = user;
        this.form.get('password')?.reset();
        this.toastr.success('Profile updated successfully');
      },
      error: () => {
        this.toastr.error('Failed to update profile');
      },
    });
  }
}
