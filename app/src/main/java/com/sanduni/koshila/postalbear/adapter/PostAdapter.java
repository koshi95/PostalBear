package com.sanduni.koshila.postalbear.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sanduni.koshila.postalbear.R;
import com.sanduni.koshila.postalbear.activity.AddEditViewPostActivity;
import com.sanduni.koshila.postalbear.util.AdapterListener;
import com.sanduni.koshila.postalbear.util.PostalBearDBContract.Post;

import java.util.ArrayList;
import java.util.HashMap;

import static com.sanduni.koshila.postalbear.util.Common.EDIT;
import static com.sanduni.koshila.postalbear.util.Common.VIEW;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    Context context;
    ArrayList<HashMap<String, String>> postList;
    int allowedToEdit;
    boolean isDistributionPostList = false;
    private ArrayList<String> postOfficeIdList;
    private ArrayList<String> postOfficeList;
    private AdapterListener adapterListener;
    public PostAdapter(Context context, ArrayList<HashMap<String, String>> postList, int allowedToEdit) {
        this.context = context;
        this.postList = postList;
        this.allowedToEdit = allowedToEdit;
    }
    public PostAdapter(Context context, ArrayList<HashMap<String, String>> postList, int allowedToEdit, AdapterListener adapterListener) {
        this.context = context;
        this.postList = postList;
        this.allowedToEdit = allowedToEdit;
        this.adapterListener = adapterListener;
    }

    public void setDistributionPostListStatus(boolean status) {
        this.isDistributionPostList = status;
    }

    public void addPost(HashMap<String, String> post) {
        String newRef = post.get(Post.COLUMN_NAME_REFERENCE_NUMBER);
        if (postList != null && checkExists(newRef)) {
            postList.add(post);
            notifyDataSetChanged();
            if (adapterListener != null) adapterListener.dataSetChanged();
        }
        else {
            Toast.makeText(context, "Post is already added to the list", Toast.LENGTH_LONG).show();
            
        }
    }

    public void removePost(int position) {
        postList.remove(position);
        notifyDataSetChanged();
        if (adapterListener != null) adapterListener.dataSetChanged();
    }

    public ArrayList<HashMap<String, String>> getPostList() {
        return postList;
    }

    public void setPostOfficeArrays(ArrayList<String> postOfficeIdList, ArrayList<String> postOfficeList) {
        this.postOfficeIdList = postOfficeIdList;
        this.postOfficeList = postOfficeList;
    }

    private String getPostOfficeName(String id) {
        int postOfficeIndex = postOfficeIdList.indexOf(id);
        if (postOfficeIndex == -1) return "";
        return postOfficeList.get(postOfficeIndex);
    }

    private boolean checkExists(String newRef) {
        for (HashMap<String, String> post: postList) {
            String ref = post.get(Post.COLUMN_NAME_REFERENCE_NUMBER);
            if (ref != null && ref.equals(newRef)){
                return false;
            }
        }
        return true;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.post_rv_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        if (postList != null && postList.get(position) != null) {
            final HashMap<String, String> postItem = postList.get(position);
            holder.referenceNumberTextView.setText(postItem.get(Post.COLUMN_NAME_REFERENCE_NUMBER));
            holder.lastPostOfficeTextView.setText(getPostOfficeName(postItem.get(Post.COLUMN_NAME_LAST_POST_OFFICE_ID)));
            holder.nextPostOfficeTextView.setText(getPostOfficeName(postItem.get(Post.COLUMN_NAME_NEXT_POST_OFFICE_ID)));
            holder.receiverAddressTextView.setText(postItem.get(Post.COLUMN_NAME_RECEIVER_ADDRESS));
            if (isDistributionPostList) {
                holder.gotToPostButton.setBackground(ResourcesCompat.getDrawable(context.getResources() ,R.drawable.ic_baseline_remove_24, null));
                holder.gotToPostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removePost(position);
                    }
                });
            }
            else {
                holder.gotToPostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent postIntent = new Intent(context, AddEditViewPostActivity.class);
                        if (allowedToEdit == EDIT) {
                            postIntent.putExtra("ACTION", "EDIT");
                        }
                        else if (allowedToEdit == VIEW) {
                            postIntent.putExtra("ACTION", "VIEW");
                        }
                        else {
                            postIntent.putExtra("ACTION", "PARTIAL_EDIT");
                        }
                        postIntent.putExtra("POST_REFERENCE_NUMBER", postItem.get(Post.COLUMN_NAME_REFERENCE_NUMBER));
                        context.startActivity(postIntent);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        if (postList == null) return 0;
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        TextView referenceNumberTextView;
        TextView lastPostOfficeTextView;
        TextView nextPostOfficeTextView;
        TextView receiverAddressTextView;
        Button gotToPostButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            referenceNumberTextView = itemView.findViewById(R.id.reference_number);
            lastPostOfficeTextView = itemView.findViewById(R.id.last_post_office);
            nextPostOfficeTextView = itemView.findViewById(R.id.next_post_office);
            receiverAddressTextView = itemView.findViewById(R.id.receiver_address);
            gotToPostButton = itemView.findViewById(R.id.goto_post);
        }
    }
}
