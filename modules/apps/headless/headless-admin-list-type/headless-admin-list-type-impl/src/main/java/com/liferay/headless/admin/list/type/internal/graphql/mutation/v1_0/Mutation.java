/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.list.type.internal.graphql.mutation.v1_0;

import com.liferay.headless.admin.list.type.dto.v1_0.ListTypeDefinition;
import com.liferay.headless.admin.list.type.dto.v1_0.ListTypeEntry;
import com.liferay.headless.admin.list.type.resource.v1_0.ListTypeDefinitionResource;
import com.liferay.headless.admin.list.type.resource.v1_0.ListTypeEntryResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Gabriel Albuquerque
 * @generated
 */
@Generated("")
public class Mutation {

	public static void setListTypeDefinitionResourceComponentServiceObjects(
		ComponentServiceObjects<ListTypeDefinitionResource>
			listTypeDefinitionResourceComponentServiceObjects) {

		_listTypeDefinitionResourceComponentServiceObjects =
			listTypeDefinitionResourceComponentServiceObjects;
	}

	public static void setListTypeEntryResourceComponentServiceObjects(
		ComponentServiceObjects<ListTypeEntryResource>
			listTypeEntryResourceComponentServiceObjects) {

		_listTypeEntryResourceComponentServiceObjects =
			listTypeEntryResourceComponentServiceObjects;
	}

	@GraphQLField
	public Response createListTypeDefinitionsPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_listTypeDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeDefinitionResource ->
				listTypeDefinitionResource.
					postListTypeDefinitionsPageExportBatch(
						search,
						_filterBiFunction.apply(
							listTypeDefinitionResource, filterString),
						_sortsBiFunction.apply(
							listTypeDefinitionResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public ListTypeDefinition createListTypeDefinition(
			@GraphQLName("listTypeDefinition") ListTypeDefinition
				listTypeDefinition)
		throws Exception {

		return _applyComponentServiceObjects(
			_listTypeDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeDefinitionResource ->
				listTypeDefinitionResource.postListTypeDefinition(
					listTypeDefinition));
	}

	@GraphQLField
	public Response createListTypeDefinitionBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_listTypeDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeDefinitionResource ->
				listTypeDefinitionResource.postListTypeDefinitionBatch(
					callbackURL, object));
	}

