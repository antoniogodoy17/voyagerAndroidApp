package voyager.voyager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
<<<<<<< HEAD
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
=======
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
>>>>>>> 8c14a0da7f6fc4bc22b3ff134330ff3b2f44bd1e

public class SignInActivity extends AppCompatActivity {


    Button btnLogIn;
    Button btnSignIn;
<<<<<<< HEAD
    EditText txtname,txtemail,txtpassword,txtbirth_date,txtlastname,txtpasswordconfirm;
    Spinner spnnationality,spnstate,spncity;
    DatabaseReference database;
    String name,lastname,email,password,passwordconfirm,nationality,state,city,birth_date;

=======
    EditText txtname,txtemail,txtpassword;
    TextView txtbirth_date;
    Spinner spnnationality,spnstate,spncity;
    DatePickerDialog datePicker;
>>>>>>> 8c14a0da7f6fc4bc22b3ff134330ff3b2f44bd1e

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        database = FirebaseDatabase.getInstance().getReference("User");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        btnLogIn = findViewById(R.id.btnLogIn_SignIn);
        btnSignIn = findViewById(R.id.btnSignIn_Signin);
        txtname = findViewById(R.id.txtNameSignIn);
        txtemail = findViewById(R.id.txtEmailSignIn);
        txtpassword = findViewById(R.id.txtPasswordSignIn);
        txtbirth_date = findViewById(R.id.txtBirthDateSignIn);
        spnnationality = findViewById(R.id.sprNationalitySignIn);
        spnstate = findViewById(R.id.sprEstateSignIn);
        spncity = findViewById(R.id.sprCitySignIn);
        btnSignIn = findViewById(R.id.btnSignIn_Signin);

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(getApplicationContext(),LogInActivity.class);
                startActivity(login);
                finish();
            }
        });

<<<<<<< HEAD
        txtname = findViewById(R.id.txtNameSignIn);
        //txtlastname = findViewById(R.id.txtLastname);
        txtemail = findViewById(R.id.txtEmailSignIn);
        txtpassword = findViewById(R.id.txtPasswordSignIn);
        //txtpassword = findViewById(R.id.txtPasswordConfirm);
        txtbirth_date = findViewById(R.id.txtBirthDateSignIn);
        spnnationality = findViewById(R.id.sprNationalitySignIn);
        spnstate = findViewById(R.id.sprEstateSignIn);
        spncity = findViewById(R.id.sprCitySignIn);
        btnSignIn = findViewById(R.id.btnSignIn_Signin);
=======
        txtbirth_date.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final Calendar c = Calendar.getInstance();
                int selYear = c.get(Calendar.YEAR); // current year
                int selMonth = c.get(Calendar.MONTH); // current month
                int selDay = c.get(Calendar.DAY_OF_MONTH); // current day

                // date picker dialog
                datePicker = new DatePickerDialog(SignInActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        txtbirth_date.setText(dayOfMonth + "/"+ (monthOfYear + 1) + "/" + year);
                    }
                }, selYear, selMonth, selDay);
                c.add(Calendar.YEAR,-10);
                datePicker.getDatePicker().setMaxDate(c.getTimeInMillis());
                c.add(Calendar.YEAR, -100);
                datePicker.getDatePicker().setMinDate(c.getTimeInMillis());
                datePicker.show();
            }
        });
>>>>>>> 8c14a0da7f6fc4bc22b3ff134330ff3b2f44bd1e
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_user_values();
                if (verify_data() == true) register_user();

            }
        });
    }

    protected void register_user(){
        //Create Object user and register it to firebase, and create a user for authentication.
        String id = database.push().getKey();
        User user = new User(id,name,lastname,email,birth_date,nationality,nationality,city);
        database.child(id).setValue(user);

<<<<<<< HEAD
//        btnSignIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent home = new Intent(getApplicationContext(),homeActivity.class);
//                startActivity(home);
//                finish();
//            }
//        });

    }

    protected boolean verify_data(){

        if(name.isEmpty()){
            Toast.makeText(this,"@strings/Name",Toast.LENGTH_LONG).show();
            return false;
        }

//        if(lastname.isEmpty()){
//            Toast.makeText(this,"@strings/Last_name",Toast.LENGTH_LONG).show();
//            return false;
//        }

        if(email.isEmpty()){
            Toast.makeText(this,"@strings/Enter_your_email",Toast.LENGTH_LONG).show();
            return false;
        }

        if(password.isEmpty()){
            Toast.makeText(this,"@strings/Enter_your_password",Toast.LENGTH_LONG).show();
            return false;
        }
//
//        if(passwordconfirm.isEmpty()){
//            Toast.makeText(this,"@strings/Confirm_your_password",Toast.LENGTH_LONG).show();
//            return false;
//        }

        if(birth_date.isEmpty()){
            Toast.makeText(this,"@strings/Birth_Date",Toast.LENGTH_LONG).show();
            return false;
        }

//        if(nationality.isEmpty()){
//            Toast.makeText(this,"@strings/Nationality",Toast.LENGTH_LONG).show();
//            return false;
//        }

//       if(!password.equals(passwordconfirm)) {
//           Toast.makeText(this,"@strings/Password_match",Toast.LENGTH_LONG).show();
//           return false;
//       }
       return true;
    }
=======
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(getApplicationContext(),ProfileActivity.class);
                startActivity(profile);
                finish();
            }
        });
>>>>>>> 8c14a0da7f6fc4bc22b3ff134330ff3b2f44bd1e

    protected void set_user_values(){
        name = txtname.getText().toString().trim();
        //lastname = txtlastname.getText().toString().trim();
        email = txtemail.getText().toString().trim();
        password = txtpassword.getText().toString().trim();
        //passwordconfirm = txtpasswordconfirm.getText().toString().trim();
        birth_date = txtbirth_date.getText().toString().trim();
        //nationality = spnnationality.getSelectedItem().toString().trim();
        //state = spnstate.getSelectedItem().toString();
        //city = spncity.getSelectedItem().toString();
    }
}
