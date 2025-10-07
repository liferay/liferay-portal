/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.jaxrs.whiteboard.internal.log;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.apache.cxf.common.logging.AbstractDelegatingLogger;

/**
 * Logger for CXF that maps {@link java.util.logging.Logger} to Liferay {@link
 * Log}.
 *
 * @author Tomas Polesovsky
 * @review
 */
public class LiferayCXFLogger extends AbstractDelegatingLogger {

	public LiferayCXFLogger(String name, String resourceBundleName) {
		super(name, resourceBundleName);

		_log = LogFactoryUtil.getLog(name);

		if (_log.isTraceEnabled()) {
			_level = Level.FINEST;
		}
		else if (_log.isDebugEnabled()) {
			_level = Level.FINER;
		}
		else if (_log.isInfoEnabled()) {
			_level = Level.INFO;
		}
		else if (_log.isWarnEnabled()) {
			_level = Level.WARNING;
		}
		else if (_log.isErrorEnabled()) {
			_level = Level.WARNING;
		}
		else {
			_level = Level.OFF;
		}
	}

	@Override
	public Level getLevel() {
		return _level;
	}

	@Override
	protected void internalLogFormatted(String msg, LogRecord logRecord) {
		Level level = logRecord.getLevel();

		if (level == null) {
			_logError(level, msg, logRecord);

			return;
		}

		if (level.equals(Level.CONFIG)) {
			if (_log.isInfoEnabled()) {
				_log.info(msg, logRecord.getThrown());
			}
		}
		else if (level.equals(Level.FINE)) {
			if (_log.isDebugEnabled()) {
				_log.debug(msg, logRecord.getThrown());
			}
		}
		else if (level.equals(Level.FINER)) {
			if (_log.isDebugEnabled()) {
				_log.debug(msg, logRecord.getThrown());
			}
		}
		else if (level.equals(Level.FINEST)) {
			if (_log.isTraceEnabled()) {
				_log.trace(msg, logRecord.getThrown());
			}
		}
		else if (level.equals(Level.INFO)) {
			if (_log.isInfoEnabled()) {
				_log.info(msg, logRecord.getThrown());
			}
		}
		else if (level.equals(Level.SEVERE)) {
			if (_log.isErrorEnabled()) {
				_log.error(msg, logRecord.getThrown());
			}
		}
		else if (level.equals(Level.WARNING)) {
			if (_log.isWarnEnabled()) {
				_log.warn(msg, logRecord.getThrown());
			}
		}
		else {
			_logError(level, msg, logRecord);
		}
	}

	private void _logError(Level level, String msg, LogRecord logRecord) {
		if (!_classLog.isErrorEnabled()) {
			return;
		}

		if (level == null) {
			_classLog.error("Unable to map Log4j level: null");
		}
		else {
			_classLog.error("Unable to map Log4j level: " + level.getName());
		}

		_classLog.error("Original message: " + msg, logRecord.getThrown());
	}

	private static final Log _classLog = LogFactoryUtil.getLog(
		LiferayCXFLogger.class);

	private final Level _level;
	private final Log _log;

}