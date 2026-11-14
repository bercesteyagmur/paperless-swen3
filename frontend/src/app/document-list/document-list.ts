import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { DocumentService } from '../shared/document.service';
import { DocumentResponse, TagResponse } from '../shared/document.model';

@Component({
  selector: 'app-document-list',
  imports: [DatePipe],
  templateUrl: './document-list.html',
  styleUrl: './document-list.css',
})

export class DocumentList {
  private service = inject(DocumentService);
  private router = inject(Router);

  allDocuments: DocumentResponse[] = [];
  documents = signal<DocumentResponse[]>([]);

  tags = signal<TagResponse[]>([]);
  selectedTag = signal<TagResponse | null>(null);

  searchQuery = signal('');
  searchActive = signal(false);

  errorMessage = signal('');

  constructor() {
    this.loadDocuments();
  }

  loadDocuments(): void {
    this.selectedTag.set(null);
    this.searchQuery.set('');
    this.searchActive.set(false);
    this.errorMessage.set('');
    this.service.getDocuments().subscribe({
      next: (documentsFromBackend) => {
        this.allDocuments = documentsFromBackend;
        this.documents.set(documentsFromBackend);

        const tags: TagResponse[] = [];
        for (const document of documentsFromBackend) {
          for (const tag of document.tags) {
            let alreadyAdded = false;
            for (const addedTag of tags) {
              if (addedTag.id == tag.id) {
                alreadyAdded = true;
              }
            }
            if (!alreadyAdded) {
              tags.push(tag);
            }
          }
        }
        this.tags.set(tags);
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
    this.router.navigate(['/files', id]);
  }

  onTagFilterClicked(clickedTag: TagResponse): void {
    const filtered: DocumentResponse[] = [];

    for (const document of this.allDocuments) {
      for (const documentTag of document.tags) {
        if (documentTag.id == clickedTag.id) {
          filtered.push(document);
        }
      }
    }

    this.documents.set(filtered);
    this.selectedTag.set(clickedTag);
  }

  onSearchInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.searchQuery.set(input.value);

    if (input.value.trim().length < 3) {
      this.documents.set(this.allDocuments);
      this.searchActive.set(false);
      this.errorMessage.set('');
      return;
    }

    this.searchDocuments();
  }

  searchDocuments(): void {
    const query = this.searchQuery().trim();

    if (query.length < 3) {
      this.documents.set(this.allDocuments);
      this.searchActive.set(false);
      return;
    }

    this.errorMessage.set('');
    this.selectedTag.set(null);

    this.service.searchDocuments(query).subscribe({
      next: (searchResults) => {
        const matchingDocuments: DocumentResponse[] = [];

        for (const result of searchResults) {
          for (const document of this.allDocuments) {
            if (document.id === result.documentId) {
              matchingDocuments.push(document);
            }
          }
        }

        this.documents.set(matchingDocuments);
        this.searchActive.set(true);
      },
      error: () => {
        this.errorMessage.set('Documents could not be searched.');
      },
    });
  }
}
