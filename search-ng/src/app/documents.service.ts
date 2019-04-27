import {Inject, Injectable} from '@angular/core';
import {CONTACT_DATA} from "./data/contact-data";
import {Contact} from "./models/contact";
import {HttpClient} from "@angular/common/http";
import {debounceTime, distinctUntilChanged, map, switchMap} from "rxjs/operators";
import {Observable} from "rxjs";

interface ContactResponse {
  item: Contact
}

interface ContactsResponse {
  items: Contact[]
}

@Injectable()
export class ContactsService {

  contacts = CONTACT_DATA;
  private API_ENDPOINT: string = 'http://localhost:4201/api';

  constructor(private http: HttpClient, @Inject('API_ENDPOINT') private apiEndpoint: String) {
  }

  getContacts(): Observable<Array<Contact>> {
    return this.http.get<ContactsResponse>( '/contacts').pipe(map((data) => data.items));
  }

  getContact(id: string): Observable<Contact> {
    return this.http.get<ContactResponse>(`${this.API_ENDPOINT}/contacts/${id}`).pipe(map((data) => data.item));
  }

  updateContact(contact: Contact): Observable<Contact> {
    let url = `${this.API_ENDPOINT}/contacts/${contact.id}`;
    return this.http.put<ContactResponse>(url, contact).pipe(map((data) => data.item));
  }

  search(term: string): Observable<Array<Contact>> {
    let url = `${this.API_ENDPOINT}/search?text=${term}`;
    return this.http.get<ContactsResponse>(url).pipe(map((data) => data.items));
  }

  rawSearch(terms: Observable<string>, debounceMs = 400) : Observable<Array<Contact>> {
    return terms.pipe(
      debounceTime(debounceMs),
      distinctUntilChanged(),
      switchMap(x => this.search(x))
    );
  }
}
