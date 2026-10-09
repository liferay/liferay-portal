/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Adolfo Pérez
 */
public abstract class BaseReplacePortletId extends BasePortletIdUpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		Map<String, List<String>> renamePortletIdsMap = new HashMap<>();

		for (String[] renamePortletIds : getRenamePortletIdsArray()) {
			List<String> sourcePortletIds = renamePortletIdsMap.computeIfAbsent(
				renamePortletIds[1], key -> new ArrayList<>());

			sourcePortletIds.add(renamePortletIds[0]);
		}

		List<String> clauses = new ArrayList<>();

		for (Map.Entry<String, List<String>> entry :
				renamePortletIdsMap.entrySet()) {

			List<String> sourcePortletIds = entry.getValue();

			if (sourcePortletIds.size() <= 1) {
				continue;
			}

			for (int i = 0; i < (sourcePortletIds.size() - 1); i++) {
				String portletId1 = sourcePortletIds.get(i);

				for (int j = i + 1; j < sourcePortletIds.size(); j++) {
					String portletId2 = sourcePortletIds.get(j);

					clauses.add(
						String.format(
							"((PP1.portletId = '%s') AND (PP2.portletId = " +
								"'%s'))",
							portletId1, portletId2));
				}
			}
		}

		if (!clauses.isEmpty()) {
			String orClauses = StringUtil.merge(clauses, " OR ");

			_deleteConflictingPreferences(orClauses);
		}

		super.doUpgrade();
	}

	protected boolean hasPortlet(String portletId) throws SQLException {
		return hasRow(
			"select count(*) from Portlet where portletId = ?", portletId);
	}

	protected boolean hasResourceAction(String name) throws SQLException {
		return hasRow(
			"select count(*) from ResourceAction where name = ?", name);
	}

	protected boolean hasResourcePermission(String name) throws SQLException {
		return hasRow(
			"select count(*) from ResourcePermission where name = ?", name);
	}

	protected boolean hasRow(String sql, String value) throws SQLException {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				sql)) {

			preparedStatement.setString(1, value);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					int count = resultSet.getInt(1);

					if (count > 0) {
						return true;
					}
				}

				return false;
			}
		}
	}

	@Override
	protected void updatePortletId(
			String oldRootPortletId, String newRootPortletId)
		throws Exception {

		if (!hasPortlet(newRootPortletId)) {
			super.updatePortletId(oldRootPortletId, newRootPortletId);
		}
	}

	@Override
	protected void updateResourceAction(String oldName, String newName)
		throws Exception {

		if (hasResourceAction(newName)) {
			try (PreparedStatement preparedStatement1 =
					connection.prepareStatement(
						StringBundler.concat(
							"select RA1.resourceActionId from ResourceAction ",
							"RA1 inner join ResourceAction RA2 on ",
							"RA1.actionId = RA2.actionId where RA1.name = ? ",
							"and RA2.name = ?"));
				PreparedStatement preparedStatement2 =
					AutoBatchPreparedStatementUtil.concurrentAutoBatch(
						connection,
						"delete from ResourceAction where resourceActionId = " +
							"?")) {

				preparedStatement1.setString(1, oldName);
				preparedStatement1.setString(2, newName);

				ResultSet resultSet = preparedStatement1.executeQuery();

				int deleteCount = 0;

				while (resultSet.next()) {
					preparedStatement2.setLong(
						1, resultSet.getLong("resourceActionId"));

					preparedStatement2.addBatch();

					deleteCount++;
				}

				if (deleteCount > 0) {
					preparedStatement2.executeBatch();
				}
			}
		}

		super.updateResourceAction(oldName, newName);
	}

	@Override
	protected void updateResourcePermission(
			String oldRootPortletId, String newRootPortletId,
			boolean updateName)
		throws Exception {

		if (hasResourcePermission(newRootPortletId)) {
			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						"delete from ResourcePermission where name = ?")) {

				preparedStatement.setString(1, oldRootPortletId);

				preparedStatement.execute();
			}
		}
		else {
			super.updateResourcePermission(
				oldRootPortletId, newRootPortletId, updateName);
		}
	}

	private void _deleteConflictingPreferences(String orClauses)
		throws Exception {

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select PP1.portletPreferencesId from PortletPreferences ",
					"PP1 inner join PortletPreferences PP2 on PP1.plid = ",
					"PP2.plid where ", orClauses));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"delete from PortletPreferences where " +
						"portletPreferencesId = ?")) {

			ResultSet resultSet = preparedStatement1.executeQuery();

			int deleteCount = 0;

			while (resultSet.next()) {
				preparedStatement2.setLong(
					1, resultSet.getLong("portletPreferencesId"));

				preparedStatement2.addBatch();

				deleteCount++;
			}

			if (deleteCount > 0) {
				preparedStatement2.executeBatch();
			}
		}
	}

}