package xr_threads_download;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @ClassName: UploadForNetDemo
 * @Description: 多线程断点下载文件
 * @author iamxiarui@foxmail.com
 * @date 2016年4月6日 下午10:27:47
 * 
 */
public class UploadPauseDemo {

	// 文件下载路径
	private static String DOWN_PATH = "http://172.25.10.172:8080/Web_UploadDemo/testApp.exe";

	// 线程的数量
	private static int THREAD_COUNT = 3;

	// 每个线程需要下载文件的大小
	private static int BLOCK_SIZE = 0;

	// 当前运行的线程数量
	private static int RUN_COUNT = 0;

	public static void main(String[] args) {

		try {
			URL url = new URL(DOWN_PATH);
			HttpURLConnection openConnection = (HttpURLConnection) url.openConnection();
			openConnection.setRequestMethod("GET");
			openConnection.setConnectTimeout(10 * 1000);

			if (openConnection.getResponseCode() == 200) {
				// 得到文件的长度
				int fileLength = openConnection.getContentLength();
				RandomAccessFile randomAccessFile = new RandomAccessFile(new File("E:\\AllTestFile\\testApp.exe"),
						"rw");
				// 给下载的文件预留空间
				randomAccessFile.setLength(fileLength);

				// 每个线程所需下载的大小为 文件大小 除以 线程数量
				BLOCK_SIZE = fileLength / THREAD_COUNT;

				// 循环开启线程
				for (int threadId = 0; threadId < THREAD_COUNT; threadId++) {
					// 线程下载起始位置
					int START_INDEX = threadId * BLOCK_SIZE;
					// 线程下载结束位置
					int END_INDEX = (threadId + 1) * BLOCK_SIZE - 1;

					// 如果是最后一个线程
					if (threadId == THREAD_COUNT - 1) {
						// 结束位置为文件大小-1
						END_INDEX = fileLength - 1;
					}

					// 开启线程下载
					new UploadThread(threadId, START_INDEX, END_INDEX).start();
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @ClassName: UploadThread
	 * @Description:多线程断点下载
	 * @author iamxiarui@foxmail.com
	 * @date 2016年4月6日 下午10:30:54
	 * 
	 */
	public static class UploadThread extends Thread {

		private int threadId;
		private int startIndex;
		private int endIndex;
		// 上次文件下载的位置
		private int lastPostion;

		public UploadThread(int threadId, int startIndex, int endIndex) {
			this.threadId = threadId;
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}

		@Override
		public void run() {

			// 同步代码块 只要进入 线程数加一
			synchronized (UploadThread.class) {
				RUN_COUNT = RUN_COUNT + 1;
			}

			try {
				URL url = new URL(DOWN_PATH);

				HttpURLConnection openConnection = (HttpURLConnection) url.openConnection();
				openConnection.setRequestMethod("GET");
				openConnection.setConnectTimeout(10 * 1000);

				System.out.println("理论上下载：   线程： " + threadId + ",开始位置： " + startIndex + ",结束位置：" + endIndex);

				// 读取上一次下载结束的位置，如果存在则继续当前位置下载 不存在则从开始下载
				File fileLast = new File("E:\\AllTestFile\\testApp" + threadId + ".txt");
				if (fileLast.exists()) {
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(new FileInputStream(fileLast)));
					// 读取文件上次下载文职
					String lastPostion_str = bufferedReader.readLine();
					lastPostion = Integer.parseInt(lastPostion_str);
					openConnection.setRequestProperty("Range", "bytes:" + lastPostion + "-" + endIndex);
					System.out.println("实际下载：  线程：" + threadId + "，开始位置：" + lastPostion + ";结束位置:" + endIndex);
					bufferedReader.close();
				} else {
					// 如果是首次下载 那么 上次下载位置 就是开始位置
					lastPostion = startIndex;
					openConnection.setRequestProperty("Range", "bytes:" + lastPostion + "-" + endIndex);
					System.out.println("实际下载：  线程：" + threadId + "，开始位置：" + lastPostion + ";结束位置:" + endIndex);
				}

				// 200 为全部资源请求信息 206 为部分资源请求信息
				if (openConnection.getResponseCode() == 206) {
					InputStream inputStream = openConnection.getInputStream();
					RandomAccessFile randomAccessFile = new RandomAccessFile(new File("E:\\AllTestFile\\testApp.exe"),
							"rw");
					// 从文件的偏移量开始存储
					randomAccessFile.seek(lastPostion);

					byte[] buf = new byte[1024];
					int length = -1;
					int totalFile = 0;
					while ((length = inputStream.read(buf)) != -1) {
						randomAccessFile.write(buf, 0, length);

						totalFile = totalFile + length;
						// 去保存当前线程下载的位置，保存到文件中
						int currentThreadPostion = lastPostion + totalFile;// 计算出当前线程本次下载的位置
						// 创建随机文件保存当前线程下载的位置
						File filePause = new File("E:\\AllTestFile\\testApp" + threadId + ".txt");
						RandomAccessFile accessfile = new RandomAccessFile(filePause, "rwd");
						accessfile.write(String.valueOf(currentThreadPostion).getBytes());
						accessfile.close();
					}

					inputStream.close();
					randomAccessFile.close();

					System.out.println("线程： " + threadId + ",下载完毕");

					// 同步代码块 如果线程下载完毕 删除所有预下载文件
					synchronized (UploadThread.class) {
						RUN_COUNT = RUN_COUNT - 1;
						if (RUN_COUNT == 0) {
							System.out.println("所有线程下载完成");
							for (int i = 0; i < THREAD_COUNT; i++) {
								File fileDelete = new File("E:\\AllTestFile\\testApp" + i + ".txt");
								fileDelete.delete();
							}
						}

					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			super.run();
		}

	}

}
