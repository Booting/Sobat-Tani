package com.sobattani;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.sobattani.Utils.FontCache;
import com.sobattani.Utils.Referensi;
import org.json.JSONObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class LoginActivity extends Activity {
    private TextView txtText1, txtText2, txtText3;
    private Typeface fontOldStandard;
    private ProgressBar progressBar;
    private SharedPreferences sobattaniPref;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_fb_login);

        sobattaniPref 	= getSharedPreferences(Referensi.PREF_NAME, Activity.MODE_PRIVATE);
        fontOldStandard = FontCache.get(this, "DroidSans");
        txtText1        = (TextView) findViewById(R.id.txtText1);
        txtText2        = (TextView) findViewById(R.id.txtText2);
        txtText3        = (TextView) findViewById(R.id.txtText3);
        progressBar     = (ProgressBar) findViewById(R.id.progressBar);
        loginButton     = (LoginButton) findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();

        txtText1.setTypeface(fontOldStandard);
        txtText2.setTypeface(fontOldStandard);
        txtText3.setTypeface(fontOldStandard);

        if (!sobattaniPref.getString("UserId", "").equals("")) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.fiveapps.android", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        // Callback registration
        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject me, GraphResponse response) {
                        if (response.getError() != null) {
                            // handle error
                        } else {
                            SharedPreferences.Editor editor = sobattaniPref.edit();
                            editor.putString("UserId", me.optString("id"));
                            editor.putString("UserName", me.optString("name"));
                            editor.putString("Email", me.optString("email"));
                            editor.commit();

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email,picture");
                request.setParameters(parameters);
                request.executeAsync();
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
        loginButton.setPadding(50, 35, 50, 35);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
    }
}
