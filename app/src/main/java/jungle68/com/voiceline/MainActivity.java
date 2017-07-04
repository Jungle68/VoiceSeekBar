package jungle68.com.voiceline;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import jungle68.com.library.core.VoiceSeekBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VoiceSeekBar voiceSeekBar= (VoiceSeekBar) findViewById(R.id.vsb);
        voiceSeekBar.setSectionCount(40);
    }
}
