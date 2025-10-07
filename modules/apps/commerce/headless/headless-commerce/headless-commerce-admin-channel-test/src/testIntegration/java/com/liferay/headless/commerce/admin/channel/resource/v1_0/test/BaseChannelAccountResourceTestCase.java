/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.resource.v1_0.test;

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
import com.liferay.headless.commerce.admin.channel.client.dto.v1_0.ChannelAccount;
import com.liferay.headless.commerce.admin.channel.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.channel.client.pagination.Page;
import com.liferay.headless.commerce.admin.channel.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.channel.client.resource.v1_0.ChannelAccountResource;
import com.liferay.headless.commerce.admin.channel.client.serdes.v1_0.ChannelAccountSerDes;
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
import com.liferay.portal.search.test.rule.SearchTestRule;
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
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public abstract class BaseChannelAccountResourceTestCase {

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

		_channelAccountResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		channelAccountResource = ChannelAccountResource.builder(
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

		ChannelAccount channelAccount1 = randomChannelAccount();

		String json = objectMapper.writeValueAsString(channelAccount1);

		ChannelAccount channelAccount2 = ChannelAccountSerDes.toDTO(json);

		Assert.assertTrue(equals(channelAccount1, channelAccount2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ChannelAccount channelAccount = randomChannelAccount();

		String json1 = objectMapper.writeValueAsString(channelAccount);
		String json2 = ChannelAccountSerDes.toJSON(channelAccount);

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

		ChannelAccount channelAccount = randomChannelAccount();

		channelAccount.setAccountExternalReferenceCode(regex);
		channelAccount.setChannelExternalReferenceCode(regex);

		String json = ChannelAccountSerDes.toJSON(channelAccount);

		Assert.assertFalse(json.contains(regex));

		channelAccount = ChannelAccountSerDes.toDTO(json);

		Assert.assertEquals(
			regex, channelAccount.getAccountExternalReferenceCode());
		Assert.assertEquals(
			regex, channelAccount.getChannelExternalReferenceCode());
	}

	@Test
	public void testDeleteChannelAccount() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ChannelAccount channelAccount =
			testDeleteChannelAccount_addChannelAccount();

		assertHttpResponseStatusCode(
			204,
			channelAccountResource.deleteChannelAccountHttpResponse(
				channelAccount.getChannelAccountId()));
	}

	protected ChannelAccount testDeleteChannelAccount_addChannelAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteChannelAccount() throws Exception {

		// No namespace

		ChannelAccount channelAccount1 =
			testGraphQLDeleteChannelAccount_addChannelAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteChannelAccount",
						new HashMap<String, Object>() {
							{
								put(
									"channelAccountId",
									channelAccount1.getChannelAccountId());
							}
						})),
				"JSONObject/data", "Object/deleteChannelAccount"));

		// Using the namespace headlessCommerceAdminChannel_v1_0

		ChannelAccount channelAccount2 =
			testGraphQLDeleteChannelAccount_addChannelAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminChannel_v1_0",
						new GraphQLField(
							"deleteChannelAccount",
							new HashMap<String, Object>() {
								{
									put(
										"channelAccountId",
										channelAccount2.getChannelAccountId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminChannel_v1_0",
				"Object/deleteChannelAccount"));
	}

	protected ChannelAccount testGraphQLDeleteChannelAccount_addChannelAccount()
		throws Exception {

		return testGraphQLChannelAccount_addChannelAccount();
	}

	@Test
	public void testDeleteChannelAccountBatch() throws Exception {
		ChannelAccount channelAccount1 =
			testDeleteChannelAccountBatch_addChannelAccount();

		testDeleteChannelAccountBatch_deleteChannelAccount(
			202, null, channelAccount1.getChannelAccountId());
	}

	protected ChannelAccount testDeleteChannelAccountBatch_addChannelAccount()
		throws Exception {

		return testDeleteChannelAccount_addChannelAccount();
	}

	protected void testDeleteChannelAccountBatch_deleteChannelAccount(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			channelAccountResource.deleteChannelAccountBatchHttpResponse(
				null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"channelAccountId", () -> id
					)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelAccountsPage()
		throws Exception {

		String externalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelAccountsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelAccountsPage_getIrrelevantExternalReferenceCode();

		Page<ChannelAccount> page =
			channelAccountResource.
				getChannelByExternalReferenceCodeChannelAccountsPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			ChannelAccount irrelevantChannelAccount =
				testGetChannelByExternalReferenceCodeChannelAccountsPage_addChannelAccount(
					irrelevantExternalReferenceCode,
					randomIrrelevantChannelAccount());

			page =
				channelAccountResource.
					getChannelByExternalReferenceCodeChannelAccountsPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantChannelAccount,
				(List<ChannelAccount>)page.getItems());
			assertValid(
				page,
				testGetChannelByExternalReferenceCodeChannelAccountsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		ChannelAccount channelAccount1 =
			testGetChannelByExternalReferenceCodeChannelAccountsPage_addChannelAccount(
				externalReferenceCode, randomChannelAccount());

		ChannelAccount channelAccount2 =
			testGetChannelByExternalReferenceCodeChannelAccountsPage_addChannelAccount(
				externalReferenceCode, randomChannelAccount());

		page =
			channelAccountResource.
				getChannelByExternalReferenceCodeChannelAccountsPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(channelAccount1, (List<ChannelAccount>)page.getItems());
		assertContains(channelAccount2, (List<ChannelAccount>)page.getItems());
		assertValid(
			page,
			testGetChannelByExternalReferenceCodeChannelAccountsPage_getExpectedActions(
				externalReferenceCode));

		channelAccountResource.deleteChannelAccount(
			channelAccount1.getChannelAccountId());

		channelAccountResource.deleteChannelAccount(
			channelAccount2.getChannelAccountId());
	}

	protected Map<String, Map<String, String>>
			testGetChannelByExternalReferenceCodeChannelAccountsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelAccountsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelAccountsPage_getExternalReferenceCode();

		Page<ChannelAccount> channelAccountsPage =
			channelAccountResource.
				getChannelByExternalReferenceCodeChannelAccountsPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			channelAccountsPage.getTotalCount());

		ChannelAccount channelAccount1 =
			testGetChannelByExternalReferenceCodeChannelAccountsPage_addChannelAccount(
				externalReferenceCode, randomChannelAccount());

		ChannelAccount channelAccount2 =
			testGetChannelByExternalReferenceCodeChannelAccountsPage_addChannelAccount(
				externalReferenceCode, randomChannelAccount());

		ChannelAccount channelAccount3 =
			testGetChannelByExternalReferenceCodeChannelAccountsPage_addChannelAccount(
				externalReferenceCode, randomChannelAccount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ChannelAccount> page1 =
				channelAccountResource.
					getChannelByExternalReferenceCodeChannelAccountsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				channelAccount1, (List<ChannelAccount>)page1.getItems());

			Page<ChannelAccount> page2 =
				channelAccountResource.
					getChannelByExternalReferenceCodeChannelAccountsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				channelAccount2, (List<ChannelAccount>)page2.getItems());

			Page<ChannelAccount> page3 =
				channelAccountResource.
					getChannelByExternalReferenceCodeChannelAccountsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				channelAccount3, (List<ChannelAccount>)page3.getItems());
		}
		else {
			Page<ChannelAccount> page1 =
				channelAccountResource.
					getChannelByExternalReferenceCodeChannelAccountsPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<ChannelAccount> channelAccounts1 =
				(List<ChannelAccount>)page1.getItems();

			Assert.assertEquals(
				channelAccounts1.toString(), totalCount + 2,
				channelAccounts1.size());

			Page<ChannelAccount> page2 =
				channelAccountResource.
					getChannelByExternalReferenceCodeChannelAccountsPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ChannelAccount> channelAccounts2 =
				(List<ChannelAccount>)page2.getItems();

			Assert.assertEquals(
				channelAccounts2.toString(), 1, channelAccounts2.size());

			Page<ChannelAccount> page3 =
				channelAccountResource.
					getChannelByExternalReferenceCodeChannelAccountsPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				channelAccount1, (List<ChannelAccount>)page3.getItems());
			assertContains(
				channelAccount2, (List<ChannelAccount>)page3.getItems());
			assertContains(
				channelAccount3, (List<ChannelAccount>)page3.getItems());
		}
	}

	protected ChannelAccount
			testGetChannelByExternalReferenceCodeChannelAccountsPage_addChannelAccount(
				String externalReferenceCode, ChannelAccount channelAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelAccountsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelAccountsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetChannelIdChannelAccountsPage() throws Exception {
		Long id = testGetChannelIdChannelAccountsPage_getId();
		Long irrelevantId =
			testGetChannelIdChannelAccountsPage_getIrrelevantId();

		Page<ChannelAccount> page =
			channelAccountResource.getChannelIdChannelAccountsPage(
				id, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			ChannelAccount irrelevantChannelAccount =
				testGetChannelIdChannelAccountsPage_addChannelAccount(
					irrelevantId, randomIrrelevantChannelAccount());

			page = channelAccountResource.getChannelIdChannelAccountsPage(
				irrelevantId, null, null, Pagination.of(1, (int)totalCount + 1),
				null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantChannelAccount,
				(List<ChannelAccount>)page.getItems());
			assertValid(
				page,
				testGetChannelIdChannelAccountsPage_getExpectedActions(
					irrelevantId));
		}

		ChannelAccount channelAccount1 =
			testGetChannelIdChannelAccountsPage_addChannelAccount(
				id, randomChannelAccount());

		ChannelAccount channelAccount2 =
			testGetChannelIdChannelAccountsPage_addChannelAccount(
				id, randomChannelAccount());

		page = channelAccountResource.getChannelIdChannelAccountsPage(
			id, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(channelAccount1, (List<ChannelAccount>)page.getItems());
		assertContains(channelAccount2, (List<ChannelAccount>)page.getItems());
		assertValid(
			page, testGetChannelIdChannelAccountsPage_getExpectedActions(id));

		channelAccountResource.deleteChannelAccount(
			channelAccount1.getChannelAccountId());

		channelAccountResource.deleteChannelAccount(
			channelAccount2.getChannelAccountId());
	}

	protected Map<String, Map<String, String>>
			testGetChannelIdChannelAccountsPage_getExpectedActions(Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelIdChannelAccountsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetChannelIdChannelAccountsPage_getId();

		ChannelAccount channelAccount1 = randomChannelAccount();

		channelAccount1 = testGetChannelIdChannelAccountsPage_addChannelAccount(
			id, channelAccount1);

		for (EntityField entityField : entityFields) {
			Page<ChannelAccount> page =
				channelAccountResource.getChannelIdChannelAccountsPage(
					id, null,
					getFilterString(entityField, "between", channelAccount1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(channelAccount1),
				(List<ChannelAccount>)page.getItems());
		}
	}

	@Test
	public void testGetChannelIdChannelAccountsPageWithFilterDoubleEquals()
		throws Exception {

		testGetChannelIdChannelAccountsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetChannelIdChannelAccountsPageWithFilterStringContains()
		throws Exception {

		testGetChannelIdChannelAccountsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelIdChannelAccountsPageWithFilterStringEquals()
		throws Exception {

		testGetChannelIdChannelAccountsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetChannelIdChannelAccountsPageWithFilterStringStartsWith()
		throws Exception {

		testGetChannelIdChannelAccountsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetChannelIdChannelAccountsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetChannelIdChannelAccountsPage_getId();

		ChannelAccount channelAccount1 =
			testGetChannelIdChannelAccountsPage_addChannelAccount(
				id, randomChannelAccount());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ChannelAccount channelAccount2 =
			testGetChannelIdChannelAccountsPage_addChannelAccount(
				id, randomChannelAccount());

		for (EntityField entityField : entityFields) {
			Page<ChannelAccount> page =
				channelAccountResource.getChannelIdChannelAccountsPage(
					id, null,
					getFilterString(entityField, operator, channelAccount1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(channelAccount1),
				(List<ChannelAccount>)page.getItems());
		}
	}

	@Test
	public void testGetChannelIdChannelAccountsPageWithPagination()
		throws Exception {

		Long id = testGetChannelIdChannelAccountsPage_getId();

		Page<ChannelAccount> channelAccountsPage =
			channelAccountResource.getChannelIdChannelAccountsPage(
				id, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			channelAccountsPage.getTotalCount());

		ChannelAccount channelAccount1 =
			testGetChannelIdChannelAccountsPage_addChannelAccount(
				id, randomChannelAccount());

		ChannelAccount channelAccount2 =
			testGetChannelIdChannelAccountsPage_addChannelAccount(
				id, randomChannelAccount());

		ChannelAccount channelAccount3 =
			testGetChannelIdChannelAccountsPage_addChannelAccount(
				id, randomChannelAccount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ChannelAccount> page1 =
				channelAccountResource.getChannelIdChannelAccountsPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				channelAccount1, (List<ChannelAccount>)page1.getItems());

			Page<ChannelAccount> page2 =
				channelAccountResource.getChannelIdChannelAccountsPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				channelAccount2, (List<ChannelAccount>)page2.getItems());

			Page<ChannelAccount> page3 =
				channelAccountResource.getChannelIdChannelAccountsPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				channelAccount3, (List<ChannelAccount>)page3.getItems());
		}
		else {
			Page<ChannelAccount> page1 =
				channelAccountResource.getChannelIdChannelAccountsPage(
					id, null, null, Pagination.of(1, totalCount + 2), null);

			List<ChannelAccount> channelAccounts1 =
				(List<ChannelAccount>)page1.getItems();

			Assert.assertEquals(
				channelAccounts1.toString(), totalCount + 2,
				channelAccounts1.size());

			Page<ChannelAccount> page2 =
				channelAccountResource.getChannelIdChannelAccountsPage(
					id, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ChannelAccount> channelAccounts2 =
				(List<ChannelAccount>)page2.getItems();

			Assert.assertEquals(
				channelAccounts2.toString(), 1, channelAccounts2.size());

			Page<ChannelAccount> page3 =
				channelAccountResource.getChannelIdChannelAccountsPage(
					id, null, null, Pagination.of(1, (int)totalCount + 3),
					null);

			assertContains(
				channelAccount1, (List<ChannelAccount>)page3.getItems());
			assertContains(
				channelAccount2, (List<ChannelAccount>)page3.getItems());
			assertContains(
				channelAccount3, (List<ChannelAccount>)page3.getItems());
		}
	}

	@Test
	public void testGetChannelIdChannelAccountsPageWithSortDateTime()
		throws Exception {

		testGetChannelIdChannelAccountsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, channelAccount1, channelAccount2) -> {
				BeanTestUtil.setProperty(
					channelAccount1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetChannelIdChannelAccountsPageWithSortDouble()
		throws Exception {

		testGetChannelIdChannelAccountsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, channelAccount1, channelAccount2) -> {
				BeanTestUtil.setProperty(
					channelAccount1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					channelAccount2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetChannelIdChannelAccountsPageWithSortInteger()
		throws Exception {

		testGetChannelIdChannelAccountsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, channelAccount1, channelAccount2) -> {
				BeanTestUtil.setProperty(
					channelAccount1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					channelAccount2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetChannelIdChannelAccountsPageWithSortString()
		throws Exception {

		testGetChannelIdChannelAccountsPageWithSort(
			EntityField.Type.STRING,
			(entityField, channelAccount1, channelAccount2) -> {
				Class<?> clazz = channelAccount1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						channelAccount1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						channelAccount2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						channelAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						channelAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						channelAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						channelAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetChannelIdChannelAccountsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, ChannelAccount, ChannelAccount, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetChannelIdChannelAccountsPage_getId();

		ChannelAccount channelAccount1 = randomChannelAccount();
		ChannelAccount channelAccount2 = randomChannelAccount();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, channelAccount1, channelAccount2);
		}

		channelAccount1 = testGetChannelIdChannelAccountsPage_addChannelAccount(
			id, channelAccount1);

		channelAccount2 = testGetChannelIdChannelAccountsPage_addChannelAccount(
			id, channelAccount2);

		Page<ChannelAccount> page =
			channelAccountResource.getChannelIdChannelAccountsPage(
				id, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ChannelAccount> ascPage =
				channelAccountResource.getChannelIdChannelAccountsPage(
					id, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				channelAccount1, (List<ChannelAccount>)ascPage.getItems());
			assertContains(
				channelAccount2, (List<ChannelAccount>)ascPage.getItems());

			Page<ChannelAccount> descPage =
				channelAccountResource.getChannelIdChannelAccountsPage(
					id, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				channelAccount2, (List<ChannelAccount>)descPage.getItems());
			assertContains(
				channelAccount1, (List<ChannelAccount>)descPage.getItems());
		}
	}

	protected ChannelAccount
			testGetChannelIdChannelAccountsPage_addChannelAccount(
				Long id, ChannelAccount channelAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelIdChannelAccountsPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelIdChannelAccountsPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostChannelByExternalReferenceCodeChannelAccount()
		throws Exception {

		ChannelAccount randomChannelAccount = randomChannelAccount();

		ChannelAccount postChannelAccount =
			testPostChannelByExternalReferenceCodeChannelAccount_addChannelAccount(
				randomChannelAccount);

		assertEquals(randomChannelAccount, postChannelAccount);
		assertValid(postChannelAccount);
	}

	protected ChannelAccount
			testPostChannelByExternalReferenceCodeChannelAccount_addChannelAccount(
				ChannelAccount channelAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostChannelIdChannelAccount() throws Exception {
		ChannelAccount randomChannelAccount = randomChannelAccount();

		ChannelAccount postChannelAccount =
			testPostChannelIdChannelAccount_addChannelAccount(
				randomChannelAccount);

		assertEquals(randomChannelAccount, postChannelAccount);
		assertValid(postChannelAccount);
	}

	protected ChannelAccount testPostChannelIdChannelAccount_addChannelAccount(
			ChannelAccount channelAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ChannelAccount channelAccount1 =
			testBatchEngineDeleteImportTask_addChannelAccount();

		testBatchEngineDeleteImportTask_deleteChannelAccount(
			200, null, channelAccount1.getChannelAccountId());
	}

	protected ChannelAccount testBatchEngineDeleteImportTask_addChannelAccount()
		throws Exception {

		return testDeleteChannelAccount_addChannelAccount();
	}

	protected void testBatchEngineDeleteImportTask_deleteChannelAccount(
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
				"com.liferay.headless.commerce.admin.channel.dto.v1_0.ChannelAccount",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"channelAccountId", () -> id
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

	protected ChannelAccount testGraphQLChannelAccount_addChannelAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		ChannelAccount channelAccount, List<ChannelAccount> channelAccounts) {

		boolean contains = false;

		for (ChannelAccount item : channelAccounts) {
			if (equals(channelAccount, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			channelAccounts + " does not contain " + channelAccount, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ChannelAccount channelAccount1, ChannelAccount channelAccount2) {

		Assert.assertTrue(
			channelAccount1 + " does not equal " + channelAccount2,
			equals(channelAccount1, channelAccount2));
	}

	protected void assertEquals(
		List<ChannelAccount> channelAccounts1,
		List<ChannelAccount> channelAccounts2) {

		Assert.assertEquals(channelAccounts1.size(), channelAccounts2.size());

		for (int i = 0; i < channelAccounts1.size(); i++) {
			ChannelAccount channelAccount1 = channelAccounts1.get(i);
			ChannelAccount channelAccount2 = channelAccounts2.get(i);

			assertEquals(channelAccount1, channelAccount2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ChannelAccount> channelAccounts1,
		List<ChannelAccount> channelAccounts2) {

		Assert.assertEquals(channelAccounts1.size(), channelAccounts2.size());

		for (ChannelAccount channelAccount1 : channelAccounts1) {
			boolean contains = false;

			for (ChannelAccount channelAccount2 : channelAccounts2) {
				if (equals(channelAccount1, channelAccount2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				channelAccounts2 + " does not contain " + channelAccount1,
				contains);
		}
	}

	protected void assertValid(ChannelAccount channelAccount) throws Exception {
		boolean valid = true;

		if (channelAccount.getChannelAccountId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("account", additionalAssertFieldName)) {
				if (channelAccount.getAccount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"accountExternalReferenceCode",
					additionalAssertFieldName)) {

				if (channelAccount.getAccountExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (channelAccount.getAccountId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (channelAccount.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("channelAccountId", additionalAssertFieldName)) {
				if (channelAccount.getChannelAccountId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"channelExternalReferenceCode",
					additionalAssertFieldName)) {

				if (channelAccount.getChannelExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("channelId", additionalAssertFieldName)) {
				if (channelAccount.getChannelId() == null) {
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

	protected void assertValid(Page<ChannelAccount> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ChannelAccount> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ChannelAccount> channelAccounts = page.getItems();

		int size = channelAccounts.size();

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

		graphQLFields.add(new GraphQLField("channelAccountId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.channel.dto.v1_0.
						ChannelAccount.class)) {

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
		ChannelAccount channelAccount1, ChannelAccount channelAccount2) {

		if (channelAccount1 == channelAccount2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("account", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						channelAccount1.getAccount(),
						channelAccount2.getAccount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"accountExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						channelAccount1.getAccountExternalReferenceCode(),
						channelAccount2.getAccountExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						channelAccount1.getAccountId(),
						channelAccount2.getAccountId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)channelAccount1.getActions(),
						(Map)channelAccount2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("channelAccountId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						channelAccount1.getChannelAccountId(),
						channelAccount2.getChannelAccountId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"channelExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						channelAccount1.getChannelExternalReferenceCode(),
						channelAccount2.getChannelExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("channelId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						channelAccount1.getChannelId(),
						channelAccount2.getChannelId())) {

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

		if (!(_channelAccountResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_channelAccountResource;

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
		ChannelAccount channelAccount) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("account")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("accountExternalReferenceCode")) {
			Object object = channelAccount.getAccountExternalReferenceCode();

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

		if (entityFieldName.equals("accountId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("actions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("channelAccountId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("channelExternalReferenceCode")) {
			Object object = channelAccount.getChannelExternalReferenceCode();

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

		if (entityFieldName.equals("channelId")) {
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

	protected ChannelAccount randomChannelAccount() throws Exception {
		return new ChannelAccount() {
			{
				accountExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				accountId = RandomTestUtil.randomLong();
				channelAccountId = RandomTestUtil.randomLong();
				channelExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				channelId = RandomTestUtil.randomLong();
			}
		};
	}

	protected ChannelAccount randomIrrelevantChannelAccount() throws Exception {
		ChannelAccount randomIrrelevantChannelAccount = randomChannelAccount();

		return randomIrrelevantChannelAccount;
	}

	protected ChannelAccount randomPatchChannelAccount() throws Exception {
		return randomChannelAccount();
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

	protected ChannelAccountResource channelAccountResource;
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
		LogFactoryUtil.getLog(BaseChannelAccountResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.channel.resource.v1_0.
		ChannelAccountResource _channelAccountResource;

}