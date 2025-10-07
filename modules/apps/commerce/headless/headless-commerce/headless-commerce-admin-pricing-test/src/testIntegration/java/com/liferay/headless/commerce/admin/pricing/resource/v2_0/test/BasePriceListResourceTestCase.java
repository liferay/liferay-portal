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
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceList;
import com.liferay.headless.commerce.admin.pricing.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Page;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.pricing.client.resource.v2_0.PriceListResource;
import com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0.PriceListSerDes;
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
public abstract class BasePriceListResourceTestCase {

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

		_priceListResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		priceListResource = PriceListResource.builder(
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

		PriceList priceList1 = randomPriceList();

		String json = objectMapper.writeValueAsString(priceList1);

		PriceList priceList2 = PriceListSerDes.toDTO(json);

		Assert.assertTrue(equals(priceList1, priceList2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		PriceList priceList = randomPriceList();

		String json1 = objectMapper.writeValueAsString(priceList);
		String json2 = PriceListSerDes.toJSON(priceList);

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

		PriceList priceList = randomPriceList();

		priceList.setAuthor(regex);
		priceList.setCatalogName(regex);
		priceList.setCurrencyCode(regex);
		priceList.setCurrencyExternalReferenceCode(regex);
		priceList.setExternalReferenceCode(regex);
		priceList.setName(regex);

		String json = PriceListSerDes.toJSON(priceList);

		Assert.assertFalse(json.contains(regex));

		priceList = PriceListSerDes.toDTO(json);

		Assert.assertEquals(regex, priceList.getAuthor());
		Assert.assertEquals(regex, priceList.getCatalogName());
		Assert.assertEquals(regex, priceList.getCurrencyCode());
		Assert.assertEquals(
			regex, priceList.getCurrencyExternalReferenceCode());
		Assert.assertEquals(regex, priceList.getExternalReferenceCode());
		Assert.assertEquals(regex, priceList.getName());
	}

	@Test
	public void testDeletePriceList() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		PriceList priceList = testDeletePriceList_addPriceList();

		assertHttpResponseStatusCode(
			204,
			priceListResource.deletePriceListHttpResponse(priceList.getId()));

		assertHttpResponseStatusCode(
			404, priceListResource.getPriceListHttpResponse(priceList.getId()));
		assertHttpResponseStatusCode(
			404, priceListResource.getPriceListHttpResponse(0L));
	}

	protected PriceList testDeletePriceList_addPriceList() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeletePriceList() throws Exception {

		// No namespace

		PriceList priceList1 = testGraphQLDeletePriceList_addPriceList();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deletePriceList",
						new HashMap<String, Object>() {
							{
								put("id", priceList1.getId());
							}
						})),
				"JSONObject/data", "Object/deletePriceList"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"priceList",
					new HashMap<String, Object>() {
						{
							put("id", priceList1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminPricing_v2_0

		PriceList priceList2 = testGraphQLDeletePriceList_addPriceList();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminPricing_v2_0",
						new GraphQLField(
							"deletePriceList",
							new HashMap<String, Object>() {
								{
									put("id", priceList2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminPricing_v2_0",
				"Object/deletePriceList"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminPricing_v2_0",
					new GraphQLField(
						"priceList",
						new HashMap<String, Object>() {
							{
								put("id", priceList2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected PriceList testGraphQLDeletePriceList_addPriceList()
		throws Exception {

		return testGraphQLPriceList_addPriceList();
	}

	@Test
	public void testDeletePriceListBatch() throws Exception {
		PriceList priceList1 = testDeletePriceListBatch_addPriceList();

		testDeletePriceListBatch_deletePriceList(
			202, priceList1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			priceListResource.getPriceListHttpResponse(priceList1.getId()));

		priceList1 = testDeletePriceListBatch_addPriceList();

		testDeletePriceListBatch_deletePriceList(202, null, priceList1.getId());

		assertHttpResponseStatusCode(
			404,
			priceListResource.getPriceListHttpResponse(priceList1.getId()));

		priceList1 = testDeletePriceListBatch_addPriceList();
		PriceList priceList2 = testDeletePriceListBatch_addPriceList();

		testDeletePriceListBatch_deletePriceList(
			202, priceList2.getExternalReferenceCode(), priceList1.getId());

		assertHttpResponseStatusCode(
			404,
			priceListResource.getPriceListHttpResponse(priceList1.getId()));
		assertHttpResponseStatusCode(
			200,
			priceListResource.getPriceListHttpResponse(priceList2.getId()));

		testDeletePriceListBatch_deletePriceList(
			202, priceList2.getExternalReferenceCode(), priceList1.getId());

		assertHttpResponseStatusCode(
			404,
			priceListResource.getPriceListHttpResponse(priceList2.getId()));
	}

	protected PriceList testDeletePriceListBatch_addPriceList()
		throws Exception {

		return testDeletePriceList_addPriceList();
	}

	protected void testDeletePriceListBatch_deletePriceList(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			priceListResource.deletePriceListBatchHttpResponse(
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
	public void testDeletePriceListByExternalReferenceCode() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		PriceList priceList =
			testDeletePriceListByExternalReferenceCode_addPriceList();

		assertHttpResponseStatusCode(
			204,
			priceListResource.
				deletePriceListByExternalReferenceCodeHttpResponse(
					priceList.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			priceListResource.getPriceListByExternalReferenceCodeHttpResponse(
				priceList.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			priceListResource.getPriceListByExternalReferenceCodeHttpResponse(
				"-"));
	}

	protected PriceList
			testDeletePriceListByExternalReferenceCode_addPriceList()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeletePriceListByExternalReferenceCode()
		throws Exception {

		// No namespace

		PriceList priceList1 =
			testGraphQLDeletePriceListByExternalReferenceCode_addPriceList();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deletePriceListByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										priceList1.getExternalReferenceCode() +
											"\"");
							}
						})),
				"JSONObject/data",
				"Object/deletePriceListByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"priceListByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + priceList1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminPricing_v2_0

		PriceList priceList2 =
			testGraphQLDeletePriceListByExternalReferenceCode_addPriceList();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminPricing_v2_0",
						new GraphQLField(
							"deletePriceListByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											priceList2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminPricing_v2_0",
				"Object/deletePriceListByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminPricing_v2_0",
					new GraphQLField(
						"priceListByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										priceList2.getExternalReferenceCode() +
											"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected PriceList
			testGraphQLDeletePriceListByExternalReferenceCode_addPriceList()
		throws Exception {

		return testGraphQLPriceList_addPriceList();
	}

	@Test
	public void testGetPriceList() throws Exception {
		PriceList postPriceList = testGetPriceList_addPriceList();

		PriceList getPriceList = priceListResource.getPriceList(
			postPriceList.getId());

		assertEquals(postPriceList, getPriceList);
		assertValid(getPriceList);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		PriceList postPriceList = testGetPriceList_addPriceList();

		PriceList getPriceList = priceListResource.getPriceList(
			postPriceList.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceList"
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

		Object item = vulcanCRUDItemDelegate.getItem(postPriceList.getId());

		assertEquals(getPriceList, PriceListSerDes.toDTO(item.toString()));
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

	protected PriceList testGetPriceList_addPriceList() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetPriceList() throws Exception {
		PriceList priceList = testGraphQLGetPriceList_addPriceList();

		// No namespace

		Assert.assertTrue(
			equals(
				priceList,
				PriceListSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"priceList",
								new HashMap<String, Object>() {
									{
										put("id", priceList.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/priceList"))));

		// Using the namespace headlessCommerceAdminPricing_v2_0

		Assert.assertTrue(
			equals(
				priceList,
				PriceListSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminPricing_v2_0",
								new GraphQLField(
									"priceList",
									new HashMap<String, Object>() {
										{
											put("id", priceList.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminPricing_v2_0",
						"Object/priceList"))));
	}

	@Test
	public void testGraphQLGetPriceListNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"priceList",
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
							"priceList",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected PriceList testGraphQLGetPriceList_addPriceList()
		throws Exception {

		return testGraphQLPriceList_addPriceList();
	}

	@Test
	public void testGetPriceListByExternalReferenceCode() throws Exception {
		PriceList postPriceList =
			testGetPriceListByExternalReferenceCode_addPriceList();

		PriceList getPriceList =
			priceListResource.getPriceListByExternalReferenceCode(
				postPriceList.getExternalReferenceCode());

		assertEquals(postPriceList, getPriceList);
		assertValid(getPriceList);
	}

	protected PriceList testGetPriceListByExternalReferenceCode_addPriceList()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetPriceListByExternalReferenceCode()
		throws Exception {

		PriceList priceList =
			testGraphQLGetPriceListByExternalReferenceCode_addPriceList();

		// No namespace

		Assert.assertTrue(
			equals(
				priceList,
				PriceListSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"priceListByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												priceList.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/priceListByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminPricing_v2_0

		Assert.assertTrue(
			equals(
				priceList,
				PriceListSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminPricing_v2_0",
								new GraphQLField(
									"priceListByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													priceList.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminPricing_v2_0",
						"Object/priceListByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetPriceListByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"priceListByExternalReferenceCode",
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
							"priceListByExternalReferenceCode",
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

	protected PriceList
			testGraphQLGetPriceListByExternalReferenceCode_addPriceList()
		throws Exception {

		return testGraphQLPriceList_addPriceList();
	}

	@Test
	public void testGetPriceListsPage() throws Exception {
		Page<PriceList> page = priceListResource.getPriceListsPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		PriceList priceList1 = testGetPriceListsPage_addPriceList(
			randomPriceList());

		PriceList priceList2 = testGetPriceListsPage_addPriceList(
			randomPriceList());

		page = priceListResource.getPriceListsPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(priceList1, (List<PriceList>)page.getItems());
		assertContains(priceList2, (List<PriceList>)page.getItems());
		assertValid(page, testGetPriceListsPage_getExpectedActions());

		priceListResource.deletePriceList(priceList1.getId());

		priceListResource.deletePriceList(priceList2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetPriceListsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetPriceListsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		PriceList priceList1 = randomPriceList();

		priceList1 = testGetPriceListsPage_addPriceList(priceList1);

		for (EntityField entityField : entityFields) {
			Page<PriceList> page = priceListResource.getPriceListsPage(
				null, getFilterString(entityField, "between", priceList1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(priceList1),
				(List<PriceList>)page.getItems());
		}
	}

	@Test
	public void testGetPriceListsPageWithFilterDoubleEquals() throws Exception {
		testGetPriceListsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetPriceListsPageWithFilterStringContains()
		throws Exception {

		testGetPriceListsPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetPriceListsPageWithFilterStringEquals() throws Exception {
		testGetPriceListsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetPriceListsPageWithFilterStringStartsWith()
		throws Exception {

		testGetPriceListsPageWithFilter("startswith", EntityField.Type.STRING);
	}

	protected void testGetPriceListsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		PriceList priceList1 = testGetPriceListsPage_addPriceList(
			randomPriceList());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PriceList priceList2 = testGetPriceListsPage_addPriceList(
			randomPriceList());

		for (EntityField entityField : entityFields) {
			Page<PriceList> page = priceListResource.getPriceListsPage(
				null, getFilterString(entityField, operator, priceList1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(priceList1),
				(List<PriceList>)page.getItems());
		}
	}

	@Test
	public void testGetPriceListsPageWithPagination() throws Exception {
		Page<PriceList> priceListsPage = priceListResource.getPriceListsPage(
			null, null, null, null);

		int totalCount = GetterUtil.getInteger(priceListsPage.getTotalCount());

		PriceList priceList1 = testGetPriceListsPage_addPriceList(
			randomPriceList());

		PriceList priceList2 = testGetPriceListsPage_addPriceList(
			randomPriceList());

		PriceList priceList3 = testGetPriceListsPage_addPriceList(
			randomPriceList());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<PriceList> page1 = priceListResource.getPriceListsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(priceList1, (List<PriceList>)page1.getItems());

			Page<PriceList> page2 = priceListResource.getPriceListsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(priceList2, (List<PriceList>)page2.getItems());

			Page<PriceList> page3 = priceListResource.getPriceListsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(priceList3, (List<PriceList>)page3.getItems());
		}
		else {
			Page<PriceList> page1 = priceListResource.getPriceListsPage(
				null, null, Pagination.of(1, totalCount + 2), null);

			List<PriceList> priceLists1 = (List<PriceList>)page1.getItems();

			Assert.assertEquals(
				priceLists1.toString(), totalCount + 2, priceLists1.size());

			Page<PriceList> page2 = priceListResource.getPriceListsPage(
				null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PriceList> priceLists2 = (List<PriceList>)page2.getItems();

			Assert.assertEquals(priceLists2.toString(), 1, priceLists2.size());

			Page<PriceList> page3 = priceListResource.getPriceListsPage(
				null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(priceList1, (List<PriceList>)page3.getItems());
			assertContains(priceList2, (List<PriceList>)page3.getItems());
			assertContains(priceList3, (List<PriceList>)page3.getItems());
		}
	}

	@Test
	public void testGetPriceListsPageWithSortDateTime() throws Exception {
		testGetPriceListsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, priceList1, priceList2) -> {
				BeanTestUtil.setProperty(
					priceList1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetPriceListsPageWithSortDouble() throws Exception {
		testGetPriceListsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, priceList1, priceList2) -> {
				BeanTestUtil.setProperty(
					priceList1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					priceList2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetPriceListsPageWithSortInteger() throws Exception {
		testGetPriceListsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, priceList1, priceList2) -> {
				BeanTestUtil.setProperty(priceList1, entityField.getName(), 0);
				BeanTestUtil.setProperty(priceList2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetPriceListsPageWithSortString() throws Exception {
		testGetPriceListsPageWithSort(
			EntityField.Type.STRING,
			(entityField, priceList1, priceList2) -> {
				Class<?> clazz = priceList1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						priceList1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						priceList2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						priceList1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						priceList2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						priceList1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						priceList2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetPriceListsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, PriceList, PriceList, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		PriceList priceList1 = randomPriceList();
		PriceList priceList2 = randomPriceList();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, priceList1, priceList2);
		}

		priceList1 = testGetPriceListsPage_addPriceList(priceList1);

		priceList2 = testGetPriceListsPage_addPriceList(priceList2);

		Page<PriceList> page = priceListResource.getPriceListsPage(
			null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<PriceList> ascPage = priceListResource.getPriceListsPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(priceList1, (List<PriceList>)ascPage.getItems());
			assertContains(priceList2, (List<PriceList>)ascPage.getItems());

			Page<PriceList> descPage = priceListResource.getPriceListsPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(priceList2, (List<PriceList>)descPage.getItems());
			assertContains(priceList1, (List<PriceList>)descPage.getItems());
		}
	}

	protected PriceList testGetPriceListsPage_addPriceList(PriceList priceList)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetPriceListsPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"priceLists",
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

		JSONObject priceListsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/priceLists");

		long totalCount = priceListsJSONObject.getLong("totalCount");

		PriceList priceList1 = testGraphQLPriceList_addPriceList(
			randomPriceList());

		PriceList priceList2 = testGraphQLPriceList_addPriceList(
			randomPriceList());

		priceListsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/priceLists");

		Assert.assertEquals(
			totalCount + 2, priceListsJSONObject.getLong("totalCount"));

		assertContains(
			priceList1,
			Arrays.asList(
				PriceListSerDes.toDTOs(
					priceListsJSONObject.getString("items"))));
		assertContains(
			priceList2,
			Arrays.asList(
				PriceListSerDes.toDTOs(
					priceListsJSONObject.getString("items"))));

		// Using the namespace headlessCommerceAdminPricing_v2_0

		priceListsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminPricing_v2_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessCommerceAdminPricing_v2_0",
			"JSONObject/priceLists");

		Assert.assertEquals(
			totalCount + 2, priceListsJSONObject.getLong("totalCount"));

		assertContains(
			priceList1,
			Arrays.asList(
				PriceListSerDes.toDTOs(
					priceListsJSONObject.getString("items"))));
		assertContains(
			priceList2,
			Arrays.asList(
				PriceListSerDes.toDTOs(
					priceListsJSONObject.getString("items"))));
	}

	@Test
	public void testPatchPriceList() throws Exception {
		PriceList postPriceList = testPatchPriceList_addPriceList();

		PriceList randomPatchPriceList = randomPatchPriceList();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PriceList patchPriceList = priceListResource.patchPriceList(
			postPriceList.getId(), randomPatchPriceList);

		PriceList expectedPatchPriceList = postPriceList.clone();

		BeanTestUtil.copyProperties(
			randomPatchPriceList, expectedPatchPriceList);

		PriceList getPriceList = priceListResource.getPriceList(
			patchPriceList.getId());

		assertEquals(expectedPatchPriceList, getPriceList);
		assertValid(getPriceList);
	}

	protected PriceList testPatchPriceList_addPriceList() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchPriceListByExternalReferenceCode() throws Exception {
		PriceList postPriceList =
			testPatchPriceListByExternalReferenceCode_addPriceList();

		PriceList randomPatchPriceList = randomPatchPriceList();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PriceList patchPriceList =
			priceListResource.patchPriceListByExternalReferenceCode(
				postPriceList.getExternalReferenceCode(), randomPatchPriceList);

		PriceList expectedPatchPriceList = postPriceList.clone();

		BeanTestUtil.copyProperties(
			randomPatchPriceList, expectedPatchPriceList);

		PriceList getPriceList =
			priceListResource.getPriceListByExternalReferenceCode(
				patchPriceList.getExternalReferenceCode());

		assertEquals(expectedPatchPriceList, getPriceList);
		assertValid(getPriceList);
	}

	protected PriceList testPatchPriceListByExternalReferenceCode_addPriceList()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostPriceList() throws Exception {
		PriceList randomPriceList = randomPriceList();

		PriceList postPriceList = testPostPriceList_addPriceList(
			randomPriceList);

		assertEquals(randomPriceList, postPriceList);
		assertValid(postPriceList);
	}

	protected PriceList testPostPriceList_addPriceList(PriceList priceList)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostPriceList() throws Exception {
		PriceList randomPriceList = randomPriceList();

		PriceList priceList = testGraphQLPriceList_addPriceList(
			randomPriceList);

		Assert.assertTrue(equals(randomPriceList, priceList));
	}

	@Test
	public void testPutPriceListByExternalReferenceCode() throws Exception {
		PriceList postPriceList =
			testPutPriceListByExternalReferenceCode_addPriceList();

		PriceList randomPriceList = randomPriceList();

		PriceList putPriceList =
			priceListResource.putPriceListByExternalReferenceCode(
				postPriceList.getExternalReferenceCode(), randomPriceList);

		assertEquals(randomPriceList, putPriceList);
		assertValid(putPriceList);

		PriceList getPriceList =
			priceListResource.getPriceListByExternalReferenceCode(
				putPriceList.getExternalReferenceCode());

		assertEquals(randomPriceList, getPriceList);
		assertValid(getPriceList);

		PriceList newPriceList =
			testPutPriceListByExternalReferenceCode_createPriceList();

		putPriceList = priceListResource.putPriceListByExternalReferenceCode(
			newPriceList.getExternalReferenceCode(), newPriceList);

		assertEquals(newPriceList, putPriceList);
		assertValid(putPriceList);

		getPriceList = priceListResource.getPriceListByExternalReferenceCode(
			putPriceList.getExternalReferenceCode());

		assertEquals(newPriceList, getPriceList);

		Assert.assertEquals(
			newPriceList.getExternalReferenceCode(),
			putPriceList.getExternalReferenceCode());
	}

	protected PriceList testPutPriceListByExternalReferenceCode_addPriceList()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected PriceList
			testPutPriceListByExternalReferenceCode_createPriceList()
		throws Exception {

		return randomPriceList();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		PriceList priceList1 = testBatchEngineDeleteImportTask_addPriceList();

		testBatchEngineDeleteImportTask_deletePriceList(
			200, priceList1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			priceListResource.getPriceListHttpResponse(priceList1.getId()));

		priceList1 = testBatchEngineDeleteImportTask_addPriceList();

		testBatchEngineDeleteImportTask_deletePriceList(
			200, null, priceList1.getId());

		assertHttpResponseStatusCode(
			404,
			priceListResource.getPriceListHttpResponse(priceList1.getId()));

		priceList1 = testBatchEngineDeleteImportTask_addPriceList();
		PriceList priceList2 = testBatchEngineDeleteImportTask_addPriceList();

		testBatchEngineDeleteImportTask_deletePriceList(
			200, priceList2.getExternalReferenceCode(), priceList1.getId());

		assertHttpResponseStatusCode(
			404,
			priceListResource.getPriceListHttpResponse(priceList1.getId()));
		assertHttpResponseStatusCode(
			200,
			priceListResource.getPriceListHttpResponse(priceList2.getId()));

		testBatchEngineDeleteImportTask_deletePriceList(
			200, priceList2.getExternalReferenceCode(), priceList1.getId());

		assertHttpResponseStatusCode(
			404,
			priceListResource.getPriceListHttpResponse(priceList2.getId()));
	}

	protected PriceList testBatchEngineDeleteImportTask_addPriceList()
		throws Exception {

		return testDeletePriceList_addPriceList();
	}

	protected void testBatchEngineDeleteImportTask_deletePriceList(
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
				"com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceList",
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

	protected PriceList testGraphQLPriceList_addPriceList() throws Exception {
		return testGraphQLPriceList_addPriceList(randomPriceList());
	}

	protected PriceList testGraphQLPriceList_addPriceList(PriceList priceList)
		throws Exception {

		JSONDeserializer<PriceList> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(PriceList.class)) {

			if (getGraphQLValue(field.get(priceList)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(priceList)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createPriceList",
						new HashMap<String, Object>() {
							{
								put("priceList", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createPriceList"),
			PriceList.class);
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

	protected void assertContains(
		PriceList priceList, List<PriceList> priceLists) {

		boolean contains = false;

		for (PriceList item : priceLists) {
			if (equals(priceList, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			priceLists + " does not contain " + priceList, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(PriceList priceList1, PriceList priceList2) {
		Assert.assertTrue(
			priceList1 + " does not equal " + priceList2,
			equals(priceList1, priceList2));
	}

	protected void assertEquals(
		List<PriceList> priceLists1, List<PriceList> priceLists2) {

		Assert.assertEquals(priceLists1.size(), priceLists2.size());

		for (int i = 0; i < priceLists1.size(); i++) {
			PriceList priceList1 = priceLists1.get(i);
			PriceList priceList2 = priceLists2.get(i);

			assertEquals(priceList1, priceList2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<PriceList> priceLists1, List<PriceList> priceLists2) {

		Assert.assertEquals(priceLists1.size(), priceLists2.size());

		for (PriceList priceList1 : priceLists1) {
			boolean contains = false;

			for (PriceList priceList2 : priceLists2) {
				if (equals(priceList1, priceList2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				priceLists2 + " does not contain " + priceList1, contains);
		}
	}

	protected void assertValid(PriceList priceList) throws Exception {
		boolean valid = true;

		if (priceList.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (priceList.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (priceList.getActive() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("author", additionalAssertFieldName)) {
				if (priceList.getAuthor() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"catalogBasePriceList", additionalAssertFieldName)) {

				if (priceList.getCatalogBasePriceList() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("catalogId", additionalAssertFieldName)) {
				if (priceList.getCatalogId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("catalogName", additionalAssertFieldName)) {
				if (priceList.getCatalogName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (priceList.getCreateDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("currencyCode", additionalAssertFieldName)) {
				if (priceList.getCurrencyCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"currencyExternalReferenceCode",
					additionalAssertFieldName)) {

				if (priceList.getCurrencyExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("currencyId", additionalAssertFieldName)) {
				if (priceList.getCurrencyId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (priceList.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("displayDate", additionalAssertFieldName)) {
				if (priceList.getDisplayDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("expirationDate", additionalAssertFieldName)) {
				if (priceList.getExpirationDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (priceList.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (priceList.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("netPrice", additionalAssertFieldName)) {
				if (priceList.getNetPrice() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("neverExpire", additionalAssertFieldName)) {
				if (priceList.getNeverExpire() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"parentPriceListId", additionalAssertFieldName)) {

				if (priceList.getParentPriceListId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("priceEntries", additionalAssertFieldName)) {
				if (priceList.getPriceEntries() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"priceListAccountGroups", additionalAssertFieldName)) {

				if (priceList.getPriceListAccountGroups() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"priceListAccounts", additionalAssertFieldName)) {

				if (priceList.getPriceListAccounts() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"priceListChannels", additionalAssertFieldName)) {

				if (priceList.getPriceListChannels() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"priceListDiscounts", additionalAssertFieldName)) {

				if (priceList.getPriceListDiscounts() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"priceListOrderTypes", additionalAssertFieldName)) {

				if (priceList.getPriceListOrderTypes() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("priceModifiers", additionalAssertFieldName)) {
				if (priceList.getPriceModifiers() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (priceList.getPriority() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (priceList.getType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowStatusInfo", additionalAssertFieldName)) {

				if (priceList.getWorkflowStatusInfo() == null) {
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

	protected void assertValid(Page<PriceList> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<PriceList> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<PriceList> priceLists = page.getItems();

		int size = priceLists.size();

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
						PriceList.class)) {

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

	protected boolean equals(PriceList priceList1, PriceList priceList2) {
		if (priceList1 == priceList2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)priceList1.getActions(),
						(Map)priceList2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceList1.getActive(), priceList2.getActive())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("author", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceList1.getAuthor(), priceList2.getAuthor())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"catalogBasePriceList", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceList1.getCatalogBasePriceList(),
						priceList2.getCatalogBasePriceList())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("catalogId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceList1.getCatalogId(), priceList2.getCatalogId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("catalogName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceList1.getCatalogName(),
						priceList2.getCatalogName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceList1.getCreateDate(),
						priceList2.getCreateDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("currencyCode", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceList1.getCurrencyCode(),
						priceList2.getCurrencyCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"currencyExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceList1.getCurrencyExternalReferenceCode(),
						priceList2.getCurrencyExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("currencyId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceList1.getCurrencyId(),
						priceList2.getCurrencyId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!equals(
						(Map)priceList1.getCustomFields(),
						(Map)priceList2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("displayDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceList1.getDisplayDate(),
						priceList2.getDisplayDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("expirationDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceList1.getExpirationDate(),
						priceList2.getExpirationDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceList1.getExternalReferenceCode(),
						priceList2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceList1.getId(), priceList2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceList1.getName(), priceList2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("netPrice", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceList1.getNetPrice(), priceList2.getNetPrice())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("neverExpire", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceList1.getNeverExpire(),
						priceList2.getNeverExpire())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"parentPriceListId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceList1.getParentPriceListId(),
						priceList2.getParentPriceListId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priceEntries", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceList1.getPriceEntries(),
						priceList2.getPriceEntries())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"priceListAccountGroups", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceList1.getPriceListAccountGroups(),
						priceList2.getPriceListAccountGroups())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"priceListAccounts", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceList1.getPriceListAccounts(),
						priceList2.getPriceListAccounts())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"priceListChannels", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceList1.getPriceListChannels(),
						priceList2.getPriceListChannels())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"priceListDiscounts", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceList1.getPriceListDiscounts(),
						priceList2.getPriceListDiscounts())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"priceListOrderTypes", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceList1.getPriceListOrderTypes(),
						priceList2.getPriceListOrderTypes())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priceModifiers", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceList1.getPriceModifiers(),
						priceList2.getPriceModifiers())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceList1.getPriority(), priceList2.getPriority())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceList1.getType(), priceList2.getType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowStatusInfo", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceList1.getWorkflowStatusInfo(),
						priceList2.getWorkflowStatusInfo())) {

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

		if (!(_priceListResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_priceListResource;

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
		EntityField entityField, String operator, PriceList priceList) {

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

		if (entityFieldName.equals("author")) {
			Object object = priceList.getAuthor();

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

		if (entityFieldName.equals("catalogBasePriceList")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("catalogId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("catalogName")) {
			Object object = priceList.getCatalogName();

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
				Date date = priceList.getCreateDate();

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

				sb.append(_format.format(priceList.getCreateDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("currencyCode")) {
			Object object = priceList.getCurrencyCode();

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
			Object object = priceList.getCurrencyExternalReferenceCode();

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

		if (entityFieldName.equals("displayDate")) {
			if (operator.equals("between")) {
				Date date = priceList.getDisplayDate();

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

				sb.append(_format.format(priceList.getDisplayDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("expirationDate")) {
			if (operator.equals("between")) {
				Date date = priceList.getExpirationDate();

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

				sb.append(_format.format(priceList.getExpirationDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = priceList.getExternalReferenceCode();

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

		if (entityFieldName.equals("name")) {
			Object object = priceList.getName();

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

		if (entityFieldName.equals("netPrice")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("neverExpire")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("parentPriceListId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priceEntries")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priceListAccountGroups")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priceListAccounts")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priceListChannels")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priceListDiscounts")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priceListOrderTypes")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priceModifiers")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priority")) {
			sb.append(String.valueOf(priceList.getPriority()));

			return sb.toString();
		}

		if (entityFieldName.equals("type")) {
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

	protected PriceList randomPriceList() throws Exception {
		return new PriceList() {
			{
				active = RandomTestUtil.randomBoolean();
				author = StringUtil.toLowerCase(RandomTestUtil.randomString());
				catalogBasePriceList = RandomTestUtil.randomBoolean();
				catalogId = RandomTestUtil.randomLong();
				catalogName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				createDate = RandomTestUtil.nextDate();
				currencyCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				currencyExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				currencyId = RandomTestUtil.randomLong();
				displayDate = RandomTestUtil.nextDate();
				expirationDate = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				netPrice = RandomTestUtil.randomBoolean();
				neverExpire = RandomTestUtil.randomBoolean();
				parentPriceListId = RandomTestUtil.randomLong();
				priority = RandomTestUtil.randomDouble();
			}
		};
	}

	protected PriceList randomIrrelevantPriceList() throws Exception {
		PriceList randomIrrelevantPriceList = randomPriceList();

		return randomIrrelevantPriceList;
	}

	protected PriceList randomPatchPriceList() throws Exception {
		return randomPriceList();
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

	protected PriceListResource priceListResource;
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
		LogFactoryUtil.getLog(BasePriceListResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.commerce.admin.pricing.resource.v2_0.
			PriceListResource _priceListResource;

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