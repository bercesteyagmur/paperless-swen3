export interface DocumentRequest {
  originalFileName: string;
}

export interface DocumentResponse {
  id: number;
  originalFileName: string;
  fileType: string;
  uploadedAt: string;
}
