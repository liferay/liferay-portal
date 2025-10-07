/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.resource.v1_0;

import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.dto.v1_0.ValidationRequest;
import com.liferay.object.rest.dto.v1_0.ValidationResponse;
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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@ProviderType
public interface ObjectEntryResource {

	public void deleteByExternalReferenceCode(String externalReferenceCode)
		throws Exception;

	public void deleteByExternalReferenceCodeByVersion(
			String externalReferenceCode, Integer version)
		throws Exception;

	public void deleteObjectEntry(Long objectEntryId) throws Exception;

	public Response deleteObjectEntryBatch(String callbackURL, Object object)
		throws Exception;

	public void deleteObjectEntryByVersion(Long objectEntryId, Integer version)
		throws Exception;

	public void deleteScopeScopeKeyByExternalReferenceCode(
			String scopeKey, String externalReferenceCode)
		throws Exception;

	public void deleteScopeScopeKeyByExternalReferenceCodeByVersion(
			String scopeKey, String externalReferenceCode, Integer version)
		throws Exception;

	public ObjectEntry getApprovedByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception;

	public Page<ObjectEntry> getApprovedPage(
			String search,
			com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public ObjectEntry getByExternalReferenceCode(String externalReferenceCode)
		throws Exception;

	public ObjectEntry getByExternalReferenceCodeByVersion(
			String externalReferenceCode, Integer version)
		throws Exception;

	public Page<ObjectEntry> getByExternalReferenceCodeVersionsPage(
			String externalReferenceCode, Pagination pagination)
		throws Exception;

	public Page<ObjectEntry> getObjectEntriesPage(
			String search,
			com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public Page<ObjectEntry> getObjectEntriesVersionsPage(
			Long objectEntryId, Pagination pagination)
		throws Exception;

	public ObjectEntry getObjectEntry(Long objectEntryId) throws Exception;

	public ObjectEntry getObjectEntryByVersion(
			Long objectEntryId, Integer version)
		throws Exception;

	public Page<com.liferay.portal.vulcan.permission.Permission>
			getObjectEntryPermissionsPage(Long objectEntryId, String roleNames)
		throws Exception;

	public ObjectEntry getScopeScopeKeyApprovedByExternalReferenceCode(
			String scopeKey, String externalReferenceCode)
		throws Exception;

	public Page<ObjectEntry> getScopeScopeKeyApprovedPage(
			String scopeKey, String search,
			com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public ObjectEntry getScopeScopeKeyByExternalReferenceCode(
			String scopeKey, String externalReferenceCode)
		throws Exception;

	public ObjectEntry getScopeScopeKeyByExternalReferenceCodeByVersion(
			String scopeKey, String externalReferenceCode, Integer version)
		throws Exception;

	public Page<ObjectEntry>
			getScopeScopeKeyByExternalReferenceCodeVersionsPage(
				String scopeKey, String externalReferenceCode,
				Pagination pagination)
		throws Exception;

	public Page<ObjectEntry> getScopeScopeKeyPage(
			String scopeKey, String search,
			com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	public ObjectEntry patchByExternalReferenceCode(
			String externalReferenceCode, ObjectEntry objectEntry)
		throws Exception;

	public ObjectEntry patchObjectEntry(
			Long objectEntryId, ObjectEntry objectEntry)
		throws Exception;

	public ObjectEntry patchScopeScopeKeyByExternalReferenceCode(
			String scopeKey, String externalReferenceCode,
			ObjectEntry objectEntry)
		throws Exception;

	public ObjectEntry postByExternalReferenceCodeByVersionCopy(
			String externalReferenceCode, Integer version)
		throws Exception;

	public ObjectEntry postByExternalReferenceCodeByVersionExpire(
			String externalReferenceCode, Integer version)
		throws Exception;

	public void postByExternalReferenceCodeSubscribe(
			String externalReferenceCode)
		throws Exception;

	public void postByExternalReferenceCodeUnsubscribe(
			String externalReferenceCode)
		throws Exception;

	public Response postObjectEntriesPageExportBatch(
			String search,
			com.liferay.portal.kernel.search.filter.Filter filter,
			com.liferay.portal.kernel.search.Sort[] sorts, String callbackURL,
			String contentType, String fieldNames)
		throws Exception;

	public ObjectEntry postObjectEntry(ObjectEntry objectEntry)
		throws Exception;

	public Response postObjectEntryBatch(String callbackURL, Object object)
		throws Exception;

	public ObjectEntry postObjectEntryByVersionCopy(
			Long objectEntryId, Integer version)
		throws Exception;

	public ObjectEntry postObjectEntryByVersionExpire(
			Long objectEntryId, Integer version)
		throws Exception;

	public ObjectEntry postObjectEntryExpire(Long objectEntryId)
		throws Exception;

	public ObjectEntry postScopeScopeKey(
			String scopeKey, ObjectEntry objectEntry)
		throws Exception;

	public ObjectEntry postScopeScopeKeyByExternalReferenceCodeByVersionCopy(
			String scopeKey, String externalReferenceCode, Integer version)
		throws Exception;

	public ObjectEntry postScopeScopeKeyByExternalReferenceCodeByVersionExpire(
			String scopeKey, String externalReferenceCode, Integer version)
		throws Exception;

	public ObjectEntry postScopeScopeKeyByExternalReferenceCodeExpire(
			String scopeKey, String externalReferenceCode)
		throws Exception;

	public void postScopeScopeKeyByExternalReferenceCodeSubscribe(
			String scopeKey, String externalReferenceCode)
		throws Exception;

	public void postScopeScopeKeyByExternalReferenceCodeUnsubscribe(
			String scopeKey, String externalReferenceCode)
		throws Exception;

	public ValidationResponse postScopeScopeKeyValidate(
			String scopeKey, ValidationRequest validationRequest)
		throws Exception;

	public ValidationResponse postValidate(ValidationRequest validationRequest)
		throws Exception;

	public ObjectEntry putByExternalReferenceCode(
			String externalReferenceCode, ObjectEntry objectEntry)
		throws Exception;

	public ObjectEntry putByExternalReferenceCodeByVersionRestore(
			String externalReferenceCode, Integer version)
		throws Exception;

	public void putByExternalReferenceCodeObjectActionObjectActionName(
			String externalReferenceCode, String objectActionName)
		throws Exception;

	public ObjectEntry putByExternalReferenceCodeRestore(
			String externalReferenceCode)
		throws Exception;

	public ObjectEntry putObjectEntry(
			Long objectEntryId, ObjectEntry objectEntry)
		throws Exception;

	public Response putObjectEntryBatch(String callbackURL, Object object)
		throws Exception;

	public ObjectEntry putObjectEntryByVersionRestore(
			Long objectEntryId, Integer version)
		throws Exception;

	public void putObjectEntryObjectActionObjectActionName(
			Long objectEntryId, String objectActionName)
		throws Exception;

	public Page<com.liferay.portal.vulcan.permission.Permission>
			putObjectEntryPermissionsPage(
				Long objectEntryId,
				com.liferay.portal.vulcan.permission.Permission[] permissions)
		throws Exception;

	public ObjectEntry putScopeScopeKeyByExternalReferenceCode(
			String scopeKey, String externalReferenceCode,
			ObjectEntry objectEntry)
		throws Exception;

	public ObjectEntry putScopeScopeKeyByExternalReferenceCodeByVersionRestore(
			String scopeKey, String externalReferenceCode, Integer version)
		throws Exception;

	public void
			putScopeScopeKeyByExternalReferenceCodeObjectActionObjectActionName(
				String scopeKey, String externalReferenceCode,
				String objectActionName)
		throws Exception;

	public ObjectEntry putScopeScopeKeyByExternalReferenceCodeRestore(
			String scopeKey, String externalReferenceCode)
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

		public ObjectEntryResource build();

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