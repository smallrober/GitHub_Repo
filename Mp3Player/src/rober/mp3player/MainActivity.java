package rober.mp3player;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class MainActivity extends TabActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		TabHost tabHost = getTabHost();

		Intent localIntent = new Intent();
		localIntent.setClass(this, LocalMp3List.class);
		TabHost.TabSpec localSpec = tabHost.newTabSpec("Local");
		Resources res = getResources();
		localSpec.setIndicator("Local", res.getDrawable(android.R.drawable.stat_sys_download_done));
		localSpec.setContent(localIntent);
		tabHost.addTab(localSpec);
		
		Intent remoteIntent = new Intent();
		remoteIntent.setClass(this,RemoteMp3List.class);
		TabHost.TabSpec remoteSpec = tabHost.newTabSpec("Remote");
		
		remoteSpec.setIndicator("Remote", res.getDrawable(android.R.drawable.stat_sys_download));
		remoteSpec.setContent(remoteIntent);
		tabHost.addTab(remoteSpec);
	}
}
