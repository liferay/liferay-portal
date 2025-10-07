/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.log4j;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogWrapper;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

/**
 * @author Brian Wing Shun Chan
 */
public class Log4jLogImpl implements Log {

	public Log4jLogImpl(Logger logger) {
		_logger = (org.apache.logging.log4j.core.Logger)logger;
	}

	@Override
	public void debug(Object msg) {
		_logger.logIfEnabled(
			_logWrapperClassName, Level.DEBUG, null, msg, null);
	}

	@Override
	public void debug(Object msg, Throwable throwable) {
		_logger.logIfEnabled(
			_logWrapperClassName, Level.DEBUG, null, msg, throwable);
	}

	@Override
	public void debug(Throwable throwable) {
		_logger.logIfEnabled(
			_logWrapperClassName, Level.DEBUG, null, throwable.getMessage(),
			throwable);
	}

	@Override
	public void error(Object msg) {
		_logger.logIfEnabled(
			_logWrapperClassName, Level.ERROR, null, msg, null);
	}

	@Override
	public void error(Object msg, Throwable throwable) {
		_logger.logIfEnabled(
			_logWrapperClassName, Level.ERROR, null, msg, throwable);
	}

	@Override
	public void error(Throwable throwable) {
		_logger.logIfEnabled(
			_logWrapperClassName, Level.ERROR, null, throwable.getMessage(),
			throwable);
	}

	@Override
	public void fatal(Object msg) {
		_logger.logIfEnabled(
			_logWrapperClassName, Level.FATAL, null, msg, null);
	}

	@Override
	public void fatal(Object msg, Throwable throwable) {
		_logger.logIfEnabled(
			_logWrapperClassName, Level.FATAL, null, msg, throwable);
	}

	@Override
	public void fatal(Throwable throwable) {
		_logger.logIfEnabled(
			_logWrapperClassName, Level.FATAL, null, throwable.getMessage(),
			throwable);
	}

	@Override
	public void info(Object msg) {
		_logger.logIfEnabled(_logWrapperClassName, Level.INFO, null, msg, null);
	}

	@Override
	public void info(Object msg, Throwable throwable) {
		_logger.logIfEnabled(
			_logWrapperClassName, Level.INFO, null, msg, throwable);
	}

	@Override
	public void info(Throwable throwable) {
		_logger.logIfEnabled(
			_logWrapperClassName, Level.INFO, null, throwable.getMessage(),
			throwable);
	}

	@Override
	public boolean isDebugEnabled() {
		return _logger.isDebugEnabled();
	}

	@Override
	public boolean isErrorEnabled() {
		return _logger.isErrorEnabled();
	}

	@Override
	public boolean isFatalEnabled() {
		return _logger.isFatalEnabled();
	}

	@Override
	public boolean isInfoEnabled() {
		return _logger.isInfoEnabled();
	}

	@Override
	public boolean isTraceEnabled() {
		return _logger.isTraceEnabled();
	}

	@Override
	public boolean isWarnEnabled() {
		return _logger.isWarnEnabled();
	}

	@Override
	public void setLogWrapperClassName(String className) {
		_logWrapperClassName = className;
	}

	@Override
	public void trace(Object msg) {
		_logger.logIfEnabled(
			_logWrapperClassName, Level.TRACE, null, msg, null);
	}

	@Override
	public void trace(Object msg, Throwable throwable) {
		_logger.logIfEnabled(
			_logWrapperClassName, Level.TRACE, null, msg, throwable);
	}

	@Override
	public void trace(Throwable throwable) {
		_logger.logIfEnabled(
			_logWrapperClassName, Level.TRACE, null, throwable.getMessage(),
			throwable);
	}

	@Override
	public void warn(Object msg) {
		_logger.logIfEnabled(_logWrapperClassName, Level.WARN, null, msg, null);
	}

	@Override
	public void warn(Object msg, Throwable throwable) {
		_logger.logIfEnabled(
			_logWrapperClassName, Level.WARN, null, msg, throwable);
	}

	@Override
	public void warn(Throwable throwable) {
		_logger.logIfEnabled(
			_logWrapperClassName, Level.WARN, null, throwable.getMessage(),
			throwable);
	}

	private final org.apache.logging.log4j.core.Logger _logger;
	private String _logWrapperClassName = LogWrapper.class.getName();

}