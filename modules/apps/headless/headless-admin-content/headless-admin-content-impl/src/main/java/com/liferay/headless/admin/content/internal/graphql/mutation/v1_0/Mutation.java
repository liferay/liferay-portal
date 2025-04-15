/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.content.internal.graphql.mutation.v1_0;

import com.liferay.headless.admin.content.resource.v1_0.PageDefinitionResource;
import com.liferay.headless.admin.content.resource.v1_0.StructuredContentResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;

import java.util.function.BiFunction;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.constraints.NotEmpty;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class Mutation {

	public static void setPageDefinitionResourceComponentServiceObjects(
		ComponentServiceObjects<PageDefinitionResource>
			pageDefinitionResourceComponentServiceObjects) {

		_pageDefinitionResourceComponentServiceObjects =
			pageDefinitionResourceComponentServiceObjects;
	}

	public static void setStructuredContentResourceComponentServiceObjects(
		ComponentServiceObjects<StructuredContentResource>
			structuredContentResourceComponentServiceObjects) {

		_structuredContentResourceComponentServiceObjects =
			structuredContentResourceComponentServiceObjects;
	}

	@GraphQLField(
		description = "Renders and retrieves HTML for the page definition using the theme of specified site."
	)
	public Response createSitePageDefinitionPreview(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("pageDefinition")
				com.liferay.headless.delivery.dto.v1_0.PageDefinition
					pageDefinition)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageDefinitionResource ->
				pageDefinitionResource.postSitePageDefinitionPreview(
					Long.valueOf(siteKey), pageDefinition));
	}

	@GraphQLField(description = "Creates a draft of a structured content")
	public com.liferay.headless.delivery.dto.v1_0.StructuredContent
			createSiteStructuredContentDraft(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("structuredContent")
					com.liferay.headless.delivery.dto.v1_0.StructuredContent
						structuredContent)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.postSiteStructuredContentDraft(
					Long.valueOf(siteKey), structuredContent));
	}

	@GraphQLField(
		description = "Deletes a version of a structured content via its ID."
	)
	public boolean deleteStructuredContentByVersion(
			@GraphQLName("structuredContentId") Long structuredContentId,
			@GraphQLName("version") Double version)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.deleteStructuredContentByVersion(
					structuredContentId, version));

		return true;
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
			PageDefinitionResource pageDefinitionResource)
		throws Exception {

		pageDefinitionResource.setContextAcceptLanguage(_acceptLanguage);
		pageDefinitionResource.setContextCompany(_company);
		pageDefinitionResource.setContextHttpServletRequest(
			_httpServletRequest);
		pageDefinitionResource.setContextHttpServletResponse(
			_httpServletResponse);
		pageDefinitionResource.setContextUriInfo(_uriInfo);
		pageDefinitionResource.setContextUser(_user);
		pageDefinitionResource.setGroupLocalService(_groupLocalService);
		pageDefinitionResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			StructuredContentResource structuredContentResource)
		throws Exception {

		structuredContentResource.setContextAcceptLanguage(_acceptLanguage);
		structuredContentResource.setContextCompany(_company);
		structuredContentResource.setContextHttpServletRequest(
			_httpServletRequest);
		structuredContentResource.setContextHttpServletResponse(
			_httpServletResponse);
		structuredContentResource.setContextUriInfo(_uriInfo);
		structuredContentResource.setContextUser(_user);
		structuredContentResource.setGroupLocalService(_groupLocalService);
		structuredContentResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<PageDefinitionResource>
		_pageDefinitionResourceComponentServiceObjects;
	private static ComponentServiceObjects<StructuredContentResource>
		_structuredContentResourceComponentServiceObjects;

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

}