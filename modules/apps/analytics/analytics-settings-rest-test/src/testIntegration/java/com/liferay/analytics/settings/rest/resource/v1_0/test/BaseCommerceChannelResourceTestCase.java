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

import com.liferay.analytics.settings.rest.client.dto.v1_0.CommerceChannel;
import com.liferay.analytics.settings.rest.client.dto.v1_0.Field;
import com.liferay.analytics.settings.rest.client.http.HttpInvoker;
import com.liferay.analytics.settings.rest.client.pagination.Page;
import com.liferay.analytics.settings.rest.client.pagination.Pagination;
import com.liferay.analytics.settings.rest.client.resource.v1_0.CommerceChannelResource;
import com.liferay.analytics.settings.rest.client.serdes.v1_0.CommerceChannelSerDes;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
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
public abstract class BaseCommerceChannelResourceTestCase {

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

		_commerceChannelResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		commerceChannelResource = CommerceChannelResource.builder(
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

		CommerceChannel commerceChannel1 = randomCommerceChannel();

		String json = objectMapper.writeValueAsString(commerceChannel1);

		CommerceChannel commerceChannel2 = CommerceChannelSerDes.toDTO(json);

		Assert.assertTrue(equals(commerceChannel1, commerceChannel2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		CommerceChannel commerceChannel = randomCommerceChannel();

		String json1 = objectMapper.writeValueAsString(commerceChannel);
		String json2 = CommerceChannelSerDes.toJSON(commerceChannel);

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

		CommerceChannel commerceChannel = randomCommerceChannel();

		commerceChannel.setChannelName(regex);
		commerceChannel.setName(regex);
		commerceChannel.setSiteName(regex);

		String json = CommerceChannelSerDes.toJSON(commerceChannel);

		Assert.assertFalse(json.contains(regex));

		commerceChannel = CommerceChannelSerDes.toDTO(json);

		Assert.assertEquals(regex, commerceChannel.getChannelName());
		Assert.assertEquals(regex, commerceChannel.getName());
		Assert.assertEquals(regex, commerceChannel.getSiteName());
	}

	@Test
	public void testGetCommerceChannelsPage() throws Exception {
		Page<CommerceChannel> page =
			commerceChannelResource.getCommerceChannelsPage(
				null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		CommerceChannel commerceChannel1 =
			testGetCommerceChannelsPage_addCommerceChannel(
				randomCommerceChannel());

		CommerceChannel commerceChannel2 =
			testGetCommerceChannelsPage_addCommerceChannel(
				randomCommerceChannel());

		page = commerceChannelResource.getCommerceChannelsPage(
			null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			commerceChannel1, (List<CommerceChannel>)page.getItems());
		assertContains(
			commerceChannel2, (List<CommerceChannel>)page.getItems());
		assertValid(page, testGetCommerceChannelsPage_getExpectedActions());
	}

	protected Map<String, Map<String, String>>
			testGetCommerceChannelsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetCommerceChannelsPageWithPagination() throws Exception {
		Page<CommerceChannel> commerceChannelsPage =
			commerceChannelResource.getCommerceChannelsPage(null, null, null);

		int totalCount = GetterUtil.getInteger(
			commerceChannelsPage.getTotalCount());

		CommerceChannel commerceChannel1 =
			testGetCommerceChannelsPage_addCommerceChannel(
				randomCommerceChannel());

		CommerceChannel commerceChannel2 =
			testGetCommerceChannelsPage_addCommerceChannel(
				randomCommerceChannel());

		CommerceChannel commerceChannel3 =
			testGetCommerceChannelsPage_addCommerceChannel(
				randomCommerceChannel());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<CommerceChannel> page1 =
				commerceChannelResource.getCommerceChannelsPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				commerceChannel1, (List<CommerceChannel>)page1.getItems());

			Page<CommerceChannel> page2 =
				commerceChannelResource.getCommerceChannelsPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				commerceChannel2, (List<CommerceChannel>)page2.getItems());

			Page<CommerceChannel> page3 =
				commerceChannelResource.getCommerceChannelsPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				commerceChannel3, (List<CommerceChannel>)page3.getItems());
		}
		else {
			Page<CommerceChannel> page1 =
				commerceChannelResource.getCommerceChannelsPage(
					null, Pagination.of(1, totalCount + 2), null);

			List<CommerceChannel> commerceChannels1 =
				(List<CommerceChannel>)page1.getItems();

			Assert.assertEquals(
				commerceChannels1.toString(), totalCount + 2,
				commerceChannels1.size());

			Page<CommerceChannel> page2 =
				commerceChannelResource.getCommerceChannelsPage(
					null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<CommerceChannel> commerceChannels2 =
				(List<CommerceChannel>)page2.getItems();

			Assert.assertEquals(
				commerceChannels2.toString(), 1, commerceChannels2.size());

			Page<CommerceChannel> page3 =
				commerceChannelResource.getCommerceChannelsPage(
					null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				commerceChannel1, (List<CommerceChannel>)page3.getItems());
			assertContains(
				commerceChannel2, (List<CommerceChannel>)page3.getItems());
			assertContains(
				commerceChannel3, (List<CommerceChannel>)page3.getItems());
		}
	}

