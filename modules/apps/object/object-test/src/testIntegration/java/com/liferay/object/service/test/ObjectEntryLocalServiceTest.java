/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.test.util.ExpandoTestUtil;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.list.type.entry.util.ListTypeEntryUtil;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.constants.ObjectValidationRuleConstants;
import com.liferay.object.constants.ObjectValidationRuleSettingConstants;
import com.liferay.object.entry.util.ObjectEntryThreadLocal;
import com.liferay.object.exception.DuplicateObjectEntryExternalReferenceCodeException;
import com.liferay.object.exception.NoSuchObjectEntryException;
import com.liferay.object.exception.ObjectDefinitionScopeException;
import com.liferay.object.exception.ObjectEntryStatusException;
import com.liferay.object.exception.ObjectEntryValuesException;
import com.liferay.object.exception.ObjectValidationRuleEngineException;
import com.liferay.object.field.builder.AttachmentObjectFieldBuilder;
import com.liferay.object.field.builder.AutoIncrementObjectFieldBuilder;
import com.liferay.object.field.builder.DateObjectFieldBuilder;
import com.liferay.object.field.builder.DateTimeObjectFieldBuilder;
import com.liferay.object.field.builder.DecimalObjectFieldBuilder;
import com.liferay.object.field.builder.EncryptedObjectFieldBuilder;
import com.liferay.object.field.builder.FormulaObjectFieldBuilder;
import com.liferay.object.field.builder.IntegerObjectFieldBuilder;
import com.liferay.object.field.builder.LongIntegerObjectFieldBuilder;
import com.liferay.object.field.builder.LongTextObjectFieldBuilder;
import com.liferay.object.field.builder.PicklistObjectFieldBuilder;
import com.liferay.object.field.builder.PrecisionDecimalObjectFieldBuilder;
import com.liferay.object.field.builder.RichTextObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.model.ObjectState;
import com.liferay.object.model.ObjectStateFlow;
import com.liferay.object.model.ObjectStateTransition;
import com.liferay.object.model.ObjectValidationRule;
import com.liferay.object.model.ObjectValidationRuleSetting;
import com.liferay.object.scope.CompanyScoped;
import com.liferay.object.scope.ObjectDefinitionScoped;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.ObjectStateFlowLocalService;
import com.liferay.object.service.ObjectStateLocalService;
import com.liferay.object.service.ObjectStateTransitionLocalService;
import com.liferay.object.service.ObjectValidationRuleLocalService;
import com.liferay.object.service.test.util.ObjectFieldTestUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.object.test.util.TreeTestUtil;
import com.liferay.object.tree.Node;
import com.liferay.object.tree.ObjectEntryTreeFactory;
import com.liferay.object.tree.Tree;
import com.liferay.object.validation.rule.ObjectValidationRuleEngine;
import com.liferay.object.validation.rule.ObjectValidationRuleResult;
import com.liferay.object.validation.rule.setting.builder.ObjectValidationRuleSettingBuilder;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.encryptor.Encryptor;
import com.liferay.portal.kernel.encryptor.EncryptorException;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.NoSuchResourceActionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.randomizerbumpers.NumericStringRandomizerBumper;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaDetector;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.security.script.management.test.rule.ScriptManagementConfigurationTestRule;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalDateTimeUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Closeable;
import java.io.Serializable;

import java.math.BigDecimal;

import java.security.NoSuchAlgorithmException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import javax.crypto.spec.SecretKeySpec;

