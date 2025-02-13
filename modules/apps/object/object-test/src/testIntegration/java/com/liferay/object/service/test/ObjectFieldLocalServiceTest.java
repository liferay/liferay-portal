/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.list.type.entry.util.ListTypeEntryUtil;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectFilterConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.exception.ObjectFieldBusinessTypeException;
import com.liferay.object.exception.ObjectFieldDBTypeException;
import com.liferay.object.exception.ObjectFieldLabelException;
import com.liferay.object.exception.ObjectFieldListTypeDefinitionIdException;
import com.liferay.object.exception.ObjectFieldLocalizedException;
import com.liferay.object.exception.ObjectFieldNameException;
import com.liferay.object.exception.ObjectFieldReadOnlyConditionExpressionException;
import com.liferay.object.exception.ObjectFieldReadOnlyException;
import com.liferay.object.exception.ObjectFieldRelationshipTypeException;
import com.liferay.object.exception.ObjectFieldRequiredException;
import com.liferay.object.exception.ObjectFieldSettingNameException;
import com.liferay.object.exception.ObjectFieldSettingValueException;
import com.liferay.object.exception.ObjectFieldStateException;
import com.liferay.object.exception.ObjectFieldSystemException;
import com.liferay.object.exception.RequiredObjectFieldException;
import com.liferay.object.field.builder.AggregationObjectFieldBuilder;
import com.liferay.object.field.builder.AttachmentObjectFieldBuilder;
import com.liferay.object.field.builder.AutoIncrementObjectFieldBuilder;
import com.liferay.object.field.builder.BooleanObjectFieldBuilder;
import com.liferay.object.field.builder.DateTimeObjectFieldBuilder;
import com.liferay.object.field.builder.EncryptedObjectFieldBuilder;
import com.liferay.object.field.builder.FormulaObjectFieldBuilder;
import com.liferay.object.field.builder.IntegerObjectFieldBuilder;
import com.liferay.object.field.builder.LongIntegerObjectFieldBuilder;
import com.liferay.object.field.builder.MultiselectPicklistObjectFieldBuilder;
import com.liferay.object.field.builder.ObjectFieldBuilder;
import com.liferay.object.field.builder.PicklistObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.field.business.type.ObjectFieldBusinessTypeRegistry;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectFilter;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectFilterLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.test.util.ObjectFieldTestUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.object.test.util.TreeTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.sql.Connection;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
@FeatureFlags("LPD-34594")
@RunWith(Arquillian.class)
public class ObjectFieldLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_listTypeEntryKey = RandomTestUtil.randomString();

		_listTypeDefinition =
			_listTypeDefinitionLocalService.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				false,
				Collections.singletonList(
					ListTypeEntryUtil.createListTypeEntry(_listTypeEntryKey)));
	}

	@FeatureFlags("LPD-32050")
	@Test
	public void testAddCustomObjectField() throws Exception {
		AssertUtils.assertFailure(
			ObjectFieldBusinessTypeException.class,
			ObjectFieldConstants.BUSINESS_TYPE_AGGREGATION +
				" business type is not indexable",
			() -> ObjectDefinitionTestUtil.addCustomObjectDefinition(
				Arrays.asList(
					new AggregationObjectFieldBuilder(
					).indexed(
						true
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"a" + RandomTestUtil.randomString()
					).objectFieldSettings(
						Arrays.asList(
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.NAME_FUNCTION
							).value(
								ObjectFieldSettingConstants.VALUE_MAX
							).build(),
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.
									NAME_OBJECT_FIELD_NAME
							).value(
								"integer"
							).build(),
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.
									NAME_OBJECT_RELATIONSHIP_NAME
							).value(
								"oneToManyRelationshipName"
							).build())
					).build())));
		AssertUtils.assertFailure(
			ObjectFieldBusinessTypeException.class,
			ObjectFieldConstants.BUSINESS_TYPE_ENCRYPTED +
				" business type is not indexable",
			() -> ObjectFieldTestUtil.withEncryptedObjectFieldProperties(
				"AES", true, ObjectFieldTestUtil.generateKey("AES"),
				() -> ObjectDefinitionTestUtil.addCustomObjectDefinition(
					Arrays.asList(
						new EncryptedObjectFieldBuilder(
						).indexed(
							true
						).labelMap(
							LocalizedMapUtil.getLocalizedMap(
								RandomTestUtil.randomString())
						).name(
							"a" + RandomTestUtil.randomString()
						).build()))));
		AssertUtils.assertFailure(
			ObjectFieldBusinessTypeException.class,
			ObjectFieldConstants.BUSINESS_TYPE_FORMULA +
				" business type is not indexable",
			() -> ObjectDefinitionTestUtil.addCustomObjectDefinition(
				Arrays.asList(
					new FormulaObjectFieldBuilder(
					).indexed(
						true
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"a" + RandomTestUtil.randomString()
					).objectFieldSettings(
						Arrays.asList(
							new ObjectFieldSettingBuilder(
							).name(
								"script"
							).value(
								"weight + 10"
							).build(),
							new ObjectFieldSettingBuilder(
							).name(
								"output"
							).value(
								ObjectFieldConstants.BUSINESS_TYPE_DECIMAL
							).build())
					).build())));

		AssertUtils.assertFailure(
			ObjectFieldBusinessTypeException.class,
			"Business type encrypted can only be used in object definitions " +
				"with a default storage type",
			() -> _addCustomObjectDefinitionWithEncryptedObjectField(
				"AES", true, ObjectFieldTestUtil.generateKey("AES"),
				ObjectDefinitionConstants.STORAGE_TYPE_SALESFORCE));
		AssertUtils.assertFailure(
			ObjectFieldBusinessTypeException.class,
			"Business type encrypted is disabled",
			() -> _addCustomObjectDefinitionWithEncryptedObjectField(
				"", false, "", ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT));
		AssertUtils.assertFailure(
			ObjectFieldBusinessTypeException.class,
			"Encryption algorithm is required for business type encrypted",
			() -> _addCustomObjectDefinitionWithEncryptedObjectField(
				"", true, ObjectFieldTestUtil.generateKey("AES"),
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT));
		AssertUtils.assertFailure(
			ObjectFieldBusinessTypeException.class,
			"Encryption key is required for business type encrypted",
			() -> _addCustomObjectDefinitionWithEncryptedObjectField(
				"AES", true, "",
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT));
		AssertUtils.assertFailure(
			ObjectFieldBusinessTypeException.class,
			"Salesforce storage type does not support aggregation and " +
				"attachment business types",
			() -> _objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, false, true, true,
				false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_SALESFORCE,
				Collections.emptyList(),
				Arrays.asList(
					new AggregationObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"a" + RandomTestUtil.randomString()
					).objectFieldSettings(
						Arrays.asList(
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.NAME_FUNCTION
							).value(
								"MAX"
							).build(),
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.
									NAME_OBJECT_FIELD_NAME
							).value(
								"integer"
							).build(),
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.
									NAME_OBJECT_RELATIONSHIP_NAME
							).value(
								"oneToManyRelationshipName"
							).build())
					).build())));
		AssertUtils.assertFailure(
			ObjectFieldBusinessTypeException.class,
			"Salesforce storage type does not support aggregation and " +
				"attachment business types",
			() -> _objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, false, true, true,
				false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_SALESFORCE,
				Collections.emptyList(),
				Arrays.asList(
					new AttachmentObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).listTypeDefinitionId(
						_listTypeDefinition.getListTypeDefinitionId()
					).name(
						"a" + RandomTestUtil.randomString()
					).build())));
		AssertUtils.assertFailure(
			ObjectFieldListTypeDefinitionIdException.class,
			"List type definition ID is 0",
			() -> ObjectDefinitionTestUtil.addCustomObjectDefinition(
				Arrays.asList(
					new MultiselectPicklistObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"a" + RandomTestUtil.randomString()
					).build())));
		AssertUtils.assertFailure(
			ObjectFieldLocalizedException.class,
			StringBundler.concat(
				"Only Attachment,Boolean,Date,DateTime,Decimal,Integer,",
				"LongInteger,LongText,MultiselectPicklist,Picklist,",
				"PrecisionDecimal,RichText and Text business types support ",
				"localization"),
			() -> ObjectDefinitionTestUtil.addCustomObjectDefinition(
				Arrays.asList(
					new AutoIncrementObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"a" + RandomTestUtil.randomString()
					).localized(
						true
					).build())));

		ObjectDefinition objectDefinition1 =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();
		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		String objectRelationshipName = "a" + RandomTestUtil.randomString();

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				objectDefinition1.getObjectDefinitionId(),
				objectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				objectRelationshipName, false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		AssertUtils.assertFailure(
			ObjectFieldNameException.MustNotBeEqualToObjectRelationshipName.
				class,
			StringBundler.concat(
				"There is already an object relationship with this name in ",
				"the object definition \"", objectDefinition1.getShortName(),
				".\" Object fields and object relationships cannot have the ",
				"same name."),
			() -> _addCustomObjectField(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					objectRelationshipName
				).objectDefinitionId(
					objectDefinition1.getObjectDefinitionId()
				).build()));
		AssertUtils.assertFailure(
			ObjectFieldNameException.MustNotBeEqualToObjectRelationshipName.
				class,
			StringBundler.concat(
				"There is already an object relationship with this name in ",
				"the object definition \"", objectDefinition1.getShortName(),
				".\" Object fields and object relationships cannot have the ",
				"same name."),
			() -> _addCustomObjectField(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					objectRelationshipName
				).objectDefinitionId(
					objectDefinition2.getObjectDefinitionId()
				).build()));

		ObjectFilter objectFilter =
			_objectFilterLocalService.createObjectFilter(0L);

		objectFilter.setFilterBy(RandomTestUtil.randomString());
		objectFilter.setFilterType(ObjectFilterConstants.TYPE_EQUALS);
		objectFilter.setJSON(
			JSONUtil.put(
				ObjectFilterConstants.TYPE_EQUALS, RandomTestUtil.randomInt()
			).toString());

		Assert.assertNotNull(
			_addCustomObjectField(
				new AggregationObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"a" + RandomTestUtil.randomString()
				).objectDefinitionId(
					objectDefinition1.getObjectDefinitionId()
				).objectFieldSettings(
					Arrays.asList(
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_FILTERS
						).objectFilters(
							Arrays.asList(objectFilter)
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_FUNCTION
						).value(
							ObjectFieldSettingConstants.VALUE_MAX
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_OBJECT_FIELD_NAME
						).value(
							"a" + RandomTestUtil.randomString()
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.
								NAME_OBJECT_RELATIONSHIP_NAME
						).value(
							objectRelationshipName
						).build())
				).build()));

		_objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship);

		objectRelationship =
			_objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				objectDefinition1.getObjectDefinitionId(),
				objectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				objectRelationshipName, false,
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null);

		AssertUtils.assertFailure(
			ObjectFieldNameException.MustNotBeEqualToObjectRelationshipName.
				class,
			StringBundler.concat(
				"There is already an object relationship with this name in ",
				"the object definition \"", objectDefinition1.getShortName(),
				".\" Object fields and object relationships cannot have the ",
				"same name."),
			() -> _addCustomObjectField(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					objectRelationshipName
				).objectDefinitionId(
					objectDefinition1.getObjectDefinitionId()
				).build()));
		AssertUtils.assertFailure(
			ObjectFieldNameException.MustNotBeEqualToObjectRelationshipName.
				class,
			StringBundler.concat(
				"There is already an object relationship with this name in ",
				"the object definition \"", objectDefinition1.getShortName(),
				".\" Object fields and object relationships cannot have the ",
				"same name."),
			() -> _addCustomObjectField(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					objectRelationshipName
				).objectDefinitionId(
					objectDefinition2.getObjectDefinitionId()
				).build()));

		_objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship);

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition1);
		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition2);

		String[] reservedNames = {
			"actions", "companyId", "createDate", "creator", "dateCreated",
			"dateModified", "externalReferenceCode", "groupId", "id",
			"lastPublishDate", "modifiedDate", "statusByUserId",
			"statusByUserName", "statusDate", "userId", "userName"
		};

		for (String reservedName : reservedNames) {
			AssertUtils.assertFailure(
				ObjectFieldNameException.MustNotBeReserved.class,
				"Reserved name " + reservedName,
				() -> ObjectDefinitionTestUtil.addCustomObjectDefinition(
					Arrays.asList(
						new TextObjectFieldBuilder(
						).labelMap(
							LocalizedMapUtil.getLocalizedMap(
								RandomTestUtil.randomString())
						).name(
							reservedName
						).build())));
		}

		AssertUtils.assertFailure(
			ObjectFieldSettingNameException.NotAllowedNames.class,
			"The settings anySetting are not allowed for object field text",
			() -> ObjectDefinitionTestUtil.addCustomObjectDefinition(
				Arrays.asList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"text"
					).objectFieldSettings(
						Arrays.asList(
							new ObjectFieldSettingBuilder(
							).name(
								"anySetting"
							).value(
								"10"
							).build(),
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.NAME_SHOW_COUNTER
							).value(
								"true"
							).build())
					).build())));
		AssertUtils.assertFailure(
			ObjectFieldSettingNameException.NotAllowedNames.class,
			"The settings defaultValue, defaultValueType are not allowed for " +
				"object field text",
			() -> ObjectDefinitionTestUtil.addCustomObjectDefinition(
				Arrays.asList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"text"
					).objectFieldSettings(
						Arrays.asList(
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.NAME_DEFAULT_VALUE
							).value(
								_listTypeEntryKey
							).build(),
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.
									NAME_DEFAULT_VALUE_TYPE
							).value(
								ObjectFieldSettingConstants.VALUE_INPUT_AS_VALUE
							).build(),
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.NAME_SHOW_COUNTER
							).value(
								"false"
							).build())
					).build())));
		AssertUtils.assertFailure(
			ObjectFieldSettingNameException.NotAllowedNames.class,
			"The settings maxLength are not allowed for object field text",
			() -> _addCustomObjectDefinitionWithTextObjectField(
				RandomTestUtil.randomString(), null, null));
		AssertUtils.assertFailure(
			ObjectFieldSettingNameException.NotAllowedNames.class,
			"The settings maxLength are not allowed for object field text",
			() -> _addCustomObjectDefinitionWithTextObjectField(
				"10", "false", null));
		AssertUtils.assertFailure(
			ObjectFieldSettingValueException.ExceedsMaxLength.class,
			"The setting \"prefix\" exceeds the maximum length of 50",
			() -> _addCustomObjectDefinitionWithAutoIncrementObjectField(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(51),
				null, false, StringPool.BLANK));
		AssertUtils.assertFailure(
			ObjectFieldSettingValueException.ExceedsMaxLength.class,
			"The setting \"suffix\" exceeds the maximum length of 50",
			() -> _addCustomObjectDefinitionWithAutoIncrementObjectField(
				RandomTestUtil.randomString(), StringPool.BLANK, null, false,
				RandomTestUtil.randomString(51)));

		String defaultValue = RandomTestUtil.randomString();

		AssertUtils.assertFailure(
			ObjectFieldSettingValueException.InvalidValue.class,
			StringBundler.concat(
				"The value ", defaultValue,
				" of setting \"defaultValue\" is invalid for object field ",
				"\"boolean\""),
			() -> ObjectDefinitionTestUtil.addCustomObjectDefinition(
				Collections.singletonList(
					new BooleanObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"boolean"
					).objectFieldSettings(
						Arrays.asList(
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.NAME_DEFAULT_VALUE
							).value(
								defaultValue
							).build(),
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.
									NAME_DEFAULT_VALUE_TYPE
							).value(
								ObjectFieldSettingConstants.VALUE_INPUT_AS_VALUE
							).build())
					).build())));
		AssertUtils.assertFailure(
			ObjectFieldSettingValueException.InvalidValue.class,
			StringBundler.concat(
				"The value ", defaultValue,
				" of setting \"defaultValue\" is invalid for object field ",
				"\"picklist\""),
			() -> _addCustomObjectDefinitionWithPicklistObjectField(
				defaultValue, ObjectFieldSettingConstants.VALUE_INPUT_AS_VALUE,
				false, false));

		long initialValue = RandomTestUtil.randomLong(Long.MIN_VALUE, 0);

		AssertUtils.assertFailure(
			ObjectFieldSettingValueException.InvalidValue.class,
			StringBundler.concat(
				"The value ", initialValue,
				" of setting \"initialValue\" is invalid for object field ",
				"\"autoIncrement\""),
			() -> _addCustomObjectDefinitionWithAutoIncrementObjectField(
				String.valueOf(initialValue), null, null, false,
				StringPool.BLANK));

		String uniqueValues = RandomTestUtil.randomString();

		AssertUtils.assertFailure(
			ObjectFieldSettingValueException.InvalidValue.class,
			StringBundler.concat(
				"The value ", uniqueValues,
				" of setting \"uniqueValues\" is invalid for object field ",
				"\"text\""),
			() -> _addCustomObjectDefinitionWithTextObjectField(
				null, null, uniqueValues));

		AssertUtils.assertFailure(
			ObjectFieldSettingValueException.InvalidValue.class,
			"The value expressionBuilder of setting \"defaultValueType\" is " +
				"invalid for object field \"picklist\"",
			() -> _addCustomObjectDefinitionWithPicklistObjectField(
				_listTypeEntryKey,
				ObjectFieldSettingConstants.VALUE_EXPRESSION_BUILDER, true,
				true));
		AssertUtils.assertFailure(
			ObjectFieldSettingValueException.InvalidValue.class,
			"The value LPS@ of setting \"prefix\" is invalid for object " +
				"field \"autoIncrement\"",
			() -> _addCustomObjectDefinitionWithAutoIncrementObjectField(
				RandomTestUtil.randomString(), "LPS@", null, false, null));
		AssertUtils.assertFailure(
			ObjectFieldSettingValueException.InvalidValue.class,
			"The value ^private of setting \"suffix\" is invalid for object " +
				"field \"autoIncrement\"",
			() -> _addCustomObjectDefinitionWithAutoIncrementObjectField(
				RandomTestUtil.randomString(), null, null, false, "^private"));
		AssertUtils.assertFailure(
			ObjectFieldSettingValueException.MissingRequiredValues.class,
			"The settings \"acceptedFileExtensions, fileSource, " +
				"maximumFileSize\" are required for object field \"upload\"",
			() -> ObjectDefinitionTestUtil.addCustomObjectDefinition(
				Arrays.asList(
					new AttachmentObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"upload"
					).build())));
		AssertUtils.assertFailure(
			ObjectFieldSettingValueException.MissingRequiredValues.class,
			"The settings \"defaultValue, defaultValueType\" are required " +
				"for object field \"picklist\"",
			() -> _addCustomObjectDefinitionWithPicklistObjectField(
				null, null, true, true));
		AssertUtils.assertFailure(
			ObjectFieldSettingValueException.MissingRequiredValues.class,
			"The settings \"initialValue\" are required for object field " +
				"\"autoIncrement\"",
			() -> _addCustomObjectDefinitionWithAutoIncrementObjectField(
				StringPool.BLANK, null, null, false, null));
		AssertUtils.assertFailure(
			ObjectFieldSettingValueException.MissingRequiredValues.class,
			"The settings \"maxLength\" are required for object field \"text\"",
			() -> _addCustomObjectDefinitionWithTextObjectField(
				null, "true", null));
		AssertUtils.assertFailure(
			ObjectFieldSettingValueException.MissingRequiredValues.class,
			"The settings \"timeStorage\" are required for object field " +
				"\"datetime\"",
			() -> ObjectDefinitionTestUtil.addCustomObjectDefinition(
				Collections.singletonList(
					new DateTimeObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"datetime"
					).build())));
		AssertUtils.assertFailure(
			ObjectFieldReadOnlyException.class, null,
			() -> _addCustomObjectDefinitionWithAutoIncrementObjectField(
				"0123", null, ObjectFieldConstants.READ_ONLY_TRUE, false,
				null));
		AssertUtils.assertFailure(
			ObjectFieldRequiredException.class, null,
			() -> _addCustomObjectDefinitionWithAutoIncrementObjectField(
				"0123", null, ObjectFieldConstants.READ_ONLY_FALSE, true,
				null));
		AssertUtils.assertFailure(
			ObjectFieldStateException.class,
			"Object field must be required when the state is true",
			() -> _addCustomObjectDefinitionWithPicklistObjectField(
				_listTypeEntryKey,
				ObjectFieldSettingConstants.VALUE_INPUT_AS_VALUE, false, true));

		_testAddCustomObjectFieldReadOnly();
	}

	@Test
	public void testAddOrUpdateCustomObjectField() throws Exception {
		ObjectFieldBuilder objectFieldBuilder =
			new LongIntegerObjectFieldBuilder();

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				false,
				Arrays.asList(
					objectFieldBuilder.userId(
						TestPropsValues.getUserId()
					).indexedAsKeyword(
						true
					).indexedLanguageId(
						StringPool.BLANK
					).labelMap(
						LocalizedMapUtil.getLocalizedMap("able")
					).name(
						"able"
					).build()));

		ObjectField objectField1 = _objectFieldLocalService.fetchObjectField(
			objectDefinition.getObjectDefinitionId(), "able");

		Assert.assertEquals("able_", objectField1.getDBColumnName());
		Assert.assertEquals(
			ObjectFieldConstants.DB_TYPE_LONG, objectField1.getDBType());
		Assert.assertFalse(objectField1.isIndexed());
		Assert.assertTrue(objectField1.isIndexedAsKeyword());
		Assert.assertEquals("", objectField1.getIndexedLanguageId());
		Assert.assertEquals(
			LocalizedMapUtil.getLocalizedMap("able"),
			objectField1.getLabelMap());
		Assert.assertEquals("able", objectField1.getName());
		Assert.assertFalse(objectField1.isRequired());

		_testAddOrUpdateCustomObjectField(
			objectFieldBuilder.dbColumnName(
				objectField1.getDBColumnName()
			).objectFieldId(
				objectField1.getObjectFieldId()
			).externalReferenceCode(
				objectField1.getExternalReferenceCode()
			).build());

		_testAddOrUpdateCustomObjectField(
			objectFieldBuilder.businessType(
				ObjectFieldConstants.BUSINESS_TYPE_PICKLIST
			).dbType(
				ObjectFieldConstants.DB_TYPE_STRING
			).indexedAsKeyword(
				false
			).listTypeDefinitionId(
				_listTypeDefinition.getListTypeDefinitionId()
			).build());

		_testAddOrUpdateCustomObjectField(
			objectFieldBuilder.objectFieldSettings(
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_DEFAULT_VALUE
					).value(
						_listTypeEntryKey
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE
					).value(
						ObjectFieldSettingConstants.VALUE_INPUT_AS_VALUE
					).build())
			).required(
				true
			).state(
				true
			).build());

		_testAddOrUpdateCustomObjectField(
			objectFieldBuilder.businessType(
				ObjectFieldConstants.BUSINESS_TYPE_TEXT
			).dbColumnName(
				"baker_"
			).indexed(
				true
			).indexedLanguageId(
				LanguageUtil.getLanguageId(LocaleUtil.getDefault())
			).labelMap(
				LocalizedMapUtil.getLocalizedMap("baker")
			).listTypeDefinitionId(
				0
			).name(
				"baker"
			).objectFieldSettings(
				Collections.emptyList()
			).state(
				false
			).build());

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());

		objectField1 = _addOrUpdateCustomObjectField(
			objectFieldBuilder.businessType(
				ObjectFieldConstants.BUSINESS_TYPE_INTEGER
			).dbType(
				ObjectFieldConstants.DB_TYPE_INTEGER
			).indexed(
				false
			).indexedAsKeyword(
				true
			).indexedLanguageId(
				StringPool.BLANK
			).labelMap(
				LocalizedMapUtil.getLocalizedMap("charlie")
			).name(
				"charlie"
			).build());

		Assert.assertEquals("baker_", objectField1.getDBColumnName());
		Assert.assertEquals(
			ObjectFieldConstants.DB_TYPE_STRING, objectField1.getDBType());
		Assert.assertFalse(objectField1.isIndexed());
		Assert.assertTrue(objectField1.isIndexedAsKeyword());
		Assert.assertEquals(
			StringPool.BLANK, objectField1.getIndexedLanguageId());
		Assert.assertEquals(
			objectField1.getLabelMap(),
			LocalizedMapUtil.getLocalizedMap("charlie"));
		Assert.assertEquals("baker", objectField1.getName());
		Assert.assertTrue(objectField1.isRequired());

		// Object field indexed language id

		objectField1 = _addCustomObjectField(
			new TextObjectFieldBuilder(
			).indexed(
				false
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"a" + RandomTestUtil.randomString()
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).build());

		Assert.assertEquals(
			StringPool.BLANK, objectField1.getIndexedLanguageId());

		objectField1.setIndexed(true);

		objectField1 = _addOrUpdateCustomObjectField(objectField1);

		Assert.assertEquals(
			LanguageUtil.getLanguageId(LocaleUtil.getDefault()),
			objectField1.getIndexedLanguageId());

		// Object field label needs to be replicated when there is an update
		// with another default language

		Locale defaultLocale = LocaleUtil.getDefault();

		LocaleUtil.setDefault(
			LocaleUtil.GERMANY.getLanguage(), LocaleUtil.GERMANY.getCountry(),
			LocaleUtil.GERMANY.getVariant());

		objectField1 = _addOrUpdateCustomObjectField(
			objectField1, objectField1.getObjectFieldSettings());

		Map<Locale, String> labelMap = objectField1.getLabelMap();

		Assert.assertEquals(
			labelMap.get(LocaleUtil.GERMANY), labelMap.get(LocaleUtil.US));

		LocaleUtil.setDefault(
			defaultLocale.getLanguage(), defaultLocale.getCountry(),
			defaultLocale.getVariant());

		// Object field read only

		objectDefinition = ObjectDefinitionTestUtil.addCustomObjectDefinition(
			false, Collections.emptyList());

		objectField1 = _addCustomObjectField(
			_getReadOnlyTextObjectField(
				objectDefinition.getObjectDefinitionId(), null, null));

		ObjectField finalObjectField1 = objectField1;

		AssertUtils.assertFailure(
			ObjectFieldReadOnlyConditionExpressionException.class,
			"Read only condition expression is required",
			() -> _updateReadOnlyObjectField(
				finalObjectField1, ObjectFieldConstants.READ_ONLY_CONDITIONAL,
				null));

		String invalidDDMScript = RandomTestUtil.randomString() + "()";

		AssertUtils.assertFailure(
			ObjectFieldReadOnlyConditionExpressionException.class,
			"Syntax error in: " + invalidDDMScript,
			() -> _updateReadOnlyObjectField(
				finalObjectField1, ObjectFieldConstants.READ_ONLY_CONDITIONAL,
				invalidDDMScript));

		String invalidReadOnly = RandomTestUtil.randomString();

		AssertUtils.assertFailure(
			ObjectFieldReadOnlyException.class,
			"Unknown read only: " + invalidReadOnly,
			() -> _updateReadOnlyObjectField(
				finalObjectField1, invalidReadOnly, null));

		ObjectDefinition relatedObjectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				false,
				Arrays.asList(
					_getIntegerObjectField(0, Collections.emptyList())));

		_objectRelationshipLocalService.addObjectRelationship(
			null, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			relatedObjectDefinition.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			"oneToManyRelationshipName", false,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		_assertReadOnly(
			invalidDDMScript, invalidReadOnly,
			_addReadOnlyAggregationObjectField(
				objectDefinition.getObjectDefinitionId(), null, null));
		_assertReadOnly(
			invalidDDMScript, invalidReadOnly,
			_addReadOnlyFormulaObjectField(
				objectDefinition.getObjectDefinitionId(), null, null));

		// Object field relationship

		relatedObjectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				relatedObjectDefinition.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"relationship", false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		ObjectField relationshipObjectField =
			_objectFieldLocalService.fetchObjectField(
				relatedObjectDefinition.getObjectDefinitionId(),
				"r_relationship_" + objectDefinition.getPKObjectFieldName());

		long relationshipObjectFieldId =
			relationshipObjectField.getObjectFieldId();

		relationshipObjectField = _addOrUpdateCustomObjectField(
			relationshipObjectField,
			Arrays.asList(
				_objectFieldSettingLocalService.fetchObjectFieldSetting(
					relationshipObjectFieldId,
					ObjectFieldSettingConstants.
						NAME_OBJECT_DEFINITION_1_SHORT_NAME),
				_objectFieldSettingLocalService.fetchObjectFieldSetting(
					relationshipObjectFieldId,
					ObjectFieldSettingConstants.
						NAME_OBJECT_RELATIONSHIP_ERC_OBJECT_FIELD_NAME)));

		String relationshipObjectFieldDBColumnName =
			relationshipObjectField.getDBColumnName();

		Assert.assertNotEquals(
			StringPool.UNDERLINE,
			relationshipObjectFieldDBColumnName.charAt(
				relationshipObjectFieldDBColumnName.length() - 1));

		AssertUtils.assertFailure(
			ObjectFieldRelationshipTypeException.class,
			"Object field relationship name and DB type cannot be changed",
			() -> _addOrUpdateCustomObjectField(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"able"
				).objectFieldId(
					relationshipObjectFieldId
				).build()));

		_objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship);

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
		_objectDefinitionLocalService.deleteObjectDefinition(
			relatedObjectDefinition);

		objectRelationship = ObjectRelationshipTestUtil.addObjectRelationship(
			_objectRelationshipLocalService,
			ObjectDefinitionTestUtil.addCustomObjectDefinition("A"),
			ObjectDefinitionTestUtil.addCustomObjectDefinition("AA"));

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			Collections.singletonList(objectRelationship));

		ObjectField objectField2 = _objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());

		objectField2.setRequired(false);

		AssertUtils.assertFailure(
			ObjectFieldRequiredException.class, null,
			() -> _addOrUpdateCustomObjectField(objectField2));

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService, new String[] {"C_A", "C_AA"},
			_objectEntryLocalService, _objectRelationshipLocalService);

		// Object field required

		objectDefinition = ObjectDefinitionTestUtil.addCustomObjectDefinition();

		ObjectField objectField3 = _addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"a" + RandomTestUtil.randomString()
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).required(
				true
			).build());

		Assert.assertTrue(objectField3.isRequired());

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());

		objectField3.setRequired(false);

		objectField3 = _addOrUpdateCustomObjectField(objectField3);

		Assert.assertFalse(objectField3.isRequired());

		objectField3.setRequired(true);

		ObjectField finalObjectField2 = objectField3;

		AssertUtils.assertFailure(
			ObjectFieldRequiredException.class, null,
			() -> _addOrUpdateCustomObjectField(finalObjectField2));
	}

	@Test
	public void testAddOrUpdateSystemObjectField() throws Exception {
		ObjectDefinition modifiableSystemObjectDefinition =
			ObjectDefinitionTestUtil.addModifiableSystemObjectDefinition(
				TestPropsValues.getUserId(), null, true,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test" + RandomTestUtil.randomString(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionConstants.SCOPE_SITE, null, 1,
				Collections.emptyList());

		ObjectField systemObjectField = _addOrUpdateSystemObjectField(
			null, modifiableSystemObjectDefinition.getObjectDefinitionId(),
			ObjectFieldConstants.BUSINESS_TYPE_TEXT, null, null,
			ObjectFieldConstants.DB_TYPE_STRING, false, false,
			LocalizedMapUtil.getLocalizedMap("Able"), false, "able", false);

		_assertSystemObjectField(
			"able_", false, false, LocalizedMapUtil.getLocalizedMap("Able"),
			false, systemObjectField);
		_assertSystemObjectField(
			"able_", true, true, LocalizedMapUtil.getLocalizedMap("Baker"),
			true,
			_addOrUpdateSystemObjectField(
				systemObjectField.getExternalReferenceCode(),
				modifiableSystemObjectDefinition.getObjectDefinitionId(),
				ObjectFieldConstants.BUSINESS_TYPE_TEXT,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				ObjectFieldConstants.DB_TYPE_STRING, true, true,
				LocalizedMapUtil.getLocalizedMap("Baker"), false, "able",
				true));

		ObjectField localizedSystemObjectField = _addOrUpdateSystemObjectField(
			null, modifiableSystemObjectDefinition.getObjectDefinitionId(),
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ObjectFieldConstants.DB_TYPE_STRING, false, false,
			LocalizedMapUtil.getLocalizedMap("Charlie"), true, "charlie",
			false);

		Assert.assertTrue(localizedSystemObjectField.isLocalized());

		_objectDefinitionLocalService.publishSystemObjectDefinition(
			TestPropsValues.getUserId(),
			modifiableSystemObjectDefinition.getObjectDefinitionId());

		// Requests from forbidden bundles can only update the label

		String liferayMode = SystemProperties.get("liferay.mode");

		SystemProperties.clear("liferay.mode");

		try {
			_assertSystemObjectField(
				"able_", true, true, LocalizedMapUtil.getLocalizedMap("Dog"),
				true,
				_addOrUpdateSystemObjectField(
					systemObjectField.getExternalReferenceCode(),
					modifiableSystemObjectDefinition.getObjectDefinitionId(),
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					ObjectFieldConstants.DB_TYPE_STRING, false, false,
					LocalizedMapUtil.getLocalizedMap("Dog"), false, "able",
					false));
		}
		finally {
			SystemProperties.set("liferay.mode", liferayMode);
		}

		_objectDefinitionLocalService.deleteObjectDefinition(
			modifiableSystemObjectDefinition.getObjectDefinitionId());
	}

	@Test
	public void testAddSystemObjectField() throws Exception {
		List<ObjectFieldBusinessType> objectFieldBusinessTypes =
			_objectFieldBusinessTypeRegistry.getObjectFieldBusinessTypes();

		for (ObjectFieldBusinessType objectFieldBusinessType :
				objectFieldBusinessTypes) {

			if (Objects.equals(
					objectFieldBusinessType.getName(),
					ObjectFieldConstants.BUSINESS_TYPE_ENCRYPTED) ||
				Objects.equals(
					objectFieldBusinessType.getName(),
					ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST) ||
				Objects.equals(
					objectFieldBusinessType.getName(),
					ObjectFieldConstants.BUSINESS_TYPE_PICKLIST)) {

				continue;
			}

			_addUnmodifiableSystemObjectDefinition(
				ObjectFieldUtil.createObjectField(
					objectFieldBusinessType.getName(), StringPool.BLANK, "Able",
					"able"));
		}

		AssertUtils.assertFailure(
			ObjectFieldBusinessTypeException.class,
			"Invalid business type businessType",
			() -> _addUnmodifiableSystemObjectDefinition(
				ObjectFieldUtil.createObjectField(
					"businessType", StringPool.BLANK, "Able", "able")));

		for (String dbType :
				_objectFieldBusinessTypeRegistry.getObjectFieldDBTypes()) {

			_addUnmodifiableSystemObjectDefinition(
				ObjectFieldUtil.createObjectField(
					StringPool.BLANK, dbType, "Able", "able"));
		}

		AssertUtils.assertFailure(
			ObjectFieldDBTypeException.class, "Blob type is not indexable",
			() -> _addUnmodifiableSystemObjectDefinition(
				ObjectFieldUtil.createObjectField(
					0, ObjectFieldConstants.BUSINESS_TYPE_LARGE_FILE, null,
					ObjectFieldConstants.DB_TYPE_BLOB, true, false, "", "",
					"able", false, true)));

		String errorMessage =
			"Indexed language ID can only be applied with type \"Clob\" or " +
				"\"String\" that is not indexed as a keyword";

		AssertUtils.assertFailure(
			ObjectFieldDBTypeException.class, errorMessage,
			() -> _addUnmodifiableSystemObjectDefinition(
				ObjectFieldUtil.createObjectField(
					0, ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER, null,
					ObjectFieldConstants.DB_TYPE_LONG, true, false, "en_US", "",
					"able", false, true)));
		AssertUtils.assertFailure(
			ObjectFieldDBTypeException.class, errorMessage,
			() -> _addUnmodifiableSystemObjectDefinition(
				ObjectFieldUtil.createObjectField(
					0, ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER, null,
					ObjectFieldConstants.DB_TYPE_LONG, true, true, "en_US", "",
					"able", false, true)));
		AssertUtils.assertFailure(
			ObjectFieldDBTypeException.class, errorMessage,
			() -> _addUnmodifiableSystemObjectDefinition(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT, null,
					ObjectFieldConstants.DB_TYPE_STRING, true, true, "en_US",
					"", 0, "able", Collections.emptyList(),
					ObjectFieldConstants.READ_ONLY_FALSE, null, false, true)));

		AssertUtils.assertFailure(
			ObjectFieldDBTypeException.class, "Invalid DB type STRING",
			() -> _addUnmodifiableSystemObjectDefinition(
				ObjectFieldUtil.createObjectField(
					StringPool.BLANK, "STRING", "Able", "able")));
		AssertUtils.assertFailure(
			ObjectFieldLabelException.class,
			"Label is null for locale " + LocaleUtil.US.getDisplayName(),
			() -> _addUnmodifiableSystemObjectDefinition(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, "", "able")));

		_addUnmodifiableSystemObjectDefinition(
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_TEXT,
				ObjectFieldConstants.DB_TYPE_STRING, " able "));
		_addUnmodifiableSystemObjectDefinition(
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_TEXT,
				ObjectFieldConstants.DB_TYPE_STRING,
				"a123456789a123456789a123456789a1234567891"));

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				false,
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, "Able", "able")));

		AssertUtils.assertFailure(
			ObjectFieldNameException.MustBeLessThan41Characters.class,
			"Name must be less than 41 characters",
			() -> _addUnmodifiableSystemObjectDefinition(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING,
					"a123456789a123456789a123456789a12345678912")));
		AssertUtils.assertFailure(
			ObjectFieldNameException.MustBeginWithLowerCaseLetter.class,
			"The first character of a name must be a lower case letter",
			() -> _addUnmodifiableSystemObjectDefinition(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, "Able")));
		AssertUtils.assertFailure(
			ObjectFieldNameException.MustNotBeDuplicate.class,
			"Duplicate name able",
			() -> _addSystemObjectField(
				objectDefinition.getObjectDefinitionId(), "able", null));
		AssertUtils.assertFailure(
			ObjectFieldNameException.MustNotBeNull.class, "Name is null",
			() -> _addUnmodifiableSystemObjectDefinition(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, "Able", "")));

		String objectDefinitionName = ObjectDefinitionTestUtil.getRandomName();

		String pkObjectFieldName = TextFormatter.format(
			objectDefinitionName + "Id", TextFormatter.I);

		AssertUtils.assertFailure(
			ObjectFieldNameException.MustNotBeReserved.class,
			"Reserved name " + pkObjectFieldName,
			() -> _addUnmodifiableSystemObjectDefinition(
				objectDefinitionName,
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, pkObjectFieldName)));

		AssertUtils.assertFailure(
			ObjectFieldNameException.MustOnlyContainLettersAndDigits.class,
			"Name must only contain letters and digits",
			() -> _addUnmodifiableSystemObjectDefinition(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, "abl e")));
		AssertUtils.assertFailure(
			ObjectFieldNameException.MustOnlyContainLettersAndDigits.class,
			"Name must only contain letters and digits",
			() -> _addUnmodifiableSystemObjectDefinition(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, "abl-e")));

		ObjectDefinition modifiableSystemObjectDefinition =
			ObjectDefinitionTestUtil.addModifiableSystemObjectDefinition(
				TestPropsValues.getUserId(), null, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test" + RandomTestUtil.randomString(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionConstants.SCOPE_SITE, null, 1,
				Collections.emptyList());

		AssertUtils.assertFailure(
			ObjectFieldSystemException.class, false,
			"Only allowed bundles can add system object fields",
			() -> _addSystemObjectField(
				modifiableSystemObjectDefinition.getObjectDefinitionId(),
				"able", Collections.emptyList()));

		_objectDefinitionLocalService.deleteObjectDefinition(
			modifiableSystemObjectDefinition);
	}

	@Test
	public void testDeleteObjectField() throws Exception {

		// Delete metadata object field

		ObjectDefinition customObjectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				false,
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, "able")));

		ObjectField externalReferenceCodeObjectField =
			_objectFieldLocalService.fetchObjectField(
				customObjectDefinition.getObjectDefinitionId(),
				"externalReferenceCode");

		AssertUtils.assertFailure(
			RequiredObjectFieldException.class,
			String.format(
				"The object field \"%s\" cannot be deleted",
				externalReferenceCodeObjectField.getName()),
			() -> _objectFieldLocalService.deleteObjectField(
				externalReferenceCodeObjectField));

		// Delete object field from custom object definition

		_assertDeleteObjectField(false, customObjectDefinition, "able");

		ObjectField ableObjectField = _addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"able"
			).objectDefinitionId(
				customObjectDefinition.getObjectDefinitionId()
			).build());

		Assert.assertFalse(
			_hasColumn(customObjectDefinition.getDBTableName(), "able_"));

		customObjectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				customObjectDefinition.getObjectDefinitionId());

		AssertUtils.assertFailure(
			RequiredObjectFieldException.class,
			String.format(
				"The object field \"%s\" cannot be deleted because it is the " +
					"only custom object field of the published object " +
						"definition",
				ableObjectField.getName()),
			() -> _objectFieldLocalService.deleteObjectField(ableObjectField));

		Assert.assertTrue(
			_hasColumn(ableObjectField.getDBTableName(), "able_"));

		_addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"baker"
			).objectDefinitionId(
				customObjectDefinition.getObjectDefinitionId()
			).build());

		_assertDeleteObjectField(true, customObjectDefinition, "able");

		_addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"charlie"
			).objectDefinitionId(
				customObjectDefinition.getObjectDefinitionId()
			).objectFieldSettings(
				Collections.emptyList()
			).build());

		_assertDeleteObjectField(true, customObjectDefinition, "charlie");

		// Delete object field business type attachment

		ObjectField attachmentObjectField = _addCustomObjectField(
			new AttachmentObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"upload"
			).objectDefinitionId(
				customObjectDefinition.getObjectDefinitionId()
			).objectFieldSettings(
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
						ObjectFieldSettingConstants.VALUE_USER_COMPUTER
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
					).value(
						"100"
					).build())
			).build());

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			TestPropsValues.getUserId(), 0,
			customObjectDefinition.getObjectDefinitionId(), null,
			HashMapBuilder.<String, Serializable>put(
				"upload",
				() -> {
					FileEntry fileEntry = TempFileEntryUtil.addTempFileEntry(
						TestPropsValues.getGroupId(),
						TestPropsValues.getUserId(), StringUtil.randomString(),
						TempFileEntryUtil.getTempFileName(
							StringUtil.randomString() + ".txt"),
						FileUtil.createTempFile(RandomTestUtil.randomBytes()),
						ContentTypes.TEXT_PLAIN);

					return fileEntry.getFileEntryId();
				}
			).build(),
			ServiceContextTestUtil.getServiceContext());

		long persistedFileEntryId = MapUtil.getLong(
			objectEntry.getValues(), "upload");

		Assert.assertNotNull(
			_dlAppLocalService.getFileEntry(persistedFileEntryId));

		_objectFieldLocalService.deleteObjectField(
			attachmentObjectField.getObjectFieldId());

		AssertUtils.assertFailure(
			NoSuchFileEntryException.class,
			StringBundler.concat(
				"No FileEntry exists with the key {fileEntryId=",
				persistedFileEntryId, "}"),
			() -> _dlAppLocalService.getFileEntry(persistedFileEntryId));

		// Delete object field business type auto increment

		ObjectField autoIncrementObjectField = _addCustomObjectField(
			new AutoIncrementObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"autoIncrement"
			).objectDefinitionId(
				customObjectDefinition.getObjectDefinitionId()
			).objectFieldSettings(
				Collections.singletonList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_INITIAL_VALUE
					).value(
						"0123"
					).build())
			).build());

		Assert.assertTrue(
			_hasColumn(
				autoIncrementObjectField.getDBTableName(),
				autoIncrementObjectField.getDBColumnName()));
		Assert.assertTrue(
			_hasColumn(
				autoIncrementObjectField.getDBTableName(),
				autoIncrementObjectField.getSortableDBColumnName()));

		_objectFieldLocalService.deleteObjectField(
			autoIncrementObjectField.getObjectFieldId());

		Assert.assertFalse(
			_hasColumn(
				autoIncrementObjectField.getDBTableName(),
				autoIncrementObjectField.getDBColumnName()));
		Assert.assertFalse(
			_hasColumn(
				autoIncrementObjectField.getDBTableName(),
				autoIncrementObjectField.getSortableDBColumnName()));

		//  Delete object field from system object definition

		ObjectDefinition systemObjectDefinition =
			_addUnmodifiableSystemObjectDefinition(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, "able",
					Collections.emptyList()));

		ObjectField ableSystemObjectField =
			_objectFieldLocalService.fetchObjectField(
				systemObjectDefinition.getObjectDefinitionId(), "able");

		Assert.assertFalse(
			_hasColumn(ableSystemObjectField.getDBTableName(), "able_"));

		AssertUtils.assertFailure(
			RequiredObjectFieldException.class,
			String.format(
				"The object field \"%s\" cannot be deleted",
				ableSystemObjectField.getName()),
			() -> _objectFieldLocalService.deleteObjectField(
				ableSystemObjectField));

		_addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"baker"
			).objectDefinitionId(
				systemObjectDefinition.getObjectDefinitionId()
			).build());

		_assertDeleteObjectField(true, systemObjectDefinition, "baker");

		_objectDefinitionLocalService.deleteObjectDefinition(
			customObjectDefinition);
		_objectDefinitionLocalService.deleteObjectDefinition(
			systemObjectDefinition);

		// Delete system object field from modifiable system object definition

		ObjectDefinition modifiableSystemObjectDefinition =
			ObjectDefinitionTestUtil.addModifiableSystemObjectDefinition(
				TestPropsValues.getUserId(), null, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test" + RandomTestUtil.randomString(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionConstants.SCOPE_SITE, null, 1,
				Collections.emptyList());

		ObjectField systemObjectField1 = _addSystemObjectField(
			modifiableSystemObjectDefinition.getObjectDefinitionId(),
			StringUtil.randomId(), Collections.emptyList());

		AssertUtils.assertFailure(
			ObjectFieldSystemException.class, false,
			"Only allowed bundles can delete system object fields",
			() -> _objectFieldLocalService.deleteObjectField(
				systemObjectField1));

		_assertDeleteObjectField(
			false, modifiableSystemObjectDefinition,
			systemObjectField1.getName());

		ObjectField systemObjectField2 = _addSystemObjectField(
			modifiableSystemObjectDefinition.getObjectDefinitionId(),
			StringUtil.randomId(), Collections.emptyList());

		modifiableSystemObjectDefinition =
			_objectDefinitionLocalService.publishSystemObjectDefinition(
				TestPropsValues.getUserId(),
				modifiableSystemObjectDefinition.getObjectDefinitionId());

		AssertUtils.assertFailure(
			RequiredObjectFieldException.class,
			String.format(
				"The object field \"%s\" cannot be deleted because it is the " +
					"only custom object field of the published object " +
						"definition",
				systemObjectField2.getName()),
			() -> _objectFieldLocalService.deleteObjectField(
				systemObjectField2));

		_addSystemObjectField(
			modifiableSystemObjectDefinition.getObjectDefinitionId(), "able",
			Collections.emptyList());

		_assertDeleteObjectField(
			true, modifiableSystemObjectDefinition, "able");

		_objectDefinitionLocalService.deleteObjectDefinition(
			modifiableSystemObjectDefinition);
	}

	@Test
	public void testObjectFieldSettings() throws Exception {

		// Business type attachment

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				false,
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, "text",
						StringUtil.randomId())));

		ObjectField attachmentObjectField = _addCustomObjectField(
			new AttachmentObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"upload"
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).objectFieldSettings(
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.
							NAME_ACCEPTED_FILE_EXTENSIONS
					).value(
						"jpg, png"
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_FILE_SOURCE
					).value(
						ObjectFieldSettingConstants.VALUE_USER_COMPUTER
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
					).value(
						"100"
					).build())
			).build());

		_assertObjectFieldSettingsValues(
			attachmentObjectField.getObjectFieldId(),
			HashMapBuilder.put(
				ObjectFieldSettingConstants.NAME_ACCEPTED_FILE_EXTENSIONS,
				"jpg, png"
			).put(
				ObjectFieldSettingConstants.NAME_FILE_SOURCE,
				ObjectFieldSettingConstants.VALUE_USER_COMPUTER
			).put(
				ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE, "100"
			).build());

		_addOrUpdateCustomObjectField(
			attachmentObjectField,
			Arrays.asList(
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_ACCEPTED_FILE_EXTENSIONS
				).value(
					"jpg"
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
					"10"
				).build()));

		_assertObjectFieldSettingsValues(
			attachmentObjectField.getObjectFieldId(),
			HashMapBuilder.put(
				ObjectFieldSettingConstants.NAME_ACCEPTED_FILE_EXTENSIONS, "jpg"
			).put(
				ObjectFieldSettingConstants.NAME_FILE_SOURCE,
				ObjectFieldSettingConstants.VALUE_DOCS_AND_MEDIA
			).put(
				ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE, "10"
			).build());

		// Business type auto increment

		ObjectField autoIncrementObjectField = _addCustomObjectField(
			_getAutoIncrementObjectField(
				"1", objectDefinition.getObjectDefinitionId(), "LPS-", null,
				false, "-private"));

		_assertObjectFieldSettingsValues(
			autoIncrementObjectField.getObjectFieldId(),
			HashMapBuilder.put(
				ObjectFieldSettingConstants.NAME_INITIAL_VALUE, "1"
			).put(
				ObjectFieldSettingConstants.NAME_PREFIX, "LPS-"
			).put(
				ObjectFieldSettingConstants.NAME_SUFFIX, "-private"
			).build());

		_addOrUpdateCustomObjectField(
			autoIncrementObjectField,
			Arrays.asList(
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_INITIAL_VALUE
				).value(
					"2"
				).build(),
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_PREFIX
				).value(
					"PTR-"
				).build()));

		_assertObjectFieldSettingsValues(
			autoIncrementObjectField.getObjectFieldId(),
			HashMapBuilder.put(
				ObjectFieldSettingConstants.NAME_INITIAL_VALUE, "2"
			).put(
				ObjectFieldSettingConstants.NAME_PREFIX, "PTR-"
			).build());

		Assert.assertNull(
			_objectFieldSettingLocalService.fetchObjectFieldSetting(
				autoIncrementObjectField.getObjectFieldId(),
				ObjectFieldSettingConstants.NAME_SUFFIX));

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());

		AssertUtils.assertFailure(
			ObjectFieldSettingValueException.UnmodifiableValue.class,
			"The value of setting \"initialValue\" is unmodifiable when " +
				"object definition is published",
			() -> _addOrUpdateCustomObjectField(
				autoIncrementObjectField,
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_INITIAL_VALUE
					).value(
						"3"
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_PREFIX
					).value(
						"PTR-"
					).build())));
		AssertUtils.assertFailure(
			ObjectFieldSettingValueException.UnmodifiableValue.class,
			"The value of setting \"prefix\" is unmodifiable when object " +
				"definition is published",
			() -> _addOrUpdateCustomObjectField(
				autoIncrementObjectField,
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_INITIAL_VALUE
					).value(
						"2"
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_PREFIX
					).value(
						"LPP-"
					).build())));
		AssertUtils.assertFailure(
			ObjectFieldSettingValueException.UnmodifiableValue.class,
			"The value of setting \"suffix\" is unmodifiable when object " +
				"definition is published",
			() -> _addOrUpdateCustomObjectField(
				autoIncrementObjectField,
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_INITIAL_VALUE
					).value(
						"2"
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_PREFIX
					).value(
						"PTR-"
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_SUFFIX
					).value(
						"-private"
					).build())));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		// Business type integer

		objectDefinition = ObjectDefinitionTestUtil.addCustomObjectDefinition(
			false, Collections.emptyList());

		ObjectField integerObjectField = _addCustomObjectField(
			_getIntegerObjectField(
				objectDefinition.getObjectDefinitionId(),
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_UNIQUE_VALUES
					).value(
						"TRUE"
					).build())));

		_assertObjectFieldSettingsValues(
			integerObjectField.getObjectFieldId(),
			HashMapBuilder.put(
				ObjectFieldSettingConstants.NAME_UNIQUE_VALUES, "TRUE"
			).build());

		_addOrUpdateCustomObjectField(
			integerObjectField,
			Arrays.asList(
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_UNIQUE_VALUES
				).value(
					"False"
				).build()));

		_assertObjectFieldSettingsValues(
			integerObjectField.getObjectFieldId(),
			HashMapBuilder.put(
				ObjectFieldSettingConstants.NAME_UNIQUE_VALUES, "False"
			).build());

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());

		AssertUtils.assertFailure(
			ObjectFieldSettingValueException.UnmodifiableValue.class,
			"The value of setting \"uniqueValues\" is unmodifiable when " +
				"object definition is published",
			() -> _addOrUpdateCustomObjectField(
				integerObjectField,
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_UNIQUE_VALUES
					).value(
						"true"
					).build())));

		// Business type picklist

		ObjectField picklistObjectField = _addPicklistObjectField(
			objectDefinition, false, false);

		_assertObjectFieldSettingsValues(
			picklistObjectField.getObjectFieldId(),
			HashMapBuilder.put(
				ObjectFieldSettingConstants.NAME_DEFAULT_VALUE,
				_listTypeEntryKey
			).put(
				ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE,
				ObjectFieldSettingConstants.VALUE_INPUT_AS_VALUE
			).build());

		_assertObjectEntryDefaultValue(
			_listTypeEntryKey, picklistObjectField, new HashMap<>());

		_addOrUpdateCustomObjectField(
			picklistObjectField,
			Arrays.asList(
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_DEFAULT_VALUE
				).value(
					"text"
				).build(),
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE
				).value(
					ObjectFieldSettingConstants.VALUE_EXPRESSION_BUILDER
				).build()));

		_assertObjectFieldSettingsValues(
			picklistObjectField.getObjectFieldId(),
			HashMapBuilder.put(
				ObjectFieldSettingConstants.NAME_DEFAULT_VALUE, "text"
			).put(
				ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE,
				ObjectFieldSettingConstants.VALUE_EXPRESSION_BUILDER
			).build());

		_assertObjectEntryDefaultValue(
			_listTypeEntryKey, picklistObjectField,
			HashMapBuilder.<String, Serializable>put(
				"text", _listTypeEntryKey
			).build());

		picklistObjectField = _addPicklistObjectField(
			objectDefinition, true, true);

		_assertObjectFieldSettingsValues(
			picklistObjectField.getObjectFieldId(),
			HashMapBuilder.put(
				ObjectFieldSettingConstants.NAME_DEFAULT_VALUE,
				_listTypeEntryKey
			).put(
				ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE,
				ObjectFieldSettingConstants.VALUE_INPUT_AS_VALUE
			).build());

		_assertObjectEntryDefaultValue(
			_listTypeEntryKey, picklistObjectField, new HashMap<>());

		// Business type text

		ObjectField textObjectField = _addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"a" + RandomTestUtil.randomString()
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).objectFieldSettings(
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_SHOW_COUNTER
					).value(
						"false"
					).build())
			).build());

		Assert.assertNull(
			_objectFieldSettingLocalService.fetchObjectFieldSetting(
				textObjectField.getObjectFieldId(),
				ObjectFieldSettingConstants.NAME_ACCEPTED_FILE_EXTENSIONS));
		Assert.assertNull(
			_objectFieldSettingLocalService.fetchObjectFieldSetting(
				textObjectField.getObjectFieldId(),
				ObjectFieldSettingConstants.NAME_FILE_SOURCE));
		Assert.assertNull(
			_objectFieldSettingLocalService.fetchObjectFieldSetting(
				textObjectField.getObjectFieldId(),
				ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE));

		_addOrUpdateCustomObjectField(
			textObjectField,
			Arrays.asList(
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_MAX_LENGTH
				).value(
					"10"
				).build(),
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_SHOW_COUNTER
				).value(
					"true"
				).build()));

		_assertObjectFieldSettingsValues(
			textObjectField.getObjectFieldId(),
			HashMapBuilder.put(
				ObjectFieldSettingConstants.NAME_MAX_LENGTH, "10"
			).put(
				ObjectFieldSettingConstants.NAME_SHOW_COUNTER, "true"
			).build());

		_addOrUpdateCustomObjectField(
			textObjectField,
			Arrays.asList(
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_SHOW_COUNTER
				).value(
					"false"
				).build()));

		Assert.assertNull(
			_objectFieldSettingLocalService.fetchObjectFieldSetting(
				textObjectField.getObjectFieldId(),
				ObjectFieldSettingConstants.NAME_MAX_LENGTH));

		_assertObjectFieldSettingsValues(
			textObjectField.getObjectFieldId(),
			HashMapBuilder.put(
				ObjectFieldSettingConstants.NAME_SHOW_COUNTER, "false"
			).build());

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Test
	public void testUpdateRequired() throws Exception {

		// Deletion type cascade

		ObjectDefinition objectDefinition1 = _publishCustomObjectDefinition();
		ObjectDefinition objectDefinition2 = _publishCustomObjectDefinition();

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				objectDefinition1.getObjectDefinitionId(),
				objectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		ObjectField objectField = _objectFieldLocalService.updateRequired(
			objectRelationship.getObjectFieldId2(), true);

		Assert.assertTrue(objectField.isRequired());

		// Deletion type disassociate

		_objectRelationshipLocalService.updateObjectRelationship(
			objectRelationship.getExternalReferenceCode(),
			objectRelationship.getObjectRelationshipId(),
			objectRelationship.getParameterObjectFieldId(),
			ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE, false,
			objectRelationship.getLabelMap(), null);

		objectField = _objectFieldLocalService.fetchObjectField(
			objectRelationship.getObjectFieldId2());

		Assert.assertFalse(objectField.isRequired());

		AssertUtils.assertFailure(
			PortalException.class,
			"Object field cannot be required because the relationship " +
				"deletion type is disassociate",
			() -> _objectFieldLocalService.updateRequired(
				objectRelationship.getObjectFieldId2(), true));

		// Deletion type prevent

		_objectRelationshipLocalService.updateObjectRelationship(
			objectRelationship.getExternalReferenceCode(),
			objectRelationship.getObjectRelationshipId(),
			objectRelationship.getParameterObjectFieldId(),
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
			objectRelationship.getLabelMap(), null);

		objectField = _objectFieldLocalService.updateRequired(
			objectRelationship.getObjectFieldId2(), true);

		Assert.assertTrue(objectField.isRequired());

		_objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship);

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition1);
		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition2);
	}

	private void _addCustomObjectDefinitionWithAutoIncrementObjectField(
			String initialValue, String prefix, String readOnly,
			boolean required, String suffix)
		throws Exception {

		ObjectDefinitionTestUtil.addCustomObjectDefinition(
			Collections.singletonList(
				_getAutoIncrementObjectField(
					initialValue, 0, prefix, readOnly, required, suffix)));
	}

	private void _addCustomObjectDefinitionWithEncryptedObjectField(
			String algorithm, boolean enabled, String key, String storageType)
		throws Exception {

		ObjectFieldTestUtil.withEncryptedObjectFieldProperties(
			algorithm, enabled, key,
			() -> _objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, false, true, true,
				false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY, storageType,
				Collections.emptyList(),
				Arrays.asList(
					new EncryptedObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"a" + RandomTestUtil.randomString()
					).build())));
	}

	private void _addCustomObjectDefinitionWithPicklistObjectField(
			String defaultValue, String defaultValueType, boolean required,
			boolean state)
		throws Exception {

		ObjectDefinitionTestUtil.addCustomObjectDefinition(
			Collections.singletonList(
				new PicklistObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).listTypeDefinitionId(
					_listTypeDefinition.getListTypeDefinitionId()
				).name(
					"picklist"
				).objectFieldSettings(
					Arrays.asList(
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_DEFAULT_VALUE
						).value(
							defaultValue
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE
						).value(
							defaultValueType
						).build())
				).required(
					required
				).state(
					state
				).build()));
	}

	private void _addCustomObjectDefinitionWithTextObjectField(
			String maxLength, String showCounter, String uniqueValues)
		throws Exception {

		ObjectDefinitionTestUtil.addCustomObjectDefinition(
			Collections.singletonList(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"text"
				).objectFieldSettings(
					Arrays.asList(
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_MAX_LENGTH
						).value(
							maxLength
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_SHOW_COUNTER
						).value(
							showCounter
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_UNIQUE_VALUES
						).value(
							uniqueValues
						).build())
				).build()));
	}

	private ObjectField _addCustomObjectField(ObjectField objectField)
		throws Exception {

		return _objectFieldLocalService.addCustomObjectField(
			objectField.getExternalReferenceCode(), TestPropsValues.getUserId(),
			objectField.getListTypeDefinitionId(),
			objectField.getObjectDefinitionId(), objectField.getBusinessType(),
			objectField.getDBType(), objectField.isIndexed(),
			objectField.isIndexedAsKeyword(),
			objectField.getIndexedLanguageId(), objectField.getLabelMap(),
			objectField.isLocalized(), objectField.getName(),
			objectField.getReadOnly(),
			objectField.getReadOnlyConditionExpression(),
			objectField.isRequired(), objectField.isState(),
			objectField.getObjectFieldSettings());
	}

	private ObjectField _addOrUpdateCustomObjectField(ObjectField objectField)
		throws Exception {

		return _addOrUpdateCustomObjectField(
			objectField, Collections.emptyList());
	}

	private ObjectField _addOrUpdateCustomObjectField(
			ObjectField objectField,
			List<ObjectFieldSetting> objectFieldSettings)
		throws Exception {

		return _objectFieldLocalService.addOrUpdateCustomObjectField(
			objectField.getExternalReferenceCode(),
			objectField.getObjectFieldId(), TestPropsValues.getUserId(),
			objectField.getListTypeDefinitionId(),
			objectField.getObjectDefinitionId(), objectField.getBusinessType(),
			objectField.getDBType(), objectField.isIndexed(),
			objectField.isIndexedAsKeyword(),
			objectField.getIndexedLanguageId(), objectField.getLabelMap(),
			objectField.isLocalized(), objectField.getName(),
			objectField.getReadOnly(),
			objectField.getReadOnlyConditionExpression(),
			objectField.isRequired(), objectField.isState(),
			objectFieldSettings);
	}

	private ObjectField _addOrUpdateSystemObjectField(
			String externalReferenceCode, long objectDefinitionId,
			String businessType, String dbColumnName, String dbTableName,
			String dbType, boolean indexed, boolean indexedAsKeyword,
			Map<Locale, String> labelMap, boolean localized, String name,
			boolean required)
		throws Exception {

		return _objectFieldLocalService.addOrUpdateSystemObjectField(
			externalReferenceCode, TestPropsValues.getUserId(), 0L,
			objectDefinitionId, businessType, dbColumnName, dbTableName, dbType,
			indexed, indexedAsKeyword, "", labelMap, localized, name,
			ObjectFieldConstants.READ_ONLY_FALSE, null, required, false,
			Collections.emptyList());
	}

	private ObjectField _addPicklistObjectField(
			ObjectDefinition objectDefinition, boolean required, boolean state)
		throws Exception {

		return _addCustomObjectField(
			new PicklistObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).listTypeDefinitionId(
				_listTypeDefinition.getListTypeDefinitionId()
			).name(
				"a" + RandomTestUtil.randomString()
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).objectFieldSettings(
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_DEFAULT_VALUE
					).value(
						_listTypeEntryKey
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE
					).value(
						ObjectFieldSettingConstants.VALUE_INPUT_AS_VALUE
					).build())
			).required(
				required
			).state(
				state
			).build());
	}

	private ObjectField _addReadOnlyAggregationObjectField(
			long objectDefinitionId, String readOnly,
			String readOnlyConditionExpression)
		throws Exception {

		return _addCustomObjectField(
			new AggregationObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"a" + RandomTestUtil.randomString()
			).objectDefinitionId(
				objectDefinitionId
			).objectFieldSettings(
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_FUNCTION
					).value(
						"MAX"
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_OBJECT_FIELD_NAME
					).value(
						"integer"
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.
							NAME_OBJECT_RELATIONSHIP_NAME
					).value(
						"oneToManyRelationshipName"
					).build())
			).readOnly(
				readOnly
			).readOnlyConditionExpression(
				readOnlyConditionExpression
			).build());
	}

	private ObjectField _addReadOnlyFormulaObjectField(
			long objectDefinitionId, String readOnly,
			String readOnlyConditionExpression)
		throws Exception {

		return _addCustomObjectField(
			new FormulaObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"a" + RandomTestUtil.randomString()
			).objectDefinitionId(
				objectDefinitionId
			).objectFieldSettings(
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						"script"
					).value(
						"weight + 10"
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						"output"
					).value(
						ObjectFieldConstants.BUSINESS_TYPE_DECIMAL
					).build())
			).readOnly(
				readOnly
			).readOnlyConditionExpression(
				readOnlyConditionExpression
			).build());
	}

	private ObjectField _addSystemObjectField(
			long objectDefinitionId, String name,
			List<ObjectFieldSetting> objectFieldSettings)
		throws Exception {

		return _objectFieldLocalService.addSystemObjectField(
			null, TestPropsValues.getUserId(), 0L, objectDefinitionId,
			ObjectFieldConstants.BUSINESS_TYPE_TEXT, null, null,
			ObjectFieldConstants.DB_TYPE_STRING, false, true, "",
			LocalizedMapUtil.getLocalizedMap(name), false, name,
			ObjectFieldConstants.READ_ONLY_FALSE, null, false, false,
			objectFieldSettings);
	}

	private ObjectDefinition _addUnmodifiableSystemObjectDefinition(
			ObjectField... objectFields)
		throws Exception {

		return _addUnmodifiableSystemObjectDefinition(
			ObjectDefinitionTestUtil.getRandomName(), objectFields);
	}

	private ObjectDefinition _addUnmodifiableSystemObjectDefinition(
			String objectDefinitionName, ObjectField... objectFields)
		throws Exception {

		return ObjectDefinitionTestUtil.addUnmodifiableSystemObjectDefinition(
			null, TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			objectDefinitionName, null, null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			ObjectDefinitionConstants.SCOPE_COMPANY, null, 1,
			Arrays.asList(objectFields));
	}

	private void _assertDeleteObjectField(
			boolean hasColumn, ObjectDefinition objectDefinition,
			String objectFieldName)
		throws Exception {

		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			objectDefinition.getObjectDefinitionId(), objectFieldName);

		Assert.assertEquals(
			hasColumn,
			_hasColumn(
				objectField.getDBTableName(), objectField.getDBColumnName()));

		_objectFieldLocalService.deleteObjectField(
			objectField.getObjectFieldId());

		Assert.assertNull(
			_objectFieldLocalService.fetchObjectField(
				objectDefinition.getObjectDefinitionId(), objectFieldName));

		Assert.assertFalse(
			_hasColumn(
				objectField.getDBTableName(), objectField.getDBColumnName()));
	}

	private void _assertObjectEntryDefaultValue(
			String expectedDefaultValue, ObjectField objectField,
			Map<String, Serializable> values)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			TestPropsValues.getUserId(), 0, objectField.getObjectDefinitionId(),
			null, values, ServiceContextTestUtil.getServiceContext());

		values = objectEntry.getValues();

		Assert.assertEquals(
			expectedDefaultValue, values.get(objectField.getName()));
	}

	private void _assertObjectFieldSettingsValues(
			long objectFieldId, Map<String, String> objectFieldSettingsValues)
		throws Exception {

		for (Map.Entry<String, String> entry :
				objectFieldSettingsValues.entrySet()) {

			ObjectFieldSetting objectFieldSetting =
				_objectFieldSettingLocalService.fetchObjectFieldSetting(
					objectFieldId, entry.getKey());

			Assert.assertEquals(
				entry.getValue(), objectFieldSetting.getValue());
		}
	}

	private void _assertReadOnly(
			String invalidDDMScript, String invalidReadOnly,
			ObjectField objectField)
		throws Exception {

		_updateReadOnlyObjectField(objectField, invalidReadOnly, null);

		_assertReadOnlyTrue(objectField);

		_updateReadOnlyObjectField(
			objectField, ObjectFieldConstants.READ_ONLY_CONDITIONAL,
			invalidDDMScript);

		_assertReadOnlyTrue(objectField);

		String validDDMScript = "isEmpty(able)";

		_updateReadOnlyObjectField(
			objectField, ObjectFieldConstants.READ_ONLY_CONDITIONAL,
			validDDMScript);

		_assertReadOnlyTrue(objectField);

		_updateReadOnlyObjectField(
			objectField, ObjectFieldConstants.READ_ONLY_FALSE, null);

		_assertReadOnlyTrue(objectField);
	}

	private void _assertReadOnlyFalse(ObjectField objectField) {
		Assert.assertEquals(
			ObjectFieldConstants.READ_ONLY_FALSE, objectField.getReadOnly());
		Assert.assertEquals(
			StringPool.BLANK, objectField.getReadOnlyConditionExpression());
	}

	private void _assertReadOnlyTrue(ObjectField objectField) {
		Assert.assertEquals(
			ObjectFieldConstants.READ_ONLY_TRUE, objectField.getReadOnly());
		Assert.assertEquals(
			StringPool.BLANK, objectField.getReadOnlyConditionExpression());
	}

	private void _assertSystemObjectField(
		String expectedDBColumnName, boolean expectedIndexed,
		boolean expectedIndexedAsKeyword, Map<Locale, String> expectedLabelMap,
		boolean expectedRequired, ObjectField systemObjectField) {

		Assert.assertEquals(
			expectedDBColumnName, systemObjectField.getDBColumnName());
		Assert.assertEquals(
			ObjectFieldConstants.DB_TYPE_STRING, systemObjectField.getDBType());
		Assert.assertEquals(expectedIndexed, systemObjectField.isIndexed());
		Assert.assertEquals(
			expectedIndexedAsKeyword, systemObjectField.isIndexedAsKeyword());
		Assert.assertEquals(
			StringPool.BLANK, systemObjectField.getIndexedLanguageId());
		Assert.assertEquals(expectedLabelMap, systemObjectField.getLabelMap());
		Assert.assertEquals("able", systemObjectField.getName());
		Assert.assertEquals(expectedRequired, systemObjectField.isRequired());
		Assert.assertTrue(systemObjectField.isSystem());
	}

	private ObjectField _getAutoIncrementObjectField(
		String initialValue, long objectDefinitionId, String prefix,
		String readOnly, boolean required, String suffix) {

		return new AutoIncrementObjectFieldBuilder(
		).labelMap(
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
		).name(
			"autoIncrement"
		).objectDefinitionId(
			objectDefinitionId
		).objectFieldSettings(
			Arrays.asList(
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_INITIAL_VALUE
				).value(
					initialValue
				).build(),
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_PREFIX
				).value(
					prefix
				).build(),
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_SUFFIX
				).value(
					suffix
				).build())
		).readOnly(
			readOnly
		).required(
			required
		).build();
	}

	private ObjectField _getIntegerObjectField(
		long objectDefinitionId, List<ObjectFieldSetting> objectFieldSettings) {

		return new IntegerObjectFieldBuilder(
		).labelMap(
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
		).name(
			"integer"
		).objectDefinitionId(
			objectDefinitionId
		).objectFieldSettings(
			objectFieldSettings
		).build();
	}

	private ObjectField _getReadOnlyTextObjectField(
		long objectDefinitionId, String readOnly,
		String readOnlyConditionExpression) {

		return new TextObjectFieldBuilder(
		).labelMap(
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
		).name(
			"a" + RandomTestUtil.randomString()
		).objectDefinitionId(
			objectDefinitionId
		).readOnly(
			readOnly
		).readOnlyConditionExpression(
			readOnlyConditionExpression
		).build();
	}

	private boolean _hasColumn(String tableName, String columnName)
		throws Exception {

		try (Connection connection = DataAccess.getConnection()) {
			DBInspector dbInspector = new DBInspector(connection);

			return dbInspector.hasColumn(tableName, columnName);
		}
	}

	private ObjectDefinition _publishCustomObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				false,
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())));

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

	private void _testAddCustomObjectFieldReadOnly() throws Exception {
		AssertUtils.assertFailure(
			ObjectFieldReadOnlyConditionExpressionException.class,
			"Read only condition expression is required",
			() -> ObjectDefinitionTestUtil.addCustomObjectDefinition(
				Arrays.asList(
					_getReadOnlyTextObjectField(
						0, ObjectFieldConstants.READ_ONLY_CONDITIONAL, null))));

		String invalidDDMScript = RandomTestUtil.randomString() + "()";

		AssertUtils.assertFailure(
			ObjectFieldReadOnlyConditionExpressionException.class,
			"Syntax error in: " + invalidDDMScript,
			() -> ObjectDefinitionTestUtil.addCustomObjectDefinition(
				Arrays.asList(
					_getReadOnlyTextObjectField(
						0, ObjectFieldConstants.READ_ONLY_CONDITIONAL,
						invalidDDMScript))));

		String invalidReadOnly = RandomTestUtil.randomString();

		AssertUtils.assertFailure(
			ObjectFieldReadOnlyException.class,
			"Unknown read only: " + invalidReadOnly,
			() -> ObjectDefinitionTestUtil.addCustomObjectDefinition(
				Arrays.asList(
					_getReadOnlyTextObjectField(0, invalidReadOnly, null))));

		ObjectDefinition objectDefinition1 =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		for (Map.Entry<String, String> entry :
				_readOnlyObjectFieldDBTypes.entrySet()) {

			_assertReadOnlyTrue(
				_objectFieldLocalService.getObjectField(
					objectDefinition1.getObjectDefinitionId(), entry.getKey()));

			ObjectField objectField = _objectFieldLocalService.getObjectField(
				objectDefinition1.getObjectDefinitionId(), entry.getKey());

			Assert.assertEquals(entry.getValue(), objectField.getDBType());
		}

		_assertReadOnlyFalse(
			_objectFieldLocalService.getObjectField(
				objectDefinition1.getObjectDefinitionId(),
				"externalReferenceCode"));
		_assertReadOnlyFalse(
			_addCustomObjectField(
				_getReadOnlyTextObjectField(
					objectDefinition1.getObjectDefinitionId(), null, null)));

		String validDDMScript = "isEmpty(able)";

		ObjectField objectField = _addCustomObjectField(
			_getReadOnlyTextObjectField(
				objectDefinition1.getObjectDefinitionId(),
				ObjectFieldConstants.READ_ONLY_CONDITIONAL, validDDMScript));

		Assert.assertEquals(
			objectField.getReadOnly(),
			ObjectFieldConstants.READ_ONLY_CONDITIONAL);
		Assert.assertEquals(
			validDDMScript, objectField.getReadOnlyConditionExpression());

		_assertReadOnlyFalse(
			_addCustomObjectField(
				_getReadOnlyTextObjectField(
					objectDefinition1.getObjectDefinitionId(),
					ObjectFieldConstants.READ_ONLY_FALSE, null)));
		_assertReadOnlyTrue(
			_addCustomObjectField(
				_getReadOnlyTextObjectField(
					objectDefinition1.getObjectDefinitionId(),
					ObjectFieldConstants.READ_ONLY_TRUE, null)));
		_assertReadOnlyTrue(
			_addReadOnlyFormulaObjectField(
				objectDefinition1.getObjectDefinitionId(),
				ObjectFieldConstants.READ_ONLY_CONDITIONAL, null));
		_assertReadOnlyTrue(
			_addReadOnlyFormulaObjectField(
				objectDefinition1.getObjectDefinitionId(),
				ObjectFieldConstants.READ_ONLY_CONDITIONAL, invalidDDMScript));
		_assertReadOnlyTrue(
			_addReadOnlyFormulaObjectField(
				objectDefinition1.getObjectDefinitionId(),
				ObjectFieldConstants.READ_ONLY_CONDITIONAL, validDDMScript));
		_assertReadOnlyTrue(
			_addReadOnlyFormulaObjectField(
				objectDefinition1.getObjectDefinitionId(),
				ObjectFieldConstants.READ_ONLY_FALSE, null));
		_assertReadOnlyTrue(
			_addReadOnlyFormulaObjectField(
				objectDefinition1.getObjectDefinitionId(),
				RandomTestUtil.randomString(), null));

		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				true,
				Arrays.asList(
					_getIntegerObjectField(0, Collections.emptyList())));

		_objectRelationshipLocalService.addObjectRelationship(
			null, TestPropsValues.getUserId(),
			objectDefinition1.getObjectDefinitionId(),
			objectDefinition2.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			"oneToManyRelationshipName", false,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		_assertReadOnlyTrue(
			_addReadOnlyAggregationObjectField(
				objectDefinition1.getObjectDefinitionId(),
				ObjectFieldConstants.READ_ONLY_CONDITIONAL, null));
		_assertReadOnlyTrue(
			_addReadOnlyAggregationObjectField(
				objectDefinition1.getObjectDefinitionId(),
				ObjectFieldConstants.READ_ONLY_CONDITIONAL, invalidDDMScript));
		_assertReadOnlyTrue(
			_addReadOnlyAggregationObjectField(
				objectDefinition1.getObjectDefinitionId(),
				ObjectFieldConstants.READ_ONLY_CONDITIONAL, validDDMScript));
		_assertReadOnlyTrue(
			_addReadOnlyAggregationObjectField(
				objectDefinition1.getObjectDefinitionId(),
				ObjectFieldConstants.READ_ONLY_FALSE, null));
		_assertReadOnlyTrue(
			_addReadOnlyAggregationObjectField(
				objectDefinition1.getObjectDefinitionId(),
				RandomTestUtil.randomString(), null));

		objectDefinition1 =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, false, true, true,
				false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test", null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_SALESFORCE,
				Collections.emptyList(), Collections.emptyList());

		_assertReadOnlyFalse(
			_addCustomObjectField(
				_getReadOnlyTextObjectField(
					objectDefinition1.getObjectDefinitionId(),
					ObjectFieldConstants.READ_ONLY_CONDITIONAL,
					validDDMScript)));
		_assertReadOnlyFalse(
			_addCustomObjectField(
				_getReadOnlyTextObjectField(
					objectDefinition1.getObjectDefinitionId(),
					ObjectFieldConstants.READ_ONLY_TRUE, null)));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition1);
	}

	private void _testAddOrUpdateCustomObjectField(
			ObjectField expectedObjectField)
		throws Exception {

		ObjectField objectField = _addOrUpdateCustomObjectField(
			expectedObjectField, expectedObjectField.getObjectFieldSettings());

		Assert.assertEquals(
			expectedObjectField.getExternalReferenceCode(),
			objectField.getExternalReferenceCode());
		Assert.assertEquals(
			expectedObjectField.getDBColumnName(),
			objectField.getDBColumnName());
		Assert.assertEquals(
			expectedObjectField.getDBType(), objectField.getDBType());
		Assert.assertEquals(
			expectedObjectField.isIndexed(), objectField.isIndexed());
		Assert.assertEquals(
			expectedObjectField.isIndexedAsKeyword(),
			objectField.isIndexedAsKeyword());
		Assert.assertEquals(
			expectedObjectField.getIndexedLanguageId(),
			objectField.getIndexedLanguageId());
		Assert.assertEquals(
			expectedObjectField.getLabelMap(), objectField.getLabelMap());
		Assert.assertEquals(
			expectedObjectField.getName(), objectField.getName());
		Assert.assertNotNull(objectField.getObjectFieldSettings());
		Assert.assertEquals(
			expectedObjectField.isRequired(), objectField.isRequired());
		Assert.assertEquals(
			expectedObjectField.isState(), objectField.isState());
	}

	private ObjectField _updateReadOnlyObjectField(
			ObjectField objectField, String readOnly,
			String readOnlyConditionExpression)
		throws PortalException {

		return _objectFieldLocalService.addOrUpdateCustomObjectField(
			objectField.getExternalReferenceCode(),
			objectField.getObjectFieldId(), TestPropsValues.getUserId(),
			objectField.getListTypeDefinitionId(),
			objectField.getObjectDefinitionId(), objectField.getBusinessType(),
			objectField.getDBType(), objectField.isIndexed(),
			objectField.isIndexedAsKeyword(),
			objectField.getIndexedLanguageId(), objectField.getLabelMap(),
			objectField.isLocalized(), objectField.getName(), readOnly,
			readOnlyConditionExpression, objectField.isRequired(),
			objectField.isState(), objectField.getObjectFieldSettings());
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@DeleteAfterTestRun
	private ListTypeDefinition _listTypeDefinition;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	private String _listTypeEntryKey;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldBusinessTypeRegistry _objectFieldBusinessTypeRegistry;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Inject
	private ObjectFilterLocalService _objectFilterLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	private final Map<String, String> _readOnlyObjectFieldDBTypes =
		HashMapBuilder.put(
			"createDate", ObjectFieldConstants.DB_TYPE_DATE
		).put(
			"creator", ObjectFieldConstants.DB_TYPE_STRING
		).put(
			"id", ObjectFieldConstants.DB_TYPE_LONG
		).put(
			"modifiedDate", ObjectFieldConstants.DB_TYPE_DATE
		).put(
			"status", ObjectFieldConstants.DB_TYPE_INTEGER
		).build();

}