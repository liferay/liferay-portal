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

import com.liferay.osb.faro.rest.client.dto.v1_0.Individual;
import com.liferay.osb.faro.rest.client.http.HttpInvoker;
import com.liferay.osb.faro.rest.client.pagination.Page;
import com.liferay.osb.faro.rest.client.pagination.Pagination;
import com.liferay.osb.faro.rest.client.resource.v1_0.IndividualResource;
import com.liferay.osb.faro.rest.client.serdes.v1_0.IndividualSerDes;
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
public abstract class BaseIndividualResourceTestCase {

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

		_individualResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		individualResource = IndividualResource.builder(
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

		Individual individual1 = randomIndividual();

		String json = objectMapper.writeValueAsString(individual1);

		Individual individual2 = IndividualSerDes.toDTO(json);

		Assert.assertTrue(equals(individual1, individual2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Individual individual = randomIndividual();

		String json1 = objectMapper.writeValueAsString(individual);
		String json2 = IndividualSerDes.toJSON(individual);

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

		Individual individual = randomIndividual();

		individual.setAccountName(regex);
		individual.setId(regex);
		individual.setLastSessionCountry(regex);

		String json = IndividualSerDes.toJSON(individual);

		Assert.assertFalse(json.contains(regex));

		individual = IndividualSerDes.toDTO(json);

		Assert.assertEquals(regex, individual.getAccountName());
		Assert.assertEquals(regex, individual.getId());
		Assert.assertEquals(regex, individual.getLastSessionCountry());
	}

	@Test
	public void testGetWorkspaceGroupChannelIndividualsPage() throws Exception {
		Long groupId = testGetWorkspaceGroupChannelIndividualsPage_getGroupId();
		Long irrelevantGroupId =
			testGetWorkspaceGroupChannelIndividualsPage_getIrrelevantGroupId();
		String channelId =
			testGetWorkspaceGroupChannelIndividualsPage_getChannelId();
		String irrelevantChannelId =
			testGetWorkspaceGroupChannelIndividualsPage_getIrrelevantChannelId();

		Page<Individual> page =
			individualResource.getWorkspaceGroupChannelIndividualsPage(
				groupId, channelId, RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if ((irrelevantGroupId != null) && (irrelevantChannelId != null)) {
			Individual irrelevantIndividual =
				testGetWorkspaceGroupChannelIndividualsPage_addIndividual(
					irrelevantGroupId, irrelevantChannelId,
					randomIrrelevantIndividual());

			page = individualResource.getWorkspaceGroupChannelIndividualsPage(
				irrelevantGroupId, irrelevantChannelId, null, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantIndividual, (List<Individual>)page.getItems());
			assertValid(
				page,
				testGetWorkspaceGroupChannelIndividualsPage_getExpectedActions(
					irrelevantGroupId, irrelevantChannelId));
		}

		Individual individual1 =
			testGetWorkspaceGroupChannelIndividualsPage_addIndividual(
				groupId, channelId, randomIndividual());

		Individual individual2 =
			testGetWorkspaceGroupChannelIndividualsPage_addIndividual(
				groupId, channelId, randomIndividual());

		page = individualResource.getWorkspaceGroupChannelIndividualsPage(
			groupId, channelId, null, null, null, null, Pagination.of(1, 10),
			null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(individual1, (List<Individual>)page.getItems());
		assertContains(individual2, (List<Individual>)page.getItems());
		assertValid(
			page,
			testGetWorkspaceGroupChannelIndividualsPage_getExpectedActions(
				groupId, channelId));
	}

	protected Map<String, Map<String, String>>
			testGetWorkspaceGroupChannelIndividualsPage_getExpectedActions(
				Long groupId, String channelId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWorkspaceGroupChannelIndividualsPageWithPagination()
		throws Exception {

		Long groupId = testGetWorkspaceGroupChannelIndividualsPage_getGroupId();
		String channelId =
			testGetWorkspaceGroupChannelIndividualsPage_getChannelId();

		Page<Individual> individualsPage =
			individualResource.getWorkspaceGroupChannelIndividualsPage(
				groupId, channelId, null, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(individualsPage.getTotalCount());

		Individual individual1 =
			testGetWorkspaceGroupChannelIndividualsPage_addIndividual(
				groupId, channelId, randomIndividual());

		Individual individual2 =
			testGetWorkspaceGroupChannelIndividualsPage_addIndividual(
				groupId, channelId, randomIndividual());

		Individual individual3 =
			testGetWorkspaceGroupChannelIndividualsPage_addIndividual(
				groupId, channelId, randomIndividual());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Individual> page1 =
				individualResource.getWorkspaceGroupChannelIndividualsPage(
					groupId, channelId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(individual1, (List<Individual>)page1.getItems());

			Page<Individual> page2 =
				individualResource.getWorkspaceGroupChannelIndividualsPage(
					groupId, channelId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(individual2, (List<Individual>)page2.getItems());

			Page<Individual> page3 =
				individualResource.getWorkspaceGroupChannelIndividualsPage(
					groupId, channelId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(individual3, (List<Individual>)page3.getItems());
		}
		else {
			Page<Individual> page1 =
				individualResource.getWorkspaceGroupChannelIndividualsPage(
					groupId, channelId, null, null, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<Individual> individuals1 = (List<Individual>)page1.getItems();

			Assert.assertEquals(
				individuals1.toString(), totalCount + 2, individuals1.size());

			Page<Individual> page2 =
				individualResource.getWorkspaceGroupChannelIndividualsPage(
					groupId, channelId, null, null, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Individual> individuals2 = (List<Individual>)page2.getItems();

			Assert.assertEquals(
				individuals2.toString(), 1, individuals2.size());

			Page<Individual> page3 =
				individualResource.getWorkspaceGroupChannelIndividualsPage(
					groupId, channelId, null, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(individual1, (List<Individual>)page3.getItems());
			assertContains(individual2, (List<Individual>)page3.getItems());
			assertContains(individual3, (List<Individual>)page3.getItems());
		}
	}

	@Test
	public void testGetWorkspaceGroupChannelIndividualsPageWithSortDateTime()
		throws Exception {

		testGetWorkspaceGroupChannelIndividualsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, individual1, individual2) -> {
				BeanTestUtil.setProperty(
					individual1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetWorkspaceGroupChannelIndividualsPageWithSortDouble()
		throws Exception {

		testGetWorkspaceGroupChannelIndividualsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, individual1, individual2) -> {
				BeanTestUtil.setProperty(
					individual1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					individual2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetWorkspaceGroupChannelIndividualsPageWithSortInteger()
		throws Exception {

		testGetWorkspaceGroupChannelIndividualsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, individual1, individual2) -> {
				BeanTestUtil.setProperty(individual1, entityField.getName(), 0);
				BeanTestUtil.setProperty(individual2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetWorkspaceGroupChannelIndividualsPageWithSortString()
		throws Exception {

		testGetWorkspaceGroupChannelIndividualsPageWithSort(
			EntityField.Type.STRING,
			(entityField, individual1, individual2) -> {
				Class<?> clazz = individual1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						individual1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						individual2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						individual1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						individual2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						individual1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						individual2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetWorkspaceGroupChannelIndividualsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Individual, Individual, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long groupId = testGetWorkspaceGroupChannelIndividualsPage_getGroupId();
		String channelId =
			testGetWorkspaceGroupChannelIndividualsPage_getChannelId();

		Individual individual1 = randomIndividual();
		Individual individual2 = randomIndividual();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, individual1, individual2);
		}

		individual1 = testGetWorkspaceGroupChannelIndividualsPage_addIndividual(
			groupId, channelId, individual1);

		individual2 = testGetWorkspaceGroupChannelIndividualsPage_addIndividual(
			groupId, channelId, individual2);

		Page<Individual> page =
			individualResource.getWorkspaceGroupChannelIndividualsPage(
				groupId, channelId, null, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Individual> ascPage =
				individualResource.getWorkspaceGroupChannelIndividualsPage(
					groupId, channelId, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(individual1, (List<Individual>)ascPage.getItems());
			assertContains(individual2, (List<Individual>)ascPage.getItems());

			Page<Individual> descPage =
				individualResource.getWorkspaceGroupChannelIndividualsPage(
					groupId, channelId, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(individual2, (List<Individual>)descPage.getItems());
			assertContains(individual1, (List<Individual>)descPage.getItems());
		}
	}

	protected Individual
			testGetWorkspaceGroupChannelIndividualsPage_addIndividual(
				Long groupId, String channelId, Individual individual)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetWorkspaceGroupChannelIndividualsPage_getGroupId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkspaceGroupChannelIndividualsPage_getIrrelevantGroupId()
		throws Exception {

		return null;
	}

	protected String testGetWorkspaceGroupChannelIndividualsPage_getChannelId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetWorkspaceGroupChannelIndividualsPage_getIrrelevantChannelId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetWorkspaceGroupIndividual() throws Exception {
		Individual postIndividual =
			testGetWorkspaceGroupIndividual_addIndividual();

		Individual getIndividual =
			individualResource.getWorkspaceGroupIndividual(
				testGetWorkspaceGroupIndividual_getGroupId(),
				postIndividual.getId(), null);

		assertEquals(postIndividual, getIndividual);
		assertValid(getIndividual);
	}

	protected Individual testGetWorkspaceGroupIndividual_addIndividual()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetWorkspaceGroupIndividual_getGroupId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetWorkspaceGroupIndividual() throws Exception {
		Individual individual =
			testGraphQLGetWorkspaceGroupIndividual_addIndividual();

		// No namespace

		Assert.assertTrue(
			equals(
				individual,
				IndividualSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"workspaceGroupIndividual",
								new HashMap<String, Object>() {
									{
										put(
											"groupId",
											testGraphQLGetWorkspaceGroupIndividual_getGroupId());
										put(
											"individualId",
											"\"" + individual.getId() + "\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/workspaceGroupIndividual"))));

		// Using the namespace faro_v1_0

		Assert.assertTrue(
			equals(
				individual,
				IndividualSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"faro_v1_0",
								new GraphQLField(
									"workspaceGroupIndividual",
									new HashMap<String, Object>() {
										{
											put(
												"groupId",
												testGraphQLGetWorkspaceGroupIndividual_getGroupId());
											put(
												"individualId",
												"\"" + individual.getId() +
													"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/faro_v1_0",
						"Object/workspaceGroupIndividual"))));
	}

	protected Long testGraphQLGetWorkspaceGroupIndividual_getGroupId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetWorkspaceGroupIndividualNotFound()
		throws Exception {

		Long irrelevantGroupId = RandomTestUtil.randomLong();
		String irrelevantIndividualId =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"workspaceGroupIndividual",
						new HashMap<String, Object>() {
							{
								put("groupId", irrelevantGroupId);
								put("individualId", irrelevantIndividualId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace faro_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"faro_v1_0",
						new GraphQLField(
							"workspaceGroupIndividual",
							new HashMap<String, Object>() {
								{
									put("groupId", irrelevantGroupId);
									put("individualId", irrelevantIndividualId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Individual testGraphQLGetWorkspaceGroupIndividual_addIndividual()
		throws Exception {

		return testGraphQLIndividual_addIndividual();
	}

	protected Individual testGraphQLIndividual_addIndividual()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		Individual individual, List<Individual> individuals) {

		boolean contains = false;

		for (Individual item : individuals) {
			if (equals(individual, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			individuals + " does not contain " + individual, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		Individual individual1, Individual individual2) {

		Assert.assertTrue(
			individual1 + " does not equal " + individual2,
			equals(individual1, individual2));
	}

	protected void assertEquals(
		List<Individual> individuals1, List<Individual> individuals2) {

		Assert.assertEquals(individuals1.size(), individuals2.size());

		for (int i = 0; i < individuals1.size(); i++) {
			Individual individual1 = individuals1.get(i);
			Individual individual2 = individuals2.get(i);

			assertEquals(individual1, individual2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Individual> individuals1, List<Individual> individuals2) {

		Assert.assertEquals(individuals1.size(), individuals2.size());

		for (Individual individual1 : individuals1) {
			boolean contains = false;

			for (Individual individual2 : individuals2) {
				if (equals(individual1, individual2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				individuals2 + " does not contain " + individual1, contains);
		}
	}

	protected void assertValid(Individual individual) throws Exception {
		boolean valid = true;

		if (individual.getDateCreated() == null) {
			valid = false;
		}

		if (individual.getDateModified() == null) {
			valid = false;
		}

		if (individual.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountName", additionalAssertFieldName)) {
				if (individual.getAccountName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("activitiesCount", additionalAssertFieldName)) {
				if (individual.getActivitiesCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("averageSessionDuration", additionalAssertFieldName)) {
				continue;
			}

			if (Objects.equals("demographics", additionalAssertFieldName)) {
				if (individual.getDemographics() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"firstActivityDate", additionalAssertFieldName)) {

				if (individual.getFirstActivityDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("lastActivityDate", additionalAssertFieldName)) {
				if (individual.getLastActivityDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"lastSessionCountry", additionalAssertFieldName)) {

				if (individual.getLastSessionCountry() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("profileType", additionalAssertFieldName)) {
				if (individual.getProfileType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("sessionsCount", additionalAssertFieldName)) {
				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		Assert.assertTrue(valid);
	}

	protected void assertValid(Page<Individual> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Individual> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Individual> individuals = page.getItems();

		int size = individuals.size();

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
					com.liferay.osb.faro.rest.dto.v1_0.Individual.class)) {

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

	protected boolean equals(Individual individual1, Individual individual2) {
		if (individual1 == individual2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individual1.getAccountName(),
						individual2.getAccountName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("activitiesCount", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individual1.getActivitiesCount(),
						individual2.getActivitiesCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("averageSessionDuration", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individual1.getAverageSessionDuration(),
						individual2.getAverageSessionDuration())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individual1.getDateCreated(),
						individual2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individual1.getDateModified(),
						individual2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("demographics", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individual1.getDemographics(),
						individual2.getDemographics())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"firstActivityDate", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						individual1.getFirstActivityDate(),
						individual2.getFirstActivityDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individual1.getId(), individual2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("lastActivityDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individual1.getLastActivityDate(),
						individual2.getLastActivityDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"lastSessionCountry", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						individual1.getLastSessionCountry(),
						individual2.getLastSessionCountry())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("profileType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individual1.getProfileType(),
						individual2.getProfileType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("sessionsCount", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individual1.getSessionsCount(),
						individual2.getSessionsCount())) {

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

		if (!(_individualResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_individualResource;

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
		EntityField entityField, String operator, Individual individual) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("accountName")) {
			Object object = individual.getAccountName();

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

		if (entityFieldName.equals("activitiesCount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("averageSessionDuration")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = individual.getDateCreated();

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

				sb.append(_format.format(individual.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = individual.getDateModified();

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

				sb.append(_format.format(individual.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("demographics")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("firstActivityDate")) {
			if (operator.equals("between")) {
				Date date = individual.getFirstActivityDate();

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

				sb.append(_format.format(individual.getFirstActivityDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("id")) {
			Object object = individual.getId();

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

		if (entityFieldName.equals("lastActivityDate")) {
			if (operator.equals("between")) {
				Date date = individual.getLastActivityDate();

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

				sb.append(_format.format(individual.getLastActivityDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("lastSessionCountry")) {
			Object object = individual.getLastSessionCountry();

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

		if (entityFieldName.equals("profileType")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("sessionsCount")) {
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

	protected Individual randomIndividual() throws Exception {
		return new Individual() {
			{
				accountName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				activitiesCount = RandomTestUtil.randomLong();
				averageSessionDuration = RandomTestUtil.randomLong();
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				firstActivityDate = RandomTestUtil.nextDate();
				id = StringUtil.toLowerCase(RandomTestUtil.randomString());
				lastActivityDate = RandomTestUtil.nextDate();
				lastSessionCountry = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				sessionsCount = RandomTestUtil.randomLong();
			}
		};
	}

	protected Individual randomIrrelevantIndividual() throws Exception {
		Individual randomIrrelevantIndividual = randomIndividual();

		return randomIrrelevantIndividual;
	}

	protected Individual randomPatchIndividual() throws Exception {
		return randomIndividual();
	}

	protected IndividualResource individualResource;
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
		LogFactoryUtil.getLog(BaseIndividualResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.osb.faro.rest.resource.v1_0.IndividualResource
		_individualResource;

}
// LIFERAY-REST-BUILDER-HASH:89174091