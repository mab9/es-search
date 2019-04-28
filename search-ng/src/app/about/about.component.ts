import { Component, OnInit } from '@angular/core';
import {EventBusService} from "../event-bus.service";

@Component({
  selector: 'trm-about',
  template: `<div class="trm-about">
    <mat-card fxLayout="column" fxFlex fxLayoutAlign="center center">
      <h2 mat-card-title>Mab search tests</h2>
      <mat-card-content>
        <img src="/assets/images/me.jpg" alt="mab">
        <p style="text-align: center;">Created by mab</p>
      </mat-card-content>
      <mat-card-actions>
        <a mat-button title="Go back to dashboard"
           routerLink="/">
          Go Back
        </a>
      </mat-card-actions>
    </mat-card>
  </div>`,




  styleUrls: ['./about.component.scss']
})
export class AboutComponent implements OnInit {

  constructor(private eventBusService: EventBusService){ }

  ngOnInit() {
    this.eventBusService.emit('appTitleChange', `About`);
  }

}
