import {Component, OnInit} from '@angular/core';
import {EventBusService} from "../../event-bus.service";
import {merge, Observable, Subject} from "rxjs";
import {debounceTime, delay, distinctUntilChanged, switchMap, takeUntil} from "rxjs/operators";
import {DocumentService} from "../document.service";
import {Document} from "../document";
import {MatTableDataSource} from "@angular/material";

@Component({
  selector: 'app-document-search',
  template: `
    <div style="text-align:center">
      
      <mat-toolbar *ngIf="showTools" style="width: 80%; margin: auto">
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
      
      <!--
      <mat-list>
        <mat-list-item *ngFor="let item of documents | async; trackBy: trackByDocuments" [routerLink]="['/document', item?.id]">
          <h3 matLine> {{item.documentName}} </h3>
          <p *ngIf="item.highlights" matLine [innerHTML]="item.highlights[0]"></p>
          <p *ngIf="item.highlights" matLine [innerHTML]="item.highlights[1]"></p>
          <p *ngIf="item.highlights" matLine [innerHTML]="item.highlights[2]"></p>
        </mat-list-item>
      </mat-list>
    -->
  
      <div class="mat-elevation-z8" style="width: 80%; margin: auto; margin-top: 10px;">

        <mat-toolbar style="width: 95%; margin: auto">
          <mat-form-field color="accent" class="trm-search-container">
            <input matInput type="text" #newTerm (input)="terms$.next($event.target.value)"
                   (keyup.enter)="search(newTerm.value)">
          </mat-form-field>
          <mat-icon color="accent">search</mat-icon>

          <button mat-raised-button color="primary" (click)="search(newTerm.value)">Search</button>
          <button mat-raised-button color="primary" (click)="toggleTools()">Tools</button>
        </mat-toolbar>


        <mat-paginator [pageSizeOptions]="[5, 10, 20]" showFirstLastButtons style="width: 95%; margin: auto"></mat-paginator>
        <table mat-table [dataSource]="dataSource" style="width: 95%; margin: auto">

          <!-- Position Column -->
          <ng-container matColumnDef="position">
            <th mat-header-cell *matHeaderCellDef> No. </th>
            <td mat-cell *matCellDef="let element"> {{element.position}} </td>
          </ng-container>

          <!-- Name Column -->
          <ng-container matColumnDef="name">
            <th mat-header-cell *matHeaderCellDef> Name </th>
            <td mat-cell *matCellDef="let element"> {{element.name}} </td>
          </ng-container>

          <!-- Weight Column -->
          <ng-container matColumnDef="weight">
            <th mat-header-cell *matHeaderCellDef> Weight </th>
            <td mat-cell *matCellDef="let element"> {{element.weight}} </td>
          </ng-container>

          <!-- Symbol Column -->
          <ng-container matColumnDef="symbol">
            <th mat-header-cell *matHeaderCellDef> Symbol </th>
            <td mat-cell *matCellDef="let element"> {{element.symbol}} </td>
          </ng-container>

          <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
          <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>
        </table>

      </div>

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

  displayedColumns: string[] = ['position', 'name', 'weight', 'symbol'];
  dataSource = new MatTableDataSource<PeriodicElement>(ELEMENT_DATA);


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


export interface PeriodicElement {
  name: string;
  position: number;
  weight: number;
  symbol: string;
}

const ELEMENT_DATA: PeriodicElement[] = [
  {position: 1, name: 'Hydrogen', weight: 1.0079, symbol: 'H'},
  {position: 2, name: 'Helium', weight: 4.0026, symbol: 'He'},
  {position: 3, name: 'Lithium', weight: 6.941, symbol: 'Li'},
  {position: 4, name: 'Beryllium', weight: 9.0122, symbol: 'Be'},
  {position: 5, name: 'Boron', weight: 10.811, symbol: 'B'},
  {position: 6, name: 'Carbon', weight: 12.0107, symbol: 'C'},
  {position: 7, name: 'Nitrogen', weight: 14.0067, symbol: 'N'},
  {position: 8, name: 'Oxygen', weight: 15.9994, symbol: 'O'},
  {position: 9, name: 'Fluorine', weight: 18.9984, symbol: 'F'},
  {position: 10, name: 'Neon', weight: 20.1797, symbol: 'Ne'},
  {position: 11, name: 'Sodium', weight: 22.9897, symbol: 'Na'},
  {position: 12, name: 'Magnesium', weight: 24.305, symbol: 'Mg'},
  {position: 13, name: 'Aluminum', weight: 26.9815, symbol: 'Al'},
  {position: 14, name: 'Silicon', weight: 28.0855, symbol: 'Si'},
  {position: 15, name: 'Phosphorus', weight: 30.9738, symbol: 'P'},
  {position: 16, name: 'Sulfur', weight: 32.065, symbol: 'S'},
  {position: 17, name: 'Chlorine', weight: 35.453, symbol: 'Cl'},
  {position: 18, name: 'Argon', weight: 39.948, symbol: 'Ar'},
  {position: 19, name: 'Potassium', weight: 39.0983, symbol: 'K'},
  {position: 20, name: 'Calcium', weight: 40.078, symbol: 'Ca'},
];


