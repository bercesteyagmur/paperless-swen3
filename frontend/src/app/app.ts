import { Component } from '@angular/core';
import { DocumentUpload } from './document-upload/document-upload';
import { DocumentList } from './document-list/document-list';

@Component({
  selector: 'app-root',
  imports: [DocumentUpload, DocumentList],
  templateUrl: './app.html',
})
export class App {}
