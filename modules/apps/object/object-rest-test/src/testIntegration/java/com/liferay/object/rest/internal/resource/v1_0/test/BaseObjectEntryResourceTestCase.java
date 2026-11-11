/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
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
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchCompanyException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
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
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
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
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;

import java.io.Serializable;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Luis Miguel Barcos
 * @author Magdalena Jedraszak
 * @author Daniel Raposo
 */
@RunWith(Arquillian.class)
public abstract class BaseObjectEntryResourceTestCase {

	@Before
	public void setUp() throws Exception {
		testGroupId = TestPropsValues.getGroupId();

		TaxonomyCategoryResource.Builder builder =
			TaxonomyCategoryResource.builder();

		taxonomyCategoryResource = builder.authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).locale(
			LocaleUtil.getDefault()
		).build();

		group = GroupTestUtil.addGroup();

		_listTypeDefinition =
			listTypeDefinitionLocalService.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				RandomTestUtil.randomLocaleStringMap(LocaleUtil.US), false,
				Collections.emptyList(), new ServiceContext());

		_listTypeEntry1 = _listTypeEntryLocalService.addListTypeEntry(
			null, TestPropsValues.getUserId(),
			_listTypeDefinition.getListTypeDefinitionId(),
			LIST_TYPE_ENTRY_KEY_1,
			RandomTestUtil.randomLocaleStringMap(LocaleUtil.US),
			_listTypeDefinition.isSystem());
		_listTypeEntry2 = _listTypeEntryLocalService.addListTypeEntry(
			null, TestPropsValues.getUserId(),
			_listTypeDefinition.getListTypeDefinitionId(),
			LIST_TYPE_ENTRY_KEY_2,
			RandomTestUtil.randomLocaleStringMap(LocaleUtil.US),
			_listTypeDefinition.isSystem());
		_listTypeEntry3 = _listTypeEntryLocalService.addListTypeEntry(
			null, TestPropsValues.getUserId(),
			_listTypeDefinition.getListTypeDefinitionId(),
			LIST_TYPE_ENTRY_KEY_3,
			RandomTestUtil.randomLocaleStringMap(LocaleUtil.US),
			_listTypeDefinition.isSystem());
	}

	@After
	public void tearDown() throws Exception {
		if (_objectDefinition1 != null) {
			objectDefinitionLocalService.deleteObjectDefinition(
				_objectDefinition1.getObjectDefinitionId());
		}

		if (_objectDefinition2 != null) {
			objectDefinitionLocalService.deleteObjectDefinition(
				_objectDefinition2.getObjectDefinitionId());
		}

		if (_objectDefinition3 != null) {
			objectDefinitionLocalService.deleteObjectDefinition(
				_objectDefinition3.getObjectDefinitionId());
		}

		if (_objectDefinition4 != null) {
			objectDefinitionLocalService.deleteObjectDefinition(
				_objectDefinition4.getObjectDefinitionId());
		}

		if (_siteScopedObjectDefinition1 != null) {
			objectDefinitionLocalService.deleteObjectDefinition(
				_siteScopedObjectDefinition1.getObjectDefinitionId());
		}

		if (_siteScopedObjectDefinition2 != null) {
			objectDefinitionLocalService.deleteObjectDefinition(
				_siteScopedObjectDefinition2.getObjectDefinitionId());
		}

		if (_assetVocabulary != null) {
			assetVocabularyLocalService.deleteVocabulary(_assetVocabulary);
		}

		listTypeDefinitionLocalService.deleteListTypeDefinition(
			_listTypeDefinition);
	}

	protected ObjectRelationship addObjectRelationshipAndRelateObjectEntries(
			ObjectDefinition objectDefinition,
			ObjectDefinition relatedObjectDefinition, long primaryKey1,
			long primaryKey2, String type)
		throws Exception {

		return addObjectRelationshipAndRelateObjectEntries(
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT, objectDefinition,
			relatedObjectDefinition, primaryKey1, primaryKey2, type);
	}

	protected ObjectRelationship addObjectRelationshipAndRelateObjectEntries(
			String deletionType, ObjectDefinition objectDefinition,
			ObjectDefinition relatedObjectDefinition, long primaryKey1,
			long primaryKey2, String type)
		throws Exception {

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				deletionType, objectDefinition, relatedObjectDefinition,
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

		user = userLocalService.updateUser(user);

		_users.add(user);

		return user;
	}

	protected void assertFilterString(
			String expectedObjectFieldName,
			Serializable expectedObjectFieldValue, String filterString,
			ObjectDefinition objectDefinition)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			objectDefinition.getRESTContextPath() + "?filter=" +
				URLCodec.encodeURL(filterString),
			Http.Method.GET);

		Assert.assertNull(jsonObject.get("status"));
		Assert.assertNull(jsonObject.get("title"));

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());

		JSONObject itemJSONObject = itemsJSONArray.getJSONObject(0);

		Assert.assertEquals(
			String.valueOf(expectedObjectFieldValue),
			String.valueOf(itemJSONObject.get(expectedObjectFieldName)));
	}

	protected void assertItem(
		int index, JSONObject jsonObject, String objectFieldName,
		Object objectFieldValue) {

		Assert.assertEquals(
			String.valueOf(objectFieldValue),
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
			UnsafeFunction<JSONObject, String, Exception>
				endpointUnsafeFunction,
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

		ObjectEntry parentObjectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			parentObjectDefinition, OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);
		ObjectEntry parentObjectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			parentObjectDefinition, OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);

		ObjectRelationship objectRelationship1 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				parentObjectDefinition, childObjectDefinition,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		String objectRelationship1ERCObjectFieldName =
			ObjectFieldSettingUtil.getValue(
				ObjectFieldSettingConstants.
					NAME_OBJECT_RELATIONSHIP_ERC_OBJECT_FIELD_NAME,
				objectFieldLocalService.getObjectField(
					objectRelationship1.getObjectFieldId2()));

		ObjectRelationship objectRelationship2 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				parentObjectDefinition, childObjectDefinition,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		ObjectField objectRelationship2ObjectField =
			objectFieldLocalService.getObjectField(
				objectRelationship2.getObjectFieldId2());

		BigDecimal randomBigDecimal = BigDecimal.valueOf(
			RandomTestUtil.randomDouble()
		).setScale(
			5, RoundingMode.HALF_UP
		);
		boolean randomBoolean = RandomTestUtil.randomBoolean();
		Date randomDate = RandomTestUtil.nextDate();
		Date randomDateTimeInput = RandomTestUtil.nextDate();
		Date randomDateTimeUTC = RandomTestUtil.nextDate();
		String randomExternalReferenceCode = RandomTestUtil.randomString();
		float randomFloat = RandomTestUtil.randomFloat();
		int randomInt = RandomTestUtil.randomInt();
		long randomLong = RandomTestUtil.randomLong(
			ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MIN,
			ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MAX);
		String randomLongText = RandomTestUtil.randomString();
		String randomRichText = RandomTestUtil.randomString();
		String randomText = RandomTestUtil.randomString();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			_createObjectEntryJSONObject(
				randomBigDecimal, randomBoolean, randomDate,
				randomDateTimeInput, randomDateTimeUTC, randomFloat, randomInt,
				randomLong, randomLongText, randomRichText, randomText
			).put(
				objectRelationship1ERCObjectFieldName,
				parentObjectEntry1.getExternalReferenceCode()
			).put(
				objectRelationship2ObjectField.getName(),
				parentObjectEntry1.getObjectEntryId()
			).put(
				"externalReferenceCode", randomExternalReferenceCode
			).put(
				"status", JSONUtil.put("code", WorkflowConstants.STATUS_DRAFT)
			).toString(),
			getEndpoint(childObjectDefinition, scopeKey), Http.Method.POST);

		JSONObject expectedJSONObject = _createExpectedObjectEntryJSONObject(
			randomBigDecimal, randomBoolean, randomDate, randomDateTimeInput,
			randomDateTimeUTC, randomFloat, randomInt, randomLong,
			randomLongText, randomRichText, randomText
		).put(
			objectRelationship1ERCObjectFieldName,
			parentObjectEntry1.getExternalReferenceCode()
		).put(
			objectRelationship2ObjectField.getName(),
			(int)parentObjectEntry1.getObjectEntryId()
		).put(
			"externalReferenceCode", randomExternalReferenceCode
		).put(
			"id", (Object)jsonObject.getLong("id")
		).put(
			"status", JSONUtil.put("code", WorkflowConstants.STATUS_DRAFT)
		);

		String endpoint = endpointUnsafeFunction.apply(jsonObject);

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
				null, endpoint, Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);

		// Boolean field

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, OBJECT_FIELD_NAME_BOOLEAN, !randomBoolean);

		_assertPatchObjectField(
			endpoint, expectedJSONObject, OBJECT_FIELD_NAME_BOOLEAN,
			!randomBoolean);

		// Date field

		Date date = new Date(randomDate.getTime() + Time.DAY);

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, OBJECT_FIELD_NAME_DATE,
			dateFormat.format(date) + "T00:00:00.000Z");

		_assertPatchObjectField(
			endpoint, expectedJSONObject, OBJECT_FIELD_NAME_DATE,
			dateFormat.format(date));

		// Date time field ('Input as value' type)

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, OBJECT_FIELD_NAME_DATE_TIME_INPUT,
			StringUtil.removeLast(dateTimeFormat.format(date), "Z"));

		_assertPatchObjectField(
			endpoint, expectedJSONObject, OBJECT_FIELD_NAME_DATE_TIME_INPUT,
			dateTimeFormat.format(date));

		// Date time field ('UTC' type)

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, OBJECT_FIELD_NAME_DATE_TIME_UTC,
			dateTimeFormat.format(date));

		_assertPatchObjectField(
			endpoint, expectedJSONObject, OBJECT_FIELD_NAME_DATE_TIME_UTC,
			dateTimeFormat.format(date));

		// Decimal field

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, OBJECT_FIELD_NAME_DECIMAL, randomFloat + 1);

		_assertPatchObjectField(
			endpoint, expectedJSONObject, OBJECT_FIELD_NAME_DECIMAL,
			randomFloat + 1);

		// Integer field

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, OBJECT_FIELD_NAME_INTEGER, randomInt + 1);

		_assertPatchObjectField(
			endpoint, expectedJSONObject, OBJECT_FIELD_NAME_INTEGER,
			randomInt + 1);

		// Long integer field

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, OBJECT_FIELD_NAME_LONG_INTEGER, randomLong + 1);

		_assertPatchObjectField(
			endpoint, expectedJSONObject, OBJECT_FIELD_NAME_LONG_INTEGER,
			randomLong + 1);

		// Long text field

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, OBJECT_FIELD_NAME_LONG_TEXT,
			"b" + randomLongText);

		_assertPatchObjectField(
			endpoint, expectedJSONObject, OBJECT_FIELD_NAME_LONG_TEXT,
			"b" + randomLongText);

		// Multiselect picklist field

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
			JSONUtil.putAll(
				_toListTypeEntryJSONObject(_listTypeEntry2),
				_toListTypeEntryJSONObject(_listTypeEntry3)));

		_assertPatchObjectField(
			endpoint, expectedJSONObject,
			OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
			JSONUtil.putAll(LIST_TYPE_ENTRY_KEY_2, LIST_TYPE_ENTRY_KEY_3));

		// Picklist field

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, OBJECT_FIELD_NAME_PICKLIST,
			_toListTypeEntryJSONObject(_listTypeEntry1));

		_assertPatchObjectField(
			endpoint, expectedJSONObject, OBJECT_FIELD_NAME_PICKLIST,
			LIST_TYPE_ENTRY_KEY_1);

		// Precision decimal field

		BigDecimal bigDecimal = randomBigDecimal.add(BigDecimal.ONE);

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, OBJECT_FIELD_NAME_PRECISION_DECIMAL,
			bigDecimal);

		_assertPatchObjectField(
			endpoint, expectedJSONObject, OBJECT_FIELD_NAME_PRECISION_DECIMAL,
			bigDecimal);

		// Relationship external reference code field

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, objectRelationship1ERCObjectFieldName,
			parentObjectEntry2.getExternalReferenceCode());

		_assertPatchObjectField(
			endpoint, expectedJSONObject, objectRelationship1ERCObjectFieldName,
			parentObjectEntry2.getExternalReferenceCode());

		// Relationship ID field

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, objectRelationship2ObjectField.getName(),
			parentObjectEntry2.getObjectEntryId());

		_assertPatchObjectField(
			endpoint, expectedJSONObject,
			objectRelationship2ObjectField.getName(),
			parentObjectEntry2.getObjectEntryId());

		// Rich text field

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, _OBJECT_FIELD_NAME_RICH_TEXT,
			"b" + randomRichText);

		_assertPatchObjectField(
			endpoint, expectedJSONObject, _OBJECT_FIELD_NAME_RICH_TEXT,
			"b" + randomRichText);

		// Text field

		expectedJSONObject = _cloneJSONObject(
			expectedJSONObject, OBJECT_FIELD_NAME_TEXT, "b" + randomText);

		_assertPatchObjectField(
			endpoint, expectedJSONObject, OBJECT_FIELD_NAME_TEXT,
			"b" + randomText);
	}

	protected void assertPutCustomObjectEntry(
			UnsafeFunction<JSONObject, String, Exception>
				endpointUnsafeFunction,
			ObjectDefinition childObjectDefinition,
			ObjectDefinition parentObjectDefinition, Object scopeKey)
		throws Exception {

		ObjectEntry parentObjectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			parentObjectDefinition, OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);
		ObjectEntry parentObjectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			parentObjectDefinition, OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);

		ObjectRelationship objectRelationship1 =
			ObjectRelationshipTestUtil.addObjectRelationship(
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

		ObjectRelationship objectRelationship2 =
			ObjectRelationshipTestUtil.addObjectRelationship(
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
		Date randomDate = RandomTestUtil.nextDate();
		Date randomDateTimeInput = RandomTestUtil.nextDate();
		Date randomDateTimeUTC = RandomTestUtil.nextDate();
		String randomExternalReferenceCode = RandomTestUtil.randomString();
		float randomFloat = RandomTestUtil.randomFloat();
		int randomInt = RandomTestUtil.randomInt();
		long randomLong = RandomTestUtil.randomLong(
			ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MIN,
			ObjectFieldValidationConstants.BUSINESS_TYPE_LONG_VALUE_MAX);
		String randomLongText = RandomTestUtil.randomString();
		String randomRichText = RandomTestUtil.randomString();
		String randomText = RandomTestUtil.randomString();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			_createObjectEntryJSONObject(
				randomBigDecimal, randomBoolean, randomDate,
				randomDateTimeInput, randomDateTimeUTC, randomFloat, randomInt,
				randomLong, randomLongText, randomRichText, randomText
			).put(
				objectRelationship1ERCObjectFieldName,
				parentObjectEntry1.getExternalReferenceCode()
			).put(
				objectRelationship1ObjectField.getName(),
				parentObjectEntry2.getObjectEntryId()
			).put(
				objectRelationship2ERCObjectFieldName,
				parentObjectEntry2.getExternalReferenceCode()
			).put(
				objectRelationship2ObjectField.getName(),
				parentObjectEntry1.getObjectEntryId()
			).put(
				"externalReferenceCode", randomExternalReferenceCode
			).toString(),
			getEndpoint(childObjectDefinition, scopeKey), Http.Method.POST);

		String endpoint = endpointUnsafeFunction.apply(jsonObject);

		HTTPTestUtil.invokeToJSONObject(
			JSONFactoryUtil.getNullJSON(), endpoint, Http.Method.PUT);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, endpoint, Http.Method.GET);

		JSONAssert.assertEquals(
			JSONUtil.put(
				_OBJECT_FIELD_NAME_RICH_TEXT, ""
			).put(
				OBJECT_FIELD_NAME_BOOLEAN, false
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
				parentObjectEntry2.getExternalReferenceCode()
			).put(
				objectRelationship1ObjectField.getName(),
				(int)parentObjectEntry2.getObjectEntryId()
			).put(
				objectRelationship2ERCObjectFieldName,
				parentObjectEntry1.getExternalReferenceCode()
			).put(
				objectRelationship2ObjectField.getName(),
				(int)parentObjectEntry1.getObjectEntryId()
			).put(
				"externalReferenceCode", randomExternalReferenceCode
			).toString(),
			jsonObject.toString(), JSONCompareMode.LENIENT);

		Assert.assertFalse(jsonObject.has(OBJECT_FIELD_NAME_DATE));
		Assert.assertFalse(jsonObject.has(OBJECT_FIELD_NAME_DATE_TIME_INPUT));
		Assert.assertFalse(jsonObject.has(OBJECT_FIELD_NAME_DATE_TIME_UTC));

		JSONAssert.assertEquals(
			_createExpectedObjectEntryJSONObject(
				randomBigDecimal, randomBoolean, randomDate,
				randomDateTimeInput, randomDateTimeUTC, randomFloat, randomInt,
				randomLong, randomLongText, randomRichText, randomText
			).put(
				objectRelationship1ERCObjectFieldName,
				parentObjectEntry1.getExternalReferenceCode()
			).put(
				objectRelationship1ObjectField.getName(),
				(int)parentObjectEntry1.getObjectEntryId()
			).put(
				objectRelationship2ERCObjectFieldName,
				parentObjectEntry2.getExternalReferenceCode()
			).put(
				objectRelationship2ObjectField.getName(),
				(int)parentObjectEntry2.getObjectEntryId()
			).put(
				"externalReferenceCode", randomExternalReferenceCode
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				_createObjectEntryJSONObject(
					randomBigDecimal, randomBoolean, randomDate,
					randomDateTimeInput, randomDateTimeUTC, randomFloat,
					randomInt, randomLong, randomLongText, randomRichText,
					randomText
				).put(
					objectRelationship1ERCObjectFieldName,
					parentObjectEntry2.getExternalReferenceCode()
				).put(
					objectRelationship1ObjectField.getName(),
					parentObjectEntry1.getObjectEntryId()
				).put(
					objectRelationship2ERCObjectFieldName,
					parentObjectEntry1.getExternalReferenceCode()
				).put(
					objectRelationship2ObjectField.getName(),
					parentObjectEntry2.getObjectEntryId()
				).toString(),
				endpoint, Http.Method.PUT
			).toString(),
			JSONCompareMode.LENIENT);
	}

	protected JSONArray createObjectEntriesJSONArray(
		String[] externalReferenceCodes, String objectFieldName,
		String[] objectFieldValues) {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (int i = 0; i < objectFieldValues.length; i++) {
			jsonArray.put(
				JSONUtil.put(
					objectFieldName, objectFieldValues[i]
				).put(
					"externalReferenceCode", externalReferenceCodes[i]
				));
		}

		return jsonArray;
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

	protected ObjectEntry getLocalizedObjectEntry() throws Exception {
		getObjectDefinition4();

		return _localizedObjectEntry;
	}

	protected String getNestedFieldsEndpoint(
		ObjectDefinition objectDefinition, long objectEntryId,
		ObjectRelationship objectRelationship) {

		return StringBundler.concat(
			objectDefinition.getRESTContextPath(), StringPool.SLASH,
			objectEntryId, "?nestedFields=", objectRelationship.getName());
	}

	protected JSONObject getNestedFieldsObjectEntryJSONObject(
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

	protected ObjectDefinition getObjectDefinition1() throws Exception {
		if (_objectDefinition1 != null) {
			return _objectDefinition1;
		}

		String randomString = RandomTestUtil.randomString(
			UniqueStringRandomizerBumper.INSTANCE);

		String objectDefinitionName = "A" + randomString;

		_objectDefinition1 = ObjectDefinitionTestUtil.publishObjectDefinition(
			objectDefinitionName,
			_createObjectDefinition1ObjectFields(objectDefinitionName),
			ObjectDefinitionConstants.SCOPE_COMPANY);

		return _objectDefinition1;
	}

	protected ObjectDefinition getObjectDefinition2() throws Exception {
		if (_objectDefinition2 != null) {
			return _objectDefinition2;
		}

		_objectDefinition2 = ObjectDefinitionTestUtil.publishObjectDefinition(
			ListUtil.concat(
				Collections.singletonList(
					_createAttachmentObjectField(
						"pdf", ObjectFieldSettingConstants.VALUE_DOCS_AND_MEDIA,
						OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE)),
				_createBusinessTypeObjectFields(),
				Arrays.asList(
					_createTextObjectField(OBJECT_FIELD_NAME_TEXT),
					_createTextObjectField(OBJECT_FIELD_NAME_2))),
			ObjectDefinitionConstants.SCOPE_COMPANY);

		return _objectDefinition2;
	}

	protected ObjectDefinition getObjectDefinition3() throws Exception {
		if (_objectDefinition3 != null) {
			return _objectDefinition3;
		}

		_objectDefinition3 = ObjectDefinitionTestUtil.publishObjectDefinition(
			ListUtil.concat(
				_createBusinessTypeObjectFields(),
				Arrays.asList(
					_createTextObjectField(OBJECT_FIELD_NAME_TEXT),
					_createTextObjectField(OBJECT_FIELD_NAME_3))),
			ObjectDefinitionConstants.SCOPE_COMPANY);

		return _objectDefinition3;
	}

	protected ObjectDefinition getObjectDefinition4() throws Exception {
		if (_objectDefinition4 != null) {
			return _objectDefinition4;
		}

		_objectDefinition4 = ObjectDefinitionTestUtil.publishObjectDefinition(
			Arrays.asList(
				new LongTextObjectFieldBuilder(
				).indexed(
					true
				).labelMap(
					RandomTestUtil.randomLocaleStringMap()
				).localized(
					true
				).name(
					OBJECT_FIELD_NAME_LOCALIZED_LONG_TEXT
				).build(),
				new RichTextObjectFieldBuilder(
				).indexed(
					true
				).labelMap(
					RandomTestUtil.randomLocaleStringMap()
				).localized(
					true
				).name(
					OBJECT_FIELD_NAME_LOCALIZED_RICH_TEXT
				).build(),
				new TextObjectFieldBuilder(
				).indexed(
					true
				).labelMap(
					RandomTestUtil.randomLocaleStringMap()
				).localized(
					true
				).name(
					"name"
				).build(),
				new TextObjectFieldBuilder(
				).indexed(
					true
				).labelMap(
					RandomTestUtil.randomLocaleStringMap()
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
				_createTextObjectField(OBJECT_FIELD_NAME_TEXT)),
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_localizedObjectEntry = ObjectEntryTestUtil.addObjectEntry(
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

		if (_siteScopedObjectDefinition1 != null) {
			return _siteScopedObjectDefinition1;
		}

		String randomString = RandomTestUtil.randomString(
			UniqueStringRandomizerBumper.INSTANCE);

		String objectDefinitionName = "A" + randomString;

		_siteScopedObjectDefinition1 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				objectDefinitionName,
				_createObjectDefinition1ObjectFields(objectDefinitionName),
				ObjectDefinitionConstants.SCOPE_SITE);

		return _siteScopedObjectDefinition1;
	}

	protected ObjectDefinition getSiteScopedObjectDefinition2()
		throws Exception {

		if (_siteScopedObjectDefinition2 != null) {
			return _siteScopedObjectDefinition2;
		}

		_siteScopedObjectDefinition2 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Arrays.asList(_createTextObjectField(OBJECT_FIELD_NAME_2)),
				ObjectDefinitionConstants.SCOPE_SITE);

		return _siteScopedObjectDefinition2;
	}

	protected ObjectDefinition getUserSystemObjectDefinition()
		throws Exception {

		if (_userSystemObjectDefinition != null) {
			return _userSystemObjectDefinition;
		}

		_userSystemObjectDefinitionManager =
			_systemObjectDefinitionManagerRegistry.
				getSystemObjectDefinitionManager("User");

		_userSystemObjectDefinition =
			objectDefinitionLocalService.fetchSystemObjectDefinition(
				TestPropsValues.getCompanyId(),
				_userSystemObjectDefinitionManager.getName());

		_userSystemObjectField = ObjectFieldTestUtil.addCustomObjectField(
			TestPropsValues.getUserId(),
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, _userSystemObjectDefinition,
			OBJECT_FIELD_NAME_2);

		return _userSystemObjectDefinition;
	}

	protected SystemObjectDefinitionManager
			getUserSystemObjectDefinitionManager()
		throws Exception {

		getUserSystemObjectDefinition();

		return _userSystemObjectDefinitionManager;
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

		AssetVocabulary assetVocabulary = _getAssetVocabulary();

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

	protected ObjectDefinition publishDepotScopedObjectDefinition()
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

		_objectDefinitionSettingLocalService.addObjectDefinitionSetting(
			objectDefinition.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS,
			StringPool.TRUE);

		return objectDefinition;
	}

	protected ObjectDefinition publishLocalizedObjectDefinition(
			String objectFieldName)
		throws Exception {

		return ObjectDefinitionTestUtil.publishObjectDefinition(
			Collections.singletonList(
				new TextObjectFieldBuilder(
				).labelMap(
					RandomTestUtil.randomLocaleStringMap()
				).localized(
					true
				).name(
					objectFieldName
				).build()),
			ObjectDefinitionConstants.SCOPE_COMPANY);
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
					RandomTestUtil.randomString(
						UniqueStringRandomizerBumper.INSTANCE));
				id = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(
					RandomTestUtil.randomString(
						UniqueStringRandomizerBumper.INSTANCE));
				numberOfTaxonomyCategories = RandomTestUtil.randomInt();
				siteId = groupId;
				taxonomyCategoryUsageCount = RandomTestUtil.randomInt();

				setTaxonomyVocabularyId(parentTaxonomyVocabularyId);
			}
		};
	}

	protected void setObjectDefinition(
		ObjectDefinition objectDefinition,
		ObjectEntryResource objectEntryResource) {

		ReflectionTestUtil.invoke(
			objectEntryResource, "setObjectDefinition",
			new Class<?>[] {ObjectDefinition.class}, objectDefinition);
	}

	protected static final String ERC_VALUE_1 =
		"a" +
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE);

	protected static final String ERC_VALUE_2 =
		"b" +
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE);

	protected static final String ERC_VALUE_3 =
		"c" +
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE);

	protected static final String LIST_TYPE_ENTRY_KEY_1 =
		"a" +
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE);

	protected static final String LIST_TYPE_ENTRY_KEY_2 =
		"b" +
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE);

	protected static final String LIST_TYPE_ENTRY_KEY_3 =
		"c" +
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE);

	protected static final int MAX_FILE_SIZE_VALUE = 1;

	protected static final String NEW_OBJECT_FIELD_VALUE_1 =
		RandomTestUtil.randomString();

	protected static final String NEW_OBJECT_FIELD_VALUE_2 =
		RandomTestUtil.randomString();

	protected static final String OBJECT_FIELD_NAME_1 =
		"x" +
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE);

	protected static final String OBJECT_FIELD_NAME_2 =
		"x" +
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE);

	protected static final String OBJECT_FIELD_NAME_3 =
		"x" +
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE);

	protected static final String
		OBJECT_FIELD_NAME_ATTACHMENT_CMS_BASIC_DOCUMENT_SOURCE =
			"x" +
				RandomTestUtil.randomString(
					UniqueStringRandomizerBumper.INSTANCE);

	protected static final String
		OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE =
			"x" +
				RandomTestUtil.randomString(
					UniqueStringRandomizerBumper.INSTANCE);

	protected static final String
		OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT_SOURCE =
			"x" +
				RandomTestUtil.randomString(
					UniqueStringRandomizerBumper.INSTANCE);

	protected static final String
		OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1 =
			"x" +
				RandomTestUtil.randomString(
					UniqueStringRandomizerBumper.INSTANCE);

	protected static final String
		OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_2 =
			"x" +
				RandomTestUtil.randomString(
					UniqueStringRandomizerBumper.INSTANCE);

	protected static final String OBJECT_FIELD_NAME_BOOLEAN =
		"x" +
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE);

	protected static final String OBJECT_FIELD_NAME_DATE =
		"x" +
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE);

	protected static final String OBJECT_FIELD_NAME_DATE_TIME_INPUT =
		"x" +
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE);

	protected static final String OBJECT_FIELD_NAME_DATE_TIME_UTC =
		"x" +
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE);

	protected static final String OBJECT_FIELD_NAME_DECIMAL =
		"x" +
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE);

	protected static final String OBJECT_FIELD_NAME_INTEGER =
		"x" +
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE);

	protected static final String OBJECT_FIELD_NAME_LOCALIZED_LONG_TEXT =
		"x" +
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE);

	protected static final String OBJECT_FIELD_NAME_LOCALIZED_RICH_TEXT =
		"x" +
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE);

	protected static final String OBJECT_FIELD_NAME_LOCALIZED_TEXT =
		"x" +
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE);

	protected static final String OBJECT_FIELD_NAME_LONG_INTEGER =
		"x" +
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE);

	protected static final String OBJECT_FIELD_NAME_LONG_TEXT =
		"x" +
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE);

	protected static final String OBJECT_FIELD_NAME_MULTISELECT_PICKLIST =
		"x" +
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE);

	protected static final String OBJECT_FIELD_NAME_PICKLIST =
		"x" +
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE);

	protected static final String OBJECT_FIELD_NAME_PRECISION_DECIMAL =
		"x" +
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE);

	protected static final String OBJECT_FIELD_NAME_TEXT =
		"x" +
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE);

	protected static final int OBJECT_FIELD_VALUE_1 =
		RandomTestUtil.randomInt();

	protected static final int OBJECT_FIELD_VALUE_2 =
		RandomTestUtil.randomInt();

	protected static final int OBJECT_FIELD_VALUE_3 =
		RandomTestUtil.randomInt();

	protected static final int OBJECT_FIELD_VALUE_4 =
		RandomTestUtil.randomInt();

	protected static final String TAG_1 = StringUtil.toLowerCase(
		RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE));

	@Inject
	protected AssetVocabularyLocalService assetVocabularyLocalService;

	@Inject
	protected ClassNameLocalService classNameLocalService;

	@Inject
	protected CompanyLocalService companyLocalService;

	protected final Format dateFormat =
		FastDateFormatFactoryUtil.getSimpleDateFormat("yyyy-MM-dd");
	protected final Format dateTimeFormat =
		FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	@Inject
	protected DepotEntryLocalService depotEntryLocalService;

	@DeleteAfterTestRun
	protected Group group;

	@Inject
	protected GroupLocalService groupLocalService;

	@Inject
	protected ListTypeDefinitionLocalService listTypeDefinitionLocalService;

	@Inject
	protected ObjectDefinitionLocalService objectDefinitionLocalService;

	@Inject
	protected ObjectEntryLocalService objectEntryLocalService;

	@Inject
	protected ObjectFieldBusinessTypeRegistry objectFieldBusinessTypeRegistry;

	@Inject
	protected ObjectFieldLocalService objectFieldLocalService;

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

	protected TaxonomyCategoryResource taxonomyCategoryResource;
	protected long testGroupId;

	@Inject
	protected UserGroupRoleLocalService userGroupRoleLocalService;

	@Inject
	protected UserLocalService userLocalService;

	private void _assertPatchObjectField(
			String endpoint, JSONObject expectedJSONObject,
			String objectFieldName, Object objectFieldValue)
		throws Exception {

		JSONAssert.assertEquals(
			expectedJSONObject.toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					objectFieldName, objectFieldValue
				).put(
					"externalReferenceCode",
					expectedJSONObject.get("externalReferenceCode")
				).toString(),
				endpoint, Http.Method.PATCH
			).toString(),
			JSONCompareMode.LENIENT);
	}

	private JSONObject _cloneJSONObject(
			JSONObject jsonObject, String objectFieldName,
			Object objectFieldValue)
		throws Exception {

		JSONObject clonedJSONObject = JSONFactoryUtil.createJSONObject(
			jsonObject.toString());

		return clonedJSONObject.put(objectFieldName, objectFieldValue);
	}

	private ObjectField _createAttachmentObjectField(
		String acceptedFileExtensions, String fileSource,
		String objectFieldName) {

		return ObjectFieldUtil.createObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
			ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
			objectFieldName, objectFieldName,
			Arrays.asList(
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_ACCEPTED_FILE_EXTENSIONS
				).value(
					acceptedFileExtensions
				).build(),
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_FILE_SOURCE
				).value(
					fileSource
				).build(),
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
				).value(
					String.valueOf(MAX_FILE_SIZE_VALUE)
				).build()),
			false);
	}

	private List<ObjectField> _createBusinessTypeObjectFields() {
		return Arrays.asList(
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
				_listTypeDefinition.getListTypeDefinitionId(),
				ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST, null,
				ObjectFieldConstants.DB_TYPE_STRING, true, false, null,
				RandomTestUtil.randomString(),
				OBJECT_FIELD_NAME_MULTISELECT_PICKLIST, false, false),
			ObjectFieldUtil.createObjectField(
				_listTypeDefinition.getListTypeDefinitionId(),
				ObjectFieldConstants.BUSINESS_TYPE_PICKLIST, null,
				ObjectFieldConstants.DB_TYPE_STRING, true, false, null,
				RandomTestUtil.randomString(), OBJECT_FIELD_NAME_PICKLIST,
				false, false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_PRECISION_DECIMAL,
				ObjectFieldConstants.DB_TYPE_BIG_DECIMAL, true, false, null,
				RandomTestUtil.randomString(),
				OBJECT_FIELD_NAME_PRECISION_DECIMAL, false));
	}

	private JSONObject _createExpectedObjectEntryJSONObject(
		BigDecimal randomBigDecimal, boolean randomBoolean, Date randomDate,
		Date randomDateTimeInput, Date randomDateTimeUTC, float randomFloat,
		int randomInt, long randomLong, String randomLongText,
		String randomRichText, String randomText) {

		return JSONUtil.put(
			_OBJECT_FIELD_NAME_RICH_TEXT, randomRichText
		).put(
			OBJECT_FIELD_NAME_BOOLEAN, randomBoolean
		).put(
			OBJECT_FIELD_NAME_DATE,
			dateFormat.format(randomDate) + "T00:00:00.000Z"
		).put(
			OBJECT_FIELD_NAME_DATE_TIME_INPUT,
			StringUtil.removeLast(
				dateTimeFormat.format(randomDateTimeInput), "Z")
		).put(
			OBJECT_FIELD_NAME_DATE_TIME_UTC,
			dateTimeFormat.format(randomDateTimeUTC)
		).put(
			OBJECT_FIELD_NAME_DECIMAL, randomFloat
		).put(
			OBJECT_FIELD_NAME_INTEGER, randomInt
		).put(
			OBJECT_FIELD_NAME_LONG_INTEGER, (double)randomLong
		).put(
			OBJECT_FIELD_NAME_LONG_TEXT, randomLongText
		).put(
			OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
			JSONUtil.putAll(
				_toListTypeEntryJSONObject(_listTypeEntry1),
				_toListTypeEntryJSONObject(_listTypeEntry2))
		).put(
			OBJECT_FIELD_NAME_PICKLIST,
			_toListTypeEntryJSONObject(_listTypeEntry3)
		).put(
			OBJECT_FIELD_NAME_PRECISION_DECIMAL, randomBigDecimal
		).put(
			OBJECT_FIELD_NAME_TEXT, randomText
		);
	}

	private List<ObjectField> _createObjectDefinition1ObjectFields(
		String objectDefinitionName) {

		return ListUtil.concat(
			Arrays.asList(
				_createAttachmentObjectField(
					"jpg, txt",
					ObjectFieldSettingConstants.VALUE_CMS_BASIC_DOCUMENT,
					OBJECT_FIELD_NAME_ATTACHMENT_CMS_BASIC_DOCUMENT_SOURCE),
				_createAttachmentObjectField(
					"jpg, txt",
					ObjectFieldSettingConstants.VALUE_DOCS_AND_MEDIA,
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE),
				_createAttachmentObjectField(
					"jpg, txt",
					ObjectFieldSettingConstants.
						VALUE_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT_SOURCE),
				_createAttachmentObjectField(
					"jpg, txt",
					ObjectFieldSettingConstants.
						VALUE_USER_COMPUTER_TO_DOCS_AND_MEDIA,
					OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER_TO_DOCS_AND_MEDIA_SOURCE_1),
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
							ObjectFieldSettingConstants.
								NAME_SHOW_FILES_IN_LIBRARY
						).value(
							Boolean.TRUE.toString()
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.
								NAME_STORAGE_DL_FOLDER_PATH
						).value(
							StringPool.SLASH + objectDefinitionName
						).build()),
					false)),
			_createBusinessTypeObjectFields(),
			Arrays.asList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
					RandomTestUtil.randomString(), _OBJECT_FIELD_NAME_RICH_TEXT,
					false),
				_createTextObjectField(OBJECT_FIELD_NAME_TEXT),
				_createTextObjectField(OBJECT_FIELD_NAME_1)));
	}

	private JSONObject _createObjectEntryJSONObject(
		BigDecimal randomBigDecimal, boolean randomBoolean, Date randomDate,
		Date randomDateTimeInput, Date randomDateTimeUTC, float randomFloat,
		int randomInt, long randomLong, String randomLongText,
		String randomRichText, String randomText) {

		return JSONUtil.put(
			_OBJECT_FIELD_NAME_RICH_TEXT, randomRichText
		).put(
			OBJECT_FIELD_NAME_BOOLEAN, randomBoolean
		).put(
			OBJECT_FIELD_NAME_DATE, dateFormat.format(randomDate)
		).put(
			OBJECT_FIELD_NAME_DATE_TIME_INPUT,
			dateTimeFormat.format(randomDateTimeInput)
		).put(
			OBJECT_FIELD_NAME_DATE_TIME_UTC,
			dateTimeFormat.format(randomDateTimeUTC)
		).put(
			OBJECT_FIELD_NAME_DECIMAL, randomFloat
		).put(
			OBJECT_FIELD_NAME_INTEGER, randomInt
		).put(
			OBJECT_FIELD_NAME_LONG_INTEGER, randomLong
		).put(
			OBJECT_FIELD_NAME_LONG_TEXT, randomLongText
		).put(
			OBJECT_FIELD_NAME_MULTISELECT_PICKLIST,
			JSONUtil.putAll(LIST_TYPE_ENTRY_KEY_1, LIST_TYPE_ENTRY_KEY_2)
		).put(
			OBJECT_FIELD_NAME_PICKLIST, LIST_TYPE_ENTRY_KEY_3
		).put(
			OBJECT_FIELD_NAME_PRECISION_DECIMAL, randomBigDecimal
		).put(
			OBJECT_FIELD_NAME_TEXT, randomText
		);
	}

	private ObjectField _createTextObjectField(String objectFieldName) {
		return ObjectFieldUtil.createObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
			RandomTestUtil.randomString(), objectFieldName, false);
	}

	private AssetVocabulary _getAssetVocabulary() throws Exception {
		if (_assetVocabulary != null) {
			return _assetVocabulary;
		}

		_assetVocabulary = assetVocabularyLocalService.addVocabulary(
			userLocalService.getGuestUserId(TestPropsValues.getCompanyId()),
			testGroupId,
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE),
			new ServiceContext());

		return _assetVocabulary;
	}

	private JSONObject _toListTypeEntryJSONObject(ListTypeEntry listTypeEntry) {
		return JSONUtil.put(
			"key", listTypeEntry.getKey()
		).put(
			"name", listTypeEntry.getName(LocaleUtil.getDefault())
		);
	}

	private static final String _OBJECT_FIELD_NAME_RICH_TEXT =
		"x" +
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE);

	private AssetVocabulary _assetVocabulary;
	private ListTypeDefinition _listTypeDefinition;
	private ListTypeEntry _listTypeEntry1;
	private ListTypeEntry _listTypeEntry2;
	private ListTypeEntry _listTypeEntry3;

	@Inject
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	private ObjectEntry _localizedObjectEntry;
	private ObjectDefinition _objectDefinition1;
	private ObjectDefinition _objectDefinition2;
	private ObjectDefinition _objectDefinition3;
	private ObjectDefinition _objectDefinition4;

	@Inject
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	private ObjectDefinition _siteScopedObjectDefinition1;
	private ObjectDefinition _siteScopedObjectDefinition2;

	@Inject
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

	private ObjectDefinition _userSystemObjectDefinition;
	private SystemObjectDefinitionManager _userSystemObjectDefinitionManager;

	@DeleteAfterTestRun
	private ObjectField _userSystemObjectField;

	@DeleteAfterTestRun
	private final List<User> _users = new ArrayList<>();

}