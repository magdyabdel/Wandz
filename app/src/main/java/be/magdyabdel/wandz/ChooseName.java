package be.magdyabdel.wandz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChooseName extends AppCompatActivity implements View.OnClickListener{

    private EditText editText;
    private Boolean skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_your_name);

        Button button = findViewById(R.id.enter);
        button.setOnClickListener(this);

        editText = findViewById(R.id.name_field);
        skip = (Boolean) getIntent().getSerializableExtra("skip");
    }

    @Override
    public void onClick(View view) {

        String name = editText.getText().toString();

        switch (view.getId()){
            case R.id.enter:

                if(!name.equals("")){
                    Profile profile = new Profile(-1, name, 0000);
                    Intent intent;
                    if (skip) {
                        intent = new Intent(this, ChooseYourWand.class);
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
