/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.manager.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryService;
import com.liferay.document.library.kernel.service.DLFolderService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.list.type.entry.util.ListTypeEntryUtil;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.action.util.ObjectActionThreadLocal;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectFieldValidationConstants;
import com.liferay.object.constants.ObjectFilterConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.exception.NoSuchObjectEntryException;
import com.liferay.object.exception.ObjectDefinitionAccountEntryRestrictedException;
import com.liferay.object.exception.ObjectEntryDefaultLanguageIdException;
import com.liferay.object.exception.ObjectEntryValuesException;
import com.liferay.object.exception.ObjectRelationshipDeletionTypeException;
import com.liferay.object.exception.RequiredObjectEntryVersionException;
import com.liferay.object.exception.RequiredObjectRelationshipException;
import com.liferay.object.field.builder.AggregationObjectFieldBuilder;
import com.liferay.object.field.builder.AttachmentObjectFieldBuilder;
import com.liferay.object.field.builder.BooleanObjectFieldBuilder;
import com.liferay.object.field.builder.DateObjectFieldBuilder;
import com.liferay.object.field.builder.DateTimeObjectFieldBuilder;
import com.liferay.object.field.builder.DecimalObjectFieldBuilder;
import com.liferay.object.field.builder.FormulaObjectFieldBuilder;
import com.liferay.object.field.builder.IntegerObjectFieldBuilder;
import com.liferay.object.field.builder.LongIntegerObjectFieldBuilder;
import com.liferay.object.field.builder.LongTextObjectFieldBuilder;
import com.liferay.object.field.builder.MultiselectPicklistObjectFieldBuilder;
import com.liferay.object.field.builder.PicklistObjectFieldBuilder;
import com.liferay.object.field.builder.PrecisionDecimalObjectFieldBuilder;
import com.liferay.object.field.builder.RichTextObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.dto.v1_0.FileEntry;
import com.liferay.object.rest.dto.v1_0.Link;
import com.liferay.object.rest.dto.v1_0.ListEntry;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.dto.v1_0.Status;
import com.liferay.object.rest.dto.v1_0.SystemProperties;
import com.liferay.object.rest.dto.v1_0.Version;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.test.util.BaseObjectEntryManagerImplTestCase;
import com.liferay.object.rest.test.util.ObjectRelationshipTestUtil;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectFilterLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.TreeTestUtil;
import com.liferay.object.tree.Edge;
import com.liferay.object.tree.Node;
import com.liferay.object.tree.Tree;
import com.liferay.object.tree.constants.TreeConstants;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlParserUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.security.script.management.test.rule.ScriptManagementConfigurationTestRule;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.aggregation.Facet;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsContext;
import com.liferay.portal.vulcan.fields.NestedFieldsContextThreadLocal;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import java.math.BigDecimal;

import java.sql.Timestamp;

import java.text.DateFormat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.hamcrest.CoreMatchers;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

/**
 * @author Feliphe Marinho
 */
