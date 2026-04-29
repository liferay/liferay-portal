/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.upgrade.v2_14_0;

import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Objects;

/**
 * @author Pei-Jung Lan
 */
public class CTConflictCheckerDispatchTriggerUpgradeProcess
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select dispatchTriggerId, dispatchTaskSettings from " +
					"DispatchTrigger where dispatchTaskSettings like " +
						"'%featureFlagKey=LPD-11018%'");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update DispatchTrigger set dispatchTaskSettings = ? " +
						"where dispatchTriggerId = ?");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				UnicodeProperties unicodeProperties =
					UnicodePropertiesBuilder.create(
						true
					).fastLoad(
						resultSet.getString("dispatchTaskSettings")
					).build();

				if (!Objects.equals(
						unicodeProperties.getProperty("featureFlagKey"),
						"LPD-11018")) {

					continue;
				}

				unicodeProperties.remove("featureFlagKey");

				preparedStatement2.setString(1, unicodeProperties.toString());
				preparedStatement2.setLong(
					2, resultSet.getLong("dispatchTriggerId"));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

}