package rober.mp3player.service;

import java.io.File;

import rober.download.HttpDownloader;
import rober.model.Mp3Info;
import rober.mp3player.AppConstant;
import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DownloadService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Mp3Info mp3Info = (Mp3Info) intent.getSerializableExtra("mp3Info");
		//System.out.println("service---->" + mp3Info);
		DownloadThread downloadThread = new DownloadThread(mp3Info);
		new Thread(downloadThread).start();
		return super.onStartCommand(intent, flags, startId);
	}
	
	class DownloadThread implements Runnable {
		private Mp3Info mp3Info = null;
		public DownloadThread(Mp3Info mp3Info) {
			super();
			this.mp3Info = mp3Info;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String mp3Url = AppConstant.URL.BASE_URL + mp3Info.getMp3Name();
			String lrcUrl = AppConstant.URL.BASE_URL + mp3Info.getLrcName();
			HttpDownloader httpDownloader = new HttpDownloader();
			int mp3Result = httpDownloader.downFile(mp3Url, "mp3/", mp3Info.getMp3Name());
			int lrcResult = httpDownloader.downFile(lrcUrl, "mp3/", mp3Info.getLrcName());
			String resultMessage = null;
			if(mp3Result == -1) {
				resultMessage = "MP3 download fail";
			}
			else if(mp3Result == 0) {
				resultMessage = "MP3 download success";
			}
			else if(mp3Result == 1) {
				resultMessage = "MP3 exists";
			}
			//use Notification to tell Client the resultMessage
			NotificationManager nm = (NotificationManager) DownloadService.this.getSystemService(NOTIFICATION_SERVICE);
			int icon = R.drawable.stat_sys_download_done;
			String tickerText = resultMessage;
			long when = System.currentTimeMillis();
			Notification n = new Notification(icon, tickerText, when);
			//nm.notify(1, n);
		}
		
	}

}
