package com.godknows.covid19;

import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListOnlineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView txtEmail;
    public LinearLayout root;

    public ListOnlineViewHolder(@NonNull View itemView) {
        super(itemView);
        root=(LinearLayout) itemView.findViewById(R.id.linear_page);
        txtEmail=(TextView) itemView.findViewById(R.id.txt_email);
    }

    @Override
    public void onClick(View v) {

    }
}
