package com.example.sif.facebooklogindemo;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Reference link for Facebook Login :- http://www.simplifiedandroid.info/how-to-add-facebook-login-to-android-app/
 */
public class MainActivity extends AppCompatActivity {

    private LoginButton bFbloginButton;
    private CircleImageView circleImageView;
    private TextView t_name, t_email;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bFbloginButton = findViewById(R.id.bt_fb_login_button);
        circleImageView = findViewById(R.id.iv_profile_image);
        t_name = findViewById(R.id.tv_name);
        t_email = findViewById(R.id.tv_email);

        callbackManager = CallbackManager.Factory.create();

        /** Permissions are required to get data from FB */
        bFbloginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        /** checks if the user was logged-in or not, to avoid getting logged-out when app is removed from the memory */
        checkLoginStatus();

        bFbloginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken == null) {
                t_name.setText("");
                t_email.setText("");
                circleImageView.setImageResource(0);
                Toast.makeText(MainActivity.this, "User is logged out", Toast.LENGTH_SHORT).show();
            } else {
                loadUserProfile(currentAccessToken);
            }
        }
    };

    /**
     * This method gets the data from facebook such as publicProfile,etc using GraphRequest calls.
     *
     * @param newAccessToken : Fresh user token.
     */
    private void loadUserProfile(AccessToken newAccessToken) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String firstName = object.getString("first_name");
                    String lastName = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");

                    String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";

                    t_email.setText(email);
                    t_name.setText(firstName + " " + lastName);
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.dontAnimate();

                    Glide.with(MainActivity.this).load(image_url).into(circleImageView);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    /**
     * checks if the user was logged-in or not, to avoid getting logged-out when app is removed from the memory
     */
    private void checkLoginStatus() {
        if (AccessToken.getCurrentAccessToken() != null) {
            loadUserProfile(AccessToken.getCurrentAccessToken());
        }
    }
}
