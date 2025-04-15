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

import com.liferay.headless.commerce.delivery.order.client.dto.v1_0.PlacedOrderAddress;
import com.liferay.headless.commerce.delivery.order.client.http.HttpInvoker;
import com.liferay.headless.commerce.delivery.order.client.pagination.Page;
import com.liferay.headless.commerce.delivery.order.client.resource.v1_0.PlacedOrderAddressResource;
import com.liferay.headless.commerce.delivery.order.client.serdes.v1_0.PlacedOrderAddressSerDes;
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
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public abstract class BasePlacedOrderAddressResourceTestCase {

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

		_placedOrderAddressResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		placedOrderAddressResource = PlacedOrderAddressResource.builder(
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

		PlacedOrderAddress placedOrderAddress1 = randomPlacedOrderAddress();

		String json = objectMapper.writeValueAsString(placedOrderAddress1);

		PlacedOrderAddress placedOrderAddress2 = PlacedOrderAddressSerDes.toDTO(
			json);

		Assert.assertTrue(equals(placedOrderAddress1, placedOrderAddress2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		PlacedOrderAddress placedOrderAddress = randomPlacedOrderAddress();

		String json1 = objectMapper.writeValueAsString(placedOrderAddress);
		String json2 = PlacedOrderAddressSerDes.toJSON(placedOrderAddress);

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

		PlacedOrderAddress placedOrderAddress = randomPlacedOrderAddress();

		placedOrderAddress.setCity(regex);
		placedOrderAddress.setCountry(regex);
		placedOrderAddress.setCountryISOCode(regex);
		placedOrderAddress.setDescription(regex);
		placedOrderAddress.setExternalReferenceCode(regex);
		placedOrderAddress.setName(regex);
		placedOrderAddress.setPhoneNumber(regex);
		placedOrderAddress.setRegion(regex);
		placedOrderAddress.setRegionISOCode(regex);
		placedOrderAddress.setStreet1(regex);
		placedOrderAddress.setStreet2(regex);
		placedOrderAddress.setStreet3(regex);
		placedOrderAddress.setSubtype(regex);
		placedOrderAddress.setType(regex);
		placedOrderAddress.setVatNumber(regex);
		placedOrderAddress.setZip(regex);

		String json = PlacedOrderAddressSerDes.toJSON(placedOrderAddress);

		Assert.assertFalse(json.contains(regex));

		placedOrderAddress = PlacedOrderAddressSerDes.toDTO(json);

		Assert.assertEquals(regex, placedOrderAddress.getCity());
		Assert.assertEquals(regex, placedOrderAddress.getCountry());
		Assert.assertEquals(regex, placedOrderAddress.getCountryISOCode());
		Assert.assertEquals(regex, placedOrderAddress.getDescription());
		Assert.assertEquals(
			regex, placedOrderAddress.getExternalReferenceCode());
		Assert.assertEquals(regex, placedOrderAddress.getName());
		Assert.assertEquals(regex, placedOrderAddress.getPhoneNumber());
		Assert.assertEquals(regex, placedOrderAddress.getRegion());
		Assert.assertEquals(regex, placedOrderAddress.getRegionISOCode());
		Assert.assertEquals(regex, placedOrderAddress.getStreet1());
		Assert.assertEquals(regex, placedOrderAddress.getStreet2());
		Assert.assertEquals(regex, placedOrderAddress.getStreet3());
		Assert.assertEquals(regex, placedOrderAddress.getSubtype());
		Assert.assertEquals(regex, placedOrderAddress.getType());
		Assert.assertEquals(regex, placedOrderAddress.getVatNumber());
		Assert.assertEquals(regex, placedOrderAddress.getZip());
	}

	@Test
	public void testGetPlacedOrderByExternalReferenceCodePlacedOrderBillingAddress()
		throws Exception {

		PlacedOrderAddress postPlacedOrderAddress =
			testGetPlacedOrderByExternalReferenceCodePlacedOrderBillingAddress_addPlacedOrderAddress();

		PlacedOrderAddress getPlacedOrderAddress =
			placedOrderAddressResource.
				getPlacedOrderByExternalReferenceCodePlacedOrderBillingAddress(
					testGetPlacedOrderByExternalReferenceCodePlacedOrderBillingAddress_getExternalReferenceCode(
						postPlacedOrderAddress));

		assertEquals(postPlacedOrderAddress, getPlacedOrderAddress);
		assertValid(getPlacedOrderAddress);
	}

	protected String
			testGetPlacedOrderByExternalReferenceCodePlacedOrderBillingAddress_getExternalReferenceCode(
				PlacedOrderAddress placedOrderAddress)
		throws Exception {

		return placedOrderAddress.getExternalReferenceCode();
	}

	protected PlacedOrderAddress
			testGetPlacedOrderByExternalReferenceCodePlacedOrderBillingAddress_addPlacedOrderAddress()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetPlacedOrderByExternalReferenceCodePlacedOrderBillingAddress()
		throws Exception {

		PlacedOrderAddress placedOrderAddress =
			testGraphQLGetPlacedOrderByExternalReferenceCodePlacedOrderBillingAddress_addPlacedOrderAddress();

		// No namespace

		Assert.assertTrue(
			equals(
				placedOrderAddress,
				PlacedOrderAddressSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"placedOrderByExternalReferenceCodePlacedOrderBillingAddress",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												testGraphQLGetPlacedOrderByExternalReferenceCodePlacedOrderBillingAddress_getExternalReferenceCode(
													placedOrderAddress) + "\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/placedOrderByExternalReferenceCodePlacedOrderBillingAddress"))));

		// Using the namespace headlessCommerceDeliveryOrder_v1_0

		Assert.assertTrue(
			equals(
				placedOrderAddress,
				PlacedOrderAddressSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceDeliveryOrder_v1_0",
								new GraphQLField(
									"placedOrderByExternalReferenceCodePlacedOrderBillingAddress",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													testGraphQLGetPlacedOrderByExternalReferenceCodePlacedOrderBillingAddress_getExternalReferenceCode(
														placedOrderAddress) +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceDeliveryOrder_v1_0",
						"Object/placedOrderByExternalReferenceCodePlacedOrderBillingAddress"))));
	}

	protected String
			testGraphQLGetPlacedOrderByExternalReferenceCodePlacedOrderBillingAddress_getExternalReferenceCode(
				PlacedOrderAddress placedOrderAddress)
		throws Exception {

		return placedOrderAddress.getExternalReferenceCode();
	}

	@Test
	public void testGraphQLGetPlacedOrderByExternalReferenceCodePlacedOrderBillingAddressNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"placedOrderByExternalReferenceCodePlacedOrderBillingAddress",
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

		// Using the namespace headlessCommerceDeliveryOrder_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceDeliveryOrder_v1_0",
						new GraphQLField(
							"placedOrderByExternalReferenceCodePlacedOrderBillingAddress",
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

	protected PlacedOrderAddress
			testGraphQLGetPlacedOrderByExternalReferenceCodePlacedOrderBillingAddress_addPlacedOrderAddress()
		throws Exception {

		return testGraphQLPlacedOrderAddress_addPlacedOrderAddress();
	}

	@Test
	public void testGetPlacedOrderByExternalReferenceCodePlacedOrderShippingAddress()
		throws Exception {

		PlacedOrderAddress postPlacedOrderAddress =
			testGetPlacedOrderByExternalReferenceCodePlacedOrderShippingAddress_addPlacedOrderAddress();

		PlacedOrderAddress getPlacedOrderAddress =
			placedOrderAddressResource.
				getPlacedOrderByExternalReferenceCodePlacedOrderShippingAddress(
					testGetPlacedOrderByExternalReferenceCodePlacedOrderShippingAddress_getExternalReferenceCode(
						postPlacedOrderAddress));

		assertEquals(postPlacedOrderAddress, getPlacedOrderAddress);
		assertValid(getPlacedOrderAddress);
	}

	protected String
			testGetPlacedOrderByExternalReferenceCodePlacedOrderShippingAddress_getExternalReferenceCode(
				PlacedOrderAddress placedOrderAddress)
		throws Exception {

		return placedOrderAddress.getExternalReferenceCode();
	}

	protected PlacedOrderAddress
			testGetPlacedOrderByExternalReferenceCodePlacedOrderShippingAddress_addPlacedOrderAddress()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetPlacedOrderByExternalReferenceCodePlacedOrderShippingAddress()
		throws Exception {

		PlacedOrderAddress placedOrderAddress =
			testGraphQLGetPlacedOrderByExternalReferenceCodePlacedOrderShippingAddress_addPlacedOrderAddress();

		// No namespace

		Assert.assertTrue(
			equals(
				placedOrderAddress,
				PlacedOrderAddressSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"placedOrderByExternalReferenceCodePlacedOrderShippingAddress",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												testGraphQLGetPlacedOrderByExternalReferenceCodePlacedOrderShippingAddress_getExternalReferenceCode(
													placedOrderAddress) + "\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/placedOrderByExternalReferenceCodePlacedOrderShippingAddress"))));

		// Using the namespace headlessCommerceDeliveryOrder_v1_0

		Assert.assertTrue(
			equals(
				placedOrderAddress,
				PlacedOrderAddressSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceDeliveryOrder_v1_0",
								new GraphQLField(
									"placedOrderByExternalReferenceCodePlacedOrderShippingAddress",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													testGraphQLGetPlacedOrderByExternalReferenceCodePlacedOrderShippingAddress_getExternalReferenceCode(
														placedOrderAddress) +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceDeliveryOrder_v1_0",
						"Object/placedOrderByExternalReferenceCodePlacedOrderShippingAddress"))));
	}

	protected String
			testGraphQLGetPlacedOrderByExternalReferenceCodePlacedOrderShippingAddress_getExternalReferenceCode(
				PlacedOrderAddress placedOrderAddress)
		throws Exception {

		return placedOrderAddress.getExternalReferenceCode();
	}

	@Test
	public void testGraphQLGetPlacedOrderByExternalReferenceCodePlacedOrderShippingAddressNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"placedOrderByExternalReferenceCodePlacedOrderShippingAddress",
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

		// Using the namespace headlessCommerceDeliveryOrder_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceDeliveryOrder_v1_0",
						new GraphQLField(
							"placedOrderByExternalReferenceCodePlacedOrderShippingAddress",
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

	protected PlacedOrderAddress
			testGraphQLGetPlacedOrderByExternalReferenceCodePlacedOrderShippingAddress_addPlacedOrderAddress()
		throws Exception {

		return testGraphQLPlacedOrderAddress_addPlacedOrderAddress();
	}

	@Test
	public void testGetPlacedOrderPlacedOrderBillingAddress() throws Exception {
		PlacedOrderAddress postPlacedOrderAddress =
			testGetPlacedOrderPlacedOrderBillingAddress_addPlacedOrderAddress();

		PlacedOrderAddress getPlacedOrderAddress =
			placedOrderAddressResource.getPlacedOrderPlacedOrderBillingAddress(
				testGetPlacedOrderPlacedOrderBillingAddress_getPlacedOrderId());

		assertEquals(postPlacedOrderAddress, getPlacedOrderAddress);
		assertValid(getPlacedOrderAddress);
	}

	protected Long
			testGetPlacedOrderPlacedOrderBillingAddress_getPlacedOrderId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected PlacedOrderAddress
			testGetPlacedOrderPlacedOrderBillingAddress_addPlacedOrderAddress()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetPlacedOrderPlacedOrderBillingAddress()
		throws Exception {

		PlacedOrderAddress placedOrderAddress =
			testGraphQLGetPlacedOrderPlacedOrderBillingAddress_addPlacedOrderAddress();

		// No namespace

		Assert.assertTrue(
			equals(
				placedOrderAddress,
				PlacedOrderAddressSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"placedOrderPlacedOrderBillingAddress",
								new HashMap<String, Object>() {
									{
										put(
											"placedOrderId",
											testGraphQLGetPlacedOrderPlacedOrderBillingAddress_getPlacedOrderId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/placedOrderPlacedOrderBillingAddress"))));

		// Using the namespace headlessCommerceDeliveryOrder_v1_0

		Assert.assertTrue(
			equals(
				placedOrderAddress,
				PlacedOrderAddressSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceDeliveryOrder_v1_0",
								new GraphQLField(
									"placedOrderPlacedOrderBillingAddress",
									new HashMap<String, Object>() {
										{
											put(
												"placedOrderId",
												testGraphQLGetPlacedOrderPlacedOrderBillingAddress_getPlacedOrderId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceDeliveryOrder_v1_0",
						"Object/placedOrderPlacedOrderBillingAddress"))));
	}

	protected Long
			testGraphQLGetPlacedOrderPlacedOrderBillingAddress_getPlacedOrderId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetPlacedOrderPlacedOrderBillingAddressNotFound()
		throws Exception {

		Long irrelevantPlacedOrderId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"placedOrderPlacedOrderBillingAddress",
						new HashMap<String, Object>() {
							{
								put("placedOrderId", irrelevantPlacedOrderId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceDeliveryOrder_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceDeliveryOrder_v1_0",
						new GraphQLField(
							"placedOrderPlacedOrderBillingAddress",
							new HashMap<String, Object>() {
								{
									put(
										"placedOrderId",
										irrelevantPlacedOrderId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected PlacedOrderAddress
			testGraphQLGetPlacedOrderPlacedOrderBillingAddress_addPlacedOrderAddress()
		throws Exception {

		return testGraphQLPlacedOrderAddress_addPlacedOrderAddress();
	}

	@Test
	public void testGetPlacedOrderPlacedOrderShippingAddress()
		throws Exception {

		PlacedOrderAddress postPlacedOrderAddress =
			testGetPlacedOrderPlacedOrderShippingAddress_addPlacedOrderAddress();

		PlacedOrderAddress getPlacedOrderAddress =
			placedOrderAddressResource.getPlacedOrderPlacedOrderShippingAddress(
				testGetPlacedOrderPlacedOrderShippingAddress_getPlacedOrderId());

		assertEquals(postPlacedOrderAddress, getPlacedOrderAddress);
		assertValid(getPlacedOrderAddress);
	}

	protected Long
			testGetPlacedOrderPlacedOrderShippingAddress_getPlacedOrderId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected PlacedOrderAddress
			testGetPlacedOrderPlacedOrderShippingAddress_addPlacedOrderAddress()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetPlacedOrderPlacedOrderShippingAddress()
		throws Exception {

		PlacedOrderAddress placedOrderAddress =
			testGraphQLGetPlacedOrderPlacedOrderShippingAddress_addPlacedOrderAddress();

		// No namespace

		Assert.assertTrue(
			equals(
				placedOrderAddress,
				PlacedOrderAddressSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"placedOrderPlacedOrderShippingAddress",
								new HashMap<String, Object>() {
									{
										put(
											"placedOrderId",
											testGraphQLGetPlacedOrderPlacedOrderShippingAddress_getPlacedOrderId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/placedOrderPlacedOrderShippingAddress"))));

		// Using the namespace headlessCommerceDeliveryOrder_v1_0

		Assert.assertTrue(
			equals(
				placedOrderAddress,
				PlacedOrderAddressSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceDeliveryOrder_v1_0",
								new GraphQLField(
									"placedOrderPlacedOrderShippingAddress",
									new HashMap<String, Object>() {
										{
											put(
												"placedOrderId",
												testGraphQLGetPlacedOrderPlacedOrderShippingAddress_getPlacedOrderId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceDeliveryOrder_v1_0",
						"Object/placedOrderPlacedOrderShippingAddress"))));
	}

	protected Long
			testGraphQLGetPlacedOrderPlacedOrderShippingAddress_getPlacedOrderId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetPlacedOrderPlacedOrderShippingAddressNotFound()
		throws Exception {

		Long irrelevantPlacedOrderId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"placedOrderPlacedOrderShippingAddress",
						new HashMap<String, Object>() {
							{
								put("placedOrderId", irrelevantPlacedOrderId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceDeliveryOrder_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceDeliveryOrder_v1_0",
						new GraphQLField(
							"placedOrderPlacedOrderShippingAddress",
							new HashMap<String, Object>() {
								{
									put(
										"placedOrderId",
										irrelevantPlacedOrderId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected PlacedOrderAddress
			testGraphQLGetPlacedOrderPlacedOrderShippingAddress_addPlacedOrderAddress()
		throws Exception {

		return testGraphQLPlacedOrderAddress_addPlacedOrderAddress();
	}

	protected PlacedOrderAddress
			testGraphQLPlacedOrderAddress_addPlacedOrderAddress()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		PlacedOrderAddress placedOrderAddress,
		List<PlacedOrderAddress> placedOrderAddresses) {

		boolean contains = false;

		for (PlacedOrderAddress item : placedOrderAddresses) {
			if (equals(placedOrderAddress, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			placedOrderAddresses + " does not contain " + placedOrderAddress,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		PlacedOrderAddress placedOrderAddress1,
		PlacedOrderAddress placedOrderAddress2) {

		Assert.assertTrue(
			placedOrderAddress1 + " does not equal " + placedOrderAddress2,
			equals(placedOrderAddress1, placedOrderAddress2));
	}

	protected void assertEquals(
		List<PlacedOrderAddress> placedOrderAddresses1,
		List<PlacedOrderAddress> placedOrderAddresses2) {

		Assert.assertEquals(
			placedOrderAddresses1.size(), placedOrderAddresses2.size());

		for (int i = 0; i < placedOrderAddresses1.size(); i++) {
			PlacedOrderAddress placedOrderAddress1 = placedOrderAddresses1.get(
				i);
			PlacedOrderAddress placedOrderAddress2 = placedOrderAddresses2.get(
				i);

			assertEquals(placedOrderAddress1, placedOrderAddress2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<PlacedOrderAddress> placedOrderAddresses1,
		List<PlacedOrderAddress> placedOrderAddresses2) {

		Assert.assertEquals(
			placedOrderAddresses1.size(), placedOrderAddresses2.size());

		for (PlacedOrderAddress placedOrderAddress1 : placedOrderAddresses1) {
			boolean contains = false;

			for (PlacedOrderAddress placedOrderAddress2 :
					placedOrderAddresses2) {

				if (equals(placedOrderAddress1, placedOrderAddress2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				placedOrderAddresses2 + " does not contain " +
					placedOrderAddress1,
				contains);
		}
	}

	protected void assertValid(PlacedOrderAddress placedOrderAddress)
		throws Exception {

		boolean valid = true;

		if (placedOrderAddress.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("city", additionalAssertFieldName)) {
				if (placedOrderAddress.getCity() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("country", additionalAssertFieldName)) {
				if (placedOrderAddress.getCountry() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("countryISOCode", additionalAssertFieldName)) {
				if (placedOrderAddress.getCountryISOCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (placedOrderAddress.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (placedOrderAddress.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("latitude", additionalAssertFieldName)) {
				if (placedOrderAddress.getLatitude() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("longitude", additionalAssertFieldName)) {
				if (placedOrderAddress.getLongitude() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (placedOrderAddress.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("phoneNumber", additionalAssertFieldName)) {
				if (placedOrderAddress.getPhoneNumber() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("region", additionalAssertFieldName)) {
				if (placedOrderAddress.getRegion() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("regionISOCode", additionalAssertFieldName)) {
				if (placedOrderAddress.getRegionISOCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("street1", additionalAssertFieldName)) {
				if (placedOrderAddress.getStreet1() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("street2", additionalAssertFieldName)) {
				if (placedOrderAddress.getStreet2() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("street3", additionalAssertFieldName)) {
				if (placedOrderAddress.getStreet3() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("subtype", additionalAssertFieldName)) {
				if (placedOrderAddress.getSubtype() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (placedOrderAddress.getType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("typeId", additionalAssertFieldName)) {
				if (placedOrderAddress.getTypeId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("vatNumber", additionalAssertFieldName)) {
				if (placedOrderAddress.getVatNumber() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("zip", additionalAssertFieldName)) {
				if (placedOrderAddress.getZip() == null) {
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

	protected void assertValid(Page<PlacedOrderAddress> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<PlacedOrderAddress> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<PlacedOrderAddress> placedOrderAddresses =
			page.getItems();

		int size = placedOrderAddresses.size();

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
						PlacedOrderAddress.class)) {

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
		PlacedOrderAddress placedOrderAddress1,
		PlacedOrderAddress placedOrderAddress2) {

		if (placedOrderAddress1 == placedOrderAddress2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("city", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderAddress1.getCity(),
						placedOrderAddress2.getCity())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("country", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderAddress1.getCountry(),
						placedOrderAddress2.getCountry())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("countryISOCode", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderAddress1.getCountryISOCode(),
						placedOrderAddress2.getCountryISOCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderAddress1.getDescription(),
						placedOrderAddress2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrderAddress1.getExternalReferenceCode(),
						placedOrderAddress2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderAddress1.getId(),
						placedOrderAddress2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("latitude", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderAddress1.getLatitude(),
						placedOrderAddress2.getLatitude())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("longitude", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderAddress1.getLongitude(),
						placedOrderAddress2.getLongitude())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderAddress1.getName(),
						placedOrderAddress2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("phoneNumber", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderAddress1.getPhoneNumber(),
						placedOrderAddress2.getPhoneNumber())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("region", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderAddress1.getRegion(),
						placedOrderAddress2.getRegion())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("regionISOCode", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderAddress1.getRegionISOCode(),
						placedOrderAddress2.getRegionISOCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("street1", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderAddress1.getStreet1(),
						placedOrderAddress2.getStreet1())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("street2", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderAddress1.getStreet2(),
						placedOrderAddress2.getStreet2())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("street3", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderAddress1.getStreet3(),
						placedOrderAddress2.getStreet3())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("subtype", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderAddress1.getSubtype(),
						placedOrderAddress2.getSubtype())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderAddress1.getType(),
						placedOrderAddress2.getType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("typeId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderAddress1.getTypeId(),
						placedOrderAddress2.getTypeId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("vatNumber", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderAddress1.getVatNumber(),
						placedOrderAddress2.getVatNumber())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("zip", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderAddress1.getZip(),
						placedOrderAddress2.getZip())) {

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

		if (!(_placedOrderAddressResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_placedOrderAddressResource;

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
		PlacedOrderAddress placedOrderAddress) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("city")) {
			Object object = placedOrderAddress.getCity();

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

		if (entityFieldName.equals("country")) {
			Object object = placedOrderAddress.getCountry();

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

		if (entityFieldName.equals("countryISOCode")) {
			Object object = placedOrderAddress.getCountryISOCode();

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

		if (entityFieldName.equals("description")) {
			Object object = placedOrderAddress.getDescription();

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
			Object object = placedOrderAddress.getExternalReferenceCode();

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

		if (entityFieldName.equals("latitude")) {
			sb.append(String.valueOf(placedOrderAddress.getLatitude()));

			return sb.toString();
		}

		if (entityFieldName.equals("longitude")) {
			sb.append(String.valueOf(placedOrderAddress.getLongitude()));

			return sb.toString();
		}

		if (entityFieldName.equals("name")) {
			Object object = placedOrderAddress.getName();

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

		if (entityFieldName.equals("phoneNumber")) {
			Object object = placedOrderAddress.getPhoneNumber();

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

		if (entityFieldName.equals("region")) {
			Object object = placedOrderAddress.getRegion();

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

		if (entityFieldName.equals("regionISOCode")) {
			Object object = placedOrderAddress.getRegionISOCode();

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

		if (entityFieldName.equals("street1")) {
			Object object = placedOrderAddress.getStreet1();

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

		if (entityFieldName.equals("street2")) {
			Object object = placedOrderAddress.getStreet2();

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

		if (entityFieldName.equals("street3")) {
			Object object = placedOrderAddress.getStreet3();

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

		if (entityFieldName.equals("subtype")) {
			Object object = placedOrderAddress.getSubtype();

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

		if (entityFieldName.equals("type")) {
			Object object = placedOrderAddress.getType();

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

		if (entityFieldName.equals("typeId")) {
			sb.append(String.valueOf(placedOrderAddress.getTypeId()));

			return sb.toString();
		}

		if (entityFieldName.equals("vatNumber")) {
			Object object = placedOrderAddress.getVatNumber();

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

		if (entityFieldName.equals("zip")) {
			Object object = placedOrderAddress.getZip();

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

	protected PlacedOrderAddress randomPlacedOrderAddress() throws Exception {
		return new PlacedOrderAddress() {
			{
				city = StringUtil.toLowerCase(RandomTestUtil.randomString());
				country = StringUtil.toLowerCase(RandomTestUtil.randomString());
				countryISOCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				latitude = RandomTestUtil.randomDouble();
				longitude = RandomTestUtil.randomDouble();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				phoneNumber = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				region = StringUtil.toLowerCase(RandomTestUtil.randomString());
				regionISOCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				street1 = StringUtil.toLowerCase(RandomTestUtil.randomString());
				street2 = StringUtil.toLowerCase(RandomTestUtil.randomString());
				street3 = StringUtil.toLowerCase(RandomTestUtil.randomString());
				subtype = StringUtil.toLowerCase(RandomTestUtil.randomString());
				type = StringUtil.toLowerCase(RandomTestUtil.randomString());
				typeId = RandomTestUtil.randomInt();
				vatNumber = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				zip = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected PlacedOrderAddress randomIrrelevantPlacedOrderAddress()
		throws Exception {

		PlacedOrderAddress randomIrrelevantPlacedOrderAddress =
			randomPlacedOrderAddress();

		return randomIrrelevantPlacedOrderAddress;
	}

	protected PlacedOrderAddress randomPatchPlacedOrderAddress()
		throws Exception {

		return randomPlacedOrderAddress();
	}

	protected PlacedOrderAddressResource placedOrderAddressResource;
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
		LogFactoryUtil.getLog(BasePlacedOrderAddressResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.delivery.order.resource.v1_0.
		PlacedOrderAddressResource _placedOrderAddressResource;

}