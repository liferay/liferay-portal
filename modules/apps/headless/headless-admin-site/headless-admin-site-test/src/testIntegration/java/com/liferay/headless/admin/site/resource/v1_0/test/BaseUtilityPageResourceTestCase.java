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
import com.liferay.headless.admin.site.client.dto.v1_0.UtilityPage;
import com.liferay.headless.admin.site.client.http.HttpInvoker;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.pagination.Pagination;
import com.liferay.headless.admin.site.client.permission.Permission;
import com.liferay.headless.admin.site.client.resource.v1_0.UtilityPageResource;
import com.liferay.headless.admin.site.client.serdes.v1_0.UtilityPageSerDes;
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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;
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
public abstract class BaseUtilityPageResourceTestCase {

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

		_utilityPageResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		utilityPageResource = UtilityPageResource.builder(
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

		UtilityPage utilityPage1 = randomUtilityPage();

		String json = objectMapper.writeValueAsString(utilityPage1);

		UtilityPage utilityPage2 = UtilityPageSerDes.toDTO(json);

		Assert.assertTrue(equals(utilityPage1, utilityPage2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		UtilityPage utilityPage = randomUtilityPage();

		String json1 = objectMapper.writeValueAsString(utilityPage);
		String json2 = UtilityPageSerDes.toJSON(utilityPage);

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

		UtilityPage utilityPage = randomUtilityPage();

		utilityPage.setCreatorExternalReferenceCode(regex);
		utilityPage.setExternalReferenceCode(regex);
		utilityPage.setName(regex);
		utilityPage.setUuid(regex);

		String json = UtilityPageSerDes.toJSON(utilityPage);

		Assert.assertFalse(json.contains(regex));

		utilityPage = UtilityPageSerDes.toDTO(json);

		Assert.assertEquals(
			regex, utilityPage.getCreatorExternalReferenceCode());
		Assert.assertEquals(regex, utilityPage.getExternalReferenceCode());
		Assert.assertEquals(regex, utilityPage.getName());
		Assert.assertEquals(regex, utilityPage.getUuid());
	}

	@Test
	public void testDeleteSiteSiteByExternalReferenceCodeUtilityPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UtilityPage utilityPage =
			testDeleteSiteSiteByExternalReferenceCodeUtilityPage_addUtilityPage();

		assertHttpResponseStatusCode(
			204,
			utilityPageResource.
				deleteSiteSiteByExternalReferenceCodeUtilityPageHttpResponse(
					testDeleteSiteSiteByExternalReferenceCodeUtilityPage_getSiteExternalReferenceCode(),
					utilityPage.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			utilityPageResource.
				getSiteSiteByExternalReferenceCodeUtilityPageHttpResponse(
					testDeleteSiteSiteByExternalReferenceCodeUtilityPage_getSiteExternalReferenceCode(),
					utilityPage.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			utilityPageResource.
				getSiteSiteByExternalReferenceCodeUtilityPageHttpResponse(
					testDeleteSiteSiteByExternalReferenceCodeUtilityPage_getSiteExternalReferenceCode(),
					"-"));
	}

	protected UtilityPage
			testDeleteSiteSiteByExternalReferenceCodeUtilityPage_addUtilityPage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteSiteSiteByExternalReferenceCodeUtilityPage_getSiteExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeUtilityPage()
		throws Exception {

		UtilityPage postUtilityPage =
			testGetSiteSiteByExternalReferenceCodeUtilityPage_addUtilityPage();

		UtilityPage getUtilityPage =
			utilityPageResource.getSiteSiteByExternalReferenceCodeUtilityPage(
				testGetSiteSiteByExternalReferenceCodeUtilityPage_getSiteExternalReferenceCode(),
				postUtilityPage.getExternalReferenceCode());

		assertEquals(postUtilityPage, getUtilityPage);
		assertValid(getUtilityPage);
	}

	protected UtilityPage
			testGetSiteSiteByExternalReferenceCodeUtilityPage_addUtilityPage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteSiteByExternalReferenceCodeUtilityPage_getSiteExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteSiteByExternalReferenceCodeUtilityPage()
		throws Exception {

		UtilityPage utilityPage =
			testGraphQLGetSiteSiteByExternalReferenceCodeUtilityPage_addUtilityPage();

		// No namespace

		Assert.assertTrue(
			equals(
				utilityPage,
				UtilityPageSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"siteByExternalReferenceCodeUtilityPage",
								new HashMap<String, Object>() {
									{
										put(
											"siteExternalReferenceCode",
											"\"" +
												testGraphQLGetSiteSiteByExternalReferenceCodeUtilityPage_getSiteExternalReferenceCode() +
													"\"");
										put(
											"utilityPageExternalReferenceCode",
											"\"" +
												utilityPage.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/siteByExternalReferenceCodeUtilityPage"))));

		// Using the namespace headlessAdminSite_v1_0

		Assert.assertTrue(
			equals(
				utilityPage,
				UtilityPageSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminSite_v1_0",
								new GraphQLField(
									"siteByExternalReferenceCodeUtilityPage",
									new HashMap<String, Object>() {
										{
											put(
												"siteExternalReferenceCode",
												"\"" +
													testGraphQLGetSiteSiteByExternalReferenceCodeUtilityPage_getSiteExternalReferenceCode() +
														"\"");
											put(
												"utilityPageExternalReferenceCode",
												"\"" +
													utilityPage.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessAdminSite_v1_0",
						"Object/siteByExternalReferenceCodeUtilityPage"))));
	}

	protected String
			testGraphQLGetSiteSiteByExternalReferenceCodeUtilityPage_getSiteExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteSiteByExternalReferenceCodeUtilityPageNotFound()
		throws Exception {

		String irrelevantUtilityPageExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"siteByExternalReferenceCodeUtilityPage",
						new HashMap<String, Object>() {
							{
								put(
									"siteExternalReferenceCode",
									"\"" +
										irrelevantGroup.
											getExternalReferenceCode() + "\"");
								put(
									"utilityPageExternalReferenceCode",
									irrelevantUtilityPageExternalReferenceCode);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessAdminSite_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminSite_v1_0",
						new GraphQLField(
							"siteByExternalReferenceCodeUtilityPage",
							new HashMap<String, Object>() {
								{
									put(
										"siteExternalReferenceCode",
										"\"" +
											irrelevantGroup.
												getExternalReferenceCode() +
													"\"");
									put(
										"utilityPageExternalReferenceCode",
										irrelevantUtilityPageExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected UtilityPage
			testGraphQLGetSiteSiteByExternalReferenceCodeUtilityPage_addUtilityPage()
		throws Exception {

		return testGraphQLUtilityPage_addUtilityPage();
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeUtilityPagesPage()
		throws Exception {

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_getSiteExternalReferenceCode();
		String irrelevantSiteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_getIrrelevantSiteExternalReferenceCode();

		Page<UtilityPage> page =
			utilityPageResource.
				getSiteSiteByExternalReferenceCodeUtilityPagesPage(
					siteExternalReferenceCode, null, null, null,
					Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteExternalReferenceCode != null) {
			UtilityPage irrelevantUtilityPage =
				testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_addUtilityPage(
					irrelevantSiteExternalReferenceCode,
					randomIrrelevantUtilityPage());

			page =
				utilityPageResource.
					getSiteSiteByExternalReferenceCodeUtilityPagesPage(
						irrelevantSiteExternalReferenceCode, null, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantUtilityPage, (List<UtilityPage>)page.getItems());
			assertValid(
				page,
				testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_getExpectedActions(
					irrelevantSiteExternalReferenceCode));
		}

		UtilityPage utilityPage1 =
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_addUtilityPage(
				siteExternalReferenceCode, randomUtilityPage());

		UtilityPage utilityPage2 =
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_addUtilityPage(
				siteExternalReferenceCode, randomUtilityPage());

		page =
			utilityPageResource.
				getSiteSiteByExternalReferenceCodeUtilityPagesPage(
					siteExternalReferenceCode, null, null, null,
					Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(utilityPage1, (List<UtilityPage>)page.getItems());
		assertContains(utilityPage2, (List<UtilityPage>)page.getItems());
		assertValid(
			page,
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_getExpectedActions(
				siteExternalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_getExpectedActions(
				String siteExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_getSiteExternalReferenceCode();

		UtilityPage utilityPage1 = randomUtilityPage();

		utilityPage1 =
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_addUtilityPage(
				siteExternalReferenceCode, utilityPage1);

		for (EntityField entityField : entityFields) {
			Page<UtilityPage> page =
				utilityPageResource.
					getSiteSiteByExternalReferenceCodeUtilityPagesPage(
						siteExternalReferenceCode, null, null,
						getFilterString(entityField, "between", utilityPage1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(utilityPage1),
				(List<UtilityPage>)page.getItems());
		}
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithFilterStringContains()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithFilterStringEquals()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_getSiteExternalReferenceCode();

		UtilityPage utilityPage1 =
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_addUtilityPage(
				siteExternalReferenceCode, randomUtilityPage());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UtilityPage utilityPage2 =
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_addUtilityPage(
				siteExternalReferenceCode, randomUtilityPage());

		for (EntityField entityField : entityFields) {
			Page<UtilityPage> page =
				utilityPageResource.
					getSiteSiteByExternalReferenceCodeUtilityPagesPage(
						siteExternalReferenceCode, null, null,
						getFilterString(entityField, operator, utilityPage1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(utilityPage1),
				(List<UtilityPage>)page.getItems());
		}
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithPagination()
		throws Exception {

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_getSiteExternalReferenceCode();

		Page<UtilityPage> utilityPagesPage =
			utilityPageResource.
				getSiteSiteByExternalReferenceCodeUtilityPagesPage(
					siteExternalReferenceCode, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			utilityPagesPage.getTotalCount());

		UtilityPage utilityPage1 =
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_addUtilityPage(
				siteExternalReferenceCode, randomUtilityPage());

		UtilityPage utilityPage2 =
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_addUtilityPage(
				siteExternalReferenceCode, randomUtilityPage());

		UtilityPage utilityPage3 =
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_addUtilityPage(
				siteExternalReferenceCode, randomUtilityPage());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<UtilityPage> page1 =
				utilityPageResource.
					getSiteSiteByExternalReferenceCodeUtilityPagesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(utilityPage1, (List<UtilityPage>)page1.getItems());

			Page<UtilityPage> page2 =
				utilityPageResource.
					getSiteSiteByExternalReferenceCodeUtilityPagesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(utilityPage2, (List<UtilityPage>)page2.getItems());

			Page<UtilityPage> page3 =
				utilityPageResource.
					getSiteSiteByExternalReferenceCodeUtilityPagesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(utilityPage3, (List<UtilityPage>)page3.getItems());
		}
		else {
			Page<UtilityPage> page1 =
				utilityPageResource.
					getSiteSiteByExternalReferenceCodeUtilityPagesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<UtilityPage> utilityPages1 =
				(List<UtilityPage>)page1.getItems();

			Assert.assertEquals(
				utilityPages1.toString(), totalCount + 2, utilityPages1.size());

			Page<UtilityPage> page2 =
				utilityPageResource.
					getSiteSiteByExternalReferenceCodeUtilityPagesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<UtilityPage> utilityPages2 =
				(List<UtilityPage>)page2.getItems();

			Assert.assertEquals(
				utilityPages2.toString(), 1, utilityPages2.size());

			Page<UtilityPage> page3 =
				utilityPageResource.
					getSiteSiteByExternalReferenceCodeUtilityPagesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(utilityPage1, (List<UtilityPage>)page3.getItems());
			assertContains(utilityPage2, (List<UtilityPage>)page3.getItems());
			assertContains(utilityPage3, (List<UtilityPage>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithSortDateTime()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, utilityPage1, utilityPage2) -> {
				BeanTestUtil.setProperty(
					utilityPage1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithSortDouble()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, utilityPage1, utilityPage2) -> {
				BeanTestUtil.setProperty(
					utilityPage1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					utilityPage2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithSortInteger()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, utilityPage1, utilityPage2) -> {
				BeanTestUtil.setProperty(
					utilityPage1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					utilityPage2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithSortString()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithSort(
			EntityField.Type.STRING,
			(entityField, utilityPage1, utilityPage2) -> {
				Class<?> clazz = utilityPage1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						utilityPage1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						utilityPage2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						utilityPage1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						utilityPage2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						utilityPage1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						utilityPage2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, UtilityPage, UtilityPage, Exception>
						unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_getSiteExternalReferenceCode();

		UtilityPage utilityPage1 = randomUtilityPage();
		UtilityPage utilityPage2 = randomUtilityPage();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, utilityPage1, utilityPage2);
		}

		utilityPage1 =
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_addUtilityPage(
				siteExternalReferenceCode, utilityPage1);

		utilityPage2 =
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_addUtilityPage(
				siteExternalReferenceCode, utilityPage2);

		Page<UtilityPage> page =
			utilityPageResource.
				getSiteSiteByExternalReferenceCodeUtilityPagesPage(
					siteExternalReferenceCode, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<UtilityPage> ascPage =
				utilityPageResource.
					getSiteSiteByExternalReferenceCodeUtilityPagesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(utilityPage1, (List<UtilityPage>)ascPage.getItems());
			assertContains(utilityPage2, (List<UtilityPage>)ascPage.getItems());

			Page<UtilityPage> descPage =
				utilityPageResource.
					getSiteSiteByExternalReferenceCodeUtilityPagesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				utilityPage2, (List<UtilityPage>)descPage.getItems());
			assertContains(
				utilityPage1, (List<UtilityPage>)descPage.getItems());
		}
	}

	protected UtilityPage
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_addUtilityPage(
				String siteExternalReferenceCode, UtilityPage utilityPage)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	@Test
	public void testGetSiteUtilityPagePermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		UtilityPage postUtilityPage =
			testGetSiteUtilityPagePermissionsPage_addUtilityPage();

		Page<Permission> page =
			utilityPageResource.getSiteUtilityPagePermissionsPage(
				testGroup.getExternalReferenceCode(),
				postUtilityPage.getExternalReferenceCode(),
				RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected UtilityPage testGetSiteUtilityPagePermissionsPage_addUtilityPage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchSiteSiteByExternalReferenceCodeUtilityPage()
		throws Exception {

		UtilityPage postUtilityPage =
			testPatchSiteSiteByExternalReferenceCodeUtilityPage_addUtilityPage();

		UtilityPage randomPatchUtilityPage = randomPatchUtilityPage();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UtilityPage patchUtilityPage =
			utilityPageResource.patchSiteSiteByExternalReferenceCodeUtilityPage(
				null, postUtilityPage.getExternalReferenceCode(),
				randomPatchUtilityPage);

		UtilityPage expectedPatchUtilityPage = postUtilityPage.clone();

		BeanTestUtil.copyProperties(
			randomPatchUtilityPage, expectedPatchUtilityPage);

		UtilityPage getUtilityPage =
			utilityPageResource.getSiteSiteByExternalReferenceCodeUtilityPage(
				null, patchUtilityPage.getExternalReferenceCode());

		assertEquals(expectedPatchUtilityPage, getUtilityPage);
		assertValid(getUtilityPage);
	}

	protected UtilityPage
			testPatchSiteSiteByExternalReferenceCodeUtilityPage_addUtilityPage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSiteSiteByExternalReferenceCodeUtilityPage()
		throws Exception {

		UtilityPage randomUtilityPage = randomUtilityPage();

		UtilityPage postUtilityPage =
			testPostSiteSiteByExternalReferenceCodeUtilityPage_addUtilityPage(
				randomUtilityPage);

		assertEquals(randomUtilityPage, postUtilityPage);
		assertValid(postUtilityPage);
	}

	protected UtilityPage
			testPostSiteSiteByExternalReferenceCodeUtilityPage_addUtilityPage(
				UtilityPage utilityPage)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutSiteSiteByExternalReferenceCodeUtilityPage()
		throws Exception {

		UtilityPage postUtilityPage =
			testPutSiteSiteByExternalReferenceCodeUtilityPage_addUtilityPage();

		UtilityPage randomUtilityPage = randomUtilityPage();

		UtilityPage putUtilityPage =
			utilityPageResource.putSiteSiteByExternalReferenceCodeUtilityPage(
				testPutSiteSiteByExternalReferenceCodeUtilityPage_getSiteExternalReferenceCode(),
				postUtilityPage.getExternalReferenceCode(), randomUtilityPage);

		assertEquals(randomUtilityPage, putUtilityPage);
		assertValid(putUtilityPage);

		UtilityPage getUtilityPage =
			utilityPageResource.getSiteSiteByExternalReferenceCodeUtilityPage(
				testPutSiteSiteByExternalReferenceCodeUtilityPage_getSiteExternalReferenceCode(),
				putUtilityPage.getExternalReferenceCode());

		assertEquals(randomUtilityPage, getUtilityPage);
		assertValid(getUtilityPage);
	}

	protected UtilityPage
			testPutSiteSiteByExternalReferenceCodeUtilityPage_addUtilityPage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testPutSiteSiteByExternalReferenceCodeUtilityPage_getSiteExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutSiteUtilityPagePermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		UtilityPage utilityPage =
			testPutSiteUtilityPagePermissionsPage_addUtilityPage();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			utilityPageResource.putSiteUtilityPagePermissionsPageHttpResponse(
				testGroup.getExternalReferenceCode(), null,
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
			utilityPageResource.putSiteUtilityPagePermissionsPageHttpResponse(
				testGroup.getExternalReferenceCode(), null,
				new Permission[] {
					new Permission() {
						{
							setActionIds(new String[] {"-"});
							setRoleName("-");
						}
					}
				}));
	}

	protected UtilityPage testPutSiteUtilityPagePermissionsPage_addUtilityPage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	@Test
	public void testPostSiteSiteByExternalReferenceCodeUtilityPagePageSpecification()
		throws Exception {

		Assert.assertTrue(true);
	}

	protected UtilityPage testGraphQLUtilityPage_addUtilityPage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		UtilityPage utilityPage, List<UtilityPage> utilityPages) {

		boolean contains = false;

		for (UtilityPage item : utilityPages) {
			if (equals(utilityPage, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			utilityPages + " does not contain " + utilityPage, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		UtilityPage utilityPage1, UtilityPage utilityPage2) {

		Assert.assertTrue(
			utilityPage1 + " does not equal " + utilityPage2,
			equals(utilityPage1, utilityPage2));
	}

	protected void assertEquals(
		List<UtilityPage> utilityPages1, List<UtilityPage> utilityPages2) {

		Assert.assertEquals(utilityPages1.size(), utilityPages2.size());

		for (int i = 0; i < utilityPages1.size(); i++) {
			UtilityPage utilityPage1 = utilityPages1.get(i);
			UtilityPage utilityPage2 = utilityPages2.get(i);

			assertEquals(utilityPage1, utilityPage2);
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
		List<UtilityPage> utilityPages1, List<UtilityPage> utilityPages2) {

		Assert.assertEquals(utilityPages1.size(), utilityPages2.size());

		for (UtilityPage utilityPage1 : utilityPages1) {
			boolean contains = false;

			for (UtilityPage utilityPage2 : utilityPages2) {
				if (equals(utilityPage1, utilityPage2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				utilityPages2 + " does not contain " + utilityPage1, contains);
		}
	}

	protected void assertValid(UtilityPage utilityPage) throws Exception {
		boolean valid = true;

		if (utilityPage.getDateCreated() == null) {
			valid = false;
		}

		if (utilityPage.getDateModified() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (utilityPage.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"creatorExternalReferenceCode",
					additionalAssertFieldName)) {

				if (utilityPage.getCreatorExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("datePublished", additionalAssertFieldName)) {
				if (utilityPage.getDatePublished() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (utilityPage.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"friendlyUrlHistory", additionalAssertFieldName)) {

				if (utilityPage.getFriendlyUrlHistory() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"friendlyUrlPath_i18n", additionalAssertFieldName)) {

				if (utilityPage.getFriendlyUrlPath_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("markedAsDefault", additionalAssertFieldName)) {
				if (utilityPage.getMarkedAsDefault() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (utilityPage.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"pageSpecifications", additionalAssertFieldName)) {

				if (utilityPage.getPageSpecifications() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("thumbnail", additionalAssertFieldName)) {
				if (utilityPage.getThumbnail() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (utilityPage.getType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"utilityPageSettings", additionalAssertFieldName)) {

				if (utilityPage.getUtilityPageSettings() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("uuid", additionalAssertFieldName)) {
				if (utilityPage.getUuid() == null) {
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

	protected void assertValid(Page<UtilityPage> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<UtilityPage> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<UtilityPage> utilityPages = page.getItems();

		int size = utilityPages.size();

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

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.admin.site.dto.v1_0.UtilityPage.
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
		UtilityPage utilityPage1, UtilityPage utilityPage2) {

		if (utilityPage1 == utilityPage2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						utilityPage1.getCreator(), utilityPage2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"creatorExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						utilityPage1.getCreatorExternalReferenceCode(),
						utilityPage2.getCreatorExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						utilityPage1.getDateCreated(),
						utilityPage2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						utilityPage1.getDateModified(),
						utilityPage2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("datePublished", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						utilityPage1.getDatePublished(),
						utilityPage2.getDatePublished())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						utilityPage1.getExternalReferenceCode(),
						utilityPage2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"friendlyUrlHistory", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						utilityPage1.getFriendlyUrlHistory(),
						utilityPage2.getFriendlyUrlHistory())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"friendlyUrlPath_i18n", additionalAssertFieldName)) {

				if (!equals(
						(Map)utilityPage1.getFriendlyUrlPath_i18n(),
						(Map)utilityPage2.getFriendlyUrlPath_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("markedAsDefault", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						utilityPage1.getMarkedAsDefault(),
						utilityPage2.getMarkedAsDefault())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						utilityPage1.getName(), utilityPage2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"pageSpecifications", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						utilityPage1.getPageSpecifications(),
						utilityPage2.getPageSpecifications())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("thumbnail", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						utilityPage1.getThumbnail(),
						utilityPage2.getThumbnail())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						utilityPage1.getType(), utilityPage2.getType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"utilityPageSettings", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						utilityPage1.getUtilityPageSettings(),
						utilityPage2.getUtilityPageSettings())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("uuid", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						utilityPage1.getUuid(), utilityPage2.getUuid())) {

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

		if (!(_utilityPageResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_utilityPageResource;

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
		EntityField entityField, String operator, UtilityPage utilityPage) {

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

		if (entityFieldName.equals("creatorExternalReferenceCode")) {
			Object object = utilityPage.getCreatorExternalReferenceCode();

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
				Date date = utilityPage.getDateCreated();

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

				sb.append(_format.format(utilityPage.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = utilityPage.getDateModified();

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

				sb.append(_format.format(utilityPage.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("datePublished")) {
			if (operator.equals("between")) {
				Date date = utilityPage.getDatePublished();

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

				sb.append(_format.format(utilityPage.getDatePublished()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = utilityPage.getExternalReferenceCode();

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

		if (entityFieldName.equals("friendlyUrlHistory")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("friendlyUrlPath_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("markedAsDefault")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("name")) {
			Object object = utilityPage.getName();

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

		if (entityFieldName.equals("thumbnail")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("type")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("utilityPageSettings")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("uuid")) {
			Object object = utilityPage.getUuid();

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

	protected UtilityPage randomUtilityPage() throws Exception {
		return new UtilityPage() {
			{
				creatorExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				datePublished = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				markedAsDefault = RandomTestUtil.randomBoolean();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				uuid = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected UtilityPage randomIrrelevantUtilityPage() throws Exception {
		UtilityPage randomIrrelevantUtilityPage = randomUtilityPage();

		return randomIrrelevantUtilityPage;
	}

	protected UtilityPage randomPatchUtilityPage() throws Exception {
		return randomUtilityPage();
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

	protected UtilityPageResource utilityPageResource;
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
		LogFactoryUtil.getLog(BaseUtilityPageResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.admin.site.resource.v1_0.UtilityPageResource
		_utilityPageResource;

}