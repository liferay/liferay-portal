/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.json;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONSerializable;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.json.JSONTransformer;
import com.liferay.portal.kernel.util.UnicodeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jodd.json.JsonContext;
import jodd.json.JsonSerializer;
import jodd.json.TypeJsonSerializer;
import jodd.json.TypeJsonSerializerMap;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Igor Spasic
 */
public class JSONSerializerImpl implements JSONSerializer {

	public JSONSerializerImpl() {
		_jsonSerializer.strictStringEncoding(true);
	}

	@Override
	public JSONSerializerImpl exclude(String... fields) {
		_jsonSerializer.exclude(fields);

		_customized = true;

		return this;
	}

	@Override
	public JSONSerializerImpl include(String... fields) {
		_jsonSerializer.include(fields);

		_customized = true;

		return this;
	}

	@Override
	public String serialize(Object target) {
		StringBuilder sb = new StringBuilder();

		_jsonSerializer.serialize(target, sb);

		return sb.toString();
	}

	@Override
	public String serializeDeep(Object target) {
		StringBuilder sb = new StringBuilder();

		if (_customized || !_append(sb, target, null)) {
			sb.setLength(0);

			JsonSerializer jsonSerializer = _jsonSerializer.deep(true);

			jsonSerializer.serialize(target, sb);
		}

		return sb.toString();
	}

	@Override
	public JSONSerializerImpl transform(
		JSONTransformer jsonTransformer, Class<?> type) {

		TypeJsonSerializer<?> typeJsonSerializer = null;

		if (jsonTransformer instanceof TypeJsonSerializer) {
			typeJsonSerializer = (TypeJsonSerializer<?>)jsonTransformer;
		}
		else {
			typeJsonSerializer = new JoddJsonTransformer(jsonTransformer);
		}

		_jsonSerializer.withSerializer(type, typeJsonSerializer);

		_customized = true;

		return this;
	}

	@Override
	public JSONSerializerImpl transform(
		JSONTransformer jsonTransformer, String field) {

		TypeJsonSerializer<?> typeJsonSerializer = null;

		if (jsonTransformer instanceof TypeJsonSerializer) {
			typeJsonSerializer = (TypeJsonSerializer<?>)jsonTransformer;
		}
		else {
			typeJsonSerializer = new JoddJsonTransformer(jsonTransformer);
		}

		_jsonSerializer.withSerializer(field, typeJsonSerializer);

		_customized = true;

		return this;
	}

	private static boolean _addIfAbsent(List<Object> values, Object value) {
		for (Object curValue : values) {
			if (curValue == value) {
				return false;
			}
		}

		values.add(value);

		return true;
	}

	private static boolean _append(
		StringBuilder sb, Object value, List<Object> ancestors) {

		if (value == null) {
			sb.append(StringPool.NULL);

			return true;
		}

		if (value instanceof String string) {
			_appendString(sb, string);

			return true;
		}

		if (value instanceof Boolean || value instanceof Integer) {
			sb.append(value);

			return true;
		}

		if (value instanceof Long) {
			sb.append(CharPool.QUOTE);
			sb.append(value);
			sb.append(CharPool.QUOTE);

			return true;
		}

		if (value instanceof com.liferay.portal.kernel.json.JSONArray ||
			value instanceof com.liferay.portal.kernel.json.JSONObject) {

			sb.append(value.toString());

			return true;
		}

		if (value instanceof JSONArray || value instanceof JSONObject ||
			value instanceof JSONSerializable) {

			return false;
		}

		if (value instanceof Map<?, ?> map) {
			return _appendMap(sb, map, _getAncestors(ancestors));
		}

		if (value instanceof Iterable<?> iterable) {
			return _appendIterable(
				sb, value, iterable, _getAncestors(ancestors));
		}

		if (value instanceof Object[] array) {
			return _appendIterable(
				sb, value, Arrays.asList(array), _getAncestors(ancestors));
		}

		return false;
	}

