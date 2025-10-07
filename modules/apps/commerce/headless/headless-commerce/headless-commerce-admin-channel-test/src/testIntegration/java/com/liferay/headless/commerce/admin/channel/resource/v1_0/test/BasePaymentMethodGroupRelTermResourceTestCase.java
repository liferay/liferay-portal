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
import com.liferay.headless.commerce.admin.channel.client.dto.v1_0.PaymentMethodGroupRelTerm;
import com.liferay.headless.commerce.admin.channel.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.channel.client.pagination.Page;
import com.liferay.headless.commerce.admin.channel.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.channel.client.resource.v1_0.PaymentMethodGroupRelTermResource;
import com.liferay.headless.commerce.admin.channel.client.serdes.v1_0.PaymentMethodGroupRelTermSerDes;
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
public abstract class BasePaymentMethodGroupRelTermResourceTestCase {

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

		_paymentMethodGroupRelTermResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		paymentMethodGroupRelTermResource =
			PaymentMethodGroupRelTermResource.builder(
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

		PaymentMethodGroupRelTerm paymentMethodGroupRelTerm1 =
			randomPaymentMethodGroupRelTerm();

		String json = objectMapper.writeValueAsString(
			paymentMethodGroupRelTerm1);

		PaymentMethodGroupRelTerm paymentMethodGroupRelTerm2 =
			PaymentMethodGroupRelTermSerDes.toDTO(json);

		Assert.assertTrue(
			equals(paymentMethodGroupRelTerm1, paymentMethodGroupRelTerm2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		PaymentMethodGroupRelTerm paymentMethodGroupRelTerm =
			randomPaymentMethodGroupRelTerm();

		String json1 = objectMapper.writeValueAsString(
			paymentMethodGroupRelTerm);
		String json2 = PaymentMethodGroupRelTermSerDes.toJSON(
			paymentMethodGroupRelTerm);

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

		PaymentMethodGroupRelTerm paymentMethodGroupRelTerm =
			randomPaymentMethodGroupRelTerm();

		paymentMethodGroupRelTerm.setTermExternalReferenceCode(regex);

		String json = PaymentMethodGroupRelTermSerDes.toJSON(
			paymentMethodGroupRelTerm);

		Assert.assertFalse(json.contains(regex));

		paymentMethodGroupRelTerm = PaymentMethodGroupRelTermSerDes.toDTO(json);

		Assert.assertEquals(
			regex, paymentMethodGroupRelTerm.getTermExternalReferenceCode());
	}

	@Test
	public void testDeletePaymentMethodGroupRelTerm() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		PaymentMethodGroupRelTerm paymentMethodGroupRelTerm =
			testDeletePaymentMethodGroupRelTerm_addPaymentMethodGroupRelTerm();

		assertHttpResponseStatusCode(
			204,
			paymentMethodGroupRelTermResource.
				deletePaymentMethodGroupRelTermHttpResponse(
					paymentMethodGroupRelTerm.
						getPaymentMethodGroupRelTermId()));
	}

	protected PaymentMethodGroupRelTerm
			testDeletePaymentMethodGroupRelTerm_addPaymentMethodGroupRelTerm()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeletePaymentMethodGroupRelTerm() throws Exception {

		// No namespace

		PaymentMethodGroupRelTerm paymentMethodGroupRelTerm1 =
			testGraphQLDeletePaymentMethodGroupRelTerm_addPaymentMethodGroupRelTerm();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deletePaymentMethodGroupRelTerm",
						new HashMap<String, Object>() {
							{
								put(
									"paymentMethodGroupRelTermId",
									paymentMethodGroupRelTerm1.
										getPaymentMethodGroupRelTermId());
							}
						})),
				"JSONObject/data", "Object/deletePaymentMethodGroupRelTerm"));

		// Using the namespace headlessCommerceAdminChannel_v1_0

		PaymentMethodGroupRelTerm paymentMethodGroupRelTerm2 =
			testGraphQLDeletePaymentMethodGroupRelTerm_addPaymentMethodGroupRelTerm();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminChannel_v1_0",
						new GraphQLField(
							"deletePaymentMethodGroupRelTerm",
							new HashMap<String, Object>() {
								{
									put(
										"paymentMethodGroupRelTermId",
										paymentMethodGroupRelTerm2.
											getPaymentMethodGroupRelTermId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminChannel_v1_0",
				"Object/deletePaymentMethodGroupRelTerm"));
	}

	protected PaymentMethodGroupRelTerm
			testGraphQLDeletePaymentMethodGroupRelTerm_addPaymentMethodGroupRelTerm()
		throws Exception {

		return testGraphQLPaymentMethodGroupRelTerm_addPaymentMethodGroupRelTerm();
	}

	@Test
	public void testDeletePaymentMethodGroupRelTermBatch() throws Exception {
		PaymentMethodGroupRelTerm paymentMethodGroupRelTerm1 =
			testDeletePaymentMethodGroupRelTermBatch_addPaymentMethodGroupRelTerm();

		testDeletePaymentMethodGroupRelTermBatch_deletePaymentMethodGroupRelTerm(
			202, null,
			paymentMethodGroupRelTerm1.getPaymentMethodGroupRelTermId());
	}

	protected PaymentMethodGroupRelTerm
			testDeletePaymentMethodGroupRelTermBatch_addPaymentMethodGroupRelTerm()
		throws Exception {

		return testDeletePaymentMethodGroupRelTerm_addPaymentMethodGroupRelTerm();
	}

	protected void
			testDeletePaymentMethodGroupRelTermBatch_deletePaymentMethodGroupRelTerm(
				int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			paymentMethodGroupRelTermResource.
				deletePaymentMethodGroupRelTermBatchHttpResponse(
					null,
					JSONUtil.putAll(
						JSONUtil.put(
							"externalReferenceCode", () -> externalReferenceCode
						).put(
							"paymentMethodGroupRelTermId", () -> id
						)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage()
		throws Exception {

		Long id =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage_getId();
		Long irrelevantId =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage_getIrrelevantId();

		Page<PaymentMethodGroupRelTerm> page =
			paymentMethodGroupRelTermResource.
				getPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage(
					id, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			PaymentMethodGroupRelTerm irrelevantPaymentMethodGroupRelTerm =
				testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage_addPaymentMethodGroupRelTerm(
					irrelevantId, randomIrrelevantPaymentMethodGroupRelTerm());

			page =
				paymentMethodGroupRelTermResource.
					getPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage(
						irrelevantId, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPaymentMethodGroupRelTerm,
				(List<PaymentMethodGroupRelTerm>)page.getItems());
			assertValid(
				page,
				testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage_getExpectedActions(
					irrelevantId));
		}

		PaymentMethodGroupRelTerm paymentMethodGroupRelTerm1 =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage_addPaymentMethodGroupRelTerm(
				id, randomPaymentMethodGroupRelTerm());

		PaymentMethodGroupRelTerm paymentMethodGroupRelTerm2 =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage_addPaymentMethodGroupRelTerm(
				id, randomPaymentMethodGroupRelTerm());

		page =
			paymentMethodGroupRelTermResource.
				getPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage(
					id, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			paymentMethodGroupRelTerm1,
			(List<PaymentMethodGroupRelTerm>)page.getItems());
		assertContains(
			paymentMethodGroupRelTerm2,
			(List<PaymentMethodGroupRelTerm>)page.getItems());
		assertValid(
			page,
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage_getExpectedActions(
				id));

		paymentMethodGroupRelTermResource.deletePaymentMethodGroupRelTerm(
			paymentMethodGroupRelTerm1.getPaymentMethodGroupRelTermId());

		paymentMethodGroupRelTermResource.deletePaymentMethodGroupRelTerm(
			paymentMethodGroupRelTerm2.getPaymentMethodGroupRelTermId());
	}

	protected Map<String, Map<String, String>>
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage_getExpectedActions(
				Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage_getId();

		PaymentMethodGroupRelTerm paymentMethodGroupRelTerm1 =
			randomPaymentMethodGroupRelTerm();

		paymentMethodGroupRelTerm1 =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage_addPaymentMethodGroupRelTerm(
				id, paymentMethodGroupRelTerm1);

		for (EntityField entityField : entityFields) {
			Page<PaymentMethodGroupRelTerm> page =
				paymentMethodGroupRelTermResource.
					getPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage(
						id, null,
						getFilterString(
							entityField, "between", paymentMethodGroupRelTerm1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(paymentMethodGroupRelTerm1),
				(List<PaymentMethodGroupRelTerm>)page.getItems());
		}
	}

	@Test
	public void testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPageWithFilterDoubleEquals()
		throws Exception {

		testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPageWithFilterStringContains()
		throws Exception {

		testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPageWithFilterStringEquals()
		throws Exception {

		testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPageWithFilterStringStartsWith()
		throws Exception {

		testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage_getId();

		PaymentMethodGroupRelTerm paymentMethodGroupRelTerm1 =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage_addPaymentMethodGroupRelTerm(
				id, randomPaymentMethodGroupRelTerm());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PaymentMethodGroupRelTerm paymentMethodGroupRelTerm2 =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage_addPaymentMethodGroupRelTerm(
				id, randomPaymentMethodGroupRelTerm());

		for (EntityField entityField : entityFields) {
			Page<PaymentMethodGroupRelTerm> page =
				paymentMethodGroupRelTermResource.
					getPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage(
						id, null,
						getFilterString(
							entityField, operator, paymentMethodGroupRelTerm1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(paymentMethodGroupRelTerm1),
				(List<PaymentMethodGroupRelTerm>)page.getItems());
		}
	}

	@Test
	public void testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPageWithPagination()
		throws Exception {

		Long id =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage_getId();

		Page<PaymentMethodGroupRelTerm> paymentMethodGroupRelTermsPage =
			paymentMethodGroupRelTermResource.
				getPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage(
					id, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			paymentMethodGroupRelTermsPage.getTotalCount());

		PaymentMethodGroupRelTerm paymentMethodGroupRelTerm1 =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage_addPaymentMethodGroupRelTerm(
				id, randomPaymentMethodGroupRelTerm());

		PaymentMethodGroupRelTerm paymentMethodGroupRelTerm2 =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage_addPaymentMethodGroupRelTerm(
				id, randomPaymentMethodGroupRelTerm());

		PaymentMethodGroupRelTerm paymentMethodGroupRelTerm3 =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage_addPaymentMethodGroupRelTerm(
				id, randomPaymentMethodGroupRelTerm());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<PaymentMethodGroupRelTerm> page1 =
				paymentMethodGroupRelTermResource.
					getPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				paymentMethodGroupRelTerm1,
				(List<PaymentMethodGroupRelTerm>)page1.getItems());

			Page<PaymentMethodGroupRelTerm> page2 =
				paymentMethodGroupRelTermResource.
					getPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				paymentMethodGroupRelTerm2,
				(List<PaymentMethodGroupRelTerm>)page2.getItems());

			Page<PaymentMethodGroupRelTerm> page3 =
				paymentMethodGroupRelTermResource.
					getPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				paymentMethodGroupRelTerm3,
				(List<PaymentMethodGroupRelTerm>)page3.getItems());
		}
		else {
			Page<PaymentMethodGroupRelTerm> page1 =
				paymentMethodGroupRelTermResource.
					getPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage(
						id, null, null, Pagination.of(1, totalCount + 2), null);

			List<PaymentMethodGroupRelTerm> paymentMethodGroupRelTerms1 =
				(List<PaymentMethodGroupRelTerm>)page1.getItems();

			Assert.assertEquals(
				paymentMethodGroupRelTerms1.toString(), totalCount + 2,
				paymentMethodGroupRelTerms1.size());

			Page<PaymentMethodGroupRelTerm> page2 =
				paymentMethodGroupRelTermResource.
					getPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage(
						id, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PaymentMethodGroupRelTerm> paymentMethodGroupRelTerms2 =
				(List<PaymentMethodGroupRelTerm>)page2.getItems();

			Assert.assertEquals(
				paymentMethodGroupRelTerms2.toString(), 1,
				paymentMethodGroupRelTerms2.size());

			Page<PaymentMethodGroupRelTerm> page3 =
				paymentMethodGroupRelTermResource.
					getPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage(
						id, null, null, Pagination.of(1, (int)totalCount + 3),
						null);

			assertContains(
				paymentMethodGroupRelTerm1,
				(List<PaymentMethodGroupRelTerm>)page3.getItems());
			assertContains(
				paymentMethodGroupRelTerm2,
				(List<PaymentMethodGroupRelTerm>)page3.getItems());
			assertContains(
				paymentMethodGroupRelTerm3,
				(List<PaymentMethodGroupRelTerm>)page3.getItems());
		}
	}

	@Test
	public void testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPageWithSortDateTime()
		throws Exception {

		testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, paymentMethodGroupRelTerm1,
			 paymentMethodGroupRelTerm2) -> {

				BeanTestUtil.setProperty(
					paymentMethodGroupRelTerm1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPageWithSortDouble()
		throws Exception {

		testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, paymentMethodGroupRelTerm1,
			 paymentMethodGroupRelTerm2) -> {

				BeanTestUtil.setProperty(
					paymentMethodGroupRelTerm1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					paymentMethodGroupRelTerm2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPageWithSortInteger()
		throws Exception {

		testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, paymentMethodGroupRelTerm1,
			 paymentMethodGroupRelTerm2) -> {

				BeanTestUtil.setProperty(
					paymentMethodGroupRelTerm1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					paymentMethodGroupRelTerm2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPageWithSortString()
		throws Exception {

		testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPageWithSort(
			EntityField.Type.STRING,
			(entityField, paymentMethodGroupRelTerm1,
			 paymentMethodGroupRelTerm2) -> {

				Class<?> clazz = paymentMethodGroupRelTerm1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						paymentMethodGroupRelTerm1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						paymentMethodGroupRelTerm2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						paymentMethodGroupRelTerm1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						paymentMethodGroupRelTerm2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						paymentMethodGroupRelTerm1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						paymentMethodGroupRelTerm2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, PaymentMethodGroupRelTerm,
					 PaymentMethodGroupRelTerm, Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage_getId();

		PaymentMethodGroupRelTerm paymentMethodGroupRelTerm1 =
			randomPaymentMethodGroupRelTerm();
		PaymentMethodGroupRelTerm paymentMethodGroupRelTerm2 =
			randomPaymentMethodGroupRelTerm();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, paymentMethodGroupRelTerm1,
				paymentMethodGroupRelTerm2);
		}

		paymentMethodGroupRelTerm1 =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage_addPaymentMethodGroupRelTerm(
				id, paymentMethodGroupRelTerm1);

		paymentMethodGroupRelTerm2 =
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage_addPaymentMethodGroupRelTerm(
				id, paymentMethodGroupRelTerm2);

		Page<PaymentMethodGroupRelTerm> page =
			paymentMethodGroupRelTermResource.
				getPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage(
					id, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<PaymentMethodGroupRelTerm> ascPage =
				paymentMethodGroupRelTermResource.
					getPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage(
						id, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				paymentMethodGroupRelTerm1,
				(List<PaymentMethodGroupRelTerm>)ascPage.getItems());
			assertContains(
				paymentMethodGroupRelTerm2,
				(List<PaymentMethodGroupRelTerm>)ascPage.getItems());

			Page<PaymentMethodGroupRelTerm> descPage =
				paymentMethodGroupRelTermResource.
					getPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage(
						id, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				paymentMethodGroupRelTerm2,
				(List<PaymentMethodGroupRelTerm>)descPage.getItems());
			assertContains(
				paymentMethodGroupRelTerm1,
				(List<PaymentMethodGroupRelTerm>)descPage.getItems());
		}
	}

	protected PaymentMethodGroupRelTerm
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage_addPaymentMethodGroupRelTerm(
				Long id, PaymentMethodGroupRelTerm paymentMethodGroupRelTerm)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetPaymentMethodGroupRelIdPaymentMethodGroupRelTermsPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostPaymentMethodGroupRelIdPaymentMethodGroupRelTerm()
		throws Exception {

		PaymentMethodGroupRelTerm randomPaymentMethodGroupRelTerm =
			randomPaymentMethodGroupRelTerm();

		PaymentMethodGroupRelTerm postPaymentMethodGroupRelTerm =
			testPostPaymentMethodGroupRelIdPaymentMethodGroupRelTerm_addPaymentMethodGroupRelTerm(
				randomPaymentMethodGroupRelTerm);

		assertEquals(
			randomPaymentMethodGroupRelTerm, postPaymentMethodGroupRelTerm);
		assertValid(postPaymentMethodGroupRelTerm);
	}

	protected PaymentMethodGroupRelTerm
			testPostPaymentMethodGroupRelIdPaymentMethodGroupRelTerm_addPaymentMethodGroupRelTerm(
				PaymentMethodGroupRelTerm paymentMethodGroupRelTerm)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		PaymentMethodGroupRelTerm paymentMethodGroupRelTerm1 =
			testBatchEngineDeleteImportTask_addPaymentMethodGroupRelTerm();

		testBatchEngineDeleteImportTask_deletePaymentMethodGroupRelTerm(
			200, null,
			paymentMethodGroupRelTerm1.getPaymentMethodGroupRelTermId());
	}

	protected PaymentMethodGroupRelTerm
			testBatchEngineDeleteImportTask_addPaymentMethodGroupRelTerm()
		throws Exception {

		return testDeletePaymentMethodGroupRelTerm_addPaymentMethodGroupRelTerm();
	}

	protected void
			testBatchEngineDeleteImportTask_deletePaymentMethodGroupRelTerm(
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
				"com.liferay.headless.commerce.admin.channel.dto.v1_0.PaymentMethodGroupRelTerm",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"paymentMethodGroupRelTermId", () -> id
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

	protected PaymentMethodGroupRelTerm
			testGraphQLPaymentMethodGroupRelTerm_addPaymentMethodGroupRelTerm()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		PaymentMethodGroupRelTerm paymentMethodGroupRelTerm,
		List<PaymentMethodGroupRelTerm> paymentMethodGroupRelTerms) {

		boolean contains = false;

		for (PaymentMethodGroupRelTerm item : paymentMethodGroupRelTerms) {
			if (equals(paymentMethodGroupRelTerm, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			paymentMethodGroupRelTerms + " does not contain " +
				paymentMethodGroupRelTerm,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		PaymentMethodGroupRelTerm paymentMethodGroupRelTerm1,
		PaymentMethodGroupRelTerm paymentMethodGroupRelTerm2) {

		Assert.assertTrue(
			paymentMethodGroupRelTerm1 + " does not equal " +
				paymentMethodGroupRelTerm2,
			equals(paymentMethodGroupRelTerm1, paymentMethodGroupRelTerm2));
	}

	protected void assertEquals(
		List<PaymentMethodGroupRelTerm> paymentMethodGroupRelTerms1,
		List<PaymentMethodGroupRelTerm> paymentMethodGroupRelTerms2) {

		Assert.assertEquals(
			paymentMethodGroupRelTerms1.size(),
			paymentMethodGroupRelTerms2.size());

		for (int i = 0; i < paymentMethodGroupRelTerms1.size(); i++) {
			PaymentMethodGroupRelTerm paymentMethodGroupRelTerm1 =
				paymentMethodGroupRelTerms1.get(i);
			PaymentMethodGroupRelTerm paymentMethodGroupRelTerm2 =
				paymentMethodGroupRelTerms2.get(i);

			assertEquals(
				paymentMethodGroupRelTerm1, paymentMethodGroupRelTerm2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<PaymentMethodGroupRelTerm> paymentMethodGroupRelTerms1,
		List<PaymentMethodGroupRelTerm> paymentMethodGroupRelTerms2) {

		Assert.assertEquals(
			paymentMethodGroupRelTerms1.size(),
			paymentMethodGroupRelTerms2.size());

		for (PaymentMethodGroupRelTerm paymentMethodGroupRelTerm1 :
				paymentMethodGroupRelTerms1) {

			boolean contains = false;

			for (PaymentMethodGroupRelTerm paymentMethodGroupRelTerm2 :
					paymentMethodGroupRelTerms2) {

				if (equals(
						paymentMethodGroupRelTerm1,
						paymentMethodGroupRelTerm2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				paymentMethodGroupRelTerms2 + " does not contain " +
					paymentMethodGroupRelTerm1,
				contains);
		}
	}

	protected void assertValid(
			PaymentMethodGroupRelTerm paymentMethodGroupRelTerm)
		throws Exception {

		boolean valid = true;

		if (paymentMethodGroupRelTerm.getPaymentMethodGroupRelTermId() ==
				null) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (paymentMethodGroupRelTerm.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentMethodGroupRelId", additionalAssertFieldName)) {

				if (paymentMethodGroupRelTerm.getPaymentMethodGroupRelId() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentMethodGroupRelTermId", additionalAssertFieldName)) {

				if (paymentMethodGroupRelTerm.
						getPaymentMethodGroupRelTermId() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("term", additionalAssertFieldName)) {
				if (paymentMethodGroupRelTerm.getTerm() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"termExternalReferenceCode", additionalAssertFieldName)) {

				if (paymentMethodGroupRelTerm.getTermExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("termId", additionalAssertFieldName)) {
				if (paymentMethodGroupRelTerm.getTermId() == null) {
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

	protected void assertValid(Page<PaymentMethodGroupRelTerm> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<PaymentMethodGroupRelTerm> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<PaymentMethodGroupRelTerm>
			paymentMethodGroupRelTerms = page.getItems();

		int size = paymentMethodGroupRelTerms.size();

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

		graphQLFields.add(new GraphQLField("paymentMethodGroupRelTermId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.channel.dto.v1_0.
						PaymentMethodGroupRelTerm.class)) {

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
		PaymentMethodGroupRelTerm paymentMethodGroupRelTerm1,
		PaymentMethodGroupRelTerm paymentMethodGroupRelTerm2) {

		if (paymentMethodGroupRelTerm1 == paymentMethodGroupRelTerm2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)paymentMethodGroupRelTerm1.getActions(),
						(Map)paymentMethodGroupRelTerm2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentMethodGroupRelId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						paymentMethodGroupRelTerm1.getPaymentMethodGroupRelId(),
						paymentMethodGroupRelTerm2.
							getPaymentMethodGroupRelId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentMethodGroupRelTermId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						paymentMethodGroupRelTerm1.
							getPaymentMethodGroupRelTermId(),
						paymentMethodGroupRelTerm2.
							getPaymentMethodGroupRelTermId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("term", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						paymentMethodGroupRelTerm1.getTerm(),
						paymentMethodGroupRelTerm2.getTerm())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"termExternalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						paymentMethodGroupRelTerm1.
							getTermExternalReferenceCode(),
						paymentMethodGroupRelTerm2.
							getTermExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("termId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						paymentMethodGroupRelTerm1.getTermId(),
						paymentMethodGroupRelTerm2.getTermId())) {

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

		if (!(_paymentMethodGroupRelTermResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_paymentMethodGroupRelTermResource;

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
		PaymentMethodGroupRelTerm paymentMethodGroupRelTerm) {

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

		if (entityFieldName.equals("paymentMethodGroupRelId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("paymentMethodGroupRelTermId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("term")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("termExternalReferenceCode")) {
			Object object =
				paymentMethodGroupRelTerm.getTermExternalReferenceCode();

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

		if (entityFieldName.equals("termId")) {
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

	protected PaymentMethodGroupRelTerm randomPaymentMethodGroupRelTerm()
		throws Exception {

		return new PaymentMethodGroupRelTerm() {
			{
				paymentMethodGroupRelId = RandomTestUtil.randomLong();
				paymentMethodGroupRelTermId = RandomTestUtil.randomLong();
				termExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				termId = RandomTestUtil.randomLong();
			}
		};
	}

	protected PaymentMethodGroupRelTerm
			randomIrrelevantPaymentMethodGroupRelTerm()
		throws Exception {

		PaymentMethodGroupRelTerm randomIrrelevantPaymentMethodGroupRelTerm =
			randomPaymentMethodGroupRelTerm();

		return randomIrrelevantPaymentMethodGroupRelTerm;
	}

	protected PaymentMethodGroupRelTerm randomPatchPaymentMethodGroupRelTerm()
		throws Exception {

		return randomPaymentMethodGroupRelTerm();
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

	protected PaymentMethodGroupRelTermResource
		paymentMethodGroupRelTermResource;
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
			BasePaymentMethodGroupRelTermResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.channel.resource.v1_0.
		PaymentMethodGroupRelTermResource _paymentMethodGroupRelTermResource;

}