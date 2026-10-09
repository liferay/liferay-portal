/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class ListUtil {

	public static <T> void add(List<T> list, T item) {
		list.add(item);
	}

	public static <E> List<E> copy(List<? extends E> master) {
		if (master == null) {
			return null;
		}

		return new ArrayList<>(master);
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
				sublist(
					filter(listBiFunction.apply(start, end), predicate), 0,
					pageSize - list.size()));
		}

		return list;
	}

	public static <T> T get(List<T> list, Integer index) {
		return list.get(index);
	}

	public static <T> T get(List<T> list, Long index) {
		return list.get(Math.toIntExact(index));
	}

	public static <T> T get(List<T> list, String index) {
		try {
			return list.get(Integer.parseInt(index));
		}
		catch (IndexOutOfBoundsException | NumberFormatException exception) {
			throw exception;
		}
	}

	public static boolean isEmpty(List<?> list) {
		if ((list == null) || list.isEmpty()) {
			return true;
		}

		return false;
	}

	public static List<String> newList() {
		return new ArrayList<>();
	}

	public static List<String> newListFromString(String s) {
		return newListFromString(s, StringPool.COMMA);
	}

	public static List<String> newListFromString(String s, String delimiter) {
		s = s.trim();

		if (delimiter.equals(",") && s.endsWith("]") && s.startsWith("[")) {
			try {
				JSONArray jsonArray = new JSONArray(s);

				List<String> list = new ArrayList<>();

				if (jsonArray != null) {
					for (int i = 0; i < jsonArray.length(); i++) {
						list.add(jsonArray.getString(i));
					}
				}

				return list;
			}
			catch (JSONException jsonException) {
			}
		}

		List<String> list = new ArrayList<>();

		for (String item : s.split(delimiter)) {
			list.add(item.trim());
		}

		return list;
	}

	public static List<Object> newObjectList() {
		return new ArrayList<>();
	}

	public static <T> void remove(List<T> list, T item) {
		list.remove(item);
	}

	public static <T> String size(List<T> list) {
		int size = list.size();

		return String.valueOf(size);
	}

	public static <E> List<E> sort(List<E> list) {
		return sort(list, null);
	}

	public static <E> List<E> sort(
		List<E> list, Comparator<? super E> comparator) {

		list = copy(list);

		Collections.sort(list, comparator);

		return list;
	}

	public static String sort(String s) {
		return sort(s, StringPool.COMMA);
	}

	public static String sort(String s, String delimiter) {
		List<String> list = Arrays.asList(s.split(delimiter));

		return toString(sort(list), delimiter);
	}

	public static <E> List<E> sublist(List<E> list, int start, int end) {
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

	public static String toString(List<?> list) {
		return toString(list, StringPool.COMMA);
	}

	public static String toString(List<?> list, String delimiter) {
		if (isEmpty(list)) {
			return StringPool.BLANK;
		}

		StringBuilder sb = new StringBuilder((2 * list.size()) - 1);

		for (int i = 0; i < list.size(); i++) {
			Object value = list.get(i);

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

}