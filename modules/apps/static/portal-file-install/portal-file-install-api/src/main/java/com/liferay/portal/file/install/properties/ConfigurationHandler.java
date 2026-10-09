/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.file.install.properties;

import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Writer;

import java.lang.reflect.Array;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Matthew Tambara
 */
public class ConfigurationHandler {

	public static Object read(String value) throws IOException {
		try (UnsyncStringReader unsyncStringReader = new UnsyncStringReader(
				value);

			PushbackReader pushbackReader = new PushbackReader(
				unsyncStringReader, 1)) {

			return _readValue(pushbackReader);
		}
	}

	public static String write(Object value) throws IOException {
		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		_writeValue(unsyncStringWriter, value);

		return unsyncStringWriter.toString();
	}

	private static int _ignorablePageBreakAndWhiteSpace(
			PushbackReader pushbackReader)
		throws IOException {

		int c1 = _ignorableWhiteSpace(pushbackReader);

		while (true) {
			if (c1 != CharPool.BACK_SLASH) {
				break;
			}

			int c2 = pushbackReader.read();

			if ((c2 == CharPool.NEW_LINE) || (c2 == CharPool.RETURN)) {
				c1 = _ignorableWhiteSpace(pushbackReader);
			}
			else {
				pushbackReader.unread(c2);

				break;
			}
		}

		return c1;
	}

	private static int _ignorableWhiteSpace(PushbackReader pushbackReader)
		throws IOException {

		int c = _read(pushbackReader);

		while ((c >= 0) && Character.isWhitespace((char)c)) {
			c = _read(pushbackReader);
		}

		return c;
	}

	private static int _read(PushbackReader pushbackReader) throws IOException {
		int c1 = pushbackReader.read();

		if (c1 == CharPool.RETURN) {
			int c2 = pushbackReader.read();

			if (c2 != CharPool.NEW_LINE) {
				pushbackReader.unread(c2);
			}

			c1 = CharPool.NEW_LINE;
		}

		return c1;
	}

	private static int _read(PushbackReader pushbackReader, char[] buffer)
		throws IOException {

		for (int i = 0; i < buffer.length; i++) {
			int c = _read(pushbackReader);

			if (c < 0) {
				return i;
			}

			buffer[i] = (char)c;
		}

		return buffer.length;
	}

	private static Object _readArray(
			int typeCode, PushbackReader pushbackReader)
		throws IOException {

		List<Object> objects = new ArrayList<>();

		while (true) {
			int token = _ignorablePageBreakAndWhiteSpace(pushbackReader);

			if (token == _TOKEN_VAL_OPEN) {
				Object value = _readSimple(typeCode, pushbackReader);

				if (value == null) {

					// abort due to error

					return null;
				}

				_read(pushbackReader);

				objects.add(value);

				token = _ignorablePageBreakAndWhiteSpace(pushbackReader);
			}

			if (token == CharPool.CLOSE_BRACKET) {
				Class<?> type = _codeToType.get(typeCode);

				Object array = Array.newInstance(type, objects.size());

				for (int i = 0; i < objects.size(); i++) {
					Array.set(array, i, objects.get(i));
				}

				return array;
			}
			else if (token < 0) {
				return null;
			}
			else if (token != CharPool.COMMA) {
				return null;
			}
		}
	}

	private static Collection<Object> _readCollection(
			int typeCode, PushbackReader pushbackReader)
		throws IOException {

		Collection<Object> objects = new ArrayList<>();

		while (true) {
			int token = _ignorablePageBreakAndWhiteSpace(pushbackReader);

			if (token == _TOKEN_VAL_OPEN) {
				Object value = _readSimple(typeCode, pushbackReader);

				if (value == null) {

					// abort due to error

					return null;
				}

				_read(pushbackReader);

				objects.add(value);

				token = _ignorablePageBreakAndWhiteSpace(pushbackReader);
			}

			if (token == CharPool.CLOSE_PARENTHESIS) {
				return objects;
			}
			else if (token < 0) {
				return null;
			}
			else if (token != CharPool.COMMA) {
				return null;
			}
		}
	}

