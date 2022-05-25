package com.example.gxwl.rederdemo.SAED;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gxwl.rederdemo.AppConfig.SharedPreference;
import com.example.gxwl.rederdemo.EntryActivity;
import com.example.gxwl.rederdemo.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.login1)
    Button login;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.user_name)
    EditText username;
    @BindView(R.id.password)
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        listeners();
    }

    void listeners() {
        login.setOnClickListener(loginListener);
    }

    private final View.OnClickListener loginListener = view -> {
        if (checkFields()) {
            String u = username.getText().toString();
            String p = password.getText().toString();
            if (u.equals("admin") && p.equals("admin"))
                startEntryActivity();

//                for (int i = 0; i < users.size(); i++) {
//                    if (u.equals(users.get(i).first) && p.equals(users.get(i).second)) {
//                        startEntryActivity();
//                        return;
//                   }
//                }
            else
                Toast.makeText(this, getResources().getString(R.string.wrong_user_or_Pass), Toast.LENGTH_SHORT).show();

        }
    };

    void startEntryActivity() {
        Intent intent = new Intent(this, EntryActivity.class);
        intent.putExtra("key", "admin");
        startActivity(intent);
        this.finish();
    }

    /**
     * checking if username or password not empty
     */
    boolean checkFields() {
        if (username.getText().length() == 0) {
            username.setError("حقل فارغ");
            return false;
        }
        if (password.getText().length() == 0) {
            password.setError("حقل فارغ");
            return false;
        }
        return true;
    }
}