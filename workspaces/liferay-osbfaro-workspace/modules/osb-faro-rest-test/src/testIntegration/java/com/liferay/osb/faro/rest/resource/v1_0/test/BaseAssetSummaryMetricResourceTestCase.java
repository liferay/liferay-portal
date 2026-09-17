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

import com.liferay.osb.faro.rest.client.dto.v1_0.AssetSummaryMetric;
import com.liferay.osb.faro.rest.client.http.HttpInvoker;
import com.liferay.osb.faro.rest.client.pagination.Page;
import com.liferay.osb.faro.rest.client.pagination.Pagination;
import com.liferay.osb.faro.rest.client.resource.v1_0.AssetSummaryMetricResource;
import com.liferay.osb.faro.rest.client.serdes.v1_0.AssetSummaryMetricSerDes;
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
public abstract class BaseAssetSummaryMetricResourceTestCase {

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

		_assetSummaryMetricResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		assetSummaryMetricResource = AssetSummaryMetricResource.builder(
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

		AssetSummaryMetric assetSummaryMetric1 = randomAssetSummaryMetric();

		String json = objectMapper.writeValueAsString(assetSummaryMetric1);

		AssetSummaryMetric assetSummaryMetric2 = AssetSummaryMetricSerDes.toDTO(
			json);

		Assert.assertTrue(equals(assetSummaryMetric1, assetSummaryMetric2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		AssetSummaryMetric assetSummaryMetric = randomAssetSummaryMetric();

		String json1 = objectMapper.writeValueAsString(assetSummaryMetric);
		String json2 = AssetSummaryMetricSerDes.toJSON(assetSummaryMetric);

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

		AssetSummaryMetric assetSummaryMetric = randomAssetSummaryMetric();

		assetSummaryMetric.setAssetId(regex);
		assetSummaryMetric.setAssetTitle(regex);
		assetSummaryMetric.setAssetType(regex);

		String json = AssetSummaryMetricSerDes.toJSON(assetSummaryMetric);

		Assert.assertFalse(json.contains(regex));

		assetSummaryMetric = AssetSummaryMetricSerDes.toDTO(json);

		Assert.assertEquals(regex, assetSummaryMetric.getAssetId());
		Assert.assertEquals(regex, assetSummaryMetric.getAssetTitle());
		Assert.assertEquals(regex, assetSummaryMetric.getAssetType());
	}

	@Test
	public void testGetWorkspaceGroupChannelAssetSummariesPage()
		throws Exception {

		Long groupId =
			testGetWorkspaceGroupChannelAssetSummariesPage_getGroupId();
		Long irrelevantGroupId =
			testGetWorkspaceGroupChannelAssetSummariesPage_getIrrelevantGroupId();
		String channelId =
			testGetWorkspaceGroupChannelAssetSummariesPage_getChannelId();
		String irrelevantChannelId =
			testGetWorkspaceGroupChannelAssetSummariesPage_getIrrelevantChannelId();

		Page<AssetSummaryMetric> page =
			assetSummaryMetricResource.
				getWorkspaceGroupChannelAssetSummariesPage(
					groupId, channelId, RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), null,
					RandomTestUtil.randomString(), Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if ((irrelevantGroupId != null) && (irrelevantChannelId != null)) {
			AssetSummaryMetric irrelevantAssetSummaryMetric =
				testGetWorkspaceGroupChannelAssetSummariesPage_addAssetSummaryMetric(
					irrelevantGroupId, irrelevantChannelId,
					randomIrrelevantAssetSummaryMetric());

			page =
				assetSummaryMetricResource.
					getWorkspaceGroupChannelAssetSummariesPage(
						irrelevantGroupId, irrelevantChannelId, null, null,
						null, null, null, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAssetSummaryMetric,
				(List<AssetSummaryMetric>)page.getItems());
			assertValid(
				page,
				testGetWorkspaceGroupChannelAssetSummariesPage_getExpectedActions(
					irrelevantGroupId, irrelevantChannelId));
		}

		AssetSummaryMetric assetSummaryMetric1 =
			testGetWorkspaceGroupChannelAssetSummariesPage_addAssetSummaryMetric(
				groupId, channelId, randomAssetSummaryMetric());

		AssetSummaryMetric assetSummaryMetric2 =
			testGetWorkspaceGroupChannelAssetSummariesPage_addAssetSummaryMetric(
				groupId, channelId, randomAssetSummaryMetric());

		page =
			assetSummaryMetricResource.
				getWorkspaceGroupChannelAssetSummariesPage(
					groupId, channelId, null, null, null, null, null, null,
					null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			assetSummaryMetric1, (List<AssetSummaryMetric>)page.getItems());
		assertContains(
			assetSummaryMetric2, (List<AssetSummaryMetric>)page.getItems());
		assertValid(
			page,
			testGetWorkspaceGroupChannelAssetSummariesPage_getExpectedActions(
				groupId, channelId));
	}

	protected Map<String, Map<String, String>>
			testGetWorkspaceGroupChannelAssetSummariesPage_getExpectedActions(
				Long groupId, String channelId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWorkspaceGroupChannelAssetSummariesPageWithPagination()
		throws Exception {

		Long groupId =
			testGetWorkspaceGroupChannelAssetSummariesPage_getGroupId();
		String channelId =
			testGetWorkspaceGroupChannelAssetSummariesPage_getChannelId();

		Page<AssetSummaryMetric> assetSummaryMetricsPage =
			assetSummaryMetricResource.
				getWorkspaceGroupChannelAssetSummariesPage(
					groupId, channelId, null, null, null, null, null, null,
					null, null, null);

		int totalCount = GetterUtil.getInteger(
			assetSummaryMetricsPage.getTotalCount());

		AssetSummaryMetric assetSummaryMetric1 =
			testGetWorkspaceGroupChannelAssetSummariesPage_addAssetSummaryMetric(
				groupId, channelId, randomAssetSummaryMetric());

		AssetSummaryMetric assetSummaryMetric2 =
			testGetWorkspaceGroupChannelAssetSummariesPage_addAssetSummaryMetric(
				groupId, channelId, randomAssetSummaryMetric());

		AssetSummaryMetric assetSummaryMetric3 =
			testGetWorkspaceGroupChannelAssetSummariesPage_addAssetSummaryMetric(
				groupId, channelId, randomAssetSummaryMetric());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<AssetSummaryMetric> page1 =
				assetSummaryMetricResource.
					getWorkspaceGroupChannelAssetSummariesPage(
						groupId, channelId, null, null, null, null, null, null,
						null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				assetSummaryMetric1,
				(List<AssetSummaryMetric>)page1.getItems());

			Page<AssetSummaryMetric> page2 =
				assetSummaryMetricResource.
					getWorkspaceGroupChannelAssetSummariesPage(
						groupId, channelId, null, null, null, null, null, null,
						null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				assetSummaryMetric2,
				(List<AssetSummaryMetric>)page2.getItems());

			Page<AssetSummaryMetric> page3 =
				assetSummaryMetricResource.
					getWorkspaceGroupChannelAssetSummariesPage(
						groupId, channelId, null, null, null, null, null, null,
						null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				assetSummaryMetric3,
				(List<AssetSummaryMetric>)page3.getItems());
		}
		else {
			Page<AssetSummaryMetric> page1 =
				assetSummaryMetricResource.
					getWorkspaceGroupChannelAssetSummariesPage(
						groupId, channelId, null, null, null, null, null, null,
						null, Pagination.of(1, totalCount + 2), null);

			List<AssetSummaryMetric> assetSummaryMetrics1 =
				(List<AssetSummaryMetric>)page1.getItems();

			Assert.assertEquals(
				assetSummaryMetrics1.toString(), totalCount + 2,
				assetSummaryMetrics1.size());

			Page<AssetSummaryMetric> page2 =
				assetSummaryMetricResource.
					getWorkspaceGroupChannelAssetSummariesPage(
						groupId, channelId, null, null, null, null, null, null,
						null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<AssetSummaryMetric> assetSummaryMetrics2 =
				(List<AssetSummaryMetric>)page2.getItems();

			Assert.assertEquals(
				assetSummaryMetrics2.toString(), 1,
				assetSummaryMetrics2.size());

			Page<AssetSummaryMetric> page3 =
				assetSummaryMetricResource.
					getWorkspaceGroupChannelAssetSummariesPage(
						groupId, channelId, null, null, null, null, null, null,
						null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				assetSummaryMetric1,
				(List<AssetSummaryMetric>)page3.getItems());
			assertContains(
				assetSummaryMetric2,
				(List<AssetSummaryMetric>)page3.getItems());
			assertContains(
				assetSummaryMetric3,
				(List<AssetSummaryMetric>)page3.getItems());
		}
	}

	@Test
	public void testGetWorkspaceGroupChannelAssetSummariesPageWithSortDateTime()
		throws Exception {

		testGetWorkspaceGroupChannelAssetSummariesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, assetSummaryMetric1, assetSummaryMetric2) -> {
				BeanTestUtil.setProperty(
					assetSummaryMetric1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetWorkspaceGroupChannelAssetSummariesPageWithSortDouble()
		throws Exception {

		testGetWorkspaceGroupChannelAssetSummariesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, assetSummaryMetric1, assetSummaryMetric2) -> {
				BeanTestUtil.setProperty(
					assetSummaryMetric1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					assetSummaryMetric2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetWorkspaceGroupChannelAssetSummariesPageWithSortInteger()
		throws Exception {

		testGetWorkspaceGroupChannelAssetSummariesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, assetSummaryMetric1, assetSummaryMetric2) -> {
				BeanTestUtil.setProperty(
					assetSummaryMetric1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					assetSummaryMetric2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetWorkspaceGroupChannelAssetSummariesPageWithSortString()
		throws Exception {

		testGetWorkspaceGroupChannelAssetSummariesPageWithSort(
			EntityField.Type.STRING,
			(entityField, assetSummaryMetric1, assetSummaryMetric2) -> {
				Class<?> clazz = assetSummaryMetric1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						assetSummaryMetric1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						assetSummaryMetric2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						assetSummaryMetric1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						assetSummaryMetric2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						assetSummaryMetric1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						assetSummaryMetric2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetWorkspaceGroupChannelAssetSummariesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, AssetSummaryMetric, AssetSummaryMetric, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long groupId =
			testGetWorkspaceGroupChannelAssetSummariesPage_getGroupId();
		String channelId =
			testGetWorkspaceGroupChannelAssetSummariesPage_getChannelId();

		AssetSummaryMetric assetSummaryMetric1 = randomAssetSummaryMetric();
		AssetSummaryMetric assetSummaryMetric2 = randomAssetSummaryMetric();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, assetSummaryMetric1, assetSummaryMetric2);
		}

		assetSummaryMetric1 =
			testGetWorkspaceGroupChannelAssetSummariesPage_addAssetSummaryMetric(
				groupId, channelId, assetSummaryMetric1);

		assetSummaryMetric2 =
			testGetWorkspaceGroupChannelAssetSummariesPage_addAssetSummaryMetric(
				groupId, channelId, assetSummaryMetric2);

		Page<AssetSummaryMetric> page =
			assetSummaryMetricResource.
				getWorkspaceGroupChannelAssetSummariesPage(
					groupId, channelId, null, null, null, null, null, null,
					null, null, null);

		for (EntityField entityField : entityFields) {
			Page<AssetSummaryMetric> ascPage =
				assetSummaryMetricResource.
					getWorkspaceGroupChannelAssetSummariesPage(
						groupId, channelId, null, null, null, null, null, null,
						null, Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				assetSummaryMetric1,
				(List<AssetSummaryMetric>)ascPage.getItems());
			assertContains(
				assetSummaryMetric2,
				(List<AssetSummaryMetric>)ascPage.getItems());

			Page<AssetSummaryMetric> descPage =
				assetSummaryMetricResource.
					getWorkspaceGroupChannelAssetSummariesPage(
						groupId, channelId, null, null, null, null, null, null,
						null, Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				assetSummaryMetric2,
				(List<AssetSummaryMetric>)descPage.getItems());
			assertContains(
				assetSummaryMetric1,
				(List<AssetSummaryMetric>)descPage.getItems());
		}
	}

	protected AssetSummaryMetric
			testGetWorkspaceGroupChannelAssetSummariesPage_addAssetSummaryMetric(
				Long groupId, String channelId,
				AssetSummaryMetric assetSummaryMetric)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetWorkspaceGroupChannelAssetSummariesPage_getGroupId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkspaceGroupChannelAssetSummariesPage_getIrrelevantGroupId()
		throws Exception {

		return null;
	}

	protected String
			testGetWorkspaceGroupChannelAssetSummariesPage_getChannelId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetWorkspaceGroupChannelAssetSummariesPage_getIrrelevantChannelId()
		throws Exception {

		return null;
	}

	protected void assertContains(
		AssetSummaryMetric assetSummaryMetric,
		List<AssetSummaryMetric> assetSummaryMetrics) {

		boolean contains = false;

		for (AssetSummaryMetric item : assetSummaryMetrics) {
			if (equals(assetSummaryMetric, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			assetSummaryMetrics + " does not contain " + assetSummaryMetric,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		AssetSummaryMetric assetSummaryMetric1,
		AssetSummaryMetric assetSummaryMetric2) {

		Assert.assertTrue(
			assetSummaryMetric1 + " does not equal " + assetSummaryMetric2,
			equals(assetSummaryMetric1, assetSummaryMetric2));
	}

	protected void assertEquals(
		List<AssetSummaryMetric> assetSummaryMetrics1,
		List<AssetSummaryMetric> assetSummaryMetrics2) {

		Assert.assertEquals(
			assetSummaryMetrics1.size(), assetSummaryMetrics2.size());

		for (int i = 0; i < assetSummaryMetrics1.size(); i++) {
			AssetSummaryMetric assetSummaryMetric1 = assetSummaryMetrics1.get(
				i);
			AssetSummaryMetric assetSummaryMetric2 = assetSummaryMetrics2.get(
				i);

			assertEquals(assetSummaryMetric1, assetSummaryMetric2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<AssetSummaryMetric> assetSummaryMetrics1,
		List<AssetSummaryMetric> assetSummaryMetrics2) {

		Assert.assertEquals(
			assetSummaryMetrics1.size(), assetSummaryMetrics2.size());

		for (AssetSummaryMetric assetSummaryMetric1 : assetSummaryMetrics1) {
			boolean contains = false;

			for (AssetSummaryMetric assetSummaryMetric2 :
					assetSummaryMetrics2) {

				if (equals(assetSummaryMetric1, assetSummaryMetric2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				assetSummaryMetrics2 + " does not contain " +
					assetSummaryMetric1,
				contains);
		}
	}

	protected void assertValid(AssetSummaryMetric assetSummaryMetric)
		throws Exception {

		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("assetId", additionalAssertFieldName)) {
				if (assetSummaryMetric.getAssetId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assetTitle", additionalAssertFieldName)) {
				if (assetSummaryMetric.getAssetTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assetType", additionalAssertFieldName)) {
				if (assetSummaryMetric.getAssetType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("downloads", additionalAssertFieldName)) {
				if (assetSummaryMetric.getDownloads() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"downloadsTrendPercentage", additionalAssertFieldName)) {

				if (assetSummaryMetric.getDownloadsTrendPercentage() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("impressions", additionalAssertFieldName)) {
				if (assetSummaryMetric.getImpressions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"impressionsTrendPercentage", additionalAssertFieldName)) {

				if (assetSummaryMetric.getImpressionsTrendPercentage() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("reads", additionalAssertFieldName)) {
				if (assetSummaryMetric.getReads() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"readsTrendPercentage", additionalAssertFieldName)) {

				if (assetSummaryMetric.getReadsTrendPercentage() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("views", additionalAssertFieldName)) {
				if (assetSummaryMetric.getViews() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"viewsTrendPercentage", additionalAssertFieldName)) {

				if (assetSummaryMetric.getViewsTrendPercentage() == null) {
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

	protected void assertValid(Page<AssetSummaryMetric> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<AssetSummaryMetric> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<AssetSummaryMetric> assetSummaryMetrics =
			page.getItems();

		int size = assetSummaryMetrics.size();

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
					com.liferay.osb.faro.rest.dto.v1_0.AssetSummaryMetric.
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
		AssetSummaryMetric assetSummaryMetric1,
		AssetSummaryMetric assetSummaryMetric2) {

		if (assetSummaryMetric1 == assetSummaryMetric2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("assetId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetSummaryMetric1.getAssetId(),
						assetSummaryMetric2.getAssetId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("assetTitle", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetSummaryMetric1.getAssetTitle(),
						assetSummaryMetric2.getAssetTitle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("assetType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetSummaryMetric1.getAssetType(),
						assetSummaryMetric2.getAssetType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("downloads", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetSummaryMetric1.getDownloads(),
						assetSummaryMetric2.getDownloads())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"downloadsTrendPercentage", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						assetSummaryMetric1.getDownloadsTrendPercentage(),
						assetSummaryMetric2.getDownloadsTrendPercentage())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("impressions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetSummaryMetric1.getImpressions(),
						assetSummaryMetric2.getImpressions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"impressionsTrendPercentage", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						assetSummaryMetric1.getImpressionsTrendPercentage(),
						assetSummaryMetric2.getImpressionsTrendPercentage())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("reads", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetSummaryMetric1.getReads(),
						assetSummaryMetric2.getReads())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"readsTrendPercentage", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						assetSummaryMetric1.getReadsTrendPercentage(),
						assetSummaryMetric2.getReadsTrendPercentage())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("views", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetSummaryMetric1.getViews(),
						assetSummaryMetric2.getViews())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"viewsTrendPercentage", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						assetSummaryMetric1.getViewsTrendPercentage(),
						assetSummaryMetric2.getViewsTrendPercentage())) {

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

		if (!(_assetSummaryMetricResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_assetSummaryMetricResource;

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
		AssetSummaryMetric assetSummaryMetric) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("assetId")) {
			Object object = assetSummaryMetric.getAssetId();

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

		if (entityFieldName.equals("assetTitle")) {
			Object object = assetSummaryMetric.getAssetTitle();

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

		if (entityFieldName.equals("assetType")) {
			Object object = assetSummaryMetric.getAssetType();

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

		if (entityFieldName.equals("downloads")) {
			sb.append(String.valueOf(assetSummaryMetric.getDownloads()));

			return sb.toString();
		}

		if (entityFieldName.equals("downloadsTrendPercentage")) {
			sb.append(
				String.valueOf(
					assetSummaryMetric.getDownloadsTrendPercentage()));

			return sb.toString();
		}

		if (entityFieldName.equals("impressions")) {
			sb.append(String.valueOf(assetSummaryMetric.getImpressions()));

			return sb.toString();
		}

		if (entityFieldName.equals("impressionsTrendPercentage")) {
			sb.append(
				String.valueOf(
					assetSummaryMetric.getImpressionsTrendPercentage()));

			return sb.toString();
		}

		if (entityFieldName.equals("reads")) {
			sb.append(String.valueOf(assetSummaryMetric.getReads()));

			return sb.toString();
		}

		if (entityFieldName.equals("readsTrendPercentage")) {
			sb.append(
				String.valueOf(assetSummaryMetric.getReadsTrendPercentage()));

			return sb.toString();
		}

		if (entityFieldName.equals("views")) {
			sb.append(String.valueOf(assetSummaryMetric.getViews()));

			return sb.toString();
		}

		if (entityFieldName.equals("viewsTrendPercentage")) {
			sb.append(
				String.valueOf(assetSummaryMetric.getViewsTrendPercentage()));

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

	protected AssetSummaryMetric randomAssetSummaryMetric() throws Exception {
		return new AssetSummaryMetric() {
			{
				assetId = StringUtil.toLowerCase(RandomTestUtil.randomString());
				assetTitle = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				assetType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				downloads = RandomTestUtil.randomDouble();
				downloadsTrendPercentage = RandomTestUtil.randomDouble();
				impressions = RandomTestUtil.randomDouble();
				impressionsTrendPercentage = RandomTestUtil.randomDouble();
				reads = RandomTestUtil.randomDouble();
				readsTrendPercentage = RandomTestUtil.randomDouble();
				views = RandomTestUtil.randomDouble();
				viewsTrendPercentage = RandomTestUtil.randomDouble();
			}
		};
	}

	protected AssetSummaryMetric randomIrrelevantAssetSummaryMetric()
		throws Exception {

		AssetSummaryMetric randomIrrelevantAssetSummaryMetric =
			randomAssetSummaryMetric();

		return randomIrrelevantAssetSummaryMetric;
	}

	protected AssetSummaryMetric randomPatchAssetSummaryMetric()
		throws Exception {

		return randomAssetSummaryMetric();
	}

	protected AssetSummaryMetricResource assetSummaryMetricResource;
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
		LogFactoryUtil.getLog(BaseAssetSummaryMetricResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.osb.faro.rest.resource.v1_0.AssetSummaryMetricResource
		_assetSummaryMetricResource;

}
// LIFERAY-REST-BUILDER-HASH:-1510061278