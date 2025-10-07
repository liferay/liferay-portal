/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.object.resource.v1_0.test;

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
import com.liferay.headless.object.client.dto.v1_0.ObjectEntryFolder;
import com.liferay.headless.object.client.http.HttpInvoker;
import com.liferay.headless.object.client.pagination.Page;
import com.liferay.headless.object.client.pagination.Pagination;
import com.liferay.headless.object.client.permission.Permission;
import com.liferay.headless.object.client.resource.v1_0.ObjectEntryFolderResource;
import com.liferay.headless.object.client.serdes.v1_0.ObjectEntryFolderSerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
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
 * @author Alicia García
 * @generated
 */
@Generated("")
public abstract class BaseObjectEntryFolderResourceTestCase {

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

		_objectEntryFolderResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		objectEntryFolderResource = ObjectEntryFolderResource.builder(
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

		permissionsObjectEntryFolderResource =
			ObjectEntryFolderResource.builder(
			).authentication(
				_testCompanyAdminUser.getEmailAddress(),
				PropsValues.DEFAULT_ADMIN_PASSWORD
			).endpoint(
				testCompany.getVirtualHostname(), 8080, "http"
			).locale(
				LocaleUtil.getDefault()
			).parameter(
				"nestedFields", "permissions"
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

		ObjectEntryFolder objectEntryFolder1 = randomObjectEntryFolder();

		String json = objectMapper.writeValueAsString(objectEntryFolder1);

		ObjectEntryFolder objectEntryFolder2 = ObjectEntryFolderSerDes.toDTO(
			json);

		Assert.assertTrue(equals(objectEntryFolder1, objectEntryFolder2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ObjectEntryFolder objectEntryFolder = randomObjectEntryFolder();

		String json1 = objectMapper.writeValueAsString(objectEntryFolder);
		String json2 = ObjectEntryFolderSerDes.toJSON(objectEntryFolder);

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

		ObjectEntryFolder objectEntryFolder = randomObjectEntryFolder();

		objectEntryFolder.setDescription(regex);
		objectEntryFolder.setExternalReferenceCode(regex);
		objectEntryFolder.setLabel(regex);
		objectEntryFolder.setParentObjectEntryFolderExternalReferenceCode(
			regex);
		objectEntryFolder.setScopeKey(regex);
		objectEntryFolder.setTitle(regex);

		String json = ObjectEntryFolderSerDes.toJSON(objectEntryFolder);

		Assert.assertFalse(json.contains(regex));

		objectEntryFolder = ObjectEntryFolderSerDes.toDTO(json);

		Assert.assertEquals(regex, objectEntryFolder.getDescription());
		Assert.assertEquals(
			regex, objectEntryFolder.getExternalReferenceCode());
		Assert.assertEquals(regex, objectEntryFolder.getLabel());
		Assert.assertEquals(
			regex,
			objectEntryFolder.
				getParentObjectEntryFolderExternalReferenceCode());
		Assert.assertEquals(regex, objectEntryFolder.getScopeKey());
		Assert.assertEquals(regex, objectEntryFolder.getTitle());
	}

	@Test
	public void testDeleteObjectEntryFolder() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ObjectEntryFolder objectEntryFolder =
			testDeleteObjectEntryFolder_addObjectEntryFolder();

		assertHttpResponseStatusCode(
			204,
			objectEntryFolderResource.deleteObjectEntryFolderHttpResponse(
				objectEntryFolder.getId()));

		assertHttpResponseStatusCode(
			404,
			objectEntryFolderResource.getObjectEntryFolderHttpResponse(
				objectEntryFolder.getId()));
		assertHttpResponseStatusCode(
			404,
			objectEntryFolderResource.getObjectEntryFolderHttpResponse(0L));
	}

	protected ObjectEntryFolder
			testDeleteObjectEntryFolder_addObjectEntryFolder()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteObjectEntryFolder() throws Exception {

		// No namespace

		ObjectEntryFolder objectEntryFolder1 =
			testGraphQLDeleteObjectEntryFolder_addObjectEntryFolder();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteObjectEntryFolder",
						new HashMap<String, Object>() {
							{
								put(
									"objectEntryFolderId",
									objectEntryFolder1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteObjectEntryFolder"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"objectEntryFolder",
					new HashMap<String, Object>() {
						{
							put(
								"objectEntryFolderId",
								objectEntryFolder1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessObject_v1_0

		ObjectEntryFolder objectEntryFolder2 =
			testGraphQLDeleteObjectEntryFolder_addObjectEntryFolder();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessObject_v1_0",
						new GraphQLField(
							"deleteObjectEntryFolder",
							new HashMap<String, Object>() {
								{
									put(
										"objectEntryFolderId",
										objectEntryFolder2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessObject_v1_0",
				"Object/deleteObjectEntryFolder"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessObject_v1_0",
					new GraphQLField(
						"objectEntryFolder",
						new HashMap<String, Object>() {
							{
								put(
									"objectEntryFolderId",
									objectEntryFolder2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ObjectEntryFolder
			testGraphQLDeleteObjectEntryFolder_addObjectEntryFolder()
		throws Exception {

		return testGraphQLObjectEntryFolder_addObjectEntryFolder();
	}

	@Test
	public void testDeleteObjectEntryFolderBatch() throws Exception {
		ObjectEntryFolder objectEntryFolder1 =
			testDeleteObjectEntryFolderBatch_addObjectEntryFolder();

		testDeleteObjectEntryFolderBatch_deleteObjectEntryFolder(
			202, null, objectEntryFolder1.getId());

		assertHttpResponseStatusCode(
			404,
			objectEntryFolderResource.getObjectEntryFolderHttpResponse(
				objectEntryFolder1.getId()));
	}

	protected ObjectEntryFolder
			testDeleteObjectEntryFolderBatch_addObjectEntryFolder()
		throws Exception {

		return testDeleteObjectEntryFolder_addObjectEntryFolder();
	}

	protected void testDeleteObjectEntryFolderBatch_deleteObjectEntryFolder(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			objectEntryFolderResource.deleteObjectEntryFolderBatchHttpResponse(
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
	public void testDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ObjectEntryFolder objectEntryFolder =
			testDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder();

		assertHttpResponseStatusCode(
			204,
			objectEntryFolderResource.
				deleteScopeScopeKeyObjectEntryFolderByExternalReferenceCodeHttpResponse(
					testDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
						objectEntryFolder),
					objectEntryFolder.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			objectEntryFolderResource.
				getScopeScopeKeyObjectEntryFolderByExternalReferenceCodeHttpResponse(
					testDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
						objectEntryFolder),
					objectEntryFolder.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			objectEntryFolderResource.
				getScopeScopeKeyObjectEntryFolderByExternalReferenceCodeHttpResponse(
					testDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
						objectEntryFolder),
					"-"));
	}

	protected ObjectEntryFolder
			testDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode()
		throws Exception {

		// No namespace

		ObjectEntryFolder objectEntryFolder1 =
			testGraphQLDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"scopeKey",
									"\"" +
										testGraphQLDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
											objectEntryFolder1) + "\"");
								put(
									"externalReferenceCode",
									"\"" +
										objectEntryFolder1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"scopeScopeKeyObjectEntryFolderByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"scopeKey",
								"\"" +
									testGraphQLDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
										objectEntryFolder1) + "\"");
							put(
								"externalReferenceCode",
								"\"" +
									objectEntryFolder1.
										getExternalReferenceCode() + "\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessObject_v1_0

		ObjectEntryFolder objectEntryFolder2 =
			testGraphQLDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessObject_v1_0",
						new GraphQLField(
							"deleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"scopeKey",
										"\"" +
											testGraphQLDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
												objectEntryFolder2) + "\"");
									put(
										"externalReferenceCode",
										"\"" +
											objectEntryFolder2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessObject_v1_0",
				"Object/deleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessObject_v1_0",
					new GraphQLField(
						"scopeScopeKeyObjectEntryFolderByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"scopeKey",
									"\"" +
										testGraphQLDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
											objectEntryFolder2) + "\"");
								put(
									"externalReferenceCode",
									"\"" +
										objectEntryFolder2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected String
			testGraphQLDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ObjectEntryFolder
			testGraphQLDeleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder()
		throws Exception {

		return testGraphQLObjectEntryFolder_addObjectEntryFolder();
	}

	@Test
	public void testGetObjectEntryFolder() throws Exception {
		ObjectEntryFolder postObjectEntryFolder =
			testGetObjectEntryFolder_addObjectEntryFolder();

		ObjectEntryFolder getObjectEntryFolder =
			objectEntryFolderResource.getObjectEntryFolder(
				postObjectEntryFolder.getId());

		assertEquals(postObjectEntryFolder, getObjectEntryFolder);
		assertValid(getObjectEntryFolder);

		Assert.assertNull(getObjectEntryFolder.getPermissions());

		getObjectEntryFolder =
			permissionsObjectEntryFolderResource.getObjectEntryFolder(
				postObjectEntryFolder.getId());

		Assert.assertNotNull(getObjectEntryFolder.getPermissions());
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		ObjectEntryFolder postObjectEntryFolder =
			testGetObjectEntryFolder_addObjectEntryFolder();

		ObjectEntryFolder getObjectEntryFolder =
			objectEntryFolderResource.getObjectEntryFolder(
				postObjectEntryFolder.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.object.dto.v1_0.ObjectEntryFolder"
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
			postObjectEntryFolder.getId());

		assertEquals(
			getObjectEntryFolder,
			ObjectEntryFolderSerDes.toDTO(item.toString()));
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

	protected ObjectEntryFolder testGetObjectEntryFolder_addObjectEntryFolder()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetObjectEntryFolder() throws Exception {
		ObjectEntryFolder objectEntryFolder =
			testGraphQLGetObjectEntryFolder_addObjectEntryFolder();

		// No namespace

		Assert.assertTrue(
			equals(
				objectEntryFolder,
				ObjectEntryFolderSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"objectEntryFolder",
								new HashMap<String, Object>() {
									{
										put(
											"objectEntryFolderId",
											objectEntryFolder.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/objectEntryFolder"))));

		// Using the namespace headlessObject_v1_0

		Assert.assertTrue(
			equals(
				objectEntryFolder,
				ObjectEntryFolderSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessObject_v1_0",
								new GraphQLField(
									"objectEntryFolder",
									new HashMap<String, Object>() {
										{
											put(
												"objectEntryFolderId",
												objectEntryFolder.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessObject_v1_0",
						"Object/objectEntryFolder"))));
	}

	@Test
	public void testGraphQLGetObjectEntryFolderNotFound() throws Exception {
		Long irrelevantObjectEntryFolderId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"objectEntryFolder",
						new HashMap<String, Object>() {
							{
								put(
									"objectEntryFolderId",
									irrelevantObjectEntryFolderId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessObject_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessObject_v1_0",
						new GraphQLField(
							"objectEntryFolder",
							new HashMap<String, Object>() {
								{
									put(
										"objectEntryFolderId",
										irrelevantObjectEntryFolderId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ObjectEntryFolder
			testGraphQLGetObjectEntryFolder_addObjectEntryFolder()
		throws Exception {

		return testGraphQLObjectEntryFolder_addObjectEntryFolder();
	}

	@Test
	public void testGetObjectEntryFolderPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ObjectEntryFolder postObjectEntryFolder =
			testGetObjectEntryFolderPermissionsPage_addObjectEntryFolder();

		Page<Permission> page =
			objectEntryFolderResource.getObjectEntryFolderPermissionsPage(
				postObjectEntryFolder.getId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected ObjectEntryFolder
			testGetObjectEntryFolderPermissionsPage_addObjectEntryFolder()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCode()
		throws Exception {

		ObjectEntryFolder postObjectEntryFolder =
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder();

		ObjectEntryFolder getObjectEntryFolder =
			objectEntryFolderResource.
				getScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
					testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
						postObjectEntryFolder),
					postObjectEntryFolder.getExternalReferenceCode());

		assertEquals(postObjectEntryFolder, getObjectEntryFolder);
		assertValid(getObjectEntryFolder);
	}

	protected ObjectEntryFolder
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetScopeScopeKeyObjectEntryFolderByExternalReferenceCode()
		throws Exception {

		ObjectEntryFolder objectEntryFolder =
			testGraphQLGetScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder();

		// No namespace

		Assert.assertTrue(
			equals(
				objectEntryFolder,
				ObjectEntryFolderSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"scopeScopeKeyObjectEntryFolderByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"scopeKey",
											"\"" +
												testGraphQLGetScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
													objectEntryFolder) + "\"");
										put(
											"externalReferenceCode",
											"\"" +
												objectEntryFolder.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/scopeScopeKeyObjectEntryFolderByExternalReferenceCode"))));

		// Using the namespace headlessObject_v1_0

		Assert.assertTrue(
			equals(
				objectEntryFolder,
				ObjectEntryFolderSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessObject_v1_0",
								new GraphQLField(
									"scopeScopeKeyObjectEntryFolderByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"scopeKey",
												"\"" +
													testGraphQLGetScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
														objectEntryFolder) +
															"\"");
											put(
												"externalReferenceCode",
												"\"" +
													objectEntryFolder.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessObject_v1_0",
						"Object/scopeScopeKeyObjectEntryFolderByExternalReferenceCode"))));
	}

	protected String
			testGraphQLGetScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetScopeScopeKeyObjectEntryFolderByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantScopeKey = "\"" + RandomTestUtil.randomString() + "\"";
		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"scopeScopeKeyObjectEntryFolderByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put("scopeKey", irrelevantScopeKey);
								put(
									"externalReferenceCode",
									irrelevantExternalReferenceCode);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessObject_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessObject_v1_0",
						new GraphQLField(
							"scopeScopeKeyObjectEntryFolderByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put("scopeKey", irrelevantScopeKey);
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ObjectEntryFolder
			testGraphQLGetScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder()
		throws Exception {

		return testGraphQLObjectEntryFolder_addObjectEntryFolder();
	}

	@Test
	public void testGetScopeScopeKeyObjectEntryFoldersPage() throws Exception {
		String scopeKey =
			testGetScopeScopeKeyObjectEntryFoldersPage_getScopeKey();
		String irrelevantScopeKey =
			testGetScopeScopeKeyObjectEntryFoldersPage_getIrrelevantScopeKey();

		Page<ObjectEntryFolder> page =
			objectEntryFolderResource.getScopeScopeKeyObjectEntryFoldersPage(
				scopeKey, null, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantScopeKey != null) {
			ObjectEntryFolder irrelevantObjectEntryFolder =
				testGetScopeScopeKeyObjectEntryFoldersPage_addObjectEntryFolder(
					irrelevantScopeKey, randomIrrelevantObjectEntryFolder());

			page =
				objectEntryFolderResource.
					getScopeScopeKeyObjectEntryFoldersPage(
						irrelevantScopeKey, null, null, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantObjectEntryFolder,
				(List<ObjectEntryFolder>)page.getItems());
			assertValid(
				page,
				testGetScopeScopeKeyObjectEntryFoldersPage_getExpectedActions(
					irrelevantScopeKey));
		}

		ObjectEntryFolder objectEntryFolder1 =
			testGetScopeScopeKeyObjectEntryFoldersPage_addObjectEntryFolder(
				scopeKey, randomObjectEntryFolder());

		ObjectEntryFolder objectEntryFolder2 =
			testGetScopeScopeKeyObjectEntryFoldersPage_addObjectEntryFolder(
				scopeKey, randomObjectEntryFolder());

		page = objectEntryFolderResource.getScopeScopeKeyObjectEntryFoldersPage(
			scopeKey, null, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			objectEntryFolder1, (List<ObjectEntryFolder>)page.getItems());
		assertContains(
			objectEntryFolder2, (List<ObjectEntryFolder>)page.getItems());
		assertValid(
			page,
			testGetScopeScopeKeyObjectEntryFoldersPage_getExpectedActions(
				scopeKey));

		objectEntryFolderResource.deleteObjectEntryFolder(
			objectEntryFolder1.getId());

		objectEntryFolderResource.deleteObjectEntryFolder(
			objectEntryFolder2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetScopeScopeKeyObjectEntryFoldersPage_getExpectedActions(
				String scopeKey)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetScopeScopeKeyObjectEntryFoldersPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String scopeKey =
			testGetScopeScopeKeyObjectEntryFoldersPage_getScopeKey();

		ObjectEntryFolder objectEntryFolder1 = randomObjectEntryFolder();

		objectEntryFolder1 =
			testGetScopeScopeKeyObjectEntryFoldersPage_addObjectEntryFolder(
				scopeKey, objectEntryFolder1);

		for (EntityField entityField : entityFields) {
			Page<ObjectEntryFolder> page =
				objectEntryFolderResource.
					getScopeScopeKeyObjectEntryFoldersPage(
						scopeKey, null, null, null,
						getFilterString(
							entityField, "between", objectEntryFolder1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(objectEntryFolder1),
				(List<ObjectEntryFolder>)page.getItems());
		}
	}

	@Test
	public void testGetScopeScopeKeyObjectEntryFoldersPageWithFilterDoubleEquals()
		throws Exception {

		testGetScopeScopeKeyObjectEntryFoldersPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetScopeScopeKeyObjectEntryFoldersPageWithFilterStringContains()
		throws Exception {

		testGetScopeScopeKeyObjectEntryFoldersPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetScopeScopeKeyObjectEntryFoldersPageWithFilterStringEquals()
		throws Exception {

		testGetScopeScopeKeyObjectEntryFoldersPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetScopeScopeKeyObjectEntryFoldersPageWithFilterStringStartsWith()
		throws Exception {

		testGetScopeScopeKeyObjectEntryFoldersPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetScopeScopeKeyObjectEntryFoldersPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String scopeKey =
			testGetScopeScopeKeyObjectEntryFoldersPage_getScopeKey();

		ObjectEntryFolder objectEntryFolder1 =
			testGetScopeScopeKeyObjectEntryFoldersPage_addObjectEntryFolder(
				scopeKey, randomObjectEntryFolder());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ObjectEntryFolder objectEntryFolder2 =
			testGetScopeScopeKeyObjectEntryFoldersPage_addObjectEntryFolder(
				scopeKey, randomObjectEntryFolder());

		for (EntityField entityField : entityFields) {
			Page<ObjectEntryFolder> page =
				objectEntryFolderResource.
					getScopeScopeKeyObjectEntryFoldersPage(
						scopeKey, null, null, null,
						getFilterString(
							entityField, operator, objectEntryFolder1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(objectEntryFolder1),
				(List<ObjectEntryFolder>)page.getItems());
		}
	}

	@Test
	public void testGetScopeScopeKeyObjectEntryFoldersPageWithPagination()
		throws Exception {

		String scopeKey =
			testGetScopeScopeKeyObjectEntryFoldersPage_getScopeKey();

		Page<ObjectEntryFolder> objectEntryFoldersPage =
			objectEntryFolderResource.getScopeScopeKeyObjectEntryFoldersPage(
				scopeKey, null, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			objectEntryFoldersPage.getTotalCount());

		ObjectEntryFolder objectEntryFolder1 =
			testGetScopeScopeKeyObjectEntryFoldersPage_addObjectEntryFolder(
				scopeKey, randomObjectEntryFolder());

		ObjectEntryFolder objectEntryFolder2 =
			testGetScopeScopeKeyObjectEntryFoldersPage_addObjectEntryFolder(
				scopeKey, randomObjectEntryFolder());

		ObjectEntryFolder objectEntryFolder3 =
			testGetScopeScopeKeyObjectEntryFoldersPage_addObjectEntryFolder(
				scopeKey, randomObjectEntryFolder());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ObjectEntryFolder> page1 =
				objectEntryFolderResource.
					getScopeScopeKeyObjectEntryFoldersPage(
						scopeKey, null, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				objectEntryFolder1, (List<ObjectEntryFolder>)page1.getItems());

			Page<ObjectEntryFolder> page2 =
				objectEntryFolderResource.
					getScopeScopeKeyObjectEntryFoldersPage(
						scopeKey, null, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				objectEntryFolder2, (List<ObjectEntryFolder>)page2.getItems());

			Page<ObjectEntryFolder> page3 =
				objectEntryFolderResource.
					getScopeScopeKeyObjectEntryFoldersPage(
						scopeKey, null, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				objectEntryFolder3, (List<ObjectEntryFolder>)page3.getItems());
		}
		else {
			Page<ObjectEntryFolder> page1 =
				objectEntryFolderResource.
					getScopeScopeKeyObjectEntryFoldersPage(
						scopeKey, null, null, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<ObjectEntryFolder> objectEntryFolders1 =
				(List<ObjectEntryFolder>)page1.getItems();

			Assert.assertEquals(
				objectEntryFolders1.toString(), totalCount + 2,
				objectEntryFolders1.size());

			Page<ObjectEntryFolder> page2 =
				objectEntryFolderResource.
					getScopeScopeKeyObjectEntryFoldersPage(
						scopeKey, null, null, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ObjectEntryFolder> objectEntryFolders2 =
				(List<ObjectEntryFolder>)page2.getItems();

			Assert.assertEquals(
				objectEntryFolders2.toString(), 1, objectEntryFolders2.size());

			Page<ObjectEntryFolder> page3 =
				objectEntryFolderResource.
					getScopeScopeKeyObjectEntryFoldersPage(
						scopeKey, null, null, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				objectEntryFolder1, (List<ObjectEntryFolder>)page3.getItems());
			assertContains(
				objectEntryFolder2, (List<ObjectEntryFolder>)page3.getItems());
			assertContains(
				objectEntryFolder3, (List<ObjectEntryFolder>)page3.getItems());
		}
	}

	@Test
	public void testGetScopeScopeKeyObjectEntryFoldersPageWithSortDateTime()
		throws Exception {

		testGetScopeScopeKeyObjectEntryFoldersPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, objectEntryFolder1, objectEntryFolder2) -> {
				BeanTestUtil.setProperty(
					objectEntryFolder1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetScopeScopeKeyObjectEntryFoldersPageWithSortDouble()
		throws Exception {

		testGetScopeScopeKeyObjectEntryFoldersPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, objectEntryFolder1, objectEntryFolder2) -> {
				BeanTestUtil.setProperty(
					objectEntryFolder1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					objectEntryFolder2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetScopeScopeKeyObjectEntryFoldersPageWithSortInteger()
		throws Exception {

		testGetScopeScopeKeyObjectEntryFoldersPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, objectEntryFolder1, objectEntryFolder2) -> {
				BeanTestUtil.setProperty(
					objectEntryFolder1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					objectEntryFolder2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetScopeScopeKeyObjectEntryFoldersPageWithSortString()
		throws Exception {

		testGetScopeScopeKeyObjectEntryFoldersPageWithSort(
			EntityField.Type.STRING,
			(entityField, objectEntryFolder1, objectEntryFolder2) -> {
				Class<?> clazz = objectEntryFolder1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						objectEntryFolder1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						objectEntryFolder2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						objectEntryFolder1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						objectEntryFolder2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						objectEntryFolder1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						objectEntryFolder2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetScopeScopeKeyObjectEntryFoldersPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, ObjectEntryFolder, ObjectEntryFolder, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String scopeKey =
			testGetScopeScopeKeyObjectEntryFoldersPage_getScopeKey();

		ObjectEntryFolder objectEntryFolder1 = randomObjectEntryFolder();
		ObjectEntryFolder objectEntryFolder2 = randomObjectEntryFolder();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, objectEntryFolder1, objectEntryFolder2);
		}

		objectEntryFolder1 =
			testGetScopeScopeKeyObjectEntryFoldersPage_addObjectEntryFolder(
				scopeKey, objectEntryFolder1);

		objectEntryFolder2 =
			testGetScopeScopeKeyObjectEntryFoldersPage_addObjectEntryFolder(
				scopeKey, objectEntryFolder2);

		Page<ObjectEntryFolder> page =
			objectEntryFolderResource.getScopeScopeKeyObjectEntryFoldersPage(
				scopeKey, null, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ObjectEntryFolder> ascPage =
				objectEntryFolderResource.
					getScopeScopeKeyObjectEntryFoldersPage(
						scopeKey, null, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				objectEntryFolder1,
				(List<ObjectEntryFolder>)ascPage.getItems());
			assertContains(
				objectEntryFolder2,
				(List<ObjectEntryFolder>)ascPage.getItems());

			Page<ObjectEntryFolder> descPage =
				objectEntryFolderResource.
					getScopeScopeKeyObjectEntryFoldersPage(
						scopeKey, null, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				objectEntryFolder2,
				(List<ObjectEntryFolder>)descPage.getItems());
			assertContains(
				objectEntryFolder1,
				(List<ObjectEntryFolder>)descPage.getItems());
		}
	}

	protected ObjectEntryFolder
			testGetScopeScopeKeyObjectEntryFoldersPage_addObjectEntryFolder(
				String scopeKey, ObjectEntryFolder objectEntryFolder)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String testGetScopeScopeKeyObjectEntryFoldersPage_getScopeKey()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetScopeScopeKeyObjectEntryFoldersPage_getIrrelevantScopeKey()
		throws Exception {

		return null;
	}

	@Test
	public void testPatchObjectEntryFolder() throws Exception {
		ObjectEntryFolder postObjectEntryFolder =
			testPatchObjectEntryFolder_addObjectEntryFolder();

		ObjectEntryFolder randomPatchObjectEntryFolder =
			randomPatchObjectEntryFolder();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ObjectEntryFolder patchObjectEntryFolder =
			objectEntryFolderResource.patchObjectEntryFolder(
				postObjectEntryFolder.getId(), randomPatchObjectEntryFolder);

		ObjectEntryFolder expectedPatchObjectEntryFolder =
			postObjectEntryFolder.clone();

		BeanTestUtil.copyProperties(
			randomPatchObjectEntryFolder, expectedPatchObjectEntryFolder);

		ObjectEntryFolder getObjectEntryFolder =
			objectEntryFolderResource.getObjectEntryFolder(
				patchObjectEntryFolder.getId());

		assertEquals(expectedPatchObjectEntryFolder, getObjectEntryFolder);
		assertValid(getObjectEntryFolder);
	}

	protected ObjectEntryFolder
			testPatchObjectEntryFolder_addObjectEntryFolder()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchScopeScopeKeyObjectEntryFolderByExternalReferenceCode()
		throws Exception {

		ObjectEntryFolder postObjectEntryFolder =
			testPatchScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder();

		ObjectEntryFolder randomPatchObjectEntryFolder =
			randomPatchObjectEntryFolder();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ObjectEntryFolder patchObjectEntryFolder =
			objectEntryFolderResource.
				patchScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
					postObjectEntryFolder.getScopeKey(),
					postObjectEntryFolder.getExternalReferenceCode(),
					randomPatchObjectEntryFolder);

		ObjectEntryFolder expectedPatchObjectEntryFolder =
			postObjectEntryFolder.clone();

		BeanTestUtil.copyProperties(
			randomPatchObjectEntryFolder, expectedPatchObjectEntryFolder);

		ObjectEntryFolder getObjectEntryFolder =
			objectEntryFolderResource.
				getScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
					patchObjectEntryFolder.getScopeKey(),
					patchObjectEntryFolder.getExternalReferenceCode());

		assertEquals(expectedPatchObjectEntryFolder, getObjectEntryFolder);
		assertValid(getObjectEntryFolder);
	}

	protected ObjectEntryFolder
			testPatchScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostScopeScopeKeyObjectEntryFolder() throws Exception {
		ObjectEntryFolder randomObjectEntryFolder = randomObjectEntryFolder();

		ObjectEntryFolder postObjectEntryFolder =
			testPostScopeScopeKeyObjectEntryFolder_addObjectEntryFolder(
				randomObjectEntryFolder);

		assertEquals(randomObjectEntryFolder, postObjectEntryFolder);
		assertValid(postObjectEntryFolder);
	}

	protected ObjectEntryFolder
			testPostScopeScopeKeyObjectEntryFolder_addObjectEntryFolder(
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeRestore()
		throws Exception {

		ObjectEntryFolder randomObjectEntryFolder = randomObjectEntryFolder();

		ObjectEntryFolder postObjectEntryFolder =
			testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeRestore_addObjectEntryFolder(
				randomObjectEntryFolder);

		assertEquals(randomObjectEntryFolder, postObjectEntryFolder);
		assertValid(postObjectEntryFolder);
	}

	protected ObjectEntryFolder
			testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeRestore_addObjectEntryFolder(
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeSubscribe()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ObjectEntryFolder objectEntryFolder =
			testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeSubscribe_addObjectEntryFolder();

		assertHttpResponseStatusCode(
			204,
			objectEntryFolderResource.
				postScopeScopeKeyObjectEntryFolderByExternalReferenceCodeSubscribeHttpResponse(
					testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeSubscribe_getScopeKey(
						objectEntryFolder),
					objectEntryFolder.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			objectEntryFolderResource.
				postScopeScopeKeyObjectEntryFolderByExternalReferenceCodeSubscribeHttpResponse(
					testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeSubscribe_getScopeKey(
						objectEntryFolder),
					"-"));
	}

	protected String
			testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeSubscribe_getScopeKey(
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ObjectEntryFolder
			testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeSubscribe_addObjectEntryFolder()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeUnsubscribe()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ObjectEntryFolder objectEntryFolder =
			testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeUnsubscribe_addObjectEntryFolder();

		assertHttpResponseStatusCode(
			204,
			objectEntryFolderResource.
				postScopeScopeKeyObjectEntryFolderByExternalReferenceCodeUnsubscribeHttpResponse(
					testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeUnsubscribe_getScopeKey(
						objectEntryFolder),
					objectEntryFolder.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			objectEntryFolderResource.
				postScopeScopeKeyObjectEntryFolderByExternalReferenceCodeUnsubscribeHttpResponse(
					testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeUnsubscribe_getScopeKey(
						objectEntryFolder),
					"-"));
	}

	protected String
			testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeUnsubscribe_getScopeKey(
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ObjectEntryFolder
			testPostScopeScopeKeyObjectEntryFolderByExternalReferenceCodeUnsubscribe_addObjectEntryFolder()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutObjectEntryFolder() throws Exception {
		ObjectEntryFolder postObjectEntryFolder =
			testPutObjectEntryFolder_addObjectEntryFolder();

		ObjectEntryFolder randomObjectEntryFolder = randomObjectEntryFolder();

		ObjectEntryFolder putObjectEntryFolder =
			objectEntryFolderResource.putObjectEntryFolder(
				postObjectEntryFolder.getId(), randomObjectEntryFolder);

		assertEquals(randomObjectEntryFolder, putObjectEntryFolder);
		assertValid(putObjectEntryFolder);

		Assert.assertNull(putObjectEntryFolder.getPermissions());

		ObjectEntryFolder getObjectEntryFolder =
			objectEntryFolderResource.getObjectEntryFolder(
				putObjectEntryFolder.getId());

		assertEquals(randomObjectEntryFolder, getObjectEntryFolder);
		assertValid(getObjectEntryFolder);

		ObjectEntryFolder randomPermissionsObjectEntryFolder =
			randomPermissionsObjectEntryFolder();

		putObjectEntryFolder = objectEntryFolderResource.putObjectEntryFolder(
			postObjectEntryFolder.getId(), randomPermissionsObjectEntryFolder);

		assertEquals(randomPermissionsObjectEntryFolder, putObjectEntryFolder);
		assertValid(putObjectEntryFolder);

		Assert.assertNull(putObjectEntryFolder.getPermissions());

		putObjectEntryFolder =
			permissionsObjectEntryFolderResource.putObjectEntryFolder(
				postObjectEntryFolder.getId(),
				randomPermissionsObjectEntryFolder);

		Assert.assertNotNull(putObjectEntryFolder.getPermissions());
	}

	protected ObjectEntryFolder testPutObjectEntryFolder_addObjectEntryFolder()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutObjectEntryFolderPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ObjectEntryFolder objectEntryFolder =
			testPutObjectEntryFolderPermissionsPage_addObjectEntryFolder();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			objectEntryFolderResource.
				putObjectEntryFolderPermissionsPageHttpResponse(
					objectEntryFolder.getId(),
					new Permission[] {
						new Permission() {
							{
								setActionIds(new String[] {"VIEW"});
								setRoleName(role.getName());
							}
						}
					}));

		assertHttpResponseStatusCode(
			404,
			objectEntryFolderResource.
				putObjectEntryFolderPermissionsPageHttpResponse(
					0L,
					new Permission[] {
						new Permission() {
							{
								setActionIds(new String[] {"-"});
								setRoleName("-");
							}
						}
					}));
	}

	protected ObjectEntryFolder
			testPutObjectEntryFolderPermissionsPage_addObjectEntryFolder()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode()
		throws Exception {

		ObjectEntryFolder postObjectEntryFolder =
			testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder();

		ObjectEntryFolder randomObjectEntryFolder = randomObjectEntryFolder();

		ObjectEntryFolder putObjectEntryFolder =
			objectEntryFolderResource.
				putScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
					testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
						postObjectEntryFolder),
					postObjectEntryFolder.getExternalReferenceCode(),
					randomObjectEntryFolder);

		assertEquals(randomObjectEntryFolder, putObjectEntryFolder);
		assertValid(putObjectEntryFolder);

		ObjectEntryFolder getObjectEntryFolder =
			objectEntryFolderResource.
				getScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
					testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
						putObjectEntryFolder),
					putObjectEntryFolder.getExternalReferenceCode());

		assertEquals(randomObjectEntryFolder, getObjectEntryFolder);
		assertValid(getObjectEntryFolder);

		ObjectEntryFolder newObjectEntryFolder =
			testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode_createObjectEntryFolder();

		putObjectEntryFolder =
			objectEntryFolderResource.
				putScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
					testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
						newObjectEntryFolder),
					newObjectEntryFolder.getExternalReferenceCode(),
					newObjectEntryFolder);

		assertEquals(newObjectEntryFolder, putObjectEntryFolder);
		assertValid(putObjectEntryFolder);

		getObjectEntryFolder =
			objectEntryFolderResource.
				getScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
					testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
						putObjectEntryFolder),
					putObjectEntryFolder.getExternalReferenceCode());

		assertEquals(newObjectEntryFolder, getObjectEntryFolder);

		Assert.assertEquals(
			newObjectEntryFolder.getExternalReferenceCode(),
			putObjectEntryFolder.getExternalReferenceCode());
	}

	protected ObjectEntryFolder
			testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode_addObjectEntryFolder()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode_getScopeKey(
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ObjectEntryFolder
			testPutScopeScopeKeyObjectEntryFolderByExternalReferenceCode_createObjectEntryFolder()
		throws Exception {

		return randomObjectEntryFolder();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ObjectEntryFolder objectEntryFolder1 =
			testBatchEngineDeleteImportTask_addObjectEntryFolder();

		testBatchEngineDeleteImportTask_deleteObjectEntryFolder(
			200, null, objectEntryFolder1.getId());

		assertHttpResponseStatusCode(
			404,
			objectEntryFolderResource.getObjectEntryFolderHttpResponse(
				objectEntryFolder1.getId()));
	}

	protected ObjectEntryFolder
			testBatchEngineDeleteImportTask_addObjectEntryFolder()
		throws Exception {

		return testDeleteObjectEntryFolder_addObjectEntryFolder();
	}

	protected void testBatchEngineDeleteImportTask_deleteObjectEntryFolder(
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
				"com.liferay.headless.object.dto.v1_0.ObjectEntryFolder", null,
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

	protected ObjectEntryFolder
			testGraphQLObjectEntryFolder_addObjectEntryFolder()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		ObjectEntryFolder objectEntryFolder,
		List<ObjectEntryFolder> objectEntryFolders) {

		boolean contains = false;

		for (ObjectEntryFolder item : objectEntryFolders) {
			if (equals(objectEntryFolder, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			objectEntryFolders + " does not contain " + objectEntryFolder,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ObjectEntryFolder objectEntryFolder1,
		ObjectEntryFolder objectEntryFolder2) {

		Assert.assertTrue(
			objectEntryFolder1 + " does not equal " + objectEntryFolder2,
			equals(objectEntryFolder1, objectEntryFolder2));
	}

	protected void assertEquals(
		List<ObjectEntryFolder> objectEntryFolders1,
		List<ObjectEntryFolder> objectEntryFolders2) {

		Assert.assertEquals(
			objectEntryFolders1.size(), objectEntryFolders2.size());

		for (int i = 0; i < objectEntryFolders1.size(); i++) {
			ObjectEntryFolder objectEntryFolder1 = objectEntryFolders1.get(i);
			ObjectEntryFolder objectEntryFolder2 = objectEntryFolders2.get(i);

			assertEquals(objectEntryFolder1, objectEntryFolder2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ObjectEntryFolder> objectEntryFolders1,
		List<ObjectEntryFolder> objectEntryFolders2) {

		Assert.assertEquals(
			objectEntryFolders1.size(), objectEntryFolders2.size());

		for (ObjectEntryFolder objectEntryFolder1 : objectEntryFolders1) {
			boolean contains = false;

			for (ObjectEntryFolder objectEntryFolder2 : objectEntryFolders2) {
				if (equals(objectEntryFolder1, objectEntryFolder2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				objectEntryFolders2 + " does not contain " + objectEntryFolder1,
				contains);
		}
	}

	protected void assertValid(ObjectEntryFolder objectEntryFolder)
		throws Exception {

		boolean valid = true;

		if (objectEntryFolder.getDateCreated() == null) {
			valid = false;
		}

		if (objectEntryFolder.getDateModified() == null) {
			valid = false;
		}

		if (objectEntryFolder.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (objectEntryFolder.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (objectEntryFolder.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (objectEntryFolder.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (objectEntryFolder.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("label", additionalAssertFieldName)) {
				if (objectEntryFolder.getLabel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("label_i18n", additionalAssertFieldName)) {
				if (objectEntryFolder.getLabel_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfObjectEntries", additionalAssertFieldName)) {

				if (objectEntryFolder.getNumberOfObjectEntries() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfObjectEntryFolders", additionalAssertFieldName)) {

				if (objectEntryFolder.getNumberOfObjectEntryFolders() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"parentObjectEntryFolderBrief",
					additionalAssertFieldName)) {

				if (objectEntryFolder.getParentObjectEntryFolderBrief() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"parentObjectEntryFolderExternalReferenceCode",
					additionalAssertFieldName)) {

				if (objectEntryFolder.
						getParentObjectEntryFolderExternalReferenceCode() ==
							null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"parentObjectEntryFolderId", additionalAssertFieldName)) {

				if (objectEntryFolder.getParentObjectEntryFolderId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (objectEntryFolder.getPermissions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("removedBy", additionalAssertFieldName)) {
				if (objectEntryFolder.getRemovedBy() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("removedDate", additionalAssertFieldName)) {
				if (objectEntryFolder.getRemovedDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("scopeId", additionalAssertFieldName)) {
				if (objectEntryFolder.getScopeId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("scopeKey", additionalAssertFieldName)) {
				if (objectEntryFolder.getScopeKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (objectEntryFolder.getStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (objectEntryFolder.getTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (objectEntryFolder.getViewableBy() == null) {
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

	protected void assertValid(Page<ObjectEntryFolder> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ObjectEntryFolder> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ObjectEntryFolder> objectEntryFolders =
			page.getItems();

		int size = objectEntryFolders.size();

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
					com.liferay.headless.object.dto.v1_0.ObjectEntryFolder.
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
		ObjectEntryFolder objectEntryFolder1,
		ObjectEntryFolder objectEntryFolder2) {

		if (objectEntryFolder1 == objectEntryFolder2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)objectEntryFolder1.getActions(),
						(Map)objectEntryFolder2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectEntryFolder1.getCreator(),
						objectEntryFolder2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectEntryFolder1.getDateCreated(),
						objectEntryFolder2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectEntryFolder1.getDateModified(),
						objectEntryFolder2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectEntryFolder1.getDescription(),
						objectEntryFolder2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectEntryFolder1.getExternalReferenceCode(),
						objectEntryFolder2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectEntryFolder1.getId(),
						objectEntryFolder2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("label", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectEntryFolder1.getLabel(),
						objectEntryFolder2.getLabel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("label_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)objectEntryFolder1.getLabel_i18n(),
						(Map)objectEntryFolder2.getLabel_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfObjectEntries", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectEntryFolder1.getNumberOfObjectEntries(),
						objectEntryFolder2.getNumberOfObjectEntries())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfObjectEntryFolders", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectEntryFolder1.getNumberOfObjectEntryFolders(),
						objectEntryFolder2.getNumberOfObjectEntryFolders())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"parentObjectEntryFolderBrief",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectEntryFolder1.getParentObjectEntryFolderBrief(),
						objectEntryFolder2.getParentObjectEntryFolderBrief())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"parentObjectEntryFolderExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectEntryFolder1.
							getParentObjectEntryFolderExternalReferenceCode(),
						objectEntryFolder2.
							getParentObjectEntryFolderExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"parentObjectEntryFolderId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectEntryFolder1.getParentObjectEntryFolderId(),
						objectEntryFolder2.getParentObjectEntryFolderId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectEntryFolder1.getPermissions(),
						objectEntryFolder2.getPermissions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("removedBy", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectEntryFolder1.getRemovedBy(),
						objectEntryFolder2.getRemovedBy())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("removedDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectEntryFolder1.getRemovedDate(),
						objectEntryFolder2.getRemovedDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("scopeId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectEntryFolder1.getScopeId(),
						objectEntryFolder2.getScopeId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("scopeKey", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectEntryFolder1.getScopeKey(),
						objectEntryFolder2.getScopeKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectEntryFolder1.getStatus(),
						objectEntryFolder2.getStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectEntryFolder1.getTitle(),
						objectEntryFolder2.getTitle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectEntryFolder1.getViewableBy(),
						objectEntryFolder2.getViewableBy())) {

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

		if (!(_objectEntryFolderResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_objectEntryFolderResource;

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
		ObjectEntryFolder objectEntryFolder) {

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
				Date date = objectEntryFolder.getDateCreated();

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

				sb.append(_format.format(objectEntryFolder.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = objectEntryFolder.getDateModified();

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

				sb.append(_format.format(objectEntryFolder.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = objectEntryFolder.getDescription();

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
			Object object = objectEntryFolder.getExternalReferenceCode();

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
			Object object = objectEntryFolder.getLabel();

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

		if (entityFieldName.equals("label_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("numberOfObjectEntries")) {
			sb.append(
				String.valueOf(objectEntryFolder.getNumberOfObjectEntries()));

			return sb.toString();
		}

		if (entityFieldName.equals("numberOfObjectEntryFolders")) {
			sb.append(
				String.valueOf(
					objectEntryFolder.getNumberOfObjectEntryFolders()));

			return sb.toString();
		}

		if (entityFieldName.equals("parentObjectEntryFolderBrief")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals(
				"parentObjectEntryFolderExternalReferenceCode")) {

			Object object =
				objectEntryFolder.
					getParentObjectEntryFolderExternalReferenceCode();

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

		if (entityFieldName.equals("parentObjectEntryFolderId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("permissions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("removedBy")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("removedDate")) {
			if (operator.equals("between")) {
				Date date = objectEntryFolder.getRemovedDate();

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

				sb.append(_format.format(objectEntryFolder.getRemovedDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("scopeId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("scopeKey")) {
			Object object = objectEntryFolder.getScopeKey();

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

		if (entityFieldName.equals("status")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("title")) {
			Object object = objectEntryFolder.getTitle();

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

		if (entityFieldName.equals("viewableBy")) {
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

	protected ObjectEntryFolder randomObjectEntryFolder() throws Exception {
		return new ObjectEntryFolder() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				label = StringUtil.toLowerCase(RandomTestUtil.randomString());
				numberOfObjectEntries = RandomTestUtil.randomInt();
				numberOfObjectEntryFolders = RandomTestUtil.randomInt();
				parentObjectEntryFolderExternalReferenceCode =
					StringUtil.toLowerCase(RandomTestUtil.randomString());
				parentObjectEntryFolderId = RandomTestUtil.randomLong();
				removedDate = RandomTestUtil.nextDate();
				scopeId = RandomTestUtil.randomLong();
				scopeKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected ObjectEntryFolder randomIrrelevantObjectEntryFolder()
		throws Exception {

		ObjectEntryFolder randomIrrelevantObjectEntryFolder =
			randomObjectEntryFolder();

		return randomIrrelevantObjectEntryFolder;
	}

	protected ObjectEntryFolder randomPatchObjectEntryFolder()
		throws Exception {

		return randomObjectEntryFolder();
	}

	protected ObjectEntryFolder randomPermissionsObjectEntryFolder()
		throws Exception {

		ObjectEntryFolder objectEntryFolder = randomObjectEntryFolder();

		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		objectEntryFolder.setPermissions(
			new Permission[] {
				new Permission() {
					{
						setActionIds(new String[] {"VIEW"});
						setRoleName(role.getName());
					}
				}
			});

		return objectEntryFolder;
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

	protected ObjectEntryFolderResource objectEntryFolderResource;
	protected ImportTaskResource importTaskResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected ObjectEntryFolderResource permissionsObjectEntryFolderResource;
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
		LogFactoryUtil.getLog(BaseObjectEntryFolderResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.object.resource.v1_0.ObjectEntryFolderResource
		_objectEntryFolderResource;

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