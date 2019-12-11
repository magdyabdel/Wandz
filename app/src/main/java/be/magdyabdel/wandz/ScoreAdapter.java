package be.magdyabdel.wandz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {

    private ArrayList<Profile> profiles;
    private Context context;

    public ScoreAdapter(ArrayList<Profile> profiles, Context context) {
        this.profiles = profiles;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.match_profile_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Profile profile = profiles.get(position);
        TextView textView = viewHolder.player_name;
        textView.setText(profile.getName());
        ImageView imageView = viewHolder.profile;
        profile.setProfileImage(context, imageView);
        TextView score = viewHolder.score;
        score.setText(Integer.toString(profile.getScore()));
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView profile;
        private TextView player_name;
        private TextView score;

        public ViewHolder(View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.image_player);
            player_name = itemView.findViewById(R.id.name_player);
            score = itemView.findViewById(R.id.score);
        }
    }
}

