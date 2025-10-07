/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowDefinition;
import com.liferay.headless.admin.workflow.client.http.HttpInvoker;
import com.liferay.headless.admin.workflow.client.pagination.Page;
import com.liferay.headless.admin.workflow.client.pagination.Pagination;
import com.liferay.headless.admin.workflow.client.resource.v1_0.WorkflowDefinitionResource;
import com.liferay.headless.admin.workflow.client.serdes.v1_0.WorkflowDefinitionSerDes;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONDeserializer;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegate;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegateBuilderRegistry;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.lang.reflect.Method;

import java.net.URI;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public abstract class BaseWorkflowDefinitionResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");
	}

	@Before
	public void setUp() throws Exception {
		irrelevantGroup = GroupTestUtil.addGroup();
		testGroup = GroupTestUtil.addGroup();

		testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		_workflowDefinitionResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		workflowDefinitionResource = WorkflowDefinitionResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		importTaskResource = ImportTaskResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@After
	public void tearDown() throws Exception {
		GroupTestUtil.deleteGroup(irrelevantGroup);
		GroupTestUtil.deleteGroup(testGroup);
	}

	@Test
	public void testClientSerDesToDTO() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		WorkflowDefinition workflowDefinition1 = randomWorkflowDefinition();

		String json = objectMapper.writeValueAsString(workflowDefinition1);

		WorkflowDefinition workflowDefinition2 = WorkflowDefinitionSerDes.toDTO(
			json);

		Assert.assertTrue(equals(workflowDefinition1, workflowDefinition2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		WorkflowDefinition workflowDefinition = randomWorkflowDefinition();

		String json1 = objectMapper.writeValueAsString(workflowDefinition);
		String json2 = WorkflowDefinitionSerDes.toJSON(workflowDefinition);

		Assert.assertEquals(
			objectMapper.readTree(json1), objectMapper.readTree(json2));
	}

	protected ObjectMapper getClientSerDesObjectMapper() {
		return new ObjectMapper() {
			{
				configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
				configure(
					SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
				enable(SerializationFeature.INDENT_OUTPUT);
				setDateFormat(new ISO8601DateFormat());
				setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
				setSerializationInclusion(JsonInclude.Include.NON_NULL);
				setVisibility(
					PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
				setVisibility(
					PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
			}
		};
	}

	@Test
	public void testEscapeRegexInStringFields() throws Exception {
		String regex = "^[0-9]+(\\.[0-9]{1,2})\"?";

		WorkflowDefinition workflowDefinition = randomWorkflowDefinition();

		workflowDefinition.setContent(regex);
		workflowDefinition.setDescription(regex);
		workflowDefinition.setExternalReferenceCode(regex);
		workflowDefinition.setName(regex);
		workflowDefinition.setTitle(regex);
		workflowDefinition.setVersion(regex);

		String json = WorkflowDefinitionSerDes.toJSON(workflowDefinition);

		Assert.assertFalse(json.contains(regex));

		workflowDefinition = WorkflowDefinitionSerDes.toDTO(json);

		Assert.assertEquals(regex, workflowDefinition.getContent());
		Assert.assertEquals(regex, workflowDefinition.getDescription());
		Assert.assertEquals(
			regex, workflowDefinition.getExternalReferenceCode());
		Assert.assertEquals(regex, workflowDefinition.getName());
		Assert.assertEquals(regex, workflowDefinition.getTitle());
		Assert.assertEquals(regex, workflowDefinition.getVersion());
	}

	@Test
	public void testDeleteWorkflowDefinition() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		WorkflowDefinition workflowDefinition =
			testDeleteWorkflowDefinition_addWorkflowDefinition();

		assertHttpResponseStatusCode(
			204,
			workflowDefinitionResource.deleteWorkflowDefinitionHttpResponse(
				workflowDefinition.getId()));

		assertHttpResponseStatusCode(
			404,
			workflowDefinitionResource.getWorkflowDefinitionHttpResponse(
				workflowDefinition.getId()));
		assertHttpResponseStatusCode(
			404,
			workflowDefinitionResource.getWorkflowDefinitionHttpResponse(0L));
	}

	protected WorkflowDefinition
			testDeleteWorkflowDefinition_addWorkflowDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteWorkflowDefinition() throws Exception {

		// No namespace

		WorkflowDefinition workflowDefinition1 =
			testGraphQLDeleteWorkflowDefinition_addWorkflowDefinition();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteWorkflowDefinition",
						new HashMap<String, Object>() {
							{
								put(
									"workflowDefinitionId",
									workflowDefinition1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteWorkflowDefinition"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"workflowDefinition",
					new HashMap<String, Object>() {
						{
							put(
								"workflowDefinitionId",
								workflowDefinition1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessAdminWorkflow_v1_0

		WorkflowDefinition workflowDefinition2 =
			testGraphQLDeleteWorkflowDefinition_addWorkflowDefinition();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminWorkflow_v1_0",
						new GraphQLField(
							"deleteWorkflowDefinition",
							new HashMap<String, Object>() {
								{
									put(
										"workflowDefinitionId",
										workflowDefinition2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminWorkflow_v1_0",
				"Object/deleteWorkflowDefinition"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessAdminWorkflow_v1_0",
					new GraphQLField(
						"workflowDefinition",
						new HashMap<String, Object>() {
							{
								put(
									"workflowDefinitionId",
									workflowDefinition2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected WorkflowDefinition
			testGraphQLDeleteWorkflowDefinition_addWorkflowDefinition()
		throws Exception {

		return testGraphQLWorkflowDefinition_addWorkflowDefinition();
	}

	@Test
	public void testDeleteWorkflowDefinitionBatch() throws Exception {
		WorkflowDefinition workflowDefinition1 =
			testDeleteWorkflowDefinitionBatch_addWorkflowDefinition();

		testDeleteWorkflowDefinitionBatch_deleteWorkflowDefinition(
			202, null, workflowDefinition1.getId());

		assertHttpResponseStatusCode(
			404,
			workflowDefinitionResource.getWorkflowDefinitionHttpResponse(
				workflowDefinition1.getId()));
	}

	protected WorkflowDefinition
			testDeleteWorkflowDefinitionBatch_addWorkflowDefinition()
		throws Exception {

		return testDeleteWorkflowDefinition_addWorkflowDefinition();
	}

	protected void testDeleteWorkflowDefinitionBatch_deleteWorkflowDefinition(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			workflowDefinitionResource.
				deleteWorkflowDefinitionBatchHttpResponse(
					null,
					JSONUtil.putAll(
						JSONUtil.put(
							"externalReferenceCode", () -> externalReferenceCode
						).put(
							"id", () -> id
						)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testDeleteWorkflowDefinitionUndeploy() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		WorkflowDefinition workflowDefinition =
			testDeleteWorkflowDefinitionUndeploy_addWorkflowDefinition();

		assertHttpResponseStatusCode(
			204,
			workflowDefinitionResource.
				deleteWorkflowDefinitionUndeployHttpResponse(null, null));
	}

	protected WorkflowDefinition
			testDeleteWorkflowDefinitionUndeploy_addWorkflowDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteWorkflowDefinitionUndeploy() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testGetWorkflowDefinition() throws Exception {
		WorkflowDefinition postWorkflowDefinition =
			testGetWorkflowDefinition_addWorkflowDefinition();

		WorkflowDefinition getWorkflowDefinition =
			workflowDefinitionResource.getWorkflowDefinition(
				postWorkflowDefinition.getId());

		assertEquals(postWorkflowDefinition, getWorkflowDefinition);
		assertValid(getWorkflowDefinition);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		WorkflowDefinition postWorkflowDefinition =
			testGetWorkflowDefinition_addWorkflowDefinition();

		WorkflowDefinition getWorkflowDefinition =
			workflowDefinitionResource.getWorkflowDefinition(
				postWorkflowDefinition.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.admin.workflow.dto.v1_0.WorkflowDefinition"
			).acceptLanguage(
				new AcceptLanguage() {

					@Override
					public List<Locale> getLocales() {
						return Arrays.asList(LocaleUtil.getDefault());
					}

					@Override
					public String getPreferredLanguageId() {
						return LocaleUtil.toLanguageId(LocaleUtil.getDefault());
					}

					@Override
					public Locale getPreferredLocale() {
						return LocaleUtil.getDefault();
					}

				}
			).groupLocalService(
				_groupLocalService
			).httpServletRequest(
				testVulcanCRUDItemDelegate_getHttpServletRequest()
			).httpServletResponse(
				new MockHttpServletResponse()
			).resourceActionLocalService(
				_resourceActionLocalService
			).resourcePermissionLocalService(
				_resourcePermissionLocalService
			).roleLocalService(
				_roleLocalService
			).scopeChecker(
				_scopeChecker
			).uriInfo(
				testVulcanCRUDItemDelegate_getUriInfo()
			).user(
				testVulcanCRUDItemDelegate_getUser()
			).build();

		Object item = vulcanCRUDItemDelegate.getItem(
			postWorkflowDefinition.getId());

		assertEquals(
			getWorkflowDefinition,
			WorkflowDefinitionSerDes.toDTO(item.toString()));
	}

	protected HttpServletRequest
		testVulcanCRUDItemDelegate_getHttpServletRequest() {

		return new MockHttpServletRequest() {

			@Override
			public StringBuffer getRequestURL() {
				return new StringBuffer(
					StringBundler.concat(
						"http://localhost:8080/o/v1.0/",
						RandomTestUtil.randomString(), "/",
						RandomTestUtil.randomString()));
			}

		};
	}

	protected UriInfo testVulcanCRUDItemDelegate_getUriInfo() {
		String applicationPath = RandomTestUtil.randomString() + "/";
		String resourcePath = RandomTestUtil.randomString();

		return new UriInfo() {

			@Override
			public String getPath() {
				return resourcePath;
			}

			@Override
			public String getPath(boolean decode) {
				return getPath();
			}

			@Override
			public List<PathSegment> getPathSegments() {
				return Collections.emptyList();
			}

			@Override
			public List<PathSegment> getPathSegments(boolean decode) {
				return getPathSegments();
			}

			@Override
			public URI getRequestUri() {
				return URI.create(
					"http://localhost:8080/o/" + applicationPath +
						resourcePath);
			}

			@Override
			public UriBuilder getRequestUriBuilder() {
				return UriBuilder.fromUri(getRequestUri());
			}

			@Override
			public URI getAbsolutePath() {
				return getRequestUri();
			}

			@Override
			public UriBuilder getAbsolutePathBuilder() {
				return getRequestUriBuilder();
			}

			@Override
			public URI getBaseUri() {
				return URI.create("http://localhost:8080/o/" + applicationPath);
			}

			@Override
			public UriBuilder getBaseUriBuilder() {
				return UriBuilder.fromUri(getBaseUri());
			}

			@Override
			public MultivaluedMap<String, String> getPathParameters() {
				return new MultivaluedHashMap<>();
			}

			@Override
			public MultivaluedMap<String, String> getPathParameters(
				boolean decode) {

				return getPathParameters();
			}

			@Override
			public MultivaluedMap<String, String> getQueryParameters() {
				return new MultivaluedHashMap<>();
			}

			@Override
			public MultivaluedMap<String, String> getQueryParameters(
				boolean decode) {

				return getQueryParameters();
			}

			@Override
			public List<String> getMatchedURIs() {
				return Collections.emptyList();
			}

			@Override
			public List<String> getMatchedURIs(boolean decode) {
				return getMatchedURIs();
			}

			@Override
			public List<Object> getMatchedResources() {
				return Collections.emptyList();
			}

			@Override
			public URI resolve(URI requestUri) {
				return getBaseUri().resolve(requestUri);
			}

			@Override
			public URI relativize(URI uri) {
				return getBaseUri().relativize(uri);
			}

		};
	}

	protected com.liferay.portal.kernel.model.User
		testVulcanCRUDItemDelegate_getUser() {

		return _testCompanyAdminUser;
	}

	protected WorkflowDefinition
			testGetWorkflowDefinition_addWorkflowDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetWorkflowDefinition() throws Exception {
		WorkflowDefinition workflowDefinition =
			testGraphQLGetWorkflowDefinition_addWorkflowDefinition();

		// No namespace

		Assert.assertTrue(
			equals(
				workflowDefinition,
				WorkflowDefinitionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"workflowDefinition",
								new HashMap<String, Object>() {
									{
										put(
											"workflowDefinitionId",
											workflowDefinition.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/workflowDefinition"))));

		// Using the namespace headlessAdminWorkflow_v1_0

		Assert.assertTrue(
			equals(
				workflowDefinition,
				WorkflowDefinitionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminWorkflow_v1_0",
								new GraphQLField(
									"workflowDefinition",
									new HashMap<String, Object>() {
										{
											put(
												"workflowDefinitionId",
												workflowDefinition.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessAdminWorkflow_v1_0",
						"Object/workflowDefinition"))));
	}

	@Test
	public void testGraphQLGetWorkflowDefinitionNotFound() throws Exception {
		Long irrelevantWorkflowDefinitionId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"workflowDefinition",
						new HashMap<String, Object>() {
							{
								put(
									"workflowDefinitionId",
									irrelevantWorkflowDefinitionId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessAdminWorkflow_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminWorkflow_v1_0",
						new GraphQLField(
							"workflowDefinition",
							new HashMap<String, Object>() {
								{
									put(
										"workflowDefinitionId",
										irrelevantWorkflowDefinitionId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected WorkflowDefinition
			testGraphQLGetWorkflowDefinition_addWorkflowDefinition()
		throws Exception {

		return testGraphQLWorkflowDefinition_addWorkflowDefinition();
	}

	@Test
	public void testGetWorkflowDefinitionByName() throws Exception {
		WorkflowDefinition postWorkflowDefinition =
			testGetWorkflowDefinitionByName_addWorkflowDefinition();

		WorkflowDefinition getWorkflowDefinition =
			workflowDefinitionResource.getWorkflowDefinitionByName(
				postWorkflowDefinition.getName(), null, null);

		assertEquals(postWorkflowDefinition, getWorkflowDefinition);
		assertValid(getWorkflowDefinition);
	}

	protected WorkflowDefinition
			testGetWorkflowDefinitionByName_addWorkflowDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetWorkflowDefinitionByName() throws Exception {
		WorkflowDefinition workflowDefinition =
			testGraphQLGetWorkflowDefinitionByName_addWorkflowDefinition();

		// No namespace

		Assert.assertTrue(
			equals(
				workflowDefinition,
				WorkflowDefinitionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"workflowDefinitionByName",
								new HashMap<String, Object>() {
									{
										put(
											"name",
											"\"" +
												workflowDefinition.getName() +
													"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/workflowDefinitionByName"))));

		// Using the namespace headlessAdminWorkflow_v1_0

		Assert.assertTrue(
			equals(
				workflowDefinition,
				WorkflowDefinitionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminWorkflow_v1_0",
								new GraphQLField(
									"workflowDefinitionByName",
									new HashMap<String, Object>() {
										{
											put(
												"name",
												"\"" +
													workflowDefinition.
														getName() + "\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessAdminWorkflow_v1_0",
						"Object/workflowDefinitionByName"))));
	}

	@Test
	public void testGraphQLGetWorkflowDefinitionByNameNotFound()
		throws Exception {

		String irrelevantName = "\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"workflowDefinitionByName",
						new HashMap<String, Object>() {
							{
								put("name", irrelevantName);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessAdminWorkflow_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminWorkflow_v1_0",
						new GraphQLField(
							"workflowDefinitionByName",
							new HashMap<String, Object>() {
								{
									put("name", irrelevantName);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected WorkflowDefinition
			testGraphQLGetWorkflowDefinitionByName_addWorkflowDefinition()
		throws Exception {

		return testGraphQLWorkflowDefinition_addWorkflowDefinition();
	}

	@Test
	public void testGetWorkflowDefinitionsPage() throws Exception {
		Page<WorkflowDefinition> page =
			workflowDefinitionResource.getWorkflowDefinitionsPage(
				null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		WorkflowDefinition workflowDefinition1 =
			testGetWorkflowDefinitionsPage_addWorkflowDefinition(
				randomWorkflowDefinition());

		WorkflowDefinition workflowDefinition2 =
			testGetWorkflowDefinitionsPage_addWorkflowDefinition(
				randomWorkflowDefinition());

		page = workflowDefinitionResource.getWorkflowDefinitionsPage(
			null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			workflowDefinition1, (List<WorkflowDefinition>)page.getItems());
		assertContains(
			workflowDefinition2, (List<WorkflowDefinition>)page.getItems());
		assertValid(page, testGetWorkflowDefinitionsPage_getExpectedActions());

		workflowDefinitionResource.deleteWorkflowDefinition(
			workflowDefinition1.getId());

		workflowDefinitionResource.deleteWorkflowDefinition(
			workflowDefinition2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetWorkflowDefinitionsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWorkflowDefinitionsPageWithPagination()
		throws Exception {

		Page<WorkflowDefinition> workflowDefinitionsPage =
			workflowDefinitionResource.getWorkflowDefinitionsPage(
				null, null, null);

		int totalCount = GetterUtil.getInteger(
			workflowDefinitionsPage.getTotalCount());

		WorkflowDefinition workflowDefinition1 =
			testGetWorkflowDefinitionsPage_addWorkflowDefinition(
				randomWorkflowDefinition());

		WorkflowDefinition workflowDefinition2 =
			testGetWorkflowDefinitionsPage_addWorkflowDefinition(
				randomWorkflowDefinition());

		WorkflowDefinition workflowDefinition3 =
			testGetWorkflowDefinitionsPage_addWorkflowDefinition(
				randomWorkflowDefinition());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<WorkflowDefinition> page1 =
				workflowDefinitionResource.getWorkflowDefinitionsPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				workflowDefinition1,
				(List<WorkflowDefinition>)page1.getItems());

			Page<WorkflowDefinition> page2 =
				workflowDefinitionResource.getWorkflowDefinitionsPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				workflowDefinition2,
				(List<WorkflowDefinition>)page2.getItems());

			Page<WorkflowDefinition> page3 =
				workflowDefinitionResource.getWorkflowDefinitionsPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				workflowDefinition3,
				(List<WorkflowDefinition>)page3.getItems());
		}
		else {
			Page<WorkflowDefinition> page1 =
				workflowDefinitionResource.getWorkflowDefinitionsPage(
					null, Pagination.of(1, totalCount + 2), null);

			List<WorkflowDefinition> workflowDefinitions1 =
				(List<WorkflowDefinition>)page1.getItems();

			Assert.assertEquals(
				workflowDefinitions1.toString(), totalCount + 2,
				workflowDefinitions1.size());

			Page<WorkflowDefinition> page2 =
				workflowDefinitionResource.getWorkflowDefinitionsPage(
					null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<WorkflowDefinition> workflowDefinitions2 =
				(List<WorkflowDefinition>)page2.getItems();

			Assert.assertEquals(
				workflowDefinitions2.toString(), 1,
				workflowDefinitions2.size());

			Page<WorkflowDefinition> page3 =
				workflowDefinitionResource.getWorkflowDefinitionsPage(
					null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				workflowDefinition1,
				(List<WorkflowDefinition>)page3.getItems());
			assertContains(
				workflowDefinition2,
				(List<WorkflowDefinition>)page3.getItems());
			assertContains(
				workflowDefinition3,
				(List<WorkflowDefinition>)page3.getItems());
		}
	}

	@Test
	public void testGetWorkflowDefinitionsPageWithSortDateTime()
		throws Exception {

		testGetWorkflowDefinitionsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, workflowDefinition1, workflowDefinition2) -> {
				BeanTestUtil.setProperty(
					workflowDefinition1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetWorkflowDefinitionsPageWithSortDouble()
		throws Exception {

		testGetWorkflowDefinitionsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, workflowDefinition1, workflowDefinition2) -> {
				BeanTestUtil.setProperty(
					workflowDefinition1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					workflowDefinition2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetWorkflowDefinitionsPageWithSortInteger()
		throws Exception {

		testGetWorkflowDefinitionsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, workflowDefinition1, workflowDefinition2) -> {
				BeanTestUtil.setProperty(
					workflowDefinition1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					workflowDefinition2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetWorkflowDefinitionsPageWithSortString()
		throws Exception {

		testGetWorkflowDefinitionsPageWithSort(
			EntityField.Type.STRING,
			(entityField, workflowDefinition1, workflowDefinition2) -> {
				Class<?> clazz = workflowDefinition1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						workflowDefinition1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						workflowDefinition2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						workflowDefinition1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						workflowDefinition2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						workflowDefinition1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						workflowDefinition2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetWorkflowDefinitionsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, WorkflowDefinition, WorkflowDefinition, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		WorkflowDefinition workflowDefinition1 = randomWorkflowDefinition();
		WorkflowDefinition workflowDefinition2 = randomWorkflowDefinition();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, workflowDefinition1, workflowDefinition2);
		}

		workflowDefinition1 =
			testGetWorkflowDefinitionsPage_addWorkflowDefinition(
				workflowDefinition1);

		workflowDefinition2 =
			testGetWorkflowDefinitionsPage_addWorkflowDefinition(
				workflowDefinition2);

		Page<WorkflowDefinition> page =
			workflowDefinitionResource.getWorkflowDefinitionsPage(
				null, null, null);

		for (EntityField entityField : entityFields) {
			Page<WorkflowDefinition> ascPage =
				workflowDefinitionResource.getWorkflowDefinitionsPage(
					null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				workflowDefinition1,
				(List<WorkflowDefinition>)ascPage.getItems());
			assertContains(
				workflowDefinition2,
				(List<WorkflowDefinition>)ascPage.getItems());

			Page<WorkflowDefinition> descPage =
				workflowDefinitionResource.getWorkflowDefinitionsPage(
					null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				workflowDefinition2,
				(List<WorkflowDefinition>)descPage.getItems());
			assertContains(
				workflowDefinition1,
				(List<WorkflowDefinition>)descPage.getItems());
		}
	}

	protected WorkflowDefinition
			testGetWorkflowDefinitionsPage_addWorkflowDefinition(
				WorkflowDefinition workflowDefinition)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetWorkflowDefinitionsPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"workflowDefinitions",
			new HashMap<String, Object>() {
				{
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject workflowDefinitionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/workflowDefinitions");

		long totalCount = workflowDefinitionsJSONObject.getLong("totalCount");

		WorkflowDefinition workflowDefinition1 =
			testGraphQLWorkflowDefinition_addWorkflowDefinition(
				randomWorkflowDefinition());

		WorkflowDefinition workflowDefinition2 =
			testGraphQLWorkflowDefinition_addWorkflowDefinition(
				randomWorkflowDefinition());

		workflowDefinitionsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/workflowDefinitions");

		Assert.assertEquals(
			totalCount + 2,
			workflowDefinitionsJSONObject.getLong("totalCount"));

		assertContains(
			workflowDefinition1,
			Arrays.asList(
				WorkflowDefinitionSerDes.toDTOs(
					workflowDefinitionsJSONObject.getString("items"))));
		assertContains(
			workflowDefinition2,
			Arrays.asList(
				WorkflowDefinitionSerDes.toDTOs(
					workflowDefinitionsJSONObject.getString("items"))));

		// Using the namespace headlessAdminWorkflow_v1_0

		workflowDefinitionsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessAdminWorkflow_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessAdminWorkflow_v1_0",
			"JSONObject/workflowDefinitions");

		Assert.assertEquals(
			totalCount + 2,
			workflowDefinitionsJSONObject.getLong("totalCount"));

		assertContains(
			workflowDefinition1,
			Arrays.asList(
				WorkflowDefinitionSerDes.toDTOs(
					workflowDefinitionsJSONObject.getString("items"))));
		assertContains(
			workflowDefinition2,
			Arrays.asList(
				WorkflowDefinitionSerDes.toDTOs(
					workflowDefinitionsJSONObject.getString("items"))));
	}

	@Test
	public void testPostWorkflowDefinition() throws Exception {
		WorkflowDefinition randomWorkflowDefinition =
			randomWorkflowDefinition();

		WorkflowDefinition postWorkflowDefinition =
			testPostWorkflowDefinition_addWorkflowDefinition(
				randomWorkflowDefinition);

		assertEquals(randomWorkflowDefinition, postWorkflowDefinition);
		assertValid(postWorkflowDefinition);
	}

	protected WorkflowDefinition
			testPostWorkflowDefinition_addWorkflowDefinition(
				WorkflowDefinition workflowDefinition)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostWorkflowDefinition() throws Exception {
		WorkflowDefinition randomWorkflowDefinition =
			randomWorkflowDefinition();

		WorkflowDefinition workflowDefinition =
			testGraphQLWorkflowDefinition_addWorkflowDefinition(
				randomWorkflowDefinition);

		Assert.assertTrue(equals(randomWorkflowDefinition, workflowDefinition));
	}

	@Test
	public void testPostWorkflowDefinitionDeploy() throws Exception {
		WorkflowDefinition randomWorkflowDefinition =
			randomWorkflowDefinition();

		WorkflowDefinition postWorkflowDefinition =
			testPostWorkflowDefinitionDeploy_addWorkflowDefinition(
				randomWorkflowDefinition);

		assertEquals(randomWorkflowDefinition, postWorkflowDefinition);
		assertValid(postWorkflowDefinition);
	}

	protected WorkflowDefinition
			testPostWorkflowDefinitionDeploy_addWorkflowDefinition(
				WorkflowDefinition workflowDefinition)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostWorkflowDefinitionDeploy() throws Exception {
		WorkflowDefinition randomWorkflowDefinition =
			randomWorkflowDefinition();

		WorkflowDefinition workflowDefinition =
			testGraphQLWorkflowDefinition_addWorkflowDefinition(
				randomWorkflowDefinition);

		Assert.assertTrue(equals(randomWorkflowDefinition, workflowDefinition));
	}

	@Test
	public void testPostWorkflowDefinitionSave() throws Exception {
		WorkflowDefinition randomWorkflowDefinition =
			randomWorkflowDefinition();

		WorkflowDefinition postWorkflowDefinition =
			testPostWorkflowDefinitionSave_addWorkflowDefinition(
				randomWorkflowDefinition);

		assertEquals(randomWorkflowDefinition, postWorkflowDefinition);
		assertValid(postWorkflowDefinition);
	}

	protected WorkflowDefinition
			testPostWorkflowDefinitionSave_addWorkflowDefinition(
				WorkflowDefinition workflowDefinition)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostWorkflowDefinitionSave() throws Exception {
		WorkflowDefinition randomWorkflowDefinition =
			randomWorkflowDefinition();

		WorkflowDefinition workflowDefinition =
			testGraphQLWorkflowDefinition_addWorkflowDefinition(
				randomWorkflowDefinition);

		Assert.assertTrue(equals(randomWorkflowDefinition, workflowDefinition));
	}

	@Test
	public void testPostWorkflowDefinitionUpdateActive() throws Exception {
		WorkflowDefinition randomWorkflowDefinition =
			randomWorkflowDefinition();

		WorkflowDefinition postWorkflowDefinition =
			testPostWorkflowDefinitionUpdateActive_addWorkflowDefinition(
				randomWorkflowDefinition);

		assertEquals(randomWorkflowDefinition, postWorkflowDefinition);
		assertValid(postWorkflowDefinition);
	}

	protected WorkflowDefinition
			testPostWorkflowDefinitionUpdateActive_addWorkflowDefinition(
				WorkflowDefinition workflowDefinition)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostWorkflowDefinitionUpdateActive()
		throws Exception {

		WorkflowDefinition randomWorkflowDefinition =
			randomWorkflowDefinition();

		WorkflowDefinition workflowDefinition =
			testGraphQLWorkflowDefinition_addWorkflowDefinition(
				randomWorkflowDefinition);

		Assert.assertTrue(equals(randomWorkflowDefinition, workflowDefinition));
	}

	@Test
	public void testPutWorkflowDefinition() throws Exception {
		WorkflowDefinition postWorkflowDefinition =
			testPutWorkflowDefinition_addWorkflowDefinition();

		WorkflowDefinition randomWorkflowDefinition =
			randomWorkflowDefinition();

		WorkflowDefinition putWorkflowDefinition =
			workflowDefinitionResource.putWorkflowDefinition(
				postWorkflowDefinition.getId(), randomWorkflowDefinition);

		assertEquals(randomWorkflowDefinition, putWorkflowDefinition);
		assertValid(putWorkflowDefinition);

		WorkflowDefinition getWorkflowDefinition =
			workflowDefinitionResource.getWorkflowDefinition(
				putWorkflowDefinition.getId());

		assertEquals(randomWorkflowDefinition, getWorkflowDefinition);
		assertValid(getWorkflowDefinition);
	}

	protected WorkflowDefinition
			testPutWorkflowDefinition_addWorkflowDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		WorkflowDefinition workflowDefinition1 =
			testBatchEngineDeleteImportTask_addWorkflowDefinition();

		testBatchEngineDeleteImportTask_deleteWorkflowDefinition(
			200, null, workflowDefinition1.getId());

		assertHttpResponseStatusCode(
			404,
			workflowDefinitionResource.getWorkflowDefinitionHttpResponse(
				workflowDefinition1.getId()));
	}

	protected WorkflowDefinition
			testBatchEngineDeleteImportTask_addWorkflowDefinition()
		throws Exception {

		return testDeleteWorkflowDefinition_addWorkflowDefinition();
	}

	protected void testBatchEngineDeleteImportTask_deleteWorkflowDefinition(
			int expectedStatusCode, String externalReferenceCode, Long id,
			String... parameters)
		throws Exception {

		ImportTaskResource importTaskResource = ImportTaskResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).parameters(
			parameters
		).build();

		HttpResponse httpResponse =
			importTaskResource.deleteImportTaskHttpResponse(
				"com.liferay.headless.admin.workflow.dto.v1_0.WorkflowDefinition",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"id", () -> id
					)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		if (expectedStatusCode == 200) {
			waitForFinish(
				"COMPLETED",
				JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
		}
	}

	protected WorkflowDefinition
			testGraphQLWorkflowDefinition_addWorkflowDefinition()
		throws Exception {

		return testGraphQLWorkflowDefinition_addWorkflowDefinition(
			randomWorkflowDefinition());
	}

	protected WorkflowDefinition
			testGraphQLWorkflowDefinition_addWorkflowDefinition(
				WorkflowDefinition workflowDefinition)
		throws Exception {

		JSONDeserializer<WorkflowDefinition> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(WorkflowDefinition.class)) {

			if (getGraphQLValue(field.get(workflowDefinition)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(workflowDefinition)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createWorkflowDefinition",
						new HashMap<String, Object>() {
							{
								put("workflowDefinition", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createWorkflowDefinition"),
			WorkflowDefinition.class);
	}

	protected String getGraphQLValue(Object value) throws Exception {
		if (value == null) {
			return null;
		}
		else if (value instanceof Boolean || value instanceof Number) {
			return value.toString();
		}
		else if (value instanceof Date date) {
			return "\"" +
				DateUtil.getDate(
					date, "yyyy-MM-dd'T'HH:mm:ss'Z'", LocaleUtil.getDefault(),
					TimeZone.getTimeZone("UTC")) + "\"";
		}
		else if (value instanceof Enum<?> enm) {
			return enm.name();
		}
		else if (value instanceof Map<?, ?> map) {
			List<String> entries = new ArrayList<>();

			for (Map.Entry<?, ?> entry : map.entrySet()) {
				String graphQLValue = getGraphQLValue(entry.getValue());

				if (graphQLValue != null) {
					entries.add(entry.getKey() + ": " + graphQLValue);
				}
			}

			return "{" + String.join(", ", entries) + "}";
		}
		else if (value instanceof Object[] array) {
			List<String> entries = new ArrayList<>();

			for (Object entry : array) {
				String graphQLValue = getGraphQLValue(entry);

				if (graphQLValue != null) {
					entries.add(graphQLValue);
				}
			}

			return "[" + String.join(", ", entries) + "]";
		}
		else if (value instanceof String) {
			return "\"" + value + "\"";
		}
		else {
			List<String> entries = new ArrayList<>();

			Class<?> clazz = value.getClass();
			java.lang.reflect.Field[] declaredFields = getDeclaredFields(clazz);

			if (declaredFields.length == 0) {
				declaredFields = getDeclaredFields(clazz.getSuperclass());
			}

			for (java.lang.reflect.Field field : declaredFields) {
				String graphQLValue = getGraphQLValue(field.get(value));

				if (graphQLValue != null) {
					entries.add(field.getName() + ": " + graphQLValue);
				}
			}

			return "{" + String.join(", ", entries) + "}";
		}
	}

	protected void assertContains(
		WorkflowDefinition workflowDefinition,
		List<WorkflowDefinition> workflowDefinitions) {

		boolean contains = false;

		for (WorkflowDefinition item : workflowDefinitions) {
			if (equals(workflowDefinition, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			workflowDefinitions + " does not contain " + workflowDefinition,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		WorkflowDefinition workflowDefinition1,
		WorkflowDefinition workflowDefinition2) {

		Assert.assertTrue(
			workflowDefinition1 + " does not equal " + workflowDefinition2,
			equals(workflowDefinition1, workflowDefinition2));
	}

	protected void assertEquals(
		List<WorkflowDefinition> workflowDefinitions1,
		List<WorkflowDefinition> workflowDefinitions2) {

		Assert.assertEquals(
			workflowDefinitions1.size(), workflowDefinitions2.size());

		for (int i = 0; i < workflowDefinitions1.size(); i++) {
			WorkflowDefinition workflowDefinition1 = workflowDefinitions1.get(
				i);
			WorkflowDefinition workflowDefinition2 = workflowDefinitions2.get(
				i);

			assertEquals(workflowDefinition1, workflowDefinition2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<WorkflowDefinition> workflowDefinitions1,
		List<WorkflowDefinition> workflowDefinitions2) {

		Assert.assertEquals(
			workflowDefinitions1.size(), workflowDefinitions2.size());

		for (WorkflowDefinition workflowDefinition1 : workflowDefinitions1) {
			boolean contains = false;

			for (WorkflowDefinition workflowDefinition2 :
					workflowDefinitions2) {

				if (equals(workflowDefinition1, workflowDefinition2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				workflowDefinitions2 + " does not contain " +
					workflowDefinition1,
				contains);
		}
	}

	protected void assertValid(WorkflowDefinition workflowDefinition)
		throws Exception {

		boolean valid = true;

		if (workflowDefinition.getDateCreated() == null) {
			valid = false;
		}

		if (workflowDefinition.getDateModified() == null) {
			valid = false;
		}

		if (workflowDefinition.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (workflowDefinition.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (workflowDefinition.getActive() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("content", additionalAssertFieldName)) {
				if (workflowDefinition.getContent() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (workflowDefinition.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (workflowDefinition.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (workflowDefinition.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (workflowDefinition.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("nodes", additionalAssertFieldName)) {
				if (workflowDefinition.getNodes() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (workflowDefinition.getTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title_i18n", additionalAssertFieldName)) {
				if (workflowDefinition.getTitle_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("transitions", additionalAssertFieldName)) {
				if (workflowDefinition.getTransitions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("version", additionalAssertFieldName)) {
				if (workflowDefinition.getVersion() == null) {
					valid = false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		Assert.assertTrue(valid);
	}

	protected void assertValid(Page<WorkflowDefinition> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<WorkflowDefinition> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<WorkflowDefinition> workflowDefinitions =
			page.getItems();

		int size = workflowDefinitions.size();

		if ((page.getLastPage() > 0) && (page.getPage() > 0) &&
			(page.getPageSize() > 0) && (page.getTotalCount() > 0) &&
			(size > 0)) {

			valid = true;
		}

		Assert.assertTrue(valid);

		assertValid(page.getActions(), expectedActions);
	}

	protected void assertValid(
		Map<String, Map<String, String>> actions1,
		Map<String, Map<String, String>> actions2) {

		for (String key : actions2.keySet()) {
			Map action = actions1.get(key);

			Assert.assertNotNull(key + " does not contain an action", action);

			Map<String, String> expectedAction = actions2.get(key);

			Assert.assertEquals(
				expectedAction.get("method"), action.get("method"));
			Assert.assertEquals(expectedAction.get("href"), action.get("href"));
		}
	}

	protected String[] getAdditionalAssertFieldNames() {
		return new String[0];
	}

	protected List<GraphQLField> getGraphQLFields() throws Exception {
		List<GraphQLField> graphQLFields = new ArrayList<>();

		graphQLFields.add(new GraphQLField("externalReferenceCode"));

		graphQLFields.add(new GraphQLField("id"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.admin.workflow.dto.v1_0.
						WorkflowDefinition.class)) {

			if (!ArrayUtil.contains(
					getAdditionalAssertFieldNames(), field.getName())) {

				continue;
			}

			graphQLFields.addAll(getGraphQLFields(field));
		}

		return graphQLFields;
	}

	protected List<GraphQLField> getGraphQLFields(
			java.lang.reflect.Field... fields)
		throws Exception {

		List<GraphQLField> graphQLFields = new ArrayList<>();

		for (java.lang.reflect.Field field : fields) {
			com.liferay.portal.vulcan.graphql.annotation.GraphQLField
				vulcanGraphQLField = field.getAnnotation(
					com.liferay.portal.vulcan.graphql.annotation.GraphQLField.
						class);

			if (vulcanGraphQLField != null) {
				Class<?> clazz = field.getType();

				if (clazz.isArray()) {
					clazz = clazz.getComponentType();
				}

				List<GraphQLField> childrenGraphQLFields = getGraphQLFields(
					getDeclaredFields(clazz));

				graphQLFields.add(
					new GraphQLField(field.getName(), childrenGraphQLFields));
			}
		}

		return graphQLFields;
	}

	protected String[] getIgnoredEntityFieldNames() {
		return new String[0];
	}

	protected boolean equals(
		WorkflowDefinition workflowDefinition1,
		WorkflowDefinition workflowDefinition2) {

		if (workflowDefinition1 == workflowDefinition2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)workflowDefinition1.getActions(),
						(Map)workflowDefinition2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowDefinition1.getActive(),
						workflowDefinition2.getActive())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("content", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowDefinition1.getContent(),
						workflowDefinition2.getContent())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowDefinition1.getCreator(),
						workflowDefinition2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowDefinition1.getDateCreated(),
						workflowDefinition2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowDefinition1.getDateModified(),
						workflowDefinition2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowDefinition1.getDescription(),
						workflowDefinition2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						workflowDefinition1.getExternalReferenceCode(),
						workflowDefinition2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowDefinition1.getId(),
						workflowDefinition2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowDefinition1.getName(),
						workflowDefinition2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("nodes", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowDefinition1.getNodes(),
						workflowDefinition2.getNodes())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowDefinition1.getTitle(),
						workflowDefinition2.getTitle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)workflowDefinition1.getTitle_i18n(),
						(Map)workflowDefinition2.getTitle_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("transitions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowDefinition1.getTransitions(),
						workflowDefinition2.getTransitions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("version", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowDefinition1.getVersion(),
						workflowDefinition2.getVersion())) {

					return false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		return true;
	}

	protected boolean equals(
		Map<String, Object> map1, Map<String, Object> map2) {

		if (Objects.equals(map1.keySet(), map2.keySet())) {
			for (Map.Entry<String, Object> entry : map1.entrySet()) {
				if (entry.getValue() instanceof Map) {
					if (!equals(
							(Map)entry.getValue(),
							(Map)map2.get(entry.getKey()))) {

						return false;
					}
				}
				else if (!Objects.deepEquals(
							entry.getValue(), map2.get(entry.getKey()))) {

					return false;
				}
			}

			return true;
		}

		return false;
	}

	protected java.lang.reflect.Field[] getDeclaredFields(Class clazz)
		throws Exception {

		if (clazz.getClassLoader() == null) {
			return new java.lang.reflect.Field[0];
		}

		return TransformUtil.transform(
			ReflectionUtil.getDeclaredFields(clazz),
			field -> {
				if (field.isSynthetic()) {
					return null;
				}

				return field;
			},
			java.lang.reflect.Field.class);
	}

	protected java.util.Collection<EntityField> getEntityFields()
		throws Exception {

		if (!(_workflowDefinitionResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_workflowDefinitionResource;

		EntityModel entityModel = entityModelResource.getEntityModel(
			new MultivaluedHashMap());

		if (entityModel == null) {
			return Collections.emptyList();
		}

		Map<String, EntityField> entityFieldsMap =
			entityModel.getEntityFieldsMap();

		return entityFieldsMap.values();
	}

	protected List<EntityField> getEntityFields(EntityField.Type type)
		throws Exception {

		return TransformUtil.transform(
			getEntityFields(),
			entityField -> {
				if (!Objects.equals(entityField.getType(), type) ||
					ArrayUtil.contains(
						getIgnoredEntityFieldNames(), entityField.getName())) {

					return null;
				}

				return entityField;
			});
	}

	protected String getFilterString(
		EntityField entityField, String operator,
		WorkflowDefinition workflowDefinition) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("actions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("active")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("content")) {
			Object object = workflowDefinition.getContent();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = workflowDefinition.getDateCreated();

				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(_format.format(date.getTime() - (2 * Time.SECOND)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(_format.format(date.getTime() + (2 * Time.SECOND)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(_format.format(workflowDefinition.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = workflowDefinition.getDateModified();

				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(_format.format(date.getTime() - (2 * Time.SECOND)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(_format.format(date.getTime() + (2 * Time.SECOND)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(_format.format(workflowDefinition.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = workflowDefinition.getDescription();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = workflowDefinition.getExternalReferenceCode();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("name")) {
			Object object = workflowDefinition.getName();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("nodes")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("title")) {
			Object object = workflowDefinition.getTitle();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("title_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("transitions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("version")) {
			Object object = workflowDefinition.getVersion();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		throw new IllegalArgumentException(
			"Invalid entity field " + entityFieldName);
	}

	protected String invoke(String query) throws Exception {
		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.body(
			JSONUtil.put(
				"query", query
			).toString(),
			"application/json");
		httpInvoker.httpMethod(HttpInvoker.HttpMethod.POST);
		httpInvoker.path("http://localhost:8080/o/graphql");
		httpInvoker.userNameAndPassword(
			"test@liferay.com:" + PropsValues.DEFAULT_ADMIN_PASSWORD);

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		return httpResponse.getContent();
	}

	protected JSONObject invokeGraphQLMutation(GraphQLField graphQLField)
		throws Exception {

		GraphQLField mutationGraphQLField = new GraphQLField(
			"mutation", graphQLField);

		return JSONFactoryUtil.createJSONObject(
			invoke(mutationGraphQLField.toString()));
	}

	protected JSONObject invokeGraphQLQuery(GraphQLField graphQLField)
		throws Exception {

		GraphQLField queryGraphQLField = new GraphQLField(
			"query", graphQLField);

		return JSONFactoryUtil.createJSONObject(
			invoke(queryGraphQLField.toString()));
	}

	protected WorkflowDefinition randomWorkflowDefinition() throws Exception {
		return new WorkflowDefinition() {
			{
				active = RandomTestUtil.randomBoolean();
				content = StringUtil.toLowerCase(RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
				version = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected WorkflowDefinition randomIrrelevantWorkflowDefinition()
		throws Exception {

		WorkflowDefinition randomIrrelevantWorkflowDefinition =
			randomWorkflowDefinition();

		return randomIrrelevantWorkflowDefinition;
	}

	protected WorkflowDefinition randomPatchWorkflowDefinition()
		throws Exception {

		return randomWorkflowDefinition();
	}

	protected final JSONObject waitForFinish(
			String expectedExecuteStatus, JSONObject jsonObject)
		throws Exception {

		while (true) {
			ImportTask importTask = importTaskResource.getImportTask(
				jsonObject.getLong("id"));

			ImportTask.ExecuteStatus executeStatus =
				importTask.getExecuteStatus();

			if (StringUtil.equals(executeStatus.getValue(), "COMPLETED") ||
				StringUtil.equals(executeStatus.getValue(), "FAILED")) {

				Assert.assertEquals(
					expectedExecuteStatus, executeStatus.getValue());

				return jsonObject;
			}
		}
	}

	protected WorkflowDefinitionResource workflowDefinitionResource;
	protected ImportTaskResource importTaskResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected com.liferay.portal.kernel.model.Company testCompany;
	protected com.liferay.portal.kernel.model.Group testGroup;

	protected static class BeanTestUtil {

		public static void copyProperties(Object source, Object target)
			throws Exception {

			Class<?> sourceClass = source.getClass();

			Class<?> targetClass = target.getClass();

			for (java.lang.reflect.Field field :
					_getAllDeclaredFields(sourceClass)) {

				if (field.isSynthetic()) {
					continue;
				}

				Method getMethod = _getMethod(
					sourceClass, field.getName(), "get");

				try {
					Method setMethod = _getMethod(
						targetClass, field.getName(), "set",
						getMethod.getReturnType());

					setMethod.invoke(target, getMethod.invoke(source));
				}
				catch (Exception e) {
					continue;
				}
			}
		}

		public static boolean hasProperty(Object bean, String name) {
			Method setMethod = _getMethod(
				bean.getClass(), "set" + StringUtil.upperCaseFirstLetter(name));

			if (setMethod != null) {
				return true;
			}

			return false;
		}

		public static void setProperty(Object bean, String name, Object value)
			throws Exception {

			Class<?> clazz = bean.getClass();

			Method setMethod = _getMethod(
				clazz, "set" + StringUtil.upperCaseFirstLetter(name));

			if (setMethod == null) {
				throw new NoSuchMethodException();
			}

			Class<?>[] parameterTypes = setMethod.getParameterTypes();

			setMethod.invoke(bean, _translateValue(parameterTypes[0], value));
		}

		private static List<java.lang.reflect.Field> _getAllDeclaredFields(
			Class<?> clazz) {

			List<java.lang.reflect.Field> fields = new ArrayList<>();

			while ((clazz != null) && (clazz != Object.class)) {
				for (java.lang.reflect.Field field :
						clazz.getDeclaredFields()) {

					fields.add(field);
				}

				clazz = clazz.getSuperclass();
			}

			return fields;
		}

		private static Method _getMethod(Class<?> clazz, String name) {
			for (Method method : clazz.getMethods()) {
				if (name.equals(method.getName()) &&
					(method.getParameterCount() == 1) &&
					_parameterTypes.contains(method.getParameterTypes()[0])) {

					return method;
				}
			}

			return null;
		}

		private static Method _getMethod(
				Class<?> clazz, String fieldName, String prefix,
				Class<?>... parameterTypes)
			throws Exception {

			return clazz.getMethod(
				prefix + StringUtil.upperCaseFirstLetter(fieldName),
				parameterTypes);
		}

		private static Object _translateValue(
			Class<?> parameterType, Object value) {

			if ((value instanceof Integer) &&
				parameterType.equals(Long.class)) {

				Integer intValue = (Integer)value;

				return intValue.longValue();
			}

			return value;
		}

		private static final Set<Class<?>> _parameterTypes = new HashSet<>(
			Arrays.asList(
				Boolean.class, Date.class, Double.class, Integer.class,
				Long.class, Map.class, String.class));

	}

	protected class GraphQLField {

		public GraphQLField(String key, GraphQLField... graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(String key, List<GraphQLField> graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			GraphQLField... graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = Arrays.asList(graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			List<GraphQLField> graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = graphQLFields;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(_key);

			if (!_parameterMap.isEmpty()) {
				sb.append("(");

				for (Map.Entry<String, Object> entry :
						_parameterMap.entrySet()) {

					sb.append(entry.getKey());
					sb.append(": ");
					sb.append(entry.getValue());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append(")");
			}

			if (!_graphQLFields.isEmpty()) {
				sb.append("{");

				for (GraphQLField graphQLField : _graphQLFields) {
					sb.append(graphQLField.toString());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append("}");
			}

			return sb.toString();
		}

		private final List<GraphQLField> _graphQLFields;
		private final String _key;
		private final Map<String, Object> _parameterMap;

	}

	private static final com.liferay.portal.kernel.log.Log _log =
		LogFactoryUtil.getLog(BaseWorkflowDefinitionResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.admin.workflow.resource.v1_0.
			WorkflowDefinitionResource _workflowDefinitionResource;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private ScopeChecker _scopeChecker;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private VulcanCRUDItemDelegateBuilderRegistry
		_vulcanCRUDItemDelegateBuilderRegistry;

}