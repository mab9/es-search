import { Component } from '@angular/core';
import {Contact} from "./models/contact";
import {CONTACT_DATA} from "./data/contact-data";

@Component({
  selector: 'app-root',
  template: `
    <div style="text-align:center">
      <mat-toolbar color="primary">Contacts</mat-toolbar>
      <mat-list>
        <mat-list-item *ngFor="let item of contacts ; trackBy: trackByContacts">
          <img mat-list-avatar [src]="item.image" alt="{{item.name}}">
          <h3 mat-line>{{ item.name }}</h3></mat-list-item>
      </mat-list>
    </div>`,
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  contacts = CONTACT_DATA;
  trackByContacts(index: number, contact: Contact): number | string { return contact.id; }
}
