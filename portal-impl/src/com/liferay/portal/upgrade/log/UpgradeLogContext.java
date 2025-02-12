/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.log;

import com.liferay.portal.dao.db.BaseDB;
import com.liferay.portal.kernel.dao.db.BaseDBProcess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogContext;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.tools.DBUpgrader;
import com.liferay.portal.verify.VerifyProperties;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author Luis Ortiz
 */
public class UpgradeLogContext implements LogContext {

	public static void clearContext() {
		_component = "framework";
	}

	public static LogContext getInstance() {
		return _INSTANCE;
	}

	public static void setContext(String component) {
		_component = component;
	}

	@Override
	public Map<String, String> getContext(String logName) {
		if (_isUpgradeClass(logName)) {
			return Collections.singletonMap("component", _component);
		}

		return Collections.emptyMap();
	}

	@Override
	public String getName() {
		return "upgrade";
	}

	private boolean _isUpgradeClass(String name) {
		try {
			if (_upgradeClassNames.contains(name)) {
				return true;
			}

			Thread thread = Thread.currentThread();

			Class<?> clazz = Class.forName(
				name, true, thread.getContextClassLoader());

			for (Class<?> baseClazz : _baseUpgradeClasses) {
				if (baseClazz.isAssignableFrom(clazz)) {
					return true;
				}
			}
		}
		catch (ClassNotFoundException classNotFoundException) {
			if (_log.isDebugEnabled()) {
				_log.debug(classNotFoundException);
			}
		}

		return false;
	}

	private static final UpgradeLogContext _INSTANCE = new UpgradeLogContext();

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradeLogContext.class);

	private static volatile String _component = "framework";

	private final Class<?>[] _baseUpgradeClasses = new Class<?>[] {
		BaseDB.class, BaseDBProcess.class, UpgradeStep.class
	};
	private final Set<String> _upgradeClassNames = SetUtil.fromArray(
		DBUpgrader.class.getName(), LoggingTimer.class.getName(),
		VerifyProperties.class.getName(),
		"com.liferay.portal.upgrade.internal.executor.UpgradeExecutor",
		"com.liferay.portal.upgrade.internal.release.ReleaseManagerImpl",
		"com.liferay.portal.upgrade.internal.report.UpgradeReport",
		"com.liferay.portal.upgrade.internal.recorder.UpgradeRecorder");

}