/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.rest.resource.v1_0.test;

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
import com.liferay.notification.rest.client.dto.v1_0.NotificationQueueEntry;
import com.liferay.notification.rest.client.http.HttpInvoker;
import com.liferay.notification.rest.client.pagination.Page;
import com.liferay.notification.rest.client.pagination.Pagination;
import com.liferay.notification.rest.client.resource.v1_0.NotificationQueueEntryResource;
import com.liferay.notification.rest.client.serdes.v1_0.NotificationQueueEntrySerDes;
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
 * @author Gabriel Albuquerque
 * @generated
 */
@Generated("")
public abstract class BaseNotificationQueueEntryResourceTestCase {

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

		_notificationQueueEntryResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		notificationQueueEntryResource = NotificationQueueEntryResource.builder(
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

		NotificationQueueEntry notificationQueueEntry1 =
			randomNotificationQueueEntry();

		String json = objectMapper.writeValueAsString(notificationQueueEntry1);

		NotificationQueueEntry notificationQueueEntry2 =
			NotificationQueueEntrySerDes.toDTO(json);

		Assert.assertTrue(
			equals(notificationQueueEntry1, notificationQueueEntry2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		NotificationQueueEntry notificationQueueEntry =
			randomNotificationQueueEntry();

		String json1 = objectMapper.writeValueAsString(notificationQueueEntry);
		String json2 = NotificationQueueEntrySerDes.toJSON(
			notificationQueueEntry);

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

		NotificationQueueEntry notificationQueueEntry =
			randomNotificationQueueEntry();

		notificationQueueEntry.setBody(regex);
		notificationQueueEntry.setFromName(regex);
		notificationQueueEntry.setRecipientsSummary(regex);
		notificationQueueEntry.setSubject(regex);
		notificationQueueEntry.setTriggerBy(regex);
		notificationQueueEntry.setType(regex);
		notificationQueueEntry.setTypeLabel(regex);

		String json = NotificationQueueEntrySerDes.toJSON(
			notificationQueueEntry);

		Assert.assertFalse(json.contains(regex));

		notificationQueueEntry = NotificationQueueEntrySerDes.toDTO(json);

		Assert.assertEquals(regex, notificationQueueEntry.getBody());
		Assert.assertEquals(regex, notificationQueueEntry.getFromName());
		Assert.assertEquals(
			regex, notificationQueueEntry.getRecipientsSummary());
		Assert.assertEquals(regex, notificationQueueEntry.getSubject());
		Assert.assertEquals(regex, notificationQueueEntry.getTriggerBy());
		Assert.assertEquals(regex, notificationQueueEntry.getType());
		Assert.assertEquals(regex, notificationQueueEntry.getTypeLabel());
	}

	@Test
	public void testDeleteNotificationQueueEntry() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		NotificationQueueEntry notificationQueueEntry =
			testDeleteNotificationQueueEntry_addNotificationQueueEntry();

		assertHttpResponseStatusCode(
			204,
			notificationQueueEntryResource.
				deleteNotificationQueueEntryHttpResponse(
					notificationQueueEntry.getId()));

		assertHttpResponseStatusCode(
			404,
			notificationQueueEntryResource.
				getNotificationQueueEntryHttpResponse(
					notificationQueueEntry.getId()));
		assertHttpResponseStatusCode(
			404,
			notificationQueueEntryResource.
				getNotificationQueueEntryHttpResponse(0L));
	}

	protected NotificationQueueEntry
			testDeleteNotificationQueueEntry_addNotificationQueueEntry()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteNotificationQueueEntry() throws Exception {

		// No namespace

		NotificationQueueEntry notificationQueueEntry1 =
			testGraphQLDeleteNotificationQueueEntry_addNotificationQueueEntry();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteNotificationQueueEntry",
						new HashMap<String, Object>() {
							{
								put(
									"notificationQueueEntryId",
									notificationQueueEntry1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteNotificationQueueEntry"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"notificationQueueEntry",
					new HashMap<String, Object>() {
						{
							put(
								"notificationQueueEntryId",
								notificationQueueEntry1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace notification_v1_0

		NotificationQueueEntry notificationQueueEntry2 =
			testGraphQLDeleteNotificationQueueEntry_addNotificationQueueEntry();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"notification_v1_0",
						new GraphQLField(
							"deleteNotificationQueueEntry",
							new HashMap<String, Object>() {
								{
									put(
										"notificationQueueEntryId",
										notificationQueueEntry2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/notification_v1_0",
				"Object/deleteNotificationQueueEntry"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"notification_v1_0",
					new GraphQLField(
						"notificationQueueEntry",
						new HashMap<String, Object>() {
							{
								put(
									"notificationQueueEntryId",
									notificationQueueEntry2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected NotificationQueueEntry
			testGraphQLDeleteNotificationQueueEntry_addNotificationQueueEntry()
		throws Exception {

		return testGraphQLNotificationQueueEntry_addNotificationQueueEntry();
	}

	@Test
	public void testDeleteNotificationQueueEntryBatch() throws Exception {
		NotificationQueueEntry notificationQueueEntry1 =
			testDeleteNotificationQueueEntryBatch_addNotificationQueueEntry();

		testDeleteNotificationQueueEntryBatch_deleteNotificationQueueEntry(
			202, null, notificationQueueEntry1.getId());

		assertHttpResponseStatusCode(
			404,
			notificationQueueEntryResource.
				getNotificationQueueEntryHttpResponse(
					notificationQueueEntry1.getId()));
	}

	protected NotificationQueueEntry
			testDeleteNotificationQueueEntryBatch_addNotificationQueueEntry()
		throws Exception {

		return testDeleteNotificationQueueEntry_addNotificationQueueEntry();
	}

	protected void
			testDeleteNotificationQueueEntryBatch_deleteNotificationQueueEntry(
				int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			notificationQueueEntryResource.
				deleteNotificationQueueEntryBatchHttpResponse(
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
	public void testGetNotificationQueueEntriesPage() throws Exception {
		Page<NotificationQueueEntry> page =
			notificationQueueEntryResource.getNotificationQueueEntriesPage(
				null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		NotificationQueueEntry notificationQueueEntry1 =
			testGetNotificationQueueEntriesPage_addNotificationQueueEntry(
				randomNotificationQueueEntry());

		NotificationQueueEntry notificationQueueEntry2 =
			testGetNotificationQueueEntriesPage_addNotificationQueueEntry(
				randomNotificationQueueEntry());

		page = notificationQueueEntryResource.getNotificationQueueEntriesPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			notificationQueueEntry1,
			(List<NotificationQueueEntry>)page.getItems());
		assertContains(
			notificationQueueEntry2,
			(List<NotificationQueueEntry>)page.getItems());
		assertValid(
			page, testGetNotificationQueueEntriesPage_getExpectedActions());

		notificationQueueEntryResource.deleteNotificationQueueEntry(
			notificationQueueEntry1.getId());

		notificationQueueEntryResource.deleteNotificationQueueEntry(
			notificationQueueEntry2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetNotificationQueueEntriesPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetNotificationQueueEntriesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		NotificationQueueEntry notificationQueueEntry1 =
			randomNotificationQueueEntry();

		notificationQueueEntry1 =
			testGetNotificationQueueEntriesPage_addNotificationQueueEntry(
				notificationQueueEntry1);

		for (EntityField entityField : entityFields) {
			Page<NotificationQueueEntry> page =
				notificationQueueEntryResource.getNotificationQueueEntriesPage(
					null,
					getFilterString(
						entityField, "between", notificationQueueEntry1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(notificationQueueEntry1),
				(List<NotificationQueueEntry>)page.getItems());
		}
	}

	@Test
	public void testGetNotificationQueueEntriesPageWithFilterDoubleEquals()
		throws Exception {

		testGetNotificationQueueEntriesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetNotificationQueueEntriesPageWithFilterStringContains()
		throws Exception {

		testGetNotificationQueueEntriesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetNotificationQueueEntriesPageWithFilterStringEquals()
		throws Exception {

		testGetNotificationQueueEntriesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetNotificationQueueEntriesPageWithFilterStringStartsWith()
		throws Exception {

		testGetNotificationQueueEntriesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetNotificationQueueEntriesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		NotificationQueueEntry notificationQueueEntry1 =
			testGetNotificationQueueEntriesPage_addNotificationQueueEntry(
				randomNotificationQueueEntry());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		NotificationQueueEntry notificationQueueEntry2 =
			testGetNotificationQueueEntriesPage_addNotificationQueueEntry(
				randomNotificationQueueEntry());

		for (EntityField entityField : entityFields) {
			Page<NotificationQueueEntry> page =
				notificationQueueEntryResource.getNotificationQueueEntriesPage(
					null,
					getFilterString(
						entityField, operator, notificationQueueEntry1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(notificationQueueEntry1),
				(List<NotificationQueueEntry>)page.getItems());
		}
	}

	@Test
	public void testGetNotificationQueueEntriesPageWithPagination()
		throws Exception {

		Page<NotificationQueueEntry> notificationQueueEntriesPage =
			notificationQueueEntryResource.getNotificationQueueEntriesPage(
				null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			notificationQueueEntriesPage.getTotalCount());

		NotificationQueueEntry notificationQueueEntry1 =
			testGetNotificationQueueEntriesPage_addNotificationQueueEntry(
				randomNotificationQueueEntry());

		NotificationQueueEntry notificationQueueEntry2 =
			testGetNotificationQueueEntriesPage_addNotificationQueueEntry(
				randomNotificationQueueEntry());

		NotificationQueueEntry notificationQueueEntry3 =
			testGetNotificationQueueEntriesPage_addNotificationQueueEntry(
				randomNotificationQueueEntry());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<NotificationQueueEntry> page1 =
				notificationQueueEntryResource.getNotificationQueueEntriesPage(
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				notificationQueueEntry1,
				(List<NotificationQueueEntry>)page1.getItems());

			Page<NotificationQueueEntry> page2 =
				notificationQueueEntryResource.getNotificationQueueEntriesPage(
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				notificationQueueEntry2,
				(List<NotificationQueueEntry>)page2.getItems());

			Page<NotificationQueueEntry> page3 =
				notificationQueueEntryResource.getNotificationQueueEntriesPage(
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				notificationQueueEntry3,
				(List<NotificationQueueEntry>)page3.getItems());
		}
		else {
			Page<NotificationQueueEntry> page1 =
				notificationQueueEntryResource.getNotificationQueueEntriesPage(
					null, null, Pagination.of(1, totalCount + 2), null);

			List<NotificationQueueEntry> notificationQueueEntries1 =
				(List<NotificationQueueEntry>)page1.getItems();

			Assert.assertEquals(
				notificationQueueEntries1.toString(), totalCount + 2,
				notificationQueueEntries1.size());

			Page<NotificationQueueEntry> page2 =
				notificationQueueEntryResource.getNotificationQueueEntriesPage(
					null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<NotificationQueueEntry> notificationQueueEntries2 =
				(List<NotificationQueueEntry>)page2.getItems();

			Assert.assertEquals(
				notificationQueueEntries2.toString(), 1,
				notificationQueueEntries2.size());

			Page<NotificationQueueEntry> page3 =
				notificationQueueEntryResource.getNotificationQueueEntriesPage(
					null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				notificationQueueEntry1,
				(List<NotificationQueueEntry>)page3.getItems());
			assertContains(
				notificationQueueEntry2,
				(List<NotificationQueueEntry>)page3.getItems());
			assertContains(
				notificationQueueEntry3,
				(List<NotificationQueueEntry>)page3.getItems());
		}
	}

	@Test
	public void testGetNotificationQueueEntriesPageWithSortDateTime()
		throws Exception {

		testGetNotificationQueueEntriesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, notificationQueueEntry1, notificationQueueEntry2) -> {
				BeanTestUtil.setProperty(
					notificationQueueEntry1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetNotificationQueueEntriesPageWithSortDouble()
		throws Exception {

		testGetNotificationQueueEntriesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, notificationQueueEntry1, notificationQueueEntry2) -> {
				BeanTestUtil.setProperty(
					notificationQueueEntry1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					notificationQueueEntry2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetNotificationQueueEntriesPageWithSortInteger()
		throws Exception {

		testGetNotificationQueueEntriesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, notificationQueueEntry1, notificationQueueEntry2) -> {
				BeanTestUtil.setProperty(
					notificationQueueEntry1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					notificationQueueEntry2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetNotificationQueueEntriesPageWithSortString()
		throws Exception {

		testGetNotificationQueueEntriesPageWithSort(
			EntityField.Type.STRING,
			(entityField, notificationQueueEntry1, notificationQueueEntry2) -> {
				Class<?> clazz = notificationQueueEntry1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						notificationQueueEntry1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						notificationQueueEntry2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						notificationQueueEntry1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						notificationQueueEntry2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						notificationQueueEntry1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						notificationQueueEntry2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetNotificationQueueEntriesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, NotificationQueueEntry, NotificationQueueEntry,
				 Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		NotificationQueueEntry notificationQueueEntry1 =
			randomNotificationQueueEntry();
		NotificationQueueEntry notificationQueueEntry2 =
			randomNotificationQueueEntry();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, notificationQueueEntry1, notificationQueueEntry2);
		}

		notificationQueueEntry1 =
			testGetNotificationQueueEntriesPage_addNotificationQueueEntry(
				notificationQueueEntry1);

		notificationQueueEntry2 =
			testGetNotificationQueueEntriesPage_addNotificationQueueEntry(
				notificationQueueEntry2);

		Page<NotificationQueueEntry> page =
			notificationQueueEntryResource.getNotificationQueueEntriesPage(
				null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<NotificationQueueEntry> ascPage =
				notificationQueueEntryResource.getNotificationQueueEntriesPage(
					null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				notificationQueueEntry1,
				(List<NotificationQueueEntry>)ascPage.getItems());
			assertContains(
				notificationQueueEntry2,
				(List<NotificationQueueEntry>)ascPage.getItems());

			Page<NotificationQueueEntry> descPage =
				notificationQueueEntryResource.getNotificationQueueEntriesPage(
					null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				notificationQueueEntry2,
				(List<NotificationQueueEntry>)descPage.getItems());
			assertContains(
				notificationQueueEntry1,
				(List<NotificationQueueEntry>)descPage.getItems());
		}
	}

	protected NotificationQueueEntry
			testGetNotificationQueueEntriesPage_addNotificationQueueEntry(
				NotificationQueueEntry notificationQueueEntry)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetNotificationQueueEntriesPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"notificationQueueEntries",
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

		JSONObject notificationQueueEntriesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/notificationQueueEntries");

		long totalCount = notificationQueueEntriesJSONObject.getLong(
			"totalCount");

		NotificationQueueEntry notificationQueueEntry1 =
			testGraphQLNotificationQueueEntry_addNotificationQueueEntry(
				randomNotificationQueueEntry());

		NotificationQueueEntry notificationQueueEntry2 =
			testGraphQLNotificationQueueEntry_addNotificationQueueEntry(
				randomNotificationQueueEntry());

		notificationQueueEntriesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/notificationQueueEntries");

		Assert.assertEquals(
			totalCount + 2,
			notificationQueueEntriesJSONObject.getLong("totalCount"));

		assertContains(
			notificationQueueEntry1,
			Arrays.asList(
				NotificationQueueEntrySerDes.toDTOs(
					notificationQueueEntriesJSONObject.getString("items"))));
		assertContains(
			notificationQueueEntry2,
			Arrays.asList(
				NotificationQueueEntrySerDes.toDTOs(
					notificationQueueEntriesJSONObject.getString("items"))));

		// Using the namespace notification_v1_0

		notificationQueueEntriesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("notification_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/notification_v1_0",
			"JSONObject/notificationQueueEntries");

		Assert.assertEquals(
			totalCount + 2,
			notificationQueueEntriesJSONObject.getLong("totalCount"));

		assertContains(
			notificationQueueEntry1,
			Arrays.asList(
				NotificationQueueEntrySerDes.toDTOs(
					notificationQueueEntriesJSONObject.getString("items"))));
		assertContains(
			notificationQueueEntry2,
			Arrays.asList(
				NotificationQueueEntrySerDes.toDTOs(
					notificationQueueEntriesJSONObject.getString("items"))));
	}

	@Test
	public void testGetNotificationQueueEntry() throws Exception {
		NotificationQueueEntry postNotificationQueueEntry =
			testGetNotificationQueueEntry_addNotificationQueueEntry();

		NotificationQueueEntry getNotificationQueueEntry =
			notificationQueueEntryResource.getNotificationQueueEntry(
				postNotificationQueueEntry.getId());

		assertEquals(postNotificationQueueEntry, getNotificationQueueEntry);
		assertValid(getNotificationQueueEntry);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		NotificationQueueEntry postNotificationQueueEntry =
			testGetNotificationQueueEntry_addNotificationQueueEntry();

		NotificationQueueEntry getNotificationQueueEntry =
			notificationQueueEntryResource.getNotificationQueueEntry(
				postNotificationQueueEntry.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.notification.rest.dto.v1_0.NotificationQueueEntry"
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
			postNotificationQueueEntry.getId());

		assertEquals(
			getNotificationQueueEntry,
			NotificationQueueEntrySerDes.toDTO(item.toString()));
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

	protected NotificationQueueEntry
			testGetNotificationQueueEntry_addNotificationQueueEntry()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetNotificationQueueEntry() throws Exception {
		NotificationQueueEntry notificationQueueEntry =
			testGraphQLGetNotificationQueueEntry_addNotificationQueueEntry();

		// No namespace

		Assert.assertTrue(
			equals(
				notificationQueueEntry,
				NotificationQueueEntrySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"notificationQueueEntry",
								new HashMap<String, Object>() {
									{
										put(
											"notificationQueueEntryId",
											notificationQueueEntry.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/notificationQueueEntry"))));

		// Using the namespace notification_v1_0

		Assert.assertTrue(
			equals(
				notificationQueueEntry,
				NotificationQueueEntrySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"notification_v1_0",
								new GraphQLField(
									"notificationQueueEntry",
									new HashMap<String, Object>() {
										{
											put(
												"notificationQueueEntryId",
												notificationQueueEntry.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/notification_v1_0",
						"Object/notificationQueueEntry"))));
	}

	@Test
	public void testGraphQLGetNotificationQueueEntryNotFound()
		throws Exception {

		Long irrelevantNotificationQueueEntryId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"notificationQueueEntry",
						new HashMap<String, Object>() {
							{
								put(
									"notificationQueueEntryId",
									irrelevantNotificationQueueEntryId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace notification_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"notification_v1_0",
						new GraphQLField(
							"notificationQueueEntry",
							new HashMap<String, Object>() {
								{
									put(
										"notificationQueueEntryId",
										irrelevantNotificationQueueEntryId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected NotificationQueueEntry
			testGraphQLGetNotificationQueueEntry_addNotificationQueueEntry()
		throws Exception {

		return testGraphQLNotificationQueueEntry_addNotificationQueueEntry();
	}

	@Test
	public void testPostNotificationQueueEntry() throws Exception {
		NotificationQueueEntry randomNotificationQueueEntry =
			randomNotificationQueueEntry();

		NotificationQueueEntry postNotificationQueueEntry =
			testPostNotificationQueueEntry_addNotificationQueueEntry(
				randomNotificationQueueEntry);

		assertEquals(randomNotificationQueueEntry, postNotificationQueueEntry);
		assertValid(postNotificationQueueEntry);
	}

	protected NotificationQueueEntry
			testPostNotificationQueueEntry_addNotificationQueueEntry(
				NotificationQueueEntry notificationQueueEntry)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostNotificationQueueEntry() throws Exception {
		NotificationQueueEntry randomNotificationQueueEntry =
			randomNotificationQueueEntry();

		NotificationQueueEntry notificationQueueEntry =
			testGraphQLNotificationQueueEntry_addNotificationQueueEntry(
				randomNotificationQueueEntry);

		Assert.assertTrue(
			equals(randomNotificationQueueEntry, notificationQueueEntry));
	}

	@Test
	public void testPutNotificationQueueEntryResend() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		NotificationQueueEntry notificationQueueEntry =
			testPutNotificationQueueEntryResend_addNotificationQueueEntry();

		assertHttpResponseStatusCode(
			204,
			notificationQueueEntryResource.
				putNotificationQueueEntryResendHttpResponse(
					notificationQueueEntry.getId()));

		assertHttpResponseStatusCode(
			404,
			notificationQueueEntryResource.
				putNotificationQueueEntryResendHttpResponse(0L));
	}

	protected NotificationQueueEntry
			testPutNotificationQueueEntryResend_addNotificationQueueEntry()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		NotificationQueueEntry notificationQueueEntry1 =
			testBatchEngineDeleteImportTask_addNotificationQueueEntry();

		testBatchEngineDeleteImportTask_deleteNotificationQueueEntry(
			200, null, notificationQueueEntry1.getId());

		assertHttpResponseStatusCode(
			404,
			notificationQueueEntryResource.
				getNotificationQueueEntryHttpResponse(
					notificationQueueEntry1.getId()));
	}

	protected NotificationQueueEntry
			testBatchEngineDeleteImportTask_addNotificationQueueEntry()
		throws Exception {

		return testDeleteNotificationQueueEntry_addNotificationQueueEntry();
	}

	protected void testBatchEngineDeleteImportTask_deleteNotificationQueueEntry(
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
				"com.liferay.notification.rest.dto.v1_0.NotificationQueueEntry",
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

	protected NotificationQueueEntry
			testGraphQLNotificationQueueEntry_addNotificationQueueEntry()
		throws Exception {

		return testGraphQLNotificationQueueEntry_addNotificationQueueEntry(
			randomNotificationQueueEntry());
	}

	protected NotificationQueueEntry
			testGraphQLNotificationQueueEntry_addNotificationQueueEntry(
				NotificationQueueEntry notificationQueueEntry)
		throws Exception {

		JSONDeserializer<NotificationQueueEntry> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(NotificationQueueEntry.class)) {

			if (getGraphQLValue(field.get(notificationQueueEntry)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(notificationQueueEntry)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createNotificationQueueEntry",
						new HashMap<String, Object>() {
							{
								put("notificationQueueEntry", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createNotificationQueueEntry"),
			NotificationQueueEntry.class);
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
		NotificationQueueEntry notificationQueueEntry,
		List<NotificationQueueEntry> notificationQueueEntries) {

		boolean contains = false;

		for (NotificationQueueEntry item : notificationQueueEntries) {
			if (equals(notificationQueueEntry, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			notificationQueueEntries + " does not contain " +
				notificationQueueEntry,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		NotificationQueueEntry notificationQueueEntry1,
		NotificationQueueEntry notificationQueueEntry2) {

		Assert.assertTrue(
			notificationQueueEntry1 + " does not equal " +
				notificationQueueEntry2,
			equals(notificationQueueEntry1, notificationQueueEntry2));
	}

	protected void assertEquals(
		List<NotificationQueueEntry> notificationQueueEntries1,
		List<NotificationQueueEntry> notificationQueueEntries2) {

		Assert.assertEquals(
			notificationQueueEntries1.size(), notificationQueueEntries2.size());

		for (int i = 0; i < notificationQueueEntries1.size(); i++) {
			NotificationQueueEntry notificationQueueEntry1 =
				notificationQueueEntries1.get(i);
			NotificationQueueEntry notificationQueueEntry2 =
				notificationQueueEntries2.get(i);

			assertEquals(notificationQueueEntry1, notificationQueueEntry2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<NotificationQueueEntry> notificationQueueEntries1,
		List<NotificationQueueEntry> notificationQueueEntries2) {

		Assert.assertEquals(
			notificationQueueEntries1.size(), notificationQueueEntries2.size());

		for (NotificationQueueEntry notificationQueueEntry1 :
				notificationQueueEntries1) {

			boolean contains = false;

			for (NotificationQueueEntry notificationQueueEntry2 :
					notificationQueueEntries2) {

				if (equals(notificationQueueEntry1, notificationQueueEntry2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				notificationQueueEntries2 + " does not contain " +
					notificationQueueEntry1,
				contains);
		}
	}

	protected void assertValid(NotificationQueueEntry notificationQueueEntry)
		throws Exception {

		boolean valid = true;

		if (notificationQueueEntry.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (notificationQueueEntry.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("body", additionalAssertFieldName)) {
				if (notificationQueueEntry.getBody() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("fromName", additionalAssertFieldName)) {
				if (notificationQueueEntry.getFromName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("recipients", additionalAssertFieldName)) {
				if (notificationQueueEntry.getRecipients() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"recipientsSummary", additionalAssertFieldName)) {

				if (notificationQueueEntry.getRecipientsSummary() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("sentDate", additionalAssertFieldName)) {
				if (notificationQueueEntry.getSentDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (notificationQueueEntry.getStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("subject", additionalAssertFieldName)) {
				if (notificationQueueEntry.getSubject() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("triggerBy", additionalAssertFieldName)) {
				if (notificationQueueEntry.getTriggerBy() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (notificationQueueEntry.getType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("typeLabel", additionalAssertFieldName)) {
				if (notificationQueueEntry.getTypeLabel() == null) {
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

	protected void assertValid(Page<NotificationQueueEntry> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<NotificationQueueEntry> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<NotificationQueueEntry> notificationQueueEntries =
			page.getItems();

		int size = notificationQueueEntries.size();

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
					com.liferay.notification.rest.dto.v1_0.
						NotificationQueueEntry.class)) {

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
		NotificationQueueEntry notificationQueueEntry1,
		NotificationQueueEntry notificationQueueEntry2) {

		if (notificationQueueEntry1 == notificationQueueEntry2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)notificationQueueEntry1.getActions(),
						(Map)notificationQueueEntry2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("body", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						notificationQueueEntry1.getBody(),
						notificationQueueEntry2.getBody())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("fromName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						notificationQueueEntry1.getFromName(),
						notificationQueueEntry2.getFromName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						notificationQueueEntry1.getId(),
						notificationQueueEntry2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("recipients", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						notificationQueueEntry1.getRecipients(),
						notificationQueueEntry2.getRecipients())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"recipientsSummary", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						notificationQueueEntry1.getRecipientsSummary(),
						notificationQueueEntry2.getRecipientsSummary())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("sentDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						notificationQueueEntry1.getSentDate(),
						notificationQueueEntry2.getSentDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						notificationQueueEntry1.getStatus(),
						notificationQueueEntry2.getStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("subject", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						notificationQueueEntry1.getSubject(),
						notificationQueueEntry2.getSubject())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("triggerBy", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						notificationQueueEntry1.getTriggerBy(),
						notificationQueueEntry2.getTriggerBy())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						notificationQueueEntry1.getType(),
						notificationQueueEntry2.getType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("typeLabel", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						notificationQueueEntry1.getTypeLabel(),
						notificationQueueEntry2.getTypeLabel())) {

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

		if (!(_notificationQueueEntryResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_notificationQueueEntryResource;

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
		NotificationQueueEntry notificationQueueEntry) {

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

		if (entityFieldName.equals("body")) {
			Object object = notificationQueueEntry.getBody();

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

		if (entityFieldName.equals("fromName")) {
			Object object = notificationQueueEntry.getFromName();

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

		if (entityFieldName.equals("recipients")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("recipientsSummary")) {
			Object object = notificationQueueEntry.getRecipientsSummary();

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

		if (entityFieldName.equals("sentDate")) {
			if (operator.equals("between")) {
				Date date = notificationQueueEntry.getSentDate();

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

				sb.append(_format.format(notificationQueueEntry.getSentDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("status")) {
			sb.append(String.valueOf(notificationQueueEntry.getStatus()));

			return sb.toString();
		}

		if (entityFieldName.equals("subject")) {
			Object object = notificationQueueEntry.getSubject();

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

		if (entityFieldName.equals("triggerBy")) {
			Object object = notificationQueueEntry.getTriggerBy();

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

		if (entityFieldName.equals("type")) {
			Object object = notificationQueueEntry.getType();

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

		if (entityFieldName.equals("typeLabel")) {
			Object object = notificationQueueEntry.getTypeLabel();

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

	protected NotificationQueueEntry randomNotificationQueueEntry()
		throws Exception {

		return new NotificationQueueEntry() {
			{
				body = StringUtil.toLowerCase(RandomTestUtil.randomString());
				fromName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				recipientsSummary = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				sentDate = RandomTestUtil.nextDate();
				status = RandomTestUtil.randomInt();
				subject = StringUtil.toLowerCase(RandomTestUtil.randomString());
				triggerBy = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				type = StringUtil.toLowerCase(RandomTestUtil.randomString());
				typeLabel = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected NotificationQueueEntry randomIrrelevantNotificationQueueEntry()
		throws Exception {

		NotificationQueueEntry randomIrrelevantNotificationQueueEntry =
			randomNotificationQueueEntry();

		return randomIrrelevantNotificationQueueEntry;
	}

	protected NotificationQueueEntry randomPatchNotificationQueueEntry()
		throws Exception {

		return randomNotificationQueueEntry();
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

	protected NotificationQueueEntryResource notificationQueueEntryResource;
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
		LogFactoryUtil.getLog(BaseNotificationQueueEntryResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.notification.rest.resource.v1_0.
			NotificationQueueEntryResource _notificationQueueEntryResource;

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