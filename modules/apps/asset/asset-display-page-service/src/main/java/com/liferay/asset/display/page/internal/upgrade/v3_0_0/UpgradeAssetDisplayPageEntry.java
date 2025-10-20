/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.internal.upgrade.v3_0_0;

import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.upgrade.BaseUpgradeAssetDisplayPageEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.sql.Timestamp;

/**
 * @author Jürgen Kappler
 */
public class UpgradeAssetDisplayPageEntry
	extends BaseUpgradeAssetDisplayPageEntry {

	@Override
	protected void doUpgrade() throws Exception {
		try (SafeCloseable safeCloseable = addTemporaryIndex(
				"AssetDisplayPageEntry", false, "classNameId", "type_")) {

			upgradeAssetDisplayPageTypes(
				"BlogsEntry", "entryId", "com.liferay.blogs.model.BlogsEntry");

			upgradeAssetDisplayPageTypes(
				"JournalArticle", "resourcePrimKey",
				"com.liferay.journal.model.JournalArticle");

			_upgradeDLAssetDisplayPageTypes();
		}
	}

	private void _upgradeDLAssetDisplayPageTypes() throws Exception {
		long dlFileEntryClassNameId = PortalUtil.getClassNameId(
			DLFileEntryConstants.getClassName());

		long fileEntryClassNameId = PortalUtil.getClassNameId(
			FileEntry.class.getName());

		processConcurrently(
			StringBundler.concat(
				"select distinct DLFileEntry.ctCollectionId, ",
				"DLFileEntry.groupId, DLFileEntry.companyId, ",
				"DLFileEntry.userId, DLFileEntry.userName, ",
				"DLFileEntry.fileEntryId from DLFileEntry left join ",
				"AssetDisplayPageEntry on AssetDisplayPageEntry.classPK = ",
				"DLFileEntry.fileEntryId and ",
				"AssetDisplayPageEntry.classNameId in (",
				dlFileEntryClassNameId, ", ", fileEntryClassNameId,
				") and AssetDisplayPageEntry.ctCollectionId = ",
				"DLFileEntry.ctCollectionId where ",
				"AssetDisplayPageEntry.classPK is null"),
			StringBundler.concat(
				"insert into AssetDisplayPageEntry (ctCollectionId, uuid_, ",
				"assetDisplayPageEntryId, groupId, companyId, userId, ",
				"userName, createDate, modifiedDate, classNameId, classPK, ",
				"layoutPageTemplateEntryId, type_, plid) values(?, ?, ?, ?, ",
				"?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"),
			resultSet -> new Object[] {
				resultSet.getLong(1), resultSet.getLong(2),
				resultSet.getLong(3), resultSet.getLong(4),
				resultSet.getString(5), resultSet.getLong(6)
			},
			(values, preparedStatement) -> {
				long ctCollectionId = (long)values[0];
				long groupId = (long)values[1];
				long companyId = (long)values[2];
				long userId = (long)values[3];
				String userName = (String)values[4];
				long fileEntryId = (long)values[5];

				Timestamp now = new Timestamp(System.currentTimeMillis());

				preparedStatement.setLong(1, ctCollectionId);
				preparedStatement.setString(2, PortalUUIDUtil.generate());
				preparedStatement.setLong(3, increment());
				preparedStatement.setLong(4, groupId);
				preparedStatement.setLong(5, companyId);
				preparedStatement.setLong(6, userId);
				preparedStatement.setString(7, userName);
				preparedStatement.setTimestamp(8, now);
				preparedStatement.setTimestamp(9, now);
				preparedStatement.setLong(10, fileEntryClassNameId);
				preparedStatement.setLong(11, fileEntryId);
				preparedStatement.setLong(12, 0L);
				preparedStatement.setLong(
					13, AssetDisplayPageConstants.TYPE_NONE);
				preparedStatement.setLong(14, 0L);

				preparedStatement.addBatch();
			},
			null);

		cleanAssetDisplayPageEntry(
			new long[] {dlFileEntryClassNameId, fileEntryClassNameId});
	}

}