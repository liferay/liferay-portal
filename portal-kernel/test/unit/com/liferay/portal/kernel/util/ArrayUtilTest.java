/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.test.AssertUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Eudaldo Alonso
 * @author Roberto Díaz
 */
public class ArrayUtilTest {

	@Test
	public void testAppend() {
		Assert.assertArrayEquals(
			new boolean[] {true, false, true},
			ArrayUtil.append(new boolean[] {true, false}, true));
		Assert.assertArrayEquals(
			new boolean[] {true, false, false, false},
			ArrayUtil.append(
				new boolean[] {true, false}, new boolean[] {false, false}));
		Assert.assertArrayEquals(
			new byte[] {1, 2, 3, 4},
			ArrayUtil.append(new byte[] {1, 2, 3}, (byte)4));
		Assert.assertArrayEquals(
			new byte[] {1, 2, 3, 4, 5, 6},
			ArrayUtil.append(new byte[] {1, 2, 3}, new byte[] {4, 5, 6}));
		Assert.assertArrayEquals(
			new char[] {'a', 'b', 'c', 'd'},
			ArrayUtil.append(new char[] {'a', 'b', 'c'}, 'd'));
		Assert.assertArrayEquals(
			new char[] {'a', 'b', 'c', 'd', 'e', 'f'},
			ArrayUtil.append(
				new char[] {'a', 'b', 'c'}, new char[] {'d', 'e', 'f'}));
		Assert.assertArrayEquals(
			new double[] {1.0, 2.0, 3.0, 4.0},
			ArrayUtil.append(new double[] {1.0, 2.0, 3.0}, 4.0), 0.0001);
		Assert.assertArrayEquals(
			new double[] {1.0, 2.0, 3.0, 4.0, 5.0, 6.0},
			ArrayUtil.append(
				new double[] {1.0, 2.0, 3.0}, new double[] {4.0, 5.0, 6.0}),
			0.0001);
		Assert.assertArrayEquals(
			new float[] {1.0F, 2.0F, 3.0F, 4.0F},
			ArrayUtil.append(new float[] {1.0F, 2.0F, 3.0F}, 4.0F), 0.0001F);
		Assert.assertArrayEquals(
			new float[] {1.0F, 2.0F, 3.0F, 4.0F, 5.0F, 6.0F},
			ArrayUtil.append(
				new float[] {1.0F, 2.0F, 3.0F}, new float[] {4.0F, 5.0F, 6.0F}),
			0.0001F);
		Assert.assertArrayEquals(
			new int[] {1, 2, 3, 4}, ArrayUtil.append(new int[] {1, 2, 3}, 4));
		Assert.assertArrayEquals(
			new int[] {1, 2, 3, 4, 5, 6},
			ArrayUtil.append(new int[] {1, 2, 3}, new int[] {4, 5, 6}));
		Assert.assertArrayEquals(
			new long[] {1L, 2L, 3L, 4L},
			ArrayUtil.append(new long[] {1L, 2L, 3L}, 4L));
		Assert.assertArrayEquals(
			new long[] {1L, 2L, 3L, 4L, 5L, 6L},
			ArrayUtil.append(new long[] {1L, 2L, 3L}, new long[] {4L, 5L, 6L}));
		Assert.assertArrayEquals(
			new short[] {1, 2, 3, 4},
			ArrayUtil.append(new short[] {1, 2, 3}, (short)4));
		Assert.assertArrayEquals(
			new short[] {1, 2, 3, 4, 5, 6},
			ArrayUtil.append(new short[] {1, 2, 3}, new short[] {4, 5, 6}));
		Assert.assertArrayEquals(
			new Integer[] {1, 2, 3, 4, 5, 6},
			ArrayUtil.append(
				new Integer[] {1, 2}, new Integer[] {3, 4},
				new Integer[] {5, 6}));
		Assert.assertArrayEquals(
			new Integer[] {1, 2, 3, 4},
			ArrayUtil.append(new Integer[] {1, 2, 3}, 4));
		Assert.assertArrayEquals(
			new Integer[] {1, 2, 3, 4, 5, 6},
			ArrayUtil.append(new Integer[] {1, 2, 3}, new Integer[] {4, 5, 6}));
		Assert.assertArrayEquals(
			new Integer[][] {new Integer[] {1, 2, 3}, new Integer[] {4, 5, 6}},
			ArrayUtil.append(
				new Integer[][] {new Integer[] {1, 2, 3}},
				new Integer[] {4, 5, 6}));
		Assert.assertArrayEquals(
			new Integer[][] {new Integer[] {1, 2, 3}, new Integer[] {4, 5, 6}},
			ArrayUtil.append(
				new Integer[][] {new Integer[] {1, 2, 3}},
				new Integer[][] {new Integer[] {4, 5, 6}}));
	}

	@Test
	public void testContainsAllBooleanArray() throws Exception {
		boolean[] array1 = {true};
		boolean[] array2 = {true, false};

		Assert.assertFalse(ArrayUtil.containsAll(array1, array2));
		Assert.assertTrue(ArrayUtil.containsAll(array2, array1));
	}

