/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.log4j;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactory;

import org.apache.logging.log4j.LogManager;

/**
 * @author Brian Wing Shun Chan
 */
public class Log4jLogFactoryImpl implements LogFactory {

	@Override
	public Log getLog(Class<?> c) {
		return getLog(c.getName());
	}

	@Override
	public Log getLog(String name) {
		return new Log4jLogContextLogWrapper(
			new Log4jLogImpl(LogManager.getLogger(name)), name);
	}

}