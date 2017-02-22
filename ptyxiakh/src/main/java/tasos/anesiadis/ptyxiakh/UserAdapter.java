package tasos.anesiadis.ptyxiakh;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class UserAdapter extends ArrayAdapter<User> {
    ArrayList<User> userList;
    LayoutInflater vi;
    int Resource;
    ViewHolder holder;

    public UserAdapter(Context context, int resource, ArrayList<User> users) {
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
                    .findViewById(R.id.row_item_name);
            holder.lastNameTextView = (TextView) v
                    .findViewById(R.id.row_item_surname);

            v.setTag(holder);

        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder.firstNameTextView.setText(userList.get(position).getFirstName());
        holder.lastNameTextView.setText(userList.get(position).getLastName());

        return v;

    }

    static class ViewHolder {

        public TextView firstNameTextView;
        public TextView lastNameTextView;

    }

}