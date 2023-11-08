package com.example.onbroading_login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    Context context;
    Resources resources;
    private AlertDialog alertDialog;
    private EditText editTextLogInEmail, editTextLogInPassWord;
    private Button buttonLogInLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView txt_Login=findViewById(R.id.txt_Login);
        List<SpinnerItem> items = new ArrayList<>();
        items.add(new SpinnerItem(R.drawable.englishlanguage, "English"));
        items.add(new SpinnerItem(R.drawable.vietnamlanguage, "VietNam"));
        items.add(new SpinnerItem(R.drawable.francelanguage, "France"));
        items.add(new SpinnerItem(R.drawable.thailandlanguage, "ThaiLand"));
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, items);
        Spinner spinner = findViewById(R.id.ChooseLanguage);
        spinner.setAdapter(adapter);
        SharedPreferences preferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        String savedLanguage = preferences.getString("language", "");

        if (!savedLanguage.isEmpty()) {
            context = LocaleHelper.setLocale(LoginActivity.this, savedLanguage);
            resources = context.getResources();
            recursivelySetLanguage(findViewById(android.R.id.content), resources);
            if(savedLanguage.equals("en")){
                spinner.setSelection(0);
            } else if (savedLanguage.equals("vi")) {
                spinner.setSelection(1);
            } else  if (savedLanguage.equals("fr"))  {
                spinner.setSelection(2);
            }
            else {
                spinner.setSelection(3);
            }
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SpinnerItem Item = adapter.getItem(position);
                String selectedItem = Item.getText();
                String language="en";
                if (selectedItem.equals("VietNam")) {
                    language="vi";
                } else if (selectedItem.equals("English")) {
                    language="en";
                } else if(selectedItem.equals("France")){
                    language="fr";
                }
                else{
                    language="th";
                }
                context = LocaleHelper.setLocale(LoginActivity.this, language);
                resources = context.getResources();
                recursivelySetLanguage(findViewById(android.R.id.content), resources);
                Log.d("LanguageSelection", "Selected Language: " + language);

                SharedPreferences preferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("language", language);
                editor.apply();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                context = LocaleHelper.setLocale(LoginActivity.this, "en");
                resources = context.getResources();
                recursivelySetLanguage(findViewById(android.R.id.content), resources);
            }
        });

        ImageView backHomeFromLogin = findViewById(R.id.img_LoginBack);
        backHomeFromLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        TextView SignUptoLogin = findViewById(R.id.txt_toSignUp);
        SignUptoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        TextView LogintoForgotPassword = findViewById(R.id.txt_toForgotPassword);
        LogintoForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(intent);
            }
        });
        editTextLogInEmail = findViewById(R.id.edt_loginEmail);
        editTextLogInPassWord = findViewById(R.id.edt_loginPassword);
        buttonLogInLogIn = findViewById(R.id.btn_loginLogIn);


        ImageView passwordVisibilityToggle = findViewById(R.id.img_passwordVisibility);
        EditText passwordEditText = findViewById(R.id.edt_loginPassword);

        final boolean[] passwordVisible = {false};

        passwordVisibilityToggle.setOnClickListener(v -> {
            passwordVisible[0] = !passwordVisible[0];

            if (passwordVisible[0]) {
                passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordVisibilityToggle.setImageResource(R.drawable.visible); // Thay đổi biểu tượng hiện mật khẩu
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordVisibilityToggle.setImageResource(R.drawable.invisible); // Thay đổi biểu tượng ẩn mật khẩu
            }

            // Đảm bảo rằng văn bản trong EditText không bị mất
            passwordEditText.setSelection(passwordEditText.length());
        });

        buttonLogInLogIn.setOnClickListener(view -> {
            if (!validateEmail() | !validatePassWord() ) {

            }
            else {
                login();
            }
        });
    }

    private Boolean validateEmail() {
        String val = editTextLogInEmail.getText().toString();
        if (val.isEmpty()) {
            editTextLogInEmail.setError("Email cannot be empty");
            return false;
        } else {
            editTextLogInEmail.setError(null);
            return true;
        }
    }

    private Boolean validatePassWord() {
        String val = editTextLogInPassWord.getText().toString();
        if (val.isEmpty()) {
            editTextLogInPassWord.setError("Password cannot be empty");
            return false;
        } else {
            editTextLogInPassWord.setError(null);
            return true;
        }
    }

    private void login() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://uiot.ixxc.dev/auth/realms/master/protocol/openid-connect/") // Replace with the base URL of your API
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API_Interface authService = retrofit.create(API_Interface.class);
        String username = editTextLogInEmail.getText().toString();
        String password = editTextLogInPassWord.getText().toString();
        Call<HelperClass> call = authService.login("openremote", username, password, "password");
        call.enqueue(new Callback<HelperClass>() {
            @Override
            public void onResponse(Call<HelperClass> call, Response<HelperClass> response) {
                if (response.isSuccessful()) {
                    HelperClass loginResponse = response.body();
                    String accessToken = loginResponse.getAccess_token();
                    Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                } else {
                    // Authentication failed
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<HelperClass> call, Throwable t) {
                // Handle network or API errors
                Toast.makeText(LoginActivity.this, "Login Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void recursivelySetLanguage(View view, Resources resources) {
        if (view instanceof TextView) {
            if (view.getId() != View.NO_ID) {
                // Lấy ID của TextView
                int viewId = view.getId();
                CharSequence originalText = ((TextView) view).getText();
                if (originalText instanceof Spanned) {
                    originalText = ((Spanned) originalText).toString();
                }
                String resourceName = resources.getResourceEntryName(viewId);
                Log.d("resourceId", "resourceId: " + originalText);
                int resourceId = resources.getIdentifier(resourceName, "string", getPackageName());

                if (resourceId != 0) {
                    ((TextView) view).setText(resources.getString(resourceId));
                }

            }
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                recursivelySetLanguage(((ViewGroup) view).getChildAt(i), resources);
            }
        }
    }

}
