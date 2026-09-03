/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal.verify;

import com.liferay.data.cleanup.internal.verify.util.PostUpgradeDataCleanupProcessUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.TableOrphanReferencesDataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.DataCleanupLoggingUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.upgrade.data.cleanup.PortletPreferencesDataCleanupPreupgradeProcess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Luis Ortiz
 */
public class PortletPreferencesPostUpgradeDataCleanupProcess
	implements PostUpgradeDataCleanupProcess {

	public PortletPreferencesPostUpgradeDataCleanupProcess(
		Connection connection, boolean deletePortlets,
		PortletLocalService portletLocalService) {

		_connection = connection;
		_deletePortlets = deletePortlets;
		_portletLocalService = portletLocalService;

		_dbInspector = new DBInspector(connection);
	}

	@Override
	public void cleanUp() throws Exception {
		if (!PostUpgradeDataCleanupProcessUtil.isEveryLiferayBundleActive()) {
			if (_log.isWarnEnabled() && CompanyThreadLocal.isDefaultCompany()) {
				_log.warn(
					StringBundler.concat(
						PortletPreferencesPostUpgradeDataCleanupProcess.class.
							getSimpleName(),
						" cannot be executed because there are modules that ",
						"are inactive"));
			}

			return;
		}

		Set<String> portletIds = new HashSet<>();

		for (Portlet portlet : PortletLocalServiceUtil.getPortlets()) {
			portletIds.add(portlet.getPortletId());
		}

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"select id_, portletId from Portlet");

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String portletId = resultSet.getString("portletId");

				if ((!portletId.startsWith("com.liferay.") &&
					 !portletId.startsWith("com_liferay_")) ||
					portletIds.contains(portletId)) {

					continue;
				}

				if (_deletePortlets) {
					_portletLocalService.deletePortlet(
						resultSet.getLong("id_"));
				}

				DataCleanupLoggingUtil.logDelete(
					_log, 1, !_deletePortlets,
					_dbInspector.normalizeName("Portlet"),
					StringBundler.concat(
						"\"", portletId, "\" is not installed"));
			}
		}

		UpgradeProcess upgradeProcess = new PortletUpgradeProcess(
			portletIds, !_deletePortlets);

		upgradeProcess.upgrade();

		if (_deletePortlets) {
			upgradeProcess =
				new PortletPreferencesDataCleanupPreupgradeProcess();

			upgradeProcess.upgrade();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletPreferencesPostUpgradeDataCleanupProcess.class);

	private final Connection _connection;
	private final DBInspector _dbInspector;
	private final boolean _deletePortlets;
	private final PortletLocalService _portletLocalService;

	private static class PortletUpgradeProcess
		extends DataCleanupPreupgradeProcess {

		public PortletUpgradeProcess(Set<String> portletIds, boolean readOnly) {
			_portletIds = portletIds;
			_readOnly = readOnly;
		}

		@Override
		protected void doUpgrade() throws Exception {
			if (!hasTable(_TEMP_TABLE_NAME)) {
				runSQL(
					"create table " + _TEMP_TABLE_NAME +
						" (portletId VARCHAR(200))");
			}

			try (PreparedStatement preparedStatement =
					AutoBatchPreparedStatementUtil.concurrentAutoBatch(
						connection,
						"insert into " + _TEMP_TABLE_NAME +
							" (portletId) values (?)")) {

				for (String portletId : _portletIds) {
					preparedStatement.setString(1, portletId);
					preparedStatement.addBatch();
				}

				preparedStatement.executeBatch();
			}

			upgrade(
				new TableOrphanReferencesDataCleanupPreupgradeProcess(
					SQLTransformer.transform(
						StringBundler.concat(
							"CASE WHEN INSTR([$SOURCE_TABLE_ALIAS$].",
							"portletId, '_INSTANCE_') > 0 THEN SUBSTR(",
							"[$SOURCE_TABLE_ALIAS$].portletId, 1, INSTR(",
							"[$SOURCE_TABLE_ALIAS$].portletId, '_INSTANCE_') ",
							"- 1) ELSE [$SOURCE_TABLE_ALIAS$].portletId END ",
							"or [$TARGET_TABLE_ALIAS$].portletId = CASE WHEN ",
							"INSTR([$SOURCE_TABLE_ALIAS$].portletId, '_USER_'",
							") > 0 THEN SUBSTR([$SOURCE_TABLE_ALIAS$].",
							"portletId, 1, INSTR([$SOURCE_TABLE_ALIAS$].",
							"portletId, '_USER_') - 1) ELSE ",
							"[$SOURCE_TABLE_ALIAS$].portletId END")),
					_readOnly,
					"[$SOURCE_TABLE_ALIAS$].ownerType = " +
						PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
					"portletId", "PortletPreferences", "portletId",
					_TEMP_TABLE_NAME));

			runSQL("DROP_TABLE_IF_EXISTS(" + _TEMP_TABLE_NAME + ")");
		}

		private static final String _TEMP_TABLE_NAME = "TEMP_TABLE_PORTLET";

		private final Set<String> _portletIds;
		private final boolean _readOnly;

	}

}