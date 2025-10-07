/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.util;

import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.randomizerbumpers.RandomizerBumper;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PwdGenerator;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.sql.Timestamp;

import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * @author Manuel de la Peña
 */
public class RandomTestUtil {

	public static Date nextDate() {
		return new Date();
	}

	public static double nextDouble() {
		return CounterLocalServiceUtil.increment();
	}

	public static int nextInt() {
		return (int)CounterLocalServiceUtil.increment();
	}

	public static long nextLong() {
		return CounterLocalServiceUtil.increment();
	}

	public static Timestamp nextTimestamp() {
		return new Timestamp(nextDate().getTime());
	}

	public static boolean randomBoolean() {
		return _random.nextBoolean();
	}

	public static byte[] randomBytes() {
		String string = randomString();

		return string.getBytes();
	}

	public static double randomDouble() {
		double value = _random.nextDouble();

		if (value > 0) {
			return value;
		}
		else if (value == 0) {
			return randomDouble();
		}

		return -value;
	}

	public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
		T[] enumConstants = clazz.getEnumConstants();

		return enumConstants[_random.nextInt(enumConstants.length)];
	}

	public static float randomFloat() {
		float value = _random.nextFloat();

		if (value > 0) {
			return value;
		}
		else if (value == 0) {
			return randomFloat();
		}

		return -value;
	}

	public static int randomInt() {
		return randomInt(1, Integer.MAX_VALUE);
	}

	public static int randomInt(int min, int max) {
		if (max < min) {
			throw new IllegalArgumentException(
				"Max value must be greater than or equal to the min value");
		}

		int value = _random.nextInt();

		long range = max + 1 - min;

		if (range == 0) {
			return value;
		}

		return (int)((Math.abs(value) % range) + min);
	}

	public static Map<Locale, String> randomLocaleStringMap() {
		return randomLocaleStringMap(LocaleUtil.getDefault());
	}

	public static Map<Locale, String> randomLocaleStringMap(Locale locale) {
		return HashMapBuilder.put(
			locale, randomString()
		).build();
	}

	public static long randomLong() {
		return randomLong(1, Long.MAX_VALUE);
	}

	public static long randomLong(long min, long max) {
		if (max < min) {
			throw new IllegalArgumentException(
				"Max value must be greater than or equal to the min value");
		}

		long value = _random.nextLong();

		long range = max + 1 - min;

		if (range == 0) {
			return value;
		}

		return (Math.abs(value) % range) + min;
	}

	@SafeVarargs
	public static String randomString(
		int length, RandomizerBumper<String>... randomizerBumpers) {

		generation:
		for (int i = 0; i < _RANDOMIZER_BUMPER_TRIES_MAX; i++) {
			String randomString = PwdGenerator.getPassword(length);

			for (RandomizerBumper<String> randomizerBumper :
					randomizerBumpers) {

				if (!randomizerBumper.accept(randomString)) {
					continue generation;
				}
			}

			return randomString;
		}

		throw new IllegalStateException(
			StringBundler.concat(
				"Unable to generate a random string that is acceptable by all ",
				"randomizer bumpers ", Arrays.toString(randomizerBumpers),
				" after ", _RANDOMIZER_BUMPER_TRIES_MAX, " tries"));
	}

	@SafeVarargs
	public static String randomString(
		RandomizerBumper<String>... randomizerBumpers) {

		return randomString(8, randomizerBumpers);
	}

	@SafeVarargs
	public static String[] randomStrings(
		int count, RandomizerBumper<String>... randomizerBumpers) {

		String[] strings = new String[count];

		for (int i = 0; i < count; i++) {
			strings[i] = randomString(randomizerBumpers);
		}

		return strings;
	}

	public static UnicodeProperties randomUnicodeProperties(
		int propertyCount, int keyLength, int valueLength) {

		UnicodeProperties unicodeProperties = new UnicodeProperties();

		for (int i = 0; i < propertyCount; i++) {
			unicodeProperties.put(
				randomString(keyLength), randomString(valueLength));
		}

		return unicodeProperties;
	}

	private static final int _RANDOMIZER_BUMPER_TRIES_MAX = 100;

	private static final Random _random = new Random();

}