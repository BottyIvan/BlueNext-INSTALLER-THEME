package com.botty.theme.next.blue.Fragment;

/**
 * Created by ivanbotty on 29/05/14.
 */
import android.annotation.SuppressLint;
import android.app.Fragment;
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
    private ProgressDialog progressDialog;

    public IconHelp() {

    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_icon_helper, container,
                false);

        new LoadApp().doInBackground();
        SendInfo();

        return view;
    }

    public class LoadApp extends AsyncTask<Void, Integer, Void>{

        @Override
        protected Void doInBackground(Void... params) {
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
            return null;
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
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                // When clicked, show a toast with the TextView text
                AppInfo country = (AppInfo) parent.getItemAtPosition(position);
                CheckBox a=(CheckBox) view.findViewById(R.id.checkBox1);
                a.toggle();
                country.setSelected(a.isChecked());
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

    //////




    ///////////////////////////


    //////

    private void SendInfo() {

        Button myButton = (Button) view.findViewById(R.id.findSelected);
        myButton.setOnClickListener(new OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                ArrayList<AppInfo> AppList = dataAdapter.appList;
                Send mytask = new Send(getActivity().getApplicationContext());

                mytask.execute(AppList);
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
                String[] recipients = new String[]{"droidbotty@gmail.com"};
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
        });

    }

    public class NotificationHelper {
        private Context mContext;
        private int NOTIFICATION_ID = 1;
        private Notification mNotification;
        private NotificationManager mNotificationManager;
        private PendingIntent mContentIntent;
        private CharSequence mContentTitle;
        public NotificationHelper(Context context)
        {
            mContext = context;
        }

        @SuppressWarnings("deprecation")
        public void completed()    {
            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            //create the notification
            int icon = R.drawable.icon;
            CharSequence tickerText = mContext.getString(R.string.activity_uploaded_notify); //Initial text that appears in the status bar
            long when = System.currentTimeMillis();
            mNotification = new Notification(icon, tickerText, when);
            mContentTitle = getActivity().getString(R.string.activity_uploaded_notify); //Full title of the notification in the pull down
            CharSequence contentText = mContext.getString(R.string.message_app_uploaded_notify); //Text of the notification in the pull down
            //you have to set a PendingIntent on a notification to tell the system what you want it to do when the notification is selected
            //I don't want to use this here so I'm just creating a blank one
            Intent notificationIntent = new Intent();
            mContentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);

            //add the additional content and intent to the notification
            mNotification.setLatestEventInfo(mContext, mContentTitle, contentText, mContentIntent);

            //make this notification appear in the 'Ongoing events' section
            mNotification.flags |= Notification.FLAG_AUTO_CANCEL;

            //show the notification
            mNotificationManager.notify(NOTIFICATION_ID, mNotification);
            //remove the notification from the status bar

        }
    }

    private class Send extends AsyncTask< ArrayList<AppInfo>, Integer, Void> {
        private NotificationHelper mNotificationHelper;
        public Send(Context contextin)
        {
            mNotificationHelper = new NotificationHelper(contextin);
        }
        @Override
        protected Void doInBackground(ArrayList<AppInfo>... params) {

            ArrayList<AppInfo> AppList =params[0];
            HttpURLConnection connection;
            OutputStreamWriter request = null;
            URL url = null;
            for(int i=0;i<AppList.size();i++)
            {

                AppInfo app = AppList.get(i);
                if(app.isSelected())
                {

                    String parameters = "icon_pack=meeui&app_name="+AppList.get(i).name+"&package="+AppList.get(i).code.split("/")[0]+"&component_info="+AppList.get(i).code;
                    try
                    {
                        url = new URL("http://droidthemes.com/request.php");

                        connection = (HttpURLConnection) url.openConnection();
                        connection.setDoOutput(true);
                        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        connection.setRequestMethod("POST");
                        request = new OutputStreamWriter(connection.getOutputStream());
                        request.write(parameters);
                        request.flush();
                        request.close();
                        String line = "";
                        InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                        BufferedReader reader = new BufferedReader(isr);
                        StringBuilder sb = new StringBuilder();
                        while ((line = reader.readLine()) != null)
                        {
                            sb.append(line + "\n");
                        }
                        isr.close();
                        reader.close();

                    }
                    catch(IOException e)
                    {
                        // Error
                    }


                }

            }

            return null;
        }


        protected void onProgressUpdate(Integer... i) {

        }

        @Override
        //Complete
        protected void onPostExecute(Void res) {
            mNotificationHelper.completed();
        }

        @Override
        protected void onPreExecute() {

        }
    }

}