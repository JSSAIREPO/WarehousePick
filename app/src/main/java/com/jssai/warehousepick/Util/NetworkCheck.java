package com.jssai.warehousepick.Util;

import android.content.Context;
import android.net.ConnectivityManager;

public class NetworkCheck {
	 //Network information variables
     private ConnectivityManager mNetworkConnection;
     private Context mNetworkContext;
     private boolean mNetworkConnectingFlag=false;
	 
	/**
	 * Function to check network connection 
	 */
	public boolean ConnectivityCheck(Context context) {
		mNetworkContext=context;
		//Network connectivity check
		mNetworkConnection = (ConnectivityManager) this.mNetworkContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(mNetworkConnection.getActiveNetworkInfo()!=null && mNetworkConnection.getActiveNetworkInfo().isAvailable() && mNetworkConnection.getActiveNetworkInfo().isConnectedOrConnecting()){
			if(mNetworkConnection.getActiveNetworkInfo().isConnected()){
				mNetworkConnectingFlag=true;
			}else if(mNetworkConnection.getActiveNetworkInfo().isFailover()){
				mNetworkConnectingFlag=false;
			}
		}else{
			mNetworkConnectingFlag=false;
		}
		
		return mNetworkConnectingFlag;
	}
}




