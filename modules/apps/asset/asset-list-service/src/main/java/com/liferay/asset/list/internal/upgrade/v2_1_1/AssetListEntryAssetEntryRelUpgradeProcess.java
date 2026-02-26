/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.internal.upgrade.v2_1_1;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LoggingTimer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Lourdes Fernández Besada
 */
public class AssetListEntryAssetEntryRelUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		Map<Long, Map<Long, Map<Long, List<Integer>>>> map =
			new ConcurrentHashMap<>();

		String sql = StringBundler.concat(
			"select distinct ctCollectionId, assetListEntryAssetEntryRelId, ",
			"assetListEntryId, segmentsEntryId, position from ",
			"AssetListEntryAssetEntryRel where not exists (select 1 from ",
			"AssetEntry where entryId = assetEntryId)");

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			processConcurrently(
				SQLTransformer.transform(sql),
				"delete from AssetListEntryAssetEntryRel where " +
					"ctCollectionId = ? and assetListEntryAssetEntryRelId = ?",
				resultSet -> new Object[] {
					resultSet.getLong("ctCollectionId"),
					resultSet.getLong("assetListEntryAssetEntryRelId"),
					resultSet.getLong("assetListEntryId"),
					resultSet.getLong("segmentsEntryId"),
					resultSet.getInt("position")
				},
				(values, preparedStatement) -> {
					long ctCollectionId = (Long)values[0];
					long assetListEntryAssetEntryRelId = (Long)values[1];

					preparedStatement.setLong(1, ctCollectionId);
					preparedStatement.setLong(2, assetListEntryAssetEntryRelId);

					preparedStatement.addBatch();

					Map<Long, Map<Long, List<Integer>>> assetListEntryIdsMap =
						map.computeIfAbsent(
							ctCollectionId,
							curCTCollectionId -> new ConcurrentHashMap<>());

					Map<Long, List<Integer>> segmentsEntryIdsMap =
						assetListEntryIdsMap.computeIfAbsent(
							(Long)values[2],
							curAssetListEntryId -> new ConcurrentHashMap<>());

					List<Integer> positions =
						segmentsEntryIdsMap.computeIfAbsent(
							(Long)values[3],
							curSegmentsEntryId -> new ArrayList<>());

					positions.add((Integer)values[4]);
				},
				null);

			try (PreparedStatement preparedStatement =
					AutoBatchPreparedStatementUtil.concurrentAutoBatch(
						connection,
						"update AssetListEntryAssetEntryRel set position = ? " +
							"where ctCollectionId = ? and " +
								"assetListEntryAssetEntryRelId = ?")) {

				for (Map.Entry<Long, Map<Long, Map<Long, List<Integer>>>>
						ctCollectionIdEntry : map.entrySet()) {

					Map<Long, Map<Long, List<Integer>>> assetListEntryIdsMap =
						ctCollectionIdEntry.getValue();

					for (Map.Entry<Long, Map<Long, List<Integer>>>
							assetListEntryIdEntry :
								assetListEntryIdsMap.entrySet()) {

						Map<Long, List<Integer>> segmentsEntryIdsMap =
							assetListEntryIdEntry.getValue();

						for (Map.Entry<Long, List<Integer>>
								segmentsEntryIdEntry :
									segmentsEntryIdsMap.entrySet()) {

							List<Integer> positions =
								segmentsEntryIdEntry.getValue();

							Collections.sort(positions);

							_updateAssetListEntryAssetEntryRelPositions(
								assetListEntryIdEntry.getKey(),
								ctCollectionIdEntry.getKey(), positions,
								preparedStatement,
								segmentsEntryIdEntry.getKey());
						}
					}
				}
			}
		}
	}

	private void _updateAssetListEntryAssetEntryRelPositions(
		long assetListEntryId, long ctCollectionId, List<Integer> positions,
		PreparedStatement preparedStatement1, long segmentsEntryId) {

		try (PreparedStatement preparedStatement2 = connection.prepareStatement(
				SQLTransformer.transform(
					StringBundler.concat(
						"select distinct assetListEntryAssetEntryRelId, ",
						"position from AssetListEntryAssetEntryRel where ",
						"ctCollectionId = ? and assetListEntryId = ? and ",
						"segmentsEntryId = ? and position > ? order by ",
						"position asc")))) {

			preparedStatement2.setLong(1, ctCollectionId);
			preparedStatement2.setLong(2, assetListEntryId);
			preparedStatement2.setLong(3, segmentsEntryId);
			preparedStatement2.setLong(4, positions.get(0));

			try (ResultSet resultSet = preparedStatement2.executeQuery()) {
				while (resultSet.next()) {
					int curPosition = resultSet.getInt("position");

					for (Integer position : positions) {
						if (curPosition < position) {
							break;
						}

						curPosition--;
					}

					preparedStatement1.setLong(1, curPosition);

					preparedStatement1.setLong(2, ctCollectionId);

					preparedStatement1.setLong(
						3, resultSet.getLong("assetListEntryAssetEntryRelId"));

					preparedStatement1.addBatch();
				}
			}

			preparedStatement1.executeBatch();
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to update asset list entry and asset entry ",
						"relationships for asset list entry ID ",
						assetListEntryId, " and segments entry ID ",
						segmentsEntryId),
					exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetListEntryAssetEntryRelUpgradeProcess.class);

}