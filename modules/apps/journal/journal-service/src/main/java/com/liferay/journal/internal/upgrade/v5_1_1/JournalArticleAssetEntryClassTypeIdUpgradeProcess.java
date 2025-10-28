/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.v5_1_1;

import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lourdes Fernández Besada
 */
public class JournalArticleAssetEntryClassTypeIdUpgradeProcess
	extends UpgradeProcess {

	public JournalArticleAssetEntryClassTypeIdUpgradeProcess(
		ClassNameLocalService classNameLocalService) {

		_classNameLocalService = classNameLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		long classNameId = _classNameLocalService.getClassNameId(
			JournalArticle.class.getName());
		Map<Long, Map<Long, List<Long>>> entryIdsMaps = new HashMap<>();

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select distinct AssetEntry.ctCollectionId, AssetEntry.",
					"entryId, AssetEntry.classTypeId, JournalArticle.",
					"DDMStructureId from AssetEntry, JournalArticle where ",
					"AssetEntry.classNameId = ? and (AssetEntry.classPK = ",
					"JournalArticle.id_ or AssetEntry.classPK = ",
					"JournalArticle.resourcePrimKey) and AssetEntry.",
					"classTypeId != JournalArticle.DDMStructureId"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update AssetEntry set classTypeId = ? where " +
						"ctCollectionId = ? and entryId = ?")) {

			preparedStatement1.setLong(1, classNameId);

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					long ctCollectionId = resultSet.getLong(1);
					long entryId = resultSet.getLong(2);
					long classTypeId = resultSet.getLong(3);

					long ddmStructureId = resultSet.getLong(4);

					preparedStatement2.setLong(1, ddmStructureId);

					preparedStatement2.setLong(2, ctCollectionId);
					preparedStatement2.setLong(3, entryId);

					preparedStatement2.addBatch();

					Map<Long, List<Long>> entryIdsMap =
						entryIdsMaps.computeIfAbsent(
							classTypeId, key -> new HashMap<>());

					List<Long> entryIds = entryIdsMap.computeIfAbsent(
						ddmStructureId, key -> new ArrayList<>());

					entryIds.add(entryId);
				}

				preparedStatement2.executeBatch();
			}
		}
		catch (SQLException sqlException) {
			_log.error("Unable to set asset entry class type ID", sqlException);
		}

		if (_log.isDebugEnabled() && entryIdsMaps.isEmpty()) {
			_log.debug(
				"No asset entries with the wrong class type ID were found");
		}

		if (!_log.isWarnEnabled() || entryIdsMaps.isEmpty()) {
			return;
		}

		for (Map.Entry<Long, Map<Long, List<Long>>> entry1 :
				entryIdsMaps.entrySet()) {

			long classTypeId = entry1.getKey();

			_log.warn(
				"Asset entries with the wrong class type ID " + classTypeId +
					" were found");

			Map<Long, List<Long>> entryIdsMap = entry1.getValue();

			for (Map.Entry<Long, List<Long>> entry2 : entryIdsMap.entrySet()) {
				long ddmStructureId = entry2.getKey();
				List<Long> entryIds = entry2.getValue();

				_log.warn(
					ddmStructureId +
						" has been set as class type ID for the entry IDs " +
							entryIds);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleAssetEntryClassTypeIdUpgradeProcess.class);

	private final ClassNameLocalService _classNameLocalService;

}