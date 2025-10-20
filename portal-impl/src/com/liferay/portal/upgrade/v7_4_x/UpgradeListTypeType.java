/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PropsValues;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Luis Ortiz
 */
public class UpgradeListTypeType extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		if (PropsValues.DATABASE_PARTITION_ENABLED) {
			_updateListType(CompanyThreadLocal.getCompanyId(), "intranet");
			_updateListType(CompanyThreadLocal.getCompanyId(), "public");

			return;
		}

		long[] companyIds = PortalInstancePool.getCompanyIds();

		for (long companyId : companyIds) {
			_updateListType(companyId, "intranet");
			_updateListType(companyId, "public");
		}
	}

	private void _updateListType(long companyId, String name) throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"Select 1 from ListType where companyId = ? and name = ? and " +
					"type_ = ?")) {

			preparedStatement1.setLong(1, companyId);
			preparedStatement1.setString(2, name);
			preparedStatement1.setString(
				3, "com.liferay.portal.kernel.model.Company.website");

			ResultSet resultSet = preparedStatement1.executeQuery();

			if (resultSet.next()) {
				try (PreparedStatement preparedStatement2 =
						connection.prepareStatement(
							"DELETE FROM ListType where companyId = ? and  " +
								"name = ? and type_ = ?")) {

					preparedStatement2.setLong(1, companyId);
					preparedStatement2.setString(2, name);
					preparedStatement2.setString(
						3, "com.liferay.account.model.AccountEntry.address");

					preparedStatement2.executeUpdate();
				}

				return;
			}

			try (PreparedStatement preparedStatement3 =
					connection.prepareStatement(
						"update ListType set type_ = ? where companyId = ?  " +
							"and name = ? and type_ = ?")) {

				preparedStatement3.setString(
					1, "com.liferay.portal.kernel.model.Company.website");
				preparedStatement3.setLong(2, companyId);
				preparedStatement3.setString(3, name);
				preparedStatement3.setString(
					4, "com.liferay.account.model.AccountEntry.address");

				preparedStatement3.executeUpdate();
			}
		}
	}

}