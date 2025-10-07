/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.site.setting.resource.v1_0.test;

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
import com.liferay.headless.commerce.admin.site.setting.client.dto.v1_0.MeasurementUnit;
import com.liferay.headless.commerce.admin.site.setting.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.site.setting.client.pagination.Page;
import com.liferay.headless.commerce.admin.site.setting.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.site.setting.client.resource.v1_0.MeasurementUnitResource;
import com.liferay.headless.commerce.admin.site.setting.client.serdes.v1_0.MeasurementUnitSerDes;
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
public abstract class BaseMeasurementUnitResourceTestCase {

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

		_measurementUnitResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		measurementUnitResource = MeasurementUnitResource.builder(
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

		MeasurementUnit measurementUnit1 = randomMeasurementUnit();

		String json = objectMapper.writeValueAsString(measurementUnit1);

		MeasurementUnit measurementUnit2 = MeasurementUnitSerDes.toDTO(json);

		Assert.assertTrue(equals(measurementUnit1, measurementUnit2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		MeasurementUnit measurementUnit = randomMeasurementUnit();

		String json1 = objectMapper.writeValueAsString(measurementUnit);
		String json2 = MeasurementUnitSerDes.toJSON(measurementUnit);

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

		MeasurementUnit measurementUnit = randomMeasurementUnit();

		measurementUnit.setExternalReferenceCode(regex);
		measurementUnit.setKey(regex);
		measurementUnit.setType(regex);

		String json = MeasurementUnitSerDes.toJSON(measurementUnit);

		Assert.assertFalse(json.contains(regex));

		measurementUnit = MeasurementUnitSerDes.toDTO(json);

		Assert.assertEquals(regex, measurementUnit.getExternalReferenceCode());
		Assert.assertEquals(regex, measurementUnit.getKey());
		Assert.assertEquals(regex, measurementUnit.getType());
	}

	@Test
	public void testDeleteMeasurementUnit() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		MeasurementUnit measurementUnit =
			testDeleteMeasurementUnit_addMeasurementUnit();

		assertHttpResponseStatusCode(
			204,
			measurementUnitResource.deleteMeasurementUnitHttpResponse(
				measurementUnit.getId()));

		assertHttpResponseStatusCode(
			404,
			measurementUnitResource.getMeasurementUnitHttpResponse(
				measurementUnit.getId()));
		assertHttpResponseStatusCode(
			404, measurementUnitResource.getMeasurementUnitHttpResponse(0L));
	}

	protected MeasurementUnit testDeleteMeasurementUnit_addMeasurementUnit()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteMeasurementUnit() throws Exception {

		// No namespace

		MeasurementUnit measurementUnit1 =
			testGraphQLDeleteMeasurementUnit_addMeasurementUnit();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteMeasurementUnit",
						new HashMap<String, Object>() {
							{
								put("id", measurementUnit1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteMeasurementUnit"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"measurementUnit",
					new HashMap<String, Object>() {
						{
							put("id", measurementUnit1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminSiteSetting_v1_0

		MeasurementUnit measurementUnit2 =
			testGraphQLDeleteMeasurementUnit_addMeasurementUnit();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminSiteSetting_v1_0",
						new GraphQLField(
							"deleteMeasurementUnit",
							new HashMap<String, Object>() {
								{
									put("id", measurementUnit2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminSiteSetting_v1_0",
				"Object/deleteMeasurementUnit"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminSiteSetting_v1_0",
					new GraphQLField(
						"measurementUnit",
						new HashMap<String, Object>() {
							{
								put("id", measurementUnit2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected MeasurementUnit
			testGraphQLDeleteMeasurementUnit_addMeasurementUnit()
		throws Exception {

		return testGraphQLMeasurementUnit_addMeasurementUnit();
	}

	@Test
	public void testDeleteMeasurementUnitBatch() throws Exception {
		MeasurementUnit measurementUnit1 =
			testDeleteMeasurementUnitBatch_addMeasurementUnit();

		testDeleteMeasurementUnitBatch_deleteMeasurementUnit(
			202, measurementUnit1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			measurementUnitResource.getMeasurementUnitHttpResponse(
				measurementUnit1.getId()));

		measurementUnit1 = testDeleteMeasurementUnitBatch_addMeasurementUnit();

		testDeleteMeasurementUnitBatch_deleteMeasurementUnit(
			202, null, measurementUnit1.getId());

		assertHttpResponseStatusCode(
			404,
			measurementUnitResource.getMeasurementUnitHttpResponse(
				measurementUnit1.getId()));

		measurementUnit1 = testDeleteMeasurementUnitBatch_addMeasurementUnit();
		MeasurementUnit measurementUnit2 =
			testDeleteMeasurementUnitBatch_addMeasurementUnit();

		testDeleteMeasurementUnitBatch_deleteMeasurementUnit(
			202, measurementUnit2.getExternalReferenceCode(),
			measurementUnit1.getId());

		assertHttpResponseStatusCode(
			404,
			measurementUnitResource.getMeasurementUnitHttpResponse(
				measurementUnit1.getId()));
		assertHttpResponseStatusCode(
			200,
			measurementUnitResource.getMeasurementUnitHttpResponse(
				measurementUnit2.getId()));

		testDeleteMeasurementUnitBatch_deleteMeasurementUnit(
			202, measurementUnit2.getExternalReferenceCode(),
			measurementUnit1.getId());

		assertHttpResponseStatusCode(
			404,
			measurementUnitResource.getMeasurementUnitHttpResponse(
				measurementUnit2.getId()));
	}

	protected MeasurementUnit
			testDeleteMeasurementUnitBatch_addMeasurementUnit()
		throws Exception {

		return testDeleteMeasurementUnit_addMeasurementUnit();
	}

	protected void testDeleteMeasurementUnitBatch_deleteMeasurementUnit(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			measurementUnitResource.deleteMeasurementUnitBatchHttpResponse(
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
	public void testDeleteMeasurementUnitByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		MeasurementUnit measurementUnit =
			testDeleteMeasurementUnitByExternalReferenceCode_addMeasurementUnit();

		assertHttpResponseStatusCode(
			204,
			measurementUnitResource.
				deleteMeasurementUnitByExternalReferenceCodeHttpResponse(
					measurementUnit.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			measurementUnitResource.
				getMeasurementUnitByExternalReferenceCodeHttpResponse(
					measurementUnit.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			measurementUnitResource.
				getMeasurementUnitByExternalReferenceCodeHttpResponse("-"));
	}

	protected MeasurementUnit
			testDeleteMeasurementUnitByExternalReferenceCode_addMeasurementUnit()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteMeasurementUnitByExternalReferenceCode()
		throws Exception {

		// No namespace

		MeasurementUnit measurementUnit1 =
			testGraphQLDeleteMeasurementUnitByExternalReferenceCode_addMeasurementUnit();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteMeasurementUnitByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										measurementUnit1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteMeasurementUnitByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"measurementUnitByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" +
									measurementUnit1.
										getExternalReferenceCode() + "\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminSiteSetting_v1_0

		MeasurementUnit measurementUnit2 =
			testGraphQLDeleteMeasurementUnitByExternalReferenceCode_addMeasurementUnit();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminSiteSetting_v1_0",
						new GraphQLField(
							"deleteMeasurementUnitByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											measurementUnit2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminSiteSetting_v1_0",
				"Object/deleteMeasurementUnitByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminSiteSetting_v1_0",
					new GraphQLField(
						"measurementUnitByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										measurementUnit2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected MeasurementUnit
			testGraphQLDeleteMeasurementUnitByExternalReferenceCode_addMeasurementUnit()
		throws Exception {

		return testGraphQLMeasurementUnit_addMeasurementUnit();
	}

	@Test
	public void testDeleteMeasurementUnitByKey() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		MeasurementUnit measurementUnit =
			testDeleteMeasurementUnitByKey_addMeasurementUnit();

		assertHttpResponseStatusCode(
			204,
			measurementUnitResource.deleteMeasurementUnitByKeyHttpResponse(
				measurementUnit.getKey()));

		assertHttpResponseStatusCode(
			404,
			measurementUnitResource.getMeasurementUnitByKeyHttpResponse(
				measurementUnit.getKey()));
		assertHttpResponseStatusCode(
			404,
			measurementUnitResource.getMeasurementUnitByKeyHttpResponse(
				measurementUnit.getKey()));
	}

	protected MeasurementUnit
			testDeleteMeasurementUnitByKey_addMeasurementUnit()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteMeasurementUnitByKey() throws Exception {

		// No namespace

		MeasurementUnit measurementUnit1 =
			testGraphQLDeleteMeasurementUnitByKey_addMeasurementUnit();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteMeasurementUnitByKey",
						new HashMap<String, Object>() {
							{
								put(
									"key",
									"\"" + measurementUnit1.getKey() + "\"");
							}
						})),
				"JSONObject/data", "Object/deleteMeasurementUnitByKey"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"measurementUnitByKey",
					new HashMap<String, Object>() {
						{
							put("key", "\"" + measurementUnit1.getKey() + "\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminSiteSetting_v1_0

		MeasurementUnit measurementUnit2 =
			testGraphQLDeleteMeasurementUnitByKey_addMeasurementUnit();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminSiteSetting_v1_0",
						new GraphQLField(
							"deleteMeasurementUnitByKey",
							new HashMap<String, Object>() {
								{
									put(
										"key",
										"\"" + measurementUnit2.getKey() +
											"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminSiteSetting_v1_0",
				"Object/deleteMeasurementUnitByKey"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminSiteSetting_v1_0",
					new GraphQLField(
						"measurementUnitByKey",
						new HashMap<String, Object>() {
							{
								put(
									"key",
									"\"" + measurementUnit2.getKey() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected MeasurementUnit
			testGraphQLDeleteMeasurementUnitByKey_addMeasurementUnit()
		throws Exception {

		return testGraphQLMeasurementUnit_addMeasurementUnit();
	}

	@Test
	public void testGetMeasurementUnit() throws Exception {
		MeasurementUnit postMeasurementUnit =
			testGetMeasurementUnit_addMeasurementUnit();

		MeasurementUnit getMeasurementUnit =
			measurementUnitResource.getMeasurementUnit(
				postMeasurementUnit.getId());

		assertEquals(postMeasurementUnit, getMeasurementUnit);
		assertValid(getMeasurementUnit);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		MeasurementUnit postMeasurementUnit =
			testGetMeasurementUnit_addMeasurementUnit();

		MeasurementUnit getMeasurementUnit =
			measurementUnitResource.getMeasurementUnit(
				postMeasurementUnit.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.site.setting.dto.v1_0.MeasurementUnit"
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
			postMeasurementUnit.getId());

		assertEquals(
			getMeasurementUnit, MeasurementUnitSerDes.toDTO(item.toString()));
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

	protected MeasurementUnit testGetMeasurementUnit_addMeasurementUnit()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetMeasurementUnit() throws Exception {
		MeasurementUnit measurementUnit =
			testGraphQLGetMeasurementUnit_addMeasurementUnit();

		// No namespace

		Assert.assertTrue(
			equals(
				measurementUnit,
				MeasurementUnitSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"measurementUnit",
								new HashMap<String, Object>() {
									{
										put("id", measurementUnit.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/measurementUnit"))));

		// Using the namespace headlessCommerceAdminSiteSetting_v1_0

		Assert.assertTrue(
			equals(
				measurementUnit,
				MeasurementUnitSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminSiteSetting_v1_0",
								new GraphQLField(
									"measurementUnit",
									new HashMap<String, Object>() {
										{
											put("id", measurementUnit.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminSiteSetting_v1_0",
						"Object/measurementUnit"))));
	}

	@Test
	public void testGraphQLGetMeasurementUnitNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"measurementUnit",
						new HashMap<String, Object>() {
							{
								put("id", irrelevantId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminSiteSetting_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminSiteSetting_v1_0",
						new GraphQLField(
							"measurementUnit",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected MeasurementUnit testGraphQLGetMeasurementUnit_addMeasurementUnit()
		throws Exception {

		return testGraphQLMeasurementUnit_addMeasurementUnit();
	}

	@Test
	public void testGetMeasurementUnitByExternalReferenceCode()
		throws Exception {

		MeasurementUnit postMeasurementUnit =
			testGetMeasurementUnitByExternalReferenceCode_addMeasurementUnit();

		MeasurementUnit getMeasurementUnit =
			measurementUnitResource.getMeasurementUnitByExternalReferenceCode(
				postMeasurementUnit.getExternalReferenceCode());

		assertEquals(postMeasurementUnit, getMeasurementUnit);
		assertValid(getMeasurementUnit);
	}

	protected MeasurementUnit
			testGetMeasurementUnitByExternalReferenceCode_addMeasurementUnit()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetMeasurementUnitByExternalReferenceCode()
		throws Exception {

		MeasurementUnit measurementUnit =
			testGraphQLGetMeasurementUnitByExternalReferenceCode_addMeasurementUnit();

		// No namespace

		Assert.assertTrue(
			equals(
				measurementUnit,
				MeasurementUnitSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"measurementUnitByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												measurementUnit.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/measurementUnitByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminSiteSetting_v1_0

		Assert.assertTrue(
			equals(
				measurementUnit,
				MeasurementUnitSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminSiteSetting_v1_0",
								new GraphQLField(
									"measurementUnitByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													measurementUnit.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminSiteSetting_v1_0",
						"Object/measurementUnitByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetMeasurementUnitByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"measurementUnitByExternalReferenceCode",
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

		// Using the namespace headlessCommerceAdminSiteSetting_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminSiteSetting_v1_0",
						new GraphQLField(
							"measurementUnitByExternalReferenceCode",
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

	protected MeasurementUnit
			testGraphQLGetMeasurementUnitByExternalReferenceCode_addMeasurementUnit()
		throws Exception {

		return testGraphQLMeasurementUnit_addMeasurementUnit();
	}

	@Test
	public void testGetMeasurementUnitByKey() throws Exception {
		MeasurementUnit postMeasurementUnit =
			testGetMeasurementUnitByKey_addMeasurementUnit();

		MeasurementUnit getMeasurementUnit =
			measurementUnitResource.getMeasurementUnitByKey(
				postMeasurementUnit.getKey());

		assertEquals(postMeasurementUnit, getMeasurementUnit);
		assertValid(getMeasurementUnit);
	}

	protected MeasurementUnit testGetMeasurementUnitByKey_addMeasurementUnit()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetMeasurementUnitByKey() throws Exception {
		MeasurementUnit measurementUnit =
			testGraphQLGetMeasurementUnitByKey_addMeasurementUnit();

		// No namespace

		Assert.assertTrue(
			equals(
				measurementUnit,
				MeasurementUnitSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"measurementUnitByKey",
								new HashMap<String, Object>() {
									{
										put(
											"key",
											"\"" + measurementUnit.getKey() +
												"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/measurementUnitByKey"))));

		// Using the namespace headlessCommerceAdminSiteSetting_v1_0

		Assert.assertTrue(
			equals(
				measurementUnit,
				MeasurementUnitSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminSiteSetting_v1_0",
								new GraphQLField(
									"measurementUnitByKey",
									new HashMap<String, Object>() {
										{
											put(
												"key",
												"\"" +
													measurementUnit.getKey() +
														"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminSiteSetting_v1_0",
						"Object/measurementUnitByKey"))));
	}

	@Test
	public void testGraphQLGetMeasurementUnitByKeyNotFound() throws Exception {
		String irrelevantKey = "\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"measurementUnitByKey",
						new HashMap<String, Object>() {
							{
								put("key", irrelevantKey);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminSiteSetting_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminSiteSetting_v1_0",
						new GraphQLField(
							"measurementUnitByKey",
							new HashMap<String, Object>() {
								{
									put("key", irrelevantKey);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected MeasurementUnit
			testGraphQLGetMeasurementUnitByKey_addMeasurementUnit()
		throws Exception {

		return testGraphQLMeasurementUnit_addMeasurementUnit();
	}

	@Test
	public void testGetMeasurementUnitsByType() throws Exception {
		String measurementUnitType =
			testGetMeasurementUnitsByType_getMeasurementUnitType();
		String irrelevantMeasurementUnitType =
			testGetMeasurementUnitsByType_getIrrelevantMeasurementUnitType();

		Page<MeasurementUnit> page =
			measurementUnitResource.getMeasurementUnitsByType(
				measurementUnitType, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantMeasurementUnitType != null) {
			MeasurementUnit irrelevantMeasurementUnit =
				testGetMeasurementUnitsByType_addMeasurementUnit(
					irrelevantMeasurementUnitType,
					randomIrrelevantMeasurementUnit());

			page = measurementUnitResource.getMeasurementUnitsByType(
				irrelevantMeasurementUnitType,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantMeasurementUnit,
				(List<MeasurementUnit>)page.getItems());
			assertValid(
				page,
				testGetMeasurementUnitsByType_getExpectedActions(
					irrelevantMeasurementUnitType));
		}

		MeasurementUnit measurementUnit1 =
			testGetMeasurementUnitsByType_addMeasurementUnit(
				measurementUnitType, randomMeasurementUnit());

		MeasurementUnit measurementUnit2 =
			testGetMeasurementUnitsByType_addMeasurementUnit(
				measurementUnitType, randomMeasurementUnit());

		page = measurementUnitResource.getMeasurementUnitsByType(
			measurementUnitType, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			measurementUnit1, (List<MeasurementUnit>)page.getItems());
		assertContains(
			measurementUnit2, (List<MeasurementUnit>)page.getItems());
		assertValid(
			page,
			testGetMeasurementUnitsByType_getExpectedActions(
				measurementUnitType));

		measurementUnitResource.deleteMeasurementUnit(measurementUnit1.getId());

		measurementUnitResource.deleteMeasurementUnit(measurementUnit2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetMeasurementUnitsByType_getExpectedActions(
				String measurementUnitType)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetMeasurementUnitsByTypeWithPagination() throws Exception {
		String measurementUnitType =
			testGetMeasurementUnitsByType_getMeasurementUnitType();

		Page<MeasurementUnit> measurementUnitsPage =
			measurementUnitResource.getMeasurementUnitsByType(
				measurementUnitType, null, null);

		int totalCount = GetterUtil.getInteger(
			measurementUnitsPage.getTotalCount());

		MeasurementUnit measurementUnit1 =
			testGetMeasurementUnitsByType_addMeasurementUnit(
				measurementUnitType, randomMeasurementUnit());

		MeasurementUnit measurementUnit2 =
			testGetMeasurementUnitsByType_addMeasurementUnit(
				measurementUnitType, randomMeasurementUnit());

		MeasurementUnit measurementUnit3 =
			testGetMeasurementUnitsByType_addMeasurementUnit(
				measurementUnitType, randomMeasurementUnit());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<MeasurementUnit> page1 =
				measurementUnitResource.getMeasurementUnitsByType(
					measurementUnitType,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				measurementUnit1, (List<MeasurementUnit>)page1.getItems());

			Page<MeasurementUnit> page2 =
				measurementUnitResource.getMeasurementUnitsByType(
					measurementUnitType,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				measurementUnit2, (List<MeasurementUnit>)page2.getItems());

			Page<MeasurementUnit> page3 =
				measurementUnitResource.getMeasurementUnitsByType(
					measurementUnitType,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				measurementUnit3, (List<MeasurementUnit>)page3.getItems());
		}
		else {
			Page<MeasurementUnit> page1 =
				measurementUnitResource.getMeasurementUnitsByType(
					measurementUnitType, Pagination.of(1, totalCount + 2),
					null);

			List<MeasurementUnit> measurementUnits1 =
				(List<MeasurementUnit>)page1.getItems();

			Assert.assertEquals(
				measurementUnits1.toString(), totalCount + 2,
				measurementUnits1.size());

			Page<MeasurementUnit> page2 =
				measurementUnitResource.getMeasurementUnitsByType(
					measurementUnitType, Pagination.of(2, totalCount + 2),
					null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<MeasurementUnit> measurementUnits2 =
				(List<MeasurementUnit>)page2.getItems();

			Assert.assertEquals(
				measurementUnits2.toString(), 1, measurementUnits2.size());

			Page<MeasurementUnit> page3 =
				measurementUnitResource.getMeasurementUnitsByType(
					measurementUnitType, Pagination.of(1, (int)totalCount + 3),
					null);

			assertContains(
				measurementUnit1, (List<MeasurementUnit>)page3.getItems());
			assertContains(
				measurementUnit2, (List<MeasurementUnit>)page3.getItems());
			assertContains(
				measurementUnit3, (List<MeasurementUnit>)page3.getItems());
		}
	}

	@Test
	public void testGetMeasurementUnitsByTypeWithSortDateTime()
		throws Exception {

		testGetMeasurementUnitsByTypeWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, measurementUnit1, measurementUnit2) -> {
				BeanTestUtil.setProperty(
					measurementUnit1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetMeasurementUnitsByTypeWithSortDouble() throws Exception {
		testGetMeasurementUnitsByTypeWithSort(
			EntityField.Type.DOUBLE,
			(entityField, measurementUnit1, measurementUnit2) -> {
				BeanTestUtil.setProperty(
					measurementUnit1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					measurementUnit2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetMeasurementUnitsByTypeWithSortInteger()
		throws Exception {

		testGetMeasurementUnitsByTypeWithSort(
			EntityField.Type.INTEGER,
			(entityField, measurementUnit1, measurementUnit2) -> {
				BeanTestUtil.setProperty(
					measurementUnit1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					measurementUnit2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetMeasurementUnitsByTypeWithSortString() throws Exception {
		testGetMeasurementUnitsByTypeWithSort(
			EntityField.Type.STRING,
			(entityField, measurementUnit1, measurementUnit2) -> {
				Class<?> clazz = measurementUnit1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						measurementUnit1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						measurementUnit2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						measurementUnit1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						measurementUnit2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						measurementUnit1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						measurementUnit2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetMeasurementUnitsByTypeWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, MeasurementUnit, MeasurementUnit, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String measurementUnitType =
			testGetMeasurementUnitsByType_getMeasurementUnitType();

		MeasurementUnit measurementUnit1 = randomMeasurementUnit();
		MeasurementUnit measurementUnit2 = randomMeasurementUnit();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, measurementUnit1, measurementUnit2);
		}

		measurementUnit1 = testGetMeasurementUnitsByType_addMeasurementUnit(
			measurementUnitType, measurementUnit1);

		measurementUnit2 = testGetMeasurementUnitsByType_addMeasurementUnit(
			measurementUnitType, measurementUnit2);

		Page<MeasurementUnit> page =
			measurementUnitResource.getMeasurementUnitsByType(
				measurementUnitType, null, null);

		for (EntityField entityField : entityFields) {
			Page<MeasurementUnit> ascPage =
				measurementUnitResource.getMeasurementUnitsByType(
					measurementUnitType,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				measurementUnit1, (List<MeasurementUnit>)ascPage.getItems());
			assertContains(
				measurementUnit2, (List<MeasurementUnit>)ascPage.getItems());

			Page<MeasurementUnit> descPage =
				measurementUnitResource.getMeasurementUnitsByType(
					measurementUnitType,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				measurementUnit2, (List<MeasurementUnit>)descPage.getItems());
			assertContains(
				measurementUnit1, (List<MeasurementUnit>)descPage.getItems());
		}
	}

	protected MeasurementUnit testGetMeasurementUnitsByType_addMeasurementUnit(
			String measurementUnitType, MeasurementUnit measurementUnit)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String testGetMeasurementUnitsByType_getMeasurementUnitType()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetMeasurementUnitsByType_getIrrelevantMeasurementUnitType()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetMeasurementUnitsByType() throws Exception {
		String measurementUnitType =
			testGetMeasurementUnitsByType_getMeasurementUnitType();

		GraphQLField graphQLField = new GraphQLField(
			"measurementUnitsByType",
			new HashMap<String, Object>() {
				{
					put(
						"measurementUnitType",
						"\"" + measurementUnitType + "\"");
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject measurementUnitsByTypeJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/measurementUnitsByType");

		long totalCount = measurementUnitsByTypeJSONObject.getLong(
			"totalCount");

		MeasurementUnit measurementUnit1 =
			testGraphQLGetMeasurementUnitsByTypeMeasurementUnit_addMeasurementUnit(
				measurementUnitType, randomMeasurementUnit());

		MeasurementUnit measurementUnit2 =
			testGraphQLGetMeasurementUnitsByTypeMeasurementUnit_addMeasurementUnit(
				measurementUnitType, randomMeasurementUnit());

		measurementUnitsByTypeJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/measurementUnitsByType");

		Assert.assertEquals(
			totalCount + 2,
			measurementUnitsByTypeJSONObject.getLong("totalCount"));

		assertContains(
			measurementUnit1,
			Arrays.asList(
				MeasurementUnitSerDes.toDTOs(
					measurementUnitsByTypeJSONObject.getString("items"))));
		assertContains(
			measurementUnit2,
			Arrays.asList(
				MeasurementUnitSerDes.toDTOs(
					measurementUnitsByTypeJSONObject.getString("items"))));

		// Using the namespace headlessCommerceAdminSiteSetting_v1_0

		measurementUnitsByTypeJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminSiteSetting_v1_0", graphQLField)),
			"JSONObject/data",
			"JSONObject/headlessCommerceAdminSiteSetting_v1_0",
			"JSONObject/measurementUnitsByType");

		Assert.assertEquals(
			totalCount + 2,
			measurementUnitsByTypeJSONObject.getLong("totalCount"));

		assertContains(
			measurementUnit1,
			Arrays.asList(
				MeasurementUnitSerDes.toDTOs(
					measurementUnitsByTypeJSONObject.getString("items"))));
		assertContains(
			measurementUnit2,
			Arrays.asList(
				MeasurementUnitSerDes.toDTOs(
					measurementUnitsByTypeJSONObject.getString("items"))));
	}

	protected MeasurementUnit
			testGraphQLGetMeasurementUnitsByTypeMeasurementUnit_addMeasurementUnit(
				String measurementUnitType, MeasurementUnit measurementUnit)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetMeasurementUnitsPage() throws Exception {
		Page<MeasurementUnit> page =
			measurementUnitResource.getMeasurementUnitsPage(
				null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		MeasurementUnit measurementUnit1 =
			testGetMeasurementUnitsPage_addMeasurementUnit(
				randomMeasurementUnit());

		MeasurementUnit measurementUnit2 =
			testGetMeasurementUnitsPage_addMeasurementUnit(
				randomMeasurementUnit());

		page = measurementUnitResource.getMeasurementUnitsPage(
			null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			measurementUnit1, (List<MeasurementUnit>)page.getItems());
		assertContains(
			measurementUnit2, (List<MeasurementUnit>)page.getItems());
		assertValid(page, testGetMeasurementUnitsPage_getExpectedActions());

		measurementUnitResource.deleteMeasurementUnit(measurementUnit1.getId());

		measurementUnitResource.deleteMeasurementUnit(measurementUnit2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetMeasurementUnitsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetMeasurementUnitsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		MeasurementUnit measurementUnit1 = randomMeasurementUnit();

		measurementUnit1 = testGetMeasurementUnitsPage_addMeasurementUnit(
			measurementUnit1);

		for (EntityField entityField : entityFields) {
			Page<MeasurementUnit> page =
				measurementUnitResource.getMeasurementUnitsPage(
					getFilterString(entityField, "between", measurementUnit1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(measurementUnit1),
				(List<MeasurementUnit>)page.getItems());
		}
	}

	@Test
	public void testGetMeasurementUnitsPageWithFilterDoubleEquals()
		throws Exception {

		testGetMeasurementUnitsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetMeasurementUnitsPageWithFilterStringContains()
		throws Exception {

		testGetMeasurementUnitsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetMeasurementUnitsPageWithFilterStringEquals()
		throws Exception {

		testGetMeasurementUnitsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetMeasurementUnitsPageWithFilterStringStartsWith()
		throws Exception {

		testGetMeasurementUnitsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetMeasurementUnitsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		MeasurementUnit measurementUnit1 =
			testGetMeasurementUnitsPage_addMeasurementUnit(
				randomMeasurementUnit());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		MeasurementUnit measurementUnit2 =
			testGetMeasurementUnitsPage_addMeasurementUnit(
				randomMeasurementUnit());

		for (EntityField entityField : entityFields) {
			Page<MeasurementUnit> page =
				measurementUnitResource.getMeasurementUnitsPage(
					getFilterString(entityField, operator, measurementUnit1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(measurementUnit1),
				(List<MeasurementUnit>)page.getItems());
		}
	}

	@Test
	public void testGetMeasurementUnitsPageWithPagination() throws Exception {
		Page<MeasurementUnit> measurementUnitsPage =
			measurementUnitResource.getMeasurementUnitsPage(null, null, null);

		int totalCount = GetterUtil.getInteger(
			measurementUnitsPage.getTotalCount());

		MeasurementUnit measurementUnit1 =
			testGetMeasurementUnitsPage_addMeasurementUnit(
				randomMeasurementUnit());

		MeasurementUnit measurementUnit2 =
			testGetMeasurementUnitsPage_addMeasurementUnit(
				randomMeasurementUnit());

		MeasurementUnit measurementUnit3 =
			testGetMeasurementUnitsPage_addMeasurementUnit(
				randomMeasurementUnit());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<MeasurementUnit> page1 =
				measurementUnitResource.getMeasurementUnitsPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				measurementUnit1, (List<MeasurementUnit>)page1.getItems());

			Page<MeasurementUnit> page2 =
				measurementUnitResource.getMeasurementUnitsPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				measurementUnit2, (List<MeasurementUnit>)page2.getItems());

			Page<MeasurementUnit> page3 =
				measurementUnitResource.getMeasurementUnitsPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				measurementUnit3, (List<MeasurementUnit>)page3.getItems());
		}
		else {
			Page<MeasurementUnit> page1 =
				measurementUnitResource.getMeasurementUnitsPage(
					null, Pagination.of(1, totalCount + 2), null);

			List<MeasurementUnit> measurementUnits1 =
				(List<MeasurementUnit>)page1.getItems();

			Assert.assertEquals(
				measurementUnits1.toString(), totalCount + 2,
				measurementUnits1.size());

			Page<MeasurementUnit> page2 =
				measurementUnitResource.getMeasurementUnitsPage(
					null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<MeasurementUnit> measurementUnits2 =
				(List<MeasurementUnit>)page2.getItems();

			Assert.assertEquals(
				measurementUnits2.toString(), 1, measurementUnits2.size());

			Page<MeasurementUnit> page3 =
				measurementUnitResource.getMeasurementUnitsPage(
					null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				measurementUnit1, (List<MeasurementUnit>)page3.getItems());
			assertContains(
				measurementUnit2, (List<MeasurementUnit>)page3.getItems());
			assertContains(
				measurementUnit3, (List<MeasurementUnit>)page3.getItems());
		}
	}

	@Test
	public void testGetMeasurementUnitsPageWithSortDateTime() throws Exception {
		testGetMeasurementUnitsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, measurementUnit1, measurementUnit2) -> {
				BeanTestUtil.setProperty(
					measurementUnit1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetMeasurementUnitsPageWithSortDouble() throws Exception {
		testGetMeasurementUnitsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, measurementUnit1, measurementUnit2) -> {
				BeanTestUtil.setProperty(
					measurementUnit1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					measurementUnit2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetMeasurementUnitsPageWithSortInteger() throws Exception {
		testGetMeasurementUnitsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, measurementUnit1, measurementUnit2) -> {
				BeanTestUtil.setProperty(
					measurementUnit1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					measurementUnit2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetMeasurementUnitsPageWithSortString() throws Exception {
		testGetMeasurementUnitsPageWithSort(
			EntityField.Type.STRING,
			(entityField, measurementUnit1, measurementUnit2) -> {
				Class<?> clazz = measurementUnit1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						measurementUnit1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						measurementUnit2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						measurementUnit1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						measurementUnit2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						measurementUnit1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						measurementUnit2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetMeasurementUnitsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, MeasurementUnit, MeasurementUnit, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		MeasurementUnit measurementUnit1 = randomMeasurementUnit();
		MeasurementUnit measurementUnit2 = randomMeasurementUnit();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, measurementUnit1, measurementUnit2);
		}

		measurementUnit1 = testGetMeasurementUnitsPage_addMeasurementUnit(
			measurementUnit1);

		measurementUnit2 = testGetMeasurementUnitsPage_addMeasurementUnit(
			measurementUnit2);

		Page<MeasurementUnit> page =
			measurementUnitResource.getMeasurementUnitsPage(null, null, null);

		for (EntityField entityField : entityFields) {
			Page<MeasurementUnit> ascPage =
				measurementUnitResource.getMeasurementUnitsPage(
					null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				measurementUnit1, (List<MeasurementUnit>)ascPage.getItems());
			assertContains(
				measurementUnit2, (List<MeasurementUnit>)ascPage.getItems());

			Page<MeasurementUnit> descPage =
				measurementUnitResource.getMeasurementUnitsPage(
					null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				measurementUnit2, (List<MeasurementUnit>)descPage.getItems());
			assertContains(
				measurementUnit1, (List<MeasurementUnit>)descPage.getItems());
		}
	}

	protected MeasurementUnit testGetMeasurementUnitsPage_addMeasurementUnit(
			MeasurementUnit measurementUnit)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetMeasurementUnitsPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"measurementUnits",
			new HashMap<String, Object>() {
				{
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject measurementUnitsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/measurementUnits");

		long totalCount = measurementUnitsJSONObject.getLong("totalCount");

		MeasurementUnit measurementUnit1 =
			testGraphQLMeasurementUnit_addMeasurementUnit(
				randomMeasurementUnit());

		MeasurementUnit measurementUnit2 =
			testGraphQLMeasurementUnit_addMeasurementUnit(
				randomMeasurementUnit());

		measurementUnitsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/measurementUnits");

		Assert.assertEquals(
			totalCount + 2, measurementUnitsJSONObject.getLong("totalCount"));

		assertContains(
			measurementUnit1,
			Arrays.asList(
				MeasurementUnitSerDes.toDTOs(
					measurementUnitsJSONObject.getString("items"))));
		assertContains(
			measurementUnit2,
			Arrays.asList(
				MeasurementUnitSerDes.toDTOs(
					measurementUnitsJSONObject.getString("items"))));

		// Using the namespace headlessCommerceAdminSiteSetting_v1_0

		measurementUnitsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminSiteSetting_v1_0", graphQLField)),
			"JSONObject/data",
			"JSONObject/headlessCommerceAdminSiteSetting_v1_0",
			"JSONObject/measurementUnits");

		Assert.assertEquals(
			totalCount + 2, measurementUnitsJSONObject.getLong("totalCount"));

		assertContains(
			measurementUnit1,
			Arrays.asList(
				MeasurementUnitSerDes.toDTOs(
					measurementUnitsJSONObject.getString("items"))));
		assertContains(
			measurementUnit2,
			Arrays.asList(
				MeasurementUnitSerDes.toDTOs(
					measurementUnitsJSONObject.getString("items"))));
	}

	@Test
	public void testPatchMeasurementUnit() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPatchMeasurementUnitByExternalReferenceCode()
		throws Exception {

		Assert.assertTrue(false);
	}

	@Test
	public void testPatchMeasurementUnitByKey() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPostMeasurementUnit() throws Exception {
		MeasurementUnit randomMeasurementUnit = randomMeasurementUnit();

		MeasurementUnit postMeasurementUnit =
			testPostMeasurementUnit_addMeasurementUnit(randomMeasurementUnit);

		assertEquals(randomMeasurementUnit, postMeasurementUnit);
		assertValid(postMeasurementUnit);
	}

	protected MeasurementUnit testPostMeasurementUnit_addMeasurementUnit(
			MeasurementUnit measurementUnit)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostMeasurementUnit() throws Exception {
		MeasurementUnit randomMeasurementUnit = randomMeasurementUnit();

		MeasurementUnit measurementUnit =
			testGraphQLMeasurementUnit_addMeasurementUnit(
				randomMeasurementUnit);

		Assert.assertTrue(equals(randomMeasurementUnit, measurementUnit));
	}

	@Test
	public void testPutMeasurementUnitByExternalReferenceCode()
		throws Exception {

		MeasurementUnit postMeasurementUnit =
			testPutMeasurementUnitByExternalReferenceCode_addMeasurementUnit();

		MeasurementUnit randomMeasurementUnit = randomMeasurementUnit();

		MeasurementUnit putMeasurementUnit =
			measurementUnitResource.putMeasurementUnitByExternalReferenceCode(
				postMeasurementUnit.getExternalReferenceCode(),
				randomMeasurementUnit);

		assertEquals(randomMeasurementUnit, putMeasurementUnit);
		assertValid(putMeasurementUnit);

		MeasurementUnit getMeasurementUnit =
			measurementUnitResource.getMeasurementUnitByExternalReferenceCode(
				putMeasurementUnit.getExternalReferenceCode());

		assertEquals(randomMeasurementUnit, getMeasurementUnit);
		assertValid(getMeasurementUnit);

		MeasurementUnit newMeasurementUnit =
			testPutMeasurementUnitByExternalReferenceCode_createMeasurementUnit();

		putMeasurementUnit =
			measurementUnitResource.putMeasurementUnitByExternalReferenceCode(
				newMeasurementUnit.getExternalReferenceCode(),
				newMeasurementUnit);

		assertEquals(newMeasurementUnit, putMeasurementUnit);
		assertValid(putMeasurementUnit);

		getMeasurementUnit =
			measurementUnitResource.getMeasurementUnitByExternalReferenceCode(
				putMeasurementUnit.getExternalReferenceCode());

		assertEquals(newMeasurementUnit, getMeasurementUnit);

		Assert.assertEquals(
			newMeasurementUnit.getExternalReferenceCode(),
			putMeasurementUnit.getExternalReferenceCode());
	}

	protected MeasurementUnit
			testPutMeasurementUnitByExternalReferenceCode_addMeasurementUnit()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected MeasurementUnit
			testPutMeasurementUnitByExternalReferenceCode_createMeasurementUnit()
		throws Exception {

		return randomMeasurementUnit();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		MeasurementUnit measurementUnit1 =
			testBatchEngineDeleteImportTask_addMeasurementUnit();

		testBatchEngineDeleteImportTask_deleteMeasurementUnit(
			200, measurementUnit1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			measurementUnitResource.getMeasurementUnitHttpResponse(
				measurementUnit1.getId()));

		measurementUnit1 = testBatchEngineDeleteImportTask_addMeasurementUnit();

		testBatchEngineDeleteImportTask_deleteMeasurementUnit(
			200, null, measurementUnit1.getId());

		assertHttpResponseStatusCode(
			404,
			measurementUnitResource.getMeasurementUnitHttpResponse(
				measurementUnit1.getId()));

		measurementUnit1 = testBatchEngineDeleteImportTask_addMeasurementUnit();
		MeasurementUnit measurementUnit2 =
			testBatchEngineDeleteImportTask_addMeasurementUnit();

		testBatchEngineDeleteImportTask_deleteMeasurementUnit(
			200, measurementUnit2.getExternalReferenceCode(),
			measurementUnit1.getId());

		assertHttpResponseStatusCode(
			404,
			measurementUnitResource.getMeasurementUnitHttpResponse(
				measurementUnit1.getId()));
		assertHttpResponseStatusCode(
			200,
			measurementUnitResource.getMeasurementUnitHttpResponse(
				measurementUnit2.getId()));

		testBatchEngineDeleteImportTask_deleteMeasurementUnit(
			200, measurementUnit2.getExternalReferenceCode(),
			measurementUnit1.getId());

		assertHttpResponseStatusCode(
			404,
			measurementUnitResource.getMeasurementUnitHttpResponse(
				measurementUnit2.getId()));
	}

	protected MeasurementUnit
			testBatchEngineDeleteImportTask_addMeasurementUnit()
		throws Exception {

		return testDeleteMeasurementUnit_addMeasurementUnit();
	}

	protected void testBatchEngineDeleteImportTask_deleteMeasurementUnit(
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
				"com.liferay.headless.commerce.admin.site.setting.dto.v1_0.MeasurementUnit",
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

	protected MeasurementUnit testGraphQLMeasurementUnit_addMeasurementUnit()
		throws Exception {

		return testGraphQLMeasurementUnit_addMeasurementUnit(
			randomMeasurementUnit());
	}

	protected MeasurementUnit testGraphQLMeasurementUnit_addMeasurementUnit(
			MeasurementUnit measurementUnit)
		throws Exception {

		JSONDeserializer<MeasurementUnit> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(MeasurementUnit.class)) {

			if (getGraphQLValue(field.get(measurementUnit)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(measurementUnit)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createMeasurementUnit",
						new HashMap<String, Object>() {
							{
								put("measurementUnit", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createMeasurementUnit"),
			MeasurementUnit.class);
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
		MeasurementUnit measurementUnit,
		List<MeasurementUnit> measurementUnits) {

		boolean contains = false;

		for (MeasurementUnit item : measurementUnits) {
			if (equals(measurementUnit, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			measurementUnits + " does not contain " + measurementUnit,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		MeasurementUnit measurementUnit1, MeasurementUnit measurementUnit2) {

		Assert.assertTrue(
			measurementUnit1 + " does not equal " + measurementUnit2,
			equals(measurementUnit1, measurementUnit2));
	}

	protected void assertEquals(
		List<MeasurementUnit> measurementUnits1,
		List<MeasurementUnit> measurementUnits2) {

		Assert.assertEquals(measurementUnits1.size(), measurementUnits2.size());

		for (int i = 0; i < measurementUnits1.size(); i++) {
			MeasurementUnit measurementUnit1 = measurementUnits1.get(i);
			MeasurementUnit measurementUnit2 = measurementUnits2.get(i);

			assertEquals(measurementUnit1, measurementUnit2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<MeasurementUnit> measurementUnits1,
		List<MeasurementUnit> measurementUnits2) {

		Assert.assertEquals(measurementUnits1.size(), measurementUnits2.size());

		for (MeasurementUnit measurementUnit1 : measurementUnits1) {
			boolean contains = false;

			for (MeasurementUnit measurementUnit2 : measurementUnits2) {
				if (equals(measurementUnit1, measurementUnit2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				measurementUnits2 + " does not contain " + measurementUnit1,
				contains);
		}
	}

	protected void assertValid(MeasurementUnit measurementUnit)
		throws Exception {

		boolean valid = true;

		if (measurementUnit.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("companyId", additionalAssertFieldName)) {
				if (measurementUnit.getCompanyId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (measurementUnit.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (measurementUnit.getKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (measurementUnit.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("primary", additionalAssertFieldName)) {
				if (measurementUnit.getPrimary() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (measurementUnit.getPriority() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("rate", additionalAssertFieldName)) {
				if (measurementUnit.getRate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (measurementUnit.getType() == null) {
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

	protected void assertValid(Page<MeasurementUnit> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<MeasurementUnit> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<MeasurementUnit> measurementUnits =
			page.getItems();

		int size = measurementUnits.size();

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
					com.liferay.headless.commerce.admin.site.setting.dto.v1_0.
						MeasurementUnit.class)) {

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
		MeasurementUnit measurementUnit1, MeasurementUnit measurementUnit2) {

		if (measurementUnit1 == measurementUnit2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("companyId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						measurementUnit1.getCompanyId(),
						measurementUnit2.getCompanyId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						measurementUnit1.getExternalReferenceCode(),
						measurementUnit2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						measurementUnit1.getId(), measurementUnit2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						measurementUnit1.getKey(), measurementUnit2.getKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!equals(
						(Map)measurementUnit1.getName(),
						(Map)measurementUnit2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("primary", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						measurementUnit1.getPrimary(),
						measurementUnit2.getPrimary())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						measurementUnit1.getPriority(),
						measurementUnit2.getPriority())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("rate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						measurementUnit1.getRate(),
						measurementUnit2.getRate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						measurementUnit1.getType(),
						measurementUnit2.getType())) {

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

		if (!(_measurementUnitResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_measurementUnitResource;

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
		MeasurementUnit measurementUnit) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("companyId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = measurementUnit.getExternalReferenceCode();

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

		if (entityFieldName.equals("key")) {
			Object object = measurementUnit.getKey();

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

		if (entityFieldName.equals("name")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("primary")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priority")) {
			sb.append(String.valueOf(measurementUnit.getPriority()));

			return sb.toString();
		}

		if (entityFieldName.equals("rate")) {
			sb.append(String.valueOf(measurementUnit.getRate()));

			return sb.toString();
		}

		if (entityFieldName.equals("type")) {
			Object object = measurementUnit.getType();

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

	protected MeasurementUnit randomMeasurementUnit() throws Exception {
		return new MeasurementUnit() {
			{
				companyId = RandomTestUtil.randomLong();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				primary = RandomTestUtil.randomBoolean();
				priority = RandomTestUtil.randomDouble();
				rate = RandomTestUtil.randomDouble();
				type = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected MeasurementUnit randomIrrelevantMeasurementUnit()
		throws Exception {

		MeasurementUnit randomIrrelevantMeasurementUnit =
			randomMeasurementUnit();

		return randomIrrelevantMeasurementUnit;
	}

	protected MeasurementUnit randomPatchMeasurementUnit() throws Exception {
		return randomMeasurementUnit();
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

	protected MeasurementUnitResource measurementUnitResource;
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
		LogFactoryUtil.getLog(BaseMeasurementUnitResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.site.setting.resource.v1_0.
		MeasurementUnitResource _measurementUnitResource;

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