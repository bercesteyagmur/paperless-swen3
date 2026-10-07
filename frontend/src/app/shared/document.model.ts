export interface DocumentRequest {
  newFileName: string;
}

export interface TagResponse {
  id: number;
  name: string;
}

export interface DocumentResponse {
  id: number;
  originalFileName: string;
  fileType: string;
  uploadedAt: string;
  tags: TagResponse[];
}
