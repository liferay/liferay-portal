/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.rest.internal.graphql.mutation.v1_0;

import com.liferay.change.tracking.rest.dto.v1_0.CTCollection;
import com.liferay.change.tracking.rest.dto.v1_0.CTRemote;
import com.liferay.change.tracking.rest.resource.v1_0.CTCollectionResource;
import com.liferay.change.tracking.rest.resource.v1_0.CTProcessResource;
import com.liferay.change.tracking.rest.resource.v1_0.CTRemoteResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;

import java.util.Date;
import java.util.function.BiFunction;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author David Truong
 * @generated
 */
@Generated("")
public class Mutation {

	public static void setCTCollectionResourceComponentServiceObjects(
		ComponentServiceObjects<CTCollectionResource>
			ctCollectionResourceComponentServiceObjects) {

		_ctCollectionResourceComponentServiceObjects =
			ctCollectionResourceComponentServiceObjects;
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

	@GraphQLField
	public Response createCTCollectionsPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("status") Integer[] status,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctCollectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctCollectionResource ->
				ctCollectionResource.postCTCollectionsPageExportBatch(
					search, status,
					_sortsBiFunction.apply(ctCollectionResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public CTCollection createCTCollection(
			@GraphQLName("ctCollection") CTCollection ctCollection)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctCollectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctCollectionResource -> ctCollectionResource.postCTCollection(
				ctCollection));
	}

	@GraphQLField
	public Response createCTCollectionBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctCollectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctCollectionResource -> ctCollectionResource.postCTCollectionBatch(
				callbackURL, object));
	}

	@GraphQLField
	public boolean deleteCTCollectionByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_ctCollectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctCollectionResource ->
				ctCollectionResource.deleteCTCollectionByExternalReferenceCode(
					externalReferenceCode));

		return true;
	}

	@GraphQLField
	public CTCollection patchCTCollectionByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("ctCollection") CTCollection ctCollection)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctCollectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctCollectionResource ->
				ctCollectionResource.patchCTCollectionByExternalReferenceCode(
					externalReferenceCode, ctCollection));
	}

	@GraphQLField
	public boolean createCTCollectionByExternalReferenceCodePublish(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_ctCollectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctCollectionResource ->
				ctCollectionResource.
					postCTCollectionByExternalReferenceCodePublish(
						externalReferenceCode));

		return true;
	}

	@GraphQLField
	public boolean createCTCollectionByExternalReferenceCodeSchedulePublish(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("publishDate") Date publishDate)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_ctCollectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctCollectionResource ->
				ctCollectionResource.
					postCTCollectionByExternalReferenceCodeSchedulePublish(
						externalReferenceCode, publishDate));

		return true;
	}

	@GraphQLField
	public boolean deleteCTCollection(
			@GraphQLName("ctCollectionId") Long ctCollectionId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_ctCollectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctCollectionResource -> ctCollectionResource.deleteCTCollection(
				ctCollectionId));

		return true;
	}

	@GraphQLField
	public Response deleteCTCollectionBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctCollectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctCollectionResource ->
				ctCollectionResource.deleteCTCollectionBatch(
					callbackURL, object));
	}

	@GraphQLField
	public CTCollection patchCTCollection(
			@GraphQLName("ctCollectionId") Long ctCollectionId,
			@GraphQLName("ctCollection") CTCollection ctCollection)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctCollectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctCollectionResource -> ctCollectionResource.patchCTCollection(
				ctCollectionId, ctCollection));
	}

	@GraphQLField
	public CTCollection updateCTCollection(
			@GraphQLName("ctCollectionId") Long ctCollectionId,
			@GraphQLName("ctCollection") CTCollection ctCollection)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctCollectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctCollectionResource -> ctCollectionResource.putCTCollection(
				ctCollectionId, ctCollection));
	}

	@GraphQLField
	public Response updateCTCollectionBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctCollectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctCollectionResource -> ctCollectionResource.putCTCollectionBatch(
				callbackURL, object));
	}

	@GraphQLField
	public boolean createCTCollectionCheckout(
			@GraphQLName("ctCollectionId") Long ctCollectionId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_ctCollectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctCollectionResource ->
				ctCollectionResource.postCTCollectionCheckout(ctCollectionId));

		return true;
	}

	@GraphQLField
	public boolean createCTCollectionPublish(
			@GraphQLName("ctCollectionId") Long ctCollectionId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_ctCollectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctCollectionResource ->
				ctCollectionResource.postCTCollectionPublish(ctCollectionId));

		return true;
	}

	@GraphQLField
	public boolean createCTCollectionSchedulePublish(
			@GraphQLName("ctCollectionId") Long ctCollectionId,
			@GraphQLName("publishDate") Date publishDate)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_ctCollectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctCollectionResource ->
				ctCollectionResource.postCTCollectionSchedulePublish(
					ctCollectionId, publishDate));

		return true;
	}

	@GraphQLField
	public Response createCTProcessesPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("status") Integer[] status,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctProcessResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctProcessResource ->
				ctProcessResource.postCTProcessesPageExportBatch(
					search, status,
					_filterBiFunction.apply(ctProcessResource, filterString),
					_sortsBiFunction.apply(ctProcessResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public boolean deleteCTProcess(@GraphQLName("ctProcessId") Long ctProcessId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_ctProcessResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctProcessResource -> ctProcessResource.deleteCTProcess(
				ctProcessId));

		return true;
	}

	@GraphQLField
	public Response deleteCTProcessBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctProcessResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctProcessResource -> ctProcessResource.deleteCTProcessBatch(
				callbackURL, object));
	}

	@GraphQLField
	public boolean createCTProcessRevert(
			@GraphQLName("ctProcessId") Long ctProcessId,
			@GraphQLName("description") String description,
			@GraphQLName("name") String name)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_ctProcessResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctProcessResource -> ctProcessResource.postCTProcessRevert(
				ctProcessId, description, name));

		return true;
	}

	@GraphQLField
	public Response createCTRemotesPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctRemoteResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctRemoteResource -> ctRemoteResource.postCTRemotesPageExportBatch(
				search, _sortsBiFunction.apply(ctRemoteResource, sortsString),
				callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public CTRemote createCTRemote(@GraphQLName("ctRemote") CTRemote ctRemote)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctRemoteResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctRemoteResource -> ctRemoteResource.postCTRemote(ctRemote));
	}

	@GraphQLField
	public Response createCTRemoteBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctRemoteResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctRemoteResource -> ctRemoteResource.postCTRemoteBatch(
				callbackURL, object));
	}

	@GraphQLField
	public boolean deleteCTRemote(@GraphQLName("id") Long id) throws Exception {
		_applyVoidComponentServiceObjects(
			_ctRemoteResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctRemoteResource -> ctRemoteResource.deleteCTRemote(id));

		return true;
	}

	@GraphQLField
	public Response deleteCTRemoteBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctRemoteResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctRemoteResource -> ctRemoteResource.deleteCTRemoteBatch(
				callbackURL, object));
	}

	@GraphQLField
	public CTRemote patchCTRemote(
			@GraphQLName("id") Long id,
			@GraphQLName("ctRemote") CTRemote ctRemote)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctRemoteResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctRemoteResource -> ctRemoteResource.patchCTRemote(id, ctRemote));
	}

	@GraphQLField
	public CTRemote updateCTRemote(
			@GraphQLName("id") Long id,
			@GraphQLName("ctRemote") CTRemote ctRemote)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctRemoteResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctRemoteResource -> ctRemoteResource.putCTRemote(id, ctRemote));
	}

	@GraphQLField
	public Response updateCTRemoteBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_ctRemoteResourceComponentServiceObjects,
			this::_populateResourceContext,
			ctRemoteResource -> ctRemoteResource.putCTRemoteBatch(
				callbackURL, object));
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
		ctCollectionResource.setRoleLocalService(_roleLocalService);

		ctCollectionResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		ctCollectionResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		ctProcessResource.setRoleLocalService(_roleLocalService);

		ctProcessResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		ctProcessResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		ctRemoteResource.setRoleLocalService(_roleLocalService);

		ctRemoteResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		ctRemoteResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private static ComponentServiceObjects<CTCollectionResource>
		_ctCollectionResourceComponentServiceObjects;
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