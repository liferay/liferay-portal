/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.list.type.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.admin.list.type.client.dto.v1_0.ListTypeEntry;
import com.liferay.headless.admin.list.type.client.http.HttpInvoker;
import com.liferay.headless.admin.list.type.client.pagination.Page;
import com.liferay.headless.admin.list.type.client.pagination.Pagination;
import com.liferay.headless.admin.list.type.client.resource.v1_0.ListTypeEntryResource;
import com.liferay.headless.admin.list.type.client.serdes.v1_0.ListTypeEntrySerDes;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONDeserializer;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateUtil;
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
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegate;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegateBuilderRegistry;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.lang.reflect.Method;

import java.net.URI;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Gabriel Albuquerque
 * @generated
 */
@Generated("")
public abstract class BaseListTypeEntryResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

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

		_listTypeEntryResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		listTypeEntryResource = ListTypeEntryResource.builder(
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

		ListTypeEntry listTypeEntry1 = randomListTypeEntry();

		String json = objectMapper.writeValueAsString(listTypeEntry1);

		ListTypeEntry listTypeEntry2 = ListTypeEntrySerDes.toDTO(json);

		Assert.assertTrue(equals(listTypeEntry1, listTypeEntry2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ListTypeEntry listTypeEntry = randomListTypeEntry();

		String json1 = objectMapper.writeValueAsString(listTypeEntry);
		String json2 = ListTypeEntrySerDes.toJSON(listTypeEntry);

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

		ListTypeEntry listTypeEntry = randomListTypeEntry();

		listTypeEntry.setExternalReferenceCode(regex);
		listTypeEntry.setKey(regex);
		listTypeEntry.setName(regex);
		listTypeEntry.setType(regex);

		String json = ListTypeEntrySerDes.toJSON(listTypeEntry);

		Assert.assertFalse(json.contains(regex));

		listTypeEntry = ListTypeEntrySerDes.toDTO(json);

		Assert.assertEquals(regex, listTypeEntry.getExternalReferenceCode());
		Assert.assertEquals(regex, listTypeEntry.getKey());
		Assert.assertEquals(regex, listTypeEntry.getName());
		Assert.assertEquals(regex, listTypeEntry.getType());
	}

	@Test
	public void testDeleteListTypeEntry() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ListTypeEntry listTypeEntry =
			testDeleteListTypeEntry_addListTypeEntry();

		assertHttpResponseStatusCode(
			204,
			listTypeEntryResource.deleteListTypeEntryHttpResponse(
				listTypeEntry.getId()));

		assertHttpResponseStatusCode(
			404,
			listTypeEntryResource.getListTypeEntryHttpResponse(
				listTypeEntry.getId()));
		assertHttpResponseStatusCode(
			404, listTypeEntryResource.getListTypeEntryHttpResponse(0L));
	}

	protected ListTypeEntry testDeleteListTypeEntry_addListTypeEntry()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteListTypeEntry() throws Exception {

		// No namespace

		ListTypeEntry listTypeEntry1 =
			testGraphQLDeleteListTypeEntry_addListTypeEntry();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteListTypeEntry",
						new HashMap<String, Object>() {
							{
								put("listTypeEntryId", listTypeEntry1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteListTypeEntry"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"listTypeEntry",
					new HashMap<String, Object>() {
						{
							put("listTypeEntryId", listTypeEntry1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessAdminListType_v1_0

		ListTypeEntry listTypeEntry2 =
			testGraphQLDeleteListTypeEntry_addListTypeEntry();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminListType_v1_0",
						new GraphQLField(
							"deleteListTypeEntry",
							new HashMap<String, Object>() {
								{
									put(
										"listTypeEntryId",
										listTypeEntry2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminListType_v1_0",
				"Object/deleteListTypeEntry"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessAdminListType_v1_0",
					new GraphQLField(
						"listTypeEntry",
						new HashMap<String, Object>() {
							{
								put("listTypeEntryId", listTypeEntry2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ListTypeEntry testGraphQLDeleteListTypeEntry_addListTypeEntry()
		throws Exception {

		return testGraphQLListTypeEntry_addListTypeEntry();
	}

	@Test
	public void testDeleteListTypeEntryBatch() throws Exception {
		ListTypeEntry listTypeEntry1 =
			testDeleteListTypeEntryBatch_addListTypeEntry();

		testDeleteListTypeEntryBatch_deleteListTypeEntry(
			202, null, listTypeEntry1.getId());

		assertHttpResponseStatusCode(
			404,
			listTypeEntryResource.getListTypeEntryHttpResponse(
				listTypeEntry1.getId()));
	}

	protected ListTypeEntry testDeleteListTypeEntryBatch_addListTypeEntry()
		throws Exception {

		return testDeleteListTypeEntry_addListTypeEntry();
	}

	protected void testDeleteListTypeEntryBatch_deleteListTypeEntry(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			listTypeEntryResource.deleteListTypeEntryBatchHttpResponse(
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
	public void testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage()
		throws Exception {

		String externalReferenceCode =
			testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage_getIrrelevantExternalReferenceCode();

		Page<ListTypeEntry> page =
			listTypeEntryResource.
				getListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage(
					externalReferenceCode, null, null, null,
					Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			ListTypeEntry irrelevantListTypeEntry =
				testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage_addListTypeEntry(
					irrelevantExternalReferenceCode,
					randomIrrelevantListTypeEntry());

			page =
				listTypeEntryResource.
					getListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage(
						irrelevantExternalReferenceCode, null, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantListTypeEntry, (List<ListTypeEntry>)page.getItems());
			assertValid(
				page,
				testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		ListTypeEntry listTypeEntry1 =
			testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage_addListTypeEntry(
				externalReferenceCode, randomListTypeEntry());

		ListTypeEntry listTypeEntry2 =
			testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage_addListTypeEntry(
				externalReferenceCode, randomListTypeEntry());

		page =
			listTypeEntryResource.
				getListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage(
					externalReferenceCode, null, null, null,
					Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(listTypeEntry1, (List<ListTypeEntry>)page.getItems());
		assertContains(listTypeEntry2, (List<ListTypeEntry>)page.getItems());
		assertValid(
			page,
			testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage_getExpectedActions(
				externalReferenceCode));

		listTypeEntryResource.deleteListTypeEntry(listTypeEntry1.getId());

		listTypeEntryResource.deleteListTypeEntry(listTypeEntry2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage_getExternalReferenceCode();

		ListTypeEntry listTypeEntry1 = randomListTypeEntry();

		listTypeEntry1 =
			testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage_addListTypeEntry(
				externalReferenceCode, listTypeEntry1);

		for (EntityField entityField : entityFields) {
			Page<ListTypeEntry> page =
				listTypeEntryResource.
					getListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage(
						externalReferenceCode, null, null,
						getFilterString(entityField, "between", listTypeEntry1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(listTypeEntry1),
				(List<ListTypeEntry>)page.getItems());
		}
	}

	@Test
	public void testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPageWithFilterDoubleEquals()
		throws Exception {

		testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPageWithFilterStringContains()
		throws Exception {

		testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPageWithFilterStringEquals()
		throws Exception {

		testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPageWithFilterStringStartsWith()
		throws Exception {

		testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage_getExternalReferenceCode();

		ListTypeEntry listTypeEntry1 =
			testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage_addListTypeEntry(
				externalReferenceCode, randomListTypeEntry());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ListTypeEntry listTypeEntry2 =
			testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage_addListTypeEntry(
				externalReferenceCode, randomListTypeEntry());

		for (EntityField entityField : entityFields) {
			Page<ListTypeEntry> page =
				listTypeEntryResource.
					getListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage(
						externalReferenceCode, null, null,
						getFilterString(entityField, operator, listTypeEntry1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(listTypeEntry1),
				(List<ListTypeEntry>)page.getItems());
		}
	}

	@Test
	public void testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage_getExternalReferenceCode();

		Page<ListTypeEntry> listTypeEntriesPage =
			listTypeEntryResource.
				getListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage(
					externalReferenceCode, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			listTypeEntriesPage.getTotalCount());

		ListTypeEntry listTypeEntry1 =
			testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage_addListTypeEntry(
				externalReferenceCode, randomListTypeEntry());

		ListTypeEntry listTypeEntry2 =
			testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage_addListTypeEntry(
				externalReferenceCode, randomListTypeEntry());

		ListTypeEntry listTypeEntry3 =
			testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage_addListTypeEntry(
				externalReferenceCode, randomListTypeEntry());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ListTypeEntry> page1 =
				listTypeEntryResource.
					getListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage(
						externalReferenceCode, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				listTypeEntry1, (List<ListTypeEntry>)page1.getItems());

			Page<ListTypeEntry> page2 =
				listTypeEntryResource.
					getListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage(
						externalReferenceCode, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				listTypeEntry2, (List<ListTypeEntry>)page2.getItems());

			Page<ListTypeEntry> page3 =
				listTypeEntryResource.
					getListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage(
						externalReferenceCode, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				listTypeEntry3, (List<ListTypeEntry>)page3.getItems());
		}
		else {
			Page<ListTypeEntry> page1 =
				listTypeEntryResource.
					getListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage(
						externalReferenceCode, null, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<ListTypeEntry> listTypeEntries1 =
				(List<ListTypeEntry>)page1.getItems();

			Assert.assertEquals(
				listTypeEntries1.toString(), totalCount + 2,
				listTypeEntries1.size());

			Page<ListTypeEntry> page2 =
				listTypeEntryResource.
					getListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage(
						externalReferenceCode, null, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ListTypeEntry> listTypeEntries2 =
				(List<ListTypeEntry>)page2.getItems();

			Assert.assertEquals(
				listTypeEntries2.toString(), 1, listTypeEntries2.size());

			Page<ListTypeEntry> page3 =
				listTypeEntryResource.
					getListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage(
						externalReferenceCode, null, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				listTypeEntry1, (List<ListTypeEntry>)page3.getItems());
			assertContains(
				listTypeEntry2, (List<ListTypeEntry>)page3.getItems());
			assertContains(
				listTypeEntry3, (List<ListTypeEntry>)page3.getItems());
		}
	}

	@Test
	public void testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPageWithSortDateTime()
		throws Exception {

		testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, listTypeEntry1, listTypeEntry2) -> {
				BeanTestUtil.setProperty(
					listTypeEntry1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPageWithSortDouble()
		throws Exception {

		testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, listTypeEntry1, listTypeEntry2) -> {
				BeanTestUtil.setProperty(
					listTypeEntry1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					listTypeEntry2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPageWithSortInteger()
		throws Exception {

		testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, listTypeEntry1, listTypeEntry2) -> {
				BeanTestUtil.setProperty(
					listTypeEntry1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					listTypeEntry2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPageWithSortString()
		throws Exception {

		testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPageWithSort(
			EntityField.Type.STRING,
			(entityField, listTypeEntry1, listTypeEntry2) -> {
				Class<?> clazz = listTypeEntry1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						listTypeEntry1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						listTypeEntry2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						listTypeEntry1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						listTypeEntry2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						listTypeEntry1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						listTypeEntry2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, ListTypeEntry, ListTypeEntry, Exception>
						unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage_getExternalReferenceCode();

		ListTypeEntry listTypeEntry1 = randomListTypeEntry();
		ListTypeEntry listTypeEntry2 = randomListTypeEntry();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, listTypeEntry1, listTypeEntry2);
		}

		listTypeEntry1 =
			testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage_addListTypeEntry(
				externalReferenceCode, listTypeEntry1);

		listTypeEntry2 =
			testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage_addListTypeEntry(
				externalReferenceCode, listTypeEntry2);

		Page<ListTypeEntry> page =
			listTypeEntryResource.
				getListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage(
					externalReferenceCode, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ListTypeEntry> ascPage =
				listTypeEntryResource.
					getListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage(
						externalReferenceCode, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				listTypeEntry1, (List<ListTypeEntry>)ascPage.getItems());
			assertContains(
				listTypeEntry2, (List<ListTypeEntry>)ascPage.getItems());

			Page<ListTypeEntry> descPage =
				listTypeEntryResource.
					getListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage(
						externalReferenceCode, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				listTypeEntry2, (List<ListTypeEntry>)descPage.getItems());
			assertContains(
				listTypeEntry1, (List<ListTypeEntry>)descPage.getItems());
		}
	}

	protected ListTypeEntry
			testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage_addListTypeEntry(
				String externalReferenceCode, ListTypeEntry listTypeEntry)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage()
		throws Exception {

		String externalReferenceCode =
			testGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage_getExternalReferenceCode();

		GraphQLField graphQLField = new GraphQLField(
			"listTypeDefinitionByExternalReferenceCodeListTypeEntries",
			new HashMap<String, Object>() {
				{
					put(
						"externalReferenceCode",
						"\"" + externalReferenceCode + "\"");
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject
			listTypeDefinitionByExternalReferenceCodeListTypeEntriesJSONObject =
				JSONUtil.getValueAsJSONObject(
					invokeGraphQLQuery(graphQLField), "JSONObject/data",
					"JSONObject/listTypeDefinitionByExternalReferenceCodeListTypeEntries");

		long totalCount =
			listTypeDefinitionByExternalReferenceCodeListTypeEntriesJSONObject.
				getLong("totalCount");

		ListTypeEntry listTypeEntry1 =
			testGraphQLGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPageListTypeDefinitionListTypeEntry_addListTypeEntry(
				externalReferenceCode, randomListTypeEntry());

		ListTypeEntry listTypeEntry2 =
			testGraphQLGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPageListTypeDefinitionListTypeEntry_addListTypeEntry(
				externalReferenceCode, randomListTypeEntry());

		listTypeDefinitionByExternalReferenceCodeListTypeEntriesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/listTypeDefinitionByExternalReferenceCodeListTypeEntries");

		Assert.assertEquals(
			totalCount + 2,
			listTypeDefinitionByExternalReferenceCodeListTypeEntriesJSONObject.
				getLong("totalCount"));

		assertContains(
			listTypeEntry1,
			Arrays.asList(
				ListTypeEntrySerDes.toDTOs(
					listTypeDefinitionByExternalReferenceCodeListTypeEntriesJSONObject.
						getString("items"))));
		assertContains(
			listTypeEntry2,
			Arrays.asList(
				ListTypeEntrySerDes.toDTOs(
					listTypeDefinitionByExternalReferenceCodeListTypeEntriesJSONObject.
						getString("items"))));

		// Using the namespace headlessAdminListType_v1_0

		listTypeDefinitionByExternalReferenceCodeListTypeEntriesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminListType_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/headlessAdminListType_v1_0",
				"JSONObject/listTypeDefinitionByExternalReferenceCodeListTypeEntries");

		Assert.assertEquals(
			totalCount + 2,
			listTypeDefinitionByExternalReferenceCodeListTypeEntriesJSONObject.
				getLong("totalCount"));

		assertContains(
			listTypeEntry1,
			Arrays.asList(
				ListTypeEntrySerDes.toDTOs(
					listTypeDefinitionByExternalReferenceCodeListTypeEntriesJSONObject.
						getString("items"))));
		assertContains(
			listTypeEntry2,
			Arrays.asList(
				ListTypeEntrySerDes.toDTOs(
					listTypeDefinitionByExternalReferenceCodeListTypeEntriesJSONObject.
						getString("items"))));
	}

	protected ListTypeEntry
			testGraphQLGetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPageListTypeDefinitionListTypeEntry_addListTypeEntry(
				String externalReferenceCode, ListTypeEntry listTypeEntry)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetListTypeDefinitionListTypeEntriesPage()
		throws Exception {

		Long listTypeDefinitionId =
			testGetListTypeDefinitionListTypeEntriesPage_getListTypeDefinitionId();
		Long irrelevantListTypeDefinitionId =
			testGetListTypeDefinitionListTypeEntriesPage_getIrrelevantListTypeDefinitionId();

		Page<ListTypeEntry> page =
			listTypeEntryResource.getListTypeDefinitionListTypeEntriesPage(
				listTypeDefinitionId, null, null, null, Pagination.of(1, 10),
				null);

		long totalCount = page.getTotalCount();

		if (irrelevantListTypeDefinitionId != null) {
			ListTypeEntry irrelevantListTypeEntry =
				testGetListTypeDefinitionListTypeEntriesPage_addListTypeEntry(
					irrelevantListTypeDefinitionId,
					randomIrrelevantListTypeEntry());

			page =
				listTypeEntryResource.getListTypeDefinitionListTypeEntriesPage(
					irrelevantListTypeDefinitionId, null, null, null,
					Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantListTypeEntry, (List<ListTypeEntry>)page.getItems());
			assertValid(
				page,
				testGetListTypeDefinitionListTypeEntriesPage_getExpectedActions(
					irrelevantListTypeDefinitionId));
		}

		ListTypeEntry listTypeEntry1 =
			testGetListTypeDefinitionListTypeEntriesPage_addListTypeEntry(
				listTypeDefinitionId, randomListTypeEntry());

		ListTypeEntry listTypeEntry2 =
			testGetListTypeDefinitionListTypeEntriesPage_addListTypeEntry(
				listTypeDefinitionId, randomListTypeEntry());

		page = listTypeEntryResource.getListTypeDefinitionListTypeEntriesPage(
			listTypeDefinitionId, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(listTypeEntry1, (List<ListTypeEntry>)page.getItems());
		assertContains(listTypeEntry2, (List<ListTypeEntry>)page.getItems());
		assertValid(
			page,
			testGetListTypeDefinitionListTypeEntriesPage_getExpectedActions(
				listTypeDefinitionId));

		listTypeEntryResource.deleteListTypeEntry(listTypeEntry1.getId());

		listTypeEntryResource.deleteListTypeEntry(listTypeEntry2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetListTypeDefinitionListTypeEntriesPage_getExpectedActions(
				Long listTypeDefinitionId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-admin-list-type/v1.0/list-type-definitions/{listTypeDefinitionId}/list-type-entries/batch".
				replace(
					"{listTypeDefinitionId}",
					String.valueOf(listTypeDefinitionId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetListTypeDefinitionListTypeEntriesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long listTypeDefinitionId =
			testGetListTypeDefinitionListTypeEntriesPage_getListTypeDefinitionId();

		ListTypeEntry listTypeEntry1 = randomListTypeEntry();

		listTypeEntry1 =
			testGetListTypeDefinitionListTypeEntriesPage_addListTypeEntry(
				listTypeDefinitionId, listTypeEntry1);

		for (EntityField entityField : entityFields) {
			Page<ListTypeEntry> page =
				listTypeEntryResource.getListTypeDefinitionListTypeEntriesPage(
					listTypeDefinitionId, null, null,
					getFilterString(entityField, "between", listTypeEntry1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(listTypeEntry1),
				(List<ListTypeEntry>)page.getItems());
		}
	}

	@Test
	public void testGetListTypeDefinitionListTypeEntriesPageWithFilterDoubleEquals()
		throws Exception {

		testGetListTypeDefinitionListTypeEntriesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetListTypeDefinitionListTypeEntriesPageWithFilterStringContains()
		throws Exception {

		testGetListTypeDefinitionListTypeEntriesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetListTypeDefinitionListTypeEntriesPageWithFilterStringEquals()
		throws Exception {

		testGetListTypeDefinitionListTypeEntriesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetListTypeDefinitionListTypeEntriesPageWithFilterStringStartsWith()
		throws Exception {

		testGetListTypeDefinitionListTypeEntriesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetListTypeDefinitionListTypeEntriesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long listTypeDefinitionId =
			testGetListTypeDefinitionListTypeEntriesPage_getListTypeDefinitionId();

		ListTypeEntry listTypeEntry1 =
			testGetListTypeDefinitionListTypeEntriesPage_addListTypeEntry(
				listTypeDefinitionId, randomListTypeEntry());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ListTypeEntry listTypeEntry2 =
			testGetListTypeDefinitionListTypeEntriesPage_addListTypeEntry(
				listTypeDefinitionId, randomListTypeEntry());

		for (EntityField entityField : entityFields) {
			Page<ListTypeEntry> page =
				listTypeEntryResource.getListTypeDefinitionListTypeEntriesPage(
					listTypeDefinitionId, null, null,
					getFilterString(entityField, operator, listTypeEntry1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(listTypeEntry1),
				(List<ListTypeEntry>)page.getItems());
		}
	}

	@Test
	public void testGetListTypeDefinitionListTypeEntriesPageWithPagination()
		throws Exception {

		Long listTypeDefinitionId =
			testGetListTypeDefinitionListTypeEntriesPage_getListTypeDefinitionId();

		Page<ListTypeEntry> listTypeEntriesPage =
			listTypeEntryResource.getListTypeDefinitionListTypeEntriesPage(
				listTypeDefinitionId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			listTypeEntriesPage.getTotalCount());

		ListTypeEntry listTypeEntry1 =
			testGetListTypeDefinitionListTypeEntriesPage_addListTypeEntry(
				listTypeDefinitionId, randomListTypeEntry());

		ListTypeEntry listTypeEntry2 =
			testGetListTypeDefinitionListTypeEntriesPage_addListTypeEntry(
				listTypeDefinitionId, randomListTypeEntry());

		ListTypeEntry listTypeEntry3 =
			testGetListTypeDefinitionListTypeEntriesPage_addListTypeEntry(
				listTypeDefinitionId, randomListTypeEntry());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ListTypeEntry> page1 =
				listTypeEntryResource.getListTypeDefinitionListTypeEntriesPage(
					listTypeDefinitionId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				listTypeEntry1, (List<ListTypeEntry>)page1.getItems());

			Page<ListTypeEntry> page2 =
				listTypeEntryResource.getListTypeDefinitionListTypeEntriesPage(
					listTypeDefinitionId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				listTypeEntry2, (List<ListTypeEntry>)page2.getItems());

			Page<ListTypeEntry> page3 =
				listTypeEntryResource.getListTypeDefinitionListTypeEntriesPage(
					listTypeDefinitionId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				listTypeEntry3, (List<ListTypeEntry>)page3.getItems());
		}
		else {
			Page<ListTypeEntry> page1 =
				listTypeEntryResource.getListTypeDefinitionListTypeEntriesPage(
					listTypeDefinitionId, null, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<ListTypeEntry> listTypeEntries1 =
				(List<ListTypeEntry>)page1.getItems();

			Assert.assertEquals(
				listTypeEntries1.toString(), totalCount + 2,
				listTypeEntries1.size());

			Page<ListTypeEntry> page2 =
				listTypeEntryResource.getListTypeDefinitionListTypeEntriesPage(
					listTypeDefinitionId, null, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ListTypeEntry> listTypeEntries2 =
				(List<ListTypeEntry>)page2.getItems();

			Assert.assertEquals(
				listTypeEntries2.toString(), 1, listTypeEntries2.size());

			Page<ListTypeEntry> page3 =
				listTypeEntryResource.getListTypeDefinitionListTypeEntriesPage(
					listTypeDefinitionId, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				listTypeEntry1, (List<ListTypeEntry>)page3.getItems());
			assertContains(
				listTypeEntry2, (List<ListTypeEntry>)page3.getItems());
			assertContains(
				listTypeEntry3, (List<ListTypeEntry>)page3.getItems());
		}
	}

	@Test
	public void testGetListTypeDefinitionListTypeEntriesPageWithSortDateTime()
		throws Exception {

		testGetListTypeDefinitionListTypeEntriesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, listTypeEntry1, listTypeEntry2) -> {
				BeanTestUtil.setProperty(
					listTypeEntry1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetListTypeDefinitionListTypeEntriesPageWithSortDouble()
		throws Exception {

		testGetListTypeDefinitionListTypeEntriesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, listTypeEntry1, listTypeEntry2) -> {
				BeanTestUtil.setProperty(
					listTypeEntry1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					listTypeEntry2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetListTypeDefinitionListTypeEntriesPageWithSortInteger()
		throws Exception {

		testGetListTypeDefinitionListTypeEntriesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, listTypeEntry1, listTypeEntry2) -> {
				BeanTestUtil.setProperty(
					listTypeEntry1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					listTypeEntry2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetListTypeDefinitionListTypeEntriesPageWithSortString()
		throws Exception {

		testGetListTypeDefinitionListTypeEntriesPageWithSort(
			EntityField.Type.STRING,
			(entityField, listTypeEntry1, listTypeEntry2) -> {
				Class<?> clazz = listTypeEntry1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						listTypeEntry1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						listTypeEntry2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						listTypeEntry1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						listTypeEntry2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						listTypeEntry1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						listTypeEntry2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetListTypeDefinitionListTypeEntriesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, ListTypeEntry, ListTypeEntry, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long listTypeDefinitionId =
			testGetListTypeDefinitionListTypeEntriesPage_getListTypeDefinitionId();

		ListTypeEntry listTypeEntry1 = randomListTypeEntry();
		ListTypeEntry listTypeEntry2 = randomListTypeEntry();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, listTypeEntry1, listTypeEntry2);
		}

		listTypeEntry1 =
			testGetListTypeDefinitionListTypeEntriesPage_addListTypeEntry(
				listTypeDefinitionId, listTypeEntry1);

		listTypeEntry2 =
			testGetListTypeDefinitionListTypeEntriesPage_addListTypeEntry(
				listTypeDefinitionId, listTypeEntry2);

		Page<ListTypeEntry> page =
			listTypeEntryResource.getListTypeDefinitionListTypeEntriesPage(
				listTypeDefinitionId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ListTypeEntry> ascPage =
				listTypeEntryResource.getListTypeDefinitionListTypeEntriesPage(
					listTypeDefinitionId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				listTypeEntry1, (List<ListTypeEntry>)ascPage.getItems());
			assertContains(
				listTypeEntry2, (List<ListTypeEntry>)ascPage.getItems());

			Page<ListTypeEntry> descPage =
				listTypeEntryResource.getListTypeDefinitionListTypeEntriesPage(
					listTypeDefinitionId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				listTypeEntry2, (List<ListTypeEntry>)descPage.getItems());
			assertContains(
				listTypeEntry1, (List<ListTypeEntry>)descPage.getItems());
		}
	}

	protected ListTypeEntry
			testGetListTypeDefinitionListTypeEntriesPage_addListTypeEntry(
				Long listTypeDefinitionId, ListTypeEntry listTypeEntry)
		throws Exception {

		return listTypeEntryResource.postListTypeDefinitionListTypeEntry(
			listTypeDefinitionId, listTypeEntry);
	}

	protected Long
			testGetListTypeDefinitionListTypeEntriesPage_getListTypeDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetListTypeDefinitionListTypeEntriesPage_getIrrelevantListTypeDefinitionId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetListTypeDefinitionListTypeEntriesPage()
		throws Exception {

		Long listTypeDefinitionId =
			testGetListTypeDefinitionListTypeEntriesPage_getListTypeDefinitionId();

		GraphQLField graphQLField = new GraphQLField(
			"listTypeDefinitionListTypeEntries",
			new HashMap<String, Object>() {
				{
					put("listTypeDefinitionId", listTypeDefinitionId);
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject listTypeDefinitionListTypeEntriesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/listTypeDefinitionListTypeEntries");

		long totalCount = listTypeDefinitionListTypeEntriesJSONObject.getLong(
			"totalCount");

		ListTypeEntry listTypeEntry1 =
			testGraphQLListTypeDefinitionListTypeEntry_addListTypeEntry(
				listTypeDefinitionId, randomListTypeEntry());

		ListTypeEntry listTypeEntry2 =
			testGraphQLListTypeDefinitionListTypeEntry_addListTypeEntry(
				listTypeDefinitionId, randomListTypeEntry());

		listTypeDefinitionListTypeEntriesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/listTypeDefinitionListTypeEntries");

		Assert.assertEquals(
			totalCount + 2,
			listTypeDefinitionListTypeEntriesJSONObject.getLong("totalCount"));

		assertContains(
			listTypeEntry1,
			Arrays.asList(
				ListTypeEntrySerDes.toDTOs(
					listTypeDefinitionListTypeEntriesJSONObject.getString(
						"items"))));
		assertContains(
			listTypeEntry2,
			Arrays.asList(
				ListTypeEntrySerDes.toDTOs(
					listTypeDefinitionListTypeEntriesJSONObject.getString(
						"items"))));

		// Using the namespace headlessAdminListType_v1_0

		listTypeDefinitionListTypeEntriesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminListType_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/headlessAdminListType_v1_0",
				"JSONObject/listTypeDefinitionListTypeEntries");

		Assert.assertEquals(
			totalCount + 2,
			listTypeDefinitionListTypeEntriesJSONObject.getLong("totalCount"));

		assertContains(
			listTypeEntry1,
			Arrays.asList(
				ListTypeEntrySerDes.toDTOs(
					listTypeDefinitionListTypeEntriesJSONObject.getString(
						"items"))));
		assertContains(
			listTypeEntry2,
			Arrays.asList(
				ListTypeEntrySerDes.toDTOs(
					listTypeDefinitionListTypeEntriesJSONObject.getString(
						"items"))));
	}

	@Test
	public void testGetListTypeEntry() throws Exception {
		ListTypeEntry postListTypeEntry =
			testGetListTypeEntry_addListTypeEntry();

		ListTypeEntry getListTypeEntry = listTypeEntryResource.getListTypeEntry(
			postListTypeEntry.getId());

		assertEquals(postListTypeEntry, getListTypeEntry);
		assertValid(getListTypeEntry);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		ListTypeEntry postListTypeEntry =
			testGetListTypeEntry_addListTypeEntry();

		ListTypeEntry getListTypeEntry = listTypeEntryResource.getListTypeEntry(
			postListTypeEntry.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.admin.list.type.dto.v1_0.ListTypeEntry"
			).acceptLanguage(
				new AcceptLanguage() {

					@Override
					public List<Locale> getLocales() {
						return Arrays.asList(LocaleUtil.getDefault());
					}

					@Override
					public String getPreferredLanguageId() {
						return LocaleUtil.toLanguageId(LocaleUtil.getDefault());
					}

					@Override
					public Locale getPreferredLocale() {
						return LocaleUtil.getDefault();
					}

				}
			).groupLocalService(
				_groupLocalService
			).httpServletRequest(
				testVulcanCRUDItemDelegate_getHttpServletRequest()
			).httpServletResponse(
				new MockHttpServletResponse()
			).resourceActionLocalService(
				_resourceActionLocalService
			).resourcePermissionLocalService(
				_resourcePermissionLocalService
			).roleLocalService(
				_roleLocalService
			).scopeChecker(
				_scopeChecker
			).uriInfo(
				testVulcanCRUDItemDelegate_getUriInfo()
			).user(
				testVulcanCRUDItemDelegate_getUser()
			).build();

		Object item = vulcanCRUDItemDelegate.getItem(postListTypeEntry.getId());

		assertEquals(
			getListTypeEntry, ListTypeEntrySerDes.toDTO(item.toString()));
	}

	protected HttpServletRequest
		testVulcanCRUDItemDelegate_getHttpServletRequest() {

		return new MockHttpServletRequest() {

			@Override
			public StringBuffer getRequestURL() {
				return new StringBuffer(
					StringBundler.concat(
						"http://localhost:8080/o/v1.0/",
						RandomTestUtil.randomString(), "/",
						RandomTestUtil.randomString()));
			}

		};
	}

	protected UriInfo testVulcanCRUDItemDelegate_getUriInfo() {
		String applicationPath = RandomTestUtil.randomString() + "/";
		String resourcePath = RandomTestUtil.randomString();

		return new UriInfo() {

			@Override
			public String getPath() {
				return resourcePath;
			}

			@Override
			public String getPath(boolean decode) {
				return getPath();
			}

			@Override
			public List<PathSegment> getPathSegments() {
				return Collections.emptyList();
			}

			@Override
			public List<PathSegment> getPathSegments(boolean decode) {
				return getPathSegments();
			}

			@Override
			public URI getRequestUri() {
				return URI.create(
					"http://localhost:8080/o/" + applicationPath +
						resourcePath);
			}

			@Override
			public UriBuilder getRequestUriBuilder() {
				return UriBuilder.fromUri(getRequestUri());
			}

			@Override
			public URI getAbsolutePath() {
				return getRequestUri();
			}

			@Override
			public UriBuilder getAbsolutePathBuilder() {
				return getRequestUriBuilder();
			}

			@Override
			public URI getBaseUri() {
				return URI.create("http://localhost:8080/o/" + applicationPath);
			}

			@Override
			public UriBuilder getBaseUriBuilder() {
				return UriBuilder.fromUri(getBaseUri());
			}

			@Override
			public MultivaluedMap<String, String> getPathParameters() {
				return new MultivaluedHashMap<>();
			}

			@Override
			public MultivaluedMap<String, String> getPathParameters(
				boolean decode) {

				return getPathParameters();
			}

			@Override
			public MultivaluedMap<String, String> getQueryParameters() {
				return new MultivaluedHashMap<>();
			}

			@Override
			public MultivaluedMap<String, String> getQueryParameters(
				boolean decode) {

				return getQueryParameters();
			}

			@Override
			public List<String> getMatchedURIs() {
				return Collections.emptyList();
			}

			@Override
			public List<String> getMatchedURIs(boolean decode) {
				return getMatchedURIs();
			}

			@Override
			public List<Object> getMatchedResources() {
				return Collections.emptyList();
			}

			@Override
			public URI resolve(URI requestUri) {
				return getBaseUri().resolve(requestUri);
			}

			@Override
			public URI relativize(URI uri) {
				return getBaseUri().relativize(uri);
			}

		};
	}

	protected com.liferay.portal.kernel.model.User
		testVulcanCRUDItemDelegate_getUser() {

		return _testCompanyAdminUser;
	}

	protected ListTypeEntry testGetListTypeEntry_addListTypeEntry()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetListTypeEntry() throws Exception {
		ListTypeEntry listTypeEntry =
			testGraphQLGetListTypeEntry_addListTypeEntry();

		// No namespace

		Assert.assertTrue(
			equals(
				listTypeEntry,
				ListTypeEntrySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"listTypeEntry",
								new HashMap<String, Object>() {
									{
										put(
											"listTypeEntryId",
											listTypeEntry.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/listTypeEntry"))));

		// Using the namespace headlessAdminListType_v1_0

		Assert.assertTrue(
			equals(
				listTypeEntry,
				ListTypeEntrySerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminListType_v1_0",
								new GraphQLField(
									"listTypeEntry",
									new HashMap<String, Object>() {
										{
											put(
												"listTypeEntryId",
												listTypeEntry.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessAdminListType_v1_0",
						"Object/listTypeEntry"))));
	}

	@Test
	public void testGraphQLGetListTypeEntryNotFound() throws Exception {
		Long irrelevantListTypeEntryId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"listTypeEntry",
						new HashMap<String, Object>() {
							{
								put(
									"listTypeEntryId",
									irrelevantListTypeEntryId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessAdminListType_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminListType_v1_0",
						new GraphQLField(
							"listTypeEntry",
							new HashMap<String, Object>() {
								{
									put(
										"listTypeEntryId",
										irrelevantListTypeEntryId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ListTypeEntry testGraphQLGetListTypeEntry_addListTypeEntry()
		throws Exception {

		return testGraphQLListTypeEntry_addListTypeEntry();
	}

	@Test
	public void testPostListTypeDefinitionByExternalReferenceCodeListTypeEntry()
		throws Exception {

		ListTypeEntry randomListTypeEntry = randomListTypeEntry();

		ListTypeEntry postListTypeEntry =
			testPostListTypeDefinitionByExternalReferenceCodeListTypeEntry_addListTypeEntry(
				randomListTypeEntry);

		assertEquals(randomListTypeEntry, postListTypeEntry);
		assertValid(postListTypeEntry);
	}

	protected ListTypeEntry
			testPostListTypeDefinitionByExternalReferenceCodeListTypeEntry_addListTypeEntry(
				ListTypeEntry listTypeEntry)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostListTypeDefinitionByExternalReferenceCodeListTypeEntry()
		throws Exception {

		ListTypeEntry randomListTypeEntry = randomListTypeEntry();

		ListTypeEntry listTypeEntry =
			testGraphQLListTypeDefinitionListTypeEntry_addListTypeEntry(
				testGraphQLPostListTypeDefinitionByExternalReferenceCodeListTypeEntry_getListTypeDefinitionId(),
				randomListTypeEntry);

		Assert.assertTrue(equals(randomListTypeEntry, listTypeEntry));
	}

	protected Long
			testGraphQLPostListTypeDefinitionByExternalReferenceCodeListTypeEntry_getListTypeDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostListTypeDefinitionListTypeEntry() throws Exception {
		ListTypeEntry randomListTypeEntry = randomListTypeEntry();

		ListTypeEntry postListTypeEntry =
			testPostListTypeDefinitionListTypeEntry_addListTypeEntry(
				randomListTypeEntry);

		assertEquals(randomListTypeEntry, postListTypeEntry);
		assertValid(postListTypeEntry);
	}

	protected ListTypeEntry
			testPostListTypeDefinitionListTypeEntry_addListTypeEntry(
				ListTypeEntry listTypeEntry)
		throws Exception {

		return listTypeEntryResource.postListTypeDefinitionListTypeEntry(
			testGetListTypeDefinitionListTypeEntriesPage_getListTypeDefinitionId(),
			listTypeEntry);
	}

	@Test
	public void testGraphQLPostListTypeDefinitionListTypeEntry()
		throws Exception {

		ListTypeEntry randomListTypeEntry = randomListTypeEntry();

		ListTypeEntry listTypeEntry =
			testGraphQLListTypeDefinitionListTypeEntry_addListTypeEntry(
				testGraphQLPostListTypeDefinitionListTypeEntry_getListTypeDefinitionId(),
				randomListTypeEntry);

		Assert.assertTrue(equals(randomListTypeEntry, listTypeEntry));
	}

	protected Long
			testGraphQLPostListTypeDefinitionListTypeEntry_getListTypeDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutListTypeEntry() throws Exception {
		ListTypeEntry postListTypeEntry =
			testPutListTypeEntry_addListTypeEntry();

		ListTypeEntry randomListTypeEntry = randomListTypeEntry();

		ListTypeEntry putListTypeEntry = listTypeEntryResource.putListTypeEntry(
			postListTypeEntry.getId(), randomListTypeEntry);

		assertEquals(randomListTypeEntry, putListTypeEntry);
		assertValid(putListTypeEntry);

		ListTypeEntry getListTypeEntry = listTypeEntryResource.getListTypeEntry(
			putListTypeEntry.getId());

		assertEquals(randomListTypeEntry, getListTypeEntry);
		assertValid(getListTypeEntry);
	}

	protected ListTypeEntry testPutListTypeEntry_addListTypeEntry()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ListTypeEntry listTypeEntry1 =
			testBatchEngineDeleteImportTask_addListTypeEntry();

		testBatchEngineDeleteImportTask_deleteListTypeEntry(
			200, null, listTypeEntry1.getId());

		assertHttpResponseStatusCode(
			404,
			listTypeEntryResource.getListTypeEntryHttpResponse(
				listTypeEntry1.getId()));
	}

	protected ListTypeEntry testBatchEngineDeleteImportTask_addListTypeEntry()
		throws Exception {

		return testDeleteListTypeEntry_addListTypeEntry();
	}

	protected void testBatchEngineDeleteImportTask_deleteListTypeEntry(
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
				"com.liferay.headless.admin.list.type.dto.v1_0.ListTypeEntry",
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

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected ListTypeEntry testGraphQLListTypeEntry_addListTypeEntry()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ListTypeEntry
			testGraphQLListTypeDefinitionListTypeEntry_addListTypeEntry()
		throws Exception {

		return testGraphQLListTypeDefinitionListTypeEntry_addListTypeEntry(
			testGraphQLListTypeDefinitionListTypeEntry_getListTypeDefinitionId(),
			randomListTypeEntry());
	}

	protected Long
			testGraphQLListTypeDefinitionListTypeEntry_getListTypeDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ListTypeEntry
			testGraphQLListTypeDefinitionListTypeEntry_addListTypeEntry(
				Long listTypeDefinitionId, ListTypeEntry listTypeEntry)
		throws Exception {

		JSONDeserializer<ListTypeEntry> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(ListTypeEntry.class)) {

			if (getGraphQLValue(field.get(listTypeEntry)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(listTypeEntry)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createListTypeDefinitionListTypeEntry",
						new HashMap<String, Object>() {
							{
								put(
									"listTypeDefinitionId",
									listTypeDefinitionId);
								put("listTypeEntry", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data",
				"JSONObject/createListTypeDefinitionListTypeEntry"),
			ListTypeEntry.class);
	}

	protected String getGraphQLValue(Object value) throws Exception {
		if (value == null) {
			return null;
		}
		else if (value instanceof Boolean || value instanceof Number) {
			return value.toString();
		}
		else if (value instanceof Date date) {
			return "\"" +
				DateUtil.getDate(
					date, "yyyy-MM-dd'T'HH:mm:ss'Z'", LocaleUtil.getDefault(),
					TimeZone.getTimeZone("UTC")) + "\"";
		}
		else if (value instanceof Enum<?> enm) {
			return enm.name();
		}
		else if (value instanceof Map<?, ?> map) {
			List<String> entries = new ArrayList<>();

			for (Map.Entry<?, ?> entry : map.entrySet()) {
				String graphQLValue = getGraphQLValue(entry.getValue());

				if (graphQLValue != null) {
					entries.add(entry.getKey() + ": " + graphQLValue);
				}
			}

			return "{" + String.join(", ", entries) + "}";
		}
		else if (value instanceof Object[] array) {
			List<String> entries = new ArrayList<>();

			for (Object entry : array) {
				String graphQLValue = getGraphQLValue(entry);

				if (graphQLValue != null) {
					entries.add(graphQLValue);
				}
			}

			return "[" + String.join(", ", entries) + "]";
		}
		else if (value instanceof String) {
			return "\"" + value + "\"";
		}
		else {
			List<String> entries = new ArrayList<>();

			Class<?> clazz = value.getClass();
			java.lang.reflect.Field[] declaredFields = getDeclaredFields(clazz);

			if (declaredFields.length == 0) {
				declaredFields = getDeclaredFields(clazz.getSuperclass());
			}

			for (java.lang.reflect.Field field : declaredFields) {
				String graphQLValue = getGraphQLValue(field.get(value));

				if (graphQLValue != null) {
					entries.add(field.getName() + ": " + graphQLValue);
				}
			}

			return "{" + String.join(", ", entries) + "}";
		}
	}

	protected void assertContains(
		ListTypeEntry listTypeEntry, List<ListTypeEntry> listTypeEntries) {

		boolean contains = false;

		for (ListTypeEntry item : listTypeEntries) {
			if (equals(listTypeEntry, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			listTypeEntries + " does not contain " + listTypeEntry, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ListTypeEntry listTypeEntry1, ListTypeEntry listTypeEntry2) {

		Assert.assertTrue(
			listTypeEntry1 + " does not equal " + listTypeEntry2,
			equals(listTypeEntry1, listTypeEntry2));
	}

	protected void assertEquals(
		List<ListTypeEntry> listTypeEntries1,
		List<ListTypeEntry> listTypeEntries2) {

		Assert.assertEquals(listTypeEntries1.size(), listTypeEntries2.size());

		for (int i = 0; i < listTypeEntries1.size(); i++) {
			ListTypeEntry listTypeEntry1 = listTypeEntries1.get(i);
			ListTypeEntry listTypeEntry2 = listTypeEntries2.get(i);

			assertEquals(listTypeEntry1, listTypeEntry2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ListTypeEntry> listTypeEntries1,
		List<ListTypeEntry> listTypeEntries2) {

		Assert.assertEquals(listTypeEntries1.size(), listTypeEntries2.size());

		for (ListTypeEntry listTypeEntry1 : listTypeEntries1) {
			boolean contains = false;

			for (ListTypeEntry listTypeEntry2 : listTypeEntries2) {
				if (equals(listTypeEntry1, listTypeEntry2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				listTypeEntries2 + " does not contain " + listTypeEntry1,
				contains);
		}
	}

	protected void assertValid(ListTypeEntry listTypeEntry) throws Exception {
		boolean valid = true;

		if (listTypeEntry.getDateCreated() == null) {
			valid = false;
		}

		if (listTypeEntry.getDateModified() == null) {
			valid = false;
		}

		if (listTypeEntry.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (listTypeEntry.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (listTypeEntry.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (listTypeEntry.getKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (listTypeEntry.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (listTypeEntry.getName_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("system", additionalAssertFieldName)) {
				if (listTypeEntry.getSystem() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (listTypeEntry.getType() == null) {
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

	protected void assertValid(Page<ListTypeEntry> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ListTypeEntry> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ListTypeEntry> listTypeEntries = page.getItems();

		int size = listTypeEntries.size();

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
					com.liferay.headless.admin.list.type.dto.v1_0.ListTypeEntry.
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
		ListTypeEntry listTypeEntry1, ListTypeEntry listTypeEntry2) {

		if (listTypeEntry1 == listTypeEntry2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)listTypeEntry1.getActions(),
						(Map)listTypeEntry2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						listTypeEntry1.getDateCreated(),
						listTypeEntry2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						listTypeEntry1.getDateModified(),
						listTypeEntry2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						listTypeEntry1.getExternalReferenceCode(),
						listTypeEntry2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						listTypeEntry1.getId(), listTypeEntry2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						listTypeEntry1.getKey(), listTypeEntry2.getKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						listTypeEntry1.getName(), listTypeEntry2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)listTypeEntry1.getName_i18n(),
						(Map)listTypeEntry2.getName_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("system", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						listTypeEntry1.getSystem(),
						listTypeEntry2.getSystem())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						listTypeEntry1.getType(), listTypeEntry2.getType())) {

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

		if (!(_listTypeEntryResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_listTypeEntryResource;

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
		EntityField entityField, String operator, ListTypeEntry listTypeEntry) {

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

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = listTypeEntry.getDateCreated();

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

				sb.append(_format.format(listTypeEntry.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = listTypeEntry.getDateModified();

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

				sb.append(_format.format(listTypeEntry.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = listTypeEntry.getExternalReferenceCode();

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

		if (entityFieldName.equals("key")) {
			Object object = listTypeEntry.getKey();

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

		if (entityFieldName.equals("name")) {
			Object object = listTypeEntry.getName();

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

		if (entityFieldName.equals("system")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("type")) {
			Object object = listTypeEntry.getType();

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

	protected ListTypeEntry randomListTypeEntry() throws Exception {
		return new ListTypeEntry() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				system = RandomTestUtil.randomBoolean();
				type = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected ListTypeEntry randomIrrelevantListTypeEntry() throws Exception {
		ListTypeEntry randomIrrelevantListTypeEntry = randomListTypeEntry();

		return randomIrrelevantListTypeEntry;
	}

	protected ListTypeEntry randomPatchListTypeEntry() throws Exception {
		return randomListTypeEntry();
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

	protected ListTypeEntryResource listTypeEntryResource;
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
		LogFactoryUtil.getLog(BaseListTypeEntryResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.admin.list.type.resource.v1_0.ListTypeEntryResource
			_listTypeEntryResource;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private ScopeChecker _scopeChecker;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private VulcanCRUDItemDelegateBuilderRegistry
		_vulcanCRUDItemDelegateBuilderRegistry;

}