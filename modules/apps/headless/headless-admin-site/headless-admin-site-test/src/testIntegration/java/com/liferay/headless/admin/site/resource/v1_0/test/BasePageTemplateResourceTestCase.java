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
import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageTemplate;
import com.liferay.headless.admin.site.client.dto.v1_0.PageTemplate;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageTemplate;
import com.liferay.headless.admin.site.client.http.HttpInvoker;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.pagination.Pagination;
import com.liferay.headless.admin.site.client.permission.Permission;
import com.liferay.headless.admin.site.client.resource.v1_0.PageTemplateResource;
import com.liferay.headless.admin.site.client.serdes.v1_0.PageTemplateSerDes;
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
import java.util.function.Supplier;

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
public abstract class BasePageTemplateResourceTestCase {

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

		_pageTemplateResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		pageTemplateResource = PageTemplateResource.builder(
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

		PageTemplate pageTemplate1 = randomPageTemplate();

		String json = objectMapper.writeValueAsString(pageTemplate1);

		PageTemplate pageTemplate2 = PageTemplateSerDes.toDTO(json);

		Assert.assertTrue(equals(pageTemplate1, pageTemplate2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		PageTemplate pageTemplate = randomPageTemplate();

		String json1 = objectMapper.writeValueAsString(pageTemplate);
		String json2 = PageTemplateSerDes.toJSON(pageTemplate);

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

		PageTemplate pageTemplate = randomPageTemplate();

		pageTemplate.setCreatorExternalReferenceCode(regex);
		pageTemplate.setExternalReferenceCode(regex);
		pageTemplate.setKey(regex);
		pageTemplate.setName(regex);
		pageTemplate.setUuid(regex);

		String json = PageTemplateSerDes.toJSON(pageTemplate);

		Assert.assertFalse(json.contains(regex));

		pageTemplate = PageTemplateSerDes.toDTO(json);

		Assert.assertEquals(
			regex, pageTemplate.getCreatorExternalReferenceCode());
		Assert.assertEquals(regex, pageTemplate.getExternalReferenceCode());
		Assert.assertEquals(regex, pageTemplate.getKey());
		Assert.assertEquals(regex, pageTemplate.getName());
		Assert.assertEquals(regex, pageTemplate.getUuid());
	}

	@Test
	public void testDeleteSiteSiteByExternalReferenceCodePageTemplate()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PageTemplate pageTemplate =
			testDeleteSiteSiteByExternalReferenceCodePageTemplate_addPageTemplate();

		assertHttpResponseStatusCode(
			204,
			pageTemplateResource.
				deleteSiteSiteByExternalReferenceCodePageTemplateHttpResponse(
					testDeleteSiteSiteByExternalReferenceCodePageTemplate_getSiteExternalReferenceCode(),
					pageTemplate.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			pageTemplateResource.
				getSiteSiteByExternalReferenceCodePageTemplateHttpResponse(
					testDeleteSiteSiteByExternalReferenceCodePageTemplate_getSiteExternalReferenceCode(),
					pageTemplate.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			pageTemplateResource.
				getSiteSiteByExternalReferenceCodePageTemplateHttpResponse(
					testDeleteSiteSiteByExternalReferenceCodePageTemplate_getSiteExternalReferenceCode(),
					"-"));
	}

	protected PageTemplate
			testDeleteSiteSiteByExternalReferenceCodePageTemplate_addPageTemplate()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteSiteSiteByExternalReferenceCodePageTemplate_getSiteExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetSitePageTemplatePermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		PageTemplate postPageTemplate =
			testGetSitePageTemplatePermissionsPage_addPageTemplate();

		Page<Permission> page =
			pageTemplateResource.getSitePageTemplatePermissionsPage(
				testGroup.getExternalReferenceCode(),
				postPageTemplate.getExternalReferenceCode(),
				RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected PageTemplate
			testGetSitePageTemplatePermissionsPage_addPageTemplate()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodePageTemplate()
		throws Exception {

		PageTemplate postPageTemplate =
			testGetSiteSiteByExternalReferenceCodePageTemplate_addPageTemplate();

		PageTemplate getPageTemplate =
			pageTemplateResource.getSiteSiteByExternalReferenceCodePageTemplate(
				testGetSiteSiteByExternalReferenceCodePageTemplate_getSiteExternalReferenceCode(),
				postPageTemplate.getExternalReferenceCode());

		assertEquals(postPageTemplate, getPageTemplate);
		assertValid(getPageTemplate);
	}

	protected PageTemplate
			testGetSiteSiteByExternalReferenceCodePageTemplate_addPageTemplate()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteSiteByExternalReferenceCodePageTemplate_getSiteExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteSiteByExternalReferenceCodePageTemplate()
		throws Exception {

		PageTemplate pageTemplate =
			testGraphQLGetSiteSiteByExternalReferenceCodePageTemplate_addPageTemplate();

		// No namespace

		Assert.assertTrue(
			equals(
				pageTemplate,
				PageTemplateSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"siteByExternalReferenceCodePageTemplate",
								new HashMap<String, Object>() {
									{
										put(
											"siteExternalReferenceCode",
											"\"" +
												testGraphQLGetSiteSiteByExternalReferenceCodePageTemplate_getSiteExternalReferenceCode() +
													"\"");
										put(
											"pageTemplateExternalReferenceCode",
											"\"" +
												pageTemplate.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/siteByExternalReferenceCodePageTemplate"))));

		// Using the namespace headlessAdminSite_v1_0

		Assert.assertTrue(
			equals(
				pageTemplate,
				PageTemplateSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminSite_v1_0",
								new GraphQLField(
									"siteByExternalReferenceCodePageTemplate",
									new HashMap<String, Object>() {
										{
											put(
												"siteExternalReferenceCode",
												"\"" +
													testGraphQLGetSiteSiteByExternalReferenceCodePageTemplate_getSiteExternalReferenceCode() +
														"\"");
											put(
												"pageTemplateExternalReferenceCode",
												"\"" +
													pageTemplate.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessAdminSite_v1_0",
						"Object/siteByExternalReferenceCodePageTemplate"))));
	}

	protected String
			testGraphQLGetSiteSiteByExternalReferenceCodePageTemplate_getSiteExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteSiteByExternalReferenceCodePageTemplateNotFound()
		throws Exception {

		String irrelevantPageTemplateExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"siteByExternalReferenceCodePageTemplate",
						new HashMap<String, Object>() {
							{
								put(
									"siteExternalReferenceCode",
									"\"" +
										irrelevantGroup.
											getExternalReferenceCode() + "\"");
								put(
									"pageTemplateExternalReferenceCode",
									irrelevantPageTemplateExternalReferenceCode);
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
							"siteByExternalReferenceCodePageTemplate",
							new HashMap<String, Object>() {
								{
									put(
										"siteExternalReferenceCode",
										"\"" +
											irrelevantGroup.
												getExternalReferenceCode() +
													"\"");
									put(
										"pageTemplateExternalReferenceCode",
										irrelevantPageTemplateExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected PageTemplate
			testGraphQLGetSiteSiteByExternalReferenceCodePageTemplate_addPageTemplate()
		throws Exception {

		return testGraphQLPageTemplate_addPageTemplate();
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodePageTemplateSetPageTemplatesPage()
		throws Exception {

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodePageTemplateSetPageTemplatesPage_getSiteExternalReferenceCode();
		String irrelevantSiteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodePageTemplateSetPageTemplatesPage_getIrrelevantSiteExternalReferenceCode();
		String pageTemplateSetExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodePageTemplateSetPageTemplatesPage_getPageTemplateSetExternalReferenceCode();
		String irrelevantPageTemplateSetExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodePageTemplateSetPageTemplatesPage_getIrrelevantPageTemplateSetExternalReferenceCode();

		Page<PageTemplate> page =
			pageTemplateResource.
				getSiteSiteByExternalReferenceCodePageTemplateSetPageTemplatesPage(
					siteExternalReferenceCode,
					pageTemplateSetExternalReferenceCode, null);

		long totalCount = page.getTotalCount();

		if ((irrelevantSiteExternalReferenceCode != null) &&
			(irrelevantPageTemplateSetExternalReferenceCode != null)) {

			PageTemplate irrelevantPageTemplate =
				testGetSiteSiteByExternalReferenceCodePageTemplateSetPageTemplatesPage_addPageTemplate(
					irrelevantSiteExternalReferenceCode,
					irrelevantPageTemplateSetExternalReferenceCode,
					randomIrrelevantPageTemplate());

			page =
				pageTemplateResource.
					getSiteSiteByExternalReferenceCodePageTemplateSetPageTemplatesPage(
						irrelevantSiteExternalReferenceCode,
						irrelevantPageTemplateSetExternalReferenceCode, null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPageTemplate, (List<PageTemplate>)page.getItems());
			assertValid(
				page,
				testGetSiteSiteByExternalReferenceCodePageTemplateSetPageTemplatesPage_getExpectedActions(
					irrelevantSiteExternalReferenceCode,
					irrelevantPageTemplateSetExternalReferenceCode));
		}

		PageTemplate pageTemplate1 =
			testGetSiteSiteByExternalReferenceCodePageTemplateSetPageTemplatesPage_addPageTemplate(
				siteExternalReferenceCode, pageTemplateSetExternalReferenceCode,
				randomPageTemplate());

		PageTemplate pageTemplate2 =
			testGetSiteSiteByExternalReferenceCodePageTemplateSetPageTemplatesPage_addPageTemplate(
				siteExternalReferenceCode, pageTemplateSetExternalReferenceCode,
				randomPageTemplate());

		page =
			pageTemplateResource.
				getSiteSiteByExternalReferenceCodePageTemplateSetPageTemplatesPage(
					siteExternalReferenceCode,
					pageTemplateSetExternalReferenceCode, null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(pageTemplate1, (List<PageTemplate>)page.getItems());
		assertContains(pageTemplate2, (List<PageTemplate>)page.getItems());
		assertValid(
			page,
			testGetSiteSiteByExternalReferenceCodePageTemplateSetPageTemplatesPage_getExpectedActions(
				siteExternalReferenceCode,
				pageTemplateSetExternalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetSiteSiteByExternalReferenceCodePageTemplateSetPageTemplatesPage_getExpectedActions(
				String siteExternalReferenceCode,
				String pageTemplateSetExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected PageTemplate
			testGetSiteSiteByExternalReferenceCodePageTemplateSetPageTemplatesPage_addPageTemplate(
				String siteExternalReferenceCode,
				String pageTemplateSetExternalReferenceCode,
				PageTemplate pageTemplate)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteSiteByExternalReferenceCodePageTemplateSetPageTemplatesPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteSiteByExternalReferenceCodePageTemplateSetPageTemplatesPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteSiteByExternalReferenceCodePageTemplateSetPageTemplatesPage_getPageTemplateSetExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteSiteByExternalReferenceCodePageTemplateSetPageTemplatesPage_getIrrelevantPageTemplateSetExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodePageTemplatesPage()
		throws Exception {

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodePageTemplatesPage_getSiteExternalReferenceCode();
		String irrelevantSiteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodePageTemplatesPage_getIrrelevantSiteExternalReferenceCode();

		Page<PageTemplate> page =
			pageTemplateResource.
				getSiteSiteByExternalReferenceCodePageTemplatesPage(
					siteExternalReferenceCode, null, null, null,
					Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteExternalReferenceCode != null) {
			PageTemplate irrelevantPageTemplate =
				testGetSiteSiteByExternalReferenceCodePageTemplatesPage_addPageTemplate(
					irrelevantSiteExternalReferenceCode,
					randomIrrelevantPageTemplate());

			page =
				pageTemplateResource.
					getSiteSiteByExternalReferenceCodePageTemplatesPage(
						irrelevantSiteExternalReferenceCode, null, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPageTemplate, (List<PageTemplate>)page.getItems());
			assertValid(
				page,
				testGetSiteSiteByExternalReferenceCodePageTemplatesPage_getExpectedActions(
					irrelevantSiteExternalReferenceCode));
		}

		PageTemplate pageTemplate1 =
			testGetSiteSiteByExternalReferenceCodePageTemplatesPage_addPageTemplate(
				siteExternalReferenceCode, randomPageTemplate());

		PageTemplate pageTemplate2 =
			testGetSiteSiteByExternalReferenceCodePageTemplatesPage_addPageTemplate(
				siteExternalReferenceCode, randomPageTemplate());

		page =
			pageTemplateResource.
				getSiteSiteByExternalReferenceCodePageTemplatesPage(
					siteExternalReferenceCode, null, null, null,
					Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(pageTemplate1, (List<PageTemplate>)page.getItems());
		assertContains(pageTemplate2, (List<PageTemplate>)page.getItems());
		assertValid(
			page,
			testGetSiteSiteByExternalReferenceCodePageTemplatesPage_getExpectedActions(
				siteExternalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetSiteSiteByExternalReferenceCodePageTemplatesPage_getExpectedActions(
				String siteExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodePageTemplatesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodePageTemplatesPage_getSiteExternalReferenceCode();

		PageTemplate pageTemplate1 = randomPageTemplate();

		pageTemplate1 =
			testGetSiteSiteByExternalReferenceCodePageTemplatesPage_addPageTemplate(
				siteExternalReferenceCode, pageTemplate1);

		for (EntityField entityField : entityFields) {
			Page<PageTemplate> page =
				pageTemplateResource.
					getSiteSiteByExternalReferenceCodePageTemplatesPage(
						siteExternalReferenceCode, null, null,
						getFilterString(entityField, "between", pageTemplate1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(pageTemplate1),
				(List<PageTemplate>)page.getItems());
		}
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodePageTemplatesPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodePageTemplatesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodePageTemplatesPageWithFilterStringContains()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodePageTemplatesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodePageTemplatesPageWithFilterStringEquals()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodePageTemplatesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodePageTemplatesPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodePageTemplatesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetSiteSiteByExternalReferenceCodePageTemplatesPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodePageTemplatesPage_getSiteExternalReferenceCode();

		PageTemplate pageTemplate1 =
			testGetSiteSiteByExternalReferenceCodePageTemplatesPage_addPageTemplate(
				siteExternalReferenceCode, randomPageTemplate());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PageTemplate pageTemplate2 =
			testGetSiteSiteByExternalReferenceCodePageTemplatesPage_addPageTemplate(
				siteExternalReferenceCode, randomPageTemplate());

		for (EntityField entityField : entityFields) {
			Page<PageTemplate> page =
				pageTemplateResource.
					getSiteSiteByExternalReferenceCodePageTemplatesPage(
						siteExternalReferenceCode, null, null,
						getFilterString(entityField, operator, pageTemplate1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(pageTemplate1),
				(List<PageTemplate>)page.getItems());
		}
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodePageTemplatesPageWithPagination()
		throws Exception {

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodePageTemplatesPage_getSiteExternalReferenceCode();

		Page<PageTemplate> pageTemplatesPage =
			pageTemplateResource.
				getSiteSiteByExternalReferenceCodePageTemplatesPage(
					siteExternalReferenceCode, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			pageTemplatesPage.getTotalCount());

		PageTemplate pageTemplate1 =
			testGetSiteSiteByExternalReferenceCodePageTemplatesPage_addPageTemplate(
				siteExternalReferenceCode, randomPageTemplate());

		PageTemplate pageTemplate2 =
			testGetSiteSiteByExternalReferenceCodePageTemplatesPage_addPageTemplate(
				siteExternalReferenceCode, randomPageTemplate());

		PageTemplate pageTemplate3 =
			testGetSiteSiteByExternalReferenceCodePageTemplatesPage_addPageTemplate(
				siteExternalReferenceCode, randomPageTemplate());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<PageTemplate> page1 =
				pageTemplateResource.
					getSiteSiteByExternalReferenceCodePageTemplatesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(pageTemplate1, (List<PageTemplate>)page1.getItems());

			Page<PageTemplate> page2 =
				pageTemplateResource.
					getSiteSiteByExternalReferenceCodePageTemplatesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(pageTemplate2, (List<PageTemplate>)page2.getItems());

			Page<PageTemplate> page3 =
				pageTemplateResource.
					getSiteSiteByExternalReferenceCodePageTemplatesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(pageTemplate3, (List<PageTemplate>)page3.getItems());
		}
		else {
			Page<PageTemplate> page1 =
				pageTemplateResource.
					getSiteSiteByExternalReferenceCodePageTemplatesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<PageTemplate> pageTemplates1 =
				(List<PageTemplate>)page1.getItems();

			Assert.assertEquals(
				pageTemplates1.toString(), totalCount + 2,
				pageTemplates1.size());

			Page<PageTemplate> page2 =
				pageTemplateResource.
					getSiteSiteByExternalReferenceCodePageTemplatesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PageTemplate> pageTemplates2 =
				(List<PageTemplate>)page2.getItems();

			Assert.assertEquals(
				pageTemplates2.toString(), 1, pageTemplates2.size());

			Page<PageTemplate> page3 =
				pageTemplateResource.
					getSiteSiteByExternalReferenceCodePageTemplatesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(pageTemplate1, (List<PageTemplate>)page3.getItems());
			assertContains(pageTemplate2, (List<PageTemplate>)page3.getItems());
			assertContains(pageTemplate3, (List<PageTemplate>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodePageTemplatesPageWithSortDateTime()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodePageTemplatesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, pageTemplate1, pageTemplate2) -> {
				BeanTestUtil.setProperty(
					pageTemplate1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodePageTemplatesPageWithSortDouble()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodePageTemplatesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, pageTemplate1, pageTemplate2) -> {
				BeanTestUtil.setProperty(
					pageTemplate1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					pageTemplate2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodePageTemplatesPageWithSortInteger()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodePageTemplatesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, pageTemplate1, pageTemplate2) -> {
				BeanTestUtil.setProperty(
					pageTemplate1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					pageTemplate2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodePageTemplatesPageWithSortString()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodePageTemplatesPageWithSort(
			EntityField.Type.STRING,
			(entityField, pageTemplate1, pageTemplate2) -> {
				Class<?> clazz = pageTemplate1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						pageTemplate1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						pageTemplate2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						pageTemplate1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						pageTemplate2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						pageTemplate1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						pageTemplate2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetSiteSiteByExternalReferenceCodePageTemplatesPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, PageTemplate, PageTemplate, Exception>
						unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodePageTemplatesPage_getSiteExternalReferenceCode();

		PageTemplate pageTemplate1 = randomPageTemplate();
		PageTemplate pageTemplate2 = randomPageTemplate();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, pageTemplate1, pageTemplate2);
		}

		pageTemplate1 =
			testGetSiteSiteByExternalReferenceCodePageTemplatesPage_addPageTemplate(
				siteExternalReferenceCode, pageTemplate1);

		pageTemplate2 =
			testGetSiteSiteByExternalReferenceCodePageTemplatesPage_addPageTemplate(
				siteExternalReferenceCode, pageTemplate2);

		Page<PageTemplate> page =
			pageTemplateResource.
				getSiteSiteByExternalReferenceCodePageTemplatesPage(
					siteExternalReferenceCode, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<PageTemplate> ascPage =
				pageTemplateResource.
					getSiteSiteByExternalReferenceCodePageTemplatesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				pageTemplate1, (List<PageTemplate>)ascPage.getItems());
			assertContains(
				pageTemplate2, (List<PageTemplate>)ascPage.getItems());

			Page<PageTemplate> descPage =
				pageTemplateResource.
					getSiteSiteByExternalReferenceCodePageTemplatesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				pageTemplate2, (List<PageTemplate>)descPage.getItems());
			assertContains(
				pageTemplate1, (List<PageTemplate>)descPage.getItems());
		}
	}

	protected PageTemplate
			testGetSiteSiteByExternalReferenceCodePageTemplatesPage_addPageTemplate(
				String siteExternalReferenceCode, PageTemplate pageTemplate)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteSiteByExternalReferenceCodePageTemplatesPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteSiteByExternalReferenceCodePageTemplatesPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	@Test
	public void testPatchSiteSiteByExternalReferenceCodePageTemplate()
		throws Exception {

		PageTemplate postPageTemplate =
			testPatchSiteSiteByExternalReferenceCodePageTemplate_addPageTemplate();

		PageTemplate randomPatchPageTemplate = randomPatchPageTemplate();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PageTemplate patchPageTemplate =
			pageTemplateResource.
				patchSiteSiteByExternalReferenceCodePageTemplate(
					null, postPageTemplate.getExternalReferenceCode(),
					randomPatchPageTemplate);

		PageTemplate expectedPatchPageTemplate = postPageTemplate.clone();

		BeanTestUtil.copyProperties(
			randomPatchPageTemplate, expectedPatchPageTemplate);

		PageTemplate getPageTemplate =
			pageTemplateResource.getSiteSiteByExternalReferenceCodePageTemplate(
				null, patchPageTemplate.getExternalReferenceCode());

		assertEquals(expectedPatchPageTemplate, getPageTemplate);
		assertValid(getPageTemplate);
	}

	protected PageTemplate
			testPatchSiteSiteByExternalReferenceCodePageTemplate_addPageTemplate()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSiteSiteByExternalReferenceCodePageTemplate()
		throws Exception {

		PageTemplate randomPageTemplate = randomPageTemplate();

		PageTemplate postPageTemplate =
			testPostSiteSiteByExternalReferenceCodePageTemplate_addPageTemplate(
				randomPageTemplate);

		assertEquals(randomPageTemplate, postPageTemplate);
		assertValid(postPageTemplate);

		ContentPageTemplate contentPageTemplate = new ContentPageTemplate() {
			{
				creatorExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				datePublished = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				uuid = StringUtil.toLowerCase(RandomTestUtil.randomString());

				type = Type.create("ContentPageTemplate");
			}
		};

		assertEquals(
			contentPageTemplate,
			testPostSiteSiteByExternalReferenceCodePageTemplate_addPageTemplate(
				contentPageTemplate));

		WidgetPageTemplate widgetPageTemplate = new WidgetPageTemplate() {
			{
				creatorExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				datePublished = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				uuid = StringUtil.toLowerCase(RandomTestUtil.randomString());
				active = RandomTestUtil.randomBoolean();
				hiddenFromNavigation = RandomTestUtil.randomBoolean();

				type = Type.create("WidgetPageTemplate");
			}
		};

		assertEquals(
			widgetPageTemplate,
			testPostSiteSiteByExternalReferenceCodePageTemplate_addPageTemplate(
				widgetPageTemplate));
	}

	protected PageTemplate
			testPostSiteSiteByExternalReferenceCodePageTemplate_addPageTemplate(
				PageTemplate pageTemplate)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSiteSiteByExternalReferenceCodePageTemplateSetPageTemplate()
		throws Exception {

		PageTemplate randomPageTemplate = randomPageTemplate();

		PageTemplate postPageTemplate =
			testPostSiteSiteByExternalReferenceCodePageTemplateSetPageTemplate_addPageTemplate(
				randomPageTemplate);

		assertEquals(randomPageTemplate, postPageTemplate);
		assertValid(postPageTemplate);

		ContentPageTemplate contentPageTemplate = new ContentPageTemplate() {
			{
				creatorExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				datePublished = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				uuid = StringUtil.toLowerCase(RandomTestUtil.randomString());

				type = Type.create("ContentPageTemplate");
			}
		};

		assertEquals(
			contentPageTemplate,
			testPostSiteSiteByExternalReferenceCodePageTemplateSetPageTemplate_addPageTemplate(
				contentPageTemplate));

		WidgetPageTemplate widgetPageTemplate = new WidgetPageTemplate() {
			{
				creatorExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				datePublished = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				uuid = StringUtil.toLowerCase(RandomTestUtil.randomString());
				active = RandomTestUtil.randomBoolean();
				hiddenFromNavigation = RandomTestUtil.randomBoolean();

				type = Type.create("WidgetPageTemplate");
			}
		};

		assertEquals(
			widgetPageTemplate,
			testPostSiteSiteByExternalReferenceCodePageTemplateSetPageTemplate_addPageTemplate(
				widgetPageTemplate));
	}

	protected PageTemplate
			testPostSiteSiteByExternalReferenceCodePageTemplateSetPageTemplate_addPageTemplate(
				PageTemplate pageTemplate)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutSitePageTemplatePermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		PageTemplate pageTemplate =
			testPutSitePageTemplatePermissionsPage_addPageTemplate();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			pageTemplateResource.putSitePageTemplatePermissionsPageHttpResponse(
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
			pageTemplateResource.putSitePageTemplatePermissionsPageHttpResponse(
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

	protected PageTemplate
			testPutSitePageTemplatePermissionsPage_addPageTemplate()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutSiteSiteByExternalReferenceCodePageTemplate()
		throws Exception {

		PageTemplate postPageTemplate =
			testPutSiteSiteByExternalReferenceCodePageTemplate_addPageTemplate();

		PageTemplate randomPageTemplate = randomPageTemplate();

		PageTemplate putPageTemplate =
			pageTemplateResource.putSiteSiteByExternalReferenceCodePageTemplate(
				testPutSiteSiteByExternalReferenceCodePageTemplate_getSiteExternalReferenceCode(),
				postPageTemplate.getExternalReferenceCode(),
				randomPageTemplate);

		assertEquals(randomPageTemplate, putPageTemplate);
		assertValid(putPageTemplate);

		PageTemplate getPageTemplate =
			pageTemplateResource.getSiteSiteByExternalReferenceCodePageTemplate(
				testPutSiteSiteByExternalReferenceCodePageTemplate_getSiteExternalReferenceCode(),
				putPageTemplate.getExternalReferenceCode());

		assertEquals(randomPageTemplate, getPageTemplate);
		assertValid(getPageTemplate);
	}

	protected PageTemplate
			testPutSiteSiteByExternalReferenceCodePageTemplate_addPageTemplate()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testPutSiteSiteByExternalReferenceCodePageTemplate_getSiteExternalReferenceCode()
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
	public void testPostSiteSiteByExternalReferenceCodePageTemplatePageSpecification()
		throws Exception {

		Assert.assertTrue(true);
	}

	protected PageTemplate testGraphQLPageTemplate_addPageTemplate()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		PageTemplate pageTemplate, List<PageTemplate> pageTemplates) {

		boolean contains = false;

		for (PageTemplate item : pageTemplates) {
			if (equals(pageTemplate, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			pageTemplates + " does not contain " + pageTemplate, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		PageTemplate pageTemplate1, PageTemplate pageTemplate2) {

		Assert.assertTrue(
			pageTemplate1 + " does not equal " + pageTemplate2,
			equals(pageTemplate1, pageTemplate2));
	}

	protected void assertEquals(
		List<PageTemplate> pageTemplates1, List<PageTemplate> pageTemplates2) {

		Assert.assertEquals(pageTemplates1.size(), pageTemplates2.size());

		for (int i = 0; i < pageTemplates1.size(); i++) {
			PageTemplate pageTemplate1 = pageTemplates1.get(i);
			PageTemplate pageTemplate2 = pageTemplates2.get(i);

			assertEquals(pageTemplate1, pageTemplate2);
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
		List<PageTemplate> pageTemplates1, List<PageTemplate> pageTemplates2) {

		Assert.assertEquals(pageTemplates1.size(), pageTemplates2.size());

		for (PageTemplate pageTemplate1 : pageTemplates1) {
			boolean contains = false;

			for (PageTemplate pageTemplate2 : pageTemplates2) {
				if (equals(pageTemplate1, pageTemplate2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				pageTemplates2 + " does not contain " + pageTemplate1,
				contains);
		}
	}

	protected void assertValid(PageTemplate pageTemplate) throws Exception {
		boolean valid = true;

		if (pageTemplate.getDateCreated() == null) {
			valid = false;
		}

		if (pageTemplate.getDateModified() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (pageTemplate.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"creatorExternalReferenceCode",
					additionalAssertFieldName)) {

				if (pageTemplate.getCreatorExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("datePublished", additionalAssertFieldName)) {
				if (pageTemplate.getDatePublished() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (pageTemplate.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (pageTemplate.getKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"keywordItemExternalReferences",
					additionalAssertFieldName)) {

				if (pageTemplate.getKeywordItemExternalReferences() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("keywords", additionalAssertFieldName)) {
				if (pageTemplate.getKeywords() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (pageTemplate.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"pageSpecifications", additionalAssertFieldName)) {

				if (pageTemplate.getPageSpecifications() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("pageTemplateSet", additionalAssertFieldName)) {
				if (pageTemplate.getPageTemplateSet() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"pageTemplateSettings", additionalAssertFieldName)) {

				if (pageTemplate.getPageTemplateSettings() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategories", additionalAssertFieldName)) {

				if (pageTemplate.getTaxonomyCategories() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryItemExternalReferences",
					additionalAssertFieldName)) {

				if (pageTemplate.getTaxonomyCategoryItemExternalReferences() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (pageTemplate.getType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("uuid", additionalAssertFieldName)) {
				if (pageTemplate.getUuid() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (!(pageTemplate instanceof WidgetPageTemplate)) {
					continue;
				}

				if (((WidgetPageTemplate)pageTemplate).getActive() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (!(pageTemplate instanceof WidgetPageTemplate)) {
					continue;
				}

				if (((WidgetPageTemplate)pageTemplate).getDescription_i18n() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"friendlyUrlPath_i18n", additionalAssertFieldName)) {

				if (!(pageTemplate instanceof WidgetPageTemplate)) {
					continue;
				}

				if (((WidgetPageTemplate)pageTemplate).
						getFriendlyUrlPath_i18n() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"hiddenFromNavigation", additionalAssertFieldName)) {

				if (!(pageTemplate instanceof WidgetPageTemplate)) {
					continue;
				}

				if (((WidgetPageTemplate)pageTemplate).
						getHiddenFromNavigation() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (!(pageTemplate instanceof WidgetPageTemplate)) {
					continue;
				}

				if (((WidgetPageTemplate)pageTemplate).getName_i18n() == null) {
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

	protected void assertValid(Page<PageTemplate> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<PageTemplate> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<PageTemplate> pageTemplates = page.getItems();

		int size = pageTemplates.size();

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
					com.liferay.headless.admin.site.dto.v1_0.PageTemplate.
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
		PageTemplate pageTemplate1, PageTemplate pageTemplate2) {

		if (pageTemplate1 == pageTemplate2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						pageTemplate1.getCreator(),
						pageTemplate2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"creatorExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						pageTemplate1.getCreatorExternalReferenceCode(),
						pageTemplate2.getCreatorExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						pageTemplate1.getDateCreated(),
						pageTemplate2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						pageTemplate1.getDateModified(),
						pageTemplate2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("datePublished", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						pageTemplate1.getDatePublished(),
						pageTemplate2.getDatePublished())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						pageTemplate1.getExternalReferenceCode(),
						pageTemplate2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						pageTemplate1.getKey(), pageTemplate2.getKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"keywordItemExternalReferences",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						pageTemplate1.getKeywordItemExternalReferences(),
						pageTemplate2.getKeywordItemExternalReferences())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("keywords", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						pageTemplate1.getKeywords(),
						pageTemplate2.getKeywords())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						pageTemplate1.getName(), pageTemplate2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"pageSpecifications", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						pageTemplate1.getPageSpecifications(),
						pageTemplate2.getPageSpecifications())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("pageTemplateSet", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						pageTemplate1.getPageTemplateSet(),
						pageTemplate2.getPageTemplateSet())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"pageTemplateSettings", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						pageTemplate1.getPageTemplateSettings(),
						pageTemplate2.getPageTemplateSettings())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategories", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						pageTemplate1.getTaxonomyCategories(),
						pageTemplate2.getTaxonomyCategories())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryItemExternalReferences",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						pageTemplate1.
							getTaxonomyCategoryItemExternalReferences(),
						pageTemplate2.
							getTaxonomyCategoryItemExternalReferences())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						pageTemplate1.getType(), pageTemplate2.getType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("uuid", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						pageTemplate1.getUuid(), pageTemplate2.getUuid())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (!(pageTemplate1 instanceof WidgetPageTemplate) ||
					!(pageTemplate2 instanceof WidgetPageTemplate)) {

					continue;
				}

				if (!Objects.deepEquals(
						((WidgetPageTemplate)pageTemplate1).getActive(),
						((WidgetPageTemplate)pageTemplate2).getActive())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (!(pageTemplate1 instanceof WidgetPageTemplate) ||
					!(pageTemplate2 instanceof WidgetPageTemplate)) {

					continue;
				}

				if (!Objects.deepEquals(
						((WidgetPageTemplate)pageTemplate1).
							getDescription_i18n(),
						((WidgetPageTemplate)pageTemplate2).
							getDescription_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"friendlyUrlPath_i18n", additionalAssertFieldName)) {

				if (!(pageTemplate1 instanceof WidgetPageTemplate) ||
					!(pageTemplate2 instanceof WidgetPageTemplate)) {

					continue;
				}

				if (!Objects.deepEquals(
						((WidgetPageTemplate)pageTemplate1).
							getFriendlyUrlPath_i18n(),
						((WidgetPageTemplate)pageTemplate2).
							getFriendlyUrlPath_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"hiddenFromNavigation", additionalAssertFieldName)) {

				if (!(pageTemplate1 instanceof WidgetPageTemplate) ||
					!(pageTemplate2 instanceof WidgetPageTemplate)) {

					continue;
				}

				if (!Objects.deepEquals(
						((WidgetPageTemplate)pageTemplate1).
							getHiddenFromNavigation(),
						((WidgetPageTemplate)pageTemplate2).
							getHiddenFromNavigation())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (!(pageTemplate1 instanceof WidgetPageTemplate) ||
					!(pageTemplate2 instanceof WidgetPageTemplate)) {

					continue;
				}

				if (!Objects.deepEquals(
						((WidgetPageTemplate)pageTemplate1).getName_i18n(),
						((WidgetPageTemplate)pageTemplate2).getName_i18n())) {

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

		if (!(_pageTemplateResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_pageTemplateResource;

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
		EntityField entityField, String operator, PageTemplate pageTemplate) {

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
			Object object = pageTemplate.getCreatorExternalReferenceCode();

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
				Date date = pageTemplate.getDateCreated();

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

				sb.append(_format.format(pageTemplate.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = pageTemplate.getDateModified();

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

				sb.append(_format.format(pageTemplate.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("datePublished")) {
			if (operator.equals("between")) {
				Date date = pageTemplate.getDatePublished();

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

				sb.append(_format.format(pageTemplate.getDatePublished()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = pageTemplate.getExternalReferenceCode();

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
			Object object = pageTemplate.getKey();

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

		if (entityFieldName.equals("keywordItemExternalReferences")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("keywords")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("name")) {
			Object object = pageTemplate.getName();

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

		if (entityFieldName.equals("pageTemplateSet")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("pageTemplateSettings")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
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
			Object object = pageTemplate.getUuid();

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

	protected PageTemplate randomPageTemplate() throws Exception {
		List<Supplier<PageTemplate>> suppliers = Arrays.asList(
			() -> {
				ContentPageTemplate pageTemplate = new ContentPageTemplate();

				pageTemplate.setCreatorExternalReferenceCode(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				pageTemplate.setDateCreated(RandomTestUtil.nextDate());
				pageTemplate.setDateModified(RandomTestUtil.nextDate());
				pageTemplate.setDatePublished(RandomTestUtil.nextDate());
				pageTemplate.setExternalReferenceCode(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				pageTemplate.setKey(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				pageTemplate.setName(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				pageTemplate.setUuid(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));

				pageTemplate.setType(
					PageTemplate.Type.create("ContentPageTemplate"));

				return pageTemplate;
			},
			() -> {
				WidgetPageTemplate pageTemplate = new WidgetPageTemplate();

				pageTemplate.setCreatorExternalReferenceCode(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				pageTemplate.setDateCreated(RandomTestUtil.nextDate());
				pageTemplate.setDateModified(RandomTestUtil.nextDate());
				pageTemplate.setDatePublished(RandomTestUtil.nextDate());
				pageTemplate.setExternalReferenceCode(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				pageTemplate.setKey(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				pageTemplate.setName(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				pageTemplate.setUuid(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));

				pageTemplate.setActive(RandomTestUtil.randomBoolean());
				pageTemplate.setHiddenFromNavigation(
					RandomTestUtil.randomBoolean());

				pageTemplate.setType(
					PageTemplate.Type.create("WidgetPageTemplate"));

				return pageTemplate;
			});

		Supplier<PageTemplate> supplier = suppliers.get(
			RandomTestUtil.randomInt(0, suppliers.size() - 1));

		return supplier.get();
	}

	protected PageTemplate randomIrrelevantPageTemplate() throws Exception {
		PageTemplate randomIrrelevantPageTemplate = randomPageTemplate();

		return randomIrrelevantPageTemplate;
	}

	protected PageTemplate randomPatchPageTemplate() throws Exception {
		return randomPageTemplate();
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

	protected PageTemplateResource pageTemplateResource;
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
		LogFactoryUtil.getLog(BasePageTemplateResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.admin.site.resource.v1_0.PageTemplateResource
		_pageTemplateResource;

}