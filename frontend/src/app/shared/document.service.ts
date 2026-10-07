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

  selectedFileId = signal<number | null>(null);

  getDocuments(): Observable<DocumentResponse[]> {
    return this.http.get<DocumentResponse[]>(this.apiUrl);
  }

  getDocument(id: number): Observable<DocumentResponse> {
    return this.http.get<DocumentResponse>(`${this.apiUrl}/${id}`);
  }

  // source: https://medium.com/@muhebollah.diu/understanding-multipart-form-data-the-ultimate-guide-for-beginners-fd039c04553d
  // source: https://refine.dev/blog/how-to-multipart-upload/
  uploadDocument(file: File): Observable<DocumentResponse> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<DocumentResponse>(this.apiUrl, formData);
  }

  renameDocument(id: number, newFileName: string): Observable<DocumentResponse> {
    return this.http.patch<DocumentResponse>(`${this.apiUrl}/${id}`, { newFileName });
  }

  deleteDocument(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

}
