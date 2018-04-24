package voyager.voyager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {
    Button btnSignIn, btnLogin;
    EditText txtEmail, txtPassword;
    String email, password;

    FirebaseAuth firebaseAuth;
    FirebaseUser fbUser;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        //Database reference
        firebaseAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        //Hiding status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Hiding action bar
        getSupportActionBar().hide();

        //Elements initialization
        btnLogin = findViewById(R.id.btnLogIn);
        btnSignIn = findViewById(R.id.btnSignIn);
        txtEmail = findViewById(R.id.txtEmailLogin);
        txtPassword = findViewById(R.id.txtPasswordLogIn);

        //Elements Listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.GONE);
                set_user_values();
                if (verify_data())
                    auth_SignIn();
                else{
                    btnLogin.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signin = new Intent(getApplicationContext(),SignInActivity.class);
                startActivity(signin);
    //          overridePendingTransition(R.anim.alpha_transition,R.anim.alpha_transition);
                finish();
            }
        });
    }
    public void set_user_values(){
        email = txtEmail.getText().toString().trim();
        password = txtPassword.getText().toString().trim();
    }
    protected boolean verify_data() {
        if (email.isEmpty()) {
            Toast.makeText(this, R.string.Enter_your_email, Toast.LENGTH_LONG).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, R.string.Enter_your_password, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    protected void auth_SignIn(){
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            //Animation to wait until authorization is completed
            if(task.isSuccessful()){
                fbUser = firebaseAuth.getCurrentUser();
                if (fbUser.isEmailVerified()){
                    Intent home = new Intent(getApplicationContext(),homeActivity.class);
                    startActivity(home);
                    finish();
                }else{
                    Toast.makeText(LogInActivity.this,"Your email is not verified yet.", Toast.LENGTH_LONG).show();
                    btnLogin.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }

            }else{
                Toast.makeText(LogInActivity.this,"The email or password is incorrect.", Toast.LENGTH_LONG).show();
                btnLogin.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
            }
        });
    }
}
