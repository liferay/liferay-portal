/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.resource.v1_0;

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
public interface CollaboratorResource {

	public void deleteObjectEntryCollaboratorByEmailAddress(
			Long objectEntryId, String emailAddress)
		throws Exception;

	public void deleteObjectEntryCollaboratorByTypeCollaborator(
			Long objectEntryId, String type, Long collaboratorId)
		throws Exception;

	public void
			deleteScopeScopeKeyByExternalReferenceCodeCollaboratorByEmailAddress(
				String scopeKey, String externalReferenceCode,
				String emailAddress)
		throws Exception;

	public void
			deleteScopeScopeKeyByExternalReferenceCodeCollaboratorByTypeCollaborator(
				String scopeKey, String externalReferenceCode, String type,
				Long collaboratorId)
		throws Exception;

	public com.liferay.headless.object.dto.v1_0.Collaborator
			getObjectEntryCollaboratorByEmailAddress(
				Long objectEntryId, String emailAddress)
		throws Exception;

	public com.liferay.headless.object.dto.v1_0.Collaborator
			getObjectEntryCollaboratorByTypeCollaborator(
				Long objectEntryId, String type, Long collaboratorId)
		throws Exception;

	public Page<com.liferay.headless.object.dto.v1_0.Collaborator>
			getObjectEntryCollaboratorsPage(
				Long objectEntryId, Pagination pagination)
		throws Exception;

	public com.liferay.headless.object.dto.v1_0.Collaborator
			getScopeScopeKeyByExternalReferenceCodeCollaboratorByEmailAddress(
				String scopeKey, String externalReferenceCode,
				String emailAddress)
		throws Exception;

	public com.liferay.headless.object.dto.v1_0.Collaborator
			getScopeScopeKeyByExternalReferenceCodeCollaboratorByTypeCollaborator(
				String scopeKey, String externalReferenceCode, String type,
				Long collaboratorId)
		throws Exception;

	public Page<com.liferay.headless.object.dto.v1_0.Collaborator>
			getScopeScopeKeyByExternalReferenceCodeCollaboratorsPage(
				String scopeKey, String externalReferenceCode,
				Pagination pagination)
		throws Exception;

	public Page<com.liferay.headless.object.dto.v1_0.Collaborator>
			postObjectEntryCollaboratorsPage(
				Long objectEntryId,
				com.liferay.headless.object.dto.v1_0.Collaborator[]
					collaborators)
		throws Exception;

	public Response postObjectEntryCollaboratorsPageExportBatch(
			Long objectEntryId, String callbackURL, String contentType,
			String fieldNames)
		throws Exception;

	public Page<com.liferay.headless.object.dto.v1_0.Collaborator>
			postScopeScopeKeyByExternalReferenceCodeCollaboratorsPage(
				String scopeKey, String externalReferenceCode,
				com.liferay.headless.object.dto.v1_0.Collaborator[]
					collaborators)
		throws Exception;

	public com.liferay.headless.object.dto.v1_0.Collaborator
			putObjectEntryCollaboratorByEmailAddress(
				Long objectEntryId, String emailAddress,
				com.liferay.headless.object.dto.v1_0.Collaborator collaborator)
		throws Exception;

	public com.liferay.headless.object.dto.v1_0.Collaborator
			putObjectEntryCollaboratorByTypeCollaborator(
				Long objectEntryId, String type, Long collaboratorId,
				com.liferay.headless.object.dto.v1_0.Collaborator collaborator)
		throws Exception;

	public com.liferay.headless.object.dto.v1_0.Collaborator
			putScopeScopeKeyByExternalReferenceCodeCollaboratorByEmailAddress(
				String scopeKey, String externalReferenceCode,
				String emailAddress,
				com.liferay.headless.object.dto.v1_0.Collaborator collaborator)
		throws Exception;

	public com.liferay.headless.object.dto.v1_0.Collaborator
			putScopeScopeKeyByExternalReferenceCodeCollaboratorByTypeCollaborator(
				String scopeKey, String externalReferenceCode, String type,
				Long collaboratorId,
				com.liferay.headless.object.dto.v1_0.Collaborator collaborator)
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

		public CollaboratorResource build();

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
// LIFERAY-REST-BUILDER-HASH:1312670604