	@Test
	public void testContainsAllByteArray() throws Exception {
		byte[] array1 = {1, 2};
		byte[] array2 = {1, 2, 3};

		Assert.assertFalse(ArrayUtil.containsAll(array1, array2));
		Assert.assertTrue(ArrayUtil.containsAll(array2, array1));
	}

	@Test
	public void testContainsAllCharArray() throws Exception {
		char[] array1 = {'a', 'b'};
		char[] array2 = {'a', 'b', 'c'};

		Assert.assertFalse(ArrayUtil.containsAll(array1, array2));
		Assert.assertTrue(ArrayUtil.containsAll(array2, array1));
	}

	@Test
	public void testContainsAllDoubleArray() throws Exception {
		double[] array1 = {1.5D, 2.5D};
		double[] array2 = {1.5D, 2.5D, 3.5D};

		Assert.assertFalse(ArrayUtil.containsAll(array1, array2));
		Assert.assertTrue(ArrayUtil.containsAll(array2, array1));
	}

	@Test
	public void testContainsAllFloatArray() throws Exception {
		float[] array1 = {1.5F, 2.5F};
		float[] array2 = {1.5F, 2.5F, 3.5F};

		Assert.assertFalse(ArrayUtil.containsAll(array1, array2));
		Assert.assertTrue(ArrayUtil.containsAll(array2, array1));
	}

	@Test
	public void testContainsAllIntArray() throws Exception {
		int[] array1 = {1, 2};
		int[] array2 = {1, 2, 3};

		Assert.assertFalse(ArrayUtil.containsAll(array1, array2));
		Assert.assertTrue(ArrayUtil.containsAll(array2, array1));
	}

	@Test
	public void testContainsAllLongArray() throws Exception {
		long[] array1 = {1L, 2L};
		long[] array2 = {1L, 2L, 3L};

		Assert.assertFalse(ArrayUtil.containsAll(array1, array2));
		Assert.assertTrue(ArrayUtil.containsAll(array2, array1));
	}

	@Test
	public void testContainsAllShortArray() throws Exception {
		short[] array1 = {1, 2};
		short[] array2 = {1, 2, 3};

		Assert.assertFalse(ArrayUtil.containsAll(array1, array2));
		Assert.assertTrue(ArrayUtil.containsAll(array2, array1));
	}

	@Test
	public void testContainsAllUserArray() throws Exception {
		User brian = new User("brian", 20);
		User julio = new User("julio", 20);
		User sergio = new User("sergio", 20);

		User[] array1 = {julio, sergio};
		User[] array2 = {brian, julio, sergio};

		Assert.assertFalse(ArrayUtil.containsAll(array1, array2));
		Assert.assertTrue(ArrayUtil.containsAll(array2, array1));
	}

	@Test
	public void testContainsBooleanArray() throws Exception {
		boolean[] array1 = {true, true};

		Assert.assertFalse(ArrayUtil.contains(array1, false));
		Assert.assertTrue(ArrayUtil.contains(array1, true));
	}

	@Test
	public void testContainsByteArray() throws Exception {
		byte[] array = {2, 3};

		Assert.assertFalse(ArrayUtil.contains(array, (byte)1));
		Assert.assertTrue(ArrayUtil.contains(array, (byte)2));
	}

	@Test
	public void testContainsCharArray() throws Exception {
		char[] array = {'a', 'b'};

		Assert.assertFalse(ArrayUtil.contains(array, 'C'));
		Assert.assertTrue(ArrayUtil.contains(array, 'a'));
	}

	@Test
	public void testContainsDoubleArray() throws Exception {
		double[] array = {2.5D, 3.5D};

		Assert.assertFalse(ArrayUtil.contains(array, 1.5D));
		Assert.assertTrue(ArrayUtil.contains(array, 2.5D));
	}

	@Test
	public void testContainsFloatArray() throws Exception {
		float[] array = {2.5F, 3.5F};

		Assert.assertFalse(ArrayUtil.contains(array, 1.5F));
		Assert.assertTrue(ArrayUtil.contains(array, 2.5F));
	}

	@Test
	public void testContainsIntArray() throws Exception {
		int[] array = {2, 3};

		Assert.assertFalse(ArrayUtil.contains(array, 1));
		Assert.assertTrue(ArrayUtil.contains(array, 2));
	}

	@Test
	public void testContainsLongArray() throws Exception {
		long[] array = {2L, 3L};

		Assert.assertFalse(ArrayUtil.contains(array, 1L));
		Assert.assertTrue(ArrayUtil.contains(array, 2L));
	}

	@Test
	public void testContainsShortArray() throws Exception {
		short[] array = {2, 3};

		Assert.assertFalse(ArrayUtil.contains(array, (short)1));
		Assert.assertTrue(ArrayUtil.contains(array, (short)2));
	}

