package tasos.anesiadis.ptyxiakh;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class CheckBoxAdapter extends ArrayAdapter<User> {
    ArrayList<User> userList;
    LayoutInflater vi;
    int Resource;
    ViewHolder holder;

    public CheckBoxAdapter(Context context, int resource, ArrayList<User> users) {
        super(context, resource, users);
        vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource = resource;
        userList = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            holder = new ViewHolder();
            v = vi.inflate(Resource, null);

            holder.firstNameTextView = (TextView) v
                    .findViewById(R.id.create_team_row_item_name);
            holder.lastNameTextView = (TextView) v
                    .findViewById(R.id.create_team_row_item_surname);
            holder.checkbox = (CheckBox) v.findViewById(R.id.checkbox);

            v.setTag(holder);

            holder.checkbox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    CheckBox cb = (CheckBox) v;

                    User u = (User) cb.getTag();

                    Log.e("USER CLICKED:", u.getUserName());

                    Log.e("CHECKBOX CHECKED:", "" + cb.isChecked());

                    u.setSelected(cb.isChecked());


                }
            });

        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder.firstNameTextView.setText(userList.get(position).getFirstName());
        holder.lastNameTextView.setText(userList.get(position).getLastName());
        holder.checkbox.setTag(userList.get(position));

        return v;

    }

    static class ViewHolder {

        public TextView firstNameTextView;
        public TextView lastNameTextView;
        public CheckBox checkbox;

    }

    public ArrayList<User> getUserList() {
        return userList;
    }

}