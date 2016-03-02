package xr_threads_regex;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * ������ʽ�Ĺ��ܲ���
 * 
 * */
public class RegexTest {

	public static void main(String[] args) {

		String oldstr = "nihaoowoshiixiaruui";

		// ƥ��
		System.out.println("*********************");
		System.out.print("������QQ�ţ� ");
		Scanner sc1 = new Scanner(System.in);
		String qqnum = sc1.nextLine();
		System.out.print("У������ ");
		checkDemo(qqnum);

		// �и�
		System.out.println("*********************");
		splitDemo(oldstr);

		// �滻
		System.out.println("\n*********************");
		replaceDemo(oldstr);

		// ��ȡ
		System.out.println("\n*********************");
		System.out.print("������һ���ַ����� ");
		Scanner sc2 = new Scanner(System.in);
		String str = sc2.nextLine();
		System.out.print("��ȡ����� ");
		getDemo(str);
	}

	/*
	 * ƥ�书�� 1��QQ:������λ��������0��ͷ��ȫ��Ϊ����
	 * 
	 */
	public static void checkDemo(String num) {
		// [n-m] n��m֮������� \d ���֣�[0-9] {n,m} n��mλ
		String checkRegex = "[1-9]\\d{5,}";
		boolean flag = num.matches(checkRegex);
		if (flag) {
			System.out.println("������ȷ!");
		} else {
			System.out.println("�������!");
		}
	}

	/*
	 * �и�� 2�������ַ����������Ե����и�
	 * 
	 */
	public static void splitDemo(String oldstr) {
		System.out.println("ԭ�ַ����� " + oldstr);
		// . �����ַ� ()��װ���� 1���
		String splitRegex = "(.)\\1+";
		String[] newstr = oldstr.split(splitRegex);
		System.out.print("���ַ����� ");
		// ��������
		for (String s : newstr) {
			System.out.print(" " + s);
		}

	}

	/*
	 * �滻���� 3���������еĶ���ַ��滻�ɵ����ַ�
	 * 
	 */

	public static void replaceDemo(String oldstr) {
		System.out.println("ԭ�ַ����� " + oldstr);
		String replaceRegex = "(.)\\1+";
		// ȡ���е��ַ�Ϊ��$n��nΪ��ı��,�滻Ϊ���е��ַ�
		String newstr = oldstr.replaceAll(replaceRegex, "$1");
		System.out.print("���ַ����� " + newstr);
	}

	/*
	 * ��ȡ���� 4��ȡ��һ�仰��ֻ�������ַ����Ӵ�
	 * 
	 */

	public static void getDemo(String str) {
		String getRegex = "\\b[a-z]{3}\\b";
		Pattern p = Pattern.compile(getRegex);
		Matcher m = p.matcher(str);
		while(m.find()){
			System.out.print(m.group()+" ");
		}
	}
}
