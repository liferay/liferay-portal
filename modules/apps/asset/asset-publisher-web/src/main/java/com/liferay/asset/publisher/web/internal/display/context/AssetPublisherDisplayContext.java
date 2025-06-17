/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.display.context;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.ClassType;
import com.liferay.asset.kernel.model.ClassTypeField;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetEntryServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyServiceUtil;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.list.asset.entry.provider.AssetListAssetEntryProvider;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalService;
import com.liferay.asset.publisher.action.AssetEntryAction;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.asset.publisher.constants.AssetPublisherWebKeys;
import com.liferay.asset.publisher.util.AssetEntryResult;
import com.liferay.asset.publisher.util.AssetPublisherHelper;
import com.liferay.asset.publisher.web.internal.configuration.AssetPublisherPortletInstanceConfiguration;
import com.liferay.asset.publisher.web.internal.configuration.AssetPublisherSelectionStyleConfigurationUtil;
import com.liferay.asset.publisher.web.internal.configuration.AssetPublisherWebConfiguration;
import com.liferay.asset.publisher.web.internal.constants.AssetPublisherSelectionStyleConstants;
import com.liferay.asset.publisher.web.internal.helper.AssetPublisherWebHelper;
import com.liferay.asset.publisher.web.internal.util.AssetPublisherCustomizer;
import com.liferay.asset.publisher.web.internal.util.AssetPublisherUtil;
import com.liferay.asset.publisher.web.internal.util.FF_LPD_39304_CompanyTemporarySwapper;
import com.liferay.asset.tags.item.selector.AssetTagsItemSelectorCriterion;
import com.liferay.asset.tags.item.selector.AssetTagsItemSelectorReturnType;
import com.liferay.asset.util.AssetHelper;
import com.liferay.asset.util.AssetPublisherAddItemHolder;
import com.liferay.asset.util.LinkedAssetEntryIdsUtil;
import com.liferay.asset.util.comparator.AssetRendererFactoryTypeNameComparator;
import com.liferay.asset.util.comparator.ClassTypeNameComparator;
import com.liferay.document.library.kernel.document.conversion.DocumentConversionUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.collection.provider.item.selector.InfoCollectionProviderItemSelectorCriterion;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.list.provider.item.selector.criterion.InfoListProviderItemSelectorReturnType;
import com.liferay.info.pagination.InfoPage;
import com.liferay.info.pagination.Pagination;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.AssetEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.GroupItemSelectorReturnType;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.InfoListItemSelectorReturnType;
import com.liferay.item.selector.criteria.asset.criterion.AssetEntryItemSelectorCriterion;
import com.liferay.item.selector.criteria.group.criterion.GroupItemSelectorCriterion;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceComparator;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CollatorUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsParamUtil;
import com.liferay.portal.kernel.util.StringComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.rss.util.RSSUtil;
import com.liferay.segments.SegmentsEntryRetriever;
import com.liferay.segments.constants.SegmentsWebKeys;
import com.liferay.segments.context.RequestContextMapper;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.text.Collator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * Provides utility methods moved from the Asset Publisher portlet's JSP files
 * to reduce the complexity of the views.
 *
 * @author Eudaldo Alonso
 */
public class AssetPublisherDisplayContext {

	public static final String PAGINATION_TYPE_NONE = "none";

	public static final String PAGINATION_TYPE_REGULAR = "regular";

	public static final String PAGINATION_TYPE_SIMPLE = "simple";

	public static final String[] PAGINATION_TYPES = {
		PAGINATION_TYPE_NONE, PAGINATION_TYPE_REGULAR, PAGINATION_TYPE_SIMPLE
	};

	public AssetPublisherDisplayContext(
			AssetHelper assetHelper,
			AssetListAssetEntryProvider assetListAssetEntryProvider,
			AssetListEntrySegmentsEntryRelLocalService
				assetListEntrySegmentsEntryRelLocalService,
			AssetPublisherCustomizer assetPublisherCustomizer,
			AssetPublisherHelper assetPublisherHelper,
			AssetPublisherWebConfiguration assetPublisherWebConfiguration,
			AssetPublisherWebHelper assetPublisherWebHelper,
			InfoItemServiceRegistry infoItemServiceRegistry,
			ItemSelector itemSelector, Portal portal,
			PortletRequest portletRequest, PortletResponse portletResponse,
			PortletPreferences portletPreferences,
			RequestContextMapper requestContextMapper,
			SegmentsEntryRetriever segmentsEntryRetriever)
		throws ConfigurationException {

		_assetHelper = assetHelper;
		_assetListAssetEntryProvider = assetListAssetEntryProvider;
		_assetListEntrySegmentsEntryRelLocalService =
			assetListEntrySegmentsEntryRelLocalService;
		_assetPublisherCustomizer = assetPublisherCustomizer;
		_assetPublisherHelper = assetPublisherHelper;
		_assetPublisherWebConfiguration = assetPublisherWebConfiguration;
		_assetPublisherWebHelper = assetPublisherWebHelper;
		_infoItemServiceRegistry = infoItemServiceRegistry;
		_itemSelector = itemSelector;
		_portal = portal;
		_portletRequest = portletRequest;
		_portletResponse = portletResponse;
		_portletPreferences = portletPreferences;
		_requestContextMapper = requestContextMapper;
		_segmentsEntryRetriever = segmentsEntryRetriever;

		_themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_assetPublisherPortletInstanceConfiguration =
			ConfigurationProviderUtil.getPortletInstanceConfiguration(
				AssetPublisherPortletInstanceConfiguration.class,
				_themeDisplay);

		_httpServletRequest = portal.getHttpServletRequest(portletRequest);
	}

	public AssetListEntry fetchAssetListEntry() throws PortalException {
		if (_assetListEntry != null) {
			return _assetListEntry;
		}

		try {
			_assetListEntry = AssetPublisherUtil.getAssetListEntry(
				true, _themeDisplay.getCompanyId(),
				_themeDisplay.getScopeGroupId(), _portletPreferences);
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}
		}

