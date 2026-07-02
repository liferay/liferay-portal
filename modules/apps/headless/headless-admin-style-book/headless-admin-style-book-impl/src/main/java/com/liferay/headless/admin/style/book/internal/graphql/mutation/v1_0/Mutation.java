/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.style.book.internal.graphql.mutation.v1_0;

import com.liferay.headless.admin.style.book.dto.v1_0.StyleBook;
import com.liferay.headless.admin.style.book.resource.v1_0.StyleBookResource;
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

import jakarta.validation.constraints.NotEmpty;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Thiago Buarque
 * @generated
 */
@Generated("")
public class Mutation {

	public static void setStyleBookResourceComponentServiceObjects(
		ComponentServiceObjects<StyleBookResource>
			styleBookResourceComponentServiceObjects) {

		_styleBookResourceComponentServiceObjects =
			styleBookResourceComponentServiceObjects;
	}

	@GraphQLField(
		description = "Deletes a specific style book of an asset library."
	)
	public boolean deleteAssetLibraryStyleBook(
			@GraphQLName("assetLibraryExternalReferenceCode") @NotEmpty String
				assetLibraryExternalReferenceCode,
			@GraphQLName("styleBookExternalReferenceCode") String
				styleBookExternalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_styleBookResourceComponentServiceObjects,
			this::_populateResourceContext,
			styleBookResource -> styleBookResource.deleteAssetLibraryStyleBook(
				assetLibraryExternalReferenceCode,
				styleBookExternalReferenceCode));

		return true;
	}

	@GraphQLField(description = "Deletes a specific style book of a site.")
	public boolean deleteSiteStyleBook(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("styleBookExternalReferenceCode") String
				styleBookExternalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_styleBookResourceComponentServiceObjects,
			this::_populateResourceContext,
			styleBookResource -> styleBookResource.deleteSiteStyleBook(
				siteExternalReferenceCode, styleBookExternalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates only the fields received in the request body, leaving any other fields untouched."
	)
	public StyleBook patchSiteStyleBook(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("styleBookExternalReferenceCode") String
				styleBookExternalReferenceCode,
			@GraphQLName("styleBook") StyleBook styleBook)
		throws Exception {

		return _applyComponentServiceObjects(
			_styleBookResourceComponentServiceObjects,
			this::_populateResourceContext,
			styleBookResource -> styleBookResource.patchSiteStyleBook(
				siteExternalReferenceCode, styleBookExternalReferenceCode,
				styleBook));
	}

	@GraphQLField
	public Response createAssetLibraryStyleBooksPageExportBatch(
			@GraphQLName("assetLibraryExternalReferenceCode") @NotEmpty String
				assetLibraryExternalReferenceCode,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_styleBookResourceComponentServiceObjects,
			this::_populateResourceContext,
			styleBookResource ->
				styleBookResource.postAssetLibraryStyleBooksPageExportBatch(
					assetLibraryExternalReferenceCode, search,
					_filterBiFunction.apply(styleBookResource, filterString),
					_sortsBiFunction.apply(styleBookResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField(description = "Adds a new style book.")
	public StyleBook createSiteStyleBook(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("styleBook") StyleBook styleBook)
		throws Exception {

		return _applyComponentServiceObjects(
			_styleBookResourceComponentServiceObjects,
			this::_populateResourceContext,
			styleBookResource -> styleBookResource.postSiteStyleBook(
				siteExternalReferenceCode, styleBook));
	}

	@GraphQLField
	public Response createSiteStyleBookBatch(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_styleBookResourceComponentServiceObjects,
			this::_populateResourceContext,
			styleBookResource -> styleBookResource.postSiteStyleBookBatch(
				siteExternalReferenceCode, callbackURL, object));
	}

	@GraphQLField
	public Response createSiteStyleBooksPageExportBatch(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_styleBookResourceComponentServiceObjects,
			this::_populateResourceContext,
			styleBookResource ->
				styleBookResource.postSiteStyleBooksPageExportBatch(
					siteExternalReferenceCode, search,
					_filterBiFunction.apply(styleBookResource, filterString),
					_sortsBiFunction.apply(styleBookResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Updates the style book with the given external reference code, or creates it if it does not exist."
	)
	public StyleBook updateSiteStyleBook(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("styleBookExternalReferenceCode") String
				styleBookExternalReferenceCode,
			@GraphQLName("styleBook") StyleBook styleBook)
		throws Exception {

		return _applyComponentServiceObjects(
			_styleBookResourceComponentServiceObjects,
			this::_populateResourceContext,
			styleBookResource -> styleBookResource.putSiteStyleBook(
				siteExternalReferenceCode, styleBookExternalReferenceCode,
				styleBook));
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

	private void _populateResourceContext(StyleBookResource styleBookResource)
		throws Exception {

		styleBookResource.setContextAcceptLanguage(_acceptLanguage);
		styleBookResource.setContextCompany(_company);
		styleBookResource.setContextHttpServletRequest(_httpServletRequest);
		styleBookResource.setContextHttpServletResponse(_httpServletResponse);
		styleBookResource.setContextUriInfo(_uriInfo);
		styleBookResource.setContextUser(_user);
		styleBookResource.setGroupLocalService(_groupLocalService);
		styleBookResource.setRoleLocalService(_roleLocalService);

		styleBookResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		styleBookResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private static ComponentServiceObjects<StyleBookResource>
		_styleBookResourceComponentServiceObjects;

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
// LIFERAY-REST-BUILDER-HASH:-17563226