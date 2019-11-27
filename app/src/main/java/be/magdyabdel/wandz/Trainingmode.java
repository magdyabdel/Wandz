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
        TextView yourNameTextView = findViewById(R.id.your_name);
        yourNameTextView.setOnClickListener(this);
        Button trainingmode = findViewById(R.id.training_mode);
        trainingmode.setOnClickListener(this);
        Button multiplayer = findViewById(R.id.multiplayer);
        multiplayer.setOnClickListener(this);
        Button myWand = findViewById(R.id.my_wand);
        myWand.setOnClickListener(this);
        /******* Navigation Drawer *******/

        Button next = findViewById(R.id.next);
        next.setOnClickListener(this);

        Button previous = findViewById(R.id.previous);
        previous.setOnClickListener(this);

        imageView = findViewById(R.id.view_gestures);
        imageView.setImageResource(R.drawable.ic_gesture_horizontal_right);

        appData = (AppData) getIntent().getSerializableExtra("data");
        yourNameTextView.setText(appData.getName_player());
        appData.setProfileImage(this, profileImageView, -1);

    }

    @Override
    public void onClick(View view) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        Intent intent = null;
        switch (view.getId()) {

            /******* Navigation Drawer *******/
            case R.id.profile:
            case R.id.your_name:
                intent = new Intent(this, ChangeProfileIcon.class);
                break;
            case R.id.training_mode:
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                break;
            case R.id.multiplayer:
                intent = new Intent(this, Multiplayer.class);
                break;
            case R.id.my_wand:
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
