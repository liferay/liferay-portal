/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.upgrade.v2_15_0;

import com.liferay.change.tracking.constants.CTDestinationNames;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

/**
 * @author Kiana Suetani
 */
public class CTCollectionUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		if (!hasTable("QUARTZ_TRIGGERS")) {
			return;
		}

		try (PreparedStatement selectPreparedStatement =
				connection.prepareStatement(
					"select TRIGGER_NAME, START_TIME from QUARTZ_TRIGGERS " +
						"where TRIGGER_GROUP = ?")) {

			selectPreparedStatement.setString(
				1, CTDestinationNames.CT_COLLECTION_SCHEDULED_PUBLISH);

			try (ResultSet resultSet = selectPreparedStatement.executeQuery();
				PreparedStatement updatePreparedStatement =
					AutoBatchPreparedStatementUtil.autoBatch(
						connection,
						"update CTCollection set scheduledDate = ? where " +
							"ctCollectionId = ? and status = ?")) {

				while (resultSet.next()) {
					String triggerName = resultSet.getString("TRIGGER_NAME");

					int index = triggerName.indexOf(StringPool.AT);

					if (index < 0) {
						continue;
					}

					long startTime = resultSet.getLong("START_TIME");

					if (startTime <= 0) {
						continue;
					}

					long ctCollectionId = GetterUtil.getLong(
						triggerName.substring(0, index));

					updatePreparedStatement.setTimestamp(
						1, new Timestamp(startTime));
					updatePreparedStatement.setLong(2, ctCollectionId);
					updatePreparedStatement.setInt(
						3, WorkflowConstants.STATUS_SCHEDULED);

					updatePreparedStatement.addBatch();
				}

				updatePreparedStatement.executeBatch();
			}
		}
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.addColumns(
				"CTCollection", "scheduledDate DATE null")
		};
	}

}