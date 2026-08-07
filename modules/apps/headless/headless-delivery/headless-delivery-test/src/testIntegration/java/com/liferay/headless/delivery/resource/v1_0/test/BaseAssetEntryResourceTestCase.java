/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.delivery.client.dto.v1_0.AssetEntry;
import com.liferay.headless.delivery.client.dto.v1_0.Field;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.resource.v1_0.AssetEntryResource;
import com.liferay.headless.delivery.client.serdes.v1_0.AssetEntrySerDes;
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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public abstract class BaseAssetEntryResourceTestCase {

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

		_assetEntryResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		assetEntryResource = AssetEntryResource.builder(
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

		AssetEntry assetEntry1 = randomAssetEntry();

		String json = objectMapper.writeValueAsString(assetEntry1);

		AssetEntry assetEntry2 = AssetEntrySerDes.toDTO(json);

		Assert.assertTrue(equals(assetEntry1, assetEntry2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		AssetEntry assetEntry = randomAssetEntry();

		String json1 = objectMapper.writeValueAsString(assetEntry);
		String json2 = AssetEntrySerDes.toJSON(assetEntry);

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

		AssetEntry assetEntry = randomAssetEntry();

		assetEntry.setAssetType(regex);
		assetEntry.setClassName(regex);
		assetEntry.setDescription(regex);
		assetEntry.setGroupDescriptiveName(regex);
		assetEntry.setTitle(regex);

		String json = AssetEntrySerDes.toJSON(assetEntry);

		Assert.assertFalse(json.contains(regex));

		assetEntry = AssetEntrySerDes.toDTO(json);

		Assert.assertEquals(regex, assetEntry.getAssetType());
		Assert.assertEquals(regex, assetEntry.getClassName());
		Assert.assertEquals(regex, assetEntry.getDescription());
		Assert.assertEquals(regex, assetEntry.getGroupDescriptiveName());
		Assert.assertEquals(regex, assetEntry.getTitle());
	}

	@Test
	public void testGetAssetEntriesPage() throws Exception {
		Page<AssetEntry> page = assetEntryResource.getAssetEntriesPage(
			null, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		AssetEntry assetEntry1 = testGetAssetEntriesPage_addAssetEntry(
			randomAssetEntry());

		AssetEntry assetEntry2 = testGetAssetEntriesPage_addAssetEntry(
			randomAssetEntry());

		page = assetEntryResource.getAssetEntriesPage(
			null, null, null, null, Pagination.of(1, (int)totalCount + 2),
			null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(assetEntry1, (List<AssetEntry>)page.getItems());
		assertContains(assetEntry2, (List<AssetEntry>)page.getItems());
		assertValid(page, testGetAssetEntriesPage_getExpectedActions());
	}

	protected Map<String, Map<String, String>>
			testGetAssetEntriesPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAssetEntriesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		AssetEntry assetEntry1 = randomAssetEntry();

		assetEntry1 = testGetAssetEntriesPage_addAssetEntry(assetEntry1);

		for (EntityField entityField : entityFields) {
			Page<AssetEntry> page = assetEntryResource.getAssetEntriesPage(
				null, null, null,
				getFilterString(entityField, "between", assetEntry1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(assetEntry1),
				(List<AssetEntry>)page.getItems());
		}
	}

	@Test
	public void testGetAssetEntriesPageWithFilterDoubleEquals()
		throws Exception {

		testGetAssetEntriesPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetAssetEntriesPageWithFilterStringContains()
		throws Exception {

		testGetAssetEntriesPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetAssetEntriesPageWithFilterStringEquals()
		throws Exception {

		testGetAssetEntriesPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetAssetEntriesPageWithFilterStringStartsWith()
		throws Exception {

		testGetAssetEntriesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetAssetEntriesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		AssetEntry assetEntry1 = testGetAssetEntriesPage_addAssetEntry(
			randomAssetEntry());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AssetEntry assetEntry2 = testGetAssetEntriesPage_addAssetEntry(
			randomAssetEntry());

		for (EntityField entityField : entityFields) {
			Page<AssetEntry> page = assetEntryResource.getAssetEntriesPage(
				null, null, null,
				getFilterString(entityField, operator, assetEntry1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(assetEntry1),
				(List<AssetEntry>)page.getItems());
		}
	}

	@Test
	public void testGetAssetEntriesPageWithPagination() throws Exception {
		Page<AssetEntry> assetEntriesPage =
			assetEntryResource.getAssetEntriesPage(
				null, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			assetEntriesPage.getTotalCount());

		AssetEntry assetEntry1 = testGetAssetEntriesPage_addAssetEntry(
			randomAssetEntry());

		AssetEntry assetEntry2 = testGetAssetEntriesPage_addAssetEntry(
			randomAssetEntry());

		AssetEntry assetEntry3 = testGetAssetEntriesPage_addAssetEntry(
			randomAssetEntry());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<AssetEntry> page1 = assetEntryResource.getAssetEntriesPage(
				null, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(assetEntry1, (List<AssetEntry>)page1.getItems());

			Page<AssetEntry> page2 = assetEntryResource.getAssetEntriesPage(
				null, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(assetEntry2, (List<AssetEntry>)page2.getItems());

			Page<AssetEntry> page3 = assetEntryResource.getAssetEntriesPage(
				null, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(assetEntry3, (List<AssetEntry>)page3.getItems());
		}
		else {
			Page<AssetEntry> page1 = assetEntryResource.getAssetEntriesPage(
				null, null, null, null, Pagination.of(1, totalCount + 2), null);

			List<AssetEntry> assetEntries1 = (List<AssetEntry>)page1.getItems();

			Assert.assertEquals(
				assetEntries1.toString(), totalCount + 2, assetEntries1.size());

			Page<AssetEntry> page2 = assetEntryResource.getAssetEntriesPage(
				null, null, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<AssetEntry> assetEntries2 = (List<AssetEntry>)page2.getItems();

			Assert.assertEquals(
				assetEntries2.toString(), 1, assetEntries2.size());

			Page<AssetEntry> page3 = assetEntryResource.getAssetEntriesPage(
				null, null, null, null, Pagination.of(1, (int)totalCount + 3),
				null);

			assertContains(assetEntry1, (List<AssetEntry>)page3.getItems());
			assertContains(assetEntry2, (List<AssetEntry>)page3.getItems());
			assertContains(assetEntry3, (List<AssetEntry>)page3.getItems());
		}
	}

	@Test
	public void testGetAssetEntriesPageWithSortDateTime() throws Exception {
		testGetAssetEntriesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, assetEntry1, assetEntry2) -> {
				BeanTestUtil.setProperty(
					assetEntry1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAssetEntriesPageWithSortDouble() throws Exception {
		testGetAssetEntriesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, assetEntry1, assetEntry2) -> {
				BeanTestUtil.setProperty(
					assetEntry1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					assetEntry2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAssetEntriesPageWithSortInteger() throws Exception {
		testGetAssetEntriesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, assetEntry1, assetEntry2) -> {
				BeanTestUtil.setProperty(assetEntry1, entityField.getName(), 0);
				BeanTestUtil.setProperty(assetEntry2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAssetEntriesPageWithSortString() throws Exception {
		testGetAssetEntriesPageWithSort(
			EntityField.Type.STRING,
			(entityField, assetEntry1, assetEntry2) -> {
				Class<?> clazz = assetEntry1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						assetEntry1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						assetEntry2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						assetEntry1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						assetEntry2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						assetEntry1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						assetEntry2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetAssetEntriesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, AssetEntry, AssetEntry, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		AssetEntry assetEntry1 = randomAssetEntry();
		AssetEntry assetEntry2 = randomAssetEntry();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, assetEntry1, assetEntry2);
		}

		assetEntry1 = testGetAssetEntriesPage_addAssetEntry(assetEntry1);

		assetEntry2 = testGetAssetEntriesPage_addAssetEntry(assetEntry2);

		Page<AssetEntry> page = assetEntryResource.getAssetEntriesPage(
			null, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<AssetEntry> ascPage = assetEntryResource.getAssetEntriesPage(
				null, null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(assetEntry1, (List<AssetEntry>)ascPage.getItems());
			assertContains(assetEntry2, (List<AssetEntry>)ascPage.getItems());

			Page<AssetEntry> descPage = assetEntryResource.getAssetEntriesPage(
				null, null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(assetEntry2, (List<AssetEntry>)descPage.getItems());
			assertContains(assetEntry1, (List<AssetEntry>)descPage.getItems());
		}
	}

	protected AssetEntry testGetAssetEntriesPage_addAssetEntry(
			AssetEntry assetEntry)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertContains(
		AssetEntry assetEntry, List<AssetEntry> assetEntries) {

		boolean contains = false;

		for (AssetEntry item : assetEntries) {
			if (equals(assetEntry, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			assetEntries + " does not contain " + assetEntry, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		AssetEntry assetEntry1, AssetEntry assetEntry2) {

		Assert.assertTrue(
			assetEntry1 + " does not equal " + assetEntry2,
			equals(assetEntry1, assetEntry2));
	}

	protected void assertEquals(
		List<AssetEntry> assetEntries1, List<AssetEntry> assetEntries2) {

		Assert.assertEquals(assetEntries1.size(), assetEntries2.size());

		for (int i = 0; i < assetEntries1.size(); i++) {
			AssetEntry assetEntry1 = assetEntries1.get(i);
			AssetEntry assetEntry2 = assetEntries2.get(i);

			assertEquals(assetEntry1, assetEntry2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<AssetEntry> assetEntries1, List<AssetEntry> assetEntries2) {

		Assert.assertEquals(assetEntries1.size(), assetEntries2.size());

		for (AssetEntry assetEntry1 : assetEntries1) {
			boolean contains = false;

			for (AssetEntry assetEntry2 : assetEntries2) {
				if (equals(assetEntry1, assetEntry2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				assetEntries2 + " does not contain " + assetEntry1, contains);
		}
	}

	protected void assertValid(AssetEntry assetEntry) throws Exception {
		boolean valid = true;

		if (assetEntry.getDateModified() == null) {
			valid = false;
		}

		if (assetEntry.getAssetEntryId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("assetEntryId", additionalAssertFieldName)) {
				if (assetEntry.getAssetEntryId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assetType", additionalAssertFieldName)) {
				if (assetEntry.getAssetType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("className", additionalAssertFieldName)) {
				if (assetEntry.getClassName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("classNameId", additionalAssertFieldName)) {
				if (assetEntry.getClassNameId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("classPK", additionalAssertFieldName)) {
				if (assetEntry.getClassPK() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (assetEntry.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (assetEntry.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"groupDescriptiveName", additionalAssertFieldName)) {

				if (assetEntry.getGroupDescriptiveName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (assetEntry.getPermissions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (assetEntry.getStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (assetEntry.getTitle() == null) {
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

	protected void assertValid(Page<AssetEntry> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<AssetEntry> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<AssetEntry> assetEntries = page.getItems();

		int size = assetEntries.size();

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

		graphQLFields.add(new GraphQLField("assetEntryId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.delivery.dto.v1_0.AssetEntry.class)) {

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

	protected boolean equals(AssetEntry assetEntry1, AssetEntry assetEntry2) {
		if (assetEntry1 == assetEntry2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("assetEntryId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetEntry1.getAssetEntryId(),
						assetEntry2.getAssetEntryId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("assetType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetEntry1.getAssetType(),
						assetEntry2.getAssetType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("className", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetEntry1.getClassName(),
						assetEntry2.getClassName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("classNameId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetEntry1.getClassNameId(),
						assetEntry2.getClassNameId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("classPK", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetEntry1.getClassPK(), assetEntry2.getClassPK())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetEntry1.getCreator(), assetEntry2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetEntry1.getDateModified(),
						assetEntry2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetEntry1.getDescription(),
						assetEntry2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"groupDescriptiveName", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						assetEntry1.getGroupDescriptiveName(),
						assetEntry2.getGroupDescriptiveName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetEntry1.getPermissions(),
						assetEntry2.getPermissions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetEntry1.getStatus(), assetEntry2.getStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetEntry1.getTitle(), assetEntry2.getTitle())) {

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

		if (!(_assetEntryResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_assetEntryResource;

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
		EntityField entityField, String operator, AssetEntry assetEntry) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("assetEntryId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("assetType")) {
			Object object = assetEntry.getAssetType();

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

		if (entityFieldName.equals("className")) {
			Object object = assetEntry.getClassName();

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

		if (entityFieldName.equals("classNameId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("classPK")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = assetEntry.getDateModified();

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

				sb.append(_format.format(assetEntry.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = assetEntry.getDescription();

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

		if (entityFieldName.equals("groupDescriptiveName")) {
			Object object = assetEntry.getGroupDescriptiveName();

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

		if (entityFieldName.equals("permissions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("status")) {
			sb.append(String.valueOf(assetEntry.getStatus()));

			return sb.toString();
		}

		if (entityFieldName.equals("title")) {
			Object object = assetEntry.getTitle();

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

	protected AssetEntry randomAssetEntry() throws Exception {
		return new AssetEntry() {
			{
				assetEntryId = RandomTestUtil.randomLong();
				assetType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				className = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				classNameId = RandomTestUtil.randomLong();
				classPK = RandomTestUtil.randomLong();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				groupDescriptiveName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				status = RandomTestUtil.randomInt();
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected AssetEntry randomIrrelevantAssetEntry() throws Exception {
		AssetEntry randomIrrelevantAssetEntry = randomAssetEntry();

		return randomIrrelevantAssetEntry;
	}

	protected AssetEntry randomPatchAssetEntry() throws Exception {
		return randomAssetEntry();
	}

	protected AssetEntryResource assetEntryResource;
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
		LogFactoryUtil.getLog(BaseAssetEntryResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.delivery.resource.v1_0.AssetEntryResource
		_assetEntryResource;

}
// LIFERAY-REST-BUILDER-HASH:-1244491827