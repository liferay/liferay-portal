/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.display.context;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.ClassType;
import com.liferay.asset.kernel.model.ClassTypeField;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyServiceUtil;
import com.liferay.asset.list.constants.AssetListPortletKeys;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntryAssetEntryRel;
import com.liferay.asset.list.model.AssetListEntrySegmentsEntryRel;
import com.liferay.asset.list.service.AssetListEntryAssetEntryRelLocalServiceUtil;
import com.liferay.asset.list.service.AssetListEntryLocalServiceUtil;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalServiceUtil;
import com.liferay.asset.list.util.comparator.ClassNameModelResourceComparator;
import com.liferay.asset.list.web.internal.constants.AssetListWebKeys;
import com.liferay.asset.tags.item.selector.AssetTagsItemSelectorCriterion;
import com.liferay.asset.tags.item.selector.AssetTagsItemSelectorReturnType;
import com.liferay.asset.util.AssetRendererFactoryClassProvider;
import com.liferay.asset.util.comparator.AssetRendererFactoryTypeNameComparator;
import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.criteria.AssetEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.GroupItemSelectorReturnType;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.asset.criterion.AssetEntryItemSelectorCriterion;
import com.liferay.item.selector.criteria.group.criterion.GroupItemSelectorCriterion;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
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
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CollatorUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.configuration.provider.SegmentsConfigurationProvider;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.constants.SegmentsPortletKeys;
import com.liferay.segments.item.selector.SegmentsEntryItemSelectorCriterion;
import com.liferay.segments.item.selector.SegmentsEntryItemSelectorReturnType;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.service.SegmentsEntryLocalServiceUtil;
import com.liferay.segments.service.SegmentsEntryServiceUtil;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

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

/**
 * @author Pavel Savinov
 */
public class EditAssetListDisplayContext {

