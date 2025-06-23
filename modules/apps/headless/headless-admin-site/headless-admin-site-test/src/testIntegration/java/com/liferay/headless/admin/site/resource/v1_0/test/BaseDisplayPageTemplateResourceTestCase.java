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
import com.liferay.headless.admin.site.client.dto.v1_0.DisplayPageTemplate;
import com.liferay.headless.admin.site.client.http.HttpInvoker;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.pagination.Pagination;
import com.liferay.headless.admin.site.client.permission.Permission;
import com.liferay.headless.admin.site.client.resource.v1_0.DisplayPageTemplateResource;
import com.liferay.headless.admin.site.client.serdes.v1_0.DisplayPageTemplateSerDes;
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
public abstract class BaseDisplayPageTemplateResourceTestCase {

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

		_displayPageTemplateResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		displayPageTemplateResource = DisplayPageTemplateResource.builder(
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

		DisplayPageTemplate displayPageTemplate1 = randomDisplayPageTemplate();

		String json = objectMapper.writeValueAsString(displayPageTemplate1);

		DisplayPageTemplate displayPageTemplate2 =
			DisplayPageTemplateSerDes.toDTO(json);

		Assert.assertTrue(equals(displayPageTemplate1, displayPageTemplate2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		String json1 = objectMapper.writeValueAsString(displayPageTemplate);
		String json2 = DisplayPageTemplateSerDes.toJSON(displayPageTemplate);

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

		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		displayPageTemplate.setCreatorExternalReferenceCode(regex);
		displayPageTemplate.setExternalReferenceCode(regex);
		displayPageTemplate.setKey(regex);
		displayPageTemplate.setName(regex);
		displayPageTemplate.setUuid(regex);

		String json = DisplayPageTemplateSerDes.toJSON(displayPageTemplate);

		Assert.assertFalse(json.contains(regex));

		displayPageTemplate = DisplayPageTemplateSerDes.toDTO(json);

		Assert.assertEquals(
			regex, displayPageTemplate.getCreatorExternalReferenceCode());
		Assert.assertEquals(
			regex, displayPageTemplate.getExternalReferenceCode());
		Assert.assertEquals(regex, displayPageTemplate.getKey());
		Assert.assertEquals(regex, displayPageTemplate.getName());
		Assert.assertEquals(regex, displayPageTemplate.getUuid());
	}

	@Test
	public void testDeleteSiteSiteByExternalReferenceCodeDisplayPageTemplate()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		DisplayPageTemplate displayPageTemplate =
			testDeleteSiteSiteByExternalReferenceCodeDisplayPageTemplate_addDisplayPageTemplate();

		assertHttpResponseStatusCode(
			204,
			displayPageTemplateResource.
				deleteSiteSiteByExternalReferenceCodeDisplayPageTemplateHttpResponse(
					testDeleteSiteSiteByExternalReferenceCodeDisplayPageTemplate_getSiteExternalReferenceCode(),
					displayPageTemplate.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			displayPageTemplateResource.
				getSiteSiteByExternalReferenceCodeDisplayPageTemplateHttpResponse(
					testDeleteSiteSiteByExternalReferenceCodeDisplayPageTemplate_getSiteExternalReferenceCode(),
					displayPageTemplate.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			displayPageTemplateResource.
				getSiteSiteByExternalReferenceCodeDisplayPageTemplateHttpResponse(
					testDeleteSiteSiteByExternalReferenceCodeDisplayPageTemplate_getSiteExternalReferenceCode(),
					"-"));
	}

	protected DisplayPageTemplate
			testDeleteSiteSiteByExternalReferenceCodeDisplayPageTemplate_addDisplayPageTemplate()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteSiteSiteByExternalReferenceCodeDisplayPageTemplate_getSiteExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetSiteDisplayPageTemplatePermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		DisplayPageTemplate postDisplayPageTemplate =
			testGetSiteDisplayPageTemplatePermissionsPage_addDisplayPageTemplate();

		Page<Permission> page =
			displayPageTemplateResource.
				getSiteDisplayPageTemplatePermissionsPage(
					testGroup.getExternalReferenceCode(),
					postDisplayPageTemplate.getExternalReferenceCode(),
					RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected DisplayPageTemplate
			testGetSiteDisplayPageTemplatePermissionsPage_addDisplayPageTemplate()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeDisplayPageTemplate()
		throws Exception {

		DisplayPageTemplate postDisplayPageTemplate =
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplate_addDisplayPageTemplate();

		DisplayPageTemplate getDisplayPageTemplate =
			displayPageTemplateResource.
				getSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testGetSiteSiteByExternalReferenceCodeDisplayPageTemplate_getSiteExternalReferenceCode(),
					postDisplayPageTemplate.getExternalReferenceCode());

		assertEquals(postDisplayPageTemplate, getDisplayPageTemplate);
		assertValid(getDisplayPageTemplate);
	}

	protected DisplayPageTemplate
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplate_addDisplayPageTemplate()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplate_getSiteExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteSiteByExternalReferenceCodeDisplayPageTemplate()
		throws Exception {

		DisplayPageTemplate displayPageTemplate =
			testGraphQLGetSiteSiteByExternalReferenceCodeDisplayPageTemplate_addDisplayPageTemplate();

		// No namespace

		Assert.assertTrue(
			equals(
				displayPageTemplate,
				DisplayPageTemplateSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"siteByExternalReferenceCodeDisplayPageTemplate",
								new HashMap<String, Object>() {
									{
										put(
											"siteExternalReferenceCode",
											"\"" +
												testGraphQLGetSiteSiteByExternalReferenceCodeDisplayPageTemplate_getSiteExternalReferenceCode() +
													"\"");
										put(
											"displayPageTemplateExternalReferenceCode",
											"\"" +
												displayPageTemplate.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/siteByExternalReferenceCodeDisplayPageTemplate"))));

		// Using the namespace headlessAdminSite_v1_0

		Assert.assertTrue(
			equals(
				displayPageTemplate,
				DisplayPageTemplateSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminSite_v1_0",
								new GraphQLField(
									"siteByExternalReferenceCodeDisplayPageTemplate",
									new HashMap<String, Object>() {
										{
											put(
												"siteExternalReferenceCode",
												"\"" +
													testGraphQLGetSiteSiteByExternalReferenceCodeDisplayPageTemplate_getSiteExternalReferenceCode() +
														"\"");
											put(
												"displayPageTemplateExternalReferenceCode",
												"\"" +
													displayPageTemplate.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessAdminSite_v1_0",
						"Object/siteByExternalReferenceCodeDisplayPageTemplate"))));
	}

	protected String
			testGraphQLGetSiteSiteByExternalReferenceCodeDisplayPageTemplate_getSiteExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteSiteByExternalReferenceCodeDisplayPageTemplateNotFound()
		throws Exception {

		String irrelevantDisplayPageTemplateExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"siteByExternalReferenceCodeDisplayPageTemplate",
						new HashMap<String, Object>() {
							{
								put(
									"siteExternalReferenceCode",
									"\"" +
										irrelevantGroup.
											getExternalReferenceCode() + "\"");
								put(
									"displayPageTemplateExternalReferenceCode",
									irrelevantDisplayPageTemplateExternalReferenceCode);
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
							"siteByExternalReferenceCodeDisplayPageTemplate",
							new HashMap<String, Object>() {
								{
									put(
										"siteExternalReferenceCode",
										"\"" +
											irrelevantGroup.
												getExternalReferenceCode() +
													"\"");
									put(
										"displayPageTemplateExternalReferenceCode",
										irrelevantDisplayPageTemplateExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected DisplayPageTemplate
			testGraphQLGetSiteSiteByExternalReferenceCodeDisplayPageTemplate_addDisplayPageTemplate()
		throws Exception {

		return testGraphQLDisplayPageTemplate_addDisplayPageTemplate();
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplatesPage()
		throws Exception {

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplatesPage_getSiteExternalReferenceCode();
		String irrelevantSiteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplatesPage_getIrrelevantSiteExternalReferenceCode();
		String displayPageTemplateFolderExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplatesPage_getDisplayPageTemplateFolderExternalReferenceCode();
		String irrelevantDisplayPageTemplateFolderExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplatesPage_getIrrelevantDisplayPageTemplateFolderExternalReferenceCode();

		Page<DisplayPageTemplate> page =
			displayPageTemplateResource.
				getSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplatesPage(
					siteExternalReferenceCode,
					displayPageTemplateFolderExternalReferenceCode, null);

		long totalCount = page.getTotalCount();

		if ((irrelevantSiteExternalReferenceCode != null) &&
			(irrelevantDisplayPageTemplateFolderExternalReferenceCode !=
				null)) {

			DisplayPageTemplate irrelevantDisplayPageTemplate =
				testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplatesPage_addDisplayPageTemplate(
					irrelevantSiteExternalReferenceCode,
					irrelevantDisplayPageTemplateFolderExternalReferenceCode,
					randomIrrelevantDisplayPageTemplate());

			page =
				displayPageTemplateResource.
					getSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplatesPage(
						irrelevantSiteExternalReferenceCode,
						irrelevantDisplayPageTemplateFolderExternalReferenceCode,
						null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDisplayPageTemplate,
				(List<DisplayPageTemplate>)page.getItems());
			assertValid(
				page,
				testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplatesPage_getExpectedActions(
					irrelevantSiteExternalReferenceCode,
					irrelevantDisplayPageTemplateFolderExternalReferenceCode));
		}

		DisplayPageTemplate displayPageTemplate1 =
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplatesPage_addDisplayPageTemplate(
				siteExternalReferenceCode,
				displayPageTemplateFolderExternalReferenceCode,
				randomDisplayPageTemplate());

		DisplayPageTemplate displayPageTemplate2 =
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplatesPage_addDisplayPageTemplate(
				siteExternalReferenceCode,
				displayPageTemplateFolderExternalReferenceCode,
				randomDisplayPageTemplate());

		page =
			displayPageTemplateResource.
				getSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplatesPage(
					siteExternalReferenceCode,
					displayPageTemplateFolderExternalReferenceCode, null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			displayPageTemplate1, (List<DisplayPageTemplate>)page.getItems());
		assertContains(
			displayPageTemplate2, (List<DisplayPageTemplate>)page.getItems());
		assertValid(
			page,
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplatesPage_getExpectedActions(
				siteExternalReferenceCode,
				displayPageTemplateFolderExternalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplatesPage_getExpectedActions(
				String siteExternalReferenceCode,
				String displayPageTemplateFolderExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected DisplayPageTemplate
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplatesPage_addDisplayPageTemplate(
				String siteExternalReferenceCode,
				String displayPageTemplateFolderExternalReferenceCode,
				DisplayPageTemplate displayPageTemplate)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplatesPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplatesPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplatesPage_getDisplayPageTemplateFolderExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplatesPage_getIrrelevantDisplayPageTemplateFolderExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage()
		throws Exception {

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage_getSiteExternalReferenceCode();
		String irrelevantSiteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage_getIrrelevantSiteExternalReferenceCode();

		Page<DisplayPageTemplate> page =
			displayPageTemplateResource.
				getSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage(
					siteExternalReferenceCode, null, null, null,
					Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteExternalReferenceCode != null) {
			DisplayPageTemplate irrelevantDisplayPageTemplate =
				testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage_addDisplayPageTemplate(
					irrelevantSiteExternalReferenceCode,
					randomIrrelevantDisplayPageTemplate());

			page =
				displayPageTemplateResource.
					getSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage(
						irrelevantSiteExternalReferenceCode, null, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDisplayPageTemplate,
				(List<DisplayPageTemplate>)page.getItems());
			assertValid(
				page,
				testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage_getExpectedActions(
					irrelevantSiteExternalReferenceCode));
		}

		DisplayPageTemplate displayPageTemplate1 =
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage_addDisplayPageTemplate(
				siteExternalReferenceCode, randomDisplayPageTemplate());

		DisplayPageTemplate displayPageTemplate2 =
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage_addDisplayPageTemplate(
				siteExternalReferenceCode, randomDisplayPageTemplate());

		page =
			displayPageTemplateResource.
				getSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage(
					siteExternalReferenceCode, null, null, null,
					Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			displayPageTemplate1, (List<DisplayPageTemplate>)page.getItems());
		assertContains(
			displayPageTemplate2, (List<DisplayPageTemplate>)page.getItems());
		assertValid(
			page,
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage_getExpectedActions(
				siteExternalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage_getExpectedActions(
				String siteExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage_getSiteExternalReferenceCode();

		DisplayPageTemplate displayPageTemplate1 = randomDisplayPageTemplate();

		displayPageTemplate1 =
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage_addDisplayPageTemplate(
				siteExternalReferenceCode, displayPageTemplate1);

		for (EntityField entityField : entityFields) {
			Page<DisplayPageTemplate> page =
				displayPageTemplateResource.
					getSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage(
						siteExternalReferenceCode, null, null,
						getFilterString(
							entityField, "between", displayPageTemplate1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(displayPageTemplate1),
				(List<DisplayPageTemplate>)page.getItems());
		}
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithFilterStringContains()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithFilterStringEquals()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage_getSiteExternalReferenceCode();

		DisplayPageTemplate displayPageTemplate1 =
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage_addDisplayPageTemplate(
				siteExternalReferenceCode, randomDisplayPageTemplate());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		DisplayPageTemplate displayPageTemplate2 =
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage_addDisplayPageTemplate(
				siteExternalReferenceCode, randomDisplayPageTemplate());

		for (EntityField entityField : entityFields) {
			Page<DisplayPageTemplate> page =
				displayPageTemplateResource.
					getSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage(
						siteExternalReferenceCode, null, null,
						getFilterString(
							entityField, operator, displayPageTemplate1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(displayPageTemplate1),
				(List<DisplayPageTemplate>)page.getItems());
		}
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithPagination()
		throws Exception {

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage_getSiteExternalReferenceCode();

		Page<DisplayPageTemplate> displayPageTemplatesPage =
			displayPageTemplateResource.
				getSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage(
					siteExternalReferenceCode, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			displayPageTemplatesPage.getTotalCount());

		DisplayPageTemplate displayPageTemplate1 =
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage_addDisplayPageTemplate(
				siteExternalReferenceCode, randomDisplayPageTemplate());

		DisplayPageTemplate displayPageTemplate2 =
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage_addDisplayPageTemplate(
				siteExternalReferenceCode, randomDisplayPageTemplate());

		DisplayPageTemplate displayPageTemplate3 =
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage_addDisplayPageTemplate(
				siteExternalReferenceCode, randomDisplayPageTemplate());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<DisplayPageTemplate> page1 =
				displayPageTemplateResource.
					getSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				displayPageTemplate1,
				(List<DisplayPageTemplate>)page1.getItems());

			Page<DisplayPageTemplate> page2 =
				displayPageTemplateResource.
					getSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				displayPageTemplate2,
				(List<DisplayPageTemplate>)page2.getItems());

			Page<DisplayPageTemplate> page3 =
				displayPageTemplateResource.
					getSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				displayPageTemplate3,
				(List<DisplayPageTemplate>)page3.getItems());
		}
		else {
			Page<DisplayPageTemplate> page1 =
				displayPageTemplateResource.
					getSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<DisplayPageTemplate> displayPageTemplates1 =
				(List<DisplayPageTemplate>)page1.getItems();

			Assert.assertEquals(
				displayPageTemplates1.toString(), totalCount + 2,
				displayPageTemplates1.size());

			Page<DisplayPageTemplate> page2 =
				displayPageTemplateResource.
					getSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<DisplayPageTemplate> displayPageTemplates2 =
				(List<DisplayPageTemplate>)page2.getItems();

			Assert.assertEquals(
				displayPageTemplates2.toString(), 1,
				displayPageTemplates2.size());

			Page<DisplayPageTemplate> page3 =
				displayPageTemplateResource.
					getSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				displayPageTemplate1,
				(List<DisplayPageTemplate>)page3.getItems());
			assertContains(
				displayPageTemplate2,
				(List<DisplayPageTemplate>)page3.getItems());
			assertContains(
				displayPageTemplate3,
				(List<DisplayPageTemplate>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithSortDateTime()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, displayPageTemplate1, displayPageTemplate2) -> {
				BeanTestUtil.setProperty(
					displayPageTemplate1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithSortDouble()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, displayPageTemplate1, displayPageTemplate2) -> {
				BeanTestUtil.setProperty(
					displayPageTemplate1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					displayPageTemplate2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithSortInteger()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, displayPageTemplate1, displayPageTemplate2) -> {
				BeanTestUtil.setProperty(
					displayPageTemplate1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					displayPageTemplate2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithSortString()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithSort(
			EntityField.Type.STRING,
			(entityField, displayPageTemplate1, displayPageTemplate2) -> {
				Class<?> clazz = displayPageTemplate1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						displayPageTemplate1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						displayPageTemplate2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						displayPageTemplate1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						displayPageTemplate2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						displayPageTemplate1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						displayPageTemplate2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, DisplayPageTemplate, DisplayPageTemplate,
					 Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage_getSiteExternalReferenceCode();

		DisplayPageTemplate displayPageTemplate1 = randomDisplayPageTemplate();
		DisplayPageTemplate displayPageTemplate2 = randomDisplayPageTemplate();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, displayPageTemplate1, displayPageTemplate2);
		}

		displayPageTemplate1 =
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage_addDisplayPageTemplate(
				siteExternalReferenceCode, displayPageTemplate1);

		displayPageTemplate2 =
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage_addDisplayPageTemplate(
				siteExternalReferenceCode, displayPageTemplate2);

		Page<DisplayPageTemplate> page =
			displayPageTemplateResource.
				getSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage(
					siteExternalReferenceCode, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<DisplayPageTemplate> ascPage =
				displayPageTemplateResource.
					getSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				displayPageTemplate1,
				(List<DisplayPageTemplate>)ascPage.getItems());
			assertContains(
				displayPageTemplate2,
				(List<DisplayPageTemplate>)ascPage.getItems());

			Page<DisplayPageTemplate> descPage =
				displayPageTemplateResource.
					getSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage(
						siteExternalReferenceCode, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				displayPageTemplate2,
				(List<DisplayPageTemplate>)descPage.getItems());
			assertContains(
				displayPageTemplate1,
				(List<DisplayPageTemplate>)descPage.getItems());
		}
	}

	protected DisplayPageTemplate
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage_addDisplayPageTemplate(
				String siteExternalReferenceCode,
				DisplayPageTemplate displayPageTemplate)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	@Test
	public void testPatchSiteSiteByExternalReferenceCodeDisplayPageTemplate()
		throws Exception {

		DisplayPageTemplate postDisplayPageTemplate =
			testPatchSiteSiteByExternalReferenceCodeDisplayPageTemplate_addDisplayPageTemplate();

		DisplayPageTemplate randomPatchDisplayPageTemplate =
			randomPatchDisplayPageTemplate();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		DisplayPageTemplate patchDisplayPageTemplate =
			displayPageTemplateResource.
				patchSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					null, postDisplayPageTemplate.getExternalReferenceCode(),
					randomPatchDisplayPageTemplate);

		DisplayPageTemplate expectedPatchDisplayPageTemplate =
			postDisplayPageTemplate.clone();

		BeanTestUtil.copyProperties(
			randomPatchDisplayPageTemplate, expectedPatchDisplayPageTemplate);

		DisplayPageTemplate getDisplayPageTemplate =
			displayPageTemplateResource.
				getSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					null, patchDisplayPageTemplate.getExternalReferenceCode());

		assertEquals(expectedPatchDisplayPageTemplate, getDisplayPageTemplate);
		assertValid(getDisplayPageTemplate);
	}

	protected DisplayPageTemplate
			testPatchSiteSiteByExternalReferenceCodeDisplayPageTemplate_addDisplayPageTemplate()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSiteSiteByExternalReferenceCodeDisplayPageTemplate()
		throws Exception {

		DisplayPageTemplate randomDisplayPageTemplate =
			randomDisplayPageTemplate();

		DisplayPageTemplate postDisplayPageTemplate =
			testPostSiteSiteByExternalReferenceCodeDisplayPageTemplate_addDisplayPageTemplate(
				randomDisplayPageTemplate);

		assertEquals(randomDisplayPageTemplate, postDisplayPageTemplate);
		assertValid(postDisplayPageTemplate);
	}

	protected DisplayPageTemplate
			testPostSiteSiteByExternalReferenceCodeDisplayPageTemplate_addDisplayPageTemplate(
				DisplayPageTemplate displayPageTemplate)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplate()
		throws Exception {

		DisplayPageTemplate randomDisplayPageTemplate =
			randomDisplayPageTemplate();

		DisplayPageTemplate postDisplayPageTemplate =
			testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplate_addDisplayPageTemplate(
				randomDisplayPageTemplate);

		assertEquals(randomDisplayPageTemplate, postDisplayPageTemplate);
		assertValid(postDisplayPageTemplate);
	}

	protected DisplayPageTemplate
			testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplate_addDisplayPageTemplate(
				DisplayPageTemplate displayPageTemplate)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutSiteDisplayPageTemplatePermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		DisplayPageTemplate displayPageTemplate =
			testPutSiteDisplayPageTemplatePermissionsPage_addDisplayPageTemplate();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			displayPageTemplateResource.
				putSiteDisplayPageTemplatePermissionsPageHttpResponse(
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
			displayPageTemplateResource.
				putSiteDisplayPageTemplatePermissionsPageHttpResponse(
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

	protected DisplayPageTemplate
			testPutSiteDisplayPageTemplatePermissionsPage_addDisplayPageTemplate()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutSiteSiteByExternalReferenceCodeDisplayPageTemplate()
		throws Exception {

		DisplayPageTemplate postDisplayPageTemplate =
			testPutSiteSiteByExternalReferenceCodeDisplayPageTemplate_addDisplayPageTemplate();

		DisplayPageTemplate randomDisplayPageTemplate =
			randomDisplayPageTemplate();

		DisplayPageTemplate putDisplayPageTemplate =
			displayPageTemplateResource.
				putSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testPutSiteSiteByExternalReferenceCodeDisplayPageTemplate_getSiteExternalReferenceCode(),
					postDisplayPageTemplate.getExternalReferenceCode(),
					randomDisplayPageTemplate);

		assertEquals(randomDisplayPageTemplate, putDisplayPageTemplate);
		assertValid(putDisplayPageTemplate);

		DisplayPageTemplate getDisplayPageTemplate =
			displayPageTemplateResource.
				getSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testPutSiteSiteByExternalReferenceCodeDisplayPageTemplate_getSiteExternalReferenceCode(),
					putDisplayPageTemplate.getExternalReferenceCode());

		assertEquals(randomDisplayPageTemplate, getDisplayPageTemplate);
		assertValid(getDisplayPageTemplate);
	}

	protected DisplayPageTemplate
			testPutSiteSiteByExternalReferenceCodeDisplayPageTemplate_addDisplayPageTemplate()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testPutSiteSiteByExternalReferenceCodeDisplayPageTemplate_getSiteExternalReferenceCode()
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
	public void testPostSiteSiteByExternalReferenceCodeDisplayPageTemplatePageSpecification()
		throws Exception {

		Assert.assertTrue(true);
	}

	protected DisplayPageTemplate
			testGraphQLDisplayPageTemplate_addDisplayPageTemplate()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		DisplayPageTemplate displayPageTemplate,
		List<DisplayPageTemplate> displayPageTemplates) {

		boolean contains = false;

		for (DisplayPageTemplate item : displayPageTemplates) {
			if (equals(displayPageTemplate, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			displayPageTemplates + " does not contain " + displayPageTemplate,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		DisplayPageTemplate displayPageTemplate1,
		DisplayPageTemplate displayPageTemplate2) {

		Assert.assertTrue(
			displayPageTemplate1 + " does not equal " + displayPageTemplate2,
			equals(displayPageTemplate1, displayPageTemplate2));
	}

	protected void assertEquals(
		List<DisplayPageTemplate> displayPageTemplates1,
		List<DisplayPageTemplate> displayPageTemplates2) {

		Assert.assertEquals(
			displayPageTemplates1.size(), displayPageTemplates2.size());

		for (int i = 0; i < displayPageTemplates1.size(); i++) {
			DisplayPageTemplate displayPageTemplate1 =
				displayPageTemplates1.get(i);
			DisplayPageTemplate displayPageTemplate2 =
				displayPageTemplates2.get(i);

			assertEquals(displayPageTemplate1, displayPageTemplate2);
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
		List<DisplayPageTemplate> displayPageTemplates1,
		List<DisplayPageTemplate> displayPageTemplates2) {

		Assert.assertEquals(
			displayPageTemplates1.size(), displayPageTemplates2.size());

		for (DisplayPageTemplate displayPageTemplate1 : displayPageTemplates1) {
			boolean contains = false;

			for (DisplayPageTemplate displayPageTemplate2 :
					displayPageTemplates2) {

				if (equals(displayPageTemplate1, displayPageTemplate2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				displayPageTemplates2 + " does not contain " +
					displayPageTemplate1,
				contains);
		}
	}

	protected void assertValid(DisplayPageTemplate displayPageTemplate)
		throws Exception {

		boolean valid = true;

		if (displayPageTemplate.getDateCreated() == null) {
			valid = false;
		}

		if (displayPageTemplate.getDateModified() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"contentTypeReference", additionalAssertFieldName)) {

				if (displayPageTemplate.getContentTypeReference() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (displayPageTemplate.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"creatorExternalReferenceCode",
					additionalAssertFieldName)) {

				if (displayPageTemplate.getCreatorExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("datePublished", additionalAssertFieldName)) {
				if (displayPageTemplate.getDatePublished() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"displayPageTemplateSettings", additionalAssertFieldName)) {

				if (displayPageTemplate.getDisplayPageTemplateSettings() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (displayPageTemplate.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"friendlyUrlHistory", additionalAssertFieldName)) {

				if (displayPageTemplate.getFriendlyUrlHistory() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"friendlyUrlPath_i18n", additionalAssertFieldName)) {

				if (displayPageTemplate.getFriendlyUrlPath_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (displayPageTemplate.getKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("markedAsDefault", additionalAssertFieldName)) {
				if (displayPageTemplate.getMarkedAsDefault() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (displayPageTemplate.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"pageSpecifications", additionalAssertFieldName)) {

				if (displayPageTemplate.getPageSpecifications() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("parentFolder", additionalAssertFieldName)) {
				if (displayPageTemplate.getParentFolder() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("thumbnail", additionalAssertFieldName)) {
				if (displayPageTemplate.getThumbnail() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("uuid", additionalAssertFieldName)) {
				if (displayPageTemplate.getUuid() == null) {
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

	protected void assertValid(Page<DisplayPageTemplate> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<DisplayPageTemplate> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<DisplayPageTemplate> displayPageTemplates =
			page.getItems();

		int size = displayPageTemplates.size();

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
					com.liferay.headless.admin.site.dto.v1_0.
						DisplayPageTemplate.class)) {

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
		DisplayPageTemplate displayPageTemplate1,
		DisplayPageTemplate displayPageTemplate2) {

		if (displayPageTemplate1 == displayPageTemplate2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"contentTypeReference", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						displayPageTemplate1.getContentTypeReference(),
						displayPageTemplate2.getContentTypeReference())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						displayPageTemplate1.getCreator(),
						displayPageTemplate2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"creatorExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						displayPageTemplate1.getCreatorExternalReferenceCode(),
						displayPageTemplate2.
							getCreatorExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						displayPageTemplate1.getDateCreated(),
						displayPageTemplate2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						displayPageTemplate1.getDateModified(),
						displayPageTemplate2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("datePublished", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						displayPageTemplate1.getDatePublished(),
						displayPageTemplate2.getDatePublished())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"displayPageTemplateSettings", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						displayPageTemplate1.getDisplayPageTemplateSettings(),
						displayPageTemplate2.
							getDisplayPageTemplateSettings())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						displayPageTemplate1.getExternalReferenceCode(),
						displayPageTemplate2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"friendlyUrlHistory", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						displayPageTemplate1.getFriendlyUrlHistory(),
						displayPageTemplate2.getFriendlyUrlHistory())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"friendlyUrlPath_i18n", additionalAssertFieldName)) {

				if (!equals(
						(Map)displayPageTemplate1.getFriendlyUrlPath_i18n(),
						(Map)displayPageTemplate2.getFriendlyUrlPath_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						displayPageTemplate1.getKey(),
						displayPageTemplate2.getKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("markedAsDefault", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						displayPageTemplate1.getMarkedAsDefault(),
						displayPageTemplate2.getMarkedAsDefault())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						displayPageTemplate1.getName(),
						displayPageTemplate2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"pageSpecifications", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						displayPageTemplate1.getPageSpecifications(),
						displayPageTemplate2.getPageSpecifications())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("parentFolder", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						displayPageTemplate1.getParentFolder(),
						displayPageTemplate2.getParentFolder())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("thumbnail", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						displayPageTemplate1.getThumbnail(),
						displayPageTemplate2.getThumbnail())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("uuid", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						displayPageTemplate1.getUuid(),
						displayPageTemplate2.getUuid())) {

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

		if (!(_displayPageTemplateResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_displayPageTemplateResource;

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
		DisplayPageTemplate displayPageTemplate) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("contentTypeReference")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("creatorExternalReferenceCode")) {
			Object object =
				displayPageTemplate.getCreatorExternalReferenceCode();

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
				Date date = displayPageTemplate.getDateCreated();

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

				sb.append(_format.format(displayPageTemplate.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = displayPageTemplate.getDateModified();

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

				sb.append(
					_format.format(displayPageTemplate.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("datePublished")) {
			if (operator.equals("between")) {
				Date date = displayPageTemplate.getDatePublished();

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

				sb.append(
					_format.format(displayPageTemplate.getDatePublished()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("displayPageTemplateSettings")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = displayPageTemplate.getExternalReferenceCode();

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

		if (entityFieldName.equals("key")) {
			Object object = displayPageTemplate.getKey();

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

		if (entityFieldName.equals("markedAsDefault")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("name")) {
			Object object = displayPageTemplate.getName();

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

		if (entityFieldName.equals("parentFolder")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("thumbnail")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("uuid")) {
			Object object = displayPageTemplate.getUuid();

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

	protected DisplayPageTemplate randomDisplayPageTemplate() throws Exception {
		return new DisplayPageTemplate() {
			{
				creatorExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
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

	protected DisplayPageTemplate randomIrrelevantDisplayPageTemplate()
		throws Exception {

		DisplayPageTemplate randomIrrelevantDisplayPageTemplate =
			randomDisplayPageTemplate();

		return randomIrrelevantDisplayPageTemplate;
	}

	protected DisplayPageTemplate randomPatchDisplayPageTemplate()
		throws Exception {

		return randomDisplayPageTemplate();
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

	protected DisplayPageTemplateResource displayPageTemplateResource;
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
		LogFactoryUtil.getLog(BaseDisplayPageTemplateResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.admin.site.resource.v1_0.
			DisplayPageTemplateResource _displayPageTemplateResource;

}