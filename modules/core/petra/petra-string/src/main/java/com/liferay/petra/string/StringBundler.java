/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.string;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

/**
 * <p>
 * See https://issues.liferay.com/browse/LPS-6072.
 * </p>
 *
 * @author Shuyang Zhou
 * @author Brian Wing Shun Chan
 * @author Preston Crary
 */
public class StringBundler implements Serializable {

	public static String concat(Object... objects) {
		String[] strings = new String[objects.length];

		for (int i = 0; i < objects.length; i++) {
			strings[i] = String.valueOf(objects[i]);
		}

		return _toString(strings, strings.length);
	}

	public static String concat(String... strings) {
		for (int i = 0; i < strings.length; i++) {
			if (strings[i] == null) {
				strings[i] = StringPool.NULL;
			}
		}

		return _toString(strings, strings.length);
	}

	public StringBundler() {
		_array = StringPool.EMPTY_ARRAY;
	}

	public StringBundler(int initialCapacity) {
		if (initialCapacity <= 0) {
			_array = StringPool.EMPTY_ARRAY;
		}
		else {
			_array = new String[initialCapacity];
		}
	}

	public StringBundler(String s) {
		_array = new String[_DEFAULT_ARRAY_CAPACITY];

		_array[0] = s;

		_arrayIndex = 1;
	}

	public StringBundler(String[] stringArray) {
		this(stringArray, 0);
	}

	public StringBundler(String[] stringArray, int extraSpace) {
		_array = new String[stringArray.length + extraSpace];

		for (String s : stringArray) {
			if ((s != null) && !s.isEmpty()) {
				_array[_arrayIndex++] = s;
			}
		}
	}

	public StringBundler append(boolean b) {
		if (b) {
			return append(StringPool.TRUE);
		}

		return append(StringPool.FALSE);
	}

	public StringBundler append(char c) {
		return append(String.valueOf(c));
	}

	public StringBundler append(char[] chars) {
		if (chars == null) {
			return append("null");
		}

		return append(new String(chars));
	}

	public StringBundler append(double d) {
		return append(String.valueOf(d));
	}

	public StringBundler append(float f) {
		return append(String.valueOf(f));
	}

	public StringBundler append(int i) {
		return append(String.valueOf(i));
	}

	public StringBundler append(long l) {
		return append(String.valueOf(l));
	}

	public StringBundler append(Object object) {
		return append(String.valueOf(object));
	}

	public StringBundler append(String s) {
		if (s == null) {
			s = StringPool.NULL;
		}

		if (s.isEmpty()) {
			return this;
		}

		if (_arrayIndex >= _array.length) {
			expandCapacity(_array.length * 2);
		}

		_array[_arrayIndex++] = s;

		return this;
	}

	public StringBundler append(String[] stringArray) {
		if ((stringArray == null) || (stringArray.length == 0)) {
			return this;
		}

		if ((_array.length - _arrayIndex) < stringArray.length) {
			expandCapacity((_array.length + stringArray.length) * 2);
		}

		for (String s : stringArray) {
			if ((s != null) && (s.length() > 0)) {
				_array[_arrayIndex++] = s;
			}
		}

		return this;
	}

	public StringBundler append(StringBundler sb) {
		if ((sb == null) || (sb._arrayIndex == 0)) {
			return this;
		}

		if ((_array.length - _arrayIndex) < sb._arrayIndex) {
			expandCapacity((_array.length + sb._arrayIndex) * 2);
		}

		System.arraycopy(sb._array, 0, _array, _arrayIndex, sb._arrayIndex);

		_arrayIndex += sb._arrayIndex;

		return this;
	}

	public int capacity() {
		return _array.length;
	}

	public String[] getStrings() {
		return _array;
	}

	public int index() {
		return _arrayIndex;
	}

	public int length() {
		int length = 0;

		for (int i = 0; i < _arrayIndex; i++) {
			length += _array[i].length();
		}

		return length;
	}

