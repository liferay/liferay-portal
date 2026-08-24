/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.upgrade.v6_2_1;

import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Rubén Pulido
 */
public class LayoutPageTemplateEntryClassTypeKeyUpgradeProcess
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		_updateClassTypeKey(
			"com.liferay.journal.model.JournalArticle", "structureId",
			"structureKey", "DDMStructure");
		_updateClassTypeKey(
			"com.liferay.portal.kernel.repository.model.FileEntry",
			"fileEntryTypeId", "fileEntryTypeKey", "DLFileEntryType");
	}

	private long _getClassNameId(String className) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select classNameId from ClassName_ where value = ?")) {

			preparedStatement.setString(1, className);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getLong("classNameId");
				}
			}
		}

		return 0;
	}

	private void _updateClassTypeKey(
			String className, String classTypeIdColumnName,
			String classTypeKeyColumnName, String classTypeTableName)
		throws Exception {

		long classNameId = _getClassNameId(className);

		if (classNameId == 0) {
			return;
		}

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select LayoutPageTemplateEntry.ctCollectionId, ",
					"LayoutPageTemplateEntry.layoutPageTemplateEntryId, ",
					classTypeTableName, ".", classTypeKeyColumnName,
					" from LayoutPageTemplateEntry inner join ",
					classTypeTableName, " on ", classTypeTableName, ".",
					classTypeIdColumnName,
					" = LayoutPageTemplateEntry.classTypeId and (",
					classTypeTableName,
					".ctCollectionId = LayoutPageTemplateEntry.ctCollectionId ",
					"or ", classTypeTableName, ".ctCollectionId = 0) where ",
					"LayoutPageTemplateEntry.classNameId = ? and ",
					"LayoutPageTemplateEntry.type_ = ? and ",
					"(LayoutPageTemplateEntry.classTypeKey is null or ",
					"LayoutPageTemplateEntry.classTypeKey = '')"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update LayoutPageTemplateEntry set classTypeKey = ? " +
						"where ctCollectionId = ? and " +
							"layoutPageTemplateEntryId = ?")) {

			preparedStatement1.setLong(1, classNameId);
			preparedStatement1.setInt(
				2, LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE);

			ResultSet resultSet = preparedStatement1.executeQuery();

			while (resultSet.next()) {
				preparedStatement2.setString(
					1, resultSet.getString(classTypeKeyColumnName));
				preparedStatement2.setLong(
					2, resultSet.getLong("ctCollectionId"));
				preparedStatement2.setLong(
					3, resultSet.getLong("layoutPageTemplateEntryId"));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

}