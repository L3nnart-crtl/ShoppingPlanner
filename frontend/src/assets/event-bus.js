// src/event-bus.js
import mitt from 'mitt';

// Create an event bus instance using mitt
const emitter = mitt();

// Export the emitter to be used in other components
export const EventBus = emitter;
