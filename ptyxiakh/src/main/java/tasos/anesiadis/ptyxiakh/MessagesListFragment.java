package tasos.anesiadis.ptyxiakh;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class MessagesListFragment extends Fragment implements OnClickListener {

    private View contentView;

    Button sendButton;

    EditText messageEditText;

    private Socket mSocket;

    private RecyclerView recyclerView;

    private RecyclerView.Adapter mAdapter;

    private List<Message> mMessages = new ArrayList<Message>();


    public EditText search;

    {
        try {
            mSocket = IO.socket("http://" + GlobalDataSingleton.GLOBAL_SOCKET_ADDRESS);

        } catch (URISyntaxException e) {
            Log.e("socket_url", e.toString());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String  url2 = "http://" + GlobalDataSingleton.GLOBAL_ADDRESS
                + "/hatalink/GetMessages?user1=" + GlobalDataSingleton.getInstance().getId()+"&user2="+GlobalDataSingleton.getInstance().getCurrId();


        contentView = inflater.inflate(R.layout.messages_list_fragment,
                container, false);

        TextView friendNameTextView = (TextView) contentView.findViewById(R.id.team_name);

        friendNameTextView.setBackgroundResource(R.drawable.sign_in_rounded);

        friendNameTextView.setText(GlobalDataSingleton.getInstance().getCurrUserName());

        recyclerView = (RecyclerView) contentView.findViewById(R.id.messages);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new MessageAdapter(getActivity(), mMessages);

        recyclerView.setAdapter(mAdapter);

        FutureCallback<JsonObject> futureCallBack = new FutureCallback<JsonObject>() {

            @Override
            public void onCompleted(Exception e, JsonObject result) {
                Log.d("Messages json:",result.toString());
                if (e == null) {
                    // success on the request

                    // get the jsonArray
                    JsonArray jsonArray = result.get("Messages").getAsJsonArray();

                    //final ArrayList<Message> m = new ArrayList<User>();

                    for (int i=0;i<jsonArray.size();i++) { // scan the json array
                        JsonObject messageJson = jsonArray.get(i).getAsJsonObject();

                        String username = String.valueOf(messageJson.get("Username")).replace("\"", "");
                        String message = String.valueOf(messageJson.get("Text")).replace("\"", "");
                        mMessages.add(new Message.Builder(Message.TYPE_MESSAGE)
                                .username(username).message(message).build());
                        Log.d("", messageJson.toString());
                    }

                    mAdapter.notifyItemInserted(mMessages.size() - 1);
                    recyclerView.scrollToPosition(mMessages.size()-1);

                } else {
                    // u got network failures
                    Log.e("ERROR", "REQUEST DENIED", e);
                    Toast.makeText(getActivity(), "Network error",
                            Toast.LENGTH_SHORT).show();
                }

            }
        };
        Ion.with(getActivity()).load(url2)//
                .asJsonObject()//
                .setCallback(futureCallBack);

        sendButton = (Button) contentView
                .findViewById(R.id.send_message_button);

        sendButton.setOnClickListener(this);

        search = (EditText)contentView.findViewById(R.id.search_edit_text);
        addTextListener();
        return contentView;

    }

    public void addTextListener(){
        //final String  url3 = "http://" + GlobalDataSingleton.GLOBAL_ADDRESS
         //       + "/hatalink/GetMessages?user1=" + GlobalDataSingleton.getInstance().getId()+"&user2="+GlobalDataSingleton.getInstance().getCurrId();
        //mMessages1.clear();
        //mAdapter = new MessageAdapter(getActivity(), mMessages1);

        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                final CharSequence search_query = query.toString().toLowerCase();

                final List<Message> mMessages1 = new ArrayList<Message>();



                            for (int i=0;i<mMessages.size();i++) { // scan the json array
                                Message messageObj = mMessages.get(i);
                                String username = messageObj.getUsername();
                                String message = messageObj.getMessage();
                                if (message.contains(search_query)) {
                                    mMessages1.add(new Message.Builder(Message.TYPE_MESSAGE)
                                            .username(username).message(message).build());
                                    Log.e("", message.toString());
                                }


                                //Log.d("", messageJson.toString());
                            }

                            mAdapter.notifyItemInserted(mMessages1.size() - 1);
                            recyclerView.scrollToPosition(mMessages1.size()-1);


                //recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                recyclerView = (RecyclerView) contentView.findViewById(R.id.messages);
                mAdapter = new MessageAdapter(getActivity(), mMessages1);
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();  // data set changed
            }
        });
    }

    @Override
    public void onClick(View v) {
        recyclerView = (RecyclerView) contentView.findViewById(R.id.messages);
        mAdapter = new MessageAdapter(getActivity(), mMessages);
        recyclerView.setAdapter(mAdapter);
        messageEditText = (EditText) contentView
                .findViewById(R.id.messages_edit_text);
        if (!messageEditText.getText().toString().isEmpty()){

            String url = "http://" + GlobalDataSingleton.GLOBAL_ADDRESS
                + "/hatalink/SendMessages?MessageSenderID=" + GlobalDataSingleton.getInstance().getId()
                    + "&MessageReceiverID=" + GlobalDataSingleton.getInstance().getCurrId()
                    +"&Text=" + messageEditText.getText().toString().trim();
            url = url.replaceAll(" ","%20");

            Log.e("Save message = ", url);

            Ion.with(getActivity()).load(url)//
                .asJsonObject();

            Log.i("sender#############", GlobalDataSingleton.getInstance().getId());
            Log.i("receiver#############", GlobalDataSingleton.getInstance().getCurrId());

            if (v.getId() == R.id.send_message_button) {

                if (mSocket.connected()) {

                    Log.d("SOCKET_STATUS", "CONNECTED");


                    String newMessage = messageEditText.getText().toString().trim();



                    messageEditText.setText("");

                    JSONObject messageObj = new JSONObject();
                    try {
                        messageObj.put("type", 0);
                        messageObj.put("username", GlobalDataSingleton.getInstance().getUsername());
                        messageObj.put("message", newMessage);

                    } catch (JSONException e) {

                        e.printStackTrace();
                    }


                    mSocket.emit("chat message", messageObj);

                } else {

                    Toast.makeText(getActivity(), R.string.chat_server_conn_error, Toast.LENGTH_SHORT).show();
                    Log.d("SOCKET_STATUS", "NOT CONNECTED!");

                    messageEditText.setText("");

                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSocket.on("chat message", onNewMessage);

        mSocket.connect();

        mSocket.emit("connect to room", GlobalDataSingleton.getInstance().getUniqueRoomName(), GlobalDataSingleton.getInstance().getUsername());

        JSONObject obj = new JSONObject();
        try {
            obj.put("type", 1);
            obj.put("username", GlobalDataSingleton.getInstance().getUsername());
            obj.put("message", " connected!");

        } catch (JSONException e) {

            e.printStackTrace();
        }

        mSocket.emit("chat message", obj);

        Log.v("mSocketURL:", "assigned");

    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    JSONObject data = (JSONObject) args[0];
                    int type;
                    String username;
                    String message;
                    try {

                        type = data.getInt("type");
                        username = data.getString("username");
                        message = data.getString("message");

                    } catch (JSONException e) {

                        e.printStackTrace();
                        return;
                    }

                    if (type == 0) {

                        // add the message to view
                        Log.e("MESSAGE_OBJECT", " username = " + username + " message = " + message);
                        addMessage(username, message);

                    } else if (type == 1) {

                        // add the action to view
                        Log.e("ACTION_OBJECT", " username = " + username + " action = " + message);
                        addAction(username, message);

                    }
                }
            });
        }
    };


    @Override
    public void onDestroy() {

        JSONObject obj = new JSONObject();
        try {
            obj.put("type", 1);
            obj.put("username", GlobalDataSingleton.getInstance().getUsername());
            obj.put("message", " disconnected!");

        } catch (JSONException e) {

            e.printStackTrace();
        }


        mSocket.emit("chat message", obj);
        mSocket.emit("disconnect from room", GlobalDataSingleton.getInstance().getUniqueRoomName());
        mSocket.disconnect();
        mSocket.off("chat message", onNewMessage);

        super.onDestroy();
    }

    private void addMessage(String username, String message) {
        mMessages.add(new Message.Builder(Message.TYPE_MESSAGE)
                .username(username).message(message).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        recyclerView.scrollToPosition(mMessages.size()-1);
    }

    private void addAction(String username, String message) {
        mMessages.add(new Message.Builder(Message.TYPE_ACTION)
                .username(username).message(message).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
    }
}





