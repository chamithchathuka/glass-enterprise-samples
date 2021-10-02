/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.glass.voicerecognitionsample;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.glass.ui.GlassGestureDetector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements
    GlassGestureDetector.OnGestureListener {

  private static final int REQUEST_CODE = 999;
  private static final String TAG = MainActivity.class.getSimpleName();
  private static final String DELIMITER = "\n";
  public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
  private static final int SPEECH_REQUEST = 109;
  private static final int FEATURE_VOICE_COMMANDS = 14;

  final String[] keywords = {"normal", "finding"};

  private TextView resultTextView;
  private GlassGestureDetector glassGestureDetector;
  private List<String> mVoiceResults = new ArrayList<>(4);
  private RadioButton firstOption;
  private RadioButton secondOption;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    firstOption = (RadioButton) findViewById(R.id.radioButton);
    secondOption = (RadioButton) findViewById(R.id.radioButton2);
    resultTextView = findViewById(R.id.results);
    glassGestureDetector = new GlassGestureDetector(this, this);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (resultCode == RESULT_OK) {
      final List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
      Log.d(TAG, "results: " + results.toString());
      if (results != null && results.size() > 0 && !results.get(0).isEmpty()) {
//        updateUI(results.get(0));

        boolean anyMatch = Arrays.stream(keywords).anyMatch(results.get(0)::contains);

        if(anyMatch){

          if(keywords[0].toLowerCase(Locale.ROOT).contains(results.get(0))){
            firstOption.setChecked(true);
            secondOption.setChecked(false);

          }else{
            firstOption.setChecked(false);
            secondOption.setChecked(true);

          }
          Toast.makeText(getApplicationContext(),"Got it.",Toast.LENGTH_SHORT).show();
        }else{
          Toast.makeText(getApplicationContext(),results.get(0),Toast.LENGTH_SHORT).show();
        }

      }
    } else {
      Log.d(TAG, "Result not OK");
    }
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    return glassGestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
  }

  @Override
  public boolean onGesture(GlassGestureDetector.Gesture gesture) {
    switch (gesture) {
      case TAP:
        requestVoiceRecognition();
        return true;
      case SWIPE_DOWN:
        finish();
        return true;
      default:
        return false;
    }
  }

  private void requestVoiceRecognition() {
    final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    intent.putExtra("recognition-phrases", keywords);
//    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    startActivityForResult(intent, SPEECH_REQUEST);
  }

  private void updateUI(String result) {
    if (mVoiceResults.size() >= 4) {
      mVoiceResults.remove(mVoiceResults.size() - 1);
    }
    mVoiceResults.add(0, result);
    final String recognizedText = String.join(DELIMITER, mVoiceResults);
    resultTextView.setText(recognizedText);
  }
}
