/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.log4j;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogContext;
import com.liferay.portal.kernel.log.LogWrapper;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.ThreadContext;

/**
 * @author Tina Tian
 */
public class Log4jLogContextLogWrapper extends LogWrapper {

	public Log4jLogContextLogWrapper(Log log, String name) {
		super(log);

		_name = name;

		setLogWrapperClassName(Log4jLogContextLogWrapper.class.getName());
	}

	@Override
	public void debug(Object message) {
		Map<String, String> context = _getContext();

		ThreadContext.putAll(context);

		super.debug(message);

		ThreadContext.removeAll(context.keySet());
	}

	@Override
	public void debug(Object message, Throwable throwable) {
		Map<String, String> context = _getContext();

		ThreadContext.putAll(context);

		super.debug(message, throwable);

		ThreadContext.removeAll(context.keySet());
	}

	@Override
	public void debug(Throwable throwable) {
		Map<String, String> context = _getContext();

		ThreadContext.putAll(context);

		super.debug(throwable);

		ThreadContext.removeAll(context.keySet());
	}

	@Override
	public void error(Object message) {
		Map<String, String> context = _getContext();

		ThreadContext.putAll(context);

		super.error(message);

		ThreadContext.removeAll(context.keySet());
	}

	@Override
	public void error(Object message, Throwable throwable) {
		Map<String, String> context = _getContext();

		ThreadContext.putAll(context);

		super.error(message, throwable);

		ThreadContext.removeAll(context.keySet());
	}

	@Override
	public void error(Throwable throwable) {
		Map<String, String> context = _getContext();

		ThreadContext.putAll(context);

		super.error(throwable);

		ThreadContext.removeAll(context.keySet());
	}

	@Override
	public void fatal(Object message) {
		Map<String, String> context = _getContext();

		ThreadContext.putAll(context);

		super.fatal(message);

		ThreadContext.removeAll(context.keySet());
	}

	@Override
	public void fatal(Object message, Throwable throwable) {
		Map<String, String> context = _getContext();

		ThreadContext.putAll(context);

		super.fatal(message, throwable);

		ThreadContext.removeAll(context.keySet());
	}

	@Override
	public void fatal(Throwable throwable) {
		Map<String, String> context = _getContext();

		ThreadContext.putAll(context);

		super.fatal(throwable);

		ThreadContext.removeAll(context.keySet());
	}

	@Override
	public void info(Object message) {
		Map<String, String> context = _getContext();

		ThreadContext.putAll(context);

		super.info(message);

		ThreadContext.removeAll(context.keySet());
	}

	@Override
	public void info(Object message, Throwable throwable) {
		Map<String, String> context = _getContext();

		ThreadContext.putAll(context);

		super.info(message, throwable);

		ThreadContext.removeAll(context.keySet());
	}

	@Override
	public void info(Throwable throwable) {
		Map<String, String> context = _getContext();

		ThreadContext.putAll(context);

		super.info(throwable);

		ThreadContext.removeAll(context.keySet());
	}

	@Override
	public void trace(Object message) {
		Map<String, String> context = _getContext();

		ThreadContext.putAll(context);

		super.trace(message);

		ThreadContext.removeAll(context.keySet());
	}

	@Override
	public void trace(Object message, Throwable throwable) {
		Map<String, String> context = _getContext();

		ThreadContext.putAll(context);

		super.trace(message, throwable);

		ThreadContext.removeAll(context.keySet());
	}

	@Override
	public void trace(Throwable throwable) {
		Map<String, String> context = _getContext();

		ThreadContext.putAll(context);

		super.trace(throwable);

		ThreadContext.removeAll(context.keySet());
	}

	@Override
	public void warn(Object message) {
		Map<String, String> context = _getContext();

		ThreadContext.putAll(context);

		super.warn(message);

		ThreadContext.removeAll(context.keySet());
	}

	@Override
	public void warn(Object message, Throwable throwable) {
		Map<String, String> context = _getContext();

		ThreadContext.putAll(context);

		super.warn(message, throwable);

		ThreadContext.removeAll(context.keySet());
	}

	@Override
	public void warn(Throwable throwable) {
		Map<String, String> context = _getContext();

		ThreadContext.putAll(context);

		super.warn(throwable);

		ThreadContext.removeAll(context.keySet());
	}

	private static ServiceTrackerList<LogContext> _createServiceTrackerList() {
		try {
			return ServiceTrackerListFactory.open(
				SystemBundleUtil.getBundleContext(), LogContext.class);
		}
		catch (Throwable throwable) {
			return null;
		}
	}

	private Map<String, String> _getContext() {
		Map<String, String> context = new HashMap<>();

		ServiceTrackerList<LogContext> serviceTrackerList =
			_serviceTrackerListDCLSingleton.getSingleton(
				Log4jLogContextLogWrapper::_createServiceTrackerList);

		if (serviceTrackerList == null) {
			return context;
		}

		for (LogContext logContext : serviceTrackerList) {
			Map<String, String> logContextContext = logContext.getContext(
				_name);

			for (Map.Entry<String, String> entry :
					logContextContext.entrySet()) {

				String key = entry.getKey();

				String logContextName = logContext.getName();

				if (Validator.isNotNull(logContextName)) {
					key = logContextName + "." + key;
				}

				context.put(key, entry.getValue());
			}
		}

		return context;
	}

	private static final DCLSingleton<ServiceTrackerList<LogContext>>
		_serviceTrackerListDCLSingleton = new DCLSingleton<>();

	private final String _name;

}