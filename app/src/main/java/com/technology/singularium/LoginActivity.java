package com.technology.singularium;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout passLayout;
    TextInputEditText username, password;
    MaterialButton signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        passLayout = findViewById(R.id.passLayout);
        signIn = findViewById(R.id.signIn);

        signIn.setOnClickListener(view -> {
            String nameString = username.getText().toString();
            String passString = password.getText().toString();

            if(nameString.equals("")){
                username.setError("Please fill your username");
                return;
            }

            if(passString.equals("")){
                passLayout.setError("Please fill your password");
                return;
            }

            username.setError(null);
            passLayout.setError(null);

            if(nameString.equals(getString(R.string.username)) && passString.equals(getString(R.string.password))) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }

        });
    }
}
