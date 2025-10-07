/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.io;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import java.nio.ByteOrder;

/**
 * Encodes/decodes primitive types to/from big-endian byte sequences.
 *
 * @author Shuyang Zhou
 */
public class BigEndianCodec {

	public static boolean getBoolean(byte[] bytes, int index) {
		if (bytes[index] != 0) {
			return true;
		}

		return false;
	}

	public static char getChar(byte[] bytes, int index) {
		return (char)_charArrayVarHandle.get(bytes, index);
	}

	public static double getDouble(byte[] bytes, int index) {
		return Double.longBitsToDouble(getLong(bytes, index));
	}

	public static float getFloat(byte[] bytes, int index) {
		return Float.intBitsToFloat(getInt(bytes, index));
	}

	public static int getInt(byte[] bytes, int index) {
		return (int)_intArrayVarHandle.get(bytes, index);
	}

	public static long getLong(byte[] bytes, int index) {
		return (long)_longArrayVarHandle.get(bytes, index);
	}

	public static short getShort(byte[] bytes, int index) {
		return (short)_shortArrayVarHandle.get(bytes, index);
	}

	public static void putBoolean(byte[] bytes, int index, boolean b) {
		bytes[index] = (byte)(b ? 1 : 0);
	}

	public static void putChar(byte[] bytes, int index, char c) {
		_charArrayVarHandle.set(bytes, index, c);
	}

	public static void putDouble(byte[] bytes, int index, double d) {
		putLong(bytes, index, Double.doubleToLongBits(d));
	}

	public static void putFloat(byte[] bytes, int index, float f) {
		putInt(bytes, index, Float.floatToIntBits(f));
	}

	public static void putInt(byte[] bytes, int index, int i) {
		_intArrayVarHandle.set(bytes, index, i);
	}

	public static void putLong(byte[] bytes, int index, long l) {
		_longArrayVarHandle.set(bytes, index, l);
	}

	public static void putShort(byte[] bytes, int index, short s) {
		_shortArrayVarHandle.set(bytes, index, s);
	}

	private static final VarHandle _charArrayVarHandle =
		MethodHandles.byteArrayViewVarHandle(
			char[].class, ByteOrder.BIG_ENDIAN);
	private static final VarHandle _intArrayVarHandle =
		MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
	private static final VarHandle _longArrayVarHandle =
		MethodHandles.byteArrayViewVarHandle(
			long[].class, ByteOrder.BIG_ENDIAN);
	private static final VarHandle _shortArrayVarHandle =
		MethodHandles.byteArrayViewVarHandle(
			short[].class, ByteOrder.BIG_ENDIAN);

}