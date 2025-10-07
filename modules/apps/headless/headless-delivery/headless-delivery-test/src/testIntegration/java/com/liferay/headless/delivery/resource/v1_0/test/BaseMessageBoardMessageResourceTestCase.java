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
import com.liferay.headless.delivery.client.dto.v1_0.MessageBoardMessage;
import com.liferay.headless.delivery.client.dto.v1_0.Rating;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.permission.Permission;
import com.liferay.headless.delivery.client.resource.v1_0.MessageBoardMessageResource;
import com.liferay.headless.delivery.client.serdes.v1_0.MessageBoardMessageSerDes;
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
public abstract class BaseMessageBoardMessageResourceTestCase {

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

		_messageBoardMessageResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		messageBoardMessageResource = MessageBoardMessageResource.builder(
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

		MessageBoardMessage messageBoardMessage1 = randomMessageBoardMessage();

		String json = objectMapper.writeValueAsString(messageBoardMessage1);

		MessageBoardMessage messageBoardMessage2 =
			MessageBoardMessageSerDes.toDTO(json);

		Assert.assertTrue(equals(messageBoardMessage1, messageBoardMessage2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		MessageBoardMessage messageBoardMessage = randomMessageBoardMessage();

		String json1 = objectMapper.writeValueAsString(messageBoardMessage);
		String json2 = MessageBoardMessageSerDes.toJSON(messageBoardMessage);

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

		MessageBoardMessage messageBoardMessage = randomMessageBoardMessage();

		messageBoardMessage.setArticleBody(regex);
		messageBoardMessage.setEncodingFormat(regex);
		messageBoardMessage.setExternalReferenceCode(regex);
		messageBoardMessage.setFriendlyUrlPath(regex);
		messageBoardMessage.setHeadline(regex);
		messageBoardMessage.setStatus(regex);

		String json = MessageBoardMessageSerDes.toJSON(messageBoardMessage);

		Assert.assertFalse(json.contains(regex));

		messageBoardMessage = MessageBoardMessageSerDes.toDTO(json);

		Assert.assertEquals(regex, messageBoardMessage.getArticleBody());
		Assert.assertEquals(regex, messageBoardMessage.getEncodingFormat());
		Assert.assertEquals(
			regex, messageBoardMessage.getExternalReferenceCode());
		Assert.assertEquals(regex, messageBoardMessage.getFriendlyUrlPath());
		Assert.assertEquals(regex, messageBoardMessage.getHeadline());
		Assert.assertEquals(regex, messageBoardMessage.getStatus());
	}

	@Test
	public void testDeleteMessageBoardMessage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardMessage messageBoardMessage =
			testDeleteMessageBoardMessage_addMessageBoardMessage();

		assertHttpResponseStatusCode(
			204,
			messageBoardMessageResource.deleteMessageBoardMessageHttpResponse(
				messageBoardMessage.getId()));

		assertHttpResponseStatusCode(
			404,
			messageBoardMessageResource.getMessageBoardMessageHttpResponse(
				messageBoardMessage.getId()));
		assertHttpResponseStatusCode(
			404,
			messageBoardMessageResource.getMessageBoardMessageHttpResponse(0L));
	}

	protected MessageBoardMessage
			testDeleteMessageBoardMessage_addMessageBoardMessage()
		throws Exception {

		return testPostMessageBoardMessageMessageBoardMessage_addMessageBoardMessage(
			randomMessageBoardMessage());
	}

	@Test
	public void testGraphQLDeleteMessageBoardMessage() throws Exception {

		// No namespace

		MessageBoardMessage messageBoardMessage1 =
			testGraphQLDeleteMessageBoardMessage_addMessageBoardMessage();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteMessageBoardMessage",
						new HashMap<String, Object>() {
							{
								put(
									"messageBoardMessageId",
									messageBoardMessage1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteMessageBoardMessage"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"messageBoardMessage",
					new HashMap<String, Object>() {
						{
							put(
								"messageBoardMessageId",
								messageBoardMessage1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		MessageBoardMessage messageBoardMessage2 =
			testGraphQLDeleteMessageBoardMessage_addMessageBoardMessage();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteMessageBoardMessage",
							new HashMap<String, Object>() {
								{
									put(
										"messageBoardMessageId",
										messageBoardMessage2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteMessageBoardMessage"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"messageBoardMessage",
						new HashMap<String, Object>() {
							{
								put(
									"messageBoardMessageId",
									messageBoardMessage2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected MessageBoardMessage
			testGraphQLDeleteMessageBoardMessage_addMessageBoardMessage()
		throws Exception {

		return testGraphQLMessageBoardMessage_addMessageBoardMessage();
	}

	@Test
	public void testDeleteMessageBoardMessageBatch() throws Exception {
		MessageBoardMessage messageBoardMessage1 =
			testDeleteMessageBoardMessageBatch_addMessageBoardMessage();

		testDeleteMessageBoardMessageBatch_deleteMessageBoardMessage(
			202, null, messageBoardMessage1.getId());

		assertHttpResponseStatusCode(
			404,
			messageBoardMessageResource.getMessageBoardMessageHttpResponse(
				messageBoardMessage1.getId()));
	}

	protected MessageBoardMessage
			testDeleteMessageBoardMessageBatch_addMessageBoardMessage()
		throws Exception {

		return testDeleteMessageBoardMessage_addMessageBoardMessage();
	}

	protected void testDeleteMessageBoardMessageBatch_deleteMessageBoardMessage(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			messageBoardMessageResource.
				deleteMessageBoardMessageBatchHttpResponse(
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
	public void testDeleteMessageBoardMessageMyRating() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardMessage messageBoardMessage =
			testDeleteMessageBoardMessageMyRating_addMessageBoardMessage();

		assertHttpResponseStatusCode(
			204,
			messageBoardMessageResource.
				deleteMessageBoardMessageMyRatingHttpResponse(
					messageBoardMessage.getId()));

		assertHttpResponseStatusCode(
			404,
			messageBoardMessageResource.
				getMessageBoardMessageMyRatingHttpResponse(
					messageBoardMessage.getId()));
		assertHttpResponseStatusCode(
			404,
			messageBoardMessageResource.
				getMessageBoardMessageMyRatingHttpResponse(0L));
	}

	protected MessageBoardMessage
			testDeleteMessageBoardMessageMyRating_addMessageBoardMessage()
		throws Exception {

		return testPostMessageBoardMessageMessageBoardMessage_addMessageBoardMessage(
			randomMessageBoardMessage());
	}

	@Test
	public void testGraphQLDeleteMessageBoardMessageMyRating()
		throws Exception {

		// No namespace

		MessageBoardMessage messageBoardMessage1 =
			testGraphQLDeleteMessageBoardMessageMyRating_addMessageBoardMessage();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteMessageBoardMessageMyRating",
						new HashMap<String, Object>() {
							{
								put(
									"messageBoardMessageId",
									messageBoardMessage1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteMessageBoardMessageMyRating"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"messageBoardMessageMyRating",
					new HashMap<String, Object>() {
						{
							put(
								"messageBoardMessageId",
								messageBoardMessage1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		MessageBoardMessage messageBoardMessage2 =
			testGraphQLDeleteMessageBoardMessageMyRating_addMessageBoardMessage();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteMessageBoardMessageMyRating",
							new HashMap<String, Object>() {
								{
									put(
										"messageBoardMessageId",
										messageBoardMessage2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteMessageBoardMessageMyRating"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"messageBoardMessageMyRating",
						new HashMap<String, Object>() {
							{
								put(
									"messageBoardMessageId",
									messageBoardMessage2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected MessageBoardMessage
			testGraphQLDeleteMessageBoardMessageMyRating_addMessageBoardMessage()
		throws Exception {

		return testGraphQLMessageBoardMessage_addMessageBoardMessage();
	}

	@Test
	public void testDeleteSiteMessageBoardMessageByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardMessage messageBoardMessage =
			testDeleteSiteMessageBoardMessageByExternalReferenceCode_addMessageBoardMessage();

		assertHttpResponseStatusCode(
			204,
			messageBoardMessageResource.
				deleteSiteMessageBoardMessageByExternalReferenceCodeHttpResponse(
					messageBoardMessage.getSiteId(),
					messageBoardMessage.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			messageBoardMessageResource.
				getSiteMessageBoardMessageByExternalReferenceCodeHttpResponse(
					messageBoardMessage.getSiteId(),
					messageBoardMessage.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			messageBoardMessageResource.
				getSiteMessageBoardMessageByExternalReferenceCodeHttpResponse(
					messageBoardMessage.getSiteId(), "-"));
	}

	protected MessageBoardMessage
			testDeleteSiteMessageBoardMessageByExternalReferenceCode_addMessageBoardMessage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteSiteMessageBoardMessageByExternalReferenceCode()
		throws Exception {

		// No namespace

		MessageBoardMessage messageBoardMessage1 =
			testGraphQLDeleteSiteMessageBoardMessageByExternalReferenceCode_addMessageBoardMessage();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSiteMessageBoardMessageByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + messageBoardMessage1.getSiteId() +
										"\"");
								put(
									"externalReferenceCode",
									"\"" +
										messageBoardMessage1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteSiteMessageBoardMessageByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"messageBoardMessageByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"siteKey",
								"\"" + messageBoardMessage1.getSiteId() + "\"");
							put(
								"externalReferenceCode",
								"\"" +
									messageBoardMessage1.
										getExternalReferenceCode() + "\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		MessageBoardMessage messageBoardMessage2 =
			testGraphQLDeleteSiteMessageBoardMessageByExternalReferenceCode_addMessageBoardMessage();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteSiteMessageBoardMessageByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" +
											messageBoardMessage2.getSiteId() +
												"\"");
									put(
										"externalReferenceCode",
										"\"" +
											messageBoardMessage2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteSiteMessageBoardMessageByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"messageBoardMessageByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + messageBoardMessage2.getSiteId() +
										"\"");
								put(
									"externalReferenceCode",
									"\"" +
										messageBoardMessage2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected MessageBoardMessage
			testGraphQLDeleteSiteMessageBoardMessageByExternalReferenceCode_addMessageBoardMessage()
		throws Exception {

		return testGraphQLSiteMessageBoardMessage_addMessageBoardMessage();
	}

	@Test
	public void testGetMessageBoardMessage() throws Exception {
		MessageBoardMessage postMessageBoardMessage =
			testGetMessageBoardMessage_addMessageBoardMessage();

		MessageBoardMessage getMessageBoardMessage =
			messageBoardMessageResource.getMessageBoardMessage(
				postMessageBoardMessage.getId());

		assertEquals(postMessageBoardMessage, getMessageBoardMessage);
		assertValid(getMessageBoardMessage);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		MessageBoardMessage postMessageBoardMessage =
			testGetMessageBoardMessage_addMessageBoardMessage();

		MessageBoardMessage getMessageBoardMessage =
			messageBoardMessageResource.getMessageBoardMessage(
				postMessageBoardMessage.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.delivery.dto.v1_0.MessageBoardMessage"
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
			postMessageBoardMessage.getId());

		assertEquals(
			getMessageBoardMessage,
			MessageBoardMessageSerDes.toDTO(item.toString()));
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

	protected MessageBoardMessage
			testGetMessageBoardMessage_addMessageBoardMessage()
		throws Exception {

		return testPostMessageBoardMessageMessageBoardMessage_addMessageBoardMessage(
			randomMessageBoardMessage());
	}

	@Test
	public void testGraphQLGetMessageBoardMessage() throws Exception {
		MessageBoardMessage messageBoardMessage =
			testGraphQLGetMessageBoardMessage_addMessageBoardMessage();

		// No namespace

		Assert.assertTrue(
			equals(
				messageBoardMessage,
				MessageBoardMessageSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"messageBoardMessage",
								new HashMap<String, Object>() {
									{
										put(
											"messageBoardMessageId",
											messageBoardMessage.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/messageBoardMessage"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				messageBoardMessage,
				MessageBoardMessageSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"messageBoardMessage",
									new HashMap<String, Object>() {
										{
											put(
												"messageBoardMessageId",
												messageBoardMessage.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/messageBoardMessage"))));
	}

	@Test
	public void testGraphQLGetMessageBoardMessageNotFound() throws Exception {
		Long irrelevantMessageBoardMessageId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"messageBoardMessage",
						new HashMap<String, Object>() {
							{
								put(
									"messageBoardMessageId",
									irrelevantMessageBoardMessageId);
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
							"messageBoardMessage",
							new HashMap<String, Object>() {
								{
									put(
										"messageBoardMessageId",
										irrelevantMessageBoardMessageId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected MessageBoardMessage
			testGraphQLGetMessageBoardMessage_addMessageBoardMessage()
		throws Exception {

		return testGraphQLMessageBoardMessage_addMessageBoardMessage();
	}

	@Test
	public void testGetMessageBoardMessageMessageBoardMessagesPage()
		throws Exception {

		Long parentMessageBoardMessageId =
			testGetMessageBoardMessageMessageBoardMessagesPage_getParentMessageBoardMessageId();
		Long irrelevantParentMessageBoardMessageId =
			testGetMessageBoardMessageMessageBoardMessagesPage_getIrrelevantParentMessageBoardMessageId();

		Page<MessageBoardMessage> page =
			messageBoardMessageResource.
				getMessageBoardMessageMessageBoardMessagesPage(
					parentMessageBoardMessageId, null, null, null, null,
					Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantParentMessageBoardMessageId != null) {
			MessageBoardMessage irrelevantMessageBoardMessage =
				testGetMessageBoardMessageMessageBoardMessagesPage_addMessageBoardMessage(
					irrelevantParentMessageBoardMessageId,
					randomIrrelevantMessageBoardMessage());

			page =
				messageBoardMessageResource.
					getMessageBoardMessageMessageBoardMessagesPage(
						irrelevantParentMessageBoardMessageId, null, null, null,
						null, Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantMessageBoardMessage,
				(List<MessageBoardMessage>)page.getItems());
			assertValid(
				page,
				testGetMessageBoardMessageMessageBoardMessagesPage_getExpectedActions(
					irrelevantParentMessageBoardMessageId));
		}

		MessageBoardMessage messageBoardMessage1 =
			testGetMessageBoardMessageMessageBoardMessagesPage_addMessageBoardMessage(
				parentMessageBoardMessageId, randomMessageBoardMessage());

		MessageBoardMessage messageBoardMessage2 =
			testGetMessageBoardMessageMessageBoardMessagesPage_addMessageBoardMessage(
				parentMessageBoardMessageId, randomMessageBoardMessage());

		page =
			messageBoardMessageResource.
				getMessageBoardMessageMessageBoardMessagesPage(
					parentMessageBoardMessageId, null, null, null, null,
					Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			messageBoardMessage1, (List<MessageBoardMessage>)page.getItems());
		assertContains(
			messageBoardMessage2, (List<MessageBoardMessage>)page.getItems());
		assertValid(
			page,
			testGetMessageBoardMessageMessageBoardMessagesPage_getExpectedActions(
				parentMessageBoardMessageId));

		messageBoardMessageResource.deleteMessageBoardMessage(
			messageBoardMessage1.getId());

		messageBoardMessageResource.deleteMessageBoardMessage(
			messageBoardMessage2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetMessageBoardMessageMessageBoardMessagesPage_getExpectedActions(
				Long parentMessageBoardMessageId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetMessageBoardMessageMessageBoardMessagesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long parentMessageBoardMessageId =
			testGetMessageBoardMessageMessageBoardMessagesPage_getParentMessageBoardMessageId();

		MessageBoardMessage messageBoardMessage1 = randomMessageBoardMessage();

		messageBoardMessage1 =
			testGetMessageBoardMessageMessageBoardMessagesPage_addMessageBoardMessage(
				parentMessageBoardMessageId, messageBoardMessage1);

		for (EntityField entityField : entityFields) {
			Page<MessageBoardMessage> page =
				messageBoardMessageResource.
					getMessageBoardMessageMessageBoardMessagesPage(
						parentMessageBoardMessageId, null, null, null,
						getFilterString(
							entityField, "between", messageBoardMessage1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(messageBoardMessage1),
				(List<MessageBoardMessage>)page.getItems());
		}
	}

	@Test
	public void testGetMessageBoardMessageMessageBoardMessagesPageWithFilterDoubleEquals()
		throws Exception {

		testGetMessageBoardMessageMessageBoardMessagesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetMessageBoardMessageMessageBoardMessagesPageWithFilterStringContains()
		throws Exception {

		testGetMessageBoardMessageMessageBoardMessagesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetMessageBoardMessageMessageBoardMessagesPageWithFilterStringEquals()
		throws Exception {

		testGetMessageBoardMessageMessageBoardMessagesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetMessageBoardMessageMessageBoardMessagesPageWithFilterStringStartsWith()
		throws Exception {

		testGetMessageBoardMessageMessageBoardMessagesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetMessageBoardMessageMessageBoardMessagesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long parentMessageBoardMessageId =
			testGetMessageBoardMessageMessageBoardMessagesPage_getParentMessageBoardMessageId();

		MessageBoardMessage messageBoardMessage1 =
			testGetMessageBoardMessageMessageBoardMessagesPage_addMessageBoardMessage(
				parentMessageBoardMessageId, randomMessageBoardMessage());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardMessage messageBoardMessage2 =
			testGetMessageBoardMessageMessageBoardMessagesPage_addMessageBoardMessage(
				parentMessageBoardMessageId, randomMessageBoardMessage());

		for (EntityField entityField : entityFields) {
			Page<MessageBoardMessage> page =
				messageBoardMessageResource.
					getMessageBoardMessageMessageBoardMessagesPage(
						parentMessageBoardMessageId, null, null, null,
						getFilterString(
							entityField, operator, messageBoardMessage1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(messageBoardMessage1),
				(List<MessageBoardMessage>)page.getItems());
		}
	}

	@Test
	public void testGetMessageBoardMessageMessageBoardMessagesPageWithPagination()
		throws Exception {

		Long parentMessageBoardMessageId =
			testGetMessageBoardMessageMessageBoardMessagesPage_getParentMessageBoardMessageId();

		Page<MessageBoardMessage> messageBoardMessagesPage =
			messageBoardMessageResource.
				getMessageBoardMessageMessageBoardMessagesPage(
					parentMessageBoardMessageId, null, null, null, null, null,
					null);

		int totalCount = GetterUtil.getInteger(
			messageBoardMessagesPage.getTotalCount());

		MessageBoardMessage messageBoardMessage1 =
			testGetMessageBoardMessageMessageBoardMessagesPage_addMessageBoardMessage(
				parentMessageBoardMessageId, randomMessageBoardMessage());

		MessageBoardMessage messageBoardMessage2 =
			testGetMessageBoardMessageMessageBoardMessagesPage_addMessageBoardMessage(
				parentMessageBoardMessageId, randomMessageBoardMessage());

		MessageBoardMessage messageBoardMessage3 =
			testGetMessageBoardMessageMessageBoardMessagesPage_addMessageBoardMessage(
				parentMessageBoardMessageId, randomMessageBoardMessage());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<MessageBoardMessage> page1 =
				messageBoardMessageResource.
					getMessageBoardMessageMessageBoardMessagesPage(
						parentMessageBoardMessageId, null, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				messageBoardMessage1,
				(List<MessageBoardMessage>)page1.getItems());

			Page<MessageBoardMessage> page2 =
				messageBoardMessageResource.
					getMessageBoardMessageMessageBoardMessagesPage(
						parentMessageBoardMessageId, null, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				messageBoardMessage2,
				(List<MessageBoardMessage>)page2.getItems());

			Page<MessageBoardMessage> page3 =
				messageBoardMessageResource.
					getMessageBoardMessageMessageBoardMessagesPage(
						parentMessageBoardMessageId, null, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				messageBoardMessage3,
				(List<MessageBoardMessage>)page3.getItems());
		}
		else {
			Page<MessageBoardMessage> page1 =
				messageBoardMessageResource.
					getMessageBoardMessageMessageBoardMessagesPage(
						parentMessageBoardMessageId, null, null, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<MessageBoardMessage> messageBoardMessages1 =
				(List<MessageBoardMessage>)page1.getItems();

			Assert.assertEquals(
				messageBoardMessages1.toString(), totalCount + 2,
				messageBoardMessages1.size());

			Page<MessageBoardMessage> page2 =
				messageBoardMessageResource.
					getMessageBoardMessageMessageBoardMessagesPage(
						parentMessageBoardMessageId, null, null, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<MessageBoardMessage> messageBoardMessages2 =
				(List<MessageBoardMessage>)page2.getItems();

			Assert.assertEquals(
				messageBoardMessages2.toString(), 1,
				messageBoardMessages2.size());

			Page<MessageBoardMessage> page3 =
				messageBoardMessageResource.
					getMessageBoardMessageMessageBoardMessagesPage(
						parentMessageBoardMessageId, null, null, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				messageBoardMessage1,
				(List<MessageBoardMessage>)page3.getItems());
			assertContains(
				messageBoardMessage2,
				(List<MessageBoardMessage>)page3.getItems());
			assertContains(
				messageBoardMessage3,
				(List<MessageBoardMessage>)page3.getItems());
		}
	}

	@Test
	public void testGetMessageBoardMessageMessageBoardMessagesPageWithSortDateTime()
		throws Exception {

		testGetMessageBoardMessageMessageBoardMessagesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, messageBoardMessage1, messageBoardMessage2) -> {
				BeanTestUtil.setProperty(
					messageBoardMessage1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetMessageBoardMessageMessageBoardMessagesPageWithSortDouble()
		throws Exception {

		testGetMessageBoardMessageMessageBoardMessagesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, messageBoardMessage1, messageBoardMessage2) -> {
				BeanTestUtil.setProperty(
					messageBoardMessage1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					messageBoardMessage2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetMessageBoardMessageMessageBoardMessagesPageWithSortInteger()
		throws Exception {

		testGetMessageBoardMessageMessageBoardMessagesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, messageBoardMessage1, messageBoardMessage2) -> {
				BeanTestUtil.setProperty(
					messageBoardMessage1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					messageBoardMessage2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetMessageBoardMessageMessageBoardMessagesPageWithSortString()
		throws Exception {

		testGetMessageBoardMessageMessageBoardMessagesPageWithSort(
			EntityField.Type.STRING,
			(entityField, messageBoardMessage1, messageBoardMessage2) -> {
				Class<?> clazz = messageBoardMessage1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						messageBoardMessage1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						messageBoardMessage2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						messageBoardMessage1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						messageBoardMessage2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						messageBoardMessage1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						messageBoardMessage2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetMessageBoardMessageMessageBoardMessagesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, MessageBoardMessage, MessageBoardMessage,
				 Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long parentMessageBoardMessageId =
			testGetMessageBoardMessageMessageBoardMessagesPage_getParentMessageBoardMessageId();

		MessageBoardMessage messageBoardMessage1 = randomMessageBoardMessage();
		MessageBoardMessage messageBoardMessage2 = randomMessageBoardMessage();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, messageBoardMessage1, messageBoardMessage2);
		}

		messageBoardMessage1 =
			testGetMessageBoardMessageMessageBoardMessagesPage_addMessageBoardMessage(
				parentMessageBoardMessageId, messageBoardMessage1);

		messageBoardMessage2 =
			testGetMessageBoardMessageMessageBoardMessagesPage_addMessageBoardMessage(
				parentMessageBoardMessageId, messageBoardMessage2);

		Page<MessageBoardMessage> page =
			messageBoardMessageResource.
				getMessageBoardMessageMessageBoardMessagesPage(
					parentMessageBoardMessageId, null, null, null, null, null,
					null);

		for (EntityField entityField : entityFields) {
			Page<MessageBoardMessage> ascPage =
				messageBoardMessageResource.
					getMessageBoardMessageMessageBoardMessagesPage(
						parentMessageBoardMessageId, null, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				messageBoardMessage1,
				(List<MessageBoardMessage>)ascPage.getItems());
			assertContains(
				messageBoardMessage2,
				(List<MessageBoardMessage>)ascPage.getItems());

			Page<MessageBoardMessage> descPage =
				messageBoardMessageResource.
					getMessageBoardMessageMessageBoardMessagesPage(
						parentMessageBoardMessageId, null, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				messageBoardMessage2,
				(List<MessageBoardMessage>)descPage.getItems());
			assertContains(
				messageBoardMessage1,
				(List<MessageBoardMessage>)descPage.getItems());
		}
	}

	protected MessageBoardMessage
			testGetMessageBoardMessageMessageBoardMessagesPage_addMessageBoardMessage(
				Long parentMessageBoardMessageId,
				MessageBoardMessage messageBoardMessage)
		throws Exception {

		return messageBoardMessageResource.
			postMessageBoardMessageMessageBoardMessage(
				parentMessageBoardMessageId, messageBoardMessage);
	}

	protected Long
			testGetMessageBoardMessageMessageBoardMessagesPage_getParentMessageBoardMessageId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetMessageBoardMessageMessageBoardMessagesPage_getIrrelevantParentMessageBoardMessageId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetMessageBoardMessagePermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardMessage postMessageBoardMessage =
			testGetMessageBoardMessagePermissionsPage_addMessageBoardMessage();

		Page<Permission> page =
			messageBoardMessageResource.getMessageBoardMessagePermissionsPage(
				postMessageBoardMessage.getId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected MessageBoardMessage
			testGetMessageBoardMessagePermissionsPage_addMessageBoardMessage()
		throws Exception {

		return testPostMessageBoardMessageMessageBoardMessage_addMessageBoardMessage(
			randomMessageBoardMessage());
	}

	@Test
	public void testGetMessageBoardThreadMessageBoardMessagesPage()
		throws Exception {

		Long messageBoardThreadId =
			testGetMessageBoardThreadMessageBoardMessagesPage_getMessageBoardThreadId();
		Long irrelevantMessageBoardThreadId =
			testGetMessageBoardThreadMessageBoardMessagesPage_getIrrelevantMessageBoardThreadId();

		Page<MessageBoardMessage> page =
			messageBoardMessageResource.
				getMessageBoardThreadMessageBoardMessagesPage(
					messageBoardThreadId, null, null, null,
					Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantMessageBoardThreadId != null) {
			MessageBoardMessage irrelevantMessageBoardMessage =
				testGetMessageBoardThreadMessageBoardMessagesPage_addMessageBoardMessage(
					irrelevantMessageBoardThreadId,
					randomIrrelevantMessageBoardMessage());

			page =
				messageBoardMessageResource.
					getMessageBoardThreadMessageBoardMessagesPage(
						irrelevantMessageBoardThreadId, null, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantMessageBoardMessage,
				(List<MessageBoardMessage>)page.getItems());
			assertValid(
				page,
				testGetMessageBoardThreadMessageBoardMessagesPage_getExpectedActions(
					irrelevantMessageBoardThreadId));
		}

		MessageBoardMessage messageBoardMessage1 =
			testGetMessageBoardThreadMessageBoardMessagesPage_addMessageBoardMessage(
				messageBoardThreadId, randomMessageBoardMessage());

		MessageBoardMessage messageBoardMessage2 =
			testGetMessageBoardThreadMessageBoardMessagesPage_addMessageBoardMessage(
				messageBoardThreadId, randomMessageBoardMessage());

		page =
			messageBoardMessageResource.
				getMessageBoardThreadMessageBoardMessagesPage(
					messageBoardThreadId, null, null, null,
					Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			messageBoardMessage1, (List<MessageBoardMessage>)page.getItems());
		assertContains(
			messageBoardMessage2, (List<MessageBoardMessage>)page.getItems());
		assertValid(
			page,
			testGetMessageBoardThreadMessageBoardMessagesPage_getExpectedActions(
				messageBoardThreadId));

		messageBoardMessageResource.deleteMessageBoardMessage(
			messageBoardMessage1.getId());

		messageBoardMessageResource.deleteMessageBoardMessage(
			messageBoardMessage2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetMessageBoardThreadMessageBoardMessagesPage_getExpectedActions(
				Long messageBoardThreadId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/message-board-threads/{messageBoardThreadId}/message-board-messages/batch".
				replace(
					"{messageBoardThreadId}",
					String.valueOf(messageBoardThreadId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetMessageBoardThreadMessageBoardMessagesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long messageBoardThreadId =
			testGetMessageBoardThreadMessageBoardMessagesPage_getMessageBoardThreadId();

		MessageBoardMessage messageBoardMessage1 = randomMessageBoardMessage();

		messageBoardMessage1 =
			testGetMessageBoardThreadMessageBoardMessagesPage_addMessageBoardMessage(
				messageBoardThreadId, messageBoardMessage1);

		for (EntityField entityField : entityFields) {
			Page<MessageBoardMessage> page =
				messageBoardMessageResource.
					getMessageBoardThreadMessageBoardMessagesPage(
						messageBoardThreadId, null, null,
						getFilterString(
							entityField, "between", messageBoardMessage1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(messageBoardMessage1),
				(List<MessageBoardMessage>)page.getItems());
		}
	}

	@Test
	public void testGetMessageBoardThreadMessageBoardMessagesPageWithFilterDoubleEquals()
		throws Exception {

		testGetMessageBoardThreadMessageBoardMessagesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetMessageBoardThreadMessageBoardMessagesPageWithFilterStringContains()
		throws Exception {

		testGetMessageBoardThreadMessageBoardMessagesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetMessageBoardThreadMessageBoardMessagesPageWithFilterStringEquals()
		throws Exception {

		testGetMessageBoardThreadMessageBoardMessagesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetMessageBoardThreadMessageBoardMessagesPageWithFilterStringStartsWith()
		throws Exception {

		testGetMessageBoardThreadMessageBoardMessagesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetMessageBoardThreadMessageBoardMessagesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long messageBoardThreadId =
			testGetMessageBoardThreadMessageBoardMessagesPage_getMessageBoardThreadId();

		MessageBoardMessage messageBoardMessage1 =
			testGetMessageBoardThreadMessageBoardMessagesPage_addMessageBoardMessage(
				messageBoardThreadId, randomMessageBoardMessage());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardMessage messageBoardMessage2 =
			testGetMessageBoardThreadMessageBoardMessagesPage_addMessageBoardMessage(
				messageBoardThreadId, randomMessageBoardMessage());

		for (EntityField entityField : entityFields) {
			Page<MessageBoardMessage> page =
				messageBoardMessageResource.
					getMessageBoardThreadMessageBoardMessagesPage(
						messageBoardThreadId, null, null,
						getFilterString(
							entityField, operator, messageBoardMessage1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(messageBoardMessage1),
				(List<MessageBoardMessage>)page.getItems());
		}
	}

	@Test
	public void testGetMessageBoardThreadMessageBoardMessagesPageWithPagination()
		throws Exception {

		Long messageBoardThreadId =
			testGetMessageBoardThreadMessageBoardMessagesPage_getMessageBoardThreadId();

		Page<MessageBoardMessage> messageBoardMessagesPage =
			messageBoardMessageResource.
				getMessageBoardThreadMessageBoardMessagesPage(
					messageBoardThreadId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			messageBoardMessagesPage.getTotalCount());

		MessageBoardMessage messageBoardMessage1 =
			testGetMessageBoardThreadMessageBoardMessagesPage_addMessageBoardMessage(
				messageBoardThreadId, randomMessageBoardMessage());

		MessageBoardMessage messageBoardMessage2 =
			testGetMessageBoardThreadMessageBoardMessagesPage_addMessageBoardMessage(
				messageBoardThreadId, randomMessageBoardMessage());

		MessageBoardMessage messageBoardMessage3 =
			testGetMessageBoardThreadMessageBoardMessagesPage_addMessageBoardMessage(
				messageBoardThreadId, randomMessageBoardMessage());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<MessageBoardMessage> page1 =
				messageBoardMessageResource.
					getMessageBoardThreadMessageBoardMessagesPage(
						messageBoardThreadId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				messageBoardMessage1,
				(List<MessageBoardMessage>)page1.getItems());

			Page<MessageBoardMessage> page2 =
				messageBoardMessageResource.
					getMessageBoardThreadMessageBoardMessagesPage(
						messageBoardThreadId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				messageBoardMessage2,
				(List<MessageBoardMessage>)page2.getItems());

			Page<MessageBoardMessage> page3 =
				messageBoardMessageResource.
					getMessageBoardThreadMessageBoardMessagesPage(
						messageBoardThreadId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				messageBoardMessage3,
				(List<MessageBoardMessage>)page3.getItems());
		}
		else {
			Page<MessageBoardMessage> page1 =
				messageBoardMessageResource.
					getMessageBoardThreadMessageBoardMessagesPage(
						messageBoardThreadId, null, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<MessageBoardMessage> messageBoardMessages1 =
				(List<MessageBoardMessage>)page1.getItems();

			Assert.assertEquals(
				messageBoardMessages1.toString(), totalCount + 2,
				messageBoardMessages1.size());

			Page<MessageBoardMessage> page2 =
				messageBoardMessageResource.
					getMessageBoardThreadMessageBoardMessagesPage(
						messageBoardThreadId, null, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<MessageBoardMessage> messageBoardMessages2 =
				(List<MessageBoardMessage>)page2.getItems();

			Assert.assertEquals(
				messageBoardMessages2.toString(), 1,
				messageBoardMessages2.size());

			Page<MessageBoardMessage> page3 =
				messageBoardMessageResource.
					getMessageBoardThreadMessageBoardMessagesPage(
						messageBoardThreadId, null, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				messageBoardMessage1,
				(List<MessageBoardMessage>)page3.getItems());
			assertContains(
				messageBoardMessage2,
				(List<MessageBoardMessage>)page3.getItems());
			assertContains(
				messageBoardMessage3,
				(List<MessageBoardMessage>)page3.getItems());
		}
	}

	@Test
	public void testGetMessageBoardThreadMessageBoardMessagesPageWithSortDateTime()
		throws Exception {

		testGetMessageBoardThreadMessageBoardMessagesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, messageBoardMessage1, messageBoardMessage2) -> {
				BeanTestUtil.setProperty(
					messageBoardMessage1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetMessageBoardThreadMessageBoardMessagesPageWithSortDouble()
		throws Exception {

		testGetMessageBoardThreadMessageBoardMessagesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, messageBoardMessage1, messageBoardMessage2) -> {
				BeanTestUtil.setProperty(
					messageBoardMessage1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					messageBoardMessage2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetMessageBoardThreadMessageBoardMessagesPageWithSortInteger()
		throws Exception {

		testGetMessageBoardThreadMessageBoardMessagesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, messageBoardMessage1, messageBoardMessage2) -> {
				BeanTestUtil.setProperty(
					messageBoardMessage1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					messageBoardMessage2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetMessageBoardThreadMessageBoardMessagesPageWithSortString()
		throws Exception {

		testGetMessageBoardThreadMessageBoardMessagesPageWithSort(
			EntityField.Type.STRING,
			(entityField, messageBoardMessage1, messageBoardMessage2) -> {
				Class<?> clazz = messageBoardMessage1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						messageBoardMessage1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						messageBoardMessage2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						messageBoardMessage1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						messageBoardMessage2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						messageBoardMessage1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						messageBoardMessage2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetMessageBoardThreadMessageBoardMessagesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, MessageBoardMessage, MessageBoardMessage,
				 Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long messageBoardThreadId =
			testGetMessageBoardThreadMessageBoardMessagesPage_getMessageBoardThreadId();

		MessageBoardMessage messageBoardMessage1 = randomMessageBoardMessage();
		MessageBoardMessage messageBoardMessage2 = randomMessageBoardMessage();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, messageBoardMessage1, messageBoardMessage2);
		}

		messageBoardMessage1 =
			testGetMessageBoardThreadMessageBoardMessagesPage_addMessageBoardMessage(
				messageBoardThreadId, messageBoardMessage1);

		messageBoardMessage2 =
			testGetMessageBoardThreadMessageBoardMessagesPage_addMessageBoardMessage(
				messageBoardThreadId, messageBoardMessage2);

		Page<MessageBoardMessage> page =
			messageBoardMessageResource.
				getMessageBoardThreadMessageBoardMessagesPage(
					messageBoardThreadId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<MessageBoardMessage> ascPage =
				messageBoardMessageResource.
					getMessageBoardThreadMessageBoardMessagesPage(
						messageBoardThreadId, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				messageBoardMessage1,
				(List<MessageBoardMessage>)ascPage.getItems());
			assertContains(
				messageBoardMessage2,
				(List<MessageBoardMessage>)ascPage.getItems());

			Page<MessageBoardMessage> descPage =
				messageBoardMessageResource.
					getMessageBoardThreadMessageBoardMessagesPage(
						messageBoardThreadId, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				messageBoardMessage2,
				(List<MessageBoardMessage>)descPage.getItems());
			assertContains(
				messageBoardMessage1,
				(List<MessageBoardMessage>)descPage.getItems());
		}
	}

	protected MessageBoardMessage
			testGetMessageBoardThreadMessageBoardMessagesPage_addMessageBoardMessage(
				Long messageBoardThreadId,
				MessageBoardMessage messageBoardMessage)
		throws Exception {

		return messageBoardMessageResource.
			postMessageBoardThreadMessageBoardMessage(
				messageBoardThreadId, messageBoardMessage);
	}

	protected Long
			testGetMessageBoardThreadMessageBoardMessagesPage_getMessageBoardThreadId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetMessageBoardThreadMessageBoardMessagesPage_getIrrelevantMessageBoardThreadId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetMessageBoardThreadMessageBoardMessagesPage()
		throws Exception {

		Long messageBoardThreadId =
			testGetMessageBoardThreadMessageBoardMessagesPage_getMessageBoardThreadId();

		GraphQLField graphQLField = new GraphQLField(
			"messageBoardThreadMessageBoardMessages",
			new HashMap<String, Object>() {
				{
					put("messageBoardThreadId", messageBoardThreadId);
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject messageBoardThreadMessageBoardMessagesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/messageBoardThreadMessageBoardMessages");

		long totalCount =
			messageBoardThreadMessageBoardMessagesJSONObject.getLong(
				"totalCount");

		MessageBoardMessage messageBoardMessage1 =
			testGraphQLMessageBoardThreadMessageBoardMessage_addMessageBoardMessage(
				messageBoardThreadId, randomMessageBoardMessage());

		MessageBoardMessage messageBoardMessage2 =
			testGraphQLMessageBoardThreadMessageBoardMessage_addMessageBoardMessage(
				messageBoardThreadId, randomMessageBoardMessage());

		messageBoardThreadMessageBoardMessagesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/messageBoardThreadMessageBoardMessages");

		Assert.assertEquals(
			totalCount + 2,
			messageBoardThreadMessageBoardMessagesJSONObject.getLong(
				"totalCount"));

		assertContains(
			messageBoardMessage1,
			Arrays.asList(
				MessageBoardMessageSerDes.toDTOs(
					messageBoardThreadMessageBoardMessagesJSONObject.getString(
						"items"))));
		assertContains(
			messageBoardMessage2,
			Arrays.asList(
				MessageBoardMessageSerDes.toDTOs(
					messageBoardThreadMessageBoardMessagesJSONObject.getString(
						"items"))));

		// Using the namespace headlessDelivery_v1_0

		messageBoardThreadMessageBoardMessagesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField("headlessDelivery_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"JSONObject/messageBoardThreadMessageBoardMessages");

		Assert.assertEquals(
			totalCount + 2,
			messageBoardThreadMessageBoardMessagesJSONObject.getLong(
				"totalCount"));

		assertContains(
			messageBoardMessage1,
			Arrays.asList(
				MessageBoardMessageSerDes.toDTOs(
					messageBoardThreadMessageBoardMessagesJSONObject.getString(
						"items"))));
		assertContains(
			messageBoardMessage2,
			Arrays.asList(
				MessageBoardMessageSerDes.toDTOs(
					messageBoardThreadMessageBoardMessagesJSONObject.getString(
						"items"))));
	}

	@Test
	public void testGetSiteMessageBoardMessageByExternalReferenceCode()
		throws Exception {

		MessageBoardMessage postMessageBoardMessage =
			testGetSiteMessageBoardMessageByExternalReferenceCode_addMessageBoardMessage();

		MessageBoardMessage getMessageBoardMessage =
			messageBoardMessageResource.
				getSiteMessageBoardMessageByExternalReferenceCode(
					postMessageBoardMessage.getSiteId(),
					postMessageBoardMessage.getExternalReferenceCode());

		assertEquals(postMessageBoardMessage, getMessageBoardMessage);
		assertValid(getMessageBoardMessage);
	}

	protected MessageBoardMessage
			testGetSiteMessageBoardMessageByExternalReferenceCode_addMessageBoardMessage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteMessageBoardMessageByExternalReferenceCode()
		throws Exception {

		MessageBoardMessage messageBoardMessage =
			testGraphQLGetSiteMessageBoardMessageByExternalReferenceCode_addMessageBoardMessage();

		// No namespace

		Assert.assertTrue(
			equals(
				messageBoardMessage,
				MessageBoardMessageSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"messageBoardMessageByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" +
												messageBoardMessage.
													getSiteId() + "\"");
										put(
											"externalReferenceCode",
											"\"" +
												messageBoardMessage.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/messageBoardMessageByExternalReferenceCode"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				messageBoardMessage,
				MessageBoardMessageSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"messageBoardMessageByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													messageBoardMessage.
														getSiteId() + "\"");
											put(
												"externalReferenceCode",
												"\"" +
													messageBoardMessage.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/messageBoardMessageByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetSiteMessageBoardMessageByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"messageBoardMessageByExternalReferenceCode",
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
							"messageBoardMessageByExternalReferenceCode",
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

	protected MessageBoardMessage
			testGraphQLGetSiteMessageBoardMessageByExternalReferenceCode_addMessageBoardMessage()
		throws Exception {

		return testGraphQLSiteMessageBoardMessage_addMessageBoardMessage();
	}

	@Test
	public void testGetSiteMessageBoardMessageByFriendlyUrlPath()
		throws Exception {

		MessageBoardMessage postMessageBoardMessage =
			testGetSiteMessageBoardMessageByFriendlyUrlPath_addMessageBoardMessage();

		MessageBoardMessage getMessageBoardMessage =
			messageBoardMessageResource.
				getSiteMessageBoardMessageByFriendlyUrlPath(
					postMessageBoardMessage.getSiteId(),
					postMessageBoardMessage.getFriendlyUrlPath());

		assertEquals(postMessageBoardMessage, getMessageBoardMessage);
		assertValid(getMessageBoardMessage);
	}

	protected MessageBoardMessage
			testGetSiteMessageBoardMessageByFriendlyUrlPath_addMessageBoardMessage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteMessageBoardMessageByFriendlyUrlPath()
		throws Exception {

		MessageBoardMessage messageBoardMessage =
			testGraphQLGetSiteMessageBoardMessageByFriendlyUrlPath_addMessageBoardMessage();

		// No namespace

		Assert.assertTrue(
			equals(
				messageBoardMessage,
				MessageBoardMessageSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"messageBoardMessageByFriendlyUrlPath",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" +
												messageBoardMessage.
													getSiteId() + "\"");
										put(
											"friendlyUrlPath",
											"\"" +
												messageBoardMessage.
													getFriendlyUrlPath() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/messageBoardMessageByFriendlyUrlPath"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				messageBoardMessage,
				MessageBoardMessageSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"messageBoardMessageByFriendlyUrlPath",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													messageBoardMessage.
														getSiteId() + "\"");
											put(
												"friendlyUrlPath",
												"\"" +
													messageBoardMessage.
														getFriendlyUrlPath() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/messageBoardMessageByFriendlyUrlPath"))));
	}

	@Test
	public void testGraphQLGetSiteMessageBoardMessageByFriendlyUrlPathNotFound()
		throws Exception {

		String irrelevantFriendlyUrlPath =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"messageBoardMessageByFriendlyUrlPath",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + irrelevantGroup.getGroupId() + "\"");
								put(
									"friendlyUrlPath",
									irrelevantFriendlyUrlPath);
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
							"messageBoardMessageByFriendlyUrlPath",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + irrelevantGroup.getGroupId() +
											"\"");
									put(
										"friendlyUrlPath",
										irrelevantFriendlyUrlPath);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected MessageBoardMessage
			testGraphQLGetSiteMessageBoardMessageByFriendlyUrlPath_addMessageBoardMessage()
		throws Exception {

		return testGraphQLSiteMessageBoardMessage_addMessageBoardMessage();
	}

	@Test
	public void testGetSiteMessageBoardMessagePermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardMessage postMessageBoardMessage =
			testGetSiteMessageBoardMessagePermissionsPage_addMessageBoardMessage();

		Page<Permission> page =
			messageBoardMessageResource.
				getSiteMessageBoardMessagePermissionsPage(
					testGroup.getGroupId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected MessageBoardMessage
			testGetSiteMessageBoardMessagePermissionsPage_addMessageBoardMessage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetSiteMessageBoardMessagesPage() throws Exception {
		Long siteId = testGetSiteMessageBoardMessagesPage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteMessageBoardMessagesPage_getIrrelevantSiteId();

		Page<MessageBoardMessage> page =
			messageBoardMessageResource.getSiteMessageBoardMessagesPage(
				siteId, null, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteId != null) {
			MessageBoardMessage irrelevantMessageBoardMessage =
				testGetSiteMessageBoardMessagesPage_addMessageBoardMessage(
					irrelevantSiteId, randomIrrelevantMessageBoardMessage());

			page = messageBoardMessageResource.getSiteMessageBoardMessagesPage(
				irrelevantSiteId, null, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantMessageBoardMessage,
				(List<MessageBoardMessage>)page.getItems());
			assertValid(
				page,
				testGetSiteMessageBoardMessagesPage_getExpectedActions(
					irrelevantSiteId));
		}

		MessageBoardMessage messageBoardMessage1 =
			testGetSiteMessageBoardMessagesPage_addMessageBoardMessage(
				siteId, randomMessageBoardMessage());

		MessageBoardMessage messageBoardMessage2 =
			testGetSiteMessageBoardMessagesPage_addMessageBoardMessage(
				siteId, randomMessageBoardMessage());

		page = messageBoardMessageResource.getSiteMessageBoardMessagesPage(
			siteId, null, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			messageBoardMessage1, (List<MessageBoardMessage>)page.getItems());
		assertContains(
			messageBoardMessage2, (List<MessageBoardMessage>)page.getItems());
		assertValid(
			page,
			testGetSiteMessageBoardMessagesPage_getExpectedActions(siteId));

		messageBoardMessageResource.deleteMessageBoardMessage(
			messageBoardMessage1.getId());

		messageBoardMessageResource.deleteMessageBoardMessage(
			messageBoardMessage2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSiteMessageBoardMessagesPage_getExpectedActions(Long siteId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSiteMessageBoardMessagesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteMessageBoardMessagesPage_getSiteId();

		MessageBoardMessage messageBoardMessage1 = randomMessageBoardMessage();

		messageBoardMessage1 =
			testGetSiteMessageBoardMessagesPage_addMessageBoardMessage(
				siteId, messageBoardMessage1);

		for (EntityField entityField : entityFields) {
			Page<MessageBoardMessage> page =
				messageBoardMessageResource.getSiteMessageBoardMessagesPage(
					siteId, null, null, null,
					getFilterString(
						entityField, "between", messageBoardMessage1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(messageBoardMessage1),
				(List<MessageBoardMessage>)page.getItems());
		}
	}

	@Test
	public void testGetSiteMessageBoardMessagesPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteMessageBoardMessagesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteMessageBoardMessagesPageWithFilterStringContains()
		throws Exception {

		testGetSiteMessageBoardMessagesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteMessageBoardMessagesPageWithFilterStringEquals()
		throws Exception {

		testGetSiteMessageBoardMessagesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteMessageBoardMessagesPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteMessageBoardMessagesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetSiteMessageBoardMessagesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteMessageBoardMessagesPage_getSiteId();

		MessageBoardMessage messageBoardMessage1 =
			testGetSiteMessageBoardMessagesPage_addMessageBoardMessage(
				siteId, randomMessageBoardMessage());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardMessage messageBoardMessage2 =
			testGetSiteMessageBoardMessagesPage_addMessageBoardMessage(
				siteId, randomMessageBoardMessage());

		for (EntityField entityField : entityFields) {
			Page<MessageBoardMessage> page =
				messageBoardMessageResource.getSiteMessageBoardMessagesPage(
					siteId, null, null, null,
					getFilterString(
						entityField, operator, messageBoardMessage1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(messageBoardMessage1),
				(List<MessageBoardMessage>)page.getItems());
		}
	}

	@Test
	public void testGetSiteMessageBoardMessagesPageWithPagination()
		throws Exception {

		Long siteId = testGetSiteMessageBoardMessagesPage_getSiteId();

		Page<MessageBoardMessage> messageBoardMessagesPage =
			messageBoardMessageResource.getSiteMessageBoardMessagesPage(
				siteId, null, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			messageBoardMessagesPage.getTotalCount());

		MessageBoardMessage messageBoardMessage1 =
			testGetSiteMessageBoardMessagesPage_addMessageBoardMessage(
				siteId, randomMessageBoardMessage());

		MessageBoardMessage messageBoardMessage2 =
			testGetSiteMessageBoardMessagesPage_addMessageBoardMessage(
				siteId, randomMessageBoardMessage());

		MessageBoardMessage messageBoardMessage3 =
			testGetSiteMessageBoardMessagesPage_addMessageBoardMessage(
				siteId, randomMessageBoardMessage());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<MessageBoardMessage> page1 =
				messageBoardMessageResource.getSiteMessageBoardMessagesPage(
					siteId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				messageBoardMessage1,
				(List<MessageBoardMessage>)page1.getItems());

			Page<MessageBoardMessage> page2 =
				messageBoardMessageResource.getSiteMessageBoardMessagesPage(
					siteId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				messageBoardMessage2,
				(List<MessageBoardMessage>)page2.getItems());

			Page<MessageBoardMessage> page3 =
				messageBoardMessageResource.getSiteMessageBoardMessagesPage(
					siteId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				messageBoardMessage3,
				(List<MessageBoardMessage>)page3.getItems());
		}
		else {
			Page<MessageBoardMessage> page1 =
				messageBoardMessageResource.getSiteMessageBoardMessagesPage(
					siteId, null, null, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<MessageBoardMessage> messageBoardMessages1 =
				(List<MessageBoardMessage>)page1.getItems();

			Assert.assertEquals(
				messageBoardMessages1.toString(), totalCount + 2,
				messageBoardMessages1.size());

			Page<MessageBoardMessage> page2 =
				messageBoardMessageResource.getSiteMessageBoardMessagesPage(
					siteId, null, null, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<MessageBoardMessage> messageBoardMessages2 =
				(List<MessageBoardMessage>)page2.getItems();

			Assert.assertEquals(
				messageBoardMessages2.toString(), 1,
				messageBoardMessages2.size());

			Page<MessageBoardMessage> page3 =
				messageBoardMessageResource.getSiteMessageBoardMessagesPage(
					siteId, null, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				messageBoardMessage1,
				(List<MessageBoardMessage>)page3.getItems());
			assertContains(
				messageBoardMessage2,
				(List<MessageBoardMessage>)page3.getItems());
			assertContains(
				messageBoardMessage3,
				(List<MessageBoardMessage>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteMessageBoardMessagesPageWithSortDateTime()
		throws Exception {

		testGetSiteMessageBoardMessagesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, messageBoardMessage1, messageBoardMessage2) -> {
				BeanTestUtil.setProperty(
					messageBoardMessage1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteMessageBoardMessagesPageWithSortDouble()
		throws Exception {

		testGetSiteMessageBoardMessagesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, messageBoardMessage1, messageBoardMessage2) -> {
				BeanTestUtil.setProperty(
					messageBoardMessage1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					messageBoardMessage2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteMessageBoardMessagesPageWithSortInteger()
		throws Exception {

		testGetSiteMessageBoardMessagesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, messageBoardMessage1, messageBoardMessage2) -> {
				BeanTestUtil.setProperty(
					messageBoardMessage1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					messageBoardMessage2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteMessageBoardMessagesPageWithSortString()
		throws Exception {

		testGetSiteMessageBoardMessagesPageWithSort(
			EntityField.Type.STRING,
			(entityField, messageBoardMessage1, messageBoardMessage2) -> {
				Class<?> clazz = messageBoardMessage1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						messageBoardMessage1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						messageBoardMessage2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						messageBoardMessage1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						messageBoardMessage2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						messageBoardMessage1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						messageBoardMessage2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSiteMessageBoardMessagesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, MessageBoardMessage, MessageBoardMessage,
				 Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteMessageBoardMessagesPage_getSiteId();

		MessageBoardMessage messageBoardMessage1 = randomMessageBoardMessage();
		MessageBoardMessage messageBoardMessage2 = randomMessageBoardMessage();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, messageBoardMessage1, messageBoardMessage2);
		}

		messageBoardMessage1 =
			testGetSiteMessageBoardMessagesPage_addMessageBoardMessage(
				siteId, messageBoardMessage1);

		messageBoardMessage2 =
			testGetSiteMessageBoardMessagesPage_addMessageBoardMessage(
				siteId, messageBoardMessage2);

		Page<MessageBoardMessage> page =
			messageBoardMessageResource.getSiteMessageBoardMessagesPage(
				siteId, null, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<MessageBoardMessage> ascPage =
				messageBoardMessageResource.getSiteMessageBoardMessagesPage(
					siteId, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				messageBoardMessage1,
				(List<MessageBoardMessage>)ascPage.getItems());
			assertContains(
				messageBoardMessage2,
				(List<MessageBoardMessage>)ascPage.getItems());

			Page<MessageBoardMessage> descPage =
				messageBoardMessageResource.getSiteMessageBoardMessagesPage(
					siteId, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				messageBoardMessage2,
				(List<MessageBoardMessage>)descPage.getItems());
			assertContains(
				messageBoardMessage1,
				(List<MessageBoardMessage>)descPage.getItems());
		}
	}

	protected MessageBoardMessage
			testGetSiteMessageBoardMessagesPage_addMessageBoardMessage(
				Long siteId, MessageBoardMessage messageBoardMessage)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetSiteMessageBoardMessagesPage_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected Long testGetSiteMessageBoardMessagesPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	@Test
	public void testGetSiteUserMessageBoardMessagesActivityPage()
		throws Exception {

		Long siteId =
			testGetSiteUserMessageBoardMessagesActivityPage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteUserMessageBoardMessagesActivityPage_getIrrelevantSiteId();
		Long userId =
			testGetSiteUserMessageBoardMessagesActivityPage_getUserId();
		Long irrelevantUserId =
			testGetSiteUserMessageBoardMessagesActivityPage_getIrrelevantUserId();

		Page<MessageBoardMessage> page =
			messageBoardMessageResource.
				getSiteUserMessageBoardMessagesActivityPage(
					siteId, userId, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if ((irrelevantSiteId != null) && (irrelevantUserId != null)) {
			MessageBoardMessage irrelevantMessageBoardMessage =
				testGetSiteUserMessageBoardMessagesActivityPage_addMessageBoardMessage(
					irrelevantSiteId, irrelevantUserId,
					randomIrrelevantMessageBoardMessage());

			page =
				messageBoardMessageResource.
					getSiteUserMessageBoardMessagesActivityPage(
						irrelevantSiteId, irrelevantUserId,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantMessageBoardMessage,
				(List<MessageBoardMessage>)page.getItems());
			assertValid(
				page,
				testGetSiteUserMessageBoardMessagesActivityPage_getExpectedActions(
					irrelevantSiteId, irrelevantUserId));
		}

		MessageBoardMessage messageBoardMessage1 =
			testGetSiteUserMessageBoardMessagesActivityPage_addMessageBoardMessage(
				siteId, userId, randomMessageBoardMessage());

		MessageBoardMessage messageBoardMessage2 =
			testGetSiteUserMessageBoardMessagesActivityPage_addMessageBoardMessage(
				siteId, userId, randomMessageBoardMessage());

		page =
			messageBoardMessageResource.
				getSiteUserMessageBoardMessagesActivityPage(
					siteId, userId, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			messageBoardMessage1, (List<MessageBoardMessage>)page.getItems());
		assertContains(
			messageBoardMessage2, (List<MessageBoardMessage>)page.getItems());
		assertValid(
			page,
			testGetSiteUserMessageBoardMessagesActivityPage_getExpectedActions(
				siteId, userId));

		messageBoardMessageResource.deleteMessageBoardMessage(
			messageBoardMessage1.getId());

		messageBoardMessageResource.deleteMessageBoardMessage(
			messageBoardMessage2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSiteUserMessageBoardMessagesActivityPage_getExpectedActions(
				Long siteId, Long userId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSiteUserMessageBoardMessagesActivityPageWithPagination()
		throws Exception {

		Long siteId =
			testGetSiteUserMessageBoardMessagesActivityPage_getSiteId();
		Long userId =
			testGetSiteUserMessageBoardMessagesActivityPage_getUserId();

		Page<MessageBoardMessage> messageBoardMessagesPage =
			messageBoardMessageResource.
				getSiteUserMessageBoardMessagesActivityPage(
					siteId, userId, null);

		int totalCount = GetterUtil.getInteger(
			messageBoardMessagesPage.getTotalCount());

		MessageBoardMessage messageBoardMessage1 =
			testGetSiteUserMessageBoardMessagesActivityPage_addMessageBoardMessage(
				siteId, userId, randomMessageBoardMessage());

		MessageBoardMessage messageBoardMessage2 =
			testGetSiteUserMessageBoardMessagesActivityPage_addMessageBoardMessage(
				siteId, userId, randomMessageBoardMessage());

		MessageBoardMessage messageBoardMessage3 =
			testGetSiteUserMessageBoardMessagesActivityPage_addMessageBoardMessage(
				siteId, userId, randomMessageBoardMessage());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<MessageBoardMessage> page1 =
				messageBoardMessageResource.
					getSiteUserMessageBoardMessagesActivityPage(
						siteId, userId,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				messageBoardMessage1,
				(List<MessageBoardMessage>)page1.getItems());

			Page<MessageBoardMessage> page2 =
				messageBoardMessageResource.
					getSiteUserMessageBoardMessagesActivityPage(
						siteId, userId,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				messageBoardMessage2,
				(List<MessageBoardMessage>)page2.getItems());

			Page<MessageBoardMessage> page3 =
				messageBoardMessageResource.
					getSiteUserMessageBoardMessagesActivityPage(
						siteId, userId,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				messageBoardMessage3,
				(List<MessageBoardMessage>)page3.getItems());
		}
		else {
			Page<MessageBoardMessage> page1 =
				messageBoardMessageResource.
					getSiteUserMessageBoardMessagesActivityPage(
						siteId, userId, Pagination.of(1, totalCount + 2));

			List<MessageBoardMessage> messageBoardMessages1 =
				(List<MessageBoardMessage>)page1.getItems();

			Assert.assertEquals(
				messageBoardMessages1.toString(), totalCount + 2,
				messageBoardMessages1.size());

			Page<MessageBoardMessage> page2 =
				messageBoardMessageResource.
					getSiteUserMessageBoardMessagesActivityPage(
						siteId, userId, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<MessageBoardMessage> messageBoardMessages2 =
				(List<MessageBoardMessage>)page2.getItems();

			Assert.assertEquals(
				messageBoardMessages2.toString(), 1,
				messageBoardMessages2.size());

			Page<MessageBoardMessage> page3 =
				messageBoardMessageResource.
					getSiteUserMessageBoardMessagesActivityPage(
						siteId, userId, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				messageBoardMessage1,
				(List<MessageBoardMessage>)page3.getItems());
			assertContains(
				messageBoardMessage2,
				(List<MessageBoardMessage>)page3.getItems());
			assertContains(
				messageBoardMessage3,
				(List<MessageBoardMessage>)page3.getItems());
		}
	}

	protected MessageBoardMessage
			testGetSiteUserMessageBoardMessagesActivityPage_addMessageBoardMessage(
				Long siteId, Long userId,
				MessageBoardMessage messageBoardMessage)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetSiteUserMessageBoardMessagesActivityPage_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected Long
			testGetSiteUserMessageBoardMessagesActivityPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	protected Long testGetSiteUserMessageBoardMessagesActivityPage_getUserId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetSiteUserMessageBoardMessagesActivityPage_getIrrelevantUserId()
		throws Exception {

		return null;
	}

	@Test
	public void testPatchMessageBoardMessage() throws Exception {
		MessageBoardMessage postMessageBoardMessage =
			testPatchMessageBoardMessage_addMessageBoardMessage();

		MessageBoardMessage randomPatchMessageBoardMessage =
			randomPatchMessageBoardMessage();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardMessage patchMessageBoardMessage =
			messageBoardMessageResource.patchMessageBoardMessage(
				postMessageBoardMessage.getId(),
				randomPatchMessageBoardMessage);

		MessageBoardMessage expectedPatchMessageBoardMessage =
			postMessageBoardMessage.clone();

		BeanTestUtil.copyProperties(
			randomPatchMessageBoardMessage, expectedPatchMessageBoardMessage);

		MessageBoardMessage getMessageBoardMessage =
			messageBoardMessageResource.getMessageBoardMessage(
				patchMessageBoardMessage.getId());

		assertEquals(expectedPatchMessageBoardMessage, getMessageBoardMessage);
		assertValid(getMessageBoardMessage);
	}

	protected MessageBoardMessage
			testPatchMessageBoardMessage_addMessageBoardMessage()
		throws Exception {

		return testPostMessageBoardMessageMessageBoardMessage_addMessageBoardMessage(
			randomMessageBoardMessage());
	}

	@Test
	public void testPostMessageBoardMessageMessageBoardMessage()
		throws Exception {

		MessageBoardMessage randomMessageBoardMessage =
			randomMessageBoardMessage();

		MessageBoardMessage postMessageBoardMessage =
			testPostMessageBoardMessageMessageBoardMessage_addMessageBoardMessage(
				randomMessageBoardMessage);

		assertEquals(randomMessageBoardMessage, postMessageBoardMessage);
		assertValid(postMessageBoardMessage);
	}

	protected MessageBoardMessage
			testPostMessageBoardMessageMessageBoardMessage_addMessageBoardMessage(
				MessageBoardMessage messageBoardMessage)
		throws Exception {

		return messageBoardMessageResource.
			postMessageBoardMessageMessageBoardMessage(
				testGetMessageBoardMessageMessageBoardMessagesPage_getParentMessageBoardMessageId(),
				messageBoardMessage);
	}

	@Test
	public void testPostMessageBoardThreadMessageBoardMessage()
		throws Exception {

		MessageBoardMessage randomMessageBoardMessage =
			randomMessageBoardMessage();

		MessageBoardMessage postMessageBoardMessage =
			testPostMessageBoardThreadMessageBoardMessage_addMessageBoardMessage(
				randomMessageBoardMessage);

		assertEquals(randomMessageBoardMessage, postMessageBoardMessage);
		assertValid(postMessageBoardMessage);
	}

	protected MessageBoardMessage
			testPostMessageBoardThreadMessageBoardMessage_addMessageBoardMessage(
				MessageBoardMessage messageBoardMessage)
		throws Exception {

		return messageBoardMessageResource.
			postMessageBoardThreadMessageBoardMessage(
				testGetMessageBoardThreadMessageBoardMessagesPage_getMessageBoardThreadId(),
				messageBoardMessage);
	}

	@Test
	public void testGraphQLPostMessageBoardThreadMessageBoardMessage()
		throws Exception {

		MessageBoardMessage randomMessageBoardMessage =
			randomMessageBoardMessage();

		MessageBoardMessage messageBoardMessage =
			testGraphQLMessageBoardThreadMessageBoardMessage_addMessageBoardMessage(
				testGraphQLPostMessageBoardThreadMessageBoardMessage_getMessageBoardThreadId(
					randomMessageBoardMessage),
				randomMessageBoardMessage);

		Assert.assertTrue(
			equals(randomMessageBoardMessage, messageBoardMessage));
	}

	protected Long
			testGraphQLPostMessageBoardThreadMessageBoardMessage_getMessageBoardThreadId(
				MessageBoardMessage messageBoardMessage)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutMessageBoardMessage() throws Exception {
		MessageBoardMessage postMessageBoardMessage =
			testPutMessageBoardMessage_addMessageBoardMessage();

		MessageBoardMessage randomMessageBoardMessage =
			randomMessageBoardMessage();

		MessageBoardMessage putMessageBoardMessage =
			messageBoardMessageResource.putMessageBoardMessage(
				postMessageBoardMessage.getId(), randomMessageBoardMessage);

		assertEquals(randomMessageBoardMessage, putMessageBoardMessage);
		assertValid(putMessageBoardMessage);

		MessageBoardMessage getMessageBoardMessage =
			messageBoardMessageResource.getMessageBoardMessage(
				putMessageBoardMessage.getId());

		assertEquals(randomMessageBoardMessage, getMessageBoardMessage);
		assertValid(getMessageBoardMessage);
	}

	protected MessageBoardMessage
			testPutMessageBoardMessage_addMessageBoardMessage()
		throws Exception {

		return testPostMessageBoardMessageMessageBoardMessage_addMessageBoardMessage(
			randomMessageBoardMessage());
	}

	@Test
	public void testPutMessageBoardMessageMarkAsAnswer() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardMessage messageBoardMessage =
			testPutMessageBoardMessageMarkAsAnswer_addMessageBoardMessage();

		assertHttpResponseStatusCode(
			204,
			messageBoardMessageResource.
				putMessageBoardMessageMarkAsAnswerHttpResponse(
					messageBoardMessage.getId()));

		assertHttpResponseStatusCode(
			404,
			messageBoardMessageResource.
				putMessageBoardMessageMarkAsAnswerHttpResponse(0L));
	}

	protected MessageBoardMessage
			testPutMessageBoardMessageMarkAsAnswer_addMessageBoardMessage()
		throws Exception {

		return testPostMessageBoardMessageMessageBoardMessage_addMessageBoardMessage(
			randomMessageBoardMessage());
	}

	@Test
	public void testPutMessageBoardMessagePermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardMessage messageBoardMessage =
			testPutMessageBoardMessagePermissionsPage_addMessageBoardMessage();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			messageBoardMessageResource.
				putMessageBoardMessagePermissionsPageHttpResponse(
					messageBoardMessage.getId(),
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
			messageBoardMessageResource.
				putMessageBoardMessagePermissionsPageHttpResponse(
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

	protected MessageBoardMessage
			testPutMessageBoardMessagePermissionsPage_addMessageBoardMessage()
		throws Exception {

		return testPostMessageBoardMessageMessageBoardMessage_addMessageBoardMessage(
			randomMessageBoardMessage());
	}

	@Test
	public void testPutMessageBoardMessageSubscribe() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardMessage messageBoardMessage =
			testPutMessageBoardMessageSubscribe_addMessageBoardMessage();

		assertHttpResponseStatusCode(
			204,
			messageBoardMessageResource.
				putMessageBoardMessageSubscribeHttpResponse(
					messageBoardMessage.getId()));

		assertHttpResponseStatusCode(
			404,
			messageBoardMessageResource.
				putMessageBoardMessageSubscribeHttpResponse(0L));
	}

	protected MessageBoardMessage
			testPutMessageBoardMessageSubscribe_addMessageBoardMessage()
		throws Exception {

		return testPostMessageBoardMessageMessageBoardMessage_addMessageBoardMessage(
			randomMessageBoardMessage());
	}

	@Test
	public void testPutMessageBoardMessageUnmarkAsAnswer() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardMessage messageBoardMessage =
			testPutMessageBoardMessageUnmarkAsAnswer_addMessageBoardMessage();

		assertHttpResponseStatusCode(
			204,
			messageBoardMessageResource.
				putMessageBoardMessageUnmarkAsAnswerHttpResponse(
					messageBoardMessage.getId()));

		assertHttpResponseStatusCode(
			404,
			messageBoardMessageResource.
				putMessageBoardMessageUnmarkAsAnswerHttpResponse(0L));
	}

	protected MessageBoardMessage
			testPutMessageBoardMessageUnmarkAsAnswer_addMessageBoardMessage()
		throws Exception {

		return testPostMessageBoardMessageMessageBoardMessage_addMessageBoardMessage(
			randomMessageBoardMessage());
	}

	@Test
	public void testPutMessageBoardMessageUnsubscribe() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardMessage messageBoardMessage =
			testPutMessageBoardMessageUnsubscribe_addMessageBoardMessage();

		assertHttpResponseStatusCode(
			204,
			messageBoardMessageResource.
				putMessageBoardMessageUnsubscribeHttpResponse(
					messageBoardMessage.getId()));

		assertHttpResponseStatusCode(
			404,
			messageBoardMessageResource.
				putMessageBoardMessageUnsubscribeHttpResponse(0L));
	}

	protected MessageBoardMessage
			testPutMessageBoardMessageUnsubscribe_addMessageBoardMessage()
		throws Exception {

		return testPostMessageBoardMessageMessageBoardMessage_addMessageBoardMessage(
			randomMessageBoardMessage());
	}

	@Test
	public void testPutSiteMessageBoardMessageByExternalReferenceCode()
		throws Exception {

		MessageBoardMessage postMessageBoardMessage =
			testPutSiteMessageBoardMessageByExternalReferenceCode_addMessageBoardMessage();

		MessageBoardMessage randomMessageBoardMessage =
			randomMessageBoardMessage();

		MessageBoardMessage putMessageBoardMessage =
			messageBoardMessageResource.
				putSiteMessageBoardMessageByExternalReferenceCode(
					postMessageBoardMessage.getSiteId(),
					postMessageBoardMessage.getExternalReferenceCode(),
					randomMessageBoardMessage);

		assertEquals(randomMessageBoardMessage, putMessageBoardMessage);
		assertValid(putMessageBoardMessage);

		MessageBoardMessage getMessageBoardMessage =
			messageBoardMessageResource.
				getSiteMessageBoardMessageByExternalReferenceCode(
					putMessageBoardMessage.getSiteId(),
					putMessageBoardMessage.getExternalReferenceCode());

		assertEquals(randomMessageBoardMessage, getMessageBoardMessage);
		assertValid(getMessageBoardMessage);

		MessageBoardMessage newMessageBoardMessage =
			testPutSiteMessageBoardMessageByExternalReferenceCode_createMessageBoardMessage();

		putMessageBoardMessage =
			messageBoardMessageResource.
				putSiteMessageBoardMessageByExternalReferenceCode(
					newMessageBoardMessage.getSiteId(),
					newMessageBoardMessage.getExternalReferenceCode(),
					newMessageBoardMessage);

		assertEquals(newMessageBoardMessage, putMessageBoardMessage);
		assertValid(putMessageBoardMessage);

		getMessageBoardMessage =
			messageBoardMessageResource.
				getSiteMessageBoardMessageByExternalReferenceCode(
					putMessageBoardMessage.getSiteId(),
					putMessageBoardMessage.getExternalReferenceCode());

		assertEquals(newMessageBoardMessage, getMessageBoardMessage);

		Assert.assertEquals(
			newMessageBoardMessage.getExternalReferenceCode(),
			putMessageBoardMessage.getExternalReferenceCode());
	}

	protected MessageBoardMessage
			testPutSiteMessageBoardMessageByExternalReferenceCode_addMessageBoardMessage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected MessageBoardMessage
			testPutSiteMessageBoardMessageByExternalReferenceCode_createMessageBoardMessage()
		throws Exception {

		return randomMessageBoardMessage();
	}

	@Test
	public void testPutSiteMessageBoardMessagePermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardMessage messageBoardMessage =
			testPutSiteMessageBoardMessagePermissionsPage_addMessageBoardMessage();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			messageBoardMessageResource.
				putSiteMessageBoardMessagePermissionsPageHttpResponse(
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
			messageBoardMessageResource.
				putSiteMessageBoardMessagePermissionsPageHttpResponse(
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

	protected MessageBoardMessage
			testPutSiteMessageBoardMessagePermissionsPage_addMessageBoardMessage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		MessageBoardMessage messageBoardMessage1 =
			testBatchEngineDeleteImportTask_addMessageBoardMessage();

		testBatchEngineDeleteImportTask_deleteMessageBoardMessage(
			200, null, messageBoardMessage1.getId());

		assertHttpResponseStatusCode(
			404,
			messageBoardMessageResource.getMessageBoardMessageHttpResponse(
				messageBoardMessage1.getId()));
	}

	protected MessageBoardMessage
			testBatchEngineDeleteImportTask_addMessageBoardMessage()
		throws Exception {

		return testDeleteMessageBoardMessage_addMessageBoardMessage();
	}

	protected void testBatchEngineDeleteImportTask_deleteMessageBoardMessage(
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
				"com.liferay.headless.delivery.dto.v1_0.MessageBoardMessage",
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
	public void testGetMessageBoardMessageMyRating() throws Exception {
		MessageBoardMessage postMessageBoardMessage =
			testGetMessageBoardMessage_addMessageBoardMessage();

		Rating postRating = testGetMessageBoardMessageMyRating_addRating(
			postMessageBoardMessage.getId(), randomRating());

		Rating getRating =
			messageBoardMessageResource.getMessageBoardMessageMyRating(
				postMessageBoardMessage.getId());

		assertEquals(postRating, getRating);
		assertValid(getRating);
	}

	protected Rating testGetMessageBoardMessageMyRating_addRating(
			long messageBoardMessageId, Rating rating)
		throws Exception {

		return messageBoardMessageResource.postMessageBoardMessageMyRating(
			messageBoardMessageId, rating);
	}

	@Test
	public void testPostMessageBoardMessageMyRating() throws Exception {
		Assert.assertTrue(true);
	}

	@Test
	public void testPutMessageBoardMessageMyRating() throws Exception {
		MessageBoardMessage postMessageBoardMessage =
			testPutMessageBoardMessage_addMessageBoardMessage();

		testPutMessageBoardMessageMyRating_addRating(
			postMessageBoardMessage.getId(), randomRating());

		Rating randomRating = randomRating();

		Rating putRating =
			messageBoardMessageResource.putMessageBoardMessageMyRating(
				postMessageBoardMessage.getId(), randomRating);

		assertEquals(randomRating, putRating);
		assertValid(putRating);
	}

	protected Rating testPutMessageBoardMessageMyRating_addRating(
			long messageBoardMessageId, Rating rating)
		throws Exception {

		return messageBoardMessageResource.postMessageBoardMessageMyRating(
			messageBoardMessageId, rating);
	}

	protected MessageBoardMessage
			testGraphQLMessageBoardMessage_addMessageBoardMessage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected MessageBoardMessage
			testGraphQLSiteMessageBoardMessage_addMessageBoardMessage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected MessageBoardMessage
			testGraphQLMessageBoardThreadMessageBoardMessage_addMessageBoardMessage()
		throws Exception {

		return testGraphQLMessageBoardThreadMessageBoardMessage_addMessageBoardMessage(
			testGraphQLMessageBoardThreadMessageBoardMessage_getMessageBoardThreadId(),
			randomMessageBoardMessage());
	}

	protected Long
			testGraphQLMessageBoardThreadMessageBoardMessage_getMessageBoardThreadId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected MessageBoardMessage
			testGraphQLMessageBoardThreadMessageBoardMessage_addMessageBoardMessage(
				Long messageBoardThreadId,
				MessageBoardMessage messageBoardMessage)
		throws Exception {

		JSONDeserializer<MessageBoardMessage> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(MessageBoardMessage.class)) {

			if (getGraphQLValue(field.get(messageBoardMessage)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(messageBoardMessage)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createMessageBoardThreadMessageBoardMessage",
						new HashMap<String, Object>() {
							{
								put(
									"messageBoardThreadId",
									messageBoardThreadId);
								put("messageBoardMessage", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data",
				"JSONObject/createMessageBoardThreadMessageBoardMessage"),
			MessageBoardMessage.class);
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
		MessageBoardMessage messageBoardMessage,
		List<MessageBoardMessage> messageBoardMessages) {

		boolean contains = false;

		for (MessageBoardMessage item : messageBoardMessages) {
			if (equals(messageBoardMessage, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			messageBoardMessages + " does not contain " + messageBoardMessage,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		MessageBoardMessage messageBoardMessage1,
		MessageBoardMessage messageBoardMessage2) {

		Assert.assertTrue(
			messageBoardMessage1 + " does not equal " + messageBoardMessage2,
			equals(messageBoardMessage1, messageBoardMessage2));
	}

	protected void assertEquals(
		List<MessageBoardMessage> messageBoardMessages1,
		List<MessageBoardMessage> messageBoardMessages2) {

		Assert.assertEquals(
			messageBoardMessages1.size(), messageBoardMessages2.size());

		for (int i = 0; i < messageBoardMessages1.size(); i++) {
			MessageBoardMessage messageBoardMessage1 =
				messageBoardMessages1.get(i);
			MessageBoardMessage messageBoardMessage2 =
				messageBoardMessages2.get(i);

			assertEquals(messageBoardMessage1, messageBoardMessage2);
		}
	}

	protected void assertEquals(Rating rating1, Rating rating2) {
		Assert.assertTrue(
			rating1 + " does not equal " + rating2, equals(rating1, rating2));
	}

	protected void assertEqualsIgnoringOrder(
		List<MessageBoardMessage> messageBoardMessages1,
		List<MessageBoardMessage> messageBoardMessages2) {

		Assert.assertEquals(
			messageBoardMessages1.size(), messageBoardMessages2.size());

		for (MessageBoardMessage messageBoardMessage1 : messageBoardMessages1) {
			boolean contains = false;

			for (MessageBoardMessage messageBoardMessage2 :
					messageBoardMessages2) {

				if (equals(messageBoardMessage1, messageBoardMessage2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				messageBoardMessages2 + " does not contain " +
					messageBoardMessage1,
				contains);
		}
	}

	protected void assertValid(MessageBoardMessage messageBoardMessage)
		throws Exception {

		boolean valid = true;

		if (messageBoardMessage.getDateCreated() == null) {
			valid = false;
		}

		if (messageBoardMessage.getDateModified() == null) {
			valid = false;
		}

		if (messageBoardMessage.getId() == null) {
			valid = false;
		}

		if (!Objects.equals(
				messageBoardMessage.getSiteId(), testGroup.getGroupId())) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (messageBoardMessage.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("aggregateRating", additionalAssertFieldName)) {
				if (messageBoardMessage.getAggregateRating() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("anonymous", additionalAssertFieldName)) {
				if (messageBoardMessage.getAnonymous() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("articleBody", additionalAssertFieldName)) {
				if (messageBoardMessage.getArticleBody() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (messageBoardMessage.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"creatorStatistics", additionalAssertFieldName)) {

				if (messageBoardMessage.getCreatorStatistics() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (messageBoardMessage.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("encodingFormat", additionalAssertFieldName)) {
				if (messageBoardMessage.getEncodingFormat() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (messageBoardMessage.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("friendlyUrlPath", additionalAssertFieldName)) {
				if (messageBoardMessage.getFriendlyUrlPath() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("hasCompanyMx", additionalAssertFieldName)) {
				if (messageBoardMessage.getHasCompanyMx() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("headline", additionalAssertFieldName)) {
				if (messageBoardMessage.getHeadline() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("keywords", additionalAssertFieldName)) {
				if (messageBoardMessage.getKeywords() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"messageBoardSectionId", additionalAssertFieldName)) {

				if (messageBoardMessage.getMessageBoardSectionId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"messageBoardThreadId", additionalAssertFieldName)) {

				if (messageBoardMessage.getMessageBoardThreadId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("modified", additionalAssertFieldName)) {
				if (messageBoardMessage.getModified() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfMessageBoardAttachments",
					additionalAssertFieldName)) {

				if (messageBoardMessage.getNumberOfMessageBoardAttachments() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfMessageBoardMessages",
					additionalAssertFieldName)) {

				if (messageBoardMessage.getNumberOfMessageBoardMessages() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"parentMessageBoardMessageId", additionalAssertFieldName)) {

				if (messageBoardMessage.getParentMessageBoardMessageId() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("relatedContents", additionalAssertFieldName)) {
				if (messageBoardMessage.getRelatedContents() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("showAsAnswer", additionalAssertFieldName)) {
				if (messageBoardMessage.getShowAsAnswer() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (messageBoardMessage.getStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("subscribed", additionalAssertFieldName)) {
				if (messageBoardMessage.getSubscribed() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (messageBoardMessage.getViewableBy() == null) {
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

	protected void assertValid(Page<MessageBoardMessage> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<MessageBoardMessage> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<MessageBoardMessage> messageBoardMessages =
			page.getItems();

		int size = messageBoardMessages.size();

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
					com.liferay.headless.delivery.dto.v1_0.MessageBoardMessage.
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
		MessageBoardMessage messageBoardMessage1,
		MessageBoardMessage messageBoardMessage2) {

		if (messageBoardMessage1 == messageBoardMessage2) {
			return true;
		}

		if (!Objects.equals(
				messageBoardMessage1.getSiteId(),
				messageBoardMessage2.getSiteId())) {

			return false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)messageBoardMessage1.getActions(),
						(Map)messageBoardMessage2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("aggregateRating", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardMessage1.getAggregateRating(),
						messageBoardMessage2.getAggregateRating())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("anonymous", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardMessage1.getAnonymous(),
						messageBoardMessage2.getAnonymous())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("articleBody", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardMessage1.getArticleBody(),
						messageBoardMessage2.getArticleBody())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardMessage1.getCreator(),
						messageBoardMessage2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"creatorStatistics", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						messageBoardMessage1.getCreatorStatistics(),
						messageBoardMessage2.getCreatorStatistics())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardMessage1.getCustomFields(),
						messageBoardMessage2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardMessage1.getDateCreated(),
						messageBoardMessage2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardMessage1.getDateModified(),
						messageBoardMessage2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("encodingFormat", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardMessage1.getEncodingFormat(),
						messageBoardMessage2.getEncodingFormat())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						messageBoardMessage1.getExternalReferenceCode(),
						messageBoardMessage2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("friendlyUrlPath", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardMessage1.getFriendlyUrlPath(),
						messageBoardMessage2.getFriendlyUrlPath())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("hasCompanyMx", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardMessage1.getHasCompanyMx(),
						messageBoardMessage2.getHasCompanyMx())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("headline", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardMessage1.getHeadline(),
						messageBoardMessage2.getHeadline())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardMessage1.getId(),
						messageBoardMessage2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("keywords", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardMessage1.getKeywords(),
						messageBoardMessage2.getKeywords())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"messageBoardSectionId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						messageBoardMessage1.getMessageBoardSectionId(),
						messageBoardMessage2.getMessageBoardSectionId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"messageBoardThreadId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						messageBoardMessage1.getMessageBoardThreadId(),
						messageBoardMessage2.getMessageBoardThreadId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("modified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardMessage1.getModified(),
						messageBoardMessage2.getModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfMessageBoardAttachments",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						messageBoardMessage1.
							getNumberOfMessageBoardAttachments(),
						messageBoardMessage2.
							getNumberOfMessageBoardAttachments())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfMessageBoardMessages",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						messageBoardMessage1.getNumberOfMessageBoardMessages(),
						messageBoardMessage2.
							getNumberOfMessageBoardMessages())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"parentMessageBoardMessageId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						messageBoardMessage1.getParentMessageBoardMessageId(),
						messageBoardMessage2.
							getParentMessageBoardMessageId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("relatedContents", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardMessage1.getRelatedContents(),
						messageBoardMessage2.getRelatedContents())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("showAsAnswer", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardMessage1.getShowAsAnswer(),
						messageBoardMessage2.getShowAsAnswer())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardMessage1.getStatus(),
						messageBoardMessage2.getStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("subscribed", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardMessage1.getSubscribed(),
						messageBoardMessage2.getSubscribed())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardMessage1.getViewableBy(),
						messageBoardMessage2.getViewableBy())) {

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

		if (!(_messageBoardMessageResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_messageBoardMessageResource;

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
		MessageBoardMessage messageBoardMessage) {

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

		if (entityFieldName.equals("anonymous")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("articleBody")) {
			Object object = messageBoardMessage.getArticleBody();

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

		if (entityFieldName.equals("creatorStatistics")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("customFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = messageBoardMessage.getDateCreated();

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

				sb.append(_format.format(messageBoardMessage.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = messageBoardMessage.getDateModified();

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
					_format.format(messageBoardMessage.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("encodingFormat")) {
			Object object = messageBoardMessage.getEncodingFormat();

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
			Object object = messageBoardMessage.getExternalReferenceCode();

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
			Object object = messageBoardMessage.getFriendlyUrlPath();

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

		if (entityFieldName.equals("hasCompanyMx")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("headline")) {
			Object object = messageBoardMessage.getHeadline();

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

		if (entityFieldName.equals("messageBoardSectionId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("messageBoardThreadId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("modified")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("numberOfMessageBoardAttachments")) {
			sb.append(
				String.valueOf(
					messageBoardMessage.getNumberOfMessageBoardAttachments()));

			return sb.toString();
		}

		if (entityFieldName.equals("numberOfMessageBoardMessages")) {
			sb.append(
				String.valueOf(
					messageBoardMessage.getNumberOfMessageBoardMessages()));

			return sb.toString();
		}

		if (entityFieldName.equals("parentMessageBoardMessageId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("relatedContents")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("showAsAnswer")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("siteId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("status")) {
			Object object = messageBoardMessage.getStatus();

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

		if (entityFieldName.equals("subscribed")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
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

	protected MessageBoardMessage randomMessageBoardMessage() throws Exception {
		return new MessageBoardMessage() {
			{
				anonymous = RandomTestUtil.randomBoolean();
				articleBody = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				encodingFormat = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				friendlyUrlPath = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				hasCompanyMx = RandomTestUtil.randomBoolean();
				headline = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				messageBoardSectionId = RandomTestUtil.randomLong();
				messageBoardThreadId = RandomTestUtil.randomLong();
				modified = RandomTestUtil.randomBoolean();
				numberOfMessageBoardAttachments = RandomTestUtil.randomInt();
				numberOfMessageBoardMessages = RandomTestUtil.randomInt();
				parentMessageBoardMessageId = RandomTestUtil.randomLong();
				showAsAnswer = RandomTestUtil.randomBoolean();
				siteId = testGroup.getGroupId();
				status = StringUtil.toLowerCase(RandomTestUtil.randomString());
				subscribed = RandomTestUtil.randomBoolean();
			}
		};
	}

	protected MessageBoardMessage randomIrrelevantMessageBoardMessage()
		throws Exception {

		MessageBoardMessage randomIrrelevantMessageBoardMessage =
			randomMessageBoardMessage();

		randomIrrelevantMessageBoardMessage.setSiteId(
			irrelevantGroup.getGroupId());

		return randomIrrelevantMessageBoardMessage;
	}

	protected MessageBoardMessage randomPatchMessageBoardMessage()
		throws Exception {

		return randomMessageBoardMessage();
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

	protected MessageBoardMessageResource messageBoardMessageResource;
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
		LogFactoryUtil.getLog(BaseMessageBoardMessageResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.delivery.resource.v1_0.MessageBoardMessageResource
			_messageBoardMessageResource;

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