/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.internal.info.collection.provider;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.search.AssetSearcherFactory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.util.AssetHelper;
import com.liferay.asset.util.comparator.AssetRendererFactoryTypeNameComparator;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.ConfigurableInfoCollectionProvider;
import com.liferay.info.collection.provider.RelatedInfoItemCollectionProvider;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldSet;
import com.liferay.info.field.type.CategoriesInfoFieldType;
import com.liferay.info.field.type.MultiselectInfoFieldType;
import com.liferay.info.field.type.OptionInfoFieldType;
import com.liferay.info.field.type.SelectInfoFieldType;
import com.liferay.info.form.InfoForm;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.info.localized.bundle.ModelResourceLocalizedValue;
import com.liferay.info.localized.bundle.ResourceBundleInfoLocalizedValue;
import com.liferay.info.pagination.InfoPage;
import com.liferay.info.pagination.Pagination;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.BaseSearcher;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "item.class.name=com.liferay.asset.kernel.model.AssetEntry",
	service = RelatedInfoItemCollectionProvider.class
)
public class AssetEntriesWithSameAssetCategoryRelatedInfoItemCollectionProvider
	implements ConfigurableInfoCollectionProvider<AssetEntry>,
			   RelatedInfoItemCollectionProvider<AssetEntry, AssetEntry> {

	@Override
	public InfoPage<AssetEntry> getCollectionInfoPage(
		CollectionQuery collectionQuery) {

		Object relatedItem = collectionQuery.getRelatedItem();

		if (!(relatedItem instanceof AssetEntry)) {
			return InfoPage.of(
				Collections.emptyList(), collectionQuery.getPagination(), 0);
		}

		AssetEntry assetEntry = (AssetEntry)relatedItem;

		if (ArrayUtil.isEmpty(assetEntry.getCategoryIds())) {
			return InfoPage.of(
				Collections.emptyList(), collectionQuery.getPagination(), 0);
		}

		SearchContext searchContext = _getSearchContext();

		try {
			BooleanFilter assetCategoryIdsBooleanFilter =
				_getAssetCategoryIdsBooleanFilter(
					assetEntry, collectionQuery, searchContext);

			if (!assetCategoryIdsBooleanFilter.hasClauses()) {
				return InfoPage.of(
					Collections.emptyList(), collectionQuery.getPagination(),
					0);
			}

			BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

			booleanQueryImpl.setPreBooleanFilter(assetCategoryIdsBooleanFilter);

			searchContext.setBooleanClauses(
				new BooleanClause[] {
					_getAssetEntryIdBooleanClause(assetEntry),
					BooleanClauseFactoryUtil.create(
						booleanQueryImpl, BooleanClauseOccur.MUST.getName())
				});

			AssetEntryQuery assetEntryQuery = _getAssetEntryQuery(
				collectionQuery);

			Hits hits = _assetHelper.search(
				searchContext, assetEntryQuery, assetEntryQuery.getStart(),
				assetEntryQuery.getEnd());

			Long count = _assetHelper.searchCount(
				searchContext, assetEntryQuery);

			return InfoPage.of(
				_assetHelper.getAssetEntries(hits),
				collectionQuery.getPagination(), count.intValue());
		}
		catch (Exception exception) {
			_log.error("Unable to get asset entries", exception);
		}

		return InfoPage.of(
			Collections.emptyList(), collectionQuery.getPagination(), 0);
	}

	@Override
	public String getCollectionItemClassName() {
		return AssetEntry.class.getName();
	}

	@Override
	public InfoForm getConfigurationInfoForm() {
		return InfoForm.builder(
		).infoFieldSetEntry(
			InfoFieldSet.builder(
			).infoFieldSetEntry(
				_getItemTypesInfoField()
			).descriptionInfoLocalizedValue(
				InfoLocalizedValue.localize(
					getClass(),
					"by-filtering,-you-can-narrow-down-the-results-that-" +
						"appear-on-the-page")
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(getClass(), "filter")
			).name(
				"filter"
			).build()
		).infoFieldSetEntry(
			InfoFieldSet.builder(
			).infoFieldSetEntry(
				InfoField.builder(
				).infoFieldType(
					SelectInfoFieldType.INSTANCE
				).namespace(
					StringPool.BLANK
				).name(
					"assetCategoryRule"
				).attribute(
					SelectInfoFieldType.INLINE, true
				).attribute(
					SelectInfoFieldType.OPTIONS,
					ListUtil.fromArray(
						new OptionInfoFieldType(
							true,
							new ResourceBundleInfoLocalizedValue(
								getClass(), "not-selected"),
							StringPool.BLANK),
						new OptionInfoFieldType(
							new ResourceBundleInfoLocalizedValue(
								getClass(),
								"any-category-of-the-same-vocabulary"),
							"anyAssetCategoryOfTheSameAssetVocabulary"),
						new OptionInfoFieldType(
							new ResourceBundleInfoLocalizedValue(
								getClass(), "a-specific-category"),
							"specificAssetCategory"))
				).labelInfoLocalizedValue(
					InfoLocalizedValue.localize(getClass(), "and-contains")
				).localizable(
					true
				).build()
			).infoFieldSetEntry(
				InfoField.builder(
				).infoFieldType(
					CategoriesInfoFieldType.INSTANCE
				).namespace(
					StringPool.BLANK
				).name(
					"specificAssetCategoryJSONObject"
				).attribute(
					CategoriesInfoFieldType.DEPENDENCY,
					new KeyValuePair(
						"assetCategoryRule", "specificAssetCategory")
				).attribute(
					CategoriesInfoFieldType.INFO_ITEM_SELECTOR_URL,
					_getItemSelectorURL()
				).labelInfoLocalizedValue(
					InfoLocalizedValue.localize(getClass(), "category")
				).localizable(
					false
				).build()
			).descriptionInfoLocalizedValue(
				InfoLocalizedValue.localize(
					getClass(),
					"you-can-also-add-a-rule-for-more-accurate-results")
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(getClass(), "advanced-rule")
			).name(
				"advanced-rule"
			).build()
		).build();
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "items-with-same-categories");
	}

	@Override
	public Class<?> getSourceItemClass() {
		return AssetEntry.class;
	}

	private BooleanFilter
			_getAnyAssetCategoryOfTheSameAssetVocabularyBooleanFilter(
				AssetEntry assetEntry, SearchContext searchContext)
		throws Exception {

		Map<Long, List<Long>> assetEntryAssetVocabulariesMap = new HashMap<>();

		Map<Long, List<Long>> otherAssetCategoriesAssetVocabulariesMap =
			new HashMap<>();

		for (long assetEntryAssetCategoryId : assetEntry.getCategoryIds()) {
			AssetCategory assetCategory =
				_assetCategoryLocalService.fetchAssetCategory(
					assetEntryAssetCategoryId);

			if (assetCategory == null) {
				continue;
			}

			List<Long> assetCategoryIds =
				assetEntryAssetVocabulariesMap.computeIfAbsent(
					assetCategory.getVocabularyId(), key -> new ArrayList<>());

			assetCategoryIds.add(assetEntryAssetCategoryId);

			if (!otherAssetCategoriesAssetVocabulariesMap.containsKey(
					assetCategory.getVocabularyId())) {

				otherAssetCategoriesAssetVocabulariesMap.put(
					assetCategory.getVocabularyId(),
					ListUtil.filter(
						ListUtil.toList(
							_assetCategoryLocalService.getVocabularyCategories(
								assetCategory.getVocabularyId(),
								QueryUtil.ALL_POS, QueryUtil.ALL_POS, null),
							AssetCategory.CATEGORY_ID_ACCESSOR),
						categoryId -> !ArrayUtil.contains(
							assetEntry.getCategoryIds(), categoryId)));
			}
		}

		List<BooleanFilter> booleanFilters = new ArrayList<>();

		for (Map.Entry<Long, List<Long>> entry :
				assetEntryAssetVocabulariesMap.entrySet()) {

			Long assetVocabularyId = entry.getKey();

			List<Long> otherAssetCategoryIds =
				otherAssetCategoriesAssetVocabulariesMap.get(assetVocabularyId);

			if (ListUtil.isEmpty(otherAssetCategoryIds)) {
				continue;
			}

			for (long assetCategoryId : entry.getValue()) {
				booleanFilters.add(
					_getAssetSearcherPreBooleanFilter(
						new long[] {assetCategoryId},
						ArrayUtil.toLongArray(otherAssetCategoryIds),
						searchContext));
			}
		}

		BooleanFilter assetCategoryIdsBooleanFilter = new BooleanFilter();

		for (BooleanFilter booleanFilter : booleanFilters) {
			assetCategoryIdsBooleanFilter.add(
				booleanFilter, BooleanClauseOccur.SHOULD);
		}

		return assetCategoryIdsBooleanFilter;
	}

	private BooleanFilter _getAssetCategoryIdsBooleanFilter(
			AssetEntry assetEntry, CollectionQuery collectionQuery,
			SearchContext searchContext)
		throws Exception {

		Tuple assetCategoryRuleTuple = _getAssetCategoryRuleTuple(
			collectionQuery);

		if ((assetCategoryRuleTuple.getSize() == 1) &&
			Objects.equals(
				assetCategoryRuleTuple.getObject(0),
				"anyAssetCategoryOfTheSameAssetVocabulary")) {

			return _getAnyAssetCategoryOfTheSameAssetVocabularyBooleanFilter(
				assetEntry, searchContext);
		}

		if ((assetCategoryRuleTuple.getSize() == 2) &&
			Objects.equals(
				assetCategoryRuleTuple.getObject(0), "specificAssetCategory")) {

			return _getAssetSearcherPreBooleanFilter(
				new long[] {
					GetterUtil.getLong(assetCategoryRuleTuple.getObject(1))
				},
				assetEntry.getCategoryIds(), searchContext);
		}

		return _getAssetSearcherPreBooleanFilter(
			new long[0], assetEntry.getCategoryIds(), searchContext);
	}

	private Tuple _getAssetCategoryRuleTuple(CollectionQuery collectionQuery) {
		Map<String, String[]> configuration =
			collectionQuery.getConfiguration();

		if ((configuration == null) ||
			ArrayUtil.isEmpty(configuration.get("assetCategoryRule"))) {

			return new Tuple();
		}

		String[] assetCategoryRules = configuration.get("assetCategoryRule");

		String assetCategoryRule = assetCategoryRules[0];

		if (Objects.equals(assetCategoryRule, "specificAssetCategory") &&
			ArrayUtil.isNotEmpty(
				configuration.get("specificAssetCategoryJSONObject"))) {

			String[] specificAssetCategoryJSONObjects = configuration.get(
				"specificAssetCategoryJSONObject");

			JSONObject specificAssetCategoryJSONObject = null;

			try {
				specificAssetCategoryJSONObject = _jsonFactory.createJSONObject(
					specificAssetCategoryJSONObjects[0]);
			}
			catch (JSONException jsonException) {
				if (_log.isDebugEnabled()) {
					_log.debug(jsonException);
				}

				return new Tuple();
			}

			long specificAssetCategoryId =
				specificAssetCategoryJSONObject.getLong("classPK");

			if (specificAssetCategoryId <= 0) {
				return new Tuple();
			}

			AssetCategory assetCategory =
				_assetCategoryLocalService.fetchAssetCategory(
					specificAssetCategoryId);

			if (assetCategory == null) {
				return new Tuple();
			}

			return new Tuple("specificAssetCategory", specificAssetCategoryId);
		}

		if (Objects.equals(
				assetCategoryRule,
				"anyAssetCategoryOfTheSameAssetVocabulary")) {

			return new Tuple(assetCategoryRule);
		}

		return new Tuple();
	}

	private BooleanClause<Query> _getAssetEntryIdBooleanClause(
		AssetEntry assetEntry) {

		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		BooleanFilter assetEntryIdBooleanFilter = new BooleanFilter();

		TermsFilter assetEntryIdTermsFilter = new TermsFilter(
			Field.ASSET_ENTRY_ID);

		assetEntryIdTermsFilter.addValue(
			String.valueOf(assetEntry.getEntryId()));

		assetEntryIdBooleanFilter.add(
			assetEntryIdTermsFilter, BooleanClauseOccur.MUST_NOT);

		booleanQueryImpl.setPreBooleanFilter(assetEntryIdBooleanFilter);

		return BooleanClauseFactoryUtil.create(
			booleanQueryImpl, BooleanClauseOccur.MUST.getName());
	}

	private AssetEntryQuery _getAssetEntryQuery(
		CollectionQuery collectionQuery) {

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		assetEntryQuery.setClassNameIds(_getClassNameIds(collectionQuery));
		assetEntryQuery.setEnablePermissions(true);

		Pagination pagination = collectionQuery.getPagination();

		if (pagination != null) {
			assetEntryQuery.setEnd(pagination.getEnd());
		}

		assetEntryQuery.setGroupIds(
			new long[] {serviceContext.getScopeGroupId()});
		assetEntryQuery.setOrderByCol1(Field.MODIFIED_DATE);
		assetEntryQuery.setOrderByType1("DESC");

		if (pagination != null) {
			assetEntryQuery.setStart(pagination.getStart());
		}

		return assetEntryQuery;
	}

	private BooleanFilter _getAssetSearcherPreBooleanFilter(
			long[] allCategoryIds, long[] anyCategoryIds,
			SearchContext searchContext)
		throws Exception {

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setAllCategoryIds(allCategoryIds);
		assetEntryQuery.setAnyCategoryIds(anyCategoryIds);

		BaseSearcher baseSearcher = _assetSearcherFactory.createBaseSearcher(
			assetEntryQuery);

		BooleanQuery booleanQuery = baseSearcher.getFullQuery(searchContext);

		return booleanQuery.getPreBooleanFilter();
	}

	private long[] _getClassNameIds(CollectionQuery collectionQuery) {
		Map<String, String[]> configuration =
			collectionQuery.getConfiguration();

		if (MapUtil.isNotEmpty(configuration) &&
			ArrayUtil.isNotEmpty(configuration.get("item_types"))) {

			List<Long> classNameIds = new ArrayList<>();

			String[] itemTypes = configuration.get("item_types");

			for (String itemType : itemTypes) {
				if (Validator.isNotNull(itemType)) {
					classNameIds.add(_portal.getClassNameId(itemType));
				}
			}

			if (ListUtil.isNotEmpty(classNameIds)) {
				return ArrayUtil.toArray(classNameIds.toArray(new Long[0]));
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		return AssetRendererFactoryRegistryUtil.getIndexableClassNameIds(
			serviceContext.getCompanyId(), true);
	}

	private String _getItemSelectorURL() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			return null;
		}

		HttpServletRequest httpServletRequest = serviceContext.getRequest();

		if (httpServletRequest == null) {
			return null;
		}

		InfoItemItemSelectorCriterion itemSelectorCriterion =
			new InfoItemItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new InfoItemItemSelectorReturnType());
		itemSelectorCriterion.setItemType(AssetCategory.class.getName());

		String namespace = StringPool.BLANK;

		LiferayPortletResponse liferayPortletResponse =
			serviceContext.getLiferayPortletResponse();

		if (liferayPortletResponse != null) {
			namespace = liferayPortletResponse.getNamespace();
		}

		return PortletURLBuilder.create(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(httpServletRequest),
				namespace + "selectInfoItem", itemSelectorCriterion)
		).buildString();
	}

	private InfoField _getItemTypesInfoField() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		List<AssetRendererFactory<?>> assetRendererFactories = ListUtil.filter(
			AssetRendererFactoryRegistryUtil.getAssetRendererFactories(
				serviceContext.getCompanyId(), true),
			assetRendererFactory -> {
				if (!assetRendererFactory.isCategorizable()) {
					return false;
				}

				Indexer<?> indexer = IndexerRegistryUtil.getIndexer(
					_portal.getClassName(
						assetRendererFactory.getClassNameId()));

				if (indexer == null) {
					return false;
				}

				return true;
			});

		assetRendererFactories.sort(
			new AssetRendererFactoryTypeNameComparator(
				serviceContext.getLocale()));

		InfoField.FinalStep finalStep = InfoField.builder(
		).infoFieldType(
			MultiselectInfoFieldType.INSTANCE
		).namespace(
			StringPool.BLANK
		).name(
			"item_types"
		).attribute(
			MultiselectInfoFieldType.OPTIONS,
			TransformUtil.transform(
				assetRendererFactories,
				assetRendererFactory -> new OptionInfoFieldType(
					new ModelResourceLocalizedValue(
						assetRendererFactory.getClassName()),
					assetRendererFactory.getClassName()))
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(getClass(), "item-type")
		).localizable(
			true
		);

		return finalStep.build();
	}

	private SearchContext _getSearchContext() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		return SearchContextFactory.getInstance(
			new long[0], new String[0],
			HashMapBuilder.<String, Serializable>put(
				Field.STATUS, WorkflowConstants.STATUS_APPROVED
			).put(
				"head", true
			).build(),
			serviceContext.getCompanyId(), null, themeDisplay.getLayout(), null,
			serviceContext.getScopeGroupId(), null, serviceContext.getUserId());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetEntriesWithSameAssetCategoryRelatedInfoItemCollectionProvider.
			class);

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetHelper _assetHelper;

	@Reference
	private AssetSearcherFactory _assetSearcherFactory;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}