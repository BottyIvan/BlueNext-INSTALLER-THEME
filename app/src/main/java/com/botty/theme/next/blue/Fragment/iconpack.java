package com.botty.theme.next.blue.Fragment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.botty.theme.next.blue.R;

import java.util.ArrayList;

/**
 * Created by ivanbotty on 30/05/14.
 */
public class iconpack extends Fragment {

    private static final String ACTION_ADW_PICK_ICON="org.adw.launcher.icons.ACTION_PICK_ICON";
    private boolean mPickerMode=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.icon_picker, container,
                false);

        int iconSize=getResources().getDimensionPixelSize(android.R.dimen.app_icon_size);
        GridView g=(GridView) view.findViewById(R.id.icon_grid);
        g.setNumColumns(GridView.AUTO_FIT);
        g.setColumnWidth(iconSize);
        g.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);
        g.setVerticalSpacing(iconSize/3);
        IconsAdapter adapter=new IconsAdapter(getActivity(),iconSize);
        g.setAdapter(adapter);

        return view;

    }

    private class IconsAdapter extends BaseAdapter{
        private Context mContext;
        private int mIconSize;
        public IconsAdapter(Context mContext, int iconsize) {
            super();
            this.mContext = mContext;
            this.mIconSize = iconsize;
            loadIcons();
        }

        public int getCount() {
            return mThumbs.size();
        }

        public Object getItem(int position) {
            Options opts=new Options();
            opts.inPreferredConfig=Bitmap.Config.ARGB_8888;
            return BitmapFactory.decodeResource(mContext.getResources(), mThumbs.get(position), opts);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(mIconSize, mIconSize));
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setImageResource(mThumbs.get(position));
            return imageView;
        }

        private ArrayList<Integer> mThumbs;

        private void loadIcons() {
            mThumbs = new ArrayList<Integer>();

            final Resources resources = getResources();
            final String packageName = getActivity().getPackageName();

            addIcons(resources, packageName, R.array.icon_pack);
        }
        private void addIcons(Resources resources, String packageName, int list) {
            final String[] extras = resources.getStringArray(list);
            for (String extra : extras) {
                int res = resources.getIdentifier(extra, "drawable", packageName);
                if (res != 0) {
                    final int thumbRes = resources.getIdentifier(extra,"drawable", packageName);
                    if (thumbRes != 0) {
                        mThumbs.add(thumbRes);
                    }
                }
            }
        }

    }
}