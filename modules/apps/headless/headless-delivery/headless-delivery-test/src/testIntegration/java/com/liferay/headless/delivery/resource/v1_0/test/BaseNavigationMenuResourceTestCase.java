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
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PropsValues;
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
					new GraphQLField("id"))),
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
						new GraphQLField("id")))),
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
			"COMPLETED", null, navigationMenu1.getId());

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
			String expectedExecuteStatus, String externalReferenceCode, Long id)
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

		Assert.assertEquals(202, httpResponse.getStatusCode());

		waitForFinish(
			expectedExecuteStatus,
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
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
	public void testGetNavigationMenuPermissionsPage() throws Exception {
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

		return testPostSiteNavigationMenu_addNavigationMenu(
			randomNavigationMenu());
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
	public void testGetSiteNavigationMenusPage() throws Exception {
		Long siteId = testGetSiteNavigationMenusPage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteNavigationMenusPage_getIrrelevantSiteId();

		Page<NavigationMenu> page =
			navigationMenuResource.getSiteNavigationMenusPage(
				siteId, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantSiteId != null) {
			NavigationMenu irrelevantNavigationMenu =
				testGetSiteNavigationMenusPage_addNavigationMenu(
					irrelevantSiteId, randomIrrelevantNavigationMenu());

			page = navigationMenuResource.getSiteNavigationMenusPage(
				irrelevantSiteId, Pagination.of(1, (int)totalCount + 1));

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
			siteId, Pagination.of(1, 10));

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
	public void testGetSiteNavigationMenusPageWithPagination()
		throws Exception {

		Long siteId = testGetSiteNavigationMenusPage_getSiteId();

		Page<NavigationMenu> navigationMenuPage =
			navigationMenuResource.getSiteNavigationMenusPage(siteId, null);

		int totalCount = GetterUtil.getInteger(
			navigationMenuPage.getTotalCount());

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
					siteId,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				navigationMenu1, (List<NavigationMenu>)page1.getItems());

			Page<NavigationMenu> page2 =
				navigationMenuResource.getSiteNavigationMenusPage(
					siteId,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				navigationMenu2, (List<NavigationMenu>)page2.getItems());

			Page<NavigationMenu> page3 =
				navigationMenuResource.getSiteNavigationMenusPage(
					siteId,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				navigationMenu3, (List<NavigationMenu>)page3.getItems());
		}
		else {
			Page<NavigationMenu> page1 =
				navigationMenuResource.getSiteNavigationMenusPage(
					siteId, Pagination.of(1, totalCount + 2));

			List<NavigationMenu> navigationMenus1 =
				(List<NavigationMenu>)page1.getItems();

			Assert.assertEquals(
				navigationMenus1.toString(), totalCount + 2,
				navigationMenus1.size());

			Page<NavigationMenu> page2 =
				navigationMenuResource.getSiteNavigationMenusPage(
					siteId, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<NavigationMenu> navigationMenus2 =
				(List<NavigationMenu>)page2.getItems();

			Assert.assertEquals(
				navigationMenus2.toString(), 1, navigationMenus2.size());

			Page<NavigationMenu> page3 =
				navigationMenuResource.getSiteNavigationMenusPage(
					siteId, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				navigationMenu1, (List<NavigationMenu>)page3.getItems());
			assertContains(
				navigationMenu2, (List<NavigationMenu>)page3.getItems());
			assertContains(
				navigationMenu3, (List<NavigationMenu>)page3.getItems());
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
					put("page", 1);
					put("pageSize", 10);

					put("siteKey", "\"" + siteId + "\"");
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
			testGraphQLGetSiteNavigationMenusPage_addNavigationMenu();
		NavigationMenu navigationMenu2 =
			testGraphQLGetSiteNavigationMenusPage_addNavigationMenu();

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

	protected NavigationMenu
			testGraphQLGetSiteNavigationMenusPage_addNavigationMenu()
		throws Exception {

		return testGraphQLNavigationMenu_addNavigationMenu();
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
			testGraphQLNavigationMenu_addNavigationMenu(randomNavigationMenu);

		Assert.assertTrue(equals(randomNavigationMenu, navigationMenu));
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
					testDeleteSiteNavigationMenuByExternalReferenceCode_getSiteId(
						navigationMenu),
					navigationMenu.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			navigationMenuResource.
				getSiteNavigationMenuByExternalReferenceCodeHttpResponse(
					testDeleteSiteNavigationMenuByExternalReferenceCode_getSiteId(
						navigationMenu),
					navigationMenu.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			navigationMenuResource.
				getSiteNavigationMenuByExternalReferenceCodeHttpResponse(
					testDeleteSiteNavigationMenuByExternalReferenceCode_getSiteId(
						navigationMenu),
					"-"));
	}

	protected Long
			testDeleteSiteNavigationMenuByExternalReferenceCode_getSiteId(
				NavigationMenu navigationMenu)
		throws Exception {

		return navigationMenu.getSiteId();
	}

	protected NavigationMenu
			testDeleteSiteNavigationMenuByExternalReferenceCode_addNavigationMenu()
		throws Exception {

		return navigationMenuResource.postSiteNavigationMenu(
			testGroup.getGroupId(), randomNavigationMenu());
	}

	@Test
	public void testGetSiteNavigationMenuByExternalReferenceCode()
		throws Exception {

		NavigationMenu postNavigationMenu =
			testGetSiteNavigationMenuByExternalReferenceCode_addNavigationMenu();

		NavigationMenu getNavigationMenu =
			navigationMenuResource.getSiteNavigationMenuByExternalReferenceCode(
				testGetSiteNavigationMenuByExternalReferenceCode_getSiteId(
					postNavigationMenu),
				postNavigationMenu.getExternalReferenceCode());

		assertEquals(postNavigationMenu, getNavigationMenu);
		assertValid(getNavigationMenu);
	}

	protected Long testGetSiteNavigationMenuByExternalReferenceCode_getSiteId(
			NavigationMenu navigationMenu)
		throws Exception {

		return navigationMenu.getSiteId();
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
											"\"" +
												testGraphQLGetSiteNavigationMenuByExternalReferenceCode_getSiteId(
													navigationMenu) + "\"");

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
													testGraphQLGetSiteNavigationMenuByExternalReferenceCode_getSiteId(
														navigationMenu) + "\"");

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

	protected Long
			testGraphQLGetSiteNavigationMenuByExternalReferenceCode_getSiteId(
				NavigationMenu navigationMenu)
		throws Exception {

		return navigationMenu.getSiteId();
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

		return testGraphQLNavigationMenu_addNavigationMenu();
	}

	@Test
	public void testPutSiteNavigationMenuByExternalReferenceCode()
		throws Exception {

		NavigationMenu postNavigationMenu =
			testPutSiteNavigationMenuByExternalReferenceCode_addNavigationMenu();

		NavigationMenu randomNavigationMenu = randomNavigationMenu();

		NavigationMenu putNavigationMenu =
			navigationMenuResource.putSiteNavigationMenuByExternalReferenceCode(
				testPutSiteNavigationMenuByExternalReferenceCode_getSiteId(
					postNavigationMenu),
				postNavigationMenu.getExternalReferenceCode(),
				randomNavigationMenu);

		assertEquals(randomNavigationMenu, putNavigationMenu);
		assertValid(putNavigationMenu);

		NavigationMenu getNavigationMenu =
			navigationMenuResource.getSiteNavigationMenuByExternalReferenceCode(
				testPutSiteNavigationMenuByExternalReferenceCode_getSiteId(
					putNavigationMenu),
				putNavigationMenu.getExternalReferenceCode());

		assertEquals(randomNavigationMenu, getNavigationMenu);
		assertValid(getNavigationMenu);

		NavigationMenu newNavigationMenu =
			testPutSiteNavigationMenuByExternalReferenceCode_createNavigationMenu();

		putNavigationMenu =
			navigationMenuResource.putSiteNavigationMenuByExternalReferenceCode(
				testPutSiteNavigationMenuByExternalReferenceCode_getSiteId(
					newNavigationMenu),
				newNavigationMenu.getExternalReferenceCode(),
				newNavigationMenu);

		assertEquals(newNavigationMenu, putNavigationMenu);
		assertValid(putNavigationMenu);

		getNavigationMenu =
			navigationMenuResource.getSiteNavigationMenuByExternalReferenceCode(
				testPutSiteNavigationMenuByExternalReferenceCode_getSiteId(
					putNavigationMenu),
				putNavigationMenu.getExternalReferenceCode());

		assertEquals(newNavigationMenu, getNavigationMenu);

		Assert.assertEquals(
			newNavigationMenu.getExternalReferenceCode(),
			putNavigationMenu.getExternalReferenceCode());
	}

	protected Long testPutSiteNavigationMenuByExternalReferenceCode_getSiteId(
			NavigationMenu navigationMenu)
		throws Exception {

		return navigationMenu.getSiteId();
	}

	protected NavigationMenu
			testPutSiteNavigationMenuByExternalReferenceCode_createNavigationMenu()
		throws Exception {

		return randomNavigationMenu();
	}

	protected NavigationMenu
			testPutSiteNavigationMenuByExternalReferenceCode_addNavigationMenu()
		throws Exception {

		return navigationMenuResource.postSiteNavigationMenu(
			testGroup.getGroupId(), randomNavigationMenu());
	}

	@Test
	public void testGetSiteNavigationMenuPermissionsPage() throws Exception {
		Page<Permission> page =
			navigationMenuResource.getSiteNavigationMenuPermissionsPage(
				testGroup.getGroupId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected NavigationMenu
			testGetSiteNavigationMenuPermissionsPage_addNavigationMenu()
		throws Exception {

		return testPostSiteNavigationMenu_addNavigationMenu(
			randomNavigationMenu());
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
					navigationMenu.getSiteId(),
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
					navigationMenu.getSiteId(),
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

	protected void appendGraphQLFieldValue(StringBuilder sb, Object value)
		throws Exception {

		if (value instanceof Object[]) {
			StringBuilder arraySB = new StringBuilder("[");

			for (Object object : (Object[])value) {
				if (arraySB.length() > 1) {
					arraySB.append(", ");
				}

				arraySB.append("{");

				Class<?> clazz = object.getClass();

				for (java.lang.reflect.Field field :
						getDeclaredFields(clazz.getSuperclass())) {

					arraySB.append(field.getName());
					arraySB.append(": ");

					appendGraphQLFieldValue(arraySB, field.get(object));

					arraySB.append(", ");
				}

				arraySB.setLength(arraySB.length() - 2);

				arraySB.append("}");
			}

			arraySB.append("]");

			sb.append(arraySB.toString());
		}
		else if (value instanceof String) {
			sb.append("\"");
			sb.append(value);
			sb.append("\"");
		}
		else {
			sb.append(value);
		}
	}

	protected NavigationMenu testGraphQLNavigationMenu_addNavigationMenu()
		throws Exception {

		return testGraphQLNavigationMenu_addNavigationMenu(
			randomNavigationMenu());
	}

	protected NavigationMenu testGraphQLNavigationMenu_addNavigationMenu(
			NavigationMenu navigationMenu)
		throws Exception {

		JSONDeserializer<NavigationMenu> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(NavigationMenu.class)) {

			if (!ArrayUtil.contains(
					getAdditionalAssertFieldNames(), field.getName())) {

				continue;
			}

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append(field.getName());
			sb.append(": ");

			appendGraphQLFieldValue(sb, field.get(navigationMenu));
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		graphQLFields.add(new GraphQLField("externalReferenceCode"));

		graphQLFields.add(new GraphQLField("id"));

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteNavigationMenu",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + testGroup.getGroupId() + "\"");
								put("navigationMenu", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteNavigationMenu"),
			NavigationMenu.class);
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