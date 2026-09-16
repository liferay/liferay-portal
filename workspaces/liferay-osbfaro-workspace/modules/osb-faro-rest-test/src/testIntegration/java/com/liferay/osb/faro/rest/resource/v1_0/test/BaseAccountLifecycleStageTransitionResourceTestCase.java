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

import com.liferay.osb.faro.rest.client.dto.v1_0.AccountLifecycleStageTransition;
import com.liferay.osb.faro.rest.client.http.HttpInvoker;
import com.liferay.osb.faro.rest.client.pagination.Page;
import com.liferay.osb.faro.rest.client.pagination.Pagination;
import com.liferay.osb.faro.rest.client.resource.v1_0.AccountLifecycleStageTransitionResource;
import com.liferay.osb.faro.rest.client.serdes.v1_0.AccountLifecycleStageTransitionSerDes;
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
public abstract class BaseAccountLifecycleStageTransitionResourceTestCase {

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

		_accountLifecycleStageTransitionResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		accountLifecycleStageTransitionResource =
			AccountLifecycleStageTransitionResource.builder(
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

		AccountLifecycleStageTransition accountLifecycleStageTransition1 =
			randomAccountLifecycleStageTransition();

		String json = objectMapper.writeValueAsString(
			accountLifecycleStageTransition1);

		AccountLifecycleStageTransition accountLifecycleStageTransition2 =
			AccountLifecycleStageTransitionSerDes.toDTO(json);

		Assert.assertTrue(
			equals(
				accountLifecycleStageTransition1,
				accountLifecycleStageTransition2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		AccountLifecycleStageTransition accountLifecycleStageTransition =
			randomAccountLifecycleStageTransition();

		String json1 = objectMapper.writeValueAsString(
			accountLifecycleStageTransition);
		String json2 = AccountLifecycleStageTransitionSerDes.toJSON(
			accountLifecycleStageTransition);

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

		AccountLifecycleStageTransition accountLifecycleStageTransition =
			randomAccountLifecycleStageTransition();

		accountLifecycleStageTransition.setAccountId(regex);
		accountLifecycleStageTransition.setAccountName(regex);

		String json = AccountLifecycleStageTransitionSerDes.toJSON(
			accountLifecycleStageTransition);

		Assert.assertFalse(json.contains(regex));

		accountLifecycleStageTransition =
			AccountLifecycleStageTransitionSerDes.toDTO(json);

		Assert.assertEquals(
			regex, accountLifecycleStageTransition.getAccountId());
		Assert.assertEquals(
			regex, accountLifecycleStageTransition.getAccountName());
	}

	@Test
	public void testGetWorkspaceGroupAccountLifecycleStageTransitionsPage()
		throws Exception {

		Long groupId =
			testGetWorkspaceGroupAccountLifecycleStageTransitionsPage_getGroupId();
		Long irrelevantGroupId =
			testGetWorkspaceGroupAccountLifecycleStageTransitionsPage_getIrrelevantGroupId();
		String accountLifecycleId =
			testGetWorkspaceGroupAccountLifecycleStageTransitionsPage_getAccountLifecycleId();
		String irrelevantAccountLifecycleId =
			testGetWorkspaceGroupAccountLifecycleStageTransitionsPage_getIrrelevantAccountLifecycleId();

		Page<AccountLifecycleStageTransition> page =
			accountLifecycleStageTransitionResource.
				getWorkspaceGroupAccountLifecycleStageTransitionsPage(
					groupId, accountLifecycleId, RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), null,
					RandomTestUtil.randomString(), Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if ((irrelevantGroupId != null) &&
			(irrelevantAccountLifecycleId != null)) {

			AccountLifecycleStageTransition
				irrelevantAccountLifecycleStageTransition =
					testGetWorkspaceGroupAccountLifecycleStageTransitionsPage_addAccountLifecycleStageTransition(
						irrelevantGroupId, irrelevantAccountLifecycleId,
						randomIrrelevantAccountLifecycleStageTransition());

			page =
				accountLifecycleStageTransitionResource.
					getWorkspaceGroupAccountLifecycleStageTransitionsPage(
						irrelevantGroupId, irrelevantAccountLifecycleId, null,
						null, null, null, null, null, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAccountLifecycleStageTransition,
				(List<AccountLifecycleStageTransition>)page.getItems());
			assertValid(
				page,
				testGetWorkspaceGroupAccountLifecycleStageTransitionsPage_getExpectedActions(
					irrelevantGroupId, irrelevantAccountLifecycleId));
		}

		AccountLifecycleStageTransition accountLifecycleStageTransition1 =
			testGetWorkspaceGroupAccountLifecycleStageTransitionsPage_addAccountLifecycleStageTransition(
				groupId, accountLifecycleId,
				randomAccountLifecycleStageTransition());

		AccountLifecycleStageTransition accountLifecycleStageTransition2 =
			testGetWorkspaceGroupAccountLifecycleStageTransitionsPage_addAccountLifecycleStageTransition(
				groupId, accountLifecycleId,
				randomAccountLifecycleStageTransition());

		page =
			accountLifecycleStageTransitionResource.
				getWorkspaceGroupAccountLifecycleStageTransitionsPage(
					groupId, accountLifecycleId, null, null, null, null, null,
					null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			accountLifecycleStageTransition1,
			(List<AccountLifecycleStageTransition>)page.getItems());
		assertContains(
			accountLifecycleStageTransition2,
			(List<AccountLifecycleStageTransition>)page.getItems());
		assertValid(
			page,
			testGetWorkspaceGroupAccountLifecycleStageTransitionsPage_getExpectedActions(
				groupId, accountLifecycleId));
	}

	protected Map<String, Map<String, String>>
			testGetWorkspaceGroupAccountLifecycleStageTransitionsPage_getExpectedActions(
				Long groupId, String accountLifecycleId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWorkspaceGroupAccountLifecycleStageTransitionsPageWithPagination()
		throws Exception {

		Long groupId =
			testGetWorkspaceGroupAccountLifecycleStageTransitionsPage_getGroupId();
		String accountLifecycleId =
			testGetWorkspaceGroupAccountLifecycleStageTransitionsPage_getAccountLifecycleId();

		Page<AccountLifecycleStageTransition>
			accountLifecycleStageTransitionsPage =
				accountLifecycleStageTransitionResource.
					getWorkspaceGroupAccountLifecycleStageTransitionsPage(
						groupId, accountLifecycleId, null, null, null, null,
						null, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			accountLifecycleStageTransitionsPage.getTotalCount());

		AccountLifecycleStageTransition accountLifecycleStageTransition1 =
			testGetWorkspaceGroupAccountLifecycleStageTransitionsPage_addAccountLifecycleStageTransition(
				groupId, accountLifecycleId,
				randomAccountLifecycleStageTransition());

		AccountLifecycleStageTransition accountLifecycleStageTransition2 =
			testGetWorkspaceGroupAccountLifecycleStageTransitionsPage_addAccountLifecycleStageTransition(
				groupId, accountLifecycleId,
				randomAccountLifecycleStageTransition());

		AccountLifecycleStageTransition accountLifecycleStageTransition3 =
			testGetWorkspaceGroupAccountLifecycleStageTransitionsPage_addAccountLifecycleStageTransition(
				groupId, accountLifecycleId,
				randomAccountLifecycleStageTransition());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<AccountLifecycleStageTransition> page1 =
				accountLifecycleStageTransitionResource.
					getWorkspaceGroupAccountLifecycleStageTransitionsPage(
						groupId, accountLifecycleId, null, null, null, null,
						null, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				accountLifecycleStageTransition1,
				(List<AccountLifecycleStageTransition>)page1.getItems());

			Page<AccountLifecycleStageTransition> page2 =
				accountLifecycleStageTransitionResource.
					getWorkspaceGroupAccountLifecycleStageTransitionsPage(
						groupId, accountLifecycleId, null, null, null, null,
						null, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				accountLifecycleStageTransition2,
				(List<AccountLifecycleStageTransition>)page2.getItems());

			Page<AccountLifecycleStageTransition> page3 =
				accountLifecycleStageTransitionResource.
					getWorkspaceGroupAccountLifecycleStageTransitionsPage(
						groupId, accountLifecycleId, null, null, null, null,
						null, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				accountLifecycleStageTransition3,
				(List<AccountLifecycleStageTransition>)page3.getItems());
		}
		else {
			Page<AccountLifecycleStageTransition> page1 =
				accountLifecycleStageTransitionResource.
					getWorkspaceGroupAccountLifecycleStageTransitionsPage(
						groupId, accountLifecycleId, null, null, null, null,
						null, null, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<AccountLifecycleStageTransition>
				accountLifecycleStageTransitions1 =
					(List<AccountLifecycleStageTransition>)page1.getItems();

			Assert.assertEquals(
				accountLifecycleStageTransitions1.toString(), totalCount + 2,
				accountLifecycleStageTransitions1.size());

			Page<AccountLifecycleStageTransition> page2 =
				accountLifecycleStageTransitionResource.
					getWorkspaceGroupAccountLifecycleStageTransitionsPage(
						groupId, accountLifecycleId, null, null, null, null,
						null, null, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<AccountLifecycleStageTransition>
				accountLifecycleStageTransitions2 =
					(List<AccountLifecycleStageTransition>)page2.getItems();

			Assert.assertEquals(
				accountLifecycleStageTransitions2.toString(), 1,
				accountLifecycleStageTransitions2.size());

			Page<AccountLifecycleStageTransition> page3 =
				accountLifecycleStageTransitionResource.
					getWorkspaceGroupAccountLifecycleStageTransitionsPage(
						groupId, accountLifecycleId, null, null, null, null,
						null, null, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				accountLifecycleStageTransition1,
				(List<AccountLifecycleStageTransition>)page3.getItems());
			assertContains(
				accountLifecycleStageTransition2,
				(List<AccountLifecycleStageTransition>)page3.getItems());
			assertContains(
				accountLifecycleStageTransition3,
				(List<AccountLifecycleStageTransition>)page3.getItems());
		}
	}

	@Test
	public void testGetWorkspaceGroupAccountLifecycleStageTransitionsPageWithSortDateTime()
		throws Exception {

		testGetWorkspaceGroupAccountLifecycleStageTransitionsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, accountLifecycleStageTransition1,
			 accountLifecycleStageTransition2) -> {

				BeanTestUtil.setProperty(
					accountLifecycleStageTransition1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetWorkspaceGroupAccountLifecycleStageTransitionsPageWithSortDouble()
		throws Exception {

		testGetWorkspaceGroupAccountLifecycleStageTransitionsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, accountLifecycleStageTransition1,
			 accountLifecycleStageTransition2) -> {

				BeanTestUtil.setProperty(
					accountLifecycleStageTransition1, entityField.getName(),
					0.1);
				BeanTestUtil.setProperty(
					accountLifecycleStageTransition2, entityField.getName(),
					0.5);
			});
	}

	@Test
	public void testGetWorkspaceGroupAccountLifecycleStageTransitionsPageWithSortInteger()
		throws Exception {

		testGetWorkspaceGroupAccountLifecycleStageTransitionsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, accountLifecycleStageTransition1,
			 accountLifecycleStageTransition2) -> {

				BeanTestUtil.setProperty(
					accountLifecycleStageTransition1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					accountLifecycleStageTransition2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetWorkspaceGroupAccountLifecycleStageTransitionsPageWithSortString()
		throws Exception {

		testGetWorkspaceGroupAccountLifecycleStageTransitionsPageWithSort(
			EntityField.Type.STRING,
			(entityField, accountLifecycleStageTransition1,
			 accountLifecycleStageTransition2) -> {

				Class<?> clazz = accountLifecycleStageTransition1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						accountLifecycleStageTransition1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						accountLifecycleStageTransition2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						accountLifecycleStageTransition1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						accountLifecycleStageTransition2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						accountLifecycleStageTransition1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						accountLifecycleStageTransition2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetWorkspaceGroupAccountLifecycleStageTransitionsPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, AccountLifecycleStageTransition,
					 AccountLifecycleStageTransition, Exception>
						unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long groupId =
			testGetWorkspaceGroupAccountLifecycleStageTransitionsPage_getGroupId();
		String accountLifecycleId =
			testGetWorkspaceGroupAccountLifecycleStageTransitionsPage_getAccountLifecycleId();

		AccountLifecycleStageTransition accountLifecycleStageTransition1 =
			randomAccountLifecycleStageTransition();
		AccountLifecycleStageTransition accountLifecycleStageTransition2 =
			randomAccountLifecycleStageTransition();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, accountLifecycleStageTransition1,
				accountLifecycleStageTransition2);
		}

		accountLifecycleStageTransition1 =
			testGetWorkspaceGroupAccountLifecycleStageTransitionsPage_addAccountLifecycleStageTransition(
				groupId, accountLifecycleId, accountLifecycleStageTransition1);

		accountLifecycleStageTransition2 =
			testGetWorkspaceGroupAccountLifecycleStageTransitionsPage_addAccountLifecycleStageTransition(
				groupId, accountLifecycleId, accountLifecycleStageTransition2);

		Page<AccountLifecycleStageTransition> page =
			accountLifecycleStageTransitionResource.
				getWorkspaceGroupAccountLifecycleStageTransitionsPage(
					groupId, accountLifecycleId, null, null, null, null, null,
					null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<AccountLifecycleStageTransition> ascPage =
				accountLifecycleStageTransitionResource.
					getWorkspaceGroupAccountLifecycleStageTransitionsPage(
						groupId, accountLifecycleId, null, null, null, null,
						null, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				accountLifecycleStageTransition1,
				(List<AccountLifecycleStageTransition>)ascPage.getItems());
			assertContains(
				accountLifecycleStageTransition2,
				(List<AccountLifecycleStageTransition>)ascPage.getItems());

			Page<AccountLifecycleStageTransition> descPage =
				accountLifecycleStageTransitionResource.
					getWorkspaceGroupAccountLifecycleStageTransitionsPage(
						groupId, accountLifecycleId, null, null, null, null,
						null, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				accountLifecycleStageTransition2,
				(List<AccountLifecycleStageTransition>)descPage.getItems());
			assertContains(
				accountLifecycleStageTransition1,
				(List<AccountLifecycleStageTransition>)descPage.getItems());
		}
	}

	protected AccountLifecycleStageTransition
			testGetWorkspaceGroupAccountLifecycleStageTransitionsPage_addAccountLifecycleStageTransition(
				Long groupId, String accountLifecycleId,
				AccountLifecycleStageTransition accountLifecycleStageTransition)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkspaceGroupAccountLifecycleStageTransitionsPage_getGroupId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkspaceGroupAccountLifecycleStageTransitionsPage_getIrrelevantGroupId()
		throws Exception {

		return null;
	}

	protected String
			testGetWorkspaceGroupAccountLifecycleStageTransitionsPage_getAccountLifecycleId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetWorkspaceGroupAccountLifecycleStageTransitionsPage_getIrrelevantAccountLifecycleId()
		throws Exception {

		return null;
	}

	protected void assertContains(
		AccountLifecycleStageTransition accountLifecycleStageTransition,
		List<AccountLifecycleStageTransition>
			accountLifecycleStageTransitions) {

		boolean contains = false;

		for (AccountLifecycleStageTransition item :
				accountLifecycleStageTransitions) {

			if (equals(accountLifecycleStageTransition, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			accountLifecycleStageTransitions + " does not contain " +
				accountLifecycleStageTransition,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		AccountLifecycleStageTransition accountLifecycleStageTransition1,
		AccountLifecycleStageTransition accountLifecycleStageTransition2) {

		Assert.assertTrue(
			accountLifecycleStageTransition1 + " does not equal " +
				accountLifecycleStageTransition2,
			equals(
				accountLifecycleStageTransition1,
				accountLifecycleStageTransition2));
	}

	protected void assertEquals(
		List<AccountLifecycleStageTransition> accountLifecycleStageTransitions1,
		List<AccountLifecycleStageTransition>
			accountLifecycleStageTransitions2) {

		Assert.assertEquals(
			accountLifecycleStageTransitions1.size(),
			accountLifecycleStageTransitions2.size());

		for (int i = 0; i < accountLifecycleStageTransitions1.size(); i++) {
			AccountLifecycleStageTransition accountLifecycleStageTransition1 =
				accountLifecycleStageTransitions1.get(i);
			AccountLifecycleStageTransition accountLifecycleStageTransition2 =
				accountLifecycleStageTransitions2.get(i);

			assertEquals(
				accountLifecycleStageTransition1,
				accountLifecycleStageTransition2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<AccountLifecycleStageTransition> accountLifecycleStageTransitions1,
		List<AccountLifecycleStageTransition>
			accountLifecycleStageTransitions2) {

		Assert.assertEquals(
			accountLifecycleStageTransitions1.size(),
			accountLifecycleStageTransitions2.size());

		for (AccountLifecycleStageTransition accountLifecycleStageTransition1 :
				accountLifecycleStageTransitions1) {

			boolean contains = false;

			for (AccountLifecycleStageTransition
					accountLifecycleStageTransition2 :
						accountLifecycleStageTransitions2) {

				if (equals(
						accountLifecycleStageTransition1,
						accountLifecycleStageTransition2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				accountLifecycleStageTransitions2 + " does not contain " +
					accountLifecycleStageTransition1,
				contains);
		}
	}

	protected void assertValid(
			AccountLifecycleStageTransition accountLifecycleStageTransition)
		throws Exception {

		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (accountLifecycleStageTransition.getAccountId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("accountName", additionalAssertFieldName)) {
				if (accountLifecycleStageTransition.getAccountName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"fromAccountLifecycleStage", additionalAssertFieldName)) {

				if (accountLifecycleStageTransition.
						getFromAccountLifecycleStage() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"toAccountLifecycleStage", additionalAssertFieldName)) {

				if (accountLifecycleStageTransition.
						getToAccountLifecycleStage() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("transitionDate", additionalAssertFieldName)) {
				if (accountLifecycleStageTransition.getTransitionDate() ==
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

	protected void assertValid(Page<AccountLifecycleStageTransition> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<AccountLifecycleStageTransition> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<AccountLifecycleStageTransition>
			accountLifecycleStageTransitions = page.getItems();

		int size = accountLifecycleStageTransitions.size();

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
					com.liferay.osb.faro.rest.dto.v1_0.
						AccountLifecycleStageTransition.class)) {

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
		AccountLifecycleStageTransition accountLifecycleStageTransition1,
		AccountLifecycleStageTransition accountLifecycleStageTransition2) {

		if (accountLifecycleStageTransition1 ==
				accountLifecycleStageTransition2) {

			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountLifecycleStageTransition1.getAccountId(),
						accountLifecycleStageTransition2.getAccountId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("accountName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountLifecycleStageTransition1.getAccountName(),
						accountLifecycleStageTransition2.getAccountName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"fromAccountLifecycleStage", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						accountLifecycleStageTransition1.
							getFromAccountLifecycleStage(),
						accountLifecycleStageTransition2.
							getFromAccountLifecycleStage())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"toAccountLifecycleStage", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						accountLifecycleStageTransition1.
							getToAccountLifecycleStage(),
						accountLifecycleStageTransition2.
							getToAccountLifecycleStage())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("transitionDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountLifecycleStageTransition1.getTransitionDate(),
						accountLifecycleStageTransition2.getTransitionDate())) {

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

		if (!(_accountLifecycleStageTransitionResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_accountLifecycleStageTransitionResource;

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
		AccountLifecycleStageTransition accountLifecycleStageTransition) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("accountId")) {
			Object object = accountLifecycleStageTransition.getAccountId();

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

		if (entityFieldName.equals("accountName")) {
			Object object = accountLifecycleStageTransition.getAccountName();

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

		if (entityFieldName.equals("fromAccountLifecycleStage")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("toAccountLifecycleStage")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("transitionDate")) {
			if (operator.equals("between")) {
				Date date = accountLifecycleStageTransition.getTransitionDate();

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
						accountLifecycleStageTransition.getTransitionDate()));
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

	protected AccountLifecycleStageTransition
			randomAccountLifecycleStageTransition()
		throws Exception {

		return new AccountLifecycleStageTransition() {
			{
				accountId = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				accountName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				transitionDate = RandomTestUtil.nextDate();
			}
		};
	}

	protected AccountLifecycleStageTransition
			randomIrrelevantAccountLifecycleStageTransition()
		throws Exception {

		AccountLifecycleStageTransition
			randomIrrelevantAccountLifecycleStageTransition =
				randomAccountLifecycleStageTransition();

		return randomIrrelevantAccountLifecycleStageTransition;
	}

	protected AccountLifecycleStageTransition
			randomPatchAccountLifecycleStageTransition()
		throws Exception {

		return randomAccountLifecycleStageTransition();
	}

	protected AccountLifecycleStageTransitionResource
		accountLifecycleStageTransitionResource;
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
			BaseAccountLifecycleStageTransitionResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.osb.faro.rest.resource.v1_0.
		AccountLifecycleStageTransitionResource
			_accountLifecycleStageTransitionResource;

}
// LIFERAY-REST-BUILDER-HASH:-682377298