/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.object.internal.graphql.mutation.v1_0;

import com.liferay.headless.object.dto.v1_0.Collaborator;
import com.liferay.headless.object.dto.v1_0.ObjectEntryFolder;
import com.liferay.headless.object.resource.v1_0.CollaboratorResource;
import com.liferay.headless.object.resource.v1_0.ObjectEntryFolderResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.pagination.Page;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Alicia García
 * @generated
 */
@Generated("")
public class Mutation {

	public static void setCollaboratorResourceComponentServiceObjects(
		ComponentServiceObjects<CollaboratorResource>
			collaboratorResourceComponentServiceObjects) {

		_collaboratorResourceComponentServiceObjects =
			collaboratorResourceComponentServiceObjects;
	}

	public static void setObjectEntryFolderResourceComponentServiceObjects(
		ComponentServiceObjects<ObjectEntryFolderResource>
			objectEntryFolderResourceComponentServiceObjects) {

		_objectEntryFolderResourceComponentServiceObjects =
			objectEntryFolderResourceComponentServiceObjects;
	}

	@GraphQLField(
		description = "Deletes the collaborator for an object entry folder and returns a 204 if the operation succeeds."
	)
	public boolean deleteObjectEntryFolderCollaboratorByTypeCollaborator(
			@GraphQLName("objectEntryFolderId") Long objectEntryFolderId,
			@GraphQLName("type") String type,
			@GraphQLName("collaboratorId") Long collaboratorId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_collaboratorResourceComponentServiceObjects,
			this::_populateResourceContext,
			collaboratorResource ->
				collaboratorResource.
					deleteObjectEntryFolderCollaboratorByTypeCollaborator(
						objectEntryFolderId, type, collaboratorId));

