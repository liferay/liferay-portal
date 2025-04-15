/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0;

import com.liferay.headless.delivery.dto.v1_0.KnowledgeBaseArticle;
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
public interface KnowledgeBaseArticleResource {

	public void deleteKnowledgeBaseArticle(Long knowledgeBaseArticleId)
		throws Exception;

	public Response deleteKnowledgeBaseArticleBatch(
			String callbackURL, Object object)
		throws Exception;

	public KnowledgeBaseArticle getKnowledgeBaseArticle(
			Long knowledgeBaseArticleId)
		throws Exception;

	public KnowledgeBaseArticle patchKnowledgeBaseArticle(
			Long knowledgeBaseArticleId,
			KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception;

	public KnowledgeBaseArticle putKnowledgeBaseArticle(
			Long knowledgeBaseArticleId,
			KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception;

	public Response putKnowledgeBaseArticleBatch(
			String callbackURL, Object object)
		throws Exception;

	public void deleteKnowledgeBaseArticleMyRating(Long knowledgeBaseArticleId)
		throws Exception;

	public Rating getKnowledgeBaseArticleMyRating(Long knowledgeBaseArticleId)
		throws Exception;

	public Rating postKnowledgeBaseArticleMyRating(
			Long knowledgeBaseArticleId, Rating rating)
		throws Exception;

	public Rating putKnowledgeBaseArticleMyRating(
			Long knowledgeBaseArticleId, Rating rating)
		throws Exception;

	public Page<com.liferay.portal.vulcan.permission.Permission>
			getKnowledgeBaseArticlePermissionsPage(
				Long knowledgeBaseArticleId, String roleNames)
		throws Exception;

	public Page<com.liferay.portal.vulcan.permission.Permission>
			putKnowledgeBaseArticlePermissionsPage(
				Long knowledgeBaseArticleId,
				com.liferay.portal.vulcan.permission.Permission[] permissions)
		throws Exception;

	public void putKnowledgeBaseArticleSubscribe(Long knowledgeBaseArticleId)
		throws Exception;

	public void putKnowledgeBaseArticleUnsubscribe(Long knowledgeBaseArticleId)
		throws Exception;

	public Page<KnowledgeBaseArticle>
			getKnowledgeBaseArticleKnowledgeBaseArticlesPage(
				Long parentKnowledgeBaseArticleId, Boolean flatten,
				String search,
				com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
				com.liferay.portal.kernel.search.filter.Filter filter,
				Pagination pagination,
				com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public KnowledgeBaseArticle postKnowledgeBaseArticleKnowledgeBaseArticle(
			Long parentKnowledgeBaseArticleId,
			KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception;

	public Page<KnowledgeBaseArticle>
			getKnowledgeBaseFolderKnowledgeBaseArticlesPage(
				Long knowledgeBaseFolderId, Boolean flatten, String search,
				com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
				com.liferay.portal.kernel.search.filter.Filter filter,
				Pagination pagination,
				com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public Response postKnowledgeBaseFolderKnowledgeBaseArticlesPageExportBatch(
			Long knowledgeBaseFolderId, String search,
			com.liferay.portal.kernel.search.filter.Filter filter,
			com.liferay.portal.kernel.search.Sort[] sorts, String callbackURL,
			String contentType, String fieldNames)
		throws Exception;

	public KnowledgeBaseArticle postKnowledgeBaseFolderKnowledgeBaseArticle(
			Long knowledgeBaseFolderId,
			KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception;

	public Response postKnowledgeBaseFolderKnowledgeBaseArticleBatch(
			Long knowledgeBaseFolderId, String callbackURL, Object object)
		throws Exception;

	public Page<KnowledgeBaseArticle> getSiteKnowledgeBaseArticlesPage(
			Long siteId, Boolean flatten, String search,
			com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public Response postSiteKnowledgeBaseArticlesPageExportBatch(
			Long siteId, String search,
			com.liferay.portal.kernel.search.filter.Filter filter,
			com.liferay.portal.kernel.search.Sort[] sorts, String callbackURL,
			String contentType, String fieldNames)
		throws Exception;

	public KnowledgeBaseArticle postSiteKnowledgeBaseArticle(
			Long siteId, KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception;

	public Response postSiteKnowledgeBaseArticleBatch(
			Long siteId, String callbackURL, Object object)
		throws Exception;

	public void deleteSiteKnowledgeBaseArticleByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception;

	public KnowledgeBaseArticle
			getSiteKnowledgeBaseArticleByExternalReferenceCode(
				Long siteId, String externalReferenceCode)
		throws Exception;

	public KnowledgeBaseArticle
			putSiteKnowledgeBaseArticleByExternalReferenceCode(
				Long siteId, String externalReferenceCode,
				KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception;

	public Page<com.liferay.portal.vulcan.permission.Permission>
			getSiteKnowledgeBaseArticlePermissionsPage(
				Long siteId, String roleNames)
		throws Exception;

	public Page<com.liferay.portal.vulcan.permission.Permission>
			putSiteKnowledgeBaseArticlePermissionsPage(
				Long siteId,
				com.liferay.portal.vulcan.permission.Permission[] permissions)
		throws Exception;

	public void putSiteKnowledgeBaseArticleSubscribe(Long siteId)
		throws Exception;

	public void putSiteKnowledgeBaseArticleUnsubscribe(Long siteId)
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

		public KnowledgeBaseArticleResource build();

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