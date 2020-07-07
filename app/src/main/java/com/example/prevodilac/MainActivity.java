package com.example.prevodilac;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

public class MainActivity extends AppCompatActivity {

    private TextView mSourceLang, mTranslatedText;
    private Button mTranslatedBtn;
    private EditText mSourceText;
    private String sourceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSourceLang = findViewById(R.id.sourceLanguage);
        mSourceText = findViewById(R.id.sourceText);
        mTranslatedText = findViewById(R.id.translatedText);
        mTranslatedBtn = findViewById(R.id.btnTranslate);

        mTranslatedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identifyLanguage();
            }
        });


    }

    private void identifyLanguage() {
        sourceText = mSourceText.getText().toString();
        FirebaseLanguageIdentification identification = FirebaseNaturalLanguage.getInstance()
                .getLanguageIdentification();

        mSourceLang.setText("Trazi jezik...");

        identification.identifyLanguage(sourceText).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                if(s.equals("und"))
                {
                    Toast.makeText(MainActivity.this, "Nije prepoznat jezik", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    getLanguageCode(s);
                }
            }
        });
    }

    private void getLanguageCode(String language) {
        int langCode;
        switch (language){
            case "es":
                langCode = FirebaseTranslateLanguage.ES;
                mSourceLang.setText("Spanski jezik");
                break;
            case "fr":
                langCode = FirebaseTranslateLanguage.FR;
                mSourceLang.setText("Francuski jezik");
                break;
            case "de":
                langCode = FirebaseTranslateLanguage.DE;
                mSourceLang.setText("Nemacki jezik");
                break;
            default:
                langCode = 0;
        }
        translateText(langCode);
    }

    private void translateText(int langCode) {

        mTranslatedText.setText("Prevodi...");

        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                //from langauge
                .setSourceLanguage(langCode)
                //to langauge
                .setTargetLanguage(FirebaseTranslateLanguage.EN)
                .build();

        final FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance()
                .getTranslator(options);

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .build();

        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                translator.translate(sourceText).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        mTranslatedText.setText(s);
                    }
                });
            }
        });
    }
}
