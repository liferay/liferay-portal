/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
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

import com.liferay.headless.cms.client.dto.v1_0.BulkAction;
import com.liferay.headless.cms.client.dto.v1_0.BulkActionTask;
import com.liferay.headless.cms.client.dto.v1_0.DefaultPermissionBulkAction;
import com.liferay.headless.cms.client.dto.v1_0.DeleteBulkAction;
import com.liferay.headless.cms.client.dto.v1_0.KeywordBulkAction;
import com.liferay.headless.cms.client.dto.v1_0.MoveBulkAction;
import com.liferay.headless.cms.client.dto.v1_0.PermissionBulkAction;
import com.liferay.headless.cms.client.dto.v1_0.StatusBulkAction;
import com.liferay.headless.cms.client.dto.v1_0.TaxonomyCategoryBulkAction;
import com.liferay.headless.cms.client.http.HttpInvoker;
import com.liferay.headless.cms.client.pagination.Page;
import com.liferay.headless.cms.client.resource.v1_0.BulkActionResource;
import com.liferay.headless.cms.client.serdes.v1_0.BulkActionSerDes;
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
import java.util.function.Supplier;

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
public abstract class BaseBulkActionResourceTestCase {

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

		_bulkActionResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		bulkActionResource = BulkActionResource.builder(
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

		BulkAction bulkAction1 = randomBulkAction();

		String json = objectMapper.writeValueAsString(bulkAction1);

		BulkAction bulkAction2 = BulkActionSerDes.toDTO(json);

		Assert.assertTrue(equals(bulkAction1, bulkAction2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		BulkAction bulkAction = randomBulkAction();

		String json1 = objectMapper.writeValueAsString(bulkAction);
		String json2 = BulkActionSerDes.toJSON(bulkAction);

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

		BulkAction bulkAction = randomBulkAction();

		String json = BulkActionSerDes.toJSON(bulkAction);

		Assert.assertFalse(json.contains(regex));

		bulkAction = BulkActionSerDes.toDTO(json);
	}

	@Test
	public void testPostBulkActionItemPreviewPage() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPostBulkAction() throws Exception {
		Assert.assertTrue(true);
	}

	protected void assertContains(
		BulkAction bulkAction, List<BulkAction> bulkActions) {

		boolean contains = false;

		for (BulkAction item : bulkActions) {
			if (equals(bulkAction, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			bulkActions + " does not contain " + bulkAction, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		BulkAction bulkAction1, BulkAction bulkAction2) {

		Assert.assertTrue(
			bulkAction1 + " does not equal " + bulkAction2,
			equals(bulkAction1, bulkAction2));
	}

	protected void assertEquals(
		List<BulkAction> bulkActions1, List<BulkAction> bulkActions2) {

		Assert.assertEquals(bulkActions1.size(), bulkActions2.size());

		for (int i = 0; i < bulkActions1.size(); i++) {
			BulkAction bulkAction1 = bulkActions1.get(i);
			BulkAction bulkAction2 = bulkActions2.get(i);

			assertEquals(bulkAction1, bulkAction2);
		}
	}

	protected void assertEquals(
		BulkActionTask bulkActionTask1, BulkActionTask bulkActionTask2) {

		Assert.assertTrue(
			bulkActionTask1 + " does not equal " + bulkActionTask2,
			equals(bulkActionTask1, bulkActionTask2));
	}

	protected void assertEqualsIgnoringOrder(
		List<BulkAction> bulkActions1, List<BulkAction> bulkActions2) {

		Assert.assertEquals(bulkActions1.size(), bulkActions2.size());

		for (BulkAction bulkAction1 : bulkActions1) {
			boolean contains = false;

			for (BulkAction bulkAction2 : bulkActions2) {
				if (equals(bulkAction1, bulkAction2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				bulkActions2 + " does not contain " + bulkAction1, contains);
		}
	}

	protected void assertValid(BulkAction bulkAction) throws Exception {
		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("bulkActionItems", additionalAssertFieldName)) {
				if (bulkAction.getBulkActionItems() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("selectAll", additionalAssertFieldName)) {
				if (bulkAction.getSelectAll() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (bulkAction.getType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultPermissions", additionalAssertFieldName)) {

				if (!(bulkAction instanceof DefaultPermissionBulkAction)) {
					continue;
				}

				if (((DefaultPermissionBulkAction)bulkAction).
						getDefaultPermissions() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("depotGroupId", additionalAssertFieldName)) {
				if (!(bulkAction instanceof DefaultPermissionBulkAction)) {
					continue;
				}

				if (((DefaultPermissionBulkAction)bulkAction).
						getDepotGroupId() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("treePath", additionalAssertFieldName)) {
				if (!(bulkAction instanceof DefaultPermissionBulkAction)) {
					continue;
				}

				if (((DefaultPermissionBulkAction)bulkAction).getTreePath() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("keywords", additionalAssertFieldName)) {
				if (!(bulkAction instanceof KeywordBulkAction)) {
					continue;
				}

				if (((KeywordBulkAction)bulkAction).getKeywords() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectEntryFolderId", additionalAssertFieldName)) {

				if (!(bulkAction instanceof MoveBulkAction)) {
					continue;
				}

				if (((MoveBulkAction)bulkAction).getObjectEntryFolderId() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("configuration", additionalAssertFieldName)) {
				if (!(bulkAction instanceof PermissionBulkAction)) {
					continue;
				}

				if (((PermissionBulkAction)bulkAction).getConfiguration() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (!(bulkAction instanceof PermissionBulkAction)) {
					continue;
				}

				if (((PermissionBulkAction)bulkAction).getPermissions() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!(bulkAction instanceof StatusBulkAction)) {
					continue;
				}

				if (((StatusBulkAction)bulkAction).getStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryIds", additionalAssertFieldName)) {

				if (!(bulkAction instanceof TaxonomyCategoryBulkAction)) {
					continue;
				}

				if (((TaxonomyCategoryBulkAction)bulkAction).
						getTaxonomyCategoryIds() == null) {

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

	protected void assertValid(Page<BulkAction> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<BulkAction> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<BulkAction> bulkActions = page.getItems();

		int size = bulkActions.size();

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

	protected void assertValid(BulkActionTask bulkActionTask) {
		boolean valid = true;

		if (bulkActionTask.getExternalReferenceCode() == null) {
			valid = false;
		}

		if (bulkActionTask.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalBulkActionTaskAssertFieldNames()) {

			if (Objects.equals("actionName", additionalAssertFieldName)) {
				if (bulkActionTask.getActionName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("author", additionalAssertFieldName)) {
				if (bulkActionTask.getAuthor() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("completionDate", additionalAssertFieldName)) {
				if (bulkActionTask.getCompletionDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("createdDate", additionalAssertFieldName)) {
				if (bulkActionTask.getCreatedDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("executeStatus", additionalAssertFieldName)) {
				if (bulkActionTask.getExecuteStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (bulkActionTask.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("numberOfItems", additionalAssertFieldName)) {
				if (bulkActionTask.getNumberOfItems() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (bulkActionTask.getType() == null) {
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

	protected String[] getAdditionalAssertFieldNames() {
		return new String[0];
	}

	protected String[] getAdditionalBulkActionTaskAssertFieldNames() {
		return new String[0];
	}

	protected List<GraphQLField> getGraphQLFields() throws Exception {
		List<GraphQLField> graphQLFields = new ArrayList<>();

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.cms.dto.v1_0.BulkAction.class)) {

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

	protected boolean equals(BulkAction bulkAction1, BulkAction bulkAction2) {
		if (bulkAction1 == bulkAction2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("bulkActionItems", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						bulkAction1.getBulkActionItems(),
						bulkAction2.getBulkActionItems())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("selectAll", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						bulkAction1.getSelectAll(),
						bulkAction2.getSelectAll())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						bulkAction1.getType(), bulkAction2.getType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultPermissions", additionalAssertFieldName)) {

				if (!(bulkAction1 instanceof DefaultPermissionBulkAction) ||
					!(bulkAction2 instanceof DefaultPermissionBulkAction)) {

					continue;
				}

				if (!Objects.deepEquals(
						((DefaultPermissionBulkAction)bulkAction1).
							getDefaultPermissions(),
						((DefaultPermissionBulkAction)bulkAction2).
							getDefaultPermissions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("depotGroupId", additionalAssertFieldName)) {
				if (!(bulkAction1 instanceof DefaultPermissionBulkAction) ||
					!(bulkAction2 instanceof DefaultPermissionBulkAction)) {

					continue;
				}

				if (!Objects.deepEquals(
						((DefaultPermissionBulkAction)bulkAction1).
							getDepotGroupId(),
						((DefaultPermissionBulkAction)bulkAction2).
							getDepotGroupId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("treePath", additionalAssertFieldName)) {
				if (!(bulkAction1 instanceof DefaultPermissionBulkAction) ||
					!(bulkAction2 instanceof DefaultPermissionBulkAction)) {

					continue;
				}

				if (!Objects.deepEquals(
						((DefaultPermissionBulkAction)bulkAction1).
							getTreePath(),
						((DefaultPermissionBulkAction)bulkAction2).
							getTreePath())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("keywords", additionalAssertFieldName)) {
				if (!(bulkAction1 instanceof KeywordBulkAction) ||
					!(bulkAction2 instanceof KeywordBulkAction)) {

					continue;
				}

				if (!Objects.deepEquals(
						((KeywordBulkAction)bulkAction1).getKeywords(),
						((KeywordBulkAction)bulkAction2).getKeywords())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectEntryFolderId", additionalAssertFieldName)) {

				if (!(bulkAction1 instanceof MoveBulkAction) ||
					!(bulkAction2 instanceof MoveBulkAction)) {

					continue;
				}

				if (!Objects.deepEquals(
						((MoveBulkAction)bulkAction1).getObjectEntryFolderId(),
						((MoveBulkAction)bulkAction2).
							getObjectEntryFolderId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("configuration", additionalAssertFieldName)) {
				if (!(bulkAction1 instanceof PermissionBulkAction) ||
					!(bulkAction2 instanceof PermissionBulkAction)) {

					continue;
				}

				if (!Objects.deepEquals(
						((PermissionBulkAction)bulkAction1).getConfiguration(),
						((PermissionBulkAction)bulkAction2).
							getConfiguration())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (!(bulkAction1 instanceof PermissionBulkAction) ||
					!(bulkAction2 instanceof PermissionBulkAction)) {

					continue;
				}

				if (!Objects.deepEquals(
						((PermissionBulkAction)bulkAction1).getPermissions(),
						((PermissionBulkAction)bulkAction2).getPermissions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!(bulkAction1 instanceof StatusBulkAction) ||
					!(bulkAction2 instanceof StatusBulkAction)) {

					continue;
				}

				if (!Objects.deepEquals(
						((StatusBulkAction)bulkAction1).getStatus(),
						((StatusBulkAction)bulkAction2).getStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryIds", additionalAssertFieldName)) {

				if (!(bulkAction1 instanceof TaxonomyCategoryBulkAction) ||
					!(bulkAction2 instanceof TaxonomyCategoryBulkAction)) {

					continue;
				}

				if (!Objects.deepEquals(
						((TaxonomyCategoryBulkAction)bulkAction1).
							getTaxonomyCategoryIds(),
						((TaxonomyCategoryBulkAction)bulkAction2).
							getTaxonomyCategoryIds())) {

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

	protected boolean equals(
		BulkActionTask bulkActionTask1, BulkActionTask bulkActionTask2) {

		if (bulkActionTask1 == bulkActionTask2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalBulkActionTaskAssertFieldNames()) {

			if (Objects.equals("actionName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						bulkActionTask1.getActionName(),
						bulkActionTask2.getActionName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("author", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						bulkActionTask1.getAuthor(),
						bulkActionTask2.getAuthor())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("completionDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						bulkActionTask1.getCompletionDate(),
						bulkActionTask2.getCompletionDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("createdDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						bulkActionTask1.getCreatedDate(),
						bulkActionTask2.getCreatedDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("executeStatus", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						bulkActionTask1.getExecuteStatus(),
						bulkActionTask2.getExecuteStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						bulkActionTask1.getExternalReferenceCode(),
						bulkActionTask2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						bulkActionTask1.getId(), bulkActionTask2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("numberOfItems", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						bulkActionTask1.getNumberOfItems(),
						bulkActionTask2.getNumberOfItems())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						bulkActionTask1.getType(), bulkActionTask2.getType())) {

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

		if (!(_bulkActionResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_bulkActionResource;

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
		EntityField entityField, String operator, BulkAction bulkAction) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("bulkActionItems")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("selectAll")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("type")) {
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

	protected BulkAction randomBulkAction() throws Exception {
		List<Supplier<BulkAction>> suppliers = Arrays.asList(
			() -> {
				DefaultPermissionBulkAction bulkAction =
					new DefaultPermissionBulkAction();

				bulkAction.setSelectAll(RandomTestUtil.randomBoolean());

				bulkAction.setDefaultPermissions(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				bulkAction.setDepotGroupId(RandomTestUtil.randomLong());
				bulkAction.setTreePath(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));

				bulkAction.setType(
					BulkAction.Type.create("DefaultPermissionBulkAction"));

				return bulkAction;
			},
			() -> {
				DeleteBulkAction bulkAction = new DeleteBulkAction();

				bulkAction.setSelectAll(RandomTestUtil.randomBoolean());

				bulkAction.setType(BulkAction.Type.create("DeleteBulkAction"));

				return bulkAction;
			},
			() -> {
				KeywordBulkAction bulkAction = new KeywordBulkAction();

				bulkAction.setSelectAll(RandomTestUtil.randomBoolean());

				bulkAction.setType(BulkAction.Type.create("KeywordBulkAction"));

				return bulkAction;
			},
			() -> {
				MoveBulkAction bulkAction = new MoveBulkAction();

				bulkAction.setSelectAll(RandomTestUtil.randomBoolean());

				bulkAction.setObjectEntryFolderId(RandomTestUtil.randomLong());

				bulkAction.setType(BulkAction.Type.create("MoveBulkAction"));

				return bulkAction;
			},
			() -> {
				PermissionBulkAction bulkAction = new PermissionBulkAction();

				bulkAction.setSelectAll(RandomTestUtil.randomBoolean());

				bulkAction.setConfiguration(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));

				bulkAction.setType(
					BulkAction.Type.create("PermissionBulkAction"));

				return bulkAction;
			},
			() -> {
				StatusBulkAction bulkAction = new StatusBulkAction();

				bulkAction.setSelectAll(RandomTestUtil.randomBoolean());

				bulkAction.setStatus(RandomTestUtil.randomInt());

				bulkAction.setType(BulkAction.Type.create("StatusBulkAction"));

				return bulkAction;
			},
			() -> {
				TaxonomyCategoryBulkAction bulkAction =
					new TaxonomyCategoryBulkAction();

				bulkAction.setSelectAll(RandomTestUtil.randomBoolean());

				bulkAction.setType(
					BulkAction.Type.create("TaxonomyCategoryBulkAction"));

				return bulkAction;
			});

		Supplier<BulkAction> supplier = suppliers.get(
			RandomTestUtil.randomInt(0, suppliers.size() - 1));

		return supplier.get();
	}

	protected BulkAction randomIrrelevantBulkAction() throws Exception {
		BulkAction randomIrrelevantBulkAction = randomBulkAction();

		return randomIrrelevantBulkAction;
	}

	protected BulkAction randomPatchBulkAction() throws Exception {
		return randomBulkAction();
	}

	protected BulkActionTask randomBulkActionTask() throws Exception {
		return new BulkActionTask() {
			{
				actionName = RandomTestUtil.randomString();
				author = RandomTestUtil.randomString();
				completionDate = RandomTestUtil.nextDate();
				createdDate = RandomTestUtil.nextDate();
				executeStatus = RandomTestUtil.randomString();
				externalReferenceCode = RandomTestUtil.randomString();
				id = RandomTestUtil.randomLong();
				numberOfItems = RandomTestUtil.randomInt();
				type = RandomTestUtil.randomString();
			}
		};
	}

	protected BulkActionResource bulkActionResource;
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
		LogFactoryUtil.getLog(BaseBulkActionResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.cms.resource.v1_0.BulkActionResource
		_bulkActionResource;

}