package xr_threads_pro_con;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*生产者-消费者问题
 * 
 * JDK1.5 多线程解决方案新特性
 * 
 * 1、将同步Synchronized替换成显式的Lock操作：加锁lock()，解锁unlock()。
 * 2、将Object中的wait、notify、notifyAll替换成Condition对象的await、signal、signalAll。
 * 3、同一个Lock可以添加多个Condition对象，newCondition（）。
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

	// 声明一个Lock对象
	private Lock lock = new ReentrantLock();

	// 通过Lock 绑定不同的Condition对象
	private Condition condition_pro = lock.newCondition();
	private Condition condition_con = lock.newCondition();

	public void pro_two(String name) throws InterruptedException {
		// 加锁
		lock.lock();
		try {
			// while循环解决的问题是：一个已经判断标志位且已经处于等待状态的生产者线程被唤醒，但并没有再次判断标志位，从而造成安全问题。
			while (flag)
				// 生产者线程等待
				condition_pro.await();
			this.name = name + "------" + num++;
			System.out.println(Thread.currentThread().getName() + "---生产者---" + this.name);
			flag = true;
			// 唤醒消费者线程
			condition_con.signal();
		} finally {
			// 必须执行解锁 所以放在finally中
			lock.unlock();
		}

	}

	public void con_two() throws InterruptedException {
		// 加锁
		lock.lock();
		try {
			// while循环解决的问题是：一个已经判断标志位且已经处于等待状态的消费者线程被唤醒，但并没有再次判断标志位，从而造成安全问题。
			while (!flag)
				// 消费者线程等待
				condition_con.await();
			System.out.println(Thread.currentThread().getName() + "-----消费者-----" + this.name);
			flag = false;
			// 唤醒生产者线程
			condition_pro.signal();
		} finally {
			// 必须执行解锁 所以放在finally中
			lock.unlock();
		}
	}
}

// 生产者
class Producer_two implements Runnable {

	private Things_two th;

	Producer_two(Things_two th) {
		this.th = th;
	}

	@Override
	public void run() {
		while (true) {
			try {
				th.pro_two("--商品--");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}

// 消费者
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
