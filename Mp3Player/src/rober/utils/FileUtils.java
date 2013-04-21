package rober.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import rober.model.Mp3Info;
import android.os.Environment;
import android.util.Log;

public class FileUtils {
	private String SDCardRoot;

	public FileUtils() {
		// 得到当前外部存储设备的目录
		SDCardRoot = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ File.separator;// windows是'\'，unix是'/'
	}

	/**
	 * 在SD卡上创建文件
	 * 
	 * @throws IOException
	 */
	public File createFileInSDCard(String fileName, String dir)
			throws IOException {
		File file = new File(SDCardRoot + dir + File.separator + fileName);
		//System.out.println("file---->" + file);//测试
		file.createNewFile();
		return file;
	}

	/**
	 * 在SD卡上创建目录
	 * 
	 * @param dirName
	 */
	public File createSDDir(String dir) {
		File dirFile = new File(SDCardRoot + dir + File.separator);
		//this method does not throw IOException on failure. Callers must check the return value.
		if(dirFile.mkdirs()) {
			System.out.println("创建成功！");
			return dirFile;
		} else {
			System.out.println("创建失败，或者文件夹已存在！");
			return null;
		}
		
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 */
	public  boolean isFileExist(String fileName, String path) {
		File file = new File(SDCardRoot + path + File.separator + fileName);
		return file.exists();
	}

	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 */
	public File write2SDFromInput(String path, String fileName,
			InputStream input) {

		File file = null;
		OutputStream output = null;
		try {
			createSDDir(path);
			file = createFileInSDCard(fileName, path);
			output = new FileOutputStream(file);
			byte buffer[] = new byte[4 * 1024];
			int count;
			while ((count = input.read(buffer)) != -1) {//Reads bytes from this stream and stores them in the byte array buffer.
				output.write(buffer, 0, count);//Writes count bytes from the byte array buffer starting at position offset to this stream.
			}//先用input的read方法将InputStream里面字节数据读入到buffer，根据cpu的分配可能读数次才读完，
			 //所以需要用while循环，读一段写入一段，直到读完为止。
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * 读取目录中的Mp3文件的名字和大小
	 */
	public List<Mp3Info> getMp3Files(String path) {
		List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
		File file = new File(SDCardRoot + path);//文件夹OR目录
		//Log.i("path", file.toString());
		if(!file.exists()) {
			Log.v("getMp3Files", "文件夹不存在");
			return null;
		}
		/*File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().endsWith("mp3")) {
				Mp3Info mp3Info = new Mp3Info();
				mp3Info.setMp3Name(files[i].getName());
				mp3Info.setMp3Size(files[i].length() + "");
				String temp [] = mp3Info.getMp3Name().split("\\.");
				String eLrcName = temp[0] + ".lrc";
				if(isFileExist(eLrcName, "/mp3")){
					mp3Info.setLrcName(eLrcName);
				}
				mp3Infos.add(mp3Info);
			}
		}*/
		File[] files = file.listFiles(new Mp3Filter());
		for (int i = 0; i < files.length; i++) {
			Mp3Info mp3Info = new Mp3Info();
			mp3Info.setMp3Name(files[i].getName());
			mp3Info.setMp3Size(files[i].length() + "");
			mp3Infos.add(mp3Info);
		}
		return mp3Infos;
	}
	/*用于过滤文件类型*/
	class Mp3Filter implements FilenameFilter {

		@Override
		public boolean accept(File dir, String filename) {
			return filename.endsWith(".mp3");
		}
		
	}
}