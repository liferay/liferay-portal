/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.usage;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntryAssetEntryRel;
import com.liferay.asset.list.model.AssetListEntryUsageTable;
import com.liferay.asset.list.service.AssetListEntryAssetEntryRelLocalService;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.list.service.AssetListEntryUsageLocalService;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Mikel Lorza
 */
public class CollectionUsageResolver {

	public CollectionUsageResolver(
		AssetEntryLocalService assetEntryLocalService,
		AssetListEntryAssetEntryRelLocalService
			assetListEntryAssetEntryRelLocalService,
		AssetListEntryLocalService assetListEntryLocalService,
		AssetListEntryUsageLocalService assetListEntryUsageLocalService,
		DepotEntryLocalService depotEntryLocalService,
		LayoutLocalService layoutLocalService,
		LayoutPageTemplateEntryLocalService layoutPageTemplateEntryLocalService,
		Portal portal,
		PortletPreferencesLocalService portletPreferencesLocalService) {

		_assetEntryLocalService = assetEntryLocalService;
		_assetListEntryAssetEntryRelLocalService =
			assetListEntryAssetEntryRelLocalService;
		_assetListEntryLocalService = assetListEntryLocalService;
		_assetListEntryUsageLocalService = assetListEntryUsageLocalService;
		_depotEntryLocalService = depotEntryLocalService;
		_layoutLocalService = layoutLocalService;
		_layoutPageTemplateEntryLocalService =
			layoutPageTemplateEntryLocalService;
		_portal = portal;
		_portletPreferencesLocalService = portletPreferencesLocalService;
	}

	public CollectionUsages getCollectionUsages(long companyId) {
		CollectionUsages collectionUsages = new CollectionUsages();

		long portletClassNameId = _portal.getClassNameId(Portlet.class);

		List<Object[]> assetListEntryUsageObjectsList =
			_assetListEntryUsageLocalService.dslQuery(
				DSLQueryFactoryUtil.select(
					AssetListEntryUsageTable.INSTANCE.groupId,
					AssetListEntryUsageTable.INSTANCE.plid,
					AssetListEntryUsageTable.INSTANCE.key,
					AssetListEntryUsageTable.INSTANCE.containerKey,
					AssetListEntryUsageTable.INSTANCE.containerType
				).from(
					AssetListEntryUsageTable.INSTANCE
				).where(
					AssetListEntryUsageTable.INSTANCE.companyId.eq(
						companyId
					).and(
						AssetListEntryUsageTable.INSTANCE.classNameId.eq(
							_portal.getClassNameId(AssetListEntry.class))
					).and(
						AssetListEntryUsageTable.INSTANCE.containerType.in(
							new Long[] {
								_portal.getClassNameId(
									CollectionStyledLayoutStructureItem.class),
								portletClassNameId
							})
					)
				));

		ResolutionContext resolutionContext = new ResolutionContext(
			collectionUsages);

		for (Object[] row : assetListEntryUsageObjectsList) {
			long groupId = (Long)row[0];
			long plid = (Long)row[1];
			String key = (String)row[2];
			String containerKey = (String)row[3];
			long containerType = (Long)row[4];

			try {
				Layout layout = resolutionContext.getLayout(plid);

				if (containerType == portletClassNameId) {
					layout = _getPortletLayout(
						layout, containerKey, resolutionContext);
				}

				if ((layout == null) || !_isPublished(layout)) {
					continue;
				}

				Set<Long> connectedDepotGroupIds =
					resolutionContext.getConnectedDepotGroupIds(groupId);

				if (connectedDepotGroupIds.isEmpty()) {
					continue;
				}

				_addObjectEntryIds(key, resolutionContext);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}
		}

		return collectionUsages;
	}

