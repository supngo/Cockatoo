package com.naturecode.cockatoo;

import java.io.IOException;
import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class PlaybackActivity extends Activity implements OnCompletionListener, SeekBar.OnSeekBarChangeListener {
	private ImageButton btnPlay;
	private ImageButton btnForward;
	private ImageButton btnBackward;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
//	private ImageButton btnPlaylist;
//	private ImageButton btnRepeat;
//	private ImageButton btnShuffle;
	private SeekBar songProgressBar;
	private TextView songTitleLabel;
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;
	private  MediaPlayer mp;
	private Handler mHandler = new Handler();;
	private FileManager fileman;
	private Utilities utils;
	private int seekForwardTime = 2000; // 5000 milliseconds
	private int seekBackwardTime = 2000;
	private int currentSongIndex = 0; 
//	private boolean isShuffle = false;
//	private boolean isRepeat = false;
	private Vector<RowData> fileList = new Vector<RowData>();
//	private static final String TAG = "Playback";
	
	
//	@Override
//	public void onPause() {
//		super.onPause();
//		if (mp!=null)
//			mp.release();
//		mHandler.removeCallbacks(mUpdateTimeTask);
//	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playback);
		
//		All player buttons
		btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		btnForward = (ImageButton) findViewById(R.id.btnForward);
		btnBackward = (ImageButton) findViewById(R.id.btnBackward);
		btnNext = (ImageButton) findViewById(R.id.btnNext);
		btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
//		btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
//		btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
//		btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
		songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
		songTitleLabel = (TextView) findViewById(R.id.songTitle);
		songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
		
//		Media Player
		mp = new MediaPlayer();
		fileman = new FileManager();
		utils = new Utilities();
		
//		Listeners
		songProgressBar.setOnSeekBarChangeListener(this); // Important
		mp.setOnCompletionListener(this); // Important
		
//		Getting all songs list
		fileList = fileman.getPlayList();
		int file_id=0;
		Intent data = getIntent();
		if(data!=null){
			Bundle extras = data.getExtras();
			if (extras != null) {
				String file_index = extras.getString("file_index");
				file_id = Integer.parseInt(file_index);
			}
		}
		
//		By default play first song
		playSong(file_id);
		
		/**
		 * Play button click event
		 * plays a song and changes button to pause image
		 * pauses a song and changes button to play image
		 * */
		btnPlay.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View arg0) {
//				check for already playing
				if(mp.isPlaying()){
					if(mp!=null){
						mp.pause();
//						Changing button image to play button
						btnPlay.setImageResource(R.drawable.btn_play);
					}
				}else{
//					Resume song
					if(mp!=null){
						mp.start();
						updateProgressBar();
//						Changing button image to pause button
						btnPlay.setImageResource(R.drawable.btn_pause);
					}
				}
				
			}
		});
		
		/**
		 * FORWARD button click event
		 * Forwards song specified seconds
		 * */
		btnForward.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View arg0) {
//				get current song position				
				int currentPosition = mp.getCurrentPosition();
//				check if seekForward time is lesser than song duration
				if(currentPosition + seekForwardTime <= mp.getDuration()){
//					forward song
					mp.seekTo(currentPosition + seekForwardTime);
				}else{
//					forward to end position
					mp.seekTo(mp.getDuration());
				}
			}
		});
		
		/**
		 * BACKWARD button click event
		 * Backward song to specified seconds
		 * */
		btnBackward.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				get current song position				
				int currentPosition = mp.getCurrentPosition();
//				check if seekBackward time is greater than 0 sec
				if(currentPosition - seekBackwardTime >= 0){
//					backward song
					mp.seekTo(currentPosition - seekBackwardTime);
				}else{
//					backward to starting position
					mp.seekTo(0);
				}
				
			}
		});
		
		/**
		 * NEXT button click event
		 * Plays next song by taking currentSongIndex + 1
		 * */
		btnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				check if next song is there or not
				if(currentSongIndex < (fileList.size() - 1)){
					playSong(currentSongIndex + 1);
					currentSongIndex = currentSongIndex + 1;
				}else{
//					play first song
					playSong(0);
					currentSongIndex = 0;
				}
				
			}
		});
		
		/**
		 * BACK button click event
		 * Plays previous song by currentSongIndex - 1
		 * */
		btnPrevious.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(currentSongIndex > 0){
					playSong(currentSongIndex - 1);
					currentSongIndex = currentSongIndex - 1;
				}else{
//					play last song
					playSong(fileList.size() - 1);
					currentSongIndex = fileList.size() - 1;
				}
			}
		});
	}
	
//	/**
//	 * Receiving song index from playlist view
//	 * and play the song
//	 * */
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if(resultCode == 100){
//			currentSongIndex = data.getExtras().getInt("file_index");
//			playSong(currentSongIndex);
//		}
//	}
	
	/**
	 * Function to play a song
	 * @param songIndex - index of song
	 * */
	public void playSong(int songIndex){
		try {
        	mp.reset();
			mp.setDataSource(fileList.get(songIndex).getPath());
			mp.prepare();
			mp.start();
			
//			Displaying Song title
			String songTitle = fileList.get(songIndex).getName();
        	songTitleLabel.setText(songTitle);
			
//        	Changing Button Image to pause image
			btnPlay.setImageResource(R.drawable.btn_pause);
			
//			set Progress bar values
			songProgressBar.setProgress(0);
			songProgressBar.setMax(100);
			
//			Updating progress bar
			updateProgressBar();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Update timer on seekbar
	 * */
	public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);        
    }	
	
	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			try{
				long totalDuration = mp.getDuration();
				long currentDuration = mp.getCurrentPosition();

//				Displaying Total Duration time
				songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
//				Displaying time completed playing
				songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

//				Updating progress bar
				int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
//				Log.d("Progress", ""+progress);
				songProgressBar.setProgress(progress);

//				Running this thread after 100 milliseconds
				mHandler.postDelayed(this, 100);
			}catch(IllegalStateException e){}
		}
	};
		
	/**
	 * 
	 * */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {}

	/**
	 * When user starts moving the progress handler
	 * */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
//		remove message Handler from updating progress bar
		mHandler.removeCallbacks(mUpdateTimeTask);
    }
	
	/**
	 * When user stops moving the progress hanlder
	 * */
	@Override
    public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = mp.getDuration();
		int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
		
//		forward or backward to certain seconds
		mp.seekTo(currentPosition);
		
//		update timer progress again
		updateProgressBar();
    }
	
	/**
	 * On Song Playing completed
	 * if repeat is ON play same song again
	 * if shuffle is ON play random song
	 * */
	@Override
	public void onCompletion(MediaPlayer arg0) {
		btnPlay.setImageResource(R.drawable.btn_play);
		mHandler.removeCallbacks(mUpdateTimeTask);
		songProgressBar.setProgress(0);
//		songProgressBar.setMax(100);
//		if(isRepeat){
//			playSong(currentSongIndex);
//		} 
//		else if(isShuffle){
//			Random rand = new Random();
//			currentSongIndex = rand.nextInt((fileList.size() - 1) - 0 + 1) + 0;
//			playSong(currentSongIndex);
//		} 
//		else{
//			if(currentSongIndex < (fileList.size() - 1)){
//				playSong(currentSongIndex + 1);
//				currentSongIndex = currentSongIndex + 1;
//			}
//			else{
//				playSong(0);
//				currentSongIndex = 0;
//			}
//		}
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		mp.release();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {	
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}
    
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.playlist:
			Intent myIntent = new Intent(PlaybackActivity.this, PlayListActivity.class);
			PlaybackActivity.this.startActivity(myIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}