package com.example.flopfl.inhalationlistener.Recording;


import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Context;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;

import com.example.flopfl.inhalationlistener.AppExecutors;
import com.example.flopfl.inhalationlistener.Data_Inhalation.AppDatabase2;
import com.example.flopfl.inhalationlistener.Data_Inhalation.InhalationEntry;
import com.google.android.gms.common.util.IOUtils;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

//new imports
//import org.apache.commons.io.*;

//originally extends IntentService
public class RecordService extends IntentService implements Runnable {

    // Intentservice der die Aufzeichnung in die Cloud läd und die Bewertung wieder herunterläd
    Date currentTime;
    Boolean loading;

    String key;
    URL connectURLPCM, connectURLTXT;
    String responseString;
    String Title;
    String DescriptionPCM, DescriptionTXT;
    byte[] dataToServer;
    FileInputStream fileInputStream = null;


    View newReport;
    Activity reportActivity;

    /*
    FirebaseStorage storage = FirebaseStorage.getInstance();
    Date currentTime;
    boolean loading;*/

    AppDatabase2 dbs;


    public RecordService() {
        super("RecordService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        loading = true;
        currentTime = Calendar.getInstance().getTime();
        if (intent.hasExtra("UUID")) {
            fileupload(intent.getStringExtra("UUID"));

        }

    }

    //files werden in dem Records/uuid.pcm File in FirebaseStorage gespeichert
    public void fileupload(String uuid) {
        final String upfile = uuid;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //ClassLoader classLoader = getClass().getClassLoader();
                FileInputStream inputStream;
                byte[] bytes = null;
                try {
                    inputStream = openFileInput(upfile);
                    bytes = IOUtils.toByteArray(inputStream);
                } catch (Exception e) {
                    Log.d("Link_Request", "inputstreamfail");
                    e.printStackTrace();
                }
                try {
                    //New Version without Firebase and via own webserver:
                    URL url = new URL("https://narutohyuuga3.pythonanywhere.com/uploadPCM");
                    HttpURLConnection connection = null;

                    connection = (HttpURLConnection) url.openConnection();

//            String auth = "Bearer " + oauthToken;
//            connection.setRequestProperty("Authorization", basicAuth);

                    //String boundary = UUID.randomUUID().toString();
                    String boundary = "*****";
                    connection.setRequestMethod("POST");

                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                    DataOutputStream request = new DataOutputStream(connection.getOutputStream());//uc.getOutputStream());

                    request.writeBytes("--" + boundary + "\r\n");
                    request.writeBytes("Content-Disposition: form-data; name=\"description\"\r\n\r\n");
                    request.writeBytes("audio" + "\r\n");

                    request.writeBytes("--" + boundary + "\r\n");
                    request.writeBytes("Content-Disposition: form-data; name=\"audio\"; filename=\"" + upfile + ".pcm" + "\"\r\n\r\n");


                    request.write(bytes);
                    //request.write(FileUtils.readFileToByteArray(file));
                    request.writeBytes("\r\n");

                    request.writeBytes("--" + boundary + "--\r\n");
                    request.flush();
                    int respCode = connection.getResponseCode();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        downloadTask(uuid);



        /* //Old version by Flopfl
        StorageReference storageRef = storage.getReference();
        StorageReference spaceRef = storageRef.child("records/"+uuid+".pcm");
        Log.d("Link_Request", uuid);
        String date=new Date().toString();
        StorageMetadata metadata=new StorageMetadata.Builder().setCustomMetadata("Date",date).build();
        FileInputStream inputStream;
        byte[] bytes=null;
        try {
            inputStream = openFileInput(uuid);
            bytes = IOUtils.toByteArray(inputStream);
        } catch (Exception e) {
            Log.d("Link_Request", "inputstreamfail");
            e.printStackTrace();
        }
        if(bytes!=null){
         //   printBytes(bytes);
            UploadTask uploadTask=spaceRef.putBytes(bytes,metadata);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri url=taskSnapshot.getUploadSessionUri();

                    Log.d("Link_Request", url.toString());
                }

            });
            downloadTask(uuid);

        }*/

    }

    //Bewertungen mit der passenden UUID werden wieder heruntergeladen
    public void downloadTask(String uuid) {
        SystemClock.sleep(75000); //waiting 75 secs from sending and return of analysed file
        //uuid = "testfile_final";
        final String downfile = uuid;
        reportActivity = new Activity();
        Thread p = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                //Preparing file to write in it with directory in download/mydir
                File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "mydir");

                Log.d("Linl-Request2", dir.getAbsolutePath());
                if (!dir.exists()) {
                    Log.d("Linl-Request2", "mkDir");
                    dir.mkdir();
                }
                if (!Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).exists()) {
                    Log.d("Linl-Request2", "something went wrong");
                }


                //HTTPs-Version




                /*try {
                    File gpxfile = new File(dir, downfile+".txt");
                    FileWriter writer = new FileWriter(gpxfile);

                    Log.d("Linl-Request2", "fileWriter");

                    //Downloadstream
                   /* InputStream is = (InputStream) new URL("http://narutohyuuga3.pythonanywhere.com/return-filesTXT/?TXTkey=" + downfile + ".txt").openStream();

                    InputStreamReader isr = new InputStreamReader ( is ) ;
                    BufferedReader buffreader = new BufferedReader ( isr ) ;

                    String readString = buffreader.readLine ( ) ;

                    StringBuffer datax = new StringBuffer("");
                    while ( readString != null ) {
                        datax.append(readString);
                        readString = buffreader.readLine ( ) ;
                    }

                    Log.d("Linl-Request2", "sting created");

                    isr.close ( ) ;

                    writer.append(readString);

                    Log.d("Linl-Request2", "file written");
                    writer.flush();
                    writer.close();
                    is.close();

                    Log.d("Linl-Request2", "closed");
                } catch (Exception e){
                    e.printStackTrace();
                }*/
                //Log.d("Linl-Request2", "finish");


                //HTTPs-Version


                //Original Version
                File gpxfile = new File(dir, downfile + ".txt");
                Log.d("Linl-Request2", "file created");
                //Download part
                try (BufferedInputStream in = new BufferedInputStream(new URL("https://narutohyuuga3.pythonanywhere.com/return-filesTXT/?TXTkey=" + downfile + ".txt").openStream());
                     FileOutputStream fos = new FileOutputStream(gpxfile)) {
                    Log.d("Linl-Request2", "start of download");

                    byte dataBuffer[] = new byte[1024];
                    int bytesRead;
                    String s = new String(dataBuffer, StandardCharsets.UTF_8);
                    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                        fos.write(dataBuffer, 0, bytesRead);
                    }

                    //surfbytes[n]=bytesRead;

                    Log.d("Linl-Request2", s);
                    Log.d("Linl-Request2", "sucess of download");


                    //Bytesorting for surface
                    dbs = AppDatabase2.getInstance(getApplicationContext());
                    loading = false;

                    String time = currentTime.toString();
                    String[] time2 = time.split("GMT");

                    ArrayList<String> surfbytes = new ArrayList<String>();

                    String content = "";
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(gpxfile);
                        char current;
                        while (fis.available() > 0) {
                            current = (char) fis.read();
                            content += String.valueOf(current);
                        }
                    } catch (Exception e) {

                    } finally {
                        if (fis != null) {
                            try {
                                fis.close();
                            } catch (IOException e) {
                            }
                        }
                    }
                    Log.d("Linl-Request2", content);

