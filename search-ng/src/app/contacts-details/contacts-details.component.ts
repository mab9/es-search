import { Component, OnInit } from '@angular/core';
import {Contact} from "../models/contact";
import {ContactsService} from "../contacts.service";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-contacts-details',
  templateUrl: './contacts-details.component.html',
  styleUrls: ['./contacts-details.component.scss']
})
export class ContactsDetailsComponent implements OnInit {

  contact: Contact;

  constructor(private route: ActivatedRoute, private contactService: ContactsService) {
  }

  ngOnInit() {
    let id = this.route.snapshot.params['id'];
    this.contactService.getContact(id)
      .subscribe(contact => {
        console.info(contact);
        this.contact = contact;
      });
  }
}