	@Test
	public void testContainsStringArray() throws Exception {
		String[] array = {"a", "b", null};

		Assert.assertFalse(ArrayUtil.contains(array, "c", true));
		Assert.assertFalse(ArrayUtil.contains(array, "C", false));
		Assert.assertTrue(ArrayUtil.contains(array, "a", true));
		Assert.assertTrue(ArrayUtil.contains(array, "a", false));
		Assert.assertTrue(ArrayUtil.contains(array, "A", true));
		Assert.assertTrue(ArrayUtil.contains(array, null, true));
		Assert.assertTrue(ArrayUtil.contains(array, null, false));
	}

	@Test
	public void testContainsUserArray() throws Exception {
		User brian = new User("brian", 20);

		User julio = new User("julio", 20);
		User sergio = new User("sergio", 20);

		User[] array = {julio, sergio, null};

		Assert.assertFalse(ArrayUtil.contains(array, brian));
		Assert.assertTrue(ArrayUtil.contains(array, julio));
		Assert.assertTrue(ArrayUtil.contains(array, null));
	}

	@Test
	public void testCountStringArray() {
		Assert.assertEquals(
			1,
			ArrayUtil.count(new String[] {"a", "b", "c"}, s -> s.equals("b")));
	}

	@Test
	public void testCountStringEmptyArray() {
		Assert.assertEquals(0, ArrayUtil.count(new String[0], s -> true));
	}

	@Test
	public void testCountStringNullArray() {
		String[] array = null;

		Assert.assertEquals(0, ArrayUtil.count(array, s -> true));
	}

	@Test
	public void testEqualsIgnoreCase() {
		Assert.assertFalse(
			ArrayUtil.equalsIgnoreCase(
				new String[] {"A", "A"}, new String[] {"B", "B"}));
		Assert.assertTrue(
			ArrayUtil.equalsIgnoreCase(
				new String[] {"A", "B"}, new String[] {"A", "B"}));
		Assert.assertTrue(
			ArrayUtil.equalsIgnoreCase(
				new String[] {"a", "A"}, new String[] {"A", "a"}));
		Assert.assertTrue(
			ArrayUtil.equalsIgnoreCase(
				new String[] {"a", "b"}, new String[] {"a", "b"}));
	}

	@Test
	public void testFilterDoubleArray() {
		double[] array = ArrayUtil.filter(
			new double[] {0.1, 0.2, 1.2, 1.3}, _doublePredicate);

		Assert.assertEquals(Arrays.toString(array), 2, array.length);
		AssertUtils.assertEquals(new double[] {1.2, 1.3}, array);
	}

	@Test
	public void testFilterDoubleEmptyArray() {
		double[] array = ArrayUtil.filter(new double[0], _doublePredicate);

		Assert.assertEquals(Arrays.toString(array), 0, array.length);
		AssertUtils.assertEquals(new double[0], array);
	}

	@Test
	public void testFilterDoubleNullArray() {
		double[] array = null;

		double[] filteredArray = ArrayUtil.filter(array, _doublePredicate);

		Assert.assertNull(filteredArray);
	}

	@Test
	public void testFilterIntegerArray() {
		int[] array = ArrayUtil.filter(
			new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, _integerPredicate);

		Assert.assertEquals(Arrays.toString(array), 5, array.length);
		Assert.assertArrayEquals(new int[] {5, 6, 7, 8, 9}, array);
	}

	@Test
	public void testFilterIntegerEmptyArray() {
		int[] array = ArrayUtil.filter(new int[0], _integerPredicate);

		Assert.assertEquals(Arrays.toString(array), 0, array.length);
		Assert.assertArrayEquals(new int[0], array);
	}

	@Test
	public void testFilterIntegerNullArray() {
		int[] array = null;

		int[] filteredArray = ArrayUtil.filter(array, _integerPredicate);

		Assert.assertNull(filteredArray);
	}

	@Test
	public void testFilterUserArray() {
		User[] array = ArrayUtil.filter(
			new User[] {new User("james", 17), new User("john", 26)},
			_userPredicate);

		Assert.assertEquals(Arrays.toString(array), 1, array.length);

		Assert.assertEquals("john", array[0].getName());
		Assert.assertEquals(26, array[0].getAge());
	}

	@Test
	public void testFilterUserEmptyArray() {
		User[] array = ArrayUtil.filter(new User[0], _userPredicate);

		Assert.assertEquals(Arrays.toString(array), 0, array.length);
	}

	@Test
	public void testFilterUserNullArray() {
		User[] array = ArrayUtil.filter(null, _userPredicate);

		Assert.assertNull(array);
	}

	@Test
	public void testIsEmptyBooleanArray() {
		Assert.assertTrue(ArrayUtil.isEmpty((boolean[])null));
		Assert.assertTrue(ArrayUtil.isEmpty(new boolean[0]));
		Assert.assertFalse(ArrayUtil.isEmpty(new boolean[] {true, true}));
	}

	@Test
	public void testIsEmptyByteArray() {
		Assert.assertTrue(ArrayUtil.isEmpty((byte[])null));
		Assert.assertTrue(ArrayUtil.isEmpty(new byte[0]));
		Assert.assertFalse(ArrayUtil.isEmpty(new byte[] {1, 2}));
	}

