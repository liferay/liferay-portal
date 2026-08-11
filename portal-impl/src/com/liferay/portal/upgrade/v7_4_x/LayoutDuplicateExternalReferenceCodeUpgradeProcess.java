/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Georgel Pop
 */
public class LayoutDuplicateExternalReferenceCodeUpgradeProcess
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		Map<Long, Set<String>> externalReferenceCodesMap =
			_getExternalReferenceCodesMap();

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select distinct Layout1.externalReferenceCode, ",
					"Layout1.plid, Layout1.groupId from Layout Layout1 inner ",
					"join (select ctCollectionId, externalReferenceCode, ",
					"groupId, max(plid) maxPlid from Layout group by ",
					"ctCollectionId, externalReferenceCode, groupId having ",
					"count(*) > 1) Layout2 on Layout2.ctCollectionId = ",
					"Layout1.ctCollectionId and Layout2.externalReferenceCode ",
					"= Layout1.externalReferenceCode and Layout2.groupId = ",
					"Layout1.groupId where Layout1.plid != Layout2.maxPlid ",
					"order by Layout1.plid"));

			ResultSet resultSet = preparedStatement1.executeQuery();

			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update Layout set externalReferenceCode = ? where " +
						"externalReferenceCode = ? and plid = ?")) {

			while (resultSet.next()) {
				long plid = resultSet.getLong("plid");

				Set<String> externalReferenceCodes =
					externalReferenceCodesMap.get(resultSet.getLong("groupId"));

				String externalReferenceCode = String.valueOf(plid);

				while (externalReferenceCodes.contains(externalReferenceCode)) {
					externalReferenceCode = String.valueOf(
						increment(Layout.class.getName()));
				}

				externalReferenceCodes.add(externalReferenceCode);

				preparedStatement2.setString(1, externalReferenceCode);

				preparedStatement2.setString(
					2, resultSet.getString("externalReferenceCode"));
				preparedStatement2.setLong(3, plid);

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	private Map<Long, Set<String>> _getExternalReferenceCodesMap()
		throws Exception {

		Map<Long, Set<String>> externalReferenceCodesMap = new HashMap<>();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select Layout1.externalReferenceCode, Layout1.groupId ",
					"from Layout Layout1 inner join (select distinct groupId ",
					"from Layout group by ctCollectionId, ",
					"externalReferenceCode, groupId having count(*) > 1) ",
					"Layout2 on Layout2.groupId = Layout1.groupId"));

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				Set<String> externalReferenceCodes =
					externalReferenceCodesMap.computeIfAbsent(
						resultSet.getLong("groupId"), key -> new HashSet<>());

				externalReferenceCodes.add(
					resultSet.getString("externalReferenceCode"));
			}
		}

		return externalReferenceCodesMap;
	}

}