                    String[] splitArray = content.split("\n");
                    for (int i = 0; i < splitArray.length; i++) {
                        surfbytes.add(splitArray[i]);
                    }
                    Log.d("Linl-Request2", "splitting sucess");

                    int intbits[] = new int[4];
                    int schut = 100;
                    for (int i = 0; i < splitArray.length; i++) {
                        String test = splitArray[i];
                        Log.d("Link-Request", "length old " + test.length() + " " + test);
                        test.replaceAll("[^\\p{Print}]", "");

                        int lastIdx = test.length() - 1;
                        if (lastIdx >= 0) {
                            test = test.substring(0, lastIdx);
                        }
                        int idx = test.indexOf(".");
                        if (idx > 0) {
                            test = test.substring(0, idx);
                        }
                        Log.d("Link-Request", "length new " + test.length());

                        intbits[i] = Integer.parseInt(test);
                        Log.d("Linl-Request2", "Element:");
                        Log.d("Linl-Request2", "Intbit at i: " + intbits[i]);
                        Log.d("Linl-Request2", "##########");

                    }
                    Log.d("Linl-Request2", "Integer finished");

                    if (intbits[2] < 10) {
                        schut = 0;
                    }
                    Log.d("Linl-Request2", "compare sucess");


                    //get Battery status
                    //
                    //
                    IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                    Intent batteryStatus = getBaseContext().registerReceiver(null, ifilter);

