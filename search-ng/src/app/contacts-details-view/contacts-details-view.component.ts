import { Component, OnInit } from '@angular/core';
import {Contact} from "../models/contact";
import {Observable} from "rxjs";
import {ActivatedRoute, Router} from "@angular/router";
import {ContactsService} from "../contacts.service";

@Component({
  selector: 'trm-contacts-detail-view',
  template: `
    <trm-contacts-details [contact$]="contact$"
                          (edit)="navigateToEditor($event)"
                          (back)="navigateToList()">
    </trm-contacts-details>`,
  styleUrls: ['./contacts-details-view.component.scss']
})
export class ContactsDetailsViewComponent implements OnInit {

  contact$: Observable<Contact>;

  constructor(private route: ActivatedRoute, private contactService: ContactsService, private router: Router) {
  }

  ngOnInit() {
    let id = this.route.snapshot.params['id'];
    this.contact$ = this.contactService.getContact(id)
  }

  navigateToEditor(contact: Contact) {
    this.router.navigate(['/contact', contact.id, 'edit']);
  }

  navigateToList() {
    this.router.navigate(['']);
  }
}
