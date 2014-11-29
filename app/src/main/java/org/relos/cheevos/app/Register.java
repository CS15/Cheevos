package org.relos.cheevos.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONObject;
import org.relos.cheevos.R;
import org.relos.cheevos.misc.HelperClass;
import org.relos.cheevos.misc.SingletonVolley;

import java.util.HashMap;
import java.util.Map;

/**
 * Register activity
 * <p/>
 * Created by Christian (ReloS) Soler on 11/26/2014.
 */
public class Register extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_register, container, false);

        // set title
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("Register");

        final EditText etEmail = (EditText) view.findViewById(R.id.et_email);
        final EditText etPassword = (EditText) view.findViewById(R.id.et_password);
        final EditText etPasswordConfirm = (EditText) view.findViewById(R.id.et_confirm_password);
        final EditText etGamertag = (EditText) view.findViewById(R.id.et_gamertag);
        final Button btRegister = (Button) view.findViewById(R.id.bt_register);

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
                    new AlertDialog.Builder(getActivity())
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

        return view;
    }

    private void registerUser(String email, String password, String gamertag) {
        getXboxId(email, password, gamertag);
    }

    private void getXboxId(final String email, final String password, final String gamertag){

        String url = "https://xboxapi.com/v2/xuid/" + gamertag;

        RequestQueue queue = SingletonVolley.getRequestQueque();

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {

                ParseUser user = new ParseUser();
                user.setUsername(email);
                user.setPassword(password);
                user.setEmail(email);
                user.put("gamertag", gamertag);
                user.put("xboxId", response);

                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getActivity(), "Successfully register and logged in", Toast.LENGTH_LONG).show();

                            HelperClass.reloadActivity(getActivity());
                        } else {
                            // log error
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("X-AUTH", "c298a7edee735d5559a219b0020a60fb9bb657dc");
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }
}
