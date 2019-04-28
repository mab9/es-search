import { Component, OnInit } from '@angular/core';
import {Contact} from "../models/contact";
import {Observable} from "rxjs";
import {ActivatedRoute, Router} from "@angular/router";
import {ContactsService} from "../contacts.service";
import {EventBusService} from "../event-bus.service";

@Component({
  selector: 'trm-contacts-detail-view',
  template: `
    <trm-contacts-detail [contact$]="contact$"
                         (edit)="navigateToEditor($event)"
                         (back)="navigateToList()">
    </trm-contacts-detail>`,
  styleUrls: ['./contacts-detail-view.component.scss']
})
export class ContactsDetailViewComponent implements OnInit {

  contact$: Observable<Contact>;

  constructor(private route: ActivatedRoute, private contactService: ContactsService, private router: Router, private eventBusService: EventBusService) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.contact$ = this.contactService.getContact(params.id);
    });
    this.eventBusService.emit('appTitleChange', 'Detail');
  }

  navigateToEditor(contact: Contact) {
    this.router.navigate(['/contact', contact.id, 'edit']);
  }

  navigateToList() {
    this.router.navigate(['']);
  }
}
