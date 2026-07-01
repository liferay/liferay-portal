/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.ai.hub.rest.client.dto.v1_0.AgentDefinition;
import com.liferay.ai.hub.rest.client.http.HttpInvoker;
import com.liferay.ai.hub.rest.client.pagination.Page;
import com.liferay.ai.hub.rest.client.pagination.Pagination;
import com.liferay.ai.hub.rest.client.resource.v1_0.AgentDefinitionResource;
import com.liferay.ai.hub.rest.client.serdes.v1_0.AgentDefinitionSerDes;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
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
 * @author Feliphe Marinho
 * @generated
 */
@Generated("")
public abstract class BaseAgentDefinitionResourceTestCase {

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

		_agentDefinitionResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		agentDefinitionResource = AgentDefinitionResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		importTaskResource = ImportTaskResource.builder(
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

		AgentDefinition agentDefinition1 = randomAgentDefinition();

		String json = objectMapper.writeValueAsString(agentDefinition1);

		AgentDefinition agentDefinition2 = AgentDefinitionSerDes.toDTO(json);

		Assert.assertTrue(equals(agentDefinition1, agentDefinition2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		AgentDefinition agentDefinition = randomAgentDefinition();

		String json1 = objectMapper.writeValueAsString(agentDefinition);
		String json2 = AgentDefinitionSerDes.toJSON(agentDefinition);

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

		AgentDefinition agentDefinition = randomAgentDefinition();

		agentDefinition.setDescription(regex);
		agentDefinition.setExternalReferenceCode(regex);
		agentDefinition.setTitle(regex);
		agentDefinition.setWorkflowDefinitionName(regex);

		String json = AgentDefinitionSerDes.toJSON(agentDefinition);

		Assert.assertFalse(json.contains(regex));

		agentDefinition = AgentDefinitionSerDes.toDTO(json);

		Assert.assertEquals(regex, agentDefinition.getDescription());
		Assert.assertEquals(regex, agentDefinition.getExternalReferenceCode());
		Assert.assertEquals(regex, agentDefinition.getTitle());
		Assert.assertEquals(regex, agentDefinition.getWorkflowDefinitionName());
	}

	@Test
	public void testDeleteAgentDefinitionByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AgentDefinition agentDefinition =
			testDeleteAgentDefinitionByExternalReferenceCode_addAgentDefinition();

		assertHttpResponseStatusCode(
			204,
			agentDefinitionResource.
				deleteAgentDefinitionByExternalReferenceCodeHttpResponse(
					agentDefinition.getExternalReferenceCode()));
	}

	protected AgentDefinition
			testDeleteAgentDefinitionByExternalReferenceCode_addAgentDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetAgentDefinitionsPage() throws Exception {
		Page<AgentDefinition> page =
			agentDefinitionResource.getAgentDefinitionsPage(
				null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		AgentDefinition agentDefinition1 =
			testGetAgentDefinitionsPage_addAgentDefinition(
				randomAgentDefinition());

		AgentDefinition agentDefinition2 =
			testGetAgentDefinitionsPage_addAgentDefinition(
				randomAgentDefinition());

		page = agentDefinitionResource.getAgentDefinitionsPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			agentDefinition1, (List<AgentDefinition>)page.getItems());
		assertContains(
			agentDefinition2, (List<AgentDefinition>)page.getItems());
		assertValid(page, testGetAgentDefinitionsPage_getExpectedActions());
	}

	protected Map<String, Map<String, String>>
			testGetAgentDefinitionsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAgentDefinitionsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		AgentDefinition agentDefinition1 = randomAgentDefinition();

		agentDefinition1 = testGetAgentDefinitionsPage_addAgentDefinition(
			agentDefinition1);

		for (EntityField entityField : entityFields) {
			Page<AgentDefinition> page =
				agentDefinitionResource.getAgentDefinitionsPage(
					null,
					getFilterString(entityField, "between", agentDefinition1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(agentDefinition1),
				(List<AgentDefinition>)page.getItems());
		}
	}

	@Test
	public void testGetAgentDefinitionsPageWithFilterDoubleEquals()
		throws Exception {

		testGetAgentDefinitionsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetAgentDefinitionsPageWithFilterStringContains()
		throws Exception {

		testGetAgentDefinitionsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetAgentDefinitionsPageWithFilterStringEquals()
		throws Exception {

		testGetAgentDefinitionsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetAgentDefinitionsPageWithFilterStringStartsWith()
		throws Exception {

		testGetAgentDefinitionsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetAgentDefinitionsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		AgentDefinition agentDefinition1 =
			testGetAgentDefinitionsPage_addAgentDefinition(
				randomAgentDefinition());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AgentDefinition agentDefinition2 =
			testGetAgentDefinitionsPage_addAgentDefinition(
				randomAgentDefinition());

		for (EntityField entityField : entityFields) {
			Page<AgentDefinition> page =
				agentDefinitionResource.getAgentDefinitionsPage(
					null,
					getFilterString(entityField, operator, agentDefinition1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(agentDefinition1),
				(List<AgentDefinition>)page.getItems());
		}
	}

	@Test
	public void testGetAgentDefinitionsPageWithPagination() throws Exception {
		Page<AgentDefinition> agentDefinitionsPage =
			agentDefinitionResource.getAgentDefinitionsPage(
				null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			agentDefinitionsPage.getTotalCount());

		AgentDefinition agentDefinition1 =
			testGetAgentDefinitionsPage_addAgentDefinition(
				randomAgentDefinition());

		AgentDefinition agentDefinition2 =
			testGetAgentDefinitionsPage_addAgentDefinition(
				randomAgentDefinition());

		AgentDefinition agentDefinition3 =
			testGetAgentDefinitionsPage_addAgentDefinition(
				randomAgentDefinition());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<AgentDefinition> page1 =
				agentDefinitionResource.getAgentDefinitionsPage(
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				agentDefinition1, (List<AgentDefinition>)page1.getItems());

			Page<AgentDefinition> page2 =
				agentDefinitionResource.getAgentDefinitionsPage(
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				agentDefinition2, (List<AgentDefinition>)page2.getItems());

			Page<AgentDefinition> page3 =
				agentDefinitionResource.getAgentDefinitionsPage(
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				agentDefinition3, (List<AgentDefinition>)page3.getItems());
		}
		else {
			Page<AgentDefinition> page1 =
				agentDefinitionResource.getAgentDefinitionsPage(
					null, null, Pagination.of(1, totalCount + 2), null);

			List<AgentDefinition> agentDefinitions1 =
				(List<AgentDefinition>)page1.getItems();

			Assert.assertEquals(
				agentDefinitions1.toString(), totalCount + 2,
				agentDefinitions1.size());

			Page<AgentDefinition> page2 =
				agentDefinitionResource.getAgentDefinitionsPage(
					null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<AgentDefinition> agentDefinitions2 =
				(List<AgentDefinition>)page2.getItems();

			Assert.assertEquals(
				agentDefinitions2.toString(), 1, agentDefinitions2.size());

			Page<AgentDefinition> page3 =
				agentDefinitionResource.getAgentDefinitionsPage(
					null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				agentDefinition1, (List<AgentDefinition>)page3.getItems());
			assertContains(
				agentDefinition2, (List<AgentDefinition>)page3.getItems());
			assertContains(
				agentDefinition3, (List<AgentDefinition>)page3.getItems());
		}
	}

	@Test
	public void testGetAgentDefinitionsPageWithSortDateTime() throws Exception {
		testGetAgentDefinitionsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, agentDefinition1, agentDefinition2) -> {
				BeanTestUtil.setProperty(
					agentDefinition1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAgentDefinitionsPageWithSortDouble() throws Exception {
		testGetAgentDefinitionsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, agentDefinition1, agentDefinition2) -> {
				BeanTestUtil.setProperty(
					agentDefinition1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					agentDefinition2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAgentDefinitionsPageWithSortInteger() throws Exception {
		testGetAgentDefinitionsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, agentDefinition1, agentDefinition2) -> {
				BeanTestUtil.setProperty(
					agentDefinition1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					agentDefinition2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAgentDefinitionsPageWithSortString() throws Exception {
		testGetAgentDefinitionsPageWithSort(
			EntityField.Type.STRING,
			(entityField, agentDefinition1, agentDefinition2) -> {
				Class<?> clazz = agentDefinition1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						agentDefinition1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						agentDefinition2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						agentDefinition1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						agentDefinition2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						agentDefinition1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						agentDefinition2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetAgentDefinitionsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, AgentDefinition, AgentDefinition, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		AgentDefinition agentDefinition1 = randomAgentDefinition();
		AgentDefinition agentDefinition2 = randomAgentDefinition();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, agentDefinition1, agentDefinition2);
		}

		agentDefinition1 = testGetAgentDefinitionsPage_addAgentDefinition(
			agentDefinition1);

		agentDefinition2 = testGetAgentDefinitionsPage_addAgentDefinition(
			agentDefinition2);

		Page<AgentDefinition> page =
			agentDefinitionResource.getAgentDefinitionsPage(
				null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<AgentDefinition> ascPage =
				agentDefinitionResource.getAgentDefinitionsPage(
					null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				agentDefinition1, (List<AgentDefinition>)ascPage.getItems());
			assertContains(
				agentDefinition2, (List<AgentDefinition>)ascPage.getItems());

			Page<AgentDefinition> descPage =
				agentDefinitionResource.getAgentDefinitionsPage(
					null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				agentDefinition2, (List<AgentDefinition>)descPage.getItems());
			assertContains(
				agentDefinition1, (List<AgentDefinition>)descPage.getItems());
		}
	}

	protected AgentDefinition testGetAgentDefinitionsPage_addAgentDefinition(
			AgentDefinition agentDefinition)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchAgentDefinitionByExternalReferenceCodeUpdateActive()
		throws Exception {

		Assert.assertTrue(false);
	}

	@Test
	public void testPostAgentDefinitionByExternalReferenceCodeCopy()
		throws Exception {

		AgentDefinition randomAgentDefinition = randomAgentDefinition();

		AgentDefinition postAgentDefinition =
			testPostAgentDefinitionByExternalReferenceCodeCopy_addAgentDefinition(
				randomAgentDefinition);

		assertEquals(randomAgentDefinition, postAgentDefinition);
		assertValid(postAgentDefinition);
	}

	protected AgentDefinition
			testPostAgentDefinitionByExternalReferenceCodeCopy_addAgentDefinition(
				AgentDefinition agentDefinition)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostAgentDefinitionDraft() throws Exception {
		AgentDefinition randomAgentDefinition = randomAgentDefinition();

		AgentDefinition postAgentDefinition =
			testPostAgentDefinitionDraft_addAgentDefinition(
				randomAgentDefinition);

		assertEquals(randomAgentDefinition, postAgentDefinition);
		assertValid(postAgentDefinition);
	}

	protected AgentDefinition testPostAgentDefinitionDraft_addAgentDefinition(
			AgentDefinition agentDefinition)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		AgentDefinition agentDefinition1 =
			testBatchEngineDeleteImportTask_addAgentDefinition();

		testBatchEngineDeleteImportTask_deleteAgentDefinition(
			200, agentDefinition1.getExternalReferenceCode());
	}

	protected AgentDefinition
			testBatchEngineDeleteImportTask_addAgentDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void testBatchEngineDeleteImportTask_deleteAgentDefinition(
			int expectedStatusCode, String externalReferenceCode,
			String... parameters)
		throws Exception {

		ImportTaskResource importTaskResource = ImportTaskResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).parameters(
			parameters
		).build();

		HttpResponse httpResponse =
			importTaskResource.deleteImportTaskHttpResponse(
				"com.liferay.ai.hub.rest.dto.v1_0.AgentDefinition", null, null,
				null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		if (expectedStatusCode == 200) {
			waitForFinish(
				"COMPLETED",
				JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
		}
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertContains(
		AgentDefinition agentDefinition,
		List<AgentDefinition> agentDefinitions) {

		boolean contains = false;

		for (AgentDefinition item : agentDefinitions) {
			if (equals(agentDefinition, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			agentDefinitions + " does not contain " + agentDefinition,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		AgentDefinition agentDefinition1, AgentDefinition agentDefinition2) {

		Assert.assertTrue(
			agentDefinition1 + " does not equal " + agentDefinition2,
			equals(agentDefinition1, agentDefinition2));
	}

	protected void assertEquals(
		List<AgentDefinition> agentDefinitions1,
		List<AgentDefinition> agentDefinitions2) {

		Assert.assertEquals(agentDefinitions1.size(), agentDefinitions2.size());

		for (int i = 0; i < agentDefinitions1.size(); i++) {
			AgentDefinition agentDefinition1 = agentDefinitions1.get(i);
			AgentDefinition agentDefinition2 = agentDefinitions2.get(i);

			assertEquals(agentDefinition1, agentDefinition2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<AgentDefinition> agentDefinitions1,
		List<AgentDefinition> agentDefinitions2) {

		Assert.assertEquals(agentDefinitions1.size(), agentDefinitions2.size());

		for (AgentDefinition agentDefinition1 : agentDefinitions1) {
			boolean contains = false;

			for (AgentDefinition agentDefinition2 : agentDefinitions2) {
				if (equals(agentDefinition1, agentDefinition2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				agentDefinitions2 + " does not contain " + agentDefinition1,
				contains);
		}
	}

	protected void assertValid(AgentDefinition agentDefinition)
		throws Exception {

		boolean valid = true;

		if (agentDefinition.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (agentDefinition.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (agentDefinition.getActive() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (agentDefinition.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (agentDefinition.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("inputVariables", additionalAssertFieldName)) {
				if (agentDefinition.getInputVariables() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("model", additionalAssertFieldName)) {
				if (agentDefinition.getModel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("outputVariable", additionalAssertFieldName)) {
				if (agentDefinition.getOutputVariable() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (agentDefinition.getStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("system", additionalAssertFieldName)) {
				if (agentDefinition.getSystem() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (agentDefinition.getTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("version", additionalAssertFieldName)) {
				if (agentDefinition.getVersion() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowDefinitionName", additionalAssertFieldName)) {

				if (agentDefinition.getWorkflowDefinitionName() == null) {
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

	protected void assertValid(Page<AgentDefinition> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<AgentDefinition> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<AgentDefinition> agentDefinitions =
			page.getItems();

		int size = agentDefinitions.size();

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

		graphQLFields.add(new GraphQLField("externalReferenceCode"));

		graphQLFields.add(new GraphQLField("id"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.ai.hub.rest.dto.v1_0.AgentDefinition.class)) {

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
		AgentDefinition agentDefinition1, AgentDefinition agentDefinition2) {

		if (agentDefinition1 == agentDefinition2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)agentDefinition1.getActions(),
						(Map)agentDefinition2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						agentDefinition1.getActive(),
						agentDefinition2.getActive())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						agentDefinition1.getDescription(),
						agentDefinition2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						agentDefinition1.getExternalReferenceCode(),
						agentDefinition2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						agentDefinition1.getId(), agentDefinition2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("inputVariables", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						agentDefinition1.getInputVariables(),
						agentDefinition2.getInputVariables())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("model", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						agentDefinition1.getModel(),
						agentDefinition2.getModel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("outputVariable", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						agentDefinition1.getOutputVariable(),
						agentDefinition2.getOutputVariable())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						agentDefinition1.getStatus(),
						agentDefinition2.getStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("system", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						agentDefinition1.getSystem(),
						agentDefinition2.getSystem())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						agentDefinition1.getTitle(),
						agentDefinition2.getTitle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("version", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						agentDefinition1.getVersion(),
						agentDefinition2.getVersion())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowDefinitionName", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						agentDefinition1.getWorkflowDefinitionName(),
						agentDefinition2.getWorkflowDefinitionName())) {

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

		if (!(_agentDefinitionResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_agentDefinitionResource;

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
		AgentDefinition agentDefinition) {

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

		if (entityFieldName.equals("active")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("description")) {
			Object object = agentDefinition.getDescription();

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

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = agentDefinition.getExternalReferenceCode();

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
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("inputVariables")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("model")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("outputVariable")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("status")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("system")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("title")) {
			Object object = agentDefinition.getTitle();

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

		if (entityFieldName.equals("version")) {
			sb.append(String.valueOf(agentDefinition.getVersion()));

			return sb.toString();
		}

		if (entityFieldName.equals("workflowDefinitionName")) {
			Object object = agentDefinition.getWorkflowDefinitionName();

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

	protected AgentDefinition randomAgentDefinition() throws Exception {
		return new AgentDefinition() {
			{
				active = RandomTestUtil.randomBoolean();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				system = RandomTestUtil.randomBoolean();
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
				version = RandomTestUtil.randomInt();
				workflowDefinitionName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected AgentDefinition randomIrrelevantAgentDefinition()
		throws Exception {

		AgentDefinition randomIrrelevantAgentDefinition =
			randomAgentDefinition();

		return randomIrrelevantAgentDefinition;
	}

	protected AgentDefinition randomPatchAgentDefinition() throws Exception {
		return randomAgentDefinition();
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

	protected AgentDefinitionResource agentDefinitionResource;
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
		LogFactoryUtil.getLog(BaseAgentDefinitionResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.ai.hub.rest.resource.v1_0.AgentDefinitionResource
		_agentDefinitionResource;

}
// LIFERAY-REST-BUILDER-HASH:-1610169972