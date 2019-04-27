import {Component, OnInit} from '@angular/core';
import {Contact} from "../models/contact";
import {ContactsService} from "../documents.service";
import {CONTACT_DATA} from "../data/contact-data";

@Component({
  selector: 'trm-contacts-list',
  template: `
    <div style="text-align:center">
      <mat-toolbar color="primary">Contacts</mat-toolbar>
      <mat-list>
        <mat-list-item *ngFor="let item of contacts ; trackBy: trackByContacts">
          <img mat-list-avatar [src]="item.image" alt="{{item.name}}">
          <h3 mat-line>{{ item.name }}</h3></mat-list-item>
      </mat-list>
    </div>`,
  styleUrls: ['./contacts-list.component.scss']
})
export class ContactsListComponent implements OnInit {

  //contacts: Observable<Array<Contact>>;
  contacts: Array<Contact> = CONTACT_DATA;

  trackByContacts(index: number, contact: Contact): number | string {
    return contact.id;
  }

  constructor(private  contactService: ContactsService) {
  }

  ngOnInit() {
    //this.contacts = this.contactService.getContacts();
  }

}
