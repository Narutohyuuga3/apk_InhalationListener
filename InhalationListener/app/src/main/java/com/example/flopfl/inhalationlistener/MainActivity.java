package com.example.flopfl.inhalationlistener;

import android.arch.lifecycle.LiveData;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flopfl.inhalationlistener.Data.AppDatabase;
import com.example.flopfl.inhalationlistener.Data.MedikamentEntry;
import com.example.flopfl.inhalationlistener.Utility.Notifier;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button mInhalation_Button;
    private Button mMedikamente_Button;
    private TextView mBluetooth_Textview;
    private TextView mPat_ID;

    public static String DEVICELIST_IDENTIFIER = "Device_List";
    public static Set<String> DEVICELIST_DEFAULT_IDENTIFIER = null;
    public static String Job_Tag_Key;

    private BluetoothAdapter mBluetoothAdapter;
    private int Blueootooth_Request_Code = 125;
    private BluetoothManager BT_Mng;
    private AppDatabase mdb;
    private String patID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBluetooth_Textview = (TextView) findViewById(R.id.TextView_Search_BT_ID);
        mInhalation_Button = (Button) findViewById(R.id.Button_Inhalation_ID);
        mMedikamente_Button = (Button) findViewById(R.id.Button_Medikamente_ID);
        mPat_ID = (TextView) findViewById(R.id.pat_id);
        mdb = AppDatabase.getInstance(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            BT_Mng = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Intent audio = new Intent(Intent.ACTION_HEADSET_PLUG);

        //prüft ob Bluetooth an
        if (mBluetoothAdapter == null) {//schauen ob BLuetooth vorhanden
            Toast.makeText(this, "your device supports no Bluetooth, This app wont work without Bluetooth", Toast.LENGTH_LONG);
        }
        if (!mBluetoothAdapter.isEnabled()) {//anfrage bluetooth an
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Blueootooth_Request_Code);
        }
        if (getIntent() != null) {//eintritt in Medikament activity von Notification
            Intent intent = getIntent();
            if (intent.hasExtra(getString(R.string.Medikamente_Extra_Key))) {
                Intent intent2 = new Intent(this, Medikamente.class);
                startActivity(intent2);
            }
        }

    }

    //öffnen Inhalation activity wenn gewählt
    public void Inhalation_On_Click(View view) {
        patID = mPat_ID.getText().toString();
        Intent intent = new Intent(this, Inhalation.class);
        Bundle b = new Bundle();
        b.putString("ID", patID);
        intent.putExtras(b);
        startActivity(intent);

    }

    //öffnen Meidkament activity wenn gewählt
    public void Medikamente_On_Click(View view) {
        Intent intent = new Intent(this, Medikamente.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}



