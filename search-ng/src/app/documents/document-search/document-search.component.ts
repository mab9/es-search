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
                 (keyup.enter)="search(newTerm.value)">
        </mat-form-field>
        <mat-icon color="accent">search</mat-icon>

        <button mat-raised-button color="primary" (click)="search(newTerm.value)">Search</button>
        <button mat-raised-button color="primary" (click)="toggleTools()">Tools</button>
      </mat-toolbar>


      <mat-toolbar *ngIf="showTools">
        <mat-checkbox [(ngModel)]="fuzzySearch" style="width: 10%;">fuzzy search</mat-checkbox>
        <mat-checkbox [(ngModel)]="documentNameSearch" style="width: 20%;">only document name search</mat-checkbox>

        <div style="width: 10%; margin-right: 15px">
          <mat-form-field color="accent">
            <input matInput [matDatepicker]="picker" placeholder="From date" [(ngModel)]="fromDate">
            <mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
            <mat-datepicker #picker></mat-datepicker>
          </mat-form-field>
        </div>

        <div style="width: 10%;">
          <mat-form-field color="accent">
            <input matInput [matDatepicker]="picker2" placeholder="To date" [(ngModel)]="toDate">
            <mat-datepicker-toggle matSuffix [for]="picker2"></mat-datepicker-toggle>
            <mat-datepicker #picker2></mat-datepicker>
          </mat-form-field>
        </div>
      </mat-toolbar>
      
      <mat-list>
        <mat-list-item *ngFor="let item of documents | async; trackBy: trackByDocuments" [routerLink]="['/document', item?.id]">
          <h3 matLine> {{item.documentName}} </h3>
          <p *ngIf="item.highlights" matLine [innerHTML]="item.highlights[0]"></p>
          <p *ngIf="item.highlights" matLine [innerHTML]="item.highlights[1]"></p>
          <p *ngIf="item.highlights" matLine [innerHTML]="item.highlights[2]"></p>
        </mat-list-item>
      </mat-list>
    </div>`,
  styleUrls: ['./document-search.component.scss']
})
export class DocumentSearchComponent implements OnInit {

  documents: Observable<Array<Document>>;
  terms$ = new Subject<string>();
  showTools: boolean = false;
  fuzzySearch: boolean = false;
  documentNameSearch: boolean = false;
  fromDate: Date;
  toDate: Date;

  constructor(private documentService: DocumentService, private eventBusService: EventBusService) {
  }

  ngOnInit() {
    const documentSearch$ = this.terms$.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      switchMap(x => this.documentService.searchByQueryHighlighted(x))
    );

    this.documents = merge(
      documentSearch$,
      this.documentService.getDocuments().pipe(delay(0), takeUntil(this.terms$)));

    this.eventBusService.emit('appTitleChange', `Documents`);
  }

  trackByDocuments(index: number, document: Document): number | string {
    return document.id;
  }

  search(newTerm: string) {
    if (newTerm) {
      this.documents = this.documentService.searchByQueryHighlighted(newTerm);
    } else {
      this.documents = this.documentService.getDocuments();
    }
  }

  toggleTools() {
    this.showTools = !this.showTools;
    this.fuzzySearch = false;
    this.documentNameSearch = false;
    this.fromDate = null;
    this.toDate = null;

  }

}
