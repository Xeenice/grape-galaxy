package org.grape.galaxy.utils;

public final class StringUtils {

	public static int getLevenshteinDistance(String sA, String sB) {
		int m = sA.length(), n = sB.length();
		int[] dA = new int[n + 1];
		int[] dB = new int[n + 1];

		for (int i = 0; i <= n; i++) {
			dB[i] = i;
		}

		for (int i = 1; i <= m; i++) {
			dA = dB;
			dB = new int[n + 1];
			for (int j = 0; j <= n; j++) {
				if (j == 0) {
					dB[j] = i;
				} else {
					int cost = (sA.charAt(i - 1) != sB.charAt(j - 1)) ? 1 : 0;
					if (dB[j - 1] < dA[j] && dB[j - 1] < dA[j - 1] + cost) {
						dB[j] = dB[j - 1] + 1;
					} else if (dA[j] < dA[j - 1] + cost) {
						dB[j] = dA[j] + 1;
					} else {
						dB[j] = dA[j - 1] + cost;
					}
				}
			}
		}
		return dB[n];
	}
	
	public static void main(String[] args) {
		String nameA = "abcabcabcabcd";
		String nameB = "abcabcabcabc";
		int l = Math.max(nameA.length(), nameB.length());
		int dist = StringUtils.getLevenshteinDistance(nameA, nameB);
		System.out.println((double) (dist + 1) / (l + 1));
	}
}
