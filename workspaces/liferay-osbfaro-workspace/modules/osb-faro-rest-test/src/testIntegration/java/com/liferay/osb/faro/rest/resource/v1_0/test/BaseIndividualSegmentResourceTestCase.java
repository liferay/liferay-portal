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

import com.liferay.osb.faro.rest.client.dto.v1_0.IndividualSegment;
import com.liferay.osb.faro.rest.client.http.HttpInvoker;
import com.liferay.osb.faro.rest.client.pagination.Page;
import com.liferay.osb.faro.rest.client.pagination.Pagination;
import com.liferay.osb.faro.rest.client.resource.v1_0.IndividualSegmentResource;
import com.liferay.osb.faro.rest.client.serdes.v1_0.IndividualSegmentSerDes;
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
public abstract class BaseIndividualSegmentResourceTestCase {

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

		_individualSegmentResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		individualSegmentResource = IndividualSegmentResource.builder(
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

		IndividualSegment individualSegment1 = randomIndividualSegment();

		String json = objectMapper.writeValueAsString(individualSegment1);

		IndividualSegment individualSegment2 = IndividualSegmentSerDes.toDTO(
			json);

		Assert.assertTrue(equals(individualSegment1, individualSegment2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		IndividualSegment individualSegment = randomIndividualSegment();

		String json1 = objectMapper.writeValueAsString(individualSegment);
		String json2 = IndividualSegmentSerDes.toJSON(individualSegment);

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

		IndividualSegment individualSegment = randomIndividualSegment();

		individualSegment.setChannelId(regex);
		individualSegment.setFilter(regex);
		individualSegment.setId(regex);
		individualSegment.setName(regex);

		String json = IndividualSegmentSerDes.toJSON(individualSegment);

		Assert.assertFalse(json.contains(regex));

		individualSegment = IndividualSegmentSerDes.toDTO(json);

		Assert.assertEquals(regex, individualSegment.getChannelId());
		Assert.assertEquals(regex, individualSegment.getFilter());
		Assert.assertEquals(regex, individualSegment.getId());
		Assert.assertEquals(regex, individualSegment.getName());
	}

	@Test
	public void testGetWorkspaceGroupChannelIndividualSegmentsPage()
		throws Exception {

		Long groupId =
			testGetWorkspaceGroupChannelIndividualSegmentsPage_getGroupId();
		Long irrelevantGroupId =
			testGetWorkspaceGroupChannelIndividualSegmentsPage_getIrrelevantGroupId();
		String channelId =
			testGetWorkspaceGroupChannelIndividualSegmentsPage_getChannelId();
		String irrelevantChannelId =
			testGetWorkspaceGroupChannelIndividualSegmentsPage_getIrrelevantChannelId();

		Page<IndividualSegment> page =
			individualSegmentResource.
				getWorkspaceGroupChannelIndividualSegmentsPage(
					groupId, channelId, RandomTestUtil.randomString(), null,
					RandomTestUtil.randomString(), Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if ((irrelevantGroupId != null) && (irrelevantChannelId != null)) {
			IndividualSegment irrelevantIndividualSegment =
				testGetWorkspaceGroupChannelIndividualSegmentsPage_addIndividualSegment(
					irrelevantGroupId, irrelevantChannelId,
					randomIrrelevantIndividualSegment());

			page =
				individualSegmentResource.
					getWorkspaceGroupChannelIndividualSegmentsPage(
						irrelevantGroupId, irrelevantChannelId, null, null,
						null, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantIndividualSegment,
				(List<IndividualSegment>)page.getItems());
			assertValid(
				page,
				testGetWorkspaceGroupChannelIndividualSegmentsPage_getExpectedActions(
					irrelevantGroupId, irrelevantChannelId));
		}

		IndividualSegment individualSegment1 =
			testGetWorkspaceGroupChannelIndividualSegmentsPage_addIndividualSegment(
				groupId, channelId, randomIndividualSegment());

		IndividualSegment individualSegment2 =
			testGetWorkspaceGroupChannelIndividualSegmentsPage_addIndividualSegment(
				groupId, channelId, randomIndividualSegment());

		page =
			individualSegmentResource.
				getWorkspaceGroupChannelIndividualSegmentsPage(
					groupId, channelId, null, null, null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			individualSegment1, (List<IndividualSegment>)page.getItems());
		assertContains(
			individualSegment2, (List<IndividualSegment>)page.getItems());
		assertValid(
			page,
			testGetWorkspaceGroupChannelIndividualSegmentsPage_getExpectedActions(
				groupId, channelId));
	}

	protected Map<String, Map<String, String>>
			testGetWorkspaceGroupChannelIndividualSegmentsPage_getExpectedActions(
				Long groupId, String channelId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWorkspaceGroupChannelIndividualSegmentsPageWithPagination()
		throws Exception {

		Long groupId =
			testGetWorkspaceGroupChannelIndividualSegmentsPage_getGroupId();
		String channelId =
			testGetWorkspaceGroupChannelIndividualSegmentsPage_getChannelId();

		Page<IndividualSegment> individualSegmentsPage =
			individualSegmentResource.
				getWorkspaceGroupChannelIndividualSegmentsPage(
					groupId, channelId, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			individualSegmentsPage.getTotalCount());

		IndividualSegment individualSegment1 =
			testGetWorkspaceGroupChannelIndividualSegmentsPage_addIndividualSegment(
				groupId, channelId, randomIndividualSegment());

		IndividualSegment individualSegment2 =
			testGetWorkspaceGroupChannelIndividualSegmentsPage_addIndividualSegment(
				groupId, channelId, randomIndividualSegment());

		IndividualSegment individualSegment3 =
			testGetWorkspaceGroupChannelIndividualSegmentsPage_addIndividualSegment(
				groupId, channelId, randomIndividualSegment());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<IndividualSegment> page1 =
				individualSegmentResource.
					getWorkspaceGroupChannelIndividualSegmentsPage(
						groupId, channelId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				individualSegment1, (List<IndividualSegment>)page1.getItems());

			Page<IndividualSegment> page2 =
				individualSegmentResource.
					getWorkspaceGroupChannelIndividualSegmentsPage(
						groupId, channelId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				individualSegment2, (List<IndividualSegment>)page2.getItems());

			Page<IndividualSegment> page3 =
				individualSegmentResource.
					getWorkspaceGroupChannelIndividualSegmentsPage(
						groupId, channelId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				individualSegment3, (List<IndividualSegment>)page3.getItems());
		}
		else {
			Page<IndividualSegment> page1 =
				individualSegmentResource.
					getWorkspaceGroupChannelIndividualSegmentsPage(
						groupId, channelId, null, null, null,
						Pagination.of(1, totalCount + 2));

			List<IndividualSegment> individualSegments1 =
				(List<IndividualSegment>)page1.getItems();

			Assert.assertEquals(
				individualSegments1.toString(), totalCount + 2,
				individualSegments1.size());

			Page<IndividualSegment> page2 =
				individualSegmentResource.
					getWorkspaceGroupChannelIndividualSegmentsPage(
						groupId, channelId, null, null, null,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<IndividualSegment> individualSegments2 =
				(List<IndividualSegment>)page2.getItems();

			Assert.assertEquals(
				individualSegments2.toString(), 1, individualSegments2.size());

			Page<IndividualSegment> page3 =
				individualSegmentResource.
					getWorkspaceGroupChannelIndividualSegmentsPage(
						groupId, channelId, null, null, null,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				individualSegment1, (List<IndividualSegment>)page3.getItems());
			assertContains(
				individualSegment2, (List<IndividualSegment>)page3.getItems());
			assertContains(
				individualSegment3, (List<IndividualSegment>)page3.getItems());
		}
	}

	protected IndividualSegment
			testGetWorkspaceGroupChannelIndividualSegmentsPage_addIndividualSegment(
				Long groupId, String channelId,
				IndividualSegment individualSegment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkspaceGroupChannelIndividualSegmentsPage_getGroupId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkspaceGroupChannelIndividualSegmentsPage_getIrrelevantGroupId()
		throws Exception {

		return null;
	}

	protected String
			testGetWorkspaceGroupChannelIndividualSegmentsPage_getChannelId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetWorkspaceGroupChannelIndividualSegmentsPage_getIrrelevantChannelId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetWorkspaceGroupIndividualIndividualSegmentsPage()
		throws Exception {

		Long groupId =
			testGetWorkspaceGroupIndividualIndividualSegmentsPage_getGroupId();
		Long irrelevantGroupId =
			testGetWorkspaceGroupIndividualIndividualSegmentsPage_getIrrelevantGroupId();
		String individualId =
			testGetWorkspaceGroupIndividualIndividualSegmentsPage_getIndividualId();
		String irrelevantIndividualId =
			testGetWorkspaceGroupIndividualIndividualSegmentsPage_getIrrelevantIndividualId();

		Page<IndividualSegment> page =
			individualSegmentResource.
				getWorkspaceGroupIndividualIndividualSegmentsPage(
					groupId, individualId, RandomTestUtil.randomString(), null,
					RandomTestUtil.randomString(), Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if ((irrelevantGroupId != null) && (irrelevantIndividualId != null)) {
			IndividualSegment irrelevantIndividualSegment =
				testGetWorkspaceGroupIndividualIndividualSegmentsPage_addIndividualSegment(
					irrelevantGroupId, irrelevantIndividualId,
					randomIrrelevantIndividualSegment());

			page =
				individualSegmentResource.
					getWorkspaceGroupIndividualIndividualSegmentsPage(
						irrelevantGroupId, irrelevantIndividualId, null, null,
						null, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantIndividualSegment,
				(List<IndividualSegment>)page.getItems());
			assertValid(
				page,
				testGetWorkspaceGroupIndividualIndividualSegmentsPage_getExpectedActions(
					irrelevantGroupId, irrelevantIndividualId));
		}

		IndividualSegment individualSegment1 =
			testGetWorkspaceGroupIndividualIndividualSegmentsPage_addIndividualSegment(
				groupId, individualId, randomIndividualSegment());

		IndividualSegment individualSegment2 =
			testGetWorkspaceGroupIndividualIndividualSegmentsPage_addIndividualSegment(
				groupId, individualId, randomIndividualSegment());

		page =
			individualSegmentResource.
				getWorkspaceGroupIndividualIndividualSegmentsPage(
					groupId, individualId, null, null, null,
					Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			individualSegment1, (List<IndividualSegment>)page.getItems());
		assertContains(
			individualSegment2, (List<IndividualSegment>)page.getItems());
		assertValid(
			page,
			testGetWorkspaceGroupIndividualIndividualSegmentsPage_getExpectedActions(
				groupId, individualId));
	}

	protected Map<String, Map<String, String>>
			testGetWorkspaceGroupIndividualIndividualSegmentsPage_getExpectedActions(
				Long groupId, String individualId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWorkspaceGroupIndividualIndividualSegmentsPageWithPagination()
		throws Exception {

		Long groupId =
			testGetWorkspaceGroupIndividualIndividualSegmentsPage_getGroupId();
		String individualId =
			testGetWorkspaceGroupIndividualIndividualSegmentsPage_getIndividualId();

		Page<IndividualSegment> individualSegmentsPage =
			individualSegmentResource.
				getWorkspaceGroupIndividualIndividualSegmentsPage(
					groupId, individualId, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			individualSegmentsPage.getTotalCount());

		IndividualSegment individualSegment1 =
			testGetWorkspaceGroupIndividualIndividualSegmentsPage_addIndividualSegment(
				groupId, individualId, randomIndividualSegment());

		IndividualSegment individualSegment2 =
			testGetWorkspaceGroupIndividualIndividualSegmentsPage_addIndividualSegment(
				groupId, individualId, randomIndividualSegment());

		IndividualSegment individualSegment3 =
			testGetWorkspaceGroupIndividualIndividualSegmentsPage_addIndividualSegment(
				groupId, individualId, randomIndividualSegment());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<IndividualSegment> page1 =
				individualSegmentResource.
					getWorkspaceGroupIndividualIndividualSegmentsPage(
						groupId, individualId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				individualSegment1, (List<IndividualSegment>)page1.getItems());

			Page<IndividualSegment> page2 =
				individualSegmentResource.
					getWorkspaceGroupIndividualIndividualSegmentsPage(
						groupId, individualId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				individualSegment2, (List<IndividualSegment>)page2.getItems());

			Page<IndividualSegment> page3 =
				individualSegmentResource.
					getWorkspaceGroupIndividualIndividualSegmentsPage(
						groupId, individualId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				individualSegment3, (List<IndividualSegment>)page3.getItems());
		}
		else {
			Page<IndividualSegment> page1 =
				individualSegmentResource.
					getWorkspaceGroupIndividualIndividualSegmentsPage(
						groupId, individualId, null, null, null,
						Pagination.of(1, totalCount + 2));

			List<IndividualSegment> individualSegments1 =
				(List<IndividualSegment>)page1.getItems();

			Assert.assertEquals(
				individualSegments1.toString(), totalCount + 2,
				individualSegments1.size());

			Page<IndividualSegment> page2 =
				individualSegmentResource.
					getWorkspaceGroupIndividualIndividualSegmentsPage(
						groupId, individualId, null, null, null,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<IndividualSegment> individualSegments2 =
				(List<IndividualSegment>)page2.getItems();

			Assert.assertEquals(
				individualSegments2.toString(), 1, individualSegments2.size());

			Page<IndividualSegment> page3 =
				individualSegmentResource.
					getWorkspaceGroupIndividualIndividualSegmentsPage(
						groupId, individualId, null, null, null,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				individualSegment1, (List<IndividualSegment>)page3.getItems());
			assertContains(
				individualSegment2, (List<IndividualSegment>)page3.getItems());
			assertContains(
				individualSegment3, (List<IndividualSegment>)page3.getItems());
		}
	}

	protected IndividualSegment
			testGetWorkspaceGroupIndividualIndividualSegmentsPage_addIndividualSegment(
				Long groupId, String individualId,
				IndividualSegment individualSegment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkspaceGroupIndividualIndividualSegmentsPage_getGroupId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkspaceGroupIndividualIndividualSegmentsPage_getIrrelevantGroupId()
		throws Exception {

		return null;
	}

	protected String
			testGetWorkspaceGroupIndividualIndividualSegmentsPage_getIndividualId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetWorkspaceGroupIndividualIndividualSegmentsPage_getIrrelevantIndividualId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetWorkspaceGroupIndividualSegment() throws Exception {
		IndividualSegment postIndividualSegment =
			testGetWorkspaceGroupIndividualSegment_addIndividualSegment();

		IndividualSegment getIndividualSegment =
			individualSegmentResource.getWorkspaceGroupIndividualSegment(
				testGetWorkspaceGroupIndividualSegment_getGroupId(),
				postIndividualSegment.getId());

		assertEquals(postIndividualSegment, getIndividualSegment);
		assertValid(getIndividualSegment);
	}

	protected IndividualSegment
			testGetWorkspaceGroupIndividualSegment_addIndividualSegment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetWorkspaceGroupIndividualSegment_getGroupId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetWorkspaceGroupIndividualSegment()
		throws Exception {

		IndividualSegment individualSegment =
			testGraphQLGetWorkspaceGroupIndividualSegment_addIndividualSegment();

		// No namespace

		Assert.assertTrue(
			equals(
				individualSegment,
				IndividualSegmentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"workspaceGroupIndividualSegment",
								new HashMap<String, Object>() {
									{
										put(
											"groupId",
											testGraphQLGetWorkspaceGroupIndividualSegment_getGroupId());
										put(
											"individualSegmentId",
											"\"" + individualSegment.getId() +
												"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/workspaceGroupIndividualSegment"))));

		// Using the namespace faro_v1_0

		Assert.assertTrue(
			equals(
				individualSegment,
				IndividualSegmentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"faro_v1_0",
								new GraphQLField(
									"workspaceGroupIndividualSegment",
									new HashMap<String, Object>() {
										{
											put(
												"groupId",
												testGraphQLGetWorkspaceGroupIndividualSegment_getGroupId());
											put(
												"individualSegmentId",
												"\"" +
													individualSegment.getId() +
														"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/faro_v1_0",
						"Object/workspaceGroupIndividualSegment"))));
	}

	protected Long testGraphQLGetWorkspaceGroupIndividualSegment_getGroupId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetWorkspaceGroupIndividualSegmentNotFound()
		throws Exception {

		Long irrelevantGroupId = RandomTestUtil.randomLong();
		String irrelevantIndividualSegmentId =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"workspaceGroupIndividualSegment",
						new HashMap<String, Object>() {
							{
								put("groupId", irrelevantGroupId);
								put(
									"individualSegmentId",
									irrelevantIndividualSegmentId);
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
							"workspaceGroupIndividualSegment",
							new HashMap<String, Object>() {
								{
									put("groupId", irrelevantGroupId);
									put(
										"individualSegmentId",
										irrelevantIndividualSegmentId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected IndividualSegment
			testGraphQLGetWorkspaceGroupIndividualSegment_addIndividualSegment()
		throws Exception {

		return testGraphQLIndividualSegment_addIndividualSegment();
	}

	protected IndividualSegment
			testGraphQLIndividualSegment_addIndividualSegment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		IndividualSegment individualSegment,
		List<IndividualSegment> individualSegments) {

		boolean contains = false;

		for (IndividualSegment item : individualSegments) {
			if (equals(individualSegment, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			individualSegments + " does not contain " + individualSegment,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		IndividualSegment individualSegment1,
		IndividualSegment individualSegment2) {

		Assert.assertTrue(
			individualSegment1 + " does not equal " + individualSegment2,
			equals(individualSegment1, individualSegment2));
	}

	protected void assertEquals(
		List<IndividualSegment> individualSegments1,
		List<IndividualSegment> individualSegments2) {

		Assert.assertEquals(
			individualSegments1.size(), individualSegments2.size());

		for (int i = 0; i < individualSegments1.size(); i++) {
			IndividualSegment individualSegment1 = individualSegments1.get(i);
			IndividualSegment individualSegment2 = individualSegments2.get(i);

			assertEquals(individualSegment1, individualSegment2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<IndividualSegment> individualSegments1,
		List<IndividualSegment> individualSegments2) {

		Assert.assertEquals(
			individualSegments1.size(), individualSegments2.size());

		for (IndividualSegment individualSegment1 : individualSegments1) {
			boolean contains = false;

			for (IndividualSegment individualSegment2 : individualSegments2) {
				if (equals(individualSegment1, individualSegment2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				individualSegments2 + " does not contain " + individualSegment1,
				contains);
		}
	}

	protected void assertValid(IndividualSegment individualSegment)
		throws Exception {

		boolean valid = true;

		if (individualSegment.getDateCreated() == null) {
			valid = false;
		}

		if (individualSegment.getDateModified() == null) {
			valid = false;
		}

		if (individualSegment.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"activeIndividualCount", additionalAssertFieldName)) {

				if (individualSegment.getActiveIndividualCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"anonymousIndividualCount", additionalAssertFieldName)) {

				if (individualSegment.getAnonymousIndividualCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("channelId", additionalAssertFieldName)) {
				if (individualSegment.getChannelId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("filter", additionalAssertFieldName)) {
				if (individualSegment.getFilter() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"includeAnonymousUsers", additionalAssertFieldName)) {

				if (individualSegment.getIncludeAnonymousUsers() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("individualCount", additionalAssertFieldName)) {
				if (individualSegment.getIndividualCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"knownIndividualCount", additionalAssertFieldName)) {

				if (individualSegment.getKnownIndividualCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("lastActivityDate", additionalAssertFieldName)) {
				if (individualSegment.getLastActivityDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (individualSegment.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("segmentType", additionalAssertFieldName)) {
				if (individualSegment.getSegmentType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("state", additionalAssertFieldName)) {
				if (individualSegment.getState() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (individualSegment.getStatus() == null) {
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

	protected void assertValid(Page<IndividualSegment> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<IndividualSegment> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<IndividualSegment> individualSegments =
			page.getItems();

		int size = individualSegments.size();

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
					com.liferay.osb.faro.rest.dto.v1_0.IndividualSegment.
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
		IndividualSegment individualSegment1,
		IndividualSegment individualSegment2) {

		if (individualSegment1 == individualSegment2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"activeIndividualCount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						individualSegment1.getActiveIndividualCount(),
						individualSegment2.getActiveIndividualCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"anonymousIndividualCount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						individualSegment1.getAnonymousIndividualCount(),
						individualSegment2.getAnonymousIndividualCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("channelId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individualSegment1.getChannelId(),
						individualSegment2.getChannelId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individualSegment1.getDateCreated(),
						individualSegment2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individualSegment1.getDateModified(),
						individualSegment2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("filter", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individualSegment1.getFilter(),
						individualSegment2.getFilter())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individualSegment1.getId(),
						individualSegment2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"includeAnonymousUsers", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						individualSegment1.getIncludeAnonymousUsers(),
						individualSegment2.getIncludeAnonymousUsers())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("individualCount", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individualSegment1.getIndividualCount(),
						individualSegment2.getIndividualCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"knownIndividualCount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						individualSegment1.getKnownIndividualCount(),
						individualSegment2.getKnownIndividualCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("lastActivityDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individualSegment1.getLastActivityDate(),
						individualSegment2.getLastActivityDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individualSegment1.getName(),
						individualSegment2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("segmentType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individualSegment1.getSegmentType(),
						individualSegment2.getSegmentType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("state", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individualSegment1.getState(),
						individualSegment2.getState())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individualSegment1.getStatus(),
						individualSegment2.getStatus())) {

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

		if (!(_individualSegmentResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_individualSegmentResource;

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
		IndividualSegment individualSegment) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("activeIndividualCount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("anonymousIndividualCount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("channelId")) {
			Object object = individualSegment.getChannelId();

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
				Date date = individualSegment.getDateCreated();

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

				sb.append(_format.format(individualSegment.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = individualSegment.getDateModified();

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

				sb.append(_format.format(individualSegment.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("filter")) {
			Object object = individualSegment.getFilter();

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
			Object object = individualSegment.getId();

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

		if (entityFieldName.equals("includeAnonymousUsers")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("individualCount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("knownIndividualCount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("lastActivityDate")) {
			if (operator.equals("between")) {
				Date date = individualSegment.getLastActivityDate();

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
					_format.format(individualSegment.getLastActivityDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("name")) {
			Object object = individualSegment.getName();

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

		if (entityFieldName.equals("segmentType")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("state")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("status")) {
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

	protected IndividualSegment randomIndividualSegment() throws Exception {
		return new IndividualSegment() {
			{
				activeIndividualCount = RandomTestUtil.randomLong();
				anonymousIndividualCount = RandomTestUtil.randomLong();
				channelId = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				filter = StringUtil.toLowerCase(RandomTestUtil.randomString());
				id = StringUtil.toLowerCase(RandomTestUtil.randomString());
				includeAnonymousUsers = RandomTestUtil.randomBoolean();
				individualCount = RandomTestUtil.randomLong();
				knownIndividualCount = RandomTestUtil.randomLong();
				lastActivityDate = RandomTestUtil.nextDate();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected IndividualSegment randomIrrelevantIndividualSegment()
		throws Exception {

		IndividualSegment randomIrrelevantIndividualSegment =
			randomIndividualSegment();

		return randomIrrelevantIndividualSegment;
	}

	protected IndividualSegment randomPatchIndividualSegment()
		throws Exception {

		return randomIndividualSegment();
	}

	protected IndividualSegmentResource individualSegmentResource;
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
		LogFactoryUtil.getLog(BaseIndividualSegmentResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.osb.faro.rest.resource.v1_0.IndividualSegmentResource
		_individualSegmentResource;

}
// LIFERAY-REST-BUILDER-HASH:1855285172