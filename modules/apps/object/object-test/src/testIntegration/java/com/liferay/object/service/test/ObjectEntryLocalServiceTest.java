/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.service.CommerceOrderLocalServiceUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.test.util.ExpandoTestUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.list.type.entry.util.ListTypeEntryUtil;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.action.util.ObjectActionThreadLocal;
import com.liferay.object.constants.ObjectActionConstants;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.constants.ObjectValidationRuleConstants;
import com.liferay.object.constants.ObjectValidationRuleSettingConstants;
import com.liferay.object.entry.util.ObjectEntryThreadLocal;
import com.liferay.object.exception.DuplicateObjectEntryExternalReferenceCodeException;
import com.liferay.object.exception.NoSuchObjectDefinitionException;
import com.liferay.object.exception.NoSuchObjectEntryException;
import com.liferay.object.exception.ObjectEntryGroupIdException;
import com.liferay.object.exception.ObjectEntryStatusException;
import com.liferay.object.exception.ObjectEntryValuesException;
import com.liferay.object.exception.ObjectValidationRuleEngineException;
import com.liferay.object.field.builder.AggregationObjectFieldBuilder;
import com.liferay.object.field.builder.AssigneeObjectFieldBuilder;
import com.liferay.object.field.builder.AttachmentObjectFieldBuilder;
import com.liferay.object.field.builder.AutoIncrementObjectFieldBuilder;
import com.liferay.object.field.builder.BooleanObjectFieldBuilder;
import com.liferay.object.field.builder.DateObjectFieldBuilder;
import com.liferay.object.field.builder.DateTimeObjectFieldBuilder;
import com.liferay.object.field.builder.DecimalObjectFieldBuilder;
import com.liferay.object.field.builder.EncryptedObjectFieldBuilder;
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
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.model.ObjectState;
import com.liferay.object.model.ObjectStateFlow;
import com.liferay.object.model.ObjectStateTransition;
import com.liferay.object.model.ObjectValidationRule;
import com.liferay.object.model.ObjectValidationRuleSetting;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.scope.CompanyScoped;
import com.liferay.object.scope.ObjectDefinitionScoped;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.ObjectStateFlowLocalService;
import com.liferay.object.service.ObjectStateLocalService;
import com.liferay.object.service.ObjectStateTransitionLocalService;
import com.liferay.object.service.ObjectValidationRuleLocalService;
import com.liferay.object.service.test.util.ObjectFieldTestUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectEntryFolderTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.object.test.util.TreeTestUtil;
import com.liferay.object.tree.Node;
import com.liferay.object.tree.Tree;
import com.liferay.object.validation.rule.ObjectValidationRuleEngine;
import com.liferay.object.validation.rule.ObjectValidationRuleResult;
import com.liferay.object.validation.rule.setting.builder.ObjectValidationRuleSettingBuilder;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.comment.CommentManagerUtil;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
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
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.IdentityServiceContextFunction;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
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
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.security.script.management.test.rule.ScriptManagementConfigurationTestRule;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalDateTimeUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.service.TrashEntryLocalService;

