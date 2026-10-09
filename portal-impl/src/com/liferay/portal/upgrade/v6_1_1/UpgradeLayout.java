/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v6_1_1;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LoggingTimer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Jorge Ferrer
 */
public class UpgradeLayout extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updateSourcePrototypeLayoutUuid();
	}

	protected long getLayoutPrototypeGroupId(String layoutPrototypeUuid)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select groupId from Group_ where classPK = (select " +
					"layoutPrototypeId from LayoutPrototype where uuid_ = " +
						"?)")) {

			preparedStatement.setString(1, layoutPrototypeUuid);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					return resultSet.getLong("groupId");
				}
			}
		}

		return 0;
	}

	protected boolean isGroupPrivateLayout(
			long groupId, String sourcePrototypeLayoutUuid)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select count(*) as count from Layout where uuid_ = ? and " +
					"groupId = ? and privateLayout = ?")) {

			preparedStatement.setString(1, sourcePrototypeLayoutUuid);
			preparedStatement.setLong(2, groupId);
			preparedStatement.setBoolean(3, true);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					if (resultSet.getLong("count") > 0) {
						return true;
					}

					return false;
				}
			}
		}

		return false;
	}

	protected void updateSourcePrototypeLayoutUuid() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			StringBundler sb = new StringBundler(4);

			sb.append("select plid, layoutPrototypeUuid, ");
			sb.append("sourcePrototypeLayoutUuid from Layout where ");
			sb.append("layoutPrototypeUuid != '' and ");
			sb.append("sourcePrototypeLayoutUuid != ''");

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(sb.toString());

				ResultSet resultSet = preparedStatement.executeQuery()) {

				// Get pages with a sourcePrototypeLayoutUuid that have a page
				// template. If the layoutUuid points to a page template, remove
				// it. Otherwise, it points to a site template page, so leave
				// it.

				while (resultSet.next()) {
					String layoutPrototypeUuid = resultSet.getString(
						"layoutPrototypeUuid");

					long groupId = getLayoutPrototypeGroupId(
						layoutPrototypeUuid);

					if (groupId == 0) {
						continue;
					}

					String sourcePrototypeLayoutUuid = resultSet.getString(
						"sourcePrototypeLayoutUuid");

					if (!isGroupPrivateLayout(
							groupId, sourcePrototypeLayoutUuid)) {

						continue;
					}

					long plid = resultSet.getLong("plid");

					runSQL(
						"update Layout set sourcePrototypeLayoutUuid = null " +
							"where plid = " + plid);
				}
			}
		}
	}

}