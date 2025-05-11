/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.resource.v1_0;

import com.liferay.headless.admin.taxonomy.dto.v1_0.Keyword;
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

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Generated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

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
public interface KeywordResource {

	public void deleteAssetLibraryKeywordByExternalReferenceCode(
			Long assetLibraryId, String externalReferenceCode)
		throws Exception;

	public void deleteKeyword(Long keywordId) throws Exception;

	public Response deleteKeywordBatch(String callbackURL, Object object)
		throws Exception;

	public void deleteSiteKeywordByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception;

	public Keyword getAssetLibraryKeywordByExternalReferenceCode(
			Long assetLibraryId, String externalReferenceCode)
		throws Exception;

	public Page<com.liferay.portal.vulcan.permission.Permission>
			getAssetLibraryKeywordPermissionsPage(
				Long assetLibraryId, String roleNames)
		throws Exception;

	public Page<Keyword> getAssetLibraryKeywordsPage(
			Long assetLibraryId, String search,
			com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public Keyword getKeyword(Long keywordId) throws Exception;

	public Page<Keyword> getKeywordsPage(
			String search,
			com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public Page<Keyword> getKeywordsRankedPage(
			String search, Long siteId, Pagination pagination)
		throws Exception;

	public Keyword getSiteKeywordByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception;

	public Page<com.liferay.portal.vulcan.permission.Permission>
			getSiteKeywordPermissionsPage(Long siteId, String roleNames)
		throws Exception;

	public Page<Keyword> getSiteKeywordsPage(
			Long siteId, String search,
			com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public Keyword postAssetLibraryKeyword(Long assetLibraryId, Keyword keyword)
		throws Exception;

	public Response postAssetLibraryKeywordBatch(
			Long assetLibraryId, String callbackURL, Object object)
		throws Exception;

	public Response postAssetLibraryKeywordsPageExportBatch(
			Long assetLibraryId, String search,
			com.liferay.portal.kernel.search.filter.Filter filter,
			com.liferay.portal.kernel.search.Sort[] sorts, String callbackURL,
			String contentType, String fieldNames)
		throws Exception;

	public Keyword postKeyword(Keyword keyword) throws Exception;

	public Response postKeywordBatch(String callbackURL, Object object)
		throws Exception;

	public Response postKeywordsPageExportBatch(
			String search,
			com.liferay.portal.kernel.search.filter.Filter filter,
			com.liferay.portal.kernel.search.Sort[] sorts, String callbackURL,
			String contentType, String fieldNames)
		throws Exception;

	public Keyword postSiteKeyword(Long siteId, Keyword keyword)
		throws Exception;

	public Response postSiteKeywordBatch(
			Long siteId, String callbackURL, Object object)
		throws Exception;

	public Response postSiteKeywordsPageExportBatch(
			Long siteId, String search,
			com.liferay.portal.kernel.search.filter.Filter filter,
			com.liferay.portal.kernel.search.Sort[] sorts, String callbackURL,
			String contentType, String fieldNames)
		throws Exception;

	public Keyword putAssetLibraryKeywordByExternalReferenceCode(
			Long assetLibraryId, String externalReferenceCode, Keyword keyword)
		throws Exception;

	public Page<com.liferay.portal.vulcan.permission.Permission>
			putAssetLibraryKeywordPermissionsPage(
				Long assetLibraryId,
				com.liferay.portal.vulcan.permission.Permission[] permissions)
		throws Exception;

	public Keyword putKeyword(Long keywordId, Keyword keyword) throws Exception;

	public Response putKeywordBatch(String callbackURL, Object object)
		throws Exception;

	public void putKeywordMerge(Long toKeywordId, Long[] fromKeywordIds)
		throws Exception;

	public void putKeywordSubscribe(Long keywordId) throws Exception;

	public void putKeywordUnsubscribe(Long keywordId) throws Exception;

	public Keyword putSiteKeywordByExternalReferenceCode(
			Long siteId, String externalReferenceCode, Keyword keyword)
		throws Exception;

	public Page<com.liferay.portal.vulcan.permission.Permission>
			putSiteKeywordPermissionsPage(
				Long siteId,
				com.liferay.portal.vulcan.permission.Permission[] permissions)
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

		public KeywordResource build();

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