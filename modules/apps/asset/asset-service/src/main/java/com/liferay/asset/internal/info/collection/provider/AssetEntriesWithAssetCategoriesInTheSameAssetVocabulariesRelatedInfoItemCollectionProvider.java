/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.internal.info.collection.provider;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryModel;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.util.AssetHelper;
import com.liferay.asset.util.comparator.AssetRendererFactoryTypeNameComparator;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.ConfigurableInfoCollectionProvider;
import com.liferay.info.collection.provider.RelatedInfoItemCollectionProvider;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.type.MultiselectInfoFieldType;
import com.liferay.info.field.type.OptionInfoFieldType;
import com.liferay.info.form.InfoForm;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.info.localized.bundle.ModelResourceLocalizedValue;
import com.liferay.info.pagination.InfoPage;
import com.liferay.info.pagination.Pagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "item.class.name=com.liferay.asset.kernel.model.AssetEntry",
	service = RelatedInfoItemCollectionProvider.class
)
public class
	AssetEntriesWithAssetCategoriesInTheSameAssetVocabulariesRelatedInfoItemCollectionProvider
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

		List<AssetCategory> assetCategories = assetEntry.getCategories();

		if (ListUtil.isEmpty(assetCategories)) {
			return InfoPage.of(
				Collections.emptyList(), collectionQuery.getPagination(), 0);
		}

		try {
			AssetEntryQuery assetEntryQuery = _getAssetEntryQuery(
				assetCategories, collectionQuery, _getGroupIds());

			SearchContext searchContext = _getSearchContext(assetEntry);

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
			_getItemTypesInfoField()
		).build();
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(
			locale, "items-with-categories-in-the-same-vocabularies");
	}

	@Override
	public Class<?> getSourceItemClass() {
		return AssetEntry.class;
	}

	private long[] _getAssetCategories(
		List<AssetCategory> assetEntryAssetCategories) {

		List<AssetCategory> assetCategories = new ArrayList<>();
		Set<Long> assetVocabularies = new HashSet<>();

		for (AssetCategory assetCategory : assetEntryAssetCategories) {
			if (!assetVocabularies.contains(assetCategory.getVocabularyId())) {
				assetCategories.addAll(
					_assetCategoryLocalService.getVocabularyCategories(
						assetCategory.getVocabularyId(), QueryUtil.ALL_POS,
						QueryUtil.ALL_POS, null));
			}

			assetVocabularies.add(assetCategory.getVocabularyId());
		}

		return ListUtil.toLongArray(
			assetCategories, AssetCategoryModel::getCategoryId);
	}

	private BooleanClause[] _getAssetEntryIdBooleanClause(
		AssetEntry assetEntry) {

		BooleanQuery booleanQuery = new BooleanQuery();

		BooleanFilter assetEntryIdBooleanFilter = new BooleanFilter();

		TermsFilter assetEntryIdTermsFilter = new TermsFilter(
			Field.ASSET_ENTRY_ID);

		assetEntryIdTermsFilter.addValue(
			String.valueOf(assetEntry.getEntryId()));

		assetEntryIdBooleanFilter.add(
			assetEntryIdTermsFilter, BooleanClauseOccur.MUST_NOT);

		booleanQuery.setPreBooleanFilter(assetEntryIdBooleanFilter);

		return new BooleanClause[] {
			new BooleanClause<>(booleanQuery, BooleanClauseOccur.MUST)
		};
	}

	private AssetEntryQuery _getAssetEntryQuery(
		List<AssetCategory> assetEntryAssetCategories,
		CollectionQuery collectionQuery, long[] groupIds) {

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setAnyCategoryIds(
			_getAssetCategories(assetEntryAssetCategories));
		assetEntryQuery.setClassNameIds(_getClassNameIds(collectionQuery));
		assetEntryQuery.setEnablePermissions(true);

		Pagination pagination = collectionQuery.getPagination();

		if (pagination != null) {
			assetEntryQuery.setEnd(pagination.getEnd());
		}

		assetEntryQuery.setGroupIds(groupIds);

		assetEntryQuery.setOrderByCol1(Field.MODIFIED_DATE);
		assetEntryQuery.setOrderByType1("DESC");

		if (pagination != null) {
			assetEntryQuery.setStart(pagination.getStart());
		}

		return assetEntryQuery;
	}

	private long[] _getClassNameIds(CollectionQuery collectionQuery) {
		Map<String, String[]> configuration =
			collectionQuery.getConfiguration();

		if (MapUtil.isNotEmpty(configuration) &&
			ArrayUtil.isNotEmpty(configuration.get("item_types"))) {

			List<Long> classNameIds = new ArrayList<>();

			String[] itemTypes = configuration.get("item_types");

			for (String itemType : itemTypes) {
				if (Validator.isNull(itemType)) {
					continue;
				}

				classNameIds.add(_portal.getClassNameId(itemType));
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

	private long[] _getGroupIds() throws PortalException {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		long scopeGroupId = serviceContext.getScopeGroupId();

		return ArrayUtil.append(
			new long[] {scopeGroupId},
			ListUtil.toLongArray(
				_depotEntryLocalService.getGroupConnectedDepotEntries(
					scopeGroupId, DepotConstants.TYPE_SPACE, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS),
				DepotEntry::getGroupId));
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

	private SearchContext _getSearchContext(AssetEntry assetEntry) {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		SearchContext searchContext = SearchContextFactory.getInstance(
			new long[0], new String[0],
			HashMapBuilder.<String, Serializable>put(
				Field.STATUS, WorkflowConstants.STATUS_APPROVED
			).put(
				"head", true
			).build(),
			serviceContext.getCompanyId(), null, themeDisplay.getLayout(), null,
			serviceContext.getScopeGroupId(), null, serviceContext.getUserId());

		searchContext.setBooleanClauses(
			_getAssetEntryIdBooleanClause(assetEntry));

		return searchContext;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetEntriesWithAssetCategoriesInTheSameAssetVocabulariesRelatedInfoItemCollectionProvider.class);

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetHelper _assetHelper;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}