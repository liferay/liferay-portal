/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.petra.string.StringBundler;

import java.lang.reflect.Array;

import java.security.Key;
import java.security.spec.KeySpec;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jorge García Jiménez
 */
public class FIPSAuditEvent {

	public FIPSAuditEvent(String eventType, Severity severity) {
		_eventType = eventType;
		_severity = severity;
	}

	public String getEventType() {
		return _eventType;
	}

	public Map<String, Object> getFields() {
		return Collections.unmodifiableMap(_fields);
	}

	public Severity getSeverity() {
		return _severity;
	}

	public void put(String key, Object value) {
		_validate(key, value);

		_fields.put(key, value);
	}

	public enum Severity {

		CRITICAL, INFO, WARNING

	}

	private boolean _isNonfiniteNumber(Object value) {
		if (value instanceof Double) {
			return !Double.isFinite((Double)value);
		}

		if (value instanceof Float) {
			return !Float.isFinite((Float)value);
		}

		return false;
	}

	private boolean _isSensitiveSecurityParameter(Object value) {
		if (value instanceof byte[] || value instanceof char[] ||
			value instanceof Key || value instanceof KeySpec) {

			return true;
		}

		return false;
	}

	private void _validate(String key, Object value) {
		if (value == null) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"Unable to write the FIPS audit field \"", key,
					"\" because the JSON serializer drops a null value"));
		}

		if (_isSensitiveSecurityParameter(value)) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"Unable to write the FIPS audit field \"", key,
					"\" because a sensitive security parameter must never ",
					"reach a FIPS audit event"));
		}

		if (value instanceof Iterable) {
			for (Object curValue : (Iterable<?>)value) {
				_validate(key, curValue);
			}

			return;
		}

		if (value instanceof Map) {
			Map<?, ?> map = (Map<?, ?>)value;

			for (Map.Entry<?, ?> entry : map.entrySet()) {
				_validate(key, entry.getValue());
			}

			return;
		}

		Class<?> valueClass = value.getClass();

		if (valueClass.isArray()) {
			for (int i = 0; i < Array.getLength(value); i++) {
				_validate(key, Array.get(value, i));
			}

			return;
		}

		if (_isNonfiniteNumber(value)) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"Unable to write the FIPS audit field \"", key,
					"\" because the number \"", value,
					"\" is not finite and has no JSON representation"));
		}
	}

	private final String _eventType;
	private final Map<String, Object> _fields = new HashMap<>();
	private final Severity _severity;

}