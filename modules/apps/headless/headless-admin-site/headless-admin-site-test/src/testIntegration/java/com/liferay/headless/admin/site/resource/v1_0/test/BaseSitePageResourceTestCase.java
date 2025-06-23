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
import com.liferay.headless.admin.site.client.dto.v1_0.SitePage;
import com.liferay.headless.admin.site.client.http.HttpInvoker;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.pagination.Pagination;
import com.liferay.headless.admin.site.client.permission.Permission;
import com.liferay.headless.admin.site.client.resource.v1_0.SitePageResource;
import com.liferay.headless.admin.site.client.serdes.v1_0.SitePageSerDes;
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
public abstract class BaseSitePageResourceTestCase {

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

		_sitePageResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		sitePageResource = SitePageResource.builder(
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

		SitePage sitePage1 = randomSitePage();

		String json = objectMapper.writeValueAsString(sitePage1);

		SitePage sitePage2 = SitePageSerDes.toDTO(json);

		Assert.assertTrue(equals(sitePage1, sitePage2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		SitePage sitePage = randomSitePage();

		String json1 = objectMapper.writeValueAsString(sitePage);
		String json2 = SitePageSerDes.toJSON(sitePage);

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

		SitePage sitePage = randomSitePage();

		sitePage.setCreatorExternalReferenceCode(regex);
		sitePage.setExternalReferenceCode(regex);
		sitePage.setParentSitePageExternalReferenceCode(regex);
		sitePage.setUuid(regex);

		String json = SitePageSerDes.toJSON(sitePage);

		Assert.assertFalse(json.contains(regex));

		sitePage = SitePageSerDes.toDTO(json);

		Assert.assertEquals(regex, sitePage.getCreatorExternalReferenceCode());
		Assert.assertEquals(regex, sitePage.getExternalReferenceCode());
		Assert.assertEquals(
			regex, sitePage.getParentSitePageExternalReferenceCode());
		Assert.assertEquals(regex, sitePage.getUuid());
	}

	@Test
	public void testDeleteSiteSiteByExternalReferenceCodeSitePage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		SitePage sitePage =
			testDeleteSiteSiteByExternalReferenceCodeSitePage_addSitePage();

		assertHttpResponseStatusCode(
			204,
			sitePageResource.
				deleteSiteSiteByExternalReferenceCodeSitePageHttpResponse(
					testDeleteSiteSiteByExternalReferenceCodeSitePage_getSiteExternalReferenceCode(),
					sitePage.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			sitePageResource.
				getSiteSiteByExternalReferenceCodeSitePageHttpResponse(
					testDeleteSiteSiteByExternalReferenceCodeSitePage_getSiteExternalReferenceCode(),
					sitePage.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			sitePageResource.
				getSiteSiteByExternalReferenceCodeSitePageHttpResponse(
					testDeleteSiteSiteByExternalReferenceCodeSitePage_getSiteExternalReferenceCode(),
					"-"));
	}

	protected SitePage
			testDeleteSiteSiteByExternalReferenceCodeSitePage_addSitePage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteSiteSiteByExternalReferenceCodeSitePage_getSiteExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeSitePage()
		throws Exception {

		SitePage postSitePage =
			testGetSiteSiteByExternalReferenceCodeSitePage_addSitePage();

		SitePage getSitePage =
			sitePageResource.getSiteSiteByExternalReferenceCodeSitePage(
				testGetSiteSiteByExternalReferenceCodeSitePage_getSiteExternalReferenceCode(),
				postSitePage.getExternalReferenceCode());

		assertEquals(postSitePage, getSitePage);
		assertValid(getSitePage);
	}

	protected SitePage
			testGetSiteSiteByExternalReferenceCodeSitePage_addSitePage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteSiteByExternalReferenceCodeSitePage_getSiteExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteSiteByExternalReferenceCodeSitePage()
		throws Exception {

		SitePage sitePage =
			testGraphQLGetSiteSiteByExternalReferenceCodeSitePage_addSitePage();

		// No namespace

		Assert.assertTrue(
			equals(
				sitePage,
				SitePageSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"siteByExternalReferenceCodeSitePage",
								new HashMap<String, Object>() {
									{
										put(
											"siteExternalReferenceCode",
											"\"" +
												testGraphQLGetSiteSiteByExternalReferenceCodeSitePage_getSiteExternalReferenceCode() +
													"\"");
										put(
											"sitePageExternalReferenceCode",
											"\"" +
												sitePage.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/siteByExternalReferenceCodeSitePage"))));

		// Using the namespace headlessAdminSite_v1_0

		Assert.assertTrue(
			equals(
				sitePage,
				SitePageSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminSite_v1_0",
								new GraphQLField(
									"siteByExternalReferenceCodeSitePage",
									new HashMap<String, Object>() {
										{
											put(
												"siteExternalReferenceCode",
												"\"" +
													testGraphQLGetSiteSiteByExternalReferenceCodeSitePage_getSiteExternalReferenceCode() +
														"\"");
											put(
												"sitePageExternalReferenceCode",
												"\"" +
													sitePage.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessAdminSite_v1_0",
						"Object/siteByExternalReferenceCodeSitePage"))));
	}

	protected String
			testGraphQLGetSiteSiteByExternalReferenceCodeSitePage_getSiteExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteSiteByExternalReferenceCodeSitePageNotFound()
		throws Exception {

		String irrelevantSitePageExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"siteByExternalReferenceCodeSitePage",
						new HashMap<String, Object>() {
							{
								put(
									"siteExternalReferenceCode",
									"\"" +
										irrelevantGroup.
											getExternalReferenceCode() + "\"");
								put(
									"sitePageExternalReferenceCode",
									irrelevantSitePageExternalReferenceCode);
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
							"siteByExternalReferenceCodeSitePage",
							new HashMap<String, Object>() {
								{
									put(
										"siteExternalReferenceCode",
										"\"" +
											irrelevantGroup.
												getExternalReferenceCode() +
													"\"");
									put(
										"sitePageExternalReferenceCode",
										irrelevantSitePageExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected SitePage
			testGraphQLGetSiteSiteByExternalReferenceCodeSitePage_addSitePage()
		throws Exception {

		return testGraphQLSitePage_addSitePage();
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeSitePagesPage()
		throws Exception {

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeSitePagesPage_getSiteExternalReferenceCode();
		String irrelevantSiteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeSitePagesPage_getIrrelevantSiteExternalReferenceCode();

		Page<SitePage> page =
			sitePageResource.getSiteSiteByExternalReferenceCodeSitePagesPage(
				siteExternalReferenceCode, null, null, null,
				Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteExternalReferenceCode != null) {
			SitePage irrelevantSitePage =
				testGetSiteSiteByExternalReferenceCodeSitePagesPage_addSitePage(
					irrelevantSiteExternalReferenceCode,
					randomIrrelevantSitePage());

			page =
				sitePageResource.
					getSiteSiteByExternalReferenceCodeSitePagesPage(
						irrelevantSiteExternalReferenceCode, null, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantSitePage, (List<SitePage>)page.getItems());
			assertValid(
				page,
				testGetSiteSiteByExternalReferenceCodeSitePagesPage_getExpectedActions(
					irrelevantSiteExternalReferenceCode));
		}

		SitePage sitePage1 =
			testGetSiteSiteByExternalReferenceCodeSitePagesPage_addSitePage(
				siteExternalReferenceCode, randomSitePage());

		SitePage sitePage2 =
			testGetSiteSiteByExternalReferenceCodeSitePagesPage_addSitePage(
				siteExternalReferenceCode, randomSitePage());

		page = sitePageResource.getSiteSiteByExternalReferenceCodeSitePagesPage(
			siteExternalReferenceCode, null, null, null, Pagination.of(1, 10),
			null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(sitePage1, (List<SitePage>)page.getItems());
		assertContains(sitePage2, (List<SitePage>)page.getItems());
		assertValid(
			page,
			testGetSiteSiteByExternalReferenceCodeSitePagesPage_getExpectedActions(
				siteExternalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetSiteSiteByExternalReferenceCodeSitePagesPage_getExpectedActions(
				String siteExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeSitePagesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeSitePagesPage_getSiteExternalReferenceCode();

		SitePage sitePage1 = randomSitePage();

		sitePage1 =
			testGetSiteSiteByExternalReferenceCodeSitePagesPage_addSitePage(
				siteExternalReferenceCode, sitePage1);

		for (EntityField entityField : entityFields) {
			Page<SitePage> page =
				sitePageResource.
					getSiteSiteByExternalReferenceCodeSitePagesPage(
						siteExternalReferenceCode, null, null,
						getFilterString(entityField, "between", sitePage1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(sitePage1),
				(List<SitePage>)page.getItems());
		}
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeSitePagesPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeSitePagesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeSitePagesPageWithFilterStringContains()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeSitePagesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeSitePagesPageWithFilterStringEquals()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeSitePagesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeSitePagesPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeSitePagesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetSiteSiteByExternalReferenceCodeSitePagesPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeSitePagesPage_getSiteExternalReferenceCode();

		SitePage sitePage1 =
			testGetSiteSiteByExternalReferenceCodeSitePagesPage_addSitePage(
				siteExternalReferenceCode, randomSitePage());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		SitePage sitePage2 =
			testGetSiteSiteByExternalReferenceCodeSitePagesPage_addSitePage(
				siteExternalReferenceCode, randomSitePage());

		for (EntityField entityField : entityFields) {
			Page<SitePage> page =
				sitePageResource.
					getSiteSiteByExternalReferenceCodeSitePagesPage(
						siteExternalReferenceCode, null, null,
						getFilterString(entityField, operator, sitePage1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(sitePage1),
				(List<SitePage>)page.getItems());
		}
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeSitePagesPageWithPagination()
		throws Exception {

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeSitePagesPage_getSiteExternalReferenceCode();

		Page<SitePage> sitePagesPage =
			sitePageResource.getSiteSiteByExternalReferenceCodeSitePagesPage(
				siteExternalReferenceCode, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(sitePagesPage.getTotalCount());

		SitePage sitePage1 =
			testGetSiteSiteByExternalReferenceCodeSitePagesPage_addSitePage(
				siteExternalReferenceCode, randomSitePage());

		SitePage sitePage2 =
			testGetSiteSiteByExternalReferenceCodeSitePagesPage_addSitePage(
				siteExternalReferenceCode, randomSitePage());

		SitePage sitePage3 =
			testGetSiteSiteByExternalReferenceCodeSitePagesPage_addSitePage(
				siteExternalReferenceCode, randomSitePage());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<SitePage> page1 =
				sitePageResource.
					getSiteSiteByExternalReferenceCodeSitePagesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(sitePage1, (List<SitePage>)page1.getItems());

			Page<SitePage> page2 =
				sitePageResource.
					getSiteSiteByExternalReferenceCodeSitePagesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(sitePage2, (List<SitePage>)page2.getItems());

			Page<SitePage> page3 =
				sitePageResource.
					getSiteSiteByExternalReferenceCodeSitePagesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(sitePage3, (List<SitePage>)page3.getItems());
		}
		else {
			Page<SitePage> page1 =
				sitePageResource.
					getSiteSiteByExternalReferenceCodeSitePagesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<SitePage> sitePages1 = (List<SitePage>)page1.getItems();

			Assert.assertEquals(
				sitePages1.toString(), totalCount + 2, sitePages1.size());

			Page<SitePage> page2 =
				sitePageResource.
					getSiteSiteByExternalReferenceCodeSitePagesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<SitePage> sitePages2 = (List<SitePage>)page2.getItems();

			Assert.assertEquals(sitePages2.toString(), 1, sitePages2.size());

			Page<SitePage> page3 =
				sitePageResource.
					getSiteSiteByExternalReferenceCodeSitePagesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(sitePage1, (List<SitePage>)page3.getItems());
			assertContains(sitePage2, (List<SitePage>)page3.getItems());
			assertContains(sitePage3, (List<SitePage>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeSitePagesPageWithSortDateTime()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeSitePagesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, sitePage1, sitePage2) -> {
				BeanTestUtil.setProperty(
					sitePage1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeSitePagesPageWithSortDouble()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeSitePagesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, sitePage1, sitePage2) -> {
				BeanTestUtil.setProperty(sitePage1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(sitePage2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeSitePagesPageWithSortInteger()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeSitePagesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, sitePage1, sitePage2) -> {
				BeanTestUtil.setProperty(sitePage1, entityField.getName(), 0);
				BeanTestUtil.setProperty(sitePage2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeSitePagesPageWithSortString()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeSitePagesPageWithSort(
			EntityField.Type.STRING,
			(entityField, sitePage1, sitePage2) -> {
				Class<?> clazz = sitePage1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						sitePage1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						sitePage2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						sitePage1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						sitePage2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						sitePage1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						sitePage2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSiteSiteByExternalReferenceCodeSitePagesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, SitePage, SitePage, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeSitePagesPage_getSiteExternalReferenceCode();

		SitePage sitePage1 = randomSitePage();
		SitePage sitePage2 = randomSitePage();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, sitePage1, sitePage2);
		}

		sitePage1 =
			testGetSiteSiteByExternalReferenceCodeSitePagesPage_addSitePage(
				siteExternalReferenceCode, sitePage1);

		sitePage2 =
			testGetSiteSiteByExternalReferenceCodeSitePagesPage_addSitePage(
				siteExternalReferenceCode, sitePage2);

		Page<SitePage> page =
			sitePageResource.getSiteSiteByExternalReferenceCodeSitePagesPage(
				siteExternalReferenceCode, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<SitePage> ascPage =
				sitePageResource.
					getSiteSiteByExternalReferenceCodeSitePagesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(sitePage1, (List<SitePage>)ascPage.getItems());
			assertContains(sitePage2, (List<SitePage>)ascPage.getItems());

			Page<SitePage> descPage =
				sitePageResource.
					getSiteSiteByExternalReferenceCodeSitePagesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(sitePage2, (List<SitePage>)descPage.getItems());
			assertContains(sitePage1, (List<SitePage>)descPage.getItems());
		}
	}

	protected SitePage
			testGetSiteSiteByExternalReferenceCodeSitePagesPage_addSitePage(
				String siteExternalReferenceCode, SitePage sitePage)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteSiteByExternalReferenceCodeSitePagesPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteSiteByExternalReferenceCodeSitePagesPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	@Test
	public void testGetSiteSitePagePermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		SitePage postSitePage =
			testGetSiteSitePagePermissionsPage_addSitePage();

		Page<Permission> page = sitePageResource.getSiteSitePagePermissionsPage(
			testGroup.getExternalReferenceCode(),
			postSitePage.getExternalReferenceCode(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected SitePage testGetSiteSitePagePermissionsPage_addSitePage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchSiteSiteByExternalReferenceCodeSitePage()
		throws Exception {

		SitePage postSitePage =
			testPatchSiteSiteByExternalReferenceCodeSitePage_addSitePage();

		SitePage randomPatchSitePage = randomPatchSitePage();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		SitePage patchSitePage =
			sitePageResource.patchSiteSiteByExternalReferenceCodeSitePage(
				null, postSitePage.getExternalReferenceCode(),
				randomPatchSitePage);

		SitePage expectedPatchSitePage = postSitePage.clone();

		BeanTestUtil.copyProperties(randomPatchSitePage, expectedPatchSitePage);

		SitePage getSitePage =
			sitePageResource.getSiteSiteByExternalReferenceCodeSitePage(
				null, patchSitePage.getExternalReferenceCode());

		assertEquals(expectedPatchSitePage, getSitePage);
		assertValid(getSitePage);
	}

	protected SitePage
			testPatchSiteSiteByExternalReferenceCodeSitePage_addSitePage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostByExternalReferenceCodeSitePage() throws Exception {
		SitePage randomSitePage = randomSitePage();

		SitePage postSitePage =
			testPostByExternalReferenceCodeSitePage_addSitePage(randomSitePage);

		assertEquals(randomSitePage, postSitePage);
		assertValid(postSitePage);
	}

	protected SitePage testPostByExternalReferenceCodeSitePage_addSitePage(
			SitePage sitePage)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutSiteSiteByExternalReferenceCodeSitePage()
		throws Exception {

		SitePage postSitePage =
			testPutSiteSiteByExternalReferenceCodeSitePage_addSitePage();

		SitePage randomSitePage = randomSitePage();

		SitePage putSitePage =
			sitePageResource.putSiteSiteByExternalReferenceCodeSitePage(
				testPutSiteSiteByExternalReferenceCodeSitePage_getSiteExternalReferenceCode(),
				postSitePage.getExternalReferenceCode(), randomSitePage);

		assertEquals(randomSitePage, putSitePage);
		assertValid(putSitePage);

		SitePage getSitePage =
			sitePageResource.getSiteSiteByExternalReferenceCodeSitePage(
				testPutSiteSiteByExternalReferenceCodeSitePage_getSiteExternalReferenceCode(),
				putSitePage.getExternalReferenceCode());

		assertEquals(randomSitePage, getSitePage);
		assertValid(getSitePage);
	}

	protected SitePage
			testPutSiteSiteByExternalReferenceCodeSitePage_addSitePage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testPutSiteSiteByExternalReferenceCodeSitePage_getSiteExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutSiteSitePagePermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		SitePage sitePage = testPutSiteSitePagePermissionsPage_addSitePage();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			sitePageResource.putSiteSitePagePermissionsPageHttpResponse(
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
			sitePageResource.putSiteSitePagePermissionsPageHttpResponse(
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

	protected SitePage testPutSiteSitePagePermissionsPage_addSitePage()
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
	public void testPostSiteSiteByExternalReferenceCodeSitePagePageSpecification()
		throws Exception {

		Assert.assertTrue(true);
	}

	protected SitePage testGraphQLSitePage_addSitePage() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(SitePage sitePage, List<SitePage> sitePages) {
		boolean contains = false;

		for (SitePage item : sitePages) {
			if (equals(sitePage, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			sitePages + " does not contain " + sitePage, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(SitePage sitePage1, SitePage sitePage2) {
		Assert.assertTrue(
			sitePage1 + " does not equal " + sitePage2,
			equals(sitePage1, sitePage2));
	}

	protected void assertEquals(
		List<SitePage> sitePages1, List<SitePage> sitePages2) {

		Assert.assertEquals(sitePages1.size(), sitePages2.size());

		for (int i = 0; i < sitePages1.size(); i++) {
			SitePage sitePage1 = sitePages1.get(i);
			SitePage sitePage2 = sitePages2.get(i);

			assertEquals(sitePage1, sitePage2);
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
		List<SitePage> sitePages1, List<SitePage> sitePages2) {

		Assert.assertEquals(sitePages1.size(), sitePages2.size());

		for (SitePage sitePage1 : sitePages1) {
			boolean contains = false;

			for (SitePage sitePage2 : sitePages2) {
				if (equals(sitePage1, sitePage2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				sitePages2 + " does not contain " + sitePage1, contains);
		}
	}

	protected void assertValid(SitePage sitePage) throws Exception {
		boolean valid = true;

		if (sitePage.getDateCreated() == null) {
			valid = false;
		}

		if (sitePage.getDateModified() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"availableLanguages", additionalAssertFieldName)) {

				if (sitePage.getAvailableLanguages() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (sitePage.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"creatorExternalReferenceCode",
					additionalAssertFieldName)) {

				if (sitePage.getCreatorExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (sitePage.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("datePublished", additionalAssertFieldName)) {
				if (sitePage.getDatePublished() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (sitePage.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"friendlyUrlHistory", additionalAssertFieldName)) {

				if (sitePage.getFriendlyUrlHistory() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"friendlyUrlPath_i18n", additionalAssertFieldName)) {

				if (sitePage.getFriendlyUrlPath_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"keywordItemExternalReferences",
					additionalAssertFieldName)) {

				if (sitePage.getKeywordItemExternalReferences() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("keywords", additionalAssertFieldName)) {
				if (sitePage.getKeywords() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (sitePage.getName_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("pageSettings", additionalAssertFieldName)) {
				if (sitePage.getPageSettings() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"pageSpecifications", additionalAssertFieldName)) {

				if (sitePage.getPageSpecifications() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"parentSitePageExternalReferenceCode",
					additionalAssertFieldName)) {

				if (sitePage.getParentSitePageExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategories", additionalAssertFieldName)) {

				if (sitePage.getTaxonomyCategories() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryItemExternalReferences",
					additionalAssertFieldName)) {

				if (sitePage.getTaxonomyCategoryItemExternalReferences() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (sitePage.getType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("uuid", additionalAssertFieldName)) {
				if (sitePage.getUuid() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (sitePage.getViewableBy() == null) {
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

	protected void assertValid(Page<SitePage> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<SitePage> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<SitePage> sitePages = page.getItems();

		int size = sitePages.size();

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
					com.liferay.headless.admin.site.dto.v1_0.SitePage.class)) {

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

	protected boolean equals(SitePage sitePage1, SitePage sitePage2) {
		if (sitePage1 == sitePage2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"availableLanguages", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sitePage1.getAvailableLanguages(),
						sitePage2.getAvailableLanguages())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sitePage1.getCreator(), sitePage2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"creatorExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sitePage1.getCreatorExternalReferenceCode(),
						sitePage2.getCreatorExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sitePage1.getCustomFields(),
						sitePage2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sitePage1.getDateCreated(),
						sitePage2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sitePage1.getDateModified(),
						sitePage2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("datePublished", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sitePage1.getDatePublished(),
						sitePage2.getDatePublished())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sitePage1.getExternalReferenceCode(),
						sitePage2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"friendlyUrlHistory", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sitePage1.getFriendlyUrlHistory(),
						sitePage2.getFriendlyUrlHistory())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"friendlyUrlPath_i18n", additionalAssertFieldName)) {

				if (!equals(
						(Map)sitePage1.getFriendlyUrlPath_i18n(),
						(Map)sitePage2.getFriendlyUrlPath_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"keywordItemExternalReferences",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sitePage1.getKeywordItemExternalReferences(),
						sitePage2.getKeywordItemExternalReferences())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("keywords", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sitePage1.getKeywords(), sitePage2.getKeywords())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)sitePage1.getName_i18n(),
						(Map)sitePage2.getName_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("pageSettings", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sitePage1.getPageSettings(),
						sitePage2.getPageSettings())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"pageSpecifications", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sitePage1.getPageSpecifications(),
						sitePage2.getPageSpecifications())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"parentSitePageExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sitePage1.getParentSitePageExternalReferenceCode(),
						sitePage2.getParentSitePageExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategories", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sitePage1.getTaxonomyCategories(),
						sitePage2.getTaxonomyCategories())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryItemExternalReferences",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sitePage1.getTaxonomyCategoryItemExternalReferences(),
						sitePage2.
							getTaxonomyCategoryItemExternalReferences())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sitePage1.getType(), sitePage2.getType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("uuid", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sitePage1.getUuid(), sitePage2.getUuid())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sitePage1.getViewableBy(), sitePage2.getViewableBy())) {

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

		if (!(_sitePageResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_sitePageResource;

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
		EntityField entityField, String operator, SitePage sitePage) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("availableLanguages")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("creatorExternalReferenceCode")) {
			Object object = sitePage.getCreatorExternalReferenceCode();

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

		if (entityFieldName.equals("customFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = sitePage.getDateCreated();

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

				sb.append(_format.format(sitePage.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = sitePage.getDateModified();

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

				sb.append(_format.format(sitePage.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("datePublished")) {
			if (operator.equals("between")) {
				Date date = sitePage.getDatePublished();

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

				sb.append(_format.format(sitePage.getDatePublished()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = sitePage.getExternalReferenceCode();

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

		if (entityFieldName.equals("keywordItemExternalReferences")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("keywords")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("name_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("pageSettings")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("pageSpecifications")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("parentSitePageExternalReferenceCode")) {
			Object object = sitePage.getParentSitePageExternalReferenceCode();

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

		if (entityFieldName.equals("taxonomyCategories")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("taxonomyCategoryItemExternalReferences")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("type")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("uuid")) {
			Object object = sitePage.getUuid();

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

	protected SitePage randomSitePage() throws Exception {
		return new SitePage() {
			{
				creatorExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				datePublished = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				parentSitePageExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				uuid = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected SitePage randomIrrelevantSitePage() throws Exception {
		SitePage randomIrrelevantSitePage = randomSitePage();

		return randomIrrelevantSitePage;
	}

	protected SitePage randomPatchSitePage() throws Exception {
		return randomSitePage();
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

	protected SitePageResource sitePageResource;
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
		LogFactoryUtil.getLog(BaseSitePageResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.admin.site.resource.v1_0.SitePageResource
		_sitePageResource;

}