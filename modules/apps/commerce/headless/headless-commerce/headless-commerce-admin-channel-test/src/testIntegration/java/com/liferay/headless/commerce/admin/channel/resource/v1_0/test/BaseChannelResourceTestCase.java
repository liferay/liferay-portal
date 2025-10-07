/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.resource.v1_0.test;

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
import com.liferay.headless.commerce.admin.channel.client.dto.v1_0.Channel;
import com.liferay.headless.commerce.admin.channel.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.channel.client.pagination.Page;
import com.liferay.headless.commerce.admin.channel.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.channel.client.resource.v1_0.ChannelResource;
import com.liferay.headless.commerce.admin.channel.client.serdes.v1_0.ChannelSerDes;
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
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public abstract class BaseChannelResourceTestCase {

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

		_channelResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		channelResource = ChannelResource.builder(
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

		Channel channel1 = randomChannel();

		String json = objectMapper.writeValueAsString(channel1);

		Channel channel2 = ChannelSerDes.toDTO(json);

		Assert.assertTrue(equals(channel1, channel2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Channel channel = randomChannel();

		String json1 = objectMapper.writeValueAsString(channel);
		String json2 = ChannelSerDes.toJSON(channel);

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

		Channel channel = randomChannel();

		channel.setAccountExternalReferenceCode(regex);
		channel.setCurrencyCode(regex);
		channel.setCurrencyExternalReferenceCode(regex);
		channel.setExternalReferenceCode(regex);
		channel.setName(regex);
		channel.setType(regex);

		String json = ChannelSerDes.toJSON(channel);

		Assert.assertFalse(json.contains(regex));

		channel = ChannelSerDes.toDTO(json);

		Assert.assertEquals(regex, channel.getAccountExternalReferenceCode());
		Assert.assertEquals(regex, channel.getCurrencyCode());
		Assert.assertEquals(regex, channel.getCurrencyExternalReferenceCode());
		Assert.assertEquals(regex, channel.getExternalReferenceCode());
		Assert.assertEquals(regex, channel.getName());
		Assert.assertEquals(regex, channel.getType());
	}

	@Test
	public void testDeleteChannel() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Channel channel = testDeleteChannel_addChannel();

		assertHttpResponseStatusCode(
			204, channelResource.deleteChannelHttpResponse(channel.getId()));

		assertHttpResponseStatusCode(
			404, channelResource.getChannelHttpResponse(channel.getId()));
		assertHttpResponseStatusCode(
			404, channelResource.getChannelHttpResponse(0L));
	}

	protected Channel testDeleteChannel_addChannel() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteChannel() throws Exception {

		// No namespace

		Channel channel1 = testGraphQLDeleteChannel_addChannel();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteChannel",
						new HashMap<String, Object>() {
							{
								put("channelId", channel1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteChannel"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"channel",
					new HashMap<String, Object>() {
						{
							put("channelId", channel1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminChannel_v1_0

		Channel channel2 = testGraphQLDeleteChannel_addChannel();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminChannel_v1_0",
						new GraphQLField(
							"deleteChannel",
							new HashMap<String, Object>() {
								{
									put("channelId", channel2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminChannel_v1_0",
				"Object/deleteChannel"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminChannel_v1_0",
					new GraphQLField(
						"channel",
						new HashMap<String, Object>() {
							{
								put("channelId", channel2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Channel testGraphQLDeleteChannel_addChannel() throws Exception {
		return testGraphQLChannel_addChannel();
	}

	@Test
	public void testDeleteChannelBatch() throws Exception {
		Channel channel1 = testDeleteChannelBatch_addChannel();

		testDeleteChannelBatch_deleteChannel(
			202, channel1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, channelResource.getChannelHttpResponse(channel1.getId()));

		channel1 = testDeleteChannelBatch_addChannel();

		testDeleteChannelBatch_deleteChannel(202, null, channel1.getId());

		assertHttpResponseStatusCode(
			404, channelResource.getChannelHttpResponse(channel1.getId()));

		channel1 = testDeleteChannelBatch_addChannel();
		Channel channel2 = testDeleteChannelBatch_addChannel();

		testDeleteChannelBatch_deleteChannel(
			202, channel2.getExternalReferenceCode(), channel1.getId());

		assertHttpResponseStatusCode(
			404, channelResource.getChannelHttpResponse(channel1.getId()));
		assertHttpResponseStatusCode(
			200, channelResource.getChannelHttpResponse(channel2.getId()));

		testDeleteChannelBatch_deleteChannel(
			202, channel2.getExternalReferenceCode(), channel1.getId());

		assertHttpResponseStatusCode(
			404, channelResource.getChannelHttpResponse(channel2.getId()));
	}

	protected Channel testDeleteChannelBatch_addChannel() throws Exception {
		return testDeleteChannel_addChannel();
	}

	protected void testDeleteChannelBatch_deleteChannel(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			channelResource.deleteChannelBatchHttpResponse(
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
	public void testDeleteChannelByExternalReferenceCode() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Channel channel = testDeleteChannelByExternalReferenceCode_addChannel();

		assertHttpResponseStatusCode(
			204,
			channelResource.deleteChannelByExternalReferenceCodeHttpResponse(
				channel.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			channelResource.getChannelByExternalReferenceCodeHttpResponse(
				channel.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			channelResource.getChannelByExternalReferenceCodeHttpResponse("-"));
	}

	protected Channel testDeleteChannelByExternalReferenceCode_addChannel()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteChannelByExternalReferenceCode()
		throws Exception {

		// No namespace

		Channel channel1 =
			testGraphQLDeleteChannelByExternalReferenceCode_addChannel();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteChannelByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + channel1.getExternalReferenceCode() +
										"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteChannelByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"channelByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + channel1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminChannel_v1_0

		Channel channel2 =
			testGraphQLDeleteChannelByExternalReferenceCode_addChannel();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminChannel_v1_0",
						new GraphQLField(
							"deleteChannelByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											channel2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminChannel_v1_0",
				"Object/deleteChannelByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminChannel_v1_0",
					new GraphQLField(
						"channelByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + channel2.getExternalReferenceCode() +
										"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Channel
			testGraphQLDeleteChannelByExternalReferenceCode_addChannel()
		throws Exception {

		return testGraphQLChannel_addChannel();
	}

	@Test
	public void testGetAccountAddressChannelChannel() throws Exception {
		Channel postChannel = testGetAccountAddressChannelChannel_addChannel();

		Channel getChannel = channelResource.getAccountAddressChannelChannel(
			testGetAccountAddressChannelChannel_getAccountAddressChannelId());

		assertEquals(postChannel, getChannel);
		assertValid(getChannel);
	}

	protected Channel testGetAccountAddressChannelChannel_addChannel()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetAccountAddressChannelChannel_getAccountAddressChannelId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetAccountAddressChannelChannel() throws Exception {
		Channel channel =
			testGraphQLGetAccountAddressChannelChannel_addChannel();

		// No namespace

		Assert.assertTrue(
			equals(
				channel,
				ChannelSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"accountAddressChannelChannel",
								new HashMap<String, Object>() {
									{
										put(
											"accountAddressChannelId",
											testGraphQLGetAccountAddressChannelChannel_getAccountAddressChannelId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/accountAddressChannelChannel"))));

		// Using the namespace headlessCommerceAdminChannel_v1_0

		Assert.assertTrue(
			equals(
				channel,
				ChannelSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminChannel_v1_0",
								new GraphQLField(
									"accountAddressChannelChannel",
									new HashMap<String, Object>() {
										{
											put(
												"accountAddressChannelId",
												testGraphQLGetAccountAddressChannelChannel_getAccountAddressChannelId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminChannel_v1_0",
						"Object/accountAddressChannelChannel"))));
	}

	protected Long
			testGraphQLGetAccountAddressChannelChannel_getAccountAddressChannelId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetAccountAddressChannelChannelNotFound()
		throws Exception {

		Long irrelevantAccountAddressChannelId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"accountAddressChannelChannel",
						new HashMap<String, Object>() {
							{
								put(
									"accountAddressChannelId",
									irrelevantAccountAddressChannelId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminChannel_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminChannel_v1_0",
						new GraphQLField(
							"accountAddressChannelChannel",
							new HashMap<String, Object>() {
								{
									put(
										"accountAddressChannelId",
										irrelevantAccountAddressChannelId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Channel testGraphQLGetAccountAddressChannelChannel_addChannel()
		throws Exception {

		return testGraphQLChannel_addChannel();
	}

	@Test
	public void testGetChannel() throws Exception {
		Channel postChannel = testGetChannel_addChannel();

		Channel getChannel = channelResource.getChannel(postChannel.getId());

		assertEquals(postChannel, getChannel);
		assertValid(getChannel);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		Channel postChannel = testGetChannel_addChannel();

		Channel getChannel = channelResource.getChannel(postChannel.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.channel.dto.v1_0.Channel"
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

		Object item = vulcanCRUDItemDelegate.getItem(postChannel.getId());

		assertEquals(getChannel, ChannelSerDes.toDTO(item.toString()));
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

	protected Channel testGetChannel_addChannel() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetChannel() throws Exception {
		Channel channel = testGraphQLGetChannel_addChannel();

		// No namespace

		Assert.assertTrue(
			equals(
				channel,
				ChannelSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"channel",
								new HashMap<String, Object>() {
									{
										put("channelId", channel.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/channel"))));

		// Using the namespace headlessCommerceAdminChannel_v1_0

		Assert.assertTrue(
			equals(
				channel,
				ChannelSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminChannel_v1_0",
								new GraphQLField(
									"channel",
									new HashMap<String, Object>() {
										{
											put("channelId", channel.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminChannel_v1_0",
						"Object/channel"))));
	}

	@Test
	public void testGraphQLGetChannelNotFound() throws Exception {
		Long irrelevantChannelId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"channel",
						new HashMap<String, Object>() {
							{
								put("channelId", irrelevantChannelId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminChannel_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminChannel_v1_0",
						new GraphQLField(
							"channel",
							new HashMap<String, Object>() {
								{
									put("channelId", irrelevantChannelId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Channel testGraphQLGetChannel_addChannel() throws Exception {
		return testGraphQLChannel_addChannel();
	}

	@Test
	public void testGetChannelByExternalReferenceCode() throws Exception {
		Channel postChannel =
			testGetChannelByExternalReferenceCode_addChannel();

		Channel getChannel = channelResource.getChannelByExternalReferenceCode(
			postChannel.getExternalReferenceCode());

		assertEquals(postChannel, getChannel);
		assertValid(getChannel);
	}

	protected Channel testGetChannelByExternalReferenceCode_addChannel()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetChannelByExternalReferenceCode()
		throws Exception {

		Channel channel =
			testGraphQLGetChannelByExternalReferenceCode_addChannel();

		// No namespace

		Assert.assertTrue(
			equals(
				channel,
				ChannelSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"channelByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												channel.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/channelByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminChannel_v1_0

		Assert.assertTrue(
			equals(
				channel,
				ChannelSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminChannel_v1_0",
								new GraphQLField(
									"channelByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													channel.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminChannel_v1_0",
						"Object/channelByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetChannelByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"channelByExternalReferenceCode",
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

		// Using the namespace headlessCommerceAdminChannel_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminChannel_v1_0",
						new GraphQLField(
							"channelByExternalReferenceCode",
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

	protected Channel testGraphQLGetChannelByExternalReferenceCode_addChannel()
		throws Exception {

		return testGraphQLChannel_addChannel();
	}

	@Test
	public void testGetChannelsPage() throws Exception {
		Page<Channel> page = channelResource.getChannelsPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		Channel channel1 = testGetChannelsPage_addChannel(randomChannel());

		Channel channel2 = testGetChannelsPage_addChannel(randomChannel());

		page = channelResource.getChannelsPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(channel1, (List<Channel>)page.getItems());
		assertContains(channel2, (List<Channel>)page.getItems());
		assertValid(page, testGetChannelsPage_getExpectedActions());

		channelResource.deleteChannel(channel1.getId());

		channelResource.deleteChannel(channel2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetChannelsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelsPageWithFilterDateTimeEquals() throws Exception {
		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Channel channel1 = randomChannel();

		channel1 = testGetChannelsPage_addChannel(channel1);

		for (EntityField entityField : entityFields) {
			Page<Channel> page = channelResource.getChannelsPage(
				null, getFilterString(entityField, "between", channel1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(channel1),
				(List<Channel>)page.getItems());
		}
	}

	@Test
	public void testGetChannelsPageWithFilterDoubleEquals() throws Exception {
		testGetChannelsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetChannelsPageWithFilterStringContains() throws Exception {
		testGetChannelsPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelsPageWithFilterStringEquals() throws Exception {
		testGetChannelsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelsPageWithFilterStringStartsWith()
		throws Exception {

		testGetChannelsPageWithFilter("startswith", EntityField.Type.STRING);
	}

	protected void testGetChannelsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Channel channel1 = testGetChannelsPage_addChannel(randomChannel());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Channel channel2 = testGetChannelsPage_addChannel(randomChannel());

		for (EntityField entityField : entityFields) {
			Page<Channel> page = channelResource.getChannelsPage(
				null, getFilterString(entityField, operator, channel1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(channel1),
				(List<Channel>)page.getItems());
		}
	}

	@Test
	public void testGetChannelsPageWithPagination() throws Exception {
		Page<Channel> channelsPage = channelResource.getChannelsPage(
			null, null, null, null);

		int totalCount = GetterUtil.getInteger(channelsPage.getTotalCount());

		Channel channel1 = testGetChannelsPage_addChannel(randomChannel());

		Channel channel2 = testGetChannelsPage_addChannel(randomChannel());

		Channel channel3 = testGetChannelsPage_addChannel(randomChannel());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Channel> page1 = channelResource.getChannelsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(channel1, (List<Channel>)page1.getItems());

			Page<Channel> page2 = channelResource.getChannelsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(channel2, (List<Channel>)page2.getItems());

			Page<Channel> page3 = channelResource.getChannelsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(channel3, (List<Channel>)page3.getItems());
		}
		else {
			Page<Channel> page1 = channelResource.getChannelsPage(
				null, null, Pagination.of(1, totalCount + 2), null);

			List<Channel> channels1 = (List<Channel>)page1.getItems();

			Assert.assertEquals(
				channels1.toString(), totalCount + 2, channels1.size());

			Page<Channel> page2 = channelResource.getChannelsPage(
				null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Channel> channels2 = (List<Channel>)page2.getItems();

			Assert.assertEquals(channels2.toString(), 1, channels2.size());

			Page<Channel> page3 = channelResource.getChannelsPage(
				null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(channel1, (List<Channel>)page3.getItems());
			assertContains(channel2, (List<Channel>)page3.getItems());
			assertContains(channel3, (List<Channel>)page3.getItems());
		}
	}

	@Test
	public void testGetChannelsPageWithSortDateTime() throws Exception {
		testGetChannelsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, channel1, channel2) -> {
				BeanTestUtil.setProperty(
					channel1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetChannelsPageWithSortDouble() throws Exception {
		testGetChannelsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, channel1, channel2) -> {
				BeanTestUtil.setProperty(channel1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(channel2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetChannelsPageWithSortInteger() throws Exception {
		testGetChannelsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, channel1, channel2) -> {
				BeanTestUtil.setProperty(channel1, entityField.getName(), 0);
				BeanTestUtil.setProperty(channel2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetChannelsPageWithSortString() throws Exception {
		testGetChannelsPageWithSort(
			EntityField.Type.STRING,
			(entityField, channel1, channel2) -> {
				Class<?> clazz = channel1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						channel1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						channel2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						channel1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						channel2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						channel1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						channel2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetChannelsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Channel, Channel, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Channel channel1 = randomChannel();
		Channel channel2 = randomChannel();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, channel1, channel2);
		}

		channel1 = testGetChannelsPage_addChannel(channel1);

		channel2 = testGetChannelsPage_addChannel(channel2);

		Page<Channel> page = channelResource.getChannelsPage(
			null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Channel> ascPage = channelResource.getChannelsPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(channel1, (List<Channel>)ascPage.getItems());
			assertContains(channel2, (List<Channel>)ascPage.getItems());

			Page<Channel> descPage = channelResource.getChannelsPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(channel2, (List<Channel>)descPage.getItems());
			assertContains(channel1, (List<Channel>)descPage.getItems());
		}
	}

	protected Channel testGetChannelsPage_addChannel(Channel channel)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetChannelsPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"channels",
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

		JSONObject channelsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/channels");

		long totalCount = channelsJSONObject.getLong("totalCount");

		Channel channel1 = testGraphQLChannel_addChannel(randomChannel());

		Channel channel2 = testGraphQLChannel_addChannel(randomChannel());

		channelsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/channels");

		Assert.assertEquals(
			totalCount + 2, channelsJSONObject.getLong("totalCount"));

		assertContains(
			channel1,
			Arrays.asList(
				ChannelSerDes.toDTOs(channelsJSONObject.getString("items"))));
		assertContains(
			channel2,
			Arrays.asList(
				ChannelSerDes.toDTOs(channelsJSONObject.getString("items"))));

		// Using the namespace headlessCommerceAdminChannel_v1_0

		channelsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminChannel_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessCommerceAdminChannel_v1_0",
			"JSONObject/channels");

		Assert.assertEquals(
			totalCount + 2, channelsJSONObject.getLong("totalCount"));

		assertContains(
			channel1,
			Arrays.asList(
				ChannelSerDes.toDTOs(channelsJSONObject.getString("items"))));
		assertContains(
			channel2,
			Arrays.asList(
				ChannelSerDes.toDTOs(channelsJSONObject.getString("items"))));
	}

	@Test
	public void testPatchChannel() throws Exception {
		Channel postChannel = testPatchChannel_addChannel();

		Channel randomPatchChannel = randomPatchChannel();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Channel patchChannel = channelResource.patchChannel(
			postChannel.getId(), randomPatchChannel);

		Channel expectedPatchChannel = postChannel.clone();

		BeanTestUtil.copyProperties(randomPatchChannel, expectedPatchChannel);

		Channel getChannel = channelResource.getChannel(patchChannel.getId());

		assertEquals(expectedPatchChannel, getChannel);
		assertValid(getChannel);
	}

	protected Channel testPatchChannel_addChannel() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchChannelByExternalReferenceCode() throws Exception {
		Channel postChannel =
			testPatchChannelByExternalReferenceCode_addChannel();

		Channel randomPatchChannel = randomPatchChannel();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Channel patchChannel =
			channelResource.patchChannelByExternalReferenceCode(
				postChannel.getExternalReferenceCode(), randomPatchChannel);

		Channel expectedPatchChannel = postChannel.clone();

		BeanTestUtil.copyProperties(randomPatchChannel, expectedPatchChannel);

		Channel getChannel = channelResource.getChannelByExternalReferenceCode(
			patchChannel.getExternalReferenceCode());

		assertEquals(expectedPatchChannel, getChannel);
		assertValid(getChannel);
	}

	protected Channel testPatchChannelByExternalReferenceCode_addChannel()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostChannel() throws Exception {
		Channel randomChannel = randomChannel();

		Channel postChannel = testPostChannel_addChannel(randomChannel);

		assertEquals(randomChannel, postChannel);
		assertValid(postChannel);
	}

	protected Channel testPostChannel_addChannel(Channel channel)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostChannel() throws Exception {
		Channel randomChannel = randomChannel();

		Channel channel = testGraphQLChannel_addChannel(randomChannel);

		Assert.assertTrue(equals(randomChannel, channel));
	}

	@Test
	public void testPutChannel() throws Exception {
		Channel postChannel = testPutChannel_addChannel();

		Channel randomChannel = randomChannel();

		Channel putChannel = channelResource.putChannel(
			postChannel.getId(), randomChannel);

		assertEquals(randomChannel, putChannel);
		assertValid(putChannel);

		Channel getChannel = channelResource.getChannel(putChannel.getId());

		assertEquals(randomChannel, getChannel);
		assertValid(getChannel);
	}

	protected Channel testPutChannel_addChannel() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutChannelByExternalReferenceCode() throws Exception {
		Channel postChannel =
			testPutChannelByExternalReferenceCode_addChannel();

		Channel randomChannel = randomChannel();

		Channel putChannel = channelResource.putChannelByExternalReferenceCode(
			postChannel.getExternalReferenceCode(), randomChannel);

		assertEquals(randomChannel, putChannel);
		assertValid(putChannel);

		Channel getChannel = channelResource.getChannelByExternalReferenceCode(
			putChannel.getExternalReferenceCode());

		assertEquals(randomChannel, getChannel);
		assertValid(getChannel);

		Channel newChannel =
			testPutChannelByExternalReferenceCode_createChannel();

		putChannel = channelResource.putChannelByExternalReferenceCode(
			newChannel.getExternalReferenceCode(), newChannel);

		assertEquals(newChannel, putChannel);
		assertValid(putChannel);

		getChannel = channelResource.getChannelByExternalReferenceCode(
			putChannel.getExternalReferenceCode());

		assertEquals(newChannel, getChannel);

		Assert.assertEquals(
			newChannel.getExternalReferenceCode(),
			putChannel.getExternalReferenceCode());
	}

	protected Channel testPutChannelByExternalReferenceCode_addChannel()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Channel testPutChannelByExternalReferenceCode_createChannel()
		throws Exception {

		return randomChannel();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Channel channel1 = testBatchEngineDeleteImportTask_addChannel();

		testBatchEngineDeleteImportTask_deleteChannel(
			200, channel1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, channelResource.getChannelHttpResponse(channel1.getId()));

		channel1 = testBatchEngineDeleteImportTask_addChannel();

		testBatchEngineDeleteImportTask_deleteChannel(
			200, null, channel1.getId());

		assertHttpResponseStatusCode(
			404, channelResource.getChannelHttpResponse(channel1.getId()));

		channel1 = testBatchEngineDeleteImportTask_addChannel();
		Channel channel2 = testBatchEngineDeleteImportTask_addChannel();

		testBatchEngineDeleteImportTask_deleteChannel(
			200, channel2.getExternalReferenceCode(), channel1.getId());

		assertHttpResponseStatusCode(
			404, channelResource.getChannelHttpResponse(channel1.getId()));
		assertHttpResponseStatusCode(
			200, channelResource.getChannelHttpResponse(channel2.getId()));

		testBatchEngineDeleteImportTask_deleteChannel(
			200, channel2.getExternalReferenceCode(), channel1.getId());

		assertHttpResponseStatusCode(
			404, channelResource.getChannelHttpResponse(channel2.getId()));
	}

	protected Channel testBatchEngineDeleteImportTask_addChannel()
		throws Exception {

		return testDeleteChannel_addChannel();
	}

	protected void testBatchEngineDeleteImportTask_deleteChannel(
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
				"com.liferay.headless.commerce.admin.channel.dto.v1_0.Channel",
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

	protected Channel testGraphQLChannel_addChannel() throws Exception {
		return testGraphQLChannel_addChannel(randomChannel());
	}

	protected Channel testGraphQLChannel_addChannel(Channel channel)
		throws Exception {

		JSONDeserializer<Channel> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field : getDeclaredFields(Channel.class)) {
			if (getGraphQLValue(field.get(channel)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(channel)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createChannel",
						new HashMap<String, Object>() {
							{
								put("channel", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createChannel"),
			Channel.class);
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

	protected void assertContains(Channel channel, List<Channel> channels) {
		boolean contains = false;

		for (Channel item : channels) {
			if (equals(channel, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(channels + " does not contain " + channel, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Channel channel1, Channel channel2) {
		Assert.assertTrue(
			channel1 + " does not equal " + channel2,
			equals(channel1, channel2));
	}

	protected void assertEquals(
		List<Channel> channels1, List<Channel> channels2) {

		Assert.assertEquals(channels1.size(), channels2.size());

		for (int i = 0; i < channels1.size(); i++) {
			Channel channel1 = channels1.get(i);
			Channel channel2 = channels2.get(i);

			assertEquals(channel1, channel2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Channel> channels1, List<Channel> channels2) {

		Assert.assertEquals(channels1.size(), channels2.size());

		for (Channel channel1 : channels1) {
			boolean contains = false;

			for (Channel channel2 : channels2) {
				if (equals(channel1, channel2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				channels2 + " does not contain " + channel1, contains);
		}
	}

	protected void assertValid(Channel channel) throws Exception {
		boolean valid = true;

		if (channel.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"accountExternalReferenceCode",
					additionalAssertFieldName)) {

				if (channel.getAccountExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (channel.getAccountId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("currencyCode", additionalAssertFieldName)) {
				if (channel.getCurrencyCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"currencyExternalReferenceCode",
					additionalAssertFieldName)) {

				if (channel.getCurrencyExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("currencyId", additionalAssertFieldName)) {
				if (channel.getCurrencyId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (channel.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (channel.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("siteGroupId", additionalAssertFieldName)) {
				if (channel.getSiteGroupId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (channel.getType() == null) {
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

	protected void assertValid(Page<Channel> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Channel> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Channel> channels = page.getItems();

		int size = channels.size();

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
					com.liferay.headless.commerce.admin.channel.dto.v1_0.
						Channel.class)) {

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

	protected boolean equals(Channel channel1, Channel channel2) {
		if (channel1 == channel2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"accountExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						channel1.getAccountExternalReferenceCode(),
						channel2.getAccountExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						channel1.getAccountId(), channel2.getAccountId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("currencyCode", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						channel1.getCurrencyCode(),
						channel2.getCurrencyCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"currencyExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						channel1.getCurrencyExternalReferenceCode(),
						channel2.getCurrencyExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("currencyId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						channel1.getCurrencyId(), channel2.getCurrencyId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						channel1.getExternalReferenceCode(),
						channel2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(channel1.getId(), channel2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						channel1.getName(), channel2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("siteGroupId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						channel1.getSiteGroupId(), channel2.getSiteGroupId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						channel1.getType(), channel2.getType())) {

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

		if (!(_channelResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_channelResource;

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
		EntityField entityField, String operator, Channel channel) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("accountExternalReferenceCode")) {
			Object object = channel.getAccountExternalReferenceCode();

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

		if (entityFieldName.equals("accountId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("currencyCode")) {
			Object object = channel.getCurrencyCode();

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
			Object object = channel.getCurrencyExternalReferenceCode();

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

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = channel.getExternalReferenceCode();

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
			Object object = channel.getName();

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

		if (entityFieldName.equals("siteGroupId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("type")) {
			Object object = channel.getType();

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

	protected Channel randomChannel() throws Exception {
		return new Channel() {
			{
				accountExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				accountId = RandomTestUtil.randomLong();
				currencyCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				currencyExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				currencyId = RandomTestUtil.randomLong();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				siteGroupId = RandomTestUtil.randomLong();
				type = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected Channel randomIrrelevantChannel() throws Exception {
		Channel randomIrrelevantChannel = randomChannel();

		return randomIrrelevantChannel;
	}

	protected Channel randomPatchChannel() throws Exception {
		return randomChannel();
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

	protected ChannelResource channelResource;
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
		LogFactoryUtil.getLog(BaseChannelResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.commerce.admin.channel.resource.v1_0.
			ChannelResource _channelResource;

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