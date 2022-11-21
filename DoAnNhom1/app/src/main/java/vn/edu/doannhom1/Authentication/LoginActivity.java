package vn.edu.doannhom1.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import vn.edu.doannhom1.MainActivity;
import vn.edu.doannhom1.PreferenceManager;
import vn.edu.doannhom1.R;
import vn.edu.doannhom1.Upload.AdminActivity;
import vn.edu.doannhom1.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    String type;
    FirebaseAuth auth;

    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        type = getIntent().getStringExtra("type");
        preferenceManager = new PreferenceManager(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (type.equals("customer")){
            binding.txtAs.setText("Đăng nhập với tư cách là khách hàng");
            if (user !=null){
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }


        auth = FirebaseAuth.getInstance();

        binding.btnLogin.setOnClickListener(view -> {
            String email = binding.inputEmail.getText().toString();
            String password = binding.inputPassword.getText().toString();
            if (email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Bắt buộc tất cả các trường", Toast.LENGTH_SHORT).show();
            }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Toast.makeText(this, "Nhập địa chỉ email hợp lệ", Toast.LENGTH_SHORT).show();
            }else {
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Đăng nhập...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        progressDialog.dismiss();
                        Intent intent;
                        if (type.equals("shop")){
                            intent = new Intent(LoginActivity.this, AdminActivity.class);
                        }else {
                            intent = new Intent(LoginActivity.this, MainActivity.class);
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
//                        preferenceManager.putBoolean("SisSigned",true);
                        preferenceManager.putBoolean("CisSigned",true);
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });

        binding.txtSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this,RegistrationActivity.class);
            intent.putExtra("type",type);
            startActivity(intent);
        });

        binding.txtForgot.setOnClickListener(view -> {
            Dialog dialog = new Dialog(LoginActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            dialog.setContentView(R.layout.item_dialogue_verify);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView btnSend = dialog.findViewById(R.id.btnSend);
            EditText input = dialog.findViewById(R.id.inputEmail);

            btnSend.setOnClickListener(view1 -> {
                String email = input.getText().toString();
                if (email.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Email bắt buộc..", Toast.LENGTH_SHORT).show();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(this, "Nhập địa chỉ email hợp lệ", Toast.LENGTH_SHORT).show();
                }else {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            dialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Email đã được gửi đi!", Toast.LENGTH_SHORT).show();
                        }else {
                            dialog.dismiss();

                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            dialog.show();

        });



    }
}