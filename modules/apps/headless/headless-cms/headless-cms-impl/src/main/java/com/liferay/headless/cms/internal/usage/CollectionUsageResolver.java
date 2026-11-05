/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.usage;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.list.asset.entry.provider.AssetListAssetEntryProvider;
import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntryAssetEntryRel;
import com.liferay.asset.list.model.AssetListEntrySegmentsEntryRel;
import com.liferay.asset.list.model.AssetListEntryUsageTable;
import com.liferay.asset.list.service.AssetListEntryAssetEntryRelLocalService;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalService;
import com.liferay.asset.list.service.AssetListEntryUsageLocalService;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.headless.cms.internal.util.CMSSearchUtil;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.NestedQuery;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.model.SegmentsExperienceTable;

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
		AssetListAssetEntryProvider assetListAssetEntryProvider,
		AssetListEntryAssetEntryRelLocalService
			assetListEntryAssetEntryRelLocalService,
		AssetListEntryLocalService assetListEntryLocalService,
		AssetListEntrySegmentsEntryRelLocalService
			assetListEntrySegmentsEntryRelLocalService,
		AssetListEntryUsageLocalService assetListEntryUsageLocalService,
		DepotEntryLocalService depotEntryLocalService,
		LayoutLocalService layoutLocalService,
		LayoutPageTemplateEntryLocalService layoutPageTemplateEntryLocalService,
		LayoutPageTemplateStructureLocalService
			layoutPageTemplateStructureLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectFieldLocalService objectFieldLocalService, Portal portal,
		PortletPreferencesLocalService portletPreferencesLocalService) {

		_assetEntryLocalService = assetEntryLocalService;
		_assetListAssetEntryProvider = assetListAssetEntryProvider;
		_assetListEntryAssetEntryRelLocalService =
			assetListEntryAssetEntryRelLocalService;
		_assetListEntryLocalService = assetListEntryLocalService;
		_assetListEntrySegmentsEntryRelLocalService =
			assetListEntrySegmentsEntryRelLocalService;
		_assetListEntryUsageLocalService = assetListEntryUsageLocalService;
		_depotEntryLocalService = depotEntryLocalService;
		_layoutLocalService = layoutLocalService;
		_layoutPageTemplateEntryLocalService =
			layoutPageTemplateEntryLocalService;
		_layoutPageTemplateStructureLocalService =
			layoutPageTemplateStructureLocalService;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectFieldLocalService = objectFieldLocalService;
		_portal = portal;
		_portletPreferencesLocalService = portletPreferencesLocalService;
	}

	public CollectionUsages getCollectionUsages(long companyId) {
		CollectionUsages collectionUsages = new CollectionUsages();

		long assetListEntryClassNameId = _portal.getClassNameId(
			AssetListEntry.class);
		long portletClassNameId = _portal.getClassNameId(Portlet.class);

		List<Object[]> assetListEntryUsageObjectsList =
			_assetListEntryUsageLocalService.dslQuery(
				DSLQueryFactoryUtil.select(
					AssetListEntryUsageTable.INSTANCE.groupId,
					AssetListEntryUsageTable.INSTANCE.classNameId,
					AssetListEntryUsageTable.INSTANCE.containerKey,
					AssetListEntryUsageTable.INSTANCE.containerType,
					AssetListEntryUsageTable.INSTANCE.key,
					AssetListEntryUsageTable.INSTANCE.plid
				).from(
					AssetListEntryUsageTable.INSTANCE
				).where(
					AssetListEntryUsageTable.INSTANCE.companyId.eq(
						companyId
					).and(
						AssetListEntryUsageTable.INSTANCE.classNameId.in(
							new Long[] {
								assetListEntryClassNameId,
								_portal.getClassNameId(
									InfoCollectionProvider.class)
							})
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
			collectionUsages, companyId);

		for (Object[] row : assetListEntryUsageObjectsList) {
			long groupId = (Long)row[0];
			long classNameId = (Long)row[1];
			String containerKey = (String)row[2];
			long containerType = (Long)row[3];
			String key = (String)row[4];
			long plid = (Long)row[5];

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

				if (classNameId == assetListEntryClassNameId) {
					_addObjectEntryIdsOrBooleanQueries(key, resolutionContext);
				}
				else {
					JSONObject collectionJSONObject =
						resolutionContext.getCollectionJSONObject(
							groupId, containerKey, layout.getPlid());

					if (collectionJSONObject != null) {
						_addQuery(
							collectionJSONObject.getJSONObject("config"),
							groupId, key, resolutionContext);
					}
				}
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}
		}

		List<Object[]> segmentsExperienceObjectsList =
			_assetListEntryUsageLocalService.dslQuery(
				DSLQueryFactoryUtil.select(
					SegmentsExperienceTable.INSTANCE.segmentsExperienceId,
					SegmentsExperienceTable.INSTANCE.groupId,
					SegmentsExperienceTable.INSTANCE.plid
				).from(
					SegmentsExperienceTable.INSTANCE
				).where(
					SegmentsExperienceTable.INSTANCE.companyId.eq(
						companyId
					).and(
						SegmentsExperienceTable.INSTANCE.segmentsExperienceKey.
							neq(SegmentsExperienceConstants.KEY_DEFAULT)
					).and(
						SegmentsExperienceTable.INSTANCE.active.eq(true)
					)
				));

		for (Object[] row : segmentsExperienceObjectsList) {
			long segmentsExperienceId = (Long)row[0];
			long groupId = (Long)row[1];
			long plid = (Long)row[2];

			try {
				Layout layout = resolutionContext.getLayout(plid);

				if ((layout == null) || !_isPublished(layout)) {
					continue;
				}

				Set<Long> connectedDepotGroupIds =
					resolutionContext.getConnectedDepotGroupIds(groupId);

				if (connectedDepotGroupIds.isEmpty()) {
					continue;
				}

				LayoutPageTemplateStructure layoutPageTemplateStructure =
					_layoutPageTemplateStructureLocalService.
						fetchLayoutPageTemplateStructure(groupId, plid);

				if (layoutPageTemplateStructure == null) {
					continue;
				}

				LayoutStructure layoutStructure = LayoutStructure.of(
					layoutPageTemplateStructure.getData(segmentsExperienceId));

				for (LayoutStructureItem layoutStructureItem :
						layoutStructure.
							getCollectionStyledLayoutStructureItems()) {

					if (layoutStructure.isItemMarkedForDeletion(
							layoutStructureItem.getItemId())) {

						continue;
					}

					CollectionStyledLayoutStructureItem
						collectionStyledLayoutStructureItem =
							(CollectionStyledLayoutStructureItem)
								layoutStructureItem;

					JSONObject collectionJSONObject =
						collectionStyledLayoutStructureItem.
							getCollectionJSONObject();

					if (collectionJSONObject == null) {
						continue;
					}

					if (collectionJSONObject.has("classPK")) {
						_addObjectEntryIdsOrBooleanQueries(
							collectionJSONObject.getString("classPK"),
							resolutionContext);
					}

					if (!collectionJSONObject.has("key")) {
						continue;
					}

					_addQuery(
						collectionJSONObject.getJSONObject("config"), groupId,
						collectionJSONObject.getString("key"),
						resolutionContext);
				}
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}
		}

		return collectionUsages;
	}

	private void _addBooleanQueries(
			AssetListEntry assetListEntry, CollectionUsages collectionUsages)
		throws Exception {

		List<AssetListEntrySegmentsEntryRel> assetListEntrySegmentsEntryRels =
			_assetListEntrySegmentsEntryRelLocalService.
				getAssetListEntrySegmentsEntryRels(
					assetListEntry.getAssetListEntryId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

		long[] segmentsEntryIds = {SegmentsEntryConstants.ID_DEFAULT};

		if (!assetListEntrySegmentsEntryRels.isEmpty()) {
			segmentsEntryIds = ListUtil.toLongArray(
				assetListEntrySegmentsEntryRels,
				AssetListEntrySegmentsEntryRel::getSegmentsEntryId);
		}

		for (long segmentsEntryId : segmentsEntryIds) {
			String booleanQueryKey = StringBundler.concat(
				assetListEntry.getGroupId(), StringPool.POUND,
				assetListEntry.getAssetEntryType(), StringPool.POUND,
				assetListEntry.getAssetEntrySubtype(), StringPool.POUND,
				assetListEntry.getTypeSettings(segmentsEntryId));

			if (collectionUsages.hasBooleanQuery(booleanQueryKey)) {
				continue;
			}

			AssetEntryQuery assetEntryQuery =
				_assetListAssetEntryProvider.getAssetEntryQuery(
					assetListEntry, new long[] {segmentsEntryId},
					StringPool.BLANK);

			if (ArrayUtil.isEmpty(assetEntryQuery.getClassNameIds())) {
				continue;
			}

			collectionUsages.addBooleanQuery(
				_assetListAssetEntryProvider.getBooleanQuery(
					assetEntryQuery, assetListEntry, segmentsEntryId),
				assetEntryQuery.getGroupIds(), booleanQueryKey);
		}
	}

	private void _addObjectEntryIds(
		AssetListEntry assetListEntry, CollectionUsages collectionUsages) {

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

	private void _addObjectEntryIdsOrBooleanQueries(
			String key, ResolutionContext resolutionContext)
		throws Exception {

		Set<String> resolutionKeys = resolutionContext.getResolutionKeys();

		if (!resolutionKeys.add(key)) {
			return;
		}

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.fetchAssetListEntry(
				GetterUtil.getLong(key));

		if (assetListEntry == null) {
			return;
		}

		CollectionUsages collectionUsages =
			resolutionContext.getCollectionUsages();

		if (assetListEntry.getType() ==
				AssetListEntryTypeConstants.TYPE_MANUAL) {

			_addObjectEntryIds(assetListEntry, collectionUsages);
		}
		else {
			_addBooleanQueries(assetListEntry, collectionUsages);
		}
	}

	private void _addQuery(
			JSONObject configJSONObject, long groupId, String key,
			ResolutionContext resolutionContext)
		throws Exception {

		if (!StringUtil.startsWith(
				key,
				ObjectDefinitionConstants.
					CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION)) {

			return;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				resolutionContext.getCompanyId(), key);

		if ((objectDefinition == null) || !objectDefinition.isCMS()) {
			return;
		}

		Set<String> resolutionKeys = resolutionContext.getResolutionKeys();

		if (!resolutionKeys.add(
				StringBundler.concat(
					groupId, StringPool.POUND, key, StringPool.POUND,
					configJSONObject))) {

			return;
		}

		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		booleanQuery.addFilterQueryClauses(
			QueriesUtil.term(Field.ENTRY_CLASS_NAME, key),
			CMSSearchUtil.getTermsQuery(
				Field.GROUP_ID,
				ArrayUtil.toStringArray(
					resolutionContext.getConnectedDepotGroupIds(groupId))),
			QueriesUtil.term(Field.STATUS, WorkflowConstants.STATUS_APPROVED));

		if (configJSONObject != null) {
			for (String fieldName : configJSONObject.keySet()) {
				JSONArray fieldValuesJSONArray = configJSONObject.getJSONArray(
					fieldName);

				String fieldValue = null;

				if (fieldValuesJSONArray == null) {
					fieldValue = configJSONObject.getString(fieldName);
				}
				else {
					fieldValue = fieldValuesJSONArray.getString(0);
				}

				if (Validator.isNull(fieldValue)) {
					continue;
				}

				ObjectField objectField =
					_objectFieldLocalService.fetchObjectField(
						objectDefinition.getObjectDefinitionId(), fieldName);

				if (objectField == null) {
					return;
				}

				if (objectField.isIndexedAsKeyword()) {
					booleanQuery.addFilterQueryClauses(
						_getNestedQuery(
							fieldName, "value_keyword",
							StringUtil.lowerCase(fieldValue)));
				}
				else if (Objects.equals(
							objectField.getBusinessType(),
							ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN)) {

					booleanQuery.addFilterQueryClauses(
						_getNestedQuery(
							fieldName, "value_boolean",
							GetterUtil.getBoolean(fieldValue)));
				}
				else if (Objects.equals(
							objectField.getBusinessType(),
							ObjectFieldConstants.BUSINESS_TYPE_PICKLIST)) {

					booleanQuery.addFilterQueryClauses(
						_getNestedQuery(
							fieldName, "value_keyword_lowercase", fieldValue));
				}
				else {
					return;
				}
			}
		}

		CollectionUsages collectionUsages =
			resolutionContext.getCollectionUsages();

		collectionUsages.addQuery(booleanQuery);
	}

	private NestedQuery _getNestedQuery(
		String fieldName, String valueFieldName, Object value) {

		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		booleanQuery.addFilterQueryClauses(
			QueriesUtil.term("nestedFieldArray.fieldName", fieldName),
			QueriesUtil.term("nestedFieldArray." + valueFieldName, value));

		return QueriesUtil.nested("nestedFieldArray", booleanQuery);
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
	private final AssetListAssetEntryProvider _assetListAssetEntryProvider;
	private final AssetListEntryAssetEntryRelLocalService
		_assetListEntryAssetEntryRelLocalService;
	private final AssetListEntryLocalService _assetListEntryLocalService;
	private final AssetListEntrySegmentsEntryRelLocalService
		_assetListEntrySegmentsEntryRelLocalService;
	private final AssetListEntryUsageLocalService
		_assetListEntryUsageLocalService;
	private final DepotEntryLocalService _depotEntryLocalService;
	private final LayoutLocalService _layoutLocalService;
	private final LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;
	private final LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final Portal _portal;
	private final PortletPreferencesLocalService
		_portletPreferencesLocalService;

	private class ResolutionContext {

		public JSONObject getCollectionJSONObject(
			long groupId, String itemId, long plid) {

			LayoutStructure layoutStructure = _layoutStructures.get(plid);

			if (layoutStructure == null) {
				LayoutPageTemplateStructure layoutPageTemplateStructure =
					_layoutPageTemplateStructureLocalService.
						fetchLayoutPageTemplateStructure(groupId, plid);

				if (layoutPageTemplateStructure == null) {
					return null;
				}

				layoutStructure = LayoutStructure.of(
					layoutPageTemplateStructure.
						getDefaultSegmentsExperienceData());

				_layoutStructures.put(plid, layoutStructure);
			}

			LayoutStructureItem layoutStructureItem =
				layoutStructure.getLayoutStructureItem(itemId);

			if (!(layoutStructureItem instanceof
					CollectionStyledLayoutStructureItem)) {

				return null;
			}

			CollectionStyledLayoutStructureItem
				collectionStyledLayoutStructureItem =
					(CollectionStyledLayoutStructureItem)layoutStructureItem;

			return collectionStyledLayoutStructureItem.
				getCollectionJSONObject();
		}

		public CollectionUsages getCollectionUsages() {
			return _collectionUsages;
		}

		public long getCompanyId() {
			return _companyId;
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

		private ResolutionContext(
			CollectionUsages collectionUsages, long companyId) {

			_collectionUsages = collectionUsages;
			_companyId = companyId;
		}

		private final CollectionUsages _collectionUsages;
		private final long _companyId;
		private final Map<Long, Set<Long>> _connectedDepotGroupIdsMap =
			new HashMap<>();
		private final Map<Long, LayoutStructure> _layoutStructures =
			new HashMap<>();
		private final Map<Long, Layout> _layouts = new HashMap<>();
		private final Set<String> _resolutionKeys = new HashSet<>();

	}

}