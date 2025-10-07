package com.liferay.testray.rest.internal.graphql.mutation.v1_0;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.multipart.MultipartBody;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.testray.rest.dto.v1_0.TestrayBuild;
import com.liferay.testray.rest.dto.v1_0.TestrayBuildAutofill;
import com.liferay.testray.rest.dto.v1_0.TestraySubtask;
import com.liferay.testray.rest.dto.v1_0.TestrayTestFlow;
import com.liferay.testray.rest.dto.v1_0.TestrayTestSuite;
import com.liferay.testray.rest.resource.v1_0.TestrayBuildAutofillResource;
import com.liferay.testray.rest.resource.v1_0.TestrayBuildResource;
import com.liferay.testray.rest.resource.v1_0.TestrayTestFlowResource;
import com.liferay.testray.rest.resource.v1_0.TestrayTestSuiteResource;

import java.util.function.BiFunction;

import javax.annotation.Generated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Nilton Vieira
 * @generated
 */
@Generated("")
public class Mutation {

	public static void setTestrayBuildResourceComponentServiceObjects(
		ComponentServiceObjects<TestrayBuildResource>
			testrayBuildResourceComponentServiceObjects) {

		_testrayBuildResourceComponentServiceObjects =
			testrayBuildResourceComponentServiceObjects;
	}

	public static void setTestrayBuildAutofillResourceComponentServiceObjects(
		ComponentServiceObjects<TestrayBuildAutofillResource>
			testrayBuildAutofillResourceComponentServiceObjects) {

		_testrayBuildAutofillResourceComponentServiceObjects =
			testrayBuildAutofillResourceComponentServiceObjects;
	}

	public static void setTestrayTestFlowResourceComponentServiceObjects(
		ComponentServiceObjects<TestrayTestFlowResource>
			testrayTestFlowResourceComponentServiceObjects) {

		_testrayTestFlowResourceComponentServiceObjects =
			testrayTestFlowResourceComponentServiceObjects;
	}

	public static void setTestrayTestSuiteResourceComponentServiceObjects(
		ComponentServiceObjects<TestrayTestSuiteResource>
			testrayTestSuiteResourceComponentServiceObjects) {

		_testrayTestSuiteResourceComponentServiceObjects =
			testrayTestSuiteResourceComponentServiceObjects;
	}

	@GraphQLField
	public TestrayBuild patchTestrayBuild(
			@GraphQLName("testrayBuildId") Long testrayBuildId)
		throws Exception {

		return _applyComponentServiceObjects(
			_testrayBuildResourceComponentServiceObjects,
			this::_populateResourceContext,
			testrayBuildResource -> testrayBuildResource.patchTestrayBuild(
				testrayBuildId));
	}

	@GraphQLField
	public TestrayBuildAutofill createTestrayBuildAutofill(
			@GraphQLName("testrayBuildId1") Long testrayBuildId1,
			@GraphQLName("testrayBuildId2") Long testrayBuildId2)
		throws Exception {

		return _applyComponentServiceObjects(
			_testrayBuildAutofillResourceComponentServiceObjects,
			this::_populateResourceContext,
			testrayBuildAutofillResource ->
				testrayBuildAutofillResource.postTestrayBuildAutofill(
					testrayBuildId1, testrayBuildId2));
	}