	private void _addObjectEntryIds(
		String key, ResolutionContext resolutionContext) {

		Set<String> resolutionKeys = resolutionContext.getResolutionKeys();

		if (!resolutionKeys.add(key)) {
			return;
		}

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.fetchAssetListEntry(
				GetterUtil.getLong(key));

		if ((assetListEntry == null) ||
			(assetListEntry.getType() !=
				AssetListEntryTypeConstants.TYPE_MANUAL)) {

			return;
		}

		CollectionUsages collectionUsages =
			resolutionContext.getCollectionUsages();

		for (AssetListEntryAssetEntryRel assetListEntryAssetEntryRel :
				_assetListEntryAssetEntryRelLocalService.
					getAssetListEntryAssetEntryRels(
						assetListEntry.getAssetListEntryId(), QueryUtil.ALL_POS,
						QueryUtil.ALL_POS)) {

			AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
				assetListEntryAssetEntryRel.getAssetEntryId());

			if ((assetEntry == null) ||
				!StringUtil.startsWith(
					assetEntry.getClassName(),
					ObjectDefinitionConstants.
						CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION)) {

				continue;
			}

			collectionUsages.addObjectEntryId(assetEntry.getClassPK());
		}
	}

	private Layout _getPortletLayout(
		Layout layout, String portletId, ResolutionContext resolutionContext) {

		if (layout == null) {
			return null;
		}

		if (layout.isDraftLayout()) {
			layout = resolutionContext.getLayout(layout.getClassPK());

			if (layout == null) {
				return null;
			}
		}

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.fetchPortletPreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
				portletId);

		if (portletPreferences == null) {
			return null;
		}

		return layout;
	}

	private boolean _isPublished(Layout layout) {
		if (layout.isDraftLayout()) {
			return false;
		}

		if (Objects.equals(
				layout.getType(), LayoutConstants.TYPE_ASSET_DISPLAY)) {

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

			if ((layoutPageTemplateEntry != null) &&
				(layoutPageTemplateEntry.getStatus() ==
					WorkflowConstants.STATUS_APPROVED)) {

				return true;
			}

			return false;
		}

		if ((layout.isTypeContent() && layout.isPublished()) ||
			layout.isTypePortlet()) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CollectionUsageResolver.class);

	private final AssetEntryLocalService _assetEntryLocalService;
	private final AssetListEntryAssetEntryRelLocalService
		_assetListEntryAssetEntryRelLocalService;
	private final AssetListEntryLocalService _assetListEntryLocalService;
	private final AssetListEntryUsageLocalService
		_assetListEntryUsageLocalService;
	private final DepotEntryLocalService _depotEntryLocalService;
	private final LayoutLocalService _layoutLocalService;
	private final LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;
	private final Portal _portal;
	private final PortletPreferencesLocalService
		_portletPreferencesLocalService;

	private class ResolutionContext {

		public CollectionUsages getCollectionUsages() {
			return _collectionUsages;
		}

		public Set<Long> getConnectedDepotGroupIds(long groupId)
			throws Exception {

			Set<Long> connectedDepotGroupIds = _connectedDepotGroupIdsMap.get(
				groupId);

			if (connectedDepotGroupIds != null) {
				return connectedDepotGroupIds;
			}

			connectedDepotGroupIds = new HashSet<>(
				TransformUtil.transform(
					_depotEntryLocalService.getGroupConnectedDepotEntries(
						groupId, DepotConstants.TYPE_SPACE, QueryUtil.ALL_POS,
						QueryUtil.ALL_POS),
					DepotEntry::getGroupId));

			_connectedDepotGroupIdsMap.put(groupId, connectedDepotGroupIds);

			return connectedDepotGroupIds;
		}

		public Layout getLayout(long plid) {
			return _layouts.computeIfAbsent(
				plid, _layoutLocalService::fetchLayout);
		}

		public Set<String> getResolutionKeys() {
			return _resolutionKeys;
		}

		private ResolutionContext(CollectionUsages collectionUsages) {
			_collectionUsages = collectionUsages;
		}

		private final CollectionUsages _collectionUsages;
		private final Map<Long, Set<Long>> _connectedDepotGroupIdsMap =
			new HashMap<>();
		private final Map<Long, Layout> _layouts = new HashMap<>();
		private final Set<String> _resolutionKeys = new HashSet<>();

	}

}