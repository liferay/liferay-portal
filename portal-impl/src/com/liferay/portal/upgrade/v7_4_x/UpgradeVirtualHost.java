/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Christopher Kian
 */
public class UpgradeVirtualHost extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select ctCollectionId, virtualHostId, hostname from " +
					"VirtualHost where hostname != LOWER(hostname)");

			PreparedStatement preparedStatement2 = connection.prepareStatement(
				"update VirtualHost set hostname = ? where ctCollectionId = " +
					"? and virtualHostId = ?")) {

			ResultSet resultSet = preparedStatement1.executeQuery();

			while (resultSet.next()) {
				String hostname = resultSet.getString("hostname");

				preparedStatement2.setString(
					1, StringUtil.toLowerCase(hostname));

				long ctCollectionId = resultSet.getLong("ctCollectionId");

				preparedStatement2.setLong(2, ctCollectionId);

				long virtualHostId = resultSet.getLong("virtualHostId");

				preparedStatement2.setLong(3, virtualHostId);

				try {
					preparedStatement2.addBatch();
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Deleting duplicate virtual host ",
								virtualHostId, " with hostname ", hostname),
							exception);
					}

					runSQL(
						"delete from VirtualHost where virtualHostId = " +
							virtualHostId);
				}
			}

			preparedStatement2.executeBatch();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradeVirtualHost.class);

}