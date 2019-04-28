import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Contact} from "../models/contact";

@Component({
  selector: 'trm-contacts-details',
  templateUrl: './contacts-details.component.html',
  styleUrls: ['./contacts-details.component.scss']
})
export class ContactsDetailsComponent {
  @Input() contact$: Contact;
  @Output() edit = new EventEmitter<Contact>();
  @Output() back = new EventEmitter<Contact>();
}
