import { Component, OnInit } from '@angular/core';
import {Contact} from "../models/contact";
import {ContactsService} from "../contacts.service";
import {ActivatedRoute} from "@angular/router";
import {Observable} from "rxjs";

@Component({
  selector: 'app-contacts-details',
  templateUrl: './contacts-details.component.html',
  styleUrls: ['./contacts-details.component.scss']
})
export class ContactsDetailsComponent implements OnInit {

  contact$: Observable<Contact>;

  loading: Contact;

  constructor(private route: ActivatedRoute, private contactService: ContactsService) {
  }

  ngOnInit() {
    let id = this.route.snapshot.params['id'];
    this.contact$ = this.contactService.getContact(id)
  }
}
