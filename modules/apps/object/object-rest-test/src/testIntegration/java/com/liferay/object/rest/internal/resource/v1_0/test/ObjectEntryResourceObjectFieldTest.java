/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.map.geocoder.GeocoderResult;
import com.liferay.map.test.util.GeocoderTestUtil;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectValidationRuleConstants;
import com.liferay.object.field.builder.AssigneeObjectFieldBuilder;
import com.liferay.object.field.builder.LocationObjectFieldBuilder;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.GuestOrUserUtil;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Luis Miguel Barcos
 */
@FeatureFlags(featureFlags = @FeatureFlag(value = "LPS-164801"))
@RunWith(Arquillian.class)
public class ObjectEntryResourceObjectFieldTest
	extends BaseObjectEntryResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetObjectEntriesPageWithDeletedObjectField()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						OBJECT_FIELD_NAME_1, OBJECT_FIELD_NAME_1)));

		ObjectEntryTestUtil.addObjectEntry(
			objectDefinition, "able", RandomTestUtil.randomString());

		HTTPTestUtil.invokeToJSONObject(
			null, objectDefinition.getRESTContextPath(), Http.Method.GET);

		objectFieldLocalService.deleteObjectField(
			objectFieldLocalService.getObjectField(
				objectDefinition.getObjectDefinitionId(), OBJECT_FIELD_NAME_1));

		ObjectEntryTestUtil.addObjectEntry(
			objectDefinition, "able", RandomTestUtil.randomString());

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, objectDefinition.getRESTContextPath(), Http.Method.GET);

		Assert.assertEquals(
			jsonObject.toString(), 2, jsonObject.getInt("totalCount"));

		objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Test
	public void testGetObjectEntriesWithAssigneeObjectField() throws Exception {
		ObjectFieldUtil.addCustomObjectField(
			new AssigneeObjectFieldBuilder(
			).labelMap(
				RandomTestUtil.randomLocaleStringMap()
			).name(
				"assignee"
			).objectDefinitionId(
				getObjectDefinition1().getObjectDefinitionId()
			).userId(
				TestPropsValues.getUserId()
			).build());

		User user1 = UserTestUtil.addUser();

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"assignee",
				HashMapBuilder.put(
					"externalReferenceCode",
					() -> {
						Role role = roleLocalService.getRole(
							TestPropsValues.getCompanyId(),
							AccountRoleConstants.
								REQUIRED_ROLE_NAME_ACCOUNT_MANAGER);

						Organization organization =
							OrganizationTestUtil.addOrganization();

						_organizationLocalService.addUserOrganization(
							user1.getUserId(),
							organization.getOrganizationId());

						Group group = groupLocalService.getOrganizationGroup(
							TestPropsValues.getCompanyId(),
							organization.getOrganizationId());

						userGroupRoleLocalService.addUserGroupRole(
							user1.getUserId(), group.getGroupId(),
							role.getRoleId());

						return role.getExternalReferenceCode();
					}
				).put(
					"type", "Role"
				).build()
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);
		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"assignee",
				HashMapBuilder.put(
					"externalReferenceCode", user1.getExternalReferenceCode()
				).put(
					"type", "User"
				).build()
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		User user2 = UserTestUtil.addUser();

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"assignee",
				HashMapBuilder.put(
					"externalReferenceCode",
					() -> {
						Role role = RoleTestUtil.addRole(
							"Regular Role", RoleConstants.TYPE_REGULAR);

						userLocalService.addRoleUser(role.getRoleId(), user2);

						return role.getExternalReferenceCode();
					}
				).put(
					"type", "Role"
				).build()
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);
		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"assignee",
				HashMapBuilder.put(
					"externalReferenceCode", user2.getExternalReferenceCode()
				).put(
					"type", "User"
				).build()
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(),
				"?assigneeUserExternalReferenceCode=",
				user1.getExternalReferenceCode(), "&sort=",
				URLCodec.encodeURL("id:asc")),
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(2, itemsJSONArray.length());

		Assert.assertEquals(
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MANAGER,
			JSONUtil.getValue(
				itemsJSONArray.getJSONObject(0), "JSONObject/assignee",
				"Object/name"));
		Assert.assertEquals(
			user1.getFullName(),
			JSONUtil.getValue(
				itemsJSONArray.getJSONObject(1), "JSONObject/assignee",
				"Object/name"));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(),
				"?assigneeUserExternalReferenceCode=",
				user2.getExternalReferenceCode(), "&sort=",
				URLCodec.encodeURL("id:asc")),
			Http.Method.GET);

		itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(2, itemsJSONArray.length());

		Assert.assertEquals(
			"Regular Role",
			JSONUtil.getValue(
				itemsJSONArray.getJSONObject(0), "JSONObject/assignee",
				"Object/name"));
		Assert.assertEquals(
			user2.getFullName(),
			JSONUtil.getValue(
				itemsJSONArray.getJSONObject(1), "JSONObject/assignee",
				"Object/name"));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			getObjectDefinition1().getRESTContextPath() +
				"?assigneeUserExternalReferenceCode=" +
					RandomTestUtil.randomString(),
			Http.Method.GET);

		itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(0, itemsJSONArray.length());
	}

	@Test
	public void testPatchPutCustomObjectEntryByExternalReferenceCodeWithAssigneeObjectField()
		throws Exception {

		_testPatchPutCustomObjectEntryWithAssigneeObjectField(
			Http.Method.PATCH, getObjectDefinition1(), true);
		_testPatchPutCustomObjectEntryWithAssigneeObjectField(
			Http.Method.PUT, getObjectDefinition1(), true);
		_testPatchPutCustomObjectEntryWithAssigneeObjectField(
			Http.Method.PATCH, getSiteScopedObjectDefinition1(), true);
		_testPatchPutCustomObjectEntryWithAssigneeObjectField(
			Http.Method.PUT, getSiteScopedObjectDefinition1(), true);
	}

	@Test
	public void testPatchPutCustomObjectEntryWithAssigneeObjectField()
		throws Exception {

		_testPatchPutCustomObjectEntryWithAssigneeObjectField(
			Http.Method.PATCH, getObjectDefinition1(), false);
		_testPatchPutCustomObjectEntryWithAssigneeObjectField(
			Http.Method.PUT, getObjectDefinition1(), false);
		_testPatchPutCustomObjectEntryWithAssigneeObjectField(
			Http.Method.PATCH, getSiteScopedObjectDefinition1(), false);
		_testPatchPutCustomObjectEntryWithAssigneeObjectField(
			Http.Method.PUT, getSiteScopedObjectDefinition1(), false);
	}

	@Test
	public void testPostCustomObjectEntryWithAssigneeObjectField()
		throws Exception {

		_testPostCustomObjectEntryWithAssigneeObjectField(
			getObjectDefinition1());
		_testPostCustomObjectEntryWithAssigneeObjectField(
			getSiteScopedObjectDefinition1());
	}

	@Test
	public void testPostCustomObjectEntryWithAutoIncrementObjectField()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_AUTO_INCREMENT,
						ObjectFieldConstants.DB_TYPE_STRING, "autoIncrement",
						Arrays.asList(
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.NAME_INITIAL_VALUE
							).value(
								"10"
							).build(),
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.NAME_SUFFIX
							).value(
								"-private"
							).build()))),
				false);

		String endpoint = getEndpoint(objectDefinition, testGroupId);

		HTTPTestUtil.invokeToJSONObject(
			JSONFactoryUtil.getNullJSON(), endpoint, Http.Method.POST);
		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"autoIncrement", "100-private"
			).toString(),
			endpoint, Http.Method.POST);
		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"autoIncrement", "90-private"
			).toString(),
			endpoint, Http.Method.POST);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				endpoint, "?sort=", URLCodec.encodeURL("id:asc")),
			Http.Method.GET);

		assertItem(0, jsonObject, "autoIncrement", "10-private");
		assertItem(1, jsonObject, "autoIncrement", "100-private");
		assertItem(2, jsonObject, "autoIncrement", "90-private");

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				endpoint, "?sort=", URLCodec.encodeURL("autoIncrement:asc")),
			Http.Method.GET);

		assertItem(0, jsonObject, "autoIncrement", "10-private");
		assertItem(1, jsonObject, "autoIncrement", "90-private");
		assertItem(2, jsonObject, "autoIncrement", "100-private");

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				endpoint, "?sort=", URLCodec.encodeURL("autoIncrement:desc")),
			Http.Method.GET);

		assertItem(0, jsonObject, "autoIncrement", "100-private");
		assertItem(1, jsonObject, "autoIncrement", "90-private");
		assertItem(2, jsonObject, "autoIncrement", "10-private");

		objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Test
	public void testPostCustomObjectEntryWithDateTimeObjectField()
		throws Exception {

		User user = GuestOrUserUtil.getGuestOrUser();

		String timeZoneId = user.getTimeZoneId();

		try {
			String testTimeZoneId = "Europe/Madrid";

			user.setTimeZoneId(testTimeZoneId);

			user = userLocalService.updateUser(user);

			// Format: "EEE MMM dd HH:mm:ss zzz yyyy"

			JSONAssert.assertEquals(
				JSONUtil.put(
					OBJECT_FIELD_NAME_DATE_TIME_INPUT, "2000-07-27T11:00:00.000"
				).put(
					OBJECT_FIELD_NAME_DATE_TIME_UTC, "2000-07-27T09:00:00.000Z"
				).toString(),
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_DATE_TIME_INPUT,
						"Thu Jul 27 11:00:00 CET 2000"
					).put(
						OBJECT_FIELD_NAME_DATE_TIME_UTC,
						"Thu Jul 27 11:00:00 CET 2000"
					).toString(),
					getObjectDefinition1().getRESTContextPath(),
					Http.Method.POST
				).toString(),
				JSONCompareMode.LENIENT);

			// Format: "dd-MMM-yyyy hh:mm:ss.SSS a"

			JSONAssert.assertEquals(
				JSONUtil.put(
					OBJECT_FIELD_NAME_DATE_TIME_INPUT, "2000-07-27T11:00:00.000"
				).put(
					OBJECT_FIELD_NAME_DATE_TIME_UTC, "2000-07-27T09:00:00.000Z"
				).toString(),
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_DATE_TIME_INPUT,
						"27-Jul-2000 11:00:00.000 AM"
					).put(
						OBJECT_FIELD_NAME_DATE_TIME_UTC,
						"27-Jul-2000 11:00:00.000 AM"
					).toString(),
					getObjectDefinition1().getRESTContextPath(),
					Http.Method.POST
				).toString(),
				JSONCompareMode.LENIENT);

			// Format: "yyyy-MM-dd HH:mm"

			JSONAssert.assertEquals(
				JSONUtil.put(
					OBJECT_FIELD_NAME_DATE_TIME_INPUT, "2000-07-27T11:00:00.000"
				).put(
					OBJECT_FIELD_NAME_DATE_TIME_UTC, "2000-07-27T09:00:00.000Z"
				).toString(),
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_DATE_TIME_INPUT, "2000-07-27 11:00"
					).put(
						OBJECT_FIELD_NAME_DATE_TIME_UTC, "2000-07-27 11:00"
					).toString(),
					getObjectDefinition1().getRESTContextPath(),
					Http.Method.POST
				).toString(),
				JSONCompareMode.LENIENT);

			// Format: "yyyy-MM-dd HH:mm:ss.S"

			JSONAssert.assertEquals(
				JSONUtil.put(
					OBJECT_FIELD_NAME_DATE_TIME_INPUT, "2000-07-27T11:00:00.000"
				).put(
					OBJECT_FIELD_NAME_DATE_TIME_UTC, "2000-07-27T09:00:00.000Z"
				).toString(),
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_DATE_TIME_INPUT,
						"2000-07-27 11:00:00.000"
					).put(
						OBJECT_FIELD_NAME_DATE_TIME_UTC,
						"2000-07-27 11:00:00.000"
					).toString(),
					getObjectDefinition1().getRESTContextPath(),
					Http.Method.POST
				).toString(),
				JSONCompareMode.LENIENT);

			// Format: "yyyy-MM-dd HH:mm:ss.SSS"

			JSONAssert.assertEquals(
				JSONUtil.put(
					OBJECT_FIELD_NAME_DATE_TIME_INPUT, "2000-07-27T11:00:00.000"
				).put(
					OBJECT_FIELD_NAME_DATE_TIME_UTC, "2000-07-27T09:00:00.000Z"
				).toString(),
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_DATE_TIME_INPUT,
						"2000-07-27 11:00:00.000"
					).put(
						OBJECT_FIELD_NAME_DATE_TIME_UTC,
						"2000-07-27 11:00:00.000"
					).toString(),
					getObjectDefinition1().getRESTContextPath(),
					Http.Method.POST
				).toString(),
				JSONCompareMode.LENIENT);

			// Format: "yyyy-MM-dd'T'HH:mm:ss'Z'"

			JSONAssert.assertEquals(
				JSONUtil.put(
					OBJECT_FIELD_NAME_DATE_TIME_INPUT, "2000-07-27T11:00:00.000"
				).put(
					OBJECT_FIELD_NAME_DATE_TIME_UTC, "2000-07-27T11:00:00.000Z"
				).toString(),
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_DATE_TIME_INPUT,
						"2000-07-27T11:00:00Z"
					).put(
						OBJECT_FIELD_NAME_DATE_TIME_UTC, "2000-07-27T11:00:00Z"
					).toString(),
					getObjectDefinition1().getRESTContextPath(),
					Http.Method.POST
				).toString(),
				JSONCompareMode.LENIENT);

			// Format: "yyyy-MM-dd'T'HH:mm:ss.SSS"

			JSONAssert.assertEquals(
				JSONUtil.put(
					OBJECT_FIELD_NAME_DATE_TIME_INPUT, "2000-07-27T11:00:00.000"
				).put(
					OBJECT_FIELD_NAME_DATE_TIME_UTC, "2000-07-27T09:00:00.000Z"
				).toString(),
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_DATE_TIME_INPUT,
						"2000-07-27T11:00:00.000"
					).put(
						OBJECT_FIELD_NAME_DATE_TIME_UTC,
						"2000-07-27T11:00:00.000"
					).toString(),
					getObjectDefinition1().getRESTContextPath(),
					Http.Method.POST
				).toString(),
				JSONCompareMode.LENIENT);

			// Format: "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

			JSONAssert.assertEquals(
				JSONUtil.put(
					OBJECT_FIELD_NAME_DATE_TIME_INPUT, "2000-07-27T11:00:00.000"
				).put(
					OBJECT_FIELD_NAME_DATE_TIME_UTC, "2000-07-27T11:00:00.000Z"
				).toString(),
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_DATE_TIME_INPUT,
						"2000-07-27T11:00:00.000Z"
					).put(
						OBJECT_FIELD_NAME_DATE_TIME_UTC,
						"2000-07-27T11:00:00.000Z"
					).toString(),
					getObjectDefinition1().getRESTContextPath(),
					Http.Method.POST
				).toString(),
				JSONCompareMode.LENIENT);

			// Format: "yyyy-MM-dd'T'HH:mm:ss.SSSZ" and negative offset

			JSONAssert.assertEquals(
				JSONUtil.put(
					OBJECT_FIELD_NAME_DATE_TIME_INPUT, "2000-07-27T11:00:00.000"
				).put(
					OBJECT_FIELD_NAME_DATE_TIME_UTC, "2000-07-27T15:00:00.000Z"
				).toString(),
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_DATE_TIME_INPUT,
						"2000-07-27T11:00:00.000-0400"
					).put(
						OBJECT_FIELD_NAME_DATE_TIME_UTC,
						"2000-07-27T11:00:00.000-0400"
					).toString(),
					getObjectDefinition1().getRESTContextPath(),
					Http.Method.POST
				).toString(),
				JSONCompareMode.LENIENT);

			// Format: "yyyy-MM-dd'T'HH:mm:ss.SSSZ" and positive offset

			JSONAssert.assertEquals(
				JSONUtil.put(
					OBJECT_FIELD_NAME_DATE_TIME_INPUT, "2000-07-27T11:00:00.000"
				).put(
					OBJECT_FIELD_NAME_DATE_TIME_UTC, "2000-07-27T07:00:00.000Z"
				).toString(),
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_DATE_TIME_INPUT,
						"2000-07-27T11:00:00.000+0400"
					).put(
						OBJECT_FIELD_NAME_DATE_TIME_UTC,
						"2000-07-27T11:00:00.000+0400"
					).toString(),
					getObjectDefinition1().getRESTContextPath(),
					Http.Method.POST
				).toString(),
				JSONCompareMode.LENIENT);
		}
		finally {
			user.setTimeZoneId(timeZoneId);

			userLocalService.updateUser(user);
		}
	}

	@FeatureFlag("LPD-11388")
	@Test
	public void testPostCustomObjectEntryWithLocationObjectField()
		throws Exception {

		GeocoderResult geocoderResult = new GeocoderResult(
			RandomTestUtil.randomString(), RandomTestUtil.randomDouble(),
			RandomTestUtil.randomDouble());

		try (SafeCloseable safeCloseable =
				GeocoderTestUtil.swapWithSafeCloseable(
					geocoderResult,
					objectFieldBusinessTypeRegistry.getObjectFieldBusinessType(
						ObjectFieldConstants.BUSINESS_TYPE_LOCATION))) {

			String defaultValue = RandomTestUtil.randomString();

			ObjectDefinition objectDefinition =
				ObjectDefinitionTestUtil.publishObjectDefinition(
					Collections.singletonList(
						new LocationObjectFieldBuilder(
						).labelMap(
							RandomTestUtil.randomLocaleStringMap()
						).name(
							"location"
						).objectFieldSettings(
							Arrays.asList(
								new ObjectFieldSettingBuilder(
								).name(
									ObjectFieldSettingConstants.
										NAME_DEFAULT_VALUE
								).value(
									JSONUtil.put(
										"address", defaultValue
									).toString()
								).build(),
								new ObjectFieldSettingBuilder(
								).name(
									ObjectFieldSettingConstants.
										NAME_DEFAULT_VALUE_TYPE
								).value(
									ObjectFieldSettingConstants.
										VALUE_INPUT_AS_VALUE
								).build())
						).build()));

			String address = RandomTestUtil.randomString();

			JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"location", JSONUtil.put("address", address)
				).toString(),
				objectDefinition.getRESTContextPath(), Http.Method.POST);

			assertLocationJSONObject(
				address, geocoderResult.getLatitude(),
				geocoderResult.getLongitude(),
				jsonObject.getJSONObject("location"));

			objectValidationRuleLocalService.addObjectValidationRule(
				StringUtil.randomString(), TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId(), true,
				ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
				LocalizedMapUtil.getLocalizedMap("Field must not be empty"),
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
				"not(isEmpty(location))", false, Collections.emptyList());

			jsonObject = HTTPTestUtil.invokeToJSONObject(
				String.valueOf(jsonFactory.createJSONObject()),
				objectDefinition.getRESTContextPath(), Http.Method.POST);

			assertLocationJSONObject(
				defaultValue, geocoderResult.getLatitude(),
				geocoderResult.getLongitude(),
				jsonObject.getJSONObject("location"));
		}
	}

	private JSONObject _postCustomObjectEntryWithAssigneeObjectField(
			ObjectDefinition objectDefinition, User user)
		throws Exception {

		ObjectField objectField = objectFieldLocalService.fetchObjectField(
			objectDefinition.getObjectDefinitionId(), "assignee");

		if (objectField == null) {
			ObjectFieldUtil.addCustomObjectField(
				new AssigneeObjectFieldBuilder(
				).labelMap(
					RandomTestUtil.randomLocaleStringMap()
				).name(
					"assignee"
				).objectDefinitionId(
					objectDefinition.getObjectDefinitionId()
				).userId(
					TestPropsValues.getUserId()
				).build());
		}

		return HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"assignee",
				HashMapBuilder.put(
					"externalReferenceCode", user.getExternalReferenceCode()
				).put(
					"type", "User"
				).build()
			).toString(),
			getEndpoint(objectDefinition, testGroupId), Http.Method.POST);
	}

	private void _testPatchPutCustomObjectEntryWithAssigneeObjectField(
			Http.Method httpMethod, ObjectDefinition objectDefinition,
			boolean useExternalReferenceCode)
		throws Exception {

		JSONObject jsonObject = _postCustomObjectEntryWithAssigneeObjectField(
			objectDefinition, UserTestUtil.addUser());

		String endpoint =
			objectDefinition.getRESTContextPath() + "/" +
				jsonObject.getLong("id");

		if (useExternalReferenceCode) {
			endpoint =
				getEndpoint(objectDefinition, testGroupId) +
					"/by-external-reference-code/" +
						jsonObject.getString("externalReferenceCode");
		}

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"assignee",
				HashMapBuilder.put(
					"externalReferenceCode", role.getExternalReferenceCode()
				).put(
					"type", "Role"
				).build()
			).put(
				"externalReferenceCode",
				jsonObject.getString("externalReferenceCode")
			).put(
				"id", jsonObject.getLong("id")
			).toString(),
			endpoint, httpMethod);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"externalReferenceCode", role.getExternalReferenceCode()
			).put(
				"name", role.getName()
			).put(
				"type", "Role"
			).toString(),
			String.valueOf(jsonObject.get("assignee")),
			JSONCompareMode.LENIENT);
	}

	private void _testPostCustomObjectEntryWithAssigneeObjectField(
			ObjectDefinition objectDefinition)
		throws Exception {

		User user = UserTestUtil.addUser();

		user.setPortraitId(RandomTestUtil.nextLong());

		user = userLocalService.updateUser(user);

		JSONObject jsonObject = _postCustomObjectEntryWithAssigneeObjectField(
			objectDefinition, user);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"externalReferenceCode", user.getExternalReferenceCode()
			).put(
				"name", user.getFullName()
			).put(
				"portrait",
				user.getPortraitURL(
					new ThemeDisplay() {
						{
							setPathImage(_portal.getPathImage());
						}
					})
			).put(
				"type", "User"
			).toString(),
			String.valueOf(jsonObject.get("assignee")),
			JSONCompareMode.LENIENT);
	}

	@Inject
	private OrganizationLocalService _organizationLocalService;

	@Inject
	private Portal _portal;

}