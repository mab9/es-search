import {ContactsListComponent} from "./contacts-list/contacts-list.component";
import {ContactsDetailsViewComponent} from "./contacts-details-view/contacts-details-view.component";


export const APP_ROUTES = [
  { path: '', component: ContactsListComponent },
  { path: '', component: ContactsListComponent },
  { path: 'contact/:id', component: ContactsDetailsViewComponent },
  { path: '**', redirectTo: '/' }
];
