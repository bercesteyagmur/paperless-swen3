import { Routes } from '@angular/router';
import { DocumentUpload } from './document-upload/document-upload';
import { DocumentList } from './document-list/document-list';
import { DocumentDetail } from './document-detail/document-detail';

export const routes: Routes = [
  { path: '', component: DocumentList },
  { path: 'upload', component: DocumentUpload },
  { path: 'files/:id', component: DocumentDetail },
];
