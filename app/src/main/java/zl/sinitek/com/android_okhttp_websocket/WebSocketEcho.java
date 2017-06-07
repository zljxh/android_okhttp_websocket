package zl.sinitek.com.android_okhttp_websocket;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


public class WebSocketEcho extends WebSocketListener {

    private static WebSocketEcho mWebSocketEcho = null;
    private static WebSocketInteractor mWebSocketInteractor;

    private WebSocketEcho() {
    }

    public static WebSocketEcho getInstance(WebSocketInteractor webSocketInteractor) {
        if (mWebSocketEcho == null) {
            mWebSocketInteractor = webSocketInteractor;
            mWebSocketEcho = new WebSocketEcho();
            mWebSocketEcho.run();
        }
        return mWebSocketEcho;
    }

    public void reLink() {
        run();
    }

    private void run() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
//                .url("ws://echo.websocket.org")
                .url("ws://192.168.50.163:8089/zl")
                .build();

        client.newWebSocket(request, this);

        // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.

        client.dispatcher().executorService().shutdown();
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        mWebSocketInteractor.onOpen(webSocket);

        Log.i("zl", "onOpen");
        /*webSocket.send("Hello...");
        webSocket.send("...World!");
        webSocket.send(ByteString.decodeHex("deadbeef"));
        webSocket.close(1000, "Goodbye, World!");*/
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        System.out.println("MESSAGE: " + text);
        Log.i("zl", "onMessage");
        mWebSocketInteractor.onGetMessage(text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        Log.i("zl", "onMessage");
        System.out.println("MESSAGE: " + bytes.hex());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
//        webSocket.close(1000, null);
        Log.i("zl", "onClosing=" + reason);

        System.out.println("CLOSE: " + code + " " + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        t.printStackTrace();
        Log.i("zl", "onFailure");
    }


    /*
    * Interface to interact with main acivity
    * */
    public interface WebSocketInteractor {
        void onOpen(WebSocket webSocket);

        void onGetMessage(String message);
    }
}
