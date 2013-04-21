package rober.mp3player;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import rober.download.HttpDownloader;
import rober.model.Mp3Info;
import rober.mp3player.service.DownloadService;
import rober.xml.Mp3ListContentHandler;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class RemoteMp3List extends ListActivity {
	
	private static final String DEBUG_TAG = "RemoteMp3List";
	private static final int UPDATE = 1;
	private static final int ABOUT = 2;
	private static final String RESOURCES_URL_STR = "http://192.168.0.101:8888/mp3/resources.xml";
	private List<Mp3Info> mp3Infos = null;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remote_mp3_list);
        updateListView();//下次改动为从本地读取resources文件
        //new UpdateTask().execute();//既要下载文件，又要解析，属于耗时操作，此处可考虑用异步任务
    }
    
	private class UpdateTask extends AsyncTask<Void,Void,Void> {

		@Override
		protected Void doInBackground(Void... params) {
			updateListView();
			return null;
		}	
	}
	
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
    	menu.add(0, UPDATE, 1, R.string.mp3list_update);
    	menu.add(0, ABOUT, 2, R.string.mp3list_about);
		return super.onCreateOptionsMenu(menu);
	} 
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	int itemId = item.getItemId();
    	switch (itemId) {
    		case UPDATE:
    			updateListView();
    			break;
    		case ABOUT:
    			Toast.makeText(RemoteMp3List.this, "Thank you for using it.", Toast.LENGTH_LONG).show();
    			break;
    	}  	
    	return super.onOptionsItemSelected(item);
//    	if(item.getItemId() == UPDATE) {
//    		//update
//    		//System.out.println("ItemId------>" + item.getItemId());
//    		updateListView();
//    		
//		}else if (item.getItemId() == ABOUT) {
//    		//about
//    	}
//    	//System.out.println("ItemId------>" + item.getItemId());

	}
    
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Mp3Info mp3Info = mp3Infos.get(position);
		//System.out.println("mp3Info---->" + mp3Info);调用Mp3Info对象的toString()方法
		Intent intent = new Intent();
		intent.putExtra("mp3Info", mp3Info);
		intent.setClass(this, DownloadService.class);
		startService(intent);
		
		super.onListItemClick(l, v, position, id);
	}
	
    private SimpleAdapter buildSimpleAdapter(List<Mp3Info> infos){
    	//生成一个List对象，并按照SimpleAdapter的标准将Mp3Info当中的数据添加到List当中去
    	List<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
		for (Iterator iterator = infos.iterator(); iterator.hasNext();) {
			Mp3Info mp3Info = (Mp3Info) iterator.next();
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("mp3_name", mp3Info.getMp3Name());
			map.put("mp3_size", mp3Info.getMp3Size());
			list.add(map);
		}
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, list, 
				R.layout.mp3info_item, new String[]{"mp3_name","mp3_size"}, 
				new int[]{R.id.mp3_name,R.id.mp3_size});
    	return simpleAdapter;
    }
    
	private void updateListView() {
		// 下载XML文件内容
		String xml = downloadXML(RESOURCES_URL_STR);
		//System.out.println("xml---->" + xml);
		//解析XML内容，将内容放置到Mp3Info对象，并将对象放入List
		mp3Infos = parse(xml);
		//创建SimpleAdapter对象
		SimpleAdapter simpleAdapter = buildSimpleAdapter(mp3Infos);
		//将simpleAdapter设置到ListActivity当中
		setListAdapter(simpleAdapter);
	}

	private String downloadXML(String urlStr) {
		HttpDownloader httpDownloader = new HttpDownloader();
		return httpDownloader.download(urlStr);
	}
	
	private List<Mp3Info> parse(String xmlStr) {
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		List<Mp3Info> infos = new ArrayList<Mp3Info>();
		try {
			XMLReader xmlReader = saxParserFactory.newSAXParser().getXMLReader();
			Mp3ListContentHandler mp3ListContentHandler = new Mp3ListContentHandler(infos);
			xmlReader.setContentHandler(mp3ListContentHandler);//为xmlReader设置内容处理器
			xmlReader.parse(new InputSource(new StringReader(xmlStr)));//开始解析，死记硬背
			//测试用
			Iterator<Mp3Info> it = infos.iterator();
			while(it.hasNext()) {
				Mp3Info mp3Info = it.next();
				System.out.println(mp3Info);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return infos;
	}
	
}