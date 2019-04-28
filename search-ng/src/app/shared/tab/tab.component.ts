import {Component, Input} from '@angular/core';

@Component({
  selector: 'trm-tab',
  templateUrl: './tab.component.html',
  styleUrls: ['./tab.component.scss']
})
export class TabComponent {

  @Input() selected: boolean;
  @Input() title: string;
}
