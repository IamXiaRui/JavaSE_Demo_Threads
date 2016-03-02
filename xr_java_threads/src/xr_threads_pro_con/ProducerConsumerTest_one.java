package xr_threads_pro_con;

/*生产者-消费者问题
 * 
 * 1、判断标志位必须放在while循环中，但是会造成全部等待状态。
 * 2、所以唤醒必须唤醒全部线程，notifyAll()。
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

	// 同步函数
	public synchronized void pro_one(String name) {
		// while循环解决的问题是：一个已经判断标志位且已经处于等待状态的生产者线程被唤醒，但并没有再次判断标志位，从而造成安全问题。
		while (flag)
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		this.name = name + "------" + num++;
		System.out.println(Thread.currentThread().getName() + "---生产者---" + this.name);
		flag = true;
		// 唤醒全部是防止所有线程都处于等待状态而产生锁死。
		this.notifyAll();
	}

	// 同步函数
	public synchronized void con_one() {
		// while循环解决的问题是：一个已经判断标志位且已经处于等待状态的消费者线程被唤醒，但并没有再次判断标志位，从而造成安全问题。
		while (!flag)
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		System.out.println(Thread.currentThread().getName() + "-----消费者-----" + this.name);
		flag = false;
		// 唤醒全部是防止所有线程都处于等待状态而产生锁死。
		this.notifyAll();
	}
}

// 生产者
class Producer_one implements Runnable {

	private Things_one th;

	Producer_one(Things_one th) {
		this.th = th;
	}

	@Override
	public void run() {
		while (true) {
			th.pro_one("--商品--");
		}
	}

}

// 消费者
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
