/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.security.RandomUtil;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 */
public class SetUtil {

	public static <T> Set<T> asymmetricDifference(
		Collection<T> collection1, Collection<T> collection2) {

		if (collection1.isEmpty()) {
			return Collections.emptySet();
		}

		Set<T> set1 = new HashSet<>(collection1);
		Set<T> set2 = new HashSet<>(collection2);

		Set<T> symmetricDifferenceSet = symmetricDifference(set1, set2);

		symmetricDifferenceSet.removeAll(set2);

		return symmetricDifferenceSet;
	}

	public static Set<Boolean> fromArray(boolean[] array) {
		if (ArrayUtil.isEmpty(array)) {
			return new HashSet<>();
		}

		Set<Boolean> set = new HashSet<>();

		for (boolean b : array) {
			set.add(b);
		}

		return set;
	}

	public static Set<Byte> fromArray(byte[] array) {
		if (ArrayUtil.isEmpty(array)) {
			return new HashSet<>();
		}

		Set<Byte> set = new HashSet<>();

		for (byte b : array) {
			set.add(b);
		}

		return set;
	}

	public static Set<Character> fromArray(char[] array) {
		if (ArrayUtil.isEmpty(array)) {
			return new HashSet<>();
		}

		Set<Character> set = new HashSet<>();

		for (char c : array) {
			set.add(c);
		}

		return set;
	}

	public static Set<Double> fromArray(double[] array) {
		if (ArrayUtil.isEmpty(array)) {
			return new HashSet<>();
		}

		Set<Double> set = new HashSet<>();

		for (double d : array) {
			set.add(d);
		}

		return set;
	}

	public static <E> Set<E> fromArray(E... array) {
		if (ArrayUtil.isEmpty(array)) {
			return new HashSet<>();
		}

		Set<E> set = new HashSet<>();

		for (E object : array) {
			set.add(object);
		}

		return set;
	}

	public static Set<Float> fromArray(float[] array) {
		if (ArrayUtil.isEmpty(array)) {
			return new HashSet<>();
		}

		Set<Float> set = new HashSet<>();

		for (float f : array) {
			set.add(f);
		}

		return set;
	}

	public static Set<Integer> fromArray(int[] array) {
		if (ArrayUtil.isEmpty(array)) {
			return new HashSet<>();
		}

		Set<Integer> set = new HashSet<>();

		for (int i : array) {
			set.add(i);
		}

		return set;
	}

	public static Set<Long> fromArray(long[] array) {
		if (ArrayUtil.isEmpty(array)) {
			return new HashSet<>();
		}

		Set<Long> set = new HashSet<>();

		for (long l : array) {
			set.add(l);
		}

		return set;
	}

	public static Set<Short> fromArray(short[] array) {
		if (ArrayUtil.isEmpty(array)) {
			return new HashSet<>();
		}

		Set<Short> set = new HashSet<>();

		for (short s : array) {
			set.add(s);
		}

		return set;
	}

	public static <E> Set<E> fromCollection(Collection<? extends E> c) {
		if ((c != null) && (c instanceof Set)) {
			return (Set<E>)c;
		}

		if ((c == null) || c.isEmpty()) {
			return new HashSet<>();
		}

		return new HashSet<>(c);
	}

	public static <E> Set<E> fromEnumeration(
		Enumeration<? extends E> enumeration) {

		Set<E> set = new HashSet<>();

		while (enumeration.hasMoreElements()) {
			set.add(enumeration.nextElement());
		}

		return set;
	}

	public static Set<String> fromFile(File file) throws IOException {
		Set<String> set = new HashSet<>();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new FileReader(file))) {

			String s = StringPool.BLANK;

			while ((s = unsyncBufferedReader.readLine()) != null) {
				set.add(s);
			}
		}

		return set;
	}

	public static Set<String> fromFile(String fileName) throws IOException {
		return fromFile(new File(fileName));
	}

	public static <E> Set<E> fromIterator(Iterator<E> iterator) {
		Set<E> set = new HashSet<>();

		while (iterator.hasNext()) {
			set.add(iterator.next());
		}

		return set;
	}

	public static <E> Set<E> fromList(List<? extends E> array) {
		if (ListUtil.isEmpty(array)) {
			return new HashSet<>();
		}

		return new HashSet<>(array);
	}

	public static Set<String> fromString(String s) {
		return fromArray(StringUtil.splitLines(s));
	}

	public static <T> Set<T> intersect(
		Collection<T> collection1, Collection<T> collection2) {

		if (collection1.isEmpty() || collection2.isEmpty()) {
			return Collections.emptySet();
		}

		Set<T> set1 = new HashSet<>(collection1);
		Set<T> set2 = new HashSet<>(collection2);

		if (set1.size() > set2.size()) {
			set2.retainAll(set1);

			return set2;
		}

		set1.retainAll(set2);

		return set1;
	}

	public static Set<Long> intersect(long[] array1, long[] array2) {
		return intersect(fromArray(array1), fromArray(array2));
	}

	public static boolean isEmpty(Set<?> set) {
		if ((set == null) || set.isEmpty()) {
			return true;
		}

		return false;
	}

	public static boolean isNotEmpty(Set<?> set) {
		return !isEmpty(set);
	}

	public static <T> T randomElement(Set<T> set) {
		if (isEmpty(set)) {
			return null;
		}

		int index = RandomUtil.nextInt(set.size());

		Iterator<T> iterator = set.iterator();

		for (int i = 0; i < index; i++) {
			iterator.next();
		}

		return iterator.next();
	}

	public static <T> Set<T> symmetricDifference(
		Collection<T> collection1, Collection<T> collection2) {

		if (collection1.isEmpty()) {
			return new HashSet<>(collection2);
		}

		if (collection2.isEmpty()) {
			return new HashSet<>(collection1);
		}

		Set<T> set1 = new HashSet<>(collection1);
		Set<T> set2 = new HashSet<>(collection2);

		Set<T> intersectionSet = intersect(set1, set2);

		if (set1.size() > set2.size()) {
			set1.addAll(set2);
		}
		else {
			set2.addAll(set1);

			set1 = set2;
		}

		set1.removeAll(intersectionSet);

		return set1;
	}

	public static Set<Long> symmetricDifference(long[] array1, long[] array2) {
		return symmetricDifference(fromArray(array1), fromArray(array2));
	}

}