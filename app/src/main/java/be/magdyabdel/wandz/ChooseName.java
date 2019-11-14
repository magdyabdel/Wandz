package be.magdyabdel.wandz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChooseName extends AppCompatActivity implements View.OnClickListener{

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_your_name);

        ImageView imageView = findViewById(R.id.wizard);
        imageView.setImageResource(R.drawable.ic_name_wizard_black_wand);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(this);

        editText = findViewById(R.id.name_field);
    }

    @Override
    public void onClick(View view) {

        String name = editText.getText().toString();
        //Toast.makeText(this, name, Toast.LENGTH_SHORT).show();

        switch (view.getId()){
            case R.id.button:

                if(!name.equals("")){
                    Intent intent = new Intent(this, LearnGesture.class);
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
