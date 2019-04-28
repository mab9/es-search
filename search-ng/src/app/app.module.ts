import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {ContactsMaterialModule} from "./contacts-material.module";
import {ContactsService} from "./contacts.service";
import { ContactsListComponent } from './contacts-list/contacts-list.component';
import {HttpClientModule} from "@angular/common/http";
import {RouterModule} from "@angular/router";
import {APP_ROUTES} from "./app.routes";
import { ContactsDetailsComponent } from './contacts-details/contacts-details.component';
import { ContactsDetailsViewComponent } from './contacts-details-view/contacts-details-view.component';
import { ContactsEditorComponent } from './contacts-editor/contacts-editor.component';
import {FormsModule} from "@angular/forms";
import { ContactsDashboardComponent } from './contacts-dashboard/contacts-dashboard.component';
import { AboutComponent } from './about/about.component';

@NgModule({
  declarations: [
    AppComponent,
    ContactsListComponent,
    ContactsDetailsComponent,
    ContactsDetailsViewComponent,
    ContactsEditorComponent,
    ContactsDashboardComponent,
    AboutComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    ContactsMaterialModule,
    HttpClientModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    RouterModule.forRoot(APP_ROUTES)
  ],
  providers: [
    ContactsService, ContactsListComponent, ContactsDetailsComponent, ContactsDetailsViewComponent, ContactsEditorComponent,
    {provide: 'API_ENDPOINT', useValue: 'http://localhost:4201/api'},
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
