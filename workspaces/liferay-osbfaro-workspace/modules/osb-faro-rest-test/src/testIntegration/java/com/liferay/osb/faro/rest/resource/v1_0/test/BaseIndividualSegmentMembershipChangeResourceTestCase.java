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

import com.liferay.osb.faro.rest.client.dto.v1_0.IndividualSegmentMembershipChange;
import com.liferay.osb.faro.rest.client.http.HttpInvoker;
import com.liferay.osb.faro.rest.client.pagination.Page;
import com.liferay.osb.faro.rest.client.pagination.Pagination;
import com.liferay.osb.faro.rest.client.resource.v1_0.IndividualSegmentMembershipChangeResource;
import com.liferay.osb.faro.rest.client.serdes.v1_0.IndividualSegmentMembershipChangeSerDes;
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
public abstract class BaseIndividualSegmentMembershipChangeResourceTestCase {

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

		_individualSegmentMembershipChangeResource.setContextCompany(
			testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		individualSegmentMembershipChangeResource =
			IndividualSegmentMembershipChangeResource.builder(
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

		IndividualSegmentMembershipChange individualSegmentMembershipChange1 =
			randomIndividualSegmentMembershipChange();

		String json = objectMapper.writeValueAsString(
			individualSegmentMembershipChange1);

		IndividualSegmentMembershipChange individualSegmentMembershipChange2 =
			IndividualSegmentMembershipChangeSerDes.toDTO(json);

		Assert.assertTrue(
			equals(
				individualSegmentMembershipChange1,
				individualSegmentMembershipChange2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		IndividualSegmentMembershipChange individualSegmentMembershipChange =
			randomIndividualSegmentMembershipChange();

		String json1 = objectMapper.writeValueAsString(
			individualSegmentMembershipChange);
		String json2 = IndividualSegmentMembershipChangeSerDes.toJSON(
			individualSegmentMembershipChange);

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

		IndividualSegmentMembershipChange individualSegmentMembershipChange =
			randomIndividualSegmentMembershipChange();

		individualSegmentMembershipChange.setId(regex);
		individualSegmentMembershipChange.setIndividualEmail(regex);
		individualSegmentMembershipChange.setIndividualId(regex);
		individualSegmentMembershipChange.setIndividualName(regex);
		individualSegmentMembershipChange.setIndividualSegmentId(regex);
		individualSegmentMembershipChange.setOperation(regex);

		String json = IndividualSegmentMembershipChangeSerDes.toJSON(
			individualSegmentMembershipChange);

		Assert.assertFalse(json.contains(regex));

		individualSegmentMembershipChange =
			IndividualSegmentMembershipChangeSerDes.toDTO(json);

		Assert.assertEquals(regex, individualSegmentMembershipChange.getId());
		Assert.assertEquals(
			regex, individualSegmentMembershipChange.getIndividualEmail());
		Assert.assertEquals(
			regex, individualSegmentMembershipChange.getIndividualId());
		Assert.assertEquals(
			regex, individualSegmentMembershipChange.getIndividualName());
		Assert.assertEquals(
			regex, individualSegmentMembershipChange.getIndividualSegmentId());
		Assert.assertEquals(
			regex, individualSegmentMembershipChange.getOperation());
	}

	@Test
	public void testGetWorkspaceGroupIndividualSegmentMembershipChangesPage()
		throws Exception {

		Long groupId =
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPage_getGroupId();
		Long irrelevantGroupId =
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPage_getIrrelevantGroupId();
		String individualSegmentId =
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPage_getIndividualSegmentId();
		String irrelevantIndividualSegmentId =
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPage_getIrrelevantIndividualSegmentId();

		Page<IndividualSegmentMembershipChange> page =
			individualSegmentMembershipChangeResource.
				getWorkspaceGroupIndividualSegmentMembershipChangesPage(
					groupId, individualSegmentId, RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), null, Pagination.of(1, 10),
					null);

		long totalCount = page.getTotalCount();

		if ((irrelevantGroupId != null) &&
			(irrelevantIndividualSegmentId != null)) {

			IndividualSegmentMembershipChange
				irrelevantIndividualSegmentMembershipChange =
					testGetWorkspaceGroupIndividualSegmentMembershipChangesPage_addIndividualSegmentMembershipChange(
						irrelevantGroupId, irrelevantIndividualSegmentId,
						randomIrrelevantIndividualSegmentMembershipChange());

			page =
				individualSegmentMembershipChangeResource.
					getWorkspaceGroupIndividualSegmentMembershipChangesPage(
						irrelevantGroupId, irrelevantIndividualSegmentId, null,
						null, null, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantIndividualSegmentMembershipChange,
				(List<IndividualSegmentMembershipChange>)page.getItems());
			assertValid(
				page,
				testGetWorkspaceGroupIndividualSegmentMembershipChangesPage_getExpectedActions(
					irrelevantGroupId, irrelevantIndividualSegmentId));
		}

		IndividualSegmentMembershipChange individualSegmentMembershipChange1 =
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPage_addIndividualSegmentMembershipChange(
				groupId, individualSegmentId,
				randomIndividualSegmentMembershipChange());

		IndividualSegmentMembershipChange individualSegmentMembershipChange2 =
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPage_addIndividualSegmentMembershipChange(
				groupId, individualSegmentId,
				randomIndividualSegmentMembershipChange());

		page =
			individualSegmentMembershipChangeResource.
				getWorkspaceGroupIndividualSegmentMembershipChangesPage(
					groupId, individualSegmentId, null, null, null, null, null,
					Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			individualSegmentMembershipChange1,
			(List<IndividualSegmentMembershipChange>)page.getItems());
		assertContains(
			individualSegmentMembershipChange2,
			(List<IndividualSegmentMembershipChange>)page.getItems());
		assertValid(
			page,
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPage_getExpectedActions(
				groupId, individualSegmentId));
	}

	protected Map<String, Map<String, String>>
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPage_getExpectedActions(
				Long groupId, String individualSegmentId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithPagination()
		throws Exception {

		Long groupId =
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPage_getGroupId();
		String individualSegmentId =
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPage_getIndividualSegmentId();

		Page<IndividualSegmentMembershipChange>
			individualSegmentMembershipChangesPage =
				individualSegmentMembershipChangeResource.
					getWorkspaceGroupIndividualSegmentMembershipChangesPage(
						groupId, individualSegmentId, null, null, null, null,
						null, null, null);

		int totalCount = GetterUtil.getInteger(
			individualSegmentMembershipChangesPage.getTotalCount());

		IndividualSegmentMembershipChange individualSegmentMembershipChange1 =
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPage_addIndividualSegmentMembershipChange(
				groupId, individualSegmentId,
				randomIndividualSegmentMembershipChange());

		IndividualSegmentMembershipChange individualSegmentMembershipChange2 =
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPage_addIndividualSegmentMembershipChange(
				groupId, individualSegmentId,
				randomIndividualSegmentMembershipChange());

		IndividualSegmentMembershipChange individualSegmentMembershipChange3 =
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPage_addIndividualSegmentMembershipChange(
				groupId, individualSegmentId,
				randomIndividualSegmentMembershipChange());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<IndividualSegmentMembershipChange> page1 =
				individualSegmentMembershipChangeResource.
					getWorkspaceGroupIndividualSegmentMembershipChangesPage(
						groupId, individualSegmentId, null, null, null, null,
						null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				individualSegmentMembershipChange1,
				(List<IndividualSegmentMembershipChange>)page1.getItems());

			Page<IndividualSegmentMembershipChange> page2 =
				individualSegmentMembershipChangeResource.
					getWorkspaceGroupIndividualSegmentMembershipChangesPage(
						groupId, individualSegmentId, null, null, null, null,
						null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				individualSegmentMembershipChange2,
				(List<IndividualSegmentMembershipChange>)page2.getItems());

			Page<IndividualSegmentMembershipChange> page3 =
				individualSegmentMembershipChangeResource.
					getWorkspaceGroupIndividualSegmentMembershipChangesPage(
						groupId, individualSegmentId, null, null, null, null,
						null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				individualSegmentMembershipChange3,
				(List<IndividualSegmentMembershipChange>)page3.getItems());
		}
		else {
			Page<IndividualSegmentMembershipChange> page1 =
				individualSegmentMembershipChangeResource.
					getWorkspaceGroupIndividualSegmentMembershipChangesPage(
						groupId, individualSegmentId, null, null, null, null,
						null, Pagination.of(1, totalCount + 2), null);

			List<IndividualSegmentMembershipChange>
				individualSegmentMembershipChanges1 =
					(List<IndividualSegmentMembershipChange>)page1.getItems();

			Assert.assertEquals(
				individualSegmentMembershipChanges1.toString(), totalCount + 2,
				individualSegmentMembershipChanges1.size());

			Page<IndividualSegmentMembershipChange> page2 =
				individualSegmentMembershipChangeResource.
					getWorkspaceGroupIndividualSegmentMembershipChangesPage(
						groupId, individualSegmentId, null, null, null, null,
						null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<IndividualSegmentMembershipChange>
				individualSegmentMembershipChanges2 =
					(List<IndividualSegmentMembershipChange>)page2.getItems();

			Assert.assertEquals(
				individualSegmentMembershipChanges2.toString(), 1,
				individualSegmentMembershipChanges2.size());

			Page<IndividualSegmentMembershipChange> page3 =
				individualSegmentMembershipChangeResource.
					getWorkspaceGroupIndividualSegmentMembershipChangesPage(
						groupId, individualSegmentId, null, null, null, null,
						null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				individualSegmentMembershipChange1,
				(List<IndividualSegmentMembershipChange>)page3.getItems());
			assertContains(
				individualSegmentMembershipChange2,
				(List<IndividualSegmentMembershipChange>)page3.getItems());
			assertContains(
				individualSegmentMembershipChange3,
				(List<IndividualSegmentMembershipChange>)page3.getItems());
		}
	}

	@Test
	public void testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithSortDateTime()
		throws Exception {

		testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, individualSegmentMembershipChange1,
			 individualSegmentMembershipChange2) -> {

				BeanTestUtil.setProperty(
					individualSegmentMembershipChange1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithSortDouble()
		throws Exception {

		testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, individualSegmentMembershipChange1,
			 individualSegmentMembershipChange2) -> {

				BeanTestUtil.setProperty(
					individualSegmentMembershipChange1, entityField.getName(),
					0.1);
				BeanTestUtil.setProperty(
					individualSegmentMembershipChange2, entityField.getName(),
					0.5);
			});
	}

	@Test
	public void testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithSortInteger()
		throws Exception {

		testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, individualSegmentMembershipChange1,
			 individualSegmentMembershipChange2) -> {

				BeanTestUtil.setProperty(
					individualSegmentMembershipChange1, entityField.getName(),
					0);
				BeanTestUtil.setProperty(
					individualSegmentMembershipChange2, entityField.getName(),
					1);
			});
	}

	@Test
	public void testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithSortString()
		throws Exception {

		testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithSort(
			EntityField.Type.STRING,
			(entityField, individualSegmentMembershipChange1,
			 individualSegmentMembershipChange2) -> {

				Class<?> clazz = individualSegmentMembershipChange1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						individualSegmentMembershipChange1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						individualSegmentMembershipChange2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						individualSegmentMembershipChange1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						individualSegmentMembershipChange2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						individualSegmentMembershipChange1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						individualSegmentMembershipChange2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, IndividualSegmentMembershipChange,
					 IndividualSegmentMembershipChange, Exception>
						unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long groupId =
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPage_getGroupId();
		String individualSegmentId =
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPage_getIndividualSegmentId();

		IndividualSegmentMembershipChange individualSegmentMembershipChange1 =
			randomIndividualSegmentMembershipChange();
		IndividualSegmentMembershipChange individualSegmentMembershipChange2 =
			randomIndividualSegmentMembershipChange();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, individualSegmentMembershipChange1,
				individualSegmentMembershipChange2);
		}

		individualSegmentMembershipChange1 =
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPage_addIndividualSegmentMembershipChange(
				groupId, individualSegmentId,
				individualSegmentMembershipChange1);

		individualSegmentMembershipChange2 =
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPage_addIndividualSegmentMembershipChange(
				groupId, individualSegmentId,
				individualSegmentMembershipChange2);

		Page<IndividualSegmentMembershipChange> page =
			individualSegmentMembershipChangeResource.
				getWorkspaceGroupIndividualSegmentMembershipChangesPage(
					groupId, individualSegmentId, null, null, null, null, null,
					null, null);

		for (EntityField entityField : entityFields) {
			Page<IndividualSegmentMembershipChange> ascPage =
				individualSegmentMembershipChangeResource.
					getWorkspaceGroupIndividualSegmentMembershipChangesPage(
						groupId, individualSegmentId, null, null, null, null,
						null, Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				individualSegmentMembershipChange1,
				(List<IndividualSegmentMembershipChange>)ascPage.getItems());
			assertContains(
				individualSegmentMembershipChange2,
				(List<IndividualSegmentMembershipChange>)ascPage.getItems());

			Page<IndividualSegmentMembershipChange> descPage =
				individualSegmentMembershipChangeResource.
					getWorkspaceGroupIndividualSegmentMembershipChangesPage(
						groupId, individualSegmentId, null, null, null, null,
						null, Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				individualSegmentMembershipChange2,
				(List<IndividualSegmentMembershipChange>)descPage.getItems());
			assertContains(
				individualSegmentMembershipChange1,
				(List<IndividualSegmentMembershipChange>)descPage.getItems());
		}
	}

	protected IndividualSegmentMembershipChange
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPage_addIndividualSegmentMembershipChange(
				Long groupId, String individualSegmentId,
				IndividualSegmentMembershipChange
					individualSegmentMembershipChange)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPage_getGroupId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPage_getIrrelevantGroupId()
		throws Exception {

		return null;
	}

	protected String
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPage_getIndividualSegmentId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetWorkspaceGroupIndividualSegmentMembershipChangesPage_getIrrelevantIndividualSegmentId()
		throws Exception {

		return null;
	}

	protected void assertContains(
		IndividualSegmentMembershipChange individualSegmentMembershipChange,
		List<IndividualSegmentMembershipChange>
			individualSegmentMembershipChanges) {

		boolean contains = false;

		for (IndividualSegmentMembershipChange item :
				individualSegmentMembershipChanges) {

			if (equals(individualSegmentMembershipChange, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			individualSegmentMembershipChanges + " does not contain " +
				individualSegmentMembershipChange,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		IndividualSegmentMembershipChange individualSegmentMembershipChange1,
		IndividualSegmentMembershipChange individualSegmentMembershipChange2) {

		Assert.assertTrue(
			individualSegmentMembershipChange1 + " does not equal " +
				individualSegmentMembershipChange2,
			equals(
				individualSegmentMembershipChange1,
				individualSegmentMembershipChange2));
	}

	protected void assertEquals(
		List<IndividualSegmentMembershipChange>
			individualSegmentMembershipChanges1,
		List<IndividualSegmentMembershipChange>
			individualSegmentMembershipChanges2) {

		Assert.assertEquals(
			individualSegmentMembershipChanges1.size(),
			individualSegmentMembershipChanges2.size());

		for (int i = 0; i < individualSegmentMembershipChanges1.size(); i++) {
			IndividualSegmentMembershipChange
				individualSegmentMembershipChange1 =
					individualSegmentMembershipChanges1.get(i);
			IndividualSegmentMembershipChange
				individualSegmentMembershipChange2 =
					individualSegmentMembershipChanges2.get(i);

			assertEquals(
				individualSegmentMembershipChange1,
				individualSegmentMembershipChange2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<IndividualSegmentMembershipChange>
			individualSegmentMembershipChanges1,
		List<IndividualSegmentMembershipChange>
			individualSegmentMembershipChanges2) {

		Assert.assertEquals(
			individualSegmentMembershipChanges1.size(),
			individualSegmentMembershipChanges2.size());

		for (IndividualSegmentMembershipChange
				individualSegmentMembershipChange1 :
					individualSegmentMembershipChanges1) {

			boolean contains = false;

			for (IndividualSegmentMembershipChange
					individualSegmentMembershipChange2 :
						individualSegmentMembershipChanges2) {

				if (equals(
						individualSegmentMembershipChange1,
						individualSegmentMembershipChange2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				individualSegmentMembershipChanges2 + " does not contain " +
					individualSegmentMembershipChange1,
				contains);
		}
	}

	protected void assertValid(
			IndividualSegmentMembershipChange individualSegmentMembershipChange)
		throws Exception {

		boolean valid = true;

		if (individualSegmentMembershipChange.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("dateChanged", additionalAssertFieldName)) {
				if (individualSegmentMembershipChange.getDateChanged() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("dateFirst", additionalAssertFieldName)) {
				if (individualSegmentMembershipChange.getDateFirst() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("individualEmail", additionalAssertFieldName)) {
				if (individualSegmentMembershipChange.getIndividualEmail() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("individualId", additionalAssertFieldName)) {
				if (individualSegmentMembershipChange.getIndividualId() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("individualName", additionalAssertFieldName)) {
				if (individualSegmentMembershipChange.getIndividualName() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"individualSegmentId", additionalAssertFieldName)) {

				if (individualSegmentMembershipChange.
						getIndividualSegmentId() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("operation", additionalAssertFieldName)) {
				if (individualSegmentMembershipChange.getOperation() == null) {
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

	protected void assertValid(Page<IndividualSegmentMembershipChange> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<IndividualSegmentMembershipChange> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<IndividualSegmentMembershipChange>
			individualSegmentMembershipChanges = page.getItems();

		int size = individualSegmentMembershipChanges.size();

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
					com.liferay.osb.faro.rest.dto.v1_0.
						IndividualSegmentMembershipChange.class)) {

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
		IndividualSegmentMembershipChange individualSegmentMembershipChange1,
		IndividualSegmentMembershipChange individualSegmentMembershipChange2) {

		if (individualSegmentMembershipChange1 ==
				individualSegmentMembershipChange2) {

			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("dateChanged", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individualSegmentMembershipChange1.getDateChanged(),
						individualSegmentMembershipChange2.getDateChanged())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateFirst", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individualSegmentMembershipChange1.getDateFirst(),
						individualSegmentMembershipChange2.getDateFirst())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individualSegmentMembershipChange1.getId(),
						individualSegmentMembershipChange2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("individualEmail", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individualSegmentMembershipChange1.getIndividualEmail(),
						individualSegmentMembershipChange2.
							getIndividualEmail())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("individualId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individualSegmentMembershipChange1.getIndividualId(),
						individualSegmentMembershipChange2.getIndividualId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("individualName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individualSegmentMembershipChange1.getIndividualName(),
						individualSegmentMembershipChange2.
							getIndividualName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"individualSegmentId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						individualSegmentMembershipChange1.
							getIndividualSegmentId(),
						individualSegmentMembershipChange2.
							getIndividualSegmentId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("operation", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						individualSegmentMembershipChange1.getOperation(),
						individualSegmentMembershipChange2.getOperation())) {

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

		if (!(_individualSegmentMembershipChangeResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_individualSegmentMembershipChangeResource;

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
		IndividualSegmentMembershipChange individualSegmentMembershipChange) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("dateChanged")) {
			if (operator.equals("between")) {
				Date date = individualSegmentMembershipChange.getDateChanged();

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
						individualSegmentMembershipChange.getDateChanged()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateFirst")) {
			if (operator.equals("between")) {
				Date date = individualSegmentMembershipChange.getDateFirst();

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
						individualSegmentMembershipChange.getDateFirst()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("id")) {
			Object object = individualSegmentMembershipChange.getId();

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

		if (entityFieldName.equals("individualEmail")) {
			Object object =
				individualSegmentMembershipChange.getIndividualEmail();

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

		if (entityFieldName.equals("individualId")) {
			Object object = individualSegmentMembershipChange.getIndividualId();

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

		if (entityFieldName.equals("individualName")) {
			Object object =
				individualSegmentMembershipChange.getIndividualName();

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

		if (entityFieldName.equals("individualSegmentId")) {
			Object object =
				individualSegmentMembershipChange.getIndividualSegmentId();

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

		if (entityFieldName.equals("operation")) {
			Object object = individualSegmentMembershipChange.getOperation();

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

	protected IndividualSegmentMembershipChange
			randomIndividualSegmentMembershipChange()
		throws Exception {

		return new IndividualSegmentMembershipChange() {
			{
				dateChanged = RandomTestUtil.nextDate();
				dateFirst = RandomTestUtil.nextDate();
				id = StringUtil.toLowerCase(RandomTestUtil.randomString());
				individualEmail = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				individualId = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				individualName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				individualSegmentId = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				operation = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected IndividualSegmentMembershipChange
			randomIrrelevantIndividualSegmentMembershipChange()
		throws Exception {

		IndividualSegmentMembershipChange
			randomIrrelevantIndividualSegmentMembershipChange =
				randomIndividualSegmentMembershipChange();

		return randomIrrelevantIndividualSegmentMembershipChange;
	}

	protected IndividualSegmentMembershipChange
			randomPatchIndividualSegmentMembershipChange()
		throws Exception {

		return randomIndividualSegmentMembershipChange();
	}

	protected IndividualSegmentMembershipChangeResource
		individualSegmentMembershipChangeResource;
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
			BaseIndividualSegmentMembershipChangeResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.osb.faro.rest.resource.v1_0.
		IndividualSegmentMembershipChangeResource
			_individualSegmentMembershipChangeResource;

}
// LIFERAY-REST-BUILDER-HASH:-1169598745