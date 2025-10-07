/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osgi.log.service.extender.internal.osgi.commands;

import com.liferay.osgi.util.osgi.commands.OSGiCommands;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.log4j.Log4JUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.log.LogLevel;
import org.osgi.service.log.admin.LoggerAdmin;
import org.osgi.service.log.admin.LoggerContext;

/**
 * @author Raymond Augé
 */
public class LoggingOSGiCommands implements OSGiCommands {

	public LoggingOSGiCommands(LoggerAdmin loggerAdmin) {
		_loggerAdmin = loggerAdmin;
	}

	public String level(String context, String name, String level) {
		Objects.requireNonNull(name);

		LoggerContext loggerContext = _loggerAdmin.getLoggerContext(context);

		Map<String, LogLevel> logLevels = loggerContext.getLogLevels();

		LogLevel logLevel = LogLevel.ERROR;

		try {
			if (level == null) {
				logLevels.remove(name);
			}
			else {
				logLevel = LogLevel.valueOf(StringUtil.toUpperCase(level));

				logLevels.put(name, logLevel);
			}

			Log4JUtil.setLevel(
				"osgi.logging.".concat(name), logLevel.name(), false);

			loggerContext.setLogLevels(logLevels);

			return StringBundler.concat(
				name, StringPool.EQUAL, logLevel.name());
		}
		catch (IllegalArgumentException illegalArgumentException) {
			if (_log.isDebugEnabled()) {
				_log.debug(illegalArgumentException);
			}

			return "Invalid log level: " + level;
		}
	}

	public String[] levels(String context) {
		LoggerContext loggerContext = _loggerAdmin.getLoggerContext(context);

		List<String> categories = new ArrayList<>();

		Map<String, LogLevel> logLevels = loggerContext.getLogLevels();

		for (Map.Entry<String, LogLevel> entry : logLevels.entrySet()) {
			LogLevel logLevel = entry.getValue();

			categories.add(
				StringBundler.concat(
					entry.getKey(), StringPool.EQUAL, logLevel.name()));
		}

		return categories.toArray(new String[0]);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LoggingOSGiCommands.class);

	private final LoggerAdmin _loggerAdmin;

}