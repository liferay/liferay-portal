/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.analytics.settings.rest.client.dto.v1_0.Channel;
import com.liferay.analytics.settings.rest.client.dto.v1_0.Field;
import com.liferay.analytics.settings.rest.client.http.HttpInvoker;
import com.liferay.analytics.settings.rest.client.pagination.Page;
import com.liferay.analytics.settings.rest.client.pagination.Pagination;
import com.liferay.analytics.settings.rest.client.resource.v1_0.ChannelResource;
import com.liferay.analytics.settings.rest.client.serdes.v1_0.ChannelSerDes;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONDeserializer;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
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
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.annotation.Generated;

import jakarta.ws.rs.core.MultivaluedHashMap;

import java.lang.reflect.Method;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

/**
 * @author Riccardo Ferrari
 * @generated
 */
@Generated("")
public abstract class BaseChannelResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

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

		channel.setChannelId(regex);
		channel.setName(regex);

		String json = ChannelSerDes.toJSON(channel);

		Assert.assertFalse(json.contains(regex));

		channel = ChannelSerDes.toDTO(json);

		Assert.assertEquals(regex, channel.getChannelId());
		Assert.assertEquals(regex, channel.getName());
	}

	@Test
	public void testGetChannelsPage() throws Exception {
		Page<Channel> page = channelResource.getChannelsPage(
			null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		Channel channel1 = testGetChannelsPage_addChannel(randomChannel());

		Channel channel2 = testGetChannelsPage_addChannel(randomChannel());

		page = channelResource.getChannelsPage(
			null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(channel1, (List<Channel>)page.getItems());
		assertContains(channel2, (List<Channel>)page.getItems());
		assertValid(page, testGetChannelsPage_getExpectedActions());
	}

	protected Map<String, Map<String, String>>
			testGetChannelsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelsPageWithPagination() throws Exception {
		Page<Channel> channelsPage = channelResource.getChannelsPage(
			null, null, null);

		int totalCount = GetterUtil.getInteger(channelsPage.getTotalCount());

		Channel channel1 = testGetChannelsPage_addChannel(randomChannel());

		Channel channel2 = testGetChannelsPage_addChannel(randomChannel());

		Channel channel3 = testGetChannelsPage_addChannel(randomChannel());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Channel> page1 = channelResource.getChannelsPage(
				null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(channel1, (List<Channel>)page1.getItems());

			Page<Channel> page2 = channelResource.getChannelsPage(
				null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(channel2, (List<Channel>)page2.getItems());

			Page<Channel> page3 = channelResource.getChannelsPage(
				null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(channel3, (List<Channel>)page3.getItems());
		}
		else {
			Page<Channel> page1 = channelResource.getChannelsPage(
				null, Pagination.of(1, totalCount + 2), null);

			List<Channel> channels1 = (List<Channel>)page1.getItems();

			Assert.assertEquals(
				channels1.toString(), totalCount + 2, channels1.size());

			Page<Channel> page2 = channelResource.getChannelsPage(
				null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Channel> channels2 = (List<Channel>)page2.getItems();

			Assert.assertEquals(channels2.toString(), 1, channels2.size());

			Page<Channel> page3 = channelResource.getChannelsPage(
				null, Pagination.of(1, (int)totalCount + 3), null);

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

		Page<Channel> page = channelResource.getChannelsPage(null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Channel> ascPage = channelResource.getChannelsPage(
				null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(channel1, (List<Channel>)ascPage.getItems());
			assertContains(channel2, (List<Channel>)ascPage.getItems());

			Page<Channel> descPage = channelResource.getChannelsPage(
				null, Pagination.of(1, (int)page.getTotalCount() + 1),
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
					put("keywords", null);
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

		// Using the namespace analyticsSettings_v1_0

		channelsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("analyticsSettings_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/analyticsSettings_v1_0",
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
		Assert.assertTrue(false);
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

		if (channel.getChannelId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("channelId", additionalAssertFieldName)) {
				if (channel.getChannelId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"commerceSyncEnabled", additionalAssertFieldName)) {

				if (channel.getCommerceSyncEnabled() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (channel.getCreateDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("dataSources", additionalAssertFieldName)) {
				if (channel.getDataSources() == null) {
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

		graphQLFields.add(new GraphQLField("channelId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.analytics.settings.rest.dto.v1_0.Channel.
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

	protected boolean equals(Channel channel1, Channel channel2) {
		if (channel1 == channel2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("channelId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						channel1.getChannelId(), channel2.getChannelId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"commerceSyncEnabled", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						channel1.getCommerceSyncEnabled(),
						channel2.getCommerceSyncEnabled())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						channel1.getCreateDate(), channel2.getCreateDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dataSources", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						channel1.getDataSources(), channel2.getDataSources())) {

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

		if (entityFieldName.equals("channelId")) {
			Object object = channel.getChannelId();

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

		if (entityFieldName.equals("commerceSyncEnabled")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("createDate")) {
			if (operator.equals("between")) {
				Date date = channel.getCreateDate();

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

				sb.append(_format.format(channel.getCreateDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dataSources")) {
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
				channelId = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				commerceSyncEnabled = RandomTestUtil.randomBoolean();
				createDate = RandomTestUtil.nextDate();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
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

	protected ChannelResource channelResource;
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
	private com.liferay.analytics.settings.rest.resource.v1_0.ChannelResource
		_channelResource;

}