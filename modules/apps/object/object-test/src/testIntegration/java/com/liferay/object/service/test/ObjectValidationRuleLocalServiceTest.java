/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectValidationRuleConstants;
import com.liferay.object.constants.ObjectValidationRuleSettingConstants;
import com.liferay.object.exception.NoSuchObjectValidationRuleException;
import com.liferay.object.exception.ObjectValidationRuleEngineException;
import com.liferay.object.exception.ObjectValidationRuleNameException;
import com.liferay.object.exception.ObjectValidationRuleOutputTypeException;
import com.liferay.object.exception.ObjectValidationRuleScriptException;
import com.liferay.object.exception.ObjectValidationRuleSettingNameException;
import com.liferay.object.exception.ObjectValidationRuleSettingValueException;
import com.liferay.object.exception.ObjectValidationRuleSystemException;
import com.liferay.object.field.builder.DateObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectValidationRule;
import com.liferay.object.model.ObjectValidationRuleSetting;
import com.liferay.object.scripting.executor.ObjectScriptingExecutor;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectValidationRuleLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.validation.rule.ObjectValidationRuleEngineRegistry;
import com.liferay.object.validation.rule.ObjectValidationRuleResult;
import com.liferay.object.validation.rule.setting.builder.ObjectValidationRuleSettingBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.security.script.management.test.rule.ScriptManagementConfigurationTestRule;
import com.liferay.portal.security.script.management.test.util.ScriptManagementConfigurationTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Closeable;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Marcela Cunha
 */
