/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.object.internal.graphql.query.v1_0;

import com.liferay.headless.object.dto.v1_0.Collaborator;
import com.liferay.headless.object.dto.v1_0.ObjectEntryFolder;
import com.liferay.headless.object.resource.v1_0.CollaboratorResource;
import com.liferay.headless.object.resource.v1_0.ObjectEntryFolderResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.aggregation.Facet;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLTypeExtension;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.UriInfo;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Alicia García
 * @generated
 */
@Generated("")
public class Query {

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

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectEntryFolderCollaboratorByTypeCollaborator(collaboratorId: ___, objectEntryFolderId: ___, type: ___){actionIds, actions, creator, dateExpired, externalReferenceCode, id, name, portrait, share, type}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the collaborator of an object entry."
	)
	public Collaborator objectEntryFolderCollaboratorByTypeCollaborator(
			@GraphQLName("objectEntryFolderId") Long objectEntryFolderId,
			@GraphQLName("type") String type,
			@GraphQLName("collaboratorId") Long collaboratorId)
		throws Exception {

		return _applyComponentServiceObjects(
			_collaboratorResourceComponentServiceObjects,
			this::_populateResourceContext,
			collaboratorResource ->
				collaboratorResource.
					getObjectEntryFolderCollaboratorByTypeCollaborator(
						objectEntryFolderId, type, collaboratorId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectEntryFolderCollaborators(objectEntryFolderId: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the collaborators of an object entry folder."
	)
	public CollaboratorPage objectEntryFolderCollaborators(
			@GraphQLName("objectEntryFolderId") Long objectEntryFolderId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_collaboratorResourceComponentServiceObjects,
			this::_populateResourceContext,
			collaboratorResource -> new CollaboratorPage(
				collaboratorResource.getObjectEntryFolderCollaboratorsPage(
					objectEntryFolderId, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {scopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator(collaboratorId: ___, externalReferenceCode: ___, scopeKey: ___, type: ___){actionIds, actions, creator, dateExpired, externalReferenceCode, id, name, portrait, share, type}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the collaborator for an object entry folder."
	)
	public Collaborator
			scopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator(
				@GraphQLName("scopeKey") String scopeKey,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("type") String type,
				@GraphQLName("collaboratorId") Long collaboratorId)
		throws Exception {

		return _applyComponentServiceObjects(
			_collaboratorResourceComponentServiceObjects,
			this::_populateResourceContext,
			collaboratorResource ->
				collaboratorResource.
					getScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorByTypeCollaborator(
						scopeKey, externalReferenceCode, type, collaboratorId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {scopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaborators(externalReferenceCode: ___, page: ___, pageSize: ___, scopeKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the collaborators of an object entry."
	)
	public CollaboratorPage
			scopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaborators(
				@GraphQLName("scopeKey") String scopeKey,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_collaboratorResourceComponentServiceObjects,
			this::_populateResourceContext,
			collaboratorResource -> new CollaboratorPage(
				collaboratorResource.
					getScopeScopeKeyObjectEntryFolderByExternalReferenceCodeCollaboratorsPage(
						scopeKey, externalReferenceCode,
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectEntryFolder(objectEntryFolderId: ___){actions, creator, dateCreated, dateModified, description, externalReferenceCode, id, label, label_i18n, numberOfObjectEntries, numberOfObjectEntryFolders, parentObjectEntryFolderBrief, parentObjectEntryFolderExternalReferenceCode, parentObjectEntryFolderId, permissions, removedBy, removedDate, scopeId, scopeKey, status, title, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the object entry folder.")
	public ObjectEntryFolder objectEntryFolder(
			@GraphQLName("objectEntryFolderId") Long objectEntryFolderId)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryFolderResource ->
				objectEntryFolderResource.getObjectEntryFolder(
					objectEntryFolderId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {objectEntryFolderPermissions(objectEntryFolderId: ___, roleNames: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectEntryFolderPage objectEntryFolderPermissions(
			@GraphQLName("objectEntryFolderId") Long objectEntryFolderId,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryFolderResource -> new ObjectEntryFolderPage(
				objectEntryFolderResource.getObjectEntryFolderPermissionsPage(
					objectEntryFolderId, roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {scopeScopeKeyObjectEntryFolderByExternalReferenceCode(externalReferenceCode: ___, scopeKey: ___){actions, creator, dateCreated, dateModified, description, externalReferenceCode, id, label, label_i18n, numberOfObjectEntries, numberOfObjectEntryFolders, parentObjectEntryFolderBrief, parentObjectEntryFolderExternalReferenceCode, parentObjectEntryFolderId, permissions, removedBy, removedDate, scopeId, scopeKey, status, title, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectEntryFolder
			scopeScopeKeyObjectEntryFolderByExternalReferenceCode(
				@GraphQLName("scopeKey") String scopeKey,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryFolderResource ->
				objectEntryFolderResource.
					getScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
						scopeKey, externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {scopeScopeKeyObjectEntryFolders(aggregation: ___, filter: ___, flatten: ___, page: ___, pageSize: ___, scopeKey: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ObjectEntryFolderPage scopeScopeKeyObjectEntryFolders(
			@GraphQLName("scopeKey") String scopeKey,
			@GraphQLName("flatten") Boolean flatten,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_objectEntryFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			objectEntryFolderResource -> new ObjectEntryFolderPage(
				objectEntryFolderResource.
					getScopeScopeKeyObjectEntryFoldersPage(
						scopeKey, flatten, search,
						_aggregationBiFunction.apply(
							objectEntryFolderResource, aggregations),
						_filterBiFunction.apply(
							objectEntryFolderResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							objectEntryFolderResource, sortsString))));
	}

	@GraphQLTypeExtension(ObjectEntryFolder.class)
	public class
		GetObjectEntryFolderCollaboratorByTypeCollaboratorTypeExtension {

		public GetObjectEntryFolderCollaboratorByTypeCollaboratorTypeExtension(
			ObjectEntryFolder objectEntryFolder) {

			_objectEntryFolder = objectEntryFolder;
		}

		@GraphQLField(
			description = "Retrieves the collaborator of an object entry."
		)
		public Collaborator collaboratorByTypeCollaborator(
				@GraphQLName("type") String type,
				@GraphQLName("collaboratorId") Long collaboratorId)
			throws Exception {

			return _applyComponentServiceObjects(
				_collaboratorResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				collaboratorResource ->
					collaboratorResource.
						getObjectEntryFolderCollaboratorByTypeCollaborator(
							_objectEntryFolder.getId(), type, collaboratorId));
		}

		private ObjectEntryFolder _objectEntryFolder;

	}

	@GraphQLTypeExtension(ObjectEntryFolder.class)
	public class GetObjectEntryFolderCollaboratorsPageTypeExtension {

		public GetObjectEntryFolderCollaboratorsPageTypeExtension(
			ObjectEntryFolder objectEntryFolder) {

			_objectEntryFolder = objectEntryFolder;
		}

		@GraphQLField(
			description = "Retrieves the collaborators of an object entry folder."
		)
		public CollaboratorPage collaborators(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_collaboratorResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				collaboratorResource -> new CollaboratorPage(
					collaboratorResource.getObjectEntryFolderCollaboratorsPage(
						_objectEntryFolder.getId(),
						Pagination.of(page, pageSize))));
		}

		private ObjectEntryFolder _objectEntryFolder;

	}

	@GraphQLName("CollaboratorPage")
	public class CollaboratorPage {

		public CollaboratorPage(Page collaboratorPage) {
			actions = collaboratorPage.getActions();

			facets = collaboratorPage.getFacets();

			items = collaboratorPage.getItems();
			lastPage = collaboratorPage.getLastPage();
			page = collaboratorPage.getPage();
			pageSize = collaboratorPage.getPageSize();
			totalCount = collaboratorPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<Collaborator> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ObjectEntryFolderPage")
	public class ObjectEntryFolderPage {

		public ObjectEntryFolderPage(Page objectEntryFolderPage) {
			actions = objectEntryFolderPage.getActions();

			facets = objectEntryFolderPage.getFacets();

			items = objectEntryFolderPage.getItems();
			lastPage = objectEntryFolderPage.getLastPage();
			page = objectEntryFolderPage.getPage();
			pageSize = objectEntryFolderPage.getPageSize();
			totalCount = objectEntryFolderPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<ObjectEntryFolder> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLTypeExtension(ObjectEntryFolder.class)
	public class ParentObjectEntryFolderObjectEntryFolderIdTypeExtension {

		public ParentObjectEntryFolderObjectEntryFolderIdTypeExtension(
			ObjectEntryFolder objectEntryFolder) {

			_objectEntryFolder = objectEntryFolder;
		}

		@GraphQLField(description = "Retrieves the object entry folder.")
		public ObjectEntryFolder parentObjectEntryFolder() throws Exception {
			if (_objectEntryFolder.getParentObjectEntryFolderId() == null) {
				return null;
			}

			return _applyComponentServiceObjects(
				_objectEntryFolderResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				objectEntryFolderResource ->
					objectEntryFolderResource.getObjectEntryFolder(
						_objectEntryFolder.getParentObjectEntryFolderId()));
		}

		private ObjectEntryFolder _objectEntryFolder;

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
		collaboratorResource.setResourceActionLocalService(
			_resourceActionLocalService);
		collaboratorResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		collaboratorResource.setRoleLocalService(_roleLocalService);
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
		objectEntryFolderResource.setResourceActionLocalService(
			_resourceActionLocalService);
		objectEntryFolderResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		objectEntryFolderResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<CollaboratorResource>
		_collaboratorResourceComponentServiceObjects;
	private static ComponentServiceObjects<ObjectEntryFolderResource>
		_objectEntryFolderResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private BiFunction<Object, List<String>, Aggregation>
		_aggregationBiFunction;
	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction
		<Object, String, com.liferay.portal.kernel.search.filter.Filter>
			_filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private ResourceActionLocalService _resourceActionLocalService;
	private ResourcePermissionLocalService _resourcePermissionLocalService;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]>
		_sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;

}