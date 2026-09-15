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

import com.liferay.osb.faro.rest.client.dto.v1_0.UserSession;
import com.liferay.osb.faro.rest.client.http.HttpInvoker;
import com.liferay.osb.faro.rest.client.pagination.Page;
import com.liferay.osb.faro.rest.client.pagination.Pagination;
import com.liferay.osb.faro.rest.client.resource.v1_0.UserSessionResource;
import com.liferay.osb.faro.rest.client.serdes.v1_0.UserSessionSerDes;
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
public abstract class BaseUserSessionResourceTestCase {

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

		_userSessionResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		userSessionResource = UserSessionResource.builder(
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

		UserSession userSession1 = randomUserSession();

		String json = objectMapper.writeValueAsString(userSession1);

		UserSession userSession2 = UserSessionSerDes.toDTO(json);

		Assert.assertTrue(equals(userSession1, userSession2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		UserSession userSession = randomUserSession();

		String json1 = objectMapper.writeValueAsString(userSession);
		String json2 = UserSessionSerDes.toJSON(userSession);

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

		UserSession userSession = randomUserSession();

		userSession.setBrowserName(regex);
		userSession.setDeviceType(regex);

		String json = UserSessionSerDes.toJSON(userSession);

		Assert.assertFalse(json.contains(regex));

		userSession = UserSessionSerDes.toDTO(json);

		Assert.assertEquals(regex, userSession.getBrowserName());
		Assert.assertEquals(regex, userSession.getDeviceType());
	}

	@Test
	public void testGetWorkspaceGroupChannelAccountUserSessionsPage()
		throws Exception {

		Long groupId =
			testGetWorkspaceGroupChannelAccountUserSessionsPage_getGroupId();
		Long irrelevantGroupId =
			testGetWorkspaceGroupChannelAccountUserSessionsPage_getIrrelevantGroupId();
		String channelId =
			testGetWorkspaceGroupChannelAccountUserSessionsPage_getChannelId();
		String irrelevantChannelId =
			testGetWorkspaceGroupChannelAccountUserSessionsPage_getIrrelevantChannelId();
		String accountId =
			testGetWorkspaceGroupChannelAccountUserSessionsPage_getAccountId();
		String irrelevantAccountId =
			testGetWorkspaceGroupChannelAccountUserSessionsPage_getIrrelevantAccountId();

		Page<UserSession> page =
			userSessionResource.getWorkspaceGroupChannelAccountUserSessionsPage(
				groupId, channelId, accountId, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if ((irrelevantGroupId != null) && (irrelevantChannelId != null) &&
			(irrelevantAccountId != null)) {

			UserSession irrelevantUserSession =
				testGetWorkspaceGroupChannelAccountUserSessionsPage_addUserSession(
					irrelevantGroupId, irrelevantChannelId, irrelevantAccountId,
					randomIrrelevantUserSession());

			page =
				userSessionResource.
					getWorkspaceGroupChannelAccountUserSessionsPage(
						irrelevantGroupId, irrelevantChannelId,
						irrelevantAccountId, null, null, null, null,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantUserSession, (List<UserSession>)page.getItems());
			assertValid(
				page,
				testGetWorkspaceGroupChannelAccountUserSessionsPage_getExpectedActions(
					irrelevantGroupId, irrelevantChannelId,
					irrelevantAccountId));
		}

		UserSession userSession1 =
			testGetWorkspaceGroupChannelAccountUserSessionsPage_addUserSession(
				groupId, channelId, accountId, randomUserSession());

		UserSession userSession2 =
			testGetWorkspaceGroupChannelAccountUserSessionsPage_addUserSession(
				groupId, channelId, accountId, randomUserSession());

		page =
			userSessionResource.getWorkspaceGroupChannelAccountUserSessionsPage(
				groupId, channelId, accountId, null, null, null, null,
				Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(userSession1, (List<UserSession>)page.getItems());
		assertContains(userSession2, (List<UserSession>)page.getItems());
		assertValid(
			page,
			testGetWorkspaceGroupChannelAccountUserSessionsPage_getExpectedActions(
				groupId, channelId, accountId));
	}

	protected Map<String, Map<String, String>>
			testGetWorkspaceGroupChannelAccountUserSessionsPage_getExpectedActions(
				Long groupId, String channelId, String accountId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWorkspaceGroupChannelAccountUserSessionsPageWithPagination()
		throws Exception {

		Long groupId =
			testGetWorkspaceGroupChannelAccountUserSessionsPage_getGroupId();
		String channelId =
			testGetWorkspaceGroupChannelAccountUserSessionsPage_getChannelId();
		String accountId =
			testGetWorkspaceGroupChannelAccountUserSessionsPage_getAccountId();

		Page<UserSession> userSessionsPage =
			userSessionResource.getWorkspaceGroupChannelAccountUserSessionsPage(
				groupId, channelId, accountId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			userSessionsPage.getTotalCount());

		UserSession userSession1 =
			testGetWorkspaceGroupChannelAccountUserSessionsPage_addUserSession(
				groupId, channelId, accountId, randomUserSession());

		UserSession userSession2 =
			testGetWorkspaceGroupChannelAccountUserSessionsPage_addUserSession(
				groupId, channelId, accountId, randomUserSession());

		UserSession userSession3 =
			testGetWorkspaceGroupChannelAccountUserSessionsPage_addUserSession(
				groupId, channelId, accountId, randomUserSession());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<UserSession> page1 =
				userSessionResource.
					getWorkspaceGroupChannelAccountUserSessionsPage(
						groupId, channelId, accountId, null, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(userSession1, (List<UserSession>)page1.getItems());

			Page<UserSession> page2 =
				userSessionResource.
					getWorkspaceGroupChannelAccountUserSessionsPage(
						groupId, channelId, accountId, null, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(userSession2, (List<UserSession>)page2.getItems());

			Page<UserSession> page3 =
				userSessionResource.
					getWorkspaceGroupChannelAccountUserSessionsPage(
						groupId, channelId, accountId, null, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(userSession3, (List<UserSession>)page3.getItems());
		}
		else {
			Page<UserSession> page1 =
				userSessionResource.
					getWorkspaceGroupChannelAccountUserSessionsPage(
						groupId, channelId, accountId, null, null, null, null,
						Pagination.of(1, totalCount + 2));

			List<UserSession> userSessions1 =
				(List<UserSession>)page1.getItems();

			Assert.assertEquals(
				userSessions1.toString(), totalCount + 2, userSessions1.size());

			Page<UserSession> page2 =
				userSessionResource.
					getWorkspaceGroupChannelAccountUserSessionsPage(
						groupId, channelId, accountId, null, null, null, null,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<UserSession> userSessions2 =
				(List<UserSession>)page2.getItems();

			Assert.assertEquals(
				userSessions2.toString(), 1, userSessions2.size());

			Page<UserSession> page3 =
				userSessionResource.
					getWorkspaceGroupChannelAccountUserSessionsPage(
						groupId, channelId, accountId, null, null, null, null,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(userSession1, (List<UserSession>)page3.getItems());
			assertContains(userSession2, (List<UserSession>)page3.getItems());
			assertContains(userSession3, (List<UserSession>)page3.getItems());
		}
	}

	protected UserSession
			testGetWorkspaceGroupChannelAccountUserSessionsPage_addUserSession(
				Long groupId, String channelId, String accountId,
				UserSession userSession)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkspaceGroupChannelAccountUserSessionsPage_getGroupId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkspaceGroupChannelAccountUserSessionsPage_getIrrelevantGroupId()
		throws Exception {

		return null;
	}

	protected String
			testGetWorkspaceGroupChannelAccountUserSessionsPage_getChannelId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetWorkspaceGroupChannelAccountUserSessionsPage_getIrrelevantChannelId()
		throws Exception {

		return null;
	}

	protected String
			testGetWorkspaceGroupChannelAccountUserSessionsPage_getAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetWorkspaceGroupChannelAccountUserSessionsPage_getIrrelevantAccountId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetWorkspaceGroupChannelIndividualUserSessionsPage()
		throws Exception {

		Long groupId =
			testGetWorkspaceGroupChannelIndividualUserSessionsPage_getGroupId();
		Long irrelevantGroupId =
			testGetWorkspaceGroupChannelIndividualUserSessionsPage_getIrrelevantGroupId();
		String channelId =
			testGetWorkspaceGroupChannelIndividualUserSessionsPage_getChannelId();
		String irrelevantChannelId =
			testGetWorkspaceGroupChannelIndividualUserSessionsPage_getIrrelevantChannelId();
		String individualId =
			testGetWorkspaceGroupChannelIndividualUserSessionsPage_getIndividualId();
		String irrelevantIndividualId =
			testGetWorkspaceGroupChannelIndividualUserSessionsPage_getIrrelevantIndividualId();

		Page<UserSession> page =
			userSessionResource.
				getWorkspaceGroupChannelIndividualUserSessionsPage(
					groupId, channelId, individualId,
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if ((irrelevantGroupId != null) && (irrelevantChannelId != null) &&
			(irrelevantIndividualId != null)) {

			UserSession irrelevantUserSession =
				testGetWorkspaceGroupChannelIndividualUserSessionsPage_addUserSession(
					irrelevantGroupId, irrelevantChannelId,
					irrelevantIndividualId, randomIrrelevantUserSession());

			page =
				userSessionResource.
					getWorkspaceGroupChannelIndividualUserSessionsPage(
						irrelevantGroupId, irrelevantChannelId,
						irrelevantIndividualId, null, null, null, null,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantUserSession, (List<UserSession>)page.getItems());
			assertValid(
				page,
				testGetWorkspaceGroupChannelIndividualUserSessionsPage_getExpectedActions(
					irrelevantGroupId, irrelevantChannelId,
					irrelevantIndividualId));
		}

		UserSession userSession1 =
			testGetWorkspaceGroupChannelIndividualUserSessionsPage_addUserSession(
				groupId, channelId, individualId, randomUserSession());

		UserSession userSession2 =
			testGetWorkspaceGroupChannelIndividualUserSessionsPage_addUserSession(
				groupId, channelId, individualId, randomUserSession());

		page =
			userSessionResource.
				getWorkspaceGroupChannelIndividualUserSessionsPage(
					groupId, channelId, individualId, null, null, null, null,
					Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(userSession1, (List<UserSession>)page.getItems());
		assertContains(userSession2, (List<UserSession>)page.getItems());
		assertValid(
			page,
			testGetWorkspaceGroupChannelIndividualUserSessionsPage_getExpectedActions(
				groupId, channelId, individualId));
	}

	protected Map<String, Map<String, String>>
			testGetWorkspaceGroupChannelIndividualUserSessionsPage_getExpectedActions(
				Long groupId, String channelId, String individualId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWorkspaceGroupChannelIndividualUserSessionsPageWithPagination()
		throws Exception {

		Long groupId =
			testGetWorkspaceGroupChannelIndividualUserSessionsPage_getGroupId();
		String channelId =
			testGetWorkspaceGroupChannelIndividualUserSessionsPage_getChannelId();
		String individualId =
			testGetWorkspaceGroupChannelIndividualUserSessionsPage_getIndividualId();

		Page<UserSession> userSessionsPage =
			userSessionResource.
				getWorkspaceGroupChannelIndividualUserSessionsPage(
					groupId, channelId, individualId, null, null, null, null,
					null);

		int totalCount = GetterUtil.getInteger(
			userSessionsPage.getTotalCount());

		UserSession userSession1 =
			testGetWorkspaceGroupChannelIndividualUserSessionsPage_addUserSession(
				groupId, channelId, individualId, randomUserSession());

		UserSession userSession2 =
			testGetWorkspaceGroupChannelIndividualUserSessionsPage_addUserSession(
				groupId, channelId, individualId, randomUserSession());

		UserSession userSession3 =
			testGetWorkspaceGroupChannelIndividualUserSessionsPage_addUserSession(
				groupId, channelId, individualId, randomUserSession());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<UserSession> page1 =
				userSessionResource.
					getWorkspaceGroupChannelIndividualUserSessionsPage(
						groupId, channelId, individualId, null, null, null,
						null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(userSession1, (List<UserSession>)page1.getItems());

			Page<UserSession> page2 =
				userSessionResource.
					getWorkspaceGroupChannelIndividualUserSessionsPage(
						groupId, channelId, individualId, null, null, null,
						null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(userSession2, (List<UserSession>)page2.getItems());

			Page<UserSession> page3 =
				userSessionResource.
					getWorkspaceGroupChannelIndividualUserSessionsPage(
						groupId, channelId, individualId, null, null, null,
						null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(userSession3, (List<UserSession>)page3.getItems());
		}
		else {
			Page<UserSession> page1 =
				userSessionResource.
					getWorkspaceGroupChannelIndividualUserSessionsPage(
						groupId, channelId, individualId, null, null, null,
						null, Pagination.of(1, totalCount + 2));

			List<UserSession> userSessions1 =
				(List<UserSession>)page1.getItems();

			Assert.assertEquals(
				userSessions1.toString(), totalCount + 2, userSessions1.size());

			Page<UserSession> page2 =
				userSessionResource.
					getWorkspaceGroupChannelIndividualUserSessionsPage(
						groupId, channelId, individualId, null, null, null,
						null, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<UserSession> userSessions2 =
				(List<UserSession>)page2.getItems();

			Assert.assertEquals(
				userSessions2.toString(), 1, userSessions2.size());

			Page<UserSession> page3 =
				userSessionResource.
					getWorkspaceGroupChannelIndividualUserSessionsPage(
						groupId, channelId, individualId, null, null, null,
						null, Pagination.of(1, (int)totalCount + 3));

			assertContains(userSession1, (List<UserSession>)page3.getItems());
			assertContains(userSession2, (List<UserSession>)page3.getItems());
			assertContains(userSession3, (List<UserSession>)page3.getItems());
		}
	}

	protected UserSession
			testGetWorkspaceGroupChannelIndividualUserSessionsPage_addUserSession(
				Long groupId, String channelId, String individualId,
				UserSession userSession)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkspaceGroupChannelIndividualUserSessionsPage_getGroupId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkspaceGroupChannelIndividualUserSessionsPage_getIrrelevantGroupId()
		throws Exception {

		return null;
	}

	protected String
			testGetWorkspaceGroupChannelIndividualUserSessionsPage_getChannelId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetWorkspaceGroupChannelIndividualUserSessionsPage_getIrrelevantChannelId()
		throws Exception {

		return null;
	}

	protected String
			testGetWorkspaceGroupChannelIndividualUserSessionsPage_getIndividualId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetWorkspaceGroupChannelIndividualUserSessionsPage_getIrrelevantIndividualId()
		throws Exception {

		return null;
	}

	protected void assertContains(
		UserSession userSession, List<UserSession> userSessions) {

		boolean contains = false;

		for (UserSession item : userSessions) {
			if (equals(userSession, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			userSessions + " does not contain " + userSession, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		UserSession userSession1, UserSession userSession2) {

		Assert.assertTrue(
			userSession1 + " does not equal " + userSession2,
			equals(userSession1, userSession2));
	}

	protected void assertEquals(
		List<UserSession> userSessions1, List<UserSession> userSessions2) {

		Assert.assertEquals(userSessions1.size(), userSessions2.size());

		for (int i = 0; i < userSessions1.size(); i++) {
			UserSession userSession1 = userSessions1.get(i);
			UserSession userSession2 = userSessions2.get(i);

			assertEquals(userSession1, userSession2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<UserSession> userSessions1, List<UserSession> userSessions2) {

		Assert.assertEquals(userSessions1.size(), userSessions2.size());

		for (UserSession userSession1 : userSessions1) {
			boolean contains = false;

			for (UserSession userSession2 : userSessions2) {
				if (equals(userSession1, userSession2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				userSessions2 + " does not contain " + userSession1, contains);
		}
	}

	protected void assertValid(UserSession userSession) throws Exception {
		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("becameKnown", additionalAssertFieldName)) {
				if (userSession.getBecameKnown() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("browserName", additionalAssertFieldName)) {
				if (userSession.getBrowserName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("completeDate", additionalAssertFieldName)) {
				if (userSession.getCompleteDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (userSession.getCreateDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("deviceType", additionalAssertFieldName)) {
				if (userSession.getDeviceType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("events", additionalAssertFieldName)) {
				if (userSession.getEvents() == null) {
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

	protected void assertValid(Page<UserSession> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<UserSession> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<UserSession> userSessions = page.getItems();

		int size = userSessions.size();

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
					com.liferay.osb.faro.rest.dto.v1_0.UserSession.class)) {

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
		UserSession userSession1, UserSession userSession2) {

		if (userSession1 == userSession2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("becameKnown", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userSession1.getBecameKnown(),
						userSession2.getBecameKnown())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("browserName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userSession1.getBrowserName(),
						userSession2.getBrowserName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("completeDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userSession1.getCompleteDate(),
						userSession2.getCompleteDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("createDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userSession1.getCreateDate(),
						userSession2.getCreateDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("deviceType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userSession1.getDeviceType(),
						userSession2.getDeviceType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("events", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userSession1.getEvents(), userSession2.getEvents())) {

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

		if (!(_userSessionResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_userSessionResource;

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
		EntityField entityField, String operator, UserSession userSession) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("becameKnown")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("browserName")) {
			Object object = userSession.getBrowserName();

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

		if (entityFieldName.equals("completeDate")) {
			if (operator.equals("between")) {
				Date date = userSession.getCompleteDate();

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

				sb.append(_format.format(userSession.getCompleteDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("createDate")) {
			if (operator.equals("between")) {
				Date date = userSession.getCreateDate();

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

				sb.append(_format.format(userSession.getCreateDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("deviceType")) {
			Object object = userSession.getDeviceType();

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

		if (entityFieldName.equals("events")) {
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

	protected UserSession randomUserSession() throws Exception {
		return new UserSession() {
			{
				becameKnown = RandomTestUtil.randomBoolean();
				browserName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				completeDate = RandomTestUtil.nextDate();
				createDate = RandomTestUtil.nextDate();
				deviceType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected UserSession randomIrrelevantUserSession() throws Exception {
		UserSession randomIrrelevantUserSession = randomUserSession();

		return randomIrrelevantUserSession;
	}

	protected UserSession randomPatchUserSession() throws Exception {
		return randomUserSession();
	}

	protected UserSessionResource userSessionResource;
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
		LogFactoryUtil.getLog(BaseUserSessionResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.osb.faro.rest.resource.v1_0.UserSessionResource
		_userSessionResource;

}
// LIFERAY-REST-BUILDER-HASH:991396656