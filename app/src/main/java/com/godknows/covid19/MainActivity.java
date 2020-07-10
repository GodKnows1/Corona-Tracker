package com.godknows.covid19;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.LinearGradient;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextView reset_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        mAuth=FirebaseAuth.getInstance();
        //
        reset_pass =(TextView) findViewById(R.id.reset_pass);
        reset_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Reset_pass.class));
            }
        });

        //
//        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar_log);
//        toolbar.setTitle("Finder");
//        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_smartphone_black_24dp);
        CircularProgressButton rel=(CircularProgressButton) findViewById(R.id.cirLoginButton);
        rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email=(EditText) findViewById(R.id.editTextEmail);
                EditText pass=(EditText) findViewById(R.id.editTextPassword);
                if(email.getText().toString().isEmpty() || pass.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Empty Fields!! ", Toast.LENGTH_SHORT).show();
                }else {
                    mAuth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("Done", "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        startActivity(new Intent(getApplicationContext(), ListOnline.class));
                                        finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("Fail", "signInWithEmail:failure", task.getException());
                                        Toast.makeText(MainActivity.this, "Authentication failed Pls see ur credentials...", Toast.LENGTH_SHORT).show();
                                        //updateUI(null);
                                        // ...
                                    }

                                    // ...
                                }
                            });
                }
            }
        });
        TextView reg=(TextView) findViewById(R.id.new_user);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,Register_activity.class);

                startActivity(i);
            }
        });
        //startService(new Intent(this,MyService.class));
        try {
            stopService(new Intent(this, MyService.class));
        }catch (Exception e){

        }
    }
}
