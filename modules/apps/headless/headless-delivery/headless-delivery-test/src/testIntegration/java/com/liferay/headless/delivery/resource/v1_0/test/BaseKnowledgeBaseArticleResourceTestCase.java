/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

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
import com.liferay.headless.delivery.client.dto.v1_0.Field;
import com.liferay.headless.delivery.client.dto.v1_0.KnowledgeBaseArticle;
import com.liferay.headless.delivery.client.dto.v1_0.Rating;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.permission.Permission;
import com.liferay.headless.delivery.client.resource.v1_0.KnowledgeBaseArticleResource;
import com.liferay.headless.delivery.client.serdes.v1_0.KnowledgeBaseArticleSerDes;
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
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public abstract class BaseKnowledgeBaseArticleResourceTestCase {

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

		_knowledgeBaseArticleResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		knowledgeBaseArticleResource = KnowledgeBaseArticleResource.builder(
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

		KnowledgeBaseArticle knowledgeBaseArticle1 =
			randomKnowledgeBaseArticle();

		String json = objectMapper.writeValueAsString(knowledgeBaseArticle1);

		KnowledgeBaseArticle knowledgeBaseArticle2 =
			KnowledgeBaseArticleSerDes.toDTO(json);

		Assert.assertTrue(equals(knowledgeBaseArticle1, knowledgeBaseArticle2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		KnowledgeBaseArticle knowledgeBaseArticle =
			randomKnowledgeBaseArticle();

		String json1 = objectMapper.writeValueAsString(knowledgeBaseArticle);
		String json2 = KnowledgeBaseArticleSerDes.toJSON(knowledgeBaseArticle);

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

		KnowledgeBaseArticle knowledgeBaseArticle =
			randomKnowledgeBaseArticle();

		knowledgeBaseArticle.setArticleBody(regex);
		knowledgeBaseArticle.setDescription(regex);
		knowledgeBaseArticle.setEncodingFormat(regex);
		knowledgeBaseArticle.setExternalReferenceCode(regex);
		knowledgeBaseArticle.setFriendlyUrlPath(regex);
		knowledgeBaseArticle.setTitle(regex);

		String json = KnowledgeBaseArticleSerDes.toJSON(knowledgeBaseArticle);

		Assert.assertFalse(json.contains(regex));

		knowledgeBaseArticle = KnowledgeBaseArticleSerDes.toDTO(json);

		Assert.assertEquals(regex, knowledgeBaseArticle.getArticleBody());
		Assert.assertEquals(regex, knowledgeBaseArticle.getDescription());
		Assert.assertEquals(regex, knowledgeBaseArticle.getEncodingFormat());
		Assert.assertEquals(
			regex, knowledgeBaseArticle.getExternalReferenceCode());
		Assert.assertEquals(regex, knowledgeBaseArticle.getFriendlyUrlPath());
		Assert.assertEquals(regex, knowledgeBaseArticle.getTitle());
	}

	@Test
	public void testDeleteKnowledgeBaseArticle() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		KnowledgeBaseArticle knowledgeBaseArticle =
			testDeleteKnowledgeBaseArticle_addKnowledgeBaseArticle();

		assertHttpResponseStatusCode(
			204,
			knowledgeBaseArticleResource.deleteKnowledgeBaseArticleHttpResponse(
				knowledgeBaseArticle.getId()));

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseArticleResource.getKnowledgeBaseArticleHttpResponse(
				knowledgeBaseArticle.getId()));
		assertHttpResponseStatusCode(
			404,
			knowledgeBaseArticleResource.getKnowledgeBaseArticleHttpResponse(
				0L));
	}

	protected KnowledgeBaseArticle
			testDeleteKnowledgeBaseArticle_addKnowledgeBaseArticle()
		throws Exception {

		return knowledgeBaseArticleResource.postSiteKnowledgeBaseArticle(
			testGroup.getGroupId(), randomKnowledgeBaseArticle());
	}

	@Test
	public void testGraphQLDeleteKnowledgeBaseArticle() throws Exception {

		// No namespace

		KnowledgeBaseArticle knowledgeBaseArticle1 =
			testGraphQLDeleteKnowledgeBaseArticle_addKnowledgeBaseArticle();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteKnowledgeBaseArticle",
						new HashMap<String, Object>() {
							{
								put(
									"knowledgeBaseArticleId",
									knowledgeBaseArticle1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteKnowledgeBaseArticle"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"knowledgeBaseArticle",
					new HashMap<String, Object>() {
						{
							put(
								"knowledgeBaseArticleId",
								knowledgeBaseArticle1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		KnowledgeBaseArticle knowledgeBaseArticle2 =
			testGraphQLDeleteKnowledgeBaseArticle_addKnowledgeBaseArticle();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteKnowledgeBaseArticle",
							new HashMap<String, Object>() {
								{
									put(
										"knowledgeBaseArticleId",
										knowledgeBaseArticle2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteKnowledgeBaseArticle"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"knowledgeBaseArticle",
						new HashMap<String, Object>() {
							{
								put(
									"knowledgeBaseArticleId",
									knowledgeBaseArticle2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected KnowledgeBaseArticle
			testGraphQLDeleteKnowledgeBaseArticle_addKnowledgeBaseArticle()
		throws Exception {

		return testGraphQLKnowledgeBaseArticle_addKnowledgeBaseArticle();
	}

	@Test
	public void testDeleteKnowledgeBaseArticleBatch() throws Exception {
		KnowledgeBaseArticle knowledgeBaseArticle1 =
			testDeleteKnowledgeBaseArticleBatch_addKnowledgeBaseArticle();

		testDeleteKnowledgeBaseArticleBatch_deleteKnowledgeBaseArticle(
			202, null, knowledgeBaseArticle1.getId());

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseArticleResource.getKnowledgeBaseArticleHttpResponse(
				knowledgeBaseArticle1.getId()));
	}

	protected KnowledgeBaseArticle
			testDeleteKnowledgeBaseArticleBatch_addKnowledgeBaseArticle()
		throws Exception {

		return testDeleteKnowledgeBaseArticle_addKnowledgeBaseArticle();
	}

	protected void
			testDeleteKnowledgeBaseArticleBatch_deleteKnowledgeBaseArticle(
				int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			knowledgeBaseArticleResource.
				deleteKnowledgeBaseArticleBatchHttpResponse(
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
	public void testDeleteKnowledgeBaseArticleMyRating() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		KnowledgeBaseArticle knowledgeBaseArticle =
			testDeleteKnowledgeBaseArticleMyRating_addKnowledgeBaseArticle();

		assertHttpResponseStatusCode(
			204,
			knowledgeBaseArticleResource.
				deleteKnowledgeBaseArticleMyRatingHttpResponse(
					knowledgeBaseArticle.getId()));

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseArticleResource.
				getKnowledgeBaseArticleMyRatingHttpResponse(
					knowledgeBaseArticle.getId()));
		assertHttpResponseStatusCode(
			404,
			knowledgeBaseArticleResource.
				getKnowledgeBaseArticleMyRatingHttpResponse(0L));
	}

	protected KnowledgeBaseArticle
			testDeleteKnowledgeBaseArticleMyRating_addKnowledgeBaseArticle()
		throws Exception {

		return knowledgeBaseArticleResource.postSiteKnowledgeBaseArticle(
			testGroup.getGroupId(), randomKnowledgeBaseArticle());
	}

	@Test
	public void testGraphQLDeleteKnowledgeBaseArticleMyRating()
		throws Exception {

		// No namespace

		KnowledgeBaseArticle knowledgeBaseArticle1 =
			testGraphQLDeleteKnowledgeBaseArticleMyRating_addKnowledgeBaseArticle();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteKnowledgeBaseArticleMyRating",
						new HashMap<String, Object>() {
							{
								put(
									"knowledgeBaseArticleId",
									knowledgeBaseArticle1.getId());
							}
						})),
				"JSONObject/data",
				"Object/deleteKnowledgeBaseArticleMyRating"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"knowledgeBaseArticleMyRating",
					new HashMap<String, Object>() {
						{
							put(
								"knowledgeBaseArticleId",
								knowledgeBaseArticle1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		KnowledgeBaseArticle knowledgeBaseArticle2 =
			testGraphQLDeleteKnowledgeBaseArticleMyRating_addKnowledgeBaseArticle();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteKnowledgeBaseArticleMyRating",
							new HashMap<String, Object>() {
								{
									put(
										"knowledgeBaseArticleId",
										knowledgeBaseArticle2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteKnowledgeBaseArticleMyRating"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"knowledgeBaseArticleMyRating",
						new HashMap<String, Object>() {
							{
								put(
									"knowledgeBaseArticleId",
									knowledgeBaseArticle2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected KnowledgeBaseArticle
			testGraphQLDeleteKnowledgeBaseArticleMyRating_addKnowledgeBaseArticle()
		throws Exception {

		return testGraphQLKnowledgeBaseArticle_addKnowledgeBaseArticle();
	}

	@Test
	public void testDeleteSiteKnowledgeBaseArticleByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		KnowledgeBaseArticle knowledgeBaseArticle =
			testDeleteSiteKnowledgeBaseArticleByExternalReferenceCode_addKnowledgeBaseArticle();

		assertHttpResponseStatusCode(
			204,
			knowledgeBaseArticleResource.
				deleteSiteKnowledgeBaseArticleByExternalReferenceCodeHttpResponse(
					knowledgeBaseArticle.getSiteId(),
					knowledgeBaseArticle.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseArticleResource.
				getSiteKnowledgeBaseArticleByExternalReferenceCodeHttpResponse(
					knowledgeBaseArticle.getSiteId(),
					knowledgeBaseArticle.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			knowledgeBaseArticleResource.
				getSiteKnowledgeBaseArticleByExternalReferenceCodeHttpResponse(
					knowledgeBaseArticle.getSiteId(), "-"));
	}

	protected KnowledgeBaseArticle
			testDeleteSiteKnowledgeBaseArticleByExternalReferenceCode_addKnowledgeBaseArticle()
		throws Exception {

		return knowledgeBaseArticleResource.postSiteKnowledgeBaseArticle(
			testGroup.getGroupId(), randomKnowledgeBaseArticle());
	}

	@Test
	public void testGraphQLDeleteSiteKnowledgeBaseArticleByExternalReferenceCode()
		throws Exception {

		// No namespace

		KnowledgeBaseArticle knowledgeBaseArticle1 =
			testGraphQLDeleteSiteKnowledgeBaseArticleByExternalReferenceCode_addKnowledgeBaseArticle();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSiteKnowledgeBaseArticleByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + knowledgeBaseArticle1.getSiteId() +
										"\"");
								put(
									"externalReferenceCode",
									"\"" +
										knowledgeBaseArticle1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteSiteKnowledgeBaseArticleByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"knowledgeBaseArticleByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"siteKey",
								"\"" + knowledgeBaseArticle1.getSiteId() +
									"\"");
							put(
								"externalReferenceCode",
								"\"" +
									knowledgeBaseArticle1.
										getExternalReferenceCode() + "\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		KnowledgeBaseArticle knowledgeBaseArticle2 =
			testGraphQLDeleteSiteKnowledgeBaseArticleByExternalReferenceCode_addKnowledgeBaseArticle();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteSiteKnowledgeBaseArticleByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" +
											knowledgeBaseArticle2.getSiteId() +
												"\"");
									put(
										"externalReferenceCode",
										"\"" +
											knowledgeBaseArticle2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteSiteKnowledgeBaseArticleByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"knowledgeBaseArticleByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + knowledgeBaseArticle2.getSiteId() +
										"\"");
								put(
									"externalReferenceCode",
									"\"" +
										knowledgeBaseArticle2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected KnowledgeBaseArticle
			testGraphQLDeleteSiteKnowledgeBaseArticleByExternalReferenceCode_addKnowledgeBaseArticle()
		throws Exception {

		return testGraphQLSiteKnowledgeBaseArticle_addKnowledgeBaseArticle();
	}

	@Test
	public void testGetKnowledgeBaseArticle() throws Exception {
		KnowledgeBaseArticle postKnowledgeBaseArticle =
			testGetKnowledgeBaseArticle_addKnowledgeBaseArticle();

		KnowledgeBaseArticle getKnowledgeBaseArticle =
			knowledgeBaseArticleResource.getKnowledgeBaseArticle(
				postKnowledgeBaseArticle.getId());

		assertEquals(postKnowledgeBaseArticle, getKnowledgeBaseArticle);
		assertValid(getKnowledgeBaseArticle);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		KnowledgeBaseArticle postKnowledgeBaseArticle =
			testGetKnowledgeBaseArticle_addKnowledgeBaseArticle();

		KnowledgeBaseArticle getKnowledgeBaseArticle =
			knowledgeBaseArticleResource.getKnowledgeBaseArticle(
				postKnowledgeBaseArticle.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.delivery.dto.v1_0.KnowledgeBaseArticle"
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
			postKnowledgeBaseArticle.getId());

		assertEquals(
			getKnowledgeBaseArticle,
			KnowledgeBaseArticleSerDes.toDTO(item.toString()));
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

	protected KnowledgeBaseArticle
			testGetKnowledgeBaseArticle_addKnowledgeBaseArticle()
		throws Exception {

		return knowledgeBaseArticleResource.postSiteKnowledgeBaseArticle(
			testGroup.getGroupId(), randomKnowledgeBaseArticle());
	}

	@Test
	public void testGraphQLGetKnowledgeBaseArticle() throws Exception {
		KnowledgeBaseArticle knowledgeBaseArticle =
			testGraphQLGetKnowledgeBaseArticle_addKnowledgeBaseArticle();

		// No namespace

		Assert.assertTrue(
			equals(
				knowledgeBaseArticle,
				KnowledgeBaseArticleSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"knowledgeBaseArticle",
								new HashMap<String, Object>() {
									{
										put(
											"knowledgeBaseArticleId",
											knowledgeBaseArticle.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/knowledgeBaseArticle"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				knowledgeBaseArticle,
				KnowledgeBaseArticleSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"knowledgeBaseArticle",
									new HashMap<String, Object>() {
										{
											put(
												"knowledgeBaseArticleId",
												knowledgeBaseArticle.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/knowledgeBaseArticle"))));
	}

	@Test
	public void testGraphQLGetKnowledgeBaseArticleNotFound() throws Exception {
		Long irrelevantKnowledgeBaseArticleId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"knowledgeBaseArticle",
						new HashMap<String, Object>() {
							{
								put(
									"knowledgeBaseArticleId",
									irrelevantKnowledgeBaseArticleId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"knowledgeBaseArticle",
							new HashMap<String, Object>() {
								{
									put(
										"knowledgeBaseArticleId",
										irrelevantKnowledgeBaseArticleId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected KnowledgeBaseArticle
			testGraphQLGetKnowledgeBaseArticle_addKnowledgeBaseArticle()
		throws Exception {

		return testGraphQLKnowledgeBaseArticle_addKnowledgeBaseArticle();
	}

	@Test
	public void testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage()
		throws Exception {

		Long parentKnowledgeBaseArticleId =
			testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_getParentKnowledgeBaseArticleId();
		Long irrelevantParentKnowledgeBaseArticleId =
			testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_getIrrelevantParentKnowledgeBaseArticleId();

		Page<KnowledgeBaseArticle> page =
			knowledgeBaseArticleResource.
				getKnowledgeBaseArticleKnowledgeBaseArticlesPage(
					parentKnowledgeBaseArticleId, null, null, null, null,
					Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantParentKnowledgeBaseArticleId != null) {
			KnowledgeBaseArticle irrelevantKnowledgeBaseArticle =
				testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
					irrelevantParentKnowledgeBaseArticleId,
					randomIrrelevantKnowledgeBaseArticle());

			page =
				knowledgeBaseArticleResource.
					getKnowledgeBaseArticleKnowledgeBaseArticlesPage(
						irrelevantParentKnowledgeBaseArticleId, null, null,
						null, null, Pagination.of(1, (int)totalCount + 1),
						null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantKnowledgeBaseArticle,
				(List<KnowledgeBaseArticle>)page.getItems());
			assertValid(
				page,
				testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_getExpectedActions(
					irrelevantParentKnowledgeBaseArticleId));
		}

		KnowledgeBaseArticle knowledgeBaseArticle1 =
			testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				parentKnowledgeBaseArticleId, randomKnowledgeBaseArticle());

		KnowledgeBaseArticle knowledgeBaseArticle2 =
			testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				parentKnowledgeBaseArticleId, randomKnowledgeBaseArticle());

		page =
			knowledgeBaseArticleResource.
				getKnowledgeBaseArticleKnowledgeBaseArticlesPage(
					parentKnowledgeBaseArticleId, null, null, null, null,
					Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			knowledgeBaseArticle1, (List<KnowledgeBaseArticle>)page.getItems());
		assertContains(
			knowledgeBaseArticle2, (List<KnowledgeBaseArticle>)page.getItems());
		assertValid(
			page,
			testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_getExpectedActions(
				parentKnowledgeBaseArticleId));

		knowledgeBaseArticleResource.deleteKnowledgeBaseArticle(
			knowledgeBaseArticle1.getId());

		knowledgeBaseArticleResource.deleteKnowledgeBaseArticle(
			knowledgeBaseArticle2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_getExpectedActions(
				Long parentKnowledgeBaseArticleId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetKnowledgeBaseArticleKnowledgeBaseArticlesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long parentKnowledgeBaseArticleId =
			testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_getParentKnowledgeBaseArticleId();

		KnowledgeBaseArticle knowledgeBaseArticle1 =
			randomKnowledgeBaseArticle();

		knowledgeBaseArticle1 =
			testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				parentKnowledgeBaseArticleId, knowledgeBaseArticle1);

		for (EntityField entityField : entityFields) {
			Page<KnowledgeBaseArticle> page =
				knowledgeBaseArticleResource.
					getKnowledgeBaseArticleKnowledgeBaseArticlesPage(
						parentKnowledgeBaseArticleId, null, null, null,
						getFilterString(
							entityField, "between", knowledgeBaseArticle1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(knowledgeBaseArticle1),
				(List<KnowledgeBaseArticle>)page.getItems());
		}
	}

	@Test
	public void testGetKnowledgeBaseArticleKnowledgeBaseArticlesPageWithFilterDoubleEquals()
		throws Exception {

		testGetKnowledgeBaseArticleKnowledgeBaseArticlesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetKnowledgeBaseArticleKnowledgeBaseArticlesPageWithFilterStringContains()
		throws Exception {

		testGetKnowledgeBaseArticleKnowledgeBaseArticlesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetKnowledgeBaseArticleKnowledgeBaseArticlesPageWithFilterStringEquals()
		throws Exception {

		testGetKnowledgeBaseArticleKnowledgeBaseArticlesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetKnowledgeBaseArticleKnowledgeBaseArticlesPageWithFilterStringStartsWith()
		throws Exception {

		testGetKnowledgeBaseArticleKnowledgeBaseArticlesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetKnowledgeBaseArticleKnowledgeBaseArticlesPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long parentKnowledgeBaseArticleId =
			testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_getParentKnowledgeBaseArticleId();

		KnowledgeBaseArticle knowledgeBaseArticle1 =
			testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				parentKnowledgeBaseArticleId, randomKnowledgeBaseArticle());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		KnowledgeBaseArticle knowledgeBaseArticle2 =
			testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				parentKnowledgeBaseArticleId, randomKnowledgeBaseArticle());

		for (EntityField entityField : entityFields) {
			Page<KnowledgeBaseArticle> page =
				knowledgeBaseArticleResource.
					getKnowledgeBaseArticleKnowledgeBaseArticlesPage(
						parentKnowledgeBaseArticleId, null, null, null,
						getFilterString(
							entityField, operator, knowledgeBaseArticle1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(knowledgeBaseArticle1),
				(List<KnowledgeBaseArticle>)page.getItems());
		}
	}

	@Test
	public void testGetKnowledgeBaseArticleKnowledgeBaseArticlesPageWithPagination()
		throws Exception {

		Long parentKnowledgeBaseArticleId =
			testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_getParentKnowledgeBaseArticleId();

		Page<KnowledgeBaseArticle> knowledgeBaseArticlesPage =
			knowledgeBaseArticleResource.
				getKnowledgeBaseArticleKnowledgeBaseArticlesPage(
					parentKnowledgeBaseArticleId, null, null, null, null, null,
					null);

		int totalCount = GetterUtil.getInteger(
			knowledgeBaseArticlesPage.getTotalCount());

		KnowledgeBaseArticle knowledgeBaseArticle1 =
			testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				parentKnowledgeBaseArticleId, randomKnowledgeBaseArticle());

		KnowledgeBaseArticle knowledgeBaseArticle2 =
			testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				parentKnowledgeBaseArticleId, randomKnowledgeBaseArticle());

		KnowledgeBaseArticle knowledgeBaseArticle3 =
			testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				parentKnowledgeBaseArticleId, randomKnowledgeBaseArticle());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<KnowledgeBaseArticle> page1 =
				knowledgeBaseArticleResource.
					getKnowledgeBaseArticleKnowledgeBaseArticlesPage(
						parentKnowledgeBaseArticleId, null, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				knowledgeBaseArticle1,
				(List<KnowledgeBaseArticle>)page1.getItems());

			Page<KnowledgeBaseArticle> page2 =
				knowledgeBaseArticleResource.
					getKnowledgeBaseArticleKnowledgeBaseArticlesPage(
						parentKnowledgeBaseArticleId, null, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				knowledgeBaseArticle2,
				(List<KnowledgeBaseArticle>)page2.getItems());

			Page<KnowledgeBaseArticle> page3 =
				knowledgeBaseArticleResource.
					getKnowledgeBaseArticleKnowledgeBaseArticlesPage(
						parentKnowledgeBaseArticleId, null, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				knowledgeBaseArticle3,
				(List<KnowledgeBaseArticle>)page3.getItems());
		}
		else {
			Page<KnowledgeBaseArticle> page1 =
				knowledgeBaseArticleResource.
					getKnowledgeBaseArticleKnowledgeBaseArticlesPage(
						parentKnowledgeBaseArticleId, null, null, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<KnowledgeBaseArticle> knowledgeBaseArticles1 =
				(List<KnowledgeBaseArticle>)page1.getItems();

			Assert.assertEquals(
				knowledgeBaseArticles1.toString(), totalCount + 2,
				knowledgeBaseArticles1.size());

			Page<KnowledgeBaseArticle> page2 =
				knowledgeBaseArticleResource.
					getKnowledgeBaseArticleKnowledgeBaseArticlesPage(
						parentKnowledgeBaseArticleId, null, null, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<KnowledgeBaseArticle> knowledgeBaseArticles2 =
				(List<KnowledgeBaseArticle>)page2.getItems();

			Assert.assertEquals(
				knowledgeBaseArticles2.toString(), 1,
				knowledgeBaseArticles2.size());

			Page<KnowledgeBaseArticle> page3 =
				knowledgeBaseArticleResource.
					getKnowledgeBaseArticleKnowledgeBaseArticlesPage(
						parentKnowledgeBaseArticleId, null, null, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				knowledgeBaseArticle1,
				(List<KnowledgeBaseArticle>)page3.getItems());
			assertContains(
				knowledgeBaseArticle2,
				(List<KnowledgeBaseArticle>)page3.getItems());
			assertContains(
				knowledgeBaseArticle3,
				(List<KnowledgeBaseArticle>)page3.getItems());
		}
	}

	@Test
	public void testGetKnowledgeBaseArticleKnowledgeBaseArticlesPageWithSortDateTime()
		throws Exception {

		testGetKnowledgeBaseArticleKnowledgeBaseArticlesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, knowledgeBaseArticle1, knowledgeBaseArticle2) -> {
				BeanTestUtil.setProperty(
					knowledgeBaseArticle1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetKnowledgeBaseArticleKnowledgeBaseArticlesPageWithSortDouble()
		throws Exception {

		testGetKnowledgeBaseArticleKnowledgeBaseArticlesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, knowledgeBaseArticle1, knowledgeBaseArticle2) -> {
				BeanTestUtil.setProperty(
					knowledgeBaseArticle1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					knowledgeBaseArticle2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetKnowledgeBaseArticleKnowledgeBaseArticlesPageWithSortInteger()
		throws Exception {

		testGetKnowledgeBaseArticleKnowledgeBaseArticlesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, knowledgeBaseArticle1, knowledgeBaseArticle2) -> {
				BeanTestUtil.setProperty(
					knowledgeBaseArticle1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					knowledgeBaseArticle2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetKnowledgeBaseArticleKnowledgeBaseArticlesPageWithSortString()
		throws Exception {

		testGetKnowledgeBaseArticleKnowledgeBaseArticlesPageWithSort(
			EntityField.Type.STRING,
			(entityField, knowledgeBaseArticle1, knowledgeBaseArticle2) -> {
				Class<?> clazz = knowledgeBaseArticle1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						knowledgeBaseArticle1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						knowledgeBaseArticle2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						knowledgeBaseArticle1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						knowledgeBaseArticle2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						knowledgeBaseArticle1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						knowledgeBaseArticle2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetKnowledgeBaseArticleKnowledgeBaseArticlesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, KnowledgeBaseArticle, KnowledgeBaseArticle,
				 Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long parentKnowledgeBaseArticleId =
			testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_getParentKnowledgeBaseArticleId();

		KnowledgeBaseArticle knowledgeBaseArticle1 =
			randomKnowledgeBaseArticle();
		KnowledgeBaseArticle knowledgeBaseArticle2 =
			randomKnowledgeBaseArticle();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, knowledgeBaseArticle1, knowledgeBaseArticle2);
		}

		knowledgeBaseArticle1 =
			testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				parentKnowledgeBaseArticleId, knowledgeBaseArticle1);

		knowledgeBaseArticle2 =
			testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				parentKnowledgeBaseArticleId, knowledgeBaseArticle2);

		Page<KnowledgeBaseArticle> page =
			knowledgeBaseArticleResource.
				getKnowledgeBaseArticleKnowledgeBaseArticlesPage(
					parentKnowledgeBaseArticleId, null, null, null, null, null,
					null);

		for (EntityField entityField : entityFields) {
			Page<KnowledgeBaseArticle> ascPage =
				knowledgeBaseArticleResource.
					getKnowledgeBaseArticleKnowledgeBaseArticlesPage(
						parentKnowledgeBaseArticleId, null, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				knowledgeBaseArticle1,
				(List<KnowledgeBaseArticle>)ascPage.getItems());
			assertContains(
				knowledgeBaseArticle2,
				(List<KnowledgeBaseArticle>)ascPage.getItems());

			Page<KnowledgeBaseArticle> descPage =
				knowledgeBaseArticleResource.
					getKnowledgeBaseArticleKnowledgeBaseArticlesPage(
						parentKnowledgeBaseArticleId, null, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				knowledgeBaseArticle2,
				(List<KnowledgeBaseArticle>)descPage.getItems());
			assertContains(
				knowledgeBaseArticle1,
				(List<KnowledgeBaseArticle>)descPage.getItems());
		}
	}

	protected KnowledgeBaseArticle
			testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				Long parentKnowledgeBaseArticleId,
				KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception {

		return knowledgeBaseArticleResource.
			postKnowledgeBaseArticleKnowledgeBaseArticle(
				parentKnowledgeBaseArticleId, knowledgeBaseArticle);
	}

	protected Long
			testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_getParentKnowledgeBaseArticleId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_getIrrelevantParentKnowledgeBaseArticleId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetKnowledgeBaseArticleKnowledgeBaseArticlesPage()
		throws Exception {

		Long parentKnowledgeBaseArticleId =
			testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_getParentKnowledgeBaseArticleId();

		GraphQLField graphQLField = new GraphQLField(
			"knowledgeBaseArticleKnowledgeBaseArticles",
			new HashMap<String, Object>() {
				{
					put(
						"parentKnowledgeBaseArticleId",
						parentKnowledgeBaseArticleId);
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject knowledgeBaseArticleKnowledgeBaseArticlesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/knowledgeBaseArticleKnowledgeBaseArticles");

		long totalCount =
			knowledgeBaseArticleKnowledgeBaseArticlesJSONObject.getLong(
				"totalCount");

		KnowledgeBaseArticle knowledgeBaseArticle1 =
			testGraphQLGetKnowledgeBaseArticleKnowledgeBaseArticlesPageKnowledgeBaseArticle_addKnowledgeBaseArticle(
				parentKnowledgeBaseArticleId, randomKnowledgeBaseArticle());

		KnowledgeBaseArticle knowledgeBaseArticle2 =
			testGraphQLGetKnowledgeBaseArticleKnowledgeBaseArticlesPageKnowledgeBaseArticle_addKnowledgeBaseArticle(
				parentKnowledgeBaseArticleId, randomKnowledgeBaseArticle());

		knowledgeBaseArticleKnowledgeBaseArticlesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/knowledgeBaseArticleKnowledgeBaseArticles");

		Assert.assertEquals(
			totalCount + 2,
			knowledgeBaseArticleKnowledgeBaseArticlesJSONObject.getLong(
				"totalCount"));

		assertContains(
			knowledgeBaseArticle1,
			Arrays.asList(
				KnowledgeBaseArticleSerDes.toDTOs(
					knowledgeBaseArticleKnowledgeBaseArticlesJSONObject.
						getString("items"))));
		assertContains(
			knowledgeBaseArticle2,
			Arrays.asList(
				KnowledgeBaseArticleSerDes.toDTOs(
					knowledgeBaseArticleKnowledgeBaseArticlesJSONObject.
						getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		knowledgeBaseArticleKnowledgeBaseArticlesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField("headlessDelivery_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"JSONObject/knowledgeBaseArticleKnowledgeBaseArticles");

		Assert.assertEquals(
			totalCount + 2,
			knowledgeBaseArticleKnowledgeBaseArticlesJSONObject.getLong(
				"totalCount"));

		assertContains(
			knowledgeBaseArticle1,
			Arrays.asList(
				KnowledgeBaseArticleSerDes.toDTOs(
					knowledgeBaseArticleKnowledgeBaseArticlesJSONObject.
						getString("items"))));
		assertContains(
			knowledgeBaseArticle2,
			Arrays.asList(
				KnowledgeBaseArticleSerDes.toDTOs(
					knowledgeBaseArticleKnowledgeBaseArticlesJSONObject.
						getString("items"))));
	}

	protected KnowledgeBaseArticle
			testGraphQLGetKnowledgeBaseArticleKnowledgeBaseArticlesPageKnowledgeBaseArticle_addKnowledgeBaseArticle(
				Long parentKnowledgeBaseArticleId,
				KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetKnowledgeBaseArticlePermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		KnowledgeBaseArticle postKnowledgeBaseArticle =
			testGetKnowledgeBaseArticlePermissionsPage_addKnowledgeBaseArticle();

		Page<Permission> page =
			knowledgeBaseArticleResource.getKnowledgeBaseArticlePermissionsPage(
				postKnowledgeBaseArticle.getId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected KnowledgeBaseArticle
			testGetKnowledgeBaseArticlePermissionsPage_addKnowledgeBaseArticle()
		throws Exception {

		return knowledgeBaseArticleResource.postSiteKnowledgeBaseArticle(
			testGroup.getGroupId(), randomKnowledgeBaseArticle());
	}

	@Test
	public void testGraphQLGetKnowledgeBaseArticlePermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		KnowledgeBaseArticle postKnowledgeBaseArticle =
			testGraphQLGetKnowledgeBaseArticlePermissionsPage_addKnowledgeBaseArticle();

		GraphQLField graphQLField = new GraphQLField(
			"knowledgeBaseArticlePermissions",
			new HashMap<String, Object>() {
				{
					put(
						"knowledgeBaseArticleId",
						postKnowledgeBaseArticle.getId());
				}
			},
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject knowledgeBaseArticlePermissionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/knowledgeBaseArticlePermissions");

		Assert.assertNotNull(knowledgeBaseArticlePermissionsJSONObject);
	}

	protected KnowledgeBaseArticle
			testGraphQLGetKnowledgeBaseArticlePermissionsPage_addKnowledgeBaseArticle()
		throws Exception {

		return testGraphQLKnowledgeBaseArticle_addKnowledgeBaseArticle();
	}

	@Test
	public void testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage()
		throws Exception {

		Long knowledgeBaseFolderId =
			testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_getKnowledgeBaseFolderId();
		Long irrelevantKnowledgeBaseFolderId =
			testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_getIrrelevantKnowledgeBaseFolderId();

		Page<KnowledgeBaseArticle> page =
			knowledgeBaseArticleResource.
				getKnowledgeBaseFolderKnowledgeBaseArticlesPage(
					knowledgeBaseFolderId, null, null, null, null,
					Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantKnowledgeBaseFolderId != null) {
			KnowledgeBaseArticle irrelevantKnowledgeBaseArticle =
				testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
					irrelevantKnowledgeBaseFolderId,
					randomIrrelevantKnowledgeBaseArticle());

			page =
				knowledgeBaseArticleResource.
					getKnowledgeBaseFolderKnowledgeBaseArticlesPage(
						irrelevantKnowledgeBaseFolderId, null, null, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantKnowledgeBaseArticle,
				(List<KnowledgeBaseArticle>)page.getItems());
			assertValid(
				page,
				testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_getExpectedActions(
					irrelevantKnowledgeBaseFolderId));
		}

		KnowledgeBaseArticle knowledgeBaseArticle1 =
			testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				knowledgeBaseFolderId, randomKnowledgeBaseArticle());

		KnowledgeBaseArticle knowledgeBaseArticle2 =
			testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				knowledgeBaseFolderId, randomKnowledgeBaseArticle());

		page =
			knowledgeBaseArticleResource.
				getKnowledgeBaseFolderKnowledgeBaseArticlesPage(
					knowledgeBaseFolderId, null, null, null, null,
					Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			knowledgeBaseArticle1, (List<KnowledgeBaseArticle>)page.getItems());
		assertContains(
			knowledgeBaseArticle2, (List<KnowledgeBaseArticle>)page.getItems());
		assertValid(
			page,
			testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_getExpectedActions(
				knowledgeBaseFolderId));

		knowledgeBaseArticleResource.deleteKnowledgeBaseArticle(
			knowledgeBaseArticle1.getId());

		knowledgeBaseArticleResource.deleteKnowledgeBaseArticle(
			knowledgeBaseArticle2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_getExpectedActions(
				Long knowledgeBaseFolderId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/knowledge-base-folders/{knowledgeBaseFolderId}/knowledge-base-articles/batch".
				replace(
					"{knowledgeBaseFolderId}",
					String.valueOf(knowledgeBaseFolderId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetKnowledgeBaseFolderKnowledgeBaseArticlesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long knowledgeBaseFolderId =
			testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_getKnowledgeBaseFolderId();

		KnowledgeBaseArticle knowledgeBaseArticle1 =
			randomKnowledgeBaseArticle();

		knowledgeBaseArticle1 =
			testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				knowledgeBaseFolderId, knowledgeBaseArticle1);

		for (EntityField entityField : entityFields) {
			Page<KnowledgeBaseArticle> page =
				knowledgeBaseArticleResource.
					getKnowledgeBaseFolderKnowledgeBaseArticlesPage(
						knowledgeBaseFolderId, null, null, null,
						getFilterString(
							entityField, "between", knowledgeBaseArticle1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(knowledgeBaseArticle1),
				(List<KnowledgeBaseArticle>)page.getItems());
		}
	}

	@Test
	public void testGetKnowledgeBaseFolderKnowledgeBaseArticlesPageWithFilterDoubleEquals()
		throws Exception {

		testGetKnowledgeBaseFolderKnowledgeBaseArticlesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetKnowledgeBaseFolderKnowledgeBaseArticlesPageWithFilterStringContains()
		throws Exception {

		testGetKnowledgeBaseFolderKnowledgeBaseArticlesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetKnowledgeBaseFolderKnowledgeBaseArticlesPageWithFilterStringEquals()
		throws Exception {

		testGetKnowledgeBaseFolderKnowledgeBaseArticlesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetKnowledgeBaseFolderKnowledgeBaseArticlesPageWithFilterStringStartsWith()
		throws Exception {

		testGetKnowledgeBaseFolderKnowledgeBaseArticlesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetKnowledgeBaseFolderKnowledgeBaseArticlesPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long knowledgeBaseFolderId =
			testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_getKnowledgeBaseFolderId();

		KnowledgeBaseArticle knowledgeBaseArticle1 =
			testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				knowledgeBaseFolderId, randomKnowledgeBaseArticle());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		KnowledgeBaseArticle knowledgeBaseArticle2 =
			testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				knowledgeBaseFolderId, randomKnowledgeBaseArticle());

		for (EntityField entityField : entityFields) {
			Page<KnowledgeBaseArticle> page =
				knowledgeBaseArticleResource.
					getKnowledgeBaseFolderKnowledgeBaseArticlesPage(
						knowledgeBaseFolderId, null, null, null,
						getFilterString(
							entityField, operator, knowledgeBaseArticle1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(knowledgeBaseArticle1),
				(List<KnowledgeBaseArticle>)page.getItems());
		}
	}

	@Test
	public void testGetKnowledgeBaseFolderKnowledgeBaseArticlesPageWithPagination()
		throws Exception {

		Long knowledgeBaseFolderId =
			testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_getKnowledgeBaseFolderId();

		Page<KnowledgeBaseArticle> knowledgeBaseArticlesPage =
			knowledgeBaseArticleResource.
				getKnowledgeBaseFolderKnowledgeBaseArticlesPage(
					knowledgeBaseFolderId, null, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			knowledgeBaseArticlesPage.getTotalCount());

		KnowledgeBaseArticle knowledgeBaseArticle1 =
			testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				knowledgeBaseFolderId, randomKnowledgeBaseArticle());

		KnowledgeBaseArticle knowledgeBaseArticle2 =
			testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				knowledgeBaseFolderId, randomKnowledgeBaseArticle());

		KnowledgeBaseArticle knowledgeBaseArticle3 =
			testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				knowledgeBaseFolderId, randomKnowledgeBaseArticle());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<KnowledgeBaseArticle> page1 =
				knowledgeBaseArticleResource.
					getKnowledgeBaseFolderKnowledgeBaseArticlesPage(
						knowledgeBaseFolderId, null, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				knowledgeBaseArticle1,
				(List<KnowledgeBaseArticle>)page1.getItems());

			Page<KnowledgeBaseArticle> page2 =
				knowledgeBaseArticleResource.
					getKnowledgeBaseFolderKnowledgeBaseArticlesPage(
						knowledgeBaseFolderId, null, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				knowledgeBaseArticle2,
				(List<KnowledgeBaseArticle>)page2.getItems());

			Page<KnowledgeBaseArticle> page3 =
				knowledgeBaseArticleResource.
					getKnowledgeBaseFolderKnowledgeBaseArticlesPage(
						knowledgeBaseFolderId, null, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				knowledgeBaseArticle3,
				(List<KnowledgeBaseArticle>)page3.getItems());
		}
		else {
			Page<KnowledgeBaseArticle> page1 =
				knowledgeBaseArticleResource.
					getKnowledgeBaseFolderKnowledgeBaseArticlesPage(
						knowledgeBaseFolderId, null, null, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<KnowledgeBaseArticle> knowledgeBaseArticles1 =
				(List<KnowledgeBaseArticle>)page1.getItems();

			Assert.assertEquals(
				knowledgeBaseArticles1.toString(), totalCount + 2,
				knowledgeBaseArticles1.size());

			Page<KnowledgeBaseArticle> page2 =
				knowledgeBaseArticleResource.
					getKnowledgeBaseFolderKnowledgeBaseArticlesPage(
						knowledgeBaseFolderId, null, null, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<KnowledgeBaseArticle> knowledgeBaseArticles2 =
				(List<KnowledgeBaseArticle>)page2.getItems();

			Assert.assertEquals(
				knowledgeBaseArticles2.toString(), 1,
				knowledgeBaseArticles2.size());

			Page<KnowledgeBaseArticle> page3 =
				knowledgeBaseArticleResource.
					getKnowledgeBaseFolderKnowledgeBaseArticlesPage(
						knowledgeBaseFolderId, null, null, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				knowledgeBaseArticle1,
				(List<KnowledgeBaseArticle>)page3.getItems());
			assertContains(
				knowledgeBaseArticle2,
				(List<KnowledgeBaseArticle>)page3.getItems());
			assertContains(
				knowledgeBaseArticle3,
				(List<KnowledgeBaseArticle>)page3.getItems());
		}
	}

	@Test
	public void testGetKnowledgeBaseFolderKnowledgeBaseArticlesPageWithSortDateTime()
		throws Exception {

		testGetKnowledgeBaseFolderKnowledgeBaseArticlesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, knowledgeBaseArticle1, knowledgeBaseArticle2) -> {
				BeanTestUtil.setProperty(
					knowledgeBaseArticle1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetKnowledgeBaseFolderKnowledgeBaseArticlesPageWithSortDouble()
		throws Exception {

		testGetKnowledgeBaseFolderKnowledgeBaseArticlesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, knowledgeBaseArticle1, knowledgeBaseArticle2) -> {
				BeanTestUtil.setProperty(
					knowledgeBaseArticle1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					knowledgeBaseArticle2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetKnowledgeBaseFolderKnowledgeBaseArticlesPageWithSortInteger()
		throws Exception {

		testGetKnowledgeBaseFolderKnowledgeBaseArticlesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, knowledgeBaseArticle1, knowledgeBaseArticle2) -> {
				BeanTestUtil.setProperty(
					knowledgeBaseArticle1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					knowledgeBaseArticle2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetKnowledgeBaseFolderKnowledgeBaseArticlesPageWithSortString()
		throws Exception {

		testGetKnowledgeBaseFolderKnowledgeBaseArticlesPageWithSort(
			EntityField.Type.STRING,
			(entityField, knowledgeBaseArticle1, knowledgeBaseArticle2) -> {
				Class<?> clazz = knowledgeBaseArticle1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						knowledgeBaseArticle1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						knowledgeBaseArticle2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						knowledgeBaseArticle1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						knowledgeBaseArticle2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						knowledgeBaseArticle1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						knowledgeBaseArticle2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetKnowledgeBaseFolderKnowledgeBaseArticlesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, KnowledgeBaseArticle, KnowledgeBaseArticle,
				 Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long knowledgeBaseFolderId =
			testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_getKnowledgeBaseFolderId();

		KnowledgeBaseArticle knowledgeBaseArticle1 =
			randomKnowledgeBaseArticle();
		KnowledgeBaseArticle knowledgeBaseArticle2 =
			randomKnowledgeBaseArticle();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, knowledgeBaseArticle1, knowledgeBaseArticle2);
		}

		knowledgeBaseArticle1 =
			testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				knowledgeBaseFolderId, knowledgeBaseArticle1);

		knowledgeBaseArticle2 =
			testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				knowledgeBaseFolderId, knowledgeBaseArticle2);

		Page<KnowledgeBaseArticle> page =
			knowledgeBaseArticleResource.
				getKnowledgeBaseFolderKnowledgeBaseArticlesPage(
					knowledgeBaseFolderId, null, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<KnowledgeBaseArticle> ascPage =
				knowledgeBaseArticleResource.
					getKnowledgeBaseFolderKnowledgeBaseArticlesPage(
						knowledgeBaseFolderId, null, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				knowledgeBaseArticle1,
				(List<KnowledgeBaseArticle>)ascPage.getItems());
			assertContains(
				knowledgeBaseArticle2,
				(List<KnowledgeBaseArticle>)ascPage.getItems());

			Page<KnowledgeBaseArticle> descPage =
				knowledgeBaseArticleResource.
					getKnowledgeBaseFolderKnowledgeBaseArticlesPage(
						knowledgeBaseFolderId, null, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				knowledgeBaseArticle2,
				(List<KnowledgeBaseArticle>)descPage.getItems());
			assertContains(
				knowledgeBaseArticle1,
				(List<KnowledgeBaseArticle>)descPage.getItems());
		}
	}

	protected KnowledgeBaseArticle
			testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				Long knowledgeBaseFolderId,
				KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception {

		return knowledgeBaseArticleResource.
			postKnowledgeBaseFolderKnowledgeBaseArticle(
				knowledgeBaseFolderId, knowledgeBaseArticle);
	}

	protected Long
			testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_getKnowledgeBaseFolderId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_getIrrelevantKnowledgeBaseFolderId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetKnowledgeBaseFolderKnowledgeBaseArticlesPage()
		throws Exception {

		Long knowledgeBaseFolderId =
			testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_getKnowledgeBaseFolderId();

		GraphQLField graphQLField = new GraphQLField(
			"knowledgeBaseFolderKnowledgeBaseArticles",
			new HashMap<String, Object>() {
				{
					put("knowledgeBaseFolderId", knowledgeBaseFolderId);
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject knowledgeBaseFolderKnowledgeBaseArticlesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/knowledgeBaseFolderKnowledgeBaseArticles");

		long totalCount =
			knowledgeBaseFolderKnowledgeBaseArticlesJSONObject.getLong(
				"totalCount");

		KnowledgeBaseArticle knowledgeBaseArticle1 =
			testGraphQLKnowledgeBaseFolderKnowledgeBaseArticle_addKnowledgeBaseArticle(
				knowledgeBaseFolderId, randomKnowledgeBaseArticle());

		KnowledgeBaseArticle knowledgeBaseArticle2 =
			testGraphQLKnowledgeBaseFolderKnowledgeBaseArticle_addKnowledgeBaseArticle(
				knowledgeBaseFolderId, randomKnowledgeBaseArticle());

		knowledgeBaseFolderKnowledgeBaseArticlesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/knowledgeBaseFolderKnowledgeBaseArticles");

		Assert.assertEquals(
			totalCount + 2,
			knowledgeBaseFolderKnowledgeBaseArticlesJSONObject.getLong(
				"totalCount"));

		assertContains(
			knowledgeBaseArticle1,
			Arrays.asList(
				KnowledgeBaseArticleSerDes.toDTOs(
					knowledgeBaseFolderKnowledgeBaseArticlesJSONObject.
						getString("items"))));
		assertContains(
			knowledgeBaseArticle2,
			Arrays.asList(
				KnowledgeBaseArticleSerDes.toDTOs(
					knowledgeBaseFolderKnowledgeBaseArticlesJSONObject.
						getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		knowledgeBaseFolderKnowledgeBaseArticlesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField("headlessDelivery_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"JSONObject/knowledgeBaseFolderKnowledgeBaseArticles");

		Assert.assertEquals(
			totalCount + 2,
			knowledgeBaseFolderKnowledgeBaseArticlesJSONObject.getLong(
				"totalCount"));

		assertContains(
			knowledgeBaseArticle1,
			Arrays.asList(
				KnowledgeBaseArticleSerDes.toDTOs(
					knowledgeBaseFolderKnowledgeBaseArticlesJSONObject.
						getString("items"))));
		assertContains(
			knowledgeBaseArticle2,
			Arrays.asList(
				KnowledgeBaseArticleSerDes.toDTOs(
					knowledgeBaseFolderKnowledgeBaseArticlesJSONObject.
						getString("items"))));
	}

	@Test
	public void testGetSiteKnowledgeBaseArticleByExternalReferenceCode()
		throws Exception {

		KnowledgeBaseArticle postKnowledgeBaseArticle =
			testGetSiteKnowledgeBaseArticleByExternalReferenceCode_addKnowledgeBaseArticle();

		KnowledgeBaseArticle getKnowledgeBaseArticle =
			knowledgeBaseArticleResource.
				getSiteKnowledgeBaseArticleByExternalReferenceCode(
					postKnowledgeBaseArticle.getSiteId(),
					postKnowledgeBaseArticle.getExternalReferenceCode());

		assertEquals(postKnowledgeBaseArticle, getKnowledgeBaseArticle);
		assertValid(getKnowledgeBaseArticle);
	}

	protected KnowledgeBaseArticle
			testGetSiteKnowledgeBaseArticleByExternalReferenceCode_addKnowledgeBaseArticle()
		throws Exception {

		return knowledgeBaseArticleResource.postSiteKnowledgeBaseArticle(
			testGroup.getGroupId(), randomKnowledgeBaseArticle());
	}

	@Test
	public void testGraphQLGetSiteKnowledgeBaseArticleByExternalReferenceCode()
		throws Exception {

		KnowledgeBaseArticle knowledgeBaseArticle =
			testGraphQLGetSiteKnowledgeBaseArticleByExternalReferenceCode_addKnowledgeBaseArticle();

		// No namespace

		Assert.assertTrue(
			equals(
				knowledgeBaseArticle,
				KnowledgeBaseArticleSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"knowledgeBaseArticleByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" +
												knowledgeBaseArticle.
													getSiteId() + "\"");
										put(
											"externalReferenceCode",
											"\"" +
												knowledgeBaseArticle.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/knowledgeBaseArticleByExternalReferenceCode"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				knowledgeBaseArticle,
				KnowledgeBaseArticleSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"knowledgeBaseArticleByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													knowledgeBaseArticle.
														getSiteId() + "\"");
											put(
												"externalReferenceCode",
												"\"" +
													knowledgeBaseArticle.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/knowledgeBaseArticleByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetSiteKnowledgeBaseArticleByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"knowledgeBaseArticleByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + irrelevantGroup.getGroupId() + "\"");
								put(
									"externalReferenceCode",
									irrelevantExternalReferenceCode);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"knowledgeBaseArticleByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + irrelevantGroup.getGroupId() +
											"\"");
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected KnowledgeBaseArticle
			testGraphQLGetSiteKnowledgeBaseArticleByExternalReferenceCode_addKnowledgeBaseArticle()
		throws Exception {

		return testGraphQLSiteKnowledgeBaseArticle_addKnowledgeBaseArticle();
	}

	@Test
	public void testGetSiteKnowledgeBaseArticlePermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		KnowledgeBaseArticle postKnowledgeBaseArticle =
			testGetSiteKnowledgeBaseArticlePermissionsPage_addKnowledgeBaseArticle();

		Page<Permission> page =
			knowledgeBaseArticleResource.
				getSiteKnowledgeBaseArticlePermissionsPage(
					testGroup.getGroupId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected KnowledgeBaseArticle
			testGetSiteKnowledgeBaseArticlePermissionsPage_addKnowledgeBaseArticle()
		throws Exception {

		return knowledgeBaseArticleResource.postSiteKnowledgeBaseArticle(
			testGroup.getGroupId(), randomKnowledgeBaseArticle());
	}

	@Test
	public void testGraphQLGetSiteKnowledgeBaseArticlePermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		KnowledgeBaseArticle postKnowledgeBaseArticle =
			testGraphQLGetSiteKnowledgeBaseArticlePermissionsPage_addKnowledgeBaseArticle();

		GraphQLField graphQLField = new GraphQLField(
			"siteKnowledgeBaseArticlePermissions",
			new HashMap<String, Object>() {
				{
					put(
						"siteKey",
						"\"" + postKnowledgeBaseArticle.getSiteId() + "\"");
				}
			},
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject siteKnowledgeBaseArticlePermissionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/siteKnowledgeBaseArticlePermissions");

		Assert.assertNotNull(siteKnowledgeBaseArticlePermissionsJSONObject);
	}

	protected KnowledgeBaseArticle
			testGraphQLGetSiteKnowledgeBaseArticlePermissionsPage_addKnowledgeBaseArticle()
		throws Exception {

		return testGraphQLSiteKnowledgeBaseArticle_addKnowledgeBaseArticle();
	}

	@Test
	public void testGetSiteKnowledgeBaseArticlesPage() throws Exception {
		Long siteId = testGetSiteKnowledgeBaseArticlesPage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteKnowledgeBaseArticlesPage_getIrrelevantSiteId();

		Page<KnowledgeBaseArticle> page =
			knowledgeBaseArticleResource.getSiteKnowledgeBaseArticlesPage(
				siteId, null, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteId != null) {
			KnowledgeBaseArticle irrelevantKnowledgeBaseArticle =
				testGetSiteKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
					irrelevantSiteId, randomIrrelevantKnowledgeBaseArticle());

			page =
				knowledgeBaseArticleResource.getSiteKnowledgeBaseArticlesPage(
					irrelevantSiteId, null, null, null, null,
					Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantKnowledgeBaseArticle,
				(List<KnowledgeBaseArticle>)page.getItems());
			assertValid(
				page,
				testGetSiteKnowledgeBaseArticlesPage_getExpectedActions(
					irrelevantSiteId));
		}

		KnowledgeBaseArticle knowledgeBaseArticle1 =
			testGetSiteKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				siteId, randomKnowledgeBaseArticle());

		KnowledgeBaseArticle knowledgeBaseArticle2 =
			testGetSiteKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				siteId, randomKnowledgeBaseArticle());

		page = knowledgeBaseArticleResource.getSiteKnowledgeBaseArticlesPage(
			siteId, null, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			knowledgeBaseArticle1, (List<KnowledgeBaseArticle>)page.getItems());
		assertContains(
			knowledgeBaseArticle2, (List<KnowledgeBaseArticle>)page.getItems());
		assertValid(
			page,
			testGetSiteKnowledgeBaseArticlesPage_getExpectedActions(siteId));

		knowledgeBaseArticleResource.deleteKnowledgeBaseArticle(
			knowledgeBaseArticle1.getId());

		knowledgeBaseArticleResource.deleteKnowledgeBaseArticle(
			knowledgeBaseArticle2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSiteKnowledgeBaseArticlesPage_getExpectedActions(Long siteId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/sites/{siteId}/knowledge-base-articles/batch".
				replace("{siteId}", String.valueOf(siteId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetSiteKnowledgeBaseArticlesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteKnowledgeBaseArticlesPage_getSiteId();

		KnowledgeBaseArticle knowledgeBaseArticle1 =
			randomKnowledgeBaseArticle();

		knowledgeBaseArticle1 =
			testGetSiteKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				siteId, knowledgeBaseArticle1);

		for (EntityField entityField : entityFields) {
			Page<KnowledgeBaseArticle> page =
				knowledgeBaseArticleResource.getSiteKnowledgeBaseArticlesPage(
					siteId, null, null, null,
					getFilterString(
						entityField, "between", knowledgeBaseArticle1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(knowledgeBaseArticle1),
				(List<KnowledgeBaseArticle>)page.getItems());
		}
	}

	@Test
	public void testGetSiteKnowledgeBaseArticlesPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteKnowledgeBaseArticlesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteKnowledgeBaseArticlesPageWithFilterStringContains()
		throws Exception {

		testGetSiteKnowledgeBaseArticlesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteKnowledgeBaseArticlesPageWithFilterStringEquals()
		throws Exception {

		testGetSiteKnowledgeBaseArticlesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteKnowledgeBaseArticlesPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteKnowledgeBaseArticlesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetSiteKnowledgeBaseArticlesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteKnowledgeBaseArticlesPage_getSiteId();

		KnowledgeBaseArticle knowledgeBaseArticle1 =
			testGetSiteKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				siteId, randomKnowledgeBaseArticle());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		KnowledgeBaseArticle knowledgeBaseArticle2 =
			testGetSiteKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				siteId, randomKnowledgeBaseArticle());

		for (EntityField entityField : entityFields) {
			Page<KnowledgeBaseArticle> page =
				knowledgeBaseArticleResource.getSiteKnowledgeBaseArticlesPage(
					siteId, null, null, null,
					getFilterString(
						entityField, operator, knowledgeBaseArticle1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(knowledgeBaseArticle1),
				(List<KnowledgeBaseArticle>)page.getItems());
		}
	}

	@Test
	public void testGetSiteKnowledgeBaseArticlesPageWithPagination()
		throws Exception {

		Long siteId = testGetSiteKnowledgeBaseArticlesPage_getSiteId();

		Page<KnowledgeBaseArticle> knowledgeBaseArticlesPage =
			knowledgeBaseArticleResource.getSiteKnowledgeBaseArticlesPage(
				siteId, null, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			knowledgeBaseArticlesPage.getTotalCount());

		KnowledgeBaseArticle knowledgeBaseArticle1 =
			testGetSiteKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				siteId, randomKnowledgeBaseArticle());

		KnowledgeBaseArticle knowledgeBaseArticle2 =
			testGetSiteKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				siteId, randomKnowledgeBaseArticle());

		KnowledgeBaseArticle knowledgeBaseArticle3 =
			testGetSiteKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				siteId, randomKnowledgeBaseArticle());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<KnowledgeBaseArticle> page1 =
				knowledgeBaseArticleResource.getSiteKnowledgeBaseArticlesPage(
					siteId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				knowledgeBaseArticle1,
				(List<KnowledgeBaseArticle>)page1.getItems());

			Page<KnowledgeBaseArticle> page2 =
				knowledgeBaseArticleResource.getSiteKnowledgeBaseArticlesPage(
					siteId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				knowledgeBaseArticle2,
				(List<KnowledgeBaseArticle>)page2.getItems());

			Page<KnowledgeBaseArticle> page3 =
				knowledgeBaseArticleResource.getSiteKnowledgeBaseArticlesPage(
					siteId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				knowledgeBaseArticle3,
				(List<KnowledgeBaseArticle>)page3.getItems());
		}
		else {
			Page<KnowledgeBaseArticle> page1 =
				knowledgeBaseArticleResource.getSiteKnowledgeBaseArticlesPage(
					siteId, null, null, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<KnowledgeBaseArticle> knowledgeBaseArticles1 =
				(List<KnowledgeBaseArticle>)page1.getItems();

			Assert.assertEquals(
				knowledgeBaseArticles1.toString(), totalCount + 2,
				knowledgeBaseArticles1.size());

			Page<KnowledgeBaseArticle> page2 =
				knowledgeBaseArticleResource.getSiteKnowledgeBaseArticlesPage(
					siteId, null, null, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<KnowledgeBaseArticle> knowledgeBaseArticles2 =
				(List<KnowledgeBaseArticle>)page2.getItems();

			Assert.assertEquals(
				knowledgeBaseArticles2.toString(), 1,
				knowledgeBaseArticles2.size());

			Page<KnowledgeBaseArticle> page3 =
				knowledgeBaseArticleResource.getSiteKnowledgeBaseArticlesPage(
					siteId, null, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				knowledgeBaseArticle1,
				(List<KnowledgeBaseArticle>)page3.getItems());
			assertContains(
				knowledgeBaseArticle2,
				(List<KnowledgeBaseArticle>)page3.getItems());
			assertContains(
				knowledgeBaseArticle3,
				(List<KnowledgeBaseArticle>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteKnowledgeBaseArticlesPageWithSortDateTime()
		throws Exception {

		testGetSiteKnowledgeBaseArticlesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, knowledgeBaseArticle1, knowledgeBaseArticle2) -> {
				BeanTestUtil.setProperty(
					knowledgeBaseArticle1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteKnowledgeBaseArticlesPageWithSortDouble()
		throws Exception {

		testGetSiteKnowledgeBaseArticlesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, knowledgeBaseArticle1, knowledgeBaseArticle2) -> {
				BeanTestUtil.setProperty(
					knowledgeBaseArticle1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					knowledgeBaseArticle2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteKnowledgeBaseArticlesPageWithSortInteger()
		throws Exception {

		testGetSiteKnowledgeBaseArticlesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, knowledgeBaseArticle1, knowledgeBaseArticle2) -> {
				BeanTestUtil.setProperty(
					knowledgeBaseArticle1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					knowledgeBaseArticle2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteKnowledgeBaseArticlesPageWithSortString()
		throws Exception {

		testGetSiteKnowledgeBaseArticlesPageWithSort(
			EntityField.Type.STRING,
			(entityField, knowledgeBaseArticle1, knowledgeBaseArticle2) -> {
				Class<?> clazz = knowledgeBaseArticle1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						knowledgeBaseArticle1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						knowledgeBaseArticle2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						knowledgeBaseArticle1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						knowledgeBaseArticle2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						knowledgeBaseArticle1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						knowledgeBaseArticle2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSiteKnowledgeBaseArticlesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, KnowledgeBaseArticle, KnowledgeBaseArticle,
				 Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteKnowledgeBaseArticlesPage_getSiteId();

		KnowledgeBaseArticle knowledgeBaseArticle1 =
			randomKnowledgeBaseArticle();
		KnowledgeBaseArticle knowledgeBaseArticle2 =
			randomKnowledgeBaseArticle();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, knowledgeBaseArticle1, knowledgeBaseArticle2);
		}

		knowledgeBaseArticle1 =
			testGetSiteKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				siteId, knowledgeBaseArticle1);

		knowledgeBaseArticle2 =
			testGetSiteKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				siteId, knowledgeBaseArticle2);

		Page<KnowledgeBaseArticle> page =
			knowledgeBaseArticleResource.getSiteKnowledgeBaseArticlesPage(
				siteId, null, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<KnowledgeBaseArticle> ascPage =
				knowledgeBaseArticleResource.getSiteKnowledgeBaseArticlesPage(
					siteId, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				knowledgeBaseArticle1,
				(List<KnowledgeBaseArticle>)ascPage.getItems());
			assertContains(
				knowledgeBaseArticle2,
				(List<KnowledgeBaseArticle>)ascPage.getItems());

			Page<KnowledgeBaseArticle> descPage =
				knowledgeBaseArticleResource.getSiteKnowledgeBaseArticlesPage(
					siteId, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				knowledgeBaseArticle2,
				(List<KnowledgeBaseArticle>)descPage.getItems());
			assertContains(
				knowledgeBaseArticle1,
				(List<KnowledgeBaseArticle>)descPage.getItems());
		}
	}

	protected KnowledgeBaseArticle
			testGetSiteKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				Long siteId, KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception {

		return knowledgeBaseArticleResource.postSiteKnowledgeBaseArticle(
			siteId, knowledgeBaseArticle);
	}

	protected Long testGetSiteKnowledgeBaseArticlesPage_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected Long testGetSiteKnowledgeBaseArticlesPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	@Test
	public void testGraphQLGetSiteKnowledgeBaseArticlesPage() throws Exception {
		Long siteId = testGetSiteKnowledgeBaseArticlesPage_getSiteId();

		GraphQLField graphQLField = new GraphQLField(
			"knowledgeBaseArticles",
			new HashMap<String, Object>() {
				{
					put("siteKey", "\"" + siteId + "\"");
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject knowledgeBaseArticlesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/knowledgeBaseArticles");

		long totalCount = knowledgeBaseArticlesJSONObject.getLong("totalCount");

		KnowledgeBaseArticle knowledgeBaseArticle1 =
			testGraphQLSiteKnowledgeBaseArticle_addKnowledgeBaseArticle(
				siteId, randomKnowledgeBaseArticle());

		KnowledgeBaseArticle knowledgeBaseArticle2 =
			testGraphQLSiteKnowledgeBaseArticle_addKnowledgeBaseArticle(
				siteId, randomKnowledgeBaseArticle());

		knowledgeBaseArticlesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/knowledgeBaseArticles");

		Assert.assertEquals(
			totalCount + 2,
			knowledgeBaseArticlesJSONObject.getLong("totalCount"));

		assertContains(
			knowledgeBaseArticle1,
			Arrays.asList(
				KnowledgeBaseArticleSerDes.toDTOs(
					knowledgeBaseArticlesJSONObject.getString("items"))));
		assertContains(
			knowledgeBaseArticle2,
			Arrays.asList(
				KnowledgeBaseArticleSerDes.toDTOs(
					knowledgeBaseArticlesJSONObject.getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		knowledgeBaseArticlesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessDelivery_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
			"JSONObject/knowledgeBaseArticles");

		Assert.assertEquals(
			totalCount + 2,
			knowledgeBaseArticlesJSONObject.getLong("totalCount"));

		assertContains(
			knowledgeBaseArticle1,
			Arrays.asList(
				KnowledgeBaseArticleSerDes.toDTOs(
					knowledgeBaseArticlesJSONObject.getString("items"))));
		assertContains(
			knowledgeBaseArticle2,
			Arrays.asList(
				KnowledgeBaseArticleSerDes.toDTOs(
					knowledgeBaseArticlesJSONObject.getString("items"))));
	}

	@Test
	public void testPatchKnowledgeBaseArticle() throws Exception {
		KnowledgeBaseArticle postKnowledgeBaseArticle =
			testPatchKnowledgeBaseArticle_addKnowledgeBaseArticle();

		KnowledgeBaseArticle randomPatchKnowledgeBaseArticle =
			randomPatchKnowledgeBaseArticle();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		KnowledgeBaseArticle patchKnowledgeBaseArticle =
			knowledgeBaseArticleResource.patchKnowledgeBaseArticle(
				postKnowledgeBaseArticle.getId(),
				randomPatchKnowledgeBaseArticle);

		KnowledgeBaseArticle expectedPatchKnowledgeBaseArticle =
			postKnowledgeBaseArticle.clone();

		BeanTestUtil.copyProperties(
			randomPatchKnowledgeBaseArticle, expectedPatchKnowledgeBaseArticle);

		KnowledgeBaseArticle getKnowledgeBaseArticle =
			knowledgeBaseArticleResource.getKnowledgeBaseArticle(
				patchKnowledgeBaseArticle.getId());

		assertEquals(
			expectedPatchKnowledgeBaseArticle, getKnowledgeBaseArticle);
		assertValid(getKnowledgeBaseArticle);
	}

	protected KnowledgeBaseArticle
			testPatchKnowledgeBaseArticle_addKnowledgeBaseArticle()
		throws Exception {

		return knowledgeBaseArticleResource.postSiteKnowledgeBaseArticle(
			testGroup.getGroupId(), randomKnowledgeBaseArticle());
	}

	@Test
	public void testPostKnowledgeBaseArticleKnowledgeBaseArticle()
		throws Exception {

		KnowledgeBaseArticle randomKnowledgeBaseArticle =
			randomKnowledgeBaseArticle();

		KnowledgeBaseArticle postKnowledgeBaseArticle =
			testPostKnowledgeBaseArticleKnowledgeBaseArticle_addKnowledgeBaseArticle(
				randomKnowledgeBaseArticle);

		assertEquals(randomKnowledgeBaseArticle, postKnowledgeBaseArticle);
		assertValid(postKnowledgeBaseArticle);
	}

	protected KnowledgeBaseArticle
			testPostKnowledgeBaseArticleKnowledgeBaseArticle_addKnowledgeBaseArticle(
				KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception {

		return knowledgeBaseArticleResource.
			postKnowledgeBaseArticleKnowledgeBaseArticle(
				testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_getParentKnowledgeBaseArticleId(),
				knowledgeBaseArticle);
	}

	@Test
	public void testGraphQLPostKnowledgeBaseArticleKnowledgeBaseArticle()
		throws Exception {

		KnowledgeBaseArticle randomKnowledgeBaseArticle =
			randomKnowledgeBaseArticle();

		KnowledgeBaseArticle knowledgeBaseArticle =
			testGraphQLKnowledgeBaseArticle_addKnowledgeBaseArticle(
				testGroup.getGroupId(), randomKnowledgeBaseArticle);

		Assert.assertTrue(
			equals(randomKnowledgeBaseArticle, knowledgeBaseArticle));
	}

	@Test
	public void testPostKnowledgeBaseFolderKnowledgeBaseArticle()
		throws Exception {

		KnowledgeBaseArticle randomKnowledgeBaseArticle =
			randomKnowledgeBaseArticle();

		KnowledgeBaseArticle postKnowledgeBaseArticle =
			testPostKnowledgeBaseFolderKnowledgeBaseArticle_addKnowledgeBaseArticle(
				randomKnowledgeBaseArticle);

		assertEquals(randomKnowledgeBaseArticle, postKnowledgeBaseArticle);
		assertValid(postKnowledgeBaseArticle);
	}

	protected KnowledgeBaseArticle
			testPostKnowledgeBaseFolderKnowledgeBaseArticle_addKnowledgeBaseArticle(
				KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception {

		return knowledgeBaseArticleResource.
			postKnowledgeBaseFolderKnowledgeBaseArticle(
				testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_getKnowledgeBaseFolderId(),
				knowledgeBaseArticle);
	}

	@Test
	public void testGraphQLPostKnowledgeBaseFolderKnowledgeBaseArticle()
		throws Exception {

		KnowledgeBaseArticle randomKnowledgeBaseArticle =
			randomKnowledgeBaseArticle();

		KnowledgeBaseArticle knowledgeBaseArticle =
			testGraphQLKnowledgeBaseFolderKnowledgeBaseArticle_addKnowledgeBaseArticle(
				testGraphQLPostKnowledgeBaseFolderKnowledgeBaseArticle_getKnowledgeBaseFolderId(),
				randomKnowledgeBaseArticle);

		Assert.assertTrue(
			equals(randomKnowledgeBaseArticle, knowledgeBaseArticle));
	}

	protected Long
			testGraphQLPostKnowledgeBaseFolderKnowledgeBaseArticle_getKnowledgeBaseFolderId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSiteKnowledgeBaseArticle() throws Exception {
		KnowledgeBaseArticle randomKnowledgeBaseArticle =
			randomKnowledgeBaseArticle();

		KnowledgeBaseArticle postKnowledgeBaseArticle =
			testPostSiteKnowledgeBaseArticle_addKnowledgeBaseArticle(
				randomKnowledgeBaseArticle);

		assertEquals(randomKnowledgeBaseArticle, postKnowledgeBaseArticle);
		assertValid(postKnowledgeBaseArticle);
	}

	protected KnowledgeBaseArticle
			testPostSiteKnowledgeBaseArticle_addKnowledgeBaseArticle(
				KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception {

		return knowledgeBaseArticleResource.postSiteKnowledgeBaseArticle(
			testGetSiteKnowledgeBaseArticlesPage_getSiteId(),
			knowledgeBaseArticle);
	}

	@Test
	public void testGraphQLPostSiteKnowledgeBaseArticle() throws Exception {
		KnowledgeBaseArticle randomKnowledgeBaseArticle =
			randomKnowledgeBaseArticle();

		KnowledgeBaseArticle knowledgeBaseArticle =
			testGraphQLSiteKnowledgeBaseArticle_addKnowledgeBaseArticle(
				testGroup.getGroupId(), randomKnowledgeBaseArticle);

		Assert.assertTrue(
			equals(randomKnowledgeBaseArticle, knowledgeBaseArticle));
	}

	@Test
	public void testPutKnowledgeBaseArticle() throws Exception {
		KnowledgeBaseArticle postKnowledgeBaseArticle =
			testPutKnowledgeBaseArticle_addKnowledgeBaseArticle();

		KnowledgeBaseArticle randomKnowledgeBaseArticle =
			randomKnowledgeBaseArticle();

		KnowledgeBaseArticle putKnowledgeBaseArticle =
			knowledgeBaseArticleResource.putKnowledgeBaseArticle(
				postKnowledgeBaseArticle.getId(), randomKnowledgeBaseArticle);

		assertEquals(randomKnowledgeBaseArticle, putKnowledgeBaseArticle);
		assertValid(putKnowledgeBaseArticle);

		KnowledgeBaseArticle getKnowledgeBaseArticle =
			knowledgeBaseArticleResource.getKnowledgeBaseArticle(
				putKnowledgeBaseArticle.getId());

		assertEquals(randomKnowledgeBaseArticle, getKnowledgeBaseArticle);
		assertValid(getKnowledgeBaseArticle);
	}

	protected KnowledgeBaseArticle
			testPutKnowledgeBaseArticle_addKnowledgeBaseArticle()
		throws Exception {

		return knowledgeBaseArticleResource.postSiteKnowledgeBaseArticle(
			testGroup.getGroupId(), randomKnowledgeBaseArticle());
	}

	@Test
	public void testPutKnowledgeBaseArticlePermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		KnowledgeBaseArticle knowledgeBaseArticle =
			testPutKnowledgeBaseArticlePermissionsPage_addKnowledgeBaseArticle();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			knowledgeBaseArticleResource.
				putKnowledgeBaseArticlePermissionsPageHttpResponse(
					knowledgeBaseArticle.getId(),
					new Permission[] {
						new Permission() {
							{
								setActionIds(new String[] {"VIEW"});
								setRoleName(role.getName());
							}
						}
					}));

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseArticleResource.
				putKnowledgeBaseArticlePermissionsPageHttpResponse(
					0L,
					new Permission[] {
						new Permission() {
							{
								setActionIds(new String[] {"-"});
								setRoleName("-");
							}
						}
					}));
	}

	protected KnowledgeBaseArticle
			testPutKnowledgeBaseArticlePermissionsPage_addKnowledgeBaseArticle()
		throws Exception {

		return knowledgeBaseArticleResource.postSiteKnowledgeBaseArticle(
			testGroup.getGroupId(), randomKnowledgeBaseArticle());
	}

	@Test
	public void testPutKnowledgeBaseArticleSubscribe() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		KnowledgeBaseArticle knowledgeBaseArticle =
			testPutKnowledgeBaseArticleSubscribe_addKnowledgeBaseArticle();

		assertHttpResponseStatusCode(
			204,
			knowledgeBaseArticleResource.
				putKnowledgeBaseArticleSubscribeHttpResponse(
					knowledgeBaseArticle.getId()));

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseArticleResource.
				putKnowledgeBaseArticleSubscribeHttpResponse(0L));
	}

	protected KnowledgeBaseArticle
			testPutKnowledgeBaseArticleSubscribe_addKnowledgeBaseArticle()
		throws Exception {

		return knowledgeBaseArticleResource.postSiteKnowledgeBaseArticle(
			testGroup.getGroupId(), randomKnowledgeBaseArticle());
	}

	@Test
	public void testPutKnowledgeBaseArticleUnsubscribe() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		KnowledgeBaseArticle knowledgeBaseArticle =
			testPutKnowledgeBaseArticleUnsubscribe_addKnowledgeBaseArticle();

		assertHttpResponseStatusCode(
			204,
			knowledgeBaseArticleResource.
				putKnowledgeBaseArticleUnsubscribeHttpResponse(
					knowledgeBaseArticle.getId()));

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseArticleResource.
				putKnowledgeBaseArticleUnsubscribeHttpResponse(0L));
	}

	protected KnowledgeBaseArticle
			testPutKnowledgeBaseArticleUnsubscribe_addKnowledgeBaseArticle()
		throws Exception {

		return knowledgeBaseArticleResource.postSiteKnowledgeBaseArticle(
			testGroup.getGroupId(), randomKnowledgeBaseArticle());
	}

	@Test
	public void testPutSiteKnowledgeBaseArticleByExternalReferenceCode()
		throws Exception {

		KnowledgeBaseArticle postKnowledgeBaseArticle =
			testPutSiteKnowledgeBaseArticleByExternalReferenceCode_addKnowledgeBaseArticle();

		KnowledgeBaseArticle randomKnowledgeBaseArticle =
			randomKnowledgeBaseArticle();

		KnowledgeBaseArticle putKnowledgeBaseArticle =
			knowledgeBaseArticleResource.
				putSiteKnowledgeBaseArticleByExternalReferenceCode(
					postKnowledgeBaseArticle.getSiteId(),
					postKnowledgeBaseArticle.getExternalReferenceCode(),
					randomKnowledgeBaseArticle);

		assertEquals(randomKnowledgeBaseArticle, putKnowledgeBaseArticle);
		assertValid(putKnowledgeBaseArticle);

		KnowledgeBaseArticle getKnowledgeBaseArticle =
			knowledgeBaseArticleResource.
				getSiteKnowledgeBaseArticleByExternalReferenceCode(
					putKnowledgeBaseArticle.getSiteId(),
					putKnowledgeBaseArticle.getExternalReferenceCode());

		assertEquals(randomKnowledgeBaseArticle, getKnowledgeBaseArticle);
		assertValid(getKnowledgeBaseArticle);

		KnowledgeBaseArticle newKnowledgeBaseArticle =
			testPutSiteKnowledgeBaseArticleByExternalReferenceCode_createKnowledgeBaseArticle();

		putKnowledgeBaseArticle =
			knowledgeBaseArticleResource.
				putSiteKnowledgeBaseArticleByExternalReferenceCode(
					newKnowledgeBaseArticle.getSiteId(),
					newKnowledgeBaseArticle.getExternalReferenceCode(),
					newKnowledgeBaseArticle);

		assertEquals(newKnowledgeBaseArticle, putKnowledgeBaseArticle);
		assertValid(putKnowledgeBaseArticle);

		getKnowledgeBaseArticle =
			knowledgeBaseArticleResource.
				getSiteKnowledgeBaseArticleByExternalReferenceCode(
					putKnowledgeBaseArticle.getSiteId(),
					putKnowledgeBaseArticle.getExternalReferenceCode());

		assertEquals(newKnowledgeBaseArticle, getKnowledgeBaseArticle);

		Assert.assertEquals(
			newKnowledgeBaseArticle.getExternalReferenceCode(),
			putKnowledgeBaseArticle.getExternalReferenceCode());
	}

	protected KnowledgeBaseArticle
			testPutSiteKnowledgeBaseArticleByExternalReferenceCode_addKnowledgeBaseArticle()
		throws Exception {

		return knowledgeBaseArticleResource.postSiteKnowledgeBaseArticle(
			testGroup.getGroupId(), randomKnowledgeBaseArticle());
	}

	protected KnowledgeBaseArticle
			testPutSiteKnowledgeBaseArticleByExternalReferenceCode_createKnowledgeBaseArticle()
		throws Exception {

		return randomKnowledgeBaseArticle();
	}

	@Test
	public void testPutSiteKnowledgeBaseArticlePermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		KnowledgeBaseArticle knowledgeBaseArticle =
			testPutSiteKnowledgeBaseArticlePermissionsPage_addKnowledgeBaseArticle();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			knowledgeBaseArticleResource.
				putSiteKnowledgeBaseArticlePermissionsPageHttpResponse(
					testGroup.getGroupId(),
					new Permission[] {
						new Permission() {
							{
								setActionIds(new String[] {"PERMISSIONS"});
								setRoleName(role.getName());
							}
						}
					}));

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseArticleResource.
				putSiteKnowledgeBaseArticlePermissionsPageHttpResponse(
					testGroup.getGroupId(),
					new Permission[] {
						new Permission() {
							{
								setActionIds(new String[] {"-"});
								setRoleName("-");
							}
						}
					}));
	}

	protected KnowledgeBaseArticle
			testPutSiteKnowledgeBaseArticlePermissionsPage_addKnowledgeBaseArticle()
		throws Exception {

		return knowledgeBaseArticleResource.postSiteKnowledgeBaseArticle(
			testGroup.getGroupId(), randomKnowledgeBaseArticle());
	}

	@Test
	public void testPutSiteKnowledgeBaseArticleSubscribe() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		KnowledgeBaseArticle knowledgeBaseArticle =
			testPutSiteKnowledgeBaseArticleSubscribe_addKnowledgeBaseArticle();

		assertHttpResponseStatusCode(
			204,
			knowledgeBaseArticleResource.
				putSiteKnowledgeBaseArticleSubscribeHttpResponse(
					knowledgeBaseArticle.getSiteId()));

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseArticleResource.
				putSiteKnowledgeBaseArticleSubscribeHttpResponse(
					knowledgeBaseArticle.getSiteId()));
	}

	protected KnowledgeBaseArticle
			testPutSiteKnowledgeBaseArticleSubscribe_addKnowledgeBaseArticle()
		throws Exception {

		return knowledgeBaseArticleResource.postSiteKnowledgeBaseArticle(
			testGroup.getGroupId(), randomKnowledgeBaseArticle());
	}

	@Test
	public void testPutSiteKnowledgeBaseArticleUnsubscribe() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		KnowledgeBaseArticle knowledgeBaseArticle =
			testPutSiteKnowledgeBaseArticleUnsubscribe_addKnowledgeBaseArticle();

		assertHttpResponseStatusCode(
			204,
			knowledgeBaseArticleResource.
				putSiteKnowledgeBaseArticleUnsubscribeHttpResponse(
					knowledgeBaseArticle.getSiteId()));

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseArticleResource.
				putSiteKnowledgeBaseArticleUnsubscribeHttpResponse(
					knowledgeBaseArticle.getSiteId()));
	}

	protected KnowledgeBaseArticle
			testPutSiteKnowledgeBaseArticleUnsubscribe_addKnowledgeBaseArticle()
		throws Exception {

		return knowledgeBaseArticleResource.postSiteKnowledgeBaseArticle(
			testGroup.getGroupId(), randomKnowledgeBaseArticle());
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		KnowledgeBaseArticle knowledgeBaseArticle1 =
			testBatchEngineDeleteImportTask_addKnowledgeBaseArticle();

		testBatchEngineDeleteImportTask_deleteKnowledgeBaseArticle(
			200, null, knowledgeBaseArticle1.getId());

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseArticleResource.getKnowledgeBaseArticleHttpResponse(
				knowledgeBaseArticle1.getId()));
	}

	protected KnowledgeBaseArticle
			testBatchEngineDeleteImportTask_addKnowledgeBaseArticle()
		throws Exception {

		return testDeleteKnowledgeBaseArticle_addKnowledgeBaseArticle();
	}

	protected void testBatchEngineDeleteImportTask_deleteKnowledgeBaseArticle(
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
				"com.liferay.headless.delivery.dto.v1_0.KnowledgeBaseArticle",
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

	@Test
	public void testGetKnowledgeBaseArticleMyRating() throws Exception {
		KnowledgeBaseArticle postKnowledgeBaseArticle =
			testGetKnowledgeBaseArticle_addKnowledgeBaseArticle();

		Rating postRating = testGetKnowledgeBaseArticleMyRating_addRating(
			postKnowledgeBaseArticle.getId(), randomRating());

		Rating getRating =
			knowledgeBaseArticleResource.getKnowledgeBaseArticleMyRating(
				postKnowledgeBaseArticle.getId());

		assertEquals(postRating, getRating);
		assertValid(getRating);
	}

	protected Rating testGetKnowledgeBaseArticleMyRating_addRating(
			long knowledgeBaseArticleId, Rating rating)
		throws Exception {

		return knowledgeBaseArticleResource.postKnowledgeBaseArticleMyRating(
			knowledgeBaseArticleId, rating);
	}

	@Test
	public void testPostKnowledgeBaseArticleMyRating() throws Exception {
		Assert.assertTrue(true);
	}

	@Test
	public void testPutKnowledgeBaseArticleMyRating() throws Exception {
		KnowledgeBaseArticle postKnowledgeBaseArticle =
			testPutKnowledgeBaseArticle_addKnowledgeBaseArticle();

		testPutKnowledgeBaseArticleMyRating_addRating(
			postKnowledgeBaseArticle.getId(), randomRating());

		Rating randomRating = randomRating();

		Rating putRating =
			knowledgeBaseArticleResource.putKnowledgeBaseArticleMyRating(
				postKnowledgeBaseArticle.getId(), randomRating);

		assertEquals(randomRating, putRating);
		assertValid(putRating);
	}

	protected Rating testPutKnowledgeBaseArticleMyRating_addRating(
			long knowledgeBaseArticleId, Rating rating)
		throws Exception {

		return knowledgeBaseArticleResource.postKnowledgeBaseArticleMyRating(
			knowledgeBaseArticleId, rating);
	}

	protected KnowledgeBaseArticle
			testGraphQLKnowledgeBaseArticle_addKnowledgeBaseArticle()
		throws Exception {

		return testGraphQLKnowledgeBaseArticle_addKnowledgeBaseArticle(
			testGroup.getGroupId(), randomKnowledgeBaseArticle());
	}

	protected KnowledgeBaseArticle
			testGraphQLKnowledgeBaseArticle_addKnowledgeBaseArticle(
				Long siteId, KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception {

		JSONDeserializer<KnowledgeBaseArticle> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(KnowledgeBaseArticle.class)) {

			if (getGraphQLValue(field.get(knowledgeBaseArticle)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(knowledgeBaseArticle)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteKnowledgeBaseArticle",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("knowledgeBaseArticle", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteKnowledgeBaseArticle"),
			KnowledgeBaseArticle.class);
	}

	protected KnowledgeBaseArticle
			testGraphQLSiteKnowledgeBaseArticle_addKnowledgeBaseArticle()
		throws Exception {

		return testGraphQLSiteKnowledgeBaseArticle_addKnowledgeBaseArticle(
			testGroup.getGroupId(), randomKnowledgeBaseArticle());
	}

	protected KnowledgeBaseArticle
			testGraphQLSiteKnowledgeBaseArticle_addKnowledgeBaseArticle(
				Long siteId, KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception {

		JSONDeserializer<KnowledgeBaseArticle> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(KnowledgeBaseArticle.class)) {

			if (getGraphQLValue(field.get(knowledgeBaseArticle)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(knowledgeBaseArticle)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteKnowledgeBaseArticle",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("knowledgeBaseArticle", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteKnowledgeBaseArticle"),
			KnowledgeBaseArticle.class);
	}

	protected KnowledgeBaseArticle
			testGraphQLKnowledgeBaseFolderKnowledgeBaseArticle_addKnowledgeBaseArticle()
		throws Exception {

		return testGraphQLKnowledgeBaseFolderKnowledgeBaseArticle_addKnowledgeBaseArticle(
			testGraphQLKnowledgeBaseFolderKnowledgeBaseArticle_getKnowledgeBaseFolderId(),
			randomKnowledgeBaseArticle());
	}

	protected Long
			testGraphQLKnowledgeBaseFolderKnowledgeBaseArticle_getKnowledgeBaseFolderId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected KnowledgeBaseArticle
			testGraphQLKnowledgeBaseFolderKnowledgeBaseArticle_addKnowledgeBaseArticle(
				Long knowledgeBaseFolderId,
				KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception {

		JSONDeserializer<KnowledgeBaseArticle> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(KnowledgeBaseArticle.class)) {

			if (getGraphQLValue(field.get(knowledgeBaseArticle)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(knowledgeBaseArticle)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createKnowledgeBaseFolderKnowledgeBaseArticle",
						new HashMap<String, Object>() {
							{
								put(
									"knowledgeBaseFolderId",
									knowledgeBaseFolderId);
								put("knowledgeBaseArticle", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data",
				"JSONObject/createKnowledgeBaseFolderKnowledgeBaseArticle"),
			KnowledgeBaseArticle.class);
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
		KnowledgeBaseArticle knowledgeBaseArticle,
		List<KnowledgeBaseArticle> knowledgeBaseArticles) {

		boolean contains = false;

		for (KnowledgeBaseArticle item : knowledgeBaseArticles) {
			if (equals(knowledgeBaseArticle, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			knowledgeBaseArticles + " does not contain " + knowledgeBaseArticle,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		KnowledgeBaseArticle knowledgeBaseArticle1,
		KnowledgeBaseArticle knowledgeBaseArticle2) {

		Assert.assertTrue(
			knowledgeBaseArticle1 + " does not equal " + knowledgeBaseArticle2,
			equals(knowledgeBaseArticle1, knowledgeBaseArticle2));
	}

	protected void assertEquals(
		List<KnowledgeBaseArticle> knowledgeBaseArticles1,
		List<KnowledgeBaseArticle> knowledgeBaseArticles2) {

		Assert.assertEquals(
			knowledgeBaseArticles1.size(), knowledgeBaseArticles2.size());

		for (int i = 0; i < knowledgeBaseArticles1.size(); i++) {
			KnowledgeBaseArticle knowledgeBaseArticle1 =
				knowledgeBaseArticles1.get(i);
			KnowledgeBaseArticle knowledgeBaseArticle2 =
				knowledgeBaseArticles2.get(i);

			assertEquals(knowledgeBaseArticle1, knowledgeBaseArticle2);
		}
	}

	protected void assertEquals(Rating rating1, Rating rating2) {
		Assert.assertTrue(
			rating1 + " does not equal " + rating2, equals(rating1, rating2));
	}

	protected void assertEqualsIgnoringOrder(
		List<KnowledgeBaseArticle> knowledgeBaseArticles1,
		List<KnowledgeBaseArticle> knowledgeBaseArticles2) {

		Assert.assertEquals(
			knowledgeBaseArticles1.size(), knowledgeBaseArticles2.size());

		for (KnowledgeBaseArticle knowledgeBaseArticle1 :
				knowledgeBaseArticles1) {

			boolean contains = false;

			for (KnowledgeBaseArticle knowledgeBaseArticle2 :
					knowledgeBaseArticles2) {

				if (equals(knowledgeBaseArticle1, knowledgeBaseArticle2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				knowledgeBaseArticles2 + " does not contain " +
					knowledgeBaseArticle1,
				contains);
		}
	}

	protected void assertValid(KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception {

		boolean valid = true;

		if (knowledgeBaseArticle.getDateCreated() == null) {
			valid = false;
		}

		if (knowledgeBaseArticle.getDateModified() == null) {
			valid = false;
		}

		if (knowledgeBaseArticle.getId() == null) {
			valid = false;
		}

		if (!Objects.equals(
				knowledgeBaseArticle.getSiteId(), testGroup.getGroupId())) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (knowledgeBaseArticle.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("aggregateRating", additionalAssertFieldName)) {
				if (knowledgeBaseArticle.getAggregateRating() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("articleBody", additionalAssertFieldName)) {
				if (knowledgeBaseArticle.getArticleBody() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (knowledgeBaseArticle.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (knowledgeBaseArticle.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("datePublished", additionalAssertFieldName)) {
				if (knowledgeBaseArticle.getDatePublished() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (knowledgeBaseArticle.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("encodingFormat", additionalAssertFieldName)) {
				if (knowledgeBaseArticle.getEncodingFormat() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (knowledgeBaseArticle.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("friendlyUrlPath", additionalAssertFieldName)) {
				if (knowledgeBaseArticle.getFriendlyUrlPath() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("keywords", additionalAssertFieldName)) {
				if (knowledgeBaseArticle.getKeywords() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfAttachments", additionalAssertFieldName)) {

				if (knowledgeBaseArticle.getNumberOfAttachments() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfKnowledgeBaseArticles",
					additionalAssertFieldName)) {

				if (knowledgeBaseArticle.getNumberOfKnowledgeBaseArticles() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"parentKnowledgeBaseArticleId",
					additionalAssertFieldName)) {

				if (knowledgeBaseArticle.getParentKnowledgeBaseArticleId() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"parentKnowledgeBaseFolder", additionalAssertFieldName)) {

				if (knowledgeBaseArticle.getParentKnowledgeBaseFolder() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"parentKnowledgeBaseFolderId", additionalAssertFieldName)) {

				if (knowledgeBaseArticle.getParentKnowledgeBaseFolderId() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("relatedContents", additionalAssertFieldName)) {
				if (knowledgeBaseArticle.getRelatedContents() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("subscribed", additionalAssertFieldName)) {
				if (knowledgeBaseArticle.getSubscribed() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryBriefs", additionalAssertFieldName)) {

				if (knowledgeBaseArticle.getTaxonomyCategoryBriefs() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryIds", additionalAssertFieldName)) {

				if (knowledgeBaseArticle.getTaxonomyCategoryIds() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (knowledgeBaseArticle.getTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (knowledgeBaseArticle.getViewableBy() == null) {
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

	protected void assertValid(Page<KnowledgeBaseArticle> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<KnowledgeBaseArticle> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<KnowledgeBaseArticle> knowledgeBaseArticles =
			page.getItems();

		int size = knowledgeBaseArticles.size();

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

	protected void assertValid(Rating rating) {
		boolean valid = true;

		if (rating.getDateCreated() == null) {
			valid = false;
		}

		if (rating.getDateModified() == null) {
			valid = false;
		}

		if (rating.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalRatingAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (rating.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("bestRating", additionalAssertFieldName)) {
				if (rating.getBestRating() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (rating.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("ratingValue", additionalAssertFieldName)) {
				if (rating.getRatingValue() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("worstRating", additionalAssertFieldName)) {
				if (rating.getWorstRating() == null) {
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

	protected String[] getAdditionalAssertFieldNames() {
		return new String[0];
	}

	protected String[] getAdditionalRatingAssertFieldNames() {
		return new String[0];
	}

	protected List<GraphQLField> getGraphQLFields() throws Exception {
		List<GraphQLField> graphQLFields = new ArrayList<>();

		graphQLFields.add(new GraphQLField("externalReferenceCode"));

		graphQLFields.add(new GraphQLField("id"));

		graphQLFields.add(new GraphQLField("siteId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.delivery.dto.v1_0.KnowledgeBaseArticle.
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
		KnowledgeBaseArticle knowledgeBaseArticle1,
		KnowledgeBaseArticle knowledgeBaseArticle2) {

		if (knowledgeBaseArticle1 == knowledgeBaseArticle2) {
			return true;
		}

		if (!Objects.equals(
				knowledgeBaseArticle1.getSiteId(),
				knowledgeBaseArticle2.getSiteId())) {

			return false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)knowledgeBaseArticle1.getActions(),
						(Map)knowledgeBaseArticle2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("aggregateRating", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						knowledgeBaseArticle1.getAggregateRating(),
						knowledgeBaseArticle2.getAggregateRating())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("articleBody", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						knowledgeBaseArticle1.getArticleBody(),
						knowledgeBaseArticle2.getArticleBody())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						knowledgeBaseArticle1.getCreator(),
						knowledgeBaseArticle2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						knowledgeBaseArticle1.getCustomFields(),
						knowledgeBaseArticle2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						knowledgeBaseArticle1.getDateCreated(),
						knowledgeBaseArticle2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						knowledgeBaseArticle1.getDateModified(),
						knowledgeBaseArticle2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("datePublished", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						knowledgeBaseArticle1.getDatePublished(),
						knowledgeBaseArticle2.getDatePublished())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						knowledgeBaseArticle1.getDescription(),
						knowledgeBaseArticle2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("encodingFormat", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						knowledgeBaseArticle1.getEncodingFormat(),
						knowledgeBaseArticle2.getEncodingFormat())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						knowledgeBaseArticle1.getExternalReferenceCode(),
						knowledgeBaseArticle2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("friendlyUrlPath", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						knowledgeBaseArticle1.getFriendlyUrlPath(),
						knowledgeBaseArticle2.getFriendlyUrlPath())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						knowledgeBaseArticle1.getId(),
						knowledgeBaseArticle2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("keywords", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						knowledgeBaseArticle1.getKeywords(),
						knowledgeBaseArticle2.getKeywords())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfAttachments", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						knowledgeBaseArticle1.getNumberOfAttachments(),
						knowledgeBaseArticle2.getNumberOfAttachments())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfKnowledgeBaseArticles",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						knowledgeBaseArticle1.
							getNumberOfKnowledgeBaseArticles(),
						knowledgeBaseArticle2.
							getNumberOfKnowledgeBaseArticles())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"parentKnowledgeBaseArticleId",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						knowledgeBaseArticle1.getParentKnowledgeBaseArticleId(),
						knowledgeBaseArticle2.
							getParentKnowledgeBaseArticleId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"parentKnowledgeBaseFolder", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						knowledgeBaseArticle1.getParentKnowledgeBaseFolder(),
						knowledgeBaseArticle2.getParentKnowledgeBaseFolder())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"parentKnowledgeBaseFolderId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						knowledgeBaseArticle1.getParentKnowledgeBaseFolderId(),
						knowledgeBaseArticle2.
							getParentKnowledgeBaseFolderId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("relatedContents", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						knowledgeBaseArticle1.getRelatedContents(),
						knowledgeBaseArticle2.getRelatedContents())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("subscribed", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						knowledgeBaseArticle1.getSubscribed(),
						knowledgeBaseArticle2.getSubscribed())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryBriefs", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						knowledgeBaseArticle1.getTaxonomyCategoryBriefs(),
						knowledgeBaseArticle2.getTaxonomyCategoryBriefs())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryIds", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						knowledgeBaseArticle1.getTaxonomyCategoryIds(),
						knowledgeBaseArticle2.getTaxonomyCategoryIds())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						knowledgeBaseArticle1.getTitle(),
						knowledgeBaseArticle2.getTitle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						knowledgeBaseArticle1.getViewableBy(),
						knowledgeBaseArticle2.getViewableBy())) {

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

	protected boolean equals(Rating rating1, Rating rating2) {
		if (rating1 == rating2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalRatingAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getActions(), rating2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("bestRating", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getBestRating(), rating2.getBestRating())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getCreator(), rating2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getDateCreated(), rating2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getDateModified(), rating2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(rating1.getId(), rating2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("ratingValue", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getRatingValue(), rating2.getRatingValue())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("worstRating", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getWorstRating(), rating2.getWorstRating())) {

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

		if (!(_knowledgeBaseArticleResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_knowledgeBaseArticleResource;

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
		KnowledgeBaseArticle knowledgeBaseArticle) {

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

		if (entityFieldName.equals("aggregateRating")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("articleBody")) {
			Object object = knowledgeBaseArticle.getArticleBody();

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

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("customFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = knowledgeBaseArticle.getDateCreated();

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

				sb.append(
					_format.format(knowledgeBaseArticle.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = knowledgeBaseArticle.getDateModified();

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

				sb.append(
					_format.format(knowledgeBaseArticle.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("datePublished")) {
			if (operator.equals("between")) {
				Date date = knowledgeBaseArticle.getDatePublished();

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

				sb.append(
					_format.format(knowledgeBaseArticle.getDatePublished()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = knowledgeBaseArticle.getDescription();

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

		if (entityFieldName.equals("encodingFormat")) {
			Object object = knowledgeBaseArticle.getEncodingFormat();

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
			Object object = knowledgeBaseArticle.getExternalReferenceCode();

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

		if (entityFieldName.equals("friendlyUrlPath")) {
			Object object = knowledgeBaseArticle.getFriendlyUrlPath();

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

		if (entityFieldName.equals("keywords")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("numberOfAttachments")) {
			sb.append(
				String.valueOf(knowledgeBaseArticle.getNumberOfAttachments()));

			return sb.toString();
		}

		if (entityFieldName.equals("numberOfKnowledgeBaseArticles")) {
			sb.append(
				String.valueOf(
					knowledgeBaseArticle.getNumberOfKnowledgeBaseArticles()));

			return sb.toString();
		}

		if (entityFieldName.equals("parentKnowledgeBaseArticleId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("parentKnowledgeBaseFolder")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("parentKnowledgeBaseFolderId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("relatedContents")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("siteId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("subscribed")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("taxonomyCategoryBriefs")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("taxonomyCategoryIds")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("title")) {
			Object object = knowledgeBaseArticle.getTitle();

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

		if (entityFieldName.equals("viewableBy")) {
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

	protected KnowledgeBaseArticle randomKnowledgeBaseArticle()
		throws Exception {

		return new KnowledgeBaseArticle() {
			{
				articleBody = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				datePublished = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				encodingFormat = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				friendlyUrlPath = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				numberOfAttachments = RandomTestUtil.randomInt();
				numberOfKnowledgeBaseArticles = RandomTestUtil.randomInt();
				parentKnowledgeBaseArticleId = RandomTestUtil.randomLong();
				parentKnowledgeBaseFolderId = RandomTestUtil.randomLong();
				siteId = testGroup.getGroupId();
				subscribed = RandomTestUtil.randomBoolean();
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected KnowledgeBaseArticle randomIrrelevantKnowledgeBaseArticle()
		throws Exception {

		KnowledgeBaseArticle randomIrrelevantKnowledgeBaseArticle =
			randomKnowledgeBaseArticle();

		randomIrrelevantKnowledgeBaseArticle.setSiteId(
			irrelevantGroup.getGroupId());

		return randomIrrelevantKnowledgeBaseArticle;
	}

	protected KnowledgeBaseArticle randomPatchKnowledgeBaseArticle()
		throws Exception {

		return randomKnowledgeBaseArticle();
	}

	protected Rating randomRating() throws Exception {
		return new Rating() {
			{
				bestRating = RandomTestUtil.randomDouble();
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				id = RandomTestUtil.randomLong();
				ratingValue = RandomTestUtil.randomDouble();
				worstRating = RandomTestUtil.randomDouble();
			}
		};
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

	protected KnowledgeBaseArticleResource knowledgeBaseArticleResource;
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
		LogFactoryUtil.getLog(BaseKnowledgeBaseArticleResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.delivery.resource.v1_0.KnowledgeBaseArticleResource
			_knowledgeBaseArticleResource;

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