package vn.vistark.giam_sat_nha_yen.ui.dashboard_screen.video_transfer;


import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;

import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okio.ByteString;

import static vn.vistark.giam_sat_nha_yen.data.firebase.FirebaseConfig.wsRef;

/**
 * Project ĐK Nhà Yến
 * Created by Nguyễn Trọng Nghĩa on 10/19/2019.
 * Organization: Vistark Team
 * Email: dev.vistark@gmail.com
 */

public class VideoTransfer {
    public final static String TAG = VideoTransfer.class.getSimpleName();

    public static String gottedUrl;
    private static VideoWebSocketListener listener;

    private static WebSocket ws;

    public static void attach() {
        wsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String urlStr = dataSnapshot.getValue().toString();
                    if (gottedUrl == null || gottedUrl.isEmpty()) {
                        gottedUrl = urlStr;
                        init();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static boolean init() {
        try {
            Request request = new Request.Builder().url(gottedUrl).build();
            listener = new VideoWebSocketListener();
            OkHttpClient client = new OkHttpClient();
            ws = client.newWebSocket(request, listener);
            client.dispatcher().executorService().shutdown();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean send(Bitmap bitmap) {
        if (listener.isConnected) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
            byte[] byteArray = stream.toByteArray();
            ByteString byteString = ByteString.of(byteArray, 0, byteArray.length);
            return ws.send(byteString);
        } else {
            init();
            return false;
        }
    }
}
