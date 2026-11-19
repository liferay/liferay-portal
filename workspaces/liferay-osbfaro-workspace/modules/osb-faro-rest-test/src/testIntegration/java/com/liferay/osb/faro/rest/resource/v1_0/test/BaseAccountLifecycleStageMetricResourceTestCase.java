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

import com.liferay.osb.faro.rest.client.dto.v1_0.AccountLifecycleStageMetric;
import com.liferay.osb.faro.rest.client.http.HttpInvoker;
import com.liferay.osb.faro.rest.client.pagination.Page;
import com.liferay.osb.faro.rest.client.pagination.Pagination;
import com.liferay.osb.faro.rest.client.resource.v1_0.AccountLifecycleStageMetricResource;
import com.liferay.osb.faro.rest.client.serdes.v1_0.AccountLifecycleStageMetricSerDes;
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
public abstract class BaseAccountLifecycleStageMetricResourceTestCase {

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

		_accountLifecycleStageMetricResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		accountLifecycleStageMetricResource =
			AccountLifecycleStageMetricResource.builder(
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

		AccountLifecycleStageMetric accountLifecycleStageMetric1 =
			randomAccountLifecycleStageMetric();

		String json = objectMapper.writeValueAsString(
			accountLifecycleStageMetric1);

		AccountLifecycleStageMetric accountLifecycleStageMetric2 =
			AccountLifecycleStageMetricSerDes.toDTO(json);

		Assert.assertTrue(
			equals(accountLifecycleStageMetric1, accountLifecycleStageMetric2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		AccountLifecycleStageMetric accountLifecycleStageMetric =
			randomAccountLifecycleStageMetric();

		String json1 = objectMapper.writeValueAsString(
			accountLifecycleStageMetric);
		String json2 = AccountLifecycleStageMetricSerDes.toJSON(
			accountLifecycleStageMetric);

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

		AccountLifecycleStageMetric accountLifecycleStageMetric =
			randomAccountLifecycleStageMetric();

		accountLifecycleStageMetric.setDescription(regex);
		accountLifecycleStageMetric.setName(regex);

		String json = AccountLifecycleStageMetricSerDes.toJSON(
			accountLifecycleStageMetric);

		Assert.assertFalse(json.contains(regex));

		accountLifecycleStageMetric = AccountLifecycleStageMetricSerDes.toDTO(
			json);

		Assert.assertEquals(
			regex, accountLifecycleStageMetric.getDescription());
		Assert.assertEquals(regex, accountLifecycleStageMetric.getName());
	}

	@Test
	public void testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPage()
		throws Exception {

		Long groupId =
			testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPage_getGroupId();
		Long irrelevantGroupId =
			testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPage_getIrrelevantGroupId();
		String channelId =
			testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPage_getChannelId();
		String irrelevantChannelId =
			testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPage_getIrrelevantChannelId();
		String accountLifecycleId =
			testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPage_getAccountLifecycleId();
		String irrelevantAccountLifecycleId =
			testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPage_getIrrelevantAccountLifecycleId();

		Page<AccountLifecycleStageMetric> page =
			accountLifecycleStageMetricResource.
				getWorkspaceGroupChannelAccountLifecycleStageMetricsPage(
					groupId, channelId, accountLifecycleId,
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if ((irrelevantGroupId != null) && (irrelevantChannelId != null) &&
			(irrelevantAccountLifecycleId != null)) {

			AccountLifecycleStageMetric irrelevantAccountLifecycleStageMetric =
				testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPage_addAccountLifecycleStageMetric(
					irrelevantGroupId, irrelevantChannelId,
					irrelevantAccountLifecycleId,
					randomIrrelevantAccountLifecycleStageMetric());

			page =
				accountLifecycleStageMetricResource.
					getWorkspaceGroupChannelAccountLifecycleStageMetricsPage(
						irrelevantGroupId, irrelevantChannelId,
						irrelevantAccountLifecycleId, null, null, null,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAccountLifecycleStageMetric,
				(List<AccountLifecycleStageMetric>)page.getItems());
			assertValid(
				page,
				testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPage_getExpectedActions(
					irrelevantGroupId, irrelevantChannelId,
					irrelevantAccountLifecycleId));
		}

		AccountLifecycleStageMetric accountLifecycleStageMetric1 =
			testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPage_addAccountLifecycleStageMetric(
				groupId, channelId, accountLifecycleId,
				randomAccountLifecycleStageMetric());

		AccountLifecycleStageMetric accountLifecycleStageMetric2 =
			testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPage_addAccountLifecycleStageMetric(
				groupId, channelId, accountLifecycleId,
				randomAccountLifecycleStageMetric());

		page =
			accountLifecycleStageMetricResource.
				getWorkspaceGroupChannelAccountLifecycleStageMetricsPage(
					groupId, channelId, accountLifecycleId, null, null, null,
					Pagination.of(1, (int)totalCount + 2));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			accountLifecycleStageMetric1,
			(List<AccountLifecycleStageMetric>)page.getItems());
		assertContains(
			accountLifecycleStageMetric2,
			(List<AccountLifecycleStageMetric>)page.getItems());
		assertValid(
			page,
			testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPage_getExpectedActions(
				groupId, channelId, accountLifecycleId));
	}

	protected Map<String, Map<String, String>>
			testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPage_getExpectedActions(
				Long groupId, String channelId, String accountLifecycleId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPageWithPagination()
		throws Exception {

		Long groupId =
			testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPage_getGroupId();
		String channelId =
			testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPage_getChannelId();
		String accountLifecycleId =
			testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPage_getAccountLifecycleId();

		Page<AccountLifecycleStageMetric> accountLifecycleStageMetricsPage =
			accountLifecycleStageMetricResource.
				getWorkspaceGroupChannelAccountLifecycleStageMetricsPage(
					groupId, channelId, accountLifecycleId, null, null, null,
					null);

		int totalCount = GetterUtil.getInteger(
			accountLifecycleStageMetricsPage.getTotalCount());

		AccountLifecycleStageMetric accountLifecycleStageMetric1 =
			testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPage_addAccountLifecycleStageMetric(
				groupId, channelId, accountLifecycleId,
				randomAccountLifecycleStageMetric());

		AccountLifecycleStageMetric accountLifecycleStageMetric2 =
			testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPage_addAccountLifecycleStageMetric(
				groupId, channelId, accountLifecycleId,
				randomAccountLifecycleStageMetric());

		AccountLifecycleStageMetric accountLifecycleStageMetric3 =
			testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPage_addAccountLifecycleStageMetric(
				groupId, channelId, accountLifecycleId,
				randomAccountLifecycleStageMetric());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<AccountLifecycleStageMetric> page1 =
				accountLifecycleStageMetricResource.
					getWorkspaceGroupChannelAccountLifecycleStageMetricsPage(
						groupId, channelId, accountLifecycleId, null, null,
						null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				accountLifecycleStageMetric1,
				(List<AccountLifecycleStageMetric>)page1.getItems());

			Page<AccountLifecycleStageMetric> page2 =
				accountLifecycleStageMetricResource.
					getWorkspaceGroupChannelAccountLifecycleStageMetricsPage(
						groupId, channelId, accountLifecycleId, null, null,
						null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				accountLifecycleStageMetric2,
				(List<AccountLifecycleStageMetric>)page2.getItems());

			Page<AccountLifecycleStageMetric> page3 =
				accountLifecycleStageMetricResource.
					getWorkspaceGroupChannelAccountLifecycleStageMetricsPage(
						groupId, channelId, accountLifecycleId, null, null,
						null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				accountLifecycleStageMetric3,
				(List<AccountLifecycleStageMetric>)page3.getItems());
		}
		else {
			Page<AccountLifecycleStageMetric> page1 =
				accountLifecycleStageMetricResource.
					getWorkspaceGroupChannelAccountLifecycleStageMetricsPage(
						groupId, channelId, accountLifecycleId, null, null,
						null, Pagination.of(1, totalCount + 2));

			List<AccountLifecycleStageMetric> accountLifecycleStageMetrics1 =
				(List<AccountLifecycleStageMetric>)page1.getItems();

			Assert.assertEquals(
				accountLifecycleStageMetrics1.toString(), totalCount + 2,
				accountLifecycleStageMetrics1.size());

			Page<AccountLifecycleStageMetric> page2 =
				accountLifecycleStageMetricResource.
					getWorkspaceGroupChannelAccountLifecycleStageMetricsPage(
						groupId, channelId, accountLifecycleId, null, null,
						null, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<AccountLifecycleStageMetric> accountLifecycleStageMetrics2 =
				(List<AccountLifecycleStageMetric>)page2.getItems();

			Assert.assertEquals(
				accountLifecycleStageMetrics2.toString(), 1,
				accountLifecycleStageMetrics2.size());

			Page<AccountLifecycleStageMetric> page3 =
				accountLifecycleStageMetricResource.
					getWorkspaceGroupChannelAccountLifecycleStageMetricsPage(
						groupId, channelId, accountLifecycleId, null, null,
						null, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				accountLifecycleStageMetric1,
				(List<AccountLifecycleStageMetric>)page3.getItems());
			assertContains(
				accountLifecycleStageMetric2,
				(List<AccountLifecycleStageMetric>)page3.getItems());
			assertContains(
				accountLifecycleStageMetric3,
				(List<AccountLifecycleStageMetric>)page3.getItems());
		}
	}

	protected AccountLifecycleStageMetric
			testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPage_addAccountLifecycleStageMetric(
				Long groupId, String channelId, String accountLifecycleId,
				AccountLifecycleStageMetric accountLifecycleStageMetric)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPage_getGroupId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPage_getIrrelevantGroupId()
		throws Exception {

		return null;
	}

	protected String
			testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPage_getChannelId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPage_getIrrelevantChannelId()
		throws Exception {

		return null;
	}

	protected String
			testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPage_getAccountLifecycleId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPage_getIrrelevantAccountLifecycleId()
		throws Exception {

		return null;
	}

	protected void assertContains(
		AccountLifecycleStageMetric accountLifecycleStageMetric,
		List<AccountLifecycleStageMetric> accountLifecycleStageMetrics) {

		boolean contains = false;

		for (AccountLifecycleStageMetric item : accountLifecycleStageMetrics) {
			if (equals(accountLifecycleStageMetric, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			accountLifecycleStageMetrics + " does not contain " +
				accountLifecycleStageMetric,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		AccountLifecycleStageMetric accountLifecycleStageMetric1,
		AccountLifecycleStageMetric accountLifecycleStageMetric2) {

		Assert.assertTrue(
			accountLifecycleStageMetric1 + " does not equal " +
				accountLifecycleStageMetric2,
			equals(accountLifecycleStageMetric1, accountLifecycleStageMetric2));
	}

	protected void assertEquals(
		List<AccountLifecycleStageMetric> accountLifecycleStageMetrics1,
		List<AccountLifecycleStageMetric> accountLifecycleStageMetrics2) {

		Assert.assertEquals(
			accountLifecycleStageMetrics1.size(),
			accountLifecycleStageMetrics2.size());

		for (int i = 0; i < accountLifecycleStageMetrics1.size(); i++) {
			AccountLifecycleStageMetric accountLifecycleStageMetric1 =
				accountLifecycleStageMetrics1.get(i);
			AccountLifecycleStageMetric accountLifecycleStageMetric2 =
				accountLifecycleStageMetrics2.get(i);

			assertEquals(
				accountLifecycleStageMetric1, accountLifecycleStageMetric2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<AccountLifecycleStageMetric> accountLifecycleStageMetrics1,
		List<AccountLifecycleStageMetric> accountLifecycleStageMetrics2) {

		Assert.assertEquals(
			accountLifecycleStageMetrics1.size(),
			accountLifecycleStageMetrics2.size());

		for (AccountLifecycleStageMetric accountLifecycleStageMetric1 :
				accountLifecycleStageMetrics1) {

			boolean contains = false;

			for (AccountLifecycleStageMetric accountLifecycleStageMetric2 :
					accountLifecycleStageMetrics2) {

				if (equals(
						accountLifecycleStageMetric1,
						accountLifecycleStageMetric2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				accountLifecycleStageMetrics2 + " does not contain " +
					accountLifecycleStageMetric1,
				contains);
		}
	}

	protected void assertValid(
			AccountLifecycleStageMetric accountLifecycleStageMetric)
		throws Exception {

		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountsCount", additionalAssertFieldName)) {
				if (accountLifecycleStageMetric.getAccountsCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"averageStageDuration", additionalAssertFieldName)) {

				if (accountLifecycleStageMetric.getAverageStageDuration() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"conversionRateToNextStage", additionalAssertFieldName)) {

				if (accountLifecycleStageMetric.
						getConversionRateToNextStage() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (accountLifecycleStageMetric.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (accountLifecycleStageMetric.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("percentage", additionalAssertFieldName)) {
				if (accountLifecycleStageMetric.getPercentage() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("position", additionalAssertFieldName)) {
				if (accountLifecycleStageMetric.getPosition() == null) {
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

	protected void assertValid(Page<AccountLifecycleStageMetric> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<AccountLifecycleStageMetric> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<AccountLifecycleStageMetric>
			accountLifecycleStageMetrics = page.getItems();

		int size = accountLifecycleStageMetrics.size();

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
					com.liferay.osb.faro.rest.dto.v1_0.
						AccountLifecycleStageMetric.class)) {

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
		AccountLifecycleStageMetric accountLifecycleStageMetric1,
		AccountLifecycleStageMetric accountLifecycleStageMetric2) {

		if (accountLifecycleStageMetric1 == accountLifecycleStageMetric2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountsCount", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountLifecycleStageMetric1.getAccountsCount(),
						accountLifecycleStageMetric2.getAccountsCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"averageStageDuration", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						accountLifecycleStageMetric1.getAverageStageDuration(),
						accountLifecycleStageMetric2.
							getAverageStageDuration())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"conversionRateToNextStage", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						accountLifecycleStageMetric1.
							getConversionRateToNextStage(),
						accountLifecycleStageMetric2.
							getConversionRateToNextStage())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountLifecycleStageMetric1.getDescription(),
						accountLifecycleStageMetric2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountLifecycleStageMetric1.getName(),
						accountLifecycleStageMetric2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("percentage", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountLifecycleStageMetric1.getPercentage(),
						accountLifecycleStageMetric2.getPercentage())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("position", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountLifecycleStageMetric1.getPosition(),
						accountLifecycleStageMetric2.getPosition())) {

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

		if (!(_accountLifecycleStageMetricResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_accountLifecycleStageMetricResource;

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
		AccountLifecycleStageMetric accountLifecycleStageMetric) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("accountsCount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("averageStageDuration")) {
			sb.append(
				String.valueOf(
					accountLifecycleStageMetric.getAverageStageDuration()));

			return sb.toString();
		}

		if (entityFieldName.equals("conversionRateToNextStage")) {
			sb.append(
				String.valueOf(
					accountLifecycleStageMetric.
						getConversionRateToNextStage()));

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = accountLifecycleStageMetric.getDescription();

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
			Object object = accountLifecycleStageMetric.getName();

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

		if (entityFieldName.equals("percentage")) {
			sb.append(
				String.valueOf(accountLifecycleStageMetric.getPercentage()));

			return sb.toString();
		}

		if (entityFieldName.equals("position")) {
			sb.append(
				String.valueOf(accountLifecycleStageMetric.getPosition()));

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

	protected AccountLifecycleStageMetric randomAccountLifecycleStageMetric()
		throws Exception {

		return new AccountLifecycleStageMetric() {
			{
				accountsCount = RandomTestUtil.randomLong();
				averageStageDuration = RandomTestUtil.randomDouble();
				conversionRateToNextStage = RandomTestUtil.randomDouble();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				percentage = RandomTestUtil.randomDouble();
				position = RandomTestUtil.randomInt();
			}
		};
	}

	protected AccountLifecycleStageMetric
			randomIrrelevantAccountLifecycleStageMetric()
		throws Exception {

		AccountLifecycleStageMetric
			randomIrrelevantAccountLifecycleStageMetric =
				randomAccountLifecycleStageMetric();

		return randomIrrelevantAccountLifecycleStageMetric;
	}

	protected AccountLifecycleStageMetric
			randomPatchAccountLifecycleStageMetric()
		throws Exception {

		return randomAccountLifecycleStageMetric();
	}

	protected AccountLifecycleStageMetricResource
		accountLifecycleStageMetricResource;
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
		LogFactoryUtil.getLog(
			BaseAccountLifecycleStageMetricResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.osb.faro.rest.resource.v1_0.
			AccountLifecycleStageMetricResource
				_accountLifecycleStageMetricResource;

}
// LIFERAY-REST-BUILDER-HASH:-526379241