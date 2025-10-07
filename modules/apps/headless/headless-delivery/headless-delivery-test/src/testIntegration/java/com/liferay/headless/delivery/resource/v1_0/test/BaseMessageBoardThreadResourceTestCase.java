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
import com.liferay.headless.delivery.client.dto.v1_0.MessageBoardThread;
import com.liferay.headless.delivery.client.dto.v1_0.Rating;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.permission.Permission;
import com.liferay.headless.delivery.client.resource.v1_0.MessageBoardThreadResource;
import com.liferay.headless.delivery.client.serdes.v1_0.MessageBoardThreadSerDes;
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
public abstract class BaseMessageBoardThreadResourceTestCase {

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

		_messageBoardThreadResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		messageBoardThreadResource = MessageBoardThreadResource.builder(
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

		MessageBoardThread messageBoardThread1 = randomMessageBoardThread();

		String json = objectMapper.writeValueAsString(messageBoardThread1);

		MessageBoardThread messageBoardThread2 = MessageBoardThreadSerDes.toDTO(
			json);

		Assert.assertTrue(equals(messageBoardThread1, messageBoardThread2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		MessageBoardThread messageBoardThread = randomMessageBoardThread();

		String json1 = objectMapper.writeValueAsString(messageBoardThread);
		String json2 = MessageBoardThreadSerDes.toJSON(messageBoardThread);

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

		MessageBoardThread messageBoardThread = randomMessageBoardThread();

		messageBoardThread.setArticleBody(regex);
		messageBoardThread.setEncodingFormat(regex);
		messageBoardThread.setFriendlyUrlPath(regex);
		messageBoardThread.setHeadline(regex);
		messageBoardThread.setStatus(regex);
		messageBoardThread.setThreadType(regex);

		String json = MessageBoardThreadSerDes.toJSON(messageBoardThread);

		Assert.assertFalse(json.contains(regex));

		messageBoardThread = MessageBoardThreadSerDes.toDTO(json);

		Assert.assertEquals(regex, messageBoardThread.getArticleBody());
		Assert.assertEquals(regex, messageBoardThread.getEncodingFormat());
		Assert.assertEquals(regex, messageBoardThread.getFriendlyUrlPath());
		Assert.assertEquals(regex, messageBoardThread.getHeadline());
		Assert.assertEquals(regex, messageBoardThread.getStatus());
		Assert.assertEquals(regex, messageBoardThread.getThreadType());
	}

	@Test
	public void testDeleteMessageBoardThread() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardThread messageBoardThread =
			testDeleteMessageBoardThread_addMessageBoardThread();

		assertHttpResponseStatusCode(
			204,
			messageBoardThreadResource.deleteMessageBoardThreadHttpResponse(
				messageBoardThread.getId()));

		assertHttpResponseStatusCode(
			404,
			messageBoardThreadResource.getMessageBoardThreadHttpResponse(
				messageBoardThread.getId()));
		assertHttpResponseStatusCode(
			404,
			messageBoardThreadResource.getMessageBoardThreadHttpResponse(0L));
	}

	protected MessageBoardThread
			testDeleteMessageBoardThread_addMessageBoardThread()
		throws Exception {

		return messageBoardThreadResource.postSiteMessageBoardThread(
			testGroup.getGroupId(), randomMessageBoardThread());
	}

	@Test
	public void testGraphQLDeleteMessageBoardThread() throws Exception {

		// No namespace

		MessageBoardThread messageBoardThread1 =
			testGraphQLDeleteMessageBoardThread_addMessageBoardThread();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteMessageBoardThread",
						new HashMap<String, Object>() {
							{
								put(
									"messageBoardThreadId",
									messageBoardThread1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteMessageBoardThread"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"messageBoardThread",
					new HashMap<String, Object>() {
						{
							put(
								"messageBoardThreadId",
								messageBoardThread1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		MessageBoardThread messageBoardThread2 =
			testGraphQLDeleteMessageBoardThread_addMessageBoardThread();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteMessageBoardThread",
							new HashMap<String, Object>() {
								{
									put(
										"messageBoardThreadId",
										messageBoardThread2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteMessageBoardThread"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"messageBoardThread",
						new HashMap<String, Object>() {
							{
								put(
									"messageBoardThreadId",
									messageBoardThread2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected MessageBoardThread
			testGraphQLDeleteMessageBoardThread_addMessageBoardThread()
		throws Exception {

		return testGraphQLMessageBoardThread_addMessageBoardThread();
	}

	@Test
	public void testDeleteMessageBoardThreadBatch() throws Exception {
		MessageBoardThread messageBoardThread1 =
			testDeleteMessageBoardThreadBatch_addMessageBoardThread();

		testDeleteMessageBoardThreadBatch_deleteMessageBoardThread(
			202, null, messageBoardThread1.getId());

		assertHttpResponseStatusCode(
			404,
			messageBoardThreadResource.getMessageBoardThreadHttpResponse(
				messageBoardThread1.getId()));
	}

	protected MessageBoardThread
			testDeleteMessageBoardThreadBatch_addMessageBoardThread()
		throws Exception {

		return testDeleteMessageBoardThread_addMessageBoardThread();
	}

	protected void testDeleteMessageBoardThreadBatch_deleteMessageBoardThread(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			messageBoardThreadResource.
				deleteMessageBoardThreadBatchHttpResponse(
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
	public void testDeleteMessageBoardThreadMyRating() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardThread messageBoardThread =
			testDeleteMessageBoardThreadMyRating_addMessageBoardThread();

		assertHttpResponseStatusCode(
			204,
			messageBoardThreadResource.
				deleteMessageBoardThreadMyRatingHttpResponse(
					messageBoardThread.getId()));

		assertHttpResponseStatusCode(
			404,
			messageBoardThreadResource.
				getMessageBoardThreadMyRatingHttpResponse(
					messageBoardThread.getId()));
		assertHttpResponseStatusCode(
			404,
			messageBoardThreadResource.
				getMessageBoardThreadMyRatingHttpResponse(0L));
	}

	protected MessageBoardThread
			testDeleteMessageBoardThreadMyRating_addMessageBoardThread()
		throws Exception {

		return messageBoardThreadResource.postSiteMessageBoardThread(
			testGroup.getGroupId(), randomMessageBoardThread());
	}

	@Test
	public void testGraphQLDeleteMessageBoardThreadMyRating() throws Exception {

		// No namespace

		MessageBoardThread messageBoardThread1 =
			testGraphQLDeleteMessageBoardThreadMyRating_addMessageBoardThread();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteMessageBoardThreadMyRating",
						new HashMap<String, Object>() {
							{
								put(
									"messageBoardThreadId",
									messageBoardThread1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteMessageBoardThreadMyRating"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"messageBoardThreadMyRating",
					new HashMap<String, Object>() {
						{
							put(
								"messageBoardThreadId",
								messageBoardThread1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		MessageBoardThread messageBoardThread2 =
			testGraphQLDeleteMessageBoardThreadMyRating_addMessageBoardThread();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteMessageBoardThreadMyRating",
							new HashMap<String, Object>() {
								{
									put(
										"messageBoardThreadId",
										messageBoardThread2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteMessageBoardThreadMyRating"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"messageBoardThreadMyRating",
						new HashMap<String, Object>() {
							{
								put(
									"messageBoardThreadId",
									messageBoardThread2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected MessageBoardThread
			testGraphQLDeleteMessageBoardThreadMyRating_addMessageBoardThread()
		throws Exception {

		return testGraphQLMessageBoardThread_addMessageBoardThread();
	}

	@Test
	public void testGetMessageBoardSectionMessageBoardThreadsPage()
		throws Exception {

		Long messageBoardSectionId =
			testGetMessageBoardSectionMessageBoardThreadsPage_getMessageBoardSectionId();
		Long irrelevantMessageBoardSectionId =
			testGetMessageBoardSectionMessageBoardThreadsPage_getIrrelevantMessageBoardSectionId();

		Page<MessageBoardThread> page =
			messageBoardThreadResource.
				getMessageBoardSectionMessageBoardThreadsPage(
					messageBoardSectionId, null, null, null,
					Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantMessageBoardSectionId != null) {
			MessageBoardThread irrelevantMessageBoardThread =
				testGetMessageBoardSectionMessageBoardThreadsPage_addMessageBoardThread(
					irrelevantMessageBoardSectionId,
					randomIrrelevantMessageBoardThread());

			page =
				messageBoardThreadResource.
					getMessageBoardSectionMessageBoardThreadsPage(
						irrelevantMessageBoardSectionId, null, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantMessageBoardThread,
				(List<MessageBoardThread>)page.getItems());
			assertValid(
				page,
				testGetMessageBoardSectionMessageBoardThreadsPage_getExpectedActions(
					irrelevantMessageBoardSectionId));
		}

		MessageBoardThread messageBoardThread1 =
			testGetMessageBoardSectionMessageBoardThreadsPage_addMessageBoardThread(
				messageBoardSectionId, randomMessageBoardThread());

		MessageBoardThread messageBoardThread2 =
			testGetMessageBoardSectionMessageBoardThreadsPage_addMessageBoardThread(
				messageBoardSectionId, randomMessageBoardThread());

		page =
			messageBoardThreadResource.
				getMessageBoardSectionMessageBoardThreadsPage(
					messageBoardSectionId, null, null, null,
					Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			messageBoardThread1, (List<MessageBoardThread>)page.getItems());
		assertContains(
			messageBoardThread2, (List<MessageBoardThread>)page.getItems());
		assertValid(
			page,
			testGetMessageBoardSectionMessageBoardThreadsPage_getExpectedActions(
				messageBoardSectionId));

		messageBoardThreadResource.deleteMessageBoardThread(
			messageBoardThread1.getId());

		messageBoardThreadResource.deleteMessageBoardThread(
			messageBoardThread2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetMessageBoardSectionMessageBoardThreadsPage_getExpectedActions(
				Long messageBoardSectionId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/message-board-sections/{messageBoardSectionId}/message-board-threads/batch".
				replace(
					"{messageBoardSectionId}",
					String.valueOf(messageBoardSectionId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetMessageBoardSectionMessageBoardThreadsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long messageBoardSectionId =
			testGetMessageBoardSectionMessageBoardThreadsPage_getMessageBoardSectionId();

		MessageBoardThread messageBoardThread1 = randomMessageBoardThread();

		messageBoardThread1 =
			testGetMessageBoardSectionMessageBoardThreadsPage_addMessageBoardThread(
				messageBoardSectionId, messageBoardThread1);

		for (EntityField entityField : entityFields) {
			Page<MessageBoardThread> page =
				messageBoardThreadResource.
					getMessageBoardSectionMessageBoardThreadsPage(
						messageBoardSectionId, null, null,
						getFilterString(
							entityField, "between", messageBoardThread1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(messageBoardThread1),
				(List<MessageBoardThread>)page.getItems());
		}
	}

	@Test
	public void testGetMessageBoardSectionMessageBoardThreadsPageWithFilterDoubleEquals()
		throws Exception {

		testGetMessageBoardSectionMessageBoardThreadsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetMessageBoardSectionMessageBoardThreadsPageWithFilterStringContains()
		throws Exception {

		testGetMessageBoardSectionMessageBoardThreadsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetMessageBoardSectionMessageBoardThreadsPageWithFilterStringEquals()
		throws Exception {

		testGetMessageBoardSectionMessageBoardThreadsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetMessageBoardSectionMessageBoardThreadsPageWithFilterStringStartsWith()
		throws Exception {

		testGetMessageBoardSectionMessageBoardThreadsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetMessageBoardSectionMessageBoardThreadsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long messageBoardSectionId =
			testGetMessageBoardSectionMessageBoardThreadsPage_getMessageBoardSectionId();

		MessageBoardThread messageBoardThread1 =
			testGetMessageBoardSectionMessageBoardThreadsPage_addMessageBoardThread(
				messageBoardSectionId, randomMessageBoardThread());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardThread messageBoardThread2 =
			testGetMessageBoardSectionMessageBoardThreadsPage_addMessageBoardThread(
				messageBoardSectionId, randomMessageBoardThread());

		for (EntityField entityField : entityFields) {
			Page<MessageBoardThread> page =
				messageBoardThreadResource.
					getMessageBoardSectionMessageBoardThreadsPage(
						messageBoardSectionId, null, null,
						getFilterString(
							entityField, operator, messageBoardThread1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(messageBoardThread1),
				(List<MessageBoardThread>)page.getItems());
		}
	}

	@Test
	public void testGetMessageBoardSectionMessageBoardThreadsPageWithPagination()
		throws Exception {

		Long messageBoardSectionId =
			testGetMessageBoardSectionMessageBoardThreadsPage_getMessageBoardSectionId();

		Page<MessageBoardThread> messageBoardThreadsPage =
			messageBoardThreadResource.
				getMessageBoardSectionMessageBoardThreadsPage(
					messageBoardSectionId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			messageBoardThreadsPage.getTotalCount());

		MessageBoardThread messageBoardThread1 =
			testGetMessageBoardSectionMessageBoardThreadsPage_addMessageBoardThread(
				messageBoardSectionId, randomMessageBoardThread());

		MessageBoardThread messageBoardThread2 =
			testGetMessageBoardSectionMessageBoardThreadsPage_addMessageBoardThread(
				messageBoardSectionId, randomMessageBoardThread());

		MessageBoardThread messageBoardThread3 =
			testGetMessageBoardSectionMessageBoardThreadsPage_addMessageBoardThread(
				messageBoardSectionId, randomMessageBoardThread());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<MessageBoardThread> page1 =
				messageBoardThreadResource.
					getMessageBoardSectionMessageBoardThreadsPage(
						messageBoardSectionId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				messageBoardThread1,
				(List<MessageBoardThread>)page1.getItems());

			Page<MessageBoardThread> page2 =
				messageBoardThreadResource.
					getMessageBoardSectionMessageBoardThreadsPage(
						messageBoardSectionId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				messageBoardThread2,
				(List<MessageBoardThread>)page2.getItems());

			Page<MessageBoardThread> page3 =
				messageBoardThreadResource.
					getMessageBoardSectionMessageBoardThreadsPage(
						messageBoardSectionId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				messageBoardThread3,
				(List<MessageBoardThread>)page3.getItems());
		}
		else {
			Page<MessageBoardThread> page1 =
				messageBoardThreadResource.
					getMessageBoardSectionMessageBoardThreadsPage(
						messageBoardSectionId, null, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<MessageBoardThread> messageBoardThreads1 =
				(List<MessageBoardThread>)page1.getItems();

			Assert.assertEquals(
				messageBoardThreads1.toString(), totalCount + 2,
				messageBoardThreads1.size());

			Page<MessageBoardThread> page2 =
				messageBoardThreadResource.
					getMessageBoardSectionMessageBoardThreadsPage(
						messageBoardSectionId, null, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<MessageBoardThread> messageBoardThreads2 =
				(List<MessageBoardThread>)page2.getItems();

			Assert.assertEquals(
				messageBoardThreads2.toString(), 1,
				messageBoardThreads2.size());

			Page<MessageBoardThread> page3 =
				messageBoardThreadResource.
					getMessageBoardSectionMessageBoardThreadsPage(
						messageBoardSectionId, null, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				messageBoardThread1,
				(List<MessageBoardThread>)page3.getItems());
			assertContains(
				messageBoardThread2,
				(List<MessageBoardThread>)page3.getItems());
			assertContains(
				messageBoardThread3,
				(List<MessageBoardThread>)page3.getItems());
		}
	}

	@Test
	public void testGetMessageBoardSectionMessageBoardThreadsPageWithSortDateTime()
		throws Exception {

		testGetMessageBoardSectionMessageBoardThreadsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, messageBoardThread1, messageBoardThread2) -> {
				BeanTestUtil.setProperty(
					messageBoardThread1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetMessageBoardSectionMessageBoardThreadsPageWithSortDouble()
		throws Exception {

		testGetMessageBoardSectionMessageBoardThreadsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, messageBoardThread1, messageBoardThread2) -> {
				BeanTestUtil.setProperty(
					messageBoardThread1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					messageBoardThread2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetMessageBoardSectionMessageBoardThreadsPageWithSortInteger()
		throws Exception {

		testGetMessageBoardSectionMessageBoardThreadsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, messageBoardThread1, messageBoardThread2) -> {
				BeanTestUtil.setProperty(
					messageBoardThread1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					messageBoardThread2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetMessageBoardSectionMessageBoardThreadsPageWithSortString()
		throws Exception {

		testGetMessageBoardSectionMessageBoardThreadsPageWithSort(
			EntityField.Type.STRING,
			(entityField, messageBoardThread1, messageBoardThread2) -> {
				Class<?> clazz = messageBoardThread1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						messageBoardThread1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						messageBoardThread2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						messageBoardThread1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						messageBoardThread2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						messageBoardThread1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						messageBoardThread2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetMessageBoardSectionMessageBoardThreadsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, MessageBoardThread, MessageBoardThread, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long messageBoardSectionId =
			testGetMessageBoardSectionMessageBoardThreadsPage_getMessageBoardSectionId();

		MessageBoardThread messageBoardThread1 = randomMessageBoardThread();
		MessageBoardThread messageBoardThread2 = randomMessageBoardThread();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, messageBoardThread1, messageBoardThread2);
		}

		messageBoardThread1 =
			testGetMessageBoardSectionMessageBoardThreadsPage_addMessageBoardThread(
				messageBoardSectionId, messageBoardThread1);

		messageBoardThread2 =
			testGetMessageBoardSectionMessageBoardThreadsPage_addMessageBoardThread(
				messageBoardSectionId, messageBoardThread2);

		Page<MessageBoardThread> page =
			messageBoardThreadResource.
				getMessageBoardSectionMessageBoardThreadsPage(
					messageBoardSectionId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<MessageBoardThread> ascPage =
				messageBoardThreadResource.
					getMessageBoardSectionMessageBoardThreadsPage(
						messageBoardSectionId, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				messageBoardThread1,
				(List<MessageBoardThread>)ascPage.getItems());
			assertContains(
				messageBoardThread2,
				(List<MessageBoardThread>)ascPage.getItems());

			Page<MessageBoardThread> descPage =
				messageBoardThreadResource.
					getMessageBoardSectionMessageBoardThreadsPage(
						messageBoardSectionId, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				messageBoardThread2,
				(List<MessageBoardThread>)descPage.getItems());
			assertContains(
				messageBoardThread1,
				(List<MessageBoardThread>)descPage.getItems());
		}
	}

	protected MessageBoardThread
			testGetMessageBoardSectionMessageBoardThreadsPage_addMessageBoardThread(
				Long messageBoardSectionId,
				MessageBoardThread messageBoardThread)
		throws Exception {

		return messageBoardThreadResource.
			postMessageBoardSectionMessageBoardThread(
				messageBoardSectionId, messageBoardThread);
	}

	protected Long
			testGetMessageBoardSectionMessageBoardThreadsPage_getMessageBoardSectionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetMessageBoardSectionMessageBoardThreadsPage_getIrrelevantMessageBoardSectionId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetMessageBoardSectionMessageBoardThreadsPage()
		throws Exception {

		Long messageBoardSectionId =
			testGetMessageBoardSectionMessageBoardThreadsPage_getMessageBoardSectionId();

		GraphQLField graphQLField = new GraphQLField(
			"messageBoardSectionMessageBoardThreads",
			new HashMap<String, Object>() {
				{
					put("messageBoardSectionId", messageBoardSectionId);
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject messageBoardSectionMessageBoardThreadsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/messageBoardSectionMessageBoardThreads");

		long totalCount =
			messageBoardSectionMessageBoardThreadsJSONObject.getLong(
				"totalCount");

		MessageBoardThread messageBoardThread1 =
			testGraphQLMessageBoardSectionMessageBoardThread_addMessageBoardThread(
				messageBoardSectionId, randomMessageBoardThread());

		MessageBoardThread messageBoardThread2 =
			testGraphQLMessageBoardSectionMessageBoardThread_addMessageBoardThread(
				messageBoardSectionId, randomMessageBoardThread());

		messageBoardSectionMessageBoardThreadsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/messageBoardSectionMessageBoardThreads");

		Assert.assertEquals(
			totalCount + 2,
			messageBoardSectionMessageBoardThreadsJSONObject.getLong(
				"totalCount"));

		assertContains(
			messageBoardThread1,
			Arrays.asList(
				MessageBoardThreadSerDes.toDTOs(
					messageBoardSectionMessageBoardThreadsJSONObject.getString(
						"items"))));
		assertContains(
			messageBoardThread2,
			Arrays.asList(
				MessageBoardThreadSerDes.toDTOs(
					messageBoardSectionMessageBoardThreadsJSONObject.getString(
						"items"))));

		// Using the namespace headlessDelivery_v1_0

		messageBoardSectionMessageBoardThreadsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField("headlessDelivery_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"JSONObject/messageBoardSectionMessageBoardThreads");

		Assert.assertEquals(
			totalCount + 2,
			messageBoardSectionMessageBoardThreadsJSONObject.getLong(
				"totalCount"));

		assertContains(
			messageBoardThread1,
			Arrays.asList(
				MessageBoardThreadSerDes.toDTOs(
					messageBoardSectionMessageBoardThreadsJSONObject.getString(
						"items"))));
		assertContains(
			messageBoardThread2,
			Arrays.asList(
				MessageBoardThreadSerDes.toDTOs(
					messageBoardSectionMessageBoardThreadsJSONObject.getString(
						"items"))));
	}

	@Test
	public void testGetMessageBoardThread() throws Exception {
		MessageBoardThread postMessageBoardThread =
			testGetMessageBoardThread_addMessageBoardThread();

		MessageBoardThread getMessageBoardThread =
			messageBoardThreadResource.getMessageBoardThread(
				postMessageBoardThread.getId());

		assertEquals(postMessageBoardThread, getMessageBoardThread);
		assertValid(getMessageBoardThread);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		MessageBoardThread postMessageBoardThread =
			testGetMessageBoardThread_addMessageBoardThread();

		MessageBoardThread getMessageBoardThread =
			messageBoardThreadResource.getMessageBoardThread(
				postMessageBoardThread.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.delivery.dto.v1_0.MessageBoardThread"
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
			postMessageBoardThread.getId());

		assertEquals(
			getMessageBoardThread,
			MessageBoardThreadSerDes.toDTO(item.toString()));
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

	protected MessageBoardThread
			testGetMessageBoardThread_addMessageBoardThread()
		throws Exception {

		return messageBoardThreadResource.postSiteMessageBoardThread(
			testGroup.getGroupId(), randomMessageBoardThread());
	}

	@Test
	public void testGraphQLGetMessageBoardThread() throws Exception {
		MessageBoardThread messageBoardThread =
			testGraphQLGetMessageBoardThread_addMessageBoardThread();

		// No namespace

		Assert.assertTrue(
			equals(
				messageBoardThread,
				MessageBoardThreadSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"messageBoardThread",
								new HashMap<String, Object>() {
									{
										put(
											"messageBoardThreadId",
											messageBoardThread.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/messageBoardThread"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				messageBoardThread,
				MessageBoardThreadSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"messageBoardThread",
									new HashMap<String, Object>() {
										{
											put(
												"messageBoardThreadId",
												messageBoardThread.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/messageBoardThread"))));
	}

	@Test
	public void testGraphQLGetMessageBoardThreadNotFound() throws Exception {
		Long irrelevantMessageBoardThreadId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"messageBoardThread",
						new HashMap<String, Object>() {
							{
								put(
									"messageBoardThreadId",
									irrelevantMessageBoardThreadId);
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
							"messageBoardThread",
							new HashMap<String, Object>() {
								{
									put(
										"messageBoardThreadId",
										irrelevantMessageBoardThreadId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected MessageBoardThread
			testGraphQLGetMessageBoardThread_addMessageBoardThread()
		throws Exception {

		return testGraphQLMessageBoardThread_addMessageBoardThread();
	}

	@Test
	public void testGetMessageBoardThreadPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardThread postMessageBoardThread =
			testGetMessageBoardThreadPermissionsPage_addMessageBoardThread();

		Page<Permission> page =
			messageBoardThreadResource.getMessageBoardThreadPermissionsPage(
				postMessageBoardThread.getId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected MessageBoardThread
			testGetMessageBoardThreadPermissionsPage_addMessageBoardThread()
		throws Exception {

		return messageBoardThreadResource.postSiteMessageBoardThread(
			testGroup.getGroupId(), randomMessageBoardThread());
	}

	@Test
	public void testGraphQLGetMessageBoardThreadPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardThread postMessageBoardThread =
			testGraphQLGetMessageBoardThreadPermissionsPage_addMessageBoardThread();

		GraphQLField graphQLField = new GraphQLField(
			"messageBoardThreadPermissions",
			new HashMap<String, Object>() {
				{
					put("messageBoardThreadId", postMessageBoardThread.getId());
				}
			},
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject messageBoardThreadPermissionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/messageBoardThreadPermissions");

		Assert.assertNotNull(messageBoardThreadPermissionsJSONObject);
	}

	protected MessageBoardThread
			testGraphQLGetMessageBoardThreadPermissionsPage_addMessageBoardThread()
		throws Exception {

		return testGraphQLMessageBoardThread_addMessageBoardThread();
	}

	@Test
	public void testGetMessageBoardThreadsRankedPage() throws Exception {
		Page<MessageBoardThread> page =
			messageBoardThreadResource.getMessageBoardThreadsRankedPage(
				RandomTestUtil.nextDate(), RandomTestUtil.nextDate(), null,
				Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		MessageBoardThread messageBoardThread1 =
			testGetMessageBoardThreadsRankedPage_addMessageBoardThread(
				randomMessageBoardThread());

		MessageBoardThread messageBoardThread2 =
			testGetMessageBoardThreadsRankedPage_addMessageBoardThread(
				randomMessageBoardThread());

		page = messageBoardThreadResource.getMessageBoardThreadsRankedPage(
			null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			messageBoardThread1, (List<MessageBoardThread>)page.getItems());
		assertContains(
			messageBoardThread2, (List<MessageBoardThread>)page.getItems());
		assertValid(
			page, testGetMessageBoardThreadsRankedPage_getExpectedActions());

		messageBoardThreadResource.deleteMessageBoardThread(
			messageBoardThread1.getId());

		messageBoardThreadResource.deleteMessageBoardThread(
			messageBoardThread2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetMessageBoardThreadsRankedPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetMessageBoardThreadsRankedPageWithPagination()
		throws Exception {

		Page<MessageBoardThread> messageBoardThreadsPage =
			messageBoardThreadResource.getMessageBoardThreadsRankedPage(
				null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			messageBoardThreadsPage.getTotalCount());

		MessageBoardThread messageBoardThread1 =
			testGetMessageBoardThreadsRankedPage_addMessageBoardThread(
				randomMessageBoardThread());

		MessageBoardThread messageBoardThread2 =
			testGetMessageBoardThreadsRankedPage_addMessageBoardThread(
				randomMessageBoardThread());

		MessageBoardThread messageBoardThread3 =
			testGetMessageBoardThreadsRankedPage_addMessageBoardThread(
				randomMessageBoardThread());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<MessageBoardThread> page1 =
				messageBoardThreadResource.getMessageBoardThreadsRankedPage(
					null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				messageBoardThread1,
				(List<MessageBoardThread>)page1.getItems());

			Page<MessageBoardThread> page2 =
				messageBoardThreadResource.getMessageBoardThreadsRankedPage(
					null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				messageBoardThread2,
				(List<MessageBoardThread>)page2.getItems());

			Page<MessageBoardThread> page3 =
				messageBoardThreadResource.getMessageBoardThreadsRankedPage(
					null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				messageBoardThread3,
				(List<MessageBoardThread>)page3.getItems());
		}
		else {
			Page<MessageBoardThread> page1 =
				messageBoardThreadResource.getMessageBoardThreadsRankedPage(
					null, null, null, Pagination.of(1, totalCount + 2), null);

			List<MessageBoardThread> messageBoardThreads1 =
				(List<MessageBoardThread>)page1.getItems();

			Assert.assertEquals(
				messageBoardThreads1.toString(), totalCount + 2,
				messageBoardThreads1.size());

			Page<MessageBoardThread> page2 =
				messageBoardThreadResource.getMessageBoardThreadsRankedPage(
					null, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<MessageBoardThread> messageBoardThreads2 =
				(List<MessageBoardThread>)page2.getItems();

			Assert.assertEquals(
				messageBoardThreads2.toString(), 1,
				messageBoardThreads2.size());

			Page<MessageBoardThread> page3 =
				messageBoardThreadResource.getMessageBoardThreadsRankedPage(
					null, null, null, Pagination.of(1, (int)totalCount + 3),
					null);

			assertContains(
				messageBoardThread1,
				(List<MessageBoardThread>)page3.getItems());
			assertContains(
				messageBoardThread2,
				(List<MessageBoardThread>)page3.getItems());
			assertContains(
				messageBoardThread3,
				(List<MessageBoardThread>)page3.getItems());
		}
	}

	@Test
	public void testGetMessageBoardThreadsRankedPageWithSortDateTime()
		throws Exception {

		testGetMessageBoardThreadsRankedPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, messageBoardThread1, messageBoardThread2) -> {
				BeanTestUtil.setProperty(
					messageBoardThread1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetMessageBoardThreadsRankedPageWithSortDouble()
		throws Exception {

		testGetMessageBoardThreadsRankedPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, messageBoardThread1, messageBoardThread2) -> {
				BeanTestUtil.setProperty(
					messageBoardThread1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					messageBoardThread2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetMessageBoardThreadsRankedPageWithSortInteger()
		throws Exception {

		testGetMessageBoardThreadsRankedPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, messageBoardThread1, messageBoardThread2) -> {
				BeanTestUtil.setProperty(
					messageBoardThread1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					messageBoardThread2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetMessageBoardThreadsRankedPageWithSortString()
		throws Exception {

		testGetMessageBoardThreadsRankedPageWithSort(
			EntityField.Type.STRING,
			(entityField, messageBoardThread1, messageBoardThread2) -> {
				Class<?> clazz = messageBoardThread1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						messageBoardThread1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						messageBoardThread2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						messageBoardThread1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						messageBoardThread2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						messageBoardThread1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						messageBoardThread2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetMessageBoardThreadsRankedPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, MessageBoardThread, MessageBoardThread, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		MessageBoardThread messageBoardThread1 = randomMessageBoardThread();
		MessageBoardThread messageBoardThread2 = randomMessageBoardThread();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, messageBoardThread1, messageBoardThread2);
		}

		messageBoardThread1 =
			testGetMessageBoardThreadsRankedPage_addMessageBoardThread(
				messageBoardThread1);

		messageBoardThread2 =
			testGetMessageBoardThreadsRankedPage_addMessageBoardThread(
				messageBoardThread2);

		Page<MessageBoardThread> page =
			messageBoardThreadResource.getMessageBoardThreadsRankedPage(
				null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<MessageBoardThread> ascPage =
				messageBoardThreadResource.getMessageBoardThreadsRankedPage(
					null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				messageBoardThread1,
				(List<MessageBoardThread>)ascPage.getItems());
			assertContains(
				messageBoardThread2,
				(List<MessageBoardThread>)ascPage.getItems());

			Page<MessageBoardThread> descPage =
				messageBoardThreadResource.getMessageBoardThreadsRankedPage(
					null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				messageBoardThread2,
				(List<MessageBoardThread>)descPage.getItems());
			assertContains(
				messageBoardThread1,
				(List<MessageBoardThread>)descPage.getItems());
		}
	}

	protected MessageBoardThread
			testGetMessageBoardThreadsRankedPage_addMessageBoardThread(
				MessageBoardThread messageBoardThread)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetMessageBoardThreadsRankedPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"messageBoardThreadsRanked",
			new HashMap<String, Object>() {
				{
					put(
						"dateCreated",
						getGraphQLValue(RandomTestUtil.nextDate()));
					put(
						"dateModified",
						getGraphQLValue(RandomTestUtil.nextDate()));
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject messageBoardThreadsRankedJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/messageBoardThreadsRanked");

		long totalCount = messageBoardThreadsRankedJSONObject.getLong(
			"totalCount");

		MessageBoardThread messageBoardThread1 =
			testGraphQLGetMessageBoardThreadsRankedPageMessageBoardThread_addMessageBoardThread(
				randomMessageBoardThread());

		MessageBoardThread messageBoardThread2 =
			testGraphQLGetMessageBoardThreadsRankedPageMessageBoardThread_addMessageBoardThread(
				randomMessageBoardThread());

		messageBoardThreadsRankedJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/messageBoardThreadsRanked");

		Assert.assertEquals(
			totalCount + 2,
			messageBoardThreadsRankedJSONObject.getLong("totalCount"));

		assertContains(
			messageBoardThread1,
			Arrays.asList(
				MessageBoardThreadSerDes.toDTOs(
					messageBoardThreadsRankedJSONObject.getString("items"))));
		assertContains(
			messageBoardThread2,
			Arrays.asList(
				MessageBoardThreadSerDes.toDTOs(
					messageBoardThreadsRankedJSONObject.getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		messageBoardThreadsRankedJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessDelivery_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
			"JSONObject/messageBoardThreadsRanked");

		Assert.assertEquals(
			totalCount + 2,
			messageBoardThreadsRankedJSONObject.getLong("totalCount"));

		assertContains(
			messageBoardThread1,
			Arrays.asList(
				MessageBoardThreadSerDes.toDTOs(
					messageBoardThreadsRankedJSONObject.getString("items"))));
		assertContains(
			messageBoardThread2,
			Arrays.asList(
				MessageBoardThreadSerDes.toDTOs(
					messageBoardThreadsRankedJSONObject.getString("items"))));
	}

	protected MessageBoardThread
			testGraphQLGetMessageBoardThreadsRankedPageMessageBoardThread_addMessageBoardThread(
				MessageBoardThread messageBoardThread)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetSiteMessageBoardThreadByFriendlyUrlPath()
		throws Exception {

		MessageBoardThread postMessageBoardThread =
			testGetSiteMessageBoardThreadByFriendlyUrlPath_addMessageBoardThread();

		MessageBoardThread getMessageBoardThread =
			messageBoardThreadResource.
				getSiteMessageBoardThreadByFriendlyUrlPath(
					postMessageBoardThread.getSiteId(),
					postMessageBoardThread.getFriendlyUrlPath());

		assertEquals(postMessageBoardThread, getMessageBoardThread);
		assertValid(getMessageBoardThread);
	}

	protected MessageBoardThread
			testGetSiteMessageBoardThreadByFriendlyUrlPath_addMessageBoardThread()
		throws Exception {

		return messageBoardThreadResource.postSiteMessageBoardThread(
			testGroup.getGroupId(), randomMessageBoardThread());
	}

	@Test
	public void testGraphQLGetSiteMessageBoardThreadByFriendlyUrlPath()
		throws Exception {

		MessageBoardThread messageBoardThread =
			testGraphQLGetSiteMessageBoardThreadByFriendlyUrlPath_addMessageBoardThread();

		// No namespace

		Assert.assertTrue(
			equals(
				messageBoardThread,
				MessageBoardThreadSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"messageBoardThreadByFriendlyUrlPath",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" +
												messageBoardThread.getSiteId() +
													"\"");
										put(
											"friendlyUrlPath",
											"\"" +
												messageBoardThread.
													getFriendlyUrlPath() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/messageBoardThreadByFriendlyUrlPath"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				messageBoardThread,
				MessageBoardThreadSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"messageBoardThreadByFriendlyUrlPath",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													messageBoardThread.
														getSiteId() + "\"");
											put(
												"friendlyUrlPath",
												"\"" +
													messageBoardThread.
														getFriendlyUrlPath() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/messageBoardThreadByFriendlyUrlPath"))));
	}

	@Test
	public void testGraphQLGetSiteMessageBoardThreadByFriendlyUrlPathNotFound()
		throws Exception {

		String irrelevantFriendlyUrlPath =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"messageBoardThreadByFriendlyUrlPath",
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
							"messageBoardThreadByFriendlyUrlPath",
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

	protected MessageBoardThread
			testGraphQLGetSiteMessageBoardThreadByFriendlyUrlPath_addMessageBoardThread()
		throws Exception {

		return testGraphQLSiteMessageBoardThread_addMessageBoardThread();
	}

	@Test
	public void testGetSiteMessageBoardThreadPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardThread postMessageBoardThread =
			testGetSiteMessageBoardThreadPermissionsPage_addMessageBoardThread();

		Page<Permission> page =
			messageBoardThreadResource.getSiteMessageBoardThreadPermissionsPage(
				testGroup.getGroupId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected MessageBoardThread
			testGetSiteMessageBoardThreadPermissionsPage_addMessageBoardThread()
		throws Exception {

		return messageBoardThreadResource.postSiteMessageBoardThread(
			testGroup.getGroupId(), randomMessageBoardThread());
	}

	@Test
	public void testGraphQLGetSiteMessageBoardThreadPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardThread postMessageBoardThread =
			testGraphQLGetSiteMessageBoardThreadPermissionsPage_addMessageBoardThread();

		GraphQLField graphQLField = new GraphQLField(
			"siteMessageBoardThreadPermissions",
			new HashMap<String, Object>() {
				{
					put(
						"siteKey",
						"\"" + postMessageBoardThread.getSiteId() + "\"");
				}
			},
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject siteMessageBoardThreadPermissionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/siteMessageBoardThreadPermissions");

		Assert.assertNotNull(siteMessageBoardThreadPermissionsJSONObject);
	}

	protected MessageBoardThread
			testGraphQLGetSiteMessageBoardThreadPermissionsPage_addMessageBoardThread()
		throws Exception {

		return testGraphQLSiteMessageBoardThread_addMessageBoardThread();
	}

	@Test
	public void testGetSiteMessageBoardThreadsPage() throws Exception {
		Long siteId = testGetSiteMessageBoardThreadsPage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteMessageBoardThreadsPage_getIrrelevantSiteId();

		Page<MessageBoardThread> page =
			messageBoardThreadResource.getSiteMessageBoardThreadsPage(
				siteId, null, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteId != null) {
			MessageBoardThread irrelevantMessageBoardThread =
				testGetSiteMessageBoardThreadsPage_addMessageBoardThread(
					irrelevantSiteId, randomIrrelevantMessageBoardThread());

			page = messageBoardThreadResource.getSiteMessageBoardThreadsPage(
				irrelevantSiteId, null, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantMessageBoardThread,
				(List<MessageBoardThread>)page.getItems());
			assertValid(
				page,
				testGetSiteMessageBoardThreadsPage_getExpectedActions(
					irrelevantSiteId));
		}

		MessageBoardThread messageBoardThread1 =
			testGetSiteMessageBoardThreadsPage_addMessageBoardThread(
				siteId, randomMessageBoardThread());

		MessageBoardThread messageBoardThread2 =
			testGetSiteMessageBoardThreadsPage_addMessageBoardThread(
				siteId, randomMessageBoardThread());

		page = messageBoardThreadResource.getSiteMessageBoardThreadsPage(
			siteId, null, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			messageBoardThread1, (List<MessageBoardThread>)page.getItems());
		assertContains(
			messageBoardThread2, (List<MessageBoardThread>)page.getItems());
		assertValid(
			page,
			testGetSiteMessageBoardThreadsPage_getExpectedActions(siteId));

		messageBoardThreadResource.deleteMessageBoardThread(
			messageBoardThread1.getId());

		messageBoardThreadResource.deleteMessageBoardThread(
			messageBoardThread2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSiteMessageBoardThreadsPage_getExpectedActions(Long siteId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/sites/{siteId}/message-board-threads/batch".
				replace("{siteId}", String.valueOf(siteId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetSiteMessageBoardThreadsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteMessageBoardThreadsPage_getSiteId();

		MessageBoardThread messageBoardThread1 = randomMessageBoardThread();

		messageBoardThread1 =
			testGetSiteMessageBoardThreadsPage_addMessageBoardThread(
				siteId, messageBoardThread1);

		for (EntityField entityField : entityFields) {
			Page<MessageBoardThread> page =
				messageBoardThreadResource.getSiteMessageBoardThreadsPage(
					siteId, null, null, null,
					getFilterString(
						entityField, "between", messageBoardThread1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(messageBoardThread1),
				(List<MessageBoardThread>)page.getItems());
		}
	}

	@Test
	public void testGetSiteMessageBoardThreadsPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteMessageBoardThreadsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteMessageBoardThreadsPageWithFilterStringContains()
		throws Exception {

		testGetSiteMessageBoardThreadsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteMessageBoardThreadsPageWithFilterStringEquals()
		throws Exception {

		testGetSiteMessageBoardThreadsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteMessageBoardThreadsPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteMessageBoardThreadsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetSiteMessageBoardThreadsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteMessageBoardThreadsPage_getSiteId();

		MessageBoardThread messageBoardThread1 =
			testGetSiteMessageBoardThreadsPage_addMessageBoardThread(
				siteId, randomMessageBoardThread());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardThread messageBoardThread2 =
			testGetSiteMessageBoardThreadsPage_addMessageBoardThread(
				siteId, randomMessageBoardThread());

		for (EntityField entityField : entityFields) {
			Page<MessageBoardThread> page =
				messageBoardThreadResource.getSiteMessageBoardThreadsPage(
					siteId, null, null, null,
					getFilterString(entityField, operator, messageBoardThread1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(messageBoardThread1),
				(List<MessageBoardThread>)page.getItems());
		}
	}

	@Test
	public void testGetSiteMessageBoardThreadsPageWithPagination()
		throws Exception {

		Long siteId = testGetSiteMessageBoardThreadsPage_getSiteId();

		Page<MessageBoardThread> messageBoardThreadsPage =
			messageBoardThreadResource.getSiteMessageBoardThreadsPage(
				siteId, null, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			messageBoardThreadsPage.getTotalCount());

		MessageBoardThread messageBoardThread1 =
			testGetSiteMessageBoardThreadsPage_addMessageBoardThread(
				siteId, randomMessageBoardThread());

		MessageBoardThread messageBoardThread2 =
			testGetSiteMessageBoardThreadsPage_addMessageBoardThread(
				siteId, randomMessageBoardThread());

		MessageBoardThread messageBoardThread3 =
			testGetSiteMessageBoardThreadsPage_addMessageBoardThread(
				siteId, randomMessageBoardThread());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<MessageBoardThread> page1 =
				messageBoardThreadResource.getSiteMessageBoardThreadsPage(
					siteId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				messageBoardThread1,
				(List<MessageBoardThread>)page1.getItems());

			Page<MessageBoardThread> page2 =
				messageBoardThreadResource.getSiteMessageBoardThreadsPage(
					siteId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				messageBoardThread2,
				(List<MessageBoardThread>)page2.getItems());

			Page<MessageBoardThread> page3 =
				messageBoardThreadResource.getSiteMessageBoardThreadsPage(
					siteId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				messageBoardThread3,
				(List<MessageBoardThread>)page3.getItems());
		}
		else {
			Page<MessageBoardThread> page1 =
				messageBoardThreadResource.getSiteMessageBoardThreadsPage(
					siteId, null, null, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<MessageBoardThread> messageBoardThreads1 =
				(List<MessageBoardThread>)page1.getItems();

			Assert.assertEquals(
				messageBoardThreads1.toString(), totalCount + 2,
				messageBoardThreads1.size());

			Page<MessageBoardThread> page2 =
				messageBoardThreadResource.getSiteMessageBoardThreadsPage(
					siteId, null, null, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<MessageBoardThread> messageBoardThreads2 =
				(List<MessageBoardThread>)page2.getItems();

			Assert.assertEquals(
				messageBoardThreads2.toString(), 1,
				messageBoardThreads2.size());

			Page<MessageBoardThread> page3 =
				messageBoardThreadResource.getSiteMessageBoardThreadsPage(
					siteId, null, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				messageBoardThread1,
				(List<MessageBoardThread>)page3.getItems());
			assertContains(
				messageBoardThread2,
				(List<MessageBoardThread>)page3.getItems());
			assertContains(
				messageBoardThread3,
				(List<MessageBoardThread>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteMessageBoardThreadsPageWithSortDateTime()
		throws Exception {

		testGetSiteMessageBoardThreadsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, messageBoardThread1, messageBoardThread2) -> {
				BeanTestUtil.setProperty(
					messageBoardThread1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteMessageBoardThreadsPageWithSortDouble()
		throws Exception {

		testGetSiteMessageBoardThreadsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, messageBoardThread1, messageBoardThread2) -> {
				BeanTestUtil.setProperty(
					messageBoardThread1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					messageBoardThread2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteMessageBoardThreadsPageWithSortInteger()
		throws Exception {

		testGetSiteMessageBoardThreadsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, messageBoardThread1, messageBoardThread2) -> {
				BeanTestUtil.setProperty(
					messageBoardThread1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					messageBoardThread2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteMessageBoardThreadsPageWithSortString()
		throws Exception {

		testGetSiteMessageBoardThreadsPageWithSort(
			EntityField.Type.STRING,
			(entityField, messageBoardThread1, messageBoardThread2) -> {
				Class<?> clazz = messageBoardThread1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						messageBoardThread1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						messageBoardThread2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						messageBoardThread1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						messageBoardThread2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						messageBoardThread1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						messageBoardThread2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSiteMessageBoardThreadsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, MessageBoardThread, MessageBoardThread, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteMessageBoardThreadsPage_getSiteId();

		MessageBoardThread messageBoardThread1 = randomMessageBoardThread();
		MessageBoardThread messageBoardThread2 = randomMessageBoardThread();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, messageBoardThread1, messageBoardThread2);
		}

		messageBoardThread1 =
			testGetSiteMessageBoardThreadsPage_addMessageBoardThread(
				siteId, messageBoardThread1);

		messageBoardThread2 =
			testGetSiteMessageBoardThreadsPage_addMessageBoardThread(
				siteId, messageBoardThread2);

		Page<MessageBoardThread> page =
			messageBoardThreadResource.getSiteMessageBoardThreadsPage(
				siteId, null, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<MessageBoardThread> ascPage =
				messageBoardThreadResource.getSiteMessageBoardThreadsPage(
					siteId, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				messageBoardThread1,
				(List<MessageBoardThread>)ascPage.getItems());
			assertContains(
				messageBoardThread2,
				(List<MessageBoardThread>)ascPage.getItems());

			Page<MessageBoardThread> descPage =
				messageBoardThreadResource.getSiteMessageBoardThreadsPage(
					siteId, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				messageBoardThread2,
				(List<MessageBoardThread>)descPage.getItems());
			assertContains(
				messageBoardThread1,
				(List<MessageBoardThread>)descPage.getItems());
		}
	}

	protected MessageBoardThread
			testGetSiteMessageBoardThreadsPage_addMessageBoardThread(
				Long siteId, MessageBoardThread messageBoardThread)
		throws Exception {

		return messageBoardThreadResource.postSiteMessageBoardThread(
			siteId, messageBoardThread);
	}

	protected Long testGetSiteMessageBoardThreadsPage_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected Long testGetSiteMessageBoardThreadsPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	@Test
	public void testGraphQLGetSiteMessageBoardThreadsPage() throws Exception {
		Long siteId = testGetSiteMessageBoardThreadsPage_getSiteId();

		GraphQLField graphQLField = new GraphQLField(
			"messageBoardThreads",
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

		JSONObject messageBoardThreadsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/messageBoardThreads");

		long totalCount = messageBoardThreadsJSONObject.getLong("totalCount");

		MessageBoardThread messageBoardThread1 =
			testGraphQLSiteMessageBoardThread_addMessageBoardThread(
				siteId, randomMessageBoardThread());

		MessageBoardThread messageBoardThread2 =
			testGraphQLSiteMessageBoardThread_addMessageBoardThread(
				siteId, randomMessageBoardThread());

		messageBoardThreadsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/messageBoardThreads");

		Assert.assertEquals(
			totalCount + 2,
			messageBoardThreadsJSONObject.getLong("totalCount"));

		assertContains(
			messageBoardThread1,
			Arrays.asList(
				MessageBoardThreadSerDes.toDTOs(
					messageBoardThreadsJSONObject.getString("items"))));
		assertContains(
			messageBoardThread2,
			Arrays.asList(
				MessageBoardThreadSerDes.toDTOs(
					messageBoardThreadsJSONObject.getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		messageBoardThreadsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessDelivery_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
			"JSONObject/messageBoardThreads");

		Assert.assertEquals(
			totalCount + 2,
			messageBoardThreadsJSONObject.getLong("totalCount"));

		assertContains(
			messageBoardThread1,
			Arrays.asList(
				MessageBoardThreadSerDes.toDTOs(
					messageBoardThreadsJSONObject.getString("items"))));
		assertContains(
			messageBoardThread2,
			Arrays.asList(
				MessageBoardThreadSerDes.toDTOs(
					messageBoardThreadsJSONObject.getString("items"))));
	}

	@Test
	public void testPatchMessageBoardThread() throws Exception {
		MessageBoardThread postMessageBoardThread =
			testPatchMessageBoardThread_addMessageBoardThread();

		MessageBoardThread randomPatchMessageBoardThread =
			randomPatchMessageBoardThread();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardThread patchMessageBoardThread =
			messageBoardThreadResource.patchMessageBoardThread(
				postMessageBoardThread.getId(), randomPatchMessageBoardThread);

		MessageBoardThread expectedPatchMessageBoardThread =
			postMessageBoardThread.clone();

		BeanTestUtil.copyProperties(
			randomPatchMessageBoardThread, expectedPatchMessageBoardThread);

		MessageBoardThread getMessageBoardThread =
			messageBoardThreadResource.getMessageBoardThread(
				patchMessageBoardThread.getId());

		assertEquals(expectedPatchMessageBoardThread, getMessageBoardThread);
		assertValid(getMessageBoardThread);
	}

	protected MessageBoardThread
			testPatchMessageBoardThread_addMessageBoardThread()
		throws Exception {

		return messageBoardThreadResource.postSiteMessageBoardThread(
			testGroup.getGroupId(), randomMessageBoardThread());
	}

	@Test
	public void testPostMessageBoardSectionMessageBoardThread()
		throws Exception {

		MessageBoardThread randomMessageBoardThread =
			randomMessageBoardThread();

		MessageBoardThread postMessageBoardThread =
			testPostMessageBoardSectionMessageBoardThread_addMessageBoardThread(
				randomMessageBoardThread);

		assertEquals(randomMessageBoardThread, postMessageBoardThread);
		assertValid(postMessageBoardThread);
	}

	protected MessageBoardThread
			testPostMessageBoardSectionMessageBoardThread_addMessageBoardThread(
				MessageBoardThread messageBoardThread)
		throws Exception {

		return messageBoardThreadResource.
			postMessageBoardSectionMessageBoardThread(
				testGetMessageBoardSectionMessageBoardThreadsPage_getMessageBoardSectionId(),
				messageBoardThread);
	}

	@Test
	public void testGraphQLPostMessageBoardSectionMessageBoardThread()
		throws Exception {

		MessageBoardThread randomMessageBoardThread =
			randomMessageBoardThread();

		MessageBoardThread messageBoardThread =
			testGraphQLMessageBoardSectionMessageBoardThread_addMessageBoardThread(
				testGraphQLPostMessageBoardSectionMessageBoardThread_getMessageBoardSectionId(
					randomMessageBoardThread),
				randomMessageBoardThread);

		Assert.assertTrue(equals(randomMessageBoardThread, messageBoardThread));
	}

	protected Long
			testGraphQLPostMessageBoardSectionMessageBoardThread_getMessageBoardSectionId(
				MessageBoardThread messageBoardThread)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSiteMessageBoardThread() throws Exception {
		MessageBoardThread randomMessageBoardThread =
			randomMessageBoardThread();

		MessageBoardThread postMessageBoardThread =
			testPostSiteMessageBoardThread_addMessageBoardThread(
				randomMessageBoardThread);

		assertEquals(randomMessageBoardThread, postMessageBoardThread);
		assertValid(postMessageBoardThread);
	}

	protected MessageBoardThread
			testPostSiteMessageBoardThread_addMessageBoardThread(
				MessageBoardThread messageBoardThread)
		throws Exception {

		return messageBoardThreadResource.postSiteMessageBoardThread(
			testGetSiteMessageBoardThreadsPage_getSiteId(), messageBoardThread);
	}

	@Test
	public void testGraphQLPostSiteMessageBoardThread() throws Exception {
		MessageBoardThread randomMessageBoardThread =
			randomMessageBoardThread();

		MessageBoardThread messageBoardThread =
			testGraphQLSiteMessageBoardThread_addMessageBoardThread(
				testGroup.getGroupId(), randomMessageBoardThread);

		Assert.assertTrue(equals(randomMessageBoardThread, messageBoardThread));
	}

	@Test
	public void testPutMessageBoardThread() throws Exception {
		MessageBoardThread postMessageBoardThread =
			testPutMessageBoardThread_addMessageBoardThread();

		MessageBoardThread randomMessageBoardThread =
			randomMessageBoardThread();

		MessageBoardThread putMessageBoardThread =
			messageBoardThreadResource.putMessageBoardThread(
				postMessageBoardThread.getId(), randomMessageBoardThread);

		assertEquals(randomMessageBoardThread, putMessageBoardThread);
		assertValid(putMessageBoardThread);

		MessageBoardThread getMessageBoardThread =
			messageBoardThreadResource.getMessageBoardThread(
				putMessageBoardThread.getId());

		assertEquals(randomMessageBoardThread, getMessageBoardThread);
		assertValid(getMessageBoardThread);
	}

	protected MessageBoardThread
			testPutMessageBoardThread_addMessageBoardThread()
		throws Exception {

		return messageBoardThreadResource.postSiteMessageBoardThread(
			testGroup.getGroupId(), randomMessageBoardThread());
	}

	@Test
	public void testPutMessageBoardThreadPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardThread messageBoardThread =
			testPutMessageBoardThreadPermissionsPage_addMessageBoardThread();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			messageBoardThreadResource.
				putMessageBoardThreadPermissionsPageHttpResponse(
					messageBoardThread.getId(),
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
			messageBoardThreadResource.
				putMessageBoardThreadPermissionsPageHttpResponse(
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

	protected MessageBoardThread
			testPutMessageBoardThreadPermissionsPage_addMessageBoardThread()
		throws Exception {

		return messageBoardThreadResource.postSiteMessageBoardThread(
			testGroup.getGroupId(), randomMessageBoardThread());
	}

	@Test
	public void testPutMessageBoardThreadSubscribe() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardThread messageBoardThread =
			testPutMessageBoardThreadSubscribe_addMessageBoardThread();

		assertHttpResponseStatusCode(
			204,
			messageBoardThreadResource.
				putMessageBoardThreadSubscribeHttpResponse(
					messageBoardThread.getId()));

		assertHttpResponseStatusCode(
			404,
			messageBoardThreadResource.
				putMessageBoardThreadSubscribeHttpResponse(0L));
	}

	protected MessageBoardThread
			testPutMessageBoardThreadSubscribe_addMessageBoardThread()
		throws Exception {

		return messageBoardThreadResource.postSiteMessageBoardThread(
			testGroup.getGroupId(), randomMessageBoardThread());
	}

	@Test
	public void testPutMessageBoardThreadUnsubscribe() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardThread messageBoardThread =
			testPutMessageBoardThreadUnsubscribe_addMessageBoardThread();

		assertHttpResponseStatusCode(
			204,
			messageBoardThreadResource.
				putMessageBoardThreadUnsubscribeHttpResponse(
					messageBoardThread.getId()));

		assertHttpResponseStatusCode(
			404,
			messageBoardThreadResource.
				putMessageBoardThreadUnsubscribeHttpResponse(0L));
	}

	protected MessageBoardThread
			testPutMessageBoardThreadUnsubscribe_addMessageBoardThread()
		throws Exception {

		return messageBoardThreadResource.postSiteMessageBoardThread(
			testGroup.getGroupId(), randomMessageBoardThread());
	}

	@Test
	public void testPutSiteMessageBoardThreadPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		MessageBoardThread messageBoardThread =
			testPutSiteMessageBoardThreadPermissionsPage_addMessageBoardThread();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			messageBoardThreadResource.
				putSiteMessageBoardThreadPermissionsPageHttpResponse(
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
			messageBoardThreadResource.
				putSiteMessageBoardThreadPermissionsPageHttpResponse(
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

	protected MessageBoardThread
			testPutSiteMessageBoardThreadPermissionsPage_addMessageBoardThread()
		throws Exception {

		return messageBoardThreadResource.postSiteMessageBoardThread(
			testGroup.getGroupId(), randomMessageBoardThread());
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		MessageBoardThread messageBoardThread1 =
			testBatchEngineDeleteImportTask_addMessageBoardThread();

		testBatchEngineDeleteImportTask_deleteMessageBoardThread(
			200, null, messageBoardThread1.getId());

		assertHttpResponseStatusCode(
			404,
			messageBoardThreadResource.getMessageBoardThreadHttpResponse(
				messageBoardThread1.getId()));
	}

	protected MessageBoardThread
			testBatchEngineDeleteImportTask_addMessageBoardThread()
		throws Exception {

		return testDeleteMessageBoardThread_addMessageBoardThread();
	}

	protected void testBatchEngineDeleteImportTask_deleteMessageBoardThread(
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
				"com.liferay.headless.delivery.dto.v1_0.MessageBoardThread",
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
	public void testGetMessageBoardThreadMyRating() throws Exception {
		MessageBoardThread postMessageBoardThread =
			testGetMessageBoardThread_addMessageBoardThread();

		Rating postRating = testGetMessageBoardThreadMyRating_addRating(
			postMessageBoardThread.getId(), randomRating());

		Rating getRating =
			messageBoardThreadResource.getMessageBoardThreadMyRating(
				postMessageBoardThread.getId());

		assertEquals(postRating, getRating);
		assertValid(getRating);
	}

	protected Rating testGetMessageBoardThreadMyRating_addRating(
			long messageBoardThreadId, Rating rating)
		throws Exception {

		return messageBoardThreadResource.postMessageBoardThreadMyRating(
			messageBoardThreadId, rating);
	}

	@Test
	public void testPostMessageBoardThreadMyRating() throws Exception {
		Assert.assertTrue(true);
	}

	@Test
	public void testPutMessageBoardThreadMyRating() throws Exception {
		MessageBoardThread postMessageBoardThread =
			testPutMessageBoardThread_addMessageBoardThread();

		testPutMessageBoardThreadMyRating_addRating(
			postMessageBoardThread.getId(), randomRating());

		Rating randomRating = randomRating();

		Rating putRating =
			messageBoardThreadResource.putMessageBoardThreadMyRating(
				postMessageBoardThread.getId(), randomRating);

		assertEquals(randomRating, putRating);
		assertValid(putRating);
	}

	protected Rating testPutMessageBoardThreadMyRating_addRating(
			long messageBoardThreadId, Rating rating)
		throws Exception {

		return messageBoardThreadResource.postMessageBoardThreadMyRating(
			messageBoardThreadId, rating);
	}

	protected MessageBoardThread
			testGraphQLMessageBoardThread_addMessageBoardThread()
		throws Exception {

		return testGraphQLMessageBoardThread_addMessageBoardThread(
			testGroup.getGroupId(), randomMessageBoardThread());
	}

	protected MessageBoardThread
			testGraphQLMessageBoardThread_addMessageBoardThread(
				Long siteId, MessageBoardThread messageBoardThread)
		throws Exception {

		JSONDeserializer<MessageBoardThread> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(MessageBoardThread.class)) {

			if (getGraphQLValue(field.get(messageBoardThread)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(messageBoardThread)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteMessageBoardThread",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("messageBoardThread", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteMessageBoardThread"),
			MessageBoardThread.class);
	}

	protected MessageBoardThread
			testGraphQLMessageBoardSectionMessageBoardThread_addMessageBoardThread()
		throws Exception {

		return testGraphQLMessageBoardSectionMessageBoardThread_addMessageBoardThread(
			testGraphQLMessageBoardSectionMessageBoardThread_getMessageBoardSectionId(),
			randomMessageBoardThread());
	}

	protected Long
			testGraphQLMessageBoardSectionMessageBoardThread_getMessageBoardSectionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected MessageBoardThread
			testGraphQLMessageBoardSectionMessageBoardThread_addMessageBoardThread(
				Long messageBoardSectionId,
				MessageBoardThread messageBoardThread)
		throws Exception {

		JSONDeserializer<MessageBoardThread> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(MessageBoardThread.class)) {

			if (getGraphQLValue(field.get(messageBoardThread)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(messageBoardThread)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createMessageBoardSectionMessageBoardThread",
						new HashMap<String, Object>() {
							{
								put(
									"messageBoardSectionId",
									messageBoardSectionId);
								put("messageBoardThread", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data",
				"JSONObject/createMessageBoardSectionMessageBoardThread"),
			MessageBoardThread.class);
	}

	protected MessageBoardThread
			testGraphQLSiteMessageBoardThread_addMessageBoardThread()
		throws Exception {

		return testGraphQLSiteMessageBoardThread_addMessageBoardThread(
			testGroup.getGroupId(), randomMessageBoardThread());
	}

	protected MessageBoardThread
			testGraphQLSiteMessageBoardThread_addMessageBoardThread(
				Long siteId, MessageBoardThread messageBoardThread)
		throws Exception {

		JSONDeserializer<MessageBoardThread> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(MessageBoardThread.class)) {

			if (getGraphQLValue(field.get(messageBoardThread)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(messageBoardThread)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteMessageBoardThread",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("messageBoardThread", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteMessageBoardThread"),
			MessageBoardThread.class);
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
		MessageBoardThread messageBoardThread,
		List<MessageBoardThread> messageBoardThreads) {

		boolean contains = false;

		for (MessageBoardThread item : messageBoardThreads) {
			if (equals(messageBoardThread, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			messageBoardThreads + " does not contain " + messageBoardThread,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		MessageBoardThread messageBoardThread1,
		MessageBoardThread messageBoardThread2) {

		Assert.assertTrue(
			messageBoardThread1 + " does not equal " + messageBoardThread2,
			equals(messageBoardThread1, messageBoardThread2));
	}

	protected void assertEquals(
		List<MessageBoardThread> messageBoardThreads1,
		List<MessageBoardThread> messageBoardThreads2) {

		Assert.assertEquals(
			messageBoardThreads1.size(), messageBoardThreads2.size());

		for (int i = 0; i < messageBoardThreads1.size(); i++) {
			MessageBoardThread messageBoardThread1 = messageBoardThreads1.get(
				i);
			MessageBoardThread messageBoardThread2 = messageBoardThreads2.get(
				i);

			assertEquals(messageBoardThread1, messageBoardThread2);
		}
	}

	protected void assertEquals(Rating rating1, Rating rating2) {
		Assert.assertTrue(
			rating1 + " does not equal " + rating2, equals(rating1, rating2));
	}

	protected void assertEqualsIgnoringOrder(
		List<MessageBoardThread> messageBoardThreads1,
		List<MessageBoardThread> messageBoardThreads2) {

		Assert.assertEquals(
			messageBoardThreads1.size(), messageBoardThreads2.size());

		for (MessageBoardThread messageBoardThread1 : messageBoardThreads1) {
			boolean contains = false;

			for (MessageBoardThread messageBoardThread2 :
					messageBoardThreads2) {

				if (equals(messageBoardThread1, messageBoardThread2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				messageBoardThreads2 + " does not contain " +
					messageBoardThread1,
				contains);
		}
	}

	protected void assertValid(MessageBoardThread messageBoardThread)
		throws Exception {

		boolean valid = true;

		if (messageBoardThread.getDateCreated() == null) {
			valid = false;
		}

		if (messageBoardThread.getDateModified() == null) {
			valid = false;
		}

		if (messageBoardThread.getId() == null) {
			valid = false;
		}

		if (!Objects.equals(
				messageBoardThread.getSiteId(), testGroup.getGroupId())) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (messageBoardThread.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("aggregateRating", additionalAssertFieldName)) {
				if (messageBoardThread.getAggregateRating() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("articleBody", additionalAssertFieldName)) {
				if (messageBoardThread.getArticleBody() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (messageBoardThread.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"creatorStatistics", additionalAssertFieldName)) {

				if (messageBoardThread.getCreatorStatistics() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (messageBoardThread.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("encodingFormat", additionalAssertFieldName)) {
				if (messageBoardThread.getEncodingFormat() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("friendlyUrlPath", additionalAssertFieldName)) {
				if (messageBoardThread.getFriendlyUrlPath() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("hasValidAnswer", additionalAssertFieldName)) {
				if (messageBoardThread.getHasValidAnswer() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("headline", additionalAssertFieldName)) {
				if (messageBoardThread.getHeadline() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("keywords", additionalAssertFieldName)) {
				if (messageBoardThread.getKeywords() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("lastPostDate", additionalAssertFieldName)) {
				if (messageBoardThread.getLastPostDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("locked", additionalAssertFieldName)) {
				if (messageBoardThread.getLocked() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"messageBoardRootMessageId", additionalAssertFieldName)) {

				if (messageBoardThread.getMessageBoardRootMessageId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"messageBoardSectionId", additionalAssertFieldName)) {

				if (messageBoardThread.getMessageBoardSectionId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfMessageBoardAttachments",
					additionalAssertFieldName)) {

				if (messageBoardThread.getNumberOfMessageBoardAttachments() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfMessageBoardMessages",
					additionalAssertFieldName)) {

				if (messageBoardThread.getNumberOfMessageBoardMessages() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("relatedContents", additionalAssertFieldName)) {
				if (messageBoardThread.getRelatedContents() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("seen", additionalAssertFieldName)) {
				if (messageBoardThread.getSeen() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("showAsQuestion", additionalAssertFieldName)) {
				if (messageBoardThread.getShowAsQuestion() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (messageBoardThread.getStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("subscribed", additionalAssertFieldName)) {
				if (messageBoardThread.getSubscribed() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryBriefs", additionalAssertFieldName)) {

				if (messageBoardThread.getTaxonomyCategoryBriefs() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryIds", additionalAssertFieldName)) {

				if (messageBoardThread.getTaxonomyCategoryIds() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("threadType", additionalAssertFieldName)) {
				if (messageBoardThread.getThreadType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("viewCount", additionalAssertFieldName)) {
				if (messageBoardThread.getViewCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (messageBoardThread.getViewableBy() == null) {
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

	protected void assertValid(Page<MessageBoardThread> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<MessageBoardThread> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<MessageBoardThread> messageBoardThreads =
			page.getItems();

		int size = messageBoardThreads.size();

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

		graphQLFields.add(new GraphQLField("id"));

		graphQLFields.add(new GraphQLField("siteId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.delivery.dto.v1_0.MessageBoardThread.
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
		MessageBoardThread messageBoardThread1,
		MessageBoardThread messageBoardThread2) {

		if (messageBoardThread1 == messageBoardThread2) {
			return true;
		}

		if (!Objects.equals(
				messageBoardThread1.getSiteId(),
				messageBoardThread2.getSiteId())) {

			return false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)messageBoardThread1.getActions(),
						(Map)messageBoardThread2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("aggregateRating", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardThread1.getAggregateRating(),
						messageBoardThread2.getAggregateRating())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("articleBody", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardThread1.getArticleBody(),
						messageBoardThread2.getArticleBody())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardThread1.getCreator(),
						messageBoardThread2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"creatorStatistics", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						messageBoardThread1.getCreatorStatistics(),
						messageBoardThread2.getCreatorStatistics())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardThread1.getCustomFields(),
						messageBoardThread2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardThread1.getDateCreated(),
						messageBoardThread2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardThread1.getDateModified(),
						messageBoardThread2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("encodingFormat", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardThread1.getEncodingFormat(),
						messageBoardThread2.getEncodingFormat())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("friendlyUrlPath", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardThread1.getFriendlyUrlPath(),
						messageBoardThread2.getFriendlyUrlPath())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("hasValidAnswer", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardThread1.getHasValidAnswer(),
						messageBoardThread2.getHasValidAnswer())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("headline", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardThread1.getHeadline(),
						messageBoardThread2.getHeadline())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardThread1.getId(),
						messageBoardThread2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("keywords", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardThread1.getKeywords(),
						messageBoardThread2.getKeywords())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("lastPostDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardThread1.getLastPostDate(),
						messageBoardThread2.getLastPostDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("locked", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardThread1.getLocked(),
						messageBoardThread2.getLocked())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"messageBoardRootMessageId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						messageBoardThread1.getMessageBoardRootMessageId(),
						messageBoardThread2.getMessageBoardRootMessageId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"messageBoardSectionId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						messageBoardThread1.getMessageBoardSectionId(),
						messageBoardThread2.getMessageBoardSectionId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfMessageBoardAttachments",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						messageBoardThread1.
							getNumberOfMessageBoardAttachments(),
						messageBoardThread2.
							getNumberOfMessageBoardAttachments())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfMessageBoardMessages",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						messageBoardThread1.getNumberOfMessageBoardMessages(),
						messageBoardThread2.
							getNumberOfMessageBoardMessages())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("relatedContents", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardThread1.getRelatedContents(),
						messageBoardThread2.getRelatedContents())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("seen", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardThread1.getSeen(),
						messageBoardThread2.getSeen())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("showAsQuestion", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardThread1.getShowAsQuestion(),
						messageBoardThread2.getShowAsQuestion())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardThread1.getStatus(),
						messageBoardThread2.getStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("subscribed", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardThread1.getSubscribed(),
						messageBoardThread2.getSubscribed())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryBriefs", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						messageBoardThread1.getTaxonomyCategoryBriefs(),
						messageBoardThread2.getTaxonomyCategoryBriefs())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryIds", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						messageBoardThread1.getTaxonomyCategoryIds(),
						messageBoardThread2.getTaxonomyCategoryIds())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("threadType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardThread1.getThreadType(),
						messageBoardThread2.getThreadType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("viewCount", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardThread1.getViewCount(),
						messageBoardThread2.getViewCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						messageBoardThread1.getViewableBy(),
						messageBoardThread2.getViewableBy())) {

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

		if (!(_messageBoardThreadResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_messageBoardThreadResource;

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
		MessageBoardThread messageBoardThread) {

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
			Object object = messageBoardThread.getArticleBody();

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
				Date date = messageBoardThread.getDateCreated();

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

				sb.append(_format.format(messageBoardThread.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = messageBoardThread.getDateModified();

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

				sb.append(_format.format(messageBoardThread.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("encodingFormat")) {
			Object object = messageBoardThread.getEncodingFormat();

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
			Object object = messageBoardThread.getFriendlyUrlPath();

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

		if (entityFieldName.equals("hasValidAnswer")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("headline")) {
			Object object = messageBoardThread.getHeadline();

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

		if (entityFieldName.equals("lastPostDate")) {
			if (operator.equals("between")) {
				Date date = messageBoardThread.getLastPostDate();

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

				sb.append(_format.format(messageBoardThread.getLastPostDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("locked")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("messageBoardRootMessageId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("messageBoardSectionId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("numberOfMessageBoardAttachments")) {
			sb.append(
				String.valueOf(
					messageBoardThread.getNumberOfMessageBoardAttachments()));

			return sb.toString();
		}

		if (entityFieldName.equals("numberOfMessageBoardMessages")) {
			sb.append(
				String.valueOf(
					messageBoardThread.getNumberOfMessageBoardMessages()));

			return sb.toString();
		}

		if (entityFieldName.equals("relatedContents")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("seen")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("showAsQuestion")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("siteId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("status")) {
			Object object = messageBoardThread.getStatus();

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

		if (entityFieldName.equals("taxonomyCategoryBriefs")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("taxonomyCategoryIds")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("threadType")) {
			Object object = messageBoardThread.getThreadType();

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

		if (entityFieldName.equals("viewCount")) {
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

	protected MessageBoardThread randomMessageBoardThread() throws Exception {
		return new MessageBoardThread() {
			{
				articleBody = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				encodingFormat = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				friendlyUrlPath = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				hasValidAnswer = RandomTestUtil.randomBoolean();
				headline = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				lastPostDate = RandomTestUtil.nextDate();
				locked = RandomTestUtil.randomBoolean();
				messageBoardRootMessageId = RandomTestUtil.randomLong();
				messageBoardSectionId = RandomTestUtil.randomLong();
				numberOfMessageBoardAttachments = RandomTestUtil.randomInt();
				numberOfMessageBoardMessages = RandomTestUtil.randomInt();
				seen = RandomTestUtil.randomBoolean();
				showAsQuestion = RandomTestUtil.randomBoolean();
				siteId = testGroup.getGroupId();
				status = StringUtil.toLowerCase(RandomTestUtil.randomString());
				subscribed = RandomTestUtil.randomBoolean();
				threadType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				viewCount = RandomTestUtil.randomLong();
			}
		};
	}

	protected MessageBoardThread randomIrrelevantMessageBoardThread()
		throws Exception {

		MessageBoardThread randomIrrelevantMessageBoardThread =
			randomMessageBoardThread();

		randomIrrelevantMessageBoardThread.setSiteId(
			irrelevantGroup.getGroupId());

		return randomIrrelevantMessageBoardThread;
	}

	protected MessageBoardThread randomPatchMessageBoardThread()
		throws Exception {

		return randomMessageBoardThread();
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

	protected MessageBoardThreadResource messageBoardThreadResource;
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
		LogFactoryUtil.getLog(BaseMessageBoardThreadResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.delivery.resource.v1_0.MessageBoardThreadResource
			_messageBoardThreadResource;

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