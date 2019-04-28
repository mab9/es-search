import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {Contact} from "../models/contact";
import {ContactsService} from "../contacts.service";
import {EventBusService} from "../event-bus.service";

@Component({
  selector: 'app-contacts-editor',
  templateUrl: './contacts-editor.component.html',
  styleUrls: ['./contacts-editor.component.scss']
})
export class ContactsEditorComponent implements OnInit {

  contact: Contact = <Contact>{ address: {}};

  constructor(private route: ActivatedRoute,
              private contactService: ContactsService,
              private router: Router,
              private eventBusService: EventBusService) {
  }

  ngOnInit() {
    let id = this.route.snapshot.params['id'];
    this.contactService.getContact(id)               // or better use async pipe. change contact to type observable, and remove subscribtion bewlow. to display the contact name just getContact(id).pipe(this.eventBus..emit)
      .subscribe(contact => {
        this.contact = contact;
        this.eventBusService.emit('appTitleChange', `Edit ${contact.name}`);
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
