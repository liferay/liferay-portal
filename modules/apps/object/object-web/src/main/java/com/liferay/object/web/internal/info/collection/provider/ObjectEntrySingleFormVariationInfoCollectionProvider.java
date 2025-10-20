/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.collection.provider;

import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.depot.util.SiteConnectedGroupGroupProviderUtil;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.ConfigurableInfoCollectionProvider;
import com.liferay.info.collection.provider.FilteredInfoCollectionProvider;
import com.liferay.info.collection.provider.SingleFormVariationInfoCollectionProvider;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldSet;
import com.liferay.info.field.InfoFieldSetEntry;
import com.liferay.info.field.type.MultiselectInfoFieldType;
import com.liferay.info.field.type.OptionInfoFieldType;
import com.liferay.info.field.type.SelectInfoFieldType;
import com.liferay.info.filter.InfoFilter;
import com.liferay.info.filter.KeywordsInfoFilter;
import com.liferay.info.form.InfoForm;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.info.localized.SingleValueInfoLocalizedValue;
import com.liferay.info.localized.bundle.FunctionInfoLocalizedValue;
import com.liferay.info.localized.bundle.ResourceBundleInfoLocalizedValue;
import com.liferay.info.pagination.InfoPage;
import com.liferay.info.pagination.Pagination;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectLayoutBoxConstants;
import com.liferay.object.info.collection.provider.util.ObjectEntryInfoCollectionProviderUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectLayout;
import com.liferay.object.model.ObjectLayoutTab;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectLayoutLocalService;
import com.liferay.object.web.internal.util.ObjectEntryUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.NestedQuery;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portlet.asset.util.comparator.AssetTagNameComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Jorge Ferrer
 * @author Guilherme Camacho
 */