import org.hamcrest.CoreMatchers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
@FeatureFlags({"LPD-32050", "LPD-34594"})
@RunWith(Arquillian.class)
public class ObjectEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			ScriptManagementConfigurationTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_draftObjectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				false,
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())));
		_irrelevantObjectDefinition = _publishCustomObjectDefinition(
			false,
			Arrays.asList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING,
					RandomTestUtil.randomString(), StringUtil.randomId())));

		_listTypeDefinition =
			_listTypeDefinitionLocalService.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				false,
				ListUtil.concat(
					_createListTypeEntries(
						"listTypeEntryKey", "List Type Entry Key ", 4),
					_createListTypeEntries(
						"multipleListTypeEntryKey",
						"Multiple List Type Entry Key ", 6)));

		_objectDefinition = _publishCustomObjectDefinition(
			true,
			Arrays.asList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER,
					ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
					"Age of Death", "ageOfDeath", false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN,
					ObjectFieldConstants.DB_TYPE_BOOLEAN, true, false, null,
					"Author of Gospel", "authorOfGospel", false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_DATE,
					ObjectFieldConstants.DB_TYPE_DATE, true, false, null,
					"Birthday", "birthday", false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_PRECISION_DECIMAL,
					ObjectFieldConstants.DB_TYPE_DOUBLE, true, false, null,
					"BloodPressure", "bloodPressure", false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
					"Email Address", "emailAddress",
					Arrays.asList(
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_UNIQUE_VALUES
						).value(
							Boolean.TRUE.toString()
						).build()),
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
					"Email Address Required", "emailAddressRequired", true),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
					"Email Address Domain", "emailAddressDomain", false),
				ObjectFieldUtil.createObjectField(
					0, ObjectFieldConstants.BUSINESS_TYPE_TEXT, null,
					ObjectFieldConstants.DB_TYPE_STRING, true, false, null,
					"First Name", "firstName", false, true),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_DECIMAL,
					ObjectFieldConstants.DB_TYPE_DOUBLE, true, false, null,
					"Height", "height", false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, false, null,
					"Last Name", "lastName", false),
				ObjectFieldUtil.createObjectField(
					_listTypeDefinition.getListTypeDefinitionId(),
					ObjectFieldConstants.BUSINESS_TYPE_PICKLIST, null,
					ObjectFieldConstants.DB_TYPE_STRING, true, false, null,
					"List Type Entry Key", "listTypeEntryKey", false, false),
				ObjectFieldUtil.createObjectField(
					_listTypeDefinition.getListTypeDefinitionId(),
					ObjectFieldConstants.BUSINESS_TYPE_PICKLIST, null,
					ObjectFieldConstants.DB_TYPE_STRING, true, false, null,
					"List Type Entry Key Required", "listTypeEntryKeyRequired",
					true, false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, false, null,
					"Middle Name", "middleName", false),
				ObjectFieldUtil.createObjectField(
					_listTypeDefinition.getListTypeDefinitionId(),
					ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST,
					null, ObjectFieldConstants.DB_TYPE_STRING, true, false,
					null, "Multiple List Type Entries Key",
					"multipleListTypeEntriesKey", false, false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_INTEGER,
					ObjectFieldConstants.DB_TYPE_INTEGER, true, false, null,
					"Number of Books Written", "numberOfBooksWritten", false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_LONG_TEXT,
					ObjectFieldConstants.DB_TYPE_CLOB, false, false, null,
					"Script", "script", false)));

		_addCustomObjectField(
			new DateObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap("Date")
			).name(
				"date"
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).build());
		_addCustomObjectField(
			new PrecisionDecimalObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap("Speed")
			).name(
				"speed"
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).build());
		_addCustomObjectField(
			new PicklistObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap("State")
			).listTypeDefinitionId(
				_listTypeDefinition.getListTypeDefinitionId()
			).name(
				"state"
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).objectFieldSettings(
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_DEFAULT_VALUE
					).value(
						"listTypeEntryKey1"
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
		_addCustomObjectField(
			new DateTimeObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap("Time")
			).name(
				"time"
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).objectFieldSettings(
				Collections.singletonList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_TIME_STORAGE
					).value(
						ObjectFieldSettingConstants.VALUE_USE_INPUT_AS_ENTERED
					).build())
			).build());
		_addCustomObjectField(
			new AttachmentObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap("Upload")
			).name(
				"upload"
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
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
		_addCustomObjectField(
			new DecimalObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap("Weight")
			).name(
				"weight"
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).build());

		_objectRelationship =
			_objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_irrelevantObjectDefinition.getObjectDefinitionId(),
				_objectDefinition.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);
	}

	@After
	public void tearDown() throws Exception {

		// Do not rely on @DeleteAfterTestRun because object entries that
		// reference a required list type entry cannot be deleted before it is
		// unreferenced

		_objectDefinitionLocalService.deleteObjectDefinition(_objectDefinition);
	}

	@FeatureFlags("LPD-43542")
	@Test
	public void testAddAndUpdateObjectEntryWithObjectValidationRule()
		throws Exception {

		ObjectDefinition objectDefinition1 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"name"
					).build()));
		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		ObjectRelationship objectRelationship1 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, objectDefinition1,
				objectDefinition2);

		ObjectField relationshipObjectField1 =
			_objectFieldLocalService.fetchObjectField(
				objectRelationship1.getObjectFieldId2());

		ObjectValidationRule objectValidationRule = _addObjectValidationRule(
			objectDefinition2,
			String.format(
				"not(isEmpty(%s_name))", relationshipObjectField1.getName()));

		ObjectRelationship objectRelationship2 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService,
				_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
					TestPropsValues.getCompanyId(), User.class.getName()),
				objectDefinition2);

		ObjectField relationshipObjectField2 =
			_objectFieldLocalService.fetchObjectField(
				objectRelationship2.getObjectFieldId2());

		try {
			_addObjectEntry(
				0, objectDefinition2.getObjectDefinitionId(),
				HashMapBuilder.<String, Serializable>put(
					relationshipObjectField2.getName(),
					TestPropsValues.getUserId()
				).build());

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			_assertFailureObjectValidationRule(
				modelListenerException, objectValidationRule);
		}

		objectValidationRule = _updateObjectValidationRule(
			objectValidationRule,
			String.format(
				"isEmpty(%s_name)", relationshipObjectField1.getName()));

		ObjectEntry objectEntry = _addObjectEntry(
			0, objectDefinition2.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				relationshipObjectField2.getName(),
				() -> {
					User user = UserTestUtil.addUser(
						RandomTestUtil.randomString(), LocaleUtil.getDefault(),
						"Paul", RandomTestUtil.randomString(),
						new long[] {TestPropsValues.getGroupId()});

					return user.getUserId();
				}
			).build());

		_updateObjectValidationRule(
			objectValidationRule,
			String.format(
				"%s_name == oldValue(\"%s_givenName\")",
				relationshipObjectField1.getName(),
				relationshipObjectField2.getName()));

		_clearValidatedObjectEntryIds();

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			HashMapBuilder.<String, Serializable>put(
				relationshipObjectField1.getName(),
				() -> {
					ObjectEntry relatedObjectEntry = _addObjectEntry(
						0, objectDefinition1.getObjectDefinitionId(),
						HashMapBuilder.<String, Serializable>put(
							"name", "Paul"
						).build());

					return relatedObjectEntry.getObjectEntryId();
				}
			).put(
				relationshipObjectField2.getName(),
				() -> {
					User user2 = UserTestUtil.addUser(
						RandomTestUtil.randomString(), LocaleUtil.getDefault(),
						"Peter", RandomTestUtil.randomString(),
						new long[] {TestPropsValues.getGroupId()});

					return user2.getUserId();
				}
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_clearValidatedObjectEntryIds();

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			HashMapBuilder.<String, Serializable>put(
				relationshipObjectField1.getName(),
				() -> {
					ObjectEntry relatedObjectEntry = _addObjectEntry(
						0, objectDefinition1.getObjectDefinitionId(),
						HashMapBuilder.<String, Serializable>put(
							"name", "Peter"
						).build());

					return relatedObjectEntry.getObjectEntryId();
				}
			).put(
				relationshipObjectField2.getName(), 0
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testAddMultipleObjectEntriesWithTheSameObjectValidationRule()
		throws Exception {

		ObjectValidationRule objectValidationRule = _addObjectValidationRule(
			ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
			LocalizedMapUtil.getLocalizedMap("Field must be an email address"),
			"isEmailAddress(emailAddressRequired)");

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "bob@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build());

		_assertCount(1);

		try {
			_addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"emailAddressRequired", RandomTestUtil.randomString()
				).put(
					"listTypeEntryKeyRequired", "listTypeEntryKey1"
				).build());

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			_assertFailureObjectValidationRule(
				modelListenerException, objectValidationRule);
		}
	}

	@Test
	public void testAddObjectEntry() throws Exception {
		_assertCount(0);

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "peter@liferay.com"
			).put(
				"firstName", "Peter"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build());

		_assertCount(1);

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddress", "james@liferay.com"
			).put(
				"emailAddressRequired", "james@liferay.com"
			).put(
				"firstName", "James"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build());

		_assertCount(2);

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "john@liferay.com"
			).put(
				"firstName", "John"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build());

		_assertCount(3);

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "john@liferay.com"
			).put(
				"listTypeEntryKey", "listTypeEntryKey1"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build());

		_assertCount(4);

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "john@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).put(
				"multipleListTypeEntriesKey",
				"multipleListTypeEntryKey1, multipleListTypeEntryKey2"
			).build());

		_assertCount(5);

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"bloodPressure", "12,8"
			).put(
				"emailAddressRequired", "diogo@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build());

		_assertCount(6);

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"ageOfDeath", "0140"
			).put(
				"emailAddressRequired", "job@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build());

		_assertCount(7);

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "job@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).put(
				"numberOfBooksWritten", "01"
			).build());

		_assertCount(8);

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsIntegerSize.class,
			"Object entry value exceeds integer field allowed size",
			() -> _addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"emailAddressRequired", "matthew@liferay.com"
				).put(
					"listTypeEntryKeyRequired", "listTypeEntryKey1"
				).put(
					"numberOfBooksWritten", "2147483648"
				).build()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsIntegerSize.class,
			"Object entry value exceeds integer field allowed size",
			() -> _addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"emailAddressRequired", "matthew@liferay.com"
				).put(
					"listTypeEntryKeyRequired", "listTypeEntryKey1"
				).put(
					"numberOfBooksWritten", "-2147483649"
				).build()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsLongMaxSize.class,
			"Object entry value exceeds maximum long field allowed size",
			() -> _addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"ageOfDeath", "9007199254740992"
				).put(
					"emailAddressRequired", "matthew@liferay.com"
				).put(
					"listTypeEntryKeyRequired", "listTypeEntryKey1"
				).build()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsLongMinSize.class,
			"Object entry value falls below minimum long field allowed size",
			() -> _addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"ageOfDeath", "-9007199254740992"
				).put(
					"emailAddressRequired", "matthew@liferay.com"
				).put(
					"listTypeEntryKeyRequired", "listTypeEntryKey1"
				).build()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsLongSize.class,
			"Object entry value exceeds long field allowed size",
			() -> _addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"ageOfDeath", "9223372036854775808"
				).put(
					"emailAddressRequired", "matthew@liferay.com"
				).put(
					"listTypeEntryKeyRequired", "listTypeEntryKey1"
				).build()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsLongSize.class,
			"Object entry value exceeds long field allowed size",
			() -> _addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"ageOfDeath", "-9223372036854775809"
				).put(
					"emailAddressRequired", "matthew@liferay.com"
				).put(
					"listTypeEntryKeyRequired", "listTypeEntryKey1"
				).build()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsTextMaxLength.class,
			"Object entry value exceeds the maximum length of 280 characters " +
				"for object field \"firstName\"",
			() -> _addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"emailAddressRequired", "matthew@liferay.com"
				).put(
					"firstName", RandomTestUtil.randomString(281)
				).put(
					"listTypeEntryKeyRequired", "listTypeEntryKey1"
				).build()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsTextMaxLength.class,
			"Object entry value exceeds the maximum length of 65000 " +
				"characters for object field \"script\"",
			() -> _addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"emailAddressRequired", "matthew@liferay.com"
				).put(
					"listTypeEntryKeyRequired", "listTypeEntryKey1"
				).put(
					"script", RandomTestUtil.randomString(65001)
				).build()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.InvalidFileExtension.class,
			"The file extension \"txt\" is invalid for object field \"upload\"",
			() -> {
				ObjectField objectField =
					_objectFieldLocalService.fetchObjectField(
						_objectDefinition.getObjectDefinitionId(), "upload");

				ObjectFieldSetting objectFieldSetting =
					_objectFieldSettingLocalService.fetchObjectFieldSetting(
						objectField.getObjectFieldId(),
						"acceptedFileExtensions");

				_objectFieldSettingLocalService.updateObjectFieldSetting(
					objectFieldSetting.getObjectFieldSettingId(), "jpg, png");

				_addObjectEntry(
					HashMapBuilder.<String, Serializable>put(
						"emailAddressRequired", "peter@liferay.com"
					).put(
						"listTypeEntryKeyRequired", "listTypeEntryKey1"
					).put(
						"upload",
						_addTempFileEntry(
							StringUtil.randomString()
						).getFileEntryId()
					).build());
			});

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ListTypeEntry.class,
			"Object field name \"listTypeEntryKeyRequired\" is not mapped to " +
				"a valid list type entry",
			() -> _addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"emailAddressRequired", "john@liferay.com"
				).put(
					"listTypeEntryKeyRequired", RandomTestUtil.randomString()
				).build()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ListTypeEntry.class,
			"Object field name \"multipleListTypeEntriesKey\" is not mapped " +
				"to a valid list type entry",
			() -> _addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"emailAddressRequired", "john@liferay.com"
				).put(
					"multipleListTypeEntriesKey",
					(Serializable)Arrays.asList(
						"multipleListTypeEntryKey1",
						RandomTestUtil.randomString())
				).build()));

		_objectFieldLocalService.updateRequired(
			_objectRelationship.getObjectFieldId2(), true);

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			_objectRelationship.getObjectFieldId2());

		AssertUtils.assertFailure(
			ObjectEntryValuesException.Required.class,
			StringBundler.concat(
				"No value was provided for required object field ", '"',
				objectField.getName(), '"'),
			() -> _addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"emailAddressRequired", "john@liferay.com"
				).put(
					"firstName", "Judas"
				).put(
					"listTypeEntryKeyRequired", "listTypeEntryKey1"
				).build()));

		_objectFieldLocalService.updateRequired(
			_objectRelationship.getObjectFieldId2(), false);

		AssertUtils.assertFailure(
			ObjectEntryValuesException.Required.class,
			"No value was provided for required object field " +
				"\"emailAddressRequired\"",
			() -> _addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"firstName", "Judas"
				).put(
					"listTypeEntryKeyRequired", "listTypeEntryKey1"
				).build()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.Required.class,
			"No value was provided for required object field " +
				"\"listTypeEntryKeyRequired\"",
			() -> _addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"emailAddressRequired", "john@liferay.com"
				).put(
					"firstName", "Judas"
				).build()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.UniqueValueConstraintViolation.class,
			"Unique value constraint violation for " +
				_objectDefinition.getDBTableName() +
					".emailAddress_ with value james@liferay.com",
			() -> _addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"emailAddress", "james@liferay.com"
				).put(
					"emailAddressRequired", "james@liferay.com"
				).put(
					"listTypeEntryKeyRequired", "listTypeEntryKey1"
				).build()));

		ObjectDefinition objectDefinition = _publishCustomObjectDefinition(
			true,
			Arrays.asList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
					RandomTestUtil.randomString(), "name",
					Arrays.asList(
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_UNIQUE_VALUES
						).value(
							Boolean.TRUE.toString()
						).build()),
					false)));

		objectDefinition.setScope(ObjectDefinitionConstants.SCOPE_SITE);

		objectDefinition = _objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);

		_addObjectEntry(
			TestPropsValues.getGroupId(),
			objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"name", "Peter"
			).build());

		long finalObjectDefinitionId = objectDefinition.getObjectDefinitionId();

		AssertUtils.assertFailure(
			ObjectEntryValuesException.UniqueValueConstraintViolation.class,
			"Unique value constraint violation for " +
				objectDefinition.getDBTableName() + ".name_ with value Peter",
			() -> _addObjectEntry(
				TestPropsValues.getGroupId(), finalObjectDefinitionId,
				HashMapBuilder.<String, Serializable>put(
					"name", "Peter"
				).build()));

		Group group = GroupTestUtil.addGroup();

		_addObjectEntry(
			group.getGroupId(), objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"name", "Peter"
			).build());

		AssertUtils.assertFailure(
			ObjectEntryValuesException.UniqueValueConstraintViolation.class,
			"Unique value constraint violation for " +
				objectDefinition.getDBTableName() + ".name_ with value Peter",
			() -> _addObjectEntry(
				group.getGroupId(), finalObjectDefinitionId,
				HashMapBuilder.<String, Serializable>put(
					"name", "Peter"
				).build()));

		_testAddObjectEntryWithLocalizedValues(objectDefinition, group);

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		ObjectDefinition modifiableSystemObjectDefinition =
			ObjectDefinitionTestUtil.addModifiableSystemObjectDefinition(
				TestPropsValues.getUserId(), null, true,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test", null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionConstants.SCOPE_SITE, null, 1,
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())));

		_objectDefinitionLocalService.publishSystemObjectDefinition(
			TestPropsValues.getUserId(),
			modifiableSystemObjectDefinition.getObjectDefinitionId());

		_testAddObjectEntryWithLocalizedValues(
			modifiableSystemObjectDefinition, group);

		_objectDefinitionLocalService.deleteObjectDefinition(
			modifiableSystemObjectDefinition.getObjectDefinitionId());
	}

	@FeatureFlags("LPD-32050")
	@Test
	public void testAddObjectEntryAfterDeletingLocalizedObjectField()
		throws Exception {

		ObjectField objectField = new TextObjectFieldBuilder(
		).labelMap(
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
		).name(
			"name"
		).build();

		Assert.assertFalse(objectField.isLocalized());

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(objectField));

		ObjectField localizedObjectField1 =
			ObjectFieldUtil.addCustomObjectField(
				new IntegerObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).localized(
					true
				).name(
					"a" + RandomTestUtil.randomString()
				).objectDefinitionId(
					objectDefinition.getObjectDefinitionId()
				).userId(
					TestPropsValues.getUserId()
				).build());

		Map<String, Serializable> localizedValues = Collections.singletonMap(
			localizedObjectField1.getI18nObjectFieldName(),
			HashMapBuilder.put(
				"en_US", RandomTestUtil.randomInt()
			).put(
				"pt_BR", RandomTestUtil.randomInt()
			).build());

		_assertObjectEntryLocalizedValues(
			localizedValues,
			_addObjectEntry(
				0, objectDefinition.getObjectDefinitionId(), localizedValues),
			localizedObjectField1);

		_objectFieldLocalService.deleteObjectField(localizedObjectField1);

		ObjectField localizedObjectField2 =
			ObjectFieldUtil.addCustomObjectField(
				new IntegerObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).localized(
					true
				).name(
					"a" + RandomTestUtil.randomString()
				).objectDefinitionId(
					objectDefinition.getObjectDefinitionId()
				).userId(
					TestPropsValues.getUserId()
				).build());

		localizedValues = Collections.singletonMap(
			localizedObjectField2.getI18nObjectFieldName(),
			HashMapBuilder.put(
				"en_US", RandomTestUtil.randomInt()
			).put(
				"pt_BR", RandomTestUtil.randomInt()
			).build());

		_assertObjectEntryLocalizedValues(
			localizedValues,
			_addObjectEntry(
				0, objectDefinition.getObjectDefinitionId(), localizedValues),
			localizedObjectField2);

		ObjectField localizedObjectField3 =
			ObjectFieldUtil.addCustomObjectField(
				new IntegerObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).localized(
					true
				).name(
					"a" + RandomTestUtil.randomString()
				).objectDefinitionId(
					objectDefinition.getObjectDefinitionId()
				).userId(
					TestPropsValues.getUserId()
				).build());

		localizedValues = HashMapBuilder.<String, Serializable>put(
			localizedObjectField2.getI18nObjectFieldName(),
			HashMapBuilder.put(
				"en_US", RandomTestUtil.randomInt()
			).put(
				"pt_BR", RandomTestUtil.randomInt()
			).build()
		).put(
			localizedObjectField3.getI18nObjectFieldName(),
			HashMapBuilder.put(
				"en_US", RandomTestUtil.randomInt()
			).put(
				"pt_BR", RandomTestUtil.randomInt()
			).build()
		).build();

		ObjectEntry objectEntry = _addObjectEntry(
			0, objectDefinition.getObjectDefinitionId(), localizedValues);

		_assertObjectEntryLocalizedValues(
			localizedValues, objectEntry, localizedObjectField2);
		_assertObjectEntryLocalizedValues(
			localizedValues, objectEntry, localizedObjectField3);

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Test
	public void testAddObjectEntryAsDraft() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		int originalWorkflowAction = serviceContext.getWorkflowAction();

		try {
			_testAddObjectEntryAsDraft();
		}
		finally {
			serviceContext.setWorkflowAction(originalWorkflowAction);
		}
	}

	@Test
	public void testAddObjectEntryWithAttachmentObjectField() throws Exception {
		FileEntry tempFileEntry1 = _addTempFileEntry(StringUtil.randomString());

		ObjectEntry objectEntry = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "james@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).put(
				"upload", tempFileEntry1.getFileEntryId()
			).build());

		long tempFileEntryId = tempFileEntry1.getFileEntryId();

		AssertUtils.assertFailure(
			NoSuchFileEntryException.class,
			StringBundler.concat(
				"No FileEntry exists with the key {fileEntryId=",
				tempFileEntryId, "}"),
			() -> _dlAppLocalService.getFileEntry(tempFileEntryId));

		long persistedFileEntryId1 = MapUtil.getLong(
			objectEntry.getValues(), "upload");

		Assert.assertNotEquals(tempFileEntryId, persistedFileEntryId1);

		DLFileEntry persistedDLFileEntry =
			_dlFileEntryLocalService.getFileEntry(persistedFileEntryId1);

		Assert.assertEquals(
			_objectDefinition.getClassName(),
			persistedDLFileEntry.getClassName());
		Assert.assertEquals(
			objectEntry.getObjectEntryId(), persistedDLFileEntry.getClassPK());

		// LPS-180587 Partial updates should not delete existing files

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			HashMapBuilder.<String, Serializable>put(
				"state", "listTypeEntryKey3"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertNotNull(
			_dlAppLocalService.getFileEntry(persistedFileEntryId1));

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			HashMapBuilder.<String, Serializable>put(
				"upload", 0L
			).build(),
			ServiceContextTestUtil.getServiceContext());

		AssertUtils.assertFailure(
			NoSuchFileEntryException.class,
			StringBundler.concat(
				"No FileEntry exists with the key {fileEntryId=",
				persistedFileEntryId1, "}"),
			() -> _dlAppLocalService.getFileEntry(persistedFileEntryId1));

		// Delete object entry should delete existing files

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			HashMapBuilder.<String, Serializable>put(
				"upload",
				() -> {
					FileEntry tempFileEntry2 = _addTempFileEntry(
						StringUtil.randomString());

					return tempFileEntry2.getFileEntryId();
				}
			).build(),
			ServiceContextTestUtil.getServiceContext());

		long persistedFileEntryId2 = MapUtil.getLong(
			objectEntry.getValues(), "upload");

		Assert.assertNotNull(
			_dlAppLocalService.getFileEntry(persistedFileEntryId2));

		_objectEntryLocalService.deleteObjectEntry(objectEntry);

		AssertUtils.assertFailure(
			NoSuchFileEntryException.class,
			StringBundler.concat(
				"No FileEntry exists with the key {fileEntryId=",
				persistedFileEntryId2, "}"),
			() -> _dlAppLocalService.getFileEntry(persistedFileEntryId2));
	}

	@Test
	public void testAddObjectEntryWithAutoIncrementObjectField()
		throws Exception {

		ObjectField objectField = _addCustomObjectField(
			new AutoIncrementObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"autoIncrement"
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).objectFieldSettings(
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_INITIAL_VALUE
					).value(
						"0123"
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_PREFIX
					).value(
						"LPS-"
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_SUFFIX
					).value(
						"-private"
					).build())
			).build());

		Map<String, Serializable> requiredValues =
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "athanasius@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build();

		AssertUtils.assertFailure(
			ObjectEntryValuesException.InvalidValue.class,
			"The value is invalid for object field \"autoIncrement\"",
			() -> _addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"autoIncrement",
					RandomTestUtil.randomString() + "0123-private"
				).putAll(
					requiredValues
				).build()));
		AssertUtils.assertFailure(
			ObjectEntryValuesException.InvalidValue.class,
			"The value is invalid for object field \"autoIncrement\"",
			() -> _addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"autoIncrement",
					"LPS-" + RandomTestUtil.randomString() + "-private"
				).putAll(
					requiredValues
				).build()));
		AssertUtils.assertFailure(
			ObjectEntryValuesException.InvalidValue.class,
			"The value is invalid for object field \"autoIncrement\"",
			() -> _addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"autoIncrement", "LPS-0123" + RandomTestUtil.randomString()
				).putAll(
					requiredValues
				).build()));
		AssertUtils.assertFailure(
			ObjectEntryValuesException.InvalidValue.class,
			"The value is invalid for object field \"autoIncrement\"",
			() -> _addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"autoIncrement", "LPS-xxxx-private"
				).putAll(
					requiredValues
				).build()));
		AssertUtils.assertFailure(
			ObjectEntryValuesException.InvalidValue.class,
			"The value is invalid for object field \"autoIncrement\"",
			() -> _addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"autoIncrement", "LPS-3-private"
				).putAll(
					requiredValues
				).build()));
		AssertUtils.assertFailure(
			ObjectEntryValuesException.InvalidValue.class,
			"The value is invalid for object field \"autoIncrement\"",
			() -> _addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"autoIncrement", "LPS-123-private"
				).putAll(
					requiredValues
				).build()));
		AssertUtils.assertFailure(
			ObjectEntryValuesException.InvalidValue.class,
			"The value is invalid for object field \"autoIncrement\"",
			() -> _addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"autoIncrement", "LPS-00123-private"
				).putAll(
					requiredValues
				).build()));

		ObjectEntry objectEntry1 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"autoIncrement", "LPS-0200-private"
			).putAll(
				requiredValues
			).build());

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select ", objectField.getSortableDBColumnName(), " from ",
					objectField.getDBTableName(), " where ",
					_objectDefinition.getPKObjectFieldDBColumnName(), " = ",
					objectEntry1.getObjectEntryId()));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			resultSet.next();

			Assert.assertEquals(200, resultSet.getLong(1));
		}

		// Auto increment object field value must always be unique

		AssertUtils.assertFailure(
			ObjectEntryValuesException.UniqueValueConstraintViolation.class,
			String.format(
				"Unique value constraint violation for %s.%s with value %s",
				objectField.getDBTableName(), objectField.getDBColumnName(),
				"LPS-0200-private"),
			() -> _addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"autoIncrement", "LPS-0200-private"
				).putAll(
					requiredValues
				).build()));

		// Auto increment object field value must not be updatable

		objectEntry1 = _addOrUpdateObjectEntry(
			objectEntry1.getExternalReferenceCode(), 0,
			HashMapBuilder.<String, Serializable>put(
				"autoIncrement", "LPS-2000-private"
			).putAll(
				requiredValues
			).build());

		Assert.assertEquals(
			"LPS-0200-private",
			MapUtil.getString(objectEntry1.getValues(), "autoIncrement"));

		ObjectEntry objectEntry2 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"autoIncrement", "LPS-0150-private"
			).putAll(
				requiredValues
			).build());

		Assert.assertEquals(
			"LPS-0150-private",
			MapUtil.getString(objectEntry2.getValues(), "autoIncrement"));

		ObjectEntry objectEntry3 = _addObjectEntry(
			HashMapBuilder.putAll(
				requiredValues
			).build());

		Assert.assertEquals(
			"LPS-0201-private",
			MapUtil.getString(objectEntry3.getValues(), "autoIncrement"));

		Assert.assertEquals(
			201,
			_counterLocalService.getCurrentId(
				ObjectFieldUtil.getCounterName(objectField)));

		_objectFieldLocalService.deleteObjectField(objectField);

		Assert.assertEquals(
			0,
			_counterLocalService.getCurrentId(
				ObjectFieldUtil.getCounterName(objectField)));
	}

	@Test
	public void testAddObjectEntryWithEncryptedObjectField() throws Exception {
		String key = ObjectFieldTestUtil.generateKey("AES");

		ObjectFieldTestUtil.withEncryptedObjectFieldProperties(
			"AES", true, key,
			() -> {
				_addCustomObjectField(
					new EncryptedObjectFieldBuilder(
					).externalReferenceCode(
						"encryptedObjectFieldERC"
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"encrypted"
					).objectDefinitionId(
						_objectDefinition.getObjectDefinitionId()
					).build());

				ObjectEntry objectEntry = _addObjectEntry(
					HashMapBuilder.<String, Serializable>put(
						"emailAddress", RandomTestUtil.randomString()
					).put(
						"emailAddressRequired", "athanasius@liferay.com"
					).put(
						"encrypted", "test"
					).put(
						"externalReferenceCode", "objectEntryERC"
					).put(
						"listTypeEntryKeyRequired", "listTypeEntryKey1"
					).build());

				_assertCount(1);

				Assert.assertEquals(
					"test",
					MapUtil.getString(
						_objectEntryLocalService.getValues(
							objectEntry.getObjectEntryId()),
						"encrypted"));
			});

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			"objectEntryERC", _objectDefinition.getObjectDefinitionId());

		ObjectFieldTestUtil.withEncryptedObjectFieldProperties(
			"", true, "",
			() -> {
				AssertUtils.assertFailure(
					IllegalArgumentException.class,
					"Please insert an encryption key or remove the object's " +
						"encryption field to recover these entries.",
					() -> _objectEntryLocalService.getValues(
						objectEntry.getObjectEntryId()));

				AssertUtils.assertFailure(
					SystemException.class,
					IllegalArgumentException.class.getName() + ": Empty key",
					() -> _addObjectEntry(
						HashMapBuilder.<String, Serializable>put(
							"emailAddress", RandomTestUtil.randomString()
						).put(
							"emailAddressRequired", "athanasius@liferay.com"
						).put(
							"encrypted", RandomTestUtil.randomString()
						).put(
							"listTypeEntryKeyRequired", "listTypeEntryKey1"
						).build()));

				_assertCount(1);
			});
		ObjectFieldTestUtil.withEncryptedObjectFieldProperties(
			"", true, key,
			() -> {
				AssertUtils.assertFailure(
					PortalException.class,
					StringBundler.concat(
						EncryptorException.class.getName(), ": ",
						EncryptorException.class.getName(), ": ",
						NoSuchAlgorithmException.class.getName(),
						_getNoSuchAlgorithmExceptionMessage()),
					() -> _objectEntryLocalService.getValues(
						objectEntry.getObjectEntryId()));

				AssertUtils.assertFailure(
					SystemException.class,
					StringBundler.concat(
						EncryptorException.class.getName(), ": ",
						EncryptorException.class.getName(), ": ",
						NoSuchAlgorithmException.class.getName(),
						_getNoSuchAlgorithmExceptionMessage()),
					() -> _addObjectEntry(
						HashMapBuilder.<String, Serializable>put(
							"emailAddress", RandomTestUtil.randomString()
						).put(
							"emailAddressRequired", "athanasius@liferay.com"
						).put(
							"encrypted", RandomTestUtil.randomString()
						).put(
							"listTypeEntryKeyRequired", "listTypeEntryKey1"
						).build()));

				_assertCount(1);
			});
		ObjectFieldTestUtil.withEncryptedObjectFieldProperties(
			"AES", true, "",
			() -> {
				AssertUtils.assertFailure(
					IllegalArgumentException.class,
					"Please insert an encryption key or remove the object's " +
						"encryption field to recover these entries.",
					() -> _objectEntryLocalService.getValues(
						objectEntry.getObjectEntryId()));

				AssertUtils.assertFailure(
					SystemException.class,
					IllegalArgumentException.class.getName() + ": Empty key",
					() -> _addObjectEntry(
						HashMapBuilder.<String, Serializable>put(
							"emailAddress", RandomTestUtil.randomString()
						).put(
							"emailAddressRequired", "athanasius@liferay.com"
						).put(
							"encrypted", RandomTestUtil.randomString()
						).put(
							"listTypeEntryKeyRequired", "listTypeEntryKey1"
						).build()));

				_assertCount(1);
			});

		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			"encryptedObjectFieldERC",
			_objectDefinition.getObjectDefinitionId());

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select ", objectField.getDBColumnName(), " from ",
					_objectDefinition.getExtensionDBTableName(), " where ",
					_objectDefinition.getPKObjectFieldDBColumnName(), " = ",
					objectEntry.getObjectEntryId()));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			resultSet.next();

			Assert.assertEquals(
				_encryptor.encrypt(
					new SecretKeySpec(Base64.decode(key), "AES"), "test"),
				resultSet.getString(1));
		}

		_objectFieldLocalService.deleteObjectField(objectField);
	}

	@Test
	public void testAddObjectEntryWithFormulaObjectField() throws Exception {
		ObjectField objectField1 = _addCustomObjectField(
			new FormulaObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"idSum"
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).objectFieldSettings(
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						"script"
					).value(
						"id + id"
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						"output"
					).value(
						ObjectFieldConstants.BUSINESS_TYPE_DECIMAL
					).build())
			).build());
		ObjectField objectField2 = _addCustomObjectField(
			new FormulaObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"overweight"
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
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
			).build());

		ObjectEntry objectEntry = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddress", RandomTestUtil.randomString()
			).put(
				"emailAddressRequired", "athanasius@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).put(
				"weight", 65D
			).build());

		Assert.assertEquals(
			objectEntry.getObjectEntryId() + objectEntry.getObjectEntryId(),
			MapUtil.getDouble(
				_objectEntryLocalService.getValues(
					objectEntry.getObjectEntryId()),
				"idSum"),
			0);
		Assert.assertEquals(
			75D,
			MapUtil.getDouble(
				_objectEntryLocalService.getValues(
					objectEntry.getObjectEntryId()),
				"overweight"),
			0);

		_objectFieldLocalService.deleteObjectField(objectField1);
		_objectFieldLocalService.deleteObjectField(objectField2);
	}

	@FeatureFlags("LPD-43542")
	@Test
	public void testAddObjectEntryWithFormulaObjectFieldAndObjectRelationship()
		throws Exception {

		ObjectDefinition objectDefinition1 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				true, Collections.emptyList());
		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		ObjectRelationshipTestUtil.addObjectRelationship(
			_objectRelationshipLocalService, objectDefinition1,
			objectDefinition2,
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
			"objectRelationship");

		ObjectField objectField1 = ObjectFieldUtil.addCustomObjectField(
			new IntegerObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).localized(
				true
			).name(
				"a" + RandomTestUtil.randomString()
			).objectDefinitionId(
				objectDefinition1.getObjectDefinitionId()
			).userId(
				TestPropsValues.getUserId()
			).build());
		ObjectField objectField2 = ObjectFieldUtil.addCustomObjectField(
			new IntegerObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"a" + RandomTestUtil.randomString()
			).objectDefinitionId(
				objectDefinition2.getObjectDefinitionId()
			).userId(
				TestPropsValues.getUserId()
			).build());

		ObjectField objectField3 = ObjectFieldUtil.addCustomObjectField(
			new FormulaObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"a" + RandomTestUtil.randomString()
			).objectDefinitionId(
				objectDefinition2.getObjectDefinitionId()
			).objectFieldSettings(
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						"output"
					).value(
						ObjectFieldConstants.BUSINESS_TYPE_INTEGER
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						"script"
					).value(
						String.format(
							"r_objectRelationship_%s_%s + %s",
							objectDefinition1.getPKObjectFieldName(),
							objectField1.getName(), objectField2.getName())
					).build())
			).userId(
				TestPropsValues.getUserId()
			).build());

		Locale originalThemeDisplayLocale =
			LocaleThreadLocal.getThemeDisplayLocale();

		try {
			int randomInt1 = RandomTestUtil.randomInt(1, 10);
			int randomInt2 = RandomTestUtil.randomInt(1, 10);
			int randomInt3 = RandomTestUtil.randomInt(1, 10);

			ObjectEntry objectEntry = _addObjectEntry(
				0, objectDefinition2.getObjectDefinitionId(),
				HashMapBuilder.<String, Serializable>put(
					"r_objectRelationship_" +
						objectDefinition1.getPKObjectFieldName(),
					() -> {
						ObjectEntry relatedObjectEntry = _addObjectEntry(
							0, objectDefinition1.getObjectDefinitionId(),
							Collections.singletonMap(
								objectField1.getI18nObjectFieldName(),
								HashMapBuilder.put(
									"en_US", randomInt1
								).put(
									"pt_BR", randomInt2
								).build()));

						return relatedObjectEntry.getObjectEntryId();
					}
				).put(
					objectField2.getName(), randomInt3
				).build());

			LocaleThreadLocal.setThemeDisplayLocale(LocaleUtil.BRAZIL);

			Assert.assertEquals(
				randomInt2 + randomInt3,
				MapUtil.getInteger(
					_objectEntryLocalService.getValues(objectEntry),
					objectField3.getName()));

			LocaleThreadLocal.setThemeDisplayLocale(LocaleUtil.US);

			Assert.assertEquals(
				randomInt1 + randomInt3,
				MapUtil.getInteger(
					_objectEntryLocalService.getValues(objectEntry),
					objectField3.getName()));
		}
		finally {
			LocaleThreadLocal.setThemeDisplayLocale(originalThemeDisplayLocale);

			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition1);
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition2);
		}
	}

	@Test
	public void testAddObjectEntryWithHierarchyAndLocalizedValues()
		throws Exception {

		ObjectField objectField = new TextObjectFieldBuilder(
		).labelMap(
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
		).localized(
			true
		).name(
			"a" + RandomTestUtil.randomString()
		).build();

		ObjectDefinition objectDefinitionA =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				true, Collections.singletonList(objectField));
		ObjectDefinition objectDefinitionAA =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				true, Collections.singletonList(objectField));
		ObjectDefinition objectDefinitionAAA =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				true, Collections.singletonList(objectField));

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			Arrays.asList(
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService, objectDefinitionA,
					objectDefinitionAA,
					ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
					"objectRelationship1"),
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService, objectDefinitionAA,
					objectDefinitionAAA,
					ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
					"objectRelationship2")));

		Map<String, Serializable> localizedValues =
			HashMapBuilder.<String, Serializable>put(
				objectField.getI18nObjectFieldName(),
				HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).put(
					"pt_BR", RandomTestUtil.randomString()
				).build()
			).build();

		ObjectEntry objectEntryA = _addObjectEntry(
			0, objectDefinitionA.getObjectDefinitionId(), localizedValues);

		_assertObjectEntryLocalizedValues(
			localizedValues, objectEntryA, objectField);

		ObjectEntry objectEntryAA = _addObjectEntry(
			0, objectDefinitionAA.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>putAll(
				localizedValues
			).put(
				"r_objectRelationship1_" +
					objectDefinitionA.getPKObjectFieldName(),
				objectEntryA.getObjectEntryId()
			).build());

		_assertObjectEntryLocalizedValues(
			localizedValues, objectEntryAA, objectField);

		ObjectEntry objectEntryAAA = _addObjectEntry(
			0, objectDefinitionAAA.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>putAll(
				localizedValues
			).put(
				"r_objectRelationship2_" +
					objectDefinitionAA.getPKObjectFieldName(),
				objectEntryAA.getObjectEntryId()
			).build());

		_assertObjectEntryLocalizedValues(
			localizedValues, objectEntryAAA, objectField);

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {
				objectDefinitionA.getName(), objectDefinitionAA.getName(),
				objectDefinitionAAA.getName()
			},
			_objectEntryLocalService, _objectRelationshipLocalService);
	}

	@Test
	public void testAddObjectEntryWithHierarchyAndObjectValidationRule()
		throws Exception {

		ObjectField objectField = new TextObjectFieldBuilder(
		).labelMap(
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
		).name(
			"a" + RandomTestUtil.randomString()
		).build();

		ObjectDefinition objectDefinitionA =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				false, Collections.singletonList(objectField));
		ObjectDefinition objectDefinitionAA =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				false, Collections.singletonList(objectField));
		ObjectDefinition objectDefinitionAAA =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				false, Collections.singletonList(objectField));

		ObjectValidationRule objectValidationRuleA = _addObjectValidationRule(
			objectDefinitionA,
			String.format("not(isEmpty(%s))", objectField.getName()));
		ObjectValidationRule objectValidationRuleAA = _addObjectValidationRule(
			objectDefinitionAA,
			String.format("not(isEmpty(%s))", objectField.getName()));
		ObjectValidationRule objectValidationRuleAAA = _addObjectValidationRule(
			objectDefinitionAAA,
			String.format("not(isEmpty(%s))", objectField.getName()));

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			Arrays.asList(
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService, objectDefinitionA,
					objectDefinitionAA,
					ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
					"objectRelationship1"),
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService, objectDefinitionAA,
					objectDefinitionAAA,
					ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
					"objectRelationship2")));

		ObjectEntry objectEntryA = _addObjectEntry(
			0, objectDefinitionA.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				objectField.getName(), RandomTestUtil.randomString()
			).build());

		ObjectEntry objectEntryAA = _addObjectEntry(
			0, objectDefinitionAA.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				objectField.getName(), RandomTestUtil.randomString()
			).put(
				"r_objectRelationship1_" +
					objectDefinitionA.getPKObjectFieldName(),
				objectEntryA.getObjectEntryId()
			).build());

		_addObjectEntry(
			0, objectDefinitionAAA.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				objectField.getName(), RandomTestUtil.randomString()
			).put(
				"r_objectRelationship2_" +
					objectDefinitionAA.getPKObjectFieldName(),
				objectEntryAA.getObjectEntryId()
			).build());

		try {
			_addObjectEntry(
				0, objectDefinitionA.getObjectDefinitionId(),
				Collections.emptyMap());

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			_assertFailureObjectValidationRule(
				modelListenerException, objectValidationRuleA);
		}

		try {
			_addObjectEntry(
				0, objectDefinitionAA.getObjectDefinitionId(),
				HashMapBuilder.<String, Serializable>put(
					"r_objectRelationship1_" +
						objectDefinitionA.getPKObjectFieldName(),
					objectEntryA.getObjectEntryId()
				).build());

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			_assertFailureObjectValidationRule(
				modelListenerException, objectValidationRuleAA);
		}

		try {
			_addObjectEntry(
				0, objectDefinitionAAA.getObjectDefinitionId(),
				HashMapBuilder.<String, Serializable>put(
					"r_objectRelationship2_" +
						objectDefinitionAA.getPKObjectFieldName(),
					objectEntryAA.getObjectEntryId()
				).build());

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			_assertFailureObjectValidationRule(
				modelListenerException, objectValidationRuleAAA);
		}

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {
				objectDefinitionA.getName(), objectDefinitionAA.getName(),
				objectDefinitionAAA.getName()
			},
			_objectEntryLocalService, _objectRelationshipLocalService);
	}

	@Test
	public void testAddObjectEntryWithObjectValidationRule() throws Exception {

		// Composite key field values must be unique

		ObjectValidationRule objectValidationRule1 = _addObjectValidationRule(
			ObjectValidationRuleConstants.ENGINE_TYPE_COMPOSITE_KEY,
			LocalizedMapUtil.getLocalizedMap(
				"Composite key field values must be unique"),
			ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
			StringPool.BLANK,
			Arrays.asList(
				new ObjectValidationRuleSettingBuilder(
				).name(
					ObjectValidationRuleSettingConstants.
						NAME_COMPOSITE_KEY_OBJECT_FIELD_ID
				).value(
					() -> {
						ObjectField objectField =
							_objectFieldLocalService.fetchObjectField(
								_objectDefinition.getObjectDefinitionId(),
								"emailAddressRequired");

						return String.valueOf(objectField.getObjectFieldId());
					}
				).build(),
				new ObjectValidationRuleSettingBuilder(
				).name(
					ObjectValidationRuleSettingConstants.
						NAME_COMPOSITE_KEY_OBJECT_FIELD_ID
				).value(
					() -> {
						ObjectField objectField =
							_objectFieldLocalService.fetchObjectField(
								_objectDefinition.getObjectDefinitionId(),
								"listTypeEntryKey");

						return String.valueOf(objectField.getObjectFieldId());
					}
				).build(),
				new ObjectValidationRuleSettingBuilder(
				).name(
					ObjectValidationRuleSettingConstants.
						NAME_COMPOSITE_KEY_OBJECT_FIELD_ID
				).value(
					() -> {
						ObjectField objectField =
							_objectFieldLocalService.fetchObjectField(
								_objectDefinition.getObjectDefinitionId(),
								"listTypeEntryKeyRequired");

						return String.valueOf(objectField.getObjectFieldId());
					}
				).build(),
				new ObjectValidationRuleSettingBuilder(
				).name(
					ObjectValidationRuleSettingConstants.
						NAME_COMPOSITE_KEY_OBJECT_FIELD_ID
				).value(
					() -> {
						ObjectField objectField =
							_objectFieldLocalService.fetchObjectField(
								_objectDefinition.getObjectDefinitionId(),
								"numberOfBooksWritten");

						return String.valueOf(objectField.getObjectFieldId());
					}
				).build()));

		Map<String, Serializable> values =
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "bob@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).put(
				"numberOfBooksWritten", 5
			).build();

		_addObjectEntry(values);

		_assertCount(1);

		try {
			_addObjectEntry(values);

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			_assertFailureObjectValidationRule(
				modelListenerException, objectValidationRule1);
		}

		_objectValidationRuleLocalService.deleteObjectValidationRule(
			objectValidationRule1);

		// Date must be in the future

		ObjectValidationRule objectValidationRule2 = _addObjectValidationRule(
			ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
			LocalizedMapUtil.getLocalizedMap("Date must be in the future"),
			"futureDates(date, currentDate)");

		LocalDate todayLocalDate = LocalDate.now();

		LocalDate tomorrowLocalDate = todayLocalDate.plusDays(1);

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"date", tomorrowLocalDate.toString()
			).put(
				"emailAddressRequired", "bob@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build());

		_assertCount(2);

		// Date time must be in the future

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
			"yyyy-MM-dd HH:mm");

		LocalDateTime localDateTime = LocalDateTime.now();

		String timeString = dateTimeFormatter.format(localDateTime.plusDays(1));

		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			_objectDefinition.getObjectDefinitionId(), "time");

		ObjectValidationRule objectValidationRule3 = _addObjectValidationRule(
			ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
			LocalizedMapUtil.getLocalizedMap("Date time must be in the future"),
			ObjectValidationRuleConstants.OUTPUT_TYPE_PARTIAL_VALIDATION,
			String.format(
				"futureDates(%s, \"%s\")", objectField.getName(),
				dateTimeFormatter.format(LocalDateTime.now())),
			Collections.singletonList(
				new ObjectValidationRuleSettingBuilder(
				).name(
					ObjectValidationRuleSettingConstants.
						NAME_OUTPUT_OBJECT_FIELD_ID
				).value(
					String.valueOf(objectField.getObjectFieldId())
				).build()));

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"date", tomorrowLocalDate.toString()
			).put(
				"emailAddressRequired", RandomTestUtil.randomString()
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).put(
				"time", timeString
			).build());

		_assertCount(3);

		// Field must be an email address

		ObjectValidationRule objectValidationRule4 = _addObjectValidationRule(
			ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
			LocalizedMapUtil.getLocalizedMap("Field must be an email address"),
			"isEmailAddress(emailAddress)");

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"date", tomorrowLocalDate.toString()
			).put(
				"emailAddress", "bob@liferay.com"
			).put(
				"emailAddressRequired", "bob@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).put(
				"time", timeString
			).build());

		_assertCount(4);

		// Must be over 18 years old

		Class<?> clazz = getClass();

		ObjectValidationRule objectValidationRule5 = _addObjectValidationRule(
			ObjectValidationRuleConstants.ENGINE_TYPE_GROOVY,
			LocalizedMapUtil.getLocalizedMap("Must be over 18 years old"),
			StringUtil.read(
				clazz,
				StringBundler.concat(
					"dependencies/", clazz.getSimpleName(), StringPool.PERIOD,
					testName.getMethodName(), ".groovy")));

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"birthday", "2000-12-25"
			).put(
				"date", tomorrowLocalDate.toString()
			).put(
				"emailAddressRequired", "bob@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).put(
				"time", timeString
			).build());

		_assertCount(5);

		// Names must be equals

		ObjectValidationRule objectValidationRule6 = _addObjectValidationRule(
			ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
			LocalizedMapUtil.getLocalizedMap("Names must be equals"),
			"equals(lastName, middleName)");

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"birthday", "2000-12-25"
			).put(
				"date", tomorrowLocalDate.toString()
			).put(
				"emailAddress", "john@liferay.com"
			).put(
				"emailAddressRequired", "bob@liferay.com"
			).put(
				"lastName", "Doe"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).put(
				"middleName", "Doe"
			).put(
				"time", timeString
			).build());

		_assertCount(6);

		values = HashMapBuilder.<String, Serializable>put(
			"birthday", "2010-12-25"
		).put(
			"date", "2010-12-25"
		).put(
			"emailAddress", RandomTestUtil.randomString()
		).put(
			"emailAddressRequired", RandomTestUtil.randomString()
		).put(
			"lastName", RandomTestUtil.randomString()
		).put(
			"listTypeEntryKeyRequired", "listTypeEntryKey1"
		).put(
			"middleName", RandomTestUtil.randomString()
		).put(
			"time", "2000-12-25 08:50"
		).build();

		try {
			_addObjectEntry(values);

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
				objectValidationRuleResults.toString(), 5,
				objectValidationRuleResults.size());

			_assertObjectValidationRuleResult(
				objectValidationRule2.getErrorLabel(LocaleUtil.getDefault()),
				null, objectValidationRuleResults.get(0));
			_assertObjectValidationRuleResult(
				objectValidationRule3.getErrorLabel(LocaleUtil.getDefault()),
				objectField.getName(), objectValidationRuleResults.get(1));
			_assertObjectValidationRuleResult(
				objectValidationRule4.getErrorLabel(LocaleUtil.getDefault()),
				null, objectValidationRuleResults.get(2));
			_assertObjectValidationRuleResult(
				objectValidationRule5.getErrorLabel(LocaleUtil.getDefault()),
				null, objectValidationRuleResults.get(3));
			_assertObjectValidationRuleResult(
				objectValidationRule6.getErrorLabel(LocaleUtil.getDefault()),
				null, objectValidationRuleResults.get(4));
		}

		// Disable object validation rule 6

		objectValidationRule6.setActive(false);

		_objectValidationRuleLocalService.updateObjectValidationRule(
			objectValidationRule6);

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"birthday", "2000-12-25"
			).put(
				"date", tomorrowLocalDate.toString()
			).put(
				"emailAddressRequired", "bob@liferay.com"
			).put(
				"lastName", RandomTestUtil.randomString()
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).put(
				"middleName", RandomTestUtil.randomString()
			).put(
				"time", timeString
			).build());

		_assertCount(7);

		// No such engine

		String engine = RandomTestUtil.randomString();

		_addObjectValidationRule(
			engine,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			StringPool.BLANK);

		Map<String, Serializable> finalValues = values;

		AssertUtils.assertFailure(
			ModelListenerException.class,
			StringBundler.concat(
				ObjectValidationRuleEngineException.NoSuchEngine.class.
					getName(),
				": Engine \"", engine, "\" does not exist"),
			() -> _addObjectEntry(finalValues));

		// Skip object validation rules

		_objectDefinition.setEnableObjectEntryDraft(true);

		_objectDefinitionLocalService.updateObjectDefinition(_objectDefinition);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		_objectFieldLocalService.updateRequired(
			_objectRelationship.getObjectFieldId2(), true);

		_objectEntryLocalService.addObjectEntry(
			TestPropsValues.getUserId(), 0,
			_objectDefinition.getObjectDefinitionId(), values, serviceContext);

		_assertCount(8);

		_objectFieldLocalService.updateRequired(
			_objectRelationship.getObjectFieldId2(), false);
	}

	@FeatureFlags("LPD-31212")
	@Test
	public void testAddObjectEntryWithRichTextObjectField() throws Exception {
		ObjectDefinition objectDefinition = _publishCustomObjectDefinition(
			true,
			Arrays.asList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
					RandomTestUtil.randomString(), "name",
					Arrays.asList(
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_UNIQUE_VALUES
						).value(
							Boolean.TRUE.toString()
						).build()),
					false)));

		_addCustomObjectField(
			new RichTextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"richText"
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).build());

		objectDefinition.setScope(ObjectDefinitionConstants.SCOPE_SITE);

		objectDefinition = _objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);

		Map<String, Serializable> expectedValues =
			HashMapBuilder.<String, Serializable>put(
				"richText",
				StringBundler.concat(
					"<div class=\"embed-responsive embed-responsive-16by9\" ",
					"data-embed-id=",
					"\"https://www.youtube.com/embed/6LjQ7Z99N74?rel=0\" ",
					"data-styles=\"{&quot;width&quot;:&quot;81%&quot;}",
					"\" style=\"width:81%\"><iframe allow=\"autoplay; ",
					"encrypted-media\" allowfullscreen=\"\" frameborder=\"0\" ",
					"height=\"315\" src=",
					"\"https://www.youtube.com/embed/6LjQ7Z99N74?rel=0\" ",
					"width=\"560\"></iframe></div><p>&nbsp;</p>")
			).build();

		ObjectEntry objectEntry = _addObjectEntry(
			TestPropsValues.getGroupId(),
			objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.create(
				expectedValues
			).build());

		Map<String, Serializable> actualValues = objectEntry.getValues();

		Assert.assertEquals(
			expectedValues.get("richText"), actualValues.get("richText"));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Test
	public void testAddObjectEntryWithStandaloneObjectAction()
		throws Exception {

		ObjectDefinition objectDefinition1 =
			ObjectDefinitionTestUtil.publishObjectDefinition();
		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, objectDefinition1,
				objectDefinition2);

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			Collections.singletonList(objectRelationship));

		ObjectAction objectAction = _objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			objectDefinition2.getObjectDefinitionId(), true, null,
			RandomTestUtil.randomString(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_GROOVY,
			ObjectActionTriggerConstants.KEY_STANDALONE,
			UnicodePropertiesBuilder.put(
				"script", "println \"Hello World\""
			).build(),
			false);

		Assert.assertThat(
			_resourceActions.getModelResourceActions(
				objectDefinition2.getClassName()),
			CoreMatchers.hasItem(objectAction.getName()));
		Assert.assertThat(
			_resourceActions.getModelResourceOwnerDefaultActions(
				objectDefinition2.getClassName()),
			CoreMatchers.hasItem(objectAction.getName()));

		ObjectEntry objectEntry1 = _addObjectEntry(
			0, objectDefinition1.getObjectDefinitionId(),
			Collections.emptyMap());
		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			objectRelationship.getObjectFieldId2());

		ObjectEntry objectEntry2 = _addObjectEntry(
			0, objectDefinition2.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				objectField.getName(), objectEntry1.getObjectEntryId()
			).build());

		Assert.assertTrue(
			_hasResourcePermission(
				objectAction, objectDefinition2, objectEntry2));

		_objectActionLocalService.deleteObjectAction(
			objectAction.getObjectActionId());

		Assert.assertThat(
			_resourceActions.getModelResourceActions(
				objectDefinition2.getClassName()),
			CoreMatchers.not(CoreMatchers.hasItem(objectAction.getName())));
		Assert.assertThat(
			_resourceActions.getModelResourceOwnerDefaultActions(
				objectDefinition2.getClassName()),
			CoreMatchers.not(CoreMatchers.hasItem(objectAction.getName())));
		AssertUtils.assertFailure(
			NoSuchResourceActionException.class,
			objectDefinition2.getClassName() + StringPool.POUND +
				objectAction.getName(),
			() -> _hasResourcePermission(
				objectAction, objectDefinition2, objectEntry2));

		ObjectEntry objectEntry3 = _addObjectEntry(
			0, objectDefinition2.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				objectField.getName(), objectEntry1.getObjectEntryId()
			).build());

		Assert.assertFalse(
			_hasResourcePermission(
				objectAction, objectDefinition2, objectEntry3));

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {
				objectDefinition1.getName(), objectDefinition2.getName()
			},
			_objectEntryLocalService, _objectRelationshipLocalService);
	}

	@Test
	public void testAddOrUpdateObjectEntry() throws Exception {
		_assertCount(0);

		Map<String, Serializable> values =
			HashMapBuilder.<String, Serializable>put(
				"emailAddressDomain", "@liferay.com"
			).put(
				"emailAddressRequired", "peter@liferay.com"
			).put(
				"firstName", "Peter"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build();

		ObjectEntry objectEntry = _addOrUpdateObjectEntry("peter", 0, values);

		_assertCount(1);

		_assertObjectEntryValues(
			25, values,
			_objectEntryLocalService.getValues(objectEntry.getObjectEntryId()));

		values = HashMapBuilder.<String, Serializable>put(
			"emailAddressDomain", "@liferay.com"
		).put(
			"emailAddressRequired", "peter@liferay.com"
		).put(
			"firstName", "Pedro"
		).put(
			"listTypeEntryKeyRequired", "listTypeEntryKey2"
		).build();

		_addOrUpdateObjectEntry("peter", 0, values);

		_assertCount(1);

		_assertObjectEntryValues(
			25, values,
			_objectEntryLocalService.getValues(objectEntry.getObjectEntryId()));

		_addOrUpdateObjectEntry(
			"james", 0,
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "james@liferay.com"
			).put(
				"firstName", "James"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey3"
			).build());

		_assertCount(2);

		_addOrUpdateObjectEntry(
			"john", 0,
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "john@liferay.com"
			).put(
				"firstName", "John"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build());

		_assertCount(3);

		// TODO Test where group ID is not 0

		// TODO Test where group ID does not belong to right company

		// TODO Test object entries scoped to company vs. scoped to group

	}

	@FeatureFlags("LPD-21926")
	@Test
	public void testAddOrUpdateObjectEntryWithFriendlyURL() throws Exception {

		// Scope by company

		ObjectDefinition companyObjectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				true, Collections.emptyList());

		ObjectEntry companyObjectEntry1 = _addObjectEntry(
			0, companyObjectDefinition.getObjectDefinitionId(),
			Collections.emptyMap());

		_assertURLTitleMap(
			HashMapBuilder.put(
				"en_US", companyObjectEntry1.getExternalReferenceCode()
			).build(),
			companyObjectDefinition, companyObjectEntry1);

		companyObjectEntry1 = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), companyObjectEntry1.getObjectEntryId(),
			HashMapBuilder.<String, Serializable>put(
				"able", "Test URL"
			).put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_assertURLTitleMap(
			HashMapBuilder.put(
				"en_US", "test-url"
			).build(),
			companyObjectDefinition, companyObjectEntry1);

		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).localized(
				true
			).name(
				"a" + RandomTestUtil.randomString()
			).objectDefinitionId(
				companyObjectDefinition.getObjectDefinitionId()
			).build());

		_objectDefinitionLocalService.updateTitleObjectFieldId(
			companyObjectDefinition.getObjectDefinitionId(),
			objectField.getObjectFieldId());

		ObjectEntry companyObjectEntry2 = _addObjectEntry(
			0, companyObjectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				objectField.getI18nObjectFieldName(),
				HashMapBuilder.put(
					"en_US", "Test URL"
				).put(
					"pt_BR", "Test URL"
				).build()
			).put(
				"able", RandomTestUtil.randomString()
			).build());

		_assertURLTitleMap(
			HashMapBuilder.put(
				"en_US", "test-url-1"
			).put(
				"pt_BR", "test-url-1"
			).build(),
			companyObjectDefinition, companyObjectEntry2);

		_assertFriendlyURLEntriesSize(
			2, companyObjectDefinition, companyObjectEntry1);
		_assertFriendlyURLEntriesSize(
			1, companyObjectDefinition, companyObjectEntry2);

		_objectDefinitionLocalService.deleteObjectDefinition(
			companyObjectDefinition);

		// Scope by site

		ObjectDefinition siteObjectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		siteObjectDefinition.setScope(ObjectDefinitionConstants.SCOPE_SITE);

		siteObjectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				siteObjectDefinition);

		Group group1 = GroupTestUtil.addGroup();

		ObjectEntry siteObjectEntry1 = _addObjectEntry(
			group1.getGroupId(), siteObjectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"able", "Test URL"
			).build());

		_assertURLTitleMap(
			HashMapBuilder.put(
				"en_US", "test-url"
			).build(),
			siteObjectDefinition, siteObjectEntry1);

		Group group2 = GroupTestUtil.addGroup();

		ObjectEntry siteObjectEntry2 = _addObjectEntry(
			group2.getGroupId(), siteObjectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"able", "Test URL"
			).build());

		_assertURLTitleMap(
			HashMapBuilder.put(
				"en_US", "test-url"
			).build(),
			siteObjectDefinition, siteObjectEntry2);

		_objectDefinitionLocalService.deleteObjectDefinition(
			siteObjectDefinition);

		_assertFriendlyURLEntriesSize(
			0, siteObjectDefinition, siteObjectEntry1);
		_assertFriendlyURLEntriesSize(
			0, siteObjectDefinition, siteObjectEntry2);
	}

	@Test
	public void testAddOrUpdateObjectEntryWithObjectDefinitionTree()
		throws Exception {

		Tree objectDefinitionTree = TreeTestUtil.createObjectDefinitionTree(
			_objectDefinitionLocalService, _objectRelationshipLocalService,
			true,
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

		ObjectDefinition objectDefinitionA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_A");

		Tree tree1 = TreeTestUtil.createObjectEntryTree(
			"1", _objectDefinitionLocalService, _objectEntryLocalService,
			_objectFieldLocalService, _objectRelationshipLocalService,
			objectDefinitionA.getObjectDefinitionId());

		TreeTestUtil.assertObjectEntryTree(
			LinkedHashMapBuilder.put(
				"A1", new String[] {"AA1", "AB1"}
			).put(
				"AA1", new String[] {"AAA1", "AAB1"}
			).put(
				"AB1", new String[0]
			).put(
				"AAA1", new String[0]
			).put(
				"AAB1", new String[0]
			).build(),
			tree1, _objectEntryLocalService);

		Tree tree2 = TreeTestUtil.createObjectEntryTree(
			"2", _objectDefinitionLocalService, _objectEntryLocalService,
			_objectFieldLocalService, _objectRelationshipLocalService,
			objectDefinitionA.getObjectDefinitionId());

		TreeTestUtil.assertObjectEntryTree(
			LinkedHashMapBuilder.put(
				"A2", new String[] {"AA2", "AB2"}
			).put(
				"AA2", new String[] {"AAA2", "AAB2"}
			).put(
				"AB2", new String[0]
			).put(
				"AAA2", new String[0]
			).put(
				"AAB2", new String[0]
			).build(),
			tree2, _objectEntryLocalService);

		ObjectDefinition objectDefinitionAA =
			_objectDefinitionLocalService.fetchObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AA");

		_objectEntryLocalService.addOrUpdateObjectEntry(
			"AA1", TestPropsValues.getUserId(), 0,
			objectDefinitionAA.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				() -> {
					Node node = objectDefinitionTree.getNode(
						objectDefinitionAA.getObjectDefinitionId());

					ObjectRelationship objectRelationship =
						_objectRelationshipLocalService.getObjectRelationship(
							node.getEdge(
							).getObjectRelationshipId());

					ObjectField objectField =
						_objectFieldLocalService.fetchObjectField(
							objectRelationship.getObjectFieldId2());

					return objectField.getName();
				},
				() -> {
					ObjectEntry objectEntry =
						_objectEntryLocalService.getObjectEntry(
							"A2", objectDefinitionA.getObjectDefinitionId());

					return objectEntry.getObjectEntryId();
				}
			).build(),
			ServiceContextTestUtil.getServiceContext());

		ObjectEntryTreeFactory objectEntryTreeFactory =
			new ObjectEntryTreeFactory(
				_objectEntryLocalService, _objectRelationshipLocalService);

		tree1 = objectEntryTreeFactory.create(
			tree1.getRootNode(
			).getPrimaryKey());

		TreeTestUtil.assertObjectEntryTree(
			LinkedHashMapBuilder.put(
				"A1", new String[] {"AB1"}
			).put(
				"AB1", new String[0]
			).build(),
			tree1, _objectEntryLocalService);

		tree2 = objectEntryTreeFactory.create(
			tree2.getRootNode(
			).getPrimaryKey());

		TreeTestUtil.assertObjectEntryTree(
			LinkedHashMapBuilder.put(
				"A2", new String[] {"AA1", "AA2", "AB2"}
			).put(
				"AA1", new String[] {"AAA1", "AAB1"}
			).put(
				"AA2", new String[] {"AAA2", "AAB2"}
			).put(
				"AB2", new String[0]
			).put(
				"AAA1", new String[0]
			).put(
				"AAB1", new String[0]
			).put(
				"AAA2", new String[0]
			).put(
				"AAB2", new String[0]
			).build(),
			tree2, _objectEntryLocalService);

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AB", "C_AAA", "C_AAB"},
			_objectEntryLocalService, _objectRelationshipLocalService);
	}

	@Test
	public void testAuditRouter() throws Exception {
		Queue<AuditMessage> auditMessages = new LinkedList<>();

		AuditRouter auditRouter =
			(AuditRouter)ReflectionTestUtil.getAndSetFieldValue(
				_objectEntryModelListener, "_auditRouter",
				ProxyUtil.newProxyInstance(
					AuditRouter.class.getClassLoader(),
					new Class<?>[] {AuditRouter.class},
					(proxy, method, arguments) -> {
						auditMessages.add((AuditMessage)arguments[0]);

						return null;
					}));

		_objectDefinition.setEnableObjectEntryHistory(true);

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);

		ObjectEntry objectEntry = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "james@liferay.com"
			).put(
				"firstName", "James"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).put(
				"upload",
				() -> {
					FileEntry fileEntry = _addTempFileEntry("Old Testament");

					return fileEntry.getFileEntryId();
				}
			).build());

		AuditMessage auditMessage = auditMessages.poll();

		JSONAssert.assertEquals(
			JSONUtil.put(
				"emailAddressRequired", "james@liferay.com"
			).put(
				"firstName", "James"
			).put(
				"listTypeEntryKeyRequired",
				JSONUtil.put(
					"key", "listTypeEntryKey1"
				).put(
					"name", "List Type Entry Key 1"
				)
			).put(
				"upload", JSONUtil.put("title", "Old Testament")
			).toString(),
			String.valueOf(auditMessage.getAdditionalInfo()),
			JSONCompareMode.STRICT_ORDER);

		auditMessages.clear();

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "peter@liferay.com"
			).put(
				"firstName", "Peter"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey3"
			).put(
				"upload",
				() -> {
					FileEntry fileEntry = _addTempFileEntry("New Testament");

					return fileEntry.getFileEntryId();
				}
			).build(),
			ServiceContextTestUtil.getServiceContext());

		auditMessage = auditMessages.poll();

		JSONAssert.assertEquals(
			JSONUtil.put(
				"attributes",
				JSONUtil.putAll(
					JSONUtil.put(
						"name", "emailAddressRequired"
					).put(
						"newValue", "peter@liferay.com"
					).put(
						"oldValue", "james@liferay.com"
					),
					JSONUtil.put(
						"name", "firstName"
					).put(
						"newValue", "Peter"
					).put(
						"oldValue", "James"
					),
					JSONUtil.put(
						"name", "listTypeEntryKeyRequired"
					).put(
						"newValue",
						JSONUtil.put(
							"key", "listTypeEntryKey3"
						).put(
							"name", "List Type Entry Key 3"
						)
					).put(
						"oldValue",
						JSONUtil.put(
							"key", "listTypeEntryKey1"
						).put(
							"name", "List Type Entry Key 1"
						)
					),
					JSONUtil.put(
						"name", "upload"
					).put(
						"newValue", JSONUtil.put("title", "New Testament")
					).put(
						"oldValue", JSONUtil.put("title", "Old Testament")
					))
			).toString(),
			String.valueOf(auditMessage.getAdditionalInfo()),
			JSONCompareMode.STRICT_ORDER);

		auditMessages.clear();

		_objectEntryLocalService.deleteObjectEntry(
			objectEntry.getObjectEntryId());

		auditMessage = auditMessages.poll();

		JSONAssert.assertEquals(
			JSONUtil.put(
				"emailAddressRequired", "peter@liferay.com"
			).put(
				"firstName", "Peter"
			).put(
				"listTypeEntryKeyRequired",
				JSONUtil.put(
					"key", "listTypeEntryKey3"
				).put(
					"name", "List Type Entry Key 3"
				)
			).put(
				"upload", JSONUtil.put("title", "New Testament")
			).toString(),
			String.valueOf(auditMessage.getAdditionalInfo()),
			JSONCompareMode.STRICT_ORDER);

		ReflectionTestUtil.setFieldValue(
			_objectEntryModelListener, "_auditRouter", auditRouter);
	}

	@Test
	public void testCachedValues() throws Exception {
		ObjectEntry objectEntry = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "peter@liferay.com"
			).put(
				"firstName", "Peter"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build());

		FinderCacheUtil.clearDSLQueryCache(_objectDefinition.getDBTableName());
		FinderCacheUtil.clearDSLQueryCache(
			_objectDefinition.getExtensionDBTableName());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"org.hibernate.SQL", LoggerTestUtil.DEBUG)) {

			objectEntry = _objectEntryLocalService.getObjectEntry(
				objectEntry.getObjectEntryId());

			Assert.assertNull(
				ReflectionTestUtil.getFieldValue(objectEntry, "_values"));

			objectEntry.getValues();

			Assert.assertNotNull(
				ReflectionTestUtil.getFieldValue(objectEntry, "_values"));

			Assert.assertTrue(_containsObjectEntryValuesSQLQuery(logCapture));
		}

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"org.hibernate.SQL", LoggerTestUtil.DEBUG)) {

			objectEntry = _objectEntryLocalService.getObjectEntry(
				objectEntry.getObjectEntryId());

			objectEntry.getValues();

			Assert.assertFalse(_containsObjectEntryValuesSQLQuery(logCapture));
		}
	}

	@Test
	public void testDeleteObjectEntry() throws Exception {
		ObjectEntry objectEntry1 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "peter@liferay.com"
			).put(
				"firstName", "Peter"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build());
		ObjectEntry objectEntry2 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "john@liferay.com"
			).put(
				"firstName", "John"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey3"
			).build());

		_assertCount(2);

		// Delete object entry

		_objectEntryLocalService.deleteObjectEntry(
			objectEntry1.getObjectEntryId());

		AssertUtils.assertFailure(
			NoSuchObjectEntryException.class,
			"No ObjectEntry exists with the primary key " +
				objectEntry1.getObjectEntryId(),
			() -> _objectEntryLocalService.deleteObjectEntry(
				objectEntry1.getObjectEntryId()));

		_objectEntryLocalService.deleteObjectEntry(objectEntry1);

		AssertUtils.assertFailure(
			NoSuchObjectEntryException.class,
			"No ObjectEntry exists with the primary key " +
				objectEntry1.getObjectEntryId(),
			() -> _objectEntryLocalService.getValues(
				objectEntry1.getObjectEntryId()));

		_assertCount(1);

		_objectEntryLocalService.deleteObjectEntry(objectEntry2);

		_assertCount(0);

		// Delete object entry when its object definition is related to a draft
		// object definition

		ObjectEntry objectEntry3 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "bruce@liferay.com"
			).put(
				"firstName", "Bruce"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey4"
			).build());

		_objectRelationshipLocalService.addObjectRelationship(
			null, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			_draftObjectDefinition.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			StringUtil.randomId(), false,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		_objectEntryLocalService.deleteObjectEntry(objectEntry3);

		_assertCount(0);

		// Delete object entry with an inactive object definition

		ObjectEntry objectEntry4 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "john@liferay.com"
			).put(
				"firstName", "John"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey3"
			).build());

		_objectDefinitionLocalService.updateCustomObjectDefinition(
			_objectDefinition.getExternalReferenceCode(),
			_objectDefinition.getObjectDefinitionId(),
			_objectDefinition.getAccountEntryRestrictedObjectFieldId(),
			_objectDefinition.getDescriptionObjectFieldId(), 0,
			_objectDefinition.getTitleObjectFieldId(),
			_objectDefinition.isAccountEntryRestricted(), false,
			_objectDefinition.getClassName(),
			_objectDefinition.isEnableCategorization(),
			_objectDefinition.isEnableComments(),
			_objectDefinition.isEnableFriendlyURLCustomization(),
			_objectDefinition.isEnableIndexSearch(),
			_objectDefinition.isEnableLocalization(),
			_objectDefinition.isEnableObjectEntryDraft(),
			_objectDefinition.isEnableObjectEntryHistory(),
			_objectDefinition.getLabelMap(), _objectDefinition.getName(),
			_objectDefinition.getPanelAppOrder(),
			_objectDefinition.getPanelCategoryKey(),
			_objectDefinition.isPortlet(),
			_objectDefinition.getPluralLabelMap(), _objectDefinition.getScope(),
			_objectDefinition.getStatus());

		_objectEntryLocalService.deleteObjectEntry(objectEntry4);

		_assertCount(0);

		// Delete object entry with an object definition that is related to a
		// draft object definition

		ObjectDefinition draftObjectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				false,
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())));

		ObjectDefinition publishedObjectDefinition =
			_publishCustomObjectDefinition(
				false,
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())));

		ObjectEntry objectEntry5 = _addObjectEntry(
			0, publishedObjectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "bruce@liferay.com"
			).put(
				"firstName", "Bruce"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey4"
			).build());

		_objectRelationshipLocalService.addObjectRelationship(
			null, TestPropsValues.getUserId(),
			publishedObjectDefinition.getObjectDefinitionId(),
			draftObjectDefinition.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			StringUtil.randomId(), false,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		_objectEntryLocalService.deleteObjectEntry(objectEntry5);

		_assertCount(0);

		_objectDefinitionLocalService.deleteObjectDefinition(
			draftObjectDefinition);
		_objectDefinitionLocalService.deleteObjectDefinition(
			publishedObjectDefinition);
	}

	@Test
	public void testDeleteObjectEntryWithObjectDefinitionTree()
		throws Exception {

		TreeTestUtil.createObjectDefinitionTree(
			_objectDefinitionLocalService, _objectRelationshipLocalService,
			true,
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

		ObjectDefinition rootObjectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_A");

		Tree tree = TreeTestUtil.createObjectEntryTree(
			"1", _objectDefinitionLocalService, _objectEntryLocalService,
			_objectFieldLocalService, _objectRelationshipLocalService,
			rootObjectDefinition.getObjectDefinitionId());

		ObjectEntry rootObjectEntry = _objectEntryLocalService.getObjectEntry(
			"A1", rootObjectDefinition.getObjectDefinitionId());

		_objectEntryLocalService.deleteObjectEntry(
			rootObjectEntry.getObjectEntryId());

		Iterator<Node> iterator = tree.iterator();

		while (iterator.hasNext()) {
			Node node = iterator.next();

			AssertUtils.assertFailure(
				NoSuchObjectEntryException.class,
				"No ObjectEntry exists with the primary key " +
					node.getPrimaryKey(),
				() -> _objectEntryLocalService.getObjectEntry(
					node.getPrimaryKey()));
		}

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AB", "C_AAA", "C_AAB"},
			_objectEntryLocalService, _objectRelationshipLocalService);
	}

	@Test
	public void testGetExtensionDynamicObjectDefinitionTableValues()
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				TestPropsValues.getCompanyId(), User.class.getName());

		ObjectField objectField1 = _addCustomObjectField(
			new LongIntegerObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"longField"
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).build());
		ObjectField objectField2 = _addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"textField"
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).required(
				true
			).build());

		User user = UserTestUtil.addUser();

		try {
			_objectEntryLocalService.
				addOrUpdateExtensionDynamicObjectDefinitionTableValues(
					TestPropsValues.getUserId(), objectDefinition,
					user.getUserId(), Collections.emptyMap(),
					ServiceContextTestUtil.getServiceContext());

			Assert.fail();
		}
		catch (ObjectEntryValuesException.Required objectEntryValuesException) {
			Assert.assertEquals(
				"No value was provided for required object field \"textField\"",
				objectEntryValuesException.getMessage());
		}

		Map<String, Serializable> values =
			HashMapBuilder.<String, Serializable>put(
				"longField", 10L
			).put(
				"textField", "Value"
			).build();

		_objectEntryLocalService.
			addOrUpdateExtensionDynamicObjectDefinitionTableValues(
				TestPropsValues.getUserId(), objectDefinition, user.getUserId(),
				values, ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			values,
			_objectEntryLocalService.
				getExtensionDynamicObjectDefinitionTableValues(
					objectDefinition, user.getUserId()));

		values = HashMapBuilder.<String, Serializable>put(
			"longField", 1000L
		).put(
			"textField", "New Value"
		).build();

		_objectEntryLocalService.
			addOrUpdateExtensionDynamicObjectDefinitionTableValues(
				TestPropsValues.getUserId(), objectDefinition, user.getUserId(),
				values, ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			values,
			_objectEntryLocalService.
				getExtensionDynamicObjectDefinitionTableValues(
					objectDefinition, user.getUserId()));

		_objectEntryLocalService.
			deleteExtensionDynamicObjectDefinitionTableValues(
				objectDefinition, user.getUserId());

		Map<String, Serializable> extensionValues =
			_objectEntryLocalService.
				getExtensionDynamicObjectDefinitionTableValues(
					objectDefinition, user.getUserId());

		Assert.assertEquals(0L, extensionValues.get("longField"));
		Assert.assertEquals(StringPool.BLANK, extensionValues.get("textField"));

		_objectFieldLocalService.deleteObjectField(
			objectField1.getObjectFieldId());
		_objectFieldLocalService.deleteObjectField(
			objectField2.getObjectFieldId());
	}

	@Test
	public void testGetObjectEntries() throws Exception {
		List<ObjectEntry> objectEntries =
			_objectEntryLocalService.getObjectEntries(
				0, _objectDefinition.getObjectDefinitionId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		Assert.assertEquals(objectEntries.toString(), 0, objectEntries.size());

		_assertCount(0);

		// Add first object entry

		Map<String, Serializable> values1 =
			HashMapBuilder.<String, Serializable>put(
				"emailAddressDomain", "@liferay.com"
			).put(
				"emailAddressRequired", "peter@liferay.com"
			).put(
				"firstName", "Peter"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build();

		_addObjectEntry(values1);

		objectEntries = _objectEntryLocalService.getObjectEntries(
			0, _objectDefinition.getObjectDefinitionId(), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		Assert.assertEquals(objectEntries.toString(), 1, objectEntries.size());

		_assertCount(1);

		_assertObjectEntryValues(
			25, values1, _getValuesFromDatabase(objectEntries.get(0)));

		// Add second object entry

		Map<String, Serializable> values2 =
			HashMapBuilder.<String, Serializable>put(
				"emailAddressDomain", "@liferay.com"
			).put(
				"emailAddressRequired", "james@liferay.com"
			).put(
				"firstName", "James"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey2"
			).build();

		_addObjectEntry(values2);

		objectEntries = _objectEntryLocalService.getObjectEntries(
			0, _objectDefinition.getObjectDefinitionId(), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		Assert.assertEquals(objectEntries.toString(), 2, objectEntries.size());

		_assertCount(2);

		_assertObjectEntryValues(
			25, values1, _getValuesFromDatabase(objectEntries.get(0)));
		_assertObjectEntryValues(
			25, values2, _getValuesFromDatabase(objectEntries.get(1)));

		// Add third object entry

		Map<String, Serializable> values3 =
			HashMapBuilder.<String, Serializable>put(
				"emailAddressDomain", "@liferay.com"
			).put(
				"emailAddressRequired", "john@liferay.com"
			).put(
				"firstName", "John"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey3"
			).build();

		_addObjectEntry(values3);

		objectEntries = _objectEntryLocalService.getObjectEntries(
			0, _objectDefinition.getObjectDefinitionId(), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		Assert.assertEquals(objectEntries.toString(), 3, objectEntries.size());

		_assertCount(3);

		_assertObjectEntryValues(
			25, values1, _getValuesFromDatabase(objectEntries.get(0)));
		_assertObjectEntryValues(
			25, values2, _getValuesFromDatabase(objectEntries.get(1)));
		_assertObjectEntryValues(
			25, values3, _getValuesFromDatabase(objectEntries.get(2)));

		// Irrelevant object definition

		objectEntries = _objectEntryLocalService.getObjectEntries(
			0, _irrelevantObjectDefinition.getObjectDefinitionId(),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(objectEntries.toString(), 0, objectEntries.size());
	}

	@Test
	public void testGetObjectEntry() throws Exception {
		ObjectEntry objectEntry = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "john@liferay.com"
			).put(
				"firstName", "John"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build());

		Map<String, Serializable> systemValues =
			_objectEntryLocalService.getSystemValues(objectEntry);

		Assert.assertEquals(
			LocalDateTimeUtil.toLocalDateTime(
				objectEntry.getCreateDate()
			).toLocalDate(),
			LocalDateTimeUtil.toLocalDateTime(
				(Date)systemValues.get("createDate")
			).toLocalDate());
		Assert.assertEquals(
			objectEntry.getExternalReferenceCode(),
			systemValues.get("externalReferenceCode"));
		Assert.assertEquals(
			LocalDateTimeUtil.toLocalDateTime(
				objectEntry.getModifiedDate()
			).toLocalDate(),
			LocalDateTimeUtil.toLocalDateTime(
				(Date)systemValues.get("modifiedDate")
			).toLocalDate());
		Assert.assertEquals(
			objectEntry.getObjectEntryId(), systemValues.get("objectEntryId"));
		Assert.assertEquals(
			objectEntry.getStatus(), systemValues.get("status"));
		Assert.assertEquals(
			objectEntry.getUserName(), systemValues.get("userName"));

		Assert.assertEquals(systemValues.toString(), 6, systemValues.size());

		Map<String, Serializable> values = _objectEntryLocalService.getValues(
			objectEntry.getObjectEntryId());

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			_objectRelationship.getObjectFieldId2());

		Assert.assertEquals(0L, values.get("ageOfDeath"));
		Assert.assertFalse((boolean)values.get("authorOfGospel"));
		Assert.assertEquals(null, values.get("birthday"));
		Assert.assertEquals(
			"john@liferay.com", values.get("emailAddressRequired"));
		Assert.assertEquals("John", values.get("firstName"));
		Assert.assertEquals(0D, values.get("height"));
		Assert.assertEquals(StringPool.BLANK, values.get("lastName"));
		Assert.assertEquals(StringPool.BLANK, values.get("middleName"));
		Assert.assertEquals(0, values.get("numberOfBooksWritten"));
		Assert.assertEquals(StringPool.BLANK, values.get("listTypeEntryKey"));
		Assert.assertEquals(
			"listTypeEntryKey1", values.get("listTypeEntryKeyRequired"));
		Assert.assertEquals(StringPool.BLANK, values.get("script"));
		Assert.assertEquals(_getBigDecimal(0L), values.get("speed"));
		Assert.assertEquals("listTypeEntryKey1", values.get("state"));
		Assert.assertEquals(null, values.get("time"));
		Assert.assertEquals(0D, values.get("weight"));
		Assert.assertEquals(0L, values.get(objectField.getName()));
		Assert.assertEquals(
			"",
			values.get(
				ObjectFieldSettingUtil.getValue(
					ObjectFieldSettingConstants.
						NAME_OBJECT_RELATIONSHIP_ERC_OBJECT_FIELD_NAME,
					objectField)));
		Assert.assertEquals(
			objectEntry.getObjectEntryId(),
			values.get(_objectDefinition.getPKObjectFieldName()));
		Assert.assertEquals(values.toString(), 25, values.size());

		AssertUtils.assertFailure(
			NoSuchObjectEntryException.class,
			"No ObjectEntry exists with the primary key 0",
			() -> _objectEntryLocalService.getValues(0));
	}

	@Test
	public void testGetValuesList() throws Exception {
		Sort[] sorts = {new Sort("id", false)};

		List<Map<String, Serializable>> valuesList =
			_objectEntryLocalService.getValuesList(
				0, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				_objectDefinition.getObjectDefinitionId(), null, null,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, sorts);

		Assert.assertEquals(valuesList.toString(), 0, valuesList.size());

		_assertCount(0);

		// Add first object entry

		Map<String, Serializable> values1 =
			HashMapBuilder.<String, Serializable>put(
				"emailAddressDomain", "@liferay.com"
			).put(
				"emailAddressRequired", "peter@liferay.com"
			).put(
				"firstName", "Peter"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build();

		_addObjectEntry(values1);

		valuesList = _objectEntryLocalService.getValuesList(
			0, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), null, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, sorts);

		Assert.assertEquals(valuesList.toString(), 1, valuesList.size());

		_assertCount(1);

		_assertObjectEntryValues(25, values1, valuesList.get(0));

		// Add second object entry

		Map<String, Serializable> values2 =
			HashMapBuilder.<String, Serializable>put(
				"emailAddressDomain", "@liferay.com"
			).put(
				"emailAddressRequired", "james@liferay.com"
			).put(
				"firstName", "James"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey2"
			).build();

		_addObjectEntry(values2);

		valuesList = _objectEntryLocalService.getValuesList(
			0, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), null, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, sorts);

		Assert.assertEquals(valuesList.toString(), 2, valuesList.size());

		_assertCount(2);

		_assertObjectEntryValues(25, values1, valuesList.get(0));
		_assertObjectEntryValues(25, values2, valuesList.get(1));

		// Add third object entry

		Map<String, Serializable> values3 =
			HashMapBuilder.<String, Serializable>put(
				"emailAddressDomain", "@liferay.com"
			).put(
				"emailAddressRequired", "john@liferay.com"
			).put(
				"firstName", "John"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey3"
			).build();

		ObjectEntry objectEntry = _addObjectEntry(values3);

		valuesList = _objectEntryLocalService.getValuesList(
			0, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), null, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, sorts);

		Assert.assertEquals(valuesList.toString(), 3, valuesList.size());

		_assertCount(3);

		_assertObjectEntryValues(25, values1, valuesList.get(0));
		_assertObjectEntryValues(25, values2, valuesList.get(1));
		_assertObjectEntryValues(25, values3, valuesList.get(2));

		// Irrelevant object definition

		valuesList = _objectEntryLocalService.getValuesList(
			0, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			_irrelevantObjectDefinition.getObjectDefinitionId(), null, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, sorts);

		Assert.assertEquals(valuesList.toString(), 0, valuesList.size());

		// Permissions check

		User user = UserTestUtil.addUser();

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		String originalName = PrincipalThreadLocal.getName();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		PrincipalThreadLocal.setName(user.getUserId());

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), _objectDefinition.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntry.getObjectEntryId()), role.getRoleId(),
			new String[] {ActionKeys.VIEW});

		_userLocalService.addRoleUser(role.getRoleId(), user);

		valuesList = _objectEntryLocalService.getValuesList(
			0, TestPropsValues.getCompanyId(), user.getUserId(),
			_objectDefinition.getObjectDefinitionId(), null, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, sorts);

		Assert.assertEquals(valuesList.toString(), 1, valuesList.size());

		_assertObjectEntryValues(25, values3, valuesList.get(0));

		PermissionThreadLocal.setPermissionChecker(originalPermissionChecker);
		PrincipalThreadLocal.setName(originalName);

		// Predicate

		Column<?, Object> firstNameColumn =
			(Column<?, Object>)_objectFieldLocalService.getColumn(
				_objectDefinition.getObjectDefinitionId(), "firstName");

		Predicate predicate = firstNameColumn.eq(
			"Peter"
		).or(
			firstNameColumn.eq("John")
		);

		valuesList = _objectEntryLocalService.getValuesList(
			0, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), predicate, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, sorts);

		Assert.assertEquals(valuesList.toString(), 2, valuesList.size());

		_assertObjectEntryValues(25, values1, valuesList.get(0));
		_assertObjectEntryValues(25, values3, valuesList.get(1));

		// Predicate and search

		String search = "John";

		valuesList = _objectEntryLocalService.getValuesList(
			0, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), predicate, search,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, sorts);

		Assert.assertEquals(valuesList.toString(), 1, valuesList.size());

		_assertObjectEntryValues(25, values3, valuesList.get(0));

		valuesList = _objectEntryLocalService.getValuesList(
			0, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), predicate,
			StringUtil.toLowerCase(search), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, sorts);

		Assert.assertEquals(valuesList.toString(), 1, valuesList.size());

		_assertObjectEntryValues(25, values3, valuesList.get(0));

		valuesList = _objectEntryLocalService.getValuesList(
			0, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), predicate,
			StringUtil.toUpperCase(search), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, sorts);

		Assert.assertEquals(valuesList.toString(), 1, valuesList.size());

		_assertObjectEntryValues(25, values3, valuesList.get(0));

		valuesList = _objectEntryLocalService.getValuesList(
			0, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), predicate,
			RandomTestUtil.randomString(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			sorts);

		Assert.assertEquals(valuesList.toString(), 0, valuesList.size());

		// Predicate with permissions check

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		PrincipalThreadLocal.setName(user.getUserId());

		valuesList = _objectEntryLocalService.getValuesList(
			0, TestPropsValues.getCompanyId(), user.getUserId(),
			_objectDefinition.getObjectDefinitionId(), predicate, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, sorts);

		Assert.assertEquals(valuesList.toString(), 1, valuesList.size());

		_assertObjectEntryValues(25, values3, valuesList.get(0));

		PermissionThreadLocal.setPermissionChecker(originalPermissionChecker);
		PrincipalThreadLocal.setName(originalName);

		// Selected object field names

		String[] selectedObjectFieldNames = {
			"emailAddressRequired", "firstName"
		};

		valuesList = _objectEntryLocalService.getValuesList(
			0, TestPropsValues.getCompanyId(), user.getUserId(),
			_objectDefinition.getObjectDefinitionId(), null, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, sorts);

		Assert.assertEquals(valuesList.toString(), 3, valuesList.size());

		_assertObjectEntryValues(
			25, values1, valuesList.get(0), selectedObjectFieldNames);
		_assertObjectEntryValues(
			25, values2, valuesList.get(1), selectedObjectFieldNames);
		_assertObjectEntryValues(
			25, values3, valuesList.get(2), selectedObjectFieldNames);
	}

	@Test
	public void testScope() throws Exception {

		// Scope by company

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_testScope(0, ObjectDefinitionConstants.SCOPE_COMPANY, true);
		_testScope(
			depotEntry.getGroupId(), ObjectDefinitionConstants.SCOPE_COMPANY,
			false);
		_testScope(
			TestPropsValues.getGroupId(),
			ObjectDefinitionConstants.SCOPE_COMPANY, false);

		// Scope by depot

		// TODO Turn on theses tests once depot is reenabled

		/*_testScope(0, ObjectDefinitionConstants.SCOPE_DEPOT, false);
		_testScope(
			depotEntryGroupId, ObjectDefinitionConstants.SCOPE_DEPOT, true);
		_testScope(siteGroupId, ObjectDefinitionConstants.SCOPE_DEPOT, false);*/

		// Scope by site

		_testScope(0, ObjectDefinitionConstants.SCOPE_SITE, false);
		_testScope(
			depotEntry.getGroupId(), ObjectDefinitionConstants.SCOPE_SITE,
			false);
		_testScope(
			TestPropsValues.getGroupId(), ObjectDefinitionConstants.SCOPE_SITE,
			true);
	}

	@Test
	public void testSearchObjectEntries() throws Exception {

		// Without keywords

		BaseModelSearchResult<ObjectEntry> baseModelSearchResult =
			_objectEntryLocalService.searchObjectEntries(
				0, _objectDefinition.getObjectDefinitionId(), null, 0, 20);

		Assert.assertEquals(0, baseModelSearchResult.getLength());

		// Add first object entry

		Map<String, Serializable> values1 =
			HashMapBuilder.<String, Serializable>put(
				"emailAddressDomain", "@liferay.com"
			).put(
				"emailAddressRequired", "peter@liferay.com"
			).put(
				"firstName", "Peter"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build();

		_addObjectEntry(values1);

		baseModelSearchResult = _objectEntryLocalService.searchObjectEntries(
			0, _objectDefinition.getObjectDefinitionId(), null, 0, 20);

		Assert.assertEquals(1, baseModelSearchResult.getLength());

		List<ObjectEntry> objectEntries = baseModelSearchResult.getBaseModels();

		_assertObjectEntryValues(
			25, values1, _getValuesFromDatabase(objectEntries.get(0)));

		// Add second object entry

		Map<String, Serializable> values2 =
			HashMapBuilder.<String, Serializable>put(
				"emailAddressDomain", "@liferay.com"
			).put(
				"emailAddressRequired", "james@liferay.com"
			).put(
				"firstName", "James"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey2"
			).build();

		_addObjectEntry(values2);

		baseModelSearchResult = _objectEntryLocalService.searchObjectEntries(
			0, _objectDefinition.getObjectDefinitionId(), null, 0, 20);

		Assert.assertEquals(2, baseModelSearchResult.getLength());

		objectEntries = baseModelSearchResult.getBaseModels();

		_assertObjectEntryValues(
			25, values1, _getValuesFromDatabase(objectEntries.get(0)));
		_assertObjectEntryValues(
			25, values2, _getValuesFromDatabase(objectEntries.get(1)));

		// Add third object entry

		Map<String, Serializable> values3 =
			HashMapBuilder.<String, Serializable>put(
				"emailAddressDomain", "@liferay.com"
			).put(
				"emailAddressRequired", "john@liferay.com"
			).put(
				"firstName", "John"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey3"
			).build();

		_addObjectEntry(values3);

		baseModelSearchResult = _objectEntryLocalService.searchObjectEntries(
			0, _objectDefinition.getObjectDefinitionId(), null, 0, 20);

		Assert.assertEquals(3, baseModelSearchResult.getLength());

		objectEntries = baseModelSearchResult.getBaseModels();

		_assertObjectEntryValues(
			25, values1, _getValuesFromDatabase(objectEntries.get(0)));
		_assertObjectEntryValues(
			25, values2, _getValuesFromDatabase(objectEntries.get(1)));
		_assertObjectEntryValues(
			25, values3, _getValuesFromDatabase(objectEntries.get(2)));

		// With keywords

		_assertKeywords("@ liferay.com", 3);
		_assertKeywords("@-liferay.com", 0);
		_assertKeywords("@life", 3);
		_assertKeywords("@liferay", 3);
		_assertKeywords("@liferay.com", 3);
		_assertKeywords("Peter", 1);
		_assertKeywords("j0hn", 0);
		_assertKeywords("john", 1);
		_assertKeywords("life", 0);
		_assertKeywords("liferay", 0);
		_assertKeywords("liferay.com", 0);
		_assertKeywords("listTypeEntryKey1", 1);
		_assertKeywords("listTypeEntryKey2", 1);
		_assertKeywords("listTypeEntryKey3", 1);
		_assertKeywords("peter", 1);

		// Irrelevant object definition

		baseModelSearchResult = _objectEntryLocalService.searchObjectEntries(
			0, _irrelevantObjectDefinition.getObjectDefinitionId(), null, 0,
			20);

		Assert.assertEquals(0, baseModelSearchResult.getLength());
	}

	@Test
	public void testUpdateAsset() throws Exception {
		ObjectField objectField = _objectFieldLocalService.getObjectField(
			_objectDefinition.getObjectDefinitionId(), "emailAddressRequired");

		_objectDefinitionLocalService.updateTitleObjectFieldId(
			_objectDefinition.getObjectDefinitionId(),
			objectField.getObjectFieldId());

		ObjectEntry objectEntry = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "john@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build());

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			_objectDefinition.getClassName(), objectEntry.getObjectEntryId());

		Assert.assertEquals("john@liferay.com", assetEntry.getTitle());

		objectField = _addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"textLocalized"
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).localized(
				true
			).build());

		_objectDefinitionLocalService.updateTitleObjectFieldId(
			_objectDefinition.getObjectDefinitionId(),
			objectField.getObjectFieldId());

		String value1 = "en_US " + RandomTestUtil.randomString();
		String value2 = "pt_BR " + RandomTestUtil.randomString();

		objectEntry = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "john@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).put(
				"textLocalized_i18n",
				HashMapBuilder.put(
					"en_US", value1
				).put(
					"pt_BR", value2
				).build()
			).build());

		assetEntry = _assetEntryLocalService.fetchEntry(
			_objectDefinition.getClassName(), objectEntry.getObjectEntryId());

		Assert.assertEquals(
			LocalizationUtil.getXml(
				HashMapBuilder.put(
					"en_US", value1
				).put(
					"pt_BR", value2
				).build(),
				objectField.getDefaultLanguageId(), "title"),
			assetEntry.getTitle());
	}

	@Test
	public void testUpdateObjectEntry() throws Exception {
		_assertCount(0);

		ObjectEntry objectEntry = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "john@liferay.com"
			).put(
				"firstName", "John"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).put(
				"multipleListTypeEntriesKey",
				(Serializable)Arrays.asList(
					"multipleListTypeEntryKey1", "multipleListTypeEntryKey2")
			).build());

		_assertCount(1);

		Assert.assertNotNull(
			ReflectionTestUtil.getFieldValue(objectEntry, "_values"));

		_getValuesFromCacheField(objectEntry);

		//Assert.assertNotNull(
		//	ReflectionTestUtil.getFieldValue(objectEntry, "_values"));

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			HashMapBuilder.putAll(
				_objectEntryLocalService.getValues(objectEntry)
			).put(
				"firstName", "João"
			).put(
				"lastName", "o Discípulo Amado"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey2"
			).put(
				"multipleListTypeEntriesKey",
				(Serializable)Arrays.asList(
					"multipleListTypeEntryKey3", "multipleListTypeEntryKey4")
			).put(
				"state", "listTypeEntryKey1"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_assertCount(1);

		objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntry.getObjectEntryId());

		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(objectEntry, "_values"));

		Map<String, Serializable> values = objectEntry.getValues();

		Assert.assertEquals(_getValuesFromCacheField(objectEntry), values);
		Assert.assertEquals(0L, values.get("ageOfDeath"));
		Assert.assertFalse((boolean)values.get("authorOfGospel"));
		Assert.assertEquals(null, values.get("birthday"));
		Assert.assertEquals(
			"john@liferay.com", values.get("emailAddressRequired"));
		Assert.assertEquals("João", values.get("firstName"));
		Assert.assertEquals(0D, values.get("height"));
		Assert.assertEquals("o Discípulo Amado", values.get("lastName"));
		Assert.assertEquals(StringPool.BLANK, values.get("listTypeEntryKey"));
		Assert.assertEquals(
			"listTypeEntryKey2", values.get("listTypeEntryKeyRequired"));
		Assert.assertEquals(StringPool.BLANK, values.get("middleName"));
		Assert.assertEquals(
			"multipleListTypeEntryKey3, multipleListTypeEntryKey4",
			values.get("multipleListTypeEntriesKey"));
		Assert.assertEquals(0, values.get("numberOfBooksWritten"));
		Assert.assertEquals(StringPool.BLANK, values.get("script"));
		Assert.assertEquals(_getBigDecimal(0L), values.get("speed"));
		Assert.assertEquals("listTypeEntryKey1", values.get("state"));
		Assert.assertEquals(null, values.get("time"));
		Assert.assertEquals(0L, values.get("upload"));
		Assert.assertEquals(0D, values.get("weight"));
		Assert.assertEquals(
			objectEntry.getObjectEntryId(),
			values.get(_objectDefinition.getPKObjectFieldName()));
		Assert.assertEquals(values.toString(), 25, values.size());

		Calendar calendar = new GregorianCalendar();

		calendar.set(6, Calendar.DECEMBER, 28);
		calendar.setTimeInMillis(0);

		Date birthdayDate = calendar.getTime();

		String script = RandomTestUtil.randomString(1500);
		Timestamp timestamp = Timestamp.valueOf(
			LocalDateTime.now(
			).withNano(
				0
			));

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			HashMapBuilder.<String, Serializable>put(
				"ageOfDeath", "94"
			).put(
				"authorOfGospel", true
			).put(
				"birthday", birthdayDate
			).put(
				"height", 180
			).put(
				"listTypeEntryKey", "listTypeEntryKey1"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey3"
			).put(
				"multipleListTypeEntriesKey",
				(Serializable)Arrays.asList(
					"multipleListTypeEntryKey5", "multipleListTypeEntryKey6")
			).put(
				"numberOfBooksWritten", 5
			).put(
				"script", script
			).put(
				"speed", BigDecimal.valueOf(45L)
			).put(
				"state", "listTypeEntryKey2"
			).put(
				"time", timestamp
			).put(
				"upload", 0L
			).put(
				"weight", 60
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_assertCount(1);

		values = _objectEntryLocalService.getValues(
			objectEntry.getObjectEntryId());

		Assert.assertEquals(94L, values.get("ageOfDeath"));
		Assert.assertTrue((boolean)values.get("authorOfGospel"));
		Assert.assertEquals(birthdayDate, values.get("birthday"));
		Assert.assertEquals(
			"john@liferay.com", values.get("emailAddressRequired"));
		Assert.assertEquals("João", values.get("firstName"));
		Assert.assertEquals(180D, values.get("height"));
		Assert.assertEquals("o Discípulo Amado", values.get("lastName"));
		Assert.assertEquals(
			"listTypeEntryKey1", values.get("listTypeEntryKey"));
		Assert.assertEquals(
			"listTypeEntryKey3", values.get("listTypeEntryKeyRequired"));
		Assert.assertEquals(StringPool.BLANK, values.get("middleName"));
		Assert.assertEquals(
			"multipleListTypeEntryKey5, multipleListTypeEntryKey6",
			values.get("multipleListTypeEntriesKey"));
		Assert.assertEquals(5, values.get("numberOfBooksWritten"));
		Assert.assertEquals(script, values.get("script"));
		Assert.assertEquals(_getBigDecimal(45L), values.get("speed"));
		Assert.assertEquals("listTypeEntryKey2", values.get("state"));
		Assert.assertEquals(timestamp, values.get("time"));
		Assert.assertEquals(0L, values.get("upload"));
		Assert.assertEquals(60D, values.get("weight"));
		Assert.assertEquals(
			objectEntry.getObjectEntryId(),
			values.get(_objectDefinition.getPKObjectFieldName()));
		Assert.assertEquals(values.toString(), 25, values.size());

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			HashMapBuilder.<String, Serializable>put(
				"state", "listTypeEntryKey3"
			).put(
				"upload", 0L
			).put(
				"weight", 65D
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_assertCount(1);

		values = _objectEntryLocalService.getValues(
			objectEntry.getObjectEntryId());

		Assert.assertEquals(94L, values.get("ageOfDeath"));
		Assert.assertTrue((boolean)values.get("authorOfGospel"));
		Assert.assertEquals(birthdayDate, values.get("birthday"));
		Assert.assertEquals(
			"john@liferay.com", values.get("emailAddressRequired"));
		Assert.assertEquals("João", values.get("firstName"));
		Assert.assertEquals(180D, values.get("height"));
		Assert.assertEquals("o Discípulo Amado", values.get("lastName"));
		Assert.assertEquals(
			"listTypeEntryKey1", values.get("listTypeEntryKey"));
		Assert.assertEquals(
			"listTypeEntryKey3", values.get("listTypeEntryKeyRequired"));
		Assert.assertEquals(StringPool.BLANK, values.get("middleName"));
		Assert.assertEquals(
			"multipleListTypeEntryKey5, multipleListTypeEntryKey6",
			values.get("multipleListTypeEntriesKey"));
		Assert.assertEquals(5, values.get("numberOfBooksWritten"));
		Assert.assertEquals(script, values.get("script"));
		Assert.assertEquals(_getBigDecimal(45L), values.get("speed"));
		Assert.assertEquals("listTypeEntryKey3", values.get("state"));
		Assert.assertEquals(timestamp, values.get("time"));
		Assert.assertEquals(0L, values.get("upload"));
		Assert.assertEquals(65D, values.get("weight"));
		Assert.assertEquals(
			objectEntry.getObjectEntryId(),
			values.get(_objectDefinition.getPKObjectFieldName()));
		Assert.assertEquals(values.toString(), 25, values.size());

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			HashMapBuilder.<String, Serializable>put(
				"ageOfDeath", StringPool.BLANK
			).put(
				"authorOfGospel", StringPool.BLANK
			).put(
				"birthday", StringPool.BLANK
			).put(
				"firstName", StringPool.BLANK
			).put(
				"time", StringPool.BLANK
			).build(),
			ServiceContextTestUtil.getServiceContext());

		values = _objectEntryLocalService.getValues(
			objectEntry.getObjectEntryId());

		Assert.assertEquals(0L, values.get("ageOfDeath"));
		Assert.assertFalse((boolean)values.get("authorOfGospel"));
		Assert.assertNull(values.get("birthday"));
		Assert.assertEquals(StringPool.BLANK, values.get("firstName"));
		Assert.assertNull(values.get("time"));

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			new HashMap<String, Serializable>(),
			ServiceContextTestUtil.getServiceContext());

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			HashMapBuilder.<String, Serializable>put(
				_objectDefinition.getPKObjectFieldName(), ""
			).put(
				"invalidName", ""
			).build(),
			ServiceContextTestUtil.getServiceContext());

		long objectEntryId = objectEntry.getObjectEntryId();

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsIntegerSize.class,
			"Object entry value exceeds integer field allowed size",
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId,
				HashMapBuilder.<String, Serializable>put(
					"numberOfBooksWritten", "2147483648"
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsIntegerSize.class,
			"Object entry value exceeds integer field allowed size",
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId,
				HashMapBuilder.<String, Serializable>put(
					"numberOfBooksWritten", "-2147483649"
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsLongMaxSize.class,
			"Object entry value exceeds maximum long field allowed size",
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId,
				HashMapBuilder.<String, Serializable>put(
					"ageOfDeath", "9007199254740992"
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsLongMinSize.class,
			"Object entry value falls below minimum long field allowed size",
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId,
				HashMapBuilder.<String, Serializable>put(
					"ageOfDeath", "-9007199254740992"
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsLongSize.class,
			"Object entry value exceeds long field allowed size",
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId,
				HashMapBuilder.<String, Serializable>put(
					"ageOfDeath", "9223372036854775808"
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsLongSize.class,
			"Object entry value exceeds long field allowed size",
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId,
				HashMapBuilder.<String, Serializable>put(
					"ageOfDeath", "-9223372036854775809"
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsTextMaxLength.class,
			"Object entry value exceeds the maximum length of 280 characters " +
				"for object field \"firstName\"",
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId,
				HashMapBuilder.<String, Serializable>put(
					"firstName", RandomTestUtil.randomString(281)
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddress", "james@liferay.com"
			).put(
				"emailAddressRequired", "james@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build());

		AssertUtils.assertFailure(
			ObjectEntryValuesException.UniqueValueConstraintViolation.class,
			"Unique value constraint violation for " +
				_objectDefinition.getDBTableName() +
					".emailAddress_ with value james@liferay.com",
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId,
				HashMapBuilder.<String, Serializable>put(
					"emailAddress", "james@liferay.com"
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		_testUpdateObjectEntryExternalReferenceCode();
		_testUpdateObjectEntryObjectStateTransitions();
	}

	@Test
	public void testUpdateObjectEntryWithJavaDelegateObjectValidationRule()
		throws Exception {

		BiFunction<Map<String, Object>, String, String> biFunction =
			(inputObjects, sourceName) -> {
				Map<String, Object> entryDTO =
					(Map<String, Object>)inputObjects.get(sourceName);

				Map<String, Object> entryValues =
					(Map<String, Object>)entryDTO.get("properties");

				return GetterUtil.getString(
					entryValues.get("emailAddressRequired"));
			};

		Consumer<Map<String, Object>> consumer = inputObjects -> {
			Assert.assertEquals(
				"john@liferay.com", biFunction.apply(inputObjects, "entryDTO"));
			Assert.assertEquals(
				"bob@liferay.com",
				biFunction.apply(inputObjects, "originalEntryDTO"));
		};

		String key =
			ObjectValidationRuleConstants.ENGINE_TYPE_JAVA_DELEGATE_PREFIX +
				RandomTestUtil.randomString();

		try (Closeable closeable = _registerTestObjectValidationRuleEngine(
				consumer, key)) {

			ObjectEntry objectEntry = _addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"emailAddressRequired", "bob@liferay.com"
				).put(
					"listTypeEntryKeyRequired", "listTypeEntryKey1"
				).build());

			ObjectValidationRule objectValidationRule =
				_addObjectValidationRule(
					key,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					"");

			_objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
				HashMapBuilder.<String, Serializable>put(
					"emailAddressRequired", "john@liferay.com"
				).put(
					"listTypeEntryKeyRequired", "listTypeEntryKey1"
				).build(),
				ServiceContextTestUtil.getServiceContext());

			_objectValidationRuleLocalService.deleteObjectValidationRule(
				objectValidationRule);
		}
	}

	@Test
	public void testUpdateStatus() throws Exception {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

			_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
				TestPropsValues.getUserId(), TestPropsValues.getCompanyId(), 0,
				_objectDefinition.getClassName(), 0, 0, "Single Approver", 1);

			ObjectEntry objectEntry = _addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"emailAddressRequired", "peter@liferay.com"
				).put(
					"firstName", "Peter"
				).put(
					"listTypeEntryKeyRequired", "listTypeEntryKey1"
				).build());

			Assert.assertEquals(
				WorkflowConstants.STATUS_PENDING, objectEntry.getStatus());

			List<WorkflowTask> workflowTasks =
				_workflowTaskManager.getWorkflowTasksBySubmittingUser(
					TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
					false, 0, 1, null);

			WorkflowTask workflowTask = workflowTasks.get(0);

			_workflowTaskManager.assignWorkflowTaskToUser(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				workflowTask.getWorkflowTaskId(), TestPropsValues.getUserId(),
				StringPool.BLANK, null, null);

			_workflowTaskManager.completeWorkflowTask(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				workflowTask.getWorkflowTaskId(), Constants.APPROVE,
				StringPool.BLANK, null);

			objectEntry = _objectEntryLocalService.getObjectEntry(
				objectEntry.getObjectEntryId());

			Assert.assertEquals(
				WorkflowConstants.STATUS_APPROVED, objectEntry.getStatus());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	@Test
	public void testUpdateSystemObjectEntryWithDDMObjectValidationRule()
		throws Exception {

		User user = UserTestUtil.addUser();

		String emailAddress = user.getEmailAddress();

		ObjectDefinition userObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				TestPropsValues.getCompanyId(), User.class.getName());

		ObjectValidationRule objectValidationRule =
			_objectValidationRuleLocalService.addObjectValidationRule(
				StringPool.BLANK, TestPropsValues.getUserId(),
				userObjectDefinition.getObjectDefinitionId(), true,
				ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
				"oldValue(\"emailAddress\") == \"" + emailAddress + "\"", false,
				Collections.emptyList());

		user.setEmailAddress(RandomTestUtil.randomString());

		user = _userLocalService.updateUser(user);

		try {
			user.setEmailAddress(RandomTestUtil.randomString());

			_clearValidatedObjectEntryIds();

			_userLocalService.updateUser(user);

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			_assertFailureObjectValidationRule(
				modelListenerException, objectValidationRule);
		}
		finally {
			_objectValidationRuleLocalService.deleteObjectValidationRule(
				objectValidationRule);
		}
	}

	@Test
	public void testUpdateSystemObjectEntryWithJavaDelegateObjectValidationRule()
		throws Exception {

		Consumer<Map<String, Object>> consumer = inputObjects -> {
			Map<String, Object> entryDTO =
				(Map<String, Object>)inputObjects.get("entryDTO");

			List<Map<String, Object>> customFields =
				(List<Map<String, Object>>)entryDTO.get("customFields");

			Map<String, Object> customField = customFields.get(0);

			Assert.assertEquals("customFieldName", customField.get("name"));

			Map<String, Object> customValue =
				(Map<String, Object>)customField.get("customValue");

			Assert.assertEquals("customFieldValue", customValue.get("data"));

			Assert.assertEquals(
				"textObjectFieldValue", entryDTO.get("textObjectFieldName"));
		};

		String key =
			ObjectValidationRuleConstants.ENGINE_TYPE_JAVA_DELEGATE_PREFIX +
				RandomTestUtil.randomString();

		try (Closeable closeable = _registerTestObjectValidationRuleEngine(
				consumer, key)) {

			ExpandoTable expandoTable = ExpandoTestUtil.addTable(
				PortalUtil.getClassNameId(User.class),
				ExpandoTableConstants.DEFAULT_TABLE_NAME);

			ExpandoColumn expandoColumn = ExpandoTestUtil.addColumn(
				expandoTable, "customFieldName", ExpandoColumnConstants.STRING);

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext();

			serviceContext.setExpandoBridgeAttributes(
				HashMapBuilder.<String, Serializable>put(
					expandoColumn.getName(), "customFieldValue"
				).build());

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.fetchSystemObjectDefinition(
					TestPropsValues.getCompanyId(), "User");

			User user = UserTestUtil.addUser(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				RandomTestUtil.randomString(
					NumericStringRandomizerBumper.INSTANCE,
					UniqueStringRandomizerBumper.INSTANCE),
				serviceContext.getLocale(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				new long[] {serviceContext.getScopeGroupId()}, serviceContext);

			ObjectField objectField = _addCustomObjectField(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"textObjectFieldName"
				).objectDefinitionId(
					objectDefinition.getObjectDefinitionId()
				).build());

			_objectEntryLocalService.
				addOrUpdateExtensionDynamicObjectDefinitionTableValues(
					TestPropsValues.getUserId(), objectDefinition,
					user.getPrimaryKey(),
					HashMapBuilder.<String, Serializable>put(
						objectField.getName(), "textObjectFieldValue"
					).build(),
					serviceContext);

			ObjectValidationRule objectValidationRule =
				_objectValidationRuleLocalService.addObjectValidationRule(
					StringPool.BLANK, TestPropsValues.getUserId(),
					objectDefinition.getObjectDefinitionId(), true, key,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
					"", false, Collections.emptyList());

			UserTestUtil.updateUser(user);

			_objectValidationRuleLocalService.deleteObjectValidationRule(
				objectValidationRule);
		}
	}

	@Rule
	public TestName testName = new TestName();

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

	private ObjectEntry _addObjectEntry(
			long groupId, long objectDefinitionId,
			Map<String, Serializable> values)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			TestPropsValues.getUserId(), groupId, objectDefinitionId, values,
			ServiceContextTestUtil.getServiceContext());
	}

	private ObjectEntry _addObjectEntry(Map<String, Serializable> values)
		throws Exception {

		return _addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(), values);
	}

	private ObjectValidationRule _addObjectValidationRule(
			ObjectDefinition objectDefinition, String script)
		throws PortalException {

		return _objectValidationRuleLocalService.addObjectValidationRule(
			StringPool.BLANK, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), true,
			ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
			RandomTestUtil.randomLocaleStringMap(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION, script,
			false, Collections.emptyList());
	}

	private ObjectValidationRule _addObjectValidationRule(
			String engine, Map<Locale, String> errorLabelMap, String script)
		throws Exception {

		return _addObjectValidationRule(
			engine, errorLabelMap,
			ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION, script,
			Collections.emptyList());
	}

	private ObjectValidationRule _addObjectValidationRule(
			String engine, Map<Locale, String> errorLabelMap, String outputType,
			String script,
			List<ObjectValidationRuleSetting> objectValidationRuleSettings)
		throws Exception {

		return _objectValidationRuleLocalService.addObjectValidationRule(
			StringPool.BLANK, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), true, engine,
			errorLabelMap,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			outputType, script, false, objectValidationRuleSettings);
	}

	private ObjectEntry _addOrUpdateObjectEntry(
			String externalReferenceCode, long groupId,
			Map<String, Serializable> values)
		throws Exception {

		return _objectEntryLocalService.addOrUpdateObjectEntry(
			externalReferenceCode, TestPropsValues.getUserId(), groupId,
			_objectDefinition.getObjectDefinitionId(), values,
			ServiceContextTestUtil.getServiceContext());
	}

	private void _addSystemObjectField(ObjectField objectField)
		throws Exception {

		_objectFieldLocalService.addSystemObjectField(
			objectField.getExternalReferenceCode(), TestPropsValues.getUserId(),
			objectField.getListTypeDefinitionId(),
			objectField.getObjectDefinitionId(), objectField.getBusinessType(),
			null, null, objectField.getDBType(), objectField.isIndexed(),
			objectField.isIndexedAsKeyword(),
			objectField.getIndexedLanguageId(), objectField.getLabelMap(),
			objectField.isLocalized(), objectField.getName(),
			objectField.getReadOnly(),
			objectField.getReadOnlyConditionExpression(),
			objectField.isRequired(), objectField.isState(),
			objectField.getObjectFieldSettings());
	}

	private FileEntry _addTempFileEntry(String title) throws Exception {
		return TempFileEntryUtil.addTempFileEntry(
			TestPropsValues.getGroupId(), TestPropsValues.getUserId(),
			_objectDefinition.getPortletId(),
			TempFileEntryUtil.getTempFileName(title + ".txt"),
			FileUtil.createTempFile(RandomTestUtil.randomBytes()),
			ContentTypes.TEXT_PLAIN);
	}

	private void _assertCount(int count) throws Exception {
		Assert.assertEquals(
			count,
			_assetEntryLocalService.getEntriesCount(
				new AssetEntryQuery() {
					{
						setClassName(_objectDefinition.getClassName());
						setVisible(null);
					}
				}));
		Assert.assertEquals(
			count,
			_objectEntryLocalService.getObjectEntriesCount(
				0, _objectDefinition.getObjectDefinitionId()));
		Assert.assertEquals(count, _count());
	}

	private void _assertFailureObjectValidationRule(
		ModelListenerException modelListenerException,
		ObjectValidationRule objectValidationRule) {

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

		_assertObjectValidationRuleResult(
			objectValidationRule.getErrorLabel(LocaleUtil.getDefault()), null,
			objectValidationRuleResults.get(0));
	}

	private void _assertFriendlyURLEntriesSize(
			int expectedSize, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry)
		throws Exception {

		long classNameId = _classNameLocalService.getClassNameId(
			objectDefinition.getClassName());

		List<FriendlyURLEntry> friendlyURLEntries =
			_friendlyURLEntryLocalService.getFriendlyURLEntries(
				objectEntry.getNonzeroGroupId(), classNameId,
				objectEntry.getObjectEntryId());

		Assert.assertEquals(
			friendlyURLEntries.toString(), expectedSize,
			friendlyURLEntries.size());

		if (expectedSize == 0) {
			return;
		}

		_objectEntryLocalService.deleteObjectEntry(objectEntry);

		friendlyURLEntries =
			_friendlyURLEntryLocalService.getFriendlyURLEntries(
				objectEntry.getNonzeroGroupId(), classNameId,
				objectEntry.getObjectEntryId());

		Assert.assertEquals(
			friendlyURLEntries.toString(), 0, friendlyURLEntries.size());
	}

	private void _assertKeywords(String keywords, int count) throws Exception {
		BaseModelSearchResult<ObjectEntry> baseModelSearchResult =
			_objectEntryLocalService.searchObjectEntries(
				0, _objectDefinition.getObjectDefinitionId(), keywords, 0, 20);

		Assert.assertEquals(count, baseModelSearchResult.getLength());
	}

	private void _assertObjectEntryLocalizedValues(
			Map<String, Serializable> expectedLocalizedValues,
			ObjectEntry objectEntry, ObjectField objectField)
		throws Exception {

		Map<String, Serializable> actualLocalizedValues =
			_objectEntryLocalService.getValues(objectEntry.getObjectEntryId());

		Assert.assertEquals(
			expectedLocalizedValues.get(objectField.getI18nObjectFieldName()),
			actualLocalizedValues.get(objectField.getI18nObjectFieldName()));
	}

	private void _assertObjectEntryValues(
		int expectedValuesSize, Map<String, Serializable> expectedValues,
		Map<String, Serializable> actualValues) {

		Assert.assertEquals(
			expectedValues.get("emailAddressDomain"),
			actualValues.get("emailAddressDomain"));
		Assert.assertEquals(
			expectedValues.get("emailAddressRequired"),
			actualValues.get("emailAddressRequired"));
		Assert.assertEquals(
			expectedValues.get("firstName"), actualValues.get("firstName"));
		Assert.assertEquals(
			expectedValues.get("listTypeEntryKeyRequired"),
			actualValues.get("listTypeEntryKeyRequired"));

		Assert.assertEquals(
			actualValues.toString(), expectedValuesSize, actualValues.size());
	}

	private void _assertObjectEntryValues(
		int expectedValuesSize, Map<String, Serializable> expectedValues,
		Map<String, Serializable> actualValues,
		String[] selectedObjectFieldNames) {

		for (String selectedObjectFieldName : selectedObjectFieldNames) {
			Assert.assertEquals(
				expectedValues.get(selectedObjectFieldName),
				actualValues.get(selectedObjectFieldName));
		}

		Assert.assertEquals(
			actualValues.toString(), expectedValuesSize, actualValues.size());
	}

	private void _assertObjectValidationRuleResult(
		String expectedErrorMessage, String expectedObjectFieldName,
		ObjectValidationRuleResult objectValidationRuleResult) {

		Assert.assertEquals(
			expectedErrorMessage, objectValidationRuleResult.getErrorMessage());
		Assert.assertEquals(
			expectedObjectFieldName,
			objectValidationRuleResult.getObjectFieldName());
	}

	private void _assertURLTitleMap(
			Map<String, String> expectedURLTitleMap,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry)
		throws Exception {

		Map<String, String> actualURLTitleMap = new HashMap<>();

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_classNameLocalService.getClassNameId(
					objectDefinition.getClassName()),
				objectEntry.getObjectEntryId());

		for (FriendlyURLEntryLocalization friendlyURLEntryLocalization :
				_friendlyURLEntryLocalService.getFriendlyURLEntryLocalizations(
					friendlyURLEntry.getFriendlyURLEntryId())) {

			actualURLTitleMap.put(
				friendlyURLEntryLocalization.getLanguageId(),
				friendlyURLEntryLocalization.getUrlTitle());
		}

		AssertUtils.assertEquals(expectedURLTitleMap, actualURLTitleMap);
	}

	private void _clearValidatedObjectEntryIds() {
		ThreadLocal<Set<Long>> threadLocal = ReflectionTestUtil.getFieldValue(
			ObjectEntryThreadLocal.class, "_validatedObjectEntryIds");

		threadLocal.set(new HashSet<>());
	}

	private boolean _containsObjectEntryValuesSQLQuery(LogCapture logCapture) {
		List<LogEntry> logEntries = logCapture.getLogEntries();

		for (LogEntry logEntry : logEntries) {
			String message = logEntry.getMessage();

			if (message.startsWith("select") &&
				message.contains(_objectDefinition.getDBTableName()) &&
				message.contains(_objectDefinition.getExtensionDBTableName())) {

				return true;
			}
		}

		return false;
	}

	private int _count() throws Exception {
		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select count(*) from " + _objectDefinition.getDBTableName());
			ResultSet resultSet = preparedStatement.executeQuery()) {

			resultSet.next();

			return resultSet.getInt(1);
		}
	}

	private List<ListTypeEntry> _createListTypeEntries(
		String prefixKey, String prefixName, int size) {

		List<ListTypeEntry> listTypeEntries = new ArrayList<>();

		for (int i = 1; i <= size; i++) {
			listTypeEntries.add(
				ListTypeEntryUtil.createListTypeEntry(
					prefixKey + i,
					Collections.singletonMap(LocaleUtil.US, prefixName + i)));
		}

		return listTypeEntries;
	}

	private BigDecimal _getBigDecimal(long value) {
		return BigDecimalUtil.stripTrailingZeros(BigDecimal.valueOf(value));
	}

	private String _getNoSuchAlgorithmExceptionMessage() {
		if (JavaDetector.isJDK21()) {
			return ": Null or empty transformation";
		}

		return ": Invalid transformation format:";
	}

	private Map<String, Serializable> _getValuesFromCacheField(
			ObjectEntry objectEntry)
		throws Exception {

		Map<String, Serializable> values = null;

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.object.model.impl.ObjectEntryImpl",
				LoggerTestUtil.DEBUG)) {

			values = objectEntry.getValues();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				logEntry.getMessage(),
				"Use cached values for object entry " +
					objectEntry.getObjectEntryId());
		}

		return values;
	}

	private Map<String, Serializable> _getValuesFromDatabase(
			ObjectEntry objectEntry)
		throws Exception {

		Map<String, Serializable> values = null;

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.object.model.impl.ObjectEntryImpl",
				LoggerTestUtil.DEBUG)) {

			values = objectEntry.getValues();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				logEntry.getMessage(),
				"Get values for object entry " +
					objectEntry.getObjectEntryId());
		}

		return values;
	}

	private boolean _hasResourcePermission(
			ObjectAction objectAction, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry)
		throws Exception {

		Role role = _roleLocalService.getRole(
			objectEntry.getCompanyId(), RoleConstants.OWNER);

		return _resourcePermissionLocalService.hasResourcePermission(
			objectEntry.getCompanyId(), objectDefinition.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntry.getPrimaryKey()), role.getRoleId(),
			objectAction.getName());
	}

	private ObjectDefinition _publishCustomObjectDefinition(
			boolean enableLocalization, List<ObjectField> objectFields)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				enableLocalization, objectFields);

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

	private Closeable _registerTestObjectValidationRuleEngine(
			Consumer<Map<String, Object>> consumer, String key)
		throws PortalException {

		Bundle bundle = FrameworkUtil.getBundle(
			ObjectEntryLocalServiceTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<ObjectValidationRuleEngine> serviceRegistration =
			bundleContext.registerService(
				ObjectValidationRuleEngine.class,
				new TestObjectValidationRuleEngine(
					TestPropsValues.getCompanyId(),
					Collections.singletonList(_objectDefinition.getName()),
					consumer, key),
				null);

		return serviceRegistration::unregister;
	}

	private void _testAddObjectEntryAsDraft() throws Exception {
		_objectDefinition.setEnableObjectEntryDraft(true);

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);

		Map<String, Serializable> values1 =
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", RandomTestUtil.randomString()
			).put(
				"firstName", RandomTestUtil.randomString()
			).build();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			TestPropsValues.getUserId(), 0,
			_objectDefinition.getObjectDefinitionId(), values1, serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, objectEntry.getStatus());

		long objectEntryId1 = objectEntry.getObjectEntryId();

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		AssertUtils.assertFailure(
			ObjectEntryValuesException.Required.class,
			"No value was provided for required object field " +
				"\"listTypeEntryKeyRequired\"",
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId1, values1,
				serviceContext));

		Map<String, Serializable> values2 =
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", RandomTestUtil.randomString()
			).put(
				"firstName", RandomTestUtil.randomString()
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build();

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntryId1, values2,
			serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, objectEntry.getStatus());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		AssertUtils.assertFailure(
			ObjectEntryStatusException.class, "Draft status is not allowed",
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId1, values2,
				serviceContext));

		objectEntry = _objectEntryLocalService.addObjectEntry(
			TestPropsValues.getUserId(), 0,
			_objectDefinition.getObjectDefinitionId(), values2, serviceContext);

		_objectDefinition.setEnableObjectEntryDraft(false);

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);

		objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntry.getObjectEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, objectEntry.getStatus());

		long objectEntryId2 = objectEntry.getObjectEntryId();

		AssertUtils.assertFailure(
			ObjectEntryStatusException.class, "Draft status is not allowed",
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId2, values2,
				serviceContext));

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntryId2, values2,
			serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, objectEntry.getStatus());
	}

	private void _testAddObjectEntryWithLocalizedValues(
			ObjectDefinition objectDefinition, Group group)
		throws Exception {

		if (objectDefinition.isModifiableAndSystem()) {
			_addSystemObjectField(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).localized(
					true
				).name(
					"longTextLocalized1"
				).objectDefinitionId(
					objectDefinition.getObjectDefinitionId()
				).system(
					true
				).build());
		}

		_addCustomObjectField(
			new AttachmentObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).localized(
				true
			).name(
				"localizedAttachment"
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).objectFieldSettings(
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.
							NAME_ACCEPTED_FILE_EXTENSIONS
					).value(
						"png"
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
		_addCustomObjectField(
			new IntegerObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).localized(
				true
			).name(
				"integerLocalized"
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).build());
		_addCustomObjectField(
			new LongIntegerObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).localized(
				true
			).name(
				"longIntegerLocalized"
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).build());
		_addCustomObjectField(
			new LongTextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).localized(
				true
			).name(
				"longTextLocalized2"
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).build());
		_addCustomObjectField(
			new RichTextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).localized(
				true
			).name(
				"richTextLocalized"
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).build());
		_addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).localized(
				true
			).name(
				"textLocalized"
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).objectFieldSettings(
				Collections.singletonList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_UNIQUE_VALUES
					).value(
						Boolean.TRUE.toString()
					).build())
			).build());

		String value1 = "en_US " + RandomTestUtil.randomString();
		String value2 = "pt_BR " + RandomTestUtil.randomString();

		Map<String, Serializable> localizedValues = new HashMap<>();

		if (objectDefinition.isModifiableAndSystem()) {
			localizedValues.put(
				"longTextLocalized1_i18n",
				HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).put(
					"pt_BR", RandomTestUtil.randomString()
				).build());
		}

		localizedValues.put(
			"longTextLocalized2_i18n",
			HashMapBuilder.put(
				"en_US", RandomTestUtil.randomString()
			).put(
				"pt_BR", RandomTestUtil.randomString()
			).build());
		localizedValues.put(
			"richTextLocalized_i18n",
			HashMapBuilder.put(
				"en_US", RandomTestUtil.randomString()
			).put(
				"pt_BR", RandomTestUtil.randomString()
			).build());
		localizedValues.put(
			"textLocalized_i18n",
			HashMapBuilder.put(
				"en_US", value1
			).put(
				"pt_BR", value2
			).build());

		ObjectEntry objectEntry = _addObjectEntry(
			group.getGroupId(), objectDefinition.getObjectDefinitionId(),
			localizedValues);

		Map<String, Serializable> values = objectEntry.getValues();

		if (objectDefinition.isModifiableAndSystem()) {
			Assert.assertEquals(
				localizedValues.get("longTextLocalized1_i18n"),
				values.get("longTextLocalized1_i18n"));
		}

		Assert.assertEquals(
			localizedValues.get("longTextLocalized2_i18n"),
			values.get("longTextLocalized2_i18n"));
		Assert.assertEquals(
			localizedValues.get("richTextLocalized_i18n"),
			values.get("richTextLocalized_i18n"));
		Assert.assertEquals(
			localizedValues.get("textLocalized_i18n"),
			values.get("textLocalized_i18n"));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsIntegerSize.class,
			"Object entry value exceeds integer field allowed size",
			() -> _addObjectEntry(
				group.getGroupId(), objectDefinition.getObjectDefinitionId(),
				HashMapBuilder.<String, Serializable>put(
					"integerLocalized_i18n",
					HashMapBuilder.put(
						"en_US", "2147483648"
					).build()
				).build()));
		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsLongSize.class,
			"Object entry value exceeds long field allowed size",
			() -> _addObjectEntry(
				group.getGroupId(), objectDefinition.getObjectDefinitionId(),
				HashMapBuilder.<String, Serializable>put(
					"longIntegerLocalized_i18n",
					HashMapBuilder.put(
						"en_US", "9223372036854775808"
					).build()
				).build()));
		AssertUtils.assertFailure(
			ObjectEntryValuesException.InvalidFileExtension.class,
			"The file extension \"txt\" is invalid for object field " +
				"\"localizedAttachment\"",
			() -> _addObjectEntry(
				group.getGroupId(), objectDefinition.getObjectDefinitionId(),
				HashMapBuilder.<String, Serializable>put(
					"localizedAttachment_i18n",
					HashMapBuilder.put(
						"en_US",
						() -> {
							FileEntry fileEntry = _addTempFileEntry(
								RandomTestUtil.randomString());

							return fileEntry.getFileEntryId();
						}
					).build()
				).build()));
		AssertUtils.assertFailure(
			ObjectEntryValuesException.UniqueValueConstraintViolation.class,
			StringBundler.concat(
				"Unique value constraint violation for ",
				objectDefinition.getLocalizationDBTableName(),
				".textLocalized_ with value ", value1),
			() -> _addObjectEntry(
				group.getGroupId(), objectDefinition.getObjectDefinitionId(),
				localizedValues));
		AssertUtils.assertFailure(
			ObjectEntryValuesException.UniqueValueConstraintViolation.class,
			StringBundler.concat(
				"Unique value constraint violation for ",
				objectDefinition.getLocalizationDBTableName(),
				".textLocalized_ with value ", value2),
			() -> _addObjectEntry(
				group.getGroupId(), objectDefinition.getObjectDefinitionId(),
				HashMapBuilder.<String, Serializable>put(
					"textLocalized_i18n",
					HashMapBuilder.put(
						"en_US", "en_US " + RandomTestUtil.randomString()
					).put(
						"pt_BR", value2
					).build()
				).build()));
	}

	private void _testScope(long groupId, String scope, boolean expectSuccess)
		throws Exception {

		ObjectDefinition objectDefinition = _publishCustomObjectDefinition(
			false,
			Arrays.asList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING,
					RandomTestUtil.randomString(), StringUtil.randomId())));

		objectDefinition.setScope(scope);

		_objectDefinitionLocalService.updateObjectDefinition(objectDefinition);

		Assert.assertEquals(
			0,
			_objectEntryLocalService.getObjectEntriesCount(
				groupId, objectDefinition.getObjectDefinitionId()));

		BaseModelSearchResult<ObjectEntry> baseModelSearchResult =
			_objectEntryLocalService.searchObjectEntries(
				groupId, objectDefinition.getObjectDefinitionId(), null, 0, 20);

		Assert.assertEquals(0, baseModelSearchResult.getLength());

		if (!expectSuccess) {
			AssertUtils.assertFailure(
				ObjectDefinitionScopeException.class,
				StringBundler.concat(
					"Group ID ", groupId, " is not valid for scope \"", scope,
					"\""),
				() -> _objectEntryLocalService.addObjectEntry(
					TestPropsValues.getUserId(), groupId,
					objectDefinition.getObjectDefinitionId(),
					Collections.<String, Serializable>emptyMap(),
					ServiceContextTestUtil.getServiceContext()));
		}
		else {
			_objectEntryLocalService.addObjectEntry(
				TestPropsValues.getUserId(), groupId,
				objectDefinition.getObjectDefinitionId(),
				Collections.<String, Serializable>emptyMap(),
				ServiceContextTestUtil.getServiceContext());

			Assert.assertEquals(
				1,
				_objectEntryLocalService.getObjectEntriesCount(
					groupId, objectDefinition.getObjectDefinitionId()));

			baseModelSearchResult =
				_objectEntryLocalService.searchObjectEntries(
					groupId, objectDefinition.getObjectDefinitionId(), null, 0,
					20);

			Assert.assertEquals(1, baseModelSearchResult.getLength());
		}

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	private void _testUpdateObjectEntryExternalReferenceCode()
		throws Exception {

		ObjectEntry objectEntry1 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "john@liferay.com"
			).put(
				"firstName", "John"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build());

		Assert.assertEquals(
			objectEntry1.getUuid(), objectEntry1.getExternalReferenceCode());

		long objectEntryId1 = objectEntry1.getObjectEntryId();

		objectEntry1 = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntryId1,
			HashMapBuilder.<String, Serializable>put(
				"externalReferenceCode", "newExternalReferenceCode"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			"newExternalReferenceCode",
			objectEntry1.getExternalReferenceCode());

		ObjectEntry objectEntry2 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "matthew@liferay.com"
			).put(
				"firstName", "Matthew"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey2"
			).build());

		long objectEntryId2 = objectEntry2.getObjectEntryId();

		AssertUtils.assertFailure(
			DuplicateObjectEntryExternalReferenceCodeException.class,
			"Duplicate object entry with external reference code " +
				"newExternalReferenceCode and object definition ID " +
					_objectDefinition.getObjectDefinitionId(),
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId2,
				HashMapBuilder.<String, Serializable>put(
					"externalReferenceCode", "newExternalReferenceCode"
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		objectEntry2 = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntryId2,
			HashMapBuilder.<String, Serializable>put(
				"externalReferenceCode", ""
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			objectEntry2.getUuid(), objectEntry2.getExternalReferenceCode());

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntryId2,
			HashMapBuilder.<String, Serializable>put(
				"externalReferenceCode", objectEntry1.getUuid()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		AssertUtils.assertFailure(
			DuplicateObjectEntryExternalReferenceCodeException.class,
			StringBundler.concat(
				"Duplicate object entry with external reference code ",
				objectEntry1.getUuid(), " and object definition ID ",
				_objectDefinition.getObjectDefinitionId()),
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId1,
				HashMapBuilder.<String, Serializable>put(
					"externalReferenceCode", ""
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		String randomString = RandomTestUtil.randomString();

		objectEntry1 = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntryId1,
			HashMapBuilder.<String, Serializable>put(
				"externalReferenceCode", randomString
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			randomString, objectEntry1.getExternalReferenceCode());

		_objectEntryLocalService.deleteObjectEntry(objectEntryId1);
		_objectEntryLocalService.deleteObjectEntry(objectEntryId2);
	}

	private void _testUpdateObjectEntryObjectStateTransitions()
		throws Exception {

		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			_objectDefinition.getObjectDefinitionId(), "state");

		ObjectStateFlow objectStateFlow =
			_objectStateFlowLocalService.fetchObjectFieldObjectStateFlow(
				objectField.getObjectFieldId());

		List<ObjectState> objectStates =
			_objectStateLocalService.getObjectStateFlowObjectStates(
				objectStateFlow.getObjectStateFlowId());

		for (ObjectState objectState : objectStates) {
			List<ObjectStateTransition> objectStateTransitions =
				_objectStateTransitionLocalService.
					getObjectStateObjectStateTransitions(
						objectState.getObjectStateId());

			objectState.setObjectStateTransitions(
				Collections.singletonList(objectStateTransitions.get(0)));
		}

		objectStateFlow.setObjectStates(objectStates);

		_objectStateTransitionLocalService.updateObjectStateTransitions(
			objectStateFlow);

		ObjectEntry objectEntry = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "john@liferay.com"
			).put(
				"firstName", "John"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).put(
				"state", "listTypeEntryKey1"
			).build());

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			HashMapBuilder.<String, Serializable>put(
				"firstName", "Peter"
			).put(
				"state", "listTypeEntryKey1"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		ObjectState objectStateListTypeEntryKey1 = objectStates.get(0);
		ObjectState objectStateListTypeEntryKey2 = objectStates.get(1);
		ObjectState objectStateListTypeEntryKey3 = objectStates.get(2);

		long objectEntryId = objectEntry.getObjectEntryId();

		AssertUtils.assertFailure(
			ObjectEntryValuesException.InvalidObjectStateTransition.class,
			StringBundler.concat(
				"Object state ID ",
				objectStateListTypeEntryKey1.getObjectStateId(),
				" cannot be transitioned to object state ID ",
				objectStateListTypeEntryKey3.getObjectStateId()),
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId,
				HashMapBuilder.<String, Serializable>put(
					"state", "listTypeEntryKey3"
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			HashMapBuilder.<String, Serializable>put(
				"state", "listTypeEntryKey2"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		AssertUtils.assertFailure(
			ObjectEntryValuesException.InvalidObjectStateTransition.class,
			StringBundler.concat(
				"Object state ID ",
				objectStateListTypeEntryKey2.getObjectStateId(),
				" cannot be transitioned to object state ID ",
				objectStateListTypeEntryKey3.getObjectStateId()),
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId,
				HashMapBuilder.<String, Serializable>put(
					"state", "listTypeEntryKey3"
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		_objectEntryLocalService.deleteObjectEntry(
			objectEntry.getObjectEntryId());
	}

	private ObjectValidationRule _updateObjectValidationRule(
		ObjectValidationRule objectValidationRule, String script) {

		objectValidationRule.setScript(script);

		return _objectValidationRuleLocalService.updateObjectValidationRule(
			objectValidationRule);
	}

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CounterLocalService _counterLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _draftObjectDefinition;

	@Inject
	private Encryptor _encryptor;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _irrelevantObjectDefinition;

	@DeleteAfterTestRun
	private ListTypeDefinition _listTypeDefinition;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.object.internal.model.listener.ObjectEntryModelListener"
	)
	private ModelListener<ObjectEntry> _objectEntryModelListener;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	private ObjectRelationship _objectRelationship;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private ObjectStateFlowLocalService _objectStateFlowLocalService;

	@Inject
	private ObjectStateLocalService _objectStateLocalService;

	@Inject
	private ObjectStateTransitionLocalService
		_objectStateTransitionLocalService;

	@Inject
	private ObjectValidationRuleLocalService _objectValidationRuleLocalService;

	@Inject
	private ResourceActions _resourceActions;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowTaskManager _workflowTaskManager;

	private static class TestObjectValidationRuleEngine
		implements CompanyScoped, ObjectDefinitionScoped,
				   ObjectValidationRuleEngine {

		@Override
		public Map<String, Object> execute(
			Map<String, Object> inputObjects, String script) {

			_consumer.accept(inputObjects);

			return HashMapBuilder.<String, Object>put(
				"validationCriteriaMet", true
			).build();
		}

		@Override
		public long getAllowedCompanyId() {
			return _allowedCompanyId;
		}

		@Override
		public List<String> getAllowedObjectDefinitionNames() {
			return _allowedObjectDefinitionNames;
		}

		@Override
		public String getKey() {
			return _key;
		}

		@Override
		public String getLabel(Locale locale) {
			return RandomTestUtil.randomString();
		}

		private TestObjectValidationRuleEngine(
			long allowedCompanyId, List<String> allowedObjectDefinitionNames,
			Consumer<Map<String, Object>> consumer, String key) {

			_allowedCompanyId = allowedCompanyId;
			_allowedObjectDefinitionNames = allowedObjectDefinitionNames;
			_consumer = consumer;
			_key = key;
		}

		private final long _allowedCompanyId;
		private final List<String> _allowedObjectDefinitionNames;
		private final Consumer<Map<String, Object>> _consumer;
		private final String _key;

	}

}