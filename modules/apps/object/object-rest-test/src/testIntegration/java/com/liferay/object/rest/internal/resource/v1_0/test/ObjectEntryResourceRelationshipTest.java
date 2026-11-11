/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
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
 * @author Magdalena Jedraszak
 * @author Daniel Raposo
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
	public void testGetObjectEntriesPageWithObjectRelationshipERCObjectFieldInOneToManyRelationship()
		throws Exception {

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			getObjectDefinition1(), OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);

		ObjectDefinition objectDefinition2 = getObjectDefinition2();

		ObjectEntry objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			objectDefinition2, OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);

		ObjectRelationship objectRelationship =
			addObjectRelationshipAndRelateObjectEntries(
				getObjectDefinition1(), objectDefinition2,
				objectEntry1.getPrimaryKey(), objectEntry2.getPrimaryKey(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"items",
				JSONUtil.putAll(
					JSONUtil.put(
						objectRelationship.getName() + "ERC",
						objectEntry1.getExternalReferenceCode()))
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null, objectDefinition2.getRESTContextPath(), Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);

		JSONObject jsonObject = getNestedFieldsObjectEntryJSONObject(
			null, objectRelationship.getName(), getObjectDefinition1());

		JSONAssert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					objectRelationship.getName() + "ERC",
					objectEntry1.getExternalReferenceCode())
			).toString(),
			String.valueOf(
				jsonObject.getJSONArray(objectRelationship.getName())),
			JSONCompareMode.LENIENT);
	}

	@Test
	@TestInfo("LPD-66174")
	public void testGetObjectEntryWithSystemObjectCustomFieldsInOneToManyRelationship()
		throws Exception {

		String expandoFieldName = RandomTestUtil.randomString(
			UniqueStringRandomizerBumper.INSTANCE);
		String expandoFieldValue = RandomTestUtil.randomString();

		ExpandoTestUtil.addColumn(
			ExpandoTestUtil.addTable(
				PortalUtil.getClassNameId(User.class),
				ExpandoTableConstants.DEFAULT_TABLE_NAME),
			expandoFieldName, ExpandoColumnConstants.STRING);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setExpandoBridgeAttributes(
			HashMapBuilder.<String, Serializable>put(
				expandoFieldName, expandoFieldValue
			).build());

		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(UniqueStringRandomizerBumper.INSTANCE),
			LocaleUtil.getDefault(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new long[0], serviceContext);

		ObjectDefinition objectDefinition = getObjectDefinition1();

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				getUserSystemObjectDefinition(), objectDefinition,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1
			).put(
				StringBundler.concat(
					"r_", objectRelationship.getName(), "_userERC"),
				user.getExternalReferenceCode()
			).toString(),
			objectDefinition.getRESTContextPath(), Http.Method.POST);

		JSONAssert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					"customValue", JSONUtil.put("data", expandoFieldValue)
				).put(
					"dataType", "Text"
				).put(
					"name", expandoFieldName
				)
			).toString(),
			String.valueOf(
				JSONUtil.getValueAsJSONArray(
					HTTPTestUtil.invokeToJSONObject(
						null,
						getNestedFieldsEndpoint(
							objectDefinition, jsonObject.getLong("id"),
							objectRelationship),
						Http.Method.GET),
					"JSONObject/" + objectRelationship.getName(),
					"JSONArray/customFields")),
			JSONCompareMode.STRICT);
	}

	@Test
	public void testPostCustomObjectEntryWithManyToOneRelationshipPriorities()
		throws Exception {

		// Custom object

		ObjectDefinition objectDefinition1 = getObjectDefinition1();

		JSONObject customObjectJSONObject1 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1
			).toString(),
			objectDefinition1.getRESTContextPath() +
				"?fields=id,externalReferenceCode," + OBJECT_FIELD_NAME_1,
			Http.Method.POST);
		JSONObject customObjectJSONObject2 = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).toString(),
			objectDefinition1.getRESTContextPath() +
				"?fields=id,externalReferenceCode," + OBJECT_FIELD_NAME_1,
			Http.Method.POST);

		ObjectRelationship objectRelationship1 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition1, getObjectDefinition2(),
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_testPostCustomObjectEntryWithManyToOneRelationshipPriorities(
			customObjectJSONObject1, customObjectJSONObject2,
			JSONFactoryUtil::createJSONObject, objectRelationship1);

		// System object

		JSONObject systemObjectJSONObject1 =
			UserAccountTestUtil.addUserAccountJSONObject(
				getUserSystemObjectDefinitionManager(),
				HashMapBuilder.<String, Serializable>put(
					OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_1)
				).build());
		JSONObject systemObjectJSONObject2 =
			UserAccountTestUtil.addUserAccountJSONObject(
				getUserSystemObjectDefinitionManager(),
				HashMapBuilder.<String, Serializable>put(
					OBJECT_FIELD_NAME_2, RandomTestUtil.randomString()
				).build());

		ObjectRelationship objectRelationship2 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				getUserSystemObjectDefinition(), getObjectDefinition2(),
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		_testPostCustomObjectEntryWithManyToOneRelationshipPriorities(
			systemObjectJSONObject1, systemObjectJSONObject2,
			() -> JSONFactoryUtil.createJSONObject(
				String.valueOf(UserAccountTestUtil.randomUserAccount())),
			objectRelationship2);
	}

	@Test
	public void testPostCustomObjectEntryWithNonexistentRelatedObjectEntryId()
		throws Exception {

		ObjectDefinition objectDefinition1 = getObjectDefinition1();
		ObjectDefinition objectDefinition2 = getObjectDefinition2();

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition1, objectDefinition2,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2
			).put(
				StringBundler.concat(
					"r_", objectRelationship.getName(), "_",
					objectDefinition1.getPKObjectFieldName()),
				StringPool.BLANK
			).toString(),
			objectDefinition2.getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals(
			0,
			jsonObject.getJSONObject(
				"status"
			).get(
				"code"
			));

		_testPostCustomObjectEntryWithNonexistentRelatedObjectEntryId(
			objectDefinition1, objectRelationship);
		_testPostCustomObjectEntryWithNonexistentRelatedObjectEntryId(
			getUserSystemObjectDefinition(),
			ObjectRelationshipTestUtil.addObjectRelationship(
				getUserSystemObjectDefinition(), objectDefinition2,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY));
	}

	@Test
	public void testPostCustomObjectEntryWithRelatedObjectEntryInDifferentCompany()
		throws Exception {

		ObjectRelationship objectRelationship = _addObjectRelationship(
			TestPropsValues.getCompanyId());

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId1()),
			OBJECT_FIELD_NAME_TEXT, RandomTestUtil.randomString());

		Company company = getCompanyByWebId();

		ObjectRelationship companyObjectRelationship = _addObjectRelationship(
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
						companyObjectRelationship.getObjectDefinitionId2());
				ObjectField objectField =
					objectFieldLocalService.getObjectField(
						companyObjectRelationship.getObjectFieldId2());

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
	public void testPutByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode()
		throws Exception {

		ObjectDefinition objectDefinition1 = getObjectDefinition1();

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			objectDefinition1, OBJECT_FIELD_NAME_1, OBJECT_FIELD_VALUE_1);

		ObjectDefinition objectDefinition2 = getObjectDefinition2();

		ObjectEntry objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			objectDefinition2, OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2);

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition1, objectDefinition2,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				objectDefinition1.getRESTContextPath(),
				"/by-external-reference-code/",
				objectEntry1.getExternalReferenceCode(), StringPool.SLASH,
				objectRelationship.getName(), StringPool.SLASH,
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
				objectDefinition2.getRESTContextPath(),
				"/by-external-reference-code/",
				objectEntry2.getExternalReferenceCode(), StringPool.SLASH,
				objectRelationship.getName(), StringPool.SLASH,
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
				objectDefinition2.getRESTContextPath(),
				"/by-external-reference-code/",
				objectEntry2.getExternalReferenceCode(), StringPool.SLASH,
				objectRelationship.getName(), StringPool.SLASH,
				RandomTestUtil.randomString()),
			Http.Method.PUT);

		Assert.assertNull(jsonObject.get("title"));

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", "a"
			).toString(),
			objectDefinition1.getRESTContextPath(), Http.Method.POST);
		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", "a/b"
			).toString(),
			objectDefinition1.getRESTContextPath(), Http.Method.POST);
		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"externalReferenceCode", "c"
			).toString(),
			objectDefinition2.getRESTContextPath(), Http.Method.POST);
		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"externalReferenceCode", "c/d"
			).toString(),
			objectDefinition2.getRESTContextPath(), Http.Method.POST);

		// Slash on both sides

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					null,
					StringBundler.concat(
						objectDefinition1.getRESTContextPath(),
						"/by-external-reference-code/a%252Fb/",
						objectRelationship.getName(), "/c/d"),
					Http.Method.PUT));
			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					null,
					StringBundler.concat(
						objectDefinition1.getRESTContextPath(),
						"/by-external-reference-code/a/b/",
						objectRelationship.getName(), "/c%252Fd"),
					Http.Method.PUT));
			Assert.assertEquals(
				404,
				HTTPTestUtil.invokeToHttpCode(
					null,
					StringBundler.concat(
						objectDefinition1.getRESTContextPath(),
						"/by-external-reference-code/a/b/",
						objectRelationship.getName(), "/c/d"),
					Http.Method.PUT));
		}

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				null,
				StringBundler.concat(
					objectDefinition1.getRESTContextPath(),
					"/by-external-reference-code/a%252Fb/",
					objectRelationship.getName(), "/c%252Fd"),
				Http.Method.PUT));

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
						objectDefinition1.getRESTContextPath(),
						"/by-external-reference-code/a/b/",
						objectRelationship.getName(), "/c"),
					Http.Method.PUT));
		}

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				null,
				StringBundler.concat(
					objectDefinition1.getRESTContextPath(),
					"/by-external-reference-code/a%252Fb/",
					objectRelationship.getName(), "/c"),
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
						objectDefinition1.getRESTContextPath(),
						"/by-external-reference-code/a/",
						objectRelationship.getName(), "/c/d"),
					Http.Method.PUT));
		}

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				null,
				StringBundler.concat(
					objectDefinition1.getRESTContextPath(),
					"/by-external-reference-code/a/",
					objectRelationship.getName(), "/c%252Fd"),
				Http.Method.PUT));
	}

	@Test
	public void testPutByExternalReferenceCodeManyToManyRelationshipWithSelf()
		throws Exception {

		ObjectDefinition objectDefinition = getObjectDefinition1();

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition, objectDefinition, TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		String objectFieldValue1 = RandomTestUtil.randomString();
		String objectFieldValue2 = RandomTestUtil.randomString();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, RandomTestUtil.randomString()
			).put(
				objectRelationship.getName(),
				JSONUtil.putAll(
					JSONUtil.put(
						OBJECT_FIELD_NAME_1, objectFieldValue1
					).put(
						"externalReferenceCode", ERC_VALUE_2
					),
					JSONUtil.put(
						OBJECT_FIELD_NAME_1, objectFieldValue2
					).put(
						"externalReferenceCode", ERC_VALUE_3
					))
			).toString(),
			StringBundler.concat(
				objectDefinition.getRESTContextPath(),
				"/by-external-reference-code/", ERC_VALUE_1),
			Http.Method.PUT);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"items",
				JSONUtil.putAll(
					JSONUtil.put(
						OBJECT_FIELD_NAME_1, objectFieldValue1
					).put(
						"externalReferenceCode", ERC_VALUE_2
					),
					JSONUtil.put(
						OBJECT_FIELD_NAME_1, objectFieldValue2
					).put(
						"externalReferenceCode", ERC_VALUE_3
					))
			).put(
				"totalCount", 2
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					objectDefinition.getRESTContextPath(), StringPool.SLASH,
					jsonObject.get("id"), StringPool.SLASH,
					objectRelationship.getName()),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1
			).put(
				objectRelationship.getName(),
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
				objectDefinition.getRESTContextPath(),
				"/by-external-reference-code/", ERC_VALUE_1),
			Http.Method.PUT);

		JSONAssert.assertEquals(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1
			).put(
				objectRelationship.getName(),
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
				getNestedFieldsEndpoint(
					objectDefinition, jsonObject.getLong("id"),
					objectRelationship),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);
	}

	@Test
	public void testPutByExternalReferenceCodeMultipleManyToManyRelationships()
		throws Exception {

		ObjectDefinition objectDefinition1 = getObjectDefinition1();

		ObjectRelationship objectRelationship1 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition1, getObjectDefinition2(),
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
		ObjectRelationship objectRelationship2 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				getObjectDefinition3(), objectDefinition1,
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
						new String[] {
							RandomTestUtil.randomString(
								UniqueStringRandomizerBumper.INSTANCE)
						},
						OBJECT_FIELD_NAME_2,
						new String[] {RandomTestUtil.randomString()})
				).put(
					objectRelationship2.getName(),
					createObjectEntriesJSONArray(
						new String[] {ERC_VALUE_3},
						RandomTestUtil.randomString(),
						new String[] {RandomTestUtil.randomString()})
				).toString(),
				objectDefinition1.getRESTContextPath(), Http.Method.POST));

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
					objectDefinition1.getRESTContextPath(),
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
					objectDefinition1.getRESTContextPath(),
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

		ObjectDefinition objectDefinition1 = getObjectDefinition1();
		ObjectDefinition objectDefinition2 = getObjectDefinition2();

		ObjectRelationship objectRelationship1 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition1, objectDefinition2,
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
		ObjectRelationship objectRelationship2 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition2, getObjectDefinition3(),
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
						"externalReferenceCode",
						RandomTestUtil.randomString(
							UniqueStringRandomizerBumper.INSTANCE)
					))
			).toString(),
			objectDefinition2.getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_2, NEW_OBJECT_FIELD_VALUE_2
				).toString(),
				StringBundler.concat(
					objectDefinition2.getRESTContextPath(),
					"/by-external-reference-code/", ERC_VALUE_2),
				Http.Method.PUT));

		String objectEntryId = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				objectDefinition1.getRESTContextPath(),
				"/by-external-reference-code/", ERC_VALUE_1),
			Http.Method.GET
		).getString(
			"id"
		);

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
					objectDefinition1.getRESTContextPath(), StringPool.SLASH,
					objectEntryId, StringPool.SLASH,
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
					objectDefinition2.getRESTContextPath(),
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
					objectDefinition1.getRESTContextPath(), StringPool.SLASH,
					objectEntryId, StringPool.SLASH,
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
					objectDefinition2.getRESTContextPath(), StringPool.SLASH,
					jsonObject.get("id"), StringPool.SLASH,
					objectRelationship2.getName()),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);
	}

	@Test
	public void testPutByExternalReferenceCodeWithNonexistentValueOneToManyRelationship()
		throws Exception {

		ObjectDefinition objectDefinition1 = getObjectDefinition1();

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				objectDefinition1, getObjectDefinition2(),
				TestPropsValues.getUserId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1
				).put(
					objectRelationship.getName(),
					createObjectEntriesJSONArray(
						new String[] {ERC_VALUE_2}, OBJECT_FIELD_NAME_2,
						new String[] {NEW_OBJECT_FIELD_VALUE_2})
				).toString(),
				StringBundler.concat(
					objectDefinition1.getRESTContextPath(),
					"/by-external-reference-code/", ERC_VALUE_1),
				Http.Method.PUT));

		JSONAssert.assertEquals(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, NEW_OBJECT_FIELD_VALUE_1
			).put(
				objectRelationship.getName(),
				JSONUtil.putAll(
					JSONUtil.put("externalReferenceCode", ERC_VALUE_2))
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					objectDefinition1.getRESTContextPath(),
					"/by-external-reference-code/", ERC_VALUE_1,
					"?nestedFields=", objectRelationship.getName()),
				Http.Method.GET
			).toString(),
			JSONCompareMode.LENIENT);
	}

	private ObjectRelationship _addObjectRelationship(long companyId)
		throws Exception {

		User user = UserTestUtil.getAdminUser(companyId);

		ObjectDefinition childObjectDefinition =
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
		ObjectDefinition parentObjectDefinition =
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
			parentObjectDefinition, childObjectDefinition, user.getUserId(),
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

		// Priority(ERC) > priority(invalid ID)

		ObjectDefinition objectDefinition = getObjectDefinition2();

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
				objectDefinition.getRESTContextPath(), Http.Method.POST
			).toString(),
			JSONCompareMode.LENIENT);

		// Priority(ID) > priority(ERC)

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
				objectDefinition.getRESTContextPath(), Http.Method.POST
			).toString(),
			JSONCompareMode.LENIENT);

		// Priority(relationshipName) > priority(ID) > priority(ERC)

		String objectRelationshipName = objectRelationship.getName();

		JSONAssert.assertEquals(
			JSONUtil.put(
				OBJECT_FIELD_NAME_2, String.valueOf(OBJECT_FIELD_VALUE_2)
			).put(
				objectFieldName, Long.valueOf(jsonObject1.getLong("id"))
			).put(
				objectRelationshipERCObjectFieldName,
				jsonObject1.getString("externalReferenceCode")
			).put(
				objectRelationshipName,
				JSONUtil.put(
					"externalReferenceCode",
					jsonObject1.getString("externalReferenceCode"))
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
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
				objectDefinition.getRESTContextPath(), Http.Method.POST
			).toString(),
			JSONCompareMode.LENIENT);

		JSONObject actualJSONObject = HTTPTestUtil.invokeToJSONObject(
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
			objectDefinition.getRESTContextPath(), Http.Method.POST);

		Assert.assertNotEquals(
			jsonObject1.getLong("id"),
			actualJSONObject.getLong(objectFieldName));
		Assert.assertNotEquals(
			jsonObject1.getString("externalReferenceCode"),
			actualJSONObject.getString(objectRelationshipERCObjectFieldName));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			// Invalid ERC

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
					objectDefinition.getRESTContextPath(), Http.Method.POST
				).toString(),
				JSONCompareMode.LENIENT);

			// Priority(invalid ID) > priority(invalid ERC)

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
					objectDefinition.getRESTContextPath(), Http.Method.POST
				).toString(),
				JSONCompareMode.LENIENT);
		}
	}

	private void _testPostCustomObjectEntryWithNonexistentRelatedObjectEntryId(
			ObjectDefinition parentObjectDefinition,
			ObjectRelationship objectRelationship)
		throws Exception {

		ObjectField objectField = objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());

		ObjectDefinition objectDefinition = getObjectDefinition2();

		JSONAssert.assertEquals(
			JSONUtil.put(
				"status", "NOT_FOUND"
			).put(
				"title",
				String.format(
					"The value for %s does not exist.", objectField.getName())
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_2, OBJECT_FIELD_VALUE_2
				).put(
					StringBundler.concat(
						"r_", objectRelationship.getName(), "_",
						parentObjectDefinition.getPKObjectFieldName()),
					RandomTestUtil.randomLong()
				).toString(),
				objectDefinition.getRESTContextPath(), Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);
	}

	private static final String _NEW_OBJECT_FIELD_VALUE_3 =
		RandomTestUtil.randomString();

}