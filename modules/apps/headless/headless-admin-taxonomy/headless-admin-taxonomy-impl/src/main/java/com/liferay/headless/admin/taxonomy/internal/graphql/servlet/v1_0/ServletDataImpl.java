/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.internal.graphql.servlet.v1_0;

import com.liferay.headless.admin.taxonomy.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.headless.admin.taxonomy.internal.graphql.query.v1_0.Query;
import com.liferay.headless.admin.taxonomy.internal.resource.v1_0.KeywordResourceImpl;
import com.liferay.headless.admin.taxonomy.internal.resource.v1_0.TaxonomyCategoryResourceImpl;
import com.liferay.headless.admin.taxonomy.internal.resource.v1_0.TaxonomyVocabularyResourceImpl;
import com.liferay.headless.admin.taxonomy.resource.v1_0.KeywordResource;
import com.liferay.headless.admin.taxonomy.resource.v1_0.TaxonomyCategoryResource;
import com.liferay.headless.admin.taxonomy.resource.v1_0.TaxonomyVocabularyResource;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;

import jakarta.annotation.Generated;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Javier Gamarra
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setKeywordResourceComponentServiceObjects(
			_keywordResourceComponentServiceObjects);
		Mutation.setTaxonomyCategoryResourceComponentServiceObjects(
			_taxonomyCategoryResourceComponentServiceObjects);
		Mutation.setTaxonomyVocabularyResourceComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects);

		Query.setKeywordResourceComponentServiceObjects(
			_keywordResourceComponentServiceObjects);
		Query.setTaxonomyCategoryResourceComponentServiceObjects(
			_taxonomyCategoryResourceComponentServiceObjects);
		Query.setTaxonomyVocabularyResourceComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Headless.Admin.Taxonomy";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/headless-admin-taxonomy-graphql/v1_0";
	}

	@Override
	public Query getQuery() {
		return new Query();
	}

	public ObjectValuePair<Class<?>, String> getResourceMethodObjectValuePair(
		String methodName, boolean mutation) {

		if (mutation) {
			return _resourceMethodObjectValuePairs.get(
				"mutation#" + methodName);
		}

		return _resourceMethodObjectValuePairs.get("query#" + methodName);
	}

	private static final Map<String, ObjectValuePair<Class<?>, String>>
		_resourceMethodObjectValuePairs =
			new HashMap<String, ObjectValuePair<Class<?>, String>>() {
				{
					put(
						"mutation#deleteAssetLibraryKeywordByExternalReferenceCode",
						new ObjectValuePair<>(
							KeywordResourceImpl.class,
							"deleteAssetLibraryKeywordByExternalReferenceCode"));
					put(
						"mutation#deleteKeyword",
						new ObjectValuePair<>(
							KeywordResourceImpl.class, "deleteKeyword"));
					put(
						"mutation#deleteKeywordBatch",
						new ObjectValuePair<>(
							KeywordResourceImpl.class, "deleteKeywordBatch"));
					put(
						"mutation#deleteSiteKeywordByExternalReferenceCode",
						new ObjectValuePair<>(
							KeywordResourceImpl.class,
							"deleteSiteKeywordByExternalReferenceCode"));
					put(
						"mutation#createAssetLibraryKeyword",
						new ObjectValuePair<>(
							KeywordResourceImpl.class,
							"postAssetLibraryKeyword"));
					put(
						"mutation#createAssetLibraryKeywordBatch",
						new ObjectValuePair<>(
							KeywordResourceImpl.class,
							"postAssetLibraryKeywordBatch"));
					put(
						"mutation#createAssetLibraryKeywordsPageExportBatch",
						new ObjectValuePair<>(
							KeywordResourceImpl.class,
							"postAssetLibraryKeywordsPageExportBatch"));
					put(
						"mutation#createSiteKeyword",
						new ObjectValuePair<>(
							KeywordResourceImpl.class, "postSiteKeyword"));
					put(
						"mutation#createSiteKeywordBatch",
						new ObjectValuePair<>(
							KeywordResourceImpl.class, "postSiteKeywordBatch"));
					put(
						"mutation#createSiteKeywordsPageExportBatch",
						new ObjectValuePair<>(
							KeywordResourceImpl.class,
							"postSiteKeywordsPageExportBatch"));
					put(
						"mutation#updateAssetLibraryKeywordByExternalReferenceCode",
						new ObjectValuePair<>(
							KeywordResourceImpl.class,
							"putAssetLibraryKeywordByExternalReferenceCode"));
					put(
						"mutation#updateAssetLibraryKeywordPermissionsPage",
						new ObjectValuePair<>(
							KeywordResourceImpl.class,
							"putAssetLibraryKeywordPermissionsPage"));
					put(
						"mutation#updateKeyword",
						new ObjectValuePair<>(
							KeywordResourceImpl.class, "putKeyword"));
					put(
						"mutation#updateKeywordBatch",
						new ObjectValuePair<>(
							KeywordResourceImpl.class, "putKeywordBatch"));
					put(
						"mutation#updateKeywordMerge",
						new ObjectValuePair<>(
							KeywordResourceImpl.class, "putKeywordMerge"));
					put(
						"mutation#updateKeywordSubscribe",
						new ObjectValuePair<>(
							KeywordResourceImpl.class, "putKeywordSubscribe"));
					put(
						"mutation#updateKeywordUnsubscribe",
						new ObjectValuePair<>(
							KeywordResourceImpl.class,
							"putKeywordUnsubscribe"));
					put(
						"mutation#updateSiteKeywordByExternalReferenceCode",
						new ObjectValuePair<>(
							KeywordResourceImpl.class,
							"putSiteKeywordByExternalReferenceCode"));
					put(
						"mutation#updateSiteKeywordPermissionsPage",
						new ObjectValuePair<>(
							KeywordResourceImpl.class,
							"putSiteKeywordPermissionsPage"));
					put(
						"mutation#deleteAssetLibraryTaxonomyCategoryByExternalReferenceCode",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"deleteAssetLibraryTaxonomyCategoryByExternalReferenceCode"));
					put(
						"mutation#deleteSiteTaxonomyCategoryByExternalReferenceCode",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"deleteSiteTaxonomyCategoryByExternalReferenceCode"));
					put(
						"mutation#deleteTaxonomyCategory",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"deleteTaxonomyCategory"));
					put(
						"mutation#deleteTaxonomyCategoryBatch",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"deleteTaxonomyCategoryBatch"));
					put(
						"mutation#deleteTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"deleteTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode"));
					put(
						"mutation#patchTaxonomyCategory",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"patchTaxonomyCategory"));
					put(
						"mutation#createAssetLibraryTaxonomyCategoriesPageExportBatch",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"postAssetLibraryTaxonomyCategoriesPageExportBatch"));
					put(
						"mutation#createAssetLibraryTaxonomyCategory",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"postAssetLibraryTaxonomyCategory"));
					put(
						"mutation#createAssetLibraryTaxonomyCategoryBatch",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"postAssetLibraryTaxonomyCategoryBatch"));
					put(
						"mutation#createSiteTaxonomyCategoriesPageExportBatch",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"postSiteTaxonomyCategoriesPageExportBatch"));
					put(
						"mutation#createSiteTaxonomyCategory",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"postSiteTaxonomyCategory"));
					put(
						"mutation#createSiteTaxonomyCategoryBatch",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"postSiteTaxonomyCategoryBatch"));
					put(
						"mutation#createTaxonomyCategoryTaxonomyCategory",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"postTaxonomyCategoryTaxonomyCategory"));
					put(
						"mutation#createTaxonomyVocabularyTaxonomyCategoriesPageExportBatch",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"postTaxonomyVocabularyTaxonomyCategoriesPageExportBatch"));
					put(
						"mutation#createTaxonomyVocabularyTaxonomyCategory",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"postTaxonomyVocabularyTaxonomyCategory"));
					put(
						"mutation#createTaxonomyVocabularyTaxonomyCategoryBatch",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"postTaxonomyVocabularyTaxonomyCategoryBatch"));
					put(
						"mutation#updateAssetLibraryTaxonomyCategoryByExternalReferenceCode",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"putAssetLibraryTaxonomyCategoryByExternalReferenceCode"));
					put(
						"mutation#updateSiteTaxonomyCategoryByExternalReferenceCode",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"putSiteTaxonomyCategoryByExternalReferenceCode"));
					put(
						"mutation#updateTaxonomyCategory",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"putTaxonomyCategory"));
					put(
						"mutation#updateTaxonomyCategoryBatch",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"putTaxonomyCategoryBatch"));
					put(
						"mutation#updateTaxonomyCategoryPermissionsPage",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"putTaxonomyCategoryPermissionsPage"));
					put(
						"mutation#updateTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"putTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode"));
					put(
						"mutation#deleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"deleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode"));
					put(
						"mutation#deleteSiteTaxonomyVocabularyByExternalReferenceCode",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"deleteSiteTaxonomyVocabularyByExternalReferenceCode"));
					put(
						"mutation#deleteTaxonomyVocabulary",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"deleteTaxonomyVocabulary"));
					put(
						"mutation#deleteTaxonomyVocabularyBatch",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"deleteTaxonomyVocabularyBatch"));
					put(
						"mutation#patchTaxonomyVocabulary",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"patchTaxonomyVocabulary"));
					put(
						"mutation#createAssetLibraryTaxonomyVocabulariesPageExportBatch",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"postAssetLibraryTaxonomyVocabulariesPageExportBatch"));
					put(
						"mutation#createAssetLibraryTaxonomyVocabulary",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"postAssetLibraryTaxonomyVocabulary"));
					put(
						"mutation#createAssetLibraryTaxonomyVocabularyBatch",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"postAssetLibraryTaxonomyVocabularyBatch"));
					put(
						"mutation#createSiteTaxonomyVocabulariesPageExportBatch",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"postSiteTaxonomyVocabulariesPageExportBatch"));
					put(
						"mutation#createSiteTaxonomyVocabulary",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"postSiteTaxonomyVocabulary"));
					put(
						"mutation#createSiteTaxonomyVocabularyBatch",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"postSiteTaxonomyVocabularyBatch"));
					put(
						"mutation#updateAssetLibraryTaxonomyVocabularyByExternalReferenceCode",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"putAssetLibraryTaxonomyVocabularyByExternalReferenceCode"));
					put(
						"mutation#updateAssetLibraryTaxonomyVocabularyPermissionsPage",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"putAssetLibraryTaxonomyVocabularyPermissionsPage"));
					put(
						"mutation#updateSiteTaxonomyVocabularyByExternalReferenceCode",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"putSiteTaxonomyVocabularyByExternalReferenceCode"));
					put(
						"mutation#updateSiteTaxonomyVocabularyPermissionsPage",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"putSiteTaxonomyVocabularyPermissionsPage"));
					put(
						"mutation#updateTaxonomyVocabulary",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"putTaxonomyVocabulary"));
					put(
						"mutation#updateTaxonomyVocabularyBatch",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"putTaxonomyVocabularyBatch"));
					put(
						"mutation#updateTaxonomyVocabularyPermissionsPage",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"putTaxonomyVocabularyPermissionsPage"));

					put(
						"query#assetLibraryKeywordByExternalReferenceCode",
						new ObjectValuePair<>(
							KeywordResourceImpl.class,
							"getAssetLibraryKeywordByExternalReferenceCode"));
					put(
						"query#assetLibraryKeywordPermissions",
						new ObjectValuePair<>(
							KeywordResourceImpl.class,
							"getAssetLibraryKeywordPermissionsPage"));
					put(
						"query#assetLibraryKeywords",
						new ObjectValuePair<>(
							KeywordResourceImpl.class,
							"getAssetLibraryKeywordsPage"));
					put(
						"query#keyword",
						new ObjectValuePair<>(
							KeywordResourceImpl.class, "getKeyword"));
					put(
						"query#keywordsRanked",
						new ObjectValuePair<>(
							KeywordResourceImpl.class,
							"getKeywordsRankedPage"));
					put(
						"query#keywordByExternalReferenceCode",
						new ObjectValuePair<>(
							KeywordResourceImpl.class,
							"getSiteKeywordByExternalReferenceCode"));
					put(
						"query#keywordPermissions",
						new ObjectValuePair<>(
							KeywordResourceImpl.class,
							"getSiteKeywordPermissionsPage"));
					put(
						"query#keywords",
						new ObjectValuePair<>(
							KeywordResourceImpl.class, "getSiteKeywordsPage"));
					put(
						"query#assetLibraryTaxonomyCategories",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"getAssetLibraryTaxonomyCategoriesPage"));
					put(
						"query#assetLibraryTaxonomyCategoryByExternalReferenceCode",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"getAssetLibraryTaxonomyCategoryByExternalReferenceCode"));
					put(
						"query#taxonomyCategories",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"getSiteTaxonomyCategoriesPage"));
					put(
						"query#taxonomyCategoryByExternalReferenceCode",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"getSiteTaxonomyCategoryByExternalReferenceCode"));
					put(
						"query#taxonomyCategoriesRanked",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"getTaxonomyCategoriesRankedPage"));
					put(
						"query#taxonomyCategory",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"getTaxonomyCategory"));
					put(
						"query#taxonomyCategoryPermissions",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"getTaxonomyCategoryPermissionsPage"));
					put(
						"query#taxonomyCategoryTaxonomyCategories",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"getTaxonomyCategoryTaxonomyCategoriesPage"));
					put(
						"query#taxonomyVocabularyTaxonomyCategories",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"getTaxonomyVocabularyTaxonomyCategoriesPage"));
					put(
						"query#taxonomyVocabularyTaxonomyCategoryByExternalReferenceCode",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"getTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode"));
					put(
						"query#assetLibraryTaxonomyVocabularies",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"getAssetLibraryTaxonomyVocabulariesPage"));
					put(
						"query#assetLibraryTaxonomyVocabularyByExternalReferenceCode",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"getAssetLibraryTaxonomyVocabularyByExternalReferenceCode"));
					put(
						"query#assetLibraryTaxonomyVocabularyPermissions",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"getAssetLibraryTaxonomyVocabularyPermissionsPage"));
					put(
						"query#taxonomyVocabularies",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"getSiteTaxonomyVocabulariesPage"));
					put(
						"query#taxonomyVocabularyByExternalReferenceCode",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"getSiteTaxonomyVocabularyByExternalReferenceCode"));
					put(
						"query#siteTaxonomyVocabularyPermissions",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"getSiteTaxonomyVocabularyPermissionsPage"));
					put(
						"query#taxonomyVocabulary",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"getTaxonomyVocabulary"));
					put(
						"query#taxonomyVocabularyPermissions",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"getTaxonomyVocabularyPermissionsPage"));

					put(
						"query#TaxonomyVocabulary.taxonomyCategories",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"getTaxonomyVocabularyTaxonomyCategoriesPage"));
					put(
						"query#TaxonomyCategory.taxonomyCategories",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"getTaxonomyCategoryTaxonomyCategoriesPage"));
					put(
						"query#TaxonomyCategory.taxonomyVocabulary",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"getTaxonomyVocabulary"));
					put(
						"query#TaxonomyVocabulary.taxonomyCategoryByExternalReferenceCode",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"getTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<KeywordResource>
		_keywordResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TaxonomyCategoryResource>
		_taxonomyCategoryResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TaxonomyVocabularyResource>
		_taxonomyVocabularyResourceComponentServiceObjects;

}