	@GraphQLField
	public ListTypeDefinition updateListTypeDefinitionByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("listTypeDefinition") ListTypeDefinition
				listTypeDefinition)
		throws Exception {

		return _applyComponentServiceObjects(
			_listTypeDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeDefinitionResource ->
				listTypeDefinitionResource.
					putListTypeDefinitionByExternalReferenceCode(
						externalReferenceCode, listTypeDefinition));
	}

	@GraphQLField
	public boolean deleteListTypeDefinition(
			@GraphQLName("listTypeDefinitionId") Long listTypeDefinitionId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_listTypeDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeDefinitionResource ->
				listTypeDefinitionResource.deleteListTypeDefinition(
					listTypeDefinitionId));

		return true;
	}

	@GraphQLField
	public Response deleteListTypeDefinitionBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_listTypeDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeDefinitionResource ->
				listTypeDefinitionResource.deleteListTypeDefinitionBatch(
					callbackURL, object));
	}

	@GraphQLField
	public ListTypeDefinition patchListTypeDefinition(
			@GraphQLName("listTypeDefinitionId") Long listTypeDefinitionId,
			@GraphQLName("listTypeDefinition") ListTypeDefinition
				listTypeDefinition)
		throws Exception {

		return _applyComponentServiceObjects(
			_listTypeDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeDefinitionResource ->
				listTypeDefinitionResource.patchListTypeDefinition(
					listTypeDefinitionId, listTypeDefinition));
	}

	@GraphQLField
	public ListTypeDefinition updateListTypeDefinition(
			@GraphQLName("listTypeDefinitionId") Long listTypeDefinitionId,
			@GraphQLName("listTypeDefinition") ListTypeDefinition
				listTypeDefinition)
		throws Exception {

		return _applyComponentServiceObjects(
			_listTypeDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeDefinitionResource ->
				listTypeDefinitionResource.putListTypeDefinition(
					listTypeDefinitionId, listTypeDefinition));
	}

	@GraphQLField
	public Response updateListTypeDefinitionBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_listTypeDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeDefinitionResource ->
				listTypeDefinitionResource.putListTypeDefinitionBatch(
					callbackURL, object));
	}

	@GraphQLField
	public ListTypeEntry
			createListTypeDefinitionByExternalReferenceCodeListTypeEntry(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("listTypeEntry") ListTypeEntry listTypeEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_listTypeEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeEntryResource ->
				listTypeEntryResource.
					postListTypeDefinitionByExternalReferenceCodeListTypeEntry(
						externalReferenceCode, listTypeEntry));
	}

	@GraphQLField
	public Response createListTypeDefinitionListTypeEntriesPageExportBatch(
			@GraphQLName("listTypeDefinitionId") Long listTypeDefinitionId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_listTypeEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeEntryResource ->
				listTypeEntryResource.
					postListTypeDefinitionListTypeEntriesPageExportBatch(
						listTypeDefinitionId, search,
						_filterBiFunction.apply(
							listTypeEntryResource, filterString),
						_sortsBiFunction.apply(
							listTypeEntryResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public ListTypeEntry createListTypeDefinitionListTypeEntry(
			@GraphQLName("listTypeDefinitionId") Long listTypeDefinitionId,
			@GraphQLName("listTypeEntry") ListTypeEntry listTypeEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_listTypeEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeEntryResource ->
				listTypeEntryResource.postListTypeDefinitionListTypeEntry(
					listTypeDefinitionId, listTypeEntry));
	}

	@GraphQLField
	public Response createListTypeDefinitionListTypeEntryBatch(
			@GraphQLName("listTypeDefinitionId") Long listTypeDefinitionId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_listTypeEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeEntryResource ->
				listTypeEntryResource.postListTypeDefinitionListTypeEntryBatch(
					listTypeDefinitionId, callbackURL, object));
	}

	@GraphQLField
	public boolean deleteListTypeEntry(
			@GraphQLName("listTypeEntryId") Long listTypeEntryId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_listTypeEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeEntryResource -> listTypeEntryResource.deleteListTypeEntry(
				listTypeEntryId));

		return true;
	}

	@GraphQLField
	public Response deleteListTypeEntryBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_listTypeEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeEntryResource ->
				listTypeEntryResource.deleteListTypeEntryBatch(
					callbackURL, object));
	}

	@GraphQLField
	public ListTypeEntry updateListTypeEntry(
			@GraphQLName("listTypeEntryId") Long listTypeEntryId,
			@GraphQLName("listTypeEntry") ListTypeEntry listTypeEntry)
		throws Exception {

		return _applyComponentServiceObjects(
			_listTypeEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeEntryResource -> listTypeEntryResource.putListTypeEntry(
				listTypeEntryId, listTypeEntry));
	}

	@GraphQLField
	public Response updateListTypeEntryBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_listTypeEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeEntryResource ->
				listTypeEntryResource.putListTypeEntryBatch(
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
			ListTypeDefinitionResource listTypeDefinitionResource)
		throws Exception {

		listTypeDefinitionResource.setContextAcceptLanguage(_acceptLanguage);
		listTypeDefinitionResource.setContextCompany(_company);
		listTypeDefinitionResource.setContextHttpServletRequest(
			_httpServletRequest);
		listTypeDefinitionResource.setContextHttpServletResponse(
			_httpServletResponse);
		listTypeDefinitionResource.setContextUriInfo(_uriInfo);
		listTypeDefinitionResource.setContextUser(_user);
		listTypeDefinitionResource.setGroupLocalService(_groupLocalService);
		listTypeDefinitionResource.setRoleLocalService(_roleLocalService);

		listTypeDefinitionResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		listTypeDefinitionResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			ListTypeEntryResource listTypeEntryResource)
		throws Exception {

		listTypeEntryResource.setContextAcceptLanguage(_acceptLanguage);
		listTypeEntryResource.setContextCompany(_company);
		listTypeEntryResource.setContextHttpServletRequest(_httpServletRequest);
		listTypeEntryResource.setContextHttpServletResponse(
			_httpServletResponse);
		listTypeEntryResource.setContextUriInfo(_uriInfo);
		listTypeEntryResource.setContextUser(_user);
		listTypeEntryResource.setGroupLocalService(_groupLocalService);
		listTypeEntryResource.setRoleLocalService(_roleLocalService);

		listTypeEntryResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		listTypeEntryResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private static ComponentServiceObjects<ListTypeDefinitionResource>
		_listTypeDefinitionResourceComponentServiceObjects;
	private static ComponentServiceObjects<ListTypeEntryResource>
		_listTypeEntryResourceComponentServiceObjects;

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