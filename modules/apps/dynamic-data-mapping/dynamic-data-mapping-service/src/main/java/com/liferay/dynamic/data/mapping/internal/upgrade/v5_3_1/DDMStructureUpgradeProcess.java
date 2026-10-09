/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v5_3_1;

import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormSerializer;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.util.DDMFormDeserializeUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormSerializeUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Mikel Lorza
 */
public class DDMStructureUpgradeProcess extends UpgradeProcess {

	public DDMStructureUpgradeProcess(
		DDMFormDeserializer ddmFormDeserializer,
		DDMFormSerializer ddmFormSerializer, Language language) {

		_ddmFormDeserializer = ddmFormDeserializer;
		_ddmFormSerializer = ddmFormSerializer;
		_language = language;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_upgradeDDMStructure();
		_upgradeDDMStructureVersion();
	}

	private void _addLabelToFieldsGroup(
		Set<Locale> availableLocales, List<DDMFormField> ddmFormFields) {

		for (DDMFormField ddmFormField : ddmFormFields) {
			if (Objects.equals(
					ddmFormField.getType(),
					DDMFormFieldTypeConstants.FIELDSET) &&
				!_isLabelSet(ddmFormField)) {

				ddmFormField.setLabel(
					new LocalizedValue() {
						{
							for (Locale locale : availableLocales) {
								addString(
									locale,
									_language.get(locale, "fields-group"));
							}
						}
					});
				ddmFormField.setLocalizable(true);
				ddmFormField.setProperty("labelAtStructureLevel", true);
				ddmFormField.setShowLabel(true);
			}

			if (ListUtil.isEmpty(ddmFormField.getNestedDDMFormFields())) {
				continue;
			}

			_addLabelToFieldsGroup(
				availableLocales, ddmFormField.getNestedDDMFormFields());
		}
	}

	private String _addLabelToFieldsGroup(String dataDefinition)
		throws Exception {

		DDMForm ddmForm = DDMFormDeserializeUtil.deserialize(
			_ddmFormDeserializer, dataDefinition);

		_addLabelToFieldsGroup(
			ddmForm.getAvailableLocales(), ddmForm.getDDMFormFields());

		return DDMFormSerializeUtil.serialize(ddmForm, _ddmFormSerializer);
	}

	private boolean _isLabelSet(DDMFormField ddmFormField) {
		LocalizedValue localizedValue = ddmFormField.getLabel();

		Map<Locale, String> values = localizedValue.getValues();

		for (String value : values.values()) {
			if (Validator.isNotNull(value)) {
				return true;
			}
		}

		return false;
	}

	private void _upgradeDDMStructure() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select ctCollectionId, structureId, definition from " +
					"DDMStructure where classNameId = ? or classNameId = ? " +
						"order by createDate");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMStructure set definition = ? where " +
						"ctCollectionId = ? and structureId = ?")) {

			preparedStatement1.setLong(
				1,
				PortalUtil.getClassNameId(_CLASS_NAME_DL_FILE_ENTRY_METADATA));
			preparedStatement1.setLong(
				2, PortalUtil.getClassNameId(_CLASS_NAME_JOURNAL_ARTICLE));

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					preparedStatement2.setString(
						1,
						_addLabelToFieldsGroup(
							resultSet.getString("definition")));
					preparedStatement2.setLong(
						2, resultSet.getLong("ctCollectionId"));
					preparedStatement2.setLong(
						3, resultSet.getLong("structureId"));

					preparedStatement2.addBatch();
				}

				preparedStatement2.executeBatch();
			}
		}
	}

	private void _upgradeDDMStructureVersion() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select DDMStructure.structureKey,  ",
					"DDMStructureVersion.ctCollectionId, ",
					"DDMStructureVersion.structureVersionId, ",
					"DDMStructureVersion.definition from DDMStructureVersion ",
					"inner join DDMStructure on DDMStructure.structureId = ",
					"DDMStructureVersion.structureId where ",
					"DDMStructure.classNameId = ? or DDMStructure.classNameId ",
					"= ?"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMStructureVersion set definition = ? where " +
						"ctCollectionId = ? and structureVersionId = ?")) {

			preparedStatement1.setLong(
				1,
				PortalUtil.getClassNameId(_CLASS_NAME_DL_FILE_ENTRY_METADATA));
			preparedStatement1.setLong(
				2, PortalUtil.getClassNameId(_CLASS_NAME_JOURNAL_ARTICLE));

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					preparedStatement2.setString(
						1,
						_addLabelToFieldsGroup(
							resultSet.getString("definition")));
					preparedStatement2.setLong(
						2, resultSet.getLong("ctCollectionId"));
					preparedStatement2.setLong(
						3, resultSet.getLong("structureVersionId"));

					preparedStatement2.addBatch();
				}

				preparedStatement2.executeBatch();
			}
		}
	}

	private static final String _CLASS_NAME_DL_FILE_ENTRY_METADATA =
		"com.liferay.document.library.kernel.model.DLFileEntryMetadata";

	private static final String _CLASS_NAME_JOURNAL_ARTICLE =
		"com.liferay.journal.model.JournalArticle";

	private final DDMFormDeserializer _ddmFormDeserializer;
	private final DDMFormSerializer _ddmFormSerializer;
	private final Language _language;

}