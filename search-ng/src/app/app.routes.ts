import {ContactsDetailViewComponent} from "./contacts/contacts-detail-view/contacts-detail-view.component";
import {ContactsEditorComponent} from "./contacts/contacts-editor/contacts-editor.component";
import {ContactsDashboardComponent} from "./contacts/contacts-dashboard/contacts-dashboard.component";
import {AboutComponent} from "./about/about.component";


export const APP_ROUTES = [
  { path: 'about', component: AboutComponent },
  {
    path: '',
    component: ContactsDashboardComponent,
    children: [
      { path: '', redirectTo: 'contact/0', pathMatch: 'full' },
      { path: 'contact/:id', component: ContactsDetailViewComponent },
      { path: 'contact/:id/edit', component: ContactsEditorComponent }
    ]
  },
  {path: '**', redirectTo: '/'}         // default page
];
