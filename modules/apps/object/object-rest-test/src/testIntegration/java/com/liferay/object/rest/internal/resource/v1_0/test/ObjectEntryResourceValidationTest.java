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
import com.liferay.object.service.ObjectFieldLocalServiceUtil;
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
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.GroupUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import jakarta.ws.rs.BadRequestException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
	public void testGetObjectEntryWithObjectValidationRule() throws Exception {
		ObjectField integerObjectField = objectFieldLocalService.getObjectField(
			getObjectDefinition1().getObjectDefinitionId(),
			OBJECT_FIELD_NAME_INTEGER);

		ObjectField textObjectField = objectFieldLocalService.getObjectField(
			getObjectDefinition1().getObjectDefinitionId(),
			OBJECT_FIELD_NAME_TEXT);

		String error = RandomTestUtil.randomString();

		objectValidationRuleLocalService.addObjectValidationRule(
			StringPool.BLANK, TestPropsValues.getUserId(),
			getObjectDefinition1().getObjectDefinitionId(), true,
			ObjectValidationRuleConstants.ENGINE_TYPE_COMPOSITE_KEY,
			LocalizedMapUtil.getLocalizedMap(error),
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

		Integer randomInt = RandomTestUtil.randomInt();
		String randomString = RandomTestUtil.randomString();

		Assert.assertEquals(
			200,
			HTTPTestUtil.invokeToHttpCode(
				JSONUtil.put(
					OBJECT_FIELD_NAME_INTEGER, randomInt
				).put(
					OBJECT_FIELD_NAME_TEXT, randomString
				).toString(),
				getObjectDefinition1().getRESTContextPath(), Http.Method.POST));

		JSONAssert.assertEquals(
			JSONUtil.put(
				"detail", "[{\"errorMessage\":\"" + error + "\"}]"
			).put(
				"status", "BAD_REQUEST"
			).put(
				"type", "ObjectValidationRuleEngineException"
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					OBJECT_FIELD_NAME_INTEGER, randomInt
				).put(
					OBJECT_FIELD_NAME_TEXT, randomString
				).toString(),
				getObjectDefinition1().getRESTContextPath(), Http.Method.POST
			).toString(),
			JSONCompareMode.LENIENT);
	}

	@Test
	public void testPatchObjectEntryWithRequiredObjectFields()
		throws Exception {

		ObjectField objectField1 = objectFieldLocalService.getObjectField(
			getObjectDefinition1().getObjectDefinitionId(),
			OBJECT_FIELD_NAME_1);

		objectFieldLocalService.updateRequired(
			objectField1.getObjectFieldId(), true);

		ObjectField objectField2 = objectFieldLocalService.getObjectField(
			getObjectDefinition1().getObjectDefinitionId(),
			OBJECT_FIELD_NAME_TEXT);

		objectFieldLocalService.updateRequired(
			objectField2.getObjectFieldId(), true);

		String objectFieldValue1 = RandomTestUtil.randomString();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_1, objectFieldValue1
			).put(
				OBJECT_FIELD_NAME_TEXT, RandomTestUtil.randomString()
			).toString(),
			getObjectDefinition1().getRESTContextPath(), Http.Method.POST);

		String objectFieldValue2 = RandomTestUtil.randomString();

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_TEXT, objectFieldValue2
			).toString(),
			StringBundler.concat(
				getObjectDefinition1().getRESTContextPath(), StringPool.SLASH,
				jsonObject.get("id")),
			Http.Method.PATCH);

		Assert.assertEquals(
			objectFieldValue1, jsonObject.getString(OBJECT_FIELD_NAME_1));
		Assert.assertEquals(
			objectFieldValue2, jsonObject.getString(OBJECT_FIELD_NAME_TEXT));
	}

	@Test
	public void testPostScopeScopeKeyValidate() throws Exception {
		Group group = groupLocalService.fetchGroup(testGroupId);

		_testPostValidate(
			group.getExternalReferenceCode(), getSiteScopedObjectDefinition1(),
			objectFieldLocalService.getObjectField(
				getSiteScopedObjectDefinition1().getObjectDefinitionId(),
				OBJECT_FIELD_NAME_1));

		_testPostValidate(
			String.valueOf(group.getGroupId()),
			getSiteScopedObjectDefinition1(),
			objectFieldLocalService.getObjectField(
				getSiteScopedObjectDefinition1().getObjectDefinitionId(),
				OBJECT_FIELD_NAME_1));

		_testPostValidate(
			group.getGroupKey(), getSiteScopedObjectDefinition1(),
			objectFieldLocalService.getObjectField(
				getSiteScopedObjectDefinition1().getObjectDefinitionId(),
				OBJECT_FIELD_NAME_1));
	}

	@Test
	public void testPostValidate() throws Exception {
		_testPostValidate(
			null, getObjectDefinition1(),
			objectFieldLocalService.getObjectField(
				getObjectDefinition1().getObjectDefinitionId(),
				OBJECT_FIELD_NAME_TEXT));
	}

	private ValidationRequest _getValidationRequest(
		Map<String, Object> values,
		String... objectValidationRuleExternalReferenceCodes) {

		com.liferay.object.rest.dto.v1_0.ObjectEntry objectEntry =
			new com.liferay.object.rest.dto.v1_0.ObjectEntry();

		objectEntry.setProperties((values != null) ? values : new HashMap<>());

		String[] finalObjectValidationRuleExternalReferenceCodes =
			objectValidationRuleExternalReferenceCodes;

		return new ValidationRequest() {
			{
				setObjectValidationRuleExternalReferenceCodes(
					() -> finalObjectValidationRuleExternalReferenceCodes);
				setValues(() -> objectEntry);
			}
		};
	}

	private void _setUpPermissionThreadLocal(User user) {
		PrincipalThreadLocal.setName(user.getUserId());

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
	}

	private void _testPostValidate(
			String scopeKey, ObjectDefinition objectDefinition,
			ObjectField objectField1)
		throws Exception {

		String errorMessage1 = RandomTestUtil.randomString();

		ObjectValidationRule objectValidationRule1 =
			objectValidationRuleLocalService.addObjectValidationRule(
				StringUtil.randomString(), TestPropsValues.getUserId(),
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
				StringUtil.randomString(), TestPropsValues.getUserId(),
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

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		String originalName = PrincipalThreadLocal.getName();

		try {
			User user = addUser("test1", "test1");

			ObjectEntryResource objectEntryResource = getObjectEntryResource(
				objectDefinition, TestPropsValues.getUser());

			resourcePermissionLocalService.removeResourcePermission(
				TestPropsValues.getCompanyId(), ObjectEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(getObjectEntry5().getObjectEntryId()),
				user.getRoleIds()[0], ActionKeys.ADD_ENTRY);

			_setUpPermissionThreadLocal(user);

			String errorScopeKey = "";

			if (Validator.isNotNull(scopeKey)) {
				errorScopeKey = scopeKey;

				if (!Validator.isNumber(scopeKey)) {
					errorScopeKey = String.valueOf(
						GroupUtil.getGroupId(
							TestPropsValues.getCompanyId(), scopeKey,
							groupLocalService));
				}
			}

			AssertUtils.assertFailure(
				PrincipalException.MustHavePermission.class,
				StringBundler.concat(
					"User ", user.getUserId(),
					" must have ADD_OBJECT_ENTRY permission for ",
					objectDefinition.getResourceName(), " ", errorScopeKey),
				() -> _validate(
					scopeKey, objectEntryResource,
					_getValidationRequest(
						HashMapBuilder.<String, Object>put(
							objectField1.getName(), StringUtil.randomString()
						).build(),
						objectValidationRule1.getExternalReferenceCode())));

			_setUpPermissionThreadLocal(TestPropsValues.getUser());

			AssertUtils.assertFailure(
				BadRequestException.class, "No values found in body",
				() -> _validate(
					scopeKey, objectEntryResource, new ValidationRequest()));

			ValidationResponse validationResponse = _validate(
				scopeKey, objectEntryResource,
				_getValidationRequest(
					HashMapBuilder.<String, Object>put(
						objectField1.getName(), StringUtil.randomString()
					).build(),
					objectValidationRule1.getExternalReferenceCode()));

			ValidationError[] validationErrors =
				validationResponse.getValidationErrors();

			Assert.assertEquals(
				errorMessage1, validationErrors[0].getErrorMessage());

			validationResponse = _validate(
				scopeKey, objectEntryResource,
				_getValidationRequest(
					HashMapBuilder.<String, Object>put(
						objectField1.getName(), "foo"
					).build(),
					objectValidationRule1.getExternalReferenceCode()));

			validationErrors = validationResponse.getValidationErrors();

			Assert.assertTrue(ArrayUtil.isEmpty(validationErrors));

			validationResponse = _validate(
				scopeKey, objectEntryResource,
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
				scopeKey, objectEntryResource,
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
				ObjectFieldLocalServiceUtil.addCustomObjectField(
					StringUtil.randomString(), TestPropsValues.getUserId(), 0,
					objectDefinition.getObjectDefinitionId(),
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, null, true, false,
					null, LocalizedMapUtil.getLocalizedMap("Name Required"),
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
				scopeKey, objectEntryResource,
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
				scopeKey, objectEntryResource,
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
				scopeKey, objectEntryResource,
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
				scopeKey, objectEntryResource,
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
			String scopeKey, ObjectEntryResource objectEntryResource,
			ValidationRequest validationRequest)
		throws Exception {

		if (scopeKey != null) {
			return objectEntryResource.postScopeScopeKeyValidate(
				scopeKey, validationRequest);
		}

		return objectEntryResource.postValidate(validationRequest);
	}

}