	@GraphQLField
	public Response createTestrayBuildAutofillBatch(
			@GraphQLName("testrayBuildId1") Long testrayBuildId1,
			@GraphQLName("testrayBuildId2") Long testrayBuildId2,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_testrayBuildAutofillResourceComponentServiceObjects,
			this::_populateResourceContext,
			testrayBuildAutofillResource ->
				testrayBuildAutofillResource.postTestrayBuildAutofillBatch(
					testrayBuildId1, testrayBuildId2, callbackURL, object));
	}

	@GraphQLField
	public TestrayTestFlow createTestrayTestFlow(
			@GraphQLName("testrayTaskId") Long testrayTaskId)
		throws Exception {

		return _applyComponentServiceObjects(
			_testrayTestFlowResourceComponentServiceObjects,
			this::_populateResourceContext,
			testrayTestFlowResource ->
				testrayTestFlowResource.postTestrayTestFlow(testrayTaskId));
	}

	@GraphQLField
	public Response createTestrayTestFlowBatch(
			@GraphQLName("testrayTaskId") Long testrayTaskId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_testrayTestFlowResourceComponentServiceObjects,
			this::_populateResourceContext,
			testrayTestFlowResource ->
				testrayTestFlowResource.postTestrayTestFlowBatch(
					testrayTaskId, callbackURL, object));
	}

	@GraphQLField
	public TestrayTestFlow
			updateTestrayTestFlowByTestraySubtaskIdTestraySubtask(
				@GraphQLName("testraySubtaskId") Long testraySubtaskId,
				@GraphQLName("testrayTestFlow") TestrayTestFlow testrayTestFlow)
		throws Exception {

		return _applyComponentServiceObjects(
			_testrayTestFlowResourceComponentServiceObjects,
			this::_populateResourceContext,
			testrayTestFlowResource ->
				testrayTestFlowResource.
					putTestrayTestFlowByTestraySubtaskIdTestraySubtask(
						testraySubtaskId, testrayTestFlow));
	}

	@GraphQLField
	public java.util.Collection<TestraySubtask>
			updateTestrayTestFlowTestraySubtaskMergePage(
				@GraphQLName("testraySubtasks") TestraySubtask[]
					testraySubtasks)
		throws Exception {

		return _applyComponentServiceObjects(
			_testrayTestFlowResourceComponentServiceObjects,
			this::_populateResourceContext,
			testrayTestFlowResource -> {
				Page paginationPage =
					testrayTestFlowResource.
						putTestrayTestFlowTestraySubtaskMergePage(
							testraySubtasks);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	@GraphQLName(
		description = "null", value = "postTestrayTestSuiteMultipartBody"
	)
	public TestrayTestSuite createTestrayTestSuite(
			@GraphQLName("multipartBody") MultipartBody multipartBody)
		throws Exception {

		return _applyComponentServiceObjects(
			_testrayTestSuiteResourceComponentServiceObjects,
			this::_populateResourceContext,
			testrayTestSuiteResource ->
				testrayTestSuiteResource.postTestrayTestSuite(multipartBody));
	}

	@GraphQLField
	public Response createTestrayTestSuiteBatch(
			@GraphQLName("multipartBody") MultipartBody multipartBody,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_testrayTestSuiteResourceComponentServiceObjects,
			this::_populateResourceContext,
			testrayTestSuiteResource ->
				testrayTestSuiteResource.postTestrayTestSuiteBatch(
					multipartBody, callbackURL, object));
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
			TestrayBuildResource testrayBuildResource)
		throws Exception {

		testrayBuildResource.setContextAcceptLanguage(_acceptLanguage);
		testrayBuildResource.setContextCompany(_company);
		testrayBuildResource.setContextHttpServletRequest(_httpServletRequest);
		testrayBuildResource.setContextHttpServletResponse(
			_httpServletResponse);
		testrayBuildResource.setContextUriInfo(_uriInfo);
		testrayBuildResource.setContextUser(_user);
		testrayBuildResource.setGroupLocalService(_groupLocalService);
		testrayBuildResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			TestrayBuildAutofillResource testrayBuildAutofillResource)
		throws Exception {

		testrayBuildAutofillResource.setContextAcceptLanguage(_acceptLanguage);
		testrayBuildAutofillResource.setContextCompany(_company);
		testrayBuildAutofillResource.setContextHttpServletRequest(
			_httpServletRequest);
		testrayBuildAutofillResource.setContextHttpServletResponse(
			_httpServletResponse);
		testrayBuildAutofillResource.setContextUriInfo(_uriInfo);
		testrayBuildAutofillResource.setContextUser(_user);
		testrayBuildAutofillResource.setGroupLocalService(_groupLocalService);
		testrayBuildAutofillResource.setRoleLocalService(_roleLocalService);

		testrayBuildAutofillResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		testrayBuildAutofillResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			TestrayTestFlowResource testrayTestFlowResource)
		throws Exception {

		testrayTestFlowResource.setContextAcceptLanguage(_acceptLanguage);
		testrayTestFlowResource.setContextCompany(_company);
		testrayTestFlowResource.setContextHttpServletRequest(
			_httpServletRequest);
		testrayTestFlowResource.setContextHttpServletResponse(
			_httpServletResponse);
		testrayTestFlowResource.setContextUriInfo(_uriInfo);
		testrayTestFlowResource.setContextUser(_user);
		testrayTestFlowResource.setGroupLocalService(_groupLocalService);
		testrayTestFlowResource.setRoleLocalService(_roleLocalService);

		testrayTestFlowResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		testrayTestFlowResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			TestrayTestSuiteResource testrayTestSuiteResource)
		throws Exception {

		testrayTestSuiteResource.setContextAcceptLanguage(_acceptLanguage);
		testrayTestSuiteResource.setContextCompany(_company);
		testrayTestSuiteResource.setContextHttpServletRequest(
			_httpServletRequest);
		testrayTestSuiteResource.setContextHttpServletResponse(
			_httpServletResponse);
		testrayTestSuiteResource.setContextUriInfo(_uriInfo);
		testrayTestSuiteResource.setContextUser(_user);
		testrayTestSuiteResource.setGroupLocalService(_groupLocalService);
		testrayTestSuiteResource.setRoleLocalService(_roleLocalService);

		testrayTestSuiteResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		testrayTestSuiteResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private static ComponentServiceObjects<TestrayBuildResource>
		_testrayBuildResourceComponentServiceObjects;
	private static ComponentServiceObjects<TestrayBuildAutofillResource>
		_testrayBuildAutofillResourceComponentServiceObjects;
	private static ComponentServiceObjects<TestrayTestFlowResource>
		_testrayTestFlowResourceComponentServiceObjects;
	private static ComponentServiceObjects<TestrayTestSuiteResource>
		_testrayTestSuiteResourceComponentServiceObjects;

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