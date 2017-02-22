package tasos.anesiadis.ptyxiakh;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

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
import android.widget.Toast;

public class RegisterFragment extends Fragment implements OnClickListener {

    private View contentView;

    protected EditText nameEditText;
    protected EditText surnameEditText;
    protected EditText usernameEditText;
    protected EditText passwordEditText;
    protected EditText confPasswordEditText;
    protected EditText emailEditText;
    protected EditText confEmailEditText;

    protected Button createAccountButton;

    String[] textFieldArray;

    String url;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        contentView = inflater.inflate(R.layout.register_fragment, container,
                false);

        nameEditText = (EditText) contentView.findViewById(R.id.name_input);
        surnameEditText = (EditText) contentView
                .findViewById(R.id.surname_input);
        usernameEditText = (EditText) contentView
                .findViewById(R.id.username_input);
        passwordEditText = (EditText) contentView
                .findViewById(R.id.password_input);
        confPasswordEditText = (EditText) contentView
                .findViewById(R.id.confirm_password_input);
        emailEditText = (EditText) contentView.findViewById(R.id.email_input);
        confEmailEditText = (EditText) contentView
                .findViewById(R.id.confirm_email_input);

        createAccountButton = (Button) contentView
                .findViewById(R.id.register_user_button);

        textFieldArray = new String[7];

        createAccountButton.setOnClickListener(this);

        return contentView;

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register_user_button) {

            textFieldArray[0] = nameEditText.getText().toString();
            textFieldArray[1] = surnameEditText.getText().toString();
            textFieldArray[2] = usernameEditText.getText().toString();
            textFieldArray[3] = passwordEditText.getText().toString();
            textFieldArray[4] = confPasswordEditText.getText().toString();
            textFieldArray[5] = emailEditText.getText().toString();
            textFieldArray[6] = confEmailEditText.getText().toString();

            int emptyFields = 0;

            for (int index = 0; index < 7; index++) {
                if (textFieldArray[index].equals("")) {

                    emptyFields++;

                }

            }

            if ((emptyFields != 0)
                    && (textFieldArray[3].equals(textFieldArray[4]))
                    && (textFieldArray[5].equals(textFieldArray[6]))) {

                Toast.makeText(getActivity(),
                        "Please check all the required fields!",
                        Toast.LENGTH_SHORT).show();

            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        getActivity());

                // Add message and title
                builder.setMessage(R.string.register_prompt).setTitle(
                        R.string.register_user);

                // Add the buttons
                builder.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                sendCredentials();

                                getFragmentManager().popBackStackImmediate();
                                Toast.makeText(
                                        getActivity(),
                                        "User registration complete! Enter username and password to Login",
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

            }

        }

    }

    public void sendCredentials() {

        FutureCallback<JsonObject> futureCallBack = new FutureCallback<JsonObject>() {

            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if (e == null) {
                    // success on the request

                    String status = result.get("registration_status")
                            .getAsString();
                    if (status != null && status.equals("complete")) {

                        Log.d("CONNECTION", "Credentials sent!");

                    } else {
                        // Credentials not sent
                        // handle the UI here

                        Toast.makeText(
                                getActivity(),
                                result.get("registration_status").getAsString(),
                                Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // u got network failures
                    Log.e("CONNECTION", "ERROR, cannot connect to server", e);
                    Toast.makeText(getActivity(), "Network error",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        url = "http://" + GlobalDataSingleton.GLOBAL_ADDRESS
                + "/hatalink/Register?UserName=" + textFieldArray[2]
                + "&Password=" + textFieldArray[3] + "&FirstName="
                + textFieldArray[0] + "&LastName=" + textFieldArray[1]
                + "&email=" + textFieldArray[5];

        Log.e("REGISTRATION_URL = ", url);

        Ion.with(getActivity()).load(url)//
                .asJsonObject()//
                .setCallback(futureCallBack);

    }
}