	@Test
	public void testIsEmptyCharArray() {
		Assert.assertTrue(ArrayUtil.isEmpty((char[])null));
		Assert.assertTrue(ArrayUtil.isEmpty(new char[0]));
		Assert.assertFalse(ArrayUtil.isEmpty(new char[] {1, 2}));
	}

	@Test
	public void testIsEmptyDoubleArray() {
		Assert.assertTrue(ArrayUtil.isEmpty((double[])null));
		Assert.assertTrue(ArrayUtil.isEmpty(new double[0]));
		Assert.assertFalse(ArrayUtil.isEmpty(new double[] {1, 2}));
	}

	@Test
	public void testIsEmptyFloatArray() {
		Assert.assertTrue(ArrayUtil.isEmpty((float[])null));
		Assert.assertTrue(ArrayUtil.isEmpty(new float[0]));
		Assert.assertFalse(ArrayUtil.isEmpty(new float[] {1, 2}));
	}

	@Test
	public void testIsEmptyIntArray() {
		Assert.assertTrue(ArrayUtil.isEmpty((int[])null));
		Assert.assertTrue(ArrayUtil.isEmpty(new int[0]));
		Assert.assertFalse(ArrayUtil.isEmpty(new int[] {1, 2}));
	}

	@Test
	public void testIsEmptyLongArray() {
		Assert.assertTrue(ArrayUtil.isEmpty((long[])null));
		Assert.assertTrue(ArrayUtil.isEmpty(new long[0]));
		Assert.assertFalse(ArrayUtil.isEmpty(new long[] {1, 2}));
	}

	@Test
	public void testIsEmptyShortArray() {
		Assert.assertTrue(ArrayUtil.isEmpty((short[])null));
		Assert.assertTrue(ArrayUtil.isEmpty(new short[0]));
		Assert.assertFalse(ArrayUtil.isEmpty(new short[] {1, 2}));
	}

	@Test
	public void testIsEmptyUserArray() {
		Assert.assertTrue(ArrayUtil.isEmpty((User[])null));
		Assert.assertTrue(ArrayUtil.isEmpty(new User[0]));
		Assert.assertFalse(
			ArrayUtil.isEmpty(
				new User[] {
					new User("brian", 20), new User("julio", 20),
					new User("sergio", 20)
				}));
	}

	@Test
	public void testIsNotEmptyBooleanArray() {
		Assert.assertFalse(ArrayUtil.isNotEmpty((boolean[])null));
		Assert.assertFalse(ArrayUtil.isNotEmpty(new boolean[0]));
		Assert.assertTrue(ArrayUtil.isNotEmpty(new boolean[] {true, true}));
	}

	@Test
	public void testIsNotEmptyByteArray() {
		Assert.assertFalse(ArrayUtil.isNotEmpty((byte[])null));
		Assert.assertFalse(ArrayUtil.isNotEmpty(new byte[0]));
		Assert.assertTrue(ArrayUtil.isNotEmpty(new byte[] {1, 2}));
	}

	@Test
	public void testIsNotEmptyCharArray() {
		Assert.assertFalse(ArrayUtil.isNotEmpty((char[])null));
		Assert.assertFalse(ArrayUtil.isNotEmpty(new char[0]));
		Assert.assertTrue(ArrayUtil.isNotEmpty(new char[] {1, 2}));
	}

	@Test
	public void testIsNotEmptyDoubleArray() {
		Assert.assertFalse(ArrayUtil.isNotEmpty((double[])null));
		Assert.assertFalse(ArrayUtil.isNotEmpty(new double[0]));
		Assert.assertTrue(ArrayUtil.isNotEmpty(new double[] {1, 2}));
	}

	@Test
	public void testIsNotEmptyFloatArray() {
		Assert.assertFalse(ArrayUtil.isNotEmpty((float[])null));
		Assert.assertFalse(ArrayUtil.isNotEmpty(new float[0]));
		Assert.assertTrue(ArrayUtil.isNotEmpty(new float[] {1, 2}));
	}

	@Test
	public void testIsNotEmptyIntArray() {
		Assert.assertFalse(ArrayUtil.isNotEmpty((int[])null));
		Assert.assertFalse(ArrayUtil.isNotEmpty(new int[0]));
		Assert.assertTrue(ArrayUtil.isNotEmpty(new int[] {1, 2}));
	}

	@Test
	public void testIsNotEmptyLongArray() {
		Assert.assertFalse(ArrayUtil.isNotEmpty((long[])null));
		Assert.assertFalse(ArrayUtil.isNotEmpty(new long[0]));
		Assert.assertTrue(ArrayUtil.isNotEmpty(new long[] {1, 2}));
	}

	@Test
	public void testIsNotEmptyShortArray() {
		Assert.assertFalse(ArrayUtil.isNotEmpty((short[])null));
		Assert.assertFalse(ArrayUtil.isNotEmpty(new short[0]));
		Assert.assertTrue(ArrayUtil.isNotEmpty(new short[] {1, 2}));
	}

