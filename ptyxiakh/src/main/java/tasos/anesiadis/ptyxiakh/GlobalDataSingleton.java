package tasos.anesiadis.ptyxiakh;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class GlobalDataSingleton {
    //the address for localhost on android is 10.0.2.2
    final static String GLOBAL_ADDRESS = "192.168.17.197:8080";
    final static String GLOBAL_SOCKET_ADDRESS = "nodejs-chat-thesis.herokuapp.com";

    // My data
    String id;
    String username;
    String firstname;
    String lastname;
    String address;
    String addressnum;
    String city;
    String email;
    String currentToken;

    // The other user's data
    String currFirstName;
    String currLastName;
    String currUserName;
    String currId;
    String currLat;
    String currLong;

    String currTeamId;
    String currTeamName;

    String uniqueRoomName;

    /**
     * Single instance of GlobalDataSingleton.
     */
    private static final GlobalDataSingleton instance = new GlobalDataSingleton();

    /**
     * Return the single instance.
     */
    public static GlobalDataSingleton getInstance() {
        return instance;
    }

    /**
     * Supress public default constructor to assure non-instantiation
     */
    private GlobalDataSingleton() {
        // Never invoked from outside the class
    }

    public void parseCurrUserJsonObject(JsonObject jsonObject) {

        id = jsonObject.get("Id").getAsString();
        username = jsonObject.get("UserName").getAsString();
        firstname = jsonObject.get("FirstName").getAsString();
        lastname = jsonObject.get("LastName").getAsString();
        address = jsonObject.get("Address").getAsString();
        addressnum = jsonObject.get("AddressNumber").getAsString();
        city = jsonObject.get("City").getAsString();
        email = jsonObject.get("email").getAsString();
        currentToken = jsonObject.get("currentToken").getAsString();

        Log.i("ID", id);
        Log.i("UserName", username);
        Log.i("FirstName", firstname);
        Log.i("LastName", lastname);
        Log.i("Address", address);
        Log.i("AddressNumber", addressnum);
        Log.i("City", city);
        Log.i("email", email);
        Log.i("currentToken", currentToken);

    }


    public void updateUserCoordinates(Context context, final Runnable success,
                                      final Runnable failure) {

        String url = "http://" + GlobalDataSingleton.GLOBAL_ADDRESS
                + "/hatalink/GetLocation?UserID=" + currId;

        FutureCallback<JsonObject> futureCallBack = new FutureCallback<JsonObject>() {

            @Override
            public void onCompleted(Exception e, JsonObject result) {

                if (e == null) {

                    currLat = result.get("latitude").getAsString();
                    currLong = result.get("longitude").getAsString();

                    success.run();

                } else {

                    failure.run();

                }
            }

        };

        Ion.with(context).load(url)//
                .asJsonObject()//
                .setCallback(futureCallBack);

    }

    public String getId() {

        return id;
    }

    public String getUsername() {

        return username;
    }

    public String getCurrUserName() {

        return currUserName;

    }

    public String getFirstname() {

        return firstname;
    }

    public String getLastname() {

        return lastname;
    }

    public String getAddress() {

        return address;
    }

    public String getAddressNum() {

        return addressnum;
    }

    public String getCity() {

        return city;
    }

    public String getEmail() {

        return email;
    }

    public String getCurrLat() {

        if (currLat != null && currLat.equals("null")) {

            return null;

        } else {

            return currLat;

        }
    }

    public String getCurrLong() {
        if (currLong != null && currLong.equals("null")) {

            return null;

        } else {

            return currLong;

        }
    }

    public String getCurrId() {

        return currId;

    }

    public String getUniqueRoomName() {

        return uniqueRoomName;

    }

    public void setUniqueRoomName(String uniqueRoomName) {

        this.uniqueRoomName = uniqueRoomName;

    }

    public void setCurrTeamId(String currTeamId) {

        this.currTeamId = currTeamId;

    }

    public String getCurrTeamId() {

        return currTeamId;

    }

    public void setCurrTeamName(String currTeamName) {

        this.currTeamName = currTeamName;

    }

    public String getCurrTeamName() {

        return currTeamName;

    }
}