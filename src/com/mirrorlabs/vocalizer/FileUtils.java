package com.mirrorlabs.vocalizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.util.Log;



public class FileUtils {
	public static Boolean copyFile(String srFile, String dtFile) 
	        throws IOException 
	    {
		
		    File in = new File(srFile);
		    File out = new File(dtFile);
	        FileChannel inChannel = new
	            FileInputStream(in).getChannel();
	        FileChannel outChannel = new
	            FileOutputStream(out).getChannel();
	        try {
	            inChannel.transferTo(0, inChannel.size(),
	                    outChannel);
	            
	        } 
	        catch (IOException e) {
	            throw e;
	        }
	        finally {
	            if (inChannel != null) inChannel.close();
	            if (outChannel != null) outChannel.close();
	        }
	        return true;
	       
	    }
	
	public static String getCanonizePath(File f) {
		String path;
		try {
			path = f.getCanonicalPath();
		} catch (IOException e) {
			path = f.getPath();
			
		}
		return path;
	}

	    


}
