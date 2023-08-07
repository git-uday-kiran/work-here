package driver_code;

public class Coder {

	public static void main(String... args) {
		System.out.println("Let's do it....");

//		int[] ints = { 941, 797, 1475, 638, 191, 712 };
		int[] ints = {64,32,16,8,4,2,1,1000};
		int k = 8;

		System.out.println(new Coder().distributeCookies(ints, 8));
	}

	public int minCookies = Integer.MAX_VALUE;

	public int distributeCookies(int[] cookies, int k) {
		search(cookies, new int[k], 0);
		return minCookies;
	}

	public void search(int[] cookies, int[] childs, int pos) {
		if (pos == cookies.length) {
			int max = 0;
			for (int x : childs) {
				if (x > max)
					max = x;
			}
			minCookies = Math.min(minCookies, max);
			return;
		}

		for (int childPos = 0; childPos < childs.length; childPos++) {
			childs[childPos] += cookies[pos];
			search(cookies, childs, pos + 1);
			childs[childPos] -= cookies[pos];
		}
	}
}
