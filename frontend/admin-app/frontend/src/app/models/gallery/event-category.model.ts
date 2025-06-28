// src/app/models/gallery/event-category.model.ts

import { Event } from './event.model';

export interface EventCategory {
  id: number;
  name: string;
  events: Event[];
}
