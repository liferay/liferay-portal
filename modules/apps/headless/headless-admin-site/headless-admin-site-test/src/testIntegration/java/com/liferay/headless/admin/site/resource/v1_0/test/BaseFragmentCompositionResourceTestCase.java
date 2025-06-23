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

import com.liferay.headless.admin.site.client.dto.v1_0.FragmentComposition;
import com.liferay.headless.admin.site.client.http.HttpInvoker;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.pagination.Pagination;
import com.liferay.headless.admin.site.client.resource.v1_0.FragmentCompositionResource;
import com.liferay.headless.admin.site.client.serdes.v1_0.FragmentCompositionSerDes;
import com.liferay.petra.function.UnsafeTriConsumer;
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
public abstract class BaseFragmentCompositionResourceTestCase {

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

		_fragmentCompositionResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		fragmentCompositionResource = FragmentCompositionResource.builder(
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

		FragmentComposition fragmentComposition1 = randomFragmentComposition();

		String json = objectMapper.writeValueAsString(fragmentComposition1);

		FragmentComposition fragmentComposition2 =
			FragmentCompositionSerDes.toDTO(json);

		Assert.assertTrue(equals(fragmentComposition1, fragmentComposition2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		FragmentComposition fragmentComposition = randomFragmentComposition();

		String json1 = objectMapper.writeValueAsString(fragmentComposition);
		String json2 = FragmentCompositionSerDes.toJSON(fragmentComposition);

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

		FragmentComposition fragmentComposition = randomFragmentComposition();

		fragmentComposition.setCreatorExternalReferenceCode(regex);
		fragmentComposition.setDescription(regex);
		fragmentComposition.setExternalReferenceCode(regex);
		fragmentComposition.setFragmentSetExternalReferenceCode(regex);
		fragmentComposition.setKey(regex);
		fragmentComposition.setName(regex);

		String json = FragmentCompositionSerDes.toJSON(fragmentComposition);

		Assert.assertFalse(json.contains(regex));

		fragmentComposition = FragmentCompositionSerDes.toDTO(json);

		Assert.assertEquals(
			regex, fragmentComposition.getCreatorExternalReferenceCode());
		Assert.assertEquals(regex, fragmentComposition.getDescription());
		Assert.assertEquals(
			regex, fragmentComposition.getExternalReferenceCode());
		Assert.assertEquals(
			regex, fragmentComposition.getFragmentSetExternalReferenceCode());
		Assert.assertEquals(regex, fragmentComposition.getKey());
		Assert.assertEquals(regex, fragmentComposition.getName());
	}

	@Test
	public void testDeleteSiteSiteByExternalReferenceCodeFragmentComposition()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		FragmentComposition fragmentComposition =
			testDeleteSiteSiteByExternalReferenceCodeFragmentComposition_addFragmentComposition();

		assertHttpResponseStatusCode(
			204,
			fragmentCompositionResource.
				deleteSiteSiteByExternalReferenceCodeFragmentCompositionHttpResponse(
					testDeleteSiteSiteByExternalReferenceCodeFragmentComposition_getSiteExternalReferenceCode(),
					fragmentComposition.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			fragmentCompositionResource.
				getSiteSiteByExternalReferenceCodeFragmentCompositionHttpResponse(
					testDeleteSiteSiteByExternalReferenceCodeFragmentComposition_getSiteExternalReferenceCode(),
					fragmentComposition.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			fragmentCompositionResource.
				getSiteSiteByExternalReferenceCodeFragmentCompositionHttpResponse(
					testDeleteSiteSiteByExternalReferenceCodeFragmentComposition_getSiteExternalReferenceCode(),
					"-"));
	}

	protected FragmentComposition
			testDeleteSiteSiteByExternalReferenceCodeFragmentComposition_addFragmentComposition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteSiteSiteByExternalReferenceCodeFragmentComposition_getSiteExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeFragmentComposition()
		throws Exception {

		FragmentComposition postFragmentComposition =
			testGetSiteSiteByExternalReferenceCodeFragmentComposition_addFragmentComposition();

		FragmentComposition getFragmentComposition =
			fragmentCompositionResource.
				getSiteSiteByExternalReferenceCodeFragmentComposition(
					testGetSiteSiteByExternalReferenceCodeFragmentComposition_getSiteExternalReferenceCode(),
					postFragmentComposition.getExternalReferenceCode());

		assertEquals(postFragmentComposition, getFragmentComposition);
		assertValid(getFragmentComposition);
	}

	protected FragmentComposition
			testGetSiteSiteByExternalReferenceCodeFragmentComposition_addFragmentComposition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteSiteByExternalReferenceCodeFragmentComposition_getSiteExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteSiteByExternalReferenceCodeFragmentComposition()
		throws Exception {

		FragmentComposition fragmentComposition =
			testGraphQLGetSiteSiteByExternalReferenceCodeFragmentComposition_addFragmentComposition();

		// No namespace

		Assert.assertTrue(
			equals(
				fragmentComposition,
				FragmentCompositionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"siteByExternalReferenceCodeFragmentComposition",
								new HashMap<String, Object>() {
									{
										put(
											"siteExternalReferenceCode",
											"\"" +
												testGraphQLGetSiteSiteByExternalReferenceCodeFragmentComposition_getSiteExternalReferenceCode() +
													"\"");
										put(
											"fragmentCompositionExternalReferenceCode",
											"\"" +
												fragmentComposition.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/siteByExternalReferenceCodeFragmentComposition"))));

		// Using the namespace headlessAdminSite_v1_0

		Assert.assertTrue(
			equals(
				fragmentComposition,
				FragmentCompositionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminSite_v1_0",
								new GraphQLField(
									"siteByExternalReferenceCodeFragmentComposition",
									new HashMap<String, Object>() {
										{
											put(
												"siteExternalReferenceCode",
												"\"" +
													testGraphQLGetSiteSiteByExternalReferenceCodeFragmentComposition_getSiteExternalReferenceCode() +
														"\"");
											put(
												"fragmentCompositionExternalReferenceCode",
												"\"" +
													fragmentComposition.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessAdminSite_v1_0",
						"Object/siteByExternalReferenceCodeFragmentComposition"))));
	}

	protected String
			testGraphQLGetSiteSiteByExternalReferenceCodeFragmentComposition_getSiteExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteSiteByExternalReferenceCodeFragmentCompositionNotFound()
		throws Exception {

		String irrelevantFragmentCompositionExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"siteByExternalReferenceCodeFragmentComposition",
						new HashMap<String, Object>() {
							{
								put(
									"siteExternalReferenceCode",
									"\"" +
										irrelevantGroup.
											getExternalReferenceCode() + "\"");
								put(
									"fragmentCompositionExternalReferenceCode",
									irrelevantFragmentCompositionExternalReferenceCode);
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
							"siteByExternalReferenceCodeFragmentComposition",
							new HashMap<String, Object>() {
								{
									put(
										"siteExternalReferenceCode",
										"\"" +
											irrelevantGroup.
												getExternalReferenceCode() +
													"\"");
									put(
										"fragmentCompositionExternalReferenceCode",
										irrelevantFragmentCompositionExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected FragmentComposition
			testGraphQLGetSiteSiteByExternalReferenceCodeFragmentComposition_addFragmentComposition()
		throws Exception {

		return testGraphQLFragmentComposition_addFragmentComposition();
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPage()
		throws Exception {

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPage_getSiteExternalReferenceCode();
		String irrelevantSiteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPage_getIrrelevantSiteExternalReferenceCode();

		Page<FragmentComposition> page =
			fragmentCompositionResource.
				getSiteSiteByExternalReferenceCodeFragmentCompositionsPage(
					siteExternalReferenceCode, null, null, Pagination.of(1, 10),
					null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteExternalReferenceCode != null) {
			FragmentComposition irrelevantFragmentComposition =
				testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPage_addFragmentComposition(
					irrelevantSiteExternalReferenceCode,
					randomIrrelevantFragmentComposition());

			page =
				fragmentCompositionResource.
					getSiteSiteByExternalReferenceCodeFragmentCompositionsPage(
						irrelevantSiteExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantFragmentComposition,
				(List<FragmentComposition>)page.getItems());
			assertValid(
				page,
				testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPage_getExpectedActions(
					irrelevantSiteExternalReferenceCode));
		}

		FragmentComposition fragmentComposition1 =
			testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPage_addFragmentComposition(
				siteExternalReferenceCode, randomFragmentComposition());

		FragmentComposition fragmentComposition2 =
			testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPage_addFragmentComposition(
				siteExternalReferenceCode, randomFragmentComposition());

		page =
			fragmentCompositionResource.
				getSiteSiteByExternalReferenceCodeFragmentCompositionsPage(
					siteExternalReferenceCode, null, null, Pagination.of(1, 10),
					null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			fragmentComposition1, (List<FragmentComposition>)page.getItems());
		assertContains(
			fragmentComposition2, (List<FragmentComposition>)page.getItems());
		assertValid(
			page,
			testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPage_getExpectedActions(
				siteExternalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPage_getExpectedActions(
				String siteExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPage_getSiteExternalReferenceCode();

		FragmentComposition fragmentComposition1 = randomFragmentComposition();

		fragmentComposition1 =
			testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPage_addFragmentComposition(
				siteExternalReferenceCode, fragmentComposition1);

		for (EntityField entityField : entityFields) {
			Page<FragmentComposition> page =
				fragmentCompositionResource.
					getSiteSiteByExternalReferenceCodeFragmentCompositionsPage(
						siteExternalReferenceCode, null,
						getFilterString(
							entityField, "between", fragmentComposition1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(fragmentComposition1),
				(List<FragmentComposition>)page.getItems());
		}
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPageWithFilterStringContains()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPageWithFilterStringEquals()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPage_getSiteExternalReferenceCode();

		FragmentComposition fragmentComposition1 =
			testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPage_addFragmentComposition(
				siteExternalReferenceCode, randomFragmentComposition());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		FragmentComposition fragmentComposition2 =
			testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPage_addFragmentComposition(
				siteExternalReferenceCode, randomFragmentComposition());

		for (EntityField entityField : entityFields) {
			Page<FragmentComposition> page =
				fragmentCompositionResource.
					getSiteSiteByExternalReferenceCodeFragmentCompositionsPage(
						siteExternalReferenceCode, null,
						getFilterString(
							entityField, operator, fragmentComposition1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(fragmentComposition1),
				(List<FragmentComposition>)page.getItems());
		}
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPageWithPagination()
		throws Exception {

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPage_getSiteExternalReferenceCode();

		Page<FragmentComposition> fragmentCompositionsPage =
			fragmentCompositionResource.
				getSiteSiteByExternalReferenceCodeFragmentCompositionsPage(
					siteExternalReferenceCode, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			fragmentCompositionsPage.getTotalCount());

		FragmentComposition fragmentComposition1 =
			testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPage_addFragmentComposition(
				siteExternalReferenceCode, randomFragmentComposition());

		FragmentComposition fragmentComposition2 =
			testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPage_addFragmentComposition(
				siteExternalReferenceCode, randomFragmentComposition());

		FragmentComposition fragmentComposition3 =
			testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPage_addFragmentComposition(
				siteExternalReferenceCode, randomFragmentComposition());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<FragmentComposition> page1 =
				fragmentCompositionResource.
					getSiteSiteByExternalReferenceCodeFragmentCompositionsPage(
						siteExternalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				fragmentComposition1,
				(List<FragmentComposition>)page1.getItems());

			Page<FragmentComposition> page2 =
				fragmentCompositionResource.
					getSiteSiteByExternalReferenceCodeFragmentCompositionsPage(
						siteExternalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				fragmentComposition2,
				(List<FragmentComposition>)page2.getItems());

			Page<FragmentComposition> page3 =
				fragmentCompositionResource.
					getSiteSiteByExternalReferenceCodeFragmentCompositionsPage(
						siteExternalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				fragmentComposition3,
				(List<FragmentComposition>)page3.getItems());
		}
		else {
			Page<FragmentComposition> page1 =
				fragmentCompositionResource.
					getSiteSiteByExternalReferenceCodeFragmentCompositionsPage(
						siteExternalReferenceCode, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<FragmentComposition> fragmentCompositions1 =
				(List<FragmentComposition>)page1.getItems();

			Assert.assertEquals(
				fragmentCompositions1.toString(), totalCount + 2,
				fragmentCompositions1.size());

			Page<FragmentComposition> page2 =
				fragmentCompositionResource.
					getSiteSiteByExternalReferenceCodeFragmentCompositionsPage(
						siteExternalReferenceCode, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<FragmentComposition> fragmentCompositions2 =
				(List<FragmentComposition>)page2.getItems();

			Assert.assertEquals(
				fragmentCompositions2.toString(), 1,
				fragmentCompositions2.size());

			Page<FragmentComposition> page3 =
				fragmentCompositionResource.
					getSiteSiteByExternalReferenceCodeFragmentCompositionsPage(
						siteExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				fragmentComposition1,
				(List<FragmentComposition>)page3.getItems());
			assertContains(
				fragmentComposition2,
				(List<FragmentComposition>)page3.getItems());
			assertContains(
				fragmentComposition3,
				(List<FragmentComposition>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPageWithSortDateTime()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, fragmentComposition1, fragmentComposition2) -> {
				BeanTestUtil.setProperty(
					fragmentComposition1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPageWithSortDouble()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, fragmentComposition1, fragmentComposition2) -> {
				BeanTestUtil.setProperty(
					fragmentComposition1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					fragmentComposition2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPageWithSortInteger()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, fragmentComposition1, fragmentComposition2) -> {
				BeanTestUtil.setProperty(
					fragmentComposition1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					fragmentComposition2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPageWithSortString()
		throws Exception {

		testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPageWithSort(
			EntityField.Type.STRING,
			(entityField, fragmentComposition1, fragmentComposition2) -> {
				Class<?> clazz = fragmentComposition1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						fragmentComposition1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						fragmentComposition2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						fragmentComposition1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						fragmentComposition2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						fragmentComposition1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						fragmentComposition2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, FragmentComposition, FragmentComposition,
					 Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPage_getSiteExternalReferenceCode();

		FragmentComposition fragmentComposition1 = randomFragmentComposition();
		FragmentComposition fragmentComposition2 = randomFragmentComposition();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, fragmentComposition1, fragmentComposition2);
		}

		fragmentComposition1 =
			testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPage_addFragmentComposition(
				siteExternalReferenceCode, fragmentComposition1);

		fragmentComposition2 =
			testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPage_addFragmentComposition(
				siteExternalReferenceCode, fragmentComposition2);

		Page<FragmentComposition> page =
			fragmentCompositionResource.
				getSiteSiteByExternalReferenceCodeFragmentCompositionsPage(
					siteExternalReferenceCode, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<FragmentComposition> ascPage =
				fragmentCompositionResource.
					getSiteSiteByExternalReferenceCodeFragmentCompositionsPage(
						siteExternalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				fragmentComposition1,
				(List<FragmentComposition>)ascPage.getItems());
			assertContains(
				fragmentComposition2,
				(List<FragmentComposition>)ascPage.getItems());

			Page<FragmentComposition> descPage =
				fragmentCompositionResource.
					getSiteSiteByExternalReferenceCodeFragmentCompositionsPage(
						siteExternalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				fragmentComposition2,
				(List<FragmentComposition>)descPage.getItems());
			assertContains(
				fragmentComposition1,
				(List<FragmentComposition>)descPage.getItems());
		}
	}

	protected FragmentComposition
			testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPage_addFragmentComposition(
				String siteExternalReferenceCode,
				FragmentComposition fragmentComposition)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteSiteByExternalReferenceCodeFragmentCompositionsPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	@Test
	public void testPatchSiteSiteByExternalReferenceCodeFragmentComposition()
		throws Exception {

		FragmentComposition postFragmentComposition =
			testPatchSiteSiteByExternalReferenceCodeFragmentComposition_addFragmentComposition();

		FragmentComposition randomPatchFragmentComposition =
			randomPatchFragmentComposition();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		FragmentComposition patchFragmentComposition =
			fragmentCompositionResource.
				patchSiteSiteByExternalReferenceCodeFragmentComposition(
					null, postFragmentComposition.getExternalReferenceCode(),
					randomPatchFragmentComposition);

		FragmentComposition expectedPatchFragmentComposition =
			postFragmentComposition.clone();

		BeanTestUtil.copyProperties(
			randomPatchFragmentComposition, expectedPatchFragmentComposition);

		FragmentComposition getFragmentComposition =
			fragmentCompositionResource.
				getSiteSiteByExternalReferenceCodeFragmentComposition(
					null, patchFragmentComposition.getExternalReferenceCode());

		assertEquals(expectedPatchFragmentComposition, getFragmentComposition);
		assertValid(getFragmentComposition);
	}

	protected FragmentComposition
			testPatchSiteSiteByExternalReferenceCodeFragmentComposition_addFragmentComposition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSiteSiteByExternalReferenceCodeFragmentComposition()
		throws Exception {

		FragmentComposition randomFragmentComposition =
			randomFragmentComposition();

		FragmentComposition postFragmentComposition =
			testPostSiteSiteByExternalReferenceCodeFragmentComposition_addFragmentComposition(
				randomFragmentComposition);

		assertEquals(randomFragmentComposition, postFragmentComposition);
		assertValid(postFragmentComposition);
	}

	protected FragmentComposition
			testPostSiteSiteByExternalReferenceCodeFragmentComposition_addFragmentComposition(
				FragmentComposition fragmentComposition)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutSiteSiteByExternalReferenceCodeFragmentComposition()
		throws Exception {

		FragmentComposition postFragmentComposition =
			testPutSiteSiteByExternalReferenceCodeFragmentComposition_addFragmentComposition();

		FragmentComposition randomFragmentComposition =
			randomFragmentComposition();

		FragmentComposition putFragmentComposition =
			fragmentCompositionResource.
				putSiteSiteByExternalReferenceCodeFragmentComposition(
					testPutSiteSiteByExternalReferenceCodeFragmentComposition_getSiteExternalReferenceCode(),
					postFragmentComposition.getExternalReferenceCode(),
					randomFragmentComposition);

		assertEquals(randomFragmentComposition, putFragmentComposition);
		assertValid(putFragmentComposition);

		FragmentComposition getFragmentComposition =
			fragmentCompositionResource.
				getSiteSiteByExternalReferenceCodeFragmentComposition(
					testPutSiteSiteByExternalReferenceCodeFragmentComposition_getSiteExternalReferenceCode(),
					putFragmentComposition.getExternalReferenceCode());

		assertEquals(randomFragmentComposition, getFragmentComposition);
		assertValid(getFragmentComposition);
	}

	protected FragmentComposition
			testPutSiteSiteByExternalReferenceCodeFragmentComposition_addFragmentComposition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testPutSiteSiteByExternalReferenceCodeFragmentComposition_getSiteExternalReferenceCode()
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

	protected FragmentComposition
			testGraphQLFragmentComposition_addFragmentComposition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		FragmentComposition fragmentComposition,
		List<FragmentComposition> fragmentCompositions) {

		boolean contains = false;

		for (FragmentComposition item : fragmentCompositions) {
			if (equals(fragmentComposition, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			fragmentCompositions + " does not contain " + fragmentComposition,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		FragmentComposition fragmentComposition1,
		FragmentComposition fragmentComposition2) {

		Assert.assertTrue(
			fragmentComposition1 + " does not equal " + fragmentComposition2,
			equals(fragmentComposition1, fragmentComposition2));
	}

	protected void assertEquals(
		List<FragmentComposition> fragmentCompositions1,
		List<FragmentComposition> fragmentCompositions2) {

		Assert.assertEquals(
			fragmentCompositions1.size(), fragmentCompositions2.size());

		for (int i = 0; i < fragmentCompositions1.size(); i++) {
			FragmentComposition fragmentComposition1 =
				fragmentCompositions1.get(i);
			FragmentComposition fragmentComposition2 =
				fragmentCompositions2.get(i);

			assertEquals(fragmentComposition1, fragmentComposition2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<FragmentComposition> fragmentCompositions1,
		List<FragmentComposition> fragmentCompositions2) {

		Assert.assertEquals(
			fragmentCompositions1.size(), fragmentCompositions2.size());

		for (FragmentComposition fragmentComposition1 : fragmentCompositions1) {
			boolean contains = false;

			for (FragmentComposition fragmentComposition2 :
					fragmentCompositions2) {

				if (equals(fragmentComposition1, fragmentComposition2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				fragmentCompositions2 + " does not contain " +
					fragmentComposition1,
				contains);
		}
	}

	protected void assertValid(FragmentComposition fragmentComposition)
		throws Exception {

		boolean valid = true;

		if (fragmentComposition.getDateCreated() == null) {
			valid = false;
		}

		if (fragmentComposition.getDateModified() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (fragmentComposition.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"creatorExternalReferenceCode",
					additionalAssertFieldName)) {

				if (fragmentComposition.getCreatorExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("datePublished", additionalAssertFieldName)) {
				if (fragmentComposition.getDatePublished() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (fragmentComposition.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (fragmentComposition.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"fragmentSetExternalReferenceCode",
					additionalAssertFieldName)) {

				if (fragmentComposition.getFragmentSetExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (fragmentComposition.getKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (fragmentComposition.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("pageElement", additionalAssertFieldName)) {
				if (fragmentComposition.getPageElement() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("thumbnail", additionalAssertFieldName)) {
				if (fragmentComposition.getThumbnail() == null) {
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

	protected void assertValid(Page<FragmentComposition> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<FragmentComposition> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<FragmentComposition> fragmentCompositions =
			page.getItems();

		int size = fragmentCompositions.size();

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
						FragmentComposition.class)) {

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
		FragmentComposition fragmentComposition1,
		FragmentComposition fragmentComposition2) {

		if (fragmentComposition1 == fragmentComposition2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragmentComposition1.getCreator(),
						fragmentComposition2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"creatorExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						fragmentComposition1.getCreatorExternalReferenceCode(),
						fragmentComposition2.
							getCreatorExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragmentComposition1.getDateCreated(),
						fragmentComposition2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragmentComposition1.getDateModified(),
						fragmentComposition2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("datePublished", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragmentComposition1.getDatePublished(),
						fragmentComposition2.getDatePublished())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragmentComposition1.getDescription(),
						fragmentComposition2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						fragmentComposition1.getExternalReferenceCode(),
						fragmentComposition2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"fragmentSetExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						fragmentComposition1.
							getFragmentSetExternalReferenceCode(),
						fragmentComposition2.
							getFragmentSetExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragmentComposition1.getKey(),
						fragmentComposition2.getKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragmentComposition1.getName(),
						fragmentComposition2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("pageElement", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragmentComposition1.getPageElement(),
						fragmentComposition2.getPageElement())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("thumbnail", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						fragmentComposition1.getThumbnail(),
						fragmentComposition2.getThumbnail())) {

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

		if (!(_fragmentCompositionResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_fragmentCompositionResource;

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
		FragmentComposition fragmentComposition) {

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
			Object object =
				fragmentComposition.getCreatorExternalReferenceCode();

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
				Date date = fragmentComposition.getDateCreated();

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

				sb.append(_format.format(fragmentComposition.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = fragmentComposition.getDateModified();

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
					_format.format(fragmentComposition.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("datePublished")) {
			if (operator.equals("between")) {
				Date date = fragmentComposition.getDatePublished();

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
					_format.format(fragmentComposition.getDatePublished()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = fragmentComposition.getDescription();

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

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = fragmentComposition.getExternalReferenceCode();

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

		if (entityFieldName.equals("fragmentSetExternalReferenceCode")) {
			Object object =
				fragmentComposition.getFragmentSetExternalReferenceCode();

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
			Object object = fragmentComposition.getKey();

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

		if (entityFieldName.equals("name")) {
			Object object = fragmentComposition.getName();

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

		if (entityFieldName.equals("pageElement")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("thumbnail")) {
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

	protected FragmentComposition randomFragmentComposition() throws Exception {
		return new FragmentComposition() {
			{
				creatorExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				datePublished = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				fragmentSetExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected FragmentComposition randomIrrelevantFragmentComposition()
		throws Exception {

		FragmentComposition randomIrrelevantFragmentComposition =
			randomFragmentComposition();

		return randomIrrelevantFragmentComposition;
	}

	protected FragmentComposition randomPatchFragmentComposition()
		throws Exception {

		return randomFragmentComposition();
	}

	protected FragmentCompositionResource fragmentCompositionResource;
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
		LogFactoryUtil.getLog(BaseFragmentCompositionResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.admin.site.resource.v1_0.
			FragmentCompositionResource _fragmentCompositionResource;

}