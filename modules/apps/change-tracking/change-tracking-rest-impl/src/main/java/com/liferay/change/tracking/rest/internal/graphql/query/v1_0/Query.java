/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.rest.internal.graphql.query.v1_0;

import com.liferay.change.tracking.rest.dto.v1_0.CTCollection;
import com.liferay.change.tracking.rest.dto.v1_0.CTEntry;
import com.liferay.change.tracking.rest.dto.v1_0.CTProcess;
import com.liferay.change.tracking.rest.dto.v1_0.CTRemote;
import com.liferay.change.tracking.rest.resource.v1_0.CTCollectionResource;
import com.liferay.change.tracking.rest.resource.v1_0.CTEntryResource;
import com.liferay.change.tracking.rest.resource.v1_0.CTProcessResource;
import com.liferay.change.tracking.rest.resource.v1_0.CTRemoteResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLTypeExtension;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.constraints.NotEmpty;

import jakarta.ws.rs.core.UriInfo;

import java.util.Map;
import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author David Truong
 * @generated
 */
@Generated("")
public class Query {

	public static void setCTCollectionResourceComponentServiceObjects(
		ComponentServiceObjects<CTCollectionResource>
			ctCollectionResourceComponentServiceObjects) {

		_ctCollectionResourceComponentServiceObjects =
			ctCollectionResourceComponentServiceObjects;
	}

	public static void setCTEntryResourceComponentServiceObjects(
		ComponentServiceObjects<CTEntryResource>
			ctEntryResourceComponentServiceObjects) {

		_ctEntryResourceComponentServiceObjects =
			ctEntryResourceComponentServiceObjects;
	}

	public static void setCTProcessResourceComponentServiceObjects(
		ComponentServiceObjects<CTProcessResource>
			ctProcessResourceComponentServiceObjects) {

		_ctProcessResourceComponentServiceObjects =
			ctProcessResourceComponentServiceObjects;
	}

