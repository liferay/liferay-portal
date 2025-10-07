/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

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
import com.liferay.headless.delivery.client.dto.v1_0.Field;
import com.liferay.headless.delivery.client.dto.v1_0.NavigationMenu;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.permission.Permission;
import com.liferay.headless.delivery.client.resource.v1_0.NavigationMenuResource;
import com.liferay.headless.delivery.client.serdes.v1_0.NavigationMenuSerDes;
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
public abstract class BaseNavigationMenuResourceTestCase {

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

		_navigationMenuResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		navigationMenuResource = NavigationMenuResource.builder(
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

		NavigationMenu navigationMenu1 = randomNavigationMenu();

		String json = objectMapper.writeValueAsString(navigationMenu1);

		NavigationMenu navigationMenu2 = NavigationMenuSerDes.toDTO(json);

		Assert.assertTrue(equals(navigationMenu1, navigationMenu2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		NavigationMenu navigationMenu = randomNavigationMenu();

		String json1 = objectMapper.writeValueAsString(navigationMenu);
		String json2 = NavigationMenuSerDes.toJSON(navigationMenu);

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

		NavigationMenu navigationMenu = randomNavigationMenu();

		navigationMenu.setExternalReferenceCode(regex);
		navigationMenu.setName(regex);

		String json = NavigationMenuSerDes.toJSON(navigationMenu);

		Assert.assertFalse(json.contains(regex));

		navigationMenu = NavigationMenuSerDes.toDTO(json);

		Assert.assertEquals(regex, navigationMenu.getExternalReferenceCode());
		Assert.assertEquals(regex, navigationMenu.getName());
	}

	@Test
	public void testDeleteNavigationMenu() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		NavigationMenu navigationMenu =
			testDeleteNavigationMenu_addNavigationMenu();

		assertHttpResponseStatusCode(
			204,
			navigationMenuResource.deleteNavigationMenuHttpResponse(
				navigationMenu.getId()));

		assertHttpResponseStatusCode(
			404,
			navigationMenuResource.getNavigationMenuHttpResponse(
				navigationMenu.getId()));
		assertHttpResponseStatusCode(
			404, navigationMenuResource.getNavigationMenuHttpResponse(0L));
	}

	protected NavigationMenu testDeleteNavigationMenu_addNavigationMenu()
		throws Exception {

		return navigationMenuResource.postSiteNavigationMenu(
			testGroup.getGroupId(), randomNavigationMenu());
	}

	@Test
	public void testGraphQLDeleteNavigationMenu() throws Exception {

		// No namespace

		NavigationMenu navigationMenu1 =
			testGraphQLDeleteNavigationMenu_addNavigationMenu();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteNavigationMenu",
						new HashMap<String, Object>() {
							{
								put(
									"navigationMenuId",
									navigationMenu1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteNavigationMenu"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"navigationMenu",
					new HashMap<String, Object>() {
						{
							put("navigationMenuId", navigationMenu1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		NavigationMenu navigationMenu2 =
			testGraphQLDeleteNavigationMenu_addNavigationMenu();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteNavigationMenu",
							new HashMap<String, Object>() {
								{
									put(
										"navigationMenuId",
										navigationMenu2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteNavigationMenu"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"navigationMenu",
						new HashMap<String, Object>() {
							{
								put(
									"navigationMenuId",
									navigationMenu2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected NavigationMenu testGraphQLDeleteNavigationMenu_addNavigationMenu()
		throws Exception {

		return testGraphQLNavigationMenu_addNavigationMenu();
	}

	@Test
	public void testDeleteNavigationMenuBatch() throws Exception {
		NavigationMenu navigationMenu1 =
			testDeleteNavigationMenuBatch_addNavigationMenu();

		testDeleteNavigationMenuBatch_deleteNavigationMenu(
			202, null, navigationMenu1.getId());

		assertHttpResponseStatusCode(
			404,
			navigationMenuResource.getNavigationMenuHttpResponse(
				navigationMenu1.getId()));
	}

	protected NavigationMenu testDeleteNavigationMenuBatch_addNavigationMenu()
		throws Exception {

		return testDeleteNavigationMenu_addNavigationMenu();
	}

	protected void testDeleteNavigationMenuBatch_deleteNavigationMenu(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			navigationMenuResource.deleteNavigationMenuBatchHttpResponse(
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
	public void testDeleteSiteNavigationMenuByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		NavigationMenu navigationMenu =
			testDeleteSiteNavigationMenuByExternalReferenceCode_addNavigationMenu();

		assertHttpResponseStatusCode(
			204,
			navigationMenuResource.
				deleteSiteNavigationMenuByExternalReferenceCodeHttpResponse(
					navigationMenu.getSiteId(),
					navigationMenu.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			navigationMenuResource.
				getSiteNavigationMenuByExternalReferenceCodeHttpResponse(
					navigationMenu.getSiteId(),
					navigationMenu.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			navigationMenuResource.
				getSiteNavigationMenuByExternalReferenceCodeHttpResponse(
					navigationMenu.getSiteId(), "-"));
	}

	protected NavigationMenu
			testDeleteSiteNavigationMenuByExternalReferenceCode_addNavigationMenu()
		throws Exception {

		return navigationMenuResource.postSiteNavigationMenu(
			testGroup.getGroupId(), randomNavigationMenu());
	}

	@Test
	public void testGraphQLDeleteSiteNavigationMenuByExternalReferenceCode()
		throws Exception {

		// No namespace

		NavigationMenu navigationMenu1 =
			testGraphQLDeleteSiteNavigationMenuByExternalReferenceCode_addNavigationMenu();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSiteNavigationMenuByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + navigationMenu1.getSiteId() + "\"");
								put(
									"externalReferenceCode",
									"\"" +
										navigationMenu1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteSiteNavigationMenuByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"navigationMenuByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"siteKey",
								"\"" + navigationMenu1.getSiteId() + "\"");
							put(
								"externalReferenceCode",
								"\"" +
									navigationMenu1.getExternalReferenceCode() +
										"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		NavigationMenu navigationMenu2 =
			testGraphQLDeleteSiteNavigationMenuByExternalReferenceCode_addNavigationMenu();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteSiteNavigationMenuByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + navigationMenu2.getSiteId() +
											"\"");
									put(
										"externalReferenceCode",
										"\"" +
											navigationMenu2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteSiteNavigationMenuByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"navigationMenuByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + navigationMenu2.getSiteId() + "\"");
								put(
									"externalReferenceCode",
									"\"" +
										navigationMenu2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected NavigationMenu
			testGraphQLDeleteSiteNavigationMenuByExternalReferenceCode_addNavigationMenu()
		throws Exception {

		return testGraphQLSiteNavigationMenu_addNavigationMenu();
	}

	@Test
	public void testGetNavigationMenu() throws Exception {
		NavigationMenu postNavigationMenu =
			testGetNavigationMenu_addNavigationMenu();

		NavigationMenu getNavigationMenu =
			navigationMenuResource.getNavigationMenu(
				postNavigationMenu.getId());

		assertEquals(postNavigationMenu, getNavigationMenu);
		assertValid(getNavigationMenu);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		NavigationMenu postNavigationMenu =
			testGetNavigationMenu_addNavigationMenu();

		NavigationMenu getNavigationMenu =
			navigationMenuResource.getNavigationMenu(
				postNavigationMenu.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.delivery.dto.v1_0.NavigationMenu"
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
			postNavigationMenu.getId());

		assertEquals(
			getNavigationMenu, NavigationMenuSerDes.toDTO(item.toString()));
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

	protected NavigationMenu testGetNavigationMenu_addNavigationMenu()
		throws Exception {

		return navigationMenuResource.postSiteNavigationMenu(
			testGroup.getGroupId(), randomNavigationMenu());
	}

	@Test
	public void testGraphQLGetNavigationMenu() throws Exception {
		NavigationMenu navigationMenu =
			testGraphQLGetNavigationMenu_addNavigationMenu();

		// No namespace

		Assert.assertTrue(
			equals(
				navigationMenu,
				NavigationMenuSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"navigationMenu",
								new HashMap<String, Object>() {
									{
										put(
											"navigationMenuId",
											navigationMenu.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/navigationMenu"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				navigationMenu,
				NavigationMenuSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"navigationMenu",
									new HashMap<String, Object>() {
										{
											put(
												"navigationMenuId",
												navigationMenu.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/navigationMenu"))));
	}

	@Test
	public void testGraphQLGetNavigationMenuNotFound() throws Exception {
		Long irrelevantNavigationMenuId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"navigationMenu",
						new HashMap<String, Object>() {
							{
								put(
									"navigationMenuId",
									irrelevantNavigationMenuId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"navigationMenu",
							new HashMap<String, Object>() {
								{
									put(
										"navigationMenuId",
										irrelevantNavigationMenuId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected NavigationMenu testGraphQLGetNavigationMenu_addNavigationMenu()
		throws Exception {

		return testGraphQLNavigationMenu_addNavigationMenu();
	}

	@Test
	public void testGetNavigationMenuPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		NavigationMenu postNavigationMenu =
			testGetNavigationMenuPermissionsPage_addNavigationMenu();

		Page<Permission> page =
			navigationMenuResource.getNavigationMenuPermissionsPage(
				postNavigationMenu.getId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected NavigationMenu
			testGetNavigationMenuPermissionsPage_addNavigationMenu()
		throws Exception {

		return navigationMenuResource.postSiteNavigationMenu(
			testGroup.getGroupId(), randomNavigationMenu());
	}

	@Test
	public void testGraphQLGetNavigationMenuPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		NavigationMenu postNavigationMenu =
			testGraphQLGetNavigationMenuPermissionsPage_addNavigationMenu();

		GraphQLField graphQLField = new GraphQLField(
			"navigationMenuPermissions",
			new HashMap<String, Object>() {
				{
					put("navigationMenuId", postNavigationMenu.getId());
				}
			},
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject navigationMenuPermissionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/navigationMenuPermissions");

		Assert.assertNotNull(navigationMenuPermissionsJSONObject);
	}

	protected NavigationMenu
			testGraphQLGetNavigationMenuPermissionsPage_addNavigationMenu()
		throws Exception {

		return testGraphQLNavigationMenu_addNavigationMenu();
	}

	@Test
	public void testGetSiteNavigationMenuByExternalReferenceCode()
		throws Exception {

		NavigationMenu postNavigationMenu =
			testGetSiteNavigationMenuByExternalReferenceCode_addNavigationMenu();

		NavigationMenu getNavigationMenu =
			navigationMenuResource.getSiteNavigationMenuByExternalReferenceCode(
				postNavigationMenu.getSiteId(),
				postNavigationMenu.getExternalReferenceCode());

		assertEquals(postNavigationMenu, getNavigationMenu);
		assertValid(getNavigationMenu);
	}

	protected NavigationMenu
			testGetSiteNavigationMenuByExternalReferenceCode_addNavigationMenu()
		throws Exception {

		return navigationMenuResource.postSiteNavigationMenu(
			testGroup.getGroupId(), randomNavigationMenu());
	}

	@Test
	public void testGraphQLGetSiteNavigationMenuByExternalReferenceCode()
		throws Exception {

		NavigationMenu navigationMenu =
			testGraphQLGetSiteNavigationMenuByExternalReferenceCode_addNavigationMenu();

		// No namespace

		Assert.assertTrue(
			equals(
				navigationMenu,
				NavigationMenuSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"navigationMenuByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" + navigationMenu.getSiteId() +
												"\"");
										put(
											"externalReferenceCode",
											"\"" +
												navigationMenu.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/navigationMenuByExternalReferenceCode"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				navigationMenu,
				NavigationMenuSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"navigationMenuByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													navigationMenu.getSiteId() +
														"\"");
											put(
												"externalReferenceCode",
												"\"" +
													navigationMenu.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/navigationMenuByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetSiteNavigationMenuByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"navigationMenuByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + irrelevantGroup.getGroupId() + "\"");
								put(
									"externalReferenceCode",
									irrelevantExternalReferenceCode);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"navigationMenuByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + irrelevantGroup.getGroupId() +
											"\"");
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected NavigationMenu
			testGraphQLGetSiteNavigationMenuByExternalReferenceCode_addNavigationMenu()
		throws Exception {

		return testGraphQLSiteNavigationMenu_addNavigationMenu();
	}

	@Test
	public void testGetSiteNavigationMenuPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		NavigationMenu postNavigationMenu =
			testGetSiteNavigationMenuPermissionsPage_addNavigationMenu();

		Page<Permission> page =
			navigationMenuResource.getSiteNavigationMenuPermissionsPage(
				testGroup.getGroupId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected NavigationMenu
			testGetSiteNavigationMenuPermissionsPage_addNavigationMenu()
		throws Exception {

		return navigationMenuResource.postSiteNavigationMenu(
			testGroup.getGroupId(), randomNavigationMenu());
	}

	@Test
	public void testGraphQLGetSiteNavigationMenuPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		NavigationMenu postNavigationMenu =
			testGraphQLGetSiteNavigationMenuPermissionsPage_addNavigationMenu();

		GraphQLField graphQLField = new GraphQLField(
			"siteNavigationMenuPermissions",
			new HashMap<String, Object>() {
				{
					put(
						"siteKey",
						"\"" + postNavigationMenu.getSiteId() + "\"");
				}
			},
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject siteNavigationMenuPermissionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/siteNavigationMenuPermissions");

		Assert.assertNotNull(siteNavigationMenuPermissionsJSONObject);
	}

	protected NavigationMenu
			testGraphQLGetSiteNavigationMenuPermissionsPage_addNavigationMenu()
		throws Exception {

		return testGraphQLSiteNavigationMenu_addNavigationMenu();
	}

	@Test
	public void testGetSiteNavigationMenusPage() throws Exception {
		Long siteId = testGetSiteNavigationMenusPage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteNavigationMenusPage_getIrrelevantSiteId();

		Page<NavigationMenu> page =
			navigationMenuResource.getSiteNavigationMenusPage(
				siteId, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteId != null) {
			NavigationMenu irrelevantNavigationMenu =
				testGetSiteNavigationMenusPage_addNavigationMenu(
					irrelevantSiteId, randomIrrelevantNavigationMenu());

			page = navigationMenuResource.getSiteNavigationMenusPage(
				irrelevantSiteId, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantNavigationMenu,
				(List<NavigationMenu>)page.getItems());
			assertValid(
				page,
				testGetSiteNavigationMenusPage_getExpectedActions(
					irrelevantSiteId));
		}

		NavigationMenu navigationMenu1 =
			testGetSiteNavigationMenusPage_addNavigationMenu(
				siteId, randomNavigationMenu());

		NavigationMenu navigationMenu2 =
			testGetSiteNavigationMenusPage_addNavigationMenu(
				siteId, randomNavigationMenu());

		page = navigationMenuResource.getSiteNavigationMenusPage(
			siteId, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(navigationMenu1, (List<NavigationMenu>)page.getItems());
		assertContains(navigationMenu2, (List<NavigationMenu>)page.getItems());
		assertValid(
			page, testGetSiteNavigationMenusPage_getExpectedActions(siteId));

		navigationMenuResource.deleteNavigationMenu(navigationMenu1.getId());

		navigationMenuResource.deleteNavigationMenu(navigationMenu2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSiteNavigationMenusPage_getExpectedActions(Long siteId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/sites/{siteId}/navigation-menus/batch".
				replace("{siteId}", String.valueOf(siteId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetSiteNavigationMenusPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteNavigationMenusPage_getSiteId();

		NavigationMenu navigationMenu1 = randomNavigationMenu();

		navigationMenu1 = testGetSiteNavigationMenusPage_addNavigationMenu(
			siteId, navigationMenu1);

		for (EntityField entityField : entityFields) {
			Page<NavigationMenu> page =
				navigationMenuResource.getSiteNavigationMenusPage(
					siteId, null,
					getFilterString(entityField, "between", navigationMenu1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(navigationMenu1),
				(List<NavigationMenu>)page.getItems());
		}
	}

	@Test
	public void testGetSiteNavigationMenusPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteNavigationMenusPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteNavigationMenusPageWithFilterStringContains()
		throws Exception {

		testGetSiteNavigationMenusPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteNavigationMenusPageWithFilterStringEquals()
		throws Exception {

		testGetSiteNavigationMenusPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteNavigationMenusPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteNavigationMenusPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetSiteNavigationMenusPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteNavigationMenusPage_getSiteId();

		NavigationMenu navigationMenu1 =
			testGetSiteNavigationMenusPage_addNavigationMenu(
				siteId, randomNavigationMenu());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		NavigationMenu navigationMenu2 =
			testGetSiteNavigationMenusPage_addNavigationMenu(
				siteId, randomNavigationMenu());

		for (EntityField entityField : entityFields) {
			Page<NavigationMenu> page =
				navigationMenuResource.getSiteNavigationMenusPage(
					siteId, null,
					getFilterString(entityField, operator, navigationMenu1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(navigationMenu1),
				(List<NavigationMenu>)page.getItems());
		}
	}

	@Test
	public void testGetSiteNavigationMenusPageWithPagination()
		throws Exception {

		Long siteId = testGetSiteNavigationMenusPage_getSiteId();

		Page<NavigationMenu> navigationMenusPage =
			navigationMenuResource.getSiteNavigationMenusPage(
				siteId, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			navigationMenusPage.getTotalCount());

		NavigationMenu navigationMenu1 =
			testGetSiteNavigationMenusPage_addNavigationMenu(
				siteId, randomNavigationMenu());

		NavigationMenu navigationMenu2 =
			testGetSiteNavigationMenusPage_addNavigationMenu(
				siteId, randomNavigationMenu());

		NavigationMenu navigationMenu3 =
			testGetSiteNavigationMenusPage_addNavigationMenu(
				siteId, randomNavigationMenu());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<NavigationMenu> page1 =
				navigationMenuResource.getSiteNavigationMenusPage(
					siteId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				navigationMenu1, (List<NavigationMenu>)page1.getItems());

			Page<NavigationMenu> page2 =
				navigationMenuResource.getSiteNavigationMenusPage(
					siteId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				navigationMenu2, (List<NavigationMenu>)page2.getItems());

			Page<NavigationMenu> page3 =
				navigationMenuResource.getSiteNavigationMenusPage(
					siteId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				navigationMenu3, (List<NavigationMenu>)page3.getItems());
		}
		else {
			Page<NavigationMenu> page1 =
				navigationMenuResource.getSiteNavigationMenusPage(
					siteId, null, null, Pagination.of(1, totalCount + 2), null);

			List<NavigationMenu> navigationMenus1 =
				(List<NavigationMenu>)page1.getItems();

			Assert.assertEquals(
				navigationMenus1.toString(), totalCount + 2,
				navigationMenus1.size());

			Page<NavigationMenu> page2 =
				navigationMenuResource.getSiteNavigationMenusPage(
					siteId, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<NavigationMenu> navigationMenus2 =
				(List<NavigationMenu>)page2.getItems();

			Assert.assertEquals(
				navigationMenus2.toString(), 1, navigationMenus2.size());

			Page<NavigationMenu> page3 =
				navigationMenuResource.getSiteNavigationMenusPage(
					siteId, null, null, Pagination.of(1, (int)totalCount + 3),
					null);

			assertContains(
				navigationMenu1, (List<NavigationMenu>)page3.getItems());
			assertContains(
				navigationMenu2, (List<NavigationMenu>)page3.getItems());
			assertContains(
				navigationMenu3, (List<NavigationMenu>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteNavigationMenusPageWithSortDateTime()
		throws Exception {

		testGetSiteNavigationMenusPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, navigationMenu1, navigationMenu2) -> {
				BeanTestUtil.setProperty(
					navigationMenu1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteNavigationMenusPageWithSortDouble()
		throws Exception {

		testGetSiteNavigationMenusPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, navigationMenu1, navigationMenu2) -> {
				BeanTestUtil.setProperty(
					navigationMenu1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					navigationMenu2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteNavigationMenusPageWithSortInteger()
		throws Exception {

		testGetSiteNavigationMenusPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, navigationMenu1, navigationMenu2) -> {
				BeanTestUtil.setProperty(
					navigationMenu1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					navigationMenu2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteNavigationMenusPageWithSortString()
		throws Exception {

		testGetSiteNavigationMenusPageWithSort(
			EntityField.Type.STRING,
			(entityField, navigationMenu1, navigationMenu2) -> {
				Class<?> clazz = navigationMenu1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						navigationMenu1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						navigationMenu2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						navigationMenu1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						navigationMenu2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						navigationMenu1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						navigationMenu2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSiteNavigationMenusPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, NavigationMenu, NavigationMenu, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteNavigationMenusPage_getSiteId();

		NavigationMenu navigationMenu1 = randomNavigationMenu();
		NavigationMenu navigationMenu2 = randomNavigationMenu();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, navigationMenu1, navigationMenu2);
		}

		navigationMenu1 = testGetSiteNavigationMenusPage_addNavigationMenu(
			siteId, navigationMenu1);

		navigationMenu2 = testGetSiteNavigationMenusPage_addNavigationMenu(
			siteId, navigationMenu2);

		Page<NavigationMenu> page =
			navigationMenuResource.getSiteNavigationMenusPage(
				siteId, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<NavigationMenu> ascPage =
				navigationMenuResource.getSiteNavigationMenusPage(
					siteId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				navigationMenu1, (List<NavigationMenu>)ascPage.getItems());
			assertContains(
				navigationMenu2, (List<NavigationMenu>)ascPage.getItems());

			Page<NavigationMenu> descPage =
				navigationMenuResource.getSiteNavigationMenusPage(
					siteId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				navigationMenu2, (List<NavigationMenu>)descPage.getItems());
			assertContains(
				navigationMenu1, (List<NavigationMenu>)descPage.getItems());
		}
	}

	protected NavigationMenu testGetSiteNavigationMenusPage_addNavigationMenu(
			Long siteId, NavigationMenu navigationMenu)
		throws Exception {

		return navigationMenuResource.postSiteNavigationMenu(
			siteId, navigationMenu);
	}

	protected Long testGetSiteNavigationMenusPage_getSiteId() throws Exception {
		return testGroup.getGroupId();
	}

	protected Long testGetSiteNavigationMenusPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	@Test
	public void testGraphQLGetSiteNavigationMenusPage() throws Exception {
		Long siteId = testGetSiteNavigationMenusPage_getSiteId();

		GraphQLField graphQLField = new GraphQLField(
			"navigationMenus",
			new HashMap<String, Object>() {
				{
					put("siteKey", "\"" + siteId + "\"");
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject navigationMenusJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/navigationMenus");

		long totalCount = navigationMenusJSONObject.getLong("totalCount");

		NavigationMenu navigationMenu1 =
			testGraphQLSiteNavigationMenu_addNavigationMenu(
				siteId, randomNavigationMenu());

		NavigationMenu navigationMenu2 =
			testGraphQLSiteNavigationMenu_addNavigationMenu(
				siteId, randomNavigationMenu());

		navigationMenusJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/navigationMenus");

		Assert.assertEquals(
			totalCount + 2, navigationMenusJSONObject.getLong("totalCount"));

		assertContains(
			navigationMenu1,
			Arrays.asList(
				NavigationMenuSerDes.toDTOs(
					navigationMenusJSONObject.getString("items"))));
		assertContains(
			navigationMenu2,
			Arrays.asList(
				NavigationMenuSerDes.toDTOs(
					navigationMenusJSONObject.getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		navigationMenusJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessDelivery_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
			"JSONObject/navigationMenus");

		Assert.assertEquals(
			totalCount + 2, navigationMenusJSONObject.getLong("totalCount"));

		assertContains(
			navigationMenu1,
			Arrays.asList(
				NavigationMenuSerDes.toDTOs(
					navigationMenusJSONObject.getString("items"))));
		assertContains(
			navigationMenu2,
			Arrays.asList(
				NavigationMenuSerDes.toDTOs(
					navigationMenusJSONObject.getString("items"))));
	}

	@Test
	public void testPostSiteNavigationMenu() throws Exception {
		NavigationMenu randomNavigationMenu = randomNavigationMenu();

		NavigationMenu postNavigationMenu =
			testPostSiteNavigationMenu_addNavigationMenu(randomNavigationMenu);

		assertEquals(randomNavigationMenu, postNavigationMenu);
		assertValid(postNavigationMenu);
	}

	protected NavigationMenu testPostSiteNavigationMenu_addNavigationMenu(
			NavigationMenu navigationMenu)
		throws Exception {

		return navigationMenuResource.postSiteNavigationMenu(
			testGetSiteNavigationMenusPage_getSiteId(), navigationMenu);
	}

	@Test
	public void testGraphQLPostSiteNavigationMenu() throws Exception {
		NavigationMenu randomNavigationMenu = randomNavigationMenu();

		NavigationMenu navigationMenu =
			testGraphQLSiteNavigationMenu_addNavigationMenu(
				testGroup.getGroupId(), randomNavigationMenu);

		Assert.assertTrue(equals(randomNavigationMenu, navigationMenu));
	}

	@Test
	public void testPutNavigationMenu() throws Exception {
		NavigationMenu postNavigationMenu =
			testPutNavigationMenu_addNavigationMenu();

		NavigationMenu randomNavigationMenu = randomNavigationMenu();

		NavigationMenu putNavigationMenu =
			navigationMenuResource.putNavigationMenu(
				postNavigationMenu.getId(), randomNavigationMenu);

		assertEquals(randomNavigationMenu, putNavigationMenu);
		assertValid(putNavigationMenu);

		NavigationMenu getNavigationMenu =
			navigationMenuResource.getNavigationMenu(putNavigationMenu.getId());

		assertEquals(randomNavigationMenu, getNavigationMenu);
		assertValid(getNavigationMenu);
	}

	protected NavigationMenu testPutNavigationMenu_addNavigationMenu()
		throws Exception {

		return navigationMenuResource.postSiteNavigationMenu(
			testGroup.getGroupId(), randomNavigationMenu());
	}

	@Test
	public void testPutNavigationMenuPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		NavigationMenu navigationMenu =
			testPutNavigationMenuPermissionsPage_addNavigationMenu();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			navigationMenuResource.putNavigationMenuPermissionsPageHttpResponse(
				navigationMenu.getId(),
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
			navigationMenuResource.putNavigationMenuPermissionsPageHttpResponse(
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

	protected NavigationMenu
			testPutNavigationMenuPermissionsPage_addNavigationMenu()
		throws Exception {

		return navigationMenuResource.postSiteNavigationMenu(
			testGroup.getGroupId(), randomNavigationMenu());
	}

	@Test
	public void testPutSiteNavigationMenuByExternalReferenceCode()
		throws Exception {

		NavigationMenu postNavigationMenu =
			testPutSiteNavigationMenuByExternalReferenceCode_addNavigationMenu();

		NavigationMenu randomNavigationMenu = randomNavigationMenu();

		NavigationMenu putNavigationMenu =
			navigationMenuResource.putSiteNavigationMenuByExternalReferenceCode(
				postNavigationMenu.getSiteId(),
				postNavigationMenu.getExternalReferenceCode(),
				randomNavigationMenu);

		assertEquals(randomNavigationMenu, putNavigationMenu);
		assertValid(putNavigationMenu);

		NavigationMenu getNavigationMenu =
			navigationMenuResource.getSiteNavigationMenuByExternalReferenceCode(
				putNavigationMenu.getSiteId(),
				putNavigationMenu.getExternalReferenceCode());

		assertEquals(randomNavigationMenu, getNavigationMenu);
		assertValid(getNavigationMenu);

		NavigationMenu newNavigationMenu =
			testPutSiteNavigationMenuByExternalReferenceCode_createNavigationMenu();

		putNavigationMenu =
			navigationMenuResource.putSiteNavigationMenuByExternalReferenceCode(
				newNavigationMenu.getSiteId(),
				newNavigationMenu.getExternalReferenceCode(),
				newNavigationMenu);

		assertEquals(newNavigationMenu, putNavigationMenu);
		assertValid(putNavigationMenu);

		getNavigationMenu =
			navigationMenuResource.getSiteNavigationMenuByExternalReferenceCode(
				putNavigationMenu.getSiteId(),
				putNavigationMenu.getExternalReferenceCode());

		assertEquals(newNavigationMenu, getNavigationMenu);

		Assert.assertEquals(
			newNavigationMenu.getExternalReferenceCode(),
			putNavigationMenu.getExternalReferenceCode());
	}

	protected NavigationMenu
			testPutSiteNavigationMenuByExternalReferenceCode_addNavigationMenu()
		throws Exception {

		return navigationMenuResource.postSiteNavigationMenu(
			testGroup.getGroupId(), randomNavigationMenu());
	}

	protected NavigationMenu
			testPutSiteNavigationMenuByExternalReferenceCode_createNavigationMenu()
		throws Exception {

		return randomNavigationMenu();
	}

	@Test
	public void testPutSiteNavigationMenuPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		NavigationMenu navigationMenu =
			testPutSiteNavigationMenuPermissionsPage_addNavigationMenu();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			navigationMenuResource.
				putSiteNavigationMenuPermissionsPageHttpResponse(
					testGroup.getGroupId(),
					new Permission[] {
						new Permission() {
							{
								setActionIds(new String[] {"PERMISSIONS"});
								setRoleName(role.getName());
							}
						}
					}));

		assertHttpResponseStatusCode(
			404,
			navigationMenuResource.
				putSiteNavigationMenuPermissionsPageHttpResponse(
					testGroup.getGroupId(),
					new Permission[] {
						new Permission() {
							{
								setActionIds(new String[] {"-"});
								setRoleName("-");
							}
						}
					}));
	}

	protected NavigationMenu
			testPutSiteNavigationMenuPermissionsPage_addNavigationMenu()
		throws Exception {

		return navigationMenuResource.postSiteNavigationMenu(
			testGroup.getGroupId(), randomNavigationMenu());
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		NavigationMenu navigationMenu1 =
			testBatchEngineDeleteImportTask_addNavigationMenu();

		testBatchEngineDeleteImportTask_deleteNavigationMenu(
			200, null, navigationMenu1.getId());

		assertHttpResponseStatusCode(
			404,
			navigationMenuResource.getNavigationMenuHttpResponse(
				navigationMenu1.getId()));
	}

	protected NavigationMenu testBatchEngineDeleteImportTask_addNavigationMenu()
		throws Exception {

		return testDeleteNavigationMenu_addNavigationMenu();
	}

	protected void testBatchEngineDeleteImportTask_deleteNavigationMenu(
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
				"com.liferay.headless.delivery.dto.v1_0.NavigationMenu", null,
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

	protected NavigationMenu testGraphQLNavigationMenu_addNavigationMenu()
		throws Exception {

		return testGraphQLNavigationMenu_addNavigationMenu(
			testGroup.getGroupId(), randomNavigationMenu());
	}

	protected NavigationMenu testGraphQLNavigationMenu_addNavigationMenu(
			Long siteId, NavigationMenu navigationMenu)
		throws Exception {

		JSONDeserializer<NavigationMenu> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(NavigationMenu.class)) {

			if (getGraphQLValue(field.get(navigationMenu)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(navigationMenu)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteNavigationMenu",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("navigationMenu", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteNavigationMenu"),
			NavigationMenu.class);
	}

	protected NavigationMenu testGraphQLSiteNavigationMenu_addNavigationMenu()
		throws Exception {

		return testGraphQLSiteNavigationMenu_addNavigationMenu(
			testGroup.getGroupId(), randomNavigationMenu());
	}

	protected NavigationMenu testGraphQLSiteNavigationMenu_addNavigationMenu(
			Long siteId, NavigationMenu navigationMenu)
		throws Exception {

		JSONDeserializer<NavigationMenu> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(NavigationMenu.class)) {

			if (getGraphQLValue(field.get(navigationMenu)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(navigationMenu)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteNavigationMenu",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("navigationMenu", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteNavigationMenu"),
			NavigationMenu.class);
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
		NavigationMenu navigationMenu, List<NavigationMenu> navigationMenus) {

		boolean contains = false;

		for (NavigationMenu item : navigationMenus) {
			if (equals(navigationMenu, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			navigationMenus + " does not contain " + navigationMenu, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		NavigationMenu navigationMenu1, NavigationMenu navigationMenu2) {

		Assert.assertTrue(
			navigationMenu1 + " does not equal " + navigationMenu2,
			equals(navigationMenu1, navigationMenu2));
	}

	protected void assertEquals(
		List<NavigationMenu> navigationMenus1,
		List<NavigationMenu> navigationMenus2) {

		Assert.assertEquals(navigationMenus1.size(), navigationMenus2.size());

		for (int i = 0; i < navigationMenus1.size(); i++) {
			NavigationMenu navigationMenu1 = navigationMenus1.get(i);
			NavigationMenu navigationMenu2 = navigationMenus2.get(i);

			assertEquals(navigationMenu1, navigationMenu2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<NavigationMenu> navigationMenus1,
		List<NavigationMenu> navigationMenus2) {

		Assert.assertEquals(navigationMenus1.size(), navigationMenus2.size());

		for (NavigationMenu navigationMenu1 : navigationMenus1) {
			boolean contains = false;

			for (NavigationMenu navigationMenu2 : navigationMenus2) {
				if (equals(navigationMenu1, navigationMenu2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				navigationMenus2 + " does not contain " + navigationMenu1,
				contains);
		}
	}

	protected void assertValid(NavigationMenu navigationMenu) throws Exception {
		boolean valid = true;

		if (navigationMenu.getDateCreated() == null) {
			valid = false;
		}

		if (navigationMenu.getDateModified() == null) {
			valid = false;
		}

		if (navigationMenu.getId() == null) {
			valid = false;
		}

		if (!Objects.equals(
				navigationMenu.getSiteId(), testGroup.getGroupId())) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (navigationMenu.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (navigationMenu.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (navigationMenu.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (navigationMenu.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"navigationMenuItems", additionalAssertFieldName)) {

				if (navigationMenu.getNavigationMenuItems() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("navigationType", additionalAssertFieldName)) {
				if (navigationMenu.getNavigationType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (navigationMenu.getPermissions() == null) {
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

	protected void assertValid(Page<NavigationMenu> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<NavigationMenu> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<NavigationMenu> navigationMenus = page.getItems();

		int size = navigationMenus.size();

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

		graphQLFields.add(new GraphQLField("siteId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.delivery.dto.v1_0.NavigationMenu.
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
		NavigationMenu navigationMenu1, NavigationMenu navigationMenu2) {

		if (navigationMenu1 == navigationMenu2) {
			return true;
		}

		if (!Objects.equals(
				navigationMenu1.getSiteId(), navigationMenu2.getSiteId())) {

			return false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)navigationMenu1.getActions(),
						(Map)navigationMenu2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						navigationMenu1.getCreator(),
						navigationMenu2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						navigationMenu1.getDateCreated(),
						navigationMenu2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						navigationMenu1.getDateModified(),
						navigationMenu2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						navigationMenu1.getExternalReferenceCode(),
						navigationMenu2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						navigationMenu1.getId(), navigationMenu2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						navigationMenu1.getName(), navigationMenu2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"navigationMenuItems", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						navigationMenu1.getNavigationMenuItems(),
						navigationMenu2.getNavigationMenuItems())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("navigationType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						navigationMenu1.getNavigationType(),
						navigationMenu2.getNavigationType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						navigationMenu1.getPermissions(),
						navigationMenu2.getPermissions())) {

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

		if (!(_navigationMenuResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_navigationMenuResource;

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
		NavigationMenu navigationMenu) {

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
				Date date = navigationMenu.getDateCreated();

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

				sb.append(_format.format(navigationMenu.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = navigationMenu.getDateModified();

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

				sb.append(_format.format(navigationMenu.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = navigationMenu.getExternalReferenceCode();

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
			Object object = navigationMenu.getName();

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

		if (entityFieldName.equals("navigationMenuItems")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("navigationType")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("permissions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("siteId")) {
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

	protected NavigationMenu randomNavigationMenu() throws Exception {
		return new NavigationMenu() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				siteId = testGroup.getGroupId();
			}
		};
	}

	protected NavigationMenu randomIrrelevantNavigationMenu() throws Exception {
		NavigationMenu randomIrrelevantNavigationMenu = randomNavigationMenu();

		randomIrrelevantNavigationMenu.setSiteId(irrelevantGroup.getGroupId());

		return randomIrrelevantNavigationMenu;
	}

	protected NavigationMenu randomPatchNavigationMenu() throws Exception {
		return randomNavigationMenu();
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

	protected NavigationMenuResource navigationMenuResource;
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
		LogFactoryUtil.getLog(BaseNavigationMenuResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.delivery.resource.v1_0.NavigationMenuResource
		_navigationMenuResource;

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