import {Component, OnInit} from '@angular/core';
import {Contact} from "../models/contact";
import {ContactsService} from "../contacts.service";

@Component({
  selector: 'trm-contacts-list',
  template: `
    <div style="text-align:center">
      <mat-toolbar color="warn">Contacts</mat-toolbar>
      <mat-list>
        <a mat-list-item [routerLink]="['/contact', item?.id]"
           *ngFor="let item of contacts ; trackBy: trackByContacts">
          <!-- <img mat-list-avatar [src]="item?.image" alt="{{item?.name}}"> -->
          <h3 mat-line>{{ item?.name }}</h3>
        </a>
      </mat-list>
    </div>`,
  styleUrls: ['./contacts-list.component.scss']
})
export class ContactsListComponent implements OnInit {

  contacts: Contact[];

  trackByContacts(index: number, contact: Contact): number | string {
    return contact.id;
  }

  constructor(private contactService: ContactsService) {
  }

  ngOnInit() {
    this.contactService.getContacts()
      .subscribe(contacts => {
        this.contacts = contacts;
      });
  }
}
