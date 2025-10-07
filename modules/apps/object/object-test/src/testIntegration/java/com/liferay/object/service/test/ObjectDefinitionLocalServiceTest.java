/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.definition.setting.builder.ObjectDefinitionSettingBuilder;
import com.liferay.object.exception.NoSuchObjectFieldException;
import com.liferay.object.exception.NoSuchObjectFolderException;
import com.liferay.object.exception.ObjectDefinitionAccountEntryRestrictedException;
import com.liferay.object.exception.ObjectDefinitionActiveException;
import com.liferay.object.exception.ObjectDefinitionClassNameException;
import com.liferay.object.exception.ObjectDefinitionEnableFormContainerException;
import com.liferay.object.exception.ObjectDefinitionEnableFriendlyURLCustomizationException;
import com.liferay.object.exception.ObjectDefinitionEnableLocalizationException;
import com.liferay.object.exception.ObjectDefinitionEnableObjectEntryHistoryException;
import com.liferay.object.exception.ObjectDefinitionEnableObjectEntryScheduleException;
import com.liferay.object.exception.ObjectDefinitionEnableObjectEntrySubscriptionException;
import com.liferay.object.exception.ObjectDefinitionEnableObjectEntryVersioningException;
import com.liferay.object.exception.ObjectDefinitionExternalReferenceCodeException;
import com.liferay.object.exception.ObjectDefinitionFriendlyURLSeparatorException;
import com.liferay.object.exception.ObjectDefinitionLabelException;
import com.liferay.object.exception.ObjectDefinitionModifiableException;
import com.liferay.object.exception.ObjectDefinitionNameException;
import com.liferay.object.exception.ObjectDefinitionPluralLabelException;
import com.liferay.object.exception.ObjectDefinitionScopeException;
import com.liferay.object.exception.ObjectDefinitionSettingNameException;
import com.liferay.object.exception.ObjectDefinitionSettingValueException;
import com.liferay.object.exception.ObjectDefinitionStatusException;
import com.liferay.object.exception.ObjectDefinitionSystemException;
import com.liferay.object.exception.ObjectDefinitionVersionException;
import com.liferay.object.exception.ObjectFieldRelationshipTypeException;
import com.liferay.object.exception.ObjectRelationshipEdgeException;
import com.liferay.object.field.builder.AssigneeObjectFieldBuilder;
import com.liferay.object.field.builder.BooleanObjectFieldBuilder;
import com.liferay.object.field.builder.DateObjectFieldBuilder;
import com.liferay.object.field.builder.DateTimeObjectFieldBuilder;
import com.liferay.object.field.builder.LongIntegerObjectFieldBuilder;
import com.liferay.object.field.builder.ObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.model.ObjectEntryVersionTable;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectActionLocalServiceUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryVersionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.system.BaseSystemObjectDefinitionManager;
import com.liferay.object.system.JaxRsApplicationDescriptor;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.object.test.util.TreeTestUtil;
import com.liferay.object.tree.Node;
import com.liferay.object.tree.ObjectDefinitionTreeFactory;
import com.liferay.object.tree.Tree;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.model.UserNotificationEventTable;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.service.PLOEntryLocalService;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.service.SharingEntryLocalService;

import java.io.Serializable;

