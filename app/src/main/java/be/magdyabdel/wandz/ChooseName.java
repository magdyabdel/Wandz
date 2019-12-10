package be.magdyabdel.wandz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChooseName extends AppCompatActivity implements View.OnClickListener{

    private EditText editText;
    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_your_name);

        Button button = findViewById(R.id.enter);
        button.setOnClickListener(this);

        editText = findViewById(R.id.name_field);

        editText.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        profile = (Profile) getIntent().getSerializableExtra("profile");
    }

    @Override
    public void onPause() {
        super.onPause();

        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    @Override
    public void onClick(View view) {

        String name = editText.getText().toString();

        switch (view.getId()){
            case R.id.enter:

                if(!name.equals("")){
                    profile.setName(name);
                    Intent intent;
                    if (profile.getSkip()) {
                        intent = new Intent(this, Menu.class);
                        intent.putExtra("skip", true);
                    } else {
                        intent = new Intent(this, WandExplanation.class);
                    }
                    intent.putExtra("profile", profile);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast toast = Toast.makeText(this, "Fill in your name before entering!", Toast.LENGTH_SHORT);
                    toast.show();
                }

                break;
            default:
                break;
        }
    }
}
