/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_0_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LoggingTimer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sampsa Sohlman
 */
public class UpgradeResourcePermission extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		upgradeResourcePermissions();
	}

	protected void upgradeResourcePermissions() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			runSQL(
				"update ResourcePermission set viewActionId = [$FALSE$] " +
					"where MOD(actionIds, 2) = 0");
			runSQL(
				"update ResourcePermission set viewActionId = [$TRUE$] where " +
					"MOD(actionIds, 2) = 1");

			try (PreparedStatement preparedStatement1 =
					connection.prepareStatement(
						"select distinct name from ResourcePermission");

				ResultSet resultSet1 = preparedStatement1.executeQuery();

				PreparedStatement preparedStatement2 =
					connection.prepareStatement(
						"select distinct primKey from ResourcePermission " +
							"where name = ?")) {

				while (resultSet1.next()) {
					List<String> primKeys = new ArrayList<>();

					String name = resultSet1.getString("name");

					preparedStatement2.setString(1, name);

					try (ResultSet resultSet2 =
							preparedStatement2.executeQuery()) {

						while (resultSet2.next()) {
							String primKey = resultSet2.getString("primKey");

							if ((GetterUtil.getLong(primKey) > 0) ||
								primKey.contains(
									PortletConstants.LAYOUT_SEPARATOR)) {

								continue;
							}

							primKeys.add(primKey);
						}
					}

					String[][] primKeysArray = null;

					if (!primKeys.isEmpty()) {
						primKeysArray = (String[][])ArrayUtil.split(
							primKeys.toArray(new String[0]),
							_getPrimKeysSplitSize(primKeys.size()));
					}

					_updatePrimKeyIdsByName(name, primKeysArray);
				}
			}
		}
	}

	private String _createInClause(String[] primKeys) {
		StringBundler sb = new StringBundler(primKeys.length + 1);

		sb.append("in (?");

		for (int i = 1; i < primKeys.length; i++) {
			sb.append(", ?");
		}

		sb.append(")");

		return sb.toString();
	}

	private int _getPrimKeysSplitSize(int primKeysCount) {
		if (DBManagerUtil.getDBType() == DBType.ORACLE) {
			return 1000;
		}

		return primKeysCount;
	}

	private void _updatePrimKeyIds(String sql, String name, String[] primKeys)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				SQLTransformer.transform(sql))) {

			preparedStatement.setString(1, name);

			for (int i = 0; i < primKeys.length; i++) {
				String primKey = primKeys[i];

				preparedStatement.setString(i + 2, primKey);
			}

			preparedStatement.executeUpdate();
		}
	}

	private void _updatePrimKeyIdsByName(String name, String[][] primKeysArray)
		throws Exception {

		if (ArrayUtil.isEmpty(primKeysArray)) {
			_updatePrimKeyIds(
				"update ResourcePermission set primKeyId = CAST_LONG(" +
					"primKey) where name = ? and primKey not like '%_LAYOUT_%'",
				name, new String[0]);

			_updatePrimKeyIds(
				"update ResourcePermission set primKeyId = 0 where name = ? " +
					"and primKey like '%_LAYOUT_%'",
				name, new String[0]);

			return;
		}

		for (String[] primKeys : primKeysArray) {
			String inClause = _createInClause(primKeys);

			_updatePrimKeyIds(
				StringBundler.concat(
					"update ResourcePermission set primKeyId = 0 where name = ",
					"? and (primKey like '%_LAYOUT_%' or primKey ", inClause,
					")"),
				name, primKeys);
		}

		runSQL(
			StringBundler.concat(
				"update ResourcePermission set primKeyId = CAST_LONG(primKey",
				") where name = '", name,
				"' and (primKey not like '%_LAYOUT_%' and (primKeyId IS NULL ",
				"or primKeyId != 0))"));
	}

}