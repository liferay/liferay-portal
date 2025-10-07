/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup;

import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.DataCleanupLoggingUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author István András Dézsi
 */
public class QuartzJobDetailsDataCleanupPreupgradeProcess
	extends DataCleanupPreupgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select JOB_NAME from QUARTZ_JOB_DETAILS where JOB_DATA is " +
					"null");
			PreparedStatement preparedStatement2 = connection.prepareStatement(
				"delete from QUARTZ_JOB_DETAILS where JOB_DATA is null");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			preparedStatement2.execute();

			if (!_log.isInfoEnabled()) {
				return;
			}

			DBInspector dbInspector = new DBInspector(connection);

			while (resultSet.next()) {
				DataCleanupLoggingUtil.logDelete(
					_log, 1, dbInspector.normalizeName("QUARTZ_JOB_DETAILS"),
					dbInspector.normalizeName("JOB_DATA") +
						" was null for job " + resultSet.getString("JOB_NAME"));
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		QuartzJobDetailsDataCleanupPreupgradeProcess.class);

}