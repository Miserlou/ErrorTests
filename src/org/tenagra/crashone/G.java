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

import android.content.pm.PackageInfo;

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

public class G {
	// This must be set by the application - it used to automatically
	// transmit exceptions to the trace server
	public static String FILES_PATH 				= null;
	
	public static String APP_VERSION 				= "unknown";
	public static String APP_PACKAGE 				= "unknown";
	public static String APP_CRASHTIME				= "unknown";
	public static String APP_SHARED_PREFERENCES	= "unknown";
	public static String APP_VERSION_CODE         = "unknown";
	
    public static String ANDROID_VERSION          = "unknown";
    public static String ANDROID_SDK 				= "unknown";
    public static String ANDROID_TAGS				= "unknown";
    
    public static String PHONE_BOARD 				= "unknown";
    public static String PHONE_BRAND 				= "unknown";
    public static String PHONE_CPUABI 				= "unknown";
    public static String PHONE_FINGERPRINT        = "unknown";
    public static String PHONE_ID   				= "unknown";
    public static String PHONE_MANUFACTURER		= "unknown";
    public static String PHONE_MODEL 				= "unknown";
    public static String PHONE_PRODUCT 			= "unknown";
    public static String PHONE_TYPE				= "unknown";
    
    public static String NETWORK_TYPE				= "unknown";
    public static String NETWORK_SUBTYPE			= "unknown";
    public static String NETWORK_IS_CONNECTED 		= "unknown";
    public static String NETWORK_IS_AVAILABLE		= "unknown";
    public static String NETWORK_IS_ROAMING		= "unknown";
    
    public static String DATA_DIRECTORY			= "unknown";
    public static String DATA_EXTERNAL_STORAGE_STATE		= "unknown";
    public static String DATA_EXTERNAL_STORAGE_DIRECTORY	= "unknown";
    public static String DATA_EXTERNAL_AVAILABLE_BLOCKS	= "unknown";
    public static String DATA_EXTERNAL_BLOCK_SIZE	= "unknown";
    
    public static String INTERNAL_MEMORY_AVAILABLE = "unknown";
    public static String INTERNAL_MEMORY_TOTAL     = "unknown";
    
    public static String USER               = "unknown";
    public static String HOST               = "unknown";
    public static String ORIENTATION        = "unknown";
    
    public static String CUSTOM_DATA              = "";
    
    
    // Where are the stack traces posted?
	public static String URL						= "http://192.168.1.4:8000/report/";
	public static String TraceVersion				= "0.3.0";
}
