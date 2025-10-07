/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
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

import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.MasterPage;
import com.liferay.headless.admin.site.client.http.HttpInvoker;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.pagination.Pagination;
import com.liferay.headless.admin.site.client.permission.Permission;
import com.liferay.headless.admin.site.client.resource.v1_0.MasterPageResource;
import com.liferay.headless.admin.site.client.serdes.v1_0.MasterPageSerDes;
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
public abstract class BaseMasterPageResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

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

		_masterPageResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		masterPageResource = MasterPageResource.builder(
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

		permissionsMasterPageResource = MasterPageResource.builder(
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

		MasterPage masterPage1 = randomMasterPage();

		String json = objectMapper.writeValueAsString(masterPage1);

		MasterPage masterPage2 = MasterPageSerDes.toDTO(json);

		Assert.assertTrue(equals(masterPage1, masterPage2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		MasterPage masterPage = randomMasterPage();

		String json1 = objectMapper.writeValueAsString(masterPage);
		String json2 = MasterPageSerDes.toJSON(masterPage);

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

		MasterPage masterPage = randomMasterPage();

		masterPage.setExternalReferenceCode(regex);
		masterPage.setKey(regex);
		masterPage.setName(regex);
		masterPage.setUuid(regex);

		String json = MasterPageSerDes.toJSON(masterPage);

		Assert.assertFalse(json.contains(regex));

		masterPage = MasterPageSerDes.toDTO(json);

		Assert.assertEquals(regex, masterPage.getExternalReferenceCode());
		Assert.assertEquals(regex, masterPage.getKey());
		Assert.assertEquals(regex, masterPage.getName());
		Assert.assertEquals(regex, masterPage.getUuid());
	}

	@Test
	public void testDeleteSiteMasterPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		MasterPage masterPage = testDeleteSiteMasterPage_addMasterPage();

		assertHttpResponseStatusCode(
			204,
			masterPageResource.deleteSiteMasterPageHttpResponse(
				testDeleteSiteMasterPage_getSiteExternalReferenceCode(),
				masterPage.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			masterPageResource.getSiteMasterPageHttpResponse(
				testDeleteSiteMasterPage_getSiteExternalReferenceCode(),
				masterPage.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			masterPageResource.getSiteMasterPageHttpResponse(
				testDeleteSiteMasterPage_getSiteExternalReferenceCode(), "-"));
	}

	protected MasterPage testDeleteSiteMasterPage_addMasterPage()
		throws Exception {

		return masterPageResource.postSiteMasterPage(
			testGroup.getExternalReferenceCode(), randomMasterPage());
	}

	protected String testDeleteSiteMasterPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	@Test
	public void testGetSiteMasterPage() throws Exception {
		MasterPage postMasterPage = testGetSiteMasterPage_addMasterPage();

		MasterPage getMasterPage = masterPageResource.getSiteMasterPage(
			testGetSiteMasterPage_getSiteExternalReferenceCode(),
			postMasterPage.getExternalReferenceCode());

		assertEquals(postMasterPage, getMasterPage);
		assertValid(getMasterPage);

		Assert.assertNull(getMasterPage.getPermissions());

		getMasterPage = permissionsMasterPageResource.getSiteMasterPage(
			testGetSiteMasterPage_getSiteExternalReferenceCode(),
			postMasterPage.getExternalReferenceCode());

		Assert.assertNotNull(getMasterPage.getPermissions());
	}

	protected MasterPage testGetSiteMasterPage_addMasterPage()
		throws Exception {

		return masterPageResource.postSiteMasterPage(
			testGroup.getExternalReferenceCode(), randomMasterPage());
	}

	protected String testGetSiteMasterPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	@Test
	public void testGetSiteMasterPagePermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		MasterPage postMasterPage =
			testGetSiteMasterPagePermissionsPage_addMasterPage();

		Page<Permission> page =
			masterPageResource.getSiteMasterPagePermissionsPage(
				testGroup.getExternalReferenceCode(),
				postMasterPage.getExternalReferenceCode(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected MasterPage testGetSiteMasterPagePermissionsPage_addMasterPage()
		throws Exception {

		return masterPageResource.postSiteMasterPage(
			testGroup.getExternalReferenceCode(), randomMasterPage());
	}

	@Test
	public void testGetSiteMasterPagesPage() throws Exception {
		String siteExternalReferenceCode =
			testGetSiteMasterPagesPage_getSiteExternalReferenceCode();
		String irrelevantSiteExternalReferenceCode =
			testGetSiteMasterPagesPage_getIrrelevantSiteExternalReferenceCode();

		Page<MasterPage> page = masterPageResource.getSiteMasterPagesPage(
			siteExternalReferenceCode, null, null, null, Pagination.of(1, 10),
			null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteExternalReferenceCode != null) {
			MasterPage irrelevantMasterPage =
				testGetSiteMasterPagesPage_addMasterPage(
					irrelevantSiteExternalReferenceCode,
					randomIrrelevantMasterPage());

			page = masterPageResource.getSiteMasterPagesPage(
				irrelevantSiteExternalReferenceCode, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantMasterPage, (List<MasterPage>)page.getItems());
			assertValid(
				page,
				testGetSiteMasterPagesPage_getExpectedActions(
					irrelevantSiteExternalReferenceCode));
		}

		MasterPage masterPage1 = testGetSiteMasterPagesPage_addMasterPage(
			siteExternalReferenceCode, randomMasterPage());

		MasterPage masterPage2 = testGetSiteMasterPagesPage_addMasterPage(
			siteExternalReferenceCode, randomMasterPage());

		page = masterPageResource.getSiteMasterPagesPage(
			siteExternalReferenceCode, null, null, null, Pagination.of(1, 10),
			null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(masterPage1, (List<MasterPage>)page.getItems());
		assertContains(masterPage2, (List<MasterPage>)page.getItems());
		assertValid(
			page,
			testGetSiteMasterPagesPage_getExpectedActions(
				siteExternalReferenceCode));

		for (MasterPage masterPage : page.getItems()) {
			Assert.assertNull(masterPage.getPermissions());
		}

		page = permissionsMasterPageResource.getSiteMasterPagesPage(
			siteExternalReferenceCode, null, null, null, Pagination.of(1, 10),
			null);

		for (MasterPage masterPage : page.getItems()) {
			Assert.assertNotNull(masterPage.getPermissions());
		}
	}

	protected Map<String, Map<String, String>>
			testGetSiteMasterPagesPage_getExpectedActions(
				String siteExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-admin-site/v1.0/sites/{siteExternalReferenceCode}/master-pages/batch".
				replace(
					"{siteExternalReferenceCode}",
					String.valueOf(siteExternalReferenceCode)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetSiteMasterPagesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSiteMasterPagesPage_getSiteExternalReferenceCode();

		MasterPage masterPage1 = randomMasterPage();

		masterPage1 = testGetSiteMasterPagesPage_addMasterPage(
			siteExternalReferenceCode, masterPage1);

		for (EntityField entityField : entityFields) {
			Page<MasterPage> page = masterPageResource.getSiteMasterPagesPage(
				siteExternalReferenceCode, null, null,
				getFilterString(entityField, "between", masterPage1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(masterPage1),
				(List<MasterPage>)page.getItems());
		}
	}

	@Test
	public void testGetSiteMasterPagesPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteMasterPagesPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteMasterPagesPageWithFilterStringContains()
		throws Exception {

		testGetSiteMasterPagesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteMasterPagesPageWithFilterStringEquals()
		throws Exception {

		testGetSiteMasterPagesPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteMasterPagesPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteMasterPagesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetSiteMasterPagesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSiteMasterPagesPage_getSiteExternalReferenceCode();

		MasterPage masterPage1 = testGetSiteMasterPagesPage_addMasterPage(
			siteExternalReferenceCode, randomMasterPage());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		MasterPage masterPage2 = testGetSiteMasterPagesPage_addMasterPage(
			siteExternalReferenceCode, randomMasterPage());

		for (EntityField entityField : entityFields) {
			Page<MasterPage> page = masterPageResource.getSiteMasterPagesPage(
				siteExternalReferenceCode, null, null,
				getFilterString(entityField, operator, masterPage1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(masterPage1),
				(List<MasterPage>)page.getItems());
		}
	}

	@Test
	public void testGetSiteMasterPagesPageWithPagination() throws Exception {
		String siteExternalReferenceCode =
			testGetSiteMasterPagesPage_getSiteExternalReferenceCode();

		Page<MasterPage> masterPagesPage =
			masterPageResource.getSiteMasterPagesPage(
				siteExternalReferenceCode, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(masterPagesPage.getTotalCount());

		MasterPage masterPage1 = testGetSiteMasterPagesPage_addMasterPage(
			siteExternalReferenceCode, randomMasterPage());

		MasterPage masterPage2 = testGetSiteMasterPagesPage_addMasterPage(
			siteExternalReferenceCode, randomMasterPage());

		MasterPage masterPage3 = testGetSiteMasterPagesPage_addMasterPage(
			siteExternalReferenceCode, randomMasterPage());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<MasterPage> page1 = masterPageResource.getSiteMasterPagesPage(
				siteExternalReferenceCode, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(masterPage1, (List<MasterPage>)page1.getItems());

			Page<MasterPage> page2 = masterPageResource.getSiteMasterPagesPage(
				siteExternalReferenceCode, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(masterPage2, (List<MasterPage>)page2.getItems());

			Page<MasterPage> page3 = masterPageResource.getSiteMasterPagesPage(
				siteExternalReferenceCode, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(masterPage3, (List<MasterPage>)page3.getItems());
		}
		else {
			Page<MasterPage> page1 = masterPageResource.getSiteMasterPagesPage(
				siteExternalReferenceCode, null, null, null,
				Pagination.of(1, totalCount + 2), null);

			List<MasterPage> masterPages1 = (List<MasterPage>)page1.getItems();

			Assert.assertEquals(
				masterPages1.toString(), totalCount + 2, masterPages1.size());

			Page<MasterPage> page2 = masterPageResource.getSiteMasterPagesPage(
				siteExternalReferenceCode, null, null, null,
				Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<MasterPage> masterPages2 = (List<MasterPage>)page2.getItems();

			Assert.assertEquals(
				masterPages2.toString(), 1, masterPages2.size());

			Page<MasterPage> page3 = masterPageResource.getSiteMasterPagesPage(
				siteExternalReferenceCode, null, null, null,
				Pagination.of(1, (int)totalCount + 3), null);

			assertContains(masterPage1, (List<MasterPage>)page3.getItems());
			assertContains(masterPage2, (List<MasterPage>)page3.getItems());
			assertContains(masterPage3, (List<MasterPage>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteMasterPagesPageWithSortDateTime() throws Exception {
		testGetSiteMasterPagesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, masterPage1, masterPage2) -> {
				BeanTestUtil.setProperty(
					masterPage1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteMasterPagesPageWithSortDouble() throws Exception {
		testGetSiteMasterPagesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, masterPage1, masterPage2) -> {
				BeanTestUtil.setProperty(
					masterPage1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					masterPage2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteMasterPagesPageWithSortInteger() throws Exception {
		testGetSiteMasterPagesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, masterPage1, masterPage2) -> {
				BeanTestUtil.setProperty(masterPage1, entityField.getName(), 0);
				BeanTestUtil.setProperty(masterPage2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteMasterPagesPageWithSortString() throws Exception {
		testGetSiteMasterPagesPageWithSort(
			EntityField.Type.STRING,
			(entityField, masterPage1, masterPage2) -> {
				Class<?> clazz = masterPage1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						masterPage1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						masterPage2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						masterPage1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						masterPage2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						masterPage1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						masterPage2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSiteMasterPagesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, MasterPage, MasterPage, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSiteMasterPagesPage_getSiteExternalReferenceCode();

		MasterPage masterPage1 = randomMasterPage();
		MasterPage masterPage2 = randomMasterPage();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, masterPage1, masterPage2);
		}

		masterPage1 = testGetSiteMasterPagesPage_addMasterPage(
			siteExternalReferenceCode, masterPage1);

		masterPage2 = testGetSiteMasterPagesPage_addMasterPage(
			siteExternalReferenceCode, masterPage2);

		Page<MasterPage> page = masterPageResource.getSiteMasterPagesPage(
			siteExternalReferenceCode, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<MasterPage> ascPage =
				masterPageResource.getSiteMasterPagesPage(
					siteExternalReferenceCode, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(masterPage1, (List<MasterPage>)ascPage.getItems());
			assertContains(masterPage2, (List<MasterPage>)ascPage.getItems());

			Page<MasterPage> descPage =
				masterPageResource.getSiteMasterPagesPage(
					siteExternalReferenceCode, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(masterPage2, (List<MasterPage>)descPage.getItems());
			assertContains(masterPage1, (List<MasterPage>)descPage.getItems());
		}
	}

	protected MasterPage testGetSiteMasterPagesPage_addMasterPage(
			String siteExternalReferenceCode, MasterPage masterPage)
		throws Exception {

		return masterPageResource.postSiteMasterPage(
			siteExternalReferenceCode, masterPage);
	}

	protected String testGetSiteMasterPagesPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteMasterPagesPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	@Test
	public void testPatchSiteMasterPage() throws Exception {
		MasterPage postMasterPage = testPatchSiteMasterPage_addMasterPage();

		MasterPage randomPatchMasterPage = randomPatchMasterPage();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		MasterPage patchMasterPage = masterPageResource.patchSiteMasterPage(
			null, postMasterPage.getExternalReferenceCode(),
			randomPatchMasterPage);

		MasterPage expectedPatchMasterPage = postMasterPage.clone();

		BeanTestUtil.copyProperties(
			randomPatchMasterPage, expectedPatchMasterPage);

		MasterPage getMasterPage = masterPageResource.getSiteMasterPage(
			null, patchMasterPage.getExternalReferenceCode());

		assertEquals(expectedPatchMasterPage, getMasterPage);
		assertValid(getMasterPage);
	}

	protected MasterPage testPatchSiteMasterPage_addMasterPage()
		throws Exception {

		return masterPageResource.postSiteMasterPage(
			testGroup.getExternalReferenceCode(), randomMasterPage());
	}

	@Test
	public void testPostSiteMasterPage() throws Exception {
		MasterPage randomMasterPage = randomMasterPage();

		MasterPage postMasterPage = testPostSiteMasterPage_addMasterPage(
			randomMasterPage);

		assertEquals(randomMasterPage, postMasterPage);
		assertValid(postMasterPage);

		MasterPage randomPermissionsMasterPage1 = randomPermissionsMasterPage();

		MasterPage postPermissionsMasterPage1 =
			testPostSiteMasterPage_addMasterPage(randomPermissionsMasterPage1);

		Assert.assertNull(postPermissionsMasterPage1.getPermissions());

		MasterPage randomPermissionsMasterPage2 = randomPermissionsMasterPage();

		MasterPage postPermissionsMasterPage2 =
			testPostSiteMasterPage_addPermissionsMasterPage(
				randomPermissionsMasterPage2);

		Assert.assertNotNull(postPermissionsMasterPage2.getPermissions());
	}

	protected MasterPage testPostSiteMasterPage_addMasterPage(
			MasterPage masterPage)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected MasterPage testPostSiteMasterPage_addPermissionsMasterPage(
			MasterPage masterPage)
		throws Exception {

		return permissionsMasterPageResource.postSiteMasterPage(
			testGetSiteMasterPagesPage_getSiteExternalReferenceCode(),
			masterPage);
	}

	@Test
	public void testPutSiteMasterPage() throws Exception {
		MasterPage postMasterPage = testPutSiteMasterPage_addMasterPage();

		MasterPage randomMasterPage = randomMasterPage();

		MasterPage putMasterPage = masterPageResource.putSiteMasterPage(
			testPutSiteMasterPage_getSiteExternalReferenceCode(),
			postMasterPage.getExternalReferenceCode(), randomMasterPage);

		assertEquals(randomMasterPage, putMasterPage);
		assertValid(putMasterPage);

		Assert.assertNull(putMasterPage.getPermissions());

		MasterPage getMasterPage = masterPageResource.getSiteMasterPage(
			testPutSiteMasterPage_getSiteExternalReferenceCode(),
			putMasterPage.getExternalReferenceCode());

		assertEquals(randomMasterPage, getMasterPage);
		assertValid(getMasterPage);

		MasterPage randomPermissionsMasterPage = randomPermissionsMasterPage();

		putMasterPage = masterPageResource.putSiteMasterPage(
			testPutSiteMasterPage_getSiteExternalReferenceCode(),
			postMasterPage.getExternalReferenceCode(),
			randomPermissionsMasterPage);

		assertEquals(randomPermissionsMasterPage, putMasterPage);
		assertValid(putMasterPage);

		Assert.assertNull(putMasterPage.getPermissions());

		putMasterPage = permissionsMasterPageResource.putSiteMasterPage(
			testPutSiteMasterPage_getSiteExternalReferenceCode(),
			postMasterPage.getExternalReferenceCode(),
			randomPermissionsMasterPage);

		Assert.assertNotNull(putMasterPage.getPermissions());
	}

	protected MasterPage testPutSiteMasterPage_addMasterPage()
		throws Exception {

		return masterPageResource.postSiteMasterPage(
			testGroup.getExternalReferenceCode(), randomMasterPage());
	}

	protected String testPutSiteMasterPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	@Test
	public void testPutSiteMasterPagePermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		MasterPage masterPage =
			testPutSiteMasterPagePermissionsPage_addMasterPage();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			masterPageResource.putSiteMasterPagePermissionsPageHttpResponse(
				testGroup.getExternalReferenceCode(),
				masterPage.getExternalReferenceCode(),
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
			masterPageResource.putSiteMasterPagePermissionsPageHttpResponse(
				testGroup.getExternalReferenceCode(),
				masterPage.getExternalReferenceCode(),
				new Permission[] {
					new Permission() {
						{
							setActionIds(new String[] {"-"});
							setRoleName("-");
						}
					}
				}));
	}

	protected MasterPage testPutSiteMasterPagePermissionsPage_addMasterPage()
		throws Exception {

		return masterPageResource.postSiteMasterPage(
			testGroup.getExternalReferenceCode(), randomMasterPage());
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		MasterPage masterPage1 =
			testBatchEngineDeleteImportTask_addSiteMasterPage();

		testBatchEngineDeleteImportTask_deleteMasterPage(
			200, masterPage1.getExternalReferenceCode(),
			"siteExternalReferenceCode", testGroup.getExternalReferenceCode());

		assertHttpResponseStatusCode(
			404,
			masterPageResource.getSiteMasterPageHttpResponse(
				testBatchEngineDeleteImportTask_getSiteExternalReferenceCode(),
				masterPage1.getExternalReferenceCode()));
	}

	protected MasterPage testBatchEngineDeleteImportTask_addSiteMasterPage()
		throws Exception {

		return testDeleteSiteMasterPage_addMasterPage();
	}

	protected void testBatchEngineDeleteImportTask_deleteMasterPage(
			int expectedStatusCode, String externalReferenceCode,
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
				"com.liferay.headless.admin.site.dto.v1_0.MasterPage", null,
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

	protected String
			testBatchEngineDeleteImportTask_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	@Test
	public void testPostSiteMasterPagePageSpecification() throws Exception {
		Assert.assertTrue(true);
	}

	protected void assertContains(
		MasterPage masterPage, List<MasterPage> masterPages) {

		boolean contains = false;

		for (MasterPage item : masterPages) {
			if (equals(masterPage, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			masterPages + " does not contain " + masterPage, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		MasterPage masterPage1, MasterPage masterPage2) {

		Assert.assertTrue(
			masterPage1 + " does not equal " + masterPage2,
			equals(masterPage1, masterPage2));
	}

	protected void assertEquals(
		List<MasterPage> masterPages1, List<MasterPage> masterPages2) {

		Assert.assertEquals(masterPages1.size(), masterPages2.size());

		for (int i = 0; i < masterPages1.size(); i++) {
			MasterPage masterPage1 = masterPages1.get(i);
			MasterPage masterPage2 = masterPages2.get(i);

			assertEquals(masterPage1, masterPage2);
		}
	}

	protected void assertEquals(
		ContentPageSpecification contentPageSpecification1,
		ContentPageSpecification contentPageSpecification2) {

		Assert.assertTrue(
			contentPageSpecification1 + " does not equal " +
				contentPageSpecification2,
			equals(contentPageSpecification1, contentPageSpecification2));
	}

	protected void assertEqualsIgnoringOrder(
		List<MasterPage> masterPages1, List<MasterPage> masterPages2) {

		Assert.assertEquals(masterPages1.size(), masterPages2.size());

		for (MasterPage masterPage1 : masterPages1) {
			boolean contains = false;

			for (MasterPage masterPage2 : masterPages2) {
				if (equals(masterPage1, masterPage2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				masterPages2 + " does not contain " + masterPage1, contains);
		}
	}

	protected void assertValid(MasterPage masterPage) throws Exception {
		boolean valid = true;

		if (masterPage.getDateCreated() == null) {
			valid = false;
		}

		if (masterPage.getDateModified() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (masterPage.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("datePublished", additionalAssertFieldName)) {
				if (masterPage.getDatePublished() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (masterPage.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (masterPage.getKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("keywords", additionalAssertFieldName)) {
				if (masterPage.getKeywords() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("markedAsDefault", additionalAssertFieldName)) {
				if (masterPage.getMarkedAsDefault() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (masterPage.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"pageSpecifications", additionalAssertFieldName)) {

				if (masterPage.getPageSpecifications() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (masterPage.getPermissions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryItemExternalReferences",
					additionalAssertFieldName)) {

				if (masterPage.getTaxonomyCategoryItemExternalReferences() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("thumbnail", additionalAssertFieldName)) {
				if (masterPage.getThumbnail() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("uuid", additionalAssertFieldName)) {
				if (masterPage.getUuid() == null) {
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

	protected void assertValid(Page<MasterPage> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<MasterPage> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<MasterPage> masterPages = page.getItems();

		int size = masterPages.size();

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

	protected void assertValid(
		ContentPageSpecification contentPageSpecification) {

		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalContentPageSpecificationAssertFieldNames()) {

			if (Objects.equals(
					"draftContentPageSpecificationExternalReferenceCode",
					additionalAssertFieldName)) {

				if (contentPageSpecification.
						getDraftContentPageSpecificationExternalReferenceCode() ==
							null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("pageExperiences", additionalAssertFieldName)) {
				if (contentPageSpecification.getPageExperiences() == null) {
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

	protected String[] getAdditionalAssertFieldNames() {
		return new String[0];
	}

	protected String[] getAdditionalContentPageSpecificationAssertFieldNames() {
		return new String[0];
	}

	protected List<GraphQLField> getGraphQLFields() throws Exception {
		List<GraphQLField> graphQLFields = new ArrayList<>();

		graphQLFields.add(new GraphQLField("externalReferenceCode"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.admin.site.dto.v1_0.MasterPage.
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

	protected boolean equals(MasterPage masterPage1, MasterPage masterPage2) {
		if (masterPage1 == masterPage2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						masterPage1.getCreator(), masterPage2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						masterPage1.getDateCreated(),
						masterPage2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						masterPage1.getDateModified(),
						masterPage2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("datePublished", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						masterPage1.getDatePublished(),
						masterPage2.getDatePublished())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						masterPage1.getExternalReferenceCode(),
						masterPage2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						masterPage1.getKey(), masterPage2.getKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("keywords", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						masterPage1.getKeywords(), masterPage2.getKeywords())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("markedAsDefault", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						masterPage1.getMarkedAsDefault(),
						masterPage2.getMarkedAsDefault())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						masterPage1.getName(), masterPage2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"pageSpecifications", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						masterPage1.getPageSpecifications(),
						masterPage2.getPageSpecifications())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						masterPage1.getPermissions(),
						masterPage2.getPermissions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryItemExternalReferences",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						masterPage1.getTaxonomyCategoryItemExternalReferences(),
						masterPage2.
							getTaxonomyCategoryItemExternalReferences())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("thumbnail", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						masterPage1.getThumbnail(),
						masterPage2.getThumbnail())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("uuid", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						masterPage1.getUuid(), masterPage2.getUuid())) {

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

	protected boolean equals(
		ContentPageSpecification contentPageSpecification1,
		ContentPageSpecification contentPageSpecification2) {

		if (contentPageSpecification1 == contentPageSpecification2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalContentPageSpecificationAssertFieldNames()) {

			if (Objects.equals(
					"draftContentPageSpecificationExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						contentPageSpecification1.
							getDraftContentPageSpecificationExternalReferenceCode(),
						contentPageSpecification2.
							getDraftContentPageSpecificationExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("pageExperiences", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						contentPageSpecification1.getPageExperiences(),
						contentPageSpecification2.getPageExperiences())) {

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

		if (!(_masterPageResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_masterPageResource;

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
		EntityField entityField, String operator, MasterPage masterPage) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = masterPage.getDateCreated();

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

				sb.append(_format.format(masterPage.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = masterPage.getDateModified();

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

				sb.append(_format.format(masterPage.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("datePublished")) {
			if (operator.equals("between")) {
				Date date = masterPage.getDatePublished();

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

				sb.append(_format.format(masterPage.getDatePublished()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = masterPage.getExternalReferenceCode();

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

		if (entityFieldName.equals("key")) {
			Object object = masterPage.getKey();

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

		if (entityFieldName.equals("keywords")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("markedAsDefault")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("name")) {
			Object object = masterPage.getName();

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

		if (entityFieldName.equals("pageSpecifications")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("permissions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("taxonomyCategoryItemExternalReferences")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("thumbnail")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("uuid")) {
			Object object = masterPage.getUuid();

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

	protected MasterPage randomMasterPage() throws Exception {
		return new MasterPage() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				datePublished = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				markedAsDefault = RandomTestUtil.randomBoolean();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				uuid = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected MasterPage randomIrrelevantMasterPage() throws Exception {
		MasterPage randomIrrelevantMasterPage = randomMasterPage();

		return randomIrrelevantMasterPage;
	}

	protected MasterPage randomPatchMasterPage() throws Exception {
		return randomMasterPage();
	}

	protected MasterPage randomPermissionsMasterPage() throws Exception {
		MasterPage masterPage = randomMasterPage();

		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		masterPage.setPermissions(
			new Permission[] {
				new Permission() {
					{
						setActionIds(new String[] {"VIEW"});
						setRoleName(role.getName());
					}
				}
			});

		return masterPage;
	}

	protected ContentPageSpecification randomContentPageSpecification()
		throws Exception {

		return new ContentPageSpecification() {
			{
				draftContentPageSpecificationExternalReferenceCode =
					RandomTestUtil.randomString();
			}
		};
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

	protected MasterPageResource masterPageResource;
	protected ImportTaskResource importTaskResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected MasterPageResource permissionsMasterPageResource;
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
		LogFactoryUtil.getLog(BaseMasterPageResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.admin.site.resource.v1_0.MasterPageResource
		_masterPageResource;

}