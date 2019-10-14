package vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.video_transfer;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import vn.vistark.giam_sat_nha_yen.Config;

public class VideoWebSocketListener extends WebSocketListener {
    public final static String TAG = VideoWebSocketListener.class.getSimpleName();
    public boolean isConnected = false;

    private static final int NORMAL_CLOSURE_STATUS = 1000;

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        super.onOpen(webSocket, response);
        isConnected = true;
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        isConnected = false;
        Log.d(TAG, "onClosing: " + code + " / " + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        isConnected = false;
        Log.d(TAG, "onFailure: " + t.getMessage());
    }
}
