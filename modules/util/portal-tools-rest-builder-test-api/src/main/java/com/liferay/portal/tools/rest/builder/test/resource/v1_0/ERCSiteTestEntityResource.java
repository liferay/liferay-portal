/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.resource.v1_0;

import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.odata.filter.ExpressionConvert;
import com.liferay.portal.odata.filter.FilterParserProvider;
import com.liferay.portal.odata.sort.SortParserProvider;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.ERCSiteTestEntity;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.Filter;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.Sort;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.pagination.Page;

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
 *     curl -u your@email.com:yourpassword -D - http://localhost:8080/o/test/v1.0
 *
 * @author Alejandro Tardín
 * @generated
 */
@CTAware
@Generated("")
@ProviderType
public interface ERCSiteTestEntityResource {

	public void deleteSiteERCSiteTestEntity(
			String siteExternalReferenceCode,
			String ercSiteTestEntityExternalReferenceCode)
		throws Exception;

	public Page<ERCSiteTestEntity> getSiteERCSiteTestEntitiesPage(
			String siteExternalReferenceCode)
		throws Exception;

	public ERCSiteTestEntity getSiteERCSiteTestEntity(
			String siteExternalReferenceCode,
			String ercSiteTestEntityExternalReferenceCode)
		throws Exception;

	public Page<com.liferay.portal.vulcan.permission.Permission>
			getSiteERCSiteTestEntityPermissionsPage(
				String siteExternalReferenceCode,
				String ercSiteTestEntityExternalReferenceCode, String roleNames)
		throws Exception;

	public Response postSiteERCSiteTestEntitiesPageExportBatch(
			String siteExternalReferenceCode, String callbackURL,
			String contentType, String fieldNames)
		throws Exception;

	public ERCSiteTestEntity postSiteERCSiteTestEntity(
			String siteExternalReferenceCode,
			ERCSiteTestEntity ercSiteTestEntity)
		throws Exception;

	public Response postSiteERCSiteTestEntityBatch(
			String siteExternalReferenceCode, String callbackURL, Object object)
		throws Exception;

	public ERCSiteTestEntity putSiteERCSiteTestEntity(
			String siteExternalReferenceCode,
			String ercSiteTestEntityExternalReferenceCode,
			ERCSiteTestEntity ercSiteTestEntity)
		throws Exception;

	public Page<com.liferay.portal.vulcan.permission.Permission>
			putSiteERCSiteTestEntityPermissionsPage(
				String siteExternalReferenceCode,
				String ercSiteTestEntityExternalReferenceCode,
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

		public ERCSiteTestEntityResource build();

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