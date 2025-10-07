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

import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.headless.commerce.delivery.cart.client.dto.v1_0.Cart;
import com.liferay.headless.commerce.delivery.cart.client.http.HttpInvoker;
import com.liferay.headless.commerce.delivery.cart.client.pagination.Page;
import com.liferay.headless.commerce.delivery.cart.client.pagination.Pagination;
import com.liferay.headless.commerce.delivery.cart.client.resource.v1_0.CartResource;
import com.liferay.headless.commerce.delivery.cart.client.serdes.v1_0.CartSerDes;
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
import com.liferay.portal.search.test.rule.SearchTestRule;
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
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public abstract class BaseCartResourceTestCase {

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

		_cartResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		cartResource = CartResource.builder(
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

		Cart cart1 = randomCart();

		String json = objectMapper.writeValueAsString(cart1);

		Cart cart2 = CartSerDes.toDTO(json);

		Assert.assertTrue(equals(cart1, cart2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Cart cart = randomCart();

		String json1 = objectMapper.writeValueAsString(cart);
		String json2 = CartSerDes.toJSON(cart);

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

		Cart cart = randomCart();

		cart.setAccount(regex);
		cart.setAuthor(regex);
		cart.setBillingAddressExternalReferenceCode(regex);
		cart.setCouponCode(regex);
		cart.setCurrencyCode(regex);
		cart.setCurrencyExternalReferenceCode(regex);
		cart.setDeliveryTermLabel(regex);
		cart.setExternalReferenceCode(regex);
		cart.setFriendlyURLSeparator(regex);
		cart.setName(regex);
		cart.setOrderType(regex);
		cart.setOrderTypeExternalReferenceCode(regex);
		cart.setOrderUUID(regex);
		cart.setPaymentMethod(regex);
		cart.setPaymentMethodLabel(regex);
		cart.setPaymentStatusLabel(regex);
		cart.setPaymentTermLabel(regex);
		cart.setPrintedNote(regex);
		cart.setPurchaseOrderNumber(regex);
		cart.setShippingAddressExternalReferenceCode(regex);
		cart.setShippingMethod(regex);
		cart.setShippingOption(regex);
		cart.setStatus(regex);

		String json = CartSerDes.toJSON(cart);

		Assert.assertFalse(json.contains(regex));

		cart = CartSerDes.toDTO(json);

		Assert.assertEquals(regex, cart.getAccount());
		Assert.assertEquals(regex, cart.getAuthor());
		Assert.assertEquals(
			regex, cart.getBillingAddressExternalReferenceCode());
		Assert.assertEquals(regex, cart.getCouponCode());
		Assert.assertEquals(regex, cart.getCurrencyCode());
		Assert.assertEquals(regex, cart.getCurrencyExternalReferenceCode());
		Assert.assertEquals(regex, cart.getDeliveryTermLabel());
		Assert.assertEquals(regex, cart.getExternalReferenceCode());
		Assert.assertEquals(regex, cart.getFriendlyURLSeparator());
		Assert.assertEquals(regex, cart.getName());
		Assert.assertEquals(regex, cart.getOrderType());
		Assert.assertEquals(regex, cart.getOrderTypeExternalReferenceCode());
		Assert.assertEquals(regex, cart.getOrderUUID());
		Assert.assertEquals(regex, cart.getPaymentMethod());
		Assert.assertEquals(regex, cart.getPaymentMethodLabel());
		Assert.assertEquals(regex, cart.getPaymentStatusLabel());
		Assert.assertEquals(regex, cart.getPaymentTermLabel());
		Assert.assertEquals(regex, cart.getPrintedNote());
		Assert.assertEquals(regex, cart.getPurchaseOrderNumber());
		Assert.assertEquals(
			regex, cart.getShippingAddressExternalReferenceCode());
		Assert.assertEquals(regex, cart.getShippingMethod());
		Assert.assertEquals(regex, cart.getShippingOption());
		Assert.assertEquals(regex, cart.getStatus());
	}

	@Test
	public void testDeleteCart() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Cart cart = testDeleteCart_addCart();

		assertHttpResponseStatusCode(
			204, cartResource.deleteCartHttpResponse(cart.getId()));

		assertHttpResponseStatusCode(
			404, cartResource.getCartHttpResponse(cart.getId()));
		assertHttpResponseStatusCode(404, cartResource.getCartHttpResponse(0L));
	}

	protected Cart testDeleteCart_addCart() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteCart() throws Exception {

		// No namespace

		Cart cart1 = testGraphQLDeleteCart_addCart();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteCart",
						new HashMap<String, Object>() {
							{
								put("cartId", cart1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteCart"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"cart",
					new HashMap<String, Object>() {
						{
							put("cartId", cart1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceDeliveryCart_v1_0

		Cart cart2 = testGraphQLDeleteCart_addCart();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceDeliveryCart_v1_0",
						new GraphQLField(
							"deleteCart",
							new HashMap<String, Object>() {
								{
									put("cartId", cart2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceDeliveryCart_v1_0",
				"Object/deleteCart"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceDeliveryCart_v1_0",
					new GraphQLField(
						"cart",
						new HashMap<String, Object>() {
							{
								put("cartId", cart2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Cart testGraphQLDeleteCart_addCart() throws Exception {
		return testGraphQLCart_addCart();
	}

	@Test
	public void testDeleteCartBatch() throws Exception {
		Cart cart1 = testDeleteCartBatch_addCart();

		testDeleteCartBatch_deleteCart(
			202, cart1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, cartResource.getCartHttpResponse(cart1.getId()));

		cart1 = testDeleteCartBatch_addCart();

		testDeleteCartBatch_deleteCart(202, null, cart1.getId());

		assertHttpResponseStatusCode(
			404, cartResource.getCartHttpResponse(cart1.getId()));

		cart1 = testDeleteCartBatch_addCart();
		Cart cart2 = testDeleteCartBatch_addCart();

		testDeleteCartBatch_deleteCart(
			202, cart2.getExternalReferenceCode(), cart1.getId());

		assertHttpResponseStatusCode(
			404, cartResource.getCartHttpResponse(cart1.getId()));
		assertHttpResponseStatusCode(
			200, cartResource.getCartHttpResponse(cart2.getId()));

		testDeleteCartBatch_deleteCart(
			202, cart2.getExternalReferenceCode(), cart1.getId());

		assertHttpResponseStatusCode(
			404, cartResource.getCartHttpResponse(cart2.getId()));
	}

	protected Cart testDeleteCartBatch_addCart() throws Exception {
		return testDeleteCart_addCart();
	}

	protected void testDeleteCartBatch_deleteCart(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			cartResource.deleteCartBatchHttpResponse(
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
	public void testDeleteCartByExternalReferenceCode() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Cart cart = testDeleteCartByExternalReferenceCode_addCart();

		assertHttpResponseStatusCode(
			204,
			cartResource.deleteCartByExternalReferenceCodeHttpResponse(
				cart.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			cartResource.getCartByExternalReferenceCodeHttpResponse(
				cart.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404, cartResource.getCartByExternalReferenceCodeHttpResponse("-"));
	}

	protected Cart testDeleteCartByExternalReferenceCode_addCart()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteCartByExternalReferenceCode()
		throws Exception {

		// No namespace

		Cart cart1 = testGraphQLDeleteCartByExternalReferenceCode_addCart();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteCartByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + cart1.getExternalReferenceCode() +
										"\"");
							}
						})),
				"JSONObject/data", "Object/deleteCartByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"cartByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + cart1.getExternalReferenceCode() + "\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceDeliveryCart_v1_0

		Cart cart2 = testGraphQLDeleteCartByExternalReferenceCode_addCart();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceDeliveryCart_v1_0",
						new GraphQLField(
							"deleteCartByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											cart2.getExternalReferenceCode() +
												"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceDeliveryCart_v1_0",
				"Object/deleteCartByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceDeliveryCart_v1_0",
					new GraphQLField(
						"cartByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + cart2.getExternalReferenceCode() +
										"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Cart testGraphQLDeleteCartByExternalReferenceCode_addCart()
		throws Exception {

		return testGraphQLCart_addCart();
	}

	@Test
	public void testGetCart() throws Exception {
		Cart postCart = testGetCart_addCart();

		Cart getCart = cartResource.getCart(postCart.getId());

		assertEquals(postCart, getCart);
		assertValid(getCart);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		Cart postCart = testGetCart_addCart();

		Cart getCart = cartResource.getCart(postCart.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.delivery.cart.dto.v1_0.Cart"
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

		Object item = vulcanCRUDItemDelegate.getItem(postCart.getId());

		assertEquals(getCart, CartSerDes.toDTO(item.toString()));
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

	protected Cart testGetCart_addCart() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetCart() throws Exception {
		Cart cart = testGraphQLGetCart_addCart();

		// No namespace

		Assert.assertTrue(
			equals(
				cart,
				CartSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"cart",
								new HashMap<String, Object>() {
									{
										put("cartId", cart.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/cart"))));

		// Using the namespace headlessCommerceDeliveryCart_v1_0

		Assert.assertTrue(
			equals(
				cart,
				CartSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceDeliveryCart_v1_0",
								new GraphQLField(
									"cart",
									new HashMap<String, Object>() {
										{
											put("cartId", cart.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceDeliveryCart_v1_0",
						"Object/cart"))));
	}

	@Test
	public void testGraphQLGetCartNotFound() throws Exception {
		Long irrelevantCartId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"cart",
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
							"cart",
							new HashMap<String, Object>() {
								{
									put("cartId", irrelevantCartId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Cart testGraphQLGetCart_addCart() throws Exception {
		return testGraphQLCart_addCart();
	}

	@Test
	public void testGetCartByExternalReferenceCode() throws Exception {
		Cart postCart = testGetCartByExternalReferenceCode_addCart();

		Cart getCart = cartResource.getCartByExternalReferenceCode(
			postCart.getExternalReferenceCode());

		assertEquals(postCart, getCart);
		assertValid(getCart);
	}

	protected Cart testGetCartByExternalReferenceCode_addCart()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetCartByExternalReferenceCode() throws Exception {
		Cart cart = testGraphQLGetCartByExternalReferenceCode_addCart();

		// No namespace

		Assert.assertTrue(
			equals(
				cart,
				CartSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"cartByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												cart.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/cartByExternalReferenceCode"))));

		// Using the namespace headlessCommerceDeliveryCart_v1_0

		Assert.assertTrue(
			equals(
				cart,
				CartSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceDeliveryCart_v1_0",
								new GraphQLField(
									"cartByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													cart.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceDeliveryCart_v1_0",
						"Object/cartByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetCartByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"cartByExternalReferenceCode",
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
							"cartByExternalReferenceCode",
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

	protected Cart testGraphQLGetCartByExternalReferenceCode_addCart()
		throws Exception {

		return testGraphQLCart_addCart();
	}

	@Test
	public void testGetCartByExternalReferenceCodePaymentUrl()
		throws Exception {

		Assert.assertTrue(false);
	}

	@Test
	public void testGetCartPaymentURL() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testGetChannelAccountCartsPage() throws Exception {
		Long accountId = testGetChannelAccountCartsPage_getAccountId();
		Long irrelevantAccountId =
			testGetChannelAccountCartsPage_getIrrelevantAccountId();
		Long channelId = testGetChannelAccountCartsPage_getChannelId();
		Long irrelevantChannelId =
			testGetChannelAccountCartsPage_getIrrelevantChannelId();

		Page<Cart> page = cartResource.getChannelAccountCartsPage(
			accountId, channelId, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if ((irrelevantAccountId != null) && (irrelevantChannelId != null)) {
			Cart irrelevantCart = testGetChannelAccountCartsPage_addCart(
				irrelevantAccountId, irrelevantChannelId,
				randomIrrelevantCart());

			page = cartResource.getChannelAccountCartsPage(
				irrelevantAccountId, irrelevantChannelId, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantCart, (List<Cart>)page.getItems());
			assertValid(
				page,
				testGetChannelAccountCartsPage_getExpectedActions(
					irrelevantAccountId, irrelevantChannelId));
		}

		Cart cart1 = testGetChannelAccountCartsPage_addCart(
			accountId, channelId, randomCart());

		Cart cart2 = testGetChannelAccountCartsPage_addCart(
			accountId, channelId, randomCart());

		page = cartResource.getChannelAccountCartsPage(
			accountId, channelId, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(cart1, (List<Cart>)page.getItems());
		assertContains(cart2, (List<Cart>)page.getItems());
		assertValid(
			page,
			testGetChannelAccountCartsPage_getExpectedActions(
				accountId, channelId));

		cartResource.deleteCart(cart1.getId());

		cartResource.deleteCart(cart2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetChannelAccountCartsPage_getExpectedActions(
				Long accountId, Long channelId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelAccountCartsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long accountId = testGetChannelAccountCartsPage_getAccountId();
		Long channelId = testGetChannelAccountCartsPage_getChannelId();

		Cart cart1 = randomCart();

		cart1 = testGetChannelAccountCartsPage_addCart(
			accountId, channelId, cart1);

		for (EntityField entityField : entityFields) {
			Page<Cart> page = cartResource.getChannelAccountCartsPage(
				accountId, channelId, null,
				getFilterString(entityField, "between", cart1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(cart1), (List<Cart>)page.getItems());
		}
	}

	@Test
	public void testGetChannelAccountCartsPageWithFilterDoubleEquals()
		throws Exception {

		testGetChannelAccountCartsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetChannelAccountCartsPageWithFilterStringContains()
		throws Exception {

		testGetChannelAccountCartsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelAccountCartsPageWithFilterStringEquals()
		throws Exception {

		testGetChannelAccountCartsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelAccountCartsPageWithFilterStringStartsWith()
		throws Exception {

		testGetChannelAccountCartsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetChannelAccountCartsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long accountId = testGetChannelAccountCartsPage_getAccountId();
		Long channelId = testGetChannelAccountCartsPage_getChannelId();

		Cart cart1 = testGetChannelAccountCartsPage_addCart(
			accountId, channelId, randomCart());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Cart cart2 = testGetChannelAccountCartsPage_addCart(
			accountId, channelId, randomCart());

		for (EntityField entityField : entityFields) {
			Page<Cart> page = cartResource.getChannelAccountCartsPage(
				accountId, channelId, null,
				getFilterString(entityField, operator, cart1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(cart1), (List<Cart>)page.getItems());
		}
	}

	@Test
	public void testGetChannelAccountCartsPageWithPagination()
		throws Exception {

		Long accountId = testGetChannelAccountCartsPage_getAccountId();
		Long channelId = testGetChannelAccountCartsPage_getChannelId();

		Page<Cart> cartsPage = cartResource.getChannelAccountCartsPage(
			accountId, channelId, null, null, null, null);

		int totalCount = GetterUtil.getInteger(cartsPage.getTotalCount());

		Cart cart1 = testGetChannelAccountCartsPage_addCart(
			accountId, channelId, randomCart());

		Cart cart2 = testGetChannelAccountCartsPage_addCart(
			accountId, channelId, randomCart());

		Cart cart3 = testGetChannelAccountCartsPage_addCart(
			accountId, channelId, randomCart());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Cart> page1 = cartResource.getChannelAccountCartsPage(
				accountId, channelId, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(cart1, (List<Cart>)page1.getItems());

			Page<Cart> page2 = cartResource.getChannelAccountCartsPage(
				accountId, channelId, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(cart2, (List<Cart>)page2.getItems());

			Page<Cart> page3 = cartResource.getChannelAccountCartsPage(
				accountId, channelId, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(cart3, (List<Cart>)page3.getItems());
		}
		else {
			Page<Cart> page1 = cartResource.getChannelAccountCartsPage(
				accountId, channelId, null, null,
				Pagination.of(1, totalCount + 2), null);

			List<Cart> carts1 = (List<Cart>)page1.getItems();

			Assert.assertEquals(
				carts1.toString(), totalCount + 2, carts1.size());

			Page<Cart> page2 = cartResource.getChannelAccountCartsPage(
				accountId, channelId, null, null,
				Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Cart> carts2 = (List<Cart>)page2.getItems();

			Assert.assertEquals(carts2.toString(), 1, carts2.size());

			Page<Cart> page3 = cartResource.getChannelAccountCartsPage(
				accountId, channelId, null, null,
				Pagination.of(1, (int)totalCount + 3), null);

			assertContains(cart1, (List<Cart>)page3.getItems());
			assertContains(cart2, (List<Cart>)page3.getItems());
			assertContains(cart3, (List<Cart>)page3.getItems());
		}
	}

	@Test
	public void testGetChannelAccountCartsPageWithSortDateTime()
		throws Exception {

		testGetChannelAccountCartsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, cart1, cart2) -> {
				BeanTestUtil.setProperty(
					cart1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetChannelAccountCartsPageWithSortDouble()
		throws Exception {

		testGetChannelAccountCartsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, cart1, cart2) -> {
				BeanTestUtil.setProperty(cart1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(cart2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetChannelAccountCartsPageWithSortInteger()
		throws Exception {

		testGetChannelAccountCartsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, cart1, cart2) -> {
				BeanTestUtil.setProperty(cart1, entityField.getName(), 0);
				BeanTestUtil.setProperty(cart2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetChannelAccountCartsPageWithSortString()
		throws Exception {

		testGetChannelAccountCartsPageWithSort(
			EntityField.Type.STRING,
			(entityField, cart1, cart2) -> {
				Class<?> clazz = cart1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						cart1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						cart2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						cart1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						cart2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						cart1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						cart2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetChannelAccountCartsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Cart, Cart, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long accountId = testGetChannelAccountCartsPage_getAccountId();
		Long channelId = testGetChannelAccountCartsPage_getChannelId();

		Cart cart1 = randomCart();
		Cart cart2 = randomCart();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, cart1, cart2);
		}

		cart1 = testGetChannelAccountCartsPage_addCart(
			accountId, channelId, cart1);

		cart2 = testGetChannelAccountCartsPage_addCart(
			accountId, channelId, cart2);

		Page<Cart> page = cartResource.getChannelAccountCartsPage(
			accountId, channelId, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Cart> ascPage = cartResource.getChannelAccountCartsPage(
				accountId, channelId, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(cart1, (List<Cart>)ascPage.getItems());
			assertContains(cart2, (List<Cart>)ascPage.getItems());

			Page<Cart> descPage = cartResource.getChannelAccountCartsPage(
				accountId, channelId, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(cart2, (List<Cart>)descPage.getItems());
			assertContains(cart1, (List<Cart>)descPage.getItems());
		}
	}

	protected Cart testGetChannelAccountCartsPage_addCart(
			Long accountId, Long channelId, Cart cart)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelAccountCartsPage_getAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelAccountCartsPage_getIrrelevantAccountId()
		throws Exception {

		return null;
	}

	protected Long testGetChannelAccountCartsPage_getChannelId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelAccountCartsPage_getIrrelevantChannelId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage()
		throws Exception {

		String accountExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_getAccountExternalReferenceCode();
		String irrelevantAccountExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_getIrrelevantAccountExternalReferenceCode();
		String channelExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_getChannelExternalReferenceCode();
		String irrelevantChannelExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_getIrrelevantChannelExternalReferenceCode();

		Page<Cart> page =
			cartResource.
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage(
					accountExternalReferenceCode, channelExternalReferenceCode,
					null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if ((irrelevantAccountExternalReferenceCode != null) &&
			(irrelevantChannelExternalReferenceCode != null)) {

			Cart irrelevantCart =
				testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_addCart(
					irrelevantAccountExternalReferenceCode,
					irrelevantChannelExternalReferenceCode,
					randomIrrelevantCart());

			page =
				cartResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage(
						irrelevantAccountExternalReferenceCode,
						irrelevantChannelExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantCart, (List<Cart>)page.getItems());
			assertValid(
				page,
				testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_getExpectedActions(
					irrelevantAccountExternalReferenceCode,
					irrelevantChannelExternalReferenceCode));
		}

		Cart cart1 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_addCart(
				accountExternalReferenceCode, channelExternalReferenceCode,
				randomCart());

		Cart cart2 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_addCart(
				accountExternalReferenceCode, channelExternalReferenceCode,
				randomCart());

		page =
			cartResource.
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage(
					accountExternalReferenceCode, channelExternalReferenceCode,
					null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(cart1, (List<Cart>)page.getItems());
		assertContains(cart2, (List<Cart>)page.getItems());
		assertValid(
			page,
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_getExpectedActions(
				accountExternalReferenceCode, channelExternalReferenceCode));

		cartResource.deleteCart(cart1.getId());

		cartResource.deleteCart(cart2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_getExpectedActions(
				String accountExternalReferenceCode,
				String channelExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String accountExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_getAccountExternalReferenceCode();
		String channelExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_getChannelExternalReferenceCode();

		Cart cart1 = randomCart();

		cart1 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_addCart(
				accountExternalReferenceCode, channelExternalReferenceCode,
				cart1);

		for (EntityField entityField : entityFields) {
			Page<Cart> page =
				cartResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage(
						accountExternalReferenceCode,
						channelExternalReferenceCode, null,
						getFilterString(entityField, "between", cart1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(cart1), (List<Cart>)page.getItems());
		}
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPageWithFilterDoubleEquals()
		throws Exception {

		testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPageWithFilterStringContains()
		throws Exception {

		testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPageWithFilterStringEquals()
		throws Exception {

		testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPageWithFilterStringStartsWith()
		throws Exception {

		testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String accountExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_getAccountExternalReferenceCode();
		String channelExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_getChannelExternalReferenceCode();

		Cart cart1 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_addCart(
				accountExternalReferenceCode, channelExternalReferenceCode,
				randomCart());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Cart cart2 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_addCart(
				accountExternalReferenceCode, channelExternalReferenceCode,
				randomCart());

		for (EntityField entityField : entityFields) {
			Page<Cart> page =
				cartResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage(
						accountExternalReferenceCode,
						channelExternalReferenceCode, null,
						getFilterString(entityField, operator, cart1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(cart1), (List<Cart>)page.getItems());
		}
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPageWithPagination()
		throws Exception {

		String accountExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_getAccountExternalReferenceCode();
		String channelExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_getChannelExternalReferenceCode();

		Page<Cart> cartsPage =
			cartResource.
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage(
					accountExternalReferenceCode, channelExternalReferenceCode,
					null, null, null, null);

		int totalCount = GetterUtil.getInteger(cartsPage.getTotalCount());

		Cart cart1 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_addCart(
				accountExternalReferenceCode, channelExternalReferenceCode,
				randomCart());

		Cart cart2 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_addCart(
				accountExternalReferenceCode, channelExternalReferenceCode,
				randomCart());

		Cart cart3 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_addCart(
				accountExternalReferenceCode, channelExternalReferenceCode,
				randomCart());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Cart> page1 =
				cartResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage(
						accountExternalReferenceCode,
						channelExternalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(cart1, (List<Cart>)page1.getItems());

			Page<Cart> page2 =
				cartResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage(
						accountExternalReferenceCode,
						channelExternalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(cart2, (List<Cart>)page2.getItems());

			Page<Cart> page3 =
				cartResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage(
						accountExternalReferenceCode,
						channelExternalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(cart3, (List<Cart>)page3.getItems());
		}
		else {
			Page<Cart> page1 =
				cartResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage(
						accountExternalReferenceCode,
						channelExternalReferenceCode, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<Cart> carts1 = (List<Cart>)page1.getItems();

			Assert.assertEquals(
				carts1.toString(), totalCount + 2, carts1.size());

			Page<Cart> page2 =
				cartResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage(
						accountExternalReferenceCode,
						channelExternalReferenceCode, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Cart> carts2 = (List<Cart>)page2.getItems();

			Assert.assertEquals(carts2.toString(), 1, carts2.size());

			Page<Cart> page3 =
				cartResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage(
						accountExternalReferenceCode,
						channelExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(cart1, (List<Cart>)page3.getItems());
			assertContains(cart2, (List<Cart>)page3.getItems());
			assertContains(cart3, (List<Cart>)page3.getItems());
		}
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPageWithSortDateTime()
		throws Exception {

		testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, cart1, cart2) -> {
				BeanTestUtil.setProperty(
					cart1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPageWithSortDouble()
		throws Exception {

		testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, cart1, cart2) -> {
				BeanTestUtil.setProperty(cart1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(cart2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPageWithSortInteger()
		throws Exception {

		testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, cart1, cart2) -> {
				BeanTestUtil.setProperty(cart1, entityField.getName(), 0);
				BeanTestUtil.setProperty(cart2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPageWithSortString()
		throws Exception {

		testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPageWithSort(
			EntityField.Type.STRING,
			(entityField, cart1, cart2) -> {
				Class<?> clazz = cart1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						cart1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						cart2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						cart1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						cart2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						cart1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						cart2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer<EntityField, Cart, Cart, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String accountExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_getAccountExternalReferenceCode();
		String channelExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_getChannelExternalReferenceCode();

		Cart cart1 = randomCart();
		Cart cart2 = randomCart();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, cart1, cart2);
		}

		cart1 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_addCart(
				accountExternalReferenceCode, channelExternalReferenceCode,
				cart1);

		cart2 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_addCart(
				accountExternalReferenceCode, channelExternalReferenceCode,
				cart2);

		Page<Cart> page =
			cartResource.
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage(
					accountExternalReferenceCode, channelExternalReferenceCode,
					null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Cart> ascPage =
				cartResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage(
						accountExternalReferenceCode,
						channelExternalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(cart1, (List<Cart>)ascPage.getItems());
			assertContains(cart2, (List<Cart>)ascPage.getItems());

			Page<Cart> descPage =
				cartResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage(
						accountExternalReferenceCode,
						channelExternalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(cart2, (List<Cart>)descPage.getItems());
			assertContains(cart1, (List<Cart>)descPage.getItems());
		}
	}

	protected Cart
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_addCart(
				String accountExternalReferenceCode,
				String channelExternalReferenceCode, Cart cart)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_getAccountExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_getIrrelevantAccountExternalReferenceCode()
		throws Exception {

		return null;
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_getChannelExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodeCartsPage_getIrrelevantChannelExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetChannelCartsPage() throws Exception {
		Long channelId = testGetChannelCartsPage_getChannelId();
		Long irrelevantChannelId =
			testGetChannelCartsPage_getIrrelevantChannelId();

		Page<Cart> page = cartResource.getChannelCartsPage(
			channelId, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantChannelId != null) {
			Cart irrelevantCart = testGetChannelCartsPage_addCart(
				irrelevantChannelId, randomIrrelevantCart());

			page = cartResource.getChannelCartsPage(
				irrelevantChannelId, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantCart, (List<Cart>)page.getItems());
			assertValid(
				page,
				testGetChannelCartsPage_getExpectedActions(
					irrelevantChannelId));
		}

		Cart cart1 = testGetChannelCartsPage_addCart(channelId, randomCart());

		Cart cart2 = testGetChannelCartsPage_addCart(channelId, randomCart());

		page = cartResource.getChannelCartsPage(
			channelId, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(cart1, (List<Cart>)page.getItems());
		assertContains(cart2, (List<Cart>)page.getItems());
		assertValid(
			page, testGetChannelCartsPage_getExpectedActions(channelId));

		cartResource.deleteCart(cart1.getId());

		cartResource.deleteCart(cart2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetChannelCartsPage_getExpectedActions(Long channelId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelCartsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long channelId = testGetChannelCartsPage_getChannelId();

		Cart cart1 = randomCart();

		cart1 = testGetChannelCartsPage_addCart(channelId, cart1);

		for (EntityField entityField : entityFields) {
			Page<Cart> page = cartResource.getChannelCartsPage(
				channelId, null, getFilterString(entityField, "between", cart1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(cart1), (List<Cart>)page.getItems());
		}
	}

	@Test
	public void testGetChannelCartsPageWithFilterDoubleEquals()
		throws Exception {

		testGetChannelCartsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetChannelCartsPageWithFilterStringContains()
		throws Exception {

		testGetChannelCartsPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelCartsPageWithFilterStringEquals()
		throws Exception {

		testGetChannelCartsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelCartsPageWithFilterStringStartsWith()
		throws Exception {

		testGetChannelCartsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetChannelCartsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long channelId = testGetChannelCartsPage_getChannelId();

		Cart cart1 = testGetChannelCartsPage_addCart(channelId, randomCart());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Cart cart2 = testGetChannelCartsPage_addCart(channelId, randomCart());

		for (EntityField entityField : entityFields) {
			Page<Cart> page = cartResource.getChannelCartsPage(
				channelId, null, getFilterString(entityField, operator, cart1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(cart1), (List<Cart>)page.getItems());
		}
	}

	@Test
	public void testGetChannelCartsPageWithPagination() throws Exception {
		Long channelId = testGetChannelCartsPage_getChannelId();

		Page<Cart> cartsPage = cartResource.getChannelCartsPage(
			channelId, null, null, null, null);

		int totalCount = GetterUtil.getInteger(cartsPage.getTotalCount());

		Cart cart1 = testGetChannelCartsPage_addCart(channelId, randomCart());

		Cart cart2 = testGetChannelCartsPage_addCart(channelId, randomCart());

		Cart cart3 = testGetChannelCartsPage_addCart(channelId, randomCart());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Cart> page1 = cartResource.getChannelCartsPage(
				channelId, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(cart1, (List<Cart>)page1.getItems());

			Page<Cart> page2 = cartResource.getChannelCartsPage(
				channelId, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(cart2, (List<Cart>)page2.getItems());

			Page<Cart> page3 = cartResource.getChannelCartsPage(
				channelId, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(cart3, (List<Cart>)page3.getItems());
		}
		else {
			Page<Cart> page1 = cartResource.getChannelCartsPage(
				channelId, null, null, Pagination.of(1, totalCount + 2), null);

			List<Cart> carts1 = (List<Cart>)page1.getItems();

			Assert.assertEquals(
				carts1.toString(), totalCount + 2, carts1.size());

			Page<Cart> page2 = cartResource.getChannelCartsPage(
				channelId, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Cart> carts2 = (List<Cart>)page2.getItems();

			Assert.assertEquals(carts2.toString(), 1, carts2.size());

			Page<Cart> page3 = cartResource.getChannelCartsPage(
				channelId, null, null, Pagination.of(1, (int)totalCount + 3),
				null);

			assertContains(cart1, (List<Cart>)page3.getItems());
			assertContains(cart2, (List<Cart>)page3.getItems());
			assertContains(cart3, (List<Cart>)page3.getItems());
		}
	}

	@Test
	public void testGetChannelCartsPageWithSortDateTime() throws Exception {
		testGetChannelCartsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, cart1, cart2) -> {
				BeanTestUtil.setProperty(
					cart1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetChannelCartsPageWithSortDouble() throws Exception {
		testGetChannelCartsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, cart1, cart2) -> {
				BeanTestUtil.setProperty(cart1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(cart2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetChannelCartsPageWithSortInteger() throws Exception {
		testGetChannelCartsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, cart1, cart2) -> {
				BeanTestUtil.setProperty(cart1, entityField.getName(), 0);
				BeanTestUtil.setProperty(cart2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetChannelCartsPageWithSortString() throws Exception {
		testGetChannelCartsPageWithSort(
			EntityField.Type.STRING,
			(entityField, cart1, cart2) -> {
				Class<?> clazz = cart1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						cart1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						cart2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						cart1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						cart2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						cart1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						cart2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetChannelCartsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Cart, Cart, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long channelId = testGetChannelCartsPage_getChannelId();

		Cart cart1 = randomCart();
		Cart cart2 = randomCart();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, cart1, cart2);
		}

		cart1 = testGetChannelCartsPage_addCart(channelId, cart1);

		cart2 = testGetChannelCartsPage_addCart(channelId, cart2);

		Page<Cart> page = cartResource.getChannelCartsPage(
			channelId, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Cart> ascPage = cartResource.getChannelCartsPage(
				channelId, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(cart1, (List<Cart>)ascPage.getItems());
			assertContains(cart2, (List<Cart>)ascPage.getItems());

			Page<Cart> descPage = cartResource.getChannelCartsPage(
				channelId, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(cart2, (List<Cart>)descPage.getItems());
			assertContains(cart1, (List<Cart>)descPage.getItems());
		}
	}

	protected Cart testGetChannelCartsPage_addCart(Long channelId, Cart cart)
		throws Exception {

		return cartResource.postChannelCart(channelId, cart);
	}

	protected Long testGetChannelCartsPage_getChannelId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelCartsPage_getIrrelevantChannelId()
		throws Exception {

		return null;
	}

	@Test
	public void testPatchCart() throws Exception {
		Cart postCart = testPatchCart_addCart();

		Cart randomPatchCart = randomPatchCart();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Cart patchCart = cartResource.patchCart(
			postCart.getId(), randomPatchCart);

		Cart expectedPatchCart = postCart.clone();

		BeanTestUtil.copyProperties(randomPatchCart, expectedPatchCart);

		Cart getCart = cartResource.getCart(patchCart.getId());

		assertEquals(expectedPatchCart, getCart);
		assertValid(getCart);
	}

	protected Cart testPatchCart_addCart() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchCartByExternalReferenceCode() throws Exception {
		Cart postCart = testPatchCartByExternalReferenceCode_addCart();

		Cart randomPatchCart = randomPatchCart();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Cart patchCart = cartResource.patchCartByExternalReferenceCode(
			postCart.getExternalReferenceCode(), randomPatchCart);

		Cart expectedPatchCart = postCart.clone();

		BeanTestUtil.copyProperties(randomPatchCart, expectedPatchCart);

		Cart getCart = cartResource.getCartByExternalReferenceCode(
			patchCart.getExternalReferenceCode());

		assertEquals(expectedPatchCart, getCart);
		assertValid(getCart);
	}

	protected Cart testPatchCartByExternalReferenceCode_addCart()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostCartByExternalReferenceCodeCheckout() throws Exception {
		Cart randomCart = randomCart();

		Cart postCart = testPostCartByExternalReferenceCodeCheckout_addCart(
			randomCart);

		assertEquals(randomCart, postCart);
		assertValid(postCart);
	}

	protected Cart testPostCartByExternalReferenceCodeCheckout_addCart(
			Cart cart)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostCartByExternalReferenceCodeCouponCode()
		throws Exception {

		Cart randomCart = randomCart();

		Cart postCart = testPostCartByExternalReferenceCodeCouponCode_addCart(
			randomCart);

		assertEquals(randomCart, postCart);
		assertValid(postCart);
	}

	protected Cart testPostCartByExternalReferenceCodeCouponCode_addCart(
			Cart cart)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostCartCheckout() throws Exception {
		Cart randomCart = randomCart();

		Cart postCart = testPostCartCheckout_addCart(randomCart);

		assertEquals(randomCart, postCart);
		assertValid(postCart);
	}

	protected Cart testPostCartCheckout_addCart(Cart cart) throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostCartCouponCode() throws Exception {
		Cart randomCart = randomCart();

		Cart postCart = testPostCartCouponCode_addCart(randomCart);

		assertEquals(randomCart, postCart);
		assertValid(postCart);
	}

	protected Cart testPostCartCouponCode_addCart(Cart cart) throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostChannelCart() throws Exception {
		Cart randomCart = randomCart();

		Cart postCart = testPostChannelCart_addCart(randomCart);

		assertEquals(randomCart, postCart);
		assertValid(postCart);
	}

	protected Cart testPostChannelCart_addCart(Cart cart) throws Exception {
		return cartResource.postChannelCart(
			testGetChannelCartsPage_getChannelId(), cart);
	}

	@Test
	public void testPostChannelCartByExternalReferenceCode() throws Exception {
		Cart randomCart = randomCart();

		Cart postCart = testPostChannelCartByExternalReferenceCode_addCart(
			randomCart);

		assertEquals(randomCart, postCart);
		assertValid(postCart);
	}

	protected Cart testPostChannelCartByExternalReferenceCode_addCart(Cart cart)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutCart() throws Exception {
		Cart postCart = testPutCart_addCart();

		Cart randomCart = randomCart();

		Cart putCart = cartResource.putCart(postCart.getId(), randomCart);

		assertEquals(randomCart, putCart);
		assertValid(putCart);

		Cart getCart = cartResource.getCart(putCart.getId());

		assertEquals(randomCart, getCart);
		assertValid(getCart);
	}

	protected Cart testPutCart_addCart() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutCartByExternalReferenceCode() throws Exception {
		Cart postCart = testPutCartByExternalReferenceCode_addCart();

		Cart randomCart = randomCart();

		Cart putCart = cartResource.putCartByExternalReferenceCode(
			postCart.getExternalReferenceCode(), randomCart);

		assertEquals(randomCart, putCart);
		assertValid(putCart);

		Cart getCart = cartResource.getCartByExternalReferenceCode(
			putCart.getExternalReferenceCode());

		assertEquals(randomCart, getCart);
		assertValid(getCart);

		Cart newCart = testPutCartByExternalReferenceCode_createCart();

		putCart = cartResource.putCartByExternalReferenceCode(
			newCart.getExternalReferenceCode(), newCart);

		assertEquals(newCart, putCart);
		assertValid(putCart);

		getCart = cartResource.getCartByExternalReferenceCode(
			putCart.getExternalReferenceCode());

		assertEquals(newCart, getCart);

		Assert.assertEquals(
			newCart.getExternalReferenceCode(),
			putCart.getExternalReferenceCode());
	}

	protected Cart testPutCartByExternalReferenceCode_addCart()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Cart testPutCartByExternalReferenceCode_createCart()
		throws Exception {

		return randomCart();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Cart cart1 = testBatchEngineDeleteImportTask_addCart();

		testBatchEngineDeleteImportTask_deleteCart(
			200, cart1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, cartResource.getCartHttpResponse(cart1.getId()));

		cart1 = testBatchEngineDeleteImportTask_addCart();

		testBatchEngineDeleteImportTask_deleteCart(200, null, cart1.getId());

		assertHttpResponseStatusCode(
			404, cartResource.getCartHttpResponse(cart1.getId()));

		cart1 = testBatchEngineDeleteImportTask_addCart();
		Cart cart2 = testBatchEngineDeleteImportTask_addCart();

		testBatchEngineDeleteImportTask_deleteCart(
			200, cart2.getExternalReferenceCode(), cart1.getId());

		assertHttpResponseStatusCode(
			404, cartResource.getCartHttpResponse(cart1.getId()));
		assertHttpResponseStatusCode(
			200, cartResource.getCartHttpResponse(cart2.getId()));

		testBatchEngineDeleteImportTask_deleteCart(
			200, cart2.getExternalReferenceCode(), cart1.getId());

		assertHttpResponseStatusCode(
			404, cartResource.getCartHttpResponse(cart2.getId()));
	}

	protected Cart testBatchEngineDeleteImportTask_addCart() throws Exception {
		return testDeleteCart_addCart();
	}

	protected void testBatchEngineDeleteImportTask_deleteCart(
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
				"com.liferay.headless.commerce.delivery.cart.dto.v1_0.Cart",
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

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected Cart testGraphQLCart_addCart() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(Cart cart, List<Cart> carts) {
		boolean contains = false;

		for (Cart item : carts) {
			if (equals(cart, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(carts + " does not contain " + cart, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Cart cart1, Cart cart2) {
		Assert.assertTrue(
			cart1 + " does not equal " + cart2, equals(cart1, cart2));
	}

	protected void assertEquals(List<Cart> carts1, List<Cart> carts2) {
		Assert.assertEquals(carts1.size(), carts2.size());

		for (int i = 0; i < carts1.size(); i++) {
			Cart cart1 = carts1.get(i);
			Cart cart2 = carts2.get(i);

			assertEquals(cart1, cart2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Cart> carts1, List<Cart> carts2) {

		Assert.assertEquals(carts1.size(), carts2.size());

		for (Cart cart1 : carts1) {
			boolean contains = false;

			for (Cart cart2 : carts2) {
				if (equals(cart1, cart2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(carts2 + " does not contain " + cart1, contains);
		}
	}

	protected void assertValid(Cart cart) throws Exception {
		boolean valid = true;

		if (cart.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("account", additionalAssertFieldName)) {
				if (cart.getAccount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (cart.getAccountId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("attachments", additionalAssertFieldName)) {
				if (cart.getAttachments() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("author", additionalAssertFieldName)) {
				if (cart.getAuthor() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("billingAddress", additionalAssertFieldName)) {
				if (cart.getBillingAddress() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"billingAddressExternalReferenceCode",
					additionalAssertFieldName)) {

				if (cart.getBillingAddressExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("billingAddressId", additionalAssertFieldName)) {
				if (cart.getBillingAddressId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("cartItems", additionalAssertFieldName)) {
				if (cart.getCartItems() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("channelId", additionalAssertFieldName)) {
				if (cart.getChannelId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("couponCode", additionalAssertFieldName)) {
				if (cart.getCouponCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (cart.getCreateDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("currencyCode", additionalAssertFieldName)) {
				if (cart.getCurrencyCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"currencyExternalReferenceCode",
					additionalAssertFieldName)) {

				if (cart.getCurrencyExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("currencyId", additionalAssertFieldName)) {
				if (cart.getCurrencyId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (cart.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("deliveryTermId", additionalAssertFieldName)) {
				if (cart.getDeliveryTermId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"deliveryTermLabel", additionalAssertFieldName)) {

				if (cart.getDeliveryTermLabel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("errorMessages", additionalAssertFieldName)) {
				if (cart.getErrorMessages() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (cart.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"friendlyURLSeparator", additionalAssertFieldName)) {

				if (cart.getFriendlyURLSeparator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"lastPriceUpdateDate", additionalAssertFieldName)) {

				if (cart.getLastPriceUpdateDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("modifiedDate", additionalAssertFieldName)) {
				if (cart.getModifiedDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (cart.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("notes", additionalAssertFieldName)) {
				if (cart.getNotes() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderStatusInfo", additionalAssertFieldName)) {
				if (cart.getOrderStatusInfo() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderType", additionalAssertFieldName)) {
				if (cart.getOrderType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"orderTypeExternalReferenceCode",
					additionalAssertFieldName)) {

				if (cart.getOrderTypeExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderTypeId", additionalAssertFieldName)) {
				if (cart.getOrderTypeId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderUUID", additionalAssertFieldName)) {
				if (cart.getOrderUUID() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("paymentMethod", additionalAssertFieldName)) {
				if (cart.getPaymentMethod() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentMethodLabel", additionalAssertFieldName)) {

				if (cart.getPaymentMethodLabel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentMethodType", additionalAssertFieldName)) {

				if (cart.getPaymentMethodType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("paymentStatus", additionalAssertFieldName)) {
				if (cart.getPaymentStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentStatusInfo", additionalAssertFieldName)) {

				if (cart.getPaymentStatusInfo() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentStatusLabel", additionalAssertFieldName)) {

				if (cart.getPaymentStatusLabel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("paymentTermId", additionalAssertFieldName)) {
				if (cart.getPaymentTermId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("paymentTermLabel", additionalAssertFieldName)) {
				if (cart.getPaymentTermLabel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("printedNote", additionalAssertFieldName)) {
				if (cart.getPrintedNote() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"purchaseOrderNumber", additionalAssertFieldName)) {

				if (cart.getPurchaseOrderNumber() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"requestedDeliveryDate", additionalAssertFieldName)) {

				if (cart.getRequestedDeliveryDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("shippingAddress", additionalAssertFieldName)) {
				if (cart.getShippingAddress() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingAddressExternalReferenceCode",
					additionalAssertFieldName)) {

				if (cart.getShippingAddressExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingAddressId", additionalAssertFieldName)) {

				if (cart.getShippingAddressId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("shippingMethod", additionalAssertFieldName)) {
				if (cart.getShippingMethod() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("shippingOption", additionalAssertFieldName)) {
				if (cart.getShippingOption() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (cart.getStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("steps", additionalAssertFieldName)) {
				if (cart.getSteps() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("summary", additionalAssertFieldName)) {
				if (cart.getSummary() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("useAsBilling", additionalAssertFieldName)) {
				if (cart.getUseAsBilling() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("valid", additionalAssertFieldName)) {
				if (cart.getValid() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowStatusInfo", additionalAssertFieldName)) {

				if (cart.getWorkflowStatusInfo() == null) {
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

	protected void assertValid(Page<Cart> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Cart> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Cart> carts = page.getItems();

		int size = carts.size();

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
					com.liferay.headless.commerce.delivery.cart.dto.v1_0.Cart.
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

	protected boolean equals(Cart cart1, Cart cart2) {
		if (cart1 == cart2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("account", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getAccount(), cart2.getAccount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getAccountId(), cart2.getAccountId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("attachments", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getAttachments(), cart2.getAttachments())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("author", additionalAssertFieldName)) {
				if (!Objects.deepEquals(cart1.getAuthor(), cart2.getAuthor())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("billingAddress", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getBillingAddress(), cart2.getBillingAddress())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"billingAddressExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						cart1.getBillingAddressExternalReferenceCode(),
						cart2.getBillingAddressExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("billingAddressId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getBillingAddressId(),
						cart2.getBillingAddressId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("cartItems", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getCartItems(), cart2.getCartItems())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("channelId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getChannelId(), cart2.getChannelId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("couponCode", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getCouponCode(), cart2.getCouponCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getCreateDate(), cart2.getCreateDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("currencyCode", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getCurrencyCode(), cart2.getCurrencyCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"currencyExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						cart1.getCurrencyExternalReferenceCode(),
						cart2.getCurrencyExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("currencyId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getCurrencyId(), cart2.getCurrencyId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!equals(
						(Map)cart1.getCustomFields(),
						(Map)cart2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("deliveryTermId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getDeliveryTermId(), cart2.getDeliveryTermId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"deliveryTermLabel", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						cart1.getDeliveryTermLabel(),
						cart2.getDeliveryTermLabel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("errorMessages", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getErrorMessages(), cart2.getErrorMessages())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						cart1.getExternalReferenceCode(),
						cart2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"friendlyURLSeparator", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						cart1.getFriendlyURLSeparator(),
						cart2.getFriendlyURLSeparator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(cart1.getId(), cart2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals(
					"lastPriceUpdateDate", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						cart1.getLastPriceUpdateDate(),
						cart2.getLastPriceUpdateDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("modifiedDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getModifiedDate(), cart2.getModifiedDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(cart1.getName(), cart2.getName())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("notes", additionalAssertFieldName)) {
				if (!Objects.deepEquals(cart1.getNotes(), cart2.getNotes())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("orderStatusInfo", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getOrderStatusInfo(),
						cart2.getOrderStatusInfo())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getOrderType(), cart2.getOrderType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"orderTypeExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						cart1.getOrderTypeExternalReferenceCode(),
						cart2.getOrderTypeExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderTypeId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getOrderTypeId(), cart2.getOrderTypeId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderUUID", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getOrderUUID(), cart2.getOrderUUID())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("paymentMethod", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getPaymentMethod(), cart2.getPaymentMethod())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentMethodLabel", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						cart1.getPaymentMethodLabel(),
						cart2.getPaymentMethodLabel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentMethodType", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						cart1.getPaymentMethodType(),
						cart2.getPaymentMethodType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("paymentStatus", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getPaymentStatus(), cart2.getPaymentStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentStatusInfo", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						cart1.getPaymentStatusInfo(),
						cart2.getPaymentStatusInfo())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentStatusLabel", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						cart1.getPaymentStatusLabel(),
						cart2.getPaymentStatusLabel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("paymentTermId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getPaymentTermId(), cart2.getPaymentTermId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("paymentTermLabel", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getPaymentTermLabel(),
						cart2.getPaymentTermLabel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("printedNote", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getPrintedNote(), cart2.getPrintedNote())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"purchaseOrderNumber", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						cart1.getPurchaseOrderNumber(),
						cart2.getPurchaseOrderNumber())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"requestedDeliveryDate", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						cart1.getRequestedDeliveryDate(),
						cart2.getRequestedDeliveryDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("shippingAddress", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getShippingAddress(),
						cart2.getShippingAddress())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingAddressExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						cart1.getShippingAddressExternalReferenceCode(),
						cart2.getShippingAddressExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingAddressId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						cart1.getShippingAddressId(),
						cart2.getShippingAddressId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("shippingMethod", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getShippingMethod(), cart2.getShippingMethod())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("shippingOption", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getShippingOption(), cart2.getShippingOption())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!Objects.deepEquals(cart1.getStatus(), cart2.getStatus())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("steps", additionalAssertFieldName)) {
				if (!Objects.deepEquals(cart1.getSteps(), cart2.getSteps())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("summary", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getSummary(), cart2.getSummary())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("useAsBilling", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cart1.getUseAsBilling(), cart2.getUseAsBilling())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("valid", additionalAssertFieldName)) {
				if (!Objects.deepEquals(cart1.getValid(), cart2.getValid())) {
					return false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowStatusInfo", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						cart1.getWorkflowStatusInfo(),
						cart2.getWorkflowStatusInfo())) {

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

		if (!(_cartResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_cartResource;

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
		EntityField entityField, String operator, Cart cart) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("account")) {
			Object object = cart.getAccount();

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

		if (entityFieldName.equals("accountId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("attachments")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("author")) {
			Object object = cart.getAuthor();

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

		if (entityFieldName.equals("billingAddress")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("billingAddressExternalReferenceCode")) {
			Object object = cart.getBillingAddressExternalReferenceCode();

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

		if (entityFieldName.equals("billingAddressId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("cartItems")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("channelId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("couponCode")) {
			Object object = cart.getCouponCode();

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
				Date date = cart.getCreateDate();

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

				sb.append(_format.format(cart.getCreateDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("currencyCode")) {
			Object object = cart.getCurrencyCode();

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

		if (entityFieldName.equals("currencyExternalReferenceCode")) {
			Object object = cart.getCurrencyExternalReferenceCode();

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

		if (entityFieldName.equals("currencyId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("customFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("deliveryTermId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("deliveryTermLabel")) {
			Object object = cart.getDeliveryTermLabel();

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

		if (entityFieldName.equals("errorMessages")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = cart.getExternalReferenceCode();

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

		if (entityFieldName.equals("friendlyURLSeparator")) {
			Object object = cart.getFriendlyURLSeparator();

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

		if (entityFieldName.equals("lastPriceUpdateDate")) {
			if (operator.equals("between")) {
				Date date = cart.getLastPriceUpdateDate();

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

				sb.append(_format.format(cart.getLastPriceUpdateDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("modifiedDate")) {
			if (operator.equals("between")) {
				Date date = cart.getModifiedDate();

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

				sb.append(_format.format(cart.getModifiedDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("name")) {
			Object object = cart.getName();

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

		if (entityFieldName.equals("notes")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("orderStatusInfo")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("orderType")) {
			Object object = cart.getOrderType();

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

		if (entityFieldName.equals("orderTypeExternalReferenceCode")) {
			Object object = cart.getOrderTypeExternalReferenceCode();

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

		if (entityFieldName.equals("orderUUID")) {
			Object object = cart.getOrderUUID();

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

		if (entityFieldName.equals("paymentMethod")) {
			Object object = cart.getPaymentMethod();

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

		if (entityFieldName.equals("paymentMethodLabel")) {
			Object object = cart.getPaymentMethodLabel();

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

		if (entityFieldName.equals("paymentMethodType")) {
			sb.append(String.valueOf(cart.getPaymentMethodType()));

			return sb.toString();
		}

		if (entityFieldName.equals("paymentStatus")) {
			sb.append(String.valueOf(cart.getPaymentStatus()));

			return sb.toString();
		}

		if (entityFieldName.equals("paymentStatusInfo")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("paymentStatusLabel")) {
			Object object = cart.getPaymentStatusLabel();

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

		if (entityFieldName.equals("paymentTermId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("paymentTermLabel")) {
			Object object = cart.getPaymentTermLabel();

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

		if (entityFieldName.equals("printedNote")) {
			Object object = cart.getPrintedNote();

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

		if (entityFieldName.equals("purchaseOrderNumber")) {
			Object object = cart.getPurchaseOrderNumber();

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

		if (entityFieldName.equals("requestedDeliveryDate")) {
			if (operator.equals("between")) {
				Date date = cart.getRequestedDeliveryDate();

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

				sb.append(_format.format(cart.getRequestedDeliveryDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("shippingAddress")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippingAddressExternalReferenceCode")) {
			Object object = cart.getShippingAddressExternalReferenceCode();

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

		if (entityFieldName.equals("shippingAddressId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippingMethod")) {
			Object object = cart.getShippingMethod();

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

		if (entityFieldName.equals("shippingOption")) {
			Object object = cart.getShippingOption();

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
			Object object = cart.getStatus();

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

		if (entityFieldName.equals("steps")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("summary")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("useAsBilling")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("valid")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("workflowStatusInfo")) {
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

	protected Cart randomCart() throws Exception {
		return new Cart() {
			{
				account = StringUtil.toLowerCase(RandomTestUtil.randomString());
				accountId = RandomTestUtil.randomLong();
				author = StringUtil.toLowerCase(RandomTestUtil.randomString());
				billingAddressExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				billingAddressId = RandomTestUtil.randomLong();
				channelId = RandomTestUtil.randomLong();
				couponCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				createDate = RandomTestUtil.nextDate();
				currencyCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				currencyExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				currencyId = RandomTestUtil.randomLong();
				deliveryTermId = RandomTestUtil.randomLong();
				deliveryTermLabel = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				friendlyURLSeparator = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				lastPriceUpdateDate = RandomTestUtil.nextDate();
				modifiedDate = RandomTestUtil.nextDate();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				orderType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				orderTypeExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				orderTypeId = RandomTestUtil.randomLong();
				orderUUID = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				paymentMethod = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				paymentMethodLabel = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				paymentMethodType = RandomTestUtil.randomInt();
				paymentStatus = RandomTestUtil.randomInt();
				paymentStatusLabel = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				paymentTermId = RandomTestUtil.randomLong();
				paymentTermLabel = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				printedNote = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				purchaseOrderNumber = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				requestedDeliveryDate = RandomTestUtil.nextDate();
				shippingAddressExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				shippingAddressId = RandomTestUtil.randomLong();
				shippingMethod = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				shippingOption = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				status = StringUtil.toLowerCase(RandomTestUtil.randomString());
				useAsBilling = RandomTestUtil.randomBoolean();
				valid = RandomTestUtil.randomBoolean();
			}
		};
	}

	protected Cart randomIrrelevantCart() throws Exception {
		Cart randomIrrelevantCart = randomCart();

		return randomIrrelevantCart;
	}

	protected Cart randomPatchCart() throws Exception {
		return randomCart();
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

	protected CartResource cartResource;
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
		LogFactoryUtil.getLog(BaseCartResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.commerce.delivery.cart.resource.v1_0.CartResource
			_cartResource;

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