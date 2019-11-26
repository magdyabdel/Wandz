package be.magdyabdel.wandz;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;

import com.devs.vectorchildfinder.VectorChildFinder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class AppData implements Serializable {

    private String name_player;
    private ArrayList<String> names;
    private int outfit_color_array_id;
    private int skin_color_array_id;
    private int hair_color_array_id;
    private int eye_color_array_id;

    public AppData() {

        names = new ArrayList<>();
        outfit_color_array_id = 0;
        skin_color_array_id = 0;
        hair_color_array_id = 0;
        eye_color_array_id = 0;

    }

    public String getName_player() {
        return name_player;
    }

    public void setName_player(String name_player) {
        this.name_player = name_player;
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public void setNames(ArrayList<String> names) {
        this.names = names;
    }

    public void addName(String newName) {
        names.add(newName);
    }

    public void removeName(String nameToRemove) {
        names.remove(nameToRemove);
    }

    public void setProfileImage(Context context, ImageView profile, int id) {

        String[] outfitColors = context.getResources().getStringArray(R.array.outfit_array);
        String[] skinColors = context.getResources().getStringArray(R.array.skin_array);
        String[] hairColors = context.getResources().getStringArray(R.array.hair_array);
        String[] eyeColors = context.getResources().getStringArray(R.array.eye_array);

        VectorChildFinder vector = new VectorChildFinder(context, R.drawable.ic_profile, profile);
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath cape_path_light = vector.findPathByName("cape_light");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath cape_path_dark1 = vector.findPathByName("cape_dark1");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath cape_path_dark2 = vector.findPathByName("cape_dark2");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath hat_path_light = vector.findPathByName("hat_light");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath hat_path_dark = vector.findPathByName("hat_dark");

        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath hair_path1 = vector.findPathByName("hair1");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath hair_path2 = vector.findPathByName("hair2");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath hair_path3 = vector.findPathByName("hair3");

        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath skin_path1 = vector.findPathByName("skin1");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath skin_path2 = vector.findPathByName("skin2");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath skin_path3 = vector.findPathByName("skin3");

        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath eye_path_left = vector.findPathByName("eyeLeft");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath eye_path_right = vector.findPathByName("eyeRight");

        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath details_path1 = vector.findPathByName("details1");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath details_path2 = vector.findPathByName("details2");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath details_path3 = vector.findPathByName("details3");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath details_path4 = vector.findPathByName("details4");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath details_path5 = vector.findPathByName("details5");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath details_path6 = vector.findPathByName("details6");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath details_path7 = vector.findPathByName("details7");
        com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath details_path8 = vector.findPathByName("details8");

        if (id != -1) {
            switch (id) {
                case R.id.outfit_left:

                    outfit_color_array_id = outfit_color_array_id - 2;
                    if (outfit_color_array_id < 0) {
                        outfit_color_array_id = outfitColors.length - 2;
                    }
                    break;
                case R.id.outfit_right:

                    outfit_color_array_id = outfit_color_array_id + 2;
                    if (outfit_color_array_id >= outfitColors.length) {
                        outfit_color_array_id = 0;
                    }
                    break;
                case R.id.hair_left:

                    hair_color_array_id--;
                    if (hair_color_array_id < 0) {
                        hair_color_array_id = hairColors.length - 1;
                    }
                    break;
                case R.id.hair_right:

                    hair_color_array_id++;
                    if (hair_color_array_id >= hairColors.length) {
                        hair_color_array_id = 0;
                    }
                    break;
                case R.id.skin_left:

                    skin_color_array_id = skin_color_array_id - 2;
                    if (skin_color_array_id < 0) {
                        skin_color_array_id = skinColors.length - 2;
                    }
                    break;
                case R.id.skin_right:

                    skin_color_array_id = skin_color_array_id + 2;
                    if (skin_color_array_id >= skinColors.length) {
                        skin_color_array_id = 0;
                    }
                    break;
                case R.id.eye_left:

                    eye_color_array_id--;
                    if (eye_color_array_id < 0) {
                        eye_color_array_id = eyeColors.length - 1;
                    }
                    break;
                case R.id.eye_right:

                    eye_color_array_id++;
                    if (eye_color_array_id >= eyeColors.length) {
                        eye_color_array_id = 0;
                    }
                    break;
                case R.id.random:
                    outfit_color_array_id = new Random().nextInt((outfitColors.length - 1) / 2) * 2;
                    skin_color_array_id = new Random().nextInt((skinColors.length - 1) / 2) * 2;
                    hair_color_array_id = new Random().nextInt((hairColors.length));
                    eye_color_array_id = new Random().nextInt((eyeColors.length - 1));

                    break;

                default:
                    break;
            }
        }

        cape_path_light.setFillColor(Color.parseColor(outfitColors[outfit_color_array_id]));
        hat_path_light.setFillColor(Color.parseColor(outfitColors[outfit_color_array_id]));
        cape_path_dark1.setFillColor(Color.parseColor(outfitColors[outfit_color_array_id + 1]));
        cape_path_dark2.setFillColor(Color.parseColor(outfitColors[outfit_color_array_id + 1]));
        hat_path_dark.setFillColor(Color.parseColor(outfitColors[outfit_color_array_id + 1]));

        hair_path1.setFillColor(Color.parseColor(hairColors[hair_color_array_id]));
        hair_path2.setFillColor(Color.parseColor(hairColors[hair_color_array_id]));
        hair_path3.setFillColor(Color.parseColor(hairColors[hair_color_array_id]));

        skin_path1.setFillColor(Color.parseColor(skinColors[skin_color_array_id]));
        skin_path2.setFillColor(Color.parseColor(skinColors[skin_color_array_id]));
        skin_path3.setFillColor(Color.parseColor(skinColors[skin_color_array_id]));

        details_path1.setFillColor(Color.parseColor(skinColors[skin_color_array_id + 1]));
        details_path2.setFillColor(Color.parseColor(skinColors[skin_color_array_id + 1]));
        details_path3.setFillColor(Color.parseColor(skinColors[skin_color_array_id + 1]));
        details_path4.setFillColor(Color.parseColor(skinColors[skin_color_array_id + 1]));
        details_path5.setFillColor(Color.parseColor(skinColors[skin_color_array_id + 1]));
        details_path6.setFillColor(Color.parseColor(skinColors[skin_color_array_id + 1]));
        details_path7.setFillColor(Color.parseColor(skinColors[skin_color_array_id + 1]));
        details_path8.setFillColor(Color.parseColor(skinColors[skin_color_array_id + 1]));

        eye_path_left.setFillColor(Color.parseColor(eyeColors[eye_color_array_id]));
        eye_path_right.setFillColor(Color.parseColor(eyeColors[eye_color_array_id]));
    }
}
