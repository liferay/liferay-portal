/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.resource.v1_0;

import com.liferay.headless.admin.taxonomy.dto.v1_0.TaxonomyCategory;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.odata.filter.ExpressionConvert;
import com.liferay.portal.odata.filter.FilterParserProvider;
import com.liferay.portal.odata.sort.SortParserProvider;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * To access this resource, run:
 *
 *     curl -u your@email.com:yourpassword -D - http://localhost:8080/o/headless-admin-taxonomy/v1.0
 *
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@ProviderType
public interface TaxonomyCategoryResource {

	public void deleteAssetLibraryTaxonomyCategoryByExternalReferenceCode(
			Long assetLibraryId, String externalReferenceCode)
		throws Exception;

	public void deleteSiteTaxonomyCategoryByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception;

	public void deleteTaxonomyCategory(String taxonomyCategoryId)
		throws Exception;

	public Response deleteTaxonomyCategoryBatch(
			String callbackURL, Object object)
		throws Exception;

	public void deleteTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
			Long taxonomyVocabularyId, String externalReferenceCode)
		throws Exception;

	public Page<TaxonomyCategory> getAssetLibraryTaxonomyCategoriesPage(
			Long assetLibraryId, String search,
			com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public TaxonomyCategory
			getAssetLibraryTaxonomyCategoryByExternalReferenceCode(
				Long assetLibraryId, String externalReferenceCode)
		throws Exception;

	public Page<TaxonomyCategory> getSiteTaxonomyCategoriesPage(
			Long siteId, String search,
			com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public TaxonomyCategory getSiteTaxonomyCategoryByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception;

	public Page<TaxonomyCategory> getTaxonomyCategoriesRankedPage(
			Long siteId, Pagination pagination)
		throws Exception;

	public TaxonomyCategory getTaxonomyCategory(String taxonomyCategoryId)
		throws Exception;

	public Page<com.liferay.portal.vulcan.permission.Permission>
			getTaxonomyCategoryPermissionsPage(
				String taxonomyCategoryId, String roleNames)
		throws Exception;

	public Page<TaxonomyCategory> getTaxonomyCategoryTaxonomyCategoriesPage(
			String parentTaxonomyCategoryId, String search,
			com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public Page<TaxonomyCategory> getTaxonomyVocabularyTaxonomyCategoriesPage(
			Long taxonomyVocabularyId, Boolean flatten, String search,
			com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public TaxonomyCategory
			getTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
				Long taxonomyVocabularyId, String externalReferenceCode)
		throws Exception;

	public TaxonomyCategory patchTaxonomyCategory(
			String taxonomyCategoryId, TaxonomyCategory taxonomyCategory)
		throws Exception;

	public Response postAssetLibraryTaxonomyCategoriesPageExportBatch(
			Long assetLibraryId, String search,
			com.liferay.portal.kernel.search.filter.Filter filter,
			com.liferay.portal.kernel.search.Sort[] sorts, String callbackURL,
			String contentType, String fieldNames)
		throws Exception;

	public TaxonomyCategory postAssetLibraryTaxonomyCategory(
			Long assetLibraryId, TaxonomyCategory taxonomyCategory)
		throws Exception;

	public Response postAssetLibraryTaxonomyCategoryBatch(
			Long assetLibraryId, String callbackURL, Object object)
		throws Exception;

	public Response postSiteTaxonomyCategoriesPageExportBatch(
			Long siteId, String search,
			com.liferay.portal.kernel.search.filter.Filter filter,
			com.liferay.portal.kernel.search.Sort[] sorts, String callbackURL,
			String contentType, String fieldNames)
		throws Exception;

	public TaxonomyCategory postSiteTaxonomyCategory(
			Long siteId, TaxonomyCategory taxonomyCategory)
		throws Exception;

	public Response postSiteTaxonomyCategoryBatch(
			Long siteId, String callbackURL, Object object)
		throws Exception;

	public TaxonomyCategory postTaxonomyCategoryTaxonomyCategory(
			String parentTaxonomyCategoryId, TaxonomyCategory taxonomyCategory)
		throws Exception;

	public Response postTaxonomyVocabularyTaxonomyCategoriesPageExportBatch(
			Long taxonomyVocabularyId, String search,
			com.liferay.portal.kernel.search.filter.Filter filter,
			com.liferay.portal.kernel.search.Sort[] sorts, String callbackURL,
			String contentType, String fieldNames)
		throws Exception;

	public TaxonomyCategory postTaxonomyVocabularyTaxonomyCategory(
			Long taxonomyVocabularyId, TaxonomyCategory taxonomyCategory)
		throws Exception;

	public Response postTaxonomyVocabularyTaxonomyCategoryBatch(
			Long taxonomyVocabularyId, String callbackURL, Object object)
		throws Exception;

	public TaxonomyCategory
			putAssetLibraryTaxonomyCategoryByExternalReferenceCode(
				Long assetLibraryId, String externalReferenceCode,
				TaxonomyCategory taxonomyCategory)
		throws Exception;

	public TaxonomyCategory putSiteTaxonomyCategoryByExternalReferenceCode(
			Long siteId, String externalReferenceCode,
			TaxonomyCategory taxonomyCategory)
		throws Exception;

	public TaxonomyCategory putTaxonomyCategory(
			String taxonomyCategoryId, TaxonomyCategory taxonomyCategory)
		throws Exception;

	public Response putTaxonomyCategoryBatch(String callbackURL, Object object)
		throws Exception;

	public Page<com.liferay.portal.vulcan.permission.Permission>
			putTaxonomyCategoryPermissionsPage(
				String taxonomyCategoryId,
				com.liferay.portal.vulcan.permission.Permission[] permissions)
		throws Exception;

	public TaxonomyCategory
			putTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
				Long taxonomyVocabularyId, String externalReferenceCode,
				TaxonomyCategory taxonomyCategory)
		throws Exception;

	public default void setContextAcceptLanguage(
		AcceptLanguage contextAcceptLanguage) {
	}

	public void setContextCompany(
		com.liferay.portal.kernel.model.Company contextCompany);

	public default void setContextHttpServletRequest(
		HttpServletRequest contextHttpServletRequest) {
	}

	public default void setContextHttpServletResponse(
		HttpServletResponse contextHttpServletResponse) {
	}

	public default void setContextUriInfo(UriInfo contextUriInfo) {
	}

	public void setContextUser(
		com.liferay.portal.kernel.model.User contextUser);

	public void setExpressionConvert(
		ExpressionConvert<com.liferay.portal.kernel.search.filter.Filter>
			expressionConvert);

	public void setFilterParserProvider(
		FilterParserProvider filterParserProvider);

	public void setGroupLocalService(GroupLocalService groupLocalService);

	public void setResourceActionLocalService(
		ResourceActionLocalService resourceActionLocalService);

	public void setResourcePermissionLocalService(
		ResourcePermissionLocalService resourcePermissionLocalService);

	public void setRoleLocalService(RoleLocalService roleLocalService);

	public void setSortParserProvider(SortParserProvider sortParserProvider);

	public void setVulcanBatchEngineExportTaskResource(
		VulcanBatchEngineExportTaskResource
			vulcanBatchEngineExportTaskResource);

	public void setVulcanBatchEngineImportTaskResource(
		VulcanBatchEngineImportTaskResource
			vulcanBatchEngineImportTaskResource);

	public default com.liferay.portal.kernel.search.filter.Filter toFilter(
		String filterString) {

		return toFilter(
			filterString, Collections.<String, List<String>>emptyMap());
	}

	public default com.liferay.portal.kernel.search.filter.Filter toFilter(
		String filterString, Map<String, List<String>> multivaluedMap) {

		return null;
	}

	public default com.liferay.portal.kernel.search.Sort[] toSorts(
		String sortsString) {

		return new com.liferay.portal.kernel.search.Sort[0];
	}

	@ProviderType
	public interface Builder {

		public TaxonomyCategoryResource build();

		public Builder checkPermissions(boolean checkPermissions);

		public Builder httpServletRequest(
			HttpServletRequest httpServletRequest);

		public Builder httpServletResponse(
			HttpServletResponse httpServletResponse);

		public Builder preferredLocale(Locale preferredLocale);

		public Builder uriInfo(UriInfo uriInfo);

		public Builder user(com.liferay.portal.kernel.model.User user);

	}

	@ProviderType
	public interface Factory {

		public Builder create();

	}

}