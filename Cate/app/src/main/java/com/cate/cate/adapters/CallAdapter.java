package com.cate.cate.adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cate.cate.R;

import java.util.ArrayList;

public class CallAdapter extends ArrayAdapter<String> {

    Context context;
    ArrayList<String> names;
    ArrayList<String> phones;

    public CallAdapter(Context c, ArrayList<String> names, ArrayList<String> phones) {
        super(c, R.layout.list_row, R.id.textView1, names);
        this.context = c;
        this.names = names;
        this.phones = phones;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.list_row, parent, false);
        TextView myTitle = (TextView) row.findViewById(R.id.textView1);
        TextView myDescription = (TextView) row.findViewById(R.id.textView2);

        // now set our resources on views
        myTitle.setText(names.get(position));
        myDescription.setText(phones.get(position));

        return row;
    }
}


