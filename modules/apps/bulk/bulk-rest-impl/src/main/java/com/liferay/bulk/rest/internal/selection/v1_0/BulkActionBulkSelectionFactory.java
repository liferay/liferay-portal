/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.internal.selection.v1_0;

import com.liferay.bulk.rest.dto.v1_0.BulkAction;
import com.liferay.bulk.rest.dto.v1_0.BulkActionItem;
import com.liferay.bulk.rest.dto.v1_0.DefaultPermissionObjectBulkSelectionAction;
import com.liferay.bulk.rest.dto.v1_0.DeleteObjectAssetVersionBulkSelectionAction;
import com.liferay.bulk.rest.dto.v1_0.SelectionScope;
import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.bulk.selection.BulkSelectionFactoryRegistry;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryVersionLocalService;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.MatchAllQuery;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.rest.dto.v1_0.SearchRequestBody;
import com.liferay.portal.search.rest.util.FilterUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.validation.ValidationException;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

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

					boolean selectAll = false;

					SelectionScope selectionScope =
						_bulkAction.getSelectionScope();

					if (selectionScope != null) {
						selectAll = GetterUtil.getBoolean(
							selectionScope.getSelectAll());
					}

					if (selectAll) {
						return _getRowIds();
					}

					return _getSelectedItemsRowIds();
				}
			).build());
	}

	public static class Builder {

		public Builder acceptLanguage(AcceptLanguage acceptLanguage) {
			_acceptLanguage = acceptLanguage;

			return this;
		}

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

		public Builder company(Company company) {
			_company = company;

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

		public Builder groupLocalService(GroupLocalService groupLocalService) {
			_groupLocalService = groupLocalService;

			return this;
		}

		public Builder httpServletRequest(
			HttpServletRequest httpServletRequest) {

			_httpServletRequest = httpServletRequest;

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

		public Builder objectEntryVersionLocalService(
			ObjectEntryVersionLocalService objectEntryVersionLocalService) {

			_objectEntryVersionLocalService = objectEntryVersionLocalService;

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

		public Builder searchRequestBuilderFactory(
			SearchRequestBuilderFactory searchRequestBuilderFactory) {

			_searchRequestBuilderFactory = searchRequestBuilderFactory;

			return this;
		}

		public Builder searcher(Searcher searcher) {
			_searcher = searcher;

			return this;
		}

		public Builder sorts(Sort[] sorts) {
			_sorts = sorts;

			return this;
		}

		public Builder user(User user) {
			_user = user;

			return this;
		}

		private AcceptLanguage _acceptLanguage;
		private String _blueprintExternalReferenceCode;
		private BulkAction _bulkAction;
		private BulkSelectionFactoryRegistry _bulkSelectionFactoryRegistry;
		private Company _company;
		private Boolean _emptySearch;
		private String _entryClassNames;
		private Filter _filter;
		private FilterFactory _filterFactory;
		private GroupLocalService _groupLocalService;
		private HttpServletRequest _httpServletRequest;
		private Localization _localization;
		private ObjectDefinitionLocalService _objectDefinitionLocalService;
		private ObjectEntryLocalService _objectEntryLocalService;
		private ObjectEntryVersionLocalService _objectEntryVersionLocalService;
		private String _scope;
		private String _search;
		private SearchRequestBuilderFactory _searchRequestBuilderFactory;
		private Searcher _searcher;
		private Sort[] _sorts;
		private User _user;

	}

	private BulkActionBulkSelectionFactory(Builder builder) {
		_acceptLanguage = builder._acceptLanguage;
		_blueprintExternalReferenceCode =
			builder._blueprintExternalReferenceCode;
		_bulkAction = builder._bulkAction;
		_bulkSelectionFactoryRegistry = builder._bulkSelectionFactoryRegistry;
		_company = builder._company;
		_emptySearch = builder._emptySearch;
		_entryClassNames = builder._entryClassNames;
		_filter = builder._filter;
		_filterFactory = builder._filterFactory;
		_groupLocalService = builder._groupLocalService;
		_httpServletRequest = builder._httpServletRequest;
		_localization = builder._localization;
		_objectDefinitionLocalService = builder._objectDefinitionLocalService;
		_objectEntryLocalService = builder._objectEntryLocalService;
		_objectEntryVersionLocalService =
			builder._objectEntryVersionLocalService;
		_scope = builder._scope;
		_search = builder._search;
		_searchRequestBuilderFactory = builder._searchRequestBuilderFactory;
		_searcher = builder._searcher;
		_sorts = builder._sorts;
		_user = builder._user;
	}

	private BooleanClause<?> _getBooleanClause(
		UnsafeConsumer<BooleanQuery, Exception> booleanQueryUnsafeConsumer,
		Filter filter) {

		BooleanQuery booleanQuery = new BooleanQuery() {
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

			return new BooleanClause<>(booleanQuery, BooleanClauseOccur.MUST);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private String _getEntryClassName(Document document) {
		Map<String, com.liferay.portal.search.document.Field> fields =
			document.getFields();

		com.liferay.portal.search.document.Field field = fields.get(
			Field.ENTRY_CLASS_NAME);

		if (field != null) {
			return GetterUtil.getString(field.getValue());
		}

		return document.getString(Field.ENTRY_CLASS_NAME);
	}

	private Long _getEntryClassPK(Document document) {
		Map<String, com.liferay.portal.search.document.Field> fields =
			document.getFields();

		com.liferay.portal.search.document.Field field = fields.get(
			Field.ENTRY_CLASS_PK);

		if (field != null) {
			return GetterUtil.getLong(field.getValue());
		}

		return document.getLong(Field.ENTRY_CLASS_PK);
	}

	private String[] _getRowIds() throws PortalException {
		if (BulkAction.Type.DEFAULT_PERMISSION_OBJECT_BULK_SELECTION_ACTION.
				equals(_bulkAction.getType())) {

			DefaultPermissionObjectBulkSelectionAction
				defaultPermissionObjectBulkSelectionAction =
					(DefaultPermissionObjectBulkSelectionAction)_bulkAction;

			String filterString = StringBundler.concat(
				"(className eq '", ObjectEntryFolder.class.getName(),
				"') and ");

			if (Validator.isNull(
					defaultPermissionObjectBulkSelectionAction.getTreePath())) {

				filterString = StringBundler.concat(
					filterString, "(depotGroupId eq ",
					defaultPermissionObjectBulkSelectionAction.
						getDepotGroupId(),
					")");
			}
			else {
				filterString = StringBundler.concat(
					filterString, "(startswith(treePath, '",
					defaultPermissionObjectBulkSelectionAction.getTreePath(),
					"'))");
			}

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.
					getObjectDefinitionByExternalReferenceCode(
						"L_CMS_DEFAULT_PERMISSION", _company.getCompanyId());

			Predicate predicate = _filterFactory.create(
				filterString, objectDefinition);

			return TransformUtil.transformToArray(
				_objectEntryLocalService.getPrimaryKeys(
					new Long[0], _company.getCompanyId(), _user.getUserId(),
					objectDefinition.getObjectDefinitionId(), predicate, false,
					null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null),
				primaryKey ->
					objectDefinition.getClassName() + StringPool.SPACE +
						primaryKey,
				String.class);
		}
		else if (BulkAction.Type.
					DELETE_OBJECT_ASSET_VERSION_BULK_SELECTION_ACTION.equals(
						_bulkAction.getType())) {

			DeleteObjectAssetVersionBulkSelectionAction
				deleteAssetVersionBulkAction =
					(DeleteObjectAssetVersionBulkSelectionAction)_bulkAction;

			if (!Objects.equals(
					deleteAssetVersionBulkAction.getClassName(),
					ObjectEntry.class.getName())) {

				throw new UnsupportedOperationException();
			}

			ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
				deleteAssetVersionBulkAction.getClassPK());

			ObjectDefinition objectDefinition =
				objectEntry.getObjectDefinition();

			return new String[] {
				objectDefinition.getClassName() + StringPool.SPACE +
					objectEntry.getObjectEntryId()
			};
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
				_company.getCompanyId()
			).fetchSourceIncludes(
				new String[] {
					_localization.getLocalizedName(
						Field.CONTENT,
						_acceptLanguage.getPreferredLanguageId()),
					Field.CREATE_DATE,
					_localization.getLocalizedName(
						Field.DESCRIPTION,
						_acceptLanguage.getPreferredLanguageId()),
					Field.MODIFIED_DATE
				}
			).withSearchContext(
				searchContext -> _populateSearchContext(
					searchRequestBody.getAttributes(), _filter, _scope, _search,
					searchContext, _sorts)
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

		return TransformUtil.transformToArray(
			searchHits.getSearchHits(),
			searchHit -> {
				Document document = searchHit.getDocument();

				return _getEntryClassName(document) + StringPool.SPACE +
					_getEntryClassPK(document);
			},
			String.class);
	}

	private String[] _getSelectedItemsRowIds() throws PortalException {
		BulkActionItem[] bulkActionItems = _bulkAction.getBulkActionItems();

		if (ArrayUtil.isEmpty(bulkActionItems)) {
			return new String[0];
		}

		if (!BulkAction.Type.DEFAULT_PERMISSION_OBJECT_BULK_SELECTION_ACTION.
				equals(_bulkAction.getType())) {

			return TransformUtil.transform(
				bulkActionItems,
				bulkActionItem -> StringBundler.concat(
					bulkActionItem.getClassName(), StringPool.SPACE,
					bulkActionItem.getClassPK()),
				String.class);
		}

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
					"L_CMS_DEFAULT_PERMISSION", _company.getCompanyId());

		Predicate predicate = _filterFactory.create(
			filterString, objectDefinition);

		return TransformUtil.transformToArray(
			_objectEntryLocalService.getPrimaryKeys(
				new Long[0], _company.getCompanyId(), _user.getUserId(),
				objectDefinition.getObjectDefinitionId(), predicate, false,
				null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null),
			primaryKey ->
				objectDefinition.getClassName() + StringPool.SPACE + primaryKey,
			String.class);
	}

	private boolean _isAllowedSearchContextAttribute(String key) {
		if (key.equals("search.empty.search") || key.equals("status") ||
			key.startsWith("search.experiences.")) {

			return true;
		}

		return false;
	}

	private void _populateSearchContext(
		Map<String, Object> attributes, Filter filter, String scope,
		String search, SearchContext searchContext, Sort[] sorts) {

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
				_httpServletRequest.getRemoteAddr());
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

		searchContext.setEnd(QueryUtil.ALL_POS);
		searchContext.setGroupIds(_toGroupIds(_company.getCompanyId(), scope));
		searchContext.setKeywords(search);
		searchContext.setLocale(_acceptLanguage.getPreferredLocale());

		if (ArrayUtil.isNotEmpty(sorts)) {
			searchContext.setSorts(sorts);
		}

		searchContext.setStart(QueryUtil.ALL_POS);
		searchContext.setTimeZone(_user.getTimeZone());
		searchContext.setUserId(_user.getUserId());
	}

	private String[] _toArray(String csv) {
		if (Validator.isBlank(csv)) {
			return new String[0];
		}

		csv = StringUtil.trim(csv);

		return csv.split("\\s*,\\s*");
	}

	private long[] _toGroupIds(long companyId, String scope) {
		return TransformUtil.transformToLongArray(
			_toArray(scope),
			part -> {
				Group group =
					_groupLocalService.fetchGroupByExternalReferenceCode(
						part, companyId);

				if (group != null) {
					return group.getGroupId();
				}

				try {
					return Long.parseLong(part);
				}
				catch (NumberFormatException numberFormatException) {
					throw new SystemException(
						"Invalid external reference code or group ID: " + part,
						numberFormatException);
				}
			});
	}

	private void _validate() {
		boolean selectAll = false;
		SelectionScope selectionScope = _bulkAction.getSelectionScope();

		if (selectionScope != null) {
			selectAll = GetterUtil.getBoolean(selectionScope.getSelectAll());
		}

		if (BulkAction.Type.DEFAULT_PERMISSION_OBJECT_BULK_SELECTION_ACTION.
				equals(_bulkAction.getType())) {

			if (selectAll &&
				ArrayUtil.isEmpty(_bulkAction.getBulkActionItems())) {

				DefaultPermissionObjectBulkSelectionAction
					defaultPermissionObjectBulkSelectionAction =
						(DefaultPermissionObjectBulkSelectionAction)_bulkAction;

				long depotGroupId = GetterUtil.getLong(
					defaultPermissionObjectBulkSelectionAction.
						getDepotGroupId());

				if ((depotGroupId == 0) &&
					Validator.isNull(
						GetterUtil.getString(
							defaultPermissionObjectBulkSelectionAction.
								getTreePath()))) {

					throw new ValidationException();
				}
			}
		}
		else {
			if (selectAll &&
				ArrayUtil.isEmpty(_bulkAction.getBulkActionItems()) &&
				(_filter == null)) {

				throw new ValidationException("Filter is null");
			}
		}
	}

	private final AcceptLanguage _acceptLanguage;
	private final String _blueprintExternalReferenceCode;
	private final BulkAction _bulkAction;
	private final BulkSelectionFactoryRegistry _bulkSelectionFactoryRegistry;
	private final Company _company;
	private final Boolean _emptySearch;
	private final String _entryClassNames;
	private final Filter _filter;
	private final FilterFactory<Predicate> _filterFactory;
	private final GroupLocalService _groupLocalService;
	private final HttpServletRequest _httpServletRequest;
	private final Localization _localization;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectEntryVersionLocalService
		_objectEntryVersionLocalService;
	private final String _scope;
	private final String _search;
	private final SearchRequestBuilderFactory _searchRequestBuilderFactory;
	private final Searcher _searcher;
	private final Sort[] _sorts;
	private final User _user;

}