package com.jaylax.wiredshack;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ForgetPasswordActivity extends AppCompatActivity {

    EditText username;
    String _email;
    TextView code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        username = (EditText) findViewById(R.id.username);
        code = (TextView) findViewById(R.id.send_code);

        code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _email = username.getText().toString();
            }
        });
    }
}