@RunWith(Arquillian.class)
public class ObjectValidationRuleLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			ScriptManagementConfigurationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_modifiableSystemObjectDefinition =
			ObjectDefinitionTestUtil.addModifiableSystemObjectDefinition(
				TestPropsValues.getUserId(), null, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test", null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionConstants.SCOPE_SITE, null, 1,
				Collections.emptyList());

		_objectDefinition = ObjectDefinitionTestUtil.addCustomObjectDefinition(
			false,
			Arrays.asList(
				new DateObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"dateObjectField"
				).objectFieldSettings(
					Collections.emptyList()
				).build(),
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"textObjectField"
				).objectFieldSettings(
					Collections.emptyList()
				).build()));
	}

	@After
	public void tearDown() throws Exception {
		_objectDefinitionLocalService.deleteObjectDefinition(
			_modifiableSystemObjectDefinition);
		_objectDefinitionLocalService.deleteObjectDefinition(_objectDefinition);
	}

	@Test
	public void testAddObjectValidationRule() throws Exception {
		AssertUtils.assertFailure(
			ObjectValidationRuleEngineException.MustNotBeNull.class,
			"Engine is null",
			() -> _addObjectValidationRule(
				StringPool.BLANK, _VALID_DDM_SCRIPT));

		try (Closeable closeable =
				ScriptManagementConfigurationTestUtil.saveWithCloseable(
					false)) {

			AssertUtils.assertFailure(
				ObjectValidationRuleEngineException.NotAllowedEngine.class,
				"Engine \"groovy\" is not allowed",
				() -> _addObjectValidationRule(
					ObjectValidationRuleConstants.ENGINE_TYPE_GROOVY,
					"invalidFields = false;"));
		}

		AssertUtils.assertFailure(
			ObjectValidationRuleNameException.class,
			"Name is null for locale " + LocaleUtil.US.getDisplayName(),
			() -> _addObjectValidationRule(
				StringPool.BLANK, ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				LocalizedMapUtil.getLocalizedMap(StringPool.BLANK),
				_VALID_DDM_SCRIPT));
		AssertUtils.assertFailure(
			ObjectValidationRuleNameException.class,
			"Name is null for locale " + LocaleUtil.US.getDisplayName(),
			() -> _addObjectValidationRule(
				StringPool.BLANK, ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				null, _VALID_DDM_SCRIPT));

		Map<Locale, String> errorLabelMap = LocalizedMapUtil.getLocalizedMap(
			RandomTestUtil.randomString());
		Map<Locale, String> nameLabelMap = LocalizedMapUtil.getLocalizedMap(
			RandomTestUtil.randomString());

		String outputType = RandomTestUtil.randomString();

		AssertUtils.assertFailure(
			ObjectValidationRuleOutputTypeException.class,
			"Invalid output type " + outputType,
			() -> _addObjectValidationRule(
				StringPool.BLANK, ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
				errorLabelMap, nameLabelMap, outputType, _VALID_DDM_SCRIPT,
				false, Collections.emptyList()));

		AssertUtils.assertFailure(
			ObjectValidationRuleScriptException.class, "The script is required",
			() -> _addObjectValidationRule(
				ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
				StringPool.BLANK));
		AssertUtils.assertFailure(
			ObjectValidationRuleScriptException.class,
			"The script syntax is invalid",
			() -> _addObjectValidationRule(
				ObjectValidationRuleConstants.ENGINE_TYPE_GROOVY,
				"import;\ninvalidFields = false;"));

		AssertUtils.assertFailure(
			ObjectValidationRuleSettingNameException.MissingRequiredName.class,
			String.format(
				"The object validation rule setting \"%s\" is required",
				ObjectValidationRuleSettingConstants.
					NAME_OUTPUT_OBJECT_FIELD_ID),
			() -> _addObjectValidationRule(
				StringPool.BLANK, ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
				errorLabelMap, nameLabelMap,
				ObjectValidationRuleConstants.OUTPUT_TYPE_PARTIAL_VALIDATION,
				_VALID_DDM_SCRIPT, false, Collections.emptyList()));
		AssertUtils.assertFailure(
			ObjectValidationRuleSettingNameException.NotAllowedName.class,
			String.format(
				"The object validation rule setting \"%s\" is not allowed",
				ObjectValidationRuleSettingConstants.
					NAME_ALLOW_ACTIVE_STATUS_UPDATE),
			() -> _addObjectValidationRule(
				StringPool.BLANK, ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
				errorLabelMap, nameLabelMap,
				ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
				_VALID_DDM_SCRIPT, false,
				Collections.singletonList(
					new ObjectValidationRuleSettingBuilder(
					).name(
						ObjectValidationRuleSettingConstants.
							NAME_ALLOW_ACTIVE_STATUS_UPDATE
					).value(
						"true"
					).build())));
		AssertUtils.assertFailure(
			ObjectValidationRuleSettingNameException.NotAllowedName.class,
			String.format(
				"The object validation rule setting \"%s\" is not allowed",
				ObjectValidationRuleSettingConstants.
					NAME_OUTPUT_OBJECT_FIELD_ID),
			() -> _addObjectValidationRule(
				StringPool.BLANK, ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
				errorLabelMap, nameLabelMap,
				ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
				_VALID_DDM_SCRIPT, false,
				Collections.singletonList(
					new ObjectValidationRuleSettingBuilder(
					).name(
						ObjectValidationRuleSettingConstants.
							NAME_OUTPUT_OBJECT_FIELD_ID
					).value(
						RandomTestUtil.randomString()
					).build())));

		List<ObjectValidationRuleSetting> objectValidationRuleSettings =
			new ArrayList<>();

		for (int i = 0; i < 6; i++) {
			objectValidationRuleSettings.add(
				new ObjectValidationRuleSettingBuilder(
				).name(
					ObjectValidationRuleSettingConstants.
						NAME_COMPOSITE_KEY_OBJECT_FIELD_ID
				).value(
					() -> {
						ObjectField objectField =
							ObjectFieldUtil.addCustomObjectField(
								new TextObjectFieldBuilder(
								).userId(
									TestPropsValues.getUserId()
								).labelMap(
									LocalizedMapUtil.getLocalizedMap(
										RandomTestUtil.randomString())
								).name(
									"a" + RandomTestUtil.randomString()
								).objectDefinitionId(
									_objectDefinition.getObjectDefinitionId()
								).build());

						return String.valueOf(objectField.getObjectFieldId());
					}
				).build());
		}

		AssertUtils.assertFailure(
			ObjectValidationRuleSettingValueException.
				CompositeKeyMustHaveMaxObjectFields.class,
			"Add a maximum of five object fields to create unique composite " +
				"keys",
			() -> _addObjectValidationRule(
				StringPool.BLANK,
				ObjectValidationRuleConstants.ENGINE_TYPE_COMPOSITE_KEY,
				errorLabelMap, nameLabelMap,
				ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
				StringPool.BLANK, false, objectValidationRuleSettings));

		ObjectField textObjectField = _objectFieldLocalService.fetchObjectField(
			_objectDefinition.getObjectDefinitionId(), "textObjectField");

		AssertUtils.assertFailure(
			ObjectValidationRuleSettingValueException.
				CompositeKeyMustHaveMinObjectFields.class,
			"Add a minimum of two object fields to create unique composite " +
				"keys",
			() -> _addObjectValidationRule(
				StringPool.BLANK,
				ObjectValidationRuleConstants.ENGINE_TYPE_COMPOSITE_KEY,
				errorLabelMap, nameLabelMap,
				ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
				StringPool.BLANK, false,
				Collections.singletonList(
					new ObjectValidationRuleSettingBuilder(
					).name(
						ObjectValidationRuleSettingConstants.
							NAME_COMPOSITE_KEY_OBJECT_FIELD_ID
					).value(
						String.valueOf(textObjectField.getObjectFieldId())
					).build())));

		ObjectField dateObjectField = _objectFieldLocalService.fetchObjectField(
			_objectDefinition.getObjectDefinitionId(), "dateObjectField");

		AssertUtils.assertFailure(
			ObjectValidationRuleSettingValueException.InvalidValue.class,
			String.format(
				"The value \"%s\" of the object validation rule setting " +
					"\"%s\" is invalid",
				dateObjectField.getObjectFieldId(),
				ObjectValidationRuleSettingConstants.
					NAME_COMPOSITE_KEY_OBJECT_FIELD_ID),
			() -> _addObjectValidationRule(
				StringPool.BLANK,
				ObjectValidationRuleConstants.ENGINE_TYPE_COMPOSITE_KEY,
				errorLabelMap, nameLabelMap,
				ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
				StringPool.BLANK, false,
				Arrays.asList(
					new ObjectValidationRuleSettingBuilder(
					).name(
						ObjectValidationRuleSettingConstants.
							NAME_COMPOSITE_KEY_OBJECT_FIELD_ID
					).value(
						String.valueOf(dateObjectField.getObjectFieldId())
					).build(),
					new ObjectValidationRuleSettingBuilder(
					).name(
						ObjectValidationRuleSettingConstants.
							NAME_COMPOSITE_KEY_OBJECT_FIELD_ID
					).value(
						String.valueOf(textObjectField.getObjectFieldId())
					).build())));

		String objectValidationRuleSettingValue = RandomTestUtil.randomString();

		AssertUtils.assertFailure(
			ObjectValidationRuleSettingValueException.InvalidValue.class,
			String.format(
				"The value \"%s\" of the object validation rule setting " +
					"\"%s\" is invalid",
				objectValidationRuleSettingValue,
				ObjectValidationRuleSettingConstants.
					NAME_OUTPUT_OBJECT_FIELD_ID),
			() -> _addObjectValidationRule(
				StringPool.BLANK, ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
				errorLabelMap, nameLabelMap,
				ObjectValidationRuleConstants.OUTPUT_TYPE_PARTIAL_VALIDATION,
				_VALID_DDM_SCRIPT, false,
				Collections.singletonList(
					new ObjectValidationRuleSettingBuilder(
					).name(
						ObjectValidationRuleSettingConstants.
							NAME_OUTPUT_OBJECT_FIELD_ID
					).value(
						objectValidationRuleSettingValue
					).build())));

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId());

		_objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"textObjectField", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		AssertUtils.assertFailure(
			ObjectValidationRuleSettingValueException.InvalidValue.class,
			String.format(
				"The value \"%s\" of the object validation rule setting " +
					"\"%s\" is invalid",
				textObjectField.getObjectFieldId(),
				ObjectValidationRuleSettingConstants.
					NAME_COMPOSITE_KEY_OBJECT_FIELD_ID),
			() -> _addObjectValidationRule(
				StringPool.BLANK,
				ObjectValidationRuleConstants.ENGINE_TYPE_COMPOSITE_KEY,
				errorLabelMap, nameLabelMap,
				ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
				StringPool.BLANK, false,
				Arrays.asList(
					new ObjectValidationRuleSettingBuilder(
					).name(
						ObjectValidationRuleSettingConstants.
							NAME_COMPOSITE_KEY_OBJECT_FIELD_ID
					).value(
						String.valueOf(textObjectField.getObjectFieldId())
					).build(),
					new ObjectValidationRuleSettingBuilder(
					).name(
						ObjectValidationRuleSettingConstants.
							NAME_COMPOSITE_KEY_OBJECT_FIELD_ID
					).value(
						() -> {
							ObjectField objectField =
								ObjectFieldUtil.addCustomObjectField(
									new TextObjectFieldBuilder(
									).userId(
										TestPropsValues.getUserId()
									).labelMap(
										LocalizedMapUtil.getLocalizedMap(
											RandomTestUtil.randomString())
									).name(
										"a" + RandomTestUtil.randomString()
									).objectDefinitionId(
										_objectDefinition.
											getObjectDefinitionId()
									).build());

							return String.valueOf(
								objectField.getObjectFieldId());
						}
					).build())));

		AssertUtils.assertFailure(
			ObjectValidationRuleSystemException.class, false,
			"Only allowed bundles can add system object validation rules",
			() -> _addObjectValidationRule(
				StringPool.BLANK, ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
				_VALID_DDM_SCRIPT, true, Collections.emptyList()));

		String externalReferenceCode = RandomTestUtil.randomString();

		ObjectValidationRule objectValidationRule = _addObjectValidationRule(
			externalReferenceCode,
			ObjectValidationRuleConstants.ENGINE_TYPE_DDM, errorLabelMap,
			nameLabelMap, _VALID_DDM_SCRIPT);

		_assertObjectValidationRule(
			true, null, ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
			errorLabelMap, externalReferenceCode, nameLabelMap, null,
			ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
			_VALID_DDM_SCRIPT, objectValidationRule);

		_objectValidationRuleLocalService.deleteObjectValidationRule(
			objectValidationRule.getObjectValidationRuleId());

		externalReferenceCode = RandomTestUtil.randomString();

		String script =
			"import com.liferay.commerce.service.CommerceOrderLocalService;\n" +
				"invalidFields = false;";

		_assertObjectValidationRule(
			true, null, ObjectValidationRuleConstants.ENGINE_TYPE_GROOVY,
			errorLabelMap, externalReferenceCode, nameLabelMap, null,
			ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION, script,
			_addObjectValidationRule(
				externalReferenceCode,
				ObjectValidationRuleConstants.ENGINE_TYPE_GROOVY, errorLabelMap,
				nameLabelMap, script));

		ObjectScriptingExecutor originalObjectScriptingExecutor =
			(ObjectScriptingExecutor)_getAndSetFieldValue(
				ObjectScriptingExecutor.class, "_objectScriptingExecutor",
				ObjectValidationRuleConstants.ENGINE_TYPE_GROOVY);

		try {
			Assert.assertEquals(0, _argumentsList.size());

			_objectEntryLocalService.addObjectEntry(
				0, TestPropsValues.getUserId(),
				_objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"textObjectField", RandomTestUtil.randomString()
				).build(),
				ServiceContextTestUtil.getServiceContext());

			Assert.assertEquals(1, _argumentsList.size());

			Object[] arguments = _argumentsList.poll();

			Assert.assertEquals(
				TestPropsValues.getUserId(),
				MapUtil.getLong(
					(Map<String, Object>)arguments[0], "currentUserId"));
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_objectValidationRuleEngineRegistry.
					getObjectValidationRuleEngine(
						0, ObjectValidationRuleConstants.ENGINE_TYPE_GROOVY),
				"_objectScriptingExecutor", originalObjectScriptingExecutor);
		}

		externalReferenceCode = RandomTestUtil.randomString();

		String engine = RandomTestUtil.randomString();

		_assertObjectValidationRule(
			true, null, engine, errorLabelMap, externalReferenceCode,
			nameLabelMap, null,
			ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
			StringPool.BLANK,
			_addObjectValidationRule(
				externalReferenceCode, engine, errorLabelMap, nameLabelMap,
				StringPool.BLANK));

		externalReferenceCode = RandomTestUtil.randomString();

		objectValidationRule = _addObjectValidationRule(
			externalReferenceCode,
			ObjectValidationRuleConstants.ENGINE_TYPE_DDM, errorLabelMap,
			nameLabelMap,
			ObjectValidationRuleConstants.OUTPUT_TYPE_PARTIAL_VALIDATION,
			_VALID_DDM_SCRIPT, false,
			Collections.singletonList(
				new ObjectValidationRuleSettingBuilder(
				).name(
					ObjectValidationRuleSettingConstants.
						NAME_OUTPUT_OBJECT_FIELD_ID
				).value(
					String.valueOf(textObjectField.getObjectFieldId())
				).build()));

		_assertObjectValidationRule(
			true, null, ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
			errorLabelMap, externalReferenceCode, nameLabelMap,
			String.valueOf(textObjectField.getObjectFieldId()),
			ObjectValidationRuleConstants.OUTPUT_TYPE_PARTIAL_VALIDATION,
			_VALID_DDM_SCRIPT, objectValidationRule);

		_objectFieldLocalService.deleteObjectField(
			textObjectField.getObjectFieldId());

		objectValidationRule =
			_objectValidationRuleLocalService.getObjectValidationRule(
				objectValidationRule.getObjectValidationRuleId());

		Assert.assertEquals(
			ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
			objectValidationRule.getOutputType());
	}

	@Test
	public void testAddSystemObjectValidationRule() {
		Map<Locale, String> errorLabelMap = LocalizedMapUtil.getLocalizedMap(
			RandomTestUtil.randomString());
		Map<Locale, String> nameLabelMap = LocalizedMapUtil.getLocalizedMap(
			RandomTestUtil.randomString());

		AssertUtils.assertFailure(
			ObjectValidationRuleSettingNameException.NotAllowedName.class,
			String.format(
				"The object validation rule setting \"%s\" is not allowed",
				ObjectValidationRuleSettingConstants.
					NAME_ALLOW_ACTIVE_STATUS_UPDATE),
			() -> _addObjectValidationRule(
				StringPool.BLANK,
				_modifiableSystemObjectDefinition.getObjectDefinitionId(),
				ObjectValidationRuleConstants.ENGINE_TYPE_DDM, errorLabelMap,
				nameLabelMap,
				ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
				_VALID_DDM_SCRIPT, false,
				Collections.singletonList(
					new ObjectValidationRuleSettingBuilder(
					).name(
						ObjectValidationRuleSettingConstants.
							NAME_ALLOW_ACTIVE_STATUS_UPDATE
					).value(
						"true"
					).build())));

		String objectValidationRuleSettingValue = RandomTestUtil.randomString();

		AssertUtils.assertFailure(
			ObjectValidationRuleSettingValueException.InvalidValue.class,
			String.format(
				"The value \"%s\" of the object validation rule setting " +
					"\"%s\" is invalid",
				objectValidationRuleSettingValue,
				ObjectValidationRuleSettingConstants.
					NAME_ALLOW_ACTIVE_STATUS_UPDATE),
			() -> _addObjectValidationRule(
				StringPool.BLANK,
				_modifiableSystemObjectDefinition.getObjectDefinitionId(),
				ObjectValidationRuleConstants.ENGINE_TYPE_DDM, errorLabelMap,
				nameLabelMap,
				ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
				_VALID_DDM_SCRIPT, true,
				Collections.singletonList(
					new ObjectValidationRuleSettingBuilder(
					).name(
						ObjectValidationRuleSettingConstants.
							NAME_ALLOW_ACTIVE_STATUS_UPDATE
					).value(
						objectValidationRuleSettingValue
					).build())));
	}

	@Test
	public void testDeleteObjectValidationRule() throws Exception {
		ObjectValidationRule objectValidationRule = _addObjectValidationRule(
			ObjectValidationRuleConstants.ENGINE_TYPE_DDM, _VALID_DDM_SCRIPT);

		_testDeleteObjectValidationRule(
			objectValidationRule.getObjectValidationRuleId());

		ObjectField textObjectField1 =
			_objectFieldLocalService.fetchObjectField(
				_objectDefinition.getObjectDefinitionId(), "textObjectField");
		ObjectField textObjectField2 = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"a" + RandomTestUtil.randomString()
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).build());

		objectValidationRule = _addObjectValidationRule(
			StringPool.BLANK,
			ObjectValidationRuleConstants.ENGINE_TYPE_COMPOSITE_KEY,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
			StringPool.BLANK, false,
			Arrays.asList(
				new ObjectValidationRuleSettingBuilder(
				).name(
					ObjectValidationRuleSettingConstants.
						NAME_COMPOSITE_KEY_OBJECT_FIELD_ID
				).value(
					String.valueOf(textObjectField1.getObjectFieldId())
				).build(),
				new ObjectValidationRuleSettingBuilder(
				).name(
					ObjectValidationRuleSettingConstants.
						NAME_COMPOSITE_KEY_OBJECT_FIELD_ID
				).value(
					String.valueOf(textObjectField2.getObjectFieldId())
				).build()));

		_testDeleteObjectValidationRule(
			objectValidationRule.getObjectValidationRuleId());

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId());

		objectValidationRule = _addObjectValidationRule(
			StringPool.BLANK,
			ObjectValidationRuleConstants.ENGINE_TYPE_COMPOSITE_KEY,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
			StringPool.BLANK, false,
			Arrays.asList(
				new ObjectValidationRuleSettingBuilder(
				).name(
					ObjectValidationRuleSettingConstants.
						NAME_COMPOSITE_KEY_OBJECT_FIELD_ID
				).value(
					String.valueOf(textObjectField1.getObjectFieldId())
				).build(),
				new ObjectValidationRuleSettingBuilder(
				).name(
					ObjectValidationRuleSettingConstants.
						NAME_COMPOSITE_KEY_OBJECT_FIELD_ID
				).value(
					String.valueOf(textObjectField2.getObjectFieldId())
				).build()));

		_testDeleteObjectValidationRule(
			objectValidationRule.getObjectValidationRuleId());
	}

	@Test
	public void testDeleteSystemObjectValidationRule() throws Exception {
		ObjectValidationRule systemObjectValidationRule =
			_addObjectValidationRule(
				StringPool.BLANK, ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
				_VALID_DDM_SCRIPT, true, Collections.emptyList());

		AssertUtils.assertFailure(
			ObjectValidationRuleSystemException.class, false,
			"Only allowed bundles can delete system object validation rules",
			() -> _objectValidationRuleLocalService.deleteObjectValidationRule(
				systemObjectValidationRule.getObjectValidationRuleId()));

		_testDeleteObjectValidationRule(
			systemObjectValidationRule.getObjectValidationRuleId());
	}

	@Test
	public void testGetErrorLabel() throws Exception {
		ObjectValidationRule objectValidationRule = _addObjectValidationRule(
			StringPool.BLANK, ObjectValidationRuleConstants.ENGINE_TYPE_GROOVY,
			HashMapBuilder.put(
				LocaleUtil.BRAZIL, RandomTestUtil.randomString()
			).put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			"invalidFields = true;");

		User user = UserTestUtil.addUser();

		user = _userLocalService.updateLanguageId(
			user.getUserId(), LanguageUtil.getLanguageId(LocaleUtil.BRAZIL));

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		PrincipalThreadLocal.setName(user.getUserId());

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			user.getUserId(), _objectDefinition.getObjectDefinitionId());

		try {
			_objectEntryLocalService.addObjectEntry(
				0, user.getUserId(), _objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"textObjectField", RandomTestUtil.randomString()
				).build(),
				ServiceContextTestUtil.getServiceContext());

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			ObjectValidationRuleEngineException
				objectValidationRuleEngineException =
					(ObjectValidationRuleEngineException)
						modelListenerException.getCause();

			List<ObjectValidationRuleResult> objectValidationRuleResults =
				objectValidationRuleEngineException.
					getObjectValidationRuleResults();

			Assert.assertEquals(
				objectValidationRuleResults.toString(), 1,
				objectValidationRuleResults.size());

			ObjectValidationRuleResult objectValidationRuleResult =
				objectValidationRuleResults.get(0);

			Assert.assertEquals(
				objectValidationRule.getErrorLabel(user.getLanguageId()),
				objectValidationRuleResult.getErrorMessage());
		}
	}

	@Test
	public void testUpdateObjectValidationRule() throws Exception {
		ObjectValidationRule objectValidationRule = _addObjectValidationRule(
			ObjectValidationRuleConstants.ENGINE_TYPE_DDM, _VALID_DDM_SCRIPT);

		long randomId = RandomTestUtil.randomLong();

		AssertUtils.assertFailure(
			NoSuchObjectValidationRuleException.class,
			String.format(
				"No ObjectValidationRule exists with the primary key %s",
				randomId),
			() -> _objectValidationRuleLocalService.updateObjectValidationRule(
				StringPool.BLANK, randomId, false,
				ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
				_VALID_DDM_SCRIPT, Collections.emptyList()));

		ObjectValidationRule finalObjectValidationRule = objectValidationRule;

		AssertUtils.assertFailure(
			ObjectValidationRuleSettingNameException.NotAllowedName.class,
			String.format(
				"The object validation rule setting \"%s\" is not allowed",
				ObjectValidationRuleSettingConstants.
					NAME_ALLOW_ACTIVE_STATUS_UPDATE),
			() -> _objectValidationRuleLocalService.updateObjectValidationRule(
				finalObjectValidationRule.getExternalReferenceCode(),
				finalObjectValidationRule.getObjectValidationRuleId(), false,
				finalObjectValidationRule.getEngine(),
				finalObjectValidationRule.getErrorLabelMap(),
				finalObjectValidationRule.getNameMap(),
				finalObjectValidationRule.getOutputType(),
				finalObjectValidationRule.getScript(),
				Collections.singletonList(
					new ObjectValidationRuleSettingBuilder(
					).name(
						ObjectValidationRuleSettingConstants.
							NAME_ALLOW_ACTIVE_STATUS_UPDATE
					).value(
						"true"
					).build())));

		ObjectField textObjectField = _objectFieldLocalService.fetchObjectField(
			_objectDefinition.getObjectDefinitionId(), "textObjectField");

		objectValidationRule =
			_objectValidationRuleLocalService.updateObjectValidationRule(
				"externalReferenceCode",
				objectValidationRule.getObjectValidationRuleId(), true,
				ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
				LocalizedMapUtil.getLocalizedMap("Field must be an URL"),
				LocalizedMapUtil.getLocalizedMap("URL Validation"),
				ObjectValidationRuleConstants.OUTPUT_TYPE_PARTIAL_VALIDATION,
				"isURL(textObjectField)",
				Collections.singletonList(
					new ObjectValidationRuleSettingBuilder(
					).name(
						ObjectValidationRuleSettingConstants.
							NAME_OUTPUT_OBJECT_FIELD_ID
					).value(
						String.valueOf(textObjectField.getObjectFieldId())
					).build()));

		_assertObjectValidationRule(
			true, null, ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
			LocalizedMapUtil.getLocalizedMap("Field must be an URL"),
			"externalReferenceCode",
			LocalizedMapUtil.getLocalizedMap("URL Validation"),
			String.valueOf(textObjectField.getObjectFieldId()),
			ObjectValidationRuleConstants.OUTPUT_TYPE_PARTIAL_VALIDATION,
			"isURL(textObjectField)", objectValidationRule);

		ObjectField dateObjectField = _objectFieldLocalService.fetchObjectField(
			_objectDefinition.getObjectDefinitionId(), "dateObjectField");

		objectValidationRule =
			_objectValidationRuleLocalService.updateObjectValidationRule(
				objectValidationRule.getExternalReferenceCode(),
				objectValidationRule.getObjectValidationRuleId(), false,
				objectValidationRule.getEngine(),
				objectValidationRule.getErrorLabelMap(),
				objectValidationRule.getNameMap(),
				ObjectValidationRuleConstants.OUTPUT_TYPE_PARTIAL_VALIDATION,
				objectValidationRule.getScript(),
				Collections.singletonList(
					new ObjectValidationRuleSettingBuilder(
					).name(
						ObjectValidationRuleSettingConstants.
							NAME_OUTPUT_OBJECT_FIELD_ID
					).value(
						String.valueOf(dateObjectField.getObjectFieldId())
					).build()));

		_assertObjectValidationRule(
			false, null, ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
			LocalizedMapUtil.getLocalizedMap("Field must be an URL"),
			"externalReferenceCode",
			LocalizedMapUtil.getLocalizedMap("URL Validation"),
			String.valueOf(dateObjectField.getObjectFieldId()),
			ObjectValidationRuleConstants.OUTPUT_TYPE_PARTIAL_VALIDATION,
			"isURL(textObjectField)", objectValidationRule);

		Map<Locale, String> errorLabelMap = LocalizedMapUtil.getLocalizedMap(
			RandomTestUtil.randomString());

		_assertObjectValidationRule(
			objectValidationRule.isActive(), null,
			objectValidationRule.getEngine(), errorLabelMap,
			objectValidationRule.getExternalReferenceCode(),
			objectValidationRule.getNameMap(),
			String.valueOf(dateObjectField.getObjectFieldId()),
			objectValidationRule.getOutputType(),
			objectValidationRule.getScript(),
			_objectValidationRuleLocalService.updateObjectValidationRule(
				objectValidationRule.getExternalReferenceCode(),
				objectValidationRule.getObjectValidationRuleId(),
				objectValidationRule.isActive(),
				objectValidationRule.getEngine(), errorLabelMap,
				objectValidationRule.getNameMap(),
				objectValidationRule.getOutputType(),
				objectValidationRule.getScript(),
				objectValidationRule.getObjectValidationRuleSettings()));
	}

	@Test
	public void testUpdateSystemObjectValidationRule() throws Exception {
		ObjectValidationRule systemObjectValidationRule =
			_addObjectValidationRule(
				StringPool.BLANK,
				_modifiableSystemObjectDefinition.getObjectDefinitionId(),
				ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
				_VALID_DDM_SCRIPT, true, Collections.emptyList());

		AssertUtils.assertFailure(
			ObjectValidationRuleSystemException.class, false,
			"Only allowed bundles can edit system object validation rules",
			() -> _objectValidationRuleLocalService.updateObjectValidationRule(
				StringPool.BLANK,
				systemObjectValidationRule.getObjectValidationRuleId(), false,
				ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
				_VALID_DDM_SCRIPT, Collections.emptyList()));

		systemObjectValidationRule.setObjectValidationRuleSettings(
			Collections.singletonList(
				new ObjectValidationRuleSettingBuilder(
				).name(
					ObjectValidationRuleSettingConstants.
						NAME_ALLOW_ACTIVE_STATUS_UPDATE
				).value(
					"true"
				).build()));

		_assertObjectValidationRule(
			systemObjectValidationRule.isActive(), "true",
			systemObjectValidationRule.getEngine(),
			systemObjectValidationRule.getErrorLabelMap(),
			systemObjectValidationRule.getExternalReferenceCode(),
			systemObjectValidationRule.getNameMap(), null,
			systemObjectValidationRule.getOutputType(),
			systemObjectValidationRule.getScript(),
			_objectValidationRuleLocalService.updateObjectValidationRule(
				systemObjectValidationRule.getExternalReferenceCode(),
				systemObjectValidationRule.getObjectValidationRuleId(),
				systemObjectValidationRule.isActive(),
				systemObjectValidationRule.getEngine(),
				systemObjectValidationRule.getErrorLabelMap(),
				systemObjectValidationRule.getNameMap(),
				systemObjectValidationRule.getOutputType(),
				systemObjectValidationRule.getScript(),
				systemObjectValidationRule.getObjectValidationRuleSettings()));

		String liferayMode = SystemProperties.get("liferay.mode");

		SystemProperties.clear("liferay.mode");

		try {
			_assertObjectValidationRule(
				false, "true", systemObjectValidationRule.getEngine(),
				systemObjectValidationRule.getErrorLabelMap(),
				systemObjectValidationRule.getExternalReferenceCode(),
				systemObjectValidationRule.getNameMap(), null,
				systemObjectValidationRule.getOutputType(),
				systemObjectValidationRule.getScript(),
				_objectValidationRuleLocalService.updateObjectValidationRule(
					systemObjectValidationRule.getExternalReferenceCode(),
					systemObjectValidationRule.getObjectValidationRuleId(),
					false, systemObjectValidationRule.getEngine(),
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					systemObjectValidationRule.getOutputType(),
					systemObjectValidationRule.getScript(),
					Collections.emptyList()));
		}
		finally {
			SystemProperties.set("liferay.mode", liferayMode);
		}
	}

	private ObjectValidationRule _addObjectValidationRule(
			String externalReferenceCode, long objectDefinitionId,
			String engine, Map<Locale, String> errorLabelMap,
			Map<Locale, String> nameLabelMap, String outputType, String script,
			boolean system,
			List<ObjectValidationRuleSetting> objectValidationRuleSettings)
		throws Exception {

		return _objectValidationRuleLocalService.addObjectValidationRule(
			externalReferenceCode, TestPropsValues.getUserId(),
			objectDefinitionId, true, engine, errorLabelMap, nameLabelMap,
			outputType, script, system, objectValidationRuleSettings);
	}

	private ObjectValidationRule _addObjectValidationRule(
			String engine, String script)
		throws Exception {

		return _addObjectValidationRule(
			StringPool.BLANK, engine,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			script);
	}

	private ObjectValidationRule _addObjectValidationRule(
			String externalReferenceCode, String engine,
			Map<Locale, String> errorLabelMap, Map<Locale, String> nameLabelMap,
			String script)
		throws Exception {

		return _addObjectValidationRule(
			externalReferenceCode, engine, errorLabelMap, nameLabelMap,
			ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION, script,
			false, Collections.emptyList());
	}

	private ObjectValidationRule _addObjectValidationRule(
			String externalReferenceCode, String engine,
			Map<Locale, String> errorLabelMap, Map<Locale, String> nameLabelMap,
			String outputType, String script, boolean system,
			List<ObjectValidationRuleSetting> objectValidationRuleSettings)
		throws Exception {

		return _objectValidationRuleLocalService.addObjectValidationRule(
			externalReferenceCode, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), true, engine,
			errorLabelMap, nameLabelMap, outputType, script, system,
			objectValidationRuleSettings);
	}

	private void _assertObjectValidationRule(
		boolean expectedActive, String expectedAllowActiveStatusUpdate,
		String expectedEngine, Map<Locale, String> expectedErrorLabelMap,
		String expectedExternalReferenceCode,
		Map<Locale, String> expectedNameLabelMap, String expectedObjectFieldId,
		String expectedOutputType, String expectedScript,
		ObjectValidationRule objectValidationRule) {

		Assert.assertEquals(expectedActive, objectValidationRule.isActive());
		Assert.assertEquals(expectedEngine, objectValidationRule.getEngine());
		Assert.assertEquals(
			expectedErrorLabelMap, objectValidationRule.getErrorLabelMap());
		Assert.assertEquals(
			expectedExternalReferenceCode,
			objectValidationRule.getExternalReferenceCode());
		Assert.assertEquals(
			expectedNameLabelMap, objectValidationRule.getNameMap());
		Assert.assertEquals(
			expectedOutputType, objectValidationRule.getOutputType());
		Assert.assertEquals(expectedScript, objectValidationRule.getScript());

		Map<String, Object> objectValidationRuleSettings = new HashMap<>();

		for (ObjectValidationRuleSetting objectValidationRuleSetting :
				objectValidationRule.getObjectValidationRuleSettings()) {

			objectValidationRuleSettings.put(
				objectValidationRuleSetting.getName(),
				objectValidationRuleSetting.getValue());
		}

		Assert.assertEquals(
			expectedAllowActiveStatusUpdate,
			objectValidationRuleSettings.getOrDefault(
				ObjectValidationRuleSettingConstants.
					NAME_ALLOW_ACTIVE_STATUS_UPDATE,
				null));

		if (StringUtil.equals(
				objectValidationRule.getOutputType(),
				ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION)) {

			Assert.assertNull(
				objectValidationRuleSettings.get(
					ObjectValidationRuleSettingConstants.
						NAME_OUTPUT_OBJECT_FIELD_ID));
		}
		else if (StringUtil.equals(
					objectValidationRule.getOutputType(),
					ObjectValidationRuleConstants.
						OUTPUT_TYPE_PARTIAL_VALIDATION)) {

			Assert.assertEquals(
				expectedObjectFieldId,
				objectValidationRuleSettings.get(
					ObjectValidationRuleSettingConstants.
						NAME_OUTPUT_OBJECT_FIELD_ID));
		}
	}

	private Object _getAndSetFieldValue(
			Class<?> clazz, String fieldName,
			String objectValidationRuleEngineKey)
		throws Exception {

		return ReflectionTestUtil.getAndSetFieldValue(
			_objectValidationRuleEngineRegistry.getObjectValidationRuleEngine(
				0, objectValidationRuleEngineKey),
			fieldName,
			ProxyUtil.newProxyInstance(
				clazz.getClassLoader(), new Class<?>[] {clazz},
				(proxy, method, arguments) -> {
					_argumentsList.add(arguments);

					if (!Objects.equals(
							method.getDeclaringClass(),
							ObjectScriptingExecutor.class) ||
						!Objects.equals(method.getName(), "execute")) {

						return null;
					}

					return HashMapBuilder.<String, Object>put(
						"validationCriteriaMet", true
					).build();
				}));
	}

	private void _testDeleteObjectValidationRule(long objectValidationRuleId)
		throws Exception {

		Assert.assertNotNull(
			_objectValidationRuleLocalService.fetchObjectValidationRule(
				objectValidationRuleId));

		_objectValidationRuleLocalService.deleteObjectValidationRule(
			objectValidationRuleId);

		Assert.assertNull(
			_objectValidationRuleLocalService.fetchObjectValidationRule(
				objectValidationRuleId));
	}

	private static final String _VALID_DDM_SCRIPT =
		"isEmailAddress(textObjectField)";

	private final Queue<Object[]> _argumentsList = new LinkedList<>();
	private ObjectDefinition _modifiableSystemObjectDefinition;
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectValidationRuleEngineRegistry
		_objectValidationRuleEngineRegistry;

	@Inject
	private ObjectValidationRuleLocalService _objectValidationRuleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}