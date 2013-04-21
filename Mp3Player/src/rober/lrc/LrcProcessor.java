package rober.lrc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author rober
 * 处理歌词文件
 */
public class LrcProcessor {
	
	public ArrayList<Queue> process(InputStream inputStream) {
		
		Queue<Long> timeMills = new LinkedList<Long>();
		Queue<String> messages = new LinkedList<String>();
		ArrayList<Queue> queues = new ArrayList<Queue>();
		
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(inputStream,"GBK"));
			String temp = null;
			int i = 0;//??
			
			Pattern p = Pattern.compile("\\[([^\\]]+)\\]");
			String result = null;
			Boolean b = true;//??
			while((temp = br.readLine()) != null) {
				i++;
				Matcher m = p.matcher(temp);
				if(m.find()) {
					if(result != null) {
						messages.add(result);
					}
					String timeStr = m.group();
					Long timeMill = time2Long(timeStr.substring(1, timeStr.length()-1));
					if(b) {
						timeMills.offer(timeMill);
					}
					String msg = temp.substring(10);
					if(msg == null || msg.trim().equals("")){
						msg = "-----";
					}
					result = "" + msg + "\n";
			
				} else {
					result = result + temp + "\n";
				}
				
			}
			messages.add(result);
			
			queues.add(timeMills);
			queues.add(messages);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return queues;
	}

	/**
	 * 将时间字符串转换成毫秒
	 * @param timeStr
	 * @return
	 */
	private Long time2Long(String timeStr) {
		//System.out.println("time2Long--->" + timeStr);
		String[] s = timeStr.split(":");
		//System.out.println(s[1]);
		String[] ss = s[1].split("\\.");//注意特殊字符处理
		int min = Integer.parseInt(s[0]);
		//System.out.println("min==" + min);
		int sec = Integer.parseInt(ss[0]);
		//System.out.println("sec==" + sec);
		int mill = Integer.parseInt(ss[1]);
		//System.out.println("mill==" + mill);
		long timeMill = min * 60 * 1000 + sec * 1000 + mill * 10L;
		//System.out.println("timeMill==" + timeMill);
		return timeMill;
	}
}