	@Test
	public void testGetCommerceChannelsPageWithSortDateTime() throws Exception {
		testGetCommerceChannelsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, commerceChannel1, commerceChannel2) -> {
				BeanTestUtil.setProperty(
					commerceChannel1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetCommerceChannelsPageWithSortDouble() throws Exception {
		testGetCommerceChannelsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, commerceChannel1, commerceChannel2) -> {
				BeanTestUtil.setProperty(
					commerceChannel1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					commerceChannel2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetCommerceChannelsPageWithSortInteger() throws Exception {
		testGetCommerceChannelsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, commerceChannel1, commerceChannel2) -> {
				BeanTestUtil.setProperty(
					commerceChannel1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					commerceChannel2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetCommerceChannelsPageWithSortString() throws Exception {
		testGetCommerceChannelsPageWithSort(
			EntityField.Type.STRING,
			(entityField, commerceChannel1, commerceChannel2) -> {
				Class<?> clazz = commerceChannel1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						commerceChannel1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						commerceChannel2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						commerceChannel1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						commerceChannel2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						commerceChannel1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						commerceChannel2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetCommerceChannelsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, CommerceChannel, CommerceChannel, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		CommerceChannel commerceChannel1 = randomCommerceChannel();
		CommerceChannel commerceChannel2 = randomCommerceChannel();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, commerceChannel1, commerceChannel2);
		}

		commerceChannel1 = testGetCommerceChannelsPage_addCommerceChannel(
			commerceChannel1);

		commerceChannel2 = testGetCommerceChannelsPage_addCommerceChannel(
			commerceChannel2);

		Page<CommerceChannel> page =
			commerceChannelResource.getCommerceChannelsPage(null, null, null);

		for (EntityField entityField : entityFields) {
			Page<CommerceChannel> ascPage =
				commerceChannelResource.getCommerceChannelsPage(
					null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				commerceChannel1, (List<CommerceChannel>)ascPage.getItems());
			assertContains(
				commerceChannel2, (List<CommerceChannel>)ascPage.getItems());

			Page<CommerceChannel> descPage =
				commerceChannelResource.getCommerceChannelsPage(
					null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				commerceChannel2, (List<CommerceChannel>)descPage.getItems());
			assertContains(
				commerceChannel1, (List<CommerceChannel>)descPage.getItems());
		}
	}

	protected CommerceChannel testGetCommerceChannelsPage_addCommerceChannel(
			CommerceChannel commerceChannel)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		CommerceChannel commerceChannel,
		List<CommerceChannel> commerceChannels) {

		boolean contains = false;

		for (CommerceChannel item : commerceChannels) {
			if (equals(commerceChannel, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			commerceChannels + " does not contain " + commerceChannel,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		CommerceChannel commerceChannel1, CommerceChannel commerceChannel2) {

		Assert.assertTrue(
			commerceChannel1 + " does not equal " + commerceChannel2,
			equals(commerceChannel1, commerceChannel2));
	}

	protected void assertEquals(
		List<CommerceChannel> commerceChannels1,
		List<CommerceChannel> commerceChannels2) {

		Assert.assertEquals(commerceChannels1.size(), commerceChannels2.size());

		for (int i = 0; i < commerceChannels1.size(); i++) {
			CommerceChannel commerceChannel1 = commerceChannels1.get(i);
			CommerceChannel commerceChannel2 = commerceChannels2.get(i);

			assertEquals(commerceChannel1, commerceChannel2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<CommerceChannel> commerceChannels1,
		List<CommerceChannel> commerceChannels2) {

		Assert.assertEquals(commerceChannels1.size(), commerceChannels2.size());

		for (CommerceChannel commerceChannel1 : commerceChannels1) {
			boolean contains = false;

			for (CommerceChannel commerceChannel2 : commerceChannels2) {
				if (equals(commerceChannel1, commerceChannel2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				commerceChannels2 + " does not contain " + commerceChannel1,
				contains);
		}
	}

	protected void assertValid(CommerceChannel commerceChannel)
		throws Exception {

		boolean valid = true;

		if (commerceChannel.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("channelName", additionalAssertFieldName)) {
				if (commerceChannel.getChannelName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (commerceChannel.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("siteName", additionalAssertFieldName)) {
				if (commerceChannel.getSiteName() == null) {
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

	protected void assertValid(Page<CommerceChannel> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<CommerceChannel> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<CommerceChannel> commerceChannels =
			page.getItems();

		int size = commerceChannels.size();

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

		graphQLFields.add(new GraphQLField("id"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.analytics.settings.rest.dto.v1_0.
						CommerceChannel.class)) {

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
		CommerceChannel commerceChannel1, CommerceChannel commerceChannel2) {

		if (commerceChannel1 == commerceChannel2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("channelName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						commerceChannel1.getChannelName(),
						commerceChannel2.getChannelName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						commerceChannel1.getId(), commerceChannel2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						commerceChannel1.getName(),
						commerceChannel2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("siteName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						commerceChannel1.getSiteName(),
						commerceChannel2.getSiteName())) {

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

		if (!(_commerceChannelResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_commerceChannelResource;

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
		CommerceChannel commerceChannel) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("channelName")) {
			Object object = commerceChannel.getChannelName();

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
			Object object = commerceChannel.getName();

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

		if (entityFieldName.equals("siteName")) {
			Object object = commerceChannel.getSiteName();

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

	protected CommerceChannel randomCommerceChannel() throws Exception {
		return new CommerceChannel() {
			{
				channelName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				siteName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected CommerceChannel randomIrrelevantCommerceChannel()
		throws Exception {

		CommerceChannel randomIrrelevantCommerceChannel =
			randomCommerceChannel();

		return randomIrrelevantCommerceChannel;
	}

	protected CommerceChannel randomPatchCommerceChannel() throws Exception {
		return randomCommerceChannel();
	}

	protected CommerceChannelResource commerceChannelResource;
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
		LogFactoryUtil.getLog(BaseCommerceChannelResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.analytics.settings.rest.resource.v1_0.
			CommerceChannelResource _commerceChannelResource;

}