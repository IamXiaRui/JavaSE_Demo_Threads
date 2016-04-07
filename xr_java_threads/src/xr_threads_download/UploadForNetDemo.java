package xr_threads_download;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @ClassName: UploadForNetDemo
 * @Description: 多线程下载文件
 * @author iamxiarui@foxmail.com
 * @date 2016年4月6日 下午10:27:47
 * 
 */
public class UploadForNetDemo {

	// 文件下载路径
	private static String DOWN_PATH = "http://172.25.10.172:8080/Web_UploadExeDemo/testApp.exe";

	// 线程的数量
	private static int THREAD_COUNT = 3;

	// 每个线程需要下载文件的大小
	private static int BLOCK_SIZE = 0;

	public static void main(String[] args) {

		try {
			URL url = new URL(DOWN_PATH);
			HttpURLConnection openConnection = (HttpURLConnection) url.openConnection();
			openConnection.setRequestMethod("GET");
			openConnection.setConnectTimeout(10 * 1000);

			int responseCode = openConnection.getResponseCode();

			if (responseCode == 200) {
				// 得到文件的长度
				int fileLength = openConnection.getContentLength();
				RandomAccessFile randomAccessFile = new RandomAccessFile(new File("E:\\AllTestFile\\testApp.exe"),
						"rw");
				// 给下载的文件预留空间
				randomAccessFile.setLength(fileLength);

				// 每个线程所需下载的大小为 文件大小 除以 线程数量
				BLOCK_SIZE = fileLength / THREAD_COUNT;

				// 循环开启线程
				for (int THREAD_ID = 0; THREAD_ID < THREAD_COUNT; THREAD_ID++) {
					// 线程下载起始位置
					int START_INDEX = THREAD_ID * BLOCK_SIZE;
					// 线程下载结束位置
					int END_INDEX = (THREAD_ID + 1) * BLOCK_SIZE - 1;

					// 如果是最后一个线程
					if (THREAD_ID == THREAD_COUNT - 1) {
						// 结束位置为文件大小-1
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

	/**
	 * @ClassName: UploadThread
	 * @Description:多线程下载
	 * @author iamxiarui@foxmail.com
	 * @date 2016年4月6日 下午10:30:54
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

				System.out.println("线程： " + threadId + ",开始位置： " + startIndex + ",结束位置：" + endIndex);

				// 文件头信息 必须的格式
				openConnection.setRequestProperty("Range", "bytes:" + startIndex + "-" + endIndex);
				int responseCode = openConnection.getResponseCode();
				// 200 为全部资源请求信息 206 为部分资源请求信息
				if (responseCode == 206) {
					InputStream inputStream = openConnection.getInputStream();

					RandomAccessFile randomAccessFile = new RandomAccessFile(new File("E:\\AllTestFile\\testApp.exe"),
							"rw");
					// 从文件的偏移量开始存储
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
