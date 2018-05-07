package voyager.voyager;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {
    // Database Initialization
    private DatabaseReference database,countryRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser fbUser;
    //
    // UI Initialization
    Button btnLogIn, btnSignIn;
    EditText txtname,txtemail,txtpassword,txtlastname,txtpasswordconfirm;
    Spinner spnnationality,spnstate,spncity;
    String name,lastname,email,password,passwordconfirm,nationality,state,city,birth_date;
    TextView txtbirth_date;
    DatePickerDialog datePicker;
    ProgressDialog progressDialog;
    //
    // Variables Initialization
    String fbUserId;
    private ArrayList<HashMap<String,String>> countries;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Database Setup
        database = FirebaseDatabase.getInstance().getReference("User");
        countryRef = FirebaseDatabase.getInstance().getReference("Paises");
        firebaseAuth = FirebaseAuth.getInstance();
        // End Database Setup

        // UI Setup
        progressDialog = new ProgressDialog(this);
        //Hiding status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Hiding action bar
        getSupportActionBar().hide();
        txtname = findViewById(R.id.txtNameSignIn);
        txtlastname = findViewById(R.id.txtLastNameSignIn);
        txtemail = findViewById(R.id.txtEmailSignIn);
        txtpassword = findViewById(R.id.txtPasswordSignIn);
        txtpasswordconfirm = findViewById(R.id.txtPasswordConfirmSignIn);
        spnnationality = findViewById(R.id.sprNationalitySignIn);
        spnstate = findViewById(R.id.sprEstateSignIn);
        spncity = findViewById(R.id.sprCitySignIn);
        btnLogIn = findViewById(R.id.btnLogIn_SignIn);
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });
        btnSignIn = findViewById(R.id.btnSignIn_Signin);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        txtbirth_date = findViewById(R.id.txtBirthDateSignIn);
        txtbirth_date.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR); // current year
                int month = c.get(Calendar.MONTH); // current month
                int day = c.get(Calendar.DAY_OF_MONTH); // current day



                //Date picker dialog
                datePicker = new DatePickerDialog(SignInActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        txtbirth_date.setText(dayOfMonth + "/"+ (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);

                //User must have at least 10 years to sign up
                c.add(Calendar.YEAR,-10);
                datePicker.getDatePicker().setMaxDate(c.getTimeInMillis());
                //User must not have more than 100 years to sign up
                c.add(Calendar.YEAR, -100);
                datePicker.getDatePicker().setMinDate(c.getTimeInMillis());
                datePicker.show();
            }
        });
        // End UI Setup

        //Get countries snapshot
        countryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                saveCountries(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    //Save all countries on a list
        public void saveCountries(DataSnapshot data){
        countries = new ArrayList<>();
            for(DataSnapshot ds:data.getChildren()){
                countries.add((HashMap<String,String>)data.getValue());
            }

            for(int i =0;i<countries.size();i++)
            {
                System.out.println("------------------------> " + countries.get(i).get("nombre_pais_int"));
            }
        }
    //End method save countries
    public void displayProgressDialog(String title, String message){
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(true);
    }
    public void goToLogin(){
        Intent login = new Intent(getApplicationContext(),LogInActivity.class);
        startActivity(login);
        finish();
    }
    public void signIn(){
        displayProgressDialog("Creating Account","Please Wait");
        setUserValues();
        if (verifyData()){
            authRegister();
        }
    }
    public void setUserValues(){
        name = txtname.getText().toString().trim();
        lastname = txtlastname.getText().toString().trim();
        email = txtemail.getText().toString().trim();
        password = txtpassword.getText().toString().trim();
        passwordconfirm = txtpasswordconfirm.getText().toString().trim();
        birth_date = txtbirth_date.getText().toString().trim();
        //nationality = spnnationality.getSelectedItem().toString().trim();
        //state = spnstate.getSelectedItem().toString();
        //city = spncity.getSelectedItem().toString();
    }
    protected boolean verifyData() {
        if (name.isEmpty()) {
            Toast.makeText(this, R.string.Name, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return false;
        }
        if (lastname.isEmpty()) {
            Toast.makeText(this, R.string.Last_name, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return false;
        }
        if (email.isEmpty()) {
            Toast.makeText(this, R.string.Enter_your_email, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, R.string.Enter_your_password, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return false;
        }
        if (passwordconfirm.isEmpty()) {
            Toast.makeText(this, R.string.Confirm_your_password, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return false;
        }
        if (birth_date.isEmpty()) {
            Toast.makeText(this, R.string.Birth_Date, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return false;
        }
//        if(nationality.isEmpty()){
//            Toast.makeText(this,R.string.Nationality,Toast.LENGTH_LONG).show();
//            return false;
//        }
        if (!password.equals(passwordconfirm)) {
            Toast.makeText(this, R.string.Password_match, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return false;
        }
        return true;
    }
    protected void authRegister(){
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        registerUser();
                        sendEmailVerification();
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(SignInActivity.this, R.string.Error, Toast.LENGTH_LONG).show();
                    }
                }
            });
    }
    protected void sendEmailVerification(){
        fbUser = firebaseAuth.getCurrentUser();
        fbUser.sendEmailVerification()
            .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name+" "+lastname).build();
                        fbUser.updateProfile(profileUpdates);
                        Toast.makeText(SignInActivity.this,"Verification email sent to " + fbUser.getEmail(), Toast.LENGTH_LONG).show();
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(SignInActivity.this,"Failed to send verification email.", Toast.LENGTH_LONG).show();
                    }
                }
            });
    }
    protected void registerUser(){
        fbUserId = firebaseAuth.getCurrentUser().getUid();
        final User user = new User(fbUserId,name,lastname,email,birth_date,nationality);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(fbUserId)){
                    try{
                        progressDialog.dismiss();
                        database.child(fbUserId).setValue(user);
                        goToLogin();
                    }
                    catch (DatabaseException e){
                        progressDialog.dismiss();
                        Toast.makeText(SignInActivity.this, "ERROR: " + e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}
