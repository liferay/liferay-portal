/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.resource.v1_0;

import com.liferay.headless.admin.user.dto.v1_0.Role;
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
 *     curl -u your@email.com:yourpassword -D - http://localhost:8080/o/headless-admin-user/v1.0
 *
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@ProviderType
public interface RoleResource {

	public Page<Role> getRolesPage(
			String search, Integer[] types,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination)
		throws Exception;

	public Response postRolesPageExportBatch(
			String search, Integer[] types,
			com.liferay.portal.kernel.search.filter.Filter filter,
			String callbackURL, String contentType, String fieldNames)
		throws Exception;

	public Role postRole(Role role) throws Exception;

	public Response postRoleBatch(String callbackURL, Object object)
		throws Exception;

	public void deleteRoleByExternalReferenceCode(String externalReferenceCode)
		throws Exception;

	public Role getRoleByExternalReferenceCode(String externalReferenceCode)
		throws Exception;

	public Role patchRoleByExternalReferenceCode(
			String externalReferenceCode, Role role)
		throws Exception;

	public Role putRoleByExternalReferenceCode(
			String externalReferenceCode, Role role)
		throws Exception;

	public void deleteRoleByExternalReferenceCodeUserAccountAssociation(
			String externalReferenceCode, Long userAccountId)
		throws Exception;

	public void postRoleByExternalReferenceCodeUserAccountAssociation(
			String externalReferenceCode, Long userAccountId)
		throws Exception;

	public void
			deleteOrganizationRoleByExternalReferenceCodeUserAccountAssociation(
				String externalReferenceCode, Long userAccountId,
				Long organizationId)
		throws Exception;

	public void
			postOrganizationRoleByExternalReferenceCodeUserAccountAssociation(
				String externalReferenceCode, Long userAccountId,
				Long organizationId)
		throws Exception;

	public void deleteSiteRoleByExternalReferenceCodeUserAccountAssociation(
			String externalReferenceCode, Long userAccountId, Long siteId)
		throws Exception;

	public void postSiteRoleByExternalReferenceCodeUserAccountAssociation(
			String externalReferenceCode, Long userAccountId, Long siteId)
		throws Exception;

	public void deleteRole(Long roleId) throws Exception;

	public Response deleteRoleBatch(String callbackURL, Object object)
		throws Exception;

	public Role getRole(Long roleId) throws Exception;

	public Role patchRole(Long roleId, Role role) throws Exception;

	public Role putRole(Long roleId, Role role) throws Exception;

	public Response putRoleBatch(String callbackURL, Object object)
		throws Exception;

	public void deleteRoleUserAccountAssociation(
			Long roleId, Long userAccountId)
		throws Exception;

	public void postRoleUserAccountAssociation(Long roleId, Long userAccountId)
		throws Exception;

	public void deleteOrganizationRoleUserAccountAssociation(
			Long roleId, Long userAccountId, Long organizationId)
		throws Exception;

	public void postOrganizationRoleUserAccountAssociation(
			Long roleId, Long userAccountId, Long organizationId)
		throws Exception;

	public void deleteSiteRoleUserAccountAssociation(
			Long roleId, Long userAccountId, Long siteId)
		throws Exception;

	public void postSiteRoleUserAccountAssociation(
			Long roleId, Long userAccountId, Long siteId)
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

		public RoleResource build();

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