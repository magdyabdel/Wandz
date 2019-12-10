package be.magdyabdel.wandz;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;

import com.devs.vectorchildfinder.VectorChildFinder;

import java.io.Serializable;
import java.util.Random;

public class Profile implements Serializable {

    private String[] outfitColors;
    private String[] outfitColorsTwo;
    private String[] skinColors;
    private String[] skinDetailColors;
    private String[] hairColors;
    private String[] eyeColors;
    private String name;
    private int id = -1;
    private int outfit_color_array_id = 0;
    private int skin_color_array_id = 0;
    private int hair_color_array_id = 0;
    private int eye_color_array_id = 0;
    private int health = 1000;
    private Boolean demo = true;
    private Boolean skip = false;

    public Boolean getSkip() {
        return skip;
    }

    public void setSkip(Boolean skip) {
        this.skip = skip;
    }

    public Boolean getDemo() {
        return demo;
    }

    public void setDemo(Boolean demo) {
        this.demo = demo;
    }

    public Profile(int id, String name, int layoutNumbers) {
        this.name = name;
        this.id = id;
        setLayoutNumbers(layoutNumbers);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLayoutNumbers() {
        return outfit_color_array_id + skin_color_array_id * 10 + hair_color_array_id * 100 + eye_color_array_id * 1000;
    }

    public String[] getOutfitColors() {
        return outfitColors;
    }

    public String[] getOutfitColorsTwo() {
        return outfitColorsTwo;
    }

    public int getOutfit_color_array_id() {
        return outfit_color_array_id;
    }

    public void setLayoutNumbers(int layoutNumbers) {

        int[] array_ids = new int[4];
        int index = 0;
        int number = layoutNumbers;
        while (number > 0) {
            array_ids[index] = number % 10;
            index++;
            number = number / 10;
        }

        outfit_color_array_id = array_ids[0];
        skin_color_array_id = array_ids[1];
        hair_color_array_id = array_ids[2];
        eye_color_array_id = array_ids[3];

    }

    public String[] getSkinColors() {
        return skinColors;
    }

    public String[] getHairColors() {
        return hairColors;
    }

    public String[] getEyeColors() {
        return eyeColors;
    }

    public int getSkin_color_array_id() {
        return skin_color_array_id;
    }

    public int getHair_color_array_id() {
        return hair_color_array_id;
    }

    public int getEye_color_array_id() {
        return eye_color_array_id;
    }

    public void setProfileImage(Context context, ImageView profile) {

        outfitColors = context.getResources().getStringArray(R.array.outfit_array);
        outfitColorsTwo = context.getResources().getStringArray(R.array.outfit_array_two);
        skinColors = context.getResources().getStringArray(R.array.skin_array);
        skinDetailColors = context.getResources().getStringArray(R.array.skin_details);
        hairColors = context.getResources().getStringArray(R.array.hair_array);
        eyeColors = context.getResources().getStringArray(R.array.eye_array);

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

        cape_path_light.setFillColor(Color.parseColor(outfitColors[outfit_color_array_id]));
        hat_path_light.setFillColor(Color.parseColor(outfitColors[outfit_color_array_id]));
        cape_path_dark1.setFillColor(Color.parseColor(outfitColorsTwo[outfit_color_array_id]));
        cape_path_dark2.setFillColor(Color.parseColor(outfitColorsTwo[outfit_color_array_id]));
        hat_path_dark.setFillColor(Color.parseColor(outfitColorsTwo[outfit_color_array_id]));

        hair_path1.setFillColor(Color.parseColor(hairColors[hair_color_array_id]));
        hair_path2.setFillColor(Color.parseColor(hairColors[hair_color_array_id]));
        hair_path3.setFillColor(Color.parseColor(hairColors[hair_color_array_id]));

        skin_path1.setFillColor(Color.parseColor(skinColors[skin_color_array_id]));
        skin_path2.setFillColor(Color.parseColor(skinColors[skin_color_array_id]));
        skin_path3.setFillColor(Color.parseColor(skinColors[skin_color_array_id]));

        details_path1.setFillColor(Color.parseColor(skinDetailColors[skin_color_array_id]));
        details_path2.setFillColor(Color.parseColor(skinDetailColors[skin_color_array_id]));
        details_path3.setFillColor(Color.parseColor(skinDetailColors[skin_color_array_id]));
        details_path4.setFillColor(Color.parseColor(skinDetailColors[skin_color_array_id]));
        details_path5.setFillColor(Color.parseColor(skinDetailColors[skin_color_array_id]));
        details_path6.setFillColor(Color.parseColor(skinDetailColors[skin_color_array_id]));
        details_path7.setFillColor(Color.parseColor(skinDetailColors[skin_color_array_id]));
        details_path8.setFillColor(Color.parseColor(skinDetailColors[skin_color_array_id]));

        eye_path_left.setFillColor(Color.parseColor(eyeColors[eye_color_array_id]));
        eye_path_right.setFillColor(Color.parseColor(eyeColors[eye_color_array_id]));
    }

    public void changeProfileByButton(int id) {
        if (id != -1) {
            switch (id) {
                case R.id.outfit_left:

                    outfit_color_array_id--;
                    if (outfit_color_array_id < 0) {
                        outfit_color_array_id = outfitColors.length - 1;
                    }
                    break;
                case R.id.outfit_right:

                    outfit_color_array_id++;
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

                    skin_color_array_id--;
                    if (skin_color_array_id < 0) {
                        skin_color_array_id = skinColors.length - 1;
                    }
                    break;
                case R.id.skin_right:

                    skin_color_array_id++;
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
                    outfit_color_array_id = new Random().nextInt(outfitColors.length);
                    skin_color_array_id = new Random().nextInt(skinColors.length);
                    hair_color_array_id = new Random().nextInt(hairColors.length);
                    eye_color_array_id = new Random().nextInt(eyeColors.length);

                    break;

                default:
                    break;
            }
        }
    }
}
