/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.internal.selection.v1_0;

import com.liferay.bulk.rest.dto.v1_0.BulkAction;
import com.liferay.bulk.rest.dto.v1_0.BulkActionItem;
import com.liferay.bulk.rest.dto.v1_0.DefaultPermissionBulkAction;
import com.liferay.bulk.rest.dto.v1_0.SelectionScope;
import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.bulk.selection.BulkSelectionFactoryRegistry;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.MatchAllQuery;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.rest.dto.v1_0.SearchRequestBody;
import com.liferay.portal.search.rest.util.FilterUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.validation.ValidationException;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Andrea Sbarra
 */
public class BulkActionBulkSelectionFactory {

	public BulkSelection<Object> create() {
		BulkSelectionFactory<Object> bulkSelectionFactory =
			_bulkSelectionFactoryRegistry.getBulkSelectionFactory(
				Object.class.getName());

		return bulkSelectionFactory.create(
			HashMapBuilder.put(
				"rowIds",
				() -> {
					_validate();

					SelectionScope selectionScope =
						_bulkAction.getSelectionScope();

					if (GetterUtil.getBoolean(selectionScope.getSelectAll())) {
						return _searchRowIds();
					}

					if (ArrayUtil.isEmpty(_bulkAction.getBulkActionItems())) {
						return new String[0];
					}

					return _getSelectedItemsRowIds();
				}
			).build());
	}

	public static class Builder {

		public Builder blueprintExternalReferenceCode(
			String blueprintExternalReferenceCode) {

			_blueprintExternalReferenceCode = blueprintExternalReferenceCode;

			return this;
		}

		public BulkActionBulkSelectionFactory build() {
			return new BulkActionBulkSelectionFactory(this);
		}

		public Builder bulkAction(BulkAction bulkAction) {
			_bulkAction = bulkAction;

			return this;
		}

		public Builder bulkSelectionFactoryRegistry(
			BulkSelectionFactoryRegistry bulkSelectionFactoryRegistry) {

			_bulkSelectionFactoryRegistry = bulkSelectionFactoryRegistry;

			return this;
		}

		public Builder contextAcceptLanguage(
			AcceptLanguage contextAcceptLanguage) {

			_contextAcceptLanguage = contextAcceptLanguage;

			return this;
		}

		public Builder contextCompany(Company contextCompany) {
			_contextCompany = contextCompany;

			return this;
		}

		public Builder contextHttpServletRequest(
			HttpServletRequest contextHttpServletRequest) {

			_contextHttpServletRequest = contextHttpServletRequest;

			return this;
		}

		public Builder contextUser(User contextUser) {
			_contextUser = contextUser;

			return this;
		}

		public Builder emptySearch(Boolean emptySearch) {
			_emptySearch = emptySearch;

			return this;
		}

		public Builder entryClassNames(String entryClassNames) {
			_entryClassNames = entryClassNames;

			return this;
		}

		public Builder filter(Filter filter) {
			_filter = filter;

			return this;
		}

		public Builder filterFactory(FilterFactory<Predicate> filterFactory) {
			_filterFactory = filterFactory;

			return this;
		}

		public Builder indexerRegistry(IndexerRegistry indexerRegistry) {
			_indexerRegistry = indexerRegistry;

			return this;
		}

		public Builder localization(Localization localization) {
			_localization = localization;

			return this;
		}

		public Builder objectDefinitionLocalService(
			ObjectDefinitionLocalService objectDefinitionLocalService) {

			_objectDefinitionLocalService = objectDefinitionLocalService;

			return this;
		}

		public Builder objectEntryLocalService(
			ObjectEntryLocalService objectEntryLocalService) {

			_objectEntryLocalService = objectEntryLocalService;

			return this;
		}

		public Builder pagination(Pagination pagination) {
			_pagination = pagination;

			return this;
		}

		public Builder scope(String scope) {
			_scope = scope;

			return this;
		}

		public Builder search(String search) {
			_search = search;

			return this;
		}

		public Builder searcher(Searcher searcher) {
			_searcher = searcher;

			return this;
		}

		public Builder searchRequestBuilderFactory(
			SearchRequestBuilderFactory searchRequestBuilderFactory) {

			_searchRequestBuilderFactory = searchRequestBuilderFactory;

			return this;
		}

		public Builder sorts(Sort[] sorts) {
			_sorts = sorts;

			return this;
		}

