package org.relos.cheevos.app;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.relos.cheevos.R;

/**
 * Login page
 *
 * Created by Christian (ReloS) Soler on 11/26/2014.
 */
public class Login extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        final EditText etEmail = (EditText) findViewById(R.id.et_email);
        final EditText etPassword = (EditText) findViewById(R.id.et_password);
        final Button btLogin = (Button) findViewById(R.id.bt_login);

        // animation
        ObjectAnimator.ofFloat(etEmail, "alpha", 0, 1).setDuration(1500).start();
        ObjectAnimator.ofFloat(etPassword, "alpha", 0, 1).setDuration(1500).start();
        ObjectAnimator.ofFloat(btLogin, "alpha", 0, 1).setDuration(1500).start();

        if (isLoggedIn()){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            finish();
        }

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if(!email.isEmpty() && !password.isEmpty() && password.length() > 5){
                    login(email, password);
                } else {
                    new AlertDialog.Builder(Login.this)
                            .setTitle("Error")
                            .setMessage("Fields cannot be empty and password most be longer than 6 characters.")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
            }
        });

        findViewById(R.id.tv_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
    }

    private void login(String email, String password) {
        ParseUser.logInInBackground(email, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);

                    finish();
                } else {
                    // log error
                    Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean isLoggedIn(){
        return (ParseUser.getCurrentUser() != null);
    }
}
