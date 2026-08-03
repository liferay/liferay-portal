/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.language.override.internal.graphql.mutation.v1_0;

import com.liferay.headless.admin.language.override.dto.v1_0.LanguageOverride;
import com.liferay.headless.admin.language.override.resource.v1_0.LanguageOverrideResource;
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
 * @author Thiago Buarque
 * @generated
 */
@Generated("")
public class Mutation {

	public static void setLanguageOverrideResourceComponentServiceObjects(
		ComponentServiceObjects<LanguageOverrideResource>
			languageOverrideResourceComponentServiceObjects) {

		_languageOverrideResourceComponentServiceObjects =
			languageOverrideResourceComponentServiceObjects;
	}

	@GraphQLField
	public boolean deleteLanguageOverrideByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_languageOverrideResourceComponentServiceObjects,
			this::_populateResourceContext,
			languageOverrideResource ->
				languageOverrideResource.
					deleteLanguageOverrideByExternalReferenceCode(
						externalReferenceCode));

		return true;
	}

	@GraphQLField
	public LanguageOverride createLanguageOverride(
			@GraphQLName("languageOverride") LanguageOverride languageOverride)
		throws Exception {

		return _applyComponentServiceObjects(
			_languageOverrideResourceComponentServiceObjects,
			this::_populateResourceContext,
			languageOverrideResource ->
				languageOverrideResource.postLanguageOverride(
					languageOverride));
	}

	@GraphQLField
	public Response createLanguageOverrideBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_languageOverrideResourceComponentServiceObjects,
			this::_populateResourceContext,
			languageOverrideResource ->
				languageOverrideResource.postLanguageOverrideBatch(
					callbackURL, object));
	}

	@GraphQLField
	public Response createLanguageOverridesPageExportBatch(
			@GraphQLName("search") String search,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_languageOverrideResourceComponentServiceObjects,
			this::_populateResourceContext,
			languageOverrideResource ->
				languageOverrideResource.postLanguageOverridesPageExportBatch(
					search,
					_sortsBiFunction.apply(
						languageOverrideResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public LanguageOverride updateLanguageOverrideByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("languageOverride") LanguageOverride languageOverride)
		throws Exception {

		return _applyComponentServiceObjects(
			_languageOverrideResourceComponentServiceObjects,
			this::_populateResourceContext,
			languageOverrideResource ->
				languageOverrideResource.
					putLanguageOverrideByExternalReferenceCode(
						externalReferenceCode, languageOverride));
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
			LanguageOverrideResource languageOverrideResource)
		throws Exception {

		languageOverrideResource.setContextAcceptLanguage(_acceptLanguage);
		languageOverrideResource.setContextCompany(_company);
		languageOverrideResource.setContextHttpServletRequest(
			_httpServletRequest);
		languageOverrideResource.setContextHttpServletResponse(
			_httpServletResponse);
		languageOverrideResource.setContextUriInfo(_uriInfo);
		languageOverrideResource.setContextUser(_user);
		languageOverrideResource.setGroupLocalService(_groupLocalService);
		languageOverrideResource.setRoleLocalService(_roleLocalService);

		languageOverrideResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		languageOverrideResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private static ComponentServiceObjects<LanguageOverrideResource>
		_languageOverrideResourceComponentServiceObjects;

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
// LIFERAY-REST-BUILDER-HASH:1653057350