/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectAction;
import com.liferay.object.admin.rest.client.http.HttpInvoker;
import com.liferay.object.admin.rest.client.pagination.Page;
import com.liferay.object.admin.rest.client.pagination.Pagination;
import com.liferay.object.admin.rest.client.resource.v1_0.ObjectActionResource;
import com.liferay.object.admin.rest.client.serdes.v1_0.ObjectActionSerDes;
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
public abstract class BaseObjectActionResourceTestCase {

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

		_objectActionResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		objectActionResource = ObjectActionResource.builder(
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

		ObjectAction objectAction1 = randomObjectAction();

		String json = objectMapper.writeValueAsString(objectAction1);

		ObjectAction objectAction2 = ObjectActionSerDes.toDTO(json);

		Assert.assertTrue(equals(objectAction1, objectAction2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ObjectAction objectAction = randomObjectAction();

		String json1 = objectMapper.writeValueAsString(objectAction);
		String json2 = ObjectActionSerDes.toJSON(objectAction);

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

		ObjectAction objectAction = randomObjectAction();

		objectAction.setConditionExpression(regex);
		objectAction.setDescription(regex);
		objectAction.setExternalReferenceCode(regex);
		objectAction.setName(regex);
		objectAction.setObjectActionExecutorKey(regex);
		objectAction.setObjectActionTriggerKey(regex);

		String json = ObjectActionSerDes.toJSON(objectAction);

		Assert.assertFalse(json.contains(regex));

		objectAction = ObjectActionSerDes.toDTO(json);

		Assert.assertEquals(regex, objectAction.getConditionExpression());
		Assert.assertEquals(regex, objectAction.getDescription());
		Assert.assertEquals(regex, objectAction.getExternalReferenceCode());
		Assert.assertEquals(regex, objectAction.getName());
		Assert.assertEquals(regex, objectAction.getObjectActionExecutorKey());
		Assert.assertEquals(regex, objectAction.getObjectActionTriggerKey());
	}

	@Test
	public void testDeleteObjectAction() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ObjectAction objectAction = testDeleteObjectAction_addObjectAction();

		assertHttpResponseStatusCode(
			204,
			objectActionResource.deleteObjectActionHttpResponse(
				objectAction.getId()));

		assertHttpResponseStatusCode(
			404,
			objectActionResource.getObjectActionHttpResponse(
				objectAction.getId()));
		assertHttpResponseStatusCode(
			404, objectActionResource.getObjectActionHttpResponse(0L));
	}

	protected ObjectAction testDeleteObjectAction_addObjectAction()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteObjectAction() throws Exception {

		// No namespace

		ObjectAction objectAction1 =
			testGraphQLDeleteObjectAction_addObjectAction();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteObjectAction",
						new HashMap<String, Object>() {
							{
								put("objectActionId", objectAction1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteObjectAction"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"objectAction",
					new HashMap<String, Object>() {
						{
							put("objectActionId", objectAction1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace objectAdmin_v1_0

		ObjectAction objectAction2 =
			testGraphQLDeleteObjectAction_addObjectAction();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"objectAdmin_v1_0",
						new GraphQLField(
							"deleteObjectAction",
							new HashMap<String, Object>() {
								{
									put(
										"objectActionId",
										objectAction2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/objectAdmin_v1_0",
				"Object/deleteObjectAction"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"objectAdmin_v1_0",
					new GraphQLField(
						"objectAction",
						new HashMap<String, Object>() {
							{
								put("objectActionId", objectAction2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ObjectAction testGraphQLDeleteObjectAction_addObjectAction()
		throws Exception {

		return testGraphQLObjectAction_addObjectAction();
	}

	@Test
	public void testDeleteObjectActionBatch() throws Exception {
		ObjectAction objectAction1 =
			testDeleteObjectActionBatch_addObjectAction();

		testDeleteObjectActionBatch_deleteObjectAction(
			202, null, objectAction1.getId());

		assertHttpResponseStatusCode(
			404,
			objectActionResource.getObjectActionHttpResponse(
				objectAction1.getId()));
	}

	protected ObjectAction testDeleteObjectActionBatch_addObjectAction()
		throws Exception {

		return testDeleteObjectAction_addObjectAction();
	}

	protected void testDeleteObjectActionBatch_deleteObjectAction(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			objectActionResource.deleteObjectActionBatchHttpResponse(
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
	public void testGetObjectAction() throws Exception {
		ObjectAction postObjectAction = testGetObjectAction_addObjectAction();

		ObjectAction getObjectAction = objectActionResource.getObjectAction(
			postObjectAction.getId());

		assertEquals(postObjectAction, getObjectAction);
		assertValid(getObjectAction);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		ObjectAction postObjectAction = testGetObjectAction_addObjectAction();

		ObjectAction getObjectAction = objectActionResource.getObjectAction(
			postObjectAction.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.object.admin.rest.dto.v1_0.ObjectAction"
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

		Object item = vulcanCRUDItemDelegate.getItem(postObjectAction.getId());

		assertEquals(
			getObjectAction, ObjectActionSerDes.toDTO(item.toString()));
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

	protected ObjectAction testGetObjectAction_addObjectAction()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetObjectAction() throws Exception {
		ObjectAction objectAction =
			testGraphQLGetObjectAction_addObjectAction();

		// No namespace

		Assert.assertTrue(
			equals(
				objectAction,
				ObjectActionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"objectAction",
								new HashMap<String, Object>() {
									{
										put(
											"objectActionId",
											objectAction.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/objectAction"))));

		// Using the namespace objectAdmin_v1_0

		Assert.assertTrue(
			equals(
				objectAction,
				ObjectActionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"objectAdmin_v1_0",
								new GraphQLField(
									"objectAction",
									new HashMap<String, Object>() {
										{
											put(
												"objectActionId",
												objectAction.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/objectAdmin_v1_0",
						"Object/objectAction"))));
	}

	@Test
	public void testGraphQLGetObjectActionNotFound() throws Exception {
		Long irrelevantObjectActionId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"objectAction",
						new HashMap<String, Object>() {
							{
								put("objectActionId", irrelevantObjectActionId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace objectAdmin_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"objectAdmin_v1_0",
						new GraphQLField(
							"objectAction",
							new HashMap<String, Object>() {
								{
									put(
										"objectActionId",
										irrelevantObjectActionId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ObjectAction testGraphQLGetObjectAction_addObjectAction()
		throws Exception {

		return testGraphQLObjectAction_addObjectAction();
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectActionsPage()
		throws Exception {

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectActionsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectActionsPage_getIrrelevantExternalReferenceCode();

		Page<ObjectAction> page =
			objectActionResource.
				getObjectDefinitionByExternalReferenceCodeObjectActionsPage(
					externalReferenceCode, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			ObjectAction irrelevantObjectAction =
				testGetObjectDefinitionByExternalReferenceCodeObjectActionsPage_addObjectAction(
					irrelevantExternalReferenceCode,
					randomIrrelevantObjectAction());

			page =
				objectActionResource.
					getObjectDefinitionByExternalReferenceCodeObjectActionsPage(
						irrelevantExternalReferenceCode, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantObjectAction, (List<ObjectAction>)page.getItems());
			assertValid(
				page,
				testGetObjectDefinitionByExternalReferenceCodeObjectActionsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		ObjectAction objectAction1 =
			testGetObjectDefinitionByExternalReferenceCodeObjectActionsPage_addObjectAction(
				externalReferenceCode, randomObjectAction());

		ObjectAction objectAction2 =
			testGetObjectDefinitionByExternalReferenceCodeObjectActionsPage_addObjectAction(
				externalReferenceCode, randomObjectAction());

		page =
			objectActionResource.
				getObjectDefinitionByExternalReferenceCodeObjectActionsPage(
					externalReferenceCode, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(objectAction1, (List<ObjectAction>)page.getItems());
		assertContains(objectAction2, (List<ObjectAction>)page.getItems());
		assertValid(
			page,
			testGetObjectDefinitionByExternalReferenceCodeObjectActionsPage_getExpectedActions(
				externalReferenceCode));

		objectActionResource.deleteObjectAction(objectAction1.getId());

		objectActionResource.deleteObjectAction(objectAction2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetObjectDefinitionByExternalReferenceCodeObjectActionsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectActionsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectActionsPage_getExternalReferenceCode();

		Page<ObjectAction> objectActionsPage =
			objectActionResource.
				getObjectDefinitionByExternalReferenceCodeObjectActionsPage(
					externalReferenceCode, null, null, null);

		int totalCount = GetterUtil.getInteger(
			objectActionsPage.getTotalCount());

		ObjectAction objectAction1 =
			testGetObjectDefinitionByExternalReferenceCodeObjectActionsPage_addObjectAction(
				externalReferenceCode, randomObjectAction());

		ObjectAction objectAction2 =
			testGetObjectDefinitionByExternalReferenceCodeObjectActionsPage_addObjectAction(
				externalReferenceCode, randomObjectAction());

		ObjectAction objectAction3 =
			testGetObjectDefinitionByExternalReferenceCodeObjectActionsPage_addObjectAction(
				externalReferenceCode, randomObjectAction());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ObjectAction> page1 =
				objectActionResource.
					getObjectDefinitionByExternalReferenceCodeObjectActionsPage(
						externalReferenceCode, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(objectAction1, (List<ObjectAction>)page1.getItems());

			Page<ObjectAction> page2 =
				objectActionResource.
					getObjectDefinitionByExternalReferenceCodeObjectActionsPage(
						externalReferenceCode, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(objectAction2, (List<ObjectAction>)page2.getItems());

			Page<ObjectAction> page3 =
				objectActionResource.
					getObjectDefinitionByExternalReferenceCodeObjectActionsPage(
						externalReferenceCode, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(objectAction3, (List<ObjectAction>)page3.getItems());
		}
		else {
			Page<ObjectAction> page1 =
				objectActionResource.
					getObjectDefinitionByExternalReferenceCodeObjectActionsPage(
						externalReferenceCode, null,
						Pagination.of(1, totalCount + 2), null);

			List<ObjectAction> objectActions1 =
				(List<ObjectAction>)page1.getItems();

			Assert.assertEquals(
				objectActions1.toString(), totalCount + 2,
				objectActions1.size());

			Page<ObjectAction> page2 =
				objectActionResource.
					getObjectDefinitionByExternalReferenceCodeObjectActionsPage(
						externalReferenceCode, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ObjectAction> objectActions2 =
				(List<ObjectAction>)page2.getItems();

			Assert.assertEquals(
				objectActions2.toString(), 1, objectActions2.size());

			Page<ObjectAction> page3 =
				objectActionResource.
					getObjectDefinitionByExternalReferenceCodeObjectActionsPage(
						externalReferenceCode, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(objectAction1, (List<ObjectAction>)page3.getItems());
			assertContains(objectAction2, (List<ObjectAction>)page3.getItems());
			assertContains(objectAction3, (List<ObjectAction>)page3.getItems());
		}
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectActionsPageWithSortDateTime()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectActionsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, objectAction1, objectAction2) -> {
				BeanTestUtil.setProperty(
					objectAction1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectActionsPageWithSortDouble()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectActionsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, objectAction1, objectAction2) -> {
				BeanTestUtil.setProperty(
					objectAction1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					objectAction2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectActionsPageWithSortInteger()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectActionsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, objectAction1, objectAction2) -> {
				BeanTestUtil.setProperty(
					objectAction1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					objectAction2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectActionsPageWithSortString()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectActionsPageWithSort(
			EntityField.Type.STRING,
			(entityField, objectAction1, objectAction2) -> {
				Class<?> clazz = objectAction1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						objectAction1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						objectAction2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						objectAction1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						objectAction2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						objectAction1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						objectAction2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetObjectDefinitionByExternalReferenceCodeObjectActionsPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, ObjectAction, ObjectAction, Exception>
						unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectActionsPage_getExternalReferenceCode();

		ObjectAction objectAction1 = randomObjectAction();
		ObjectAction objectAction2 = randomObjectAction();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, objectAction1, objectAction2);
		}

		objectAction1 =
			testGetObjectDefinitionByExternalReferenceCodeObjectActionsPage_addObjectAction(
				externalReferenceCode, objectAction1);

		objectAction2 =
			testGetObjectDefinitionByExternalReferenceCodeObjectActionsPage_addObjectAction(
				externalReferenceCode, objectAction2);

		Page<ObjectAction> page =
			objectActionResource.
				getObjectDefinitionByExternalReferenceCodeObjectActionsPage(
					externalReferenceCode, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ObjectAction> ascPage =
				objectActionResource.
					getObjectDefinitionByExternalReferenceCodeObjectActionsPage(
						externalReferenceCode, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				objectAction1, (List<ObjectAction>)ascPage.getItems());
			assertContains(
				objectAction2, (List<ObjectAction>)ascPage.getItems());

			Page<ObjectAction> descPage =
				objectActionResource.
					getObjectDefinitionByExternalReferenceCodeObjectActionsPage(
						externalReferenceCode, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				objectAction2, (List<ObjectAction>)descPage.getItems());
			assertContains(
				objectAction1, (List<ObjectAction>)descPage.getItems());
		}
	}

	protected ObjectAction
			testGetObjectDefinitionByExternalReferenceCodeObjectActionsPage_addObjectAction(
				String externalReferenceCode, ObjectAction objectAction)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetObjectDefinitionByExternalReferenceCodeObjectActionsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetObjectDefinitionByExternalReferenceCodeObjectActionsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectActionsPage()
		throws Exception {

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectActionsPage_getExternalReferenceCode();

		GraphQLField graphQLField = new GraphQLField(
			"objectDefinitionByExternalReferenceCodeObjectActions",
			new HashMap<String, Object>() {
				{
					put(
						"externalReferenceCode",
						"\"" + externalReferenceCode + "\"");
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject
			objectDefinitionByExternalReferenceCodeObjectActionsJSONObject =
				JSONUtil.getValueAsJSONObject(
					invokeGraphQLQuery(graphQLField), "JSONObject/data",
					"JSONObject/objectDefinitionByExternalReferenceCodeObjectActions");

		long totalCount =
			objectDefinitionByExternalReferenceCodeObjectActionsJSONObject.
				getLong("totalCount");

		ObjectAction objectAction1 =
			testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectActionsPageObjectDefinitionObjectAction_addObjectAction(
				externalReferenceCode, randomObjectAction());

		ObjectAction objectAction2 =
			testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectActionsPageObjectDefinitionObjectAction_addObjectAction(
				externalReferenceCode, randomObjectAction());

		objectDefinitionByExternalReferenceCodeObjectActionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/objectDefinitionByExternalReferenceCodeObjectActions");

		Assert.assertEquals(
			totalCount + 2,
			objectDefinitionByExternalReferenceCodeObjectActionsJSONObject.
				getLong("totalCount"));

		assertContains(
			objectAction1,
			Arrays.asList(
				ObjectActionSerDes.toDTOs(
					objectDefinitionByExternalReferenceCodeObjectActionsJSONObject.
						getString("items"))));
		assertContains(
			objectAction2,
			Arrays.asList(
				ObjectActionSerDes.toDTOs(
					objectDefinitionByExternalReferenceCodeObjectActionsJSONObject.
						getString("items"))));

		// Using the namespace objectAdmin_v1_0

		objectDefinitionByExternalReferenceCodeObjectActionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField("objectAdmin_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/objectAdmin_v1_0",
				"JSONObject/objectDefinitionByExternalReferenceCodeObjectActions");

		Assert.assertEquals(
			totalCount + 2,
			objectDefinitionByExternalReferenceCodeObjectActionsJSONObject.
				getLong("totalCount"));

		assertContains(
			objectAction1,
			Arrays.asList(
				ObjectActionSerDes.toDTOs(
					objectDefinitionByExternalReferenceCodeObjectActionsJSONObject.
						getString("items"))));
		assertContains(
			objectAction2,
			Arrays.asList(
				ObjectActionSerDes.toDTOs(
					objectDefinitionByExternalReferenceCodeObjectActionsJSONObject.
						getString("items"))));
	}

	protected ObjectAction
			testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectActionsPageObjectDefinitionObjectAction_addObjectAction(
				String externalReferenceCode, ObjectAction objectAction)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetObjectDefinitionObjectActionsPage() throws Exception {
		Long objectDefinitionId =
			testGetObjectDefinitionObjectActionsPage_getObjectDefinitionId();
		Long irrelevantObjectDefinitionId =
			testGetObjectDefinitionObjectActionsPage_getIrrelevantObjectDefinitionId();

		Page<ObjectAction> page =
			objectActionResource.getObjectDefinitionObjectActionsPage(
				objectDefinitionId, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantObjectDefinitionId != null) {
			ObjectAction irrelevantObjectAction =
				testGetObjectDefinitionObjectActionsPage_addObjectAction(
					irrelevantObjectDefinitionId,
					randomIrrelevantObjectAction());

			page = objectActionResource.getObjectDefinitionObjectActionsPage(
				irrelevantObjectDefinitionId, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantObjectAction, (List<ObjectAction>)page.getItems());
			assertValid(
				page,
				testGetObjectDefinitionObjectActionsPage_getExpectedActions(
					irrelevantObjectDefinitionId));
		}

		ObjectAction objectAction1 =
			testGetObjectDefinitionObjectActionsPage_addObjectAction(
				objectDefinitionId, randomObjectAction());

		ObjectAction objectAction2 =
			testGetObjectDefinitionObjectActionsPage_addObjectAction(
				objectDefinitionId, randomObjectAction());

		page = objectActionResource.getObjectDefinitionObjectActionsPage(
			objectDefinitionId, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(objectAction1, (List<ObjectAction>)page.getItems());
		assertContains(objectAction2, (List<ObjectAction>)page.getItems());
		assertValid(
			page,
			testGetObjectDefinitionObjectActionsPage_getExpectedActions(
				objectDefinitionId));

		objectActionResource.deleteObjectAction(objectAction1.getId());

		objectActionResource.deleteObjectAction(objectAction2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetObjectDefinitionObjectActionsPage_getExpectedActions(
				Long objectDefinitionId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/object-admin/v1.0/object-definitions/{objectDefinitionId}/object-actions/batch".
				replace(
					"{objectDefinitionId}",
					String.valueOf(objectDefinitionId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetObjectDefinitionObjectActionsPageWithPagination()
		throws Exception {

		Long objectDefinitionId =
			testGetObjectDefinitionObjectActionsPage_getObjectDefinitionId();

		Page<ObjectAction> objectActionsPage =
			objectActionResource.getObjectDefinitionObjectActionsPage(
				objectDefinitionId, null, null, null);

		int totalCount = GetterUtil.getInteger(
			objectActionsPage.getTotalCount());

		ObjectAction objectAction1 =
			testGetObjectDefinitionObjectActionsPage_addObjectAction(
				objectDefinitionId, randomObjectAction());

		ObjectAction objectAction2 =
			testGetObjectDefinitionObjectActionsPage_addObjectAction(
				objectDefinitionId, randomObjectAction());

		ObjectAction objectAction3 =
			testGetObjectDefinitionObjectActionsPage_addObjectAction(
				objectDefinitionId, randomObjectAction());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ObjectAction> page1 =
				objectActionResource.getObjectDefinitionObjectActionsPage(
					objectDefinitionId, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(objectAction1, (List<ObjectAction>)page1.getItems());

			Page<ObjectAction> page2 =
				objectActionResource.getObjectDefinitionObjectActionsPage(
					objectDefinitionId, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(objectAction2, (List<ObjectAction>)page2.getItems());

			Page<ObjectAction> page3 =
				objectActionResource.getObjectDefinitionObjectActionsPage(
					objectDefinitionId, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(objectAction3, (List<ObjectAction>)page3.getItems());
		}
		else {
			Page<ObjectAction> page1 =
				objectActionResource.getObjectDefinitionObjectActionsPage(
					objectDefinitionId, null, Pagination.of(1, totalCount + 2),
					null);

			List<ObjectAction> objectActions1 =
				(List<ObjectAction>)page1.getItems();

			Assert.assertEquals(
				objectActions1.toString(), totalCount + 2,
				objectActions1.size());

			Page<ObjectAction> page2 =
				objectActionResource.getObjectDefinitionObjectActionsPage(
					objectDefinitionId, null, Pagination.of(2, totalCount + 2),
					null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ObjectAction> objectActions2 =
				(List<ObjectAction>)page2.getItems();

			Assert.assertEquals(
				objectActions2.toString(), 1, objectActions2.size());

			Page<ObjectAction> page3 =
				objectActionResource.getObjectDefinitionObjectActionsPage(
					objectDefinitionId, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(objectAction1, (List<ObjectAction>)page3.getItems());
			assertContains(objectAction2, (List<ObjectAction>)page3.getItems());
			assertContains(objectAction3, (List<ObjectAction>)page3.getItems());
		}
	}

	@Test
	public void testGetObjectDefinitionObjectActionsPageWithSortDateTime()
		throws Exception {

		testGetObjectDefinitionObjectActionsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, objectAction1, objectAction2) -> {
				BeanTestUtil.setProperty(
					objectAction1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetObjectDefinitionObjectActionsPageWithSortDouble()
		throws Exception {

		testGetObjectDefinitionObjectActionsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, objectAction1, objectAction2) -> {
				BeanTestUtil.setProperty(
					objectAction1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					objectAction2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetObjectDefinitionObjectActionsPageWithSortInteger()
		throws Exception {

		testGetObjectDefinitionObjectActionsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, objectAction1, objectAction2) -> {
				BeanTestUtil.setProperty(
					objectAction1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					objectAction2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetObjectDefinitionObjectActionsPageWithSortString()
		throws Exception {

		testGetObjectDefinitionObjectActionsPageWithSort(
			EntityField.Type.STRING,
			(entityField, objectAction1, objectAction2) -> {
				Class<?> clazz = objectAction1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						objectAction1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						objectAction2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						objectAction1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						objectAction2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						objectAction1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						objectAction2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetObjectDefinitionObjectActionsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, ObjectAction, ObjectAction, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long objectDefinitionId =
			testGetObjectDefinitionObjectActionsPage_getObjectDefinitionId();

		ObjectAction objectAction1 = randomObjectAction();
		ObjectAction objectAction2 = randomObjectAction();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, objectAction1, objectAction2);
		}

		objectAction1 =
			testGetObjectDefinitionObjectActionsPage_addObjectAction(
				objectDefinitionId, objectAction1);

		objectAction2 =
			testGetObjectDefinitionObjectActionsPage_addObjectAction(
				objectDefinitionId, objectAction2);

		Page<ObjectAction> page =
			objectActionResource.getObjectDefinitionObjectActionsPage(
				objectDefinitionId, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ObjectAction> ascPage =
				objectActionResource.getObjectDefinitionObjectActionsPage(
					objectDefinitionId, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				objectAction1, (List<ObjectAction>)ascPage.getItems());
			assertContains(
				objectAction2, (List<ObjectAction>)ascPage.getItems());

			Page<ObjectAction> descPage =
				objectActionResource.getObjectDefinitionObjectActionsPage(
					objectDefinitionId, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				objectAction2, (List<ObjectAction>)descPage.getItems());
			assertContains(
				objectAction1, (List<ObjectAction>)descPage.getItems());
		}
	}

	protected ObjectAction
			testGetObjectDefinitionObjectActionsPage_addObjectAction(
				Long objectDefinitionId, ObjectAction objectAction)
		throws Exception {

		return objectActionResource.postObjectDefinitionObjectAction(
			objectDefinitionId, objectAction);
	}

	protected Long
			testGetObjectDefinitionObjectActionsPage_getObjectDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetObjectDefinitionObjectActionsPage_getIrrelevantObjectDefinitionId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetObjectDefinitionObjectActionsPage()
		throws Exception {

		Long objectDefinitionId =
			testGetObjectDefinitionObjectActionsPage_getObjectDefinitionId();

		GraphQLField graphQLField = new GraphQLField(
			"objectDefinitionObjectActions",
			new HashMap<String, Object>() {
				{
					put("objectDefinitionId", objectDefinitionId);
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject objectDefinitionObjectActionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/objectDefinitionObjectActions");

		long totalCount = objectDefinitionObjectActionsJSONObject.getLong(
			"totalCount");

		ObjectAction objectAction1 =
			testGraphQLObjectDefinitionObjectAction_addObjectAction(
				objectDefinitionId, randomObjectAction());

		ObjectAction objectAction2 =
			testGraphQLObjectDefinitionObjectAction_addObjectAction(
				objectDefinitionId, randomObjectAction());

		objectDefinitionObjectActionsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/objectDefinitionObjectActions");

		Assert.assertEquals(
			totalCount + 2,
			objectDefinitionObjectActionsJSONObject.getLong("totalCount"));

		assertContains(
			objectAction1,
			Arrays.asList(
				ObjectActionSerDes.toDTOs(
					objectDefinitionObjectActionsJSONObject.getString(
						"items"))));
		assertContains(
			objectAction2,
			Arrays.asList(
				ObjectActionSerDes.toDTOs(
					objectDefinitionObjectActionsJSONObject.getString(
						"items"))));

		// Using the namespace objectAdmin_v1_0

		objectDefinitionObjectActionsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("objectAdmin_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/objectAdmin_v1_0",
			"JSONObject/objectDefinitionObjectActions");

		Assert.assertEquals(
			totalCount + 2,
			objectDefinitionObjectActionsJSONObject.getLong("totalCount"));

		assertContains(
			objectAction1,
			Arrays.asList(
				ObjectActionSerDes.toDTOs(
					objectDefinitionObjectActionsJSONObject.getString(
						"items"))));
		assertContains(
			objectAction2,
			Arrays.asList(
				ObjectActionSerDes.toDTOs(
					objectDefinitionObjectActionsJSONObject.getString(
						"items"))));
	}

	@Test
	public void testPatchObjectAction() throws Exception {
		ObjectAction postObjectAction = testPatchObjectAction_addObjectAction();

		ObjectAction randomPatchObjectAction = randomPatchObjectAction();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ObjectAction patchObjectAction = objectActionResource.patchObjectAction(
			postObjectAction.getId(), randomPatchObjectAction);

		ObjectAction expectedPatchObjectAction = postObjectAction.clone();

		BeanTestUtil.copyProperties(
			randomPatchObjectAction, expectedPatchObjectAction);

		ObjectAction getObjectAction = objectActionResource.getObjectAction(
			patchObjectAction.getId());

		assertEquals(expectedPatchObjectAction, getObjectAction);
		assertValid(getObjectAction);
	}

	protected ObjectAction testPatchObjectAction_addObjectAction()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostObjectDefinitionByExternalReferenceCodeObjectAction()
		throws Exception {

		ObjectAction randomObjectAction = randomObjectAction();

		ObjectAction postObjectAction =
			testPostObjectDefinitionByExternalReferenceCodeObjectAction_addObjectAction(
				randomObjectAction);

		assertEquals(randomObjectAction, postObjectAction);
		assertValid(postObjectAction);
	}

	protected ObjectAction
			testPostObjectDefinitionByExternalReferenceCodeObjectAction_addObjectAction(
				ObjectAction objectAction)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostObjectDefinitionByExternalReferenceCodeObjectAction()
		throws Exception {

		ObjectAction randomObjectAction = randomObjectAction();

		ObjectAction objectAction =
			testGraphQLObjectDefinitionObjectAction_addObjectAction(
				testGraphQLPostObjectDefinitionByExternalReferenceCodeObjectAction_getObjectDefinitionId(),
				randomObjectAction);

		Assert.assertTrue(equals(randomObjectAction, objectAction));
	}

	protected Long
			testGraphQLPostObjectDefinitionByExternalReferenceCodeObjectAction_getObjectDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostObjectDefinitionObjectAction() throws Exception {
		ObjectAction randomObjectAction = randomObjectAction();

		ObjectAction postObjectAction =
			testPostObjectDefinitionObjectAction_addObjectAction(
				randomObjectAction);

		assertEquals(randomObjectAction, postObjectAction);
		assertValid(postObjectAction);
	}

	protected ObjectAction testPostObjectDefinitionObjectAction_addObjectAction(
			ObjectAction objectAction)
		throws Exception {

		return objectActionResource.postObjectDefinitionObjectAction(
			testGetObjectDefinitionObjectActionsPage_getObjectDefinitionId(),
			objectAction);
	}

	@Test
	public void testGraphQLPostObjectDefinitionObjectAction() throws Exception {
		ObjectAction randomObjectAction = randomObjectAction();

		ObjectAction objectAction =
			testGraphQLObjectDefinitionObjectAction_addObjectAction(
				testGraphQLPostObjectDefinitionObjectAction_getObjectDefinitionId(),
				randomObjectAction);

		Assert.assertTrue(equals(randomObjectAction, objectAction));
	}

	protected Long
			testGraphQLPostObjectDefinitionObjectAction_getObjectDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutObjectAction() throws Exception {
		ObjectAction postObjectAction = testPutObjectAction_addObjectAction();

		ObjectAction randomObjectAction = randomObjectAction();

		ObjectAction putObjectAction = objectActionResource.putObjectAction(
			postObjectAction.getId(), randomObjectAction);

		assertEquals(randomObjectAction, putObjectAction);
		assertValid(putObjectAction);

		ObjectAction getObjectAction = objectActionResource.getObjectAction(
			putObjectAction.getId());

		assertEquals(randomObjectAction, getObjectAction);
		assertValid(getObjectAction);
	}

	protected ObjectAction testPutObjectAction_addObjectAction()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ObjectAction objectAction1 =
			testBatchEngineDeleteImportTask_addObjectAction();

		testBatchEngineDeleteImportTask_deleteObjectAction(
			200, null, objectAction1.getId());

		assertHttpResponseStatusCode(
			404,
			objectActionResource.getObjectActionHttpResponse(
				objectAction1.getId()));
	}

	protected ObjectAction testBatchEngineDeleteImportTask_addObjectAction()
		throws Exception {

		return testDeleteObjectAction_addObjectAction();
	}

	protected void testBatchEngineDeleteImportTask_deleteObjectAction(
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
				"com.liferay.object.admin.rest.dto.v1_0.ObjectAction", null,
				null, null, null,
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

	protected ObjectAction testGraphQLObjectAction_addObjectAction()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ObjectAction
			testGraphQLObjectDefinitionObjectAction_addObjectAction()
		throws Exception {

		return testGraphQLObjectDefinitionObjectAction_addObjectAction(
			testGraphQLObjectDefinitionObjectAction_getObjectDefinitionId(),
			randomObjectAction());
	}

	protected Long
			testGraphQLObjectDefinitionObjectAction_getObjectDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ObjectAction
			testGraphQLObjectDefinitionObjectAction_addObjectAction(
				Long objectDefinitionId, ObjectAction objectAction)
		throws Exception {

		JSONDeserializer<ObjectAction> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(ObjectAction.class)) {

			if (getGraphQLValue(field.get(objectAction)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(objectAction)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createObjectDefinitionObjectAction",
						new HashMap<String, Object>() {
							{
								put("objectDefinitionId", objectDefinitionId);
								put("objectAction", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data",
				"JSONObject/createObjectDefinitionObjectAction"),
			ObjectAction.class);
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
		ObjectAction objectAction, List<ObjectAction> objectActions) {

		boolean contains = false;

		for (ObjectAction item : objectActions) {
			if (equals(objectAction, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			objectActions + " does not contain " + objectAction, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ObjectAction objectAction1, ObjectAction objectAction2) {

		Assert.assertTrue(
			objectAction1 + " does not equal " + objectAction2,
			equals(objectAction1, objectAction2));
	}

	protected void assertEquals(
		List<ObjectAction> objectActions1, List<ObjectAction> objectActions2) {

		Assert.assertEquals(objectActions1.size(), objectActions2.size());

		for (int i = 0; i < objectActions1.size(); i++) {
			ObjectAction objectAction1 = objectActions1.get(i);
			ObjectAction objectAction2 = objectActions2.get(i);

			assertEquals(objectAction1, objectAction2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ObjectAction> objectActions1, List<ObjectAction> objectActions2) {

		Assert.assertEquals(objectActions1.size(), objectActions2.size());

		for (ObjectAction objectAction1 : objectActions1) {
			boolean contains = false;

			for (ObjectAction objectAction2 : objectActions2) {
				if (equals(objectAction1, objectAction2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				objectActions2 + " does not contain " + objectAction1,
				contains);
		}
	}

	protected void assertValid(ObjectAction objectAction) throws Exception {
		boolean valid = true;

		if (objectAction.getDateCreated() == null) {
			valid = false;
		}

		if (objectAction.getDateModified() == null) {
			valid = false;
		}

		if (objectAction.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (objectAction.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (objectAction.getActive() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"conditionExpression", additionalAssertFieldName)) {

				if (objectAction.getConditionExpression() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (objectAction.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("errorMessage", additionalAssertFieldName)) {
				if (objectAction.getErrorMessage() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (objectAction.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("label", additionalAssertFieldName)) {
				if (objectAction.getLabel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (objectAction.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectActionExecutorKey", additionalAssertFieldName)) {

				if (objectAction.getObjectActionExecutorKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectActionTriggerKey", additionalAssertFieldName)) {

				if (objectAction.getObjectActionTriggerKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("parameters", additionalAssertFieldName)) {
				if (objectAction.getParameters() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (objectAction.getStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("system", additionalAssertFieldName)) {
				if (objectAction.getSystem() == null) {
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

	protected void assertValid(Page<ObjectAction> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ObjectAction> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ObjectAction> objectActions = page.getItems();

		int size = objectActions.size();

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
					com.liferay.object.admin.rest.dto.v1_0.ObjectAction.
						class)) {

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
		ObjectAction objectAction1, ObjectAction objectAction2) {

		if (objectAction1 == objectAction2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)objectAction1.getActions(),
						(Map)objectAction2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectAction1.getActive(), objectAction2.getActive())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"conditionExpression", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectAction1.getConditionExpression(),
						objectAction2.getConditionExpression())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectAction1.getDateCreated(),
						objectAction2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectAction1.getDateModified(),
						objectAction2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectAction1.getDescription(),
						objectAction2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("errorMessage", additionalAssertFieldName)) {
				if (!equals(
						(Map)objectAction1.getErrorMessage(),
						(Map)objectAction2.getErrorMessage())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectAction1.getExternalReferenceCode(),
						objectAction2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectAction1.getId(), objectAction2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("label", additionalAssertFieldName)) {
				if (!equals(
						(Map)objectAction1.getLabel(),
						(Map)objectAction2.getLabel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectAction1.getName(), objectAction2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectActionExecutorKey", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectAction1.getObjectActionExecutorKey(),
						objectAction2.getObjectActionExecutorKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectActionTriggerKey", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectAction1.getObjectActionTriggerKey(),
						objectAction2.getObjectActionTriggerKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("parameters", additionalAssertFieldName)) {
				if (!equals(
						(Map)objectAction1.getParameters(),
						(Map)objectAction2.getParameters())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectAction1.getStatus(), objectAction2.getStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("system", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectAction1.getSystem(), objectAction2.getSystem())) {

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

		if (!(_objectActionResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_objectActionResource;

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
		EntityField entityField, String operator, ObjectAction objectAction) {

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

		if (entityFieldName.equals("conditionExpression")) {
			Object object = objectAction.getConditionExpression();

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

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = objectAction.getDateCreated();

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

				sb.append(_format.format(objectAction.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = objectAction.getDateModified();

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

				sb.append(_format.format(objectAction.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = objectAction.getDescription();

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

		if (entityFieldName.equals("errorMessage")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = objectAction.getExternalReferenceCode();

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

		if (entityFieldName.equals("label")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("name")) {
			Object object = objectAction.getName();

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

		if (entityFieldName.equals("objectActionExecutorKey")) {
			Object object = objectAction.getObjectActionExecutorKey();

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

		if (entityFieldName.equals("objectActionTriggerKey")) {
			Object object = objectAction.getObjectActionTriggerKey();

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

		if (entityFieldName.equals("parameters")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("status")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("system")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
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

	protected ObjectAction randomObjectAction() throws Exception {
		return new ObjectAction() {
			{
				active = RandomTestUtil.randomBoolean();
				conditionExpression = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				objectActionExecutorKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				objectActionTriggerKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				system = RandomTestUtil.randomBoolean();
			}
		};
	}

	protected ObjectAction randomIrrelevantObjectAction() throws Exception {
		ObjectAction randomIrrelevantObjectAction = randomObjectAction();

		return randomIrrelevantObjectAction;
	}

	protected ObjectAction randomPatchObjectAction() throws Exception {
		return randomObjectAction();
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

	protected ObjectActionResource objectActionResource;
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
		LogFactoryUtil.getLog(BaseObjectActionResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.object.admin.rest.resource.v1_0.ObjectActionResource
		_objectActionResource;

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