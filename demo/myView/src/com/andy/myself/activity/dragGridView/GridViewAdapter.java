package com.andy.myself.activity.dragGridView;


import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andy.framework.view.draggridview.AndyDragGridViewAdapter;

public class GridViewAdapter extends AndyDragGridViewAdapter {
    private Context context;
    private List<String> strList;

    public GridViewAdapter(Context context, List<String> strList) {
        this.context = context;
        this.strList = strList;
    }

    @Override
    public int getCount() {
        return strList.size();
    }

    @Override
    public String getItem(int position) {
        return strList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view;
        if(convertView == null) {
            view = new TextView(context);
        } else {
            view = (TextView)convertView;
        }

        //hide时隐藏Text
        if (position != hidePosition) {
            view.setText(strList.get(position));
        } else {
            view.setText("");
        }
        view.setId(position);

        return view;
    }

	@Override
	public void onHideView(int position) {
		super.onHideView(position);
	}

	@Override
	public void onShowHideView() {
		super.onShowHideView();
	}

	@Override
	public void onDraggingView(int draggedPos, int destPos) {
		
		//从前向后拖动，其他item依次前移
        if(draggedPos < destPos) {
            strList.add(destPos+1, getItem(draggedPos));
            strList.remove(draggedPos);
        }
        //从后向前拖动，其他item依次后移
        else if(draggedPos > destPos) {
            strList.add(destPos, getItem(draggedPos));
            strList.remove(draggedPos+1);
        }
        super.onDraggingView(draggedPos, destPos);
	}

	@Override
	public void onRemoveView(int position) {
		strList.remove(position);
		super.onRemoveView(position);
	}
}
