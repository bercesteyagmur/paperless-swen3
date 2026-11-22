export interface TagResponse {
  id: number;
  name: string;
}

export interface DocumentResponse {
  id: number;
  originalFileName: string;
  fileType: string;
  uploadedAt: string;
  summary: string | null;
  tags: TagResponse[];
}

export interface DocumentSearchResponse {
  documentId: number;
  fileName: string;
}
