package com.example.flopfl.inhalationlistener;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MicrophoneInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.flopfl.inhalationlistener.Data_Inhalation.AppDatabase2;
import com.example.flopfl.inhalationlistener.Data_Inhalation.InhalationEntry;
import com.example.flopfl.inhalationlistener.Recording.MainViewModel_Inhalation;
import com.example.flopfl.inhalationlistener.Recording.RecordService;
import com.example.flopfl.inhalationlistener.Recording.TaskAdapter_Inhalation;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

//Inhalationen aufzeichnen und protokollieren
public class Inhalation extends AppCompatActivity implements TaskAdapter_Inhalation.ItemClickListener {
    private RecyclerView mRecyclerview;
    private TaskAdapter_Inhalation mAdapter;
    private AppDatabase2 mdb;
    BroadcastReceiver broadcastReceiver;

    private MusicIntentReceiver hdsreceiver;

    AudioManager audioManager;
    AudioRecord audioRecord = null;
    Button mrecord;
    Button mpause;
    boolean isRecording = false;
    private Thread recordingThread = null;
    private int bufferSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
    private UUID uuid;


    //erstellung der Recyclerview für die Darstellung und ihrem Adapter
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inhalation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        hdsreceiver = new MusicIntentReceiver();
        IntentFilter filter1 = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(hdsreceiver, filter1);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
                if (AudioManager.SCO_AUDIO_STATE_CONNECTED == state) {
                    Log.d("SCOO", "connected");
                    if (!isRecording) {

                        unregisterReceiver(broadcastReceiver);
                        startRecording();
                    }

                }
                if (AudioManager.SCO_AUDIO_STATE_DISCONNECTED == state) {
                    Log.d("SCOO", "BLT disconnected");
                    int hdsstatus = hdsreceiver.getStatus();
                    if (hdsstatus == 0) {
                        Toast.makeText(getApplicationContext(), "microphone not plugged in", Toast.LENGTH_LONG).show();
                        Log.d("SCOO/HDS", "Not plugged");
                    }
                    if (hdsstatus == 1) {
                        Toast.makeText(getApplicationContext(), "microphone plugged in", Toast.LENGTH_LONG).show();
                        Log.d("SCOO/HDS", "Plugged");
                        startRecording();
                    }
                    if (hdsstatus == -1) {
                        Toast.makeText(getApplicationContext(), "This didn't worked", Toast.LENGTH_LONG).show();
                        Log.d("SCOO/HDS", "Error 404");
                    }
                }
                mrecord.setEnabled(false);
            }
        };


        mRecyclerview = (RecyclerView) findViewById(R.id.recyclerViewTask_Inhalation);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new TaskAdapter_Inhalation(this, this);
        mRecyclerview.setAdapter(mAdapter);
        DividerItemDecoration deco = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        mRecyclerview.addItemDecoration(deco);
        //reagiert auf berührung des screens
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Bei swipen wird Entry gelöscht
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mdb.taskDao2().deleteTask(mAdapter.getEntries().get(viewHolder.getAdapterPosition()));
                    }
                });
            }
        }).attachToRecyclerView(mRecyclerview);

        mdb = AppDatabase2.getInstance(getApplicationContext());
        retrieve();
        mrecord = findViewById(R.id.Button_Record_ID);

        mpause = findViewById(R.id.Button_Pause_ID);
        mrecord.setEnabled(true);
        audioManager = (AudioManager) this.getSystemService(this.AUDIO_SERVICE);

    }

    //ausgelöst wenn Einträg betätigt wird
    public void onItemClickListener(int itemID) {
    }

    //beobachten der Einträge im Viewmodel
    private void retrieve() {

        MainViewModel_Inhalation model = ViewModelProviders.of(this).get(MainViewModel_Inhalation.class);
        model.getEntries().observe(this, new Observer<List<InhalationEntry>>() {
            @Override
            public void onChanged(@Nullable List<InhalationEntry> medikamentEntries) {
                mAdapter.setTasks(medikamentEntries);
            }
        });

    }


    //Übertragt die aufgezeichneten Daten in ein File
    private void writeAudioDataToFile() {
        Bundle b = getIntent().getExtras();
        Log.d("Link_Request", "output" + uuid.toString() + b.getString("ID"));
        String filename = uuid.toString() + b.getString("ID");
        byte saudioBuffer[] = new byte[bufferSize];
        FileOutputStream os = null;

        try {
            os = openFileOutput(filename, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("headset_rec", "false filepath");
        }

        while (isRecording) {

            audioRecord.read(saudioBuffer, 0, bufferSize);
            try {
                os.write(saudioBuffer, 0, bufferSize);
                Log.d("headset_rec", "writing: " + saudioBuffer[0]);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("headset_rec", "writefail");
            }
        }
        try {
            os.close();
        } catch (IOException e) {
            Log.d("headset_rec", "close");
            e.printStackTrace();
        }

    }

    public void Record_On_Click(View view) {
        requestRecordAudioPermission();
        sendTone();

    }

    //spielt Startsignal und startet Aufzeichnung wenn Bluetooth verbunden
    public void sendTone() {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.beep);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                Log.d("headset_rec", "media start");
                mp.release();
                IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
                registerReceiver(broadcastReceiver, intentFilter);
                audioManager.setMode(audioManager.MODE_NORMAL);
                audioManager.setBluetoothScoOn(true);
                audioManager.startBluetoothSco();
                audioManager.setSpeakerphoneOn(false);
            }
        });
        Log.d("headset_rec", "media start");
        mp.start();
    }

    //erstellt Aufzeichnungsobjekt und startet Datenspeicherung
    //erstellt UUID für Filename
    private void startRecording() {
        mpause.setEnabled(true);
        mrecord.setEnabled(false);
        uuid = UUID.randomUUID();
        Log.d("SCOO", "voice");
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        audioRecord.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();


    }

    //Button zum pausieren
    public void Record_Pause_Click(View view) {
        stopRecording();
        mpause.setEnabled(false);
    }

    //beendet Aufzeichnung und setzt die dafür benötigten konfigurationen zurück
    private void stopRecording() {
        audioManager.stopBluetoothSco();
        audioManager.setMode(audioManager.MODE_NORMAL);
        audioManager.setBluetoothScoOn(false);

        if (null != audioRecord) {
            isRecording = false;
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
            recordingThread = null;
            endTone();
            Bundle b = getIntent().getExtras();
            Intent intent = new Intent(this, RecordService.class);
            intent.putExtra("UUID", uuid.toString() + b.getString("ID"));
            startService(intent);
        }
        mrecord.setEnabled(true);
    }

    //Endton nach der Inhalation
    public void endTone() {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.beep);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                Log.d("headset_rec", "media end");
                mp.release();
            }
        });
        mp.start();
    }


    //Wenn Bluetoothsco verbunden ist kann eine Aufzeichnung gestartet werden
    /*private BroadcastReceiver mBluetoothScoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
            if (AudioManager.SCO_AUDIO_STATE_CONNECTED == state) {
                Log.d("SCOO","connected");
                if(!isRecording){

                    unregisterReceiver(mBluetoothScoReceiver);
                    startRecording();
                }

            }
            if(AudioManager.SCO_AUDIO_STATE_DISCONNECTED==state){
                Log.d("SCOO","BLT disconnected");
                mrecord.setEnabled(false);
            }
        }
    };*/


    //Get BLO-Battery status:


    @Override
    protected void onResume() {
        if (isRecording) {
            mpause.setEnabled(true);
            mrecord.setEnabled(false);
        }
        super.onResume();
    }

    //Wird die Activity zerstört werden die konfigurationen zurückgesetzt
    @Override
    protected void onDestroy() {

        audioManager.stopBluetoothSco();
        audioManager.setMode(audioManager.MODE_NORMAL);
        audioManager.setBluetoothScoOn(false);
        // Start Speaker.
        audioManager.setSpeakerphoneOn(true);
        super.onDestroy();
    }

    //holt sich Berechtigungen für Inhalation
    private void requestRecordAudioPermission() {
        //prüft ob Permission benötigt in abhängigkeit der Version
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                Log.d("Activity_Request", "Wastn granted!");
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                    Log.d("Activity_Request", "request!");
                } else {
                    Log.d("Activity_Request", "take!");
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                }
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Log.d("Activity_Request", "Wastn granted!");
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Log.d("Activity_Request", "request!");
                } else {
                    Log.d("Activity_Request", "take!");
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
        }
    }

}