	public EditAssetListDisplayContext(
		AssetRendererFactoryClassProvider assetRendererFactoryClassProvider,
		InfoSearchClassMapperRegistry infoSearchClassMapperRegistry,
		ItemSelector itemSelector, PortletRequest portletRequest,
		PortletResponse portletResponse,
		SegmentsConfigurationProvider segmentsConfigurationProvider,
		UnicodeProperties unicodeProperties) {

		_assetRendererFactoryClassProvider = assetRendererFactoryClassProvider;
		_infoSearchClassMapperRegistry = infoSearchClassMapperRegistry;
		_itemSelector = itemSelector;
		_portletRequest = portletRequest;
		_portletResponse = portletResponse;
		_segmentsConfigurationProvider = segmentsConfigurationProvider;
		_unicodeProperties = unicodeProperties;

		_httpServletRequest = PortalUtil.getHttpServletRequest(portletRequest);

		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String encodeName(
		long ddmStructureId, String fieldReference, Locale locale) {

		DDMIndexer ddmIndexer = (DDMIndexer)_httpServletRequest.getAttribute(
			AssetListWebKeys.DDM_INDEXER);

		return ddmIndexer.encodeName(ddmStructureId, fieldReference, locale);
	}

	public List<DropdownItem> getActionDropdownItems() throws Exception {
		DropdownItemList dropdownItemList = new DropdownItemList();

		AssetListEntry assetListEntry = getAssetListEntry();

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.create(
			true
		).fastLoad(
			_getTypeSettings()
		).build();

		long[] classNameIds = GetterUtil.getLongValues(
			StringUtil.split(
				unicodeProperties.getProperty("classNameIds", null)));

		if (classNameIds.length == 0) {
			classNameIds = _getDefaultClassNameIds();
		}

		for (long classNameId : classNameIds) {
			AssetRendererFactory<?> assetRendererFactory =
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassNameId(classNameId);

			if ((assetRendererFactory == null) ||
				!assetRendererFactory.isActive(_themeDisplay.getCompanyId()) ||
				!assetRendererFactory.isSelectable()) {

				continue;
			}

			if (!Objects.equals(
					assetListEntry.getAssetEntryType(),
					AssetEntry.class.getName()) &&
				!Objects.equals(
					assetListEntry.getAssetEntryType(),
					assetRendererFactory.getClassName())) {

				continue;
			}

			if (!assetRendererFactory.isSupportsClassTypes()) {
				dropdownItemList.add(
					_getUnsafeConsumer(
						assetRendererFactory, _DEFAULT_SUBTYPE_SELECTION_ID,
						assetRendererFactory.getTypeName(
							_themeDisplay.getLocale())));

				continue;
			}

			Class<? extends AssetRendererFactory<?>> clazz =
				_assetRendererFactoryClassProvider.getClass(
					assetRendererFactory);

			ClassTypeReader classTypeReader =
				assetRendererFactory.getClassTypeReader();

			long[] classTypeIds = GetterUtil.getLongValues(
				StringUtil.split(
					unicodeProperties.getProperty(
						"classTypeIds" + clazz.getSimpleName(), null)),
				_getDefaultClassTypeIds(classTypeReader));

			for (long classTypeId : classTypeIds) {
				dropdownItemList.add(
					_getClassTypeIdUnsafeConsumer(
						assetRendererFactory,
						assetListEntry.getAssetEntrySubtype(), classTypeId,
						classTypeReader));
			}
		}

		return ListUtil.sort(
			dropdownItemList,
			new SelectorEntriesLabelComparator(_themeDisplay.getLocale()));
	}

	public AssetListEntry getAssetListEntry() {
		if (_assetListEntry != null) {
			return _assetListEntry;
		}

		_assetListEntry = AssetListEntryLocalServiceUtil.fetchAssetListEntry(
			getAssetListEntryId());

		return _assetListEntry;
	}

	public long getAssetListEntryId() {
		if (_assetListEntryId != null) {
			return _assetListEntryId;
		}

		_assetListEntryId = ParamUtil.getLong(
			_httpServletRequest, "assetListEntryId");

		return _assetListEntryId;
	}

	public List<AssetListEntrySegmentsEntryRel>
		getAssetListEntrySegmentsEntryRels() {

		if (_assetListEntrySegmentsEntryRels != null) {
			return _assetListEntrySegmentsEntryRels;
		}

		_assetListEntrySegmentsEntryRels =
			AssetListEntrySegmentsEntryRelLocalServiceUtil.
				getAssetListEntrySegmentsEntryRels(
					getAssetListEntryId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

		return _assetListEntrySegmentsEntryRels;
	}

	public int getAssetListEntryType() {
		if (_assetListEntryType != null) {
			return _assetListEntryType;
		}

		AssetListEntry assetListEntry = getAssetListEntry();

		int assetListEntryType = ParamUtil.getInteger(
			_httpServletRequest, "assetListEntryType");

		if (assetListEntry != null) {
			assetListEntryType = assetListEntry.getType();
		}

		_assetListEntryType = assetListEntryType;

		return _assetListEntryType;
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
				String queryValues = PropertiesParamUtil.getString(
					_unicodeProperties, _httpServletRequest, "queryValues" + i);

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
			JSONObject ruleJSONObject = JSONUtil.put(
				"queryAndOperator",
				PropertiesParamUtil.getBoolean(
					_unicodeProperties, _httpServletRequest,
					"queryAndOperator" + queryLogicIndex)
			).put(
				"queryContains",
				PropertiesParamUtil.getBoolean(
					_unicodeProperties, _httpServletRequest,
					"queryContains" + queryLogicIndex, true)
			);

			String queryValues = _unicodeProperties.getProperty(
				"queryValues" + queryLogicIndex, StringPool.BLANK);

			String queryName = PropertiesParamUtil.getString(
				_unicodeProperties, _httpServletRequest,
				"queryName" + queryLogicIndex, "assetTags");

			if (Objects.equals(queryName, "assetTags")) {
				queryValues = ParamUtil.getString(
					_httpServletRequest, "queryTagNames" + queryLogicIndex,
					queryValues);

				queryValues = _filterAssetTagNames(
					_themeDisplay.getScopeGroupId(), queryValues);

				String[] tagNames = StringUtil.split(
					queryValues, StringPool.COMMA);

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

				StringBundler sb = new StringBundler(keywords.length * 2);

				for (String keyword : keywords) {
					if (keyword.contains(" ")) {
						keyword = StringUtil.quote(keyword, CharPool.QUOTE);
					}

					sb.append(keyword);
					sb.append(StringPool.SPACE);
				}

				sb.setIndex(sb.index() - 1);

				queryValues = sb.toString();

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

				List<HashMap<String, Object>> selectedItems = new ArrayList<>();

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

		if (rulesJSONArray.length() == 0) {
			rulesJSONArray.put(
				JSONUtil.put(
					"queryContains", true
				).put(
					"type", "assetTags"
				));
		}

		return rulesJSONArray;
	}

	public List<Long> getAvailableClassNameIds() {
		if (_availableClassNameIds != null) {
			return _availableClassNameIds;
		}

		List<Long> availableClassNameIds = ListUtil.fromArray(
			AssetRendererFactoryRegistryUtil.getIndexableClassNameIds(
				_themeDisplay.getCompanyId(), true));

		availableClassNameIds = ListUtil.sort(
			availableClassNameIds,
			new ClassNameModelResourceComparator(
				true, _themeDisplay.getLocale()));

		_availableClassNameIds = availableClassNameIds;

		return _availableClassNameIds;
	}

	public Set<Group> getAvailableGroups() throws PortalException {
		Set<Group> availableGroups = new HashSet<>();

		Company company = _themeDisplay.getCompany();

		availableGroups.add(company.getGroup());

		availableGroups.add(_themeDisplay.getScopeGroup());

		return availableGroups;
	}

	public List<SegmentsEntry> getAvailableSegmentsEntries() {
		if (_availableSegmentsEntries != null) {
			return _availableSegmentsEntries;
		}

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		Group group = _themeDisplay.getScopeGroup();

		if (!stagingGroupHelper.isStagedPortlet(
				_themeDisplay.getScopeGroupId(),
				SegmentsPortletKeys.SEGMENTS)) {

			group = stagingGroupHelper.getStagedPortletGroup(
				_themeDisplay.getScopeGroup(), SegmentsPortletKeys.SEGMENTS);
		}

		_availableSegmentsEntries = ListUtil.filter(
			SegmentsEntryServiceUtil.getSegmentsEntries(group.getGroupId()),
			segmentsEntry -> !ArrayUtil.contains(
				getSelectedSegmentsEntryIds(),
				segmentsEntry.getSegmentsEntryId()));

		return _availableSegmentsEntries;
	}

	public String getBackURL() {
		if (Validator.isNotNull(_backURL)) {
			return _backURL;
		}

		String redirect = ParamUtil.getString(_httpServletRequest, "redirect");
		String backURL = ParamUtil.getString(_httpServletRequest, "backURL");

		if (Validator.isNotNull(backURL)) {
			_backURL = backURL;
		}
		else if (Validator.isNotNull(redirect)) {
			_backURL = redirect;
		}
		else {
			LiferayPortletResponse liferayPortletResponse =
				PortalUtil.getLiferayPortletResponse(_portletResponse);

			_backURL = String.valueOf(liferayPortletResponse.createRenderURL());
		}

		return _backURL;
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

	public String getClassName(AssetRendererFactory<?> assetRendererFactory) {
		Class<? extends AssetRendererFactory<?>> clazz =
			_assetRendererFactoryClassProvider.getClass(assetRendererFactory);

		String className = clazz.getName();

		return className.substring(
			className.lastIndexOf(StringPool.PERIOD) + 1);
	}

	public long[] getClassNameIds() {
		if (_classNameIds != null) {
			return _classNameIds;
		}

		List<Long> availableClassNameIds = getAvailableClassNameIds();

		_classNameIds = getClassNameIds(
			_unicodeProperties,
			ArrayUtil.toArray(availableClassNameIds.toArray(new Long[0])));

		return _classNameIds;
	}

	public long[] getClassNameIds(
		UnicodeProperties unicodeProperties, long[] availableClassNameIds) {

		boolean anyAssetType = GetterUtil.getBoolean(
			unicodeProperties.getProperty(
				"anyAssetType", Boolean.TRUE.toString()));
		String selectionStyle = unicodeProperties.getProperty(
			"selectionStyle", "dynamic");

		if (anyAssetType || selectionStyle.equals("manual")) {
			return availableClassNameIds;
		}

		long defaultClassNameId = GetterUtil.getLong(
			unicodeProperties.getProperty("anyAssetType", null));

		if (defaultClassNameId > 0) {
			if (ArrayUtil.contains(availableClassNameIds, defaultClassNameId)) {
				return new long[] {defaultClassNameId};
			}

			return new long[0];
		}

		long[] classNameIds = GetterUtil.getLongValues(
			StringUtil.split(
				unicodeProperties.getProperty(
					"classNameIds", StringPool.BLANK)));

		if (ArrayUtil.isNotEmpty(classNameIds)) {
			return classNameIds;
		}

		return availableClassNameIds;
	}

	public long[] getClassTypeIds() {
		if (_classTypeIds != null) {
			return _classTypeIds;
		}

		long[] classNameIds = getClassNameIds();

		if (ArrayUtil.isEmpty(classNameIds) || (classNameIds.length > 1)) {
			_classTypeIds = new long[0];

			return _classTypeIds;
		}

		String className = getClassName(
			AssetRendererFactoryRegistryUtil.
				getAssetRendererFactoryByClassNameId(classNameIds[0]));

		long classTypeId = GetterUtil.getLong(
			_unicodeProperties.getProperty("anyClassType" + className));

		if (classTypeId > 0) {
			return new long[] {classTypeId};
		}

		_classTypeIds = GetterUtil.getLongValues(
			StringUtil.split(
				_unicodeProperties.getProperty(
					"classTypeIds" + className, StringPool.BLANK)));

		return _classTypeIds;
	}

	public Long[] getClassTypeIds(
		UnicodeProperties unicodeProperties, String className,
		List<ClassType> availableClassTypes) {

		Long[] availableClassTypeIds = new Long[availableClassTypes.size()];

		for (int i = 0; i < availableClassTypeIds.length; i++) {
			ClassType classType = availableClassTypes.get(i);

			availableClassTypeIds[i] = classType.getClassTypeId();
		}

		return _getClassTypeIds(
			unicodeProperties, className, availableClassTypeIds);
	}

	public Map<String, Object> getData() {
		return HashMapBuilder.<String, Object>put(
			"assetListEntrySegmentsEntryRels",
			_getAssetListEntrySegmentsEntryRelJSONArray()
		).put(
			"assetListEntryValid",
			() -> {
				AssetListEntry assetListEntry = getAssetListEntry();

				return Validator.isNotNull(assetListEntry.getAssetEntryType());
			}
		).put(
			"isSegmentationEnabled",
			isSegmentationEnabled(_themeDisplay.getCompanyId())
		).put(
			"openSelectSegmentsEntryDialogMethod",
			() -> {
				LiferayPortletResponse liferayPortletResponse =
					PortalUtil.getLiferayPortletResponse(_portletResponse);

				return liferayPortletResponse.getNamespace() +
					"openSelectSegmentsEntryDialog";
			}
		).put(
			"segmentsEntriesAvailables",
			() -> {
				List<SegmentsEntry> availableSegmentsEntries =
					getAvailableSegmentsEntries();

				return !availableSegmentsEntries.isEmpty();
			}
		).put(
			"updateVariationsPriorityURL",
			() -> {
				PortletURL assetListPortletURL =
					PortalUtil.getControlPanelPortletURL(
						_httpServletRequest, _themeDisplay.getScopeGroup(),
						AssetListPortletKeys.ASSET_LIST, 0, 0,
						PortletRequest.ACTION_PHASE);

				return PortletURLBuilder.create(
					assetListPortletURL
				).setActionName(
					"/asset_list/update_variations_priority"
				).buildString();
			}
		).build();
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

	public String getGroupItemSelectorURL() {
		ItemSelectorCriterion itemSelectorCriterion =
			new GroupItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new GroupItemSelectorReturnType());

		return PortletURLBuilder.create(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
				getSelectGroupEventName(), itemSelectorCriterion)
		).setPortletResource(
			AssetListPortletKeys.ASSET_LIST
		).buildString();
	}

	public String getOrderByColumn1() {
		if (_orderByColumn1 != null) {
			return _orderByColumn1;
		}

		_orderByColumn1 = GetterUtil.getString(
			_unicodeProperties.getProperty("orderByColumn1", "modifiedDate"));

		return _orderByColumn1;
	}

	public String getOrderByColumn2() {
		if (_orderByColumn2 != null) {
			return _orderByColumn2;
		}

		_orderByColumn2 = GetterUtil.getString(
			_unicodeProperties.getProperty("orderByColumn2", "title"));

		return _orderByColumn2;
	}

	public String getOrderByType1() {
		if (_orderByType1 != null) {
			return _orderByType1;
		}

		_orderByType1 = GetterUtil.getString(
			_unicodeProperties.getProperty("orderByType1", "DESC"));

		return _orderByType1;
	}

	public String getOrderByType2() {
		if (_orderByType2 != null) {
			return _orderByType2;
		}

		_orderByType2 = GetterUtil.getString(
			_unicodeProperties.getProperty("orderByType2", "ASC"));

		return _orderByType2;
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				_portletRequest, AssetListPortletKeys.ASSET_LIST,
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_asset_list_entry.jsp"
		).setParameter(
			"assetListEntryId", getAssetListEntryId()
		).setParameter(
			"segmentsEntryId", getSegmentsEntryId()
		).buildPortletURL();
	}

	public long[] getReferencedModelsGroupIds() throws PortalException {

		// Referenced models are asset subtypes, tags or categories that
		// are used to filter assets and can belong to a different scope of
		// the asset they are associated to

		if (_referencedModelsGroupIds != null) {
			return _referencedModelsGroupIds;
		}

		_referencedModelsGroupIds =
			PortalUtil.getCurrentAndAncestorSiteGroupIds(
				getSelectedGroupIds(), true);

		return _referencedModelsGroupIds;
	}

	public SearchContainer<AssetListEntryAssetEntryRel> getSearchContainer() {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		SearchContainer<AssetListEntryAssetEntryRel> searchContainer =
			new SearchContainer<>(
				_portletRequest, getPortletURL(), null,
				"there-are-no-asset-entries");

		searchContainer.setResultsAndTotal(
			() ->
				AssetListEntryAssetEntryRelLocalServiceUtil.
					getAssetListEntryAssetEntryRels(
						getAssetListEntryId(), getSegmentsEntryId(),
						searchContainer.getStart(), searchContainer.getEnd()),
			AssetListEntryAssetEntryRelLocalServiceUtil.
				getAssetListEntryAssetEntryRelsCount(
					getAssetListEntryId(), getSegmentsEntryId()));

		_searchContainer = searchContainer;

		return _searchContainer;
	}

	public String getSegmentsCompanyConfigurationURL() {
		try {
			return _segmentsConfigurationProvider.getCompanyConfigurationURL(
				_httpServletRequest);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return StringPool.BLANK;
	}

	public long getSegmentsEntryId() {
		if (_segmentsEntryId != null) {
			return _segmentsEntryId;
		}

		_segmentsEntryId = ParamUtil.getLong(
			_httpServletRequest, "segmentsEntryId",
			SegmentsEntryConstants.ID_DEFAULT);

		return _segmentsEntryId;
	}

	public String getSegmentsEntryName(long segmentsEntryId, Locale locale) {
		if (segmentsEntryId == SegmentsEntryConstants.ID_DEFAULT) {
			return SegmentsEntryConstants.getDefaultSegmentsEntryName(locale);
		}

		SegmentsEntry segmentsEntry =
			SegmentsEntryLocalServiceUtil.fetchSegmentsEntry(segmentsEntryId);

		return segmentsEntry.getName(locale);
	}

	public long[] getSelectedGroupIds() throws PortalException {
		return TransformUtil.transformToLongArray(
			getSelectedGroups(), Group::getGroupId);
	}

	public List<Group> getSelectedGroups() throws PortalException {
		long[] groupIds = GetterUtil.getLongValues(
			StringUtil.split(
				PropertiesParamUtil.getString(
					_unicodeProperties, _httpServletRequest, "groupIds")));

		if (ArrayUtil.isEmpty(groupIds)) {
			return Collections.singletonList(_themeDisplay.getScopeGroup());
		}

		return GroupLocalServiceUtil.getGroups(groupIds);
	}

	public long[] getSelectedSegmentsEntryIds() {
		if (_selectedSegmentsEntryIds != null) {
			return _selectedSegmentsEntryIds;
		}

		List<AssetListEntrySegmentsEntryRel> assetListEntrySegmentsEntryRels =
			getAssetListEntrySegmentsEntryRels();

		if (assetListEntrySegmentsEntryRels == null) {
			return null;
		}

		_selectedSegmentsEntryIds = TransformUtil.transformToLongArray(
			assetListEntrySegmentsEntryRels,
			AssetListEntrySegmentsEntryRel::getSegmentsEntryId);

		return _selectedSegmentsEntryIds;
	}

	public String getSelectGroupEventName() {
		return _portletResponse.getNamespace() + "_selectSite";
	}

	public String getSelectSegmentsEntryURL() {
		if (_selectSegmentsEntryURL != null) {
			return _selectSegmentsEntryURL;
		}

		SegmentsEntryItemSelectorCriterion segmentsEntryItemSelectorCriterion =
			new SegmentsEntryItemSelectorCriterion();

		segmentsEntryItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			Collections.singletonList(
				new SegmentsEntryItemSelectorReturnType()));

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		Group group = _themeDisplay.getScopeGroup();

		if (!stagingGroupHelper.isStagedPortlet(
				_themeDisplay.getScopeGroupId(),
				SegmentsPortletKeys.SEGMENTS)) {

			group = stagingGroupHelper.getStagedPortletGroup(
				_themeDisplay.getScopeGroup(), SegmentsPortletKeys.SEGMENTS);
		}

		segmentsEntryItemSelectorCriterion.setGroupId(group.getGroupId());

		_selectSegmentsEntryURL = String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_portletRequest),
				_portletResponse.getNamespace() + "selectEntity",
				segmentsEntryItemSelectorCriterion));

