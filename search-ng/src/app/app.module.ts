import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {DocumentsMaterialModule} from "./documents.-material.module";
import {ContactsService} from "./documents.service";
import { ContactsListComponent } from './contacts-list/contacts-list.component';
import {HttpClientModule} from "@angular/common/http";
import {RouterModule} from "@angular/router";
import {APP_ROUTES} from "./app.routes";

@NgModule({
  declarations: [
    AppComponent,
    ContactsListComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    DocumentsMaterialModule,
    AppRoutingModule,
    HttpClientModule,
    RouterModule.forRoot(APP_ROUTES)
  ],
  providers: [
    ContactsService, ContactsListComponent,
    {provide: 'API_ENDPOINT', useValue: 'http://localhost:4201/api'},
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
