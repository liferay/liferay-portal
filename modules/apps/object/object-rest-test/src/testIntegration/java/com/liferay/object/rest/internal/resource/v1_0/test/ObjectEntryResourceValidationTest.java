/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectValidationRuleConstants;
import com.liferay.object.constants.ObjectValidationRuleSettingConstants;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectValidationRule;
import com.liferay.object.rest.dto.v1_0.ValidationError;
import com.liferay.object.rest.dto.v1_0.ValidationRequest;
import com.liferay.object.rest.dto.v1_0.ValidationResponse;
import com.liferay.object.rest.resource.v1_0.ObjectEntryResource;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.validation.rule.setting.builder.ObjectValidationRuleSettingBuilder;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.GroupUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import jakarta.ws.rs.BadRequestException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

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
public class ObjectEntryResourceValidationTest
	extends BaseObjectEntryResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testPatchObjectEntryWithRequiredObjectFields()
		throws Exception {

		ObjectDefinition objectDefinition = getObjectDefinition1();

		ObjectField objectField1 = objectFieldLocalService.getObjectField(
			objectDefinition.getObjectDefinitionId(), OBJECT_FIELD_NAME_1);

		objectFieldLocalService.updateRequired(
			objectField1.getObjectFieldId(), true);

		ObjectField objectField2 = objectFieldLocalService.getObjectField(
			objectDefinition.getObjectDefinitionId(), OBJECT_FIELD_NAME_TEXT);

		objectFieldLocalService.updateRequired(
			objectField2.getObjectFieldId(), true);

		String objectFieldValue1 = RandomTestUtil.randomString();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, objectFieldValue1
			).put(
				OBJECT_FIELD_NAME_TEXT, RandomTestUtil.randomString()
			).toString(),
			objectDefinition.getRESTContextPath(), Http.Method.POST);

		String objectFieldValue2 = RandomTestUtil.randomString();

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_TEXT, objectFieldValue2
			).toString(),
			StringBundler.concat(
				objectDefinition.getRESTContextPath(), StringPool.SLASH,
				jsonObject.get("id")),
			Http.Method.PATCH);

		Assert.assertEquals(
			objectFieldValue1, jsonObject.getString(OBJECT_FIELD_NAME_1));
		Assert.assertEquals(
			objectFieldValue2, jsonObject.getString(OBJECT_FIELD_NAME_TEXT));
	}

	@Test
	public void testPostObjectEntryWithCompositeKeyObjectValidationRule()
		throws Exception {

		String errorMessage = RandomTestUtil.randomString();

		ObjectDefinition objectDefinition = getObjectDefinition1();

		ObjectField integerObjectField = objectFieldLocalService.getObjectField(
			objectDefinition.getObjectDefinitionId(),
			OBJECT_FIELD_NAME_INTEGER);

		ObjectField textObjectField = objectFieldLocalService.getObjectField(
			objectDefinition.getObjectDefinitionId(), OBJECT_FIELD_NAME_TEXT);

		objectValidationRuleLocalService.addObjectValidationRule(
			StringPool.BLANK, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), true,
			ObjectValidationRuleConstants.ENGINE_TYPE_COMPOSITE_KEY,
			LocalizedMapUtil.getLocalizedMap(errorMessage),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
			StringPool.BLANK, false,
			Arrays.asList(
				new ObjectValidationRuleSettingBuilder(
				).name(
					ObjectValidationRuleSettingConstants.
						NAME_COMPOSITE_KEY_OBJECT_FIELD_ID
				).value(
					String.valueOf(integerObjectField.getObjectFieldId())
				).build(),
				new ObjectValidationRuleSettingBuilder(
				).name(
					ObjectValidationRuleSettingConstants.
						NAME_COMPOSITE_KEY_OBJECT_FIELD_ID
				).value(
					String.valueOf(textObjectField.getObjectFieldId())
				).build()));

		String objectEntryJSON = JSONUtil.put(
			OBJECT_FIELD_NAME_INTEGER, RandomTestUtil.randomInt()
		).put(
			OBJECT_FIELD_NAME_TEXT, RandomTestUtil.randomString()
		).toString();

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				objectEntryJSON, objectDefinition.getRESTContextPath(),
				Http.Method.POST));

		JSONAssert.assertEquals(
			JSONUtil.put(
				"detail", "[{\"errorMessage\":\"" + errorMessage + "\"}]"
			).put(
				"status", "BAD_REQUEST"
			).put(
				"type", "ObjectValidationRuleEngineException"
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				objectEntryJSON, objectDefinition.getRESTContextPath(),
				Http.Method.POST
			).toString(),
			JSONCompareMode.LENIENT);
	}

	@Test
	public void testPostScopeScopeKeyValidate() throws Exception {
		ObjectDefinition siteScopedObjectDefinition =
			getSiteScopedObjectDefinition1();

		ObjectField objectField = objectFieldLocalService.getObjectField(
			siteScopedObjectDefinition.getObjectDefinitionId(),
			OBJECT_FIELD_NAME_1);

		Group testGroup = groupLocalService.fetchGroup(testGroupId);

		_testPostValidate(
			siteScopedObjectDefinition, objectField,
			String.valueOf(testGroupId));
		_testPostValidate(
			siteScopedObjectDefinition, objectField,
			testGroup.getExternalReferenceCode());
		_testPostValidate(
			siteScopedObjectDefinition, objectField, testGroup.getGroupKey());
	}

	@Test
	public void testPostValidate() throws Exception {
		ObjectDefinition objectDefinition = getObjectDefinition1();

		_testPostValidate(
			objectDefinition,
			objectFieldLocalService.getObjectField(
				objectDefinition.getObjectDefinitionId(),
				OBJECT_FIELD_NAME_TEXT),
			null);
	}

	private ValidationRequest _getValidationRequest(
		Map<String, Object> properties,
		String... objectValidationRuleExternalReferenceCodes) {

		ValidationRequest validationRequest = new ValidationRequest();

		com.liferay.object.rest.dto.v1_0.ObjectEntry objectEntry =
			new com.liferay.object.rest.dto.v1_0.ObjectEntry();

		objectEntry.setProperties(properties);

		validationRequest.setObjectValidationRuleExternalReferenceCodes(
			objectValidationRuleExternalReferenceCodes);
		validationRequest.setValues(objectEntry);

		return validationRequest;
	}

	private void _testPostValidate(
			ObjectDefinition objectDefinition, ObjectField objectField1,
			String scopeKey)
		throws Exception {

		String errorMessage1 = RandomTestUtil.randomString();

		ObjectValidationRule objectValidationRule1 =
			objectValidationRuleLocalService.addObjectValidationRule(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId(), true,
				ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
				LocalizedMapUtil.getLocalizedMap(errorMessage1),
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
				objectField1.getName() + " == \"foo\"", false,
				Collections.emptyList());

		String errorMessage2 = RandomTestUtil.randomString();

		ObjectValidationRule objectValidationRule2 =
			objectValidationRuleLocalService.addObjectValidationRule(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId(), true,
				ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
				LocalizedMapUtil.getLocalizedMap(errorMessage2),
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectValidationRuleConstants.OUTPUT_TYPE_PARTIAL_VALIDATION,
				"NOT(isInteger(" + objectField1.getName() + "))", false,
				Arrays.asList(
					new ObjectValidationRuleSettingBuilder(
					).name(
						ObjectValidationRuleSettingConstants.
							NAME_OUTPUT_OBJECT_FIELD_ID
					).value(
						String.valueOf(objectField1.getObjectFieldId())
					).build()));

		String originalName = PrincipalThreadLocal.getName();
		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			User user = addUser(
				RandomTestUtil.randomString(
					UniqueStringRandomizerBumper.INSTANCE),
				RandomTestUtil.randomString());

			ObjectEntryResource objectEntryResource = getObjectEntryResource(
				objectDefinition, TestPropsValues.getUser());

			ObjectEntry objectEntry = getLocalizedObjectEntry();

			resourcePermissionLocalService.removeResourcePermission(
				TestPropsValues.getCompanyId(), ObjectEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntry.getObjectEntryId()),
				user.getRoleIds()[0], ActionKeys.ADD_ENTRY);

			UserTestUtil.setUser(user);

			String errorScopeKey = StringPool.BLANK;

			if (scopeKey != null) {
				errorScopeKey = String.valueOf(
					GroupUtil.getGroupId(
						TestPropsValues.getCompanyId(), scopeKey,
						groupLocalService));
			}

			AssertUtils.assertFailure(
				PrincipalException.MustHavePermission.class,
				StringBundler.concat(
					"User ", user.getUserId(),
					" must have ADD_OBJECT_ENTRY permission for ",
					objectDefinition.getResourceName(), " ", errorScopeKey),
				() -> _validate(
					objectEntryResource, scopeKey,
					_getValidationRequest(
						HashMapBuilder.<String, Object>put(
							objectField1.getName(),
							RandomTestUtil.randomString()
						).build(),
						objectValidationRule1.getExternalReferenceCode())));

			UserTestUtil.setUser(TestPropsValues.getUser());

			AssertUtils.assertFailure(
				BadRequestException.class, "No values found in body",
				() -> _validate(
					objectEntryResource, scopeKey, new ValidationRequest()));

			ValidationResponse validationResponse = _validate(
				objectEntryResource, scopeKey,
				_getValidationRequest(
					HashMapBuilder.<String, Object>put(
						objectField1.getName(), RandomTestUtil.randomString()
					).build(),
					objectValidationRule1.getExternalReferenceCode()));

			ValidationError[] validationErrors =
				validationResponse.getValidationErrors();

			Assert.assertEquals(
				errorMessage1, validationErrors[0].getErrorMessage());

			validationResponse = _validate(
				objectEntryResource, scopeKey,
				_getValidationRequest(
					HashMapBuilder.<String, Object>put(
						objectField1.getName(), "foo"
					).build(),
					objectValidationRule1.getExternalReferenceCode()));

			validationErrors = validationResponse.getValidationErrors();

			Assert.assertTrue(ArrayUtil.isEmpty(validationErrors));

			validationResponse = _validate(
				objectEntryResource, scopeKey,
				_getValidationRequest(
					HashMapBuilder.<String, Object>put(
						objectField1.getName(), RandomTestUtil.randomInt()
					).build()));

			validationErrors = validationResponse.getValidationErrors();

			Assert.assertEquals(
				errorMessage1, validationErrors[0].getErrorMessage());
			Assert.assertEquals(
				errorMessage2, validationErrors[1].getErrorMessage());
			Assert.assertEquals(
				objectField1.getName(),
				validationErrors[1].getObjectFieldName());

			objectValidationRule2.setActive(false);

			objectValidationRuleLocalService.updateObjectValidationRule(
				objectValidationRule2);

			validationResponse = _validate(
				objectEntryResource, scopeKey,
				_getValidationRequest(
					HashMapBuilder.<String, Object>put(
						objectField1.getName(), RandomTestUtil.randomInt()
					).build()));

			validationErrors = validationResponse.getValidationErrors();

			Assert.assertEquals(
				Arrays.toString(validationErrors), 1, validationErrors.length);
			Assert.assertEquals(
				errorMessage1, validationErrors[0].getErrorMessage());

			ObjectField objectField2 =
				objectFieldLocalService.addCustomObjectField(
					RandomTestUtil.randomString(), TestPropsValues.getUserId(),
					0, objectDefinition.getObjectDefinitionId(),
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, null, true, false,
					null,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					false, "nameRequired", null, null, true, false,
					Arrays.asList(
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_MAX_LENGTH
						).value(
							"9"
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_SHOW_COUNTER
						).value(
							"true"
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_UNIQUE_VALUES
						).value(
							"true"
						).build()));

			setObjectDefinition(
				objectDefinitionLocalService.getObjectDefinition(
					objectDefinition.getObjectDefinitionId()),
				objectEntryResource);

			validationResponse = _validate(
				objectEntryResource, scopeKey,
				_getValidationRequest(Collections.emptyMap()));

			validationErrors = validationResponse.getValidationErrors();

			Assert.assertEquals(
				errorMessage1, validationErrors[0].getErrorMessage());
			Assert.assertEquals(
				"No value was provided for required object field " +
					"\"nameRequired\"",
				validationErrors[1].getErrorMessage());

			objectValidationRuleLocalService.deleteObjectValidationRules(
				objectDefinition.getObjectDefinitionId());

			validationResponse = _validate(
				objectEntryResource, scopeKey,
				_getValidationRequest(
					HashMapBuilder.<String, Object>put(
						objectField2.getName(), RandomTestUtil.randomString(10)
					).build()));

			validationErrors = validationResponse.getValidationErrors();

			Assert.assertEquals(
				"Object entry value exceeds the maximum length of 9 " +
					"characters for object field \"nameRequired\"",
				validationErrors[0].getErrorMessage());

			ObjectEntryTestUtil.addObjectEntry(
				objectDefinition, objectField2.getName(), "Peter");

			validationResponse = _validate(
				objectEntryResource, scopeKey,
				_getValidationRequest(
					HashMapBuilder.<String, Object>put(
						objectField2.getName(), "Peter"
					).build()));

			validationErrors = validationResponse.getValidationErrors();

			Assert.assertEquals(
				String.format(
					"Unique value constraint violation for %s.%s with value %s",
					objectField2.getDBTableName(),
					objectField2.getDBColumnName(), "Peter"),
				validationErrors[0].getErrorMessage());

			objectFieldLocalService.deleteObjectField(
				objectField2.getObjectFieldId());

			setObjectDefinition(
				objectDefinitionLocalService.getObjectDefinition(
					objectDefinition.getObjectDefinitionId()),
				objectEntryResource);

			FileEntry fileEntry = TempFileEntryUtil.addTempFileEntry(
				testGroupId, TestPropsValues.getUserId(),
				objectDefinition.getPortletId(),
				TempFileEntryUtil.getTempFileName("foo.pdf"),
				FileUtil.createTempFile(
					DLTestUtil.randomTextFileBytes(
						(MAX_FILE_SIZE_VALUE * 1024 * 1024) + 1)),
				ContentTypes.TEXT_PLAIN);

			validationResponse = _validate(
				objectEntryResource, scopeKey,
				_getValidationRequest(
					HashMapBuilder.<String, Object>put(
						OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE,
						fileEntry.getFileEntryId()
					).build()));

			validationErrors = validationResponse.getValidationErrors();

			Assert.assertEquals(
				StringBundler.concat(
					"The file extension \"pdf\" is invalid for object field \"",
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE, "\""),
				validationErrors[0].getErrorMessage());
			Assert.assertEquals(
				StringBundler.concat(
					"File exceeds the maximum permitted size of 1 MB for ",
					"object field \"",
					OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA_SOURCE, "\""),
				validationErrors[1].getErrorMessage());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
			PrincipalThreadLocal.setName(originalName);
		}
	}

	private ValidationResponse _validate(
			ObjectEntryResource objectEntryResource, String scopeKey,
			ValidationRequest validationRequest)
		throws Exception {

		if (scopeKey != null) {
			return objectEntryResource.postScopeScopeKeyValidate(
				scopeKey, validationRequest);
		}

		return objectEntryResource.postValidate(validationRequest);
	}

}