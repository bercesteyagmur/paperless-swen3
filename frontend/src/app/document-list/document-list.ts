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

  constructor() {
    this.loadDocuments();
  }

  loadDocuments(): void {
    this.service.getDocuments().subscribe({
      next: (documentsFromBackend) => {
        this.documents.set(documentsFromBackend);
      },
      error: () => {
        this.errorMessage.set('Files could not be loaded.');
      },
    });
  }

  onDeleteButtonClicked(document: DocumentResponse): void {
    this.service.deleteDocument(document.id).subscribe({
      next: () => {
        this.loadDocuments();
      },
      error: () => {
        this.errorMessage.set('The file ' + document.originalFileName + ' could not be deleted.');
      },
    });
  }

  onEditButtonClicked(id: number): void {
    this.service.setSelectedFileId(id);
    this.router.navigate(['/files/detail']);
  }
}
