/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.internal.upgrade.v1_9_0;

import com.liferay.asset.list.constants.AssetListEntryUsageConstants;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * @author Lourdes Fernández Besada
 */
public class AssetListEntryUsageUpgradeProcess extends UpgradeProcess {

	public AssetListEntryUsageUpgradeProcess(
		LayoutLocalService layoutLocalService,
		LayoutPageTemplateEntryLocalService layoutPageTemplateEntryLocalService,
		LayoutPageTemplateStructureLocalService
			layoutPageTemplateStructureLocalService,
		Portal portal) {

		_layoutLocalService = layoutLocalService;
		_layoutPageTemplateEntryLocalService =
			layoutPageTemplateEntryLocalService;
		_layoutPageTemplateStructureLocalService =
			layoutPageTemplateStructureLocalService;
		_portal = portal;
	}

	@Override
	protected void doUpgrade() throws Exception {
		long layoutPageTemplateStructureClassNameId = _portal.getClassNameId(
			LayoutPageTemplateStructure.class);

		try (PreparedStatement selectPreparedStatement =
				connection.prepareStatement(
					"select distinct containerKey from AssetListEntryUsage " +
						"where containerType = ?");
			PreparedStatement insertPreparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					StringBundler.concat(
						"insert into AssetListEntryUsage (uuid_, ",
						"assetListEntryUsageId, groupId, companyId, userId, ",
						"userName, createDate, modifiedDate, ",
						"assetListEntryId, classNameId, classPK, ",
						"containerKey, containerType, key_, plid, type_) ",
						"values(?, ?, ?, ?, ?, ?, ?, ?, 0, ?, 0, ?, ?, ?, ?, ",
						"?)"))) {

			selectPreparedStatement.setLong(
				1, layoutPageTemplateStructureClassNameId);

			try (ResultSet resultSet = selectPreparedStatement.executeQuery()) {
				while (resultSet.next()) {
					LayoutPageTemplateStructure layoutPageTemplateStructure =
						_layoutPageTemplateStructureLocalService.
							fetchLayoutPageTemplateStructure(
								GetterUtil.getLong(
									resultSet.getString("containerKey")));

					if (layoutPageTemplateStructure == null) {
						continue;
					}

					LayoutStructure layoutStructure = LayoutStructure.of(
						layoutPageTemplateStructure.
							getDefaultSegmentsExperienceData());

					long type = _getType(layoutPageTemplateStructure.getPlid());

					for (LayoutStructureItem layoutStructureItem :
							layoutStructure.getLayoutStructureItems()) {

						if (!(layoutStructureItem instanceof
								CollectionStyledLayoutStructureItem)) {

							continue;
						}

						CollectionStyledLayoutStructureItem
							collectionStyledLayoutStructureItem =
								(CollectionStyledLayoutStructureItem)
									layoutStructureItem;

						JSONObject collectionJSONObject =
							collectionStyledLayoutStructureItem.
								getCollectionJSONObject();

						String itemId = layoutStructureItem.getItemId();

						if (!_isMapped(
								collectionJSONObject, itemId,
								layoutStructure)) {

							continue;
						}

						if (collectionJSONObject.has("classPK")) {
							_addBatch(
								_portal.getClassNameId(AssetListEntry.class),
								itemId,
								collectionJSONObject.getString("classPK"),
								layoutPageTemplateStructure,
								insertPreparedStatement, type);
						}

						if (collectionJSONObject.has("key")) {
							_addBatch(
								_portal.getClassNameId(
									InfoCollectionProvider.class),
								itemId, collectionJSONObject.getString("key"),
								layoutPageTemplateStructure,
								insertPreparedStatement, type);
						}
					}
				}

				insertPreparedStatement.executeBatch();
			}
		}

		runSQL(
			"delete from AssetListEntryUsage where containerType = " +
				layoutPageTemplateStructureClassNameId);
	}

	private void _addBatch(
			long classNameId, String containerKey, String key,
			LayoutPageTemplateStructure layoutPageTemplateStructure,
			PreparedStatement preparedStatement, long type)
		throws SQLException {

		Timestamp now = new Timestamp(System.currentTimeMillis());

		preparedStatement.setString(1, PortalUUIDUtil.generate());
		preparedStatement.setLong(2, increment());
		preparedStatement.setLong(3, layoutPageTemplateStructure.getGroupId());
		preparedStatement.setLong(
			4, layoutPageTemplateStructure.getCompanyId());
		preparedStatement.setLong(5, layoutPageTemplateStructure.getUserId());
		preparedStatement.setString(
			6, layoutPageTemplateStructure.getUserName());
		preparedStatement.setTimestamp(7, now);
		preparedStatement.setTimestamp(8, now);
		preparedStatement.setLong(9, classNameId);
		preparedStatement.setString(10, containerKey);
		preparedStatement.setLong(
			11,
			_portal.getClassNameId(CollectionStyledLayoutStructureItem.class));
		preparedStatement.setString(12, key);
		preparedStatement.setLong(13, layoutPageTemplateStructure.getPlid());
		preparedStatement.setLong(14, type);

		preparedStatement.addBatch();
	}

	private int _getType(long plid) {
		if (plid <= 0) {
			return AssetListEntryUsageConstants.TYPE_DEFAULT;
		}

		Layout layout = _layoutLocalService.fetchLayout(plid);

		if (layout == null) {
			return AssetListEntryUsageConstants.TYPE_DEFAULT;
		}

		if (layout.isDraftLayout()) {
			plid = layout.getClassPK();
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(plid);

		if (layoutPageTemplateEntry == null) {
			return AssetListEntryUsageConstants.TYPE_LAYOUT;
		}

		if (layoutPageTemplateEntry.getType() ==
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE) {

			return AssetListEntryUsageConstants.TYPE_DISPLAY_PAGE_TEMPLATE;
		}

		return AssetListEntryUsageConstants.TYPE_PAGE_TEMPLATE;
	}

	private boolean _isMapped(
		JSONObject collectionJSONObject, String itemId,
		LayoutStructure layoutStructure) {

		if ((collectionJSONObject == null) ||
			(!collectionJSONObject.has("classPK") &&
			 !collectionJSONObject.has("key"))) {

			return false;
		}

		return !layoutStructure.isItemMarkedForDeletion(itemId);
	}

	private final LayoutLocalService _layoutLocalService;
	private final LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;
	private final LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;
	private final Portal _portal;

}