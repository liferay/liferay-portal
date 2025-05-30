/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade.util;

import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.version.Version;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 * @author Raymond Augé
 */
public class UpgradeProcessUtil {

	public static String getDefaultLanguageId(long companyId)
		throws SQLException {

		String languageId = _languageIds.get(companyId);

		if (languageId != null) {
			return languageId;
		}

		try (Connection connection = DataAccess.getConnection()) {
			DBInspector dbInspector = new DBInspector(connection);
			String sql = "select languageId from User_ where companyId = ?";

			PreparedStatement preparedStatement = null;

			if (dbInspector.hasColumn("User_", "defaultUser")) {
				preparedStatement = connection.prepareStatement(
					sql + " and defaultUser = ?");

				preparedStatement.setLong(1, companyId);
				preparedStatement.setBoolean(2, Boolean.TRUE);
			}
			else {
				preparedStatement = connection.prepareStatement(
					sql + " and type_ = " + UserConstants.TYPE_GUEST);

				preparedStatement.setLong(1, companyId);
			}

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					languageId = resultSet.getString("languageId");

					_languageIds.put(companyId, languageId);

					return languageId;
				}

				return LocaleUtil.toLanguageId(LocaleUtil.US);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return LocaleUtil.toLanguageId(LocaleUtil.US);
		}
	}

	public static List<UpgradeProcess> initUpgradeProcesses(
		ClassLoader classLoader, String[] upgradeProcessClassNames) {

		List<UpgradeProcess> upgradeProcesses = new ArrayList<>();

		for (String upgradeProcessClassName : upgradeProcessClassNames) {
			if (_log.isDebugEnabled()) {
				_log.debug("Initializing upgrade " + upgradeProcessClassName);
			}

			UpgradeProcess upgradeProcess = null;

			try {
				Class<?> clazz = classLoader.loadClass(upgradeProcessClassName);

				upgradeProcess = (UpgradeProcess)clazz.newInstance();
			}
			catch (Exception exception) {
				_log.error(
					"Unable to initialize upgrade " + upgradeProcessClassName,
					exception);

				continue;
			}

			upgradeProcesses.add(upgradeProcess);
		}

		return upgradeProcesses;
	}

	public static boolean isCreateIGImageDocumentType() {
		return _createIGImageDocumentType;
	}

	public static boolean isRequiredSchemaVersion(
		Version currentSchemaVersion, Version newSchemaVersion) {

		int result = newSchemaVersion.compareTo(currentSchemaVersion);

		if ((result > 0) &&
			((newSchemaVersion.getMajor() > currentSchemaVersion.getMajor()) ||
			 (newSchemaVersion.getMinor() > currentSchemaVersion.getMinor()))) {

			return true;
		}

		return false;
	}

	public static void setCreateIGImageDocumentType(
		boolean createIGImageDocumentType) {

		_createIGImageDocumentType = createIGImageDocumentType;
	}

	public static boolean upgradeProcess(
			int buildNumber, List<UpgradeProcess> upgradeProcesses)
		throws UpgradeException {

		boolean ranUpgradeProcess = false;

		for (UpgradeProcess upgradeProcess : upgradeProcesses) {
			boolean tempRanUpgradeProcess = _upgradeProcess(
				buildNumber, upgradeProcess);

			if (tempRanUpgradeProcess) {
				ranUpgradeProcess = true;
			}
		}

		return ranUpgradeProcess;
	}

	private static boolean _upgradeProcess(
			int buildNumber, UpgradeProcess upgradeProcess)
		throws UpgradeException {

		Class<?> clazz = upgradeProcess.getClass();

		if ((upgradeProcess.getThreshold() == 0) ||
			(upgradeProcess.getThreshold() > buildNumber)) {

			if (_log.isDebugEnabled()) {
				_log.debug("Running upgrade " + clazz.getName());
			}

			upgradeProcess.upgrade();

			if (_log.isDebugEnabled()) {
				_log.debug("Finished upgrade " + clazz.getName());
			}

			return true;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Upgrade threshold " + upgradeProcess.getThreshold() +
					" will not trigger upgrade");

			_log.debug("Skipping upgrade " + clazz.getName());
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradeProcessUtil.class);

	private static boolean _createIGImageDocumentType;
	private static final Map<Long, String> _languageIds = new HashMap<>();

}