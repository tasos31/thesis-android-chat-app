package tasos.anesiadis.ptyxiakh;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class UsersListFragment extends Fragment implements OnClickListener {

    private View contentView;
    Dialog dialog;

    String url = "http://" + GlobalDataSingleton.GLOBAL_ADDRESS
            + "/hatalink/GetUsers?UserID=" + GlobalDataSingleton.getInstance().getId();

    String uniqueRoomName;

    MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        contentView = inflater.inflate(R.layout.users_list_fragment, container,
                false);

        mainActivity = (MainActivity) getActivity();

        FutureCallback<JsonObject> futureCallBack = new FutureCallback<JsonObject>() {

            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if (e == null) {
                    // success on the request

                    // get the jsonArray
                    JsonArray jsonArray = result.get("Users").getAsJsonArray();

                    final ArrayList<User> users = new ArrayList<User>();

                    for (JsonElement je : jsonArray) { // scan the json array
                        // parse each element
                        User u = new User();
                        u.parseJsonObject(je.getAsJsonObject());

                        // add elements to object array one by one
                        users.add(u);
                    }

                    // for debugging check parsed
                    for (User u : users) {
                        Log.d("YourTAG", "User: " + u.getUserName());
                    }

                    // get a new adapter instance to display the results
                    UserAdapter userAdapter = new UserAdapter(getActivity(),
                            R.layout.users_row_item, users);

                    // get reference to the listView of the layout
                    ListView listview = (ListView) contentView
                            .findViewById(R.id.users_list_view);

                    // display the results to the listView
                    listview.setAdapter(userAdapter);

                    listview.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1,
                                                int position, long arg3) {

                            GlobalDataSingleton.getInstance().currId = users
                                    .get(position).getUserId();
                            GlobalDataSingleton.getInstance().currUserName = users
                                    .get(position).getUserName();
                            GlobalDataSingleton.getInstance().currFirstName = users
                                    .get(position).getFirstName();
                            GlobalDataSingleton.getInstance().currLastName = users
                                    .get(position).getLastName();

                            Log.d("ID: ",
                                    GlobalDataSingleton.getInstance().currId);
                            Log.d("Username: ",
                                    GlobalDataSingleton.getInstance().currUserName);
                            Log.d("First name: ",
                                    GlobalDataSingleton.getInstance().currFirstName);
                            Log.d("Last Name:",
                                    GlobalDataSingleton.getInstance().currLastName);

                            dialog = new Dialog(getActivity());
                            dialog.setContentView(R.layout.user_dialog);
                            dialog.setTitle(GlobalDataSingleton.getInstance().currUserName);

                            dialog.show();

                            Button dialogChatButton = (Button) dialog
                                    .findViewById(R.id.dialog_chat_button);

                            Button dialogNavigateButton = (Button) dialog
                                    .findViewById(R.id.dialog_navigate_button);

                            dialogChatButton
                                    .setOnClickListener(UsersListFragment.this);

                            dialogNavigateButton
                                    .setOnClickListener(UsersListFragment.this);

                        }
                    });

                } else {
                    // u got network failures
                    Log.e("ERROR", "REQUEST DENIED", e);
                    Toast.makeText(getActivity(), "Network error",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        Ion.with(getActivity()).load(url)//
                .asJsonObject()//
                .setCallback(futureCallBack);

        return contentView;

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.dialog_chat_button) {

            //Here i create a unique room name

            if (Integer.parseInt(GlobalDataSingleton.getInstance().getCurrId()) > Integer.parseInt(GlobalDataSingleton.getInstance().getId())) {

                uniqueRoomName = GlobalDataSingleton.getInstance().getCurrUserName() + GlobalDataSingleton.getInstance().getUsername();

                Log.e("if_uniqueRoomName", uniqueRoomName);


            } else {

                uniqueRoomName = GlobalDataSingleton.getInstance().getUsername() + GlobalDataSingleton.getInstance().getCurrUserName();

                Log.e("else_uniqueRoomName", uniqueRoomName);

            }

            GlobalDataSingleton.getInstance().setUniqueRoomName(uniqueRoomName);

            dialog.dismiss();

            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new MessagesListFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null).commit();

        } else if (v.getId() == R.id.dialog_navigate_button) {

            getUserCoordinates();

        }

    }

    private void getUserCoordinates() {
        if (mainActivity.latitude == null || mainActivity.longitude == null) {

            Toast.makeText(getActivity(), "No location specified by user",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        final GlobalDataSingleton instance = GlobalDataSingleton.getInstance();

        Runnable success = new Runnable() {

            @Override
            public void run() {

                String lat = instance.getCurrLat();
                String lng = instance.getCurrLong();

                if (lat != null && lng != null) {
                    showDirections(mainActivity.latitude,
                            mainActivity.longitude, lat, lng);
                } else {

                    Toast.makeText(getActivity(), "Null user coordinates",
                            Toast.LENGTH_SHORT).show();

                }
            }
        };

        Runnable failure = new Runnable() {

            @Override
            public void run() {

                Toast.makeText(getActivity(),
                        "Server failure! Cannot get user coordinates",
                        Toast.LENGTH_SHORT).show();

            }
        };

        instance.updateUserCoordinates(mainActivity, success, failure);

    }

    private void showDirections(String myLat, String myLong, String destLat,
                                String destLong) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + myLat + ","
                        + myLong + "&daddr=" + destLat + "," + destLong));
        startActivity(intent);

    }

}
