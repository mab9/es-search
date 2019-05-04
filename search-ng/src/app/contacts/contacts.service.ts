import {Inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Contact} from "./contact";
import {Observable} from "rxjs";
import {debounceTime, distinctUntilChanged, map, switchMap} from "rxjs/operators";


@Injectable()
export class ContactsService {

  private API_ENDPOINT: string = 'http://localhost:8080/api/v1';

  constructor(private http: HttpClient, @Inject('API_ENDPOINT') private apiEndpoint: String) {
  }

  getContacts(): Observable<Array<Contact>> {
    return this.http.get<Contact[]>(this.API_ENDPOINT + '/contacts').pipe(map((data) => data));
  }

  getContact(id: string): Observable<Contact> {
    return this.http.get<Contact>(`${this.API_ENDPOINT}/contacts/${id}`).pipe(map((data) => data));
  }

  search(term: string): Observable<Array<Contact>> {
    let url = `${this.API_ENDPOINT}/contacts/search?term=${term}`;
    return this.http.get<Contact[]>(url).pipe(map((data) => data));
  }

  rawSearch(terms: Observable<string>, debounceMs = 400) : Observable<Array<Contact>> {
    return terms.pipe(
      debounceTime(debounceMs),
      distinctUntilChanged(),
      switchMap(x => this.search(x))
    );
  }

  updateContact(contact: Contact): Observable<Contact> {
    let url = `${this.API_ENDPOINT}/contacts/${contact.id}`;
    return this.http.put<Contact>(url, contact).pipe(map((data) => data));
  }
}