		private String _blueprintExternalReferenceCode;
		private BulkAction _bulkAction;
		private BulkSelectionFactoryRegistry _bulkSelectionFactoryRegistry;
		private AcceptLanguage _contextAcceptLanguage;
		private Company _contextCompany;
		private HttpServletRequest _contextHttpServletRequest;
		private User _contextUser;
		private Boolean _emptySearch;
		private String _entryClassNames;
		private Filter _filter;
		private FilterFactory _filterFactory;
		private IndexerRegistry _indexerRegistry;
		private Localization _localization;
		private ObjectDefinitionLocalService _objectDefinitionLocalService;
		private ObjectEntryLocalService _objectEntryLocalService;
		private Pagination _pagination;
		private String _scope;
		private String _search;
		private Searcher _searcher;
		private SearchRequestBuilderFactory _searchRequestBuilderFactory;
		private Sort[] _sorts;

	}

	private BulkActionBulkSelectionFactory(Builder builder) {
		_bulkSelectionFactoryRegistry = builder._bulkSelectionFactoryRegistry;
		_indexerRegistry = builder._indexerRegistry;
		_localization = builder._localization;
		_searcher = builder._searcher;
		_searchRequestBuilderFactory = builder._searchRequestBuilderFactory;
		_contextAcceptLanguage = builder._contextAcceptLanguage;
		_contextCompany = builder._contextCompany;
		_blueprintExternalReferenceCode =
			builder._blueprintExternalReferenceCode;
		_emptySearch = builder._emptySearch;
		_entryClassNames = builder._entryClassNames;
		_scope = builder._scope;
		_search = builder._search;
		_filter = builder._filter;
		_filterFactory = builder._filterFactory;
		_objectDefinitionLocalService = builder._objectDefinitionLocalService;
		_objectEntryLocalService = builder._objectEntryLocalService;
		_pagination = builder._pagination;
		_sorts = builder._sorts;
		_bulkAction = builder._bulkAction;
		_contextHttpServletRequest = builder._contextHttpServletRequest;
		_contextUser = builder._contextUser;
	}