		return _assetListEntry;
	}

	public int getAbstractLength() {
		if (_abstractLength != null) {
			return _abstractLength;
		}

		_abstractLength = GetterUtil.getInteger(
			_portletPreferences.getValue("abstractLength", null),
			AssetHelper.ASSET_ENTRY_ABSTRACT_LENGTH);

		return _abstractLength;
	}

	public long[] getAllAssetCategoryIds() {
		if (_allAssetCategoryIds != null) {
			return _allAssetCategoryIds;
		}

		_allAssetCategoryIds = new long[0];

		long assetCategoryId = ParamUtil.getLong(
			_httpServletRequest, "categoryId");

		String selectionStyle = getSelectionStyle();

		if (selectionStyle.equals(
				AssetPublisherSelectionStyleConstants.TYPE_DYNAMIC)) {

			_allAssetCategoryIds = _assetPublisherHelper.getAssetCategoryIds(
				_portletPreferences);
		}

		if ((assetCategoryId > 0) &&
			!ArrayUtil.contains(_allAssetCategoryIds, assetCategoryId)) {

			_allAssetCategoryIds = ArrayUtil.append(
				_allAssetCategoryIds, assetCategoryId);
		}

		return _allAssetCategoryIds;
	}

	public String[] getAllAssetTagNames() {
		if (_allAssetTagNames != null) {
			return _allAssetTagNames;
		}

		_allAssetTagNames = new String[0];

		String assetTagName = ParamUtil.getString(_httpServletRequest, "tag");

		String selectionStyle = getSelectionStyle();

		if (selectionStyle.equals(
				AssetPublisherSelectionStyleConstants.TYPE_DYNAMIC)) {

			_allAssetTagNames = _assetPublisherHelper.getAssetTagNames(
				_portletPreferences);
		}

		if (Validator.isNotNull(assetTagName) &&
			!ArrayUtil.contains(_allAssetTagNames, assetTagName)) {

			_allAssetTagNames = ArrayUtil.append(
				_allAssetTagNames, assetTagName);
		}

		if (isMergeURLTags()) {
			_allAssetTagNames = ArrayUtil.append(
				_allAssetTagNames, getCompilerTagNames());
		}

		_allAssetTagNames = ArrayUtil.distinct(
			_allAssetTagNames, new StringComparator(true, true));

		return _allAssetTagNames;
	}

	public String[] getAllKeywords() {
		if (_allKeywords != null) {
			return _allKeywords;
		}

		_allKeywords = new String[0];

		String keyword = ParamUtil.getString(_httpServletRequest, "keyword");

		String selectionStyle = getSelectionStyle();

		if (selectionStyle.equals(
				AssetPublisherSelectionStyleConstants.TYPE_DYNAMIC)) {

			_allKeywords = _assetPublisherHelper.getKeywords(
				_portletPreferences);
		}

		if (Validator.isNotNull(keyword) &&
			!ArrayUtil.contains(_allKeywords, keyword)) {

			_allKeywords = ArrayUtil.append(_allKeywords, keyword);
		}

		_allKeywords = ArrayUtil.distinct(_allKeywords, new StringComparator());

		return _allKeywords;
	}

	public long getAssetCategoryId() {
		if (_assetCategoryId != null) {
			return _assetCategoryId;
		}

		_assetCategoryId = ParamUtil.getLong(_httpServletRequest, "categoryId");

		return _assetCategoryId;
	}

	public List<AssetEntryAction<?>> getAssetEntryActions(String className) {
		List<AssetEntryAction<?>> assetEntryActions =
			_stringListServiceTrackerMap.getService(className);

		if (assetEntryActions != null) {
			return assetEntryActions;
		}

		return Collections.emptyList();
	}

	public String getAssetEntryId() {
		return ParamUtil.getString(_httpServletRequest, "assetEntryId");
	}

	public AssetEntryQuery getAssetEntryQuery() throws Exception {
		if (_assetEntryQuery != null) {
			return _assetEntryQuery;
		}

		AssetListEntry assetListEntry = fetchAssetListEntry();

		if (isSelectionStyleAssetList() && (assetListEntry != null)) {
			_assetEntryQuery = _assetListAssetEntryProvider.getAssetEntryQuery(
				assetListEntry, _getSegmentsEntryIds(assetListEntry),
				_getSegmentsAnonymousUserId());
		}
		else {
			_assetEntryQuery = _assetPublisherHelper.getAssetEntryQuery(
				_portletPreferences, _themeDisplay.getScopeGroupId(),
				_themeDisplay.getLayout(), getAllAssetCategoryIds(),
				getAllAssetTagNames(), getAllKeywords());
		}

		_assetEntryQuery.setEnablePermissions(isEnablePermissions());

		_configureSubtypeFieldFilter(
			_assetEntryQuery, _themeDisplay.getSiteDefaultLocale());

		_assetEntryQuery.setPaginationType(getPaginationType());

		_assetPublisherWebHelper.processAssetEntryQuery(
			_themeDisplay.getUser(), _portletPreferences, _assetEntryQuery);

		_assetPublisherCustomizer.setAssetEntryQueryOptions(
			_assetEntryQuery, _httpServletRequest);

		return _assetEntryQuery;
	}

	public List<AssetEntryResult> getAssetEntryResults() throws Exception {
		if (_assetEntryResults != null) {
			return _assetEntryResults;
		}

		if (isSelectionStyleDynamic()) {
			_assetEntryResults = _assetPublisherHelper.getAssetEntryResults(
				getSearchContainer(), getAssetEntryQuery(),
				_themeDisplay.getLayout(), _portletPreferences,
				getPortletName(), _themeDisplay.getLocale(),
				_themeDisplay.getTimeZone(), _themeDisplay.getCompanyId(),
				_themeDisplay.getScopeGroupId(), _themeDisplay.getUserId(),
				getClassNameIds(), null);

			return _assetEntryResults;
		}

		SearchContainer<AssetEntry> searchContainer = getSearchContainer();

		InfoPage<AssetEntry> infoPage = _getInfoPage(searchContainer);

		if (ListUtil.isEmpty(infoPage.getPageItems())) {
			return Collections.emptyList();
		}

		searchContainer.setResultsAndTotal(
			() -> (List<AssetEntry>)infoPage.getPageItems(),
			infoPage.getTotalCount());

		List<AssetEntryResult> assetEntryResults = new ArrayList<>();

		assetEntryResults.add(
			new AssetEntryResult(searchContainer.getResults()));

		_assetEntryResults = assetEntryResults;

		return _assetEntryResults;
	}

	public String getAssetLinkBehavior() {
		if (_assetLinkBehavior != null) {
			return _assetLinkBehavior;
		}

		_assetLinkBehavior = GetterUtil.getString(
			_portletPreferences.getValue("assetLinkBehavior", "viewInPortlet"));

		return _assetLinkBehavior;
	}

	public String getAssetListSelectorURL() {
		InfoCollectionProviderItemSelectorCriterion
			infoCollectionProviderItemSelectorCriterion =
				new InfoCollectionProviderItemSelectorCriterion();

		infoCollectionProviderItemSelectorCriterion.
			setDesiredItemSelectorReturnTypes(
				new InfoListItemSelectorReturnType(),
				new InfoListProviderItemSelectorReturnType());

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_portletRequest),
				getSelectAssetListEventName(),
				infoCollectionProviderItemSelectorCriterion));
	}

	public String getAssetTagName() {
		if (_assetTagName != null) {
			return _assetTagName;
		}

		_assetTagName = ParamUtil.getString(_httpServletRequest, "tag");

		return _assetTagName;
	}

	public Map<String, Serializable> getAttributes() {
		if (_attributes != null) {
			return _attributes;
		}

		_attributes = new HashMap<>();

		Map<String, String[]> parameters =
			_httpServletRequest.getParameterMap();

		for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
			String[] values = entry.getValue();

			if (ArrayUtil.isNotEmpty(values)) {
				String name = entry.getKey();

				if (values.length == 1) {
					_attributes.put(name, values[0]);
				}
				else {
					_attributes.put(name, values);
				}
			}
		}

		return _attributes;
	}

	public JSONArray getAutoFieldRulesJSONArray() {
		String queryLogicIndexesParam = ParamUtil.getString(
			_httpServletRequest, "queryLogicIndexes");

		int[] queryLogicIndexes = null;

		if (Validator.isNotNull(queryLogicIndexesParam)) {
			queryLogicIndexes = StringUtil.split(queryLogicIndexesParam, 0);
		}
		else {
			queryLogicIndexes = new int[0];

			for (int i = 0; true; i++) {
				String queryValues = PrefsParamUtil.getString(
					_portletPreferences, _httpServletRequest,
					"queryValues" + i);

				if (Validator.isNull(queryValues)) {
					break;
				}

				queryLogicIndexes = ArrayUtil.append(queryLogicIndexes, i);
			}

			if (queryLogicIndexes.length == 0) {
				queryLogicIndexes = ArrayUtil.append(queryLogicIndexes, -1);
			}
		}

		JSONArray rulesJSONArray = JSONFactoryUtil.createJSONArray();

		for (int queryLogicIndex : queryLogicIndexes) {
			boolean queryAndOperator = PrefsParamUtil.getBoolean(
				_portletPreferences, _httpServletRequest,
				"queryAndOperator" + queryLogicIndex);

			JSONObject ruleJSONObject = JSONUtil.put(
				"queryAndOperator", queryAndOperator
			).put(
				"queryContains",
				PrefsParamUtil.getBoolean(
					_portletPreferences, _httpServletRequest,
					"queryContains" + queryLogicIndex, true)
			);

			String queryValues = StringUtil.merge(
				_portletPreferences.getValues(
					"queryValues" + queryLogicIndex, new String[0]));

			String queryName = PrefsParamUtil.getString(
				_portletPreferences, _httpServletRequest,
				"queryName" + queryLogicIndex, "assetTags");

			if (Objects.equals(queryName, "assetTags")) {
				String[] tagNames = StringUtil.split(
					queryValues, StringPool.COMMA);

				tagNames = _normalizeAssetTagNames(tagNames);

				tagNames = ParamUtil.getStringValues(
					_httpServletRequest, "queryTagNames" + queryLogicIndex,
					tagNames);

				tagNames = _assetPublisherWebHelper.filterAssetTagNames(
					_themeDisplay.getScopeGroupId(), tagNames);

				queryValues = StringUtil.merge(tagNames);

				if (ArrayUtil.isEmpty(tagNames)) {
					continue;
				}

				List<Map<String, String>> selectedItems = new ArrayList<>();

				for (String tagName : tagNames) {
					selectedItems.add(
						HashMapBuilder.put(
							"label", tagName
						).put(
							"value", tagName
						).build());
				}

				ruleJSONObject.put("selectedItems", selectedItems);
			}
			else if (Objects.equals(queryName, "keywords")) {
				queryValues = ParamUtil.getString(
					_httpServletRequest, "keywords" + queryLogicIndex,
					queryValues);

				String[] keywords = StringUtil.split(queryValues, ",");

				if (ArrayUtil.isEmpty(keywords)) {
					continue;
				}

				List<String> items = new ArrayList<>();

				for (String keyword : keywords) {
					if (keyword.contains(" ")) {
						keyword = StringUtil.quote(keyword, CharPool.QUOTE);
					}

					items.add(keyword);
				}

				queryValues = StringUtil.merge(items, StringPool.SPACE);

				ruleJSONObject.put("selectedItems", queryValues);
			}
			else {
				queryValues = ParamUtil.getString(
					_httpServletRequest, "queryCategoryIds" + queryLogicIndex,
					queryValues);

				List<AssetCategory> assetCategories = _filterAssetCategories(
					GetterUtil.getLongValues(queryValues.split(",")));

				if (ListUtil.isEmpty(assetCategories)) {
					continue;
				}

				List<Map<String, Object>> selectedItems = new ArrayList<>();

				for (AssetCategory assetCategory : assetCategories) {
					selectedItems.add(
						HashMapBuilder.<String, Object>put(
							"label",
							assetCategory.getTitle(_themeDisplay.getLocale())
						).put(
							"value", assetCategory.getCategoryId()
						).build());
				}

				ruleJSONObject.put("selectedItems", selectedItems);
			}

			if (Validator.isNull(queryValues)) {
				continue;
			}

			ruleJSONObject.put(
				"queryValues", queryValues
			).put(
				"type", queryName
			);

			rulesJSONArray.put(ruleJSONObject);
		}

		return rulesJSONArray;
	}

	public long[] getAvailableClassNameIds() {
		if (_availableClassNameIds != null) {
			return _availableClassNameIds;
		}

		_availableClassNameIds =
			AssetRendererFactoryRegistryUtil.getIndexableClassNameIds(
				_themeDisplay.getCompanyId(), true);

		return _availableClassNameIds;
	}

	public String getCategorySelectorURL() {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(_portletRequest);

		InfoItemItemSelectorCriterion itemSelectorCriterion =
			new InfoItemItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new InfoItemItemSelectorReturnType());
		itemSelectorCriterion.setItemType(AssetCategory.class.getName());
		itemSelectorCriterion.setMultiSelection(true);

		return PortletURLBuilder.create(
			_itemSelector.getItemSelectorURL(
				requestBackedPortletURLFactory, _themeDisplay.getScopeGroup(),
				_themeDisplay.getScopeGroupId(),
				_portletResponse.getNamespace() + "selectCategory",
				itemSelectorCriterion)
		).buildString();
	}

	public long[] getClassNameIds() throws Exception {
		if (_classNameIds != null) {
			return _classNameIds;
		}

		if (isSelectionStyleAssetList()) {
			AssetEntryQuery assetEntryQuery = getAssetEntryQuery();

			_classNameIds = assetEntryQuery.getClassNameIds();
		}
		else {
			try (SafeCloseable safeCloseable =
					FF_LPD_39304_CompanyTemporarySwapper.
						setCompanyIdWithSafeCloseable(
							_themeDisplay.getCompanyId())) {

				_classNameIds = _assetPublisherHelper.getClassNameIds(
					_portletPreferences, getAvailableClassNameIds());
			}
		}

		return _classNameIds;
	}

	public long[] getClassTypeIds() {
		if (_classTypeIds != null) {
			return _classTypeIds;
		}

		_classTypeIds = GetterUtil.getLongValues(
			_portletPreferences.getValues("classTypeIds", null));

		return _classTypeIds;
	}

	public List<ClassType> getClassTypes(ClassTypeReader classTypeReader)
		throws PortalException {

		List<ClassType> classTypes = classTypeReader.getAvailableClassTypes(
			getReferencedModelsGroupIds(), _themeDisplay.getLocale());

		Collections.sort(classTypes, _getClassTypeComparator());

		return classTypes;
	}

	public String[] getCompilerTagNames() {
		if (_compilerTagNames != null) {
			return _compilerTagNames;
		}

		_compilerTagNames = new String[0];

		if (isMergeURLTags()) {
			_compilerTagNames = ParamUtil.getParameterValues(
				_httpServletRequest, "tags");
		}

		return _compilerTagNames;
	}

	public String getDDMStructureDisplayFieldValue() throws Exception {
		if (_ddmStructureDisplayFieldValue != null) {
			return _ddmStructureDisplayFieldValue;
		}

		_setDDMStructure();

		return _ddmStructureDisplayFieldValue;
	}

	public String getDDMStructureFieldLabel() throws Exception {
		if (_ddmStructureFieldLabel != null) {
			return _ddmStructureFieldLabel;
		}

		_setDDMStructure();

		return _ddmStructureFieldLabel;
	}

	public String getDDMStructureFieldName() throws Exception {
		if (_ddmStructureFieldName != null) {
			return _ddmStructureFieldName;
		}

		_setDDMStructure();

		return _ddmStructureFieldName;
	}

	public String getDDMStructureFieldValue() throws Exception {
		if (_ddmStructureFieldValue != null) {
			return _ddmStructureFieldValue;
		}

		_setDDMStructure();

		return _ddmStructureFieldValue;
	}

	public String getDefaultDisplayStyle() {
		return _assetPublisherPortletInstanceConfiguration.
			defaultDisplayStyle();
	}

	public Integer getDelta() {
		return _assetPublisherCustomizer.getDelta(_portletPreferences);
	}

	public String getDisplayStyle() {
		if (_displayStyle != null) {
			return _displayStyle;
		}

		_displayStyle = GetterUtil.getString(
			_portletPreferences.getValue(
				"displayStyle",
				_assetPublisherPortletInstanceConfiguration.
					defaultDisplayStyle()));

		return _displayStyle;
	}

	public long getDisplayStyleGroupId() {
		if (_displayStyleGroupId != null) {
			return _displayStyleGroupId;
		}

		_displayStyleGroupId = AssetPublisherUtil.getDisplayStyleGroupId(
			_themeDisplay.getCompanyId(), _themeDisplay.getScopeGroupId(),
			_portletPreferences);

		return _displayStyleGroupId;
	}

	public String[] getDisplayStyles() {
		return _assetPublisherPortletInstanceConfiguration.displayStyles();
	}

	public List<DropdownItem> getDropdownItems(Group group) throws Exception {
		DropdownItemList dropdownItemList = new DropdownItemList();

		List<AssetRendererFactory<?>> assetRendererFactories = ListUtil.sort(
			AssetRendererFactoryRegistryUtil.getAssetRendererFactories(
				_themeDisplay.getCompanyId()),
			new AssetRendererFactoryTypeNameComparator(
				_themeDisplay.getLocale()));

		for (AssetRendererFactory<?> assetRendererFactory :
				assetRendererFactories) {

			if (!assetRendererFactory.isSelectable()) {
				continue;
			}

			Group curGroup;

			if (group.isStagingGroup() &&
				!group.isStagedPortlet(assetRendererFactory.getPortletId())) {

				curGroup = group.getLiveGroup();
			}
			else {
				curGroup = group;
			}

			if (!assetRendererFactory.isSupportsClassTypes()) {
				dropdownItemList.add(
					dropdownItem -> {
						dropdownItem.putData(
							"href",
							_getAssetEntryItemSelectorPortletURL(
								assetRendererFactory, curGroup,
								_DEFAULT_SUBTYPE_SELECTION_ID));
						dropdownItem.putData(
							"title",
							LanguageUtil.format(
								_httpServletRequest, "select-x",
								assetRendererFactory.getTypeName(
									_themeDisplay.getLocale()),
								false));
						dropdownItem.setLabel(
							assetRendererFactory.getTypeName(
								_themeDisplay.getLocale()));
					});

				continue;
			}

			ClassTypeReader classTypeReader =
				assetRendererFactory.getClassTypeReader();

			List<ClassType> assetAvailableClassTypes =
				classTypeReader.getAvailableClassTypes(
					_portal.getCurrentAndAncestorSiteGroupIds(
						curGroup.getGroupId()),
					_themeDisplay.getLocale());

			for (ClassType classType : assetAvailableClassTypes) {
				dropdownItemList.add(
					dropdownItem -> {
						dropdownItem.putData(
							"href",
							_getAssetEntryItemSelectorPortletURL(
								assetRendererFactory, curGroup,
								classType.getClassTypeId()));
						dropdownItem.putData(
							"title",
							LanguageUtil.format(
								_httpServletRequest, "select-x",
								classType.getName(), false));
						dropdownItem.setLabel(classType.getName());
					});
			}
		}

		return ListUtil.sort(
			dropdownItemList,
			new SelectorEntriesLabelComparator(_themeDisplay.getLocale()));
	}

	public LocalizedValuesMap getEmailAssetEntryAddedBody() {
		return _assetPublisherPortletInstanceConfiguration.
			emailAssetEntryAddedBody();
	}

	public LocalizedValuesMap getEmailAssetEntryAddedSubject() {
		return _assetPublisherPortletInstanceConfiguration.
			emailAssetEntryAddedSubject();
	}

	public String[] getExtensions() {
		if (_extensions != null) {
			return _extensions;
		}

		_extensions = _portletPreferences.getValues(
			"extensions", new String[0]);

		return _extensions;
	}

	public String[] getExtensions(AssetRenderer<?> assetRenderer) {
		final String[] supportedConversions =
			assetRenderer.getSupportedConversions();

		if (supportedConversions == null) {
			return getExtensions();
		}

		return ArrayUtil.filter(
			getExtensions(),
			extension -> ArrayUtil.contains(supportedConversions, extension));
	}

	public long[] getGroupIds() {
		if (_groupIds != null) {
			return _groupIds;
		}

		_groupIds = _assetPublisherHelper.getGroupIds(
			_portletPreferences, _themeDisplay.getScopeGroupId(),
			_themeDisplay.getLayout());

		return _groupIds;
	}

	public String getInfoListProviderKey() {
		if (_infoListProviderKey != null) {
			return _infoListProviderKey;
		}

		_infoListProviderKey = GetterUtil.getString(
			_portletPreferences.getValue("infoListProviderKey", null));

		return _infoListProviderKey;
	}

	public String getInfoListProviderLabel() {
		if (Validator.isNull(getInfoListProviderKey())) {
			return StringPool.BLANK;
		}

		InfoCollectionProvider<AssetEntry> infoCollectionProvider =
			_infoItemServiceRegistry.getInfoItemService(
				InfoCollectionProvider.class, getInfoListProviderKey());

		if (infoCollectionProvider == null) {
			return StringPool.BLANK;
		}

		return infoCollectionProvider.getLabel(_themeDisplay.getLocale());
	}

	public String[] getMetadataFields() {
		if (_metadataFields != null) {
			return _metadataFields;
		}

		String metadataFields = _portletPreferences.getValue(
			"metadataFields", null);

		if (metadataFields == null) {
			_metadataFields = new String[] {"author", "modified-date"};
		}
		else {
			_metadataFields = StringUtil.split(metadataFields);
		}

		return _metadataFields;
	}

	public String getOrderByColumn1() {
		if (_orderByColumn1 != null) {
			return _orderByColumn1;
		}

		_orderByColumn1 = GetterUtil.getString(
			_portletPreferences.getValue("orderByColumn1", "modifiedDate"));

		return _orderByColumn1;
	}

	public String getOrderByColumn2() {
		if (_orderByColumn2 != null) {
			return _orderByColumn2;
		}

		_orderByColumn2 = GetterUtil.getString(
			_portletPreferences.getValue("orderByColumn2", "title"));

		return _orderByColumn2;
	}

	public String getOrderByType1() {
		if (_orderByType1 != null) {
			return _orderByType1;
		}

		_orderByType1 = GetterUtil.getString(
			_portletPreferences.getValue("orderByType1", "DESC"));

		return _orderByType1;
	}

	public String getOrderByType2() {
		if (_orderByType2 != null) {
			return _orderByType2;
		}

		_orderByType2 = GetterUtil.getString(
			_portletPreferences.getValue("orderByType2", "ASC"));

		return _orderByType2;
	}

	public String getPaginationType() {
		if (_paginationType != null) {
			return _paginationType;
		}

		_paginationType = GetterUtil.getString(
			_portletPreferences.getValue("paginationType", "none"));

		if (!ArrayUtil.contains(PAGINATION_TYPES, _paginationType)) {
			_paginationType = PAGINATION_TYPE_NONE;
		}

		return _paginationType;
	}

	public String getPortletName() {
		PortletConfig portletConfig =
			(PortletConfig)_httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_CONFIG);

		if (portletConfig == null) {
			return StringPool.BLANK;
		}

		return portletConfig.getPortletName();
	}

	public String getPortletResource() {
		if (_portletResource != null) {
			return _portletResource;
		}

		_portletResource = ParamUtil.getString(
			_httpServletRequest, "portletResource");

		return _portletResource;
	}

	public PortletURL getPortletURL() {
		LiferayPortletResponse liferayPortletResponse =
			_portal.getLiferayPortletResponse(_portletResponse);

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		if (getAssetCategoryId() > 0) {
			portletURL.setParameter(
				"categoryId", String.valueOf(getAssetCategoryId()));
		}

		if (!isPaginationTypeNone()) {
			String redirect = ParamUtil.getString(_portletRequest, "redirect");

			if (Validator.isNull(redirect)) {
				redirect = _portal.getCurrentURL(_portletRequest);
			}

			if (Validator.isNotNull(redirect)) {
				portletURL.setParameter("redirect", redirect);
			}
		}

		return portletURL;
	}

	public long[] getReferencedModelsGroupIds() throws PortalException {

		// Referenced models are asset subtypes, tags or categories that
		// are used to filter assets and can belong to a different scope of
		// the asset they are associated to

		if (_referencedModelsGroupIds != null) {
			return _referencedModelsGroupIds;
		}

		_referencedModelsGroupIds = _portal.getCurrentAndAncestorSiteGroupIds(
			getGroupIds(), true);

		return _referencedModelsGroupIds;
	}

	public int getRSSDelta() {
		if (_rssDelta != null) {
			return _rssDelta;
		}

		_rssDelta = GetterUtil.getInteger(
			_portletPreferences.getValue("rssDelta", StringPool.BLANK),
			SearchContainer.DEFAULT_DELTA);

		return _rssDelta;
	}

	public String getRSSDisplayStyle() {
		if (_rssDisplayStyle != null) {
			return _rssDisplayStyle;
		}

		_rssDisplayStyle = _portletPreferences.getValue(
			"rssDisplayStyle", RSSUtil.DISPLAY_STYLE_ABSTRACT);

		return _rssDisplayStyle;
	}

	public String getRSSFeedType() {
		if (_rssFeedType != null) {
			return _rssFeedType;
		}

		_rssFeedType = _portletPreferences.getValue(
			"rssFeedType", RSSUtil.FEED_TYPE_DEFAULT);

		return _rssFeedType;
	}

	public String getRSSName() {
		if (_rssName != null) {
			return _rssName;
		}

		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		_rssName = _portletPreferences.getValue(
			"rssName", portletDisplay.getTitle());

		return _rssName;
	}

	public Map<Long, List<AssetPublisherAddItemHolder>>
			getScopeAssetPublisherAddItemHolders(int max)
		throws Exception {

		long[] groupIds = getGroupIds();

		if (groupIds.length == 0) {
			return Collections.emptyMap();
		}

		Map<Long, List<AssetPublisherAddItemHolder>>
			scopeAssetPublisherAddItemHolders = new HashMap<>();

		LiferayPortletRequest liferayPortletRequest =
			_portal.getLiferayPortletRequest(_portletRequest);
		LiferayPortletResponse liferayPortletResponse =
			_portal.getLiferayPortletResponse(_portletResponse);

		for (long groupId : groupIds) {
			List<AssetPublisherAddItemHolder> assetPublisherAddItemHolders =
				_assetHelper.getAssetPublisherAddItemHolders(
					liferayPortletRequest, liferayPortletResponse, groupId,
					getClassNameIds(), getClassTypeIds(),
					getAllAssetCategoryIds(), getAllAssetTagNames(),
					_themeDisplay.getURLCurrent());

			if (ListUtil.isNotEmpty(assetPublisherAddItemHolders)) {
				scopeAssetPublisherAddItemHolders.put(
					groupId, assetPublisherAddItemHolders);
			}

			if (scopeAssetPublisherAddItemHolders.size() > max) {
				break;
			}
		}

		return scopeAssetPublisherAddItemHolders;
	}

	public List<DropdownItem> getScopeDropdownItems(PortletURL addScopeURL)
		throws PortalException {

		DropdownItemList dropdownItemList = new DropdownItemList();

		Set<Group> availableGroups = new HashSet<>();

		Company company = _themeDisplay.getCompany();

		availableGroups.add(company.getGroup());

		availableGroups.add(_themeDisplay.getScopeGroup());

		Layout layout = _themeDisplay.getLayout();

		if (layout.hasScopeGroup()) {
			availableGroups.add(layout.getScopeGroup());
		}

		for (Group group : availableGroups) {
			if (ArrayUtil.contains(getGroupIds(), group.getGroupId())) {
				continue;
			}

			dropdownItemList.add(
				dropdownItem -> {
					dropdownItem.putData("action", "addScope");
					dropdownItem.putData(
						"url",
						PortletURLBuilder.create(
							addScopeURL
						).setParameter(
							"groupId", group.getGroupId()
						).setParameter(
							"title",
							group.getScopeDescriptiveName(_themeDisplay)
						).buildString());
					dropdownItem.setLabel(
						group.getScopeDescriptiveName(_themeDisplay));
				});
		}

		ItemSelector itemSelector =
			(ItemSelector)_httpServletRequest.getAttribute(
				AssetPublisherWebKeys.ITEM_SELECTOR);
		String itemSelectorEventName =
			StringPool.UNDERLINE + HtmlUtil.escapeJS(getPortletResource()) +
				"_selectSite";

		GroupItemSelectorCriterion groupItemSelectorCriterion =
			new GroupItemSelectorCriterion(layout.isPrivateLayout());

		groupItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new GroupItemSelectorReturnType());
		groupItemSelectorCriterion.setIncludeChildSites(true);
		groupItemSelectorCriterion.setIncludeLayoutScopes(true);
		groupItemSelectorCriterion.setIncludeMySites(false);
		groupItemSelectorCriterion.setIncludeParentSites(true);
		groupItemSelectorCriterion.setIncludeRecentSites(false);
		groupItemSelectorCriterion.setIncludeSitesThatIAdminister(true);

		dropdownItemList.add(
			dropdownItem -> {
				dropdownItem.putData("action", "openScopeSelector");
				dropdownItem.putData("eventName", itemSelectorEventName);
				dropdownItem.putData(
					"id", HtmlUtil.escapeJS(getPortletResource()));

				Group group = _assetPublisherHelper.getItemSelectorScopeGroup(
					layout.getGroup());

				dropdownItem.putData(
					"url",
					PortletURLBuilder.create(
						itemSelector.getItemSelectorURL(
							RequestBackedPortletURLFactoryUtil.create(
								_portletRequest),
							group, group.getGroupId(), itemSelectorEventName,
							groupItemSelectorCriterion)
					).setPortletResource(
						getPortletResource()
					).setParameter(
						"groupId", layout.getGroupId()
					).setParameter(
						"plid", layout.getPlid()
					).buildString());

				dropdownItem.setLabel(
					LanguageUtil.get(
						_httpServletRequest, "other-site-or-asset-library"));
			});

		return dropdownItemList;
	}

	public SearchContainer<AssetEntry> getSearchContainer() {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		SearchContainer<AssetEntry> searchContainer = new SearchContainer(
			_portletRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM,
			getDelta(), getPortletURL(), null, null);

		if (!isPaginationTypeNone()) {
			searchContainer.setDelta(getDelta());
			searchContainer.setDeltaConfigurable(false);
		}

		_searchContainer = searchContainer;

		return _searchContainer;
	}

	public String getSelectAssetListEventName() {
		String portletNamespace = _portal.getPortletNamespace(
			AssetPublisherPortletKeys.ASSET_PUBLISHER);

		return portletNamespace + "selectAssetList";
	}

	public Map<String, Object> getSelectCollectionProps() throws Exception {
		AssetListEntry assetListEntry = fetchAssetListEntry();

		return HashMapBuilder.<String, Object>put(
			"assetListEntryId",
			() -> {
				if (assetListEntry != null) {
					return assetListEntry.getAssetListEntryId();
				}

				return 0;
			}
		).put(
			"clearButtonEnabled",
			() -> {
				if ((assetListEntry != null) ||
					Validator.isNotNull(getInfoListProviderKey())) {

					return true;
				}

				return false;
			}
		).put(
			"defaultTitle",
			LanguageUtil.get(_httpServletRequest, "no-collection-selected")
		).put(
			"infoListProviderKey",
			() -> {
				if (Validator.isNotNull(getInfoListProviderKey())) {
					return getInfoListProviderKey();
				}

				return StringPool.BLANK;
			}
		).put(
			"selectEventName", getSelectAssetListEventName()
		).put(
			"title",
			() -> {
				if (assetListEntry != null) {
					return HtmlUtil.escape(assetListEntry.getTitle());
				}

				if (Validator.isNotNull(getInfoListProviderKey())) {
					return getInfoListProviderLabel();
				}

				return LanguageUtil.get(
					_httpServletRequest, "no-collection-selected");
			}
		).put(
			"url", getAssetListSelectorURL()
		).build();
	}

	public String getSelectionStyle() {
		if (_selectionStyle != null) {
			return _selectionStyle;
		}

		try (SafeCloseable safeCloseable =
				FF_LPD_39304_CompanyTemporarySwapper.
					setCompanyIdWithSafeCloseable(
						_themeDisplay.getCompanyId())) {

			_selectionStyle = GetterUtil.getString(
				_portletPreferences.getValue("selectionStyle", null),
				AssetPublisherSelectionStyleConfigurationUtil.
					defaultSelectionStyle());
		}

		return _selectionStyle;
	}

	public String getSocialBookmarksDisplayStyle() {
		if (_socialBookmarksDisplayStyle != null) {
			return _socialBookmarksDisplayStyle;
		}

		_socialBookmarksDisplayStyle = _portletPreferences.getValue(
			"socialBookmarksDisplayStyle", null);

		return _socialBookmarksDisplayStyle;
	}

	public String getSocialBookmarksTypes() {
		if (_socialBookmarksTypes == null) {
			_socialBookmarksTypes = GetterUtil.getString(
				_portletPreferences.getValue("socialBookmarksTypes", null));
		}

		return _socialBookmarksTypes;
	}

	public String getTagSelectorURL() {
		AssetTagsItemSelectorCriterion assetTagsItemSelectorCriterion =
			new AssetTagsItemSelectorCriterion();

		assetTagsItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new AssetTagsItemSelectorReturnType());
		assetTagsItemSelectorCriterion.setGroupIds(getGroupIds());
		assetTagsItemSelectorCriterion.setMultiSelection(true);

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
				_portletResponse.getNamespace() + "selectTag",
				assetTagsItemSelectorCriterion));
	}

	public List<Long> getVocabularyIds() throws PortalException {
		long[] groupIds = _portal.getCurrentAndAncestorSiteGroupIds(
			getReferencedModelsGroupIds());

		List<AssetVocabulary> assetVocabularies = ListUtil.filter(
			AssetVocabularyServiceUtil.getGroupsVocabularies(groupIds),
			vocabulary -> {
				long[] classNameIds = vocabulary.getSelectedClassNameIds();

				for (long classNameId : classNameIds) {
					if (classNameId == 0) {
						return true;
					}

					if (classNameId == _portal.getClassNameId(
							FileEntry.class.getName())) {

						classNameId = _portal.getClassNameId(
							DLFileEntry.class.getName());
					}

					AssetRendererFactory<?> assetRendererFactory =
						AssetRendererFactoryRegistryUtil.
							getAssetRendererFactoryByClassNameId(classNameId);

					if (assetRendererFactory == null) {
						if (_log.isDebugEnabled()) {
							_log.debug(
								"Unable to get asset renderer factory for " +
									"class name ID " + classNameId);
						}

						continue;
					}

					if (assetRendererFactory.isSelectable()) {
						return true;
					}
				}

				return false;
			});

		return ListUtil.toList(
			assetVocabularies, AssetVocabulary.VOCABULARY_ID_ACCESSOR);
	}

	public AssetEntry incrementViewCounter(AssetEntry assetEntry)
		throws PortalException {

		// Dynamically created asset entries are never persisted so incrementing
		// the view counter breaks

		if ((assetEntry == null) || assetEntry.isNew() ||
			!assetEntry.isVisible()) {

			return assetEntry;
		}

		if (isEnablePermissions()) {
			return AssetEntryServiceUtil.incrementViewCounter(
				assetEntry.getCompanyId(), assetEntry.getClassName(),
				assetEntry.getClassPK());
		}

		return AssetEntryLocalServiceUtil.incrementViewCounter(
			assetEntry.getCompanyId(), _themeDisplay.getUserId(),
			assetEntry.getClassName(), assetEntry.getClassPK());
	}

	public Boolean isAnyAssetType() {
		if (_anyAssetType != null) {
			return _anyAssetType;
		}

		_anyAssetType = GetterUtil.getBoolean(
			_portletPreferences.getValue("anyAssetType", null), true);

		return _anyAssetType;
	}

	public boolean isAssetLinkBehaviorShowFullContent() {
		String assetLinkBehavior = getAssetLinkBehavior();

		return assetLinkBehavior.equals("showFullContent");
	}

	public boolean isAssetLinkBehaviorViewInPortlet() {
		String assetLinkBehavior = getAssetLinkBehavior();

		return assetLinkBehavior.equals("viewInPortlet");
	}

	public boolean isDefaultAssetPublisher() {
		if (_defaultAssetPublisher != null) {
			return _defaultAssetPublisher;
		}

		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		_defaultAssetPublisher =
			_assetPublisherWebHelper.isDefaultAssetPublisher(
				_themeDisplay.getLayout(), portletDisplay.getId(),
				getPortletResource());

		return _defaultAssetPublisher;
	}

	public boolean isEnableCommentRatings() {
		if (_enableCommentRatings != null) {
			return _enableCommentRatings;
		}

		_enableCommentRatings = GetterUtil.getBoolean(
			_portletPreferences.getValue("enableCommentRatings", null));

		return _enableCommentRatings;
	}

	public boolean isEnableComments() {
		if (_enableComments != null) {
			return _enableComments;
		}

		_enableComments = GetterUtil.getBoolean(
			_portletPreferences.getValue("enableComments", null));

		return _enableComments;
	}

	public Boolean isEnableConversions() {
		if (_enableConversions != null) {
			return _enableConversions;
		}

		_enableConversions =
			isOpenOfficeServerEnabled() &&
			ArrayUtil.isNotEmpty(getExtensions());

		return _enableConversions;
	}

	public boolean isEnabledAutoscroll() {
		return _assetPublisherWebConfiguration.enableAutoscroll();
	}

	public boolean isEnableFlags() {
		if (_enableFlags != null) {
			return _enableFlags;
		}

		_enableFlags = GetterUtil.getBoolean(
			_portletPreferences.getValue("enableFlags", null));

		return _enableFlags;
	}

	public Boolean isEnablePermissions() {
		return _assetPublisherCustomizer.isEnablePermissions(
			_httpServletRequest);
	}

	public boolean isEnablePrint() {
		if (_enablePrint != null) {
			return _enablePrint;
		}

		_enablePrint = GetterUtil.getBoolean(
			_portletPreferences.getValue("enablePrint", null));

		return _enablePrint;
	}

	public boolean isEnableRatings() {
		if (_enableRatings != null) {
			return _enableRatings;
		}

		_enableRatings = GetterUtil.getBoolean(
			_portletPreferences.getValue("enableRatings", null));

		return _enableRatings;
	}

	public boolean isEnableRelatedAssets() {
		if (_enableRelatedAssets != null) {
			return _enableRelatedAssets;
		}

		_enableRelatedAssets = GetterUtil.getBoolean(
			_portletPreferences.getValue("enableRelatedAssets", null),
			_isShowRelatedAssets());

		return _enableRelatedAssets;
	}

	public boolean isEnableRSS() {
		if (_enableRSS != null) {
			return _enableRSS;
		}

		_enableRSS = GetterUtil.getBoolean(
			_portletPreferences.getValue("enableRss", null));

		return _enableRSS;
	}

	public boolean isEnableSetAsDefaultAssetPublisher() {
		Layout layout = _themeDisplay.getLayout();

		if (layout.isTypeAssetDisplay()) {
			return false;
		}

		if (layout.isTypeContent()) {
			Layout publishedLayout = LayoutLocalServiceUtil.fetchLayout(
				layout.getClassPK());

			if (publishedLayout == null) {
				return true;
			}

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				LayoutPageTemplateEntryLocalServiceUtil.
					fetchLayoutPageTemplateEntryByPlid(
						publishedLayout.getPlid());

			if (layoutPageTemplateEntry != null) {
				return false;
			}
		}

		String rootPortletId = PortletIdCodec.decodePortletName(
			getPortletResource());

		return rootPortletId.equals(AssetPublisherPortletKeys.ASSET_PUBLISHER);
	}

	public boolean isEnableSubscriptions() {
		if (_enableSubscriptions != null) {
			return _enableSubscriptions;
		}

		_enableSubscriptions = GetterUtil.getBoolean(
			_portletPreferences.getValue("enableSubscriptions", null));

		return _enableSubscriptions;
	}

	public boolean isEnableTagBasedNavigation() {
		if (_enableTagBasedNavigation != null) {
			return _enableTagBasedNavigation;
		}

		_enableTagBasedNavigation = GetterUtil.getBoolean(
			_portletPreferences.getValue("enableTagBasedNavigation", null));

		return _enableTagBasedNavigation;
	}

	public boolean isEnableViewCountIncrement() {
		if (_enableViewCountIncrement != null) {
			return _enableViewCountIncrement;
		}

		_enableViewCountIncrement = GetterUtil.getBoolean(
			_portletPreferences.getValue("enableViewCountIncrement", null));

		return _enableViewCountIncrement;
	}

	public boolean isExcludeZeroViewCount() {
		if (_excludeZeroViewCount != null) {
			return _excludeZeroViewCount;
		}

		_excludeZeroViewCount = GetterUtil.getBoolean(
			_portletPreferences.getValue("excludeZeroViewCount", null));

		return _excludeZeroViewCount;
	}

	public boolean isMergeURLTags() {
		if (_mergeURLTags != null) {
			return _mergeURLTags;
		}

		_mergeURLTags = GetterUtil.getBoolean(
			_portletPreferences.getValue("mergeUrlTags", null), true);

		return _mergeURLTags;
	}

	public boolean isOpenOfficeServerEnabled() {
		return DocumentConversionUtil.isEnabled();
	}

	public boolean isOrderingByTitleEnabled() {
		return _assetPublisherCustomizer.isOrderingByTitleEnabled(
			_httpServletRequest);
	}

	public boolean isPaginationTypeNone() {
		return Objects.equals(getPaginationType(), PAGINATION_TYPE_NONE);
	}

	public boolean isPaginationTypeSelected(String paginationType) {
		String curPaginationType = getPaginationType();

		return curPaginationType.equals(paginationType);
	}

	public boolean isSearchWithIndex() {
		return _assetPublisherWebConfiguration.searchWithIndex();
	}

	public boolean isSelectionStyleAssetList() {
		if (Objects.equals(
				getSelectionStyle(),
				AssetPublisherSelectionStyleConstants.TYPE_ASSET_LIST) ||
			Objects.equals(
				getSelectionStyle(),
				AssetPublisherSelectionStyleConstants.
					TYPE_ASSET_LIST_PROVIDER)) {

			return true;
		}

		return false;
	}

	public boolean isSelectionStyleDynamic() {
		return Objects.equals(
			getSelectionStyle(),
			AssetPublisherSelectionStyleConstants.TYPE_DYNAMIC);
	}

	public boolean isSelectionStyleManual() {
		return Objects.equals(
			getSelectionStyle(),
			AssetPublisherSelectionStyleConstants.TYPE_MANUAL);
	}

	public boolean isShowAddContentButton() {
		if (_showAddContentButton != null) {
			return _showAddContentButton;
		}

		_showAddContentButton = GetterUtil.getBoolean(
			_portletPreferences.getValue("showAddContentButton", null), true);

		return _showAddContentButton;
	}

	public Boolean isShowAssetTitle() {
		if (_showAssetTitle != null) {
			return _showAssetTitle;
		}

		_showAssetTitle = GetterUtil.getBoolean(
			_portletPreferences.getValue("showAssetTitle", null), true);

		return _showAssetTitle;
	}

	public boolean isShowAuthor() {
		if (_showAuthor != null) {
			return _showAuthor;
		}

		if (ArrayUtil.contains(getMetadataFields(), "author")) {
			_showAuthor = true;

			return _showAuthor;
		}

		_showAuthor = false;

		return _showAuthor;
	}

	public Boolean isShowAvailableLocales() {
		if (_showAvailableLocales != null) {
			return _showAvailableLocales;
		}

		_showAvailableLocales = GetterUtil.getBoolean(
			_portletPreferences.getValue("showAvailableLocales", null));

		return _showAvailableLocales;
	}

	public boolean isShowCategories() {
		if (_showCategories != null) {
			return _showCategories;
		}

		if (ArrayUtil.contains(getMetadataFields(), "categories")) {
			_showCategories = true;

			return _showCategories;
		}

		_showCategories = false;

		return _showCategories;
	}

	public Boolean isShowContextLink() {
		if (_showContextLink != null) {
			return _showContextLink;
		}

		_showContextLink = GetterUtil.getBoolean(
			_portletPreferences.getValue("showContextLink", null), true);

		return _showContextLink;
	}

	public Boolean isShowContextLink(long groupId, String portletId)
		throws PortalException {

		if (_showContextLink != null) {
			return _showContextLink;
		}

		_showContextLink = isShowContextLink();

		if (_showContextLink &&
			(_portal.getPlidFromPortletId(groupId, portletId) == 0)) {

			_showContextLink = false;
		}

		return _showContextLink;
	}

	public boolean isShowCreateDate() {
		if (_showCreateDate != null) {
			return _showCreateDate;
		}

		if (ArrayUtil.contains(getMetadataFields(), "create-date")) {
			_showCreateDate = true;

			return _showCreateDate;
		}

		_showCreateDate = false;

		return _showCreateDate;
	}

	public boolean isShowEnableAddContentButton() {
		return _assetPublisherCustomizer.isShowEnableAddContentButton(
			_httpServletRequest);
	}

	public Boolean isShowEnablePermissions() {
		if (_assetPublisherWebConfiguration.searchWithIndex()) {
			return false;
		}

		return _assetPublisherWebConfiguration.permissionCheckingConfigurable();
	}

	public boolean isShowEnableRelatedAssets() {
		return _assetPublisherCustomizer.isShowEnableRelatedAssets(
			_httpServletRequest);
	}

	public boolean isShowExpirationDate() {
		if (_showExpirationDate != null) {
			return _showExpirationDate;
		}

		if (ArrayUtil.contains(getMetadataFields(), "expiration-date")) {
			_showExpirationDate = true;

			return _showExpirationDate;
		}

		_showExpirationDate = false;

		return _showExpirationDate;
	}

	public boolean isShowExtraInfo() {
		if (_showExtraInfo != null) {
			return _showExtraInfo;
		}

		_showExtraInfo = GetterUtil.getBoolean(
			_portletPreferences.getValue("showExtraInfo", null), true);

		return _showExtraInfo;
	}

	public boolean isShowMetadataDescriptions() {
		if (_showMetadataDescriptions != null) {
			return _showMetadataDescriptions;
		}

		_showMetadataDescriptions = GetterUtil.getBoolean(
			_portletPreferences.getValue("showMetadataDescriptions", null),
			true);

		return _showMetadataDescriptions;
	}

	public boolean isShowModifiedDate() {
		if (_showModifiedDate != null) {
			return _showModifiedDate;
		}

		if (ArrayUtil.contains(getMetadataFields(), "modified-date")) {
			_showModifiedDate = true;

			return _showModifiedDate;
		}

		_showModifiedDate = false;

		return _showModifiedDate;
	}

	public boolean isShowOnlyLayoutAssets() {
		if (_showOnlyLayoutAssets != null) {
			return _showOnlyLayoutAssets;
		}

		_showOnlyLayoutAssets = GetterUtil.getBoolean(
			_portletPreferences.getValue("showOnlyLayoutAssets", null));

		return _showOnlyLayoutAssets;
	}

	public boolean isShowPriority() {
		if (_showPriority != null) {
			return _showPriority;
		}

		if (ArrayUtil.contains(getMetadataFields(), "priority")) {
			_showPriority = true;

			return _showPriority;
		}

		_showPriority = false;

		return _showPriority;
	}

	public boolean isShowPublishDate() {
		if (_showPublishDate != null) {
			return _showPublishDate;
		}

		if (ArrayUtil.contains(getMetadataFields(), "publish-date")) {
			_showPublishDate = true;

			return _showPublishDate;
		}

		_showPublishDate = false;

		return _showPublishDate;
	}

	public boolean isShowSubtypeFieldsFilter() {
		return _assetPublisherCustomizer.isShowSubtypeFieldsFilter(
			_httpServletRequest);
	}

	public boolean isShowTags() {
		if (_showTags != null) {
			return _showTags;
		}

		if (ArrayUtil.contains(getMetadataFields(), "tags")) {
			_showTags = true;

			return _showTags;
		}

		_showTags = false;

		return _showTags;
	}

	public boolean isShowViewCount() {
		if (_showViewCount != null) {
			return _showViewCount;
		}

		if (ArrayUtil.contains(getMetadataFields(), "view-count")) {
			_showViewCount = true;

			return _showViewCount;
		}

		_showViewCount = false;

		return _showViewCount;
	}

	public boolean isSubscriptionEnabled() throws PortalException {
		String portletName = getPortletName();

		if (Objects.equals(
				portletName, AssetPublisherPortletKeys.HIGHEST_RATED_ASSETS) ||
			Objects.equals(
				portletName, AssetPublisherPortletKeys.MOST_VIEWED_ASSETS) ||
			Objects.equals(
				portletName, AssetPublisherPortletKeys.RECENT_CONTENT) ||
			Objects.equals(
				portletName, AssetPublisherPortletKeys.RELATED_ASSETS) ||
			!_assetPublisherWebHelper.getEmailAssetEntryAddedEnabled(
				_portletPreferences)) {

			return false;
		}

		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		return PortletPermissionUtil.contains(
			_themeDisplay.getPermissionChecker(), 0, _themeDisplay.getLayout(),
			portletDisplay.getId(), ActionKeys.SUBSCRIBE, false, false);
	}

	public boolean isSubtypeFieldsFilterEnabled() {
		if (_subtypeFieldsFilterEnabled != null) {
			return _subtypeFieldsFilterEnabled;
		}

		_subtypeFieldsFilterEnabled = GetterUtil.getBoolean(
			_portletPreferences.getValue(
				"subtypeFieldsFilterEnabled", Boolean.FALSE.toString()));

		return _subtypeFieldsFilterEnabled;
	}

	public void setDisplayStyle(String displayStyle) {
		_displayStyle = displayStyle;
	}

	public void setLayoutAssetEntry(AssetEntry assetEntry)
		throws PortalException {

		String defaultAssetPublisherPortletId =
			_assetPublisherWebHelper.getDefaultAssetPublisherId(
				_themeDisplay.getLayout());

		if (!isDefaultAssetPublisher() &&
			Validator.isNotNull(defaultAssetPublisherPortletId) &&
			PortletPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(), _themeDisplay.getLayout(),
				defaultAssetPublisherPortletId, ActionKeys.VIEW)) {

			return;
		}

		if (_httpServletRequest.getAttribute(WebKeys.LAYOUT_ASSET_ENTRY) ==
				null) {

			_httpServletRequest.setAttribute(
				WebKeys.LAYOUT_ASSET_ENTRY, assetEntry);
		}

		if (assetEntry == null) {
			return;
		}

		LinkedAssetEntryIdsUtil.addLinkedAssetEntryId(
			_httpServletRequest, assetEntry.getEntryId());
	}

	public void setPageKeywords() {
		if (getAssetCategoryId() > 0) {
			AssetCategory assetCategory =
				AssetCategoryLocalServiceUtil.fetchAssetCategory(
					getAssetCategoryId());

			if (assetCategory != null) {
				_portal.setPageKeywords(
					HtmlUtil.escape(
						assetCategory.getTitle(_themeDisplay.getLocale())),
					_httpServletRequest);
			}
		}

		if (Validator.isNotNull(getAssetTagName())) {
			_portal.setPageKeywords(getAssetTagName(), _httpServletRequest);
		}
	}

	public void setSelectionStyle(String selectionStyle) {
		_selectionStyle = selectionStyle;
	}

	private void _configureSubtypeFieldFilter(
			AssetEntryQuery assetEntryQuery, Locale locale)
		throws Exception {

		long[] classNameIds = getClassNameIds();
		long[] classTypeIds = getClassTypeIds();

		if (!isSubtypeFieldsFilterEnabled() || (classNameIds.length != 1) ||
			(classTypeIds.length != 1) ||
			Validator.isNull(getDDMStructureFieldName()) ||
			Validator.isNull(getDDMStructureFieldValue())) {

			return;
		}

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.
				getAssetRendererFactoryByClassNameId(classNameIds[0]);

		ClassTypeReader classTypeReader =
			assetRendererFactory.getClassTypeReader();

		ClassType classType = classTypeReader.getClassType(
			classTypeIds[0], locale);

		ClassTypeField classTypeField = classType.getClassTypeField(
			getDDMStructureFieldName());

		assetEntryQuery.setAttribute(
			"ddmStructureFieldName",
			_assetPublisherWebHelper.encodeName(
				classTypeField.getClassTypeId(),
				classTypeField.getFieldReference(), locale));

		assetEntryQuery.setAttribute(
			"ddmStructureFieldValue", getDDMStructureFieldValue());
	}

	private List<AssetCategory> _filterAssetCategories(long[] categoryIds) {
		List<AssetCategory> filteredAssetCategories = new ArrayList<>();

		for (long categoryId : categoryIds) {
			AssetCategory category =
				AssetCategoryLocalServiceUtil.fetchAssetCategory(categoryId);

			if (category == null) {
				continue;
			}

			filteredAssetCategories.add(category);
		}

		return filteredAssetCategories;
	}

	private List<AssetEntry> _filterAssetCategoriesAssetEntries(
		List<AssetEntry> assetEntries, long[] assetCategoryIds) {

		return TransformUtil.transform(
			assetEntries,
			assetEntry -> {
				if (ArrayUtil.containsAll(
						assetEntry.getCategoryIds(), assetCategoryIds)) {

					return assetEntry;
				}

				return null;
			});
	}

	private List<AssetEntry> _filterAssetTagNamesAssetEntries(
		List<AssetEntry> assetEntries, String[] assetTagNames) {

		List<AssetEntry> filteredAssetEntries = new ArrayList<>();

		for (AssetEntry assetEntry : assetEntries) {
			List<AssetTag> assetTags = assetEntry.getTags();

			String[] assetEntryAssetTagNames = new String[assetTags.size()];

			for (int i = 0; i < assetTags.size(); i++) {
				AssetTag assetTag = assetTags.get(i);

				assetEntryAssetTagNames[i] = assetTag.getName();
			}

			if (ArrayUtil.containsAll(assetEntryAssetTagNames, assetTagNames)) {
				filteredAssetEntries.add(assetEntry);
			}
		}

		return filteredAssetEntries;
	}

	private String _getAssetEntryItemSelectorPortletURL(
		AssetRendererFactory<?> assetRendererFactory, Group scopeGroup,
		long subtypeSelectionId) {

		PortletURL portletURL = assetRendererFactory.getItemSelectorURL(
			_portal.getLiferayPortletRequest(_portletRequest),
			_portal.getLiferayPortletResponse(_portletResponse),
			subtypeSelectionId, _portletResponse.getNamespace() + "selectAsset",
			scopeGroup, true, 0);

		if (portletURL != null) {
			return portletURL.toString();
		}

		AssetEntryItemSelectorCriterion assetEntryItemSelectorCriterion =
			new AssetEntryItemSelectorCriterion();

		assetEntryItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new AssetEntryItemSelectorReturnType());
		assetEntryItemSelectorCriterion.setGroupId(
			_themeDisplay.getScopeGroupId());
		assetEntryItemSelectorCriterion.setShowNonindexable(true);
		assetEntryItemSelectorCriterion.setShowScheduled(true);
		assetEntryItemSelectorCriterion.setSubtypeSelectionId(
			subtypeSelectionId);
		assetEntryItemSelectorCriterion.setTypeSelection(
			assetRendererFactory.getClassName());

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_portletRequest),
				scopeGroup, _themeDisplay.getScopeGroupId(),
				_portletResponse.getNamespace() + "selectAsset",
				assetEntryItemSelectorCriterion));
	}

	private Comparator<ClassType> _getClassTypeComparator() {
		if (_classTypeComparator != null) {
			return _classTypeComparator;
		}

		_classTypeComparator = new ClassTypeNameComparator(
			_themeDisplay.getLocale());

		return _classTypeComparator;
	}

	private InfoPage<AssetEntry> _getInfoPage(
			SearchContainer<AssetEntry> searchContainer)
		throws Exception {

		if (isSelectionStyleManual()) {
			return _assetPublisherHelper.getInfoPage(
				_portletRequest, _portletPreferences,
				_themeDisplay.getPermissionChecker(), getGroupIds(),
				getAllAssetCategoryIds(), getAllAssetTagNames(), false,
				isEnablePermissions(), searchContainer.getStart(),
				searchContainer.getEnd());
		}

		if (!isSelectionStyleAssetList()) {
			return InfoPage.of(Collections.emptyList());
		}

		AssetListEntry assetListEntry = fetchAssetListEntry();

		if (assetListEntry != null) {
			long[][] assetCategoryIds = null;
			String[][] assetTagNames = null;

			if (ArrayUtil.isNotEmpty(getAllAssetCategoryIds())) {
				assetCategoryIds = new long[][] {getAllAssetCategoryIds()};
			}

			if (ArrayUtil.isNotEmpty(getAllAssetTagNames())) {
				assetTagNames = new String[][] {getAllAssetTagNames()};
			}

			return _assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, _getSegmentsEntryIds(assetListEntry),
				assetCategoryIds, assetTagNames, StringPool.BLANK,
				_getSegmentsAnonymousUserId(), searchContainer.getStart(),
				searchContainer.getEnd());
		}

		if (Validator.isNull(getInfoListProviderKey())) {
			return InfoPage.of(Collections.emptyList());
		}

		InfoCollectionProvider<AssetEntry> infoCollectionProvider =
			_infoItemServiceRegistry.getInfoItemService(
				InfoCollectionProvider.class, getInfoListProviderKey());

		if (infoCollectionProvider == null) {
			return InfoPage.of(Collections.emptyList());
		}

		if (ArrayUtil.isEmpty(getAllAssetCategoryIds()) &&
			ArrayUtil.isEmpty(getAllAssetTagNames())) {

			CollectionQuery collectionQuery = new CollectionQuery();

			collectionQuery.setPagination(
				Pagination.of(
					searchContainer.getEnd(), searchContainer.getStart()));

			return infoCollectionProvider.getCollectionInfoPage(
				collectionQuery);
		}

		List<AssetEntry> filteredAssetEntries = new ArrayList<>();

		int totalCount = Integer.MAX_VALUE;

		for (int start = 0;
			 (filteredAssetEntries.size() < searchContainer.getDelta()) &&
			 ((start + _INFO_COLLECTION_PROVIDER_DELTA) <= totalCount);
			 start += _INFO_COLLECTION_PROVIDER_DELTA) {

			CollectionQuery collectionQuery = new CollectionQuery();

			collectionQuery.setPagination(
				Pagination.of(start + _INFO_COLLECTION_PROVIDER_DELTA, start));

			InfoPage<AssetEntry> infoPage =
				infoCollectionProvider.getCollectionInfoPage(collectionQuery);

			totalCount = infoPage.getTotalCount();

			List<AssetEntry> assetEntries =
				(List<AssetEntry>)infoPage.getPageItems();

			if (!assetEntries.isEmpty() &&
				ArrayUtil.isNotEmpty(getAllAssetCategoryIds())) {

				assetEntries = _filterAssetCategoriesAssetEntries(
					assetEntries, getAllAssetCategoryIds());
			}

			if (!assetEntries.isEmpty() &&
				ArrayUtil.isNotEmpty(getAllAssetTagNames())) {

				assetEntries = _filterAssetTagNamesAssetEntries(
					assetEntries, getAllAssetTagNames());
			}

			filteredAssetEntries.addAll(assetEntries);
		}

		return InfoPage.of(
			ListUtil.subList(
				filteredAssetEntries, 0, searchContainer.getDelta()),
			Pagination.of(searchContainer.getEnd(), searchContainer.getStart()),
			totalCount);
	}

	private String _getSegmentsAnonymousUserId() {
		return GetterUtil.getString(
			_portletRequest.getAttribute(
				SegmentsWebKeys.SEGMENTS_ANONYMOUS_USER_ID));
	}

	private long[] _getSegmentsEntryIds(AssetListEntry assetListEntry) {
		return _segmentsEntryRetriever.getSegmentsEntryIds(
			_themeDisplay.getScopeGroupId(), _themeDisplay.getUserId(),
			_requestContextMapper.map(
				_portal.getOriginalServletRequest(
					_portal.getHttpServletRequest(_portletRequest))),
			ArrayUtil.toLongArray(
				TransformUtil.transform(
					_assetListEntrySegmentsEntryRelLocalService.
						getAssetListEntrySegmentsEntryRels(
							assetListEntry.getAssetListEntryId(),
							QueryUtil.ALL_POS, QueryUtil.ALL_POS),
					assetListEntrySegmentsEntryRel ->
						assetListEntrySegmentsEntryRel.getSegmentsEntryId())));
	}

	private boolean _isShowRelatedAssets() {
		if (_showRelatedAssets != null) {
			return _showRelatedAssets;
		}

		_showRelatedAssets = ParamUtil.getBoolean(
			_httpServletRequest, "showRelatedAssets");

		return _showRelatedAssets;
	}

	private String[] _normalizeAssetTagNames(String[] assetTagNames) {
		if (ArrayUtil.isEmpty(assetTagNames)) {
			return assetTagNames;
		}

		for (int i = 0; i < assetTagNames.length; i++) {
			assetTagNames[i] = StringUtil.trim(assetTagNames[i]);
		}

		return assetTagNames;
	}

	private void _setDDMStructure() throws Exception {
		_ddmStructureDisplayFieldValue = StringPool.BLANK;
		_ddmStructureFieldLabel = StringPool.BLANK;
		_ddmStructureFieldName = StringPool.BLANK;
		_ddmStructureFieldValue = null;

		long[] classNameIds = getClassNameIds();
		long[] classTypeIds = getClassTypeIds();

		if (!isSubtypeFieldsFilterEnabled() || (classNameIds.length != 1) ||
			(classTypeIds.length != 1)) {

			return;
		}

		_ddmStructureDisplayFieldValue = ParamUtil.getString(
			_httpServletRequest, "ddmStructureDisplayFieldValue",
			_portletPreferences.getValue(
				"ddmStructureDisplayFieldValue", StringPool.BLANK));

		_ddmStructureFieldName = ParamUtil.getString(
			_httpServletRequest, "ddmStructureFieldName",
			_portletPreferences.getValue(
				"ddmStructureFieldName", StringPool.BLANK));
		_ddmStructureFieldValue = ParamUtil.getString(
			_httpServletRequest, "ddmStructureFieldValue",
			_portletPreferences.getValue(
				"ddmStructureFieldValue", StringPool.BLANK));

		if (Validator.isNotNull(_ddmStructureFieldName) &&
			Validator.isNotNull(_ddmStructureFieldValue)) {

			AssetRendererFactory<?> assetRendererFactory =
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassNameId(classNameIds[0]);

			ClassTypeReader classTypeReader =
				assetRendererFactory.getClassTypeReader();

			ClassType classType = classTypeReader.getClassType(
				classTypeIds[0], _themeDisplay.getLocale());

			ClassTypeField classTypeField = classType.getClassTypeField(
				_ddmStructureFieldName);

			_ddmStructureFieldLabel = classTypeField.getLabel();
		}
	}

	private static final int _DEFAULT_SUBTYPE_SELECTION_ID = -1;

	private static final int _INFO_COLLECTION_PROVIDER_DELTA = 20;

	private static final Log _log = LogFactoryUtil.getLog(
		AssetPublisherDisplayContext.class);

	private static final ServiceTrackerMap<String, List<AssetEntryAction<?>>>
		_stringListServiceTrackerMap;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			AssetPublisherDisplayContext.class);

		_stringListServiceTrackerMap =
			ServiceTrackerMapFactory.openMultiValueMap(
				bundle.getBundleContext(),
				(Class<AssetEntryAction<?>>)(Class<?>)AssetEntryAction.class,
				null, new PropertyServiceReferenceMapper<>("model.class.name"),
				Collections.reverseOrder(
					new PropertyServiceReferenceComparator<>(
						"asset.entry.action.order")));
	}

	private Integer _abstractLength;
	private long[] _allAssetCategoryIds;
	private String[] _allAssetTagNames;
	private String[] _allKeywords;
	private Boolean _anyAssetType;
	private Long _assetCategoryId;
	private AssetEntryQuery _assetEntryQuery;
	private List<AssetEntryResult> _assetEntryResults;
	private final AssetHelper _assetHelper;
	private String _assetLinkBehavior;
	private final AssetListAssetEntryProvider _assetListAssetEntryProvider;
	private AssetListEntry _assetListEntry;
	private final AssetListEntrySegmentsEntryRelLocalService
		_assetListEntrySegmentsEntryRelLocalService;
	private final AssetPublisherCustomizer _assetPublisherCustomizer;
	private final AssetPublisherHelper _assetPublisherHelper;
	private final AssetPublisherPortletInstanceConfiguration
		_assetPublisherPortletInstanceConfiguration;
	private final AssetPublisherWebConfiguration
		_assetPublisherWebConfiguration;
	private final AssetPublisherWebHelper _assetPublisherWebHelper;
	private String _assetTagName;
	private Map<String, Serializable> _attributes;
	private long[] _availableClassNameIds;
	private long[] _classNameIds;
	private Comparator<ClassType> _classTypeComparator;
	private long[] _classTypeIds;
	private String[] _compilerTagNames;
	private String _ddmStructureDisplayFieldValue;
	private String _ddmStructureFieldLabel;
	private String _ddmStructureFieldName;
	private String _ddmStructureFieldValue;
	private Boolean _defaultAssetPublisher;
	private String _displayStyle;
	private Long _displayStyleGroupId;
	private Boolean _enableCommentRatings;
	private Boolean _enableComments;
	private Boolean _enableConversions;
	private Boolean _enableFlags;
	private Boolean _enablePrint;
	private Boolean _enableRatings;
	private Boolean _enableRelatedAssets;
	private Boolean _enableRSS;
	private Boolean _enableSubscriptions;
	private Boolean _enableTagBasedNavigation;
	private Boolean _enableViewCountIncrement;
	private Boolean _excludeZeroViewCount;
	private String[] _extensions;
	private long[] _groupIds;
	private final HttpServletRequest _httpServletRequest;
	private final InfoItemServiceRegistry _infoItemServiceRegistry;
	private String _infoListProviderKey;
	private final ItemSelector _itemSelector;
	private Boolean _mergeURLTags;
	private String[] _metadataFields;
	private String _orderByColumn1;
	private String _orderByColumn2;
	private String _orderByType1;
	private String _orderByType2;
	private String _paginationType;
	private final Portal _portal;
	private final PortletPreferences _portletPreferences;
	private final PortletRequest _portletRequest;
	private String _portletResource;
	private final PortletResponse _portletResponse;
	private long[] _referencedModelsGroupIds;
	private final RequestContextMapper _requestContextMapper;
	private Integer _rssDelta;
	private String _rssDisplayStyle;
	private String _rssFeedType;
	private String _rssName;
	private SearchContainer<AssetEntry> _searchContainer;
	private final SegmentsEntryRetriever _segmentsEntryRetriever;
	private String _selectionStyle;
	private Boolean _showAddContentButton;
	private Boolean _showAssetTitle;
	private Boolean _showAuthor;
	private Boolean _showAvailableLocales;
	private Boolean _showCategories;
	private Boolean _showContextLink;
	private Boolean _showCreateDate;
	private Boolean _showExpirationDate;
	private Boolean _showExtraInfo;
	private Boolean _showMetadataDescriptions;
	private Boolean _showModifiedDate;
	private Boolean _showOnlyLayoutAssets;
	private Boolean _showPriority;
	private Boolean _showPublishDate;
	private Boolean _showRelatedAssets;
	private Boolean _showTags;
	private Boolean _showViewCount;
	private String _socialBookmarksDisplayStyle;
	private String _socialBookmarksTypes;
	private Boolean _subtypeFieldsFilterEnabled;
	private final ThemeDisplay _themeDisplay;

	private class SelectorEntriesLabelComparator
		implements Comparator<Map<String, Object>>, Serializable {

		public SelectorEntriesLabelComparator(Locale locale) {
			_collator = CollatorUtil.getInstance(locale);
		}

		@Override
		public int compare(Map<String, Object> map1, Map<String, Object> map2) {
			String label1 = StringPool.BLANK;
			String label2 = StringPool.BLANK;

			if (map1.containsKey("label") && map2.containsKey("label")) {
				label1 = (String)map1.get("label");
				label2 = (String)map2.get("label");
			}

			return _collator.compare(label1, label2);
		}

		private final Collator _collator;

	}

}