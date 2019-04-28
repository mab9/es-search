import {ContactsListComponent} from "./contacts-list/contacts-list.component";
import {ContactsDetailsComponent} from "./contacts-details/contacts-details.component";


export const APP_ROUTES = [
  { path: '', component: ContactsListComponent },
  { path: '', component: ContactsListComponent },
  { path: 'contact/:id', component: ContactsDetailsComponent },
  { path: '**', redirectTo: '/' }
];
