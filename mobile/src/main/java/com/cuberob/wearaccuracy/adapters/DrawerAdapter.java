package com.cuberob.wearaccuracy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cuberob.wearaccuracy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by robdeknegt on 16/03/15.
 */
public class DrawerAdapter extends ArrayAdapter<DrawerAdapter.NavigationDrawerItem> {

    Context context;
    List<NavigationDrawerItem> items;

    public static DrawerAdapter newInstance(Context context){
        List<NavigationDrawerItem> items = new ArrayList<NavigationDrawerItem>();

        //Setup default menu
        items.add(new NavigationDrawerItem("Button Test", R.drawable.common_signin_btn_icon_normal_dark));
        items.add(new NavigationDrawerItem("Visibility Test", R.drawable.common_signin_btn_icon_focus_light));
        items.add(new NavigationDrawerItem("Vibration Test", R.drawable.abc_list_pressed_holo_dark));

        return new DrawerAdapter(context, R.layout.drawer_list_item, items);
    }

    public DrawerAdapter(Context context, int resource, List<NavigationDrawerItem> items) {
        super(context, resource, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.drawer_list_item, null);

        }

        NavigationDrawerItem p = getItem(position);

        if (p != null) {

            TextView tv = (TextView) v.findViewById(R.id.navdrawer_textView);
            ImageView iv = (ImageView) v.findViewById(R.id.navdrawer_imageView);

            if (tv != null) {
                tv.setText(p.name);
            }
            if (iv != null) {
                iv.setImageResource(p.iconId);
            }
        }

        return v;

    }

    public static class NavigationDrawerItem {
        public String name;
        public int iconId;

        public NavigationDrawerItem(String name, int iconId) {
            this.name = name;
            this.iconId = iconId;
        }
    }

}
