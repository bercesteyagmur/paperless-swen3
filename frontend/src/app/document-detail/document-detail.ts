import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { DocumentService } from '../shared/document.service';
import { DocumentResponse } from '../shared/document.model';

@Component({
  selector: 'app-document-detail',
  imports: [RouterLink],
  templateUrl: './document-detail.html',
  styleUrl: './document-detail.css',
})

export class DocumentDetail {
  private service = inject(DocumentService);
  private router = inject(Router);

  file = signal<DocumentResponse | null>(null);
  newName = signal('');
  errorMessage = signal('');

  constructor() {
    const id = this.service.selectedFileId();
    if (id != null) {
      this.loadFile(id);
    }
  }

  loadFile(id: number): void {
    this.service.getDocument(id).subscribe({
      next: (file) => {
        this.file.set(file);
        this.newName.set(file.originalFileName);
      },
      error: () => {
        this.errorMessage.set('The file could not be loaded.');
      },
    });
  }

  onNewNameTyped(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.newName.set(input.value);
  }

  renameFile(): void {
    const file = this.file();
    const name = this.newName().trim();
    if (file == null) {
      this.errorMessage.set('There is no file to rename.');
    } else if (name === '') {
      this.errorMessage.set('The file name cannot be empty. Please write a name.');
    } else {
      this.errorMessage.set('');
      this.service.renameDocument(file.id, name).subscribe({
        next: () => {
          this.router.navigate(['/files']);
        },
        error: () => {
          this.errorMessage.set('The file could not be renamed.');
        },
      });
    }
  }
}
