import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { DocumentService } from '../shared/document.service';
import { DocumentResponse, TagResponse } from '../shared/document.model';

@Component({
  selector: 'app-document-list',
  templateUrl: './document-list.html',
  styleUrl: './document-list.css',
})

export class DocumentList {
  private service = inject(DocumentService);
  private router = inject(Router);

  documents = signal<DocumentResponse[]>([]);

  tags = signal<TagResponse[]>([]);
  selectedTag = signal<TagResponse | null>(null);

  errorMessage = signal('');

  constructor() {
    this.loadDocuments();
    this.loadTags();
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

  loadTags(): void {
    this.service.getTags().subscribe({
      next: (tagsFromBackend) => {
        this.tags.set(tagsFromBackend);
      },
      error: () => {
        this.errorMessage.set('Tags could not be loaded.');
      },
    });
  }

  onDeleteButtonClicked(document: DocumentResponse): void {
    this.service.deleteDocument(document.id).subscribe({
      next: () => {
        this.showAllDocuments();
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

  filterDocumentsByTag(tag: TagResponse): void {
    this.errorMessage.set('');
    this.service.getDocumentsForTag(tag.id).subscribe({
      next: (documentsFromBackend) => {
        this.documents.set(documentsFromBackend);
        this.selectedTag.set(tag);
      },
      error: () => {
        this.errorMessage.set('Files for this tag could not be loaded.');
      },
    });
  }

  showAllDocuments(): void {
    this.selectedTag.set(null);
    this.errorMessage.set('');
    this.loadDocuments();
  }
}
