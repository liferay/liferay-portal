/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.upgrade;

import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.sql.Timestamp;

/**
 * @author Jürgen Kappler
 */
public abstract class BaseUpgradeAssetDisplayPageEntry extends UpgradeProcess {

	protected void cleanAssetDisplayPageEntry(long[] classNameIds)
		throws Exception {

		runSQL(
			StringBundler.concat(
				"delete from AssetDisplayPageEntry where classNameId in (",
				StringUtil.merge(classNameIds), ") and type_ = ",
				AssetDisplayPageConstants.TYPE_DEFAULT));
	}

	protected void upgradeAssetDisplayPageTypes(
			String tableName, String pkColumnName, String modelClassName)
		throws Exception {

		long modelClassNameId = PortalUtil.getClassNameId(modelClassName);

		processConcurrently(
			StringBundler.concat(
				"select distinct ", tableName, ".groupId, ", tableName,
				".companyId, ", tableName, StringPool.PERIOD, pkColumnName,
				" from ", tableName, " left join AssetDisplayPageEntry on ",
				"AssetDisplayPageEntry.classPK = ", tableName,
				StringPool.PERIOD, pkColumnName,
				" and AssetDisplayPageEntry.classNameId = ", modelClassNameId,
				" and AssetDisplayPageEntry.groupId = ", tableName,
				".groupId where AssetDisplayPageEntry.classPK is null"),
			StringBundler.concat(
				"insert into AssetDisplayPageEntry (uuid_, ",
				"assetDisplayPageEntryId, groupId, companyId, userId, ",
				"userName, createDate, modifiedDate, classNameId, classPK,",
				"layoutPageTemplateEntryId, type_, plid) values(?, ?, ?, ?, ",
				"?, ?, ?, ?, ?, ?, ?, ?, ?)"),
			resultSet -> new Object[] {
				resultSet.getLong(1), resultSet.getLong(2), resultSet.getLong(3)
			},
			(values, preparedStatement) -> {
				long groupId = (long)values[0];
				long companyId = (long)values[1];
				long pkColumnValue = (long)values[2];

				Timestamp now = new Timestamp(System.currentTimeMillis());

				preparedStatement.setString(1, PortalUUIDUtil.generate());
				preparedStatement.setLong(2, increment());
				preparedStatement.setLong(3, groupId);
				preparedStatement.setLong(4, companyId);
				preparedStatement.setLong(5, 0L);
				preparedStatement.setString(6, null);
				preparedStatement.setTimestamp(7, now);
				preparedStatement.setTimestamp(8, now);
				preparedStatement.setLong(9, modelClassNameId);
				preparedStatement.setLong(10, pkColumnValue);
				preparedStatement.setLong(11, 0L);
				preparedStatement.setLong(
					12, AssetDisplayPageConstants.TYPE_NONE);
				preparedStatement.setLong(13, 0L);

				preparedStatement.addBatch();
			},
			null);

		cleanAssetDisplayPageEntry(new long[] {modelClassNameId});
	}

}