/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.resource.v2_0.test;

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
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceListChannel;
import com.liferay.headless.commerce.admin.pricing.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Page;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.pricing.client.resource.v2_0.PriceListChannelResource;
import com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0.PriceListChannelSerDes;
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
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public abstract class BasePriceListChannelResourceTestCase {

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

		_priceListChannelResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		priceListChannelResource = PriceListChannelResource.builder(
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

		PriceListChannel priceListChannel1 = randomPriceListChannel();

		String json = objectMapper.writeValueAsString(priceListChannel1);

		PriceListChannel priceListChannel2 = PriceListChannelSerDes.toDTO(json);

		Assert.assertTrue(equals(priceListChannel1, priceListChannel2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		PriceListChannel priceListChannel = randomPriceListChannel();

		String json1 = objectMapper.writeValueAsString(priceListChannel);
		String json2 = PriceListChannelSerDes.toJSON(priceListChannel);

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

		PriceListChannel priceListChannel = randomPriceListChannel();

		priceListChannel.setChannelExternalReferenceCode(regex);
		priceListChannel.setPriceListExternalReferenceCode(regex);

		String json = PriceListChannelSerDes.toJSON(priceListChannel);

		Assert.assertFalse(json.contains(regex));

		priceListChannel = PriceListChannelSerDes.toDTO(json);

		Assert.assertEquals(
			regex, priceListChannel.getChannelExternalReferenceCode());
		Assert.assertEquals(
			regex, priceListChannel.getPriceListExternalReferenceCode());
	}

	@Test
	public void testDeletePriceListChannel() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		PriceListChannel priceListChannel =
			testDeletePriceListChannel_addPriceListChannel();

		assertHttpResponseStatusCode(
			204,
			priceListChannelResource.deletePriceListChannelHttpResponse(
				priceListChannel.getPriceListChannelId()));
	}

	protected PriceListChannel testDeletePriceListChannel_addPriceListChannel()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeletePriceListChannel() throws Exception {

		// No namespace

		PriceListChannel priceListChannel1 =
			testGraphQLDeletePriceListChannel_addPriceListChannel();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deletePriceListChannel",
						new HashMap<String, Object>() {
							{
								put(
									"priceListChannelId",
									priceListChannel1.getPriceListChannelId());
							}
						})),
				"JSONObject/data", "Object/deletePriceListChannel"));

		// Using the namespace headlessCommerceAdminPricing_v2_0

		PriceListChannel priceListChannel2 =
			testGraphQLDeletePriceListChannel_addPriceListChannel();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminPricing_v2_0",
						new GraphQLField(
							"deletePriceListChannel",
							new HashMap<String, Object>() {
								{
									put(
										"priceListChannelId",
										priceListChannel2.
											getPriceListChannelId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminPricing_v2_0",
				"Object/deletePriceListChannel"));
	}

	protected PriceListChannel
			testGraphQLDeletePriceListChannel_addPriceListChannel()
		throws Exception {

		return testGraphQLPriceListChannel_addPriceListChannel();
	}

	@Test
	public void testDeletePriceListChannelBatch() throws Exception {
		PriceListChannel priceListChannel1 =
			testDeletePriceListChannelBatch_addPriceListChannel();

		testDeletePriceListChannelBatch_deletePriceListChannel(
			202, null, priceListChannel1.getPriceListChannelId());
	}

	protected PriceListChannel
			testDeletePriceListChannelBatch_addPriceListChannel()
		throws Exception {

		return testDeletePriceListChannel_addPriceListChannel();
	}

	protected void testDeletePriceListChannelBatch_deletePriceListChannel(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			priceListChannelResource.deletePriceListChannelBatchHttpResponse(
				null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"priceListChannelId", () -> id
					)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetPriceListByExternalReferenceCodePriceListChannelsPage()
		throws Exception {

		String externalReferenceCode =
			testGetPriceListByExternalReferenceCodePriceListChannelsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetPriceListByExternalReferenceCodePriceListChannelsPage_getIrrelevantExternalReferenceCode();

		Page<PriceListChannel> page =
			priceListChannelResource.
				getPriceListByExternalReferenceCodePriceListChannelsPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			PriceListChannel irrelevantPriceListChannel =
				testGetPriceListByExternalReferenceCodePriceListChannelsPage_addPriceListChannel(
					irrelevantExternalReferenceCode,
					randomIrrelevantPriceListChannel());

			page =
				priceListChannelResource.
					getPriceListByExternalReferenceCodePriceListChannelsPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPriceListChannel,
				(List<PriceListChannel>)page.getItems());
			assertValid(
				page,
				testGetPriceListByExternalReferenceCodePriceListChannelsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		PriceListChannel priceListChannel1 =
			testGetPriceListByExternalReferenceCodePriceListChannelsPage_addPriceListChannel(
				externalReferenceCode, randomPriceListChannel());

		PriceListChannel priceListChannel2 =
			testGetPriceListByExternalReferenceCodePriceListChannelsPage_addPriceListChannel(
				externalReferenceCode, randomPriceListChannel());

		page =
			priceListChannelResource.
				getPriceListByExternalReferenceCodePriceListChannelsPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			priceListChannel1, (List<PriceListChannel>)page.getItems());
		assertContains(
			priceListChannel2, (List<PriceListChannel>)page.getItems());
		assertValid(
			page,
			testGetPriceListByExternalReferenceCodePriceListChannelsPage_getExpectedActions(
				externalReferenceCode));

		priceListChannelResource.deletePriceListChannel(
			priceListChannel1.getPriceListChannelId());

		priceListChannelResource.deletePriceListChannel(
			priceListChannel2.getPriceListChannelId());
	}

	protected Map<String, Map<String, String>>
			testGetPriceListByExternalReferenceCodePriceListChannelsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetPriceListByExternalReferenceCodePriceListChannelsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetPriceListByExternalReferenceCodePriceListChannelsPage_getExternalReferenceCode();

		Page<PriceListChannel> priceListChannelsPage =
			priceListChannelResource.
				getPriceListByExternalReferenceCodePriceListChannelsPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			priceListChannelsPage.getTotalCount());

		PriceListChannel priceListChannel1 =
			testGetPriceListByExternalReferenceCodePriceListChannelsPage_addPriceListChannel(
				externalReferenceCode, randomPriceListChannel());

		PriceListChannel priceListChannel2 =
			testGetPriceListByExternalReferenceCodePriceListChannelsPage_addPriceListChannel(
				externalReferenceCode, randomPriceListChannel());

		PriceListChannel priceListChannel3 =
			testGetPriceListByExternalReferenceCodePriceListChannelsPage_addPriceListChannel(
				externalReferenceCode, randomPriceListChannel());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<PriceListChannel> page1 =
				priceListChannelResource.
					getPriceListByExternalReferenceCodePriceListChannelsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				priceListChannel1, (List<PriceListChannel>)page1.getItems());

			Page<PriceListChannel> page2 =
				priceListChannelResource.
					getPriceListByExternalReferenceCodePriceListChannelsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				priceListChannel2, (List<PriceListChannel>)page2.getItems());

			Page<PriceListChannel> page3 =
				priceListChannelResource.
					getPriceListByExternalReferenceCodePriceListChannelsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				priceListChannel3, (List<PriceListChannel>)page3.getItems());
		}
		else {
			Page<PriceListChannel> page1 =
				priceListChannelResource.
					getPriceListByExternalReferenceCodePriceListChannelsPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<PriceListChannel> priceListChannels1 =
				(List<PriceListChannel>)page1.getItems();

			Assert.assertEquals(
				priceListChannels1.toString(), totalCount + 2,
				priceListChannels1.size());

			Page<PriceListChannel> page2 =
				priceListChannelResource.
					getPriceListByExternalReferenceCodePriceListChannelsPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PriceListChannel> priceListChannels2 =
				(List<PriceListChannel>)page2.getItems();

			Assert.assertEquals(
				priceListChannels2.toString(), 1, priceListChannels2.size());

			Page<PriceListChannel> page3 =
				priceListChannelResource.
					getPriceListByExternalReferenceCodePriceListChannelsPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				priceListChannel1, (List<PriceListChannel>)page3.getItems());
			assertContains(
				priceListChannel2, (List<PriceListChannel>)page3.getItems());
			assertContains(
				priceListChannel3, (List<PriceListChannel>)page3.getItems());
		}
	}

	protected PriceListChannel
			testGetPriceListByExternalReferenceCodePriceListChannelsPage_addPriceListChannel(
				String externalReferenceCode, PriceListChannel priceListChannel)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetPriceListByExternalReferenceCodePriceListChannelsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetPriceListByExternalReferenceCodePriceListChannelsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetPriceListIdPriceListChannelsPage() throws Exception {
		Long id = testGetPriceListIdPriceListChannelsPage_getId();
		Long irrelevantId =
			testGetPriceListIdPriceListChannelsPage_getIrrelevantId();

		Page<PriceListChannel> page =
			priceListChannelResource.getPriceListIdPriceListChannelsPage(
				id, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			PriceListChannel irrelevantPriceListChannel =
				testGetPriceListIdPriceListChannelsPage_addPriceListChannel(
					irrelevantId, randomIrrelevantPriceListChannel());

			page = priceListChannelResource.getPriceListIdPriceListChannelsPage(
				irrelevantId, null, null, Pagination.of(1, (int)totalCount + 1),
				null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPriceListChannel,
				(List<PriceListChannel>)page.getItems());
			assertValid(
				page,
				testGetPriceListIdPriceListChannelsPage_getExpectedActions(
					irrelevantId));
		}

		PriceListChannel priceListChannel1 =
			testGetPriceListIdPriceListChannelsPage_addPriceListChannel(
				id, randomPriceListChannel());

		PriceListChannel priceListChannel2 =
			testGetPriceListIdPriceListChannelsPage_addPriceListChannel(
				id, randomPriceListChannel());

		page = priceListChannelResource.getPriceListIdPriceListChannelsPage(
			id, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			priceListChannel1, (List<PriceListChannel>)page.getItems());
		assertContains(
			priceListChannel2, (List<PriceListChannel>)page.getItems());
		assertValid(
			page,
			testGetPriceListIdPriceListChannelsPage_getExpectedActions(id));

		priceListChannelResource.deletePriceListChannel(
			priceListChannel1.getPriceListChannelId());

		priceListChannelResource.deletePriceListChannel(
			priceListChannel2.getPriceListChannelId());
	}

	protected Map<String, Map<String, String>>
			testGetPriceListIdPriceListChannelsPage_getExpectedActions(Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetPriceListIdPriceListChannelsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetPriceListIdPriceListChannelsPage_getId();

		PriceListChannel priceListChannel1 = randomPriceListChannel();

		priceListChannel1 =
			testGetPriceListIdPriceListChannelsPage_addPriceListChannel(
				id, priceListChannel1);

		for (EntityField entityField : entityFields) {
			Page<PriceListChannel> page =
				priceListChannelResource.getPriceListIdPriceListChannelsPage(
					id, null,
					getFilterString(entityField, "between", priceListChannel1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(priceListChannel1),
				(List<PriceListChannel>)page.getItems());
		}
	}

	@Test
	public void testGetPriceListIdPriceListChannelsPageWithFilterDoubleEquals()
		throws Exception {

		testGetPriceListIdPriceListChannelsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetPriceListIdPriceListChannelsPageWithFilterStringContains()
		throws Exception {

		testGetPriceListIdPriceListChannelsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetPriceListIdPriceListChannelsPageWithFilterStringEquals()
		throws Exception {

		testGetPriceListIdPriceListChannelsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetPriceListIdPriceListChannelsPageWithFilterStringStartsWith()
		throws Exception {

		testGetPriceListIdPriceListChannelsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetPriceListIdPriceListChannelsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetPriceListIdPriceListChannelsPage_getId();

		PriceListChannel priceListChannel1 =
			testGetPriceListIdPriceListChannelsPage_addPriceListChannel(
				id, randomPriceListChannel());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PriceListChannel priceListChannel2 =
			testGetPriceListIdPriceListChannelsPage_addPriceListChannel(
				id, randomPriceListChannel());

		for (EntityField entityField : entityFields) {
			Page<PriceListChannel> page =
				priceListChannelResource.getPriceListIdPriceListChannelsPage(
					id, null,
					getFilterString(entityField, operator, priceListChannel1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(priceListChannel1),
				(List<PriceListChannel>)page.getItems());
		}
	}

	@Test
	public void testGetPriceListIdPriceListChannelsPageWithPagination()
		throws Exception {

		Long id = testGetPriceListIdPriceListChannelsPage_getId();

		Page<PriceListChannel> priceListChannelsPage =
			priceListChannelResource.getPriceListIdPriceListChannelsPage(
				id, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			priceListChannelsPage.getTotalCount());

		PriceListChannel priceListChannel1 =
			testGetPriceListIdPriceListChannelsPage_addPriceListChannel(
				id, randomPriceListChannel());

		PriceListChannel priceListChannel2 =
			testGetPriceListIdPriceListChannelsPage_addPriceListChannel(
				id, randomPriceListChannel());

		PriceListChannel priceListChannel3 =
			testGetPriceListIdPriceListChannelsPage_addPriceListChannel(
				id, randomPriceListChannel());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<PriceListChannel> page1 =
				priceListChannelResource.getPriceListIdPriceListChannelsPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				priceListChannel1, (List<PriceListChannel>)page1.getItems());

			Page<PriceListChannel> page2 =
				priceListChannelResource.getPriceListIdPriceListChannelsPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				priceListChannel2, (List<PriceListChannel>)page2.getItems());

			Page<PriceListChannel> page3 =
				priceListChannelResource.getPriceListIdPriceListChannelsPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				priceListChannel3, (List<PriceListChannel>)page3.getItems());
		}
		else {
			Page<PriceListChannel> page1 =
				priceListChannelResource.getPriceListIdPriceListChannelsPage(
					id, null, null, Pagination.of(1, totalCount + 2), null);

			List<PriceListChannel> priceListChannels1 =
				(List<PriceListChannel>)page1.getItems();

			Assert.assertEquals(
				priceListChannels1.toString(), totalCount + 2,
				priceListChannels1.size());

			Page<PriceListChannel> page2 =
				priceListChannelResource.getPriceListIdPriceListChannelsPage(
					id, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PriceListChannel> priceListChannels2 =
				(List<PriceListChannel>)page2.getItems();

			Assert.assertEquals(
				priceListChannels2.toString(), 1, priceListChannels2.size());

			Page<PriceListChannel> page3 =
				priceListChannelResource.getPriceListIdPriceListChannelsPage(
					id, null, null, Pagination.of(1, (int)totalCount + 3),
					null);

			assertContains(
				priceListChannel1, (List<PriceListChannel>)page3.getItems());
			assertContains(
				priceListChannel2, (List<PriceListChannel>)page3.getItems());
			assertContains(
				priceListChannel3, (List<PriceListChannel>)page3.getItems());
		}
	}

	@Test
	public void testGetPriceListIdPriceListChannelsPageWithSortDateTime()
		throws Exception {

		testGetPriceListIdPriceListChannelsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, priceListChannel1, priceListChannel2) -> {
				BeanTestUtil.setProperty(
					priceListChannel1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetPriceListIdPriceListChannelsPageWithSortDouble()
		throws Exception {

		testGetPriceListIdPriceListChannelsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, priceListChannel1, priceListChannel2) -> {
				BeanTestUtil.setProperty(
					priceListChannel1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					priceListChannel2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetPriceListIdPriceListChannelsPageWithSortInteger()
		throws Exception {

		testGetPriceListIdPriceListChannelsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, priceListChannel1, priceListChannel2) -> {
				BeanTestUtil.setProperty(
					priceListChannel1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					priceListChannel2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetPriceListIdPriceListChannelsPageWithSortString()
		throws Exception {

		testGetPriceListIdPriceListChannelsPageWithSort(
			EntityField.Type.STRING,
			(entityField, priceListChannel1, priceListChannel2) -> {
				Class<?> clazz = priceListChannel1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						priceListChannel1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						priceListChannel2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						priceListChannel1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						priceListChannel2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						priceListChannel1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						priceListChannel2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetPriceListIdPriceListChannelsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, PriceListChannel, PriceListChannel, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetPriceListIdPriceListChannelsPage_getId();

		PriceListChannel priceListChannel1 = randomPriceListChannel();
		PriceListChannel priceListChannel2 = randomPriceListChannel();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, priceListChannel1, priceListChannel2);
		}

		priceListChannel1 =
			testGetPriceListIdPriceListChannelsPage_addPriceListChannel(
				id, priceListChannel1);

		priceListChannel2 =
			testGetPriceListIdPriceListChannelsPage_addPriceListChannel(
				id, priceListChannel2);

		Page<PriceListChannel> page =
			priceListChannelResource.getPriceListIdPriceListChannelsPage(
				id, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<PriceListChannel> ascPage =
				priceListChannelResource.getPriceListIdPriceListChannelsPage(
					id, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				priceListChannel1, (List<PriceListChannel>)ascPage.getItems());
			assertContains(
				priceListChannel2, (List<PriceListChannel>)ascPage.getItems());

			Page<PriceListChannel> descPage =
				priceListChannelResource.getPriceListIdPriceListChannelsPage(
					id, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				priceListChannel2, (List<PriceListChannel>)descPage.getItems());
			assertContains(
				priceListChannel1, (List<PriceListChannel>)descPage.getItems());
		}
	}

	protected PriceListChannel
			testGetPriceListIdPriceListChannelsPage_addPriceListChannel(
				Long id, PriceListChannel priceListChannel)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetPriceListIdPriceListChannelsPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetPriceListIdPriceListChannelsPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostPriceListByExternalReferenceCodePriceListChannel()
		throws Exception {

		PriceListChannel randomPriceListChannel = randomPriceListChannel();

		PriceListChannel postPriceListChannel =
			testPostPriceListByExternalReferenceCodePriceListChannel_addPriceListChannel(
				randomPriceListChannel);

		assertEquals(randomPriceListChannel, postPriceListChannel);
		assertValid(postPriceListChannel);
	}

	protected PriceListChannel
			testPostPriceListByExternalReferenceCodePriceListChannel_addPriceListChannel(
				PriceListChannel priceListChannel)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostPriceListIdPriceListChannel() throws Exception {
		PriceListChannel randomPriceListChannel = randomPriceListChannel();

		PriceListChannel postPriceListChannel =
			testPostPriceListIdPriceListChannel_addPriceListChannel(
				randomPriceListChannel);

		assertEquals(randomPriceListChannel, postPriceListChannel);
		assertValid(postPriceListChannel);
	}

	protected PriceListChannel
			testPostPriceListIdPriceListChannel_addPriceListChannel(
				PriceListChannel priceListChannel)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		PriceListChannel priceListChannel1 =
			testBatchEngineDeleteImportTask_addPriceListChannel();

		testBatchEngineDeleteImportTask_deletePriceListChannel(
			200, null, priceListChannel1.getPriceListChannelId());
	}

	protected PriceListChannel
			testBatchEngineDeleteImportTask_addPriceListChannel()
		throws Exception {

		return testDeletePriceListChannel_addPriceListChannel();
	}

	protected void testBatchEngineDeleteImportTask_deletePriceListChannel(
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
				"com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceListChannel",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"priceListChannelId", () -> id
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

	protected PriceListChannel testGraphQLPriceListChannel_addPriceListChannel()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		PriceListChannel priceListChannel,
		List<PriceListChannel> priceListChannels) {

		boolean contains = false;

		for (PriceListChannel item : priceListChannels) {
			if (equals(priceListChannel, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			priceListChannels + " does not contain " + priceListChannel,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		PriceListChannel priceListChannel1,
		PriceListChannel priceListChannel2) {

		Assert.assertTrue(
			priceListChannel1 + " does not equal " + priceListChannel2,
			equals(priceListChannel1, priceListChannel2));
	}

	protected void assertEquals(
		List<PriceListChannel> priceListChannels1,
		List<PriceListChannel> priceListChannels2) {

		Assert.assertEquals(
			priceListChannels1.size(), priceListChannels2.size());

		for (int i = 0; i < priceListChannels1.size(); i++) {
			PriceListChannel priceListChannel1 = priceListChannels1.get(i);
			PriceListChannel priceListChannel2 = priceListChannels2.get(i);

			assertEquals(priceListChannel1, priceListChannel2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<PriceListChannel> priceListChannels1,
		List<PriceListChannel> priceListChannels2) {

		Assert.assertEquals(
			priceListChannels1.size(), priceListChannels2.size());

		for (PriceListChannel priceListChannel1 : priceListChannels1) {
			boolean contains = false;

			for (PriceListChannel priceListChannel2 : priceListChannels2) {
				if (equals(priceListChannel1, priceListChannel2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				priceListChannels2 + " does not contain " + priceListChannel1,
				contains);
		}
	}

	protected void assertValid(PriceListChannel priceListChannel)
		throws Exception {

		boolean valid = true;

		if (priceListChannel.getPriceListChannelId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (priceListChannel.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("channel", additionalAssertFieldName)) {
				if (priceListChannel.getChannel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"channelExternalReferenceCode",
					additionalAssertFieldName)) {

				if (priceListChannel.getChannelExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("channelId", additionalAssertFieldName)) {
				if (priceListChannel.getChannelId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("order", additionalAssertFieldName)) {
				if (priceListChannel.getOrder() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"priceListChannelId", additionalAssertFieldName)) {

				if (priceListChannel.getPriceListChannelId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"priceListExternalReferenceCode",
					additionalAssertFieldName)) {

				if (priceListChannel.getPriceListExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("priceListId", additionalAssertFieldName)) {
				if (priceListChannel.getPriceListId() == null) {
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

	protected void assertValid(Page<PriceListChannel> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<PriceListChannel> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<PriceListChannel> priceListChannels =
			page.getItems();

		int size = priceListChannels.size();

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

		graphQLFields.add(new GraphQLField("priceListChannelId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.pricing.dto.v2_0.
						PriceListChannel.class)) {

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
		PriceListChannel priceListChannel1,
		PriceListChannel priceListChannel2) {

		if (priceListChannel1 == priceListChannel2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)priceListChannel1.getActions(),
						(Map)priceListChannel2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("channel", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceListChannel1.getChannel(),
						priceListChannel2.getChannel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"channelExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceListChannel1.getChannelExternalReferenceCode(),
						priceListChannel2.getChannelExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("channelId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceListChannel1.getChannelId(),
						priceListChannel2.getChannelId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("order", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceListChannel1.getOrder(),
						priceListChannel2.getOrder())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"priceListChannelId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceListChannel1.getPriceListChannelId(),
						priceListChannel2.getPriceListChannelId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"priceListExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceListChannel1.getPriceListExternalReferenceCode(),
						priceListChannel2.
							getPriceListExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priceListId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceListChannel1.getPriceListId(),
						priceListChannel2.getPriceListId())) {

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

		if (!(_priceListChannelResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_priceListChannelResource;

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
		PriceListChannel priceListChannel) {

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

		if (entityFieldName.equals("channel")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("channelExternalReferenceCode")) {
			Object object = priceListChannel.getChannelExternalReferenceCode();

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

		if (entityFieldName.equals("order")) {
			sb.append(String.valueOf(priceListChannel.getOrder()));

			return sb.toString();
		}

		if (entityFieldName.equals("priceListChannelId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priceListExternalReferenceCode")) {
			Object object =
				priceListChannel.getPriceListExternalReferenceCode();

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

		if (entityFieldName.equals("priceListId")) {
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

	protected PriceListChannel randomPriceListChannel() throws Exception {
		return new PriceListChannel() {
			{
				channelExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				channelId = RandomTestUtil.randomLong();
				order = RandomTestUtil.randomInt();
				priceListChannelId = RandomTestUtil.randomLong();
				priceListExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				priceListId = RandomTestUtil.randomLong();
			}
		};
	}

	protected PriceListChannel randomIrrelevantPriceListChannel()
		throws Exception {

		PriceListChannel randomIrrelevantPriceListChannel =
			randomPriceListChannel();

		return randomIrrelevantPriceListChannel;
	}

	protected PriceListChannel randomPatchPriceListChannel() throws Exception {
		return randomPriceListChannel();
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

	protected PriceListChannelResource priceListChannelResource;
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
		LogFactoryUtil.getLog(BasePriceListChannelResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.pricing.resource.v2_0.
		PriceListChannelResource _priceListChannelResource;

}