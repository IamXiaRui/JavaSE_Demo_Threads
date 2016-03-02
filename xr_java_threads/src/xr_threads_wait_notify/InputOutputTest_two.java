package xr_threads_wait_notify;

/*
 * 多线程中的等待唤醒机制
 * 主要是wait()  notify()的使用方法
 * 
 * 循环复制姓名例子代码的优化
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
	private boolean flag = false; // 等待唤醒标志位

	// 同步函数
	public synchronized void set(String name, String sex) {
		if (flag)
			try {
				this.wait(); // 本类this锁等待
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		this.name = name;
		this.sex = sex;
		flag = true; // 更换标志位
		this.notify(); // 唤醒本类this锁
	}

	// 同步函数
	public synchronized void out() {
		if (!flag)
			try {
				this.wait();// 本类this锁等待
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		System.out.println(name + " ------ " + sex);
		flag = false; // 更换标志位
		this.notify(); // 唤醒本类this锁
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
				p.set("夏睿", "男");
			}
			i = (i + 1) % 2; // 更迭i的值
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