/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.admin.rest.internal.graphql.mutation.v1_0;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.saml.admin.rest.dto.v1_0.SamlProvider;
import com.liferay.saml.admin.rest.resource.v1_0.SamlProviderResource;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Stian Sigvartsen
 * @generated
 */
@Generated("")
public class Mutation {

	public static void setSamlProviderResourceComponentServiceObjects(
		ComponentServiceObjects<SamlProviderResource>
			samlProviderResourceComponentServiceObjects) {

		_samlProviderResourceComponentServiceObjects =
			samlProviderResourceComponentServiceObjects;
	}

	@GraphQLField(description = "Patch the SAML Provider configuration.")
	public SamlProvider patchSamlProvider(
			@GraphQLName("samlProvider") SamlProvider samlProvider)
		throws Exception {

		return _applyComponentServiceObjects(
			_samlProviderResourceComponentServiceObjects,
			this::_populateResourceContext,
			samlProviderResource -> samlProviderResource.patchSamlProvider(
				samlProvider));
	}

	@GraphQLField(
		description = "Creates a full SAML Provider configuration with peer connections."
	)
	public SamlProvider createSamlProvider(
			@GraphQLName("samlProvider") SamlProvider samlProvider)
		throws Exception {

		return _applyComponentServiceObjects(
			_samlProviderResourceComponentServiceObjects,
			this::_populateResourceContext,
			samlProviderResource -> samlProviderResource.postSamlProvider(
				samlProvider));
	}

	@GraphQLField
	public Response createSamlProviderBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_samlProviderResourceComponentServiceObjects,
			this::_populateResourceContext,
			samlProviderResource -> samlProviderResource.postSamlProviderBatch(
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
			SamlProviderResource samlProviderResource)
		throws Exception {

		samlProviderResource.setContextAcceptLanguage(_acceptLanguage);
		samlProviderResource.setContextCompany(_company);
		samlProviderResource.setContextHttpServletRequest(_httpServletRequest);
		samlProviderResource.setContextHttpServletResponse(
			_httpServletResponse);
		samlProviderResource.setContextUriInfo(_uriInfo);
		samlProviderResource.setContextUser(_user);
		samlProviderResource.setGroupLocalService(_groupLocalService);
		samlProviderResource.setRoleLocalService(_roleLocalService);

		samlProviderResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		samlProviderResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private static ComponentServiceObjects<SamlProviderResource>
		_samlProviderResourceComponentServiceObjects;

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