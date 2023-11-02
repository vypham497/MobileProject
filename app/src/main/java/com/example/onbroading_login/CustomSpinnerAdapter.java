package com.example.onbroading_login;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
public class CustomSpinnerAdapter extends ArrayAdapter<SpinnerItem> {
    public CustomSpinnerAdapter(Context context, List<SpinnerItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createCustomView(position, convertView, parent);
    }

    private View createCustomView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_item, parent, false);
        }

        ImageView iconImageView = convertView.findViewById(R.id.icon);
        TextView textTextView = convertView.findViewById(R.id.text);

        SpinnerItem item = getItem(position);

        if (item != null) {
            iconImageView.setImageResource(item.getIconResId());
            textTextView.setText(item.getText());
        }

        return convertView;
    }
}