	@Test
	public void testIsNotEmptyUserArray() {
		Assert.assertFalse(ArrayUtil.isNotEmpty((User[])null));
		Assert.assertFalse(ArrayUtil.isNotEmpty(new User[0]));
		Assert.assertTrue(
			ArrayUtil.isNotEmpty(
				new User[] {
					new User("brian", 20), new User("julio", 20),
					new User("sergio", 20)
				}));
	}

	@Test
	public void testRemoveFromBooleanArray() {
		boolean[] array = {true, true, false};

		array = ArrayUtil.remove(array, false);

		Assert.assertArrayEquals(new boolean[] {true, true}, array);
	}

	@Test
	public void testRemoveFromBooleanEmptyArray() {
		boolean[] array = {};

		array = ArrayUtil.remove(array, false);

		Assert.assertTrue(ArrayUtil.isEmpty(array));
	}

	@Test
	public void testRemoveFromBooleanNullArray() {
		boolean[] array = null;

		array = ArrayUtil.remove(array, false);

		Assert.assertNull(array);
	}

	@Test
	public void testRemoveFromByteArray() {
		byte[] array = {1, 2, 3};

		array = ArrayUtil.remove(array, (byte)3);

		Assert.assertArrayEquals(new byte[] {1, 2}, array);
	}

	@Test
	public void testRemoveFromByteEmptyArray() {
		byte[] array = {};

		array = ArrayUtil.remove(array, (byte)3);

		Assert.assertTrue(ArrayUtil.isEmpty(array));
	}

	@Test
	public void testRemoveFromByteNullArray() {
		byte[] array = null;

		array = ArrayUtil.remove(array, (byte)3);

		Assert.assertNull(array);
	}

	@Test
	public void testRemoveFromCharArray() {
		char[] array = {'a', 'b', 'c'};

		array = ArrayUtil.remove(array, 'c');

		Assert.assertArrayEquals(new char[] {'a', 'b'}, array);
	}

	@Test
	public void testRemoveFromCharEmptyArray() {
		char[] array = {};

		array = ArrayUtil.remove(array, 'c');

		Assert.assertTrue(ArrayUtil.isEmpty(array));
	}

	@Test
	public void testRemoveFromCharNullArray() {
		char[] array = null;

		array = ArrayUtil.remove(array, 'c');

		Assert.assertNull(array);
	}

	@Test
	public void testRemoveFromDoubleArray() {
		double[] array = {1.0D, 2.0D, 3.0D};

		array = ArrayUtil.remove(array, 3.0D);

		Assert.assertArrayEquals(new double[] {1.0D, 2.0D}, array, 0);
	}

	@Test
	public void testRemoveFromDoubleEmptyArray() {
		double[] array = {};

		array = ArrayUtil.remove(array, 3.0D);

		Assert.assertTrue(ArrayUtil.isEmpty(array));
	}

	@Test
	public void testRemoveFromDoubleNullArray() {
		double[] array = null;

		array = ArrayUtil.remove(array, 3.0D);

		Assert.assertNull(array);
	}

	@Test
	public void testRemoveFromFloatArray() {
		float[] array = {1.5F, 2.5F, 3.5F};

		array = ArrayUtil.remove(array, 3.5F);

		Assert.assertArrayEquals(new float[] {1.5F, 2.5F}, array, 0);
	}

	@Test
	public void testRemoveFromFloatEmptyArray() {
		float[] array = {};

		array = ArrayUtil.remove(array, 3.5F);

		Assert.assertTrue(ArrayUtil.isEmpty(array));
	}

	@Test
	public void testRemoveFromFloatNullArray() {
		float[] array = null;

		array = ArrayUtil.remove(array, 3.5F);

		Assert.assertNull(array);
	}

	@Test
	public void testRemoveFromIntArray() {
		int[] array = {1, 2, 3};

		array = ArrayUtil.remove(array, (byte)3);

		Assert.assertArrayEquals(new int[] {1, 2}, array);
	}

	@Test
	public void testRemoveFromIntEmptyArray() {
		int[] array = {};

		array = ArrayUtil.remove(array, (byte)3);

		Assert.assertTrue(ArrayUtil.isEmpty(array));
	}

	@Test
	public void testRemoveFromIntNullArray() {
		int[] array = null;

		array = ArrayUtil.remove(array, (byte)3);

		Assert.assertNull(array);
	}

	@Test
	public void testRemoveFromLongArray() {
		long[] array = {1L, 2L, 3L};

		array = ArrayUtil.remove(array, 3L);

		Assert.assertArrayEquals(new long[] {1L, 2L}, array);
	}

	@Test
	public void testRemoveFromLongEmptyArray() {
		long[] array = {};

		array = ArrayUtil.remove(array, 3L);

		Assert.assertTrue(ArrayUtil.isEmpty(array));
	}

	@Test
	public void testRemoveFromLongNullArray() {
		long[] array = null;

		array = ArrayUtil.remove(array, 3L);

		Assert.assertNull(array);
	}

