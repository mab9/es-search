import {Component, OnInit} from '@angular/core';
import {Observable} from "rxjs";
import {EventBusArgs} from "./models/event-bus-args";
import {EventBusService} from "./event-bus.service";

@Component({
  selector: 'app-root',
  template: `
    <mat-toolbar color="warn">{{ title$ | async }}</mat-toolbar>
    <router-outlet></router-outlet>
    `,
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  title$: Observable<EventBusArgs>;

  constructor(private eventBusService: EventBusService) {}

  ngOnInit(): void {
    this.title$ = this.eventBusService.observe('appTitleChange');
  }
}
