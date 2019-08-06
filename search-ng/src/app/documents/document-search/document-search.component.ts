import {Component, OnInit, ViewChild} from '@angular/core';
import {EventBusService} from "../../event-bus.service";
import {Observable, Subject} from "rxjs";
import {DocumentService} from "../document.service";
import {Document} from "../document";
import {MatPaginator, MatTableDataSource} from "@angular/material";
import {SearchQuery} from "../searchQuery";

@Component({
  selector: 'app-document-search',
  template: `
    <div style="text-align:center">
      
      <mat-toolbar class="tools-containe" *ngIf="showTools" style="width: 80%; margin: auto">
        <mat-checkbox [(ngModel)]="fuzzySearch" style="margin-right: 15px">fuzzy</mat-checkbox>
        <mat-checkbox [(ngModel)]="documentNameSearch" style="margin-right: 15px">only document name</mat-checkbox>

        <div style="margin-right: 15px">
          <mat-form-field color="accent">
            <input matInput [matDatepicker]="picker" placeholder="From date" [(ngModel)]="fromDate">
            <mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
            <mat-datepicker #picker></mat-datepicker>
          </mat-form-field>
        </div>

        <div>
          <mat-form-field color="accent">
            <input matInput [matDatepicker]="picker2" placeholder="To date" [(ngModel)]="toDate">
            <mat-datepicker-toggle matSuffix [for]="picker2"></mat-datepicker-toggle>
            <mat-datepicker #picker2></mat-datepicker>
          </mat-form-field>
        </div>
      </mat-toolbar>
      
      <div class="mat-elevation-z8" style="width: 80%; margin: auto; margin-top: 10px; padding-bottom: 20px;">

        <mat-toolbar style="width: 95%; margin: auto">
          <mat-form-field color="accent" class="trm-search-container">
            <input matInput type="text" #newTerm (input)="terms$.next($event.target.value)"
                   (keyup.enter)="search(newTerm.value)">
          </mat-form-field>
          <mat-icon color="accent">search</mat-icon>

          <button mat-raised-button color="primary" (click)="search(newTerm.value)">Search</button>
          <button mat-raised-button color="primary" (click)="toggleTools()">Tools</button>
        </mat-toolbar>


        <mat-paginator [pageSizeOptions]="[5, 10, 20]" showFirstLastButtons style="width: 95%; margin: auto" (page)="pageChanged($event)"></mat-paginator>
        <table mat-table [dataSource]="dataSource" style="width: 95%; margin: auto">
          
          <!-- Name Column -->
          <ng-container matColumnDef="documentName">
            <th mat-header-cell *matHeaderCellDef> Name</th>
            <td mat-cell *matCellDef="let element"> 
              <h3>{{element.documentName}}</h3>
              <p style="line-height: 0.7;" *ngIf="element.highlights" matLine [innerHTML]="element.highlights[0]"></p>
              <p style="line-height: 0.7;" *ngIf="element.highlights" matLine [innerHTML]="element.highlights[1]"></p>
              <p style="line-height: 0.7;" *ngIf="element.highlights" matLine [innerHTML]="element.highlights[2]"></p>
              <p style="line-height: 0.7;" *ngIf="element.highlights" matLine [innerHTML]="element.highlights[3]"></p>
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

  @ViewChild(MatPaginator) paginator: MatPaginator;

  displayedColumns: string[] = ['documentName'];
  dataSource = new MatTableDataSource<Document>();


  constructor(private documentService: DocumentService, private eventBusService: EventBusService) {}

  ngOnInit() {
    this.eventBusService.emit('appTitleChange', `Documents`);
    this.dataSource.paginator = this.paginator;
    this.documents = this.documentService.getDocuments();
    this.documents.subscribe(data => this.dataSource.data = data);
  }

  search(newTerm: string) {
    this.documents.subscribe(data => this.dataSource.data = data);
    if (newTerm) {
      if (this.showTools) {
        this.documents = this.searchByQuery(newTerm);
      } else {
        this.documents = this.documentService.searchByTermHighlighted(newTerm);
      }
    }
  }

  private searchByQuery(newTerm: string) {
    if (this.showTools) {
      let searchQuery: SearchQuery = {
        term: newTerm,
        fuzzy: this.fuzzySearch,
        documentName: this.documentNameSearch,
        fromDate: this.fromDate,
        toDate: this.toDate
      };

      return this.documentService.searchByQueryHighlighted(searchQuery);
    }
  }


  toggleTools() {
    this.showTools = !this.showTools;
    this.resetTools();
  }

  private resetTools() {
    this.fuzzySearch = false;
    this.documentNameSearch = false;
    this.fromDate = null;
    this.toDate = null;
  }

  pageChanged() {
    // auf welcher Seite befine ich mich gerade. Ausrechnen length / pageSize ...
    console.info(this.paginator.pageIndex);
    console.info(this.paginator.pageSize);
    console.info(this.paginator.length);
    // downloaden anzahl hits total -> dannach immer beim page wechsel die nächsten docs nachladen.

    this.paginator.length = 100;
  }

}



