package com.example.monster.proje579code;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.GraphResponse;
import com.facebook.GraphRequest;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;





    public class MainActivity extends AppCompatActivity {
        private static int flag = 0;
        private JSONObject jsonObj;
        private final int REQUEST_ENABLE_BT = 1;
        private Button scanbutton;
        public static BluetoothAdapter mBluetoothAdapter;
        private Handler mHandler;
        private TextView textView;
        private static final String EMAIL = "email";
        private static int logged_in = 0;
        static final String STATE_LOGIN = "0";
        AccessTokenTracker accessTokenTracker;
        CallbackManager callbackManager;
        LoginButton loginButton;
        TextView FacebookDataTextView;
        ImageView imageViewprofile;
        private static String fbid = "";
        Button findfriendbutton;
        public static final String EXTRA_MESSAGE = "com.example.monster.proj579code.MESSAGE";


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            FacebookSdk.sdkInitialize(com.example.monster.proje579code.MainActivity.this);
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
            FacebookDataTextView = (TextView) findViewById(R.id.TextView1);
            callbackManager = CallbackManager.Factory.create();
            loginButton = (LoginButton) findViewById(R.id.login_button);
            findfriendbutton = findViewById(R.id.butseefriend);
            if (logged_in == 0) {
                findfriendbutton.setVisibility(View.GONE);
            }
            imageViewprofile = findViewById(R.id.imageview);
            //loginButton.setReadPermissions(Arrays.asList(EMAIL));
            //loginButton.setReadPermissions(Arrays.asList("public_profile"));
            loginButton.setReadPermissions(Arrays.asList(EMAIL, "user_status", "public_profile", "user_friends"));
            if (AccessToken.getCurrentAccessToken() != null) {
                GraphLoginRequest(AccessToken.getCurrentAccessToken());

                // If already login in then show the Toast.
                Toast.makeText(com.example.monster.proje579code.MainActivity.this, "Already logged in", Toast.LENGTH_SHORT).show();

            } else {

                // If not login in then show the Toast.
                Toast.makeText(com.example.monster.proje579code.MainActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
            // Callback registration
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    // App code
                    Log.e("DD", "**************************");
                    findfriendbutton.setVisibility(View.VISIBLE); //To set visible
                    logged_in = 1;

                    Log.e("DD", loginResult.getAccessToken().toString());
                    Log.e("DD", AccessToken.getCurrentAccessToken().toString());

                    GraphLoginRequest(loginResult.getAccessToken());
                    // GraphFriendRequest(loginResult.getAccessToken());
                    Log.e("DD:", loginResult.toString());
                }


                @Override
                public void onCancel() {
                    // App code
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                }
            });
            // Detect user is login or not. If logout then clear the TextView and delete all the user info from TextView.
            accessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken accessToken, AccessToken accessToken2) {
                    if (accessToken2 == null) {

                        // Clear the TextView after logout.
                        Log.e("DD", "goneeeeeeeeeeeeeeeeeeeeeeeee2222");
                        FacebookDataTextView.setText("");
                        imageViewprofile.setImageIcon(null);
                        findfriendbutton.setVisibility(View.GONE);
                        logged_in = 0;

                    }
                }
            };


            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Intent enableBtlntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtlntent, REQUEST_ENABLE_BT);
            }


        }

        // Method to access Facebook User Data.
        protected void GraphLoginRequest(final AccessToken accessToken) {
            GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {

                            try {
                                Log.e("DD", "trying ------------------------");
                                Log.e("DD", graphResponse.toString());
                                Log.e("DD", jsonObject.getString("id"));
                                Log.e("DD", jsonObject.getString("first_name"));
                                Log.e("DD", jsonObject.toString());

                                // Adding all user info one by one into TextView.
                                fbid = jsonObject.getString("id");
                                FacebookDataTextView.setText("ID: " + jsonObject.getString("id"));
                                Picasso.with(com.example.monster.proje579code.MainActivity.this).load("https://graph.facebook.com/" + jsonObject.getString("id") + "/picture?type=large").into(imageViewprofile);
                                //Picasso.with(MainActivity.this).load("https://graph.facebook.com/" + jsonObject.getString("id")+ "/picture?type=large").into();

                                FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nName : " + jsonObject.getString("name"));

                                FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nFirst name : " + jsonObject.getString("first_name"));

                                FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nLast name : " + jsonObject.getString("last_name"));

                                FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nEmail : " + jsonObject.getString("email"));

                                FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nGender : " + jsonObject.getString("gender"));

                                FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nLink : " + jsonObject.getString("link"));

                                FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nTime zone : " + jsonObject.getString("timezone"));

                                FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nLocale : " + jsonObject.getString("locale"));

                                FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nUpdated time : " + jsonObject.getString("updated_time"));

                                FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nVerified : " + jsonObject.getString("verified"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            GraphFriendRequest(accessToken);
                        }
                    });

            Bundle bundle = new Bundle();
            bundle.putString(
                    "fields",
                    "id,name,link,email,gender,last_name,first_name,locale,timezone,updated_time,verified"
            );
            graphRequest.setParameters(bundle);
            graphRequest.executeAsync();

        }


        protected void GraphFriendRequest(AccessToken accessToken) {

            GraphRequest request = GraphRequest.newGraphPathRequest(
                    accessToken,
                    "/" + fbid + "/friends",
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            Log.e("DD", "tyring_+_+_+_+_+_+_+_+");
                            Log.e("DD", response.toString());
                        }
                    });


            Bundle bundle = new Bundle();
            bundle.putString(
                    "fields",
                    "data"
            );
            request.setParameters(bundle);
            request.executeAsync();
        }

        public void seeFriends(View view) {
            Intent intent = new Intent(this, SeeFriendsActivity.class);
            //EditText editText = (EditText) findViewById(R.id.editText);
            //String message = editText.getText().toString();
            //intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

        @Override
        protected void onResume() {
            super.onResume();
            //findfriendbutton.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSaveInstanceState(Bundle savedInstanceState) {
            // Save the user's current game state
            savedInstanceState.putInt(STATE_LOGIN, logged_in);
            super.onSaveInstanceState(savedInstanceState);
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            // Don't forget to unregister the ACTION_FOUND receiver.
            //       unregisterReceiver(mReceiver);
        }
    }
