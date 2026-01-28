import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

let client = null;

export function connectUserSocket(userId, onMessage, onStatus) {
  const token = localStorage.getItem("token"); // 🔑 JWT

  client = new Client({
    webSocketFactory: () =>
      new SockJS(`http://localhost:8080/ws?token=${token}`), // ✅ CRITICAL

    reconnectDelay: 5000,

    onConnect: () => {
      console.log("✅ WS CONNECTED AS USER", userId);
      onStatus?.("CONNECTED");

      // ✅ MUST subscribe to /user/queue/alerts
      client.subscribe("/user/queue/alerts", (msg) => {
        const event = JSON.parse(msg.body);
        console.log("WS EVENT:", event);
        onMessage(event);
      });
    },

    onStompError: (frame) => {
      console.error("❌ WS STOMP ERROR", frame);
    },

    onWebSocketClose: () => {
      console.warn("⚠️ WS CLOSED");
      onStatus?.("DISCONNECTED");
    },
  });

  client.activate();
}

export function disconnectUserSocket() {
  if (client) {
    client.deactivate();
    client = null;
  }
}
