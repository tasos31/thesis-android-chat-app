package tasos.anesiadis.ptyxiakh;

import com.koushikdutta.ion.Ion;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.app.AlertDialog;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainMenuFragment extends Fragment implements OnClickListener {

    private View contentView;
    TextView nameTextView;
    TextView fullNameTextView;
    TextView emailTextView;
    ImageView currUserImageView;
    Button chatButton;
    Button teamsButton;
    Button aboutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        contentView = inflater.inflate(R.layout.main_menu_fragment, container,
                false);

        chatButton = (Button) contentView.findViewById(R.id.friends_button);

        teamsButton = (Button) contentView.findViewById(R.id.teams_button);

        aboutButton = (Button) contentView.findViewById(R.id.about_button);

        nameTextView = (TextView) contentView.findViewById(R.id.name_text_view);

        nameTextView.setText(GlobalDataSingleton.getInstance().getUsername());

        fullNameTextView = (TextView) contentView.findViewById(R.id.full_name_text_view);

        String fullName = "(" + GlobalDataSingleton.getInstance().getFirstname() + " " + GlobalDataSingleton.getInstance().getLastname() + ")";

        fullNameTextView.setText(fullName);

        emailTextView = (TextView) contentView.findViewById(R.id.email_text_view);

        emailTextView.setText(GlobalDataSingleton.getInstance().getEmail());

        currUserImageView = (ImageView) contentView
                .findViewById(R.id.user_image);

        Ion.with(currUserImageView)
                .placeholder(R.drawable.user_placeholder)
                .error(R.drawable.error)

                .load("http://www.pclodge.com/wp-content/uploads/2014/08/placeholder.png");

        chatButton.setOnClickListener(this);
        teamsButton.setOnClickListener(this);
        aboutButton.setOnClickListener(this);

        return contentView;

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.friends_button) {

            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new UsersListFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack("mainMenuToUserList").commit();

        } else if (v.getId() == R.id.teams_button) {

            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new TeamsMainMenuFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack("mainMenuToTeamsMenu").commit();

        } else if (v.getId() == R.id.about_button) {

            AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
                    getActivity());

            // Add message and title
            builder.setMessage(R.string.about_text).setTitle(
                    R.string.about_title);

            AlertDialog dialog = builder.create();

            dialog.show();

        }

    }

}
