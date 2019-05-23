package com.fleury.marc.go4lunch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.fleury.marc.go4lunch.R;
import com.fleury.marc.go4lunch.models.User;
import com.fleury.marc.go4lunch.views.DetailViewHolder;

import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class DetailAdapter extends RecyclerView.Adapter<DetailViewHolder> {

    private List<User> usersList;
    private Context context;
    private RequestManager glide;

    public DetailAdapter(Context context, RequestManager glide) {
        this.context = context;
        this.glide = glide;
    }

    public void setUsers(List<User> usersList) {
        this.usersList = usersList;
        sortingAlphabetically(usersList);
        notifyDataSetChanged();
    }

    private void sortingAlphabetically(List<User> usersList) {
        Collections.sort(usersList, (o1, o2) -> o1.getUsername().compareToIgnoreCase(o2.getUsername()));
    }

    public User getUsers(int position){
        return this.usersList.get(position);
    }

    @Override
    public DetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_detail_item, parent, false);

        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DetailViewHolder viewHolder, int position) {
        final User user = usersList.get(position);

        String name = context.getString(R.string.joining, user.getUsername());
        String image = user.getUrlPicture();

        viewHolder.updateWithUser(image, name, this.glide);
    }

    @Override
    public int getItemCount() {
        if(usersList == null){
            return 0;
        }
        return usersList.size();
    }

}