	private static String _readQuoted(PushbackReader pushbackReader)
		throws IOException {

		StringBundler sb = new StringBundler();

		while (true) {
			int c = _read(pushbackReader);

			if (c == CharPool.BACK_SLASH) {
				c = _read(pushbackReader);

				if (c == 'b') {
					sb.append('\b');
				}
				else if (c == 't') {
					sb.append(CharPool.TAB);
				}
				else if (c == 'n') {
					sb.append(CharPool.NEW_LINE);
				}
				else if (c == 'f') {
					sb.append('\f');
				}
				else if (c == 'r') {
					sb.append(CharPool.RETURN);
				}
				else if (c == 'u') {
					char[] charBuffer = new char[4];

					if (_read(pushbackReader, charBuffer) == 4) {
						c = Integer.parseInt(new String(charBuffer), 16);

						sb.append((char)c);
					}
				}
				else {
					sb.append((char)c);
				}
			}
			else if ((c == -1) || (c == _TOKEN_VAL_CLOS)) {
				pushbackReader.unread(c);

				return sb.toString();
			}
			else {
				sb.append((char)c);
			}
		}
	}

	private static Object _readSimple(int code, PushbackReader pushbackReader)
		throws IOException {

		if (code == -1) {
			return null;
		}

		code = Character.toUpperCase(code);

		if (code == _TOKEN_BOOLEAN) {
			return Boolean.valueOf(_readQuoted(pushbackReader));
		}
		else if (code == _TOKEN_BYTE) {
			return Byte.valueOf(_readQuoted(pushbackReader));
		}
		else if (code == _TOKEN_CHARACTER) {
			String charString = _readQuoted(pushbackReader);

			if ((charString != null) && (charString.length() > 0)) {
				return charString.charAt(0);
			}

			return null;
		}
		else if (code == _TOKEN_DOUBLE) {
			String doubleString = _readQuoted(pushbackReader);

			if (doubleString.indexOf(CharPool.PERIOD) >= 0) {
				return Double.valueOf(doubleString);
			}

			return Double.longBitsToDouble(
				GetterUtil.getLongStrict(doubleString));
		}
		else if (code == _TOKEN_FLOAT) {
			String floatString = _readQuoted(pushbackReader);

			if (floatString.indexOf(CharPool.PERIOD) >= 0) {
				return Float.valueOf(floatString);
			}

			return Float.intBitsToFloat(
				GetterUtil.getIntegerStrict(floatString));
		}
		else if (code == _TOKEN_INTEGER) {
			return Integer.valueOf(_readQuoted(pushbackReader));
		}
		else if (code == _TOKEN_LONG) {
			return Long.valueOf(_readQuoted(pushbackReader));
		}
		else if (code == _TOKEN_SHORT) {
			return Short.valueOf(_readQuoted(pushbackReader));
		}
		else if (code == _TOKEN_STRING) {
			return _readQuoted(pushbackReader);
		}

		return null;
	}

	private static Object _readValue(PushbackReader pushbackReader)
		throws IOException {

		// read past any whitespace and (optional) type code

		int type = _ignorableWhiteSpace(pushbackReader);

		// read value kind code if type code is not a value kinde code

		int code = type;

		if (_codeToType.containsKey(Character.toUpperCase(type))) {
			code = _read(pushbackReader);
		}
		else {
			type = _TOKEN_STRING;
		}

		if (code == CharPool.OPEN_BRACKET) {
			return _readArray(type, pushbackReader);
		}
		else if (code == CharPool.OPEN_PARENTHESIS) {
			return _readCollection(type, pushbackReader);
		}
		else if (code == _TOKEN_VAL_OPEN) {
			Object value = _readSimple(type, pushbackReader);

			_read(pushbackReader);

			return value;
		}

		return null;
	}

	private static void _writeArray(Writer writer, Object arrayValue)
		throws IOException {

		int size = Array.getLength(arrayValue);

		Class<?> clazz = arrayValue.getClass();

		_writeType(writer, clazz.getComponentType());

		writer.write(CharPool.OPEN_BRACKET);

		writer.write(_COLLECTION_LINE_BREAK);

		for (int i = 0; i < size; i++) {
			writer.write(_INDENT);

			_writeSimple(writer, Array.get(arrayValue, i));

			if (i != (size - 1)) {
				writer.write(CharPool.COMMA);
			}

			writer.write(_COLLECTION_LINE_BREAK);
		}

		writer.write(CharPool.CLOSE_BRACKET);
	}

