/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v1_1_3;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Pedro Queiroz
 */
public class DDMStorageLinkUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select distinct DDMStorageLink.structureId, ",
					"TEMP_TABLE.structureVersionId from DDMStorageLink inner ",
					"join (select structureId, max(structureVersionId) as ",
					"structureVersionId from DDMStructureVersion group by ",
					"DDMStructureVersion.structureId) TEMP_TABLE on ",
					"DDMStorageLink.structureId = TEMP_TABLE.structureId"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMStorageLink set structureVersionId = ? where " +
						"structureId = ?");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				long ddmStructureVersionId = resultSet.getLong(
					"structureVersionId");

				if (ddmStructureVersionId <= 0) {
					continue;
				}

				preparedStatement2.setLong(1, ddmStructureVersionId);
				preparedStatement2.setLong(2, resultSet.getLong("structureId"));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.addColumns(
				"DDMStorageLink", "structureVersionId LONG")
		};
	}

}