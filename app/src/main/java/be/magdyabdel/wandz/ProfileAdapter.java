package be.magdyabdel.wandz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    private ArrayList<Profile> profiles;
    private Context context;

    public ProfileAdapter(ArrayList<Profile> profiles, Context context) {
        this.profiles = profiles;
        this.context = context;
    }

    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.profile_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ProfileAdapter.ViewHolder viewHolder, int position) {
        Profile profile = profiles.get(position);
        TextView textView = viewHolder.player_name;
        textView.setText(profile.getName());
        ImageView imageView = viewHolder.profile;
        profile.setProfileImage(context, imageView);
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView profile;
        private TextView player_name;

        public ViewHolder(View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.image_player);
            player_name = itemView.findViewById(R.id.name_player);
        }
    }
}
