/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.resource.v2_0.test;

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
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.Discount;
import com.liferay.headless.commerce.admin.pricing.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Page;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.pricing.client.resource.v2_0.DiscountResource;
import com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0.DiscountSerDes;
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
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public abstract class BaseDiscountResourceTestCase {

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

		_discountResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		discountResource = DiscountResource.builder(
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

		Discount discount1 = randomDiscount();

		String json = objectMapper.writeValueAsString(discount1);

		Discount discount2 = DiscountSerDes.toDTO(json);

		Assert.assertTrue(equals(discount1, discount2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Discount discount = randomDiscount();

		String json1 = objectMapper.writeValueAsString(discount);
		String json2 = DiscountSerDes.toJSON(discount);

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

		Discount discount = randomDiscount();

		discount.setAmountFormatted(regex);
		discount.setCouponCode(regex);
		discount.setExternalReferenceCode(regex);
		discount.setLevel(regex);
		discount.setLimitationType(regex);
		discount.setTarget(regex);
		discount.setTitle(regex);

		String json = DiscountSerDes.toJSON(discount);

		Assert.assertFalse(json.contains(regex));

		discount = DiscountSerDes.toDTO(json);

		Assert.assertEquals(regex, discount.getAmountFormatted());
		Assert.assertEquals(regex, discount.getCouponCode());
		Assert.assertEquals(regex, discount.getExternalReferenceCode());
		Assert.assertEquals(regex, discount.getLevel());
		Assert.assertEquals(regex, discount.getLimitationType());
		Assert.assertEquals(regex, discount.getTarget());
		Assert.assertEquals(regex, discount.getTitle());
	}

	@Test
	public void testDeleteDiscount() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Discount discount = testDeleteDiscount_addDiscount();

		assertHttpResponseStatusCode(
			204, discountResource.deleteDiscountHttpResponse(discount.getId()));

		assertHttpResponseStatusCode(
			404, discountResource.getDiscountHttpResponse(discount.getId()));
		assertHttpResponseStatusCode(
			404, discountResource.getDiscountHttpResponse(0L));
	}

	protected Discount testDeleteDiscount_addDiscount() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteDiscount() throws Exception {

		// No namespace

		Discount discount1 = testGraphQLDeleteDiscount_addDiscount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteDiscount",
						new HashMap<String, Object>() {
							{
								put("id", discount1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteDiscount"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"discount",
					new HashMap<String, Object>() {
						{
							put("id", discount1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminPricing_v2_0

		Discount discount2 = testGraphQLDeleteDiscount_addDiscount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminPricing_v2_0",
						new GraphQLField(
							"deleteDiscount",
							new HashMap<String, Object>() {
								{
									put("id", discount2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminPricing_v2_0",
				"Object/deleteDiscount"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminPricing_v2_0",
					new GraphQLField(
						"discount",
						new HashMap<String, Object>() {
							{
								put("id", discount2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Discount testGraphQLDeleteDiscount_addDiscount()
		throws Exception {

		return testGraphQLDiscount_addDiscount();
	}

	@Test
	public void testDeleteDiscountBatch() throws Exception {
		Discount discount1 = testDeleteDiscountBatch_addDiscount();

		testDeleteDiscountBatch_deleteDiscount(
			202, discount1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, discountResource.getDiscountHttpResponse(discount1.getId()));

		discount1 = testDeleteDiscountBatch_addDiscount();

		testDeleteDiscountBatch_deleteDiscount(202, null, discount1.getId());

		assertHttpResponseStatusCode(
			404, discountResource.getDiscountHttpResponse(discount1.getId()));

		discount1 = testDeleteDiscountBatch_addDiscount();
		Discount discount2 = testDeleteDiscountBatch_addDiscount();

		testDeleteDiscountBatch_deleteDiscount(
			202, discount2.getExternalReferenceCode(), discount1.getId());

		assertHttpResponseStatusCode(
			404, discountResource.getDiscountHttpResponse(discount1.getId()));
		assertHttpResponseStatusCode(
			200, discountResource.getDiscountHttpResponse(discount2.getId()));

		testDeleteDiscountBatch_deleteDiscount(
			202, discount2.getExternalReferenceCode(), discount1.getId());

		assertHttpResponseStatusCode(
			404, discountResource.getDiscountHttpResponse(discount2.getId()));
	}

	protected Discount testDeleteDiscountBatch_addDiscount() throws Exception {
		return testDeleteDiscount_addDiscount();
	}

	protected void testDeleteDiscountBatch_deleteDiscount(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			discountResource.deleteDiscountBatchHttpResponse(
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
	public void testDeleteDiscountByExternalReferenceCode() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Discount discount =
			testDeleteDiscountByExternalReferenceCode_addDiscount();

		assertHttpResponseStatusCode(
			204,
			discountResource.deleteDiscountByExternalReferenceCodeHttpResponse(
				discount.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			discountResource.getDiscountByExternalReferenceCodeHttpResponse(
				discount.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			discountResource.getDiscountByExternalReferenceCodeHttpResponse(
				"-"));
	}

	protected Discount testDeleteDiscountByExternalReferenceCode_addDiscount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteDiscountByExternalReferenceCode()
		throws Exception {

		// No namespace

		Discount discount1 =
			testGraphQLDeleteDiscountByExternalReferenceCode_addDiscount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteDiscountByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										discount1.getExternalReferenceCode() +
											"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteDiscountByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"discountByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + discount1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminPricing_v2_0

		Discount discount2 =
			testGraphQLDeleteDiscountByExternalReferenceCode_addDiscount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminPricing_v2_0",
						new GraphQLField(
							"deleteDiscountByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											discount2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminPricing_v2_0",
				"Object/deleteDiscountByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminPricing_v2_0",
					new GraphQLField(
						"discountByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										discount2.getExternalReferenceCode() +
											"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Discount
			testGraphQLDeleteDiscountByExternalReferenceCode_addDiscount()
		throws Exception {

		return testGraphQLDiscount_addDiscount();
	}

	@Test
	public void testGetDiscount() throws Exception {
		Discount postDiscount = testGetDiscount_addDiscount();

		Discount getDiscount = discountResource.getDiscount(
			postDiscount.getId());

		assertEquals(postDiscount, getDiscount);
		assertValid(getDiscount);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		Discount postDiscount = testGetDiscount_addDiscount();

		Discount getDiscount = discountResource.getDiscount(
			postDiscount.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.pricing.dto.v2_0.Discount"
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

		Object item = vulcanCRUDItemDelegate.getItem(postDiscount.getId());

		assertEquals(getDiscount, DiscountSerDes.toDTO(item.toString()));
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

	protected Discount testGetDiscount_addDiscount() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetDiscount() throws Exception {
		Discount discount = testGraphQLGetDiscount_addDiscount();

		// No namespace

		Assert.assertTrue(
			equals(
				discount,
				DiscountSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"discount",
								new HashMap<String, Object>() {
									{
										put("id", discount.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/discount"))));

		// Using the namespace headlessCommerceAdminPricing_v2_0

		Assert.assertTrue(
			equals(
				discount,
				DiscountSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminPricing_v2_0",
								new GraphQLField(
									"discount",
									new HashMap<String, Object>() {
										{
											put("id", discount.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminPricing_v2_0",
						"Object/discount"))));
	}

	@Test
	public void testGraphQLGetDiscountNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"discount",
						new HashMap<String, Object>() {
							{
								put("id", irrelevantId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminPricing_v2_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminPricing_v2_0",
						new GraphQLField(
							"discount",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Discount testGraphQLGetDiscount_addDiscount() throws Exception {
		return testGraphQLDiscount_addDiscount();
	}

	@Test
	public void testGetDiscountByExternalReferenceCode() throws Exception {
		Discount postDiscount =
			testGetDiscountByExternalReferenceCode_addDiscount();

		Discount getDiscount =
			discountResource.getDiscountByExternalReferenceCode(
				postDiscount.getExternalReferenceCode());

		assertEquals(postDiscount, getDiscount);
		assertValid(getDiscount);
	}

	protected Discount testGetDiscountByExternalReferenceCode_addDiscount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetDiscountByExternalReferenceCode()
		throws Exception {

		Discount discount =
			testGraphQLGetDiscountByExternalReferenceCode_addDiscount();

		// No namespace

		Assert.assertTrue(
			equals(
				discount,
				DiscountSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"discountByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												discount.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/discountByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminPricing_v2_0

		Assert.assertTrue(
			equals(
				discount,
				DiscountSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminPricing_v2_0",
								new GraphQLField(
									"discountByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													discount.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminPricing_v2_0",
						"Object/discountByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetDiscountByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"discountByExternalReferenceCode",
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

		// Using the namespace headlessCommerceAdminPricing_v2_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminPricing_v2_0",
						new GraphQLField(
							"discountByExternalReferenceCode",
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

	protected Discount
			testGraphQLGetDiscountByExternalReferenceCode_addDiscount()
		throws Exception {

		return testGraphQLDiscount_addDiscount();
	}

	@Test
	public void testGetDiscountsPage() throws Exception {
		Page<Discount> page = discountResource.getDiscountsPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		Discount discount1 = testGetDiscountsPage_addDiscount(randomDiscount());

		Discount discount2 = testGetDiscountsPage_addDiscount(randomDiscount());

		page = discountResource.getDiscountsPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(discount1, (List<Discount>)page.getItems());
		assertContains(discount2, (List<Discount>)page.getItems());
		assertValid(page, testGetDiscountsPage_getExpectedActions());

		discountResource.deleteDiscount(discount1.getId());

		discountResource.deleteDiscount(discount2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetDiscountsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetDiscountsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Discount discount1 = randomDiscount();

		discount1 = testGetDiscountsPage_addDiscount(discount1);

		for (EntityField entityField : entityFields) {
			Page<Discount> page = discountResource.getDiscountsPage(
				null, getFilterString(entityField, "between", discount1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(discount1),
				(List<Discount>)page.getItems());
		}
	}

	@Test
	public void testGetDiscountsPageWithFilterDoubleEquals() throws Exception {
		testGetDiscountsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetDiscountsPageWithFilterStringContains()
		throws Exception {

		testGetDiscountsPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetDiscountsPageWithFilterStringEquals() throws Exception {
		testGetDiscountsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetDiscountsPageWithFilterStringStartsWith()
		throws Exception {

		testGetDiscountsPageWithFilter("startswith", EntityField.Type.STRING);
	}

	protected void testGetDiscountsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Discount discount1 = testGetDiscountsPage_addDiscount(randomDiscount());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Discount discount2 = testGetDiscountsPage_addDiscount(randomDiscount());

		for (EntityField entityField : entityFields) {
			Page<Discount> page = discountResource.getDiscountsPage(
				null, getFilterString(entityField, operator, discount1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(discount1),
				(List<Discount>)page.getItems());
		}
	}

	@Test
	public void testGetDiscountsPageWithPagination() throws Exception {
		Page<Discount> discountsPage = discountResource.getDiscountsPage(
			null, null, null, null);

		int totalCount = GetterUtil.getInteger(discountsPage.getTotalCount());

		Discount discount1 = testGetDiscountsPage_addDiscount(randomDiscount());

		Discount discount2 = testGetDiscountsPage_addDiscount(randomDiscount());

		Discount discount3 = testGetDiscountsPage_addDiscount(randomDiscount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Discount> page1 = discountResource.getDiscountsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(discount1, (List<Discount>)page1.getItems());

			Page<Discount> page2 = discountResource.getDiscountsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(discount2, (List<Discount>)page2.getItems());

			Page<Discount> page3 = discountResource.getDiscountsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(discount3, (List<Discount>)page3.getItems());
		}
		else {
			Page<Discount> page1 = discountResource.getDiscountsPage(
				null, null, Pagination.of(1, totalCount + 2), null);

			List<Discount> discounts1 = (List<Discount>)page1.getItems();

			Assert.assertEquals(
				discounts1.toString(), totalCount + 2, discounts1.size());

			Page<Discount> page2 = discountResource.getDiscountsPage(
				null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Discount> discounts2 = (List<Discount>)page2.getItems();

			Assert.assertEquals(discounts2.toString(), 1, discounts2.size());

			Page<Discount> page3 = discountResource.getDiscountsPage(
				null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(discount1, (List<Discount>)page3.getItems());
			assertContains(discount2, (List<Discount>)page3.getItems());
			assertContains(discount3, (List<Discount>)page3.getItems());
		}
	}

	@Test
	public void testGetDiscountsPageWithSortDateTime() throws Exception {
		testGetDiscountsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, discount1, discount2) -> {
				BeanTestUtil.setProperty(
					discount1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetDiscountsPageWithSortDouble() throws Exception {
		testGetDiscountsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, discount1, discount2) -> {
				BeanTestUtil.setProperty(discount1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(discount2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetDiscountsPageWithSortInteger() throws Exception {
		testGetDiscountsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, discount1, discount2) -> {
				BeanTestUtil.setProperty(discount1, entityField.getName(), 0);
				BeanTestUtil.setProperty(discount2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetDiscountsPageWithSortString() throws Exception {
		testGetDiscountsPageWithSort(
			EntityField.Type.STRING,
			(entityField, discount1, discount2) -> {
				Class<?> clazz = discount1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						discount1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						discount2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						discount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						discount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						discount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						discount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetDiscountsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Discount, Discount, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Discount discount1 = randomDiscount();
		Discount discount2 = randomDiscount();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, discount1, discount2);
		}

		discount1 = testGetDiscountsPage_addDiscount(discount1);

		discount2 = testGetDiscountsPage_addDiscount(discount2);

		Page<Discount> page = discountResource.getDiscountsPage(
			null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Discount> ascPage = discountResource.getDiscountsPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(discount1, (List<Discount>)ascPage.getItems());
			assertContains(discount2, (List<Discount>)ascPage.getItems());

			Page<Discount> descPage = discountResource.getDiscountsPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(discount2, (List<Discount>)descPage.getItems());
			assertContains(discount1, (List<Discount>)descPage.getItems());
		}
	}

	protected Discount testGetDiscountsPage_addDiscount(Discount discount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetDiscountsPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"discounts",
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

		JSONObject discountsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/discounts");

		long totalCount = discountsJSONObject.getLong("totalCount");

		Discount discount1 = testGraphQLDiscount_addDiscount(randomDiscount());

		Discount discount2 = testGraphQLDiscount_addDiscount(randomDiscount());

		discountsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/discounts");

		Assert.assertEquals(
			totalCount + 2, discountsJSONObject.getLong("totalCount"));

		assertContains(
			discount1,
			Arrays.asList(
				DiscountSerDes.toDTOs(discountsJSONObject.getString("items"))));
		assertContains(
			discount2,
			Arrays.asList(
				DiscountSerDes.toDTOs(discountsJSONObject.getString("items"))));

		// Using the namespace headlessCommerceAdminPricing_v2_0

		discountsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminPricing_v2_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessCommerceAdminPricing_v2_0",
			"JSONObject/discounts");

		Assert.assertEquals(
			totalCount + 2, discountsJSONObject.getLong("totalCount"));

		assertContains(
			discount1,
			Arrays.asList(
				DiscountSerDes.toDTOs(discountsJSONObject.getString("items"))));
		assertContains(
			discount2,
			Arrays.asList(
				DiscountSerDes.toDTOs(discountsJSONObject.getString("items"))));
	}

	@Test
	public void testPatchDiscount() throws Exception {
		Discount postDiscount = testPatchDiscount_addDiscount();

		Discount randomPatchDiscount = randomPatchDiscount();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Discount patchDiscount = discountResource.patchDiscount(
			postDiscount.getId(), randomPatchDiscount);

		Discount expectedPatchDiscount = postDiscount.clone();

		BeanTestUtil.copyProperties(randomPatchDiscount, expectedPatchDiscount);

		Discount getDiscount = discountResource.getDiscount(
			patchDiscount.getId());

		assertEquals(expectedPatchDiscount, getDiscount);
		assertValid(getDiscount);
	}

	protected Discount testPatchDiscount_addDiscount() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchDiscountByExternalReferenceCode() throws Exception {
		Discount postDiscount =
			testPatchDiscountByExternalReferenceCode_addDiscount();

		Discount randomPatchDiscount = randomPatchDiscount();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Discount patchDiscount =
			discountResource.patchDiscountByExternalReferenceCode(
				postDiscount.getExternalReferenceCode(), randomPatchDiscount);

		Discount expectedPatchDiscount = postDiscount.clone();

		BeanTestUtil.copyProperties(randomPatchDiscount, expectedPatchDiscount);

		Discount getDiscount =
			discountResource.getDiscountByExternalReferenceCode(
				patchDiscount.getExternalReferenceCode());

		assertEquals(expectedPatchDiscount, getDiscount);
		assertValid(getDiscount);
	}

	protected Discount testPatchDiscountByExternalReferenceCode_addDiscount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostDiscount() throws Exception {
		Discount randomDiscount = randomDiscount();

		Discount postDiscount = testPostDiscount_addDiscount(randomDiscount);

		assertEquals(randomDiscount, postDiscount);
		assertValid(postDiscount);
	}

	protected Discount testPostDiscount_addDiscount(Discount discount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostDiscount() throws Exception {
		Discount randomDiscount = randomDiscount();

		Discount discount = testGraphQLDiscount_addDiscount(randomDiscount);

		Assert.assertTrue(equals(randomDiscount, discount));
	}

	@Test
	public void testPutDiscountByExternalReferenceCode() throws Exception {
		Discount postDiscount =
			testPutDiscountByExternalReferenceCode_addDiscount();

		Discount randomDiscount = randomDiscount();

		Discount putDiscount =
			discountResource.putDiscountByExternalReferenceCode(
				postDiscount.getExternalReferenceCode(), randomDiscount);

		assertEquals(randomDiscount, putDiscount);
		assertValid(putDiscount);

		Discount getDiscount =
			discountResource.getDiscountByExternalReferenceCode(
				putDiscount.getExternalReferenceCode());

		assertEquals(randomDiscount, getDiscount);
		assertValid(getDiscount);

		Discount newDiscount =
			testPutDiscountByExternalReferenceCode_createDiscount();

		putDiscount = discountResource.putDiscountByExternalReferenceCode(
			newDiscount.getExternalReferenceCode(), newDiscount);

		assertEquals(newDiscount, putDiscount);
		assertValid(putDiscount);

		getDiscount = discountResource.getDiscountByExternalReferenceCode(
			putDiscount.getExternalReferenceCode());

		assertEquals(newDiscount, getDiscount);

		Assert.assertEquals(
			newDiscount.getExternalReferenceCode(),
			putDiscount.getExternalReferenceCode());
	}

	protected Discount testPutDiscountByExternalReferenceCode_addDiscount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Discount testPutDiscountByExternalReferenceCode_createDiscount()
		throws Exception {

		return randomDiscount();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Discount discount1 = testBatchEngineDeleteImportTask_addDiscount();

		testBatchEngineDeleteImportTask_deleteDiscount(
			200, discount1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, discountResource.getDiscountHttpResponse(discount1.getId()));

		discount1 = testBatchEngineDeleteImportTask_addDiscount();

		testBatchEngineDeleteImportTask_deleteDiscount(
			200, null, discount1.getId());

		assertHttpResponseStatusCode(
			404, discountResource.getDiscountHttpResponse(discount1.getId()));

		discount1 = testBatchEngineDeleteImportTask_addDiscount();
		Discount discount2 = testBatchEngineDeleteImportTask_addDiscount();

		testBatchEngineDeleteImportTask_deleteDiscount(
			200, discount2.getExternalReferenceCode(), discount1.getId());

		assertHttpResponseStatusCode(
			404, discountResource.getDiscountHttpResponse(discount1.getId()));
		assertHttpResponseStatusCode(
			200, discountResource.getDiscountHttpResponse(discount2.getId()));

		testBatchEngineDeleteImportTask_deleteDiscount(
			200, discount2.getExternalReferenceCode(), discount1.getId());

		assertHttpResponseStatusCode(
			404, discountResource.getDiscountHttpResponse(discount2.getId()));
	}

	protected Discount testBatchEngineDeleteImportTask_addDiscount()
		throws Exception {

		return testDeleteDiscount_addDiscount();
	}

	protected void testBatchEngineDeleteImportTask_deleteDiscount(
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
				"com.liferay.headless.commerce.admin.pricing.dto.v2_0.Discount",
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

	protected Discount testGraphQLDiscount_addDiscount() throws Exception {
		return testGraphQLDiscount_addDiscount(randomDiscount());
	}

	protected Discount testGraphQLDiscount_addDiscount(Discount discount)
		throws Exception {

		JSONDeserializer<Discount> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(Discount.class)) {

			if (getGraphQLValue(field.get(discount)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(discount)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createDiscount",
						new HashMap<String, Object>() {
							{
								put("discount", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createDiscount"),
			Discount.class);
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

	protected void assertContains(Discount discount, List<Discount> discounts) {
		boolean contains = false;

		for (Discount item : discounts) {
			if (equals(discount, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			discounts + " does not contain " + discount, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Discount discount1, Discount discount2) {
		Assert.assertTrue(
			discount1 + " does not equal " + discount2,
			equals(discount1, discount2));
	}

	protected void assertEquals(
		List<Discount> discounts1, List<Discount> discounts2) {

		Assert.assertEquals(discounts1.size(), discounts2.size());

		for (int i = 0; i < discounts1.size(); i++) {
			Discount discount1 = discounts1.get(i);
			Discount discount2 = discounts2.get(i);

			assertEquals(discount1, discount2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Discount> discounts1, List<Discount> discounts2) {

		Assert.assertEquals(discounts1.size(), discounts2.size());

		for (Discount discount1 : discounts1) {
			boolean contains = false;

			for (Discount discount2 : discounts2) {
				if (equals(discount1, discount2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				discounts2 + " does not contain " + discount1, contains);
		}
	}

	protected void assertValid(Discount discount) throws Exception {
		boolean valid = true;

		if (discount.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (discount.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (discount.getActive() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("amountFormatted", additionalAssertFieldName)) {
				if (discount.getAmountFormatted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("couponCode", additionalAssertFieldName)) {
				if (discount.getCouponCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (discount.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"discountAccountGroups", additionalAssertFieldName)) {

				if (discount.getDiscountAccountGroups() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("discountAccounts", additionalAssertFieldName)) {
				if (discount.getDiscountAccounts() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"discountCategories", additionalAssertFieldName)) {

				if (discount.getDiscountCategories() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("discountChannels", additionalAssertFieldName)) {
				if (discount.getDiscountChannels() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"discountOrderTypes", additionalAssertFieldName)) {

				if (discount.getDiscountOrderTypes() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"discountProductGroups", additionalAssertFieldName)) {

				if (discount.getDiscountProductGroups() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("discountProducts", additionalAssertFieldName)) {
				if (discount.getDiscountProducts() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("discountRules", additionalAssertFieldName)) {
				if (discount.getDiscountRules() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("displayDate", additionalAssertFieldName)) {
				if (discount.getDisplayDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("expirationDate", additionalAssertFieldName)) {
				if (discount.getExpirationDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (discount.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("level", additionalAssertFieldName)) {
				if (discount.getLevel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("limitationTimes", additionalAssertFieldName)) {
				if (discount.getLimitationTimes() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"limitationTimesPerAccount", additionalAssertFieldName)) {

				if (discount.getLimitationTimesPerAccount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("limitationType", additionalAssertFieldName)) {
				if (discount.getLimitationType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"maximumDiscountAmount", additionalAssertFieldName)) {

				if (discount.getMaximumDiscountAmount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("modifiedDate", additionalAssertFieldName)) {
				if (discount.getModifiedDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("neverExpire", additionalAssertFieldName)) {
				if (discount.getNeverExpire() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("numberOfUse", additionalAssertFieldName)) {
				if (discount.getNumberOfUse() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("percentageLevel1", additionalAssertFieldName)) {
				if (discount.getPercentageLevel1() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("percentageLevel2", additionalAssertFieldName)) {
				if (discount.getPercentageLevel2() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("percentageLevel3", additionalAssertFieldName)) {
				if (discount.getPercentageLevel3() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("percentageLevel4", additionalAssertFieldName)) {
				if (discount.getPercentageLevel4() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("rulesConjunction", additionalAssertFieldName)) {
				if (discount.getRulesConjunction() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("target", additionalAssertFieldName)) {
				if (discount.getTarget() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (discount.getTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("useCouponCode", additionalAssertFieldName)) {
				if (discount.getUseCouponCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("usePercentage", additionalAssertFieldName)) {
				if (discount.getUsePercentage() == null) {
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

	protected void assertValid(Page<Discount> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Discount> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Discount> discounts = page.getItems();

		int size = discounts.size();

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
					com.liferay.headless.commerce.admin.pricing.dto.v2_0.
						Discount.class)) {

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

	protected boolean equals(Discount discount1, Discount discount2) {
		if (discount1 == discount2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)discount1.getActions(),
						(Map)discount2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discount1.getActive(), discount2.getActive())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("amountFormatted", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discount1.getAmountFormatted(),
						discount2.getAmountFormatted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("couponCode", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discount1.getCouponCode(), discount2.getCouponCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!equals(
						(Map)discount1.getCustomFields(),
						(Map)discount2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"discountAccountGroups", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						discount1.getDiscountAccountGroups(),
						discount2.getDiscountAccountGroups())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("discountAccounts", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discount1.getDiscountAccounts(),
						discount2.getDiscountAccounts())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"discountCategories", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						discount1.getDiscountCategories(),
						discount2.getDiscountCategories())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("discountChannels", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discount1.getDiscountChannels(),
						discount2.getDiscountChannels())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"discountOrderTypes", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						discount1.getDiscountOrderTypes(),
						discount2.getDiscountOrderTypes())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"discountProductGroups", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						discount1.getDiscountProductGroups(),
						discount2.getDiscountProductGroups())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("discountProducts", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discount1.getDiscountProducts(),
						discount2.getDiscountProducts())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("discountRules", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discount1.getDiscountRules(),
						discount2.getDiscountRules())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("displayDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discount1.getDisplayDate(),
						discount2.getDisplayDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("expirationDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discount1.getExpirationDate(),
						discount2.getExpirationDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						discount1.getExternalReferenceCode(),
						discount2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(discount1.getId(), discount2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("level", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discount1.getLevel(), discount2.getLevel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("limitationTimes", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discount1.getLimitationTimes(),
						discount2.getLimitationTimes())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"limitationTimesPerAccount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						discount1.getLimitationTimesPerAccount(),
						discount2.getLimitationTimesPerAccount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("limitationType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discount1.getLimitationType(),
						discount2.getLimitationType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"maximumDiscountAmount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						discount1.getMaximumDiscountAmount(),
						discount2.getMaximumDiscountAmount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("modifiedDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discount1.getModifiedDate(),
						discount2.getModifiedDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("neverExpire", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discount1.getNeverExpire(),
						discount2.getNeverExpire())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("numberOfUse", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discount1.getNumberOfUse(),
						discount2.getNumberOfUse())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("percentageLevel1", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discount1.getPercentageLevel1(),
						discount2.getPercentageLevel1())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("percentageLevel2", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discount1.getPercentageLevel2(),
						discount2.getPercentageLevel2())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("percentageLevel3", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discount1.getPercentageLevel3(),
						discount2.getPercentageLevel3())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("percentageLevel4", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discount1.getPercentageLevel4(),
						discount2.getPercentageLevel4())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("rulesConjunction", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discount1.getRulesConjunction(),
						discount2.getRulesConjunction())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("target", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discount1.getTarget(), discount2.getTarget())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discount1.getTitle(), discount2.getTitle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("useCouponCode", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discount1.getUseCouponCode(),
						discount2.getUseCouponCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("usePercentage", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discount1.getUsePercentage(),
						discount2.getUsePercentage())) {

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

		if (!(_discountResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_discountResource;

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
		EntityField entityField, String operator, Discount discount) {

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

		if (entityFieldName.equals("active")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("amountFormatted")) {
			Object object = discount.getAmountFormatted();

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

		if (entityFieldName.equals("couponCode")) {
			Object object = discount.getCouponCode();

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

		if (entityFieldName.equals("customFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountAccountGroups")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountAccounts")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountCategories")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountChannels")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountOrderTypes")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountProductGroups")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountProducts")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountRules")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("displayDate")) {
			if (operator.equals("between")) {
				Date date = discount.getDisplayDate();

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

				sb.append(_format.format(discount.getDisplayDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("expirationDate")) {
			if (operator.equals("between")) {
				Date date = discount.getExpirationDate();

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

				sb.append(_format.format(discount.getExpirationDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = discount.getExternalReferenceCode();

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

		if (entityFieldName.equals("level")) {
			Object object = discount.getLevel();

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

		if (entityFieldName.equals("limitationTimes")) {
			sb.append(String.valueOf(discount.getLimitationTimes()));

			return sb.toString();
		}

		if (entityFieldName.equals("limitationTimesPerAccount")) {
			sb.append(String.valueOf(discount.getLimitationTimesPerAccount()));

			return sb.toString();
		}

		if (entityFieldName.equals("limitationType")) {
			Object object = discount.getLimitationType();

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

		if (entityFieldName.equals("maximumDiscountAmount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("modifiedDate")) {
			if (operator.equals("between")) {
				Date date = discount.getModifiedDate();

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

				sb.append(_format.format(discount.getModifiedDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("neverExpire")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("numberOfUse")) {
			sb.append(String.valueOf(discount.getNumberOfUse()));

			return sb.toString();
		}

		if (entityFieldName.equals("percentageLevel1")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("percentageLevel2")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("percentageLevel3")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("percentageLevel4")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("rulesConjunction")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("target")) {
			Object object = discount.getTarget();

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

		if (entityFieldName.equals("title")) {
			Object object = discount.getTitle();

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

		if (entityFieldName.equals("useCouponCode")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("usePercentage")) {
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

	protected Discount randomDiscount() throws Exception {
		return new Discount() {
			{
				active = RandomTestUtil.randomBoolean();
				amountFormatted = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				couponCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				displayDate = RandomTestUtil.nextDate();
				expirationDate = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				level = StringUtil.toLowerCase(RandomTestUtil.randomString());
				limitationTimes = RandomTestUtil.randomInt();
				limitationTimesPerAccount = RandomTestUtil.randomInt();
				limitationType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				modifiedDate = RandomTestUtil.nextDate();
				neverExpire = RandomTestUtil.randomBoolean();
				numberOfUse = RandomTestUtil.randomInt();
				rulesConjunction = RandomTestUtil.randomBoolean();
				target = StringUtil.toLowerCase(RandomTestUtil.randomString());
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
				useCouponCode = RandomTestUtil.randomBoolean();
				usePercentage = RandomTestUtil.randomBoolean();
			}
		};
	}

	protected Discount randomIrrelevantDiscount() throws Exception {
		Discount randomIrrelevantDiscount = randomDiscount();

		return randomIrrelevantDiscount;
	}

	protected Discount randomPatchDiscount() throws Exception {
		return randomDiscount();
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

	protected DiscountResource discountResource;
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
		LogFactoryUtil.getLog(BaseDiscountResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.commerce.admin.pricing.resource.v2_0.
			DiscountResource _discountResource;

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