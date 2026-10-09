/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * @author Shuyang Zhou
 */
public class RandomUtil {

	public static int nextInt(int n) {
		Random random = _supplier.get();

		return random.nextInt(n);
	}

	public static int[] nextInts(int n, int size) {
		if (size > n) {
			size = n;
		}

		int[] numbers = new int[n];

		for (int i = 0; i < n; i++) {
			numbers[i] = i;
		}

		shuffle(_supplier.get(), numbers);

		if (size == n) {
			return numbers;
		}

		int[] results = new int[size];

		System.arraycopy(numbers, 0, results, 0, size);

		return results;
	}

	public static void shuffle(Random random, int[] numbers) {
		for (int i = numbers.length; i > 1; i--) {
			int position = random.nextInt(i);

			if (position == (i - 1)) {
				continue;
			}

			int number = numbers[position];

			numbers[position] = numbers[i - 1];
			numbers[i - 1] = number;
		}
	}

	public static String shuffle(Random random, String s) {
		StringBuilder sb = new StringBuilder(s);

		shuffle(random, sb);

		return sb.toString();
	}

	public static void shuffle(Random random, StringBuilder sb) {
		for (int i = sb.length(); i > 1; i--) {
			int position = random.nextInt(i);

			if (position == (i - 1)) {
				continue;
			}

			char c = sb.charAt(position);

			sb.setCharAt(position, sb.charAt(i - 1));
			sb.setCharAt(i - 1, c);
		}
	}

	public static String shuffle(String s) {
		return shuffle(_supplier.get(), s);
	}

	protected static void setSupplier(Supplier<Random> supplier) {
		_supplier = supplier;
	}

	private static Supplier<Random> _supplier = ThreadLocalRandom::current;

}