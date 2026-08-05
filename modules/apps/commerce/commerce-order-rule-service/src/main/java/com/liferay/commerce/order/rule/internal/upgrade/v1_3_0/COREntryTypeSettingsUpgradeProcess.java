/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.rule.internal.upgrade.v1_3_0;

import com.liferay.commerce.order.rule.constants.COREntryConstants;
import com.liferay.petra.function.transform.TransformUtil;
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
public class COREntryTypeSettingsUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement selectPreparedStatement =
				connection.prepareStatement(
					"select COREntryId, typeSettings from COREntry where " +
						"type_ = ?");
			PreparedStatement updatePreparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update COREntry set typeSettings = ? where COREntryId = " +
						"?")) {

			selectPreparedStatement.setString(
				1, COREntryConstants.TYPE_PRODUCTS_LIMIT);

			try (ResultSet resultSet = selectPreparedStatement.executeQuery()) {
				while (resultSet.next()) {
					String typeSettings = _getTypeSettings(
						resultSet.getString("typeSettings"));

					if (typeSettings == null) {
						continue;
					}

					updatePreparedStatement.setString(1, typeSettings);
					updatePreparedStatement.setLong(
						2, resultSet.getLong("COREntryId"));

					updatePreparedStatement.addBatch();
				}
			}

			updatePreparedStatement.executeBatch();
		}
	}

	private String _getCProductExternalReferenceCode(long cProductId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select externalReferenceCode from CProduct where CProductId " +
					"= ?")) {

			preparedStatement.setLong(1, cProductId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getString("externalReferenceCode");
				}
			}
		}

		return null;
	}

	private String _getTypeSettings(String typeSettings) throws Exception {
		if (Validator.isNull(typeSettings)) {
			return null;
		}

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				typeSettings
			).build();

		String cProductIds = typeSettingsUnicodeProperties.getProperty(
			_TYPE_PRODUCTS_LIMIT_FIELD_PRODUCT_IDS);

		if (Validator.isNull(cProductIds)) {
			return null;
		}

		List<String> cProductExternalReferenceCodes =
			TransformUtil.transformToList(
				StringUtil.split(cProductIds),
				cProductId -> {
					String cProductExternalReferenceCode =
						_getCProductExternalReferenceCode(
							GetterUtil.getLong(cProductId));

					if (Validator.isNull(cProductExternalReferenceCode)) {
						return null;
					}

					return cProductExternalReferenceCode;
				});

		typeSettingsUnicodeProperties.remove(
			_TYPE_PRODUCTS_LIMIT_FIELD_PRODUCT_IDS);

		typeSettingsUnicodeProperties.setProperty(
			COREntryConstants.
				TYPE_PRODUCTS_LIMIT_FIELD_PRODUCT_EXTERNAL_REFERENCE_CODES,
			StringUtil.merge(cProductExternalReferenceCodes, StringPool.COMMA));

		return typeSettingsUnicodeProperties.toString();
	}

	private static final String _TYPE_PRODUCTS_LIMIT_FIELD_PRODUCT_IDS =
		"products-limit-field-product-ids";

}