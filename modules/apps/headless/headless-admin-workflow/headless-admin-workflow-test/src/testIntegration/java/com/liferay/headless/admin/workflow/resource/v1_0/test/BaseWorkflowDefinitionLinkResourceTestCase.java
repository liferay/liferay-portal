/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowDefinitionLink;
import com.liferay.headless.admin.workflow.client.http.HttpInvoker;
import com.liferay.headless.admin.workflow.client.pagination.Page;
import com.liferay.headless.admin.workflow.client.pagination.Pagination;
import com.liferay.headless.admin.workflow.client.resource.v1_0.WorkflowDefinitionLinkResource;
import com.liferay.headless.admin.workflow.client.serdes.v1_0.WorkflowDefinitionLinkSerDes;
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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;
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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public abstract class BaseWorkflowDefinitionLinkResourceTestCase {

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

		_workflowDefinitionLinkResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		workflowDefinitionLinkResource = WorkflowDefinitionLinkResource.builder(
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

		WorkflowDefinitionLink workflowDefinitionLink1 =
			randomWorkflowDefinitionLink();

		String json = objectMapper.writeValueAsString(workflowDefinitionLink1);

		WorkflowDefinitionLink workflowDefinitionLink2 =
			WorkflowDefinitionLinkSerDes.toDTO(json);

		Assert.assertTrue(
			equals(workflowDefinitionLink1, workflowDefinitionLink2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		WorkflowDefinitionLink workflowDefinitionLink =
			randomWorkflowDefinitionLink();

		String json1 = objectMapper.writeValueAsString(workflowDefinitionLink);
		String json2 = WorkflowDefinitionLinkSerDes.toJSON(
			workflowDefinitionLink);

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

		WorkflowDefinitionLink workflowDefinitionLink =
			randomWorkflowDefinitionLink();

		workflowDefinitionLink.setClassName(regex);
		workflowDefinitionLink.setExternalReferenceCode(regex);
		workflowDefinitionLink.setGroupExternalReferenceCode(regex);
		workflowDefinitionLink.setWorkflowDefinitionName(regex);

		String json = WorkflowDefinitionLinkSerDes.toJSON(
			workflowDefinitionLink);

		Assert.assertFalse(json.contains(regex));

		workflowDefinitionLink = WorkflowDefinitionLinkSerDes.toDTO(json);

		Assert.assertEquals(regex, workflowDefinitionLink.getClassName());
		Assert.assertEquals(
			regex, workflowDefinitionLink.getExternalReferenceCode());
		Assert.assertEquals(
			regex, workflowDefinitionLink.getGroupExternalReferenceCode());
		Assert.assertEquals(
			regex, workflowDefinitionLink.getWorkflowDefinitionName());
	}

	@Test
	public void testGetWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage()
		throws Exception {

		String externalReferenceCode =
			testGetWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage_getIrrelevantExternalReferenceCode();

		Page<WorkflowDefinitionLink> page =
			workflowDefinitionLinkResource.
				getWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			WorkflowDefinitionLink irrelevantWorkflowDefinitionLink =
				testGetWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage_addWorkflowDefinitionLink(
					irrelevantExternalReferenceCode,
					randomIrrelevantWorkflowDefinitionLink());

			page =
				workflowDefinitionLinkResource.
					getWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantWorkflowDefinitionLink,
				(List<WorkflowDefinitionLink>)page.getItems());
			assertValid(
				page,
				testGetWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		WorkflowDefinitionLink workflowDefinitionLink1 =
			testGetWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage_addWorkflowDefinitionLink(
				externalReferenceCode, randomWorkflowDefinitionLink());

		WorkflowDefinitionLink workflowDefinitionLink2 =
			testGetWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage_addWorkflowDefinitionLink(
				externalReferenceCode, randomWorkflowDefinitionLink());

		page =
			workflowDefinitionLinkResource.
				getWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			workflowDefinitionLink1,
			(List<WorkflowDefinitionLink>)page.getItems());
		assertContains(
			workflowDefinitionLink2,
			(List<WorkflowDefinitionLink>)page.getItems());
		assertValid(
			page,
			testGetWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage_getExpectedActions(
				externalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage_getExternalReferenceCode();

		Page<WorkflowDefinitionLink> workflowDefinitionLinksPage =
			workflowDefinitionLinkResource.
				getWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			workflowDefinitionLinksPage.getTotalCount());

		WorkflowDefinitionLink workflowDefinitionLink1 =
			testGetWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage_addWorkflowDefinitionLink(
				externalReferenceCode, randomWorkflowDefinitionLink());

		WorkflowDefinitionLink workflowDefinitionLink2 =
			testGetWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage_addWorkflowDefinitionLink(
				externalReferenceCode, randomWorkflowDefinitionLink());

		WorkflowDefinitionLink workflowDefinitionLink3 =
			testGetWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage_addWorkflowDefinitionLink(
				externalReferenceCode, randomWorkflowDefinitionLink());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<WorkflowDefinitionLink> page1 =
				workflowDefinitionLinkResource.
					getWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				workflowDefinitionLink1,
				(List<WorkflowDefinitionLink>)page1.getItems());

			Page<WorkflowDefinitionLink> page2 =
				workflowDefinitionLinkResource.
					getWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				workflowDefinitionLink2,
				(List<WorkflowDefinitionLink>)page2.getItems());

			Page<WorkflowDefinitionLink> page3 =
				workflowDefinitionLinkResource.
					getWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				workflowDefinitionLink3,
				(List<WorkflowDefinitionLink>)page3.getItems());
		}
		else {
			Page<WorkflowDefinitionLink> page1 =
				workflowDefinitionLinkResource.
					getWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<WorkflowDefinitionLink> workflowDefinitionLinks1 =
				(List<WorkflowDefinitionLink>)page1.getItems();

			Assert.assertEquals(
				workflowDefinitionLinks1.toString(), totalCount + 2,
				workflowDefinitionLinks1.size());

			Page<WorkflowDefinitionLink> page2 =
				workflowDefinitionLinkResource.
					getWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<WorkflowDefinitionLink> workflowDefinitionLinks2 =
				(List<WorkflowDefinitionLink>)page2.getItems();

			Assert.assertEquals(
				workflowDefinitionLinks2.toString(), 1,
				workflowDefinitionLinks2.size());

			Page<WorkflowDefinitionLink> page3 =
				workflowDefinitionLinkResource.
					getWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				workflowDefinitionLink1,
				(List<WorkflowDefinitionLink>)page3.getItems());
			assertContains(
				workflowDefinitionLink2,
				(List<WorkflowDefinitionLink>)page3.getItems());
			assertContains(
				workflowDefinitionLink3,
				(List<WorkflowDefinitionLink>)page3.getItems());
		}
	}

	protected WorkflowDefinitionLink
			testGetWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage_addWorkflowDefinitionLink(
				String externalReferenceCode,
				WorkflowDefinitionLink workflowDefinitionLink)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetWorkflowDefinitionWorkflowDefinitionLinksPage()
		throws Exception {

		Long workflowDefinitionId =
			testGetWorkflowDefinitionWorkflowDefinitionLinksPage_getWorkflowDefinitionId();
		Long irrelevantWorkflowDefinitionId =
			testGetWorkflowDefinitionWorkflowDefinitionLinksPage_getIrrelevantWorkflowDefinitionId();

		Page<WorkflowDefinitionLink> page =
			workflowDefinitionLinkResource.
				getWorkflowDefinitionWorkflowDefinitionLinksPage(
					workflowDefinitionId, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantWorkflowDefinitionId != null) {
			WorkflowDefinitionLink irrelevantWorkflowDefinitionLink =
				testGetWorkflowDefinitionWorkflowDefinitionLinksPage_addWorkflowDefinitionLink(
					irrelevantWorkflowDefinitionId,
					randomIrrelevantWorkflowDefinitionLink());

			page =
				workflowDefinitionLinkResource.
					getWorkflowDefinitionWorkflowDefinitionLinksPage(
						irrelevantWorkflowDefinitionId,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantWorkflowDefinitionLink,
				(List<WorkflowDefinitionLink>)page.getItems());
			assertValid(
				page,
				testGetWorkflowDefinitionWorkflowDefinitionLinksPage_getExpectedActions(
					irrelevantWorkflowDefinitionId));
		}

		WorkflowDefinitionLink workflowDefinitionLink1 =
			testGetWorkflowDefinitionWorkflowDefinitionLinksPage_addWorkflowDefinitionLink(
				workflowDefinitionId, randomWorkflowDefinitionLink());

		WorkflowDefinitionLink workflowDefinitionLink2 =
			testGetWorkflowDefinitionWorkflowDefinitionLinksPage_addWorkflowDefinitionLink(
				workflowDefinitionId, randomWorkflowDefinitionLink());

		page =
			workflowDefinitionLinkResource.
				getWorkflowDefinitionWorkflowDefinitionLinksPage(
					workflowDefinitionId, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			workflowDefinitionLink1,
			(List<WorkflowDefinitionLink>)page.getItems());
		assertContains(
			workflowDefinitionLink2,
			(List<WorkflowDefinitionLink>)page.getItems());
		assertValid(
			page,
			testGetWorkflowDefinitionWorkflowDefinitionLinksPage_getExpectedActions(
				workflowDefinitionId));
	}

	protected Map<String, Map<String, String>>
			testGetWorkflowDefinitionWorkflowDefinitionLinksPage_getExpectedActions(
				Long workflowDefinitionId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-admin-workflow/v1.0/workflow-definitions/{workflowDefinitionId}/workflow-definition-links/batch".
				replace(
					"{workflowDefinitionId}",
					String.valueOf(workflowDefinitionId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetWorkflowDefinitionWorkflowDefinitionLinksPageWithPagination()
		throws Exception {

		Long workflowDefinitionId =
			testGetWorkflowDefinitionWorkflowDefinitionLinksPage_getWorkflowDefinitionId();

		Page<WorkflowDefinitionLink> workflowDefinitionLinksPage =
			workflowDefinitionLinkResource.
				getWorkflowDefinitionWorkflowDefinitionLinksPage(
					workflowDefinitionId, null);

		int totalCount = GetterUtil.getInteger(
			workflowDefinitionLinksPage.getTotalCount());

		WorkflowDefinitionLink workflowDefinitionLink1 =
			testGetWorkflowDefinitionWorkflowDefinitionLinksPage_addWorkflowDefinitionLink(
				workflowDefinitionId, randomWorkflowDefinitionLink());

		WorkflowDefinitionLink workflowDefinitionLink2 =
			testGetWorkflowDefinitionWorkflowDefinitionLinksPage_addWorkflowDefinitionLink(
				workflowDefinitionId, randomWorkflowDefinitionLink());

		WorkflowDefinitionLink workflowDefinitionLink3 =
			testGetWorkflowDefinitionWorkflowDefinitionLinksPage_addWorkflowDefinitionLink(
				workflowDefinitionId, randomWorkflowDefinitionLink());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<WorkflowDefinitionLink> page1 =
				workflowDefinitionLinkResource.
					getWorkflowDefinitionWorkflowDefinitionLinksPage(
						workflowDefinitionId,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				workflowDefinitionLink1,
				(List<WorkflowDefinitionLink>)page1.getItems());

			Page<WorkflowDefinitionLink> page2 =
				workflowDefinitionLinkResource.
					getWorkflowDefinitionWorkflowDefinitionLinksPage(
						workflowDefinitionId,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				workflowDefinitionLink2,
				(List<WorkflowDefinitionLink>)page2.getItems());

			Page<WorkflowDefinitionLink> page3 =
				workflowDefinitionLinkResource.
					getWorkflowDefinitionWorkflowDefinitionLinksPage(
						workflowDefinitionId,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				workflowDefinitionLink3,
				(List<WorkflowDefinitionLink>)page3.getItems());
		}
		else {
			Page<WorkflowDefinitionLink> page1 =
				workflowDefinitionLinkResource.
					getWorkflowDefinitionWorkflowDefinitionLinksPage(
						workflowDefinitionId, Pagination.of(1, totalCount + 2));

			List<WorkflowDefinitionLink> workflowDefinitionLinks1 =
				(List<WorkflowDefinitionLink>)page1.getItems();

			Assert.assertEquals(
				workflowDefinitionLinks1.toString(), totalCount + 2,
				workflowDefinitionLinks1.size());

			Page<WorkflowDefinitionLink> page2 =
				workflowDefinitionLinkResource.
					getWorkflowDefinitionWorkflowDefinitionLinksPage(
						workflowDefinitionId, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<WorkflowDefinitionLink> workflowDefinitionLinks2 =
				(List<WorkflowDefinitionLink>)page2.getItems();

			Assert.assertEquals(
				workflowDefinitionLinks2.toString(), 1,
				workflowDefinitionLinks2.size());

			Page<WorkflowDefinitionLink> page3 =
				workflowDefinitionLinkResource.
					getWorkflowDefinitionWorkflowDefinitionLinksPage(
						workflowDefinitionId,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				workflowDefinitionLink1,
				(List<WorkflowDefinitionLink>)page3.getItems());
			assertContains(
				workflowDefinitionLink2,
				(List<WorkflowDefinitionLink>)page3.getItems());
			assertContains(
				workflowDefinitionLink3,
				(List<WorkflowDefinitionLink>)page3.getItems());
		}
	}

	protected WorkflowDefinitionLink
			testGetWorkflowDefinitionWorkflowDefinitionLinksPage_addWorkflowDefinitionLink(
				Long workflowDefinitionId,
				WorkflowDefinitionLink workflowDefinitionLink)
		throws Exception {

		return workflowDefinitionLinkResource.
			postWorkflowDefinitionWorkflowDefinitionLink(
				workflowDefinitionId, workflowDefinitionLink);
	}

	protected Long
			testGetWorkflowDefinitionWorkflowDefinitionLinksPage_getWorkflowDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkflowDefinitionWorkflowDefinitionLinksPage_getIrrelevantWorkflowDefinitionId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLink()
		throws Exception {

		WorkflowDefinitionLink randomWorkflowDefinitionLink =
			randomWorkflowDefinitionLink();

		WorkflowDefinitionLink postWorkflowDefinitionLink =
			testPostWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLink_addWorkflowDefinitionLink(
				randomWorkflowDefinitionLink);

		assertEquals(randomWorkflowDefinitionLink, postWorkflowDefinitionLink);
		assertValid(postWorkflowDefinitionLink);
	}

	protected WorkflowDefinitionLink
			testPostWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLink_addWorkflowDefinitionLink(
				WorkflowDefinitionLink workflowDefinitionLink)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostWorkflowDefinitionWorkflowDefinitionLink()
		throws Exception {

		WorkflowDefinitionLink randomWorkflowDefinitionLink =
			randomWorkflowDefinitionLink();

		WorkflowDefinitionLink postWorkflowDefinitionLink =
			testPostWorkflowDefinitionWorkflowDefinitionLink_addWorkflowDefinitionLink(
				randomWorkflowDefinitionLink);

		assertEquals(randomWorkflowDefinitionLink, postWorkflowDefinitionLink);
		assertValid(postWorkflowDefinitionLink);
	}

	protected WorkflowDefinitionLink
			testPostWorkflowDefinitionWorkflowDefinitionLink_addWorkflowDefinitionLink(
				WorkflowDefinitionLink workflowDefinitionLink)
		throws Exception {

		return workflowDefinitionLinkResource.
			postWorkflowDefinitionWorkflowDefinitionLink(
				testGetWorkflowDefinitionWorkflowDefinitionLinksPage_getWorkflowDefinitionId(),
				workflowDefinitionLink);
	}

	@Test
	public void testPutWorkflowDefinitionLinkByExternalReferenceCode()
		throws Exception {

		WorkflowDefinitionLink postWorkflowDefinitionLink =
			testPutWorkflowDefinitionLinkByExternalReferenceCode_addWorkflowDefinitionLink();

		WorkflowDefinitionLink randomWorkflowDefinitionLink =
			randomWorkflowDefinitionLink();

		WorkflowDefinitionLink putWorkflowDefinitionLink =
			workflowDefinitionLinkResource.
				putWorkflowDefinitionLinkByExternalReferenceCode(
					postWorkflowDefinitionLink.getExternalReferenceCode(),
					randomWorkflowDefinitionLink);

		assertEquals(randomWorkflowDefinitionLink, putWorkflowDefinitionLink);
		assertValid(putWorkflowDefinitionLink);

		WorkflowDefinitionLink getWorkflowDefinitionLink =
			testPutWorkflowDefinitionLinkByExternalReferenceCode_getWorkflowDefinitionLink(
				putWorkflowDefinitionLink.getExternalReferenceCode());

		assertEquals(randomWorkflowDefinitionLink, getWorkflowDefinitionLink);
		assertValid(getWorkflowDefinitionLink);

		WorkflowDefinitionLink newWorkflowDefinitionLink =
			testPutWorkflowDefinitionLinkByExternalReferenceCode_createWorkflowDefinitionLink();

		putWorkflowDefinitionLink =
			workflowDefinitionLinkResource.
				putWorkflowDefinitionLinkByExternalReferenceCode(
					newWorkflowDefinitionLink.getExternalReferenceCode(),
					newWorkflowDefinitionLink);

		assertEquals(newWorkflowDefinitionLink, putWorkflowDefinitionLink);
		assertValid(putWorkflowDefinitionLink);

		getWorkflowDefinitionLink =
			testPutWorkflowDefinitionLinkByExternalReferenceCode_getWorkflowDefinitionLink(
				putWorkflowDefinitionLink.getExternalReferenceCode());

		assertEquals(newWorkflowDefinitionLink, getWorkflowDefinitionLink);

		Assert.assertEquals(
			newWorkflowDefinitionLink.getExternalReferenceCode(),
			putWorkflowDefinitionLink.getExternalReferenceCode());
	}

	protected WorkflowDefinitionLink
		testPutWorkflowDefinitionLinkByExternalReferenceCode_getWorkflowDefinitionLink(
			String externalReferenceCode) {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected WorkflowDefinitionLink
			testPutWorkflowDefinitionLinkByExternalReferenceCode_addWorkflowDefinitionLink()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected WorkflowDefinitionLink
			testPutWorkflowDefinitionLinkByExternalReferenceCode_createWorkflowDefinitionLink()
		throws Exception {

		return randomWorkflowDefinitionLink();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected void assertContains(
		WorkflowDefinitionLink workflowDefinitionLink,
		List<WorkflowDefinitionLink> workflowDefinitionLinks) {

		boolean contains = false;

		for (WorkflowDefinitionLink item : workflowDefinitionLinks) {
			if (equals(workflowDefinitionLink, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			workflowDefinitionLinks + " does not contain " +
				workflowDefinitionLink,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		WorkflowDefinitionLink workflowDefinitionLink1,
		WorkflowDefinitionLink workflowDefinitionLink2) {

		Assert.assertTrue(
			workflowDefinitionLink1 + " does not equal " +
				workflowDefinitionLink2,
			equals(workflowDefinitionLink1, workflowDefinitionLink2));
	}

	protected void assertEquals(
		List<WorkflowDefinitionLink> workflowDefinitionLinks1,
		List<WorkflowDefinitionLink> workflowDefinitionLinks2) {

		Assert.assertEquals(
			workflowDefinitionLinks1.size(), workflowDefinitionLinks2.size());

		for (int i = 0; i < workflowDefinitionLinks1.size(); i++) {
			WorkflowDefinitionLink workflowDefinitionLink1 =
				workflowDefinitionLinks1.get(i);
			WorkflowDefinitionLink workflowDefinitionLink2 =
				workflowDefinitionLinks2.get(i);

			assertEquals(workflowDefinitionLink1, workflowDefinitionLink2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<WorkflowDefinitionLink> workflowDefinitionLinks1,
		List<WorkflowDefinitionLink> workflowDefinitionLinks2) {

		Assert.assertEquals(
			workflowDefinitionLinks1.size(), workflowDefinitionLinks2.size());

		for (WorkflowDefinitionLink workflowDefinitionLink1 :
				workflowDefinitionLinks1) {

			boolean contains = false;

			for (WorkflowDefinitionLink workflowDefinitionLink2 :
					workflowDefinitionLinks2) {

				if (equals(workflowDefinitionLink1, workflowDefinitionLink2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				workflowDefinitionLinks2 + " does not contain " +
					workflowDefinitionLink1,
				contains);
		}
	}

	protected void assertValid(WorkflowDefinitionLink workflowDefinitionLink)
		throws Exception {

		boolean valid = true;

		if (workflowDefinitionLink.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("className", additionalAssertFieldName)) {
				if (workflowDefinitionLink.getClassName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (workflowDefinitionLink.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"groupExternalReferenceCode", additionalAssertFieldName)) {

				if (workflowDefinitionLink.getGroupExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("groupId", additionalAssertFieldName)) {
				if (workflowDefinitionLink.getGroupId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowDefinitionName", additionalAssertFieldName)) {

				if (workflowDefinitionLink.getWorkflowDefinitionName() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowDefinitionVersion", additionalAssertFieldName)) {

				if (workflowDefinitionLink.getWorkflowDefinitionVersion() ==
						null) {

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

	protected void assertValid(Page<WorkflowDefinitionLink> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<WorkflowDefinitionLink> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<WorkflowDefinitionLink> workflowDefinitionLinks =
			page.getItems();

		int size = workflowDefinitionLinks.size();

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
					com.liferay.headless.admin.workflow.dto.v1_0.
						WorkflowDefinitionLink.class)) {

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
		WorkflowDefinitionLink workflowDefinitionLink1,
		WorkflowDefinitionLink workflowDefinitionLink2) {

		if (workflowDefinitionLink1 == workflowDefinitionLink2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("className", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowDefinitionLink1.getClassName(),
						workflowDefinitionLink2.getClassName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						workflowDefinitionLink1.getExternalReferenceCode(),
						workflowDefinitionLink2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"groupExternalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						workflowDefinitionLink1.getGroupExternalReferenceCode(),
						workflowDefinitionLink2.
							getGroupExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("groupId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowDefinitionLink1.getGroupId(),
						workflowDefinitionLink2.getGroupId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						workflowDefinitionLink1.getId(),
						workflowDefinitionLink2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowDefinitionName", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						workflowDefinitionLink1.getWorkflowDefinitionName(),
						workflowDefinitionLink2.getWorkflowDefinitionName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"workflowDefinitionVersion", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						workflowDefinitionLink1.getWorkflowDefinitionVersion(),
						workflowDefinitionLink2.
							getWorkflowDefinitionVersion())) {

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

		if (!(_workflowDefinitionLinkResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_workflowDefinitionLinkResource;

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
		WorkflowDefinitionLink workflowDefinitionLink) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("className")) {
			Object object = workflowDefinitionLink.getClassName();

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
			Object object = workflowDefinitionLink.getExternalReferenceCode();

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

		if (entityFieldName.equals("groupExternalReferenceCode")) {
			Object object =
				workflowDefinitionLink.getGroupExternalReferenceCode();

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

		if (entityFieldName.equals("groupId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("workflowDefinitionName")) {
			Object object = workflowDefinitionLink.getWorkflowDefinitionName();

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

		if (entityFieldName.equals("workflowDefinitionVersion")) {
			sb.append(
				String.valueOf(
					workflowDefinitionLink.getWorkflowDefinitionVersion()));

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

	protected WorkflowDefinitionLink randomWorkflowDefinitionLink()
		throws Exception {

		return new WorkflowDefinitionLink() {
			{
				className = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				groupExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				groupId = RandomTestUtil.randomLong();
				id = RandomTestUtil.randomLong();
				workflowDefinitionName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				workflowDefinitionVersion = RandomTestUtil.randomInt();
			}
		};
	}

	protected WorkflowDefinitionLink randomIrrelevantWorkflowDefinitionLink()
		throws Exception {

		WorkflowDefinitionLink randomIrrelevantWorkflowDefinitionLink =
			randomWorkflowDefinitionLink();

		return randomIrrelevantWorkflowDefinitionLink;
	}

	protected WorkflowDefinitionLink randomPatchWorkflowDefinitionLink()
		throws Exception {

		return randomWorkflowDefinitionLink();
	}

	protected WorkflowDefinitionLinkResource workflowDefinitionLinkResource;
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
		LogFactoryUtil.getLog(BaseWorkflowDefinitionLinkResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.admin.workflow.resource.v1_0.
		WorkflowDefinitionLinkResource _workflowDefinitionLinkResource;

}