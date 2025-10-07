/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.log;

import com.liferay.portal.kernel.internal.log4j.Log4jLogFactoryImpl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 * @author Raymond Augé
 */
public class LogFactoryUtil {

	public static Log getLog(Class<?> c) {
		return getLog(c.getName());
	}

	public static Log getLog(String name) {
		Log log = _logs.get(name);

		if (log == null) {
			if (SanitizerLogWrapper.isEnabled()) {
				log = new SanitizerLogWrapper(_logFactory.getLog(name));
			}
			else {
				log = _logFactory.getLog(name);
			}

			Log previousLog = _logs.putIfAbsent(name, log);

			if (previousLog != null) {
				log = previousLog;
			}
		}

		return log;
	}

	public static LogFactory getLogFactory() {
		return _logFactory;
	}

	private static final LogFactory _logFactory = new Log4jLogFactoryImpl();
	private static final ConcurrentMap<String, Log> _logs =
		new ConcurrentHashMap<>();

}