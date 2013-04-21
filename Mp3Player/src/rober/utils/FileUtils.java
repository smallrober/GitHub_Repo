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
		// �õ���ǰ�ⲿ�洢�豸��Ŀ¼
		SDCardRoot = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ File.separator;// windows��'\'��unix��'/'
	}

	/**
	 * ��SD���ϴ����ļ�
	 * 
	 * @throws IOException
	 */
	public File createFileInSDCard(String fileName, String dir)
			throws IOException {
		File file = new File(SDCardRoot + dir + File.separator + fileName);
		//System.out.println("file---->" + file);//����
		file.createNewFile();
		return file;
	}

	/**
	 * ��SD���ϴ���Ŀ¼
	 * 
	 * @param dirName
	 */
	public File createSDDir(String dir) {
		File dirFile = new File(SDCardRoot + dir + File.separator);
		//this method does not throw IOException on failure. Callers must check the return value.
		if(dirFile.mkdirs()) {
			System.out.println("�����ɹ���");
			return dirFile;
		} else {
			System.out.println("����ʧ�ܣ������ļ����Ѵ��ڣ�");
			return null;
		}
		
	}

	/**
	 * �ж�SD���ϵ��ļ����Ƿ����
	 */
	public  boolean isFileExist(String fileName, String path) {
		File file = new File(SDCardRoot + path + File.separator + fileName);
		return file.exists();
	}

	/**
	 * ��һ��InputStream���������д�뵽SD����
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
			}//����input��read������InputStream�����ֽ����ݶ��뵽buffer������cpu�ķ�����ܶ����βŶ��꣬
			 //������Ҫ��whileѭ������һ��д��һ�Σ�ֱ������Ϊֹ��
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
	 * ��ȡĿ¼�е�Mp3�ļ������ֺʹ�С
	 */
	public List<Mp3Info> getMp3Files(String path) {
		List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
		File file = new File(SDCardRoot + path);//�ļ���ORĿ¼
		//Log.i("path", file.toString());
		if(!file.exists()) {
			Log.v("getMp3Files", "�ļ��в�����");
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
	/*���ڹ����ļ�����*/
	class Mp3Filter implements FilenameFilter {

		@Override
		public boolean accept(File dir, String filename) {
			return filename.endsWith(".mp3");
		}
		
	}
}