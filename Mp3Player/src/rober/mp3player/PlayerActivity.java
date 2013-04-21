package rober.mp3player;

import rober.model.Mp3Info;
import rober.mp3player.AppConstant.PlayerMSG;
import rober.mp3player.service.PlayerService;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class PlayerActivity extends Activity{
	
	private ImageButton beginButton = null;
	private ImageButton pauseButton = null;
	private ImageButton stopButton = null;
	private Mp3Info mp3Info = null;
	private TextView lrcTextView = null;
	private BroadcastReceiver receiver = null;
	private IntentFilter filter = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);
		
		Intent intent = getIntent();
		mp3Info = (Mp3Info) intent.getSerializableExtra("mp3Info");
		
		receiver = new LrcBroadcastReceiver();
		
		//System.out.println("player---->" + mp3Info);
		beginButton = (ImageButton)findViewById(R.id.begin);
		pauseButton = (ImageButton)findViewById(R.id.pause);
		stopButton = (ImageButton)findViewById(R.id.stop);
		lrcTextView = (TextView)findViewById(R.id.lrcText);
		
		beginButton.setOnClickListener(new BeginButtonListener());
		pauseButton.setOnClickListener(new PauseButtonListener());
		stopButton.setOnClickListener(new StopButtonListener());
	}
	
	class BeginButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.putExtra("mp3Info", mp3Info);
			intent.putExtra("MSG", PlayerMSG.PLAY_MSG);
			intent.setClass(PlayerActivity.this, PlayerService.class);
			startService(intent);		
		}
		
	}
	
	class PauseButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.putExtra("mp3Info", mp3Info);
			intent.putExtra("MSG", PlayerMSG.PAUSE_MSG);
			intent.setClass(PlayerActivity.this, PlayerService.class);
			startService(intent);
		}
	
	}
	
	class StopButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
			intent.putExtra("mp3Info", mp3Info);
			intent.putExtra("MSG", PlayerMSG.STOP_MSG);
			startService(intent);
		}

	}

	@Override//该周期内注册广播接收器
	protected void onResume() {
		super.onResume();
		registerReceiver(receiver, getFilter());
		
	}
	
	@Override//该周期内解除广播接收器
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
		
	}
	//	LrcBroadcastReceiver用来接收playerService发来的广播，并将歌词信息设置到TextView
	class LrcBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
				String lrcMessage = intent.getStringExtra("lrcMessage");
				lrcTextView.setText(lrcMessage);

		}
		
	}
	//得到IntentFilter的方法
	private IntentFilter getFilter(){
		if( filter == null) {
			filter = new IntentFilter();
			filter.addAction(AppConstant.LRC_MESSAGE_ACTION);	
		}
		return filter;
	}
	
}
