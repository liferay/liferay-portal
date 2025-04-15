/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.rest.internal.graphql.mutation.v1_0;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.segments.asah.rest.dto.v1_0.Experiment;
import com.liferay.segments.asah.rest.dto.v1_0.ExperimentRun;
import com.liferay.segments.asah.rest.dto.v1_0.Status;
import com.liferay.segments.asah.rest.resource.v1_0.ExperimentResource;
import com.liferay.segments.asah.rest.resource.v1_0.ExperimentRunResource;
import com.liferay.segments.asah.rest.resource.v1_0.StatusResource;

import java.util.function.BiFunction;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class Mutation {

	public static void setExperimentResourceComponentServiceObjects(
		ComponentServiceObjects<ExperimentResource>
			experimentResourceComponentServiceObjects) {

		_experimentResourceComponentServiceObjects =
			experimentResourceComponentServiceObjects;
	}

	public static void setExperimentRunResourceComponentServiceObjects(
		ComponentServiceObjects<ExperimentRunResource>
			experimentRunResourceComponentServiceObjects) {

		_experimentRunResourceComponentServiceObjects =
			experimentRunResourceComponentServiceObjects;
	}

	public static void setStatusResourceComponentServiceObjects(
		ComponentServiceObjects<StatusResource>
			statusResourceComponentServiceObjects) {

		_statusResourceComponentServiceObjects =
			statusResourceComponentServiceObjects;
	}

	@GraphQLField
	public boolean deleteExperiment(
			@GraphQLName("experimentId") String experimentId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_experimentResourceComponentServiceObjects,
			this::_populateResourceContext,
			experimentResource -> experimentResource.deleteExperiment(
				experimentId));

		return true;
	}

	@GraphQLField
	public Response deleteExperimentBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_experimentResourceComponentServiceObjects,
			this::_populateResourceContext,
			experimentResource -> experimentResource.deleteExperimentBatch(
				callbackURL, object));
	}

	@GraphQLField
	public ExperimentRun createExperimentRun(
			@GraphQLName("experimentId") Long experimentId,
			@GraphQLName("experimentRun") ExperimentRun experimentRun)
		throws Exception {

		return _applyComponentServiceObjects(
			_experimentRunResourceComponentServiceObjects,
			this::_populateResourceContext,
			experimentRunResource -> experimentRunResource.postExperimentRun(
				experimentId, experimentRun));
	}

	@GraphQLField
	public Experiment createExperimentStatus(
			@GraphQLName("experimentId") Long experimentId,
			@GraphQLName("status") Status status)
		throws Exception {

		return _applyComponentServiceObjects(
			_statusResourceComponentServiceObjects,
			this::_populateResourceContext,
			statusResource -> statusResource.postExperimentStatus(
				experimentId, status));
	}

	@GraphQLField
	public Response createExperimentStatusBatch(
			@GraphQLName("experimentId") Long experimentId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_statusResourceComponentServiceObjects,
			this::_populateResourceContext,
			statusResource -> statusResource.postExperimentStatusBatch(
				experimentId, callbackURL, object));
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

	private void _populateResourceContext(ExperimentResource experimentResource)
		throws Exception {

		experimentResource.setContextAcceptLanguage(_acceptLanguage);
		experimentResource.setContextCompany(_company);
		experimentResource.setContextHttpServletRequest(_httpServletRequest);
		experimentResource.setContextHttpServletResponse(_httpServletResponse);
		experimentResource.setContextUriInfo(_uriInfo);
		experimentResource.setContextUser(_user);
		experimentResource.setGroupLocalService(_groupLocalService);
		experimentResource.setRoleLocalService(_roleLocalService);

		experimentResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		experimentResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			ExperimentRunResource experimentRunResource)
		throws Exception {

		experimentRunResource.setContextAcceptLanguage(_acceptLanguage);
		experimentRunResource.setContextCompany(_company);
		experimentRunResource.setContextHttpServletRequest(_httpServletRequest);
		experimentRunResource.setContextHttpServletResponse(
			_httpServletResponse);
		experimentRunResource.setContextUriInfo(_uriInfo);
		experimentRunResource.setContextUser(_user);
		experimentRunResource.setGroupLocalService(_groupLocalService);
		experimentRunResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(StatusResource statusResource)
		throws Exception {

		statusResource.setContextAcceptLanguage(_acceptLanguage);
		statusResource.setContextCompany(_company);
		statusResource.setContextHttpServletRequest(_httpServletRequest);
		statusResource.setContextHttpServletResponse(_httpServletResponse);
		statusResource.setContextUriInfo(_uriInfo);
		statusResource.setContextUser(_user);
		statusResource.setGroupLocalService(_groupLocalService);
		statusResource.setRoleLocalService(_roleLocalService);

		statusResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		statusResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private static ComponentServiceObjects<ExperimentResource>
		_experimentResourceComponentServiceObjects;
	private static ComponentServiceObjects<ExperimentRunResource>
		_experimentRunResourceComponentServiceObjects;
	private static ComponentServiceObjects<StatusResource>
		_statusResourceComponentServiceObjects;

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