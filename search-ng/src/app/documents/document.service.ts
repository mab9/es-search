import {Inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Document} from "./document";
import {Observable} from "rxjs";
import {debounceTime, distinctUntilChanged, map, switchMap} from "rxjs/operators";

interface DocumentResponse {
  item: Document
}

interface DocumentResponse {
  items: Document[]
}

@Injectable()
export class DocumentService {

  private API_ENDPOINT: string = 'http://localhost:8080/api/v1';

  constructor(private http: HttpClient, @Inject('API_ENDPOINT') private apiEndpoint: String) {
  }

  getDocuments(): Observable<Array<Document>> {
    return this.http.get<DocumentResponse>(this.API_ENDPOINT + '/documents').pipe(map((data) => data.items));
  }

  getDocument(id: string): Observable<Document> {
    return this.http.get<DocumentResponse>(`${this.API_ENDPOINT}/documents/${id}`).pipe(map((data) => data.item));
  }

  search(term: string): Observable<Array<Document>> {
    let url = `${this.API_ENDPOINT}/documents/search?term=${term}`;
    return this.http.get<DocumentResponse>(url).pipe(map((data) => data.items));
  }

  rawSearch(terms: Observable<string>, debounceMs = 400) : Observable<Array<Document>> {
    return terms.pipe(
      debounceTime(debounceMs),
      distinctUntilChanged(),
      switchMap(x => this.search(x))
    );
  }

  updateDocument(contact: Document): Observable<Document> {
    let url = `${this.API_ENDPOINT}/documents/${contact.id}`;
    return this.http.put<DocumentResponse>(url, contact).pipe(map((data) => data.item));
  }
}
