import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { ContactPayload } from 'src/app/pages/models/contact.models';
import { ContactService } from 'src/app/pages/services/contact.service';

@Component({
  selector: 'app-contact-form',
  templateUrl: './contact-form.component.html',
  styleUrls: ['./contact-form.component.scss'],
})
export class ContactFormComponent {
  loading = false;

  formGroup = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]],
    subject: ['', [Validators.required, Validators.maxLength(120)]],
    message: ['', [Validators.required, Validators.minLength(10)]],
    website: [''], // honeypot anti-spam (doit rester vide)
  });

  constructor(
    private fb: FormBuilder,
    private contactService: ContactService,
    private toastr: ToastrService
  ) {}

  get name() {
    return this.formGroup.get('name');
  }
  get email() {
    return this.formGroup.get('email');
  }
  get subject() {
    return this.formGroup.get('subject');
  }
  get message() {
    return this.formGroup.get('message');
  }

  onSubmit(): void {
    if (this.formGroup.invalid) {
      this.formGroup.markAllAsTouched();
      return;
    }
    // Si le honeypot est rempli → on ignore (probable bot)
    if (this.formGroup.value.website) {
      return;
    }

    const payload = {
      name: this.formGroup.value.name!,
      email: this.formGroup.value.email!,
      subject: this.formGroup.value.subject!,
      message: this.formGroup.value.message!,
    } as ContactPayload;

    this.loading = true;
    this.formGroup.disable();

    this.contactService.send(payload).subscribe({
      next: () => {
        this.toastr.success('Message envoyé avec succès !');
        this.formGroup.reset();
      },
      error: (err: unknown) => {
        console.error(err);
        setTimeout(() => {
          this.loading = false;
          this.toastr.error("Échec de l'envoi. Réessayez plus tard.");
        }, 1000);
      },
      complete: () => {
        this.loading = false;
        this.formGroup.enable();
      },
    });
  }
}
