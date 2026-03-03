/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.upgrade.v1_2_0;

import com.liferay.notification.constants.NotificationQueueEntryConstants;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Mateus Santana
 */
public class NotificationQueueEntryUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select notificationQueueEntryId, sent from " +
					"NotificationQueueEntry");
			ResultSet resultSet = preparedStatement1.executeQuery();
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					SQLTransformer.transform(
						"update NotificationQueueEntry set status = ? where " +
							"notificationQueueEntryId = ?"))) {

			while (resultSet.next()) {
				if (resultSet.getBoolean("sent")) {
					preparedStatement2.setInt(
						1, NotificationQueueEntryConstants.STATUS_SENT);
				}
				else {
					preparedStatement2.setInt(
						1, NotificationQueueEntryConstants.STATUS_UNSENT);
				}

				preparedStatement2.setLong(
					2, resultSet.getLong("notificationQueueEntryId"));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.addColumns(
				"NotificationQueueEntry", "status INTEGER")
		};
	}

}