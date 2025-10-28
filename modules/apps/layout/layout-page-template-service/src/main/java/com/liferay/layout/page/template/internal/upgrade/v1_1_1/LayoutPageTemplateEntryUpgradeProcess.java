/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.upgrade.v1_1_1;

import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Jonathan McCann
 */
public class LayoutPageTemplateEntryUpgradeProcess extends UpgradeProcess {

	public LayoutPageTemplateEntryUpgradeProcess(
		CompanyLocalService companyLocalService) {

		_companyLocalService = companyLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement countPreparedStatement =
				connection.prepareStatement(
					"select count(*) from LayoutPageTemplateEntry where " +
						"groupId = ? and name = ?");
			PreparedStatement deletePreparedStatement =
				connection.prepareStatement(
					"delete from LayoutPageTemplateEntry where groupId <> ? " +
						"and layoutPageTemplateCollectionId <> 0 and type_ = " +
							"? and layoutPrototypeId = ?");
			PreparedStatement selectPreparedStatement =
				connection.prepareStatement(
					SQLTransformer.transform(
						StringBundler.concat(
							"select layoutPageTemplateEntryId, companyId, ",
							"name, layoutPrototypeId from ",
							"LayoutPageTemplateEntry where type_ = ? and ",
							"groupId in (select groupId from Group_ where ",
							"site = [$FALSE$])")));
			PreparedStatement updatePreparedStatement =
				connection.prepareStatement(
					"update LayoutPageTemplateEntry set groupId = ? , " +
						"layoutPageTemplateCollectionId = 0, name = ? where " +
							"layoutPageTemplateEntryId = ?")) {

			selectPreparedStatement.setInt(
				1, LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE);

			try (ResultSet resultSet = selectPreparedStatement.executeQuery()) {
				while (resultSet.next()) {
					long layoutPageTemplateEntryId = resultSet.getLong(
						"layoutPageTemplateEntryId");
					long companyId = resultSet.getLong("companyId");
					String name = resultSet.getString("name");
					long layoutPrototypeId = resultSet.getLong(
						"layoutPrototypeId");

					Company company = _companyLocalService.getCompany(
						companyId);

					String newName = name;

					for (int i = 1;; i++) {
						countPreparedStatement.setLong(1, company.getGroupId());
						countPreparedStatement.setString(2, newName);

						ResultSet countResultSet =
							countPreparedStatement.executeQuery();

						if (countResultSet.next() &&
							(countResultSet.getInt(1) > 0)) {

							newName = name + i;
						}
						else {
							break;
						}
					}

					updatePreparedStatement.setLong(1, company.getGroupId());
					updatePreparedStatement.setString(2, newName);
					updatePreparedStatement.setLong(
						3, layoutPageTemplateEntryId);

					updatePreparedStatement.executeUpdate();

					deletePreparedStatement.setLong(1, company.getGroupId());
					deletePreparedStatement.setInt(
						2, LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE);
					deletePreparedStatement.setLong(3, layoutPrototypeId);

					deletePreparedStatement.executeUpdate();
				}
			}
		}
	}

	private final CompanyLocalService _companyLocalService;

}