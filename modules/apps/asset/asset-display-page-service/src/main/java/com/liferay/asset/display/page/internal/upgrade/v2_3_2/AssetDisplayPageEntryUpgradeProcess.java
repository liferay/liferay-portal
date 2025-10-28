/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.internal.upgrade.v2_3_2;

import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PortalUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Jürgen Kappler
 */
public class AssetDisplayPageEntryUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		_deleteDuplicateDLAssetDisplayPages();
		_upgradeDLAssetDisplayPageTypes();
	}

	private void _deleteDuplicateDLAssetDisplayPages() throws Exception {
		long dlFileEntryClassNameId = PortalUtil.getClassNameId(
			DLFileEntryConstants.getClassName());
		long fileEntryClassNameId = PortalUtil.getClassNameId(
			FileEntry.class.getName());

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select assetDisplayPageEntry1.assetDisplayPageEntryId ",
					"from AssetDisplayPageEntry assetDisplayPageEntry1 inner ",
					"join AssetDisplayPageEntry assetDisplayPageEntry2 on ",
					"assetDisplayPageEntry1.groupId = assetDisplayPageEntry2.",
					"groupId and assetDisplayPageEntry2.classNameId = ? and ",
					"assetDisplayPageEntry1.classPK = assetDisplayPageEntry2.",
					"classPK where assetDisplayPageEntry1.classNameId = ?"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"delete from AssetDisplayPageEntry where " +
						"assetDisplayPageEntryId = ?")) {

			preparedStatement1.setLong(1, fileEntryClassNameId);
			preparedStatement1.setLong(2, dlFileEntryClassNameId);

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					preparedStatement2.setLong(
						1, resultSet.getLong("assetDisplayPageEntryId"));

					preparedStatement2.addBatch();
				}

				preparedStatement2.executeBatch();
			}
		}
	}

	private void _upgradeDLAssetDisplayPageTypes() throws Exception {
		long dlFileEntryClassNameId = PortalUtil.getClassNameId(
			DLFileEntryConstants.getClassName());
		long fileEntryClassNameId = PortalUtil.getClassNameId(
			FileEntry.class.getName());

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update AssetDisplayPageEntry set classNameId = ? where " +
					"classNameId = ?")) {

			preparedStatement.setLong(1, fileEntryClassNameId);
			preparedStatement.setLong(2, dlFileEntryClassNameId);

			preparedStatement.executeUpdate();
		}
	}

}