package org.relos.cheevos.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseUser;

import org.relos.cheevos.R;
import org.relos.cheevos.misc.HelperClass;

/**
 * Fragment for settings
 * <p/>
 * Created by Christian Soler on 11/28/14.
 */
public class Settings extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_settings, container, false);

        if (ParseUser.getCurrentUser() != null) {
            view.findViewById(R.id.v_first).setVisibility(View.VISIBLE);
            TextView tvLogout = (TextView) view.findViewById(R.id.tv_logout);
            tvLogout.setVisibility(View.VISIBLE);
            tvLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ParseUser.logOut();

                    HelperClass.reloadActivity(getActivity());
                }
            });
        }

        return view;
    }
}
