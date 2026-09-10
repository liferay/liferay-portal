/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v13_8_0;

import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Brian I. Kim
 */
public class LayoutPageTemplateEntryClassNameIdUpgradeProcess
	extends UpgradeProcess {

	public LayoutPageTemplateEntryClassNameIdUpgradeProcess(
		CompanyLocalService companyLocalService) {

		_companyLocalService = companyLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompany(
			company -> _updateClassNameIds(company.getCompanyId()));
	}

	private void _updateClassNameIds(long companyId) throws Exception {
		try (PreparedStatement selectPreparedStatement =
				connection.prepareStatement(
					SQLTransformer.transform(
						StringBundler.concat(
							"select ObjectDefinitionSetting.value as ",
							"oldClassNameId, ClassName_.classNameId from ",
							"ObjectDefinitionSetting inner join ",
							"ObjectDefinition on ",
							"ObjectDefinition.objectDefinitionId = ",
							"ObjectDefinitionSetting.objectDefinitionId inner ",
							"join ClassName_ on ClassName_.value = ",
							"ObjectDefinition.className where ",
							"ObjectDefinitionSetting.name = ? and ",
							"ObjectDefinition.companyId = ?")));
			PreparedStatement updatePreparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update LayoutPageTemplateEntry set classNameId = ? " +
						"where classNameId = ? and companyId = ?")) {

			selectPreparedStatement.setString(
				1, ObjectDefinitionSettingConstants.NAME_OLD_CLASS_NAME_ID);
			selectPreparedStatement.setLong(2, companyId);

			try (ResultSet resultSet = selectPreparedStatement.executeQuery()) {
				while (resultSet.next()) {
					long oldClassNameId = GetterUtil.getLong(
						resultSet.getString("oldClassNameId"));
					long newClassNameId = resultSet.getLong("classNameId");

					if ((oldClassNameId == 0) ||
						(oldClassNameId == newClassNameId)) {

						continue;
					}

					updatePreparedStatement.setLong(1, newClassNameId);
					updatePreparedStatement.setLong(2, oldClassNameId);
					updatePreparedStatement.setLong(3, companyId);

					updatePreparedStatement.addBatch();
				}
			}

			updatePreparedStatement.executeBatch();
		}
	}

	private final CompanyLocalService _companyLocalService;

}