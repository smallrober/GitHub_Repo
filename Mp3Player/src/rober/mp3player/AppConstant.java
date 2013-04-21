package rober.mp3player;

public interface AppConstant {
	public class PlayerMSG {
		public static final int PLAY_MSG = 1;
		public static final int PAUSE_MSG = 2;
		public static final int STOP_MSG = 0;
	}
	
	public class URL {
		public static final String BASE_URL = "http://192.168.0.101:8888/mp3/";
	}
	
	public static final String LRC_MESSAGE_ACTION = "rober.mp3player.lrcmessage.action";
}
