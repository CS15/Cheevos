package org.relos.cheevos.app;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.relos.cheevos.R;
import org.relos.cheevos.misc.HelperClass;

/**
 * Register activity
 * <p/>
 * Created by Christian (ReloS) Soler on 11/26/2014.
 */
public class Register extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        final EditText etEmail = (EditText) findViewById(R.id.et_email);
        final EditText etPassword = (EditText) findViewById(R.id.et_password);
        final EditText etPasswordConfirm = (EditText) findViewById(R.id.et_confirm_password);
        final EditText etGamertag = (EditText) findViewById(R.id.et_gamertag);
        final Button btRegister = (Button) findViewById(R.id.bt_register);

        // animation
        ObjectAnimator.ofFloat(etEmail, "alpha", 0, 1).setDuration(1500).start();
        ObjectAnimator.ofFloat(etPassword, "alpha", 0, 1).setDuration(1500).start();
        ObjectAnimator.ofFloat(etPasswordConfirm, "alpha", 0, 1).setDuration(1500).start();
        ObjectAnimator.ofFloat(etGamertag, "alpha", 0, 1).setDuration(1500).start();
        ObjectAnimator.ofFloat(btRegister, "alpha", 0, 1).setDuration(1500).start();

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();
                final String passwordConfirm = etPasswordConfirm.getText().toString();
                final String gamertag = etGamertag.getText().toString();

                if (password.equals(passwordConfirm) && password.length() > 5) {
                    registerUser(email, password, gamertag);
                } else {
                    new AlertDialog.Builder(Register.this)
                            .setTitle("Error")
                            .setMessage("Password did not matched or was less than 6 characters in length.")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
            }
        });
    }

    private void registerUser(String email, String password, String gamertag) {

        ParseUser user = new ParseUser();
        user.setUsername(email);
        user.setPassword(password);
        user.setEmail(email);
        user.put("gamertag", gamertag);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Intent intent = new Intent(Register.this, MainActivity.class);
                    startActivity(intent);

                    finish();
                } else {
                    // log error
                    Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
