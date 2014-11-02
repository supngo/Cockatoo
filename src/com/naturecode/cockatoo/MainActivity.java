package com.naturecode.cockatoo;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;

public class MainActivity extends Activity {
	private ImageButton record;
//	private ImageButton stop;
//	private MediaPlayer mp;
	private MediaRecorder recorder = null;
	private static final String AUDIO_RECORDER_FOLDER = "Cockatoo";
//	private static final String TAG = "test";
	private String default_filename="";
	private Chronometer myChronometer ;
	private boolean isRecord = true;

	@Override
	public void onPause() {
		super.onPause();
		try {
			stopRecord();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		myChronometer = (Chronometer)findViewById(R.id.chronometer);
		initControls();
	}

	private String getFilename(){
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, AUDIO_RECORDER_FOLDER);

		if(!file.exists()){
			file.mkdirs();
		}
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH)+1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int year = cal.get(Calendar.YEAR);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int min = cal.get(Calendar.MINUTE);
		int sec = cal.get(Calendar.SECOND);
		default_filename = month+""+day+""+year+""+hour+""+min+""+sec;
		String path = file.getAbsolutePath() + "/" + default_filename+".3gp";
		return path;
	}

	private void initControls(){
		record = (ImageButton)findViewById(R.id.record);
		record.setOnClickListener(new Button.OnClickListener() { 
			public void onClick (View v){ 
				if(isRecord){
					isRecord = false;
					record.setImageResource(R.drawable.stop);
					recordOn();
				}
				else{
					isRecord = true;
					record.setImageResource(R.drawable.record);
					try {
						recordOff();
					} catch (IOException e) {
						e.printStackTrace();
					} 	
				}
//				recordOn();
			}
		});
	}

	private void recordOn(){
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
		recorder.setOutputFile(getFilename());
		try {
			recorder.prepare();
			recorder.start();
			myChronometer.setBase(SystemClock.elapsedRealtime());
			myChronometer.start();
//			stop.setVisibility(View.VISIBLE);
//			record.setVisibility(View.INVISIBLE);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void stopRecord() throws IOException {
		if(recorder !=null){
			recorder.stop();
			recorder.release();
		}
		if(myChronometer !=null){
			myChronometer.stop();
			myChronometer.setBase(SystemClock.elapsedRealtime());	
		}
		
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File change_name = new File(filepath, AUDIO_RECORDER_FOLDER+"/"+default_filename+".3gp");
		if(change_name.exists()){
			change_name.delete();
		}
	}

	private void recordOff() throws IOException {
		if(recorder !=null){
			recorder.stop();
			recorder.release();
			recorder = null;
		}
		if(myChronometer !=null){
			myChronometer.stop();
			myChronometer.setBase(SystemClock.elapsedRealtime());
		}
//		record.setVisibility(View.VISIBLE);
//		stop.setVisibility(View.INVISIBLE);
		popUp();
	}
	
	private void popUp(){
		AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
		helpBuilder.setTitle("Save As");	
		final EditText file_name = new EditText(this);
		file_name.setSingleLine();
		
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH)+1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int year = cal.get(Calendar.YEAR);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int min = cal.get(Calendar.MINUTE);
		int sec = cal.get(Calendar.SECOND);
		String display_filename = month+"-"+day+"-"+year+"_"+hour+":"+min+":"+sec;
		
		file_name.setText(display_filename);
		helpBuilder.setView(file_name);
		helpBuilder.setMessage("Save file name as");
		helpBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String filepath = Environment.getExternalStorageDirectory().getPath();
				File change_name = new File(filepath, AUDIO_RECORDER_FOLDER+"/"+default_filename+".3gp");
				if(change_name.exists()){
//					Log.v(TAG, "change_name: "+change_name.getName());
					String new_name = file_name.getText().toString();
					if(new_name!=null && new_name.trim().length()>0){
						File new_file = new File(filepath, AUDIO_RECORDER_FOLDER+"/"+new_name+".3gp");
						change_name.renameTo(new_file);
					}
					else{
						change_name.delete();
					}
				}
			}
		});

		helpBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String filepath = Environment.getExternalStorageDirectory().getPath();
				File change_name = new File(filepath, AUDIO_RECORDER_FOLDER+"/"+default_filename+".3gp");
				if(change_name.exists()){
					change_name.delete();
					myChronometer.setBase(SystemClock.elapsedRealtime());
				}
			}
		});
		AlertDialog helpDialog = helpBuilder.create();
		helpDialog.show();
	}	
	
	public boolean onCreateOptionsMenu(Menu menu) {	
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}
    
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.playlist:
			if(record.getVisibility() == 4){
				try {
					recordOff();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if(record.getVisibility() == 0){
				if(recorder !=null){
					recorder.stop();
					recorder.release();
					recorder = null;
				}
				Intent myIntent = new Intent(MainActivity.this, PlayListActivity.class);
				MainActivity.this.startActivity(myIntent);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}