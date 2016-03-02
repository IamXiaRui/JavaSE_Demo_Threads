package xr_threads_wait_notify;

/*
 * ���߳��еĵȴ����ѻ���
 * ��Ҫ��wait()  notify()��ʹ�÷���
 * 
 * ѭ�������������Ӵ�����Ż�
 * */
public class InputOutputTest_two {

	public static void main(String[] args) {
		Person_two p = new Person_two();

		new Thread(new InputDemo_two(p)).start();
		new Thread(new OutputDemo_two(p)).start();
	}
}

class Person_two {
	private String name;
	private String sex;
	private boolean flag = false; // �ȴ����ѱ�־λ

	// ͬ������
	public synchronized void set(String name, String sex) {
		if (flag)
			try {
				this.wait(); // ����this���ȴ�
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		this.name = name;
		this.sex = sex;
		flag = true; // ������־λ
		this.notify(); // ���ѱ���this��
	}

	// ͬ������
	public synchronized void out() {
		if (!flag)
			try {
				this.wait();// ����this���ȴ�
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		System.out.println(name + " ------ " + sex);
		flag = false; // ������־λ
		this.notify(); // ���ѱ���this��
	}
}

class InputDemo_two implements Runnable {
	private Person_two p;

	InputDemo_two(Person_two p) {
		this.p = p;
	}

	@Override
	public void run() {
		int i = 0;
		while (true) {
			if (i == 0) {
				p.set("xiarui", "man");
			} else {
				p.set("���", "��");
			}
			i = (i + 1) % 2; // ����i��ֵ
		}
	}

}

class OutputDemo_two implements Runnable {
	private Person_two p;

	OutputDemo_two(Person_two p) {
		this.p = p;
	}

	@Override
	public void run() {
		while (true) {
			p.out();
		}
	}
}