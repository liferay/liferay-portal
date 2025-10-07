/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.resource.v1_0.test;

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
import com.liferay.headless.commerce.admin.pricing.client.dto.v1_0.PriceEntry;
import com.liferay.headless.commerce.admin.pricing.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Page;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.pricing.client.resource.v1_0.PriceEntryResource;
import com.liferay.headless.commerce.admin.pricing.client.serdes.v1_0.PriceEntrySerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
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
public abstract class BasePriceEntryResourceTestCase {

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

		_priceEntryResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		priceEntryResource = PriceEntryResource.builder(
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

		PriceEntry priceEntry1 = randomPriceEntry();

		String json = objectMapper.writeValueAsString(priceEntry1);

		PriceEntry priceEntry2 = PriceEntrySerDes.toDTO(json);

		Assert.assertTrue(equals(priceEntry1, priceEntry2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		PriceEntry priceEntry = randomPriceEntry();

		String json1 = objectMapper.writeValueAsString(priceEntry);
		String json2 = PriceEntrySerDes.toJSON(priceEntry);

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

		PriceEntry priceEntry = randomPriceEntry();

		priceEntry.setExternalReferenceCode(regex);
		priceEntry.setPriceListExternalReferenceCode(regex);
		priceEntry.setSku(regex);
		priceEntry.setSkuExternalReferenceCode(regex);

		String json = PriceEntrySerDes.toJSON(priceEntry);

		Assert.assertFalse(json.contains(regex));

		priceEntry = PriceEntrySerDes.toDTO(json);

		Assert.assertEquals(regex, priceEntry.getExternalReferenceCode());
		Assert.assertEquals(
			regex, priceEntry.getPriceListExternalReferenceCode());
		Assert.assertEquals(regex, priceEntry.getSku());
		Assert.assertEquals(regex, priceEntry.getSkuExternalReferenceCode());
	}

	@Test
	public void testDeletePriceEntry() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		PriceEntry priceEntry = testDeletePriceEntry_addPriceEntry();

		assertHttpResponseStatusCode(
			204,
			priceEntryResource.deletePriceEntryHttpResponse(
				priceEntry.getId()));

		assertHttpResponseStatusCode(
			404,
			priceEntryResource.getPriceEntryHttpResponse(priceEntry.getId()));
		assertHttpResponseStatusCode(
			404, priceEntryResource.getPriceEntryHttpResponse(0L));
	}

	protected PriceEntry testDeletePriceEntry_addPriceEntry() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeletePriceEntry() throws Exception {

		// No namespace

		PriceEntry priceEntry1 = testGraphQLDeletePriceEntry_addPriceEntry();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deletePriceEntry",
						new HashMap<String, Object>() {
							{
								put("id", priceEntry1.getId());
							}
						})),
				"JSONObject/data", "Object/deletePriceEntry"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"priceEntry",
					new HashMap<String, Object>() {
						{
							put("id", priceEntry1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminPricing_v1_0

		PriceEntry priceEntry2 = testGraphQLDeletePriceEntry_addPriceEntry();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminPricing_v1_0",
						new GraphQLField(
							"deletePriceEntry",
							new HashMap<String, Object>() {
								{
									put("id", priceEntry2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminPricing_v1_0",
				"Object/deletePriceEntry"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminPricing_v1_0",
					new GraphQLField(
						"priceEntry",
						new HashMap<String, Object>() {
							{
								put("id", priceEntry2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected PriceEntry testGraphQLDeletePriceEntry_addPriceEntry()
		throws Exception {

		return testGraphQLPriceEntry_addPriceEntry();
	}

	@Test
	public void testDeletePriceEntryBatch() throws Exception {
		PriceEntry priceEntry1 = testDeletePriceEntryBatch_addPriceEntry();

		testDeletePriceEntryBatch_deletePriceEntry(
			202, priceEntry1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			priceEntryResource.getPriceEntryHttpResponse(priceEntry1.getId()));

		priceEntry1 = testDeletePriceEntryBatch_addPriceEntry();

		testDeletePriceEntryBatch_deletePriceEntry(
			202, null, priceEntry1.getId());

		assertHttpResponseStatusCode(
			404,
			priceEntryResource.getPriceEntryHttpResponse(priceEntry1.getId()));

		priceEntry1 = testDeletePriceEntryBatch_addPriceEntry();
		PriceEntry priceEntry2 = testDeletePriceEntryBatch_addPriceEntry();

		testDeletePriceEntryBatch_deletePriceEntry(
			202, priceEntry2.getExternalReferenceCode(), priceEntry1.getId());

		assertHttpResponseStatusCode(
			404,
			priceEntryResource.getPriceEntryHttpResponse(priceEntry1.getId()));
		assertHttpResponseStatusCode(
			200,
			priceEntryResource.getPriceEntryHttpResponse(priceEntry2.getId()));

		testDeletePriceEntryBatch_deletePriceEntry(
			202, priceEntry2.getExternalReferenceCode(), priceEntry1.getId());

		assertHttpResponseStatusCode(
			404,
			priceEntryResource.getPriceEntryHttpResponse(priceEntry2.getId()));
	}

	protected PriceEntry testDeletePriceEntryBatch_addPriceEntry()
		throws Exception {

		return testDeletePriceEntry_addPriceEntry();
	}

	protected void testDeletePriceEntryBatch_deletePriceEntry(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			priceEntryResource.deletePriceEntryBatchHttpResponse(
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
	public void testDeletePriceEntryByExternalReferenceCode() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		PriceEntry priceEntry =
			testDeletePriceEntryByExternalReferenceCode_addPriceEntry();

		assertHttpResponseStatusCode(
			204,
			priceEntryResource.
				deletePriceEntryByExternalReferenceCodeHttpResponse(
					priceEntry.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			priceEntryResource.getPriceEntryByExternalReferenceCodeHttpResponse(
				priceEntry.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			priceEntryResource.getPriceEntryByExternalReferenceCodeHttpResponse(
				"-"));
	}

	protected PriceEntry
			testDeletePriceEntryByExternalReferenceCode_addPriceEntry()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeletePriceEntryByExternalReferenceCode()
		throws Exception {

		// No namespace

		PriceEntry priceEntry1 =
			testGraphQLDeletePriceEntryByExternalReferenceCode_addPriceEntry();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deletePriceEntryByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										priceEntry1.getExternalReferenceCode() +
											"\"");
							}
						})),
				"JSONObject/data",
				"Object/deletePriceEntryByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"priceEntryByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + priceEntry1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminPricing_v1_0

		PriceEntry priceEntry2 =
			testGraphQLDeletePriceEntryByExternalReferenceCode_addPriceEntry();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminPricing_v1_0",
						new GraphQLField(
							"deletePriceEntryByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											priceEntry2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminPricing_v1_0",
				"Object/deletePriceEntryByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminPricing_v1_0",
					new GraphQLField(
						"priceEntryByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										priceEntry2.getExternalReferenceCode() +
											"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected PriceEntry
			testGraphQLDeletePriceEntryByExternalReferenceCode_addPriceEntry()
		throws Exception {

		return testGraphQLPriceEntry_addPriceEntry();
	}

	@Test
	public void testGetPriceEntry() throws Exception {
		PriceEntry postPriceEntry = testGetPriceEntry_addPriceEntry();

		PriceEntry getPriceEntry = priceEntryResource.getPriceEntry(
			postPriceEntry.getId());

		assertEquals(postPriceEntry, getPriceEntry);
		assertValid(getPriceEntry);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		PriceEntry postPriceEntry = testGetPriceEntry_addPriceEntry();

		PriceEntry getPriceEntry = priceEntryResource.getPriceEntry(
			postPriceEntry.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.pricing.dto.v1_0.PriceEntry"
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

		Object item = vulcanCRUDItemDelegate.getItem(postPriceEntry.getId());

		assertEquals(getPriceEntry, PriceEntrySerDes.toDTO(item.toString()));
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

	protected PriceEntry testGetPriceEntry_addPriceEntry() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetPriceEntry() throws Exception {
		PriceEntry priceEntry = testGraphQLGetPriceEntry_addPriceEntry();

		// No namespace

		Assert.assertTrue(
			equals(
				priceEntry,
				PriceEntrySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"priceEntry",
								new HashMap<String, Object>() {
									{
										put("id", priceEntry.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/priceEntry"))));

		// Using the namespace headlessCommerceAdminPricing_v1_0

		Assert.assertTrue(
			equals(
				priceEntry,
				PriceEntrySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminPricing_v1_0",
								new GraphQLField(
									"priceEntry",
									new HashMap<String, Object>() {
										{
											put("id", priceEntry.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminPricing_v1_0",
						"Object/priceEntry"))));
	}

	@Test
	public void testGraphQLGetPriceEntryNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"priceEntry",
						new HashMap<String, Object>() {
							{
								put("id", irrelevantId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminPricing_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminPricing_v1_0",
						new GraphQLField(
							"priceEntry",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected PriceEntry testGraphQLGetPriceEntry_addPriceEntry()
		throws Exception {

		return testGraphQLPriceEntry_addPriceEntry();
	}

	@Test
	public void testGetPriceEntryByExternalReferenceCode() throws Exception {
		PriceEntry postPriceEntry =
			testGetPriceEntryByExternalReferenceCode_addPriceEntry();

		PriceEntry getPriceEntry =
			priceEntryResource.getPriceEntryByExternalReferenceCode(
				postPriceEntry.getExternalReferenceCode());

		assertEquals(postPriceEntry, getPriceEntry);
		assertValid(getPriceEntry);
	}

	protected PriceEntry
			testGetPriceEntryByExternalReferenceCode_addPriceEntry()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetPriceEntryByExternalReferenceCode()
		throws Exception {

		PriceEntry priceEntry =
			testGraphQLGetPriceEntryByExternalReferenceCode_addPriceEntry();

		// No namespace

		Assert.assertTrue(
			equals(
				priceEntry,
				PriceEntrySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"priceEntryByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												priceEntry.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/priceEntryByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminPricing_v1_0

		Assert.assertTrue(
			equals(
				priceEntry,
				PriceEntrySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminPricing_v1_0",
								new GraphQLField(
									"priceEntryByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													priceEntry.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminPricing_v1_0",
						"Object/priceEntryByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetPriceEntryByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"priceEntryByExternalReferenceCode",
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

		// Using the namespace headlessCommerceAdminPricing_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminPricing_v1_0",
						new GraphQLField(
							"priceEntryByExternalReferenceCode",
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

	protected PriceEntry
			testGraphQLGetPriceEntryByExternalReferenceCode_addPriceEntry()
		throws Exception {

		return testGraphQLPriceEntry_addPriceEntry();
	}

	@Test
	public void testGetPriceListByExternalReferenceCodePriceEntriesPage()
		throws Exception {

		String externalReferenceCode =
			testGetPriceListByExternalReferenceCodePriceEntriesPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetPriceListByExternalReferenceCodePriceEntriesPage_getIrrelevantExternalReferenceCode();

		Page<PriceEntry> page =
			priceEntryResource.
				getPriceListByExternalReferenceCodePriceEntriesPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			PriceEntry irrelevantPriceEntry =
				testGetPriceListByExternalReferenceCodePriceEntriesPage_addPriceEntry(
					irrelevantExternalReferenceCode,
					randomIrrelevantPriceEntry());

			page =
				priceEntryResource.
					getPriceListByExternalReferenceCodePriceEntriesPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPriceEntry, (List<PriceEntry>)page.getItems());
			assertValid(
				page,
				testGetPriceListByExternalReferenceCodePriceEntriesPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		PriceEntry priceEntry1 =
			testGetPriceListByExternalReferenceCodePriceEntriesPage_addPriceEntry(
				externalReferenceCode, randomPriceEntry());

		PriceEntry priceEntry2 =
			testGetPriceListByExternalReferenceCodePriceEntriesPage_addPriceEntry(
				externalReferenceCode, randomPriceEntry());

		page =
			priceEntryResource.
				getPriceListByExternalReferenceCodePriceEntriesPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(priceEntry1, (List<PriceEntry>)page.getItems());
		assertContains(priceEntry2, (List<PriceEntry>)page.getItems());
		assertValid(
			page,
			testGetPriceListByExternalReferenceCodePriceEntriesPage_getExpectedActions(
				externalReferenceCode));

		priceEntryResource.deletePriceEntry(priceEntry1.getId());

		priceEntryResource.deletePriceEntry(priceEntry2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetPriceListByExternalReferenceCodePriceEntriesPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetPriceListByExternalReferenceCodePriceEntriesPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetPriceListByExternalReferenceCodePriceEntriesPage_getExternalReferenceCode();

		Page<PriceEntry> priceEntriesPage =
			priceEntryResource.
				getPriceListByExternalReferenceCodePriceEntriesPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			priceEntriesPage.getTotalCount());

		PriceEntry priceEntry1 =
			testGetPriceListByExternalReferenceCodePriceEntriesPage_addPriceEntry(
				externalReferenceCode, randomPriceEntry());

		PriceEntry priceEntry2 =
			testGetPriceListByExternalReferenceCodePriceEntriesPage_addPriceEntry(
				externalReferenceCode, randomPriceEntry());

		PriceEntry priceEntry3 =
			testGetPriceListByExternalReferenceCodePriceEntriesPage_addPriceEntry(
				externalReferenceCode, randomPriceEntry());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<PriceEntry> page1 =
				priceEntryResource.
					getPriceListByExternalReferenceCodePriceEntriesPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(priceEntry1, (List<PriceEntry>)page1.getItems());

			Page<PriceEntry> page2 =
				priceEntryResource.
					getPriceListByExternalReferenceCodePriceEntriesPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(priceEntry2, (List<PriceEntry>)page2.getItems());

			Page<PriceEntry> page3 =
				priceEntryResource.
					getPriceListByExternalReferenceCodePriceEntriesPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(priceEntry3, (List<PriceEntry>)page3.getItems());
		}
		else {
			Page<PriceEntry> page1 =
				priceEntryResource.
					getPriceListByExternalReferenceCodePriceEntriesPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<PriceEntry> priceEntries1 = (List<PriceEntry>)page1.getItems();

			Assert.assertEquals(
				priceEntries1.toString(), totalCount + 2, priceEntries1.size());

			Page<PriceEntry> page2 =
				priceEntryResource.
					getPriceListByExternalReferenceCodePriceEntriesPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PriceEntry> priceEntries2 = (List<PriceEntry>)page2.getItems();

			Assert.assertEquals(
				priceEntries2.toString(), 1, priceEntries2.size());

			Page<PriceEntry> page3 =
				priceEntryResource.
					getPriceListByExternalReferenceCodePriceEntriesPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(priceEntry1, (List<PriceEntry>)page3.getItems());
			assertContains(priceEntry2, (List<PriceEntry>)page3.getItems());
			assertContains(priceEntry3, (List<PriceEntry>)page3.getItems());
		}
	}

	protected PriceEntry
			testGetPriceListByExternalReferenceCodePriceEntriesPage_addPriceEntry(
				String externalReferenceCode, PriceEntry priceEntry)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetPriceListByExternalReferenceCodePriceEntriesPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetPriceListByExternalReferenceCodePriceEntriesPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetPriceListIdPriceEntriesPage() throws Exception {
		Long id = testGetPriceListIdPriceEntriesPage_getId();
		Long irrelevantId =
			testGetPriceListIdPriceEntriesPage_getIrrelevantId();

		Page<PriceEntry> page =
			priceEntryResource.getPriceListIdPriceEntriesPage(
				id, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			PriceEntry irrelevantPriceEntry =
				testGetPriceListIdPriceEntriesPage_addPriceEntry(
					irrelevantId, randomIrrelevantPriceEntry());

			page = priceEntryResource.getPriceListIdPriceEntriesPage(
				irrelevantId, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPriceEntry, (List<PriceEntry>)page.getItems());
			assertValid(
				page,
				testGetPriceListIdPriceEntriesPage_getExpectedActions(
					irrelevantId));
		}

		PriceEntry priceEntry1 =
			testGetPriceListIdPriceEntriesPage_addPriceEntry(
				id, randomPriceEntry());

		PriceEntry priceEntry2 =
			testGetPriceListIdPriceEntriesPage_addPriceEntry(
				id, randomPriceEntry());

		page = priceEntryResource.getPriceListIdPriceEntriesPage(
			id, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(priceEntry1, (List<PriceEntry>)page.getItems());
		assertContains(priceEntry2, (List<PriceEntry>)page.getItems());
		assertValid(
			page, testGetPriceListIdPriceEntriesPage_getExpectedActions(id));

		priceEntryResource.deletePriceEntry(priceEntry1.getId());

		priceEntryResource.deletePriceEntry(priceEntry2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetPriceListIdPriceEntriesPage_getExpectedActions(Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetPriceListIdPriceEntriesPageWithPagination()
		throws Exception {

		Long id = testGetPriceListIdPriceEntriesPage_getId();

		Page<PriceEntry> priceEntriesPage =
			priceEntryResource.getPriceListIdPriceEntriesPage(id, null);

		int totalCount = GetterUtil.getInteger(
			priceEntriesPage.getTotalCount());

		PriceEntry priceEntry1 =
			testGetPriceListIdPriceEntriesPage_addPriceEntry(
				id, randomPriceEntry());

		PriceEntry priceEntry2 =
			testGetPriceListIdPriceEntriesPage_addPriceEntry(
				id, randomPriceEntry());

		PriceEntry priceEntry3 =
			testGetPriceListIdPriceEntriesPage_addPriceEntry(
				id, randomPriceEntry());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<PriceEntry> page1 =
				priceEntryResource.getPriceListIdPriceEntriesPage(
					id,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(priceEntry1, (List<PriceEntry>)page1.getItems());

			Page<PriceEntry> page2 =
				priceEntryResource.getPriceListIdPriceEntriesPage(
					id,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(priceEntry2, (List<PriceEntry>)page2.getItems());

			Page<PriceEntry> page3 =
				priceEntryResource.getPriceListIdPriceEntriesPage(
					id,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(priceEntry3, (List<PriceEntry>)page3.getItems());
		}
		else {
			Page<PriceEntry> page1 =
				priceEntryResource.getPriceListIdPriceEntriesPage(
					id, Pagination.of(1, totalCount + 2));

			List<PriceEntry> priceEntries1 = (List<PriceEntry>)page1.getItems();

			Assert.assertEquals(
				priceEntries1.toString(), totalCount + 2, priceEntries1.size());

			Page<PriceEntry> page2 =
				priceEntryResource.getPriceListIdPriceEntriesPage(
					id, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PriceEntry> priceEntries2 = (List<PriceEntry>)page2.getItems();

			Assert.assertEquals(
				priceEntries2.toString(), 1, priceEntries2.size());

			Page<PriceEntry> page3 =
				priceEntryResource.getPriceListIdPriceEntriesPage(
					id, Pagination.of(1, (int)totalCount + 3));

			assertContains(priceEntry1, (List<PriceEntry>)page3.getItems());
			assertContains(priceEntry2, (List<PriceEntry>)page3.getItems());
			assertContains(priceEntry3, (List<PriceEntry>)page3.getItems());
		}
	}

	protected PriceEntry testGetPriceListIdPriceEntriesPage_addPriceEntry(
			Long id, PriceEntry priceEntry)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetPriceListIdPriceEntriesPage_getId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetPriceListIdPriceEntriesPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPatchPriceEntry() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPatchPriceEntryByExternalReferenceCode() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPostPriceListByExternalReferenceCodePriceEntry()
		throws Exception {

		PriceEntry randomPriceEntry = randomPriceEntry();

		PriceEntry postPriceEntry =
			testPostPriceListByExternalReferenceCodePriceEntry_addPriceEntry(
				randomPriceEntry);

		assertEquals(randomPriceEntry, postPriceEntry);
		assertValid(postPriceEntry);
	}

	protected PriceEntry
			testPostPriceListByExternalReferenceCodePriceEntry_addPriceEntry(
				PriceEntry priceEntry)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostPriceListIdPriceEntry() throws Exception {
		PriceEntry randomPriceEntry = randomPriceEntry();

		PriceEntry postPriceEntry = testPostPriceListIdPriceEntry_addPriceEntry(
			randomPriceEntry);

		assertEquals(randomPriceEntry, postPriceEntry);
		assertValid(postPriceEntry);
	}

	protected PriceEntry testPostPriceListIdPriceEntry_addPriceEntry(
			PriceEntry priceEntry)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		PriceEntry priceEntry1 =
			testBatchEngineDeleteImportTask_addPriceEntry();

		testBatchEngineDeleteImportTask_deletePriceEntry(
			200, priceEntry1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			priceEntryResource.getPriceEntryHttpResponse(priceEntry1.getId()));

		priceEntry1 = testBatchEngineDeleteImportTask_addPriceEntry();

		testBatchEngineDeleteImportTask_deletePriceEntry(
			200, null, priceEntry1.getId());

		assertHttpResponseStatusCode(
			404,
			priceEntryResource.getPriceEntryHttpResponse(priceEntry1.getId()));

		priceEntry1 = testBatchEngineDeleteImportTask_addPriceEntry();
		PriceEntry priceEntry2 =
			testBatchEngineDeleteImportTask_addPriceEntry();

		testBatchEngineDeleteImportTask_deletePriceEntry(
			200, priceEntry2.getExternalReferenceCode(), priceEntry1.getId());

		assertHttpResponseStatusCode(
			404,
			priceEntryResource.getPriceEntryHttpResponse(priceEntry1.getId()));
		assertHttpResponseStatusCode(
			200,
			priceEntryResource.getPriceEntryHttpResponse(priceEntry2.getId()));

		testBatchEngineDeleteImportTask_deletePriceEntry(
			200, priceEntry2.getExternalReferenceCode(), priceEntry1.getId());

		assertHttpResponseStatusCode(
			404,
			priceEntryResource.getPriceEntryHttpResponse(priceEntry2.getId()));
	}

	protected PriceEntry testBatchEngineDeleteImportTask_addPriceEntry()
		throws Exception {

		return testDeletePriceEntry_addPriceEntry();
	}

	protected void testBatchEngineDeleteImportTask_deletePriceEntry(
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
				"com.liferay.headless.commerce.admin.pricing.dto.v1_0.PriceEntry",
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

	protected PriceEntry testGraphQLPriceEntry_addPriceEntry()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		PriceEntry priceEntry, List<PriceEntry> priceEntries) {

		boolean contains = false;

		for (PriceEntry item : priceEntries) {
			if (equals(priceEntry, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			priceEntries + " does not contain " + priceEntry, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		PriceEntry priceEntry1, PriceEntry priceEntry2) {

		Assert.assertTrue(
			priceEntry1 + " does not equal " + priceEntry2,
			equals(priceEntry1, priceEntry2));
	}

	protected void assertEquals(
		List<PriceEntry> priceEntries1, List<PriceEntry> priceEntries2) {

		Assert.assertEquals(priceEntries1.size(), priceEntries2.size());

		for (int i = 0; i < priceEntries1.size(); i++) {
			PriceEntry priceEntry1 = priceEntries1.get(i);
			PriceEntry priceEntry2 = priceEntries2.get(i);

			assertEquals(priceEntry1, priceEntry2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<PriceEntry> priceEntries1, List<PriceEntry> priceEntries2) {

		Assert.assertEquals(priceEntries1.size(), priceEntries2.size());

		for (PriceEntry priceEntry1 : priceEntries1) {
			boolean contains = false;

			for (PriceEntry priceEntry2 : priceEntries2) {
				if (equals(priceEntry1, priceEntry2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				priceEntries2 + " does not contain " + priceEntry1, contains);
		}
	}

	protected void assertValid(PriceEntry priceEntry) throws Exception {
		boolean valid = true;

		if (priceEntry.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (priceEntry.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (priceEntry.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("hasTierPrice", additionalAssertFieldName)) {
				if (priceEntry.getHasTierPrice() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("price", additionalAssertFieldName)) {
				if (priceEntry.getPrice() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"priceListExternalReferenceCode",
					additionalAssertFieldName)) {

				if (priceEntry.getPriceListExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("priceListId", additionalAssertFieldName)) {
				if (priceEntry.getPriceListId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("promoPrice", additionalAssertFieldName)) {
				if (priceEntry.getPromoPrice() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("sku", additionalAssertFieldName)) {
				if (priceEntry.getSku() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"skuExternalReferenceCode", additionalAssertFieldName)) {

				if (priceEntry.getSkuExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("skuId", additionalAssertFieldName)) {
				if (priceEntry.getSkuId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("tierPrices", additionalAssertFieldName)) {
				if (priceEntry.getTierPrices() == null) {
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

	protected void assertValid(Page<PriceEntry> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<PriceEntry> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<PriceEntry> priceEntries = page.getItems();

		int size = priceEntries.size();

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
					com.liferay.headless.commerce.admin.pricing.dto.v1_0.
						PriceEntry.class)) {

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

	protected boolean equals(PriceEntry priceEntry1, PriceEntry priceEntry2) {
		if (priceEntry1 == priceEntry2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!equals(
						(Map)priceEntry1.getCustomFields(),
						(Map)priceEntry2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceEntry1.getExternalReferenceCode(),
						priceEntry2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("hasTierPrice", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceEntry1.getHasTierPrice(),
						priceEntry2.getHasTierPrice())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceEntry1.getId(), priceEntry2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("price", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceEntry1.getPrice(), priceEntry2.getPrice())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"priceListExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceEntry1.getPriceListExternalReferenceCode(),
						priceEntry2.getPriceListExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priceListId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceEntry1.getPriceListId(),
						priceEntry2.getPriceListId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("promoPrice", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceEntry1.getPromoPrice(),
						priceEntry2.getPromoPrice())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("sku", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceEntry1.getSku(), priceEntry2.getSku())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"skuExternalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceEntry1.getSkuExternalReferenceCode(),
						priceEntry2.getSkuExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("skuId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceEntry1.getSkuId(), priceEntry2.getSkuId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("tierPrices", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceEntry1.getTierPrices(),
						priceEntry2.getTierPrices())) {

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

		if (!(_priceEntryResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_priceEntryResource;

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
		EntityField entityField, String operator, PriceEntry priceEntry) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("customFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = priceEntry.getExternalReferenceCode();

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

		if (entityFieldName.equals("hasTierPrice")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("price")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priceListExternalReferenceCode")) {
			Object object = priceEntry.getPriceListExternalReferenceCode();

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

		if (entityFieldName.equals("priceListId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("promoPrice")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("sku")) {
			Object object = priceEntry.getSku();

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

		if (entityFieldName.equals("skuExternalReferenceCode")) {
			Object object = priceEntry.getSkuExternalReferenceCode();

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

		if (entityFieldName.equals("skuId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("tierPrices")) {
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

	protected PriceEntry randomPriceEntry() throws Exception {
		return new PriceEntry() {
			{
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				hasTierPrice = RandomTestUtil.randomBoolean();
				id = RandomTestUtil.randomLong();
				priceListExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				priceListId = RandomTestUtil.randomLong();
				sku = StringUtil.toLowerCase(RandomTestUtil.randomString());
				skuExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				skuId = RandomTestUtil.randomLong();
			}
		};
	}

	protected PriceEntry randomIrrelevantPriceEntry() throws Exception {
		PriceEntry randomIrrelevantPriceEntry = randomPriceEntry();

		return randomIrrelevantPriceEntry;
	}

	protected PriceEntry randomPatchPriceEntry() throws Exception {
		return randomPriceEntry();
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

	protected PriceEntryResource priceEntryResource;
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
		LogFactoryUtil.getLog(BasePriceEntryResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.commerce.admin.pricing.resource.v1_0.
			PriceEntryResource _priceEntryResource;

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