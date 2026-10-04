import { Component, inject } from '@angular/core';
import { DocumentService } from '../shared/document.service';

@Component({
  selector: 'app-document-list',
  templateUrl: './document-list.html',
})
export class DocumentList {
  service = inject(DocumentService);


}
