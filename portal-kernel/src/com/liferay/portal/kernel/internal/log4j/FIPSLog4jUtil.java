/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.log4j;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.security.fips.FIPSAuditEvent;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ServerDetector;

import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.message.ObjectMessage;

/**
 * @author Jorge García Jiménez
 * @author Rafael Praxedes
 */
public class FIPSLog4jUtil {

	public static Marker getMarker() {
		return _marker;
	}

	public static void validate(FIPSAuditEvent.Severity severity) {
		_validate(_getLevel(severity));
	}

	public static void write(
		Map<String, Object> fipsAuditLogEntry,
		FIPSAuditEvent.Severity severity) {

		Level level = _getLevel(severity);

		_validate(level);

		_logger.log(level, _marker, new ObjectMessage(fipsAuditLogEntry));
	}

	private static Level _getLevel(FIPSAuditEvent.Severity severity) {
		if (severity == FIPSAuditEvent.Severity.CRITICAL) {
			return Level.ERROR;
		}

		return Level.INFO;
	}

	private static void _validate(Level level) {
		if (!PropsValues.FIPS_ENABLED ||
			(ServerDetector.getServerId() == null)) {

			return;
		}

		if (!_logger.isEnabled(level)) {
			throw new IllegalStateException(
				StringBundler.concat(
					"Unable to write a FIPS audit event because the logger \"",
					FIPSLog4jUtil.class.getName(),
					"\" is disabled for the level \"", level,
					"\". Check that the system property ",
					"\"log4j.configure.on.startup\" is set to true and that ",
					"no configuration raises the level of that logger"));
		}

		LoggerContext loggerContext = (LoggerContext)LogManager.getContext(
			false);

		Configuration configuration = loggerContext.getConfiguration();

		Appender appender = configuration.getAppender(_APPENDER_NAME);

		if (!(appender instanceof RollingFileAppender)) {
			throw new IllegalStateException(
				StringBundler.concat(
					"Unable to write a FIPS audit event because the appender ",
					"\"", _APPENDER_NAME, "\" is not configured"));
		}

		if (!(appender.getLayout() instanceof FIPSAuditNDJSONLayout)) {
			throw new IllegalStateException(
				StringBundler.concat(
					"Unable to write a FIPS audit event because the appender ",
					"\"", _APPENDER_NAME, "\" does not use the layout \"",
					FIPSAuditNDJSONLayout.PLUGIN_NAME, "\""));
		}
	}

	private static final String _APPENDER_NAME = "FIPS_AUDIT_FILE";

	private static final Logger _logger = LogManager.getLogger(
		FIPSLog4jUtil.class);

	private static final Marker _marker = MarkerManager.getMarker(
		"FIPS_AUDIT_MARKER");

}