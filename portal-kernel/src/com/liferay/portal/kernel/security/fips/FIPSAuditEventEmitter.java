/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;

import java.io.Flushable;
import java.io.IOException;
import java.io.UncheckedIOException;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Writes FIPS audit events as NDJSON records, one per line, each sharing a
 * common envelope, in key order: the CMVP certificate ID and deployment
 * instance ID, event schema version, event type, the validated provider name
 * and version active at emission, severity, and the §5.1 timestamp. Every
 * record is flushed synchronously, so an Error State entry reaches disk before
 * the process continues; nothing is buffered in memory.
 *
 * <p>
 * Event specific fields are nested under a single {@code fields} object rather
 * than merged into the envelope, so an event can never overwrite an envelope
 * key and misattribute the record.
 * </p>
 *
 * <p>
 * Every record carries the §5.1 timestamp in one canonical representation: UTC,
 * ISO 8601 extended form with millisecond precision and a literal
 * <code>Z</code> suffix (for example <code>2026-05-06T14:19:23.471Z</code>),
 * read from the host clock and emitted in UTC regardless of the host default
 * time zone. Sub millisecond precision is truncated rather than rounded. The
 * timestamp alone does not order two events emitted within the same
 * millisecond, so the §5.4 audit log integrity chain, not the timestamp, is the
 * authority on order.
 * </p>
 *
 * <p>
 * The emitter is intentionally free of OSGi and framework dependencies. FIPS
 * finite-state-model transitions are emitted during boot, before the audit
 * router is available, so the envelope sources are injected as suppliers and
 * records are written straight to the supplied {@link Appendable}.
 * </p>
 *
 * @author Jorge García Jiménez
 */
public class FIPSAuditEventEmitter {

	public FIPSAuditEventEmitter(
		Appendable appendable, Supplier<String> cmvpCertificateIdSupplier,
		Supplier<String> deploymentInstanceIdSupplier,
		Supplier<String> providerNameSupplier,
		Supplier<String> providerVersionSupplier) {

		_appendable = appendable;
		_cmvpCertificateIdSupplier = cmvpCertificateIdSupplier;
		_deploymentInstanceIdSupplier = deploymentInstanceIdSupplier;
		_providerNameSupplier = providerNameSupplier;
		_providerVersionSupplier = providerVersionSupplier;
	}

	public void emit(FIPSAuditEvent fipsAuditEvent) {
		Map<String, Object> record = LinkedHashMapBuilder.<String, Object>put(
			"cmvp-certificate-id", _cmvpCertificateIdSupplier.get()
		).put(
			"deployment-instance-id", _deploymentInstanceIdSupplier.get()
		).put(
			"event-schema-version", "1.0"
		).put(
			"event-type", fipsAuditEvent.getEventType()
		).put(
			"fields", fipsAuditEvent.getFields()
		).put(
			"provider-name", _providerNameSupplier.get()
		).put(
			"provider-version", _providerVersionSupplier.get()
		).put(
			"severity", _getFIPSAuditSeverityValue(fipsAuditEvent)
		).put(
			"timestamp", _getTimestamp()
		).build();

		try {
			_appendable.append(_toNDJSON(record));

			if (_appendable instanceof Flushable) {
				Flushable flushable = (Flushable)_appendable;

				flushable.flush();
			}
		}
		catch (IOException ioException) {
			throw new UncheckedIOException(
				"Unable to write FIPS audit event", ioException);
		}
	}

	private String _escape(String value) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);

			if (c == '\\') {
				sb.append("\\\\");
			}
			else if (c == '\"') {
				sb.append("\\\"");
			}
			else if (c == '\n') {
				sb.append("\\n");
			}
			else if (c == '\r') {
				sb.append("\\r");
			}
			else if (c == '\t') {
				sb.append("\\t");
			}
			else if (c < 0x20) {
				sb.append(String.format("\\u%04x", (int)c));
			}
			else {
				sb.append(c);
			}
		}

		return sb.toString();
	}

	private String _getFIPSAuditSeverityValue(FIPSAuditEvent fipsAuditEvent) {
		FIPSAuditSeverity fipsAuditSeverity =
			fipsAuditEvent.getFIPSAuditSeverity();

		return fipsAuditSeverity.getValue();
	}

	private String _getTimestamp() {
		Instant instant = Instant.now();

		return _dateTimeFormatter.format(instant.atZone(ZoneOffset.UTC));
	}

	private String _toJSONObject(Map<?, ?> map) {
		StringBundler sb = new StringBundler();

		sb.append("{");

		boolean first = true;

		for (Map.Entry<?, ?> entry : map.entrySet()) {
			if (!first) {
				sb.append(",");
			}

			first = false;

			sb.append("\"");
			sb.append(_escape(String.valueOf(entry.getKey())));
			sb.append("\":");
			sb.append(_toJSONValue(entry.getValue()));
		}

		sb.append("}");

		return sb.toString();
	}

	private String _toJSONValue(Object value) {
		if (value == null) {
			return "null";
		}

		if (value instanceof Boolean || value instanceof Number) {
			return value.toString();
		}

		if (value instanceof Map) {
			return _toJSONObject((Map<?, ?>)value);
		}

		return "\"" + _escape(value.toString()) + "\"";
	}

	private String _toNDJSON(Map<String, Object> record) {
		return _toJSONObject(record) + "\n";
	}

	private static final DateTimeFormatter _dateTimeFormatter =
		DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	private final Appendable _appendable;
	private final Supplier<String> _cmvpCertificateIdSupplier;
	private final Supplier<String> _deploymentInstanceIdSupplier;
	private final Supplier<String> _providerNameSupplier;
	private final Supplier<String> _providerVersionSupplier;

}