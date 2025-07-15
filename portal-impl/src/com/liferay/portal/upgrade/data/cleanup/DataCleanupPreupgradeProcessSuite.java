/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup;

import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeProcess;
import com.liferay.portal.upgrade.PortalUpgradeProcess;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Luis Ortiz
 */
public class DataCleanupPreupgradeProcessSuite {

	public void cleanUp() throws Exception {
		try (Connection connection = DataAccess.getConnection()) {
			if (StartupHelperUtil.isDBNew() ||
				PortalUpgradeProcess.isInLatestSchemaVersion(connection) ||
				(PortalUpgradeProcess.getCurrentState(connection) !=
					ReleaseConstants.STATE_GOOD)) {

				return;
			}
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				"Starting " +
					DataCleanupPreupgradeProcessSuite.class.getName());
		}

		for (DataCleanupPreupgradeProcess dataCleanupPreupgradeProcess :
				_dataCleanupPreupgradeProcesses) {

			dataCleanupPreupgradeProcess.upgrade();
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				"Finished " +
					DataCleanupPreupgradeProcessSuite.class.getName());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DataCleanupPreupgradeProcessSuite.class);

	private final List<DataCleanupPreupgradeProcess>
		_dataCleanupPreupgradeProcesses = new ArrayList<>(
			Arrays.asList(
				new CompanyDataCleanupPreupgradeProcess(),
				new GroupDataCleanupPreupgradeProcess()));

}