import java.sql.Connection;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
@FeatureFlags(
	featureFlags = {
		@FeatureFlag(value = "LPD-17564"), @FeatureFlag(value = "LPD-21926"),
		@FeatureFlag("LPD-34594")
	}
)
@RunWith(Arquillian.class)
public class ObjectDefinitionLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_defaultObjectFolder = _objectFolderLocalService.getObjectFolder(
			TestPropsValues.getCompanyId(), ObjectFolderConstants.NAME_DEFAULT);
	}

	@Before
	public void setUp() {
		_objectDefinitionTreeFactory = new ObjectDefinitionTreeFactory(
			_objectDefinitionLocalService, _objectRelationshipLocalService);
	}

	@FeatureFlag("LPD-6233")
	@Test
	public void testAddCustomObjectDefinition() throws Exception {

		// Enable form container

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, true, true,
				false, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList());

		Assert.assertTrue(objectDefinition.isEnableFormContainer());

		// Enable friendly URL customization

		Assert.assertTrue(objectDefinition.isEnableFriendlyURLCustomization());

		AssertUtils.assertFailure(
			ObjectDefinitionEnableFriendlyURLCustomizationException.class,
			"Enable friendly URL customization is only allowed for object " +
				"definitions with the default storage type",
			() -> _objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, true, true,
				false, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_SALESFORCE,
				Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList()));

		// Label is null

		AssertUtils.assertFailure(
			ObjectDefinitionLabelException.class,
			"Label is null for locale " + LocaleUtil.US.getDisplayName(),
			() -> _addCustomObjectDefinition("", "Test", "Tests"));

		// Name

		_objectDefinitionLocalService.deleteObjectDefinition(
			_addCustomObjectDefinition(" Test "));
		_objectDefinitionLocalService.deleteObjectDefinition(
			_addCustomObjectDefinition(
				"A123456789a123456789a123456789a1234567891"));

		AssertUtils.assertFailure(
			ObjectDefinitionNameException.MustBeLessThan41Characters.class,
			"Name must be less than 41 characters",
			() -> _addCustomObjectDefinition(
				"A123456789a123456789a123456789a12345678912"));
		AssertUtils.assertFailure(
			ObjectDefinitionNameException.MustBeginWithUpperCaseLetter.class,
			"The first character of a name must be an upper case letter",
			() -> _addCustomObjectDefinition("test"));

		objectDefinition = _addCustomObjectDefinition("Test");

		objectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		AssertUtils.assertFailure(
			ObjectDefinitionNameException.MustNotBeDuplicate.class,
			"Duplicate name C_Test", () -> _addCustomObjectDefinition("Test"));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		AssertUtils.assertFailure(
			ObjectDefinitionNameException.MustNotBeNull.class, "Name is null",
			() -> _addCustomObjectDefinition("Test", "", "Tests"));
		AssertUtils.assertFailure(
			ObjectDefinitionNameException.MustOnlyContainLettersAndDigits.class,
			"Name must only contain letters and digits",
			() -> _addCustomObjectDefinition("Tes t"));
		AssertUtils.assertFailure(
			ObjectDefinitionNameException.MustOnlyContainLettersAndDigits.class,
			"Name must only contain letters and digits",
			() -> _addCustomObjectDefinition("Tes-t"));

		// Plural label is null

		AssertUtils.assertFailure(
			ObjectDefinitionPluralLabelException.class,
			"Plural label is null for locale " + LocaleUtil.US.getDisplayName(),
			() -> _addCustomObjectDefinition("Test", "Test", ""));

		// Scope is null

		AssertUtils.assertFailure(
			ObjectDefinitionScopeException.class, "Scope is null",
			() -> _objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, false, true,
				false, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test", null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, "", ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())),
				Collections.emptyList()));

		// No object scope provider found with key

		String scope = RandomTestUtil.randomString();

		AssertUtils.assertFailure(
			ObjectDefinitionScopeException.class,
			"No object scope provider found with key " + scope,
			() -> _objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, false, true,
				false, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test", null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, scope, ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())),
				Collections.emptyList()));

		AssertUtils.assertFailure(
			ObjectDefinitionScopeException.class,
			StringBundler.concat(
				"Scope \"", ObjectDefinitionConstants.SCOPE_SITE,
				"\" cannot be associated with storage type \"",
				ObjectDefinitionConstants.STORAGE_TYPE_SALESFORCE),
			() -> _objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, false, true,
				false, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test", null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_SITE,
				ObjectDefinitionConstants.STORAGE_TYPE_SALESFORCE,
				Collections.emptyList(),
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())),
				Collections.emptyList()));

		// Name, database table, resources, and status

		objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, false, true,
				false, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test", null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, "Able", "able",
						false),
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, "Baker", "baker",
						false)),
				Collections.emptyList());

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
		ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap("Charlie")
			).name(
				"charlie"
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).required(
				true
			).userId(
				TestPropsValues.getUserId()
			).build());

		// Custom object definition names are automatically prepended with
		// with "C_"

		Assert.assertEquals("C_Test", objectDefinition.getName());

		// Before publish, database table

		Assert.assertFalse(_hasTable(objectDefinition.getDBTableName()));
		Assert.assertFalse(
			_hasTable(objectDefinition.getExtensionDBTableName()));

		Tree tree = TreeTestUtil.createObjectDefinitionTree(
			_objectDefinitionLocalService, _objectRelationshipLocalService,
			false,
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

		TreeTestUtil.forEachNodeObjectDefinition(
			tree.iterator(), _objectDefinitionLocalService,
			nodeObjectDefinition -> {
				Assert.assertFalse(
					_hasTable(nodeObjectDefinition.getDBTableName()));
				Assert.assertFalse(
					_hasTable(nodeObjectDefinition.getExtensionDBTableName()));
			});

		// Before publish, resources

		Assert.assertEquals(
			0,
			_resourceActionLocalService.getResourceActionsCount(
				objectDefinition.getClassName()));
		Assert.assertEquals(
			0,
			_resourceActionLocalService.getResourceActionsCount(
				objectDefinition.getPortletId()));
		Assert.assertEquals(
			0,
			_resourceActionLocalService.getResourceActionsCount(
				objectDefinition.getResourceName()));
		Assert.assertEquals(
			1,
			_resourcePermissionLocalService.getResourcePermissionsCount(
				objectDefinition.getCompanyId(),
				ObjectDefinition.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectDefinition.getObjectDefinitionId())));

		TreeTestUtil.forEachNodeObjectDefinition(
			tree.iterator(), _objectDefinitionLocalService,
			nodeObjectDefinition -> {
				Assert.assertEquals(
					0,
					_resourceActionLocalService.getResourceActionsCount(
						nodeObjectDefinition.getClassName()));
				Assert.assertEquals(
					0,
					_resourceActionLocalService.getResourceActionsCount(
						nodeObjectDefinition.getPortletId()));
				Assert.assertEquals(
					0,
					_resourceActionLocalService.getResourceActionsCount(
						nodeObjectDefinition.getResourceName()));
				Assert.assertEquals(
					1,
					_resourcePermissionLocalService.getResourcePermissionsCount(
						nodeObjectDefinition.getCompanyId(),
						ObjectDefinition.class.getName(),
						ResourceConstants.SCOPE_INDIVIDUAL,
						String.valueOf(
							nodeObjectDefinition.getObjectDefinitionId())));
			});

		// Before publish, status

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, objectDefinition.getStatus());

		TreeTestUtil.forEachNodeObjectDefinition(
			tree.iterator(), _objectDefinitionLocalService,
			nodeObjectDefinition -> Assert.assertEquals(
				WorkflowConstants.STATUS_DRAFT,
				nodeObjectDefinition.getStatus()));

		// Publish

		objectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).labelMap(
				LocalizedMapUtil.getLocalizedMap("Dog")
			).name(
				"dog"
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).required(
				true
			).build());

		long objectDefinitionId = objectDefinition.getObjectDefinitionId();

		AssertUtils.assertFailure(
			ObjectDefinitionStatusException.class,
			"The object definition is already published",
			() -> _objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(), objectDefinitionId));

		TreeTestUtil.forEachNodeObjectDefinition(
			tree.iterator(), _objectDefinitionLocalService,
			nodeObjectDefinition ->
				_objectDefinitionLocalService.publishCustomObjectDefinition(
					TestPropsValues.getUserId(),
					nodeObjectDefinition.getObjectDefinitionId()));

		// After publish, database table

		Assert.assertFalse(
			_hasColumn(objectDefinition.getDBTableName(), "able"));
		Assert.assertTrue(
			_hasColumn(objectDefinition.getDBTableName(), "able_"));
		Assert.assertFalse(
			_hasColumn(objectDefinition.getDBTableName(), "assignee_"));
		Assert.assertFalse(
			_hasColumn(objectDefinition.getDBTableName(), "baker"));
		Assert.assertTrue(
			_hasColumn(objectDefinition.getDBTableName(), "baker_"));
		Assert.assertFalse(
			_hasColumn(objectDefinition.getDBTableName(), "charlie"));
		Assert.assertTrue(
			_hasColumn(objectDefinition.getDBTableName(), "charlie_"));
		Assert.assertTrue(
			_hasColumn(
				objectDefinition.getDBTableName(), "classNameId_assignee_"));
		Assert.assertTrue(
			_hasColumn(objectDefinition.getDBTableName(), "classPK_assignee_"));
		Assert.assertFalse(
			_hasColumn(objectDefinition.getExtensionDBTableName(), "dog"));
		Assert.assertTrue(
			_hasColumn(objectDefinition.getExtensionDBTableName(), "dog_"));
		Assert.assertTrue(_hasTable(objectDefinition.getDBTableName()));
		Assert.assertTrue(
			_hasTable(objectDefinition.getExtensionDBTableName()));

		_objectFieldLocalService.deleteObjectField(
			_objectFieldLocalService.fetchObjectField(
				objectDefinition.getObjectDefinitionId(), "assignee"));

		Assert.assertFalse(
			_hasColumn(
				objectDefinition.getDBTableName(), "classNameId_assignee_"));
		Assert.assertFalse(
			_hasColumn(objectDefinition.getDBTableName(), "classPK_assignee_"));

		ObjectFieldUtil.addCustomObjectField(
			new AssigneeObjectFieldBuilder(
			).labelMap(
				RandomTestUtil.randomLocaleStringMap()
			).name(
				"newAssignee"
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).userId(
				TestPropsValues.getUserId()
			).build());

		Assert.assertTrue(
			_hasColumn(
				objectDefinition.getExtensionDBTableName(),
				"classNameId_newAssignee_"));
		Assert.assertTrue(
			_hasColumn(
				objectDefinition.getExtensionDBTableName(),
				"classPK_newAssignee_"));

		TreeTestUtil.forEachNodeObjectDefinition(
			tree.iterator(), _objectDefinitionLocalService,
			nodeObjectDefinition -> {
				Assert.assertFalse(
					_hasColumn(nodeObjectDefinition.getDBTableName(), "able"));
				Assert.assertTrue(
					_hasColumn(nodeObjectDefinition.getDBTableName(), "able_"));
			});

		// After publish, resources

		Assert.assertEquals(
			4,
			_resourceActionLocalService.getResourceActionsCount(
				objectDefinition.getClassName()));
		Assert.assertEquals(
			6,
			_resourceActionLocalService.getResourceActionsCount(
				objectDefinition.getPortletId()));
		Assert.assertEquals(
			2,
			_resourceActionLocalService.getResourceActionsCount(
				objectDefinition.getResourceName()));
		Assert.assertEquals(
			1,
			_resourcePermissionLocalService.getResourcePermissionsCount(
				objectDefinition.getCompanyId(),
				ObjectDefinition.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectDefinition.getObjectDefinitionId())));

		TreeTestUtil.forEachNodeObjectDefinition(
			tree.iterator(), _objectDefinitionLocalService,
			nodeObjectDefinition -> {
				Assert.assertEquals(
					4,
					_resourceActionLocalService.getResourceActionsCount(
						nodeObjectDefinition.getClassName()));
				Assert.assertEquals(
					6,
					_resourceActionLocalService.getResourceActionsCount(
						nodeObjectDefinition.getPortletId()));
				Assert.assertEquals(
					2,
					_resourceActionLocalService.getResourceActionsCount(
						nodeObjectDefinition.getResourceName()));

				Assert.assertEquals(
					1,
					_resourcePermissionLocalService.getResourcePermissionsCount(
						nodeObjectDefinition.getCompanyId(),
						ObjectDefinition.class.getName(),
						ResourceConstants.SCOPE_INDIVIDUAL,
						String.valueOf(
							nodeObjectDefinition.getObjectDefinitionId())));
			});

		// After publish, status

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, objectDefinition.getStatus());

		TreeTestUtil.forEachNodeObjectDefinition(
			tree.iterator(), _objectDefinitionLocalService,
			nodeObjectDefinition -> Assert.assertEquals(
				WorkflowConstants.STATUS_APPROVED,
				nodeObjectDefinition.getStatus()));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AAA", "C_AAB", "C_AB"},
			_objectEntryLocalService, _objectRelationshipLocalService);
	}

	@Test
	public void testAddObjectDefinition() throws Exception {
		AssertUtils.assertFailure(
			ObjectDefinitionModifiableException.MustBeModifiable.class,
			"A modifiable object definition is required",
			() -> _objectDefinitionLocalService.addObjectDefinition(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(), 0,
				false, ObjectDefinitionConstants.SCOPE_COMPANY, false));
		AssertUtils.assertFailure(
			ObjectDefinitionModifiableException.MustBeModifiable.class,
			"A modifiable object definition is required",
			() -> _objectDefinitionLocalService.addObjectDefinition(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(), 0,
				false, ObjectDefinitionConstants.SCOPE_COMPANY, true));

		_testAddObjectDefinition(true, false);
		_testAddObjectDefinition(true, true);
	}

	@Test
	public void testAddObjectDefinitionIntoObjectFolder() throws Exception {

		// Object folder does not exist

		long objectFolderId = RandomTestUtil.randomLong();

		AssertUtils.assertFailure(
			NoSuchObjectFolderException.class,
			"No ObjectFolder exists with the primary key " + objectFolderId,
			() -> ObjectDefinitionTestUtil.addCustomObjectDefinition(
				objectFolderId));

		// Add object definition to default object folder

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(0);

		Assert.assertEquals(
			_defaultObjectFolder.getObjectFolderId(),
			objectDefinition.getObjectFolderId());

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		// Add object definition to an existing object folder

		ObjectFolder objectFolder = _addObjectFolder();

		objectDefinition = ObjectDefinitionTestUtil.addCustomObjectDefinition(
			objectFolder.getObjectFolderId());

		Assert.assertEquals(
			objectFolder.getObjectFolderId(),
			objectDefinition.getObjectFolderId());

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		_objectFolderLocalService.deleteObjectFolder(objectFolder);
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testAddObjectDefinitionWithMissingWorkflowDefinitionReference()
		throws Exception {

		WorkflowDefinitionLink workflowDefinitionLink =
			_workflowDefinitionLinkLocalService.createWorkflowDefinitionLink(
				0L);

		workflowDefinitionLink.setUserId(TestPropsValues.getUserId());
		workflowDefinitionLink.setWorkflowDefinitionName(
			RandomTestUtil.randomString());

		List<WorkflowDefinitionLink> workflowDefinitionLinks =
			Collections.singletonList(workflowDefinitionLink);

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				ObjectDefinitionTestUtil.getRandomName(),
				workflowDefinitionLinks);

		workflowDefinitionLink =
			_workflowDefinitionLinkLocalService.getWorkflowDefinitionLink(
				TestPropsValues.getCompanyId(), 0,
				objectDefinition.getClassName(), 0, 0, true);

		Assert.assertNotNull(workflowDefinitionLink);

		KaleoDefinition kaleoDefinition =
			_kaleoDefinitionLocalService.getKaleoDefinition(
				workflowDefinitionLink.getWorkflowDefinitionName(),
				ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, kaleoDefinition.getStatus());

		String content = kaleoDefinition.getContent();

		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.saveWorkflowDefinition(
				kaleoDefinition.getExternalReferenceCode(),
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				kaleoDefinition.getTitle(), kaleoDefinition.getName(),
				kaleoDefinition.getScope(), content.getBytes());

		workflowDefinitionLink =
			_workflowDefinitionLinkLocalService.getWorkflowDefinitionLink(
				TestPropsValues.getCompanyId(), 0,
				objectDefinition.getClassName(), 0, 0, true);

		Assert.assertEquals(
			workflowDefinition.getVersion(),
			workflowDefinitionLink.getWorkflowDefinitionVersion());
	}

	@Test
	public void testAddObjectDefinitionWithObjectDefinitionSettings()
		throws Exception {

		String randomObjectDefinitionName =
			ObjectDefinitionTestUtil.getRandomName();

		AssertUtils.assertFailure(
			ObjectDefinitionSettingNameException.NotAllowedNames.class,
			StringBundler.concat(
				"The settings ",
				ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS,
				" are not allowed for object definition ",
				randomObjectDefinitionName),
			() -> _publishCustomObjectDefinition(
				randomObjectDefinitionName,
				ObjectDefinitionConstants.SCOPE_COMPANY,
				Collections.singletonList(
					new ObjectDefinitionSettingBuilder(
					).name(
						ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS
					).value(
						StringPool.TRUE
					).build())));
		AssertUtils.assertFailure(
			ObjectDefinitionSettingNameException.NotAllowedNames.class,
			StringBundler.concat(
				"The settings ",
				ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS,
				" are not allowed for object definition ",
				randomObjectDefinitionName),
			() -> _publishCustomObjectDefinition(
				randomObjectDefinitionName,
				ObjectDefinitionConstants.SCOPE_SITE,
				Collections.singletonList(
					new ObjectDefinitionSettingBuilder(
					).name(
						ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS
					).value(
						StringPool.TRUE
					).build())));
		AssertUtils.assertFailure(
			ObjectDefinitionSettingNameException.NotAllowedNames.class,
			StringBundler.concat(
				"The settings ",
				ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS,
				" are not allowed for object definition ",
				randomObjectDefinitionName),
			() -> _publishCustomObjectDefinition(
				randomObjectDefinitionName,
				ObjectDefinitionConstants.SCOPE_DEPOT,
				List.of(
					new ObjectDefinitionSettingBuilder(
					).name(
						ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS
					).value(
						StringPool.TRUE
					).build(),
					new ObjectDefinitionSettingBuilder(
					).name(
						ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS
					).value(
						String.valueOf(TestPropsValues.getGroupId())
					).build())));

		String objectDefinitionSettingName = RandomTestUtil.randomString();

		AssertUtils.assertFailure(
			ObjectDefinitionSettingNameException.NotAllowedNames.class,
			StringBundler.concat(
				"The settings ", objectDefinitionSettingName,
				" are not allowed for object definition ",
				randomObjectDefinitionName),
			() -> _publishCustomObjectDefinition(
				randomObjectDefinitionName,
				ObjectDefinitionConstants.SCOPE_DEPOT,
				Collections.singletonList(
					new ObjectDefinitionSettingBuilder(
					).name(
						objectDefinitionSettingName
					).value(
						StringPool.TRUE
					).build())));

		String objectDefinitionSettingValue = RandomTestUtil.randomString();

		AssertUtils.assertFailure(
			ObjectDefinitionSettingValueException.InvalidValue.class,
			StringBundler.concat(
				"The value ", objectDefinitionSettingValue, " of setting \"",
				ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS,
				"\" is invalid for object definition \"",
				randomObjectDefinitionName, "\""),
			() -> _publishCustomObjectDefinition(
				randomObjectDefinitionName,
				ObjectDefinitionConstants.SCOPE_DEPOT,
				Collections.singletonList(
					new ObjectDefinitionSettingBuilder(
					).name(
						ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS
					).value(
						objectDefinitionSettingValue
					).build())));

		DepotEntry depotEntry1 = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(), Collections.emptyMap(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		AssertUtils.assertFailure(
			ObjectDefinitionSettingValueException.InvalidValue.class,
			StringBundler.concat(
				"The value ", TestPropsValues.getGroupId(), " of setting \"",
				ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS,
				"\" is invalid for object definition \"",
				randomObjectDefinitionName, "\""),
			() -> _publishCustomObjectDefinition(
				randomObjectDefinitionName,
				ObjectDefinitionConstants.SCOPE_DEPOT,
				Collections.singletonList(
					new ObjectDefinitionSettingBuilder(
					).name(
						ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS
					).value(
						StringBundler.concat(
							depotEntry1.getGroupId(), StringPool.COMMA,
							TestPropsValues.getGroupId())
					).build())));

		ObjectDefinition objectDefinition1 = _publishCustomObjectDefinition(
			ObjectDefinitionTestUtil.getRandomName(),
			ObjectDefinitionConstants.SCOPE_DEPOT,
			Collections.singletonList(
				new ObjectDefinitionSettingBuilder(
				).name(
					ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS
				).value(
					StringPool.TRUE
				).build()));

		_assertObjectDefinitionSettingsValues(
			objectDefinition1.getObjectDefinitionSettings(),
			Collections.singletonMap(
				ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS,
				StringPool.TRUE));

		ObjectDefinition objectDefinition2 = _publishCustomObjectDefinition(
			ObjectDefinitionTestUtil.getRandomName(),
			ObjectDefinitionConstants.SCOPE_DEPOT,
			Collections.singletonList(
				new ObjectDefinitionSettingBuilder(
				).name(
					ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS
				).value(
					StringPool.TRUE
				).build()));

		_assertObjectDefinitionSettingsValues(
			objectDefinition2.getObjectDefinitionSettings(),
			Collections.singletonMap(
				ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS,
				StringPool.TRUE));

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, objectDefinition1,
				objectDefinition2,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT);

		ObjectField relationshipObjectField =
			_objectFieldLocalService.getObjectField(
				objectRelationship.getObjectFieldId2());

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			depotEntry1.getGroupId(), objectDefinition1.getObjectDefinitionId(),
			Collections.emptyMap());

		ObjectEntry objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			depotEntry1.getGroupId(), objectDefinition2.getObjectDefinitionId(),
			Collections.singletonMap(
				relationshipObjectField.getName(),
				objectEntry1.getObjectEntryId()));

		DepotEntry depotEntry2 = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(), Collections.emptyMap(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		ObjectEntry objectEntry3 = ObjectEntryTestUtil.addObjectEntry(
			depotEntry2.getGroupId(), objectDefinition1.getObjectDefinitionId(),
			Collections.emptyMap());

		objectDefinition1 =
			_objectDefinitionLocalService.updateCustomObjectDefinition(
				null, objectDefinition1.getObjectDefinitionId(), 0, 0,
				objectDefinition1.getObjectFolderId(), 0, false,
				objectDefinition1.isActive(), objectDefinition1.getClassName(),
				true, false, true, true, false, false, false, false, false,
				false, false, null, LocalizedMapUtil.getLocalizedMap("Able"),
				"Able", null, null, false,
				LocalizedMapUtil.getLocalizedMap("Ables"),
				objectDefinition1.getScope(), objectDefinition1.getStatus(),
				Collections.singletonList(
					new ObjectDefinitionSettingBuilder(
					).name(
						ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS
					).value(
						String.valueOf(depotEntry2.getGroupId())
					).build()),
				Collections.emptyList());

		_assertObjectDefinitionSettingsValues(
			objectDefinition1.getObjectDefinitionSettings(),
			Collections.singletonMap(
				ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS,
				String.valueOf(depotEntry2.getGroupId())));

		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				objectEntry1.getObjectEntryId()));
		Assert.assertNotNull(
			_objectEntryLocalService.fetchObjectEntry(
				objectEntry2.getObjectEntryId()));
		Assert.assertNotNull(
			_objectEntryLocalService.fetchObjectEntry(
				objectEntry3.getObjectEntryId()));

		objectDefinition1 =
			_objectDefinitionLocalService.updateCustomObjectDefinition(
				null, objectDefinition1.getObjectDefinitionId(), 0, 0,
				objectDefinition1.getObjectFolderId(), 0, false,
				objectDefinition1.isActive(), objectDefinition1.getClassName(),
				true, false, true, true, false, false, false, false, false,
				false, false, null, LocalizedMapUtil.getLocalizedMap("Able"),
				"Able", null, null, false,
				LocalizedMapUtil.getLocalizedMap("Ables"),
				objectDefinition1.getScope(), objectDefinition1.getStatus(),
				Collections.singletonList(
					new ObjectDefinitionSettingBuilder(
					).name(
						ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS
					).value(
						String.valueOf(depotEntry1.getGroupId())
					).build()),
				Collections.emptyList());

		_assertObjectDefinitionSettingsValues(
			objectDefinition1.getObjectDefinitionSettings(),
			Collections.singletonMap(
				ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS,
				String.valueOf(depotEntry1.getGroupId())));

		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				objectEntry3.getObjectEntryId()));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition1);
		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition2);
	}

	@Test
	public void testAddObjectDefinitionWithWorkflowDefinitionLinks()
		throws Exception {

		// Company scope

		DepotEntry depotEntry1 = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		AssertUtils.assertFailure(
			ObjectDefinitionScopeException.class,
			"An object definition can only be linked to a workflow " +
				"definition within the same scope",
			() -> _objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, true, true,
				false, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), Collections.emptyList(),
				_createWorkflowDefinitionLinks(depotEntry1.getGroupId())));

		Group group = GroupTestUtil.addGroup();

		AssertUtils.assertFailure(
			ObjectDefinitionScopeException.class,
			"An object definition can only be linked to a workflow " +
				"definition within the same scope",
			() -> _objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, true, true,
				false, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), Collections.emptyList(),
				_createWorkflowDefinitionLinks(group.getGroupId())));

		List<WorkflowDefinitionLink> workflowDefinitionLinks =
			_createWorkflowDefinitionLinks(0);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, true, true,
				false, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), Collections.emptyList(),
				workflowDefinitionLinks);

		_assertWorkflowDefinitionLink(
			workflowDefinitionLinks.get(0), 0, objectDefinition);

		// Depot scope

		AssertUtils.assertFailure(
			ObjectDefinitionScopeException.class,
			"An object definition can only be linked to a workflow " +
				"definition within the same scope",
			() -> _objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, true, true,
				false, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_DEPOT,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.singletonList(
					new ObjectDefinitionSettingBuilder(
					).name(
						ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS
					).value(
						StringPool.TRUE
					).build()),
				Collections.emptyList(),
				_createWorkflowDefinitionLinks(group.getGroupId())));

		DepotEntry depotEntry2 = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		AssertUtils.assertFailure(
			ObjectDefinitionScopeException.class,
			StringBundler.concat(
				"The group ", depotEntry2.getGroupId(),
				" is not included in the object definition scope"),
			() -> _objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, true, true,
				false, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_DEPOT,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.singletonList(
					new ObjectDefinitionSettingBuilder(
					).name(
						ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS
					).value(
						String.valueOf(depotEntry1.getGroupId())
					).build()),
				Collections.emptyList(),
				_createWorkflowDefinitionLinks(depotEntry2.getGroupId())));

		workflowDefinitionLinks = _createWorkflowDefinitionLinks(
			depotEntry1.getGroupId());

		objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, true, true,
				false, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_DEPOT,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.singletonList(
					new ObjectDefinitionSettingBuilder(
					).name(
						ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS
					).value(
						StringPool.TRUE
					).build()),
				Collections.emptyList(), workflowDefinitionLinks);

		_assertWorkflowDefinitionLink(
			workflowDefinitionLinks.get(0), depotEntry1.getGroupId(),
			objectDefinition);

		objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, true, true,
				false, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_DEPOT,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.singletonList(
					new ObjectDefinitionSettingBuilder(
					).name(
						ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS
					).value(
						String.valueOf(depotEntry1.getGroupId())
					).build()),
				Collections.emptyList(), workflowDefinitionLinks);

		_assertWorkflowDefinitionLink(
			workflowDefinitionLinks.get(0), depotEntry1.getGroupId(),
			objectDefinition);

		// Site scope

		AssertUtils.assertFailure(
			ObjectDefinitionScopeException.class,
			"An object definition can only be linked to a workflow " +
				"definition within the same scope",
			() -> _objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, true, true,
				false, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_SITE,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), Collections.emptyList(),
				_createWorkflowDefinitionLinks(depotEntry1.getGroupId())));

		workflowDefinitionLinks = _createWorkflowDefinitionLinks(
			group.getGroupId());

		objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, true, true,
				false, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_SITE,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), Collections.emptyList(),
				workflowDefinitionLinks);

		_assertWorkflowDefinitionLink(
			workflowDefinitionLinks.get(0), group.getGroupId(),
			objectDefinition);
	}

	@Test
	public void testAddOrUpdateCustomObjectDefinitionClassName()
		throws Exception {

		String className1 =
			ObjectDefinitionConstants.
				CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION +
					RandomTestUtil.randomString();

		ObjectDefinition objectDefinition1 = _addCustomObjectDefinition(
			className1, ObjectDefinitionTestUtil.getRandomName());

		objectDefinition1 = _updateCustomObjectDefinition(
			RandomTestUtil.randomString(), objectDefinition1);

		Assert.assertEquals(className1, objectDefinition1.getClassName());

		AssertUtils.assertFailure(
			ObjectDefinitionClassNameException.MustNotBeDuplicate.class,
			"Duplicate class name " + className1,
			() -> _addCustomObjectDefinition(
				className1, ObjectDefinitionTestUtil.getRandomName()));
		AssertUtils.assertFailure(
			ObjectDefinitionClassNameException.MustStartWithPrefix.class,
			"Class name must start with " +
				ObjectDefinitionConstants.
					CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION,
			() -> _addCustomObjectDefinition(
				RandomTestUtil.randomString(),
				ObjectDefinitionTestUtil.getRandomName()));

		ObjectDefinition objectDefinition2 =
			_objectDefinitionLocalService.addObjectDefinition(
				ObjectDefinitionTestUtil.getRandomName(),
				TestPropsValues.getUserId(), 0, true,
				ObjectDefinitionConstants.SCOPE_COMPANY, false);

		Assert.assertTrue(Validator.isNull(objectDefinition2.getClassName()));

		String className2 =
			ObjectDefinitionConstants.
				CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION +
					RandomTestUtil.randomString();

		objectDefinition2 = _updateCustomObjectDefinition(
			className2, objectDefinition2);

		Assert.assertEquals(className2, objectDefinition2.getClassName());

		ObjectDefinition objectDefinition3 = _addCustomObjectDefinition(
			null, ObjectDefinitionTestUtil.getRandomName());

		Assert.assertTrue(
			StringUtil.startsWith(
				objectDefinition3.getClassName(),
				ObjectDefinitionConstants.
					CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION));
	}

	@Test
	public void testAddOrUpdateSystemObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addOrUpdateSystemObjectDefinition(
				TestPropsValues.getCompanyId(), 0,
				new BaseSystemObjectDefinitionManager() {

					@Override
					public long addBaseModel(
							User user, Map<String, Object> values)
						throws Exception {

						return 0;
					}

					@Override
					public BaseModel<?> deleteBaseModel(BaseModel<?> baseModel)
						throws PortalException {

						return null;
					}

					public BaseModel<?> fetchBaseModelByExternalReferenceCode(
						String externalReferenceCode, long companyId) {

						return null;
					}

					@Override
					public BaseModel<?> getBaseModelByExternalReferenceCode(
							String externalReferenceCode, long companyId)
						throws PortalException {

						return null;
					}

					@Override
					public String getBaseModelExternalReferenceCode(
							long primaryKey)
						throws PortalException {

						return null;
					}

					@Override
					public String getExternalReferenceCode() {
						return "L_USER_NOTIFICATION_EVENT";
					}

					@Override
					public JaxRsApplicationDescriptor
						getJaxRsApplicationDescriptor() {

						return null;
					}

					@Override
					public Map<Locale, String> getLabelMap() {
						return LocalizedMapUtil.getLocalizedMap(
							"User Notification Event");
					}

					@Override
					public Class<?> getModelClass() {
						return UserNotificationEvent.class;
					}

					@Override
					public List<ObjectAction> getObjectActions() {
						return Collections.singletonList(
							_createObjectAction("updateDeliveryType1"));
					}

					@Override
					public List<ObjectField> getObjectFields() {
						return Arrays.asList(
							new BooleanObjectFieldBuilder(
							).labelMap(
								createLabelMap("Action Required")
							).name(
								"actionRequired"
							).required(
								true
							).build(),
							new LongIntegerObjectFieldBuilder(
							).labelMap(
								createLabelMap("Delivery Type")
							).name(
								"deliveryType"
							).build(),
							new TextObjectFieldBuilder(
							).dbColumnName(
								"type_"
							).labelMap(
								createLabelMap("Type")
							).name(
								"type"
							).required(
								true
							).build());
					}

					@Override
					public Map<Locale, String> getPluralLabelMap() {
						return LocalizedMapUtil.getLocalizedMap(
							"User Notification Events");
					}

					@Override
					public Column<?, Long> getPrimaryKeyColumn() {
						return UserNotificationEventTable.INSTANCE.
							userNotificationEventId;
					}

					@Override
					public String getScope() {
						return ObjectDefinitionConstants.SCOPE_COMPANY;
					}

					@Override
					public Table getTable() {
						return UserNotificationEventTable.INSTANCE;
					}

					@Override
					public int getVersion() {
						return 1;
					}

					@Override
					public void updateBaseModel(
							long primaryKey, User user,
							Map<String, Object> values)
						throws Exception {
					}

					@Override
					public long upsertBaseModel(
						String externalReferenceCode, long companyId, User user,
						Map<String, Object> values) {

						return 0;
					}

				});

		Assert.assertEquals(
			"UserNotificationEvent", objectDefinition.getDBTableName());
		Assert.assertEquals(
			"userNotificationEventId",
			objectDefinition.getPKObjectFieldDBColumnName());
		Assert.assertEquals(
			"userNotificationEventId", objectDefinition.getPKObjectFieldName());
		Assert.assertEquals(objectDefinition.isSystem(), true);
		Assert.assertEquals(1, objectDefinition.getVersion());

		_assertObjectField(
			objectDefinition, "actionRequired", "Boolean", "actionRequired",
			true);

		try {
			_objectFieldLocalService.getObjectField(
				objectDefinition.getObjectDefinitionId(), "archived");

			Assert.fail();
		}
		catch (NoSuchObjectFieldException noSuchObjectFieldException) {
			Assert.assertNotNull(noSuchObjectFieldException);
		}

		_assertObjectField(
			objectDefinition, "deliveryType", "Long", "deliveryType", false);
		_assertObjectField(objectDefinition, "type_", "String", "type", true);

		Assert.assertEquals(
			_defaultObjectFolder.getObjectFolderId(),
			objectDefinition.getObjectFolderId());

		Assert.assertNotNull(
			_objectActionLocalService.getObjectAction(
				objectDefinition.getObjectDefinitionId(), "updateDeliveryType1",
				ObjectActionTriggerConstants.KEY_ON_AFTER_ADD));

		objectDefinition =
			_objectDefinitionLocalService.addOrUpdateSystemObjectDefinition(
				TestPropsValues.getCompanyId(), 0,
				new BaseSystemObjectDefinitionManager() {

					@Override
					public long addBaseModel(
							User user, Map<String, Object> values)
						throws Exception {

						return 0;
					}

					@Override
					public BaseModel<?> deleteBaseModel(BaseModel<?> baseModel)
						throws PortalException {

						return null;
					}

					public BaseModel<?> fetchBaseModelByExternalReferenceCode(
						String externalReferenceCode, long companyId) {

						return null;
					}

					@Override
					public BaseModel<?> getBaseModelByExternalReferenceCode(
							String externalReferenceCode, long companyId)
						throws PortalException {

						return null;
					}

					@Override
					public String getBaseModelExternalReferenceCode(
							long primaryKey)
						throws PortalException {

						return null;
					}

					@Override
					public String getExternalReferenceCode() {
						return "L_USER_NOTIFICATION_EVENT";
					}

					@Override
					public JaxRsApplicationDescriptor
						getJaxRsApplicationDescriptor() {

						return null;
					}

					@Override
					public Map<Locale, String> getLabelMap() {
						return LocalizedMapUtil.getLocalizedMap(
							"User Notification Event");
					}

					@Override
					public Class<?> getModelClass() {
						return UserNotificationEvent.class;
					}

					@Override
					public List<ObjectAction> getObjectActions() {
						return Collections.singletonList(
							_createObjectAction("updateDeliveryType2"));
					}

					@Override
					public List<ObjectField> getObjectFields() {
						return Arrays.asList(
							new BooleanObjectFieldBuilder(
							).labelMap(
								createLabelMap("Archived")
							).name(
								"archived"
							).required(
								true
							).build(),
							new LongIntegerObjectFieldBuilder(
							).labelMap(
								createLabelMap("Delivery Type")
							).name(
								"deliveryType"
							).required(
								true
							).build(),
							new TextObjectFieldBuilder(
							).dbColumnName(
								"type_"
							).labelMap(
								createLabelMap("Type")
							).name(
								"type"
							).build());
					}

					@Override
					public Map<Locale, String> getPluralLabelMap() {
						return LocalizedMapUtil.getLocalizedMap(
							"User Notification Events");
					}

					@Override
					public Column<?, Long> getPrimaryKeyColumn() {
						return UserNotificationEventTable.INSTANCE.
							userNotificationEventId;
					}

					@Override
					public String getScope() {
						return ObjectDefinitionConstants.SCOPE_COMPANY;
					}

					@Override
					public Table getTable() {
						return UserNotificationEventTable.INSTANCE;
					}

					@Override
					public int getVersion() {
						return 2;
					}

					@Override
					public void updateBaseModel(
							long primaryKey, User user,
							Map<String, Object> values)
						throws Exception {
					}

					@Override
					public long upsertBaseModel(
						String externalReferenceCode, long companyId, User user,
						Map<String, Object> values) {

						return 0;
					}

				});

		Assert.assertEquals(2, objectDefinition.getVersion());

		try {
			_objectFieldLocalService.getObjectField(
				objectDefinition.getObjectDefinitionId(), "actionRequired");

			Assert.fail();
		}
		catch (NoSuchObjectFieldException noSuchObjectFieldException) {
			Assert.assertNotNull(noSuchObjectFieldException);
		}

		_assertObjectField(
			objectDefinition, "archived", "Boolean", "archived", true);
		_assertObjectField(
			objectDefinition, "deliveryType", "Long", "deliveryType", true);
		_assertObjectField(objectDefinition, "type_", "String", "type", false);

		Assert.assertNotNull(
			_objectActionLocalService.getObjectAction(
				objectDefinition.getObjectDefinitionId(), "updateDeliveryType2",
				ObjectActionTriggerConstants.KEY_ON_AFTER_ADD));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@FeatureFlags(
		featureFlags = {
			@FeatureFlag(value = "LPD-17564"), @FeatureFlag(value = "LPD-32050")
		}
	)
	@Test
	public void testAddSystemObjectDefinition() throws Exception {

		// Enable form container

		AssertUtils.assertFailure(
			ObjectDefinitionEnableFormContainerException.class,
			"Enable form container is not allowed for unmodifiable system " +
				"object definitions",
			() -> _objectDefinitionLocalService.addSystemObjectDefinition(
				null, TestPropsValues.getUserId(), 0, null, null, false, true,
				false, true, true, false, false, false, false, null,
				RandomTestUtil.randomLocaleStringMap(), false,
				ObjectDefinitionTestUtil.getRandomName(), null, null, null,
				null, RandomTestUtil.randomLocaleStringMap(), false,
				ObjectDefinitionConstants.SCOPE_COMPANY, null, 1,
				WorkflowConstants.STATUS_APPROVED, Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList()));

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addSystemObjectDefinition(
				null, TestPropsValues.getUserId(), 0, null, null, false, false,
				false, true, true, false, false, false, false, null,
				RandomTestUtil.randomLocaleStringMap(), false,
				ObjectDefinitionTestUtil.getRandomName(), null, null, null,
				null, RandomTestUtil.randomLocaleStringMap(), false,
				ObjectDefinitionConstants.SCOPE_COMPANY, null, 1,
				WorkflowConstants.STATUS_APPROVED, Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList());

		Assert.assertFalse(objectDefinition.isEnableFormContainer());

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		objectDefinition =
			_objectDefinitionLocalService.addSystemObjectDefinition(
				null, TestPropsValues.getUserId(), 0, null, null, false, true,
				false, true, true, false, false, false, false, null,
				RandomTestUtil.randomLocaleStringMap(), true, "Test", null,
				null, null, null, RandomTestUtil.randomLocaleStringMap(), false,
				ObjectDefinitionConstants.SCOPE_COMPANY, null, 1,
				WorkflowConstants.STATUS_APPROVED, Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList());

		Assert.assertTrue(objectDefinition.isEnableFormContainer());

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		// Enable friendly URL customization

		AssertUtils.assertFailure(
			ObjectDefinitionEnableFriendlyURLCustomizationException.class,
			"Enable friendly URL customization is not allowed for " +
				"unmodifiable system object definitions",
			() -> _objectDefinitionLocalService.addSystemObjectDefinition(
				null, TestPropsValues.getUserId(), 0,
				ObjectDefinitionTestUtil.getRandomName(), null, false, false,
				true, true, false, false, false, false, false, null,
				RandomTestUtil.randomLocaleStringMap(), false,
				ObjectDefinitionTestUtil.getRandomName(), null, null, null,
				null, RandomTestUtil.randomLocaleStringMap(), false,
				ObjectDefinitionConstants.SCOPE_COMPANY, null, 1,
				WorkflowConstants.STATUS_APPROVED, Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList()));

		// Enable object entry schedule

		AssertUtils.assertFailure(
			ObjectDefinitionEnableObjectEntryScheduleException.class,
			"Enable object entry schedule is not allowed for unmodifiable " +
				"system object definitions",
			() -> _objectDefinitionLocalService.addSystemObjectDefinition(
				null, TestPropsValues.getUserId(), 0,
				ObjectDefinitionTestUtil.getRandomName(), null, false, false,
				false, true, false, false, true, false, false, null,
				RandomTestUtil.randomLocaleStringMap(), false,
				ObjectDefinitionTestUtil.getRandomName(), null, null, null,
				null, RandomTestUtil.randomLocaleStringMap(), false,
				ObjectDefinitionConstants.SCOPE_COMPANY, null, 1,
				WorkflowConstants.STATUS_APPROVED, Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList()));

		// Enable object entry subscription

		AssertUtils.assertFailure(
			ObjectDefinitionEnableObjectEntrySubscriptionException.class,
			"Enable object entry subscription is not allowed for " +
				"unmodifiable system object definitions",
			() -> _objectDefinitionLocalService.addSystemObjectDefinition(
				null, TestPropsValues.getUserId(), 0,
				ObjectDefinitionTestUtil.getRandomName(), null, false, false,
				false, true, false, false, false, true, false, null,
				RandomTestUtil.randomLocaleStringMap(), false,
				ObjectDefinitionTestUtil.getRandomName(), null, null, null,
				null, RandomTestUtil.randomLocaleStringMap(), false,
				ObjectDefinitionConstants.SCOPE_COMPANY, null, 1,
				WorkflowConstants.STATUS_APPROVED, Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList()));

		// Enable object entry versioning

		AssertUtils.assertFailure(
			ObjectDefinitionEnableObjectEntryVersioningException.class,
			"Enable object entry versioning is not allowed for unmodifiable " +
				"system object definitions",
			() -> _objectDefinitionLocalService.addSystemObjectDefinition(
				null, TestPropsValues.getUserId(), 0,
				ObjectDefinitionTestUtil.getRandomName(), null, false, false,
				false, true, false, false, false, false, true, null,
				RandomTestUtil.randomLocaleStringMap(), false,
				ObjectDefinitionTestUtil.getRandomName(), null, null, null,
				null, RandomTestUtil.randomLocaleStringMap(), false,
				ObjectDefinitionConstants.SCOPE_COMPANY, null, 1,
				WorkflowConstants.STATUS_APPROVED, Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList()));

		// Label is null

		AssertUtils.assertFailure(
			ObjectDefinitionLabelException.class,
			"Label is null for locale " + LocaleUtil.US.getDisplayName(),
			() -> _addUnmodifiableSystemObjectDefinition(
				"", "Test", RandomTestUtil.randomString()));

		// Name

		_objectDefinitionLocalService.deleteObjectDefinition(
			_addUnmodifiableSystemObjectDefinition(" Test "));
		_objectDefinitionLocalService.deleteObjectDefinition(
			_addUnmodifiableSystemObjectDefinition(
				"A123456789a123456789a123456789a1234567891"));

		AssertUtils.assertFailure(
			ObjectDefinitionNameException.
				ForbiddenModifiableSystemObjectDefinitionName.class,
			"Forbidden modifiable system object definition name Invalid Test",
			() -> ObjectDefinitionTestUtil.addModifiableSystemObjectDefinition(
				TestPropsValues.getUserId(), null, true,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Invalid Test", null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionConstants.SCOPE_SITE, null, 1,
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(),
						StringUtil.randomId()))));
		AssertUtils.assertFailure(
			ObjectDefinitionNameException.MustBeLessThan41Characters.class,
			"Name must be less than 41 characters",
			() -> _addUnmodifiableSystemObjectDefinition(
				"A123456789a123456789a123456789a12345678912"));
		AssertUtils.assertFailure(
			ObjectDefinitionNameException.MustBeginWithUpperCaseLetter.class,
			"The first character of a name must be an upper case letter",
			() -> _addUnmodifiableSystemObjectDefinition("test"));

		objectDefinition = _addUnmodifiableSystemObjectDefinition("Test");

		AssertUtils.assertFailure(
			ObjectDefinitionNameException.MustNotBeDuplicate.class,
			"Duplicate name Test",
			() -> _addUnmodifiableSystemObjectDefinition("Test"));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		AssertUtils.assertFailure(
			ObjectDefinitionNameException.MustNotBeNull.class, "Name is null",
			() -> _addUnmodifiableSystemObjectDefinition(""));
		AssertUtils.assertFailure(
			ObjectDefinitionNameException.
				MustNotStartWithCAndUnderscoreForSystemObject.class,
			"System object definition names must not start with \"C_\"",
			() -> _addUnmodifiableSystemObjectDefinition("C_Test"));
		AssertUtils.assertFailure(
			ObjectDefinitionNameException.
				MustNotStartWithCAndUnderscoreForSystemObject.class,
			"System object definition names must not start with \"C_\"",
			() -> _addUnmodifiableSystemObjectDefinition("c_Test"));
		AssertUtils.assertFailure(
			ObjectDefinitionNameException.MustOnlyContainLettersAndDigits.class,
			"Name must only contain letters and digits",
			() -> _addUnmodifiableSystemObjectDefinition("Tes t"));
		AssertUtils.assertFailure(
			ObjectDefinitionNameException.MustOnlyContainLettersAndDigits.class,
			"Name must only contain letters and digits",
			() -> _addUnmodifiableSystemObjectDefinition("Tes-t"));

		// Plural label is null

		AssertUtils.assertFailure(
			ObjectDefinitionPluralLabelException.class,
			"Plural label is null for locale " + LocaleUtil.US.getDisplayName(),
			() -> _addUnmodifiableSystemObjectDefinition(
				RandomTestUtil.randomString(), "Test", ""));

		// Scope is null

		AssertUtils.assertFailure(
			ObjectDefinitionScopeException.class, "Scope is null",
			() ->
				ObjectDefinitionTestUtil.addUnmodifiableSystemObjectDefinition(
					null, TestPropsValues.getUserId(), "Test", null,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					"Test", null, null,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					"", null, 1, Collections.emptyList()));

		// No object scope provider found with key

		String scope = RandomTestUtil.randomString();

		AssertUtils.assertFailure(
			ObjectDefinitionScopeException.class,
			"No object scope provider found with key " + scope,
			() ->
				ObjectDefinitionTestUtil.addUnmodifiableSystemObjectDefinition(
					null, TestPropsValues.getUserId(), "Test", null,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					"Test", null, null,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					scope, null, 1, Collections.emptyList()));

		// Version must greater than 0

		AssertUtils.assertFailure(
			ObjectDefinitionVersionException.class,
			"System object definition versions must greater than 0",
			() ->
				ObjectDefinitionTestUtil.addUnmodifiableSystemObjectDefinition(
					null, TestPropsValues.getUserId(), "Test", null,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					"Test", null, null,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					ObjectDefinitionConstants.SCOPE_COMPANY, null, -1,
					Collections.<ObjectField>emptyList()));

		AssertUtils.assertFailure(
			ObjectDefinitionVersionException.class,
			"System object definition versions must greater than 0",
			() ->
				ObjectDefinitionTestUtil.addUnmodifiableSystemObjectDefinition(
					null, TestPropsValues.getUserId(), "Test", null,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					"Test", null, null,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					ObjectDefinitionConstants.SCOPE_COMPANY, null, 0,
					Collections.<ObjectField>emptyList()));

		// Database table, messaging, resources, and status

		objectDefinition =
			ObjectDefinitionTestUtil.addUnmodifiableSystemObjectDefinition(
				null, TestPropsValues.getUserId(), "Test", null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test", null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionConstants.SCOPE_COMPANY, null, 1,
				Collections.<ObjectField>emptyList());

		Assert.assertTrue(
			Validator.isBlank(objectDefinition.getFriendlyURLSeparator()));

		ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).labelMap(
				LocalizedMapUtil.getLocalizedMap("Able")
			).name(
				"able"
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).required(
				true
			).build());

		// Database table

		Assert.assertFalse(
			_hasColumn(objectDefinition.getExtensionDBTableName(), "able"));
		Assert.assertTrue(
			_hasColumn(objectDefinition.getExtensionDBTableName(), "able_"));
		Assert.assertFalse(_hasTable(objectDefinition.getDBTableName()));
		Assert.assertTrue(
			_hasTable(objectDefinition.getExtensionDBTableName()));

		// Messaging

		Assert.assertNull(
			_messageBus.getDestination(objectDefinition.getDestinationName()));

		// Resources

		Assert.assertEquals(
			0,
			_resourceActionLocalService.getResourceActionsCount(
				objectDefinition.getClassName()));

		try {
			_resourceActionLocalService.getResourceActionsCount(
				objectDefinition.getPortletId());

			Assert.fail();
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
			Assert.assertNotNull(unsupportedOperationException);
		}

		try {
			_resourceActionLocalService.getResourceActionsCount(
				objectDefinition.getResourceName());

			Assert.fail();
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
			Assert.assertNotNull(unsupportedOperationException);
		}

		Assert.assertEquals(
			1,
			_resourcePermissionLocalService.getResourcePermissionsCount(
				objectDefinition.getCompanyId(),
				ObjectDefinition.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectDefinition.getObjectDefinitionId())));

		// Status

		Assert.assertTrue(objectDefinition.isApproved());

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		// Publish modifiable system object definition

		objectDefinition =
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

		objectDefinition =
			_objectDefinitionLocalService.publishSystemObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		Assert.assertEquals(
			_defaultObjectFolder.getObjectFolderId(),
			objectDefinition.getObjectFolderId());
		Assert.assertTrue(
			StringUtil.startsWith(
				objectDefinition.getDBTableName(),
				ObjectDefinitionConstants.
					EXTERNAL_REFERENCE_CODE_PREFIX_SYSTEM_OBJECT_DEFINITION));
		Assert.assertEquals(
			"l_testId", objectDefinition.getPKObjectFieldName());
		Assert.assertEquals("/test", objectDefinition.getRESTContextPath());
		Assert.assertTrue(objectDefinition.isApproved());
		Assert.assertTrue(objectDefinition.isEnableCategorization());
		Assert.assertEquals("test", objectDefinition.getFriendlyURLSeparator());
		Assert.assertTrue(objectDefinition.isModifiable());
		Assert.assertTrue(objectDefinition.isSystem());
		Assert.assertTrue(_hasTable(objectDefinition.getDBTableName()));
		Assert.assertTrue(
			_hasTable(objectDefinition.getExtensionDBTableName()));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		objectDefinition =
			ObjectDefinitionTestUtil.addUnmodifiableSystemObjectDefinition(
				null, TestPropsValues.getUserId(), "Test", null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test", null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionConstants.SCOPE_COMPANY, null, 1,
				Collections.<ObjectField>emptyList());

		// Publish unmodifiable system object definition

		try {
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

			Assert.fail();
		}
		catch (ObjectDefinitionStatusException
					objectDefinitionStatusException) {

			Assert.assertNotNull(objectDefinitionStatusException);
		}

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Test
	public void testAuditRouter() throws Exception {
		Queue<AuditMessage> auditMessages = new LinkedList<>();

		AuditRouter auditRouter =
			(AuditRouter)ReflectionTestUtil.getAndSetFieldValue(
				_objectDefinitionModelListener, "_auditRouter",
				ProxyUtil.newProxyInstance(
					AuditRouter.class.getClassLoader(),
					new Class<?>[] {AuditRouter.class},
					(proxy, method, arguments) -> {
						auditMessages.add((AuditMessage)arguments[0]);

						return null;
					}));

		ObjectDefinition objectDefinition = _addCustomObjectDefinition(
			ObjectDefinitionTestUtil.getRandomName());

		AuditMessage auditMessage = auditMessages.poll();

		JSONAssert.assertEquals(
			JSONUtil.put(
				"active", objectDefinition.isActive()
			).put(
				"labelMap", objectDefinition.getLabelMap()
			).put(
				"name", objectDefinition.getName()
			).put(
				"scope", objectDefinition.getScope()
			).toString(),
			String.valueOf(auditMessage.getAdditionalInfo()),
			JSONCompareMode.STRICT_ORDER);

		auditMessages.clear();

		objectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		auditMessage = auditMessages.poll();

		JSONAssert.assertEquals(
			JSONUtil.put(
				"attributes",
				JSONUtil.putAll(
					JSONUtil.put(
						"name", "active"
					).put(
						"newValue", "true"
					).put(
						"oldValue", "false"
					))
			).toString(),
			String.valueOf(auditMessage.getAdditionalInfo()),
			JSONCompareMode.STRICT_ORDER);

		auditMessages.clear();

		objectDefinition = _objectDefinitionLocalService.deleteObjectDefinition(
			objectDefinition.getObjectDefinitionId());

		auditMessage = auditMessages.poll();

		JSONAssert.assertEquals(
			JSONUtil.put(
				"active", objectDefinition.isActive()
			).put(
				"labelMap", objectDefinition.getLabelMap()
			).put(
				"name", objectDefinition.getName()
			).put(
				"scope", objectDefinition.getScope()
			).toString(),
			String.valueOf(auditMessage.getAdditionalInfo()),
			JSONCompareMode.STRICT_ORDER);

		ReflectionTestUtil.setFieldValue(
			_objectDefinitionModelListener, "_auditRouter", auditRouter);
	}

	@Test
	public void testDeleteCompanyObjectDefinitions() throws Exception {
		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		String originalName = PrincipalThreadLocal.getName();

		Company company = CompanyTestUtil.addCompany();

		PortalInstances.initCompany(company);

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			User user = UserTestUtil.getAdminUser(company.getCompanyId());

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));
			PrincipalThreadLocal.setName(user.getUserId());

			ObjectDefinition customObjectDefinition =
				_objectDefinitionLocalService.addCustomObjectDefinition(
					user.getUserId(), 0, null, false, true, false, true, false,
					false, false, false, false, null,
					LocalizedMapUtil.getLocalizedMap("Able"), "Able", null,
					null, LocalizedMapUtil.getLocalizedMap("Ables"), true,
					ObjectDefinitionConstants.SCOPE_COMPANY,
					ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
					Collections.emptyList(),
					Collections.singletonList(
						ObjectFieldUtil.createObjectField(
							ObjectFieldConstants.BUSINESS_TYPE_TEXT,
							ObjectFieldConstants.DB_TYPE_STRING,
							RandomTestUtil.randomString(),
							StringUtil.randomId())),
					Collections.emptyList());

			_objectDefinitionLocalService.publishCustomObjectDefinition(
				user.getUserId(),
				customObjectDefinition.getObjectDefinitionId());

			ObjectDefinition modifiableSystemObjectDefinition =
				ObjectDefinitionTestUtil.addModifiableSystemObjectDefinition(
					user.getUserId(), null, false,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					"Test", null, null,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					ObjectDefinitionConstants.SCOPE_SITE, null, 1,
					Collections.singletonList(
						ObjectFieldUtil.createObjectField(
							ObjectFieldConstants.BUSINESS_TYPE_TEXT,
							ObjectFieldConstants.DB_TYPE_STRING,
							StringUtil.randomId(), Collections.emptyList())));

			_objectDefinitionLocalService.publishSystemObjectDefinition(
				user.getUserId(),
				modifiableSystemObjectDefinition.getObjectDefinitionId());
		}
		finally {
			_companyLocalService.deleteCompany(company);

			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
			PrincipalThreadLocal.setName(originalName);
		}

		Assert.assertNull(
			_objectDefinitionLocalService.fetchObjectDefinition(
				company.getCompanyId(), "Able"));
		Assert.assertNull(
			_objectDefinitionLocalService.fetchObjectDefinition(
				company.getCompanyId(), "Test"));
	}

	@Test
	public void testDeleteObjectDefinition() throws Exception {

		// Delete custom object definition

		Tree tree = TreeTestUtil.createObjectDefinitionTree(
			_objectDefinitionLocalService, _objectRelationshipLocalService,
			false,
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[0]
			).build());

		Node node = tree.getRootNode();

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				node.getPrimaryKey());

		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			"To delete this object, you must first disable inheritance and " +
				"delete its relationships",
			() -> _objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition));

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());

		ClassName className = _classNameLocalService.getClassName(
			objectDefinition.getClassName());

		Assert.assertNotNull(
			_classNameLocalService.fetchByClassNameId(
				className.getClassNameId()));

		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(), 0,
			objectDefinition.getClassName(), 0, 0, "Single Approver", 1);

		Assert.assertTrue(
			ListUtil.isNotEmpty(
				_workflowDefinitionLinkLocalService.getWorkflowDefinitionLinks(
					TestPropsValues.getCompanyId(),
					objectDefinition.getClassName())));

		TreeTestUtil.unbind(
			objectDefinition.getObjectDefinitionId(),
			_objectRelationshipLocalService);

		_objectDefinitionLocalService.deleteObjectDefinition(
			objectDefinition.getObjectDefinitionId());

		Assert.assertNull(
			_classNameLocalService.fetchByClassNameId(
				className.getClassNameId()));

		// Database table

		Assert.assertFalse(_hasTable(objectDefinition.getDBTableName()));
		Assert.assertFalse(
			_hasTable(objectDefinition.getExtensionDBTableName()));

		// Messaging

		Assert.assertNull(
			_messageBus.getDestination(objectDefinition.getDestinationName()));

		// Resources

		List<String> resourceActions = _resourceActions.getModelResourceActions(
			objectDefinition.getClassName());

		Assert.assertEquals(
			resourceActions.toString(), 0, resourceActions.size());

		resourceActions = _resourceActions.getModelResourceActions(
			objectDefinition.getResourceName());

		Assert.assertEquals(
			resourceActions.toString(), 0, resourceActions.size());

		Map<String, ?> resourceActionsBagMap = ReflectionTestUtil.getFieldValue(
			_resourceActions, "_resourceActionsBags");

		Assert.assertNull(
			resourceActionsBagMap.get(objectDefinition.getPortletId()));

		Assert.assertEquals(
			0,
			_resourceActionLocalService.getResourceActionsCount(
				objectDefinition.getClassName()));
		Assert.assertEquals(
			0,
			_resourceActionLocalService.getResourceActionsCount(
				objectDefinition.getPortletId()));
		Assert.assertEquals(
			0,
			_resourceActionLocalService.getResourceActionsCount(
				objectDefinition.getResourceName()));
		Assert.assertEquals(
			0,
			_resourcePermissionLocalService.getResourcePermissionsCount(
				objectDefinition.getCompanyId(),
				ObjectDefinition.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectDefinition.getObjectDefinitionId())));

		// Workflow definition links

		Assert.assertTrue(
			ListUtil.isEmpty(
				_workflowDefinitionLinkLocalService.getWorkflowDefinitionLinks(
					TestPropsValues.getCompanyId(),
					objectDefinition.getClassName())));

		// Delete modifiable system object definition

		ObjectDefinition modifiableSystemObjectDefinition =
			ObjectDefinitionTestUtil.addModifiableSystemObjectDefinition(
				TestPropsValues.getUserId(), null, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test", null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionConstants.SCOPE_SITE, null, 1,
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						StringUtil.randomId(), Collections.emptyList())));

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			modifiableSystemObjectDefinition.getObjectDefinitionId());

		AssertUtils.assertFailure(
			ObjectDefinitionSystemException.class, false,
			"Only allowed bundles can delete system object definitions",
			() -> _objectDefinitionLocalService.deleteObjectDefinition(
				modifiableSystemObjectDefinition.getObjectDefinitionId()));

		_objectDefinitionLocalService.deleteObjectDefinition(
			modifiableSystemObjectDefinition.getObjectDefinitionId());

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService, new String[] {"C_A", "C_AA"},
			_objectEntryLocalService, _objectRelationshipLocalService);
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testDeleteObjectDefinitionWithObjectEntries() throws Exception {
		String objectFieldName = StringUtil.randomId();

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, false, true,
				false, false, false, false, true, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"A" + StringUtil.randomString(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), objectFieldName)),
				Collections.emptyList());

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			0, objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				objectFieldName, RandomTestUtil.randomString()
			).build());

		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		_addSharingEntry(
			objectDefinition.getClassName(), objectEntry1.getObjectEntryId(),
			userGroup.getUserGroupId());

		ObjectEntry objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			0, objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				objectFieldName, RandomTestUtil.randomString()
			).build());

		_addSharingEntry(
			objectDefinition.getClassName(), objectEntry2.getObjectEntryId(),
			userGroup.getUserGroupId());

		Assert.assertEquals(
			2,
			_getObjectEntryVersionsCount(
				objectDefinition.getObjectDefinitionId()));
		Assert.assertEquals(2, _getSharingEntriesCount(objectDefinition));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		Assert.assertEquals(
			0,
			_getObjectEntryVersionsCount(
				objectDefinition.getObjectDefinitionId()));
		Assert.assertEquals(0, _getSharingEntriesCount(objectDefinition));
	}

	@Test
	public void testEnableAccountEntryRestrictedForNondefaultStorageType()
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, false, true,
				false, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap("Able"), "Able", null, null,
				LocalizedMapUtil.getLocalizedMap("Ables"), true,
				ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_SALESFORCE,
				Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList());

		objectDefinition =
			_objectDefinitionLocalService.
				enableAccountEntryRestrictedForNondefaultStorageType(
					ObjectFieldUtil.addCustomObjectField(
						new TextObjectFieldBuilder(
						).userId(
							TestPropsValues.getUserId()
						).labelMap(
							LocalizedMapUtil.getLocalizedMap(
								RandomTestUtil.randomString())
						).name(
							StringUtil.randomId()
						).objectDefinitionId(
							objectDefinition.getObjectDefinitionId()
						).required(
							true
						).build()));

		Assert.assertTrue(
			objectDefinition.getAccountEntryRestrictedObjectFieldId() > 0);
		Assert.assertEquals(
			objectDefinition.getStorageType(),
			ObjectDefinitionConstants.STORAGE_TYPE_SALESFORCE);
		Assert.assertTrue(objectDefinition.isAccountEntryRestricted());
		Assert.assertFalse(objectDefinition.isSystem());

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		AssertUtils.assertFailure(
			ObjectDefinitionAccountEntryRestrictedException.class,
			"Custom object definitions can only be restricted by an integer, " +
				"long integer, or text field",
			() ->
				_objectDefinitionLocalService.
					enableAccountEntryRestrictedForNondefaultStorageType(
						ObjectFieldUtil.addCustomObjectField(
							new DateObjectFieldBuilder(
							).userId(
								TestPropsValues.getUserId()
							).labelMap(
								LocalizedMapUtil.getLocalizedMap(
									RandomTestUtil.randomString())
							).name(
								StringUtil.randomId()
							).objectDefinitionId(
								_addCustomObjectDefinition(
									"Test" + RandomTestUtil.randomString()
								).getObjectDefinitionId()
							).required(
								true
							).build())));
		AssertUtils.assertFailure(
			UnsupportedOperationException.class, null,
			() ->
				_objectDefinitionLocalService.
					enableAccountEntryRestrictedForNondefaultStorageType(
						ObjectFieldUtil.addCustomObjectField(
							new TextObjectFieldBuilder(
							).userId(
								TestPropsValues.getUserId()
							).labelMap(
								LocalizedMapUtil.getLocalizedMap(
									RandomTestUtil.randomString())
							).name(
								StringUtil.randomId()
							).objectDefinitionId(
								_addCustomObjectDefinition(
									"Test" + RandomTestUtil.randomString()
								).getObjectDefinitionId()
							).required(
								true
							).build())));

		objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, false, true,
				false, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap("Able"), "Able", null, null,
				LocalizedMapUtil.getLocalizedMap("Ables"), true,
				ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_SALESFORCE,
				Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList());

		objectDefinition =
			_objectDefinitionLocalService.
				enableAccountEntryRestrictedForNondefaultStorageType(
					ObjectFieldUtil.addCustomObjectField(
						new TextObjectFieldBuilder(
						).userId(
							TestPropsValues.getUserId()
						).labelMap(
							LocalizedMapUtil.getLocalizedMap(
								RandomTestUtil.randomString())
						).name(
							StringUtil.randomId()
						).objectDefinitionId(
							objectDefinition.getObjectDefinitionId()
						).required(
							true
						).build()));

		Assert.assertTrue(
			objectDefinition.getAccountEntryRestrictedObjectFieldId() > 0);
		Assert.assertEquals(
			objectDefinition.getStorageType(),
			ObjectDefinitionConstants.STORAGE_TYPE_SALESFORCE);
		Assert.assertTrue(objectDefinition.isAccountEntryRestricted());
		Assert.assertFalse(objectDefinition.isSystem());

		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				StringUtil.randomId()
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).required(
				true
			).build());

		objectDefinition =
			_objectDefinitionLocalService.
				enableAccountEntryRestrictedForNondefaultStorageType(
					objectField);

		Assert.assertEquals(
			objectDefinition.getAccountEntryRestrictedObjectFieldId(),
			objectField.getObjectFieldId());
		Assert.assertTrue(objectDefinition.isAccountEntryRestricted());

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Test
	public void testEnableAccountRestricted() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addObjectDefinition(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(), 0,
				true, ObjectDefinitionConstants.SCOPE_COMPANY, false);

		objectDefinition =
			_objectDefinitionLocalService.enableAccountEntryRestricted(
				_objectRelationshipLocalService.addObjectRelationship(
					null, TestPropsValues.getUserId(),
					_objectDefinitionLocalService.fetchSystemObjectDefinition(
						TestPropsValues.getCompanyId(), "AccountEntry"
					).getObjectDefinitionId(),
					objectDefinition.getObjectDefinitionId(), 0,
					ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					StringUtil.randomId(), false,
					ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null));

		Assert.assertTrue(
			objectDefinition.getAccountEntryRestrictedObjectFieldId() > 0);
		Assert.assertTrue(objectDefinition.isAccountEntryRestricted());
		Assert.assertFalse(objectDefinition.isSystem());

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		AssertUtils.assertFailure(
			ObjectDefinitionAccountEntryRestrictedException.class,
			"Custom object definitions can only be restricted by account entry",
			() -> _objectDefinitionLocalService.enableAccountEntryRestricted(
				_objectRelationshipLocalService.addObjectRelationship(
					null, TestPropsValues.getUserId(),
					_addCustomObjectDefinition(
						"Test" + RandomTestUtil.randomString()
					).getObjectDefinitionId(),
					_addCustomObjectDefinition(
						"Test" + RandomTestUtil.randomString()
					).getObjectDefinitionId(),
					0, ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					StringUtil.randomId(), false,
					ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null)));
	}

	@Test
	public void testGetClassName() throws Exception {
		ObjectDefinition objectDefinition1 =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		String originalName = PrincipalThreadLocal.getName();

		Company company = CompanyTestUtil.addCompany();

		PortalInstances.initCompany(company);

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			User user = UserTestUtil.getAdminUser(company.getCompanyId());

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));
			PrincipalThreadLocal.setName(user.getUserId());

			ObjectDefinition objectDefinition2 =
				_objectDefinitionLocalService.addCustomObjectDefinition(
					user.getUserId(), 0, objectDefinition1.getClassName(),
					false, true, false, true, false, false, false, false, false,
					null,
					LocalizedMapUtil.getLocalizedMap(
						objectDefinition1.getLabel()),
					objectDefinition1.getShortName(), null, null,
					LocalizedMapUtil.getLocalizedMap(
						objectDefinition1.getPluralLabel()),
					true, ObjectDefinitionConstants.SCOPE_COMPANY,
					ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
					Collections.emptyList(),
					Arrays.asList(
						new TextObjectFieldBuilder(
						).labelMap(
							LocalizedMapUtil.getLocalizedMap(
								RandomTestUtil.randomString())
						).name(
							StringUtil.randomId()
						).build()),
					Collections.emptyList());

			objectDefinition2 =
				_objectDefinitionLocalService.publishCustomObjectDefinition(
					user.getUserId(),
					objectDefinition2.getObjectDefinitionId());

			Assert.assertNotEquals(
				objectDefinition1.getClassName(),
				objectDefinition2.getClassName());
		}
		finally {
			_companyLocalService.deleteCompany(company);

			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
			PrincipalThreadLocal.setName(originalName);
		}
	}

	@Test
	public void testPublishCustomObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition1 =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				false,
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"textObjectField"
					).localized(
						true
					).build()));

		ObjectDefinition finalObjectDefinition = objectDefinition1;

		AssertUtils.assertFailure(
			ObjectDefinitionEnableLocalizationException.class,
			"You cannot disable entry translation for the object definition " +
				"because translation is enabled for custom fields",
			() -> _objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				finalObjectDefinition.getObjectDefinitionId()));

		objectDefinition1.setEnableLocalization(true);
		objectDefinition1.setName(ObjectDefinitionTestUtil.getRandomName());

		objectDefinition1 = _updateCustomObjectDefinition(
			null, objectDefinition1);

		objectDefinition1 =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition1.getObjectDefinitionId());

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectDefinition1.getObjectDefinitionId(), "textObjectField");

		Assert.assertEquals(
			objectDefinition1.getDBTableName(), objectField.getDBTableName());

		ObjectDefinition objectDefinition2 = _publishCustomObjectDefinition(
			false);

		Assert.assertNull(
			IndexerRegistryUtil.getIndexer(objectDefinition2.getClassName()));

		ObjectDefinition objectDefinition3 = _publishCustomObjectDefinition(
			true);

		Assert.assertNotNull(
			IndexerRegistryUtil.getIndexer(objectDefinition3.getClassName()));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition1);
		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition2);
		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition3);
	}

	@Test
	public void testPublishObjectDefinitionWithFriendlyURLSeparator()
		throws Exception {

		ObjectDefinition objectDefinition1 =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		Assert.assertEquals(
			FriendlyURLResolverConstants.URL_SEPARATOR_Y_OBJECT_ENTRY,
			objectDefinition1.getFriendlyURLSeparator());

		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		Assert.assertEquals(
			FriendlyURLResolverConstants.URL_SEPARATOR_Y_OBJECT_ENTRY,
			objectDefinition2.getFriendlyURLSeparator());

		ObjectDefinition objectDefinition3 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				false, "Test1",
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						StringUtil.randomId()
					).build()),
				ObjectDefinitionConstants.SCOPE_COMPANY,
				TestPropsValues.getUserId());

		Assert.assertEquals(
			"c_test1", objectDefinition3.getFriendlyURLSeparator());

		AssertUtils.assertFailure(
			ObjectDefinitionFriendlyURLSeparatorException.class,
			"Other asset types may use this prefix.",
			() -> ObjectDefinitionTestUtil.publishObjectDefinition(
				false, "Test",
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						StringUtil.randomId()
					).build()),
				ObjectDefinitionConstants.SCOPE_COMPANY,
				TestPropsValues.getUserId()));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition1);
		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition2);
		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition3);
		_objectDefinitionLocalService.deleteObjectDefinition(
			_objectDefinitionLocalService.fetchObjectDefinition(
				TestPropsValues.getCompanyId(), "C_Test"));
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testSystemObjectFields() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, false, true,
				false, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test", null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList());

		_testSystemObjectFields(9, objectDefinition);

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		objectDefinition =
			ObjectDefinitionTestUtil.addUnmodifiableSystemObjectDefinition(
				null, TestPropsValues.getUserId(), "Test", null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test", null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionConstants.SCOPE_COMPANY, null, 1,
				Collections.<ObjectField>emptyList());

		_testSystemObjectFields(6, objectDefinition);

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testUpdateCustomObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, false, true,
				false, false, true, true, true, null,
				LocalizedMapUtil.getLocalizedMap("Able"), "Able", null, null,
				LocalizedMapUtil.getLocalizedMap("Ables"), true,
				ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_SALESFORCE,
				Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList());

		_assertLabelAndPluralLabel(objectDefinition, "Able", "Ables");

		Assert.assertFalse(objectDefinition.isActive());
		Assert.assertTrue(objectDefinition.isEnableFormContainer());
		Assert.assertFalse(objectDefinition.isEnableFriendlyURLCustomization());
		Assert.assertTrue(objectDefinition.isEnableObjectEntrySchedule());
		Assert.assertTrue(objectDefinition.isEnableObjectEntrySubscription());
		Assert.assertTrue(objectDefinition.isEnableObjectEntryVersioning());
		Assert.assertTrue(
			Validator.isBlank(objectDefinition.getFriendlyURLSeparator()));
		Assert.assertEquals("C_Able", objectDefinition.getName());
		Assert.assertEquals(
			ObjectDefinitionConstants.STORAGE_TYPE_SALESFORCE,
			objectDefinition.getStorageType());

		long objectDefinitionId = objectDefinition.getObjectDefinitionId();
		String scope = objectDefinition.getScope();
		int status = objectDefinition.getStatus();

		AssertUtils.assertFailure(
			ObjectDefinitionEnableFriendlyURLCustomizationException.class,
			"Enable friendly URL customization is only allowed for object " +
				"definitions with the default storage type",
			() -> _updateCustomObjectDefinition(
				null, objectDefinitionId, 0, 0, true, true, false, false, null,
				LocalizedMapUtil.getLocalizedMap("Able"), "Able",
				LocalizedMapUtil.getLocalizedMap("Ables"), scope, status));
		AssertUtils.assertFailure(
			ObjectDefinitionEnableObjectEntryHistoryException.class,
			"Enable object entry history is only allowed for object " +
				"definitions with the default storage type",
			() -> _updateCustomObjectDefinition(
				null, objectDefinitionId, 0, 0, false, true, false, false, null,
				LocalizedMapUtil.getLocalizedMap("Able"), "Able",
				LocalizedMapUtil.getLocalizedMap("Ables"), scope, status));
		AssertUtils.assertFailure(
			ObjectDefinitionExternalReferenceCodeException.
				MustNotStartWithPrefix.class,
			"The prefix L_ is reserved",
			() -> _updateCustomObjectDefinition(
				"L_INVALID_ERC_TEST", objectDefinitionId, 0, 0, false, false,
				false, false, null, LocalizedMapUtil.getLocalizedMap("Able"),
				"Able", LocalizedMapUtil.getLocalizedMap("Ables"), scope,
				status));

		objectDefinition.setStorageType(
			ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT);

		objectDefinition = _objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);

		Assert.assertEquals(
			ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
			objectDefinition.getStorageType());

		AssertUtils.assertFailure(
			NoSuchObjectFieldException.class, null,
			() -> _updateCustomObjectDefinition(
				null, objectDefinitionId, RandomTestUtil.randomLong(),
				RandomTestUtil.randomLong(), false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap("Able"), "Able",
				LocalizedMapUtil.getLocalizedMap("Ables"), scope, status));

		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				StringUtil.randomId()
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).required(
				true
			).build());

		Locale locale = LocaleUtil.getDefault();

		try {
			LocaleUtil.setDefault(
				LocaleUtil.BRAZIL.getLanguage(), LocaleUtil.BRAZIL.getCountry(),
				LocaleUtil.BRAZIL.getVariant());

			String defaultLanguageId = objectDefinition.getDefaultLanguageId();

			objectDefinition =
				_objectDefinitionLocalService.updateCustomObjectDefinition(
					null, objectDefinition.getObjectDefinitionId(), 0,
					objectField.getObjectFieldId(), 0,
					objectField.getObjectFieldId(), false,
					objectDefinition.isActive(),
					objectDefinition.getClassName(), true, false, true, false,
					true, false, false, false, false, false, false, null,
					HashMapBuilder.put(
						locale, RandomTestUtil.randomString()
					).put(
						LocaleUtil.BRAZIL, RandomTestUtil.randomString()
					).build(),
					"Able", null, null, false,
					HashMapBuilder.put(
						locale, RandomTestUtil.randomString()
					).put(
						LocaleUtil.BRAZIL, RandomTestUtil.randomString()
					).build(),
					objectDefinition.getScope(), objectDefinition.getStatus(),
					Collections.emptyList(), Collections.emptyList());

			Assert.assertEquals(
				objectField.getObjectFieldId(),
				objectDefinition.getDescriptionObjectFieldId());
			Assert.assertEquals(
				objectField.getObjectFieldId(),
				objectDefinition.getTitleObjectFieldId());
			Assert.assertEquals(
				defaultLanguageId, objectDefinition.getDefaultLanguageId());
		}
		finally {
			LocaleUtil.setDefault(
				locale.getLanguage(), locale.getCountry(), locale.getVariant());
		}

		String externalReferenceCode = RandomTestUtil.randomString();

		ObjectFolder objectFolder = _addObjectFolder();

		objectDefinition =
			_objectDefinitionLocalService.updateCustomObjectDefinition(
				externalReferenceCode, objectDefinition.getObjectDefinitionId(),
				0, 0, objectFolder.getObjectFolderId(), 0, false,
				objectDefinition.isActive(), objectDefinition.getClassName(),
				true, false, false, true, false, false, false, false, false,
				false, false, "test", LocalizedMapUtil.getLocalizedMap("Able"),
				"Able", null, null, false,
				LocalizedMapUtil.getLocalizedMap("Ables"),
				objectDefinition.getScope(), objectDefinition.getStatus(),
				Collections.emptyList(), Collections.emptyList());

		Assert.assertEquals(
			externalReferenceCode, objectDefinition.getExternalReferenceCode());
		Assert.assertEquals(0, objectDefinition.getDescriptionObjectFieldId());
		Assert.assertEquals(
			objectFolder.getObjectFolderId(),
			objectDefinition.getObjectFolderId());
		Assert.assertEquals(0, objectDefinition.getTitleObjectFieldId());
		Assert.assertFalse(objectDefinition.isActive());
		Assert.assertFalse(objectDefinition.isEnableFormContainer());
		Assert.assertTrue(objectDefinition.isEnableFriendlyURLCustomization());
		Assert.assertFalse(objectDefinition.isEnableIndexSearch());
		Assert.assertFalse(objectDefinition.isEnableObjectEntryHistory());
		Assert.assertFalse(objectDefinition.isEnableObjectEntrySchedule());
		Assert.assertFalse(objectDefinition.isEnableObjectEntrySubscription());
		Assert.assertFalse(objectDefinition.isEnableObjectEntryVersioning());
		Assert.assertEquals("test", objectDefinition.getFriendlyURLSeparator());
		Assert.assertEquals(
			LocalizedMapUtil.getLocalizedMap("Able"),
			objectDefinition.getLabelMap());
		Assert.assertEquals("C_Able", objectDefinition.getName());
		Assert.assertEquals(
			LocalizedMapUtil.getLocalizedMap("Ables"),
			objectDefinition.getPluralLabelMap());

		objectDefinition =
			_objectDefinitionLocalService.updateCustomObjectDefinition(
				null, objectDefinition.getObjectDefinitionId(), 0, 0, 0, 0,
				false, objectDefinition.isActive(), null, true, false, true,
				false, true, false, false, true, true, true, true,
				FriendlyURLResolverConstants.URL_SEPARATOR_Y_OBJECT_ENTRY,
				LocalizedMapUtil.getLocalizedMap("Baker"), "Baker", null, null,
				false, LocalizedMapUtil.getLocalizedMap("Bakers"),
				objectDefinition.getScope(), objectDefinition.getStatus(),
				Collections.emptyList(), Collections.emptyList());

		_assertLabelAndPluralLabel(objectDefinition, "Baker", "Bakers");

		Assert.assertFalse(objectDefinition.isActive());
		Assert.assertTrue(objectDefinition.isEnableFormContainer());
		Assert.assertFalse(objectDefinition.isEnableFriendlyURLCustomization());
		Assert.assertTrue(objectDefinition.isEnableIndexSearch());
		Assert.assertTrue(objectDefinition.isEnableObjectEntryHistory());
		Assert.assertTrue(objectDefinition.isEnableObjectEntrySchedule());
		Assert.assertTrue(objectDefinition.isEnableObjectEntrySubscription());
		Assert.assertTrue(objectDefinition.isEnableObjectEntryVersioning());
		Assert.assertEquals(
			FriendlyURLResolverConstants.URL_SEPARATOR_Y_OBJECT_ENTRY,
			objectDefinition.getFriendlyURLSeparator());
		Assert.assertEquals("C_Baker", objectDefinition.getName());

		objectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		AssertUtils.assertFailure(
			ObjectDefinitionEnableFriendlyURLCustomizationException.class,
			"Enable friendly URL customization is not allowed when using the " +
				"default friendly URL separator",
			() -> _updateCustomObjectDefinition(
				null, objectDefinitionId, 0, 0, true, true, true, true,
				FriendlyURLResolverConstants.URL_SEPARATOR_Y_OBJECT_ENTRY,
				LocalizedMapUtil.getLocalizedMap("Charlie"), "Charlie",
				LocalizedMapUtil.getLocalizedMap("Charlies"), scope, status));
		AssertUtils.assertFailure(
			ObjectDefinitionEnableObjectEntryScheduleException.class,
			"Object entry schedule cannot be disabled when the object " +
				"definition is published",
			() -> _updateCustomObjectDefinition(
				null, objectDefinitionId, 0, 0, false, true, false, true, null,
				LocalizedMapUtil.getLocalizedMap("Charlie"), "Charlie",
				LocalizedMapUtil.getLocalizedMap("Charlies"), scope, status));
		AssertUtils.assertFailure(
			ObjectDefinitionEnableObjectEntryVersioningException.class,
			"Object entry versioning cannot be disabled when the object " +
				"definition is published",
			() -> _updateCustomObjectDefinition(
				null, objectDefinitionId, 0, 0, false, true, true, false, null,
				LocalizedMapUtil.getLocalizedMap("Charlie"), "Charlie",
				LocalizedMapUtil.getLocalizedMap("Charlies"), scope, status));

		objectDefinition =
			_objectDefinitionLocalService.updateCustomObjectDefinition(
				null, objectDefinition.getObjectDefinitionId(), 0, 0, 0, 0,
				false, true, objectDefinition.getClassName(), true, false, true,
				true, false, false, false, true, true, true, true, null,
				LocalizedMapUtil.getLocalizedMap("Charlie"), "Charlie", null,
				null, false, LocalizedMapUtil.getLocalizedMap("Charlies"),
				objectDefinition.getScope(), objectDefinition.getStatus(),
				Collections.emptyList(), Collections.emptyList());

		_assertLabelAndPluralLabel(objectDefinition, "Charlie", "Charlies");

		Assert.assertTrue(objectDefinition.isActive());
		Assert.assertTrue(objectDefinition.isEnableFormContainer());
		Assert.assertTrue(objectDefinition.isEnableFriendlyURLCustomization());
		Assert.assertTrue(objectDefinition.isEnableIndexSearch());
		Assert.assertTrue(objectDefinition.isEnableObjectEntryHistory());
		Assert.assertTrue(objectDefinition.isEnableObjectEntrySchedule());
		Assert.assertTrue(objectDefinition.isEnableObjectEntrySubscription());
		Assert.assertTrue(objectDefinition.isEnableObjectEntryVersioning());
		Assert.assertEquals(
			"c_baker", objectDefinition.getFriendlyURLSeparator());
		Assert.assertEquals("C_Baker", objectDefinition.getName());

		_testUpdateCustomObjectDefinitionThrowsObjectFieldRelationshipTypeException(
			objectDefinition);

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		_objectFolderLocalService.deleteObjectFolder(objectFolder);
	}

	@Test
	public void testUpdateExternalReferenceCode() throws Exception {
		ObjectDefinition customObjectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, false, true,
				false, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap("Able"), "Able", null, null,
				LocalizedMapUtil.getLocalizedMap("Ables"), false,
				ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList());

		_objectDefinitionLocalService.updateExternalReferenceCode(
			customObjectDefinition.getObjectDefinitionId(), "TEST_ERC");

		AssertUtils.assertFailure(
			ObjectDefinitionExternalReferenceCodeException.
				MustNotStartWithPrefix.class,
			"The prefix L_ is reserved",
			() -> _objectDefinitionLocalService.updateExternalReferenceCode(
				customObjectDefinition.getObjectDefinitionId(),
				"L_INVALID_ERC_TEST"));

		ObjectDefinition unmodifiableSystemObjectDefinition =
			_addUnmodifiableSystemObjectDefinition("Unmodifiable");

		_objectDefinitionLocalService.updateExternalReferenceCode(
			unmodifiableSystemObjectDefinition.getObjectDefinitionId(),
			"L_TEST_ERC");

		_objectDefinitionLocalService.deleteObjectDefinition(
			customObjectDefinition);
		_objectDefinitionLocalService.deleteObjectDefinition(
			unmodifiableSystemObjectDefinition);
	}

	@Test
	public void testUpdateObjectFolderId() throws Exception {
		ObjectDefinition objectDefinition = _addCustomObjectDefinition(
			ObjectDefinitionTestUtil.getRandomName());

		Assert.assertEquals(
			_defaultObjectFolder.getObjectFolderId(),
			objectDefinition.getObjectFolderId());

		ObjectFolder objectFolder = _addObjectFolder();

		objectDefinition = _objectDefinitionLocalService.updateObjectFolderId(
			objectDefinition.getObjectDefinitionId(),
			objectFolder.getObjectFolderId());

		Assert.assertEquals(
			objectFolder.getObjectFolderId(),
			objectDefinition.getObjectFolderId());

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		_objectFolderLocalService.deleteObjectFolder(objectFolder);
	}

	@Test
	public void testUpdateRootDescendantObjectDefinition() throws Exception {
		ObjectDefinition objectDefinitionA =
			ObjectDefinitionTestUtil.publishObjectDefinition();
		ObjectDefinition objectDefinitionAA =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		TreeTestUtil.bind(
			objectDefinitionA.getObjectDefinitionId(),
			objectDefinitionAA.getObjectDefinitionId(),
			_objectRelationshipLocalService);

		String panelCategoryKey = RandomTestUtil.randomString();

		objectDefinitionAA =
			_objectDefinitionLocalService.updateCustomObjectDefinition(
				objectDefinitionAA.getExternalReferenceCode(),
				objectDefinitionAA.getObjectDefinitionId(),
				objectDefinitionAA.getAccountEntryRestrictedObjectFieldId(),
				objectDefinitionAA.getDescriptionObjectFieldId(),
				objectDefinitionAA.getObjectFolderId(),
				objectDefinitionAA.getTitleObjectFieldId(),
				objectDefinitionAA.isAccountEntryRestricted(),
				objectDefinitionAA.isActive(),
				objectDefinitionAA.getClassName(),
				objectDefinitionAA.isEnableCategorization(),
				objectDefinitionAA.isEnableComments(),
				objectDefinitionAA.isEnableFormContainer(),
				objectDefinitionAA.isEnableFriendlyURLCustomization(),
				objectDefinitionAA.isEnableIndexSearch(),
				objectDefinitionAA.isEnableLocalization(),
				objectDefinitionAA.isEnableObjectEntryDraft(),
				objectDefinitionAA.isEnableObjectEntryHistory(),
				objectDefinitionAA.isEnableObjectEntrySchedule(),
				objectDefinitionAA.isEnableObjectEntrySubscription(),
				objectDefinitionAA.isEnableObjectEntryVersioning(),
				objectDefinitionAA.getFriendlyURLSeparator(),
				objectDefinitionAA.getLabelMap(), objectDefinitionAA.getName(),
				objectDefinitionAA.getPanelAppOrder(), panelCategoryKey,
				objectDefinitionAA.isPortlet(),
				objectDefinitionAA.getPluralLabelMap(),
				objectDefinitionAA.getScope(), objectDefinitionAA.getStatus(),
				Collections.emptyList(), Collections.emptyList());

		Assert.assertEquals(
			panelCategoryKey, objectDefinitionAA.getPanelCategoryKey());
	}

	@FeatureFlag("LPD-32050")
	@Test
	public void testUpdateSystemObjectDefinition() throws Exception {

		// Before update, assert validations criterias

		ObjectDefinition objectDefinition1 =
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

		// Enable localization must be true for modifiable object definitions

		AssertUtils.assertFailure(
			ObjectDefinitionEnableLocalizationException.class,
			"Enable localization must be true for modifiable object " +
				"definitions",
			() -> _objectDefinitionLocalService.updateCustomObjectDefinition(
				null, objectDefinition1.getObjectDefinitionId(), 0, 0, 0, 0,
				false, false, objectDefinition1.getClassName(), false, true,
				true, false, true, false, false, false, false, false, false,
				null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				RandomTestUtil.randomString(), null, null, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionConstants.SCOPE_SITE,
				objectDefinition1.getStatus(), Collections.emptyList(),
				Collections.emptyList()));

		// Object folder does not exist

		long objectFolderId = RandomTestUtil.randomLong();

		AssertUtils.assertFailure(
			NoSuchObjectFolderException.class,
			"No ObjectFolder exists with the primary key " + objectFolderId,
			() -> _objectDefinitionLocalService.updateCustomObjectDefinition(
				null, objectDefinition1.getObjectDefinitionId(), 0, 0,
				objectFolderId, 0, false, false,
				objectDefinition1.getClassName(), false, true, true, false,
				true, true, false, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap("Charlie"), "Charlie", null,
				null, false, LocalizedMapUtil.getLocalizedMap("Charlie"),
				ObjectDefinitionConstants.SCOPE_SITE,
				objectDefinition1.getStatus(), Collections.emptyList(),
				Collections.emptyList()));

		// Modifiable system object definition must be published to be actived

		AssertUtils.assertFailure(
			ObjectDefinitionActiveException.class,
			"Object definitions must be published before being activated",
			() -> _objectDefinitionLocalService.updateCustomObjectDefinition(
				null, objectDefinition1.getObjectDefinitionId(), 0, 0, 0, 0,
				false, true, objectDefinition1.getClassName(), false, true,
				true, false, true, true, false, false, false, false, false,
				null, LocalizedMapUtil.getLocalizedMap("Charlie"), "Charlie",
				null, null, false, LocalizedMapUtil.getLocalizedMap("Charlies"),
				ObjectDefinitionConstants.SCOPE_SITE,
				objectDefinition1.getStatus(), Collections.emptyList(),
				Collections.emptyList()));

		// Label is null

		AssertUtils.assertFailure(
			ObjectDefinitionLabelException.class,
			"Label is null for locale " + LocaleUtil.US.getDisplayName(),
			() -> _objectDefinitionLocalService.updateCustomObjectDefinition(
				null, objectDefinition1.getObjectDefinitionId(), 0, 0, 0, 0,
				false, false, objectDefinition1.getClassName(), false, true,
				true, false, true, true, false, false, false, false, false,
				null, null, "Charlie", null, null, false,
				LocalizedMapUtil.getLocalizedMap("Charlie"),
				ObjectDefinitionConstants.SCOPE_SITE,
				objectDefinition1.getStatus(), Collections.emptyList(),
				Collections.emptyList()));

		// Plural label is null

		AssertUtils.assertFailure(
			ObjectDefinitionPluralLabelException.class,
			"Plural label is null for locale " + LocaleUtil.US.getDisplayName(),
			() -> _objectDefinitionLocalService.updateCustomObjectDefinition(
				null, objectDefinition1.getObjectDefinitionId(), 0, 0, 0, 0,
				false, false, objectDefinition1.getClassName(), false, true,
				true, false, true, true, false, false, false, false, false,
				null, LocalizedMapUtil.getLocalizedMap("Charlie"), "Charlie",
				null, null, false, null, ObjectDefinitionConstants.SCOPE_SITE,
				objectDefinition1.getStatus(), Collections.emptyList(),
				Collections.emptyList()));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition1);

		// After update, a modifiable system object definition check its
		// properties

		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.addModifiableSystemObjectDefinition(
				TestPropsValues.getUserId(), null, true,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test", null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionConstants.SCOPE_SITE, null, 1,
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())));

		Assert.assertTrue(objectDefinition2.isEnableLocalization());

		objectDefinition2 =
			_objectDefinitionLocalService.publishSystemObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition2.getObjectDefinitionId());

		ObjectFolder objectFolder = _addObjectFolder();

		objectDefinition2 =
			_objectDefinitionLocalService.updateCustomObjectDefinition(
				null, objectDefinition2.getObjectDefinitionId(), 0, 0,
				objectFolder.getObjectFolderId(), 0, false, true,
				objectDefinition2.getClassName(), false, true, true, false,
				true, true, false, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap("Charlie"), "Charlie", null,
				null, false, LocalizedMapUtil.getLocalizedMap("Charlies"),
				objectDefinition2.getScope(), objectDefinition2.getStatus(),
				Collections.emptyList(), Collections.emptyList());

		_assertLabelAndPluralLabel(objectDefinition2, "Charlie", "Charlies");

		Assert.assertEquals(
			objectFolder.getObjectFolderId(),
			objectDefinition2.getObjectFolderId());
		Assert.assertFalse(objectDefinition2.isEnableCategorization());
		Assert.assertTrue(objectDefinition2.isEnableComments());
		Assert.assertTrue(objectDefinition2.isEnableFormContainer());
		Assert.assertTrue(objectDefinition2.isEnableLocalization());
		Assert.assertEquals("Test", objectDefinition2.getName());

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition2);

		// After update, an unmodifiable system object definition check its
		// properties

		objectDefinition2 =
			ObjectDefinitionTestUtil.addUnmodifiableSystemObjectDefinition(
				null, TestPropsValues.getUserId(), "Test", null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test", null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionConstants.SCOPE_COMPANY, null, 1,
				Collections.<ObjectField>emptyList());

		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).labelMap(
				LocalizedMapUtil.getLocalizedMap("Able")
			).name(
				"able"
			).objectDefinitionId(
				objectDefinition2.getObjectDefinitionId()
			).required(
				true
			).build());

		String externalReferenceCode = RandomTestUtil.randomString();

		objectDefinition2 =
			_objectDefinitionLocalService.updateSystemObjectDefinition(
				externalReferenceCode,
				objectDefinition2.getObjectDefinitionId(),
				objectFolder.getObjectFolderId(),
				objectField.getObjectFieldId(), Collections.emptyList(),
				Collections.emptyList());

		Assert.assertEquals(
			externalReferenceCode,
			objectDefinition2.getExternalReferenceCode());
		Assert.assertEquals(
			objectFolder.getObjectFolderId(),
			objectDefinition2.getObjectFolderId());
		Assert.assertEquals(
			objectField.getObjectFieldId(),
			objectDefinition2.getTitleObjectFieldId());

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition2);

		_objectFolderLocalService.deleteObjectFolder(objectFolder);
	}

	@Test
	public void testUpdateTitleObjectFieldId() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		try {
			objectDefinition =
				_objectDefinitionLocalService.updateTitleObjectFieldId(
					objectDefinition.getObjectDefinitionId(),
					RandomTestUtil.randomLong());

			Assert.fail();
		}
		catch (NoSuchObjectFieldException noSuchObjectFieldException) {
			Assert.assertNotNull(noSuchObjectFieldException);
		}

		ObjectField objectField1 = _objectFieldLocalService.getObjectField(
			objectDefinition.getObjectDefinitionId(), "externalReferenceCode");

		Assert.assertEquals(
			objectField1.getObjectFieldId(),
			objectDefinition.getTitleObjectFieldId());

		ObjectField objectField2 = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				StringUtil.randomId()
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).required(
				true
			).build());

		objectDefinition =
			_objectDefinitionLocalService.updateTitleObjectFieldId(
				objectDefinition.getObjectDefinitionId(),
				objectField2.getObjectFieldId());

		Assert.assertEquals(
			objectField2.getObjectFieldId(),
			objectDefinition.getTitleObjectFieldId());

		_objectFieldLocalService.deleteObjectField(
			objectField2.getObjectFieldId());

		objectDefinition = _objectDefinitionLocalService.getObjectDefinition(
			objectDefinition.getObjectDefinitionId());

		Assert.assertEquals(
			objectField1.getObjectFieldId(),
			objectDefinition.getTitleObjectFieldId());

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	private ObjectDefinition _addCustomObjectDefinition(String name)
		throws Exception {

		return _addCustomObjectDefinition(null, name, name, name);
	}

	private ObjectDefinition _addCustomObjectDefinition(
			String className, String name)
		throws Exception {

		return _addCustomObjectDefinition(className, name, name, name);
	}

	private ObjectDefinition _addCustomObjectDefinition(
			String label, String name, String pluralLabel)
		throws Exception {

		return _addCustomObjectDefinition(null, label, name, pluralLabel);
	}

	private ObjectDefinition _addCustomObjectDefinition(
			String className, String label, String name, String pluralLabel)
		throws Exception {

		return _objectDefinitionLocalService.addCustomObjectDefinition(
			TestPropsValues.getUserId(), 0, className, false, true, false, true,
			false, false, false, false, false, null,
			LocalizedMapUtil.getLocalizedMap(label), name, null, null,
			LocalizedMapUtil.getLocalizedMap(pluralLabel), true,
			ObjectDefinitionConstants.SCOPE_COMPANY,
			ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
			Collections.emptyList(),
			Arrays.asList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING,
					RandomTestUtil.randomString(), StringUtil.randomId())),
			Collections.emptyList());
	}

	private ObjectFolder _addObjectFolder() throws Exception {
		return _objectFolderLocalService.addObjectFolder(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString());
	}

	private void _addSharingEntry(
			String className, long classPK, long userGroupId)
		throws Exception {

		_sharingEntryLocalService.addSharingEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			userGroupId, 0, _classNameLocalService.getClassNameId(className),
			classPK, TestPropsValues.getGroupId(), true,
			Arrays.asList(SharingEntryAction.VIEW), null,
			ServiceContextTestUtil.getServiceContext());
	}

	private ObjectDefinition _addUnmodifiableSystemObjectDefinition(String name)
		throws Exception {

		return _addUnmodifiableSystemObjectDefinition(
			RandomTestUtil.randomString(), name, RandomTestUtil.randomString());
	}

	private ObjectDefinition _addUnmodifiableSystemObjectDefinition(
			String label, String name, String pluralLabel)
		throws Exception {

		return ObjectDefinitionTestUtil.addUnmodifiableSystemObjectDefinition(
			null, TestPropsValues.getUserId(), name, null,
			LocalizedMapUtil.getLocalizedMap(label), name, null, null,
			LocalizedMapUtil.getLocalizedMap(pluralLabel),
			ObjectDefinitionConstants.SCOPE_COMPANY, null, 1,
			Arrays.asList(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					StringUtil.randomId()
				).build()));
	}

	private void _assertLabelAndPluralLabel(
		ObjectDefinition objectDefinition, String label, String pluralLabel) {

		Assert.assertEquals(
			LocalizedMapUtil.getLocalizedMap(label),
			objectDefinition.getLabelMap());

		PLOEntry labelPLOEntryKey = _ploEntryLocalService.fetchPLOEntry(
			objectDefinition.getCompanyId(),
			"model.resource." + objectDefinition.getClassName(),
			objectDefinition.getDefaultLanguageId());

		Assert.assertEquals(labelPLOEntryKey.getValue(), label);

		Assert.assertEquals(
			LocalizedMapUtil.getLocalizedMap(pluralLabel),
			objectDefinition.getPluralLabelMap());

		PLOEntry pluralLabelPLOEntryKey = _ploEntryLocalService.fetchPLOEntry(
			objectDefinition.getCompanyId(),
			"model.resource." + objectDefinition.getResourceName(),
			objectDefinition.getDefaultLanguageId());

		Assert.assertEquals(pluralLabelPLOEntryKey.getValue(), pluralLabel);
	}

	private void _assertObjectDefinitionSettingsValues(
		List<ObjectDefinitionSetting> objectDefinitionSettings,
		Map<String, String> objectDefinitionSettingsExpectedValues) {

		for (ObjectDefinitionSetting objectDefinitionSetting :
				objectDefinitionSettings) {

			Assert.assertEquals(
				objectDefinitionSettingsExpectedValues.get(
					objectDefinitionSetting.getName()),
				objectDefinitionSetting.getValue());
		}
	}

	private void _assertObjectField(
			ObjectDefinition objectDefinition, String dbColumnName,
			String dbType, String name, boolean required)
		throws Exception {

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectDefinition.getObjectDefinitionId(), name);

		Assert.assertEquals(dbColumnName, objectField.getDBColumnName());
		Assert.assertEquals(dbType, objectField.getDBType());
		Assert.assertFalse(objectField.isIndexed());
		Assert.assertFalse(objectField.isIndexedAsKeyword());
		Assert.assertEquals("", objectField.getIndexedLanguageId());
		Assert.assertEquals(required, objectField.isRequired());
	}

	private void _assertSystemObjectFields(
		ObjectField expectedObjectField, ObjectField objectField) {

		Assert.assertEquals(
			expectedObjectField.getDBColumnName(),
			objectField.getDBColumnName());
		Assert.assertEquals(
			expectedObjectField.getDBTableName(), objectField.getDBTableName());
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
		Assert.assertEquals(
			expectedObjectField.isRequired(), objectField.isRequired());
		Assert.assertEquals(
			expectedObjectField.isState(), objectField.isState());
	}

	private void _assertWorkflowDefinitionLink(
		WorkflowDefinitionLink expectedWorkflowDefinitionLink, long groupId,
		ObjectDefinition objectDefinition) {

		WorkflowDefinitionLink actualWorkflowDefinitionLink =
			_workflowDefinitionLinkLocalService.fetchWorkflowDefinitionLink(
				objectDefinition.getCompanyId(), groupId,
				objectDefinition.getClassName(), 0, 0, true);

		Assert.assertEquals(
			expectedWorkflowDefinitionLink.getGroupId(),
			actualWorkflowDefinitionLink.getGroupId());
	}

	private ObjectAction _createObjectAction(String objectActionName) {
		ObjectAction objectAction =
			ObjectActionLocalServiceUtil.createObjectAction(0);

		objectAction.setExternalReferenceCode(objectActionName);
		objectAction.setActive(true);
		objectAction.setConditionExpression(StringPool.BLANK);
		objectAction.setDescription(RandomTestUtil.randomString());
		objectAction.setErrorMessageMap(
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()));
		objectAction.setLabelMap(
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()));
		objectAction.setName(objectActionName);
		objectAction.setObjectActionExecutorKey(
			ObjectActionExecutorConstants.KEY_UPDATE_OBJECT_ENTRY);
		objectAction.setObjectActionTriggerKey(
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD);
		objectAction.setParameters(
			UnicodePropertiesBuilder.put(
				"objectDefinitionExternalReferenceCode",
				"L_USER_NOTIFICATION_EVENT"
			).put(
				"predefinedValues",
				JSONUtil.putAll(
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "deliveryType"
					).put(
						"value", UserNotificationDeliveryConstants.TYPE_SMS
					)
				).toString()
			).buildString());

		return objectAction;
	}

	private List<WorkflowDefinitionLink> _createWorkflowDefinitionLinks(
			long groupId)
		throws Exception {

		WorkflowDefinitionLink workflowDefinitionLink =
			_workflowDefinitionLinkLocalService.createWorkflowDefinitionLink(
				0L);

		workflowDefinitionLink.setGroupId(groupId);
		workflowDefinitionLink.setUserId(TestPropsValues.getUserId());
		workflowDefinitionLink.setWorkflowDefinitionName("Single Approver");

		return Collections.singletonList(workflowDefinitionLink);
	}

	private int _getObjectEntryVersionsCount(long objectDefinitionId) {
		return _objectEntryVersionLocalService.dslQueryCount(
			DSLQueryFactoryUtil.count(
			).from(
				ObjectEntryVersionTable.INSTANCE
			).innerJoinON(
				ObjectEntryTable.INSTANCE,
				ObjectEntryTable.INSTANCE.objectEntryId.eq(
					ObjectEntryVersionTable.INSTANCE.objectEntryId)
			).where(
				ObjectEntryTable.INSTANCE.objectDefinitionId.eq(
					objectDefinitionId)
			));
	}

	private int _getSharingEntriesCount(ObjectDefinition objectDefinition) {
		return _sharingEntryLocalService.getCompanySharingEntriesCount(
			objectDefinition.getCompanyId(),
			_classNameLocalService.getClassNameId(
				objectDefinition.getClassName()));
	}

	private boolean _hasColumn(String tableName, String columnName)
		throws Exception {

		try (Connection connection = DataAccess.getConnection()) {
			DBInspector dbInspector = new DBInspector(connection);

			return dbInspector.hasColumn(tableName, columnName);
		}
	}

	private boolean _hasTable(String tableName) throws Exception {
		try (Connection connection = DataAccess.getConnection()) {
			DBInspector dbInspector = new DBInspector(connection);

			return dbInspector.hasTable(tableName);
		}
	}

	private ObjectDefinition _publishCustomObjectDefinition(
			boolean enableIndexSearch)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, false,
				enableIndexSearch, false, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_SALESFORCE,
				Collections.emptyList(),
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())),
				Collections.emptyList());

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

	private ObjectDefinition _publishCustomObjectDefinition(
			String name, String scope,
			List<ObjectDefinitionSetting> objectDefinitionSettings)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, false, true,
				false, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(name), name, null, null,
				LocalizedMapUtil.getLocalizedMap(name), true, scope,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				objectDefinitionSettings,
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())),
				Collections.emptyList());

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			objectDefinition.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

	private void _testAddObjectDefinition(boolean modifiable, boolean system)
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();
		User user = TestPropsValues.getUser();
		ObjectFolder objectFolder = _addObjectFolder();

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addObjectDefinition(
				externalReferenceCode, user.getUserId(),
				objectFolder.getObjectFolderId(), modifiable,
				ObjectDefinitionConstants.SCOPE_COMPANY, system);

		_assertLabelAndPluralLabel(
			objectDefinition, externalReferenceCode, externalReferenceCode);

		Assert.assertEquals(
			externalReferenceCode, objectDefinition.getExternalReferenceCode());
		Assert.assertEquals(
			TestPropsValues.getCompanyId(), objectDefinition.getCompanyId());
		Assert.assertEquals(user.getUserId(), objectDefinition.getUserId());
		Assert.assertEquals(user.getFullName(), objectDefinition.getUserName());
		Assert.assertEquals(
			objectFolder.getObjectFolderId(),
			objectDefinition.getObjectFolderId());
		Assert.assertFalse(objectDefinition.isAccountEntryRestricted());
		Assert.assertFalse(objectDefinition.isActive());
		Assert.assertEquals(
			StringPool.BLANK, objectDefinition.getDBTableName());
		Assert.assertFalse(objectDefinition.isEnableCategorization());
		Assert.assertFalse(objectDefinition.isEnableComments());
		Assert.assertFalse(objectDefinition.isEnableIndexSearch());
		Assert.assertFalse(objectDefinition.isEnableLocalization());
		Assert.assertFalse(objectDefinition.isEnableObjectEntryHistory());
		Assert.assertEquals(modifiable, objectDefinition.isModifiable());
		Assert.assertEquals(externalReferenceCode, objectDefinition.getName());
		Assert.assertEquals(
			ObjectDefinitionConstants.SCOPE_COMPANY,
			objectDefinition.getScope());
		Assert.assertEquals(
			ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
			objectDefinition.getStorageType());
		Assert.assertEquals(system, objectDefinition.isSystem());
		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, objectDefinition.getStatus());

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		_objectFolderLocalService.deleteObjectFolder(objectFolder);
	}

	private void _testSystemObjectFields(
			int count, ObjectDefinition objectDefinition)
		throws Exception {

		List<ObjectField> objectFields =
			_objectFieldLocalService.getObjectFields(
				objectDefinition.getObjectDefinitionId());

		Assert.assertNotNull(objectFields);

		boolean system = objectDefinition.isSystem();

		Assert.assertEquals(
			objectFields.toString(), count, objectFields.size());

		ListIterator<ObjectField> iterator = objectFields.listIterator();

		Assert.assertTrue(iterator.hasNext());

		String dbColumnName = null;
		String dbTableName = null;

		ObjectEntryTable objectEntryTable = ObjectEntryTable.INSTANCE;

		if (system) {
			dbColumnName = TextFormatter.format(
				objectDefinition.getShortName() + "Id", TextFormatter.I);
			dbTableName = objectDefinition.getDBTableName();
		}
		else {
			dbColumnName = objectEntryTable.objectEntryId.getName();
			dbTableName = objectEntryTable.getTableName();
		}

		_assertSystemObjectFields(
			new DateObjectFieldBuilder(
			).dbColumnName(
				objectEntryTable.createDate.getName()
			).dbTableName(
				dbTableName
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(
					LanguageUtil.get(LocaleUtil.getDefault(), "create-date"))
			).name(
				"createDate"
			).build(),
			iterator.next());

		Assert.assertTrue(iterator.hasNext());

		_assertSystemObjectFields(
			new TextObjectFieldBuilder(
			).dbColumnName(
				objectEntryTable.userName.getName()
			).dbTableName(
				dbTableName
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(
					LanguageUtil.get(LocaleUtil.getDefault(), "author"))
			).name(
				"creator"
			).build(),
			iterator.next());

		Assert.assertTrue(iterator.hasNext());

		if (!objectDefinition.isUnmodifiableSystemObject()) {
			_assertSystemObjectFields(
				new DateTimeObjectFieldBuilder(
				).dbColumnName(
					ObjectEntryTable.INSTANCE.displayDate.getName()
				).dbTableName(
					dbTableName
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						LanguageUtil.get(
							LocaleUtil.getDefault(), "display-date"))
				).name(
					"displayDate"
				).objectFieldSettings(
					Collections.singletonList(
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_TIME_STORAGE
						).value(
							ObjectFieldSettingConstants.VALUE_CONVERT_TO_UTC
						).build())
				).build(),
				iterator.next());

			Assert.assertTrue(iterator.hasNext());

			_assertSystemObjectFields(
				new DateTimeObjectFieldBuilder(
				).dbColumnName(
					ObjectEntryTable.INSTANCE.expirationDate.getName()
				).dbTableName(
					dbTableName
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						LanguageUtil.get(
							LocaleUtil.getDefault(), "expiration-date"))
				).name(
					"expirationDate"
				).objectFieldSettings(
					Collections.singletonList(
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_TIME_STORAGE
						).value(
							ObjectFieldSettingConstants.VALUE_CONVERT_TO_UTC
						).build())
				).build(),
				iterator.next());

			Assert.assertTrue(iterator.hasNext());
		}

		_assertSystemObjectFields(
			new TextObjectFieldBuilder(
			).dbColumnName(
				objectEntryTable.externalReferenceCode.getName()
			).dbTableName(
				dbTableName
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(
					LanguageUtil.get(
						LocaleUtil.getDefault(), "external-reference-code"))
			).name(
				"externalReferenceCode"
			).build(),
			iterator.next());

		Assert.assertTrue(iterator.hasNext());

		_assertSystemObjectFields(
			new LongIntegerObjectFieldBuilder(
			).dbColumnName(
				dbColumnName
			).dbTableName(
				dbTableName
			).indexed(
				Boolean.TRUE
			).indexedAsKeyword(
				Boolean.TRUE
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(
					LanguageUtil.get(LocaleUtil.getDefault(), "id"))
			).name(
				"id"
			).build(),
			iterator.next());

		Assert.assertTrue(iterator.hasNext());

		_assertSystemObjectFields(
			new DateObjectFieldBuilder(
			).dbColumnName(
				objectEntryTable.modifiedDate.getName()
			).dbTableName(
				dbTableName
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(
					LanguageUtil.get(LocaleUtil.getDefault(), "modified-date"))
			).name(
				"modifiedDate"
			).build(),
			iterator.next());

		Assert.assertTrue(iterator.hasNext());

		if (!objectDefinition.isUnmodifiableSystemObject()) {
			_assertSystemObjectFields(
				new DateTimeObjectFieldBuilder(
				).dbColumnName(
					ObjectEntryTable.INSTANCE.reviewDate.getName()
				).dbTableName(
					dbTableName
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						LanguageUtil.get(
							LocaleUtil.getDefault(), "review-date"))
				).name(
					"reviewDate"
				).objectFieldSettings(
					Collections.singletonList(
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_TIME_STORAGE
						).value(
							ObjectFieldSettingConstants.VALUE_CONVERT_TO_UTC
						).build())
				).build(),
				iterator.next());

			Assert.assertTrue(iterator.hasNext());
		}

		_assertSystemObjectFields(
			new ObjectFieldBuilder(
			).dbColumnName(
				objectEntryTable.status.getName()
			).dbTableName(
				dbTableName
			).dbType(
				ObjectFieldConstants.DB_TYPE_INTEGER
			).businessType(
				ObjectFieldConstants.BUSINESS_TYPE_TEXT
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(
					LanguageUtil.get(LocaleUtil.getDefault(), "status"))
			).name(
				"status"
			).build(),
			iterator.next());

		Assert.assertFalse(iterator.hasNext());
	}

	private void
			_testUpdateCustomObjectDefinitionThrowsObjectFieldRelationshipTypeException(
				ObjectDefinition objectDefinition1)
		throws Exception {

		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				false,
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())));

		objectDefinition2 =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition2.getObjectDefinitionId());

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				objectDefinition1.getObjectDefinitionId(),
				objectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		try {
			objectDefinition2 =
				_objectDefinitionLocalService.updateCustomObjectDefinition(
					null, objectDefinition2.getObjectDefinitionId(), 0,
					objectRelationship.getObjectFieldId2(), 0, 0, false,
					objectDefinition2.isActive(),
					objectDefinition2.getClassName(), true, false, true, false,
					true, false, false, false, false, false, false, null,
					LocalizedMapUtil.getLocalizedMap("Able"), "Able", null,
					null, false, LocalizedMapUtil.getLocalizedMap("Ables"),
					objectDefinition2.getScope(), objectDefinition2.getStatus(),
					Collections.emptyList(), Collections.emptyList());

			Assert.fail();
		}
		catch (ObjectFieldRelationshipTypeException
					objectFieldRelationshipTypeException) {

			Assert.assertEquals(
				"Description and title object fields cannot have a " +
					"relationship type",
				objectFieldRelationshipTypeException.getMessage());
		}
		finally {

			// TODO Deleting an object definition should delete any of its
			// object relationships

			//_objectRelationshipLocalService.deleteObjectRelationship(
			//	objectRelationship);

			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition2);
		}
	}

	private ObjectDefinition _updateCustomObjectDefinition(
			String externalReferenceCode, long objectDefinitionId,
			long descriptionObjectFieldId, long titleObjectFieldId,
			boolean enableFriendlyURLCustomization,
			boolean enableObjectEntryHistory, boolean enableObjectEntrySchedule,
			boolean enableObjectEntryVersioning, String friendlyURLSeparator,
			Map<Locale, String> labelMap, String name,
			Map<Locale, String> pluralLabelMap, String scope, int status)
		throws PortalException {

		return _objectDefinitionLocalService.updateCustomObjectDefinition(
			externalReferenceCode, objectDefinitionId, 0,
			descriptionObjectFieldId, 0, titleObjectFieldId, false, false, null,
			false, false, true, enableFriendlyURLCustomization, true, false,
			false, enableObjectEntryHistory, enableObjectEntrySchedule, false,
			enableObjectEntryVersioning, friendlyURLSeparator, labelMap, name,
			null, null, false, pluralLabelMap, scope, status,
			Collections.emptyList(), Collections.emptyList());
	}

	private ObjectDefinition _updateCustomObjectDefinition(
			String className, ObjectDefinition objectDefinition)
		throws Exception {

		return _objectDefinitionLocalService.updateCustomObjectDefinition(
			objectDefinition.getExternalReferenceCode(),
			objectDefinition.getObjectDefinitionId(),
			objectDefinition.getAccountEntryRestrictedObjectFieldId(),
			objectDefinition.getDescriptionObjectFieldId(),
			objectDefinition.getObjectFolderId(),
			objectDefinition.getTitleObjectFieldId(),
			objectDefinition.isAccountEntryRestricted(),
			objectDefinition.isActive(), className,
			objectDefinition.isEnableCategorization(),
			objectDefinition.isEnableComments(),
			objectDefinition.isEnableFormContainer(),
			objectDefinition.isEnableFriendlyURLCustomization(),
			objectDefinition.isEnableIndexSearch(),
			objectDefinition.isEnableLocalization(),
			objectDefinition.isEnableObjectEntryDraft(),
			objectDefinition.isEnableObjectEntryHistory(),
			objectDefinition.isEnableObjectEntrySchedule(),
			objectDefinition.isEnableObjectEntrySubscription(),
			objectDefinition.isEnableObjectEntryVersioning(),
			objectDefinition.getFriendlyURLSeparator(),
			objectDefinition.getLabelMap(), objectDefinition.getShortName(),
			objectDefinition.getPanelAppOrder(),
			objectDefinition.getPanelCategoryKey(),
			objectDefinition.isPortlet(), objectDefinition.getPluralLabelMap(),
			objectDefinition.getScope(), objectDefinition.getStatus(),
			Collections.emptyList(), Collections.emptyList());
	}

	private static ObjectFolder _defaultObjectFolder;

	@Inject
	private static ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private KaleoDefinitionLocalService _kaleoDefinitionLocalService;

	@Inject
	private MessageBus _messageBus;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject(
		filter = "component.name=com.liferay.object.internal.model.listener.ObjectDefinitionModelListener"
	)
	private ModelListener<ObjectDefinition> _objectDefinitionModelListener;

	private ObjectDefinitionTreeFactory _objectDefinitionTreeFactory;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectEntryVersionLocalService _objectEntryVersionLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private PLOEntryLocalService _ploEntryLocalService;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourceActions _resourceActions;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private SharingEntryLocalService _sharingEntryLocalService;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowDefinitionManager _workflowDefinitionManager;

}