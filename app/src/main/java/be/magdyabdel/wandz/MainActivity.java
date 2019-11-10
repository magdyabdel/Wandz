package be.magdyabdel.wandz;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;
    private Button button;
    private Typeface typeFace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.wizard);
        mImageView.setImageResource(R.drawable.welcome_wizard);

        typeFace = Typeface.createFromAsset(getAssets(),"fonts/MagicSchoolOne.ttf");
        button = (Button) findViewById(R.id.button);
        button.setTypeface(typeFace);
    }
}
