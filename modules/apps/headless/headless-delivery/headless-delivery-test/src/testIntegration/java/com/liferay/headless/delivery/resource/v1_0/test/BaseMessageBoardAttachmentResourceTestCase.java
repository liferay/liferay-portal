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
import com.liferay.headless.delivery.client.dto.v1_0.MessageBoardAttachment;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.resource.v1_0.MessageBoardAttachmentResource;
import com.liferay.headless.delivery.client.serdes.v1_0.MessageBoardAttachmentSerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
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

import java.io.File;

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
public abstract class BaseMessageBoardAttachmentResourceTestCase {

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

		_messageBoardAttachmentResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		messageBoardAttachmentResource = MessageBoardAttachmentResource.builder(
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

		MessageBoardAttachment messageBoardAttachment1 =
			randomMessageBoardAttachment();

		String json = objectMapper.writeValueAsString(messageBoardAttachment1);

		MessageBoardAttachment messageBoardAttachment2 =
			MessageBoardAttachmentSerDes.toDTO(json);

		Assert.assertTrue(
			equals(messageBoardAttachment1, messageBoardAttachment2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		MessageBoardAttachment messageBoardAttachment =
			randomMessageBoardAttachment();

		String json1 = objectMapper.writeValueAsString(messageBoardAttachment);
		String json2 = MessageBoardAttachmentSerDes.toJSON(
			messageBoardAttachment);

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

		MessageBoardAttachment messageBoardAttachment =
			randomMessageBoardAttachment();

		messageBoardAttachment.setContentUrl(regex);
		messageBoardAttachment.setContentValue(regex);
		messageBoardAttachment.setEncodingFormat(regex);
		messageBoardAttachment.setExternalReferenceCode(regex);
		messageBoardAttachment.setFileExtension(regex);
		messageBoardAttachment.setTitle(regex);

		String json = MessageBoardAttachmentSerDes.toJSON(
			messageBoardAttachment);

		Assert.assertFalse(json.contains(regex));

		messageBoardAttachment = MessageBoardAttachmentSerDes.toDTO(json);

		Assert.assertEquals(regex, messageBoardAttachment.getContentUrl());
		Assert.assertEquals(regex, messageBoardAttachment.getContentValue());
		Assert.assertEquals(regex, messageBoardAttachment.getEncodingFormat());
		Assert.assertEquals(
			regex, messageBoardAttachment.getExternalReferenceCode());
		Assert.assertEquals(regex, messageBoardAttachment.getFileExtension());
		Assert.assertEquals(regex, messageBoardAttachment.getTitle());
	}

	@Test
	public void testDeleteMessageBoardAttachment() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardAttachment messageBoardAttachment =
			testDeleteMessageBoardAttachment_addMessageBoardAttachment();

		assertHttpResponseStatusCode(
			204,
			messageBoardAttachmentResource.
				deleteMessageBoardAttachmentHttpResponse(
					messageBoardAttachment.getId()));

		assertHttpResponseStatusCode(
			404,
			messageBoardAttachmentResource.
				getMessageBoardAttachmentHttpResponse(
					messageBoardAttachment.getId()));
		assertHttpResponseStatusCode(
			404,
			messageBoardAttachmentResource.
				getMessageBoardAttachmentHttpResponse(0L));
	}

	protected MessageBoardAttachment
			testDeleteMessageBoardAttachment_addMessageBoardAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteMessageBoardAttachment() throws Exception {

		// No namespace

		MessageBoardAttachment messageBoardAttachment1 =
			testGraphQLDeleteMessageBoardAttachment_addMessageBoardAttachment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteMessageBoardAttachment",
						new HashMap<String, Object>() {
							{
								put(
									"messageBoardAttachmentId",
									messageBoardAttachment1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteMessageBoardAttachment"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"messageBoardAttachment",
					new HashMap<String, Object>() {
						{
							put(
								"messageBoardAttachmentId",
								messageBoardAttachment1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		MessageBoardAttachment messageBoardAttachment2 =
			testGraphQLDeleteMessageBoardAttachment_addMessageBoardAttachment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteMessageBoardAttachment",
							new HashMap<String, Object>() {
								{
									put(
										"messageBoardAttachmentId",
										messageBoardAttachment2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteMessageBoardAttachment"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"messageBoardAttachment",
						new HashMap<String, Object>() {
							{
								put(
									"messageBoardAttachmentId",
									messageBoardAttachment2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected MessageBoardAttachment
			testGraphQLDeleteMessageBoardAttachment_addMessageBoardAttachment()
		throws Exception {

		return testGraphQLMessageBoardAttachment_addMessageBoardAttachment();
	}

	@Test
	public void testDeleteMessageBoardAttachmentBatch() throws Exception {
		MessageBoardAttachment messageBoardAttachment1 =
			testDeleteMessageBoardAttachmentBatch_addMessageBoardAttachment();

		testDeleteMessageBoardAttachmentBatch_deleteMessageBoardAttachment(
			202, null, messageBoardAttachment1.getId());

		assertHttpResponseStatusCode(
			404,
			messageBoardAttachmentResource.
				getMessageBoardAttachmentHttpResponse(
					messageBoardAttachment1.getId()));
	}

	protected MessageBoardAttachment
			testDeleteMessageBoardAttachmentBatch_addMessageBoardAttachment()
		throws Exception {

		return testDeleteMessageBoardAttachment_addMessageBoardAttachment();
	}

	protected void
			testDeleteMessageBoardAttachmentBatch_deleteMessageBoardAttachment(
				int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			messageBoardAttachmentResource.
				deleteMessageBoardAttachmentBatchHttpResponse(
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
	public void testDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardAttachment messageBoardAttachment =
			testDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_addMessageBoardAttachment();

		assertHttpResponseStatusCode(
			204,
			messageBoardAttachmentResource.
				deleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCodeHttpResponse(
					testDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getSiteId(),
					testDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getMessageBoardMessageExternalReferenceCode(),
					messageBoardAttachment.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			messageBoardAttachmentResource.
				getSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCodeHttpResponse(
					testDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getSiteId(),
					testDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getMessageBoardMessageExternalReferenceCode(),
					messageBoardAttachment.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			messageBoardAttachmentResource.
				getSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCodeHttpResponse(
					testDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getSiteId(),
					testDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getMessageBoardMessageExternalReferenceCode(),
					"-"));
	}

	protected MessageBoardAttachment
			testDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_addMessageBoardAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getMessageBoardMessageExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode()
		throws Exception {

		// No namespace

		MessageBoardAttachment messageBoardAttachment1 =
			testGraphQLDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_addMessageBoardAttachment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" +
										testGraphQLDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getSiteId() +
											"\"");

								put(
									"messageBoardMessageExternalReferenceCode",
									"\"" +
										testGraphQLDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getMessageBoardMessageExternalReferenceCode() +
											"\"");
								put(
									"externalReferenceCode",
									"\"" +
										messageBoardAttachment1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"messageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"siteKey",
								"\"" +
									testGraphQLDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getSiteId() +
										"\"");

							put(
								"messageBoardMessageExternalReferenceCode",
								"\"" +
									testGraphQLDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getMessageBoardMessageExternalReferenceCode() +
										"\"");
							put(
								"externalReferenceCode",
								"\"" +
									messageBoardAttachment1.
										getExternalReferenceCode() + "\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		MessageBoardAttachment messageBoardAttachment2 =
			testGraphQLDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_addMessageBoardAttachment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" +
											testGraphQLDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getSiteId() +
												"\"");

									put(
										"messageBoardMessageExternalReferenceCode",
										"\"" +
											testGraphQLDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getMessageBoardMessageExternalReferenceCode() +
												"\"");
									put(
										"externalReferenceCode",
										"\"" +
											messageBoardAttachment2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"messageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" +
										testGraphQLDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getSiteId() +
											"\"");

								put(
									"messageBoardMessageExternalReferenceCode",
									"\"" +
										testGraphQLDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getMessageBoardMessageExternalReferenceCode() +
											"\"");
								put(
									"externalReferenceCode",
									"\"" +
										messageBoardAttachment2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Long
			testGraphQLDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testGraphQLDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getMessageBoardMessageExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected MessageBoardAttachment
			testGraphQLDeleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_addMessageBoardAttachment()
		throws Exception {

		return testGraphQLSiteMessageBoardAttachment_addMessageBoardAttachment();
	}

	@Test
	public void testGetMessageBoardAttachment() throws Exception {
		MessageBoardAttachment postMessageBoardAttachment =
			testGetMessageBoardAttachment_addMessageBoardAttachment();

		MessageBoardAttachment getMessageBoardAttachment =
			messageBoardAttachmentResource.getMessageBoardAttachment(
				postMessageBoardAttachment.getId());

		assertEquals(postMessageBoardAttachment, getMessageBoardAttachment);
		assertValid(getMessageBoardAttachment);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		MessageBoardAttachment postMessageBoardAttachment =
			testGetMessageBoardAttachment_addMessageBoardAttachment();

		MessageBoardAttachment getMessageBoardAttachment =
			messageBoardAttachmentResource.getMessageBoardAttachment(
				postMessageBoardAttachment.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.delivery.dto.v1_0.MessageBoardAttachment"
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
			postMessageBoardAttachment.getId());

		assertEquals(
			getMessageBoardAttachment,
			MessageBoardAttachmentSerDes.toDTO(item.toString()));
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

	protected MessageBoardAttachment
			testGetMessageBoardAttachment_addMessageBoardAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetMessageBoardAttachment() throws Exception {
		MessageBoardAttachment messageBoardAttachment =
			testGraphQLGetMessageBoardAttachment_addMessageBoardAttachment();

		// No namespace

		Assert.assertTrue(
			equals(
				messageBoardAttachment,
				MessageBoardAttachmentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"messageBoardAttachment",
								new HashMap<String, Object>() {
									{
										put(
											"messageBoardAttachmentId",
											messageBoardAttachment.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/messageBoardAttachment"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				messageBoardAttachment,
				MessageBoardAttachmentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"messageBoardAttachment",
									new HashMap<String, Object>() {
										{
											put(
												"messageBoardAttachmentId",
												messageBoardAttachment.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/messageBoardAttachment"))));
	}

	@Test
	public void testGraphQLGetMessageBoardAttachmentNotFound()
		throws Exception {

		Long irrelevantMessageBoardAttachmentId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"messageBoardAttachment",
						new HashMap<String, Object>() {
							{
								put(
									"messageBoardAttachmentId",
									irrelevantMessageBoardAttachmentId);
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
							"messageBoardAttachment",
							new HashMap<String, Object>() {
								{
									put(
										"messageBoardAttachmentId",
										irrelevantMessageBoardAttachmentId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected MessageBoardAttachment
			testGraphQLGetMessageBoardAttachment_addMessageBoardAttachment()
		throws Exception {

		return testGraphQLMessageBoardAttachment_addMessageBoardAttachment();
	}

	@Test
	public void testGetMessageBoardMessageMessageBoardAttachmentsPage()
		throws Exception {

		Long messageBoardMessageId =
			testGetMessageBoardMessageMessageBoardAttachmentsPage_getMessageBoardMessageId();
		Long irrelevantMessageBoardMessageId =
			testGetMessageBoardMessageMessageBoardAttachmentsPage_getIrrelevantMessageBoardMessageId();

		Page<MessageBoardAttachment> page =
			messageBoardAttachmentResource.
				getMessageBoardMessageMessageBoardAttachmentsPage(
					messageBoardMessageId);

		long totalCount = page.getTotalCount();

		if (irrelevantMessageBoardMessageId != null) {
			MessageBoardAttachment irrelevantMessageBoardAttachment =
				testGetMessageBoardMessageMessageBoardAttachmentsPage_addMessageBoardAttachment(
					irrelevantMessageBoardMessageId,
					randomIrrelevantMessageBoardAttachment());

			page =
				messageBoardAttachmentResource.
					getMessageBoardMessageMessageBoardAttachmentsPage(
						irrelevantMessageBoardMessageId);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantMessageBoardAttachment,
				(List<MessageBoardAttachment>)page.getItems());
			assertValid(
				page,
				testGetMessageBoardMessageMessageBoardAttachmentsPage_getExpectedActions(
					irrelevantMessageBoardMessageId));
		}

		MessageBoardAttachment messageBoardAttachment1 =
			testGetMessageBoardMessageMessageBoardAttachmentsPage_addMessageBoardAttachment(
				messageBoardMessageId, randomMessageBoardAttachment());

		MessageBoardAttachment messageBoardAttachment2 =
			testGetMessageBoardMessageMessageBoardAttachmentsPage_addMessageBoardAttachment(
				messageBoardMessageId, randomMessageBoardAttachment());

		page =
			messageBoardAttachmentResource.
				getMessageBoardMessageMessageBoardAttachmentsPage(
					messageBoardMessageId);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			messageBoardAttachment1,
			(List<MessageBoardAttachment>)page.getItems());
		assertContains(
			messageBoardAttachment2,
			(List<MessageBoardAttachment>)page.getItems());
		assertValid(
			page,
			testGetMessageBoardMessageMessageBoardAttachmentsPage_getExpectedActions(
				messageBoardMessageId));

		messageBoardAttachmentResource.deleteMessageBoardAttachment(
			messageBoardAttachment1.getId());

		messageBoardAttachmentResource.deleteMessageBoardAttachment(
			messageBoardAttachment2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetMessageBoardMessageMessageBoardAttachmentsPage_getExpectedActions(
				Long messageBoardMessageId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/message-board-messages/{messageBoardMessageId}/message-board-attachments/batch".
				replace(
					"{messageBoardMessageId}",
					String.valueOf(messageBoardMessageId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	protected MessageBoardAttachment
			testGetMessageBoardMessageMessageBoardAttachmentsPage_addMessageBoardAttachment(
				Long messageBoardMessageId,
				MessageBoardAttachment messageBoardAttachment)
		throws Exception {

		return messageBoardAttachmentResource.
			postMessageBoardMessageMessageBoardAttachment(
				messageBoardMessageId, messageBoardAttachment,
				getMultipartFiles());
	}

	protected Long
			testGetMessageBoardMessageMessageBoardAttachmentsPage_getMessageBoardMessageId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetMessageBoardMessageMessageBoardAttachmentsPage_getIrrelevantMessageBoardMessageId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetMessageBoardMessageMessageBoardAttachmentsPage()
		throws Exception {

		Long messageBoardMessageId =
			testGetMessageBoardMessageMessageBoardAttachmentsPage_getMessageBoardMessageId();

		GraphQLField graphQLField = new GraphQLField(
			"messageBoardMessageMessageBoardAttachments",
			new HashMap<String, Object>() {
				{
					put("messageBoardMessageId", messageBoardMessageId);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject messageBoardMessageMessageBoardAttachmentsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/messageBoardMessageMessageBoardAttachments");

		long totalCount =
			messageBoardMessageMessageBoardAttachmentsJSONObject.getLong(
				"totalCount");

		MessageBoardAttachment messageBoardAttachment1 =
			testGraphQLMessageBoardMessageMessageBoardAttachment_addMessageBoardAttachment(
				messageBoardMessageId, randomMessageBoardAttachment());

		MessageBoardAttachment messageBoardAttachment2 =
			testGraphQLMessageBoardMessageMessageBoardAttachment_addMessageBoardAttachment(
				messageBoardMessageId, randomMessageBoardAttachment());

		messageBoardMessageMessageBoardAttachmentsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/messageBoardMessageMessageBoardAttachments");

		Assert.assertEquals(
			totalCount + 2,
			messageBoardMessageMessageBoardAttachmentsJSONObject.getLong(
				"totalCount"));

		assertContains(
			messageBoardAttachment1,
			Arrays.asList(
				MessageBoardAttachmentSerDes.toDTOs(
					messageBoardMessageMessageBoardAttachmentsJSONObject.
						getString("items"))));
		assertContains(
			messageBoardAttachment2,
			Arrays.asList(
				MessageBoardAttachmentSerDes.toDTOs(
					messageBoardMessageMessageBoardAttachmentsJSONObject.
						getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		messageBoardMessageMessageBoardAttachmentsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField("headlessDelivery_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"JSONObject/messageBoardMessageMessageBoardAttachments");

		Assert.assertEquals(
			totalCount + 2,
			messageBoardMessageMessageBoardAttachmentsJSONObject.getLong(
				"totalCount"));

		assertContains(
			messageBoardAttachment1,
			Arrays.asList(
				MessageBoardAttachmentSerDes.toDTOs(
					messageBoardMessageMessageBoardAttachmentsJSONObject.
						getString("items"))));
		assertContains(
			messageBoardAttachment2,
			Arrays.asList(
				MessageBoardAttachmentSerDes.toDTOs(
					messageBoardMessageMessageBoardAttachmentsJSONObject.
						getString("items"))));
	}

	@Test
	public void testGetMessageBoardThreadMessageBoardAttachmentsPage()
		throws Exception {

		Long messageBoardThreadId =
			testGetMessageBoardThreadMessageBoardAttachmentsPage_getMessageBoardThreadId();
		Long irrelevantMessageBoardThreadId =
			testGetMessageBoardThreadMessageBoardAttachmentsPage_getIrrelevantMessageBoardThreadId();

		Page<MessageBoardAttachment> page =
			messageBoardAttachmentResource.
				getMessageBoardThreadMessageBoardAttachmentsPage(
					messageBoardThreadId);

		long totalCount = page.getTotalCount();

		if (irrelevantMessageBoardThreadId != null) {
			MessageBoardAttachment irrelevantMessageBoardAttachment =
				testGetMessageBoardThreadMessageBoardAttachmentsPage_addMessageBoardAttachment(
					irrelevantMessageBoardThreadId,
					randomIrrelevantMessageBoardAttachment());

			page =
				messageBoardAttachmentResource.
					getMessageBoardThreadMessageBoardAttachmentsPage(
						irrelevantMessageBoardThreadId);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantMessageBoardAttachment,
				(List<MessageBoardAttachment>)page.getItems());
			assertValid(
				page,
				testGetMessageBoardThreadMessageBoardAttachmentsPage_getExpectedActions(
					irrelevantMessageBoardThreadId));
		}

		MessageBoardAttachment messageBoardAttachment1 =
			testGetMessageBoardThreadMessageBoardAttachmentsPage_addMessageBoardAttachment(
				messageBoardThreadId, randomMessageBoardAttachment());

		MessageBoardAttachment messageBoardAttachment2 =
			testGetMessageBoardThreadMessageBoardAttachmentsPage_addMessageBoardAttachment(
				messageBoardThreadId, randomMessageBoardAttachment());

		page =
			messageBoardAttachmentResource.
				getMessageBoardThreadMessageBoardAttachmentsPage(
					messageBoardThreadId);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			messageBoardAttachment1,
			(List<MessageBoardAttachment>)page.getItems());
		assertContains(
			messageBoardAttachment2,
			(List<MessageBoardAttachment>)page.getItems());
		assertValid(
			page,
			testGetMessageBoardThreadMessageBoardAttachmentsPage_getExpectedActions(
				messageBoardThreadId));

		messageBoardAttachmentResource.deleteMessageBoardAttachment(
			messageBoardAttachment1.getId());

		messageBoardAttachmentResource.deleteMessageBoardAttachment(
			messageBoardAttachment2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetMessageBoardThreadMessageBoardAttachmentsPage_getExpectedActions(
				Long messageBoardThreadId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/message-board-threads/{messageBoardThreadId}/message-board-attachments/batch".
				replace(
					"{messageBoardThreadId}",
					String.valueOf(messageBoardThreadId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	protected MessageBoardAttachment
			testGetMessageBoardThreadMessageBoardAttachmentsPage_addMessageBoardAttachment(
				Long messageBoardThreadId,
				MessageBoardAttachment messageBoardAttachment)
		throws Exception {

		return messageBoardAttachmentResource.
			postMessageBoardThreadMessageBoardAttachment(
				messageBoardThreadId, messageBoardAttachment,
				getMultipartFiles());
	}

	protected Long
			testGetMessageBoardThreadMessageBoardAttachmentsPage_getMessageBoardThreadId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetMessageBoardThreadMessageBoardAttachmentsPage_getIrrelevantMessageBoardThreadId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetMessageBoardThreadMessageBoardAttachmentsPage()
		throws Exception {

		Long messageBoardThreadId =
			testGetMessageBoardThreadMessageBoardAttachmentsPage_getMessageBoardThreadId();

		GraphQLField graphQLField = new GraphQLField(
			"messageBoardThreadMessageBoardAttachments",
			new HashMap<String, Object>() {
				{
					put("messageBoardThreadId", messageBoardThreadId);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject messageBoardThreadMessageBoardAttachmentsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/messageBoardThreadMessageBoardAttachments");

		long totalCount =
			messageBoardThreadMessageBoardAttachmentsJSONObject.getLong(
				"totalCount");

		MessageBoardAttachment messageBoardAttachment1 =
			testGraphQLMessageBoardThreadMessageBoardAttachment_addMessageBoardAttachment(
				messageBoardThreadId, randomMessageBoardAttachment());

		MessageBoardAttachment messageBoardAttachment2 =
			testGraphQLMessageBoardThreadMessageBoardAttachment_addMessageBoardAttachment(
				messageBoardThreadId, randomMessageBoardAttachment());

		messageBoardThreadMessageBoardAttachmentsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/messageBoardThreadMessageBoardAttachments");

		Assert.assertEquals(
			totalCount + 2,
			messageBoardThreadMessageBoardAttachmentsJSONObject.getLong(
				"totalCount"));

		assertContains(
			messageBoardAttachment1,
			Arrays.asList(
				MessageBoardAttachmentSerDes.toDTOs(
					messageBoardThreadMessageBoardAttachmentsJSONObject.
						getString("items"))));
		assertContains(
			messageBoardAttachment2,
			Arrays.asList(
				MessageBoardAttachmentSerDes.toDTOs(
					messageBoardThreadMessageBoardAttachmentsJSONObject.
						getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		messageBoardThreadMessageBoardAttachmentsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField("headlessDelivery_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"JSONObject/messageBoardThreadMessageBoardAttachments");

		Assert.assertEquals(
			totalCount + 2,
			messageBoardThreadMessageBoardAttachmentsJSONObject.getLong(
				"totalCount"));

		assertContains(
			messageBoardAttachment1,
			Arrays.asList(
				MessageBoardAttachmentSerDes.toDTOs(
					messageBoardThreadMessageBoardAttachmentsJSONObject.
						getString("items"))));
		assertContains(
			messageBoardAttachment2,
			Arrays.asList(
				MessageBoardAttachmentSerDes.toDTOs(
					messageBoardThreadMessageBoardAttachmentsJSONObject.
						getString("items"))));
	}

	@Test
	public void testGetSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode()
		throws Exception {

		MessageBoardAttachment postMessageBoardAttachment =
			testGetSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_addMessageBoardAttachment();

		MessageBoardAttachment getMessageBoardAttachment =
			messageBoardAttachmentResource.
				getSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode(
					testGetSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getSiteId(),
					testGetSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getMessageBoardMessageExternalReferenceCode(),
					postMessageBoardAttachment.getExternalReferenceCode());

		assertEquals(postMessageBoardAttachment, getMessageBoardAttachment);
		assertValid(getMessageBoardAttachment);
	}

	protected MessageBoardAttachment
			testGetSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_addMessageBoardAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testGetSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getMessageBoardMessageExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode()
		throws Exception {

		MessageBoardAttachment messageBoardAttachment =
			testGraphQLGetSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_addMessageBoardAttachment();

		// No namespace

		Assert.assertTrue(
			equals(
				messageBoardAttachment,
				MessageBoardAttachmentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"messageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" +
												testGraphQLGetSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getSiteId() +
													"\"");

										put(
											"messageBoardMessageExternalReferenceCode",
											"\"" +
												testGraphQLGetSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getMessageBoardMessageExternalReferenceCode() +
													"\"");
										put(
											"externalReferenceCode",
											"\"" +
												messageBoardAttachment.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/messageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				messageBoardAttachment,
				MessageBoardAttachmentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"messageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													testGraphQLGetSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getSiteId() +
														"\"");

											put(
												"messageBoardMessageExternalReferenceCode",
												"\"" +
													testGraphQLGetSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getMessageBoardMessageExternalReferenceCode() +
														"\"");
											put(
												"externalReferenceCode",
												"\"" +
													messageBoardAttachment.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/messageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode"))));
	}

	protected Long
			testGraphQLGetSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testGraphQLGetSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_getMessageBoardMessageExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantMessageBoardMessageExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";
		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"messageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + irrelevantGroup.getGroupId() + "\"");
								put(
									"messageBoardMessageExternalReferenceCode",
									irrelevantMessageBoardMessageExternalReferenceCode);
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
							"messageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + irrelevantGroup.getGroupId() +
											"\"");
									put(
										"messageBoardMessageExternalReferenceCode",
										irrelevantMessageBoardMessageExternalReferenceCode);
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected MessageBoardAttachment
			testGraphQLGetSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode_addMessageBoardAttachment()
		throws Exception {

		return testGraphQLSiteMessageBoardAttachment_addMessageBoardAttachment();
	}

	@Test
	public void testPostMessageBoardMessageMessageBoardAttachment()
		throws Exception {

		MessageBoardAttachment randomMessageBoardAttachment =
			randomMessageBoardAttachment();

		Map<String, File> multipartFiles = getMultipartFiles();

		MessageBoardAttachment postMessageBoardAttachment =
			testPostMessageBoardMessageMessageBoardAttachment_addMessageBoardAttachment(
				randomMessageBoardAttachment, multipartFiles);

		assertEquals(randomMessageBoardAttachment, postMessageBoardAttachment);
		assertValid(postMessageBoardAttachment);

		assertValid(postMessageBoardAttachment, multipartFiles);
	}

	protected MessageBoardAttachment
			testPostMessageBoardMessageMessageBoardAttachment_addMessageBoardAttachment(
				MessageBoardAttachment messageBoardAttachment,
				Map<String, File> multipartFiles)
		throws Exception {

		return messageBoardAttachmentResource.
			postMessageBoardMessageMessageBoardAttachment(
				testGetMessageBoardMessageMessageBoardAttachmentsPage_getMessageBoardMessageId(),
				messageBoardAttachment, multipartFiles);
	}

	@Test
	public void testPostMessageBoardThreadMessageBoardAttachment()
		throws Exception {

		MessageBoardAttachment randomMessageBoardAttachment =
			randomMessageBoardAttachment();

		Map<String, File> multipartFiles = getMultipartFiles();

		MessageBoardAttachment postMessageBoardAttachment =
			testPostMessageBoardThreadMessageBoardAttachment_addMessageBoardAttachment(
				randomMessageBoardAttachment, multipartFiles);

		assertEquals(randomMessageBoardAttachment, postMessageBoardAttachment);
		assertValid(postMessageBoardAttachment);

		assertValid(postMessageBoardAttachment, multipartFiles);
	}

	protected MessageBoardAttachment
			testPostMessageBoardThreadMessageBoardAttachment_addMessageBoardAttachment(
				MessageBoardAttachment messageBoardAttachment,
				Map<String, File> multipartFiles)
		throws Exception {

		return messageBoardAttachmentResource.
			postMessageBoardThreadMessageBoardAttachment(
				testGetMessageBoardThreadMessageBoardAttachmentsPage_getMessageBoardThreadId(),
				messageBoardAttachment, multipartFiles);
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		MessageBoardAttachment messageBoardAttachment1 =
			testBatchEngineDeleteImportTask_addMessageBoardAttachment();

		testBatchEngineDeleteImportTask_deleteMessageBoardAttachment(
			200, null, messageBoardAttachment1.getId());

		assertHttpResponseStatusCode(
			404,
			messageBoardAttachmentResource.
				getMessageBoardAttachmentHttpResponse(
					messageBoardAttachment1.getId()));
	}

	protected MessageBoardAttachment
			testBatchEngineDeleteImportTask_addMessageBoardAttachment()
		throws Exception {

		return testDeleteMessageBoardAttachment_addMessageBoardAttachment();
	}

	protected void testBatchEngineDeleteImportTask_deleteMessageBoardAttachment(
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
				"com.liferay.headless.delivery.dto.v1_0.MessageBoardAttachment",
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

	protected MessageBoardAttachment
			testGraphQLMessageBoardAttachment_addMessageBoardAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected MessageBoardAttachment
			testGraphQLSiteMessageBoardAttachment_addMessageBoardAttachment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected MessageBoardAttachment
			testGraphQLMessageBoardMessageMessageBoardAttachment_addMessageBoardAttachment()
		throws Exception {

		return testGraphQLMessageBoardMessageMessageBoardAttachment_addMessageBoardAttachment(
			testGraphQLMessageBoardMessageMessageBoardAttachment_getMessageBoardMessageId(),
			randomMessageBoardAttachment());
	}

	protected Long
			testGraphQLMessageBoardMessageMessageBoardAttachment_getMessageBoardMessageId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected MessageBoardAttachment
			testGraphQLMessageBoardMessageMessageBoardAttachment_addMessageBoardAttachment(
				Long messageBoardMessageId,
				MessageBoardAttachment messageBoardAttachment)
		throws Exception {

		JSONDeserializer<MessageBoardAttachment> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(MessageBoardAttachment.class)) {

			if (getGraphQLValue(field.get(messageBoardAttachment)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(messageBoardAttachment)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createMessageBoardMessageMessageBoardAttachment",
						new HashMap<String, Object>() {
							{
								put(
									"messageBoardMessageId",
									messageBoardMessageId);
								put("messageBoardAttachment", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data",
				"JSONObject/createMessageBoardMessageMessageBoardAttachment"),
			MessageBoardAttachment.class);
	}

	protected MessageBoardAttachment
			testGraphQLMessageBoardThreadMessageBoardAttachment_addMessageBoardAttachment()
		throws Exception {

		return testGraphQLMessageBoardThreadMessageBoardAttachment_addMessageBoardAttachment(
			testGraphQLMessageBoardThreadMessageBoardAttachment_getMessageBoardThreadId(),
			randomMessageBoardAttachment());
	}

	protected Long
			testGraphQLMessageBoardThreadMessageBoardAttachment_getMessageBoardThreadId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected MessageBoardAttachment
			testGraphQLMessageBoardThreadMessageBoardAttachment_addMessageBoardAttachment(
				Long messageBoardThreadId,
				MessageBoardAttachment messageBoardAttachment)
		throws Exception {

		JSONDeserializer<MessageBoardAttachment> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(MessageBoardAttachment.class)) {

			if (getGraphQLValue(field.get(messageBoardAttachment)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(messageBoardAttachment)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createMessageBoardThreadMessageBoardAttachment",
						new HashMap<String, Object>() {
							{
								put(
									"messageBoardThreadId",
									messageBoardThreadId);
								put("messageBoardAttachment", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data",
				"JSONObject/createMessageBoardThreadMessageBoardAttachment"),
			MessageBoardAttachment.class);
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
		MessageBoardAttachment messageBoardAttachment,
		List<MessageBoardAttachment> messageBoardAttachments) {

		boolean contains = false;

		for (MessageBoardAttachment item : messageBoardAttachments) {
			if (equals(messageBoardAttachment, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			messageBoardAttachments + " does not contain " +
				messageBoardAttachment,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		MessageBoardAttachment messageBoardAttachment1,
		MessageBoardAttachment messageBoardAttachment2) {

		Assert.assertTrue(
			messageBoardAttachment1 + " does not equal " +
				messageBoardAttachment2,
			equals(messageBoardAttachment1, messageBoardAttachment2));
	}

	protected void assertEquals(
		List<MessageBoardAttachment> messageBoardAttachments1,
		List<MessageBoardAttachment> messageBoardAttachments2) {

		Assert.assertEquals(
			messageBoardAttachments1.size(), messageBoardAttachments2.size());

		for (int i = 0; i < messageBoardAttachments1.size(); i++) {
			MessageBoardAttachment messageBoardAttachment1 =
				messageBoardAttachments1.get(i);
			MessageBoardAttachment messageBoardAttachment2 =
				messageBoardAttachments2.get(i);

			assertEquals(messageBoardAttachment1, messageBoardAttachment2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<MessageBoardAttachment> messageBoardAttachments1,
		List<MessageBoardAttachment> messageBoardAttachments2) {

		Assert.assertEquals(
			messageBoardAttachments1.size(), messageBoardAttachments2.size());

		for (MessageBoardAttachment messageBoardAttachment1 :
				messageBoardAttachments1) {

			boolean contains = false;

			for (MessageBoardAttachment messageBoardAttachment2 :
					messageBoardAttachments2) {

				if (equals(messageBoardAttachment1, messageBoardAttachment2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				messageBoardAttachments2 + " does not contain " +
					messageBoardAttachment1,
				contains);
		}
	}

	protected void assertValid(MessageBoardAttachment messageBoardAttachment)
		throws Exception {

		boolean valid = true;

		if (messageBoardAttachment.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (messageBoardAttachment.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("contentUrl", additionalAssertFieldName)) {
				if (messageBoardAttachment.getContentUrl() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("contentValue", additionalAssertFieldName)) {
				if (messageBoardAttachment.getContentValue() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("encodingFormat", additionalAssertFieldName)) {
				if (messageBoardAttachment.getEncodingFormat() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (messageBoardAttachment.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("fileExtension", additionalAssertFieldName)) {
				if (messageBoardAttachment.getFileExtension() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("sizeInBytes", additionalAssertFieldName)) {
				if (messageBoardAttachment.getSizeInBytes() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (messageBoardAttachment.getTitle() == null) {
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

	protected void assertValid(
			MessageBoardAttachment messageBoardAttachment,
			Map<String, File> multipartFiles)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertValid(Page<MessageBoardAttachment> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<MessageBoardAttachment> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<MessageBoardAttachment> messageBoardAttachments =
			page.getItems();

		int size = messageBoardAttachments.size();

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
					com.liferay.headless.delivery.dto.v1_0.
						MessageBoardAttachment.class)) {

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
		MessageBoardAttachment messageBoardAttachment1,
		MessageBoardAttachment messageBoardAttachment2) {

		if (messageBoardAttachment1 == messageBoardAttachment2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)messageBoardAttachment1.getActions(),
						(Map)messageBoardAttachment2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("contentUrl", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardAttachment1.getContentUrl(),
						messageBoardAttachment2.getContentUrl())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("contentValue", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardAttachment1.getContentValue(),
						messageBoardAttachment2.getContentValue())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("encodingFormat", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardAttachment1.getEncodingFormat(),
						messageBoardAttachment2.getEncodingFormat())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						messageBoardAttachment1.getExternalReferenceCode(),
						messageBoardAttachment2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("fileExtension", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardAttachment1.getFileExtension(),
						messageBoardAttachment2.getFileExtension())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardAttachment1.getId(),
						messageBoardAttachment2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("sizeInBytes", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardAttachment1.getSizeInBytes(),
						messageBoardAttachment2.getSizeInBytes())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardAttachment1.getTitle(),
						messageBoardAttachment2.getTitle())) {

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

		if (!(_messageBoardAttachmentResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_messageBoardAttachmentResource;

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
		MessageBoardAttachment messageBoardAttachment) {

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

		if (entityFieldName.equals("contentUrl")) {
			Object object = messageBoardAttachment.getContentUrl();

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

		if (entityFieldName.equals("contentValue")) {
			Object object = messageBoardAttachment.getContentValue();

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
			Object object = messageBoardAttachment.getEncodingFormat();

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
			Object object = messageBoardAttachment.getExternalReferenceCode();

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

		if (entityFieldName.equals("fileExtension")) {
			Object object = messageBoardAttachment.getFileExtension();

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

		if (entityFieldName.equals("sizeInBytes")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("title")) {
			Object object = messageBoardAttachment.getTitle();

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

	protected Map<String, File> getMultipartFiles() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
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

	protected MessageBoardAttachment randomMessageBoardAttachment()
		throws Exception {

		return new MessageBoardAttachment() {
			{
				contentUrl = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				contentValue = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				encodingFormat = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				fileExtension = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				sizeInBytes = RandomTestUtil.randomLong();
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected MessageBoardAttachment randomIrrelevantMessageBoardAttachment()
		throws Exception {

		MessageBoardAttachment randomIrrelevantMessageBoardAttachment =
			randomMessageBoardAttachment();

		return randomIrrelevantMessageBoardAttachment;
	}

	protected MessageBoardAttachment randomPatchMessageBoardAttachment()
		throws Exception {

		return randomMessageBoardAttachment();
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

	protected MessageBoardAttachmentResource messageBoardAttachmentResource;
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
		LogFactoryUtil.getLog(BaseMessageBoardAttachmentResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.delivery.resource.v1_0.
			MessageBoardAttachmentResource _messageBoardAttachmentResource;

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