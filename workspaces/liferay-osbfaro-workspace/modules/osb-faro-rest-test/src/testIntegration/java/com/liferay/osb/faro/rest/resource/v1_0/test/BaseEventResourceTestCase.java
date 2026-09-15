/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.osb.faro.rest.client.dto.v1_0.Event;
import com.liferay.osb.faro.rest.client.http.HttpInvoker;
import com.liferay.osb.faro.rest.client.pagination.Page;
import com.liferay.osb.faro.rest.client.pagination.Pagination;
import com.liferay.osb.faro.rest.client.resource.v1_0.EventResource;
import com.liferay.osb.faro.rest.client.serdes.v1_0.EventSerDes;
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
import com.liferay.portal.kernel.util.PortalUtil;
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
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public abstract class BaseEventResourceTestCase {

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

		_eventResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		eventResource = EventResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
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

		Event event1 = randomEvent();

		String json = objectMapper.writeValueAsString(event1);

		Event event2 = EventSerDes.toDTO(json);

		Assert.assertTrue(equals(event1, event2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Event event = randomEvent();

		String json1 = objectMapper.writeValueAsString(event);
		String json2 = EventSerDes.toJSON(event);

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

		Event event = randomEvent();

		event.setApplicationId(regex);
		event.setAssetTitle(regex);
		event.setCanonicalUrl(regex);
		event.setIndividualId(regex);
		event.setName(regex);
		event.setPageDescription(regex);
		event.setPageKeywords(regex);
		event.setPageTitle(regex);
		event.setReferrer(regex);
		event.setUrl(regex);

		String json = EventSerDes.toJSON(event);

		Assert.assertFalse(json.contains(regex));

		event = EventSerDes.toDTO(json);

		Assert.assertEquals(regex, event.getApplicationId());
		Assert.assertEquals(regex, event.getAssetTitle());
		Assert.assertEquals(regex, event.getCanonicalUrl());
		Assert.assertEquals(regex, event.getIndividualId());
		Assert.assertEquals(regex, event.getName());
		Assert.assertEquals(regex, event.getPageDescription());
		Assert.assertEquals(regex, event.getPageKeywords());
		Assert.assertEquals(regex, event.getPageTitle());
		Assert.assertEquals(regex, event.getReferrer());
		Assert.assertEquals(regex, event.getUrl());
	}

	@Test
	public void testGetWorkspaceGroupChannelEventsPage() throws Exception {
		Long groupId = testGetWorkspaceGroupChannelEventsPage_getGroupId();
		Long irrelevantGroupId =
			testGetWorkspaceGroupChannelEventsPage_getIrrelevantGroupId();
		String channelId =
			testGetWorkspaceGroupChannelEventsPage_getChannelId();
		String irrelevantChannelId =
			testGetWorkspaceGroupChannelEventsPage_getIrrelevantChannelId();

		Page<Event> page = eventResource.getWorkspaceGroupChannelEventsPage(
			groupId, channelId, RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString(), Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if ((irrelevantGroupId != null) && (irrelevantChannelId != null)) {
			Event irrelevantEvent =
				testGetWorkspaceGroupChannelEventsPage_addEvent(
					irrelevantGroupId, irrelevantChannelId,
					randomIrrelevantEvent());

			page = eventResource.getWorkspaceGroupChannelEventsPage(
				irrelevantGroupId, irrelevantChannelId, null, null, null, null,
				null, null, null, null, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantEvent, (List<Event>)page.getItems());
			assertValid(
				page,
				testGetWorkspaceGroupChannelEventsPage_getExpectedActions(
					irrelevantGroupId, irrelevantChannelId));
		}

		Event event1 = testGetWorkspaceGroupChannelEventsPage_addEvent(
			groupId, channelId, randomEvent());

		Event event2 = testGetWorkspaceGroupChannelEventsPage_addEvent(
			groupId, channelId, randomEvent());

		page = eventResource.getWorkspaceGroupChannelEventsPage(
			groupId, channelId, null, null, null, null, null, null, null, null,
			Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(event1, (List<Event>)page.getItems());
		assertContains(event2, (List<Event>)page.getItems());
		assertValid(
			page,
			testGetWorkspaceGroupChannelEventsPage_getExpectedActions(
				groupId, channelId));
	}

	protected Map<String, Map<String, String>>
			testGetWorkspaceGroupChannelEventsPage_getExpectedActions(
				Long groupId, String channelId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWorkspaceGroupChannelEventsPageWithPagination()
		throws Exception {

		Long groupId = testGetWorkspaceGroupChannelEventsPage_getGroupId();
		String channelId =
			testGetWorkspaceGroupChannelEventsPage_getChannelId();

		Page<Event> eventsPage =
			eventResource.getWorkspaceGroupChannelEventsPage(
				groupId, channelId, null, null, null, null, null, null, null,
				null, null);

		int totalCount = GetterUtil.getInteger(eventsPage.getTotalCount());

		Event event1 = testGetWorkspaceGroupChannelEventsPage_addEvent(
			groupId, channelId, randomEvent());

		Event event2 = testGetWorkspaceGroupChannelEventsPage_addEvent(
			groupId, channelId, randomEvent());

		Event event3 = testGetWorkspaceGroupChannelEventsPage_addEvent(
			groupId, channelId, randomEvent());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Event> page1 =
				eventResource.getWorkspaceGroupChannelEventsPage(
					groupId, channelId, null, null, null, null, null, null,
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(event1, (List<Event>)page1.getItems());

			Page<Event> page2 =
				eventResource.getWorkspaceGroupChannelEventsPage(
					groupId, channelId, null, null, null, null, null, null,
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(event2, (List<Event>)page2.getItems());

			Page<Event> page3 =
				eventResource.getWorkspaceGroupChannelEventsPage(
					groupId, channelId, null, null, null, null, null, null,
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(event3, (List<Event>)page3.getItems());
		}
		else {
			Page<Event> page1 =
				eventResource.getWorkspaceGroupChannelEventsPage(
					groupId, channelId, null, null, null, null, null, null,
					null, null, Pagination.of(1, totalCount + 2));

			List<Event> events1 = (List<Event>)page1.getItems();

			Assert.assertEquals(
				events1.toString(), totalCount + 2, events1.size());

			Page<Event> page2 =
				eventResource.getWorkspaceGroupChannelEventsPage(
					groupId, channelId, null, null, null, null, null, null,
					null, null, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Event> events2 = (List<Event>)page2.getItems();

			Assert.assertEquals(events2.toString(), 1, events2.size());

			Page<Event> page3 =
				eventResource.getWorkspaceGroupChannelEventsPage(
					groupId, channelId, null, null, null, null, null, null,
					null, null, Pagination.of(1, (int)totalCount + 3));

			assertContains(event1, (List<Event>)page3.getItems());
			assertContains(event2, (List<Event>)page3.getItems());
			assertContains(event3, (List<Event>)page3.getItems());
		}
	}

	protected Event testGetWorkspaceGroupChannelEventsPage_addEvent(
			Long groupId, String channelId, Event event)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetWorkspaceGroupChannelEventsPage_getGroupId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetWorkspaceGroupChannelEventsPage_getIrrelevantGroupId()
		throws Exception {

		return null;
	}

	protected String testGetWorkspaceGroupChannelEventsPage_getChannelId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetWorkspaceGroupChannelEventsPage_getIrrelevantChannelId()
		throws Exception {

		return null;
	}

	protected void assertContains(Event event, List<Event> events) {
		boolean contains = false;

		for (Event item : events) {
			if (equals(event, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(events + " does not contain " + event, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Event event1, Event event2) {
		Assert.assertTrue(
			event1 + " does not equal " + event2, equals(event1, event2));
	}

	protected void assertEquals(List<Event> events1, List<Event> events2) {
		Assert.assertEquals(events1.size(), events2.size());

		for (int i = 0; i < events1.size(); i++) {
			Event event1 = events1.get(i);
			Event event2 = events2.get(i);

			assertEquals(event1, event2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Event> events1, List<Event> events2) {

		Assert.assertEquals(events1.size(), events2.size());

		for (Event event1 : events1) {
			boolean contains = false;

			for (Event event2 : events2) {
				if (equals(event1, event2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				events2 + " does not contain " + event1, contains);
		}
	}

	protected void assertValid(Event event) throws Exception {
		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("applicationId", additionalAssertFieldName)) {
				if (event.getApplicationId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assetTitle", additionalAssertFieldName)) {
				if (event.getAssetTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("attributes", additionalAssertFieldName)) {
				if (event.getAttributes() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("canonicalUrl", additionalAssertFieldName)) {
				if (event.getCanonicalUrl() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (event.getCreateDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("individualId", additionalAssertFieldName)) {
				if (event.getIndividualId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (event.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("pageDescription", additionalAssertFieldName)) {
				if (event.getPageDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("pageKeywords", additionalAssertFieldName)) {
				if (event.getPageKeywords() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("pageTitle", additionalAssertFieldName)) {
				if (event.getPageTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("referrer", additionalAssertFieldName)) {
				if (event.getReferrer() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("url", additionalAssertFieldName)) {
				if (event.getUrl() == null) {
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

	protected void assertValid(Page<Event> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Event> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Event> events = page.getItems();

		int size = events.size();

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

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.osb.faro.rest.dto.v1_0.Event.class)) {

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

	protected boolean equals(Event event1, Event event2) {
		if (event1 == event2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("applicationId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						event1.getApplicationId(), event2.getApplicationId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("assetTitle", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						event1.getAssetTitle(), event2.getAssetTitle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("attributes", additionalAssertFieldName)) {
				if (!equals(
						(Map)event1.getAttributes(),
						(Map)event2.getAttributes())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("canonicalUrl", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						event1.getCanonicalUrl(), event2.getCanonicalUrl())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						event1.getCreateDate(), event2.getCreateDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("individualId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						event1.getIndividualId(), event2.getIndividualId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(event1.getName(), event2.getName())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("pageDescription", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						event1.getPageDescription(),
						event2.getPageDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("pageKeywords", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						event1.getPageKeywords(), event2.getPageKeywords())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("pageTitle", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						event1.getPageTitle(), event2.getPageTitle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("referrer", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						event1.getReferrer(), event2.getReferrer())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("url", additionalAssertFieldName)) {
				if (!Objects.deepEquals(event1.getUrl(), event2.getUrl())) {
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

		if (!(_eventResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_eventResource;

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
		EntityField entityField, String operator, Event event) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("applicationId")) {
			Object object = event.getApplicationId();

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

		if (entityFieldName.equals("assetTitle")) {
			Object object = event.getAssetTitle();

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

		if (entityFieldName.equals("attributes")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("canonicalUrl")) {
			Object object = event.getCanonicalUrl();

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

		if (entityFieldName.equals("createDate")) {
			if (operator.equals("between")) {
				Date date = event.getCreateDate();

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

				sb.append(_format.format(event.getCreateDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("individualId")) {
			Object object = event.getIndividualId();

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
			Object object = event.getName();

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

		if (entityFieldName.equals("pageDescription")) {
			Object object = event.getPageDescription();

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

		if (entityFieldName.equals("pageKeywords")) {
			Object object = event.getPageKeywords();

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

		if (entityFieldName.equals("pageTitle")) {
			Object object = event.getPageTitle();

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

		if (entityFieldName.equals("referrer")) {
			Object object = event.getReferrer();

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

		if (entityFieldName.equals("url")) {
			Object object = event.getUrl();

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
		httpInvoker.path(
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/o/graphql");
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

	protected Event randomEvent() throws Exception {
		return new Event() {
			{
				applicationId = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				assetTitle = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				canonicalUrl = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				createDate = RandomTestUtil.nextDate();
				individualId = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				pageDescription = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				pageKeywords = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				pageTitle = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				referrer = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				url = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected Event randomIrrelevantEvent() throws Exception {
		Event randomIrrelevantEvent = randomEvent();

		return randomIrrelevantEvent;
	}

	protected Event randomPatchEvent() throws Exception {
		return randomEvent();
	}

	protected EventResource eventResource;
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
		LogFactoryUtil.getLog(BaseEventResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.osb.faro.rest.resource.v1_0.EventResource
		_eventResource;

}
// LIFERAY-REST-BUILDER-HASH:1846481436