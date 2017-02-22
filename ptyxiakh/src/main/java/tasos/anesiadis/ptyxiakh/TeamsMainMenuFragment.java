package tasos.anesiadis.ptyxiakh;

import java.util.ArrayList;

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

public class TeamsMainMenuFragment extends Fragment implements OnClickListener {

    private View contentView;

    protected Button createTeamButton;

    protected ListView teamsMenuListView;

    private String url;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        contentView = inflater.inflate(R.layout.teams_main_menu_fragment,
                container, false);

        createTeamButton = (Button) contentView
                .findViewById(R.id.create_team_button);

        teamsMenuListView = (ListView) contentView
                .findViewById(R.id.teams_menu_list_view);

        FutureCallback<JsonObject> futureCallBack = new FutureCallback<JsonObject>() {

            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if (e == null) {
                    // success on the request

                    // get the jsonAray
                    JsonArray jsonArray = result.get("Teams").getAsJsonArray();

                    final ArrayList<Team> teams = new ArrayList<Team>();

                    for (JsonElement je : jsonArray) { // scan the json array
                        // parse each element
                        Team t = new Team();
                        t.parseJsonObject(je.getAsJsonObject());

                        // add elements to object array one by one
                        teams.add(t);
                    }

                    // for debugging check parsed
                    for (Team t : teams) {
                        Log.d("YourTAG", "User: " + t.getTeamName());
                    }

                    // get a new adapter instance to display the results
                    TeamsAdapter teamsAdapter = new TeamsAdapter(getActivity(),
                            R.layout.team_name_row_item, teams);

                    // get reference to the listView of the layout
                    ListView listview = (ListView) contentView
                            .findViewById(R.id.teams_menu_list_view);

                    // display the results to the listView
                    listview.setAdapter(teamsAdapter);

                    listview.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1,
                                                int position, long arg3) {

                            GlobalDataSingleton.getInstance().setCurrTeamId(teams
                                    .get(position).getTeamId());

                            GlobalDataSingleton.getInstance().setCurrTeamName(teams
                                    .get(position).getTeamName());


                            Log.e("TEAM_ID",
                                    GlobalDataSingleton.getInstance().getCurrTeamId());
                            Log.e("TEAM_NAME",
                                    GlobalDataSingleton.getInstance().getCurrTeamName());


                            getFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container,
                                            new TeamMessagesListFragment())
                                    .setTransition(
                                            FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .addToBackStack("teamsMenuToTeamChat")
                                    .commit();

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

        url = "http://" + GlobalDataSingleton.GLOBAL_ADDRESS
                + "/hatalink/Teams?Action=teams&UserID="
                + GlobalDataSingleton.getInstance().id;

        Ion.with(getActivity()).load(url)//
                .asJsonObject()//
                .setCallback(futureCallBack);

        createTeamButton.setOnClickListener(this);

        return contentView;

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.create_team_button) {

            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new CreateTeamFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack("teamsMenuToCreateTeam").commit();

        }
    }

}
