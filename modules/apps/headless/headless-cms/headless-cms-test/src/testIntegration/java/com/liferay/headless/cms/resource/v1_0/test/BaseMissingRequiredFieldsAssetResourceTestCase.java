/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.cms.client.dto.v1_0.MissingRequiredFieldsAsset;
import com.liferay.headless.cms.client.http.HttpInvoker;
import com.liferay.headless.cms.client.pagination.Page;
import com.liferay.headless.cms.client.pagination.Pagination;
import com.liferay.headless.cms.client.resource.v1_0.MissingRequiredFieldsAssetResource;
import com.liferay.headless.cms.client.serdes.v1_0.MissingRequiredFieldsAssetSerDes;
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
 * @author Crescenzo Rega
 * @generated
 */
@Generated("")
public abstract class BaseMissingRequiredFieldsAssetResourceTestCase {

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

		_missingRequiredFieldsAssetResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		missingRequiredFieldsAssetResource =
			MissingRequiredFieldsAssetResource.builder(
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

		MissingRequiredFieldsAsset missingRequiredFieldsAsset1 =
			randomMissingRequiredFieldsAsset();

		String json = objectMapper.writeValueAsString(
			missingRequiredFieldsAsset1);

		MissingRequiredFieldsAsset missingRequiredFieldsAsset2 =
			MissingRequiredFieldsAssetSerDes.toDTO(json);

		Assert.assertTrue(
			equals(missingRequiredFieldsAsset1, missingRequiredFieldsAsset2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		MissingRequiredFieldsAsset missingRequiredFieldsAsset =
			randomMissingRequiredFieldsAsset();

		String json1 = objectMapper.writeValueAsString(
			missingRequiredFieldsAsset);
		String json2 = MissingRequiredFieldsAssetSerDes.toJSON(
			missingRequiredFieldsAsset);

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

		MissingRequiredFieldsAsset missingRequiredFieldsAsset =
			randomMissingRequiredFieldsAsset();

		missingRequiredFieldsAsset.setHref(regex);
		missingRequiredFieldsAsset.setObjectDefinitionExternalReferenceCode(
			regex);
		missingRequiredFieldsAsset.setTitle(regex);

		String json = MissingRequiredFieldsAssetSerDes.toJSON(
			missingRequiredFieldsAsset);

		Assert.assertFalse(json.contains(regex));

		missingRequiredFieldsAsset = MissingRequiredFieldsAssetSerDes.toDTO(
			json);

		Assert.assertEquals(regex, missingRequiredFieldsAsset.getHref());
		Assert.assertEquals(
			regex,
			missingRequiredFieldsAsset.
				getObjectDefinitionExternalReferenceCode());
		Assert.assertEquals(regex, missingRequiredFieldsAsset.getTitle());
	}

	@Test
	public void testGetMissingRequiredFieldsAssetsPage() throws Exception {
		Page<MissingRequiredFieldsAsset> page =
			missingRequiredFieldsAssetResource.
				getMissingRequiredFieldsAssetsPage(
					null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		MissingRequiredFieldsAsset missingRequiredFieldsAsset1 =
			testGetMissingRequiredFieldsAssetsPage_addMissingRequiredFieldsAsset(
				randomMissingRequiredFieldsAsset());

		MissingRequiredFieldsAsset missingRequiredFieldsAsset2 =
			testGetMissingRequiredFieldsAssetsPage_addMissingRequiredFieldsAsset(
				randomMissingRequiredFieldsAsset());

		page =
			missingRequiredFieldsAssetResource.
				getMissingRequiredFieldsAssetsPage(
					null, null, Pagination.of(1, (int)totalCount + 2), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			missingRequiredFieldsAsset1,
			(List<MissingRequiredFieldsAsset>)page.getItems());
		assertContains(
			missingRequiredFieldsAsset2,
			(List<MissingRequiredFieldsAsset>)page.getItems());
		assertValid(
			page, testGetMissingRequiredFieldsAssetsPage_getExpectedActions());
	}

	protected Map<String, Map<String, String>>
			testGetMissingRequiredFieldsAssetsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetMissingRequiredFieldsAssetsPageWithPagination()
		throws Exception {

		Page<MissingRequiredFieldsAsset> missingRequiredFieldsAssetsPage =
			missingRequiredFieldsAssetResource.
				getMissingRequiredFieldsAssetsPage(null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			missingRequiredFieldsAssetsPage.getTotalCount());

		MissingRequiredFieldsAsset missingRequiredFieldsAsset1 =
			testGetMissingRequiredFieldsAssetsPage_addMissingRequiredFieldsAsset(
				randomMissingRequiredFieldsAsset());

		MissingRequiredFieldsAsset missingRequiredFieldsAsset2 =
			testGetMissingRequiredFieldsAssetsPage_addMissingRequiredFieldsAsset(
				randomMissingRequiredFieldsAsset());

		MissingRequiredFieldsAsset missingRequiredFieldsAsset3 =
			testGetMissingRequiredFieldsAssetsPage_addMissingRequiredFieldsAsset(
				randomMissingRequiredFieldsAsset());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<MissingRequiredFieldsAsset> page1 =
				missingRequiredFieldsAssetResource.
					getMissingRequiredFieldsAssetsPage(
						null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				missingRequiredFieldsAsset1,
				(List<MissingRequiredFieldsAsset>)page1.getItems());

			Page<MissingRequiredFieldsAsset> page2 =
				missingRequiredFieldsAssetResource.
					getMissingRequiredFieldsAssetsPage(
						null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				missingRequiredFieldsAsset2,
				(List<MissingRequiredFieldsAsset>)page2.getItems());

			Page<MissingRequiredFieldsAsset> page3 =
				missingRequiredFieldsAssetResource.
					getMissingRequiredFieldsAssetsPage(
						null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				missingRequiredFieldsAsset3,
				(List<MissingRequiredFieldsAsset>)page3.getItems());
		}
		else {
			Page<MissingRequiredFieldsAsset> page1 =
				missingRequiredFieldsAssetResource.
					getMissingRequiredFieldsAssetsPage(
						null, null, Pagination.of(1, totalCount + 2), null);

			List<MissingRequiredFieldsAsset> missingRequiredFieldsAssets1 =
				(List<MissingRequiredFieldsAsset>)page1.getItems();

			Assert.assertEquals(
				missingRequiredFieldsAssets1.toString(), totalCount + 2,
				missingRequiredFieldsAssets1.size());

			Page<MissingRequiredFieldsAsset> page2 =
				missingRequiredFieldsAssetResource.
					getMissingRequiredFieldsAssetsPage(
						null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<MissingRequiredFieldsAsset> missingRequiredFieldsAssets2 =
				(List<MissingRequiredFieldsAsset>)page2.getItems();

			Assert.assertEquals(
				missingRequiredFieldsAssets2.toString(), 1,
				missingRequiredFieldsAssets2.size());

			Page<MissingRequiredFieldsAsset> page3 =
				missingRequiredFieldsAssetResource.
					getMissingRequiredFieldsAssetsPage(
						null, null, Pagination.of(1, (int)totalCount + 3),
						null);

			assertContains(
				missingRequiredFieldsAsset1,
				(List<MissingRequiredFieldsAsset>)page3.getItems());
			assertContains(
				missingRequiredFieldsAsset2,
				(List<MissingRequiredFieldsAsset>)page3.getItems());
			assertContains(
				missingRequiredFieldsAsset3,
				(List<MissingRequiredFieldsAsset>)page3.getItems());
		}
	}

	@Test
	public void testGetMissingRequiredFieldsAssetsPageWithSortDateTime()
		throws Exception {

		testGetMissingRequiredFieldsAssetsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, missingRequiredFieldsAsset1,
			 missingRequiredFieldsAsset2) -> {

				BeanTestUtil.setProperty(
					missingRequiredFieldsAsset1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetMissingRequiredFieldsAssetsPageWithSortDouble()
		throws Exception {

		testGetMissingRequiredFieldsAssetsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, missingRequiredFieldsAsset1,
			 missingRequiredFieldsAsset2) -> {

				BeanTestUtil.setProperty(
					missingRequiredFieldsAsset1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					missingRequiredFieldsAsset2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetMissingRequiredFieldsAssetsPageWithSortInteger()
		throws Exception {

		testGetMissingRequiredFieldsAssetsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, missingRequiredFieldsAsset1,
			 missingRequiredFieldsAsset2) -> {

				BeanTestUtil.setProperty(
					missingRequiredFieldsAsset1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					missingRequiredFieldsAsset2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetMissingRequiredFieldsAssetsPageWithSortString()
		throws Exception {

		testGetMissingRequiredFieldsAssetsPageWithSort(
			EntityField.Type.STRING,
			(entityField, missingRequiredFieldsAsset1,
			 missingRequiredFieldsAsset2) -> {

				Class<?> clazz = missingRequiredFieldsAsset1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						missingRequiredFieldsAsset1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						missingRequiredFieldsAsset2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						missingRequiredFieldsAsset1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						missingRequiredFieldsAsset2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						missingRequiredFieldsAsset1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						missingRequiredFieldsAsset2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetMissingRequiredFieldsAssetsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, MissingRequiredFieldsAsset,
				 MissingRequiredFieldsAsset, Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		MissingRequiredFieldsAsset missingRequiredFieldsAsset1 =
			randomMissingRequiredFieldsAsset();
		MissingRequiredFieldsAsset missingRequiredFieldsAsset2 =
			randomMissingRequiredFieldsAsset();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, missingRequiredFieldsAsset1,
				missingRequiredFieldsAsset2);
		}

		missingRequiredFieldsAsset1 =
			testGetMissingRequiredFieldsAssetsPage_addMissingRequiredFieldsAsset(
				missingRequiredFieldsAsset1);

		missingRequiredFieldsAsset2 =
			testGetMissingRequiredFieldsAssetsPage_addMissingRequiredFieldsAsset(
				missingRequiredFieldsAsset2);

		Page<MissingRequiredFieldsAsset> page =
			missingRequiredFieldsAssetResource.
				getMissingRequiredFieldsAssetsPage(null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<MissingRequiredFieldsAsset> ascPage =
				missingRequiredFieldsAssetResource.
					getMissingRequiredFieldsAssetsPage(
						null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				missingRequiredFieldsAsset1,
				(List<MissingRequiredFieldsAsset>)ascPage.getItems());
			assertContains(
				missingRequiredFieldsAsset2,
				(List<MissingRequiredFieldsAsset>)ascPage.getItems());

			Page<MissingRequiredFieldsAsset> descPage =
				missingRequiredFieldsAssetResource.
					getMissingRequiredFieldsAssetsPage(
						null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				missingRequiredFieldsAsset2,
				(List<MissingRequiredFieldsAsset>)descPage.getItems());
			assertContains(
				missingRequiredFieldsAsset1,
				(List<MissingRequiredFieldsAsset>)descPage.getItems());
		}
	}

	protected MissingRequiredFieldsAsset
			testGetMissingRequiredFieldsAssetsPage_addMissingRequiredFieldsAsset(
				MissingRequiredFieldsAsset missingRequiredFieldsAsset)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		MissingRequiredFieldsAsset missingRequiredFieldsAsset,
		List<MissingRequiredFieldsAsset> missingRequiredFieldsAssets) {

		boolean contains = false;

		for (MissingRequiredFieldsAsset item : missingRequiredFieldsAssets) {
			if (equals(missingRequiredFieldsAsset, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			missingRequiredFieldsAssets + " does not contain " +
				missingRequiredFieldsAsset,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		MissingRequiredFieldsAsset missingRequiredFieldsAsset1,
		MissingRequiredFieldsAsset missingRequiredFieldsAsset2) {

		Assert.assertTrue(
			missingRequiredFieldsAsset1 + " does not equal " +
				missingRequiredFieldsAsset2,
			equals(missingRequiredFieldsAsset1, missingRequiredFieldsAsset2));
	}

	protected void assertEquals(
		List<MissingRequiredFieldsAsset> missingRequiredFieldsAssets1,
		List<MissingRequiredFieldsAsset> missingRequiredFieldsAssets2) {

		Assert.assertEquals(
			missingRequiredFieldsAssets1.size(),
			missingRequiredFieldsAssets2.size());

		for (int i = 0; i < missingRequiredFieldsAssets1.size(); i++) {
			MissingRequiredFieldsAsset missingRequiredFieldsAsset1 =
				missingRequiredFieldsAssets1.get(i);
			MissingRequiredFieldsAsset missingRequiredFieldsAsset2 =
				missingRequiredFieldsAssets2.get(i);

			assertEquals(
				missingRequiredFieldsAsset1, missingRequiredFieldsAsset2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<MissingRequiredFieldsAsset> missingRequiredFieldsAssets1,
		List<MissingRequiredFieldsAsset> missingRequiredFieldsAssets2) {

		Assert.assertEquals(
			missingRequiredFieldsAssets1.size(),
			missingRequiredFieldsAssets2.size());

		for (MissingRequiredFieldsAsset missingRequiredFieldsAsset1 :
				missingRequiredFieldsAssets1) {

			boolean contains = false;

			for (MissingRequiredFieldsAsset missingRequiredFieldsAsset2 :
					missingRequiredFieldsAssets2) {

				if (equals(
						missingRequiredFieldsAsset1,
						missingRequiredFieldsAsset2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				missingRequiredFieldsAssets2 + " does not contain " +
					missingRequiredFieldsAsset1,
				contains);
		}
	}

	protected void assertValid(
			MissingRequiredFieldsAsset missingRequiredFieldsAsset)
		throws Exception {

		boolean valid = true;

		if (missingRequiredFieldsAsset.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (missingRequiredFieldsAsset.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("href", additionalAssertFieldName)) {
				if (missingRequiredFieldsAsset.getHref() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"missingRequiredFieldsCount", additionalAssertFieldName)) {

				if (missingRequiredFieldsAsset.
						getMissingRequiredFieldsCount() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionExternalReferenceCode",
					additionalAssertFieldName)) {

				if (missingRequiredFieldsAsset.
						getObjectDefinitionExternalReferenceCode() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (missingRequiredFieldsAsset.getTitle() == null) {
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

	protected void assertValid(Page<MissingRequiredFieldsAsset> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<MissingRequiredFieldsAsset> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<MissingRequiredFieldsAsset>
			missingRequiredFieldsAssets = page.getItems();

		int size = missingRequiredFieldsAssets.size();

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
					com.liferay.headless.cms.dto.v1_0.
						MissingRequiredFieldsAsset.class)) {

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
		MissingRequiredFieldsAsset missingRequiredFieldsAsset1,
		MissingRequiredFieldsAsset missingRequiredFieldsAsset2) {

		if (missingRequiredFieldsAsset1 == missingRequiredFieldsAsset2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)missingRequiredFieldsAsset1.getActions(),
						(Map)missingRequiredFieldsAsset2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("href", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						missingRequiredFieldsAsset1.getHref(),
						missingRequiredFieldsAsset2.getHref())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						missingRequiredFieldsAsset1.getId(),
						missingRequiredFieldsAsset2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"missingRequiredFieldsCount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						missingRequiredFieldsAsset1.
							getMissingRequiredFieldsCount(),
						missingRequiredFieldsAsset2.
							getMissingRequiredFieldsCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						missingRequiredFieldsAsset1.
							getObjectDefinitionExternalReferenceCode(),
						missingRequiredFieldsAsset2.
							getObjectDefinitionExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						missingRequiredFieldsAsset1.getTitle(),
						missingRequiredFieldsAsset2.getTitle())) {

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

		if (!(_missingRequiredFieldsAssetResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_missingRequiredFieldsAssetResource;

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
		MissingRequiredFieldsAsset missingRequiredFieldsAsset) {

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

		if (entityFieldName.equals("href")) {
			Object object = missingRequiredFieldsAsset.getHref();

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

		if (entityFieldName.equals("missingRequiredFieldsCount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("objectDefinitionExternalReferenceCode")) {
			Object object =
				missingRequiredFieldsAsset.
					getObjectDefinitionExternalReferenceCode();

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

		if (entityFieldName.equals("title")) {
			Object object = missingRequiredFieldsAsset.getTitle();

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

	protected MissingRequiredFieldsAsset randomMissingRequiredFieldsAsset()
		throws Exception {

		return new MissingRequiredFieldsAsset() {
			{
				href = StringUtil.toLowerCase(RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				missingRequiredFieldsCount = RandomTestUtil.randomLong();
				objectDefinitionExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected MissingRequiredFieldsAsset
			randomIrrelevantMissingRequiredFieldsAsset()
		throws Exception {

		MissingRequiredFieldsAsset randomIrrelevantMissingRequiredFieldsAsset =
			randomMissingRequiredFieldsAsset();

		return randomIrrelevantMissingRequiredFieldsAsset;
	}

	protected MissingRequiredFieldsAsset randomPatchMissingRequiredFieldsAsset()
		throws Exception {

		return randomMissingRequiredFieldsAsset();
	}

	protected MissingRequiredFieldsAssetResource
		missingRequiredFieldsAssetResource;
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
			BaseMissingRequiredFieldsAssetResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.cms.resource.v1_0.
			MissingRequiredFieldsAssetResource
				_missingRequiredFieldsAssetResource;

}
// LIFERAY-REST-BUILDER-HASH:2098530500