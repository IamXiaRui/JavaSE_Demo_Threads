package xr_threads_regex;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * 正则表达式的功能测试
 * 
 * */
public class RegexTest {

	public static void main(String[] args) {

		String oldstr = "nihaoowoshiixiaruui";

		// 匹配
		System.out.println("*********************");
		System.out.print("请输入QQ号： ");
		Scanner sc1 = new Scanner(System.in);
		String qqnum = sc1.nextLine();
		System.out.print("校验结果： ");
		checkDemo(qqnum);

		// 切割
		System.out.println("*********************");
		splitDemo(oldstr);

		// 替换
		System.out.println("\n*********************");
		replaceDemo(oldstr);

		// 获取
		System.out.println("\n*********************");
		System.out.print("请输入一行字符串： ");
		Scanner sc2 = new Scanner(System.in);
		String str = sc2.nextLine();
		System.out.print("获取结果： ");
		getDemo(str);
	}

	/*
	 * 匹配功能 1、QQ:至少六位，不能以0开头，全部为数字
	 * 
	 */
	public static void checkDemo(String num) {
		// [n-m] n到m之间的数字 \d 数字：[0-9] {n,m} n到m位
		String checkRegex = "[1-9]\\d{5,}";
		boolean flag = num.matches(checkRegex);
		if (flag) {
			System.out.println("输入正确!");
		} else {
			System.out.println("输入错误!");
		}
	}

	/*
	 * 切割功能 2、给定字符串，将其以叠词切割
	 * 
	 */
	public static void splitDemo(String oldstr) {
		System.out.println("原字符串： " + oldstr);
		// . 任意字符 ()封装成组 1组别
		String splitRegex = "(.)\\1+";
		String[] newstr = oldstr.split(splitRegex);
		System.out.print("新字符串： ");
		// 遍历数组
		for (String s : newstr) {
			System.out.print(" " + s);
		}

	}

	/*
	 * 替换功能 3、将叠词中的多个字符替换成单个字符
	 * 
	 */

	public static void replaceDemo(String oldstr) {
		System.out.println("原字符串： " + oldstr);
		String replaceRegex = "(.)\\1+";
		// 取组中的字符为：$n，n为组的标号,替换为组中的字符
		String newstr = oldstr.replaceAll(replaceRegex, "$1");
		System.out.print("新字符串： " + newstr);
	}

	/*
	 * 获取功能 4、取出一句话中只有三个字符的子串
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
