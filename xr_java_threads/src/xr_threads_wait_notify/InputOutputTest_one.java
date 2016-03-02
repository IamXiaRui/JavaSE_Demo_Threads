package xr_threads_wait_notify;

/*
 * 多线程中的等待唤醒机制
 * 主要是wait()  notify()的使用方法
 * 
 * 循环复制姓名的例子
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
	boolean flag = false; // 等待唤醒标志位
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
			synchronized (p) { // 同步代码块 必须一致为 p锁
				if (p.flag)
					try {
						p.wait(); // InputDemo中的P锁等待
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				if (i == 0) {
					p.name = "xiarui";
					p.sex = "man";
				} else {
					p.name = "夏睿";
					p.sex = "男";
				}
				i = (i + 1) % 2; // 更迭i的值
				p.flag = true; // 更换标志位
				p.notify(); // 唤醒OutputDemo中的P锁
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
			synchronized (p) { // 同步代码块 必须一致为 p锁
				if (!p.flag)
					try {
						p.wait();// OutputDemo中的P锁等待
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				System.out.println(p.name + " ------ " + p.sex);
				p.flag = false; // 更换标志位
				p.notify(); // 唤醒InputDemo中的P锁
			}
		}
	}
}