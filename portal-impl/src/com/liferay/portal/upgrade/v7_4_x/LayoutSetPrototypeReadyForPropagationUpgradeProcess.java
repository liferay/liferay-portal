/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Alejandro Tardín
 */
public class LayoutSetPrototypeReadyForPropagationUpgradeProcess
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select ctCollectionId, layoutSetPrototypeId, settings_ from " +
					"LayoutSetPrototype");
			ResultSet resultSet = preparedStatement1.executeQuery();
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update LayoutSetPrototype set settings_ = ? where " +
						"ctCollectionId = ? and layoutSetPrototypeId = ?")) {

			while (resultSet.next()) {
				String settings = resultSet.getString("settings_");

				UnicodeProperties unicodeProperties =
					UnicodePropertiesBuilder.fastLoad(
						settings
					).build();

				if (!unicodeProperties.containsKey("readyForPropagation")) {
					continue;
				}

				unicodeProperties.setProperty("readyForPropagation", "false");

				preparedStatement2.setString(1, unicodeProperties.toString());

				preparedStatement2.setLong(
					2, resultSet.getLong("ctCollectionId"));

				preparedStatement2.setLong(
					3, resultSet.getLong("layoutSetPrototypeId"));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

}