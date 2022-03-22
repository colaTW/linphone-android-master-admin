package org.linphone.WebSocket;

import android.util.Log;
import java.net.URI;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class JWebSocketClient extends WebSocketClient {
    public JWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.e("JWebSocketClientonOpen", handshakedata.toString());
    }

    @Override
    public void onMessage(String message) {
        Log.e("JWebSocketClientMessage", "onMessage()");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.e("JWebSocketClientonClose", reason.toString());
    }

    @Override
    public void onError(Exception ex) {
        Log.e("JWebSocketClientonError", "onError()" + ex.toString());
    }
}
