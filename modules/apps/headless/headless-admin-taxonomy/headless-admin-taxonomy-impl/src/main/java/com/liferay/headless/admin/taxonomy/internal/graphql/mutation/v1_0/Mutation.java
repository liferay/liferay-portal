/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.internal.graphql.mutation.v1_0;

import com.liferay.headless.admin.taxonomy.dto.v1_0.Keyword;
import com.liferay.headless.admin.taxonomy.dto.v1_0.TaxonomyCategory;
import com.liferay.headless.admin.taxonomy.dto.v1_0.TaxonomyVocabulary;
import com.liferay.headless.admin.taxonomy.resource.v1_0.KeywordResource;
import com.liferay.headless.admin.taxonomy.resource.v1_0.TaxonomyCategoryResource;
import com.liferay.headless.admin.taxonomy.resource.v1_0.TaxonomyVocabularyResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.pagination.Page;

import java.util.function.BiFunction;

import javax.annotation.Generated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.validation.constraints.NotEmpty;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class Mutation {

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

	@GraphQLField(
		description = "Deletes the asset library's keyword by external reference code."
	)
	public boolean deleteAssetLibraryKeywordByExternalReferenceCode(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource ->
				keywordResource.
					deleteAssetLibraryKeywordByExternalReferenceCode(
						Long.valueOf(assetLibraryId), externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Deletes the keyword and returns a 204 if the operation succeeds."
	)
	public boolean deleteKeyword(@GraphQLName("keywordId") Long keywordId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource -> keywordResource.deleteKeyword(keywordId));

		return true;
	}

	@GraphQLField
	public Response deleteKeywordBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource -> keywordResource.deleteKeywordBatch(
				callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the site's keyword by external reference code."
	)
	public boolean deleteSiteKeywordByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource ->
				keywordResource.deleteSiteKeywordByExternalReferenceCode(
					Long.valueOf(siteKey), externalReferenceCode));

		return true;
	}

	@GraphQLField
	public Keyword createAssetLibraryKeyword(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("keyword") Keyword keyword)
		throws Exception {

		return _applyComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource -> keywordResource.postAssetLibraryKeyword(
				Long.valueOf(assetLibraryId), keyword));
	}

	@GraphQLField
	public Response createAssetLibraryKeywordBatch(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource -> keywordResource.postAssetLibraryKeywordBatch(
				Long.valueOf(assetLibraryId), callbackURL, object));
	}

	@GraphQLField
	public Response createAssetLibraryKeywordsPageExportBatch(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource ->
				keywordResource.postAssetLibraryKeywordsPageExportBatch(
					Long.valueOf(assetLibraryId), search,
					_filterBiFunction.apply(keywordResource, filterString),
					_sortsBiFunction.apply(keywordResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Keyword createKeyword(@GraphQLName("keyword") Keyword keyword)
		throws Exception {

		return _applyComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource -> keywordResource.postKeyword(keyword));
	}

	@GraphQLField
	public Response createKeywordBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource -> keywordResource.postKeywordBatch(
				callbackURL, object));
	}

	@GraphQLField
	public Response createKeywordsPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource -> keywordResource.postKeywordsPageExportBatch(
				search, _filterBiFunction.apply(keywordResource, filterString),
				_sortsBiFunction.apply(keywordResource, sortsString),
				callbackURL, contentType, fieldNames));
	}

	@GraphQLField(description = "Inserts a new keyword in a Site.")
	public Keyword createSiteKeyword(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("keyword") Keyword keyword)
		throws Exception {

		return _applyComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource -> keywordResource.postSiteKeyword(
				Long.valueOf(siteKey), keyword));
	}

	@GraphQLField
	public Response createSiteKeywordBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource -> keywordResource.postSiteKeywordBatch(
				Long.valueOf(siteKey), callbackURL, object));
	}

	@GraphQLField
	public Response createSiteKeywordsPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource -> keywordResource.postSiteKeywordsPageExportBatch(
				Long.valueOf(siteKey), search,
				_filterBiFunction.apply(keywordResource, filterString),
				_sortsBiFunction.apply(keywordResource, sortsString),
				callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Updates the asset library's keyword with the given external reference code, or creates it if it not exists."
	)
	public Keyword updateAssetLibraryKeywordByExternalReferenceCode(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("keyword") Keyword keyword)
		throws Exception {

		return _applyComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource ->
				keywordResource.putAssetLibraryKeywordByExternalReferenceCode(
					Long.valueOf(assetLibraryId), externalReferenceCode,
					keyword));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateAssetLibraryKeywordPermissionsPage(
				@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource -> {
				Page paginationPage =
					keywordResource.putAssetLibraryKeywordPermissionsPage(
						Long.valueOf(assetLibraryId), permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField(
		description = "Replaces the keyword with the information sent in the request body. Any missing fields are deleted, unless required."
	)
	public Keyword updateKeyword(
			@GraphQLName("keywordId") Long keywordId,
			@GraphQLName("keyword") Keyword keyword)
		throws Exception {

		return _applyComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource -> keywordResource.putKeyword(keywordId, keyword));
	}

	@GraphQLField
	public Response updateKeywordBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource -> keywordResource.putKeywordBatch(
				callbackURL, object));
	}

	@GraphQLField
	public boolean updateKeywordMerge(
			@GraphQLName("toKeywordId") Long toKeywordId,
			@GraphQLName("fromKeywordIds") Long[] fromKeywordIds)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource -> keywordResource.putKeywordMerge(
				toKeywordId, fromKeywordIds));

		return true;
	}

	@GraphQLField
	public boolean updateKeywordSubscribe(
			@GraphQLName("keywordId") Long keywordId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource -> keywordResource.putKeywordSubscribe(keywordId));

		return true;
	}

	@GraphQLField
	public boolean updateKeywordUnsubscribe(
			@GraphQLName("keywordId") Long keywordId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource -> keywordResource.putKeywordUnsubscribe(
				keywordId));

		return true;
	}

	@GraphQLField(
		description = "Updates the site's keyword with the given external reference code, or creates it if it not exists."
	)
	public Keyword updateSiteKeywordByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("keyword") Keyword keyword)
		throws Exception {

		return _applyComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource ->
				keywordResource.putSiteKeywordByExternalReferenceCode(
					Long.valueOf(siteKey), externalReferenceCode, keyword));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateSiteKeywordPermissionsPage(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource -> {
				Page paginationPage =
					keywordResource.putSiteKeywordPermissionsPage(
						Long.valueOf(siteKey), permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField(
		description = "Deletes the taxonomy category and returns a 204 if the operation succeeds."
	)
	public boolean deleteTaxonomyCategory(
			@GraphQLName("taxonomyCategoryId") String taxonomyCategoryId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_taxonomyCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyCategoryResource ->
				taxonomyCategoryResource.deleteTaxonomyCategory(
					taxonomyCategoryId));

		return true;
	}

	@GraphQLField
	public Response deleteTaxonomyCategoryBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyCategoryResource ->
				taxonomyCategoryResource.deleteTaxonomyCategoryBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the site's taxonomy category by external reference code."
	)
	public boolean
			deleteTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
				@GraphQLName("taxonomyVocabularyId") Long taxonomyVocabularyId,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_taxonomyCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyCategoryResource ->
				taxonomyCategoryResource.
					deleteTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
						taxonomyVocabularyId, externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates only the fields received in the request body. Other fields are left untouched."
	)
	public TaxonomyCategory patchTaxonomyCategory(
			@GraphQLName("taxonomyCategoryId") String taxonomyCategoryId,
			@GraphQLName("taxonomyCategory") TaxonomyCategory taxonomyCategory)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyCategoryResource ->
				taxonomyCategoryResource.patchTaxonomyCategory(
					taxonomyCategoryId, taxonomyCategory));
	}

	@GraphQLField(description = "Inserts a new child taxonomy category.")
	public TaxonomyCategory createTaxonomyCategoryTaxonomyCategory(
			@GraphQLName("parentTaxonomyCategoryId") String
				parentTaxonomyCategoryId,
			@GraphQLName("taxonomyCategory") TaxonomyCategory taxonomyCategory)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyCategoryResource ->
				taxonomyCategoryResource.postTaxonomyCategoryTaxonomyCategory(
					parentTaxonomyCategoryId, taxonomyCategory));
	}

	@GraphQLField
	public Response createTaxonomyVocabularyTaxonomyCategoriesPageExportBatch(
			@GraphQLName("taxonomyVocabularyId") Long taxonomyVocabularyId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyCategoryResource ->
				taxonomyCategoryResource.
					postTaxonomyVocabularyTaxonomyCategoriesPageExportBatch(
						taxonomyVocabularyId, search,
						_filterBiFunction.apply(
							taxonomyCategoryResource, filterString),
						_sortsBiFunction.apply(
							taxonomyCategoryResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Inserts a new taxonomy category in a taxonomy vocabulary."
	)
	public TaxonomyCategory createTaxonomyVocabularyTaxonomyCategory(
			@GraphQLName("taxonomyVocabularyId") Long taxonomyVocabularyId,
			@GraphQLName("taxonomyCategory") TaxonomyCategory taxonomyCategory)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyCategoryResource ->
				taxonomyCategoryResource.postTaxonomyVocabularyTaxonomyCategory(
					taxonomyVocabularyId, taxonomyCategory));
	}

	@GraphQLField
	public Response createTaxonomyVocabularyTaxonomyCategoryBatch(
			@GraphQLName("taxonomyVocabularyId") Long taxonomyVocabularyId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyCategoryResource ->
				taxonomyCategoryResource.
					postTaxonomyVocabularyTaxonomyCategoryBatch(
						taxonomyVocabularyId, callbackURL, object));
	}

	@GraphQLField(
		description = "Replaces the taxonomy category with the information sent in the request body. Any missing fields are deleted unless they are required."
	)
	public TaxonomyCategory updateTaxonomyCategory(
			@GraphQLName("taxonomyCategoryId") String taxonomyCategoryId,
			@GraphQLName("taxonomyCategory") TaxonomyCategory taxonomyCategory)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyCategoryResource ->
				taxonomyCategoryResource.putTaxonomyCategory(
					taxonomyCategoryId, taxonomyCategory));
	}

	@GraphQLField
	public Response updateTaxonomyCategoryBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyCategoryResource ->
				taxonomyCategoryResource.putTaxonomyCategoryBatch(
					callbackURL, object));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateTaxonomyCategoryPermissionsPage(
				@GraphQLName("taxonomyCategoryId") String taxonomyCategoryId,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyCategoryResource -> {
				Page paginationPage =
					taxonomyCategoryResource.putTaxonomyCategoryPermissionsPage(
						taxonomyCategoryId, permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField(
		description = "Updates the site's taxonomy category with the given external reference code, or creates it if it not exists."
	)
	public TaxonomyCategory
			updateTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
				@GraphQLName("taxonomyVocabularyId") Long taxonomyVocabularyId,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("taxonomyCategory") TaxonomyCategory
					taxonomyCategory)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyCategoryResource ->
				taxonomyCategoryResource.
					putTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
						taxonomyVocabularyId, externalReferenceCode,
						taxonomyCategory));
	}

	@GraphQLField(
		description = "Deletes the asset library's taxonomy vocabulary by external reference code."
	)
	public boolean deleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource ->
				taxonomyVocabularyResource.
					deleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode(
						Long.valueOf(assetLibraryId), externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Deletes the site's taxonomy vocabulary by external reference code."
	)
	public boolean deleteSiteTaxonomyVocabularyByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource ->
				taxonomyVocabularyResource.
					deleteSiteTaxonomyVocabularyByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Deletes the taxonomy vocabulary and returns a 204 if the operation succeeds."
	)
	public boolean deleteTaxonomyVocabulary(
			@GraphQLName("taxonomyVocabularyId") Long taxonomyVocabularyId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource ->
				taxonomyVocabularyResource.deleteTaxonomyVocabulary(
					taxonomyVocabularyId));

		return true;
	}

	@GraphQLField
	public Response deleteTaxonomyVocabularyBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource ->
				taxonomyVocabularyResource.deleteTaxonomyVocabularyBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Updates only the fields received in the request body. Any other fields are left untouched."
	)
	public TaxonomyVocabulary patchTaxonomyVocabulary(
			@GraphQLName("taxonomyVocabularyId") Long taxonomyVocabularyId,
			@GraphQLName("taxonomyVocabulary") TaxonomyVocabulary
				taxonomyVocabulary)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource ->
				taxonomyVocabularyResource.patchTaxonomyVocabulary(
					taxonomyVocabularyId, taxonomyVocabulary));
	}

	@GraphQLField
	public Response createAssetLibraryTaxonomyVocabulariesPageExportBatch(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource ->
				taxonomyVocabularyResource.
					postAssetLibraryTaxonomyVocabulariesPageExportBatch(
						Long.valueOf(assetLibraryId), search,
						_filterBiFunction.apply(
							taxonomyVocabularyResource, filterString),
						_sortsBiFunction.apply(
							taxonomyVocabularyResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public TaxonomyVocabulary createAssetLibraryTaxonomyVocabulary(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("taxonomyVocabulary") TaxonomyVocabulary
				taxonomyVocabulary)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource ->
				taxonomyVocabularyResource.postAssetLibraryTaxonomyVocabulary(
					Long.valueOf(assetLibraryId), taxonomyVocabulary));
	}

	@GraphQLField
	public Response createAssetLibraryTaxonomyVocabularyBatch(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource ->
				taxonomyVocabularyResource.
					postAssetLibraryTaxonomyVocabularyBatch(
						Long.valueOf(assetLibraryId), callbackURL, object));
	}

	@GraphQLField
	public Response createSiteTaxonomyVocabulariesPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource ->
				taxonomyVocabularyResource.
					postSiteTaxonomyVocabulariesPageExportBatch(
						Long.valueOf(siteKey), search,
						_filterBiFunction.apply(
							taxonomyVocabularyResource, filterString),
						_sortsBiFunction.apply(
							taxonomyVocabularyResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField(description = "Inserts a new taxonomy vocabulary in a Site.")
	public TaxonomyVocabulary createSiteTaxonomyVocabulary(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("taxonomyVocabulary") TaxonomyVocabulary
				taxonomyVocabulary)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource ->
				taxonomyVocabularyResource.postSiteTaxonomyVocabulary(
					Long.valueOf(siteKey), taxonomyVocabulary));
	}

	@GraphQLField
	public Response createSiteTaxonomyVocabularyBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource ->
				taxonomyVocabularyResource.postSiteTaxonomyVocabularyBatch(
					Long.valueOf(siteKey), callbackURL, object));
	}

	@GraphQLField
	public Response createTaxonomyVocabulariesPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource ->
				taxonomyVocabularyResource.
					postTaxonomyVocabulariesPageExportBatch(
						search,
						_filterBiFunction.apply(
							taxonomyVocabularyResource, filterString),
						_sortsBiFunction.apply(
							taxonomyVocabularyResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Creates the asset library's taxonomy vocabulary in the given spaces."
	)
	public TaxonomyVocabulary createTaxonomyVocabulary(
			@GraphQLName("taxonomyVocabulary") TaxonomyVocabulary
				taxonomyVocabulary)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource ->
				taxonomyVocabularyResource.postTaxonomyVocabulary(
					taxonomyVocabulary));
	}

	@GraphQLField
	public Response createTaxonomyVocabularyBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource ->
				taxonomyVocabularyResource.postTaxonomyVocabularyBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Updates the asset library's taxonomy vocabulary with the given external reference code, or creates it if it not exists."
	)
	public TaxonomyVocabulary
			updateAssetLibraryTaxonomyVocabularyByExternalReferenceCode(
				@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("taxonomyVocabulary") TaxonomyVocabulary
					taxonomyVocabulary)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource ->
				taxonomyVocabularyResource.
					putAssetLibraryTaxonomyVocabularyByExternalReferenceCode(
						Long.valueOf(assetLibraryId), externalReferenceCode,
						taxonomyVocabulary));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateAssetLibraryTaxonomyVocabularyPermissionsPage(
				@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource -> {
				Page paginationPage =
					taxonomyVocabularyResource.
						putAssetLibraryTaxonomyVocabularyPermissionsPage(
							Long.valueOf(assetLibraryId), permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField(
		description = "Updates the site's taxonomy vocabulary with the given external reference code, or creates it if it not exists."
	)
	public TaxonomyVocabulary
			updateSiteTaxonomyVocabularyByExternalReferenceCode(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("taxonomyVocabulary") TaxonomyVocabulary
					taxonomyVocabulary)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource ->
				taxonomyVocabularyResource.
					putSiteTaxonomyVocabularyByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode,
						taxonomyVocabulary));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateSiteTaxonomyVocabularyPermissionsPage(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource -> {
				Page paginationPage =
					taxonomyVocabularyResource.
						putSiteTaxonomyVocabularyPermissionsPage(
							Long.valueOf(siteKey), permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField(
		description = "Replaces the taxonomy vocabulary with the information sent in the request body. Any missing fields are deleted unless they are required."
	)
	public TaxonomyVocabulary updateTaxonomyVocabulary(
			@GraphQLName("taxonomyVocabularyId") Long taxonomyVocabularyId,
			@GraphQLName("taxonomyVocabulary") TaxonomyVocabulary
				taxonomyVocabulary)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource ->
				taxonomyVocabularyResource.putTaxonomyVocabulary(
					taxonomyVocabularyId, taxonomyVocabulary));
	}

	@GraphQLField
	public Response updateTaxonomyVocabularyBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource ->
				taxonomyVocabularyResource.putTaxonomyVocabularyBatch(
					callbackURL, object));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateTaxonomyVocabularyPermissionsPage(
				@GraphQLName("taxonomyVocabularyId") Long taxonomyVocabularyId,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource -> {
				Page paginationPage =
					taxonomyVocabularyResource.
						putTaxonomyVocabularyPermissionsPage(
							taxonomyVocabularyId, permissions);

				return paginationPage.getItems();
			});
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

	private <T, E1 extends Throwable, E2 extends Throwable> void
			_applyVoidComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeConsumer<T, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			unsafeFunction.accept(resource);
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
		keywordResource.setRoleLocalService(_roleLocalService);

		keywordResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		keywordResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		taxonomyCategoryResource.setRoleLocalService(_roleLocalService);

		taxonomyCategoryResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		taxonomyCategoryResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		taxonomyVocabularyResource.setRoleLocalService(_roleLocalService);

		taxonomyVocabularyResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		taxonomyVocabularyResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private static ComponentServiceObjects<KeywordResource>
		_keywordResourceComponentServiceObjects;
	private static ComponentServiceObjects<TaxonomyCategoryResource>
		_taxonomyCategoryResourceComponentServiceObjects;
	private static ComponentServiceObjects<TaxonomyVocabularyResource>
		_taxonomyVocabularyResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction
		<Object, String, com.liferay.portal.kernel.search.filter.Filter>
			_filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]>
		_sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;
	private VulcanBatchEngineExportTaskResource
		_vulcanBatchEngineExportTaskResource;
	private VulcanBatchEngineImportTaskResource
		_vulcanBatchEngineImportTaskResource;

}