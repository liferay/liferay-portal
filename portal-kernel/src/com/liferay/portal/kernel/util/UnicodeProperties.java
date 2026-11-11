/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * <p>
 * This is a rewrite of java.util.Properties that is not synchronized and
 * natively supports non-ASCII encodings. It can also be configured to be
 * "safe", allowing the values to have new line characters. When stored to a
 * given BufferedWriter, "safe" properties will replace all new line characters
 * with a _SAFE_NEWLINE_CHARACTER_.
 * </p>
 *
 * <p>
 * In its current form, this is not intended to replace java.util.Properties for
 * reading properties flat files. This class is not thread-safe.
 * </p>
 *
 * @author Alexander Chow
 */
public class UnicodeProperties extends HashMap<String, String> {

	public UnicodeProperties() {
		_safe = false;
	}

	public UnicodeProperties(boolean safe) {
		_safe = safe;
	}

	public UnicodeProperties(Map<String, String> map, boolean safe) {
		super((map != null) ? map : Collections.<String, String>emptyMap());

		_safe = safe;
	}

	public void fastLoad(String props) {
		if (Validator.isNull(props)) {
			return;
		}

		int x = props.indexOf(CharPool.NEW_LINE);
		int y = 0;

		while (x != -1) {
			put(props.substring(y, x));

			y = x;

			x = props.indexOf(CharPool.NEW_LINE, y + 1);
		}

		put(props.substring(y));
	}

	public String getProperty(String key) {
		return get(key);
	}

	public String getProperty(String key, String defaultValue) {
		String value = get(key);

		if (value == null) {
			return defaultValue;
		}

		return value;
	}

	public boolean isSafe() {
		return _safe;
	}

	public void load(String props) {
		if (Validator.isNull(props)) {
			return;
		}

		com.liferay.petra.string.StringUtil.forEachLine(props, this::put);
	}

	public void put(String line) {
		line = line.trim();

		if (_isComment(line)) {
			return;
		}

		int pos = line.indexOf(CharPool.EQUAL);

		if (pos == -1) {
			_log.error("Invalid property on line " + line);
		}
		else {
			String value = StringUtil.trim(line.substring(pos + 1));

			if (_safe) {
				value = _decode(value);
			}

			setProperty(StringUtil.trim(line.substring(0, pos)), value);
		}
	}

	@Override
	public String put(String key, String value) {
		if (key == null) {
			return null;
		}

		if (value == null) {
			return super.remove(key);
		}

		return super.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends String> map) {
		for (Map.Entry<? extends String, ? extends String> entry :
				map.entrySet()) {

			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public String remove(Object key) {
		if (key == null) {
			return null;
		}

		return super.remove(key);
	}

	public String setProperty(String key, String value) {
		return put(key, value);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(4 * size());

		Map<String, String> treeMap = new TreeMap<>(this);

		for (Map.Entry<String, String> entry : treeMap.entrySet()) {
			String value = entry.getValue();

			if (Validator.isNull(value)) {
				continue;
			}

			if (_safe) {
				value = _encode(value);
			}

			sb.append(entry.getKey());
			sb.append(StringPool.EQUAL);
			sb.append(value);
			sb.append(StringPool.NEW_LINE);
		}

		return sb.toString();
	}

	private String _decode(String value) {
		return StringUtil.replace(
			value, _SAFE_NEWLINE_CHARACTER, StringPool.NEW_LINE);
	}

	private String _encode(String value) {
		String encodedValue = StringUtil.replace(
			value, StringPool.RETURN_NEW_LINE, _SAFE_NEWLINE_CHARACTER);

		return StringUtil.replace(
			encodedValue, new char[] {CharPool.NEW_LINE, CharPool.RETURN},
			new String[] {_SAFE_NEWLINE_CHARACTER, _SAFE_NEWLINE_CHARACTER});
	}

	private boolean _isComment(String line) {
		if (line.isEmpty() || (line.charAt(0) == CharPool.POUND)) {
			return true;
		}

		return false;
	}

	private static final String _SAFE_NEWLINE_CHARACTER =
		"_SAFE_NEWLINE_CHARACTER_";

	private static final Log _log = LogFactoryUtil.getLog(
		UnicodeProperties.class);

	private final boolean _safe;

}