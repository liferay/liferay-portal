/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.client.json;

import jakarta.annotation.Generated;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public abstract class BaseJSONParser<T> {

	public static final String[][] JSON_ESCAPE_STRINGS = new String[][] {
		{"\\", "\\\\"}, {"\"", "\\\""}, {"\b", "\\b"}, {"\f", "\\f"},
		{"\n", "\\n"}, {"\r", "\\r"}, {"\t", "\\t"}
	};

	public T parseToDTO(String json) {
		if (json == null) {
			throw new IllegalArgumentException("JSON is null");
		}

		_init(json);

		_assertStartsWithAndEndsWith("{", "}");

		T dto = createDTO();

		if (_isEmpty()) {
			return dto;
		}

		_readNextChar();

		_readWhileLastCharIsWhiteSpace();

		_readNextChar();

		if (_isLastChar('}')) {
			_readWhileLastCharIsWhiteSpace();

			if (!_isEndOfJSON()) {
				_readNextChar();

				throw new IllegalArgumentException(
					"Expected end of JSON, but found '" + _lastChar + "'");
			}

			return dto;
		}

		do {
			_readWhileLastCharIsWhiteSpace();

			String fieldName = _readValueAsString();

			_readWhileLastCharIsWhiteSpace();

			_assertLastChar(':');

			_readNextChar();

			_readWhileLastCharIsWhiteSpace();

			setField(dto, fieldName, _readValue(parseMaps(fieldName)));

			_readWhileLastCharIsWhiteSpace();
		}
		while (_ifLastCharMatchesThenRead(','));

		return dto;
	}

	public T[] parseToDTOs(String json) {
		if (json == null) {
			throw new IllegalArgumentException("JSON is null");
		}

		_init(json);

		_assertStartsWithAndEndsWith("[", "]");

		if (_isEmpty()) {
			return createDTOArray(0);
		}

		_readNextChar();

		_readWhileLastCharIsWhiteSpace();

		if (_isLastChar(']')) {
			_readNextChar();

			return createDTOArray(0);
		}

		_readWhileLastCharIsWhiteSpace();

		Object[] objects = (Object[])_readValue();

		T[] dtos = createDTOArray(objects.length);

		for (int i = 0; i < dtos.length; i++) {
			dtos[i] = parseToDTO((String)objects[i]);
		}

		return dtos;
	}

	public Map<String, Object> parseToMap(String json) {
		if (json == null) {
			throw new IllegalArgumentException("JSON is null");
		}

		_init(json);

		_assertStartsWithAndEndsWith("{", "}");

		Map<String, Object> map = new TreeMap<>();

		_setCaptureStart();

		_readNextChar();

		_readNextChar();

		_readWhileLastCharIsWhiteSpace();

		if (_isLastChar('}')) {
			return map;
		}

		do {
			_readWhileLastCharIsWhiteSpace();

			String key = _readValueAsString();

			_readWhileLastCharIsWhiteSpace();

			if (!_ifLastCharMatchesThenRead(':')) {
				throw new IllegalArgumentException("Expected ':'");
			}

			_readWhileLastCharIsWhiteSpace();

			map.put(key, _readValue(true));

			_readWhileLastCharIsWhiteSpace();
		}
		while (_ifLastCharMatchesThenRead(','));

		_readWhileLastCharIsWhiteSpace();

		if (!_ifLastCharMatchesThenRead('}')) {
			throw new IllegalArgumentException(
				"Expected either ',' or '}', but found '" + _lastChar + "'");
		}

		return map;
	}

	protected abstract T createDTO();

	protected abstract T[] createDTOArray(int size);

	protected abstract boolean parseMaps(String jsonParserFieldName);

	protected abstract void setField(
		T dto, String jsonParserFieldName, Object jsonParserFieldValue);

	protected BigDecimal[] toBigDecimals(Object[] objects) {
		BigDecimal[] bigdecimals = new BigDecimal[objects.length];

		for (int i = 0; i < bigdecimals.length; i++) {
			bigdecimals[i] = new BigDecimal(objects[i].toString());
		}

		return bigdecimals;
	}

	protected Date toDate(String string) {
		try {
			return _dateFormat.parse(string);
		}
		catch (ParseException pe) {
			throw new IllegalArgumentException(
				"Unable to parse date from " + string, pe);
		}
	}

	protected Date[] toDates(Object[] objects) {
		Date[] dates = new Date[objects.length];

		for (int i = 0; i < dates.length; i++) {
			dates[i] = toDate((String)objects[i]);
		}

		return dates;
	}

	protected Integer[] toIntegers(Object[] objects) {
		Integer[] integers = new Integer[objects.length];

		for (int i = 0; i < integers.length; i++) {
			integers[i] = Integer.valueOf(objects[i].toString());
		}

		return integers;
	}

	protected Long[] toLongs(Object[] objects) {
		Long[] longs = new Long[objects.length];

		for (int i = 0; i < longs.length; i++) {
			longs[i] = Long.valueOf(objects[i].toString());
		}

		return longs;
	}

	protected String toString(Date date) {
		return _dateFormat.format(date);
	}

	protected String[] toStrings(Object[] objects) {
		String[] strings = new String[objects.length];

		for (int i = 0; i < strings.length; i++) {
			strings[i] = (String)objects[i];
		}

		return strings;
	}

	private void _assertLastChar(char c) {
		if (_lastChar != c) {
			throw new IllegalArgumentException(
				String.format(
					"Expected last char '%s', but found '%s'", c, _lastChar));
		}
	}

	private void _assertStartsWithAndEndsWith(String prefix, String sufix) {
		if (!_json.startsWith(prefix)) {
			throw new IllegalArgumentException(
				String.format(
					"Expected starts with '%s', but found '%s' in '%s'", prefix,
					_json.charAt(0), _json));
		}

		if (!_json.endsWith(sufix)) {
			throw new IllegalArgumentException(
				String.format(
					"Expected ends with '%s', but found '%s' in '%s'", sufix,
					_json.charAt(_json.length() - 1), _json));
		}
	}

	private String _getCapturedJSONSubstring() {
		return _json.substring(_captureStartStack.pop(), _index - 1);
	}

	private String _getCapturedSubstring() {
		return _unescape(_getCapturedJSONSubstring());
	}

	private boolean _ifLastCharMatchesThenRead(char ch) {
		if (_lastChar != ch) {
			return false;
		}

		_readNextChar();

		return true;
	}

	private void _init(String json) {
		_captureStartStack = new Stack<>();
		_dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXX");
		_index = 0;
		_json = json.trim();
		_lastChar = 0;
	}

	private boolean _isCharEscaped(String string, int index) {
		int backslashCount = 0;

		while (((index - 1 - backslashCount) >= 0) &&
			   (string.charAt(index - 1 - backslashCount) == '\\')) {

			backslashCount++;
		}

		if ((backslashCount % 2) == 0) {
			return false;
		}

		return true;
	}

	private boolean _isEmpty() {
		String substring = _json.substring(1, _json.length() - 1);

		substring = substring.trim();

		return substring.isEmpty();
	}

	private boolean _isEndOfJSON() {
		if (_index == _json.length()) {
			return true;
		}

		return false;
	}

	private boolean _isLastChar(char c) {
		if (_lastChar == c) {
			return true;
		}

		return false;
	}

	private boolean _isLastCharDecimalSeparator() {
		if (_lastChar == '.') {
			return true;
		}

		return false;
	}

	private boolean _isLastCharDigit() {
		if ((_lastChar >= '0') && (_lastChar <= '9')) {
			return true;
		}

		return false;
	}

	private boolean _isLastCharNegative() {
		if (_lastChar == '-') {
			return true;
		}

		return false;
	}

	private boolean _isLastCharPositive() {
		if (_lastChar == '+') {
			return true;
		}

		return false;
	}

	private boolean _isLastCharScientificNotation() {
		if (_lastChar == 'E') {
			return true;
		}

		return false;
	}

	private void _readNextChar() {
		if (!_isEndOfJSON()) {
			_lastChar = _json.charAt(_index++);
		}
	}

	private Object _readValue() {
		return _readValue(false);
	}

	private Object _readValue(boolean parseMaps) {
		if (_lastChar == '[') {
			return _readValueAsArray(parseMaps);
		}
		else if (_lastChar == 'f') {
			return _readValueAsBooleanFalse();
		}
		else if (_lastChar == 't') {
			return _readValueAsBooleanTrue();
		}
		else if (_lastChar == 'n') {
			return _readValueAsObjectNull();
		}
		else if (_lastChar == '"') {
			return _readValueAsString();
		}
		else if (parseMaps && (_lastChar == '{')) {
			try {
				Class<? extends BaseJSONParser> clazz = getClass();

				BaseJSONParser baseJSONParser = clazz.newInstance();

				return baseJSONParser.parseToMap(_readValueAsStringJSON());
			}
			catch (Exception e) {
				throw new IllegalArgumentException(
					"Expected JSON object or map");
			}
		}
		else if (_lastChar == '{') {
			return _readValueAsStringJSON();
		}
		else if ((_lastChar == '-') || (_lastChar == '0') ||
				 (_lastChar == '1') || (_lastChar == '2') ||
				 (_lastChar == '3') || (_lastChar == '4') ||
				 (_lastChar == '5') || (_lastChar == '6') ||
				 (_lastChar == '7') || (_lastChar == '8') ||
				 (_lastChar == '9')) {

			return _readValueAsStringNumber();
		}
		else {
			throw new IllegalArgumentException();
		}
	}

	private Object[] _readValueAsArray(boolean parseMaps) {
		List<Object> objects = new ArrayList<>();

		_readNextChar();

		_readWhileLastCharIsWhiteSpace();

		if (_isLastChar(']')) {
			_readNextChar();

			return objects.toArray();
		}

		do {
			_readWhileLastCharIsWhiteSpace();

			objects.add(_readValue(parseMaps));

			_readWhileLastCharIsWhiteSpace();
		}
		while (_ifLastCharMatchesThenRead(','));

		if (!_isLastChar(']')) {
			throw new IllegalArgumentException(
				"Expected ']', but found '" + _lastChar + "'");
		}

		_readNextChar();

		return objects.toArray();
	}

	private boolean _readValueAsBooleanFalse() {
		_readNextChar();

		_assertLastChar('a');

		_readNextChar();

		_assertLastChar('l');

		_readNextChar();

		_assertLastChar('s');

		_readNextChar();

		_assertLastChar('e');

		_readNextChar();

		return false;
	}

	private boolean _readValueAsBooleanTrue() {
		_readNextChar();

		_assertLastChar('r');

		_readNextChar();

		_assertLastChar('u');

		_readNextChar();

		_assertLastChar('e');

		_readNextChar();

		return true;
	}

	private Object _readValueAsObjectNull() {
		_readNextChar();

		_assertLastChar('u');

		_readNextChar();

		_assertLastChar('l');

		_readNextChar();

		_assertLastChar('l');

		_readNextChar();

		return null;
	}

	private String _readValueAsString() {
		_readNextChar();

		_setCaptureStart();

		while ((_lastChar != '"') || _isCharEscaped(_json, _index - 1)) {
			_readNextChar();
		}

		String string = _getCapturedSubstring();

		_readNextChar();

		return string;
	}

	private String _readValueAsStringJSON() {
		_setCaptureStart();

		_readNextChar();

		if (_isLastChar('}')) {
			_readNextChar();

			return _getCapturedJSONSubstring();
		}

		_readWhileLastCharIsWhiteSpace();

		if (_isLastChar('}')) {
			_readNextChar();

			return _getCapturedJSONSubstring();
		}

		do {
			_readWhileLastCharIsWhiteSpace();

			_readValueAsString();

			_readWhileLastCharIsWhiteSpace();

			if (!_ifLastCharMatchesThenRead(':')) {
				throw new IllegalArgumentException("Expected ':'");
			}

			_readWhileLastCharIsWhiteSpace();

			_readValue();

			_readWhileLastCharIsWhiteSpace();
		}
		while (_ifLastCharMatchesThenRead(','));

		_readWhileLastCharIsWhiteSpace();

		if (!_ifLastCharMatchesThenRead('}')) {
			throw new IllegalArgumentException(
				"Expected either ',' or '}', but found '" + _lastChar + "'");
		}

		return _getCapturedJSONSubstring();
	}

	private String _readValueAsStringNumber() {
		_setCaptureStart();

		do {
			_readNextChar();
		}
		while (_isLastCharDigit() || _isLastCharDecimalSeparator() ||
			   _isLastCharNegative() || _isLastCharPositive() ||
			   _isLastCharScientificNotation());

		return _getCapturedSubstring();
	}

	private void _readWhileLastCharIsWhiteSpace() {
		while ((_lastChar == ' ') || (_lastChar == '\n') ||
			   (_lastChar == '\r') || (_lastChar == '\t')) {

			_readNextChar();
		}
	}

	private void _setCaptureStart() {
		_captureStartStack.push(_index - 1);
	}

	private String _unescape(String string) {
		for (int i = JSON_ESCAPE_STRINGS.length - 1; i >= 0; i--) {
			String[] escapeStrings = JSON_ESCAPE_STRINGS[i];

			int index = string.indexOf(escapeStrings[1]);

			while (index != -1) {
				if (!_isCharEscaped(string, index)) {
					string =
						string.substring(0, index) + escapeStrings[0] +
							string.substring(index + escapeStrings[1].length());

					index = string.indexOf(
						escapeStrings[1], index + escapeStrings[0].length());
				}
				else {
					index = string.indexOf(
						escapeStrings[1], index + escapeStrings[1].length());
				}
			}
		}

		return string;
	}

	private Stack<Integer> _captureStartStack;
	private DateFormat _dateFormat;
	private int _index;
	private String _json;
	private char _lastChar;

}