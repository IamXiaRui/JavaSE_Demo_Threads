package xr_threads_pro_con;

/*������-����������
 * 
 * 1���жϱ�־λ�������whileѭ���У����ǻ����ȫ���ȴ�״̬��
 * 2�����Ի��ѱ��뻽��ȫ���̣߳�notifyAll()��
 * 
 * */

public class ProducerConsumerTest_one {

	public static void main(String[] args) {
		Things_one th = new Things_one();

		new Thread(new Producer_one(th)).start();
		new Thread(new Producer_one(th)).start();
		new Thread(new Consumer_one(th)).start();
		new Thread(new Consumer_one(th)).start();

	}

}

class Things_one {
	private String name;
	private int num = 1;
	private boolean flag = false;

	// ͬ������
	public synchronized void pro_one(String name) {
		// whileѭ������������ǣ�һ���Ѿ��жϱ�־λ���Ѿ����ڵȴ�״̬���������̱߳����ѣ�����û���ٴ��жϱ�־λ���Ӷ���ɰ�ȫ���⡣
		while (flag)
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		this.name = name + "------" + num++;
		System.out.println(Thread.currentThread().getName() + "---������---" + this.name);
		flag = true;
		// ����ȫ���Ƿ�ֹ�����̶߳����ڵȴ�״̬������������
		this.notifyAll();
	}

	// ͬ������
	public synchronized void con_one() {
		// whileѭ������������ǣ�һ���Ѿ��жϱ�־λ���Ѿ����ڵȴ�״̬���������̱߳����ѣ�����û���ٴ��жϱ�־λ���Ӷ���ɰ�ȫ���⡣
		while (!flag)
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		System.out.println(Thread.currentThread().getName() + "-----������-----" + this.name);
		flag = false;
		// ����ȫ���Ƿ�ֹ�����̶߳����ڵȴ�״̬������������
		this.notifyAll();
	}
}

// ������
class Producer_one implements Runnable {

	private Things_one th;

	Producer_one(Things_one th) {
		this.th = th;
	}

	@Override
	public void run() {
		while (true) {
			th.pro_one("--��Ʒ--");
		}
	}

}

// ������
class Consumer_one implements Runnable {

	private Things_one th;

	Consumer_one(Things_one th) {
		this.th = th;
	}

	@Override
	public void run() {
		while (true) {
			th.con_one();
		}
	}

}
