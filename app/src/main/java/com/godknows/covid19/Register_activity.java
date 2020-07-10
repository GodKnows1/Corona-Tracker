package com.godknows.covid19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class Register_activity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    DatabaseReference people;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_activity);

        mAuth=FirebaseAuth.getInstance();

        TextView back_to_login=(TextView)findViewById(R.id.reg_log);
        back_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        //
        ImageView go_back=(ImageView) findViewById(R.id.go_back_img);
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register_activity.super.onBackPressed();
            }
        });
        //
        CircularProgressButton reg_button=(CircularProgressButton) findViewById(R.id.cirRegisterButton);
        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText name = (EditText) findViewById(R.id.editTextName);
                EditText email = (EditText) findViewById(R.id.editTextEmail);
                final EditText ph = (EditText) findViewById(R.id.editTextMobile);
                EditText password = (EditText) findViewById(R.id.editTextPassword);
                if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty() || name.getText().toString().isEmpty() || ph.getText().toString().isEmpty()) {
                    Toast.makeText(Register_activity.this, "Empty Fields!! ", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(Register_activity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("Success", "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        people = FirebaseDatabase.getInstance().getReference("Register");
                                        people.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(new Register(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                                        FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                                        name.getText().toString(),
                                                        ph.getText().toString()));
                                        Toast.makeText(Register_activity.this, "Done Registration...", Toast.LENGTH_SHORT).show();
                                        //updateUI(user);
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("Fail", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(Register_activity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                        //updateUI(null);
                                    }
                                }
                            });
                }
            }
        });
    }
}
