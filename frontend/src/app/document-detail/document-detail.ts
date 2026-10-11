import { Component, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
import { DocumentService } from '../shared/document.service';
import { DocumentResponse, TagResponse } from '../shared/document.model';

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
  tags = signal<TagResponse[]>([]);
  newName = signal('');
  tagName = signal('');
  errorMessage = signal('');

  constructor() {
    const id = this.service.selectedFileId();
    if (id != null) {
      this.loadFile(id);
    }
    this.loadTags();
  }

  loadFile(id: number): void {
    this.service.getDocument(id).subscribe({
      next: (fileFromBackend) => {
        this.file.set(fileFromBackend);
        this.newName.set(fileFromBackend.originalFileName);
      },
      error: () => {
        this.errorMessage.set('The file could not be loaded.');
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

  onInputTyped(event: Event, field: string): void {
    const input = event.target as HTMLInputElement;
    if (field == 'name') {
      this.newName.set(input.value);
    } else if (field == 'tag') {
      this.tagName.set(input.value);
    }
  }

  renameFile(): void {
    const file = this.file()!;
    const name = this.newName().trim();
    if (name == '') {
      this.errorMessage.set('The file name cannot be empty. Please write a name.');
    } else if (name.length > 100) {
      this.errorMessage.set('The file name cannot be longer than 100 characters.');
    } else {
      this.errorMessage.set('');
      this.service.renameDocument(file.id, name).subscribe({
        next: () => {
          this.router.navigate(['/files']);
        },
        error: (error: HttpErrorResponse) => {
          if (error.status === 409) {
            this.errorMessage.set(name + ' already exists.');
          } else {
            this.errorMessage.set('The file could not be renamed.');
          }
        },
      });
    }
  }

  onAddTagButtonClicked(): void {
    const file = this.file()!;
    const tag = this.tagName().trim();
    if (tag == '') {
      this.errorMessage.set('The tag tag cannot be empty. Please write a tag.');
    } else if (tag.length > 50) {
      this.errorMessage.set('The tag cannot be longer than 50 characters.');
    } else if (file.tags.some(fileTag => fileTag.name.toLowerCase() == tag.toLowerCase())) {
      this.errorMessage.set('This tag is already assigned to the file.');
    } else {
      this.errorMessage.set('');
      this.service.addTagToDocument(file.id, tag).subscribe({
        next: (updatedFileFromBackend) => {
          this.tagName.set('');
          this.file.set(updatedFileFromBackend);
          this.loadTags();
        },
        error: () => {
          this.errorMessage.set('The tag could not be added to the file.');
        },
      });
    }
  }

  onRemoveTagButtonClicked(tagId: number): void {
    const file = this.file();
    if (file == null) {
      return;
    }

    //maybe add confirmation before removing tag??
    //window.confirm()

    this.errorMessage.set('');
    this.service.removeTagFromDocument(file.id, tagId).subscribe({
      next: () => {
        this.loadFile(file.id);
      },
      error: () => {
        this.errorMessage.set('The tag could not be removed from the file.');
      },
    });
  }
}
