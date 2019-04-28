import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {Contact} from "../models/contact";
import {ContactsService} from "../contacts.service";

@Component({
  selector: 'app-contacts-editor',
  templateUrl: './contacts-editor.component.html',
  styleUrls: ['./contacts-editor.component.scss']
})
export class ContactsEditorComponent implements OnInit {

  contact: Contact = <Contact>{ address: {}};

  constructor(private route: ActivatedRoute, private contactService: ContactsService, private router: Router) {
  }

  ngOnInit() {
    let id = this.route.snapshot.params['id'];
    this.contactService.getContact(id)
      .subscribe(contact => {
        this.contact = contact
      });
  }

  save(contact: Contact) {
    this.contactService.updateContact(contact)
      .subscribe(contact => {
        this.goToDetails(contact)
      });
  }

  cancel(contact: Contact) {
    this.goToDetails(contact)
  }

  private goToDetails(contact: Contact) {
    this.router.navigate(['/contact', contact.id]);
  }
}
