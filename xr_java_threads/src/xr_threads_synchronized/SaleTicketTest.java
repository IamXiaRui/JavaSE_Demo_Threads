package xr_threads_synchronized;

/*
*���߳���ͬ��������ʹ��
*
*��Ʊ����
*
*/
public class SaleTicketTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("**********��Ʊ���**********");
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
			// ͬ������� objΪ���������
			synchronized (obj) {
				if (ticket > 0) {
					System.out.println(Thread.currentThread().getName() + "--����һ�� ��Ʊ����--" + ticket--);
				} else {
					// ���Ʊ������ ɱ������
					System.exit(ticket);
				}
			}
		}
	}

}
