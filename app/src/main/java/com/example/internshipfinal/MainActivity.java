package com.example.internshipfinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    //Facebook Starts
    public LoginButton loginButton;
    public ImageView circleImageView;
    public TextView txtname;
    public TextView txtemail;
    public CallbackManager callbackManager;
    public TextView register;
    public CardView cardView;
    public EditText user,pass;

    //Facebook Ends

    //Google Starts

    int RC_SIGN_IN = 0;
    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;

    //Google Ends

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

                //Facebook Starts
    register = findViewById(R.id.textView2);
    cardView = findViewById(R.id.cardView);
    user = findViewById(R.id.editText);
    pass = findViewById(R.id.editText2);
      callbackManager = CallbackManager.Factory.create();

          txtname = findViewById(R.id.pname);
        txtemail= findViewById(R.id.pemail);
        circleImageView = findViewById(R.id.profile_pic);

        signInButton = findViewById(R.id.signin_button);

        loginButton= findViewById(R.id.loginb);

        checkloginstatus();
        loginButton.setPermissions(Arrays.asList("email","public_profile"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
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

        //Facebook Ends


        //Google Starts

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();

                mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

                signInButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        signIn();
                    }
                });



    }


                     //Google Starts

            private void signIn() {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }

            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                callbackManager.onActivityResult(requestCode, resultCode, data);
                super.onActivityResult(requestCode, resultCode, data);
                if (requestCode == RC_SIGN_IN) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    handleSignInResult(task);
                }
            }




            private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
                try {
                    GoogleSignInAccount account = completedTask.getResult(ApiException.class);
                    startActivity(new Intent(MainActivity.this, googlesignedin.class));
                } catch (ApiException e) {
                    Log.w("Google Sign In Error", "signInResult:failed code=" + e.getStatusCode());
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected void onStart() {
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
                if(account != null) {
                    startActivity(new Intent(MainActivity.this, googlesignedin.class));
                }
                super.onStart();
            }
                     //Google Ends


                //Facebook Starts


        AccessTokenTracker tokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken)
            {
                if(currentAccessToken==null)
                {
                    txtname.setText("");
                    txtemail.setText("");

                    circleImageView.setImageResource(0);
                    Toast.makeText(MainActivity.this,"User Logged Out",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this,MainActivity.class);
                    startActivity(intent);
                }
                else{
                    loaduserprofile(currentAccessToken);
                }
            }
    };

    private void loaduserprofile(AccessToken newaccessToken)
        {
           GraphRequest request = GraphRequest.newMeRequest(newaccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response)
                    {
                        try {

                            String first_name = object.getString("first_name");
                            String last_name = object.getString("last_name");
                            String email = object.getString("email");
                            String id = object.getString("id");

                            String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";
                            txtname.setText(first_name + " " + last_name);
                            txtemail.setText(email);
                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions.dontAnimate();
                            txtname.setVisibility(View.VISIBLE);
                            txtemail.setVisibility(View.VISIBLE);
                            signInButton.setVisibility(View.GONE);
                            register.setVisibility(View.GONE);
                            cardView.setVisibility(View.GONE);
                            user.setVisibility(View.GONE);
                            pass.setVisibility(View.GONE);
                            Glide.with(MainActivity.this).load(image_url).into(circleImageView);

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields","first_name,last_name,email,id");
                request.setParameters(parameters);
                request.executeAsync();


    }

                private void checkloginstatus(){
                    if(AccessToken.getCurrentAccessToken()!= null){
                        loaduserprofile(AccessToken.getCurrentAccessToken());
                }
            }



                 //Facebook Ends
}