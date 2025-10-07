/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.admin.user.client.dto.v1_0.UserGroup;
import com.liferay.headless.admin.user.client.http.HttpInvoker;
import com.liferay.headless.admin.user.client.pagination.Page;
import com.liferay.headless.admin.user.client.pagination.Pagination;
import com.liferay.headless.admin.user.client.resource.v1_0.UserGroupResource;
import com.liferay.headless.admin.user.client.serdes.v1_0.UserGroupSerDes;
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
import com.liferay.portal.search.test.rule.SearchTestRule;
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
public abstract class BaseUserGroupResourceTestCase {

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

		_userGroupResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		userGroupResource = UserGroupResource.builder(
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

		UserGroup userGroup1 = randomUserGroup();

		String json = objectMapper.writeValueAsString(userGroup1);

		UserGroup userGroup2 = UserGroupSerDes.toDTO(json);

		Assert.assertTrue(equals(userGroup1, userGroup2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		UserGroup userGroup = randomUserGroup();

		String json1 = objectMapper.writeValueAsString(userGroup);
		String json2 = UserGroupSerDes.toJSON(userGroup);

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

		UserGroup userGroup = randomUserGroup();

		userGroup.setDescription(regex);
		userGroup.setExternalReferenceCode(regex);
		userGroup.setName(regex);

		String json = UserGroupSerDes.toJSON(userGroup);

		Assert.assertFalse(json.contains(regex));

		userGroup = UserGroupSerDes.toDTO(json);

		Assert.assertEquals(regex, userGroup.getDescription());
		Assert.assertEquals(regex, userGroup.getExternalReferenceCode());
		Assert.assertEquals(regex, userGroup.getName());
	}

	@Test
	public void testDeleteUserGroup() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserGroup userGroup = testDeleteUserGroup_addUserGroup();

		assertHttpResponseStatusCode(
			204,
			userGroupResource.deleteUserGroupHttpResponse(userGroup.getId()));

		assertHttpResponseStatusCode(
			404, userGroupResource.getUserGroupHttpResponse(userGroup.getId()));
		assertHttpResponseStatusCode(
			404, userGroupResource.getUserGroupHttpResponse(0L));
	}

	protected UserGroup testDeleteUserGroup_addUserGroup() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteUserGroup() throws Exception {

		// No namespace

		UserGroup userGroup1 = testGraphQLDeleteUserGroup_addUserGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteUserGroup",
						new HashMap<String, Object>() {
							{
								put("userGroupId", userGroup1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteUserGroup"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"userGroup",
					new HashMap<String, Object>() {
						{
							put("userGroupId", userGroup1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessAdminUser_v1_0

		UserGroup userGroup2 = testGraphQLDeleteUserGroup_addUserGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteUserGroup",
							new HashMap<String, Object>() {
								{
									put("userGroupId", userGroup2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteUserGroup"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessAdminUser_v1_0",
					new GraphQLField(
						"userGroup",
						new HashMap<String, Object>() {
							{
								put("userGroupId", userGroup2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected UserGroup testGraphQLDeleteUserGroup_addUserGroup()
		throws Exception {

		return testGraphQLUserGroup_addUserGroup();
	}

	@Test
	public void testDeleteUserGroupBatch() throws Exception {
		UserGroup userGroup1 = testDeleteUserGroupBatch_addUserGroup();

		testDeleteUserGroupBatch_deleteUserGroup(
			202, userGroup1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			userGroupResource.getUserGroupHttpResponse(userGroup1.getId()));

		userGroup1 = testDeleteUserGroupBatch_addUserGroup();

		testDeleteUserGroupBatch_deleteUserGroup(202, null, userGroup1.getId());

		assertHttpResponseStatusCode(
			404,
			userGroupResource.getUserGroupHttpResponse(userGroup1.getId()));

		userGroup1 = testDeleteUserGroupBatch_addUserGroup();
		UserGroup userGroup2 = testDeleteUserGroupBatch_addUserGroup();

		testDeleteUserGroupBatch_deleteUserGroup(
			202, userGroup2.getExternalReferenceCode(), userGroup1.getId());

		assertHttpResponseStatusCode(
			404,
			userGroupResource.getUserGroupHttpResponse(userGroup1.getId()));
		assertHttpResponseStatusCode(
			200,
			userGroupResource.getUserGroupHttpResponse(userGroup2.getId()));

		testDeleteUserGroupBatch_deleteUserGroup(
			202, userGroup2.getExternalReferenceCode(), userGroup1.getId());

		assertHttpResponseStatusCode(
			404,
			userGroupResource.getUserGroupHttpResponse(userGroup2.getId()));
	}

	protected UserGroup testDeleteUserGroupBatch_addUserGroup()
		throws Exception {

		return testDeleteUserGroup_addUserGroup();
	}

	protected void testDeleteUserGroupBatch_deleteUserGroup(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			userGroupResource.deleteUserGroupBatchHttpResponse(
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
	public void testDeleteUserGroupByExternalReferenceCode() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserGroup userGroup =
			testDeleteUserGroupByExternalReferenceCode_addUserGroup();

		assertHttpResponseStatusCode(
			204,
			userGroupResource.
				deleteUserGroupByExternalReferenceCodeHttpResponse(
					userGroup.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			userGroupResource.getUserGroupByExternalReferenceCodeHttpResponse(
				userGroup.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			userGroupResource.getUserGroupByExternalReferenceCodeHttpResponse(
				"-"));
	}

	protected UserGroup
			testDeleteUserGroupByExternalReferenceCode_addUserGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteUserGroupByExternalReferenceCode()
		throws Exception {

		// No namespace

		UserGroup userGroup1 =
			testGraphQLDeleteUserGroupByExternalReferenceCode_addUserGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteUserGroupByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										userGroup1.getExternalReferenceCode() +
											"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteUserGroupByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"userGroupByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + userGroup1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessAdminUser_v1_0

		UserGroup userGroup2 =
			testGraphQLDeleteUserGroupByExternalReferenceCode_addUserGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteUserGroupByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											userGroup2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteUserGroupByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessAdminUser_v1_0",
					new GraphQLField(
						"userGroupByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										userGroup2.getExternalReferenceCode() +
											"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected UserGroup
			testGraphQLDeleteUserGroupByExternalReferenceCode_addUserGroup()
		throws Exception {

		return testGraphQLUserGroup_addUserGroup();
	}

	@Test
	public void testDeleteUserGroupByExternalReferenceCodeUsers()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserGroup userGroup =
			testDeleteUserGroupByExternalReferenceCodeUsers_addUserGroup();

		assertHttpResponseStatusCode(
			204,
			userGroupResource.
				deleteUserGroupByExternalReferenceCodeUsersHttpResponse(
					userGroup.getExternalReferenceCode(), null));
	}

	protected UserGroup
			testDeleteUserGroupByExternalReferenceCodeUsers_addUserGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteUserGroupByExternalReferenceCodeUsers()
		throws Exception {

		// No namespace

		UserGroup userGroup1 =
			testGraphQLDeleteUserGroupByExternalReferenceCodeUsers_addUserGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteUserGroupByExternalReferenceCodeUsers",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										userGroup1.getExternalReferenceCode() +
											"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteUserGroupByExternalReferenceCodeUsers"));

		// Using the namespace headlessAdminUser_v1_0

		UserGroup userGroup2 =
			testGraphQLDeleteUserGroupByExternalReferenceCodeUsers_addUserGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteUserGroupByExternalReferenceCodeUsers",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											userGroup2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteUserGroupByExternalReferenceCodeUsers"));
	}

	protected UserGroup
			testGraphQLDeleteUserGroupByExternalReferenceCodeUsers_addUserGroup()
		throws Exception {

		return testGraphQLUserGroup_addUserGroup();
	}

	@Test
	public void testDeleteUserGroupUsers() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserGroup userGroup = testDeleteUserGroupUsers_addUserGroup();

		assertHttpResponseStatusCode(
			204,
			userGroupResource.deleteUserGroupUsersHttpResponse(
				userGroup.getId(), null));
	}

	protected UserGroup testDeleteUserGroupUsers_addUserGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteUserGroupUsers() throws Exception {

		// No namespace

		UserGroup userGroup1 = testGraphQLDeleteUserGroupUsers_addUserGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteUserGroupUsers",
						new HashMap<String, Object>() {
							{
								put("userGroupId", userGroup1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteUserGroupUsers"));

		// Using the namespace headlessAdminUser_v1_0

		UserGroup userGroup2 = testGraphQLDeleteUserGroupUsers_addUserGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteUserGroupUsers",
							new HashMap<String, Object>() {
								{
									put("userGroupId", userGroup2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteUserGroupUsers"));
	}

	protected UserGroup testGraphQLDeleteUserGroupUsers_addUserGroup()
		throws Exception {

		return testGraphQLUserGroup_addUserGroup();
	}

	@Test
	public void testGetUserGroup() throws Exception {
		UserGroup postUserGroup = testGetUserGroup_addUserGroup();

		UserGroup getUserGroup = userGroupResource.getUserGroup(
			postUserGroup.getId());

		assertEquals(postUserGroup, getUserGroup);
		assertValid(getUserGroup);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		UserGroup postUserGroup = testGetUserGroup_addUserGroup();

		UserGroup getUserGroup = userGroupResource.getUserGroup(
			postUserGroup.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.admin.user.dto.v1_0.UserGroup"
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

		Object item = vulcanCRUDItemDelegate.getItem(postUserGroup.getId());

		assertEquals(getUserGroup, UserGroupSerDes.toDTO(item.toString()));
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

	protected UserGroup testGetUserGroup_addUserGroup() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetUserGroup() throws Exception {
		UserGroup userGroup = testGraphQLGetUserGroup_addUserGroup();

		// No namespace

		Assert.assertTrue(
			equals(
				userGroup,
				UserGroupSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"userGroup",
								new HashMap<String, Object>() {
									{
										put("userGroupId", userGroup.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/userGroup"))));

		// Using the namespace headlessAdminUser_v1_0

		Assert.assertTrue(
			equals(
				userGroup,
				UserGroupSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminUser_v1_0",
								new GraphQLField(
									"userGroup",
									new HashMap<String, Object>() {
										{
											put(
												"userGroupId",
												userGroup.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
						"Object/userGroup"))));
	}

	@Test
	public void testGraphQLGetUserGroupNotFound() throws Exception {
		Long irrelevantUserGroupId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"userGroup",
						new HashMap<String, Object>() {
							{
								put("userGroupId", irrelevantUserGroupId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessAdminUser_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"userGroup",
							new HashMap<String, Object>() {
								{
									put("userGroupId", irrelevantUserGroupId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected UserGroup testGraphQLGetUserGroup_addUserGroup()
		throws Exception {

		return testGraphQLUserGroup_addUserGroup();
	}

	@Test
	public void testGetUserGroupByExternalReferenceCode() throws Exception {
		UserGroup postUserGroup =
			testGetUserGroupByExternalReferenceCode_addUserGroup();

		UserGroup getUserGroup =
			userGroupResource.getUserGroupByExternalReferenceCode(
				postUserGroup.getExternalReferenceCode());

		assertEquals(postUserGroup, getUserGroup);
		assertValid(getUserGroup);
	}

	protected UserGroup testGetUserGroupByExternalReferenceCode_addUserGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetUserGroupByExternalReferenceCode()
		throws Exception {

		UserGroup userGroup =
			testGraphQLGetUserGroupByExternalReferenceCode_addUserGroup();

		// No namespace

		Assert.assertTrue(
			equals(
				userGroup,
				UserGroupSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"userGroupByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												userGroup.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/userGroupByExternalReferenceCode"))));

		// Using the namespace headlessAdminUser_v1_0

		Assert.assertTrue(
			equals(
				userGroup,
				UserGroupSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminUser_v1_0",
								new GraphQLField(
									"userGroupByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													userGroup.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
						"Object/userGroupByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetUserGroupByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"userGroupByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									irrelevantExternalReferenceCode);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessAdminUser_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"userGroupByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected UserGroup
			testGraphQLGetUserGroupByExternalReferenceCode_addUserGroup()
		throws Exception {

		return testGraphQLUserGroup_addUserGroup();
	}

	@Test
	public void testGetUserGroupsPage() throws Exception {
		Page<UserGroup> page = userGroupResource.getUserGroupsPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		UserGroup userGroup1 = testGetUserGroupsPage_addUserGroup(
			randomUserGroup());

		UserGroup userGroup2 = testGetUserGroupsPage_addUserGroup(
			randomUserGroup());

		page = userGroupResource.getUserGroupsPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(userGroup1, (List<UserGroup>)page.getItems());
		assertContains(userGroup2, (List<UserGroup>)page.getItems());
		assertValid(page, testGetUserGroupsPage_getExpectedActions());

		userGroupResource.deleteUserGroup(userGroup1.getId());

		userGroupResource.deleteUserGroup(userGroup2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetUserGroupsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetUserGroupsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		UserGroup userGroup1 = randomUserGroup();

		userGroup1 = testGetUserGroupsPage_addUserGroup(userGroup1);

		for (EntityField entityField : entityFields) {
			Page<UserGroup> page = userGroupResource.getUserGroupsPage(
				null, getFilterString(entityField, "between", userGroup1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(userGroup1),
				(List<UserGroup>)page.getItems());
		}
	}

	@Test
	public void testGetUserGroupsPageWithFilterDoubleEquals() throws Exception {
		testGetUserGroupsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetUserGroupsPageWithFilterStringContains()
		throws Exception {

		testGetUserGroupsPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetUserGroupsPageWithFilterStringEquals() throws Exception {
		testGetUserGroupsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetUserGroupsPageWithFilterStringStartsWith()
		throws Exception {

		testGetUserGroupsPageWithFilter("startswith", EntityField.Type.STRING);
	}

	protected void testGetUserGroupsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		UserGroup userGroup1 = testGetUserGroupsPage_addUserGroup(
			randomUserGroup());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserGroup userGroup2 = testGetUserGroupsPage_addUserGroup(
			randomUserGroup());

		for (EntityField entityField : entityFields) {
			Page<UserGroup> page = userGroupResource.getUserGroupsPage(
				null, getFilterString(entityField, operator, userGroup1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(userGroup1),
				(List<UserGroup>)page.getItems());
		}
	}

	@Test
	public void testGetUserGroupsPageWithPagination() throws Exception {
		Page<UserGroup> userGroupsPage = userGroupResource.getUserGroupsPage(
			null, null, null, null);

		int totalCount = GetterUtil.getInteger(userGroupsPage.getTotalCount());

		UserGroup userGroup1 = testGetUserGroupsPage_addUserGroup(
			randomUserGroup());

		UserGroup userGroup2 = testGetUserGroupsPage_addUserGroup(
			randomUserGroup());

		UserGroup userGroup3 = testGetUserGroupsPage_addUserGroup(
			randomUserGroup());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<UserGroup> page1 = userGroupResource.getUserGroupsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(userGroup1, (List<UserGroup>)page1.getItems());

			Page<UserGroup> page2 = userGroupResource.getUserGroupsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(userGroup2, (List<UserGroup>)page2.getItems());

			Page<UserGroup> page3 = userGroupResource.getUserGroupsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(userGroup3, (List<UserGroup>)page3.getItems());
		}
		else {
			Page<UserGroup> page1 = userGroupResource.getUserGroupsPage(
				null, null, Pagination.of(1, totalCount + 2), null);

			List<UserGroup> userGroups1 = (List<UserGroup>)page1.getItems();

			Assert.assertEquals(
				userGroups1.toString(), totalCount + 2, userGroups1.size());

			Page<UserGroup> page2 = userGroupResource.getUserGroupsPage(
				null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<UserGroup> userGroups2 = (List<UserGroup>)page2.getItems();

			Assert.assertEquals(userGroups2.toString(), 1, userGroups2.size());

			Page<UserGroup> page3 = userGroupResource.getUserGroupsPage(
				null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(userGroup1, (List<UserGroup>)page3.getItems());
			assertContains(userGroup2, (List<UserGroup>)page3.getItems());
			assertContains(userGroup3, (List<UserGroup>)page3.getItems());
		}
	}

	@Test
	public void testGetUserGroupsPageWithSortDateTime() throws Exception {
		testGetUserGroupsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, userGroup1, userGroup2) -> {
				BeanTestUtil.setProperty(
					userGroup1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetUserGroupsPageWithSortDouble() throws Exception {
		testGetUserGroupsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, userGroup1, userGroup2) -> {
				BeanTestUtil.setProperty(
					userGroup1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					userGroup2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetUserGroupsPageWithSortInteger() throws Exception {
		testGetUserGroupsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, userGroup1, userGroup2) -> {
				BeanTestUtil.setProperty(userGroup1, entityField.getName(), 0);
				BeanTestUtil.setProperty(userGroup2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetUserGroupsPageWithSortString() throws Exception {
		testGetUserGroupsPageWithSort(
			EntityField.Type.STRING,
			(entityField, userGroup1, userGroup2) -> {
				Class<?> clazz = userGroup1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						userGroup1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						userGroup2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						userGroup1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						userGroup2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						userGroup1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						userGroup2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetUserGroupsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, UserGroup, UserGroup, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		UserGroup userGroup1 = randomUserGroup();
		UserGroup userGroup2 = randomUserGroup();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, userGroup1, userGroup2);
		}

		userGroup1 = testGetUserGroupsPage_addUserGroup(userGroup1);

		userGroup2 = testGetUserGroupsPage_addUserGroup(userGroup2);

		Page<UserGroup> page = userGroupResource.getUserGroupsPage(
			null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<UserGroup> ascPage = userGroupResource.getUserGroupsPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(userGroup1, (List<UserGroup>)ascPage.getItems());
			assertContains(userGroup2, (List<UserGroup>)ascPage.getItems());

			Page<UserGroup> descPage = userGroupResource.getUserGroupsPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(userGroup2, (List<UserGroup>)descPage.getItems());
			assertContains(userGroup1, (List<UserGroup>)descPage.getItems());
		}
	}

	protected UserGroup testGetUserGroupsPage_addUserGroup(UserGroup userGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetUserGroupsPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"userGroups",
			new HashMap<String, Object>() {
				{
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject userGroupsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/userGroups");

		long totalCount = userGroupsJSONObject.getLong("totalCount");

		UserGroup userGroup1 = testGraphQLUserGroup_addUserGroup(
			randomUserGroup());

		UserGroup userGroup2 = testGraphQLUserGroup_addUserGroup(
			randomUserGroup());

		userGroupsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/userGroups");

		Assert.assertEquals(
			totalCount + 2, userGroupsJSONObject.getLong("totalCount"));

		assertContains(
			userGroup1,
			Arrays.asList(
				UserGroupSerDes.toDTOs(
					userGroupsJSONObject.getString("items"))));
		assertContains(
			userGroup2,
			Arrays.asList(
				UserGroupSerDes.toDTOs(
					userGroupsJSONObject.getString("items"))));

		// Using the namespace headlessAdminUser_v1_0

		userGroupsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessAdminUser_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
			"JSONObject/userGroups");

		Assert.assertEquals(
			totalCount + 2, userGroupsJSONObject.getLong("totalCount"));

		assertContains(
			userGroup1,
			Arrays.asList(
				UserGroupSerDes.toDTOs(
					userGroupsJSONObject.getString("items"))));
		assertContains(
			userGroup2,
			Arrays.asList(
				UserGroupSerDes.toDTOs(
					userGroupsJSONObject.getString("items"))));
	}

	@Test
	public void testGetUserUserGroups() throws Exception {
		Long userAccountId = testGetUserUserGroups_getUserAccountId();
		Long irrelevantUserAccountId =
			testGetUserUserGroups_getIrrelevantUserAccountId();

		Page<UserGroup> page = userGroupResource.getUserUserGroups(
			userAccountId);

		long totalCount = page.getTotalCount();

		if (irrelevantUserAccountId != null) {
			UserGroup irrelevantUserGroup = testGetUserUserGroups_addUserGroup(
				irrelevantUserAccountId, randomIrrelevantUserGroup());

			page = userGroupResource.getUserUserGroups(irrelevantUserAccountId);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantUserGroup, (List<UserGroup>)page.getItems());
			assertValid(
				page,
				testGetUserUserGroups_getExpectedActions(
					irrelevantUserAccountId));
		}

		UserGroup userGroup1 = testGetUserUserGroups_addUserGroup(
			userAccountId, randomUserGroup());

		UserGroup userGroup2 = testGetUserUserGroups_addUserGroup(
			userAccountId, randomUserGroup());

		page = userGroupResource.getUserUserGroups(userAccountId);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(userGroup1, (List<UserGroup>)page.getItems());
		assertContains(userGroup2, (List<UserGroup>)page.getItems());
		assertValid(
			page, testGetUserUserGroups_getExpectedActions(userAccountId));

		userGroupResource.deleteUserGroup(userGroup1.getId());

		userGroupResource.deleteUserGroup(userGroup2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetUserUserGroups_getExpectedActions(Long userAccountId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected UserGroup testGetUserUserGroups_addUserGroup(
			Long userAccountId, UserGroup userGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetUserUserGroups_getUserAccountId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetUserUserGroups_getIrrelevantUserAccountId()
		throws Exception {

		return null;
	}

	@Test
	public void testPatchUserGroup() throws Exception {
		UserGroup postUserGroup = testPatchUserGroup_addUserGroup();

		UserGroup randomPatchUserGroup = randomPatchUserGroup();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserGroup patchUserGroup = userGroupResource.patchUserGroup(
			postUserGroup.getId(), randomPatchUserGroup);

		UserGroup expectedPatchUserGroup = postUserGroup.clone();

		BeanTestUtil.copyProperties(
			randomPatchUserGroup, expectedPatchUserGroup);

		UserGroup getUserGroup = userGroupResource.getUserGroup(
			patchUserGroup.getId());

		assertEquals(expectedPatchUserGroup, getUserGroup);
		assertValid(getUserGroup);
	}

	protected UserGroup testPatchUserGroup_addUserGroup() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchUserGroupByExternalReferenceCode() throws Exception {
		UserGroup postUserGroup =
			testPatchUserGroupByExternalReferenceCode_addUserGroup();

		UserGroup randomPatchUserGroup = randomPatchUserGroup();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserGroup patchUserGroup =
			userGroupResource.patchUserGroupByExternalReferenceCode(
				postUserGroup.getExternalReferenceCode(), randomPatchUserGroup);

		UserGroup expectedPatchUserGroup = postUserGroup.clone();

		BeanTestUtil.copyProperties(
			randomPatchUserGroup, expectedPatchUserGroup);

		UserGroup getUserGroup =
			userGroupResource.getUserGroupByExternalReferenceCode(
				patchUserGroup.getExternalReferenceCode());

		assertEquals(expectedPatchUserGroup, getUserGroup);
		assertValid(getUserGroup);
	}

	protected UserGroup testPatchUserGroupByExternalReferenceCode_addUserGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostUserGroup() throws Exception {
		UserGroup randomUserGroup = randomUserGroup();

		UserGroup postUserGroup = testPostUserGroup_addUserGroup(
			randomUserGroup);

		assertEquals(randomUserGroup, postUserGroup);
		assertValid(postUserGroup);
	}

	protected UserGroup testPostUserGroup_addUserGroup(UserGroup userGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostUserGroup() throws Exception {
		UserGroup randomUserGroup = randomUserGroup();

		UserGroup userGroup = testGraphQLUserGroup_addUserGroup(
			randomUserGroup);

		Assert.assertTrue(equals(randomUserGroup, userGroup));
	}

	@Test
	public void testPostUserGroupByExternalReferenceCodeUsers()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserGroup userGroup =
			testPostUserGroupByExternalReferenceCodeUsers_addUserGroup();

		assertHttpResponseStatusCode(
			204,
			userGroupResource.
				postUserGroupByExternalReferenceCodeUsersHttpResponse(
					userGroup.getExternalReferenceCode(), null));

		assertHttpResponseStatusCode(
			404,
			userGroupResource.
				postUserGroupByExternalReferenceCodeUsersHttpResponse(
					"-", null));
	}

	protected UserGroup
			testPostUserGroupByExternalReferenceCodeUsers_addUserGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostUserGroupUsers() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserGroup userGroup = testPostUserGroupUsers_addUserGroup();

		assertHttpResponseStatusCode(
			204,
			userGroupResource.postUserGroupUsersHttpResponse(
				userGroup.getId(), null));

		assertHttpResponseStatusCode(
			404, userGroupResource.postUserGroupUsersHttpResponse(0L, null));
	}

	protected UserGroup testPostUserGroupUsers_addUserGroup() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutUserGroup() throws Exception {
		UserGroup postUserGroup = testPutUserGroup_addUserGroup();

		UserGroup randomUserGroup = randomUserGroup();

		UserGroup putUserGroup = userGroupResource.putUserGroup(
			postUserGroup.getId(), randomUserGroup);

		assertEquals(randomUserGroup, putUserGroup);
		assertValid(putUserGroup);

		UserGroup getUserGroup = userGroupResource.getUserGroup(
			putUserGroup.getId());

		assertEquals(randomUserGroup, getUserGroup);
		assertValid(getUserGroup);
	}

	protected UserGroup testPutUserGroup_addUserGroup() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutUserGroupByExternalReferenceCode() throws Exception {
		UserGroup postUserGroup =
			testPutUserGroupByExternalReferenceCode_addUserGroup();

		UserGroup randomUserGroup = randomUserGroup();

		UserGroup putUserGroup =
			userGroupResource.putUserGroupByExternalReferenceCode(
				postUserGroup.getExternalReferenceCode(), randomUserGroup);

		assertEquals(randomUserGroup, putUserGroup);
		assertValid(putUserGroup);

		UserGroup getUserGroup =
			userGroupResource.getUserGroupByExternalReferenceCode(
				putUserGroup.getExternalReferenceCode());

		assertEquals(randomUserGroup, getUserGroup);
		assertValid(getUserGroup);

		UserGroup newUserGroup =
			testPutUserGroupByExternalReferenceCode_createUserGroup();

		putUserGroup = userGroupResource.putUserGroupByExternalReferenceCode(
			newUserGroup.getExternalReferenceCode(), newUserGroup);

		assertEquals(newUserGroup, putUserGroup);
		assertValid(putUserGroup);

		getUserGroup = userGroupResource.getUserGroupByExternalReferenceCode(
			putUserGroup.getExternalReferenceCode());

		assertEquals(newUserGroup, getUserGroup);

		Assert.assertEquals(
			newUserGroup.getExternalReferenceCode(),
			putUserGroup.getExternalReferenceCode());
	}

	protected UserGroup testPutUserGroupByExternalReferenceCode_addUserGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected UserGroup
			testPutUserGroupByExternalReferenceCode_createUserGroup()
		throws Exception {

		return randomUserGroup();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		UserGroup userGroup1 = testBatchEngineDeleteImportTask_addUserGroup();

		testBatchEngineDeleteImportTask_deleteUserGroup(
			200, userGroup1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			userGroupResource.getUserGroupHttpResponse(userGroup1.getId()));

		userGroup1 = testBatchEngineDeleteImportTask_addUserGroup();

		testBatchEngineDeleteImportTask_deleteUserGroup(
			200, null, userGroup1.getId());

		assertHttpResponseStatusCode(
			404,
			userGroupResource.getUserGroupHttpResponse(userGroup1.getId()));

		userGroup1 = testBatchEngineDeleteImportTask_addUserGroup();
		UserGroup userGroup2 = testBatchEngineDeleteImportTask_addUserGroup();

		testBatchEngineDeleteImportTask_deleteUserGroup(
			200, userGroup2.getExternalReferenceCode(), userGroup1.getId());

		assertHttpResponseStatusCode(
			404,
			userGroupResource.getUserGroupHttpResponse(userGroup1.getId()));
		assertHttpResponseStatusCode(
			200,
			userGroupResource.getUserGroupHttpResponse(userGroup2.getId()));

		testBatchEngineDeleteImportTask_deleteUserGroup(
			200, userGroup2.getExternalReferenceCode(), userGroup1.getId());

		assertHttpResponseStatusCode(
			404,
			userGroupResource.getUserGroupHttpResponse(userGroup2.getId()));
	}

	protected UserGroup testBatchEngineDeleteImportTask_addUserGroup()
		throws Exception {

		return testDeleteUserGroup_addUserGroup();
	}

	protected void testBatchEngineDeleteImportTask_deleteUserGroup(
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
				"com.liferay.headless.admin.user.dto.v1_0.UserGroup", null,
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

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected UserGroup testGraphQLUserGroup_addUserGroup() throws Exception {
		return testGraphQLUserGroup_addUserGroup(randomUserGroup());
	}

	protected UserGroup testGraphQLUserGroup_addUserGroup(UserGroup userGroup)
		throws Exception {

		JSONDeserializer<UserGroup> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(UserGroup.class)) {

			if (getGraphQLValue(field.get(userGroup)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(userGroup)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createUserGroup",
						new HashMap<String, Object>() {
							{
								put("userGroup", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createUserGroup"),
			UserGroup.class);
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
		UserGroup userGroup, List<UserGroup> userGroups) {

		boolean contains = false;

		for (UserGroup item : userGroups) {
			if (equals(userGroup, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			userGroups + " does not contain " + userGroup, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(UserGroup userGroup1, UserGroup userGroup2) {
		Assert.assertTrue(
			userGroup1 + " does not equal " + userGroup2,
			equals(userGroup1, userGroup2));
	}

	protected void assertEquals(
		List<UserGroup> userGroups1, List<UserGroup> userGroups2) {

		Assert.assertEquals(userGroups1.size(), userGroups2.size());

		for (int i = 0; i < userGroups1.size(); i++) {
			UserGroup userGroup1 = userGroups1.get(i);
			UserGroup userGroup2 = userGroups2.get(i);

			assertEquals(userGroup1, userGroup2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<UserGroup> userGroups1, List<UserGroup> userGroups2) {

		Assert.assertEquals(userGroups1.size(), userGroups2.size());

		for (UserGroup userGroup1 : userGroups1) {
			boolean contains = false;

			for (UserGroup userGroup2 : userGroups2) {
				if (equals(userGroup1, userGroup2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				userGroups2 + " does not contain " + userGroup1, contains);
		}
	}

	protected void assertValid(UserGroup userGroup) throws Exception {
		boolean valid = true;

		if (userGroup.getDateCreated() == null) {
			valid = false;
		}

		if (userGroup.getDateModified() == null) {
			valid = false;
		}

		if (userGroup.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (userGroup.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (userGroup.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (userGroup.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (userGroup.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (userGroup.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (userGroup.getPermissions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("roleBriefs", additionalAssertFieldName)) {
				if (userGroup.getRoleBriefs() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"userAccountBriefs", additionalAssertFieldName)) {

				if (userGroup.getUserAccountBriefs() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("usersCount", additionalAssertFieldName)) {
				if (userGroup.getUsersCount() == null) {
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

	protected void assertValid(Page<UserGroup> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<UserGroup> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<UserGroup> userGroups = page.getItems();

		int size = userGroups.size();

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
					com.liferay.headless.admin.user.dto.v1_0.UserGroup.class)) {

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

	protected boolean equals(UserGroup userGroup1, UserGroup userGroup2) {
		if (userGroup1 == userGroup2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)userGroup1.getActions(),
						(Map)userGroup2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userGroup1.getCreator(), userGroup2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userGroup1.getDateCreated(),
						userGroup2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userGroup1.getDateModified(),
						userGroup2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userGroup1.getDescription(),
						userGroup2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						userGroup1.getExternalReferenceCode(),
						userGroup2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userGroup1.getId(), userGroup2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userGroup1.getName(), userGroup2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userGroup1.getPermissions(),
						userGroup2.getPermissions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("roleBriefs", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userGroup1.getRoleBriefs(),
						userGroup2.getRoleBriefs())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"userAccountBriefs", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						userGroup1.getUserAccountBriefs(),
						userGroup2.getUserAccountBriefs())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("usersCount", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userGroup1.getUsersCount(),
						userGroup2.getUsersCount())) {

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

		if (!(_userGroupResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_userGroupResource;

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
		EntityField entityField, String operator, UserGroup userGroup) {

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

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = userGroup.getDateCreated();

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

				sb.append(_format.format(userGroup.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = userGroup.getDateModified();

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

				sb.append(_format.format(userGroup.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = userGroup.getDescription();

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
			Object object = userGroup.getExternalReferenceCode();

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
			Object object = userGroup.getName();

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

		if (entityFieldName.equals("permissions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("roleBriefs")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("userAccountBriefs")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("usersCount")) {
			sb.append(String.valueOf(userGroup.getUsersCount()));

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

	protected UserGroup randomUserGroup() throws Exception {
		return new UserGroup() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				usersCount = RandomTestUtil.randomInt();
			}
		};
	}

	protected UserGroup randomIrrelevantUserGroup() throws Exception {
		UserGroup randomIrrelevantUserGroup = randomUserGroup();

		return randomIrrelevantUserGroup;
	}

	protected UserGroup randomPatchUserGroup() throws Exception {
		return randomUserGroup();
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

	protected UserGroupResource userGroupResource;
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
		LogFactoryUtil.getLog(BaseUserGroupResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.admin.user.resource.v1_0.UserGroupResource
		_userGroupResource;

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