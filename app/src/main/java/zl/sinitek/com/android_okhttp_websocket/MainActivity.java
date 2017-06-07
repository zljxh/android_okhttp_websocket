package zl.sinitek.com.android_okhttp_websocket;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.ByteString;

public class MainActivity extends AppCompatActivity implements WebSocketEcho.WebSocketInteractor {

    @BindView(R.id.msgRecyclerView) RecyclerView mMsgRecyclerView;
    @BindView(R.id.messageEditText) EditText mMsgEditText;

    WebSocket mWebSocket = null; // websocket to send message from main activity
    ChatAdapter mChatAdapter;   // recycler adapter of messages
    ChatMessage chatMessage;   // Basic message model class

    ArrayList<ChatMessage> mChatMessages = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // setting up web sockets
        WebSocketEcho.getInstance(this);


        // setting up recycler view with chat adapter

        mChatAdapter = new ChatAdapter(this,mChatMessages);

        mLinearLayoutManager = new LinearLayoutManager(MainActivity.this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMsgRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMsgRecyclerView.setAdapter(mChatAdapter);

    }

    @OnClick(R.id.relink)
    public void reLink(){
        WebSocketEcho.getInstance(this).reLink();
    }



    @OnClick(R.id.sendMessageButton)
    public void onSendMessage() {
        String message = mMsgEditText.getEditableText().toString();
        if (!message.equalsIgnoreCase("")) {
            chatMessage = new ChatMessage(message, true);
            mMsgEditText.setText("");
            mChatAdapter.add(chatMessage);
            mChatAdapter.notifyDataSetChanged();
            mMsgRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount());

            mWebSocket.send(message);
        }
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        mWebSocket = webSocket;

    }

    @Override
    public void onGetMessage(final String message) {
        if(message!=null){
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    chatMessage = new ChatMessage(message,  false);
                    mChatAdapter.add(chatMessage);
                    mChatAdapter.notifyDataSetChanged();
                }
            });

        }
    }

    private final MockWebServer mockWebServer = new MockWebServer();
    private final ExecutorService writeExecutor = Executors.newSingleThreadExecutor();



    public void web(){
        mockWebServer.enqueue(new MockResponse().withWebSocketUpgrade(new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
            }
        }));

    }

}
