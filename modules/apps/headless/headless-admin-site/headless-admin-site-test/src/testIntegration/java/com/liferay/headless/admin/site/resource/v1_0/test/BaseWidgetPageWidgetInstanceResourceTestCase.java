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

import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageWidgetInstance;
import com.liferay.headless.admin.site.client.http.HttpInvoker;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.resource.v1_0.WidgetPageWidgetInstanceResource;
import com.liferay.headless.admin.site.client.serdes.v1_0.WidgetPageWidgetInstanceSerDes;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
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
public abstract class BaseWidgetPageWidgetInstanceResourceTestCase {

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

		_widgetPageWidgetInstanceResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		widgetPageWidgetInstanceResource =
			WidgetPageWidgetInstanceResource.builder(
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

		WidgetPageWidgetInstance widgetPageWidgetInstance1 =
			randomWidgetPageWidgetInstance();

		String json = objectMapper.writeValueAsString(
			widgetPageWidgetInstance1);

		WidgetPageWidgetInstance widgetPageWidgetInstance2 =
			WidgetPageWidgetInstanceSerDes.toDTO(json);

		Assert.assertTrue(
			equals(widgetPageWidgetInstance1, widgetPageWidgetInstance2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		WidgetPageWidgetInstance widgetPageWidgetInstance =
			randomWidgetPageWidgetInstance();

		String json1 = objectMapper.writeValueAsString(
			widgetPageWidgetInstance);
		String json2 = WidgetPageWidgetInstanceSerDes.toJSON(
			widgetPageWidgetInstance);

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

		WidgetPageWidgetInstance widgetPageWidgetInstance =
			randomWidgetPageWidgetInstance();

		widgetPageWidgetInstance.setExternalReferenceCode(regex);
		widgetPageWidgetInstance.setParentSectionId(regex);
		widgetPageWidgetInstance.setParentWidgetInstanceExternalReferenceCode(
			regex);
		widgetPageWidgetInstance.setWidgetInstanceId(regex);
		widgetPageWidgetInstance.setWidgetName(regex);

		String json = WidgetPageWidgetInstanceSerDes.toJSON(
			widgetPageWidgetInstance);

		Assert.assertFalse(json.contains(regex));

		widgetPageWidgetInstance = WidgetPageWidgetInstanceSerDes.toDTO(json);

		Assert.assertEquals(
			regex, widgetPageWidgetInstance.getExternalReferenceCode());
		Assert.assertEquals(
			regex, widgetPageWidgetInstance.getParentSectionId());
		Assert.assertEquals(
			regex,
			widgetPageWidgetInstance.
				getParentWidgetInstanceExternalReferenceCode());
		Assert.assertEquals(
			regex, widgetPageWidgetInstance.getWidgetInstanceId());
		Assert.assertEquals(regex, widgetPageWidgetInstance.getWidgetName());
	}

	@Test
	public void testDeleteSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		WidgetPageWidgetInstance widgetPageWidgetInstance =
			testDeleteSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_addWidgetPageWidgetInstance();

		assertHttpResponseStatusCode(
			204,
			widgetPageWidgetInstanceResource.
				deleteSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCodeHttpResponse(
					testDeleteSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getSiteExternalReferenceCode(),
					testDeleteSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getSitePageExternalReferenceCode(),
					testDeleteSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getWidgetInstanceExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			widgetPageWidgetInstanceResource.
				getSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCodeHttpResponse(
					testDeleteSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getSiteExternalReferenceCode(),
					testDeleteSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getSitePageExternalReferenceCode(),
					testDeleteSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getWidgetInstanceExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			widgetPageWidgetInstanceResource.
				getSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCodeHttpResponse(
					testDeleteSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getSiteExternalReferenceCode(),
					testDeleteSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getSitePageExternalReferenceCode(),
					testDeleteSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getWidgetInstanceExternalReferenceCode()));
	}

	protected WidgetPageWidgetInstance
			testDeleteSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_addWidgetPageWidgetInstance()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getSiteExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getSitePageExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getWidgetInstanceExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeSitePageWidgetInstancesPage()
		throws Exception {

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeSitePageWidgetInstancesPage_getSiteExternalReferenceCode();
		String irrelevantSiteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeSitePageWidgetInstancesPage_getIrrelevantSiteExternalReferenceCode();
		String sitePageExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeSitePageWidgetInstancesPage_getSitePageExternalReferenceCode();
		String irrelevantSitePageExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeSitePageWidgetInstancesPage_getIrrelevantSitePageExternalReferenceCode();

		Page<WidgetPageWidgetInstance> page =
			widgetPageWidgetInstanceResource.
				getSiteSiteByExternalReferenceCodeSitePageWidgetInstancesPage(
					siteExternalReferenceCode, sitePageExternalReferenceCode);

		long totalCount = page.getTotalCount();

		if ((irrelevantSiteExternalReferenceCode != null) &&
			(irrelevantSitePageExternalReferenceCode != null)) {

			WidgetPageWidgetInstance irrelevantWidgetPageWidgetInstance =
				testGetSiteSiteByExternalReferenceCodeSitePageWidgetInstancesPage_addWidgetPageWidgetInstance(
					irrelevantSiteExternalReferenceCode,
					irrelevantSitePageExternalReferenceCode,
					randomIrrelevantWidgetPageWidgetInstance());

			page =
				widgetPageWidgetInstanceResource.
					getSiteSiteByExternalReferenceCodeSitePageWidgetInstancesPage(
						irrelevantSiteExternalReferenceCode,
						irrelevantSitePageExternalReferenceCode);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantWidgetPageWidgetInstance,
				(List<WidgetPageWidgetInstance>)page.getItems());
			assertValid(
				page,
				testGetSiteSiteByExternalReferenceCodeSitePageWidgetInstancesPage_getExpectedActions(
					irrelevantSiteExternalReferenceCode,
					irrelevantSitePageExternalReferenceCode));
		}

		WidgetPageWidgetInstance widgetPageWidgetInstance1 =
			testGetSiteSiteByExternalReferenceCodeSitePageWidgetInstancesPage_addWidgetPageWidgetInstance(
				siteExternalReferenceCode, sitePageExternalReferenceCode,
				randomWidgetPageWidgetInstance());

		WidgetPageWidgetInstance widgetPageWidgetInstance2 =
			testGetSiteSiteByExternalReferenceCodeSitePageWidgetInstancesPage_addWidgetPageWidgetInstance(
				siteExternalReferenceCode, sitePageExternalReferenceCode,
				randomWidgetPageWidgetInstance());

		page =
			widgetPageWidgetInstanceResource.
				getSiteSiteByExternalReferenceCodeSitePageWidgetInstancesPage(
					siteExternalReferenceCode, sitePageExternalReferenceCode);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			widgetPageWidgetInstance1,
			(List<WidgetPageWidgetInstance>)page.getItems());
		assertContains(
			widgetPageWidgetInstance2,
			(List<WidgetPageWidgetInstance>)page.getItems());
		assertValid(
			page,
			testGetSiteSiteByExternalReferenceCodeSitePageWidgetInstancesPage_getExpectedActions(
				siteExternalReferenceCode, sitePageExternalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetSiteSiteByExternalReferenceCodeSitePageWidgetInstancesPage_getExpectedActions(
				String siteExternalReferenceCode,
				String sitePageExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected WidgetPageWidgetInstance
			testGetSiteSiteByExternalReferenceCodeSitePageWidgetInstancesPage_addWidgetPageWidgetInstance(
				String siteExternalReferenceCode,
				String sitePageExternalReferenceCode,
				WidgetPageWidgetInstance widgetPageWidgetInstance)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteSiteByExternalReferenceCodeSitePageWidgetInstancesPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteSiteByExternalReferenceCodeSitePageWidgetInstancesPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteSiteByExternalReferenceCodeSitePageWidgetInstancesPage_getSitePageExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteSiteByExternalReferenceCodeSitePageWidgetInstancesPage_getIrrelevantSitePageExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode()
		throws Exception {

		WidgetPageWidgetInstance postWidgetPageWidgetInstance =
			testGetSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_addWidgetPageWidgetInstance();

		WidgetPageWidgetInstance getWidgetPageWidgetInstance =
			widgetPageWidgetInstanceResource.
				getSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode(
					testGetSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getSiteExternalReferenceCode(),
					testGetSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getSitePageExternalReferenceCode(),
					testGetSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getWidgetInstanceExternalReferenceCode());

		assertEquals(postWidgetPageWidgetInstance, getWidgetPageWidgetInstance);
		assertValid(getWidgetPageWidgetInstance);
	}

	protected WidgetPageWidgetInstance
			testGetSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_addWidgetPageWidgetInstance()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getSiteExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getSitePageExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getWidgetInstanceExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode()
		throws Exception {

		WidgetPageWidgetInstance widgetPageWidgetInstance =
			testGraphQLGetSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_addWidgetPageWidgetInstance();

		// No namespace

		Assert.assertTrue(
			equals(
				widgetPageWidgetInstance,
				WidgetPageWidgetInstanceSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"siteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"siteExternalReferenceCode",
											"\"" +
												testGraphQLGetSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getSiteExternalReferenceCode() +
													"\"");

										put(
											"sitePageExternalReferenceCode",
											"\"" +
												testGraphQLGetSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getSitePageExternalReferenceCode() +
													"\"");

										put(
											"widgetInstanceExternalReferenceCode",
											"\"" +
												testGraphQLGetSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getWidgetInstanceExternalReferenceCode() +
													"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/siteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode"))));

		// Using the namespace headlessAdminSite_v1_0

		Assert.assertTrue(
			equals(
				widgetPageWidgetInstance,
				WidgetPageWidgetInstanceSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminSite_v1_0",
								new GraphQLField(
									"siteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"siteExternalReferenceCode",
												"\"" +
													testGraphQLGetSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getSiteExternalReferenceCode() +
														"\"");

											put(
												"sitePageExternalReferenceCode",
												"\"" +
													testGraphQLGetSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getSitePageExternalReferenceCode() +
														"\"");

											put(
												"widgetInstanceExternalReferenceCode",
												"\"" +
													testGraphQLGetSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getWidgetInstanceExternalReferenceCode() +
														"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessAdminSite_v1_0",
						"Object/siteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode"))));
	}

	protected String
			testGraphQLGetSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getSiteExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGraphQLGetSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getSitePageExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGraphQLGetSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getWidgetInstanceExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantSitePageExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";
		String irrelevantWidgetInstanceExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"siteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode",
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
								put(
									"widgetInstanceExternalReferenceCode",
									irrelevantWidgetInstanceExternalReferenceCode);
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
							"siteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode",
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
									put(
										"widgetInstanceExternalReferenceCode",
										irrelevantWidgetInstanceExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected WidgetPageWidgetInstance
			testGraphQLGetSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_addWidgetPageWidgetInstance()
		throws Exception {

		return testGraphQLWidgetPageWidgetInstance_addWidgetPageWidgetInstance();
	}

	@Test
	public void testPatchSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode()
		throws Exception {

		WidgetPageWidgetInstance postWidgetPageWidgetInstance =
			testPatchSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_addWidgetPageWidgetInstance();

		WidgetPageWidgetInstance randomPatchWidgetPageWidgetInstance =
			randomPatchWidgetPageWidgetInstance();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		WidgetPageWidgetInstance patchWidgetPageWidgetInstance =
			widgetPageWidgetInstanceResource.
				patchSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode(
					null, null, null, randomPatchWidgetPageWidgetInstance);

		WidgetPageWidgetInstance expectedPatchWidgetPageWidgetInstance =
			postWidgetPageWidgetInstance.clone();

		BeanTestUtil.copyProperties(
			randomPatchWidgetPageWidgetInstance,
			expectedPatchWidgetPageWidgetInstance);

		WidgetPageWidgetInstance getWidgetPageWidgetInstance =
			widgetPageWidgetInstanceResource.
				getSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode(
					null, null, null);

		assertEquals(
			expectedPatchWidgetPageWidgetInstance, getWidgetPageWidgetInstance);
		assertValid(getWidgetPageWidgetInstance);
	}

	protected WidgetPageWidgetInstance
			testPatchSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_addWidgetPageWidgetInstance()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSiteSiteByExternalReferenceCodeSitePageWidgetInstance()
		throws Exception {

		WidgetPageWidgetInstance randomWidgetPageWidgetInstance =
			randomWidgetPageWidgetInstance();

		WidgetPageWidgetInstance postWidgetPageWidgetInstance =
			testPostSiteSiteByExternalReferenceCodeSitePageWidgetInstance_addWidgetPageWidgetInstance(
				randomWidgetPageWidgetInstance);

		assertEquals(
			randomWidgetPageWidgetInstance, postWidgetPageWidgetInstance);
		assertValid(postWidgetPageWidgetInstance);
	}

	protected WidgetPageWidgetInstance
			testPostSiteSiteByExternalReferenceCodeSitePageWidgetInstance_addWidgetPageWidgetInstance(
				WidgetPageWidgetInstance widgetPageWidgetInstance)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode()
		throws Exception {

		WidgetPageWidgetInstance postWidgetPageWidgetInstance =
			testPutSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_addWidgetPageWidgetInstance();

		WidgetPageWidgetInstance randomWidgetPageWidgetInstance =
			randomWidgetPageWidgetInstance();

		WidgetPageWidgetInstance putWidgetPageWidgetInstance =
			widgetPageWidgetInstanceResource.
				putSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode(
					testPutSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getSiteExternalReferenceCode(),
					testPutSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getSitePageExternalReferenceCode(),
					testPutSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getWidgetInstanceExternalReferenceCode(),
					randomWidgetPageWidgetInstance);

		assertEquals(
			randomWidgetPageWidgetInstance, putWidgetPageWidgetInstance);
		assertValid(putWidgetPageWidgetInstance);

		WidgetPageWidgetInstance getWidgetPageWidgetInstance =
			widgetPageWidgetInstanceResource.
				getSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode(
					testPutSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getSiteExternalReferenceCode(),
					testPutSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getSitePageExternalReferenceCode(),
					testPutSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getWidgetInstanceExternalReferenceCode());

		assertEquals(
			randomWidgetPageWidgetInstance, getWidgetPageWidgetInstance);
		assertValid(getWidgetPageWidgetInstance);
	}

	protected WidgetPageWidgetInstance
			testPutSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_addWidgetPageWidgetInstance()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testPutSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getSiteExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testPutSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getSitePageExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testPutSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode_getWidgetInstanceExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected WidgetPageWidgetInstance
			testGraphQLWidgetPageWidgetInstance_addWidgetPageWidgetInstance()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		WidgetPageWidgetInstance widgetPageWidgetInstance,
		List<WidgetPageWidgetInstance> widgetPageWidgetInstances) {

		boolean contains = false;

		for (WidgetPageWidgetInstance item : widgetPageWidgetInstances) {
			if (equals(widgetPageWidgetInstance, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			widgetPageWidgetInstances + " does not contain " +
				widgetPageWidgetInstance,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		WidgetPageWidgetInstance widgetPageWidgetInstance1,
		WidgetPageWidgetInstance widgetPageWidgetInstance2) {

		Assert.assertTrue(
			widgetPageWidgetInstance1 + " does not equal " +
				widgetPageWidgetInstance2,
			equals(widgetPageWidgetInstance1, widgetPageWidgetInstance2));
	}

	protected void assertEquals(
		List<WidgetPageWidgetInstance> widgetPageWidgetInstances1,
		List<WidgetPageWidgetInstance> widgetPageWidgetInstances2) {

		Assert.assertEquals(
			widgetPageWidgetInstances1.size(),
			widgetPageWidgetInstances2.size());

		for (int i = 0; i < widgetPageWidgetInstances1.size(); i++) {
			WidgetPageWidgetInstance widgetPageWidgetInstance1 =
				widgetPageWidgetInstances1.get(i);
			WidgetPageWidgetInstance widgetPageWidgetInstance2 =
				widgetPageWidgetInstances2.get(i);

			assertEquals(widgetPageWidgetInstance1, widgetPageWidgetInstance2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<WidgetPageWidgetInstance> widgetPageWidgetInstances1,
		List<WidgetPageWidgetInstance> widgetPageWidgetInstances2) {

		Assert.assertEquals(
			widgetPageWidgetInstances1.size(),
			widgetPageWidgetInstances2.size());

		for (WidgetPageWidgetInstance widgetPageWidgetInstance1 :
				widgetPageWidgetInstances1) {

			boolean contains = false;

			for (WidgetPageWidgetInstance widgetPageWidgetInstance2 :
					widgetPageWidgetInstances2) {

				if (equals(
						widgetPageWidgetInstance1, widgetPageWidgetInstance2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				widgetPageWidgetInstances2 + " does not contain " +
					widgetPageWidgetInstance1,
				contains);
		}
	}

	protected void assertValid(
			WidgetPageWidgetInstance widgetPageWidgetInstance)
		throws Exception {

		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (widgetPageWidgetInstance.getExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("parentSectionId", additionalAssertFieldName)) {
				if (widgetPageWidgetInstance.getParentSectionId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"parentWidgetInstanceExternalReferenceCode",
					additionalAssertFieldName)) {

				if (widgetPageWidgetInstance.
						getParentWidgetInstanceExternalReferenceCode() ==
							null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("position", additionalAssertFieldName)) {
				if (widgetPageWidgetInstance.getPosition() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("widgetConfig", additionalAssertFieldName)) {
				if (widgetPageWidgetInstance.getWidgetConfig() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("widgetInstanceId", additionalAssertFieldName)) {
				if (widgetPageWidgetInstance.getWidgetInstanceId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"widgetLookAndFeelConfig", additionalAssertFieldName)) {

				if (widgetPageWidgetInstance.getWidgetLookAndFeelConfig() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("widgetName", additionalAssertFieldName)) {
				if (widgetPageWidgetInstance.getWidgetName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"widgetPermissions", additionalAssertFieldName)) {

				if (widgetPageWidgetInstance.getWidgetPermissions() == null) {
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

	protected void assertValid(Page<WidgetPageWidgetInstance> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<WidgetPageWidgetInstance> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<WidgetPageWidgetInstance>
			widgetPageWidgetInstances = page.getItems();

		int size = widgetPageWidgetInstances.size();

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

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.admin.site.dto.v1_0.
						WidgetPageWidgetInstance.class)) {

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
		WidgetPageWidgetInstance widgetPageWidgetInstance1,
		WidgetPageWidgetInstance widgetPageWidgetInstance2) {

		if (widgetPageWidgetInstance1 == widgetPageWidgetInstance2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						widgetPageWidgetInstance1.getExternalReferenceCode(),
						widgetPageWidgetInstance2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("parentSectionId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						widgetPageWidgetInstance1.getParentSectionId(),
						widgetPageWidgetInstance2.getParentSectionId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"parentWidgetInstanceExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						widgetPageWidgetInstance1.
							getParentWidgetInstanceExternalReferenceCode(),
						widgetPageWidgetInstance2.
							getParentWidgetInstanceExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("position", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						widgetPageWidgetInstance1.getPosition(),
						widgetPageWidgetInstance2.getPosition())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("widgetConfig", additionalAssertFieldName)) {
				if (!equals(
						(Map)widgetPageWidgetInstance1.getWidgetConfig(),
						(Map)widgetPageWidgetInstance2.getWidgetConfig())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("widgetInstanceId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						widgetPageWidgetInstance1.getWidgetInstanceId(),
						widgetPageWidgetInstance2.getWidgetInstanceId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"widgetLookAndFeelConfig", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						widgetPageWidgetInstance1.getWidgetLookAndFeelConfig(),
						widgetPageWidgetInstance2.
							getWidgetLookAndFeelConfig())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("widgetName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						widgetPageWidgetInstance1.getWidgetName(),
						widgetPageWidgetInstance2.getWidgetName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"widgetPermissions", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						widgetPageWidgetInstance1.getWidgetPermissions(),
						widgetPageWidgetInstance2.getWidgetPermissions())) {

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

		if (!(_widgetPageWidgetInstanceResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_widgetPageWidgetInstanceResource;

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
		WidgetPageWidgetInstance widgetPageWidgetInstance) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = widgetPageWidgetInstance.getExternalReferenceCode();

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

		if (entityFieldName.equals("parentSectionId")) {
			Object object = widgetPageWidgetInstance.getParentSectionId();

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

		if (entityFieldName.equals(
				"parentWidgetInstanceExternalReferenceCode")) {

			Object object =
				widgetPageWidgetInstance.
					getParentWidgetInstanceExternalReferenceCode();

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

		if (entityFieldName.equals("position")) {
			sb.append(String.valueOf(widgetPageWidgetInstance.getPosition()));

			return sb.toString();
		}

		if (entityFieldName.equals("widgetConfig")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("widgetInstanceId")) {
			Object object = widgetPageWidgetInstance.getWidgetInstanceId();

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

		if (entityFieldName.equals("widgetLookAndFeelConfig")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("widgetName")) {
			Object object = widgetPageWidgetInstance.getWidgetName();

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

		if (entityFieldName.equals("widgetPermissions")) {
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

	protected WidgetPageWidgetInstance randomWidgetPageWidgetInstance()
		throws Exception {

		return new WidgetPageWidgetInstance() {
			{
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				parentSectionId = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				parentWidgetInstanceExternalReferenceCode =
					StringUtil.toLowerCase(RandomTestUtil.randomString());
				position = RandomTestUtil.randomInt();
				widgetInstanceId = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				widgetName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected WidgetPageWidgetInstance
			randomIrrelevantWidgetPageWidgetInstance()
		throws Exception {

		WidgetPageWidgetInstance randomIrrelevantWidgetPageWidgetInstance =
			randomWidgetPageWidgetInstance();

		return randomIrrelevantWidgetPageWidgetInstance;
	}

	protected WidgetPageWidgetInstance randomPatchWidgetPageWidgetInstance()
		throws Exception {

		return randomWidgetPageWidgetInstance();
	}

	protected WidgetPageWidgetInstanceResource widgetPageWidgetInstanceResource;
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
		LogFactoryUtil.getLog(
			BaseWidgetPageWidgetInstanceResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.admin.site.resource.v1_0.
		WidgetPageWidgetInstanceResource _widgetPageWidgetInstanceResource;

}