		return _selectSegmentsEntryURL;
	}

	public String getTagSelectorURL() throws Exception {
		AssetTagsItemSelectorCriterion assetTagsItemSelectorCriterion =
			new AssetTagsItemSelectorCriterion();

		assetTagsItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new AssetTagsItemSelectorReturnType());
		assetTagsItemSelectorCriterion.setGroupIds(getSelectedGroupIds());
		assetTagsItemSelectorCriterion.setMultiSelection(true);

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
				_portletResponse.getNamespace() + "selectTag",
				assetTagsItemSelectorCriterion));
	}

	public UnicodeProperties getUnicodeProperties() {
		return _unicodeProperties;
	}

	public List<Long> getVocabularyIds() throws PortalException {
		List<AssetVocabulary> assetVocabularies = ListUtil.filter(
			AssetVocabularyServiceUtil.getGroupsVocabularies(
				PortalUtil.getCurrentAndAncestorSiteGroupIds(
					getReferencedModelsGroupIds())),
			vocabulary -> {
				long[] classNameIds = vocabulary.getSelectedClassNameIds();

				for (long classNameId : classNameIds) {
					if (classNameId == 0) {
						return true;
					}

					String className =
						_infoSearchClassMapperRegistry.getSearchClassName(
							PortalUtil.getClassName(classNameId));

					AssetRendererFactory<?> assetRendererFactory =
						AssetRendererFactoryRegistryUtil.
							getAssetRendererFactoryByClassName(className);

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

	public Boolean isAnyAssetType() {
		String anyAssetType = _unicodeProperties.getProperty("anyAssetType");

		if (Validator.isNull(anyAssetType)) {
			return false;
		}

		return GetterUtil.getBoolean(anyAssetType, true);
	}

	public boolean isLiveGroup() {
		if (_liveGroup != null) {
			return _liveGroup;
		}

		Group group = _themeDisplay.getScopeGroup();

		if (group.isLayout()) {
			group = group.getParentGroup();
		}

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		if (stagingGroupHelper.isLiveGroup(group) &&
			stagingGroupHelper.isStagedPortlet(
				group, AssetListPortletKeys.ASSET_LIST)) {

			_liveGroup = true;

			return _liveGroup;
		}

		_liveGroup = false;

		return _liveGroup;
	}

	public Boolean isNoAssetTypeSelected() {
		String anyAssetType = _unicodeProperties.getProperty("anyAssetType");

		return Validator.isNull(anyAssetType);
	}

	public boolean isSegmentationEnabled(long companyId) {
		try {
			return _segmentsConfigurationProvider.isSegmentationEnabled(
				companyId);
		}
		catch (ConfigurationException configurationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(configurationException);
			}

			return false;
		}
	}

	public boolean isSubtypeFieldsFilterEnabled() {
		if (_subtypeFieldsFilterEnabled != null) {
			return _subtypeFieldsFilterEnabled;
		}

		long[] classNameIds = getClassNameIds();

		if (ArrayUtil.isEmpty(classNameIds) || (classNameIds.length > 1)) {
			_subtypeFieldsFilterEnabled = false;

			return _subtypeFieldsFilterEnabled;
		}

		String className = getClassName(
			AssetRendererFactoryRegistryUtil.
				getAssetRendererFactoryByClassNameId(classNameIds[0]));

		_subtypeFieldsFilterEnabled = GetterUtil.getBoolean(
			_unicodeProperties.getProperty(
				"subtypeFieldsFilterEnabled" + className,
				Boolean.FALSE.toString()));

		return _subtypeFieldsFilterEnabled;
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

	private String _filterAssetTagNames(long groupId, String assetTagNames) {
		List<String> filteredAssetTagNames = new ArrayList<>();

		String[] assetTagNamesArray = StringUtil.split(assetTagNames);

		long[] assetTagIds = AssetTagLocalServiceUtil.getTagIds(
			groupId, assetTagNamesArray);

		for (long assetTagId : assetTagIds) {
			AssetTag assetTag = AssetTagLocalServiceUtil.fetchAssetTag(
				assetTagId);

			if (assetTag != null) {
				filteredAssetTagNames.add(assetTag.getName());
			}
		}

		return StringUtil.merge(filteredAssetTagNames);
	}

	private PortletURL _getAssetEntryItemSelectorPortletURL(
		AssetRendererFactory<?> assetRendererFactory, long subtypeSelectionId) {

		PortletURL portletURL = assetRendererFactory.getItemSelectorURL(
			PortalUtil.getLiferayPortletRequest(_portletRequest),
			PortalUtil.getLiferayPortletResponse(_portletResponse),
			subtypeSelectionId, _portletResponse.getNamespace() + "selectAsset",
			_themeDisplay.getScopeGroup(), true, 0);

		if (portletURL != null) {
			return portletURL;
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

		return PortletURLBuilder.create(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_portletRequest),
				_portletResponse.getNamespace() + "selectAsset",
				assetEntryItemSelectorCriterion)
		).buildPortletURL();
	}

	private JSONArray _getAssetListEntrySegmentsEntryRelJSONArray() {
		List<AssetListEntrySegmentsEntryRel> assetListEntrySegmentsEntryRels =
			new ArrayList<>(getAssetListEntrySegmentsEntryRels());

		Collections.sort(
			assetListEntrySegmentsEntryRels,
			Comparator.comparingInt(
				AssetListEntrySegmentsEntryRel::getPriority));

		LiferayPortletResponse liferayPortletResponse =
			PortalUtil.getLiferayPortletResponse(_portletResponse);

		return JSONUtil.toJSONArray(
			assetListEntrySegmentsEntryRels,
			assetListEntrySegmentsEntryRel -> JSONUtil.put(
				"active",
				getSegmentsEntryId() ==
					assetListEntrySegmentsEntryRel.getSegmentsEntryId()
			).put(
				"assetListEntrySegmentsEntryRelId",
				assetListEntrySegmentsEntryRel.
					getAssetListEntrySegmentsEntryRelId()
			).put(
				"deleteAssetListEntryVariationURL",
				_getDeleteAssetListEntryVariationURL(
					liferayPortletResponse, assetListEntrySegmentsEntryRel)
			).put(
				"editAssetListEntryURL",
				PortletURLBuilder.createRenderURL(
					liferayPortletResponse
				).setMVCPath(
					"/edit_asset_list_entry.jsp"
				).setBackURL(
					getBackURL()
				).setParameter(
					"assetListEntryId",
					assetListEntrySegmentsEntryRel.getAssetListEntryId()
				).setParameter(
					"backURLTitle",
					() -> {
						PortletDisplay portletDisplay =
							_themeDisplay.getPortletDisplay();

						return portletDisplay.getPortletDisplayName();
					}
				).setParameter(
					"segmentsEntryId",
					assetListEntrySegmentsEntryRel.getSegmentsEntryId()
				).buildString()
			).put(
				"name",
				getSegmentsEntryName(
					assetListEntrySegmentsEntryRel.getSegmentsEntryId(),
					_themeDisplay.getLocale())
			),
			_log);
	}

	private Long[] _getClassTypeIds(
		UnicodeProperties unicodeProperties, String className,
		Long[] availableClassTypeIds) {

		if (GetterUtil.getBoolean(
				unicodeProperties.getProperty(
					"anyClassType" + className, Boolean.TRUE.toString()))) {

			return availableClassTypeIds;
		}

		long defaultClassTypeId = GetterUtil.getLong(
			unicodeProperties.getProperty("anyClassType" + className, null),
			-1);

		if (defaultClassTypeId > -1) {
			return new Long[] {defaultClassTypeId};
		}

		Long[] classTypeIds = ArrayUtil.toArray(
			StringUtil.split(
				unicodeProperties.getProperty("classTypeIds" + className, null),
				0L));

		if (classTypeIds != null) {
			return classTypeIds;
		}

		return availableClassTypeIds;
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getClassTypeIdUnsafeConsumer(
			AssetRendererFactory<?> assetRendererFactory,
			String assetEntrySubtype, long classTypeId,
			ClassTypeReader classTypeReader) {

		ClassType classType = null;

		try {
			classType = classTypeReader.getClassType(
				classTypeId, _themeDisplay.getLocale());
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return null;
		}

		if (Validator.isNotNull(assetEntrySubtype) &&
			!Objects.equals(
				assetEntrySubtype,
				String.valueOf(classType.getClassTypeId()))) {

			return null;
		}

		return _getUnsafeConsumer(
			assetRendererFactory, classType.getClassTypeId(),
			classType.getName());
	}

	private long[] _getDefaultClassNameIds() {
		List<AssetRendererFactory<?>> assetRendererFactories = ListUtil.sort(
			AssetRendererFactoryRegistryUtil.getAssetRendererFactories(
				_themeDisplay.getCompanyId(), true),
			new AssetRendererFactoryTypeNameComparator(
				_themeDisplay.getLocale()));

		return ListUtil.toLongArray(
			assetRendererFactories, AssetRendererFactory::getClassNameId);
	}

	private long[] _getDefaultClassTypeIds(ClassTypeReader classTypeReader)
		throws Exception {

		List<ClassType> assetAvailableClassTypes =
			classTypeReader.getAvailableClassTypes(
				PortalUtil.getCurrentAndAncestorSiteGroupIds(
					_themeDisplay.getScopeGroupId()),
				_themeDisplay.getLocale());

		return ListUtil.toLongArray(
			assetAvailableClassTypes, ClassType::getClassTypeId);
	}

	private String _getDeleteAssetListEntryVariationURL(
		LiferayPortletResponse liferayPortletResponse,
		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel) {

		if ((assetListEntrySegmentsEntryRel.getSegmentsEntryId() ==
				SegmentsEntryConstants.ID_DEFAULT) ||
			isLiveGroup()) {

			return "";
		}

		return PortletURLBuilder.createActionURL(
			liferayPortletResponse
		).setActionName(
			"/asset_list/delete_asset_list_entry_variation"
		).setBackURL(
			getBackURL()
		).setParameter(
			"assetListEntryId",
			assetListEntrySegmentsEntryRel.getAssetListEntryId()
		).setParameter(
			"segmentsEntryId",
			assetListEntrySegmentsEntryRel.getSegmentsEntryId()
		).buildString();
	}

	private String _getTypeSettings() {
		AssetListEntry assetListEntry = getAssetListEntry();

		String typeSettings = assetListEntry.getTypeSettings(
			getSegmentsEntryId());

		if (Validator.isNull(typeSettings)) {
			typeSettings = assetListEntry.getTypeSettings(
				SegmentsEntryConstants.ID_DEFAULT);
		}

		return typeSettings;
	}

	private UnsafeConsumer<DropdownItem, Exception> _getUnsafeConsumer(
		AssetRendererFactory<?> assetRendererFactory, long classTypeId,
		String classTypeName) {

		return dropdownItem -> {
			dropdownItem.putData(
				"href",
				String.valueOf(
					_getAssetEntryItemSelectorPortletURL(
						assetRendererFactory, classTypeId)));
			dropdownItem.putData(
				"title",
				HtmlUtil.escape(
					LanguageUtil.format(
						_httpServletRequest, "select-x", classTypeName,
						false)));
			dropdownItem.setLabel(classTypeName);
		};
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
			_unicodeProperties.getProperty(
				"ddmStructureDisplayFieldValue", StringPool.BLANK));

		_ddmStructureFieldName = ParamUtil.getString(
			_httpServletRequest, "ddmStructureFieldName",
			_unicodeProperties.getProperty(
				"ddmStructureFieldName", StringPool.BLANK));
		_ddmStructureFieldValue = ParamUtil.getString(
			_httpServletRequest, "ddmStructureFieldValue",
			_unicodeProperties.getProperty(
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

	private static final long _DEFAULT_SUBTYPE_SELECTION_ID = -1;

	private static final Log _log = LogFactoryUtil.getLog(
		EditAssetListDisplayContext.class);

	private AssetListEntry _assetListEntry;
	private Long _assetListEntryId;
	private List<AssetListEntrySegmentsEntryRel>
		_assetListEntrySegmentsEntryRels;
	private Integer _assetListEntryType;
	private final AssetRendererFactoryClassProvider
		_assetRendererFactoryClassProvider;
	private List<Long> _availableClassNameIds;
	private List<SegmentsEntry> _availableSegmentsEntries;
	private String _backURL;
	private long[] _classNameIds;
	private long[] _classTypeIds;
	private String _ddmStructureDisplayFieldValue;
	private String _ddmStructureFieldLabel;
	private String _ddmStructureFieldName;
	private String _ddmStructureFieldValue;
	private final HttpServletRequest _httpServletRequest;
	private final InfoSearchClassMapperRegistry _infoSearchClassMapperRegistry;
	private final ItemSelector _itemSelector;
	private Boolean _liveGroup;
	private String _orderByColumn1;
	private String _orderByColumn2;
	private String _orderByType1;
	private String _orderByType2;
	private final PortletRequest _portletRequest;
	private final PortletResponse _portletResponse;
	private long[] _referencedModelsGroupIds;
	private SearchContainer<AssetListEntryAssetEntryRel> _searchContainer;
	private final SegmentsConfigurationProvider _segmentsConfigurationProvider;
	private Long _segmentsEntryId;
	private long[] _selectedSegmentsEntryIds;
	private String _selectSegmentsEntryURL;
	private Boolean _subtypeFieldsFilterEnabled;
	private final ThemeDisplay _themeDisplay;
	private final UnicodeProperties _unicodeProperties;

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