package com.sanduni.koshila.postalbear.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sanduni.koshila.postalbear.activity.AddEditDistributionActivity;
import com.sanduni.koshila.postalbear.activity.AddEditViewPostActivity;
import com.sanduni.koshila.postalbear.activity.AddEditViewPostOfficeActivity;
import com.sanduni.koshila.postalbear.activity.HistoryActivity;
import com.sanduni.koshila.postalbear.activity.PostOfficesActivity;
import com.sanduni.koshila.postalbear.activity.PostsActivity;
import com.sanduni.koshila.postalbear.activity.ScannerActivity;
import com.sanduni.koshila.postalbear.modal.DashBoardItemModal;
import com.sanduni.koshila.postalbear.R;
import com.sanduni.koshila.postalbear.activity.ReferenceNumberActivity;

import java.util.ArrayList;

import static com.sanduni.koshila.postalbear.util.Common.ADD_POST;
import static com.sanduni.koshila.postalbear.util.Common.ADD_POST_OFFICE;
import static com.sanduni.koshila.postalbear.util.Common.CREATE_DISTRIBUTION;
import static com.sanduni.koshila.postalbear.util.Common.SEARCH_BY_QR_SCANNER;
import static com.sanduni.koshila.postalbear.util.Common.SEARCH_BY_REFERENCE_NUMBER;
import static com.sanduni.koshila.postalbear.util.Common.VIEW_HISTORY;
import static com.sanduni.koshila.postalbear.util.Common.VIEW_POSTS;
import static com.sanduni.koshila.postalbear.util.Common.VIEW_POST_OFFICES;
import static com.sanduni.koshila.postalbear.util.Common.VIEW_SEND_POSTS;

public class DashboardAdapter extends ArrayAdapter<DashBoardItemModal> {
    public DashboardAdapter(@NonNull Context context, ArrayList<DashBoardItemModal> courseModelArrayList) {
        super(context, 0, courseModelArrayList);
        }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
        // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.dashboard_grid_item, parent, false);
            listitemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getItem(position).getItemName().equals(SEARCH_BY_REFERENCE_NUMBER)) {
                        Intent searchByReferenceNumberIntent = new Intent(getContext(), ReferenceNumberActivity.class);
                        getContext().startActivity(searchByReferenceNumberIntent);
                    }
                    else if (getItem(position).getItemName().equals(SEARCH_BY_QR_SCANNER)) {
                        Intent searchByQRScannerIntent = new Intent(getContext(), ScannerActivity.class);
                        getContext().startActivity(searchByQRScannerIntent);
                    }
                    else if (getItem(position).getItemName().equals(ADD_POST_OFFICE)) {
                        Intent addEditPostOfficeIntent = new Intent(getContext(), AddEditViewPostOfficeActivity.class);
                        addEditPostOfficeIntent.putExtra("ACTION", "ADD");
                        getContext().startActivity(addEditPostOfficeIntent);
                    }
                    else if (getItem(position).getItemName().equals(VIEW_POST_OFFICES)) {
                        Intent postOfficesIntent = new Intent(getContext(), PostOfficesActivity.class);
                        getContext().startActivity(postOfficesIntent);
                    }
                    else if (getItem(position).getItemName().equals(VIEW_POSTS)) {
                        Intent postsIntent = new Intent(getContext(), PostsActivity.class);
                        getContext().startActivity(postsIntent);
                    }
                    else if (getItem(position).getItemName().equals(VIEW_HISTORY) || getItem(position).getItemName().equals(VIEW_SEND_POSTS)) {
                        Intent postsIntent = new Intent(getContext(), HistoryActivity.class);
                        getContext().startActivity(postsIntent);
                    }
                    else if (getItem(position).getItemName().equals(ADD_POST)) {
                        Intent addEditPostOfficeIntent = new Intent(getContext(), AddEditViewPostActivity.class);
                        addEditPostOfficeIntent.putExtra("ACTION", "ADD");
                        getContext().startActivity(addEditPostOfficeIntent);
                    }
                    else if (getItem(position).getItemName().equals(CREATE_DISTRIBUTION)) {
                        Intent addEditPostOfficeIntent = new Intent(getContext(), AddEditDistributionActivity.class);
                        getContext().startActivity(addEditPostOfficeIntent);
                    }
                }
            });
        }
        DashBoardItemModal itemModal = getItem(position);
        TextView itemName = listitemView.findViewById(R.id.dashboard_label);
        ImageView itemImage = listitemView.findViewById(R.id.dashboard_image);
        itemName.setText(itemModal.getItemName());
        itemImage.setImageResource(itemModal.getImageId());
        return listitemView;
        }
    }
