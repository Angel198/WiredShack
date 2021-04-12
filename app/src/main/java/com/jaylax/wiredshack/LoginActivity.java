package com.jaylax.wiredshack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextClock;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    RadioGroup radioGroup;
    RadioButton radioButton;
    EditText username, password;
    TextView login;
    String _email,_password;
    TextView forget_pass, register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login = (TextView) findViewById(R.id.text_login);
        forget_pass = (TextView) findViewById(R.id.forget_pass);
        register = (TextView) findViewById(R.id.text_register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton)findViewById(selectedId);

                _email = username.getText().toString();
                _password = password.getText().toString();

            }

        });

        forget_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(intent);
            }
        });

    }
}