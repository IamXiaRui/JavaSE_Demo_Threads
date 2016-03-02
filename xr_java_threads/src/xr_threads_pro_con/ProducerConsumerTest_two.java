package xr_threads_pro_con;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*������-����������
 * 
 * JDK1.5 ���߳̽������������
 * 
 * 1����ͬ��Synchronized�滻����ʽ��Lock����������lock()������unlock()��
 * 2����Object�е�wait��notify��notifyAll�滻��Condition�����await��signal��signalAll��
 * 3��ͬһ��Lock������Ӷ��Condition����newCondition������
 * */

public class ProducerConsumerTest_two {

	public static void main(String[] args) {
		Things_two th = new Things_two();

		new Thread(new Producer_two(th)).start();
		new Thread(new Producer_two(th)).start();
		new Thread(new Consumer_two(th)).start();
		new Thread(new Consumer_two(th)).start();

	}

}

class Things_two {
	private String name;
	private int num = 1;
	private boolean flag = false;

	// ����һ��Lock����
	private Lock lock = new ReentrantLock();

	// ͨ��Lock �󶨲�ͬ��Condition����
	private Condition condition_pro = lock.newCondition();
	private Condition condition_con = lock.newCondition();

	public void pro_two(String name) throws InterruptedException {
		// ����
		lock.lock();
		try {
			// whileѭ������������ǣ�һ���Ѿ��жϱ�־λ���Ѿ����ڵȴ�״̬���������̱߳����ѣ�����û���ٴ��жϱ�־λ���Ӷ���ɰ�ȫ���⡣
			while (flag)
				// �������̵߳ȴ�
				condition_pro.await();
			this.name = name + "------" + num++;
			System.out.println(Thread.currentThread().getName() + "---������---" + this.name);
			flag = true;
			// �����������߳�
			condition_con.signal();
		} finally {
			// ����ִ�н��� ���Է���finally��
			lock.unlock();
		}

	}

	public void con_two() throws InterruptedException {
		// ����
		lock.lock();
		try {
			// whileѭ������������ǣ�һ���Ѿ��жϱ�־λ���Ѿ����ڵȴ�״̬���������̱߳����ѣ�����û���ٴ��жϱ�־λ���Ӷ���ɰ�ȫ���⡣
			while (!flag)
				// �������̵߳ȴ�
				condition_con.await();
			System.out.println(Thread.currentThread().getName() + "-----������-----" + this.name);
			flag = false;
			// �����������߳�
			condition_pro.signal();
		} finally {
			// ����ִ�н��� ���Է���finally��
			lock.unlock();
		}
	}
}

// ������
class Producer_two implements Runnable {

	private Things_two th;

	Producer_two(Things_two th) {
		this.th = th;
	}

	@Override
	public void run() {
		while (true) {
			try {
				th.pro_two("--��Ʒ--");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}

// ������
class Consumer_two implements Runnable {

	private Things_two th;

	Consumer_two(Things_two th) {
		this.th = th;
	}

	@Override
	public void run() {
		while (true) {
			try {
				th.con_two();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
