/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.commerce.delivery.order.client.dto.v1_0.PlacedOrderItemShipment;
import com.liferay.headless.commerce.delivery.order.client.http.HttpInvoker;
import com.liferay.headless.commerce.delivery.order.client.pagination.Page;
import com.liferay.headless.commerce.delivery.order.client.resource.v1_0.PlacedOrderItemShipmentResource;
import com.liferay.headless.commerce.delivery.order.client.serdes.v1_0.PlacedOrderItemShipmentSerDes;
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
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.vulcan.resource.EntityModelResource;

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

import jakarta.annotation.Generated;

import jakarta.ws.rs.core.MultivaluedHashMap;

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
public abstract class BasePlacedOrderItemShipmentResourceTestCase {

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

		_placedOrderItemShipmentResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		placedOrderItemShipmentResource =
			PlacedOrderItemShipmentResource.builder(
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

		PlacedOrderItemShipment placedOrderItemShipment1 =
			randomPlacedOrderItemShipment();

		String json = objectMapper.writeValueAsString(placedOrderItemShipment1);

		PlacedOrderItemShipment placedOrderItemShipment2 =
			PlacedOrderItemShipmentSerDes.toDTO(json);

		Assert.assertTrue(
			equals(placedOrderItemShipment1, placedOrderItemShipment2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		PlacedOrderItemShipment placedOrderItemShipment =
			randomPlacedOrderItemShipment();

		String json1 = objectMapper.writeValueAsString(placedOrderItemShipment);
		String json2 = PlacedOrderItemShipmentSerDes.toJSON(
			placedOrderItemShipment);

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

		PlacedOrderItemShipment placedOrderItemShipment =
			randomPlacedOrderItemShipment();

		placedOrderItemShipment.setAuthor(regex);
		placedOrderItemShipment.setCarrier(regex);
		placedOrderItemShipment.setExternalReferenceCode(regex);
		placedOrderItemShipment.setShippingOptionName(regex);
		placedOrderItemShipment.setTrackingNumber(regex);
		placedOrderItemShipment.setTrackingURL(regex);
		placedOrderItemShipment.setUnitOfMeasureKey(regex);

		String json = PlacedOrderItemShipmentSerDes.toJSON(
			placedOrderItemShipment);

		Assert.assertFalse(json.contains(regex));

		placedOrderItemShipment = PlacedOrderItemShipmentSerDes.toDTO(json);

		Assert.assertEquals(regex, placedOrderItemShipment.getAuthor());
		Assert.assertEquals(regex, placedOrderItemShipment.getCarrier());
		Assert.assertEquals(
			regex, placedOrderItemShipment.getExternalReferenceCode());
		Assert.assertEquals(
			regex, placedOrderItemShipment.getShippingOptionName());
		Assert.assertEquals(regex, placedOrderItemShipment.getTrackingNumber());
		Assert.assertEquals(regex, placedOrderItemShipment.getTrackingURL());
		Assert.assertEquals(
			regex, placedOrderItemShipment.getUnitOfMeasureKey());
	}

	@Test
	public void testGetPlacedOrderItemByExternalReferenceCodePlacedOrderItemShipmentsPage()
		throws Exception {

		String externalReferenceCode =
			testGetPlacedOrderItemByExternalReferenceCodePlacedOrderItemShipmentsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetPlacedOrderItemByExternalReferenceCodePlacedOrderItemShipmentsPage_getIrrelevantExternalReferenceCode();

		Page<PlacedOrderItemShipment> page =
			placedOrderItemShipmentResource.
				getPlacedOrderItemByExternalReferenceCodePlacedOrderItemShipmentsPage(
					externalReferenceCode);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			PlacedOrderItemShipment irrelevantPlacedOrderItemShipment =
				testGetPlacedOrderItemByExternalReferenceCodePlacedOrderItemShipmentsPage_addPlacedOrderItemShipment(
					irrelevantExternalReferenceCode,
					randomIrrelevantPlacedOrderItemShipment());

			page =
				placedOrderItemShipmentResource.
					getPlacedOrderItemByExternalReferenceCodePlacedOrderItemShipmentsPage(
						irrelevantExternalReferenceCode);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPlacedOrderItemShipment,
				(List<PlacedOrderItemShipment>)page.getItems());
			assertValid(
				page,
				testGetPlacedOrderItemByExternalReferenceCodePlacedOrderItemShipmentsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		PlacedOrderItemShipment placedOrderItemShipment1 =
			testGetPlacedOrderItemByExternalReferenceCodePlacedOrderItemShipmentsPage_addPlacedOrderItemShipment(
				externalReferenceCode, randomPlacedOrderItemShipment());

		PlacedOrderItemShipment placedOrderItemShipment2 =
			testGetPlacedOrderItemByExternalReferenceCodePlacedOrderItemShipmentsPage_addPlacedOrderItemShipment(
				externalReferenceCode, randomPlacedOrderItemShipment());

		page =
			placedOrderItemShipmentResource.
				getPlacedOrderItemByExternalReferenceCodePlacedOrderItemShipmentsPage(
					externalReferenceCode);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			placedOrderItemShipment1,
			(List<PlacedOrderItemShipment>)page.getItems());
		assertContains(
			placedOrderItemShipment2,
			(List<PlacedOrderItemShipment>)page.getItems());
		assertValid(
			page,
			testGetPlacedOrderItemByExternalReferenceCodePlacedOrderItemShipmentsPage_getExpectedActions(
				externalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetPlacedOrderItemByExternalReferenceCodePlacedOrderItemShipmentsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected PlacedOrderItemShipment
			testGetPlacedOrderItemByExternalReferenceCodePlacedOrderItemShipmentsPage_addPlacedOrderItemShipment(
				String externalReferenceCode,
				PlacedOrderItemShipment placedOrderItemShipment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetPlacedOrderItemByExternalReferenceCodePlacedOrderItemShipmentsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetPlacedOrderItemByExternalReferenceCodePlacedOrderItemShipmentsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetPlacedOrderItemPlacedOrderItemShipmentsPage()
		throws Exception {

		Long placedOrderItemId =
			testGetPlacedOrderItemPlacedOrderItemShipmentsPage_getPlacedOrderItemId();
		Long irrelevantPlacedOrderItemId =
			testGetPlacedOrderItemPlacedOrderItemShipmentsPage_getIrrelevantPlacedOrderItemId();

		Page<PlacedOrderItemShipment> page =
			placedOrderItemShipmentResource.
				getPlacedOrderItemPlacedOrderItemShipmentsPage(
					placedOrderItemId);

		long totalCount = page.getTotalCount();

		if (irrelevantPlacedOrderItemId != null) {
			PlacedOrderItemShipment irrelevantPlacedOrderItemShipment =
				testGetPlacedOrderItemPlacedOrderItemShipmentsPage_addPlacedOrderItemShipment(
					irrelevantPlacedOrderItemId,
					randomIrrelevantPlacedOrderItemShipment());

			page =
				placedOrderItemShipmentResource.
					getPlacedOrderItemPlacedOrderItemShipmentsPage(
						irrelevantPlacedOrderItemId);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPlacedOrderItemShipment,
				(List<PlacedOrderItemShipment>)page.getItems());
			assertValid(
				page,
				testGetPlacedOrderItemPlacedOrderItemShipmentsPage_getExpectedActions(
					irrelevantPlacedOrderItemId));
		}

		PlacedOrderItemShipment placedOrderItemShipment1 =
			testGetPlacedOrderItemPlacedOrderItemShipmentsPage_addPlacedOrderItemShipment(
				placedOrderItemId, randomPlacedOrderItemShipment());

		PlacedOrderItemShipment placedOrderItemShipment2 =
			testGetPlacedOrderItemPlacedOrderItemShipmentsPage_addPlacedOrderItemShipment(
				placedOrderItemId, randomPlacedOrderItemShipment());

		page =
			placedOrderItemShipmentResource.
				getPlacedOrderItemPlacedOrderItemShipmentsPage(
					placedOrderItemId);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			placedOrderItemShipment1,
			(List<PlacedOrderItemShipment>)page.getItems());
		assertContains(
			placedOrderItemShipment2,
			(List<PlacedOrderItemShipment>)page.getItems());
		assertValid(
			page,
			testGetPlacedOrderItemPlacedOrderItemShipmentsPage_getExpectedActions(
				placedOrderItemId));
	}

	protected Map<String, Map<String, String>>
			testGetPlacedOrderItemPlacedOrderItemShipmentsPage_getExpectedActions(
				Long placedOrderItemId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected PlacedOrderItemShipment
			testGetPlacedOrderItemPlacedOrderItemShipmentsPage_addPlacedOrderItemShipment(
				Long placedOrderItemId,
				PlacedOrderItemShipment placedOrderItemShipment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetPlacedOrderItemPlacedOrderItemShipmentsPage_getPlacedOrderItemId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetPlacedOrderItemPlacedOrderItemShipmentsPage_getIrrelevantPlacedOrderItemId()
		throws Exception {

		return null;
	}

	protected PlacedOrderItemShipment
			testGraphQLPlacedOrderItemShipment_addPlacedOrderItemShipment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		PlacedOrderItemShipment placedOrderItemShipment,
		List<PlacedOrderItemShipment> placedOrderItemShipments) {

		boolean contains = false;

		for (PlacedOrderItemShipment item : placedOrderItemShipments) {
			if (equals(placedOrderItemShipment, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			placedOrderItemShipments + " does not contain " +
				placedOrderItemShipment,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		PlacedOrderItemShipment placedOrderItemShipment1,
		PlacedOrderItemShipment placedOrderItemShipment2) {

		Assert.assertTrue(
			placedOrderItemShipment1 + " does not equal " +
				placedOrderItemShipment2,
			equals(placedOrderItemShipment1, placedOrderItemShipment2));
	}

	protected void assertEquals(
		List<PlacedOrderItemShipment> placedOrderItemShipments1,
		List<PlacedOrderItemShipment> placedOrderItemShipments2) {

		Assert.assertEquals(
			placedOrderItemShipments1.size(), placedOrderItemShipments2.size());

		for (int i = 0; i < placedOrderItemShipments1.size(); i++) {
			PlacedOrderItemShipment placedOrderItemShipment1 =
				placedOrderItemShipments1.get(i);
			PlacedOrderItemShipment placedOrderItemShipment2 =
				placedOrderItemShipments2.get(i);

			assertEquals(placedOrderItemShipment1, placedOrderItemShipment2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<PlacedOrderItemShipment> placedOrderItemShipments1,
		List<PlacedOrderItemShipment> placedOrderItemShipments2) {

		Assert.assertEquals(
			placedOrderItemShipments1.size(), placedOrderItemShipments2.size());

		for (PlacedOrderItemShipment placedOrderItemShipment1 :
				placedOrderItemShipments1) {

			boolean contains = false;

			for (PlacedOrderItemShipment placedOrderItemShipment2 :
					placedOrderItemShipments2) {

				if (equals(
						placedOrderItemShipment1, placedOrderItemShipment2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				placedOrderItemShipments2 + " does not contain " +
					placedOrderItemShipment1,
				contains);
		}
	}

	protected void assertValid(PlacedOrderItemShipment placedOrderItemShipment)
		throws Exception {

		boolean valid = true;

		if (placedOrderItemShipment.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (placedOrderItemShipment.getAccountId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("author", additionalAssertFieldName)) {
				if (placedOrderItemShipment.getAuthor() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("carrier", additionalAssertFieldName)) {
				if (placedOrderItemShipment.getCarrier() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (placedOrderItemShipment.getCreateDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"estimatedDeliveryDate", additionalAssertFieldName)) {

				if (placedOrderItemShipment.getEstimatedDeliveryDate() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"estimatedShippingDate", additionalAssertFieldName)) {

				if (placedOrderItemShipment.getEstimatedShippingDate() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (placedOrderItemShipment.getExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("modifiedDate", additionalAssertFieldName)) {
				if (placedOrderItemShipment.getModifiedDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderId", additionalAssertFieldName)) {
				if (placedOrderItemShipment.getOrderId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("quantity", additionalAssertFieldName)) {
				if (placedOrderItemShipment.getQuantity() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingAddressId", additionalAssertFieldName)) {

				if (placedOrderItemShipment.getShippingAddressId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("shippingMethodId", additionalAssertFieldName)) {
				if (placedOrderItemShipment.getShippingMethodId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingOptionName", additionalAssertFieldName)) {

				if (placedOrderItemShipment.getShippingOptionName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (placedOrderItemShipment.getStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("supplierShipment", additionalAssertFieldName)) {
				if (placedOrderItemShipment.getSupplierShipment() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("trackingNumber", additionalAssertFieldName)) {
				if (placedOrderItemShipment.getTrackingNumber() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("trackingURL", additionalAssertFieldName)) {
				if (placedOrderItemShipment.getTrackingURL() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("unitOfMeasureKey", additionalAssertFieldName)) {
				if (placedOrderItemShipment.getUnitOfMeasureKey() == null) {
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

	protected void assertValid(Page<PlacedOrderItemShipment> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<PlacedOrderItemShipment> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<PlacedOrderItemShipment> placedOrderItemShipments =
			page.getItems();

		int size = placedOrderItemShipments.size();

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
					com.liferay.headless.commerce.delivery.order.dto.v1_0.
						PlacedOrderItemShipment.class)) {

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
		PlacedOrderItemShipment placedOrderItemShipment1,
		PlacedOrderItemShipment placedOrderItemShipment2) {

		if (placedOrderItemShipment1 == placedOrderItemShipment2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItemShipment1.getAccountId(),
						placedOrderItemShipment2.getAccountId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("author", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItemShipment1.getAuthor(),
						placedOrderItemShipment2.getAuthor())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("carrier", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItemShipment1.getCarrier(),
						placedOrderItemShipment2.getCarrier())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItemShipment1.getCreateDate(),
						placedOrderItemShipment2.getCreateDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"estimatedDeliveryDate", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrderItemShipment1.getEstimatedDeliveryDate(),
						placedOrderItemShipment2.getEstimatedDeliveryDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"estimatedShippingDate", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrderItemShipment1.getEstimatedShippingDate(),
						placedOrderItemShipment2.getEstimatedShippingDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrderItemShipment1.getExternalReferenceCode(),
						placedOrderItemShipment2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItemShipment1.getId(),
						placedOrderItemShipment2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("modifiedDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItemShipment1.getModifiedDate(),
						placedOrderItemShipment2.getModifiedDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItemShipment1.getOrderId(),
						placedOrderItemShipment2.getOrderId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("quantity", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItemShipment1.getQuantity(),
						placedOrderItemShipment2.getQuantity())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingAddressId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrderItemShipment1.getShippingAddressId(),
						placedOrderItemShipment2.getShippingAddressId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("shippingMethodId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItemShipment1.getShippingMethodId(),
						placedOrderItemShipment2.getShippingMethodId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingOptionName", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrderItemShipment1.getShippingOptionName(),
						placedOrderItemShipment2.getShippingOptionName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItemShipment1.getStatus(),
						placedOrderItemShipment2.getStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("supplierShipment", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItemShipment1.getSupplierShipment(),
						placedOrderItemShipment2.getSupplierShipment())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("trackingNumber", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItemShipment1.getTrackingNumber(),
						placedOrderItemShipment2.getTrackingNumber())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("trackingURL", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItemShipment1.getTrackingURL(),
						placedOrderItemShipment2.getTrackingURL())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("unitOfMeasureKey", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItemShipment1.getUnitOfMeasureKey(),
						placedOrderItemShipment2.getUnitOfMeasureKey())) {

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

		if (!(_placedOrderItemShipmentResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_placedOrderItemShipmentResource;

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
		PlacedOrderItemShipment placedOrderItemShipment) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("accountId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("author")) {
			Object object = placedOrderItemShipment.getAuthor();

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

		if (entityFieldName.equals("carrier")) {
			Object object = placedOrderItemShipment.getCarrier();

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

		if (entityFieldName.equals("createDate")) {
			if (operator.equals("between")) {
				Date date = placedOrderItemShipment.getCreateDate();

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
					_format.format(placedOrderItemShipment.getCreateDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("estimatedDeliveryDate")) {
			if (operator.equals("between")) {
				Date date = placedOrderItemShipment.getEstimatedDeliveryDate();

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
					_format.format(
						placedOrderItemShipment.getEstimatedDeliveryDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("estimatedShippingDate")) {
			if (operator.equals("between")) {
				Date date = placedOrderItemShipment.getEstimatedShippingDate();

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
					_format.format(
						placedOrderItemShipment.getEstimatedShippingDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = placedOrderItemShipment.getExternalReferenceCode();

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

		if (entityFieldName.equals("modifiedDate")) {
			if (operator.equals("between")) {
				Date date = placedOrderItemShipment.getModifiedDate();

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
					_format.format(placedOrderItemShipment.getModifiedDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("orderId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("quantity")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippingAddressId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippingMethodId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippingOptionName")) {
			Object object = placedOrderItemShipment.getShippingOptionName();

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

		if (entityFieldName.equals("status")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("supplierShipment")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("trackingNumber")) {
			Object object = placedOrderItemShipment.getTrackingNumber();

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

		if (entityFieldName.equals("trackingURL")) {
			Object object = placedOrderItemShipment.getTrackingURL();

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

		if (entityFieldName.equals("unitOfMeasureKey")) {
			Object object = placedOrderItemShipment.getUnitOfMeasureKey();

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

	protected PlacedOrderItemShipment randomPlacedOrderItemShipment()
		throws Exception {

		return new PlacedOrderItemShipment() {
			{
				accountId = RandomTestUtil.randomLong();
				author = StringUtil.toLowerCase(RandomTestUtil.randomString());
				carrier = StringUtil.toLowerCase(RandomTestUtil.randomString());
				createDate = RandomTestUtil.nextDate();
				estimatedDeliveryDate = RandomTestUtil.nextDate();
				estimatedShippingDate = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				modifiedDate = RandomTestUtil.nextDate();
				orderId = RandomTestUtil.randomLong();
				shippingAddressId = RandomTestUtil.randomLong();
				shippingMethodId = RandomTestUtil.randomLong();
				shippingOptionName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				supplierShipment = RandomTestUtil.randomBoolean();
				trackingNumber = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				trackingURL = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				unitOfMeasureKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected PlacedOrderItemShipment randomIrrelevantPlacedOrderItemShipment()
		throws Exception {

		PlacedOrderItemShipment randomIrrelevantPlacedOrderItemShipment =
			randomPlacedOrderItemShipment();

		return randomIrrelevantPlacedOrderItemShipment;
	}

	protected PlacedOrderItemShipment randomPatchPlacedOrderItemShipment()
		throws Exception {

		return randomPlacedOrderItemShipment();
	}

	protected PlacedOrderItemShipmentResource placedOrderItemShipmentResource;
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
			BasePlacedOrderItemShipmentResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.delivery.order.resource.v1_0.
		PlacedOrderItemShipmentResource _placedOrderItemShipmentResource;

}