	private static void _writeCollection(
			Writer writer, Collection<?> collection)
		throws IOException {

		if (collection.isEmpty()) {
			writer.write(CharPool.OPEN_PARENTHESIS);
			writer.write(_COLLECTION_LINE_BREAK);
			writer.write(CharPool.CLOSE_PARENTHESIS);
		}
		else {
			Iterator<?> iterator = collection.iterator();

			Object firstElement = iterator.next();

			_writeType(writer, firstElement.getClass());

			writer.write(CharPool.OPEN_PARENTHESIS);
			writer.write(_COLLECTION_LINE_BREAK);

			writer.write(_INDENT);

			_writeSimple(writer, firstElement);

			while (iterator.hasNext()) {
				writer.write(CharPool.COMMA);
				writer.write(_COLLECTION_LINE_BREAK);
				writer.write(_INDENT);

				_writeSimple(writer, iterator.next());
			}

			writer.write(_COLLECTION_LINE_BREAK);
			writer.write(CharPool.CLOSE_PARENTHESIS);
		}
	}

	private static void _writeQuoted(Writer writer, String simple)
		throws IOException {

		if (simple.isEmpty()) {
			return;
		}

		char c = 0;

		int length = simple.length();

		for (int i = 0; i < length; i++) {
			c = simple.charAt(i);

			if ((c == '\\') || (c == _TOKEN_VAL_CLOS) ||
				(c == CharPool.SPACE) || (c == CharPool.EQUAL) ||
				(c == CharPool.OPEN_CURLY_BRACE) ||
				(c == CharPool.CLOSE_CURLY_BRACE)) {

				writer.write(CharPool.BACK_SLASH);
				writer.write(c);
			}
			else if (c == '\b') {
				writer.write("\\b");
			}
			else if (c == CharPool.TAB) {
				writer.write("\\t");
			}
			else if (c == CharPool.NEW_LINE) {
				writer.write("\\n");
			}
			else if (c == '\f') {
				writer.write("\\f");
			}
			else if (c == CharPool.RETURN) {
				writer.write("\\r");
			}
			else if (c < CharPool.SPACE) {
				String hexString = "000" + Integer.toHexString(c);

				writer.write(
					"\\u" + hexString.substring(hexString.length() - 4));
			}
			else {
				writer.write(c);
			}
		}
	}

	private static void _writeSimple(Writer writer, Object value)
		throws IOException {

		writer.write(_TOKEN_VAL_OPEN);

		_writeQuoted(writer, String.valueOf(value));

		writer.write(_TOKEN_VAL_CLOS);
	}

	private static void _writeType(Writer writer, Class<?> valueType)
		throws IOException {

		Integer code = _typeToCode.get(valueType);

		if (code != null) {
			writer.write((char)code.intValue());
		}
	}

	private static void _writeValue(Writer writer, Object value)
		throws IOException {

		Class<?> clazz = value.getClass();

		if (clazz.isArray()) {
			_writeArray(writer, value);
		}
		else if (value instanceof Collection) {
			_writeCollection(writer, (Collection<?>)value);
		}
		else {
			_writeType(writer, clazz);
			_writeSimple(writer, value);
		}
	}

	private static final String _COLLECTION_LINE_BREAK = "\\\r\n";

	private static final String _INDENT = "  ";

	private static final int _TOKEN_BOOLEAN = 'B';

	private static final int _TOKEN_BYTE = 'X';

	private static final int _TOKEN_CHARACTER = 'C';

	private static final int _TOKEN_DOUBLE = 'D';

	private static final int _TOKEN_FLOAT = 'F';

	private static final int _TOKEN_INTEGER = 'I';

	private static final int _TOKEN_LONG = 'L';

	private static final int _TOKEN_SHORT = 'S';

	private static final int _TOKEN_STRING = 'T';

	private static final int _TOKEN_VAL_CLOS = '"'; // '}';

	private static final int _TOKEN_VAL_OPEN = '"'; // '{';

	private static final Map<Integer, Class<?>> _codeToType = new HashMap<>();
	private static final Map<Class<?>, Integer> _typeToCode =
		HashMapBuilder.<Class<?>, Integer>put(
			Boolean.class, _TOKEN_BOOLEAN
		).put(
			Byte.class, _TOKEN_BYTE
		).put(
			Character.class, _TOKEN_CHARACTER
		).put(
			Double.class, _TOKEN_DOUBLE
		).put(
			Float.class, _TOKEN_FLOAT
		).put(
			Integer.class, _TOKEN_INTEGER
		).put(
			Long.class, _TOKEN_LONG
		).put(
			Short.class, _TOKEN_SHORT
		).build();

	static {
		for (Map.Entry<Class<?>, Integer> entry : _typeToCode.entrySet()) {
			_codeToType.put(entry.getValue(), entry.getKey());
		}

		_codeToType.put(_TOKEN_STRING, String.class);
	}

}