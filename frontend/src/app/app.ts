import { Component } from '@angular/core';
import { DocumentUpload } from './document-upload/document-upload';

@Component({
  selector: 'app-root',
  imports: [DocumentUpload],
  templateUrl: './app.html',
})
export class App {}
