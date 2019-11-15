package be.magdyabdel.wandz;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MasterActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            default:
                break;
        }
    }
}
