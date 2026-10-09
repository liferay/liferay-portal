/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.upgrade.v1_11_2;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author Ethan Bustad
 */
public class CPDefinitionLinkUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select CProductId from CProduct where CProductId = ?");

			Statement s = connection.createStatement(
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

			ResultSet resultSet1 = s.executeQuery(
				"select distinct CProductId from CPDefinitionLink")) {

			while (resultSet1.next()) {
				long cProductId = resultSet1.getLong("CProductId");

				preparedStatement.setLong(1, cProductId);

				ResultSet resultSet2 = preparedStatement.executeQuery();

				if (resultSet2.next()) {
					continue;
				}

				if (_log.isDebugEnabled()) {
					_log.debug(
						"Removing commerce product definition link rows with " +
							"commerce product ID " + cProductId);
				}

				runSQL(
					"delete from CPDefinitionLink where CProductId = " +
						cProductId);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPDefinitionLinkUpgradeProcess.class);

}