	@Test
	public void testRemoveFromShortArray() {
		short[] array = {1, 2, 3};

		array = ArrayUtil.remove(array, (short)3);

		Assert.assertArrayEquals(new short[] {1, 2}, array);
	}

	@Test
	public void testRemoveFromShortEmptyArray() {
		short[] array = {};

		array = ArrayUtil.remove(array, (short)3);

		Assert.assertTrue(ArrayUtil.isEmpty(array));
	}

	@Test
	public void testRemoveFromShortNullArray() {
		short[] array = null;

		array = ArrayUtil.remove(array, (short)3);

		Assert.assertNull(array);
	}

	@Test
	public void testRemoveFromStringArray() {
		String[] array = {"a", "b", "c"};

		array = ArrayUtil.remove(array, "c");

		Assert.assertArrayEquals(new String[] {"a", "b"}, array);
	}

	@Test
	public void testRemoveFromStringEmptyArray() {
		String[] array = {};

		array = ArrayUtil.remove(array, "c");

		Assert.assertTrue(ArrayUtil.isEmpty(array));
	}

	@Test
	public void testRemoveFromStringNullArray() {
		String[] array = null;

		array = ArrayUtil.remove(array, "c");

		Assert.assertNull(array);
	}

	@Test
	public void testReverseBooleanArray() throws Exception {
		boolean[] array = {true, true, false};

		ArrayUtil.reverse(array);

		Assert.assertFalse(array[0]);
		Assert.assertTrue(array[1]);
		Assert.assertTrue(array[2]);
	}

	@Test
	public void testReverseCharArray() throws Exception {
		char[] array = {'a', 'b', 'c'};

		ArrayUtil.reverse(array);

		Assert.assertArrayEquals(new char[] {'c', 'b', 'a'}, array);
	}

	@Test
	public void testReverseDoubleArray() throws Exception {
		double[] array = {111.0, 222.0, 333.0};

		ArrayUtil.reverse(array);

		Assert.assertArrayEquals(new double[] {333.0, 222.0, 111.0}, array, 0);
	}

	@Test
	public void testReverseIntArray() throws Exception {
		int[] array = {111, 222, 333};

		ArrayUtil.reverse(array);

		Assert.assertArrayEquals(new int[] {333, 222, 111}, array);
	}

	@Test
	public void testReverseLongArray() throws Exception {
		long[] array = {111, 222, 333};

		ArrayUtil.reverse(array);

		Assert.assertArrayEquals(new long[] {333, 222, 111}, array);
	}

	@Test
	public void testReverseShortArray() throws Exception {
		short[] array = {111, 222, 333};

		ArrayUtil.reverse(array);

		Assert.assertArrayEquals(new short[] {333, 222, 111}, array);
	}

	@Test
	public void testReverseStringArray() throws Exception {
		String[] array = {"aaa", "bbb", "ccc"};

		ArrayUtil.reverse(array);

		Assert.assertArrayEquals(new String[] {"ccc", "bbb", "aaa"}, array);
	}

