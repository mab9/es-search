import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {AppMaterialModule} from "./app-material.module";
import {ContactsService} from "./contacts/contacts.service";
import {ContactsListComponent} from './contacts/contacts-list/contacts-list.component';
import {HttpClientModule} from "@angular/common/http";
import {RouterModule} from "@angular/router";
import {APP_ROUTES} from "./app.routes";
import {ContactsDetailComponent} from './contacts/contacts-detail/contacts-detail.component';
import {ContactsDetailViewComponent} from './contacts/contacts-detail-view/contacts-detail-view.component';
import {ContactsEditorComponent} from './contacts/contacts-editor/contacts-editor.component';
import {FormsModule} from "@angular/forms";
import {ContactsDashboardComponent} from './contacts/contacts-dashboard/contacts-dashboard.component';
import {AboutComponent} from './about/about.component';
import {TabComponent} from './shared/tab/tab.component';
import {TabsComponent} from './shared/tabs/tabs.component';
import {DocumentSearchComponent} from './documents/document-search/document-search.component';
import {DocumentService} from "./documents/document.service";
import {MatPaginator, MatPaginatorModule, MatTableDataSource, MatTableModule} from "@angular/material";

@NgModule({
  declarations: [
    AppComponent,
    ContactsListComponent,
    ContactsDetailComponent,
    ContactsDetailViewComponent,
    ContactsEditorComponent,
    ContactsDashboardComponent,
    AboutComponent,
    TabComponent,
    TabsComponent,
    DocumentSearchComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppMaterialModule,
    HttpClientModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    RouterModule.forRoot(APP_ROUTES),
    MatTableModule,
    MatPaginatorModule
  ],
  providers: [
    ContactsService, ContactsListComponent, ContactsDetailComponent, ContactsDetailViewComponent, ContactsEditorComponent, DocumentSearchComponent, DocumentService, MatTableModule, MatPaginatorModule,

    {provide: 'API_ENDPOINT', useValue: 'http://localhost:4201/api'},
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
