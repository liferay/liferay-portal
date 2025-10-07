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
import com.liferay.headless.commerce.admin.channel.client.dto.v1_0.AccountAddressChannel;
import com.liferay.headless.commerce.admin.channel.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.channel.client.pagination.Page;
import com.liferay.headless.commerce.admin.channel.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.channel.client.resource.v1_0.AccountAddressChannelResource;
import com.liferay.headless.commerce.admin.channel.client.serdes.v1_0.AccountAddressChannelSerDes;
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
public abstract class BaseAccountAddressChannelResourceTestCase {

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

		_accountAddressChannelResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		accountAddressChannelResource = AccountAddressChannelResource.builder(
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

		AccountAddressChannel accountAddressChannel1 =
			randomAccountAddressChannel();

		String json = objectMapper.writeValueAsString(accountAddressChannel1);

		AccountAddressChannel accountAddressChannel2 =
			AccountAddressChannelSerDes.toDTO(json);

		Assert.assertTrue(
			equals(accountAddressChannel1, accountAddressChannel2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		AccountAddressChannel accountAddressChannel =
			randomAccountAddressChannel();

		String json1 = objectMapper.writeValueAsString(accountAddressChannel);
		String json2 = AccountAddressChannelSerDes.toJSON(
			accountAddressChannel);

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

		AccountAddressChannel accountAddressChannel =
			randomAccountAddressChannel();

		accountAddressChannel.setAddressChannelExternalReferenceCode(regex);
		accountAddressChannel.setAddressExternalReferenceCode(regex);

		String json = AccountAddressChannelSerDes.toJSON(accountAddressChannel);

		Assert.assertFalse(json.contains(regex));

		accountAddressChannel = AccountAddressChannelSerDes.toDTO(json);

		Assert.assertEquals(
			regex,
			accountAddressChannel.getAddressChannelExternalReferenceCode());
		Assert.assertEquals(
			regex, accountAddressChannel.getAddressExternalReferenceCode());
	}

	@Test
	public void testDeleteAccountAddressChannel() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountAddressChannel accountAddressChannel =
			testDeleteAccountAddressChannel_addAccountAddressChannel();

		assertHttpResponseStatusCode(
			204,
			accountAddressChannelResource.
				deleteAccountAddressChannelHttpResponse(
					accountAddressChannel.getAccountAddressChannelId()));
	}

	protected AccountAddressChannel
			testDeleteAccountAddressChannel_addAccountAddressChannel()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteAccountAddressChannel() throws Exception {

		// No namespace

		AccountAddressChannel accountAddressChannel1 =
			testGraphQLDeleteAccountAddressChannel_addAccountAddressChannel();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteAccountAddressChannel",
						new HashMap<String, Object>() {
							{
								put(
									"accountAddressChannelId",
									accountAddressChannel1.
										getAccountAddressChannelId());
							}
						})),
				"JSONObject/data", "Object/deleteAccountAddressChannel"));

		// Using the namespace headlessCommerceAdminChannel_v1_0

		AccountAddressChannel accountAddressChannel2 =
			testGraphQLDeleteAccountAddressChannel_addAccountAddressChannel();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminChannel_v1_0",
						new GraphQLField(
							"deleteAccountAddressChannel",
							new HashMap<String, Object>() {
								{
									put(
										"accountAddressChannelId",
										accountAddressChannel2.
											getAccountAddressChannelId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminChannel_v1_0",
				"Object/deleteAccountAddressChannel"));
	}

	protected AccountAddressChannel
			testGraphQLDeleteAccountAddressChannel_addAccountAddressChannel()
		throws Exception {

		return testGraphQLAccountAddressChannel_addAccountAddressChannel();
	}

	@Test
	public void testDeleteAccountAddressChannelBatch() throws Exception {
		AccountAddressChannel accountAddressChannel1 =
			testDeleteAccountAddressChannelBatch_addAccountAddressChannel();

		testDeleteAccountAddressChannelBatch_deleteAccountAddressChannel(
			202, null, accountAddressChannel1.getAccountAddressChannelId());
	}

	protected AccountAddressChannel
			testDeleteAccountAddressChannelBatch_addAccountAddressChannel()
		throws Exception {

		return testDeleteAccountAddressChannel_addAccountAddressChannel();
	}

	protected void
			testDeleteAccountAddressChannelBatch_deleteAccountAddressChannel(
				int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			accountAddressChannelResource.
				deleteAccountAddressChannelBatchHttpResponse(
					null,
					JSONUtil.putAll(
						JSONUtil.put(
							"externalReferenceCode", () -> externalReferenceCode
						).put(
							"accountAddressChannelId", () -> id
						)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetAccountAddressByExternalReferenceCodeAccountAddressChannelsPage()
		throws Exception {

		String externalReferenceCode =
			testGetAccountAddressByExternalReferenceCodeAccountAddressChannelsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetAccountAddressByExternalReferenceCodeAccountAddressChannelsPage_getIrrelevantExternalReferenceCode();

		Page<AccountAddressChannel> page =
			accountAddressChannelResource.
				getAccountAddressByExternalReferenceCodeAccountAddressChannelsPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			AccountAddressChannel irrelevantAccountAddressChannel =
				testGetAccountAddressByExternalReferenceCodeAccountAddressChannelsPage_addAccountAddressChannel(
					irrelevantExternalReferenceCode,
					randomIrrelevantAccountAddressChannel());

			page =
				accountAddressChannelResource.
					getAccountAddressByExternalReferenceCodeAccountAddressChannelsPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAccountAddressChannel,
				(List<AccountAddressChannel>)page.getItems());
			assertValid(
				page,
				testGetAccountAddressByExternalReferenceCodeAccountAddressChannelsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		AccountAddressChannel accountAddressChannel1 =
			testGetAccountAddressByExternalReferenceCodeAccountAddressChannelsPage_addAccountAddressChannel(
				externalReferenceCode, randomAccountAddressChannel());

		AccountAddressChannel accountAddressChannel2 =
			testGetAccountAddressByExternalReferenceCodeAccountAddressChannelsPage_addAccountAddressChannel(
				externalReferenceCode, randomAccountAddressChannel());

		page =
			accountAddressChannelResource.
				getAccountAddressByExternalReferenceCodeAccountAddressChannelsPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			accountAddressChannel1,
			(List<AccountAddressChannel>)page.getItems());
		assertContains(
			accountAddressChannel2,
			(List<AccountAddressChannel>)page.getItems());
		assertValid(
			page,
			testGetAccountAddressByExternalReferenceCodeAccountAddressChannelsPage_getExpectedActions(
				externalReferenceCode));

		accountAddressChannelResource.deleteAccountAddressChannel(
			accountAddressChannel1.getAccountAddressChannelId());

		accountAddressChannelResource.deleteAccountAddressChannel(
			accountAddressChannel2.getAccountAddressChannelId());
	}

	protected Map<String, Map<String, String>>
			testGetAccountAddressByExternalReferenceCodeAccountAddressChannelsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAccountAddressByExternalReferenceCodeAccountAddressChannelsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetAccountAddressByExternalReferenceCodeAccountAddressChannelsPage_getExternalReferenceCode();

		Page<AccountAddressChannel> accountAddressChannelsPage =
			accountAddressChannelResource.
				getAccountAddressByExternalReferenceCodeAccountAddressChannelsPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			accountAddressChannelsPage.getTotalCount());

		AccountAddressChannel accountAddressChannel1 =
			testGetAccountAddressByExternalReferenceCodeAccountAddressChannelsPage_addAccountAddressChannel(
				externalReferenceCode, randomAccountAddressChannel());

		AccountAddressChannel accountAddressChannel2 =
			testGetAccountAddressByExternalReferenceCodeAccountAddressChannelsPage_addAccountAddressChannel(
				externalReferenceCode, randomAccountAddressChannel());

		AccountAddressChannel accountAddressChannel3 =
			testGetAccountAddressByExternalReferenceCodeAccountAddressChannelsPage_addAccountAddressChannel(
				externalReferenceCode, randomAccountAddressChannel());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<AccountAddressChannel> page1 =
				accountAddressChannelResource.
					getAccountAddressByExternalReferenceCodeAccountAddressChannelsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				accountAddressChannel1,
				(List<AccountAddressChannel>)page1.getItems());

			Page<AccountAddressChannel> page2 =
				accountAddressChannelResource.
					getAccountAddressByExternalReferenceCodeAccountAddressChannelsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				accountAddressChannel2,
				(List<AccountAddressChannel>)page2.getItems());

			Page<AccountAddressChannel> page3 =
				accountAddressChannelResource.
					getAccountAddressByExternalReferenceCodeAccountAddressChannelsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				accountAddressChannel3,
				(List<AccountAddressChannel>)page3.getItems());
		}
		else {
			Page<AccountAddressChannel> page1 =
				accountAddressChannelResource.
					getAccountAddressByExternalReferenceCodeAccountAddressChannelsPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<AccountAddressChannel> accountAddressChannels1 =
				(List<AccountAddressChannel>)page1.getItems();

			Assert.assertEquals(
				accountAddressChannels1.toString(), totalCount + 2,
				accountAddressChannels1.size());

			Page<AccountAddressChannel> page2 =
				accountAddressChannelResource.
					getAccountAddressByExternalReferenceCodeAccountAddressChannelsPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<AccountAddressChannel> accountAddressChannels2 =
				(List<AccountAddressChannel>)page2.getItems();

			Assert.assertEquals(
				accountAddressChannels2.toString(), 1,
				accountAddressChannels2.size());

			Page<AccountAddressChannel> page3 =
				accountAddressChannelResource.
					getAccountAddressByExternalReferenceCodeAccountAddressChannelsPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				accountAddressChannel1,
				(List<AccountAddressChannel>)page3.getItems());
			assertContains(
				accountAddressChannel2,
				(List<AccountAddressChannel>)page3.getItems());
			assertContains(
				accountAddressChannel3,
				(List<AccountAddressChannel>)page3.getItems());
		}
	}

	protected AccountAddressChannel
			testGetAccountAddressByExternalReferenceCodeAccountAddressChannelsPage_addAccountAddressChannel(
				String externalReferenceCode,
				AccountAddressChannel accountAddressChannel)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountAddressByExternalReferenceCodeAccountAddressChannelsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountAddressByExternalReferenceCodeAccountAddressChannelsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetAccountAddressIdAccountAddressChannelsPage()
		throws Exception {

		Long addressId =
			testGetAccountAddressIdAccountAddressChannelsPage_getAddressId();
		Long irrelevantAddressId =
			testGetAccountAddressIdAccountAddressChannelsPage_getIrrelevantAddressId();

		Page<AccountAddressChannel> page =
			accountAddressChannelResource.
				getAccountAddressIdAccountAddressChannelsPage(
					addressId, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantAddressId != null) {
			AccountAddressChannel irrelevantAccountAddressChannel =
				testGetAccountAddressIdAccountAddressChannelsPage_addAccountAddressChannel(
					irrelevantAddressId,
					randomIrrelevantAccountAddressChannel());

			page =
				accountAddressChannelResource.
					getAccountAddressIdAccountAddressChannelsPage(
						irrelevantAddressId, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAccountAddressChannel,
				(List<AccountAddressChannel>)page.getItems());
			assertValid(
				page,
				testGetAccountAddressIdAccountAddressChannelsPage_getExpectedActions(
					irrelevantAddressId));
		}

		AccountAddressChannel accountAddressChannel1 =
			testGetAccountAddressIdAccountAddressChannelsPage_addAccountAddressChannel(
				addressId, randomAccountAddressChannel());

		AccountAddressChannel accountAddressChannel2 =
			testGetAccountAddressIdAccountAddressChannelsPage_addAccountAddressChannel(
				addressId, randomAccountAddressChannel());

		page =
			accountAddressChannelResource.
				getAccountAddressIdAccountAddressChannelsPage(
					addressId, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			accountAddressChannel1,
			(List<AccountAddressChannel>)page.getItems());
		assertContains(
			accountAddressChannel2,
			(List<AccountAddressChannel>)page.getItems());
		assertValid(
			page,
			testGetAccountAddressIdAccountAddressChannelsPage_getExpectedActions(
				addressId));

		accountAddressChannelResource.deleteAccountAddressChannel(
			accountAddressChannel1.getAccountAddressChannelId());

		accountAddressChannelResource.deleteAccountAddressChannel(
			accountAddressChannel2.getAccountAddressChannelId());
	}

	protected Map<String, Map<String, String>>
			testGetAccountAddressIdAccountAddressChannelsPage_getExpectedActions(
				Long addressId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAccountAddressIdAccountAddressChannelsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long addressId =
			testGetAccountAddressIdAccountAddressChannelsPage_getAddressId();

		AccountAddressChannel accountAddressChannel1 =
			randomAccountAddressChannel();

		accountAddressChannel1 =
			testGetAccountAddressIdAccountAddressChannelsPage_addAccountAddressChannel(
				addressId, accountAddressChannel1);

		for (EntityField entityField : entityFields) {
			Page<AccountAddressChannel> page =
				accountAddressChannelResource.
					getAccountAddressIdAccountAddressChannelsPage(
						addressId, null,
						getFilterString(
							entityField, "between", accountAddressChannel1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(accountAddressChannel1),
				(List<AccountAddressChannel>)page.getItems());
		}
	}

	@Test
	public void testGetAccountAddressIdAccountAddressChannelsPageWithFilterDoubleEquals()
		throws Exception {

		testGetAccountAddressIdAccountAddressChannelsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetAccountAddressIdAccountAddressChannelsPageWithFilterStringContains()
		throws Exception {

		testGetAccountAddressIdAccountAddressChannelsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetAccountAddressIdAccountAddressChannelsPageWithFilterStringEquals()
		throws Exception {

		testGetAccountAddressIdAccountAddressChannelsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetAccountAddressIdAccountAddressChannelsPageWithFilterStringStartsWith()
		throws Exception {

		testGetAccountAddressIdAccountAddressChannelsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetAccountAddressIdAccountAddressChannelsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long addressId =
			testGetAccountAddressIdAccountAddressChannelsPage_getAddressId();

		AccountAddressChannel accountAddressChannel1 =
			testGetAccountAddressIdAccountAddressChannelsPage_addAccountAddressChannel(
				addressId, randomAccountAddressChannel());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountAddressChannel accountAddressChannel2 =
			testGetAccountAddressIdAccountAddressChannelsPage_addAccountAddressChannel(
				addressId, randomAccountAddressChannel());

		for (EntityField entityField : entityFields) {
			Page<AccountAddressChannel> page =
				accountAddressChannelResource.
					getAccountAddressIdAccountAddressChannelsPage(
						addressId, null,
						getFilterString(
							entityField, operator, accountAddressChannel1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(accountAddressChannel1),
				(List<AccountAddressChannel>)page.getItems());
		}
	}

	@Test
	public void testGetAccountAddressIdAccountAddressChannelsPageWithPagination()
		throws Exception {

		Long addressId =
			testGetAccountAddressIdAccountAddressChannelsPage_getAddressId();

		Page<AccountAddressChannel> accountAddressChannelsPage =
			accountAddressChannelResource.
				getAccountAddressIdAccountAddressChannelsPage(
					addressId, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			accountAddressChannelsPage.getTotalCount());

		AccountAddressChannel accountAddressChannel1 =
			testGetAccountAddressIdAccountAddressChannelsPage_addAccountAddressChannel(
				addressId, randomAccountAddressChannel());

		AccountAddressChannel accountAddressChannel2 =
			testGetAccountAddressIdAccountAddressChannelsPage_addAccountAddressChannel(
				addressId, randomAccountAddressChannel());

		AccountAddressChannel accountAddressChannel3 =
			testGetAccountAddressIdAccountAddressChannelsPage_addAccountAddressChannel(
				addressId, randomAccountAddressChannel());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<AccountAddressChannel> page1 =
				accountAddressChannelResource.
					getAccountAddressIdAccountAddressChannelsPage(
						addressId, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				accountAddressChannel1,
				(List<AccountAddressChannel>)page1.getItems());

			Page<AccountAddressChannel> page2 =
				accountAddressChannelResource.
					getAccountAddressIdAccountAddressChannelsPage(
						addressId, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				accountAddressChannel2,
				(List<AccountAddressChannel>)page2.getItems());

			Page<AccountAddressChannel> page3 =
				accountAddressChannelResource.
					getAccountAddressIdAccountAddressChannelsPage(
						addressId, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				accountAddressChannel3,
				(List<AccountAddressChannel>)page3.getItems());
		}
		else {
			Page<AccountAddressChannel> page1 =
				accountAddressChannelResource.
					getAccountAddressIdAccountAddressChannelsPage(
						addressId, null, null, Pagination.of(1, totalCount + 2),
						null);

			List<AccountAddressChannel> accountAddressChannels1 =
				(List<AccountAddressChannel>)page1.getItems();

			Assert.assertEquals(
				accountAddressChannels1.toString(), totalCount + 2,
				accountAddressChannels1.size());

			Page<AccountAddressChannel> page2 =
				accountAddressChannelResource.
					getAccountAddressIdAccountAddressChannelsPage(
						addressId, null, null, Pagination.of(2, totalCount + 2),
						null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<AccountAddressChannel> accountAddressChannels2 =
				(List<AccountAddressChannel>)page2.getItems();

			Assert.assertEquals(
				accountAddressChannels2.toString(), 1,
				accountAddressChannels2.size());

			Page<AccountAddressChannel> page3 =
				accountAddressChannelResource.
					getAccountAddressIdAccountAddressChannelsPage(
						addressId, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				accountAddressChannel1,
				(List<AccountAddressChannel>)page3.getItems());
			assertContains(
				accountAddressChannel2,
				(List<AccountAddressChannel>)page3.getItems());
			assertContains(
				accountAddressChannel3,
				(List<AccountAddressChannel>)page3.getItems());
		}
	}

	@Test
	public void testGetAccountAddressIdAccountAddressChannelsPageWithSortDateTime()
		throws Exception {

		testGetAccountAddressIdAccountAddressChannelsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, accountAddressChannel1, accountAddressChannel2) -> {
				BeanTestUtil.setProperty(
					accountAddressChannel1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAccountAddressIdAccountAddressChannelsPageWithSortDouble()
		throws Exception {

		testGetAccountAddressIdAccountAddressChannelsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, accountAddressChannel1, accountAddressChannel2) -> {
				BeanTestUtil.setProperty(
					accountAddressChannel1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					accountAddressChannel2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAccountAddressIdAccountAddressChannelsPageWithSortInteger()
		throws Exception {

		testGetAccountAddressIdAccountAddressChannelsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, accountAddressChannel1, accountAddressChannel2) -> {
				BeanTestUtil.setProperty(
					accountAddressChannel1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					accountAddressChannel2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAccountAddressIdAccountAddressChannelsPageWithSortString()
		throws Exception {

		testGetAccountAddressIdAccountAddressChannelsPageWithSort(
			EntityField.Type.STRING,
			(entityField, accountAddressChannel1, accountAddressChannel2) -> {
				Class<?> clazz = accountAddressChannel1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						accountAddressChannel1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						accountAddressChannel2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						accountAddressChannel1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						accountAddressChannel2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						accountAddressChannel1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						accountAddressChannel2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetAccountAddressIdAccountAddressChannelsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, AccountAddressChannel, AccountAddressChannel,
				 Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long addressId =
			testGetAccountAddressIdAccountAddressChannelsPage_getAddressId();

		AccountAddressChannel accountAddressChannel1 =
			randomAccountAddressChannel();
		AccountAddressChannel accountAddressChannel2 =
			randomAccountAddressChannel();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, accountAddressChannel1, accountAddressChannel2);
		}

		accountAddressChannel1 =
			testGetAccountAddressIdAccountAddressChannelsPage_addAccountAddressChannel(
				addressId, accountAddressChannel1);

		accountAddressChannel2 =
			testGetAccountAddressIdAccountAddressChannelsPage_addAccountAddressChannel(
				addressId, accountAddressChannel2);

		Page<AccountAddressChannel> page =
			accountAddressChannelResource.
				getAccountAddressIdAccountAddressChannelsPage(
					addressId, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<AccountAddressChannel> ascPage =
				accountAddressChannelResource.
					getAccountAddressIdAccountAddressChannelsPage(
						addressId, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				accountAddressChannel1,
				(List<AccountAddressChannel>)ascPage.getItems());
			assertContains(
				accountAddressChannel2,
				(List<AccountAddressChannel>)ascPage.getItems());

			Page<AccountAddressChannel> descPage =
				accountAddressChannelResource.
					getAccountAddressIdAccountAddressChannelsPage(
						addressId, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				accountAddressChannel2,
				(List<AccountAddressChannel>)descPage.getItems());
			assertContains(
				accountAddressChannel1,
				(List<AccountAddressChannel>)descPage.getItems());
		}
	}

	protected AccountAddressChannel
			testGetAccountAddressIdAccountAddressChannelsPage_addAccountAddressChannel(
				Long addressId, AccountAddressChannel accountAddressChannel)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetAccountAddressIdAccountAddressChannelsPage_getAddressId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetAccountAddressIdAccountAddressChannelsPage_getIrrelevantAddressId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostAccountAddressByExternalReferenceCodeAccountAddressChannel()
		throws Exception {

		AccountAddressChannel randomAccountAddressChannel =
			randomAccountAddressChannel();

		AccountAddressChannel postAccountAddressChannel =
			testPostAccountAddressByExternalReferenceCodeAccountAddressChannel_addAccountAddressChannel(
				randomAccountAddressChannel);

		assertEquals(randomAccountAddressChannel, postAccountAddressChannel);
		assertValid(postAccountAddressChannel);
	}

	protected AccountAddressChannel
			testPostAccountAddressByExternalReferenceCodeAccountAddressChannel_addAccountAddressChannel(
				AccountAddressChannel accountAddressChannel)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostAccountAddressIdAccountAddressChannel()
		throws Exception {

		AccountAddressChannel randomAccountAddressChannel =
			randomAccountAddressChannel();

		AccountAddressChannel postAccountAddressChannel =
			testPostAccountAddressIdAccountAddressChannel_addAccountAddressChannel(
				randomAccountAddressChannel);

		assertEquals(randomAccountAddressChannel, postAccountAddressChannel);
		assertValid(postAccountAddressChannel);
	}

	protected AccountAddressChannel
			testPostAccountAddressIdAccountAddressChannel_addAccountAddressChannel(
				AccountAddressChannel accountAddressChannel)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		AccountAddressChannel accountAddressChannel1 =
			testBatchEngineDeleteImportTask_addAccountAddressChannel();

		testBatchEngineDeleteImportTask_deleteAccountAddressChannel(
			200, null, accountAddressChannel1.getAccountAddressChannelId());
	}

	protected AccountAddressChannel
			testBatchEngineDeleteImportTask_addAccountAddressChannel()
		throws Exception {

		return testDeleteAccountAddressChannel_addAccountAddressChannel();
	}

	protected void testBatchEngineDeleteImportTask_deleteAccountAddressChannel(
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
				"com.liferay.headless.commerce.admin.channel.dto.v1_0.AccountAddressChannel",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"accountAddressChannelId", () -> id
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

	protected AccountAddressChannel
			testGraphQLAccountAddressChannel_addAccountAddressChannel()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		AccountAddressChannel accountAddressChannel,
		List<AccountAddressChannel> accountAddressChannels) {

		boolean contains = false;

		for (AccountAddressChannel item : accountAddressChannels) {
			if (equals(accountAddressChannel, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			accountAddressChannels + " does not contain " +
				accountAddressChannel,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		AccountAddressChannel accountAddressChannel1,
		AccountAddressChannel accountAddressChannel2) {

		Assert.assertTrue(
			accountAddressChannel1 + " does not equal " +
				accountAddressChannel2,
			equals(accountAddressChannel1, accountAddressChannel2));
	}

	protected void assertEquals(
		List<AccountAddressChannel> accountAddressChannels1,
		List<AccountAddressChannel> accountAddressChannels2) {

		Assert.assertEquals(
			accountAddressChannels1.size(), accountAddressChannels2.size());

		for (int i = 0; i < accountAddressChannels1.size(); i++) {
			AccountAddressChannel accountAddressChannel1 =
				accountAddressChannels1.get(i);
			AccountAddressChannel accountAddressChannel2 =
				accountAddressChannels2.get(i);

			assertEquals(accountAddressChannel1, accountAddressChannel2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<AccountAddressChannel> accountAddressChannels1,
		List<AccountAddressChannel> accountAddressChannels2) {

		Assert.assertEquals(
			accountAddressChannels1.size(), accountAddressChannels2.size());

		for (AccountAddressChannel accountAddressChannel1 :
				accountAddressChannels1) {

			boolean contains = false;

			for (AccountAddressChannel accountAddressChannel2 :
					accountAddressChannels2) {

				if (equals(accountAddressChannel1, accountAddressChannel2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				accountAddressChannels2 + " does not contain " +
					accountAddressChannel1,
				contains);
		}
	}

	protected void assertValid(AccountAddressChannel accountAddressChannel)
		throws Exception {

		boolean valid = true;

		if (accountAddressChannel.getAccountAddressChannelId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"accountAddressChannelId", additionalAssertFieldName)) {

				if (accountAddressChannel.getAccountAddressChannelId() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (accountAddressChannel.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"addressChannelExternalReferenceCode",
					additionalAssertFieldName)) {

				if (accountAddressChannel.
						getAddressChannelExternalReferenceCode() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("addressChannelId", additionalAssertFieldName)) {
				if (accountAddressChannel.getAddressChannelId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"addressExternalReferenceCode",
					additionalAssertFieldName)) {

				if (accountAddressChannel.getAddressExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("addressId", additionalAssertFieldName)) {
				if (accountAddressChannel.getAddressId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("channel", additionalAssertFieldName)) {
				if (accountAddressChannel.getChannel() == null) {
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

	protected void assertValid(Page<AccountAddressChannel> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<AccountAddressChannel> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<AccountAddressChannel> accountAddressChannels =
			page.getItems();

		int size = accountAddressChannels.size();

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

		graphQLFields.add(new GraphQLField("accountAddressChannelId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.channel.dto.v1_0.
						AccountAddressChannel.class)) {

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
		AccountAddressChannel accountAddressChannel1,
		AccountAddressChannel accountAddressChannel2) {

		if (accountAddressChannel1 == accountAddressChannel2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"accountAddressChannelId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						accountAddressChannel1.getAccountAddressChannelId(),
						accountAddressChannel2.getAccountAddressChannelId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)accountAddressChannel1.getActions(),
						(Map)accountAddressChannel2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"addressChannelExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						accountAddressChannel1.
							getAddressChannelExternalReferenceCode(),
						accountAddressChannel2.
							getAddressChannelExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("addressChannelId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountAddressChannel1.getAddressChannelId(),
						accountAddressChannel2.getAddressChannelId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"addressExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						accountAddressChannel1.
							getAddressExternalReferenceCode(),
						accountAddressChannel2.
							getAddressExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("addressId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountAddressChannel1.getAddressId(),
						accountAddressChannel2.getAddressId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("channel", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountAddressChannel1.getChannel(),
						accountAddressChannel2.getChannel())) {

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

		if (!(_accountAddressChannelResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_accountAddressChannelResource;

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
		AccountAddressChannel accountAddressChannel) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("accountAddressChannelId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("actions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("addressChannelExternalReferenceCode")) {
			Object object =
				accountAddressChannel.getAddressChannelExternalReferenceCode();

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

		if (entityFieldName.equals("addressChannelId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("addressExternalReferenceCode")) {
			Object object =
				accountAddressChannel.getAddressExternalReferenceCode();

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

		if (entityFieldName.equals("addressId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("channel")) {
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

	protected AccountAddressChannel randomAccountAddressChannel()
		throws Exception {

		return new AccountAddressChannel() {
			{
				accountAddressChannelId = RandomTestUtil.randomLong();
				addressChannelExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				addressChannelId = RandomTestUtil.randomLong();
				addressExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				addressId = RandomTestUtil.randomLong();
			}
		};
	}

	protected AccountAddressChannel randomIrrelevantAccountAddressChannel()
		throws Exception {

		AccountAddressChannel randomIrrelevantAccountAddressChannel =
			randomAccountAddressChannel();

		return randomIrrelevantAccountAddressChannel;
	}

	protected AccountAddressChannel randomPatchAccountAddressChannel()
		throws Exception {

		return randomAccountAddressChannel();
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

	protected AccountAddressChannelResource accountAddressChannelResource;
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
		LogFactoryUtil.getLog(BaseAccountAddressChannelResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.channel.resource.v1_0.
		AccountAddressChannelResource _accountAddressChannelResource;

}