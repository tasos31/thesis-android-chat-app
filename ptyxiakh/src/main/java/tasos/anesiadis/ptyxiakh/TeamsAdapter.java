package tasos.anesiadis.ptyxiakh;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TeamsAdapter extends ArrayAdapter<Team> {
    ArrayList<Team> teamList;
    LayoutInflater vi;
    int Resource;
    ViewHolder holder;

    public TeamsAdapter(Context context, int resource, ArrayList<Team> teams) {
        super(context, resource, teams);
        vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource = resource;
        teamList = teams;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            holder = new ViewHolder();

            Log.e("TEAM CREATOR  ID= ", teamList.get(position)
                    .getTeamCreator());

            Log.e("MY ID = ", GlobalDataSingleton.getInstance().getId());

            if (teamList.get(position).getTeamCreator() == GlobalDataSingleton
                    .getInstance().getId()) {

                v = vi.inflate(R.layout.team_name_w_del_row_item, null);

            } else {

                v = vi.inflate(Resource, null);

            }
            holder.teamNameTextView = (TextView) v
                    .findViewById(R.id.team_name_row_text_view);

            v.setTag(holder);

        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder.teamNameTextView.setText(teamList.get(position).getTeamName());

        return v;

    }

    static class ViewHolder {

        public TextView teamNameTextView;

    }

}