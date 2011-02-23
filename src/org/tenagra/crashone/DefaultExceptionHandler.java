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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Random;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

public class DefaultExceptionHandler implements UncaughtExceptionHandler {

	private UncaughtExceptionHandler defaultExceptionHandler;
	
	private static final String TAG = "UNHANDLED_EXCEPTION";
	private Context context;

	// constructor
	public DefaultExceptionHandler(UncaughtExceptionHandler pDefaultExceptionHandler, Context co)
	{
		defaultExceptionHandler = pDefaultExceptionHandler;
		context=co;
	}
	 
	// Default exception handler
	public void uncaughtException(Thread t, Throwable e) {
		// Here you should have a more robust, permanent record of problems
	    final Writer result = new StringWriter();
	    final PrintWriter printWriter = new PrintWriter(result);
	    e.printStackTrace(printWriter);
	    try {
	    	// Random number to avoid duplicate files
	    	Random generator = new Random();
	    	int random = generator.nextInt(99999);    	
	    	// Embed version in stacktrace filename
	    	String filename = G.APP_VERSION+"-"+Integer.toString(random);
	    	Log.d(TAG, "Writing unhandled exception to: " + G.FILES_PATH+"/"+filename+".stacktrace");
		    // Write the stacktrace to disk
	    	BufferedWriter bos = new BufferedWriter(new FileWriter(G.FILES_PATH+"/"+filename+".stacktrace"));
            bos.write("android_sdk|||||" + G.ANDROID_SDK + "\n");
            bos.write("android_tags|||||" + G.ANDROID_TAGS+ "\n");
            bos.write("android_version|||||" + G.ANDROID_VERSION + "\n");
            G.APP_CRASHTIME = new Long(SystemClock.elapsedRealtime()).toString();
            bos.write("app_crashtime|||||" + G.APP_CRASHTIME + "\n");
            bos.write("app_package|||||" + G.APP_PACKAGE + "\n");
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            G.APP_SHARED_PREFERENCES = (prefs.getAll()).toString();
            bos.write("app_shared_preferences|||||" + G.APP_SHARED_PREFERENCES + "\n");
            bos.write("app_version|||||" + G.APP_VERSION + "\n");
            bos.write("app_version_code|||||" + G.APP_VERSION_CODE + "\n");
            Log.d(TAG, "Registering Storage Data");
            try {
                G.DATA_DIRECTORY = Environment.getDataDirectory().getCanonicalPath();
            } catch (IOException ee) {
                G.DATA_DIRECTORY = "Unavailable Information";
            }
            String external;
            try {
                external = Environment.getExternalStorageDirectory().getCanonicalPath();
                StatFs stat = new StatFs(external);
                G.DATA_EXTERNAL_AVAILABLE_BLOCKS = new Integer(stat.getAvailableBlocks()).toString();
                G.DATA_EXTERNAL_BLOCK_SIZE= new Integer(stat.getBlockSize()).toString();
                G.DATA_EXTERNAL_STORAGE_DIRECTORY = external;
            } catch (IOException eee) {
                external = "Unavailable Information";
                G.DATA_EXTERNAL_AVAILABLE_BLOCKS = "Unavailable Information"; 
                G.DATA_EXTERNAL_BLOCK_SIZE = "Unavailable Information"; 
                G.DATA_EXTERNAL_STORAGE_DIRECTORY = "Unavailable Information"; 
            }
            G.DATA_EXTERNAL_STORAGE_STATE = Environment.getExternalStorageState();
            bos.write("data_directory|||||" + G.DATA_DIRECTORY 	+ "\n");
            bos.write("data_external_available_blocks|||||" + G.DATA_EXTERNAL_AVAILABLE_BLOCKS+ "\n");
            bos.write("data_external_storage_directory|||||" + G.DATA_EXTERNAL_STORAGE_DIRECTORY+ "\n");
            bos.write("data_external_storage_state|||||" + G.DATA_EXTERNAL_STORAGE_STATE+ "\n");
            
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
            bos.write("internal_memory_total|||||" + G.INTERNAL_MEMORY_TOTAL+ "\n");
            bos.write("internal_memory_avilable|||||" + G.INTERNAL_MEMORY_AVAILABLE + "\n");
            
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info=connManager.getActiveNetworkInfo();
            G.NETWORK_IS_CONNECTED = new Boolean(connManager.getActiveNetworkInfo().isConnected()).toString();
            G.NETWORK_IS_AVAILABLE = new Boolean(connManager.getActiveNetworkInfo().isAvailable()).toString();
            G.NETWORK_IS_ROAMING = new Boolean(connManager.getActiveNetworkInfo().isRoaming()).toString();
            G.NETWORK_TYPE = info.getTypeName();
            G.NETWORK_SUBTYPE = info.getSubtypeName();
            bos.write("network_is_available|||||" + G.NETWORK_IS_AVAILABLE+ "\n");
            bos.write("network_is_connected|||||" + G.NETWORK_IS_CONNECTED+ "\n");
            bos.write("network_is_roaming|||||" + G.NETWORK_IS_ROAMING+ "\n");
            bos.write("network_subtype|||||" + G.NETWORK_SUBTYPE+ "\n");
            bos.write("network_type|||||" + G.NETWORK_TYPE+ "\n");
            bos.write("phone_board|||||" + G.PHONE_BOARD+ "\n");
            bos.write("phone_brand|||||" + G.PHONE_BRAND+ "\n");
            bos.write("phone_cpuabi|||||" + G.PHONE_CPUABI+ "\n");
            bos.write("phone_fingerprint|||||" + G.PHONE_FINGERPRINT+ "\n");
            bos.write("phone_id|||||" + G.PHONE_ID+ "\n");
            bos.write("phone_manufacturer|||||" + G.PHONE_MANUFACTURER+ "\n");
            bos.write("phone_model|||||" + G.PHONE_MODEL+ "\n");
            bos.write("phone_product|||||" + G.PHONE_PRODUCT+ "\n");
            bos.write("phone_type|||||" + G.PHONE_TYPE+ "\n");
            bos.write("user|||||" + G.USER + "\n");
            bos.write("host|||||" + G.HOST + "\n");
            bos.write("orientation|||||" + context.getResources().getConfiguration().orientation + "\n");
            bos.write("custom_data|||||" + G.CUSTOM_DATA+ "\n");
            bos.write(result.toString());
		    bos.flush();
		    // Close up everything
		    bos.close();
	    } catch (Exception ebos) {
	    	// Nothing much we can do about this - the game is over
	    	ebos.printStackTrace();
	    }
		Log.d(TAG, result.toString());	    
		//call original handler  
    	defaultExceptionHandler.uncaughtException(t, e);        
	}
}