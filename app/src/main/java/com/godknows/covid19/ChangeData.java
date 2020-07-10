package com.godknows.covid19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.viewmodel.AuthViewModelBase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangeData extends AppCompatActivity {

    ImageView detailsImageView;
    Button ed4;
    EditText ed1,ed2,ed3,ed5;

    FirebaseAuth mAuth;

    DatabaseReference people,currentUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_data);

        mAuth=FirebaseAuth.getInstance();
        currentUserRef = FirebaseDatabase.getInstance().getReference("lastOnline").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ed1=(EditText) findViewById(R.id.name_ed);
        ed2=(EditText) findViewById(R.id.email_ed);
        ed3=(EditText) findViewById(R.id.phone_ed);
        ed5=(EditText) findViewById(R.id.pass_ed);
        ed4=(Button) findViewById(R.id.change_b);
        ed2.setText(mAuth.getCurrentUser().getEmail());
        DatabaseReference d1=FirebaseDatabase.getInstance().getReference("Register").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        d1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){
                    Object o1=dataSnapshot.child("name").getValue();
                    Object o2=dataSnapshot.child("phone").getValue();
                    String s1=o1.toString();
                    String s2=o2.toString();
                    ed1.setText(s1);
                    ed3.setText(s2);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ed4.setVisibility(View.GONE);
        ed5.setClickable(false);

        detailsImageView=(ImageView) findViewById(R.id.dist);
        detailsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ChangeData.this)
                        .setTitle("Changes in Data?")
                        .setMessage("If you want to change this info pls click yes?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ed4.setVisibility(View.VISIBLE);

                                ed1.setClickable(true);
                                ed2.setClickable(true);
                                ed3.setClickable(true);
                                ed1.setEnabled(true);
                                ed2.setEnabled(true);
                                ed3.setEnabled(true);
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        ed4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=ed1.getText().toString();
                String email=ed2.getText().toString();
                String phone=ed3.getText().toString();
                String pass=ed5.getText().toString();
                FirebaseUser user = mAuth.getCurrentUser();
                people = FirebaseDatabase.getInstance().getReference("Register");
                people.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(new Register(email,
                                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                name,
                                phone));
                user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ChangeData.this, "Email Changes Done", Toast.LENGTH_SHORT).show();
                            ed4.setVisibility(View.INVISIBLE);
                            ed1.setClickable(false);
                            ed2.setClickable(false);
                            ed3.setClickable(false);
                            ed5.setClickable(false);
                            ed1.setEnabled(false);
                            ed2.setEnabled(false);
                            ed3.setEnabled(false);
                            ed5.setEnabled(false);
                            currentUserRef.removeValue();
                        }
                    }
                });

                //                user.updatePassword(pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                //                    @Override
                //                    public void onComplete(@NonNull Task<Void> task) {
                //                        if (task.isSuccessful()) {
                //                            Toast.makeText(ChangeData.this, "Password Changes Done", Toast.LENGTH_SHORT).show();
                //                        }
                //                    }
                //                });

                Toast.makeText(ChangeData.this, "Done Changes Thanku...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });

        detailsImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(ChangeData.this, "You can change data from clicking it!!", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }
}
