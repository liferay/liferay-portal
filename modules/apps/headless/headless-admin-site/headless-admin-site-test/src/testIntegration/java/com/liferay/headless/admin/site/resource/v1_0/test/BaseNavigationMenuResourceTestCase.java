/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.admin.site.client.dto.v1_0.NavigationMenu;
import com.liferay.headless.admin.site.client.http.HttpInvoker;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.pagination.Pagination;
import com.liferay.headless.admin.site.client.permission.Permission;
import com.liferay.headless.admin.site.client.resource.v1_0.NavigationMenuResource;
import com.liferay.headless.admin.site.client.serdes.v1_0.NavigationMenuSerDes;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.JAXRSWhiteboardTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.annotation.Generated;

import jakarta.ws.rs.core.MultivaluedHashMap;

import java.lang.reflect.Method;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public abstract class BaseNavigationMenuResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		JAXRSWhiteboardTestUtil.ensureReady();
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
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		importTaskResource = ImportTaskResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		permissionsNavigationMenuResource = NavigationMenuResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
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
		navigationMenu.setSiteExternalReferenceCode(regex);

		String json = NavigationMenuSerDes.toJSON(navigationMenu);

		Assert.assertFalse(json.contains(regex));

		navigationMenu = NavigationMenuSerDes.toDTO(json);

		Assert.assertEquals(regex, navigationMenu.getExternalReferenceCode());
		Assert.assertEquals(regex, navigationMenu.getName());
		Assert.assertEquals(
			regex, navigationMenu.getSiteExternalReferenceCode());
	}

	@Test
	public void testDeleteSiteNavigationMenu() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		NavigationMenu navigationMenu =
			testDeleteSiteNavigationMenu_addNavigationMenu();

		assertHttpResponseStatusCode(
			204,
			navigationMenuResource.deleteSiteNavigationMenuHttpResponse(
				navigationMenu.getSiteExternalReferenceCode(),
				navigationMenu.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			navigationMenuResource.getSiteNavigationMenuHttpResponse(
				navigationMenu.getSiteExternalReferenceCode(),
				navigationMenu.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			navigationMenuResource.getSiteNavigationMenuHttpResponse(
				navigationMenu.getSiteExternalReferenceCode(), "-"));
	}

	protected NavigationMenu testDeleteSiteNavigationMenu_addNavigationMenu()
		throws Exception {

		return navigationMenuResource.postSiteNavigationMenu(
			testGroup.getExternalReferenceCode(), randomNavigationMenu());
	}

	@Test
	public void testGetSiteNavigationMenu() throws Exception {
		NavigationMenu postNavigationMenu =
			testGetSiteNavigationMenu_addNavigationMenu();

		NavigationMenu getNavigationMenu =
			navigationMenuResource.getSiteNavigationMenu(
				postNavigationMenu.getSiteExternalReferenceCode(),
				postNavigationMenu.getExternalReferenceCode());

		assertEquals(postNavigationMenu, getNavigationMenu);
		assertValid(getNavigationMenu);

		Assert.assertNull(getNavigationMenu.getPermissions());

		getNavigationMenu =
			permissionsNavigationMenuResource.getSiteNavigationMenu(
				postNavigationMenu.getSiteExternalReferenceCode(),
				postNavigationMenu.getExternalReferenceCode());

		Assert.assertNotNull(getNavigationMenu.getPermissions());
	}

	protected NavigationMenu testGetSiteNavigationMenu_addNavigationMenu()
		throws Exception {

		return navigationMenuResource.postSiteNavigationMenu(
			testGroup.getExternalReferenceCode(), randomNavigationMenu());
	}

	@Test
	public void testGetSiteNavigationMenuPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		NavigationMenu postNavigationMenu =
			testGetSiteNavigationMenuPermissionsPage_addNavigationMenu();

		Page<Permission> page =
			navigationMenuResource.getSiteNavigationMenuPermissionsPage(
				testGroup.getExternalReferenceCode(),
				postNavigationMenu.getExternalReferenceCode(),
				RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected NavigationMenu
			testGetSiteNavigationMenuPermissionsPage_addNavigationMenu()
		throws Exception {

		return navigationMenuResource.postSiteNavigationMenu(
			testGroup.getExternalReferenceCode(), randomNavigationMenu());
	}

	@Test
	public void testGetSiteNavigationMenusPage() throws Exception {
		String siteExternalReferenceCode =
			testGetSiteNavigationMenusPage_getSiteExternalReferenceCode();
		String irrelevantSiteExternalReferenceCode =
			testGetSiteNavigationMenusPage_getIrrelevantSiteExternalReferenceCode();

		Page<NavigationMenu> page =
			navigationMenuResource.getSiteNavigationMenusPage(
				siteExternalReferenceCode, null, null, Pagination.of(1, 10),
				null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteExternalReferenceCode != null) {
			NavigationMenu irrelevantNavigationMenu =
				testGetSiteNavigationMenusPage_addNavigationMenu(
					irrelevantSiteExternalReferenceCode,
					randomIrrelevantNavigationMenu());

			page = navigationMenuResource.getSiteNavigationMenusPage(
				irrelevantSiteExternalReferenceCode, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantNavigationMenu,
				(List<NavigationMenu>)page.getItems());
			assertValid(
				page,
				testGetSiteNavigationMenusPage_getExpectedActions(
					irrelevantSiteExternalReferenceCode));
		}

		NavigationMenu navigationMenu1 =
			testGetSiteNavigationMenusPage_addNavigationMenu(
				siteExternalReferenceCode, randomNavigationMenu());

		NavigationMenu navigationMenu2 =
			testGetSiteNavigationMenusPage_addNavigationMenu(
				siteExternalReferenceCode, randomNavigationMenu());

		page = navigationMenuResource.getSiteNavigationMenusPage(
			siteExternalReferenceCode, null, null,
			Pagination.of(1, (int)totalCount + 2), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(navigationMenu1, (List<NavigationMenu>)page.getItems());
		assertContains(navigationMenu2, (List<NavigationMenu>)page.getItems());
		assertValid(
			page,
			testGetSiteNavigationMenusPage_getExpectedActions(
				siteExternalReferenceCode));

		for (NavigationMenu navigationMenu : page.getItems()) {
			Assert.assertNull(navigationMenu.getPermissions());
		}

		page = permissionsNavigationMenuResource.getSiteNavigationMenusPage(
			siteExternalReferenceCode, null, null, Pagination.of(1, 10), null);

		for (NavigationMenu navigationMenu : page.getItems()) {
			Assert.assertNotNull(navigationMenu.getPermissions());
		}
	}

	protected Map<String, Map<String, String>>
			testGetSiteNavigationMenusPage_getExpectedActions(
				String siteExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			("http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/o/headless-admin-site/v1.0/sites/{siteExternalReferenceCode}/navigation-menus/batch").
					replace(
						"{siteExternalReferenceCode}",
						String.valueOf(siteExternalReferenceCode)));

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

		String siteExternalReferenceCode =
			testGetSiteNavigationMenusPage_getSiteExternalReferenceCode();

		NavigationMenu navigationMenu1 = randomNavigationMenu();

		navigationMenu1 = testGetSiteNavigationMenusPage_addNavigationMenu(
			siteExternalReferenceCode, navigationMenu1);

		for (EntityField entityField : entityFields) {
			Page<NavigationMenu> page =
				navigationMenuResource.getSiteNavigationMenusPage(
					siteExternalReferenceCode, null,
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

		String siteExternalReferenceCode =
			testGetSiteNavigationMenusPage_getSiteExternalReferenceCode();

		NavigationMenu navigationMenu1 =
			testGetSiteNavigationMenusPage_addNavigationMenu(
				siteExternalReferenceCode, randomNavigationMenu());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		NavigationMenu navigationMenu2 =
			testGetSiteNavigationMenusPage_addNavigationMenu(
				siteExternalReferenceCode, randomNavigationMenu());

		for (EntityField entityField : entityFields) {
			Page<NavigationMenu> page =
				navigationMenuResource.getSiteNavigationMenusPage(
					siteExternalReferenceCode, null,
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

		String siteExternalReferenceCode =
			testGetSiteNavigationMenusPage_getSiteExternalReferenceCode();

		Page<NavigationMenu> navigationMenusPage =
			navigationMenuResource.getSiteNavigationMenusPage(
				siteExternalReferenceCode, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			navigationMenusPage.getTotalCount());

		NavigationMenu navigationMenu1 =
			testGetSiteNavigationMenusPage_addNavigationMenu(
				siteExternalReferenceCode, randomNavigationMenu());

		NavigationMenu navigationMenu2 =
			testGetSiteNavigationMenusPage_addNavigationMenu(
				siteExternalReferenceCode, randomNavigationMenu());

		NavigationMenu navigationMenu3 =
			testGetSiteNavigationMenusPage_addNavigationMenu(
				siteExternalReferenceCode, randomNavigationMenu());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<NavigationMenu> page1 =
				navigationMenuResource.getSiteNavigationMenusPage(
					siteExternalReferenceCode, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				navigationMenu1, (List<NavigationMenu>)page1.getItems());

			Page<NavigationMenu> page2 =
				navigationMenuResource.getSiteNavigationMenusPage(
					siteExternalReferenceCode, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				navigationMenu2, (List<NavigationMenu>)page2.getItems());

			Page<NavigationMenu> page3 =
				navigationMenuResource.getSiteNavigationMenusPage(
					siteExternalReferenceCode, null, null,
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
					siteExternalReferenceCode, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<NavigationMenu> navigationMenus1 =
				(List<NavigationMenu>)page1.getItems();

			Assert.assertEquals(
				navigationMenus1.toString(), totalCount + 2,
				navigationMenus1.size());

			Page<NavigationMenu> page2 =
				navigationMenuResource.getSiteNavigationMenusPage(
					siteExternalReferenceCode, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<NavigationMenu> navigationMenus2 =
				(List<NavigationMenu>)page2.getItems();

			Assert.assertEquals(
				navigationMenus2.toString(), 1, navigationMenus2.size());

			Page<NavigationMenu> page3 =
				navigationMenuResource.getSiteNavigationMenusPage(
					siteExternalReferenceCode, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

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

		String siteExternalReferenceCode =
			testGetSiteNavigationMenusPage_getSiteExternalReferenceCode();

		NavigationMenu navigationMenu1 = randomNavigationMenu();
		NavigationMenu navigationMenu2 = randomNavigationMenu();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, navigationMenu1, navigationMenu2);
		}

		navigationMenu1 = testGetSiteNavigationMenusPage_addNavigationMenu(
			siteExternalReferenceCode, navigationMenu1);

		navigationMenu2 = testGetSiteNavigationMenusPage_addNavigationMenu(
			siteExternalReferenceCode, navigationMenu2);

		Page<NavigationMenu> page =
			navigationMenuResource.getSiteNavigationMenusPage(
				siteExternalReferenceCode, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<NavigationMenu> ascPage =
				navigationMenuResource.getSiteNavigationMenusPage(
					siteExternalReferenceCode, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				navigationMenu1, (List<NavigationMenu>)ascPage.getItems());
			assertContains(
				navigationMenu2, (List<NavigationMenu>)ascPage.getItems());

			Page<NavigationMenu> descPage =
				navigationMenuResource.getSiteNavigationMenusPage(
					siteExternalReferenceCode, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				navigationMenu2, (List<NavigationMenu>)descPage.getItems());
			assertContains(
				navigationMenu1, (List<NavigationMenu>)descPage.getItems());
		}
	}

	protected NavigationMenu testGetSiteNavigationMenusPage_addNavigationMenu(
			String siteExternalReferenceCode, NavigationMenu navigationMenu)
		throws Exception {

		return navigationMenuResource.postSiteNavigationMenu(
			siteExternalReferenceCode, navigationMenu);
	}

	protected String
			testGetSiteNavigationMenusPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteNavigationMenusPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	@Test
	public void testPostSiteNavigationMenu() throws Exception {
		NavigationMenu randomNavigationMenu = randomNavigationMenu();

		NavigationMenu postNavigationMenu =
			testPostSiteNavigationMenu_addNavigationMenu(randomNavigationMenu);

		assertEquals(randomNavigationMenu, postNavigationMenu);
		assertValid(postNavigationMenu);

		NavigationMenu randomPermissionsNavigationMenu1 =
			randomPermissionsNavigationMenu();

		NavigationMenu postPermissionsNavigationMenu1 =
			testPostSiteNavigationMenu_addNavigationMenu(
				randomPermissionsNavigationMenu1);

		Assert.assertNull(postPermissionsNavigationMenu1.getPermissions());

		NavigationMenu randomPermissionsNavigationMenu2 =
			randomPermissionsNavigationMenu();

		NavigationMenu postPermissionsNavigationMenu2 =
			testPostSiteNavigationMenu_addPermissionsNavigationMenu(
				randomPermissionsNavigationMenu2);

		Assert.assertNotNull(postPermissionsNavigationMenu2.getPermissions());
	}

	protected NavigationMenu testPostSiteNavigationMenu_addNavigationMenu(
			NavigationMenu navigationMenu)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected NavigationMenu
			testPostSiteNavigationMenu_addPermissionsNavigationMenu(
				NavigationMenu navigationMenu)
		throws Exception {

		return permissionsNavigationMenuResource.postSiteNavigationMenu(
			testGetSiteNavigationMenusPage_getSiteExternalReferenceCode(),
			navigationMenu);
	}

	@Test
	public void testPutSiteNavigationMenu() throws Exception {
		NavigationMenu postNavigationMenu =
			testPutSiteNavigationMenu_addNavigationMenu();

		NavigationMenu randomNavigationMenu = randomNavigationMenu();

		NavigationMenu putNavigationMenu =
			navigationMenuResource.putSiteNavigationMenu(
				postNavigationMenu.getSiteExternalReferenceCode(),
				postNavigationMenu.getExternalReferenceCode(),
				randomNavigationMenu);

		assertEquals(randomNavigationMenu, putNavigationMenu);
		assertValid(putNavigationMenu);

		Assert.assertNull(putNavigationMenu.getPermissions());

		NavigationMenu getNavigationMenu =
			navigationMenuResource.getSiteNavigationMenu(
				putNavigationMenu.getSiteExternalReferenceCode(),
				putNavigationMenu.getExternalReferenceCode());

		assertEquals(randomNavigationMenu, getNavigationMenu);
		assertValid(getNavigationMenu);

		NavigationMenu randomPermissionsNavigationMenu =
			randomPermissionsNavigationMenu();

		putNavigationMenu = navigationMenuResource.putSiteNavigationMenu(
			postNavigationMenu.getSiteExternalReferenceCode(),
			postNavigationMenu.getExternalReferenceCode(),
			randomPermissionsNavigationMenu);

		assertEquals(randomPermissionsNavigationMenu, putNavigationMenu);
		assertValid(putNavigationMenu);

		Assert.assertNull(putNavigationMenu.getPermissions());

		putNavigationMenu =
			permissionsNavigationMenuResource.putSiteNavigationMenu(
				postNavigationMenu.getSiteExternalReferenceCode(),
				postNavigationMenu.getExternalReferenceCode(),
				randomPermissionsNavigationMenu);

		Assert.assertNotNull(putNavigationMenu.getPermissions());
	}

	protected NavigationMenu testPutSiteNavigationMenu_addNavigationMenu()
		throws Exception {

		return navigationMenuResource.postSiteNavigationMenu(
			testGroup.getExternalReferenceCode(), randomNavigationMenu());
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
					testGroup.getExternalReferenceCode(),
					navigationMenu.getExternalReferenceCode(),
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
					testGroup.getExternalReferenceCode(),
					navigationMenu.getExternalReferenceCode(),
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
			testGroup.getExternalReferenceCode(), randomNavigationMenu());
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		NavigationMenu navigationMenu1 =
			testBatchEngineDeleteImportTask_addSiteNavigationMenu();

		testBatchEngineDeleteImportTask_deleteNavigationMenu(
			200, navigationMenu1.getExternalReferenceCode(),
			"siteExternalReferenceCode", testGroup.getExternalReferenceCode());

		assertHttpResponseStatusCode(
			404,
			navigationMenuResource.getSiteNavigationMenuHttpResponse(
				navigationMenu1.getSiteExternalReferenceCode(),
				navigationMenu1.getExternalReferenceCode()));
	}

	protected NavigationMenu
			testBatchEngineDeleteImportTask_addSiteNavigationMenu()
		throws Exception {

		return testDeleteSiteNavigationMenu_addNavigationMenu();
	}

	protected void testBatchEngineDeleteImportTask_deleteNavigationMenu(
			int expectedStatusCode, String externalReferenceCode,
			String... parameters)
		throws Exception {

		ImportTaskResource importTaskResource = ImportTaskResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).parameters(
			parameters
		).build();

		HttpResponse httpResponse =
			importTaskResource.deleteImportTaskHttpResponse(
				"com.liferay.headless.admin.site.dto.v1_0.NavigationMenu", null,
				null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		if (expectedStatusCode == 200) {
			waitForFinish(
				"COMPLETED",
				JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
		}
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

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

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (navigationMenu.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("auto", additionalAssertFieldName)) {
				if (navigationMenu.getAuto() == null) {
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

			if (Objects.equals(
					"siteExternalReferenceCode", additionalAssertFieldName)) {

				if (navigationMenu.getSiteExternalReferenceCode() == null) {
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

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.admin.site.dto.v1_0.NavigationMenu.
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

			if (Objects.equals("auto", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						navigationMenu1.getAuto(), navigationMenu2.getAuto())) {

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

			if (Objects.equals(
					"siteExternalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						navigationMenu1.getSiteExternalReferenceCode(),
						navigationMenu2.getSiteExternalReferenceCode())) {

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

		if (entityFieldName.equals("auto")) {
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

		if (entityFieldName.equals("siteExternalReferenceCode")) {
			Object object = navigationMenu.getSiteExternalReferenceCode();

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
		httpInvoker.path(
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/o/graphql");
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
				auto = RandomTestUtil.randomBoolean();
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				siteExternalReferenceCode =
					testGroup.getExternalReferenceCode();
			}
		};
	}

	protected NavigationMenu randomIrrelevantNavigationMenu() throws Exception {
		NavigationMenu randomIrrelevantNavigationMenu = randomNavigationMenu();

		randomIrrelevantNavigationMenu.setSiteExternalReferenceCode(
			irrelevantGroup.getExternalReferenceCode());

		return randomIrrelevantNavigationMenu;
	}

	protected NavigationMenu randomPatchNavigationMenu() throws Exception {
		return randomNavigationMenu();
	}

	protected NavigationMenu randomPermissionsNavigationMenu()
		throws Exception {

		NavigationMenu navigationMenu = randomNavigationMenu();

		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		navigationMenu.setPermissions(
			new Permission[] {
				new Permission() {
					{
						setActionIds(new String[] {"VIEW"});
						setRoleName(role.getName());
					}
				}
			});

		return navigationMenu;
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
	protected NavigationMenuResource permissionsNavigationMenuResource;
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
	private com.liferay.headless.admin.site.resource.v1_0.NavigationMenuResource
		_navigationMenuResource;

}
// LIFERAY-REST-BUILDER-HASH:-987770916