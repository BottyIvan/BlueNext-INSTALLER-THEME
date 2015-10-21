package com.botty.theme.next.blue.Fragment;

/**
 * Created by ivanbotty on 29/05/14.
 */
import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.botty.theme.next.blue.Util.AppInfo;
import com.botty.theme.next.blue.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.melnykov.fab.FloatingActionButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressLint("NewApi")
public class IconHelp extends Fragment {

    MyCustomAdapter dataAdapter = null;
    static String icon_pack="";
    View view;
    FloatingActionButton floatingActionButton;
    public IconHelp() {

    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_icon_helper, container,
                false);

        new LoadApp().doInBackground();

        return view;
    }

    public class LoadApp extends AsyncTask<Void, Integer, Boolean>{
        private ProgressDialog dialog = new ProgressDialog(getActivity());

        /** progress dialog to show user that the backup is processing. */
        /** application context. */
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Please wait");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Intent intent = getActivity().getIntent();
            icon_pack= intent.getStringExtra("app");
            //Generate list View from ArrayList
            try {
                displayListView();
            }
            catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return true;
        }

        protected void onPostExecute() {

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            // Setting data to list adapter
        }
    }


    @Override
    public void onDestroy() {

        super.onDestroy();

    }


    private void displayListView() throws NameNotFoundException {

        ArrayList<AppInfo> appList = new ArrayList<AppInfo>();
        //GET list of installed apps.
        final PackageManager pm = getActivity().getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        Collections.sort(packages, new ApplicationInfo.DisplayNameComparator(pm));
        for (ApplicationInfo packageInfo : packages)
        {
            if(pm.getLaunchIntentForPackage(packageInfo.packageName)!= null &&
                    !pm.getLaunchIntentForPackage(packageInfo.packageName).equals(""))
            {
                // Code start
                String appName = pm.getApplicationLabel(packageInfo).toString();
                String LaunchIntent =pm.getLaunchIntentForPackage(packageInfo.packageName).toString().split("cmp=")[1];
                String cmp= LaunchIntent.substring(0,LaunchIntent.length()-1);
                if(cmp.split("/")[1].startsWith("."))
                {
                    cmp= cmp.split("/")[0]+"/"+cmp.split("/")[0]+cmp.split("/")[1];
                }
                Drawable icon = pm.getApplicationIcon(packageInfo);
                AppInfo App_info = new AppInfo(cmp,appName,icon,false);
                appList.add(App_info) ;
            }
        }

        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(getActivity(),R.layout.app_info, appList);
        ListView listView = (ListView) view.findViewById(R.id.listApps);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        floatingActionButton.attachToListView(listView);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked, show a toast with the TextView text
                AppInfo country = (AppInfo) parent.getItemAtPosition(position);
                CheckBox a = (CheckBox) view.findViewById(R.id.checkBox1);
                a.toggle();
                country.setSelected(a.isChecked());
            }
        });

        floatingActionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SendInfo();
            }
        });

    }

    private class MyCustomAdapter extends ArrayAdapter<AppInfo> {

        private ArrayList<AppInfo> appList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<AppInfo> appList)
        {
            super(context, textViewResourceId, appList);
            this.appList = new ArrayList<AppInfo>();
            this.appList.addAll(appList);
        }
        private class ViewHolder
        {
            TextView cmp;
            TextView name;
            CheckBox chk;
            ImageView img;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));
            if (convertView == null)
            {
                LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.app_info, null);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.tvAppName);
                holder.cmp = (TextView) convertView.findViewById(R.id.tvCMP);
                holder.chk= (CheckBox) convertView.findViewById(R.id.checkBox1);
                holder.img=(ImageView)convertView.findViewById(R.id.app_image);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            AppInfo country = appList.get(position);
            holder.cmp.setText(country.getCode());
            holder.name.setText(country.getName());
            holder.img.setImageDrawable(country.getImage());
            holder.chk.setChecked(country.isSelected());
            holder.name.setTag(country);
            return convertView;
        }
    }


    public void SendInfo() {

        ArrayList<AppInfo> AppList = dataAdapter.appList;

        StringBuilder sb = new StringBuilder();
        sb.append(getActivity().getString(R.string.request_to_dev_for_unthemed_icon));
        for(int i=0;i<AppList.size();i++)
        {
            AppInfo app = AppList.get(i);
            if(app.isSelected())
            {
                sb.append(AppList.get(i).name+"\n");
                sb.append(AppList.get(i).code.split("/")[0]+"\n");
                sb.append(AppList.get(i).code+"\n");
                sb.append("https://play.google.com/store/apps/details?id="+AppList.get(i).code.split("/")[0]+"\n");
                sb.append("__________________\n\n");
            }
        }
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        //  String recipients = new String(emailDEV.getText().toString());
        // String[] recipients = new String[]{getActivity().getString(R.string.email_to_dev)};
        String[] recipients = new String[]{"testbottyivan@gmail.com"};
        i.putExtra(Intent.EXTRA_EMAIL, recipients);
        i.putExtra(Intent.EXTRA_SUBJECT, getActivity().getString(R.string.reques_for_theme_name));
        i.putExtra(Intent.EXTRA_TEXT   , sb.toString());
        try
        {
            startActivity(Intent.createChooser(i, getActivity().getString(R.string.send_mail)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.no_mail_client), Toast.LENGTH_SHORT).show();
        }

    }

}