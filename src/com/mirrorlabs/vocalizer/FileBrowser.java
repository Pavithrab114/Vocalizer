package com.mirrorlabs.vocalizer;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import java.util.List;









import android.app.AlertDialog;

import android.app.ListActivity;

import android.content.DialogInterface;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
public class FileBrowser extends ListActivity{
	
	private List<String> item = null;
	private List<String> path = null;
	private TextView fileInfo;
	private String[] items;
	private String root = "/sdcard";

	/** Called when the activity is first created. */
	
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_browser);
        fileInfo = (TextView)findViewById(R.id.info);
        getFileList(root);
    }
    
    //To make listview for the list of file
    public void getFileList(String dirPath){
    	
    	item = new ArrayList<String>(); //Declare as Array list
    	path = new ArrayList<String>();
    	
    	File file = new File(dirPath); // get the file
    	File[] files = file.listFiles();//get the list array of file
    	Arrays.sort(files,filecomparator_type);
    	
    	if(!dirPath.equals(root)){
    		item.add(root); 
    		path.add(root);// to get back to main list
    		
    		item.add("..");
    		path.add(file.getParent()); // back one level 
    	}
    	
    	
    	for (int i = 0; i < files.length; i++){
    		
    		File fileItem = files[i];
    		if(fileItem.isDirectory() && fileItem.getName().toString().startsWith(".")){
    			
    			}
    		else if (fileItem.isDirectory())
    		{
    			path.add(fileItem.getPath());
        		item.add(fileItem.getName()); 
    		}
    		else{
    			path.add(fileItem.getPath());
        		item.add(fileItem.getName()); 
    		}
    	}
    		
    	fileInfo.setText("Info: "+dirPath+" [ " +files.length +" item ]");
    	items = new String[item.size()]; //declare array with specific number off item
    	item.toArray(items); //send data arraylist(item) to array(items
    	setListAdapter(new IconicList()); //set the list with icon
    	
    
     
    }
    
 
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id){
    	String filepath = path.get(position);
    	File file = new File(filepath);
    	
    
    	if(file.isDirectory()){ 
    		if(file.canRead()){
    			getFileList(path.get(position));
    		}
    		else {
    			new AlertDialog.Builder(this)
    			.setIcon(R.drawable.ic_launcher).setTitle("["+file.getName()+"] folder can't be read")
    			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				}).show();
    			
    		}
    	}
    	
    	 else if (file.getName().toString().contains("mp3")){
 			Intent i = new Intent();
				i.setAction(android.content.Intent.ACTION_VIEW);
				i.setDataAndType(Uri.fromFile(file), "audio/*");
				startActivity(i);
 		}
    	
    	 else if (file.getName().toString().contains("jpg"))
    	 {
	 			    		
			    		Intent picIntent = new Intent();
			    		picIntent.setAction(android.content.Intent.ACTION_VIEW);
			    		picIntent.setDataAndType(Uri.fromFile(file), "image/*");
			    		startActivity(picIntent);
	    			
	    		
	    	}
 		
 		else if (file.getName().toString().contains("mp4")){
 			Intent movieIntent = new Intent();
	    		movieIntent.setAction(android.content.Intent.ACTION_VIEW);
	    		movieIntent.setDataAndType(Uri.fromFile(file), "video/*");
	    		startActivity(movieIntent);
 		}
 		
 		else if (file.getName().toString().contains("apk")){
 		Intent apkIntent = new Intent();
			apkIntent.setAction(android.content.Intent.ACTION_VIEW);
			apkIntent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
			startActivity(apkIntent);
 		}
 		
 		else if (file.getName().toString().contains("txt")){
 			
 		   
 			Intent intent = new Intent();
 			intent.putExtra("path", file.getAbsolutePath());
 	        setResult(RESULT_OK, intent);
 		    finish();
 			
 			}
    	else {
    		new AlertDialog.Builder(this)
    		.setIcon(R.drawable.ic_launcher)
    		.setTitle("["+file.getName()+"]")
    		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			}).show();
    	}
    }
    
    protected boolean setOpenResult(File file) {
		Intent result;

		if (!file.canRead()) {
			Toast.makeText(this, "file can't be read", Toast.LENGTH_SHORT).show();
			return false;
		}

		result = new Intent();
		result.putExtra("path", file.getAbsolutePath());
        setResult(RESULT_OK, result);
		return true;
	}
    Comparator<? super File> filecomparator_type = new Comparator<File>(){
  	  @Override
		public int compare(File file1, File file2) {
  		 
			String arg0=file1.getName().toString();
			String arg1=file2.getName().toString();
			
			final int s1Dot = arg0.lastIndexOf('.');
	        final int s2Dot = arg1.lastIndexOf('.');
	        
	        if ((s1Dot == -1) == (s2Dot == -1)) { // both or neither
	        	
	            arg0 = arg0.substring(s1Dot + 1);
	            arg1 = arg1.substring(s2Dot + 1);
	            return (arg0.toLowerCase()).compareTo((arg1.toLowerCase()));
	        }
	       
	        else if (s1Dot == -1) { // only s2 has an extension, so s1 goes first
	            return -1;
	        } else { // only s1 has an extension, so s1 goes second
	            return 1;
	        }
	    }

  	 };	 
  	 
   
    class IconicList extends ArrayAdapter<Object> {

		
		public IconicList() {
			super(FileBrowser.this, R.layout.row, items);

			// TODO Auto-generated constructor stub
		}
		
		public View getView(int position, View convertView, ViewGroup parent){
			ViewHolder holder;
			if(convertView==null){
			LayoutInflater inflater = getLayoutInflater(); //to instantiate layout XML file into its corresponding View objects
		    convertView= inflater.inflate(R.layout.row, null); //to Quick access to the LayoutInflater  instance that this Window retrieved from its Context.
			holder = new ViewHolder(convertView);
			
			convertView.setTag(holder);
			}  
			else
			{
				holder = (ViewHolder)convertView.getTag();
			}
			
			holder.name.setText(items[position]);
			File f = new File(path.get(position)); //get the file according the position
			 
			 if(f.isDirectory()){ //decide are the file folder or file
				holder.image.setImageResource(R.drawable.folder1png);
			}
			 else if (f.getName().toString().toLowerCase().contains(".txt")){
				 holder.image.setImageResource(R.drawable.textpng);
			}
			 else if (f.getName().toString().toLowerCase().contains(".mp3")){
				 holder.image.setImageResource(R.drawable.audio);
			}
			 else if (f.getName().toString().toLowerCase().contains(".mp4")){
				 holder.image.setImageResource(R.drawable.video1);
			}
			 else if (f.getName().toString().toLowerCase().contains(".zip")){
				 holder.image.setImageResource(R.drawable.zip_icon);
			}
			
			
			else
			{
				holder.image.setImageResource(R.drawable.ic_launcher);
			}
			
			return(convertView);
		}
		
		
    	
    }
    class ViewHolder {
      public TextView name=null;
      public ImageView image=null;
      ViewHolder(View row){
    	  name = (TextView)row.findViewById(R.id.label);
    	  image = (ImageView)row.findViewById(R.id.icon);
      }
      void populateFrom(String s)
      {
      name.setText(s);
      }
    }
    
    

}
