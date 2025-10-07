/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v9_2_1;

import com.liferay.notification.constants.NotificationConstants;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Objects;

/**
 * @author Feliphe Marinho
 */
public class ObjectActionUpgradeProcess extends UpgradeProcess {

	public ObjectActionUpgradeProcess(
		NotificationTemplateLocalService notificationTemplateLocalService) {

		_notificationTemplateLocalService = notificationTemplateLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				SQLTransformer.transform(
					StringBundler.concat(
						"select objectActionId, parameters from ObjectAction ",
						"where objectActionExecutorKey = '",
						ObjectActionExecutorConstants.KEY_NOTIFICATION,
						"' and objectActionTriggerKey in ('",
						ObjectActionTriggerConstants.KEY_ON_AFTER_ADD, "', '",
						ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE,
						"')")));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update ObjectAction set parameters = ? where " +
						"objectActionId = ?");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				UnicodeProperties unicodeProperties =
					UnicodePropertiesBuilder.create(
						true
					).load(
						resultSet.getString("parameters")
					).put(
						"usePreferredLanguageForGuests", "false"
					).build();

				NotificationTemplate notificationTemplate =
					_notificationTemplateLocalService.fetchNotificationTemplate(
						GetterUtil.getLong(
							unicodeProperties.get("notificationTemplateId")));

				if ((notificationTemplate == null) ||
					!Objects.equals(
						notificationTemplate.getType(),
						NotificationConstants.TYPE_EMAIL)) {

					continue;
				}

				preparedStatement2.setString(1, unicodeProperties.toString());
				preparedStatement2.setLong(
					2, resultSet.getLong("objectActionId"));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	private final NotificationTemplateLocalService
		_notificationTemplateLocalService;

}