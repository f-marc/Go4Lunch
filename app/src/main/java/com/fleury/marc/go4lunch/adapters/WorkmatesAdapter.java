package com.fleury.marc.go4lunch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.fleury.marc.go4lunch.R;
import com.fleury.marc.go4lunch.models.User;
import com.fleury.marc.go4lunch.views.WorkmatesViewHolder;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesViewHolder> {

    private List<User> usersList;
    private Context context;
    private RequestManager glide;

    public WorkmatesAdapter(Context ctx, RequestManager glide) {
        context = ctx;
        this.glide = glide;
    }

    public void setUsers(List<User> user) {
        this.usersList = user;
        notifyDataSetChanged();
    }

    public User getUsers(int position){
        return this.usersList.get(position);
    }

    @Override
    public WorkmatesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_workmates_item, parent, false);

        return new WorkmatesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WorkmatesViewHolder viewHolder, int position) {
        final int itemPos = position;
        final User user = usersList.get(position);

        String name = user.getUsername();
        String image = user.getUrlPicture();
        String restaurant = user.getRestaurant();

        viewHolder.updateWithWorkmates(image, name, restaurant, this.glide);
    }

    @Override
    public int getItemCount() {
        if(usersList == null){
            return 0;
        }
        return usersList.size();
    }

}