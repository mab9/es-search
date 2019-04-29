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
          <input matInput type="text" #newTerm (input)="terms$.next($event.target.value)"
                 (keyup.enter)="search(newTerm.value)" newTerm.value="''">
        </mat-form-field>
        <mat-icon color="accent">search</mat-icon>

        <button mat-raised-button color="primary" (click)="search(newTerm.value)">Search</button>
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
  terms = ['Windstorm', 'Bombasto', 'Magneta', 'Tornado'];


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

  search(newTerm: string) {
    if (newTerm) {
      this.terms.push(newTerm);
      // todo do search on es
    }
  }
}
