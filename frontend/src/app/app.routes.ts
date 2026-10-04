import { Routes } from '@angular/router';
import { DocumentUpload } from './document-upload/document-upload';
import { DocumentList } from './document-list/document-list';

export const routes: Routes = [
  { path: '', component: DocumentUpload },
  { path: 'files', component: DocumentList },
];
