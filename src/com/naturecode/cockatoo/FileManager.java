package com.naturecode.cockatoo;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

import android.os.Environment;

public class FileManager {
	final String MEDIA_PATH = Environment.getExternalStorageDirectory().getPath();
	private static final String AUDIO_RECORDER_FOLDER = "Cockatoo";
	private Vector<RowData> fileList = new Vector<RowData>();

	public FileManager(){}

	public Vector<RowData> getPlayList(){
		File home = new File(MEDIA_PATH, AUDIO_RECORDER_FOLDER);

		File[] files = home.listFiles(new FileExtensionFilter());

		Arrays.sort(files, new Comparator<File>(){
			public int compare(File f2, File f1){
				return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
			} 
		});

		
		if (files.length > 0) {
			int count=0;
			for (File file : files) {
				RowData cur_file = new RowData();
				cur_file.setImage_id(count);
				cur_file.setFile_index(count);
				cur_file.setPath(file.getPath());
				cur_file.setName(file.getName().substring(0, (file.getName().lastIndexOf('.'))));
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				cur_file.setDetail(sdf.format(file.lastModified()));
				fileList.add(cur_file);
				count++;
			}
		}
		return fileList;
	}
	
	class FileExtensionFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith(".3gp") || name.endsWith(".3GP"));
		}
	}
}