/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.log4j;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.File;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.apache.logging.log4j.core.config.xml.XmlConfiguration;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author Tina Tian
 */
public class Log4jConfigUtil {

	public static Map<String, String> configureLog4J(
		String xmlContent, String... removedAppenderNames) {

		try {
			SAXReader saxReader = new SAXReader();

			Document document = saxReader.read(
				new UnsyncStringReader(xmlContent));

			Element rootElement = document.getRootElement();

			if (!Objects.equals(rootElement.getName(), "Configuration")) {
				_log.error("Log4J 2 <Configuration> is required");

				return Collections.emptyMap();
			}

			if (!GetterUtil.getBoolean(rootElement.attributeValue("strict"))) {
				_log.error("<Configuration> strict attribute requires true");

				return Collections.emptyMap();
			}

			Map<String, String> priorities = new HashMap<>();

			for (Element element : rootElement.elements()) {
				_removeAppender(element, removedAppenderNames);

				for (Element childElement : element.elements("Logger")) {
					priorities.put(
						childElement.attributeValue("name"),
						childElement.attributeValue("level"));
				}
			}

			if (removedAppenderNames.length > 0) {
				xmlContent = document.asXML();
			}

			AbstractConfiguration abstractConfiguration = new XmlConfiguration(
				_loggerContext,
				new ConfigurationSource(
					new UnsyncByteArrayInputStream(
						xmlContent.getBytes(StringPool.UTF8)))) {

				@Override
				protected void setToDefault() {
				}

			};

			_centralizedConfiguration.addConfiguration(abstractConfiguration);

			return priorities;
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return Collections.emptyMap();
	}

	public static File getCompanyLogDirectory(long companyId) {
		return _centralizedConfiguration.getCompanyLogDirectory(companyId);
	}

	public static java.util.logging.Level getJDKLevel(String levelString) {
		if (StringUtil.equalsIgnoreCase(levelString, Level.DEBUG.toString())) {
			return java.util.logging.Level.FINE;
		}
		else if (StringUtil.equalsIgnoreCase(
					levelString, Level.ERROR.toString())) {

			return java.util.logging.Level.SEVERE;
		}
		else if (StringUtil.equalsIgnoreCase(
					levelString, Level.WARN.toString())) {

			return java.util.logging.Level.WARNING;
		}

		return java.util.logging.Level.INFO;
	}

	public static Map<String, String> getPriorities() {
		Map<String, String> priorities = new HashMap<>();

		Map<String, LoggerConfig> loggerConfigs =
			_centralizedConfiguration.getLoggers();

		for (LoggerConfig loggerConfig : loggerConfigs.values()) {
			String loggerConfigName = loggerConfig.getName();

			if (!Objects.equals(
					loggerConfigName, LogManager.ROOT_LOGGER_NAME)) {

				priorities.put(
					loggerConfigName, String.valueOf(loggerConfig.getLevel()));
			}
		}

		return priorities;
	}

	public static void setLevel(String name, String priority) {
		Level level = Level.toLevel(priority);

		LoggerConfig loggerConfig = _centralizedConfiguration.getLogger(name);

		if (loggerConfig == null) {
			loggerConfig = new LoggerConfig(name, level, true);

			_centralizedConfiguration.addLogger(name, loggerConfig);
		}
		else {
			loggerConfig.setLevel(level);
		}

		_loggerContext.updateLoggers();
	}

	public static void shutdownLog4J() {
		LogManager.shutdown();
	}

	private static void _removeAppender(
		Element parentElement, String... removedAppenderNames) {

		if (removedAppenderNames.length == 0) {
			return;
		}

		for (Element element : parentElement.elements()) {
			for (String appenderName : removedAppenderNames) {
				if (Objects.equals(element.getName(), "Appender") &&
					Objects.equals(
						element.attributeValue("name"), appenderName)) {

					parentElement.remove(element);
				}

				for (Element childElement : element.elements()) {
					if (Objects.equals(childElement.getName(), "AppenderRef") &&
						Objects.equals(
							childElement.attributeValue("ref"), appenderName)) {

						element.remove(childElement);
					}
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		Log4jConfigUtil.class);

	private static final CentralizedConfiguration _centralizedConfiguration;
	private static final LoggerContext _loggerContext =
		LoggerContext.getContext();

	static {
		PluginManager.addPackage("com.liferay.portal.kernel.internal.log4j");

		_centralizedConfiguration = new CentralizedConfiguration(
			_loggerContext);

		_loggerContext.setConfiguration(_centralizedConfiguration);
	}

}