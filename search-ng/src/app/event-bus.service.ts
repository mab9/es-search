import { Injectable } from '@angular/core';
import {Subject} from "rxjs";
import {filter, map} from "rxjs/operators";
import {EventBusArgs} from "./models/event-bus-args";

@Injectable({
  providedIn: 'root'
})
export class EventBusService {

  private _messages$ = new Subject<EventBusArgs>();

  emit(eventType: string, data: any) {
    this._messages$.next({ type: eventType, data: data });
  }

  observe(eventType: string) {
    return this._messages$.pipe(
      filter(args => args.type === eventType),
      map(args => args.data)
    );
  }
}
