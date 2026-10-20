/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.v6_1_12;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.dynamic.data.mapping.model.DDMFieldAttribute;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Mariano Álvaro Sáiz
 */
public class DDMFieldAttributeLanguageIdUpgradeProcess extends UpgradeProcess {

	public DDMFieldAttributeLanguageIdUpgradeProcess(
		ClassNameLocalService classNameLocalService) {

		_classNameLocalService = classNameLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		DB db = DBManagerUtil.getDB();

		try (SafeCloseable safeCloseable = db.addTemporaryIndex(
				connection, "DDMFieldAttribute", false, "ctCollectionId",
				"fieldId", "languageId");
			PreparedStatement preparedStatement1 = connection.prepareStatement(
				SQLTransformer.transform(
					StringBundler.concat(
						"select DDMField.ctCollectionId, DDMField.fieldId, ",
						"DDMField.storageId, DDMField.localizable from ",
						"DDMField where DDMField.structureVersionId in ",
						"(select DDMStructureVersion.structureVersionId from ",
						"DDMStructure inner join DDMStructureVersion on ",
						"DDMStructure.structureId = ",
						"DDMStructureVersion.structureId where ",
						"DDMStructure.classNameId = ?) and ",
						"((DDMField.localizable = [$FALSE$] and exists (",
						_getSelectSQL(false), ") and not exists (",
						_getSelectSQL(true), ")) or (DDMField.localizable = ",
						"[$TRUE$] and exists (", _getSelectSQL(true), ") and ",
						"not exists (", _getSelectSQL(false), "))) order by ",
						"DDMField.ctCollectionId, DDMField.storageId")));
			PreparedStatement preparedStatement2 = connection.prepareStatement(
				StringBundler.concat(
					"select DDMFieldAttribute.attributeName, ",
					"DDMFieldAttribute.largeAttributeValue, ",
					"DDMFieldAttribute.smallAttributeValue from DDMField ",
					"inner join DDMFieldAttribute on DDMField.ctCollectionId ",
					"= DDMFieldAttribute.ctCollectionId and DDMField.fieldId ",
					"= DDMFieldAttribute.fieldId where ",
					"DDMField.ctCollectionId = ? and DDMField.storageId = ? ",
					"and DDMField.parentFieldId = 0 and (DDMField.fieldName ",
					"is null or DDMField.fieldName = '')"));
			PreparedStatement preparedStatement3 = connection.prepareStatement(
				"select 1 from DDMFieldAttribute where ctCollectionId = ? " +
					"and fieldId = ? and languageId = ?");
			PreparedStatement preparedStatement4 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					StringBundler.concat(
						"update DDMFieldAttribute set languageId = '' where ",
						"ctCollectionId = ? and fieldId = ? and languageId = ",
						"?"));
			PreparedStatement preparedStatement5 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					SQLTransformer.transform(
						StringBundler.concat(
							"delete from DDMFieldAttribute where ",
							"ctCollectionId = 0 and fieldId = ? and ",
							"languageId != '' and languageId != ?")));
			PreparedStatement preparedStatement6 = connection.prepareStatement(
				StringBundler.concat(
					"select companyId, attributeName, largeAttributeValue, ",
					"smallAttributeValue from DDMFieldAttribute where ",
					"ctCollectionId = 0 and fieldId = ?"));
			PreparedStatement preparedStatement7 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					StringBundler.concat(
						"insert into DDMFieldAttribute (mvccVersion, ",
						"ctCollectionId, fieldAttributeId, companyId, ",
						"fieldId, storageId, attributeName, languageId, ",
						"largeAttributeValue, smallAttributeValue) values (0, ",
						"0, ?, ?, ?, ?, ?, ?, ?, ?)"));
			PreparedStatement preparedStatement8 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					StringBundler.concat(
						"update DDMFieldAttribute set languageId = ? where ",
						"ctCollectionId = ? and fieldId = ? and (languageId ",
						"is null or languageId = '')"))) {

			preparedStatement1.setLong(
				1,
				_classNameLocalService.getClassNameId(
					JournalArticle.class.getName()));

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				List<String> languageIds = Collections.emptyList();
				long languageIdsCTCollectionId = 0;
				long languageIdsStorageId = 0;

				while (resultSet.next()) {
					long ctCollectionId = resultSet.getLong("ctCollectionId");
					long storageId = resultSet.getLong("storageId");

					if ((ctCollectionId != languageIdsCTCollectionId) ||
						(storageId != languageIdsStorageId)) {

						languageIds = _getLanguageIds(
							ctCollectionId, preparedStatement2, storageId);
						languageIdsCTCollectionId = ctCollectionId;
						languageIdsStorageId = storageId;
					}

					if (languageIds.isEmpty()) {
						continue;
					}

					long fieldId = resultSet.getLong("fieldId");

					if (resultSet.getBoolean("localizable")) {
						_localizeDDMFieldAttributes(
							ctCollectionId, fieldId, languageIds,
							preparedStatement6, preparedStatement7,
							preparedStatement8, storageId);
					}
					else {
						_unlocalizeDDMFieldAttributes(
							ctCollectionId, languageIds.get(0), fieldId,
							preparedStatement3, preparedStatement4,
							preparedStatement5);
					}
				}
			}

			preparedStatement4.executeBatch();

			preparedStatement5.executeBatch();

			preparedStatement7.executeBatch();

			preparedStatement8.executeBatch();
		}
	}

	private List<String> _getLanguageIds(
			long ctCollectionId, PreparedStatement preparedStatement,
			long storageId)
		throws Exception {

		String availableLanguageIds = null;
		String defaultLanguageId = null;

		preparedStatement.setLong(1, ctCollectionId);
		preparedStatement.setLong(2, storageId);

		try (ResultSet resultSet = preparedStatement.executeQuery()) {
			while (resultSet.next()) {
				String attributeValue = resultSet.getString(
					"largeAttributeValue");

				if (Validator.isNull(attributeValue)) {
					attributeValue = resultSet.getString("smallAttributeValue");
				}

				String attributeName = resultSet.getString("attributeName");

				if (Objects.equals(attributeName, "availableLanguageIds")) {
					availableLanguageIds = attributeValue;
				}
				else if (Objects.equals(attributeName, "defaultLanguageId")) {
					defaultLanguageId = attributeValue;
				}
			}
		}

		if (Validator.isNull(defaultLanguageId)) {
			return Collections.emptyList();
		}

		Set<String> languageIds = new LinkedHashSet<>();

		languageIds.add(defaultLanguageId);

		Collections.addAll(languageIds, StringUtil.split(availableLanguageIds));

		return new ArrayList<>(languageIds);
	}

	private String _getSelectSQL(boolean blankLanguageId) {
		String sql =
			"select 1 from DDMFieldAttribute TEMP_TABLE where " +
				"TEMP_TABLE.ctCollectionId = DDMField.ctCollectionId and " +
					"TEMP_TABLE.fieldId = DDMField.fieldId and ";

		if (blankLanguageId) {
			return sql +
				"(TEMP_TABLE.languageId is null or TEMP_TABLE.languageId = '')";
		}

		return sql + "TEMP_TABLE.languageId != ''";
	}

	private void _localizeDDMFieldAttributes(
			long ctCollectionId, long fieldId, List<String> languageIds,
			PreparedStatement preparedStatement6,
			PreparedStatement preparedStatement7,
			PreparedStatement preparedStatement8, long storageId)
		throws Exception {

		preparedStatement8.setString(1, languageIds.get(0));
		preparedStatement8.setLong(2, ctCollectionId);
		preparedStatement8.setLong(3, fieldId);

		preparedStatement8.addBatch();

		if (ctCollectionId != CTConstants.CT_COLLECTION_ID_PRODUCTION) {
			return;
		}

		preparedStatement6.setLong(1, fieldId);

		try (ResultSet resultSet = preparedStatement6.executeQuery()) {
			while (resultSet.next()) {
				String attributeName = resultSet.getString("attributeName");
				long companyId = resultSet.getLong("companyId");
				String largeAttributeValue = resultSet.getString(
					"largeAttributeValue");
				String smallAttributeValue = resultSet.getString(
					"smallAttributeValue");

				for (String languageId :
						languageIds.subList(1, languageIds.size())) {

					preparedStatement7.setLong(
						1, increment(DDMFieldAttribute.class.getName()));
					preparedStatement7.setLong(2, companyId);
					preparedStatement7.setLong(3, fieldId);
					preparedStatement7.setLong(4, storageId);
					preparedStatement7.setString(5, attributeName);
					preparedStatement7.setString(6, languageId);
					preparedStatement7.setString(7, largeAttributeValue);
					preparedStatement7.setString(8, smallAttributeValue);

					preparedStatement7.addBatch();
				}
			}
		}
	}

	private void _unlocalizeDDMFieldAttributes(
			long ctCollectionId, String defaultLanguageId, long fieldId,
			PreparedStatement preparedStatement3,
			PreparedStatement preparedStatement4,
			PreparedStatement preparedStatement5)
		throws Exception {

		preparedStatement3.setLong(1, ctCollectionId);
		preparedStatement3.setLong(2, fieldId);
		preparedStatement3.setString(3, defaultLanguageId);

		try (ResultSet resultSet = preparedStatement3.executeQuery()) {
			if (!resultSet.next()) {
				return;
			}
		}

		preparedStatement4.setLong(1, ctCollectionId);
		preparedStatement4.setLong(2, fieldId);
		preparedStatement4.setString(3, defaultLanguageId);

		preparedStatement4.addBatch();

		if (ctCollectionId != CTConstants.CT_COLLECTION_ID_PRODUCTION) {
			return;
		}

		preparedStatement5.setLong(1, fieldId);
		preparedStatement5.setString(2, defaultLanguageId);

		preparedStatement5.addBatch();
	}

	private final ClassNameLocalService _classNameLocalService;

}