package com.andy.myself.activity.systemAppInfo;

import java.util.List;

import com.andy.myself.R;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class InstalledPackageAdapter extends BaseAdapter {
    
    private List<PackageInfo> mApps;
    private LayoutInflater inflater;
    private Context mContext;
    
    public InstalledPackageAdapter (Context context, List<PackageInfo> infos) {
        this.mContext = context;
        this.mApps = infos;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    @Override
    public int getCount () {
        return mApps.size();
    }
    
    @Override
    public Object getItem (int position) {
        return position;
    }
    
    @Override
    public long getItemId (int position) {
        return position;
    }
    
    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.activity_app_adapter_list_item, null);
            holder.icon = (ImageView) convertView.findViewById(R.id.app_icon);
            holder.name = (TextView) convertView.findViewById(R.id.app_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.icon.setImageDrawable(mApps.get(position).applicationInfo.loadIcon(mContext.getPackageManager()));
        holder.name.setText(mApps.get(position).applicationInfo.loadLabel(mContext.getPackageManager()));
        return convertView;
    }
    
    class ViewHolder {
        ImageView icon;
        TextView name;
    }
}
