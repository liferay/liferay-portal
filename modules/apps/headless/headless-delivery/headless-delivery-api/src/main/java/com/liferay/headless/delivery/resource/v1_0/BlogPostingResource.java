/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0;

import com.liferay.headless.delivery.dto.v1_0.BlogPosting;
import com.liferay.headless.delivery.dto.v1_0.Rating;
import com.liferay.portal.kernel.change.tracking.CTAware;
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

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.osgi.annotation.versioning.ProviderType;

/**
 * To access this resource, run:
 *
 *     curl -u your@email.com:yourpassword -D - http://localhost:8080/o/headless-delivery/v1.0
 *
 * @author Javier Gamarra
 * @generated
 */
@CTAware
@Generated("")
@ProviderType
public interface BlogPostingResource {

	public void deleteBlogPosting(Long blogPostingId) throws Exception;

	public Response deleteBlogPostingBatch(String callbackURL, Object object)
		throws Exception;

	public BlogPosting getBlogPosting(Long blogPostingId) throws Exception;

	public BlogPosting patchBlogPosting(
			Long blogPostingId, BlogPosting blogPosting)
		throws Exception;

	public BlogPosting putBlogPosting(
			Long blogPostingId, BlogPosting blogPosting)
		throws Exception;

	public Response putBlogPostingBatch(String callbackURL, Object object)
		throws Exception;

	public void deleteBlogPostingMyRating(Long blogPostingId) throws Exception;

	public Rating getBlogPostingMyRating(Long blogPostingId) throws Exception;

	public Rating postBlogPostingMyRating(Long blogPostingId, Rating rating)
		throws Exception;

	public Rating putBlogPostingMyRating(Long blogPostingId, Rating rating)
		throws Exception;

	public Page<com.liferay.portal.vulcan.permission.Permission>
			getBlogPostingPermissionsPage(Long blogPostingId, String roleNames)
		throws Exception;

	public Page<com.liferay.portal.vulcan.permission.Permission>
			putBlogPostingPermissionsPage(
				Long blogPostingId,
				com.liferay.portal.vulcan.permission.Permission[] permissions)
		throws Exception;

	public String getBlogPostingRenderedContentByDisplayPageDisplayPageKey(
			Long blogPostingId, String displayPageKey)
		throws Exception;

	public Page<BlogPosting> getSiteBlogPostingsPage(
			Long siteId, String search,
			com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public Response postSiteBlogPostingsPageExportBatch(
			Long siteId, String search,
			com.liferay.portal.kernel.search.filter.Filter filter,
			com.liferay.portal.kernel.search.Sort[] sorts, String callbackURL,
			String contentType, String fieldNames)
		throws Exception;

	public BlogPosting postSiteBlogPosting(Long siteId, BlogPosting blogPosting)
		throws Exception;

	public Response postSiteBlogPostingBatch(
			Long siteId, String callbackURL, Object object)
		throws Exception;

	public void deleteSiteBlogPostingByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception;

	public BlogPosting getSiteBlogPostingByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception;

	public BlogPosting putSiteBlogPostingByExternalReferenceCode(
			Long siteId, String externalReferenceCode, BlogPosting blogPosting)
		throws Exception;

	public Page<com.liferay.portal.vulcan.permission.Permission>
			getSiteBlogPostingPermissionsPage(Long siteId, String roleNames)
		throws Exception;

	public Page<com.liferay.portal.vulcan.permission.Permission>
			putSiteBlogPostingPermissionsPage(
				Long siteId,
				com.liferay.portal.vulcan.permission.Permission[] permissions)
		throws Exception;

	public void putSiteBlogPostingSubscribe(Long siteId) throws Exception;

	public void putSiteBlogPostingUnsubscribe(Long siteId) throws Exception;

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

		public BlogPostingResource build();

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