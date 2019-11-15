package be.magdyabdel.wandz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class Trainingmode extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private int[] gestureImages = new int[]{R.drawable.ic_gesture_horizontal_right, R.drawable.ic_gesture_horizontal_left,
            R.drawable.ic_gesture_vertical_down, R.drawable.ic_gesture_vertical_up, R.drawable.ic_gesture_round};
    private int currentGestureImage = 0;

    private AppData appData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /******* Navigation Drawer *******/
        setContentView(R.layout.activity_navigation_drawer);

        ViewStub stub = findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.activity_training_mode);
        stub.inflate();

        ImageView profileImageView = findViewById(R.id.profile);
        profileImageView.setOnClickListener(this);

        TextView trainingmodeTextView = findViewById(R.id.training_mode);
        trainingmodeTextView.setOnClickListener(this);

        ImageView trainingmodeImageView = findViewById(R.id.training_mode_image);
        trainingmodeImageView.setOnClickListener(this);

        TextView multiplayerTextView = findViewById(R.id.multiplayer);
        multiplayerTextView.setOnClickListener(this);

        ImageView multiplayerImageView = findViewById(R.id.multiplayer_image);
        multiplayerImageView.setOnClickListener(this);

        TextView myWandTextView = findViewById(R.id.my_wand);
        myWandTextView.setOnClickListener(this);

        ImageView myWandImageView = findViewById(R.id.my_wand_image);
        myWandImageView.setOnClickListener(this);
        /******* Navigation Drawer *******/

        Button next = findViewById(R.id.next);
        next.setOnClickListener(this);

        Button previous = findViewById(R.id.previous);
        previous.setOnClickListener(this);

        imageView = findViewById(R.id.view_gestures);
        imageView.setImageResource(R.drawable.ic_gesture_horizontal_right);

        appData = (AppData) getIntent().getSerializableExtra("data");

        TextView yourNameTextView = findViewById(R.id.your_name);
        yourNameTextView.setText(appData.getName_player());

    }

    @Override
    public void onClick(View view) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        Intent intent = null;
        switch (view.getId()) {
            /******* Navigation Drawer *******/
            case R.id.profile:
                intent = new Intent(this, Profile.class);
                break;
            case R.id.training_mode:
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                break;
            case R.id.training_mode_image:
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                break;
            case R.id.multiplayer:
                intent = new Intent(this, Multiplayer.class);
                break;
            case R.id.multiplayer_image:
                intent = new Intent(this, Multiplayer.class);
                break;
            case R.id.my_wand:
                intent = new Intent(this, MyWand.class);
                break;
            case R.id.my_wand_image:
                intent = new Intent(this, MyWand.class);
                break;
            /******* Navigation Drawer *******/

            case R.id.previous:
                changeGestureImage(1);
                break;
            case R.id.next:
                changeGestureImage(-1);
                break;

            default:
                break;
        }

        if (intent != null) {
            intent.putExtra("data", appData);
            startActivity(intent);
            finish();
        }
    }

    public void changeGestureImage(int direction) {
        if (direction == 1) {
            currentGestureImage++;
            if (currentGestureImage >= gestureImages.length) {
                currentGestureImage = 0;
            }
        }
        if (direction == -1) {
            currentGestureImage--;
            if (currentGestureImage < 0) {
                currentGestureImage = gestureImages.length - 1;
            }
        } else {
        }
        imageView.setImageResource(gestureImages[currentGestureImage]);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
