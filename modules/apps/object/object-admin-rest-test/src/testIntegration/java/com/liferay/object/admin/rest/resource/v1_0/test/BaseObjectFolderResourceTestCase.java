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
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectFolder;
import com.liferay.object.admin.rest.client.http.HttpInvoker;
import com.liferay.object.admin.rest.client.pagination.Page;
import com.liferay.object.admin.rest.client.pagination.Pagination;
import com.liferay.object.admin.rest.client.resource.v1_0.ObjectFolderResource;
import com.liferay.object.admin.rest.client.serdes.v1_0.ObjectFolderSerDes;
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
public abstract class BaseObjectFolderResourceTestCase {

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

		_objectFolderResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		objectFolderResource = ObjectFolderResource.builder(
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

		ObjectFolder objectFolder1 = randomObjectFolder();

		String json = objectMapper.writeValueAsString(objectFolder1);

		ObjectFolder objectFolder2 = ObjectFolderSerDes.toDTO(json);

		Assert.assertTrue(equals(objectFolder1, objectFolder2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ObjectFolder objectFolder = randomObjectFolder();

		String json1 = objectMapper.writeValueAsString(objectFolder);
		String json2 = ObjectFolderSerDes.toJSON(objectFolder);

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

		ObjectFolder objectFolder = randomObjectFolder();

		objectFolder.setExternalReferenceCode(regex);
		objectFolder.setName(regex);

		String json = ObjectFolderSerDes.toJSON(objectFolder);

		Assert.assertFalse(json.contains(regex));

		objectFolder = ObjectFolderSerDes.toDTO(json);

		Assert.assertEquals(regex, objectFolder.getExternalReferenceCode());
		Assert.assertEquals(regex, objectFolder.getName());
	}

	@Test
	public void testDeleteObjectFolder() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ObjectFolder objectFolder = testDeleteObjectFolder_addObjectFolder();

		assertHttpResponseStatusCode(
			204,
			objectFolderResource.deleteObjectFolderHttpResponse(
				objectFolder.getId()));

		assertHttpResponseStatusCode(
			404,
			objectFolderResource.getObjectFolderHttpResponse(
				objectFolder.getId()));
		assertHttpResponseStatusCode(
			404, objectFolderResource.getObjectFolderHttpResponse(0L));
	}

	protected ObjectFolder testDeleteObjectFolder_addObjectFolder()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteObjectFolder() throws Exception {

		// No namespace

		ObjectFolder objectFolder1 =
			testGraphQLDeleteObjectFolder_addObjectFolder();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteObjectFolder",
						new HashMap<String, Object>() {
							{
								put("objectFolderId", objectFolder1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteObjectFolder"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"objectFolder",
					new HashMap<String, Object>() {
						{
							put("objectFolderId", objectFolder1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace objectAdmin_v1_0

		ObjectFolder objectFolder2 =
			testGraphQLDeleteObjectFolder_addObjectFolder();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"objectAdmin_v1_0",
						new GraphQLField(
							"deleteObjectFolder",
							new HashMap<String, Object>() {
								{
									put(
										"objectFolderId",
										objectFolder2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/objectAdmin_v1_0",
				"Object/deleteObjectFolder"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"objectAdmin_v1_0",
					new GraphQLField(
						"objectFolder",
						new HashMap<String, Object>() {
							{
								put("objectFolderId", objectFolder2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ObjectFolder testGraphQLDeleteObjectFolder_addObjectFolder()
		throws Exception {

		return testGraphQLObjectFolder_addObjectFolder();
	}

	@Test
	public void testDeleteObjectFolderBatch() throws Exception {
		ObjectFolder objectFolder1 =
			testDeleteObjectFolderBatch_addObjectFolder();

		testDeleteObjectFolderBatch_deleteObjectFolder(
			202, null, objectFolder1.getId());

		assertHttpResponseStatusCode(
			404,
			objectFolderResource.getObjectFolderHttpResponse(
				objectFolder1.getId()));
	}

	protected ObjectFolder testDeleteObjectFolderBatch_addObjectFolder()
		throws Exception {

		return testDeleteObjectFolder_addObjectFolder();
	}

	protected void testDeleteObjectFolderBatch_deleteObjectFolder(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			objectFolderResource.deleteObjectFolderBatchHttpResponse(
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
	public void testGetObjectFolder() throws Exception {
		ObjectFolder postObjectFolder = testGetObjectFolder_addObjectFolder();

		ObjectFolder getObjectFolder = objectFolderResource.getObjectFolder(
			postObjectFolder.getId());

		assertEquals(postObjectFolder, getObjectFolder);
		assertValid(getObjectFolder);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		ObjectFolder postObjectFolder = testGetObjectFolder_addObjectFolder();

		ObjectFolder getObjectFolder = objectFolderResource.getObjectFolder(
			postObjectFolder.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.object.admin.rest.dto.v1_0.ObjectFolder"
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

		Object item = vulcanCRUDItemDelegate.getItem(postObjectFolder.getId());

		assertEquals(
			getObjectFolder, ObjectFolderSerDes.toDTO(item.toString()));
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

	protected ObjectFolder testGetObjectFolder_addObjectFolder()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetObjectFolder() throws Exception {
		ObjectFolder objectFolder =
			testGraphQLGetObjectFolder_addObjectFolder();

		// No namespace

		Assert.assertTrue(
			equals(
				objectFolder,
				ObjectFolderSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"objectFolder",
								new HashMap<String, Object>() {
									{
										put(
											"objectFolderId",
											objectFolder.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/objectFolder"))));

		// Using the namespace objectAdmin_v1_0

		Assert.assertTrue(
			equals(
				objectFolder,
				ObjectFolderSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"objectAdmin_v1_0",
								new GraphQLField(
									"objectFolder",
									new HashMap<String, Object>() {
										{
											put(
												"objectFolderId",
												objectFolder.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/objectAdmin_v1_0",
						"Object/objectFolder"))));
	}

	@Test
	public void testGraphQLGetObjectFolderNotFound() throws Exception {
		Long irrelevantObjectFolderId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"objectFolder",
						new HashMap<String, Object>() {
							{
								put("objectFolderId", irrelevantObjectFolderId);
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
							"objectFolder",
							new HashMap<String, Object>() {
								{
									put(
										"objectFolderId",
										irrelevantObjectFolderId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ObjectFolder testGraphQLGetObjectFolder_addObjectFolder()
		throws Exception {

		return testGraphQLObjectFolder_addObjectFolder();
	}

	@Test
	public void testGetObjectFolderByExternalReferenceCode() throws Exception {
		ObjectFolder postObjectFolder =
			testGetObjectFolderByExternalReferenceCode_addObjectFolder();

		ObjectFolder getObjectFolder =
			objectFolderResource.getObjectFolderByExternalReferenceCode(
				postObjectFolder.getExternalReferenceCode());

		assertEquals(postObjectFolder, getObjectFolder);
		assertValid(getObjectFolder);
	}

	protected ObjectFolder
			testGetObjectFolderByExternalReferenceCode_addObjectFolder()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetObjectFolderByExternalReferenceCode()
		throws Exception {

		ObjectFolder objectFolder =
			testGraphQLGetObjectFolderByExternalReferenceCode_addObjectFolder();

		// No namespace

		Assert.assertTrue(
			equals(
				objectFolder,
				ObjectFolderSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"objectFolderByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												objectFolder.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/objectFolderByExternalReferenceCode"))));

		// Using the namespace objectAdmin_v1_0

		Assert.assertTrue(
			equals(
				objectFolder,
				ObjectFolderSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"objectAdmin_v1_0",
								new GraphQLField(
									"objectFolderByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													objectFolder.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/objectAdmin_v1_0",
						"Object/objectFolderByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetObjectFolderByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"objectFolderByExternalReferenceCode",
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

		// Using the namespace objectAdmin_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"objectAdmin_v1_0",
						new GraphQLField(
							"objectFolderByExternalReferenceCode",
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

	protected ObjectFolder
			testGraphQLGetObjectFolderByExternalReferenceCode_addObjectFolder()
		throws Exception {

		return testGraphQLObjectFolder_addObjectFolder();
	}

	@Test
	public void testGetObjectFoldersPage() throws Exception {
		Page<ObjectFolder> page = objectFolderResource.getObjectFoldersPage(
			null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		ObjectFolder objectFolder1 = testGetObjectFoldersPage_addObjectFolder(
			randomObjectFolder());

		ObjectFolder objectFolder2 = testGetObjectFoldersPage_addObjectFolder(
			randomObjectFolder());

		page = objectFolderResource.getObjectFoldersPage(
			null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(objectFolder1, (List<ObjectFolder>)page.getItems());
		assertContains(objectFolder2, (List<ObjectFolder>)page.getItems());
		assertValid(page, testGetObjectFoldersPage_getExpectedActions());

		objectFolderResource.deleteObjectFolder(objectFolder1.getId());

		objectFolderResource.deleteObjectFolder(objectFolder2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetObjectFoldersPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetObjectFoldersPageWithPagination() throws Exception {
		Page<ObjectFolder> objectFoldersPage =
			objectFolderResource.getObjectFoldersPage(null, null);

		int totalCount = GetterUtil.getInteger(
			objectFoldersPage.getTotalCount());

		ObjectFolder objectFolder1 = testGetObjectFoldersPage_addObjectFolder(
			randomObjectFolder());

		ObjectFolder objectFolder2 = testGetObjectFoldersPage_addObjectFolder(
			randomObjectFolder());

		ObjectFolder objectFolder3 = testGetObjectFoldersPage_addObjectFolder(
			randomObjectFolder());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ObjectFolder> page1 =
				objectFolderResource.getObjectFoldersPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(objectFolder1, (List<ObjectFolder>)page1.getItems());

			Page<ObjectFolder> page2 =
				objectFolderResource.getObjectFoldersPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(objectFolder2, (List<ObjectFolder>)page2.getItems());

			Page<ObjectFolder> page3 =
				objectFolderResource.getObjectFoldersPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(objectFolder3, (List<ObjectFolder>)page3.getItems());
		}
		else {
			Page<ObjectFolder> page1 =
				objectFolderResource.getObjectFoldersPage(
					null, Pagination.of(1, totalCount + 2));

			List<ObjectFolder> objectFolders1 =
				(List<ObjectFolder>)page1.getItems();

			Assert.assertEquals(
				objectFolders1.toString(), totalCount + 2,
				objectFolders1.size());

			Page<ObjectFolder> page2 =
				objectFolderResource.getObjectFoldersPage(
					null, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ObjectFolder> objectFolders2 =
				(List<ObjectFolder>)page2.getItems();

			Assert.assertEquals(
				objectFolders2.toString(), 1, objectFolders2.size());

			Page<ObjectFolder> page3 =
				objectFolderResource.getObjectFoldersPage(
					null, Pagination.of(1, (int)totalCount + 3));

			assertContains(objectFolder1, (List<ObjectFolder>)page3.getItems());
			assertContains(objectFolder2, (List<ObjectFolder>)page3.getItems());
			assertContains(objectFolder3, (List<ObjectFolder>)page3.getItems());
		}
	}

	protected ObjectFolder testGetObjectFoldersPage_addObjectFolder(
			ObjectFolder objectFolder)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetObjectFoldersPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"objectFolders",
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

		JSONObject objectFoldersJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/objectFolders");

		long totalCount = objectFoldersJSONObject.getLong("totalCount");

		ObjectFolder objectFolder1 = testGraphQLObjectFolder_addObjectFolder(
			randomObjectFolder());

		ObjectFolder objectFolder2 = testGraphQLObjectFolder_addObjectFolder(
			randomObjectFolder());

		objectFoldersJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/objectFolders");

		Assert.assertEquals(
			totalCount + 2, objectFoldersJSONObject.getLong("totalCount"));

		assertContains(
			objectFolder1,
			Arrays.asList(
				ObjectFolderSerDes.toDTOs(
					objectFoldersJSONObject.getString("items"))));
		assertContains(
			objectFolder2,
			Arrays.asList(
				ObjectFolderSerDes.toDTOs(
					objectFoldersJSONObject.getString("items"))));

		// Using the namespace objectAdmin_v1_0

		objectFoldersJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("objectAdmin_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/objectAdmin_v1_0",
			"JSONObject/objectFolders");

		Assert.assertEquals(
			totalCount + 2, objectFoldersJSONObject.getLong("totalCount"));

		assertContains(
			objectFolder1,
			Arrays.asList(
				ObjectFolderSerDes.toDTOs(
					objectFoldersJSONObject.getString("items"))));
		assertContains(
			objectFolder2,
			Arrays.asList(
				ObjectFolderSerDes.toDTOs(
					objectFoldersJSONObject.getString("items"))));
	}

	@Test
	public void testPatchObjectFolder() throws Exception {
		ObjectFolder postObjectFolder = testPatchObjectFolder_addObjectFolder();

		ObjectFolder randomPatchObjectFolder = randomPatchObjectFolder();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ObjectFolder patchObjectFolder = objectFolderResource.patchObjectFolder(
			postObjectFolder.getId(), randomPatchObjectFolder);

		ObjectFolder expectedPatchObjectFolder = postObjectFolder.clone();

		BeanTestUtil.copyProperties(
			randomPatchObjectFolder, expectedPatchObjectFolder);

		ObjectFolder getObjectFolder = objectFolderResource.getObjectFolder(
			patchObjectFolder.getId());

		assertEquals(expectedPatchObjectFolder, getObjectFolder);
		assertValid(getObjectFolder);
	}

	protected ObjectFolder testPatchObjectFolder_addObjectFolder()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostObjectFolder() throws Exception {
		ObjectFolder randomObjectFolder = randomObjectFolder();

		ObjectFolder postObjectFolder = testPostObjectFolder_addObjectFolder(
			randomObjectFolder);

		assertEquals(randomObjectFolder, postObjectFolder);
		assertValid(postObjectFolder);
	}

	protected ObjectFolder testPostObjectFolder_addObjectFolder(
			ObjectFolder objectFolder)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostObjectFolder() throws Exception {
		ObjectFolder randomObjectFolder = randomObjectFolder();

		ObjectFolder objectFolder = testGraphQLObjectFolder_addObjectFolder(
			randomObjectFolder);

		Assert.assertTrue(equals(randomObjectFolder, objectFolder));
	}

	@Test
	public void testPutObjectFolder() throws Exception {
		ObjectFolder postObjectFolder = testPutObjectFolder_addObjectFolder();

		ObjectFolder randomObjectFolder = randomObjectFolder();

		ObjectFolder putObjectFolder = objectFolderResource.putObjectFolder(
			postObjectFolder.getId(), randomObjectFolder);

		assertEquals(randomObjectFolder, putObjectFolder);
		assertValid(putObjectFolder);

		ObjectFolder getObjectFolder = objectFolderResource.getObjectFolder(
			putObjectFolder.getId());

		assertEquals(randomObjectFolder, getObjectFolder);
		assertValid(getObjectFolder);
	}

	protected ObjectFolder testPutObjectFolder_addObjectFolder()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutObjectFolderByExternalReferenceCode() throws Exception {
		ObjectFolder postObjectFolder =
			testPutObjectFolderByExternalReferenceCode_addObjectFolder();

		ObjectFolder randomObjectFolder = randomObjectFolder();

		ObjectFolder putObjectFolder =
			objectFolderResource.putObjectFolderByExternalReferenceCode(
				postObjectFolder.getExternalReferenceCode(),
				randomObjectFolder);

		assertEquals(randomObjectFolder, putObjectFolder);
		assertValid(putObjectFolder);

		ObjectFolder getObjectFolder =
			objectFolderResource.getObjectFolderByExternalReferenceCode(
				putObjectFolder.getExternalReferenceCode());

		assertEquals(randomObjectFolder, getObjectFolder);
		assertValid(getObjectFolder);

		ObjectFolder newObjectFolder =
			testPutObjectFolderByExternalReferenceCode_createObjectFolder();

		putObjectFolder =
			objectFolderResource.putObjectFolderByExternalReferenceCode(
				newObjectFolder.getExternalReferenceCode(), newObjectFolder);

		assertEquals(newObjectFolder, putObjectFolder);
		assertValid(putObjectFolder);

		getObjectFolder =
			objectFolderResource.getObjectFolderByExternalReferenceCode(
				putObjectFolder.getExternalReferenceCode());

		assertEquals(newObjectFolder, getObjectFolder);

		Assert.assertEquals(
			newObjectFolder.getExternalReferenceCode(),
			putObjectFolder.getExternalReferenceCode());
	}

	protected ObjectFolder
			testPutObjectFolderByExternalReferenceCode_addObjectFolder()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ObjectFolder
			testPutObjectFolderByExternalReferenceCode_createObjectFolder()
		throws Exception {

		return randomObjectFolder();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ObjectFolder objectFolder1 =
			testBatchEngineDeleteImportTask_addObjectFolder();

		testBatchEngineDeleteImportTask_deleteObjectFolder(
			200, null, objectFolder1.getId());

		assertHttpResponseStatusCode(
			404,
			objectFolderResource.getObjectFolderHttpResponse(
				objectFolder1.getId()));
	}

	protected ObjectFolder testBatchEngineDeleteImportTask_addObjectFolder()
		throws Exception {

		return testDeleteObjectFolder_addObjectFolder();
	}

	protected void testBatchEngineDeleteImportTask_deleteObjectFolder(
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
				"com.liferay.object.admin.rest.dto.v1_0.ObjectFolder", null,
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

	protected ObjectFolder testGraphQLObjectFolder_addObjectFolder()
		throws Exception {

		return testGraphQLObjectFolder_addObjectFolder(randomObjectFolder());
	}

	protected ObjectFolder testGraphQLObjectFolder_addObjectFolder(
			ObjectFolder objectFolder)
		throws Exception {

		JSONDeserializer<ObjectFolder> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(ObjectFolder.class)) {

			if (getGraphQLValue(field.get(objectFolder)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(objectFolder)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createObjectFolder",
						new HashMap<String, Object>() {
							{
								put("objectFolder", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createObjectFolder"),
			ObjectFolder.class);
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
		ObjectFolder objectFolder, List<ObjectFolder> objectFolders) {

		boolean contains = false;

		for (ObjectFolder item : objectFolders) {
			if (equals(objectFolder, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			objectFolders + " does not contain " + objectFolder, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ObjectFolder objectFolder1, ObjectFolder objectFolder2) {

		Assert.assertTrue(
			objectFolder1 + " does not equal " + objectFolder2,
			equals(objectFolder1, objectFolder2));
	}

	protected void assertEquals(
		List<ObjectFolder> objectFolders1, List<ObjectFolder> objectFolders2) {

		Assert.assertEquals(objectFolders1.size(), objectFolders2.size());

		for (int i = 0; i < objectFolders1.size(); i++) {
			ObjectFolder objectFolder1 = objectFolders1.get(i);
			ObjectFolder objectFolder2 = objectFolders2.get(i);

			assertEquals(objectFolder1, objectFolder2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ObjectFolder> objectFolders1, List<ObjectFolder> objectFolders2) {

		Assert.assertEquals(objectFolders1.size(), objectFolders2.size());

		for (ObjectFolder objectFolder1 : objectFolders1) {
			boolean contains = false;

			for (ObjectFolder objectFolder2 : objectFolders2) {
				if (equals(objectFolder1, objectFolder2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				objectFolders2 + " does not contain " + objectFolder1,
				contains);
		}
	}

	protected void assertValid(ObjectFolder objectFolder) throws Exception {
		boolean valid = true;

		if (objectFolder.getDateCreated() == null) {
			valid = false;
		}

		if (objectFolder.getDateModified() == null) {
			valid = false;
		}

		if (objectFolder.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (objectFolder.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (objectFolder.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("label", additionalAssertFieldName)) {
				if (objectFolder.getLabel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (objectFolder.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectFolderItems", additionalAssertFieldName)) {

				if (objectFolder.getObjectFolderItems() == null) {
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

	protected void assertValid(Page<ObjectFolder> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ObjectFolder> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ObjectFolder> objectFolders = page.getItems();

		int size = objectFolders.size();

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
					com.liferay.object.admin.rest.dto.v1_0.ObjectFolder.
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
		ObjectFolder objectFolder1, ObjectFolder objectFolder2) {

		if (objectFolder1 == objectFolder2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)objectFolder1.getActions(),
						(Map)objectFolder2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectFolder1.getDateCreated(),
						objectFolder2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectFolder1.getDateModified(),
						objectFolder2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectFolder1.getExternalReferenceCode(),
						objectFolder2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectFolder1.getId(), objectFolder2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("label", additionalAssertFieldName)) {
				if (!equals(
						(Map)objectFolder1.getLabel(),
						(Map)objectFolder2.getLabel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectFolder1.getName(), objectFolder2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectFolderItems", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectFolder1.getObjectFolderItems(),
						objectFolder2.getObjectFolderItems())) {

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

		if (!(_objectFolderResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_objectFolderResource;

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
		EntityField entityField, String operator, ObjectFolder objectFolder) {

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

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = objectFolder.getDateCreated();

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

				sb.append(_format.format(objectFolder.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = objectFolder.getDateModified();

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

				sb.append(_format.format(objectFolder.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = objectFolder.getExternalReferenceCode();

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
			Object object = objectFolder.getName();

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

		if (entityFieldName.equals("objectFolderItems")) {
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

	protected ObjectFolder randomObjectFolder() throws Exception {
		return new ObjectFolder() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected ObjectFolder randomIrrelevantObjectFolder() throws Exception {
		ObjectFolder randomIrrelevantObjectFolder = randomObjectFolder();

		return randomIrrelevantObjectFolder;
	}

	protected ObjectFolder randomPatchObjectFolder() throws Exception {
		return randomObjectFolder();
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

	protected ObjectFolderResource objectFolderResource;
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
		LogFactoryUtil.getLog(BaseObjectFolderResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.object.admin.rest.resource.v1_0.ObjectFolderResource
		_objectFolderResource;

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