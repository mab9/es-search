import {Component, OnInit} from '@angular/core';
import {EventBusService} from "../../event-bus.service";
import {merge, Observable, Subject} from "rxjs";
import {debounceTime, delay, distinctUntilChanged, switchMap, takeUntil} from "rxjs/operators";
import {DocumentService} from "../document.service";
import {Document} from "../document";

@Component({
  selector: 'app-document-search',
  template: `
    <div style="text-align:center">
      <mat-toolbar>
        <mat-form-field color="accent" class="trm-search-container">
          <input matInput type="text" (input)="terms$.next($event.target.value)"></mat-form-field>
        <mat-icon color="accent">search</mat-icon>
      </mat-toolbar>

      <mat-list>
        <mat-list>
          <a mat-list-item [routerLink]="['/document', item?.id]"
             *ngFor="let item of documents | async; trackBy: trackByContacts">
            <h3 mat-line>{{ item?.documentName}}</h3>
          </a>
        </mat-list>
      </mat-list>


    </div>`,
  styleUrls: ['./document-search.component.scss']
})
export class DocumentSearchComponent implements OnInit {

  documents: Observable<Array<Document>>;
  terms$ = new Subject<string>();

  constructor(private documentService: DocumentService, private eventBusService: EventBusService) {
  }

  ngOnInit() {
    const documentSearch$ = this.terms$.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      switchMap(x => this.documentService.search(x))
    );

    this.documents = merge(
      documentSearch$,
      this.documentService.getDocuments().pipe(delay(2000), takeUntil(this.terms$)));

    this.eventBusService.emit('appTitleChange', `Documents`);
  }

  trackByContacts(index: number, contact: Document): number | string {
    return contact.id;
  }

}
