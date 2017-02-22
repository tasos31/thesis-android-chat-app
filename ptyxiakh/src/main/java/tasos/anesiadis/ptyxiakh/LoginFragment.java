package tasos.anesiadis.ptyxiakh;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class LoginFragment extends Fragment implements OnClickListener {


    private View contentView;

    private Button loginButton;
    private TextView signUpTextView;

    private String username;
    private String password;
    private EditText usernameEditText;
    private EditText passwordEditText;

    MainActivity mainActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        contentView = inflater.inflate(R.layout.login_fragment, container,
                false);

        mainActivity = (MainActivity) getActivity();

        loginButton = (Button) contentView.findViewById(R.id.login_button);

        signUpTextView = (TextView) contentView
                .findViewById(R.id.sign_up_text_view);

        usernameEditText = (EditText) contentView
                .findViewById(R.id.username_input);

        passwordEditText = (EditText) contentView
                .findViewById(R.id.password_input);

        loginButton.setOnClickListener(this);
        signUpTextView.setOnClickListener(this);


        return contentView;

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.sign_up_text_view) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new RegisterFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack("loginToRegister").commit();

        } else if (v.getId() == R.id.login_button) {

            // loginButton.setClickable(false);

            username = usernameEditText.getText().toString();

            username.replaceAll("[^A-Za-z0-9 ]", "");

            password = passwordEditText.getText().toString();

            password.replaceAll("[^A-Za-z0-9 ]", "");

            String url = "http://" + GlobalDataSingleton.GLOBAL_ADDRESS + "/hatalink/Login?UserName="
                    + username + "&Password=" + password;

            Log.e("ION_URL", url);

            FutureCallback<JsonObject> futureCallBack = new FutureCallback<JsonObject>() {

                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    if (e == null) {
                        // success on the request

                        String loginStatus = result.get("Login_status")
                                .getAsString();
                        if (loginStatus != null
                                && loginStatus.equals("success")) {
                            // parse the user json object using the method of
                            // the Singleton Object.
                            GlobalDataSingleton.getInstance()
                                    .parseCurrUserJsonObject(
                                            result.get("user")
                                                    .getAsJsonObject());

                            //
                            // now handle the UI here

                            Log.d("CONNECTION",
                                    "Got the user now start the fragment");

                            getFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container,
                                            new MainMenuFragment())
                                    .setTransition(
                                            FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .addToBackStack(null).commit();

                            Log.d("CONNECTION",
                                    "Finished the fragment transaction");

                            mainActivity.userLoggedIn = true;

                        } else {
                            // wrong user name or pass
                            // handle the UI here

                            Toast.makeText(getActivity(),
                                    "Invalid username or password",
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

            Ion.with(getActivity()).load(url)//
                    .asJsonObject()//
                    .setCallback(futureCallBack);

        }

    }

}