                    int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                    float batteryPct = level * 100 / (float) scale;
                    Log.d("Entry-State", "BatLevel " + batteryPct);
                    //
                    //


                    //intbits[2] gegen batteryPct getauscht
                    final InhalationEntry entry = new InhalationEntry(intbits[1], intbits[0], schut, (int) batteryPct, intbits[3], time2[0]);
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            dbs.taskDao2().insertTask(entry);
                        }
                    });

                    Log.d("Link_Request2", currentTime.toString());

                } catch (IOException e) {
                    Log.d("Linl-Request2", "failure of download");
                }


/*                try {
                    Log.d("Link_Request2", downfile);
                    InputStream is = (InputStream) new URL("http://narutohyuuga3.pythonanywhere.com/return-filesTXT/?TXTkey=" + downfile + ".txt").getContent();
                    FileOutputStream output = null;
                    if(reportActivity!=null) {
                        // reportActivity.openFileOutput(downfile,0);
//                    FileOutputStream output = new FileOutputStream(new File(Environment.getExternalStorageDirectory()+"/"+ downfile+".txt"));
                        Log.d("Link_Request2","File Store in "+ Environment.getExternalStorageDirectory().getAbsolutePath());
                        output  = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/myFile.txt"));

                        Log.d("Link_Request2", "filestream finished");
                        byte data[] = new byte[1024];
                        int count;
                        while ((count = is.read(data)) != -1)
                            output.write(data, 0, count);
                        output.flush();
                        output.close();
                        is.close();
                    }else{
                        Log.d("Linl-Request2", "report Is null");
                    }
                    SystemClock.sleep(100);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
        });
        p.start();
        try {
            p.join();
        } catch (
                InterruptedException e) {
            e.printStackTrace();
        }

        /*
        //Version 1.0
        try {
            URL downurl= new URL("http://narutohyuuga3.pythonanywhere.com/return-filesTXT/?TXTkey="+uuid+".txt");
            InputStream is = downurl.openStream();
            DataInputStream dis = new DataInputStream(is);

            byte[] buffer = new byte[1024];
            int length;

            FileOutputStream fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory()+"/"+ uuid+".txt"));
            while ((length = dis.read(buffer))>0){
                fos.write(buffer, 0, length);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/



        /*
        StorageReference storageRef = storage.getReference();
        StorageReference spaceRef = storageRef.child("results/"+uuid+".txt");


-----usable?----
        int k=0;
        final long ONE_MEGABYTE = 1024 * 1024;

        while(loading && k<12){
            k++;

            //firebasedownload: spaceRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {

                ----
                    dbs= AppDatabase2.getInstance(getApplicationContext());
                    loading=false;
                  //  final InhalationEntry entry= new InhalationEntry((int)bytes[0],(int)bytes[1],(int)bytes[3],(int)bytes[4],currentTime.toString());
                    String time=currentTime.toString();
                    String[] time2 =time.split("GMT");
                    int schut=100;
                    if(bytes[2]<10){
                        schut=0;
                    }
                    final InhalationEntry entry= new InhalationEntry(bytes[1],bytes[0],schut,bytes[2],bytes[3],time2[0]);
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                                dbs.taskDao2().insertTask(entry);
                        }
                    });

                    for(byte b:bytes){

                        Log.d("Link_bytes", ""+b);
                    }

                    Log.d("Link_Request2", currentTime.toString());

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                    Log.d("Link_Request2", "failed download");
                }
            });

            try {
                Thread.sleep(5000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d("Link_Request2","trying for "+k*5+ "sek");
        }*/


    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void run() {

    }
}
