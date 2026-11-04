/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.TaxonomyCategory;
import com.liferay.headless.admin.taxonomy.client.resource.v1_0.TaxonomyCategoryResource;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectFieldValidationConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.builder.LongTextObjectFieldBuilder;
import com.liferay.object.field.builder.RichTextObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.business.type.ObjectFieldBusinessTypeRegistry;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.resource.v1_0.ObjectEntryResource;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.rest.test.util.ObjectFieldTestUtil;
import com.liferay.object.rest.test.util.ObjectRelationshipTestUtil;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.ObjectValidationRuleLocalService;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.NoSuchCompanyException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.lang.reflect.Method;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.sql.Timestamp;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Luis Miguel Barcos
 */
@RunWith(Arquillian.class)
public abstract class BaseObjectEntryResourceTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		testGroupId = TestPropsValues.getGroupId();

		assetVocabulary = AssetVocabularyLocalServiceUtil.addVocabulary(
			UserLocalServiceUtil.getGuestUserId(TestPropsValues.getCompanyId()),
			testGroupId, RandomTestUtil.randomString(), new ServiceContext());

		Bundle bundle = FrameworkUtil.getBundle(
			BaseObjectEntryResourceTestCase.class);

		_bundleContext = bundle.getBundleContext();

		_serviceRegistrations = Arrays.asList(
			_bundleContext.registerService(
				ModelListener.class, testDLFileEntryModelListener, null),
			_bundleContext.registerService(
				ModelListener.class, testObjectEntryModelListener, null));

		TaxonomyCategoryResource.Builder builder =
			TaxonomyCategoryResource.builder();

		taxonomyCategoryResource = builder.authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistrations.forEach(ServiceRegistration::unregister);
	}

	@Before
	public void setUp() throws Exception {
		group = GroupTestUtil.addGroup();

		listTypeDefinition =
			listTypeDefinitionLocalService.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				false, Collections.emptyList(), new ServiceContext());

		_listTypeEntry1 = _listTypeEntryLocalService.addListTypeEntry(
			null, TestPropsValues.getUserId(),
			listTypeDefinition.getListTypeDefinitionId(), LIST_TYPE_ENTRY_KEY_1,
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			listTypeDefinition.isSystem());
		_listTypeEntry2 = _listTypeEntryLocalService.addListTypeEntry(
			null, TestPropsValues.getUserId(),
			listTypeDefinition.getListTypeDefinitionId(), LIST_TYPE_ENTRY_KEY_2,
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			listTypeDefinition.isSystem());
		_listTypeEntry3 = _listTypeEntryLocalService.addListTypeEntry(
			null, TestPropsValues.getUserId(),
			listTypeDefinition.getListTypeDefinitionId(), LIST_TYPE_ENTRY_KEY_3,
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			listTypeDefinition.isSystem());

		systemObjectDefinitionManager =
			_systemObjectDefinitionManagerRegistry.
				getSystemObjectDefinitionManager("User");

		userSystemObjectDefinition =
			objectDefinitionLocalService.fetchSystemObjectDefinition(
				TestPropsValues.getCompanyId(),
				systemObjectDefinitionManager.getName());

		_userSystemObjectField = ObjectFieldTestUtil.addCustomObjectField(
			TestPropsValues.getUserId(),
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, userSystemObjectDefinition,
			OBJECT_FIELD_NAME_2);
	}

	@After
	public void tearDown() throws Exception {
		if (objectRelationship1 != null) {
			objectRelationshipLocalService.deleteObjectRelationship(
				objectRelationship1);
		}

		if (objectRelationship2 != null) {
			objectRelationshipLocalService.deleteObjectRelationship(
				objectRelationship2);
		}

		if (objectRelationship3 != null) {
			objectRelationshipLocalService.deleteObjectRelationship(
				objectRelationship3);
		}

		if (objectRelationship4 != null) {
			objectRelationshipLocalService.deleteObjectRelationship(
				objectRelationship4);
		}

		if (objectRelationship5 != null) {
			objectRelationshipLocalService.deleteObjectRelationship(
				objectRelationship5);
		}

		if (_objectRelationship6 != null) {
			objectRelationshipLocalService.deleteObjectRelationship(
				_objectRelationship6);
		}

		if (objectRelationship7 != null) {
			objectRelationshipLocalService.deleteObjectRelationship(
				objectRelationship7);
		}

		if (objectDefinition1 != null) {
			objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition1);
		}

		if (objectDefinition2 != null) {
			objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition2);
		}

		if (objectDefinition3 != null) {
			objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition3);
		}

		if (_objectDefinition4 != null) {
			objectDefinitionLocalService.deleteObjectDefinition(
				_objectDefinition4);
		}

		if (siteScopedObjectDefinition1 != null) {
			objectDefinitionLocalService.deleteObjectDefinition(
				siteScopedObjectDefinition1);
		}

		if (_siteScopedObjectDefinition2 != null) {
			objectDefinitionLocalService.deleteObjectDefinition(
				_siteScopedObjectDefinition2);
		}

		listTypeDefinitionLocalService.deleteListTypeDefinition(
			listTypeDefinition);

		groupLocalService.deleteGroup(group);
	}

	protected ObjectDefinition addDepotScopedObjectDefinition()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).name(
						StringUtil.randomId()
					).build()),
				ObjectDefinitionConstants.SCOPE_DEPOT);

		objectDefinitionSettingLocalService.addObjectDefinitionSetting(
			objectDefinition.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS,
			StringPool.TRUE);

		return objectDefinition;
	}

	protected ObjectRelationship addObjectRelationshipAndRelateObjectEntries(
			ObjectDefinition objectDefinition1,
			ObjectDefinition objectDefinition2, long primaryKey1,
			long primaryKey2, String type)
		throws Exception {

		return addObjectRelationshipAndRelateObjectEntries(
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT,
			objectDefinition1, objectDefinition2, primaryKey1, primaryKey2,
			type);
	}

	protected ObjectRelationship addObjectRelationshipAndRelateObjectEntries(
			String type)
		throws Exception {

		return addObjectRelationshipAndRelateObjectEntries(
			getObjectDefinition1(), getObjectDefinition2(),
			objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(), type);
	}

	protected ObjectRelationship addObjectRelationshipAndRelateObjectEntries(
			String deletionType, ObjectDefinition objectDefinition1,
			ObjectDefinition objectDefinition2, long primaryKey1,
			long primaryKey2, String type)
		throws Exception {

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				deletionType, objectDefinition1, objectDefinition2,
				TestPropsValues.getUserId(), type);

		ObjectRelationshipTestUtil.relateObjectEntries(
			primaryKey1, primaryKey2, objectRelationship,
			TestPropsValues.getUserId());

		return objectRelationship;
	}

	protected void addResourcePermission(
			String actionId, String name, Role role)
		throws Exception {

		resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), name,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			actionId);
	}

	protected FileEntry addTempFileEntry(
			byte[] content, ObjectDefinition objectDefinition, String title)
		throws Exception {

		return TempFileEntryUtil.addTempFileEntry(
			testGroupId, TestPropsValues.getUserId(),
			objectDefinition.getPortletId(),
			TempFileEntryUtil.getTempFileName(title + ".txt"),
			FileUtil.createTempFile(content), ContentTypes.TEXT_PLAIN);
	}

	protected User addUser(String userName, String userPassword)
		throws Exception {

		String upperCaseFirstLetterUserName = StringUtil.upperCaseFirstLetter(
			userName);

		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			userPassword, userName + "@liferay.com", userName,
			LocaleUtil.getDefault(), upperCaseFirstLetterUserName,
			upperCaseFirstLetterUserName, null,
			ServiceContextTestUtil.getServiceContext());

		user.setEmailAddressVerified(true);

		return UserLocalServiceUtil.updateUser(user);
	}

	protected void assertFilterString(
			String expectedObjectFieldName,
			Serializable expectedObjectFieldValue, String filterString,
			ObjectDefinition objectDefinition)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			objectDefinition.getRESTContextPath() + "?filter=" + filterString,
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertNull(jsonObject.get("title"));
		Assert.assertNull(jsonObject.get("status"));

		Assert.assertEquals(1, itemsJSONArray.length());

		JSONObject itemJSONObject = itemsJSONArray.getJSONObject(0);

		Assert.assertEquals(
			String.valueOf(expectedObjectFieldValue),
			String.valueOf(itemJSONObject.get(expectedObjectFieldName)));
	}

	protected void assertItem(
		int index, JSONObject jsonObject, String objectFieldName,
		Object value) {

		Assert.assertEquals(
			String.valueOf(value),
			JSONUtil.getValueAsString(
				jsonObject, "JSONArray/items", "JSONObject/" + index,
				"Object/" + objectFieldName));
	}

	protected void assertLocationJSONObject(
		String expectedAddress, double expectedLatitude,
		double expectedLongitude, JSONObject locationJSONObject) {

		Assert.assertEquals(
			expectedAddress, locationJSONObject.getString("address"));

		JSONObject coordinatesJSONObject = locationJSONObject.getJSONObject(
			"coordinates");

		Assert.assertEquals(
			expectedLatitude, coordinatesJSONObject.getDouble("latitude"),
			0.0001);
		Assert.assertEquals(
			expectedLongitude, coordinatesJSONObject.getDouble("longitude"),
			0.0001);
	}

	protected void assertNotFound(JSONObject jsonObject) {
		Assert.assertEquals("NOT_FOUND", jsonObject.getString("status"));
		Assert.assertNull(jsonObject.get("title"));
	}

	protected void assertPatchCustomObjectEntry(
			Function<JSONObject, String> endpointFunction,
			ObjectDefinition childObjectDefinition,
			ObjectDefinition parentObjectDefinition, Object scopeKey)
		throws Exception {

		childObjectDefinition =
			objectDefinitionLocalService.getObjectDefinition(
				childObjectDefinition.getObjectDefinitionId());

		childObjectDefinition.setEnableObjectEntryDraft(true);

		childObjectDefinition =
			objectDefinitionLocalService.updateObjectDefinition(
				childObjectDefinition);

		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			parentObjectDefinition, OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);
		objectEntry3 = ObjectEntryTestUtil.addObjectEntry(
			parentObjectDefinition, OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			parentObjectDefinition, childObjectDefinition,
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		String objectRelationship1ERCObjectFieldName =
			ObjectFieldSettingUtil.getValue(
				ObjectFieldSettingConstants.
					NAME_OBJECT_RELATIONSHIP_ERC_OBJECT_FIELD_NAME,
				objectFieldLocalService.getObjectField(
					objectRelationship1.getObjectFieldId2()));

		objectRelationship2 = ObjectRelationshipTestUtil.addObjectRelationship(
			parentObjectDefinition, childObjectDefinition,
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		String objectRelationship2IdObjectFieldName =
			ObjectFieldSettingUtil.getValue(
				ObjectFieldSettingConstants.
					NAME_OBJECT_RELATIONSHIP_ERC_OBJECT_FIELD_NAME,
				objectFieldLocalService.getObjectField(
					objectRelationship2.getObjectFieldId2())
			).replace(
				"ERC", "Id"
			);

		BigDecimal randomBigDecimal = BigDecimal.valueOf(
			RandomTestUtil.randomDouble()
		).setScale(
			5, RoundingMode.HALF_UP
		);
		boolean randomBoolean = RandomTestUtil.randomBoolean();
		Date randomDate1 = RandomTestUtil.nextDate();
		Date randomDate2 = RandomTestUtil.nextDate();
		Date randomDate3 = RandomTestUtil.nextDate();
		String randomExternalReferenceCode = RandomTestUtil.randomString();
		float randomFloat = RandomTestUtil.randomFloat();
		int randomInt = RandomTestUtil.randomInt();
		long randomLong = RandomTestUtil.randomLong(
			ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MIN,
			ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MAX);
		String randomString1 = RandomTestUtil.randomString();
		String randomString2 = RandomTestUtil.randomString();
		String randomString3 = RandomTestUtil.randomString();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				_OBJECT_FIELD_NAME_RICH_TEXT, randomString2
			).put(
				OBJECT_FIELD_NAME_BOOLEAN, randomBoolean
			).put(
				OBJECT_FIELD_NAME_DATE, dateFormat.format(randomDate1)
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeDateFormat.format(randomDate2)
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_UTC,
				dateTimeDateFormat.format(randomDate3)
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat
			).put(
				OBJECT_FIELD_NAME_INTEGER, randomInt
			).put(
				OBJECT_FIELD_NAME_LONG_INTEGER, randomLong
			).put(
				OBJECT_FIELD_NAME_LONG_TEXT, randomString1
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				JSONUtil.putAll(LIST_TYPE_ENTRY_KEY_1, LIST_TYPE_ENTRY_KEY_2)
			).put(
				OBJECT_FIELD_NAME_PICKLIST, LIST_TYPE_ENTRY_KEY_3
			).put(
				OBJECT_FIELD_NAME_PRECISION_DECIMAL, randomBigDecimal
			).put(
				OBJECT_FIELD_NAME_TEXT, randomString3
			).put(
				objectRelationship1ERCObjectFieldName,
				objectEntry2.getExternalReferenceCode()
			).put(
				objectRelationship2IdObjectFieldName,
				objectEntry2.getObjectEntryId()
			).put(
				"externalReferenceCode", randomExternalReferenceCode
			).put(
				"status", JSONUtil.put("code", WorkflowConstants.STATUS_DRAFT)
			).toString(),
			getEndpoint(childObjectDefinition, scopeKey), Http.Method.POST);

		JSONObject expectedJSONObject = JSONUtil.put(
			_OBJECT_FIELD_NAME_RICH_TEXT, randomString2
		).put(
			OBJECT_FIELD_NAME_BOOLEAN, randomBoolean
		).put(
			OBJECT_FIELD_NAME_DATE,
			dateFormat.format(randomDate1) + "T00:00:00.000Z"
		).put(
			OBJECT_FIELD_NAME_DATE_TIME_INPUT,
			StringUtil.removeLast(dateTimeDateFormat.format(randomDate2), "Z")
		).put(
			OBJECT_FIELD_NAME_DATE_TIME_UTC,
			dateTimeDateFormat.format(randomDate3)
		).put(
			OBJECT_FIELD_NAME_DECIMAL, randomFloat
		).put(
			OBJECT_FIELD_NAME_INTEGER, randomInt
		).put(
			OBJECT_FIELD_NAME_LONG_INTEGER, (double)randomLong
		).put(
			OBJECT_FIELD_NAME_LONG_TEXT, randomString1
		).put(
			OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
			JSONUtil.putAll(
				JSONUtil.put(
					"key", LIST_TYPE_ENTRY_KEY_1
				).put(
					"name", _listTypeEntry1.getName(LocaleUtil.getDefault())
				),
				JSONUtil.put(
					"key", LIST_TYPE_ENTRY_KEY_2
				).put(
					"name", _listTypeEntry2.getName(LocaleUtil.getDefault())
				))
		).put(
			OBJECT_FIELD_NAME_PICKLIST,
			JSONUtil.put(
				"key", LIST_TYPE_ENTRY_KEY_3
			).put(
				"name", _listTypeEntry3.getName(LocaleUtil.getDefault())
			)
		).put(
			OBJECT_FIELD_NAME_PRECISION_DECIMAL, randomBigDecimal
		).put(
			OBJECT_FIELD_NAME_TEXT, randomString3
		).put(
			objectRelationship1ERCObjectFieldName,
			objectEntry2.getExternalReferenceCode()
		).put(
			objectRelationship2IdObjectFieldName,
			(int)objectEntry2.getObjectEntryId()
		).put(
			"externalReferenceCode", randomExternalReferenceCode
		).put(
			"id", (Object)jsonObject.getLong("id")
		).put(
			"status", JSONUtil.put("code", WorkflowConstants.STATUS_DRAFT)
		);

		String endpoint = endpointFunction.apply(jsonObject);

		JSONAssert.assertEquals(
			expectedJSONObject.toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"externalReferenceCode", randomExternalReferenceCode
				).toString(),
				endpoint, Http.Method.PATCH
			).toString(),
			JSONCompareMode.LENIENT);

		JSONAssert.assertEquals(
			expectedJSONObject.toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"externalReferenceCode", randomExternalReferenceCode
				).toString(),
				endpoint, Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);

		// Boolean field

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, OBJECT_FIELD_NAME_BOOLEAN, !randomBoolean);

		assertPatchCustomObjectEntry(
			endpoint, expectedJSONObject, OBJECT_FIELD_NAME_BOOLEAN,
			!randomBoolean);

		// Date field

		Date date = new Date(randomDate1.getTime() + (24 * 3600 * 1000));

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, OBJECT_FIELD_NAME_DATE,
			dateFormat.format(date) + "T00:00:00.000Z");

		assertPatchCustomObjectEntry(
			endpoint, expectedJSONObject, OBJECT_FIELD_NAME_DATE,
			dateFormat.format(date));

		// Date time field ('Input as value' type)

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, OBJECT_FIELD_NAME_DATE_TIME_INPUT,
			StringUtil.removeLast(dateTimeDateFormat.format(date), "Z"));

		assertPatchCustomObjectEntry(
			endpoint, expectedJSONObject, OBJECT_FIELD_NAME_DATE_TIME_INPUT,
			dateTimeDateFormat.format(date));

		// Date time field ('UTC' type)

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, OBJECT_FIELD_NAME_DATE_TIME_UTC,
			dateTimeDateFormat.format(date));

		assertPatchCustomObjectEntry(
			endpoint, expectedJSONObject, OBJECT_FIELD_NAME_DATE_TIME_UTC,
			dateTimeDateFormat.format(date));

		// Decimal field

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, OBJECT_FIELD_NAME_DECIMAL, randomFloat + 1);

		assertPatchCustomObjectEntry(
			endpoint, expectedJSONObject, OBJECT_FIELD_NAME_DECIMAL,
			randomFloat + 1);

		// Integer field

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, OBJECT_FIELD_NAME_INTEGER, randomInt + 1);

		assertPatchCustomObjectEntry(
			endpoint, expectedJSONObject, OBJECT_FIELD_NAME_INTEGER,
			randomInt + 1);

		// Long integer field

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, OBJECT_FIELD_NAME_LONG_INTEGER, randomLong + 1);

		assertPatchCustomObjectEntry(
			endpoint, expectedJSONObject, OBJECT_FIELD_NAME_LONG_INTEGER,
			randomLong + 1);

		// Long text field

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, OBJECT_FIELD_NAME_LONG_TEXT,
			"b" + randomString1);

		assertPatchCustomObjectEntry(
			endpoint, expectedJSONObject, OBJECT_FIELD_NAME_LONG_TEXT,
			"b" + randomString1);

		// Multiselect picklist field

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
			JSONUtil.putAll(
				JSONUtil.put(
					"key", LIST_TYPE_ENTRY_KEY_2
				).put(
					"name", _listTypeEntry2.getName(LocaleUtil.getDefault())
				),
				JSONUtil.put(
					"key", LIST_TYPE_ENTRY_KEY_3
				).put(
					"name", _listTypeEntry3.getName(LocaleUtil.getDefault())
				)));

		assertPatchCustomObjectEntry(
			endpoint, expectedJSONObject,
			OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
			JSONUtil.putAll(LIST_TYPE_ENTRY_KEY_2, LIST_TYPE_ENTRY_KEY_3));

		// Picklist field

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, OBJECT_FIELD_NAME_PICKLIST,
			JSONUtil.put(
				"key", LIST_TYPE_ENTRY_KEY_1
			).put(
				"name", _listTypeEntry1.getName(LocaleUtil.getDefault())
			));

		assertPatchCustomObjectEntry(
			endpoint, expectedJSONObject, OBJECT_FIELD_NAME_PICKLIST,
			LIST_TYPE_ENTRY_KEY_1);

		// Relationship ERC field

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, objectRelationship1ERCObjectFieldName,
			objectEntry3.getExternalReferenceCode());

		assertPatchCustomObjectEntry(
			endpoint, expectedJSONObject, objectRelationship1ERCObjectFieldName,
			objectEntry3.getExternalReferenceCode());

		// Relationship Id field

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, objectRelationship2IdObjectFieldName,
			objectEntry3.getObjectEntryId());

		assertPatchCustomObjectEntry(
			endpoint, expectedJSONObject, objectRelationship2IdObjectFieldName,
			objectEntry3.getObjectEntryId());

		// Precision decimal field

		BigDecimal bigDecimal = randomBigDecimal.add(BigDecimal.ONE);

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, OBJECT_FIELD_NAME_PRECISION_DECIMAL,
			bigDecimal);

		assertPatchCustomObjectEntry(
			endpoint, expectedJSONObject, OBJECT_FIELD_NAME_PRECISION_DECIMAL,
			bigDecimal);

		// Rich text field

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, _OBJECT_FIELD_NAME_RICH_TEXT,
			"b" + randomString2);

		assertPatchCustomObjectEntry(
			endpoint, expectedJSONObject, _OBJECT_FIELD_NAME_RICH_TEXT,
			"b" + randomString2);

		// Text field

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, OBJECT_FIELD_NAME_TEXT, "b" + randomString3);

		assertPatchCustomObjectEntry(
			endpoint, expectedJSONObject, OBJECT_FIELD_NAME_TEXT,
			"b" + randomString3);
	}

	protected void assertPatchCustomObjectEntry(
			String endpoint, JSONObject expectedJSONObject, String fieldName,
			Object fieldValue)
		throws Exception {

		JSONAssert.assertEquals(
			expectedJSONObject.toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					fieldName, fieldValue
				).put(
					"externalReferenceCode",
					expectedJSONObject.get("externalReferenceCode")
				).toString(),
				endpoint, Http.Method.PATCH
			).toString(),
			JSONCompareMode.LENIENT);
	}

	protected void assertPutCustomObjectEntry(
			Function<JSONObject, String> endpointFunction,
			ObjectDefinition childObjectDefinition,
			ObjectDefinition parentObjectDefinition, Object scopeKey)
		throws Exception {

		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			parentObjectDefinition, OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);
		objectEntry3 = ObjectEntryTestUtil.addObjectEntry(
			parentObjectDefinition, OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			parentObjectDefinition, childObjectDefinition,
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		ObjectField objectRelationship1ObjectField =
			objectFieldLocalService.getObjectField(
				objectRelationship1.getObjectFieldId2());

		String objectRelationship1ERCObjectFieldName =
			ObjectFieldSettingUtil.getValue(
				ObjectFieldSettingConstants.
					NAME_OBJECT_RELATIONSHIP_ERC_OBJECT_FIELD_NAME,
				objectRelationship1ObjectField);

		objectRelationship2 = ObjectRelationshipTestUtil.addObjectRelationship(
			parentObjectDefinition, childObjectDefinition,
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		ObjectField objectRelationship2ObjectField =
			objectFieldLocalService.getObjectField(
				objectRelationship2.getObjectFieldId2());

		String objectRelationship2ERCObjectFieldName =
			ObjectFieldSettingUtil.getValue(
				ObjectFieldSettingConstants.
					NAME_OBJECT_RELATIONSHIP_ERC_OBJECT_FIELD_NAME,
				objectRelationship2ObjectField);

		BigDecimal randomBigDecimal = BigDecimal.valueOf(
			RandomTestUtil.randomDouble()
		).setScale(
			5, RoundingMode.HALF_UP
		);
		boolean randomBoolean = RandomTestUtil.randomBoolean();
		Date randomDate1 = RandomTestUtil.nextDate();
		Date randomDate2 = RandomTestUtil.nextDate();
		Date randomDate3 = RandomTestUtil.nextDate();
		String randomExternalReferenceCode = RandomTestUtil.randomString();
		float randomFloat = RandomTestUtil.randomFloat();
		int randomInt = RandomTestUtil.randomInt();
		long randomLong = RandomTestUtil.randomLong(
			ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MIN,
			ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MAX);
		String randomString1 = RandomTestUtil.randomString();
		String randomString2 = RandomTestUtil.randomString();
		String randomString3 = RandomTestUtil.randomString();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				_OBJECT_FIELD_NAME_RICH_TEXT, randomString2
			).put(
				OBJECT_FIELD_NAME_BOOLEAN, randomBoolean
			).put(
				OBJECT_FIELD_NAME_DATE, dateFormat.format(randomDate1)
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				dateTimeDateFormat.format(randomDate2)
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_UTC,
				dateTimeDateFormat.format(randomDate3)
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat
			).put(
				OBJECT_FIELD_NAME_INTEGER, randomInt
			).put(
				OBJECT_FIELD_NAME_LONG_INTEGER, randomLong
			).put(
				OBJECT_FIELD_NAME_LONG_TEXT, randomString1
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				JSONUtil.putAll(LIST_TYPE_ENTRY_KEY_1, LIST_TYPE_ENTRY_KEY_2)
			).put(
				OBJECT_FIELD_NAME_PICKLIST, LIST_TYPE_ENTRY_KEY_3
			).put(
				OBJECT_FIELD_NAME_PRECISION_DECIMAL, randomBigDecimal
			).put(
				OBJECT_FIELD_NAME_TEXT, randomString3
			).put(
				objectRelationship1ERCObjectFieldName,
				objectEntry2.getExternalReferenceCode()
			).put(
				objectRelationship1ObjectField.getName(),
				objectEntry3.getObjectEntryId()
			).put(
				objectRelationship2ERCObjectFieldName,
				objectEntry3.getExternalReferenceCode()
			).put(
				objectRelationship2ObjectField.getName(),
				objectEntry2.getObjectEntryId()
			).put(
				"externalReferenceCode", randomExternalReferenceCode
			).toString(),
			getEndpoint(childObjectDefinition, scopeKey), Http.Method.POST);

		String endpoint = endpointFunction.apply(jsonObject);

		HTTPTestUtil.invokeToJSONObject(
			JSONFactoryUtil.getNullJSON(), endpoint, Http.Method.PUT);

		JSONAssert.assertEquals(
			JSONUtil.put(
				_OBJECT_FIELD_NAME_RICH_TEXT, ""
			).put(
				OBJECT_FIELD_NAME_BOOLEAN, false
			).put(
				OBJECT_FIELD_NAME_DATE, (Timestamp)null
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT, (Timestamp)null
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_UTC, (Timestamp)null
			).put(
				OBJECT_FIELD_NAME_DECIMAL, 0
			).put(
				OBJECT_FIELD_NAME_INTEGER, 0
			).put(
				OBJECT_FIELD_NAME_LONG_INTEGER, 0
			).put(
				OBJECT_FIELD_NAME_LONG_TEXT, ""
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				JSONFactoryUtil.createJSONArray()
			).put(
				OBJECT_FIELD_NAME_PICKLIST, JSONUtil.put("key", "")
			).put(
				OBJECT_FIELD_NAME_PRECISION_DECIMAL, 0
			).put(
				OBJECT_FIELD_NAME_TEXT, ""
			).put(
				objectRelationship1ERCObjectFieldName,
				objectEntry3.getExternalReferenceCode()
			).put(
				objectRelationship1ObjectField.getName(),
				(int)objectEntry3.getObjectEntryId()
			).put(
				objectRelationship2ERCObjectFieldName,
				objectEntry2.getExternalReferenceCode()
			).put(
				objectRelationship2ObjectField.getName(),
				(int)objectEntry2.getObjectEntryId()
			).put(
				"externalReferenceCode", randomExternalReferenceCode
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null, endpoint, Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);

		JSONAssert.assertEquals(
			JSONUtil.put(
				_OBJECT_FIELD_NAME_RICH_TEXT, randomString2
			).put(
				OBJECT_FIELD_NAME_BOOLEAN, randomBoolean
			).put(
				OBJECT_FIELD_NAME_DATE,
				dateFormat.format(randomDate1) + "T00:00:00.000Z"
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				StringUtil.removeLast(
					dateTimeDateFormat.format(randomDate2), "Z")
			).put(
				OBJECT_FIELD_NAME_DATE_TIME_UTC,
				dateTimeDateFormat.format(randomDate3)
			).put(
				OBJECT_FIELD_NAME_DECIMAL, randomFloat
			).put(
				OBJECT_FIELD_NAME_INTEGER, randomInt
			).put(
				OBJECT_FIELD_NAME_LONG_INTEGER, (double)randomLong
			).put(
				OBJECT_FIELD_NAME_LONG_TEXT, randomString1
			).put(
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
				JSONUtil.putAll(
					JSONUtil.put(
						"key", LIST_TYPE_ENTRY_KEY_1
					).put(
						"name", _listTypeEntry1.getName(LocaleUtil.getDefault())
					),
					JSONUtil.put(
						"key", LIST_TYPE_ENTRY_KEY_2
					).put(
						"name", _listTypeEntry2.getName(LocaleUtil.getDefault())
					))
			).put(
				OBJECT_FIELD_NAME_PICKLIST,
				JSONUtil.put(
					"key", LIST_TYPE_ENTRY_KEY_3
				).put(
					"name", _listTypeEntry3.getName(LocaleUtil.getDefault())
				)
			).put(
				OBJECT_FIELD_NAME_PRECISION_DECIMAL, randomBigDecimal
			).put(
				OBJECT_FIELD_NAME_TEXT, randomString3
			).put(
				objectRelationship1ERCObjectFieldName,
				objectEntry2.getExternalReferenceCode()
			).put(
				objectRelationship1ObjectField.getName(),
				(int)objectEntry2.getObjectEntryId()
			).put(
				objectRelationship2ERCObjectFieldName,
				objectEntry3.getExternalReferenceCode()
			).put(
				objectRelationship2ObjectField.getName(),
				(int)objectEntry3.getObjectEntryId()
			).put(
				"externalReferenceCode", randomExternalReferenceCode
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					_OBJECT_FIELD_NAME_RICH_TEXT, randomString2
				).put(
					OBJECT_FIELD_NAME_BOOLEAN, randomBoolean
				).put(
					OBJECT_FIELD_NAME_DATE, dateFormat.format(randomDate1)
				).put(
					OBJECT_FIELD_NAME_DATE_TIME_INPUT,
					dateTimeDateFormat.format(randomDate2)
				).put(
					OBJECT_FIELD_NAME_DATE_TIME_UTC,
					dateTimeDateFormat.format(randomDate3)
				).put(
					OBJECT_FIELD_NAME_DECIMAL, randomFloat
				).put(
					OBJECT_FIELD_NAME_INTEGER, randomInt
				).put(
					OBJECT_FIELD_NAME_LONG_INTEGER, randomLong
				).put(
					OBJECT_FIELD_NAME_LONG_TEXT, randomString1
				).put(
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
					JSONUtil.putAll(
						LIST_TYPE_ENTRY_KEY_1, LIST_TYPE_ENTRY_KEY_2)
				).put(
					OBJECT_FIELD_NAME_PICKLIST, LIST_TYPE_ENTRY_KEY_3
				).put(
					OBJECT_FIELD_NAME_PRECISION_DECIMAL, randomBigDecimal
				).put(
					OBJECT_FIELD_NAME_TEXT, randomString3
				).put(
					objectRelationship1ERCObjectFieldName,
					objectEntry3.getExternalReferenceCode()
				).put(
					objectRelationship1ObjectField.getName(),
					objectEntry2.getObjectEntryId()
				).put(
					objectRelationship2ERCObjectFieldName,
					objectEntry2.getExternalReferenceCode()
				).put(
					objectRelationship2ObjectField.getName(),
					objectEntry3.getObjectEntryId()
				).toString(),
				endpoint, Http.Method.PUT
			).toString(),
			JSONCompareMode.LENIENT);
	}

	protected JSONArray createObjectEntriesJSONArray(
			String[] externalReferenceCodeValues, String objectFieldName,
			String[] objectFieldValues)
		throws Exception {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (int i = 0; i < objectFieldValues.length; i++) {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				JSONUtil.put(
					objectFieldName, objectFieldValues[i]
				).put(
					"externalReferenceCode", externalReferenceCodeValues[i]
				).toString());

			jsonArray.put(jsonObject);
		}

		return jsonArray;
	}

	protected String escape(String string) {
		return URLCodec.encodeURL(string);
	}

	protected Company getCompanyByWebId() throws Exception {
		try {
			return companyLocalService.getCompanyByWebId("www.able.com");
		}
		catch (NoSuchCompanyException noSuchCompanyException) {
			return CompanyTestUtil.addCompanyWithWebId("www.able.com");
		}
	}

	protected String getEndpoint(
		long objectEntryId, ObjectRelationship objectRelationship,
		ObjectDefinition objectDefinition) {

		return StringBundler.concat(
			objectDefinition.getRESTContextPath(), StringPool.SLASH,
			objectEntryId, "?nestedFields=", objectRelationship.getName());
	}

	protected String getEndpoint(
		ObjectDefinition objectDefinition, Object scopeKey) {

		ObjectScopeProvider objectScopeProvider =
			objectScopeProviderRegistry.getObjectScopeProvider(
				objectDefinition.getScope());

		if (objectScopeProvider.isGroupAware()) {
			return objectDefinition.getRESTContextPath() + "/scopes/" +
				scopeKey;
		}

		return objectDefinition.getRESTContextPath();
	}

	protected ObjectDefinition getObjectDefinition1() throws Exception {
		if (objectDefinition1 != null) {
			return objectDefinition1;
		}

		String objectDefinitionName = ObjectDefinitionTestUtil.getRandomName();

		objectDefinition1 = ObjectDefinitionTestUtil.publishObjectDefinition(
			objectDefinitionName, _createObjectFields(objectDefinitionName),
			ObjectDefinitionConstants.SCOPE_COMPANY);

		return objectDefinition1;
	}

	protected ObjectDefinition getObjectDefinition2() throws Exception {
		if (objectDefinition2 != null) {
			return objectDefinition2;
		}

		objectDefinition2 = ObjectDefinitionTestUtil.publishObjectDefinition(
			ObjectDefinitionTestUtil.getRandomName(),
			Arrays.asList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
					ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
					Arrays.asList(
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.
								NAME_ACCEPTED_FILE_EXTENSIONS
						).value(
							"pdf"
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_FILE_SOURCE
						).value(
							ObjectFieldSettingConstants.VALUE_DOCS_AND_MEDIA
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
						).value(
							String.valueOf(MAX_FILE_SIZE_VALUE)
						).build()),
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN,
					ObjectFieldConstants.DB_TYPE_BOOLEAN, true, false, null,
					RandomTestUtil.randomString(), OBJECT_FIELD_NAME_BOOLEAN,
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_DATE,
					ObjectFieldConstants.DB_TYPE_DATE,
					RandomTestUtil.randomString(), OBJECT_FIELD_NAME_DATE,
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME,
					ObjectFieldConstants.DB_TYPE_DATE_TIME, true, true, null,
					RandomTestUtil.randomString(),
					OBJECT_FIELD_NAME_DATE_TIME_INPUT,
					Collections.singletonList(
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_TIME_STORAGE
						).value(
							ObjectFieldSettingConstants.
								VALUE_USE_INPUT_AS_ENTERED
						).build()),
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME,
					ObjectFieldConstants.DB_TYPE_DATE_TIME, true, true, null,
					RandomTestUtil.randomString(),
					OBJECT_FIELD_NAME_DATE_TIME_UTC,
					Collections.singletonList(
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_TIME_STORAGE
						).value(
							ObjectFieldSettingConstants.VALUE_CONVERT_TO_UTC
						).build()),
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_DECIMAL,
					ObjectFieldConstants.DB_TYPE_DOUBLE, true, false, null,
					RandomTestUtil.randomString(), OBJECT_FIELD_NAME_DECIMAL,
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_INTEGER,
					ObjectFieldConstants.DB_TYPE_INTEGER, true, true, null,
					RandomTestUtil.randomString(), OBJECT_FIELD_NAME_INTEGER,
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER,
					ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
					RandomTestUtil.randomString(),
					OBJECT_FIELD_NAME_LONG_INTEGER, false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_LONG_TEXT,
					ObjectFieldConstants.DB_TYPE_CLOB, false, false, null,
					RandomTestUtil.randomString(), OBJECT_FIELD_NAME_LONG_TEXT,
					false),
				ObjectFieldUtil.createObjectField(
					listTypeDefinition.getListTypeDefinitionId(),
					ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST,
					null, ObjectFieldConstants.DB_TYPE_STRING, true, false,
					null, RandomTestUtil.randomString(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, false, false),
				ObjectFieldUtil.createObjectField(
					listTypeDefinition.getListTypeDefinitionId(),
					ObjectFieldConstants.BUSINESS_TYPE_PICKLIST, null,
					ObjectFieldConstants.DB_TYPE_STRING, true, false, null,
					RandomTestUtil.randomString(), OBJECT_FIELD_NAME_PICKLIST,
					false, false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_PRECISION_DECIMAL,
					ObjectFieldConstants.DB_TYPE_BIG_DECIMAL, true, false, null,
					RandomTestUtil.randomString(),
					OBJECT_FIELD_NAME_PRECISION_DECIMAL, false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
					RandomTestUtil.randomString(), OBJECT_FIELD_NAME_TEXT,
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
					RandomTestUtil.randomString(), OBJECT_FIELD_NAME_2, false)),
			ObjectDefinitionConstants.SCOPE_COMPANY);

		return objectDefinition2;
	}

	protected ObjectDefinition getObjectDefinition3() throws Exception {
		if (objectDefinition3 != null) {
			return objectDefinition3;
		}

		objectDefinition3 = ObjectDefinitionTestUtil.publishObjectDefinition(
			ObjectDefinitionTestUtil.getRandomName(),
			Arrays.asList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN,
					ObjectFieldConstants.DB_TYPE_BOOLEAN, true, false, null,
					RandomTestUtil.randomString(), OBJECT_FIELD_NAME_BOOLEAN,
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_DATE,
					ObjectFieldConstants.DB_TYPE_DATE,
					RandomTestUtil.randomString(), OBJECT_FIELD_NAME_DATE,
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME,
					ObjectFieldConstants.DB_TYPE_DATE_TIME, true, true, null,
					RandomTestUtil.randomString(),
					OBJECT_FIELD_NAME_DATE_TIME_INPUT,
					Collections.singletonList(
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_TIME_STORAGE
						).value(
							ObjectFieldSettingConstants.
								VALUE_USE_INPUT_AS_ENTERED
						).build()),
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME,
					ObjectFieldConstants.DB_TYPE_DATE_TIME, true, true, null,
					RandomTestUtil.randomString(),
					OBJECT_FIELD_NAME_DATE_TIME_UTC,
					Collections.singletonList(
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_TIME_STORAGE
						).value(
							ObjectFieldSettingConstants.VALUE_CONVERT_TO_UTC
						).build()),
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_DECIMAL,
					ObjectFieldConstants.DB_TYPE_DOUBLE, true, false, null,
					RandomTestUtil.randomString(), OBJECT_FIELD_NAME_DECIMAL,
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_INTEGER,
					ObjectFieldConstants.DB_TYPE_INTEGER, true, true, null,
					RandomTestUtil.randomString(), OBJECT_FIELD_NAME_INTEGER,
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER,
					ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
					RandomTestUtil.randomString(),
					OBJECT_FIELD_NAME_LONG_INTEGER, false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_LONG_TEXT,
					ObjectFieldConstants.DB_TYPE_CLOB, false, false, null,
					RandomTestUtil.randomString(), OBJECT_FIELD_NAME_LONG_TEXT,
					false),
				ObjectFieldUtil.createObjectField(
					listTypeDefinition.getListTypeDefinitionId(),
					ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST,
					null, ObjectFieldConstants.DB_TYPE_STRING, true, false,
					null, RandomTestUtil.randomString(),
					OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, false, false),
				ObjectFieldUtil.createObjectField(
					listTypeDefinition.getListTypeDefinitionId(),
					ObjectFieldConstants.BUSINESS_TYPE_PICKLIST, null,
					ObjectFieldConstants.DB_TYPE_STRING, true, false, null,
					RandomTestUtil.randomString(), OBJECT_FIELD_NAME_PICKLIST,
					false, false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_PRECISION_DECIMAL,
					ObjectFieldConstants.DB_TYPE_BIG_DECIMAL, true, false, null,
					RandomTestUtil.randomString(),
					OBJECT_FIELD_NAME_PRECISION_DECIMAL, false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
					RandomTestUtil.randomString(), OBJECT_FIELD_NAME_TEXT,
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
					RandomTestUtil.randomString(), OBJECT_FIELD_NAME_3, false)),
			ObjectDefinitionConstants.SCOPE_COMPANY);

		return objectDefinition3;
	}

	protected ObjectDefinition getObjectDefinition4() throws Exception {
		if (_objectDefinition4 != null) {
			return _objectDefinition4;
		}

		_objectDefinition4 = ObjectDefinitionTestUtil.publishObjectDefinition(
			ObjectDefinitionTestUtil.getRandomName(),
			Arrays.asList(
				new LongTextObjectFieldBuilder(
				).indexed(
					true
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).localized(
					true
				).name(
					OBJECT_FIELD_NAME_LOCALIZED_LONG_TEXT
				).build(),
				new RichTextObjectFieldBuilder(
				).indexed(
					true
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).localized(
					true
				).name(
					OBJECT_FIELD_NAME_LOCALIZED_RICH_TEXT
				).build(),
				new TextObjectFieldBuilder(
				).indexed(
					true
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).localized(
					true
				).name(
					"name"
				).build(),
				new TextObjectFieldBuilder(
				).indexed(
					true
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).localized(
					true
				).name(
					OBJECT_FIELD_NAME_LOCALIZED_TEXT
				).build(),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_LONG_TEXT,
					ObjectFieldConstants.DB_TYPE_CLOB, false, false, null,
					RandomTestUtil.randomString(), OBJECT_FIELD_NAME_LONG_TEXT,
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
					RandomTestUtil.randomString(), _OBJECT_FIELD_NAME_RICH_TEXT,
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
					RandomTestUtil.randomString(), OBJECT_FIELD_NAME_TEXT,
					false)),
			ObjectDefinitionConstants.SCOPE_COMPANY,
			TestPropsValues.getUserId());

		_objectEntry5 = ObjectEntryTestUtil.addObjectEntry(
			_objectDefinition4,
			HashMapBuilder.<String, Serializable>put(
				OBJECT_FIELD_NAME_LOCALIZED_LONG_TEXT, "name2_text_english"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_LONG_TEXT + "_i18n",
				HashMapBuilder.<String, Serializable>put(
					"en_US", "longTextEng"
				).put(
					"es_ES", "longTextEsp"
				).build()
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_RICH_TEXT, "<p>c</p>\\n"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_RICH_TEXT + "_i18n",
				HashMapBuilder.<String, Serializable>put(
					"en_US", "<p>richTextEng</p>"
				).put(
					"es_ES", "<p>richTextEsp</p>"
				).build()
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_TEXT, "name1_text_english"
			).put(
				OBJECT_FIELD_NAME_LOCALIZED_TEXT + "_i18n",
				HashMapBuilder.<String, Serializable>put(
					"en_US", "textEng"
				).put(
					"es_ES", "textEsp"
				).build()
			).build());

		return _objectDefinition4;
	}

	protected ObjectEntry getObjectEntry5() throws Exception {
		getObjectDefinition4();

		return _objectEntry5;
	}

	protected JSONObject getObjectEntryJSONObject(
			Integer nestedFieldDepth, String nestedFieldName,
			ObjectDefinition objectDefinition)
		throws Exception {

		String endpoint = StringBundler.concat(
			objectDefinition.getRESTContextPath(), "?nestedFields=",
			nestedFieldName);

		if (nestedFieldDepth != null) {
			endpoint += "&nestedFieldsDepth=" + nestedFieldDepth;
		}

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, endpoint, Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());

		return itemsJSONArray.getJSONObject(0);
	}

	protected JSONObject getObjectEntryJSONObject(
			ObjectDefinition objectDefinition, String objectEntryId)
		throws Exception {

		return HTTPTestUtil.invokeToJSONObject(
			null,
			objectDefinition.getRESTContextPath() + StringPool.SLASH +
				objectEntryId,
			Http.Method.GET);
	}

	protected ObjectEntryResource getObjectEntryResource(
			ObjectDefinition objectDefinition, User user)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(
			BaseObjectEntryResourceTestCase.class);

		try (ServiceTrackerMap<String, ObjectEntryResource> serviceTrackerMap =
				ServiceTrackerMapFactory.openSingleValueMap(
					bundle.getBundleContext(), ObjectEntryResource.class,
					"entity.class.name")) {

			ObjectEntryResource objectEntryResource =
				serviceTrackerMap.getService(
					StringBundler.concat(
						com.liferay.object.rest.dto.v1_0.ObjectEntry.class.
							getName(),
						StringPool.POUND,
						StringUtil.toLowerCase(
							objectDefinition.getShortName())));

			objectEntryResource.setContextAcceptLanguage(
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

				});
			objectEntryResource.setContextCompany(
				companyLocalService.getCompany(
					objectDefinition.getCompanyId()));
			objectEntryResource.setContextUser(user);

			setObjectDefinition(objectDefinition, objectEntryResource);

			return objectEntryResource;
		}
	}

	protected JSONObject getOwnerPermissionsJSONObject(
		ObjectDefinition objectDefinition) {

		String[] actionIds = {
			ActionKeys.DELETE, ActionKeys.PERMISSIONS, ActionKeys.UPDATE,
			ActionKeys.VIEW
		};

		for (ObjectField objectField :
				objectFieldLocalService.getObjectFieldsByBusinessType(
					objectDefinition.getObjectDefinitionId(),
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

			actionIds = ArrayUtil.append(
				actionIds, objectField.getAttachmentDownloadActionKey());
		}

		return getPermissionsJSONObject(actionIds, RoleConstants.OWNER);
	}

	protected JSONObject getPermissionsJSONObject(
		String[] actionIds, String roleName) {

		return JSONUtil.put(
			"actionIds", actionIds
		).put(
			"roleName", roleName
		);
	}

	protected ObjectDefinition getSiteScopedObjectDefinition1()
		throws Exception {

		if (siteScopedObjectDefinition1 != null) {
			return siteScopedObjectDefinition1;
		}

		String objectDefinitionName = ObjectDefinitionTestUtil.getRandomName();

		siteScopedObjectDefinition1 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				objectDefinitionName, _createObjectFields(objectDefinitionName),
				ObjectDefinitionConstants.SCOPE_SITE);

		return siteScopedObjectDefinition1;
	}

	protected ObjectDefinition getSiteScopedObjectDefinition2()
		throws Exception {

		if (_siteScopedObjectDefinition2 != null) {
			return _siteScopedObjectDefinition2;
		}

		_siteScopedObjectDefinition2 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
						RandomTestUtil.randomString(), OBJECT_FIELD_NAME_2,
						false)),
				ObjectDefinitionConstants.SCOPE_SITE);

		return _siteScopedObjectDefinition2;
	}

	protected JSONObject postObjectEntry(
			long groupId, ObjectDefinition objectDefinition)
		throws Exception {

		return HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).toString(),
			getEndpoint(objectDefinition, groupId), Http.Method.POST);
	}

	protected TaxonomyCategory postTaxonomyVocabularyTaxonomyCategory()
		throws Exception {

		return postTaxonomyVocabularyTaxonomyCategory(
			testGroupId, assetVocabulary.getVocabularyId());
	}

	protected TaxonomyCategory postTaxonomyVocabularyTaxonomyCategory(
			long groupId, long taxonomyVocabularyId)
		throws Exception {

		return taxonomyCategoryResource.postTaxonomyVocabularyTaxonomyCategory(
			taxonomyVocabularyId,
			randomTaxonomyCategory(groupId, taxonomyVocabularyId));
	}

	protected ObjectDefinition publishLocalizedObjectDefinition(
			String objectFieldName)
		throws Exception {

		return ObjectDefinitionTestUtil.publishObjectDefinition(
			ObjectDefinitionTestUtil.getRandomName(),
			Collections.singletonList(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).localized(
					true
				).name(
					objectFieldName
				).build()),
			ObjectDefinitionConstants.SCOPE_COMPANY,
			TestPropsValues.getUserId());
	}

	protected TaxonomyCategory randomTaxonomyCategory(
		long groupId, long parentTaxonomyVocabularyId) {

		return new TaxonomyCategory() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				numberOfTaxonomyCategories = RandomTestUtil.randomInt();
				siteId = groupId;
				taxonomyCategoryUsageCount = RandomTestUtil.randomInt();

				setTaxonomyVocabularyId(parentTaxonomyVocabularyId);
			}
		};
	}

	protected void setObjectDefinition(
			ObjectDefinition objectDefinition,
			ObjectEntryResource objectEntryResource)
		throws Exception {

		Class<?> clazz = objectEntryResource.getClass();

		Method method = clazz.getMethod(
			"setObjectDefinition", ObjectDefinition.class);

		method.invoke(objectEntryResource, objectDefinition);
	}

	protected static final String ERC_VALUE_1 =
		"a" + RandomTestUtil.randomString();

	protected static final String ERC_VALUE_2 =
		"b" + RandomTestUtil.randomString();

	protected static final String ERC_VALUE_3 =
		"c" + RandomTestUtil.randomString();

	protected static final String LIST_TYPE_ENTRY_KEY_1 =
		"a" + RandomTestUtil.randomString();

	protected static final String LIST_TYPE_ENTRY_KEY_2 =
		"b" + RandomTestUtil.randomString();

	protected static final String LIST_TYPE_ENTRY_KEY_3 =
		"c" + RandomTestUtil.randomString();

	protected static final int MAX_FILE_SIZE_VALUE = 1;

	protected static final String NEW_OBJECT_FIELD_VALUE_1 =
		RandomTestUtil.randomString();

	protected static final String NEW_OBJECT_FIELD_VALUE_2 =
		RandomTestUtil.randomString();

	protected static final String OBJECT_FIELD_NAME_1 =
		"x" + RandomTestUtil.randomString();

	protected static final String OBJECT_FIELD_NAME_2 =
		"x" + RandomTestUtil.randomString();

	protected static final String OBJECT_FIELD_NAME_3 =
		"x" + RandomTestUtil.randomString();

	protected static final String
		OBJECT_FIELD_NAME_ATTACHMENT_CMS_BASIC_DOCUMENT_SOURCE =
			"x" + RandomTestUtil.randomString();

	protected static final String
		OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE =
			"x" + RandomTestUtil.randomString();

	protected static final String
		OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT_SOURCE =
			"x" + RandomTestUtil.randomString();

	protected static final String
		OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1 =
			"x" + RandomTestUtil.randomString();

	protected static final String
		OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2 =
			"x" + RandomTestUtil.randomString();

	protected static final String OBJECT_FIELD_NAME_BOOLEAN =
		"x" + RandomTestUtil.randomString();

	protected static final String OBJECT_FIELD_NAME_DATE =
		"x" + RandomTestUtil.randomString();

	protected static final String OBJECT_FIELD_NAME_DATE_TIME_INPUT =
		"x" + RandomTestUtil.randomString();

	protected static final String OBJECT_FIELD_NAME_DATE_TIME_UTC =
		"x" + RandomTestUtil.randomString();

	protected static final String OBJECT_FIELD_NAME_DECIMAL =
		"x" + RandomTestUtil.randomString();

	protected static final String OBJECT_FIELD_NAME_INTEGER =
		"x" + RandomTestUtil.randomString();

	protected static final String OBJECT_FIELD_NAME_LOCALIZED_LONG_TEXT =
		"x" + RandomTestUtil.randomString();

	protected static final String OBJECT_FIELD_NAME_LOCALIZED_RICH_TEXT =
		"x" + RandomTestUtil.randomString();

	protected static final String OBJECT_FIELD_NAME_LOCALIZED_TEXT =
		"x" + RandomTestUtil.randomString();

	protected static final String OBJECT_FIELD_NAME_LONG_INTEGER =
		"x" + RandomTestUtil.randomString();

	protected static final String OBJECT_FIELD_NAME_LONG_TEXT =
		"x" + RandomTestUtil.randomString();

	protected static final String OBJECT_FIELD_NAME_MULTISELECT_PICKLIST =
		"x" + RandomTestUtil.randomString();

	protected static final String OBJECT_FIELD_NAME_PICKLIST =
		"x" + RandomTestUtil.randomString();

	protected static final String OBJECT_FIELD_NAME_PRECISION_DECIMAL =
		"x" + RandomTestUtil.randomString();

	protected static final String OBJECT_FIELD_NAME_TEXT =
		"x" + RandomTestUtil.randomString();

	protected static final int OBJECT_FIELD_VALUE_1 =
		RandomTestUtil.randomInt();

	protected static final int OBJECT_FIELD_VALUE_2 =
		RandomTestUtil.randomInt();

	protected static final int OBJECT_FIELD_VALUE_3 =
		RandomTestUtil.randomInt();

	protected static final int OBJECT_FIELD_VALUE_4 =
		RandomTestUtil.randomInt();

	protected static final String TAG_1 = StringUtil.toLowerCase(
		RandomTestUtil.randomString());

	protected static AssetVocabulary assetVocabulary;
	protected static final DateFormat dateFormat =
		DateFormatFactoryUtil.getSimpleDateFormat("yyyy-MM-dd");
	protected static final DateFormat dateTimeDateFormat =
		DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	protected static TaxonomyCategoryResource taxonomyCategoryResource;
	protected static final TestDLFileEntryModelListener
		testDLFileEntryModelListener = new TestDLFileEntryModelListener();
	protected static long testGroupId;
	protected static final TestObjectEntryModelListener
		testObjectEntryModelListener = new TestObjectEntryModelListener();

	@Inject
	protected AssetVocabularyLocalService assetVocabularyLocalService;

	@Inject
	protected ClassNameLocalService classNameLocalService;

	@Inject
	protected CompanyLocalService companyLocalService;

	@Inject
	protected DepotEntryLocalService depotEntryLocalService;

	@DeleteAfterTestRun
	protected Group group;

	@Inject
	protected GroupLocalService groupLocalService;

	@Inject
	protected JSONFactory jsonFactory;

	protected ListTypeDefinition listTypeDefinition;

	@Inject
	protected ListTypeDefinitionLocalService listTypeDefinitionLocalService;

	protected ObjectDefinition objectDefinition1;
	protected ObjectDefinition objectDefinition2;
	protected ObjectDefinition objectDefinition3;

	@Inject
	protected ObjectDefinitionLocalService objectDefinitionLocalService;

	@Inject
	protected ObjectDefinitionSettingLocalService
		objectDefinitionSettingLocalService;

	protected ObjectEntry objectEntry1;
	protected ObjectEntry objectEntry2;
	protected ObjectEntry objectEntry3;
	protected ObjectEntry objectEntry4;

	@Inject
	protected ObjectEntryLocalService objectEntryLocalService;

	@Inject
	protected ObjectFieldBusinessTypeRegistry objectFieldBusinessTypeRegistry;

	@Inject
	protected ObjectFieldLocalService objectFieldLocalService;

	protected ObjectRelationship objectRelationship1;
	protected ObjectRelationship objectRelationship2;
	protected ObjectRelationship objectRelationship3;
	protected ObjectRelationship objectRelationship4;
	protected ObjectRelationship objectRelationship5;
	protected ObjectRelationship objectRelationship7;

	@Inject
	protected ObjectRelationshipLocalService objectRelationshipLocalService;

	@Inject
	protected ObjectScopeProviderRegistry objectScopeProviderRegistry;

	@Inject
	protected ObjectValidationRuleLocalService objectValidationRuleLocalService;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected RoleLocalService roleLocalService;

	protected ObjectDefinition siteScopedObjectDefinition1;
	protected SystemObjectDefinitionManager systemObjectDefinitionManager;
	protected JSONObject userAccountJSONObject;

	@Inject
	protected UserGroupRoleLocalService userGroupRoleLocalService;

	@Inject
	protected UserLocalService userLocalService;

	protected ObjectDefinition userSystemObjectDefinition;

	protected static class TestDLFileEntryModelListener
		extends BaseModelListener<DLFileEntry> {

		public Long getLastFileEntryId() {
			return _fileEntryIds.get(_fileEntryIds.size() - 1);
		}

		@Override
		public void onAfterCreate(DLFileEntry dlFileEntry)
			throws ModelListenerException {

			_fileEntryIds.add(dlFileEntry.getFileEntryId());
		}

		private List<Long> _fileEntryIds = new ArrayList<>();

	}

	protected static class TestObjectEntryModelListener
		extends BaseModelListener<ObjectEntry> {

		public Long getLastObjectEntryId() {
			return _objectEntryIds.get(_objectEntryIds.size() - 1);
		}

		@Override
		public void onAfterCreate(ObjectEntry objectEntry)
			throws ModelListenerException {

			_objectEntryIds.add(objectEntry.getObjectEntryId());
		}

		private List<Long> _objectEntryIds = new ArrayList<>();

	}

	protected enum Type {

		MANY_TO_MANY, MANY_TO_ONE, ONE_TO_MANY

	}

	private JSONObject _cloneJSONObject(
			JSONObject jsonObject, String objectFieldName,
			Object objectFieldValue)
		throws Exception {

		JSONObject clonedJSONObject = JSONFactoryUtil.createJSONObject(
			jsonObject.toString());

		return clonedJSONObject.put(objectFieldName, objectFieldValue);
	}

	private List<ObjectField> _createObjectFields(String objectDefinitionName) {
		return Arrays.asList(
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
				ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
				OBJECT_FIELD_NAME_ATTACHMENT_CMS_BASIC_DOCUMENT_SOURCE,
				OBJECT_FIELD_NAME_ATTACHMENT_CMS_BASIC_DOCUMENT_SOURCE,
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.
							NAME_ACCEPTED_FILE_EXTENSIONS
					).value(
						"jpg, txt"
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_FILE_SOURCE
					).value(
						ObjectFieldSettingConstants.VALUE_CMS_BASIC_DOCUMENT
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
					).value(
						String.valueOf(MAX_FILE_SIZE_VALUE)
					).build()),
				false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
				ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.
							NAME_ACCEPTED_FILE_EXTENSIONS
					).value(
						"jpg, txt"
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_FILE_SOURCE
					).value(
						ObjectFieldSettingConstants.VALUE_DOCS_AND_MEDIA
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
					).value(
						String.valueOf(MAX_FILE_SIZE_VALUE)
					).build()),
				false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
				ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT_SOURCE,
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT_SOURCE,
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.
							NAME_ACCEPTED_FILE_EXTENSIONS
					).value(
						"jpg, txt"
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_FILE_SOURCE
					).value(
						ObjectFieldSettingConstants.
							VALUE_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
					).value(
						String.valueOf(MAX_FILE_SIZE_VALUE)
					).build()),
				false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
				ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1,
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.
							NAME_ACCEPTED_FILE_EXTENSIONS
					).value(
						"jpg, txt"
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_FILE_SOURCE
					).value(
						ObjectFieldSettingConstants.
							VALUE_USER_COMPUTER_TO_DOCS_AND_MEDIA
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
					).value(
						String.valueOf(MAX_FILE_SIZE_VALUE)
					).build()),
				false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
				ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
				OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2,
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.
							NAME_ACCEPTED_FILE_EXTENSIONS
					).value(
						"txt"
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_FILE_SOURCE
					).value(
						ObjectFieldSettingConstants.
							VALUE_USER_COMPUTER_TO_DOCS_AND_MEDIA
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
					).value(
						String.valueOf(MAX_FILE_SIZE_VALUE)
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_SHOW_FILES_IN_LIBRARY
					).value(
						Boolean.TRUE.toString()
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_STORAGE_DL_FOLDER_PATH
					).value(
						StringPool.SLASH + objectDefinitionName
					).build()),
				false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN,
				ObjectFieldConstants.DB_TYPE_BOOLEAN, true, false, null,
				RandomTestUtil.randomString(), OBJECT_FIELD_NAME_BOOLEAN,
				false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_DATE,
				ObjectFieldConstants.DB_TYPE_DATE,
				RandomTestUtil.randomString(), OBJECT_FIELD_NAME_DATE, false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME,
				ObjectFieldConstants.DB_TYPE_DATE_TIME, true, true, null,
				RandomTestUtil.randomString(),
				OBJECT_FIELD_NAME_DATE_TIME_INPUT,
				Collections.singletonList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_TIME_STORAGE
					).value(
						ObjectFieldSettingConstants.VALUE_USE_INPUT_AS_ENTERED
					).build()),
				false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME,
				ObjectFieldConstants.DB_TYPE_DATE_TIME, true, true, null,
				RandomTestUtil.randomString(), OBJECT_FIELD_NAME_DATE_TIME_UTC,
				Collections.singletonList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_TIME_STORAGE
					).value(
						ObjectFieldSettingConstants.VALUE_CONVERT_TO_UTC
					).build()),
				false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_DECIMAL,
				ObjectFieldConstants.DB_TYPE_DOUBLE, true, false, null,
				RandomTestUtil.randomString(), OBJECT_FIELD_NAME_DECIMAL,
				false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_INTEGER,
				ObjectFieldConstants.DB_TYPE_INTEGER, true, true, null,
				RandomTestUtil.randomString(), OBJECT_FIELD_NAME_INTEGER,
				false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER,
				ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
				RandomTestUtil.randomString(), OBJECT_FIELD_NAME_LONG_INTEGER,
				false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_LONG_TEXT,
				ObjectFieldConstants.DB_TYPE_CLOB, false, false, null,
				RandomTestUtil.randomString(), OBJECT_FIELD_NAME_LONG_TEXT,
				false),
			ObjectFieldUtil.createObjectField(
				listTypeDefinition.getListTypeDefinitionId(),
				ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST, null,
				ObjectFieldConstants.DB_TYPE_STRING, true, false, null,
				RandomTestUtil.randomString(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, false, false),
			ObjectFieldUtil.createObjectField(
				listTypeDefinition.getListTypeDefinitionId(),
				ObjectFieldConstants.BUSINESS_TYPE_PICKLIST, null,
				ObjectFieldConstants.DB_TYPE_STRING, true, false, null,
				RandomTestUtil.randomString(), OBJECT_FIELD_NAME_PICKLIST,
				false, false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_PRECISION_DECIMAL,
				ObjectFieldConstants.DB_TYPE_BIG_DECIMAL, true, false, null,
				RandomTestUtil.randomString(),
				OBJECT_FIELD_NAME_PRECISION_DECIMAL, false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT,
				ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
				RandomTestUtil.randomString(), _OBJECT_FIELD_NAME_RICH_TEXT,
				false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_TEXT,
				ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
				RandomTestUtil.randomString(), OBJECT_FIELD_NAME_TEXT, false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_TEXT,
				ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
				RandomTestUtil.randomString(), OBJECT_FIELD_NAME_1, false));
	}

	private static final String _OBJECT_FIELD_NAME_RICH_TEXT =
		"x" + RandomTestUtil.randomString();

	private static BundleContext _bundleContext;
	private static List<ServiceRegistration<?>> _serviceRegistrations;

	@DeleteAfterTestRun
	private ListTypeEntry _listTypeEntry1;

	@DeleteAfterTestRun
	private ListTypeEntry _listTypeEntry2;

	@DeleteAfterTestRun
	private ListTypeEntry _listTypeEntry3;

	@Inject
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	private ObjectDefinition _objectDefinition4;
	private ObjectEntry _objectEntry5;
	private ObjectRelationship _objectRelationship6;
	private ObjectDefinition _siteScopedObjectDefinition2;

	@Inject
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

	@DeleteAfterTestRun
	private ObjectField _userSystemObjectField;

}