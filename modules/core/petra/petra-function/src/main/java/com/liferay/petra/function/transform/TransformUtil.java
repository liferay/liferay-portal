/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.function.transform;

import com.liferay.petra.function.UnsafeFunction;

import java.lang.reflect.Array;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class TransformUtil {

	public static <T, R, E extends Throwable> List<R> transform(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction) {

		try {
			return unsafeTransform(collection, unsafeFunction);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <R, E extends Throwable> R[] transform(
		int[] array, UnsafeFunction<Integer, R, E> unsafeFunction,
		Class<? extends R> clazz) {

		try {
			return unsafeTransform(array, unsafeFunction, clazz);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <R, E extends Throwable> R[] transform(
		long[] array, UnsafeFunction<Long, R, E> unsafeFunction,
		Class<? extends R> clazz) {

		try {
			return unsafeTransform(array, unsafeFunction, clazz);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T, R, E extends Throwable> R[] transform(
		T[] array, UnsafeFunction<T, R, E> unsafeFunction,
		Class<? extends R> clazz) {

		try {
			return unsafeTransform(array, unsafeFunction, clazz);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T, R, E extends Throwable> R[] transformToArray(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction,
		Class<? extends R> clazz) {

		try {
			return unsafeTransformToArray(collection, unsafeFunction, clazz);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T, E extends Throwable> boolean[] transformToBooleanArray(
		Collection<T> collection,
		UnsafeFunction<T, Boolean, E> unsafeFunction) {

		try {
			return unsafeTransformToBooleanArray(collection, unsafeFunction);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T, E extends Throwable> boolean[] transformToBooleanArray(
		T[] array, UnsafeFunction<T, Boolean, E> unsafeFunction) {

		try {
			return unsafeTransformToBooleanArray(array, unsafeFunction);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T, E extends Throwable> byte[] transformToByteArray(
		Collection<T> collection, UnsafeFunction<T, Byte, E> unsafeFunction) {

		try {
			return unsafeTransformToByteArray(collection, unsafeFunction);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T, E extends Throwable> byte[] transformToByteArray(
		T[] array, UnsafeFunction<T, Byte, E> unsafeFunction) {

		try {
			return unsafeTransformToByteArray(array, unsafeFunction);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T, E extends Throwable> double[] transformToDoubleArray(
		Collection<T> collection, UnsafeFunction<T, Double, E> unsafeFunction) {

		try {
			return unsafeTransformToDoubleArray(collection, unsafeFunction);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T, E extends Throwable> double[] transformToDoubleArray(
		T[] array, UnsafeFunction<T, Double, E> unsafeFunction) {

		try {
			return unsafeTransformToDoubleArray(array, unsafeFunction);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T, E extends Throwable> float[] transformToFloatArray(
		Collection<T> collection, UnsafeFunction<T, Float, E> unsafeFunction) {

		try {
			return unsafeTransformToFloatArray(collection, unsafeFunction);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T, E extends Throwable> float[] transformToFloatArray(
		T[] array, UnsafeFunction<T, Float, E> unsafeFunction) {

		try {
			return unsafeTransformToFloatArray(array, unsafeFunction);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T, R, E extends Throwable> int[] transformToIntArray(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction) {

		try {
			return unsafeTransformToIntArray(collection, unsafeFunction);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T, E extends Throwable> int[] transformToIntArray(
		T[] array, UnsafeFunction<T, Integer, E> unsafeFunction) {

		try {
			return unsafeTransformToIntArray(array, unsafeFunction);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <R, E extends Throwable> List<R> transformToList(
		int[] array, UnsafeFunction<Integer, R, E> unsafeFunction) {

		try {
			return unsafeTransformToList(array, unsafeFunction);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <R, E extends Throwable> List<R> transformToList(
		long[] array, UnsafeFunction<Long, R, E> unsafeFunction) {

		try {
			return unsafeTransformToList(array, unsafeFunction);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T, R, E extends Throwable> List<R> transformToList(
		T[] array, UnsafeFunction<T, R, E> unsafeFunction) {

		try {
			return unsafeTransformToList(array, unsafeFunction);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T, R, E extends Throwable> long[] transformToLongArray(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction) {

		try {
			return unsafeTransformToLongArray(collection, unsafeFunction);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T, E extends Throwable> long[] transformToLongArray(
		T[] array, UnsafeFunction<T, Long, E> unsafeFunction) {

		try {
			return unsafeTransformToLongArray(array, unsafeFunction);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T, E extends Throwable> short[] transformToShortArray(
		Collection<T> collection, UnsafeFunction<T, Short, E> unsafeFunction) {

		try {
			return unsafeTransformToShortArray(collection, unsafeFunction);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T, E extends Throwable> short[] transformToShortArray(
		T[] array, UnsafeFunction<T, Short, E> unsafeFunction) {

		try {
			return unsafeTransformToShortArray(array, unsafeFunction);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T, R, E extends Throwable> List<R> unsafeTransform(
			Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction)
		throws E {

		if (collection == null) {
			return new ArrayList<>();
		}

		List<R> list = new ArrayList<>(collection.size());

		for (T item : collection) {
			R newItem = unsafeFunction.apply(item);

			if (newItem == null) {
				continue;
			}

			list.add(newItem);
		}

		return list;
	}

	public static <R, E extends Throwable> R[] unsafeTransform(
			int[] array, UnsafeFunction<Integer, R, E> unsafeFunction,
			Class<? extends R> clazz)
		throws E {

		List<R> list = unsafeTransformToList(array, unsafeFunction);

		return list.toArray((R[])Array.newInstance(clazz, 0));
	}

	public static <R, E extends Throwable> R[] unsafeTransform(
			long[] array, UnsafeFunction<Long, R, E> unsafeFunction,
			Class<? extends R> clazz)
		throws E {

		List<R> list = unsafeTransformToList(array, unsafeFunction);

		return list.toArray((R[])Array.newInstance(clazz, 0));
	}

	public static <T, R, E extends Throwable> R[] unsafeTransform(
			T[] array, UnsafeFunction<T, R, E> unsafeFunction,
			Class<? extends R> clazz)
		throws E {

		List<R> list = unsafeTransformToList(array, unsafeFunction);

		return list.toArray((R[])Array.newInstance(clazz, 0));
	}

	public static <T, R, E extends Throwable> R[] unsafeTransformToArray(
			Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction,
			Class<? extends R> clazz)
		throws E {

		List<R> list = unsafeTransform(collection, unsafeFunction);

		return list.toArray((R[])Array.newInstance(clazz, 0));
	}

	public static <T, E extends Throwable> boolean[]
			unsafeTransformToBooleanArray(
				Collection<T> collection,
				UnsafeFunction<T, Boolean, E> unsafeFunction)
		throws E {

		return (boolean[])_unsafeTransformToPrimitiveArray(
			collection, unsafeFunction, boolean[].class);
	}

	public static <T, E extends Throwable> boolean[]
			unsafeTransformToBooleanArray(
				T[] array, UnsafeFunction<T, Boolean, E> unsafeFunction)
		throws E {

		return (boolean[])_unsafeTransformToPrimitiveArray(
			array, unsafeFunction, boolean[].class);
	}

	public static <T, E extends Throwable> byte[] unsafeTransformToByteArray(
			Collection<T> collection, UnsafeFunction<T, Byte, E> unsafeFunction)
		throws E {

		return (byte[])_unsafeTransformToPrimitiveArray(
			collection, unsafeFunction, byte[].class);
	}

	public static <T, E extends Throwable> byte[] unsafeTransformToByteArray(
			T[] array, UnsafeFunction<T, Byte, E> unsafeFunction)
		throws E {

		return (byte[])_unsafeTransformToPrimitiveArray(
			array, unsafeFunction, byte[].class);
	}

	public static <T, E extends Throwable> double[]
			unsafeTransformToDoubleArray(
				Collection<T> collection,
				UnsafeFunction<T, Double, E> unsafeFunction)
		throws E {

		return (double[])_unsafeTransformToPrimitiveArray(
			collection, unsafeFunction, double[].class);
	}

	public static <T, E extends Throwable> double[]
			unsafeTransformToDoubleArray(
				T[] array, UnsafeFunction<T, Double, E> unsafeFunction)
		throws E {

		return (double[])_unsafeTransformToPrimitiveArray(
			array, unsafeFunction, double[].class);
	}

	public static <T, E extends Throwable> float[] unsafeTransformToFloatArray(
			Collection<T> collection,
			UnsafeFunction<T, Float, E> unsafeFunction)
		throws E {

		return (float[])_unsafeTransformToPrimitiveArray(
			collection, unsafeFunction, float[].class);
	}

	public static <T, E extends Throwable> float[] unsafeTransformToFloatArray(
			T[] array, UnsafeFunction<T, Float, E> unsafeFunction)
		throws E {

		return (float[])_unsafeTransformToPrimitiveArray(
			array, unsafeFunction, float[].class);
	}

	public static <T, R, E extends Throwable> int[] unsafeTransformToIntArray(
			Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction)
		throws E {

		return (int[])_unsafeTransformToPrimitiveArray(
			collection, unsafeFunction, int[].class);
	}

	public static <T, E extends Throwable> int[] unsafeTransformToIntArray(
			T[] array, UnsafeFunction<T, Integer, E> unsafeFunction)
		throws E {

		return (int[])_unsafeTransformToPrimitiveArray(
			array, unsafeFunction, int[].class);
	}

	public static <R, E extends Throwable> List<R> unsafeTransformToList(
			int[] array, UnsafeFunction<Integer, R, E> unsafeFunction)
		throws E {

		if (array == null) {
			return new ArrayList<>();
		}

		List<R> list = new ArrayList<>(array.length);

		for (Integer item : array) {
			R newItem = unsafeFunction.apply(item);

			if (newItem == null) {
				continue;
			}

			list.add(newItem);
		}

		return list;
	}

	public static <R, E extends Throwable> List<R> unsafeTransformToList(
			long[] array, UnsafeFunction<Long, R, E> unsafeFunction)
		throws E {

		if (array == null) {
			return new ArrayList<>();
		}

		List<R> list = new ArrayList<>(array.length);

		for (Long item : array) {
			R newItem = unsafeFunction.apply(item);

			if (newItem == null) {
				continue;
			}

			list.add(newItem);
		}

		return list;
	}

	public static <T, R, E extends Throwable> List<R> unsafeTransformToList(
			T[] array, UnsafeFunction<T, R, E> unsafeFunction)
		throws E {

		if (array == null) {
			return new ArrayList<>();
		}

		List<R> list = new ArrayList<>(array.length);

		for (T item : array) {
			R newItem = unsafeFunction.apply(item);

			if (newItem == null) {
				continue;
			}

			list.add(newItem);
		}

		return list;
	}

	public static <T, R, E extends Throwable> long[] unsafeTransformToLongArray(
			Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction)
		throws E {

		return (long[])_unsafeTransformToPrimitiveArray(
			collection, unsafeFunction, long[].class);
	}

	public static <T, E extends Throwable> long[] unsafeTransformToLongArray(
			T[] array, UnsafeFunction<T, Long, E> unsafeFunction)
		throws E {

		return (long[])_unsafeTransformToPrimitiveArray(
			array, unsafeFunction, long[].class);
	}

	public static <T, E extends Throwable> short[] unsafeTransformToShortArray(
			Collection<T> collection,
			UnsafeFunction<T, Short, E> unsafeFunction)
		throws E {

		return (short[])_unsafeTransformToPrimitiveArray(
			collection, unsafeFunction, short[].class);
	}

	public static <T, E extends Throwable> short[] unsafeTransformToShortArray(
			T[] array, UnsafeFunction<T, Short, E> unsafeFunction)
		throws E {

		return (short[])_unsafeTransformToPrimitiveArray(
			array, unsafeFunction, short[].class);
	}

	private static <T, R, E extends Throwable> Object
			_unsafeTransformToPrimitiveArray(
				Collection<T> collection,
				UnsafeFunction<T, R, E> unsafeFunction, Class<?> clazz)
		throws E {

		List<R> list = unsafeTransform(collection, unsafeFunction);

		Object array = clazz.cast(
			Array.newInstance(clazz.getComponentType(), list.size()));

		for (int i = 0; i < list.size(); i++) {
			Array.set(array, i, list.get(i));
		}

		return array;
	}

	private static <T, R, E extends Throwable> Object
			_unsafeTransformToPrimitiveArray(
				T[] array1, UnsafeFunction<T, R, E> unsafeFunction,
				Class<?> clazz)
		throws E {

		Object array2 = clazz.cast(
			Array.newInstance(clazz.getComponentType(), array1.length));

		for (int i = 0; i < array1.length; i++) {
			Array.set(array2, i, unsafeFunction.apply(array1[i]));
		}

		return array2;
	}

}