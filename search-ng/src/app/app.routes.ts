import {ContactsDetailsViewComponent} from "./contacts-details-view/contacts-details-view.component";
import {ContactsEditorComponent} from "./contacts-editor/contacts-editor.component";
import {ContactsDashboardComponent} from "./contacts-dashboard/contacts-dashboard.component";
import {AboutComponent} from "./about/about.component";


export const APP_ROUTES = [
  { path: 'about', component: AboutComponent },
  {
    path: '',
    component: ContactsDashboardComponent,
    children: [
      { path: '', redirectTo: 'contact/0', pathMatch: 'full' },
      { path: 'contact/:id', component: ContactsDetailsViewComponent },
      { path: 'contact/:id/edit', component: ContactsEditorComponent }
    ]
  },
  {path: '**', redirectTo: '/'}         // default page
];
