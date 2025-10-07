/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.internal.graphql.query.v1_0;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLTypeExtension;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.search.experiences.rest.dto.v1_0.ElementInstance;
import com.liferay.search.experiences.rest.dto.v1_0.FieldMappingInfo;
import com.liferay.search.experiences.rest.dto.v1_0.KeywordQueryContributor;
import com.liferay.search.experiences.rest.dto.v1_0.ModelPrefilterContributor;
import com.liferay.search.experiences.rest.dto.v1_0.QueryPrefilterContributor;
import com.liferay.search.experiences.rest.dto.v1_0.SXPBlueprint;
import com.liferay.search.experiences.rest.dto.v1_0.SXPElement;
import com.liferay.search.experiences.rest.dto.v1_0.SXPParameterContributorDefinition;
import com.liferay.search.experiences.rest.dto.v1_0.SearchIndex;
import com.liferay.search.experiences.rest.dto.v1_0.SearchableAssetName;
import com.liferay.search.experiences.rest.dto.v1_0.SearchableAssetNameDisplay;
import com.liferay.search.experiences.rest.resource.v1_0.FieldMappingInfoResource;
import com.liferay.search.experiences.rest.resource.v1_0.KeywordQueryContributorResource;
import com.liferay.search.experiences.rest.resource.v1_0.ModelPrefilterContributorResource;
import com.liferay.search.experiences.rest.resource.v1_0.QueryPrefilterContributorResource;
import com.liferay.search.experiences.rest.resource.v1_0.SXPBlueprintResource;
import com.liferay.search.experiences.rest.resource.v1_0.SXPElementResource;
import com.liferay.search.experiences.rest.resource.v1_0.SXPParameterContributorDefinitionResource;
import com.liferay.search.experiences.rest.resource.v1_0.SearchIndexResource;
import com.liferay.search.experiences.rest.resource.v1_0.SearchableAssetNameDisplayResource;
import com.liferay.search.experiences.rest.resource.v1_0.SearchableAssetNameResource;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.Map;
import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class Query {

	public static void setFieldMappingInfoResourceComponentServiceObjects(
		ComponentServiceObjects<FieldMappingInfoResource>
			fieldMappingInfoResourceComponentServiceObjects) {

		_fieldMappingInfoResourceComponentServiceObjects =
			fieldMappingInfoResourceComponentServiceObjects;
	}

	public static void
		setKeywordQueryContributorResourceComponentServiceObjects(
			ComponentServiceObjects<KeywordQueryContributorResource>
				keywordQueryContributorResourceComponentServiceObjects) {

		_keywordQueryContributorResourceComponentServiceObjects =
			keywordQueryContributorResourceComponentServiceObjects;
	}

	public static void
		setModelPrefilterContributorResourceComponentServiceObjects(
			ComponentServiceObjects<ModelPrefilterContributorResource>
				modelPrefilterContributorResourceComponentServiceObjects) {

		_modelPrefilterContributorResourceComponentServiceObjects =
			modelPrefilterContributorResourceComponentServiceObjects;
	}

	public static void
		setQueryPrefilterContributorResourceComponentServiceObjects(
			ComponentServiceObjects<QueryPrefilterContributorResource>
				queryPrefilterContributorResourceComponentServiceObjects) {

		_queryPrefilterContributorResourceComponentServiceObjects =
			queryPrefilterContributorResourceComponentServiceObjects;
	}

	public static void setSXPBlueprintResourceComponentServiceObjects(
		ComponentServiceObjects<SXPBlueprintResource>
			sxpBlueprintResourceComponentServiceObjects) {

		_sxpBlueprintResourceComponentServiceObjects =
			sxpBlueprintResourceComponentServiceObjects;
	}

	public static void setSXPElementResourceComponentServiceObjects(
		ComponentServiceObjects<SXPElementResource>
			sxpElementResourceComponentServiceObjects) {

		_sxpElementResourceComponentServiceObjects =
			sxpElementResourceComponentServiceObjects;
	}

	public static void
		setSXPParameterContributorDefinitionResourceComponentServiceObjects(
			ComponentServiceObjects<SXPParameterContributorDefinitionResource>
				sxpParameterContributorDefinitionResourceComponentServiceObjects) {

		_sxpParameterContributorDefinitionResourceComponentServiceObjects =
			sxpParameterContributorDefinitionResourceComponentServiceObjects;
	}

	public static void setSearchIndexResourceComponentServiceObjects(
		ComponentServiceObjects<SearchIndexResource>
			searchIndexResourceComponentServiceObjects) {

		_searchIndexResourceComponentServiceObjects =
			searchIndexResourceComponentServiceObjects;
	}

	public static void setSearchableAssetNameResourceComponentServiceObjects(
		ComponentServiceObjects<SearchableAssetNameResource>
			searchableAssetNameResourceComponentServiceObjects) {

		_searchableAssetNameResourceComponentServiceObjects =
			searchableAssetNameResourceComponentServiceObjects;
	}

	public static void
		setSearchableAssetNameDisplayResourceComponentServiceObjects(
			ComponentServiceObjects<SearchableAssetNameDisplayResource>
				searchableAssetNameDisplayResourceComponentServiceObjects) {

		_searchableAssetNameDisplayResourceComponentServiceObjects =
			searchableAssetNameDisplayResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {fieldMappingInfos(external: ___, indexName: ___, query: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public FieldMappingInfoPage fieldMappingInfos(
			@GraphQLName("external") Boolean external,
			@GraphQLName("indexName") String indexName,
			@GraphQLName("query") String query)
		throws Exception {

		return _applyComponentServiceObjects(
			_fieldMappingInfoResourceComponentServiceObjects,
			this::_populateResourceContext,
			fieldMappingInfoResource -> new FieldMappingInfoPage(
				fieldMappingInfoResource.getFieldMappingInfosPage(
					external, indexName, query)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {keywordQueryContributors{items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public KeywordQueryContributorPage keywordQueryContributors()
		throws Exception {

		return _applyComponentServiceObjects(
			_keywordQueryContributorResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordQueryContributorResource -> new KeywordQueryContributorPage(
				keywordQueryContributorResource.
					getKeywordQueryContributorsPage()));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {modelPrefilterContributors{items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ModelPrefilterContributorPage modelPrefilterContributors()
		throws Exception {

		return _applyComponentServiceObjects(
			_modelPrefilterContributorResourceComponentServiceObjects,
			this::_populateResourceContext,
			modelPrefilterContributorResource ->
				new ModelPrefilterContributorPage(
					modelPrefilterContributorResource.
						getModelPrefilterContributorsPage()));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {queryPrefilterContributors{items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public QueryPrefilterContributorPage queryPrefilterContributors()
		throws Exception {

		return _applyComponentServiceObjects(
			_queryPrefilterContributorResourceComponentServiceObjects,
			this::_populateResourceContext,
			queryPrefilterContributorResource ->
				new QueryPrefilterContributorPage(
					queryPrefilterContributorResource.
						getQueryPrefilterContributorsPage()));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {sXPBlueprint(sxpBlueprintId: ___){actions, collectionProviderSubtypeName, collectionProviderTypeName, configuration, createDate, description, description_i18n, elementInstances, externalReferenceCode, id, modifiedDate, schemaVersion, title, title_i18n, userName, version}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SXPBlueprint sXPBlueprint(
			@GraphQLName("sxpBlueprintId") Long sxpBlueprintId)
		throws Exception {

		return _applyComponentServiceObjects(
			_sxpBlueprintResourceComponentServiceObjects,
			this::_populateResourceContext,
			sxpBlueprintResource -> sxpBlueprintResource.getSXPBlueprint(
				sxpBlueprintId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {sXPBlueprintByExternalReferenceCode(externalReferenceCode: ___){actions, collectionProviderSubtypeName, collectionProviderTypeName, configuration, createDate, description, description_i18n, elementInstances, externalReferenceCode, id, modifiedDate, schemaVersion, title, title_i18n, userName, version}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SXPBlueprint sXPBlueprintByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_sxpBlueprintResourceComponentServiceObjects,
			this::_populateResourceContext,
			sxpBlueprintResource ->
				sxpBlueprintResource.getSXPBlueprintByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {sXPBlueprintExport(sxpBlueprintId: ___){}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Response sXPBlueprintExport(
			@GraphQLName("sxpBlueprintId") Long sxpBlueprintId)
		throws Exception {

		return _applyComponentServiceObjects(
			_sxpBlueprintResourceComponentServiceObjects,
			this::_populateResourceContext,
			sxpBlueprintResource -> sxpBlueprintResource.getSXPBlueprintExport(
				sxpBlueprintId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {sXPBlueprints(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SXPBlueprintPage sXPBlueprints(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_sxpBlueprintResourceComponentServiceObjects,
			this::_populateResourceContext,
			sxpBlueprintResource -> new SXPBlueprintPage(
				sxpBlueprintResource.getSXPBlueprintsPage(
					search,
					_filterBiFunction.apply(sxpBlueprintResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						sxpBlueprintResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {sXPElement(sxpElementId: ___){actions, createDate, description, description_i18n, elementDefinition, externalReferenceCode, fallbackDescription, fallbackTitle, hidden, id, modifiedDate, readOnly, schemaVersion, title, title_i18n, type, userName, version}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SXPElement sXPElement(@GraphQLName("sxpElementId") Long sxpElementId)
		throws Exception {

		return _applyComponentServiceObjects(
			_sxpElementResourceComponentServiceObjects,
			this::_populateResourceContext,
			sxpElementResource -> sxpElementResource.getSXPElement(
				sxpElementId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {sXPElementByExternalReferenceCode(externalReferenceCode: ___){actions, createDate, description, description_i18n, elementDefinition, externalReferenceCode, fallbackDescription, fallbackTitle, hidden, id, modifiedDate, readOnly, schemaVersion, title, title_i18n, type, userName, version}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SXPElement sXPElementByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_sxpElementResourceComponentServiceObjects,
			this::_populateResourceContext,
			sxpElementResource ->
				sxpElementResource.getSXPElementByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {sXPElementExport(sxpElementId: ___){}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Response sXPElementExport(
			@GraphQLName("sxpElementId") Long sxpElementId)
		throws Exception {

		return _applyComponentServiceObjects(
			_sxpElementResourceComponentServiceObjects,
			this::_populateResourceContext,
			sxpElementResource -> sxpElementResource.getSXPElementExport(
				sxpElementId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {sXPElements(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SXPElementPage sXPElements(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_sxpElementResourceComponentServiceObjects,
			this::_populateResourceContext,
			sxpElementResource -> new SXPElementPage(
				sxpElementResource.getSXPElementsPage(
					search,
					_filterBiFunction.apply(sxpElementResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(sxpElementResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {sXPParameterContributorDefinitions{items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SXPParameterContributorDefinitionPage
			sXPParameterContributorDefinitions()
		throws Exception {

		return _applyComponentServiceObjects(
			_sxpParameterContributorDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			sxpParameterContributorDefinitionResource ->
				new SXPParameterContributorDefinitionPage(
					sxpParameterContributorDefinitionResource.
						getSXPParameterContributorDefinitionsPage()));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {searchIndexes{items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SearchIndexPage searchIndexes() throws Exception {
		return _applyComponentServiceObjects(
			_searchIndexResourceComponentServiceObjects,
			this::_populateResourceContext,
			searchIndexResource -> new SearchIndexPage(
				searchIndexResource.getSearchIndexesPage()));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {searchableAssetNames{items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SearchableAssetNamePage searchableAssetNames() throws Exception {
		return _applyComponentServiceObjects(
			_searchableAssetNameResourceComponentServiceObjects,
			this::_populateResourceContext,
			searchableAssetNameResource -> new SearchableAssetNamePage(
				searchableAssetNameResource.getSearchableAssetNamesPage()));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {searchableAssetNameLanguage(languageId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SearchableAssetNameDisplayPage searchableAssetNameLanguage(
			@GraphQLName("languageId") String languageId)
		throws Exception {

		return _applyComponentServiceObjects(
			_searchableAssetNameDisplayResourceComponentServiceObjects,
			this::_populateResourceContext,
			searchableAssetNameDisplayResource ->
				new SearchableAssetNameDisplayPage(
					searchableAssetNameDisplayResource.
						getSearchableAssetNameLanguagePage(languageId)));
	}

	@GraphQLTypeExtension(SXPBlueprint.class)
	public class GetSXPElementByExternalReferenceCodeTypeExtension {

		public GetSXPElementByExternalReferenceCodeTypeExtension(
			SXPBlueprint sXPBlueprint) {

			_sXPBlueprint = sXPBlueprint;
		}

		@GraphQLField
		public SXPElement sXPElementByExternalReferenceCode() throws Exception {
			return _applyComponentServiceObjects(
				_sxpElementResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				sxpElementResource ->
					sxpElementResource.getSXPElementByExternalReferenceCode(
						_sXPBlueprint.getExternalReferenceCode()));
		}

		private SXPBlueprint _sXPBlueprint;

	}

	@GraphQLTypeExtension(SXPElement.class)
	public class GetSXPElementExportTypeExtension {

		public GetSXPElementExportTypeExtension(SXPElement sXPElement) {
			_sXPElement = sXPElement;
		}

		@GraphQLField
		public Response export() throws Exception {
			return _applyComponentServiceObjects(
				_sxpElementResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				sxpElementResource -> sxpElementResource.getSXPElementExport(
					_sXPElement.getId()));
		}

		private SXPElement _sXPElement;

	}

	@GraphQLTypeExtension(ElementInstance.class)
	public class GetSXPElementTypeExtension {

		public GetSXPElementTypeExtension(ElementInstance elementInstance) {
			_elementInstance = elementInstance;
		}

		@GraphQLField
		public SXPElement sXPElement() throws Exception {
			return _applyComponentServiceObjects(
				_sxpElementResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				sxpElementResource -> sxpElementResource.getSXPElement(
					_elementInstance.getSxpElementId()));
		}

		private ElementInstance _elementInstance;

	}

	@GraphQLTypeExtension(SXPElement.class)
	public class GetSXPBlueprintByExternalReferenceCodeTypeExtension {

		public GetSXPBlueprintByExternalReferenceCodeTypeExtension(
			SXPElement sXPElement) {

			_sXPElement = sXPElement;
		}

		@GraphQLField
		public SXPBlueprint sXPBlueprintByExternalReferenceCode()
			throws Exception {

			return _applyComponentServiceObjects(
				_sxpBlueprintResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				sxpBlueprintResource ->
					sxpBlueprintResource.getSXPBlueprintByExternalReferenceCode(
						_sXPElement.getExternalReferenceCode()));
		}

		private SXPElement _sXPElement;

	}

	@GraphQLTypeExtension(SXPBlueprint.class)
	public class GetSXPBlueprintExportTypeExtension {

		public GetSXPBlueprintExportTypeExtension(SXPBlueprint sXPBlueprint) {
			_sXPBlueprint = sXPBlueprint;
		}

		@GraphQLField
		public Response export() throws Exception {
			return _applyComponentServiceObjects(
				_sxpBlueprintResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				sxpBlueprintResource ->
					sxpBlueprintResource.getSXPBlueprintExport(
						_sXPBlueprint.getId()));
		}

		private SXPBlueprint _sXPBlueprint;

	}

	@GraphQLName("FieldMappingInfoPage")
	public class FieldMappingInfoPage {

		public FieldMappingInfoPage(Page fieldMappingInfoPage) {
			actions = fieldMappingInfoPage.getActions();

			items = fieldMappingInfoPage.getItems();
			lastPage = fieldMappingInfoPage.getLastPage();
			page = fieldMappingInfoPage.getPage();
			pageSize = fieldMappingInfoPage.getPageSize();
			totalCount = fieldMappingInfoPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<FieldMappingInfo> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("KeywordQueryContributorPage")
	public class KeywordQueryContributorPage {

		public KeywordQueryContributorPage(Page keywordQueryContributorPage) {
			actions = keywordQueryContributorPage.getActions();

			items = keywordQueryContributorPage.getItems();
			lastPage = keywordQueryContributorPage.getLastPage();
			page = keywordQueryContributorPage.getPage();
			pageSize = keywordQueryContributorPage.getPageSize();
			totalCount = keywordQueryContributorPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<KeywordQueryContributor> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ModelPrefilterContributorPage")
	public class ModelPrefilterContributorPage {

		public ModelPrefilterContributorPage(
			Page modelPrefilterContributorPage) {

			actions = modelPrefilterContributorPage.getActions();

			items = modelPrefilterContributorPage.getItems();
			lastPage = modelPrefilterContributorPage.getLastPage();
			page = modelPrefilterContributorPage.getPage();
			pageSize = modelPrefilterContributorPage.getPageSize();
			totalCount = modelPrefilterContributorPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ModelPrefilterContributor> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("QueryPrefilterContributorPage")
	public class QueryPrefilterContributorPage {

		public QueryPrefilterContributorPage(
			Page queryPrefilterContributorPage) {

			actions = queryPrefilterContributorPage.getActions();

			items = queryPrefilterContributorPage.getItems();
			lastPage = queryPrefilterContributorPage.getLastPage();
			page = queryPrefilterContributorPage.getPage();
			pageSize = queryPrefilterContributorPage.getPageSize();
			totalCount = queryPrefilterContributorPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<QueryPrefilterContributor> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("SXPBlueprintPage")
	public class SXPBlueprintPage {

		public SXPBlueprintPage(Page sxpBlueprintPage) {
			actions = sxpBlueprintPage.getActions();

			items = sxpBlueprintPage.getItems();
			lastPage = sxpBlueprintPage.getLastPage();
			page = sxpBlueprintPage.getPage();
			pageSize = sxpBlueprintPage.getPageSize();
			totalCount = sxpBlueprintPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<SXPBlueprint> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("SXPElementPage")
	public class SXPElementPage {

		public SXPElementPage(Page sxpElementPage) {
			actions = sxpElementPage.getActions();

			items = sxpElementPage.getItems();
			lastPage = sxpElementPage.getLastPage();
			page = sxpElementPage.getPage();
			pageSize = sxpElementPage.getPageSize();
			totalCount = sxpElementPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<SXPElement> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("SXPParameterContributorDefinitionPage")
	public class SXPParameterContributorDefinitionPage {

		public SXPParameterContributorDefinitionPage(
			Page sxpParameterContributorDefinitionPage) {

			actions = sxpParameterContributorDefinitionPage.getActions();

			items = sxpParameterContributorDefinitionPage.getItems();
			lastPage = sxpParameterContributorDefinitionPage.getLastPage();
			page = sxpParameterContributorDefinitionPage.getPage();
			pageSize = sxpParameterContributorDefinitionPage.getPageSize();
			totalCount = sxpParameterContributorDefinitionPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<SXPParameterContributorDefinition> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("SearchIndexPage")
	public class SearchIndexPage {

		public SearchIndexPage(Page searchIndexPage) {
			actions = searchIndexPage.getActions();

			items = searchIndexPage.getItems();
			lastPage = searchIndexPage.getLastPage();
			page = searchIndexPage.getPage();
			pageSize = searchIndexPage.getPageSize();
			totalCount = searchIndexPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<SearchIndex> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("SearchableAssetNamePage")
	public class SearchableAssetNamePage {

		public SearchableAssetNamePage(Page searchableAssetNamePage) {
			actions = searchableAssetNamePage.getActions();

			items = searchableAssetNamePage.getItems();
			lastPage = searchableAssetNamePage.getLastPage();
			page = searchableAssetNamePage.getPage();
			pageSize = searchableAssetNamePage.getPageSize();
			totalCount = searchableAssetNamePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<SearchableAssetName> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("SearchableAssetNameDisplayPage")
	public class SearchableAssetNameDisplayPage {

		public SearchableAssetNameDisplayPage(
			Page searchableAssetNameDisplayPage) {

			actions = searchableAssetNameDisplayPage.getActions();

			items = searchableAssetNameDisplayPage.getItems();
			lastPage = searchableAssetNameDisplayPage.getLastPage();
			page = searchableAssetNameDisplayPage.getPage();
			pageSize = searchableAssetNameDisplayPage.getPageSize();
			totalCount = searchableAssetNameDisplayPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<SearchableAssetNameDisplay> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	private <T, R, E1 extends Throwable, E2 extends Throwable> R
			_applyComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeFunction<T, R, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			return unsafeFunction.apply(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private void _populateResourceContext(
			FieldMappingInfoResource fieldMappingInfoResource)
		throws Exception {

		fieldMappingInfoResource.setContextAcceptLanguage(_acceptLanguage);
		fieldMappingInfoResource.setContextCompany(_company);
		fieldMappingInfoResource.setContextHttpServletRequest(
			_httpServletRequest);
		fieldMappingInfoResource.setContextHttpServletResponse(
			_httpServletResponse);
		fieldMappingInfoResource.setContextUriInfo(_uriInfo);
		fieldMappingInfoResource.setContextUser(_user);
		fieldMappingInfoResource.setGroupLocalService(_groupLocalService);
		fieldMappingInfoResource.setResourceActionLocalService(
			_resourceActionLocalService);
		fieldMappingInfoResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		fieldMappingInfoResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			KeywordQueryContributorResource keywordQueryContributorResource)
		throws Exception {

		keywordQueryContributorResource.setContextAcceptLanguage(
			_acceptLanguage);
		keywordQueryContributorResource.setContextCompany(_company);
		keywordQueryContributorResource.setContextHttpServletRequest(
			_httpServletRequest);
		keywordQueryContributorResource.setContextHttpServletResponse(
			_httpServletResponse);
		keywordQueryContributorResource.setContextUriInfo(_uriInfo);
		keywordQueryContributorResource.setContextUser(_user);
		keywordQueryContributorResource.setGroupLocalService(
			_groupLocalService);
		keywordQueryContributorResource.setResourceActionLocalService(
			_resourceActionLocalService);
		keywordQueryContributorResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		keywordQueryContributorResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ModelPrefilterContributorResource modelPrefilterContributorResource)
		throws Exception {

		modelPrefilterContributorResource.setContextAcceptLanguage(
			_acceptLanguage);
		modelPrefilterContributorResource.setContextCompany(_company);
		modelPrefilterContributorResource.setContextHttpServletRequest(
			_httpServletRequest);
		modelPrefilterContributorResource.setContextHttpServletResponse(
			_httpServletResponse);
		modelPrefilterContributorResource.setContextUriInfo(_uriInfo);
		modelPrefilterContributorResource.setContextUser(_user);
		modelPrefilterContributorResource.setGroupLocalService(
			_groupLocalService);
		modelPrefilterContributorResource.setResourceActionLocalService(
			_resourceActionLocalService);
		modelPrefilterContributorResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		modelPrefilterContributorResource.setRoleLocalService(
			_roleLocalService);
	}

	private void _populateResourceContext(
			QueryPrefilterContributorResource queryPrefilterContributorResource)
		throws Exception {

		queryPrefilterContributorResource.setContextAcceptLanguage(
			_acceptLanguage);
		queryPrefilterContributorResource.setContextCompany(_company);
		queryPrefilterContributorResource.setContextHttpServletRequest(
			_httpServletRequest);
		queryPrefilterContributorResource.setContextHttpServletResponse(
			_httpServletResponse);
		queryPrefilterContributorResource.setContextUriInfo(_uriInfo);
		queryPrefilterContributorResource.setContextUser(_user);
		queryPrefilterContributorResource.setGroupLocalService(
			_groupLocalService);
		queryPrefilterContributorResource.setResourceActionLocalService(
			_resourceActionLocalService);
		queryPrefilterContributorResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		queryPrefilterContributorResource.setRoleLocalService(
			_roleLocalService);
	}

	private void _populateResourceContext(
			SXPBlueprintResource sxpBlueprintResource)
		throws Exception {

		sxpBlueprintResource.setContextAcceptLanguage(_acceptLanguage);
		sxpBlueprintResource.setContextCompany(_company);
		sxpBlueprintResource.setContextHttpServletRequest(_httpServletRequest);
		sxpBlueprintResource.setContextHttpServletResponse(
			_httpServletResponse);
		sxpBlueprintResource.setContextUriInfo(_uriInfo);
		sxpBlueprintResource.setContextUser(_user);
		sxpBlueprintResource.setGroupLocalService(_groupLocalService);
		sxpBlueprintResource.setResourceActionLocalService(
			_resourceActionLocalService);
		sxpBlueprintResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		sxpBlueprintResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(SXPElementResource sxpElementResource)
		throws Exception {

		sxpElementResource.setContextAcceptLanguage(_acceptLanguage);
		sxpElementResource.setContextCompany(_company);
		sxpElementResource.setContextHttpServletRequest(_httpServletRequest);
		sxpElementResource.setContextHttpServletResponse(_httpServletResponse);
		sxpElementResource.setContextUriInfo(_uriInfo);
		sxpElementResource.setContextUser(_user);
		sxpElementResource.setGroupLocalService(_groupLocalService);
		sxpElementResource.setResourceActionLocalService(
			_resourceActionLocalService);
		sxpElementResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		sxpElementResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			SXPParameterContributorDefinitionResource
				sxpParameterContributorDefinitionResource)
		throws Exception {

		sxpParameterContributorDefinitionResource.setContextAcceptLanguage(
			_acceptLanguage);
		sxpParameterContributorDefinitionResource.setContextCompany(_company);
		sxpParameterContributorDefinitionResource.setContextHttpServletRequest(
			_httpServletRequest);
		sxpParameterContributorDefinitionResource.setContextHttpServletResponse(
			_httpServletResponse);
		sxpParameterContributorDefinitionResource.setContextUriInfo(_uriInfo);
		sxpParameterContributorDefinitionResource.setContextUser(_user);
		sxpParameterContributorDefinitionResource.setGroupLocalService(
			_groupLocalService);
		sxpParameterContributorDefinitionResource.setResourceActionLocalService(
			_resourceActionLocalService);
		sxpParameterContributorDefinitionResource.
			setResourcePermissionLocalService(_resourcePermissionLocalService);
		sxpParameterContributorDefinitionResource.setRoleLocalService(
			_roleLocalService);
	}

	private void _populateResourceContext(
			SearchIndexResource searchIndexResource)
		throws Exception {

		searchIndexResource.setContextAcceptLanguage(_acceptLanguage);
		searchIndexResource.setContextCompany(_company);
		searchIndexResource.setContextHttpServletRequest(_httpServletRequest);
		searchIndexResource.setContextHttpServletResponse(_httpServletResponse);
		searchIndexResource.setContextUriInfo(_uriInfo);
		searchIndexResource.setContextUser(_user);
		searchIndexResource.setGroupLocalService(_groupLocalService);
		searchIndexResource.setResourceActionLocalService(
			_resourceActionLocalService);
		searchIndexResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		searchIndexResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			SearchableAssetNameResource searchableAssetNameResource)
		throws Exception {

		searchableAssetNameResource.setContextAcceptLanguage(_acceptLanguage);
		searchableAssetNameResource.setContextCompany(_company);
		searchableAssetNameResource.setContextHttpServletRequest(
			_httpServletRequest);
		searchableAssetNameResource.setContextHttpServletResponse(
			_httpServletResponse);
		searchableAssetNameResource.setContextUriInfo(_uriInfo);
		searchableAssetNameResource.setContextUser(_user);
		searchableAssetNameResource.setGroupLocalService(_groupLocalService);
		searchableAssetNameResource.setResourceActionLocalService(
			_resourceActionLocalService);
		searchableAssetNameResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		searchableAssetNameResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			SearchableAssetNameDisplayResource
				searchableAssetNameDisplayResource)
		throws Exception {

		searchableAssetNameDisplayResource.setContextAcceptLanguage(
			_acceptLanguage);
		searchableAssetNameDisplayResource.setContextCompany(_company);
		searchableAssetNameDisplayResource.setContextHttpServletRequest(
			_httpServletRequest);
		searchableAssetNameDisplayResource.setContextHttpServletResponse(
			_httpServletResponse);
		searchableAssetNameDisplayResource.setContextUriInfo(_uriInfo);
		searchableAssetNameDisplayResource.setContextUser(_user);
		searchableAssetNameDisplayResource.setGroupLocalService(
			_groupLocalService);
		searchableAssetNameDisplayResource.setResourceActionLocalService(
			_resourceActionLocalService);
		searchableAssetNameDisplayResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		searchableAssetNameDisplayResource.setRoleLocalService(
			_roleLocalService);
	}

	private static ComponentServiceObjects<FieldMappingInfoResource>
		_fieldMappingInfoResourceComponentServiceObjects;
	private static ComponentServiceObjects<KeywordQueryContributorResource>
		_keywordQueryContributorResourceComponentServiceObjects;
	private static ComponentServiceObjects<ModelPrefilterContributorResource>
		_modelPrefilterContributorResourceComponentServiceObjects;
	private static ComponentServiceObjects<QueryPrefilterContributorResource>
		_queryPrefilterContributorResourceComponentServiceObjects;
	private static ComponentServiceObjects<SXPBlueprintResource>
		_sxpBlueprintResourceComponentServiceObjects;
	private static ComponentServiceObjects<SXPElementResource>
		_sxpElementResourceComponentServiceObjects;
	private static ComponentServiceObjects
		<SXPParameterContributorDefinitionResource>
			_sxpParameterContributorDefinitionResourceComponentServiceObjects;
	private static ComponentServiceObjects<SearchIndexResource>
		_searchIndexResourceComponentServiceObjects;
	private static ComponentServiceObjects<SearchableAssetNameResource>
		_searchableAssetNameResourceComponentServiceObjects;
	private static ComponentServiceObjects<SearchableAssetNameDisplayResource>
		_searchableAssetNameDisplayResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction
		<Object, String, com.liferay.portal.kernel.search.filter.Filter>
			_filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private ResourceActionLocalService _resourceActionLocalService;
	private ResourcePermissionLocalService _resourcePermissionLocalService;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]>
		_sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;

}