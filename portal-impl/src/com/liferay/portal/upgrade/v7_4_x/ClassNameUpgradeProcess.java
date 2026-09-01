/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.document.library.kernel.processor.RawMetadataProcessor;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Marco Galluzzi
 */
public class ClassNameUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		long oldClassNameId = _getClassNameId(
			"com.liferay.document.library.kernel.util.RawMetadataProcessor");

		if (oldClassNameId == 0) {
			return;
		}

		long newClassNameId = _getClassNameId(
			RawMetadataProcessor.class.getName());

		if (newClassNameId == 0) {
			_updateClassNameValue(oldClassNameId);

			return;
		}

		_deleteClassName(oldClassNameId);

		for (long companyId : PortalInstancePool.getCompanyIds()) {
			long[] oldDDMStructureValues = _getDDMStructureValues(
				oldClassNameId, companyId);

			if (oldDDMStructureValues == null) {
				continue;
			}

			long oldCTCollectionId = oldDDMStructureValues[0];
			long oldStructureId = oldDDMStructureValues[1];

			long[] newDDMStructureValues = _getDDMStructureValues(
				newClassNameId, companyId);

			if (newDDMStructureValues == null) {
				_updateDDMStructureClassNameId(
					newClassNameId, oldCTCollectionId, oldStructureId);

				continue;
			}

			long oldDLFileEntryMetadatasCount = _getDLFileEntryMetadatasCount(
				oldStructureId);

			if (oldDLFileEntryMetadatasCount == 0) {
				_deleteDDMStructure(oldCTCollectionId, oldStructureId);

				continue;
			}

			long newCTCollectionId = newDDMStructureValues[0];

			long newStructureId = newDDMStructureValues[1];

			long newDLFileEntryMetadatasCount = _getDLFileEntryMetadatasCount(
				newStructureId);

			if (newDLFileEntryMetadatasCount == 0) {
				_deleteDDMStructure(newCTCollectionId, newStructureId);

				_updateDDMStructureClassNameId(
					newClassNameId, oldCTCollectionId, oldStructureId);

				continue;
			}

			if (newDLFileEntryMetadatasCount >= oldDLFileEntryMetadatasCount) {
				_updateDDMStructureRelatedTables(
					newStructureId, oldStructureId);

				_deleteDDMStructure(oldCTCollectionId, oldStructureId);
			}
			else {
				_updateDDMStructureRelatedTables(
					oldStructureId, newStructureId);

				_deleteDDMStructure(newCTCollectionId, newStructureId);

				_updateDDMStructureClassNameId(
					newClassNameId, oldCTCollectionId, oldStructureId);
			}
		}
	}

	private void _deleteClassName(long classNameId) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"delete from ClassName_ where classNameId = ?")) {

			preparedStatement.setLong(1, classNameId);

			preparedStatement.execute();
		}
	}

	private void _deleteDDMStructure(long ctCollectionId, long structureId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"delete from DDMStructureLayout where structureVersionId ",
					"in (select structureVersionId from DDMStructureVersion ",
					"where structureId = ?)"))) {

			preparedStatement.setLong(1, structureId);

			preparedStatement.execute();
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"delete from DDMStructureVersion where structureId = ?")) {

			preparedStatement.setLong(1, structureId);

			preparedStatement.execute();
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"delete from DDMStructure where ctCollectionId = ? and " +
					"structureId = ?")) {

			preparedStatement.setLong(1, ctCollectionId);
			preparedStatement.setLong(2, structureId);

			preparedStatement.execute();
		}
	}

	private void _deleteDLFileEntryMetadata(
			long newStructureId, long oldStructureId)
		throws Exception {

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select d1.fileEntryMetadataId from DLFileEntryMetadata ",
					"d1 where d1.DDMStructureId = ? and exists (select 1 from ",
					"DLFileEntryMetadata d2 where d1.ctCollectionId = ",
					"d2.ctCollectionId and d2.DDMStructureId = ? and ",
					"d1.fileVersionId = d2.fileVersionId)"))) {

			preparedStatement1.setLong(1, oldStructureId);
			preparedStatement1.setLong(2, newStructureId);

			try (PreparedStatement preparedStatement2 =
					AutoBatchPreparedStatementUtil.autoBatch(
						connection,
						"delete from DLFileEntryMetadata where " +
							"fileEntryMetadataId = ?");
				ResultSet resultSet = preparedStatement1.executeQuery()) {

				while (resultSet.next()) {
					preparedStatement2.setLong(
						1, resultSet.getLong("fileEntryMetadataId"));

					preparedStatement2.addBatch();
				}

				preparedStatement2.executeBatch();
			}
		}
	}

	private long _getClassNameId(String value) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select classNameId from ClassName_ where value = ?")) {

			preparedStatement.setString(1, value);

			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return resultSet.getLong("classNameId");
			}
		}

		return 0;
	}

	private long[] _getDDMStructureValues(long classNameId, long companyId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select ctCollectionId, structureId from DDMStructure where " +
					"classNameId = ? and companyId = ?")) {

			preparedStatement.setLong(1, classNameId);
			preparedStatement.setLong(2, companyId);

			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return new long[] {
					resultSet.getLong("ctCollectionId"),
					resultSet.getLong("structureId")
				};
			}
		}

		return null;
	}

	private long _getDDMStructureVersionId(long structureId) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select structureVersionId from DDMStructureVersion where " +
					"structureId = ?")) {

			preparedStatement.setLong(1, structureId);

			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return resultSet.getLong("structureVersionId");
			}
		}

		return 0;
	}

	private long _getDLFileEntryMetadatasCount(long structureId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select count(*) as count from DLFileEntryMetadata where " +
					"DDMStructureId = ?")) {

			preparedStatement.setLong(1, structureId);

			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return resultSet.getLong("count");
			}
		}

		return 0;
	}

	private void _update(
			String table, String column, long newValue, long oldValue)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"update ", table, " set ", column, " = ? where ", column,
					" = ?"))) {

			preparedStatement.setLong(1, newValue);
			preparedStatement.setLong(2, oldValue);

			preparedStatement.execute();
		}
	}

	private void _updateClassNameValue(long classNameId) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update ClassName_ set value = ? where classNameId = ? ")) {

			preparedStatement.setString(
				1, RawMetadataProcessor.class.getName());
			preparedStatement.setLong(2, classNameId);

			preparedStatement.executeUpdate();
		}
	}

	private void _updateDDMStructureClassNameId(
			long classNameId, long ctCollectionId, long structureId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update DDMStructure set classNameId = ? where " +
					"ctCollectionId = ? and structureId = ?")) {

			preparedStatement.setLong(1, classNameId);
			preparedStatement.setLong(2, ctCollectionId);
			preparedStatement.setLong(3, structureId);

			preparedStatement.execute();
		}
	}

	private void _updateDDMStructureRelatedTables(
			long newStructureId, long oldStructureId)
		throws Exception {

		_deleteDLFileEntryMetadata(newStructureId, oldStructureId);

		_update(
			"DDMStorageLink", "structureId", newStructureId, oldStructureId);
		_update(
			"DDMStructureLink", "structureId", newStructureId, oldStructureId);
		_update(
			"DLFileEntryMetadata", "DDMStructureId", newStructureId,
			oldStructureId);

		long newStructureVersionId = _getDDMStructureVersionId(newStructureId);
		long oldStructureVersionId = _getDDMStructureVersionId(oldStructureId);

		_update(
			"DDMField", "structureVersionId", newStructureVersionId,
			oldStructureVersionId);
		_update(
			"DDMStorageLink", "structureVersionId", newStructureVersionId,
			oldStructureVersionId);
		_update(
			"DDMStructureLayout", "structureVersionId", newStructureVersionId,
			oldStructureVersionId);
	}

}