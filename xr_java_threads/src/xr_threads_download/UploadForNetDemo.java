package xr_threads_download;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @ClassName: UploadForNetDemo
 * @Description: ���߳������ļ�
 * @author iamxiarui@foxmail.com
 * @date 2016��4��6�� ����10:27:47
 * 
 */
public class UploadForNetDemo {

	// �ļ�����·��
	private static String DOWN_PATH = "http://172.25.10.172:8080/Web_UploadExeDemo/testApp.exe";

	// �̵߳�����
	private static int THREAD_COUNT = 3;

	// ÿ���߳���Ҫ�����ļ��Ĵ�С
	private static int BLOCK_SIZE = 0;

	public static void main(String[] args) {

		try {
			URL url = new URL(DOWN_PATH);
			HttpURLConnection openConnection = (HttpURLConnection) url.openConnection();
			openConnection.setRequestMethod("GET");
			openConnection.setConnectTimeout(10 * 1000);

			int responseCode = openConnection.getResponseCode();

			if (responseCode == 200) {
				// �õ��ļ��ĳ���
				int fileLength = openConnection.getContentLength();
				RandomAccessFile randomAccessFile = new RandomAccessFile(new File("E:\\AllTestFile\\testApp.exe"),
						"rw");
				// �����ص��ļ�Ԥ���ռ�
				randomAccessFile.setLength(fileLength);

				// ÿ���߳��������صĴ�СΪ �ļ���С ���� �߳�����
				BLOCK_SIZE = fileLength / THREAD_COUNT;

				// ѭ�������߳�
				for (int THREAD_ID = 0; THREAD_ID < THREAD_COUNT; THREAD_ID++) {
					// �߳�������ʼλ��
					int START_INDEX = THREAD_ID * BLOCK_SIZE;
					// �߳����ؽ���λ��
					int END_INDEX = (THREAD_ID + 1) * BLOCK_SIZE - 1;

					// ��������һ���߳�
					if (THREAD_ID == THREAD_COUNT - 1) {
						// ����λ��Ϊ�ļ���С-1
						END_INDEX = fileLength - 1;
					}

					// �����߳�����
					new UploadThread(THREAD_ID, START_INDEX, END_INDEX).start();
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @ClassName: UploadThread
	 * @Description:���߳�����
	 * @author iamxiarui@foxmail.com
	 * @date 2016��4��6�� ����10:30:54
	 * 
	 */
	public static class UploadThread extends Thread {

		private int threadId;
		private int startIndex;
		private int endIndex;

		public UploadThread(int threadId, int startIndex, int endIndex) {
			this.threadId = threadId;
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}

		@Override
		public void run() {

			try {
				URL url = new URL(DOWN_PATH);
				HttpURLConnection openConnection = (HttpURLConnection) url.openConnection();
				openConnection.setRequestMethod("GET");
				openConnection.setConnectTimeout(10 * 1000);

				System.out.println("�̣߳� " + threadId + ",��ʼλ�ã� " + startIndex + ",����λ�ã�" + endIndex);

				// �ļ�ͷ��Ϣ ����ĸ�ʽ
				openConnection.setRequestProperty("Range", "bytes:" + startIndex + "-" + endIndex);
				int responseCode = openConnection.getResponseCode();
				// 200 Ϊȫ����Դ������Ϣ 206 Ϊ������Դ������Ϣ
				if (responseCode == 206) {
					InputStream inputStream = openConnection.getInputStream();

					RandomAccessFile randomAccessFile = new RandomAccessFile(new File("E:\\AllTestFile\\testApp.exe"),
							"rw");
					// ���ļ���ƫ������ʼ�洢
					randomAccessFile.seek(startIndex);

					byte[] buf = new byte[1024];
					int length = -1;
					while ((length = inputStream.read(buf)) != -1) {
						randomAccessFile.write(buf, 0, length);
					}

					inputStream.close();
					randomAccessFile.close();

					System.out.println("�̣߳� " + threadId + ",�������");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			super.run();
		}

	}

}
