import {Component, OnInit} from '@angular/core';
import {Contact} from "../models/contact";
import {ContactsService} from "../contacts.service";
import {merge, Observable, Subject} from "rxjs";
import {debounceTime, delay, distinctUntilChanged, switchMap, takeUntil} from "rxjs/operators";

@Component({
  selector: 'trm-contacts-list',
  template: `
    <div style="text-align:center">
      <mat-toolbar color="warn">Contacts</mat-toolbar>

      <mat-toolbar>
        <mat-form-field color="accent" class="trm-search-container">
          <input matInput type="text" (input)="terms$.next($event.target.value)">        </mat-form-field>
        <mat-icon color="accent">search</mat-icon>
      </mat-toolbar>
      
      <mat-list>
        <a mat-list-item [routerLink]="['/contact', item?.id]"
           *ngFor="let item of contacts$ | async; trackBy: trackByContacts">
          <img mat-list-avatar [src]="item?.image" alt="{{item?.name}}">
          <h3 mat-line>{{ item?.name}}</h3>
        </a>
      </mat-list>
    </div>`,
  styleUrls: ['./contacts-list.component.scss']
})
export class ContactsListComponent implements OnInit {

  contacts$: Observable<Array<Contact>>;
  terms$ = new Subject<string>();

  constructor(private contactService: ContactsService) {
  }

  ngOnInit() {
    const contactSearch$ = this.terms$.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      switchMap(x => this.contactService.search(x))
    );

    this.contacts$ = merge(
      contactSearch$,
      this.contactService.getContacts().pipe(delay(5000), takeUntil(this.terms$)));  }

  trackByContacts(index: number, contact: Contact): number | string {
    return contact.id;
  }

}