	private BooleanClause<?> _getBooleanClause(
		UnsafeConsumer<BooleanQuery, Exception> booleanQueryUnsafeConsumer,
		Filter filter) {

		BooleanQuery booleanQuery = new BooleanQueryImpl() {
			{
				add(new MatchAllQuery(), BooleanClauseOccur.MUST);

				BooleanFilter booleanFilter = new BooleanFilter();

				if (filter != null) {
					booleanFilter.add(filter, BooleanClauseOccur.MUST);
				}

				setPreBooleanFilter(booleanFilter);
			}
		};

		try {
			booleanQueryUnsafeConsumer.accept(booleanQuery);

			return BooleanClauseFactoryUtil.create(
				booleanQuery, BooleanClauseOccur.MUST.getName());
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private String _getEntryClassName(Document document) {
		Map<String, com.liferay.portal.search.document.Field> fields =
			document.getFields();

		com.liferay.portal.search.document.Field entryClassNameField =
			fields.get(Field.ENTRY_CLASS_NAME);

		if (entryClassNameField != null) {
			return GetterUtil.getString(entryClassNameField.getValue());
		}

		return document.getString(Field.ENTRY_CLASS_NAME);
	}

	private Long _getEntryClassPK(Document document) {
		Map<String, com.liferay.portal.search.document.Field> fields =
			document.getFields();

		com.liferay.portal.search.document.Field entryClassNamePK = fields.get(
			Field.ENTRY_CLASS_PK);

		if (entryClassNamePK != null) {
			return GetterUtil.getLong(entryClassNamePK.getValue());
		}

		return document.getLong(Field.ENTRY_CLASS_PK);
	}

	private String[] _getSelectedItemsRowIds() throws PortalException {
		BulkActionItem[] bulkActionItems = _bulkAction.getBulkActionItems();

		List<String> rowIds = new ArrayList<>(bulkActionItems.length);

		if (BulkAction.Type.DEFAULT_PERMISSION_BULK_ACTION.equals(
				_bulkAction.getType())) {

			BulkActionItem bulkActionItem = bulkActionItems[0];

			String filterString = StringBundler.concat(
				"(className eq '", bulkActionItem.getClassName(), "') and (",
				StringUtil.merge(
					TransformUtil.transform(
						bulkActionItems,
						item ->
							"(classExternalReferenceCode eq '" +
								item.getClassExternalReferenceCode() + "')",
						String.class),
					" or "),
				")");

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.
					getObjectDefinitionByExternalReferenceCode(
						"L_CMS_DEFAULT_PERMISSION",
						_contextCompany.getCompanyId());

			Predicate predicate = _filterFactory.create(
				filterString, objectDefinition);

			List<Long> primaryKeys = _objectEntryLocalService.getPrimaryKeys(
				new Long[0], _contextCompany.getCompanyId(),
				_contextUser.getUserId(),
				objectDefinition.getObjectDefinitionId(), predicate, false,
				null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			for (long primaryKey : primaryKeys) {
				rowIds.add(
					objectDefinition.getClassName() + StringPool.SPACE +
						primaryKey);
			}
		}
		else {
			for (BulkActionItem bulkActionItem : bulkActionItems) {
				rowIds.add(
					StringBundler.concat(
						bulkActionItem.getClassName(), StringPool.SPACE,
						bulkActionItem.getClassPK()));
			}
		}

		return rowIds.toArray(new String[0]);
	}

	private boolean _isAllowedSearchContextAttribute(String key) {
		if (key.startsWith("search.experiences.") ||
			key.equals("search.empty.search") || key.equals("status")) {

			return true;
		}

		return false;
	}

	private void _populateSearchContext(
		Map<String, Object> attributes, Filter filter, String scope,
		String search, SearchContext searchContext, int start, int end,
		Sort[] sorts) {

		MapUtil.isNotEmptyForEach(
			attributes,
			(key, value) -> {
				if (_isAllowedSearchContextAttribute(key) && (value != null) &&
					(value instanceof Serializable)) {

					searchContext.setAttribute(key, (Serializable)value);
				}
			});

		if (searchContext.getAttribute("search.experiences.ip.address") ==
				null) {

			searchContext.setAttribute(
				"search.experiences.ip.address",
				_contextHttpServletRequest.getRemoteAddr());
		}

		if (filter != null) {
			searchContext.setBooleanClauses(
				new BooleanClause[] {
					_getBooleanClause(
						booleanQuery -> {
						},
						filter)
				});
		}

		searchContext.setEnd(end);
		searchContext.setGroupIds(
			_toGroupIds(_contextCompany.getCompanyId(), scope));
		searchContext.setKeywords(search);
		searchContext.setLocale(_contextAcceptLanguage.getPreferredLocale());
		searchContext.setStart(start);

		if (ArrayUtil.isNotEmpty(sorts)) {
			searchContext.setSorts(sorts);
		}

		searchContext.setTimeZone(_contextUser.getTimeZone());
		searchContext.setUserId(_contextUser.getUserId());
	}

	private String[] _searchRowIds() throws PortalException {
		if (BulkAction.Type.DEFAULT_PERMISSION_BULK_ACTION.equals(
				_bulkAction.getType())) {

			DefaultPermissionBulkAction defaultPermissionBulkAction =
				(DefaultPermissionBulkAction)_bulkAction;

			String filterString = StringBundler.concat(
				"(className eq '", ObjectEntryFolder.class.getName(),
				"') and ");

			if (Validator.isNull(defaultPermissionBulkAction.getTreePath())) {
				filterString = StringBundler.concat(
					filterString, "(depotGroupId eq ",
					defaultPermissionBulkAction.getDepotGroupId(), ")");
			}
			else {
				filterString = StringBundler.concat(
					filterString, "(startswith(treePath, '",
					defaultPermissionBulkAction.getTreePath(), "'))");
			}

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.
					getObjectDefinitionByExternalReferenceCode(
						"L_CMS_DEFAULT_PERMISSION",
						_contextCompany.getCompanyId());

			Predicate predicate = _filterFactory.create(
				filterString, objectDefinition);

			List<Long> primaryKeys = _objectEntryLocalService.getPrimaryKeys(
				new Long[0], _contextCompany.getCompanyId(),
				_contextUser.getUserId(),
				objectDefinition.getObjectDefinitionId(), predicate, false,
				null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			if (ListUtil.isEmpty(primaryKeys)) {
				return new String[0];
			}

			List<String> rowIds = new ArrayList<>(primaryKeys.size());

			for (long primaryKey : primaryKeys) {
				rowIds.add(
					objectDefinition.getClassName() + StringPool.SPACE +
						primaryKey);
			}

			return rowIds.toArray(new String[0]);
		}

		if (!FeatureFlagManagerUtil.isEnabled(
				_contextCompany.getCompanyId(), "LPS-179669")) {

			return new String[0];
		}

		SearchRequestBody searchRequestBody = new SearchRequestBody();

		searchRequestBody.setAttributes(
			() -> HashMapBuilder.<String, Object>put(
				"search.empty.search", _emptySearch
			).put(
				"search.experiences.blueprint.external.reference.code",
				_blueprintExternalReferenceCode
			).put(
				"status",
				() -> {
					int[] statuses = FilterUtil.getStatuses(_filter);

					if (ArrayUtil.isNotEmpty(statuses)) {
						return statuses;
					}

					return null;
				}
			).build());

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				_contextCompany.getCompanyId()
			).fetchSourceIncludes(
				new String[] {
					_localization.getLocalizedName(
						Field.CONTENT,
						_contextAcceptLanguage.getPreferredLanguageId()),
					Field.CREATE_DATE,
					_localization.getLocalizedName(
						Field.DESCRIPTION,
						_contextAcceptLanguage.getPreferredLanguageId()),
					Field.MODIFIED_DATE
				}
			).withSearchContext(
				searchContext -> _populateSearchContext(
					searchRequestBody.getAttributes(), _filter, _scope, _search,
					searchContext, -1, -1, _sorts)
			);

		String[] entryClassNamesArray = _toArray(_entryClassNames);

		if (ArrayUtil.isNotEmpty(entryClassNamesArray)) {
			searchRequestBuilder.entryClassNames(entryClassNamesArray);
			searchRequestBuilder.modelIndexerClassNames(entryClassNamesArray);
		}

		if (!Validator.isBlank(_search)) {
			searchRequestBuilder.queryString(_search);
		}

		SearchResponse searchResponse = _searcher.search(
			searchRequestBuilder.build());

		SearchHits searchHits = searchResponse.getSearchHits();

		List<SearchHit> searchHitsList = searchHits.getSearchHits();

		List<String> searchResults = new ArrayList<>(searchHitsList.size());

		for (SearchHit searchHit : searchHitsList) {
			Document document = searchHit.getDocument();

			searchResults.add(
				_getEntryClassName(document) + StringPool.SPACE +
					_getEntryClassPK(document));
		}

		return searchResults.toArray(new String[0]);
	}

	private String[] _toArray(String csv) {
		if (Validator.isBlank(csv)) {
			return new String[0];
		}

		csv = StringUtil.trim(csv);

		return csv.split("\\s*,\\s*");
	}

	private long[] _toGroupIds(long companyId, String scope) {
		List<Long> groupIds = new ArrayList<>();

		String[] parts = _toArray(scope);

		for (String part : parts) {
			Group group =
				GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
					part, companyId);

			if (group != null) {
				groupIds.add(group.getGroupId());

				continue;
			}

			try {
				groupIds.add(Long.parseLong(part));
			}
			catch (NumberFormatException numberFormatException) {
				throw new SystemException(
					"Invalid external reference code or group ID: " + part,
					numberFormatException);
			}
		}

		return ArrayUtil.toLongArray(groupIds);
	}

	private void _validate() {
		SelectionScope selectionScope = _bulkAction.getSelectionScope();

		if (BulkAction.Type.DEFAULT_PERMISSION_BULK_ACTION.equals(
				_bulkAction.getType())) {

			if (GetterUtil.getBoolean(selectionScope.getSelectAll()) &&
				ArrayUtil.isEmpty(_bulkAction.getBulkActionItems())) {

				DefaultPermissionBulkAction defaultPermissionBulkAction =
					(DefaultPermissionBulkAction)_bulkAction;

				long depotGroupId = GetterUtil.getLong(
					defaultPermissionBulkAction.getDepotGroupId());

				if ((depotGroupId == 0) &&
					Validator.isNull(
						GetterUtil.getString(
							defaultPermissionBulkAction.getTreePath()))) {

					throw new ValidationException();
				}
			}
		}
		else {
			if (GetterUtil.getBoolean(selectionScope.getSelectAll()) &&
				ArrayUtil.isEmpty(_bulkAction.getBulkActionItems()) &&
				(_filter == null)) {

				throw new ValidationException("Filter is null");
			}
		}
	}

	private final String _blueprintExternalReferenceCode;
	private final BulkAction _bulkAction;
	private final BulkSelectionFactoryRegistry _bulkSelectionFactoryRegistry;
	private final AcceptLanguage _contextAcceptLanguage;
	private final Company _contextCompany;
	private final HttpServletRequest _contextHttpServletRequest;
	private final User _contextUser;
	private final Boolean _emptySearch;
	private final String _entryClassNames;
	private final Filter _filter;
	private final FilterFactory<Predicate> _filterFactory;
	private final IndexerRegistry _indexerRegistry;
	private final Localization _localization;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final Pagination _pagination;
	private final String _scope;
	private final String _search;
	private final Searcher _searcher;
	private final SearchRequestBuilderFactory _searchRequestBuilderFactory;
	private final Sort[] _sorts;

}