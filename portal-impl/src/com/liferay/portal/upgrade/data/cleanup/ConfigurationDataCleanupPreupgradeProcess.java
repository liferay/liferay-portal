/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.DataCleanupLoggingUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author István András Dézsi
 */
public class ConfigurationDataCleanupPreupgradeProcess
	extends DataCleanupPreupgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		DBInspector dbInspector = new DBInspector(connection);

		if (!dbInspector.hasTable(
				dbInspector.normalizeName("Configuration_"))) {

			if (_log.isDebugEnabled()) {
				_log.debug("The Configuration_ table does not exist");
			}

			return;
		}

		long[] companyIds = PortalInstancePool.getCompanyIds();
		long[] groupIds = getGroupIds();

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select configurationId, dictionary from Configuration_");
			PreparedStatement preparedStatement2 = connection.prepareStatement(
				"delete from Configuration_ where configurationId = ?");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				String dictionary = resultSet.getString("dictionary");

				long companyId = _getPrimaryKey(dictionary, _companyIdPattern);

				String configurationId = resultSet.getString("configurationId");

				if (companyId > 0) {
					if (!ArrayUtil.contains(companyIds, companyId)) {
						_deleteConfiguration(
							configurationId, dbInspector, "companyId",
							"Company", companyId, preparedStatement2);
					}

					continue;
				}

				long groupId = _getPrimaryKey(dictionary, _groupIdPattern);

				if ((groupId != -1) && !ArrayUtil.contains(groupIds, groupId)) {
					_deleteConfiguration(
						configurationId, dbInspector, "groupId", "Group_",
						groupId, preparedStatement2);
				}
			}

			preparedStatement2.executeBatch();
		}
	}

	protected long[] getGroupIds() throws Exception {
		List<Long> groupIds = new ArrayList<>();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select groupId from Group_");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				groupIds.add(resultSet.getLong("groupId"));
			}
		}

		return ArrayUtil.toArray(groupIds.toArray(new Long[0]));
	}

	private void _deleteConfiguration(
			String configurationId, DBInspector dbInspector,
			String primaryKeyColumnName, String tableName, long primaryKey,
			PreparedStatement preparedStatement)
		throws Exception {

		preparedStatement.setString(1, configurationId);
		preparedStatement.addBatch();

		DataCleanupLoggingUtil.logDelete(
			_log, 1, "Configuration_",
			StringBundler.concat(
				configurationId, " has scope ", primaryKeyColumnName,
				StringPool.SPACE, primaryKey, " that was not found in ",
				dbInspector.normalizeName(tableName), ".",
				dbInspector.normalizeName(primaryKeyColumnName)));
	}

	private long _getPrimaryKey(String dictionary, Pattern pattern) {
		Matcher matcher = pattern.matcher(dictionary);

		if (matcher.find()) {
			return GetterUtil.getLong(matcher.group(1));
		}

		return -1;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurationDataCleanupPreupgradeProcess.class);

	private static final Pattern _companyIdPattern = Pattern.compile(
		"\\bcompanyId\\s*=\\s*(?:[A-Z]?\"?(\\d+)\"?)");
	private static final Pattern _groupIdPattern = Pattern.compile(
		"\\bgroupId\\s*=\\s*(?:[A-Z]?\"?(\\d+)\"?)");

}