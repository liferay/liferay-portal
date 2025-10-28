/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.internal.upgrade.v3_1_0;

import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Jürgen Kappler
 */
public class AssetDisplayLayoutUpgradeProcess extends UpgradeProcess {

	public AssetDisplayLayoutUpgradeProcess(
		LayoutPageTemplateEntryLocalService
			layoutPageTemplateEntryLocalService) {

		_layoutPageTemplateEntryLocalService =
			layoutPageTemplateEntryLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_upgradeAssetDisplayLayouts();
	}

	private void _upgradeAssetDisplayLayouts() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select ctCollectionId, assetDisplayPageEntryId, " +
					"layoutPageTemplateEntryId from AssetDisplayPageEntry " +
						"where type_ = ? and (plid is null or plid = 0)");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update AssetDisplayPageEntry set plid = ? where " +
						"ctCollectionId = ? and assetDisplayPageEntryId = ?")) {

			preparedStatement1.setInt(
				1, AssetDisplayPageConstants.TYPE_SPECIFIC);

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					long layoutPageTemplateEntryId = resultSet.getLong(
						"layoutPageTemplateEntryId");

					LayoutPageTemplateEntry layoutPageTemplateEntry =
						_layoutPageTemplateEntryLocalService.
							fetchLayoutPageTemplateEntry(
								layoutPageTemplateEntryId);

					if (layoutPageTemplateEntry == null) {
						continue;
					}

					long plid = layoutPageTemplateEntry.getPlid();

					if (plid == LayoutConstants.DEFAULT_PLID) {
						continue;
					}

					preparedStatement2.setLong(1, plid);

					long ctCollectionId = resultSet.getLong("ctCollectionId");

					preparedStatement2.setLong(2, ctCollectionId);

					long assetDisplayPageEntryId = resultSet.getLong(
						"assetDisplayPageEntryId");

					preparedStatement2.setLong(3, assetDisplayPageEntryId);

					preparedStatement2.addBatch();
				}

				preparedStatement2.executeBatch();
			}
		}
	}

	private final LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

}