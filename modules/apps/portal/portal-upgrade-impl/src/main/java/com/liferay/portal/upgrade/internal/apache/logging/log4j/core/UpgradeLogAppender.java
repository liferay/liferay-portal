/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.internal.apache.logging.log4j.core;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.dao.db.DuplicateUniqueFinderRowsCleaner;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.OrphanReferencesDataCleanupUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.upgrade.internal.recorder.UpgradeRecorder;
import com.liferay.portal.upgrade.internal.report.UpgradeReport;

import java.io.Serializable;

import java.util.Objects;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.ErrorHandler;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sam Ziemer
 */
@Component(
	property = "appender.name=UpgradeLogAppender", service = Appender.class
)
public class UpgradeLogAppender implements Appender {

	@Override
	public void append(LogEvent logEvent) {
		Message message = logEvent.getMessage();

		String formattedMessage = message.getFormattedMessage();

		if (formattedMessage.equals(StringPool.NULL)) {
			Throwable throwable = logEvent.getThrown();

			formattedMessage = throwable.getMessage();
		}

		if (logEvent.getLevel() == Level.ERROR) {
			_upgradeRecorder.recordErrorMessage(
				logEvent.getLoggerName(), formattedMessage,
				logEvent.getThrown());
		}
		else if (logEvent.getLevel() == Level.INFO) {
			if (_isDataCleanupMessage(logEvent.getLoggerName())) {
				_upgradeRecorder.recordDataCleanupMessage(
					logEvent.getLoggerName(), formattedMessage);

				return;
			}

			if (Objects.equals(
					logEvent.getLoggerName(), UpgradeProcess.class.getName()) &&
				formattedMessage.startsWith("Completed upgrade process ")) {

				_upgradeRecorder.recordUpgradeProcessMessage(
					logEvent.getLoggerName(), formattedMessage);
			}
		}
		else if (logEvent.getLevel() == Level.WARN) {
			if (_isDataCleanupMessage(logEvent.getLoggerName())) {
				_upgradeRecorder.recordDataCleanupMessage(
					logEvent.getLoggerName(), formattedMessage);

				return;
			}

			_upgradeRecorder.recordWarningMessage(
				logEvent.getLoggerName(), message.getFormattedMessage());
		}
	}

	@Override
	public ErrorHandler getHandler() {
		return null;
	}

	@Override
	public Layout<? extends Serializable> getLayout() {
		return null;
	}

	@Override
	public String getName() {
		return "UpgradeLogAppender";
	}

	@Override
	public State getState() {
		return null;
	}

	@Override
	public boolean ignoreExceptions() {
		return false;
	}

	@Override
	public void initialize() {
	}

	@Override
	public boolean isStarted() {
		return _started;
	}

	@Override
	public boolean isStopped() {
		return !_started;
	}

	@Override
	public void setHandler(ErrorHandler handler) {
	}

	@Override
	public void start() {
		_started = true;

		_upgradeRecorder.start();

		if (PropsValues.UPGRADE_REPORT_ENABLED &&
			!StartupHelperUtil.isDBNew()) {

			_upgradeReport = new UpgradeReport();
		}

		_rootLogger.addAppender(this);
	}

	@Override
	public void stop() {
		if (_started) {
			_upgradeRecorder.stop();

			if (PropsValues.UPGRADE_REPORT_ENABLED &&
				!StartupHelperUtil.isDBNew()) {

				_upgradeReport.generateReport(_upgradeRecorder);

				_upgradeReport = null;
			}
		}

		_started = false;

		_rootLogger.removeAppender(this);
	}

	private boolean _isDataCleanupMessage(String loggerName) {
		try {
			ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

			Class<?> clazz = classLoader.loadClass(loggerName);

			if (clazz.equals(DuplicateUniqueFinderRowsCleaner.class) ||
				clazz.equals(OrphanReferencesDataCleanupUtil.class) ||
				DataCleanupPreupgradeProcess.class.isAssignableFrom(clazz)) {

				return true;
			}
		}
		catch (ClassNotFoundException classNotFoundException) {
			if (_log.isDebugEnabled()) {
				_log.debug(classNotFoundException);
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradeLogAppender.class);

	private static final Logger _rootLogger =
		(Logger)LogManager.getRootLogger();

	private volatile boolean _started;

	@Reference
	private volatile UpgradeRecorder _upgradeRecorder;

	private UpgradeReport _upgradeReport;

}