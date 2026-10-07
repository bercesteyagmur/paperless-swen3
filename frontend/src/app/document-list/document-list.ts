import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { DocumentService } from '../shared/document.service';
import { DocumentResponse } from '../shared/document.model';

@Component({
  selector: 'app-document-list',
  templateUrl: './document-list.html',
  styleUrl: './document-list.css',
})

export class DocumentList {
  private service = inject(DocumentService);
  private router = inject(Router);

  documents = signal<DocumentResponse[]>([]);
  errorMessage = signal('');

  documentToDelete = signal<DocumentResponse | null>(null);

  constructor() {
    this.loadDocuments();
  }

  loadDocuments(): void {
    this.service.getDocuments().subscribe({
      next: (documents) => {
        this.documents.set(documents);
      },
      error: () => {
        this.errorMessage.set('Files could not be loaded.');
      },
    });
  }

  onDeleteButtonClicked(document: DocumentResponse): void {
    this.documentToDelete.set(document);
    this.deleteDocument();
  }

  deleteDocument(): void {
    const document = this.documentToDelete();
    if (document == null) {
      return;
    }
    this.service.deleteDocument(document.id).subscribe({
      next: () => {
        this.documentToDelete.set(null);
        this.loadDocuments();
      },
      error: () => {
        this.documentToDelete.set(null);
        this.errorMessage.set('The file ' + document.originalFileName + ' could not be deleted.');
      },
    });
  }

  onEditButtonClicked(id: number): void {
    this.service.selectedFileId.set(id);
    this.router.navigate(['/files/detail']);
  }
}
