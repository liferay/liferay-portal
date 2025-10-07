/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.payment.resource.v1_0.test;

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
import com.liferay.headless.commerce.admin.payment.client.dto.v1_0.Payment;
import com.liferay.headless.commerce.admin.payment.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.payment.client.pagination.Page;
import com.liferay.headless.commerce.admin.payment.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.payment.client.resource.v1_0.PaymentResource;
import com.liferay.headless.commerce.admin.payment.client.serdes.v1_0.PaymentSerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONDeserializer;
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
import com.liferay.portal.kernel.util.DateUtil;
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
import java.util.TimeZone;

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
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public abstract class BasePaymentResourceTestCase {

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

		_paymentResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		paymentResource = PaymentResource.builder(
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

		Payment payment1 = randomPayment();

		String json = objectMapper.writeValueAsString(payment1);

		Payment payment2 = PaymentSerDes.toDTO(json);

		Assert.assertTrue(equals(payment1, payment2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Payment payment = randomPayment();

		String json1 = objectMapper.writeValueAsString(payment);
		String json2 = PaymentSerDes.toJSON(payment);

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

		Payment payment = randomPayment();

		payment.setAmountFormatted(regex);
		payment.setAuthor(regex);
		payment.setCallbackURL(regex);
		payment.setCancelURL(regex);
		payment.setComment(regex);
		payment.setCurrencyCode(regex);
		payment.setCurrencyExternalReferenceCode(regex);
		payment.setErrorMessages(regex);
		payment.setExternalReferenceCode(regex);
		payment.setLanguageId(regex);
		payment.setPayload(regex);
		payment.setPaymentIntegrationKey(regex);
		payment.setReasonKey(regex);
		payment.setRedirectURL(regex);
		payment.setRelatedItemName(regex);
		payment.setRelatedItemNameLabel(regex);
		payment.setTransactionCode(regex);
		payment.setTypeLabel(regex);

		String json = PaymentSerDes.toJSON(payment);

		Assert.assertFalse(json.contains(regex));

		payment = PaymentSerDes.toDTO(json);

		Assert.assertEquals(regex, payment.getAmountFormatted());
		Assert.assertEquals(regex, payment.getAuthor());
		Assert.assertEquals(regex, payment.getCallbackURL());
		Assert.assertEquals(regex, payment.getCancelURL());
		Assert.assertEquals(regex, payment.getComment());
		Assert.assertEquals(regex, payment.getCurrencyCode());
		Assert.assertEquals(regex, payment.getCurrencyExternalReferenceCode());
		Assert.assertEquals(regex, payment.getErrorMessages());
		Assert.assertEquals(regex, payment.getExternalReferenceCode());
		Assert.assertEquals(regex, payment.getLanguageId());
		Assert.assertEquals(regex, payment.getPayload());
		Assert.assertEquals(regex, payment.getPaymentIntegrationKey());
		Assert.assertEquals(regex, payment.getReasonKey());
		Assert.assertEquals(regex, payment.getRedirectURL());
		Assert.assertEquals(regex, payment.getRelatedItemName());
		Assert.assertEquals(regex, payment.getRelatedItemNameLabel());
		Assert.assertEquals(regex, payment.getTransactionCode());
		Assert.assertEquals(regex, payment.getTypeLabel());
	}

	@Test
	public void testDeletePayment() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Payment payment = testDeletePayment_addPayment();

		assertHttpResponseStatusCode(
			204, paymentResource.deletePaymentHttpResponse(payment.getId()));

		assertHttpResponseStatusCode(
			404, paymentResource.getPaymentHttpResponse(payment.getId()));
		assertHttpResponseStatusCode(
			404, paymentResource.getPaymentHttpResponse(0L));
	}

	protected Payment testDeletePayment_addPayment() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeletePayment() throws Exception {

		// No namespace

		Payment payment1 = testGraphQLDeletePayment_addPayment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deletePayment",
						new HashMap<String, Object>() {
							{
								put("id", payment1.getId());
							}
						})),
				"JSONObject/data", "Object/deletePayment"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"payment",
					new HashMap<String, Object>() {
						{
							put("id", payment1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminPayment_v1_0

		Payment payment2 = testGraphQLDeletePayment_addPayment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminPayment_v1_0",
						new GraphQLField(
							"deletePayment",
							new HashMap<String, Object>() {
								{
									put("id", payment2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminPayment_v1_0",
				"Object/deletePayment"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminPayment_v1_0",
					new GraphQLField(
						"payment",
						new HashMap<String, Object>() {
							{
								put("id", payment2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Payment testGraphQLDeletePayment_addPayment() throws Exception {
		return testGraphQLPayment_addPayment();
	}

	@Test
	public void testDeletePaymentBatch() throws Exception {
		Payment payment1 = testDeletePaymentBatch_addPayment();

		testDeletePaymentBatch_deletePayment(
			202, payment1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, paymentResource.getPaymentHttpResponse(payment1.getId()));

		payment1 = testDeletePaymentBatch_addPayment();

		testDeletePaymentBatch_deletePayment(202, null, payment1.getId());

		assertHttpResponseStatusCode(
			404, paymentResource.getPaymentHttpResponse(payment1.getId()));

		payment1 = testDeletePaymentBatch_addPayment();
		Payment payment2 = testDeletePaymentBatch_addPayment();

		testDeletePaymentBatch_deletePayment(
			202, payment2.getExternalReferenceCode(), payment1.getId());

		assertHttpResponseStatusCode(
			404, paymentResource.getPaymentHttpResponse(payment1.getId()));
		assertHttpResponseStatusCode(
			200, paymentResource.getPaymentHttpResponse(payment2.getId()));

		testDeletePaymentBatch_deletePayment(
			202, payment2.getExternalReferenceCode(), payment1.getId());

		assertHttpResponseStatusCode(
			404, paymentResource.getPaymentHttpResponse(payment2.getId()));
	}

	protected Payment testDeletePaymentBatch_addPayment() throws Exception {
		return testDeletePayment_addPayment();
	}

	protected void testDeletePaymentBatch_deletePayment(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			paymentResource.deletePaymentBatchHttpResponse(
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
	public void testDeletePaymentByExternalReferenceCode() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Payment payment = testDeletePaymentByExternalReferenceCode_addPayment();

		assertHttpResponseStatusCode(
			204,
			paymentResource.deletePaymentByExternalReferenceCodeHttpResponse(
				payment.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			paymentResource.getPaymentByExternalReferenceCodeHttpResponse(
				payment.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			paymentResource.getPaymentByExternalReferenceCodeHttpResponse("-"));
	}

	protected Payment testDeletePaymentByExternalReferenceCode_addPayment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeletePaymentByExternalReferenceCode()
		throws Exception {

		// No namespace

		Payment payment1 =
			testGraphQLDeletePaymentByExternalReferenceCode_addPayment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deletePaymentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + payment1.getExternalReferenceCode() +
										"\"");
							}
						})),
				"JSONObject/data",
				"Object/deletePaymentByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"paymentByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + payment1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminPayment_v1_0

		Payment payment2 =
			testGraphQLDeletePaymentByExternalReferenceCode_addPayment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminPayment_v1_0",
						new GraphQLField(
							"deletePaymentByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											payment2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminPayment_v1_0",
				"Object/deletePaymentByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminPayment_v1_0",
					new GraphQLField(
						"paymentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + payment2.getExternalReferenceCode() +
										"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Payment
			testGraphQLDeletePaymentByExternalReferenceCode_addPayment()
		throws Exception {

		return testGraphQLPayment_addPayment();
	}

	@Test
	public void testGetPayment() throws Exception {
		Payment postPayment = testGetPayment_addPayment();

		Payment getPayment = paymentResource.getPayment(postPayment.getId());

		assertEquals(postPayment, getPayment);
		assertValid(getPayment);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		Payment postPayment = testGetPayment_addPayment();

		Payment getPayment = paymentResource.getPayment(postPayment.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.payment.dto.v1_0.Payment"
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

		Object item = vulcanCRUDItemDelegate.getItem(postPayment.getId());

		assertEquals(getPayment, PaymentSerDes.toDTO(item.toString()));
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

	protected Payment testGetPayment_addPayment() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetPayment() throws Exception {
		Payment payment = testGraphQLGetPayment_addPayment();

		// No namespace

		Assert.assertTrue(
			equals(
				payment,
				PaymentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"payment",
								new HashMap<String, Object>() {
									{
										put("id", payment.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/payment"))));

		// Using the namespace headlessCommerceAdminPayment_v1_0

		Assert.assertTrue(
			equals(
				payment,
				PaymentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminPayment_v1_0",
								new GraphQLField(
									"payment",
									new HashMap<String, Object>() {
										{
											put("id", payment.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminPayment_v1_0",
						"Object/payment"))));
	}

	@Test
	public void testGraphQLGetPaymentNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"payment",
						new HashMap<String, Object>() {
							{
								put("id", irrelevantId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminPayment_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminPayment_v1_0",
						new GraphQLField(
							"payment",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Payment testGraphQLGetPayment_addPayment() throws Exception {
		return testGraphQLPayment_addPayment();
	}

	@Test
	public void testGetPaymentByExternalReferenceCode() throws Exception {
		Payment postPayment =
			testGetPaymentByExternalReferenceCode_addPayment();

		Payment getPayment = paymentResource.getPaymentByExternalReferenceCode(
			postPayment.getExternalReferenceCode());

		assertEquals(postPayment, getPayment);
		assertValid(getPayment);
	}

	protected Payment testGetPaymentByExternalReferenceCode_addPayment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetPaymentByExternalReferenceCode()
		throws Exception {

		Payment payment =
			testGraphQLGetPaymentByExternalReferenceCode_addPayment();

		// No namespace

		Assert.assertTrue(
			equals(
				payment,
				PaymentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"paymentByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												payment.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/paymentByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminPayment_v1_0

		Assert.assertTrue(
			equals(
				payment,
				PaymentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminPayment_v1_0",
								new GraphQLField(
									"paymentByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													payment.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminPayment_v1_0",
						"Object/paymentByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetPaymentByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"paymentByExternalReferenceCode",
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

		// Using the namespace headlessCommerceAdminPayment_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminPayment_v1_0",
						new GraphQLField(
							"paymentByExternalReferenceCode",
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

	protected Payment testGraphQLGetPaymentByExternalReferenceCode_addPayment()
		throws Exception {

		return testGraphQLPayment_addPayment();
	}

	@Test
	public void testGetPaymentsPage() throws Exception {
		Page<Payment> page = paymentResource.getPaymentsPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		Payment payment1 = testGetPaymentsPage_addPayment(randomPayment());

		Payment payment2 = testGetPaymentsPage_addPayment(randomPayment());

		page = paymentResource.getPaymentsPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(payment1, (List<Payment>)page.getItems());
		assertContains(payment2, (List<Payment>)page.getItems());
		assertValid(page, testGetPaymentsPage_getExpectedActions());

		paymentResource.deletePayment(payment1.getId());

		paymentResource.deletePayment(payment2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetPaymentsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetPaymentsPageWithFilterDateTimeEquals() throws Exception {
		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Payment payment1 = randomPayment();

		payment1 = testGetPaymentsPage_addPayment(payment1);

		for (EntityField entityField : entityFields) {
			Page<Payment> page = paymentResource.getPaymentsPage(
				null, getFilterString(entityField, "between", payment1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(payment1),
				(List<Payment>)page.getItems());
		}
	}

	@Test
	public void testGetPaymentsPageWithFilterDoubleEquals() throws Exception {
		testGetPaymentsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetPaymentsPageWithFilterStringContains() throws Exception {
		testGetPaymentsPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetPaymentsPageWithFilterStringEquals() throws Exception {
		testGetPaymentsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetPaymentsPageWithFilterStringStartsWith()
		throws Exception {

		testGetPaymentsPageWithFilter("startswith", EntityField.Type.STRING);
	}

	protected void testGetPaymentsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Payment payment1 = testGetPaymentsPage_addPayment(randomPayment());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Payment payment2 = testGetPaymentsPage_addPayment(randomPayment());

		for (EntityField entityField : entityFields) {
			Page<Payment> page = paymentResource.getPaymentsPage(
				null, getFilterString(entityField, operator, payment1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(payment1),
				(List<Payment>)page.getItems());
		}
	}

	@Test
	public void testGetPaymentsPageWithPagination() throws Exception {
		Page<Payment> paymentsPage = paymentResource.getPaymentsPage(
			null, null, null, null);

		int totalCount = GetterUtil.getInteger(paymentsPage.getTotalCount());

		Payment payment1 = testGetPaymentsPage_addPayment(randomPayment());

		Payment payment2 = testGetPaymentsPage_addPayment(randomPayment());

		Payment payment3 = testGetPaymentsPage_addPayment(randomPayment());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Payment> page1 = paymentResource.getPaymentsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(payment1, (List<Payment>)page1.getItems());

			Page<Payment> page2 = paymentResource.getPaymentsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(payment2, (List<Payment>)page2.getItems());

			Page<Payment> page3 = paymentResource.getPaymentsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(payment3, (List<Payment>)page3.getItems());
		}
		else {
			Page<Payment> page1 = paymentResource.getPaymentsPage(
				null, null, Pagination.of(1, totalCount + 2), null);

			List<Payment> payments1 = (List<Payment>)page1.getItems();

			Assert.assertEquals(
				payments1.toString(), totalCount + 2, payments1.size());

			Page<Payment> page2 = paymentResource.getPaymentsPage(
				null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Payment> payments2 = (List<Payment>)page2.getItems();

			Assert.assertEquals(payments2.toString(), 1, payments2.size());

			Page<Payment> page3 = paymentResource.getPaymentsPage(
				null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(payment1, (List<Payment>)page3.getItems());
			assertContains(payment2, (List<Payment>)page3.getItems());
			assertContains(payment3, (List<Payment>)page3.getItems());
		}
	}

	@Test
	public void testGetPaymentsPageWithSortDateTime() throws Exception {
		testGetPaymentsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, payment1, payment2) -> {
				BeanTestUtil.setProperty(
					payment1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetPaymentsPageWithSortDouble() throws Exception {
		testGetPaymentsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, payment1, payment2) -> {
				BeanTestUtil.setProperty(payment1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(payment2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetPaymentsPageWithSortInteger() throws Exception {
		testGetPaymentsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, payment1, payment2) -> {
				BeanTestUtil.setProperty(payment1, entityField.getName(), 0);
				BeanTestUtil.setProperty(payment2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetPaymentsPageWithSortString() throws Exception {
		testGetPaymentsPageWithSort(
			EntityField.Type.STRING,
			(entityField, payment1, payment2) -> {
				Class<?> clazz = payment1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						payment1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						payment2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						payment1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						payment2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						payment1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						payment2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetPaymentsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Payment, Payment, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Payment payment1 = randomPayment();
		Payment payment2 = randomPayment();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, payment1, payment2);
		}

		payment1 = testGetPaymentsPage_addPayment(payment1);

		payment2 = testGetPaymentsPage_addPayment(payment2);

		Page<Payment> page = paymentResource.getPaymentsPage(
			null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Payment> ascPage = paymentResource.getPaymentsPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(payment1, (List<Payment>)ascPage.getItems());
			assertContains(payment2, (List<Payment>)ascPage.getItems());

			Page<Payment> descPage = paymentResource.getPaymentsPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(payment2, (List<Payment>)descPage.getItems());
			assertContains(payment1, (List<Payment>)descPage.getItems());
		}
	}

	protected Payment testGetPaymentsPage_addPayment(Payment payment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetPaymentsPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"payments",
			new HashMap<String, Object>() {
				{
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject paymentsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/payments");

		long totalCount = paymentsJSONObject.getLong("totalCount");

		Payment payment1 = testGraphQLPayment_addPayment(randomPayment());

		Payment payment2 = testGraphQLPayment_addPayment(randomPayment());

		paymentsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/payments");

		Assert.assertEquals(
			totalCount + 2, paymentsJSONObject.getLong("totalCount"));

		assertContains(
			payment1,
			Arrays.asList(
				PaymentSerDes.toDTOs(paymentsJSONObject.getString("items"))));
		assertContains(
			payment2,
			Arrays.asList(
				PaymentSerDes.toDTOs(paymentsJSONObject.getString("items"))));

		// Using the namespace headlessCommerceAdminPayment_v1_0

		paymentsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminPayment_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessCommerceAdminPayment_v1_0",
			"JSONObject/payments");

		Assert.assertEquals(
			totalCount + 2, paymentsJSONObject.getLong("totalCount"));

		assertContains(
			payment1,
			Arrays.asList(
				PaymentSerDes.toDTOs(paymentsJSONObject.getString("items"))));
		assertContains(
			payment2,
			Arrays.asList(
				PaymentSerDes.toDTOs(paymentsJSONObject.getString("items"))));
	}

	@Test
	public void testPatchPayment() throws Exception {
		Payment postPayment = testPatchPayment_addPayment();

		Payment randomPatchPayment = randomPatchPayment();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Payment patchPayment = paymentResource.patchPayment(
			postPayment.getId(), randomPatchPayment);

		Payment expectedPatchPayment = postPayment.clone();

		BeanTestUtil.copyProperties(randomPatchPayment, expectedPatchPayment);

		Payment getPayment = paymentResource.getPayment(patchPayment.getId());

		assertEquals(expectedPatchPayment, getPayment);
		assertValid(getPayment);
	}

	protected Payment testPatchPayment_addPayment() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchPaymentByExternalReferenceCode() throws Exception {
		Payment postPayment =
			testPatchPaymentByExternalReferenceCode_addPayment();

		Payment randomPatchPayment = randomPatchPayment();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Payment patchPayment =
			paymentResource.patchPaymentByExternalReferenceCode(
				postPayment.getExternalReferenceCode(), randomPatchPayment);

		Payment expectedPatchPayment = postPayment.clone();

		BeanTestUtil.copyProperties(randomPatchPayment, expectedPatchPayment);

		Payment getPayment = paymentResource.getPaymentByExternalReferenceCode(
			patchPayment.getExternalReferenceCode());

		assertEquals(expectedPatchPayment, getPayment);
		assertValid(getPayment);
	}

	protected Payment testPatchPaymentByExternalReferenceCode_addPayment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostPayment() throws Exception {
		Payment randomPayment = randomPayment();

		Payment postPayment = testPostPayment_addPayment(randomPayment);

		assertEquals(randomPayment, postPayment);
		assertValid(postPayment);
	}

	protected Payment testPostPayment_addPayment(Payment payment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostPayment() throws Exception {
		Payment randomPayment = randomPayment();

		Payment payment = testGraphQLPayment_addPayment(randomPayment);

		Assert.assertTrue(equals(randomPayment, payment));
	}

	@Test
	public void testPostPaymentByExternalReferenceCodeRefund()
		throws Exception {

		Payment randomPayment = randomPayment();

		Payment postPayment =
			testPostPaymentByExternalReferenceCodeRefund_addPayment(
				randomPayment);

		assertEquals(randomPayment, postPayment);
		assertValid(postPayment);
	}

	protected Payment testPostPaymentByExternalReferenceCodeRefund_addPayment(
			Payment payment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostPaymentByExternalReferenceCodeRefund()
		throws Exception {

		Payment randomPayment = randomPayment();

		Payment payment = testGraphQLPayment_addPayment(randomPayment);

		Assert.assertTrue(equals(randomPayment, payment));
	}

	@Test
	public void testPostPaymentRefund() throws Exception {
		Payment randomPayment = randomPayment();

		Payment postPayment = testPostPaymentRefund_addPayment(randomPayment);

		assertEquals(randomPayment, postPayment);
		assertValid(postPayment);
	}

	protected Payment testPostPaymentRefund_addPayment(Payment payment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostPaymentRefund() throws Exception {
		Payment randomPayment = randomPayment();

		Payment payment = testGraphQLPayment_addPayment(randomPayment);

		Assert.assertTrue(equals(randomPayment, payment));
	}

	@Test
	public void testPutPaymentByExternalReferenceCode() throws Exception {
		Payment postPayment =
			testPutPaymentByExternalReferenceCode_addPayment();

		Payment randomPayment = randomPayment();

		Payment putPayment = paymentResource.putPaymentByExternalReferenceCode(
			postPayment.getExternalReferenceCode(), randomPayment);

		assertEquals(randomPayment, putPayment);
		assertValid(putPayment);

		Payment getPayment = paymentResource.getPaymentByExternalReferenceCode(
			putPayment.getExternalReferenceCode());

		assertEquals(randomPayment, getPayment);
		assertValid(getPayment);

		Payment newPayment =
			testPutPaymentByExternalReferenceCode_createPayment();

		putPayment = paymentResource.putPaymentByExternalReferenceCode(
			newPayment.getExternalReferenceCode(), newPayment);

		assertEquals(newPayment, putPayment);
		assertValid(putPayment);

		getPayment = paymentResource.getPaymentByExternalReferenceCode(
			putPayment.getExternalReferenceCode());

		assertEquals(newPayment, getPayment);

		Assert.assertEquals(
			newPayment.getExternalReferenceCode(),
			putPayment.getExternalReferenceCode());
	}

	protected Payment testPutPaymentByExternalReferenceCode_addPayment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Payment testPutPaymentByExternalReferenceCode_createPayment()
		throws Exception {

		return randomPayment();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Payment payment1 = testBatchEngineDeleteImportTask_addPayment();

		testBatchEngineDeleteImportTask_deletePayment(
			200, payment1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, paymentResource.getPaymentHttpResponse(payment1.getId()));

		payment1 = testBatchEngineDeleteImportTask_addPayment();

		testBatchEngineDeleteImportTask_deletePayment(
			200, null, payment1.getId());

		assertHttpResponseStatusCode(
			404, paymentResource.getPaymentHttpResponse(payment1.getId()));

		payment1 = testBatchEngineDeleteImportTask_addPayment();
		Payment payment2 = testBatchEngineDeleteImportTask_addPayment();

		testBatchEngineDeleteImportTask_deletePayment(
			200, payment2.getExternalReferenceCode(), payment1.getId());

		assertHttpResponseStatusCode(
			404, paymentResource.getPaymentHttpResponse(payment1.getId()));
		assertHttpResponseStatusCode(
			200, paymentResource.getPaymentHttpResponse(payment2.getId()));

		testBatchEngineDeleteImportTask_deletePayment(
			200, payment2.getExternalReferenceCode(), payment1.getId());

		assertHttpResponseStatusCode(
			404, paymentResource.getPaymentHttpResponse(payment2.getId()));
	}

	protected Payment testBatchEngineDeleteImportTask_addPayment()
		throws Exception {

		return testDeletePayment_addPayment();
	}

	protected void testBatchEngineDeleteImportTask_deletePayment(
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
				"com.liferay.headless.commerce.admin.payment.dto.v1_0.Payment",
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

	protected Payment testGraphQLPayment_addPayment() throws Exception {
		return testGraphQLPayment_addPayment(randomPayment());
	}

	protected Payment testGraphQLPayment_addPayment(Payment payment)
		throws Exception {

		JSONDeserializer<Payment> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field : getDeclaredFields(Payment.class)) {
			if (getGraphQLValue(field.get(payment)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(payment)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createPayment",
						new HashMap<String, Object>() {
							{
								put("payment", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createPayment"),
			Payment.class);
	}

	protected String getGraphQLValue(Object value) throws Exception {
		if (value == null) {
			return null;
		}
		else if (value instanceof Boolean || value instanceof Number) {
			return value.toString();
		}
		else if (value instanceof Date date) {
			return "\"" +
				DateUtil.getDate(
					date, "yyyy-MM-dd'T'HH:mm:ss'Z'", LocaleUtil.getDefault(),
					TimeZone.getTimeZone("UTC")) + "\"";
		}
		else if (value instanceof Enum<?> enm) {
			return enm.name();
		}
		else if (value instanceof Map<?, ?> map) {
			List<String> entries = new ArrayList<>();

			for (Map.Entry<?, ?> entry : map.entrySet()) {
				String graphQLValue = getGraphQLValue(entry.getValue());

				if (graphQLValue != null) {
					entries.add(entry.getKey() + ": " + graphQLValue);
				}
			}

			return "{" + String.join(", ", entries) + "}";
		}
		else if (value instanceof Object[] array) {
			List<String> entries = new ArrayList<>();

			for (Object entry : array) {
				String graphQLValue = getGraphQLValue(entry);

				if (graphQLValue != null) {
					entries.add(graphQLValue);
				}
			}

			return "[" + String.join(", ", entries) + "]";
		}
		else if (value instanceof String) {
			return "\"" + value + "\"";
		}
		else {
			List<String> entries = new ArrayList<>();

			Class<?> clazz = value.getClass();
			java.lang.reflect.Field[] declaredFields = getDeclaredFields(clazz);

			if (declaredFields.length == 0) {
				declaredFields = getDeclaredFields(clazz.getSuperclass());
			}

			for (java.lang.reflect.Field field : declaredFields) {
				String graphQLValue = getGraphQLValue(field.get(value));

				if (graphQLValue != null) {
					entries.add(field.getName() + ": " + graphQLValue);
				}
			}

			return "{" + String.join(", ", entries) + "}";
		}
	}

	protected void assertContains(Payment payment, List<Payment> payments) {
		boolean contains = false;

		for (Payment item : payments) {
			if (equals(payment, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(payments + " does not contain " + payment, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Payment payment1, Payment payment2) {
		Assert.assertTrue(
			payment1 + " does not equal " + payment2,
			equals(payment1, payment2));
	}

	protected void assertEquals(
		List<Payment> payments1, List<Payment> payments2) {

		Assert.assertEquals(payments1.size(), payments2.size());

		for (int i = 0; i < payments1.size(); i++) {
			Payment payment1 = payments1.get(i);
			Payment payment2 = payments2.get(i);

			assertEquals(payment1, payment2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Payment> payments1, List<Payment> payments2) {

		Assert.assertEquals(payments1.size(), payments2.size());

		for (Payment payment1 : payments1) {
			boolean contains = false;

			for (Payment payment2 : payments2) {
				if (equals(payment1, payment2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				payments2 + " does not contain " + payment1, contains);
		}
	}

	protected void assertValid(Payment payment) throws Exception {
		boolean valid = true;

		if (payment.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (payment.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("amount", additionalAssertFieldName)) {
				if (payment.getAmount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("amountFormatted", additionalAssertFieldName)) {
				if (payment.getAmountFormatted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("author", additionalAssertFieldName)) {
				if (payment.getAuthor() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("callbackURL", additionalAssertFieldName)) {
				if (payment.getCallbackURL() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("cancelURL", additionalAssertFieldName)) {
				if (payment.getCancelURL() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("channelId", additionalAssertFieldName)) {
				if (payment.getChannelId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("comment", additionalAssertFieldName)) {
				if (payment.getComment() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (payment.getCreateDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("currencyCode", additionalAssertFieldName)) {
				if (payment.getCurrencyCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"currencyExternalReferenceCode",
					additionalAssertFieldName)) {

				if (payment.getCurrencyExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("currencyId", additionalAssertFieldName)) {
				if (payment.getCurrencyId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("errorMessages", additionalAssertFieldName)) {
				if (payment.getErrorMessages() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (payment.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("languageId", additionalAssertFieldName)) {
				if (payment.getLanguageId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("payload", additionalAssertFieldName)) {
				if (payment.getPayload() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentIntegrationKey", additionalAssertFieldName)) {

				if (payment.getPaymentIntegrationKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentIntegrationType", additionalAssertFieldName)) {

				if (payment.getPaymentIntegrationType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("paymentStatus", additionalAssertFieldName)) {
				if (payment.getPaymentStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentStatusStatus", additionalAssertFieldName)) {

				if (payment.getPaymentStatusStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("reasonKey", additionalAssertFieldName)) {
				if (payment.getReasonKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("reasonName", additionalAssertFieldName)) {
				if (payment.getReasonName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("redirectURL", additionalAssertFieldName)) {
				if (payment.getRedirectURL() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("relatedItemId", additionalAssertFieldName)) {
				if (payment.getRelatedItemId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("relatedItemName", additionalAssertFieldName)) {
				if (payment.getRelatedItemName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"relatedItemNameLabel", additionalAssertFieldName)) {

				if (payment.getRelatedItemNameLabel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("transactionCode", additionalAssertFieldName)) {
				if (payment.getTransactionCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (payment.getType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("typeLabel", additionalAssertFieldName)) {
				if (payment.getTypeLabel() == null) {
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

	protected void assertValid(Page<Payment> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Payment> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Payment> payments = page.getItems();

		int size = payments.size();

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
					com.liferay.headless.commerce.admin.payment.dto.v1_0.
						Payment.class)) {

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

	protected boolean equals(Payment payment1, Payment payment2) {
		if (payment1 == payment2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)payment1.getActions(),
						(Map)payment2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("amount", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						payment1.getAmount(), payment2.getAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("amountFormatted", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						payment1.getAmountFormatted(),
						payment2.getAmountFormatted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("author", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						payment1.getAuthor(), payment2.getAuthor())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("callbackURL", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						payment1.getCallbackURL(), payment2.getCallbackURL())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("cancelURL", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						payment1.getCancelURL(), payment2.getCancelURL())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("channelId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						payment1.getChannelId(), payment2.getChannelId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("comment", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						payment1.getComment(), payment2.getComment())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						payment1.getCreateDate(), payment2.getCreateDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("currencyCode", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						payment1.getCurrencyCode(),
						payment2.getCurrencyCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"currencyExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						payment1.getCurrencyExternalReferenceCode(),
						payment2.getCurrencyExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("currencyId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						payment1.getCurrencyId(), payment2.getCurrencyId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("errorMessages", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						payment1.getErrorMessages(),
						payment2.getErrorMessages())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						payment1.getExternalReferenceCode(),
						payment2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(payment1.getId(), payment2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("languageId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						payment1.getLanguageId(), payment2.getLanguageId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("payload", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						payment1.getPayload(), payment2.getPayload())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentIntegrationKey", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						payment1.getPaymentIntegrationKey(),
						payment2.getPaymentIntegrationKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentIntegrationType", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						payment1.getPaymentIntegrationType(),
						payment2.getPaymentIntegrationType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("paymentStatus", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						payment1.getPaymentStatus(),
						payment2.getPaymentStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"paymentStatusStatus", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						payment1.getPaymentStatusStatus(),
						payment2.getPaymentStatusStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("reasonKey", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						payment1.getReasonKey(), payment2.getReasonKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("reasonName", additionalAssertFieldName)) {
				if (!equals(
						(Map)payment1.getReasonName(),
						(Map)payment2.getReasonName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("redirectURL", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						payment1.getRedirectURL(), payment2.getRedirectURL())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("relatedItemId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						payment1.getRelatedItemId(),
						payment2.getRelatedItemId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("relatedItemName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						payment1.getRelatedItemName(),
						payment2.getRelatedItemName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"relatedItemNameLabel", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						payment1.getRelatedItemNameLabel(),
						payment2.getRelatedItemNameLabel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("transactionCode", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						payment1.getTransactionCode(),
						payment2.getTransactionCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						payment1.getType(), payment2.getType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("typeLabel", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						payment1.getTypeLabel(), payment2.getTypeLabel())) {

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

		if (!(_paymentResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_paymentResource;

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
		EntityField entityField, String operator, Payment payment) {

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

		if (entityFieldName.equals("amount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("amountFormatted")) {
			Object object = payment.getAmountFormatted();

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

		if (entityFieldName.equals("author")) {
			Object object = payment.getAuthor();

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

		if (entityFieldName.equals("callbackURL")) {
			Object object = payment.getCallbackURL();

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

		if (entityFieldName.equals("cancelURL")) {
			Object object = payment.getCancelURL();

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

		if (entityFieldName.equals("channelId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("comment")) {
			Object object = payment.getComment();

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
				Date date = payment.getCreateDate();

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

				sb.append(_format.format(payment.getCreateDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("currencyCode")) {
			Object object = payment.getCurrencyCode();

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
			Object object = payment.getCurrencyExternalReferenceCode();

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

		if (entityFieldName.equals("errorMessages")) {
			Object object = payment.getErrorMessages();

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
			Object object = payment.getExternalReferenceCode();

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

		if (entityFieldName.equals("languageId")) {
			Object object = payment.getLanguageId();

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

		if (entityFieldName.equals("payload")) {
			Object object = payment.getPayload();

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

		if (entityFieldName.equals("paymentIntegrationKey")) {
			Object object = payment.getPaymentIntegrationKey();

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

		if (entityFieldName.equals("paymentIntegrationType")) {
			sb.append(String.valueOf(payment.getPaymentIntegrationType()));

			return sb.toString();
		}

		if (entityFieldName.equals("paymentStatus")) {
			sb.append(String.valueOf(payment.getPaymentStatus()));

			return sb.toString();
		}

		if (entityFieldName.equals("paymentStatusStatus")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("reasonKey")) {
			Object object = payment.getReasonKey();

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

		if (entityFieldName.equals("reasonName")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("redirectURL")) {
			Object object = payment.getRedirectURL();

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

		if (entityFieldName.equals("relatedItemId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("relatedItemName")) {
			Object object = payment.getRelatedItemName();

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

		if (entityFieldName.equals("relatedItemNameLabel")) {
			Object object = payment.getRelatedItemNameLabel();

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

		if (entityFieldName.equals("transactionCode")) {
			Object object = payment.getTransactionCode();

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
			sb.append(String.valueOf(payment.getType()));

			return sb.toString();
		}

		if (entityFieldName.equals("typeLabel")) {
			Object object = payment.getTypeLabel();

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

	protected Payment randomPayment() throws Exception {
		return new Payment() {
			{
				amountFormatted = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				author = StringUtil.toLowerCase(RandomTestUtil.randomString());
				callbackURL = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				cancelURL = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				channelId = RandomTestUtil.randomLong();
				comment = StringUtil.toLowerCase(RandomTestUtil.randomString());
				createDate = RandomTestUtil.nextDate();
				currencyCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				currencyExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				currencyId = RandomTestUtil.randomLong();
				errorMessages = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				languageId = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				payload = StringUtil.toLowerCase(RandomTestUtil.randomString());
				paymentIntegrationKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				paymentIntegrationType = RandomTestUtil.randomInt();
				paymentStatus = RandomTestUtil.randomInt();
				reasonKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				redirectURL = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				relatedItemId = RandomTestUtil.randomLong();
				relatedItemName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				relatedItemNameLabel = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				transactionCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				type = RandomTestUtil.randomInt();
				typeLabel = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected Payment randomIrrelevantPayment() throws Exception {
		Payment randomIrrelevantPayment = randomPayment();

		return randomIrrelevantPayment;
	}

	protected Payment randomPatchPayment() throws Exception {
		return randomPayment();
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

	protected PaymentResource paymentResource;
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
		LogFactoryUtil.getLog(BasePaymentResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.commerce.admin.payment.resource.v1_0.
			PaymentResource _paymentResource;

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