/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.lang.reflect.Array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToLongFunction;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class ListUtil {

	public static <E> List<E> concat(List<? extends E>... lists) {
		List<E> newList = new ArrayList<>();

		for (List<? extends E> list : lists) {
			newList.addAll(list);
		}

		return newList;
	}

	public static <E> List<E> copy(List<? extends E> master) {
		if (master == null) {
			return null;
		}

		return new ArrayList<>(master);
	}

	public static <E> void copy(
		List<? extends E> master, List<? super E> copy) {

		if ((master == null) || (copy == null)) {
			return;
		}

		copy.clear();

		copy.addAll(master);
	}

	public static <E> int count(
		List<? extends E> list, Predicate<E> predicate) {

		if (isEmpty(list)) {
			return 0;
		}

		int count = 0;

		for (E element : list) {
			if (!predicate.test(element)) {
				continue;
			}

			count++;
		}

		return count;
	}

	public static <E> void distinct(
		List<? extends E> list, Comparator<E> comparator) {

		if (isEmpty(list)) {
			return;
		}

		Set<E> set = new HashSet<>();

		Iterator<? extends E> iterator = list.iterator();

		while (iterator.hasNext()) {
			E object = iterator.next();

			if (set.add(object)) {
				continue;
			}

			iterator.remove();
		}

		if (comparator != null) {
			list.sort(comparator);
		}
	}

	public static void distinct(List<?> list) {
		distinct(list, null);
	}

	public static <E> boolean exists(
		List<? extends E> list, Predicate<E> predicate) {

		if (isEmpty(list)) {
			return false;
		}

		for (E element : list) {
			if (predicate.test(element)) {
				return true;
			}
		}

		return false;
	}

	public static <T> List<T> filter(
		List<? extends T> inputList, List<T> outputList,
		Predicate<T> predicate) {

		for (T item : inputList) {
			if (!predicate.test(item)) {
				continue;
			}

			outputList.add(item);
		}

		return outputList;
	}

	public static <T> List<T> filter(
		List<? extends T> inputList, Predicate<T> predicate) {

		return filter(inputList, new ArrayList<T>(inputList.size()), predicate);
	}

	public static <T> List<T> filter(
		List<T> list, BiFunction<Integer, Integer, List<T>> listBiFunction,
		Supplier<Integer> countSupplier, Predicate<T> predicate, int start,
		int end) {

		list = filter(list, predicate);

		int count = countSupplier.get();
		int delta = end - start;

		int pageCount = (count / delta) + (((count % delta) == 0) ? 0 : 1);
		int pageIndex = (int)Math.ceil((double)start / delta);

		int pageSize = end - start;

		while ((list.size() < pageSize) && (pageIndex < pageCount)) {
			pageIndex++;

			start += delta;
			end += delta;

			list.addAll(
				subList(
					filter(listBiFunction.apply(start, end), predicate), 0,
					pageSize - list.size()));
		}

		return list;
	}

	public static List<Boolean> fromArray(boolean[] array) {
		if (ArrayUtil.isEmpty(array)) {
			return new ArrayList<>();
		}

		List<Boolean> list = new ArrayList<>(array.length);

		for (boolean value : array) {
			list.add(value);
		}

		return list;
	}

	public static List<Character> fromArray(char[] array) {
		if (ArrayUtil.isEmpty(array)) {
			return new ArrayList<>();
		}

		List<Character> list = new ArrayList<>(array.length);

		for (char value : array) {
			list.add(value);
		}

		return list;
	}

	public static List<Double> fromArray(double[] array) {
		if (ArrayUtil.isEmpty(array)) {
			return new ArrayList<>();
		}

		List<Double> list = new ArrayList<>(array.length);

		for (double value : array) {
			list.add(value);
		}

		return list;
	}

	public static <E> List<E> fromArray(E... array) {
		if (ArrayUtil.isEmpty(array)) {
			return new ArrayList<>();
		}

		return new ArrayList<>(Arrays.asList(array));
	}

	public static List<Float> fromArray(float[] array) {
		if (ArrayUtil.isEmpty(array)) {
			return new ArrayList<>();
		}

		List<Float> list = new ArrayList<>(array.length);

		for (float value : array) {
			list.add(value);
		}

		return list;
	}

	public static List<Integer> fromArray(int[] array) {
		if (ArrayUtil.isEmpty(array)) {
			return new ArrayList<>();
		}

		List<Integer> list = new ArrayList<>(array.length);

		for (int value : array) {
			list.add(value);
		}

		return list;
	}

	public static List<Long> fromArray(long[] array) {
		if (ArrayUtil.isEmpty(array)) {
			return new ArrayList<>();
		}

		List<Long> list = new ArrayList<>(array.length);

		for (long value : array) {
			list.add(value);
		}

		return list;
	}

	public static List<Short> fromArray(short[] array) {
		if (ArrayUtil.isEmpty(array)) {
			return new ArrayList<>();
		}

		List<Short> list = new ArrayList<>(array.length);

		for (short value : array) {
			list.add(value);
		}

		return list;
	}

	@SuppressWarnings("rawtypes")
	public static <E> List<E> fromCollection(Collection<? extends E> c) {
		if ((c != null) && (c instanceof List)) {
			return (List)c;
		}

		if ((c == null) || c.isEmpty()) {
			return new ArrayList<>();
		}

		List<E> list = new ArrayList<>(c.size());

		list.addAll(c);

		return list;
	}

	public static <E> List<E> fromEnumeration(
		Enumeration<? extends E> enumeration) {

		List<E> list = new ArrayList<>();

		while (enumeration.hasMoreElements()) {
			E object = enumeration.nextElement();

			list.add(object);
		}

		return list;
	}

	public static List<String> fromFile(File file) throws IOException {
		if (!file.exists()) {
			return new ArrayList<>();
		}

		List<String> list = new ArrayList<>();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new FileReader(file))) {

			String s = StringPool.BLANK;

			while ((s = unsyncBufferedReader.readLine()) != null) {
				list.add(s);
			}
		}

		return list;
	}

	public static List<String> fromFile(String fileName) throws IOException {
		return fromFile(new File(fileName));
	}

	public static <E> List<E> fromMapKeys(Map<? extends E, ?> map) {
		if (MapUtil.isEmpty(map)) {
			return new ArrayList<>();
		}

		return new ArrayList<>(map.keySet());
	}

	public static <E> List<E> fromMapValues(Map<?, ? extends E> map) {
		if (MapUtil.isEmpty(map)) {
			return new ArrayList<>();
		}

		return new ArrayList<>(map.values());
	}

	public static List<String> fromString(String s) {
		return fromArray(StringUtil.splitLines(s));
	}

	public static List<String> fromString(String s, String delimiter) {
		return fromArray(StringUtil.split(s, delimiter));
	}

	public static <E> E[] getPreviousAndNext(
		List<E> list, Predicate<E> predicate, IntFunction<E[]> intFunction) {

		E[] previousAndNext = intFunction.apply(3);

		for (int i = 0; i < list.size(); i++) {
			E element = list.get(i);

			if (predicate.test(element)) {
				if (i > 0) {
					previousAndNext[0] = list.get(i - 1);
				}

				previousAndNext[1] = element;

				if (i < (list.size() - 1)) {
					previousAndNext[2] = list.get(i + 1);
				}

				break;
			}
		}

		return previousAndNext;
	}

	public static boolean isEmpty(List<?> list) {
		if ((list == null) || list.isEmpty()) {
			return true;
		}

		return false;
	}

	public static boolean isNotEmpty(List<?> list) {
		return !isEmpty(list);
	}

	public static <E> void isNotEmptyForEach(
		List<? extends E> list, Consumer<? super E> consumer) {

		if (!isEmpty(list)) {
			list.forEach(consumer);
		}
	}

	public static boolean isNotNull(List<?> list) {
		return !isNull(list);
	}

	public static boolean isNull(List<?> list) {
		if ((list == null) || list.isEmpty()) {
			return true;
		}

		for (Object bean : list) {
			if (Validator.isNotNull(bean)) {
				return false;
			}
		}

		return true;
	}

	public static boolean isUnmodifiableList(List<?> list) {
		return _UNMODIFIABLE_LIST_CLASS.isInstance(list);
	}

	public static <E> List<E> remove(List<E> list, List<? extends E> remove) {
		if (isEmpty(list) || isEmpty(remove)) {
			return list;
		}

		list = copy(list);

		for (E element : remove) {
			list.remove(element);
		}

		return list;
	}

	public static <E> Iterator<E> reverseIterator(List<E> list) {
		final ListIterator<E> listIterator = list.listIterator(list.size());

		return new Iterator<E>() {

			@Override
			public boolean hasNext() {
				return listIterator.hasPrevious();
			}

			@Override
			public E next() {
				return listIterator.previous();
			}

			@Override
			public void remove() {
				listIterator.remove();
			}

		};
	}

	public static <E> List<E> sort(List<E> list) {
		return sort(list, null);
	}

	public static <E> List<E> sort(
		List<E> list, Comparator<? super E> comparator) {

		if (isUnmodifiableList(list)) {
			list = copy(list);
		}

		list.sort(comparator);

		return list;
	}

	public static <E> List<E> subList(List<E> list, int start, int end) {
		if (start < 0) {
			start = 0;
		}

		if ((end < 0) || (end > list.size())) {
			end = list.size();
		}

		if (start < end) {
			return list.subList(start, end);
		}

		return Collections.emptyList();
	}

	public static <T, A> A[] toArray(
		List<? extends T> list, Accessor<T, A> accessor) {

		if (isEmpty(list)) {
			return (A[])Array.newInstance(accessor.getAttributeClass(), 0);
		}

		A[] array = (A[])Array.newInstance(
			accessor.getAttributeClass(), list.size());

		for (int i = 0; i < list.size(); i++) {
			T bean = list.get(i);

			A attribute = accessor.get(bean);

			array[i] = attribute;
		}

		return array;
	}

	public static <E> List<E> toList(E value) {
		return new ArrayList<>(Arrays.asList(value));
	}

	public static <T, A> List<A> toList(List<T> list, Accessor<T, A> accessor) {
		List<A> aList = new ArrayList<>(list.size());

		for (T t : list) {
			aList.add(accessor.get(t));
		}

		return aList;
	}

	public static <T, R> List<R> toList(List<T> list, Function<T, R> function) {
		List<R> result = new ArrayList<>(list.size());

		for (T t : list) {
			result.add(function.apply(t));
		}

		return result;
	}

	public static <T, V extends T> List<T> toList(List<V> vlist) {
		return new ArrayList<T>(vlist);
	}

	public static <T> long[] toLongArray(
		List<? extends T> list, Accessor<T, Long> accessor) {

		if (isEmpty(list)) {
			return _EMPTY_LONG_ARRAY;
		}

		long[] array = new long[list.size()];

		for (int i = 0; i < list.size(); i++) {
			T bean = list.get(i);

			Long attribute = accessor.get(bean);

			array[i] = attribute;
		}

		return array;
	}

	public static <T> long[] toLongArray(
		List<? extends T> list, ToLongFunction<T> toLongFunction) {

		if (isEmpty(list)) {
			return _EMPTY_LONG_ARRAY;
		}

		long[] array = new long[list.size()];

		for (int i = 0; i < list.size(); i++) {
			array[i] = toLongFunction.applyAsLong(list.get(i));
		}

		return array;
	}

	/**
	 * @see ArrayUtil#toString(Object[], Accessor)
	 */
	public static <T, A> String toString(
		List<? extends T> list, Accessor<T, A> accessor) {

		return toString(list, accessor, StringPool.COMMA);
	}

	/**
	 * @see ArrayUtil#toString(Object[], Accessor, String)
	 */
	public static <T, A> String toString(
		List<? extends T> list, Accessor<T, A> accessor, String delimiter) {

		if (isEmpty(list)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler((2 * list.size()) - 1);

		for (int i = 0; i < list.size(); i++) {
			T bean = list.get(i);

			A attribute = accessor.get(bean);

			if (attribute != null) {
				sb.append(attribute);
			}

			if ((i + 1) == list.size()) {
				continue;
			}

			sb.append(delimiter);
		}

		return sb.toString();
	}

	/**
	 * @see ArrayUtil#toString(Object[], String)
	 */
	public static String toString(List<?> list, String param) {
		return toString(list, param, StringPool.COMMA);
	}

	/**
	 * @see ArrayUtil#toString(Object[], String, String)
	 */
	public static String toString(
		List<?> list, String param, String delimiter) {

		if (isEmpty(list)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler((2 * list.size()) - 1);

		for (int i = 0; i < list.size(); i++) {
			Object bean = list.get(i);

			Object value = null;

			if (Validator.isNull(param)) {
				value = String.valueOf(bean);
			}
			else {
				value = BeanPropertiesUtil.getObject(bean, param);
			}

			if (value != null) {
				sb.append(value);
			}

			if ((i + 1) == list.size()) {
				continue;
			}

			sb.append(delimiter);
		}

		return sb.toString();
	}

	public static <T> List<T> unique(List<T> list) {
		Set<T> set = new LinkedHashSet<>();

		set.addAll(list);

		if (list.size() == set.size()) {
			return list;
		}

		return new ArrayList<>(set);
	}

	private static final long[] _EMPTY_LONG_ARRAY = {};

	private static final Class<? extends List<?>> _UNMODIFIABLE_LIST_CLASS;

	static {
		List<Object> unmodifiableList = Collections.<Object>unmodifiableList(
			new LinkedList<Object>());

		_UNMODIFIABLE_LIST_CLASS =
			(Class<? extends List<?>>)unmodifiableList.getClass();
	}

}