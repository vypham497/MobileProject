package com.example.onbroading_login;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;
import java.net.URISyntaxException;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {
    Context context;
    Resources resources;
    private EditText editTextSignUpUserName, editTextSignUpEmail, editTextSignUpPassWord, editTextSignUpConfirmPassword,editTextLogInEmail,editTextLogInPassWord;
    private Button buttonSignUpCreateAccount;
    private ProgressBar progressbar;
    private boolean shouldStopEvaluation=false;
    private WebView webView;
    private Boolean validateUserName() {
        String val = editTextSignUpUserName.getText().toString();
        if (val.isEmpty()) {
            editTextSignUpUserName.setError("Field cannot be empty");
            return false;
        }
        else if (val.length() >= 20) {
            editTextSignUpUserName.setError("Username too long");
            return false;
        }
        else {
            editTextSignUpUserName.setError(null);
            return true;
        }
    }
    private Boolean validateEmail() {
        String val = editTextSignUpEmail.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (val.isEmpty()) {
            editTextSignUpEmail.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            editTextSignUpEmail.setError("Invalid email address");
            return false;
        } else {
            editTextSignUpEmail.setError(null);
            return true;
        }
    }
    private Boolean validatePassword() {
        String val = editTextSignUpPassWord.getText().toString();
//        String passwordVal = "^" +
//                //"(?=.*[0-9])" +         //at least 1 digit
//                //"(?=.*[a-z])" +         //at least 1 lower case letter
//                //"(?=.*[A-Z])" +         //at least 1 upper case letter
//                "(?=.*[a-zA-Z])" +      //any letter
//                "(?=.*[@#$%^&+=])" +    //at least 1 special character
//                "(?=\\S+$)" +           //no white spaces
//                ".{8,}" +               //at least 4 characters
//                "$";
        if (val.isEmpty()) {
            editTextSignUpPassWord.setError("Field cannot be empty");
            return false;
        }
        else if (val.length() < 8) {
            editTextSignUpPassWord.setError("At least 8 characters");
            return false;
//        } else if (!val.matches(passwordVal)) {
//            editTextSignUpPassWord.setError("Password is too weak");
//            return false;
        } else {
            editTextSignUpPassWord.setError(null);
            return true;
        }
    }
    private Boolean validateConfirmPassword() {
        String val = editTextSignUpConfirmPassword.getText().toString();
        String password = editTextSignUpPassWord.getText().toString();

        if (val.isEmpty()) {
            editTextSignUpConfirmPassword.setError("Field cannot be empty");
            return false;
        } else if (!val.equals(password)) {
            editTextSignUpConfirmPassword.setError("Passwords do not match");
            return false;
        } else {
            editTextSignUpConfirmPassword.setError(null);
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
        Call<HelperClass> call = authService.login("openremote",username,password,"password");
        call.enqueue(new Callback<HelperClass>() {
            @Override
            public void onResponse(Call<HelperClass> call, Response<HelperClass> response) {
                if (response.isSuccessful()) {
                    HelperClass loginResponse = response.body();
                    String accessToken = loginResponse.getAccess_token();
                    Log.d("token",accessToken);
                    Toast.makeText(SignUpActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                }
                else {
                    // Authentication failed
                }
            }
            @Override
            public void onFailure(Call<HelperClass> call, Throwable t) {
                // Handle network or API errors
                Log.d("API CALL", t.getMessage().toString());
            }
        });
    }
    @SuppressLint("SetJavaScriptEnabled")
    private void loadWebView(String user,String email, String pass,String repass) {
        String myURL = "https://uiot.ixxc.dev/auth/realms/master/protocol/openid-connect/auth?client_id=openremote&redirect_uri=https%3A%2F%2Fuiot.ixxc.dev%2Fmanager%2F&state=7b7ef2b3-64c3-4693-ba35-33e412d3c277&response_mode=fragment&response_type=code&scope=openid&nonce=c6011dc3-ac6e-46c3-9378-33fe07ab9bec";
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(null);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(myURL);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.d("Url1",request.getUrl().toString());
                if (request.getUrl().toString().contains("manager/#state")) {
                    Log.d("Url","Go");
                    login();
                    // navigateToLoginMain();
                    return true;
                }
                return false;
            }
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                //Log.d("Url",url);
                return null;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.setVisibility(View.GONE);
                if (url.contains("openid-connect/auth")) {
                    String redirect = "document.getElementsByClassName('btn waves-effect waves-light')[1].click();";
                    view.evaluateJavascript(redirect, null);
                } else if (url.contains("login-actions/registration")){
                    shouldStopEvaluation = false;
                    String helperText = "document.getElementsByClassName('helper-text')[0].getAttribute('data-error');";
                    String redText = "document.getElementsByClassName('red-text')[1].innerText;";
                    view.evaluateJavascript(helperText, s -> {
                        if (s.equals("null")) {
                            view.evaluateJavascript(redText, s1 -> {
                                if (s1.equals("null")){
                                    String userField= "document.getElementById('username').value='" + user + "';";
                                    String emailField= "document.getElementById('email').value='" + email + "';";
                                    String passField= "document.getElementById('password').value='" + pass + "';";
                                    String repassField= "document.getElementById('password-confirm').value='" + repass + "';";

                                    view.evaluateJavascript(userField, null);
                                    view.evaluateJavascript(emailField, null);
                                    view.evaluateJavascript(passField, null);
                                    view.evaluateJavascript(repassField, null);
                                    view.evaluateJavascript("document.getElementsByTagName('form')[0].submit();", null);
                                    Toast.makeText(SignUpActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                } else {
                                    signUpErr(s1);
                                }
                            });
                        } else {
                            signUpErr(s);
                        }
                    });
                }
            }
        });
        webView.setWebChromeClient(new WebChromeClient());
    }
    private void signUpErr(String msg) {
        buttonSignUpCreateAccount.setEnabled(true);
        Toast.makeText(SignUpActivity.this, "Singup Failed" + msg, Toast.LENGTH_SHORT).show();
    }
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        editTextSignUpUserName = findViewById(R.id.edt_signupUserName);
        editTextSignUpEmail = findViewById(R.id.edt_signupEmail);
        editTextSignUpPassWord = findViewById(R.id.edt_signupPassword);
        editTextSignUpConfirmPassword = findViewById(R.id.edt_sigupConfirmPassword);
        editTextLogInEmail = findViewById(R.id.edt_loginEmail);
        editTextLogInPassWord = findViewById(R.id.edt_loginPassword);
        buttonSignUpCreateAccount = findViewById(R.id.btn_signupCreateAccount);


        webView = findViewById(R.id.webView);
        //loadingProgressBar.setVisibility(View.VISIBLE);

        buttonSignUpCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUserName() | !validateEmail() | !validatePassword() | !validateConfirmPassword()) {
                    return;
                }
                buttonSignUpCreateAccount .setEnabled(true);
                final String user = String.valueOf(editTextSignUpUserName.getText());
                final String email = String.valueOf(editTextSignUpEmail.getText());
                final String pass = String.valueOf(editTextSignUpPassWord.getText());
                final String repass = String.valueOf(editTextSignUpConfirmPassword.getText());
                loadWebView(user,email,pass,repass);
            }

        });
//        Spinner spinner = findViewById(R.id.ChooseLanguage);
//        String[] items = getResources().getStringArray(R.array.spinner_item);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
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
            context = LocaleHelper.setLocale(SignUpActivity.this, savedLanguage);
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
                context = LocaleHelper.setLocale(SignUpActivity.this, language);
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
                context = LocaleHelper.setLocale(SignUpActivity.this, "en");
                resources = context.getResources();
                recursivelySetLanguage(findViewById(android.R.id.content), resources);
            }
        });


        ImageView backHomeFromSignUp = findViewById(R.id.img_SignUpBack);
        backHomeFromSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        TextView SignUptoLogin = findViewById(R.id.txt_toLogin);
        SignUptoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
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
