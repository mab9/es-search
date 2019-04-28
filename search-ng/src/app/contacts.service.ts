import {Inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Contact} from "./models/contact";
import {Observable} from "rxjs";
import {map} from "rxjs/operators";

interface ContactResponse {
  item: Contact
}

interface ContactsResponse {
  items: Contact[]
}

@Injectable()
export class ContactsService {

  private API_ENDPOINT: string = 'http://localhost:8080/api/v1';

  constructor(private http: HttpClient, @Inject('API_ENDPOINT') private apiEndpoint: String) {
  }

  getContacts(): Observable<Array<Contact>> {
    return this.http.get<ContactsResponse>(this.API_ENDPOINT + '/contacts').pipe(map((data) => data.items));
  }

  getContact(id: string): Observable<Contact> {
    return this.http.get<ContactResponse>(`${this.API_ENDPOINT}/contacts/${id}`).pipe(map((data) => data.item));
  }

  search(term: string): Observable<Array<Contact>> {
    let url = `${this.API_ENDPOINT}/contacts/search?term=${term}`;
    return this.http.get<ContactsResponse>(url).pipe(map((data) => data.items));
  }
}
