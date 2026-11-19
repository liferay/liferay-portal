/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.internal.upgrade.v25_2_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Eudaldo Alonso
 */
public class FaroUserUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select OSBFaro_FaroUser.faroUserId, ",
					"OSBFaro_FaroUser.groupId, OSBFaro_FaroUser.liveUserId, ",
					"OSBFaro_FaroUser.emailAddress from OSBFaro_FaroUser left ",
					"join User_ on User_.userId = OSBFaro_FaroUser.liveUserId ",
					"where OSBFaro_FaroUser.liveUserId > 0 and User_.userId ",
					"is null"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"delete from OSBFaro_FaroPreferences where groupId = ? " +
						"and ownerId = ?");
			PreparedStatement preparedStatement3 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"delete from OSBFaro_FaroUser where faroUserId = ?");

			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				long groupId = resultSet.getLong("groupId");
				long liveUserId = resultSet.getLong("liveUserId");

				preparedStatement2.setLong(1, groupId);
				preparedStatement2.setLong(2, liveUserId);

				preparedStatement2.addBatch();

				long faroUserId = resultSet.getLong("faroUserId");

				preparedStatement3.setLong(1, faroUserId);

				preparedStatement3.addBatch();

				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Deleted faro user ", faroUserId, " with email ",
							"address ", resultSet.getString("emailAddress"),
							" from group ", groupId, " because user ",
							liveUserId, " does not exist"));
				}
			}

			preparedStatement2.executeBatch();

			preparedStatement3.executeBatch();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FaroUserUpgradeProcess.class);

}