package com.example.easysale.room;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.easysale.LoginActivity;
import com.example.easysale.R;
import com.example.easysale.UpdateUserFragment;
import com.example.easysale.UserListFragment;
import com.example.easysale.data.User;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private ArrayList<User> users;
    private UserListFragment userListFragment;

    public UserAdapter(ArrayList<User> users, UserListFragment userListFragment) {
        this.users = users;
        this.userListFragment = userListFragment;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        String fullName = users.get(position).getFirstName()+"  "+users.get(position).getLastName();
        holder.nameText.setText(fullName);
        holder.emailText.setText(users.get(position).getEmail());
        String avatar = users.get(position).getAvatar();

        if (!avatar.equals(""))
            Glide.with(holder.userImageView.getContext()).load(users.get(position).getAvatar()).fitCenter().into(holder.userImageView);
        else Glide.with(holder.userImageView.getContext()).load(R.drawable.bag).fitCenter().into(holder.userImageView);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nameText;
        private TextView emailText;
        private ImageView userImageView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameTextView);
            emailText = itemView.findViewById(R.id.emailTextView);
            userImageView = itemView.findViewById(R.id.userImageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            User user = users.get(position);
            UpdateUserFragment updateUserFragment = new UpdateUserFragment();
            Bundle bundle = new Bundle();
            bundle.putString("clickedFirstName",user.getFirstName());
            bundle.putString("clickedLastName",user.getLastName());
            bundle.putString("clickedEmail",user.getEmail());
            bundle.putInt("clickedId",user.getId());
            bundle.putInt("clickedUserId",user.getUserId());
            bundle.putString("clickedAvatar",user.getAvatar());
            bundle.putString("clickedPage",user.getPage());
            bundle.putInt("currentPage",userListFragment.currentPage);
            updateUserFragment.setArguments(bundle);

            FragmentManager fm = userListFragment.requireActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.recycContainLayout, updateUserFragment);
            ft.commit();
        }
    }
}
