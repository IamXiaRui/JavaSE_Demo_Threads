package xr_threads_wait_notify;

/*
 * ���߳��еĵȴ����ѻ���
 * ��Ҫ��wait()  notify()��ʹ�÷���
 * 
 * ѭ����������������
 * */
public class InputOutputTest_one {

	public static void main(String[] args) {
		Person_one p = new Person_one();

		InputDemo_one in = new InputDemo_one(p);
		OutputDemo_one out = new OutputDemo_one(p);

		Thread t1 = new Thread(in);
		Thread t2 = new Thread(out);

		t1.start();
		t2.start();
	}
}

class Person_one {
	String name;
	String sex;
	boolean flag = false; // �ȴ����ѱ�־λ
}

class InputDemo_one implements Runnable {
	private Person_one p;

	InputDemo_one(Person_one p) {
		this.p = p;
	}

	@Override
	public void run() {
		int i = 0;
		while (true) {
			synchronized (p) { // ͬ������� ����һ��Ϊ p��
				if (p.flag)
					try {
						p.wait(); // InputDemo�е�P���ȴ�
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				if (i == 0) {
					p.name = "xiarui";
					p.sex = "man";
				} else {
					p.name = "���";
					p.sex = "��";
				}
				i = (i + 1) % 2; // ����i��ֵ
				p.flag = true; // ������־λ
				p.notify(); // ����OutputDemo�е�P��
			}
		}
	}

}

class OutputDemo_one implements Runnable {
	private Person_one p;

	OutputDemo_one(Person_one p) {
		this.p = p;
	}

	@Override
	public void run() {
		while (true) {
			synchronized (p) { // ͬ������� ����һ��Ϊ p��
				if (!p.flag)
					try {
						p.wait();// OutputDemo�е�P���ȴ�
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				System.out.println(p.name + " ------ " + p.sex);
				p.flag = false; // ������־λ
				p.notify(); // ����InputDemo�е�P��
			}
		}
	}
}