/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.resource.v1_0.test;

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
import com.liferay.search.experiences.rest.client.dto.v1_0.Field;
import com.liferay.search.experiences.rest.client.dto.v1_0.SXPElement;
import com.liferay.search.experiences.rest.client.http.HttpInvoker;
import com.liferay.search.experiences.rest.client.pagination.Page;
import com.liferay.search.experiences.rest.client.pagination.Pagination;
import com.liferay.search.experiences.rest.client.resource.v1_0.SXPElementResource;
import com.liferay.search.experiences.rest.client.serdes.v1_0.SXPElementSerDes;

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
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public abstract class BaseSXPElementResourceTestCase {

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

		_sxpElementResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		sxpElementResource = SXPElementResource.builder(
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

		SXPElement sxpElement1 = randomSXPElement();

		String json = objectMapper.writeValueAsString(sxpElement1);

		SXPElement sxpElement2 = SXPElementSerDes.toDTO(json);

		Assert.assertTrue(equals(sxpElement1, sxpElement2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		SXPElement sxpElement = randomSXPElement();

		String json1 = objectMapper.writeValueAsString(sxpElement);
		String json2 = SXPElementSerDes.toJSON(sxpElement);

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

		SXPElement sxpElement = randomSXPElement();

		sxpElement.setDescription(regex);
		sxpElement.setExternalReferenceCode(regex);
		sxpElement.setFallbackDescription(regex);
		sxpElement.setFallbackTitle(regex);
		sxpElement.setSchemaVersion(regex);
		sxpElement.setTitle(regex);
		sxpElement.setUserName(regex);
		sxpElement.setVersion(regex);

		String json = SXPElementSerDes.toJSON(sxpElement);

		Assert.assertFalse(json.contains(regex));

		sxpElement = SXPElementSerDes.toDTO(json);

		Assert.assertEquals(regex, sxpElement.getDescription());
		Assert.assertEquals(regex, sxpElement.getExternalReferenceCode());
		Assert.assertEquals(regex, sxpElement.getFallbackDescription());
		Assert.assertEquals(regex, sxpElement.getFallbackTitle());
		Assert.assertEquals(regex, sxpElement.getSchemaVersion());
		Assert.assertEquals(regex, sxpElement.getTitle());
		Assert.assertEquals(regex, sxpElement.getUserName());
		Assert.assertEquals(regex, sxpElement.getVersion());
	}

	@Test
	public void testDeleteSXPElement() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		SXPElement sxpElement = testDeleteSXPElement_addSXPElement();

		assertHttpResponseStatusCode(
			204,
			sxpElementResource.deleteSXPElementHttpResponse(
				sxpElement.getId()));

		assertHttpResponseStatusCode(
			404,
			sxpElementResource.getSXPElementHttpResponse(sxpElement.getId()));
		assertHttpResponseStatusCode(
			404, sxpElementResource.getSXPElementHttpResponse(0L));
	}

	protected SXPElement testDeleteSXPElement_addSXPElement() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteSXPElement() throws Exception {

		// No namespace

		SXPElement sxpElement1 = testGraphQLDeleteSXPElement_addSXPElement();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSXPElement",
						new HashMap<String, Object>() {
							{
								put("sxpElementId", sxpElement1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteSXPElement"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"sXPElement",
					new HashMap<String, Object>() {
						{
							put("sxpElementId", sxpElement1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace searchExperiences_v1_0

		SXPElement sxpElement2 = testGraphQLDeleteSXPElement_addSXPElement();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"searchExperiences_v1_0",
						new GraphQLField(
							"deleteSXPElement",
							new HashMap<String, Object>() {
								{
									put("sxpElementId", sxpElement2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/searchExperiences_v1_0",
				"Object/deleteSXPElement"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"searchExperiences_v1_0",
					new GraphQLField(
						"sXPElement",
						new HashMap<String, Object>() {
							{
								put("sxpElementId", sxpElement2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected SXPElement testGraphQLDeleteSXPElement_addSXPElement()
		throws Exception {

		return testGraphQLSXPElement_addSXPElement();
	}

	@Test
	public void testDeleteSXPElementBatch() throws Exception {
		SXPElement sxpElement1 = testDeleteSXPElementBatch_addSXPElement();

		testDeleteSXPElementBatch_deleteSXPElement(
			202, null, sxpElement1.getId());

		assertHttpResponseStatusCode(
			404,
			sxpElementResource.getSXPElementHttpResponse(sxpElement1.getId()));
	}

	protected SXPElement testDeleteSXPElementBatch_addSXPElement()
		throws Exception {

		return testDeleteSXPElement_addSXPElement();
	}

	protected void testDeleteSXPElementBatch_deleteSXPElement(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			sxpElementResource.deleteSXPElementBatchHttpResponse(
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
	public void testGetSXPElement() throws Exception {
		SXPElement postSXPElement = testGetSXPElement_addSXPElement();

		SXPElement getSXPElement = sxpElementResource.getSXPElement(
			postSXPElement.getId());

		assertEquals(postSXPElement, getSXPElement);
		assertValid(getSXPElement);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		SXPElement postSXPElement = testGetSXPElement_addSXPElement();

		SXPElement getSXPElement = sxpElementResource.getSXPElement(
			postSXPElement.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.search.experiences.rest.dto.v1_0.SXPElement"
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

		Object item = vulcanCRUDItemDelegate.getItem(postSXPElement.getId());

		assertEquals(getSXPElement, SXPElementSerDes.toDTO(item.toString()));
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

	protected SXPElement testGetSXPElement_addSXPElement() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSXPElement() throws Exception {
		SXPElement sxpElement = testGraphQLGetSXPElement_addSXPElement();

		// No namespace

		Assert.assertTrue(
			equals(
				sxpElement,
				SXPElementSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"sXPElement",
								new HashMap<String, Object>() {
									{
										put("sxpElementId", sxpElement.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/sXPElement"))));

		// Using the namespace searchExperiences_v1_0

		Assert.assertTrue(
			equals(
				sxpElement,
				SXPElementSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"searchExperiences_v1_0",
								new GraphQLField(
									"sXPElement",
									new HashMap<String, Object>() {
										{
											put(
												"sxpElementId",
												sxpElement.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/searchExperiences_v1_0",
						"Object/sXPElement"))));
	}

	@Test
	public void testGraphQLGetSXPElementNotFound() throws Exception {
		Long irrelevantSxpElementId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"sXPElement",
						new HashMap<String, Object>() {
							{
								put("sxpElementId", irrelevantSxpElementId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace searchExperiences_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"searchExperiences_v1_0",
						new GraphQLField(
							"sXPElement",
							new HashMap<String, Object>() {
								{
									put("sxpElementId", irrelevantSxpElementId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected SXPElement testGraphQLGetSXPElement_addSXPElement()
		throws Exception {

		return testGraphQLSXPElement_addSXPElement();
	}

	@Test
	public void testGetSXPElementByExternalReferenceCode() throws Exception {
		SXPElement postSXPElement =
			testGetSXPElementByExternalReferenceCode_addSXPElement();

		SXPElement getSXPElement =
			sxpElementResource.getSXPElementByExternalReferenceCode(
				postSXPElement.getExternalReferenceCode());

		assertEquals(postSXPElement, getSXPElement);
		assertValid(getSXPElement);
	}

	protected SXPElement
			testGetSXPElementByExternalReferenceCode_addSXPElement()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSXPElementByExternalReferenceCode()
		throws Exception {

		SXPElement sxpElement =
			testGraphQLGetSXPElementByExternalReferenceCode_addSXPElement();

		// No namespace

		Assert.assertTrue(
			equals(
				sxpElement,
				SXPElementSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"sXPElementByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												sxpElement.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/sXPElementByExternalReferenceCode"))));

		// Using the namespace searchExperiences_v1_0

		Assert.assertTrue(
			equals(
				sxpElement,
				SXPElementSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"searchExperiences_v1_0",
								new GraphQLField(
									"sXPElementByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													sxpElement.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/searchExperiences_v1_0",
						"Object/sXPElementByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetSXPElementByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"sXPElementByExternalReferenceCode",
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

		// Using the namespace searchExperiences_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"searchExperiences_v1_0",
						new GraphQLField(
							"sXPElementByExternalReferenceCode",
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

	protected SXPElement
			testGraphQLGetSXPElementByExternalReferenceCode_addSXPElement()
		throws Exception {

		return testGraphQLSXPElement_addSXPElement();
	}

	@Test
	public void testGetSXPElementExport() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testGetSXPElementsPage() throws Exception {
		Page<SXPElement> page = sxpElementResource.getSXPElementsPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		SXPElement sxpElement1 = testGetSXPElementsPage_addSXPElement(
			randomSXPElement());

		SXPElement sxpElement2 = testGetSXPElementsPage_addSXPElement(
			randomSXPElement());

		page = sxpElementResource.getSXPElementsPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(sxpElement1, (List<SXPElement>)page.getItems());
		assertContains(sxpElement2, (List<SXPElement>)page.getItems());
		assertValid(page, testGetSXPElementsPage_getExpectedActions());

		sxpElementResource.deleteSXPElement(sxpElement1.getId());

		sxpElementResource.deleteSXPElement(sxpElement2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSXPElementsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSXPElementsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		SXPElement sxpElement1 = randomSXPElement();

		sxpElement1 = testGetSXPElementsPage_addSXPElement(sxpElement1);

		for (EntityField entityField : entityFields) {
			Page<SXPElement> page = sxpElementResource.getSXPElementsPage(
				null, getFilterString(entityField, "between", sxpElement1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(sxpElement1),
				(List<SXPElement>)page.getItems());
		}
	}

	@Test
	public void testGetSXPElementsPageWithFilterDoubleEquals()
		throws Exception {

		testGetSXPElementsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSXPElementsPageWithFilterStringContains()
		throws Exception {

		testGetSXPElementsPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSXPElementsPageWithFilterStringEquals()
		throws Exception {

		testGetSXPElementsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSXPElementsPageWithFilterStringStartsWith()
		throws Exception {

		testGetSXPElementsPageWithFilter("startswith", EntityField.Type.STRING);
	}

	protected void testGetSXPElementsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		SXPElement sxpElement1 = testGetSXPElementsPage_addSXPElement(
			randomSXPElement());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		SXPElement sxpElement2 = testGetSXPElementsPage_addSXPElement(
			randomSXPElement());

		for (EntityField entityField : entityFields) {
			Page<SXPElement> page = sxpElementResource.getSXPElementsPage(
				null, getFilterString(entityField, operator, sxpElement1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(sxpElement1),
				(List<SXPElement>)page.getItems());
		}
	}

	@Test
	public void testGetSXPElementsPageWithPagination() throws Exception {
		Page<SXPElement> sxpElementsPage =
			sxpElementResource.getSXPElementsPage(null, null, null, null);

		int totalCount = GetterUtil.getInteger(sxpElementsPage.getTotalCount());

		SXPElement sxpElement1 = testGetSXPElementsPage_addSXPElement(
			randomSXPElement());

		SXPElement sxpElement2 = testGetSXPElementsPage_addSXPElement(
			randomSXPElement());

		SXPElement sxpElement3 = testGetSXPElementsPage_addSXPElement(
			randomSXPElement());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<SXPElement> page1 = sxpElementResource.getSXPElementsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(sxpElement1, (List<SXPElement>)page1.getItems());

			Page<SXPElement> page2 = sxpElementResource.getSXPElementsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(sxpElement2, (List<SXPElement>)page2.getItems());

			Page<SXPElement> page3 = sxpElementResource.getSXPElementsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(sxpElement3, (List<SXPElement>)page3.getItems());
		}
		else {
			Page<SXPElement> page1 = sxpElementResource.getSXPElementsPage(
				null, null, Pagination.of(1, totalCount + 2), null);

			List<SXPElement> sxpElements1 = (List<SXPElement>)page1.getItems();

			Assert.assertEquals(
				sxpElements1.toString(), totalCount + 2, sxpElements1.size());

			Page<SXPElement> page2 = sxpElementResource.getSXPElementsPage(
				null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<SXPElement> sxpElements2 = (List<SXPElement>)page2.getItems();

			Assert.assertEquals(
				sxpElements2.toString(), 1, sxpElements2.size());

			Page<SXPElement> page3 = sxpElementResource.getSXPElementsPage(
				null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(sxpElement1, (List<SXPElement>)page3.getItems());
			assertContains(sxpElement2, (List<SXPElement>)page3.getItems());
			assertContains(sxpElement3, (List<SXPElement>)page3.getItems());
		}
	}

	@Test
	public void testGetSXPElementsPageWithSortDateTime() throws Exception {
		testGetSXPElementsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, sxpElement1, sxpElement2) -> {
				BeanTestUtil.setProperty(
					sxpElement1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSXPElementsPageWithSortDouble() throws Exception {
		testGetSXPElementsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, sxpElement1, sxpElement2) -> {
				BeanTestUtil.setProperty(
					sxpElement1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					sxpElement2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSXPElementsPageWithSortInteger() throws Exception {
		testGetSXPElementsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, sxpElement1, sxpElement2) -> {
				BeanTestUtil.setProperty(sxpElement1, entityField.getName(), 0);
				BeanTestUtil.setProperty(sxpElement2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSXPElementsPageWithSortString() throws Exception {
		testGetSXPElementsPageWithSort(
			EntityField.Type.STRING,
			(entityField, sxpElement1, sxpElement2) -> {
				Class<?> clazz = sxpElement1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						sxpElement1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						sxpElement2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						sxpElement1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						sxpElement2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						sxpElement1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						sxpElement2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSXPElementsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, SXPElement, SXPElement, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		SXPElement sxpElement1 = randomSXPElement();
		SXPElement sxpElement2 = randomSXPElement();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, sxpElement1, sxpElement2);
		}

		sxpElement1 = testGetSXPElementsPage_addSXPElement(sxpElement1);

		sxpElement2 = testGetSXPElementsPage_addSXPElement(sxpElement2);

		Page<SXPElement> page = sxpElementResource.getSXPElementsPage(
			null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<SXPElement> ascPage = sxpElementResource.getSXPElementsPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(sxpElement1, (List<SXPElement>)ascPage.getItems());
			assertContains(sxpElement2, (List<SXPElement>)ascPage.getItems());

			Page<SXPElement> descPage = sxpElementResource.getSXPElementsPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(sxpElement2, (List<SXPElement>)descPage.getItems());
			assertContains(sxpElement1, (List<SXPElement>)descPage.getItems());
		}
	}

	protected SXPElement testGetSXPElementsPage_addSXPElement(
			SXPElement sxpElement)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSXPElementsPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"sXPElements",
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

		JSONObject sXPElementsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/sXPElements");

		long totalCount = sXPElementsJSONObject.getLong("totalCount");

		SXPElement sxpElement1 = testGraphQLSXPElement_addSXPElement(
			randomSXPElement());

		SXPElement sxpElement2 = testGraphQLSXPElement_addSXPElement(
			randomSXPElement());

		sXPElementsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/sXPElements");

		Assert.assertEquals(
			totalCount + 2, sXPElementsJSONObject.getLong("totalCount"));

		assertContains(
			sxpElement1,
			Arrays.asList(
				SXPElementSerDes.toDTOs(
					sXPElementsJSONObject.getString("items"))));
		assertContains(
			sxpElement2,
			Arrays.asList(
				SXPElementSerDes.toDTOs(
					sXPElementsJSONObject.getString("items"))));

		// Using the namespace searchExperiences_v1_0

		sXPElementsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("searchExperiences_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/searchExperiences_v1_0",
			"JSONObject/sXPElements");

		Assert.assertEquals(
			totalCount + 2, sXPElementsJSONObject.getLong("totalCount"));

		assertContains(
			sxpElement1,
			Arrays.asList(
				SXPElementSerDes.toDTOs(
					sXPElementsJSONObject.getString("items"))));
		assertContains(
			sxpElement2,
			Arrays.asList(
				SXPElementSerDes.toDTOs(
					sXPElementsJSONObject.getString("items"))));
	}

	@Test
	public void testPatchSXPElement() throws Exception {
		SXPElement postSXPElement = testPatchSXPElement_addSXPElement();

		SXPElement randomPatchSXPElement = randomPatchSXPElement();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		SXPElement patchSXPElement = sxpElementResource.patchSXPElement(
			postSXPElement.getId(), randomPatchSXPElement);

		SXPElement expectedPatchSXPElement = postSXPElement.clone();

		BeanTestUtil.copyProperties(
			randomPatchSXPElement, expectedPatchSXPElement);

		SXPElement getSXPElement = sxpElementResource.getSXPElement(
			patchSXPElement.getId());

		assertEquals(expectedPatchSXPElement, getSXPElement);
		assertValid(getSXPElement);
	}

	protected SXPElement testPatchSXPElement_addSXPElement() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSXPElement() throws Exception {
		SXPElement randomSXPElement = randomSXPElement();

		SXPElement postSXPElement = testPostSXPElement_addSXPElement(
			randomSXPElement);

		assertEquals(randomSXPElement, postSXPElement);
		assertValid(postSXPElement);
	}

	protected SXPElement testPostSXPElement_addSXPElement(SXPElement sxpElement)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostSXPElement() throws Exception {
		SXPElement randomSXPElement = randomSXPElement();

		SXPElement sxpElement = testGraphQLSXPElement_addSXPElement(
			randomSXPElement);

		Assert.assertTrue(equals(randomSXPElement, sxpElement));
	}

	@Test
	public void testPostSXPElementCopy() throws Exception {
		SXPElement randomSXPElement = randomSXPElement();

		SXPElement postSXPElement = testPostSXPElementCopy_addSXPElement(
			randomSXPElement);

		assertEquals(randomSXPElement, postSXPElement);
		assertValid(postSXPElement);
	}

	protected SXPElement testPostSXPElementCopy_addSXPElement(
			SXPElement sxpElement)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostSXPElementCopy() throws Exception {
		SXPElement randomSXPElement = randomSXPElement();

		SXPElement sxpElement = testGraphQLSXPElement_addSXPElement(
			randomSXPElement);

		Assert.assertTrue(equals(randomSXPElement, sxpElement));
	}

	@Test
	public void testPostSXPElementPreview() throws Exception {
		SXPElement randomSXPElement = randomSXPElement();

		SXPElement postSXPElement = testPostSXPElementPreview_addSXPElement(
			randomSXPElement);

		assertEquals(randomSXPElement, postSXPElement);
		assertValid(postSXPElement);
	}

	protected SXPElement testPostSXPElementPreview_addSXPElement(
			SXPElement sxpElement)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostSXPElementPreview() throws Exception {
		SXPElement randomSXPElement = randomSXPElement();

		SXPElement sxpElement = testGraphQLSXPElement_addSXPElement(
			randomSXPElement);

		Assert.assertTrue(equals(randomSXPElement, sxpElement));
	}

	@Test
	public void testPostSXPElementValidate() throws Exception {
		SXPElement randomSXPElement = randomSXPElement();

		SXPElement postSXPElement = testPostSXPElementValidate_addSXPElement(
			randomSXPElement);

		assertEquals(randomSXPElement, postSXPElement);
		assertValid(postSXPElement);
	}

	protected SXPElement testPostSXPElementValidate_addSXPElement(
			SXPElement sxpElement)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostSXPElementValidate() throws Exception {
		SXPElement randomSXPElement = randomSXPElement();

		SXPElement sxpElement = testGraphQLSXPElement_addSXPElement(
			randomSXPElement);

		Assert.assertTrue(equals(randomSXPElement, sxpElement));
	}

	@Test
	public void testPutSXPElement() throws Exception {
		SXPElement postSXPElement = testPutSXPElement_addSXPElement();

		SXPElement randomSXPElement = randomSXPElement();

		SXPElement putSXPElement = sxpElementResource.putSXPElement(
			postSXPElement.getId(), randomSXPElement);

		assertEquals(randomSXPElement, putSXPElement);
		assertValid(putSXPElement);

		SXPElement getSXPElement = sxpElementResource.getSXPElement(
			putSXPElement.getId());

		assertEquals(randomSXPElement, getSXPElement);
		assertValid(getSXPElement);
	}

	protected SXPElement testPutSXPElement_addSXPElement() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutSXPElementByExternalReferenceCode() throws Exception {
		SXPElement postSXPElement =
			testPutSXPElementByExternalReferenceCode_addSXPElement();

		SXPElement randomSXPElement = randomSXPElement();

		SXPElement putSXPElement =
			sxpElementResource.putSXPElementByExternalReferenceCode(
				postSXPElement.getExternalReferenceCode(), randomSXPElement);

		assertEquals(randomSXPElement, putSXPElement);
		assertValid(putSXPElement);

		SXPElement getSXPElement =
			sxpElementResource.getSXPElementByExternalReferenceCode(
				putSXPElement.getExternalReferenceCode());

		assertEquals(randomSXPElement, getSXPElement);
		assertValid(getSXPElement);

		SXPElement newSXPElement =
			testPutSXPElementByExternalReferenceCode_createSXPElement();

		putSXPElement = sxpElementResource.putSXPElementByExternalReferenceCode(
			newSXPElement.getExternalReferenceCode(), newSXPElement);

		assertEquals(newSXPElement, putSXPElement);
		assertValid(putSXPElement);

		getSXPElement = sxpElementResource.getSXPElementByExternalReferenceCode(
			putSXPElement.getExternalReferenceCode());

		assertEquals(newSXPElement, getSXPElement);

		Assert.assertEquals(
			newSXPElement.getExternalReferenceCode(),
			putSXPElement.getExternalReferenceCode());
	}

	protected SXPElement
			testPutSXPElementByExternalReferenceCode_addSXPElement()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected SXPElement
			testPutSXPElementByExternalReferenceCode_createSXPElement()
		throws Exception {

		return randomSXPElement();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		SXPElement sxpElement1 =
			testBatchEngineDeleteImportTask_addSXPElement();

		testBatchEngineDeleteImportTask_deleteSXPElement(
			200, null, sxpElement1.getId());

		assertHttpResponseStatusCode(
			404,
			sxpElementResource.getSXPElementHttpResponse(sxpElement1.getId()));
	}

	protected SXPElement testBatchEngineDeleteImportTask_addSXPElement()
		throws Exception {

		return testDeleteSXPElement_addSXPElement();
	}

	protected void testBatchEngineDeleteImportTask_deleteSXPElement(
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
				"com.liferay.search.experiences.rest.dto.v1_0.SXPElement", null,
				null, null, null,
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

	protected SXPElement testGraphQLSXPElement_addSXPElement()
		throws Exception {

		return testGraphQLSXPElement_addSXPElement(randomSXPElement());
	}

	protected SXPElement testGraphQLSXPElement_addSXPElement(
			SXPElement sxpElement)
		throws Exception {

		JSONDeserializer<SXPElement> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(SXPElement.class)) {

			if (getGraphQLValue(field.get(sxpElement)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(sxpElement)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSXPElement",
						new HashMap<String, Object>() {
							{
								put("sxpElement", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSXPElement"),
			SXPElement.class);
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
		SXPElement sxpElement, List<SXPElement> sxpElements) {

		boolean contains = false;

		for (SXPElement item : sxpElements) {
			if (equals(sxpElement, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			sxpElements + " does not contain " + sxpElement, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		SXPElement sxpElement1, SXPElement sxpElement2) {

		Assert.assertTrue(
			sxpElement1 + " does not equal " + sxpElement2,
			equals(sxpElement1, sxpElement2));
	}

	protected void assertEquals(
		List<SXPElement> sxpElements1, List<SXPElement> sxpElements2) {

		Assert.assertEquals(sxpElements1.size(), sxpElements2.size());

		for (int i = 0; i < sxpElements1.size(); i++) {
			SXPElement sxpElement1 = sxpElements1.get(i);
			SXPElement sxpElement2 = sxpElements2.get(i);

			assertEquals(sxpElement1, sxpElement2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<SXPElement> sxpElements1, List<SXPElement> sxpElements2) {

		Assert.assertEquals(sxpElements1.size(), sxpElements2.size());

		for (SXPElement sxpElement1 : sxpElements1) {
			boolean contains = false;

			for (SXPElement sxpElement2 : sxpElements2) {
				if (equals(sxpElement1, sxpElement2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				sxpElements2 + " does not contain " + sxpElement1, contains);
		}
	}

	protected void assertValid(SXPElement sxpElement) throws Exception {
		boolean valid = true;

		if (sxpElement.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (sxpElement.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (sxpElement.getCreateDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (sxpElement.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (sxpElement.getDescription_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"elementDefinition", additionalAssertFieldName)) {

				if (sxpElement.getElementDefinition() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (sxpElement.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"fallbackDescription", additionalAssertFieldName)) {

				if (sxpElement.getFallbackDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("fallbackTitle", additionalAssertFieldName)) {
				if (sxpElement.getFallbackTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("hidden", additionalAssertFieldName)) {
				if (sxpElement.getHidden() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("modifiedDate", additionalAssertFieldName)) {
				if (sxpElement.getModifiedDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("readOnly", additionalAssertFieldName)) {
				if (sxpElement.getReadOnly() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("schemaVersion", additionalAssertFieldName)) {
				if (sxpElement.getSchemaVersion() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (sxpElement.getTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title_i18n", additionalAssertFieldName)) {
				if (sxpElement.getTitle_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (sxpElement.getType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("userName", additionalAssertFieldName)) {
				if (sxpElement.getUserName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("version", additionalAssertFieldName)) {
				if (sxpElement.getVersion() == null) {
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

	protected void assertValid(Page<SXPElement> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<SXPElement> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<SXPElement> sxpElements = page.getItems();

		int size = sxpElements.size();

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
					com.liferay.search.experiences.rest.dto.v1_0.SXPElement.
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

	protected boolean equals(SXPElement sxpElement1, SXPElement sxpElement2) {
		if (sxpElement1 == sxpElement2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)sxpElement1.getActions(),
						(Map)sxpElement2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sxpElement1.getCreateDate(),
						sxpElement2.getCreateDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sxpElement1.getDescription(),
						sxpElement2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)sxpElement1.getDescription_i18n(),
						(Map)sxpElement2.getDescription_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"elementDefinition", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sxpElement1.getElementDefinition(),
						sxpElement2.getElementDefinition())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sxpElement1.getExternalReferenceCode(),
						sxpElement2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"fallbackDescription", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sxpElement1.getFallbackDescription(),
						sxpElement2.getFallbackDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("fallbackTitle", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sxpElement1.getFallbackTitle(),
						sxpElement2.getFallbackTitle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("hidden", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sxpElement1.getHidden(), sxpElement2.getHidden())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sxpElement1.getId(), sxpElement2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("modifiedDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sxpElement1.getModifiedDate(),
						sxpElement2.getModifiedDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("readOnly", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sxpElement1.getReadOnly(), sxpElement2.getReadOnly())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("schemaVersion", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sxpElement1.getSchemaVersion(),
						sxpElement2.getSchemaVersion())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sxpElement1.getTitle(), sxpElement2.getTitle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)sxpElement1.getTitle_i18n(),
						(Map)sxpElement2.getTitle_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sxpElement1.getType(), sxpElement2.getType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("userName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sxpElement1.getUserName(), sxpElement2.getUserName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("version", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sxpElement1.getVersion(), sxpElement2.getVersion())) {

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

		if (!(_sxpElementResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_sxpElementResource;

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
		EntityField entityField, String operator, SXPElement sxpElement) {

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

		if (entityFieldName.equals("createDate")) {
			if (operator.equals("between")) {
				Date date = sxpElement.getCreateDate();

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

				sb.append(_format.format(sxpElement.getCreateDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = sxpElement.getDescription();

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

		if (entityFieldName.equals("description_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("elementDefinition")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = sxpElement.getExternalReferenceCode();

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

		if (entityFieldName.equals("fallbackDescription")) {
			Object object = sxpElement.getFallbackDescription();

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

		if (entityFieldName.equals("fallbackTitle")) {
			Object object = sxpElement.getFallbackTitle();

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

		if (entityFieldName.equals("hidden")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("modifiedDate")) {
			if (operator.equals("between")) {
				Date date = sxpElement.getModifiedDate();

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

				sb.append(_format.format(sxpElement.getModifiedDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("readOnly")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("schemaVersion")) {
			Object object = sxpElement.getSchemaVersion();

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
			Object object = sxpElement.getTitle();

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

		if (entityFieldName.equals("title_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("type")) {
			sb.append(String.valueOf(sxpElement.getType()));

			return sb.toString();
		}

		if (entityFieldName.equals("userName")) {
			Object object = sxpElement.getUserName();

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

		if (entityFieldName.equals("version")) {
			Object object = sxpElement.getVersion();

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

	protected SXPElement randomSXPElement() throws Exception {
		return new SXPElement() {
			{
				createDate = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				fallbackDescription = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				fallbackTitle = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				hidden = RandomTestUtil.randomBoolean();
				id = RandomTestUtil.randomLong();
				modifiedDate = RandomTestUtil.nextDate();
				readOnly = RandomTestUtil.randomBoolean();
				schemaVersion = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
				type = RandomTestUtil.randomInt();
				userName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				version = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected SXPElement randomIrrelevantSXPElement() throws Exception {
		SXPElement randomIrrelevantSXPElement = randomSXPElement();

		return randomIrrelevantSXPElement;
	}

	protected SXPElement randomPatchSXPElement() throws Exception {
		return randomSXPElement();
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

	protected SXPElementResource sxpElementResource;
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
		LogFactoryUtil.getLog(BaseSXPElementResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.search.experiences.rest.resource.v1_0.SXPElementResource
		_sxpElementResource;

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