@FeatureFlags(
	featureFlags = {
		@FeatureFlag(value = "LPD-32050"), @FeatureFlag(value = "LPD-34594"),
		@FeatureFlag(value = "LPS-164801"), @FeatureFlag(value = "LPS-172017")
	}
)
@RunWith(Arquillian.class)
public class DefaultObjectEntryManagerImplTest
	extends BaseObjectEntryManagerImplTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			ScriptManagementConfigurationTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		companyId = TestPropsValues.getCompanyId();
		_defaultObjectEntryManager =
			(DefaultObjectEntryManager)_objectEntryManager;
		_group = GroupTestUtil.addGroup();
		_originalName = PrincipalThreadLocal.getName();
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		_simpleDateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd");

		adminUser = TestPropsValues.getUser();

		_simpleDTOConverterContext = new DefaultDTOConverterContext(
			false, Collections.emptyMap(), dtoConverterRegistry, null,
			LocaleUtil.getDefault(), null, adminUser);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(adminUser));

		PrincipalThreadLocal.setName(adminUser.getUserId());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		GroupLocalServiceUtil.deleteGroup(_group);

		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);

		PrincipalThreadLocal.setName(_originalName);
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		listTypeDefinition =
			listTypeDefinitionLocalService.addListTypeDefinition(
				null, adminUser.getUserId(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				false, Collections.emptyList());

		_listTypeEntryKey1 = _addListTypeEntry();
		_listTypeEntryKey2 = _addListTypeEntry();
		_listTypeEntryKey3 = _addListTypeEntry();
		_listTypeEntryKey4 = _addListTypeEntry();

		_localizedMultiselectPicklistObjectFieldName =
			"a" + RandomTestUtil.randomString();

		_localizedObjectFieldI18nValues = HashMapBuilder.<String, Object>put(
			_localizedMultiselectPicklistObjectFieldName + "_i18n",
			HashMapBuilder.put(
				"en_US", Arrays.asList(_listTypeEntryKey3, _listTypeEntryKey4)
			).put(
				"pt_BR", Arrays.asList(_listTypeEntryKey4)
			).build()
		).put(
			"localizedBooleanObjectFieldName_i18n",
			HashMapBuilder.put(
				"en_US", true
			).put(
				"pt_BR", false
			).build()
		).put(
			"localizedDateObjectFieldName_i18n",
			HashMapBuilder.put(
				"en_US", "2024-01-01"
			).put(
				"pt_BR", "2025-01-01"
			).build()
		).put(
			"localizedDateTimeObjectFieldName_i18n",
			HashMapBuilder.put(
				"en_US", "2024-04-04T04:04:04.000Z"
			).put(
				"pt_BR", "2025-05-05T05:05:05.000Z"
			).build()
		).put(
			"localizedDecimalObjectFieldName_i18n",
			HashMapBuilder.put(
				"en_US", 5.0
			).put(
				"pt_BR", 2.7
			).build()
		).put(
			"localizedIntegerObjectFieldName_i18n",
			HashMapBuilder.put(
				"en_US", 50
			).put(
				"pt_BR", 27
			).build()
		).put(
			"localizedLongIntegerObjectFieldName_i18n",
			HashMapBuilder.put(
				"en_US", 50L
			).put(
				"pt_BR", 27L
			).build()
		).put(
			"localizedLongTextObjectFieldName_i18n",
			HashMapBuilder.put(
				"en_US", "en_US localizedLongTextObjectFieldValue"
			).put(
				"pt_BR", "pt_BR localizedLongTextObjectFieldValue"
			).build()
		).put(
			"localizedPicklistObjectFieldName_i18n",
			HashMapBuilder.put(
				"en_US",
				HashMapBuilder.put(
					"key", _listTypeEntryKey1
				).build()
			).put(
				"pt_BR",
				HashMapBuilder.put(
					"key", _listTypeEntryKey2
				).build()
			).build()
		).put(
			"localizedPrecisionDecimalObjectFieldName_i18n",
			HashMapBuilder.put(
				"en_US", new BigDecimal("0.18775433729")
			).put(
				"pt_BR", new BigDecimal("0.03750624632")
			).build()
		).put(
			"localizedRichTextObjectFieldName_i18n",
			HashMapBuilder.put(
				"en_US", "en_US <i>localizedRichTextObjectFieldValue</i>"
			).put(
				"pt_BR", "pt_BR <i>localizedRichTextObjectFieldValue</i>"
			).build()
		).put(
			"localizedTextObjectFieldName_i18n",
			HashMapBuilder.put(
				"en_US", "en_US localizedTextObjectFieldValue1"
			).put(
				"pt_BR", "pt_BR localizedTextObjectFieldValue1"
			).build()
		).build();

		_objectDefinition1 = _createObjectDefinition(
			Arrays.asList(
				new TextObjectFieldBuilder(
				).indexed(
					true
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"textObjectFieldName"
				).build()));

		ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				adminUser.getUserId()
			).indexed(
				true
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"textObjectFieldNameExtension"
			).objectDefinitionId(
				_objectDefinition1.getObjectDefinitionId()
			).build());

		_objectDefinition2 = _createObjectDefinition(
			Arrays.asList(
				new AttachmentObjectFieldBuilder(
				).indexed(
					true
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"attachmentObjectFieldName"
				).objectFieldSettings(
					Arrays.asList(
						_createObjectFieldSetting(
							"acceptedFileExtensions", "txt"),
						_createObjectFieldSetting(
							"fileSource", "documentsAndMedia"),
						_createObjectFieldSetting("maximumFileSize", "100"))
				).readOnly(
					"conditional"
				).readOnlyConditionExpression(
					"attachmentObjectFieldName != \"\" "
				).build(),
				new BooleanObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).localized(
					true
				).name(
					"localizedBooleanObjectFieldName"
				).build(),
				new DateObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"dateObjectFieldName"
				).build(),
				new DateObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).localized(
					true
				).name(
					"localizedDateObjectFieldName"
				).build(),
				new DateTimeObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"dateTimeObjectFieldName"
				).objectFieldSettings(
					Collections.singletonList(
						_createObjectFieldSetting(
							ObjectFieldSettingConstants.NAME_TIME_STORAGE,
							ObjectFieldSettingConstants.
								VALUE_USE_INPUT_AS_ENTERED))
				).build(),
				new DateTimeObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"dateTimeUTCObjectFieldName"
				).objectFieldSettings(
					Collections.singletonList(
						_createObjectFieldSetting(
							ObjectFieldSettingConstants.NAME_TIME_STORAGE,
							ObjectFieldSettingConstants.VALUE_CONVERT_TO_UTC))
				).build(),
				new DateTimeObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).localized(
					true
				).name(
					"localizedDateTimeObjectFieldName"
				).objectFieldSettings(
					Collections.singletonList(
						_createObjectFieldSetting(
							ObjectFieldSettingConstants.NAME_TIME_STORAGE,
							ObjectFieldSettingConstants.
								VALUE_USE_INPUT_AS_ENTERED))
				).build(),
				new DecimalObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"decimalObjectFieldName"
				).build(),
				new DecimalObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).localized(
					true
				).name(
					"localizedDecimalObjectFieldName"
				).build(),
				new FormulaObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"formulaObjectFieldName"
				).objectFieldSettings(
					Arrays.asList(
						_createObjectFieldSetting("output", "Decimal"),
						_createObjectFieldSetting(
							"script",
							"integerObjectFieldName / " +
								"longIntegerObjectFieldName"))
				).build(),
				new IntegerObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"integerObjectFieldName"
				).build(),
				new IntegerObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).localized(
					true
				).name(
					"localizedIntegerObjectFieldName"
				).build(),
				new LongIntegerObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"longIntegerObjectFieldName"
				).build(),
				new LongIntegerObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).localized(
					true
				).name(
					"localizedLongIntegerObjectFieldName"
				).build(),
				new LongTextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).localized(
					true
				).name(
					"localizedLongTextObjectFieldName"
				).build(),
				new MultiselectPicklistObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).listTypeDefinitionId(
					listTypeDefinition.getListTypeDefinitionId()
				).localized(
					true
				).name(
					_localizedMultiselectPicklistObjectFieldName
				).build(),
				new MultiselectPicklistObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).listTypeDefinitionId(
					listTypeDefinition.getListTypeDefinitionId()
				).name(
					"multiselectPicklistObjectFieldName"
				).build(),
				new PicklistObjectFieldBuilder(
				).indexed(
					true
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).listTypeDefinitionId(
					listTypeDefinition.getListTypeDefinitionId()
				).localized(
					true
				).name(
					"localizedPicklistObjectFieldName"
				).build(),
				new PicklistObjectFieldBuilder(
				).indexed(
					true
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).listTypeDefinitionId(
					listTypeDefinition.getListTypeDefinitionId()
				).name(
					"picklistObjectFieldName"
				).readOnly(
					"conditional"
				).readOnlyConditionExpression(
					"picklistObjectFieldName != \"\" "
				).build(),
				new PrecisionDecimalObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"precisionDecimalObjectFieldName"
				).build(),
				new PrecisionDecimalObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).localized(
					true
				).name(
					"localizedPrecisionDecimalObjectFieldName"
				).build(),
				new RichTextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"richTextObjectFieldName"
				).build(),
				new RichTextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).localized(
					true
				).name(
					"localizedRichTextObjectFieldName"
				).build(),
				new TextObjectFieldBuilder(
				).indexed(
					true
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"textObjectFieldName"
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
					"localizedTextObjectFieldName"
				).build()));

		ObjectRelationship objectRelationship1 =
			_objectRelationshipLocalService.addObjectRelationship(
				null, adminUser.getUserId(),
				_objectDefinition1.getObjectDefinitionId(),
				_objectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"oneToManyRelationshipName", false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		_objectRelationshipName = objectRelationship1.getName();

		_addAggregationObjectField(
			"precisionDecimalObjectFieldName", "AVERAGE",
			_objectDefinition1.getObjectDefinitionId(),
			"averageAggregationObjectFieldName", _objectRelationshipName);
		_addAggregationObjectField(
			null, "COUNT", _objectDefinition1.getObjectDefinitionId(),
			"countAggregationObjectFieldName1", _objectRelationshipName);
		_addAggregationObjectField(
			"integerObjectFieldName", "MAX",
			_objectDefinition1.getObjectDefinitionId(),
			"maxAggregationObjectFieldName", _objectRelationshipName);
		_addAggregationObjectField(
			"longIntegerObjectFieldName", "MIN",
			_objectDefinition1.getObjectDefinitionId(),
			"minAggregationObjectFieldName", _objectRelationshipName);
		_addAggregationObjectField(
			"decimalObjectFieldName", "SUM",
			_objectDefinition1.getObjectDefinitionId(),
			"sumAggregationObjectFieldName", _objectRelationshipName);

		ObjectField objectField = objectFieldLocalService.getObjectField(
			objectRelationship1.getObjectFieldId2());

		_objectRelationshipERCObjectFieldName = ObjectFieldSettingUtil.getValue(
			ObjectFieldSettingConstants.
				NAME_OBJECT_RELATIONSHIP_ERC_OBJECT_FIELD_NAME,
			objectField);
		_objectRelationshipFieldName = objectField.getName();

		_objectDefinition3 =
			objectDefinitionLocalService.addCustomObjectDefinition(
				adminUser.getUserId(), 0, null, false, false, true, true, false,
				false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				ListUtil.fromArray(
					new TextObjectFieldBuilder(
					).indexed(
						true
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"textObjectFieldName1"
					).build(),
					new TextObjectFieldBuilder(
					).indexed(
						true
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"textObjectFieldName2"
					).build()));

		ObjectDefinition accountEntryObjectDefinition =
			objectDefinitionLocalService.fetchObjectDefinition(
				companyId, "AccountEntry");

		ObjectRelationship objectRelationship2 =
			_objectRelationshipLocalService.addObjectRelationship(
				null, adminUser.getUserId(),
				accountEntryObjectDefinition.getObjectDefinitionId(),
				_objectDefinition3.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"oneToManyRelationshipName1", false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		_objectDefinition3.setAccountEntryRestrictedObjectFieldId(
			objectRelationship2.getObjectFieldId2());

		_objectDefinition3.setAccountEntryRestricted(true);

		_objectDefinition3 =
			objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition3);

		_objectDefinition3 =
			objectDefinitionLocalService.publishCustomObjectDefinition(
				adminUser.getUserId(),
				_objectDefinition3.getObjectDefinitionId());

		_accountAdministratorRole = _roleLocalService.getRole(
			companyId,
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR);
		_accountManagerRole = _roleLocalService.getRole(
			companyId, AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MANAGER);
		_buyerRole = _roleLocalService.getRole(companyId, "Buyer");

		_originalNestedFieldsContext =
			NestedFieldsContextThreadLocal.getNestedFieldsContext();

		NestedFieldsContextThreadLocal.setNestedFieldsContext(
			new NestedFieldsContext(
				1, null,
				Arrays.asList(
					StringUtil.removeLast(
						StringUtil.removeFirst(
							_objectDefinition1.getPKObjectFieldName(), "c_"),
						"Id")),
				null, null, null));

		_tree = TreeTestUtil.createObjectDefinitionTree(
			objectDefinitionLocalService, _objectRelationshipLocalService, true,
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA", "AB"}
			).put(
				"AA", new String[] {"AAA", "AAB"}
			).put(
				"AB", new String[0]
			).put(
				"AAA", new String[0]
			).put(
				"AAB", new String[0]
			).build());

		Node rootNode = _tree.getRootNode();

		_rootObjectDefinition =
			objectDefinitionLocalService.enableAccountEntryRestricted(
				_objectRelationshipLocalService.addObjectRelationship(
					null, adminUser.getUserId(),
					accountEntryObjectDefinition.getObjectDefinitionId(),
					rootNode.getPrimaryKey(), 0,
					ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					"oneToManyRelationshipName2", false,
					ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null));
	}

	@After
	public void tearDown() throws Exception {
		NestedFieldsContextThreadLocal.setNestedFieldsContext(
			_originalNestedFieldsContext);

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AB", "C_AAA", "C_AAB"},
			_objectEntryLocalService, _objectRelationshipLocalService);
	}

	@Test
	public void testAddObjectEntryWithAccountEntryRestricted1()
		throws Exception {

		// Account entry restricted scope

		AccountEntry accountEntry1 = _addAccountEntry();

		_user = _addUser();

		_assignAccountEntryRole(accountEntry1, _buyerRole, _user);

		_addResourcePermission(ObjectActionKeys.ADD_OBJECT_ENTRY, _buyerRole);

		AccountEntry accountEntry2 = _addAccountEntry();

		AssertUtils.assertFailure(
			ObjectDefinitionAccountEntryRestrictedException.class,
			StringBundler.concat(
				"User ", _user.getUserId(),
				" does not have access to account entry ",
				accountEntry2.getAccountEntryId()),
			() -> _addObjectEntry(accountEntry2));

		// Account entry restricted with organization scope

		Organization organization1 = OrganizationTestUtil.addOrganization();

		_addAccountEntryOrganizationRel(accountEntry1, organization1);

		_user = _addUser();

		_assignOrganizationRole(organization1, _accountManagerRole, _user);

		_addResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY, _accountManagerRole);

		AssertUtils.assertFailure(
			ObjectDefinitionAccountEntryRestrictedException.class,
			StringBundler.concat(
				"User ", _user.getUserId(),
				" does not have access to account entry ",
				accountEntry2.getAccountEntryId()),
			() -> _addObjectEntry(accountEntry2));

		_deleteAccountEntryOrganizationRel(accountEntry1, organization1);

		// Account entry restricted with suborganization scope

		Organization suborganization1 = OrganizationTestUtil.addOrganization(
			organization1.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		_addAccountEntryOrganizationRel(accountEntry1, suborganization1);

		_user = _addUser();

		_assignOrganizationRole(organization1, _accountManagerRole, _user);

		AssertUtils.assertFailure(
			ObjectDefinitionAccountEntryRestrictedException.class,
			StringBundler.concat(
				"User ", _user.getUserId(),
				" does not have access to account entry ",
				accountEntry2.getAccountEntryId()),
			() -> _addObjectEntry(accountEntry2));

		_deleteAccountEntryOrganizationRel(accountEntry1, suborganization1);

		_removeResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY, _accountManagerRole);

		// Check account entry permission

		_user = _addUser();

		_testAddObjectEntryAccountEntryRestriction(accountEntry1);
		_testAddObjectEntryAccountEntryRestriction(accountEntry2);

		// Check account entry permission with organization

		_addAccountEntryOrganizationRel(accountEntry1, organization1);

		_user = _addUser();

		_assignOrganizationRole(organization1, _accountManagerRole, _user);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(),
				" must have ADD_OBJECT_ENTRY permission for ",
				_objectDefinition3.getResourceName(), StringPool.SPACE),
			() -> _addObjectEntry(accountEntry1));

		_addResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY, _accountManagerRole);

		Assert.assertNotNull(_addObjectEntry(accountEntry1));

		_removeResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY, _accountManagerRole);

		// Check account entry permission with suborganization

		Organization organization2 = OrganizationTestUtil.addOrganization();

		Organization suborganization2 = OrganizationTestUtil.addOrganization(
			organization2.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		_addAccountEntryOrganizationRel(accountEntry2, suborganization2);

		_user = _addUser();

		_assignOrganizationRole(organization1, _accountManagerRole, _user);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(),
				" must have ADD_OBJECT_ENTRY permission for ",
				_objectDefinition3.getResourceName(), StringPool.SPACE),
			() -> _addObjectEntry(accountEntry1));

		_addResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY, _accountManagerRole);

		Assert.assertNotNull(_addObjectEntry(accountEntry1));

		// Regular roles' company scope permissions should not be restricted by
		// account entry

		_user = _addUser();

		Role role = _addRoleUser(
			new String[] {ObjectActionKeys.ADD_OBJECT_ENTRY},
			_objectDefinition3, _user);

		Assert.assertNotNull(_addObjectEntry(accountEntry1));

		_resourcePermissionLocalService.removeResourcePermission(
			companyId, _objectDefinition3.getResourceName(),
			ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
			role.getRoleId(), ObjectActionKeys.ADD_OBJECT_ENTRY);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(),
				" must have ADD_OBJECT_ENTRY permission for ",
				_objectDefinition3.getResourceName(), StringPool.SPACE),
			() -> _addObjectEntry(accountEntry1));
	}

	@Test
	public void testAddObjectEntryWithAccountEntryRestricted2()
		throws Exception {

		// Object definitions inherit account entry restricted from the root
		// object definition

		_addResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY, _rootObjectDefinition,
			_buyerRole);

		AccountEntry accountEntry = _addAccountEntry();

		_user = _addUser();

		_assignAccountEntryRole(accountEntry, _buyerRole, _user);

		Node rootNode = _tree.getRootNode();

		ObjectEntry objectEntry = _defaultObjectEntryManager.addObjectEntry(
			_simpleDTOConverterContext,
			objectDefinitionLocalService.getObjectDefinition(
				rootNode.getPrimaryKey()),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"r_oneToManyRelationshipName2_accountEntryId",
						accountEntry.getAccountEntryId()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.fetchObjectDefinition(
				companyId, "C_AA");

		Node childNode = _tree.getNode(
			objectDefinition.getObjectDefinitionId());

		Edge edge = childNode.getEdge();

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				edge.getObjectRelationshipId());

		ObjectField objectField = objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());

		_defaultObjectEntryManager.addObjectEntry(
			_simpleDTOConverterContext,
			objectDefinitionLocalService.getObjectDefinition(
				childNode.getPrimaryKey()),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						objectField.getName(), objectEntry.getId()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_user = _addUser();

		_assignAccountEntryRole(accountEntry, _buyerRole, _user);

		_removeResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY, _rootObjectDefinition,
			_buyerRole);

		_addResourcePermission(
			ActionKeys.UPDATE, _rootObjectDefinition, _buyerRole);

		_defaultObjectEntryManager.addObjectEntry(
			_simpleDTOConverterContext,
			objectDefinitionLocalService.getObjectDefinition(
				childNode.getPrimaryKey()),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						objectField.getName(), objectEntry.getId()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_removeResourcePermission(
			ActionKeys.UPDATE, _rootObjectDefinition, _buyerRole);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(),
				" must have ADD_OBJECT_ENTRY permission for ",
				_rootObjectDefinition.getResourceName(), StringPool.SPACE),
			() -> _defaultObjectEntryManager.addObjectEntry(
				_simpleDTOConverterContext,
				objectDefinitionLocalService.getObjectDefinition(
					childNode.getPrimaryKey()),
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							objectField.getName(), objectEntry.getId()
						).build();
					}
				},
				ObjectDefinitionConstants.SCOPE_COMPANY));
		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(),
				" must have ADD_OBJECT_ENTRY permission for ",
				_rootObjectDefinition.getResourceName(), StringPool.SPACE),
			() -> _defaultObjectEntryManager.addObjectEntry(
				_simpleDTOConverterContext,
				objectDefinitionLocalService.getObjectDefinition(
					rootNode.getPrimaryKey()),
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							"r_oneToManyRelationshipName2_accountEntryId",
							accountEntry.getAccountEntryId()
						).build();
					}
				},
				ObjectDefinitionConstants.SCOPE_COMPANY));
	}

	@Test
	public void testAddObjectEntryWithAccountEntryRestricted3()
		throws Exception {

		// Account entry restricted with implicit role Organization User

		AccountEntry accountEntry = _addAccountEntry();

		Organization organization = OrganizationTestUtil.addOrganization();

		_addAccountEntryOrganizationRel(accountEntry, organization);

		_user = _addUser();

		_organizationLocalService.addUserOrganization(
			_user.getUserId(), organization.getOrganizationId());

		Role role = _roleLocalService.getRole(
			companyId, RoleConstants.ORGANIZATION_USER);

		_addResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY, _objectDefinition3, role);

		Assert.assertNotNull(_addObjectEntry(accountEntry));

		_removeResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY, _objectDefinition3, role);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(),
				" must have ADD_OBJECT_ENTRY permission for ",
				_objectDefinition3.getResourceName(), StringPool.SPACE),
			() -> _addObjectEntry(accountEntry));
	}

	@Test
	public void testAddObjectEntryWithAttachmentObjectField() throws Exception {
		String dlFolderName = RandomTestUtil.randomString();

		ObjectDefinition objectDefinition = _createObjectDefinition(
			Collections.singletonList(
				new AttachmentObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"attachmentObjectFieldName"
				).objectFieldSettings(
					Arrays.asList(
						_createObjectFieldSetting(
							ObjectFieldSettingConstants.
								NAME_ACCEPTED_FILE_EXTENSIONS,
							"txt"),
						_createObjectFieldSetting(
							ObjectFieldSettingConstants.NAME_FILE_SOURCE,
							ObjectFieldSettingConstants.VALUE_USER_COMPUTER),
						_createObjectFieldSetting(
							ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE,
							"100"),
						_createObjectFieldSetting(
							ObjectFieldSettingConstants.
								NAME_SHOW_FILES_IN_DOCS_AND_MEDIA,
							"true"),
						_createObjectFieldSetting(
							ObjectFieldSettingConstants.
								NAME_STORAGE_DL_FOLDER_PATH,
							"/" + dlFolderName))
				).build()),
			ObjectDefinitionConstants.SCOPE_SITE);

		_user = _addUser();

		_addRoleUser(
			new String[] {
				ObjectActionKeys.ADD_OBJECT_ENTRY, ActionKeys.PERMISSIONS
			},
			objectDefinition, _user);

		String fileName = RandomTestUtil.randomString() + ".txt";

		Group group = _groupLocalService.getGroup(
			companyId, GroupConstants.GUEST);

		_defaultObjectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, objectDefinition,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"attachmentObjectFieldName",
						() -> {
							com.liferay.portal.kernel.repository.model.FileEntry
								fileEntry = TempFileEntryUtil.addTempFileEntry(
									group.getGroupId(),
									TestPropsValues.getUserId(),
									objectDefinition.getPortletId(),
									TempFileEntryUtil.getTempFileName(fileName),
									FileUtil.createTempFile(
										RandomTestUtil.randomString(
										).getBytes()),
									ContentTypes.TEXT_PLAIN);

							return fileEntry.getFileEntryId();
						}
					).put(
						"externalReferenceCode", RandomTestUtil.randomString()
					).build();
				}
			},
			String.valueOf(group.getGroupId()));

		DLFolder dlFolder = _dlFolderService.getFolder(
			group.getGroupId(), 0, dlFolderName);

		Assert.assertNotNull(
			_dlFileEntryService.getFileEntryByFileName(
				group.getGroupId(), dlFolder.getFolderId(), fileName));

		objectDefinitionLocalService.deleteObjectDefinition(
			objectDefinition.getObjectDefinitionId());
	}

	@Test
	public void testAddObjectEntryWithBooleanObjectField() throws Exception {
		ObjectDefinition objectDefinition = _createObjectDefinition(
			Arrays.asList(
				new BooleanObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"booleanObjectFieldName1"
				).objectFieldSettings(
					Arrays.asList(
						_createObjectFieldSetting(
							ObjectFieldSettingConstants.NAME_DEFAULT_VALUE,
							"false"),
						_createObjectFieldSetting(
							ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE,
							ObjectFieldSettingConstants.VALUE_INPUT_AS_VALUE))
				).build(),
				new BooleanObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"booleanObjectFieldName2"
				).objectFieldSettings(
					Arrays.asList(
						_createObjectFieldSetting(
							ObjectFieldSettingConstants.NAME_DEFAULT_VALUE,
							"false"),
						_createObjectFieldSetting(
							ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE,
							ObjectFieldSettingConstants.VALUE_INPUT_AS_VALUE))
				).build(),
				new BooleanObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"booleanObjectFieldName3"
				).objectFieldSettings(
					Arrays.asList(
						_createObjectFieldSetting(
							ObjectFieldSettingConstants.NAME_DEFAULT_VALUE,
							"true"),
						_createObjectFieldSetting(
							ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE,
							ObjectFieldSettingConstants.VALUE_INPUT_AS_VALUE))
				).build(),
				new BooleanObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"booleanObjectFieldName4"
				).objectFieldSettings(
					Arrays.asList(
						_createObjectFieldSetting(
							ObjectFieldSettingConstants.NAME_DEFAULT_VALUE,
							"isEmailAddress(textObjectFieldName)"),
						_createObjectFieldSetting(
							ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE,
							ObjectFieldSettingConstants.
								VALUE_EXPRESSION_BUILDER))
				).build(),
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"textObjectFieldName"
				).build()),
			ObjectDefinitionConstants.SCOPE_COMPANY);

		try {
			ObjectEntry objectEntry = _defaultObjectEntryManager.addObjectEntry(
				_simpleDTOConverterContext, objectDefinition,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							"booleanObjectFieldName1", true
						).put(
							"textObjectFieldName", RandomTestUtil.randomString()
						).build();
					}
				},
				null);

			Assert.assertTrue(
				MapUtil.getBoolean(
					objectEntry.getProperties(), "booleanObjectFieldName1"));
			Assert.assertFalse(
				MapUtil.getBoolean(
					objectEntry.getProperties(), "booleanObjectFieldName2"));
			Assert.assertTrue(
				MapUtil.getBoolean(
					objectEntry.getProperties(), "booleanObjectFieldName3"));
			Assert.assertFalse(
				MapUtil.getBoolean(
					objectEntry.getProperties(), "booleanObjectFieldName4"));
		}
		finally {
			objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}
	}

	@Test
	public void testAddObjectEntryWithDefaultLanguageId() throws Exception {
		_testAddObjectEntryWithDefaultLanguageId(
			ObjectDefinitionTestUtil.publishObjectDefinition());
		_testAddObjectEntryWithDefaultLanguageId(
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"a" + RandomTestUtil.randomString()
					).build()),
				ObjectDefinitionConstants.SCOPE_SITE));
	}

	@Test
	public void testAddObjectEntryWithDefaultValue() throws Exception {
		ObjectDefinition objectDefinition = _createObjectDefinition(
			Arrays.asList(
				new BooleanObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).localized(
					true
				).name(
					"localizedBooleanObjectFieldName"
				).build(),
				new PicklistObjectFieldBuilder(
				).indexed(
					true
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).listTypeDefinitionId(
					listTypeDefinition.getListTypeDefinitionId()
				).localized(
					true
				).name(
					"localizedPicklistObjectFieldName"
				).build()));

		ObjectEntry objectEntry = _addObjectEntry(
			objectDefinition,
			HashMapBuilder.<String, Object>put(
				"localizedBooleanObjectFieldName_i18n", () -> null
			).put(
				"localizedPicklistObjectFieldName_i18n", () -> null
			).build());

		Map<String, Object> properties = objectEntry.getProperties();

		Assert.assertNull(
			properties.get("localizedBooleanObjectFieldName_i18n"));
		Assert.assertNull(
			properties.get("localizedPicklistObjectFieldName_i18n"));

		Map<String, Object> localizedObjectFieldI18nValues =
			HashMapBuilder.<String, Object>put(
				"localizedBooleanObjectFieldName_i18n",
				HashMapBuilder.<String, Object>put(
					"en_US", RandomTestUtil.randomBoolean()
				).put(
					"pt_BR", RandomTestUtil.randomBoolean()
				).build()
			).put(
				"localizedPicklistObjectFieldName_i18n",
				HashMapBuilder.<String, Object>put(
					"en_US", _listTypeEntryKey1
				).put(
					"pt_BR", _listTypeEntryKey2
				).build()
			).build();

		assertEquals(
			_addObjectEntry(objectDefinition, localizedObjectFieldI18nValues),
			new ObjectEntry() {
				{
					properties = localizedObjectFieldI18nValues;
				}
			});

		_addObjectFieldSettingWithDefaultValue(
			StringPool.TRUE, objectDefinition,
			"localizedBooleanObjectFieldName");
		_addObjectFieldSettingWithDefaultValue(
			_listTypeEntryKey1, objectDefinition,
			"localizedPicklistObjectFieldName");

		assertEquals(
			_addObjectEntry(
				objectDefinition,
				HashMapBuilder.<String, Object>put(
					"localizedBooleanObjectFieldName_i18n", () -> null
				).put(
					"localizedPicklistObjectFieldName_i18n", () -> null
				).build()),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"localizedBooleanObjectFieldName_i18n",
						HashMapBuilder.<String, Object>put(
							"en_US", true
						).build()
					).put(
						"localizedPicklistObjectFieldName_i18n",
						HashMapBuilder.<String, Object>put(
							"en_US", _listTypeEntryKey1
						).build()
					).build();
				}
			});
		assertEquals(
			_addObjectEntry(
				objectDefinition,
				HashMapBuilder.<String, Object>put(
					"localizedBooleanObjectFieldName_i18n",
					HashMapBuilder.<String, Object>put(
						"pt_BR", true
					).build()
				).put(
					"localizedPicklistObjectFieldName_i18n",
					HashMapBuilder.<String, Object>put(
						"pt_BR", _listTypeEntryKey2
					).build()
				).build()),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"localizedBooleanObjectFieldName_i18n",
						HashMapBuilder.<String, Object>put(
							"en_US", true
						).put(
							"pt_BR", true
						).build()
					).put(
						"localizedPicklistObjectFieldName_i18n",
						HashMapBuilder.<String, Object>put(
							"en_US", _listTypeEntryKey1
						).put(
							"pt_BR", _listTypeEntryKey2
						).build()
					).build();
				}
			});

		objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Test
	public void testAddObjectEntryWithDynamicObjectFieldValues()
		throws Exception {

		// Aggregation field with filters

		String objectFieldName1 = "a" + RandomTestUtil.randomString();
		String objectFieldName2 = "a" + RandomTestUtil.randomString();

		_addAggregationObjectField(
			objectFieldName2, "MAX", _objectDefinition1.getObjectDefinitionId(),
			objectFieldName1, _objectRelationshipName);

		ObjectField objectField1 = objectFieldLocalService.getObjectField(
			_objectDefinition1.getObjectDefinitionId(), objectFieldName1);

		_objectFilterLocalService.addObjectFilter(
			adminUser.getUserId(), objectField1.getObjectFieldId(),
			objectFieldName2, ObjectFilterConstants.TYPE_NOT_EQUALS,
			"{\"ne\": \"25\"}");

		_addCustomObjectField(
			new IntegerObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				objectFieldName2
			).objectDefinitionId(
				_objectDefinition2.getObjectDefinitionId()
			).build());

		ObjectEntry parentObjectEntry1 =
			_defaultObjectEntryManager.addObjectEntry(
				_simpleDTOConverterContext, _objectDefinition1,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							"externalReferenceCode", "newExternalReferenceCode"
						).put(
							"textObjectFieldName", RandomTestUtil.randomString()
						).build();
					}
				},
				ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectEntry childObjectEntry1 =
			_defaultObjectEntryManager.addObjectEntry(
				dtoConverterContext, _objectDefinition2,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							_objectRelationshipERCObjectFieldName,
							"newExternalReferenceCode"
						).put(
							objectFieldName2, 15
						).put(
							"dateObjectFieldName", "2020-01-02"
						).put(
							"decimalObjectFieldName", 15.7
						).put(
							"longIntegerObjectFieldName", 100L
						).put(
							"picklistObjectFieldName", _addListTypeEntry()
						).put(
							"precisionDecimalObjectFieldName",
							new BigDecimal("0.9876543217654321")
						).build();
					}
				},
				ObjectDefinitionConstants.SCOPE_COMPANY);

		String listTypeEntryKey = _addListTypeEntry();

		ObjectEntry childObjectEntry2 =
			_defaultObjectEntryManager.addObjectEntry(
				dtoConverterContext, _objectDefinition2,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							_objectRelationshipERCObjectFieldName,
							"newExternalReferenceCode"
						).put(
							objectFieldName2, 10
						).put(
							"attachmentObjectFieldName",
							_getAttachmentObjectFieldValue()
						).put(
							"dateObjectFieldName", "2022-01-01"
						).put(
							"decimalObjectFieldName", 15.5
						).put(
							"longIntegerObjectFieldName", 50000L
						).put(
							"picklistObjectFieldName", listTypeEntryKey
						).put(
							"precisionDecimalObjectFieldName",
							new BigDecimal("0.1234567891234567")
						).put(
							"richTextObjectFieldName",
							StringBundler.concat(
								"<i>", RandomTestUtil.randomString(), "</i>")
						).put(
							"textObjectFieldName", RandomTestUtil.randomString()
						).build();
					}
				},
				ObjectDefinitionConstants.SCOPE_COMPANY);

		// Aggregation field with filter (date range with date and time)

		String currentDateString = _simpleDateFormat.format(new Date());
		ObjectField objectField2 = objectFieldLocalService.getObjectField(
			_objectDefinition1.getObjectDefinitionId(),
			"countAggregationObjectFieldName1");

		_objectFilterLocalService.addObjectFilter(
			adminUser.getUserId(), objectField2.getObjectFieldId(),
			"createDate", ObjectFilterConstants.TYPE_DATE_RANGE,
			StringBundler.concat(
				"{\"le\": \"", currentDateString, "\", \"ge\": \"",
				currentDateString, "\"}"));
		_objectFilterLocalService.addObjectFilter(
			adminUser.getUserId(), objectField2.getObjectFieldId(),
			"modifiedDate", ObjectFilterConstants.TYPE_DATE_RANGE,
			StringBundler.concat(
				"{\"le\": \"", currentDateString, "\", \"ge\": \"",
				currentDateString, "\"}"));

		_assertCountAggregationObjectFieldValue(2, parentObjectEntry1);

		// Aggregation field with filter (date range with date only)

		_objectFilterLocalService.addObjectFilter(
			adminUser.getUserId(), objectField2.getObjectFieldId(),
			"dateObjectFieldName", ObjectFilterConstants.TYPE_DATE_RANGE,
			"{\"le\": \"2020-01-02\", \"ge\": \"2020-01-02\"}");

		_assertCountAggregationObjectFieldValue(1, parentObjectEntry1);

		// Aggregation field with filter (equals and not equals)

		_objectFilterLocalService.addObjectFilter(
			adminUser.getUserId(), objectField2.getObjectFieldId(),
			objectFieldName2, ObjectFilterConstants.TYPE_EQUALS,
			"{\"eq\": \"15\"}");

		_assertCountAggregationObjectFieldValue(1, parentObjectEntry1);

		_objectFilterLocalService.addObjectFilter(
			adminUser.getUserId(), objectField2.getObjectFieldId(),
			objectFieldName2, ObjectFilterConstants.TYPE_NOT_EQUALS,
			"{\"ne\":\"15\"}");

		_assertCountAggregationObjectFieldValue(0, parentObjectEntry1);

		_objectFilterLocalService.deleteObjectFieldObjectFilter(
			objectField2.getObjectFieldId());

		ObjectEntry childObjectEntry3 =
			_defaultObjectEntryManager.addObjectEntry(
				dtoConverterContext, _objectDefinition2,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							_objectRelationshipERCObjectFieldName,
							"newExternalReferenceCode"
						).put(
							objectFieldName2, 25
						).build();
					}
				},
				ObjectDefinitionConstants.SCOPE_COMPANY);

		assertEquals(
			_defaultObjectEntryManager.getObjectEntry(
				_simpleDTOConverterContext, _objectDefinition1,
				parentObjectEntry1.getId()),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						objectFieldName1, "15"
					).build();
				}
			});

		_objectEntryLocalService.deleteObjectEntry(childObjectEntry3.getId());

		_objectFilterLocalService.deleteObjectFieldObjectFilter(
			objectField1.getObjectFieldId());

		// Aggregation field with filter (excludes and includes with a string)

		_objectFilterLocalService.addObjectFilter(
			adminUser.getUserId(), objectField2.getObjectFieldId(),
			"picklistObjectFieldName", ObjectFilterConstants.TYPE_EXCLUDES,
			"{\"not\":{\"in\":[\"" + listTypeEntryKey + "\"]}}");

		_assertCountAggregationObjectFieldValue(1, parentObjectEntry1);

		_objectFilterLocalService.addObjectFilter(
			adminUser.getUserId(), objectField2.getObjectFieldId(),
			"picklistObjectFieldName", ObjectFilterConstants.TYPE_INCLUDES,
			"{\"in\":[\"" + listTypeEntryKey + "\"]}");

		_assertCountAggregationObjectFieldValue(0, parentObjectEntry1);

		_objectFilterLocalService.deleteObjectFieldObjectFilter(
			objectField2.getObjectFieldId());

		// Aggregation field with filter (excludes and includes with an int)

		_objectFilterLocalService.addObjectFilter(
			adminUser.getUserId(), objectField2.getObjectFieldId(), "status",
			ObjectFilterConstants.TYPE_EXCLUDES,
			"{\"not\":{\"in\": [" + WorkflowConstants.STATUS_APPROVED + "]}}");

		_assertCountAggregationObjectFieldValue(0, parentObjectEntry1);

		_objectFilterLocalService.deleteObjectFieldObjectFilter(
			objectField2.getObjectFieldId());

		_objectFilterLocalService.addObjectFilter(
			adminUser.getUserId(), objectField2.getObjectFieldId(), "status",
			ObjectFilterConstants.TYPE_INCLUDES,
			"{\"in\": [" + WorkflowConstants.STATUS_APPROVED + "]}");

		_assertCountAggregationObjectFieldValue(2, parentObjectEntry1);

		_objectFilterLocalService.deleteObjectFieldObjectFilter(
			objectField2.getObjectFieldId());

		// Aggregation field without filters

		assertEquals(
			_defaultObjectEntryManager.getObjectEntry(
				_simpleDTOConverterContext, _objectDefinition1,
				parentObjectEntry1.getId()),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						objectFieldName1, "15"
					).put(
						"averageAggregationObjectFieldName",
						"0.5555555554444444"
					).put(
						"countAggregationObjectFieldName1", "2"
					).put(
						"minAggregationObjectFieldName", "100"
					).put(
						"sumAggregationObjectFieldName", "31.2"
					).put(
						"textObjectFieldName",
						MapUtil.getString(
							parentObjectEntry1.getProperties(),
							"textObjectFieldName")
					).build();
				}
			});

		_defaultObjectEntryManager.deleteObjectEntry(
			_objectDefinition2, childObjectEntry1.getId());

		assertEquals(
			_defaultObjectEntryManager.getObjectEntry(
				_simpleDTOConverterContext, _objectDefinition1,
				parentObjectEntry1.getId()),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						objectFieldName1, "10"
					).put(
						"averageAggregationObjectFieldName",
						"0.1234567891234567"
					).put(
						"countAggregationObjectFieldName1", "1"
					).put(
						"minAggregationObjectFieldName", "50000"
					).put(
						"sumAggregationObjectFieldName", "15.5"
					).put(
						"textObjectFieldName",
						MapUtil.getString(
							parentObjectEntry1.getProperties(),
							"textObjectFieldName")
					).build();
				}
			});

		_defaultObjectEntryManager.deleteObjectEntry(
			_objectDefinition2, childObjectEntry2.getId());

		assertEquals(
			_defaultObjectEntryManager.getObjectEntry(
				_simpleDTOConverterContext, _objectDefinition1,
				parentObjectEntry1.getId()),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						objectFieldName1, "0"
					).put(
						"averageAggregationObjectFieldName", "0"
					).put(
						"countAggregationObjectFieldName1", "0"
					).put(
						"minAggregationObjectFieldName", "0"
					).put(
						"sumAggregationObjectFieldName", "0"
					).put(
						"textObjectFieldName",
						MapUtil.getString(
							parentObjectEntry1.getProperties(),
							"textObjectFieldName")
					).build();
				}
			});

		// Aggregation field without filters (many to many self relationship)

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.addObjectRelationship(
				null, adminUser.getUserId(),
				_objectDefinition1.getObjectDefinitionId(),
				_objectDefinition1.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null);

		_addAggregationObjectField(
			null, "COUNT", _objectDefinition1.getObjectDefinitionId(),
			"countAggregationObjectFieldName2", objectRelationship.getName());

		ObjectEntry parentObjectEntry2 =
			_defaultObjectEntryManager.addObjectEntry(
				_simpleDTOConverterContext, _objectDefinition1,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							"textObjectFieldName", RandomTestUtil.randomString()
						).build();
					}
				},
				ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectEntry childObjectEntry4 =
			_defaultObjectEntryManager.addObjectEntry(
				_simpleDTOConverterContext, _objectDefinition1,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							"textObjectFieldName", RandomTestUtil.randomString()
						).build();
					}
				},
				ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectRelationshipTestUtil.relateObjectEntries(
			parentObjectEntry2.getId(), childObjectEntry4.getId(),
			objectRelationship, adminUser.getUserId());

		ObjectEntry childObjectEntry5 =
			_defaultObjectEntryManager.addObjectEntry(
				_simpleDTOConverterContext, _objectDefinition1,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							"textObjectFieldName", RandomTestUtil.randomString()
						).build();
					}
				},
				ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectRelationshipTestUtil.relateObjectEntries(
			parentObjectEntry2.getId(), childObjectEntry5.getId(),
			objectRelationship, adminUser.getUserId());

		assertEquals(
			_defaultObjectEntryManager.getObjectEntry(
				_simpleDTOConverterContext, _objectDefinition1,
				parentObjectEntry2.getId()),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"countAggregationObjectFieldName2", "2"
					).build();
				}
			});

		_defaultObjectEntryManager.deleteObjectEntry(
			_objectDefinition1, childObjectEntry4.getId());

		assertEquals(
			_defaultObjectEntryManager.getObjectEntry(
				_simpleDTOConverterContext, _objectDefinition1,
				parentObjectEntry2.getId()),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"countAggregationObjectFieldName2", "1"
					).build();
				}
			});

		_defaultObjectEntryManager.deleteObjectEntry(
			_objectDefinition1, childObjectEntry5.getId());

		assertEquals(
			_defaultObjectEntryManager.getObjectEntry(
				_simpleDTOConverterContext, _objectDefinition1,
				parentObjectEntry2.getId()),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"countAggregationObjectFieldName2", "0"
					).build();
				}
			});

		// Date time

		LocalDateTime nowLocalDateTime = LocalDateTime.now();

		LocalDateTime localDateTime = nowLocalDateTime.truncatedTo(
			ChronoUnit.MILLIS);

		assertEquals(
			_defaultObjectEntryManager.addObjectEntry(
				dtoConverterContext, _objectDefinition2,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							"dateTimeObjectFieldName", localDateTime
						).build();
					}
				},
				ObjectDefinitionConstants.SCOPE_COMPANY),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"dateTimeObjectFieldName",
						localDateTime.format(
							DateTimeFormatter.ofPattern(
								"yyyy-MM-dd'T'HH:mm:ss.SSS"))
					).build();
				}
			});

		User user = UserTestUtil.addOmniadminUser();

		user.setTimeZoneId("America/Sao_Paulo");

		user = _userLocalService.updateUser(user);

		DateTimeFormatter utcDateTimeFormatter = DateTimeFormatter.ofPattern(
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

		ZonedDateTime zonedDateTime = localDateTime.atZone(
			ZoneId.of(user.getTimeZoneId()));

		LocalDateTime utcLocalDateTime = LocalDateTime.from(
			zonedDateTime.withZoneSameInstant(ZoneId.of(StringPool.UTC)));

		String dateTimeString1 = utcDateTimeFormatter.format(utcLocalDateTime);

		assertEquals(
			_defaultObjectEntryManager.addObjectEntry(
				new DefaultDTOConverterContext(
					false, Collections.emptyMap(), dtoConverterRegistry, null,
					LocaleUtil.getDefault(), null, user),
				_objectDefinition2,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							"dateTimeUTCObjectFieldName", localDateTime
						).build();
					}
				},
				ObjectDefinitionConstants.SCOPE_COMPANY),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"dateTimeUTCObjectFieldName", dateTimeString1
					).build();
				}
			});

		_userLocalService.deleteUser(user);

		// Date time with filters

		String dateTimeString2 = utcDateTimeFormatter.format(
			utcLocalDateTime.plusHours(1));

		_objectEntryManager.addObjectEntry(
			dtoConverterContext, _objectDefinition2,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"dateTimeUTCObjectFieldName", dateTimeString2
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_assertFilteredObjectEntriesSize(
			"dateTimeUTCObjectFieldName eq null", _objectDefinition2, 1);
		_assertFilteredObjectEntriesSize(
			"dateTimeUTCObjectFieldName eq " + dateTimeString1,
			_objectDefinition2, 1);
		_assertFilteredObjectEntriesSize(
			"dateTimeUTCObjectFieldName ge " + dateTimeString1,
			_objectDefinition2, 2);
		_assertFilteredObjectEntriesSize(
			"dateTimeUTCObjectFieldName gt " + dateTimeString1,
			_objectDefinition2, 1);
		_assertFilteredObjectEntriesSize(
			"dateTimeUTCObjectFieldName le " + dateTimeString2,
			_objectDefinition2, 2);
		_assertFilteredObjectEntriesSize(
			"dateTimeUTCObjectFieldName lt " + dateTimeString2,
			_objectDefinition2, 1);
		_assertFilteredObjectEntriesSize(
			"dateTimeUTCObjectFieldName ne null ", _objectDefinition2, 2);
		_assertFilteredObjectEntriesSize(
			String.format(
				"dateTimeUTCObjectFieldName ne %s or " +
					"dateTimeUTCObjectFieldName eq null",
				dateTimeString1),
			_objectDefinition2, 2);

		// Formula field

		assertEquals(
			_defaultObjectEntryManager.addObjectEntry(
				dtoConverterContext, _objectDefinition2,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							"integerObjectFieldName", 3
						).put(
							"longIntegerObjectFieldName", 2
						).build();
					}
				},
				ObjectDefinitionConstants.SCOPE_COMPANY),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"formulaObjectFieldName", 1.5
					).build();
				}
			});

		// Picklist by list entry

		ListTypeEntry listTypeEntry =
			_listTypeEntryLocalService.addListTypeEntry(
				null, adminUser.getUserId(),
				listTypeDefinition.getListTypeDefinitionId(),
				RandomTestUtil.randomString(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				listTypeDefinition.isSystem());

		ListEntry listEntry = new ListEntry() {
			{
				key = listTypeEntry.getKey();
				name = listTypeEntry.getName(LocaleUtil.US);
			}
		};

		_assertPicklistOjectField(listEntry, listEntry);

		// Picklist by list type entry key

		_assertPicklistOjectField(listEntry, listTypeEntry.getKey());

		// Picklist by map

		_assertPicklistOjectField(
			listEntry,
			HashMapBuilder.put(
				"key", listTypeEntry.getKey()
			).put(
				"name", listTypeEntry.getName(LocaleUtil.US)
			).build());

		// Relationship

		String newExternalReferenceCode = RandomTestUtil.randomString();

		AssertUtils.assertFailure(
			NoSuchObjectEntryException.class,
			StringBundler.concat(
				"No ObjectEntry exists with the key ",
				"{externalReferenceCode=", newExternalReferenceCode,
				", companyId=", companyId, ", objectDefinitionId=",
				_objectDefinition1.getObjectDefinitionId(), "}"),
			() -> _defaultObjectEntryManager.addObjectEntry(
				dtoConverterContext, _objectDefinition2,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							_objectRelationshipERCObjectFieldName,
							newExternalReferenceCode
						).build();
					}
				},
				ObjectDefinitionConstants.SCOPE_COMPANY));
	}

	@Test
	@TestInfo("LPD-55658")
	public void testAddObjectEntryWithMissingParentObjectEntryReference()
		throws Throwable {

		String externalReferenceCode = RandomTestUtil.randomString();

		_testAddObjectEntryWithMissingParentObjectEntryReference(
			_objectDefinition2,
			HashMapBuilder.<String, Object>put(
				_objectRelationshipERCObjectFieldName, externalReferenceCode
			).build(),
			externalReferenceCode, _objectDefinition1, new HashMap<>());

		String requiredObjectFieldName = "a" + RandomTestUtil.randomString();

		ObjectDefinition objectDefinition = _createObjectDefinition(
			Arrays.asList(
				new TextObjectFieldBuilder(
				).indexed(
					true
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					requiredObjectFieldName
				).required(
					true
				).build()));

		String objectRelationshipName = "a" + RandomTestUtil.randomString();

		_objectRelationshipLocalService.addObjectRelationship(
			null, adminUser.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			_objectDefinition1.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			objectRelationshipName, false,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		externalReferenceCode = RandomTestUtil.randomString();

		_testAddObjectEntryWithMissingParentObjectEntryReference(
			_objectDefinition1,
			HashMapBuilder.<String, Object>put(
				objectRelationshipName,
				HashMapBuilder.put(
					"externalReferenceCode", externalReferenceCode
				).build()
			).build(),
			externalReferenceCode, objectDefinition,
			HashMapBuilder.<String, Object>put(
				requiredObjectFieldName, RandomTestUtil.randomString()
			).build());
	}

	@Test
	public void testAddObjectEntryWithRelationshipObjectField()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry(
			_objectDefinition1,
			HashMapBuilder.<String, Object>put(
				"textObjectFieldName", RandomTestUtil.randomString()
			).build());

		_user = _addUser();

		Role role = _addRoleUser(
			new String[] {ObjectActionKeys.ADD_OBJECT_ENTRY},
			_objectDefinition2, _user);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have VIEW permission for ",
				_objectDefinition1.getClassName(), StringPool.SPACE,
				objectEntry.getId()),
			() -> _addObjectEntry(
				_objectDefinition2,
				HashMapBuilder.<String, Object>put(
					_objectRelationshipFieldName, objectEntry.getId()
				).build()));

		_resourcePermissionLocalService.setResourcePermissions(
			companyId, _objectDefinition1.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntry.getId()), role.getRoleId(),
			new String[] {ActionKeys.VIEW});

		assertEquals(
			_addObjectEntry(
				_objectDefinition2,
				HashMapBuilder.<String, Object>put(
					_objectRelationshipFieldName, objectEntry.getId()
				).build()),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						_objectRelationshipFieldName, objectEntry.getId()
					).build();
				}
			});
	}

	@Test
	public void testAddObjectEntryWithRichTextObjectField() throws Exception {
		ObjectDefinition objectDefinition = _createObjectDefinition(
			Collections.singletonList(
				new RichTextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"richTextObjectFieldName"
				).build()),
			ObjectDefinitionConstants.SCOPE_COMPANY);

		try {
			ObjectEntry objectEntry = _defaultObjectEntryManager.addObjectEntry(
				_simpleDTOConverterContext, objectDefinition,
				new ObjectEntry() {
					{
						properties = Collections.emptyMap();
					}
				},
				null);

			Assert.assertEquals(
				MapUtil.getString(
					objectEntry.getProperties(), "richTextObjectFieldName"),
				StringPool.BLANK);

			String value = RandomTestUtil.randomString();

			objectEntry = _defaultObjectEntryManager.addObjectEntry(
				_simpleDTOConverterContext, objectDefinition,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							"richTextObjectFieldName", value
						).build();
					}
				},
				null);

			Assert.assertEquals(
				MapUtil.getString(
					objectEntry.getProperties(), "richTextObjectFieldName"),
				value);
		}
		finally {
			objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testAddObjectEntryWithScheduleDates() throws Exception {
		ObjectDefinition objectDefinition = _createObjectDefinition(
			Collections.singletonList(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"textObjectFieldName"
				).build()));

		Date date = new Date(
			System.currentTimeMillis() + TimeUnit.DAY.toMillis(1));

		ObjectEntry objectEntry = _defaultObjectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, objectDefinition,
			new ObjectEntry() {
				{
					setDisplayDate(date);
					setExpirationDate(date);
					setProperties(
						HashMapBuilder.<String, Object>put(
							"textObjectFieldName", RandomTestUtil.randomString()
						).build());
					setReviewDate(date);
				}
			},
			null);

		Assert.assertEquals(date, objectEntry.getDisplayDate());
		Assert.assertEquals(date, objectEntry.getExpirationDate());
		Assert.assertEquals(date, objectEntry.getReviewDate());
	}

	@Test
	public void testAddObjectEntryWithStaticObjectFieldValues()
		throws Exception {

		assertEquals(
			_defaultObjectEntryManager.addObjectEntry(
				dtoConverterContext, _objectDefinition2,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							"dateObjectFieldName", "2020-01-02"
						).put(
							"decimalObjectFieldName", 15.7
						).put(
							"integerObjectFieldName", 15
						).put(
							"longIntegerObjectFieldName", 100L
						).put(
							"textObjectFieldName", "textObjectFieldValue"
						).putAll(
							_localizedObjectFieldI18nValues
						).build();
					}
				},
				ObjectDefinitionConstants.SCOPE_COMPANY),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"dateObjectFieldName", "2020-01-02"
					).put(
						"decimalObjectFieldName", 15.7
					).put(
						"integerObjectFieldName", 15
					).put(
						"longIntegerObjectFieldName", 100L
					).put(
						"textObjectFieldName", "textObjectFieldValue"
					).putAll(
						_localizedObjectFieldI18nValues
					).build();
				}
			});
	}

	@FeatureFlag("LPD-21926")
	@Test
	public void testAddOrUpdateObjectEntryWithFriendlyURL() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		objectDefinition.setFriendlyURLSeparator("test");

		objectDefinition = objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);

		ObjectEntry objectEntry = _defaultObjectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, objectDefinition,
			new ObjectEntry() {
				{
					setFriendlyUrlPath(RandomTestUtil.randomString());
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		Assert.assertEquals(
			objectEntry.getExternalReferenceCode(),
			objectEntry.getFriendlyUrlPath());

		objectDefinition.setEnableFriendlyURLCustomization(true);

		objectDefinition = objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);

		objectEntry = _defaultObjectEntryManager.updateObjectEntry(
			_simpleDTOConverterContext, objectDefinition, objectEntry.getId(),
			new ObjectEntry() {
				{
					setFriendlyUrlPath("Test URL");
				}
			});

		Assert.assertEquals("test-url", objectEntry.getFriendlyUrlPath());

		objectEntry = _defaultObjectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, objectDefinition,
			new ObjectEntry() {
				{
					setFriendlyUrlPath_i18n(
						HashMapBuilder.put(
							"en_US", "Test URL"
						).put(
							"pt_BR", ""
						).build());
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		AssertUtils.assertEquals(
			HashMapBuilder.put(
				"en_US", "test-url-1"
			).put(
				"pt_BR", objectEntry.getExternalReferenceCode()
			).build(),
			objectEntry.getFriendlyUrlPath_i18n());

		objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Test
	public void testAddOrUpdateObjectEntryWithObjectAction() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new IntegerObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"integerObjectField"
					).build()));

		_addObjectAction(
			objectDefinition, ObjectActionTriggerConstants.KEY_ON_AFTER_ADD);
		_addObjectAction(
			objectDefinition, ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE);

		int randomInt1 = RandomTestUtil.randomInt();

		ObjectEntry objectEntry = _addObjectEntry(
			objectDefinition,
			HashMapBuilder.<String, Object>put(
				"integerObjectField", randomInt1
			).build());

		Assert.assertEquals(
			randomInt1 + 2,
			MapUtil.getInteger(
				objectEntry.getProperties(), "integerObjectField"));

		ThreadLocal<Map<Long, Set<Long>>> threadLocal =
			ReflectionTestUtil.getFieldValue(
				ObjectActionThreadLocal.class, "_objectEntryIdsMap");

		threadLocal.set(new HashMap<>());

		int randomInt2 = RandomTestUtil.randomInt();

		objectEntry = _defaultObjectEntryManager.updateObjectEntry(
			_simpleDTOConverterContext, objectDefinition, objectEntry.getId(),
			new ObjectEntry() {
				{
					setProperties(
						HashMapBuilder.<String, Object>put(
							"integerObjectField", randomInt2
						).build());
				}
			});

		Assert.assertEquals(
			randomInt2 + 1,
			MapUtil.getInteger(
				objectEntry.getProperties(), "integerObjectField"));

		objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Test
	public void testAddOrUpdateObjectEntryWithPicklistObjectField()
		throws Exception {

		ListTypeDefinition listTypeDefinition =
			listTypeDefinitionLocalService.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				false,
				ListUtil.fromArray(
					ListTypeEntryUtil.createListTypeEntry(
						"listTypeEntryKey1",
						Collections.singletonMap(
							LocaleUtil.US, RandomTestUtil.randomString())),
					ListTypeEntryUtil.createListTypeEntry(
						"listTypeEntryKey2",
						Collections.singletonMap(
							LocaleUtil.US, RandomTestUtil.randomString()))));

		ObjectDefinition objectDefinition = _createObjectDefinition(
			Collections.singletonList(
				new PicklistObjectFieldBuilder(
				).indexed(
					true
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).listTypeDefinitionId(
					listTypeDefinition.getListTypeDefinitionId()
				).name(
					"picklistObjectFieldName"
				).build()));

		try {
			long objectEntryId1 =
				_addAndAssertObjectEntryWithPicklistObjectField(
					null, null, objectDefinition);
			long objectEntryId2 =
				_addAndAssertObjectEntryWithPicklistObjectField(
					StringPool.BLANK, StringPool.BLANK, objectDefinition);
			long objectEntryId3 =
				_addAndAssertObjectEntryWithPicklistObjectField(
					"listTypeEntryKey1", "listTypeEntryKey1", objectDefinition);
			long objectEntryId4 =
				_addAndAssertObjectEntryWithPicklistObjectField(
					"listTypeEntryKey2", "listTypeEntryKey2", objectDefinition);

			_addObjectFieldSettingWithDefaultValue(
				"listTypeEntryKey1", objectDefinition,
				"picklistObjectFieldName");

			Assert.assertEquals(
				StringPool.BLANK,
				_getListEntryKey(
					_defaultObjectEntryManager.getObjectEntry(
						_simpleDTOConverterContext, objectDefinition,
						objectEntryId2)));

			_updateAndAssertObjectEntryWithPicklistObjectField(
				StringPool.BLANK, StringPool.BLANK, objectDefinition,
				objectEntryId1);
			_updateAndAssertObjectEntryWithPicklistObjectField(
				StringPool.BLANK, StringPool.BLANK, objectDefinition,
				objectEntryId2);
			_updateAndAssertObjectEntryWithPicklistObjectField(
				"listTypeEntryKey1", "listTypeEntryKey1", objectDefinition,
				objectEntryId3);
			_updateAndAssertObjectEntryWithPicklistObjectField(
				"listTypeEntryKey2", "listTypeEntryKey2", objectDefinition,
				objectEntryId4);

			_addAndAssertObjectEntryWithPicklistObjectField(
				null, "listTypeEntryKey1", objectDefinition);
			_addAndAssertObjectEntryWithPicklistObjectField(
				StringPool.BLANK, StringPool.BLANK, objectDefinition);
			_addAndAssertObjectEntryWithPicklistObjectField(
				"listTypeEntryKey1", "listTypeEntryKey1", objectDefinition);
			_addAndAssertObjectEntryWithPicklistObjectField(
				"listTypeEntryKey2", "listTypeEntryKey2", objectDefinition);
		}
		finally {
			objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}
	}

	@Test
	public void testCopyObjectEntryByVersion() throws Exception {
		_enableObjectEntryVersioning();

		ObjectEntry objectEntry1 = new ObjectEntry() {
			{
				externalReferenceCode = RandomTestUtil.randomString();
				keywords = new String[] {RandomTestUtil.randomString()};
				properties = HashMapBuilder.<String, Object>put(
					"textObjectFieldName", RandomTestUtil.randomString()
				).build();
				systemProperties = new SystemProperties() {
					{
						version = new Version() {
							{
								number = 1;
							}
						};
					}
				};
			}
		};

		objectEntry1 = _defaultObjectEntryManager.addObjectEntry(
			dtoConverterContext, _objectDefinition1, objectEntry1,
			ObjectDefinitionConstants.SCOPE_COMPANY);

		objectEntry1 = _updateObjectEntryVersion(objectEntry1, 2);

		ObjectEntry objectEntry2 =
			_defaultObjectEntryManager.copyObjectEntryByVersion(
				dtoConverterContext, _objectDefinition1, objectEntry1.getId(),
				2);

		assertEquals(
			_defaultObjectEntryManager.getObjectEntryByVersion(
				dtoConverterContext, objectEntry1.getExternalReferenceCode(),
				_objectDefinition1, 2),
			objectEntry2);
	}

	@Test
	public void testDeleteObjectEntry() throws Exception {
		ObjectDefinition objectDefinition1 = _createObjectDefinition(
			Collections.singletonList(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"textObjectFieldName"
				).build()));
		ObjectDefinition objectDefinition2 = _createObjectDefinition(
			Collections.singletonList(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"a" + RandomTestUtil.randomString()
				).build()));

		// Relationship type cascade

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.addObjectRelationship(
				null, adminUser.getUserId(),
				objectDefinition1.getObjectDefinitionId(),
				objectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"oneToManyRelationship", false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		_addRelatedObjectEntries(
			objectDefinition1, objectDefinition2, "externalReferenceCode1",
			"externalReferenceCode2", objectRelationship);

		_user = _addUser();

		Role role = _addRoleUser(
			new String[] {
				ActionKeys.DELETE, ActionKeys.PERMISSIONS, ActionKeys.UPDATE,
				ActionKeys.VIEW
			},
			objectDefinition1, _user);

		try {
			_defaultObjectEntryManager.deleteObjectEntry(
				companyId, _simpleDTOConverterContext, "externalReferenceCode1",
				objectDefinition1, null);

			Assert.fail();
		}
		catch (ObjectRelationshipDeletionTypeException
					objectRelationshipDeletionTypeException) {

			Assert.assertThat(
				objectRelationshipDeletionTypeException.getMessage(),
				CoreMatchers.containsString(
					StringBundler.concat(
						"User ", _user.getUserId(),
						" must have DELETE permission for ",
						objectDefinition2.getClassName())));
		}

		// Relationship type disassociate

		objectRelationship =
			_objectRelationshipLocalService.updateObjectRelationship(
				objectRelationship.getExternalReferenceCode(),
				objectRelationship.getObjectRelationshipId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE, false,
				objectRelationship.getLabelMap(), null);

		_defaultObjectEntryManager.deleteObjectEntry(
			companyId, _simpleDTOConverterContext, "externalReferenceCode1",
			objectDefinition1, null);

		try {
			_defaultObjectEntryManager.getObjectEntry(
				companyId, _simpleDTOConverterContext, "externalReferenceCode1",
				objectDefinition1, null);

			Assert.fail();
		}
		catch (NoSuchObjectEntryException noSuchObjectEntryException) {
			Assert.assertNotNull(noSuchObjectEntryException);
		}

		PrincipalThreadLocal.setName(adminUser.getUserId());
		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(adminUser));

		Assert.assertNotNull(
			_defaultObjectEntryManager.getObjectEntry(
				companyId, _simpleDTOConverterContext, "externalReferenceCode2",
				objectDefinition2, null));

		_addRelatedObjectEntries(
			objectDefinition1, objectDefinition2, "externalReferenceCode3",
			"externalReferenceCode4", objectRelationship);

		PrincipalThreadLocal.setName(_user.getUserId());
		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		// Relationshp type prevent

		objectRelationship =
			_objectRelationshipLocalService.updateObjectRelationship(
				objectRelationship.getExternalReferenceCode(),
				objectRelationship.getObjectRelationshipId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				objectRelationship.getLabelMap(), null);

		try {
			_defaultObjectEntryManager.deleteObjectEntry(
				companyId, _simpleDTOConverterContext, "externalReferenceCode3",
				objectDefinition1, null);

			Assert.fail();
		}
		catch (RequiredObjectRelationshipException
					requiredObjectRelationshipException) {

			Assert.assertEquals(
				StringBundler.concat(
					"Object relationship ",
					objectRelationship.getObjectRelationshipId(),
					" does not allow deletes"),
				requiredObjectRelationshipException.getMessage());
		}

		_roleLocalService.deleteRole(role.getRoleId());

		_objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship.getObjectRelationshipId());

		objectDefinitionLocalService.deleteObjectDefinition(
			objectDefinition1.getObjectDefinitionId());
		objectDefinitionLocalService.deleteObjectDefinition(
			objectDefinition2.getObjectDefinitionId());
	}

	@Test
	public void testDeleteObjectEntryVersion() throws Exception {
		_enableObjectEntryVersioning();

		ObjectEntry objectEntry = new ObjectEntry() {
			{
				externalReferenceCode = RandomTestUtil.randomString();
				keywords = new String[] {RandomTestUtil.randomString()};
				properties = HashMapBuilder.<String, Object>put(
					"textObjectFieldName", RandomTestUtil.randomString()
				).build();
				systemProperties = new SystemProperties() {
					{
						version = new Version() {
							{
								number = 1;
							}
						};
					}
				};
			}
		};

		objectEntry = _defaultObjectEntryManager.addObjectEntry(
			dtoConverterContext, _objectDefinition1, objectEntry,
			ObjectDefinitionConstants.SCOPE_COMPANY);

		assertEquals(
			_defaultObjectEntryManager.getObjectEntryByVersion(
				dtoConverterContext, objectEntry.getExternalReferenceCode(),
				_objectDefinition1, 1),
			objectEntry);

		long objectEntryId = objectEntry.getId();

		AssertUtils.assertFailure(
			RequiredObjectEntryVersionException.MustHaveOneVersion.class,
			"At least one version must remain",
			() -> _defaultObjectEntryManager.deleteObjectEntryByVersion(
				_objectDefinition1, objectEntryId, 1));

		objectEntry = _updateObjectEntryVersion(objectEntry, 2);

		assertEquals(
			_defaultObjectEntryManager.getObjectEntryByVersion(
				dtoConverterContext, objectEntry.getExternalReferenceCode(),
				_objectDefinition1, 2),
			objectEntry);

		Assert.assertEquals(
			2,
			_defaultObjectEntryManager.getVersionedObjectEntries(
				dtoConverterContext, objectEntry.getExternalReferenceCode(),
				_objectDefinition1, null
			).getItems(
			).size());

		AssertUtils.assertFailure(
			RequiredObjectEntryVersionException.MustNotDeleteLatestVersion.
				class,
			"The latest version cannot be deleted",
			() -> _defaultObjectEntryManager.deleteObjectEntryByVersion(
				_objectDefinition1, objectEntryId, 2));

		_defaultObjectEntryManager.deleteObjectEntryByVersion(
			_objectDefinition1, objectEntry.getId(), 1);

		Assert.assertEquals(
			1,
			_defaultObjectEntryManager.getVersionedObjectEntries(
				dtoConverterContext, objectEntry.getExternalReferenceCode(),
				_objectDefinition1, null
			).getItems(
			).size());
	}

	@Test
	public void testDeleteObjectEntryWithAccountEntryRestricted1()
		throws Exception {

		// Regular roles' company scope permissions should not be restricted by
		// account entry

		AccountEntry accountEntry1 = _addAccountEntry();

		ObjectEntry objectEntry1 = _addObjectEntry(accountEntry1);

		AccountEntry accountEntry2 = _addAccountEntry();

		ObjectEntry objectEntry2 = _addObjectEntry(accountEntry2);

		_user = _addUser();

		Role role = _addRoleUser(
			new String[] {ActionKeys.DELETE, ActionKeys.VIEW},
			_objectDefinition3, _user);

		_defaultObjectEntryManager.deleteObjectEntry(
			_objectDefinition3, objectEntry1.getId());

		_resourcePermissionLocalService.removeResourcePermission(
			companyId, _objectDefinition3.getClassName(),
			ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
			role.getRoleId(), ActionKeys.DELETE);

		try {
			_defaultObjectEntryManager.deleteObjectEntry(
				_objectDefinition3, objectEntry2.getId());

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				exception.getMessage(),
				StringBundler.concat(
					"User ", _user.getUserId(),
					" must have DELETE permission for ",
					_objectDefinition3.getClassName(), StringPool.SPACE,
					objectEntry2.getId()));
		}

		// Regular roles' individual permissions should not be restricted by
		// account entry

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(adminUser));

		PrincipalThreadLocal.setName(adminUser.getUserId());

		objectEntry1 = _addObjectEntry(accountEntry1);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		PrincipalThreadLocal.setName(_user.getUserId());

		_resourcePermissionLocalService.setResourcePermissions(
			companyId, _objectDefinition3.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntry1.getId()), role.getRoleId(),
			new String[] {ActionKeys.DELETE});

		_defaultObjectEntryManager.deleteObjectEntry(
			_objectDefinition3, objectEntry1.getId());

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(adminUser));

		PrincipalThreadLocal.setName(adminUser.getUserId());

		objectEntry1 = _addObjectEntry(accountEntry1);

		// Account entry scope

		_user = _addUser();

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry1.getAccountEntryId(), _user.getUserId());
		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry2.getAccountEntryId(), _user.getUserId());

		_addResourcePermission(ActionKeys.DELETE, _accountAdministratorRole);
		_addResourcePermission(ActionKeys.VIEW, _accountAdministratorRole);

		_userGroupRoleLocalService.addUserGroupRole(
			_user.getUserId(), accountEntry1.getAccountEntryGroupId(),
			_accountAdministratorRole.getRoleId());

		_addResourcePermission(ActionKeys.VIEW, _buyerRole);

		_userGroupRoleLocalService.addUserGroupRole(
			_user.getUserId(), accountEntry2.getAccountEntryGroupId(),
			_buyerRole.getRoleId());

		_defaultObjectEntryManager.deleteObjectEntry(
			_objectDefinition3, objectEntry1.getId());

		try {
			_defaultObjectEntryManager.deleteObjectEntry(
				_objectDefinition3, objectEntry2.getId());

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				exception.getMessage(),
				StringBundler.concat(
					"User ", _user.getUserId(),
					" must have DELETE permission for ",
					_objectDefinition3.getClassName(), StringPool.SPACE,
					objectEntry2.getId()));
		}

		// Organization scope

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(adminUser));

		PrincipalThreadLocal.setName(adminUser.getUserId());

		objectEntry1 = _addObjectEntry(accountEntry1);

		_user = _addUser();

		Organization organization1 = OrganizationTestUtil.addOrganization();

		_addResourcePermission(ActionKeys.VIEW, _accountManagerRole);

		_assignOrganizationRole(organization1, _accountManagerRole, _user);

		_addAccountEntryOrganizationRel(accountEntry1, organization1);

		Organization organization2 = OrganizationTestUtil.addOrganization();

		_addAccountEntryOrganizationRel(accountEntry2, organization2);

		_assertObjectEntriesSize1(1);

		try {
			_defaultObjectEntryManager.deleteObjectEntry(
				_objectDefinition3, objectEntry1.getId());

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				exception.getMessage(),
				StringBundler.concat(
					"User ", _user.getUserId(), " must have DELETE permission ",
					"for ", _objectDefinition3.getClassName(), StringPool.SPACE,
					objectEntry1.getId()));
		}

		_assertObjectEntriesSize1(1);

		_addResourcePermission(ActionKeys.DELETE, _accountManagerRole);

		_defaultObjectEntryManager.deleteObjectEntry(
			_objectDefinition3, objectEntry1.getId());

		_assertObjectEntriesSize1(0);

		_deleteAccountEntryOrganizationRel(accountEntry1, organization1);
		_deleteAccountEntryOrganizationRel(accountEntry2, organization2);

		// Suborganization scope

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(adminUser));

		PrincipalThreadLocal.setName(adminUser.getUserId());

		objectEntry1 = _addObjectEntry(accountEntry1);

		_user = _addUser();

		_assignOrganizationRole(organization1, _accountManagerRole, _user);

		Organization suborganization1 = OrganizationTestUtil.addOrganization(
			organization1.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		_addAccountEntryOrganizationRel(accountEntry1, suborganization1);

		Organization suborganization2 = OrganizationTestUtil.addOrganization(
			organization2.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		_addAccountEntryOrganizationRel(accountEntry2, suborganization2);

		_assertObjectEntriesSize1(1);

		_removeResourcePermission(ActionKeys.DELETE, _accountManagerRole);

		try {
			_defaultObjectEntryManager.deleteObjectEntry(
				_objectDefinition3, objectEntry1.getId());

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				exception.getMessage(),
				StringBundler.concat(
					"User ", _user.getUserId(), " must have DELETE permission ",
					"for ", _objectDefinition3.getClassName(), StringPool.SPACE,
					objectEntry1.getId()));
		}

		_assertObjectEntriesSize1(1);

		_addResourcePermission(ActionKeys.DELETE, _accountManagerRole);

		_defaultObjectEntryManager.deleteObjectEntry(
			_objectDefinition3, objectEntry1.getId());

		_assertObjectEntriesSize1(0);
	}

	@Test
	public void testDeleteObjectEntryWithAccountEntryRestricted2()
		throws Exception {

		// Object definitions inherit account entry restricted from the root
		// object definition

		AccountEntry accountEntry1 = _addAccountEntry();

		Tree tree = _createObjectEntryTree(accountEntry1, StringPool.BLANK);

		_user = _addUser();

		_assignAccountEntryRole(accountEntry1, _buyerRole, _user);

		_testDeleteObjectEntryWithAccountEntryRestricted2(
			ActionKeys.DELETE, tree);

		// Users can delete object entries from accounts that they belong to

		_addResourcePermission(
			ActionKeys.DELETE, _rootObjectDefinition, _buyerRole);

		TreeTestUtil.forEachNodeObjectEntry(
			tree.iterator(TreeConstants.ITERATOR_TYPE_POST_ORDER),
			_objectEntryLocalService,
			objectEntry -> {
				ObjectDefinition objectDefinition =
					objectDefinitionLocalService.fetchObjectDefinition(
						objectEntry.getObjectDefinitionId());

				_defaultObjectEntryManager.deleteObjectEntry(
					objectDefinition, objectEntry.getObjectEntryId());
			});

		// Users cannot delete object entries from accounts that they do not
		// belong to

		tree = _createObjectEntryTree(accountEntry1, StringPool.BLANK);

		AccountEntry accountEntry2 = _addAccountEntry();

		_user = _addUser();

		_assignAccountEntryRole(accountEntry2, _accountManagerRole, _user);

		_addResourcePermission(
			ActionKeys.DELETE, _rootObjectDefinition, _accountManagerRole);

		_testDeleteObjectEntryWithAccountEntryRestricted2(
			ActionKeys.VIEW, tree);

		_addResourcePermission(
			ActionKeys.VIEW, _rootObjectDefinition, _accountManagerRole);

		_testDeleteObjectEntryWithAccountEntryRestricted2(
			ActionKeys.VIEW, tree);
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testExpireObjectEntryByVersion() throws Exception {
		_enableObjectEntryVersioning();

		String objectEntryExternalReferenceCode = RandomTestUtil.randomString();

		_defaultObjectEntryManager.addObjectEntry(
			dtoConverterContext, _objectDefinition1,
			new ObjectEntry() {
				{
					externalReferenceCode = objectEntryExternalReferenceCode;
					keywords = new String[] {RandomTestUtil.randomString()};
					properties = HashMapBuilder.<String, Object>put(
						"textObjectFieldName", RandomTestUtil.randomString()
					).build();
					systemProperties = new SystemProperties() {
						{
							version = new Version() {
								{
									number = 1;
								}
							};
						}
					};
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_defaultObjectEntryManager.updateObjectEntry(
			TestPropsValues.getCompanyId(), dtoConverterContext,
			objectEntryExternalReferenceCode, _objectDefinition1,
			new ObjectEntry() {
				{
					keywords = new String[] {RandomTestUtil.randomString()};
					properties = HashMapBuilder.<String, Object>put(
						"textObjectFieldName", RandomTestUtil.randomString()
					).build();
					systemProperties = new SystemProperties() {
						{
							version = new Version() {
								{
									number = 2;
								}
							};
						}
					};
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectEntry objectEntry =
			_defaultObjectEntryManager.expireObjectEntryByVersion(
				dtoConverterContext, objectEntryExternalReferenceCode,
				_objectDefinition1, 1);

		AssertUtils.assertEquals(
			WorkflowConstants.STATUS_EXPIRED,
			objectEntry.getStatus(
			).getCode());

		objectEntry = _defaultObjectEntryManager.expireObjectEntryByVersion(
			dtoConverterContext, objectEntryExternalReferenceCode,
			_objectDefinition1, 2);

		AssertUtils.assertEquals(
			WorkflowConstants.STATUS_EXPIRED,
			objectEntry.getStatus(
			).getCode());

		objectEntry = _defaultObjectEntryManager.getObjectEntry(
			companyId, _simpleDTOConverterContext,
			objectEntryExternalReferenceCode, _objectDefinition1, null);

		AssertUtils.assertEquals(
			WorkflowConstants.STATUS_EXPIRED,
			objectEntry.getStatus(
			).getCode());
	}

	@Test
	public void testGetObjectEntries() throws Exception {
		testGetObjectEntries(Collections.emptyMap());

		String picklistObjectFieldValue1 = _addListTypeEntry();

		ObjectEntry parentObjectEntry1 =
			_defaultObjectEntryManager.addObjectEntry(
				_simpleDTOConverterContext, _objectDefinition1,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							"textObjectFieldName", "Able"
						).put(
							"textObjectFieldNameExtension", "Baker"
						).build();
					}
				},
				ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectEntry childObjectEntry1 =
			_defaultObjectEntryManager.addObjectEntry(
				dtoConverterContext, _objectDefinition2,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							_objectRelationshipFieldName,
							parentObjectEntry1.getId()
						).put(
							"attachmentObjectFieldName",
							_getAttachmentObjectFieldValue()
						).put(
							"localizedDateObjectFieldName", "2024-01-01"
						).put(
							"longIntegerObjectFieldName", 21394167160L
						).put(
							"picklistObjectFieldName", picklistObjectFieldValue1
						).put(
							"textObjectFieldName", "aaa"
						).putAll(
							_localizedObjectFieldI18nValues
						).build();
					}
				},
				ObjectDefinitionConstants.SCOPE_COMPANY);

		assertEquals(
			childObjectEntry1,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						_localizedMultiselectPicklistObjectFieldName,
						Arrays.asList(_listTypeEntryKey3, _listTypeEntryKey4)
					).put(
						_objectRelationshipFieldName, parentObjectEntry1.getId()
					).put(
						"attachmentObjectFieldName",
						_getFileEntryId(childObjectEntry1)
					).put(
						"localizedBooleanObjectFieldName", true
					).put(
						"localizedDateObjectFieldName", "2024-01-01"
					).put(
						"localizedDateTimeObjectFieldName",
						"2024-04-04T04:04:04.000"
					).put(
						"localizedDecimalObjectFieldName", 5.0
					).put(
						"localizedIntegerObjectFieldName", 50
					).put(
						"localizedLongIntegerObjectFieldName", 50L
					).put(
						"localizedLongTextObjectFieldName",
						"en_US localizedLongTextObjectFieldValue"
					).put(
						"localizedPicklistObjectFieldName", _listTypeEntryKey1
					).put(
						"localizedPrecisionDecimalObjectFieldName",
						new BigDecimal("0.18775433729")
					).put(
						"localizedRichTextObjectFieldName",
						"en_US <i>localizedRichTextObjectFieldValue</i>"
					).put(
						"localizedRichTextObjectFieldNameRawText",
						"en_US localizedRichTextObjectFieldValue"
					).put(
						"localizedTextObjectFieldName",
						"en_US localizedTextObjectFieldValue1"
					).put(
						"picklistObjectFieldName", picklistObjectFieldValue1
					).put(
						"textObjectFieldName", "aaa"
					).putAll(
						_localizedObjectFieldI18nValues
					).build();
				}
			});

		// Sleep for 1 second to ensure that child object entry 1 and child
		// object entry 2 are created 1 second apart to ensure that the
		// tests with buildRangeExpression work

		Thread.sleep(1000);

		ObjectEntry parentObjectEntry2 =
			_defaultObjectEntryManager.addObjectEntry(
				_simpleDTOConverterContext, _objectDefinition1,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							"textObjectFieldName", RandomTestUtil.randomString()
						).build();
					}
				},
				ObjectDefinitionConstants.SCOPE_COMPANY);

		String picklistObjectFieldValue2 = _addListTypeEntry();

		_localizedObjectFieldI18nValues = HashMapBuilder.<String, Object>put(
			_localizedMultiselectPicklistObjectFieldName + "_i18n",
			HashMapBuilder.put(
				"en_US", Arrays.asList(_listTypeEntryKey3)
			).put(
				"pt_BR", Arrays.asList(_listTypeEntryKey3, _listTypeEntryKey4)
			).build()
		).put(
			"localizedBooleanObjectFieldName_i18n",
			HashMapBuilder.put(
				"en_US", false
			).put(
				"pt_BR", true
			).build()
		).put(
			"localizedDateObjectFieldName_i18n",
			HashMapBuilder.put(
				"en_US", "2022-01-01"
			).put(
				"pt_BR", "2023-01-01"
			).build()
		).put(
			"localizedDateTimeObjectFieldName_i18n",
			HashMapBuilder.put(
				"en_US", "2022-02-02T02:02:02.000"
			).put(
				"pt_BR", "2023-03-03T03:03:03.000"
			).build()
		).put(
			"localizedDecimalObjectFieldName_i18n",
			HashMapBuilder.put(
				"en_US", 10.0
			).put(
				"pt_BR", 5.4
			).build()
		).put(
			"localizedIntegerObjectFieldName_i18n",
			HashMapBuilder.put(
				"en_US", 100
			).put(
				"pt_BR", 54
			).build()
		).put(
			"localizedLongIntegerObjectFieldName_i18n",
			HashMapBuilder.put(
				"en_US", 100L
			).put(
				"pt_BR", 54L
			).build()
		).put(
			"localizedPicklistObjectFieldName_i18n",
			HashMapBuilder.put(
				"en_US",
				HashMapBuilder.put(
					"key", _listTypeEntryKey2
				).build()
			).put(
				"pt_BR",
				HashMapBuilder.put(
					"key", _listTypeEntryKey1
				).build()
			).build()
		).put(
			"localizedPrecisionDecimalObjectFieldName_i18n",
			HashMapBuilder.put(
				"en_US", new BigDecimal("0.28775433729")
			).put(
				"pt_BR", new BigDecimal("0.33750624632")
			).build()
		).put(
			"localizedTextObjectFieldName_i18n",
			HashMapBuilder.put(
				"en_US", "en_US localizedTextObjectFieldValue2"
			).put(
				"pt_BR", "pt_BR localizedTextObjectFieldValue2"
			).build()
		).build();

		ObjectEntry childObjectEntry2 =
			_defaultObjectEntryManager.addObjectEntry(
				dtoConverterContext, _objectDefinition2,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							_objectRelationshipFieldName,
							parentObjectEntry2.getId()
						).put(
							"localizedLongTextObjectFieldName",
							"en_US localizedLongTextObjectFieldValue"
						).put(
							"longIntegerObjectFieldName", 200L
						).put(
							"picklistObjectFieldName", picklistObjectFieldValue2
						).put(
							"textObjectFieldName", "aab"
						).putAll(
							_localizedObjectFieldI18nValues
						).build();
					}
				},
				ObjectDefinitionConstants.SCOPE_COMPANY);

		assertEquals(
			childObjectEntry2,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						_objectRelationshipFieldName, parentObjectEntry2.getId()
					).put(
						"localizedLongTextObjectFieldName",
						"en_US localizedLongTextObjectFieldValue"
					).put(
						"localizedTextObjectFieldName",
						"en_US localizedTextObjectFieldValue2"
					).put(
						"picklistObjectFieldName", picklistObjectFieldValue2
					).put(
						"textObjectFieldName", "aab"
					).putAll(
						_localizedObjectFieldI18nValues
					).build();
				}
			});

		// And/or with parentheses

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				StringBundler.concat(
					buildEqualsExpressionFilterString(
						"picklistObjectFieldName", picklistObjectFieldValue1),
					" and (",
					buildEqualsExpressionFilterString(
						"picklistObjectFieldName", picklistObjectFieldValue1),
					" or ",
					buildEqualsExpressionFilterString(
						"picklistObjectFieldName", picklistObjectFieldValue2),
					")")
			).build(),
			childObjectEntry1);

		// Contains expression

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildContainsExpressionFilterString(
					_objectRelationshipERCObjectFieldName,
					parentObjectEntry1.getExternalReferenceCode())
			).build(),
			childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildContainsExpressionFilterString(
					_objectRelationshipERCObjectFieldName,
					parentObjectEntry2.getExternalReferenceCode())
			).build(),
			childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildContainsExpressionFilterString(
					"id", RandomTestUtil.randomString())
			).build());
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildContainsExpressionFilterString(
					"id", String.valueOf(childObjectEntry1.getId()))
			).build(),
			childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildContainsExpressionFilterString(
					"id", String.valueOf(childObjectEntry2.getId()))
			).build(),
			childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildContainsExpressionFilterString(
					"localizedLongTextObjectFieldName",
					RandomTestUtil.randomString())
			).build());
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildContainsExpressionFilterString(
					"localizedLongTextObjectFieldName", "en_US")
			).build(),
			childObjectEntry1, childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildContainsExpressionFilterString(
					"localizedLongTextObjectFieldName", "localizedLongText")
			).build(),
			childObjectEntry1, childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildContainsExpressionFilterString(
					"textObjectFieldName", RandomTestUtil.randomString())
			).build());
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildContainsExpressionFilterString(
					"textObjectFieldName", "Aab")
			).build(),
			childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildContainsExpressionFilterString(
					"textObjectFieldName", "aa")
			).build(),
			childObjectEntry1, childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildContainsExpressionFilterString("textObjectFieldName", "b")
			).build(),
			childObjectEntry2);

		// Equals expression

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				buildEqualsExpressionFilterString(
					_objectRelationshipFieldName,
					String.valueOf(parentObjectEntry1.getId()))
			).build(),
			childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				buildEqualsExpressionFilterString(
					"longIntegerObjectFieldName", 21394167160L)
			).build(),
			childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				buildEqualsExpressionFilterString(
					"picklistObjectFieldName", picklistObjectFieldValue1)
			).put(
				"search", "aa"
			).build(),
			childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				buildEqualsExpressionFilterString(
					"picklistObjectFieldName", picklistObjectFieldValue2)
			).put(
				"search", "aa"
			).build(),
			childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				buildEqualsExpressionFilterString(
					_objectRelationshipERCObjectFieldName,
					parentObjectEntry1.getExternalReferenceCode())
			).build(),
			childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				buildEqualsExpressionFilterString(
					_objectRelationshipERCObjectFieldName,
					parentObjectEntry2.getExternalReferenceCode())
			).build(),
			childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				buildEqualsExpressionFilterString(
					"localizedBooleanObjectFieldName", true)
			).build(),
			childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				buildEqualsExpressionFilterString(
					"localizedDateObjectFieldName", _getTimestamp("2024-01-01"))
			).build(),
			childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				buildEqualsExpressionFilterString(
					"localizedDateTimeObjectFieldName",
					_getLocalDateTime("2024-04-04T04:04:04.000"))
			).build(),
			childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				buildEqualsExpressionFilterString(
					"localizedDecimalObjectFieldName", 5.0)
			).build(),
			childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				buildEqualsExpressionFilterString(
					"localizedIntegerObjectFieldName", 50)
			).build(),
			childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				buildEqualsExpressionFilterString(
					"localizedLongIntegerObjectFieldName", 50L)
			).build(),
			childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				buildEqualsExpressionFilterString(
					"localizedPicklistObjectFieldName", _listTypeEntryKey1)
			).build(),
			childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				buildEqualsExpressionFilterString(
					"localizedPrecisionDecimalObjectFieldName",
					new BigDecimal("0.18775433729"))
			).build(),
			childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				buildEqualsExpressionFilterString(
					"localizedTextObjectFieldName",
					"en_US localizedTextObjectFieldValue1")
			).build(),
			childObjectEntry1);

		// In expression

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildInExpressionFilterString(
					"id", true, childObjectEntry1.getId())
			).build(),
			childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildInExpressionFilterString(
					"id", false, childObjectEntry1.getId())
			).build(),
			childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildInExpressionFilterString(
					"picklistObjectFieldName", true, picklistObjectFieldValue1)
			).build(),
			childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildInExpressionFilterString(
					"picklistObjectFieldName", false, picklistObjectFieldValue1)
			).build(),
			childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildInExpressionFilterString(
					_objectRelationshipERCObjectFieldName, true,
					parentObjectEntry1.getExternalReferenceCode())
			).build(),
			childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildInExpressionFilterString(
					_objectRelationshipERCObjectFieldName, false,
					parentObjectEntry1.getExternalReferenceCode())
			).build(),
			childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildInExpressionFilterString(
					_objectRelationshipFieldName.substring(
						_objectRelationshipFieldName.lastIndexOf("_") + 1),
					true, parentObjectEntry1.getId())
			).build(),
			childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildInExpressionFilterString(
					_objectRelationshipFieldName.substring(
						_objectRelationshipFieldName.lastIndexOf("_") + 1),
					false, parentObjectEntry1.getId())
			).build(),
			childObjectEntry2);

		// Lambda expression

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				buildEqualsExpressionFilterString(
					"creatorId", adminUser.getUserId())
			).build(),
			childObjectEntry1, childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildLambdaExpressionFilterString(
					"status", true, WorkflowConstants.STATUS_APPROVED)
			).build(),
			childObjectEntry1, childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildLambdaExpressionFilterString(
					"status", false, WorkflowConstants.STATUS_APPROVED)
			).build());

		// Range expression

		String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				buildRangeExpression(
					childObjectEntry2.getDateCreated(), new Date(),
					"dateCreated", pattern)
			).build(),
			childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				buildRangeExpression(
					childObjectEntry2.getDateModified(), new Date(),
					"dateModified", pattern)
			).build(),
			childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				buildRangeExpression(
					childObjectEntry2.getDateModified(), new Date(),
					"dateModified", pattern)
			).build(),
			childObjectEntry2);

		testGetObjectEntries(
			HashMapBuilder.put(
				"search", "en_US"
			).build(),
			childObjectEntry1, childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"search", "pt_BR"
			).build());
		testGetObjectEntries(
			HashMapBuilder.put(
				"search", String.valueOf(childObjectEntry1.getId())
			).build(),
			childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"search", String.valueOf(childObjectEntry2.getId())
			).build(),
			childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"search", _listTypeEntryKey1
			).build(),
			childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"search", _listTypeEntryKey2
			).build(),
			childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"search", picklistObjectFieldValue1
			).build(),
			childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"search", picklistObjectFieldValue2
			).build(),
			childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"search", "aa"
			).build(),
			childObjectEntry1, childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "createDate:asc"
			).build(),
			childObjectEntry1, childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "createDate:desc"
			).build(),
			childObjectEntry2, childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "id:asc"
			).build(),
			childObjectEntry1, childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "id:desc"
			).build(),
			childObjectEntry2, childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "localizedBooleanObjectFieldName:asc"
			).build(),
			childObjectEntry2, childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "localizedBooleanObjectFieldName:desc"
			).build(),
			childObjectEntry1, childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "localizedDateObjectFieldName:asc"
			).build(),
			childObjectEntry2, childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "localizedDateObjectFieldName:desc"
			).build(),
			childObjectEntry1, childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "localizedDateTimeObjectFieldName:asc"
			).build(),
			childObjectEntry2, childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "localizedDateTimeObjectFieldName:desc"
			).build(),
			childObjectEntry1, childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "localizedDecimalObjectFieldName:asc"
			).build(),
			childObjectEntry1, childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "localizedDecimalObjectFieldName:desc"
			).build(),
			childObjectEntry2, childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "localizedIntegerObjectFieldName:asc"
			).build(),
			childObjectEntry1, childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "localizedIntegerObjectFieldName:desc"
			).build(),
			childObjectEntry2, childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "localizedLongIntegerObjectFieldName:asc"
			).build(),
			childObjectEntry1, childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "localizedLongIntegerObjectFieldName:desc"
			).build(),
			childObjectEntry2, childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "localizedPrecisionDecimalObjectFieldName:asc"
			).build(),
			childObjectEntry1, childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "localizedPrecisionDecimalObjectFieldName:desc"
			).build(),
			childObjectEntry2, childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "localizedTextObjectFieldName:asc"
			).build(),
			childObjectEntry1, childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "localizedTextObjectFieldName:desc"
			).build(),
			childObjectEntry2, childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "textObjectFieldName:asc"
			).build(),
			childObjectEntry1, childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"sort", "textObjectFieldName:desc"
			).build(),
			childObjectEntry2, childObjectEntry1);

		// Search

		testGetObjectEntries(
			HashMapBuilder.put(
				"search", String.valueOf(parentObjectEntry1.getId())
			).build(),
			childObjectEntry1);

		ObjectField objectField = objectFieldLocalService.fetchObjectField(
			_objectDefinition1.getObjectDefinitionId(), "textObjectFieldName");

		_objectDefinition1.setTitleObjectFieldId(
			objectField.getObjectFieldId());

		_objectDefinition1 =
			objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition1);

		testGetObjectEntries(
			HashMapBuilder.put(
				"search", "Able"
			).build(),
			childObjectEntry1);

		objectField = objectFieldLocalService.fetchObjectField(
			_objectDefinition1.getObjectDefinitionId(),
			"textObjectFieldNameExtension");

		_objectDefinition1.setTitleObjectFieldId(
			objectField.getObjectFieldId());

		_objectDefinition1 =
			objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition1);

		testGetObjectEntries(
			HashMapBuilder.put(
				"search", "Baker"
			).build(),
			childObjectEntry1);

		objectField = objectFieldLocalService.getObjectField(
			_objectDefinition1.getObjectDefinitionId(),
			"externalReferenceCode");

		_objectDefinition1.setTitleObjectFieldId(
			objectField.getObjectFieldId());

		_objectDefinition1 =
			objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition1);

		testGetObjectEntries(
			HashMapBuilder.put(
				"search", parentObjectEntry1.getExternalReferenceCode()
			).build(),
			childObjectEntry1);

		objectField = objectFieldLocalService.fetchObjectField(
			_objectDefinition2.getObjectDefinitionId(),
			"attachmentObjectFieldName");

		_objectDefinition2.setTitleObjectFieldId(
			objectField.getObjectFieldId());

		objectDefinitionLocalService.updateObjectDefinition(_objectDefinition2);

		com.liferay.portal.kernel.repository.model.FileEntry fileEntry =
			_dlAppLocalService.getFileEntry(_getFileEntryId(childObjectEntry1));

		testGetObjectEntries(
			HashMapBuilder.put(
				"search", fileEntry.getTitle()
			).build(),
			childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"search", StringUtil.toLowerCase(fileEntry.getTitle())
			).build(),
			childObjectEntry1);

		// "Starts with" expression

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildStartsWithExpressionFilterString(
					_objectRelationshipERCObjectFieldName,
					parentObjectEntry1.getExternalReferenceCode())
			).build(),
			childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildStartsWithExpressionFilterString(
					_objectRelationshipERCObjectFieldName,
					parentObjectEntry2.getExternalReferenceCode())
			).build(),
			childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildStartsWithExpressionFilterString(
					"id", RandomTestUtil.randomString())
			).build());
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildStartsWithExpressionFilterString(
					"id", String.valueOf(childObjectEntry1.getId()))
			).build(),
			childObjectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildStartsWithExpressionFilterString(
					"id", String.valueOf(childObjectEntry2.getId()))
			).build(),
			childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildStartsWithExpressionFilterString(
					"localizedLongTextObjectFieldName",
					RandomTestUtil.randomString())
			).build());
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildStartsWithExpressionFilterString(
					"localizedLongTextObjectFieldName", "en_US")
			).build(),
			childObjectEntry1, childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildStartsWithExpressionFilterString(
					"localizedLongTextObjectFieldName", "localizedLongText")
			).build());
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildStartsWithExpressionFilterString(
					"textObjectFieldName", RandomTestUtil.randomString())
			).build());
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildStartsWithExpressionFilterString(
					"textObjectFieldName", "aa")
			).build(),
			childObjectEntry1, childObjectEntry2);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				_buildStartsWithExpressionFilterString(
					"textObjectFieldName", "b")
			).build());
	}

	@Test
	public void testGetObjectEntriesWithAccountEntryRestricted1()
		throws Exception {

		// Regular roles permissions should not be restricted by account entry

		AccountEntry accountEntry1 = _addAccountEntry();

		ObjectEntry objectEntry1 = _addObjectEntry(accountEntry1);

		AccountEntry accountEntry2 = _addAccountEntry();

		_addObjectEntry(accountEntry2);

		_user = _addUser();

		_assertObjectEntriesSize1(0);

		Role role = _addRoleUser(
			new String[] {ActionKeys.VIEW}, _objectDefinition3, _user);

		_assertObjectEntriesSize1(2);

		_resourcePermissionLocalService.removeResourcePermission(
			companyId, _objectDefinition3.getClassName(),
			ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
			role.getRoleId(), ActionKeys.VIEW);

		// Regular roles' individual permissions should not be restricted by
		// account entry

		_resourcePermissionLocalService.setResourcePermissions(
			companyId, _objectDefinition3.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntry1.getId()), role.getRoleId(),
			new String[] {ActionKeys.VIEW});

		_assertObjectEntriesSize1(1);

		_userLocalService.deleteRoleUser(role.getRoleId(), _user);

		_assertObjectEntriesSize1(0);

		// User should be able to view object entries for account entry 1
		// because he is a member of account entry 1

		Assert.assertTrue(
			AccountRoleConstants.isSharedRole(_accountAdministratorRole));

		AccountEntryUserRel accountEntryUserRel =
			_accountEntryUserRelLocalService.addAccountEntryUserRel(
				accountEntry1.getAccountEntryId(), _user.getUserId());

		_assertObjectEntriesSize1(1);

		_accountEntryUserRelLocalService.deleteAccountEntryUserRel(
			accountEntryUserRel);

		_assertObjectEntriesSize1(0);

		// User should be able to view object entries for account entry 1 and
		// account entry 2 because he is a member of an organization that
		// contains account entry 1 and account entry 2.

		Organization organization1 = OrganizationTestUtil.addOrganization();

		_addAccountEntryOrganizationRel(accountEntry1, organization1);
		_addAccountEntryOrganizationRel(accountEntry2, organization1);

		_user = _addUser();

		_organizationLocalService.addUserOrganization(
			_user.getUserId(), organization1.getOrganizationId());

		_assertObjectEntriesSize1(2);

		_deleteAccountEntryOrganizationRel(accountEntry2, organization1);

		_assertObjectEntriesSize1(1);

		_organizationLocalService.deleteUserOrganization(
			_user.getUserId(), organization1.getOrganizationId());

		_assertObjectEntriesSize1(0);

		_deleteAccountEntryOrganizationRel(accountEntry1, organization1);

		// Check suborganizations

		Organization suborganization1 = OrganizationTestUtil.addOrganization(
			organization1.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		_addAccountEntryOrganizationRel(accountEntry1, suborganization1);

		Organization organization2 = OrganizationTestUtil.addOrganization();

		Organization suborganization2 = OrganizationTestUtil.addOrganization(
			organization2.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		_addAccountEntryOrganizationRel(accountEntry2, suborganization2);

		_user = _addUser();

		_organizationLocalService.addUserOrganization(
			_user.getUserId(), organization1.getOrganizationId());

		_assertObjectEntriesSize1(1);

		_organizationLocalService.addUserOrganization(
			_user.getUserId(), suborganization2.getOrganizationId());

		_assertObjectEntriesSize1(2);

		_organizationLocalService.deleteUserOrganization(
			_user.getUserId(), suborganization2.getOrganizationId());

		_assertObjectEntriesSize1(1);

		_organizationLocalService.deleteUserOrganization(
			_user.getUserId(), organization1.getOrganizationId());

		_assertObjectEntriesSize1(0);
	}

	@Test
	public void testGetObjectEntriesWithAccountEntryRestricted2()
		throws Exception {

		// Object definitions inherit account entry restricted from the root
		// object definition

		AccountEntry accountEntry1 = _addAccountEntry();

		_createObjectEntryTree(accountEntry1, "1");

		AccountEntry accountEntry2 = _addAccountEntry();

		_createObjectEntryTree(accountEntry2, "2");

		_user = _addUser();

		TreeTestUtil.forEachNodeObjectDefinition(
			_tree.iterator(), objectDefinitionLocalService,
			objectDefinition -> _assertObjectEntriesSize1(objectDefinition, 0));

		_assignAccountEntryRole(accountEntry1, _buyerRole, _user);

		TreeTestUtil.forEachNodeObjectDefinition(
			_tree.iterator(), objectDefinitionLocalService,
			objectDefinition -> _assertObjectEntriesSize1(objectDefinition, 1));

		_assignAccountEntryRole(accountEntry2, _buyerRole, _user);

		TreeTestUtil.forEachNodeObjectDefinition(
			_tree.iterator(), objectDefinitionLocalService,
			objectDefinition -> _assertObjectEntriesSize1(objectDefinition, 2));
	}

	@Test
	public void testGetObjectEntriesWithAccountEntryRestricted3()
		throws Exception {

		// Regular roles permissions should not be restricted by account entry

		AccountEntry accountEntry1 = _addAccountEntry();

		ObjectEntry objectEntry1 = _addObjectEntry(accountEntry1);

		AccountEntry accountEntry2 = _addAccountEntry();

		_addObjectEntry(accountEntry2);

		_user = _addUser();

		_assertObjectEntriesSize2(0);

		Role role = _addRoleUser(
			new String[] {ActionKeys.VIEW}, _objectDefinition3, _user);

		_assertObjectEntriesSize2(2);

		_resourcePermissionLocalService.removeResourcePermission(
			companyId, _objectDefinition3.getClassName(),
			ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
			role.getRoleId(), ActionKeys.VIEW);

		// Regular roles' individual permissions should not be restricted by
		// account entry

		_resourcePermissionLocalService.setResourcePermissions(
			companyId, _objectDefinition3.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntry1.getId()), role.getRoleId(),
			new String[] {ActionKeys.VIEW});

		_assertObjectEntriesSize2(1);

		_userLocalService.deleteRoleUser(role.getRoleId(), _user);

		_assertObjectEntriesSize2(0);

		// User should be able to view object entries for account entry 1
		// because he is a member of account entry 1

		Assert.assertTrue(
			AccountRoleConstants.isSharedRole(_accountAdministratorRole));

		AccountEntryUserRel accountEntryUserRel =
			_accountEntryUserRelLocalService.addAccountEntryUserRel(
				accountEntry1.getAccountEntryId(), _user.getUserId());

		_assertObjectEntriesSize2(1);

		_accountEntryUserRelLocalService.deleteAccountEntryUserRel(
			accountEntryUserRel);

		_assertObjectEntriesSize2(0);

		// User should be able to view object entries for account entry 1 and
		// account entry 2 because he is a member of an organization that
		// contains account entry 1 and account entry 2.

		Organization organization1 = OrganizationTestUtil.addOrganization();

		_user = _addUser();

		_organizationLocalService.addUserOrganization(
			_user.getUserId(), organization1.getOrganizationId());

		_addAccountEntryOrganizationRel(accountEntry1, organization1);
		_addAccountEntryOrganizationRel(accountEntry2, organization1);

		_assertObjectEntriesSize2(2);

		_deleteAccountEntryOrganizationRel(accountEntry2, organization1);

		_assertObjectEntriesSize2(1);

		_organizationLocalService.deleteUserOrganization(
			_user.getUserId(), organization1.getOrganizationId());

		_assertObjectEntriesSize2(0);

		_deleteAccountEntryOrganizationRel(accountEntry1, organization1);

		// Check suborganizations

		Organization suborganization1 = OrganizationTestUtil.addOrganization(
			organization1.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		_addAccountEntryOrganizationRel(accountEntry1, suborganization1);

		Organization organization2 = OrganizationTestUtil.addOrganization();

		Organization suborganization2 = OrganizationTestUtil.addOrganization(
			organization2.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		_addAccountEntryOrganizationRel(accountEntry2, suborganization2);

		_user = _addUser();

		_organizationLocalService.addUserOrganization(
			_user.getUserId(), organization1.getOrganizationId());

		_assertObjectEntriesSize2(1);

		_organizationLocalService.addUserOrganization(
			_user.getUserId(), suborganization2.getOrganizationId());

		_assertObjectEntriesSize2(2);

		_organizationLocalService.deleteUserOrganization(
			_user.getUserId(), suborganization2.getOrganizationId());

		_assertObjectEntriesSize2(1);

		_organizationLocalService.deleteUserOrganization(
			_user.getUserId(), organization1.getOrganizationId());

		_assertObjectEntriesSize2(0);
	}

	@Test
	public void testGetObjectEntriesWithAggregationFacets() throws Exception {
		String textObjectFieldValue = RandomTestUtil.randomString();

		ObjectEntry parentObjectEntry1 = _addObjectEntry(
			_objectDefinition1,
			HashMapBuilder.<String, Object>put(
				"textObjectFieldName", textObjectFieldValue
			).build());
		ObjectEntry parentObjectEntry2 = _addObjectEntry(
			_objectDefinition1,
			HashMapBuilder.<String, Object>put(
				"textObjectFieldName", textObjectFieldValue
			).build());

		_user = _addUser();

		_addRoleUser(new String[] {ActionKeys.VIEW}, _objectDefinition1, _user);

		Page<ObjectEntry> page = _getPage(
			Collections.singletonMap("textObjectFieldName", StringPool.BLANK),
			_objectDefinition1);

		_assertAggregationFacetValue(2, textObjectFieldValue, page);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(adminUser));

		PrincipalThreadLocal.setName(adminUser.getUserId());

		_addObjectEntry(
			_objectDefinition2,
			HashMapBuilder.<String, Object>put(
				_objectRelationshipFieldName, parentObjectEntry1.getId()
			).build());
		_addObjectEntry(
			_objectDefinition2,
			HashMapBuilder.<String, Object>put(
				_objectRelationshipFieldName, parentObjectEntry2.getId()
			).build());

		ObjectEntry childObjectEntry = _addObjectEntry(
			_objectDefinition2,
			HashMapBuilder.<String, Object>put(
				_objectRelationshipFieldName, parentObjectEntry2.getId()
			).build());

		_addRoleUser(new String[] {ActionKeys.VIEW}, _objectDefinition2, _user);

		page = _getPage(
			Collections.singletonMap(
				_objectRelationshipFieldName, StringPool.BLANK),
			_objectDefinition2);

		_assertAggregationFacetValue(
			1, String.valueOf(parentObjectEntry1.getId()), page);
		_assertAggregationFacetValue(
			2, String.valueOf(parentObjectEntry2.getId()), page);

		_defaultObjectEntryManager.updateObjectEntry(
			_simpleDTOConverterContext, _objectDefinition2,
			childObjectEntry.getId(),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						_objectRelationshipFieldName, parentObjectEntry1.getId()
					).build();
				}
			});

		page = _getPage(
			Collections.singletonMap(
				_objectRelationshipFieldName, StringPool.BLANK),
			_objectDefinition2);

		_assertAggregationFacetValue(
			2, String.valueOf(parentObjectEntry1.getId()), page);
		_assertAggregationFacetValue(
			1, String.valueOf(parentObjectEntry2.getId()), page);

		_defaultObjectEntryManager.deleteObjectEntry(
			_objectDefinition2, childObjectEntry.getId());

		page = _getPage(
			Collections.singletonMap(
				_objectRelationshipFieldName, StringPool.BLANK),
			_objectDefinition2);

		_assertAggregationFacetValue(
			1, String.valueOf(parentObjectEntry1.getId()), page);
		_assertAggregationFacetValue(
			1, String.valueOf(parentObjectEntry2.getId()), page);
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testGetObjectEntryByVersion() throws Exception {
		_enableObjectEntryVersioning();

		ObjectEntry objectEntry1 = new ObjectEntry() {
			{
				externalReferenceCode = RandomTestUtil.randomString();
				keywords = new String[] {RandomTestUtil.randomString()};
				properties = HashMapBuilder.<String, Object>put(
					"textObjectFieldName", RandomTestUtil.randomString()
				).build();
				systemProperties = new SystemProperties() {
					{
						version = new Version() {
							{
								number = 1;
							}
						};
					}
				};
			}
		};

		_defaultObjectEntryManager.addObjectEntry(
			dtoConverterContext, _objectDefinition1, objectEntry1,
			ObjectDefinitionConstants.SCOPE_COMPANY);

		assertEquals(
			_defaultObjectEntryManager.getObjectEntryByVersion(
				dtoConverterContext, objectEntry1.getExternalReferenceCode(),
				_objectDefinition1, 1),
			objectEntry1);

		ObjectEntry objectEntry2 = new ObjectEntry() {
			{
				externalReferenceCode = RandomTestUtil.randomString();
				keywords = new String[] {RandomTestUtil.randomString()};
				properties = HashMapBuilder.<String, Object>put(
					"textObjectFieldName", RandomTestUtil.randomString()
				).build();
				systemProperties = new SystemProperties() {
					{
						version = new Version() {
							{
								number = 2;
							}
						};
					}
				};
			}
		};

		_defaultObjectEntryManager.updateObjectEntry(
			TestPropsValues.getCompanyId(), dtoConverterContext,
			objectEntry1.getExternalReferenceCode(), _objectDefinition1,
			objectEntry2, ObjectDefinitionConstants.SCOPE_COMPANY);

		assertEquals(
			_defaultObjectEntryManager.getObjectEntryByVersion(
				dtoConverterContext, objectEntry2.getExternalReferenceCode(),
				_objectDefinition1, 2),
			objectEntry2);
	}

	@Test
	public void testGetObjectEntryDocument() throws Exception {
		_objectEntryLocalService.addObjectEntry(
			adminUser.getUserId(), 0,
			_objectDefinition1.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", StringUtil.randomId()
			).put(
				"textObjectFieldNameExtension", StringUtil.randomId()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		SearchResponse searchResponse = _searcher.search(
			_searchRequestBuilderFactory.builder(
			).emptySearchEnabled(
				true
			).entryClassNames(
				_objectDefinition1.getClassName()
			).withSearchContext(
				searchContext -> {
					searchContext.setAttribute(
						Field.STATUS, WorkflowConstants.STATUS_ANY);
					searchContext.setAttribute(
						"objectDefinitionId",
						_objectDefinition1.getObjectDefinitionId());
					searchContext.setCompanyId(
						_objectDefinition1.getCompanyId());
					searchContext.setGroupIds(new long[] {0});
				}
			).build());

		SearchHits searchHits = searchResponse.getSearchHits();

		List<SearchHit> searchHitList = searchHits.getSearchHits();

		SearchHit searchHit = searchHitList.get(0);

		Document document = searchHit.getDocument();

		JSONObject objectEntryContentJSONObject =
			JSONFactoryUtil.createJSONObject(
				"{" + document.getString("objectEntryContent") + "}");

		for (ObjectField objectField :
				objectFieldLocalService.getObjectFields(
					_objectDefinition1.getObjectDefinitionId(), false)) {

			Assert.assertEquals(
				objectField.isIndexed(),
				objectEntryContentJSONObject.has(objectField.getName()));
		}
	}

	@Test
	public void testGetObjectEntryRelatedObjectEntriesWithAccountEntryRestricted()
		throws Exception {

		// Account entry restricted scope

		ObjectDefinition childObjectDefinition = _createObjectDefinition(
			Arrays.asList(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"textObjectFieldName"
				).build()));

		ObjectRelationship objectRelationship1 =
			_objectRelationshipLocalService.addObjectRelationship(
				null, adminUser.getUserId(),
				_objectDefinition3.getObjectDefinitionId(),
				childObjectDefinition.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		ObjectDefinition accountEntryObjectDefinition =
			objectDefinitionLocalService.fetchObjectDefinition(
				companyId, "AccountEntry");

		ObjectRelationship objectRelationship2 =
			_objectRelationshipLocalService.addObjectRelationship(
				null, adminUser.getUserId(),
				accountEntryObjectDefinition.getObjectDefinitionId(),
				childObjectDefinition.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		childObjectDefinition.setAccountEntryRestrictedObjectFieldId(
			objectRelationship2.getObjectFieldId2());

		childObjectDefinition.setAccountEntryRestricted(true);

		childObjectDefinition =
			objectDefinitionLocalService.updateObjectDefinition(
				childObjectDefinition);

		AccountEntry accountEntry = _addAccountEntry();

		ObjectEntry objectEntry1 = _defaultObjectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, _objectDefinition3,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"r_oneToManyRelationshipName_accountEntryId",
						accountEntry.getAccountEntryId()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectField objectField1 = objectFieldLocalService.getObjectField(
			objectRelationship1.getObjectFieldId2());
		ObjectField objectField2 = objectFieldLocalService.getObjectField(
			objectRelationship2.getObjectFieldId2());

		_defaultObjectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, childObjectDefinition,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						objectField1.getName(), objectEntry1.getId()
					).put(
						objectField2.getName(), accountEntry.getAccountEntryId()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_defaultObjectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, childObjectDefinition,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						objectField1.getName(), objectEntry1.getId()
					).put(
						objectField2.getName(),
						_addAccountEntry().getAccountEntryId()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_user = _addUser();

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry.getAccountEntryId(), _user.getUserId());

		Page<ObjectEntry> page =
			_defaultObjectEntryManager.getObjectEntryRelatedObjectEntries(
				_simpleDTOConverterContext, _objectDefinition3,
				objectEntry1.getId(), objectRelationship1.getName(), null);

		Collection<ObjectEntry> objectEntries = page.getItems();

		Assert.assertEquals(objectEntries.toString(), 1, objectEntries.size());

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(adminUser));

		PrincipalThreadLocal.setName(adminUser.getUserId());

		_defaultObjectEntryManager.deleteObjectEntry(
			_objectDefinition3, objectEntry1.getId());

		// Organization scope

		ObjectEntry objectEntry2 = _defaultObjectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, _objectDefinition3,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"r_oneToManyRelationshipName_accountEntryId",
						accountEntry.getAccountEntryId()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_defaultObjectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, childObjectDefinition,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						objectField1.getName(), objectEntry2.getId()
					).put(
						objectField2.getName(), accountEntry.getAccountEntryId()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_defaultObjectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, childObjectDefinition,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						objectField1.getName(), objectEntry2.getId()
					).put(
						objectField2.getName(),
						_addAccountEntry().getAccountEntryId()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		Organization organization = OrganizationTestUtil.addOrganization();

		_addAccountEntryOrganizationRel(accountEntry, organization);

		User user = _addUser();

		_organizationLocalService.addUserOrganization(
			user.getUserId(), organization.getOrganizationId());

		page = _defaultObjectEntryManager.getObjectEntryRelatedObjectEntries(
			_simpleDTOConverterContext, _objectDefinition3,
			objectEntry2.getId(), objectRelationship1.getName(), null);

		objectEntries = page.getItems();

		Assert.assertEquals(objectEntries.toString(), 1, objectEntries.size());

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(adminUser));

		PrincipalThreadLocal.setName(adminUser.getUserId());

		// Many to many relationships

		ObjectRelationship objectRelationship3 =
			_objectRelationshipLocalService.addObjectRelationship(
				null, adminUser.getUserId(),
				_objectDefinition3.getObjectDefinitionId(),
				childObjectDefinition.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null);

		ObjectEntry objectEntry3 = _defaultObjectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, _objectDefinition3,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"r_oneToManyRelationshipName_accountEntryId",
						accountEntry.getAccountEntryId()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectEntry objectEntry4 = _defaultObjectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, childObjectDefinition,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						objectField2.getName(), accountEntry.getAccountEntryId()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectRelationshipTestUtil.relateObjectEntries(
			objectEntry3.getId(), objectEntry4.getId(), objectRelationship3,
			adminUser.getUserId());

		objectEntry4 = _defaultObjectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, childObjectDefinition,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						objectField2.getName(),
						_addAccountEntry().getAccountEntryId()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectRelationshipTestUtil.relateObjectEntries(
			objectEntry3.getId(), objectEntry4.getId(), objectRelationship3,
			adminUser.getUserId());

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		PrincipalThreadLocal.setName(user.getUserId());

		page = _defaultObjectEntryManager.getObjectEntryRelatedObjectEntries(
			_simpleDTOConverterContext, _objectDefinition3,
			objectEntry3.getId(), objectRelationship3.getName(), null);

		objectEntries = page.getItems();

		Assert.assertEquals(objectEntries.toString(), 1, objectEntries.size());
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testGetVersionedObjectEntries() throws Exception {
		_enableObjectEntryVersioning();

		ObjectEntry objectEntry1 = new ObjectEntry() {
			{
				externalReferenceCode = RandomTestUtil.randomString();
				keywords = new String[] {RandomTestUtil.randomString()};
				properties = HashMapBuilder.<String, Object>put(
					"textObjectFieldName", RandomTestUtil.randomString()
				).build();
			}
		};

		_defaultObjectEntryManager.addObjectEntry(
			dtoConverterContext, _objectDefinition1, objectEntry1,
			ObjectDefinitionConstants.SCOPE_COMPANY);

		Page<ObjectEntry> page =
			_defaultObjectEntryManager.getVersionedObjectEntries(
				dtoConverterContext, objectEntry1.getExternalReferenceCode(),
				_objectDefinition1, null);

		assertEquals(
			(List<ObjectEntry>)page.getItems(),
			ListUtil.fromArray(objectEntry1));

		ObjectEntry objectEntry2 = new ObjectEntry() {
			{
				externalReferenceCode = RandomTestUtil.randomString();
				keywords = new String[] {RandomTestUtil.randomString()};
				properties = HashMapBuilder.<String, Object>put(
					"textObjectFieldName", RandomTestUtil.randomString()
				).build();
			}
		};

		_defaultObjectEntryManager.updateObjectEntry(
			TestPropsValues.getCompanyId(), dtoConverterContext,
			objectEntry1.getExternalReferenceCode(), _objectDefinition1,
			objectEntry2, ObjectDefinitionConstants.SCOPE_COMPANY);

		page = _defaultObjectEntryManager.getVersionedObjectEntries(
			dtoConverterContext, objectEntry2.getExternalReferenceCode(),
			_objectDefinition1, null);

		assertEquals(
			(List<ObjectEntry>)page.getItems(),
			ListUtil.fromArray(objectEntry1, objectEntry2));
	}

	@Test
	public void testPartialUpdateObjectEntry() throws Exception {
		LocalDateTime nowLocalDateTime = LocalDateTime.now();

		ObjectEntry objectEntry = _defaultObjectEntryManager.addObjectEntry(
			dtoConverterContext, _objectDefinition2,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"dateObjectFieldName",
						_simpleDateFormat.format(RandomTestUtil.nextDate())
					).put(
						"dateTimeObjectFieldName",
						nowLocalDateTime.truncatedTo(ChronoUnit.MILLIS)
					).put(
						"decimalObjectFieldName", RandomTestUtil.randomDouble()
					).put(
						"integerObjectFieldName", RandomTestUtil.randomInt()
					).put(
						"longIntegerObjectFieldName",
						RandomTestUtil.randomLong(
							ObjectFieldValidationConstants.
								BUSINESS_TYPE_LONG_VALUE_MIN,
							ObjectFieldValidationConstants.
								BUSINESS_TYPE_LONG_VALUE_MAX)
					).put(
						"multiselectPicklistObjectFieldName",
						Collections.singletonList(_addListTypeEntry())
					).put(
						"precisionDecimalObjectFieldName",
						new BigDecimal(
							String.valueOf(RandomTestUtil.randomDouble()))
					).put(
						"richTextObjectFieldName",
						StringBundler.concat(
							"<i>", RandomTestUtil.randomString(), "</i>")
					).put(
						"textObjectFieldName", "textObjectFieldValue"
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		Map<String, Object> objectEntryProperties =
			HashMapBuilder.<String, Object>put(
				"dateObjectFieldName", "2023-08-20"
			).put(
				"dateTimeObjectFieldName", "2023-08-20T21:00:00.000"
			).put(
				"decimalObjectFieldName", 2.7
			).put(
				"integerObjectFieldName", 25
			).put(
				"longIntegerObjectFieldName", 200L
			).put(
				"multiselectPicklistObjectFieldName",
				() -> {
					ListTypeEntry listTypeEntry =
						_listTypeEntryLocalService.addListTypeEntry(
							null, adminUser.getUserId(),
							listTypeDefinition.getListTypeDefinitionId(),
							RandomTestUtil.randomString(),
							Collections.singletonMap(
								LocaleUtil.US, RandomTestUtil.randomString()),
							listTypeDefinition.isSystem());

					return Collections.singletonList(
						new ListEntry() {
							{
								key = listTypeEntry.getKey();
								name = listTypeEntry.getName(LocaleUtil.US);
							}
						});
				}
			).put(
				"precisionDecimalObjectFieldName",
				new BigDecimal("0.8755445767")
			).put(
				"richTextObjectFieldName", "<i>richTextObjectFieldNameValue</i>"
			).build();
		ObjectEntry parentObjectEntry1 = _addObjectEntry(
			_objectDefinition1,
			HashMapBuilder.<String, Object>put(
				"textObjectFieldName", RandomTestUtil.randomString()
			).build());

		assertEquals(
			_defaultObjectEntryManager.partialUpdateObjectEntry(
				_simpleDTOConverterContext, _objectDefinition2,
				objectEntry.getId(),
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>putAll(
							objectEntryProperties
						).put(
							_objectRelationshipFieldName,
							parentObjectEntry1.getId()
						).build();
					}
				}),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>putAll(
						objectEntryProperties
					).put(
						_objectRelationshipERCObjectFieldName,
						parentObjectEntry1.getExternalReferenceCode()
					).put(
						_objectRelationshipFieldName, parentObjectEntry1.getId()
					).put(
						"textObjectFieldName", "textObjectFieldValue"
					).build();
				}
			});

		ObjectEntry parentObjectEntry2 = _addObjectEntry(
			_objectDefinition1,
			HashMapBuilder.<String, Object>put(
				"textObjectFieldName", RandomTestUtil.randomString()
			).build());

		assertEquals(
			_defaultObjectEntryManager.partialUpdateObjectEntry(
				_simpleDTOConverterContext, _objectDefinition2,
				objectEntry.getId(),
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							_objectRelationshipERCObjectFieldName,
							parentObjectEntry2.getExternalReferenceCode()
						).put(
							_objectRelationshipFieldName,
							parentObjectEntry2.getId()
						).put(
							"dateObjectFieldName", () -> null
						).put(
							"dateTimeObjectFieldName", () -> null
						).build();
					}
				}),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>putAll(
						objectEntryProperties
					).put(
						_objectRelationshipERCObjectFieldName,
						parentObjectEntry2.getExternalReferenceCode()
					).put(
						_objectRelationshipFieldName, parentObjectEntry2.getId()
					).put(
						"dateObjectFieldName", () -> null
					).put(
						"dateTimeObjectFieldName", () -> null
					).build();
				}
			});
		assertEquals(
			_defaultObjectEntryManager.partialUpdateObjectEntry(
				_simpleDTOConverterContext, _objectDefinition2,
				objectEntry.getId(),
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							_objectRelationshipERCObjectFieldName,
							parentObjectEntry1.getExternalReferenceCode()
						).put(
							_objectRelationshipFieldName,
							parentObjectEntry1.getId()
						).build();
					}
				}),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>putAll(
						objectEntryProperties
					).put(
						_objectRelationshipERCObjectFieldName,
						parentObjectEntry1.getExternalReferenceCode()
					).put(
						_objectRelationshipFieldName, parentObjectEntry1.getId()
					).build();
				}
			});
		assertEquals(
			_defaultObjectEntryManager.partialUpdateObjectEntry(
				_simpleDTOConverterContext, _objectDefinition2,
				objectEntry.getId(),
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							_objectRelationshipERCObjectFieldName,
							parentObjectEntry2.getExternalReferenceCode()
						).put(
							_objectRelationshipFieldName,
							parentObjectEntry2.getId()
						).build();
					}
				}),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>putAll(
						objectEntryProperties
					).put(
						_objectRelationshipERCObjectFieldName,
						parentObjectEntry2.getExternalReferenceCode()
					).put(
						_objectRelationshipFieldName, parentObjectEntry2.getId()
					).build();
				}
			});
	}

	@Test
	public void testPartialUpdateObjectEntryWithAttachmentObjectField()
		throws Exception {

		ObjectDefinition objectDefinition = _createObjectDefinition(
			Collections.singletonList(
				new AttachmentObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"attachmentObjectFieldName"
				).objectFieldSettings(
					Arrays.asList(
						_createObjectFieldSetting(
							ObjectFieldSettingConstants.
								NAME_ACCEPTED_FILE_EXTENSIONS,
							"txt"),
						_createObjectFieldSetting(
							ObjectFieldSettingConstants.NAME_FILE_SOURCE,
							ObjectFieldSettingConstants.VALUE_USER_COMPUTER),
						_createObjectFieldSetting(
							ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE,
							"100"))
				).build()),
			ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectEntry objectEntry = _defaultObjectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, objectDefinition,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"attachmentObjectFieldName",
						_getAttachmentObjectFieldValue()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		long expectedFileEntryId = _getFileEntryId(objectEntry);

		Role role = _roleLocalService.getRole(
			companyId, RoleConstants.ADMINISTRATOR);

		User user = _addUser();

		_userLocalService.addRoleUser(role.getRoleId(), user);

		objectEntry = _defaultObjectEntryManager.partialUpdateObjectEntry(
			_simpleDTOConverterContext, objectDefinition, objectEntry.getId(),
			new ObjectEntry() {
				{
					properties = Collections.emptyMap();
				}
			});

		Assert.assertEquals(expectedFileEntryId, _getFileEntryId(objectEntry));

		objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Test
	public void testPartialUpdateObjectEntryWithLocalizedAttachmentObjectField()
		throws Exception {

		ObjectDefinition objectDefinition = _createObjectDefinition(
			Collections.singletonList(
				new AttachmentObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap("localizedAttachment")
				).localized(
					true
				).name(
					"localizedAttachment"
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
				).build()));

		long tempFileEntryId1 = _addTempFileEntry(
			objectDefinition.getPortletId(), StringUtil.randomString());

		ObjectEntry objectEntry = _defaultObjectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, objectDefinition,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"localizedAttachment_i18n",
						HashMapBuilder.put(
							"en_US", tempFileEntryId1
						).put(
							"pt_BR", tempFileEntryId1
						).build()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		AssertUtils.assertFailure(
			NoSuchFileEntryException.class,
			StringBundler.concat(
				"No FileEntry exists with the key {fileEntryId=",
				tempFileEntryId1, "}"),
			() -> _dlAppLocalService.getFileEntry(tempFileEntryId1));

		FileEntry fileEntry1 = (FileEntry)_getLocalizedPropertyValue(
			"en_US", objectEntry, "localizedAttachment");

		Assert.assertNotNull(
			_dlAppLocalService.getFileEntry(fileEntry1.getId()));

		long tempFileEntryId2 = _addTempFileEntry(
			objectDefinition.getPortletId(), StringUtil.randomString());

		objectEntry = _defaultObjectEntryManager.partialUpdateObjectEntry(
			_simpleDTOConverterContext, objectDefinition, objectEntry.getId(),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"localizedAttachment_i18n",
						HashMapBuilder.put(
							"en_US", fileEntry1.getId()
						).put(
							"pt_BR", tempFileEntryId2
						).build()
					).build();
				}
			});

		AssertUtils.assertFailure(
			NoSuchFileEntryException.class,
			StringBundler.concat(
				"No FileEntry exists with the key {fileEntryId=",
				tempFileEntryId2, "}"),
			() -> _dlAppLocalService.getFileEntry(tempFileEntryId2));
		Assert.assertNotNull(
			_dlAppLocalService.getFileEntry(fileEntry1.getId()));

		FileEntry fileEntry2 = (FileEntry)_getLocalizedPropertyValue(
			"pt_BR", objectEntry, "localizedAttachment");

		Assert.assertNotNull(
			_dlAppLocalService.getFileEntry(fileEntry2.getId()));

		objectEntry = _defaultObjectEntryManager.partialUpdateObjectEntry(
			_simpleDTOConverterContext, objectDefinition, objectEntry.getId(),
			new ObjectEntry() {
				{
					properties = Collections.emptyMap();
				}
			});

		Assert.assertNotNull(
			_dlAppLocalService.getFileEntry(fileEntry1.getId()));
		Assert.assertNotNull(
			_dlAppLocalService.getFileEntry(fileEntry2.getId()));

		objectEntry = _defaultObjectEntryManager.partialUpdateObjectEntry(
			_simpleDTOConverterContext, objectDefinition, objectEntry.getId(),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"localizedAttachment_i18n",
						HashMapBuilder.put(
							"en_US", 0L
						).put(
							"pt_BR", fileEntry2.getId()
						).build()
					).build();
				}
			});

		AssertUtils.assertFailure(
			NoSuchFileEntryException.class,
			StringBundler.concat(
				"No FileEntry exists with the key {fileEntryId=",
				fileEntry1.getId(), "}"),
			() -> _dlAppLocalService.getFileEntry(fileEntry1.getId()));
		Assert.assertNotNull(
			_dlAppLocalService.getFileEntry(fileEntry2.getId()));

		_defaultObjectEntryManager.deleteObjectEntry(
			objectDefinition, objectEntry.getId());

		AssertUtils.assertFailure(
			NoSuchFileEntryException.class,
			StringBundler.concat(
				"No FileEntry exists with the key {fileEntryId=",
				fileEntry2.getId(), "}"),
			() -> _dlAppLocalService.getFileEntry(fileEntry2.getId()));

		objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Test
	public void testPartialUpdateObjectEntryWithReadOnlyFields()
		throws Exception {

		ObjectEntry objectEntry = _defaultObjectEntryManager.addObjectEntry(
			dtoConverterContext, _objectDefinition2,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"attachmentObjectFieldName",
						_getAttachmentObjectFieldValue()
					).put(
						"picklistObjectFieldName", _addListTypeEntry()
					).put(
						"textObjectFieldName", "Foo"
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		Object value1 = objectEntry.getPropertyValue(
			"attachmentObjectFieldName");
		Object value2 = objectEntry.getPropertyValue("picklistObjectFieldName");

		objectEntry = _defaultObjectEntryManager.updateObjectEntry(
			TestPropsValues.getCompanyId(), dtoConverterContext,
			objectEntry.getExternalReferenceCode(), _objectDefinition2,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.put(
						"attachmentObjectFieldName", value1
					).put(
						"picklistObjectFieldName", value2
					).put(
						"textObjectFieldName", "Edited"
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		Assert.assertEquals(
			"Edited", objectEntry.getPropertyValue("textObjectFieldName"));
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testRestoreObjectEntryByVersion() throws Exception {
		_enableObjectEntryVersioning();

		ObjectEntry objectEntry1 = new ObjectEntry() {
			{
				externalReferenceCode = RandomTestUtil.randomString();
				keywords = new String[] {RandomTestUtil.randomString()};
				properties = HashMapBuilder.<String, Object>put(
					"textObjectFieldName", RandomTestUtil.randomString()
				).build();
				systemProperties = new SystemProperties() {
					{
						version = new Version() {
							{
								number = 1;
							}
						};
					}
				};
			}
		};

		_defaultObjectEntryManager.addObjectEntry(
			dtoConverterContext, _objectDefinition1, objectEntry1,
			ObjectDefinitionConstants.SCOPE_COMPANY);

		assertEquals(
			_defaultObjectEntryManager.getObjectEntryByVersion(
				dtoConverterContext, objectEntry1.getExternalReferenceCode(),
				_objectDefinition1, 1),
			objectEntry1);

		ObjectEntry objectEntry2 = new ObjectEntry() {
			{
				externalReferenceCode = RandomTestUtil.randomString();
				keywords = new String[] {RandomTestUtil.randomString()};
				properties = HashMapBuilder.<String, Object>put(
					"textObjectFieldName", RandomTestUtil.randomString()
				).build();
				systemProperties = new SystemProperties() {
					{
						version = new Version() {
							{
								number = 2;
							}
						};
					}
				};
			}
		};

		_defaultObjectEntryManager.updateObjectEntry(
			TestPropsValues.getCompanyId(), dtoConverterContext,
			objectEntry1.getExternalReferenceCode(), _objectDefinition1,
			objectEntry2, ObjectDefinitionConstants.SCOPE_COMPANY);

		assertEquals(
			_defaultObjectEntryManager.getObjectEntryByVersion(
				dtoConverterContext, objectEntry2.getExternalReferenceCode(),
				_objectDefinition1, 2),
			objectEntry2);

		assertEquals(
			_defaultObjectEntryManager.restoreObjectEntryByVersion(
				dtoConverterContext, objectEntry2.getExternalReferenceCode(),
				_objectDefinition1, 1),
			new ObjectEntry() {
				{
					setExternalReferenceCode(
						objectEntry1::getExternalReferenceCode);
					setKeywords(objectEntry1::getKeywords);
					setProperties(objectEntry1::getProperties);
					setSystemProperties(
						() -> new SystemProperties() {
							{
								setVersion(
									() -> new Version() {
										{
											number = 3;
										}
									});
							}
						});
				}
			});
	}

	@Test
	public void testSearchObjectEntriesWithAccountEntryRestricted()
		throws Exception {

		AccountEntry accountEntry1 = _addAccountEntry();

		_objectEntryLocalService.addObjectEntry(
			adminUser.getUserId(), 0,
			_objectDefinition3.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"r_oneToManyRelationshipName1_accountEntryId",
				accountEntry1.getAccountEntryId()
			).put(
				"textObjectFieldName1", "Able"
			).put(
				"textObjectFieldName2", "Baker"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		AccountEntry accountEntry2 = _addAccountEntry();

		_objectEntryLocalService.addObjectEntry(
			adminUser.getUserId(), 0,
			_objectDefinition3.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"r_oneToManyRelationshipName1_accountEntryId",
				accountEntry2.getAccountEntryId()
			).put(
				"textObjectFieldName1", "Charlie"
			).put(
				"textObjectFieldName2", "Delta"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_assignAccountEntryRole(accountEntry1, _buyerRole, _addUser());

		_user = _addUser();

		_assignAccountEntryRole(accountEntry2, _buyerRole, _user);

		_assertObjectEntriesSize1(_objectDefinition3, "Able", 0);
		_assertObjectEntriesSize1(_objectDefinition3, "Baker", 0);
		_assertObjectEntriesSize1(_objectDefinition3, "Charlie", 1);
		_assertObjectEntriesSize1(_objectDefinition3, "Delta", 1);
	}

	@Test
	public void testUpdateObjectEntry() throws Exception {
		ObjectEntry objectEntry = _objectEntryManager.addObjectEntry(
			dtoConverterContext, _objectDefinition2,
			new ObjectEntry() {
				{
					properties = new HashMap<>(_localizedObjectFieldI18nValues);
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		Role role = _roleLocalService.getRole(
			companyId, RoleConstants.ADMINISTRATOR);

		_user = _addUser();

		_userLocalService.addRoleUser(role.getRoleId(), _user);

		_assertLocalizedValues(
			HashMapBuilder.<String, Object>put(
				_localizedMultiselectPicklistObjectFieldName,
				Arrays.asList(_listTypeEntryKey3, _listTypeEntryKey4)
			).put(
				"localizedBooleanObjectFieldName", true
			).put(
				"localizedDateObjectFieldName", "2024-01-01"
			).put(
				"localizedDateTimeObjectFieldName", "2024-04-04T04:04:04.000"
			).put(
				"localizedLongTextObjectFieldName",
				"en_US localizedLongTextObjectFieldValue"
			).put(
				"localizedPicklistObjectFieldName", _listTypeEntryKey1
			).put(
				"localizedRichTextObjectFieldName",
				"en_US <i>localizedRichTextObjectFieldValue</i>"
			).put(
				"localizedRichTextObjectFieldNameRawText",
				"en_US localizedRichTextObjectFieldValue"
			).put(
				"localizedTextObjectFieldName",
				"en_US localizedTextObjectFieldValue1"
			).putAll(
				_localizedObjectFieldI18nValues
			).build(),
			"en_US", objectEntry.getId());
		_assertLocalizedValues(
			HashMapBuilder.<String, Object>put(
				_localizedMultiselectPicklistObjectFieldName,
				Arrays.asList(_listTypeEntryKey4)
			).put(
				"localizedBooleanObjectFieldName", false
			).put(
				"localizedDateObjectFieldName", "2025-01-01"
			).put(
				"localizedDateTimeObjectFieldName", "2025-05-05T05:05:05.000"
			).put(
				"localizedLongTextObjectFieldName",
				"pt_BR localizedLongTextObjectFieldValue"
			).put(
				"localizedPicklistObjectFieldName", _listTypeEntryKey2
			).put(
				"localizedRichTextObjectFieldName",
				"pt_BR <i>localizedRichTextObjectFieldValue</i>"
			).put(
				"localizedRichTextObjectFieldNameRawText",
				"pt_BR localizedRichTextObjectFieldValue"
			).put(
				"localizedTextObjectFieldName",
				"pt_BR localizedTextObjectFieldValue1"
			).putAll(
				_localizedObjectFieldI18nValues
			).build(),
			"pt_BR", objectEntry.getId());

		_localizedObjectFieldI18nValues = HashMapBuilder.<String, Object>put(
			"localizedLongTextObjectFieldName_i18n",
			HashMapBuilder.put(
				"en_US", "en_US localizedLongTextObjectFieldValue"
			).put(
				"invalid_languageId", ""
			).build()
		).put(
			"localizedRichTextObjectFieldName_i18n",
			HashMapBuilder.put(
				"ar_SA", "ar_SA localizedRichTextObjectFieldValue"
			).build()
		).build();

		_updateLocalizedObjectEntryValues(
			_objectDefinition2, objectEntry.getId(),
			_localizedObjectFieldI18nValues);

		_localizedObjectFieldI18nValues.putAll(
			HashMapBuilder.<String, Object>put(
				"localizedLongTextObjectFieldName_i18n",
				HashMapBuilder.put(
					"en_US", "en_US localizedLongTextObjectFieldValue"
				).build()
			).put(
				"localizedTextObjectFieldName_i18n", new HashMap<>()
			).build());

		_assertLocalizedValues(
			HashMapBuilder.<String, Object>put(
				"localizedBooleanObjectFieldName", false
			).put(
				"localizedLongTextObjectFieldName",
				"en_US localizedLongTextObjectFieldValue"
			).put(
				"localizedRichTextObjectFieldName",
				"ar_SA localizedRichTextObjectFieldValue"
			).put(
				"localizedRichTextObjectFieldNameRawText",
				"ar_SA localizedRichTextObjectFieldValue"
			).put(
				"localizedTextObjectFieldName", ""
			).putAll(
				_localizedObjectFieldI18nValues
			).build(),
			"ar_SA", objectEntry.getId());
		_assertLocalizedValues(
			HashMapBuilder.<String, Object>put(
				"localizedBooleanObjectFieldName", false
			).put(
				"localizedLongTextObjectFieldName",
				"en_US localizedLongTextObjectFieldValue"
			).put(
				"localizedRichTextObjectFieldName", ""
			).put(
				"localizedRichTextObjectFieldNameRawText", ""
			).put(
				"localizedTextObjectFieldName", ""
			).putAll(
				_localizedObjectFieldI18nValues
			).build(),
			"en_US", objectEntry.getId());
		_assertLocalizedValues(
			HashMapBuilder.<String, Object>put(
				"localizedBooleanObjectFieldName", false
			).put(
				"localizedLongTextObjectFieldName",
				"en_US localizedLongTextObjectFieldValue"
			).put(
				"localizedRichTextObjectFieldName", ""
			).put(
				"localizedRichTextObjectFieldNameRawText", ""
			).put(
				"localizedTextObjectFieldName", ""
			).putAll(
				_localizedObjectFieldI18nValues
			).build(),
			"pt_BR", objectEntry.getId());

		_localizedObjectFieldI18nValues = HashMapBuilder.<String, Object>put(
			_localizedMultiselectPicklistObjectFieldName + "_i18n",
			HashMapBuilder.put(
				"en_US", Arrays.asList(_listTypeEntryKey3)
			).put(
				"pt_BR", Arrays.asList(_listTypeEntryKey3, _listTypeEntryKey4)
			).build()
		).put(
			"localizedBooleanObjectFieldName_i18n",
			HashMapBuilder.put(
				"ar_SA", false
			).put(
				"ca_ES", false
			).put(
				"en_US", true
			).put(
				"pt_BR", true
			).build()
		).put(
			"localizedDateObjectFieldName_i18n",
			HashMapBuilder.put(
				"en_US", "2024-02-02"
			).put(
				"pt_BR", "2025-02-02"
			).build()
		).put(
			"localizedDateTimeObjectFieldName_i18n",
			HashMapBuilder.put(
				"en_US", "2022-02-02T02:02:02.000"
			).put(
				"pt_BR", "2023-03-03T03:03:03.000"
			).build()
		).put(
			"localizedLongTextObjectFieldName_i18n",
			HashMapBuilder.put(
				"en_US", "en_US localizedLongTextObjectFieldValue"
			).put(
				"pt_BR", ""
			).build()
		).put(
			"localizedPicklistObjectFieldName_i18n",
			HashMapBuilder.put(
				"en_US", _listTypeEntryKey2
			).put(
				"pt_BR", _listTypeEntryKey1
			).build()
		).put(
			"localizedRichTextObjectFieldName_i18n",
			HashMapBuilder.put(
				"ar_SA", "ar_SA localizedRichTextObjectFieldValue"
			).put(
				"ca_ES", "ca_ES localizedRichTextObjectFieldValue"
			).put(
				"en_US", "en_US <i>localizedRichTextObjectFieldValue</i>"
			).build()
		).put(
			"localizedTextObjectFieldName_i18n",
			HashMapBuilder.put(
				"en_US", "en_US localizedTextObjectFieldValue1"
			).put(
				"pt_BR", "pt_BR localizedTextObjectFieldValue1"
			).build()
		).build();

		_updateLocalizedObjectEntryValues(
			_objectDefinition2, objectEntry.getId(),
			_localizedObjectFieldI18nValues);

		_localizedObjectFieldI18nValues.put(
			"localizedLongTextObjectFieldName_i18n",
			HashMapBuilder.put(
				"en_US", "en_US localizedLongTextObjectFieldValue"
			).build());

		_assertLocalizedValues(
			HashMapBuilder.<String, Object>put(
				"localizedBooleanObjectFieldName", false
			).put(
				"localizedLongTextObjectFieldName",
				"en_US localizedLongTextObjectFieldValue"
			).put(
				"localizedRichTextObjectFieldName",
				"ar_SA localizedRichTextObjectFieldValue"
			).put(
				"localizedRichTextObjectFieldNameRawText",
				"ar_SA localizedRichTextObjectFieldValue"
			).put(
				"localizedTextObjectFieldName",
				"en_US localizedTextObjectFieldValue1"
			).putAll(
				_localizedObjectFieldI18nValues
			).build(),
			"ar_SA", objectEntry.getId());
		_assertLocalizedValues(
			HashMapBuilder.<String, Object>put(
				"localizedBooleanObjectFieldName", false
			).put(
				"localizedLongTextObjectFieldName",
				"en_US localizedLongTextObjectFieldValue"
			).put(
				"localizedRichTextObjectFieldName",
				"ca_ES localizedRichTextObjectFieldValue"
			).put(
				"localizedRichTextObjectFieldNameRawText",
				"ca_ES localizedRichTextObjectFieldValue"
			).put(
				"localizedTextObjectFieldName",
				"en_US localizedTextObjectFieldValue1"
			).putAll(
				_localizedObjectFieldI18nValues
			).build(),
			"ca_ES", objectEntry.getId());
		_assertLocalizedValues(
			HashMapBuilder.<String, Object>put(
				_localizedMultiselectPicklistObjectFieldName,
				Arrays.asList(_listTypeEntryKey3)
			).put(
				"localizedBooleanObjectFieldName", true
			).put(
				"localizedDateObjectFieldName", "2024-02-02"
			).put(
				"localizedDateTimeObjectFieldName", "2022-02-02T02:02:02.000"
			).put(
				"localizedLongTextObjectFieldName",
				"en_US localizedLongTextObjectFieldValue"
			).put(
				"localizedPicklistObjectFieldName", _listTypeEntryKey2
			).put(
				"localizedRichTextObjectFieldName",
				"en_US <i>localizedRichTextObjectFieldValue</i>"
			).put(
				"localizedRichTextObjectFieldNameRawText",
				"en_US localizedRichTextObjectFieldValue"
			).put(
				"localizedTextObjectFieldName",
				"en_US localizedTextObjectFieldValue1"
			).putAll(
				_localizedObjectFieldI18nValues
			).build(),
			"en_US", objectEntry.getId());
		_assertLocalizedValues(
			HashMapBuilder.<String, Object>put(
				_localizedMultiselectPicklistObjectFieldName,
				Arrays.asList(_listTypeEntryKey3, _listTypeEntryKey4)
			).put(
				"localizedBooleanObjectFieldName", true
			).put(
				"localizedDateObjectFieldName", "2025-02-02"
			).put(
				"localizedDateTimeObjectFieldName", "2023-03-03T03:03:03.000"
			).put(
				"localizedLongTextObjectFieldName",
				"en_US localizedLongTextObjectFieldValue"
			).put(
				"localizedPicklistObjectFieldName", _listTypeEntryKey1
			).put(
				"localizedRichTextObjectFieldName",
				"en_US <i>localizedRichTextObjectFieldValue</i>"
			).put(
				"localizedRichTextObjectFieldNameRawText",
				"en_US localizedRichTextObjectFieldValue"
			).put(
				"localizedTextObjectFieldName",
				"pt_BR localizedTextObjectFieldValue1"
			).putAll(
				_localizedObjectFieldI18nValues
			).build(),
			"pt_BR", objectEntry.getId());
	}

	@Test
	public void testUpdateObjectEntryWithAccountEntryRestricted1()
		throws Exception {

		// Regular roles' company scope permissions should not be restricted by
		// account entry

		AccountEntry accountEntry1 = _addAccountEntry();

		ObjectEntry objectEntry1 = _addObjectEntry(accountEntry1);

		AccountEntry accountEntry2 = _addAccountEntry();

		ObjectEntry objectEntry2 = _addObjectEntry(accountEntry2);

		_user = _addUser();

		Role role = _addRoleUser(
			new String[] {ActionKeys.UPDATE, ActionKeys.VIEW},
			_objectDefinition3, _user);

		_defaultObjectEntryManager.updateObjectEntry(
			_simpleDTOConverterContext, _objectDefinition3,
			objectEntry1.getId(), objectEntry1);

		_defaultObjectEntryManager.updateObjectEntry(
			_simpleDTOConverterContext, _objectDefinition3,
			objectEntry2.getId(), objectEntry2);

		_resourcePermissionLocalService.removeResourcePermission(
			companyId, _objectDefinition3.getClassName(),
			ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
			role.getRoleId(), ActionKeys.UPDATE);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have UPDATE permission for ",
				_objectDefinition3.getClassName(), StringPool.SPACE,
				objectEntry1.getId()),
			() -> _defaultObjectEntryManager.updateObjectEntry(
				_simpleDTOConverterContext, _objectDefinition3,
				objectEntry1.getId(), objectEntry1));
		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have UPDATE permission for ",
				_objectDefinition3.getClassName(), StringPool.SPACE,
				objectEntry2.getId()),
			() -> _defaultObjectEntryManager.updateObjectEntry(
				_simpleDTOConverterContext, _objectDefinition3,
				objectEntry2.getId(), objectEntry2));

		// Regular roles' individual permissions should not be restricted by
		// account entry

		_resourcePermissionLocalService.setResourcePermissions(
			companyId, _objectDefinition3.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntry1.getId()), role.getRoleId(),
			new String[] {ActionKeys.UPDATE});

		_defaultObjectEntryManager.updateObjectEntry(
			_simpleDTOConverterContext, _objectDefinition3,
			objectEntry1.getId(), objectEntry1);

		// Account entry scope

		_addResourcePermission(ActionKeys.UPDATE, _accountAdministratorRole);
		_addResourcePermission(ActionKeys.VIEW, _accountAdministratorRole);

		_user = _addUser();

		_assignAccountEntryRole(
			accountEntry1, _accountAdministratorRole, _user);

		_addResourcePermission(ActionKeys.VIEW, _buyerRole);

		_assignAccountEntryRole(accountEntry2, _buyerRole, _user);

		_assertObjectEntriesSize1(2);

		_defaultObjectEntryManager.updateObjectEntry(
			_simpleDTOConverterContext, _objectDefinition3,
			objectEntry1.getId(), objectEntry1);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have UPDATE permission for ",
				_objectDefinition3.getClassName(), StringPool.SPACE,
				objectEntry2.getId()),
			() -> _defaultObjectEntryManager.updateObjectEntry(
				_simpleDTOConverterContext, _objectDefinition3,
				objectEntry2.getId(), objectEntry2));

		// Organization scope

		_user = _addUser();

		Organization organization1 = OrganizationTestUtil.addOrganization();

		_addResourcePermission(ActionKeys.VIEW, _accountManagerRole);

		_assignOrganizationRole(organization1, _accountManagerRole, _user);

		_addAccountEntryOrganizationRel(accountEntry1, organization1);

		Organization organization2 = OrganizationTestUtil.addOrganization();

		_addAccountEntryOrganizationRel(accountEntry2, organization2);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have UPDATE permission for ",
				_objectDefinition3.getClassName(), StringPool.SPACE,
				objectEntry1.getId()),
			() -> _defaultObjectEntryManager.updateObjectEntry(
				_simpleDTOConverterContext, _objectDefinition3,
				objectEntry1.getId(), objectEntry1));

		_addResourcePermission(ActionKeys.UPDATE, _accountManagerRole);

		_defaultObjectEntryManager.updateObjectEntry(
			_simpleDTOConverterContext, _objectDefinition3,
			objectEntry1.getId(), objectEntry1);

		_removeResourcePermission(ActionKeys.UPDATE, _accountManagerRole);

		// Suborganization scope

		Organization suborganization1 = OrganizationTestUtil.addOrganization(
			organization1.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		_addAccountEntryOrganizationRel(accountEntry1, suborganization1);

		Organization suborganization2 = OrganizationTestUtil.addOrganization(
			organization2.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		_addAccountEntryOrganizationRel(accountEntry2, suborganization2);

		_user = _addUser();

		_assignOrganizationRole(organization1, _accountManagerRole, _user);

		_assertObjectEntriesSize1(1);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have UPDATE permission for ",
				_objectDefinition3.getClassName(), StringPool.SPACE,
				objectEntry1.getId()),
			() -> _defaultObjectEntryManager.updateObjectEntry(
				_simpleDTOConverterContext, _objectDefinition3,
				objectEntry1.getId(), objectEntry1));

		_addResourcePermission(ActionKeys.UPDATE, _accountManagerRole);

		_defaultObjectEntryManager.updateObjectEntry(
			_simpleDTOConverterContext, _objectDefinition3,
			objectEntry1.getId(), objectEntry1);
	}

	@Test
	public void testUpdateObjectEntryWithAccountEntryRestricted2()
		throws Exception {

		// Object definitions inherit account entry restricted from the root
		// object definition

		AccountEntry accountEntry1 = _addAccountEntry();

		Tree tree = _createObjectEntryTree(accountEntry1, StringPool.BLANK);

		_user = _addUser();

		_assignAccountEntryRole(accountEntry1, _buyerRole, _user);

		_testUpdateObjectEntryWithAccountEntryRestricted2(
			ActionKeys.UPDATE, tree);

		// Users can delete object entries from accounts that they belong to

		_addResourcePermission(
			ActionKeys.UPDATE, _rootObjectDefinition, _buyerRole);

		TreeTestUtil.forEachNodeObjectEntry(
			tree.iterator(), _objectEntryLocalService,
			objectEntry -> {
				ObjectDefinition objectDefinition =
					objectDefinitionLocalService.fetchObjectDefinition(
						objectEntry.getObjectDefinitionId());

				_defaultObjectEntryManager.updateObjectEntry(
					_simpleDTOConverterContext, objectDefinition,
					objectEntry.getObjectEntryId(),
					_defaultObjectEntryManager.getObjectEntry(
						_simpleDTOConverterContext, objectDefinition,
						objectEntry.getObjectEntryId()));
			});

		// Users cannot delete object entries from accounts that they do not
		// belong to

		AccountEntry accountEntry2 = _addAccountEntry();

		_user = _addUser();

		_assignAccountEntryRole(accountEntry2, _accountManagerRole, _user);

		_addResourcePermission(
			ActionKeys.UPDATE, _rootObjectDefinition, _accountManagerRole);

		_testUpdateObjectEntryWithAccountEntryRestricted2(
			ActionKeys.VIEW, tree);

		_addResourcePermission(
			ActionKeys.VIEW, _rootObjectDefinition, _accountManagerRole);

		_testUpdateObjectEntryWithAccountEntryRestricted2(
			ActionKeys.VIEW, tree);
	}

	@Test
	public void testUpdateObjectEntryWithAccountEntryRestricted3()
		throws Exception {

		// Account roles' permissions should not be restricted by account entry

		ObjectDefinition objectDefinition = _createObjectDefinition(
			Arrays.asList(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"textObjectFieldName"
				).build()));

		ObjectDefinition accountEntryObjectDefinition =
			objectDefinitionLocalService.fetchObjectDefinition(
				companyId, "AccountEntry");

		ObjectRelationship objectRelationship1 =
			_objectRelationshipLocalService.addObjectRelationship(
				null, adminUser.getUserId(),
				accountEntryObjectDefinition.getObjectDefinitionId(),
				objectDefinition.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"oneToManyRelationship", false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		objectDefinition.setAccountEntryRestrictedObjectFieldId(
			objectRelationship1.getObjectFieldId2());

		objectDefinition.setAccountEntryRestricted(true);

		objectDefinition = objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);

		_addResourcePermission(ActionKeys.UPDATE, _accountAdministratorRole);
		_addResourcePermission(ActionKeys.VIEW, _accountAdministratorRole);
		_addResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY, _accountAdministratorRole);

		_addResourcePermission(
			ActionKeys.UPDATE, objectDefinition, _accountAdministratorRole);
		_addResourcePermission(
			ActionKeys.VIEW, objectDefinition, _accountAdministratorRole);
		_addResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY, objectDefinition,
			_accountAdministratorRole);

		AccountEntry accountEntry1 = _addAccountEntry();
		AccountEntry accountEntry2 = _addAccountEntry();

		_user = _addUser();

		_assignAccountEntryRole(
			accountEntry1, _accountAdministratorRole, _user);
		_assignAccountEntryRole(
			accountEntry2, _accountAdministratorRole, _user);

		ObjectRelationship objectRelationship2 =
			_objectRelationshipLocalService.addObjectRelationship(
				null, adminUser.getUserId(),
				_objectDefinition3.getObjectDefinitionId(),
				objectDefinition.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		ObjectEntry objectEntry1 = _addObjectEntry(accountEntry1);

		ObjectEntry objectEntry2 = _defaultObjectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, objectDefinition,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"r_oneToManyRelationship_accountEntryId",
						accountEntry2.getAccountEntryId()
					).put(
						"textObjectFieldName", RandomTestUtil.randomString()
					).put(
						() -> {
							ObjectField objectField =
								_objectFieldLocalService.getObjectField(
									objectRelationship2.getObjectFieldId2());

							return objectField.getName();
						},
						objectEntry1.getId()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		String textObjectFieldName = RandomTestUtil.randomString();

		Map<String, Object> customObjectEntryProperties =
			HashMapBuilder.<String, Object>put(
				"textObjectFieldName", textObjectFieldName
			).build();

		assertEquals(
			_defaultObjectEntryManager.updateObjectEntry(
				_simpleDTOConverterContext, objectDefinition,
				objectEntry2.getId(),
				new ObjectEntry() {
					{
						properties = new HashMap<>(customObjectEntryProperties);
					}
				}),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"textObjectFieldName", textObjectFieldName
					).build();
				}
			});
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testUpdateObjectEntryWithScheduleDates() throws Exception {
		ObjectDefinition objectDefinition = _createObjectDefinition(
			Collections.singletonList(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"textObjectFieldName"
				).build()));

		ObjectEntry objectEntry = _defaultObjectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, objectDefinition,
			new ObjectEntry() {
				{
					setDisplayDate(new Date());
					setExpirationDate(new Date());
					setProperties(
						HashMapBuilder.<String, Object>put(
							"textObjectFieldName", RandomTestUtil.randomString()
						).build());
					setReviewDate(new Date());
				}
			},
			null);

		Date date = new Date(
			System.currentTimeMillis() + TimeUnit.DAY.toMillis(1));

		objectEntry = _defaultObjectEntryManager.updateObjectEntry(
			_simpleDTOConverterContext, objectDefinition, objectEntry.getId(),
			new ObjectEntry() {
				{
					setDisplayDate(date);
					setExpirationDate(date);
					setProperties(
						HashMapBuilder.<String, Object>put(
							"textObjectFieldName", RandomTestUtil.randomString()
						).build());
					setReviewDate(date);
				}
			});

		Assert.assertEquals(date, objectEntry.getDisplayDate());
		Assert.assertEquals(date, objectEntry.getExpirationDate());
		Assert.assertEquals(date, objectEntry.getReviewDate());

		objectEntry = _defaultObjectEntryManager.updateObjectEntry(
			_simpleDTOConverterContext, objectDefinition, objectEntry.getId(),
			new ObjectEntry() {
				{
					setDisplayDate((Date)null);
					setExpirationDate((Date)null);
					setProperties(
						HashMapBuilder.<String, Object>put(
							"textObjectFieldName", RandomTestUtil.randomString()
						).build());
					setReviewDate((Date)null);
				}
			});

		Assert.assertNull(objectEntry.getDisplayDate());
		Assert.assertNull(objectEntry.getExpirationDate());
		Assert.assertNull(objectEntry.getReviewDate());
	}

	@Rule
	public TestName testName = new TestName();

	@Override
	protected void assertObjectEntryProperties(
			ObjectEntry actualObjectEntry,
			Map<String, Object> actualObjectEntryProperties,
			Map.Entry<String, Object> expectedEntry)
		throws Exception {

		if (Objects.equals(
				expectedEntry.getKey(),
				_localizedMultiselectPicklistObjectFieldName)) {

			_assertListEntries(
				(List<ListEntry>)actualObjectEntryProperties.get(
					expectedEntry.getKey()),
				(List<Object>)expectedEntry.getValue());
		}
		else if (Objects.equals(
					expectedEntry.getKey(),
					_localizedMultiselectPicklistObjectFieldName + "_i18n")) {

			Map<String, Object> actualValues =
				(Map<String, Object>)actualObjectEntryProperties.get(
					expectedEntry.getKey());
			Map<String, Object> expectedValues =
				(Map<String, Object>)expectedEntry.getValue();

			for (Map.Entry<String, Object> entry : expectedValues.entrySet()) {
				_assertListEntries(
					(List<ListEntry>)actualValues.get(entry.getKey()),
					(List<Object>)entry.getValue());
			}
		}
		else if (Objects.equals(
					expectedEntry.getKey(),
					_objectRelationshipERCObjectFieldName)) {

			Assert.assertEquals(
				expectedEntry.getValue(),
				actualObjectEntryProperties.get(expectedEntry.getKey()));

			assertEquals(
				(ObjectEntry)actualObjectEntryProperties.get(
					StringUtil.replaceLast(
						_objectRelationshipFieldName, "Id", StringPool.BLANK)),
				_objectEntryManager.getObjectEntry(
					_objectDefinition1.getCompanyId(),
					_simpleDTOConverterContext,
					GetterUtil.getString(expectedEntry.getValue()),
					_objectDefinition1, null));
		}
		else if (Objects.equals(
					expectedEntry.getKey(), "attachmentObjectFieldName")) {

			Object expectedValue = expectedEntry.getValue();
			FileEntry fileEntry = (FileEntry)actualObjectEntryProperties.get(
				expectedEntry.getKey());

			if ((expectedValue == null) && (fileEntry == null)) {
				return;
			}

			if (Validator.isNumber(expectedValue.toString())) {
				Assert.assertEquals(expectedValue, fileEntry.getId());
			}
			else {
				FileEntry expectedEntryFileEntry = (FileEntry)expectedValue;

				Assert.assertEquals(
					expectedEntryFileEntry.getId(), fileEntry.getId());
			}

			DLFileEntry dlFileEntry = _dlFileEntryLocalService.getFileEntry(
				fileEntry.getId());

			Assert.assertEquals(fileEntry.getName(), dlFileEntry.getFileName());

			Link link = fileEntry.getLink();

			Assert.assertEquals(link.getLabel(), dlFileEntry.getFileName());

			com.liferay.portal.kernel.repository.model.FileEntry
				repositoryFileEntry = _dlAppService.getFileEntry(
					fileEntry.getId());

			String url = _dlURLHelper.getDownloadURL(
				repositoryFileEntry, repositoryFileEntry.getFileVersion(), null,
				StringPool.BLANK);

			url = HttpComponentsUtil.addParameter(
				url, "objectDefinitionExternalReferenceCode",
				_objectDefinition2.getExternalReferenceCode());
			url = HttpComponentsUtil.addParameter(
				url, "objectEntryExternalReferenceCode",
				actualObjectEntry.getExternalReferenceCode());

			Assert.assertEquals(url, link.getHref());
		}
		else if (Objects.equals(
					expectedEntry.getKey(), "dateObjectFieldName") ||
				 Objects.equals(
					 expectedEntry.getKey(), "localizedDateObjectFieldName")) {

			if ((expectedEntry.getValue() == null) &&
				(actualObjectEntryProperties.get(expectedEntry.getKey()) ==
					null)) {

				return;
			}

			String expectedEntryValue = String.valueOf(
				expectedEntry.getValue());

			if (!StringUtil.endsWith(expectedEntryValue, "T00:00:00.000Z")) {
				expectedEntryValue += "T00:00:00.000Z";
			}

			Assert.assertEquals(
				expectedEntry.getKey(), expectedEntryValue,
				String.valueOf(
					actualObjectEntryProperties.get(expectedEntry.getKey())));
		}
		else if (Objects.equals(
					expectedEntry.getKey(),
					"localizedDateObjectFieldName_i18n")) {

			Map<String, Object> actualValues =
				(Map<String, Object>)actualObjectEntryProperties.get(
					expectedEntry.getKey());
			Map<String, Object> expectedValues =
				(Map<String, Object>)expectedEntry.getValue();

			for (Map.Entry<String, Object> entry : expectedValues.entrySet()) {
				Assert.assertEquals(
					_getTimestamp(String.valueOf(entry.getValue())),
					_getTimestamp(
						String.valueOf(actualValues.get(entry.getKey()))));
			}
		}
		else if (Objects.equals(
					expectedEntry.getKey(),
					"localizedDateTimeObjectFieldName_i18n")) {

			Map<String, Object> actualValues =
				(Map<String, Object>)actualObjectEntryProperties.get(
					expectedEntry.getKey());
			Map<String, Object> expectedValues =
				(Map<String, Object>)expectedEntry.getValue();

			for (Map.Entry<String, Object> entry : expectedValues.entrySet()) {
				Object expectedValue = entry.getValue();

				if (expectedValue instanceof String) {
					expectedValue = Timestamp.valueOf(
						_getLocalDateTime(String.valueOf(expectedValue)));
				}

				Assert.assertEquals(
					expectedValue,
					Timestamp.valueOf(
						_getLocalDateTime(
							String.valueOf(actualValues.get(entry.getKey())))));
			}
		}
		else if (Objects.equals(
					expectedEntry.getKey(),
					"localizedPicklistObjectFieldName") ||
				 Objects.equals(
					 expectedEntry.getKey(), "picklistObjectFieldName")) {

			_assertListEntry(
				(ListEntry)actualObjectEntryProperties.get(
					expectedEntry.getKey()),
				expectedEntry.getValue());
		}
		else if (Objects.equals(
					expectedEntry.getKey(),
					"localizedPicklistObjectFieldName_i18n")) {

			Map<String, Object> actualValues =
				(Map<String, Object>)actualObjectEntryProperties.get(
					expectedEntry.getKey());
			Map<String, Object> expectedValues =
				(Map<String, Object>)expectedEntry.getValue();

			for (Map.Entry<String, Object> entry : expectedValues.entrySet()) {
				_assertListEntry(
					(ListEntry)actualValues.get(entry.getKey()),
					entry.getValue());
			}
		}
		else if (Objects.equals(
					expectedEntry.getKey(), "richTextObjectFieldNameRawText")) {

			Assert.assertEquals(
				HtmlParserUtil.extractText(
					String.valueOf(expectedEntry.getValue())),
				String.valueOf(
					actualObjectEntryProperties.get(expectedEntry.getKey())));
		}
		else {
			super.assertObjectEntryProperties(
				actualObjectEntry, actualObjectEntryProperties, expectedEntry);
		}
	}

	@Override
	protected Page<ObjectEntry> getObjectEntries(
			Map<String, String> context, Sort[] sorts)
		throws Exception {

		return _defaultObjectEntryManager.getObjectEntries(
			companyId, _objectDefinition2, null, null, dtoConverterContext,
			context.get("filter"), null, context.get("search"), sorts);
	}

	private AccountEntry _addAccountEntry() throws Exception {
		return _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, adminUser.getUserId(), 0L,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, null, RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());
	}

	private void _addAccountEntryOrganizationRel(
			AccountEntry accountEntry, Organization organization)
		throws Exception {

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			accountEntry.getAccountEntryId(), organization.getOrganizationId());
	}

	private void _addAggregationObjectField(
			String argumentObjectFieldName, String functionName,
			long objectDefinitionId, String objectFieldName,
			String objectRelationshipName)
		throws Exception {

		List<ObjectFieldSetting> objectFieldSettings = new ArrayList<>();

		objectFieldSettings.add(
			_createObjectFieldSetting(
				ObjectFieldSettingConstants.NAME_FUNCTION, functionName));

		if (!Objects.equals(
				functionName, ObjectFieldSettingConstants.VALUE_COUNT)) {

			objectFieldSettings.add(
				_createObjectFieldSetting(
					ObjectFieldSettingConstants.NAME_OBJECT_FIELD_NAME,
					argumentObjectFieldName));
		}

		objectFieldSettings.add(
			_createObjectFieldSetting(
				ObjectFieldSettingConstants.NAME_OBJECT_RELATIONSHIP_NAME,
				objectRelationshipName));

		_addCustomObjectField(
			new AggregationObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				objectFieldName
			).objectDefinitionId(
				objectDefinitionId
			).objectFieldSettings(
				objectFieldSettings
			).build());
	}

	private long _addAndAssertObjectEntryWithPicklistObjectField(
			String actualPicklistObjectFieldValue,
			String expectedPicklistObjectFieldValue,
			ObjectDefinition objectDefinition)
		throws Exception {

		ObjectEntry objectEntry = _defaultObjectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, objectDefinition,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"picklistObjectFieldName",
						actualPicklistObjectFieldValue
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_assertObjectEntryWithPicklistObjectField(
			expectedPicklistObjectFieldValue, objectEntry);

		return objectEntry.getId();
	}

	private void _addCustomObjectField(ObjectField objectField)
		throws Exception {

		_objectFieldService.addCustomObjectField(
			objectField.getExternalReferenceCode(),
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

	private String _addListTypeEntry() throws Exception {
		ListTypeEntry listTypeEntry =
			_listTypeEntryLocalService.addListTypeEntry(
				null, adminUser.getUserId(),
				listTypeDefinition.getListTypeDefinitionId(),
				RandomTestUtil.randomString(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				listTypeDefinition.isSystem());

		return listTypeEntry.getKey();
	}

	private void _addObjectAction(
			ObjectDefinition objectDefinition, String objectActionTriggerKey)
		throws Exception {

		Class<?> clazz = getClass();

		_objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), true, null,
			RandomTestUtil.randomString(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_GROOVY, objectActionTriggerKey,
			UnicodePropertiesBuilder.create(
				true
			).put(
				"script",
				StringUtil.read(
					clazz,
					StringBundler.concat(
						"dependencies/", clazz.getSimpleName(),
						StringPool.PERIOD, testName.getMethodName(), ".groovy"))
			).build(),
			false);
	}

	private ObjectEntry _addObjectEntry(AccountEntry accountEntry)
		throws Exception {

		return _defaultObjectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, _objectDefinition3,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"r_oneToManyRelationshipName1_accountEntryId",
						accountEntry.getAccountEntryId()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);
	}

	private ObjectEntry _addObjectEntry(
			ObjectDefinition objectDefinition, Map<String, Object> values)
		throws Exception {

		return _defaultObjectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, objectDefinition,
			new ObjectEntry() {
				{
					properties = new HashMap<>(values);
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);
	}

	private void _addObjectFieldSettingWithDefaultValue(
			String defaultValue, ObjectDefinition objectDefinition,
			String objectFieldName)
		throws Exception {

		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			objectDefinition.getObjectDefinitionId(), objectFieldName);

		_objectFieldSettingLocalService.addObjectFieldSetting(
			TestPropsValues.getUserId(), objectField.getObjectFieldId(),
			ObjectFieldSettingConstants.NAME_DEFAULT_VALUE, defaultValue);
		_objectFieldSettingLocalService.addObjectFieldSetting(
			TestPropsValues.getUserId(), objectField.getObjectFieldId(),
			ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE,
			ObjectFieldSettingConstants.VALUE_INPUT_AS_VALUE);
	}

	private void _addRelatedObjectEntries(
			ObjectDefinition objectDefinition1,
			ObjectDefinition objectDefinition2,
			String objectEntryExternalReferenceCode1,
			String objectEntryExternalReferenceCode2,
			ObjectRelationship objectRelationship)
		throws Exception {

		_defaultObjectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, objectDefinition1,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"externalReferenceCode",
						objectEntryExternalReferenceCode1
					).put(
						"textObjectFieldName", RandomTestUtil.randomString()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		String objectRelationshipERCObjectFieldName = StringBundler.concat(
			"r_", objectRelationship.getName(), "_",
			StringUtil.replaceLast(
				objectDefinition1.getPKObjectFieldName(), "Id", "ERC"));

		_defaultObjectEntryManager.addObjectEntry(
			_simpleDTOConverterContext, objectDefinition2,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						objectRelationshipERCObjectFieldName,
						objectEntryExternalReferenceCode1
					).put(
						"externalReferenceCode",
						objectEntryExternalReferenceCode2
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);
	}

	private void _addResourcePermission(
			String actionId, ObjectDefinition objectDefinition, Role role)
		throws Exception {

		String name = objectDefinition.getClassName();

		if (Objects.equals(actionId, ObjectActionKeys.ADD_OBJECT_ENTRY)) {
			name = objectDefinition.getResourceName();
		}

		_resourcePermissionLocalService.addResourcePermission(
			companyId, name, ResourceConstants.SCOPE_GROUP_TEMPLATE, "0",
			role.getRoleId(), actionId);
	}

	private void _addResourcePermission(String actionId, Role role)
		throws Exception {

		_addResourcePermission(actionId, _objectDefinition3, role);
	}

	private Role _addRoleUser(
			String[] actionIds, ObjectDefinition objectDefinition, User user)
		throws Exception {

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		String name = objectDefinition.getClassName();

		if (ArrayUtil.contains(actionIds, ObjectActionKeys.ADD_OBJECT_ENTRY)) {
			name = objectDefinition.getResourceName();
		}

		_resourcePermissionLocalService.setResourcePermissions(
			companyId, name, ResourceConstants.SCOPE_COMPANY,
			String.valueOf(companyId), role.getRoleId(), actionIds);

		_userLocalService.addRoleUser(role.getRoleId(), user);

		return role;
	}

	private long _addTempFileEntry(String folderName, String title)
		throws Exception {

		com.liferay.portal.kernel.repository.model.FileEntry fileEntry =
			TempFileEntryUtil.addTempFileEntry(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId(),
				folderName, TempFileEntryUtil.getTempFileName(title + ".txt"),
				FileUtil.createTempFile(RandomTestUtil.randomBytes()),
				ContentTypes.TEXT_PLAIN);

		return fileEntry.getFileEntryId();
	}

	private User _addUser() throws Exception {
		User user = UserTestUtil.addUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		PrincipalThreadLocal.setName(user.getUserId());

		return user;
	}

	private void _assertAggregationFacetValue(
		Integer expectedNumberOfOccurrences, String facetValueTerm,
		Page<ObjectEntry> page) {

		List<Facet> facets = page.getFacets();

		Assert.assertFalse(ListUtil.isEmpty(facets));

		Facet facet = facets.get(0);

		List<Facet.FacetValue> facetValues = ListUtil.filter(
			facet.getFacetValues(),
			facetValue -> Objects.equals(facetValue.getTerm(), facetValueTerm));

		Assert.assertFalse(ListUtil.isEmpty(facetValues));

		Facet.FacetValue facetValue = facetValues.get(0);

		Assert.assertEquals(
			expectedNumberOfOccurrences, facetValue.getNumberOfOccurrences());
	}

	private void _assertCountAggregationObjectFieldValue(
			int expectedValue, ObjectEntry objectEntry)
		throws Exception {

		assertEquals(
			_defaultObjectEntryManager.getObjectEntry(
				_simpleDTOConverterContext, _objectDefinition1,
				objectEntry.getId()),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"countAggregationObjectFieldName1",
						String.valueOf(expectedValue)
					).build();
				}
			});
	}

	private void _assertFilteredObjectEntriesSize(
			String filterString, ObjectDefinition objectDefinition, long size)
		throws Exception {

		Page<ObjectEntry> page = _defaultObjectEntryManager.getObjectEntries(
			companyId, objectDefinition, null, null,
			new DefaultDTOConverterContext(
				false, Collections.emptyMap(), dtoConverterRegistry, null,
				LocaleUtil.getDefault(), null, _user),
			filterString, null, null, null);

		Collection<ObjectEntry> objectEntries = page.getItems();

		Assert.assertEquals(
			objectEntries.toString(), size, objectEntries.size());
	}

	private void _assertListEntries(
		List<ListEntry> listEntries, List<Object> values) {

		if (ListUtil.isEmpty(values)) {
			return;
		}

		for (Object value : values) {
			Assert.assertTrue(
				ListUtil.exists(
					listEntries,
					listEntry -> {
						if (value instanceof String) {
							return Objects.equals(value, listEntry.getKey());
						}

						return Objects.equals(value, listEntry);
					}));
		}
	}

	private void _assertListEntry(ListEntry listEntry, Object value) {
		if (value instanceof String) {
			Assert.assertEquals(value, listEntry.getKey());
		}
		else {
			Assert.assertEquals(value, listEntry);
		}
	}

	private void _assertLocalizedValues(
			Map<String, Object> expectedLocalizedValues, String languageId,
			long objectEntryId)
		throws Exception {

		// DTOConverterContext#getLocale

		assertEquals(
			_defaultObjectEntryManager.getObjectEntry(
				new DefaultDTOConverterContext(
					false, Collections.emptyMap(), dtoConverterRegistry, null,
					LocaleUtil.fromLanguageId(languageId), null, _user),
				_objectDefinition2, objectEntryId),
			new ObjectEntry() {
				{
					properties = new HashMap<>(expectedLocalizedValues);
				}
			});

		// User#getLanguageId

		String originalLanguageId = _user.getLanguageId();

		try {
			_user.setLanguageId(languageId);

			_user = _userLocalService.updateUser(_user);

			assertEquals(
				_defaultObjectEntryManager.getObjectEntry(
					new DefaultDTOConverterContext(
						false, Collections.emptyMap(), dtoConverterRegistry,
						null, null, null, _user),
					_objectDefinition2, objectEntryId),
				new ObjectEntry() {
					{
						properties = new HashMap<>(expectedLocalizedValues);
					}
				});
		}
		finally {
			_user.setLanguageId(originalLanguageId);

			_user = _userLocalService.updateUser(_user);
		}
	}

	private void _assertObjectEntriesSize1(long size) throws Exception {
		_assertObjectEntriesSize1(_objectDefinition3, size);
	}

	private void _assertObjectEntriesSize1(
			ObjectDefinition objectDefinition, long size)
		throws Exception {

		_assertObjectEntriesSize1(objectDefinition, null, size);
	}

	private void _assertObjectEntriesSize1(
			ObjectDefinition objectDefinition, String search, long size)
		throws Exception {

		Page<ObjectEntry> page = _defaultObjectEntryManager.getObjectEntries(
			companyId, objectDefinition, null, null,
			new DefaultDTOConverterContext(
				false, Collections.emptyMap(), dtoConverterRegistry, null,
				LocaleUtil.getDefault(), null, _user),
			StringPool.BLANK, null, search, null);

		Collection<ObjectEntry> objectEntries = page.getItems();

		Assert.assertEquals(
			objectEntries.toString(), size, objectEntries.size());
	}

	private void _assertObjectEntriesSize2(long size) throws Exception {
		Page<ObjectEntry> page = _defaultObjectEntryManager.getObjectEntries(
			companyId, _objectDefinition3, null, null,
			new DefaultDTOConverterContext(
				false, Collections.emptyMap(), dtoConverterRegistry, null,
				LocaleUtil.getDefault(), null, _user),
			(Filter)null, null, StringPool.BLANK, null);

		Collection<ObjectEntry> objectEntries = page.getItems();

		Assert.assertEquals(
			objectEntries.toString(), size, objectEntries.size());
	}

	private void _assertObjectEntryWithPicklistObjectField(
		String expectedPicklistObjectFieldValue, ObjectEntry objectEntry) {

		if (expectedPicklistObjectFieldValue == null) {
			Map<String, Object> properties = objectEntry.getProperties();

			Assert.assertNull(properties.get("picklistObjectFieldName"));
		}
		else {
			Assert.assertEquals(
				expectedPicklistObjectFieldValue,
				_getListEntryKey(objectEntry));
		}
	}

	private void _assertPicklistOjectField(
			ListEntry expectedListEntry, Object picklistObjectFieldValue)
		throws Exception {

		assertEquals(
			_defaultObjectEntryManager.addObjectEntry(
				dtoConverterContext, _objectDefinition2,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							"picklistObjectFieldName", picklistObjectFieldValue
						).build();
					}
				},
				ObjectDefinitionConstants.SCOPE_COMPANY),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"picklistObjectFieldName", expectedListEntry
					).build();
				}
			});
	}

	private void _assignAccountEntryRole(
			AccountEntry accountEntry, Role role, User user)
		throws Exception {

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry.getAccountEntryId(), user.getUserId());

		_userGroupRoleLocalService.addUserGroupRole(
			user.getUserId(), accountEntry.getAccountEntryGroupId(),
			role.getRoleId());
	}

	private void _assignOrganizationRole(
			Organization organization, Role role, User user)
		throws Exception {

		_organizationLocalService.addUserOrganization(
			user.getUserId(), organization.getOrganizationId());

		Group group = _groupLocalService.getOrganizationGroup(
			companyId, organization.getOrganizationId());

		_userGroupRoleLocalService.addUserGroupRole(
			user.getUserId(), group.getGroupId(), role.getRoleId());
	}

	private String _buildContainsExpressionFilterString(
		String fieldName, String value) {

		return StringBundler.concat("contains( ", fieldName, ",'", value, "')");
	}

	private String _buildInExpressionFilterString(
		String fieldName, boolean includes, Object... values) {

		List<String> valuesList = new ArrayList<>();

		for (Object value : values) {
			valuesList.add(StringUtil.quote(String.valueOf(value)));
		}

		String filterString = StringBundler.concat(
			"(", fieldName, " in (",
			StringUtil.merge(valuesList, StringPool.COMMA_AND_SPACE), "))");

		if (includes) {
			return filterString;
		}

		return StringBundler.concat("(not ", filterString, ")");
	}

	private String _buildLambdaExpressionFilterString(
		String fieldName, boolean includes, int... values) {

		List<String> valuesList = new ArrayList<>();

		for (int value : values) {
			valuesList.add(
				StringBundler.concat(
					"(x ", includes ? "eq " : "ne ", value, ")"));
		}

		return StringBundler.concat(
			"(", fieldName, "/any(x:",
			StringUtil.merge(valuesList, includes ? " or " : " and "), "))");
	}

	private String _buildStartsWithExpressionFilterString(
		String fieldName, String value) {

		return StringBundler.concat(
			"startswith( ", fieldName, ",'", value, "')");
	}

	private ObjectDefinition _createObjectDefinition(
			List<ObjectField> objectFields)
		throws Exception {

		return _createObjectDefinition(
			objectFields, ObjectDefinitionConstants.SCOPE_COMPANY);
	}

	private ObjectDefinition _createObjectDefinition(
			List<ObjectField> objectFields, String scope)
		throws Exception {

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.addCustomObjectDefinition(
				adminUser.getUserId(), 0, null, false, false, true, true, false,
				false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, scope, ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), objectFields);

		return objectDefinitionLocalService.publishCustomObjectDefinition(
			adminUser.getUserId(), objectDefinition.getObjectDefinitionId());
	}

	private Tree _createObjectEntryTree(
			AccountEntry accountEntry, String externalReferenceCodeSuffix)
		throws Exception {

		Tree tree = TreeTestUtil.createObjectEntryTree(
			externalReferenceCodeSuffix, objectDefinitionLocalService,
			_objectEntryLocalService, objectFieldLocalService,
			_objectRelationshipLocalService,
			_rootObjectDefinition.getObjectDefinitionId());

		Node node = tree.getRootNode();

		_objectEntryLocalService.updateObjectEntry(
			adminUser.getUserId(), node.getPrimaryKey(),
			HashMapBuilder.<String, Serializable>put(
				() -> {
					ObjectField objectField =
						objectFieldLocalService.getObjectField(
							_rootObjectDefinition.
								getAccountEntryRestrictedObjectFieldId());

					return objectField.getName();
				},
				accountEntry.getAccountEntryId()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		return tree;
	}

	private ObjectFieldSetting _createObjectFieldSetting(
		String name, String value) {

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.createObjectFieldSetting(0L);

		objectFieldSetting.setName(name);
		objectFieldSetting.setValue(value);

		return objectFieldSetting;
	}

	private void _deleteAccountEntryOrganizationRel(
			AccountEntry accountEntry, Organization organization)
		throws Exception {

		_accountEntryOrganizationRelLocalService.
			deleteAccountEntryOrganizationRel(
				accountEntry.getAccountEntryId(),
				organization.getOrganizationId());
	}

	private ObjectDefinition _enableObjectEntryVersioning() {
		_objectDefinition1.setEnableObjectEntryVersioning(true);

		return objectDefinitionLocalService.updateObjectDefinition(
			_objectDefinition1);
	}

	private Long _getAttachmentObjectFieldValue() throws Exception {
		byte[] bytes = TestDataConstants.TEST_BYTE_ARRAY;

		InputStream inputStream = new ByteArrayInputStream(bytes);

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.addFileEntry(
			null, _group.getCreatorUserId(), _group.getGroupId(),
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt",
			MimeTypesUtil.getExtensionContentType("txt"),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK,
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT, null,
			null, inputStream, bytes.length, null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		return dlFileEntry.getFileEntryId();
	}

	private long _getFileEntryId(ObjectEntry objectEntry) {
		Map<String, Object> properties = objectEntry.getProperties();

		FileEntry fileEntry = (FileEntry)properties.get(
			"attachmentObjectFieldName");

		return fileEntry.getId();
	}

	private String _getListEntryKey(ObjectEntry objectEntry) {
		Map<String, Object> properties = objectEntry.getProperties();

		ListEntry listEntry = (ListEntry)properties.get(
			"picklistObjectFieldName");

		return listEntry.getKey();
	}

	private LocalDateTime _getLocalDateTime(String dateTimeString) {
		return LocalDateTime.parse(
			dateTimeString,
			DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
	}

	private Object _getLocalizedPropertyValue(
		String languageId, ObjectEntry objectEntry, String objectFieldName) {

		Map<String, Serializable> localizedValues =
			(Map<String, Serializable>)objectEntry.getPropertyValue(
				objectFieldName + "_i18n");

		return localizedValues.get(languageId);
	}

	private Page<ObjectEntry> _getPage(
			Map<String, String> aggregationTerms,
			ObjectDefinition objectDefinition)
		throws Exception {

		return _defaultObjectEntryManager.getObjectEntries(
			companyId, objectDefinition, null,
			new Aggregation() {
				{
					setAggregationTerms(aggregationTerms);
				}
			},
			new DefaultDTOConverterContext(
				false, Collections.emptyMap(), dtoConverterRegistry, null,
				LocaleUtil.getDefault(), null, _user),
			StringPool.BLANK, null, null, null);
	}

	private Timestamp _getTimestamp(String dateString) throws Exception {
		Date date = DateUtil.parseDate(
			"yyyy-MM-dd", dateString, LocaleUtil.getSiteDefault());

		return new Timestamp(date.getTime());
	}

	private void _removeResourcePermission(
			String actionId, ObjectDefinition objectDefinition, Role role)
		throws Exception {

		String name = objectDefinition.getClassName();

		if (Objects.equals(actionId, ObjectActionKeys.ADD_OBJECT_ENTRY)) {
			name = objectDefinition.getResourceName();
		}

		_resourcePermissionLocalService.removeResourcePermission(
			companyId, name, ResourceConstants.SCOPE_GROUP_TEMPLATE, "0",
			role.getRoleId(), actionId);
	}

	private void _removeResourcePermission(String actionId, Role role)
		throws Exception {

		_removeResourcePermission(actionId, _objectDefinition3, role);
	}

	private void _testAddObjectEntryAccountEntryRestriction(
			AccountEntry accountEntry)
		throws Exception {

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry.getAccountEntryId(), _user.getUserId());

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(),
				" must have ADD_OBJECT_ENTRY permission for ",
				_objectDefinition3.getResourceName(), StringPool.SPACE),
			() -> _addObjectEntry(accountEntry));

		_addResourcePermission(ObjectActionKeys.ADD_OBJECT_ENTRY, _buyerRole);

		_userGroupRoleLocalService.addUserGroupRole(
			_user.getUserId(), accountEntry.getAccountEntryGroupId(),
			_buyerRole.getRoleId());

		Assert.assertNotNull(_addObjectEntry(accountEntry));

		_userGroupRoleLocalService.deleteUserGroupRolesByUserId(
			_user.getUserId());

		_removeResourcePermission(
			ObjectActionKeys.ADD_OBJECT_ENTRY, _buyerRole);
	}

	private void _testAddObjectEntryWithDefaultLanguageId(
			ObjectDefinition objectDefinition)
		throws Exception {

		String scopeKey;

		if (StringUtil.equals(
				objectDefinition.getScope(),
				ObjectDefinitionConstants.SCOPE_COMPANY)) {

			scopeKey = ObjectDefinitionConstants.SCOPE_COMPANY;
		}
		else {
			Group group = GroupTestUtil.addGroup();

			scopeKey = String.valueOf(group.getGroupId());
		}

		String invalidLanguageId = RandomTestUtil.randomString();

		AssertUtils.assertFailure(
			ObjectEntryDefaultLanguageIdException.class,
			"Language ID " + invalidLanguageId + " is not available",
			() -> _defaultObjectEntryManager.addObjectEntry(
				_simpleDTOConverterContext, objectDefinition,
				new ObjectEntry() {
					{
						defaultLanguageId = invalidLanguageId;
						properties = Collections.emptyMap();
					}
				},
				scopeKey));

		ObjectFieldUtil.addCustomObjectField(
			new IntegerObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).localized(
				true
			).name(
				"requiredLocalizedLongInteger"
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).required(
				true
			).userId(
				TestPropsValues.getUserId()
			).build());

		AssertUtils.assertFailure(
			ObjectEntryValuesException.RequiredLanguageId.class,
			"No value was provided for the language ID \"pt_BR\" in the " +
				"required object field \"requiredLocalizedLongInteger\".",
			() -> _defaultObjectEntryManager.addObjectEntry(
				_simpleDTOConverterContext, objectDefinition,
				new ObjectEntry() {
					{
						defaultLanguageId = "pt_BR";
						properties = Collections.emptyMap();
					}
				},
				scopeKey));
		AssertUtils.assertFailure(
			ObjectEntryValuesException.RequiredLanguageId.class,
			"No value was provided for the language ID \"pt_BR\" in the " +
				"required object field \"requiredLocalizedLongInteger\".",
			() -> _defaultObjectEntryManager.addObjectEntry(
				_simpleDTOConverterContext, objectDefinition,
				new ObjectEntry() {
					{
						defaultLanguageId = "pt_BR";
						properties = HashMapBuilder.<String, Object>put(
							"requiredLocalizedLongInteger_i18n",
							HashMapBuilder.put(
								"pt_BR", StringPool.BLANK
							).build()
						).build();
					}
				},
				scopeKey));

		Locale defaultLocale = LocaleUtil.getDefault();

		try {
			if (StringUtil.equals(
					ObjectDefinitionConstants.SCOPE_COMPANY, scopeKey)) {

				LocaleUtil.setDefault(
					LocaleUtil.BRAZIL.getLanguage(),
					LocaleUtil.BRAZIL.getCountry(),
					LocaleUtil.BRAZIL.getVariant());
			}
			else {
				GroupTestUtil.updateDisplaySettings(
					GetterUtil.getLong(scopeKey),
					Arrays.asList(LocaleUtil.BRAZIL, LocaleUtil.US),
					LocaleUtil.BRAZIL);
			}

			// No value provided for the default language ID in the required
			// object field of a draft object entry

			objectDefinition.setEnableObjectEntryDraft(true);

			objectDefinitionLocalService.updateObjectDefinition(
				objectDefinition);

			assertEquals(
				_defaultObjectEntryManager.addObjectEntry(
					_simpleDTOConverterContext, objectDefinition,
					new ObjectEntry() {
						{
							properties = Collections.emptyMap();
							status = new Status() {
								{
									code = WorkflowConstants.STATUS_DRAFT;
								}
							};
						}
					},
					scopeKey),
				new ObjectEntry() {
					{
						properties = Collections.emptyMap();
					}
				});

			// No value provided for the default language ID in the required
			// object field of a published object entry

			AssertUtils.assertFailure(
				ObjectEntryValuesException.RequiredLanguageId.class,
				"No value was provided for the language ID \"pt_BR\" in the " +
					"required object field \"requiredLocalizedLongInteger\".",
				() -> _defaultObjectEntryManager.addObjectEntry(
					_simpleDTOConverterContext, objectDefinition,
					new ObjectEntry() {
						{
							properties = Collections.emptyMap();
						}
					},
					scopeKey));
			AssertUtils.assertFailure(
				ObjectEntryValuesException.RequiredLanguageId.class,
				"No value was provided for the language ID \"pt_BR\" in the " +
					"required object field \"requiredLocalizedLongInteger\".",
				() -> _defaultObjectEntryManager.addObjectEntry(
					_simpleDTOConverterContext, objectDefinition,
					new ObjectEntry() {
						{
							properties = HashMapBuilder.<String, Object>put(
								"requiredLocalizedLongInteger_i18n",
								HashMapBuilder.put(
									"pt_BR", StringPool.BLANK
								).build()
							).build();
						}
					},
					scopeKey));

			assertEquals(
				_defaultObjectEntryManager.addObjectEntry(
					_simpleDTOConverterContext, objectDefinition,
					new ObjectEntry() {
						{
							defaultLanguageId = "en_US";
							properties = HashMapBuilder.<String, Object>put(
								"requiredLocalizedLongInteger", 0
							).build();
						}
					},
					scopeKey),
				new ObjectEntry() {
					{
						defaultLanguageId = "en_US";
						properties = HashMapBuilder.<String, Object>put(
							"requiredLocalizedLongInteger_i18n",
							HashMapBuilder.<String, Object>put(
								"en_US", 0
							).build()
						).build();
					}
				});
			assertEquals(
				_defaultObjectEntryManager.addObjectEntry(
					_simpleDTOConverterContext, objectDefinition,
					new ObjectEntry() {
						{
							properties = HashMapBuilder.<String, Object>put(
								"requiredLocalizedLongInteger", 0
							).build();
						}
					},
					scopeKey),
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							"requiredLocalizedLongInteger_i18n",
							HashMapBuilder.<String, Object>put(
								"pt_BR", 0
							).build()
						).build();
					}
				});

			// Value provided for the default language ID in the required
			// object field of a published object entry

			int randomInt = RandomTestUtil.randomInt();

			assertEquals(
				_defaultObjectEntryManager.addObjectEntry(
					_simpleDTOConverterContext, objectDefinition,
					new ObjectEntry() {
						{
							properties = HashMapBuilder.<String, Object>put(
								"requiredLocalizedLongInteger_i18n",
								HashMapBuilder.put(
									"pt_BR", randomInt
								).build()
							).build();
						}
					},
					scopeKey),
				new ObjectEntry() {
					{
						properties = HashMapBuilder.<String, Object>put(
							"requiredLocalizedLongInteger_i18n",
							HashMapBuilder.<String, Object>put(
								"pt_BR", randomInt
							).build()
						).build();
					}
				});
		}
		finally {
			if (scopeKey.equals(ObjectDefinitionConstants.SCOPE_COMPANY)) {
				LocaleUtil.setDefault(
					defaultLocale.getLanguage(), defaultLocale.getCountry(),
					defaultLocale.getVariant());
			}
			else {
				_groupLocalService.deleteGroup(GetterUtil.getLong(scopeKey));
			}

			objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}
	}

	private void _testAddObjectEntryWithMissingParentObjectEntryReference(
			ObjectDefinition childObjectDefinition,
			Map<String, Object> childValues, String parentExternalReferenceCode,
			ObjectDefinition parentObjectDefinition,
			Map<String, Object> parentValues)
		throws Exception {

		AssertUtils.assertFailure(
			NoSuchObjectEntryException.class,
			String.format(
				"No ObjectEntry exists with the key {externalReference" +
					"Code=%s, groupId=0, companyId=%s}",
				parentExternalReferenceCode,
				parentObjectDefinition.getCompanyId()),
			() -> _defaultObjectEntryManager.getObjectEntry(
				TestPropsValues.getCompanyId(), _simpleDTOConverterContext,
				parentExternalReferenceCode, parentObjectDefinition,
				ObjectDefinitionConstants.SCOPE_COMPANY));

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			_addObjectEntry(childObjectDefinition, childValues);
		}

		ObjectEntry objectEntry = _defaultObjectEntryManager.getObjectEntry(
			TestPropsValues.getCompanyId(), _simpleDTOConverterContext,
			parentExternalReferenceCode, parentObjectDefinition,
			ObjectDefinitionConstants.SCOPE_COMPANY);

		AssertUtils.assertEquals(
			WorkflowConstants.STATUS_INCOMPLETE,
			objectEntry.getStatus(
			).getCode());

		objectEntry = _defaultObjectEntryManager.updateObjectEntry(
			TestPropsValues.getCompanyId(), _simpleDTOConverterContext,
			parentExternalReferenceCode, parentObjectDefinition,
			new ObjectEntry() {
				{
					properties = parentValues;
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		Status status = objectEntry.getStatus();

		AssertUtils.assertEquals(
			WorkflowConstants.STATUS_APPROVED, status.getCode());
	}

	private void _testDeleteObjectEntryWithAccountEntryRestricted2(
			String actionId, Tree tree)
		throws Exception {

		Node rootNode = tree.getRootNode();

		TreeTestUtil.forEachNodeObjectEntry(
			tree.iterator(TreeConstants.ITERATOR_TYPE_POST_ORDER),
			_objectEntryLocalService,
			objectEntry -> {
				ObjectDefinition objectDefinition =
					objectDefinitionLocalService.fetchObjectDefinition(
						objectEntry.getObjectDefinitionId());

				AssertUtils.assertFailure(
					PrincipalException.MustHavePermission.class,
					StringBundler.concat(
						"User ", _user.getUserId(), " must have ", actionId,
						" permission for ",
						_rootObjectDefinition.getClassName(), StringPool.SPACE,
						rootNode.getPrimaryKey()),
					() -> _defaultObjectEntryManager.deleteObjectEntry(
						objectDefinition, objectEntry.getObjectEntryId()));
			});
	}

	private void _testUpdateObjectEntryWithAccountEntryRestricted2(
			String actionId, Tree tree)
		throws Exception {

		Node rootNode = tree.getRootNode();

		TreeTestUtil.forEachNodeObjectEntry(
			tree.iterator(), _objectEntryLocalService,
			objectEntry -> {
				ObjectDefinition objectDefinition =
					objectDefinitionLocalService.fetchObjectDefinition(
						objectEntry.getObjectDefinitionId());

				AssertUtils.assertFailure(
					PrincipalException.MustHavePermission.class,
					StringBundler.concat(
						"User ", _user.getUserId(), " must have ", actionId,
						" permission for ",
						_rootObjectDefinition.getClassName(), StringPool.SPACE,
						rootNode.getPrimaryKey()),
					() -> _defaultObjectEntryManager.updateObjectEntry(
						_simpleDTOConverterContext, objectDefinition,
						objectEntry.getObjectEntryId(),
						_defaultObjectEntryManager.getObjectEntry(
							_simpleDTOConverterContext, objectDefinition,
							objectEntry.getObjectEntryId())));
			});
	}

	private void _updateAndAssertObjectEntryWithPicklistObjectField(
			String actualPicklistObjectFieldValue,
			String expectedPicklistObjectFieldValue,
			ObjectDefinition objectDefinition, long objectEntryId)
		throws Exception {

		ObjectEntry objectEntry = _defaultObjectEntryManager.updateObjectEntry(
			_simpleDTOConverterContext, objectDefinition, objectEntryId,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"picklistObjectFieldName",
						actualPicklistObjectFieldValue
					).build();
				}
			});

		_assertObjectEntryWithPicklistObjectField(
			expectedPicklistObjectFieldValue, objectEntry);
	}

	private void _updateLocalizedObjectEntryValues(
			ObjectDefinition objectDefinition, long objectEntryId,
			Map<String, Object> values)
		throws Exception {

		_defaultObjectEntryManager.updateObjectEntry(
			_simpleDTOConverterContext, objectDefinition, objectEntryId,
			new ObjectEntry() {
				{
					properties = new HashMap<>(values);
				}
			});
	}

	private ObjectEntry _updateObjectEntryVersion(
			ObjectEntry objectEntry, int versionNumber)
		throws Exception {

		return _defaultObjectEntryManager.updateObjectEntry(
			TestPropsValues.getCompanyId(), dtoConverterContext,
			objectEntry.getExternalReferenceCode(), _objectDefinition1,
			new ObjectEntry() {
				{
					externalReferenceCode = RandomTestUtil.randomString();
					keywords = new String[] {RandomTestUtil.randomString()};
					properties = HashMapBuilder.<String, Object>put(
						"textObjectFieldName", RandomTestUtil.randomString()
					).build();
					systemProperties = new SystemProperties() {
						{
							version = new Version() {
								{
									number = versionNumber;
								}
							};
						}
					};
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);
	}

	private static DefaultObjectEntryManager _defaultObjectEntryManager;
	private static Group _group;

	@Inject(
		filter = "object.entry.manager.storage.type=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT
	)
	private static ObjectEntryManager _objectEntryManager;

	private static String _originalName;
	private static PermissionChecker _originalPermissionChecker;
	private static DateFormat _simpleDateFormat;
	private static DTOConverterContext _simpleDTOConverterContext;

	private Role _accountAdministratorRole;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	private Role _accountManagerRole;

	@Inject
	private AccountRoleLocalService _accountRoleLocalService;

	private Role _buyerRole;

	@Inject
	private DLAppService _dlAppLocalService;

	@Inject
	private DLAppService _dlAppService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private DLFileEntryService _dlFileEntryService;

	@Inject
	private DLFolderService _dlFolderService;

	@Inject
	private DLURLHelper _dlURLHelper;

	@Inject
	private GroupLocalService _groupLocalService;

	private String _listTypeEntryKey1;
	private String _listTypeEntryKey2;
	private String _listTypeEntryKey3;
	private String _listTypeEntryKey4;

	@Inject
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	private String _localizedMultiselectPicklistObjectFieldName;
	private Map<String, Object> _localizedObjectFieldI18nValues;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition1;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition2;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition3;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFieldService _objectFieldService;

	@Inject
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Inject
	private ObjectFilterLocalService _objectFilterLocalService;

	private String _objectRelationshipERCObjectFieldName;
	private String _objectRelationshipFieldName;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	private String _objectRelationshipName;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	private NestedFieldsContext _originalNestedFieldsContext;

	@Inject
	private ResourceActions _resourceActions;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private ObjectDefinition _rootObjectDefinition;

	@Inject
	private Searcher _searcher;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	private Tree _tree;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}