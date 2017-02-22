package tasos.anesiadis.ptyxiakh;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class CreateTeamFragment extends Fragment implements OnClickListener {

    private View contentView;

    Button createButton;

    ListView usersListView;

    EditText editText;

    String thisUserId = GlobalDataSingleton.getInstance().getId();

    String getUsersUrl;

    String teamName;

    String selectedUsers;

    String createTeamUrl;

    private CheckBoxAdapter checkBoxAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        contentView = inflater.inflate(R.layout.create_team_fragment,
                container, false);

        createButton = (Button) contentView
                .findViewById(R.id.create_this_team_button);

        createButton.setOnClickListener(this);

        usersListView = (ListView) contentView
                .findViewById(R.id.create_team_list_view);

        usersListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        editText = (EditText) contentView
                .findViewById(R.id.team_name_edit_text);

        getUsers();

        return contentView;

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.create_this_team_button) {

            teamName = editText.getText().toString();

            teamName = teamName.replaceAll("[^A-Za-z0-9 ]", "");

            getSelectedItems();

            if ((selectedUsers.length() >= 3) && (!teamName.equals(""))) {

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        getActivity());

                // Add message and title
                builder.setMessage(R.string.create_team_dialog_prompt)
                        .setTitle(R.string.create_team_dialog_title);

                // Add the buttons
                builder.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                createTeam();

                                getFragmentManager().popBackStackImmediate();
                                Toast.makeText(getActivity(),
                                        "Team created successfully!",
                                        Toast.LENGTH_SHORT).show();

                            }
                        });
                builder.setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });

                AlertDialog dialog = builder.create();

                dialog.show();

            } else {

                Toast.makeText(getActivity(),
                        "Please type a team name and select two or more users",
                        Toast.LENGTH_SHORT).show();

            }

        }

    }

    public void createTeam() {

        FutureCallback<JsonObject> futureCallBack2 = new FutureCallback<JsonObject>() {

            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if (e == null) {
                    // success on the request

                    String teamCreationStatus = result.get("Status")
                            .getAsString();
                    if (teamCreationStatus != null
                            && teamCreationStatus.equals("Success")) {

                        Log.d("CREATE_NEW_TEAM", "Team created successfully!");

                    } else {
                        // Team not created
                        // handle the UI here

                        Toast.makeText(getActivity(), "Team not created!",
                                Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // u got network failures
                    Log.e("CONNECTION", "ERROR", e);
                    Toast.makeText(getActivity(), "Network error",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        Log.e("Url Data: ", "team name=" + teamName + "creatorID=" + thisUserId
                + "team member ids" + selectedUsers);

        Log.i("TEAM_NAME", "Value before encoding = " + teamName);

        if (teamName.contains(" ")) {

            try {
                teamName = URLEncoder.encode(teamName, "utf-8");
            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();
            }

        }

        Log.i("TEAM_NAME", "Value after encoding = " + teamName);

        createTeamUrl = "http://" + GlobalDataSingleton.GLOBAL_ADDRESS
                + "/hatalink/Teams?Action=create&TeamName=" + teamName
                + "&CreatorID=" + thisUserId + "&UserIDS=" + selectedUsers;

        Log.i("URL TO CREATE TEAM:", createTeamUrl);

        Ion.with(getActivity()).load(createTeamUrl)//
                .asJsonObject()//
                .setCallback(futureCallBack2);

    }

    public void getUsers() {

        FutureCallback<JsonObject> futureCallBack = new FutureCallback<JsonObject>() {

            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if (e == null) {
                    // success on the request

                    // get the jsonAray
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
                    checkBoxAdapter = new CheckBoxAdapter(getActivity(),
                            R.layout.checkbox_usrs_row_item, users);

                    // display the results to the listView
                    usersListView.setAdapter(checkBoxAdapter);

                } else {
                    // u got network failures
                    Log.e("ERROR", "REQUEST DENIED", e);
                    Toast.makeText(getActivity(), "Network error",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        getUsersUrl = "http://" + GlobalDataSingleton.GLOBAL_ADDRESS
                + "/hatalink/GetUsers?UserID=" + thisUserId;

        Ion.with(getActivity()).load(getUsersUrl)//
                .asJsonObject()//
                .setCallback(futureCallBack);

    }

    public void getSelectedItems() {

        ArrayList<User> mUserList = (ArrayList<User>) checkBoxAdapter
                .getUserList();

        selectedUsers = "";

        for (int i = 0; i < mUserList.size(); i++) {

            Log.e("SelectedItemsArraylist:", mUserList.get(i)
                    .getFirstName()
                    + " "
                    + mUserList.get(i).getLastName()
                    + " "
                    + mUserList.get(i).isSelected()
                    + " "
                    + mUserList.get(i).getUserId());

            if (mUserList.get(i).isSelected()) {

                if (i == 0) {

                    selectedUsers += mUserList.get(i).getUserId();

                } else {

                    selectedUsers += "," + mUserList.get(i).getUserId();

                }
            }

        }

        Log.e("Selected variable:", selectedUsers);

    }
}
