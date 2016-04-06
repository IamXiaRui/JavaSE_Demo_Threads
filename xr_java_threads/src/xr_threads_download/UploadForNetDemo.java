package xr_threads_download;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UploadForNetDemo {

	private static String DOWN_PATH = "http://172.25.10.172:8080/Web_UploadDemo/testApp.exe";

	private static int THREAD_COUNT = 3;

	private static int BLOCK_SIZE = 0;

	public static void main(String[] args) {

		try {
			URL url = new URL(DOWN_PATH);

			HttpURLConnection openConnection = (HttpURLConnection) url.openConnection();

			openConnection.setRequestMethod("GET");
			openConnection.setConnectTimeout(10 * 1000);

			int responseCode = openConnection.getResponseCode();

			if (responseCode == 200) {
				int fileLength = openConnection.getContentLength();
				RandomAccessFile randomAccessFile = new RandomAccessFile(new File("E:\\AllTestFile\\testApp.exe"),
						"rw");
				randomAccessFile.setLength(fileLength);

				BLOCK_SIZE = fileLength / THREAD_COUNT;

				for (int THREAD_ID = 0; THREAD_ID < THREAD_COUNT; THREAD_ID++) {
					int START_INDEX = THREAD_ID * BLOCK_SIZE;
					int END_INDEX = (THREAD_ID + 1) * BLOCK_SIZE - 1;

					if (THREAD_ID == THREAD_COUNT - 1) {
						END_INDEX = fileLength - 1;
					}

					// 开启线程下载
					new UploadThread(THREAD_ID, START_INDEX, END_INDEX).start();
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

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

			URL url;
			try {
				url = new URL(DOWN_PATH);
				HttpURLConnection openConnection = (HttpURLConnection) url.openConnection();
				openConnection.setRequestMethod("GET");
				openConnection.setConnectTimeout(10 * 1000);

				System.out.println("线程： " + threadId + ",开始位置： " + startIndex + ",结束位置：" + endIndex);

				openConnection.setRequestProperty("Range", "bytes:" + startIndex + "-" + endIndex);
				int responseCode = openConnection.getResponseCode();
				if (responseCode == 206) {
					InputStream inputStream = openConnection.getInputStream();

					RandomAccessFile randomAccessFile = new RandomAccessFile(new File("E:\\AllTestFile\\testApp.exe"),
							"rw");
					randomAccessFile.seek(startIndex);

					byte[] buf = new byte[1024];
					int length = -1;
					while ((length = inputStream.read(buf)) != -1) {
						randomAccessFile.write(buf, 0, length);
					}

					inputStream.close();
					randomAccessFile.close();

					System.out.println("线程： " + threadId + ",下载完毕");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			super.run();
		}

	}

}
