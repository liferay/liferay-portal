/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.internal.graphql.query.v1_0;

import com.liferay.headless.admin.taxonomy.dto.v1_0.Keyword;
import com.liferay.headless.admin.taxonomy.dto.v1_0.TaxonomyCategory;
import com.liferay.headless.admin.taxonomy.dto.v1_0.TaxonomyVocabulary;
import com.liferay.headless.admin.taxonomy.resource.v1_0.KeywordResource;
import com.liferay.headless.admin.taxonomy.resource.v1_0.TaxonomyCategoryResource;
import com.liferay.headless.admin.taxonomy.resource.v1_0.TaxonomyVocabularyResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.aggregation.Facet;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLTypeExtension;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.constraints.NotEmpty;

import jakarta.ws.rs.core.UriInfo;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class Query {

	public static void setKeywordResourceComponentServiceObjects(
		ComponentServiceObjects<KeywordResource>
			keywordResourceComponentServiceObjects) {

		_keywordResourceComponentServiceObjects =
			keywordResourceComponentServiceObjects;
	}

	public static void setTaxonomyCategoryResourceComponentServiceObjects(
		ComponentServiceObjects<TaxonomyCategoryResource>
			taxonomyCategoryResourceComponentServiceObjects) {

		_taxonomyCategoryResourceComponentServiceObjects =
			taxonomyCategoryResourceComponentServiceObjects;
	}

	public static void setTaxonomyVocabularyResourceComponentServiceObjects(
		ComponentServiceObjects<TaxonomyVocabularyResource>
			taxonomyVocabularyResourceComponentServiceObjects) {

		_taxonomyVocabularyResourceComponentServiceObjects =
			taxonomyVocabularyResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryKeywordByExternalReferenceCode(assetLibraryId: ___, externalReferenceCode: ___){actions, assetLibraries, assetLibraryKey, creator, dateCreated, dateModified, externalReferenceCode, id, keywordUsageCount, name, siteExternalReferenceCode, siteId, subscribed}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the asset library's keyword by external reference code."
	)
	public Keyword assetLibraryKeywordByExternalReferenceCode(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource ->
				keywordResource.getAssetLibraryKeywordByExternalReferenceCode(
					Long.valueOf(assetLibraryId), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryKeywordPermissions(assetLibraryId: ___, roleNames: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public KeywordPage assetLibraryKeywordPermissions(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource -> new KeywordPage(
				keywordResource.getAssetLibraryKeywordPermissionsPage(
					Long.valueOf(assetLibraryId), roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryKeywords(aggregation: ___, assetLibraryId: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public KeywordPage assetLibraryKeywords(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource -> new KeywordPage(
				keywordResource.getAssetLibraryKeywordsPage(
					Long.valueOf(assetLibraryId), search,
					_aggregationBiFunction.apply(keywordResource, aggregations),
					_filterBiFunction.apply(keywordResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(keywordResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {keyword(keywordId: ___){actions, assetLibraries, assetLibraryKey, creator, dateCreated, dateModified, externalReferenceCode, id, keywordUsageCount, name, siteExternalReferenceCode, siteId, subscribed}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves a keyword.")
	public Keyword keyword(@GraphQLName("keywordId") Long keywordId)
		throws Exception {

		return _applyComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource -> keywordResource.getKeyword(keywordId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {keywordsRanked(page: ___, pageSize: ___, search: ___, siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public KeywordPage keywordsRanked(
			@GraphQLName("search") String search,
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource -> new KeywordPage(
				keywordResource.getKeywordsRankedPage(
					search, Long.valueOf(siteKey),
					Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {keywordByExternalReferenceCode(externalReferenceCode: ___, siteKey: ___){actions, assetLibraries, assetLibraryKey, creator, dateCreated, dateModified, externalReferenceCode, id, keywordUsageCount, name, siteExternalReferenceCode, siteId, subscribed}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the site's keyword by external reference code."
	)
	public Keyword keywordByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource ->
				keywordResource.getSiteKeywordByExternalReferenceCode(
					Long.valueOf(siteKey), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {keywordPermissions(roleNames: ___, siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public KeywordPage keywordPermissions(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource -> new KeywordPage(
				keywordResource.getSiteKeywordPermissionsPage(
					Long.valueOf(siteKey), roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {keywords(aggregation: ___, filter: ___, page: ___, pageSize: ___, search: ___, siteKey: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves a Site's keywords. Results can be paginated, filtered, searched, and sorted."
	)
	public KeywordPage keywords(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource -> new KeywordPage(
				keywordResource.getSiteKeywordsPage(
					Long.valueOf(siteKey), search,
					_aggregationBiFunction.apply(keywordResource, aggregations),
					_filterBiFunction.apply(keywordResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(keywordResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryTaxonomyCategories(aggregation: ___, assetLibraryId: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the asset library's taxonomy categories."
	)
	public TaxonomyCategoryPage assetLibraryTaxonomyCategories(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyCategoryResource -> new TaxonomyCategoryPage(
				taxonomyCategoryResource.getAssetLibraryTaxonomyCategoriesPage(
					Long.valueOf(assetLibraryId), search,
					_aggregationBiFunction.apply(
						taxonomyCategoryResource, aggregations),
					_filterBiFunction.apply(
						taxonomyCategoryResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						taxonomyCategoryResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryTaxonomyCategoryByExternalReferenceCode(assetLibraryId: ___, externalReferenceCode: ___){actions, assetLibraryKey, availableLanguages, creator, dateCreated, dateModified, description, description_i18n, externalReferenceCode, id, name, name_i18n, numberOfTaxonomyCategories, parentTaxonomyCategory, parentTaxonomyVocabulary, path, permissions, siteExternalReferenceCode, siteId, taxonomyCategoryProperties, taxonomyCategoryUsageCount, taxonomyVocabularyId, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the asset library's taxonomy category by external reference code."
	)
	public TaxonomyCategory assetLibraryTaxonomyCategoryByExternalReferenceCode(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyCategoryResource ->
				taxonomyCategoryResource.
					getAssetLibraryTaxonomyCategoryByExternalReferenceCode(
						Long.valueOf(assetLibraryId), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {taxonomyCategories(aggregation: ___, filter: ___, page: ___, pageSize: ___, search: ___, siteKey: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the site's taxonomy categories.")
	public TaxonomyCategoryPage taxonomyCategories(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyCategoryResource -> new TaxonomyCategoryPage(
				taxonomyCategoryResource.getSiteTaxonomyCategoriesPage(
					Long.valueOf(siteKey), search,
					_aggregationBiFunction.apply(
						taxonomyCategoryResource, aggregations),
					_filterBiFunction.apply(
						taxonomyCategoryResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						taxonomyCategoryResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {taxonomyCategoryByExternalReferenceCode(externalReferenceCode: ___, siteKey: ___){actions, assetLibraryKey, availableLanguages, creator, dateCreated, dateModified, description, description_i18n, externalReferenceCode, id, name, name_i18n, numberOfTaxonomyCategories, parentTaxonomyCategory, parentTaxonomyVocabulary, path, permissions, siteExternalReferenceCode, siteId, taxonomyCategoryProperties, taxonomyCategoryUsageCount, taxonomyVocabularyId, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the site's taxonomy category by external reference code."
	)
	public TaxonomyCategory taxonomyCategoryByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyCategoryResource ->
				taxonomyCategoryResource.
					getSiteTaxonomyCategoryByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {taxonomyCategoriesRanked(page: ___, pageSize: ___, siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TaxonomyCategoryPage taxonomyCategoriesRanked(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyCategoryResource -> new TaxonomyCategoryPage(
				taxonomyCategoryResource.getTaxonomyCategoriesRankedPage(
					Long.valueOf(siteKey), Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {taxonomyCategory(taxonomyCategoryId: ___){actions, assetLibraryKey, availableLanguages, creator, dateCreated, dateModified, description, description_i18n, externalReferenceCode, id, name, name_i18n, numberOfTaxonomyCategories, parentTaxonomyCategory, parentTaxonomyVocabulary, path, permissions, siteExternalReferenceCode, siteId, taxonomyCategoryProperties, taxonomyCategoryUsageCount, taxonomyVocabularyId, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves a taxonomy category.")
	public TaxonomyCategory taxonomyCategory(
			@GraphQLName("taxonomyCategoryId") String taxonomyCategoryId)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyCategoryResource ->
				taxonomyCategoryResource.getTaxonomyCategory(
					taxonomyCategoryId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {taxonomyCategoryPermissions(roleNames: ___, taxonomyCategoryId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TaxonomyCategoryPage taxonomyCategoryPermissions(
			@GraphQLName("taxonomyCategoryId") String taxonomyCategoryId,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyCategoryResource -> new TaxonomyCategoryPage(
				taxonomyCategoryResource.getTaxonomyCategoryPermissionsPage(
					taxonomyCategoryId, roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {taxonomyCategoryTaxonomyCategories(aggregation: ___, filter: ___, page: ___, pageSize: ___, parentTaxonomyCategoryId: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves a taxonomy category's child taxonomy categories. Results can be paginated, filtered, searched, and sorted."
	)
	public TaxonomyCategoryPage taxonomyCategoryTaxonomyCategories(
			@GraphQLName("parentTaxonomyCategoryId") String
				parentTaxonomyCategoryId,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyCategoryResource -> new TaxonomyCategoryPage(
				taxonomyCategoryResource.
					getTaxonomyCategoryTaxonomyCategoriesPage(
						parentTaxonomyCategoryId, search,
						_aggregationBiFunction.apply(
							taxonomyCategoryResource, aggregations),
						_filterBiFunction.apply(
							taxonomyCategoryResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							taxonomyCategoryResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {taxonomyVocabularyTaxonomyCategories(aggregation: ___, filter: ___, flatten: ___, page: ___, pageSize: ___, search: ___, sorts: ___, taxonomyVocabularyId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves a vocabulary's taxonomy categories. Results can be paginated, filtered, searched, and sorted."
	)
	public TaxonomyCategoryPage taxonomyVocabularyTaxonomyCategories(
			@GraphQLName("taxonomyVocabularyId") Long taxonomyVocabularyId,
			@GraphQLName("flatten") Boolean flatten,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyCategoryResource -> new TaxonomyCategoryPage(
				taxonomyCategoryResource.
					getTaxonomyVocabularyTaxonomyCategoriesPage(
						taxonomyVocabularyId, flatten, search,
						_aggregationBiFunction.apply(
							taxonomyCategoryResource, aggregations),
						_filterBiFunction.apply(
							taxonomyCategoryResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							taxonomyCategoryResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {taxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(externalReferenceCode: ___, taxonomyVocabularyId: ___){actions, assetLibraryKey, availableLanguages, creator, dateCreated, dateModified, description, description_i18n, externalReferenceCode, id, name, name_i18n, numberOfTaxonomyCategories, parentTaxonomyCategory, parentTaxonomyVocabulary, path, permissions, siteExternalReferenceCode, siteId, taxonomyCategoryProperties, taxonomyCategoryUsageCount, taxonomyVocabularyId, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the site's taxonomy category by external reference code."
	)
	public TaxonomyCategory
			taxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
				@GraphQLName("taxonomyVocabularyId") Long taxonomyVocabularyId,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyCategoryResource ->
				taxonomyCategoryResource.
					getTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
						taxonomyVocabularyId, externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryTaxonomyVocabularies(aggregation: ___, assetLibraryId: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TaxonomyVocabularyPage assetLibraryTaxonomyVocabularies(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource -> new TaxonomyVocabularyPage(
				taxonomyVocabularyResource.
					getAssetLibraryTaxonomyVocabulariesPage(
						Long.valueOf(assetLibraryId), search,
						_aggregationBiFunction.apply(
							taxonomyVocabularyResource, aggregations),
						_filterBiFunction.apply(
							taxonomyVocabularyResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							taxonomyVocabularyResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryTaxonomyVocabularyByExternalReferenceCode(assetLibraryId: ___, externalReferenceCode: ___){actions, assetLibraries, assetLibraryKey, assetTypes, availableLanguages, creator, dateCreated, dateModified, description, description_i18n, externalReferenceCode, id, multiValued, name, name_i18n, numberOfTaxonomyCategories, permissions, siteExternalReferenceCode, siteId, viewableBy, visibilityType}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the asset library's taxonomy vocabulary by external reference code."
	)
	public TaxonomyVocabulary
			assetLibraryTaxonomyVocabularyByExternalReferenceCode(
				@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource ->
				taxonomyVocabularyResource.
					getAssetLibraryTaxonomyVocabularyByExternalReferenceCode(
						Long.valueOf(assetLibraryId), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryTaxonomyVocabularyPermissions(assetLibraryId: ___, roleNames: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TaxonomyVocabularyPage assetLibraryTaxonomyVocabularyPermissions(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource -> new TaxonomyVocabularyPage(
				taxonomyVocabularyResource.
					getAssetLibraryTaxonomyVocabularyPermissionsPage(
						Long.valueOf(assetLibraryId), roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {taxonomyVocabularies(aggregation: ___, filter: ___, page: ___, pageSize: ___, search: ___, siteKey: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves a Site's taxonomy vocabularies. Results can be paginated, filtered, searched, and sorted."
	)
	public TaxonomyVocabularyPage taxonomyVocabularies(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource -> new TaxonomyVocabularyPage(
				taxonomyVocabularyResource.getSiteTaxonomyVocabulariesPage(
					Long.valueOf(siteKey), search,
					_aggregationBiFunction.apply(
						taxonomyVocabularyResource, aggregations),
					_filterBiFunction.apply(
						taxonomyVocabularyResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						taxonomyVocabularyResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {taxonomyVocabularyByExternalReferenceCode(externalReferenceCode: ___, siteKey: ___){actions, assetLibraries, assetLibraryKey, assetTypes, availableLanguages, creator, dateCreated, dateModified, description, description_i18n, externalReferenceCode, id, multiValued, name, name_i18n, numberOfTaxonomyCategories, permissions, siteExternalReferenceCode, siteId, viewableBy, visibilityType}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the site's taxonomy vocabulary by external reference code."
	)
	public TaxonomyVocabulary taxonomyVocabularyByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource ->
				taxonomyVocabularyResource.
					getSiteTaxonomyVocabularyByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {siteTaxonomyVocabularyPermissions(roleNames: ___, siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TaxonomyVocabularyPage siteTaxonomyVocabularyPermissions(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource -> new TaxonomyVocabularyPage(
				taxonomyVocabularyResource.
					getSiteTaxonomyVocabularyPermissionsPage(
						Long.valueOf(siteKey), roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {taxonomyVocabulary(taxonomyVocabularyId: ___){actions, assetLibraries, assetLibraryKey, assetTypes, availableLanguages, creator, dateCreated, dateModified, description, description_i18n, externalReferenceCode, id, multiValued, name, name_i18n, numberOfTaxonomyCategories, permissions, siteExternalReferenceCode, siteId, viewableBy, visibilityType}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves a taxonomy vocabulary.")
	public TaxonomyVocabulary taxonomyVocabulary(
			@GraphQLName("taxonomyVocabularyId") Long taxonomyVocabularyId)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource ->
				taxonomyVocabularyResource.getTaxonomyVocabulary(
					taxonomyVocabularyId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {taxonomyVocabularyPermissions(roleNames: ___, taxonomyVocabularyId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TaxonomyVocabularyPage taxonomyVocabularyPermissions(
			@GraphQLName("taxonomyVocabularyId") Long taxonomyVocabularyId,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource -> new TaxonomyVocabularyPage(
				taxonomyVocabularyResource.getTaxonomyVocabularyPermissionsPage(
					taxonomyVocabularyId, roleNames)));
	}

	@GraphQLTypeExtension(TaxonomyVocabulary.class)
	public class GetTaxonomyVocabularyTaxonomyCategoriesPageTypeExtension {

		public GetTaxonomyVocabularyTaxonomyCategoriesPageTypeExtension(
			TaxonomyVocabulary taxonomyVocabulary) {

			_taxonomyVocabulary = taxonomyVocabulary;
		}

		@GraphQLField(
			description = "Retrieves a vocabulary's taxonomy categories. Results can be paginated, filtered, searched, and sorted."
		)
		public TaxonomyCategoryPage taxonomyCategories(
				@GraphQLName("flatten") Boolean flatten,
				@GraphQLName("search") String search,
				@GraphQLName("aggregation") List<String> aggregations,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_taxonomyCategoryResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				taxonomyCategoryResource -> new TaxonomyCategoryPage(
					taxonomyCategoryResource.
						getTaxonomyVocabularyTaxonomyCategoriesPage(
							_taxonomyVocabulary.getId(), flatten, search,
							_aggregationBiFunction.apply(
								taxonomyCategoryResource, aggregations),
							_filterBiFunction.apply(
								taxonomyCategoryResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								taxonomyCategoryResource, sortsString))));
		}

		private TaxonomyVocabulary _taxonomyVocabulary;

	}

	@GraphQLTypeExtension(TaxonomyCategory.class)
	public class GetTaxonomyCategoryTaxonomyCategoriesPageTypeExtension {

		public GetTaxonomyCategoryTaxonomyCategoriesPageTypeExtension(
			TaxonomyCategory taxonomyCategory) {

			_taxonomyCategory = taxonomyCategory;
		}

		@GraphQLField(
			description = "Retrieves a taxonomy category's child taxonomy categories. Results can be paginated, filtered, searched, and sorted."
		)
		public TaxonomyCategoryPage taxonomyCategories(
				@GraphQLName("search") String search,
				@GraphQLName("aggregation") List<String> aggregations,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_taxonomyCategoryResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				taxonomyCategoryResource -> new TaxonomyCategoryPage(
					taxonomyCategoryResource.
						getTaxonomyCategoryTaxonomyCategoriesPage(
							_taxonomyCategory.getId(), search,
							_aggregationBiFunction.apply(
								taxonomyCategoryResource, aggregations),
							_filterBiFunction.apply(
								taxonomyCategoryResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								taxonomyCategoryResource, sortsString))));
		}

		private TaxonomyCategory _taxonomyCategory;

	}

	@GraphQLTypeExtension(TaxonomyCategory.class)
	public class GetTaxonomyVocabularyTypeExtension {

		public GetTaxonomyVocabularyTypeExtension(
			TaxonomyCategory taxonomyCategory) {

			_taxonomyCategory = taxonomyCategory;
		}

		@GraphQLField(description = "Retrieves a taxonomy vocabulary.")
		public TaxonomyVocabulary taxonomyVocabulary() throws Exception {
			return _applyComponentServiceObjects(
				_taxonomyVocabularyResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				taxonomyVocabularyResource ->
					taxonomyVocabularyResource.getTaxonomyVocabulary(
						_taxonomyCategory.getTaxonomyVocabularyId()));
		}

		private TaxonomyCategory _taxonomyCategory;

	}

	@GraphQLTypeExtension(TaxonomyVocabulary.class)
	public class
		GetTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCodeTypeExtension {

		public GetTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCodeTypeExtension(
			TaxonomyVocabulary taxonomyVocabulary) {

			_taxonomyVocabulary = taxonomyVocabulary;
		}

		@GraphQLField(
			description = "Retrieves the site's taxonomy category by external reference code."
		)
		public TaxonomyCategory taxonomyCategoryByExternalReferenceCode(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
			throws Exception {

			return _applyComponentServiceObjects(
				_taxonomyCategoryResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				taxonomyCategoryResource ->
					taxonomyCategoryResource.
						getTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
							_taxonomyVocabulary.getId(),
							externalReferenceCode));
		}

		private TaxonomyVocabulary _taxonomyVocabulary;

	}

	@GraphQLName("KeywordPage")
	public class KeywordPage {

		public KeywordPage(Page keywordPage) {
			actions = keywordPage.getActions();

			facets = keywordPage.getFacets();

			items = keywordPage.getItems();
			lastPage = keywordPage.getLastPage();
			page = keywordPage.getPage();
			pageSize = keywordPage.getPageSize();
			totalCount = keywordPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<Keyword> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("TaxonomyCategoryPage")
	public class TaxonomyCategoryPage {

		public TaxonomyCategoryPage(Page taxonomyCategoryPage) {
			actions = taxonomyCategoryPage.getActions();

			facets = taxonomyCategoryPage.getFacets();

			items = taxonomyCategoryPage.getItems();
			lastPage = taxonomyCategoryPage.getLastPage();
			page = taxonomyCategoryPage.getPage();
			pageSize = taxonomyCategoryPage.getPageSize();
			totalCount = taxonomyCategoryPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<TaxonomyCategory> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("TaxonomyVocabularyPage")
	public class TaxonomyVocabularyPage {

		public TaxonomyVocabularyPage(Page taxonomyVocabularyPage) {
			actions = taxonomyVocabularyPage.getActions();

			facets = taxonomyVocabularyPage.getFacets();

			items = taxonomyVocabularyPage.getItems();
			lastPage = taxonomyVocabularyPage.getLastPage();
			page = taxonomyVocabularyPage.getPage();
			pageSize = taxonomyVocabularyPage.getPageSize();
			totalCount = taxonomyVocabularyPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<TaxonomyVocabulary> items;

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

	private void _populateResourceContext(KeywordResource keywordResource)
		throws Exception {

		keywordResource.setContextAcceptLanguage(_acceptLanguage);
		keywordResource.setContextCompany(_company);
		keywordResource.setContextHttpServletRequest(_httpServletRequest);
		keywordResource.setContextHttpServletResponse(_httpServletResponse);
		keywordResource.setContextUriInfo(_uriInfo);
		keywordResource.setContextUser(_user);
		keywordResource.setGroupLocalService(_groupLocalService);
		keywordResource.setResourceActionLocalService(
			_resourceActionLocalService);
		keywordResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		keywordResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			TaxonomyCategoryResource taxonomyCategoryResource)
		throws Exception {

		taxonomyCategoryResource.setContextAcceptLanguage(_acceptLanguage);
		taxonomyCategoryResource.setContextCompany(_company);
		taxonomyCategoryResource.setContextHttpServletRequest(
			_httpServletRequest);
		taxonomyCategoryResource.setContextHttpServletResponse(
			_httpServletResponse);
		taxonomyCategoryResource.setContextUriInfo(_uriInfo);
		taxonomyCategoryResource.setContextUser(_user);
		taxonomyCategoryResource.setGroupLocalService(_groupLocalService);
		taxonomyCategoryResource.setResourceActionLocalService(
			_resourceActionLocalService);
		taxonomyCategoryResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		taxonomyCategoryResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			TaxonomyVocabularyResource taxonomyVocabularyResource)
		throws Exception {

		taxonomyVocabularyResource.setContextAcceptLanguage(_acceptLanguage);
		taxonomyVocabularyResource.setContextCompany(_company);
		taxonomyVocabularyResource.setContextHttpServletRequest(
			_httpServletRequest);
		taxonomyVocabularyResource.setContextHttpServletResponse(
			_httpServletResponse);
		taxonomyVocabularyResource.setContextUriInfo(_uriInfo);
		taxonomyVocabularyResource.setContextUser(_user);
		taxonomyVocabularyResource.setGroupLocalService(_groupLocalService);
		taxonomyVocabularyResource.setResourceActionLocalService(
			_resourceActionLocalService);
		taxonomyVocabularyResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		taxonomyVocabularyResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<KeywordResource>
		_keywordResourceComponentServiceObjects;
	private static ComponentServiceObjects<TaxonomyCategoryResource>
		_taxonomyCategoryResourceComponentServiceObjects;
	private static ComponentServiceObjects<TaxonomyVocabularyResource>
		_taxonomyVocabularyResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private BiFunction<Object, List<String>, Aggregation>
		_aggregationBiFunction;
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