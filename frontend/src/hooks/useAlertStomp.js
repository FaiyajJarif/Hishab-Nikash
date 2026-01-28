import { useEffect } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import { toast } from "react-hot-toast";

export function useAlertStomp(onAlert) {
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) return;

    const client = new Client({
        webSocketFactory: () =>
          new SockJS(`http://localhost:8080/ws?token=${token}`),

      connectHeaders: {
        Authorization: `Bearer ${token}`
      },

      onConnect: () => {
        console.log("🔔 Alert STOMP connected");

        client.subscribe("/user/queue/alerts", (msg) => {
          const alert = JSON.parse(msg.body);

          // 🔔 Toast
          if (alert.severity === "CRITICAL") {
            toast.error(alert.message);
          } else if (alert.severity === "WARNING") {
            toast(alert.message, { icon: "⚠️" });
          } else {
            toast(alert.message);
          }

          // 📥 Push into UI
          onAlert?.(alert);
        });
      },

      onStompError: (frame) => {
        console.error("STOMP error", frame);
      }
    });

    client.activate();

    return () => client.deactivate();
  }, [onAlert]);
}
