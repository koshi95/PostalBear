package com.sanduni.koshila.postalbear.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sanduni.koshila.postalbear.R;
import com.sanduni.koshila.postalbear.activity.AddEditViewPostOfficeActivity;
import com.sanduni.koshila.postalbear.util.PostalBearDBContract;

import java.util.ArrayList;
import java.util.HashMap;

public class PostOfficeAdapter extends RecyclerView.Adapter<PostOfficeAdapter.PostOfficeViewHolder> {
    Context context;
    ArrayList<HashMap<String, String>> postOfficeList;
    boolean allowedToEdit;
    public PostOfficeAdapter(Context context, ArrayList<HashMap<String, String>> postOfficeList, boolean allowedToEdit) {
        this.context = context;
        this.postOfficeList = postOfficeList;
        this.allowedToEdit = allowedToEdit;
    }

    @NonNull
    @Override
    public PostOfficeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.post_office_rv_item, parent, false);
        return new PostOfficeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostOfficeViewHolder holder, int position) {
        if (postOfficeList != null && postOfficeList.get(position) != null) {
            final HashMap<String, String> postOfficeItem = postOfficeList.get(position);
            holder.postOfficeNameTextView.setText(postOfficeItem.get(PostalBearDBContract.PostOffice.COLUMN_NAME_NAME));
            holder.postOfficeDistrictTextView.setText(postOfficeItem.get(PostalBearDBContract.PostOffice.COLUMN_NAME_DISTRICT));
            holder.postOfficeTelephoneTextView.setText(postOfficeItem.get(PostalBearDBContract.PostOffice.COLUMN_NAME_TELEPHONE));
            holder.gotToPostOfficeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent postOfficeIntent = new Intent(context, AddEditViewPostOfficeActivity.class);
                    if (allowedToEdit) {
                        postOfficeIntent.putExtra("ACTION", "EDIT");
                    }
                    else {
                        postOfficeIntent.putExtra("ACTION", "VIEW");
                    }
                    postOfficeIntent.putExtra("POST_OFFICE_NAME", postOfficeItem.get(PostalBearDBContract.PostOffice.COLUMN_NAME_NAME));
                    context.startActivity(postOfficeIntent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return postOfficeList.size();
    }

    public static class PostOfficeViewHolder extends RecyclerView.ViewHolder {

        TextView postOfficeNameTextView;
        TextView postOfficeDistrictTextView;
        TextView postOfficeTelephoneTextView;
        Button gotToPostOfficeButton;

        public PostOfficeViewHolder(@NonNull View itemView) {
            super(itemView);
            postOfficeNameTextView = itemView.findViewById(R.id.post_office_name);
            postOfficeDistrictTextView = itemView.findViewById(R.id.post_office_district);
            postOfficeTelephoneTextView = itemView.findViewById(R.id.post_office_telephone);
            gotToPostOfficeButton = itemView.findViewById(R.id.go_to_post_office);
        }
    }
}
