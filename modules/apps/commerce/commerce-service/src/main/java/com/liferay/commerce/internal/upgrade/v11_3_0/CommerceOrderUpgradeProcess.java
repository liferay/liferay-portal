/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.upgrade.v11_3_0;

import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Crescenzo Rega
 */
public class CommerceOrderUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select commerceOrderId from CommerceOrder where orderStatus " +
					"= ?")) {

			preparedStatement1.setInt(
				1, CommerceOrderConstants.ORDER_STATUS_OPEN);

			try (ResultSet resultSet1 = preparedStatement1.executeQuery()) {
				while (resultSet1.next()) {
					long commerceOrderId = resultSet1.getLong(1);

					runSQL(
						StringBundler.concat(
							"update CommerceOrder set shippable = ",
							_getShippable(connection, commerceOrderId),
							" where commerceOrderId = ", commerceOrderId));
				}
			}
		}
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.addColumns(
				"CommerceOrder", "shippable BOOLEAN")
		};
	}

	private String _getShippable(Connection connection, long commerceOrderId)
		throws Exception {

		PreparedStatement preparedStatement3 = connection.prepareStatement(
			"select distinct shippable from CommerceOrderItem where " +
				"commerceOrderId = ?");

		preparedStatement3.setLong(1, commerceOrderId);

		try (ResultSet resultSet3 = preparedStatement3.executeQuery()) {
			while (resultSet3.next()) {
				if (resultSet3.getBoolean("shippable")) {
					return "[$TRUE$]";
				}
			}
		}

		return "[$FALSE$]";
	}

}