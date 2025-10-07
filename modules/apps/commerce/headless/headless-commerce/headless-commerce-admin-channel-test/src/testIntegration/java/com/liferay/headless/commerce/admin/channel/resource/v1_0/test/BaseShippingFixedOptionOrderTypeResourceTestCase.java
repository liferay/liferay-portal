/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.resource.v1_0.test;

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
import com.liferay.headless.commerce.admin.channel.client.dto.v1_0.ShippingFixedOptionOrderType;
import com.liferay.headless.commerce.admin.channel.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.channel.client.pagination.Page;
import com.liferay.headless.commerce.admin.channel.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.channel.client.resource.v1_0.ShippingFixedOptionOrderTypeResource;
import com.liferay.headless.commerce.admin.channel.client.serdes.v1_0.ShippingFixedOptionOrderTypeSerDes;
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
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public abstract class BaseShippingFixedOptionOrderTypeResourceTestCase {

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

		_shippingFixedOptionOrderTypeResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		shippingFixedOptionOrderTypeResource =
			ShippingFixedOptionOrderTypeResource.builder(
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

		ShippingFixedOptionOrderType shippingFixedOptionOrderType1 =
			randomShippingFixedOptionOrderType();

		String json = objectMapper.writeValueAsString(
			shippingFixedOptionOrderType1);

		ShippingFixedOptionOrderType shippingFixedOptionOrderType2 =
			ShippingFixedOptionOrderTypeSerDes.toDTO(json);

		Assert.assertTrue(
			equals(
				shippingFixedOptionOrderType1, shippingFixedOptionOrderType2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ShippingFixedOptionOrderType shippingFixedOptionOrderType =
			randomShippingFixedOptionOrderType();

		String json1 = objectMapper.writeValueAsString(
			shippingFixedOptionOrderType);
		String json2 = ShippingFixedOptionOrderTypeSerDes.toJSON(
			shippingFixedOptionOrderType);

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

		ShippingFixedOptionOrderType shippingFixedOptionOrderType =
			randomShippingFixedOptionOrderType();

		shippingFixedOptionOrderType.setOrderTypeExternalReferenceCode(regex);

		String json = ShippingFixedOptionOrderTypeSerDes.toJSON(
			shippingFixedOptionOrderType);

		Assert.assertFalse(json.contains(regex));

		shippingFixedOptionOrderType = ShippingFixedOptionOrderTypeSerDes.toDTO(
			json);

		Assert.assertEquals(
			regex,
			shippingFixedOptionOrderType.getOrderTypeExternalReferenceCode());
	}

	@Test
	public void testDeleteShippingFixedOptionOrderType() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ShippingFixedOptionOrderType shippingFixedOptionOrderType =
			testDeleteShippingFixedOptionOrderType_addShippingFixedOptionOrderType();

		assertHttpResponseStatusCode(
			204,
			shippingFixedOptionOrderTypeResource.
				deleteShippingFixedOptionOrderTypeHttpResponse(
					shippingFixedOptionOrderType.
						getShippingFixedOptionOrderTypeId()));
	}

	protected ShippingFixedOptionOrderType
			testDeleteShippingFixedOptionOrderType_addShippingFixedOptionOrderType()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteShippingFixedOptionOrderType()
		throws Exception {

		// No namespace

		ShippingFixedOptionOrderType shippingFixedOptionOrderType1 =
			testGraphQLDeleteShippingFixedOptionOrderType_addShippingFixedOptionOrderType();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteShippingFixedOptionOrderType",
						new HashMap<String, Object>() {
							{
								put(
									"shippingFixedOptionOrderTypeId",
									shippingFixedOptionOrderType1.
										getShippingFixedOptionOrderTypeId());
							}
						})),
				"JSONObject/data",
				"Object/deleteShippingFixedOptionOrderType"));

		// Using the namespace headlessCommerceAdminChannel_v1_0

		ShippingFixedOptionOrderType shippingFixedOptionOrderType2 =
			testGraphQLDeleteShippingFixedOptionOrderType_addShippingFixedOptionOrderType();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminChannel_v1_0",
						new GraphQLField(
							"deleteShippingFixedOptionOrderType",
							new HashMap<String, Object>() {
								{
									put(
										"shippingFixedOptionOrderTypeId",
										shippingFixedOptionOrderType2.
											getShippingFixedOptionOrderTypeId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminChannel_v1_0",
				"Object/deleteShippingFixedOptionOrderType"));
	}

	protected ShippingFixedOptionOrderType
			testGraphQLDeleteShippingFixedOptionOrderType_addShippingFixedOptionOrderType()
		throws Exception {

		return testGraphQLShippingFixedOptionOrderType_addShippingFixedOptionOrderType();
	}

	@Test
	public void testDeleteShippingFixedOptionOrderTypeBatch() throws Exception {
		ShippingFixedOptionOrderType shippingFixedOptionOrderType1 =
			testDeleteShippingFixedOptionOrderTypeBatch_addShippingFixedOptionOrderType();

		testDeleteShippingFixedOptionOrderTypeBatch_deleteShippingFixedOptionOrderType(
			202, null,
			shippingFixedOptionOrderType1.getShippingFixedOptionOrderTypeId());
	}

	protected ShippingFixedOptionOrderType
			testDeleteShippingFixedOptionOrderTypeBatch_addShippingFixedOptionOrderType()
		throws Exception {

		return testDeleteShippingFixedOptionOrderType_addShippingFixedOptionOrderType();
	}

	protected void
			testDeleteShippingFixedOptionOrderTypeBatch_deleteShippingFixedOptionOrderType(
				int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			shippingFixedOptionOrderTypeResource.
				deleteShippingFixedOptionOrderTypeBatchHttpResponse(
					null,
					JSONUtil.putAll(
						JSONUtil.put(
							"externalReferenceCode", () -> externalReferenceCode
						).put(
							"shippingFixedOptionOrderTypeId", () -> id
						)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPage()
		throws Exception {

		Long id =
			testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPage_getId();
		Long irrelevantId =
			testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPage_getIrrelevantId();

		Page<ShippingFixedOptionOrderType> page =
			shippingFixedOptionOrderTypeResource.
				getShippingFixedOptionIdShippingFixedOptionOrderTypesPage(
					id, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			ShippingFixedOptionOrderType
				irrelevantShippingFixedOptionOrderType =
					testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPage_addShippingFixedOptionOrderType(
						irrelevantId,
						randomIrrelevantShippingFixedOptionOrderType());

			page =
				shippingFixedOptionOrderTypeResource.
					getShippingFixedOptionIdShippingFixedOptionOrderTypesPage(
						irrelevantId, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantShippingFixedOptionOrderType,
				(List<ShippingFixedOptionOrderType>)page.getItems());
			assertValid(
				page,
				testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPage_getExpectedActions(
					irrelevantId));
		}

		ShippingFixedOptionOrderType shippingFixedOptionOrderType1 =
			testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPage_addShippingFixedOptionOrderType(
				id, randomShippingFixedOptionOrderType());

		ShippingFixedOptionOrderType shippingFixedOptionOrderType2 =
			testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPage_addShippingFixedOptionOrderType(
				id, randomShippingFixedOptionOrderType());

		page =
			shippingFixedOptionOrderTypeResource.
				getShippingFixedOptionIdShippingFixedOptionOrderTypesPage(
					id, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			shippingFixedOptionOrderType1,
			(List<ShippingFixedOptionOrderType>)page.getItems());
		assertContains(
			shippingFixedOptionOrderType2,
			(List<ShippingFixedOptionOrderType>)page.getItems());
		assertValid(
			page,
			testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPage_getExpectedActions(
				id));

		shippingFixedOptionOrderTypeResource.deleteShippingFixedOptionOrderType(
			shippingFixedOptionOrderType1.getShippingFixedOptionOrderTypeId());

		shippingFixedOptionOrderTypeResource.deleteShippingFixedOptionOrderType(
			shippingFixedOptionOrderType2.getShippingFixedOptionOrderTypeId());
	}

	protected Map<String, Map<String, String>>
			testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPage_getExpectedActions(
				Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id =
			testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPage_getId();

		ShippingFixedOptionOrderType shippingFixedOptionOrderType1 =
			randomShippingFixedOptionOrderType();

		shippingFixedOptionOrderType1 =
			testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPage_addShippingFixedOptionOrderType(
				id, shippingFixedOptionOrderType1);

		for (EntityField entityField : entityFields) {
			Page<ShippingFixedOptionOrderType> page =
				shippingFixedOptionOrderTypeResource.
					getShippingFixedOptionIdShippingFixedOptionOrderTypesPage(
						id, null,
						getFilterString(
							entityField, "between",
							shippingFixedOptionOrderType1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(shippingFixedOptionOrderType1),
				(List<ShippingFixedOptionOrderType>)page.getItems());
		}
	}

	@Test
	public void testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPageWithFilterDoubleEquals()
		throws Exception {

		testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPageWithFilterStringContains()
		throws Exception {

		testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPageWithFilterStringEquals()
		throws Exception {

		testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPageWithFilterStringStartsWith()
		throws Exception {

		testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id =
			testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPage_getId();

		ShippingFixedOptionOrderType shippingFixedOptionOrderType1 =
			testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPage_addShippingFixedOptionOrderType(
				id, randomShippingFixedOptionOrderType());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ShippingFixedOptionOrderType shippingFixedOptionOrderType2 =
			testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPage_addShippingFixedOptionOrderType(
				id, randomShippingFixedOptionOrderType());

		for (EntityField entityField : entityFields) {
			Page<ShippingFixedOptionOrderType> page =
				shippingFixedOptionOrderTypeResource.
					getShippingFixedOptionIdShippingFixedOptionOrderTypesPage(
						id, null,
						getFilterString(
							entityField, operator,
							shippingFixedOptionOrderType1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(shippingFixedOptionOrderType1),
				(List<ShippingFixedOptionOrderType>)page.getItems());
		}
	}

	@Test
	public void testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPageWithPagination()
		throws Exception {

		Long id =
			testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPage_getId();

		Page<ShippingFixedOptionOrderType> shippingFixedOptionOrderTypesPage =
			shippingFixedOptionOrderTypeResource.
				getShippingFixedOptionIdShippingFixedOptionOrderTypesPage(
					id, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			shippingFixedOptionOrderTypesPage.getTotalCount());

		ShippingFixedOptionOrderType shippingFixedOptionOrderType1 =
			testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPage_addShippingFixedOptionOrderType(
				id, randomShippingFixedOptionOrderType());

		ShippingFixedOptionOrderType shippingFixedOptionOrderType2 =
			testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPage_addShippingFixedOptionOrderType(
				id, randomShippingFixedOptionOrderType());

		ShippingFixedOptionOrderType shippingFixedOptionOrderType3 =
			testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPage_addShippingFixedOptionOrderType(
				id, randomShippingFixedOptionOrderType());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ShippingFixedOptionOrderType> page1 =
				shippingFixedOptionOrderTypeResource.
					getShippingFixedOptionIdShippingFixedOptionOrderTypesPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				shippingFixedOptionOrderType1,
				(List<ShippingFixedOptionOrderType>)page1.getItems());

			Page<ShippingFixedOptionOrderType> page2 =
				shippingFixedOptionOrderTypeResource.
					getShippingFixedOptionIdShippingFixedOptionOrderTypesPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				shippingFixedOptionOrderType2,
				(List<ShippingFixedOptionOrderType>)page2.getItems());

			Page<ShippingFixedOptionOrderType> page3 =
				shippingFixedOptionOrderTypeResource.
					getShippingFixedOptionIdShippingFixedOptionOrderTypesPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				shippingFixedOptionOrderType3,
				(List<ShippingFixedOptionOrderType>)page3.getItems());
		}
		else {
			Page<ShippingFixedOptionOrderType> page1 =
				shippingFixedOptionOrderTypeResource.
					getShippingFixedOptionIdShippingFixedOptionOrderTypesPage(
						id, null, null, Pagination.of(1, totalCount + 2), null);

			List<ShippingFixedOptionOrderType> shippingFixedOptionOrderTypes1 =
				(List<ShippingFixedOptionOrderType>)page1.getItems();

			Assert.assertEquals(
				shippingFixedOptionOrderTypes1.toString(), totalCount + 2,
				shippingFixedOptionOrderTypes1.size());

			Page<ShippingFixedOptionOrderType> page2 =
				shippingFixedOptionOrderTypeResource.
					getShippingFixedOptionIdShippingFixedOptionOrderTypesPage(
						id, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ShippingFixedOptionOrderType> shippingFixedOptionOrderTypes2 =
				(List<ShippingFixedOptionOrderType>)page2.getItems();

			Assert.assertEquals(
				shippingFixedOptionOrderTypes2.toString(), 1,
				shippingFixedOptionOrderTypes2.size());

			Page<ShippingFixedOptionOrderType> page3 =
				shippingFixedOptionOrderTypeResource.
					getShippingFixedOptionIdShippingFixedOptionOrderTypesPage(
						id, null, null, Pagination.of(1, (int)totalCount + 3),
						null);

			assertContains(
				shippingFixedOptionOrderType1,
				(List<ShippingFixedOptionOrderType>)page3.getItems());
			assertContains(
				shippingFixedOptionOrderType2,
				(List<ShippingFixedOptionOrderType>)page3.getItems());
			assertContains(
				shippingFixedOptionOrderType3,
				(List<ShippingFixedOptionOrderType>)page3.getItems());
		}
	}

	@Test
	public void testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPageWithSortDateTime()
		throws Exception {

		testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, shippingFixedOptionOrderType1,
			 shippingFixedOptionOrderType2) -> {

				BeanTestUtil.setProperty(
					shippingFixedOptionOrderType1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPageWithSortDouble()
		throws Exception {

		testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, shippingFixedOptionOrderType1,
			 shippingFixedOptionOrderType2) -> {

				BeanTestUtil.setProperty(
					shippingFixedOptionOrderType1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					shippingFixedOptionOrderType2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPageWithSortInteger()
		throws Exception {

		testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, shippingFixedOptionOrderType1,
			 shippingFixedOptionOrderType2) -> {

				BeanTestUtil.setProperty(
					shippingFixedOptionOrderType1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					shippingFixedOptionOrderType2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPageWithSortString()
		throws Exception {

		testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPageWithSort(
			EntityField.Type.STRING,
			(entityField, shippingFixedOptionOrderType1,
			 shippingFixedOptionOrderType2) -> {

				Class<?> clazz = shippingFixedOptionOrderType1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						shippingFixedOptionOrderType1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						shippingFixedOptionOrderType2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						shippingFixedOptionOrderType1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						shippingFixedOptionOrderType2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						shippingFixedOptionOrderType1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						shippingFixedOptionOrderType2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, ShippingFixedOptionOrderType,
					 ShippingFixedOptionOrderType, Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id =
			testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPage_getId();

		ShippingFixedOptionOrderType shippingFixedOptionOrderType1 =
			randomShippingFixedOptionOrderType();
		ShippingFixedOptionOrderType shippingFixedOptionOrderType2 =
			randomShippingFixedOptionOrderType();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, shippingFixedOptionOrderType1,
				shippingFixedOptionOrderType2);
		}

		shippingFixedOptionOrderType1 =
			testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPage_addShippingFixedOptionOrderType(
				id, shippingFixedOptionOrderType1);

		shippingFixedOptionOrderType2 =
			testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPage_addShippingFixedOptionOrderType(
				id, shippingFixedOptionOrderType2);

		Page<ShippingFixedOptionOrderType> page =
			shippingFixedOptionOrderTypeResource.
				getShippingFixedOptionIdShippingFixedOptionOrderTypesPage(
					id, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ShippingFixedOptionOrderType> ascPage =
				shippingFixedOptionOrderTypeResource.
					getShippingFixedOptionIdShippingFixedOptionOrderTypesPage(
						id, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				shippingFixedOptionOrderType1,
				(List<ShippingFixedOptionOrderType>)ascPage.getItems());
			assertContains(
				shippingFixedOptionOrderType2,
				(List<ShippingFixedOptionOrderType>)ascPage.getItems());

			Page<ShippingFixedOptionOrderType> descPage =
				shippingFixedOptionOrderTypeResource.
					getShippingFixedOptionIdShippingFixedOptionOrderTypesPage(
						id, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				shippingFixedOptionOrderType2,
				(List<ShippingFixedOptionOrderType>)descPage.getItems());
			assertContains(
				shippingFixedOptionOrderType1,
				(List<ShippingFixedOptionOrderType>)descPage.getItems());
		}
	}

	protected ShippingFixedOptionOrderType
			testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPage_addShippingFixedOptionOrderType(
				Long id,
				ShippingFixedOptionOrderType shippingFixedOptionOrderType)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetShippingFixedOptionIdShippingFixedOptionOrderTypesPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostShippingFixedOptionIdShippingFixedOptionOrderType()
		throws Exception {

		ShippingFixedOptionOrderType randomShippingFixedOptionOrderType =
			randomShippingFixedOptionOrderType();

		ShippingFixedOptionOrderType postShippingFixedOptionOrderType =
			testPostShippingFixedOptionIdShippingFixedOptionOrderType_addShippingFixedOptionOrderType(
				randomShippingFixedOptionOrderType);

		assertEquals(
			randomShippingFixedOptionOrderType,
			postShippingFixedOptionOrderType);
		assertValid(postShippingFixedOptionOrderType);
	}

	protected ShippingFixedOptionOrderType
			testPostShippingFixedOptionIdShippingFixedOptionOrderType_addShippingFixedOptionOrderType(
				ShippingFixedOptionOrderType shippingFixedOptionOrderType)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ShippingFixedOptionOrderType shippingFixedOptionOrderType1 =
			testBatchEngineDeleteImportTask_addShippingFixedOptionOrderType();

		testBatchEngineDeleteImportTask_deleteShippingFixedOptionOrderType(
			200, null,
			shippingFixedOptionOrderType1.getShippingFixedOptionOrderTypeId());
	}

	protected ShippingFixedOptionOrderType
			testBatchEngineDeleteImportTask_addShippingFixedOptionOrderType()
		throws Exception {

		return testDeleteShippingFixedOptionOrderType_addShippingFixedOptionOrderType();
	}

	protected void
			testBatchEngineDeleteImportTask_deleteShippingFixedOptionOrderType(
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
				"com.liferay.headless.commerce.admin.channel.dto.v1_0.ShippingFixedOptionOrderType",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"shippingFixedOptionOrderTypeId", () -> id
					)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		if (expectedStatusCode == 200) {
			waitForFinish(
				"COMPLETED",
				JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
		}
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected ShippingFixedOptionOrderType
			testGraphQLShippingFixedOptionOrderType_addShippingFixedOptionOrderType()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		ShippingFixedOptionOrderType shippingFixedOptionOrderType,
		List<ShippingFixedOptionOrderType> shippingFixedOptionOrderTypes) {

		boolean contains = false;

		for (ShippingFixedOptionOrderType item :
				shippingFixedOptionOrderTypes) {

			if (equals(shippingFixedOptionOrderType, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			shippingFixedOptionOrderTypes + " does not contain " +
				shippingFixedOptionOrderType,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ShippingFixedOptionOrderType shippingFixedOptionOrderType1,
		ShippingFixedOptionOrderType shippingFixedOptionOrderType2) {

		Assert.assertTrue(
			shippingFixedOptionOrderType1 + " does not equal " +
				shippingFixedOptionOrderType2,
			equals(
				shippingFixedOptionOrderType1, shippingFixedOptionOrderType2));
	}

	protected void assertEquals(
		List<ShippingFixedOptionOrderType> shippingFixedOptionOrderTypes1,
		List<ShippingFixedOptionOrderType> shippingFixedOptionOrderTypes2) {

		Assert.assertEquals(
			shippingFixedOptionOrderTypes1.size(),
			shippingFixedOptionOrderTypes2.size());

		for (int i = 0; i < shippingFixedOptionOrderTypes1.size(); i++) {
			ShippingFixedOptionOrderType shippingFixedOptionOrderType1 =
				shippingFixedOptionOrderTypes1.get(i);
			ShippingFixedOptionOrderType shippingFixedOptionOrderType2 =
				shippingFixedOptionOrderTypes2.get(i);

			assertEquals(
				shippingFixedOptionOrderType1, shippingFixedOptionOrderType2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ShippingFixedOptionOrderType> shippingFixedOptionOrderTypes1,
		List<ShippingFixedOptionOrderType> shippingFixedOptionOrderTypes2) {

		Assert.assertEquals(
			shippingFixedOptionOrderTypes1.size(),
			shippingFixedOptionOrderTypes2.size());

		for (ShippingFixedOptionOrderType shippingFixedOptionOrderType1 :
				shippingFixedOptionOrderTypes1) {

			boolean contains = false;

			for (ShippingFixedOptionOrderType shippingFixedOptionOrderType2 :
					shippingFixedOptionOrderTypes2) {

				if (equals(
						shippingFixedOptionOrderType1,
						shippingFixedOptionOrderType2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				shippingFixedOptionOrderTypes2 + " does not contain " +
					shippingFixedOptionOrderType1,
				contains);
		}
	}

	protected void assertValid(
			ShippingFixedOptionOrderType shippingFixedOptionOrderType)
		throws Exception {

		boolean valid = true;

		if (shippingFixedOptionOrderType.getShippingFixedOptionOrderTypeId() ==
				null) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (shippingFixedOptionOrderType.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderType", additionalAssertFieldName)) {
				if (shippingFixedOptionOrderType.getOrderType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"orderTypeExternalReferenceCode",
					additionalAssertFieldName)) {

				if (shippingFixedOptionOrderType.
						getOrderTypeExternalReferenceCode() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderTypeId", additionalAssertFieldName)) {
				if (shippingFixedOptionOrderType.getOrderTypeId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (shippingFixedOptionOrderType.getPriority() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingFixedOptionId", additionalAssertFieldName)) {

				if (shippingFixedOptionOrderType.getShippingFixedOptionId() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingFixedOptionOrderTypeId",
					additionalAssertFieldName)) {

				if (shippingFixedOptionOrderType.
						getShippingFixedOptionOrderTypeId() == null) {

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

	protected void assertValid(Page<ShippingFixedOptionOrderType> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ShippingFixedOptionOrderType> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ShippingFixedOptionOrderType>
			shippingFixedOptionOrderTypes = page.getItems();

		int size = shippingFixedOptionOrderTypes.size();

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

		graphQLFields.add(new GraphQLField("shippingFixedOptionOrderTypeId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.channel.dto.v1_0.
						ShippingFixedOptionOrderType.class)) {

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
		ShippingFixedOptionOrderType shippingFixedOptionOrderType1,
		ShippingFixedOptionOrderType shippingFixedOptionOrderType2) {

		if (shippingFixedOptionOrderType1 == shippingFixedOptionOrderType2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)shippingFixedOptionOrderType1.getActions(),
						(Map)shippingFixedOptionOrderType2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						shippingFixedOptionOrderType1.getOrderType(),
						shippingFixedOptionOrderType2.getOrderType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"orderTypeExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						shippingFixedOptionOrderType1.
							getOrderTypeExternalReferenceCode(),
						shippingFixedOptionOrderType2.
							getOrderTypeExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderTypeId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						shippingFixedOptionOrderType1.getOrderTypeId(),
						shippingFixedOptionOrderType2.getOrderTypeId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						shippingFixedOptionOrderType1.getPriority(),
						shippingFixedOptionOrderType2.getPriority())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingFixedOptionId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						shippingFixedOptionOrderType1.
							getShippingFixedOptionId(),
						shippingFixedOptionOrderType2.
							getShippingFixedOptionId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingFixedOptionOrderTypeId",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						shippingFixedOptionOrderType1.
							getShippingFixedOptionOrderTypeId(),
						shippingFixedOptionOrderType2.
							getShippingFixedOptionOrderTypeId())) {

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

		if (!(_shippingFixedOptionOrderTypeResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_shippingFixedOptionOrderTypeResource;

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
		ShippingFixedOptionOrderType shippingFixedOptionOrderType) {

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

		if (entityFieldName.equals("orderType")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("orderTypeExternalReferenceCode")) {
			Object object =
				shippingFixedOptionOrderType.
					getOrderTypeExternalReferenceCode();

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

		if (entityFieldName.equals("orderTypeId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priority")) {
			sb.append(
				String.valueOf(shippingFixedOptionOrderType.getPriority()));

			return sb.toString();
		}

		if (entityFieldName.equals("shippingFixedOptionId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippingFixedOptionOrderTypeId")) {
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

	protected ShippingFixedOptionOrderType randomShippingFixedOptionOrderType()
		throws Exception {

		return new ShippingFixedOptionOrderType() {
			{
				orderTypeExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				orderTypeId = RandomTestUtil.randomLong();
				priority = RandomTestUtil.randomInt();
				shippingFixedOptionId = RandomTestUtil.randomLong();
				shippingFixedOptionOrderTypeId = RandomTestUtil.randomLong();
			}
		};
	}

	protected ShippingFixedOptionOrderType
			randomIrrelevantShippingFixedOptionOrderType()
		throws Exception {

		ShippingFixedOptionOrderType
			randomIrrelevantShippingFixedOptionOrderType =
				randomShippingFixedOptionOrderType();

		return randomIrrelevantShippingFixedOptionOrderType;
	}

	protected ShippingFixedOptionOrderType
			randomPatchShippingFixedOptionOrderType()
		throws Exception {

		return randomShippingFixedOptionOrderType();
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

	protected ShippingFixedOptionOrderTypeResource
		shippingFixedOptionOrderTypeResource;
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
		LogFactoryUtil.getLog(
			BaseShippingFixedOptionOrderTypeResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.channel.resource.v1_0.
		ShippingFixedOptionOrderTypeResource
			_shippingFixedOptionOrderTypeResource;

}