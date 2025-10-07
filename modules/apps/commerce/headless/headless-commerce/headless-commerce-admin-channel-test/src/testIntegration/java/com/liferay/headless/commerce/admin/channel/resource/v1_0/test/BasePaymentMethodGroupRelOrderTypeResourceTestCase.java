/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.resource.v1_0.test;

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
import com.liferay.headless.commerce.admin.channel.client.dto.v1_0.PaymentMethodGroupRelOrderType;
import com.liferay.headless.commerce.admin.channel.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.channel.client.pagination.Page;
import com.liferay.headless.commerce.admin.channel.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.channel.client.resource.v1_0.PaymentMethodGroupRelOrderTypeResource;
import com.liferay.headless.commerce.admin.channel.client.serdes.v1_0.PaymentMethodGroupRelOrderTypeSerDes;
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
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public abstract class BasePaymentMethodGroupRelOrderTypeResourceTestCase {

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

		_paymentMethodGroupRelOrderTypeResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		paymentMethodGroupRelOrderTypeResource =
			PaymentMethodGroupRelOrderTypeResource.builder(
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

		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType1 =
			randomPaymentMethodGroupRelOrderType();

		String json = objectMapper.writeValueAsString(
			paymentMethodGroupRelOrderType1);

		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType2 =
			PaymentMethodGroupRelOrderTypeSerDes.toDTO(json);

		Assert.assertTrue(
			equals(
				paymentMethodGroupRelOrderType1,
				paymentMethodGroupRelOrderType2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType =
			randomPaymentMethodGroupRelOrderType();

		String json1 = objectMapper.writeValueAsString(
			paymentMethodGroupRelOrderType);
		String json2 = PaymentMethodGroupRelOrderTypeSerDes.toJSON(
			paymentMethodGroupRelOrderType);

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

		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType =
			randomPaymentMethodGroupRelOrderType();

		paymentMethodGroupRelOrderType.setOrderTypeExternalReferenceCode(regex);

		String json = PaymentMethodGroupRelOrderTypeSerDes.toJSON(
			paymentMethodGroupRelOrderType);

		Assert.assertFalse(json.contains(regex));

		paymentMethodGroupRelOrderType =
			PaymentMethodGroupRelOrderTypeSerDes.toDTO(json);

		Assert.assertEquals(
			regex,
			paymentMethodGroupRelOrderType.getOrderTypeExternalReferenceCode());
	}

	@Test
	public void testDeletePaymentMethodGroupRelOrderType() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType =
			testDeletePaymentMethodGroupRelOrderType_addPaymentMethodGroupRelOrderType();

		assertHttpResponseStatusCode(
			204,
			paymentMethodGroupRelOrderTypeResource.
				deletePaymentMethodGroupRelOrderTypeHttpResponse(
					paymentMethodGroupRelOrderType.
						getPaymentMethodGroupRelOrderTypeId()));
	}

	protected PaymentMethodGroupRelOrderType
			testDeletePaymentMethodGroupRelOrderType_addPaymentMethodGroupRelOrderType()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeletePaymentMethodGroupRelOrderType()
		throws Exception {

		// No namespace

		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType1 =
			testGraphQLDeletePaymentMethodGroupRelOrderType_addPaymentMethodGroupRelOrderType();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deletePaymentMethodGroupRelOrderType",
						new HashMap<String, Object>() {
							{
								put(
									"paymentMethodGroupRelOrderTypeId",
									paymentMethodGroupRelOrderType1.
										getPaymentMethodGroupRelOrderTypeId());
							}
						})),
				"JSONObject/data",
				"Object/deletePaymentMethodGroupRelOrderType"));

		// Using the namespace headlessCommerceAdminChannel_v1_0

		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType2 =
			testGraphQLDeletePaymentMethodGroupRelOrderType_addPaymentMethodGroupRelOrderType();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminChannel_v1_0",
						new GraphQLField(
							"deletePaymentMethodGroupRelOrderType",
							new HashMap<String, Object>() {
								{
									put(
										"paymentMethodGroupRelOrderTypeId",
										paymentMethodGroupRelOrderType2.
											getPaymentMethodGroupRelOrderTypeId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminChannel_v1_0",
				"Object/deletePaymentMethodGroupRelOrderType"));
	}

	protected PaymentMethodGroupRelOrderType
			testGraphQLDeletePaymentMethodGroupRelOrderType_addPaymentMethodGroupRelOrderType()
		throws Exception {

		return testGraphQLPaymentMethodGroupRelOrderType_addPaymentMethodGroupRelOrderType();
	}

	@Test
	public void testDeletePaymentMethodGroupRelOrderTypeBatch()
		throws Exception {

		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType1 =
			testDeletePaymentMethodGroupRelOrderTypeBatch_addPaymentMethodGroupRelOrderType();

		testDeletePaymentMethodGroupRelOrderTypeBatch_deletePaymentMethodGroupRelOrderType(
			202, null,
			paymentMethodGroupRelOrderType1.
				getPaymentMethodGroupRelOrderTypeId());
	}

	protected PaymentMethodGroupRelOrderType
			testDeletePaymentMethodGroupRelOrderTypeBatch_addPaymentMethodGroupRelOrderType()
		throws Exception {

		return testDeletePaymentMethodGroupRelOrderType_addPaymentMethodGroupRelOrderType();
	}

	protected void
			testDeletePaymentMethodGroupRelOrderTypeBatch_deletePaymentMethodGroupRelOrderType(
				int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			paymentMethodGroupRelOrderTypeResource.
				deletePaymentMethodGroupRelOrderTypeBatchHttpResponse(
					null,
					JSONUtil.putAll(
						JSONUtil.put(
							"externalReferenceCode", () -> externalReferenceCode
						).put(
							"paymentMethodGroupRelOrderTypeId", () -> id
						)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage()
		throws Exception {

		Long id =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage_getId();
		Long irrelevantId =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage_getIrrelevantId();

		Page<PaymentMethodGroupRelOrderType> page =
			paymentMethodGroupRelOrderTypeResource.
				getPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage(
					id, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			PaymentMethodGroupRelOrderType
				irrelevantPaymentMethodGroupRelOrderType =
					testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage_addPaymentMethodGroupRelOrderType(
						irrelevantId,
						randomIrrelevantPaymentMethodGroupRelOrderType());

			page =
				paymentMethodGroupRelOrderTypeResource.
					getPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage(
						irrelevantId, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPaymentMethodGroupRelOrderType,
				(List<PaymentMethodGroupRelOrderType>)page.getItems());
			assertValid(
				page,
				testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage_getExpectedActions(
					irrelevantId));
		}

		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType1 =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage_addPaymentMethodGroupRelOrderType(
				id, randomPaymentMethodGroupRelOrderType());

		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType2 =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage_addPaymentMethodGroupRelOrderType(
				id, randomPaymentMethodGroupRelOrderType());

		page =
			paymentMethodGroupRelOrderTypeResource.
				getPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage(
					id, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			paymentMethodGroupRelOrderType1,
			(List<PaymentMethodGroupRelOrderType>)page.getItems());
		assertContains(
			paymentMethodGroupRelOrderType2,
			(List<PaymentMethodGroupRelOrderType>)page.getItems());
		assertValid(
			page,
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage_getExpectedActions(
				id));

		paymentMethodGroupRelOrderTypeResource.
			deletePaymentMethodGroupRelOrderType(
				paymentMethodGroupRelOrderType1.
					getPaymentMethodGroupRelOrderTypeId());

		paymentMethodGroupRelOrderTypeResource.
			deletePaymentMethodGroupRelOrderType(
				paymentMethodGroupRelOrderType2.
					getPaymentMethodGroupRelOrderTypeId());
	}

	protected Map<String, Map<String, String>>
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage_getExpectedActions(
				Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage_getId();

		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType1 =
			randomPaymentMethodGroupRelOrderType();

		paymentMethodGroupRelOrderType1 =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage_addPaymentMethodGroupRelOrderType(
				id, paymentMethodGroupRelOrderType1);

		for (EntityField entityField : entityFields) {
			Page<PaymentMethodGroupRelOrderType> page =
				paymentMethodGroupRelOrderTypeResource.
					getPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage(
						id, null,
						getFilterString(
							entityField, "between",
							paymentMethodGroupRelOrderType1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(paymentMethodGroupRelOrderType1),
				(List<PaymentMethodGroupRelOrderType>)page.getItems());
		}
	}

	@Test
	public void testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPageWithFilterDoubleEquals()
		throws Exception {

		testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPageWithFilterStringContains()
		throws Exception {

		testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPageWithFilterStringEquals()
		throws Exception {

		testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPageWithFilterStringStartsWith()
		throws Exception {

		testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage_getId();

		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType1 =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage_addPaymentMethodGroupRelOrderType(
				id, randomPaymentMethodGroupRelOrderType());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType2 =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage_addPaymentMethodGroupRelOrderType(
				id, randomPaymentMethodGroupRelOrderType());

		for (EntityField entityField : entityFields) {
			Page<PaymentMethodGroupRelOrderType> page =
				paymentMethodGroupRelOrderTypeResource.
					getPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage(
						id, null,
						getFilterString(
							entityField, operator,
							paymentMethodGroupRelOrderType1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(paymentMethodGroupRelOrderType1),
				(List<PaymentMethodGroupRelOrderType>)page.getItems());
		}
	}

	@Test
	public void testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPageWithPagination()
		throws Exception {

		Long id =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage_getId();

		Page<PaymentMethodGroupRelOrderType>
			paymentMethodGroupRelOrderTypesPage =
				paymentMethodGroupRelOrderTypeResource.
					getPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage(
						id, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			paymentMethodGroupRelOrderTypesPage.getTotalCount());

		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType1 =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage_addPaymentMethodGroupRelOrderType(
				id, randomPaymentMethodGroupRelOrderType());

		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType2 =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage_addPaymentMethodGroupRelOrderType(
				id, randomPaymentMethodGroupRelOrderType());

		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType3 =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage_addPaymentMethodGroupRelOrderType(
				id, randomPaymentMethodGroupRelOrderType());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<PaymentMethodGroupRelOrderType> page1 =
				paymentMethodGroupRelOrderTypeResource.
					getPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				paymentMethodGroupRelOrderType1,
				(List<PaymentMethodGroupRelOrderType>)page1.getItems());

			Page<PaymentMethodGroupRelOrderType> page2 =
				paymentMethodGroupRelOrderTypeResource.
					getPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				paymentMethodGroupRelOrderType2,
				(List<PaymentMethodGroupRelOrderType>)page2.getItems());

			Page<PaymentMethodGroupRelOrderType> page3 =
				paymentMethodGroupRelOrderTypeResource.
					getPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				paymentMethodGroupRelOrderType3,
				(List<PaymentMethodGroupRelOrderType>)page3.getItems());
		}
		else {
			Page<PaymentMethodGroupRelOrderType> page1 =
				paymentMethodGroupRelOrderTypeResource.
					getPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage(
						id, null, null, Pagination.of(1, totalCount + 2), null);

			List<PaymentMethodGroupRelOrderType>
				paymentMethodGroupRelOrderTypes1 =
					(List<PaymentMethodGroupRelOrderType>)page1.getItems();

			Assert.assertEquals(
				paymentMethodGroupRelOrderTypes1.toString(), totalCount + 2,
				paymentMethodGroupRelOrderTypes1.size());

			Page<PaymentMethodGroupRelOrderType> page2 =
				paymentMethodGroupRelOrderTypeResource.
					getPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage(
						id, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PaymentMethodGroupRelOrderType>
				paymentMethodGroupRelOrderTypes2 =
					(List<PaymentMethodGroupRelOrderType>)page2.getItems();

			Assert.assertEquals(
				paymentMethodGroupRelOrderTypes2.toString(), 1,
				paymentMethodGroupRelOrderTypes2.size());

			Page<PaymentMethodGroupRelOrderType> page3 =
				paymentMethodGroupRelOrderTypeResource.
					getPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage(
						id, null, null, Pagination.of(1, (int)totalCount + 3),
						null);

			assertContains(
				paymentMethodGroupRelOrderType1,
				(List<PaymentMethodGroupRelOrderType>)page3.getItems());
			assertContains(
				paymentMethodGroupRelOrderType2,
				(List<PaymentMethodGroupRelOrderType>)page3.getItems());
			assertContains(
				paymentMethodGroupRelOrderType3,
				(List<PaymentMethodGroupRelOrderType>)page3.getItems());
		}
	}

	@Test
	public void testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPageWithSortDateTime()
		throws Exception {

		testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, paymentMethodGroupRelOrderType1,
			 paymentMethodGroupRelOrderType2) -> {

				BeanTestUtil.setProperty(
					paymentMethodGroupRelOrderType1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPageWithSortDouble()
		throws Exception {

		testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, paymentMethodGroupRelOrderType1,
			 paymentMethodGroupRelOrderType2) -> {

				BeanTestUtil.setProperty(
					paymentMethodGroupRelOrderType1, entityField.getName(),
					0.1);
				BeanTestUtil.setProperty(
					paymentMethodGroupRelOrderType2, entityField.getName(),
					0.5);
			});
	}

	@Test
	public void testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPageWithSortInteger()
		throws Exception {

		testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, paymentMethodGroupRelOrderType1,
			 paymentMethodGroupRelOrderType2) -> {

				BeanTestUtil.setProperty(
					paymentMethodGroupRelOrderType1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					paymentMethodGroupRelOrderType2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPageWithSortString()
		throws Exception {

		testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPageWithSort(
			EntityField.Type.STRING,
			(entityField, paymentMethodGroupRelOrderType1,
			 paymentMethodGroupRelOrderType2) -> {

				Class<?> clazz = paymentMethodGroupRelOrderType1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						paymentMethodGroupRelOrderType1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						paymentMethodGroupRelOrderType2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						paymentMethodGroupRelOrderType1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						paymentMethodGroupRelOrderType2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						paymentMethodGroupRelOrderType1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						paymentMethodGroupRelOrderType2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, PaymentMethodGroupRelOrderType,
					 PaymentMethodGroupRelOrderType, Exception>
						unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage_getId();

		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType1 =
			randomPaymentMethodGroupRelOrderType();
		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType2 =
			randomPaymentMethodGroupRelOrderType();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, paymentMethodGroupRelOrderType1,
				paymentMethodGroupRelOrderType2);
		}

		paymentMethodGroupRelOrderType1 =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage_addPaymentMethodGroupRelOrderType(
				id, paymentMethodGroupRelOrderType1);

		paymentMethodGroupRelOrderType2 =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage_addPaymentMethodGroupRelOrderType(
				id, paymentMethodGroupRelOrderType2);

		Page<PaymentMethodGroupRelOrderType> page =
			paymentMethodGroupRelOrderTypeResource.
				getPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage(
					id, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<PaymentMethodGroupRelOrderType> ascPage =
				paymentMethodGroupRelOrderTypeResource.
					getPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage(
						id, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				paymentMethodGroupRelOrderType1,
				(List<PaymentMethodGroupRelOrderType>)ascPage.getItems());
			assertContains(
				paymentMethodGroupRelOrderType2,
				(List<PaymentMethodGroupRelOrderType>)ascPage.getItems());

			Page<PaymentMethodGroupRelOrderType> descPage =
				paymentMethodGroupRelOrderTypeResource.
					getPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage(
						id, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				paymentMethodGroupRelOrderType2,
				(List<PaymentMethodGroupRelOrderType>)descPage.getItems());
			assertContains(
				paymentMethodGroupRelOrderType1,
				(List<PaymentMethodGroupRelOrderType>)descPage.getItems());
		}
	}

	protected PaymentMethodGroupRelOrderType
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage_addPaymentMethodGroupRelOrderType(
				Long id,
				PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelOrderTypesPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostPaymentMethodGroupRelIdPaymentMethodGroupRelOrderType()
		throws Exception {

		PaymentMethodGroupRelOrderType randomPaymentMethodGroupRelOrderType =
			randomPaymentMethodGroupRelOrderType();

		PaymentMethodGroupRelOrderType postPaymentMethodGroupRelOrderType =
			testPostPaymentMethodGroupRelIdPaymentMethodGroupRelOrderType_addPaymentMethodGroupRelOrderType(
				randomPaymentMethodGroupRelOrderType);

		assertEquals(
			randomPaymentMethodGroupRelOrderType,
			postPaymentMethodGroupRelOrderType);
		assertValid(postPaymentMethodGroupRelOrderType);
	}

	protected PaymentMethodGroupRelOrderType
			testPostPaymentMethodGroupRelIdPaymentMethodGroupRelOrderType_addPaymentMethodGroupRelOrderType(
				PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType1 =
			testBatchEngineDeleteImportTask_addPaymentMethodGroupRelOrderType();

		testBatchEngineDeleteImportTask_deletePaymentMethodGroupRelOrderType(
			200, null,
			paymentMethodGroupRelOrderType1.
				getPaymentMethodGroupRelOrderTypeId());
	}

	protected PaymentMethodGroupRelOrderType
			testBatchEngineDeleteImportTask_addPaymentMethodGroupRelOrderType()
		throws Exception {

		return testDeletePaymentMethodGroupRelOrderType_addPaymentMethodGroupRelOrderType();
	}

	protected void
			testBatchEngineDeleteImportTask_deletePaymentMethodGroupRelOrderType(
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
				"com.liferay.headless.commerce.admin.channel.dto.v1_0.PaymentMethodGroupRelOrderType",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"paymentMethodGroupRelOrderTypeId", () -> id
					)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		if (expectedStatusCode == 200) {
			waitForFinish(
				"COMPLETED",
				JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
		}
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected PaymentMethodGroupRelOrderType
			testGraphQLPaymentMethodGroupRelOrderType_addPaymentMethodGroupRelOrderType()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType,
		List<PaymentMethodGroupRelOrderType> paymentMethodGroupRelOrderTypes) {

		boolean contains = false;

		for (PaymentMethodGroupRelOrderType item :
				paymentMethodGroupRelOrderTypes) {

			if (equals(paymentMethodGroupRelOrderType, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			paymentMethodGroupRelOrderTypes + " does not contain " +
				paymentMethodGroupRelOrderType,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType1,
		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType2) {

		Assert.assertTrue(
			paymentMethodGroupRelOrderType1 + " does not equal " +
				paymentMethodGroupRelOrderType2,
			equals(
				paymentMethodGroupRelOrderType1,
				paymentMethodGroupRelOrderType2));
	}

	protected void assertEquals(
		List<PaymentMethodGroupRelOrderType> paymentMethodGroupRelOrderTypes1,
		List<PaymentMethodGroupRelOrderType> paymentMethodGroupRelOrderTypes2) {

		Assert.assertEquals(
			paymentMethodGroupRelOrderTypes1.size(),
			paymentMethodGroupRelOrderTypes2.size());

		for (int i = 0; i < paymentMethodGroupRelOrderTypes1.size(); i++) {
			PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType1 =
				paymentMethodGroupRelOrderTypes1.get(i);
			PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType2 =
				paymentMethodGroupRelOrderTypes2.get(i);

			assertEquals(
				paymentMethodGroupRelOrderType1,
				paymentMethodGroupRelOrderType2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<PaymentMethodGroupRelOrderType> paymentMethodGroupRelOrderTypes1,
		List<PaymentMethodGroupRelOrderType> paymentMethodGroupRelOrderTypes2) {

		Assert.assertEquals(
			paymentMethodGroupRelOrderTypes1.size(),
			paymentMethodGroupRelOrderTypes2.size());

		for (PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType1 :
				paymentMethodGroupRelOrderTypes1) {

			boolean contains = false;

			for (PaymentMethodGroupRelOrderType
					paymentMethodGroupRelOrderType2 :
						paymentMethodGroupRelOrderTypes2) {

				if (equals(
						paymentMethodGroupRelOrderType1,
						paymentMethodGroupRelOrderType2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				paymentMethodGroupRelOrderTypes2 + " does not contain " +
					paymentMethodGroupRelOrderType1,
				contains);
		}
	}

	protected void assertValid(
			PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType)
		throws Exception {

		boolean valid = true;

		if (paymentMethodGroupRelOrderType.
				getPaymentMethodGroupRelOrderTypeId() == null) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (paymentMethodGroupRelOrderType.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderType", additionalAssertFieldName)) {
				if (paymentMethodGroupRelOrderType.getOrderType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"orderTypeExternalReferenceCode",
					additionalAssertFieldName)) {

				if (paymentMethodGroupRelOrderType.
						getOrderTypeExternalReferenceCode() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderTypeId", additionalAssertFieldName)) {
				if (paymentMethodGroupRelOrderType.getOrderTypeId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentMethodGroupRelId", additionalAssertFieldName)) {

				if (paymentMethodGroupRelOrderType.
						getPaymentMethodGroupRelId() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentMethodGroupRelOrderTypeId",
					additionalAssertFieldName)) {

				if (paymentMethodGroupRelOrderType.
						getPaymentMethodGroupRelOrderTypeId() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (paymentMethodGroupRelOrderType.getPriority() == null) {
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

	protected void assertValid(Page<PaymentMethodGroupRelOrderType> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<PaymentMethodGroupRelOrderType> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<PaymentMethodGroupRelOrderType>
			paymentMethodGroupRelOrderTypes = page.getItems();

		int size = paymentMethodGroupRelOrderTypes.size();

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

		graphQLFields.add(new GraphQLField("paymentMethodGroupRelOrderTypeId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.channel.dto.v1_0.
						PaymentMethodGroupRelOrderType.class)) {

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
		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType1,
		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType2) {

		if (paymentMethodGroupRelOrderType1 ==
				paymentMethodGroupRelOrderType2) {

			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)paymentMethodGroupRelOrderType1.getActions(),
						(Map)paymentMethodGroupRelOrderType2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						paymentMethodGroupRelOrderType1.getOrderType(),
						paymentMethodGroupRelOrderType2.getOrderType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"orderTypeExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						paymentMethodGroupRelOrderType1.
							getOrderTypeExternalReferenceCode(),
						paymentMethodGroupRelOrderType2.
							getOrderTypeExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderTypeId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						paymentMethodGroupRelOrderType1.getOrderTypeId(),
						paymentMethodGroupRelOrderType2.getOrderTypeId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentMethodGroupRelId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						paymentMethodGroupRelOrderType1.
							getPaymentMethodGroupRelId(),
						paymentMethodGroupRelOrderType2.
							getPaymentMethodGroupRelId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentMethodGroupRelOrderTypeId",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						paymentMethodGroupRelOrderType1.
							getPaymentMethodGroupRelOrderTypeId(),
						paymentMethodGroupRelOrderType2.
							getPaymentMethodGroupRelOrderTypeId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						paymentMethodGroupRelOrderType1.getPriority(),
						paymentMethodGroupRelOrderType2.getPriority())) {

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

		if (!(_paymentMethodGroupRelOrderTypeResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_paymentMethodGroupRelOrderTypeResource;

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
		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType) {

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

		if (entityFieldName.equals("orderType")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("orderTypeExternalReferenceCode")) {
			Object object =
				paymentMethodGroupRelOrderType.
					getOrderTypeExternalReferenceCode();

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

		if (entityFieldName.equals("orderTypeId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("paymentMethodGroupRelId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("paymentMethodGroupRelOrderTypeId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priority")) {
			sb.append(
				String.valueOf(paymentMethodGroupRelOrderType.getPriority()));

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

	protected PaymentMethodGroupRelOrderType
			randomPaymentMethodGroupRelOrderType()
		throws Exception {

		return new PaymentMethodGroupRelOrderType() {
			{
				orderTypeExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				orderTypeId = RandomTestUtil.randomLong();
				paymentMethodGroupRelId = RandomTestUtil.randomLong();
				paymentMethodGroupRelOrderTypeId = RandomTestUtil.randomLong();
				priority = RandomTestUtil.randomInt();
			}
		};
	}

	protected PaymentMethodGroupRelOrderType
			randomIrrelevantPaymentMethodGroupRelOrderType()
		throws Exception {

		PaymentMethodGroupRelOrderType
			randomIrrelevantPaymentMethodGroupRelOrderType =
				randomPaymentMethodGroupRelOrderType();

		return randomIrrelevantPaymentMethodGroupRelOrderType;
	}

	protected PaymentMethodGroupRelOrderType
			randomPatchPaymentMethodGroupRelOrderType()
		throws Exception {

		return randomPaymentMethodGroupRelOrderType();
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

	protected PaymentMethodGroupRelOrderTypeResource
		paymentMethodGroupRelOrderTypeResource;
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
		LogFactoryUtil.getLog(
			BasePaymentMethodGroupRelOrderTypeResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.channel.resource.v1_0.
		PaymentMethodGroupRelOrderTypeResource
			_paymentMethodGroupRelOrderTypeResource;

}