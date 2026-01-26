import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

let client = null;

export function connectFamilySocket(
  familyId,
  onEvent,
  onStatusChange // 👈 NEW
) {
  client = new Client({
    webSocketFactory: () =>
      new SockJS("http://localhost:8080/ws"),

    reconnectDelay: 5000,

    onConnect: () => {
      onStatusChange?.("CONNECTED"); // ✅
      client.subscribe(`/topic/family/${familyId}`, msg => {
        onEvent(JSON.parse(msg.body));
      });
    },

    onWebSocketClose: () => {
      onStatusChange?.("DISCONNECTED"); // ✅
    },

    onStompError: () => {
      onStatusChange?.("ERROR"); // ✅
    },
  });

  client.activate();
}

export function disconnectFamilySocket() {
  if (client) {
    client.deactivate();
    client = null;
  }
}
