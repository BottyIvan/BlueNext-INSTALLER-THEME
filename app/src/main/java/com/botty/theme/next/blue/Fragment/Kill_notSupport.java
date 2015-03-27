package com.botty.theme.next.blue.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.botty.theme.next.blue.R;
import com.koushikdutta.ion.Ion;

/**
 * Created by BottyIvan on 27/03/15.
 */
public class Kill_notSupport extends Fragment {

    public String kill = "https://lh6.googleusercontent.com/-lfM1Tf7LeEs/VPZNVfVVx0I/AAAAAAAAg3w/RZu1Tq6YT0U/w1250-h832-no/803f1a0db2a57b833a0049b53a886ec95b046e5c8eafe715c36f0c32183d9f65.jpg";

    public Kill_notSupport() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_fuck_you, container,
                false);

        ImageView img = (ImageView) view.findViewById(R.id.imgKill);
        Button btn_inst = (Button) view.findViewById(R.id.instTheme_btn);
        Button btn_rate = (Button) view.findViewById(R.id.rate_it);

        Ion.with(img).load(kill);
        btn_inst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment_Swiper fragment = new Fragment_Swiper();
                fragmentTransaction.replace(R.id.content_frame, fragment);
                fragmentTransaction.commit();
            }
        });
        btn_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.botty.theme.next.blue"));
                startActivity(intent);
            }
        });
        return view;
    }
}