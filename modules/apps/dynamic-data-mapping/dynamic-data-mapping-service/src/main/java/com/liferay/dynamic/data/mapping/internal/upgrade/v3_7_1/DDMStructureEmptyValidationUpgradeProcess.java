/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v3_7_1;

import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormSerializer;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidation;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidationExpression;
import com.liferay.dynamic.data.mapping.util.DDMFormDeserializeUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormSerializeUtil;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Map;

/**
 * @author Sam Ziemer
 */
public class DDMStructureEmptyValidationUpgradeProcess extends UpgradeProcess {

	public DDMStructureEmptyValidationUpgradeProcess(
		DDMFormDeserializer ddmFormDeserializer,
		DDMFormSerializer ddmFormSerializer) {

		_ddmFormDeserializer = ddmFormDeserializer;
		_ddmFormSerializer = ddmFormSerializer;
	}

	@Override
	public void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select ctCollectionId, structureId, definition from " +
					"DDMStructure where classNameId = ? and definition like " +
						"'%validation%'");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMStructure set definition = ? where " +
						"ctCollectionId = ? and structureId = ?");
			PreparedStatement preparedStatement3 = connection.prepareStatement(
				"select ctCollectionId, structureVersionId, definition from " +
					"DDMStructureVersion where structureId = ?");
			PreparedStatement preparedStatement4 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMStructureVersion set definition = ? where " +
						"ctCollectionId = ? and structureVersionId = ?")) {

			preparedStatement1.setLong(
				1,
				PortalUtil.getClassNameId(
					"com.liferay.dynamic.data.lists.model.DDLRecordSet"));

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					String definition = resultSet.getString("definition");

					String newDefinition = _updateDDMFormFieldValidation(
						definition);

					if (newDefinition.equals(definition)) {
						continue;
					}

					preparedStatement2.setString(1, newDefinition);
					preparedStatement2.setLong(
						2, resultSet.getLong("ctCollectionId"));
					preparedStatement2.setLong(
						3, resultSet.getLong("structureId"));

					preparedStatement2.addBatch();

					preparedStatement3.setLong(
						1, resultSet.getLong("structureId"));

					try (ResultSet resultSet2 =
							preparedStatement3.executeQuery()) {

						while (resultSet2.next()) {
							definition = resultSet2.getString("definition");

							newDefinition = _updateDDMFormFieldValidation(
								definition);

							if (newDefinition.equals(definition)) {
								continue;
							}

							preparedStatement4.setString(1, newDefinition);
							preparedStatement4.setLong(
								2, resultSet2.getLong("ctCollectionId"));
							preparedStatement4.setLong(
								3, resultSet2.getLong("structureVersionId"));

							preparedStatement4.addBatch();
						}
					}
				}

				preparedStatement2.executeBatch();

				preparedStatement4.executeBatch();
			}
		}
	}

	private String _updateDDMFormFieldValidation(String definition)
		throws Exception {

		DDMForm ddmForm = DDMFormDeserializeUtil.deserialize(
			_ddmFormDeserializer, definition);

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		for (DDMFormField ddmFormField : ddmFormFieldsMap.values()) {
			DDMFormFieldValidation ddmFormFieldValidation =
				ddmFormField.getDDMFormFieldValidation();

			if (ddmFormFieldValidation == null) {
				continue;
			}

			DDMFormFieldValidationExpression ddmFormFieldValidationExpression =
				ddmFormFieldValidation.getDDMFormFieldValidationExpression();

			if (Validator.isNotNull(
					ddmFormFieldValidationExpression.getName()) ||
				Validator.isNotNull(
					ddmFormFieldValidationExpression.getValue())) {

				continue;
			}

			ddmFormField.setDDMFormFieldValidation(null);
		}

		return DDMFormSerializeUtil.serialize(ddmForm, _ddmFormSerializer);
	}

	private final DDMFormDeserializer _ddmFormDeserializer;
	private final DDMFormSerializer _ddmFormSerializer;

}