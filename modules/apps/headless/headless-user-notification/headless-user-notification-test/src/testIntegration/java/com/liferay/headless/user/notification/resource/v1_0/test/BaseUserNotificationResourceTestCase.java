/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.user.notification.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.user.notification.client.dto.v1_0.UserNotification;
import com.liferay.headless.user.notification.client.http.HttpInvoker;
import com.liferay.headless.user.notification.client.pagination.Page;
import com.liferay.headless.user.notification.client.pagination.Pagination;
import com.liferay.headless.user.notification.client.resource.v1_0.UserNotificationResource;
import com.liferay.headless.user.notification.client.serdes.v1_0.UserNotificationSerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
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
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PropsValues;
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
 * @author Carlos Correa
 * @generated
 */
@Generated("")
public abstract class BaseUserNotificationResourceTestCase {

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

		_userNotificationResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		userNotificationResource = UserNotificationResource.builder(
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

		UserNotification userNotification1 = randomUserNotification();

		String json = objectMapper.writeValueAsString(userNotification1);

		UserNotification userNotification2 = UserNotificationSerDes.toDTO(json);

		Assert.assertTrue(equals(userNotification1, userNotification2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		UserNotification userNotification = randomUserNotification();

		String json1 = objectMapper.writeValueAsString(userNotification);
		String json2 = UserNotificationSerDes.toJSON(userNotification);

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

		UserNotification userNotification = randomUserNotification();

		userNotification.setMessage(regex);

		String json = UserNotificationSerDes.toJSON(userNotification);

		Assert.assertFalse(json.contains(regex));

		userNotification = UserNotificationSerDes.toDTO(json);

		Assert.assertEquals(regex, userNotification.getMessage());
	}

	@Test
	public void testGetMyUserNotificationsPage() throws Exception {
		Page<UserNotification> page =
			userNotificationResource.getMyUserNotificationsPage(
				null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		UserNotification userNotification1 =
			testGetMyUserNotificationsPage_addUserNotification(
				randomUserNotification());

		UserNotification userNotification2 =
			testGetMyUserNotificationsPage_addUserNotification(
				randomUserNotification());

		page = userNotificationResource.getMyUserNotificationsPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			userNotification1, (List<UserNotification>)page.getItems());
		assertContains(
			userNotification2, (List<UserNotification>)page.getItems());
		assertValid(page, testGetMyUserNotificationsPage_getExpectedActions());
	}

	protected Map<String, Map<String, String>>
			testGetMyUserNotificationsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetMyUserNotificationsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		UserNotification userNotification1 = randomUserNotification();

		userNotification1 = testGetMyUserNotificationsPage_addUserNotification(
			userNotification1);

		for (EntityField entityField : entityFields) {
			Page<UserNotification> page =
				userNotificationResource.getMyUserNotificationsPage(
					null,
					getFilterString(entityField, "between", userNotification1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(userNotification1),
				(List<UserNotification>)page.getItems());
		}
	}

	@Test
	public void testGetMyUserNotificationsPageWithFilterDoubleEquals()
		throws Exception {

		testGetMyUserNotificationsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetMyUserNotificationsPageWithFilterStringContains()
		throws Exception {

		testGetMyUserNotificationsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetMyUserNotificationsPageWithFilterStringEquals()
		throws Exception {

		testGetMyUserNotificationsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetMyUserNotificationsPageWithFilterStringStartsWith()
		throws Exception {

		testGetMyUserNotificationsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetMyUserNotificationsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		UserNotification userNotification1 =
			testGetMyUserNotificationsPage_addUserNotification(
				randomUserNotification());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserNotification userNotification2 =
			testGetMyUserNotificationsPage_addUserNotification(
				randomUserNotification());

		for (EntityField entityField : entityFields) {
			Page<UserNotification> page =
				userNotificationResource.getMyUserNotificationsPage(
					null,
					getFilterString(entityField, operator, userNotification1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(userNotification1),
				(List<UserNotification>)page.getItems());
		}
	}

	@Test
	public void testGetMyUserNotificationsPageWithPagination()
		throws Exception {

		Page<UserNotification> userNotificationsPage =
			userNotificationResource.getMyUserNotificationsPage(
				null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			userNotificationsPage.getTotalCount());

		UserNotification userNotification1 =
			testGetMyUserNotificationsPage_addUserNotification(
				randomUserNotification());

		UserNotification userNotification2 =
			testGetMyUserNotificationsPage_addUserNotification(
				randomUserNotification());

		UserNotification userNotification3 =
			testGetMyUserNotificationsPage_addUserNotification(
				randomUserNotification());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<UserNotification> page1 =
				userNotificationResource.getMyUserNotificationsPage(
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				userNotification1, (List<UserNotification>)page1.getItems());

			Page<UserNotification> page2 =
				userNotificationResource.getMyUserNotificationsPage(
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				userNotification2, (List<UserNotification>)page2.getItems());

			Page<UserNotification> page3 =
				userNotificationResource.getMyUserNotificationsPage(
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				userNotification3, (List<UserNotification>)page3.getItems());
		}
		else {
			Page<UserNotification> page1 =
				userNotificationResource.getMyUserNotificationsPage(
					null, null, Pagination.of(1, totalCount + 2), null);

			List<UserNotification> userNotifications1 =
				(List<UserNotification>)page1.getItems();

			Assert.assertEquals(
				userNotifications1.toString(), totalCount + 2,
				userNotifications1.size());

			Page<UserNotification> page2 =
				userNotificationResource.getMyUserNotificationsPage(
					null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<UserNotification> userNotifications2 =
				(List<UserNotification>)page2.getItems();

			Assert.assertEquals(
				userNotifications2.toString(), 1, userNotifications2.size());

			Page<UserNotification> page3 =
				userNotificationResource.getMyUserNotificationsPage(
					null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				userNotification1, (List<UserNotification>)page3.getItems());
			assertContains(
				userNotification2, (List<UserNotification>)page3.getItems());
			assertContains(
				userNotification3, (List<UserNotification>)page3.getItems());
		}
	}

	@Test
	public void testGetMyUserNotificationsPageWithSortDateTime()
		throws Exception {

		testGetMyUserNotificationsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, userNotification1, userNotification2) -> {
				BeanTestUtil.setProperty(
					userNotification1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetMyUserNotificationsPageWithSortDouble()
		throws Exception {

		testGetMyUserNotificationsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, userNotification1, userNotification2) -> {
				BeanTestUtil.setProperty(
					userNotification1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					userNotification2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetMyUserNotificationsPageWithSortInteger()
		throws Exception {

		testGetMyUserNotificationsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, userNotification1, userNotification2) -> {
				BeanTestUtil.setProperty(
					userNotification1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					userNotification2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetMyUserNotificationsPageWithSortString()
		throws Exception {

		testGetMyUserNotificationsPageWithSort(
			EntityField.Type.STRING,
			(entityField, userNotification1, userNotification2) -> {
				Class<?> clazz = userNotification1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						userNotification1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						userNotification2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						userNotification1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						userNotification2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						userNotification1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						userNotification2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetMyUserNotificationsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, UserNotification, UserNotification, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		UserNotification userNotification1 = randomUserNotification();
		UserNotification userNotification2 = randomUserNotification();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, userNotification1, userNotification2);
		}

		userNotification1 = testGetMyUserNotificationsPage_addUserNotification(
			userNotification1);

		userNotification2 = testGetMyUserNotificationsPage_addUserNotification(
			userNotification2);

		Page<UserNotification> page =
			userNotificationResource.getMyUserNotificationsPage(
				null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<UserNotification> ascPage =
				userNotificationResource.getMyUserNotificationsPage(
					null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				userNotification1, (List<UserNotification>)ascPage.getItems());
			assertContains(
				userNotification2, (List<UserNotification>)ascPage.getItems());

			Page<UserNotification> descPage =
				userNotificationResource.getMyUserNotificationsPage(
					null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				userNotification2, (List<UserNotification>)descPage.getItems());
			assertContains(
				userNotification1, (List<UserNotification>)descPage.getItems());
		}
	}

	protected UserNotification
			testGetMyUserNotificationsPage_addUserNotification(
				UserNotification userNotification)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetUserAccountUserNotificationsPage() throws Exception {
		Long userAccountId =
			testGetUserAccountUserNotificationsPage_getUserAccountId();
		Long irrelevantUserAccountId =
			testGetUserAccountUserNotificationsPage_getIrrelevantUserAccountId();

		Page<UserNotification> page =
			userNotificationResource.getUserAccountUserNotificationsPage(
				userAccountId, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantUserAccountId != null) {
			UserNotification irrelevantUserNotification =
				testGetUserAccountUserNotificationsPage_addUserNotification(
					irrelevantUserAccountId,
					randomIrrelevantUserNotification());

			page = userNotificationResource.getUserAccountUserNotificationsPage(
				irrelevantUserAccountId, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantUserNotification,
				(List<UserNotification>)page.getItems());
			assertValid(
				page,
				testGetUserAccountUserNotificationsPage_getExpectedActions(
					irrelevantUserAccountId));
		}

		UserNotification userNotification1 =
			testGetUserAccountUserNotificationsPage_addUserNotification(
				userAccountId, randomUserNotification());

		UserNotification userNotification2 =
			testGetUserAccountUserNotificationsPage_addUserNotification(
				userAccountId, randomUserNotification());

		page = userNotificationResource.getUserAccountUserNotificationsPage(
			userAccountId, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			userNotification1, (List<UserNotification>)page.getItems());
		assertContains(
			userNotification2, (List<UserNotification>)page.getItems());
		assertValid(
			page,
			testGetUserAccountUserNotificationsPage_getExpectedActions(
				userAccountId));
	}

	protected Map<String, Map<String, String>>
			testGetUserAccountUserNotificationsPage_getExpectedActions(
				Long userAccountId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetUserAccountUserNotificationsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long userAccountId =
			testGetUserAccountUserNotificationsPage_getUserAccountId();

		UserNotification userNotification1 = randomUserNotification();

		userNotification1 =
			testGetUserAccountUserNotificationsPage_addUserNotification(
				userAccountId, userNotification1);

		for (EntityField entityField : entityFields) {
			Page<UserNotification> page =
				userNotificationResource.getUserAccountUserNotificationsPage(
					userAccountId, null,
					getFilterString(entityField, "between", userNotification1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(userNotification1),
				(List<UserNotification>)page.getItems());
		}
	}

	@Test
	public void testGetUserAccountUserNotificationsPageWithFilterDoubleEquals()
		throws Exception {

		testGetUserAccountUserNotificationsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetUserAccountUserNotificationsPageWithFilterStringContains()
		throws Exception {

		testGetUserAccountUserNotificationsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetUserAccountUserNotificationsPageWithFilterStringEquals()
		throws Exception {

		testGetUserAccountUserNotificationsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetUserAccountUserNotificationsPageWithFilterStringStartsWith()
		throws Exception {

		testGetUserAccountUserNotificationsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetUserAccountUserNotificationsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long userAccountId =
			testGetUserAccountUserNotificationsPage_getUserAccountId();

		UserNotification userNotification1 =
			testGetUserAccountUserNotificationsPage_addUserNotification(
				userAccountId, randomUserNotification());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserNotification userNotification2 =
			testGetUserAccountUserNotificationsPage_addUserNotification(
				userAccountId, randomUserNotification());

		for (EntityField entityField : entityFields) {
			Page<UserNotification> page =
				userNotificationResource.getUserAccountUserNotificationsPage(
					userAccountId, null,
					getFilterString(entityField, operator, userNotification1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(userNotification1),
				(List<UserNotification>)page.getItems());
		}
	}

	@Test
	public void testGetUserAccountUserNotificationsPageWithPagination()
		throws Exception {

		Long userAccountId =
			testGetUserAccountUserNotificationsPage_getUserAccountId();

		Page<UserNotification> userNotificationsPage =
			userNotificationResource.getUserAccountUserNotificationsPage(
				userAccountId, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			userNotificationsPage.getTotalCount());

		UserNotification userNotification1 =
			testGetUserAccountUserNotificationsPage_addUserNotification(
				userAccountId, randomUserNotification());

		UserNotification userNotification2 =
			testGetUserAccountUserNotificationsPage_addUserNotification(
				userAccountId, randomUserNotification());

		UserNotification userNotification3 =
			testGetUserAccountUserNotificationsPage_addUserNotification(
				userAccountId, randomUserNotification());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<UserNotification> page1 =
				userNotificationResource.getUserAccountUserNotificationsPage(
					userAccountId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				userNotification1, (List<UserNotification>)page1.getItems());

			Page<UserNotification> page2 =
				userNotificationResource.getUserAccountUserNotificationsPage(
					userAccountId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				userNotification2, (List<UserNotification>)page2.getItems());

			Page<UserNotification> page3 =
				userNotificationResource.getUserAccountUserNotificationsPage(
					userAccountId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				userNotification3, (List<UserNotification>)page3.getItems());
		}
		else {
			Page<UserNotification> page1 =
				userNotificationResource.getUserAccountUserNotificationsPage(
					userAccountId, null, null, Pagination.of(1, totalCount + 2),
					null);

			List<UserNotification> userNotifications1 =
				(List<UserNotification>)page1.getItems();

			Assert.assertEquals(
				userNotifications1.toString(), totalCount + 2,
				userNotifications1.size());

			Page<UserNotification> page2 =
				userNotificationResource.getUserAccountUserNotificationsPage(
					userAccountId, null, null, Pagination.of(2, totalCount + 2),
					null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<UserNotification> userNotifications2 =
				(List<UserNotification>)page2.getItems();

			Assert.assertEquals(
				userNotifications2.toString(), 1, userNotifications2.size());

			Page<UserNotification> page3 =
				userNotificationResource.getUserAccountUserNotificationsPage(
					userAccountId, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				userNotification1, (List<UserNotification>)page3.getItems());
			assertContains(
				userNotification2, (List<UserNotification>)page3.getItems());
			assertContains(
				userNotification3, (List<UserNotification>)page3.getItems());
		}
	}

	@Test
	public void testGetUserAccountUserNotificationsPageWithSortDateTime()
		throws Exception {

		testGetUserAccountUserNotificationsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, userNotification1, userNotification2) -> {
				BeanTestUtil.setProperty(
					userNotification1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetUserAccountUserNotificationsPageWithSortDouble()
		throws Exception {

		testGetUserAccountUserNotificationsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, userNotification1, userNotification2) -> {
				BeanTestUtil.setProperty(
					userNotification1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					userNotification2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetUserAccountUserNotificationsPageWithSortInteger()
		throws Exception {

		testGetUserAccountUserNotificationsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, userNotification1, userNotification2) -> {
				BeanTestUtil.setProperty(
					userNotification1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					userNotification2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetUserAccountUserNotificationsPageWithSortString()
		throws Exception {

		testGetUserAccountUserNotificationsPageWithSort(
			EntityField.Type.STRING,
			(entityField, userNotification1, userNotification2) -> {
				Class<?> clazz = userNotification1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						userNotification1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						userNotification2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						userNotification1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						userNotification2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						userNotification1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						userNotification2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetUserAccountUserNotificationsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, UserNotification, UserNotification, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long userAccountId =
			testGetUserAccountUserNotificationsPage_getUserAccountId();

		UserNotification userNotification1 = randomUserNotification();
		UserNotification userNotification2 = randomUserNotification();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, userNotification1, userNotification2);
		}

		userNotification1 =
			testGetUserAccountUserNotificationsPage_addUserNotification(
				userAccountId, userNotification1);

		userNotification2 =
			testGetUserAccountUserNotificationsPage_addUserNotification(
				userAccountId, userNotification2);

		Page<UserNotification> page =
			userNotificationResource.getUserAccountUserNotificationsPage(
				userAccountId, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<UserNotification> ascPage =
				userNotificationResource.getUserAccountUserNotificationsPage(
					userAccountId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				userNotification1, (List<UserNotification>)ascPage.getItems());
			assertContains(
				userNotification2, (List<UserNotification>)ascPage.getItems());

			Page<UserNotification> descPage =
				userNotificationResource.getUserAccountUserNotificationsPage(
					userAccountId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				userNotification2, (List<UserNotification>)descPage.getItems());
			assertContains(
				userNotification1, (List<UserNotification>)descPage.getItems());
		}
	}

	protected UserNotification
			testGetUserAccountUserNotificationsPage_addUserNotification(
				Long userAccountId, UserNotification userNotification)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetUserAccountUserNotificationsPage_getUserAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetUserAccountUserNotificationsPage_getIrrelevantUserAccountId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetUserNotification() throws Exception {
		UserNotification postUserNotification =
			testGetUserNotification_addUserNotification();

		UserNotification getUserNotification =
			userNotificationResource.getUserNotification(
				postUserNotification.getId());

		assertEquals(postUserNotification, getUserNotification);
		assertValid(getUserNotification);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		UserNotification postUserNotification =
			testGetUserNotification_addUserNotification();

		UserNotification getUserNotification =
			userNotificationResource.getUserNotification(
				postUserNotification.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.user.notification.dto.v1_0.UserNotification"
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
			postUserNotification.getId());

		assertEquals(
			getUserNotification, UserNotificationSerDes.toDTO(item.toString()));
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

	protected UserNotification testGetUserNotification_addUserNotification()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetUserNotification() throws Exception {
		UserNotification userNotification =
			testGraphQLGetUserNotification_addUserNotification();

		// No namespace

		Assert.assertTrue(
			equals(
				userNotification,
				UserNotificationSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"userNotification",
								new HashMap<String, Object>() {
									{
										put(
											"userNotificationId",
											userNotification.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/userNotification"))));

		// Using the namespace headlessUserNotification_v1_0

		Assert.assertTrue(
			equals(
				userNotification,
				UserNotificationSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessUserNotification_v1_0",
								new GraphQLField(
									"userNotification",
									new HashMap<String, Object>() {
										{
											put(
												"userNotificationId",
												userNotification.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessUserNotification_v1_0",
						"Object/userNotification"))));
	}

	@Test
	public void testGraphQLGetUserNotificationNotFound() throws Exception {
		Long irrelevantUserNotificationId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"userNotification",
						new HashMap<String, Object>() {
							{
								put(
									"userNotificationId",
									irrelevantUserNotificationId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessUserNotification_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessUserNotification_v1_0",
						new GraphQLField(
							"userNotification",
							new HashMap<String, Object>() {
								{
									put(
										"userNotificationId",
										irrelevantUserNotificationId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected UserNotification
			testGraphQLGetUserNotification_addUserNotification()
		throws Exception {

		return testGraphQLUserNotification_addUserNotification();
	}

	@Test
	public void testPutUserNotificationRead() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserNotification userNotification =
			testPutUserNotificationRead_addUserNotification();

		assertHttpResponseStatusCode(
			204,
			userNotificationResource.putUserNotificationReadHttpResponse(
				userNotification.getId()));

		assertHttpResponseStatusCode(
			404,
			userNotificationResource.putUserNotificationReadHttpResponse(0L));
	}

	protected UserNotification testPutUserNotificationRead_addUserNotification()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutUserNotificationUnread() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserNotification userNotification =
			testPutUserNotificationUnread_addUserNotification();

		assertHttpResponseStatusCode(
			204,
			userNotificationResource.putUserNotificationUnreadHttpResponse(
				userNotification.getId()));

		assertHttpResponseStatusCode(
			404,
			userNotificationResource.putUserNotificationUnreadHttpResponse(0L));
	}

	protected UserNotification
			testPutUserNotificationUnread_addUserNotification()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected UserNotification testGraphQLUserNotification_addUserNotification()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		UserNotification userNotification,
		List<UserNotification> userNotifications) {

		boolean contains = false;

		for (UserNotification item : userNotifications) {
			if (equals(userNotification, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			userNotifications + " does not contain " + userNotification,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		UserNotification userNotification1,
		UserNotification userNotification2) {

		Assert.assertTrue(
			userNotification1 + " does not equal " + userNotification2,
			equals(userNotification1, userNotification2));
	}

	protected void assertEquals(
		List<UserNotification> userNotifications1,
		List<UserNotification> userNotifications2) {

		Assert.assertEquals(
			userNotifications1.size(), userNotifications2.size());

		for (int i = 0; i < userNotifications1.size(); i++) {
			UserNotification userNotification1 = userNotifications1.get(i);
			UserNotification userNotification2 = userNotifications2.get(i);

			assertEquals(userNotification1, userNotification2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<UserNotification> userNotifications1,
		List<UserNotification> userNotifications2) {

		Assert.assertEquals(
			userNotifications1.size(), userNotifications2.size());

		for (UserNotification userNotification1 : userNotifications1) {
			boolean contains = false;

			for (UserNotification userNotification2 : userNotifications2) {
				if (equals(userNotification1, userNotification2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				userNotifications2 + " does not contain " + userNotification1,
				contains);
		}
	}

	protected void assertValid(UserNotification userNotification)
		throws Exception {

		boolean valid = true;

		if (userNotification.getDateCreated() == null) {
			valid = false;
		}

		if (userNotification.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (userNotification.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("message", additionalAssertFieldName)) {
				if (userNotification.getMessage() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("read", additionalAssertFieldName)) {
				if (userNotification.getRead() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (userNotification.getType() == null) {
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

	protected void assertValid(Page<UserNotification> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<UserNotification> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<UserNotification> userNotifications =
			page.getItems();

		int size = userNotifications.size();

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
					com.liferay.headless.user.notification.dto.v1_0.
						UserNotification.class)) {

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
		UserNotification userNotification1,
		UserNotification userNotification2) {

		if (userNotification1 == userNotification2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)userNotification1.getActions(),
						(Map)userNotification2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userNotification1.getDateCreated(),
						userNotification2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userNotification1.getId(), userNotification2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("message", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userNotification1.getMessage(),
						userNotification2.getMessage())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("read", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userNotification1.getRead(),
						userNotification2.getRead())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userNotification1.getType(),
						userNotification2.getType())) {

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

		if (!(_userNotificationResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_userNotificationResource;

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
		UserNotification userNotification) {

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

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = userNotification.getDateCreated();

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

				sb.append(_format.format(userNotification.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("message")) {
			Object object = userNotification.getMessage();

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

		if (entityFieldName.equals("read")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("type")) {
			sb.append(String.valueOf(userNotification.getType()));

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

	protected UserNotification randomUserNotification() throws Exception {
		return new UserNotification() {
			{
				dateCreated = RandomTestUtil.nextDate();
				id = RandomTestUtil.randomLong();
				message = StringUtil.toLowerCase(RandomTestUtil.randomString());
				read = RandomTestUtil.randomBoolean();
				type = RandomTestUtil.randomInt();
			}
		};
	}

	protected UserNotification randomIrrelevantUserNotification()
		throws Exception {

		UserNotification randomIrrelevantUserNotification =
			randomUserNotification();

		return randomIrrelevantUserNotification;
	}

	protected UserNotification randomPatchUserNotification() throws Exception {
		return randomUserNotification();
	}

	protected UserNotificationResource userNotificationResource;
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
		LogFactoryUtil.getLog(BaseUserNotificationResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.user.notification.resource.v1_0.
		UserNotificationResource _userNotificationResource;

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