		return true;
	}

	@GraphQLField(
		description = "Deletes the collaborator for an object entry folder and returns a 204 if the operation succeeds."
	)
	public boolean
			deleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator(
				@GraphQLName("scopeKey") String scopeKey,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("type") String type,
				@GraphQLName("collaboratorId") Long collaboratorId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_collaboratorResourceComponentServiceObjects,
			this::_populateResourceContext,
			collaboratorResource ->
				collaboratorResource.
					deleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator(
						scopeKey, externalReferenceCode, type, collaboratorId));

		return true;
	}

	@GraphQLField(
		description = "Add or update all the collaborators received in the request. Delete existing collaborators that are not included in the request. Send a notification for the new collaborators and those whose permissions are different."
	)
	public java.util.Collection<Collaborator>
			createObjectEntryFolderCollaboratorsPage(
				@GraphQLName("objectEntryFolderId") Long objectEntryFolderId,
				@GraphQLName("collaborators") Collaborator[] collaborators)
		throws Exception {

		return _applyComponentServiceObjects(
			_collaboratorResourceComponentServiceObjects,
			this::_populateResourceContext,
			collaboratorResource -> {
				Page paginationPage =
					collaboratorResource.postObjectEntryFolderCollaboratorsPage(
						objectEntryFolderId, collaborators);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public Response createObjectEntryFolderCollaboratorsPageExportBatch(
			@GraphQLName("objectEntryFolderId") Long objectEntryFolderId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_collaboratorResourceComponentServiceObjects,
			this::_populateResourceContext,
			collaboratorResource ->
				collaboratorResource.
					postObjectEntryFolderCollaboratorsPageExportBatch(
						objectEntryFolderId, callbackURL, contentType,
						fieldNames));
	}

	@GraphQLField(
		description = "Add or update all the collaborators received in the request. Delete existing collaborators that are not included in the request. Send a notification for the new collaborators and those whose permissions are different."
	)
	public java.util.Collection<Collaborator>
			createScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage(
				@GraphQLName("scopeKey") String scopeKey,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("collaborators") Collaborator[] collaborators)
		throws Exception {

		return _applyComponentServiceObjects(
			_collaboratorResourceComponentServiceObjects,
			this::_populateResourceContext,
			collaboratorResource -> {
				Page paginationPage =
					collaboratorResource.
						postScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage(
							scopeKey, externalReferenceCode, collaborators);

				return paginationPage.getItems();
			});
	}

	@GraphQLField(
		description = "Add or update a collaborator received in the request."
	)
	public Collaborator updateObjectEntryFolderCollaboratorByTypeCollaborator(
			@GraphQLName("objectEntryFolderId") Long objectEntryFolderId,
			@GraphQLName("type") String type,
			@GraphQLName("collaboratorId") Long collaboratorId,
			@GraphQLName("collaborator") Collaborator collaborator)
		throws Exception {

		return _applyComponentServiceObjects(
			_collaboratorResourceComponentServiceObjects,
			this::_populateResourceContext,
			collaboratorResource ->
				collaboratorResource.
					putObjectEntryFolderCollaboratorByTypeCollaborator(
						objectEntryFolderId, type, collaboratorId,
						collaborator));
	}

	@GraphQLField(
		description = "Add or update a collaborator received in the request."
	)
	public Collaborator
			updateScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator(
				@GraphQLName("scopeKey") String scopeKey,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("type") String type,
				@GraphQLName("collaboratorId") Long collaboratorId,
				@GraphQLName("collaborator") Collaborator collaborator)
		throws Exception {

		return _applyComponentServiceObjects(
			_collaboratorResourceComponentServiceObjects,
			this::_populateResourceContext,
			collaboratorResource ->
				collaboratorResource.
					putScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator(
						scopeKey, externalReferenceCode, type, collaboratorId,
						collaborator));
	}

	@GraphQLField(
		description = "Deletes the object entry folder and returns a 204 if the operation succeeds."
	)
	public boolean deleteObjectEntryFolder(
			@GraphQLName("objectEntryFolderId") Long objectEntryFolderId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_objectEntryFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryFolderResource ->
				objectEntryFolderResource.deleteObjectEntryFolder(
					objectEntryFolderId));

		return true;
	}

	@GraphQLField
	public Response deleteObjectEntryFolderBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryFolderResource ->
				objectEntryFolderResource.deleteObjectEntryFolderBatch(
					callbackURL, object));
	}

	@GraphQLField
	public boolean deleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
			@GraphQLName("scopeKey") String scopeKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_objectEntryFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryFolderResource ->
				objectEntryFolderResource.
					deleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
						scopeKey, externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates only the fields received in the request body, leaving any other fields untouched."
	)
	public ObjectEntryFolder patchObjectEntryFolder(
			@GraphQLName("objectEntryFolderId") Long objectEntryFolderId,
			@GraphQLName("objectEntryFolder") ObjectEntryFolder
				objectEntryFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryFolderResource ->
				objectEntryFolderResource.patchObjectEntryFolder(
					objectEntryFolderId, objectEntryFolder));
	}

	@GraphQLField
	public ObjectEntryFolder
			patchScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
				@GraphQLName("scopeKey") String scopeKey,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("objectEntryFolder") ObjectEntryFolder
					objectEntryFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryFolderResource ->
				objectEntryFolderResource.
					patchScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
						scopeKey, externalReferenceCode, objectEntryFolder));
	}

	@GraphQLField
	public ObjectEntryFolder createScopeScopeKeyObjectEntryFolder(
			@GraphQLName("scopeKey") String scopeKey,
			@GraphQLName("objectEntryFolder") ObjectEntryFolder
				objectEntryFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryFolderResource ->
				objectEntryFolderResource.postScopeScopeKeyObjectEntryFolder(
					scopeKey, objectEntryFolder));
	}

	@GraphQLField
	public ObjectEntryFolder
			createScopeScopeKeyObjectEntryFolderByExternalReferenceCodeRestore(
				@GraphQLName("scopeKey") String scopeKey,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryFolderResource ->
				objectEntryFolderResource.
					postScopeScopeKeyObjectEntryFolderByExternalReferenceCodeRestore(
						scopeKey, externalReferenceCode));
	}

	@GraphQLField
	public boolean
			createScopeScopeKeyObjectEntryFolderByExternalReferenceCodeSubscribe(
				@GraphQLName("scopeKey") String scopeKey,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_objectEntryFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryFolderResource ->
				objectEntryFolderResource.
					postScopeScopeKeyObjectEntryFolderByExternalReferenceCodeSubscribe(
						scopeKey, externalReferenceCode));

		return true;
	}

	@GraphQLField
	public boolean
			createScopeScopeKeyObjectEntryFolderByExternalReferenceCodeUnsubscribe(
				@GraphQLName("scopeKey") String scopeKey,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_objectEntryFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryFolderResource ->
				objectEntryFolderResource.
					postScopeScopeKeyObjectEntryFolderByExternalReferenceCodeUnsubscribe(
						scopeKey, externalReferenceCode));

		return true;
	}

	@GraphQLField
	public ObjectEntryFolder updateObjectEntryFolder(
			@GraphQLName("objectEntryFolderId") Long objectEntryFolderId,
			@GraphQLName("objectEntryFolder") ObjectEntryFolder
				objectEntryFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryFolderResource ->
				objectEntryFolderResource.putObjectEntryFolder(
					objectEntryFolderId, objectEntryFolder));
	}

	@GraphQLField
	public Response updateObjectEntryFolderBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryFolderResource ->
				objectEntryFolderResource.putObjectEntryFolderBatch(
					callbackURL, object));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateObjectEntryFolderPermissionsPage(
				@GraphQLName("objectEntryFolderId") Long objectEntryFolderId,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryFolderResource -> {
				Page paginationPage =
					objectEntryFolderResource.
						putObjectEntryFolderPermissionsPage(
							objectEntryFolderId, permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public ObjectEntryFolder
			updateScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
				@GraphQLName("scopeKey") String scopeKey,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("objectEntryFolder") ObjectEntryFolder
					objectEntryFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryFolderResource ->
				objectEntryFolderResource.
					putScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
						scopeKey, externalReferenceCode, objectEntryFolder));
	}

	private <T, R, E1 extends Throwable, E2 extends Throwable> R
			_applyComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeFunction<T, R, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			return unsafeFunction.apply(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private <T, E1 extends Throwable, E2 extends Throwable> void
			_applyVoidComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeConsumer<T, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			unsafeFunction.accept(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private void _populateResourceContext(
			CollaboratorResource collaboratorResource)
		throws Exception {

		collaboratorResource.setContextAcceptLanguage(_acceptLanguage);
		collaboratorResource.setContextCompany(_company);
		collaboratorResource.setContextHttpServletRequest(_httpServletRequest);
		collaboratorResource.setContextHttpServletResponse(
			_httpServletResponse);
		collaboratorResource.setContextUriInfo(_uriInfo);
		collaboratorResource.setContextUser(_user);
		collaboratorResource.setGroupLocalService(_groupLocalService);
		collaboratorResource.setRoleLocalService(_roleLocalService);

		collaboratorResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		collaboratorResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			ObjectEntryFolderResource objectEntryFolderResource)
		throws Exception {

		objectEntryFolderResource.setContextAcceptLanguage(_acceptLanguage);
		objectEntryFolderResource.setContextCompany(_company);
		objectEntryFolderResource.setContextHttpServletRequest(
			_httpServletRequest);
		objectEntryFolderResource.setContextHttpServletResponse(
			_httpServletResponse);
		objectEntryFolderResource.setContextUriInfo(_uriInfo);
		objectEntryFolderResource.setContextUser(_user);
		objectEntryFolderResource.setGroupLocalService(_groupLocalService);
		objectEntryFolderResource.setRoleLocalService(_roleLocalService);

		objectEntryFolderResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		objectEntryFolderResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private static ComponentServiceObjects<CollaboratorResource>
		_collaboratorResourceComponentServiceObjects;
	private static ComponentServiceObjects<ObjectEntryFolderResource>
		_objectEntryFolderResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private com.liferay.portal.kernel.model.Company _company;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]>
		_sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;
	private VulcanBatchEngineExportTaskResource
		_vulcanBatchEngineExportTaskResource;
	private VulcanBatchEngineImportTaskResource
		_vulcanBatchEngineImportTaskResource;

}