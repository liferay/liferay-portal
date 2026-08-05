/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.internal.upgrade.v2_10_0;

import com.liferay.commerce.discount.constants.CommerceDiscountRuleConstants;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceDiscountRuleTypeSettingsUpgradeProcess
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement selectPreparedStatement =
				connection.prepareStatement(
					"select commerceDiscountRuleId, type_, typeSettings from " +
						"CommerceDiscountRule where type_ in (?, ?)");
			PreparedStatement updatePreparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update CommerceDiscountRule set typeSettings = ? where " +
						"commerceDiscountRuleId = ?")) {

			selectPreparedStatement.setString(
				1, CommerceDiscountRuleConstants.TYPE_ADDED_ALL);
			selectPreparedStatement.setString(
				2, CommerceDiscountRuleConstants.TYPE_ADDED_ANY);

			try (ResultSet resultSet = selectPreparedStatement.executeQuery()) {
				while (resultSet.next()) {
					String typeSettings = _getTypeSettings(
						resultSet.getString("type_"),
						resultSet.getString("typeSettings"));

					if (typeSettings == null) {
						continue;
					}

					updatePreparedStatement.setString(1, typeSettings);
					updatePreparedStatement.setLong(
						2, resultSet.getLong("commerceDiscountRuleId"));

					updatePreparedStatement.addBatch();
				}
			}

			updatePreparedStatement.executeBatch();
		}
	}

	private String _getCProductExternalReferenceCode(long cpDefinitionId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select CProduct.externalReferenceCode from CProduct ",
					"inner join CPDefinition on CPDefinition.CProductId = ",
					"CProduct.CProductId where CPDefinition.CPDefinitionId = ",
					"?"))) {

			preparedStatement.setLong(1, cpDefinitionId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getString("externalReferenceCode");
				}
			}
		}

		return null;
	}

	private String _getTypeSettings(String type, String typeSettings)
		throws Exception {

		if (Validator.isNull(typeSettings)) {
			return null;
		}

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				typeSettings
			).build();

		String cpDefinitionIds = typeSettingsUnicodeProperties.getProperty(
			type);

		if (Validator.isNull(cpDefinitionIds)) {
			return null;
		}

		List<String> cProductExternalReferenceCodes =
			TransformUtil.transformToList(
				StringUtil.split(cpDefinitionIds),
				cpDefinitionId -> {
					String cProductExternalReferenceCode =
						_getCProductExternalReferenceCode(
							GetterUtil.getLong(cpDefinitionId));

					if (Validator.isNull(cProductExternalReferenceCode)) {
						return null;
					}

					return cProductExternalReferenceCode;
				});

		typeSettingsUnicodeProperties.setProperty(
			type,
			StringUtil.merge(cProductExternalReferenceCodes, StringPool.COMMA));

		return typeSettingsUnicodeProperties.toString();
	}

}