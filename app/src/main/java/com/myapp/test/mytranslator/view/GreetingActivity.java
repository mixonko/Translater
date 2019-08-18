package com.myapp.test.mytranslator.view;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.test.mytranslator.R;
import com.myapp.test.mytranslator.contracts.GreetingContract;
import com.myapp.test.mytranslator.myAppContext.MyApplication;
import com.myapp.test.mytranslator.presenter.GreetingPresenter;

import java.util.Locale;

public class GreetingActivity extends Activity implements GreetingContract.View {
    GreetingContract.Presenter presenter;
    private TextView translatedText;
    private TextView greetingText;
    private Button close;
    private Button playText;
    private TextToSpeech textToSpeech;
    private Locale locale;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting);

        presenter = new GreetingPresenter(this);

        translatedText = findViewById(R.id.translatedText);
        greetingText = findViewById(R.id.greetingText);
        close = findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               presenter.onCloseButtonWasClicked();
            }
        });
        playText = findViewById(R.id.playText);
        playText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onPlayTextButtonWasClicked();
            }
        });
        presenter.onActivityWasCreated();
    }

    @Override
    public void setText(String result) {
        translatedText.setText(result);
    }

    @Override
    public String getGreetingText() {
        return greetingText.getText().toString();
    }

    @Override
    public void showError(Throwable t) {
        Toast.makeText(MyApplication.getAppContext(), "Ошибка сервера " + t.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showNoConnection() {
        Toast.makeText(MyApplication.getAppContext(), "Отсутствует интернет соединение", Toast.LENGTH_LONG).show();
    }

    @Override
    public String getFirstLang() {
        return getIntent().getStringExtra(CommunicationActivity.FIRST_LANG_KEY);
    }

    @Override
    public String getSecondLang() {
        return getIntent().getStringExtra(CommunicationActivity.SECOND_LANG_KEY);
    }

    @Override
    public void closeActivity() {
        this.finish();
    }

    @Override
    public void playText(final String lang) {
        textToSpeech = new TextToSpeech(MyApplication.getAppContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    locale = new Locale(lang);
                    int result = textToSpeech.setLanguage(locale);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(MyApplication.getAppContext(), "Извините, этот язык не поддерживается", Toast.LENGTH_LONG).show();
                    } else {
                        textToSpeech.speak(translatedText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                    }
                } else {
                    Toast.makeText(MyApplication.getAppContext(), "Ошибка воспроизведения", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onStop() {
        presenter.onStopActivity();
        super.onStop();
    }

    @Override
    public void stopTextToSpeech(){
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}
