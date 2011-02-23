/*
Copyright (c) 2009 nullwire aps

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

Contributors: 
Mads Kristiansen, mads.kristiansen@nullwire.com
Glen Humphrey
Evan Charlton
Peter Hewitt
*/

package org.tenagra.crashone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;


/*
 * Things we report back:
 * App Version
 * App Package
 * Phone Model
 * Android Release Version
 * Android SDK Version
 * Board
 * Brand
 * CPU ABI
 * Device
 * ID
 * Manufacturer
 * Model
 * Product
 * Tags
 * Time
 * Type
 * Network Type (Name)
 * Network Info (Detailed)
 * Data Directory
 * External Storage State
 * External Storage Directory
 * External Storage Available Blocks
 * External Storage Block Size
 */

public class ExceptionHandler {
	
	public static String TAG = "org.tenagra.ExceptionsHandler";
	public static Context c;
	
	
	private static String[] stackTraceFileList = null;
	
	/**
	 * Register handler for unhandled exceptions.
	 * @param context
	 */
	public static boolean register(final Context context) {
		Log.i(TAG, "Registering default exceptions handler");
		// Get information about the Package
		PackageManager pm = context.getPackageManager();
		c = context;
		Activity activity = (Activity) context;
		try {
			PackageInfo pi;
			// Version
			pi = pm.getPackageInfo(context.getPackageName(), 0);

			// Files dir for storing the stack traces
			G.FILES_PATH = context.getFilesDir().getAbsolutePath();
			
            Log.d(TAG, "Registering App Data");
			G.APP_VERSION = pi.versionName;
			G.APP_PACKAGE = pi.packageName;
            G.APP_CRASHTIME = new Long(SystemClock.elapsedRealtime()).toString();
            G.APP_VERSION_CODE = new Integer(new PackageInfo().versionCode).toString();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            G.APP_SHARED_PREFERENCES = (prefs.getAll()).toString();
			
            Log.d(TAG, "Registering Android Data");
            G.ANDROID_VERSION = android.os.Build.VERSION.RELEASE;
            G.ANDROID_SDK = android.os.Build.VERSION.SDK;
            G.ANDROID_TAGS = android.os.Build.TAGS;
            G.ANDROID_VERSION = android.os.Build.VERSION.RELEASE;
            
            Log.d(TAG, "Registering Storage Data");
            try {
				G.DATA_DIRECTORY = Environment.getDataDirectory().getCanonicalPath();
			} catch (IOException e) {
				G.DATA_DIRECTORY = "Unavailable Information";
			}
            String external;
			try {
				external = Environment.getExternalStorageDirectory().getCanonicalPath();
	            StatFs stat = new StatFs(external);
	            G.DATA_EXTERNAL_AVAILABLE_BLOCKS = new Integer(stat.getAvailableBlocks()).toString();
	            G.DATA_EXTERNAL_BLOCK_SIZE= new Integer(stat.getBlockSize()).toString();
	            G.DATA_EXTERNAL_STORAGE_DIRECTORY = external;
			} catch (IOException e) {
				external = "Unavailable Information";
	            G.DATA_EXTERNAL_AVAILABLE_BLOCKS = "Unavailable Information"; 
	            G.DATA_EXTERNAL_BLOCK_SIZE = "Unavailable Information"; 
	            G.DATA_EXTERNAL_STORAGE_DIRECTORY = "Unavailable Information"; 
			}
            G.DATA_EXTERNAL_STORAGE_STATE = Environment.getExternalStorageState();
            
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            G.INTERNAL_MEMORY_TOTAL = new Long(totalBlocks * blockSize).toString();
            long availableBlocks = stat.getAvailableBlocks();
            G.INTERNAL_MEMORY_AVAILABLE = new Long(availableBlocks * blockSize).toString();
            
            Log.d(TAG, "Registering Network Data");
			ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info=connManager.getActiveNetworkInfo();
            G.NETWORK_IS_CONNECTED = new Boolean(connManager.getActiveNetworkInfo().isConnected()).toString();
            G.NETWORK_IS_AVAILABLE = new Boolean(connManager.getActiveNetworkInfo().isAvailable()).toString();
            G.NETWORK_IS_ROAMING = new Boolean(connManager.getActiveNetworkInfo().isRoaming()).toString();
            G.NETWORK_TYPE = info.getTypeName();
            G.NETWORK_SUBTYPE = info.getSubtypeName();
            
            Log.d(TAG, "Registering Phone Data");
            G.PHONE_BOARD 	= android.os.Build.BOARD;
            G.PHONE_BRAND 	= android.os.Build.BRAND;
            G.PHONE_CPUABI	= android.os.Build.CPU_ABI;
            G.PHONE_FINGERPRINT = android.os.Build.FINGERPRINT;
            G.PHONE_ID 		= android.os.Build.ID;
            G.PHONE_MANUFACTURER = android.os.Build.MANUFACTURER;
            G.PHONE_MODEL	= android.os.Build.MODEL;
            G.PHONE_PRODUCT = android.os.Build.PRODUCT;
            G.PHONE_TYPE	= android.os.Build.TYPE;
            Display display = activity.getWindowManager().getDefaultDisplay();

            G.USER = android.os.Build.USER;
            G.HOST = android.os.Build.HOST;
            G.ORIENTATION = new Integer(context.getResources().getConfiguration().orientation).toString();

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		boolean stackTracesFound = false;
		// We'll return true if any stack traces were found
		if ( searchForStackTraces().length > 0 ) {
			stackTracesFound = true;
		}
		
		new Thread() {
			@Override
			public void run() {
				// First of all transmit any stack traces that may be lying around
				submitStackTraces();
				UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
				if (currentHandler != null) {
					Log.d(TAG, "current handler class="+currentHandler.getClass().getName());
				}	
				// don't register again if already registered
				if (!(currentHandler instanceof DefaultExceptionHandler)) {
					// Register default exceptions handler
					Thread.setDefaultUncaughtExceptionHandler(
							new DefaultExceptionHandler(currentHandler, context));
				}
			}
       	}.start();
		
		return stackTracesFound;
	}
	
	/**
	 * Register handler for unhandled exceptions.
	 * @param context
	 * @param Url
	 */
	public static void register(Context context, String url) {
		Log.i(TAG, "Registering default exceptions handler: " + url);
		// Use custom URL
		G.URL = url;
		// Call the default register method
		register(context);
	}
	
	public static void registerCustomData(String key, String value) {
	    if (G.CUSTOM_DATA == ""){
	        G.CUSTOM_DATA = key +": " + value;
	    }
	    else{
	        G.CUSTOM_DATA = G.CUSTOM_DATA + ", " + key +": " + value;
	    }
	}

	
	/**
	 * Search for stack trace files.
	 * @return
	 */
	private static String[] searchForStackTraces() {
		if ( stackTraceFileList != null ) {
			return stackTraceFileList;
		}
		File dir = new File(G.FILES_PATH + "/");
		// Try to create the files folder if it doesn't exist
		dir.mkdir();
		// Filter for ".stacktrace" files
		FilenameFilter filter = new FilenameFilter() { 
			public boolean accept(File dir, String name) {
				return name.endsWith(".stacktrace"); 
			} 
		}; 
		return (stackTraceFileList = dir.list(filter));	
	}
	
	/**
	 * Look into the files folder to see if there are any "*.stacktrace" files.
	 * If any are present, submit them to the trace server.
	 */
	public static void submitStackTraces() {
		try {
			Log.d(TAG, "Looking for exceptions in: " + G.FILES_PATH);
			String[] list = searchForStackTraces();
			if ( list != null && list.length > 0 ) {
				Log.d(TAG, "Found "+list.length+" stacktrace(s)");
				for (int i=0; i < list.length; i++) {
					String filePath = G.FILES_PATH+"/"+list[i];
					// Extract the version from the filename: "packagename-version-...."
					String version = list[i].split("-")[0];
					Log.d(TAG, "Stacktrace in file '"+filePath+"' belongs to version " + version);
					// Read contents of stacktrace
					StringBuilder contents = new StringBuilder();
					BufferedReader input =  new BufferedReader(new FileReader(filePath));
					HashMap<String, String> reportValues= new HashMap<String, String>();
					
					String line = null;
	                String key;
	                String val;
	                int t;
	                while (( line = input.readLine()) != null){
	                    if(line.contains("|||||")) {
    	                    t = line.indexOf("|||||");
    	                    key = line.substring(0, t);
    	                    val = line.substring(t+5);
	                        reportValues.put(key, val);
	                        continue;
	                    }
	                    else {
                            contents.append(line);
    			            contents.append(System.getProperty("line.separator"));
	                    }
			        }
			        input.close();
			        String stacktrace;
			        stacktrace = contents.toString();
			        Log.d(TAG, "Transmitting stack trace: " + stacktrace);
			        Log.d(TAG, G.URL);
			        
			        // Transmit stack trace with POST request
					DefaultHttpClient httpClient = new DefaultHttpClient(); 
					HttpPost httpPost = new HttpPost(G.URL);
					httpPost.addHeader("Accept-Encoding", "gzip");
					List <NameValuePair> nvps = new ArrayList <NameValuePair>(); 
					
					Iterator<String> it = reportValues.keySet().iterator();
					String k;
					while(it.hasNext()) {
					    k = it.next();
					    nvps.add(new BasicNameValuePair(k, reportValues.get(k)));
					}
                    nvps.add(new BasicNameValuePair("stacktrace", stacktrace));
                    
					httpPost.setEntity(new UrlEncodedFormEntity(nvps));

					// We don't care about the response, so we just hope it went well and on with it
					httpClient.execute(httpPost);					
				}
			}
		} catch( Exception e ) {
			e.printStackTrace();
		} finally {
			try {
				String[] list = searchForStackTraces();
				for ( int i = 0; i < list.length; i ++ ) {
					File file = new File(G.FILES_PATH+"/"+list[i]);
					file.delete();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