import java.io.ByteArrayInputStream;
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
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
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
@FeatureFlag("LPD-34594")
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

	@BeforeClass
	public static void setUpClass() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			ObjectEntryLocalServiceTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			ModelListener.class, _testDLFileEntryModelListener, null);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Before
	public void setUp() throws Exception {
		_draftObjectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())));
		_irrelevantObjectDefinition = _publishCustomObjectDefinition(
			true,
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
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
					ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
					"Attachment", "attachment",
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
							String.valueOf(100)
						).build()),
					false),
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

		_siteObjectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), "textObjectFieldName")),
				ObjectDefinitionConstants.SCOPE_SITE);
	}

	@After
	public void tearDown() throws Exception {

		// Do not rely on @DeleteAfterTestRun because object entries that
		// reference a required list type entry cannot be deleted before it is
		// unreferenced

		_objectDefinitionLocalService.deleteObjectDefinition(_objectDefinition);
	}

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

		ObjectRelationshipTestUtil.addObjectRelationship(
			_objectRelationshipLocalService, objectDefinition1,
			objectDefinition2,
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
			StringUtil.randomId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

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
			objectEntry.getObjectEntryFolderId(),
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
			objectEntry.getObjectEntryFolderId(),
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

	@FeatureFlag("LPD-32050")
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

		String externalReferenceCode = RandomTestUtil.randomString();

		Group group1 = GroupTestUtil.addGroup();

		_addObjectEntry(
			group1.getGroupId(), _siteObjectDefinition.getObjectDefinitionId(),
			Collections.singletonMap(
				"externalReferenceCode", externalReferenceCode));

		Group group2 = GroupTestUtil.addGroup();

		_addObjectEntry(
			group2.getGroupId(), _siteObjectDefinition.getObjectDefinitionId(),
			Collections.singletonMap(
				"externalReferenceCode", externalReferenceCode));

		AssertUtils.assertFailure(
			DuplicateObjectEntryExternalReferenceCodeException.class,
			StringBundler.concat(
				"Duplicate object entry with external reference code ",
				externalReferenceCode, ", group ID ", group1.getGroupId(),
				", and object definition ID ",
				_siteObjectDefinition.getObjectDefinitionId()),
			() -> _addObjectEntry(
				group1.getGroupId(),
				_siteObjectDefinition.getObjectDefinitionId(),
				Collections.singletonMap(
					"externalReferenceCode", externalReferenceCode)));

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

		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			_objectDefinition.getObjectDefinitionId(), "upload");

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.fetchObjectFieldSetting(
				objectField.getObjectFieldId(), "acceptedFileExtensions");

		_objectFieldSettingLocalService.updateObjectFieldSetting(
			objectFieldSetting.getObjectFieldSettingId(), "*");

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

		_assertCount(9);

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
					"listTypeEntryKeyRequired", "listTypeEntryKey1"
				).put(
					"multipleListTypeEntriesKey",
					(Serializable)Arrays.asList(
						"multipleListTypeEntryKey1",
						RandomTestUtil.randomString())
				).build()));
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

		Group group3 = GroupTestUtil.addGroup();

		_addObjectEntry(
			group3.getGroupId(), objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"name", "Peter"
			).build());

		AssertUtils.assertFailure(
			ObjectEntryValuesException.UniqueValueConstraintViolation.class,
			"Unique value constraint violation for " +
				objectDefinition.getDBTableName() + ".name_ with value Peter",
			() -> _addObjectEntry(
				group3.getGroupId(), finalObjectDefinitionId,
				HashMapBuilder.<String, Serializable>put(
					"name", "Peter"
				).build()));

		_testAddObjectEntryWithLocalizedValues(objectDefinition, group3);

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
			modifiableSystemObjectDefinition, group3);

		_objectDefinitionLocalService.deleteObjectDefinition(
			modifiableSystemObjectDefinition.getObjectDefinitionId());
	}

	@FeatureFlag("LPD-32050")
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
				true, Collections.singletonList(objectField));

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
	public void testAddObjectEntryToStagingGroup() throws Exception {
		Group liveGroup = null;

		try {
			liveGroup = GroupTestUtil.addGroup();

			GroupTestUtil.enableLocalStaging(
				liveGroup, TestPropsValues.getUserId());

			Group stagingGroup = liveGroup.getStagingGroup();

			_objectDefinition.setScope(ObjectDefinitionConstants.SCOPE_SITE);

			_objectDefinition =
				_objectDefinitionLocalService.updateObjectDefinition(
					_objectDefinition);

			ObjectEntry objectEntry = _addObjectEntry(
				stagingGroup.getGroupId(),
				_objectDefinition.getObjectDefinitionId(),
				HashMapBuilder.<String, Serializable>put(
					"emailAddressRequired", "athanasius@liferay.com"
				).put(
					"listTypeEntryKeyRequired", "listTypeEntryKey1"
				).build());

			Assert.assertEquals(
				stagingGroup.getGroupId(), objectEntry.getGroupId());
		}
		finally {
			if (liveGroup != null) {
				GroupTestUtil.deleteGroup(liveGroup);
			}
		}
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testAddObjectEntryWithAssetTag() throws Exception {
		ObjectFolder objectFolder =
			_objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
				TestPropsValues.getCompanyId());

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				false, ObjectDefinitionTestUtil.getRandomName(),
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, "name", "name")),
				objectFolder.getObjectFolderId(),
				ObjectDefinitionConstants.SCOPE_SITE,
				TestPropsValues.getUserId());

		String assetTagName = StringUtil.randomString();

		Assert.assertNull(
			_assetTagLocalService.fetchTag(
				TestPropsValues.getGroupId(), assetTagName));

		Group group = _groupLocalService.fetchGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CMS);

		Assert.assertNull(
			_assetTagLocalService.fetchTag(group.getGroupId(), assetTagName));

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setAssetTagNames(new String[] {assetTagName});

		_addObjectEntry(
			TestPropsValues.getGroupId(), objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"name", StringUtil.randomString()
			).build(),
			serviceContext);

		Assert.assertNull(
			_assetTagLocalService.fetchTag(
				TestPropsValues.getGroupId(), assetTagName));
		Assert.assertNotNull(
			_assetTagLocalService.fetchTag(group.getGroupId(), assetTagName));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@FeatureFlag("LPD-6233")
	@Test
	public void testAddObjectEntryWithAssigneeObjectField() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new AssigneeObjectFieldBuilder(
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).name(
						"assignee"
					).build()));

		Map<String, Long> assigneeMap = HashMapBuilder.put(
			"classNameId", _portal.getClassNameId(User.class.getName())
		).put(
			"classPK",
			() -> {
				User user = UserTestUtil.addUser();

				return user.getUserId();
			}
		).build();

		ObjectEntry objectEntry = _addObjectEntry(
			objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"assignee", (Serializable)assigneeMap
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Map<String, Serializable> values = _objectEntryLocalService.getValues(
			objectEntry.getObjectEntryId());

		Assert.assertEquals(assigneeMap, values.get("assignee"));
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

		_objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"state", "listTypeEntryKey3"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertNotNull(
			_dlAppLocalService.getFileEntry(persistedFileEntryId1));

		_objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
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

		objectEntry = _objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
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

	@FeatureFlag("LPD-17564")
	@Test
	public void testAddObjectEntryWithDraftWorkflowDefinition()
		throws Exception {

		WorkflowDefinitionLink workflowDefinitionLink =
			_workflowDefinitionLinkLocalService.createWorkflowDefinitionLink(
				0L);

		workflowDefinitionLink.setUserId(TestPropsValues.getUserId());
		workflowDefinitionLink.setWorkflowDefinitionName(
			RandomTestUtil.randomString());

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				ObjectDefinitionTestUtil.getRandomName(),
				List.of(workflowDefinitionLink));

		objectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		ObjectEntry objectEntry = _addObjectEntry(
			0, objectDefinition.getObjectDefinitionId(), Map.of());

		Assert.assertTrue(objectEntry.isApproved());
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
			"objectEntryERC", ObjectDefinitionConstants.GROUP_ID_DEFAULT,
			_objectDefinition.getObjectDefinitionId());

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
	@TestInfo("LPD-54861")
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
		ObjectField objectField3 = _addCustomObjectField(
			new FormulaObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"bodyMassIndex"
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).objectFieldSettings(
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						"script"
					).value(
						"weight / height"
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						"output"
					).value(
						ObjectFieldConstants.BUSINESS_TYPE_DECIMAL
					).build())
			).build());
		ObjectField objectField4 = _addCustomObjectField(
			new FormulaObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"decimalDivision"
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).objectFieldSettings(
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						"script"
					).value(
						"weight / id"
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						"output"
					).value(
						ObjectFieldConstants.BUSINESS_TYPE_DECIMAL
					).build())
			).build());

		ObjectField objectField5 = _addCustomObjectField(
			new FormulaObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"integerDivision"
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).objectFieldSettings(
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						"script"
					).value(
						"weight / id"
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						"output"
					).value(
						ObjectFieldConstants.BUSINESS_TYPE_INTEGER
					).build())
			).build());

		Double randomDouble = RandomTestUtil.randomDouble();

		ObjectEntry objectEntry = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddress", RandomTestUtil.randomString()
			).put(
				"emailAddressRequired", "athanasius@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).put(
				"weight", randomDouble
			).build());

		Assert.assertEquals(
			objectEntry.getObjectEntryId() + objectEntry.getObjectEntryId(),
			MapUtil.getDouble(
				_objectEntryLocalService.getValues(
					objectEntry.getObjectEntryId()),
				objectField1.getName()),
			0);
		Assert.assertEquals(
			randomDouble + 10,
			MapUtil.getDouble(
				_objectEntryLocalService.getValues(
					objectEntry.getObjectEntryId()),
				objectField2.getName()),
			0);
		Assert.assertEquals(
			0D,
			MapUtil.getDouble(
				_objectEntryLocalService.getValues(objectEntry),
				objectField3.getName()),
			0);
		Assert.assertEquals(
			randomDouble / objectEntry.getObjectEntryId(),
			MapUtil.getDouble(
				_objectEntryLocalService.getValues(objectEntry),
				objectField4.getName()),
			0.001);
		Assert.assertEquals(
			(int)(randomDouble / objectEntry.getObjectEntryId()),
			MapUtil.getDouble(
				_objectEntryLocalService.getValues(objectEntry),
				objectField5.getName()),
			0);

		_objectFieldLocalService.deleteObjectField(objectField1);
		_objectFieldLocalService.deleteObjectField(objectField2);
		_objectFieldLocalService.deleteObjectField(objectField3);
		_objectFieldLocalService.deleteObjectField(objectField4);
		_objectFieldLocalService.deleteObjectField(objectField5);
	}

	@FeatureFlag("LPD-32050")
	@Test
	public void testAddObjectEntryWithFormulaObjectFieldAndObjectRelationship()
		throws Exception {

		ObjectDefinition objectDefinition1 =
			ObjectDefinitionTestUtil.publishObjectDefinition();
		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		ObjectRelationshipTestUtil.addObjectRelationship(
			_objectRelationshipLocalService, objectDefinition1,
			objectDefinition2,
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
			StringUtil.randomId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
		ObjectRelationshipTestUtil.addObjectRelationship(
			_objectRelationshipLocalService, objectDefinition1,
			objectDefinition2,
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
			"objectRelationship", ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

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

	@FeatureFlag("LPD-32050")
	@Test
	public void testAddObjectEntryWithLocalizedAttachmentObjectField()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new AttachmentObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).localized(
				true
			).name(
				"a" + RandomTestUtil.randomString()
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
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
			).userId(
				TestPropsValues.getUserId()
			).build());

		FileEntry tempFileEntry1 = _addTempFileEntry(StringUtil.randomString());
		FileEntry tempFileEntry2 = _addTempFileEntry(StringUtil.randomString());

		ObjectEntry objectEntry = _addObjectEntry(
			0, objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				objectField.getI18nObjectFieldName(),
				HashMapBuilder.put(
					"en_US", tempFileEntry1.getFileEntryId()
				).put(
					"pt_BR", tempFileEntry2.getFileEntryId()
				).build()
			).build());

		AssertUtils.assertFailure(
			NoSuchFileEntryException.class,
			StringBundler.concat(
				"No FileEntry exists with the key {fileEntryId=",
				tempFileEntry1.getFileEntryId(), "}"),
			() -> _dlAppLocalService.getFileEntry(
				tempFileEntry1.getFileEntryId()));
		AssertUtils.assertFailure(
			NoSuchFileEntryException.class,
			StringBundler.concat(
				"No FileEntry exists with the key {fileEntryId=",
				tempFileEntry2.getFileEntryId(), "}"),
			() -> _dlAppLocalService.getFileEntry(
				tempFileEntry2.getFileEntryId()));

		Map<String, Serializable> values = objectEntry.getValues();

		Map<String, Object> localizedValues = (Map<String, Object>)values.get(
			objectField.getI18nObjectFieldName());

		Assert.assertNotNull(
			_dlAppLocalService.getFileEntry(
				GetterUtil.getLong(localizedValues.get("en_US"))));
		Assert.assertNotNull(
			_dlAppLocalService.getFileEntry(
				GetterUtil.getLong(localizedValues.get("pt_BR"))));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@FeatureFlag("LPD-32050")
	@Test
	public void testAddObjectEntryWithLocalizedBooleanObjectField()
		throws Exception {

		ObjectField objectField = new BooleanObjectFieldBuilder(
		).labelMap(
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
		).localized(
			true
		).name(
			"a" + RandomTestUtil.randomString()
		).required(
			true
		).build();

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(objectField));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.RequiredLanguageId.class,
			StringBundler.concat(
				"No value was provided for the language ID \"en_US\" in the ",
				"required object field \"", objectField.getName(), "\"."),
			() -> ObjectEntryTestUtil.addObjectEntry(
				0, objectDefinition.getObjectDefinitionId(),
				HashMapBuilder.<String, Serializable>put(
					objectField.getI18nObjectFieldName(),
					HashMapBuilder.put(
						"en_US", false
					).put(
						"pt_BR", false
					).build()
				).build()));

		Assert.assertNotNull(
			ObjectEntryTestUtil.addObjectEntry(
				0, objectDefinition.getObjectDefinitionId(),
				HashMapBuilder.<String, Serializable>put(
					objectField.getI18nObjectFieldName(),
					HashMapBuilder.put(
						"en_US", true
					).put(
						"pt_BR", false
					).build()
				).build()));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Test
	@TestInfo("LPD-55656")
	public void testAddObjectEntryWithMissingListTypeEntryReference()
		throws Exception {

		String listTypeEntryKey = RandomTestUtil.randomString();

		ListTypeEntry listTypeEntry =
			_listTypeEntryLocalService.fetchListTypeEntry(
				_listTypeDefinition.getListTypeDefinitionId(),
				listTypeEntryKey);

		Assert.assertNull(listTypeEntry);

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			_addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"emailAddressRequired", "bob@liferay.com"
				).put(
					"listTypeEntryKey", ""
				).put(
					"listTypeEntryKeyRequired", listTypeEntryKey
				).build());

			listTypeEntry = _listTypeEntryLocalService.fetchListTypeEntry(
				_listTypeDefinition.getListTypeDefinitionId(),
				listTypeEntryKey);

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, listTypeEntry.getStatus());

			Assert.assertNull(
				_listTypeEntryLocalService.fetchListTypeEntry(
					_listTypeDefinition.getListTypeDefinitionId(), ""));
		}
	}

	@Test
	public void testAddObjectEntryWithMultiselectPicklistObjectField()
		throws Exception {

		String prefixKey = RandomTestUtil.randomString(60);

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				false,
				_createListTypeEntries(
					prefixKey, RandomTestUtil.randomString(), 100));

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new MultiselectPicklistObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).listTypeDefinitionId(
						listTypeDefinition.getListTypeDefinitionId()
					).name(
						"multiselectPicklistObjectField"
					).build()));

		_objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"multiselectPicklistObjectField",
				_getMultiselectPicklistObjectFieldValue(prefixKey, 10)
			).build(),
			new ServiceContext());

		int expectedMaxLength = 5000;

		if (DBManagerUtil.getDBType() == DBType.SQLSERVER) {
			expectedMaxLength = 4000;
		}

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsTextMaxLength.class,
			StringBundler.concat(
				"Object entry value exceeds the maximum length of ",
				expectedMaxLength, " characters for object field ",
				"\"multiselectPicklistObjectField\""),
			() -> _objectEntryLocalService.addObjectEntry(
				0, TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"multiselectPicklistObjectField",
					_getMultiselectPicklistObjectFieldValue(prefixKey, 100)
				).build(),
				new ServiceContext()));
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testAddObjectEntryWithObjectEntryScheduleEnabled()
		throws Exception {

		_testAddObjectEntryWithObjectEntryScheduleEnabled(
			false, WorkflowConstants.STATUS_APPROVED,
			WorkflowConstants.ACTION_PUBLISH);
		_testAddObjectEntryWithObjectEntryScheduleEnabled(
			true, WorkflowConstants.STATUS_DRAFT,
			WorkflowConstants.ACTION_SAVE_DRAFT);
	}

	@Test
	public void testAddObjectEntryWithObjectRelationshipInScopeDepot()
		throws Exception {

		ObjectDefinition objectDefinition1 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).name(
						"a" + RandomTestUtil.randomString()
					).build()),
				ObjectDefinitionConstants.SCOPE_DEPOT);

		_objectDefinitionSettingLocalService.addObjectDefinitionSetting(
			objectDefinition1.getUserId(),
			objectDefinition1.getObjectDefinitionId(),
			ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS,
			StringPool.TRUE);

		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).name(
						"a" + RandomTestUtil.randomString()
					).build()),
				ObjectDefinitionConstants.SCOPE_DEPOT);

		_objectDefinitionSettingLocalService.addObjectDefinitionSetting(
			objectDefinition2.getUserId(),
			objectDefinition2.getObjectDefinitionId(),
			ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS,
			StringPool.TRUE);

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, objectDefinition1,
				objectDefinition2,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
				"objectRelationship");

		DepotEntry depotEntry1 = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		ObjectEntry objectEntry1 = _addObjectEntry(
			depotEntry1.getGroupId(), objectDefinition1.getObjectDefinitionId(),
			Collections.emptyMap());

		ObjectField relationshipObjectField =
			_objectFieldLocalService.fetchObjectField(
				objectRelationship.getObjectFieldId2());

		Map<String, Serializable> values = Collections.singletonMap(
			relationshipObjectField.getName(), objectEntry1.getObjectEntryId());

		ObjectEntry objectEntry2 = _addObjectEntry(
			depotEntry1.getGroupId(), objectDefinition2.getObjectDefinitionId(),
			values);

		_assertObjectEntryValues(
			4, values,
			_objectEntryLocalService.getValues(
				objectEntry2.getObjectEntryId()));

		DepotEntry depotEntry2 = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		AssertUtils.assertFailure(
			ObjectEntryValuesException.InvalidValue.class,
			"The value is invalid for object field \"" +
				relationshipObjectField.getName() + "\"",
			() -> _addObjectEntry(
				depotEntry2.getGroupId(),
				objectDefinition2.getObjectDefinitionId(), values));
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

		// Field must not be empty

		ObjectField localizedObjectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).localized(
				true
			).name(
				"textLocalized"
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).userId(
				TestPropsValues.getUserId()
			).build());

		ObjectValidationRule objectValidationRule5 = _addObjectValidationRule(
			ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
			LocalizedMapUtil.getLocalizedMap("Field must not be empty"),
			"NOT(isEmpty(textLocalized))");

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				localizedObjectField.getI18nObjectFieldName(),
				HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).put(
					"pt_BR", RandomTestUtil.randomString()
				).build()
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

		try {
			_addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					localizedObjectField.getI18nObjectFieldName(),
					HashMapBuilder.put(
						"en_US", StringPool.BLANK
					).put(
						"pt_BR", StringPool.BLANK
					).build()
				).put(
					"emailAddressRequired", RandomTestUtil.randomString()
				).put(
					"listTypeEntryKeyRequired", "listTypeEntryKey1"
				).build());

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

			_assertObjectValidationRuleResult(
				objectValidationRule5.getErrorLabel(LocaleUtil.getDefault()),
				null,
				objectValidationRuleResults.get(
					objectValidationRuleResults.size() - 1));
		}

		_objectValidationRuleLocalService.deleteObjectValidationRule(
			objectValidationRule5);

		// Must be over 18 years old

		Class<?> clazz = getClass();

		ObjectValidationRule objectValidationRule6 = _addObjectValidationRule(
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

		_assertCount(6);

		// Names must be equals

		ObjectValidationRule objectValidationRule7 = _addObjectValidationRule(
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

		_assertCount(7);

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
				objectValidationRule6.getErrorLabel(LocaleUtil.getDefault()),
				null, objectValidationRuleResults.get(3));
			_assertObjectValidationRuleResult(
				objectValidationRule7.getErrorLabel(LocaleUtil.getDefault()),
				null, objectValidationRuleResults.get(4));
		}

		// Disable object validation rule 7

		objectValidationRule7.setActive(false);

		_objectValidationRuleLocalService.updateObjectValidationRule(
			objectValidationRule7);

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

		_assertCount(8);

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

		_objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, values, serviceContext);

		_assertCount(9);
	}

	@FeatureFlag("LPD-31212")
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

		ObjectAction objectAction = _addObjectAction(
			objectDefinition2, ObjectActionTriggerConstants.KEY_STANDALONE);

		Assert.assertThat(
			_resourceActions.getModelResourceActions(
				objectDefinition2.getClassName()),
			CoreMatchers.hasItem(objectAction.getName()));
		Assert.assertThat(
			_resourceActions.getModelResourceOwnerDefaultActions(
				objectDefinition2.getClassName()),
			CoreMatchers.hasItem(objectAction.getName()));

		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			objectRelationship.getObjectFieldId2());

		ObjectEntry objectEntry2 = _addObjectEntry(
			0, objectDefinition2.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				objectField.getName(),
				() -> {
					ObjectEntry objectEntry = _addObjectEntry(
						0, objectDefinition1.getObjectDefinitionId(),
						Collections.emptyMap());

					return objectEntry.getObjectEntryId();
				}
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
			24, values,
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
			24, values,
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

	@Test
	public void testAddOrUpdateObjectEntryWithAttachmentObjectFieldAndObjectValidationRule()
		throws Exception {

		ObjectValidationRule objectValidationRule = _addObjectValidationRule(
			ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
			LocalizedMapUtil.getLocalizedMap("Field must be an email address"),
			"isEmailAddress(emailAddressRequired)");

		FileEntry tempFileEntry1 = _addTempFileEntry(StringUtil.randomString());

		try {
			_addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"emailAddressRequired", RandomTestUtil.randomString()
				).put(
					"listTypeEntryKeyRequired", "listTypeEntryKey1"
				).put(
					"upload", tempFileEntry1.getFileEntryId()
				).build());

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			_assertFailureObjectValidationRule(
				modelListenerException, objectValidationRule);
		}

		Assert.assertNotNull(
			_dlAppLocalService.getFileEntry(tempFileEntry1.getFileEntryId()));

		_clearValidatedObjectEntryIds();

		ObjectEntry objectEntry = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "james@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).put(
				"upload", tempFileEntry1.getFileEntryId()
			).build());

		AssertUtils.assertFailure(
			NoSuchFileEntryException.class,
			StringBundler.concat(
				"No FileEntry exists with the key {fileEntryId=",
				tempFileEntry1.getFileEntryId(), "}"),
			() -> _dlAppLocalService.getFileEntry(
				tempFileEntry1.getFileEntryId()));

		long dlFileEntryId = MapUtil.getLong(objectEntry.getValues(), "upload");

		Assert.assertNotNull(
			_dlFileEntryLocalService.getDLFileEntry(dlFileEntryId));

		_clearValidatedObjectEntryIds();

		FileEntry tempFileEntry2 = _addTempFileEntry(StringUtil.randomString());

		try {
			objectEntry = _addOrUpdateObjectEntry(
				objectEntry.getExternalReferenceCode(), 0,
				HashMapBuilder.<String, Serializable>put(
					"emailAddressRequired", RandomTestUtil.randomString()
				).put(
					"listTypeEntryKeyRequired", "listTypeEntryKey1"
				).put(
					"upload", tempFileEntry2.getFileEntryId()
				).build());

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			_assertFailureObjectValidationRule(
				modelListenerException, objectValidationRule);
		}

		Assert.assertEquals(
			dlFileEntryId, MapUtil.getLong(objectEntry.getValues(), "upload"));
		Assert.assertNotNull(
			_dlFileEntryLocalService.getDLFileEntry(dlFileEntryId));

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.getDLFileEntry(
			tempFileEntry2.getFileEntryId());

		dlFileEntry.setCreateDate(
			new Date(System.currentTimeMillis() - Time.DAY));

		_dlFileEntryLocalService.updateDLFileEntry(dlFileEntry);

		UnsafeRunnable<Exception> unsafeRunnable =
			_tempFileEntriesSchedulerJobConfiguration.
				getJobExecutorUnsafeRunnable();

		unsafeRunnable.run();

		AssertUtils.assertFailure(
			NoSuchFileEntryException.class,
			StringBundler.concat(
				"No FileEntry exists with the key {fileEntryId=",
				tempFileEntry2.getFileEntryId(), "}"),
			() -> _dlAppLocalService.getFileEntry(
				tempFileEntry2.getFileEntryId()));
	}

	@FeatureFlag("LPD-21926")
	@Test
	public void testAddOrUpdateObjectEntryWithFriendlyURL() throws Exception {

		// Scope by company

		ObjectDefinition companyObjectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				true, Collections.emptyList());

		companyObjectDefinition.setFriendlyURLSeparator("test1");

		companyObjectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				companyObjectDefinition);

		ObjectEntry companyObjectEntry1 = _addObjectEntry(
			0, companyObjectDefinition.getObjectDefinitionId(),
			Collections.emptyMap());

		AssertUtils.assertEquals(
			HashMapBuilder.put(
				"en_US", companyObjectEntry1.getExternalReferenceCode()
			).build(),
			companyObjectEntry1.getURLTitleMap());

		companyObjectEntry1 = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), companyObjectEntry1.getObjectEntryId(),
			companyObjectEntry1.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"able", "Test URL"
			).put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		AssertUtils.assertEquals(
			HashMapBuilder.put(
				"en_US", "test-url"
			).build(),
			companyObjectEntry1.getURLTitleMap());

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

		companyObjectDefinition =
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

		AssertUtils.assertEquals(
			HashMapBuilder.put(
				"en_US", "test-url-1"
			).build(),
			companyObjectEntry2.getURLTitleMap());

		_assertFriendlyURLEntries(
			2, companyObjectDefinition, companyObjectEntry1);
		_assertFriendlyURLEntries(
			1, companyObjectDefinition, companyObjectEntry2);

		companyObjectDefinition.setTitleObjectFieldId(0);

		companyObjectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				companyObjectDefinition);

		ObjectEntry companyObjectEntry3 = _addObjectEntry(
			0, companyObjectDefinition.getObjectDefinitionId(),
			Collections.emptyMap());

		AssertUtils.assertEquals(
			HashMapBuilder.put(
				"en_US", companyObjectEntry3.getExternalReferenceCode()
			).build(),
			companyObjectEntry3.getURLTitleMap());

		_objectDefinitionLocalService.deleteObjectDefinition(
			companyObjectDefinition);

		// Scope by site

		ObjectDefinition siteObjectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		siteObjectDefinition.setFriendlyURLSeparator("test2");
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

		AssertUtils.assertEquals(
			HashMapBuilder.put(
				"en_US", "test-url"
			).build(),
			siteObjectEntry1.getURLTitleMap());

		Group group2 = GroupTestUtil.addGroup();

		ObjectEntry siteObjectEntry2 = _addObjectEntry(
			group2.getGroupId(), siteObjectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"able", "Test URL"
			).build());

		AssertUtils.assertEquals(
			HashMapBuilder.put(
				"en_US", "test-url"
			).build(),
			siteObjectEntry2.getURLTitleMap());

		_objectDefinitionLocalService.deleteObjectDefinition(
			siteObjectDefinition);

		_assertFriendlyURLEntries(0, siteObjectDefinition, siteObjectEntry1);
		_assertFriendlyURLEntries(0, siteObjectDefinition, siteObjectEntry2);
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
			objectEntry.getObjectEntryFolderId(),
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

		List<SystemEvent> systemEvents =
			_systemEventLocalService.getSystemEvents(
				0, _portal.getClassNameId(objectEntry1.getModelClassName()),
				objectEntry1.getPrimaryKey());

		SystemEvent systemEvent = systemEvents.get(0);

		Assert.assertEquals(
			objectEntry1.getExternalReferenceCode(),
			systemEvent.getClassExternalReferenceCode());
		Assert.assertEquals(
			SystemEventConstants.TYPE_DELETE, systemEvent.getType());

		AssertUtils.assertFailure(
			NoSuchObjectEntryException.class,
			"No ObjectEntry exists with the primary key " +
				objectEntry1.getObjectEntryId(),
			() -> _objectEntryLocalService.deleteObjectEntry(
				objectEntry1.getObjectEntryId()));
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
			_objectDefinition.isEnableFormContainer(),
			_objectDefinition.isEnableFriendlyURLCustomization(),
			_objectDefinition.isEnableIndexSearch(),
			_objectDefinition.isEnableLocalization(),
			_objectDefinition.isEnableObjectEntryDraft(),
			_objectDefinition.isEnableObjectEntryHistory(),
			_objectDefinition.isEnableObjectEntrySchedule(),
			_objectDefinition.isEnableObjectEntrySubscription(),
			_objectDefinition.isEnableObjectEntryVersioning(),
			_objectDefinition.getFriendlyURLSeparator(),
			_objectDefinition.getLabelMap(), _objectDefinition.getName(),
			_objectDefinition.getPanelAppOrder(),
			_objectDefinition.getPanelCategoryKey(),
			_objectDefinition.isPortlet(),
			_objectDefinition.getPluralLabelMap(), _objectDefinition.getScope(),
			_objectDefinition.getStatus(), Collections.emptyList(),
			Collections.emptyList());

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
	public void testDeleteObjectEntryWithAttachmentObjectFieldAndEnableObjectEntryVersioning()
		throws Exception {

		_enableObjectEntryVersioning();

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"attachment",
				() -> {
					FileEntry tempFileEntry = _addTempFileEntry(
						StringUtil.randomString());

					return tempFileEntry.getFileEntryId();
				}
			).put(
				"emailAddressRequired", "test@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build());

		long dlFileEntryId1 = MapUtil.getLong(
			objectEntry.getValues(), "attachment");

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"attachment",
				() -> {
					FileEntry tempFileEntry = _addTempFileEntry(
						StringUtil.randomString());

					return tempFileEntry.getFileEntryId();
				}
			).put(
				"emailAddressRequired", "test@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		long dlFileEntryId2 = MapUtil.getLong(
			objectEntry.getValues(), "attachment");

		Assert.assertNotNull(_dlAppLocalService.getFileEntry(dlFileEntryId1));
		Assert.assertNotNull(_dlAppLocalService.getFileEntry(dlFileEntryId2));

		_objectEntryLocalService.deleteObjectEntry(objectEntry);

		AssertUtils.assertFailure(
			NoSuchFileEntryException.class,
			StringBundler.concat(
				"No FileEntry exists with the key {fileEntryId=",
				dlFileEntryId1, "}"),
			() -> _dlAppLocalService.getFileEntry(dlFileEntryId1));
		AssertUtils.assertFailure(
			NoSuchFileEntryException.class,
			StringBundler.concat(
				"No FileEntry exists with the key {fileEntryId=",
				dlFileEntryId2, "}"),
			() -> _dlAppLocalService.getFileEntry(dlFileEntryId2));
	}

	@Test
	public void testDeleteObjectEntryWithLongExternalReferenceCode()
		throws Exception {

		ObjectEntry objectEntry1 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "peter@liferay.com"
			).put(
				"firstName", "Peter"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build());

		objectEntry1.setExternalReferenceCode(
			RandomTestUtil.randomString(1000));

		objectEntry1 = _objectEntryLocalService.updateObjectEntry(objectEntry1);

		_assertCount(1);

		_objectEntryLocalService.deleteObjectEntry(
			objectEntry1.getObjectEntryId());

		List<SystemEvent> systemEvents =
			_systemEventLocalService.getSystemEvents(
				0, _portal.getClassNameId(objectEntry1.getModelClassName()),
				objectEntry1.getPrimaryKey());

		SystemEvent systemEvent = systemEvents.get(0);

		Assert.assertEquals(
			objectEntry1.getExternalReferenceCode(),
			systemEvent.getClassExternalReferenceCode());
		Assert.assertEquals(
			SystemEventConstants.TYPE_DELETE, systemEvent.getType());
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
			"A1", ObjectDefinitionConstants.GROUP_ID_DEFAULT,
			rootObjectDefinition.getObjectDefinitionId());

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

		boolean enableLocalization = objectDefinition.isEnableLocalization();

		objectDefinition.setEnableLocalization(true);

		objectDefinition = _objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);

		ObjectField objectField1 = _addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).localized(
				true
			).name(
				"localizedTextField"
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).build());
		ObjectField objectField2 = _addCustomObjectField(
			new LongIntegerObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"longField"
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).build());
		ObjectField objectField3 = _addCustomObjectField(
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

		ObjectDefinition finalObjectDefinition = objectDefinition;

		User user = UserTestUtil.addUser();

		AssertUtils.assertFailure(
			ObjectEntryValuesException.Required.class,
			"No value was provided for required object field \"textField\"",
			() ->
				_objectEntryLocalService.
					addOrUpdateExtensionDynamicObjectDefinitionTableValues(
						TestPropsValues.getUserId(), finalObjectDefinition,
						user.getUserId(), Collections.emptyMap(),
						ServiceContextTestUtil.getServiceContext()));

		Map<String, Serializable> values =
			HashMapBuilder.<String, Serializable>put(
				"localizedTextField", "en_US localizedTextFieldValue1"
			).put(
				"localizedTextField_i18n",
				HashMapBuilder.put(
					"en_US", "en_US localizedTextFieldValue1"
				).put(
					"pt_BR", "pt_BR localizedTextFieldValue1"
				).build()
			).put(
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
			"localizedTextField", "en_US localizedTextFieldValue2"
		).put(
			"localizedTextField_i18n",
			HashMapBuilder.put(
				"en_US", "en_US localizedTextFieldValue2"
			).put(
				"pt_BR", "pt_BR localizedTextFieldValue2"
			).build()
		).put(
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

		Assert.assertEquals(
			StringPool.BLANK, extensionValues.get("localizedTextField"));
		Assert.assertEquals(0L, extensionValues.get("longField"));
		Assert.assertEquals(StringPool.BLANK, extensionValues.get("textField"));

		_objectFieldLocalService.deleteObjectField(
			objectField1.getObjectFieldId());
		_objectFieldLocalService.deleteObjectField(
			objectField2.getObjectFieldId());
		_objectFieldLocalService.deleteObjectField(
			objectField3.getObjectFieldId());

		objectDefinition.setEnableLocalization(enableLocalization);

		_objectDefinitionLocalService.updateObjectDefinition(objectDefinition);
	}

	@Test
	public void testGetExtensionDynamicObjectDefinitionTableValuesWithAggregationObjectField()
		throws Exception {

		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, TestPropsValues.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT, "account", null,
			null, null, null, null,
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());

		Address address1 = _addAddress(accountEntry);
		Address address2 = _addAddress(accountEntry);

		ObjectDefinition addressObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				TestPropsValues.getCompanyId(), Address.class.getSimpleName());

		ObjectRelationship objectRelationship1 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService,
				_objectDefinitionLocalService.fetchObjectDefinition(
					TestPropsValues.getCompanyId(),
					AccountEntry.class.getSimpleName()),
				_objectDefinition);

		ObjectField relationshipObjectField1 =
			_objectFieldLocalService.fetchObjectField(
				objectRelationship1.getObjectFieldId2());

		ObjectRelationship objectRelationship2 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, addressObjectDefinition,
				_objectDefinition, TestPropsValues.getUserId(),
				relationshipObjectField1.getObjectFieldId());

		ObjectField relationshipObjectField2 =
			_objectFieldLocalService.fetchObjectField(
				objectRelationship2.getObjectFieldId2());

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "athanasius@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).put(
				relationshipObjectField1.getName(),
				accountEntry.getAccountEntryId()
			).put(
				relationshipObjectField2.getName(), address1.getAddressId()
			).build());
		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "athanasius@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).put(
				relationshipObjectField1.getName(),
				accountEntry.getAccountEntryId()
			).put(
				relationshipObjectField2.getName(), address1.getAddressId()
			).build());
		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "athanasius@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).put(
				relationshipObjectField1.getName(),
				accountEntry.getAccountEntryId()
			).put(
				relationshipObjectField2.getName(), address2.getAddressId()
			).build());

		ObjectField aggregationObjectField = _addCustomObjectField(
			new AggregationObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"a" + RandomTestUtil.randomString()
			).objectDefinitionId(
				addressObjectDefinition.getObjectDefinitionId()
			).objectFieldSettings(
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_FUNCTION
					).value(
						ObjectFieldSettingConstants.VALUE_COUNT
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.
							NAME_OBJECT_RELATIONSHIP_NAME
					).value(
						objectRelationship2.getName()
					).build())
			).build());

		Assert.assertEquals(
			2,
			MapUtil.getInteger(
				_objectEntryLocalService.
					getExtensionDynamicObjectDefinitionTableValues(
						addressObjectDefinition, address1.getPrimaryKey()),
				aggregationObjectField.getName()));
		Assert.assertEquals(
			1,
			MapUtil.getInteger(
				_objectEntryLocalService.
					getExtensionDynamicObjectDefinitionTableValues(
						addressObjectDefinition, address2.getPrimaryKey()),
				aggregationObjectField.getName()));

		_objectFieldLocalService.deleteObjectField(aggregationObjectField);

		_objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship1);
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
			24, values1, _getValuesFromDatabase(objectEntries.get(0)));

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
			24, values1, _getValuesFromDatabase(objectEntries.get(0)));
		_assertObjectEntryValues(
			24, values2, _getValuesFromDatabase(objectEntries.get(1)));

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
			24, values1, _getValuesFromDatabase(objectEntries.get(0)));
		_assertObjectEntryValues(
			24, values2, _getValuesFromDatabase(objectEntries.get(1)));
		_assertObjectEntryValues(
			24, values3, _getValuesFromDatabase(objectEntries.get(2)));

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
		Assert.assertEquals(
			objectEntry.getObjectEntryId(),
			values.get(_objectDefinition.getPKObjectFieldName()));
		Assert.assertEquals(values.toString(), 24, values.size());

		AssertUtils.assertFailure(
			NoSuchObjectEntryException.class,
			"No ObjectEntry exists with the primary key 0",
			() -> _objectEntryLocalService.getValues(0));
	}

	@Test
	@TestInfo("LPD-55658")
	public void testGetOrAddEmptyObjectEntry() throws Throwable {

		// Lazy referencing disabled

		String externalReferenceCode = RandomTestUtil.randomString();
		long groupId = TestPropsValues.getGroupId();

		AssertUtils.assertFailure(
			NoSuchObjectEntryException.class,
			String.format(
				"No ObjectEntry exists with the key {externalReference" +
					"Code=%s, groupId=%s, companyId=%s, objectDefinitionId=%s}",
				externalReferenceCode, groupId,
				_siteObjectDefinition.getCompanyId(),
				_siteObjectDefinition.getObjectDefinitionId()),
			() -> _objectEntryLocalService.getOrAddEmptyObjectEntry(
				externalReferenceCode, groupId, TestPropsValues.getUserId(),
				_siteObjectDefinition.getObjectDefinitionId()));

		// Lazy referencing enabled

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			long exportImportConfigurationId = RandomTestUtil.randomLong();

			ExportImportThreadLocal.setExportImportConfigurationId(
				exportImportConfigurationId);

			ObjectEntry objectEntry =
				_objectEntryLocalService.getOrAddEmptyObjectEntry(
					externalReferenceCode, groupId, TestPropsValues.getUserId(),
					_siteObjectDefinition.getObjectDefinitionId());

			Assert.assertEquals(groupId, objectEntry.getGroupId());
			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, objectEntry.getStatus());

			List<ExportImportReportEntry> exportImportReportEntries =
				_exportImportReportEntryLocalService.
					getExportImportReportEntries(
						TestPropsValues.getCompanyId(),
						exportImportConfigurationId);

			Assert.assertEquals(
				exportImportReportEntries.toString(), 1,
				exportImportReportEntries.size());

			ExportImportReportEntry exportImportReportEntry =
				exportImportReportEntries.get(0);

			Assert.assertEquals(
				externalReferenceCode,
				exportImportReportEntry.getClassExternalReferenceCode());
			Assert.assertEquals(
				_classNameLocalService.getClassNameId(
					_siteObjectDefinition.getClassName()),
				exportImportReportEntry.getClassNameId());
			Assert.assertEquals(
				exportImportConfigurationId,
				exportImportReportEntry.getExportImportConfigurationId());
			Assert.assertEquals(
				_siteObjectDefinition.getShortName(),
				exportImportReportEntry.getModelName());
			Assert.assertEquals(
				ObjectDefinitionConstants.SCOPE_SITE,
				exportImportReportEntry.getScope());

			Group group = _groupLocalService.getGroup(groupId);

			Assert.assertEquals(
				exportImportReportEntry.getScopeKey(),
				group.getExternalReferenceCode());

			Assert.assertEquals(
				ExportImportReportEntryConstants.TYPE_EMPTY,
				exportImportReportEntry.getType());
			Assert.assertEquals(
				ExportImportReportEntryConstants.STATUS_UNRESOLVED,
				exportImportReportEntry.getStatus());

			objectEntry = _objectEntryLocalService.updateObjectEntry(
				objectEntry.getUserId(), objectEntry.getObjectEntryId(),
				objectEntry.getObjectEntryFolderId(), Collections.emptyMap(),
				ServiceContextTestUtil.getServiceContext());

			Assert.assertEquals(groupId, objectEntry.getGroupId());
			Assert.assertEquals(
				WorkflowConstants.STATUS_APPROVED, objectEntry.getStatus());
		}
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

		_assertObjectEntryValues(24, values1, valuesList.get(0));

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

		_assertObjectEntryValues(24, values1, valuesList.get(0));
		_assertObjectEntryValues(24, values2, valuesList.get(1));

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

		_assertObjectEntryValues(24, values1, valuesList.get(0));
		_assertObjectEntryValues(24, values2, valuesList.get(1));
		_assertObjectEntryValues(24, values3, valuesList.get(2));

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

		_assertObjectEntryValues(24, values3, valuesList.get(0));

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

		_assertObjectEntryValues(24, values1, valuesList.get(0));
		_assertObjectEntryValues(24, values3, valuesList.get(1));

		// Predicate and search

		String search = "John";

		valuesList = _objectEntryLocalService.getValuesList(
			0, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), predicate, search,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, sorts);

		Assert.assertEquals(valuesList.toString(), 1, valuesList.size());

		_assertObjectEntryValues(24, values3, valuesList.get(0));

		valuesList = _objectEntryLocalService.getValuesList(
			0, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), predicate,
			StringUtil.toLowerCase(search), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, sorts);

		Assert.assertEquals(valuesList.toString(), 1, valuesList.size());

		_assertObjectEntryValues(24, values3, valuesList.get(0));

		valuesList = _objectEntryLocalService.getValuesList(
			0, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), predicate,
			StringUtil.toUpperCase(search), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, sorts);

		Assert.assertEquals(valuesList.toString(), 1, valuesList.size());

		_assertObjectEntryValues(24, values3, valuesList.get(0));

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

		_assertObjectEntryValues(24, values3, valuesList.get(0));

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
			24, values1, valuesList.get(0), selectedObjectFieldNames);
		_assertObjectEntryValues(
			24, values2, valuesList.get(1), selectedObjectFieldNames);
		_assertObjectEntryValues(
			24, values3, valuesList.get(2), selectedObjectFieldNames);
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testMoveObjectEntryToTrashWithComments() throws Exception {
		Group group = GroupTestUtil.addGroup();

		ObjectEntry objectEntry = _addObjectEntry(
			group.getGroupId(), _siteObjectDefinition.getObjectDefinitionId(),
			Collections.emptyMap());

		_addComment(group, _siteObjectDefinition, objectEntry);

		_assertCommentsCount(1, _siteObjectDefinition, objectEntry);

		_objectEntryLocalService.moveObjectEntryToTrash(
			TestPropsValues.getUserId(), objectEntry,
			ServiceContextTestUtil.getServiceContext());

		_assertCommentsCount(0, _siteObjectDefinition, objectEntry);
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testMoveObjectEntryToTrashWithObjectAction() throws Exception {
		_addObjectAction(
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE,
			_siteObjectDefinition);

		_assertCount(0);

		Group group = GroupTestUtil.addGroup();

		_objectEntryLocalService.moveObjectEntryToTrash(
			TestPropsValues.getUserId(),
			_addObjectEntry(
				group.getGroupId(),
				_siteObjectDefinition.getObjectDefinitionId(),
				Collections.emptyMap()),
			ServiceContextTestUtil.getServiceContext());

		_assertCount(1);
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testMoveObjectEntryToTrashWithObjectEntryFolder()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		ObjectEntryFolder objectEntryFolder =
			ObjectEntryFolderTestUtil.addObjectEntryFolder(group.getGroupId());

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			group.getGroupId(), _siteObjectDefinition.getObjectDefinitionId(),
			objectEntryFolder.getObjectEntryFolderId(), Collections.emptyMap());

		objectEntry = _objectEntryLocalService.moveObjectEntryToTrash(
			TestPropsValues.getUserId(), objectEntry,
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			objectEntry.getObjectEntryFolderId());

		TrashEntry trashEntry = _trashEntryLocalService.fetchEntry(
			_siteObjectDefinition.getClassName(),
			objectEntry.getObjectEntryId());

		Assert.assertEquals(
			objectEntryFolder.getObjectEntryFolderId(),
			GetterUtil.getLong(
				trashEntry.getTypeSettingsProperty("objectEntryFolderId")));
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testMoveObjectEntryToTrashWithOngoingWorkflowInstances()
		throws Exception {

		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(), 0,
			_siteObjectDefinition.getClassName(), 0, 0, "Single Approver", 1);

		Group group = GroupTestUtil.addGroup();

		ObjectEntry objectEntry = _addObjectEntry(
			group.getGroupId(), _siteObjectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", RandomTestUtil.randomString()
			).build());

		_assertGetWorkflowInstancesSize(
			_siteObjectDefinition.getClassName(),
			objectEntry.getObjectEntryId(), 1);

		_objectEntryLocalService.moveObjectEntryToTrash(
			TestPropsValues.getUserId(), objectEntry,
			ServiceContextTestUtil.getServiceContext());

		_assertGetWorkflowInstancesSize(
			_siteObjectDefinition.getClassName(),
			objectEntry.getObjectEntryId(), 0);

		TrashEntry trashEntry = _trashEntryLocalService.getEntry(
			_siteObjectDefinition.getClassName(),
			objectEntry.getObjectEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, trashEntry.getStatus());
	}

	@Test
	public void testPartialUpdateObjectEntry() throws Exception {
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

		objectEntry = _objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
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
		Assert.assertEquals(values.toString(), 24, values.size());

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

		_objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
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
		Assert.assertEquals(values.toString(), 24, values.size());

		_objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
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
		Assert.assertEquals(values.toString(), 24, values.size());

		_objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
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

		_objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			new HashMap<String, Serializable>(),
			ServiceContextTestUtil.getServiceContext());

		_objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				_objectDefinition.getPKObjectFieldName(), ""
			).put(
				"invalidName", ""
			).build(),
			ServiceContextTestUtil.getServiceContext());

		long objectEntryId = objectEntry.getObjectEntryId();
		long objectEntryFolderId = objectEntry.getObjectEntryFolderId();

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsIntegerSize.class,
			"Object entry value exceeds integer field allowed size",
			() -> _objectEntryLocalService.partialUpdateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId, objectEntryFolderId,
				HashMapBuilder.<String, Serializable>put(
					"numberOfBooksWritten", "2147483648"
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsIntegerSize.class,
			"Object entry value exceeds integer field allowed size",
			() -> _objectEntryLocalService.partialUpdateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId, objectEntryFolderId,
				HashMapBuilder.<String, Serializable>put(
					"numberOfBooksWritten", "-2147483649"
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsLongMaxSize.class,
			"Object entry value exceeds maximum long field allowed size",
			() -> _objectEntryLocalService.partialUpdateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId, objectEntryFolderId,
				HashMapBuilder.<String, Serializable>put(
					"ageOfDeath", "9007199254740992"
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsLongMinSize.class,
			"Object entry value falls below minimum long field allowed size",
			() -> _objectEntryLocalService.partialUpdateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId, objectEntryFolderId,
				HashMapBuilder.<String, Serializable>put(
					"ageOfDeath", "-9007199254740992"
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsLongSize.class,
			"Object entry value exceeds long field allowed size",
			() -> _objectEntryLocalService.partialUpdateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId, objectEntryFolderId,
				HashMapBuilder.<String, Serializable>put(
					"ageOfDeath", "9223372036854775808"
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsLongSize.class,
			"Object entry value exceeds long field allowed size",
			() -> _objectEntryLocalService.partialUpdateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId, objectEntryFolderId,
				HashMapBuilder.<String, Serializable>put(
					"ageOfDeath", "-9223372036854775809"
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsTextMaxLength.class,
			"Object entry value exceeds the maximum length of 280 characters " +
				"for object field \"firstName\"",
			() -> _objectEntryLocalService.partialUpdateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId, objectEntryFolderId,
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
			() -> _objectEntryLocalService.partialUpdateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId, objectEntryFolderId,
				HashMapBuilder.<String, Serializable>put(
					"emailAddress", "james@liferay.com"
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		_testPartialUpdateObjectEntryExternalReferenceCode();
		_testPartialUpdateObjectEntryObjectStateTransitions();
		_testPartialUpdateObjectEntryWithObjectRelationship();
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testRestoreObjectEntryFromTrashWithComments() throws Exception {
		Group group = GroupTestUtil.addGroup();

		ObjectEntry objectEntry = _addObjectEntry(
			group.getGroupId(), _siteObjectDefinition.getObjectDefinitionId(),
			Collections.emptyMap());

		_addComment(group, _siteObjectDefinition, objectEntry);

		_objectEntryLocalService.restoreObjectEntryFromTrash(
			TestPropsValues.getUserId(),
			_objectEntryLocalService.moveObjectEntryToTrash(
				TestPropsValues.getUserId(), objectEntry,
				ServiceContextTestUtil.getServiceContext()),
			ServiceContextTestUtil.getServiceContext());

		_assertCommentsCount(1, _siteObjectDefinition, objectEntry);
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testRestoreObjectEntryFromTrashWithObjectAction()
		throws Exception {

		_addObjectAction(
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
			_siteObjectDefinition);

		Group group = GroupTestUtil.addGroup();

		ObjectEntry objectEntry =
			_objectEntryLocalService.moveObjectEntryToTrash(
				TestPropsValues.getUserId(),
				_addObjectEntry(
					group.getGroupId(),
					_siteObjectDefinition.getObjectDefinitionId(),
					Collections.emptyMap()),
				ServiceContextTestUtil.getServiceContext());

		_assertCount(1);

		ThreadLocal<Map<Long, Set<Long>>> threadLocal =
			ReflectionTestUtil.getFieldValue(
				ObjectActionThreadLocal.class, "_objectEntryIdsMap");

		threadLocal.set(new HashMap<>());

		_objectEntryLocalService.restoreObjectEntryFromTrash(
			TestPropsValues.getUserId(), objectEntry,
			ServiceContextTestUtil.getServiceContext());

		_assertCount(2);
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testRestoreObjectEntryFromTrashWithObjectEntryFolder()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		ObjectEntryFolder objectEntryFolder =
			ObjectEntryFolderTestUtil.addObjectEntryFolder(group.getGroupId());

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			group.getGroupId(), _siteObjectDefinition.getObjectDefinitionId(),
			objectEntryFolder.getObjectEntryFolderId(), Collections.emptyMap());

		objectEntry = _objectEntryLocalService.restoreObjectEntryFromTrash(
			TestPropsValues.getUserId(),
			_objectEntryLocalService.moveObjectEntryToTrash(
				TestPropsValues.getUserId(), objectEntry,
				ServiceContextTestUtil.getServiceContext()),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			objectEntryFolder.getObjectEntryFolderId(),
			objectEntry.getObjectEntryFolderId());

		objectEntry = _objectEntryLocalService.moveObjectEntryToTrash(
			TestPropsValues.getUserId(), objectEntry,
			ServiceContextTestUtil.getServiceContext());

		_objectEntryFolderLocalService.deleteObjectEntryFolder(
			objectEntryFolder.getObjectEntryFolderId());

		objectEntry = _objectEntryLocalService.restoreObjectEntryFromTrash(
			TestPropsValues.getUserId(), objectEntry,
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			objectEntry.getObjectEntryFolderId());

		Assert.assertNull(
			_trashEntryLocalService.fetchEntry(
				_siteObjectDefinition.getClassName(),
				objectEntry.getObjectEntryId()));
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testRestoreObjectEntryFromTrashWithOngoingWorkflowInstances()
		throws Exception {

		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(), 0,
			_siteObjectDefinition.getClassName(), 0, 0, "Single Approver", 1);

		Group group = GroupTestUtil.addGroup();

		ObjectEntry objectEntry = _addObjectEntry(
			group.getGroupId(), _siteObjectDefinition.getObjectDefinitionId(),
			Collections.emptyMap());

		_assertGetWorkflowInstancesSize(
			_siteObjectDefinition.getClassName(),
			objectEntry.getObjectEntryId(), 1);

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, objectEntry.getStatus());

		objectEntry = _objectEntryLocalService.restoreObjectEntryFromTrash(
			TestPropsValues.getUserId(),
			_objectEntryLocalService.moveObjectEntryToTrash(
				TestPropsValues.getUserId(), objectEntry,
				ServiceContextTestUtil.getServiceContext()),
			ServiceContextTestUtil.getServiceContext());

		_assertGetWorkflowInstancesSize(
			_siteObjectDefinition.getClassName(),
			objectEntry.getObjectEntryId(), 0);

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, objectEntry.getStatus());

		objectEntry = _addOrUpdateObjectEntry(
			objectEntry.getExternalReferenceCode(), group.getGroupId(),
			_siteObjectDefinition.getObjectDefinitionId(),
			Collections.emptyMap());

		_assertGetWorkflowInstancesSize(
			_siteObjectDefinition.getClassName(),
			objectEntry.getObjectEntryId(), 1);

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, objectEntry.getStatus());

		_completeWorkflowTask(Constants.APPROVE);

		objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntry.getObjectEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, objectEntry.getStatus());

		objectEntry = _objectEntryLocalService.restoreObjectEntryFromTrash(
			TestPropsValues.getUserId(),
			_objectEntryLocalService.moveObjectEntryToTrash(
				TestPropsValues.getUserId(), objectEntry,
				ServiceContextTestUtil.getServiceContext()),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, objectEntry.getStatus());
	}

	@Test
	public void testScope() throws Exception {

		// Scope by company

		DepotEntry depotEntry1 = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_testScope(0, ObjectDefinitionConstants.SCOPE_COMPANY, true);
		_testScope(
			depotEntry1.getGroupId(), ObjectDefinitionConstants.SCOPE_COMPANY,
			false);
		_testScope(
			TestPropsValues.getGroupId(),
			ObjectDefinitionConstants.SCOPE_COMPANY, false);

		// Scope by depot

		_testScope(0, ObjectDefinitionConstants.SCOPE_DEPOT, false);
		_testScope(
			depotEntry1.getGroupId(), ObjectDefinitionConstants.SCOPE_DEPOT,
			true);
		_testScope(
			TestPropsValues.getGroupId(), ObjectDefinitionConstants.SCOPE_DEPOT,
			false);

		ObjectDefinition depotObjectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).name(
						"a" + RandomTestUtil.randomString()
					).build()),
				ObjectDefinitionConstants.SCOPE_DEPOT);

		AssertUtils.assertFailure(
			NoSuchObjectDefinitionException.class, null,
			() -> _objectEntryLocalService.addObjectEntry(
				depotEntry1.getGroupId(), TestPropsValues.getUserId(),
				depotObjectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null, Collections.emptyMap(),
				ServiceContextTestUtil.getServiceContext()));

		ObjectDefinitionSetting objectDefinitionSetting =
			_objectDefinitionSettingLocalService.addObjectDefinitionSetting(
				depotObjectDefinition.getUserId(),
				depotObjectDefinition.getObjectDefinitionId(),
				ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS,
				String.valueOf(depotEntry1.getGroupId()));

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			depotEntry1.getGroupId(), TestPropsValues.getUserId(),
			depotObjectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, Collections.emptyMap(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(depotEntry1.getGroupId(), objectEntry.getGroupId());

		DepotEntry depotEntry2 = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		AssertUtils.assertFailure(
			NoSuchObjectDefinitionException.class, null,
			() -> _objectEntryLocalService.addObjectEntry(
				depotEntry2.getGroupId(), TestPropsValues.getUserId(),
				depotObjectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null, Collections.emptyMap(),
				ServiceContextTestUtil.getServiceContext()));

		objectDefinitionSetting.setValue(
			StringBundler.concat(
				depotEntry1.getGroupId(), StringPool.COMMA,
				depotEntry2.getGroupId()));

		_objectDefinitionSettingLocalService.updateObjectDefinitionSetting(
			objectDefinitionSetting);

		objectEntry = _objectEntryLocalService.addObjectEntry(
			depotEntry2.getGroupId(), TestPropsValues.getUserId(),
			depotObjectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, Collections.emptyMap(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(depotEntry2.getGroupId(), objectEntry.getGroupId());

		_objectDefinitionLocalService.deleteObjectDefinition(
			depotObjectDefinition);

		// Scope by site

		_testScope(0, ObjectDefinitionConstants.SCOPE_SITE, false);
		_testScope(
			depotEntry1.getGroupId(), ObjectDefinitionConstants.SCOPE_SITE,
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
			24, values1, _getValuesFromDatabase(objectEntries.get(0)));

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
			24, values1, _getValuesFromDatabase(objectEntries.get(0)));
		_assertObjectEntryValues(
			24, values2, _getValuesFromDatabase(objectEntries.get(1)));

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
			24, values1, _getValuesFromDatabase(objectEntries.get(0)));
		_assertObjectEntryValues(
			24, values2, _getValuesFromDatabase(objectEntries.get(1)));
		_assertObjectEntryValues(
			24, values3, _getValuesFromDatabase(objectEntries.get(2)));

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
			).localized(
				true
			).name(
				"a" + RandomTestUtil.randomString()
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).build());

		_objectDefinitionLocalService.updateTitleObjectFieldId(
			_objectDefinition.getObjectDefinitionId(),
			objectField.getObjectFieldId());

		Map<String, String> localizedValues = HashMapBuilder.put(
			"en_US", RandomTestUtil.randomString()
		).put(
			"pt_BR", RandomTestUtil.randomString()
		).build();

		objectEntry = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "john@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).put(
				objectField.getI18nObjectFieldName(),
				(Serializable)localizedValues
			).build());

		assetEntry = _assetEntryLocalService.fetchEntry(
			_objectDefinition.getClassName(), objectEntry.getObjectEntryId());

		Assert.assertEquals(
			_localization.getXml(
				localizedValues, objectEntry.getDefaultLanguageId(), "title"),
			assetEntry.getTitle());
	}

	@Test
	public void testUpdateCommerceOrderSystemObjectDefinitionWithJavaDelegateObjectValidationRule()
		throws Exception {

		Consumer<Map<String, Object>> consumer = inputObjects -> {
			Map<String, Object> entryDTO =
				(Map<String, Object>)inputObjects.get("entryDTO");

			Assert.assertTrue(entryDTO.containsKey("customFieldName"));
		};

		try (Closeable closeable = _registerTestObjectValidationRuleEngine(
				consumer, _OBJECT_VALIDATION_RULE_KEY)) {

			ExpandoTable expandoTable = ExpandoTestUtil.addTable(
				PortalUtil.getClassNameId(CommerceOrder.class),
				ExpandoTableConstants.DEFAULT_TABLE_NAME);

			ExpandoTestUtil.addColumn(
				expandoTable, "customFieldName", ExpandoColumnConstants.STRING);

			Group group = GroupTestUtil.addGroup();

			CommerceCurrency commerceCurrency =
				CommerceCurrencyTestUtil.addCommerceCurrency(
					group.getCompanyId());

			CommerceChannel commerceChannel =
				CommerceTestUtil.addCommerceChannel(
					group.getGroupId(), commerceCurrency.getCode());

			CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
				TestPropsValues.getUserId(), commerceChannel.getGroupId(),
				commerceCurrency);

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.fetchSystemObjectDefinition(
					TestPropsValues.getCompanyId(), "CommerceOrder");

			ObjectValidationRule objectValidationRule =
				_addObjectValidationRule(
					_OBJECT_VALIDATION_RULE_KEY, objectDefinition,
					StringPool.BLANK);

			CommerceOrderLocalServiceUtil.updateCommerceOrder(commerceOrder);

			_objectValidationRuleLocalService.deleteObjectValidationRule(
				objectValidationRule);
		}
	}

	@Test
	@TestInfo("LPD-53245")
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
			objectEntry.getObjectEntryFolderId(),
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
		Assert.assertEquals(values.toString(), 24, values.size());

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
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"ageOfDeath", "94"
			).put(
				"authorOfGospel", true
			).put(
				"birthday", birthdayDate
			).put(
				"emailAddressRequired", "joao@liferay.com"
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
			"joao@liferay.com", values.get("emailAddressRequired"));
		Assert.assertEquals(StringPool.BLANK, values.get("firstName"));
		Assert.assertEquals(180D, values.get("height"));
		Assert.assertEquals(StringPool.BLANK, values.get("lastName"));
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
		Assert.assertEquals(values.toString(), 24, values.size());

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "charles@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey2"
			).put(
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

		Assert.assertEquals(0L, values.get("ageOfDeath"));
		Assert.assertFalse((boolean)values.get("authorOfGospel"));
		Assert.assertEquals(null, values.get("birthday"));
		Assert.assertEquals(
			"charles@liferay.com", values.get("emailAddressRequired"));
		Assert.assertEquals(StringPool.BLANK, values.get("firstName"));
		Assert.assertEquals(0D, values.get("height"));
		Assert.assertEquals(StringPool.BLANK, values.get("lastName"));
		Assert.assertEquals(StringPool.BLANK, values.get("listTypeEntryKey"));
		Assert.assertEquals(
			"listTypeEntryKey2", values.get("listTypeEntryKeyRequired"));
		Assert.assertEquals(StringPool.BLANK, values.get("middleName"));
		Assert.assertEquals(
			StringPool.BLANK, values.get("multipleListTypeEntriesKey"));
		Assert.assertEquals(0, values.get("numberOfBooksWritten"));
		Assert.assertEquals(StringPool.BLANK, values.get("script"));
		Assert.assertEquals(_getBigDecimal(0L), values.get("speed"));
		Assert.assertEquals("listTypeEntryKey3", values.get("state"));
		Assert.assertEquals(null, values.get("time"));
		Assert.assertEquals(0L, values.get("upload"));
		Assert.assertEquals(65D, values.get("weight"));
		Assert.assertEquals(
			objectEntry.getObjectEntryId(),
			values.get(_objectDefinition.getPKObjectFieldName()));
		Assert.assertEquals(values.toString(), 24, values.size());

		Map<String, Serializable> requiredValues =
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired",
				RandomTestUtil.randomString() + "@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build();

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
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
			).putAll(
				requiredValues
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
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>putAll(
				requiredValues
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				_objectDefinition.getPKObjectFieldName(), ""
			).put(
				"invalidName", ""
			).putAll(
				requiredValues
			).build(),
			ServiceContextTestUtil.getServiceContext());

		long objectEntryId = objectEntry.getObjectEntryId();
		long objectEntryFolderId = objectEntry.getObjectEntryFolderId();

		AssertUtils.assertFailure(
			ObjectEntryValuesException.Required.class,
			"No value was provided for required object field " +
				"\"emailAddressRequired\"",
			() -> _addObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"listTypeEntryKeyRequired", "listTypeEntryKey1"
				).build()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsIntegerSize.class,
			"Object entry value exceeds integer field allowed size",
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId, objectEntryFolderId,
				HashMapBuilder.<String, Serializable>put(
					"numberOfBooksWritten", "2147483648"
				).putAll(
					requiredValues
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsIntegerSize.class,
			"Object entry value exceeds integer field allowed size",
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId, objectEntryFolderId,
				HashMapBuilder.<String, Serializable>put(
					"numberOfBooksWritten", "-2147483649"
				).putAll(
					requiredValues
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsLongMaxSize.class,
			"Object entry value exceeds maximum long field allowed size",
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId, objectEntryFolderId,
				HashMapBuilder.<String, Serializable>put(
					"ageOfDeath", "9007199254740992"
				).putAll(
					requiredValues
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsLongMinSize.class,
			"Object entry value falls below minimum long field allowed size",
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId, objectEntryFolderId,
				HashMapBuilder.<String, Serializable>put(
					"ageOfDeath", "-9007199254740992"
				).putAll(
					requiredValues
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsLongSize.class,
			"Object entry value exceeds long field allowed size",
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId, objectEntryFolderId,
				HashMapBuilder.<String, Serializable>put(
					"ageOfDeath", "9223372036854775808"
				).putAll(
					requiredValues
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsLongSize.class,
			"Object entry value exceeds long field allowed size",
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId, objectEntryFolderId,
				HashMapBuilder.<String, Serializable>put(
					"ageOfDeath", "-9223372036854775809"
				).putAll(
					requiredValues
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		AssertUtils.assertFailure(
			ObjectEntryValuesException.ExceedsTextMaxLength.class,
			"Object entry value exceeds the maximum length of 280 characters " +
				"for object field \"firstName\"",
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId, objectEntryFolderId,
				HashMapBuilder.<String, Serializable>put(
					"firstName", RandomTestUtil.randomString(281)
				).putAll(
					requiredValues
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddress", "james@liferay.com"
			).putAll(
				requiredValues
			).build());

		AssertUtils.assertFailure(
			ObjectEntryValuesException.UniqueValueConstraintViolation.class,
			"Unique value constraint violation for " +
				_objectDefinition.getDBTableName() +
					".emailAddress_ with value james@liferay.com",
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId, objectEntryFolderId,
				HashMapBuilder.<String, Serializable>put(
					"emailAddress", "james@liferay.com"
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		_testUpdateObjectEntryExternalReferenceCode();
		_testUpdateObjectEntryObjectStateTransitions();
		_testUpdateObjectEntryWithObjectRelationship();
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testUpdateObjectEntryWithAttachmentObjectFieldAndEnableObjectEntryVersioning()
		throws Exception {

		Map<String, Serializable> values =
			HashMapBuilder.<String, Serializable>put(
				"attachment",
				() -> {
					DLFileEntry dlFileEntry = _addDLFileEntry();

					return String.valueOf(dlFileEntry.getFileEntryId());
				}
			).put(
				"emailAddressDomain", "@liferay.com"
			).put(
				"emailAddressRequired", "test@liferay.com"
			).put(
				"firstName", "Juan"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build();

		ObjectEntry objectEntry = _addOrUpdateObjectEntry("juan", 0, values);

		long fileEntryId1 = _testDLFileEntryModelListener.getLastFileEntryId();

		_assertObjectEntryValues(
			24, values,
			_objectEntryLocalService.getValues(objectEntry.getObjectEntryId()));

		values = HashMapBuilder.<String, Serializable>put(
			"attachment",
			() -> {
				DLFileEntry dlFileEntry = _addDLFileEntry();

				return String.valueOf(dlFileEntry.getFileEntryId());
			}
		).put(
			"emailAddressDomain", "@liferay.com"
		).put(
			"emailAddressRequired", "test@liferay.com"
		).put(
			"firstName", "Juan"
		).put(
			"listTypeEntryKeyRequired", "listTypeEntryKey1"
		).build();

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(), values,
			ServiceContextTestUtil.getServiceContext());

		long fileEntryId2 = _testDLFileEntryModelListener.getLastFileEntryId();

		_assertObjectEntryValues(
			24, values,
			_objectEntryLocalService.getValues(objectEntry.getObjectEntryId()));

		Assert.assertNull(
			_dlFileEntryLocalService.fetchDLFileEntry(fileEntryId1));

		_enableObjectEntryVersioning();

		values = HashMapBuilder.<String, Serializable>put(
			"attachment",
			() -> {
				DLFileEntry dlFileEntry = _addDLFileEntry();

				return String.valueOf(dlFileEntry.getFileEntryId());
			}
		).put(
			"emailAddressDomain", "@liferay.com"
		).put(
			"emailAddressRequired", "test@liferay.com"
		).put(
			"firstName", "Juan"
		).put(
			"listTypeEntryKeyRequired", "listTypeEntryKey1"
		).build();

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(), values,
			ServiceContextTestUtil.getServiceContext());

		Assert.assertNotNull(
			_dlFileEntryLocalService.fetchDLFileEntry(fileEntryId2));
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
				objectEntry.getObjectEntryFolderId(),
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

			_completeWorkflowTask(Constants.APPROVE);

			_assertObjectEntryStatus(
				WorkflowConstants.STATUS_APPROVED, objectEntry);
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	@Test
	public void testUpdateStatusWithHierarchy() throws Exception {
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

		Assert.assertNotNull(
			WorkflowHandlerRegistryUtil.getWorkflowHandler(
				objectDefinitionA.getClassName()));
		Assert.assertNotNull(
			WorkflowHandlerRegistryUtil.getWorkflowHandler(
				objectDefinitionAA.getClassName()));
		Assert.assertNotNull(
			WorkflowHandlerRegistryUtil.getWorkflowHandler(
				objectDefinitionAAA.getClassName()));

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

		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_APPROVED, objectEntryA);

		ObjectAction objectAction = _addObjectAction(
			objectDefinitionAA,
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE);

		ObjectEntry objectEntryAA = _addObjectEntry(
			0, objectDefinitionAA.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				objectField.getName(), RandomTestUtil.randomString()
			).put(
				"r_objectRelationship1_" +
					objectDefinitionA.getPKObjectFieldName(),
				objectEntryA.getObjectEntryId()
			).build());

		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_APPROVED, objectEntryAA);

		WorkflowDefinitionLink workflowDefinitionLink =
			_updateWorkflowDefinitionLink(objectDefinitionA, "Single Approver");

		ObjectEntry objectEntryAAA = _addObjectEntry(
			0, objectDefinitionAAA.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				objectField.getName(), RandomTestUtil.randomString()
			).put(
				"r_objectRelationship2_" +
					objectDefinitionAA.getPKObjectFieldName(),
				objectEntryAA.getObjectEntryId()
			).build());

		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_PENDING, objectEntryA);
		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_PENDING, objectEntryAA);
		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_PENDING, objectEntryAAA);

		_completeWorkflowTask(Constants.APPROVE);

		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_APPROVED, objectEntryA);
		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_APPROVED, objectEntryAA);
		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_APPROVED, objectEntryAAA);

		_objectEntryLocalService.deleteObjectEntry(
			objectEntryAAA.getObjectEntryId());

		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_PENDING, objectEntryA);
		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_PENDING, objectEntryAA);

		_completeWorkflowTask(Constants.APPROVE);

		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_APPROVED, objectEntryA);
		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_APPROVED, objectEntryAA);

		_assertObjectActionStatus(
			ObjectActionConstants.STATUS_NEVER_RAN, objectAction);

		objectEntryAA = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntryAA.getObjectEntryId(),
			objectEntryAA.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				objectField.getName(), RandomTestUtil.randomString()
			).put(
				"r_objectRelationship1_" +
					objectDefinitionA.getPKObjectFieldName(),
				objectEntryA.getObjectEntryId()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_assertObjectActionStatus(
			ObjectActionConstants.STATUS_SUCCESS, objectAction);

		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_PENDING, objectEntryA);
		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_PENDING, objectEntryAA);

		_workflowDefinitionLinkLocalService.deleteWorkflowDefinitionLink(
			workflowDefinitionLink);

		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_APPROVED,
			_addObjectEntry(
				0, objectDefinitionA.getObjectDefinitionId(),
				HashMapBuilder.<String, Serializable>put(
					objectField.getName(), RandomTestUtil.randomString()
				).build()));

		objectEntryAAA = _addObjectEntry(
			0, objectDefinitionAAA.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				objectField.getName(), RandomTestUtil.randomString()
			).put(
				"r_objectRelationship2_" +
					objectDefinitionAA.getPKObjectFieldName(),
				objectEntryAA.getObjectEntryId()
			).build());

		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_PENDING, objectEntryA);
		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_PENDING, objectEntryAA);
		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_PENDING, objectEntryAAA);

		_completeWorkflowTask(Constants.APPROVE);

		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_APPROVED, objectEntryA);
		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_APPROVED, objectEntryAA);
		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_APPROVED, objectEntryAAA);

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {
				objectDefinitionA.getName(), objectDefinitionAA.getName(),
				objectDefinitionAAA.getName()
			},
			_objectEntryLocalService, _objectRelationshipLocalService);
	}

	@Test
	public void testUpdateStatusWithHierarchyAndObjectEntryAsDraft()
		throws Exception {

		ObjectDefinition objectDefinitionA =
			ObjectDefinitionTestUtil.publishObjectDefinition();
		ObjectDefinition objectDefinitionAA =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		_updateEnableObjectEntryDraft(objectDefinitionA);
		_updateEnableObjectEntryDraft(objectDefinitionAA);

		_updateWorkflowDefinitionLink(objectDefinitionA, "Single Approver");

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			Collections.singletonList(
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService, objectDefinitionA,
					objectDefinitionAA,
					ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
					"objectRelationship")));

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		ObjectEntry objectEntryA = _addObjectEntry(
			objectDefinitionA, Collections.emptyMap(), serviceContext);

		ObjectEntry objectEntryAA1 = _addObjectEntry(
			objectDefinitionAA,
			HashMapBuilder.<String, Serializable>put(
				"r_objectRelationship_" +
					objectDefinitionA.getPKObjectFieldName(),
				objectEntryA.getObjectEntryId()
			).build(),
			serviceContext);
		ObjectEntry objectEntryAA2 = _addObjectEntry(
			objectDefinitionAA,
			HashMapBuilder.<String, Serializable>put(
				"r_objectRelationship_" +
					objectDefinitionA.getPKObjectFieldName(),
				objectEntryA.getObjectEntryId()
			).build(),
			serviceContext);

		_assertObjectEntryStatus(WorkflowConstants.STATUS_DRAFT, objectEntryA);

		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_DRAFT, objectEntryAA1);
		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_DRAFT, objectEntryAA2);

		_objectEntryLocalService.deleteObjectEntry(
			objectEntryAA2.getObjectEntryId());

		_assertObjectEntryStatus(WorkflowConstants.STATUS_DRAFT, objectEntryA);
		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_DRAFT, objectEntryAA1);

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		objectEntryAA2 = _addObjectEntry(
			objectDefinitionAA,
			HashMapBuilder.<String, Serializable>put(
				"r_objectRelationship_" +
					objectDefinitionA.getPKObjectFieldName(),
				objectEntryA.getObjectEntryId()
			).build(),
			serviceContext);

		_assertObjectEntryStatus(WorkflowConstants.STATUS_DRAFT, objectEntryA);
		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_DRAFT, objectEntryAA1);
		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_DRAFT, objectEntryAA2);

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntryA.getObjectEntryId(),
			objectEntryA.getObjectEntryFolderId(), Collections.emptyMap(),
			serviceContext);

		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_PENDING, objectEntryA);
		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_PENDING, objectEntryAA1);
		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_PENDING, objectEntryAA2);

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		AssertUtils.assertFailure(
			ObjectEntryStatusException.class,
			"Draft root descendant nodes cannot be added when the root node " +
				"has incomplete workflow instance",
			() -> _addObjectEntry(
				objectDefinitionAA,
				HashMapBuilder.<String, Serializable>put(
					"r_objectRelationship_" +
						objectDefinitionA.getPKObjectFieldName(),
					objectEntryA.getObjectEntryId()
				).build(),
				serviceContext));

		_completeWorkflowTask(Constants.APPROVE);

		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_APPROVED, objectEntryA);
		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_APPROVED, objectEntryAA1);
		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_APPROVED, objectEntryAA2);

		ObjectEntry objectEntryAA3 = _addObjectEntry(
			objectDefinitionAA,
			HashMapBuilder.<String, Serializable>put(
				"r_objectRelationship_" +
					objectDefinitionA.getPKObjectFieldName(),
				objectEntryA.getObjectEntryId()
			).build(),
			serviceContext);

		_assertObjectEntryStatus(WorkflowConstants.STATUS_DRAFT, objectEntryA);
		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_DRAFT, objectEntryAA1);
		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_DRAFT, objectEntryAA2);
		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_DRAFT, objectEntryAA3);

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {
				objectDefinitionA.getName(), objectDefinitionAA.getName()
			},
			_objectEntryLocalService, _objectRelationshipLocalService);
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
	public void testUpdateUserSystemObjectWithJavaDelegateObjectValidationRule()
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

		try (Closeable closeable = _registerTestObjectValidationRuleEngine(
				consumer, _OBJECT_VALIDATION_RULE_KEY)) {

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

			User user = UserTestUtil.addUser(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				RandomTestUtil.randomString(
					NumericStringRandomizerBumper.INSTANCE,
					UniqueStringRandomizerBumper.INSTANCE),
				serviceContext.getLocale(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				new long[] {serviceContext.getScopeGroupId()}, serviceContext);

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.fetchSystemObjectDefinition(
					TestPropsValues.getCompanyId(), "User");

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
				_addObjectValidationRule(
					_OBJECT_VALIDATION_RULE_KEY, objectDefinition,
					StringPool.BLANK);

			UserTestUtil.updateUser(user);

			_objectValidationRuleLocalService.deleteObjectValidationRule(
				objectValidationRule);
		}
	}

	@Rule
	public TestName testName = new TestName();

	private Address _addAddress(AccountEntry accountEntry) throws Exception {
		return _addressLocalService.addAddress(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			AccountEntry.class.getName(), accountEntry.getAccountEntryId(), 0,
			0, 0, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			false, RandomTestUtil.randomString(), false,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());
	}

	private void _addComment(
			Group group, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry)
		throws Exception {

		objectDefinition.setEnableComments(true);

		objectDefinition = _objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);

		CommentManagerUtil.addComment(
			TestPropsValues.getUserId(), group.getGroupId(),
			objectDefinition.getClassName(), objectEntry.getObjectEntryId(),
			StringUtil.randomString(),
			new IdentityServiceContextFunction(
				ServiceContextTestUtil.getServiceContext()));
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

	private DLFileEntry _addDLFileEntry() throws Exception {
		Company company = _companyLocalService.getCompanyById(
			TestPropsValues.getCompanyId());
		String content = RandomTestUtil.randomString();

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), company.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			TempFileEntryUtil.getTempFileName(
				RandomTestUtil.randomString() + ".txt"),
			ContentTypes.TEXT_PLAIN, RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			new ByteArrayInputStream(content.getBytes()), 0, null, null, null,
			ServiceContextTestUtil.getServiceContext());

		return _dlFileEntryLocalService.getFileEntry(
			fileEntry.getFileEntryId());
	}

	private ObjectAction _addObjectAction(
			long objectDefinitionId, String objectActionExecutorKey,
			String objectActionTriggerKey,
			UnicodeProperties parametersUnicodeProperties)
		throws Exception {

		return _objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			objectDefinitionId, true, StringPool.BLANK,
			RandomTestUtil.randomString(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(), objectActionExecutorKey,
			objectActionTriggerKey, parametersUnicodeProperties, false);
	}

	private ObjectAction _addObjectAction(
			ObjectDefinition objectDefinition, String objectActionTriggerKey)
		throws Exception {

		return _addObjectAction(
			objectDefinition.getObjectDefinitionId(),
			ObjectActionExecutorConstants.KEY_GROOVY, objectActionTriggerKey,
			UnicodePropertiesBuilder.put(
				"script", "println \"Hello World\""
			).build());
	}

	private void _addObjectAction(
			String objectActionTriggerKey, ObjectDefinition objectDefinition)
		throws Exception {

		_addObjectAction(
			objectDefinition.getObjectDefinitionId(),
			ObjectActionExecutorConstants.KEY_ADD_OBJECT_ENTRY,
			objectActionTriggerKey,
			UnicodePropertiesBuilder.put(
				"objectDefinitionId", _objectDefinition.getObjectDefinitionId()
			).put(
				"predefinedValues",
				JSONUtil.putAll(
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "emailAddressRequired"
					).put(
						"value", RandomTestUtil.randomString()
					),
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "listTypeEntryKeyRequired"
					).put(
						"value", "listTypeEntryKey1"
					),
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "state"
					).put(
						"value", "listTypeEntryKey2"
					)
				).toString()
			).build());
	}

	private ObjectEntry _addObjectEntry(
			long groupId, long objectDefinitionId,
			Map<String, Serializable> values)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			groupId, TestPropsValues.getUserId(), objectDefinitionId,
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, values, ServiceContextTestUtil.getServiceContext());
	}

	private ObjectEntry _addObjectEntry(
			long groupId, ObjectDefinition objectDefinition,
			Map<String, Serializable> values, ServiceContext serviceContext)
		throws PortalException {

		return _objectEntryLocalService.addObjectEntry(
			groupId, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, values, serviceContext);
	}

	private ObjectEntry _addObjectEntry(Map<String, Serializable> values)
		throws Exception {

		return _addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(), values);
	}

	private ObjectEntry _addObjectEntry(
			ObjectDefinition objectDefinition, Map<String, Serializable> values,
			ServiceContext serviceContext)
		throws PortalException {

		return _addObjectEntry(0, objectDefinition, values, serviceContext);
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

	private ObjectValidationRule _addObjectValidationRule(
			String engine, ObjectDefinition objectDefinition, String script)
		throws PortalException {

		return _objectValidationRuleLocalService.addObjectValidationRule(
			StringPool.BLANK, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), true, engine,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION, script,
			false, Collections.emptyList());
	}

	private ObjectEntry _addOrUpdateObjectEntry(
			String externalReferenceCode, long groupId, long objectDefinitionId,
			Map<String, Serializable> values)
		throws Exception {

		return _objectEntryLocalService.addOrUpdateObjectEntry(
			externalReferenceCode, groupId, TestPropsValues.getUserId(),
			objectDefinitionId,
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			values, ServiceContextTestUtil.getServiceContext());
	}

	private ObjectEntry _addOrUpdateObjectEntry(
			String externalReferenceCode, long groupId,
			Map<String, Serializable> values)
		throws Exception {

		return _addOrUpdateObjectEntry(
			externalReferenceCode, groupId,
			_objectDefinition.getObjectDefinitionId(), values);
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

	private void _assertCommentsCount(
		int expectedCount, ObjectDefinition objectDefinition,
		ObjectEntry objectEntry) {

		Assert.assertEquals(
			expectedCount,
			CommentManagerUtil.getCommentsCount(
				objectDefinition.getClassName(),
				objectEntry.getObjectEntryId()));
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

	private void _assertFriendlyURLEntries(
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

		for (FriendlyURLEntry friendlyURLEntry : friendlyURLEntries) {
			Assert.assertEquals(
				objectEntry.getDefaultLanguageId(),
				friendlyURLEntry.getDefaultLanguageId());
		}

		_objectEntryLocalService.deleteObjectEntry(objectEntry);

		friendlyURLEntries =
			_friendlyURLEntryLocalService.getFriendlyURLEntries(
				objectEntry.getNonzeroGroupId(), classNameId,
				objectEntry.getObjectEntryId());

		Assert.assertEquals(
			friendlyURLEntries.toString(), 0, friendlyURLEntries.size());
	}

	private void _assertGetWorkflowInstancesSize(
			String assetClassName, long assetClassPK, int expectedSize)
		throws Exception {

		List<WorkflowInstance> workflowInstances =
			_workflowInstanceManager.getWorkflowInstances(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				assetClassName, assetClassPK, false, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			workflowInstances.toString(), expectedSize,
			workflowInstances.size());
	}

	private void _assertKeywords(String keywords, int count) throws Exception {
		BaseModelSearchResult<ObjectEntry> baseModelSearchResult =
			_objectEntryLocalService.searchObjectEntries(
				0, _objectDefinition.getObjectDefinitionId(), keywords, 0, 20);

		Assert.assertEquals(count, baseModelSearchResult.getLength());
	}

	private void _assertObjectActionStatus(
		int expectedStatus, ObjectAction objectAction) {

		objectAction = _objectActionLocalService.fetchObjectAction(
			objectAction.getObjectActionId());

		Assert.assertEquals(expectedStatus, objectAction.getStatus());
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

	private void _assertObjectEntryStatus(
			int expectedStatus, ObjectEntry objectEntry)
		throws Exception {

		objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntry.getObjectEntryId());

		Assert.assertEquals(expectedStatus, objectEntry.getStatus());
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

	private void _clearValidatedObjectEntryIds() {
		ThreadLocal<Set<Long>> threadLocal = ReflectionTestUtil.getFieldValue(
			ObjectEntryThreadLocal.class, "_validatedObjectEntryIds");

		threadLocal.set(new HashSet<>());
	}

	private void _completeWorkflowTask(String transitionName) throws Exception {
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
			workflowTask.getWorkflowTaskId(), transitionName, StringPool.BLANK,
			null);
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

	private void _enableObjectEntryVersioning() {
		_objectDefinition.setEnableObjectEntryVersioning(true);

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);
	}

	private BigDecimal _getBigDecimal(long value) {
		return BigDecimalUtil.stripTrailingZeros(BigDecimal.valueOf(value));
	}

	private byte[] _getContentBytes(String fileName) throws Exception {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		String content = StringUtil.read(
			classLoader.getResourceAsStream(
				"com/liferay/object/service/test/dependencies/" + fileName));

		return content.getBytes();
	}

	private String _getMultiselectPicklistObjectFieldValue(
		String prefixKey, int size) {

		StringBundler sb = new StringBundler();

		for (int i = 1; i <= size; i++) {
			sb.append(prefixKey + i);

			if (i < size) {
				sb.append(StringPool.COMMA_AND_SPACE);
			}
		}

		return sb.toString();
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
		_objectDefinition = _updateEnableObjectEntryDraft(_objectDefinition);

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
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, values1, serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, objectEntry.getStatus());

		long objectEntryId1 = objectEntry.getObjectEntryId();
		long objectEntryFolderId1 = objectEntry.getObjectEntryFolderId();

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		AssertUtils.assertFailure(
			ObjectEntryValuesException.Required.class,
			"No value was provided for required object field " +
				"\"listTypeEntryKeyRequired\"",
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId1,
				objectEntryFolderId1, values1, serviceContext));

		Map<String, Serializable> values2 =
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", RandomTestUtil.randomString()
			).put(
				"firstName", RandomTestUtil.randomString()
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build();

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntryId1, objectEntryFolderId1,
			values2, serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, objectEntry.getStatus());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		AssertUtils.assertFailure(
			ObjectEntryStatusException.class, "Draft status is not allowed",
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId1,
				objectEntryFolderId1, values2, serviceContext));

		objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, values2, serviceContext);

		_objectDefinition.setEnableObjectEntryDraft(false);

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);

		objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntry.getObjectEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, objectEntry.getStatus());

		long objectEntryId2 = objectEntry.getObjectEntryId();
		long objectEntryFolderId2 = objectEntry.getObjectEntryFolderId();

		AssertUtils.assertFailure(
			ObjectEntryStatusException.class, "Draft status is not allowed",
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId2,
				objectEntryFolderId2, values2, serviceContext));

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntryId2, objectEntryFolderId2,
			values2, serviceContext);

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

	private void _testAddObjectEntryWithObjectEntryScheduleEnabled(
			boolean enableObjectEntryDraft, int status, int workflowAction)
		throws Exception {

		// Add object entry with null display date

		_objectDefinition.setEnableObjectEntryDraft(enableObjectEntryDraft);
		_objectDefinition.setEnableObjectEntrySchedule(true);

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setWorkflowAction(workflowAction);

		Map<String, Serializable> requiredValues =
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired",
				RandomTestUtil.randomString() + "@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build();

		ObjectEntry objectEntry1 = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>putAll(
				requiredValues
			).build(),
			serviceContext);

		Assert.assertEquals(status, objectEntry1.getStatus());

		// Approve pending object entry workflow instance after display date
		// time

		_workflowDefinitionManager.deployWorkflowDefinition(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(), "workflow-definition-1",
			_getContentBytes("workflow-definition-1.json"));

		WorkflowDefinitionLink workflowDefinitionLink =
			_updateWorkflowDefinitionLink(
				_objectDefinition, "workflow-definition-1");

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		ObjectEntry objectEntry2 = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"displayDate",
				new java.sql.Date(
					System.currentTimeMillis() +
						TimeUnit.MILLISECOND.toMillis(100))
			).putAll(
				requiredValues
			).build(),
			serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, objectEntry2.getStatus());

		UnsafeRunnable<Exception> jobExecutorUnsafeRunnable =
			_checkObjectEntrySchedulerJobConfiguration.
				getJobExecutorUnsafeRunnable();

		jobExecutorUnsafeRunnable.run();

		objectEntry2 = _objectEntryLocalService.getObjectEntry(
			objectEntry2.getObjectEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, objectEntry2.getStatus());

		_completeWorkflowTask(Constants.APPROVE);

		objectEntry2 = _objectEntryLocalService.getObjectEntry(
			objectEntry2.getObjectEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, objectEntry2.getStatus());

		// Approve pending object entry workflow instance before display date
		// time

		ObjectEntry objectEntry3 = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"displayDate",
				new java.sql.Date(
					System.currentTimeMillis() +
						TimeUnit.MILLISECOND.toMillis(1000))
			).putAll(
				requiredValues
			).build(),
			serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, objectEntry3.getStatus());

		_completeWorkflowTask(Constants.APPROVE);

		objectEntry3 = _objectEntryLocalService.getObjectEntry(
			objectEntry3.getObjectEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_SCHEDULED, objectEntry3.getStatus());

		Thread.sleep(1000);

		jobExecutorUnsafeRunnable.run();

		objectEntry3 = _objectEntryLocalService.getObjectEntry(
			objectEntry3.getObjectEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, objectEntry3.getStatus());

		// Reject pending object entry workflow instance after display date time

		ObjectEntry objectEntry4 = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"displayDate",
				new java.sql.Date(
					System.currentTimeMillis() +
						TimeUnit.MILLISECOND.toMillis(100))
			).putAll(
				requiredValues
			).build(),
			serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, objectEntry4.getStatus());

		jobExecutorUnsafeRunnable.run();

		objectEntry4 = _objectEntryLocalService.getObjectEntry(
			objectEntry4.getObjectEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, objectEntry4.getStatus());

		_completeWorkflowTask(Constants.REJECT);

		objectEntry4 = _objectEntryLocalService.getObjectEntry(
			objectEntry4.getObjectEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_DENIED, objectEntry4.getStatus());

		// Reject pending object entry workflow instance before display date
		// time

		ObjectEntry objectEntry5 = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"displayDate",
				new java.sql.Date(
					System.currentTimeMillis() +
						TimeUnit.MILLISECOND.toMillis(1000))
			).putAll(
				requiredValues
			).build(),
			serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, objectEntry5.getStatus());

		_completeWorkflowTask(Constants.REJECT);

		objectEntry5 = _objectEntryLocalService.getObjectEntry(
			objectEntry5.getObjectEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_DENIED, objectEntry5.getStatus());

		Thread.sleep(1000);

		jobExecutorUnsafeRunnable.run();

		objectEntry5 = _objectEntryLocalService.getObjectEntry(
			objectEntry5.getObjectEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_DENIED, objectEntry5.getStatus());

		_workflowDefinitionLinkLocalService.deleteWorkflowDefinitionLink(
			workflowDefinitionLink);

		// Update object entry with display date in the future

		serviceContext.setWorkflowAction(workflowAction);

		ObjectEntry objectEntry6 = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>putAll(
				requiredValues
			).build(),
			serviceContext);

		objectEntry6 = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry6.getObjectEntryId(),
			objectEntry6.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"displayDate",
				new java.sql.Date(
					System.currentTimeMillis() + TimeUnit.DAY.toMillis(1))
			).putAll(
				requiredValues
			).build(),
			serviceContext);

		if (workflowAction == WorkflowConstants.ACTION_PUBLISH) {
			Assert.assertEquals(
				WorkflowConstants.STATUS_SCHEDULED, objectEntry6.getStatus());
		}
		else {
			Assert.assertEquals(status, objectEntry6.getStatus());
		}

		// Update object entry with display date in the past

		objectEntry6 = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry6.getObjectEntryId(),
			objectEntry6.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"displayDate",
				new java.sql.Date(
					System.currentTimeMillis() - TimeUnit.DAY.toMillis(1))
			).putAll(
				requiredValues
			).build(),
			serviceContext);

		Assert.assertEquals(status, objectEntry6.getStatus());

		_objectDefinition.setEnableObjectEntryDraft(false);
		_objectDefinition.setEnableObjectEntrySchedule(false);

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);
	}

	private void _testPartialUpdateObjectEntryExternalReferenceCode()
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
		long objectEntryFolderId1 = objectEntry1.getObjectEntryFolderId();

		objectEntry1 = _objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(), objectEntryId1, objectEntryFolderId1,
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
		long objectEntryFolderId2 = objectEntry2.getObjectEntryFolderId();

		AssertUtils.assertFailure(
			DuplicateObjectEntryExternalReferenceCodeException.class,
			StringBundler.concat(
				"Duplicate object entry with external reference code ",
				"newExternalReferenceCode, group ID 0, and object definition ",
				"ID ", _objectDefinition.getObjectDefinitionId()),
			() -> _objectEntryLocalService.partialUpdateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId2,
				objectEntryFolderId2,
				HashMapBuilder.<String, Serializable>put(
					"externalReferenceCode", "newExternalReferenceCode"
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		objectEntry2 = _objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(), objectEntryId2, objectEntryFolderId2,
			HashMapBuilder.<String, Serializable>put(
				"externalReferenceCode", ""
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			objectEntry2.getUuid(), objectEntry2.getExternalReferenceCode());

		_objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(), objectEntryId2, objectEntryFolderId2,
			HashMapBuilder.<String, Serializable>put(
				"externalReferenceCode", objectEntry1.getUuid()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		AssertUtils.assertFailure(
			DuplicateObjectEntryExternalReferenceCodeException.class,
			StringBundler.concat(
				"Duplicate object entry with external reference code ",
				objectEntry1.getUuid(), ", group ID 0, and object definition ",
				"ID ", _objectDefinition.getObjectDefinitionId()),
			() -> _objectEntryLocalService.partialUpdateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId1,
				objectEntryFolderId1,
				HashMapBuilder.<String, Serializable>put(
					"externalReferenceCode", ""
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		String randomString = RandomTestUtil.randomString();

		objectEntry1 = _objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(), objectEntryId1, objectEntryFolderId1,
			HashMapBuilder.<String, Serializable>put(
				"externalReferenceCode", randomString
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			randomString, objectEntry1.getExternalReferenceCode());

		_objectEntryLocalService.deleteObjectEntry(objectEntryId1);
		_objectEntryLocalService.deleteObjectEntry(objectEntryId2);
	}

	private void _testPartialUpdateObjectEntryObjectStateTransitions()
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

		objectEntry = _objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
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
		long objectEntryFolderId = objectEntry.getObjectEntryFolderId();

		AssertUtils.assertFailure(
			ObjectEntryValuesException.InvalidObjectStateTransition.class,
			StringBundler.concat(
				"Object state ID ",
				objectStateListTypeEntryKey1.getObjectStateId(),
				" cannot be transitioned to object state ID ",
				objectStateListTypeEntryKey3.getObjectStateId()),
			() -> _objectEntryLocalService.partialUpdateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId, objectEntryFolderId,
				HashMapBuilder.<String, Serializable>put(
					"state", "listTypeEntryKey3"
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		objectEntry = _objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
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
			() -> _objectEntryLocalService.partialUpdateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId, objectEntryFolderId,
				HashMapBuilder.<String, Serializable>put(
					"state", "listTypeEntryKey3"
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		_objectEntryLocalService.deleteObjectEntry(
			objectEntry.getObjectEntryId());
	}

	private void _testPartialUpdateObjectEntryWithObjectRelationship()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				"RelatedObjectDefinition",
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"name"
					).build()),
				ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectRelationshipTestUtil.addObjectRelationship(
			_objectRelationshipLocalService, objectDefinition,
			_objectDefinition,
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
			"objectRelationship1");

		ObjectRelationshipTestUtil.addObjectRelationship(
			_objectRelationshipLocalService, objectDefinition,
			_objectDefinition,
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
			"objectRelationship2");

		ObjectEntry objectEntry1 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired", "carlos@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build());

		long objectEntryId1 = objectEntry1.getObjectEntryId();
		long objectEntryFolderId1 = objectEntry1.getObjectEntryFolderId();

		objectEntry1 = _objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(), objectEntryId1, objectEntryFolderId1,
			HashMapBuilder.<String, Serializable>put(
				"firstName", "Charles"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Map<String, Serializable> values = objectEntry1.getValues();

		Assert.assertEquals("Charles", values.get("firstName"));
		Assert.assertEquals(
			StringPool.BLANK,
			values.get("r_objectRelationship1_c_relatedObjectDefinitionERC"));
		Assert.assertEquals(
			0L,
			values.get("r_objectRelationship1_c_relatedObjectDefinitionId"));
		Assert.assertEquals(
			StringPool.BLANK,
			values.get("r_objectRelationship2_c_relatedObjectDefinitionERC"));
		Assert.assertEquals(
			0L,
			values.get("r_objectRelationship2_c_relatedObjectDefinitionId"));

		ObjectEntry objectEntry2 = _addObjectEntry(
			0, objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"name", RandomTestUtil.randomString()
			).build());

		ObjectEntry objectEntry3 = _addObjectEntry(
			0, objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"name", RandomTestUtil.randomString()
			).build());

		objectEntry1 = _objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(), objectEntryId1, objectEntryFolderId1,
			HashMapBuilder.<String, Serializable>put(
				"r_objectRelationship1_c_relatedObjectDefinitionERC",
				objectEntry2.getObjectEntryId()
			).put(
				"r_objectRelationship1_c_relatedObjectDefinitionId",
				objectEntry3.getObjectEntryId()
			).put(
				"r_objectRelationship2_c_relatedObjectDefinitionERC",
				objectEntry3.getObjectEntryId()
			).put(
				"r_objectRelationship2_c_relatedObjectDefinitionId",
				objectEntry2.getObjectEntryId()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		values = objectEntry1.getValues();

		Assert.assertEquals("Charles", values.get("firstName"));
		Assert.assertEquals(
			objectEntry3.getExternalReferenceCode(),
			values.get("r_objectRelationship1_c_relatedObjectDefinitionERC"));
		Assert.assertEquals(
			objectEntry3.getObjectEntryId(),
			values.get("r_objectRelationship1_c_relatedObjectDefinitionId"));
		Assert.assertEquals(
			objectEntry2.getExternalReferenceCode(),
			values.get("r_objectRelationship2_c_relatedObjectDefinitionERC"));
		Assert.assertEquals(
			objectEntry2.getObjectEntryId(),
			values.get("r_objectRelationship2_c_relatedObjectDefinitionId"));

		objectEntry1 = _objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(), objectEntryId1, objectEntryFolderId1,
			HashMapBuilder.<String, Serializable>put(
				"firstName", "Julia"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		values = objectEntry1.getValues();

		Assert.assertEquals("Julia", values.get("firstName"));
		Assert.assertEquals(
			objectEntry3.getExternalReferenceCode(),
			values.get("r_objectRelationship1_c_relatedObjectDefinitionERC"));
		Assert.assertEquals(
			objectEntry3.getObjectEntryId(),
			values.get("r_objectRelationship1_c_relatedObjectDefinitionId"));
		Assert.assertEquals(
			objectEntry2.getExternalReferenceCode(),
			values.get("r_objectRelationship2_c_relatedObjectDefinitionERC"));
		Assert.assertEquals(
			objectEntry2.getObjectEntryId(),
			values.get("r_objectRelationship2_c_relatedObjectDefinitionId"));

		objectEntry1 = _objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(), objectEntryId1, objectEntryFolderId1,
			HashMapBuilder.<String, Serializable>put(
				"firstName", "Zape"
			).put(
				"r_objectRelationship1_c_relatedObjectDefinitionId", 0L
			).put(
				"r_objectRelationship2_c_relatedObjectDefinitionERC",
				StringPool.BLANK
			).build(),
			ServiceContextTestUtil.getServiceContext());

		values = objectEntry1.getValues();

		Assert.assertEquals("Zape", values.get("firstName"));
		Assert.assertEquals(
			StringPool.BLANK,
			values.get("r_objectRelationship1_c_relatedObjectDefinitionERC"));
		Assert.assertEquals(
			0L,
			values.get("r_objectRelationship1_c_relatedObjectDefinitionId"));
		Assert.assertEquals(
			objectEntry2.getExternalReferenceCode(),
			values.get("r_objectRelationship2_c_relatedObjectDefinitionERC"));
		Assert.assertEquals(
			objectEntry2.getObjectEntryId(),
			values.get("r_objectRelationship2_c_relatedObjectDefinitionId"));

		_objectEntryLocalService.deleteObjectEntry(objectEntryId1);
		_objectEntryLocalService.deleteObjectEntry(
			objectEntry2.getObjectEntryId());
		_objectEntryLocalService.deleteObjectEntry(
			objectEntry3.getObjectEntryId());

		_objectDefinitionLocalService.deleteObjectDefinition(
			objectDefinition.getObjectDefinitionId());
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

		if (scope.equals(ObjectDefinitionConstants.SCOPE_DEPOT)) {
			_objectDefinitionSettingLocalService.addObjectDefinitionSetting(
				objectDefinition.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS,
				StringPool.TRUE);
		}

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
				ObjectEntryGroupIdException.class,
				StringBundler.concat(
					"Group ID ", groupId, " is not valid for scope \"", scope,
					"\""),
				() -> _objectEntryLocalService.addObjectEntry(
					groupId, TestPropsValues.getUserId(),
					objectDefinition.getObjectDefinitionId(),
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
					null, Collections.<String, Serializable>emptyMap(),
					ServiceContextTestUtil.getServiceContext()));
		}
		else {
			_objectEntryLocalService.addObjectEntry(
				groupId, TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null, Collections.<String, Serializable>emptyMap(),
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

		Map<String, Serializable> requiredValues =
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired",
				RandomTestUtil.randomString() + "@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build();

		ObjectEntry objectEntry1 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"firstName", "John"
			).putAll(
				requiredValues
			).build());

		Assert.assertEquals(
			objectEntry1.getUuid(), objectEntry1.getExternalReferenceCode());

		long objectEntryId1 = objectEntry1.getObjectEntryId();
		long objectEntryFolderId1 = objectEntry1.getObjectEntryFolderId();

		objectEntry1 = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntryId1, objectEntryFolderId1,
			HashMapBuilder.<String, Serializable>put(
				"externalReferenceCode", "newExternalReferenceCode"
			).putAll(
				requiredValues
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			"newExternalReferenceCode",
			objectEntry1.getExternalReferenceCode());

		ObjectEntry objectEntry2 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"firstName", "Matthew"
			).putAll(
				requiredValues
			).build());

		long objectEntryId2 = objectEntry2.getObjectEntryId();
		long objectEntryFolderId2 = objectEntry2.getObjectEntryFolderId();

		AssertUtils.assertFailure(
			DuplicateObjectEntryExternalReferenceCodeException.class,
			StringBundler.concat(
				"Duplicate object entry with external reference code ",
				"newExternalReferenceCode, group ID 0, and object definition ",
				"ID ", _objectDefinition.getObjectDefinitionId()),
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId2,
				objectEntryFolderId2,
				HashMapBuilder.<String, Serializable>put(
					"externalReferenceCode", "newExternalReferenceCode"
				).putAll(
					requiredValues
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		objectEntry2 = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntryId2, objectEntryFolderId2,
			HashMapBuilder.<String, Serializable>put(
				"externalReferenceCode", ""
			).putAll(
				requiredValues
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			objectEntry2.getUuid(), objectEntry2.getExternalReferenceCode());

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntryId2, objectEntryFolderId2,
			HashMapBuilder.<String, Serializable>put(
				"externalReferenceCode", objectEntry1.getUuid()
			).putAll(
				requiredValues
			).build(),
			ServiceContextTestUtil.getServiceContext());

		AssertUtils.assertFailure(
			DuplicateObjectEntryExternalReferenceCodeException.class,
			StringBundler.concat(
				"Duplicate object entry with external reference code ",
				objectEntry1.getUuid(), ", group ID 0, and object definition ",
				"ID ", _objectDefinition.getObjectDefinitionId()),
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId1,
				objectEntryFolderId1,
				HashMapBuilder.<String, Serializable>put(
					"externalReferenceCode", ""
				).putAll(
					requiredValues
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		String randomString = RandomTestUtil.randomString();

		objectEntry1 = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntryId1, objectEntryFolderId1,
			HashMapBuilder.<String, Serializable>put(
				"externalReferenceCode", randomString
			).putAll(
				requiredValues
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

		Map<String, Serializable> requiredValues =
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired",
				RandomTestUtil.randomString() + "@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build();

		ObjectEntry objectEntry = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"firstName", "John"
			).put(
				"state", "listTypeEntryKey1"
			).putAll(
				requiredValues
			).build());

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"firstName", "Peter"
			).put(
				"state", "listTypeEntryKey1"
			).putAll(
				requiredValues
			).build(),
			ServiceContextTestUtil.getServiceContext());

		ObjectState objectStateListTypeEntryKey1 = objectStates.get(0);
		ObjectState objectStateListTypeEntryKey2 = objectStates.get(1);
		ObjectState objectStateListTypeEntryKey3 = objectStates.get(2);

		long objectEntryId = objectEntry.getObjectEntryId();
		long objectEntryFolderId = objectEntry.getObjectEntryFolderId();

		AssertUtils.assertFailure(
			ObjectEntryValuesException.InvalidObjectStateTransition.class,
			StringBundler.concat(
				"Object state ID ",
				objectStateListTypeEntryKey1.getObjectStateId(),
				" cannot be transitioned to object state ID ",
				objectStateListTypeEntryKey3.getObjectStateId()),
			() -> _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntryId, objectEntryFolderId,
				HashMapBuilder.<String, Serializable>put(
					"state", "listTypeEntryKey3"
				).putAll(
					requiredValues
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"state", "listTypeEntryKey2"
			).putAll(
				requiredValues
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
				TestPropsValues.getUserId(), objectEntryId, objectEntryFolderId,
				HashMapBuilder.<String, Serializable>put(
					"state", "listTypeEntryKey3"
				).putAll(
					requiredValues
				).build(),
				ServiceContextTestUtil.getServiceContext()));

		_objectEntryLocalService.deleteObjectEntry(
			objectEntry.getObjectEntryId());
	}

	private void _testUpdateObjectEntryWithObjectRelationship()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				"RelatedObjectDefinition",
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"name"
					).build()),
				ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectRelationshipTestUtil.addObjectRelationship(
			_objectRelationshipLocalService, objectDefinition,
			_objectDefinition,
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
			"objectRelationship1");

		ObjectRelationshipTestUtil.addObjectRelationship(
			_objectRelationshipLocalService, objectDefinition,
			_objectDefinition,
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
			"objectRelationship2");

		Map<String, Serializable> requiredValues =
			HashMapBuilder.<String, Serializable>put(
				"emailAddressRequired",
				RandomTestUtil.randomString() + "@liferay.com"
			).put(
				"listTypeEntryKeyRequired", "listTypeEntryKey1"
			).build();

		ObjectEntry objectEntry1 = _addObjectEntry(
			HashMapBuilder.<String, Serializable>putAll(
				requiredValues
			).build());

		long objectEntryId1 = objectEntry1.getObjectEntryId();
		long objectEntryFolderId1 = objectEntry1.getObjectEntryFolderId();

		objectEntry1 = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntryId1, objectEntryFolderId1,
			HashMapBuilder.<String, Serializable>put(
				"firstName", "Charles"
			).putAll(
				requiredValues
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Map<String, Serializable> values = objectEntry1.getValues();

		Assert.assertEquals("Charles", values.get("firstName"));
		Assert.assertEquals(
			StringPool.BLANK,
			values.get("r_objectRelationship1_c_relatedObjectDefinitionERC"));
		Assert.assertEquals(
			0L,
			values.get("r_objectRelationship1_c_relatedObjectDefinitionId"));
		Assert.assertEquals(
			StringPool.BLANK,
			values.get("r_objectRelationship2_c_relatedObjectDefinitionERC"));
		Assert.assertEquals(
			0L,
			values.get("r_objectRelationship2_c_relatedObjectDefinitionId"));

		ObjectEntry objectEntry2 = _addObjectEntry(
			0, objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"name", RandomTestUtil.randomString()
			).build());

		ObjectEntry objectEntry3 = _addObjectEntry(
			0, objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"name", RandomTestUtil.randomString()
			).build());

		objectEntry1 = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntryId1, objectEntryFolderId1,
			HashMapBuilder.<String, Serializable>put(
				"r_objectRelationship1_c_relatedObjectDefinitionERC",
				objectEntry2.getObjectEntryId()
			).put(
				"r_objectRelationship1_c_relatedObjectDefinitionId",
				objectEntry3.getObjectEntryId()
			).put(
				"r_objectRelationship2_c_relatedObjectDefinitionERC",
				objectEntry3.getObjectEntryId()
			).put(
				"r_objectRelationship2_c_relatedObjectDefinitionId",
				objectEntry2.getObjectEntryId()
			).putAll(
				requiredValues
			).build(),
			ServiceContextTestUtil.getServiceContext());

		values = objectEntry1.getValues();

		Assert.assertEquals(StringPool.BLANK, values.get("firstName"));
		Assert.assertEquals(
			objectEntry3.getExternalReferenceCode(),
			values.get("r_objectRelationship1_c_relatedObjectDefinitionERC"));
		Assert.assertEquals(
			objectEntry3.getObjectEntryId(),
			values.get("r_objectRelationship1_c_relatedObjectDefinitionId"));
		Assert.assertEquals(
			objectEntry2.getExternalReferenceCode(),
			values.get("r_objectRelationship2_c_relatedObjectDefinitionERC"));
		Assert.assertEquals(
			objectEntry2.getObjectEntryId(),
			values.get("r_objectRelationship2_c_relatedObjectDefinitionId"));

		objectEntry1 = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntryId1, objectEntryFolderId1,
			HashMapBuilder.<String, Serializable>put(
				"firstName", "Julia"
			).putAll(
				requiredValues
			).build(),
			ServiceContextTestUtil.getServiceContext());

		values = objectEntry1.getValues();

		Assert.assertEquals("Julia", values.get("firstName"));
		Assert.assertEquals(
			objectEntry3.getExternalReferenceCode(),
			values.get("r_objectRelationship1_c_relatedObjectDefinitionERC"));
		Assert.assertEquals(
			objectEntry3.getObjectEntryId(),
			values.get("r_objectRelationship1_c_relatedObjectDefinitionId"));
		Assert.assertEquals(
			objectEntry2.getExternalReferenceCode(),
			values.get("r_objectRelationship2_c_relatedObjectDefinitionERC"));
		Assert.assertEquals(
			objectEntry2.getObjectEntryId(),
			values.get("r_objectRelationship2_c_relatedObjectDefinitionId"));

		objectEntry1 = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntryId1, objectEntryFolderId1,
			HashMapBuilder.<String, Serializable>put(
				"firstName", "Zape"
			).put(
				"r_objectRelationship1_c_relatedObjectDefinitionId", 0L
			).put(
				"r_objectRelationship2_c_relatedObjectDefinitionERC",
				StringPool.BLANK
			).putAll(
				requiredValues
			).build(),
			ServiceContextTestUtil.getServiceContext());

		values = objectEntry1.getValues();

		Assert.assertEquals("Zape", values.get("firstName"));
		Assert.assertEquals(
			StringPool.BLANK,
			values.get("r_objectRelationship1_c_relatedObjectDefinitionERC"));
		Assert.assertEquals(
			0L,
			values.get("r_objectRelationship1_c_relatedObjectDefinitionId"));
		Assert.assertEquals(
			objectEntry2.getExternalReferenceCode(),
			values.get("r_objectRelationship2_c_relatedObjectDefinitionERC"));
		Assert.assertEquals(
			objectEntry2.getObjectEntryId(),
			values.get("r_objectRelationship2_c_relatedObjectDefinitionId"));

		_objectEntryLocalService.deleteObjectEntry(objectEntryId1);
		_objectEntryLocalService.deleteObjectEntry(
			objectEntry2.getObjectEntryId());
		_objectEntryLocalService.deleteObjectEntry(
			objectEntry3.getObjectEntryId());

		_objectDefinitionLocalService.deleteObjectDefinition(
			objectDefinition.getObjectDefinitionId());
	}

	private ObjectDefinition _updateEnableObjectEntryDraft(
		ObjectDefinition objectDefinition) {

		objectDefinition.setEnableObjectEntryDraft(true);

		return _objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);
	}

	private ObjectValidationRule _updateObjectValidationRule(
		ObjectValidationRule objectValidationRule, String script) {

		objectValidationRule.setScript(script);

		return _objectValidationRuleLocalService.updateObjectValidationRule(
			objectValidationRule);
	}

	private WorkflowDefinitionLink _updateWorkflowDefinitionLink(
			ObjectDefinition objectDefinition, String workflowDefinitionName)
		throws Exception {

		return _workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(), 0,
			objectDefinition.getClassName(), 0, 0, workflowDefinitionName, 1);
	}

	private static final String _OBJECT_VALIDATION_RULE_KEY =
		ObjectValidationRuleConstants.ENGINE_TYPE_JAVA_DELEGATE_PREFIX +
			RandomTestUtil.randomString();

	@Inject(
		filter = "component.name=com.liferay.object.web.internal.scheduler.CheckObjectEntrySchedulerJobConfiguration"
	)
	private static SchedulerJobConfiguration
		_checkObjectEntrySchedulerJobConfiguration;

	private static ServiceRegistration<?> _serviceRegistration;
	private static final TestDLFileEntryModelListener
		_testDLFileEntryModelListener = new TestDLFileEntryModelListener();

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AddressLocalService _addressLocalService;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private AssetTagLocalService _assetTagLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

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
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _irrelevantObjectDefinition;

	@DeleteAfterTestRun
	private ListTypeDefinition _listTypeDefinition;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Inject
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Inject
	private Localization _localization;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

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

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

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
	private Portal _portal;

	@Inject
	private ResourceActions _resourceActions;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _siteObjectDefinition;

	@Inject
	private SystemEventLocalService _systemEventLocalService;

	@Inject(
		filter = "component.name=com.liferay.document.library.web.internal.scheduler.TempFileEntriesSchedulerJobConfiguration"
	)
	private SchedulerJobConfiguration _tempFileEntriesSchedulerJobConfiguration;

	@Inject
	private TrashEntryLocalService _trashEntryLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowDefinitionManager _workflowDefinitionManager;

	@Inject
	private WorkflowInstanceManager _workflowInstanceManager;

	@Inject
	private WorkflowTaskManager _workflowTaskManager;

	private static class TestDLFileEntryModelListener
		extends BaseModelListener<DLFileEntry> {

		public long getLastFileEntryId() {
			return _fileEntryIds.get(_fileEntryIds.size() - 1);
		}

		@Override
		public void onAfterCreate(DLFileEntry dlFileEntry)
			throws ModelListenerException {

			_fileEntryIds.add(dlFileEntry.getFileEntryId());
		}

		private List<Long> _fileEntryIds = new ArrayList<>();

	}

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