	public void setIndex(int newIndex) {
		if (newIndex < 0) {
			throw new ArrayIndexOutOfBoundsException(newIndex);
		}

		if (newIndex > _array.length) {
			String[] newArray = new String[newIndex];

			System.arraycopy(_array, 0, newArray, 0, _arrayIndex);

			_array = newArray;
		}

		if (_arrayIndex < newIndex) {
			for (int i = _arrayIndex; i < newIndex; i++) {
				_array[i] = StringPool.BLANK;
			}
		}

		if (_arrayIndex > newIndex) {
			for (int i = newIndex; i < _arrayIndex; i++) {
				_array[i] = null;
			}
		}

		_arrayIndex = newIndex;
	}

	public void setStringAt(String s, int index) {
		if ((index < 0) || (index >= _arrayIndex)) {
			throw new ArrayIndexOutOfBoundsException(index);
		}

		_array[index] = s;
	}

	public String stringAt(int index) {
		if ((index < 0) || (index >= _arrayIndex)) {
			throw new ArrayIndexOutOfBoundsException(index);
		}

		return _array[index];
	}

	@Override
	public String toString() {
		return _toString(_array, _arrayIndex);
	}

	public void writeTo(Writer writer) throws IOException {
		for (int i = 0; i < _arrayIndex; i++) {
			writer.write(_array[i]);
		}
	}

	protected void expandCapacity(int newCapacity) {
		if (newCapacity == 0) {
			newCapacity = _DEFAULT_ARRAY_CAPACITY;
		}

		String[] newArray = new String[newCapacity];

		System.arraycopy(_array, 0, newArray, 0, _arrayIndex);

		_array = newArray;
	}

	private static String _concat(String[] array, int arrayIndex) {
		if (_coderMethodHandle == null) {
			return null;
		}

		try {
			byte coder = (byte)_coderMethodHandle.invokeExact(array[0]);

			int length = 0;

			for (int i = 0; i < arrayIndex; i++) {
				if (coder != (byte)_coderMethodHandle.invokeExact(array[i])) {
					return null;
				}

				length += array[i].length();
			}

			length <<= coder;

			byte[] bytes = new byte[length];

			int index = 0;

			for (int i = 0; i < arrayIndex; i++) {
				byte[] value = (byte[])_valueMethodHandle.invokeExact(array[i]);

				System.arraycopy(value, 0, bytes, index, value.length);

				index += value.length;
			}

			return (String)_constructorMethodHandle.invokeExact(bytes, coder);
		}
		catch (Throwable throwable) {
			return null;
		}
	}

	private static String _toString(String[] array, int arrayIndex) {
		if (arrayIndex == 0) {
			return StringPool.BLANK;
		}

		if (arrayIndex == 1) {
			return array[0];
		}

		if (arrayIndex == 2) {
			return array[0].concat(array[1]);
		}

		String result = _concat(array, arrayIndex);

		if (result == null) {
			return _toStringSB(array, arrayIndex);
		}

		return result;
	}

	private static String _toStringSB(String[] array, int arrayIndex) {
		int length = 0;

		for (int i = 0; i < arrayIndex; i++) {
			length += array[i].length();
		}

		StringBuilder sb = new StringBuilder(length);

		for (int i = 0; i < arrayIndex; i++) {
			sb.append(array[i]);
		}

		return sb.toString();
	}

	private static final int _DEFAULT_ARRAY_CAPACITY = 10;

	private static final MethodHandle _coderMethodHandle;
	private static final MethodHandle _constructorMethodHandle;
	private static final MethodHandle _valueMethodHandle;
	private static final long serialVersionUID = 1L;

	static {
		MethodHandle coderMethodHandle = null;
		MethodHandle constructorMethodHandle = null;
		MethodHandle valueMethodHandle = null;

		try {
			Field field = MethodHandles.Lookup.class.getDeclaredField(
				"IMPL_LOOKUP");

			field.setAccessible(true);

			MethodHandles.Lookup lookup = (MethodHandles.Lookup)field.get(null);

			coderMethodHandle = lookup.findGetter(
				String.class, "coder", byte.class);
			constructorMethodHandle = lookup.unreflectConstructor(
				String.class.getDeclaredConstructor(byte[].class, byte.class));
			valueMethodHandle = lookup.findGetter(
				String.class, "value", byte[].class);
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
		}

		_coderMethodHandle = coderMethodHandle;
		_constructorMethodHandle = constructorMethodHandle;
		_valueMethodHandle = valueMethodHandle;
	}

	private String[] _array;
	private int _arrayIndex;

}