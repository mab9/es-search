import { Component, OnInit } from '@angular/core';
import {EventBusService} from "../../event-bus.service";

@Component({
  selector: 'app-document-search',
  templateUrl: './document-search.component.html',
  styleUrls: ['./document-search.component.scss']
})
export class DocumentSearchComponent implements OnInit {

  constructor(private eventBusService: EventBusService) { }

  ngOnInit() {
    this.eventBusService.emit('appTitleChange', `Secasign Box Documents`);
  }

}
