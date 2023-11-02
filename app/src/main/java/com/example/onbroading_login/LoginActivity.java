package com.example.onbroading_login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.List;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    Context context;
    Resources resources;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView txt_Login=findViewById(R.id.txt_Login);

//        Spinner spinner = findViewById(R.id.ChooseLanguage);
//        String[] items = getResources().getStringArray(R.array.spinner_item);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
        List<SpinnerItem> items = new ArrayList<>();
        items.add(new SpinnerItem(R.drawable.unitedkingdomlanguage, "English"));
        items.add(new SpinnerItem(R.drawable.vnlanguage, "VietNam"));
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
