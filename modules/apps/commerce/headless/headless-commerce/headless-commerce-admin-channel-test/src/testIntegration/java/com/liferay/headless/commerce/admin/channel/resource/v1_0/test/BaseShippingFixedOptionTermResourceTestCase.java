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
import com.liferay.headless.commerce.admin.channel.client.dto.v1_0.ShippingFixedOptionTerm;
import com.liferay.headless.commerce.admin.channel.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.channel.client.pagination.Page;
import com.liferay.headless.commerce.admin.channel.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.channel.client.resource.v1_0.ShippingFixedOptionTermResource;
import com.liferay.headless.commerce.admin.channel.client.serdes.v1_0.ShippingFixedOptionTermSerDes;
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
public abstract class BaseShippingFixedOptionTermResourceTestCase {

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

		_shippingFixedOptionTermResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		shippingFixedOptionTermResource =
			ShippingFixedOptionTermResource.builder(
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

		ShippingFixedOptionTerm shippingFixedOptionTerm1 =
			randomShippingFixedOptionTerm();

		String json = objectMapper.writeValueAsString(shippingFixedOptionTerm1);

		ShippingFixedOptionTerm shippingFixedOptionTerm2 =
			ShippingFixedOptionTermSerDes.toDTO(json);

		Assert.assertTrue(
			equals(shippingFixedOptionTerm1, shippingFixedOptionTerm2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ShippingFixedOptionTerm shippingFixedOptionTerm =
			randomShippingFixedOptionTerm();

		String json1 = objectMapper.writeValueAsString(shippingFixedOptionTerm);
		String json2 = ShippingFixedOptionTermSerDes.toJSON(
			shippingFixedOptionTerm);

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

		ShippingFixedOptionTerm shippingFixedOptionTerm =
			randomShippingFixedOptionTerm();

		shippingFixedOptionTerm.setTermExternalReferenceCode(regex);

		String json = ShippingFixedOptionTermSerDes.toJSON(
			shippingFixedOptionTerm);

		Assert.assertFalse(json.contains(regex));

		shippingFixedOptionTerm = ShippingFixedOptionTermSerDes.toDTO(json);

		Assert.assertEquals(
			regex, shippingFixedOptionTerm.getTermExternalReferenceCode());
	}

	@Test
	public void testDeleteShippingFixedOptionTerm() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ShippingFixedOptionTerm shippingFixedOptionTerm =
			testDeleteShippingFixedOptionTerm_addShippingFixedOptionTerm();

		assertHttpResponseStatusCode(
			204,
			shippingFixedOptionTermResource.
				deleteShippingFixedOptionTermHttpResponse(
					shippingFixedOptionTerm.getShippingFixedOptionTermId()));
	}

	protected ShippingFixedOptionTerm
			testDeleteShippingFixedOptionTerm_addShippingFixedOptionTerm()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteShippingFixedOptionTerm() throws Exception {

		// No namespace

		ShippingFixedOptionTerm shippingFixedOptionTerm1 =
			testGraphQLDeleteShippingFixedOptionTerm_addShippingFixedOptionTerm();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteShippingFixedOptionTerm",
						new HashMap<String, Object>() {
							{
								put(
									"shippingFixedOptionTermId",
									shippingFixedOptionTerm1.
										getShippingFixedOptionTermId());
							}
						})),
				"JSONObject/data", "Object/deleteShippingFixedOptionTerm"));

		// Using the namespace headlessCommerceAdminChannel_v1_0

		ShippingFixedOptionTerm shippingFixedOptionTerm2 =
			testGraphQLDeleteShippingFixedOptionTerm_addShippingFixedOptionTerm();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminChannel_v1_0",
						new GraphQLField(
							"deleteShippingFixedOptionTerm",
							new HashMap<String, Object>() {
								{
									put(
										"shippingFixedOptionTermId",
										shippingFixedOptionTerm2.
											getShippingFixedOptionTermId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminChannel_v1_0",
				"Object/deleteShippingFixedOptionTerm"));
	}

	protected ShippingFixedOptionTerm
			testGraphQLDeleteShippingFixedOptionTerm_addShippingFixedOptionTerm()
		throws Exception {

		return testGraphQLShippingFixedOptionTerm_addShippingFixedOptionTerm();
	}

	@Test
	public void testDeleteShippingFixedOptionTermBatch() throws Exception {
		ShippingFixedOptionTerm shippingFixedOptionTerm1 =
			testDeleteShippingFixedOptionTermBatch_addShippingFixedOptionTerm();

		testDeleteShippingFixedOptionTermBatch_deleteShippingFixedOptionTerm(
			202, null, shippingFixedOptionTerm1.getShippingFixedOptionTermId());
	}

	protected ShippingFixedOptionTerm
			testDeleteShippingFixedOptionTermBatch_addShippingFixedOptionTerm()
		throws Exception {

		return testDeleteShippingFixedOptionTerm_addShippingFixedOptionTerm();
	}

	protected void
			testDeleteShippingFixedOptionTermBatch_deleteShippingFixedOptionTerm(
				int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			shippingFixedOptionTermResource.
				deleteShippingFixedOptionTermBatchHttpResponse(
					null,
					JSONUtil.putAll(
						JSONUtil.put(
							"externalReferenceCode", () -> externalReferenceCode
						).put(
							"shippingFixedOptionTermId", () -> id
						)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetShippingFixedOptionIdShippingFixedOptionTermsPage()
		throws Exception {

		Long id =
			testGetShippingFixedOptionIdShippingFixedOptionTermsPage_getId();
		Long irrelevantId =
			testGetShippingFixedOptionIdShippingFixedOptionTermsPage_getIrrelevantId();

		Page<ShippingFixedOptionTerm> page =
			shippingFixedOptionTermResource.
				getShippingFixedOptionIdShippingFixedOptionTermsPage(
					id, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			ShippingFixedOptionTerm irrelevantShippingFixedOptionTerm =
				testGetShippingFixedOptionIdShippingFixedOptionTermsPage_addShippingFixedOptionTerm(
					irrelevantId, randomIrrelevantShippingFixedOptionTerm());

			page =
				shippingFixedOptionTermResource.
					getShippingFixedOptionIdShippingFixedOptionTermsPage(
						irrelevantId, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantShippingFixedOptionTerm,
				(List<ShippingFixedOptionTerm>)page.getItems());
			assertValid(
				page,
				testGetShippingFixedOptionIdShippingFixedOptionTermsPage_getExpectedActions(
					irrelevantId));
		}

		ShippingFixedOptionTerm shippingFixedOptionTerm1 =
			testGetShippingFixedOptionIdShippingFixedOptionTermsPage_addShippingFixedOptionTerm(
				id, randomShippingFixedOptionTerm());

		ShippingFixedOptionTerm shippingFixedOptionTerm2 =
			testGetShippingFixedOptionIdShippingFixedOptionTermsPage_addShippingFixedOptionTerm(
				id, randomShippingFixedOptionTerm());

		page =
			shippingFixedOptionTermResource.
				getShippingFixedOptionIdShippingFixedOptionTermsPage(
					id, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			shippingFixedOptionTerm1,
			(List<ShippingFixedOptionTerm>)page.getItems());
		assertContains(
			shippingFixedOptionTerm2,
			(List<ShippingFixedOptionTerm>)page.getItems());
		assertValid(
			page,
			testGetShippingFixedOptionIdShippingFixedOptionTermsPage_getExpectedActions(
				id));

		shippingFixedOptionTermResource.deleteShippingFixedOptionTerm(
			shippingFixedOptionTerm1.getShippingFixedOptionTermId());

		shippingFixedOptionTermResource.deleteShippingFixedOptionTerm(
			shippingFixedOptionTerm2.getShippingFixedOptionTermId());
	}

	protected Map<String, Map<String, String>>
			testGetShippingFixedOptionIdShippingFixedOptionTermsPage_getExpectedActions(
				Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetShippingFixedOptionIdShippingFixedOptionTermsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id =
			testGetShippingFixedOptionIdShippingFixedOptionTermsPage_getId();

		ShippingFixedOptionTerm shippingFixedOptionTerm1 =
			randomShippingFixedOptionTerm();

		shippingFixedOptionTerm1 =
			testGetShippingFixedOptionIdShippingFixedOptionTermsPage_addShippingFixedOptionTerm(
				id, shippingFixedOptionTerm1);

		for (EntityField entityField : entityFields) {
			Page<ShippingFixedOptionTerm> page =
				shippingFixedOptionTermResource.
					getShippingFixedOptionIdShippingFixedOptionTermsPage(
						id, null,
						getFilterString(
							entityField, "between", shippingFixedOptionTerm1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(shippingFixedOptionTerm1),
				(List<ShippingFixedOptionTerm>)page.getItems());
		}
	}

	@Test
	public void testGetShippingFixedOptionIdShippingFixedOptionTermsPageWithFilterDoubleEquals()
		throws Exception {

		testGetShippingFixedOptionIdShippingFixedOptionTermsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetShippingFixedOptionIdShippingFixedOptionTermsPageWithFilterStringContains()
		throws Exception {

		testGetShippingFixedOptionIdShippingFixedOptionTermsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetShippingFixedOptionIdShippingFixedOptionTermsPageWithFilterStringEquals()
		throws Exception {

		testGetShippingFixedOptionIdShippingFixedOptionTermsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetShippingFixedOptionIdShippingFixedOptionTermsPageWithFilterStringStartsWith()
		throws Exception {

		testGetShippingFixedOptionIdShippingFixedOptionTermsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetShippingFixedOptionIdShippingFixedOptionTermsPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id =
			testGetShippingFixedOptionIdShippingFixedOptionTermsPage_getId();

		ShippingFixedOptionTerm shippingFixedOptionTerm1 =
			testGetShippingFixedOptionIdShippingFixedOptionTermsPage_addShippingFixedOptionTerm(
				id, randomShippingFixedOptionTerm());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ShippingFixedOptionTerm shippingFixedOptionTerm2 =
			testGetShippingFixedOptionIdShippingFixedOptionTermsPage_addShippingFixedOptionTerm(
				id, randomShippingFixedOptionTerm());

		for (EntityField entityField : entityFields) {
			Page<ShippingFixedOptionTerm> page =
				shippingFixedOptionTermResource.
					getShippingFixedOptionIdShippingFixedOptionTermsPage(
						id, null,
						getFilterString(
							entityField, operator, shippingFixedOptionTerm1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(shippingFixedOptionTerm1),
				(List<ShippingFixedOptionTerm>)page.getItems());
		}
	}

	@Test
	public void testGetShippingFixedOptionIdShippingFixedOptionTermsPageWithPagination()
		throws Exception {

		Long id =
			testGetShippingFixedOptionIdShippingFixedOptionTermsPage_getId();

		Page<ShippingFixedOptionTerm> shippingFixedOptionTermsPage =
			shippingFixedOptionTermResource.
				getShippingFixedOptionIdShippingFixedOptionTermsPage(
					id, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			shippingFixedOptionTermsPage.getTotalCount());

		ShippingFixedOptionTerm shippingFixedOptionTerm1 =
			testGetShippingFixedOptionIdShippingFixedOptionTermsPage_addShippingFixedOptionTerm(
				id, randomShippingFixedOptionTerm());

		ShippingFixedOptionTerm shippingFixedOptionTerm2 =
			testGetShippingFixedOptionIdShippingFixedOptionTermsPage_addShippingFixedOptionTerm(
				id, randomShippingFixedOptionTerm());

		ShippingFixedOptionTerm shippingFixedOptionTerm3 =
			testGetShippingFixedOptionIdShippingFixedOptionTermsPage_addShippingFixedOptionTerm(
				id, randomShippingFixedOptionTerm());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ShippingFixedOptionTerm> page1 =
				shippingFixedOptionTermResource.
					getShippingFixedOptionIdShippingFixedOptionTermsPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				shippingFixedOptionTerm1,
				(List<ShippingFixedOptionTerm>)page1.getItems());

			Page<ShippingFixedOptionTerm> page2 =
				shippingFixedOptionTermResource.
					getShippingFixedOptionIdShippingFixedOptionTermsPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				shippingFixedOptionTerm2,
				(List<ShippingFixedOptionTerm>)page2.getItems());

			Page<ShippingFixedOptionTerm> page3 =
				shippingFixedOptionTermResource.
					getShippingFixedOptionIdShippingFixedOptionTermsPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				shippingFixedOptionTerm3,
				(List<ShippingFixedOptionTerm>)page3.getItems());
		}
		else {
			Page<ShippingFixedOptionTerm> page1 =
				shippingFixedOptionTermResource.
					getShippingFixedOptionIdShippingFixedOptionTermsPage(
						id, null, null, Pagination.of(1, totalCount + 2), null);

			List<ShippingFixedOptionTerm> shippingFixedOptionTerms1 =
				(List<ShippingFixedOptionTerm>)page1.getItems();

			Assert.assertEquals(
				shippingFixedOptionTerms1.toString(), totalCount + 2,
				shippingFixedOptionTerms1.size());

			Page<ShippingFixedOptionTerm> page2 =
				shippingFixedOptionTermResource.
					getShippingFixedOptionIdShippingFixedOptionTermsPage(
						id, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ShippingFixedOptionTerm> shippingFixedOptionTerms2 =
				(List<ShippingFixedOptionTerm>)page2.getItems();

			Assert.assertEquals(
				shippingFixedOptionTerms2.toString(), 1,
				shippingFixedOptionTerms2.size());

			Page<ShippingFixedOptionTerm> page3 =
				shippingFixedOptionTermResource.
					getShippingFixedOptionIdShippingFixedOptionTermsPage(
						id, null, null, Pagination.of(1, (int)totalCount + 3),
						null);

			assertContains(
				shippingFixedOptionTerm1,
				(List<ShippingFixedOptionTerm>)page3.getItems());
			assertContains(
				shippingFixedOptionTerm2,
				(List<ShippingFixedOptionTerm>)page3.getItems());
			assertContains(
				shippingFixedOptionTerm3,
				(List<ShippingFixedOptionTerm>)page3.getItems());
		}
	}

	@Test
	public void testGetShippingFixedOptionIdShippingFixedOptionTermsPageWithSortDateTime()
		throws Exception {

		testGetShippingFixedOptionIdShippingFixedOptionTermsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, shippingFixedOptionTerm1, shippingFixedOptionTerm2) ->{
				BeanTestUtil.setProperty(
					shippingFixedOptionTerm1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetShippingFixedOptionIdShippingFixedOptionTermsPageWithSortDouble()
		throws Exception {

		testGetShippingFixedOptionIdShippingFixedOptionTermsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, shippingFixedOptionTerm1, shippingFixedOptionTerm2) ->{
				BeanTestUtil.setProperty(
					shippingFixedOptionTerm1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					shippingFixedOptionTerm2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetShippingFixedOptionIdShippingFixedOptionTermsPageWithSortInteger()
		throws Exception {

		testGetShippingFixedOptionIdShippingFixedOptionTermsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, shippingFixedOptionTerm1, shippingFixedOptionTerm2) ->{
				BeanTestUtil.setProperty(
					shippingFixedOptionTerm1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					shippingFixedOptionTerm2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetShippingFixedOptionIdShippingFixedOptionTermsPageWithSortString()
		throws Exception {

		testGetShippingFixedOptionIdShippingFixedOptionTermsPageWithSort(
			EntityField.Type.STRING,
			(entityField, shippingFixedOptionTerm1, shippingFixedOptionTerm2) ->{
				Class<?> clazz = shippingFixedOptionTerm1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						shippingFixedOptionTerm1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						shippingFixedOptionTerm2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						shippingFixedOptionTerm1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						shippingFixedOptionTerm2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						shippingFixedOptionTerm1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						shippingFixedOptionTerm2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetShippingFixedOptionIdShippingFixedOptionTermsPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, ShippingFixedOptionTerm,
					 ShippingFixedOptionTerm, Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id =
			testGetShippingFixedOptionIdShippingFixedOptionTermsPage_getId();

		ShippingFixedOptionTerm shippingFixedOptionTerm1 =
			randomShippingFixedOptionTerm();
		ShippingFixedOptionTerm shippingFixedOptionTerm2 =
			randomShippingFixedOptionTerm();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, shippingFixedOptionTerm1,
				shippingFixedOptionTerm2);
		}

		shippingFixedOptionTerm1 =
			testGetShippingFixedOptionIdShippingFixedOptionTermsPage_addShippingFixedOptionTerm(
				id, shippingFixedOptionTerm1);

		shippingFixedOptionTerm2 =
			testGetShippingFixedOptionIdShippingFixedOptionTermsPage_addShippingFixedOptionTerm(
				id, shippingFixedOptionTerm2);

		Page<ShippingFixedOptionTerm> page =
			shippingFixedOptionTermResource.
				getShippingFixedOptionIdShippingFixedOptionTermsPage(
					id, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ShippingFixedOptionTerm> ascPage =
				shippingFixedOptionTermResource.
					getShippingFixedOptionIdShippingFixedOptionTermsPage(
						id, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				shippingFixedOptionTerm1,
				(List<ShippingFixedOptionTerm>)ascPage.getItems());
			assertContains(
				shippingFixedOptionTerm2,
				(List<ShippingFixedOptionTerm>)ascPage.getItems());

			Page<ShippingFixedOptionTerm> descPage =
				shippingFixedOptionTermResource.
					getShippingFixedOptionIdShippingFixedOptionTermsPage(
						id, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				shippingFixedOptionTerm2,
				(List<ShippingFixedOptionTerm>)descPage.getItems());
			assertContains(
				shippingFixedOptionTerm1,
				(List<ShippingFixedOptionTerm>)descPage.getItems());
		}
	}

	protected ShippingFixedOptionTerm
			testGetShippingFixedOptionIdShippingFixedOptionTermsPage_addShippingFixedOptionTerm(
				Long id, ShippingFixedOptionTerm shippingFixedOptionTerm)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetShippingFixedOptionIdShippingFixedOptionTermsPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetShippingFixedOptionIdShippingFixedOptionTermsPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostShippingFixedOptionIdShippingFixedOptionTerm()
		throws Exception {

		ShippingFixedOptionTerm randomShippingFixedOptionTerm =
			randomShippingFixedOptionTerm();

		ShippingFixedOptionTerm postShippingFixedOptionTerm =
			testPostShippingFixedOptionIdShippingFixedOptionTerm_addShippingFixedOptionTerm(
				randomShippingFixedOptionTerm);

		assertEquals(
			randomShippingFixedOptionTerm, postShippingFixedOptionTerm);
		assertValid(postShippingFixedOptionTerm);
	}

	protected ShippingFixedOptionTerm
			testPostShippingFixedOptionIdShippingFixedOptionTerm_addShippingFixedOptionTerm(
				ShippingFixedOptionTerm shippingFixedOptionTerm)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ShippingFixedOptionTerm shippingFixedOptionTerm1 =
			testBatchEngineDeleteImportTask_addShippingFixedOptionTerm();

		testBatchEngineDeleteImportTask_deleteShippingFixedOptionTerm(
			200, null, shippingFixedOptionTerm1.getShippingFixedOptionTermId());
	}

	protected ShippingFixedOptionTerm
			testBatchEngineDeleteImportTask_addShippingFixedOptionTerm()
		throws Exception {

		return testDeleteShippingFixedOptionTerm_addShippingFixedOptionTerm();
	}

	protected void
			testBatchEngineDeleteImportTask_deleteShippingFixedOptionTerm(
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
				"com.liferay.headless.commerce.admin.channel.dto.v1_0.ShippingFixedOptionTerm",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"shippingFixedOptionTermId", () -> id
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

	protected ShippingFixedOptionTerm
			testGraphQLShippingFixedOptionTerm_addShippingFixedOptionTerm()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		ShippingFixedOptionTerm shippingFixedOptionTerm,
		List<ShippingFixedOptionTerm> shippingFixedOptionTerms) {

		boolean contains = false;

		for (ShippingFixedOptionTerm item : shippingFixedOptionTerms) {
			if (equals(shippingFixedOptionTerm, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			shippingFixedOptionTerms + " does not contain " +
				shippingFixedOptionTerm,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ShippingFixedOptionTerm shippingFixedOptionTerm1,
		ShippingFixedOptionTerm shippingFixedOptionTerm2) {

		Assert.assertTrue(
			shippingFixedOptionTerm1 + " does not equal " +
				shippingFixedOptionTerm2,
			equals(shippingFixedOptionTerm1, shippingFixedOptionTerm2));
	}

	protected void assertEquals(
		List<ShippingFixedOptionTerm> shippingFixedOptionTerms1,
		List<ShippingFixedOptionTerm> shippingFixedOptionTerms2) {

		Assert.assertEquals(
			shippingFixedOptionTerms1.size(), shippingFixedOptionTerms2.size());

		for (int i = 0; i < shippingFixedOptionTerms1.size(); i++) {
			ShippingFixedOptionTerm shippingFixedOptionTerm1 =
				shippingFixedOptionTerms1.get(i);
			ShippingFixedOptionTerm shippingFixedOptionTerm2 =
				shippingFixedOptionTerms2.get(i);

			assertEquals(shippingFixedOptionTerm1, shippingFixedOptionTerm2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ShippingFixedOptionTerm> shippingFixedOptionTerms1,
		List<ShippingFixedOptionTerm> shippingFixedOptionTerms2) {

		Assert.assertEquals(
			shippingFixedOptionTerms1.size(), shippingFixedOptionTerms2.size());

		for (ShippingFixedOptionTerm shippingFixedOptionTerm1 :
				shippingFixedOptionTerms1) {

			boolean contains = false;

			for (ShippingFixedOptionTerm shippingFixedOptionTerm2 :
					shippingFixedOptionTerms2) {

				if (equals(
						shippingFixedOptionTerm1, shippingFixedOptionTerm2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				shippingFixedOptionTerms2 + " does not contain " +
					shippingFixedOptionTerm1,
				contains);
		}
	}

	protected void assertValid(ShippingFixedOptionTerm shippingFixedOptionTerm)
		throws Exception {

		boolean valid = true;

		if (shippingFixedOptionTerm.getShippingFixedOptionTermId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (shippingFixedOptionTerm.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingFixedOptionId", additionalAssertFieldName)) {

				if (shippingFixedOptionTerm.getShippingFixedOptionId() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingFixedOptionTermId", additionalAssertFieldName)) {

				if (shippingFixedOptionTerm.getShippingFixedOptionTermId() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("term", additionalAssertFieldName)) {
				if (shippingFixedOptionTerm.getTerm() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"termExternalReferenceCode", additionalAssertFieldName)) {

				if (shippingFixedOptionTerm.getTermExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("termId", additionalAssertFieldName)) {
				if (shippingFixedOptionTerm.getTermId() == null) {
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

	protected void assertValid(Page<ShippingFixedOptionTerm> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ShippingFixedOptionTerm> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ShippingFixedOptionTerm> shippingFixedOptionTerms =
			page.getItems();

		int size = shippingFixedOptionTerms.size();

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

		graphQLFields.add(new GraphQLField("shippingFixedOptionTermId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.channel.dto.v1_0.
						ShippingFixedOptionTerm.class)) {

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
		ShippingFixedOptionTerm shippingFixedOptionTerm1,
		ShippingFixedOptionTerm shippingFixedOptionTerm2) {

		if (shippingFixedOptionTerm1 == shippingFixedOptionTerm2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)shippingFixedOptionTerm1.getActions(),
						(Map)shippingFixedOptionTerm2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingFixedOptionId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						shippingFixedOptionTerm1.getShippingFixedOptionId(),
						shippingFixedOptionTerm2.getShippingFixedOptionId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingFixedOptionTermId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						shippingFixedOptionTerm1.getShippingFixedOptionTermId(),
						shippingFixedOptionTerm2.
							getShippingFixedOptionTermId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("term", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						shippingFixedOptionTerm1.getTerm(),
						shippingFixedOptionTerm2.getTerm())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"termExternalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						shippingFixedOptionTerm1.getTermExternalReferenceCode(),
						shippingFixedOptionTerm2.
							getTermExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("termId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						shippingFixedOptionTerm1.getTermId(),
						shippingFixedOptionTerm2.getTermId())) {

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

		if (!(_shippingFixedOptionTermResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_shippingFixedOptionTermResource;

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
		ShippingFixedOptionTerm shippingFixedOptionTerm) {

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

		if (entityFieldName.equals("shippingFixedOptionId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippingFixedOptionTermId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("term")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("termExternalReferenceCode")) {
			Object object =
				shippingFixedOptionTerm.getTermExternalReferenceCode();

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

		if (entityFieldName.equals("termId")) {
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

	protected ShippingFixedOptionTerm randomShippingFixedOptionTerm()
		throws Exception {

		return new ShippingFixedOptionTerm() {
			{
				shippingFixedOptionId = RandomTestUtil.randomLong();
				shippingFixedOptionTermId = RandomTestUtil.randomLong();
				termExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				termId = RandomTestUtil.randomLong();
			}
		};
	}

	protected ShippingFixedOptionTerm randomIrrelevantShippingFixedOptionTerm()
		throws Exception {

		ShippingFixedOptionTerm randomIrrelevantShippingFixedOptionTerm =
			randomShippingFixedOptionTerm();

		return randomIrrelevantShippingFixedOptionTerm;
	}

	protected ShippingFixedOptionTerm randomPatchShippingFixedOptionTerm()
		throws Exception {

		return randomShippingFixedOptionTerm();
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

	protected ShippingFixedOptionTermResource shippingFixedOptionTermResource;
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
			BaseShippingFixedOptionTermResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.channel.resource.v1_0.
		ShippingFixedOptionTermResource _shippingFixedOptionTermResource;

}