	private static boolean _appendIterable(
		StringBuilder sb, Object value, Iterable<?> iterable,
		List<Object> ancestors) {

		if (!_addIfAbsent(ancestors, value)) {
			return false;
		}

		sb.append(CharPool.OPEN_BRACKET);

		for (Object element : iterable) {
			if (!_append(sb, element, ancestors)) {
				return false;
			}

			sb.append(CharPool.COMMA);
		}

		if (sb.charAt(sb.length() - 1) == CharPool.COMMA) {
			sb.setLength(sb.length() - 1);
		}

		sb.append(CharPool.CLOSE_BRACKET);

		ancestors.remove(ancestors.size() - 1);

		return true;
	}

	private static boolean _appendMap(
		StringBuilder sb, Map<?, ?> map, List<Object> ancestors) {

		if (!_addIfAbsent(ancestors, map)) {
			return false;
		}

		sb.append(CharPool.OPEN_CURLY_BRACE);

		for (Map.Entry<?, ?> entry : map.entrySet()) {
			Object key = entry.getKey();

			if (key == null) {
				return false;
			}

			_appendString(sb, key.toString());

			sb.append(CharPool.COLON);

			if (!_append(sb, entry.getValue(), ancestors)) {
				return false;
			}

			sb.append(CharPool.COMMA);
		}

		if (sb.charAt(sb.length() - 1) == CharPool.COMMA) {
			sb.setLength(sb.length() - 1);
		}

		sb.append(CharPool.CLOSE_CURLY_BRACE);

		ancestors.remove(ancestors.size() - 1);

		return true;
	}

	private static void _appendString(StringBuilder sb, String string) {
		sb.append(CharPool.QUOTE);

		int start = 0;

		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);

			if ((c >= _ESCAPES.length) || (_ESCAPES[c] == null)) {
				continue;
			}

			sb.append(string, start, i);
			sb.append(_ESCAPES[c]);

			start = i + 1;
		}

		sb.append(string, start, string.length());
		sb.append(CharPool.QUOTE);
	}

	private static List<Object> _getAncestors(List<Object> ancestors) {
		if (ancestors == null) {
			return new ArrayList<>();
		}

		return ancestors;
	}

	private static final String[] _ESCAPES = new String[160];

	private boolean _customized;
	private final JsonSerializer _jsonSerializer = new JsonSerializer();

	private static class JSONArrayTypeJSONSerializer
		implements TypeJsonSerializer<JSONArray> {

		@Override
		public boolean serialize(JsonContext jsonContext, JSONArray jsonArray) {
			jsonContext.write(jsonArray.toString());

			return true;
		}

	}

	private static class JSONObjectTypeJSONSerializer
		implements TypeJsonSerializer<JSONObject> {

		@Override
		public boolean serialize(
			JsonContext jsonContext, JSONObject jsonObject) {

			jsonContext.write(jsonObject.toString());

			return true;
		}

	}

	private static class LongToStringTypeJSONSerializer
		implements TypeJsonSerializer<Long> {

		@Override
		public boolean serialize(JsonContext jsonContext, Long value) {
			jsonContext.writeString(String.valueOf(value));

			return true;
		}

	}

	static {
		for (int i = 0; i < _ESCAPES.length; i++) {
			if (!Character.isISOControl(i)) {
				continue;
			}

			_ESCAPES[i] =
				UnicodeFormatter.UNICODE_PREFIX +
					UnicodeFormatter.charToHex((char)i, true);
		}

		_ESCAPES['\b'] = "\\b";
		_ESCAPES['\t'] = "\\t";
		_ESCAPES['\n'] = "\\n";
		_ESCAPES['\f'] = "\\f";
		_ESCAPES['\r'] = "\\r";
		_ESCAPES['"'] = "\\\"";
		_ESCAPES['/'] = "\\/";
		_ESCAPES['\\'] = "\\\\";

		TypeJsonSerializerMap typeJsonSerializerMap =
			TypeJsonSerializerMap.get();

		typeJsonSerializerMap.register(
			JSONArray.class, new JSONArrayTypeJSONSerializer());
		typeJsonSerializerMap.register(
			JSONObject.class, new JSONObjectTypeJSONSerializer());
		typeJsonSerializerMap.register(
			Long.TYPE, new LongToStringTypeJSONSerializer());
		typeJsonSerializerMap.register(
			Long.class, new LongToStringTypeJSONSerializer());
	}

}