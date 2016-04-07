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
 * @Description: ���̶߳ϵ������ļ�
 * @author iamxiarui@foxmail.com
 * @date 2016��4��6�� ����10:27:47
 * 
 */
public class UploadPauseDemo {

	// �ļ�����·��
	private static String DOWN_PATH = "http://172.25.10.172:8080/Web_UploadDemo/testApp.exe";

	// �̵߳�����
	private static int THREAD_COUNT = 3;

	// ÿ���߳���Ҫ�����ļ��Ĵ�С
	private static int BLOCK_SIZE = 0;

	// ��ǰ���е��߳�����
	private static int RUN_COUNT = 0;

	public static void main(String[] args) {

		try {
			URL url = new URL(DOWN_PATH);
			HttpURLConnection openConnection = (HttpURLConnection) url.openConnection();
			openConnection.setRequestMethod("GET");
			openConnection.setConnectTimeout(10 * 1000);

			if (openConnection.getResponseCode() == 200) {
				// �õ��ļ��ĳ���
				int fileLength = openConnection.getContentLength();
				RandomAccessFile randomAccessFile = new RandomAccessFile(new File("E:\\AllTestFile\\testApp.exe"),
						"rw");
				// �����ص��ļ�Ԥ���ռ�
				randomAccessFile.setLength(fileLength);

				// ÿ���߳��������صĴ�СΪ �ļ���С ���� �߳�����
				BLOCK_SIZE = fileLength / THREAD_COUNT;

				// ѭ�������߳�
				for (int threadId = 0; threadId < THREAD_COUNT; threadId++) {
					// �߳�������ʼλ��
					int START_INDEX = threadId * BLOCK_SIZE;
					// �߳����ؽ���λ��
					int END_INDEX = (threadId + 1) * BLOCK_SIZE - 1;

					// ��������һ���߳�
					if (threadId == THREAD_COUNT - 1) {
						// ����λ��Ϊ�ļ���С-1
						END_INDEX = fileLength - 1;
					}

					// �����߳�����
					new UploadThread(threadId, START_INDEX, END_INDEX).start();
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @ClassName: UploadThread
	 * @Description:���̶߳ϵ�����
	 * @author iamxiarui@foxmail.com
	 * @date 2016��4��6�� ����10:30:54
	 * 
	 */
	public static class UploadThread extends Thread {

		private int threadId;
		private int startIndex;
		private int endIndex;
		// �ϴ��ļ����ص�λ��
		private int lastPostion;

		public UploadThread(int threadId, int startIndex, int endIndex) {
			this.threadId = threadId;
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}

		@Override
		public void run() {

			// ͬ������� ֻҪ���� �߳�����һ
			synchronized (UploadThread.class) {
				RUN_COUNT = RUN_COUNT + 1;
			}

			try {
				URL url = new URL(DOWN_PATH);

				HttpURLConnection openConnection = (HttpURLConnection) url.openConnection();
				openConnection.setRequestMethod("GET");
				openConnection.setConnectTimeout(10 * 1000);

				System.out.println("���������أ�   �̣߳� " + threadId + ",��ʼλ�ã� " + startIndex + ",����λ�ã�" + endIndex);

				// ��ȡ��һ�����ؽ�����λ�ã���������������ǰλ������ ��������ӿ�ʼ����
				File fileLast = new File("E:\\AllTestFile\\testApp" + threadId + ".txt");
				if (fileLast.exists()) {
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(new FileInputStream(fileLast)));
					// ��ȡ�ļ��ϴ�������ְ
					String lastPostion_str = bufferedReader.readLine();
					lastPostion = Integer.parseInt(lastPostion_str);
					openConnection.setRequestProperty("Range", "bytes:" + lastPostion + "-" + endIndex);
					System.out.println("ʵ�����أ�  �̣߳�" + threadId + "����ʼλ�ã�" + lastPostion + ";����λ��:" + endIndex);
					bufferedReader.close();
				} else {
					// ������״����� ��ô �ϴ�����λ�� ���ǿ�ʼλ��
					lastPostion = startIndex;
					openConnection.setRequestProperty("Range", "bytes:" + lastPostion + "-" + endIndex);
					System.out.println("ʵ�����أ�  �̣߳�" + threadId + "����ʼλ�ã�" + lastPostion + ";����λ��:" + endIndex);
				}

				// 200 Ϊȫ����Դ������Ϣ 206 Ϊ������Դ������Ϣ
				if (openConnection.getResponseCode() == 206) {
					InputStream inputStream = openConnection.getInputStream();
					RandomAccessFile randomAccessFile = new RandomAccessFile(new File("E:\\AllTestFile\\testApp.exe"),
							"rw");
					// ���ļ���ƫ������ʼ�洢
					randomAccessFile.seek(lastPostion);

					byte[] buf = new byte[1024];
					int length = -1;
					int totalFile = 0;
					while ((length = inputStream.read(buf)) != -1) {
						randomAccessFile.write(buf, 0, length);

						totalFile = totalFile + length;
						// ȥ���浱ǰ�߳����ص�λ�ã����浽�ļ���
						int currentThreadPostion = lastPostion + totalFile;// �������ǰ�̱߳������ص�λ��
						// ��������ļ����浱ǰ�߳����ص�λ��
						File filePause = new File("E:\\AllTestFile\\testApp" + threadId + ".txt");
						RandomAccessFile accessfile = new RandomAccessFile(filePause, "rwd");
						accessfile.write(String.valueOf(currentThreadPostion).getBytes());
						accessfile.close();
					}

					inputStream.close();
					randomAccessFile.close();

					System.out.println("�̣߳� " + threadId + ",�������");

					// ͬ������� ����߳�������� ɾ������Ԥ�����ļ�
					synchronized (UploadThread.class) {
						RUN_COUNT = RUN_COUNT - 1;
						if (RUN_COUNT == 0) {
							System.out.println("�����߳��������");
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
