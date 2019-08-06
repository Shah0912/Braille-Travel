package com.example.maps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.RECORD_AUDIO;

//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
;

public class MainActivity extends AppCompatActivity {
    private TextToSpeech mTTS;

    private final int request_microphone = 1;
    private final int request_finelocation = 2;
    public static String loc ;
    String dest = null;
    View locv, desv;
    double latloc;
    double longlac;
    double latdest;
    double longdest;

    public static final String EXTRA_LOC = "com.example.maps.EXTRA_LOC";
    public static final String EXTRA_DES = "com.example.brailletravel.EXTRA_DEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toast.makeText(this,loc,Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.UK);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                        Log.e("TTS", "Language not Supported");

                } else {
                    Log.e("TTS", "Initialization failed");
                }

                Button b = (Button)findViewById(R.id.toggle);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        openMapsactivity();
                    }
                });
            }
        });



        //Linking buttons
        final Button bLoc = findViewById(R.id.location);
        final Button bDest = findViewById(R.id.destination);

        if (!check())
            requestPermission();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO}, request_microphone);
        }
        //Sets Destination in locText
        final EditText locText = findViewById(R.id.locTxt);
        locText.setHint("Enter Location");
        //ADD TTS
        locv = findViewById(R.id.location);
        Speech(locText, locv);
        //Sets Destination in destText
        final EditText destText = findViewById(R.id.destTxt);
        destText.setHint("Enter Destination");
        //ADD TTS
        desv = findViewById(R.id.destination);
        Speech(destText, desv);
    }

    private void openMapsactivity() {
//        EditText editText = (EditText) locv;
  //      String e = editText.getText().toString();
    //    EditText editText1 = (EditText) desv;
      //  String f = editText1.getText().toString();
        Intent intent = new Intent(this,MapsActivity.class);
        intent.putExtra(EXTRA_LOC,loc);
        intent.putExtra(EXTRA_DES,dest);
        startActivity(intent);
        Toast.makeText(this,loc,Toast.LENGTH_SHORT).show();
    }


    private void Speak(EditText mEditText) {
        String text = mEditText.getText().toString();
        mTTS.setPitch(1);
        mTTS.setSpeechRate(1);
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }


    public void Speech(final EditText editText, final View v) {
        final SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            //Called when the endpointer is ready for the user to start speaking.
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            //Called when recognition results are ready.
            public void onResults(Bundle bundle) {
                //getting all the matches
                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                //displaying the first match
                if (matches != null)
                    editText.setText(matches.get(0));
                Speak(editText);
                if (v == desv)
                    dest = editText.getText().toString();
                else if (v == locv)
                    loc = editText.getText().toString();
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        //Button not pressed
                        mSpeechRecognizer.stopListening();
                        editText.setHint("You will see the input here");
                        break;

                    case MotionEvent.ACTION_DOWN:
                        //Button Pressed
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        editText.setText("");
                        editText.setHint("Listening...");
                        break;
                }
                return false;
            }
        });

    }


    //Checking for Permissions: Microphone
    private boolean check() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO}, request_microphone);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case request_microphone:
                if (grantResults.length > 0) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{RECORD_AUDIO},
                                request_microphone);
                    }

                }
            case request_finelocation:
                if (grantResults.length > 0) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{ACCESS_FINE_LOCATION},
                                request_finelocation);
                    }

                }

        }

    }


    /*public void findco(int a,String s) {
        Geocoder geocoder;
        String bestProvider;
        List<Address> user = null;
        double lat = 0;
        double lng=0;

        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        bestProvider = lm.getBestProvider(criteria, false);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, request_finelocation);
            return;
        }
        Location location = lm.getLastKnownLocation(bestProvider);

        if (location == null){
            Toast.makeText(this,"Location Not found",Toast.LENGTH_LONG).show();
        }else{
            geocoder = new Geocoder(this);
            try {
                if(loc == "current location")
                    user = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                else
                {
                    user = geocoder.getFromLocationName(s,1);
                }
                lat=(double)user.get(0).getLatitude();
                lng=(double)user.get(0).getLongitude();
                System.out.println(" DDD lat: " +lat+",  longitude: "+lng);

            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(a==1) {
            latloc = lat;
            longlac = lng;
        }
        if(a==2){
            latdest = lat;
            longdest=lng;
        }
    }
    */



    @Override
    protected void onDestroy() {
        if(mTTS!=null){
            mTTS.stop();
            mTTS.shutdown();

        }


        super.onDestroy();
    }


}
