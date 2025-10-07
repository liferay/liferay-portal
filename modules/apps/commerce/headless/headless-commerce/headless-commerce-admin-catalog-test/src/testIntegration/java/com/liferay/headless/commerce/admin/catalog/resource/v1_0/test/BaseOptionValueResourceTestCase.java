/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

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
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.OptionValue;
import com.liferay.headless.commerce.admin.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.OptionValueResource;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.OptionValueSerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
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
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public abstract class BaseOptionValueResourceTestCase {

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

		_optionValueResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		optionValueResource = OptionValueResource.builder(
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

		OptionValue optionValue1 = randomOptionValue();

		String json = objectMapper.writeValueAsString(optionValue1);

		OptionValue optionValue2 = OptionValueSerDes.toDTO(json);

		Assert.assertTrue(equals(optionValue1, optionValue2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		OptionValue optionValue = randomOptionValue();

		String json1 = objectMapper.writeValueAsString(optionValue);
		String json2 = OptionValueSerDes.toJSON(optionValue);

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

		OptionValue optionValue = randomOptionValue();

		optionValue.setExternalReferenceCode(regex);
		optionValue.setKey(regex);

		String json = OptionValueSerDes.toJSON(optionValue);

		Assert.assertFalse(json.contains(regex));

		optionValue = OptionValueSerDes.toDTO(json);

		Assert.assertEquals(regex, optionValue.getExternalReferenceCode());
		Assert.assertEquals(regex, optionValue.getKey());
	}

	@Test
	public void testDeleteOptionValue() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		OptionValue optionValue = testDeleteOptionValue_addOptionValue();

		assertHttpResponseStatusCode(
			204,
			optionValueResource.deleteOptionValueHttpResponse(
				optionValue.getId()));

		assertHttpResponseStatusCode(
			404,
			optionValueResource.getOptionValueHttpResponse(
				optionValue.getId()));
		assertHttpResponseStatusCode(
			404, optionValueResource.getOptionValueHttpResponse(0L));
	}

	protected OptionValue testDeleteOptionValue_addOptionValue()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOptionValue() throws Exception {

		// No namespace

		OptionValue optionValue1 =
			testGraphQLDeleteOptionValue_addOptionValue();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOptionValue",
						new HashMap<String, Object>() {
							{
								put("id", optionValue1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteOptionValue"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"optionValue",
					new HashMap<String, Object>() {
						{
							put("id", optionValue1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		OptionValue optionValue2 =
			testGraphQLDeleteOptionValue_addOptionValue();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteOptionValue",
							new HashMap<String, Object>() {
								{
									put("id", optionValue2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteOptionValue"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"optionValue",
						new HashMap<String, Object>() {
							{
								put("id", optionValue2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected OptionValue testGraphQLDeleteOptionValue_addOptionValue()
		throws Exception {

		return testGraphQLOptionValue_addOptionValue();
	}

	@Test
	public void testDeleteOptionValueBatch() throws Exception {
		OptionValue optionValue1 = testDeleteOptionValueBatch_addOptionValue();

		testDeleteOptionValueBatch_deleteOptionValue(
			202, optionValue1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			optionValueResource.getOptionValueHttpResponse(
				optionValue1.getId()));

		optionValue1 = testDeleteOptionValueBatch_addOptionValue();

		testDeleteOptionValueBatch_deleteOptionValue(
			202, null, optionValue1.getId());

		assertHttpResponseStatusCode(
			404,
			optionValueResource.getOptionValueHttpResponse(
				optionValue1.getId()));

		optionValue1 = testDeleteOptionValueBatch_addOptionValue();
		OptionValue optionValue2 = testDeleteOptionValueBatch_addOptionValue();

		testDeleteOptionValueBatch_deleteOptionValue(
			202, optionValue2.getExternalReferenceCode(), optionValue1.getId());

		assertHttpResponseStatusCode(
			404,
			optionValueResource.getOptionValueHttpResponse(
				optionValue1.getId()));
		assertHttpResponseStatusCode(
			200,
			optionValueResource.getOptionValueHttpResponse(
				optionValue2.getId()));

		testDeleteOptionValueBatch_deleteOptionValue(
			202, optionValue2.getExternalReferenceCode(), optionValue1.getId());

		assertHttpResponseStatusCode(
			404,
			optionValueResource.getOptionValueHttpResponse(
				optionValue2.getId()));
	}

	protected OptionValue testDeleteOptionValueBatch_addOptionValue()
		throws Exception {

		return testDeleteOptionValue_addOptionValue();
	}

	protected void testDeleteOptionValueBatch_deleteOptionValue(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			optionValueResource.deleteOptionValueBatchHttpResponse(
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
	public void testDeleteOptionValueByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		OptionValue optionValue =
			testDeleteOptionValueByExternalReferenceCode_addOptionValue();

		assertHttpResponseStatusCode(
			204,
			optionValueResource.
				deleteOptionValueByExternalReferenceCodeHttpResponse(
					optionValue.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			optionValueResource.
				getOptionValueByExternalReferenceCodeHttpResponse(
					optionValue.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			optionValueResource.
				getOptionValueByExternalReferenceCodeHttpResponse("-"));
	}

	protected OptionValue
			testDeleteOptionValueByExternalReferenceCode_addOptionValue()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOptionValueByExternalReferenceCode()
		throws Exception {

		// No namespace

		OptionValue optionValue1 =
			testGraphQLDeleteOptionValueByExternalReferenceCode_addOptionValue();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOptionValueByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										optionValue1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteOptionValueByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"optionValueByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + optionValue1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		OptionValue optionValue2 =
			testGraphQLDeleteOptionValueByExternalReferenceCode_addOptionValue();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteOptionValueByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											optionValue2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteOptionValueByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"optionValueByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										optionValue2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected OptionValue
			testGraphQLDeleteOptionValueByExternalReferenceCode_addOptionValue()
		throws Exception {

		return testGraphQLOptionValue_addOptionValue();
	}

	@Test
	public void testGetOptionByExternalReferenceCodeOptionValuesPage()
		throws Exception {

		String externalReferenceCode =
			testGetOptionByExternalReferenceCodeOptionValuesPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetOptionByExternalReferenceCodeOptionValuesPage_getIrrelevantExternalReferenceCode();

		Page<OptionValue> page =
			optionValueResource.
				getOptionByExternalReferenceCodeOptionValuesPage(
					externalReferenceCode, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			OptionValue irrelevantOptionValue =
				testGetOptionByExternalReferenceCodeOptionValuesPage_addOptionValue(
					irrelevantExternalReferenceCode,
					randomIrrelevantOptionValue());

			page =
				optionValueResource.
					getOptionByExternalReferenceCodeOptionValuesPage(
						irrelevantExternalReferenceCode, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantOptionValue, (List<OptionValue>)page.getItems());
			assertValid(
				page,
				testGetOptionByExternalReferenceCodeOptionValuesPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		OptionValue optionValue1 =
			testGetOptionByExternalReferenceCodeOptionValuesPage_addOptionValue(
				externalReferenceCode, randomOptionValue());

		OptionValue optionValue2 =
			testGetOptionByExternalReferenceCodeOptionValuesPage_addOptionValue(
				externalReferenceCode, randomOptionValue());

		page =
			optionValueResource.
				getOptionByExternalReferenceCodeOptionValuesPage(
					externalReferenceCode, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(optionValue1, (List<OptionValue>)page.getItems());
		assertContains(optionValue2, (List<OptionValue>)page.getItems());
		assertValid(
			page,
			testGetOptionByExternalReferenceCodeOptionValuesPage_getExpectedActions(
				externalReferenceCode));

		optionValueResource.deleteOptionValue(optionValue1.getId());

		optionValueResource.deleteOptionValue(optionValue2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetOptionByExternalReferenceCodeOptionValuesPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetOptionByExternalReferenceCodeOptionValuesPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetOptionByExternalReferenceCodeOptionValuesPage_getExternalReferenceCode();

		Page<OptionValue> optionValuesPage =
			optionValueResource.
				getOptionByExternalReferenceCodeOptionValuesPage(
					externalReferenceCode, null, null, null);

		int totalCount = GetterUtil.getInteger(
			optionValuesPage.getTotalCount());

		OptionValue optionValue1 =
			testGetOptionByExternalReferenceCodeOptionValuesPage_addOptionValue(
				externalReferenceCode, randomOptionValue());

		OptionValue optionValue2 =
			testGetOptionByExternalReferenceCodeOptionValuesPage_addOptionValue(
				externalReferenceCode, randomOptionValue());

		OptionValue optionValue3 =
			testGetOptionByExternalReferenceCodeOptionValuesPage_addOptionValue(
				externalReferenceCode, randomOptionValue());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<OptionValue> page1 =
				optionValueResource.
					getOptionByExternalReferenceCodeOptionValuesPage(
						externalReferenceCode, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(optionValue1, (List<OptionValue>)page1.getItems());

			Page<OptionValue> page2 =
				optionValueResource.
					getOptionByExternalReferenceCodeOptionValuesPage(
						externalReferenceCode, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(optionValue2, (List<OptionValue>)page2.getItems());

			Page<OptionValue> page3 =
				optionValueResource.
					getOptionByExternalReferenceCodeOptionValuesPage(
						externalReferenceCode, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(optionValue3, (List<OptionValue>)page3.getItems());
		}
		else {
			Page<OptionValue> page1 =
				optionValueResource.
					getOptionByExternalReferenceCodeOptionValuesPage(
						externalReferenceCode, null,
						Pagination.of(1, totalCount + 2), null);

			List<OptionValue> optionValues1 =
				(List<OptionValue>)page1.getItems();

			Assert.assertEquals(
				optionValues1.toString(), totalCount + 2, optionValues1.size());

			Page<OptionValue> page2 =
				optionValueResource.
					getOptionByExternalReferenceCodeOptionValuesPage(
						externalReferenceCode, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<OptionValue> optionValues2 =
				(List<OptionValue>)page2.getItems();

			Assert.assertEquals(
				optionValues2.toString(), 1, optionValues2.size());

			Page<OptionValue> page3 =
				optionValueResource.
					getOptionByExternalReferenceCodeOptionValuesPage(
						externalReferenceCode, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(optionValue1, (List<OptionValue>)page3.getItems());
			assertContains(optionValue2, (List<OptionValue>)page3.getItems());
			assertContains(optionValue3, (List<OptionValue>)page3.getItems());
		}
	}

	@Test
	public void testGetOptionByExternalReferenceCodeOptionValuesPageWithSortDateTime()
		throws Exception {

		testGetOptionByExternalReferenceCodeOptionValuesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, optionValue1, optionValue2) -> {
				BeanTestUtil.setProperty(
					optionValue1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetOptionByExternalReferenceCodeOptionValuesPageWithSortDouble()
		throws Exception {

		testGetOptionByExternalReferenceCodeOptionValuesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, optionValue1, optionValue2) -> {
				BeanTestUtil.setProperty(
					optionValue1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					optionValue2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetOptionByExternalReferenceCodeOptionValuesPageWithSortInteger()
		throws Exception {

		testGetOptionByExternalReferenceCodeOptionValuesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, optionValue1, optionValue2) -> {
				BeanTestUtil.setProperty(
					optionValue1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					optionValue2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetOptionByExternalReferenceCodeOptionValuesPageWithSortString()
		throws Exception {

		testGetOptionByExternalReferenceCodeOptionValuesPageWithSort(
			EntityField.Type.STRING,
			(entityField, optionValue1, optionValue2) -> {
				Class<?> clazz = optionValue1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						optionValue1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						optionValue2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						optionValue1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						optionValue2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						optionValue1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						optionValue2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetOptionByExternalReferenceCodeOptionValuesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, OptionValue, OptionValue, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetOptionByExternalReferenceCodeOptionValuesPage_getExternalReferenceCode();

		OptionValue optionValue1 = randomOptionValue();
		OptionValue optionValue2 = randomOptionValue();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, optionValue1, optionValue2);
		}

		optionValue1 =
			testGetOptionByExternalReferenceCodeOptionValuesPage_addOptionValue(
				externalReferenceCode, optionValue1);

		optionValue2 =
			testGetOptionByExternalReferenceCodeOptionValuesPage_addOptionValue(
				externalReferenceCode, optionValue2);

		Page<OptionValue> page =
			optionValueResource.
				getOptionByExternalReferenceCodeOptionValuesPage(
					externalReferenceCode, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<OptionValue> ascPage =
				optionValueResource.
					getOptionByExternalReferenceCodeOptionValuesPage(
						externalReferenceCode, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(optionValue1, (List<OptionValue>)ascPage.getItems());
			assertContains(optionValue2, (List<OptionValue>)ascPage.getItems());

			Page<OptionValue> descPage =
				optionValueResource.
					getOptionByExternalReferenceCodeOptionValuesPage(
						externalReferenceCode, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				optionValue2, (List<OptionValue>)descPage.getItems());
			assertContains(
				optionValue1, (List<OptionValue>)descPage.getItems());
		}
	}

	protected OptionValue
			testGetOptionByExternalReferenceCodeOptionValuesPage_addOptionValue(
				String externalReferenceCode, OptionValue optionValue)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOptionByExternalReferenceCodeOptionValuesPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOptionByExternalReferenceCodeOptionValuesPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetOptionIdOptionValuesPage() throws Exception {
		Long id = testGetOptionIdOptionValuesPage_getId();
		Long irrelevantId = testGetOptionIdOptionValuesPage_getIrrelevantId();

		Page<OptionValue> page =
			optionValueResource.getOptionIdOptionValuesPage(
				id, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			OptionValue irrelevantOptionValue =
				testGetOptionIdOptionValuesPage_addOptionValue(
					irrelevantId, randomIrrelevantOptionValue());

			page = optionValueResource.getOptionIdOptionValuesPage(
				irrelevantId, null, Pagination.of(1, (int)totalCount + 1),
				null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantOptionValue, (List<OptionValue>)page.getItems());
			assertValid(
				page,
				testGetOptionIdOptionValuesPage_getExpectedActions(
					irrelevantId));
		}

		OptionValue optionValue1 =
			testGetOptionIdOptionValuesPage_addOptionValue(
				id, randomOptionValue());

		OptionValue optionValue2 =
			testGetOptionIdOptionValuesPage_addOptionValue(
				id, randomOptionValue());

		page = optionValueResource.getOptionIdOptionValuesPage(
			id, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(optionValue1, (List<OptionValue>)page.getItems());
		assertContains(optionValue2, (List<OptionValue>)page.getItems());
		assertValid(
			page, testGetOptionIdOptionValuesPage_getExpectedActions(id));

		optionValueResource.deleteOptionValue(optionValue1.getId());

		optionValueResource.deleteOptionValue(optionValue2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetOptionIdOptionValuesPage_getExpectedActions(Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetOptionIdOptionValuesPageWithPagination()
		throws Exception {

		Long id = testGetOptionIdOptionValuesPage_getId();

		Page<OptionValue> optionValuesPage =
			optionValueResource.getOptionIdOptionValuesPage(
				id, null, null, null);

		int totalCount = GetterUtil.getInteger(
			optionValuesPage.getTotalCount());

		OptionValue optionValue1 =
			testGetOptionIdOptionValuesPage_addOptionValue(
				id, randomOptionValue());

		OptionValue optionValue2 =
			testGetOptionIdOptionValuesPage_addOptionValue(
				id, randomOptionValue());

		OptionValue optionValue3 =
			testGetOptionIdOptionValuesPage_addOptionValue(
				id, randomOptionValue());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<OptionValue> page1 =
				optionValueResource.getOptionIdOptionValuesPage(
					id, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(optionValue1, (List<OptionValue>)page1.getItems());

			Page<OptionValue> page2 =
				optionValueResource.getOptionIdOptionValuesPage(
					id, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(optionValue2, (List<OptionValue>)page2.getItems());

			Page<OptionValue> page3 =
				optionValueResource.getOptionIdOptionValuesPage(
					id, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(optionValue3, (List<OptionValue>)page3.getItems());
		}
		else {
			Page<OptionValue> page1 =
				optionValueResource.getOptionIdOptionValuesPage(
					id, null, Pagination.of(1, totalCount + 2), null);

			List<OptionValue> optionValues1 =
				(List<OptionValue>)page1.getItems();

			Assert.assertEquals(
				optionValues1.toString(), totalCount + 2, optionValues1.size());

			Page<OptionValue> page2 =
				optionValueResource.getOptionIdOptionValuesPage(
					id, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<OptionValue> optionValues2 =
				(List<OptionValue>)page2.getItems();

			Assert.assertEquals(
				optionValues2.toString(), 1, optionValues2.size());

			Page<OptionValue> page3 =
				optionValueResource.getOptionIdOptionValuesPage(
					id, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(optionValue1, (List<OptionValue>)page3.getItems());
			assertContains(optionValue2, (List<OptionValue>)page3.getItems());
			assertContains(optionValue3, (List<OptionValue>)page3.getItems());
		}
	}

	@Test
	public void testGetOptionIdOptionValuesPageWithSortDateTime()
		throws Exception {

		testGetOptionIdOptionValuesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, optionValue1, optionValue2) -> {
				BeanTestUtil.setProperty(
					optionValue1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetOptionIdOptionValuesPageWithSortDouble()
		throws Exception {

		testGetOptionIdOptionValuesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, optionValue1, optionValue2) -> {
				BeanTestUtil.setProperty(
					optionValue1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					optionValue2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetOptionIdOptionValuesPageWithSortInteger()
		throws Exception {

		testGetOptionIdOptionValuesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, optionValue1, optionValue2) -> {
				BeanTestUtil.setProperty(
					optionValue1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					optionValue2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetOptionIdOptionValuesPageWithSortString()
		throws Exception {

		testGetOptionIdOptionValuesPageWithSort(
			EntityField.Type.STRING,
			(entityField, optionValue1, optionValue2) -> {
				Class<?> clazz = optionValue1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						optionValue1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						optionValue2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						optionValue1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						optionValue2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						optionValue1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						optionValue2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetOptionIdOptionValuesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, OptionValue, OptionValue, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetOptionIdOptionValuesPage_getId();

		OptionValue optionValue1 = randomOptionValue();
		OptionValue optionValue2 = randomOptionValue();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, optionValue1, optionValue2);
		}

		optionValue1 = testGetOptionIdOptionValuesPage_addOptionValue(
			id, optionValue1);

		optionValue2 = testGetOptionIdOptionValuesPage_addOptionValue(
			id, optionValue2);

		Page<OptionValue> page =
			optionValueResource.getOptionIdOptionValuesPage(
				id, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<OptionValue> ascPage =
				optionValueResource.getOptionIdOptionValuesPage(
					id, null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(optionValue1, (List<OptionValue>)ascPage.getItems());
			assertContains(optionValue2, (List<OptionValue>)ascPage.getItems());

			Page<OptionValue> descPage =
				optionValueResource.getOptionIdOptionValuesPage(
					id, null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				optionValue2, (List<OptionValue>)descPage.getItems());
			assertContains(
				optionValue1, (List<OptionValue>)descPage.getItems());
		}
	}

	protected OptionValue testGetOptionIdOptionValuesPage_addOptionValue(
			Long id, OptionValue optionValue)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetOptionIdOptionValuesPage_getId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetOptionIdOptionValuesPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetOptionValue() throws Exception {
		OptionValue postOptionValue = testGetOptionValue_addOptionValue();

		OptionValue getOptionValue = optionValueResource.getOptionValue(
			postOptionValue.getId());

		assertEquals(postOptionValue, getOptionValue);
		assertValid(getOptionValue);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		OptionValue postOptionValue = testGetOptionValue_addOptionValue();

		OptionValue getOptionValue = optionValueResource.getOptionValue(
			postOptionValue.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.OptionValue"
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

		Object item = vulcanCRUDItemDelegate.getItem(postOptionValue.getId());

		assertEquals(getOptionValue, OptionValueSerDes.toDTO(item.toString()));
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

	protected OptionValue testGetOptionValue_addOptionValue() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetOptionValue() throws Exception {
		OptionValue optionValue = testGraphQLGetOptionValue_addOptionValue();

		// No namespace

		Assert.assertTrue(
			equals(
				optionValue,
				OptionValueSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"optionValue",
								new HashMap<String, Object>() {
									{
										put("id", optionValue.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/optionValue"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				optionValue,
				OptionValueSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"optionValue",
									new HashMap<String, Object>() {
										{
											put("id", optionValue.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/optionValue"))));
	}

	@Test
	public void testGraphQLGetOptionValueNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"optionValue",
						new HashMap<String, Object>() {
							{
								put("id", irrelevantId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"optionValue",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected OptionValue testGraphQLGetOptionValue_addOptionValue()
		throws Exception {

		return testGraphQLOptionValue_addOptionValue();
	}

	@Test
	public void testGetOptionValueByExternalReferenceCode() throws Exception {
		OptionValue postOptionValue =
			testGetOptionValueByExternalReferenceCode_addOptionValue();

		OptionValue getOptionValue =
			optionValueResource.getOptionValueByExternalReferenceCode(
				postOptionValue.getExternalReferenceCode());

		assertEquals(postOptionValue, getOptionValue);
		assertValid(getOptionValue);
	}

	protected OptionValue
			testGetOptionValueByExternalReferenceCode_addOptionValue()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetOptionValueByExternalReferenceCode()
		throws Exception {

		OptionValue optionValue =
			testGraphQLGetOptionValueByExternalReferenceCode_addOptionValue();

		// No namespace

		Assert.assertTrue(
			equals(
				optionValue,
				OptionValueSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"optionValueByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												optionValue.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/optionValueByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				optionValue,
				OptionValueSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"optionValueByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													optionValue.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/optionValueByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetOptionValueByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"optionValueByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									irrelevantExternalReferenceCode);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"optionValueByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected OptionValue
			testGraphQLGetOptionValueByExternalReferenceCode_addOptionValue()
		throws Exception {

		return testGraphQLOptionValue_addOptionValue();
	}

	@Test
	public void testPatchOptionValue() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPatchOptionValueByExternalReferenceCode() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPostOptionByExternalReferenceCodeOptionValue()
		throws Exception {

		OptionValue randomOptionValue = randomOptionValue();

		OptionValue postOptionValue =
			testPostOptionByExternalReferenceCodeOptionValue_addOptionValue(
				randomOptionValue);

		assertEquals(randomOptionValue, postOptionValue);
		assertValid(postOptionValue);
	}

	protected OptionValue
			testPostOptionByExternalReferenceCodeOptionValue_addOptionValue(
				OptionValue optionValue)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostOptionIdOptionValue() throws Exception {
		OptionValue randomOptionValue = randomOptionValue();

		OptionValue postOptionValue =
			testPostOptionIdOptionValue_addOptionValue(randomOptionValue);

		assertEquals(randomOptionValue, postOptionValue);
		assertValid(postOptionValue);
	}

	protected OptionValue testPostOptionIdOptionValue_addOptionValue(
			OptionValue optionValue)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		OptionValue optionValue1 =
			testBatchEngineDeleteImportTask_addOptionValue();

		testBatchEngineDeleteImportTask_deleteOptionValue(
			200, optionValue1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			optionValueResource.getOptionValueHttpResponse(
				optionValue1.getId()));

		optionValue1 = testBatchEngineDeleteImportTask_addOptionValue();

		testBatchEngineDeleteImportTask_deleteOptionValue(
			200, null, optionValue1.getId());

		assertHttpResponseStatusCode(
			404,
			optionValueResource.getOptionValueHttpResponse(
				optionValue1.getId()));

		optionValue1 = testBatchEngineDeleteImportTask_addOptionValue();
		OptionValue optionValue2 =
			testBatchEngineDeleteImportTask_addOptionValue();

		testBatchEngineDeleteImportTask_deleteOptionValue(
			200, optionValue2.getExternalReferenceCode(), optionValue1.getId());

		assertHttpResponseStatusCode(
			404,
			optionValueResource.getOptionValueHttpResponse(
				optionValue1.getId()));
		assertHttpResponseStatusCode(
			200,
			optionValueResource.getOptionValueHttpResponse(
				optionValue2.getId()));

		testBatchEngineDeleteImportTask_deleteOptionValue(
			200, optionValue2.getExternalReferenceCode(), optionValue1.getId());

		assertHttpResponseStatusCode(
			404,
			optionValueResource.getOptionValueHttpResponse(
				optionValue2.getId()));
	}

	protected OptionValue testBatchEngineDeleteImportTask_addOptionValue()
		throws Exception {

		return testDeleteOptionValue_addOptionValue();
	}

	protected void testBatchEngineDeleteImportTask_deleteOptionValue(
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
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.OptionValue",
				null, null, null, null,
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

	protected OptionValue testGraphQLOptionValue_addOptionValue()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		OptionValue optionValue, List<OptionValue> optionValues) {

		boolean contains = false;

		for (OptionValue item : optionValues) {
			if (equals(optionValue, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			optionValues + " does not contain " + optionValue, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		OptionValue optionValue1, OptionValue optionValue2) {

		Assert.assertTrue(
			optionValue1 + " does not equal " + optionValue2,
			equals(optionValue1, optionValue2));
	}

	protected void assertEquals(
		List<OptionValue> optionValues1, List<OptionValue> optionValues2) {

		Assert.assertEquals(optionValues1.size(), optionValues2.size());

		for (int i = 0; i < optionValues1.size(); i++) {
			OptionValue optionValue1 = optionValues1.get(i);
			OptionValue optionValue2 = optionValues2.get(i);

			assertEquals(optionValue1, optionValue2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<OptionValue> optionValues1, List<OptionValue> optionValues2) {

		Assert.assertEquals(optionValues1.size(), optionValues2.size());

		for (OptionValue optionValue1 : optionValues1) {
			boolean contains = false;

			for (OptionValue optionValue2 : optionValues2) {
				if (equals(optionValue1, optionValue2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				optionValues2 + " does not contain " + optionValue1, contains);
		}
	}

	protected void assertValid(OptionValue optionValue) throws Exception {
		boolean valid = true;

		if (optionValue.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (optionValue.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (optionValue.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (optionValue.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (optionValue.getKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (optionValue.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (optionValue.getPriority() == null) {
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

	protected void assertValid(Page<OptionValue> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<OptionValue> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<OptionValue> optionValues = page.getItems();

		int size = optionValues.size();

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
					com.liferay.headless.commerce.admin.catalog.dto.v1_0.
						OptionValue.class)) {

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
		OptionValue optionValue1, OptionValue optionValue2) {

		if (optionValue1 == optionValue2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)optionValue1.getActions(),
						(Map)optionValue2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						optionValue1.getCustomFields(),
						optionValue2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						optionValue1.getExternalReferenceCode(),
						optionValue2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						optionValue1.getId(), optionValue2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						optionValue1.getKey(), optionValue2.getKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!equals(
						(Map)optionValue1.getName(),
						(Map)optionValue2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						optionValue1.getPriority(),
						optionValue2.getPriority())) {

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

		if (!(_optionValueResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_optionValueResource;

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
		EntityField entityField, String operator, OptionValue optionValue) {

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

		if (entityFieldName.equals("customFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = optionValue.getExternalReferenceCode();

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

		if (entityFieldName.equals("key")) {
			Object object = optionValue.getKey();

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
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priority")) {
			sb.append(String.valueOf(optionValue.getPriority()));

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

	protected OptionValue randomOptionValue() throws Exception {
		return new OptionValue() {
			{
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				priority = RandomTestUtil.randomDouble();
			}
		};
	}

	protected OptionValue randomIrrelevantOptionValue() throws Exception {
		OptionValue randomIrrelevantOptionValue = randomOptionValue();

		return randomIrrelevantOptionValue;
	}

	protected OptionValue randomPatchOptionValue() throws Exception {
		return randomOptionValue();
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

	protected OptionValueResource optionValueResource;
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
		LogFactoryUtil.getLog(BaseOptionValueResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.catalog.resource.v1_0.
		OptionValueResource _optionValueResource;

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