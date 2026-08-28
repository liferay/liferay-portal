/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.rest.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

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
import com.liferay.portal.kernel.test.util.JAXRSWhiteboardTestUtil;
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
import com.liferay.portal.security.audit.rest.client.dto.v1_0.AuditEvent;
import com.liferay.portal.security.audit.rest.client.http.HttpInvoker;
import com.liferay.portal.security.audit.rest.client.pagination.Page;
import com.liferay.portal.security.audit.rest.client.pagination.Pagination;
import com.liferay.portal.security.audit.rest.client.resource.v1_0.AuditEventResource;
import com.liferay.portal.security.audit.rest.client.serdes.v1_0.AuditEventSerDes;
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
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public abstract class BaseAuditEventResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		JAXRSWhiteboardTestUtil.ensureReady();
	}

	@Before
	public void setUp() throws Exception {
		irrelevantGroup = GroupTestUtil.addGroup();
		testGroup = GroupTestUtil.addGroup();

		testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		_auditEventResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		auditEventResource = AuditEventResource.builder(
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

		AuditEvent auditEvent1 = randomAuditEvent();

		String json = objectMapper.writeValueAsString(auditEvent1);

		AuditEvent auditEvent2 = AuditEventSerDes.toDTO(json);

		Assert.assertTrue(equals(auditEvent1, auditEvent2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		AuditEvent auditEvent = randomAuditEvent();

		String json1 = objectMapper.writeValueAsString(auditEvent);
		String json2 = AuditEventSerDes.toJSON(auditEvent);

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

		AuditEvent auditEvent = randomAuditEvent();

		auditEvent.setClientHost(regex);
		auditEvent.setClientIP(regex);
		auditEvent.setContextName(regex);
		auditEvent.setCorrelationId(regex);
		auditEvent.setEntityType(regex);
		auditEvent.setEventType(regex);
		auditEvent.setHttpMethod(regex);
		auditEvent.setObjectName(regex);
		auditEvent.setRequestId(regex);
		auditEvent.setResourceAction(regex);
		auditEvent.setResourceType(regex);
		auditEvent.setRoles(regex);
		auditEvent.setServerName(regex);
		auditEvent.setUserAgent(regex);

		String json = AuditEventSerDes.toJSON(auditEvent);

		Assert.assertFalse(json.contains(regex));

		auditEvent = AuditEventSerDes.toDTO(json);

		Assert.assertEquals(regex, auditEvent.getClientHost());
		Assert.assertEquals(regex, auditEvent.getClientIP());
		Assert.assertEquals(regex, auditEvent.getContextName());
		Assert.assertEquals(regex, auditEvent.getCorrelationId());
		Assert.assertEquals(regex, auditEvent.getEntityType());
		Assert.assertEquals(regex, auditEvent.getEventType());
		Assert.assertEquals(regex, auditEvent.getHttpMethod());
		Assert.assertEquals(regex, auditEvent.getObjectName());
		Assert.assertEquals(regex, auditEvent.getRequestId());
		Assert.assertEquals(regex, auditEvent.getResourceAction());
		Assert.assertEquals(regex, auditEvent.getResourceType());
		Assert.assertEquals(regex, auditEvent.getRoles());
		Assert.assertEquals(regex, auditEvent.getServerName());
		Assert.assertEquals(regex, auditEvent.getUserAgent());
	}

	@Test
	public void testGetAuditEventsPage() throws Exception {
		Page<AuditEvent> page = auditEventResource.getAuditEventsPage(
			null, RandomTestUtil.randomString(), RandomTestUtil.nextDate(),
			RandomTestUtil.randomString(), RandomTestUtil.nextDate(),
			Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		AuditEvent auditEvent1 = testGetAuditEventsPage_addAuditEvent(
			randomAuditEvent());

		AuditEvent auditEvent2 = testGetAuditEventsPage_addAuditEvent(
			randomAuditEvent());

		page = auditEventResource.getAuditEventsPage(
			null, null, null, null, null, Pagination.of(1, (int)totalCount + 2),
			null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(auditEvent1, (List<AuditEvent>)page.getItems());
		assertContains(auditEvent2, (List<AuditEvent>)page.getItems());
		assertValid(page, testGetAuditEventsPage_getExpectedActions());
	}

	protected Map<String, Map<String, String>>
			testGetAuditEventsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAuditEventsPageWithPagination() throws Exception {
		Page<AuditEvent> auditEventsPage =
			auditEventResource.getAuditEventsPage(
				null, null, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(auditEventsPage.getTotalCount());

		AuditEvent auditEvent1 = testGetAuditEventsPage_addAuditEvent(
			randomAuditEvent());

		AuditEvent auditEvent2 = testGetAuditEventsPage_addAuditEvent(
			randomAuditEvent());

		AuditEvent auditEvent3 = testGetAuditEventsPage_addAuditEvent(
			randomAuditEvent());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<AuditEvent> page1 = auditEventResource.getAuditEventsPage(
				null, null, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(auditEvent1, (List<AuditEvent>)page1.getItems());

			Page<AuditEvent> page2 = auditEventResource.getAuditEventsPage(
				null, null, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(auditEvent2, (List<AuditEvent>)page2.getItems());

			Page<AuditEvent> page3 = auditEventResource.getAuditEventsPage(
				null, null, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(auditEvent3, (List<AuditEvent>)page3.getItems());
		}
		else {
			Page<AuditEvent> page1 = auditEventResource.getAuditEventsPage(
				null, null, null, null, null, Pagination.of(1, totalCount + 2),
				null);

			List<AuditEvent> auditEvents1 = (List<AuditEvent>)page1.getItems();

			Assert.assertEquals(
				auditEvents1.toString(), totalCount + 2, auditEvents1.size());

			Page<AuditEvent> page2 = auditEventResource.getAuditEventsPage(
				null, null, null, null, null, Pagination.of(2, totalCount + 2),
				null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<AuditEvent> auditEvents2 = (List<AuditEvent>)page2.getItems();

			Assert.assertEquals(
				auditEvents2.toString(), 1, auditEvents2.size());

			Page<AuditEvent> page3 = auditEventResource.getAuditEventsPage(
				null, null, null, null, null,
				Pagination.of(1, (int)totalCount + 3), null);

			assertContains(auditEvent1, (List<AuditEvent>)page3.getItems());
			assertContains(auditEvent2, (List<AuditEvent>)page3.getItems());
			assertContains(auditEvent3, (List<AuditEvent>)page3.getItems());
		}
	}

	@Test
	public void testGetAuditEventsPageWithSortDateTime() throws Exception {
		testGetAuditEventsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, auditEvent1, auditEvent2) -> {
				BeanTestUtil.setProperty(
					auditEvent1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAuditEventsPageWithSortDouble() throws Exception {
		testGetAuditEventsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, auditEvent1, auditEvent2) -> {
				BeanTestUtil.setProperty(
					auditEvent1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					auditEvent2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAuditEventsPageWithSortInteger() throws Exception {
		testGetAuditEventsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, auditEvent1, auditEvent2) -> {
				BeanTestUtil.setProperty(auditEvent1, entityField.getName(), 0);
				BeanTestUtil.setProperty(auditEvent2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAuditEventsPageWithSortString() throws Exception {
		testGetAuditEventsPageWithSort(
			EntityField.Type.STRING,
			(entityField, auditEvent1, auditEvent2) -> {
				Class<?> clazz = auditEvent1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						auditEvent1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						auditEvent2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						auditEvent1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						auditEvent2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						auditEvent1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						auditEvent2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetAuditEventsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, AuditEvent, AuditEvent, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		AuditEvent auditEvent1 = randomAuditEvent();
		AuditEvent auditEvent2 = randomAuditEvent();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, auditEvent1, auditEvent2);
		}

		auditEvent1 = testGetAuditEventsPage_addAuditEvent(auditEvent1);

		auditEvent2 = testGetAuditEventsPage_addAuditEvent(auditEvent2);

		Page<AuditEvent> page = auditEventResource.getAuditEventsPage(
			null, null, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<AuditEvent> ascPage = auditEventResource.getAuditEventsPage(
				null, null, null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(auditEvent1, (List<AuditEvent>)ascPage.getItems());
			assertContains(auditEvent2, (List<AuditEvent>)ascPage.getItems());

			Page<AuditEvent> descPage = auditEventResource.getAuditEventsPage(
				null, null, null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(auditEvent2, (List<AuditEvent>)descPage.getItems());
			assertContains(auditEvent1, (List<AuditEvent>)descPage.getItems());
		}
	}

	protected AuditEvent testGetAuditEventsPage_addAuditEvent(
			AuditEvent auditEvent)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		AuditEvent auditEvent, List<AuditEvent> auditEvents) {

		boolean contains = false;

		for (AuditEvent item : auditEvents) {
			if (equals(auditEvent, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			auditEvents + " does not contain " + auditEvent, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		AuditEvent auditEvent1, AuditEvent auditEvent2) {

		Assert.assertTrue(
			auditEvent1 + " does not equal " + auditEvent2,
			equals(auditEvent1, auditEvent2));
	}

	protected void assertEquals(
		List<AuditEvent> auditEvents1, List<AuditEvent> auditEvents2) {

		Assert.assertEquals(auditEvents1.size(), auditEvents2.size());

		for (int i = 0; i < auditEvents1.size(); i++) {
			AuditEvent auditEvent1 = auditEvents1.get(i);
			AuditEvent auditEvent2 = auditEvents2.get(i);

			assertEquals(auditEvent1, auditEvent2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<AuditEvent> auditEvents1, List<AuditEvent> auditEvents2) {

		Assert.assertEquals(auditEvents1.size(), auditEvents2.size());

		for (AuditEvent auditEvent1 : auditEvents1) {
			boolean contains = false;

			for (AuditEvent auditEvent2 : auditEvents2) {
				if (equals(auditEvent1, auditEvent2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				auditEvents2 + " does not contain " + auditEvent1, contains);
		}
	}

	protected void assertValid(AuditEvent auditEvent) throws Exception {
		boolean valid = true;

		if (auditEvent.getDateCreated() == null) {
			valid = false;
		}

		if (auditEvent.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (auditEvent.getAccountId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("additionalInfo", additionalAssertFieldName)) {
				if (auditEvent.getAdditionalInfo() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("clientHost", additionalAssertFieldName)) {
				if (auditEvent.getClientHost() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("clientIP", additionalAssertFieldName)) {
				if (auditEvent.getClientIP() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("contextName", additionalAssertFieldName)) {
				if (auditEvent.getContextName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("correlationId", additionalAssertFieldName)) {
				if (auditEvent.getCorrelationId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (auditEvent.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("entityId", additionalAssertFieldName)) {
				if (auditEvent.getEntityId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("entityType", additionalAssertFieldName)) {
				if (auditEvent.getEntityType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("eventType", additionalAssertFieldName)) {
				if (auditEvent.getEventType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("groupId", additionalAssertFieldName)) {
				if (auditEvent.getGroupId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("httpMethod", additionalAssertFieldName)) {
				if (auditEvent.getHttpMethod() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("objectName", additionalAssertFieldName)) {
				if (auditEvent.getObjectName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("requestId", additionalAssertFieldName)) {
				if (auditEvent.getRequestId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"requestIdGenerated", additionalAssertFieldName)) {

				if (auditEvent.getRequestIdGenerated() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("resourceAction", additionalAssertFieldName)) {
				if (auditEvent.getResourceAction() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("resourceType", additionalAssertFieldName)) {
				if (auditEvent.getResourceType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("roles", additionalAssertFieldName)) {
				if (auditEvent.getRoles() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("serverName", additionalAssertFieldName)) {
				if (auditEvent.getServerName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("userAgent", additionalAssertFieldName)) {
				if (auditEvent.getUserAgent() == null) {
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

	protected void assertValid(Page<AuditEvent> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<AuditEvent> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<AuditEvent> auditEvents = page.getItems();

		int size = auditEvents.size();

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
					com.liferay.portal.security.audit.rest.dto.v1_0.AuditEvent.
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

	protected boolean equals(AuditEvent auditEvent1, AuditEvent auditEvent2) {
		if (auditEvent1 == auditEvent2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						auditEvent1.getAccountId(),
						auditEvent2.getAccountId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("additionalInfo", additionalAssertFieldName)) {
				if (!equals(
						(Map)auditEvent1.getAdditionalInfo(),
						(Map)auditEvent2.getAdditionalInfo())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("clientHost", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						auditEvent1.getClientHost(),
						auditEvent2.getClientHost())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("clientIP", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						auditEvent1.getClientIP(), auditEvent2.getClientIP())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("contextName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						auditEvent1.getContextName(),
						auditEvent2.getContextName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("correlationId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						auditEvent1.getCorrelationId(),
						auditEvent2.getCorrelationId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						auditEvent1.getCreator(), auditEvent2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						auditEvent1.getDateCreated(),
						auditEvent2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("entityId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						auditEvent1.getEntityId(), auditEvent2.getEntityId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("entityType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						auditEvent1.getEntityType(),
						auditEvent2.getEntityType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("eventType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						auditEvent1.getEventType(),
						auditEvent2.getEventType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("groupId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						auditEvent1.getGroupId(), auditEvent2.getGroupId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("httpMethod", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						auditEvent1.getHttpMethod(),
						auditEvent2.getHttpMethod())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						auditEvent1.getId(), auditEvent2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("objectName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						auditEvent1.getObjectName(),
						auditEvent2.getObjectName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("requestId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						auditEvent1.getRequestId(),
						auditEvent2.getRequestId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"requestIdGenerated", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						auditEvent1.getRequestIdGenerated(),
						auditEvent2.getRequestIdGenerated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("resourceAction", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						auditEvent1.getResourceAction(),
						auditEvent2.getResourceAction())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("resourceType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						auditEvent1.getResourceType(),
						auditEvent2.getResourceType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("roles", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						auditEvent1.getRoles(), auditEvent2.getRoles())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("serverName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						auditEvent1.getServerName(),
						auditEvent2.getServerName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("userAgent", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						auditEvent1.getUserAgent(),
						auditEvent2.getUserAgent())) {

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

		if (!(_auditEventResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_auditEventResource;

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
		EntityField entityField, String operator, AuditEvent auditEvent) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("accountId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("additionalInfo")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("clientHost")) {
			Object object = auditEvent.getClientHost();

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

		if (entityFieldName.equals("clientIP")) {
			Object object = auditEvent.getClientIP();

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

		if (entityFieldName.equals("contextName")) {
			Object object = auditEvent.getContextName();

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

		if (entityFieldName.equals("correlationId")) {
			Object object = auditEvent.getCorrelationId();

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

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = auditEvent.getDateCreated();

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

				sb.append(_format.format(auditEvent.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("entityId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("entityType")) {
			Object object = auditEvent.getEntityType();

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

		if (entityFieldName.equals("eventType")) {
			Object object = auditEvent.getEventType();

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

		if (entityFieldName.equals("groupId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("httpMethod")) {
			Object object = auditEvent.getHttpMethod();

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

		if (entityFieldName.equals("objectName")) {
			Object object = auditEvent.getObjectName();

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

		if (entityFieldName.equals("requestId")) {
			Object object = auditEvent.getRequestId();

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

		if (entityFieldName.equals("requestIdGenerated")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("resourceAction")) {
			Object object = auditEvent.getResourceAction();

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

		if (entityFieldName.equals("resourceType")) {
			Object object = auditEvent.getResourceType();

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

		if (entityFieldName.equals("roles")) {
			Object object = auditEvent.getRoles();

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

		if (entityFieldName.equals("serverName")) {
			Object object = auditEvent.getServerName();

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

		if (entityFieldName.equals("userAgent")) {
			Object object = auditEvent.getUserAgent();

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

	protected AuditEvent randomAuditEvent() throws Exception {
		return new AuditEvent() {
			{
				accountId = RandomTestUtil.randomLong();
				clientHost = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				clientIP = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				contextName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				correlationId = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				entityId = RandomTestUtil.randomLong();
				entityType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				eventType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				groupId = RandomTestUtil.randomLong();
				httpMethod = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				objectName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				requestId = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				requestIdGenerated = RandomTestUtil.randomBoolean();
				resourceAction = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				resourceType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				roles = StringUtil.toLowerCase(RandomTestUtil.randomString());
				serverName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				userAgent = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected AuditEvent randomIrrelevantAuditEvent() throws Exception {
		AuditEvent randomIrrelevantAuditEvent = randomAuditEvent();

		return randomIrrelevantAuditEvent;
	}

	protected AuditEvent randomPatchAuditEvent() throws Exception {
		return randomAuditEvent();
	}

	protected AuditEventResource auditEventResource;
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
		LogFactoryUtil.getLog(BaseAuditEventResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.portal.security.audit.rest.resource.v1_0.AuditEventResource
			_auditEventResource;

}
// LIFERAY-REST-BUILDER-HASH:-1955357174