package solesurvivors.vincent;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPage extends AppCompatActivity {

    private TextView textView;
    private EditText emailField;
    private EditText passwordField;
    private Button LoginField;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_page);
        mAuth=FirebaseAuth.getInstance();
        emailField=(EditText)findViewById(R.id.email);
        passwordField=(EditText)findViewById(R.id.password);
        LoginField=(Button) findViewById(R.id.mLogin);

        LoginField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogin();
            }
        });



        textView = (TextView) findViewById(R.id.register);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignUpActivity();
            }
        });


    }


    private void checkLogin() {

        String email=emailField.getText().toString().trim();
        String password=passwordField.getText().toString().trim();
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))
        {
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Intent Mainintent=new Intent(LoginPage.this,MainActivity.class);
                        Mainintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(Mainintent);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Invalid Email/Password",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


    public void openSignUpActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
