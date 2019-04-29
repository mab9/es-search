import {Inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Document} from "./document";
import {Observable} from "rxjs";
import {debounceTime, distinctUntilChanged, map, switchMap} from "rxjs/operators";

interface DocumentResponse {
  doc: Document
}

interface DocumentResponse {
  docs: Document[]
}

@Injectable()
export class DocumentService {

  private API_ENDPOINT: string = 'http://localhost:8080/api/v1';

  constructor(private http: HttpClient, @Inject('API_ENDPOINT') private apiEndpoint: String) {
  }

  getDocuments(): Observable<Array<Document>> {
    return this.http.get<DocumentResponse>(this.API_ENDPOINT + '/documents').pipe(map((data) => data.docs));
  }

  getDocument(id: string): Observable<Document> {
    return this.http.get<DocumentResponse>(`${this.API_ENDPOINT}/documents/${id}`).pipe(map((data) => data.doc));
  }

  search(term: string): Observable<Array<Document>> {
    let url = `${this.API_ENDPOINT}/documents/search?term=${term}`;
    //return this.http.get<DocumentResponse>(url).pipe(map((data) => data.docs));
    return null;
  }

  searchByQuery(query: string): Observable<Array<Document>> {
    let url = `${this.API_ENDPOINT}/documents/search/${query}`;
    return this.http.get<DocumentResponse>(url).pipe(map((data) => data.docs));
  }

  searchByQueryHighlighted(query: string): Observable<Array<Document>> {
    let url = `${this.API_ENDPOINT}/documents/search/highlighted/${query}`;
    return this.http.get<DocumentResponse>(url).pipe(map((data) => data.docs));
  }

}
