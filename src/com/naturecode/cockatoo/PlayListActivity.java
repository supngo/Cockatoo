package com.naturecode.cockatoo;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class PlayListActivity extends ListActivity {
	private LayoutInflater mInflater;
	private Vector<RowData> fileList = new Vector<RowData>();
	private Vector<RowData> searchList = new Vector<RowData>();
	private static final String AUDIO_RECORDER_FOLDER = "Cockatoo";
	EditText file_search;
	RowData rd;
	CustomAdapter adapter;
//	private static final String TAG = "PlayList";
	private Integer[] atrributes = {R.layout.playlist, R.id.file_name, R.id.file_detail, R.id.playback_icon};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview);
		mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		createPlayList();
		
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			searchFiles(query);
		}
	}
	
	private void createPlayList(){
		FileManager fileman = new FileManager();
		fileList = fileman.getPlayList();	
		if(fileList!=null && fileList.size()>0){
			Integer[] images = new Integer[fileList.size()];
			for(int i=0;i<fileList.size();i++){
				images[i] = R.drawable.player;
			}
			adapter = new CustomAdapter(this, fileList, mInflater, atrributes, images);
			setListAdapter(adapter);			
			getListView().setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					playback(position);
				}
			});
			
			getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					selectOption(position);   
					return true;
				}
			});
		}
		else{
			ListView listView = (ListView) findViewById( android.R.id.list);
			listView.setEmptyView(findViewById( android.R.id.empty));
			listView.setAdapter( new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, new ArrayList<String>() ) );
		}
	}
	
	private void selectOption(int position){
		final int index = position;
		AlertDialog.Builder helpBuilder = new AlertDialog.Builder(PlayListActivity.this);
		helpBuilder.setTitle("Select option");
		helpBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String filepath = Environment.getExternalStorageDirectory().getPath();
				File delete_folder = new File(filepath, AUDIO_RECORDER_FOLDER);
				
				if(delete_folder.exists()){
					File[] files = delete_folder.listFiles();
					File delete_file = files[index];
					if(delete_file.exists()){
						deleteDialog(index);
						createPlayList();
					}
				}
			}
		});
		helpBuilder.setNegativeButton("Rename", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				renameDialog(index);
			}
		});
		helpBuilder.setNeutralButton("Share", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String filepath = Environment.getExternalStorageDirectory().getPath();
				File delete_folder = new File(filepath, AUDIO_RECORDER_FOLDER);
				
				if(delete_folder.exists()){
					File[] files = delete_folder.listFiles();
					File email_file = files[index];
				
					Intent intent = new Intent(Intent.ACTION_SEND);
					intent.setType("audio/acc");
					intent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
					intent.putExtra(Intent.EXTRA_SUBJECT, "");
					intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml("Attached."));
					intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/"+AUDIO_RECORDER_FOLDER+"/"+email_file.getName()));
					startActivity(intent);
				}

			}
		});
		
		helpBuilder.show();
	}
	
	private void playback(int position){		
		Intent intent = new Intent(PlayListActivity.this, PlaybackActivity.class);
		intent.putExtra("file_index", ""+position);
		startActivity(intent);
		finish();	
	}
	
	private void deleteDialog(int position){
		final String filepath = Environment.getExternalStorageDirectory().getPath();
		File select_folder = new File(filepath, AUDIO_RECORDER_FOLDER);
		
		if(select_folder.exists()){
			AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this);
			File[] files = select_folder.listFiles();
			final File select_file = files[position];
			
			if(select_file.exists()){
				deleteDialog.setTitle("Delete \""+select_file.getName().substring(0, select_file.getName().lastIndexOf('.'))+"\" ?");			
				deleteDialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						select_file.delete();
						createPlayList();
					}
				});
				
				deleteDialog.setPositiveButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
				});
				deleteDialog.show();
			}
		}
	}
	
	private void renameDialog(int position){
		final String filepath = Environment.getExternalStorageDirectory().getPath();
		File select_folder = new File(filepath, AUDIO_RECORDER_FOLDER);

		if(select_folder.exists()){
			AlertDialog.Builder renameDialog = new AlertDialog.Builder(this);
			renameDialog.setTitle("Save file name as");	
			final EditText file_name = new EditText(this);
			file_name.setSingleLine();
			File[] files = select_folder.listFiles();
			final File select_file = files[position];
			file_name.setText(select_file.getName().substring(0, select_file.getName().lastIndexOf('.')));
			renameDialog.setView(file_name);
			renameDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					String new_name = file_name.getText().toString();
					if(new_name!=null && new_name.trim().length()>0){
						File new_file = new File(filepath, AUDIO_RECORDER_FOLDER+"/"+new_name+".3gp");
						select_file.renameTo(new_file);
						createPlayList();
					}
				}
			});
		
			renameDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {}
			});
			renameDialog.show();
		}
	}
	
	private void searchFiles(String query) {
		searchList = new Vector<RowData>();
		FileManager fileList = new FileManager();
		int i=0,k=0;
		for(RowData itr:fileList.getPlayList()){
			if(itr.getName().toLowerCase().contains(query.toLowerCase())){
				RowData rd = new RowData(i, k, itr.getName(), itr.getDetail());
				searchList.add(rd);
				i++;
			}
			k++;
		}
		if (searchList.size() < 1) {
			ListView listView = (ListView) findViewById( android.R.id.list);
			listView.setEmptyView(findViewById( android.R.id.empty));
			listView.setAdapter( new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, new ArrayList<String>() ) );
		} 
		else {
			Integer[] images = new Integer[searchList.size()];
			for(int j=0;j<searchList.size();j++){
				images[j] = R.drawable.player;
			}
			adapter = new CustomAdapter(this, searchList, mInflater, atrributes, images);
			getListView().setAdapter(adapter); 
			getListView().setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					int file_index = searchList.elementAt(position).getFile_index();
					playback(file_index);
				}
			});
			
			getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					return true;
				}
			});
		}
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {	
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.search, menu);
	    return super.onCreateOptionsMenu(menu);
	}
    
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.search_record:
			onSearchRequested();
			return true;
		case R.id.playlist:
			createPlayList();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}