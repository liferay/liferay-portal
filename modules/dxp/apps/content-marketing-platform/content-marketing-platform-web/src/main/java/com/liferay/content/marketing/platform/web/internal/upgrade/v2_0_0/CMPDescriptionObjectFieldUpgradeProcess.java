/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.web.internal.upgrade.v2_0_0;

import com.liferay.content.marketing.platform.web.internal.util.CMPRichTextUtil;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Objects;

/**
 * @author Larissa Ribeiro
 */
public class CMPDescriptionObjectFieldUpgradeProcess extends UpgradeProcess {

	public CMPDescriptionObjectFieldUpgradeProcess(
		CompanyLocalService companyLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectFieldLocalService objectFieldLocalService) {

		_companyLocalService = companyLocalService;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectFieldLocalService = objectFieldLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompanyId(
			companyId -> {
				_upgradeDescriptionObjectField(companyId, "L_CMP_PROJECT");
				_upgradeDescriptionObjectField(companyId, "L_CMP_TASK");
			});
	}

	private void _upgradeDescriptionObjectField(
			long companyId, String objectDefinitionExternalReferenceCode)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					objectDefinitionExternalReferenceCode, companyId);

		if (objectDefinition == null) {
			return;
		}

		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			objectDefinition.getObjectDefinitionId(), "description");

		if ((objectField == null) ||
			Objects.equals(
				objectField.getBusinessType(),
				ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT)) {

			return;
		}

		String dbColumnName = objectField.getDBColumnName();
		String pkObjectFieldDBColumnName =
			objectDefinition.getPKObjectFieldDBColumnName();

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select ", pkObjectFieldDBColumnName, ", ", dbColumnName,
					" from ", objectField.getDBTableName(), " where ",
					dbColumnName, " is not null"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					StringBundler.concat(
						"update ", objectField.getDBTableName(), " set ",
						dbColumnName, " = ? where ", pkObjectFieldDBColumnName,
						" = ?"));

			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				String description = resultSet.getString(dbColumnName);

				if (Validator.isBlank(description)) {
					continue;
				}

				preparedStatement2.setString(
					1, CMPRichTextUtil.toHTML(description));
				preparedStatement2.setLong(
					2, resultSet.getLong(pkObjectFieldDBColumnName));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}

		objectField.setBusinessType(
			ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT);

		_objectFieldLocalService.updateObjectField(objectField);
	}

	private final CompanyLocalService _companyLocalService;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectFieldLocalService _objectFieldLocalService;

}