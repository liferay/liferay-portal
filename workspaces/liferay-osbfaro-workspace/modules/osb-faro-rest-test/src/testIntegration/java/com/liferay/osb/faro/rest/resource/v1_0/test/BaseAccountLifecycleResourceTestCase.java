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

import com.liferay.osb.faro.rest.client.dto.v1_0.AccountLifecycle;
import com.liferay.osb.faro.rest.client.http.HttpInvoker;
import com.liferay.osb.faro.rest.client.pagination.Page;
import com.liferay.osb.faro.rest.client.pagination.Pagination;
import com.liferay.osb.faro.rest.client.resource.v1_0.AccountLifecycleResource;
import com.liferay.osb.faro.rest.client.serdes.v1_0.AccountLifecycleSerDes;
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
public abstract class BaseAccountLifecycleResourceTestCase {

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

		_accountLifecycleResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		accountLifecycleResource = AccountLifecycleResource.builder(
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

		AccountLifecycle accountLifecycle1 = randomAccountLifecycle();

		String json = objectMapper.writeValueAsString(accountLifecycle1);

		AccountLifecycle accountLifecycle2 = AccountLifecycleSerDes.toDTO(json);

		Assert.assertTrue(equals(accountLifecycle1, accountLifecycle2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		AccountLifecycle accountLifecycle = randomAccountLifecycle();

		String json1 = objectMapper.writeValueAsString(accountLifecycle);
		String json2 = AccountLifecycleSerDes.toJSON(accountLifecycle);

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

		AccountLifecycle accountLifecycle = randomAccountLifecycle();

		accountLifecycle.setDescription(regex);
		accountLifecycle.setId(regex);
		accountLifecycle.setName(regex);

		String json = AccountLifecycleSerDes.toJSON(accountLifecycle);

		Assert.assertFalse(json.contains(regex));

		accountLifecycle = AccountLifecycleSerDes.toDTO(json);

		Assert.assertEquals(regex, accountLifecycle.getDescription());
		Assert.assertEquals(regex, accountLifecycle.getId());
		Assert.assertEquals(regex, accountLifecycle.getName());
	}

	@Test
	public void testGetWorkspaceGroupChannelAccountLifecyclesPage()
		throws Exception {

		Long groupId =
			testGetWorkspaceGroupChannelAccountLifecyclesPage_getGroupId();
		Long irrelevantGroupId =
			testGetWorkspaceGroupChannelAccountLifecyclesPage_getIrrelevantGroupId();
		String channelId =
			testGetWorkspaceGroupChannelAccountLifecyclesPage_getChannelId();
		String irrelevantChannelId =
			testGetWorkspaceGroupChannelAccountLifecyclesPage_getIrrelevantChannelId();

		Page<AccountLifecycle> page =
			accountLifecycleResource.
				getWorkspaceGroupChannelAccountLifecyclesPage(
					groupId, channelId, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if ((irrelevantGroupId != null) && (irrelevantChannelId != null)) {
			AccountLifecycle irrelevantAccountLifecycle =
				testGetWorkspaceGroupChannelAccountLifecyclesPage_addAccountLifecycle(
					irrelevantGroupId, irrelevantChannelId,
					randomIrrelevantAccountLifecycle());

			page =
				accountLifecycleResource.
					getWorkspaceGroupChannelAccountLifecyclesPage(
						irrelevantGroupId, irrelevantChannelId, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAccountLifecycle,
				(List<AccountLifecycle>)page.getItems());
			assertValid(
				page,
				testGetWorkspaceGroupChannelAccountLifecyclesPage_getExpectedActions(
					irrelevantGroupId, irrelevantChannelId));
		}

		AccountLifecycle accountLifecycle1 =
			testGetWorkspaceGroupChannelAccountLifecyclesPage_addAccountLifecycle(
				groupId, channelId, randomAccountLifecycle());

		AccountLifecycle accountLifecycle2 =
			testGetWorkspaceGroupChannelAccountLifecyclesPage_addAccountLifecycle(
				groupId, channelId, randomAccountLifecycle());

		page =
			accountLifecycleResource.
				getWorkspaceGroupChannelAccountLifecyclesPage(
					groupId, channelId, null,
					Pagination.of(1, (int)totalCount + 2), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			accountLifecycle1, (List<AccountLifecycle>)page.getItems());
		assertContains(
			accountLifecycle2, (List<AccountLifecycle>)page.getItems());
		assertValid(
			page,
			testGetWorkspaceGroupChannelAccountLifecyclesPage_getExpectedActions(
				groupId, channelId));
	}

	protected Map<String, Map<String, String>>
			testGetWorkspaceGroupChannelAccountLifecyclesPage_getExpectedActions(
				Long groupId, String channelId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWorkspaceGroupChannelAccountLifecyclesPageWithPagination()
		throws Exception {

		Long groupId =
			testGetWorkspaceGroupChannelAccountLifecyclesPage_getGroupId();
		String channelId =
			testGetWorkspaceGroupChannelAccountLifecyclesPage_getChannelId();

		Page<AccountLifecycle> accountLifecyclesPage =
			accountLifecycleResource.
				getWorkspaceGroupChannelAccountLifecyclesPage(
					groupId, channelId, null, null, null);

		int totalCount = GetterUtil.getInteger(
			accountLifecyclesPage.getTotalCount());

		AccountLifecycle accountLifecycle1 =
			testGetWorkspaceGroupChannelAccountLifecyclesPage_addAccountLifecycle(
				groupId, channelId, randomAccountLifecycle());

		AccountLifecycle accountLifecycle2 =
			testGetWorkspaceGroupChannelAccountLifecyclesPage_addAccountLifecycle(
				groupId, channelId, randomAccountLifecycle());

		AccountLifecycle accountLifecycle3 =
			testGetWorkspaceGroupChannelAccountLifecyclesPage_addAccountLifecycle(
				groupId, channelId, randomAccountLifecycle());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<AccountLifecycle> page1 =
				accountLifecycleResource.
					getWorkspaceGroupChannelAccountLifecyclesPage(
						groupId, channelId, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				accountLifecycle1, (List<AccountLifecycle>)page1.getItems());

			Page<AccountLifecycle> page2 =
				accountLifecycleResource.
					getWorkspaceGroupChannelAccountLifecyclesPage(
						groupId, channelId, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				accountLifecycle2, (List<AccountLifecycle>)page2.getItems());

			Page<AccountLifecycle> page3 =
				accountLifecycleResource.
					getWorkspaceGroupChannelAccountLifecyclesPage(
						groupId, channelId, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				accountLifecycle3, (List<AccountLifecycle>)page3.getItems());
		}
		else {
			Page<AccountLifecycle> page1 =
				accountLifecycleResource.
					getWorkspaceGroupChannelAccountLifecyclesPage(
						groupId, channelId, null,
						Pagination.of(1, totalCount + 2), null);

			List<AccountLifecycle> accountLifecycles1 =
				(List<AccountLifecycle>)page1.getItems();

			Assert.assertEquals(
				accountLifecycles1.toString(), totalCount + 2,
				accountLifecycles1.size());

			Page<AccountLifecycle> page2 =
				accountLifecycleResource.
					getWorkspaceGroupChannelAccountLifecyclesPage(
						groupId, channelId, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<AccountLifecycle> accountLifecycles2 =
				(List<AccountLifecycle>)page2.getItems();

			Assert.assertEquals(
				accountLifecycles2.toString(), 1, accountLifecycles2.size());

			Page<AccountLifecycle> page3 =
				accountLifecycleResource.
					getWorkspaceGroupChannelAccountLifecyclesPage(
						groupId, channelId, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				accountLifecycle1, (List<AccountLifecycle>)page3.getItems());
			assertContains(
				accountLifecycle2, (List<AccountLifecycle>)page3.getItems());
			assertContains(
				accountLifecycle3, (List<AccountLifecycle>)page3.getItems());
		}
	}

	@Test
	public void testGetWorkspaceGroupChannelAccountLifecyclesPageWithSortDateTime()
		throws Exception {

		testGetWorkspaceGroupChannelAccountLifecyclesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, accountLifecycle1, accountLifecycle2) -> {
				BeanTestUtil.setProperty(
					accountLifecycle1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetWorkspaceGroupChannelAccountLifecyclesPageWithSortDouble()
		throws Exception {

		testGetWorkspaceGroupChannelAccountLifecyclesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, accountLifecycle1, accountLifecycle2) -> {
				BeanTestUtil.setProperty(
					accountLifecycle1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					accountLifecycle2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetWorkspaceGroupChannelAccountLifecyclesPageWithSortInteger()
		throws Exception {

		testGetWorkspaceGroupChannelAccountLifecyclesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, accountLifecycle1, accountLifecycle2) -> {
				BeanTestUtil.setProperty(
					accountLifecycle1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					accountLifecycle2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetWorkspaceGroupChannelAccountLifecyclesPageWithSortString()
		throws Exception {

		testGetWorkspaceGroupChannelAccountLifecyclesPageWithSort(
			EntityField.Type.STRING,
			(entityField, accountLifecycle1, accountLifecycle2) -> {
				Class<?> clazz = accountLifecycle1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						accountLifecycle1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						accountLifecycle2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						accountLifecycle1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						accountLifecycle2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						accountLifecycle1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						accountLifecycle2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetWorkspaceGroupChannelAccountLifecyclesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, AccountLifecycle, AccountLifecycle, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long groupId =
			testGetWorkspaceGroupChannelAccountLifecyclesPage_getGroupId();
		String channelId =
			testGetWorkspaceGroupChannelAccountLifecyclesPage_getChannelId();

		AccountLifecycle accountLifecycle1 = randomAccountLifecycle();
		AccountLifecycle accountLifecycle2 = randomAccountLifecycle();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, accountLifecycle1, accountLifecycle2);
		}

		accountLifecycle1 =
			testGetWorkspaceGroupChannelAccountLifecyclesPage_addAccountLifecycle(
				groupId, channelId, accountLifecycle1);

		accountLifecycle2 =
			testGetWorkspaceGroupChannelAccountLifecyclesPage_addAccountLifecycle(
				groupId, channelId, accountLifecycle2);

		Page<AccountLifecycle> page =
			accountLifecycleResource.
				getWorkspaceGroupChannelAccountLifecyclesPage(
					groupId, channelId, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<AccountLifecycle> ascPage =
				accountLifecycleResource.
					getWorkspaceGroupChannelAccountLifecyclesPage(
						groupId, channelId, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				accountLifecycle1, (List<AccountLifecycle>)ascPage.getItems());
			assertContains(
				accountLifecycle2, (List<AccountLifecycle>)ascPage.getItems());

			Page<AccountLifecycle> descPage =
				accountLifecycleResource.
					getWorkspaceGroupChannelAccountLifecyclesPage(
						groupId, channelId, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				accountLifecycle2, (List<AccountLifecycle>)descPage.getItems());
			assertContains(
				accountLifecycle1, (List<AccountLifecycle>)descPage.getItems());
		}
	}

	protected AccountLifecycle
			testGetWorkspaceGroupChannelAccountLifecyclesPage_addAccountLifecycle(
				Long groupId, String channelId,
				AccountLifecycle accountLifecycle)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkspaceGroupChannelAccountLifecyclesPage_getGroupId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkspaceGroupChannelAccountLifecyclesPage_getIrrelevantGroupId()
		throws Exception {

		return null;
	}

	protected String
			testGetWorkspaceGroupChannelAccountLifecyclesPage_getChannelId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetWorkspaceGroupChannelAccountLifecyclesPage_getIrrelevantChannelId()
		throws Exception {

		return null;
	}

	protected void assertContains(
		AccountLifecycle accountLifecycle,
		List<AccountLifecycle> accountLifecycles) {

		boolean contains = false;

		for (AccountLifecycle item : accountLifecycles) {
			if (equals(accountLifecycle, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			accountLifecycles + " does not contain " + accountLifecycle,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		AccountLifecycle accountLifecycle1,
		AccountLifecycle accountLifecycle2) {

		Assert.assertTrue(
			accountLifecycle1 + " does not equal " + accountLifecycle2,
			equals(accountLifecycle1, accountLifecycle2));
	}

	protected void assertEquals(
		List<AccountLifecycle> accountLifecycles1,
		List<AccountLifecycle> accountLifecycles2) {

		Assert.assertEquals(
			accountLifecycles1.size(), accountLifecycles2.size());

		for (int i = 0; i < accountLifecycles1.size(); i++) {
			AccountLifecycle accountLifecycle1 = accountLifecycles1.get(i);
			AccountLifecycle accountLifecycle2 = accountLifecycles2.get(i);

			assertEquals(accountLifecycle1, accountLifecycle2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<AccountLifecycle> accountLifecycles1,
		List<AccountLifecycle> accountLifecycles2) {

		Assert.assertEquals(
			accountLifecycles1.size(), accountLifecycles2.size());

		for (AccountLifecycle accountLifecycle1 : accountLifecycles1) {
			boolean contains = false;

			for (AccountLifecycle accountLifecycle2 : accountLifecycles2) {
				if (equals(accountLifecycle1, accountLifecycle2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				accountLifecycles2 + " does not contain " + accountLifecycle1,
				contains);
		}
	}

	protected void assertValid(AccountLifecycle accountLifecycle)
		throws Exception {

		boolean valid = true;

		if (accountLifecycle.getDateModified() == null) {
			valid = false;
		}

		if (accountLifecycle.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"accountLifecycleStages", additionalAssertFieldName)) {

				if (accountLifecycle.getAccountLifecycleStages() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultAccountLifecycle", additionalAssertFieldName)) {

				if (accountLifecycle.getDefaultAccountLifecycle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (accountLifecycle.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (accountLifecycle.getName() == null) {
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

	protected void assertValid(Page<AccountLifecycle> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<AccountLifecycle> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<AccountLifecycle> accountLifecycles =
			page.getItems();

		int size = accountLifecycles.size();

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
					com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycle.
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
		AccountLifecycle accountLifecycle1,
		AccountLifecycle accountLifecycle2) {

		if (accountLifecycle1 == accountLifecycle2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"accountLifecycleStages", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						accountLifecycle1.getAccountLifecycleStages(),
						accountLifecycle2.getAccountLifecycleStages())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountLifecycle1.getDateModified(),
						accountLifecycle2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultAccountLifecycle", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						accountLifecycle1.getDefaultAccountLifecycle(),
						accountLifecycle2.getDefaultAccountLifecycle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountLifecycle1.getDescription(),
						accountLifecycle2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountLifecycle1.getId(), accountLifecycle2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountLifecycle1.getName(),
						accountLifecycle2.getName())) {

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

		if (!(_accountLifecycleResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_accountLifecycleResource;

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
		AccountLifecycle accountLifecycle) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("accountLifecycleStages")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = accountLifecycle.getDateModified();

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

				sb.append(_format.format(accountLifecycle.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("defaultAccountLifecycle")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("description")) {
			Object object = accountLifecycle.getDescription();

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
			Object object = accountLifecycle.getId();

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
			Object object = accountLifecycle.getName();

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

	protected AccountLifecycle randomAccountLifecycle() throws Exception {
		return new AccountLifecycle() {
			{
				dateModified = RandomTestUtil.nextDate();
				defaultAccountLifecycle = RandomTestUtil.randomBoolean();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected AccountLifecycle randomIrrelevantAccountLifecycle()
		throws Exception {

		AccountLifecycle randomIrrelevantAccountLifecycle =
			randomAccountLifecycle();

		return randomIrrelevantAccountLifecycle;
	}

	protected AccountLifecycle randomPatchAccountLifecycle() throws Exception {
		return randomAccountLifecycle();
	}

	protected AccountLifecycleResource accountLifecycleResource;
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
		LogFactoryUtil.getLog(BaseAccountLifecycleResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.osb.faro.rest.resource.v1_0.AccountLifecycleResource
		_accountLifecycleResource;

}
// LIFERAY-REST-BUILDER-HASH:-619061104