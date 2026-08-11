/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.exportimport.rest.client.dto.v1_0.ScheduledPublishProcess;
import com.liferay.exportimport.rest.client.dto.v1_0.Type;
import com.liferay.exportimport.rest.client.http.HttpInvoker;
import com.liferay.exportimport.rest.client.pagination.Page;
import com.liferay.exportimport.rest.client.pagination.Pagination;
import com.liferay.exportimport.rest.client.resource.v1_0.ScheduledPublishProcessResource;
import com.liferay.exportimport.rest.client.serdes.v1_0.ScheduledPublishProcessSerDes;
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
import com.liferay.portal.kernel.test.util.JAXRSWhiteboardTestUtil;
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
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public abstract class BaseScheduledPublishProcessResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		JAXRSWhiteboardTestUtil.ensureReady();
	}

	@Before
	public void setUp() throws Exception {
		irrelevantGroup = GroupTestUtil.addGroup();
		testGroup = GroupTestUtil.addGroup();

		testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		_scheduledPublishProcessResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		scheduledPublishProcessResource =
			ScheduledPublishProcessResource.builder(
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

		ScheduledPublishProcess scheduledPublishProcess1 =
			randomScheduledPublishProcess();

		String json = objectMapper.writeValueAsString(scheduledPublishProcess1);

		ScheduledPublishProcess scheduledPublishProcess2 =
			ScheduledPublishProcessSerDes.toDTO(json);

		Assert.assertTrue(
			equals(scheduledPublishProcess1, scheduledPublishProcess2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ScheduledPublishProcess scheduledPublishProcess =
			randomScheduledPublishProcess();

		String json1 = objectMapper.writeValueAsString(scheduledPublishProcess);
		String json2 = ScheduledPublishProcessSerDes.toJSON(
			scheduledPublishProcess);

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

		ScheduledPublishProcess scheduledPublishProcess =
			randomScheduledPublishProcess();

		scheduledPublishProcess.setCronExpression(regex);
		scheduledPublishProcess.setName(regex);

		String json = ScheduledPublishProcessSerDes.toJSON(
			scheduledPublishProcess);

		Assert.assertFalse(json.contains(regex));

		scheduledPublishProcess = ScheduledPublishProcessSerDes.toDTO(json);

		Assert.assertEquals(regex, scheduledPublishProcess.getCronExpression());
		Assert.assertEquals(regex, scheduledPublishProcess.getName());
	}

	@Test
	public void testDeleteSiteScheduledPublishProcess() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ScheduledPublishProcess scheduledPublishProcess =
			testDeleteSiteScheduledPublishProcess_addScheduledPublishProcess();

		assertHttpResponseStatusCode(
			204,
			scheduledPublishProcessResource.
				deleteSiteScheduledPublishProcessHttpResponse(
					testDeleteSiteScheduledPublishProcess_getSiteExternalReferenceCode(),
					scheduledPublishProcess.getId()));

		assertHttpResponseStatusCode(
			404,
			scheduledPublishProcessResource.
				getSiteScheduledPublishProcessHttpResponse(
					testDeleteSiteScheduledPublishProcess_getSiteExternalReferenceCode(),
					scheduledPublishProcess.getId()));
		assertHttpResponseStatusCode(
			404,
			scheduledPublishProcessResource.
				getSiteScheduledPublishProcessHttpResponse(
					testDeleteSiteScheduledPublishProcess_getSiteExternalReferenceCode(),
					0L));
	}

	protected ScheduledPublishProcess
			testDeleteSiteScheduledPublishProcess_addScheduledPublishProcess()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteSiteScheduledPublishProcess_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	@Test
	public void testGetSiteScheduledPublishProcess() throws Exception {
		ScheduledPublishProcess postScheduledPublishProcess =
			testGetSiteScheduledPublishProcess_addScheduledPublishProcess();

		ScheduledPublishProcess getScheduledPublishProcess =
			scheduledPublishProcessResource.getSiteScheduledPublishProcess(
				testGetSiteScheduledPublishProcess_getSiteExternalReferenceCode(),
				postScheduledPublishProcess.getId());

		assertEquals(postScheduledPublishProcess, getScheduledPublishProcess);
		assertValid(getScheduledPublishProcess);
	}

	protected ScheduledPublishProcess
			testGetSiteScheduledPublishProcess_addScheduledPublishProcess()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteScheduledPublishProcess_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	@Test
	public void testGetSiteScheduledPublishProcessesPage() throws Exception {
		String siteExternalReferenceCode =
			testGetSiteScheduledPublishProcessesPage_getSiteExternalReferenceCode();
		String irrelevantSiteExternalReferenceCode =
			testGetSiteScheduledPublishProcessesPage_getIrrelevantSiteExternalReferenceCode();

		Page<ScheduledPublishProcess> page =
			scheduledPublishProcessResource.
				getSiteScheduledPublishProcessesPage(
					siteExternalReferenceCode, null, Pagination.of(1, 10),
					null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteExternalReferenceCode != null) {
			ScheduledPublishProcess irrelevantScheduledPublishProcess =
				testGetSiteScheduledPublishProcessesPage_addScheduledPublishProcess(
					irrelevantSiteExternalReferenceCode,
					randomIrrelevantScheduledPublishProcess());

			page =
				scheduledPublishProcessResource.
					getSiteScheduledPublishProcessesPage(
						irrelevantSiteExternalReferenceCode, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantScheduledPublishProcess,
				(List<ScheduledPublishProcess>)page.getItems());
			assertValid(
				page,
				testGetSiteScheduledPublishProcessesPage_getExpectedActions(
					irrelevantSiteExternalReferenceCode));
		}

		ScheduledPublishProcess scheduledPublishProcess1 =
			testGetSiteScheduledPublishProcessesPage_addScheduledPublishProcess(
				siteExternalReferenceCode, randomScheduledPublishProcess());

		ScheduledPublishProcess scheduledPublishProcess2 =
			testGetSiteScheduledPublishProcessesPage_addScheduledPublishProcess(
				siteExternalReferenceCode, randomScheduledPublishProcess());

		page =
			scheduledPublishProcessResource.
				getSiteScheduledPublishProcessesPage(
					siteExternalReferenceCode, null,
					Pagination.of(1, (int)totalCount + 2), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			scheduledPublishProcess1,
			(List<ScheduledPublishProcess>)page.getItems());
		assertContains(
			scheduledPublishProcess2,
			(List<ScheduledPublishProcess>)page.getItems());
		assertValid(
			page,
			testGetSiteScheduledPublishProcessesPage_getExpectedActions(
				siteExternalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetSiteScheduledPublishProcessesPage_getExpectedActions(
				String siteExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSiteScheduledPublishProcessesPageWithPagination()
		throws Exception {

		String siteExternalReferenceCode =
			testGetSiteScheduledPublishProcessesPage_getSiteExternalReferenceCode();

		Page<ScheduledPublishProcess> scheduledPublishProcessesPage =
			scheduledPublishProcessResource.
				getSiteScheduledPublishProcessesPage(
					siteExternalReferenceCode, null, null, null);

		int totalCount = GetterUtil.getInteger(
			scheduledPublishProcessesPage.getTotalCount());

		ScheduledPublishProcess scheduledPublishProcess1 =
			testGetSiteScheduledPublishProcessesPage_addScheduledPublishProcess(
				siteExternalReferenceCode, randomScheduledPublishProcess());

		ScheduledPublishProcess scheduledPublishProcess2 =
			testGetSiteScheduledPublishProcessesPage_addScheduledPublishProcess(
				siteExternalReferenceCode, randomScheduledPublishProcess());

		ScheduledPublishProcess scheduledPublishProcess3 =
			testGetSiteScheduledPublishProcessesPage_addScheduledPublishProcess(
				siteExternalReferenceCode, randomScheduledPublishProcess());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ScheduledPublishProcess> page1 =
				scheduledPublishProcessResource.
					getSiteScheduledPublishProcessesPage(
						siteExternalReferenceCode, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				scheduledPublishProcess1,
				(List<ScheduledPublishProcess>)page1.getItems());

			Page<ScheduledPublishProcess> page2 =
				scheduledPublishProcessResource.
					getSiteScheduledPublishProcessesPage(
						siteExternalReferenceCode, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				scheduledPublishProcess2,
				(List<ScheduledPublishProcess>)page2.getItems());

			Page<ScheduledPublishProcess> page3 =
				scheduledPublishProcessResource.
					getSiteScheduledPublishProcessesPage(
						siteExternalReferenceCode, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				scheduledPublishProcess3,
				(List<ScheduledPublishProcess>)page3.getItems());
		}
		else {
			Page<ScheduledPublishProcess> page1 =
				scheduledPublishProcessResource.
					getSiteScheduledPublishProcessesPage(
						siteExternalReferenceCode, null,
						Pagination.of(1, totalCount + 2), null);

			List<ScheduledPublishProcess> scheduledPublishProcesses1 =
				(List<ScheduledPublishProcess>)page1.getItems();

			Assert.assertEquals(
				scheduledPublishProcesses1.toString(), totalCount + 2,
				scheduledPublishProcesses1.size());

			Page<ScheduledPublishProcess> page2 =
				scheduledPublishProcessResource.
					getSiteScheduledPublishProcessesPage(
						siteExternalReferenceCode, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ScheduledPublishProcess> scheduledPublishProcesses2 =
				(List<ScheduledPublishProcess>)page2.getItems();

			Assert.assertEquals(
				scheduledPublishProcesses2.toString(), 1,
				scheduledPublishProcesses2.size());

			Page<ScheduledPublishProcess> page3 =
				scheduledPublishProcessResource.
					getSiteScheduledPublishProcessesPage(
						siteExternalReferenceCode, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				scheduledPublishProcess1,
				(List<ScheduledPublishProcess>)page3.getItems());
			assertContains(
				scheduledPublishProcess2,
				(List<ScheduledPublishProcess>)page3.getItems());
			assertContains(
				scheduledPublishProcess3,
				(List<ScheduledPublishProcess>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteScheduledPublishProcessesPageWithSortDateTime()
		throws Exception {

		testGetSiteScheduledPublishProcessesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, scheduledPublishProcess1, scheduledPublishProcess2) ->{
				BeanTestUtil.setProperty(
					scheduledPublishProcess1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteScheduledPublishProcessesPageWithSortDouble()
		throws Exception {

		testGetSiteScheduledPublishProcessesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, scheduledPublishProcess1, scheduledPublishProcess2) ->{
				BeanTestUtil.setProperty(
					scheduledPublishProcess1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					scheduledPublishProcess2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteScheduledPublishProcessesPageWithSortInteger()
		throws Exception {

		testGetSiteScheduledPublishProcessesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, scheduledPublishProcess1, scheduledPublishProcess2) ->{
				BeanTestUtil.setProperty(
					scheduledPublishProcess1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					scheduledPublishProcess2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteScheduledPublishProcessesPageWithSortString()
		throws Exception {

		testGetSiteScheduledPublishProcessesPageWithSort(
			EntityField.Type.STRING,
			(entityField, scheduledPublishProcess1, scheduledPublishProcess2) ->{
				Class<?> clazz = scheduledPublishProcess1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						scheduledPublishProcess1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						scheduledPublishProcess2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						scheduledPublishProcess1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						scheduledPublishProcess2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						scheduledPublishProcess1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						scheduledPublishProcess2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSiteScheduledPublishProcessesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, ScheduledPublishProcess, ScheduledPublishProcess,
				 Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String siteExternalReferenceCode =
			testGetSiteScheduledPublishProcessesPage_getSiteExternalReferenceCode();

		ScheduledPublishProcess scheduledPublishProcess1 =
			randomScheduledPublishProcess();
		ScheduledPublishProcess scheduledPublishProcess2 =
			randomScheduledPublishProcess();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, scheduledPublishProcess1,
				scheduledPublishProcess2);
		}

		scheduledPublishProcess1 =
			testGetSiteScheduledPublishProcessesPage_addScheduledPublishProcess(
				siteExternalReferenceCode, scheduledPublishProcess1);

		scheduledPublishProcess2 =
			testGetSiteScheduledPublishProcessesPage_addScheduledPublishProcess(
				siteExternalReferenceCode, scheduledPublishProcess2);

		Page<ScheduledPublishProcess> page =
			scheduledPublishProcessResource.
				getSiteScheduledPublishProcessesPage(
					siteExternalReferenceCode, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ScheduledPublishProcess> ascPage =
				scheduledPublishProcessResource.
					getSiteScheduledPublishProcessesPage(
						siteExternalReferenceCode, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				scheduledPublishProcess1,
				(List<ScheduledPublishProcess>)ascPage.getItems());
			assertContains(
				scheduledPublishProcess2,
				(List<ScheduledPublishProcess>)ascPage.getItems());

			Page<ScheduledPublishProcess> descPage =
				scheduledPublishProcessResource.
					getSiteScheduledPublishProcessesPage(
						siteExternalReferenceCode, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				scheduledPublishProcess2,
				(List<ScheduledPublishProcess>)descPage.getItems());
			assertContains(
				scheduledPublishProcess1,
				(List<ScheduledPublishProcess>)descPage.getItems());
		}
	}

	protected ScheduledPublishProcess
			testGetSiteScheduledPublishProcessesPage_addScheduledPublishProcess(
				String siteExternalReferenceCode,
				ScheduledPublishProcess scheduledPublishProcess)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteScheduledPublishProcessesPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteScheduledPublishProcessesPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected void assertContains(
		ScheduledPublishProcess scheduledPublishProcess,
		List<ScheduledPublishProcess> scheduledPublishProcesses) {

		boolean contains = false;

		for (ScheduledPublishProcess item : scheduledPublishProcesses) {
			if (equals(scheduledPublishProcess, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			scheduledPublishProcesses + " does not contain " +
				scheduledPublishProcess,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ScheduledPublishProcess scheduledPublishProcess1,
		ScheduledPublishProcess scheduledPublishProcess2) {

		Assert.assertTrue(
			scheduledPublishProcess1 + " does not equal " +
				scheduledPublishProcess2,
			equals(scheduledPublishProcess1, scheduledPublishProcess2));
	}

	protected void assertEquals(
		List<ScheduledPublishProcess> scheduledPublishProcesses1,
		List<ScheduledPublishProcess> scheduledPublishProcesses2) {

		Assert.assertEquals(
			scheduledPublishProcesses1.size(),
			scheduledPublishProcesses2.size());

		for (int i = 0; i < scheduledPublishProcesses1.size(); i++) {
			ScheduledPublishProcess scheduledPublishProcess1 =
				scheduledPublishProcesses1.get(i);
			ScheduledPublishProcess scheduledPublishProcess2 =
				scheduledPublishProcesses2.get(i);

			assertEquals(scheduledPublishProcess1, scheduledPublishProcess2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ScheduledPublishProcess> scheduledPublishProcesses1,
		List<ScheduledPublishProcess> scheduledPublishProcesses2) {

		Assert.assertEquals(
			scheduledPublishProcesses1.size(),
			scheduledPublishProcesses2.size());

		for (ScheduledPublishProcess scheduledPublishProcess1 :
				scheduledPublishProcesses1) {

			boolean contains = false;

			for (ScheduledPublishProcess scheduledPublishProcess2 :
					scheduledPublishProcesses2) {

				if (equals(
						scheduledPublishProcess1, scheduledPublishProcess2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				scheduledPublishProcesses2 + " does not contain " +
					scheduledPublishProcess1,
				contains);
		}
	}

	protected void assertValid(ScheduledPublishProcess scheduledPublishProcess)
		throws Exception {

		boolean valid = true;

		if (scheduledPublishProcess.getDateCreated() == null) {
			valid = false;
		}

		if (scheduledPublishProcess.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (scheduledPublishProcess.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("cronExpression", additionalAssertFieldName)) {
				if (scheduledPublishProcess.getCronExpression() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (scheduledPublishProcess.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("nextFireDate", additionalAssertFieldName)) {
				if (scheduledPublishProcess.getNextFireDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"publishParameters", additionalAssertFieldName)) {

				if (scheduledPublishProcess.getPublishParameters() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("scheduleEndDate", additionalAssertFieldName)) {
				if (scheduledPublishProcess.getScheduleEndDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"scheduleStartDate", additionalAssertFieldName)) {

				if (scheduledPublishProcess.getScheduleStartDate() == null) {
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

	protected void assertValid(Page<ScheduledPublishProcess> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ScheduledPublishProcess> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ScheduledPublishProcess>
			scheduledPublishProcesses = page.getItems();

		int size = scheduledPublishProcesses.size();

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
					com.liferay.exportimport.rest.dto.v1_0.
						ScheduledPublishProcess.class)) {

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
		ScheduledPublishProcess scheduledPublishProcess1,
		ScheduledPublishProcess scheduledPublishProcess2) {

		if (scheduledPublishProcess1 == scheduledPublishProcess2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						scheduledPublishProcess1.getCreator(),
						scheduledPublishProcess2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("cronExpression", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						scheduledPublishProcess1.getCronExpression(),
						scheduledPublishProcess2.getCronExpression())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						scheduledPublishProcess1.getDateCreated(),
						scheduledPublishProcess2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						scheduledPublishProcess1.getId(),
						scheduledPublishProcess2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						scheduledPublishProcess1.getName(),
						scheduledPublishProcess2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("nextFireDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						scheduledPublishProcess1.getNextFireDate(),
						scheduledPublishProcess2.getNextFireDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"publishParameters", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						scheduledPublishProcess1.getPublishParameters(),
						scheduledPublishProcess2.getPublishParameters())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("scheduleEndDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						scheduledPublishProcess1.getScheduleEndDate(),
						scheduledPublishProcess2.getScheduleEndDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"scheduleStartDate", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						scheduledPublishProcess1.getScheduleStartDate(),
						scheduledPublishProcess2.getScheduleStartDate())) {

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

		if (!(_scheduledPublishProcessResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_scheduledPublishProcessResource;

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
		ScheduledPublishProcess scheduledPublishProcess) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("cronExpression")) {
			Object object = scheduledPublishProcess.getCronExpression();

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

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = scheduledPublishProcess.getDateCreated();

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

				sb.append(
					_format.format(scheduledPublishProcess.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("name")) {
			Object object = scheduledPublishProcess.getName();

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

		if (entityFieldName.equals("nextFireDate")) {
			if (operator.equals("between")) {
				Date date = scheduledPublishProcess.getNextFireDate();

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

				sb.append(
					_format.format(scheduledPublishProcess.getNextFireDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("publishParameters")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("scheduleEndDate")) {
			if (operator.equals("between")) {
				Date date = scheduledPublishProcess.getScheduleEndDate();

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

				sb.append(
					_format.format(
						scheduledPublishProcess.getScheduleEndDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("scheduleStartDate")) {
			if (operator.equals("between")) {
				Date date = scheduledPublishProcess.getScheduleStartDate();

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

				sb.append(
					_format.format(
						scheduledPublishProcess.getScheduleStartDate()));
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

	protected ScheduledPublishProcess randomScheduledPublishProcess()
		throws Exception {

		return new ScheduledPublishProcess() {
			{
				cronExpression = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				nextFireDate = RandomTestUtil.nextDate();
				scheduleEndDate = RandomTestUtil.nextDate();
				scheduleStartDate = RandomTestUtil.nextDate();
			}
		};
	}

	protected ScheduledPublishProcess randomIrrelevantScheduledPublishProcess()
		throws Exception {

		ScheduledPublishProcess randomIrrelevantScheduledPublishProcess =
			randomScheduledPublishProcess();

		return randomIrrelevantScheduledPublishProcess;
	}

	protected ScheduledPublishProcess randomPatchScheduledPublishProcess()
		throws Exception {

		return randomScheduledPublishProcess();
	}

	protected ScheduledPublishProcessResource scheduledPublishProcessResource;
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
			BaseScheduledPublishProcessResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.exportimport.rest.resource.v1_0.
			ScheduledPublishProcessResource _scheduledPublishProcessResource;

}
// LIFERAY-REST-BUILDER-HASH:1629459487