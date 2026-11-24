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

import com.liferay.osb.faro.rest.client.dto.v1_0.Campaign;
import com.liferay.osb.faro.rest.client.http.HttpInvoker;
import com.liferay.osb.faro.rest.client.pagination.Page;
import com.liferay.osb.faro.rest.client.pagination.Pagination;
import com.liferay.osb.faro.rest.client.resource.v1_0.CampaignResource;
import com.liferay.osb.faro.rest.client.serdes.v1_0.CampaignSerDes;
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
public abstract class BaseCampaignResourceTestCase {

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

		_campaignResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		campaignResource = CampaignResource.builder(
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

		Campaign campaign1 = randomCampaign();

		String json = objectMapper.writeValueAsString(campaign1);

		Campaign campaign2 = CampaignSerDes.toDTO(json);

		Assert.assertTrue(equals(campaign1, campaign2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Campaign campaign = randomCampaign();

		String json1 = objectMapper.writeValueAsString(campaign);
		String json2 = CampaignSerDes.toJSON(campaign);

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

		Campaign campaign = randomCampaign();

		campaign.setCampaignType(regex);
		campaign.setId(regex);
		campaign.setName(regex);
		campaign.setOrigin(regex);
		campaign.setStatus(regex);

		String json = CampaignSerDes.toJSON(campaign);

		Assert.assertFalse(json.contains(regex));

		campaign = CampaignSerDes.toDTO(json);

		Assert.assertEquals(regex, campaign.getCampaignType());
		Assert.assertEquals(regex, campaign.getId());
		Assert.assertEquals(regex, campaign.getName());
		Assert.assertEquals(regex, campaign.getOrigin());
		Assert.assertEquals(regex, campaign.getStatus());
	}

	@Test
	public void testGetWorkspaceGroupChannelCampaign() throws Exception {
		Campaign postCampaign =
			testGetWorkspaceGroupChannelCampaign_addCampaign();

		Campaign getCampaign =
			campaignResource.getWorkspaceGroupChannelCampaign(
				testGetWorkspaceGroupChannelCampaign_getGroupId(),
				testGetWorkspaceGroupChannelCampaign_getChannelId(),
				postCampaign.getId());

		assertEquals(postCampaign, getCampaign);
		assertValid(getCampaign);
	}

	protected Campaign testGetWorkspaceGroupChannelCampaign_addCampaign()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetWorkspaceGroupChannelCampaign_getGroupId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String testGetWorkspaceGroupChannelCampaign_getChannelId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetWorkspaceGroupChannelCampaignsPage() throws Exception {
		Long groupId = testGetWorkspaceGroupChannelCampaignsPage_getGroupId();
		Long irrelevantGroupId =
			testGetWorkspaceGroupChannelCampaignsPage_getIrrelevantGroupId();
		String channelId =
			testGetWorkspaceGroupChannelCampaignsPage_getChannelId();
		String irrelevantChannelId =
			testGetWorkspaceGroupChannelCampaignsPage_getIrrelevantChannelId();

		Page<Campaign> page =
			campaignResource.getWorkspaceGroupChannelCampaignsPage(
				groupId, channelId, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if ((irrelevantGroupId != null) && (irrelevantChannelId != null)) {
			Campaign irrelevantCampaign =
				testGetWorkspaceGroupChannelCampaignsPage_addCampaign(
					irrelevantGroupId, irrelevantChannelId,
					randomIrrelevantCampaign());

			page = campaignResource.getWorkspaceGroupChannelCampaignsPage(
				irrelevantGroupId, irrelevantChannelId, null, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantCampaign, (List<Campaign>)page.getItems());
			assertValid(
				page,
				testGetWorkspaceGroupChannelCampaignsPage_getExpectedActions(
					irrelevantGroupId, irrelevantChannelId));
		}

		Campaign campaign1 =
			testGetWorkspaceGroupChannelCampaignsPage_addCampaign(
				groupId, channelId, randomCampaign());

		Campaign campaign2 =
			testGetWorkspaceGroupChannelCampaignsPage_addCampaign(
				groupId, channelId, randomCampaign());

		page = campaignResource.getWorkspaceGroupChannelCampaignsPage(
			groupId, channelId, null, null, null, null,
			Pagination.of(1, (int)totalCount + 2), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(campaign1, (List<Campaign>)page.getItems());
		assertContains(campaign2, (List<Campaign>)page.getItems());
		assertValid(
			page,
			testGetWorkspaceGroupChannelCampaignsPage_getExpectedActions(
				groupId, channelId));
	}

	protected Map<String, Map<String, String>>
			testGetWorkspaceGroupChannelCampaignsPage_getExpectedActions(
				Long groupId, String channelId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWorkspaceGroupChannelCampaignsPageWithPagination()
		throws Exception {

		Long groupId = testGetWorkspaceGroupChannelCampaignsPage_getGroupId();
		String channelId =
			testGetWorkspaceGroupChannelCampaignsPage_getChannelId();

		Page<Campaign> campaignsPage =
			campaignResource.getWorkspaceGroupChannelCampaignsPage(
				groupId, channelId, null, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(campaignsPage.getTotalCount());

		Campaign campaign1 =
			testGetWorkspaceGroupChannelCampaignsPage_addCampaign(
				groupId, channelId, randomCampaign());

		Campaign campaign2 =
			testGetWorkspaceGroupChannelCampaignsPage_addCampaign(
				groupId, channelId, randomCampaign());

		Campaign campaign3 =
			testGetWorkspaceGroupChannelCampaignsPage_addCampaign(
				groupId, channelId, randomCampaign());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Campaign> page1 =
				campaignResource.getWorkspaceGroupChannelCampaignsPage(
					groupId, channelId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(campaign1, (List<Campaign>)page1.getItems());

			Page<Campaign> page2 =
				campaignResource.getWorkspaceGroupChannelCampaignsPage(
					groupId, channelId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(campaign2, (List<Campaign>)page2.getItems());

			Page<Campaign> page3 =
				campaignResource.getWorkspaceGroupChannelCampaignsPage(
					groupId, channelId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(campaign3, (List<Campaign>)page3.getItems());
		}
		else {
			Page<Campaign> page1 =
				campaignResource.getWorkspaceGroupChannelCampaignsPage(
					groupId, channelId, null, null, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<Campaign> campaigns1 = (List<Campaign>)page1.getItems();

			Assert.assertEquals(
				campaigns1.toString(), totalCount + 2, campaigns1.size());

			Page<Campaign> page2 =
				campaignResource.getWorkspaceGroupChannelCampaignsPage(
					groupId, channelId, null, null, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Campaign> campaigns2 = (List<Campaign>)page2.getItems();

			Assert.assertEquals(campaigns2.toString(), 1, campaigns2.size());

			Page<Campaign> page3 =
				campaignResource.getWorkspaceGroupChannelCampaignsPage(
					groupId, channelId, null, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(campaign1, (List<Campaign>)page3.getItems());
			assertContains(campaign2, (List<Campaign>)page3.getItems());
			assertContains(campaign3, (List<Campaign>)page3.getItems());
		}
	}

	@Test
	public void testGetWorkspaceGroupChannelCampaignsPageWithSortDateTime()
		throws Exception {

		testGetWorkspaceGroupChannelCampaignsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, campaign1, campaign2) -> {
				BeanTestUtil.setProperty(
					campaign1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetWorkspaceGroupChannelCampaignsPageWithSortDouble()
		throws Exception {

		testGetWorkspaceGroupChannelCampaignsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, campaign1, campaign2) -> {
				BeanTestUtil.setProperty(campaign1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(campaign2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetWorkspaceGroupChannelCampaignsPageWithSortInteger()
		throws Exception {

		testGetWorkspaceGroupChannelCampaignsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, campaign1, campaign2) -> {
				BeanTestUtil.setProperty(campaign1, entityField.getName(), 0);
				BeanTestUtil.setProperty(campaign2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetWorkspaceGroupChannelCampaignsPageWithSortString()
		throws Exception {

		testGetWorkspaceGroupChannelCampaignsPageWithSort(
			EntityField.Type.STRING,
			(entityField, campaign1, campaign2) -> {
				Class<?> clazz = campaign1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						campaign1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						campaign2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						campaign1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						campaign2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						campaign1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						campaign2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetWorkspaceGroupChannelCampaignsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Campaign, Campaign, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long groupId = testGetWorkspaceGroupChannelCampaignsPage_getGroupId();
		String channelId =
			testGetWorkspaceGroupChannelCampaignsPage_getChannelId();

		Campaign campaign1 = randomCampaign();
		Campaign campaign2 = randomCampaign();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, campaign1, campaign2);
		}

		campaign1 = testGetWorkspaceGroupChannelCampaignsPage_addCampaign(
			groupId, channelId, campaign1);

		campaign2 = testGetWorkspaceGroupChannelCampaignsPage_addCampaign(
			groupId, channelId, campaign2);

		Page<Campaign> page =
			campaignResource.getWorkspaceGroupChannelCampaignsPage(
				groupId, channelId, null, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Campaign> ascPage =
				campaignResource.getWorkspaceGroupChannelCampaignsPage(
					groupId, channelId, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(campaign1, (List<Campaign>)ascPage.getItems());
			assertContains(campaign2, (List<Campaign>)ascPage.getItems());

			Page<Campaign> descPage =
				campaignResource.getWorkspaceGroupChannelCampaignsPage(
					groupId, channelId, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(campaign2, (List<Campaign>)descPage.getItems());
			assertContains(campaign1, (List<Campaign>)descPage.getItems());
		}
	}

	protected Campaign testGetWorkspaceGroupChannelCampaignsPage_addCampaign(
			Long groupId, String channelId, Campaign campaign)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetWorkspaceGroupChannelCampaignsPage_getGroupId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkspaceGroupChannelCampaignsPage_getIrrelevantGroupId()
		throws Exception {

		return null;
	}

	protected String testGetWorkspaceGroupChannelCampaignsPage_getChannelId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetWorkspaceGroupChannelCampaignsPage_getIrrelevantChannelId()
		throws Exception {

		return null;
	}

	protected void assertContains(Campaign campaign, List<Campaign> campaigns) {
		boolean contains = false;

		for (Campaign item : campaigns) {
			if (equals(campaign, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			campaigns + " does not contain " + campaign, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Campaign campaign1, Campaign campaign2) {
		Assert.assertTrue(
			campaign1 + " does not equal " + campaign2,
			equals(campaign1, campaign2));
	}

	protected void assertEquals(
		List<Campaign> campaigns1, List<Campaign> campaigns2) {

		Assert.assertEquals(campaigns1.size(), campaigns2.size());

		for (int i = 0; i < campaigns1.size(); i++) {
			Campaign campaign1 = campaigns1.get(i);
			Campaign campaign2 = campaigns2.get(i);

			assertEquals(campaign1, campaign2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Campaign> campaigns1, List<Campaign> campaigns2) {

		Assert.assertEquals(campaigns1.size(), campaigns2.size());

		for (Campaign campaign1 : campaigns1) {
			boolean contains = false;

			for (Campaign campaign2 : campaigns2) {
				if (equals(campaign1, campaign2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				campaigns2 + " does not contain " + campaign1, contains);
		}
	}

	protected void assertValid(Campaign campaign) throws Exception {
		boolean valid = true;

		if (campaign.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountsTouched", additionalAssertFieldName)) {
				if (campaign.getAccountsTouched() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("campaignType", additionalAssertFieldName)) {
				if (campaign.getCampaignType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("endDate", additionalAssertFieldName)) {
				if (campaign.getEndDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"individualsTouched", additionalAssertFieldName)) {

				if (campaign.getIndividualsTouched() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("lastTouchDate", additionalAssertFieldName)) {
				if (campaign.getLastTouchDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (campaign.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("origin", additionalAssertFieldName)) {
				if (campaign.getOrigin() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("startDate", additionalAssertFieldName)) {
				if (campaign.getStartDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (campaign.getStatus() == null) {
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

	protected void assertValid(Page<Campaign> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Campaign> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Campaign> campaigns = page.getItems();

		int size = campaigns.size();

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
					com.liferay.osb.faro.rest.dto.v1_0.Campaign.class)) {

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

	protected boolean equals(Campaign campaign1, Campaign campaign2) {
		if (campaign1 == campaign2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountsTouched", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						campaign1.getAccountsTouched(),
						campaign2.getAccountsTouched())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("campaignType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						campaign1.getCampaignType(),
						campaign2.getCampaignType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("endDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						campaign1.getEndDate(), campaign2.getEndDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(campaign1.getId(), campaign2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals(
					"individualsTouched", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						campaign1.getIndividualsTouched(),
						campaign2.getIndividualsTouched())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("lastTouchDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						campaign1.getLastTouchDate(),
						campaign2.getLastTouchDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						campaign1.getName(), campaign2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("origin", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						campaign1.getOrigin(), campaign2.getOrigin())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("startDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						campaign1.getStartDate(), campaign2.getStartDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						campaign1.getStatus(), campaign2.getStatus())) {

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

		if (!(_campaignResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_campaignResource;

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
		EntityField entityField, String operator, Campaign campaign) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("accountsTouched")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("campaignType")) {
			Object object = campaign.getCampaignType();

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

		if (entityFieldName.equals("endDate")) {
			if (operator.equals("between")) {
				Date date = campaign.getEndDate();

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

				sb.append(_format.format(campaign.getEndDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("id")) {
			Object object = campaign.getId();

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

		if (entityFieldName.equals("individualsTouched")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("lastTouchDate")) {
			if (operator.equals("between")) {
				Date date = campaign.getLastTouchDate();

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

				sb.append(_format.format(campaign.getLastTouchDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("name")) {
			Object object = campaign.getName();

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

		if (entityFieldName.equals("origin")) {
			Object object = campaign.getOrigin();

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

		if (entityFieldName.equals("startDate")) {
			if (operator.equals("between")) {
				Date date = campaign.getStartDate();

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

				sb.append(_format.format(campaign.getStartDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("status")) {
			Object object = campaign.getStatus();

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

	protected Campaign randomCampaign() throws Exception {
		return new Campaign() {
			{
				accountsTouched = RandomTestUtil.randomLong();
				campaignType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				endDate = RandomTestUtil.nextDate();
				id = StringUtil.toLowerCase(RandomTestUtil.randomString());
				individualsTouched = RandomTestUtil.randomLong();
				lastTouchDate = RandomTestUtil.nextDate();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				origin = StringUtil.toLowerCase(RandomTestUtil.randomString());
				startDate = RandomTestUtil.nextDate();
				status = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected Campaign randomIrrelevantCampaign() throws Exception {
		Campaign randomIrrelevantCampaign = randomCampaign();

		return randomIrrelevantCampaign;
	}

	protected Campaign randomPatchCampaign() throws Exception {
		return randomCampaign();
	}

	protected CampaignResource campaignResource;
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
		LogFactoryUtil.getLog(BaseCampaignResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.osb.faro.rest.resource.v1_0.CampaignResource
		_campaignResource;

}
// LIFERAY-REST-BUILDER-HASH:-1964130161