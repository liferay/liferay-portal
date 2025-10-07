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
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceEntry;
import com.liferay.headless.commerce.admin.pricing.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Page;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.pricing.client.resource.v2_0.PriceEntryResource;
import com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0.PriceEntrySerDes;
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

		priceEntry.setDiscountLevelsFormatted(regex);
		priceEntry.setExternalReferenceCode(regex);
		priceEntry.setPriceFormatted(regex);
		priceEntry.setPriceListExternalReferenceCode(regex);
		priceEntry.setSkuExternalReferenceCode(regex);
		priceEntry.setUnitOfMeasureKey(regex);

		String json = PriceEntrySerDes.toJSON(priceEntry);

		Assert.assertFalse(json.contains(regex));

		priceEntry = PriceEntrySerDes.toDTO(json);

		Assert.assertEquals(regex, priceEntry.getDiscountLevelsFormatted());
		Assert.assertEquals(regex, priceEntry.getExternalReferenceCode());
		Assert.assertEquals(regex, priceEntry.getPriceFormatted());
		Assert.assertEquals(
			regex, priceEntry.getPriceListExternalReferenceCode());
		Assert.assertEquals(regex, priceEntry.getSkuExternalReferenceCode());
		Assert.assertEquals(regex, priceEntry.getUnitOfMeasureKey());
	}

	@Test
	public void testDeletePriceEntry() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		PriceEntry priceEntry = testDeletePriceEntry_addPriceEntry();

		assertHttpResponseStatusCode(
			204,
			priceEntryResource.deletePriceEntryHttpResponse(
				priceEntry.getPriceEntryId()));

		assertHttpResponseStatusCode(
			404,
			priceEntryResource.getPriceEntryHttpResponse(
				priceEntry.getPriceEntryId()));
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
								put(
									"priceEntryId",
									priceEntry1.getPriceEntryId());
							}
						})),
				"JSONObject/data", "Object/deletePriceEntry"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"priceEntry",
					new HashMap<String, Object>() {
						{
							put("priceEntryId", priceEntry1.getPriceEntryId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminPricing_v2_0

		PriceEntry priceEntry2 = testGraphQLDeletePriceEntry_addPriceEntry();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminPricing_v2_0",
						new GraphQLField(
							"deletePriceEntry",
							new HashMap<String, Object>() {
								{
									put(
										"priceEntryId",
										priceEntry2.getPriceEntryId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminPricing_v2_0",
				"Object/deletePriceEntry"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminPricing_v2_0",
					new GraphQLField(
						"priceEntry",
						new HashMap<String, Object>() {
							{
								put(
									"priceEntryId",
									priceEntry2.getPriceEntryId());
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
			priceEntryResource.getPriceEntryHttpResponse(
				priceEntry1.getPriceEntryId()));

		priceEntry1 = testDeletePriceEntryBatch_addPriceEntry();

		testDeletePriceEntryBatch_deletePriceEntry(
			202, null, priceEntry1.getPriceEntryId());

		assertHttpResponseStatusCode(
			404,
			priceEntryResource.getPriceEntryHttpResponse(
				priceEntry1.getPriceEntryId()));

		priceEntry1 = testDeletePriceEntryBatch_addPriceEntry();
		PriceEntry priceEntry2 = testDeletePriceEntryBatch_addPriceEntry();

		testDeletePriceEntryBatch_deletePriceEntry(
			202, priceEntry2.getExternalReferenceCode(),
			priceEntry1.getPriceEntryId());

		assertHttpResponseStatusCode(
			404,
			priceEntryResource.getPriceEntryHttpResponse(
				priceEntry1.getPriceEntryId()));
		assertHttpResponseStatusCode(
			200,
			priceEntryResource.getPriceEntryHttpResponse(
				priceEntry2.getPriceEntryId()));

		testDeletePriceEntryBatch_deletePriceEntry(
			202, priceEntry2.getExternalReferenceCode(),
			priceEntry1.getPriceEntryId());

		assertHttpResponseStatusCode(
			404,
			priceEntryResource.getPriceEntryHttpResponse(
				priceEntry2.getPriceEntryId()));
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
						"priceEntryId", () -> id
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

		// Using the namespace headlessCommerceAdminPricing_v2_0

		PriceEntry priceEntry2 =
			testGraphQLDeletePriceEntryByExternalReferenceCode_addPriceEntry();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminPricing_v2_0",
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
				"JSONObject/headlessCommerceAdminPricing_v2_0",
				"Object/deletePriceEntryByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminPricing_v2_0",
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
			postPriceEntry.getPriceEntryId());

		assertEquals(postPriceEntry, getPriceEntry);
		assertValid(getPriceEntry);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		PriceEntry postPriceEntry = testGetPriceEntry_addPriceEntry();

		PriceEntry getPriceEntry = priceEntryResource.getPriceEntry(
			postPriceEntry.getPriceEntryId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceEntry"
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

		Object item = vulcanCRUDItemDelegate.getItem(
			postPriceEntry.getPriceEntryId());

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
										put(
											"priceEntryId",
											priceEntry.getPriceEntryId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/priceEntry"))));

		// Using the namespace headlessCommerceAdminPricing_v2_0

		Assert.assertTrue(
			equals(
				priceEntry,
				PriceEntrySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminPricing_v2_0",
								new GraphQLField(
									"priceEntry",
									new HashMap<String, Object>() {
										{
											put(
												"priceEntryId",
												priceEntry.getPriceEntryId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminPricing_v2_0",
						"Object/priceEntry"))));
	}

	@Test
	public void testGraphQLGetPriceEntryNotFound() throws Exception {
		Long irrelevantPriceEntryId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"priceEntry",
						new HashMap<String, Object>() {
							{
								put("priceEntryId", irrelevantPriceEntryId);
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
							"priceEntry",
							new HashMap<String, Object>() {
								{
									put("priceEntryId", irrelevantPriceEntryId);
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

		// Using the namespace headlessCommerceAdminPricing_v2_0

		Assert.assertTrue(
			equals(
				priceEntry,
				PriceEntrySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminPricing_v2_0",
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
						"JSONObject/headlessCommerceAdminPricing_v2_0",
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

		// Using the namespace headlessCommerceAdminPricing_v2_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminPricing_v2_0",
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
					externalReferenceCode, null, null, Pagination.of(1, 10),
					null);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			PriceEntry irrelevantPriceEntry =
				testGetPriceListByExternalReferenceCodePriceEntriesPage_addPriceEntry(
					irrelevantExternalReferenceCode,
					randomIrrelevantPriceEntry());

			page =
				priceEntryResource.
					getPriceListByExternalReferenceCodePriceEntriesPage(
						irrelevantExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

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
					externalReferenceCode, null, null, Pagination.of(1, 10),
					null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(priceEntry1, (List<PriceEntry>)page.getItems());
		assertContains(priceEntry2, (List<PriceEntry>)page.getItems());
		assertValid(
			page,
			testGetPriceListByExternalReferenceCodePriceEntriesPage_getExpectedActions(
				externalReferenceCode));

		priceEntryResource.deletePriceEntry(priceEntry1.getPriceEntryId());

		priceEntryResource.deletePriceEntry(priceEntry2.getPriceEntryId());
	}

	protected Map<String, Map<String, String>>
			testGetPriceListByExternalReferenceCodePriceEntriesPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetPriceListByExternalReferenceCodePriceEntriesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetPriceListByExternalReferenceCodePriceEntriesPage_getExternalReferenceCode();

		PriceEntry priceEntry1 = randomPriceEntry();

		priceEntry1 =
			testGetPriceListByExternalReferenceCodePriceEntriesPage_addPriceEntry(
				externalReferenceCode, priceEntry1);

		for (EntityField entityField : entityFields) {
			Page<PriceEntry> page =
				priceEntryResource.
					getPriceListByExternalReferenceCodePriceEntriesPage(
						externalReferenceCode, null,
						getFilterString(entityField, "between", priceEntry1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(priceEntry1),
				(List<PriceEntry>)page.getItems());
		}
	}

	@Test
	public void testGetPriceListByExternalReferenceCodePriceEntriesPageWithFilterDoubleEquals()
		throws Exception {

		testGetPriceListByExternalReferenceCodePriceEntriesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetPriceListByExternalReferenceCodePriceEntriesPageWithFilterStringContains()
		throws Exception {

		testGetPriceListByExternalReferenceCodePriceEntriesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetPriceListByExternalReferenceCodePriceEntriesPageWithFilterStringEquals()
		throws Exception {

		testGetPriceListByExternalReferenceCodePriceEntriesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetPriceListByExternalReferenceCodePriceEntriesPageWithFilterStringStartsWith()
		throws Exception {

		testGetPriceListByExternalReferenceCodePriceEntriesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetPriceListByExternalReferenceCodePriceEntriesPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetPriceListByExternalReferenceCodePriceEntriesPage_getExternalReferenceCode();

		PriceEntry priceEntry1 =
			testGetPriceListByExternalReferenceCodePriceEntriesPage_addPriceEntry(
				externalReferenceCode, randomPriceEntry());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PriceEntry priceEntry2 =
			testGetPriceListByExternalReferenceCodePriceEntriesPage_addPriceEntry(
				externalReferenceCode, randomPriceEntry());

		for (EntityField entityField : entityFields) {
			Page<PriceEntry> page =
				priceEntryResource.
					getPriceListByExternalReferenceCodePriceEntriesPage(
						externalReferenceCode, null,
						getFilterString(entityField, operator, priceEntry1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(priceEntry1),
				(List<PriceEntry>)page.getItems());
		}
	}

	@Test
	public void testGetPriceListByExternalReferenceCodePriceEntriesPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetPriceListByExternalReferenceCodePriceEntriesPage_getExternalReferenceCode();

		Page<PriceEntry> priceEntriesPage =
			priceEntryResource.
				getPriceListByExternalReferenceCodePriceEntriesPage(
					externalReferenceCode, null, null, null, null);

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
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(priceEntry1, (List<PriceEntry>)page1.getItems());

			Page<PriceEntry> page2 =
				priceEntryResource.
					getPriceListByExternalReferenceCodePriceEntriesPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(priceEntry2, (List<PriceEntry>)page2.getItems());

			Page<PriceEntry> page3 =
				priceEntryResource.
					getPriceListByExternalReferenceCodePriceEntriesPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(priceEntry3, (List<PriceEntry>)page3.getItems());
		}
		else {
			Page<PriceEntry> page1 =
				priceEntryResource.
					getPriceListByExternalReferenceCodePriceEntriesPage(
						externalReferenceCode, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<PriceEntry> priceEntries1 = (List<PriceEntry>)page1.getItems();

			Assert.assertEquals(
				priceEntries1.toString(), totalCount + 2, priceEntries1.size());

			Page<PriceEntry> page2 =
				priceEntryResource.
					getPriceListByExternalReferenceCodePriceEntriesPage(
						externalReferenceCode, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PriceEntry> priceEntries2 = (List<PriceEntry>)page2.getItems();

			Assert.assertEquals(
				priceEntries2.toString(), 1, priceEntries2.size());

			Page<PriceEntry> page3 =
				priceEntryResource.
					getPriceListByExternalReferenceCodePriceEntriesPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(priceEntry1, (List<PriceEntry>)page3.getItems());
			assertContains(priceEntry2, (List<PriceEntry>)page3.getItems());
			assertContains(priceEntry3, (List<PriceEntry>)page3.getItems());
		}
	}

	@Test
	public void testGetPriceListByExternalReferenceCodePriceEntriesPageWithSortDateTime()
		throws Exception {

		testGetPriceListByExternalReferenceCodePriceEntriesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, priceEntry1, priceEntry2) -> {
				BeanTestUtil.setProperty(
					priceEntry1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetPriceListByExternalReferenceCodePriceEntriesPageWithSortDouble()
		throws Exception {

		testGetPriceListByExternalReferenceCodePriceEntriesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, priceEntry1, priceEntry2) -> {
				BeanTestUtil.setProperty(
					priceEntry1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					priceEntry2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetPriceListByExternalReferenceCodePriceEntriesPageWithSortInteger()
		throws Exception {

		testGetPriceListByExternalReferenceCodePriceEntriesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, priceEntry1, priceEntry2) -> {
				BeanTestUtil.setProperty(priceEntry1, entityField.getName(), 0);
				BeanTestUtil.setProperty(priceEntry2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetPriceListByExternalReferenceCodePriceEntriesPageWithSortString()
		throws Exception {

		testGetPriceListByExternalReferenceCodePriceEntriesPageWithSort(
			EntityField.Type.STRING,
			(entityField, priceEntry1, priceEntry2) -> {
				Class<?> clazz = priceEntry1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						priceEntry1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						priceEntry2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						priceEntry1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						priceEntry2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						priceEntry1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						priceEntry2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetPriceListByExternalReferenceCodePriceEntriesPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, PriceEntry, PriceEntry, Exception>
						unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetPriceListByExternalReferenceCodePriceEntriesPage_getExternalReferenceCode();

		PriceEntry priceEntry1 = randomPriceEntry();
		PriceEntry priceEntry2 = randomPriceEntry();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, priceEntry1, priceEntry2);
		}

		priceEntry1 =
			testGetPriceListByExternalReferenceCodePriceEntriesPage_addPriceEntry(
				externalReferenceCode, priceEntry1);

		priceEntry2 =
			testGetPriceListByExternalReferenceCodePriceEntriesPage_addPriceEntry(
				externalReferenceCode, priceEntry2);

		Page<PriceEntry> page =
			priceEntryResource.
				getPriceListByExternalReferenceCodePriceEntriesPage(
					externalReferenceCode, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<PriceEntry> ascPage =
				priceEntryResource.
					getPriceListByExternalReferenceCodePriceEntriesPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(priceEntry1, (List<PriceEntry>)ascPage.getItems());
			assertContains(priceEntry2, (List<PriceEntry>)ascPage.getItems());

			Page<PriceEntry> descPage =
				priceEntryResource.
					getPriceListByExternalReferenceCodePriceEntriesPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(priceEntry2, (List<PriceEntry>)descPage.getItems());
			assertContains(priceEntry1, (List<PriceEntry>)descPage.getItems());
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
				id, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			PriceEntry irrelevantPriceEntry =
				testGetPriceListIdPriceEntriesPage_addPriceEntry(
					irrelevantId, randomIrrelevantPriceEntry());

			page = priceEntryResource.getPriceListIdPriceEntriesPage(
				irrelevantId, null, null, Pagination.of(1, (int)totalCount + 1),
				null);

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
			id, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(priceEntry1, (List<PriceEntry>)page.getItems());
		assertContains(priceEntry2, (List<PriceEntry>)page.getItems());
		assertValid(
			page, testGetPriceListIdPriceEntriesPage_getExpectedActions(id));

		priceEntryResource.deletePriceEntry(priceEntry1.getPriceEntryId());

		priceEntryResource.deletePriceEntry(priceEntry2.getPriceEntryId());
	}

	protected Map<String, Map<String, String>>
			testGetPriceListIdPriceEntriesPage_getExpectedActions(Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetPriceListIdPriceEntriesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetPriceListIdPriceEntriesPage_getId();

		PriceEntry priceEntry1 = randomPriceEntry();

		priceEntry1 = testGetPriceListIdPriceEntriesPage_addPriceEntry(
			id, priceEntry1);

		for (EntityField entityField : entityFields) {
			Page<PriceEntry> page =
				priceEntryResource.getPriceListIdPriceEntriesPage(
					id, null,
					getFilterString(entityField, "between", priceEntry1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(priceEntry1),
				(List<PriceEntry>)page.getItems());
		}
	}

	@Test
	public void testGetPriceListIdPriceEntriesPageWithFilterDoubleEquals()
		throws Exception {

		testGetPriceListIdPriceEntriesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetPriceListIdPriceEntriesPageWithFilterStringContains()
		throws Exception {

		testGetPriceListIdPriceEntriesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetPriceListIdPriceEntriesPageWithFilterStringEquals()
		throws Exception {

		testGetPriceListIdPriceEntriesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetPriceListIdPriceEntriesPageWithFilterStringStartsWith()
		throws Exception {

		testGetPriceListIdPriceEntriesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetPriceListIdPriceEntriesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetPriceListIdPriceEntriesPage_getId();

		PriceEntry priceEntry1 =
			testGetPriceListIdPriceEntriesPage_addPriceEntry(
				id, randomPriceEntry());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PriceEntry priceEntry2 =
			testGetPriceListIdPriceEntriesPage_addPriceEntry(
				id, randomPriceEntry());

		for (EntityField entityField : entityFields) {
			Page<PriceEntry> page =
				priceEntryResource.getPriceListIdPriceEntriesPage(
					id, null,
					getFilterString(entityField, operator, priceEntry1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(priceEntry1),
				(List<PriceEntry>)page.getItems());
		}
	}

	@Test
	public void testGetPriceListIdPriceEntriesPageWithPagination()
		throws Exception {

		Long id = testGetPriceListIdPriceEntriesPage_getId();

		Page<PriceEntry> priceEntriesPage =
			priceEntryResource.getPriceListIdPriceEntriesPage(
				id, null, null, null, null);

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
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(priceEntry1, (List<PriceEntry>)page1.getItems());

			Page<PriceEntry> page2 =
				priceEntryResource.getPriceListIdPriceEntriesPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(priceEntry2, (List<PriceEntry>)page2.getItems());

			Page<PriceEntry> page3 =
				priceEntryResource.getPriceListIdPriceEntriesPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(priceEntry3, (List<PriceEntry>)page3.getItems());
		}
		else {
			Page<PriceEntry> page1 =
				priceEntryResource.getPriceListIdPriceEntriesPage(
					id, null, null, Pagination.of(1, totalCount + 2), null);

			List<PriceEntry> priceEntries1 = (List<PriceEntry>)page1.getItems();

			Assert.assertEquals(
				priceEntries1.toString(), totalCount + 2, priceEntries1.size());

			Page<PriceEntry> page2 =
				priceEntryResource.getPriceListIdPriceEntriesPage(
					id, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PriceEntry> priceEntries2 = (List<PriceEntry>)page2.getItems();

			Assert.assertEquals(
				priceEntries2.toString(), 1, priceEntries2.size());

			Page<PriceEntry> page3 =
				priceEntryResource.getPriceListIdPriceEntriesPage(
					id, null, null, Pagination.of(1, (int)totalCount + 3),
					null);

			assertContains(priceEntry1, (List<PriceEntry>)page3.getItems());
			assertContains(priceEntry2, (List<PriceEntry>)page3.getItems());
			assertContains(priceEntry3, (List<PriceEntry>)page3.getItems());
		}
	}

	@Test
	public void testGetPriceListIdPriceEntriesPageWithSortDateTime()
		throws Exception {

		testGetPriceListIdPriceEntriesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, priceEntry1, priceEntry2) -> {
				BeanTestUtil.setProperty(
					priceEntry1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetPriceListIdPriceEntriesPageWithSortDouble()
		throws Exception {

		testGetPriceListIdPriceEntriesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, priceEntry1, priceEntry2) -> {
				BeanTestUtil.setProperty(
					priceEntry1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					priceEntry2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetPriceListIdPriceEntriesPageWithSortInteger()
		throws Exception {

		testGetPriceListIdPriceEntriesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, priceEntry1, priceEntry2) -> {
				BeanTestUtil.setProperty(priceEntry1, entityField.getName(), 0);
				BeanTestUtil.setProperty(priceEntry2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetPriceListIdPriceEntriesPageWithSortString()
		throws Exception {

		testGetPriceListIdPriceEntriesPageWithSort(
			EntityField.Type.STRING,
			(entityField, priceEntry1, priceEntry2) -> {
				Class<?> clazz = priceEntry1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						priceEntry1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						priceEntry2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						priceEntry1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						priceEntry2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						priceEntry1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						priceEntry2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetPriceListIdPriceEntriesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, PriceEntry, PriceEntry, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetPriceListIdPriceEntriesPage_getId();

		PriceEntry priceEntry1 = randomPriceEntry();
		PriceEntry priceEntry2 = randomPriceEntry();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, priceEntry1, priceEntry2);
		}

		priceEntry1 = testGetPriceListIdPriceEntriesPage_addPriceEntry(
			id, priceEntry1);

		priceEntry2 = testGetPriceListIdPriceEntriesPage_addPriceEntry(
			id, priceEntry2);

		Page<PriceEntry> page =
			priceEntryResource.getPriceListIdPriceEntriesPage(
				id, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<PriceEntry> ascPage =
				priceEntryResource.getPriceListIdPriceEntriesPage(
					id, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(priceEntry1, (List<PriceEntry>)ascPage.getItems());
			assertContains(priceEntry2, (List<PriceEntry>)ascPage.getItems());

			Page<PriceEntry> descPage =
				priceEntryResource.getPriceListIdPriceEntriesPage(
					id, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(priceEntry2, (List<PriceEntry>)descPage.getItems());
			assertContains(priceEntry1, (List<PriceEntry>)descPage.getItems());
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
		PriceEntry postPriceEntry = testPatchPriceEntry_addPriceEntry();

		PriceEntry randomPatchPriceEntry = randomPatchPriceEntry();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PriceEntry patchPriceEntry = priceEntryResource.patchPriceEntry(
			postPriceEntry.getPriceEntryId(), randomPatchPriceEntry);

		PriceEntry expectedPatchPriceEntry = postPriceEntry.clone();

		BeanTestUtil.copyProperties(
			randomPatchPriceEntry, expectedPatchPriceEntry);

		PriceEntry getPriceEntry = priceEntryResource.getPriceEntry(
			patchPriceEntry.getPriceEntryId());

		assertEquals(expectedPatchPriceEntry, getPriceEntry);
		assertValid(getPriceEntry);
	}

	protected PriceEntry testPatchPriceEntry_addPriceEntry() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchPriceEntryByExternalReferenceCode() throws Exception {
		PriceEntry postPriceEntry =
			testPatchPriceEntryByExternalReferenceCode_addPriceEntry();

		PriceEntry randomPatchPriceEntry = randomPatchPriceEntry();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PriceEntry patchPriceEntry =
			priceEntryResource.patchPriceEntryByExternalReferenceCode(
				postPriceEntry.getExternalReferenceCode(),
				randomPatchPriceEntry);

		PriceEntry expectedPatchPriceEntry = postPriceEntry.clone();

		BeanTestUtil.copyProperties(
			randomPatchPriceEntry, expectedPatchPriceEntry);

		PriceEntry getPriceEntry =
			priceEntryResource.getPriceEntryByExternalReferenceCode(
				patchPriceEntry.getExternalReferenceCode());

		assertEquals(expectedPatchPriceEntry, getPriceEntry);
		assertValid(getPriceEntry);
	}

	protected PriceEntry
			testPatchPriceEntryByExternalReferenceCode_addPriceEntry()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
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
			priceEntryResource.getPriceEntryHttpResponse(
				priceEntry1.getPriceEntryId()));

		priceEntry1 = testBatchEngineDeleteImportTask_addPriceEntry();

		testBatchEngineDeleteImportTask_deletePriceEntry(
			200, null, priceEntry1.getPriceEntryId());

		assertHttpResponseStatusCode(
			404,
			priceEntryResource.getPriceEntryHttpResponse(
				priceEntry1.getPriceEntryId()));

		priceEntry1 = testBatchEngineDeleteImportTask_addPriceEntry();
		PriceEntry priceEntry2 =
			testBatchEngineDeleteImportTask_addPriceEntry();

		testBatchEngineDeleteImportTask_deletePriceEntry(
			200, priceEntry2.getExternalReferenceCode(),
			priceEntry1.getPriceEntryId());

		assertHttpResponseStatusCode(
			404,
			priceEntryResource.getPriceEntryHttpResponse(
				priceEntry1.getPriceEntryId()));
		assertHttpResponseStatusCode(
			200,
			priceEntryResource.getPriceEntryHttpResponse(
				priceEntry2.getPriceEntryId()));

		testBatchEngineDeleteImportTask_deletePriceEntry(
			200, priceEntry2.getExternalReferenceCode(),
			priceEntry1.getPriceEntryId());

		assertHttpResponseStatusCode(
			404,
			priceEntryResource.getPriceEntryHttpResponse(
				priceEntry2.getPriceEntryId()));
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
				"com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceEntry",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"priceEntryId", () -> id
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

		if (priceEntry.getPriceEntryId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (priceEntry.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (priceEntry.getActive() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("bulkPricing", additionalAssertFieldName)) {
				if (priceEntry.getBulkPricing() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (priceEntry.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"discountDiscovery", additionalAssertFieldName)) {

				if (priceEntry.getDiscountDiscovery() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("discountLevel1", additionalAssertFieldName)) {
				if (priceEntry.getDiscountLevel1() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("discountLevel2", additionalAssertFieldName)) {
				if (priceEntry.getDiscountLevel2() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("discountLevel3", additionalAssertFieldName)) {
				if (priceEntry.getDiscountLevel3() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("discountLevel4", additionalAssertFieldName)) {
				if (priceEntry.getDiscountLevel4() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"discountLevelsFormatted", additionalAssertFieldName)) {

				if (priceEntry.getDiscountLevelsFormatted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("displayDate", additionalAssertFieldName)) {
				if (priceEntry.getDisplayDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("expirationDate", additionalAssertFieldName)) {
				if (priceEntry.getExpirationDate() == null) {
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

			if (Objects.equals("neverExpire", additionalAssertFieldName)) {
				if (priceEntry.getNeverExpire() == null) {
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

			if (Objects.equals("priceEntryId", additionalAssertFieldName)) {
				if (priceEntry.getPriceEntryId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("priceFormatted", additionalAssertFieldName)) {
				if (priceEntry.getPriceFormatted() == null) {
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

			if (Objects.equals(
					"priceOnApplication", additionalAssertFieldName)) {

				if (priceEntry.getPriceOnApplication() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("product", additionalAssertFieldName)) {
				if (priceEntry.getProduct() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("quantity", additionalAssertFieldName)) {
				if (priceEntry.getQuantity() == null) {
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

			if (Objects.equals("unitOfMeasureKey", additionalAssertFieldName)) {
				if (priceEntry.getUnitOfMeasureKey() == null) {
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

		graphQLFields.add(new GraphQLField("priceEntryId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.pricing.dto.v2_0.
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

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)priceEntry1.getActions(),
						(Map)priceEntry2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceEntry1.getActive(), priceEntry2.getActive())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("bulkPricing", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceEntry1.getBulkPricing(),
						priceEntry2.getBulkPricing())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!equals(
						(Map)priceEntry1.getCustomFields(),
						(Map)priceEntry2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"discountDiscovery", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceEntry1.getDiscountDiscovery(),
						priceEntry2.getDiscountDiscovery())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("discountLevel1", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceEntry1.getDiscountLevel1(),
						priceEntry2.getDiscountLevel1())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("discountLevel2", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceEntry1.getDiscountLevel2(),
						priceEntry2.getDiscountLevel2())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("discountLevel3", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceEntry1.getDiscountLevel3(),
						priceEntry2.getDiscountLevel3())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("discountLevel4", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceEntry1.getDiscountLevel4(),
						priceEntry2.getDiscountLevel4())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"discountLevelsFormatted", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceEntry1.getDiscountLevelsFormatted(),
						priceEntry2.getDiscountLevelsFormatted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("displayDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceEntry1.getDisplayDate(),
						priceEntry2.getDisplayDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("expirationDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceEntry1.getExpirationDate(),
						priceEntry2.getExpirationDate())) {

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

			if (Objects.equals("neverExpire", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceEntry1.getNeverExpire(),
						priceEntry2.getNeverExpire())) {

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

			if (Objects.equals("priceEntryId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceEntry1.getPriceEntryId(),
						priceEntry2.getPriceEntryId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priceFormatted", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceEntry1.getPriceFormatted(),
						priceEntry2.getPriceFormatted())) {

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

			if (Objects.equals(
					"priceOnApplication", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceEntry1.getPriceOnApplication(),
						priceEntry2.getPriceOnApplication())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("product", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceEntry1.getProduct(), priceEntry2.getProduct())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("quantity", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceEntry1.getQuantity(), priceEntry2.getQuantity())) {

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

			if (Objects.equals("unitOfMeasureKey", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceEntry1.getUnitOfMeasureKey(),
						priceEntry2.getUnitOfMeasureKey())) {

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

		if (entityFieldName.equals("actions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("active")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("bulkPricing")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("customFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountDiscovery")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountLevel1")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountLevel2")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountLevel3")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountLevel4")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountLevelsFormatted")) {
			Object object = priceEntry.getDiscountLevelsFormatted();

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

		if (entityFieldName.equals("displayDate")) {
			if (operator.equals("between")) {
				Date date = priceEntry.getDisplayDate();

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

				sb.append(_format.format(priceEntry.getDisplayDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("expirationDate")) {
			if (operator.equals("between")) {
				Date date = priceEntry.getExpirationDate();

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

				sb.append(_format.format(priceEntry.getExpirationDate()));
			}

			return sb.toString();
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

		if (entityFieldName.equals("neverExpire")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("price")) {
			sb.append(String.valueOf(priceEntry.getPrice()));

			return sb.toString();
		}

		if (entityFieldName.equals("priceEntryId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priceFormatted")) {
			Object object = priceEntry.getPriceFormatted();

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

		if (entityFieldName.equals("priceOnApplication")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("product")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("quantity")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("sku")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
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

		if (entityFieldName.equals("unitOfMeasureKey")) {
			Object object = priceEntry.getUnitOfMeasureKey();

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

	protected PriceEntry randomPriceEntry() throws Exception {
		return new PriceEntry() {
			{
				active = RandomTestUtil.randomBoolean();
				bulkPricing = RandomTestUtil.randomBoolean();
				discountDiscovery = RandomTestUtil.randomBoolean();
				discountLevelsFormatted = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				displayDate = RandomTestUtil.nextDate();
				expirationDate = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				hasTierPrice = RandomTestUtil.randomBoolean();
				neverExpire = RandomTestUtil.randomBoolean();
				price = RandomTestUtil.randomDouble();
				priceEntryId = RandomTestUtil.randomLong();
				priceFormatted = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				priceListExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				priceListId = RandomTestUtil.randomLong();
				priceOnApplication = RandomTestUtil.randomBoolean();
				skuExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				skuId = RandomTestUtil.randomLong();
				unitOfMeasureKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
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
		com.liferay.headless.commerce.admin.pricing.resource.v2_0.
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