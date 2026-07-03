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
 * @author Carlos Correa
 */
public class LayoutSetRemoveUnusedSettingsUpgradeProcess
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select ctCollectionId, layoutSetId, settings_ from LayoutSet");

			ResultSet resultSet = preparedStatement1.executeQuery();

			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update LayoutSet set settings_ = ? where ctCollectionId " +
						"= ? and layoutSetId = ?")) {

			while (resultSet.next()) {
				String settings = resultSet.getString("settings_");

				UnicodeProperties unicodeProperties =
					UnicodePropertiesBuilder.fastLoad(
						settings
					).build();

				boolean modified = false;

				for (String key : _UNUSED_PROPERTY_KEYS) {
					if (unicodeProperties.remove(key) != null) {
						modified = true;
					}
				}

				if (!modified) {
					continue;
				}

				preparedStatement2.setString(1, unicodeProperties.toString());

				preparedStatement2.setLong(
					2, resultSet.getLong("ctCollectionId"));
				preparedStatement2.setLong(3, resultSet.getLong("layoutSetId"));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	private static final String[] _UNUSED_PROPERTY_KEYS = {
		"last-merge-time", "last-merge-version", "last-reset-time",
		"merge-fail-count", "merge-fail-friendly-url-layouts"
	};

}