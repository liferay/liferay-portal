/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.upgrade.v4_0_1;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Yuri Monteiro
 */
public class NotificationTemplateUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select companyId, notificationTemplateId from " +
					"NotificationTemplate where externalReferenceCode = " +
						"'L_COMMERCE_ORDER_TEMPLATE'");
			PreparedStatement preparedStatement2 = connection.prepareStatement(
				"select notificationTemplateId from NotificationTemplate " +
					"where companyId = ? and externalReferenceCode = " +
						"'COMMERCE_ORDER_TEMPLATE'");
			PreparedStatement preparedStatement3 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update NotificationTemplate set externalReferenceCode = " +
						"'COMMERCE_ORDER_TEMPLATE' where " +
							"notificationTemplateId = ?");

			ResultSet resultSet1 = preparedStatement1.executeQuery()) {

			while (resultSet1.next()) {
				preparedStatement2.setLong(1, resultSet1.getLong("companyId"));

				try (ResultSet resultSet2 = preparedStatement2.executeQuery()) {
					if (resultSet2.next()) {
						if (_log.isInfoEnabled()) {
							_log.info(
								StringBundler.concat(
									"Unable to modify ",
									"\"L_COMMERCE_ORDER_TEMPLATE\" to ",
									"\"COMMERCE_ORDER_TEMPLATE\" because it ",
									"is already used by notification template ",
									resultSet2.getLong(
										"notificationTemplateId")));
						}

						continue;
					}
				}

				preparedStatement3.setLong(
					1, resultSet1.getLong("notificationTemplateId"));

				preparedStatement3.addBatch();
			}

			preparedStatement3.executeBatch();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		NotificationTemplateUpgradeProcess.class);

}