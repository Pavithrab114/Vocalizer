package com.mirrorlabs.vocalizer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;

import java.io.FileReader;
import java.io.IOException;




import java.util.HashMap;
import java.util.List;








import android.app.AlertDialog;


import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;



import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;


import android.os.Bundle;
import android.os.Environment;

import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;





import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.View.OnClickListener;

import android.view.View;
import android.view.Window;



import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import android.widget.ListView;


import android.widget.Toast;

public class VocalizerMain extends ListActivity implements OnInitListener, OnUtteranceCompletedListener {
	
	
	private CommentsDataSource datasource;
	TextToSpeech tts;
	EditText edtxt;
	private int MY_DATA_CHECK_CODE = 0;
	MediaPlayer mButtonClick;
	MediaPlayer mButtonClick1;
	MediaPlayer mButtonClick2;
	MediaPlayer mMediaPlayer;
	AudioManager audioManager;
	private static final String PREF_PITCH = "PREF_PITCH";
    private static final String PREF_SPEED = "PREF_SPEED";
    private static final String PREF_VOLUME = "PREF_VOLUME";
	protected static final int FILE_SELECT = 1;
    private int mPitch;
    private int mSpeed;
    private int mVolume;
    boolean mCanceled;


    @Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		//set content view of this screen to main.xml
		setContentView(R.layout.main);
		
		// Load saved preferences
        final SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        mPitch = prefs.getInt(PREF_PITCH, 50);
        mSpeed = prefs.getInt(PREF_SPEED, 50);
        mVolume = prefs.getInt(PREF_VOLUME, 50);
        
        
        
        edtxt = (EditText)findViewById(R.id.myeditText);
	
		mButtonClick = MediaPlayer.create(this,R.raw.button_click5);
		mButtonClick1 = MediaPlayer.create(this,R.raw.button_click3);
		mButtonClick2 = MediaPlayer.create(this,R.raw.button_click6);

		datasource = new CommentsDataSource(this);
		datasource.open();
		
		List<Comment> values = datasource.getAllComments();

		// Use the SimpleCursorAdapter to show the
		// elements in a ListView
		ArrayAdapter<Comment> adapter = new ArrayAdapter<Comment>(this,
				android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
		listfunction();
		


	       
	}
    
    private Intent createShareIntent() {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Hey Guys Checkout This awesome Vocalizer Ultra Pro for Android.It's name is File Explorer Ultra");
        return Intent.createChooser(intent, "Share");
    }
    
