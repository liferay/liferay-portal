/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.headless.asset.library.client.dto.v1_0.UserGroup;
import com.liferay.headless.asset.library.client.http.HttpInvoker;
import com.liferay.headless.asset.library.client.pagination.Page;
import com.liferay.headless.asset.library.client.pagination.Pagination;
import com.liferay.headless.asset.library.client.resource.v1_0.UserGroupResource;
import com.liferay.headless.asset.library.client.serdes.v1_0.UserGroupSerDes;
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
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
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
 * @author Roberto Díaz
 * @generated
 */
@Generated("")
public abstract class BaseUserGroupResourceTestCase {

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

		irrelevantDepotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_ASSET_LIBRARY,
			new ServiceContext() {
				{
					setCompanyId(testCompany.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});
		irrelevantDepotEntryGroup = irrelevantDepotEntry.getGroup();
		testDepotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_ASSET_LIBRARY,
			new ServiceContext() {
				{
					setCompanyId(testCompany.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});
		testDepotEntryGroup = testDepotEntry.getGroup();

		_userGroupResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		userGroupResource = UserGroupResource.builder(
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

		UserGroup userGroup1 = randomUserGroup();

		String json = objectMapper.writeValueAsString(userGroup1);

		UserGroup userGroup2 = UserGroupSerDes.toDTO(json);

		Assert.assertTrue(equals(userGroup1, userGroup2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		UserGroup userGroup = randomUserGroup();

		String json1 = objectMapper.writeValueAsString(userGroup);
		String json2 = UserGroupSerDes.toJSON(userGroup);

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

		UserGroup userGroup = randomUserGroup();

		userGroup.setExternalReferenceCode(regex);
		userGroup.setName(regex);

		String json = UserGroupSerDes.toJSON(userGroup);

		Assert.assertFalse(json.contains(regex));

		userGroup = UserGroupSerDes.toDTO(json);

		Assert.assertEquals(regex, userGroup.getExternalReferenceCode());
		Assert.assertEquals(regex, userGroup.getName());
	}

	@Test
	public void testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserGroup userGroup =
			testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode_addUserGroup();

		assertHttpResponseStatusCode(
			204,
			userGroupResource.
				deleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode_getAssetLibraryExternalReferenceCode(),
					userGroup.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			userGroupResource.
				getAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode_getAssetLibraryExternalReferenceCode(),
					userGroup.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			userGroupResource.
				getAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode_getAssetLibraryExternalReferenceCode(),
					"-"));
	}

	protected UserGroup
			testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode_addUserGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode_getAssetLibraryExternalReferenceCode()
		throws Exception {

		return testDepotEntryGroup.getExternalReferenceCode();
	}

	@Test
	public void testDeleteAssetLibraryUserGroup() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserGroup userGroup = testDeleteAssetLibraryUserGroup_addUserGroup();

		assertHttpResponseStatusCode(
			204,
			userGroupResource.deleteAssetLibraryUserGroupHttpResponse(
				testDeleteAssetLibraryUserGroup_getAssetLibraryId(),
				userGroup.getId()));

		assertHttpResponseStatusCode(
			404,
			userGroupResource.getAssetLibraryUserGroupHttpResponse(
				testDeleteAssetLibraryUserGroup_getAssetLibraryId(),
				userGroup.getId()));
		assertHttpResponseStatusCode(
			404,
			userGroupResource.getAssetLibraryUserGroupHttpResponse(
				testDeleteAssetLibraryUserGroup_getAssetLibraryId(), 0L));
	}

	protected UserGroup testDeleteAssetLibraryUserGroup_addUserGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testDeleteAssetLibraryUserGroup_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGetAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode()
		throws Exception {

		UserGroup postUserGroup =
			testGetAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode_addUserGroup();

		UserGroup getUserGroup =
			userGroupResource.
				getAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode(
					testGetAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode_getAssetLibraryExternalReferenceCode(),
					postUserGroup.getExternalReferenceCode());

		assertEquals(postUserGroup, getUserGroup);
		assertValid(getUserGroup);
	}

	protected UserGroup
			testGetAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode_addUserGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode_getAssetLibraryExternalReferenceCode()
		throws Exception {

		return testDepotEntryGroup.getExternalReferenceCode();
	}

	@Test
	public void testGetAssetLibraryByExternalReferenceCodeUserGroupsPage()
		throws Exception {

		String externalReferenceCode =
			testGetAssetLibraryByExternalReferenceCodeUserGroupsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetAssetLibraryByExternalReferenceCodeUserGroupsPage_getIrrelevantExternalReferenceCode();

		Page<UserGroup> page =
			userGroupResource.
				getAssetLibraryByExternalReferenceCodeUserGroupsPage(
					externalReferenceCode, null, null, Pagination.of(1, 10),
					null);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			UserGroup irrelevantUserGroup =
				testGetAssetLibraryByExternalReferenceCodeUserGroupsPage_addUserGroup(
					irrelevantExternalReferenceCode,
					randomIrrelevantUserGroup());

			page =
				userGroupResource.
					getAssetLibraryByExternalReferenceCodeUserGroupsPage(
						irrelevantExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantUserGroup, (List<UserGroup>)page.getItems());
			assertValid(
				page,
				testGetAssetLibraryByExternalReferenceCodeUserGroupsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		UserGroup userGroup1 =
			testGetAssetLibraryByExternalReferenceCodeUserGroupsPage_addUserGroup(
				externalReferenceCode, randomUserGroup());

		UserGroup userGroup2 =
			testGetAssetLibraryByExternalReferenceCodeUserGroupsPage_addUserGroup(
				externalReferenceCode, randomUserGroup());

		page =
			userGroupResource.
				getAssetLibraryByExternalReferenceCodeUserGroupsPage(
					externalReferenceCode, null, null, Pagination.of(1, 10),
					null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(userGroup1, (List<UserGroup>)page.getItems());
		assertContains(userGroup2, (List<UserGroup>)page.getItems());
		assertValid(
			page,
			testGetAssetLibraryByExternalReferenceCodeUserGroupsPage_getExpectedActions(
				externalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibraryByExternalReferenceCodeUserGroupsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAssetLibraryByExternalReferenceCodeUserGroupsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetAssetLibraryByExternalReferenceCodeUserGroupsPage_getExternalReferenceCode();

		Page<UserGroup> userGroupsPage =
			userGroupResource.
				getAssetLibraryByExternalReferenceCodeUserGroupsPage(
					externalReferenceCode, null, null, null, null);

		int totalCount = GetterUtil.getInteger(userGroupsPage.getTotalCount());

		UserGroup userGroup1 =
			testGetAssetLibraryByExternalReferenceCodeUserGroupsPage_addUserGroup(
				externalReferenceCode, randomUserGroup());

		UserGroup userGroup2 =
			testGetAssetLibraryByExternalReferenceCodeUserGroupsPage_addUserGroup(
				externalReferenceCode, randomUserGroup());

		UserGroup userGroup3 =
			testGetAssetLibraryByExternalReferenceCodeUserGroupsPage_addUserGroup(
				externalReferenceCode, randomUserGroup());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<UserGroup> page1 =
				userGroupResource.
					getAssetLibraryByExternalReferenceCodeUserGroupsPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(userGroup1, (List<UserGroup>)page1.getItems());

			Page<UserGroup> page2 =
				userGroupResource.
					getAssetLibraryByExternalReferenceCodeUserGroupsPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(userGroup2, (List<UserGroup>)page2.getItems());

			Page<UserGroup> page3 =
				userGroupResource.
					getAssetLibraryByExternalReferenceCodeUserGroupsPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(userGroup3, (List<UserGroup>)page3.getItems());
		}
		else {
			Page<UserGroup> page1 =
				userGroupResource.
					getAssetLibraryByExternalReferenceCodeUserGroupsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<UserGroup> userGroups1 = (List<UserGroup>)page1.getItems();

			Assert.assertEquals(
				userGroups1.toString(), totalCount + 2, userGroups1.size());

			Page<UserGroup> page2 =
				userGroupResource.
					getAssetLibraryByExternalReferenceCodeUserGroupsPage(
						externalReferenceCode, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<UserGroup> userGroups2 = (List<UserGroup>)page2.getItems();

			Assert.assertEquals(userGroups2.toString(), 1, userGroups2.size());

			Page<UserGroup> page3 =
				userGroupResource.
					getAssetLibraryByExternalReferenceCodeUserGroupsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(userGroup1, (List<UserGroup>)page3.getItems());
			assertContains(userGroup2, (List<UserGroup>)page3.getItems());
			assertContains(userGroup3, (List<UserGroup>)page3.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryByExternalReferenceCodeUserGroupsPageWithSortDateTime()
		throws Exception {

		testGetAssetLibraryByExternalReferenceCodeUserGroupsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, userGroup1, userGroup2) -> {
				BeanTestUtil.setProperty(
					userGroup1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAssetLibraryByExternalReferenceCodeUserGroupsPageWithSortDouble()
		throws Exception {

		testGetAssetLibraryByExternalReferenceCodeUserGroupsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, userGroup1, userGroup2) -> {
				BeanTestUtil.setProperty(
					userGroup1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					userGroup2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAssetLibraryByExternalReferenceCodeUserGroupsPageWithSortInteger()
		throws Exception {

		testGetAssetLibraryByExternalReferenceCodeUserGroupsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, userGroup1, userGroup2) -> {
				BeanTestUtil.setProperty(userGroup1, entityField.getName(), 0);
				BeanTestUtil.setProperty(userGroup2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAssetLibraryByExternalReferenceCodeUserGroupsPageWithSortString()
		throws Exception {

		testGetAssetLibraryByExternalReferenceCodeUserGroupsPageWithSort(
			EntityField.Type.STRING,
			(entityField, userGroup1, userGroup2) -> {
				Class<?> clazz = userGroup1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						userGroup1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						userGroup2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						userGroup1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						userGroup2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						userGroup1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						userGroup2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetAssetLibraryByExternalReferenceCodeUserGroupsPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer<EntityField, UserGroup, UserGroup, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetAssetLibraryByExternalReferenceCodeUserGroupsPage_getExternalReferenceCode();

		UserGroup userGroup1 = randomUserGroup();
		UserGroup userGroup2 = randomUserGroup();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, userGroup1, userGroup2);
		}

		userGroup1 =
			testGetAssetLibraryByExternalReferenceCodeUserGroupsPage_addUserGroup(
				externalReferenceCode, userGroup1);

		userGroup2 =
			testGetAssetLibraryByExternalReferenceCodeUserGroupsPage_addUserGroup(
				externalReferenceCode, userGroup2);

		Page<UserGroup> page =
			userGroupResource.
				getAssetLibraryByExternalReferenceCodeUserGroupsPage(
					externalReferenceCode, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<UserGroup> ascPage =
				userGroupResource.
					getAssetLibraryByExternalReferenceCodeUserGroupsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(userGroup1, (List<UserGroup>)ascPage.getItems());
			assertContains(userGroup2, (List<UserGroup>)ascPage.getItems());

			Page<UserGroup> descPage =
				userGroupResource.
					getAssetLibraryByExternalReferenceCodeUserGroupsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(userGroup2, (List<UserGroup>)descPage.getItems());
			assertContains(userGroup1, (List<UserGroup>)descPage.getItems());
		}
	}

	protected UserGroup
			testGetAssetLibraryByExternalReferenceCodeUserGroupsPage_addUserGroup(
				String externalReferenceCode, UserGroup userGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAssetLibraryByExternalReferenceCodeUserGroupsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAssetLibraryByExternalReferenceCodeUserGroupsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetAssetLibraryUserGroup() throws Exception {
		UserGroup postUserGroup = testGetAssetLibraryUserGroup_addUserGroup();

		UserGroup getUserGroup = userGroupResource.getAssetLibraryUserGroup(
			testGetAssetLibraryUserGroup_getAssetLibraryId(),
			postUserGroup.getId());

		assertEquals(postUserGroup, getUserGroup);
		assertValid(getUserGroup);
	}

	protected UserGroup testGetAssetLibraryUserGroup_addUserGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAssetLibraryUserGroup_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGetAssetLibraryUserGroupsPage() throws Exception {
		Long assetLibraryId =
			testGetAssetLibraryUserGroupsPage_getAssetLibraryId();
		Long irrelevantAssetLibraryId =
			testGetAssetLibraryUserGroupsPage_getIrrelevantAssetLibraryId();

		Page<UserGroup> page = userGroupResource.getAssetLibraryUserGroupsPage(
			assetLibraryId, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantAssetLibraryId != null) {
			UserGroup irrelevantUserGroup =
				testGetAssetLibraryUserGroupsPage_addUserGroup(
					irrelevantAssetLibraryId, randomIrrelevantUserGroup());

			page = userGroupResource.getAssetLibraryUserGroupsPage(
				irrelevantAssetLibraryId, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantUserGroup, (List<UserGroup>)page.getItems());
			assertValid(
				page,
				testGetAssetLibraryUserGroupsPage_getExpectedActions(
					irrelevantAssetLibraryId));
		}

		UserGroup userGroup1 = testGetAssetLibraryUserGroupsPage_addUserGroup(
			assetLibraryId, randomUserGroup());

		UserGroup userGroup2 = testGetAssetLibraryUserGroupsPage_addUserGroup(
			assetLibraryId, randomUserGroup());

		page = userGroupResource.getAssetLibraryUserGroupsPage(
			assetLibraryId, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(userGroup1, (List<UserGroup>)page.getItems());
		assertContains(userGroup2, (List<UserGroup>)page.getItems());
		assertValid(
			page,
			testGetAssetLibraryUserGroupsPage_getExpectedActions(
				assetLibraryId));
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibraryUserGroupsPage_getExpectedActions(
				Long assetLibraryId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAssetLibraryUserGroupsPageWithPagination()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryUserGroupsPage_getAssetLibraryId();

		Page<UserGroup> userGroupsPage =
			userGroupResource.getAssetLibraryUserGroupsPage(
				assetLibraryId, null, null, null, null);

		int totalCount = GetterUtil.getInteger(userGroupsPage.getTotalCount());

		UserGroup userGroup1 = testGetAssetLibraryUserGroupsPage_addUserGroup(
			assetLibraryId, randomUserGroup());

		UserGroup userGroup2 = testGetAssetLibraryUserGroupsPage_addUserGroup(
			assetLibraryId, randomUserGroup());

		UserGroup userGroup3 = testGetAssetLibraryUserGroupsPage_addUserGroup(
			assetLibraryId, randomUserGroup());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<UserGroup> page1 =
				userGroupResource.getAssetLibraryUserGroupsPage(
					assetLibraryId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(userGroup1, (List<UserGroup>)page1.getItems());

			Page<UserGroup> page2 =
				userGroupResource.getAssetLibraryUserGroupsPage(
					assetLibraryId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(userGroup2, (List<UserGroup>)page2.getItems());

			Page<UserGroup> page3 =
				userGroupResource.getAssetLibraryUserGroupsPage(
					assetLibraryId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(userGroup3, (List<UserGroup>)page3.getItems());
		}
		else {
			Page<UserGroup> page1 =
				userGroupResource.getAssetLibraryUserGroupsPage(
					assetLibraryId, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<UserGroup> userGroups1 = (List<UserGroup>)page1.getItems();

			Assert.assertEquals(
				userGroups1.toString(), totalCount + 2, userGroups1.size());

			Page<UserGroup> page2 =
				userGroupResource.getAssetLibraryUserGroupsPage(
					assetLibraryId, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<UserGroup> userGroups2 = (List<UserGroup>)page2.getItems();

			Assert.assertEquals(userGroups2.toString(), 1, userGroups2.size());

			Page<UserGroup> page3 =
				userGroupResource.getAssetLibraryUserGroupsPage(
					assetLibraryId, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(userGroup1, (List<UserGroup>)page3.getItems());
			assertContains(userGroup2, (List<UserGroup>)page3.getItems());
			assertContains(userGroup3, (List<UserGroup>)page3.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryUserGroupsPageWithSortDateTime()
		throws Exception {

		testGetAssetLibraryUserGroupsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, userGroup1, userGroup2) -> {
				BeanTestUtil.setProperty(
					userGroup1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAssetLibraryUserGroupsPageWithSortDouble()
		throws Exception {

		testGetAssetLibraryUserGroupsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, userGroup1, userGroup2) -> {
				BeanTestUtil.setProperty(
					userGroup1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					userGroup2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAssetLibraryUserGroupsPageWithSortInteger()
		throws Exception {

		testGetAssetLibraryUserGroupsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, userGroup1, userGroup2) -> {
				BeanTestUtil.setProperty(userGroup1, entityField.getName(), 0);
				BeanTestUtil.setProperty(userGroup2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAssetLibraryUserGroupsPageWithSortString()
		throws Exception {

		testGetAssetLibraryUserGroupsPageWithSort(
			EntityField.Type.STRING,
			(entityField, userGroup1, userGroup2) -> {
				Class<?> clazz = userGroup1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						userGroup1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						userGroup2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						userGroup1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						userGroup2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						userGroup1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						userGroup2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetAssetLibraryUserGroupsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, UserGroup, UserGroup, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryUserGroupsPage_getAssetLibraryId();

		UserGroup userGroup1 = randomUserGroup();
		UserGroup userGroup2 = randomUserGroup();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, userGroup1, userGroup2);
		}

		userGroup1 = testGetAssetLibraryUserGroupsPage_addUserGroup(
			assetLibraryId, userGroup1);

		userGroup2 = testGetAssetLibraryUserGroupsPage_addUserGroup(
			assetLibraryId, userGroup2);

		Page<UserGroup> page = userGroupResource.getAssetLibraryUserGroupsPage(
			assetLibraryId, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<UserGroup> ascPage =
				userGroupResource.getAssetLibraryUserGroupsPage(
					assetLibraryId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(userGroup1, (List<UserGroup>)ascPage.getItems());
			assertContains(userGroup2, (List<UserGroup>)ascPage.getItems());

			Page<UserGroup> descPage =
				userGroupResource.getAssetLibraryUserGroupsPage(
					assetLibraryId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(userGroup2, (List<UserGroup>)descPage.getItems());
			assertContains(userGroup1, (List<UserGroup>)descPage.getItems());
		}
	}

	protected UserGroup testGetAssetLibraryUserGroupsPage_addUserGroup(
			Long assetLibraryId, UserGroup userGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAssetLibraryUserGroupsPage_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected Long
			testGetAssetLibraryUserGroupsPage_getIrrelevantAssetLibraryId()
		throws Exception {

		return irrelevantDepotEntry.getDepotEntryId();
	}

	@Test
	public void testPutAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode()
		throws Exception {

		UserGroup postUserGroup =
			testPutAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode_addUserGroup();

		UserGroup randomUserGroup = randomUserGroup();

		UserGroup putUserGroup =
			userGroupResource.
				putAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode(
					testPutAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode_getAssetLibraryExternalReferenceCode(),
					postUserGroup.getExternalReferenceCode());

		assertEquals(randomUserGroup, putUserGroup);
		assertValid(putUserGroup);

		UserGroup getUserGroup =
			userGroupResource.
				getAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode(
					testPutAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode_getAssetLibraryExternalReferenceCode(),
					putUserGroup.getExternalReferenceCode());

		assertEquals(randomUserGroup, getUserGroup);
		assertValid(getUserGroup);
	}

	protected UserGroup
			testPutAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode_addUserGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testPutAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserGroupByExternalReferenceCodeUserGroupExternalReferenceCode_getAssetLibraryExternalReferenceCode()
		throws Exception {

		return testDepotEntryGroup.getExternalReferenceCode();
	}

	@Test
	public void testPutAssetLibraryUserGroup() throws Exception {
		UserGroup postUserGroup = testPutAssetLibraryUserGroup_addUserGroup();

		UserGroup randomUserGroup = randomUserGroup();

		UserGroup putUserGroup = userGroupResource.putAssetLibraryUserGroup(
			testPutAssetLibraryUserGroup_getAssetLibraryId(),
			postUserGroup.getId());

		assertEquals(randomUserGroup, putUserGroup);
		assertValid(putUserGroup);

		UserGroup getUserGroup = userGroupResource.getAssetLibraryUserGroup(
			testPutAssetLibraryUserGroup_getAssetLibraryId(),
			putUserGroup.getId());

		assertEquals(randomUserGroup, getUserGroup);
		assertValid(getUserGroup);
	}

	protected UserGroup testPutAssetLibraryUserGroup_addUserGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testPutAssetLibraryUserGroup_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		UserGroup userGroup1 =
			testBatchEngineDeleteImportTask_addAssetLibraryUserGroup();

		testBatchEngineDeleteImportTask_deleteUserGroup(
			200, userGroup1.getExternalReferenceCode(),
			"assetLibraryExternalReferenceCode",
			testDepotEntryGroup.getExternalReferenceCode());

		assertHttpResponseStatusCode(
			404,
			userGroupResource.getAssetLibraryUserGroupHttpResponse(
				testBatchEngineDeleteImportTask_getAssetLibraryId(),
				userGroup1.getId()));
	}

	protected UserGroup
			testBatchEngineDeleteImportTask_addAssetLibraryUserGroup()
		throws Exception {

		return testDeleteAssetLibraryUserGroup_addUserGroup();
	}

	protected void testBatchEngineDeleteImportTask_deleteUserGroup(
			int expectedStatusCode, String externalReferenceCode,
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
				"com.liferay.headless.asset.library.dto.v1_0.UserGroup", null,
				null, null, null,
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

	protected Long testBatchEngineDeleteImportTask_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected void assertContains(
		UserGroup userGroup, List<UserGroup> userGroups) {

		boolean contains = false;

		for (UserGroup item : userGroups) {
			if (equals(userGroup, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			userGroups + " does not contain " + userGroup, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(UserGroup userGroup1, UserGroup userGroup2) {
		Assert.assertTrue(
			userGroup1 + " does not equal " + userGroup2,
			equals(userGroup1, userGroup2));
	}

	protected void assertEquals(
		List<UserGroup> userGroups1, List<UserGroup> userGroups2) {

		Assert.assertEquals(userGroups1.size(), userGroups2.size());

		for (int i = 0; i < userGroups1.size(); i++) {
			UserGroup userGroup1 = userGroups1.get(i);
			UserGroup userGroup2 = userGroups2.get(i);

			assertEquals(userGroup1, userGroup2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<UserGroup> userGroups1, List<UserGroup> userGroups2) {

		Assert.assertEquals(userGroups1.size(), userGroups2.size());

		for (UserGroup userGroup1 : userGroups1) {
			boolean contains = false;

			for (UserGroup userGroup2 : userGroups2) {
				if (equals(userGroup1, userGroup2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				userGroups2 + " does not contain " + userGroup1, contains);
		}
	}

	protected void assertValid(UserGroup userGroup) throws Exception {
		boolean valid = true;

		if (userGroup.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (userGroup.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (userGroup.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (userGroup.getName_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfUserAccounts", additionalAssertFieldName)) {

				if (userGroup.getNumberOfUserAccounts() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("roles", additionalAssertFieldName)) {
				if (userGroup.getRoles() == null) {
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

	protected void assertValid(Page<UserGroup> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<UserGroup> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<UserGroup> userGroups = page.getItems();

		int size = userGroups.size();

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
					com.liferay.headless.asset.library.dto.v1_0.UserGroup.
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

	protected boolean equals(UserGroup userGroup1, UserGroup userGroup2) {
		if (userGroup1 == userGroup2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						userGroup1.getExternalReferenceCode(),
						userGroup2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userGroup1.getId(), userGroup2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userGroup1.getName(), userGroup2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)userGroup1.getName_i18n(),
						(Map)userGroup2.getName_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfUserAccounts", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						userGroup1.getNumberOfUserAccounts(),
						userGroup2.getNumberOfUserAccounts())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("roles", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userGroup1.getRoles(), userGroup2.getRoles())) {

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

		if (!(_userGroupResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_userGroupResource;

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
		EntityField entityField, String operator, UserGroup userGroup) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = userGroup.getExternalReferenceCode();

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

		if (entityFieldName.equals("name")) {
			Object object = userGroup.getName();

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

		if (entityFieldName.equals("name_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("numberOfUserAccounts")) {
			sb.append(String.valueOf(userGroup.getNumberOfUserAccounts()));

			return sb.toString();
		}

		if (entityFieldName.equals("roles")) {
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

	protected UserGroup randomUserGroup() throws Exception {
		return new UserGroup() {
			{
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				numberOfUserAccounts = RandomTestUtil.randomInt();
			}
		};
	}

	protected UserGroup randomIrrelevantUserGroup() throws Exception {
		UserGroup randomIrrelevantUserGroup = randomUserGroup();

		return randomIrrelevantUserGroup;
	}

	protected UserGroup randomPatchUserGroup() throws Exception {
		return randomUserGroup();
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

	protected UserGroupResource userGroupResource;
	protected ImportTaskResource importTaskResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected com.liferay.portal.kernel.model.Company testCompany;
	protected DepotEntry irrelevantDepotEntry;
	protected com.liferay.portal.kernel.model.Group irrelevantDepotEntryGroup;
	protected DepotEntry testDepotEntry;
	protected com.liferay.portal.kernel.model.Group testDepotEntryGroup;
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
		LogFactoryUtil.getLog(BaseUserGroupResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.asset.library.resource.v1_0.UserGroupResource
		_userGroupResource;

}