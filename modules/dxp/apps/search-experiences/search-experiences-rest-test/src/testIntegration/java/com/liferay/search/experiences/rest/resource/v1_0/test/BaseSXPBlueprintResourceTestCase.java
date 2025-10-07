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
import com.liferay.search.experiences.rest.client.dto.v1_0.SXPBlueprint;
import com.liferay.search.experiences.rest.client.http.HttpInvoker;
import com.liferay.search.experiences.rest.client.pagination.Page;
import com.liferay.search.experiences.rest.client.pagination.Pagination;
import com.liferay.search.experiences.rest.client.resource.v1_0.SXPBlueprintResource;
import com.liferay.search.experiences.rest.client.serdes.v1_0.SXPBlueprintSerDes;

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
public abstract class BaseSXPBlueprintResourceTestCase {

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

		_sxpBlueprintResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		sxpBlueprintResource = SXPBlueprintResource.builder(
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

		SXPBlueprint sxpBlueprint1 = randomSXPBlueprint();

		String json = objectMapper.writeValueAsString(sxpBlueprint1);

		SXPBlueprint sxpBlueprint2 = SXPBlueprintSerDes.toDTO(json);

		Assert.assertTrue(equals(sxpBlueprint1, sxpBlueprint2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		SXPBlueprint sxpBlueprint = randomSXPBlueprint();

		String json1 = objectMapper.writeValueAsString(sxpBlueprint);
		String json2 = SXPBlueprintSerDes.toJSON(sxpBlueprint);

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

		SXPBlueprint sxpBlueprint = randomSXPBlueprint();

		sxpBlueprint.setCollectionProviderSubtypeName(regex);
		sxpBlueprint.setCollectionProviderTypeName(regex);
		sxpBlueprint.setDescription(regex);
		sxpBlueprint.setExternalReferenceCode(regex);
		sxpBlueprint.setSchemaVersion(regex);
		sxpBlueprint.setTitle(regex);
		sxpBlueprint.setUserName(regex);
		sxpBlueprint.setVersion(regex);

		String json = SXPBlueprintSerDes.toJSON(sxpBlueprint);

		Assert.assertFalse(json.contains(regex));

		sxpBlueprint = SXPBlueprintSerDes.toDTO(json);

		Assert.assertEquals(
			regex, sxpBlueprint.getCollectionProviderSubtypeName());
		Assert.assertEquals(
			regex, sxpBlueprint.getCollectionProviderTypeName());
		Assert.assertEquals(regex, sxpBlueprint.getDescription());
		Assert.assertEquals(regex, sxpBlueprint.getExternalReferenceCode());
		Assert.assertEquals(regex, sxpBlueprint.getSchemaVersion());
		Assert.assertEquals(regex, sxpBlueprint.getTitle());
		Assert.assertEquals(regex, sxpBlueprint.getUserName());
		Assert.assertEquals(regex, sxpBlueprint.getVersion());
	}

	@Test
	public void testDeleteSXPBlueprint() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		SXPBlueprint sxpBlueprint = testDeleteSXPBlueprint_addSXPBlueprint();

		assertHttpResponseStatusCode(
			204,
			sxpBlueprintResource.deleteSXPBlueprintHttpResponse(
				sxpBlueprint.getId()));

		assertHttpResponseStatusCode(
			404,
			sxpBlueprintResource.getSXPBlueprintHttpResponse(
				sxpBlueprint.getId()));
		assertHttpResponseStatusCode(
			404, sxpBlueprintResource.getSXPBlueprintHttpResponse(0L));
	}

	protected SXPBlueprint testDeleteSXPBlueprint_addSXPBlueprint()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteSXPBlueprint() throws Exception {

		// No namespace

		SXPBlueprint sxpBlueprint1 =
			testGraphQLDeleteSXPBlueprint_addSXPBlueprint();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSXPBlueprint",
						new HashMap<String, Object>() {
							{
								put("sxpBlueprintId", sxpBlueprint1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteSXPBlueprint"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"sXPBlueprint",
					new HashMap<String, Object>() {
						{
							put("sxpBlueprintId", sxpBlueprint1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace searchExperiences_v1_0

		SXPBlueprint sxpBlueprint2 =
			testGraphQLDeleteSXPBlueprint_addSXPBlueprint();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"searchExperiences_v1_0",
						new GraphQLField(
							"deleteSXPBlueprint",
							new HashMap<String, Object>() {
								{
									put(
										"sxpBlueprintId",
										sxpBlueprint2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/searchExperiences_v1_0",
				"Object/deleteSXPBlueprint"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"searchExperiences_v1_0",
					new GraphQLField(
						"sXPBlueprint",
						new HashMap<String, Object>() {
							{
								put("sxpBlueprintId", sxpBlueprint2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected SXPBlueprint testGraphQLDeleteSXPBlueprint_addSXPBlueprint()
		throws Exception {

		return testGraphQLSXPBlueprint_addSXPBlueprint();
	}

	@Test
	public void testDeleteSXPBlueprintBatch() throws Exception {
		SXPBlueprint sxpBlueprint1 =
			testDeleteSXPBlueprintBatch_addSXPBlueprint();

		testDeleteSXPBlueprintBatch_deleteSXPBlueprint(
			202, null, sxpBlueprint1.getId());

		assertHttpResponseStatusCode(
			404,
			sxpBlueprintResource.getSXPBlueprintHttpResponse(
				sxpBlueprint1.getId()));
	}

	protected SXPBlueprint testDeleteSXPBlueprintBatch_addSXPBlueprint()
		throws Exception {

		return testDeleteSXPBlueprint_addSXPBlueprint();
	}

	protected void testDeleteSXPBlueprintBatch_deleteSXPBlueprint(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			sxpBlueprintResource.deleteSXPBlueprintBatchHttpResponse(
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
	public void testGetSXPBlueprint() throws Exception {
		SXPBlueprint postSXPBlueprint = testGetSXPBlueprint_addSXPBlueprint();

		SXPBlueprint getSXPBlueprint = sxpBlueprintResource.getSXPBlueprint(
			postSXPBlueprint.getId());

		assertEquals(postSXPBlueprint, getSXPBlueprint);
		assertValid(getSXPBlueprint);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		SXPBlueprint postSXPBlueprint = testGetSXPBlueprint_addSXPBlueprint();

		SXPBlueprint getSXPBlueprint = sxpBlueprintResource.getSXPBlueprint(
			postSXPBlueprint.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.search.experiences.rest.dto.v1_0.SXPBlueprint"
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

		Object item = vulcanCRUDItemDelegate.getItem(postSXPBlueprint.getId());

		assertEquals(
			getSXPBlueprint, SXPBlueprintSerDes.toDTO(item.toString()));
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

	protected SXPBlueprint testGetSXPBlueprint_addSXPBlueprint()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSXPBlueprint() throws Exception {
		SXPBlueprint sxpBlueprint =
			testGraphQLGetSXPBlueprint_addSXPBlueprint();

		// No namespace

		Assert.assertTrue(
			equals(
				sxpBlueprint,
				SXPBlueprintSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"sXPBlueprint",
								new HashMap<String, Object>() {
									{
										put(
											"sxpBlueprintId",
											sxpBlueprint.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/sXPBlueprint"))));

		// Using the namespace searchExperiences_v1_0

		Assert.assertTrue(
			equals(
				sxpBlueprint,
				SXPBlueprintSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"searchExperiences_v1_0",
								new GraphQLField(
									"sXPBlueprint",
									new HashMap<String, Object>() {
										{
											put(
												"sxpBlueprintId",
												sxpBlueprint.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/searchExperiences_v1_0",
						"Object/sXPBlueprint"))));
	}

	@Test
	public void testGraphQLGetSXPBlueprintNotFound() throws Exception {
		Long irrelevantSxpBlueprintId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"sXPBlueprint",
						new HashMap<String, Object>() {
							{
								put("sxpBlueprintId", irrelevantSxpBlueprintId);
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
							"sXPBlueprint",
							new HashMap<String, Object>() {
								{
									put(
										"sxpBlueprintId",
										irrelevantSxpBlueprintId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected SXPBlueprint testGraphQLGetSXPBlueprint_addSXPBlueprint()
		throws Exception {

		return testGraphQLSXPBlueprint_addSXPBlueprint();
	}

	@Test
	public void testGetSXPBlueprintByExternalReferenceCode() throws Exception {
		SXPBlueprint postSXPBlueprint =
			testGetSXPBlueprintByExternalReferenceCode_addSXPBlueprint();

		SXPBlueprint getSXPBlueprint =
			sxpBlueprintResource.getSXPBlueprintByExternalReferenceCode(
				postSXPBlueprint.getExternalReferenceCode());

		assertEquals(postSXPBlueprint, getSXPBlueprint);
		assertValid(getSXPBlueprint);
	}

	protected SXPBlueprint
			testGetSXPBlueprintByExternalReferenceCode_addSXPBlueprint()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSXPBlueprintByExternalReferenceCode()
		throws Exception {

		SXPBlueprint sxpBlueprint =
			testGraphQLGetSXPBlueprintByExternalReferenceCode_addSXPBlueprint();

		// No namespace

		Assert.assertTrue(
			equals(
				sxpBlueprint,
				SXPBlueprintSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"sXPBlueprintByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												sxpBlueprint.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/sXPBlueprintByExternalReferenceCode"))));

		// Using the namespace searchExperiences_v1_0

		Assert.assertTrue(
			equals(
				sxpBlueprint,
				SXPBlueprintSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"searchExperiences_v1_0",
								new GraphQLField(
									"sXPBlueprintByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													sxpBlueprint.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/searchExperiences_v1_0",
						"Object/sXPBlueprintByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetSXPBlueprintByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"sXPBlueprintByExternalReferenceCode",
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
							"sXPBlueprintByExternalReferenceCode",
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

	protected SXPBlueprint
			testGraphQLGetSXPBlueprintByExternalReferenceCode_addSXPBlueprint()
		throws Exception {

		return testGraphQLSXPBlueprint_addSXPBlueprint();
	}

	@Test
	public void testGetSXPBlueprintExport() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testGetSXPBlueprintsPage() throws Exception {
		Page<SXPBlueprint> page = sxpBlueprintResource.getSXPBlueprintsPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		SXPBlueprint sxpBlueprint1 = testGetSXPBlueprintsPage_addSXPBlueprint(
			randomSXPBlueprint());

		SXPBlueprint sxpBlueprint2 = testGetSXPBlueprintsPage_addSXPBlueprint(
			randomSXPBlueprint());

		page = sxpBlueprintResource.getSXPBlueprintsPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(sxpBlueprint1, (List<SXPBlueprint>)page.getItems());
		assertContains(sxpBlueprint2, (List<SXPBlueprint>)page.getItems());
		assertValid(page, testGetSXPBlueprintsPage_getExpectedActions());

		sxpBlueprintResource.deleteSXPBlueprint(sxpBlueprint1.getId());

		sxpBlueprintResource.deleteSXPBlueprint(sxpBlueprint2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSXPBlueprintsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSXPBlueprintsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		SXPBlueprint sxpBlueprint1 = randomSXPBlueprint();

		sxpBlueprint1 = testGetSXPBlueprintsPage_addSXPBlueprint(sxpBlueprint1);

		for (EntityField entityField : entityFields) {
			Page<SXPBlueprint> page = sxpBlueprintResource.getSXPBlueprintsPage(
				null, getFilterString(entityField, "between", sxpBlueprint1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(sxpBlueprint1),
				(List<SXPBlueprint>)page.getItems());
		}
	}

	@Test
	public void testGetSXPBlueprintsPageWithFilterDoubleEquals()
		throws Exception {

		testGetSXPBlueprintsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSXPBlueprintsPageWithFilterStringContains()
		throws Exception {

		testGetSXPBlueprintsPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSXPBlueprintsPageWithFilterStringEquals()
		throws Exception {

		testGetSXPBlueprintsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSXPBlueprintsPageWithFilterStringStartsWith()
		throws Exception {

		testGetSXPBlueprintsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetSXPBlueprintsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		SXPBlueprint sxpBlueprint1 = testGetSXPBlueprintsPage_addSXPBlueprint(
			randomSXPBlueprint());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		SXPBlueprint sxpBlueprint2 = testGetSXPBlueprintsPage_addSXPBlueprint(
			randomSXPBlueprint());

		for (EntityField entityField : entityFields) {
			Page<SXPBlueprint> page = sxpBlueprintResource.getSXPBlueprintsPage(
				null, getFilterString(entityField, operator, sxpBlueprint1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(sxpBlueprint1),
				(List<SXPBlueprint>)page.getItems());
		}
	}

	@Test
	public void testGetSXPBlueprintsPageWithPagination() throws Exception {
		Page<SXPBlueprint> sxpBlueprintsPage =
			sxpBlueprintResource.getSXPBlueprintsPage(null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			sxpBlueprintsPage.getTotalCount());

		SXPBlueprint sxpBlueprint1 = testGetSXPBlueprintsPage_addSXPBlueprint(
			randomSXPBlueprint());

		SXPBlueprint sxpBlueprint2 = testGetSXPBlueprintsPage_addSXPBlueprint(
			randomSXPBlueprint());

		SXPBlueprint sxpBlueprint3 = testGetSXPBlueprintsPage_addSXPBlueprint(
			randomSXPBlueprint());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<SXPBlueprint> page1 =
				sxpBlueprintResource.getSXPBlueprintsPage(
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(sxpBlueprint1, (List<SXPBlueprint>)page1.getItems());

			Page<SXPBlueprint> page2 =
				sxpBlueprintResource.getSXPBlueprintsPage(
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(sxpBlueprint2, (List<SXPBlueprint>)page2.getItems());

			Page<SXPBlueprint> page3 =
				sxpBlueprintResource.getSXPBlueprintsPage(
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(sxpBlueprint3, (List<SXPBlueprint>)page3.getItems());
		}
		else {
			Page<SXPBlueprint> page1 =
				sxpBlueprintResource.getSXPBlueprintsPage(
					null, null, Pagination.of(1, totalCount + 2), null);

			List<SXPBlueprint> sxpBlueprints1 =
				(List<SXPBlueprint>)page1.getItems();

			Assert.assertEquals(
				sxpBlueprints1.toString(), totalCount + 2,
				sxpBlueprints1.size());

			Page<SXPBlueprint> page2 =
				sxpBlueprintResource.getSXPBlueprintsPage(
					null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<SXPBlueprint> sxpBlueprints2 =
				(List<SXPBlueprint>)page2.getItems();

			Assert.assertEquals(
				sxpBlueprints2.toString(), 1, sxpBlueprints2.size());

			Page<SXPBlueprint> page3 =
				sxpBlueprintResource.getSXPBlueprintsPage(
					null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(sxpBlueprint1, (List<SXPBlueprint>)page3.getItems());
			assertContains(sxpBlueprint2, (List<SXPBlueprint>)page3.getItems());
			assertContains(sxpBlueprint3, (List<SXPBlueprint>)page3.getItems());
		}
	}

	@Test
	public void testGetSXPBlueprintsPageWithSortDateTime() throws Exception {
		testGetSXPBlueprintsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, sxpBlueprint1, sxpBlueprint2) -> {
				BeanTestUtil.setProperty(
					sxpBlueprint1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSXPBlueprintsPageWithSortDouble() throws Exception {
		testGetSXPBlueprintsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, sxpBlueprint1, sxpBlueprint2) -> {
				BeanTestUtil.setProperty(
					sxpBlueprint1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					sxpBlueprint2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSXPBlueprintsPageWithSortInteger() throws Exception {
		testGetSXPBlueprintsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, sxpBlueprint1, sxpBlueprint2) -> {
				BeanTestUtil.setProperty(
					sxpBlueprint1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					sxpBlueprint2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSXPBlueprintsPageWithSortString() throws Exception {
		testGetSXPBlueprintsPageWithSort(
			EntityField.Type.STRING,
			(entityField, sxpBlueprint1, sxpBlueprint2) -> {
				Class<?> clazz = sxpBlueprint1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						sxpBlueprint1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						sxpBlueprint2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						sxpBlueprint1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						sxpBlueprint2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						sxpBlueprint1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						sxpBlueprint2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSXPBlueprintsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, SXPBlueprint, SXPBlueprint, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		SXPBlueprint sxpBlueprint1 = randomSXPBlueprint();
		SXPBlueprint sxpBlueprint2 = randomSXPBlueprint();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, sxpBlueprint1, sxpBlueprint2);
		}

		sxpBlueprint1 = testGetSXPBlueprintsPage_addSXPBlueprint(sxpBlueprint1);

		sxpBlueprint2 = testGetSXPBlueprintsPage_addSXPBlueprint(sxpBlueprint2);

		Page<SXPBlueprint> page = sxpBlueprintResource.getSXPBlueprintsPage(
			null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<SXPBlueprint> ascPage =
				sxpBlueprintResource.getSXPBlueprintsPage(
					null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				sxpBlueprint1, (List<SXPBlueprint>)ascPage.getItems());
			assertContains(
				sxpBlueprint2, (List<SXPBlueprint>)ascPage.getItems());

			Page<SXPBlueprint> descPage =
				sxpBlueprintResource.getSXPBlueprintsPage(
					null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				sxpBlueprint2, (List<SXPBlueprint>)descPage.getItems());
			assertContains(
				sxpBlueprint1, (List<SXPBlueprint>)descPage.getItems());
		}
	}

	protected SXPBlueprint testGetSXPBlueprintsPage_addSXPBlueprint(
			SXPBlueprint sxpBlueprint)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSXPBlueprintsPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"sXPBlueprints",
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

		JSONObject sXPBlueprintsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/sXPBlueprints");

		long totalCount = sXPBlueprintsJSONObject.getLong("totalCount");

		SXPBlueprint sxpBlueprint1 = testGraphQLSXPBlueprint_addSXPBlueprint(
			randomSXPBlueprint());

		SXPBlueprint sxpBlueprint2 = testGraphQLSXPBlueprint_addSXPBlueprint(
			randomSXPBlueprint());

		sXPBlueprintsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/sXPBlueprints");

		Assert.assertEquals(
			totalCount + 2, sXPBlueprintsJSONObject.getLong("totalCount"));

		assertContains(
			sxpBlueprint1,
			Arrays.asList(
				SXPBlueprintSerDes.toDTOs(
					sXPBlueprintsJSONObject.getString("items"))));
		assertContains(
			sxpBlueprint2,
			Arrays.asList(
				SXPBlueprintSerDes.toDTOs(
					sXPBlueprintsJSONObject.getString("items"))));

		// Using the namespace searchExperiences_v1_0

		sXPBlueprintsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("searchExperiences_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/searchExperiences_v1_0",
			"JSONObject/sXPBlueprints");

		Assert.assertEquals(
			totalCount + 2, sXPBlueprintsJSONObject.getLong("totalCount"));

		assertContains(
			sxpBlueprint1,
			Arrays.asList(
				SXPBlueprintSerDes.toDTOs(
					sXPBlueprintsJSONObject.getString("items"))));
		assertContains(
			sxpBlueprint2,
			Arrays.asList(
				SXPBlueprintSerDes.toDTOs(
					sXPBlueprintsJSONObject.getString("items"))));
	}

	@Test
	public void testPatchSXPBlueprint() throws Exception {
		SXPBlueprint postSXPBlueprint = testPatchSXPBlueprint_addSXPBlueprint();

		SXPBlueprint randomPatchSXPBlueprint = randomPatchSXPBlueprint();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		SXPBlueprint patchSXPBlueprint = sxpBlueprintResource.patchSXPBlueprint(
			postSXPBlueprint.getId(), randomPatchSXPBlueprint);

		SXPBlueprint expectedPatchSXPBlueprint = postSXPBlueprint.clone();

		BeanTestUtil.copyProperties(
			randomPatchSXPBlueprint, expectedPatchSXPBlueprint);

		SXPBlueprint getSXPBlueprint = sxpBlueprintResource.getSXPBlueprint(
			patchSXPBlueprint.getId());

		assertEquals(expectedPatchSXPBlueprint, getSXPBlueprint);
		assertValid(getSXPBlueprint);
	}

	protected SXPBlueprint testPatchSXPBlueprint_addSXPBlueprint()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSXPBlueprint() throws Exception {
		SXPBlueprint randomSXPBlueprint = randomSXPBlueprint();

		SXPBlueprint postSXPBlueprint = testPostSXPBlueprint_addSXPBlueprint(
			randomSXPBlueprint);

		assertEquals(randomSXPBlueprint, postSXPBlueprint);
		assertValid(postSXPBlueprint);
	}

	protected SXPBlueprint testPostSXPBlueprint_addSXPBlueprint(
			SXPBlueprint sxpBlueprint)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostSXPBlueprint() throws Exception {
		SXPBlueprint randomSXPBlueprint = randomSXPBlueprint();

		SXPBlueprint sxpBlueprint = testGraphQLSXPBlueprint_addSXPBlueprint(
			randomSXPBlueprint);

		Assert.assertTrue(equals(randomSXPBlueprint, sxpBlueprint));
	}

	@Test
	public void testPostSXPBlueprintCopy() throws Exception {
		SXPBlueprint randomSXPBlueprint = randomSXPBlueprint();

		SXPBlueprint postSXPBlueprint =
			testPostSXPBlueprintCopy_addSXPBlueprint(randomSXPBlueprint);

		assertEquals(randomSXPBlueprint, postSXPBlueprint);
		assertValid(postSXPBlueprint);
	}

	protected SXPBlueprint testPostSXPBlueprintCopy_addSXPBlueprint(
			SXPBlueprint sxpBlueprint)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostSXPBlueprintCopy() throws Exception {
		SXPBlueprint randomSXPBlueprint = randomSXPBlueprint();

		SXPBlueprint sxpBlueprint = testGraphQLSXPBlueprint_addSXPBlueprint(
			randomSXPBlueprint);

		Assert.assertTrue(equals(randomSXPBlueprint, sxpBlueprint));
	}

	@Test
	public void testPostSXPBlueprintValidate() throws Exception {
		SXPBlueprint randomSXPBlueprint = randomSXPBlueprint();

		SXPBlueprint postSXPBlueprint =
			testPostSXPBlueprintValidate_addSXPBlueprint(randomSXPBlueprint);

		assertEquals(randomSXPBlueprint, postSXPBlueprint);
		assertValid(postSXPBlueprint);
	}

	protected SXPBlueprint testPostSXPBlueprintValidate_addSXPBlueprint(
			SXPBlueprint sxpBlueprint)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostSXPBlueprintValidate() throws Exception {
		SXPBlueprint randomSXPBlueprint = randomSXPBlueprint();

		SXPBlueprint sxpBlueprint = testGraphQLSXPBlueprint_addSXPBlueprint(
			randomSXPBlueprint);

		Assert.assertTrue(equals(randomSXPBlueprint, sxpBlueprint));
	}

	@Test
	public void testPutSXPBlueprint() throws Exception {
		SXPBlueprint postSXPBlueprint = testPutSXPBlueprint_addSXPBlueprint();

		SXPBlueprint randomSXPBlueprint = randomSXPBlueprint();

		SXPBlueprint putSXPBlueprint = sxpBlueprintResource.putSXPBlueprint(
			postSXPBlueprint.getId(), randomSXPBlueprint);

		assertEquals(randomSXPBlueprint, putSXPBlueprint);
		assertValid(putSXPBlueprint);

		SXPBlueprint getSXPBlueprint = sxpBlueprintResource.getSXPBlueprint(
			putSXPBlueprint.getId());

		assertEquals(randomSXPBlueprint, getSXPBlueprint);
		assertValid(getSXPBlueprint);
	}

	protected SXPBlueprint testPutSXPBlueprint_addSXPBlueprint()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutSXPBlueprintByExternalReferenceCode() throws Exception {
		SXPBlueprint postSXPBlueprint =
			testPutSXPBlueprintByExternalReferenceCode_addSXPBlueprint();

		SXPBlueprint randomSXPBlueprint = randomSXPBlueprint();

		SXPBlueprint putSXPBlueprint =
			sxpBlueprintResource.putSXPBlueprintByExternalReferenceCode(
				postSXPBlueprint.getExternalReferenceCode(),
				randomSXPBlueprint);

		assertEquals(randomSXPBlueprint, putSXPBlueprint);
		assertValid(putSXPBlueprint);

		SXPBlueprint getSXPBlueprint =
			sxpBlueprintResource.getSXPBlueprintByExternalReferenceCode(
				putSXPBlueprint.getExternalReferenceCode());

		assertEquals(randomSXPBlueprint, getSXPBlueprint);
		assertValid(getSXPBlueprint);

		SXPBlueprint newSXPBlueprint =
			testPutSXPBlueprintByExternalReferenceCode_createSXPBlueprint();

		putSXPBlueprint =
			sxpBlueprintResource.putSXPBlueprintByExternalReferenceCode(
				newSXPBlueprint.getExternalReferenceCode(), newSXPBlueprint);

		assertEquals(newSXPBlueprint, putSXPBlueprint);
		assertValid(putSXPBlueprint);

		getSXPBlueprint =
			sxpBlueprintResource.getSXPBlueprintByExternalReferenceCode(
				putSXPBlueprint.getExternalReferenceCode());

		assertEquals(newSXPBlueprint, getSXPBlueprint);

		Assert.assertEquals(
			newSXPBlueprint.getExternalReferenceCode(),
			putSXPBlueprint.getExternalReferenceCode());
	}

	protected SXPBlueprint
			testPutSXPBlueprintByExternalReferenceCode_addSXPBlueprint()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected SXPBlueprint
			testPutSXPBlueprintByExternalReferenceCode_createSXPBlueprint()
		throws Exception {

		return randomSXPBlueprint();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		SXPBlueprint sxpBlueprint1 =
			testBatchEngineDeleteImportTask_addSXPBlueprint();

		testBatchEngineDeleteImportTask_deleteSXPBlueprint(
			200, null, sxpBlueprint1.getId());

		assertHttpResponseStatusCode(
			404,
			sxpBlueprintResource.getSXPBlueprintHttpResponse(
				sxpBlueprint1.getId()));
	}

	protected SXPBlueprint testBatchEngineDeleteImportTask_addSXPBlueprint()
		throws Exception {

		return testDeleteSXPBlueprint_addSXPBlueprint();
	}

	protected void testBatchEngineDeleteImportTask_deleteSXPBlueprint(
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
				"com.liferay.search.experiences.rest.dto.v1_0.SXPBlueprint",
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

	protected SXPBlueprint testGraphQLSXPBlueprint_addSXPBlueprint()
		throws Exception {

		return testGraphQLSXPBlueprint_addSXPBlueprint(randomSXPBlueprint());
	}

	protected SXPBlueprint testGraphQLSXPBlueprint_addSXPBlueprint(
			SXPBlueprint sxpBlueprint)
		throws Exception {

		JSONDeserializer<SXPBlueprint> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(SXPBlueprint.class)) {

			if (getGraphQLValue(field.get(sxpBlueprint)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(sxpBlueprint)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSXPBlueprint",
						new HashMap<String, Object>() {
							{
								put("sxpBlueprint", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSXPBlueprint"),
			SXPBlueprint.class);
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
		SXPBlueprint sxpBlueprint, List<SXPBlueprint> sxpBlueprints) {

		boolean contains = false;

		for (SXPBlueprint item : sxpBlueprints) {
			if (equals(sxpBlueprint, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			sxpBlueprints + " does not contain " + sxpBlueprint, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		SXPBlueprint sxpBlueprint1, SXPBlueprint sxpBlueprint2) {

		Assert.assertTrue(
			sxpBlueprint1 + " does not equal " + sxpBlueprint2,
			equals(sxpBlueprint1, sxpBlueprint2));
	}

	protected void assertEquals(
		List<SXPBlueprint> sxpBlueprints1, List<SXPBlueprint> sxpBlueprints2) {

		Assert.assertEquals(sxpBlueprints1.size(), sxpBlueprints2.size());

		for (int i = 0; i < sxpBlueprints1.size(); i++) {
			SXPBlueprint sxpBlueprint1 = sxpBlueprints1.get(i);
			SXPBlueprint sxpBlueprint2 = sxpBlueprints2.get(i);

			assertEquals(sxpBlueprint1, sxpBlueprint2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<SXPBlueprint> sxpBlueprints1, List<SXPBlueprint> sxpBlueprints2) {

		Assert.assertEquals(sxpBlueprints1.size(), sxpBlueprints2.size());

		for (SXPBlueprint sxpBlueprint1 : sxpBlueprints1) {
			boolean contains = false;

			for (SXPBlueprint sxpBlueprint2 : sxpBlueprints2) {
				if (equals(sxpBlueprint1, sxpBlueprint2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				sxpBlueprints2 + " does not contain " + sxpBlueprint1,
				contains);
		}
	}

	protected void assertValid(SXPBlueprint sxpBlueprint) throws Exception {
		boolean valid = true;

		if (sxpBlueprint.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (sxpBlueprint.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"collectionProviderSubtypeName",
					additionalAssertFieldName)) {

				if (sxpBlueprint.getCollectionProviderSubtypeName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"collectionProviderTypeName", additionalAssertFieldName)) {

				if (sxpBlueprint.getCollectionProviderTypeName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("configuration", additionalAssertFieldName)) {
				if (sxpBlueprint.getConfiguration() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (sxpBlueprint.getCreateDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (sxpBlueprint.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (sxpBlueprint.getDescription_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("elementInstances", additionalAssertFieldName)) {
				if (sxpBlueprint.getElementInstances() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (sxpBlueprint.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("modifiedDate", additionalAssertFieldName)) {
				if (sxpBlueprint.getModifiedDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("schemaVersion", additionalAssertFieldName)) {
				if (sxpBlueprint.getSchemaVersion() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (sxpBlueprint.getTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title_i18n", additionalAssertFieldName)) {
				if (sxpBlueprint.getTitle_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("userName", additionalAssertFieldName)) {
				if (sxpBlueprint.getUserName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("version", additionalAssertFieldName)) {
				if (sxpBlueprint.getVersion() == null) {
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

	protected void assertValid(Page<SXPBlueprint> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<SXPBlueprint> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<SXPBlueprint> sxpBlueprints = page.getItems();

		int size = sxpBlueprints.size();

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
					com.liferay.search.experiences.rest.dto.v1_0.SXPBlueprint.
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

	protected boolean equals(
		SXPBlueprint sxpBlueprint1, SXPBlueprint sxpBlueprint2) {

		if (sxpBlueprint1 == sxpBlueprint2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)sxpBlueprint1.getActions(),
						(Map)sxpBlueprint2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"collectionProviderSubtypeName",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sxpBlueprint1.getCollectionProviderSubtypeName(),
						sxpBlueprint2.getCollectionProviderSubtypeName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"collectionProviderTypeName", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sxpBlueprint1.getCollectionProviderTypeName(),
						sxpBlueprint2.getCollectionProviderTypeName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("configuration", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sxpBlueprint1.getConfiguration(),
						sxpBlueprint2.getConfiguration())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sxpBlueprint1.getCreateDate(),
						sxpBlueprint2.getCreateDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sxpBlueprint1.getDescription(),
						sxpBlueprint2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)sxpBlueprint1.getDescription_i18n(),
						(Map)sxpBlueprint2.getDescription_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("elementInstances", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sxpBlueprint1.getElementInstances(),
						sxpBlueprint2.getElementInstances())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sxpBlueprint1.getExternalReferenceCode(),
						sxpBlueprint2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sxpBlueprint1.getId(), sxpBlueprint2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("modifiedDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sxpBlueprint1.getModifiedDate(),
						sxpBlueprint2.getModifiedDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("schemaVersion", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sxpBlueprint1.getSchemaVersion(),
						sxpBlueprint2.getSchemaVersion())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sxpBlueprint1.getTitle(), sxpBlueprint2.getTitle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)sxpBlueprint1.getTitle_i18n(),
						(Map)sxpBlueprint2.getTitle_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("userName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sxpBlueprint1.getUserName(),
						sxpBlueprint2.getUserName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("version", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sxpBlueprint1.getVersion(),
						sxpBlueprint2.getVersion())) {

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

		if (!(_sxpBlueprintResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_sxpBlueprintResource;

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
		EntityField entityField, String operator, SXPBlueprint sxpBlueprint) {

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

		if (entityFieldName.equals("collectionProviderSubtypeName")) {
			Object object = sxpBlueprint.getCollectionProviderSubtypeName();

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

		if (entityFieldName.equals("collectionProviderTypeName")) {
			Object object = sxpBlueprint.getCollectionProviderTypeName();

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

		if (entityFieldName.equals("configuration")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("createDate")) {
			if (operator.equals("between")) {
				Date date = sxpBlueprint.getCreateDate();

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

				sb.append(_format.format(sxpBlueprint.getCreateDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = sxpBlueprint.getDescription();

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

		if (entityFieldName.equals("elementInstances")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = sxpBlueprint.getExternalReferenceCode();

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

		if (entityFieldName.equals("modifiedDate")) {
			if (operator.equals("between")) {
				Date date = sxpBlueprint.getModifiedDate();

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

				sb.append(_format.format(sxpBlueprint.getModifiedDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("schemaVersion")) {
			Object object = sxpBlueprint.getSchemaVersion();

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
			Object object = sxpBlueprint.getTitle();

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

		if (entityFieldName.equals("userName")) {
			Object object = sxpBlueprint.getUserName();

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
			Object object = sxpBlueprint.getVersion();

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

	protected SXPBlueprint randomSXPBlueprint() throws Exception {
		return new SXPBlueprint() {
			{
				collectionProviderSubtypeName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				collectionProviderTypeName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				createDate = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				modifiedDate = RandomTestUtil.nextDate();
				schemaVersion = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
				userName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				version = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected SXPBlueprint randomIrrelevantSXPBlueprint() throws Exception {
		SXPBlueprint randomIrrelevantSXPBlueprint = randomSXPBlueprint();

		return randomIrrelevantSXPBlueprint;
	}

	protected SXPBlueprint randomPatchSXPBlueprint() throws Exception {
		return randomSXPBlueprint();
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

	protected SXPBlueprintResource sxpBlueprintResource;
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
		LogFactoryUtil.getLog(BaseSXPBlueprintResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.search.experiences.rest.resource.v1_0.SXPBlueprintResource
			_sxpBlueprintResource;

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