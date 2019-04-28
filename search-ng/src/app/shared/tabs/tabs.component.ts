import {AfterContentInit, Component, ContentChildren, OnInit, QueryList} from '@angular/core';
import {TabComponent} from "../tab/tab.component";

@Component({
  selector: 'trm-tabs',
  templateUrl: './tabs.component.html',
  styleUrls: ['./tabs.component.scss']
})
export class TabsComponent implements AfterContentInit {

  @ContentChildren(TabComponent)
  tabs: QueryList<TabComponent>;

  ngAfterContentInit() {
    this.select(this.tabs.first);
  }

  select(tab: TabComponent) {
    this.tabs.forEach(t => {
      t.selected = false;
    });
    tab.selected = true;
  }
}
