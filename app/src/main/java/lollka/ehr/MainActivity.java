package lollka.ehr;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;

import co.mobiwise.library.RadioListener;
import co.mobiwise.library.RadioManager;

import static android.media.AudioManager.STREAM_MUSIC;


public class MainActivity extends AppCompatActivity implements RadioListener {
    private ImageButton btn; //POGA
    private SeekBar volumebar; //SKAĻUMA LĪNIJA
    private ImageButton audiomax;
    private ImageButton audiomin;
    private ImageButton social1;
    private ImageButton social2;
    private ImageButton social3;
    TextView artist;//DZIESMAS NOSAUKUMS
    TextView song;
    String url = "http://ehr.lollka.me/";//DZIESMU NOSAUKUMU LINKS
    String audiostream = "http://stream.europeanhitradio.lv:8000/ehr.mp3";
    String artistname;
    String songname;
    private AudioManager audioManager;
    RadioManager mRadioManager = RadioManager.with(this);
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        public void run() {
            new songDisplay().execute();//uzsāk songDisplay
        }
    };
    Runnable r = new Runnable() {
        public void run() {
            handler.postDelayed(this, 0);
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            volumebar.setProgress(currentVolume);
            //Log.d("LOLLKA", "Volume now " + currentVolume);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRadioManager.registerListener(this);
        mRadioManager.enableNotification(false);
        artist = (TextView)findViewById(R.id.artistName);
        song = (TextView)findViewById(R.id.songName);
        btn = (ImageButton) findViewById(R.id.playButton);
        social1 = (ImageButton) findViewById(R.id.social1);
        social2 = (ImageButton) findViewById(R.id.social2);
        social3 = (ImageButton) findViewById(R.id.social3);
        volumebar = (SeekBar) findViewById(R.id.soundBar);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        btn.setImageResource(R.drawable.playpoga);
        runnable.run();     //Uzsāk runnable,  un execute soungDisplay
        setVolumeControlStream(STREAM_MUSIC);
        appvolumeControls();
        maxminvolume();
        ehrLivestream();
        sociallinks();
    }
    //SKAĻUMA KONTROLE
    private void appvolumeControls(){
        try {
            volumebar.setMax(audioManager.getStreamMaxVolume(STREAM_MUSIC));
            volumebar.setProgress(audioManager.getStreamVolume(STREAM_MUSIC));
            volumebar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(STREAM_MUSIC,progress,0);
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }catch (Exception e){}
    }
    private void maxminvolume(){
        try{
            audiomax = (ImageButton) findViewById(R.id.audiomax);
            audiomin = (ImageButton) findViewById(R.id.audiomin);
            volumebar = (SeekBar) findViewById(R.id.soundBar);
            final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            audiomin.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                audioManager.setStreamVolume(STREAM_MUSIC,0,0);
                volumebar.setProgress(0);
                }
            });
            audiomax.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    audioManager.setStreamVolume(STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                    volumebar.setProgress(100);
                }
            });

        }catch (Exception e){}
    }
    //DZIESMAS NOSAUKUMS
    private class songDisplay extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                String jsonout = Jsoup.connect(url).ignoreContentType(true).execute().body();
                JSONObject current = new JSONObject(jsonout);
                JSONObject ehr = current.getJSONObject("ehr");
                songname = ehr.getString("song");
                artistname = ehr.getString("artist");
                //Log.d("LOLLKA.JSON",songname+"|"+artistname);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            artist.setText(artistname);
            song.setText(songname);
            handler.postDelayed(runnable, 1000);
            handler.postDelayed(r, 1000);//soundbārs
        }
    }
    //RADIO
    public void ehrLivestream() {
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mRadioManager.isPlaying()) {
                    mRadioManager.stopRadio();
                    btn.setImageResource(R.drawable.playpoga);

                }else{
                    mRadioManager.startRadio(audiostream);
                    btn.setImageResource(R.drawable.pausepoga);
                }
            }
        });
    }
    @Override
    public void onRadioConnected() {
    }
    @Override
    public void onRadioStarted() {
    }
    @Override
    public void onRadioStopped() {
    }
    @Override
    public void onMetaDataReceived(String s, String s1) {
    }
    @Override
    protected void onStart() {
        super.onStart();
        mRadioManager.connect();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mRadioManager.connect();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRadioManager.disconnect();
    }
//SOCIAL LINKI
//Liekot linkus jāliek vai nu HTTP vai arī HTTPS lai neizraisītu errorus
    public void sociallinks(){
        social1.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Uri uri = Uri.parse("https://twitter.com/ehr_lv");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        social2.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Uri uri = Uri.parse("https://www.facebook.com/europeanhitradio");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        social3.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Uri uri = Uri.parse("https://www.instagram.com/europeanhitradio/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }
}
