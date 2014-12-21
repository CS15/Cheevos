package org.cs15.xchievements.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.cs15.xchievements.R;
import org.cs15.xchievements.Repository.Database;
import org.cs15.xchievements.misc.HelperClass;

/**
 * Login page
 * <p/>
 * Created by Christian (ReloS) Soler on 11/26/2014.
 */
public class Login extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fragment layout
        View view = inflater.inflate(R.layout.frag_login, container, false);

        // set title
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("Login");

        final EditText etEmail = (EditText) view.findViewById(R.id.et_email);
        final EditText etPassword = (EditText) view.findViewById(R.id.et_password);
        final Button btLogin = (Button) view.findViewById(R.id.bt_login);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (!email.isEmpty() && !password.isEmpty() && password.length() > 5) {
                    new Database().login(email, password, new Database.ILogin() {
                        @Override
                        public void onSuccess(String message) {
                            HelperClass.toast(getActivity(), message);
                            HelperClass.reloadActivity(getActivity());
                        }

                        @Override
                        public void onError(String error) {
                            HelperClass.toast(getActivity(), error);
                        }
                    });
                } else {
                    new AlertDialog.Builder(getActivity())
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

        view.findViewById(R.id.tv_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Register frag = new Register();

                FragmentTransaction fragTrans = getActivity().getSupportFragmentManager().beginTransaction();
                fragTrans.setCustomAnimations(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left, R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                fragTrans.addToBackStack(null);
                fragTrans.replace(R.id.container, frag);
                fragTrans.commit();
            }
        });

        // return view
        return view;
    }
}
