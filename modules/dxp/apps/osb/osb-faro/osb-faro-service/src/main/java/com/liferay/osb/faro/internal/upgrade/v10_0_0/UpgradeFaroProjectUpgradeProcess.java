/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.internal.upgrade.v10_0_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Matthew Kong
 */
public class UpgradeFaroProjectUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		_updateIncidentReportEmailAddresses();
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.addColumns(
				"OSBFaro_FaroProject", "incidentReportEmailAddresses STRING")
		};
	}

	private long _getSiteOwnerRoleId() throws Exception {
		String roleName = StringUtil.quote(
			RoleConstants.SITE_OWNER, StringPool.APOSTROPHE);

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select roleId from Role_ where name = ?")) {

			preparedStatement.setString(1, roleName);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getLong(1);
				}

				throw new Exception("Could not find site owner role ID");
			}
		}
	}

	private void _updateIncidentReportEmailAddresses() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select OSBFaro_FaroUser.emailAddress, OSBFaro_",
					"FaroProject.faroProjectId from OSBFaro_FaroProject inner ",
					"join OSBFaro_FaroUser on OSBFaro_FaroProject.groupId = ",
					"OSBFaro_FaroUser.groupId where OSBFaro_FaroUser.roleId = ",
					"?"))) {

			preparedStatement.setLong(1, _getSiteOwnerRoleId());

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					try (PreparedStatement updatePreparedStatement =
							connection.prepareStatement(
								"update OSBFaro_FaroProject set " +
									"incidentReportEmailAddresses = ? where " +
										"faroProjectId = ?")) {

						updatePreparedStatement.setString(
							1, "[\"" + resultSet.getString(1) + "\"]");
						updatePreparedStatement.setLong(
							2, resultSet.getLong(2));

						updatePreparedStatement.executeUpdate();
					}
				}
			}
		}
	}

}