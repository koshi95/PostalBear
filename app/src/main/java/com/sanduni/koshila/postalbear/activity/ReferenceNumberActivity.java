package com.sanduni.koshila.postalbear.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sanduni.koshila.postalbear.R;
import com.sanduni.koshila.postalbear.util.PostalBearDBHelper;

public class ReferenceNumberActivity extends AppCompatActivity {

    private PostalBearDBHelper postalBearDBHelper;

    private EditText referenceNumberEditText;
    private Button searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reference_number);

        // Register Database
        postalBearDBHelper = new PostalBearDBHelper(getApplicationContext());

        referenceNumberEditText = findViewById(R.id.reference_number);
        searchBtn = findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchStr = referenceNumberEditText.getText().toString();
                Intent viewPostIntent = new Intent(getApplicationContext(), AddEditViewPostActivity.class);
                viewPostIntent.putExtra("ACTION", "VIEW");
                viewPostIntent.putExtra("POST_REFERENCE_NUMBER", searchStr);
                startActivity(viewPostIntent);
            }
        });
    }
}