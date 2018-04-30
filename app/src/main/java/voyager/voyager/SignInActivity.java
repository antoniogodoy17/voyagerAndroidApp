package voyager.voyager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class SignInActivity extends AppCompatActivity {
    Button btnLogIn, btnSignIn;
    EditText txtname,txtemail,txtpassword,txtlastname,txtpasswordconfirm;
    Spinner spnnationality,spnstate,spncity;
    String name,lastname,email,password,passwordconfirm,nationality,state,city,birth_date;
    TextView txtbirth_date;
    DatePickerDialog datePicker;
    FirebaseUser fbUser;

    DatabaseReference database;
    private FirebaseAuth firebaseAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        //Database reference
        database = FirebaseDatabase.getInstance().getReference("User");
        firebaseAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        //Hiding status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Hiding action bar
        getSupportActionBar().hide();

        //Elements initialization
        btnLogIn = findViewById(R.id.btnLogIn_SignIn);
        btnSignIn = findViewById(R.id.btnSignIn_Signin);
        txtname = findViewById(R.id.txtNameSignIn);
        txtlastname = findViewById(R.id.txtLastNameSignIn);
        txtemail = findViewById(R.id.txtEmailSignIn);
        txtpassword = findViewById(R.id.txtPasswordSignIn);
        txtpasswordconfirm = findViewById(R.id.txtPasswordConfirmSignIn);
        txtbirth_date = findViewById(R.id.txtBirthDateSignIn);
        spnnationality = findViewById(R.id.sprNationalitySignIn);
        spnstate = findViewById(R.id.sprEstateSignIn);
        spncity = findViewById(R.id.sprCitySignIn);
        btnSignIn = findViewById(R.id.btnSignIn_Signin);

        //Elements Listener
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent login = new Intent(getApplicationContext(),LogInActivity.class);
            startActivity(login);
            finish();
            }
        });
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
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                btnSignIn.setVisibility(View.GONE);
                set_user_values();
                if (verify_data()){
                    auth_register();
                }
                else{
                    btnSignIn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }

            }
        });
    }

    protected void set_user_values(){
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

    protected boolean verify_data() {
        if (name.isEmpty()) {
            Toast.makeText(this, R.string.Name, Toast.LENGTH_LONG).show();
            return false;
        }
        if (lastname.isEmpty()) {
            Toast.makeText(this, R.string.Last_name, Toast.LENGTH_LONG).show();
            return false;
        }
        if (email.isEmpty()) {
            Toast.makeText(this, R.string.Enter_your_email, Toast.LENGTH_LONG).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, R.string.Enter_your_password, Toast.LENGTH_LONG).show();
            return false;
        }
        if (passwordconfirm.isEmpty()) {
            Toast.makeText(this, R.string.Confirm_your_password, Toast.LENGTH_LONG).show();
            return false;
        }
        if (birth_date.isEmpty()) {
            Toast.makeText(this, R.string.Birth_Date, Toast.LENGTH_LONG).show();
            return false;
        }
//        if(nationality.isEmpty()){
//            Toast.makeText(this,R.string.Nationality,Toast.LENGTH_LONG).show();
//            return false;
//        }
        if (!password.equals(passwordconfirm)) {
            Toast.makeText(this, R.string.Password_match, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    protected void auth_register(){
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //                            register_user();
                    //                            // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(SignInActivity.this, R.string.Successful, Toast.LENGTH_LONG).show();
                    register_user();
                    auth_email_verification();
                }
                else {
                    Toast.makeText(SignInActivity.this, R.string.Error, Toast.LENGTH_LONG).show();
                    // If sign in fails, display a message to the user.
                    btnSignIn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                }
            });
    }

    protected void auth_email_verification(){
        fbUser = firebaseAuth.getCurrentUser();
        fbUser.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignInActivity.this,"Verification email sent to " + fbUser.getEmail(), Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(SignInActivity.this,"Failed to send verification email.", Toast.LENGTH_LONG).show();
                            btnSignIn.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
    protected void register_user(){
        //Create Object user and register it to firebase, and create a user for authentication.
        try {
            String id = database.push().getKey();
            User user = new User(id,name,lastname,email,birth_date,nationality,nationality,city);
            database.child(id).setValue(user);
            //Email validation?

            //Return to log in view
            Intent login = new Intent(getApplicationContext(),LogInActivity.class);
            startActivity(login);
            finish();
        }
        catch (DatabaseException e){
            Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show();
        }
    }
}
