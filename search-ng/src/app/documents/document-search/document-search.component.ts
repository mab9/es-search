import {Component, OnInit} from '@angular/core';
import {EventBusService} from "../../event-bus.service";
import {merge, Observable, Subject} from "rxjs";
import {Contact} from "../../contacts/contact";
import {ContactsService} from "../../contacts/contacts.service";
import {debounceTime, delay, distinctUntilChanged, switchMap, takeUntil} from "rxjs/operators";

@Component({
  selector: 'app-document-search',
  template: `
    <div style="text-align:center">
      <mat-toolbar>
        <mat-form-field color="accent" class="trm-search-container">
          <input matInput type="text" (input)="terms$.next($event.target.value)"></mat-form-field>
        <mat-icon color="accent">search</mat-icon>
      </mat-toolbar>
    </div>`,
  styleUrls: ['./document-search.component.scss']
})
export class DocumentSearchComponent implements OnInit {

  contacts$: Observable<Array<Contact>>;
  terms$ = new Subject<string>();

  constructor(private contactService: ContactsService, private eventBusService: EventBusService) {
  }

  ngOnInit() {
    const contactSearch$ = this.terms$.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      switchMap(x => this.contactService.search(x))
    );

    this.contacts$ = merge(
      contactSearch$,
      this.contactService.getContacts().pipe(delay(2000), takeUntil(this.terms$)));

    this.eventBusService.emit('appTitleChange', `Documents`);
  }

  trackByContacts(index: number, contact: Contact): number | string {
    return contact.id;
  }

}
