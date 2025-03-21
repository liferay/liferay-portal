/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyServiceUtil;
import com.liferay.asset.tags.item.selector.AssetTagsItemSelectorCriterion;
import com.liferay.asset.tags.item.selector.AssetTagsItemSelectorReturnType;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.catalog.CPSku;
import com.liferay.commerce.product.content.render.list.CPContentListRendererRegistry;
import com.liferay.commerce.product.content.render.list.entry.CPContentListEntryRendererRegistry;
import com.liferay.commerce.product.content.web.internal.helper.CPPublisherWebHelper;
import com.liferay.commerce.product.data.source.CPDataSource;
import com.liferay.commerce.product.data.source.CPDataSourceRegistry;
import com.liferay.commerce.product.item.selector.criterion.CPDefinitionItemSelectorCriterion;
import com.liferay.commerce.product.type.CPTypeRegistry;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.commerce.product.util.CPInstanceHelper;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PrefsParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletPreferences;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public class CPPublisherConfigurationDisplayContext
	extends BaseCPPublisherDisplayContext {

	public CPPublisherConfigurationDisplayContext(
			AssetCategoryLocalService assetCategoryLocalService,
			AssetTagLocalService assetTagLocalService,
			ConfigurationProvider configurationProvider,
			CPContentListEntryRendererRegistry contentListEntryRendererRegistry,
			CPContentListRendererRegistry cpContentListRendererRegistry,
			CPDataSourceRegistry cpDataSourceRegistry,
			CPDefinitionHelper cpDefinitionHelper,
			CPInstanceHelper cpInstanceHelper,
			CPPublisherWebHelper cpPublisherWebHelper,
			CPTypeRegistry cpTypeRegistry, GroupLocalService groupLocalService,
			HttpServletRequest httpServletRequest, ItemSelector itemSelector)
		throws PortalException {

		super(
			configurationProvider, contentListEntryRendererRegistry,
			cpContentListRendererRegistry, cpPublisherWebHelper, cpTypeRegistry,
			groupLocalService, httpServletRequest);

		_assetCategoryLocalService = assetCategoryLocalService;
		_assetTagLocalService = assetTagLocalService;
		_configurationProvider = configurationProvider;
		_cpDataSourceRegistry = cpDataSourceRegistry;
		_cpDefinitionHelper = cpDefinitionHelper;
		_cpInstanceHelper = cpInstanceHelper;
		_itemSelector = itemSelector;
	}

	public String filterAssetTagNames(long groupId, String assetTagNames) {
		List<String> filteredAssetTagNames = new ArrayList<>();

		String[] assetTagNamesArray = StringUtil.split(assetTagNames);

		long[] assetTagIds = _assetTagLocalService.getTagIds(
			groupId, assetTagNamesArray);

		for (long assetTagId : assetTagIds) {
			AssetTag assetTag = _assetTagLocalService.fetchAssetTag(assetTagId);

			if (assetTag != null) {
				filteredAssetTagNames.add(assetTag.getName());
			}
		}

		return StringUtil.merge(filteredAssetTagNames);
	}

	public JSONArray getAutoFieldRulesJSONArray() {
		ThemeDisplay themeDisplay = cpContentRequestHelper.getThemeDisplay();

		PortletPreferences portletPreferences =
			cpContentRequestHelper.getPortletPreferences();

		String queryLogicIndexesParam = ParamUtil.getString(
			cpContentRequestHelper.getRequest(), "queryLogicIndexes");

		int[] queryLogicIndexes = null;

		if (Validator.isNotNull(queryLogicIndexesParam)) {
			queryLogicIndexes = StringUtil.split(queryLogicIndexesParam, 0);
		}
		else {
			queryLogicIndexes = new int[0];

			for (int i = 0; true; i++) {
				String queryValues = PrefsParamUtil.getString(
					portletPreferences, cpContentRequestHelper.getRequest(),
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
				portletPreferences, cpContentRequestHelper.getRequest(),
				"queryAndOperator" + queryLogicIndex);
			boolean queryContains = PrefsParamUtil.getBoolean(
				portletPreferences, cpContentRequestHelper.getRequest(),
				"queryContains" + queryLogicIndex, true);

			JSONObject ruleJSONObject = JSONUtil.put(
				"queryAndOperator", queryAndOperator
			).put(
				"queryContains", queryContains
			);

			String queryValues = StringUtil.merge(
				portletPreferences.getValues(
					"queryValues" + queryLogicIndex, new String[0]));

			String queryName = PrefsParamUtil.getString(
				portletPreferences, cpContentRequestHelper.getRequest(),
				"queryName" + queryLogicIndex, "assetTags");

			if (Objects.equals(queryName, "assetTags")) {
				queryValues = ParamUtil.getString(
					cpContentRequestHelper.getRequest(),
					"queryTagNames" + queryLogicIndex, queryValues);

				queryValues = filterAssetTagNames(
					themeDisplay.getCompanyGroupId(), queryValues);
			}
			else {
				queryValues = ParamUtil.getString(
					cpContentRequestHelper.getRequest(),
					"queryCategoryIds" + queryLogicIndex, queryValues);

				JSONArray categoryIdsTitlesJSONArray =
					JSONFactoryUtil.createJSONArray();

				List<AssetCategory> categories = _filterAssetCategories(
					GetterUtil.getLongValues(StringUtil.split(queryValues)));

				for (AssetCategory category : categories) {
					categoryIdsTitlesJSONArray.put(
						category.getTitle(themeDisplay.getLocale()));
				}

				List<Long> categoryIds = ListUtil.toList(
					categories, AssetCategory.CATEGORY_ID_ACCESSOR);

				queryValues = StringUtil.merge(categoryIds);

				ruleJSONObject.put(
					"categoryIdsTitles", categoryIdsTitlesJSONArray);
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

	public String getCategorySelectorURL() {
		HttpServletRequest httpServletRequest =
			cpContentRequestHelper.getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		InfoItemItemSelectorCriterion itemSelectorCriterion =
			new InfoItemItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new InfoItemItemSelectorReturnType());
		itemSelectorCriterion.setItemType(AssetCategory.class.getName());

		return PortletURLBuilder.create(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(
					cpContentRequestHelper.getLiferayPortletRequest()),
				themeDisplay.getScopeGroup(), themeDisplay.getScopeGroupId(),
				_getPortletNamespace() + "selectCategory",
				itemSelectorCriterion)
		).buildString();
	}

	public List<CPDataSource> getCPDataSources() {
		return _cpDataSourceRegistry.getCPDataSources();
	}

	public CPSku getDefaultCPSku(CPCatalogEntry cpCatalogEntry)
		throws Exception {

		return _cpInstanceHelper.getDefaultCPSku(cpCatalogEntry);
	}

	public String getDefaultImageFileURL(CPCatalogEntry cpCatalogEntry)
		throws PortalException {

		HttpServletRequest httpServletRequest =
			cpContentRequestHelper.getRequest();

		return _cpDefinitionHelper.getDefaultImageFileURL(
			CommerceUtil.getCommerceAccountId(
				(CommerceContext)httpServletRequest.getAttribute(
					CommerceWebKeys.COMMERCE_CONTEXT)),
			cpCatalogEntry.getCPDefinitionId());
	}

	public String getItemSelectorUrl() {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(
				cpContentRequestHelper.getRenderRequest());

		CPDefinitionItemSelectorCriterion cpDefinitionItemSelectorCriterion =
			new CPDefinitionItemSelectorCriterion();

		cpDefinitionItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			Collections.<ItemSelectorReturnType>singletonList(
				new UUIDItemSelectorReturnType()));

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				requestBackedPortletURLFactory, "productDefinitionsSelectItem",
				cpDefinitionItemSelectorCriterion));
	}

	public String getOrderByColumn1() {
		if (_orderByColumn1 != null) {
			return _orderByColumn1;
		}

		PortletPreferences portletPreferences =
			cpContentRequestHelper.getPortletPreferences();

		_orderByColumn1 = GetterUtil.getString(
			portletPreferences.getValue("orderByColumn1", "modifiedDate"));

		return _orderByColumn1;
	}

	public String getOrderByColumn2() {
		if (_orderByColumn2 != null) {
			return _orderByColumn2;
		}

		PortletPreferences portletPreferences =
			cpContentRequestHelper.getPortletPreferences();

		_orderByColumn2 = GetterUtil.getString(
			portletPreferences.getValue("orderByColumn2", "title"));

		return _orderByColumn2;
	}

	public String getOrderByType1() {
		if (_orderByType1 != null) {
			return _orderByType1;
		}

		PortletPreferences portletPreferences =
			cpContentRequestHelper.getPortletPreferences();

		_orderByType1 = GetterUtil.getString(
			portletPreferences.getValue("orderByType1", "DESC"));

		return _orderByType1;
	}

	public String getOrderByType2() {
		if (_orderByType2 != null) {
			return _orderByType2;
		}

		PortletPreferences portletPreferences =
			cpContentRequestHelper.getPortletPreferences();

		_orderByType2 = GetterUtil.getString(
			portletPreferences.getValue("orderByType2", "ASC"));

		return _orderByType2;
	}

	public String getTagSelectorURL() {
		try {
			AssetTagsItemSelectorCriterion assetTagsItemSelectorCriterion =
				new AssetTagsItemSelectorCriterion();

			assetTagsItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
				new AssetTagsItemSelectorReturnType());

			Company company = cpContentRequestHelper.getCompany();

			assetTagsItemSelectorCriterion.setGroupIds(
				new long[] {company.getGroupId()});

			assetTagsItemSelectorCriterion.setMultiSelection(true);

			return String.valueOf(
				_itemSelector.getItemSelectorURL(
					RequestBackedPortletURLFactoryUtil.create(
						cpContentRequestHelper.getRequest()),
					_getPortletNamespace() + "selectTag",
					assetTagsItemSelectorCriterion));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	public String getVocabularyIds() throws Exception {
		ThemeDisplay themeDisplay = cpContentRequestHelper.getThemeDisplay();

		List<AssetVocabulary> vocabularies =
			AssetVocabularyServiceUtil.getGroupVocabularies(
				themeDisplay.getCompanyGroupId());

		return ListUtil.toString(
			vocabularies, AssetVocabulary.VOCABULARY_ID_ACCESSOR);
	}

	private List<AssetCategory> _filterAssetCategories(long[] categoryIds) {
		List<AssetCategory> filteredCategories = new ArrayList<>();

		for (long categoryId : categoryIds) {
			AssetCategory category =
				_assetCategoryLocalService.fetchAssetCategory(categoryId);

			if (category == null) {
				continue;
			}

			filteredCategories.add(category);
		}

		return filteredCategories;
	}

	private String _getPortletNamespace() {
		LiferayPortletResponse liferayPortletResponse =
			cpContentRequestHelper.getLiferayPortletResponse();

		return liferayPortletResponse.getNamespace();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPPublisherConfigurationDisplayContext.class);

	private final AssetCategoryLocalService _assetCategoryLocalService;
	private final AssetTagLocalService _assetTagLocalService;
	private final ConfigurationProvider _configurationProvider;
	private final CPDataSourceRegistry _cpDataSourceRegistry;
	private final CPDefinitionHelper _cpDefinitionHelper;
	private final CPInstanceHelper _cpInstanceHelper;
	private final ItemSelector _itemSelector;
	private String _orderByColumn1;
	private String _orderByColumn2;
	private String _orderByType1;
	private String _orderByType2;

}