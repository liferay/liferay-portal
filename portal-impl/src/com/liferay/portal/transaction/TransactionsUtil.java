/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.transaction;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;

/**
 * @author Miguel Pastor
 */
public class TransactionsUtil {

	public static void disableTransactions() {
		if (_log.isDebugEnabled()) {
			_log.debug("Disable transactions");
		}

		PropsValues.SPRING_HIBERNATE_SESSION_DELEGATED = false;

		_enabled = false;
	}

	public static void enableTransactions() {
		if (_log.isDebugEnabled()) {
			_log.debug("Enable transactions");
		}

		PropsValues.SPRING_HIBERNATE_SESSION_DELEGATED = GetterUtil.getBoolean(
			PropsUtil.get(PropsKeys.SPRING_HIBERNATE_SESSION_DELEGATED));

		_enabled = true;
	}

	public static boolean isEnabled() {
		return _enabled;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TransactionsUtil.class);

	private static boolean _enabled = true;

}