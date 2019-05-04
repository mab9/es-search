import {Inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Document} from "./document";
import {Observable} from "rxjs";
import {map} from "rxjs/operators";
import {SearchQuery} from "./searchQuery";


@Injectable()
export class DocumentService {

  private API_ENDPOINT: string = 'http://localhost:8080/api/v1';

  constructor(private http: HttpClient, @Inject('API_ENDPOINT') private apiEndpoint: String) {
  }

  getDocuments(): Observable<Array<Document>> {
    return this.http.get<Document[]>(this.API_ENDPOINT + '/documents').pipe(map((data) => data));
  }

  getDocument(id: string): Observable<Document> {
    return this.http.get<Document>(`${this.API_ENDPOINT}/documents/${id}`).pipe(map((data) => data));
  }

  searchByTerm(term: string): Observable<Array<Document>> {
    let url = `${this.API_ENDPOINT}/documents/search/${term}`;
    return this.http.get<Document[]>(url).pipe(map((data) => data));
  }

  searchByTermHighlighted(term: string): Observable<Array<Document>> {
    let url = `${this.API_ENDPOINT}/documents/search/highlighted/${term}`;
    return this.http.get<Document[]>(url).pipe(map((data) => data));
  }

  // todo check get method instead post
  searchByQueryHighlighted(query: SearchQuery): Observable<Array<Document>> {
    let url = `${this.API_ENDPOINT}/documents/search/highlighted/query`;
    return this.http.post<Document[]>(url, query).pipe(map((data) => data));
  }
}
