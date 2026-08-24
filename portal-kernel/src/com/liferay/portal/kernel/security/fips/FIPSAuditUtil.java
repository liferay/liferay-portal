/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.internal.log4j.FIPSLog4jUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;
import java.io.UncheckedIOException;

import java.lang.reflect.Array;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.Provider;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Jorge García Jiménez
 * @author Rafael Praxedes
 */
public class FIPSAuditUtil {

	public static void write(FIPSAuditEvent fipsAuditEvent) {
		Provider provider = FIPSModeValidator.fetchProvider();

		String providerName = StringPool.BLANK;
		String providerVersion = StringPool.BLANK;

		if (provider != null) {
			providerName = provider.getName();
			providerVersion = provider.getVersionStr();
		}

		FIPSAuditEvent.Severity severity = fipsAuditEvent.getSeverity();

		FIPSLog4jUtil.write(
			LinkedHashMapBuilder.<String, Object>put(
				"cmvp-certificate-id",
				GetterUtil.getString(
					PropsValues.FIPS_AUDIT_PROVIDER_CMVP_CERTIFICATE_ID)
			).put(
				"deployment-instance-id", _getDeploymentInstanceId()
			).put(
				"event-schema-version", "1.0"
			).put(
				"event-sequence", _eventSequence.incrementAndGet()
			).put(
				"event-type", fipsAuditEvent.getEventType()
			).put(
				"fields", _normalizeTimestamps(fipsAuditEvent.getFields())
			).put(
				"provider-name", providerName
			).put(
				"provider-version", providerVersion
			).put(
				"severity", severity.name()
			).put(
				"timestamp", _formatTimestamp(Instant.now())
			).build(),
			severity);
	}

	private static String _formatTimestamp(Instant instant) {
		return _dateTimeFormatter.format(instant.atZone(ZoneOffset.UTC));
	}

	private static String _getDeploymentInstanceId() {
		String deploymentInstanceId =
			PropsValues.FIPS_AUDIT_DEPLOYMENT_INSTANCE_ID;

		if (Validator.isNotNull(deploymentInstanceId)) {
			return deploymentInstanceId;
		}

		return _deploymentInstanceIdDCLSingleton.getSingleton(
			FIPSAuditUtil::_getPersistedDeploymentInstanceId);
	}

	private static String _getPersistedDeploymentInstanceId() {
		Path path = Paths.get(
			PropsValues.LIFERAY_HOME, "data",
			"fips-audit-deployment-instance-id");

		try {
			if (Files.exists(path)) {
				String persistedId = new String(
					Files.readAllBytes(path), StandardCharsets.UTF_8);

				return persistedId.trim();
			}

			String generatedId = String.valueOf(UUID.randomUUID());

			Files.createDirectories(path.getParent());

			Files.write(path, generatedId.getBytes(StandardCharsets.UTF_8));

			return generatedId;
		}
		catch (IOException ioException) {
			throw new UncheckedIOException(
				"Unable to resolve the FIPS audit deployment instance ID",
				ioException);
		}
	}

	private static Object _normalizeTimestamp(Object value) {
		if (value instanceof Date) {
			Date date = (Date)value;

			return _formatTimestamp(date.toInstant());
		}

		if (value instanceof Iterable) {
			List<Object> normalizedValues = new ArrayList<>();

			for (Object curValue : (Iterable<?>)value) {
				normalizedValues.add(_normalizeTimestamp(curValue));
			}

			return normalizedValues;
		}

		if (value instanceof Map) {
			return _normalizeTimestamps((Map<?, ?>)value);
		}

		if (value instanceof TemporalAccessor) {
			TemporalAccessor temporalAccessor = (TemporalAccessor)value;

			try {
				return _formatTimestamp(Instant.from(temporalAccessor));
			}
			catch (DateTimeException dateTimeException) {
				throw new IllegalArgumentException(
					StringBundler.concat(
						"Unable to normalize the FIPS audit timestamp \"",
						temporalAccessor, "\" because it carries no time zone"),
					dateTimeException);
			}
		}

		Class<?> valueClass = value.getClass();

		if (valueClass.isArray()) {
			List<Object> normalizedValues = new ArrayList<>();

			for (int i = 0; i < Array.getLength(value); i++) {
				normalizedValues.add(_normalizeTimestamp(Array.get(value, i)));
			}

			return normalizedValues;
		}

		return value;
	}

	private static Map<String, Object> _normalizeTimestamps(Map<?, ?> map) {
		Map<String, Object> normalizedMap = new LinkedHashMap<>();

		for (Map.Entry<?, ?> entry : map.entrySet()) {
			normalizedMap.put(
				String.valueOf(entry.getKey()),
				_normalizeTimestamp(entry.getValue()));
		}

		return normalizedMap;
	}

	private static final DateTimeFormatter _dateTimeFormatter =
		DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private static final DCLSingleton<String>
		_deploymentInstanceIdDCLSingleton = new DCLSingleton<>();
	private static final AtomicLong _eventSequence = new AtomicLong();

}