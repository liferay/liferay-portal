/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.orm;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class FinderPath {

	public static String[] decodeDSLQueryCacheName(String cacheName) {
		return StringUtil.split(cacheName, _TABLE_SEPARATOR);
	}

	public static String encodeDSLQueryCacheName(String[] tableNames) {
		StringBundler sb = new StringBundler((tableNames.length * 2) - 1);

		for (int i = 0; i < tableNames.length; i++) {
			sb.append(tableNames[i]);

			if ((i + 1) < tableNames.length) {
				sb.append(_TABLE_SEPARATOR);
			}
		}

		return sb.toString();
	}

	public FinderPath(
		String cacheName, String methodName, String[] params,
		String[] columnNames, boolean baseModelResult) {

		this(
			cacheName, methodName, params, columnNames, 0, 0, baseModelResult,
			null);
	}

	public FinderPath(
		String cacheName, String methodName, String[] params,
		String[] columnNames, int caseInsensitiveBitmask,
		int convertNullBitmask, boolean baseModelResult,
		Function<Object, Object[]> argsExtractorFunction) {

		_cacheName = cacheName;

		int index = methodName.indexOf("By");

		if (index == -1) {
			_finderName = methodName;
		}
		else {
			_finderName = methodName.substring(index + 2);
		}

		_columnNames = columnNames;
		_caseInsensitiveBitmask = caseInsensitiveBitmask;
		_convertNullBitmask = convertNullBitmask;
		_baseModelResult = baseModelResult;

		if (argsExtractorFunction == null) {
			_argsExtractorFunction = _EMPTY_ARGS_EXTRACTOR_FUNCTION;
		}
		else {
			_argsExtractorFunction = argsExtractorFunction;
		}

		_initCacheKeyPrefix(methodName, params);

		if (_cacheName.contains(".List") || methodName.equals("dslQuery")) {
			_singleResult = false;
		}
		else {
			_singleResult = true;
		}
	}

	public Object[] extractArgs(BaseModel<?> baseModel) {
		return _argsExtractorFunction.apply(baseModel);
	}

	public String getCacheKeyPrefix() {
		return _cacheKeyPrefix;
	}

	public String getCacheName() {
		return _cacheName;
	}

	public String[] getColumnNames() {
		return _columnNames;
	}

	public String getFinderName() {
		return _finderName;
	}

	public boolean isBaseModelResult() {
		return _baseModelResult;
	}

	public boolean isTouched() {
		if (_singleResult &&
			((System.nanoTime() - _timestamp) >= _COOL_DOWN_PERIOD)) {

			return false;
		}

		return true;
	}

	public Object normalizeArgument(int columnIndex, Object value) {
		if (value instanceof Date date) {
			return date.getTime();
		}

		if (_isCaseInsensitive(columnIndex)) {
			value = StringUtil.toLowerCase((String)value);
		}

		if (_isConvertNull(columnIndex)) {
			value = Objects.toString(value, "");
		}

		return value;
	}

	public void touch() {
		if (_singleResult) {
			_timestamp = System.nanoTime();
		}
	}

	private static Map<String, String> _getEncodedTypes() {
		return HashMapBuilder.put(
			Boolean.class.getName(), Boolean.class.getSimpleName()
		).put(
			Byte.class.getName(), Byte.class.getSimpleName()
		).put(
			Character.class.getName(), Character.class.getSimpleName()
		).put(
			Double.class.getName(), Double.class.getSimpleName()
		).put(
			Float.class.getName(), Float.class.getSimpleName()
		).put(
			Integer.class.getName(), Integer.class.getSimpleName()
		).put(
			Long.class.getName(), Long.class.getSimpleName()
		).put(
			Short.class.getName(), Short.class.getSimpleName()
		).put(
			String.class.getName(), String.class.getSimpleName()
		).build();
	}

	private void _initCacheKeyPrefix(String methodName, String[] params) {
		StringBundler sb = new StringBundler((params.length * 2) + 3);

		sb.append(methodName);
		sb.append(_PARAMS_SEPARATOR);

		for (String param : params) {
			sb.append(StringPool.PERIOD);
			sb.append(_encodedTypes.getOrDefault(param, param));
		}

		sb.append(_ARGS_SEPARATOR);

		_cacheKeyPrefix = sb.toString();
	}

	private boolean _isCaseInsensitive(int columnIndex) {
		if ((_caseInsensitiveBitmask & (1 << columnIndex)) != 0) {
			return true;
		}

		return false;
	}

	private boolean _isConvertNull(int columnIndex) {
		if ((_convertNullBitmask & (1 << columnIndex)) != 0) {
			return true;
		}

		return false;
	}

	private static final String _ARGS_SEPARATOR = "_A_";

	private static final long _COOL_DOWN_PERIOD = GetterUtil.getLong(
		PropsUtil.get(
			"value.object.finder.cache.single.result.cool.down.period"),
		600_000_000_000L);

	private static final Function<Object, Object[]>
		_EMPTY_ARGS_EXTRACTOR_FUNCTION = baseModel -> new Object[0];

	private static final String _PARAMS_SEPARATOR = "_P_";

	private static final String _TABLE_SEPARATOR = "_T_";

	private static final Map<String, String> _encodedTypes = _getEncodedTypes();

	private final Function<Object, Object[]> _argsExtractorFunction;
	private final boolean _baseModelResult;
	private String _cacheKeyPrefix;
	private final String _cacheName;
	private final int _caseInsensitiveBitmask;
	private final String[] _columnNames;
	private final int _convertNullBitmask;
	private final String _finderName;
	private final boolean _singleResult;
	private volatile long _timestamp;

}