public class ObjectEntrySingleFormVariationInfoCollectionProvider
	implements ConfigurableInfoCollectionProvider<ObjectEntry>,
			   FilteredInfoCollectionProvider<ObjectEntry>,
			   SingleFormVariationInfoCollectionProvider<ObjectEntry> {

	public ObjectEntrySingleFormVariationInfoCollectionProvider(
		AssetCategoryLocalService assetCategoryLocalService,
		AssetTagLocalService assetTagLocalService,
		AssetVocabularyLocalService assetVocabularyLocalService,
		GroupLocalService groupLocalService,
		ListTypeEntryLocalService listTypeEntryLocalService,
		ObjectDefinition objectDefinition,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectEntryManagerRegistry objectEntryManagerRegistry,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectLayoutLocalService objectLayoutLocalService,
		ObjectScopeProviderRegistry objectScopeProviderRegistry) {

		_assetCategoryLocalService = assetCategoryLocalService;
		_assetTagLocalService = assetTagLocalService;
		_assetVocabularyLocalService = assetVocabularyLocalService;
		_groupLocalService = groupLocalService;
		_listTypeEntryLocalService = listTypeEntryLocalService;
		_objectDefinition = objectDefinition;
		_objectEntryLocalService = objectEntryLocalService;
		_objectEntryManagerRegistry = objectEntryManagerRegistry;
		_objectFieldLocalService = objectFieldLocalService;
		_objectLayoutLocalService = objectLayoutLocalService;
		_objectScopeProviderRegistry = objectScopeProviderRegistry;
	}

	@Override
	public InfoPage<ObjectEntry> getCollectionInfoPage(
		CollectionQuery collectionQuery) {

		try {
			if (!_objectDefinition.isAccountEntryRestricted() &&
				_objectDefinition.isDefaultStorageType() &&
				_objectDefinition.isEnableIndexSearch()) {

				return _getCollectionInfoPageByIndexer(collectionQuery);
			}

			return _getCollectionInfoPageByObjectEntryManager(collectionQuery);
		}
		catch (Exception exception) {
			throw new RuntimeException(
				"Unable to get object entries for object definition " +
					_objectDefinition.getObjectDefinitionId(),
				exception);
		}
	}

	@Override
	public String getCollectionItemClassName() {
		return _objectDefinition.getClassName();
	}

	@Override
	public InfoForm getConfigurationInfoForm() {
		return InfoForm.builder(
		).infoFieldSetEntries(
			_getInfoFieldSetEntries()
		).infoFieldSetEntry(
			InfoFieldSet.builder(
			).infoFieldSetEntry(
				unsafeConsumer -> {
					InfoField<?> infoField = _getInfoField();

					if (infoField != null) {
						unsafeConsumer.accept(infoField);
					}
				}
			).build()
		).infoFieldSetEntry(
			InfoFieldSet.builder(
			).infoFieldSetEntry(
				unsafeConsumer -> {
					for (ObjectField objectField :
							_objectFieldLocalService.getObjectFields(
								_objectDefinition.getObjectDefinitionId())) {

						if (!(Objects.equals(
								objectField.getDBType(),
								ObjectFieldConstants.DB_TYPE_BOOLEAN) ||
							  (Objects.equals(
								  objectField.getDBType(),
								  ObjectFieldConstants.DB_TYPE_STRING) &&
							   (objectField.getListTypeDefinitionId() != 0))) ||
							!objectField.isIndexed()) {

							continue;
						}

						unsafeConsumer.accept(
							InfoField.builder(
							).infoFieldType(
								SelectInfoFieldType.INSTANCE
							).namespace(
								StringPool.BLANK
							).name(
								objectField.getName()
							).attribute(
								SelectInfoFieldType.OPTIONS,
								_getOptions(objectField)
							).labelInfoLocalizedValue(
								InfoLocalizedValue.<String>builder(
								).defaultLocale(
									LocaleUtil.fromLanguageId(
										objectField.getDefaultLanguageId())
								).values(
									objectField.getLabelMap()
								).build()
							).localizable(
								true
							).build());
					}
				}
			).build()
		).build();
	}

	@Override
	public String getFormVariationKey() {
		return String.valueOf(_objectDefinition.getObjectDefinitionId());
	}

	@Override
	public String getKey() {
		return _objectDefinition.getClassName();
	}

	@Override
	public String getLabel(Locale locale) {
		return _objectDefinition.getPluralLabel(locale);
	}

	@Override
	public List<InfoFilter> getSupportedInfoFilters() {
		return Arrays.asList(new KeywordsInfoFilter());
	}

	@Override
	public boolean isAvailable() {
		if (_objectDefinition.getCompanyId() !=
				CompanyThreadLocal.getCompanyId()) {

			return false;
		}

		return true;
	}

	private SearchContext _buildSearchContext(CollectionQuery collectionQuery)
		throws Exception {

		SearchContext searchContext = new SearchContext();

		List<String> assetCategoryIds = new ArrayList<>();

		Map<String, String[]> configuration =
			collectionQuery.getConfiguration();

		if (configuration == null) {
			configuration = Collections.emptyMap();
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		for (AssetVocabulary assetVocabulary :
				_getAssetVocabularies(serviceContext)) {

			String[] assetCategoryIdsArray = configuration.get(
				String.valueOf(assetVocabulary.getVocabularyId()));

			if ((assetCategoryIdsArray != null) &&
				!StringUtil.equals(assetCategoryIdsArray[0], "null")) {

				Collections.addAll(assetCategoryIds, assetCategoryIdsArray);
			}
		}

		searchContext.setAssetCategoryIds(
			ListUtil.toLongArray(assetCategoryIds, Long::parseLong));

		String[] assetTagNames = configuration.get(Field.ASSET_TAG_NAMES);

		if (ArrayUtil.isNotEmpty(assetTagNames) &&
			Validator.isNotNull(assetTagNames[0])) {

			searchContext.setAssetTagNames(assetTagNames);
		}

		searchContext.setAttribute(
			Field.STATUS, WorkflowConstants.STATUS_APPROVED);
		searchContext.setAttribute(
			"objectDefinitionId", _objectDefinition.getObjectDefinitionId());
		searchContext.setBooleanClauses(_getBooleanClauses(collectionQuery));
		searchContext.setCompanyId(serviceContext.getCompanyId());

		Pagination pagination = collectionQuery.getPagination();

		searchContext.setEnd(pagination.getEnd());

		searchContext.setGroupIds(new long[] {_getGroupId()});

		KeywordsInfoFilter keywordsInfoFilter = collectionQuery.getInfoFilter(
			KeywordsInfoFilter.class);

		if (keywordsInfoFilter != null) {
			searchContext.setKeywords(keywordsInfoFilter.getKeywords());
		}

		com.liferay.info.sort.Sort sort = collectionQuery.getSort();

		if (sort == null) {
			searchContext.setSorts(
				SortFactoryUtil.create(
					Field.CREATE_DATE, Sort.LONG_TYPE, false));
		}

		searchContext.setStart(pagination.getStart());

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return searchContext;
	}

	private List<AssetVocabulary> _getAssetVocabularies(
		ServiceContext serviceContext) {

		try {
			return ListUtil.filter(
				_assetVocabularyLocalService.getGroupVocabularies(
					SiteConnectedGroupGroupProviderUtil.
						getCurrentAndAncestorSiteAndDepotGroupIds(
							serviceContext.getScopeGroupId())),
				assetVocabulary ->
					assetVocabulary.isAssociatedToClassNameIdAndClassTypePK(
						PortalUtil.getClassNameId(
							_objectDefinition.getClassName()),
						AssetCategoryConstants.ALL_CLASS_TYPE_PK));
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return Collections.emptyList();
		}
	}

	private BooleanClause[] _getBooleanClauses(CollectionQuery collectionQuery)
		throws Exception {

		BooleanQuery booleanQuery = new BooleanQueryImpl();

		List<ObjectField> objectFields =
			_objectFieldLocalService.getObjectFields(
				_objectDefinition.getObjectDefinitionId());

		Map<String, String[]> configuration =
			collectionQuery.getConfiguration();

		if (configuration == null) {
			configuration = Collections.emptyMap();
		}

		for (Map.Entry<String, String[]> entry : configuration.entrySet()) {
			String[] values = entry.getValue();

			if (ArrayUtil.isEmpty(values) || values[0].isEmpty()) {
				continue;
			}

			ObjectField objectField = _getObjectField(
				entry.getKey(), objectFields);

			if (objectField == null) {
				continue;
			}

			BooleanQuery nestedBooleanQuery = new BooleanQueryImpl();

			nestedBooleanQuery.add(
				new TermQueryImpl(
					_getFieldName(objectField), entry.getValue()[0]),
				BooleanClauseOccur.MUST);
			nestedBooleanQuery.add(
				new TermQueryImpl("nestedFieldArray.fieldName", entry.getKey()),
				BooleanClauseOccur.MUST);

			booleanQuery.add(
				new NestedQuery("nestedFieldArray", nestedBooleanQuery),
				BooleanClauseOccur.MUST);
		}

		return new BooleanClause[] {
			BooleanClauseFactoryUtil.create(
				booleanQuery, BooleanClauseOccur.MUST.getName())
		};
	}

	private InfoPage<ObjectEntry> _getCollectionInfoPageByIndexer(
			CollectionQuery collectionQuery)
		throws Exception {

		Indexer<ObjectEntry> indexer = IndexerRegistryUtil.getIndexer(
			_objectDefinition.getClassName());

		Hits hits = indexer.search(_buildSearchContext(collectionQuery));

		return InfoPage.of(
			TransformUtil.transformToList(
				hits.getDocs(),
				document -> {
					long classPK = GetterUtil.getLong(
						document.get(Field.ENTRY_CLASS_PK));

					return _objectEntryLocalService.fetchObjectEntry(classPK);
				}),
			collectionQuery.getPagination(), hits.getLength());
	}

	private InfoPage<ObjectEntry> _getCollectionInfoPageByObjectEntryManager(
			CollectionQuery collectionQuery)
		throws Exception {

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				_objectDefinition.getStorageType());

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		Group scopeGroup = themeDisplay.getScopeGroup();

		Page<com.liferay.object.rest.dto.v1_0.ObjectEntry> objectEntriesPage =
			objectEntryManager.getObjectEntries(
				themeDisplay.getCompanyId(), _objectDefinition,
				scopeGroup.getGroupKey(), null,
				new DefaultDTOConverterContext(
					false, null, null, null, null, themeDisplay.getLocale(),
					null, themeDisplay.getUser()),
				_getFilterString(collectionQuery),
				ObjectEntryInfoCollectionProviderUtil.getPagination(
					collectionQuery.getPagination()),
				ObjectEntryInfoCollectionProviderUtil.getSearch(
					collectionQuery),
				null);

		return InfoPage.of(
			TransformUtil.transform(
				new ArrayList<>(objectEntriesPage.getItems()),
				objectEntry -> ObjectEntryUtil.toObjectEntry(
					_objectDefinition, objectEntry)),
			collectionQuery.getPagination(),
			(int)objectEntriesPage.getTotalCount());
	}

	private String _getFieldName(ObjectField objectField) {
		if (Objects.equals(
				objectField.getDBType(),
				ObjectFieldConstants.DB_TYPE_BOOLEAN)) {

			return "nestedFieldArray.value_boolean";
		}
		else if (Objects.equals(
					objectField.getDBType(),
					ObjectFieldConstants.DB_TYPE_STRING)) {

			return "nestedFieldArray.value_keyword_lowercase";
		}

		return "";
	}

	private String _getFilterString(CollectionQuery collectionQuery) {
		Map<String, String[]> configuration =
			collectionQuery.getConfiguration();

		if (configuration == null) {
			return null;
		}

		StringBundler sb = new StringBundler();

		List<ObjectField> objectFields =
			_objectFieldLocalService.getObjectFields(
				_objectDefinition.getObjectDefinitionId());

		for (Map.Entry<String, String[]> entry : configuration.entrySet()) {
			if (Validator.isNull(entry.getValue()[0])) {
				continue;
			}

			ObjectField objectField = _getObjectField(
				entry.getKey(), objectFields);

			if (objectField == null) {
				continue;
			}

			sb.append(entry.getKey());
			sb.append(" eq ");

			if (Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN)) {

				sb.append(entry.getValue()[0]);
			}
			else {
				sb.append(StringPool.APOSTROPHE);
				sb.append(entry.getValue()[0]);
				sb.append(StringPool.APOSTROPHE);
			}

			sb.append(" and ");
		}

		return StringUtil.removeLast(sb.toString(), " and ");
	}

	private long _getGroupId() throws Exception {
		ObjectScopeProvider objectScopeProvider =
			_objectScopeProviderRegistry.getObjectScopeProvider(
				_objectDefinition.getScope());

		if (!objectScopeProvider.isGroupAware()) {
			return 0;
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		return objectScopeProvider.getGroupId(serviceContext.getRequest());
	}

	private InfoField<?> _getInfoField() {
		if (!StringUtil.equals(
				_objectDefinition.getStorageType(),
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT) ||
			!_hasCategorizationObjectLayoutBox()) {

			return null;
		}

		long groupId = 0;

		if (StringUtil.equals(
				_objectDefinition.getScope(),
				ObjectDefinitionConstants.SCOPE_COMPANY)) {

			try {
				Group group = _groupLocalService.getCompanyGroup(
					_objectDefinition.getCompanyId());

				groupId = group.getGroupId();
			}
			catch (PortalException portalException) {
				_log.error(portalException);
			}
		}
		else {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			groupId = serviceContext.getScopeGroupId();
		}

		return InfoField.builder(
		).infoFieldType(
			MultiselectInfoFieldType.INSTANCE
		).namespace(
			StringPool.BLANK
		).name(
			Field.ASSET_TAG_NAMES
		).attribute(
			MultiselectInfoFieldType.OPTIONS,
			TransformUtil.transform(
				_assetTagLocalService.getGroupTags(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					new AssetTagNameComparator(true)),
				assetTag -> new OptionInfoFieldType(
					new SingleValueInfoLocalizedValue<>(assetTag.getName()),
					assetTag.getName()))
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(getClass(), "tag")
		).localizable(
			true
		).build();
	}

	private List<InfoFieldSetEntry> _getInfoFieldSetEntries() {
		if (!StringUtil.equals(
				_objectDefinition.getStorageType(),
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT) ||
			!_hasCategorizationObjectLayoutBox()) {

			return Collections.emptyList();
		}

		List<InfoFieldSetEntry> fieldSetEntries = new ArrayList<>();

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		for (AssetVocabulary assetVocabulary :
				_getAssetVocabularies(serviceContext)) {

			List<OptionInfoFieldType> optionInfoFieldTypes =
				TransformUtil.transform(
					_assetCategoryLocalService.getVocabularyCategories(
						assetVocabulary.getVocabularyId(), QueryUtil.ALL_POS,
						QueryUtil.ALL_POS, null),
					assetCategory -> new OptionInfoFieldType(
						new SingleValueInfoLocalizedValue<>(
							assetCategory.getName()),
						String.valueOf(assetCategory.getCategoryId())));

			if (!optionInfoFieldTypes.isEmpty()) {
				fieldSetEntries.add(
					InfoField.builder(
					).infoFieldType(
						MultiselectInfoFieldType.INSTANCE
					).namespace(
						StringPool.BLANK
					).name(
						String.valueOf(assetVocabulary.getVocabularyId())
					).attribute(
						MultiselectInfoFieldType.OPTIONS, optionInfoFieldTypes
					).labelInfoLocalizedValue(
						InfoLocalizedValue.singleValue(
							assetVocabulary.getTitle(
								serviceContext.getLocale()))
					).localizable(
						true
					).build());
			}
		}

		return fieldSetEntries;
	}

	private ObjectField _getObjectField(
		String name, List<ObjectField> objectFields) {

		for (ObjectField objectField : objectFields) {
			if (Objects.equals(name, objectField.getName())) {
				return objectField;
			}
		}

		return null;
	}

	private List<OptionInfoFieldType> _getOptions(ObjectField objectField) {
		List<OptionInfoFieldType> optionInfoFieldTypes = new ArrayList<>();

		optionInfoFieldTypes.add(
			new OptionInfoFieldType(
				new ResourceBundleInfoLocalizedValue(
					getClass(), "choose-an-option"),
				""));

		if (Objects.equals(
				objectField.getDBType(),
				ObjectFieldConstants.DB_TYPE_BOOLEAN)) {

			optionInfoFieldTypes.add(
				new OptionInfoFieldType(
					new ResourceBundleInfoLocalizedValue(getClass(), "true"),
					"true"));
			optionInfoFieldTypes.add(
				new OptionInfoFieldType(
					new ResourceBundleInfoLocalizedValue(getClass(), "false"),
					"false"));
		}
		else if (objectField.getListTypeDefinitionId() != 0) {
			optionInfoFieldTypes.addAll(
				TransformUtil.transform(
					_listTypeEntryLocalService.getListTypeEntries(
						objectField.getListTypeDefinitionId(),
						QueryUtil.ALL_POS, QueryUtil.ALL_POS),
					listTypeEntry -> new OptionInfoFieldType(
						new FunctionInfoLocalizedValue<>(
							listTypeEntry::getName),
						listTypeEntry.getKey())));
		}

		return optionInfoFieldTypes;
	}

	private boolean _hasCategorizationObjectLayoutBox() {
		ObjectLayout objectLayout = null;

		try {
			objectLayout = _objectLayoutLocalService.getDefaultObjectLayout(
				_objectDefinition.getObjectDefinitionId());
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return false;
		}

		for (ObjectLayoutTab objectLayoutTab :
				objectLayout.getObjectLayoutTabs()) {

			if (ListUtil.exists(
					objectLayoutTab.getObjectLayoutBoxes(),
					objectLayoutBox -> StringUtil.equals(
						objectLayoutBox.getType(),
						ObjectLayoutBoxConstants.TYPE_CATEGORIZATION))) {

				return true;
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntrySingleFormVariationInfoCollectionProvider.class);

	private final AssetCategoryLocalService _assetCategoryLocalService;
	private final AssetTagLocalService _assetTagLocalService;
	private final AssetVocabularyLocalService _assetVocabularyLocalService;
	private final GroupLocalService _groupLocalService;
	private final ListTypeEntryLocalService _listTypeEntryLocalService;
	private final ObjectDefinition _objectDefinition;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectEntryManagerRegistry _objectEntryManagerRegistry;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectLayoutLocalService _objectLayoutLocalService;
	private final ObjectScopeProviderRegistry _objectScopeProviderRegistry;

}