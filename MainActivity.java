/* Title : FireNot App
 * Version : 1.2
 * Language : Android Studio
 * Programmer : Tom Rho
 * Date : 09/12/2017
 */
package com.example.tg.firenot;

import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

//Main activity
public class MainActivity extends AppCompatActivity /*implements View.OnClickListener*/{

    private static final String TAG = "MainActivity";
    private String myToken;
//    private Button buttonDisplayToken;
//    private TextView textViewToken;

    CheckBox chSubscribe;
    Button btSubscribe;
    public TextView tvResult;
    EditText etName;
    EditText etEmail;

    FirebaseDatabase mdatabase;
    DatabaseReference myRef;
    private String userId;

   @Override
   //Get Firebase token from google cloud
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//       getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//               | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//               | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
//        textViewToken = (TextView) findViewById(R.id.textViewToken);
//        buttonDisplayToken = (Button) findViewById(R.id.buttonDisplayToken);

        //adding listener to view
//        buttonDisplayToken.setOnClickListener(this);
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        FirebaseInstanceId.getInstance().getToken();

        chSubscribe = (CheckBox) findViewById(R.id.ch_checkbox);
        btSubscribe = (Button) findViewById(R.id.bt_register);
        tvResult = (TextView) findViewById(R.id.tv_result);
        etName = (EditText) findViewById(R.id.et_name);
        etEmail = (EditText) findViewById(R.id.et_email);

        myToken = FirebaseInstanceId.getInstance().getToken();
        if (myToken != null) {
            tvResult.setText(myToken.substring(0, 70));
        }

        mdatabase = FirebaseDatabase.getInstance();
        myRef = mdatabase.getReference("users");

        //Changing Registering button text
        if (chSubscribe.isChecked()) {
            btSubscribe.setText("Register");
        } else {
            btSubscribe.setText("Remove");
        }

        //Get data from database after searching token
        Query myQuery = myRef.orderByChild("token").equalTo(myToken);
        myQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    String name = user.getName().toString();
                    String email = user.getEmail().toString();
                    tvResult.setText("Name: " + name + " Email: " + email + " is registered.");
                    etName.setText(name);
                    etEmail.setText(email);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //
                Log.d(TAG, "Failed to read value", databaseError.toException());
            }
        });

        //Checkbox click event - Changing check listner
        chSubscribe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //
                    btSubscribe.setText("Register");
                } else {
                    btSubscribe.setText("Remove");
                }
            }
        });

        //Register button click event
        btSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etName.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Input your name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etEmail.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Input your mail", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (chSubscribe.isChecked()) {
                    //Register
                    //FirebaseMessaging.getInstance().subscribeToTopic("news");
                    Log.d(TAG, "Register: Yes");

                    //Check if there are already data or not
                    if (TextUtils.isEmpty(userId)) {
                        createUser(etName.getText().toString(), etEmail.getText().toString(), myToken);
                    } else {
                        updateUser(etName.getText().toString(), etEmail.getText().toString(), myToken);
                    }

                } else {
                    //Delete user in DB completely
                    deleteUser(myToken);
                    Log.d(TAG, "Register: Cancel");
                }
            }
        });
    }

    //Create a new user under users' node
        private void createUser(String name, String email, String token) {
            //Devide from TODO registering
            ///Create a new User if there is no token
            //Create User ID
            Query myQuery = myRef.orderByChild("token").equalTo(myToken);
            //Check
            if(TextUtils.isEmpty(userId)){
                userId = myRef.push().getKey();
            }
            User user = new User(name, email, token);
            myRef.child(userId).setValue(user);
        }

        //Update the value if there is userid
        private void updateUser(String name, String email, String token){
            if (!TextUtils.isEmpty(name)){
                myRef.child(userId).child("name").setValue(name);
            }
            if (!TextUtils.isEmpty(email)){
                myRef.child(userId).child("email").setValue(email);
            }
            if (!TextUtils.isEmpty(token)){
                myRef.child(userId).child("token").setValue(token);
            }
        }

        //Delete
        private void deleteUser(final String token){
            //Delete at DB
            Query myQuery = myRef.orderByChild("token").equalTo(token);
            myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        snapshot.getRef().removeValue();
                        Toast.makeText(getApplicationContext(), "Delete completely!", Toast.LENGTH_SHORT).show();
                        if(myToken!=null) {
                            tvResult.setText(myToken.substring(0, 100));
                        }
                    }
                }

                @Override
                //Cancel because of database error
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "Failed to read value", databaseError.toException());
                }
            });
        }

    //Open map activity
    public void mapLocation (View aView)
    {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
/*    @Override
    public void onClick(View view) {
        if (view == buttonDisplayToken) {
            //getting token from shared preferences
            String token = SharedPrefManager.getInstance(this).getDeviceToken();

            //if token is not null
            if (token != null) {
                //displaying the token
                textViewToken.setText(token);
            } else {
                //if token is null that means something wrong
                textViewToken.setText("Token not generated");
            }
        }
    }
    */

}
