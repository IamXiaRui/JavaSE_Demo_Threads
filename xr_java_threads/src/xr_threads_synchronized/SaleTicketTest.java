package xr_threads_synchronized;

/*
*多线程中同步代码块的使用
*
*售票例子
*
*/
public class SaleTicketTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("**********售票情况**********");
		SaleTicket st = new SaleTicket();

		Thread t1 = new Thread(st);
		Thread t2 = new Thread(st);
		Thread t3 = new Thread(st);
		Thread t4 = new Thread(st);

		t1.start();
		t2.start();
		t3.start();
		t4.start();
	}

}

class SaleTicket implements Runnable {

	private int ticket = 2000;
	Object obj = new Object();

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			// 同步代码块 obj为任意对象锁
			synchronized (obj) {
				if (ticket > 0) {
					System.out.println(Thread.currentThread().getName() + "--出售一张 余票数：--" + ticket--);
				} else {
					// 如果票卖完了 杀死进程
					System.exit(ticket);
				}
			}
		}
	}

}
