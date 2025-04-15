/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0;

import com.liferay.headless.delivery.dto.v1_0.Rating;
import com.liferay.headless.delivery.dto.v1_0.StructuredContent;
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
public interface StructuredContentResource {

	public Page<StructuredContent> getAssetLibraryStructuredContentsPage(
			Long assetLibraryId, Boolean flatten, String search,
			com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public Response postAssetLibraryStructuredContentsPageExportBatch(
			Long assetLibraryId, String search,
			com.liferay.portal.kernel.search.filter.Filter filter,
			com.liferay.portal.kernel.search.Sort[] sorts, String callbackURL,
			String contentType, String fieldNames)
		throws Exception;

	public StructuredContent postAssetLibraryStructuredContent(
			Long assetLibraryId, StructuredContent structuredContent)
		throws Exception;

	public Response postAssetLibraryStructuredContentBatch(
			Long assetLibraryId, String callbackURL, Object object)
		throws Exception;

	public void deleteAssetLibraryStructuredContentByExternalReferenceCode(
			Long assetLibraryId, String externalReferenceCode)
		throws Exception;

	public StructuredContent
			getAssetLibraryStructuredContentByExternalReferenceCode(
				Long assetLibraryId, String externalReferenceCode)
		throws Exception;

	public StructuredContent
			putAssetLibraryStructuredContentByExternalReferenceCode(
				Long assetLibraryId, String externalReferenceCode,
				StructuredContent structuredContent)
		throws Exception;

	public Page<com.liferay.portal.vulcan.permission.Permission>
			getAssetLibraryStructuredContentPermissionsPage(
				Long assetLibraryId, String roleNames)
		throws Exception;

	public Page<com.liferay.portal.vulcan.permission.Permission>
			putAssetLibraryStructuredContentPermissionsPage(
				Long assetLibraryId,
				com.liferay.portal.vulcan.permission.Permission[] permissions)
		throws Exception;

	public Page<StructuredContent> getContentStructureStructuredContentsPage(
			Long contentStructureId, String search,
			com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public Response postContentStructureStructuredContentsPageExportBatch(
			Long contentStructureId, String search,
			com.liferay.portal.kernel.search.filter.Filter filter,
			com.liferay.portal.kernel.search.Sort[] sorts, String callbackURL,
			String contentType, String fieldNames)
		throws Exception;

	public Page<StructuredContent> getSiteStructuredContentsPage(
			Long siteId, Boolean flatten, String search,
			com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public Response postSiteStructuredContentsPageExportBatch(
			Long siteId, String search,
			com.liferay.portal.kernel.search.filter.Filter filter,
			com.liferay.portal.kernel.search.Sort[] sorts, String callbackURL,
			String contentType, String fieldNames)
		throws Exception;

	public StructuredContent postSiteStructuredContent(
			Long siteId, StructuredContent structuredContent)
		throws Exception;

	public Response postSiteStructuredContentBatch(
			Long siteId, String callbackURL, Object object)
		throws Exception;

	public void deleteSiteStructuredContentByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception;

	public StructuredContent getSiteStructuredContentByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception;

	public StructuredContent putSiteStructuredContentByExternalReferenceCode(
			Long siteId, String externalReferenceCode,
			StructuredContent structuredContent)
		throws Exception;

	public StructuredContent getSiteStructuredContentByKey(
			Long siteId, String key)
		throws Exception;

	public StructuredContent getSiteStructuredContentByUuid(
			Long siteId, String uuid)
		throws Exception;

	public Page<com.liferay.portal.vulcan.permission.Permission>
			getSiteStructuredContentPermissionsPage(
				Long siteId, String roleNames)
		throws Exception;

	public Page<com.liferay.portal.vulcan.permission.Permission>
			putSiteStructuredContentPermissionsPage(
				Long siteId,
				com.liferay.portal.vulcan.permission.Permission[] permissions)
		throws Exception;

	public Page<StructuredContent>
			getStructuredContentFolderStructuredContentsPage(
				Long structuredContentFolderId, Boolean flatten, String search,
				com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
				com.liferay.portal.kernel.search.filter.Filter filter,
				Pagination pagination,
				com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public Response
			postStructuredContentFolderStructuredContentsPageExportBatch(
				Long structuredContentFolderId, String search,
				com.liferay.portal.kernel.search.filter.Filter filter,
				com.liferay.portal.kernel.search.Sort[] sorts,
				String callbackURL, String contentType, String fieldNames)
		throws Exception;

	public StructuredContent postStructuredContentFolderStructuredContent(
			Long structuredContentFolderId, StructuredContent structuredContent)
		throws Exception;

	public Response postStructuredContentFolderStructuredContentBatch(
			Long structuredContentFolderId, String callbackURL, Object object)
		throws Exception;

	public void deleteStructuredContent(Long structuredContentId)
		throws Exception;

	public Response deleteStructuredContentBatch(
			String callbackURL, Object object)
		throws Exception;

	public StructuredContent getStructuredContent(Long structuredContentId)
		throws Exception;

	public StructuredContent patchStructuredContent(
			Long structuredContentId, StructuredContent structuredContent)
		throws Exception;

	public StructuredContent putStructuredContent(
			Long structuredContentId, StructuredContent structuredContent)
		throws Exception;

	public Response putStructuredContentBatch(String callbackURL, Object object)
		throws Exception;

	public void deleteStructuredContentMyRating(Long structuredContentId)
		throws Exception;

	public Rating getStructuredContentMyRating(Long structuredContentId)
		throws Exception;

	public Rating postStructuredContentMyRating(
			Long structuredContentId, Rating rating)
		throws Exception;

	public Rating putStructuredContentMyRating(
			Long structuredContentId, Rating rating)
		throws Exception;

	public Page<com.liferay.portal.vulcan.permission.Permission>
			getStructuredContentPermissionsPage(
				Long structuredContentId, String roleNames)
		throws Exception;

	public Page<com.liferay.portal.vulcan.permission.Permission>
			putStructuredContentPermissionsPage(
				Long structuredContentId,
				com.liferay.portal.vulcan.permission.Permission[] permissions)
		throws Exception;

	public String
			getStructuredContentRenderedContentByDisplayPageDisplayPageKey(
				Long structuredContentId, String displayPageKey)
		throws Exception;

	public String getStructuredContentRenderedContentContentTemplate(
			Long structuredContentId, String contentTemplateId)
		throws Exception;

	public void putStructuredContentSubscribe(Long structuredContentId)
		throws Exception;

	public void putStructuredContentUnsubscribe(Long structuredContentId)
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

		public StructuredContentResource build();

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