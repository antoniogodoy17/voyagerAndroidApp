package voyager.voyager.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import voyager.voyager.R;

public class LogInActivity extends AppCompatActivity {
    // Database Declarations
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseUser fbUser;
    //
    // UI Declarations
    Button btnSignIn, btnLogin;
    EditText txtEmail, txtPassword;
    ProgressDialog progressDialog;
    // Variables Declarations
    String email, password;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // Database Initialization
        firebaseAuth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                fbUser = firebaseAuth.getCurrentUser();
                if(fbUser != null && fbUser.isEmailVerified()){
                    goToHome();
                }
            }
        };
        firebaseAuth.addAuthStateListener(authListener);
        // End Database Initialization

        // UI Initialization
        progressDialog = new ProgressDialog(this);
        //Hiding status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Hiding action bar
        getSupportActionBar().hide();
        txtEmail = findViewById(R.id.txtEmailLogin);
        txtPassword = findViewById(R.id.txtPasswordLogIn);
        btnLogin = findViewById(R.id.btnLogIn);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignin();
            }
        });
        // End UI Initialization
    }

    @Override
    protected void onDestroy() {
        firebaseAuth.removeAuthStateListener(authListener);
        super.onDestroy();
    }

    public void displayProgressDialog(int title, int message){
        progressDialog.setTitle(title);
        progressDialog.setMessage(getApplicationContext().getString(message));
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(true);
    }

    public void logIn(){
        displayProgressDialog(R.string.Logging_In,R.string.Please_Wait);
        setUserValues();
        if (verifyData()){
            authLogin();
        }
    }

    public void setUserValues(){
        email = txtEmail.getText().toString().trim();
        password = txtPassword.getText().toString().trim();
    }

    protected boolean verifyData() {
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
        return true;
    }

    protected void authLogin(){
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            //Animation to wait until authorization is completed
            if(task.isSuccessful()){
                fbUser = firebaseAuth.getCurrentUser();
                if (fbUser.isEmailVerified()){
                    progressDialog.dismiss();
                    goToHome();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(LogInActivity.this,"Your email is not verified yet.", Toast.LENGTH_LONG).show();
                }
            }else{
                progressDialog.dismiss();
                Toast.makeText(LogInActivity.this,"The email or password is incorrect.", Toast.LENGTH_LONG).show();
            }
            }
        });
    }

    public void goToHome(){
        Intent home = new Intent(getApplicationContext(),HomeActivity.class);
        startActivity(home);
        finish();
    }

    public void goToSignin(){
        Intent signin = new Intent(getApplicationContext(),SignInActivity.class);
        startActivity(signin);
    }
}
