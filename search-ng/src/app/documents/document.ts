export interface Document {
  id: number | string;
  archivespaceId: number | string;
  documentName: string;
  uploadDate: number;
  signDate: number;
  documentState?: string;
  documentContent: string;
  highlights: string[];
  documentId: number | string;
}
