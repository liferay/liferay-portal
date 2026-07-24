/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal.verify;

import com.liferay.data.cleanup.internal.verify.util.PostUpgradeDataCleanupProcessUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.DataCleanupLoggingUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Luis Ortiz
 */
public class ResourceActionPostUpgradeDataCleanupProcess
	implements PostUpgradeDataCleanupProcess {

	public ResourceActionPostUpgradeDataCleanupProcess(
		Connection connection,
		ResourceActionLocalService resourceActionLocalService) {

		_connection = connection;
		_resourceActionLocalService = resourceActionLocalService;

		_dbInspector = new DBInspector(connection);
	}

	@Override
	public void cleanUp() throws Exception {
		if (!PostUpgradeDataCleanupProcessUtil.isEveryLiferayBundleResolved()) {
			if (_log.isWarnEnabled() && CompanyThreadLocal.isDefaultCompany()) {
				_log.warn(
					StringBundler.concat(
						ResourceActionPostUpgradeDataCleanupProcess.class.
							getSimpleName(),
						" cannot be executed because there are modules with ",
						"unsatisfied references"));
			}

			return;
		}

		Set<String> modelNames = new HashSet<>(
			ResourceActionsUtil.getModelNames());
		Set<String> portletNames = new HashSet<>(
			ResourceActionsUtil.getPortletNames());

		try (PreparedStatement preparedStatement1 =
				_connection.prepareStatement(
					"select distinct name from ResourceAction");
			PreparedStatement preparedStatement2 = _connection.prepareStatement(
				"select 1 from ResourcePermission where name = ?");
			ResultSet resultSet1 = preparedStatement1.executeQuery()) {

			while (resultSet1.next()) {
				String name = resultSet1.getString("name");

				if (Validator.isNull(name) ||
					(!name.startsWith("com.liferay.") &&
					 !name.startsWith("com_liferay_"))) {

					continue;
				}

				if (modelNames.contains(name) || portletNames.contains(name)) {
					continue;
				}

				preparedStatement2.setString(1, name);

				try (ResultSet resultSet2 = preparedStatement2.executeQuery()) {
					if (resultSet2.next() && _log.isDebugEnabled()) {
						_log.debug(
							StringBundler.concat(
								"Resource action ", name,
								" is not defined in any deployed module but ",
								"is referenced in ",
								_dbInspector.normalizeName(
									"ResourcePermission"),
								" table"));

						continue;
					}
				}

				List<ResourceAction> resourceActions =
					_resourceActionLocalService.getResourceActions(name);

				for (ResourceAction resourceAction : resourceActions) {
					_resourceActionLocalService.deleteResourceAction(
						resourceAction);
				}

				DataCleanupLoggingUtil.logDelete(
					_log, resourceActions.size(),
					_dbInspector.normalizeName("ResourceAction"),
					StringBundler.concat(
						"'", name,
						"' is not defined in any deployed module and is not ",
						"in use"));
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ResourceActionPostUpgradeDataCleanupProcess.class);

	private final Connection _connection;
	private final DBInspector _dbInspector;
	private final ResourceActionLocalService _resourceActionLocalService;

}