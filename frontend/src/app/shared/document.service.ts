import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DocumentResponse } from './document.model';

@Injectable({
  providedIn: 'root'
})

export class DocumentService {
  private apiUrl = '/api/files';

  private http = inject(HttpClient);

  private _documents = signal<DocumentResponse[]>([]);
  readonly documents = this._documents.asReadonly();

  loadDocuments(): void {
    const request: Observable<DocumentResponse[]> = this.http.get<DocumentResponse[]>(this.apiUrl);
    request.subscribe((data) => {this._documents.set(data);
    });
  }

  uploadDocument(file: File): void {
    const formData = new FormData();
    formData.append('file', file);
    const request: Observable<DocumentResponse> = this.http.post<DocumentResponse>(this.apiUrl, formData);
    request.subscribe((uploadedDocument) => {
      this._documents.update((documents) => [...documents, uploadedDocument]);
    });
  }
}