	@Test
	public void testSoredUnique() {
		byte[] bytes = {2, 3, 1};

		byte[] sortedUniqueBytes = ArrayUtil.sortedUnique(bytes);

		Assert.assertSame(bytes, sortedUniqueBytes);
		Assert.assertArrayEquals(new byte[] {1, 2, 3}, sortedUniqueBytes);

		bytes = new byte[] {2, 3, 1, 2, 3, 3, 1};

		sortedUniqueBytes = ArrayUtil.sortedUnique(bytes);

		Assert.assertNotSame(bytes, sortedUniqueBytes);
		Assert.assertArrayEquals(new byte[] {1, 2, 3}, sortedUniqueBytes);

		double[] doubles = {2.0, 3.0, 1.0};

		double[] sortedUniqueDoubles = ArrayUtil.sortedUnique(doubles);

		Assert.assertSame(doubles, sortedUniqueDoubles);
		Assert.assertArrayEquals(
			new double[] {1.0, 2.0, 3.0}, sortedUniqueDoubles, 0.0001);

		doubles = new double[] {2.0, 3.0, 1.0, 2.0, 3.0, 3.0, 1.0};

		sortedUniqueDoubles = ArrayUtil.sortedUnique(doubles);

		Assert.assertNotSame(doubles, sortedUniqueDoubles);
		Assert.assertArrayEquals(
			new double[] {1.0, 2.0, 3.0}, sortedUniqueDoubles, 0.0001);

		float[] floats = {2.0F, 3.0F, 1.0F};

		float[] sortedUniqueFloats = ArrayUtil.sortedUnique(floats);

		Assert.assertSame(floats, sortedUniqueFloats);
		Assert.assertArrayEquals(
			new float[] {1.0F, 2.0F, 3.0F}, sortedUniqueFloats, 0.0001F);

		floats = new float[] {2.0F, 3.0F, 1.0F, 2.0F, 3.0F, 3.0F, 1.0F};

		sortedUniqueFloats = ArrayUtil.sortedUnique(floats);

		Assert.assertNotSame(floats, sortedUniqueFloats);
		Assert.assertArrayEquals(
			new float[] {1.0F, 2.0F, 3.0F}, sortedUniqueFloats, 0.0001F);

		int[] ints = {2, 3, 1};

		int[] sortedUniqueInts = ArrayUtil.sortedUnique(ints);

		Assert.assertSame(ints, sortedUniqueInts);
		Assert.assertArrayEquals(new int[] {1, 2, 3}, sortedUniqueInts);

		ints = new int[] {2, 3, 1, 2, 3, 3, 1};

		sortedUniqueInts = ArrayUtil.sortedUnique(ints);

		Assert.assertNotSame(ints, sortedUniqueInts);
		Assert.assertArrayEquals(new int[] {1, 2, 3}, sortedUniqueInts);

		long[] longs = {2, 3, 1};

		long[] sortedUniqueLongs = ArrayUtil.sortedUnique(longs);

		Assert.assertSame(longs, sortedUniqueLongs);
		Assert.assertArrayEquals(new long[] {1, 2, 3}, sortedUniqueLongs);

		longs = new long[] {2, 3, 1, 2, 3, 3, 1};

		sortedUniqueLongs = ArrayUtil.sortedUnique(longs);

		Assert.assertNotSame(longs, sortedUniqueLongs);
		Assert.assertArrayEquals(new long[] {1, 2, 3}, sortedUniqueLongs);

		short[] shorts = {2, 3, 1};

		short[] sortedUniqueShorts = ArrayUtil.sortedUnique(shorts);

		Assert.assertSame(shorts, sortedUniqueShorts);
		Assert.assertArrayEquals(new short[] {1, 2, 3}, sortedUniqueShorts);

		shorts = new short[] {2, 3, 1, 2, 3, 3, 1};

		sortedUniqueShorts = ArrayUtil.sortedUnique(shorts);

		Assert.assertNotSame(shorts, sortedUniqueShorts);
		Assert.assertArrayEquals(new short[] {1, 2, 3}, sortedUniqueShorts);

		String[] strings = {"world", "hello"};

		String[] sortedUniqueStrings = ArrayUtil.sortedUnique(strings);

		Assert.assertSame(strings, sortedUniqueStrings);
		Assert.assertArrayEquals(
			new String[] {"hello", "world"}, sortedUniqueStrings);

		strings = new String[] {"world", "hello", null, "hello", null, "world"};

		sortedUniqueStrings = ArrayUtil.sortedUnique(strings);

		Assert.assertNotSame(strings, sortedUniqueStrings);
		Assert.assertArrayEquals(
			new String[] {"hello", "world", null}, sortedUniqueStrings);
	}

	@Test
	public void testSplitEmptyArray() {
		int[] array = new int[0];

		int[][] arraySplit = (int[][])ArrayUtil.split(array, 2);

		Assert.assertEquals(Arrays.toString(arraySplit), 0, arraySplit.length);
	}

	@Test
	public void testSplitEqualToSplitSize() {
		int[] array = {1, 2};

		int[][] arraySplit = (int[][])ArrayUtil.split(array, 2);

		Assert.assertEquals(Arrays.toString(arraySplit), 1, arraySplit.length);

		Assert.assertSame(array, arraySplit[0]);
	}

	@Test
	public void testSplitGreaterThanSplitSize() {
		int[] array = {1, 2};

		int[][] expected = {{1}, {2}};

		int[][] arraySplit = (int[][])ArrayUtil.split(array, 1);

		Assert.assertEquals(Arrays.toString(arraySplit), 2, arraySplit.length);

		for (int i = 0; i < arraySplit.length; i++) {
			Assert.assertArrayEquals(expected[i], arraySplit[i]);
		}
	}

	@Test
	public void testSplitLessThanSplitSize() {
		int[] array = {0, 1, 2, 3};

		int[][] arraySplit = (int[][])ArrayUtil.split(array, 5);

		Assert.assertEquals(Arrays.toString(arraySplit), 1, arraySplit.length);

		Assert.assertSame(array, arraySplit[0]);
	}

	@Test
	public void testSubset() {
		Assert.assertArrayEquals(
			new boolean[] {true, false},
			ArrayUtil.subset(new boolean[] {true, false, true}, 0, 2));
		Assert.assertArrayEquals(
			new byte[] {1, 2, 3},
			ArrayUtil.subset(new byte[] {1, 2, 3, 4}, 0, 3));
		Assert.assertArrayEquals(
			new char[] {'a', 'b', 'c'},
			ArrayUtil.subset(new char[] {'a', 'b', 'c', 'd'}, 0, 3));
		Assert.assertArrayEquals(
			new double[] {1.0, 2.0, 3.0},
			ArrayUtil.subset(new double[] {1.0, 2.0, 3.0, 4.0}, 0, 3), 0.0001);
		Assert.assertArrayEquals(
			new float[] {1.0F, 2.0F, 3.0F},
			ArrayUtil.subset(new float[] {1.0F, 2.0F, 3.0F, 4.0F}, 0, 3),
			0.0001F);
		Assert.assertArrayEquals(
			new int[] {1, 2, 3},
			ArrayUtil.subset(new int[] {1, 2, 3, 4}, 0, 3));
		Assert.assertArrayEquals(
			new long[] {1, 2, 3},
			ArrayUtil.subset(new long[] {1, 2, 3, 4}, 0, 3));
		Assert.assertArrayEquals(
			new short[] {1, 2, 3},
			ArrayUtil.subset(new short[] {1, 2, 3, 4}, 0, 3));
		Assert.assertArrayEquals(
			new Integer[] {1, 2, 3},
			ArrayUtil.subset(new Integer[] {1, 2, 3, 4}, 0, 3));
	}

