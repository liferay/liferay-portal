/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.internal.upgrade.v3_1_1;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.workflow.kaleo.definition.NotificationType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Objects;

/**
 * @author Rafael Praxedes
 */
public class KaleoNotificationUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select kaleoNotificationId, notificationTypes  from " +
					"KaleoNotification where notificationTypes like ? OR " +
						"notificationTypes like ?");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update KaleoNotification set notificationTypes = ? " +
						"where kaleoNotificationId = ?")) {

			preparedStatement1.setString(1, "%im%");
			preparedStatement1.setString(2, "%private-message%");

			ResultSet resultSet = preparedStatement1.executeQuery();

			while (resultSet.next()) {
				String[] notificationTypes = TransformUtil.transform(
					StringUtil.split(resultSet.getString("notificationTypes")),
					notificationType -> {
						if (Objects.equals(notificationType, "im") ||
							Objects.equals(
								notificationType, "private-message")) {

							return null;
						}

						return notificationType;
					},
					String.class);

				if (ArrayUtil.isEmpty(notificationTypes)) {
					notificationTypes = new String[] {
						NotificationType.USER_NOTIFICATION.getValue()
					};
				}

				preparedStatement2.setString(
					1, StringUtil.merge(notificationTypes));
				preparedStatement2.setLong(
					2, resultSet.getLong("kaleoNotificationId"));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
		catch (SQLException sqlException) {
			throw new UpgradeException(sqlException);
		}
	}

}