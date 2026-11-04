/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.test.util.ExpandoTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.rest.test.util.ObjectRelationshipTestUtil;
import com.liferay.object.rest.test.util.UserAccountTestUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

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
public class ObjectEntryResourceRelationshipTest
	extends BaseObjectEntryResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-66174")
	public void testGetObjectEntryWithSystemObjectCustomFieldsInOneToManyRelationship()
		throws Exception {

		String expandoFieldName = RandomTestUtil.randomString();
		String expandoFieldValue = RandomTestUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setExpandoBridgeAttributes(
			HashMapBuilder.<String, Serializable>put(
				() -> {
					ExpandoColumn expandoColumn = ExpandoTestUtil.addColumn(
						ExpandoTestUtil.addTable(
							PortalUtil.getClassNameId(User.class),
							ExpandoTableConstants.DEFAULT_TABLE_NAME),
						expandoFieldName, ExpandoColumnConstants.STRING);

					return expandoColumn.getName();
				},
				expandoFieldValue
			).build());

		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new long[0], serviceContext);

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			userSystemObjectDefinition, getObjectDefinition1(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		JSONObject jsonObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1
			).put(
				StringBundler.concat(
					"r_", objectRelationship1.getName(), "_userERC"),
				user.getExternalReferenceCode()
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		JSONObject jsonObject2 = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(), StringPool.SLASH,
				jsonObject1.getString("id"), "?nestedFields=",
				objectRelationship1.getName()),
			Http.Method.GET);

		JSONArray customFieldsJSONArray = JSONUtil.getValueAsJSONArray(
			jsonObject2, "JSONObject/" + objectRelationship1.getName(),
			"JSONArray/customFields");

		Assert.assertEquals(1, customFieldsJSONArray.length());

		JSONAssert.assertEquals(
			JSONUtil.put(
				"customValue", JSONUtil.put("data", expandoFieldValue)
			).put(
				"dataType", "Text"
			).put(
				"name", expandoFieldName
			).toString(),
			String.valueOf(customFieldsJSONArray.getJSONObject(0)),
			JSONCompareMode.STRICT);
	}

	@Test
	public void testGetObjectRelationshipERCFieldNameInOneToManyRelationship()
		throws Exception {

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);

		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, getObjectDefinition2().getRESTContextPath(), Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());

		JSONObject itemJSONObject = itemsJSONArray.getJSONObject(0);

		Assert.assertEquals(
			itemJSONObject.getString(objectRelationship1.getName() + "ERC"),
			objectEntry1.getExternalReferenceCode());
	}

	@Test
	public void testGetObjectRelationshipERCFieldNameInOneToManyRelationshipFromRelatedObjectEntry()
		throws Exception {

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);

		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);

		objectRelationship1 = addObjectRelationshipAndRelateObjectEntries(
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(), "?nestedFields=",
				objectRelationship1.getName()),
			Http.Method.GET);

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(1, itemsJSONArray.length());

		JSONObject itemJSONObject = itemsJSONArray.getJSONObject(0);

		JSONArray relationshipJSONArray = itemJSONObject.getJSONArray(
			objectRelationship1.getName());

		Assert.assertEquals(1, relationshipJSONArray.length());

		JSONObject relatedObjectEntryJSONObject =
			relationshipJSONArray.getJSONObject(0);

		Assert.assertEquals(
			relatedObjectEntryJSONObject.getString(
				objectRelationship1.getName() + "ERC"),
			objectEntry1.getExternalReferenceCode());
	}

	@Test
	public void testPostCustomObjectEntryWithManyToOneRelationshipPriorities()
		throws Exception {

		// Custom object

		JSONObject customObjectJSONObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1
			).toString(),
			getObjectDefinition1().getRESTContextPath() +
				"?fields=id,externalReferenceCode," + OBJECT_FIELD_NAME_1,
			Http.Method.POST);

		JSONObject customObjectJSONObject2 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).toString(),
			getObjectDefinition1().getRESTContextPath() +
				"?fields=id,externalReferenceCode," + OBJECT_FIELD_NAME_1,
			Http.Method.POST);

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_testPostCustomObjectEntryWithManyToOneRelationshipPriorities(
			customObjectJSONObject1, customObjectJSONObject2,
			JSONFactoryUtil::createJSONObject, objectRelationship1);

		// System object

		JSONObject systemObjectJSONObject1 =
			UserAccountTestUtil.addUserAccountJSONObject(
				systemObjectDefinitionManager,
				HashMapBuilder.<String, Serializable>put(
					OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_1)
				).build());

		JSONObject systemObjectJSONObject2 =
			UserAccountTestUtil.addUserAccountJSONObject(
				systemObjectDefinitionManager,
				HashMapBuilder.<String, Serializable>put(
					OBJECT_FIELD_NAME_2, RandomTestUtil.randomString()
				).build());

		objectRelationship2 = ObjectRelationshipTestUtil.addObjectRelationship(
			userSystemObjectDefinition, getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_testPostCustomObjectEntryWithManyToOneRelationshipPriorities(
			systemObjectJSONObject1, systemObjectJSONObject2,
			() -> JSONFactoryUtil.createJSONObject(
				UserAccountTestUtil.randomUserAccount(
				).toString()),
			objectRelationship2);
	}

	@Test
	public void testPostCustomObjectEntryWithNonexistentRelatedCustomObjectEntryId()
		throws Exception {

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		ObjectField objectField = objectFieldLocalService.fetchObjectField(
			objectRelationship1.getObjectFieldId2());

		JSONObject objectEntryJSONObject = JSONUtil.put(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2
		).put(
			StringBundler.concat(
				"r_", objectRelationship1.getName(), "_",
				getObjectDefinition1().getPKObjectFieldName()),
			StringPool.BLANK
		);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			objectEntryJSONObject.toString(),
			getObjectDefinition2().getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		long randomObjectEntryId = RandomTestUtil.randomLong();

		objectEntryJSONObject = JSONUtil.put(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2
		).put(
			StringBundler.concat(
				"r_", objectRelationship1.getName(), "_",
				getObjectDefinition1().getPKObjectFieldName()),
			randomObjectEntryId
		);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "NOT_FOUND"
			).put(
				"title",
				String.format(
					"The value for %s does not exist.", objectField.getName())
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				objectEntryJSONObject.toString(),
				getObjectDefinition2().getRESTContextPath(), Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				userSystemObjectDefinition, getObjectDefinition2(),
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		objectField = objectFieldLocalService.fetchObjectField(
			objectRelationship.getObjectFieldId2());

		objectEntryJSONObject = JSONUtil.put(
			OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2
		).put(
			StringBundler.concat(
				"r_", objectRelationship.getName(), "_",
				userSystemObjectDefinition.getPKObjectFieldName()),
			randomObjectEntryId
		);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "NOT_FOUND"
			).put(
				"title",
				String.format(
					"The value for %s does not exist.", objectField.getName())
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				objectEntryJSONObject.toString(),
				getObjectDefinition2().getRESTContextPath(), Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);
	}

	@Test
	public void testPostRelatedObjectEntryInDifferentCompany()
		throws Exception {

		ObjectRelationship objectRelationship1 = _addObjectRelationship(
			TestPropsValues.getCompanyId());

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			objectDefinitionLocalService.getObjectDefinition(
				objectRelationship1.getObjectDefinitionId1()),
			OBJECT_FIELD_NAME_TEXT, RandomTestUtil.randomString());

		Company company = getCompanyByWebId();

		ObjectRelationship objectRelationship2 = _addObjectRelationship(
			company.getCompanyId());

		HTTPTestUtil.customize(
		).withBaseURL(
			"http://www.able.com:" + PortalUtil.getPortalServerPort(false)
		).withCredentials(
			"test@www.able.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).apply(
			() -> {
				ObjectDefinition objectDefinition =
					objectDefinitionLocalService.getObjectDefinition(
						objectRelationship2.getObjectDefinitionId2());

				ObjectField objectField =
					objectFieldLocalService.getObjectField(
						objectRelationship2.getObjectFieldId2());

				Assert.assertEquals(
					400,
					HTTPTestUtil.invokeToHttpCode(
						JSONUtil.put(
							objectField.getName(),
							objectEntry.getObjectEntryId()
						).toString(),
						objectDefinition.getRESTContextPath(),
						Http.Method.POST));
			}
		);
	}

	@Test
	public void testPutByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCodeWithSlash()
		throws Exception {

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);

		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", "a"
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);
		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", "a/b"
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);
		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", "c"
			).toString(),
			getObjectDefinition2().getRESTContextPath(), Http.Method.POST);
		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", "c/d"
			).toString(),
			getObjectDefinition2().getRESTContextPath(), Http.Method.POST);

		// Slash on the left side

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					null,
					StringBundler.concat(
						getObjectDefinition1().getRESTContextPath(),
						"/by-external-reference-code/a/b/",
						objectRelationship1.getName(), "/c"),
					Http.Method.PUT));
		}

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				null,
				StringBundler.concat(
					getObjectDefinition1().getRESTContextPath(),
					"/by-external-reference-code/a%252Fb/",
					objectRelationship1.getName(), "/c"),
				Http.Method.PUT));

		// Slash on the right side

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					null,
					StringBundler.concat(
						getObjectDefinition1().getRESTContextPath(),
						"/by-external-reference-code/a/",
						objectRelationship1.getName(), "/c/d"),
					Http.Method.PUT));
		}

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				null,
				StringBundler.concat(
					getObjectDefinition1().getRESTContextPath(),
					"/by-external-reference-code/a/",
					objectRelationship1.getName(), "/c%252Fd"),
				Http.Method.PUT));

		// Slash on the both sides

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					null,
					StringBundler.concat(
						getObjectDefinition1().getRESTContextPath(),
						"/by-external-reference-code/a/b/",
						objectRelationship1.getName(), "/c/d"),
					Http.Method.PUT));
		}

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				null,
				StringBundler.concat(
					getObjectDefinition1().getRESTContextPath(),
					"/by-external-reference-code/a%252Fb/",
					objectRelationship1.getName(), "/c%252Fd"),
				Http.Method.PUT));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					null,
					StringBundler.concat(
						getObjectDefinition1().getRESTContextPath(),
						"/by-external-reference-code/a%252Fb/",
						objectRelationship1.getName(), "/c/d"),
					Http.Method.PUT));
			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					null,
					StringBundler.concat(
						getObjectDefinition1().getRESTContextPath(),
						"/by-external-reference-code/a/b/",
						objectRelationship1.getName(), "/c%252Fd"),
					Http.Method.PUT));
		}
	}

	@Test
	public void testPutByExternalReferenceCodeManyToManyRelationship()
		throws Exception {

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);

		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition2(), OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(),
				"/by-external-reference-code/",
				objectEntry1.getExternalReferenceCode(), StringPool.SLASH,
				objectRelationship1.getName(), StringPool.SLASH,
				objectEntry2.getExternalReferenceCode()),
			Http.Method.PUT);

		Assert.assertEquals(
			objectEntry2.getExternalReferenceCode(),
			jsonObject.getString("externalReferenceCode"));
		Assert.assertEquals(
			OBJECT_FIELD_VALUE_2, jsonObject.getInt(OBJECT_FIELD_NAME_2));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition2().getRESTContextPath(),
				"/by-external-reference-code/",
				objectEntry2.getExternalReferenceCode(), StringPool.SLASH,
				objectRelationship1.getName(), StringPool.SLASH,
				objectEntry1.getExternalReferenceCode()),
			Http.Method.PUT);

		Assert.assertEquals(
			objectEntry1.getExternalReferenceCode(),
			jsonObject.getString("externalReferenceCode"));
		Assert.assertEquals(
			OBJECT_FIELD_VALUE_1, jsonObject.getInt(OBJECT_FIELD_NAME_1));

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition2().getRESTContextPath(),
				"/by-external-reference-code/",
				objectEntry2.getExternalReferenceCode(), StringPool.SLASH,
				objectRelationship1.getName(), StringPool.SLASH,
				RandomTestUtil.randomString()),
			Http.Method.PUT);

		Assert.assertNull(jsonObject.get("title"));
	}

	@Test
	public void testPutByExternalReferenceCodeManyToManyRelationshipWithSelf()
		throws Exception {

		String objectFieldValue1 = RandomTestUtil.randomString();
		String objectFieldValue2 = RandomTestUtil.randomString();
		String objectFieldValue3 = RandomTestUtil.randomString();

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition1(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, objectFieldValue1
			).put(
				objectRelationship1.getName(),
				JSONUtil.putAll(
					JSONUtil.put(
						OBJECT_FIELD_NAME_1, objectFieldValue2
					).put(
						"externalReferenceCode", ERC_VALUE_2
					),
					JSONUtil.put(
						OBJECT_FIELD_NAME_1, objectFieldValue3
					).put(
						"externalReferenceCode", ERC_VALUE_3
					))
			).toString(),
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(),
				"/by-external-reference-code/", ERC_VALUE_1),
			Http.Method.PUT);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"items",
				JSONUtil.putAll(
					JSONUtil.put(
						OBJECT_FIELD_NAME_1, objectFieldValue2
					).put(
						"externalReferenceCode", ERC_VALUE_2
					),
					JSONUtil.put(
						OBJECT_FIELD_NAME_1, objectFieldValue3
					).put(
						"externalReferenceCode", ERC_VALUE_3
					))
			).put(
				"totalCount", 2
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					getObjectDefinition1().getRESTContextPath(),
					StringPool.SLASH, jsonObject.get("id"), StringPool.SLASH,
					objectRelationship1.getName()),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1
			).put(
				objectRelationship1.getName(),
				JSONUtil.putAll(
					JSONUtil.put(
						OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_2
					).put(
						"externalReferenceCode", ERC_VALUE_2
					),
					JSONUtil.put(
						OBJECT_FIELD_NAME_1, _NEW_OBJECT_FIELD_VALUE_3
					).put(
						"externalReferenceCode", ERC_VALUE_3
					))
			).toString(),
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(),
				"/by-external-reference-code/", ERC_VALUE_1),
			Http.Method.PUT);

		JSONAssert.assertEquals(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1
			).put(
				objectRelationship1.getName(),
				JSONUtil.putAll(
					JSONUtil.put(
						OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_2
					).put(
						"externalReferenceCode", ERC_VALUE_2
					),
					JSONUtil.put(
						OBJECT_FIELD_NAME_1, _NEW_OBJECT_FIELD_VALUE_3
					).put(
						"externalReferenceCode", ERC_VALUE_3
					))
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					getObjectDefinition1().getRESTContextPath(),
					StringPool.SLASH, jsonObject.get("id"), "?nestedFields=",
					objectRelationship1.getName()),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);
	}

	@Test
	public void testPutByExternalReferenceCodeMultipleManyToManyRelationships()
		throws Exception {

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		objectRelationship2 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition3(), getObjectDefinition1(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"externalReferenceCode", ERC_VALUE_1
				).put(
					objectRelationship1.getName(),
					createObjectEntriesJSONArray(
						new String[] {RandomTestUtil.randomString()},
						OBJECT_FIELD_NAME_2,
						new String[] {RandomTestUtil.randomString()})
				).put(
					objectRelationship2.getName(),
					createObjectEntriesJSONArray(
						new String[] {ERC_VALUE_3},
						RandomTestUtil.randomString(),
						new String[] {RandomTestUtil.randomString()})
				).toString(),
				getObjectDefinition1().getRESTContextPath(), Http.Method.POST));

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1
				).put(
					objectRelationship1.getName(),
					createObjectEntriesJSONArray(
						new String[] {ERC_VALUE_2}, OBJECT_FIELD_NAME_2,
						new String[] {NEW_OBJECT_FIELD_VALUE_2})
				).toString(),
				StringBundler.concat(
					getObjectDefinition1().getRESTContextPath(),
					"/by-external-reference-code/", ERC_VALUE_1),
				Http.Method.PUT));

		JSONAssert.assertEquals(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1
			).put(
				objectRelationship1.getName(),
				JSONUtil.putAll(
					JSONUtil.put("externalReferenceCode", ERC_VALUE_2))
			).put(
				objectRelationship2.getName(),
				JSONUtil.putAll(
					JSONUtil.put("externalReferenceCode", ERC_VALUE_3))
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					getObjectDefinition1().getRESTContextPath(),
					"/by-external-reference-code/", ERC_VALUE_1,
					"?nestedFields=", objectRelationship1.getName(), ",",
					objectRelationship2.getName()),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);
	}

	@Test
	@TestInfo("LPD-53245")
	public void testPutByExternalReferenceCodeMultipleOneToManyRelationships()
		throws Exception {

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		objectRelationship2 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition2(), getObjectDefinition3(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_2, RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", ERC_VALUE_2
			).put(
				objectRelationship1.getName(),
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
				).put(
					"externalReferenceCode", ERC_VALUE_1
				)
			).put(
				objectRelationship2.getName(),
				JSONUtil.putAll(
					JSONUtil.put(
						OBJECT_FIELD_NAME_3, RandomTestUtil.randomString()
					).put(
						"externalReferenceCode", ERC_VALUE_3
					),
					JSONUtil.put(
						OBJECT_FIELD_NAME_3, RandomTestUtil.randomString()
					).put(
						"externalReferenceCode", RandomTestUtil.randomString()
					))
			).toString(),
			getObjectDefinition2().getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_2, NEW_OBJECT_FIELD_VALUE_2
				).toString(),
				StringBundler.concat(
					getObjectDefinition2().getRESTContextPath(),
					"/by-external-reference-code/", ERC_VALUE_2),
				Http.Method.PUT));

		String objectEntryId = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(),
				"/by-external-reference-code/", ERC_VALUE_1),
			Http.Method.GET
		).get(
			"id"
		).toString();

		JSONAssert.assertEquals(
			JSONUtil.put(
				"items",
				JSONUtil.putAll(
					JSONUtil.put("externalReferenceCode", ERC_VALUE_2))
			).put(
				"lastPage", 1
			).put(
				"page", 1
			).put(
				"pageSize", 20
			).put(
				"totalCount", 1
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					getObjectDefinition1().getRESTContextPath(),
					StringPool.SLASH, objectEntryId, StringPool.SLASH,
					objectRelationship1.getName()),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_2, NEW_OBJECT_FIELD_VALUE_2
				).put(
					objectRelationship1.getName(),
					JSONUtil.put(
						OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1
					).put(
						"externalReferenceCode", ERC_VALUE_1
					)
				).put(
					objectRelationship2.getName(),
					createObjectEntriesJSONArray(
						new String[] {ERC_VALUE_3}, OBJECT_FIELD_NAME_3,
						new String[] {_NEW_OBJECT_FIELD_VALUE_3})
				).toString(),
				StringBundler.concat(
					getObjectDefinition2().getRESTContextPath(),
					"/by-external-reference-code/", ERC_VALUE_2),
				Http.Method.PUT));

		JSONAssert.assertEquals(
			JSONUtil.put(
				"items",
				JSONUtil.putAll(
					JSONUtil.put(
						OBJECT_FIELD_NAME_2, NEW_OBJECT_FIELD_VALUE_2
					).put(
						"externalReferenceCode", ERC_VALUE_2
					))
			).put(
				"lastPage", 1
			).put(
				"page", 1
			).put(
				"pageSize", 20
			).put(
				"totalCount", 1
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					getObjectDefinition1().getRESTContextPath(),
					StringPool.SLASH, objectEntryId, StringPool.SLASH,
					objectRelationship1.getName()),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"items",
				JSONUtil.putAll(
					JSONUtil.put(
						OBJECT_FIELD_NAME_3, _NEW_OBJECT_FIELD_VALUE_3
					).put(
						"externalReferenceCode", ERC_VALUE_3
					))
			).put(
				"lastPage", 1
			).put(
				"page", 1
			).put(
				"pageSize", 20
			).put(
				"totalCount", 1
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					getObjectDefinition2().getRESTContextPath(),
					StringPool.SLASH, jsonObject.get("id"), StringPool.SLASH,
					objectRelationship2.getName()),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);
	}

	@Test
	public void testPutByExternalReferenceCodeWithNonexistentValueOneToManyRelationship()
		throws Exception {

		objectRelationship1 = ObjectRelationshipTestUtil.addObjectRelationship(
			getObjectDefinition1(), getObjectDefinition2(),
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1
				).put(
					objectRelationship1.getName(),
					createObjectEntriesJSONArray(
						new String[] {ERC_VALUE_2}, OBJECT_FIELD_NAME_2,
						new String[] {NEW_OBJECT_FIELD_VALUE_2})
				).toString(),
				StringBundler.concat(
					getObjectDefinition1().getRESTContextPath(),
					"/by-external-reference-code/", ERC_VALUE_1),
				Http.Method.PUT));

		JSONAssert.assertEquals(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1
			).put(
				objectRelationship1.getName(),
				JSONUtil.putAll(
					JSONUtil.put("externalReferenceCode", ERC_VALUE_2))
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					getObjectDefinition1().getRESTContextPath(),
					"/by-external-reference-code/", ERC_VALUE_1,
					"?nestedFields=", objectRelationship1.getName()),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);
	}

	private ObjectRelationship _addObjectRelationship(long companyId)
		throws Exception {

		User user = UserTestUtil.getAdminUser(companyId);

		ObjectDefinition objectDefinition1 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						OBJECT_FIELD_NAME_TEXT
					).build()),
				ObjectDefinitionConstants.SCOPE_COMPANY, user.getUserId());
		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						OBJECT_FIELD_NAME_TEXT
					).build()),
				ObjectDefinitionConstants.SCOPE_COMPANY, user.getUserId());

		return ObjectRelationshipTestUtil.addObjectRelationship(
			objectDefinition1, objectDefinition2, user.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
	}

	private void _testPostCustomObjectEntryWithManyToOneRelationshipPriorities(
			JSONObject jsonObject1, JSONObject jsonObject2,
			UnsafeSupplier<JSONObject, Exception> jsonObjectUnsafeSupplier,
			ObjectRelationship objectRelationship)
		throws Exception {

		ObjectField objectField = objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());

		String objectFieldName = objectField.getName();

		String objectRelationshipERCObjectFieldName =
			ObjectFieldSettingUtil.getValue(
				ObjectFieldSettingConstants.
					NAME_OBJECT_RELATIONSHIP_ERC_OBJECT_FIELD_NAME,
				objectField);

		// priority(ERC) > priority(invalid id)

		JSONAssert.assertEquals(
			JSONUtil.put(
				OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)
			).put(
				objectFieldName, Long.valueOf(jsonObject1.getLong("id"))
			).put(
				objectRelationshipERCObjectFieldName,
				jsonObject1.getString("externalReferenceCode")
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2
				).put(
					objectFieldName, RandomTestUtil.randomLong()
				).put(
					objectRelationshipERCObjectFieldName,
					jsonObject1.getString("externalReferenceCode")
				).toString(),
				getObjectDefinition2().getRESTContextPath(), Http.Method.POST
			).toString(),
			JSONCompareMode.LENIENT);

		//  priority(id) > priority(ERC)

		JSONAssert.assertEquals(
			JSONUtil.put(
				OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)
			).put(
				objectFieldName, Long.valueOf(jsonObject1.getLong("id"))
			).put(
				objectRelationshipERCObjectFieldName,
				jsonObject1.getString("externalReferenceCode")
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2
				).put(
					objectFieldName, jsonObject1.getLong("id")
				).put(
					objectRelationshipERCObjectFieldName,
					jsonObject2.getString("externalReferenceCode")
				).toString(),
				getObjectDefinition2().getRESTContextPath(), Http.Method.POST
			).toString(),
			JSONCompareMode.LENIENT);

		// priority(relationshipName) > priority(id) > priority(ERC)

		String objectRelationshipName = objectRelationship.getName();

		JSONObject actualJSONObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2
			).put(
				objectFieldName, jsonObject2.getLong("id")
			).put(
				objectRelationshipERCObjectFieldName,
				jsonObject2.getString("externalReferenceCode")
			).put(
				objectRelationshipName, jsonObject1
			).toString(),
			getObjectDefinition2().getRESTContextPath(), Http.Method.POST);

		JSONAssert.assertEquals(
			JSONUtil.put(
				OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)
			).put(
				objectFieldName, Long.valueOf(jsonObject1.getLong("id"))
			).put(
				objectRelationshipERCObjectFieldName,
				jsonObject1.getString("externalReferenceCode")
			).toString(),
			actualJSONObject1.toString(), JSONCompareMode.LENIENT);

		Assert.assertEquals(
			jsonObject1.getString("externalReferenceCode"),
			JSONUtil.getValueAsString(
				actualJSONObject1, "JSONObject/" + objectRelationshipName,
				"Object/externalReferenceCode"));

		JSONObject actualJSONObject2 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2
			).put(
				objectFieldName, jsonObject1.getLong("id")
			).put(
				objectRelationshipERCObjectFieldName,
				jsonObject1.getString("externalReferenceCode")
			).put(
				objectRelationshipName, jsonObjectUnsafeSupplier.get()
			).toString(),
			getObjectDefinition2().getRESTContextPath(), Http.Method.POST);

		Assert.assertNotEquals(
			jsonObject1.getLong("id"),
			actualJSONObject2.getLong(objectFieldName));

		Assert.assertNotEquals(
			jsonObject1.getString("externalReferenceCode"),
			actualJSONObject2.getString(objectRelationshipERCObjectFieldName));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			// invalid ERC

			JSONAssert.assertEquals(
				JSONUtil.put(
					"status", "NOT_FOUND"
				).toString(),
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2
					).put(
						objectRelationshipERCObjectFieldName,
						RandomTestUtil.randomString()
					).toString(),
					getObjectDefinition2().getRESTContextPath(),
					Http.Method.POST
				).toString(),
				JSONCompareMode.LENIENT);

			// priority(invalid id) > priority(invalid ERC)

			JSONAssert.assertEquals(
				JSONUtil.put(
					"status", "NOT_FOUND"
				).toString(),
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2
					).put(
						objectFieldName, RandomTestUtil.randomLong()
					).put(
						objectRelationshipERCObjectFieldName,
						RandomTestUtil.randomString()
					).toString(),
					getObjectDefinition2().getRESTContextPath(),
					Http.Method.POST
				).toString(),
				JSONCompareMode.LENIENT);
		}
	}

	private static final String _NEW_OBJECT_FIELD_VALUE_3 =
		RandomTestUtil.randomString();

}