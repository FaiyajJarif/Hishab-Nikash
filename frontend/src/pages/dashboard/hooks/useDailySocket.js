import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { useEffect } from "react";

export function useDailySocket(onUpdate) {
  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
      reconnectDelay: 5000,
      debug: (str) => console.log("STOMP:", str),
    });

    client.onConnect = () => {
      console.log("✅ WS connected");

      client.subscribe(`/topic/daily/${userId}`, (msg) => {
        const payload = JSON.parse(msg.body);
        console.log("📡 Daily update", payload);
      });      
    };

    client.onStompError = (frame) => {
      console.error("❌ STOMP error", frame);
    };

    client.activate();

    return () => client.deactivate();
  }, [onUpdate]);
}
