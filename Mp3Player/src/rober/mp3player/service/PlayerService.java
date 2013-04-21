package rober.mp3player.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Queue;

import rober.lrc.LrcProcessor;
import rober.model.Mp3Info;
import rober.mp3player.AppConstant;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;

public class PlayerService extends Service{

	MediaPlayer mediaPlayer = null;
	private boolean isPlaying = false;
	private boolean isPause = false;
	private boolean isRelease = false;
	private ArrayList<Queue> queues = null;
	private Handler handler = new Handler();
	private UpdateTimeCallBack updateTimeCallBack = null;
	private long begin = 0;
	private long nextTimeMill = 0;
	private long currentTimeMill = 0;
	private long pauseTimeMill = 0;
	private String message = null;
	private long timeMill = 0;
	Queue messages = null;
	Queue timeMills = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Mp3Info mp3Info = (Mp3Info)intent.getSerializableExtra("mp3Info");
		//System.out.println("playerService--->" + mp3Info);
		int MSG = intent.getIntExtra("MSG", 0);
		//System.out.println(MSG + "");
		if(mp3Info != null) {
			switch (MSG) {
			case AppConstant.PlayerMSG.PLAY_MSG:
				play(mp3Info);
				break;
			case AppConstant.PlayerMSG.STOP_MSG:
				stop();
				break;
			case AppConstant.PlayerMSG.PAUSE_MSG:
				pause();
				break;
			}
		} 
		return super.onStartCommand(intent, flags, startId);
	}

	private void pause() {
		//System.out.println("pause--->");
		if(mediaPlayer != null) {
			if(isPlaying) {
				mediaPlayer.pause();
				handler.removeCallbacks(updateTimeCallBack);
				long pauseTimeMill = System.currentTimeMillis();
			} else {
				mediaPlayer.start();
				handler.postDelayed(updateTimeCallBack, 5);
				begin = System.currentTimeMillis() - pauseTimeMill + begin;
			}
			isPlaying = isPlaying ? false : true;
		}
	}

	private void stop() {
		if(mediaPlayer != null) {
			if(isPlaying) {
				if(!isRelease) {
					handler.removeCallbacks(updateTimeCallBack);
					mediaPlayer.stop();
					mediaPlayer.release();
					isRelease = true;
				}
				isPlaying = false;
			} 
		} 
	}

	private void play(Mp3Info mp3Info) {
		if(mediaPlayer == null){
			String path = getMp3Path(mp3Info);
			mediaPlayer = MediaPlayer.create(this, Uri.parse("file://" + path));
			//parse方法返回的是一个Uri类型，通过这个Uri可以访问一个网络上或者是本地的资源
			mediaPlayer.setLooping(false);
			mediaPlayer.start();
			prepareLrc(mp3Info.getLrcName());
			updateTimeCallBack = new UpdateTimeCallBack(queues);
			handler.postDelayed(updateTimeCallBack, 5);
			begin = System.currentTimeMillis();
		} else if(!isPlaying) {
			mediaPlayer.start();
			handler.postDelayed(updateTimeCallBack, 5);
			begin = System.currentTimeMillis() - pauseTimeMill + begin;
		}
		isPlaying = true;
		isRelease = false;	
			
	}
	private void prepareLrc(String lrcName) {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mp3" 
		+ File.separator + lrcName; 
		File file = new File(path);
		//System.out.println("prepareLrc--->" + file);
		if (file.exists()) {
			try {
				InputStream inputStream = new FileInputStream(file);
				LrcProcessor lrcProcessor = new LrcProcessor();
				queues = lrcProcessor.process(inputStream);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public class UpdateTimeCallBack implements Runnable {
		//private ArrayList<Queue> queues = null;
		//构造方法
		public UpdateTimeCallBack(ArrayList<Queue> queues) {
			//this.queues = queues;
			timeMills = queues.get(0);
			messages = queues.get(1);
		}
		@Override
		public void run() {
			//currentTimeMill = mediaPlayer.getCurrentPosition();
			long offset = System.currentTimeMillis() - begin;
			if(currentTimeMill == 0) {
				nextTimeMill = (Long)timeMills.poll();
				message = (String)messages.poll();
			}
			if(offset >= nextTimeMill) {
				Intent intent = new Intent();
				intent.putExtra("lrcMessage", message);
				//System.out.println("----lrc:"+message);
				intent.setAction(AppConstant.LRC_MESSAGE_ACTION);
				sendBroadcast(intent);	
				
				if(!messages.isEmpty() && !timeMills.isEmpty()) {
					message = (String) messages.poll();
					nextTimeMill = (Long) timeMills.poll();
				}
			}
			if(!messages.isEmpty() && !timeMills.isEmpty()){
				currentTimeMill = currentTimeMill + 10;
				handler.postDelayed(updateTimeCallBack, 10);
			}

			


		}
		
	}

	private String getMp3Path(Mp3Info mp3Info) {
		String SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
		String path = SDCardRoot +"mp3" +File.separator + mp3Info.getMp3Name();
		return path;
	}
}
