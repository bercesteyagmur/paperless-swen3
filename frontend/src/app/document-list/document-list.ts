import { Component, inject, signal } from '@angular/core';
import { DocumentService } from '../shared/document.service';
import { DocumentResponse } from '../shared/document.model';

@Component({
  selector: 'app-document-list',
  templateUrl: './document-list.html',
  styleUrl: './document-list.css',
})

export class DocumentList {
  private service = inject(DocumentService);

  documents = signal<DocumentResponse[]>([]);
  errorMessage = signal('');

  constructor() {
    this.loadDocuments();
  }

  loadDocuments(): void {
    this.service.getDocuments().subscribe({
      next: (documents) => {
        this.documents.set(documents);
      },
      error: () => {
        this.errorMessage.set('Could not load documents');
      },
    });
  }
}
