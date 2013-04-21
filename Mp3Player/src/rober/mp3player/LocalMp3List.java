package rober.mp3player;

import rober.model.Mp3Info;
import rober.utils.FileUtils;

public class LocalMp3List extends ListActivity {
	
	private List<Mp3Info> mp3Infos = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_mp3_list);
		
	}
	@Override
	protected void onResume() {//只要当显示该Activity即刻刷新数据

		FileUtils fileUtils = new FileUtils();
		mp3Infos = fileUtils.getMp3Files("mp3/");
		if(null == mp3Infos) 
		{
			Toast.makeText(this, "无mp3文件夹或该文件为空", Toast.LENGTH_SHORT).show();
		} 
		else 
		{
			List<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
			for (Iterator<Mp3Info> iterator = mp3Infos.iterator(); iterator.hasNext();) 
			{
				Mp3Info mp3Info = iterator.next();
				HashMap<String,String> map = new HashMap<String,String>();
				map.put("mp3_name", mp3Info.getMp3Name());
				map.put("mp3_size", mp3Info.getMp3Size());
				list.add(map);
			}
			String[] from = {"mp3_name", "mp3_size"};
			int[] to = {R.id.mp3_name, R.id.mp3_size};
			SimpleAdapter simpleAdapter = new SimpleAdapter(this, list, R.layout.mp3info_item, from, to);
			setListAdapter(simpleAdapter);
		}
		
		super.onResume();
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if(null != mp3Infos) 
		{
			Mp3Info mp3Info = mp3Infos.get(position);
			Intent intent = new Intent();
			intent.putExtra("mp3Info", mp3Info);
			intent.setClass(this, PlayerActivity.class);
			startActivity(intent);
		}
		super.onListItemClick(l, v, position, id);
	}
	
}
