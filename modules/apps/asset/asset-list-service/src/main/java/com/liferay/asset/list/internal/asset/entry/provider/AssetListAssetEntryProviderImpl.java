/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.internal.asset.entry.provider;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.ClassType;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.list.asset.entry.provider.AssetListAssetEntryProvider;
import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.internal.configuration.AssetListConfiguration;
import com.liferay.asset.list.internal.util.AssetListFiltersUtil;
import com.liferay.asset.list.internal.util.AssetListOrderByColumnUtil;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntryAssetEntryRel;
import com.liferay.asset.list.model.AssetListEntryAssetEntryRelModel;
import com.liferay.asset.list.model.AssetListEntrySegmentsEntryRel;
import com.liferay.asset.list.model.AssetListEntrySegmentsEntryRelModel;
import com.liferay.asset.list.service.AssetListEntryAssetEntryRelLocalService;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalService;
import com.liferay.asset.list.util.comparator.AssetListEntrySegmentsEntryRelPriorityComparator;
import com.liferay.asset.util.AssetHelper;
import com.liferay.asset.util.AssetRendererFactoryClassProvider;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.util.DLFileEntryTypeUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.info.pagination.InfoPage;
import com.liferay.info.pagination.Pagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.segments.constants.SegmentsEntryConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sarai Díaz
 */
