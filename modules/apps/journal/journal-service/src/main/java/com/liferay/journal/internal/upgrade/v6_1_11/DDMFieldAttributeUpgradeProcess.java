/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.v6_1_11;

import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMFieldAttribute;
import com.liferay.journal.internal.upgrade.helper.JournalArticleImageUpgradeHelper;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.Validator;

import java.nio.charset.StandardCharsets;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Set;

/**
 * @author Akhash Ramprakash
 */
public class DDMFieldAttributeUpgradeProcess extends UpgradeProcess {

	public DDMFieldAttributeUpgradeProcess(
		ClassNameLocalService classNameLocalService,
		JournalArticleImageUpgradeHelper journalArticleImageUpgradeHelper) {

		_classNameLocalService = classNameLocalService;
		_journalArticleImageUpgradeHelper = journalArticleImageUpgradeHelper;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select DDMFieldAttribute.fieldAttributeId, ",
					"DDMFieldAttribute.companyId, DDMFieldAttribute.fieldId, ",
					"DDMFieldAttribute.storageId, ",
					"DDMFieldAttribute.languageId, ",
					"DDMFieldAttribute.largeAttributeValue, ",
					"DDMFieldAttribute.smallAttributeValue from DDMStructure ",
					"inner join DDMStructureVersion on ",
					"DDMStructureVersion.ctCollectionId = 0 and ",
					"DDMStructure.structureId = ",
					"DDMStructureVersion.structureId inner join DDMField on ",
					"DDMStructureVersion.structureVersionId = ",
					"DDMField.structureVersionId inner join DDMFieldAttribute ",
					"on DDMField.ctCollectionId = ",
					"DDMFieldAttribute.ctCollectionId and DDMField.fieldId = ",
					"DDMFieldAttribute.fieldId where ",
					"DDMFieldAttribute.ctCollectionId = 0 and ",
					"DDMStructure.ctCollectionId = 0 and ",
					"DDMStructure.classNameId = ? and DDMField.fieldType = ? ",
					"and (DDMFieldAttribute.attributeName is null or ",
					"DDMFieldAttribute.attributeName = '')"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"delete from DDMFieldAttribute where ctCollectionId = 0 " +
						"and fieldAttributeId = ?");
			PreparedStatement preparedStatement3 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					StringBundler.concat(
						"insert into DDMFieldAttribute (mvccVersion, ",
						"ctCollectionId, fieldAttributeId, companyId, ",
						"fieldId, storageId, attributeName, languageId, ",
						"largeAttributeValue, smallAttributeValue) values (0, ",
						"0, ?, ?, ?, ?, ?, ?, ?, ?)"))) {

			preparedStatement1.setLong(
				1,
				_classNameLocalService.getClassNameId(
					JournalArticle.class.getName()));
			preparedStatement1.setString(
				2, DDMFormFieldTypeConstants.DOCUMENT_LIBRARY);

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					String attributeValue = resultSet.getString(
						"largeAttributeValue");

					if (Validator.isBlank(attributeValue)) {
						attributeValue = resultSet.getString(
							"smallAttributeValue");
					}

					if (Validator.isBlank(attributeValue)) {
						continue;
					}

					String documentLibraryValue =
						_journalArticleImageUpgradeHelper.
							getDocumentLibraryValue(attributeValue);

					if (Validator.isNull(documentLibraryValue)) {
						continue;
					}

					preparedStatement2.setLong(
						1, resultSet.getLong("fieldAttributeId"));

					preparedStatement2.addBatch();

					JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
						documentLibraryValue);

					Set<String> keySet = jsonObject.keySet();

					long fieldAttributeId = increment(
						DDMFieldAttribute.class.getName(), keySet.size());

					fieldAttributeId -= keySet.size();

					long companyId = resultSet.getLong("companyId");
					long fieldId = resultSet.getLong("fieldId");
					String languageId = resultSet.getString("languageId");
					long storageId = resultSet.getLong("storageId");

					for (String key : keySet) {
						_addDDMFieldAttribute(
							preparedStatement3, ++fieldAttributeId, companyId,
							fieldId, storageId, key, languageId,
							_jsonSerializer.serialize(jsonObject.get(key)));
					}
				}
			}

			preparedStatement2.executeBatch();

			preparedStatement3.executeBatch();
		}
	}

	private void _addDDMFieldAttribute(
			PreparedStatement preparedStatement, long fieldAttributeId,
			long companyId, long fieldId, long storageId, String attributeName,
			String languageId, String attributeValue)
		throws Exception {

		preparedStatement.setLong(1, fieldAttributeId);
		preparedStatement.setLong(2, companyId);
		preparedStatement.setLong(3, fieldId);
		preparedStatement.setLong(4, storageId);
		preparedStatement.setString(5, attributeName);
		preparedStatement.setString(6, languageId);

		byte[] bytes = attributeValue.getBytes(StandardCharsets.UTF_8);

		if (bytes.length > _SMALL_ATTRIBUTE_VALUE_MAX_LENGTH) {
			preparedStatement.setString(7, attributeValue);
			preparedStatement.setString(8, null);
		}
		else {
			preparedStatement.setString(7, null);
			preparedStatement.setString(8, attributeValue);
		}

		preparedStatement.addBatch();
	}

	private static final int _SMALL_ATTRIBUTE_VALUE_MAX_LENGTH = 255;

	private final ClassNameLocalService _classNameLocalService;
	private final JournalArticleImageUpgradeHelper
		_journalArticleImageUpgradeHelper;
	private final JSONSerializer _jsonSerializer =
		JSONFactoryUtil.createJSONSerializer();

}