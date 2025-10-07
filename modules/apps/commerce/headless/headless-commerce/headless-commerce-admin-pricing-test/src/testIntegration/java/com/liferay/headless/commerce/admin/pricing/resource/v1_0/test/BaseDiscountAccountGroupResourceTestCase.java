/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.resource.v1_0.test;

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
import com.liferay.headless.commerce.admin.pricing.client.dto.v1_0.DiscountAccountGroup;
import com.liferay.headless.commerce.admin.pricing.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Page;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.pricing.client.resource.v1_0.DiscountAccountGroupResource;
import com.liferay.headless.commerce.admin.pricing.client.serdes.v1_0.DiscountAccountGroupSerDes;
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
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public abstract class BaseDiscountAccountGroupResourceTestCase {

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

		_discountAccountGroupResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		discountAccountGroupResource = DiscountAccountGroupResource.builder(
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

		DiscountAccountGroup discountAccountGroup1 =
			randomDiscountAccountGroup();

		String json = objectMapper.writeValueAsString(discountAccountGroup1);

		DiscountAccountGroup discountAccountGroup2 =
			DiscountAccountGroupSerDes.toDTO(json);

		Assert.assertTrue(equals(discountAccountGroup1, discountAccountGroup2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		DiscountAccountGroup discountAccountGroup =
			randomDiscountAccountGroup();

		String json1 = objectMapper.writeValueAsString(discountAccountGroup);
		String json2 = DiscountAccountGroupSerDes.toJSON(discountAccountGroup);

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

		DiscountAccountGroup discountAccountGroup =
			randomDiscountAccountGroup();

		discountAccountGroup.setAccountGroupExternalReferenceCode(regex);
		discountAccountGroup.setDiscountExternalReferenceCode(regex);

		String json = DiscountAccountGroupSerDes.toJSON(discountAccountGroup);

		Assert.assertFalse(json.contains(regex));

		discountAccountGroup = DiscountAccountGroupSerDes.toDTO(json);

		Assert.assertEquals(
			regex, discountAccountGroup.getAccountGroupExternalReferenceCode());
		Assert.assertEquals(
			regex, discountAccountGroup.getDiscountExternalReferenceCode());
	}

	@Test
	public void testDeleteDiscountAccountGroup() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		DiscountAccountGroup discountAccountGroup =
			testDeleteDiscountAccountGroup_addDiscountAccountGroup();

		assertHttpResponseStatusCode(
			204,
			discountAccountGroupResource.deleteDiscountAccountGroupHttpResponse(
				discountAccountGroup.getId()));
	}

	protected DiscountAccountGroup
			testDeleteDiscountAccountGroup_addDiscountAccountGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteDiscountAccountGroup() throws Exception {

		// No namespace

		DiscountAccountGroup discountAccountGroup1 =
			testGraphQLDeleteDiscountAccountGroup_addDiscountAccountGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteDiscountAccountGroup",
						new HashMap<String, Object>() {
							{
								put("id", discountAccountGroup1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteDiscountAccountGroup"));

		// Using the namespace headlessCommerceAdminPricing_v1_0

		DiscountAccountGroup discountAccountGroup2 =
			testGraphQLDeleteDiscountAccountGroup_addDiscountAccountGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminPricing_v1_0",
						new GraphQLField(
							"deleteDiscountAccountGroup",
							new HashMap<String, Object>() {
								{
									put("id", discountAccountGroup2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminPricing_v1_0",
				"Object/deleteDiscountAccountGroup"));
	}

	protected DiscountAccountGroup
			testGraphQLDeleteDiscountAccountGroup_addDiscountAccountGroup()
		throws Exception {

		return testGraphQLDiscountAccountGroup_addDiscountAccountGroup();
	}

	@Test
	public void testDeleteDiscountAccountGroupBatch() throws Exception {
		DiscountAccountGroup discountAccountGroup1 =
			testDeleteDiscountAccountGroupBatch_addDiscountAccountGroup();

		testDeleteDiscountAccountGroupBatch_deleteDiscountAccountGroup(
			202, null, discountAccountGroup1.getId());
	}

	protected DiscountAccountGroup
			testDeleteDiscountAccountGroupBatch_addDiscountAccountGroup()
		throws Exception {

		return testDeleteDiscountAccountGroup_addDiscountAccountGroup();
	}

	protected void
			testDeleteDiscountAccountGroupBatch_deleteDiscountAccountGroup(
				int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			discountAccountGroupResource.
				deleteDiscountAccountGroupBatchHttpResponse(
					null,
					JSONUtil.putAll(
						JSONUtil.put(
							"externalReferenceCode", () -> externalReferenceCode
						).put(
							"id", () -> id
						)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetDiscountByExternalReferenceCodeDiscountAccountGroupsPage()
		throws Exception {

		String externalReferenceCode =
			testGetDiscountByExternalReferenceCodeDiscountAccountGroupsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetDiscountByExternalReferenceCodeDiscountAccountGroupsPage_getIrrelevantExternalReferenceCode();

		Page<DiscountAccountGroup> page =
			discountAccountGroupResource.
				getDiscountByExternalReferenceCodeDiscountAccountGroupsPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			DiscountAccountGroup irrelevantDiscountAccountGroup =
				testGetDiscountByExternalReferenceCodeDiscountAccountGroupsPage_addDiscountAccountGroup(
					irrelevantExternalReferenceCode,
					randomIrrelevantDiscountAccountGroup());

			page =
				discountAccountGroupResource.
					getDiscountByExternalReferenceCodeDiscountAccountGroupsPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDiscountAccountGroup,
				(List<DiscountAccountGroup>)page.getItems());
			assertValid(
				page,
				testGetDiscountByExternalReferenceCodeDiscountAccountGroupsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		DiscountAccountGroup discountAccountGroup1 =
			testGetDiscountByExternalReferenceCodeDiscountAccountGroupsPage_addDiscountAccountGroup(
				externalReferenceCode, randomDiscountAccountGroup());

		DiscountAccountGroup discountAccountGroup2 =
			testGetDiscountByExternalReferenceCodeDiscountAccountGroupsPage_addDiscountAccountGroup(
				externalReferenceCode, randomDiscountAccountGroup());

		page =
			discountAccountGroupResource.
				getDiscountByExternalReferenceCodeDiscountAccountGroupsPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			discountAccountGroup1, (List<DiscountAccountGroup>)page.getItems());
		assertContains(
			discountAccountGroup2, (List<DiscountAccountGroup>)page.getItems());
		assertValid(
			page,
			testGetDiscountByExternalReferenceCodeDiscountAccountGroupsPage_getExpectedActions(
				externalReferenceCode));

		discountAccountGroupResource.deleteDiscountAccountGroup(
			discountAccountGroup1.getId());

		discountAccountGroupResource.deleteDiscountAccountGroup(
			discountAccountGroup2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetDiscountByExternalReferenceCodeDiscountAccountGroupsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetDiscountByExternalReferenceCodeDiscountAccountGroupsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetDiscountByExternalReferenceCodeDiscountAccountGroupsPage_getExternalReferenceCode();

		Page<DiscountAccountGroup> discountAccountGroupsPage =
			discountAccountGroupResource.
				getDiscountByExternalReferenceCodeDiscountAccountGroupsPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			discountAccountGroupsPage.getTotalCount());

		DiscountAccountGroup discountAccountGroup1 =
			testGetDiscountByExternalReferenceCodeDiscountAccountGroupsPage_addDiscountAccountGroup(
				externalReferenceCode, randomDiscountAccountGroup());

		DiscountAccountGroup discountAccountGroup2 =
			testGetDiscountByExternalReferenceCodeDiscountAccountGroupsPage_addDiscountAccountGroup(
				externalReferenceCode, randomDiscountAccountGroup());

		DiscountAccountGroup discountAccountGroup3 =
			testGetDiscountByExternalReferenceCodeDiscountAccountGroupsPage_addDiscountAccountGroup(
				externalReferenceCode, randomDiscountAccountGroup());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<DiscountAccountGroup> page1 =
				discountAccountGroupResource.
					getDiscountByExternalReferenceCodeDiscountAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				discountAccountGroup1,
				(List<DiscountAccountGroup>)page1.getItems());

			Page<DiscountAccountGroup> page2 =
				discountAccountGroupResource.
					getDiscountByExternalReferenceCodeDiscountAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				discountAccountGroup2,
				(List<DiscountAccountGroup>)page2.getItems());

			Page<DiscountAccountGroup> page3 =
				discountAccountGroupResource.
					getDiscountByExternalReferenceCodeDiscountAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				discountAccountGroup3,
				(List<DiscountAccountGroup>)page3.getItems());
		}
		else {
			Page<DiscountAccountGroup> page1 =
				discountAccountGroupResource.
					getDiscountByExternalReferenceCodeDiscountAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<DiscountAccountGroup> discountAccountGroups1 =
				(List<DiscountAccountGroup>)page1.getItems();

			Assert.assertEquals(
				discountAccountGroups1.toString(), totalCount + 2,
				discountAccountGroups1.size());

			Page<DiscountAccountGroup> page2 =
				discountAccountGroupResource.
					getDiscountByExternalReferenceCodeDiscountAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<DiscountAccountGroup> discountAccountGroups2 =
				(List<DiscountAccountGroup>)page2.getItems();

			Assert.assertEquals(
				discountAccountGroups2.toString(), 1,
				discountAccountGroups2.size());

			Page<DiscountAccountGroup> page3 =
				discountAccountGroupResource.
					getDiscountByExternalReferenceCodeDiscountAccountGroupsPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				discountAccountGroup1,
				(List<DiscountAccountGroup>)page3.getItems());
			assertContains(
				discountAccountGroup2,
				(List<DiscountAccountGroup>)page3.getItems());
			assertContains(
				discountAccountGroup3,
				(List<DiscountAccountGroup>)page3.getItems());
		}
	}

	protected DiscountAccountGroup
			testGetDiscountByExternalReferenceCodeDiscountAccountGroupsPage_addDiscountAccountGroup(
				String externalReferenceCode,
				DiscountAccountGroup discountAccountGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetDiscountByExternalReferenceCodeDiscountAccountGroupsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetDiscountByExternalReferenceCodeDiscountAccountGroupsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetDiscountIdDiscountAccountGroupsPage() throws Exception {
		Long id = testGetDiscountIdDiscountAccountGroupsPage_getId();
		Long irrelevantId =
			testGetDiscountIdDiscountAccountGroupsPage_getIrrelevantId();

		Page<DiscountAccountGroup> page =
			discountAccountGroupResource.getDiscountIdDiscountAccountGroupsPage(
				id, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			DiscountAccountGroup irrelevantDiscountAccountGroup =
				testGetDiscountIdDiscountAccountGroupsPage_addDiscountAccountGroup(
					irrelevantId, randomIrrelevantDiscountAccountGroup());

			page =
				discountAccountGroupResource.
					getDiscountIdDiscountAccountGroupsPage(
						irrelevantId, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDiscountAccountGroup,
				(List<DiscountAccountGroup>)page.getItems());
			assertValid(
				page,
				testGetDiscountIdDiscountAccountGroupsPage_getExpectedActions(
					irrelevantId));
		}

		DiscountAccountGroup discountAccountGroup1 =
			testGetDiscountIdDiscountAccountGroupsPage_addDiscountAccountGroup(
				id, randomDiscountAccountGroup());

		DiscountAccountGroup discountAccountGroup2 =
			testGetDiscountIdDiscountAccountGroupsPage_addDiscountAccountGroup(
				id, randomDiscountAccountGroup());

		page =
			discountAccountGroupResource.getDiscountIdDiscountAccountGroupsPage(
				id, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			discountAccountGroup1, (List<DiscountAccountGroup>)page.getItems());
		assertContains(
			discountAccountGroup2, (List<DiscountAccountGroup>)page.getItems());
		assertValid(
			page,
			testGetDiscountIdDiscountAccountGroupsPage_getExpectedActions(id));

		discountAccountGroupResource.deleteDiscountAccountGroup(
			discountAccountGroup1.getId());

		discountAccountGroupResource.deleteDiscountAccountGroup(
			discountAccountGroup2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetDiscountIdDiscountAccountGroupsPage_getExpectedActions(
				Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetDiscountIdDiscountAccountGroupsPageWithPagination()
		throws Exception {

		Long id = testGetDiscountIdDiscountAccountGroupsPage_getId();

		Page<DiscountAccountGroup> discountAccountGroupsPage =
			discountAccountGroupResource.getDiscountIdDiscountAccountGroupsPage(
				id, null);

		int totalCount = GetterUtil.getInteger(
			discountAccountGroupsPage.getTotalCount());

		DiscountAccountGroup discountAccountGroup1 =
			testGetDiscountIdDiscountAccountGroupsPage_addDiscountAccountGroup(
				id, randomDiscountAccountGroup());

		DiscountAccountGroup discountAccountGroup2 =
			testGetDiscountIdDiscountAccountGroupsPage_addDiscountAccountGroup(
				id, randomDiscountAccountGroup());

		DiscountAccountGroup discountAccountGroup3 =
			testGetDiscountIdDiscountAccountGroupsPage_addDiscountAccountGroup(
				id, randomDiscountAccountGroup());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<DiscountAccountGroup> page1 =
				discountAccountGroupResource.
					getDiscountIdDiscountAccountGroupsPage(
						id,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				discountAccountGroup1,
				(List<DiscountAccountGroup>)page1.getItems());

			Page<DiscountAccountGroup> page2 =
				discountAccountGroupResource.
					getDiscountIdDiscountAccountGroupsPage(
						id,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				discountAccountGroup2,
				(List<DiscountAccountGroup>)page2.getItems());

			Page<DiscountAccountGroup> page3 =
				discountAccountGroupResource.
					getDiscountIdDiscountAccountGroupsPage(
						id,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				discountAccountGroup3,
				(List<DiscountAccountGroup>)page3.getItems());
		}
		else {
			Page<DiscountAccountGroup> page1 =
				discountAccountGroupResource.
					getDiscountIdDiscountAccountGroupsPage(
						id, Pagination.of(1, totalCount + 2));

			List<DiscountAccountGroup> discountAccountGroups1 =
				(List<DiscountAccountGroup>)page1.getItems();

			Assert.assertEquals(
				discountAccountGroups1.toString(), totalCount + 2,
				discountAccountGroups1.size());

			Page<DiscountAccountGroup> page2 =
				discountAccountGroupResource.
					getDiscountIdDiscountAccountGroupsPage(
						id, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<DiscountAccountGroup> discountAccountGroups2 =
				(List<DiscountAccountGroup>)page2.getItems();

			Assert.assertEquals(
				discountAccountGroups2.toString(), 1,
				discountAccountGroups2.size());

			Page<DiscountAccountGroup> page3 =
				discountAccountGroupResource.
					getDiscountIdDiscountAccountGroupsPage(
						id, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				discountAccountGroup1,
				(List<DiscountAccountGroup>)page3.getItems());
			assertContains(
				discountAccountGroup2,
				(List<DiscountAccountGroup>)page3.getItems());
			assertContains(
				discountAccountGroup3,
				(List<DiscountAccountGroup>)page3.getItems());
		}
	}

	protected DiscountAccountGroup
			testGetDiscountIdDiscountAccountGroupsPage_addDiscountAccountGroup(
				Long id, DiscountAccountGroup discountAccountGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetDiscountIdDiscountAccountGroupsPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetDiscountIdDiscountAccountGroupsPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostDiscountByExternalReferenceCodeDiscountAccountGroup()
		throws Exception {

		DiscountAccountGroup randomDiscountAccountGroup =
			randomDiscountAccountGroup();

		DiscountAccountGroup postDiscountAccountGroup =
			testPostDiscountByExternalReferenceCodeDiscountAccountGroup_addDiscountAccountGroup(
				randomDiscountAccountGroup);

		assertEquals(randomDiscountAccountGroup, postDiscountAccountGroup);
		assertValid(postDiscountAccountGroup);
	}

	protected DiscountAccountGroup
			testPostDiscountByExternalReferenceCodeDiscountAccountGroup_addDiscountAccountGroup(
				DiscountAccountGroup discountAccountGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostDiscountIdDiscountAccountGroup() throws Exception {
		DiscountAccountGroup randomDiscountAccountGroup =
			randomDiscountAccountGroup();

		DiscountAccountGroup postDiscountAccountGroup =
			testPostDiscountIdDiscountAccountGroup_addDiscountAccountGroup(
				randomDiscountAccountGroup);

		assertEquals(randomDiscountAccountGroup, postDiscountAccountGroup);
		assertValid(postDiscountAccountGroup);
	}

	protected DiscountAccountGroup
			testPostDiscountIdDiscountAccountGroup_addDiscountAccountGroup(
				DiscountAccountGroup discountAccountGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		DiscountAccountGroup discountAccountGroup1 =
			testBatchEngineDeleteImportTask_addDiscountAccountGroup();

		testBatchEngineDeleteImportTask_deleteDiscountAccountGroup(
			200, null, discountAccountGroup1.getId());
	}

	protected DiscountAccountGroup
			testBatchEngineDeleteImportTask_addDiscountAccountGroup()
		throws Exception {

		return testDeleteDiscountAccountGroup_addDiscountAccountGroup();
	}

	protected void testBatchEngineDeleteImportTask_deleteDiscountAccountGroup(
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
				"com.liferay.headless.commerce.admin.pricing.dto.v1_0.DiscountAccountGroup",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"id", () -> id
					)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		if (expectedStatusCode == 200) {
			waitForFinish(
				"COMPLETED",
				JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
		}
	}

	protected DiscountAccountGroup
			testGraphQLDiscountAccountGroup_addDiscountAccountGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		DiscountAccountGroup discountAccountGroup,
		List<DiscountAccountGroup> discountAccountGroups) {

		boolean contains = false;

		for (DiscountAccountGroup item : discountAccountGroups) {
			if (equals(discountAccountGroup, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			discountAccountGroups + " does not contain " + discountAccountGroup,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		DiscountAccountGroup discountAccountGroup1,
		DiscountAccountGroup discountAccountGroup2) {

		Assert.assertTrue(
			discountAccountGroup1 + " does not equal " + discountAccountGroup2,
			equals(discountAccountGroup1, discountAccountGroup2));
	}

	protected void assertEquals(
		List<DiscountAccountGroup> discountAccountGroups1,
		List<DiscountAccountGroup> discountAccountGroups2) {

		Assert.assertEquals(
			discountAccountGroups1.size(), discountAccountGroups2.size());

		for (int i = 0; i < discountAccountGroups1.size(); i++) {
			DiscountAccountGroup discountAccountGroup1 =
				discountAccountGroups1.get(i);
			DiscountAccountGroup discountAccountGroup2 =
				discountAccountGroups2.get(i);

			assertEquals(discountAccountGroup1, discountAccountGroup2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<DiscountAccountGroup> discountAccountGroups1,
		List<DiscountAccountGroup> discountAccountGroups2) {

		Assert.assertEquals(
			discountAccountGroups1.size(), discountAccountGroups2.size());

		for (DiscountAccountGroup discountAccountGroup1 :
				discountAccountGroups1) {

			boolean contains = false;

			for (DiscountAccountGroup discountAccountGroup2 :
					discountAccountGroups2) {

				if (equals(discountAccountGroup1, discountAccountGroup2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				discountAccountGroups2 + " does not contain " +
					discountAccountGroup1,
				contains);
		}
	}

	protected void assertValid(DiscountAccountGroup discountAccountGroup)
		throws Exception {

		boolean valid = true;

		if (discountAccountGroup.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"accountGroupExternalReferenceCode",
					additionalAssertFieldName)) {

				if (discountAccountGroup.
						getAccountGroupExternalReferenceCode() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("accountGroupId", additionalAssertFieldName)) {
				if (discountAccountGroup.getAccountGroupId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"discountExternalReferenceCode",
					additionalAssertFieldName)) {

				if (discountAccountGroup.getDiscountExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("discountId", additionalAssertFieldName)) {
				if (discountAccountGroup.getDiscountId() == null) {
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

	protected void assertValid(Page<DiscountAccountGroup> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<DiscountAccountGroup> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<DiscountAccountGroup> discountAccountGroups =
			page.getItems();

		int size = discountAccountGroups.size();

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
					com.liferay.headless.commerce.admin.pricing.dto.v1_0.
						DiscountAccountGroup.class)) {

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
		DiscountAccountGroup discountAccountGroup1,
		DiscountAccountGroup discountAccountGroup2) {

		if (discountAccountGroup1 == discountAccountGroup2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"accountGroupExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						discountAccountGroup1.
							getAccountGroupExternalReferenceCode(),
						discountAccountGroup2.
							getAccountGroupExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("accountGroupId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discountAccountGroup1.getAccountGroupId(),
						discountAccountGroup2.getAccountGroupId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"discountExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						discountAccountGroup1.
							getDiscountExternalReferenceCode(),
						discountAccountGroup2.
							getDiscountExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("discountId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discountAccountGroup1.getDiscountId(),
						discountAccountGroup2.getDiscountId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						discountAccountGroup1.getId(),
						discountAccountGroup2.getId())) {

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

		if (!(_discountAccountGroupResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_discountAccountGroupResource;

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
		DiscountAccountGroup discountAccountGroup) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("accountGroupExternalReferenceCode")) {
			Object object =
				discountAccountGroup.getAccountGroupExternalReferenceCode();

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

		if (entityFieldName.equals("accountGroupId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountExternalReferenceCode")) {
			Object object =
				discountAccountGroup.getDiscountExternalReferenceCode();

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

		if (entityFieldName.equals("discountId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("id")) {
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

	protected DiscountAccountGroup randomDiscountAccountGroup()
		throws Exception {

		return new DiscountAccountGroup() {
			{
				accountGroupExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				accountGroupId = RandomTestUtil.randomLong();
				discountExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				discountId = RandomTestUtil.randomLong();
				id = RandomTestUtil.randomLong();
			}
		};
	}

	protected DiscountAccountGroup randomIrrelevantDiscountAccountGroup()
		throws Exception {

		DiscountAccountGroup randomIrrelevantDiscountAccountGroup =
			randomDiscountAccountGroup();

		return randomIrrelevantDiscountAccountGroup;
	}

	protected DiscountAccountGroup randomPatchDiscountAccountGroup()
		throws Exception {

		return randomDiscountAccountGroup();
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

	protected DiscountAccountGroupResource discountAccountGroupResource;
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
		LogFactoryUtil.getLog(BaseDiscountAccountGroupResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.pricing.resource.v1_0.
		DiscountAccountGroupResource _discountAccountGroupResource;

}