	@Test
	public void testToDoubleArray() throws Exception {
		List<Double> list = new ArrayList<>();

		list.add(1.0);
		list.add(2.0);

		double[] array = ArrayUtil.toDoubleArray(list);

		Assert.assertEquals(list.toString(), array.length, list.size());

		for (int i = 0; i < list.size(); i++) {
			Double value = list.get(i);

			AssertUtils.assertEquals(value.doubleValue(), array[i]);
		}
	}

	@Test
	public void testToFloatArray() throws Exception {
		List<Float> list = new ArrayList<>();

		list.add(1.0F);
		list.add(2.0F);

		float[] array = ArrayUtil.toFloatArray(list);

		Assert.assertEquals(list.toString(), array.length, list.size());

		for (int i = 0; i < list.size(); i++) {
			Float value = list.get(i);

			AssertUtils.assertEquals(value.floatValue(), array[i]);
		}
	}

	@Test
	public void testToIntArray() throws Exception {
		List<Integer> list = new ArrayList<>();

		list.add(1);
		list.add(2);

		int[] array = ArrayUtil.toIntArray(list);

		Assert.assertEquals(list.toString(), array.length, list.size());

		for (int i = 0; i < list.size(); i++) {
			Integer value = list.get(i);

			Assert.assertEquals(value.intValue(), array[i]);
		}
	}

	@Test
	public void testToLongArray() throws Exception {
		List<Long> list = new ArrayList<>();

		list.add(1L);
		list.add(2L);

		long[] array = ArrayUtil.toLongArray(list);

		Assert.assertEquals(list.toString(), array.length, list.size());

		for (int i = 0; i < list.size(); i++) {
			Long value = list.get(i);

			Assert.assertEquals(value.longValue(), array[i]);
		}
	}

	@Test
	public void testUnique() {
		byte[] bytes = {1, 2, 3};

		Assert.assertArrayEquals(
			bytes, ArrayUtil.unique(new byte[] {1, 2, 3, 3, 2}));
		Assert.assertSame(bytes, ArrayUtil.unique(bytes));

		double[] doubles = {1.0, 2.0, 3.0};

		Assert.assertArrayEquals(
			doubles,
			ArrayUtil.unique(new double[] {1.0, 2.0, 3.0, 1.0, 2.0, 3.0}),
			0.0001);
		Assert.assertSame(doubles, ArrayUtil.unique(doubles));

		float[] floats = {1.0F, 2.0F, 3.0F};

		Assert.assertArrayEquals(
			floats,
			ArrayUtil.unique(new float[] {1.0F, 2.0F, 3.0F, 3.0F, 2.0F}),
			0.0001F);
		Assert.assertSame(floats, ArrayUtil.unique(floats));

		int[] ints = {1, 2, 3};

		Assert.assertArrayEquals(
			ints, ArrayUtil.unique(new int[] {1, 2, 3, 3, 2}));
		Assert.assertSame(ints, ArrayUtil.unique(ints));

		long[] longs = {1L, 2L, 3L};

		Assert.assertArrayEquals(
			longs, ArrayUtil.unique(new long[] {1L, 2L, 3L, 3L, 2L}));
		Assert.assertSame(longs, ArrayUtil.unique(longs));

		short[] shorts = {1, 2, 3};

		Assert.assertArrayEquals(
			shorts, ArrayUtil.unique(new short[] {1, 2, 3, 3, 2}));
		Assert.assertSame(shorts, ArrayUtil.unique(shorts));

		String[] strings = {"hello", null, "world"};

		Assert.assertArrayEquals(
			strings,
			ArrayUtil.unique(
				new String[] {"hello", null, "hello", null, "world", "world"}));
		Assert.assertSame(strings, ArrayUtil.unique(strings));
	}

	private final Predicate<Double> _doublePredicate = d -> d >= 1.1;
	private final Predicate<Integer> _integerPredicate = i -> i >= 5;
	private final Predicate<User> _userPredicate = user -> user.getAge() > 18;

	private static class User {

		public User(String name, int age) {
			_name = name;
			_age = age;
		}

		public int getAge() {
			return _age;
		}

		public String getName() {
			return _name;
		}

		private final int _age;
		private final String _name;

	}

}