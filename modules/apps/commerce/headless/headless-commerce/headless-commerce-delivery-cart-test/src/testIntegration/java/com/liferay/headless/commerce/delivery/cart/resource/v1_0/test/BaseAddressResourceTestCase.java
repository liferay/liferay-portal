/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.commerce.delivery.cart.client.dto.v1_0.Address;
import com.liferay.headless.commerce.delivery.cart.client.http.HttpInvoker;
import com.liferay.headless.commerce.delivery.cart.client.pagination.Page;
import com.liferay.headless.commerce.delivery.cart.client.resource.v1_0.AddressResource;
import com.liferay.headless.commerce.delivery.cart.client.serdes.v1_0.AddressSerDes;
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
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
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
public abstract class BaseAddressResourceTestCase {

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

		_addressResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		addressResource = AddressResource.builder(
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

		Address address1 = randomAddress();

		String json = objectMapper.writeValueAsString(address1);

		Address address2 = AddressSerDes.toDTO(json);

		Assert.assertTrue(equals(address1, address2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Address address = randomAddress();

		String json1 = objectMapper.writeValueAsString(address);
		String json2 = AddressSerDes.toJSON(address);

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

		Address address = randomAddress();

		address.setCity(regex);
		address.setCountry(regex);
		address.setCountryISOCode(regex);
		address.setDescription(regex);
		address.setExternalReferenceCode(regex);
		address.setName(regex);
		address.setPhoneNumber(regex);
		address.setRegion(regex);
		address.setRegionISOCode(regex);
		address.setStreet1(regex);
		address.setStreet2(regex);
		address.setStreet3(regex);
		address.setSubtype(regex);
		address.setType(regex);
		address.setVatNumber(regex);
		address.setZip(regex);

		String json = AddressSerDes.toJSON(address);

		Assert.assertFalse(json.contains(regex));

		address = AddressSerDes.toDTO(json);

		Assert.assertEquals(regex, address.getCity());
		Assert.assertEquals(regex, address.getCountry());
		Assert.assertEquals(regex, address.getCountryISOCode());
		Assert.assertEquals(regex, address.getDescription());
		Assert.assertEquals(regex, address.getExternalReferenceCode());
		Assert.assertEquals(regex, address.getName());
		Assert.assertEquals(regex, address.getPhoneNumber());
		Assert.assertEquals(regex, address.getRegion());
		Assert.assertEquals(regex, address.getRegionISOCode());
		Assert.assertEquals(regex, address.getStreet1());
		Assert.assertEquals(regex, address.getStreet2());
		Assert.assertEquals(regex, address.getStreet3());
		Assert.assertEquals(regex, address.getSubtype());
		Assert.assertEquals(regex, address.getType());
		Assert.assertEquals(regex, address.getVatNumber());
		Assert.assertEquals(regex, address.getZip());
	}

	@Test
	public void testGetCartBillingAddres() throws Exception {
		Address postAddress = testGetCartBillingAddres_addAddress();

		Address getAddress = addressResource.getCartBillingAddres(
			testGetCartBillingAddres_getCartId());

		assertEquals(postAddress, getAddress);
		assertValid(getAddress);
	}

	protected Address testGetCartBillingAddres_addAddress() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetCartBillingAddres_getCartId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetCartBillingAddres() throws Exception {
		Address address = testGraphQLGetCartBillingAddres_addAddress();

		// No namespace

		Assert.assertTrue(
			equals(
				address,
				AddressSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"cartBillingAddres",
								new HashMap<String, Object>() {
									{
										put(
											"cartId",
											testGraphQLGetCartBillingAddres_getCartId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/cartBillingAddres"))));

		// Using the namespace headlessCommerceDeliveryCart_v1_0

		Assert.assertTrue(
			equals(
				address,
				AddressSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceDeliveryCart_v1_0",
								new GraphQLField(
									"cartBillingAddres",
									new HashMap<String, Object>() {
										{
											put(
												"cartId",
												testGraphQLGetCartBillingAddres_getCartId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceDeliveryCart_v1_0",
						"Object/cartBillingAddres"))));
	}

	protected Long testGraphQLGetCartBillingAddres_getCartId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetCartBillingAddresNotFound() throws Exception {
		Long irrelevantCartId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"cartBillingAddres",
						new HashMap<String, Object>() {
							{
								put("cartId", irrelevantCartId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceDeliveryCart_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceDeliveryCart_v1_0",
						new GraphQLField(
							"cartBillingAddres",
							new HashMap<String, Object>() {
								{
									put("cartId", irrelevantCartId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Address testGraphQLGetCartBillingAddres_addAddress()
		throws Exception {

		return testGraphQLCartAddress_addAddress();
	}

	@Test
	public void testGetCartByExternalReferenceCodeBillingAddress()
		throws Exception {

		Address postAddress =
			testGetCartByExternalReferenceCodeBillingAddress_addAddress();

		Address getAddress =
			addressResource.getCartByExternalReferenceCodeBillingAddress(
				testGetCartByExternalReferenceCodeBillingAddress_getExternalReferenceCode(
					postAddress));

		assertEquals(postAddress, getAddress);
		assertValid(getAddress);
	}

	protected Address
			testGetCartByExternalReferenceCodeBillingAddress_addAddress()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetCartByExternalReferenceCodeBillingAddress_getExternalReferenceCode(
				Address address)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetCartByExternalReferenceCodeBillingAddress()
		throws Exception {

		Address address =
			testGraphQLGetCartByExternalReferenceCodeBillingAddress_addAddress();

		// No namespace

		Assert.assertTrue(
			equals(
				address,
				AddressSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"cartByExternalReferenceCodeBillingAddress",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												testGraphQLGetCartByExternalReferenceCodeBillingAddress_getExternalReferenceCode(
													address) + "\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/cartByExternalReferenceCodeBillingAddress"))));

		// Using the namespace headlessCommerceDeliveryCart_v1_0

		Assert.assertTrue(
			equals(
				address,
				AddressSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceDeliveryCart_v1_0",
								new GraphQLField(
									"cartByExternalReferenceCodeBillingAddress",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													testGraphQLGetCartByExternalReferenceCodeBillingAddress_getExternalReferenceCode(
														address) + "\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceDeliveryCart_v1_0",
						"Object/cartByExternalReferenceCodeBillingAddress"))));
	}

	protected String
			testGraphQLGetCartByExternalReferenceCodeBillingAddress_getExternalReferenceCode(
				Address address)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetCartByExternalReferenceCodeBillingAddressNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"cartByExternalReferenceCodeBillingAddress",
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

		// Using the namespace headlessCommerceDeliveryCart_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceDeliveryCart_v1_0",
						new GraphQLField(
							"cartByExternalReferenceCodeBillingAddress",
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

	protected Address
			testGraphQLGetCartByExternalReferenceCodeBillingAddress_addAddress()
		throws Exception {

		return testGraphQLCartAddress_addAddress();
	}

	@Test
	public void testGetCartByExternalReferenceCodeShippingAddress()
		throws Exception {

		Address postAddress =
			testGetCartByExternalReferenceCodeShippingAddress_addAddress();

		Address getAddress =
			addressResource.getCartByExternalReferenceCodeShippingAddress(
				testGetCartByExternalReferenceCodeShippingAddress_getExternalReferenceCode(
					postAddress));

		assertEquals(postAddress, getAddress);
		assertValid(getAddress);
	}

	protected Address
			testGetCartByExternalReferenceCodeShippingAddress_addAddress()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetCartByExternalReferenceCodeShippingAddress_getExternalReferenceCode(
				Address address)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetCartByExternalReferenceCodeShippingAddress()
		throws Exception {

		Address address =
			testGraphQLGetCartByExternalReferenceCodeShippingAddress_addAddress();

		// No namespace

		Assert.assertTrue(
			equals(
				address,
				AddressSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"cartByExternalReferenceCodeShippingAddress",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												testGraphQLGetCartByExternalReferenceCodeShippingAddress_getExternalReferenceCode(
													address) + "\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/cartByExternalReferenceCodeShippingAddress"))));

		// Using the namespace headlessCommerceDeliveryCart_v1_0

		Assert.assertTrue(
			equals(
				address,
				AddressSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceDeliveryCart_v1_0",
								new GraphQLField(
									"cartByExternalReferenceCodeShippingAddress",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													testGraphQLGetCartByExternalReferenceCodeShippingAddress_getExternalReferenceCode(
														address) + "\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceDeliveryCart_v1_0",
						"Object/cartByExternalReferenceCodeShippingAddress"))));
	}

	protected String
			testGraphQLGetCartByExternalReferenceCodeShippingAddress_getExternalReferenceCode(
				Address address)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetCartByExternalReferenceCodeShippingAddressNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"cartByExternalReferenceCodeShippingAddress",
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

		// Using the namespace headlessCommerceDeliveryCart_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceDeliveryCart_v1_0",
						new GraphQLField(
							"cartByExternalReferenceCodeShippingAddress",
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

	protected Address
			testGraphQLGetCartByExternalReferenceCodeShippingAddress_addAddress()
		throws Exception {

		return testGraphQLCartAddress_addAddress();
	}

	@Test
	public void testGetCartShippingAddres() throws Exception {
		Address postAddress = testGetCartShippingAddres_addAddress();

		Address getAddress = addressResource.getCartShippingAddres(
			testGetCartShippingAddres_getCartId());

		assertEquals(postAddress, getAddress);
		assertValid(getAddress);
	}

	protected Address testGetCartShippingAddres_addAddress() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetCartShippingAddres_getCartId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetCartShippingAddres() throws Exception {
		Address address = testGraphQLGetCartShippingAddres_addAddress();

		// No namespace

		Assert.assertTrue(
			equals(
				address,
				AddressSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"cartShippingAddres",
								new HashMap<String, Object>() {
									{
										put(
											"cartId",
											testGraphQLGetCartShippingAddres_getCartId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/cartShippingAddres"))));

		// Using the namespace headlessCommerceDeliveryCart_v1_0

		Assert.assertTrue(
			equals(
				address,
				AddressSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceDeliveryCart_v1_0",
								new GraphQLField(
									"cartShippingAddres",
									new HashMap<String, Object>() {
										{
											put(
												"cartId",
												testGraphQLGetCartShippingAddres_getCartId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceDeliveryCart_v1_0",
						"Object/cartShippingAddres"))));
	}

	protected Long testGraphQLGetCartShippingAddres_getCartId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetCartShippingAddresNotFound() throws Exception {
		Long irrelevantCartId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"cartShippingAddres",
						new HashMap<String, Object>() {
							{
								put("cartId", irrelevantCartId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceDeliveryCart_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceDeliveryCart_v1_0",
						new GraphQLField(
							"cartShippingAddres",
							new HashMap<String, Object>() {
								{
									put("cartId", irrelevantCartId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Address testGraphQLGetCartShippingAddres_addAddress()
		throws Exception {

		return testGraphQLCartAddress_addAddress();
	}

	protected Address testGraphQLCartAddress_addAddress() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(Address address, List<Address> addresses) {
		boolean contains = false;

		for (Address item : addresses) {
			if (equals(address, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(addresses + " does not contain " + address, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Address address1, Address address2) {
		Assert.assertTrue(
			address1 + " does not equal " + address2,
			equals(address1, address2));
	}

	protected void assertEquals(
		List<Address> addresses1, List<Address> addresses2) {

		Assert.assertEquals(addresses1.size(), addresses2.size());

		for (int i = 0; i < addresses1.size(); i++) {
			Address address1 = addresses1.get(i);
			Address address2 = addresses2.get(i);

			assertEquals(address1, address2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Address> addresses1, List<Address> addresses2) {

		Assert.assertEquals(addresses1.size(), addresses2.size());

		for (Address address1 : addresses1) {
			boolean contains = false;

			for (Address address2 : addresses2) {
				if (equals(address1, address2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				addresses2 + " does not contain " + address1, contains);
		}
	}

	protected void assertValid(Address address) throws Exception {
		boolean valid = true;

		if (address.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("city", additionalAssertFieldName)) {
				if (address.getCity() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("country", additionalAssertFieldName)) {
				if (address.getCountry() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("countryISOCode", additionalAssertFieldName)) {
				if (address.getCountryISOCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (address.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (address.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("latitude", additionalAssertFieldName)) {
				if (address.getLatitude() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("longitude", additionalAssertFieldName)) {
				if (address.getLongitude() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (address.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("phoneNumber", additionalAssertFieldName)) {
				if (address.getPhoneNumber() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("region", additionalAssertFieldName)) {
				if (address.getRegion() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("regionISOCode", additionalAssertFieldName)) {
				if (address.getRegionISOCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("street1", additionalAssertFieldName)) {
				if (address.getStreet1() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("street2", additionalAssertFieldName)) {
				if (address.getStreet2() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("street3", additionalAssertFieldName)) {
				if (address.getStreet3() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("subtype", additionalAssertFieldName)) {
				if (address.getSubtype() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (address.getType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("typeId", additionalAssertFieldName)) {
				if (address.getTypeId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("vatNumber", additionalAssertFieldName)) {
				if (address.getVatNumber() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("zip", additionalAssertFieldName)) {
				if (address.getZip() == null) {
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

	protected void assertValid(Page<Address> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Address> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Address> addresses = page.getItems();

		int size = addresses.size();

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
					com.liferay.headless.commerce.delivery.cart.dto.v1_0.
						Address.class)) {

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

	protected boolean equals(Address address1, Address address2) {
		if (address1 == address2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("city", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						address1.getCity(), address2.getCity())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("country", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						address1.getCountry(), address2.getCountry())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("countryISOCode", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						address1.getCountryISOCode(),
						address2.getCountryISOCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						address1.getDescription(), address2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						address1.getExternalReferenceCode(),
						address2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(address1.getId(), address2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("latitude", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						address1.getLatitude(), address2.getLatitude())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("longitude", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						address1.getLongitude(), address2.getLongitude())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						address1.getName(), address2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("phoneNumber", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						address1.getPhoneNumber(), address2.getPhoneNumber())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("region", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						address1.getRegion(), address2.getRegion())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("regionISOCode", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						address1.getRegionISOCode(),
						address2.getRegionISOCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("street1", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						address1.getStreet1(), address2.getStreet1())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("street2", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						address1.getStreet2(), address2.getStreet2())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("street3", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						address1.getStreet3(), address2.getStreet3())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("subtype", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						address1.getSubtype(), address2.getSubtype())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						address1.getType(), address2.getType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("typeId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						address1.getTypeId(), address2.getTypeId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("vatNumber", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						address1.getVatNumber(), address2.getVatNumber())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("zip", additionalAssertFieldName)) {
				if (!Objects.deepEquals(address1.getZip(), address2.getZip())) {
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

		if (!(_addressResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_addressResource;

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
		EntityField entityField, String operator, Address address) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("city")) {
			Object object = address.getCity();

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
			Object object = address.getCountry();

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
			Object object = address.getCountryISOCode();

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
			Object object = address.getDescription();

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
			Object object = address.getExternalReferenceCode();

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
			sb.append(String.valueOf(address.getLatitude()));

			return sb.toString();
		}

		if (entityFieldName.equals("longitude")) {
			sb.append(String.valueOf(address.getLongitude()));

			return sb.toString();
		}

		if (entityFieldName.equals("name")) {
			Object object = address.getName();

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
			Object object = address.getPhoneNumber();

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
			Object object = address.getRegion();

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
			Object object = address.getRegionISOCode();

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
			Object object = address.getStreet1();

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
			Object object = address.getStreet2();

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
			Object object = address.getStreet3();

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
			Object object = address.getSubtype();

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
			Object object = address.getType();

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
			sb.append(String.valueOf(address.getTypeId()));

			return sb.toString();
		}

		if (entityFieldName.equals("vatNumber")) {
			Object object = address.getVatNumber();

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
			Object object = address.getZip();

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

	protected Address randomAddress() throws Exception {
		return new Address() {
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

	protected Address randomIrrelevantAddress() throws Exception {
		Address randomIrrelevantAddress = randomAddress();

		return randomIrrelevantAddress;
	}

	protected Address randomPatchAddress() throws Exception {
		return randomAddress();
	}

	protected AddressResource addressResource;
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
		LogFactoryUtil.getLog(BaseAddressResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.commerce.delivery.cart.resource.v1_0.
			AddressResource _addressResource;

}