	public static void setCTRemoteResourceComponentServiceObjects(
		ComponentServiceObjects<CTRemoteResource>
			ctRemoteResourceComponentServiceObjects) {

		_ctRemoteResourceComponentServiceObjects =
			ctRemoteResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cTCollection(ctCollectionId: ___){actions, dateCreated, dateModified, dateScheduled, description, externalReferenceCode, id, name, ownerName, status, statusMessage}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CTCollection cTCollection(
			@GraphQLName("ctCollectionId") Long ctCollectionId)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctCollectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctCollectionResource -> ctCollectionResource.getCTCollection(
				ctCollectionId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cTCollectionByExternalReferenceCode(externalReferenceCode: ___){actions, dateCreated, dateModified, dateScheduled, description, externalReferenceCode, id, name, ownerName, status, statusMessage}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CTCollection cTCollectionByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctCollectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctCollectionResource ->
				ctCollectionResource.getCTCollectionByExternalReferenceCode(
					externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cTCollectionByExternalReferenceCodeShareLink(externalReferenceCode: ___){}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public String cTCollectionByExternalReferenceCodeShareLink(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctCollectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctCollectionResource ->
				ctCollectionResource.
					getCTCollectionByExternalReferenceCodeShareLink(
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cTCollectionShareLink(ctCollectionId: ___){}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public String cTCollectionShareLink(
			@GraphQLName("ctCollectionId") Long ctCollectionId)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctCollectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctCollectionResource ->
				ctCollectionResource.getCTCollectionShareLink(ctCollectionId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cTCollections(page: ___, pageSize: ___, search: ___, sorts: ___, status: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CTCollectionPage cTCollections(
			@GraphQLName("search") String search,
			@GraphQLName("status") Integer[] status,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctCollectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctCollectionResource -> new CTCollectionPage(
				ctCollectionResource.getCTCollectionsPage(
					search, status, Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						ctCollectionResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cTEntriesHistory(classNameId: ___, classPK: ___, filter: ___, page: ___, pageSize: ___, search: ___, siteKey: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CTEntryPage cTEntriesHistory(
			@GraphQLName("classNameId") Long classNameId,
			@GraphQLName("classPK") Long classPK,
			@GraphQLName("search") String search,
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctEntryResource -> new CTEntryPage(
				ctEntryResource.getCTEntriesHistoryPage(
					classNameId, classPK, search, Long.valueOf(siteKey),
					_filterBiFunction.apply(ctEntryResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(ctEntryResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cTEntry(ctEntryId: ___){actions, changeType, ctCollectionId, ctCollectionName, ctCollectionStatus, ctCollectionStatusDate, ctCollectionStatusUserName, dateCreated, dateModified, hideable, id, modelClassNameId, modelClassPK, ownerId, ownerName, siteId, siteName, status, statusMessage, title, typeName}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CTEntry cTEntry(@GraphQLName("ctEntryId") Long ctEntryId)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctEntryResource -> ctEntryResource.getCTEntry(ctEntryId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {ctCollectionCTEntries(ctCollectionId: ___, filter: ___, page: ___, pageSize: ___, search: ___, showHideable: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CTEntryPage ctCollectionCTEntries(
			@GraphQLName("ctCollectionId") Long ctCollectionId,
			@GraphQLName("search") String search,
			@GraphQLName("showHideable") Boolean showHideable,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctEntryResource -> new CTEntryPage(
				ctEntryResource.getCtCollectionCTEntriesPage(
					ctCollectionId, search, showHideable,
					_filterBiFunction.apply(ctEntryResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(ctEntryResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {ctCollectionCTEntryByModelClassNameByModelClassPkModelClassPK(ctCollectionId: ___, modelClassNameId: ___, modelClassPK: ___){actions, changeType, ctCollectionId, ctCollectionName, ctCollectionStatus, ctCollectionStatusDate, ctCollectionStatusUserName, dateCreated, dateModified, hideable, id, modelClassNameId, modelClassPK, ownerId, ownerName, siteId, siteName, status, statusMessage, title, typeName}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CTEntry
			ctCollectionCTEntryByModelClassNameByModelClassPkModelClassPK(
				@GraphQLName("ctCollectionId") Long ctCollectionId,
				@GraphQLName("modelClassNameId") Long modelClassNameId,
				@GraphQLName("modelClassPK") Long modelClassPK)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctEntryResource ->
				ctEntryResource.
					getCtCollectionCTEntryByModelClassNameByModelClassPkModelClassPK(
						ctCollectionId, modelClassNameId, modelClassPK));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cTProcess(ctProcessId: ___){actions, ctCollectionId, datePublished, description, id, name, ownerName, status}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CTProcess cTProcess(@GraphQLName("ctProcessId") Long ctProcessId)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctProcessResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctProcessResource -> ctProcessResource.getCTProcess(ctProcessId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cTProcesses(filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___, status: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CTProcessPage cTProcesses(
			@GraphQLName("search") String search,
			@GraphQLName("status") Integer[] status,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctProcessResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctProcessResource -> new CTProcessPage(
				ctProcessResource.getCTProcessesPage(
					search, status,
					_filterBiFunction.apply(ctProcessResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(ctProcessResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cTRemote(id: ___){actions, clientId, clientSecret, dateCreated, dateModified, description, id, name, ownerName, url}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CTRemote cTRemote(@GraphQLName("id") Long id) throws Exception {
		return _applyComponentServiceObjects(
			_ctRemoteResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctRemoteResource -> ctRemoteResource.getCTRemote(id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {cTRemotes(page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CTRemotePage cTRemotes(
			@GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctRemoteResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctRemoteResource -> new CTRemotePage(
				ctRemoteResource.getCTRemotesPage(
					search, Pagination.of(page, pageSize),
					_sortsBiFunction.apply(ctRemoteResource, sortsString))));
	}

	@GraphQLTypeExtension(CTCollection.class)
	public class GetCTCollectionShareLinkTypeExtension {

		public GetCTCollectionShareLinkTypeExtension(
			CTCollection cTCollection) {

			_cTCollection = cTCollection;
		}

		@GraphQLField
		public String shareLink() throws Exception {
			return _applyComponentServiceObjects(
				_ctCollectionResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				ctCollectionResource ->
					ctCollectionResource.getCTCollectionShareLink(
						_cTCollection.getId()));
		}

		private CTCollection _cTCollection;

	}

	@GraphQLTypeExtension(CTCollection.class)
	public class GetCTCollectionByExternalReferenceCodeShareLinkTypeExtension {

		public GetCTCollectionByExternalReferenceCodeShareLinkTypeExtension(
			CTCollection cTCollection) {

			_cTCollection = cTCollection;
		}

		@GraphQLField
		public String byExternalReferenceCodeShareLink() throws Exception {
			return _applyComponentServiceObjects(
				_ctCollectionResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				ctCollectionResource ->
					ctCollectionResource.
						getCTCollectionByExternalReferenceCodeShareLink(
							_cTCollection.getExternalReferenceCode()));
		}

		private CTCollection _cTCollection;

	}

	@GraphQLTypeExtension(CTProcess.class)
	public class GetCTCollectionTypeExtension {

		public GetCTCollectionTypeExtension(CTProcess cTProcess) {
			_cTProcess = cTProcess;
		}

		@GraphQLField
		public CTCollection cTCollection() throws Exception {
			return _applyComponentServiceObjects(
				_ctCollectionResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				ctCollectionResource -> ctCollectionResource.getCTCollection(
					_cTProcess.getCtCollectionId()));
		}

		private CTProcess _cTProcess;

	}

	@GraphQLTypeExtension(CTCollection.class)
	public class GetCtCollectionCTEntriesPageTypeExtension {

		public GetCtCollectionCTEntriesPageTypeExtension(
			CTCollection cTCollection) {

			_cTCollection = cTCollection;
		}

		@GraphQLField
		public CTEntryPage ctCollectionCTEntries(
				@GraphQLName("search") String search,
				@GraphQLName("showHideable") Boolean showHideable,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_ctEntryResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				ctEntryResource -> new CTEntryPage(
					ctEntryResource.getCtCollectionCTEntriesPage(
						_cTCollection.getId(), search, showHideable,
						_filterBiFunction.apply(ctEntryResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(ctEntryResource, sortsString))));
		}

		private CTCollection _cTCollection;

	}

	@GraphQLTypeExtension(CTCollection.class)
	public class
		GetCtCollectionCTEntryByModelClassNameByModelClassPkModelClassPKTypeExtension {

		public GetCtCollectionCTEntryByModelClassNameByModelClassPkModelClassPKTypeExtension(
			CTCollection cTCollection) {

			_cTCollection = cTCollection;
		}

		@GraphQLField
		public CTEntry
				ctCollectionCTEntryByModelClassNameByModelClassPkModelClassPK(
					@GraphQLName("modelClassNameId") Long modelClassNameId,
					@GraphQLName("modelClassPK") Long modelClassPK)
			throws Exception {

			return _applyComponentServiceObjects(
				_ctEntryResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				ctEntryResource ->
					ctEntryResource.
						getCtCollectionCTEntryByModelClassNameByModelClassPkModelClassPK(
							_cTCollection.getId(), modelClassNameId,
							modelClassPK));
		}

		private CTCollection _cTCollection;

	}

	@GraphQLName("CTCollectionPage")
	public class CTCollectionPage {

		public CTCollectionPage(Page ctCollectionPage) {
			actions = ctCollectionPage.getActions();

			items = ctCollectionPage.getItems();
			lastPage = ctCollectionPage.getLastPage();
			page = ctCollectionPage.getPage();
			pageSize = ctCollectionPage.getPageSize();
			totalCount = ctCollectionPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<CTCollection> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("CTEntryPage")
	public class CTEntryPage {

		public CTEntryPage(Page ctEntryPage) {
			actions = ctEntryPage.getActions();

			items = ctEntryPage.getItems();
			lastPage = ctEntryPage.getLastPage();
			page = ctEntryPage.getPage();
			pageSize = ctEntryPage.getPageSize();
			totalCount = ctEntryPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<CTEntry> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("CTProcessPage")
	public class CTProcessPage {

		public CTProcessPage(Page ctProcessPage) {
			actions = ctProcessPage.getActions();

			items = ctProcessPage.getItems();
			lastPage = ctProcessPage.getLastPage();
			page = ctProcessPage.getPage();
			pageSize = ctProcessPage.getPageSize();
			totalCount = ctProcessPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<CTProcess> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("CTRemotePage")
	public class CTRemotePage {

		public CTRemotePage(Page ctRemotePage) {
			actions = ctRemotePage.getActions();

			items = ctRemotePage.getItems();
			lastPage = ctRemotePage.getLastPage();
			page = ctRemotePage.getPage();
			pageSize = ctRemotePage.getPageSize();
			totalCount = ctRemotePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<CTRemote> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

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
			CTCollectionResource ctCollectionResource)
		throws Exception {

		ctCollectionResource.setContextAcceptLanguage(_acceptLanguage);
		ctCollectionResource.setContextCompany(_company);
		ctCollectionResource.setContextHttpServletRequest(_httpServletRequest);
		ctCollectionResource.setContextHttpServletResponse(
			_httpServletResponse);
		ctCollectionResource.setContextUriInfo(_uriInfo);
		ctCollectionResource.setContextUser(_user);
		ctCollectionResource.setGroupLocalService(_groupLocalService);
		ctCollectionResource.setResourceActionLocalService(
			_resourceActionLocalService);
		ctCollectionResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		ctCollectionResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(CTEntryResource ctEntryResource)
		throws Exception {

		ctEntryResource.setContextAcceptLanguage(_acceptLanguage);
		ctEntryResource.setContextCompany(_company);
		ctEntryResource.setContextHttpServletRequest(_httpServletRequest);
		ctEntryResource.setContextHttpServletResponse(_httpServletResponse);
		ctEntryResource.setContextUriInfo(_uriInfo);
		ctEntryResource.setContextUser(_user);
		ctEntryResource.setGroupLocalService(_groupLocalService);
		ctEntryResource.setResourceActionLocalService(
			_resourceActionLocalService);
		ctEntryResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		ctEntryResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(CTProcessResource ctProcessResource)
		throws Exception {

		ctProcessResource.setContextAcceptLanguage(_acceptLanguage);
		ctProcessResource.setContextCompany(_company);
		ctProcessResource.setContextHttpServletRequest(_httpServletRequest);
		ctProcessResource.setContextHttpServletResponse(_httpServletResponse);
		ctProcessResource.setContextUriInfo(_uriInfo);
		ctProcessResource.setContextUser(_user);
		ctProcessResource.setGroupLocalService(_groupLocalService);
		ctProcessResource.setResourceActionLocalService(
			_resourceActionLocalService);
		ctProcessResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		ctProcessResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(CTRemoteResource ctRemoteResource)
		throws Exception {

		ctRemoteResource.setContextAcceptLanguage(_acceptLanguage);
		ctRemoteResource.setContextCompany(_company);
		ctRemoteResource.setContextHttpServletRequest(_httpServletRequest);
		ctRemoteResource.setContextHttpServletResponse(_httpServletResponse);
		ctRemoteResource.setContextUriInfo(_uriInfo);
		ctRemoteResource.setContextUser(_user);
		ctRemoteResource.setGroupLocalService(_groupLocalService);
		ctRemoteResource.setResourceActionLocalService(
			_resourceActionLocalService);
		ctRemoteResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		ctRemoteResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<CTCollectionResource>
		_ctCollectionResourceComponentServiceObjects;
	private static ComponentServiceObjects<CTEntryResource>
		_ctEntryResourceComponentServiceObjects;
	private static ComponentServiceObjects<CTProcessResource>
		_ctProcessResourceComponentServiceObjects;
	private static ComponentServiceObjects<CTRemoteResource>
		_ctRemoteResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
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