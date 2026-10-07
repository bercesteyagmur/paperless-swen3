import { Routes } from '@angular/router';
import { DocumentUpload } from './document-upload/document-upload';
import { DocumentList } from './document-list/document-list';
import { DocumentDetail } from './document-detail/document-detail';

export const routes: Routes = [
  { path: '', component: DocumentUpload },
  { path: 'files', component: DocumentList },
  { path: 'files/detail', component: DocumentDetail },
];
