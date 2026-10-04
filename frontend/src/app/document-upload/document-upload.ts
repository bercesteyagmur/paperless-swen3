import { Component, inject, signal } from '@angular/core';
import { DocumentService } from '../shared/document.service';

@Component({
  selector: 'app-document-upload',
  templateUrl: './document-upload.html',
  styleUrl: './document-upload.css',
})

export class DocumentUpload {
  private service = inject(DocumentService);

  uploadedFile = signal<File | null>(null);

  errorMessage = signal('');
  successMessage = signal('');

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const files = input.files;
    if (files != null) {
      this.uploadedFile.set(files[0]);
    }
  }

  uploadFile(): void {
    const file = this.uploadedFile();
    this.successMessage.set('');
    if (file == null) {
      this.errorMessage.set('Please upload a file');
    } else if (!file.name.toLowerCase().endsWith('.pdf')) {
      this.errorMessage.set('Please upload a pdf');
    } else {
      this.errorMessage.set('');
      this.service.uploadDocument(file);
      this.successMessage.set(file.name + ' is uploaded');
    }
  }
}