    public void listfunction(){
    	
    	ListView mylist;
		mylist = getListView();
		
		//display options when list item is clicked
		mylist.setOnItemLongClickListener(new OnItemLongClickListener() {
            
			@Override
			public boolean onItemLongClick(AdapterView<?> av, View v,
					final int position, long id) {
				// TODO Auto-generated method stub
				
				Object itemno= id;
				Object note = getListAdapter().getItem(position);
				String keyword= note.toString();
				final EditText editNote;
				String value = itemno.toString();
				final int i = Integer.parseInt(value);
				
				//declaration of edit dialog box 
		        final Dialog dialog = new Dialog(VocalizerMain.this);
		        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
		        dialog.setTitle("Edit Note");
		        dialog.setContentView(R.layout.edit_dialog);
		        dialog.setCancelable(true);
		      //end of dialog declaration
		        
				//declaration of options dialog box 
				final CharSequence[] items = {"Edit", "Delete", "Save","Send Text","Send Audio"};

				AlertDialog.Builder builder = new AlertDialog.Builder(VocalizerMain.this);
				builder.setTitle("Options");
				builder.setItems(items, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dInterface, int item) {
				    	switch(item){
				    	case 0 :
				    		dialog.show();
				    		break;
				    	case 1 :
				    		@SuppressWarnings("unchecked")
							ArrayAdapter<Comment> adapter = (ArrayAdapter<Comment>) getListAdapter();
							Comment comment = null;
			  				if (getListAdapter().getCount() > 0) {
			  					comment = (Comment) getListAdapter().getItem(i);
			  					datasource.deleteComment(comment);
			  					adapter.remove(comment);
			  				}
			  				Toast.makeText(dialog.getContext(),"List item deleted",Toast.LENGTH_SHORT).show();
			  			    break;
				    	case 2:
				    		 LayoutInflater factory = LayoutInflater.from(VocalizerMain.this);
				             final View SaveAsView = factory.inflate(R.layout.alert2_dialog, null);
				             final AlertDialog alert2 = new AlertDialog.Builder(VocalizerMain.this).create();
				 			 alert2.setTitle("Save as");
				 			 alert2.setView(SaveAsView);
				 			 alert2.setIcon(R.drawable.save_as);
				 			
				 			 
				 			 alert2.setButton("Save", new DialogInterface.OnClickListener() {
							      public void onClick(DialogInterface dialog, int which) {
							    	  final EditText savetxt= (EditText)alert2.findViewById(R.id.saveTextfile);
							    	  final RadioGroup choice = (RadioGroup)alert2.findViewById(R.id.choiceTxtorMp3);
							    	  int radioId = choice.getCheckedRadioButtonId();
							    	  String filename = savetxt.getText().toString();
							    	  Object note = getListAdapter().getItem(position);
										String keyword= note.toString();
										switch (radioId) {
										  case R.id.text: 
											  try {
									    			File myDir = new File("/sdcard/appdata");
									    			File myFile = new File("/sdcard/"+filename+".txt");
									    			if(myDir.exists()==false)
									    			{
									    			myDir.mkdir();
									    			}
									    			myFile.createNewFile();
									    			FileOutputStream fOut = new FileOutputStream(myFile);
									    			OutputStreamWriter myOutWriter = 
									    									new OutputStreamWriter(fOut);
									    			myOutWriter.write(keyword);
									    			myOutWriter.close();
									    			fOut.close();
									    			Toast.makeText(getBaseContext(),
									    					"File saved as sdcard/"+filename+".txt",
									    					Toast.LENGTH_SHORT).show();
									    		} catch (Exception e) {
									    			Toast.makeText(getBaseContext(), e.getMessage(),
									    					Toast.LENGTH_SHORT).show();
									    		}
										        break;
										  case R.id.mp3 :
											  
											  HashMap<String,String> myHashRender = new HashMap<String, String>();
											     Object note1 = getListAdapter().getItem(position);
												String keyword1= note1.toString();
												
												 final String tempFileName = "/sdcard/temp.mp3";
												 final String permFileName ="/sdcard/"+filename+".mp3";
												 myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, keyword1);
												 tts.setPitch(mPitch / 50.0f);
										         tts.setSpeechRate(mSpeed / 50.0f);  
										         
										         tts.synthesizeToFile(keyword, myHashRender, tempFileName);
												 tts.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {
													
													@Override
													public void onUtteranceCompleted(String keyword) {
														// TODO Auto-generated method stub
														try {
															FileUtils.copyFile(tempFileName, permFileName);
															
															
														} catch (IOException e) {
															// TODO Auto-generated catch block
															e.printStackTrace();
														}
														
														
														}
													});
												 Toast.makeText(getBaseContext(),
									    					"File saved as sdcard/"+filename+".mp3",
									    					Toast.LENGTH_SHORT).show();
												
											       break;
										}
												                    
										
							        }}); 
							alert2.setButton2("Cancel", new DialogInterface.OnClickListener() {
							      public void onClick(DialogInterface dialog, int which) {
							 
							       alert2.dismiss();
							 
							    } }); 
							 alert2.show();
				 			 break;
				 			 
				 			 
				    	case 3:
							Object note1 = getListAdapter().getItem(position);
							String keyword1= note1.toString();
				            
				    		Intent i = new Intent(Intent.ACTION_SEND);
				    	    i.putExtra(Intent.EXTRA_SUBJECT, "Title");
				    		i.putExtra(Intent.EXTRA_TEXT,keyword1);
				         	i.setType("text/plain");
				    		startActivity(Intent.createChooser(i, "Send via"));

				    		break;
				    	case 4:
				    		LayoutInflater factory1 = LayoutInflater.from(VocalizerMain.this);
				            final View OpenTxtView = factory1.inflate(R.layout.openfile_dialog, null);
				            final AlertDialog alert4 = new AlertDialog.Builder(VocalizerMain.this).create();
							 alert4.setTitle("Send file");
							 alert4.setView(OpenTxtView);
							 alert4.setIcon(R.drawable.mail);
							 //definition and declaration of send button on dialog
							 alert4.setButton("Send", new DialogInterface.OnClickListener() {
							     
							      public void onClick(DialogInterface dialog, int which) {
							    	// TODO Auto-generated method stub
										EditText savefile = (EditText)alert4.findViewById(R.id.saveTextfile); 	  
								        final String filename = savefile.getText().toString();
								    	  
					    		            HashMap<String,String> myHashRender = new HashMap<String, String>();
							                Object note3= getListAdapter().getItem(position);
								             String keyword3= note3.toString();
								
								              final String tempFileName = "/sdcard/temp.mp3";
							             	 final String permFileName ="/sdcard/"+filename+".mp3";
							              	 myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, keyword3);
								              tts.setPitch(mPitch / 50.0f);
						                        tts.setSpeechRate(mSpeed / 50.0f);  
						                     
						                   tts.synthesizeToFile(keyword3, myHashRender, tempFileName);
						                   alert4.dismiss();
								           tts.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {
									
									               @Override
									                public void onUtteranceCompleted(String keyword3) {
									               	// TODO Auto-generated method stub
										            try {
											
										           	FileUtils.copyFile(tempFileName, permFileName);
										           	Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND); 
											
	                                        
											         Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),filename));

								    	             emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Vocalizer Audio"); 
								    	             emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, 
								    	             "Hey..how are u doing ? this is a audio msg for you i made using Vocalizer .Cheers :)"); 
								    	              emailIntent.setType("text/plain");

								    	             emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
								    	    startActivity(Intent.createChooser(emailIntent, "Send"));
								    	    

											} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										
										
										}
									});
							    	  
							        }}); 
							 
							alert4.setButton2("Cancel", new DialogInterface.OnClickListener() {
							      public void onClick(DialogInterface dialog, int which) {
							 
							       alert4.dismiss();
							 
							    } }); 
							
							alert4.show();
							             
							
				    	     
				    	
				         break;
				    	
				    	}
				        
				    }
				});
				AlertDialog alert = builder.create();
				alert.show();

			    
			   //define the contents of edit dialog
		          editNote = (EditText)dialog.findViewById(R.id.editNote_dialog);
		          editNote.setText(keyword);
		        // dialog save button to save the edited item
		          Button saveButton =(Button)dialog.findViewById(R.id.save_note);
		          //for updating the list item
		          saveButton.setOnClickListener(new View.OnClickListener() {
					
		        	  
		        	  
                    @Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
						@SuppressWarnings("unchecked")
						ArrayAdapter<Comment> adapter = (ArrayAdapter<Comment>) getListAdapter();
						Comment comment = null;
						//check if the string entered is null
						if(editNote.getText().toString().length()>0){
						//first delete the current note
		  					comment = (Comment) getListAdapter().getItem(i);
		  					datasource.deleteComment(comment);
		  					adapter.remove(comment);
		  				//add the edited note
						comment = datasource.createComment(editNote.getText().toString());
						adapter.insert(comment,i);	  
						dialog.dismiss();
						Toast.makeText(dialog.getContext(),"List item updated",Toast.LENGTH_SHORT).show();
						}
						else {
							 Toast.makeText(dialog.getContext(), "Enter the edited note !" , Toast.LENGTH_SHORT).show();
						}
						
					}
				});
		          
		         //cancel button declaration
		        Button cancelButton=(Button)dialog.findViewById(R.id.cancel_dialog);
		        cancelButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						
					}
				}); 
		       
		        //dialog.show(); 
		        dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.note_edit);
		        
				
				return false;
			}
		});
	
		
		   tts = new TextToSpeech(VocalizerMain.this, new TextToSpeech.OnInitListener() {
				
				@Override
				public void onInit(int status) {
					// TODO Auto-generated method stub
					if(status!= TextToSpeech.ERROR){
						
					}
					
				}
			});
		
    }
    

	//inflate the menu 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_option, menu);
	    return true;
	}
	//actions performed on menu options
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.about_us:     
	        //declare the dialog box for About us menu option
	        	  final Dialog dialog = new Dialog(VocalizerMain.this);
	              dialog.setContentView(R.layout.about_us_dialog);
	              dialog.setTitle("About");
	              dialog.setCancelable(true);
	              
	              
	               TextView app_name ,version_name,developer_name,twitter_name;
	               app_name =(TextView)dialog.findViewById(R.id.name_app);
	               version_name =(TextView)dialog.findViewById(R.id.version_app);
	               developer_name =(TextView)dialog.findViewById(R.id.name_developer);
	               twitter_name =(TextView)dialog.findViewById(R.id.name_twitter);
	               
	              
	               
	               
	              Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);
	              cancelButton.setOnClickListener(new OnClickListener() {
	              @Override
	                  public void onClick(View v) {
	                     dialog.dismiss();
	                  }
	              });
	              
	              
	              
	              Button emailButton = (Button) dialog.findViewById(R.id.contact_button);
	              emailButton.setOnClickListener(new OnClickListener() {
	                  @Override
	                      public void onClick(View v) {
	                  	   
	                  	final Intent i= new Intent(Intent.ACTION_SEND);
	      	        	String[] recipients = new String[]{"kshark05@gmail.com"};
	      	               i.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
	      	               i.putExtra(Intent.EXTRA_EMAIL,recipients);
	      	               i.putExtra(Intent.EXTRA_TEXT,"Write us a feedback about what you liked or didn't like about the app.");
	      	    	       i.setType("message/rfc822");
	      	    		   startActivity(Intent.createChooser(i, "Contact Us"));
	                         dialog.dismiss();
	                      }
	                  });
	              
	              Button marketButton = (Button) dialog.findViewById(R.id.market_link);
	              marketButton.setOnClickListener(new OnClickListener() {
	                  @Override
	                      public void onClick(View v) {
	                         dialog.dismiss();
	                      }
	                  });
	              
	              
	              dialog.show(); 
	  	        	
	    
	                            break;
	        case R.id.help_menu: 
	        	//declare the dialog box for help menu option
	        	 Intent myintent = new Intent(getApplicationContext(),Help.class);
	        	 startActivity(myintent);
	        	 
	        	
	             break;
	        case R.id.file_browser:
	        	Intent intent1 = new Intent(getApplicationContext(),FileBrowser.class);
	        	 startActivity(intent1);
	        	break;
	       
	        	
	        case R.id.exit_menu: 
	        	 //exit from application
	        	  finish();
	              break;
	              
	        case R.id.contact_us:
	        	
	        	
	        final Intent i= new Intent(Intent.ACTION_SEND);
	        	String[] recipients = new String[]{"mirrorlabs.android@gmail.com"};
	        	
	          i.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
	          i.putExtra(Intent.EXTRA_EMAIL,recipients);
	          i.putExtra(Intent.EXTRA_TEXT,"Write us a feedback about what you liked or didn't like about the app.");
	          i.setType("message/rfc822");
	    		startActivity(Intent.createChooser(i, "Conatact Us"));
	        	break;
	        case  R.id.settings_menu: 
	        	ComponentName componentToLaunch = new ComponentName(
	        	        "com.android.settings",
	        	        "com.android.settings.TextToSpeechSettings");
	        	Intent intent = new Intent();
	        	intent.addCategory(Intent.CATEGORY_LAUNCHER);
	        	intent.setComponent(componentToLaunch);
	        	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        	startActivity(intent);

	        	//Intent myIntent = new Intent(VocalizerMain.this, Settings.class);
	        	//VocalizerMain.this.startActivity(myIntent);

            break;                  
	    }
	    return true;
	}

	  
    private void showMessage(String message) {
        Toast.makeText(VocalizerMain.this, message, Toast.LENGTH_SHORT).show();
    }
	
	public void doOpenFile(String path){
		File file = new File(path);
		ArrayAdapter<Comment> adapter = (ArrayAdapter<Comment>) getListAdapter();

  	     Comment comment= null;

		 if(file.exists()==true){
			    //Read text from file
					
				StringBuilder text = new StringBuilder();

				try {

				    BufferedReader br = new BufferedReader(new FileReader(file));
				    String line;

				    while ((line = br.readLine()) != null) {
				        text.append(line);
				        text.append('\n');
				    }
				    comment = datasource.createComment(text.toString());
					adapter.add(comment);
					showMessage("comment was added !");
				}
				
				catch (IOException e) {
				  //You'll need to add proper error handling here
					e.printStackTrace();
				}
           
	             
				
				
		 }
	}
	
	public void openFileDialog(final String filepath){
		
		final ArrayAdapter<Comment> adapter = (ArrayAdapter<Comment>) getListAdapter();
	    Comment comment = null;
		LayoutInflater factory = LayoutInflater.from(VocalizerMain.this);
		
        final View OpenTxtView = factory.inflate(R.layout.openfile_dialog, null);
        final EditText openfiletxt = (EditText)OpenTxtView.findViewById(R.id.saveTextfile);
		  if(filepath!=null){
			  openfiletxt.setText(filepath);
		  }
        final AlertDialog alert3 = new AlertDialog.Builder(VocalizerMain.this).create();
		 alert3.setTitle("Open file");
		 alert3.setView(OpenTxtView);
		 alert3.setIcon(R.drawable.folder);
		 alert3.setButton("Open", new DialogInterface.OnClickListener() {
			     
		      public void onClick(DialogInterface dialog, int which) {
		    	  
		    	  Comment comment= null;
				 
				  String filename = openfiletxt.getText().toString();
				 
		    	//Find the directory for the SD Card using the API
					
					
		            File file = new File(filename);
		           
				   
		           if(file.exists()==true){
				    //Read text from file
						
					StringBuilder text = new StringBuilder();

					try {

					    BufferedReader br = new BufferedReader(new FileReader(file));
					    String line;

					    while ((line = br.readLine()) != null) {
					        text.append(line);
					        text.append('\n');
					    }
					}
					
					catch (IOException e) {
					  //You'll need to add proper error handling here
						e.printStackTrace();
					}
                  
		             
					//Set the text
					if(filename.length()>0)
				   {
			         edtxt.setText(text);
					 comment = datasource.createComment(edtxt.getText().toString());
					 adapter.add(comment);
					 edtxt.setText("");
					}
					else
					   {
					    Toast.makeText(getBaseContext(),"file is not readable or doesn't exist", Toast.LENGTH_SHORT).show();
					   }
					}
					
					else 
					    {
					     Toast.makeText(getBaseContext(),"file is not readable or doesn't exist",Toast.LENGTH_SHORT).show();	
					    }
		    	 

		        }}); 
		alert3.setButton3("Browse", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(VocalizerMain.this,FileBrowser.class);
				startActivityForResult(intent,FILE_SELECT);
			}
		});
		 
		alert3.setButton2("Cancel", new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
		 
		       alert3.dismiss();
		 
		    } }); 
		
		alert3.show();
		
	}
	  // Will be called via the onClick attribute
		// of the buttons in main.xml
	public void onClick(View view) throws IOException {
		@SuppressWarnings("unchecked")
		final ArrayAdapter<Comment> adapter = (ArrayAdapter<Comment>) getListAdapter();
	    Comment comment = null;
		
		
		switch (view.getId()) {
		case R.id.add:
			// Save the new comment to the database
			mButtonClick2.start();
			
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
	        findViewById(R.id.myeditText).startAnimation(shake);
	        
			if(edtxt.getText().toString().length()>0){
			comment = datasource.createComment(edtxt.getText().toString());
			//tts.speak(edtxt.getText().toString(),TextToSpeech.QUEUE_FLUSH, null);
			adapter.add(comment);

			}
			else {
				Toast.makeText(this, "Add a valid note ! " , Toast.LENGTH_SHORT).show();
				break;
			}
			edtxt.setText("");
			break;
			
		case R.id.delete:
			
		//alert dialog for resetting the list
			final AlertDialog alertDialog = new AlertDialog.Builder(VocalizerMain.this).create();
			alertDialog.setTitle("Reset List");
			

			
		    alertDialog.setIcon(R.drawable.clear_list_attention);
			alertDialog.setMessage("Do u want to clear the list ?");
			alertDialog.setButton("Yes", new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
			    	  mButtonClick.start();
			    	  adapter.clear();
			    	  datasource.deleteList();

			        }}); 
			alertDialog.setButton2("No", new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
			 
			       alertDialog.dismiss();
			 
			    } }); 
			alertDialog.show();
			break;
			
		case R.id.speak:
		// text to speech the entered text or read the list item
		   
		    	
			 tts.setPitch(mPitch / 50.0f);
		     tts.setSpeechRate(mSpeed / 50.0f);
		   
       
	    if(edtxt.getText().toString().length()>0)
	        {
	    	    Animation shake1 = AnimationUtils.loadAnimation(this, R.anim.shake);
		        findViewById(R.id.myeditText).startAnimation(shake1);
	        	tts.speak(edtxt.getText().toString(),TextToSpeech.QUEUE_FLUSH, null);
	        	break;
	        }
	    
	    else if (edtxt.getText().toString().length()==0 && getListAdapter().getCount()==0)
        {   
	    	Animation shake1 = AnimationUtils.loadAnimation(this, R.anim.shake);
	        findViewById(R.id.myeditText).startAnimation(shake1);
        	Toast.makeText(VocalizerMain.this, "Enter some text first ! ", Toast.LENGTH_SHORT).show();
        }
	        
	     else if (edtxt.getText().toString().length()==0)
	        {   
	        	if(getListAdapter().getCount()>1)
	        	{
	        		int last_item=getListAdapter().getCount();
	                
	        		for(int i=0; i<last_item; i++)
	        		{
	        			Object note = this.getListAdapter().getItem(i);
	        			String keyword = note.toString();
	        			int j=i+1;
	        			
	        			HashMap<String, String> myHashRender = new HashMap<String, String>();
	        			myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utid");
	        			tts.speak("item" + j + keyword, TextToSpeech.QUEUE_ADD, myHashRender);
                        tts.setOnUtteranceCompletedListener(this);
                        

	        		}
	        		
	        	}
	        	else if (getListAdapter().getCount()==1)
	        	{
	        		Object note = this.getListAdapter().getItem(0);
        			String keyword = note.toString();
	        		tts.speak(keyword, TextToSpeech.QUEUE_FLUSH, null);
	        	}
	        	
	        }
	      
	        
	        break;
		case R.id.cleartext:
			mButtonClick1.start();
			edtxt.setText("");
		
		     break;
			
		case R.id.Opentext:
			
			openFileDialog(null);
	        
			
			
			break;
			
		case R.id.pause:
			tts.stop();
			
			break;
		
		
		case R.id.shareVia:
			Intent i=new Intent(android.content.Intent.ACTION_SEND);
			i.setType("text/plain");
			i.putExtra(android.content.Intent.EXTRA_SUBJECT,"Vocalizer");
			i.putExtra(android.content.Intent.EXTRA_TEXT, "Hey guys i found a really cool app on market it's name is Vocalizer.Check it out !!");
			startActivity(Intent.createChooser(i,"Share via"));
            break; 
			
		case R.id.savefile:
			
			if(edtxt.getText().toString().length() >0){
			
			final Dialog dialog = new Dialog(VocalizerMain.this);
	        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
	        dialog.setTitle("Save as mp3 ");
	        dialog.setContentView(R.layout.save_file_dialog);
	        dialog.setCancelable(true);
	        
	        

	   	            final ProgressDialog mProgressDialog = new ProgressDialog(this);
	   	            mProgressDialog.setCancelable(true);
	   	            mProgressDialog.setTitle("Saving file");
	   	            mProgressDialog.setMessage("saving file...please wait");
	   	            mProgressDialog.setIndeterminate(true);
	   	            mProgressDialog.setOnCancelListener(mOnCancelListener);

	   	           
	       
	          //end of dialog declaration
	        
	         final  EditText editfilename = (EditText)dialog.findViewById(R.id.savefile);
	        
	        // dialog save button to save the edited item
	          Button save =(Button)dialog.findViewById(R.id.save_savedialog);
	          save.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
			 HashMap<String,String> myHashRender = new HashMap<String, String>();
			 final String keyword = edtxt.getText().toString();
			 final String filename =editfilename.getText().toString();
			 final String tempFileName = "/sdcard/temp.mp3";
			 final String permFileName ="/sdcard/"+filename+".mp3";
			 myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, keyword);
			 tts.setPitch(mPitch / 50.0f);
	         tts.setSpeechRate(mSpeed / 50.0f);
	         
	         mProgressDialog.show();
	         mCanceled = false;



			 tts.synthesizeToFile(keyword, myHashRender, tempFileName);
			 tts.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {
				
				@Override
				public void onUtteranceCompleted(String keyword) {
					// TODO Auto-generated method stub
					try {
						FileUtils.copyFile(tempFileName, permFileName);
						mProgressDialog.dismiss();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					}
				});
			
			 dialog.dismiss();
					}
					
				});
				
				Button cancelButton =(Button)dialog.findViewById(R.id.cacel_savedialog);
				cancelButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
				dialog.show(); 
		        dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.save);
			}
			else
			{
				Animation shake2 = AnimationUtils.loadAnimation(this, R.anim.shake);
		        findViewById(R.id.myeditText).startAnimation(shake2);
	            Toast.makeText(getBaseContext(), "Enter some text first !", Toast.LENGTH_SHORT).show();
			}
			
			 break;
				
         case R.id.settingsTab: 
			/*ComponentName componentToLaunch = new ComponentName(
	        "com.android.settings",
	        "com.android.settings.TextToSpeechSettings");
	          Intent intent = new Intent();
	           intent.addCategory(Intent.CATEGORY_LAUNCHER);
	           intent.setComponent(componentToLaunch);
	           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	           startActivity(intent);
	           break; */
        	 final Dialog dialog = new Dialog(VocalizerMain.this);
 	        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
 	        dialog.setTitle("Speech Properties");
 	        dialog.setContentView(R.layout.properties_dialog);
 	        dialog.setCancelable(true);
 			
        	SeekBar pitch = (SeekBar)dialog.findViewById(R.id.seekPitch) ;
        	SeekBar volume = (SeekBar)dialog.findViewById(R.id.seekVolume) ;
        	SeekBar speed = (SeekBar)dialog.findViewById(R.id.seekSpeed) ;
           
            pitch.setOnSeekBarChangeListener(mSeekListener);
            volume.setOnSeekBarChangeListener(mSeekListener);
            speed.setOnSeekBarChangeListener(mSeekListener);
            
            audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

            
            volume.setMax(maxVolume);
            pitch.setProgress(mPitch);
            speed.setProgress(mSpeed);
            volume.setProgress(curVolume);
            
            dialog.show();
            dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.tools);
		break;


	}
		adapter.notifyDataSetChanged();
		
	}
	
	//create a log instance
	public void appendLog(String text)
	{       
	   File logFile = new File("sdcard/log.file");
	   if (!logFile.exists())
	   {
	      try
	      {
	         logFile.createNewFile();
	      } 
	      catch (IOException e)
	      {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
	      }
	   }
	   try
	   {
	      //BufferedWriter for performance, true to set append to file flag
	      BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
	      buf.append(text);
	      buf.newLine();
	      buf.close();
	   }
	   catch (IOException e)
	   {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	   }
	}

	

	@Override
	protected void onResume() {
		datasource.open();
		super.onResume();
		
	
	}

	@Override
	protected void onPause() {
		datasource.close();
		final SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        final Editor editor = prefs.edit();
        editor.putInt(PREF_PITCH, mPitch);
        editor.putInt(PREF_SPEED, mSpeed);
        editor.putInt(PREF_VOLUME,mVolume);
        editor.commit();
        super.onPause();
        
             
	}
	private final DialogInterface.OnCancelListener mOnCancelListener = new OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            mCanceled = true;
            tts.stop();
        }
    };

	
	 

	// check for tts data and if not present install it
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			        if (requestCode == MY_DATA_CHECK_CODE) {
			            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
			                // success, create the TTS instance
			                tts = new TextToSpeech(this, this);
			            }
			            else {
			                // missing data, install it
			                Intent installIntent = new Intent();
			                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
			                startActivity(installIntent);
			            }
			        }
			        if(requestCode==FILE_SELECT){
			        String path=	data.getExtras().getString("path");
			        openFileDialog(path);
			        }
			 
			    }
	
	
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
        // Get the item that was clicked
        
        Object note = this.getListAdapter().getItem(position);
        String keyword = note.toString();
      
        //text to speech when list item is clicked
        tts.setPitch(mPitch / 50.0f);
	     tts.setSpeechRate(mSpeed / 50.0f);
	     
        tts.speak(keyword, TextToSpeech.QUEUE_FLUSH, null);
        
	         
	    
        }

	

	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
		
		       

		 if (status == TextToSpeech.SUCCESS) {
				            Toast.makeText(VocalizerMain.this,
				                    "Speaking...", Toast.LENGTH_SHORT).show();
				        }
				        else if (status == TextToSpeech.ERROR) {
				            Toast.makeText(VocalizerMain.this,
				                    "Error occurred while initializing Text-To-Speech engine", Toast.LENGTH_LONG).show();
				        }
				    }
	
	

	
	

	@Override
	public void onUtteranceCompleted(String uttId) {
		// TODO Auto-generated method stub
	}
	
	 private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
	        @Override
	        public void onStopTrackingTouch(SeekBar v) {
	            // Do nothing.
	        }

	        @Override
	        public void onStartTrackingTouch(SeekBar v) {
	            // Do nothing.
	        }

	        @Override
	        public void onProgressChanged(SeekBar v, int progress, boolean fromUser) {
	            if (!fromUser) {
	                return;
	            }

	            switch (v.getId()) {
	                case R.id.seekPitch:
	                    mPitch = progress;
	                    
	                    break;
	                case R.id.seekSpeed:
	                    mSpeed = progress;
	                   
	                    break;
	                case R.id.seekVolume:
	                	mVolume = progress;
	                	audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress,0);
	            }
	        }
	    };


	
	    
	
}



	
        

