package solesurvivors.vincent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText mNameField;
    private  EditText mEmailField;
    private EditText mPasswordField;
    private Button mRegisterField;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        mProgress = new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        mNameField=(EditText)findViewById(R.id.nameField);
        mNameField.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mEmailField=(EditText)findViewById(R.id.emailField);
        mEmailField.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mPasswordField=(EditText)findViewById(R.id.passwordField);
        mPasswordField.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mRegisterField=(Button) findViewById(R.id.registerField);

        mRegisterField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });

    }

    private void startRegister() {

        final String name=mNameField.getText().toString().trim();
        String email=mEmailField.getText().toString().trim();
        String password=mPasswordField.getText().toString().trim();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty((password)))
        {
            mProgress.setMessage("Signing Up");
            mProgress.show();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        String user_id=mAuth.getCurrentUser().getUid();
                        DatabaseReference UserData=mDatabase.child(user_id);
                        UserData.child("Name").setValue(name);
                        UserData.child("Image").setValue("default");
                        mProgress.dismiss();



                        for(int i=0;i<3000;i++)
                        {
                            UserData.child(Integer.toString(i)).setValue(0.0);
                        }


                        Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    }
                    else
                    {

                        Toast.makeText(getApplicationContext(),"Invalid Details\n Try Again",Toast.LENGTH_LONG).show();
                        mProgress.dismiss();
                    }
                }
            });
        }






    }

}
