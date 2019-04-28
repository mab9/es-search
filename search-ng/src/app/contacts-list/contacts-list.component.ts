import {Component, OnInit} from '@angular/core';
import {Contact} from "../models/contact";
import {ContactsService} from "../contacts.service";
import {merge, Observable, Subject} from "rxjs";
import {debounceTime, delay, distinctUntilChanged, switchMap, takeUntil} from "rxjs/operators";
import {EventBusService} from "../event-bus.service";

@Component({
  selector: 'trm-contacts-list',
  template: `
    <div style="text-align:center">
      <mat-toolbar>
        <mat-form-field color="accent" class="trm-search-container">
          <input matInput type="text" (input)="terms$.next($event.target.value)">        </mat-form-field>
        <mat-icon color="accent">search</mat-icon>
      </mat-toolbar>
      
      <mat-list>
        <mat-list>
          <a mat-list-item [routerLink]="['/contact', item?.id]"
             *ngFor="let item of contacts$ | async; trackBy: trackByContacts">
            <img mat-list-avatar [src]="'assets/images/placeholder.png'" alt="{{item?.firstName}}">
            <h3 mat-line>{{ item?.firstName}}</h3>
          </a>
        </mat-list>
      </mat-list>
    </div>`,
  styleUrls: ['./contacts-list.component.scss']
})
export class ContactsListComponent implements OnInit {

  contacts$: Observable<Array<Contact>>;
  terms$ = new Subject<string>();

  constructor(private contactService: ContactsService,
              private eventBusService: EventBusService) {
  }

  ngOnInit() {
    const contactSearch$ = this.terms$.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      switchMap(x => this.contactService.search(x))
    );

    this.contacts$ = merge(
      contactSearch$,
      this.contactService.getContacts().pipe(delay(5000), takeUntil(this.terms$)));

    this.eventBusService.emit('appTitleChange', 'Contacts')
  }

  trackByContacts(index: number, contact: Contact): number | string {
    return contact.id;
  }

}
