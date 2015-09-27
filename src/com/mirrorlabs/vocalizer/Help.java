package com.mirrorlabs.vocalizer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;



import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.TextView;


public class Help extends Activity{
	
	TextView helptext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_dialog);
		helptext = (TextView)findViewById(R.id.help_text);
		AssetManager manager;
		String line =null;
		Vector <String> assetVector = new Vector<String>();
		try
		{
			manager = getAssets();
			InputStream is = manager.open("help.txt");
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			 while ((line = br.readLine()) != null) {
			        helptext.append(line);
			        helptext.append("\n");
			        assetVector.add(line);
			       
			    }
			}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
       
		}
		
		
		
	}