@Component(
	configurationPid = "com.liferay.asset.list.internal.configuration.AssetListConfiguration",
	service = AssetListAssetEntryProvider.class
)
public class AssetListAssetEntryProviderImpl
	implements AssetListAssetEntryProvider {

	@Override
	public InfoPage<AssetEntry> getAssetEntriesInfoPage(
		AssetListEntry assetListEntry, long[] segmentsEntryIds,
		long[][] assetCategoryIds, String[][] assetTagNames, String keywords,
		String userId, int start, int end) {

		if (Objects.equals(
				assetListEntry.getType(),
				AssetListEntryTypeConstants.TYPE_MANUAL)) {

			return _getManualAssetEntries(
				assetListEntry, segmentsEntryIds, assetCategoryIds,
				assetTagNames, keywords, start, end);
		}

		return _getDynamicAssetEntries(
			assetListEntry, segmentsEntryIds, assetCategoryIds, assetTagNames,
			keywords, userId, start, end);
	}

	@Override
	public AssetEntryQuery getAssetEntryQuery(
		AssetListEntry assetListEntry, long[] segmentsEntryIds, String userId) {

		return getAssetEntryQuery(
			assetListEntry,
			_getFirstSegmentsEntryId(assetListEntry, segmentsEntryIds), userId);
	}

	@Activate
	@Modified
	protected void activate(
			BundleContext bundleContext, Map<String, Object> properties)
		throws ConfigurationException {

		_assetListConfiguration = ConfigurableUtil.createConfigurable(
			AssetListConfiguration.class, properties);
	}

	protected AssetEntryQuery getAssetEntryQuery(
		AssetListEntry assetListEntry, long segmentsEntryId, String userId) {

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.create(
			true
		).fastLoad(
			assetListEntry.getTypeSettings(segmentsEntryId)
		).build();

		return _createAssetEntryQuery(assetListEntry, unicodeProperties);
	}

	private AssetEntryQuery _createAssetEntryQuery(
		AssetListEntry assetListEntry, UnicodeProperties unicodeProperties) {

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		_setCategoriesAndTagsAndKeywords(
			assetEntryQuery, unicodeProperties,
			_getAssetCategoryIds(unicodeProperties),
			_getAssetTagNames(unicodeProperties),
			_getKeywords(unicodeProperties));

		long[] groupIds = GetterUtil.getLongValues(
			StringUtil.split(
				unicodeProperties.getProperty("groupIds", StringPool.BLANK)));

		assetEntryQuery.setGroupIds(
			_getAssetEntryQueryGroupIds(assetListEntry.getGroupId(), groupIds));

		boolean anyAssetType = GetterUtil.getBoolean(
			unicodeProperties.getProperty("anyAssetType", null), true);
		long[] availableClassNameIds =
			AssetRendererFactoryRegistryUtil.getClassNameIds(
				assetListEntry.getCompanyId(), true);
		long[] classTypeIds = {};

		if (!anyAssetType) {
			long[] classNameIds = _getClassNameIds(
				unicodeProperties, availableClassNameIds);

			assetEntryQuery.setClassNameIds(classNameIds);

			if (classNameIds.length == 1) {
				classTypeIds = _getClassTypeIds(
					assetListEntry, unicodeProperties,
					_portal.getClassName(classNameIds[0]));

				assetEntryQuery.setClassTypeIds(classTypeIds);
			}
		}
		else {
			assetEntryQuery.setClassNameIds(availableClassNameIds);
		}

		String ddmStructureFieldName = unicodeProperties.getProperty(
			"ddmStructureFieldName");

		String ddmStructureFieldValue = unicodeProperties.getProperty(
			"ddmStructureFieldValue");

		if (Validator.isNotNull(ddmStructureFieldName) &&
			Validator.isNotNull(ddmStructureFieldValue) &&
			(classTypeIds.length == 1)) {

			DLFileEntryType dlFileEntryType =
				_dlFileEntryTypeLocalService.fetchFileEntryType(
					classTypeIds[0]);

			if (dlFileEntryType != null) {
				List<DDMStructure> ddmStructures =
					DLFileEntryTypeUtil.getDDMStructures(dlFileEntryType);

				if (!ddmStructures.isEmpty()) {
					DDMStructure ddmStructure = ddmStructures.get(0);

					assetEntryQuery.setAttribute(
						"ddmStructureFieldName",
						_ddmIndexer.encodeName(
							ddmStructure.getStructureId(),
							_getFieldReference(
								ddmStructure, ddmStructureFieldName),
							LocaleUtil.getMostRelevantLocale()));
				}
			}
			else {
				long ddmStructureId = classTypeIds[0];

				assetEntryQuery.setAttribute(
					"ddmStructureFieldName",
					_ddmIndexer.encodeName(
						ddmStructureId,
						_getFieldReference(
							ddmStructureId, ddmStructureFieldName),
						LocaleUtil.getMostRelevantLocale()));
			}

			assetEntryQuery.setAttribute(
				"ddmStructureFieldValue", ddmStructureFieldValue);
		}

		if (FeatureFlagManagerUtil.isEnabled(
				assetListEntry.getCompanyId(), "LPD-74731")) {

			String filtersJSON = unicodeProperties.getProperty("filters");

			if (Validator.isNotNull(filtersJSON)) {
				try {
					assetEntryQuery.setAttribute(
						"filters", _jsonFactory.createJSONArray(filtersJSON));
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							"Unable to parse filters: " + filtersJSON,
							exception);
					}
				}
			}
		}

		assetEntryQuery.setOrderByCol1(
			_getOrderByColumn(
				assetListEntry.getCompanyId(), "priority",
				GetterUtil.getString(
					unicodeProperties.getProperty(
						"orderByColumn1", "priority"))));

		assetEntryQuery.setOrderByCol2(
			_getOrderByColumn(
				assetListEntry.getCompanyId(), "modifiedDate",
				GetterUtil.getString(
					unicodeProperties.getProperty(
						"orderByColumn2", "modifiedDate"))));

		assetEntryQuery.setOrderByType1(
			GetterUtil.getString(
				unicodeProperties.getProperty("orderByType1", "ASC")));
		assetEntryQuery.setOrderByType2(
			GetterUtil.getString(
				unicodeProperties.getProperty("orderByType2", "ASC")));

		return assetEntryQuery;
	}

	private InfoPage<AssetEntry> _dynamicSearch(
		long companyId, long[][] assetCategoryIds,
		List<AssetEntryQuery> assetEntryQueries, String[][] assetTagNames,
		String keywords, int start, int end) {

		try {
			if (ListUtil.isEmpty(assetEntryQueries)) {
				return InfoPage.of(Collections.emptyList());
			}

			AssetEntryQuery assetEntryQuery = assetEntryQueries.get(0);

			if (assetEntryQueries.size() == 1) {
				Hits hits = _assetHelper.search(
					_getDynamicSearchContext(
						companyId, assetCategoryIds, assetEntryQuery,
						assetTagNames, keywords),
					assetEntryQuery, start, end);

				return InfoPage.of(
					_assetHelper.getAssetEntries(hits),
					Pagination.of(end, start), hits.getLength());
			}

			SearchHits searchHits = _assetHelper.search(
				_getDynamicSearchContext(
					companyId, assetCategoryIds, assetEntryQueries.get(0),
					assetTagNames, keywords),
				assetEntryQueries, start, end);

			Long totalHits = searchHits.getTotalHits();

			return InfoPage.of(
				_assetHelper.getAssetEntries(searchHits),
				Pagination.of(end, start), totalHits.intValue());
		}
		catch (Exception exception) {
			_log.error("Unable to get asset entries", exception);
		}

		return InfoPage.of(Collections.emptyList());
	}

	private long[] _filterAssetCategoryIds(long[] assetCategoryIds) {
		List<Long> filteredAssetCategoryIds = TransformUtil.transformToList(
			assetCategoryIds,
			assetCategoryId -> {
				AssetCategory category =
					_assetCategoryLocalService.fetchAssetCategory(
						assetCategoryId);

				if (category == null) {
					return null;
				}

				return assetCategoryId;
			});

		return ArrayUtil.toArray(filteredAssetCategoryIds.toArray(new Long[0]));
	}

	private long[] _getAssetCategoryIds(UnicodeProperties unicodeProperties) {
		long[] assetCategoryIds = new long[0];

		for (int i = 0; true; i++) {
			String[] queryValues = StringUtil.split(
				unicodeProperties.getProperty("queryValues" + i, null));

			if (ArrayUtil.isEmpty(queryValues)) {
				break;
			}

			boolean queryContains = GetterUtil.getBoolean(
				unicodeProperties.getProperty(
					"queryContains" + i, StringPool.BLANK));
			boolean queryAndOperator = GetterUtil.getBoolean(
				unicodeProperties.getProperty(
					"queryAndOperator" + i, StringPool.BLANK));
			String queryName = unicodeProperties.getProperty(
				"queryName" + i, StringPool.BLANK);

			if (Objects.equals(queryName, "assetCategories") && queryContains &&
				(queryAndOperator || (queryValues.length == 1))) {

				assetCategoryIds = ArrayUtil.append(
					assetCategoryIds, GetterUtil.getLongValues(queryValues));
			}
		}

		return assetCategoryIds;
	}

	private BooleanClause[] _getAssetCategoryIdsBooleanClauses(
		long[][] assetCategoryIds) {

		if (ArrayUtil.isEmpty(assetCategoryIds)) {
			return new BooleanClause[0];
		}

		BooleanQuery booleanQuery = new BooleanQuery();

		BooleanFilter assetCategoryIdsBooleanFilter = new BooleanFilter();

		for (long[] assetCategoryArrayIds : assetCategoryIds) {
			TermsFilter assetCategoryIdTermsFilter = new TermsFilter(
				Field.ASSET_CATEGORY_IDS);

			assetCategoryIdTermsFilter.addValues(
				ArrayUtil.toStringArray(assetCategoryArrayIds));

			assetCategoryIdsBooleanFilter.add(
				assetCategoryIdTermsFilter, BooleanClauseOccur.MUST);
		}

		booleanQuery.setPreBooleanFilter(assetCategoryIdsBooleanFilter);

		return new BooleanClause[] {
			new BooleanClause<>(booleanQuery, BooleanClauseOccur.MUST)
		};
	}

	private long[] _getAssetEntryQueryGroupIds(
		long assetListEntryGroupId, long[] groupIds) {

		if (ArrayUtil.isEmpty(groupIds)) {
			return new long[] {assetListEntryGroupId};
		}

		Set<Long> connectedDepotEntryGroupIds = _getConnectedDepotEntryGroupIds(
			assetListEntryGroupId);

		return ArrayUtil.filter(
			groupIds,
			groupId -> {
				if (groupId == assetListEntryGroupId) {
					return true;
				}

				Group group = _groupLocalService.fetchGroup(groupId);

				if (group == null) {
					return false;
				}

				if (!group.isDepot()) {
					return true;
				}

				return connectedDepotEntryGroupIds.contains(groupId);
			});
	}

	private String[] _getAssetTagNames(UnicodeProperties unicodeProperties) {
		List<String> allAssetTagNames = new ArrayList<>();

		for (int i = 0; true; i++) {
			String[] queryValues = StringUtil.split(
				unicodeProperties.getProperty("queryValues" + i, null));

			if (ArrayUtil.isEmpty(queryValues)) {
				break;
			}

			boolean queryContains = GetterUtil.getBoolean(
				unicodeProperties.getProperty(
					"queryContains" + i, StringPool.BLANK));
			boolean queryAndOperator = GetterUtil.getBoolean(
				unicodeProperties.getProperty(
					"queryAndOperator" + i, StringPool.BLANK));
			String queryName = unicodeProperties.getProperty(
				"queryName" + i, StringPool.BLANK);

			if (Objects.equals(queryName, "assetTags") && queryContains &&
				(queryAndOperator || (queryValues.length == 1))) {

				Collections.addAll(allAssetTagNames, queryValues);
			}
		}

		return allAssetTagNames.toArray(new String[0]);
	}

	private BooleanClause[] _getAssetTagNamesBooleanClauses(
		String[][] assetTagNames) {

		if (ArrayUtil.isEmpty(assetTagNames)) {
			return new BooleanClause[0];
		}

		BooleanQuery booleanQuery = new BooleanQuery();

		BooleanFilter assetTagNamesBooleanFilter = new BooleanFilter();

		for (String[] assetTagArrayNames : assetTagNames) {
			TermsFilter assetTagIdTermsFilter = new TermsFilter(
				Field.ASSET_TAG_NAMES + ".raw");

			assetTagIdTermsFilter.addValues(
				ArrayUtil.toStringArray(assetTagArrayNames));

			assetTagNamesBooleanFilter.add(
				assetTagIdTermsFilter, BooleanClauseOccur.MUST);
		}

		booleanQuery.setPreBooleanFilter(assetTagNamesBooleanFilter);

		return new BooleanClause[] {
			new BooleanClause<>(booleanQuery, BooleanClauseOccur.MUST)
		};
	}

	private long[] _getClassNameIds(
		UnicodeProperties unicodeProperties, long[] availableClassNameIds) {

		boolean anyAssetType = GetterUtil.getBoolean(
			unicodeProperties.getProperty(
				"anyAssetType", Boolean.TRUE.toString()));

		if (anyAssetType) {
			return availableClassNameIds;
		}

		long defaultClassNameId = GetterUtil.getLong(
			unicodeProperties.getProperty("anyAssetType", null));

		if (defaultClassNameId > 0) {
			return new long[] {defaultClassNameId};
		}

		long[] classNameIds = GetterUtil.getLongValues(
			StringUtil.split(
				unicodeProperties.getProperty("classNameIds", null)));

		if (ArrayUtil.isNotEmpty(classNameIds)) {
			return classNameIds;
		}

		return availableClassNameIds;
	}

	private long[] _getClassTypeIds(
		AssetListEntry assetListEntry, UnicodeProperties unicodeProperties,
		String className) {

		long[] availableClassTypeIds = {};

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				className);

		if (assetRendererFactory == null) {
			return availableClassTypeIds;
		}

		ClassTypeReader classTypeReader =
			assetRendererFactory.getClassTypeReader();

		try {
			availableClassTypeIds = TransformUtil.transformToLongArray(
				classTypeReader.getAvailableClassTypes(
					_portal.getSharedContentSiteGroupIds(
						assetListEntry.getCompanyId(),
						assetListEntry.getGroupId(),
						assetListEntry.getUserId()),
					LocaleUtil.getDefault()),
				ClassType::getClassTypeId);
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to get class types for class name " + className,
				portalException);
		}

		Class<? extends AssetRendererFactory> clazz =
			_assetRendererFactoryClassProvider.getClass(assetRendererFactory);

		boolean anyAssetType = GetterUtil.getBoolean(
			unicodeProperties.getProperty(
				"anyClassType" + clazz.getSimpleName(),
				Boolean.TRUE.toString()));

		if (anyAssetType) {
			return availableClassTypeIds;
		}

		long anyClassTypeId = GetterUtil.getLong(
			unicodeProperties.getProperty(
				"anyClassType" + clazz.getSimpleName(), null),
			-1);

		if (anyClassTypeId > -1) {
			return new long[] {anyClassTypeId};
		}

		long[] classTypeIds = StringUtil.split(
			unicodeProperties.getProperty(
				"classTypeIds" + clazz.getSimpleName(), null),
			0L);

		if (classTypeIds != null) {
			return classTypeIds;
		}

		return availableClassTypeIds;
	}

	private BooleanClause[] _getClassTypeIdsBooleanClauses(
		long[] classTypeIds) {

		if (ArrayUtil.isEmpty(classTypeIds)) {
			return new BooleanClause[0];
		}

		BooleanQuery booleanQuery = new BooleanQuery();

		BooleanFilter booleanFilter = new BooleanFilter();

		TermsFilter termsFilter = new TermsFilter(Field.CLASS_TYPE_ID);

		termsFilter.addValues(ArrayUtil.toStringArray(classTypeIds));

		booleanFilter.add(termsFilter, BooleanClauseOccur.MUST);

		booleanQuery.setPreBooleanFilter(booleanFilter);

		return new BooleanClause[] {
			new BooleanClause<>(booleanQuery, BooleanClauseOccur.MUST)
		};
	}

	private long[] _getCombinedSegmentsEntryIds(
		AssetListEntry assetListEntry, long[] segmentEntryIds) {

		if ((segmentEntryIds.length > 1) &&
			ArrayUtil.contains(
				segmentEntryIds, SegmentsEntryConstants.ID_DEFAULT)) {

			segmentEntryIds = ArrayUtil.remove(
				segmentEntryIds, SegmentsEntryConstants.ID_DEFAULT);
		}

		long[] combinedSegmentsEntryIds = TransformUtil.transformToLongArray(
			ListUtil.sort(
				TransformUtil.transformToList(
					segmentEntryIds,
					segmentsEntryId ->
						_assetListEntrySegmentsEntryRelLocalService.
							fetchAssetListEntrySegmentsEntryRel(
								assetListEntry.getAssetListEntryId(),
								segmentsEntryId)),
				Comparator.comparing(
					AssetListEntrySegmentsEntryRel::getPriority)),
			AssetListEntrySegmentsEntryRelModel::getSegmentsEntryId);

		if (combinedSegmentsEntryIds.length == 0) {
			combinedSegmentsEntryIds = new long[] {
				SegmentsEntryConstants.ID_DEFAULT
			};
		}

		return combinedSegmentsEntryIds;
	}

	private Set<Long> _getConnectedDepotEntryGroupIds(long groupId) {
		try {
			return SetUtil.fromList(
				TransformUtil.transform(
					_depotEntryLocalService.getGroupConnectedDepotEntries(
						groupId, DepotConstants.TYPE_ANY, QueryUtil.ALL_POS,
						QueryUtil.ALL_POS),
					DepotEntry::getGroupId));
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return Collections.emptySet();
		}
	}

	private InfoPage<AssetEntry> _getDynamicAssetEntries(
		AssetListEntry assetListEntry, long[] segmentsEntryIds,
		long[][] assetCategoryIds, String[][] assetTagNames, String keywords,
		String userId, int start, int end) {

		if (!_assetListConfiguration.combineAssetsFromAllSegmentsDynamic()) {
			AssetEntryQuery assetEntryQuery = getAssetEntryQuery(
				assetListEntry,
				_getFirstSegmentsEntryId(assetListEntry, segmentsEntryIds),
				userId);

			assetEntryQuery.setEnd(end);
			assetEntryQuery.setStart(start);

			return _dynamicSearch(
				assetListEntry.getCompanyId(), assetCategoryIds,
				Collections.singletonList(assetEntryQuery), assetTagNames,
				keywords, start, end);
		}

		return _dynamicSearch(
			assetListEntry.getCompanyId(), assetCategoryIds,
			TransformUtil.transformToList(
				_getCombinedSegmentsEntryIds(assetListEntry, segmentsEntryIds),
				segmentsEntryId -> getAssetEntryQuery(
					assetListEntry, segmentsEntryId, userId)),
			assetTagNames, keywords, start, end);
	}

	private SearchContext _getDynamicSearchContext(
		long companyId, long[][] assetCategoryIds,
		AssetEntryQuery assetEntryQuery, String[][] assetTagNames,
		String keywords) {

		SearchContext searchContext = new SearchContext();

		searchContext.setBooleanClauses(
			ArrayUtil.append(
				_getAssetCategoryIdsBooleanClauses(assetCategoryIds),
				_getAssetTagNamesBooleanClauses(assetTagNames),
				_getClassTypeIdsBooleanClauses(
					assetEntryQuery.getClassTypeIds()),
				_getFiltersBooleanClauses(assetEntryQuery, companyId)));
		searchContext.setCompanyId(companyId);
		searchContext.setEnd(assetEntryQuery.getEnd());
		searchContext.setKeywords(keywords);
		searchContext.setStart(assetEntryQuery.getStart());

		return searchContext;
	}

	private String _getFieldReference(
		DDMStructure ddmStructure, String fieldName) {

		try {
			return ddmStructure.getFieldProperty(fieldName, "fieldReference");
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return fieldName;
		}
	}

	private String _getFieldReference(long ddmStructureId, String fieldName) {
		try {
			DDMStructure ddmStructure =
				_ddmStructureLocalService.getDDMStructure(ddmStructureId);

			return ddmStructure.getFieldProperty(fieldName, "fieldReference");
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return fieldName;
		}
	}

	private BooleanClause[] _getFiltersBooleanClauses(
		AssetEntryQuery assetEntryQuery, long companyId) {

		JSONArray filtersJSONArray = (JSONArray)assetEntryQuery.getAttribute(
			"filters");

		return AssetListFiltersUtil.getFiltersBooleanClauses(
			companyId, filtersJSONArray, LocaleUtil.getMostRelevantLocale());
	}

	private long _getFirstSegmentsEntryId(
		AssetListEntry assetListEntry, long[] segmentsEntryIds) {

		if (segmentsEntryIds.length == 0) {
			return SegmentsEntryConstants.ID_DEFAULT;
		}

		if (segmentsEntryIds.length == 1) {
			return segmentsEntryIds[0];
		}

		List<AssetListEntrySegmentsEntryRel> assetListEntrySegmentsEntryRels =
			_assetListEntrySegmentsEntryRelLocalService.
				getAssetListEntrySegmentsEntryRels(
					assetListEntry.getAssetListEntryId(), segmentsEntryIds, 0,
					1,
					AssetListEntrySegmentsEntryRelPriorityComparator.
						getInstance(true));

		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel =
			assetListEntrySegmentsEntryRels.get(0);

		return assetListEntrySegmentsEntryRel.getSegmentsEntryId();
	}

	private Map<AssetListEntryAssetEntryRel, Long> _getGroupIds(
		AssetListEntry assetListEntry, long[] segmentsEntryIds) {

		if (_assetListConfiguration.combineAssetsFromAllSegmentsManual()) {
			Map<AssetListEntryAssetEntryRel, Long> groupIds = new HashMap<>();

			segmentsEntryIds = _sort(
				assetListEntry,
				_getCombinedSegmentsEntryIds(assetListEntry, segmentsEntryIds));

			for (long segmentId : segmentsEntryIds) {
				groupIds.putAll(
					_getGroupIds(
						_assetListEntryAssetEntryRelLocalService.
							getAssetListEntryAssetEntryRels(
								assetListEntry.getAssetListEntryId(),
								new long[] {segmentId}, QueryUtil.ALL_POS,
								QueryUtil.ALL_POS)));
			}

			return groupIds;
		}

		return _getGroupIds(
			_assetListEntryAssetEntryRelLocalService.
				getAssetListEntryAssetEntryRels(
					assetListEntry.getAssetListEntryId(),
					new long[] {
						_getFirstSegmentsEntryId(
							assetListEntry, segmentsEntryIds)
					},
					QueryUtil.ALL_POS, QueryUtil.ALL_POS));
	}

	private Map<AssetListEntryAssetEntryRel, Long> _getGroupIds(
		List<AssetListEntryAssetEntryRel> assetListEntryAssetEntryRels) {

		Map<AssetListEntryAssetEntryRel, Long> groupIds = new HashMap<>();

		for (AssetListEntryAssetEntryRel assetListEntryAssetEntryRel :
				assetListEntryAssetEntryRels) {

			AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
				assetListEntryAssetEntryRel.getAssetEntryId());

			if ((assetEntry == null) || !assetEntry.isVisible()) {
				continue;
			}

			AssetRendererFactory<?> assetRendererFactory =
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(
						assetEntry.getClassName());

			if (assetRendererFactory == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"No asset renderer factory found for class " +
							assetEntry.getClassName());
				}

				continue;
			}

			groupIds.put(assetListEntryAssetEntryRel, assetEntry.getGroupId());
		}

		return groupIds;
	}

	private String[] _getKeywords(UnicodeProperties unicodeProperties) {
		String[] allKeywords = new String[0];

		for (int i = 0; true; i++) {
			String[] queryValues = StringUtil.split(
				unicodeProperties.getProperty("queryValues" + i, null));

			if (ArrayUtil.isEmpty(queryValues)) {
				break;
			}

			boolean queryContains = GetterUtil.getBoolean(
				unicodeProperties.getProperty(
					"queryContains" + i, StringPool.BLANK));
			boolean queryAndOperator = GetterUtil.getBoolean(
				unicodeProperties.getProperty(
					"queryAndOperator" + i, StringPool.BLANK));
			String queryName = unicodeProperties.getProperty(
				"queryName" + i, StringPool.BLANK);

			if (Objects.equals(queryName, "keywords") && queryContains &&
				(queryAndOperator || (queryValues.length == 1))) {

				allKeywords = queryValues;
			}
		}

		return allKeywords;
	}

	private InfoPage<AssetEntry> _getManualAssetEntries(
		AssetListEntry assetListEntry, long[] segmentsEntryIds,
		long[][] assetCategoryIds, String[][] assetTagNames, String keywords,
		int start, int end) {

		Map<AssetListEntryAssetEntryRel, Long> groupIds = _getGroupIds(
			assetListEntry, segmentsEntryIds);

		if (MapUtil.isEmpty(groupIds)) {
			return InfoPage.of(Collections.emptyList());
		}

		List<Long> assetEntryIds = ListUtil.toList(
			ListUtil.sort(
				ListUtil.fromMapKeys(groupIds),
				Comparator.comparing(
					AssetListEntryAssetEntryRelModel::getPosition)),
			AssetListEntryAssetEntryRelModel::getAssetEntryId);

		try {
			Hits hits = _assetHelper.search(
				_getManualSearchContext(
					assetCategoryIds, assetEntryIds, assetTagNames,
					assetListEntry.getCompanyId(), keywords),
				_getManualAssetEntryQuery(
					assetListEntry, ArrayUtil.toLongArray(groupIds.values())),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			List<AssetEntry> assetEntries = _assetHelper.getAssetEntries(hits);

			ListUtil.sort(
				assetEntries,
				Comparator.comparing(
					assetEntry -> assetEntryIds.indexOf(
						assetEntry.getEntryId())));

			return InfoPage.of(
				ListUtil.subList(assetEntries, start, end),
				Pagination.of(end, start), hits.getLength());
		}
		catch (Exception exception) {
			_log.error("Unable to get asset entries", exception);
		}

		return InfoPage.of(Collections.emptyList());
	}

	private AssetEntryQuery _getManualAssetEntryQuery(
		AssetListEntry assetListEntry, long[] groupIds) {

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		if (Validator.isNotNull(assetListEntry.getAssetEntryType()) &&
			!Objects.equals(
				assetListEntry.getAssetEntryType(),
				AssetEntry.class.getName())) {

			assetEntryQuery.setClassName(assetListEntry.getAssetEntryType());

			long classTypeId = GetterUtil.getLong(
				assetListEntry.getAssetEntrySubtype());

			if (classTypeId > 0) {
				assetEntryQuery.setClassTypeIds(new long[] {classTypeId});
			}
		}
		else {
			assetEntryQuery.setClassNameIds(
				AssetRendererFactoryRegistryUtil.getClassNameIds(
					assetListEntry.getCompanyId(), true));
		}

		assetEntryQuery.setGroupIds(ArrayUtil.unique(groupIds));

		return assetEntryQuery;
	}

	private SearchContext _getManualSearchContext(
		long[][] assetCategoryIds, List<Long> assetEntryIds,
		String[][] assetTagNames, long companyId, String keywords) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttribute(
			Field.ASSET_ENTRY_IDS, ArrayUtil.toLongArray(assetEntryIds));
		searchContext.setBooleanClauses(
			ArrayUtil.append(
				_getAssetTagNamesBooleanClauses(assetTagNames),
				_getAssetCategoryIdsBooleanClauses(assetCategoryIds)));
		searchContext.setCompanyId(companyId);
		searchContext.setKeywords(keywords);

		return searchContext;
	}

	private String _getOrderByColumn(
		long companyId, String defaultOrderByColumn, String orderByColumn) {

		if (!orderByColumn.startsWith(StringPool.OPEN_CURLY_BRACE)) {
			return orderByColumn;
		}

		if (FeatureFlagManagerUtil.isEnabled(companyId, "LPD-74731")) {
			orderByColumn = AssetListOrderByColumnUtil.toOrderByColumn(
				companyId, orderByColumn);
		}

		if (orderByColumn.startsWith(StringPool.OPEN_CURLY_BRACE)) {
			return defaultOrderByColumn;
		}

		return orderByColumn;
	}

	private long[] _getReferencedModelsGroupIds(long[] groupIds) {
		for (long groupId : groupIds) {
			Group group = _groupLocalService.fetchGroup(groupId);

			if (group == null) {
				continue;
			}

			int depotEntryType = GetterUtil.getInteger(
				group.getTypeSettingsProperty("depotEntryType"));

			if (depotEntryType != DepotConstants.TYPE_SPACE) {
				continue;
			}

			Group cmsGroup = _groupLocalService.fetchGroup(
				group.getCompanyId(), GroupConstants.CMS);

			if (cmsGroup != null) {
				return ArrayUtil.append(groupIds, cmsGroup.getGroupId());
			}

			break;
		}

		return groupIds;
	}

	private void _setCategoriesAndTagsAndKeywords(
		AssetEntryQuery assetEntryQuery, UnicodeProperties unicodeProperties,
		long[] overrideAllAssetCategoryIds, String[] overrideAllAssetTagNames,
		String[] overrideAllKeywords) {

		long[] allAssetCategoryIds = new long[0];
		long[] anyAssetCategoryIds = new long[0];
		long[] notAllAssetCategoryIds = new long[0];
		long[] notAnyAssetCategoryIds = new long[0];

		String[] allAssetTagNames = new String[0];
		String[] anyAssetTagNames = new String[0];
		String[] notAllAssetTagNames = new String[0];
		String[] notAnyAssetTagNames = new String[0];

		String[] allKeywords = new String[0];
		String[] anyKeywords = new String[0];
		String[] notAllKeywords = new String[0];
		String[] notAnyKeywords = new String[0];

		for (int i = 0; true; i++) {
			String[] queryValues = StringUtil.split(
				unicodeProperties.getProperty("queryValues" + i, null));

			if (ArrayUtil.isEmpty(queryValues)) {
				break;
			}

			boolean queryContains = GetterUtil.getBoolean(
				unicodeProperties.getProperty(
					"queryContains" + i, StringPool.BLANK));
			boolean queryAndOperator = GetterUtil.getBoolean(
				unicodeProperties.getProperty(
					"queryAndOperator" + i, StringPool.BLANK));
			String queryName = unicodeProperties.getProperty(
				"queryName" + i, StringPool.BLANK);

			if (Objects.equals(queryName, "assetCategories")) {
				long[] assetCategoryIds = GetterUtil.getLongValues(queryValues);

				if (queryContains && queryAndOperator) {
					allAssetCategoryIds = assetCategoryIds;
				}
				else if (queryContains && !queryAndOperator) {
					anyAssetCategoryIds = assetCategoryIds;
				}
				else if (!queryContains && queryAndOperator) {
					notAllAssetCategoryIds = assetCategoryIds;
				}
				else {
					notAnyAssetCategoryIds = assetCategoryIds;
				}
			}
			else if (Objects.equals(queryName, "keywords")) {
				if (queryContains && queryAndOperator) {
					allKeywords = queryValues;
				}
				else if (queryContains && !queryAndOperator) {
					anyKeywords = queryValues;
				}
				else if (!queryContains && queryAndOperator) {
					notAllKeywords = queryValues;
				}
				else {
					notAnyKeywords = queryValues;
				}
			}
			else {
				if (queryContains && queryAndOperator) {
					allAssetTagNames = queryValues;
				}
				else if (queryContains && !queryAndOperator) {
					anyAssetTagNames = queryValues;
				}
				else if (!queryContains && queryAndOperator) {
					notAllAssetTagNames = queryValues;
				}
				else {
					notAnyAssetTagNames = queryValues;
				}
			}
		}

		if (overrideAllAssetCategoryIds != null) {
			allAssetCategoryIds = overrideAllAssetCategoryIds;
		}

		allAssetCategoryIds = _filterAssetCategoryIds(allAssetCategoryIds);

		assetEntryQuery.setAllCategoryIds(allAssetCategoryIds);

		if (overrideAllKeywords != null) {
			allKeywords = overrideAllKeywords;
		}

		assetEntryQuery.setAllKeywords(allKeywords);

		if (overrideAllAssetTagNames != null) {
			allAssetTagNames = overrideAllAssetTagNames;
		}

		long[] groupIds = _getReferencedModelsGroupIds(
			GetterUtil.getLongValues(
				StringUtil.split(
					unicodeProperties.getProperty("groupIds", null))));

		for (String assetTagName : allAssetTagNames) {
			long[] allAssetTagIds = _assetTagLocalService.getTagIds(
				groupIds, assetTagName);

			assetEntryQuery.addAllTagIdsArray(allAssetTagIds);
		}

		anyAssetCategoryIds = _filterAssetCategoryIds(anyAssetCategoryIds);

		assetEntryQuery.setAnyCategoryIds(anyAssetCategoryIds);

		assetEntryQuery.setAnyKeywords(anyKeywords);

		long[] anyAssetTagIds = _assetTagLocalService.getTagIds(
			groupIds, anyAssetTagNames);

		assetEntryQuery.setAnyTagIds(anyAssetTagIds);

		assetEntryQuery.setNotAllCategoryIds(notAllAssetCategoryIds);
		assetEntryQuery.setNotAllKeywords(notAllKeywords);

		for (String assetTagName : notAllAssetTagNames) {
			long[] notAllAssetTagIds = _assetTagLocalService.getTagIds(
				groupIds, assetTagName);

			assetEntryQuery.addNotAllTagIdsArray(notAllAssetTagIds);
		}

		assetEntryQuery.setNotAnyCategoryIds(notAnyAssetCategoryIds);
		assetEntryQuery.setNotAnyKeywords(notAnyKeywords);

		long[] notAnyAssetTagIds = _assetTagLocalService.getTagIds(
			groupIds, notAnyAssetTagNames);

		assetEntryQuery.setNotAnyTagIds(notAnyAssetTagIds);
	}

	private long[] _sort(
		AssetListEntry assetListEntry, long[] segmentsEntryIds) {

		return TransformUtil.transformToLongArray(
			ListUtil.sort(
				TransformUtil.transformToList(
					segmentsEntryIds,
					segmentsEntryId ->
						_assetListEntrySegmentsEntryRelLocalService.
							fetchAssetListEntrySegmentsEntryRel(
								assetListEntry.getAssetListEntryId(),
								segmentsEntryId)),
				Comparator.comparing(
					AssetListEntrySegmentsEntryRel::getPriority)),
			AssetListEntrySegmentsEntryRel::getSegmentsEntryId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetListAssetEntryProviderImpl.class);

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetHelper _assetHelper;

	private volatile AssetListConfiguration _assetListConfiguration;

	@Reference
	private AssetListEntryAssetEntryRelLocalService
		_assetListEntryAssetEntryRelLocalService;

	@Reference
	private AssetListEntrySegmentsEntryRelLocalService
		_assetListEntrySegmentsEntryRelLocalService;

	@Reference
	private AssetRendererFactoryClassProvider
		_assetRendererFactoryClassProvider;

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private DDMIndexer _ddmIndexer;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

}