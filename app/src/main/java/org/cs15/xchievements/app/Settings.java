package org.cs15.xchievements.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.cs15.xchievements.R;
import org.cs15.xchievements.Repository.Database;
import org.cs15.xchievements.misc.HelperClass;
import org.cs15.xchievements.misc.UserProfile;

/**
 * Fragment for settings
 * <p/>
 * Created by Christian Soler on 11/28/14.
 */
public class Settings extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_settings, container, false);

        if (UserProfile.getCurrentUser() != null) {
            view.findViewById(R.id.v_first).setVisibility(View.VISIBLE);
            TextView tvLogout = (TextView) view.findViewById(R.id.tv_logout);
            tvLogout.setVisibility(View.VISIBLE);
            tvLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Database().logout();

                    HelperClass.reloadActivity(getActivity());
                }
            });
        }

        try {
            int apkVersionCode = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode;
            String apkVersionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;

            TextView tvVersionName = (TextView) view.findViewById(R.id.tv_version_name);
            tvVersionName.setText(String.format("App Version: %s", apkVersionName));

            TextView tvVersionCode = (TextView) view.findViewById(R.id.tv_version_code);
            tvVersionCode.setText(String.format("Build Version: %s", apkVersionCode));

            TextView tvTermsOfUse = (TextView) view.findViewById(R.id.tv_terms_of_use);
            tvTermsOfUse.setText("Terms of Use");
            tvTermsOfUse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.xchievements.com/legal")));
                }
            });

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }



        return view;
    }
}
